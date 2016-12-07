package org.opencb.biodata.tools.commons;

import org.opencb.biodata.formats.wig.WigUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by jtarraga on 07/11/16.
 */
public class ChunkFrequencyManager {

    private Path databasePath;
    private int chunkSize;
    private int chunkSize64;

    private final static int DEFAULT_CHUNK_SIZE = 1000;

    // main counters and structures to save packed coverages
    int counter1 = 0; // counter for 8-byte mean coverages array
    int counter2 = 0; // counter for 8-long packed coverages array
    byte[] meanCoverages = new byte[8]; // contains 8 coverages
    long[] packedCoverages = new long[8]; // contains 8 x 8 coverages
    private Map chunkIdMap;

    private Logger logger;

    public ChunkFrequencyManager(Path databasePath) throws IOException {
        this(databasePath, DEFAULT_CHUNK_SIZE);
    }

    public ChunkFrequencyManager(Path databasePath, int chunkSize) {
        this.databasePath = databasePath;
        this.chunkSize = chunkSize;
        this.chunkSize64 = chunkSize * 64;

        logger = LoggerFactory.getLogger(this.getClass());
    }

    public void init(List<String> chromosomeNames, List<Integer> chromosomeSizes) {
        Statement stmt = null;
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

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

            sql = "CREATE TABLE info "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name VARCHAR NOT NULL,"
                    + "value VARCHAR NOT NULL);"
                    + "CREATE UNIQUE INDEX name_idx ON info (name);";
            stmt.executeUpdate(sql);

            sql = "insert into info (name, value) values ('chunkSize', '" + chunkSize + "');";
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
            for (int i = 0; i < chromosomeNames.size(); i++) {
                String name = chromosomeNames.get(i);
                int length = chromosomeSizes.get(i);

                int cont = 0;
                for (int j = 0; j < length; j += chunkSize64) {
                    String chunkId = name + "_" + cont + "_" + minorChunkSuffix;
                    insertChunk.setString(1, chunkId);
                    insertChunk.setString(2, name);
                    insertChunk.setInt(3, j + 1);
                    insertChunk.setInt(4, j + chunkSize64);
                    insertChunk.addBatch();
                    cont++;
                }
                insertChunk.executeBatch();
            }
            connection.commit();
            connection.setAutoCommit(true);

            // retrieve chunk IDs from database
            chunkIdMap = new HashMap<String, Integer>();
            sql = "SELECT id, chromosome, start FROM chunk";
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                chunkIdMap.put(rs.getString("chromosome") + "_" + rs.getInt("start"), rs.getInt("id"));
            }

