package org.opencb.biodata.tools.alignment.coverage;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceRecord;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.AlignmentOptions;
import org.opencb.biodata.tools.alignment.BamManager;
import org.opencb.biodata.tools.alignment.BamUtils;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public class CoverageSQLiteUtils {

    private static final String COVERAGE_SUFFIX = ".coverage";
    private static final String COVERAGE_DATABASE_NAME = "coverage.db";

    private static final int MINOR_CHUNK_SIZE = 1000;

    /**
     * Calculate the coverage for the input BAM file. The coverage is stored in a SQLite database
     * located in the sqlPath output directory.
     *
     * @param bamPath       BAM file
     * @param sqlPath       Output dir where to store the SQLite database
     * @throws IOException  IO exception
     */
    public static void calculateCoverate(Path bamPath, Path sqlPath) throws IOException {
        BamManager bamManager = new BamManager(bamPath);

        // Check if the bam index (.bai) does not exit, then create it
        if (!bamPath.getParent().resolve(bamPath.getFileName().toString() + ".bai").toFile().exists()) {
            bamManager.createIndex();
        }

        // Calculate coverage and store in SQLite
        SAMFileHeader fileHeader = BamUtils.getFileHeader(bamPath);
//        long start = System.currentTimeMillis();
        initDatabase(fileHeader.getSequenceDictionary().getSequences(), sqlPath);
//        System.out.println("SQLite database initialization, in " + ((System.currentTimeMillis() - start) / 1000.0f)
//                + " s.");

        Path coveragePath = sqlPath.toAbsolutePath().resolve(bamPath.getFileName() + COVERAGE_SUFFIX);

        AlignmentOptions options = new AlignmentOptions();
        options.setContained(false);

        Iterator<SAMSequenceRecord> iterator = fileHeader.getSequenceDictionary().getSequences().iterator();
        PrintWriter writer = new PrintWriter(coveragePath.toFile());
        StringBuilder line;
//        start = System.currentTimeMillis();
        while (iterator.hasNext()) {
            SAMSequenceRecord next = iterator.next();
            for (int i = 0; i < next.getSequenceLength(); i += MINOR_CHUNK_SIZE) {
                Region region = new Region(next.getSequenceName(), i + 1,
                        Math.min(i + MINOR_CHUNK_SIZE, next.getSequenceLength()));
                RegionCoverage regionCoverage = bamManager.coverage(region, null, options);
                int meanDepth = Math.min(regionCoverage.meanCoverage(), 255);

                // File columns: chunk   chromosome start   end coverage
                // chunk format: chrom_id_suffix, where:
                //      id: int value starting at 0
                //      suffix: chunkSize + k
                // eg. 3_4_1k

                line = new StringBuilder();
                line.append(region.getChromosome()).append("_");
                line.append(i / MINOR_CHUNK_SIZE).append("_").append(MINOR_CHUNK_SIZE / 1000).append("k");
                line.append("\t").append(region.getChromosome());
                line.append("\t").append(region.getStart());
                line.append("\t").append(region.getEnd());
                line.append("\t").append(meanDepth);
                writer.println(line.toString());
            }
        }
        writer.close();
//        System.out.println("Mean coverage file creation, in " + ((System.currentTimeMillis() - start) / 1000.0f) + " s.");

        // save file to db
//        start = System.currentTimeMillis();
        insertCoverageDB(bamPath, sqlPath);
//        System.out.println("SQLite database population, in " + ((System.currentTimeMillis() - start) / 1000.0f) + " s.");

    }

    private static void initDatabase(List<SAMSequenceRecord> sequenceRecordList, Path workspace) {
        Path coverageDBPath = workspace.toAbsolutePath().resolve(COVERAGE_DATABASE_NAME);
        if (!coverageDBPath.toFile().exists()) {

            try {
                Class.forName("org.sqlite.JDBC");
                // Create tables
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + coverageDBPath);
                     Statement stmt = connection.createStatement()) {
                    String sql = "CREATE TABLE chunk "
                            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "chunk_id VARCHAR NOT NULL,"
                            + "chromosome VARCHAR NOT NULL, "
                            + "start INT NOT NULL, "
                            + "end INT NOT NULL); "
                            + "CREATE UNIQUE INDEX chunk_id_idx ON chunk (chunk_id);"
                            + "CREATE INDEX chrom_start_end_idx ON chunk (chromosome, start, end);";
                    stmt.executeUpdate(sql);

                    sql = "CREATE TABLE file "
                            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "path VARCHAR NOT NULL,"
                            + "name VARCHAR NOT NULL);"
                            + "CREATE UNIQUE INDEX path_idx ON file (path);";
                    stmt.executeUpdate(sql);

                    sql = "CREATE TABLE mean_coverage "
                            + "(chunk_id INTEGER,"
                            + "file_id INTEGER,"
                            + "v1 INTEGER, "
                            + "v2 INTEGER, "
                            + "v3 INTEGER, "
                            + "v4 INTEGER, "
                            + "v5 INTEGER, "
                            + "v6 INTEGER, "
                            + "v7 INTEGER, "
                            + "v8 INTEGER,"
                            + "PRIMARY KEY(chunk_id, file_id));";
                    stmt.executeUpdate(sql);

                    // Insert all the chunks
                    String minorChunkSuffix = (MINOR_CHUNK_SIZE / 1000) * 64 + "k";

                    try (PreparedStatement insertChunk = connection.prepareStatement("insert into chunk (chunk_id, chromosome, start, end) "
                            + "values (?, ?, ?, ?)")) {
                        connection.setAutoCommit(false);

                        for (SAMSequenceRecord samSequenceRecord : sequenceRecordList) {
                            String chromosome = samSequenceRecord.getSequenceName();
                            int sequenceLength = samSequenceRecord.getSequenceLength();

                            int cont = 0;
                            for (int i = 0; i < sequenceLength; i += 64 * MINOR_CHUNK_SIZE) {
                                String chunkId = chromosome + "_" + cont + "_" + minorChunkSuffix;
                                insertChunk.setString(1, chunkId);
                                insertChunk.setString(2, chromosome);
                                insertChunk.setInt(3, i + 1);
                                insertChunk.setInt(4, i + 64 * MINOR_CHUNK_SIZE);
                                insertChunk.addBatch();
                                cont++;
                            }
                            insertChunk.executeBatch();
                        }
                        connection.commit();
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Opened database successfully");
        }
    }

    private static void insertCoverageDB(Path bamPath, Path workspace) throws IOException {
        FileUtils.checkFile(bamPath);
        String absoluteBamPath = bamPath.toFile().getAbsolutePath();
        Path coveragePath = workspace.toAbsolutePath().resolve(bamPath.getFileName() + COVERAGE_SUFFIX);

        String fileName = bamPath.toFile().getName();

        Path coverageDBPath = workspace.toAbsolutePath().resolve("coverage.db");
        try {
            // Insert into file table
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + coverageDBPath);
                 Statement stmt = connection.createStatement()) {
                String insertFileSql = "insert into file (path, name) values ('" + absoluteBamPath + "', '" + fileName + "');";
                stmt.executeUpdate(insertFileSql);
                stmt.close();
                int fileId = -1;

                try (ResultSet resultSet = stmt.executeQuery("SELECT id FROM file where path = '" + absoluteBamPath + "';")) {
                    while (resultSet.next()) {
                        fileId = resultSet.getInt("id");
                    }
                }

                if (fileId != -1) {
                    Map chunkIdMap = new HashMap<String, Integer>();
                    String sql = "SELECT id, chromosome, start FROM chunk";
                    try (ResultSet resultSet = stmt.executeQuery(sql)) {
                        while (resultSet.next()) {
                            chunkIdMap.put(resultSet.getString("chromosome") + "_" + resultSet.getInt("start"), resultSet.getInt("id"));
                        }
                    }
                    // Iterate file
                    PreparedStatement insertCoverage = connection.prepareStatement("insert into mean_coverage (chunk_id, "
                            + " file_id, v1, v2, v3, v4, v5, v6, v7, v8) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    connection.setAutoCommit(false);

                    // Checkstyle plugin is not happy with assignations inside while/for
                    int chunkId = -1;
                    byte[] meanCoverages = new byte[8]; // contains 8 coverages
                    long[] packedCoverages = new long[8]; // contains 8 x 8 coverages
                    int counter1 = 0; // counter for 8-byte mean coverages array
                    int counter2 = 0; // counter for 8-long packed coverages array
                    String prevChromosome = null;

                    try (BufferedReader bufferedReader = FileUtils.newBufferedReader(coveragePath)) {

                        String line = bufferedReader.readLine();
                        while (line != null) {
                            String[] fields = line.split("\t");

                            if (prevChromosome == null) {
                                prevChromosome = fields[1];
                                System.out.println("Processing chromosome " + prevChromosome + "...");
                            } else if (!prevChromosome.equals(fields[1])) {
                                // we have to write the current results into the DB
                                if (counter1 > 0 || counter2 > 0) {
                                    packedCoverages[counter2] = bytesToLong(meanCoverages);
                                    insertPackedCoverages(insertCoverage, chunkId, fileId, packedCoverages);
                                }
                                prevChromosome = fields[1];
                                System.out.println("Processing chromosome " + prevChromosome + "...");

                                // reset arrays, counters,...
                                Arrays.fill(meanCoverages, (byte) 0);
                                Arrays.fill(packedCoverages, 0);
                                counter2 = 0;
                                counter1 = 0;
                                chunkId = -1;
                            }
                            if (chunkId == -1) {
                                String key = fields[1] + "_" + fields[2];
                                if (chunkIdMap.containsKey(key)) {
                                    chunkId = (int) chunkIdMap.get(key);
                                } else {
                                    throw new SQLException("Internal error: coverage chunk " + fields[1]
                                            + ":" + fields[2] + "-, not found in database");
                                }
                            }
                            meanCoverages[counter1] = (byte) Integer.parseInt(fields[4]);
                            if (++counter1 == 8) {
                                // packed mean coverages and save into the packed coverages array
                                packedCoverages[counter2] = bytesToLong(meanCoverages);
                                if (++counter2 == 8) {
                                    // write packed coverages array to DB
                                    insertPackedCoverages(insertCoverage, chunkId, fileId, packedCoverages);

                                    // reset packed coverages array and counter2
                                    Arrays.fill(packedCoverages, 0);
                                    counter2 = 0;
                                    chunkId = -1;
                                }
                                // reset mean coverages array and counter1
                                counter1 = 0;
                                Arrays.fill(meanCoverages, (byte) 0);
                            }
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();
                    }

                    if (counter1 > 0 || counter2 > 0) {
                        packedCoverages[counter2] = bytesToLong(meanCoverages);
                        insertPackedCoverages(insertCoverage, chunkId, fileId, packedCoverages);
                    }

                    // insert batch to the DB
                    insertCoverage.executeBatch();
                }
                connection.commit();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertPackedCoverages(PreparedStatement insertCoverage, int chunkId, int fileId,
                                       long[] packedCoverages) throws SQLException {
        assert (chunkId != -1);

        insertCoverage.setInt(1, chunkId);
        insertCoverage.setInt(2, fileId);
        for (int i = 0; i < 8; i++) {
            insertCoverage.setLong(i + 3, packedCoverages[i]);
        }
        insertCoverage.addBatch();
    }

    private static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip(); // need flip
        return buffer.getLong();
    }
}
