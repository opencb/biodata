package org.opencb.biodata.tools.commons;

import org.opencb.biodata.models.core.Region;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jtarraga on 07/11/16.
 */
public class ChunkFrequencyManager {

    private final static int DEFAULT_CHUNK_SIZE = 1000;

    private int chunkSize;
    private Path dbPath;

    public class ChunkFrequency extends Region {
        private int windowSize;
        private short[] values;

        public ChunkFrequency(Region region, int windowSize, short[] values) {
            super(region.getChromosome(), region.getStart(), region.getEnd());
            this.windowSize = windowSize;
            this.values = values;
        }

        public int getWindowSize() { return this.windowSize; }
        public short[] getValues() { return this.values; }
    }

    public ChunkFrequencyManager(Path dbPath) throws IOException {
        this(dbPath, DEFAULT_CHUNK_SIZE);
    }

    public ChunkFrequencyManager(Path dbPath, int chunkSize) throws IOException {
        this.dbPath = dbPath;
        this.chunkSize = chunkSize;
//        FileUtils.checkFile(dbPath);
    }

    public void init(List<String> fragmentNames, List<Integer> fragmentSizes) {
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            // Create tables
            stmt = connection.createStatement();
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
            String minorChunkSuffix = (chunkSize / 1000) * 64 + "k";

            PreparedStatement insertChunk = connection.prepareStatement("insert into chunk (chunk_id, chromosome, start, end) "
                    + "values (?, ?, ?, ?)");
            connection.setAutoCommit(false);

            for (int i = 0; i < fragmentNames.size(); i++) {
                String name = fragmentNames.get(i);
                int length = fragmentSizes.get(i);

                int cont = 0;
                for (int j = 0; j < length; j += 64 * chunkSize) {
                    String chunkId = name + "_" + cont + "_" + minorChunkSuffix;
                    insertChunk.setString(1, chunkId);
                    insertChunk.setString(2, name);
                    insertChunk.setInt(3, j + 1);
                    insertChunk.setInt(4, j + 64 * chunkSize);
                    insertChunk.addBatch();
                    cont++;
                }
                insertChunk.executeBatch();
            }

            connection.commit();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Initialized database successfully");
    }