            // close both resources
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
//            System.exit(0);
        } catch (ClassNotFoundException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        logger.debug("Initialized database successfully");
    }

    public void insert(String chromosome, List<Integer> values, int fileId) throws IOException {
        try {
            // Insert into file table
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

            // get chunk size from database
            chunkSize = readChunkSize(conn);
            if (chunkSize == -1) {
                throw new InternalError("Coverage DB does not contain information about chunk size");
            }

            // prepare statement to iterate over the file
            PreparedStatement insertCoverage = conn.prepareStatement("insert into mean_coverage (chunk_id, "
                    + " file_id, v1, v2, v3, v4, v5, v6, v7, v8) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            conn.setAutoCommit(false);

            // reset counters
            resetCounters();
            int position = 1;

            for (int v: values) {
                meanCoverages[counter1] = (byte) Math.min(v, 255);
                if (++counter1 == 8) {
                    // packed mean coverages and save into the packed coverages array
                    packedCoverages[counter2] = bytesToLong(meanCoverages);
                    if (++counter2 == 8) {
                        // write packed coverages array to DB
                        insertPackedCoverages(insertCoverage, getChunkId(chromosome, position),
                                fileId, packedCoverages);

                        // reset packed coverages array and counter2
                        Arrays.fill(packedCoverages, 0);
                        counter2 = 0;
                    }
                    // reset mean coverages array and counter1
                    counter1 = 0;
                    Arrays.fill(meanCoverages, (byte) 0);
                }
                position += chunkSize;
            }

            // write to DB, if pending mean values
            if (counter1 > 0 || counter2 > 0) {
                packedCoverages[counter2] = bytesToLong(meanCoverages);
                insertPackedCoverages(insertCoverage, getChunkId(chromosome, position), fileId, packedCoverages);
            }

            // insert batch to the DB
            insertCoverage.executeBatch();

            conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int insertFile(Path bamPath) throws Exception {
        // Insert into file table
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        int fileId = insertFile(bamPath, conn);
        conn.close();
        return fileId;
    }

    public int readChunkSize() throws Exception {
        // Insert into file table
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        int chunkSize = readChunkSize(conn);
        conn.close();
        return chunkSize;
    }

//    @Deprecated
//    public void loadWigFile(Path countPath, Path bamPath) throws IOException {
//        FileUtils.checkFile(countPath);
//
//        try {
//            // Insert into file table
//            Class.forName("org.sqlite.JDBC");
//            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
//
//            // get chunk size from database
//            chunkSize = getChunkSize(conn);
//            if (chunkSize == -1) {
//                throw new InternalError("Coverage DB does not contain information about chunk size");
//            }
//
//            // insert file info get chunk size from database
//            int fileId = insertFile(bamPath, conn);
//            if (fileId == -1) {
//                throw new InternalError("Error inserting file " + bamPath + " into the coverage database");
//            }
//
//            // retrieve chunk IDs from database
//            chunkIdMap = new HashMap<String, Integer>();
//            String sql = "SELECT id, chromosome, start FROM chunk";
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(sql);
//            while (rs.next()) {
//                chunkIdMap.put(rs.getString("chromosome") + "_" + rs.getInt("start"), rs.getInt("id"));
//            }
//
//            // prepare statement to iterate over the file
//            PreparedStatement insertCoverage = conn.prepareStatement("insert into mean_coverage (chunk_id, "
//                    + " file_id, v1, v2, v3, v4, v5, v6, v7, v8) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//            conn.setAutoCommit(false);
//
//            // reader
//            BufferedReader bufferedReader = FileUtils.newBufferedReader(countPath);
//
//            int step = 1;
//            int span = 1;
//            int position = 1;
//            String chromosome = null;
//
//            // main loop
//            String line = bufferedReader.readLine();
//            while (line != null) {
//                // check for header lines
//                if (WigUtils.isHeaderLine(line)) {
//                    System.out.println("Loading wig data:" + line);
//                    if (WigUtils.isVariableStep(line)) {
//                        throw new UnsupportedOperationException("Wig coverage file with 'variableStep'"
//                                + " is not supported yet.");
//                    }
//
//                    // update some values
//                    step = WigUtils.getStep(line);
//                    span = WigUtils.getSpan(line);
//                    position = WigUtils.getStart(line);
//                    chromosome = WigUtils.getChromosome(line);
//
//                    if (step != 1) {
//                        throw new UnsupportedOperationException("Wig coverage file with"
//                                + " 'step' != 1 is not supported yet.");
//                    }
//
//                    // next line...
//                    line = bufferedReader.readLine();
//                } else {
//                    // main body, coverage values
//                    if (span <= chunkSize) {
//                        // span is smaller than chunk size, we have to read n = chunkSize/span lines
//                        // and get a mean coverage for the chunk
//                        int n = chunkSize / span;
//                        int sum = 0;
//                        int counter = 0;
//                        while (true) {
//                            // accumulate the current value, and check if we have a complete value
//                            sum += Integer.parseInt(line);
//                            if (++counter == n) {
//                                updatePackedCoverages((byte) Math.min(sum / counter, 255), getChunkId(chromosome, position),
//                                        fileId, insertCoverage);
//                                sum = 0;
//                                counter = 0;
//                            }
//                            line = bufferedReader.readLine();
//                            if (line == null || (line != null && WigUtils.isHeaderLine(line))) {
//                                // pending coverages to save
//                                if (counter > 0) {
//                                    updatePackedCoverages((byte) Math.min(sum / counter, 255),
//                                            getChunkId(chromosome, position), fileId, insertCoverage);
//                                }
//                                if (counter1 > 0 || counter2 > 0) {
//                                    packedCoverages[counter2] = bytesToLong(meanCoverages);
//                                    insertPackedCoverages(insertCoverage, getChunkId(chromosome, position),
//                                            fileId, packedCoverages);
//                                }
//                                resetCounters();
//                                break;
//                            } else {
//                                position += span;
//                            }
//                        }
//                    } else {
//                        throw new UnsupportedOperationException("span bigger than chunkSize is not supported");
//                    }
//                }
//            }
//            bufferedReader.close();
//
//            if (counter1 > 0 || counter2 > 0) {
//                packedCoverages[counter2] = bytesToLong(meanCoverages);
//                insertPackedCoverages(insertCoverage, getChunkId(chromosome, position), fileId, packedCoverages);
//            }
//
//            // insert batch to the DB
//            insertCoverage.executeBatch();
//
//            conn.commit();
//            stmt.close();
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public ChunkFrequency query(Region region, Path filePath, int windowSize) {
        return query(region, filePath, windowSize, mean());
    }

    //    public ChunkFrequency query(Region region, Path filePath, int windowSize, AggregatorFunction aggregatorFunction) {
    public ChunkFrequency query(Region region, Path filePath, int windowSize, BiFunction aggregatorFunction) {
        if (aggregatorFunction == null) {
            aggregatorFunction = mean();
        }

        windowSize = Math.max(windowSize / chunkSize * chunkSize, chunkSize);
        int size = ((region.getEnd() - region.getStart() + 1) / windowSize) + 1;
        short[] values = new short[size];

        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
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
                throw new SQLException("File " + filePath + " not found in the coverage DB");
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
                    packedCoverages = rs.getLong("v" + (i + 1));
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

    public BiFunction<Integer, Integer, Short> mean() {
        return (a, b) -> (short) Math.min(Math.round(1.0f * a / b), 255);
    }

    public BiFunction<Integer, Integer, Short> addition() {
        return (a, b) -> (short) Math.min(a, 255);
    }

    /**
     *
     * ChunkFrequency object
     *
     */

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


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChunkFrequencyManager{");
        sb.append("databasePath=").append(databasePath);
        sb.append(", chunkSize=").append(chunkSize);
        sb.append('}');
        return sb.toString();
    }

    public Path getDatabasePath() {
        return databasePath;
    }

    public ChunkFrequencyManager setDatabasePath(Path databasePath) {
        this.databasePath = databasePath;
        return this;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public ChunkFrequencyManager setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        return this;
    }

    /**
     *
     * P R I V A T E     F U N C T I O N S
     *
     */
    private int insertFile(Path bamPath, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String insertFileSql = "insert into file (path, name) values ('" + bamPath.getParent()
                + "', '" + bamPath.getFileName() + "');";
        stmt.executeUpdate(insertFileSql);
        stmt.close();

        ResultSet rs = stmt.executeQuery("SELECT id FROM file where path = '" + bamPath.getParent() + "';");
        int fileId = -1;
        while (rs.next()) {
            fileId = rs.getInt("id");
        }
        stmt.close();

        return fileId;
    }

    private int readChunkSize(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT value FROM info where name = 'chunkSize';");
        int chunkSize = -1;
        while (rs.next()) {
            chunkSize = Integer.parseInt(rs.getString("value"));
        }
        return chunkSize;
    }

    private int readFileId(Path bamPath, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id FROM file where path = '" + bamPath.getParent() + "';");
        int fileId = -1;
        while (rs.next()) {
            fileId = rs.getInt("id");
        }
        stmt.close();
        return fileId;
    }

    private int getChunkId(String chromosome, int position) {
        int chunkId;
        String key = chromosome + "_" + (position / chunkSize64 * chunkSize64 + 1);
        if (chunkIdMap.containsKey(key)) {
            chunkId = (int) chunkIdMap.get(key);
        } else {
            throw new InternalError("Coverage chunk " + chromosome
                    + ":" + position + "-, not found in database");
        }
        return chunkId;
    }
    //
//    private void updatePackedCoverages(byte coverage, int chunkId, int fileId, PreparedStatement insertCoverage)
//            throws SQLException {
//        meanCoverages[counter1] = coverage;
//        if (++counter1 == 8) {
//            // packed mean coverages and save into the packed coverages array
//            packedCoverages[counter2] = bytesToLong(meanCoverages);
//            if (++counter2 == 8) {
//                // write packed coverages array to DB
//                insertPackedCoverages(insertCoverage, chunkId, fileId, packedCoverages);
//
//                // reset packed coverages array and counter2
//                Arrays.fill(packedCoverages, 0);
//                counter2 = 0;
//            }
//            // reset mean coverages array and counter1
//            counter1 = 0;
//            Arrays.fill(meanCoverages, (byte) 0);
//        }
//    }
//
    private void resetCounters() {
        Arrays.fill(meanCoverages, (byte) 0);
        Arrays.fill(packedCoverages, 0);
        counter2 = 0;
        counter1 = 0;
    }

    private void insertPackedCoverages(PreparedStatement insertCoverage, int chunkId, int fileId, long[] packedCoverages)
            throws SQLException {
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
