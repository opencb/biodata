/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.commons;

import org.opencb.biodata.models.core.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private int chunkSize64k;

    private final static int DEFAULT_CHUNK_SIZE = 1000;

    // main counters and structures to save packed coverages
    int counter1 = 0; // counter for 8-byte mean coverages array
    int counter2 = 0; // counter for 8-long packed coverages array
    byte[] chunkValues = new byte[8]; // contains 8 coverages
    long[] packedChunkValues = new long[8]; // contains 8 x 8 coverages
    private Map<String, Integer> chunkIdMap = new HashMap<>();

    private Logger logger;

    /**
     * Constructor.
     *
     * @param databasePath  Full path to the file where the database is saved
     * @throws IOException
     */
    public ChunkFrequencyManager(Path databasePath) throws IOException {
        this(databasePath, DEFAULT_CHUNK_SIZE);
    }

    /**
     * Constructor.
     *
     * @param databasePath  Full path to the file where the database is saved
     * @param chunkSize     Chunk size (a value is saved for each chunk)
     */
    public ChunkFrequencyManager(Path databasePath, int chunkSize) {
        this.databasePath = databasePath;
        this.chunkSize = chunkSize;
        this.chunkSize64k = chunkSize * 64;

        logger = LoggerFactory.getLogger(this.getClass());
        init();
    }

    /**
     * Insert values for the given chromosome, a value per chunk.
     *
     * @param filePath      Full path to the file target
     * @param chromosome    Chromosome target
     * @param values        Chunk values for that chromosome
     * @throws IOException
     */
    public void insert(Path filePath, String chromosome, List<Integer> values) {

        Connection conn = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

            // get the file ID from the database, (if it does not exits, the file will be inserted)
            int fileId = getFileId(filePath, conn);
            if (fileId == -1) {
                throw new InternalError("Impossible to insert file '" + filePath
                        + "' into the database '" + databasePath + "'");
            }

            // update chromosome chunks, inserting them to the database and update the map, if necessary
            if (!chunkIdMap.containsKey(buildChunkMapKey(chromosome, 1))) {
                updateChromosomeChunks(chromosome, values.size(), conn);
            }

            // reset counters
            resetCounters();
            int chunk64k = 0;

            // prepare statement to iterate over the values
            PreparedStatement insertCoverage = conn.prepareStatement("insert into mean_coverage (chunk_id, "
                    + " file_id, v1, v2, v3, v4, v5, v6, v7, v8) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            conn.setAutoCommit(false);

            // main loop
            for (int v: values) {
                chunkValues[counter1] = (byte) Math.min(v, 255);
                if (++counter1 == 8) {
                    // packed mean coverages and save into the packed coverages array
                    packedChunkValues[counter2] = bytesToLong(chunkValues);
                    if (++counter2 == 8) {
                        // write packed coverages array to DB
                        insertPackedChunkValues(insertCoverage, getChunkId(chromosome, chunk64k),
                                fileId, packedChunkValues);

                        // update counters and reset packed coverages array and counter2
                        chunk64k++;
                        Arrays.fill(packedChunkValues, 0);
                        counter2 = 0;
                    }
                    // reset mean coverages array and counter1
                    counter1 = 0;
                    Arrays.fill(chunkValues, (byte) 0);
                }
            }

            // write to DB, if pending mean values
            if (counter1 > 0 || counter2 > 0) {
                packedChunkValues[counter2] = bytesToLong(chunkValues);
                insertPackedChunkValues(insertCoverage, getChunkId(chromosome, chunk64k), fileId, packedChunkValues);
            }
            // execute batch
            insertCoverage.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            // close resources
            conn.close();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            cleanConnectionClose(conn, null);
        } catch (ClassNotFoundException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Query values for the given region and file. Values are grouped according to the windowSize, and then a mean
     * value is computed.
     *
     * @param region        Region target
     * @param filePath      File target
     * @param windowSize    Group size of values, the mean is computed for those values
     * @return              A chunk frequency region with the mean values
     */
    public ChunkFrequency query(Region region, Path filePath, int windowSize) {
        return query(region, filePath, windowSize, mean());
    }

    /**
     * Query values for the given region and file. Values are grouped according to the windowSize, to those values
     * an aggregation function will be applied, e.g.: mean or addition.
     *
     * @param region                Region target
     * @param filePath              File target
     * @param windowSize            Group size of values
     * @param aggregatorFunction    The aggregation function to apply to the group of values
     * @return                      A chunk frequency region with the mean values
     */
    public ChunkFrequency query(Region region, Path filePath, int windowSize, BiFunction aggregatorFunction) {
        if (aggregatorFunction == null) {
            aggregatorFunction = mean();
        }

        windowSize = Math.max(windowSize / chunkSize * chunkSize, chunkSize);
        int size = ((region.getEnd() - region.getStart() + 1) / windowSize);
        if ((region.getEnd() - region.getStart() + 1) % windowSize > 0) {
            size++;
        }
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

    /**
     * Aggregation function to compute the mean for the chunk values.
     *
     * @return
     */
    public BiFunction<Integer, Integer, Short> mean() {
        return (a, b) -> (short) Math.min(Math.round(1.0f * a / b), 255);
    }

    /**
     * Aggregation function to compute the addition for the chunk values.
     *
     * @return
     */
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

        /**
         * Constructor.
         *
         * @param region        Region target
         * @param windowSize    Number of contiguous elements, all of them will have the same value
         * @param values        Array of values
         */
        public ChunkFrequency(Region region, int windowSize, short[] values) {
            super(region.getChromosome(), region.getStart(), region.getEnd());
            this.windowSize = windowSize;
            this.values = values;
        }

        public int getWindowSize() { return this.windowSize; }
        public short[] getValues() { return this.values; }
    }


    /**
     * Return a string with useful information.
     *
     * @return  String
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChunkFrequencyManager{");
        sb.append("databasePath=").append(databasePath);
        sb.append(", chunkSize=").append(chunkSize);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Get the database path.
     *
     * @return  Database path
     */
    public Path getDatabasePath() {
        return databasePath;
    }

    /**
     * Get the chunk size used in the database.
     *
     * @return  Chunk size
     */
    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * P R I V A T E   M E T H O D S
     */

    /**
     * Initialize database creating tables.
     */
    private void init() {
        if (databasePath.toFile().exists()) {
            // we have to initialize chunk size and the map of chunks
            // and return
            initChunkMap();
            int chunkSize = readChunkSize();
            assert(this.chunkSize == chunkSize);
            if (chunkSize == -1) {
                throw new InternalError("Impossible to read chunk size from the database '" + databasePath + "'");
            }
            this.chunkSize = chunkSize;
            logger.debug("Database was initialized previously. Nothing to do.");
            return;
        }

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
                    + "start INTEGER NOT NULL, "
                    + "end INTEGER NOT NULL); "
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

            // close both resources
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            cleanConnectionClose(connection, stmt);
        } catch (ClassNotFoundException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        logger.debug("Initialized database successfully");
    }

    /**
     * Initialize the map of chunks.
     */
    private void initChunkMap() {
        Statement stmt = null;
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

            // retrieve chunk IDs from database
            String sql = "SELECT id, chromosome, start FROM chunk";
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            chunkIdMap = new HashMap<>();
            while (rs.next()) {
                chunkIdMap.put(rs.getString("chromosome") + "_" + rs.getInt("start"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            cleanConnectionClose(connection, stmt);
        } catch (ClassNotFoundException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insert all the chunks for the given chromosome and update the internal map of chunks, if necessary.
     *
     * @param chromosome    Chromosome target
     * @param numChunks     Number of chunks for that chromosome
     * @param conn          Database connection
     * Database connection
     */
    private void updateChromosomeChunks(String chromosome, int numChunks, Connection conn) throws SQLException {
        // insert all the chunks for that chromosome
        String minorChunkSuffix = (chunkSize / 1000) * 64 + "k";

        String sql = "insert into chunk (chunk_id, chromosome, start, end) values (?, ?, ?, ?)";
        PreparedStatement insertChunk = conn.prepareStatement(sql);
        conn.setAutoCommit(false);
        for (int i = 0, j = 1; i < numChunks; i++, j += chunkSize64k) {
            String chunkId = chromosome + "_" + i + "_" + minorChunkSuffix;
//
//            // check if this chunk is in the dabasete
//            sql = "SELECT id FROM chunk where chunk_id = '" + chunkId + "'";
//            ResultSet rs = stmt.executeQuery(sql);
//            if (!rs.next()) {
            // if this chunk is not in the database, then insert it
            insertChunk.setString(1, chunkId);
            insertChunk.setString(2, chromosome);
            insertChunk.setInt(3, j);
            insertChunk.setInt(4, j + chunkSize64k - 1);
            insertChunk.addBatch();
//            }
        }
        insertChunk.executeBatch();
        conn.commit();
        conn.setAutoCommit(true);

        initChunkMap();
    }

    /**
     * Read chunk size from the database.
     *
     * @return  Chunk size
     */
    private int readChunkSize() {
        // read chunk size from database
        int chunkSize = -1;
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT value FROM info where name = 'chunkSize';");
            while (rs.next()) {
                // the first is read
                chunkSize = Integer.parseInt(rs.getString("value"));
                break;
            }
            conn.close();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            cleanConnectionClose(conn, null);
        } catch (ClassNotFoundException e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return chunkSize;
    }

    /**
     * Close connection and sql statement in a clean way.
     *
     * @param connection    Connection to close.
     * @param stmt          Statement to close.
     */
    private void cleanConnectionClose(Connection connection, Statement stmt) {
        try {
            // clean close
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
    }

    /**
     * Read file ID from the database. If it does not exits, then insert it to the database and return the file ID.
     *
     * @param filePath  File to insert
     * @param conn      Database connection
     * @return          File ID in the database
     */
    private int getFileId(Path filePath, Connection conn) throws SQLException {
        int fileId = readFileId(filePath, conn);
        if (fileId == -1) {
            Statement stmt = conn.createStatement();
            String insertFileSql = "insert into file (path, name) values ('" + filePath.getParent()
                    + "', '" + filePath.getFileName() + "');";
            stmt.executeUpdate(insertFileSql);
            stmt.close();

            fileId = readFileId(filePath, conn);
            if (fileId == -1) {
                throw new InternalError("Impossible to read the ID for the file " + filePath + " in database "
                        + databasePath);
            }

            stmt.close();
        }

        return fileId;
    }

    /**
     * Read file ID from the database.
     *
     * @param filePath  Path to the file
     * @param conn      Database connection
     * @return          File ID in the database
     */
    private int readFileId(Path filePath, Connection conn) {
        int fileId = -1;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM file where path = '" + filePath.getParent() + "';");
            while (rs.next()) {
                fileId = rs.getInt("id");
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fileId;
    }

    /**
     * Reset the counter used when inserting chunk values into the database.
     */
    private void resetCounters() {
        Arrays.fill(chunkValues, (byte) 0);
        Arrays.fill(packedChunkValues, 0);
        counter2 = 0;
        counter1 = 0;
    }

    /**
     * Insert packed values into the database using a prepared statement batch for efficiency.
     *
     * @param insertPStatement  Prepared statement
     * @param chunkId           ID of the chunk target
     * @param fileId            ID of the file target
     * @param packedValues      An array of 8-long values, each long value contain 8-byte chunk values
     * @throws SQLException
     */
    private void insertPackedChunkValues(PreparedStatement insertPStatement, int chunkId,
                                         int fileId, long[] packedValues)
            throws SQLException {
        assert(chunkId != -1);

        insertPStatement.setInt(1, chunkId);
        insertPStatement.setInt(2, fileId);
        for (int i = 0; i < 8; i++) {
            insertPStatement.setLong(i + 3, packedValues[i]);
        }
        insertPStatement.addBatch();
    }

    /**
     * Create the key for the map of chunks from the chromosome and start position.
     *
     * @param chromosome    Chromosome target
     * @param start         Start position target
     * @return              Key
     */
    private String buildChunkMapKey(String chromosome, int start) {
        return chromosome + "_" + start;
    }

    /**
     * Get the chunk ID from the map of chunks.
     *
     * @param chromosome    Chromosome of the chunk target
     * @param chunk64k         Number of 64k chunk
     * @return              Chunk ID in the database
     */
    private int getChunkId(String chromosome, int chunk64k) {
        int chunkId, pos = chunk64k * chunkSize64k + 1;
        String key = buildChunkMapKey(chromosome, pos);
        if (chunkIdMap.containsKey(key)) {
            chunkId = chunkIdMap.get(key);
        } else {
            throw new InternalError("Coverage chunk " + chromosome
                    + ":" + pos + "-, not found in database (64-chunk : " + chunk64k + ")");
        }
        return chunkId;
    }

    /**
     * Unpack function: from a long value to array of bytes
     *
     * @param x Long value
     * @return  Array of bytes
     */
    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    /**
     * Pack function: from array of bytes to a long value
     *
     * @param bytes Array of bytes
     * @return      Long value
     */
    private long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip(); // need flip
        return buffer.getLong();
    }
}