    public void load(Path countPath, Path filePath) throws IOException {
        FileUtils.checkFile(countPath);
        try {
            // Insert into file table
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement stmt = connection.createStatement();
            String insertFileSql = "insert into file (path, name) values ('" + filePath.getParent()
                    + "', '" + filePath.getFileName() + "');";
            stmt.executeUpdate(insertFileSql);
            stmt.close();

            ResultSet rs = stmt.executeQuery("SELECT id FROM file where path = '" + filePath.getParent() + "';");
            int fileId = -1;
            while (rs.next()) {
                fileId = rs.getInt("id");
            }

            if (fileId != -1) {
                Map chunkIdMap = new HashMap<String, Integer>();
                String sql = "SELECT id, chromosome, start FROM chunk";
//                        System.out.println(sql);
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    chunkIdMap.put(rs.getString("chromosome") + "_" + rs.getInt("start"), rs.getInt("id"));
                }

                // Iterate file
                PreparedStatement insertCoverage = connection.prepareStatement("insert into mean_coverage (chunk_id, "
                        + " file_id, v1, v2, v3, v4, v5, v6, v7, v8) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                connection.setAutoCommit(false);

                BufferedReader bufferedReader = FileUtils.newBufferedReader(countPath);
                // Checkstyle plugin is not happy with assignations inside while/for
                int chunkId = -1;

                byte[] meanCoverages = new byte[8]; // contains 8 coverages
                long[] packedCoverages = new long[8]; // contains 8 x 8 coverages
                int counter1 = 0; // counter for 8-byte mean coverages array
                int counter2 = 0; // counter for 8-long packed coverages array
                String prevChromosome = null;

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

                if (counter1 > 0 || counter2 > 0) {
                    packedCoverages[counter2] = bytesToLong(meanCoverages);
                    insertPackedCoverages(insertCoverage, chunkId, fileId, packedCoverages);
                }

                // insert batch to the DB
                insertCoverage.executeBatch();
            }
            connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ChunkFrequency query(Region region, Path filePath, int windowSize) {
        return query(region, filePath, windowSize, null);
    }

    public ChunkFrequency query(Region region, Path filePath, int windowSize, AggregatorFunction aggregatorFunction) {
        if (aggregatorFunction == null) {
            aggregatorFunction = mean();
        }

        windowSize = Math.max(windowSize / chunkSize * chunkSize, chunkSize);
        int size = ((region.getEnd() - region.getStart() + 1) / windowSize) + 1;
        short[] values = new short[size];

        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement stmt = connection.createStatement();

            String sql = "SELECT id FROM file where path = '" + filePath.getParent() + "';";
            ResultSet rs = stmt.executeQuery(sql);
            int fileId = -1;
            while (rs.next()) {
                fileId = rs.getInt("id");
                break;
            }

            // sanity check
            if (fileId == -1) {
                throw new SQLException("Internal error: file " + filePath + " not found in the coverage DB");
            }

            sql = "SELECT c.start, c.end, mc.v1, mc.v2, mc.v3, mc.v4, mc.v5, mc.v6, mc.v7, mc.v8"
                    + " FROM chunk c, mean_coverage mc"
                    + " WHERE c.id = mc.chunk_id AND mc.file_id = " + fileId
                    + " AND c.chromosome = '" + region.getChromosome() + "' AND c.start <= " + region.getEnd()
                    + " AND c.end > " + region.getStart() + " ORDER by c.start ASC;";
            rs = stmt.executeQuery(sql);

            int chunksPerWindow = windowSize / chunkSize;
            int chunkCounter = 0;
            int coverageAccumulator = 0;
            int arrayPos = 0;

            int start = 0;
            long packedCoverages;
            byte[] meanCoverages;

            boolean first = true;
            while (rs.next()) {
                if (first) {
                    start = rs.getInt("start");
                    first = false;
                }
                for (int i = 0; i < 8; i++) {
                    packedCoverages = rs.getInt("v" + (i + 1));
                    meanCoverages = longToBytes(packedCoverages);
                    for (int j = 0; j < 8; j++) {
                        if (start <= region.getEnd() && (start + chunkSize) >= region.getStart()) {
                            // A byte is always signed in Java,
                            // we can get its unsigned value by binary-anding it with 0xFF
                            coverageAccumulator += (meanCoverages[j] & 0xFF);
                            if (++chunkCounter >= chunksPerWindow) {
                                values[arrayPos++] = (short) aggregatorFunction.apply(coverageAccumulator, chunkCounter);
//                                        (short) Math.min(Math.round(
//                                        1.0f * coverageAccumulator / chunkCounter), 255);
                                coverageAccumulator = 0;
                                chunkCounter = 0;
                            }
                        }
                        start += chunkSize;
                    }
                }
            }
            if (chunkCounter > 0) {
                values[arrayPos++] = (short) aggregatorFunction.apply(coverageAccumulator, chunkCounter);
//                (short) Math.min(Math.round(1.0f * coverageAccumulator / chunkCounter), 255);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ChunkFrequency(region, windowSize, values);
    }

    @FunctionalInterface
    interface AggregatorFunction<A, B, R> {
        //R is like Return, but doesn't have to be last in the list nor named R.
        public R apply(A a, B b);
    }

    public AggregatorFunction mean() {
        AggregatorFunction<Integer, Integer, Short> aggregatorFunction;
        aggregatorFunction = (a, b) -> { return (short) Math.min(Math.round(1.0f * a / b), 255);};
        return aggregatorFunction;
    }

    public AggregatorFunction addition() {
        AggregatorFunction<Integer, Integer, Short> aggregatorFunction;
        aggregatorFunction = (a, b) -> { return (short) Math.min(a, 255);};
        return aggregatorFunction;
    }

    private void insertPackedCoverages(PreparedStatement insertCoverage, int chunkId, int fileId,
                                       long[] packedCoverages) throws SQLException {
        assert(chunkId != -1);

        insertCoverage.setInt(1, chunkId);
        insertCoverage.setInt(2, fileId);
        for (int i = 0; i < 8; i++) {
            insertCoverage.setLong(i + 3, packedCoverages[i]);
        }
        insertCoverage.addBatch();
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip(); // need flip
        return buffer.getLong();
    }
}
