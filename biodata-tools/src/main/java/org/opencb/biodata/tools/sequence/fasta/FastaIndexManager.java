/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.tools.sequence.fasta;

import org.opencb.biodata.models.core.Region;
import org.opencb.commons.utils.FileUtils;
import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by imedina on 19/01/16.
 */
public class FastaIndexManager {

    private Path fastaFilePath;

    private Options options;
    private RocksDB rocksDB;

    private boolean connected = false;

    private int CHUNK_SIZE = 2000;
    private String chunkIdSuffix = CHUNK_SIZE / 1000 + "k";

    public FastaIndexManager(Path fastaFilePath) throws Exception {
        this(fastaFilePath, false);
    }

    public FastaIndexManager(Path fastaFilePath, boolean connect) throws Exception {
        this.fastaFilePath = fastaFilePath;
        if (connect) {
            connect();
        }
    }

    public void connect() throws Exception {
        try {
            if (Files.exists(fastaFilePath) && Files.isDirectory(fastaFilePath)) {
                rocksDB = RocksDB.openReadOnly(fastaFilePath.toAbsolutePath().toString());
                connected = true;
            } else {
                Path rocksdbIdxPath = Paths.get(fastaFilePath.toString() + ".rdb");
                if (Files.exists(rocksdbIdxPath) && Files.isDirectory(rocksdbIdxPath)) {
                    rocksDB = RocksDB.openReadOnly(rocksdbIdxPath.toString());
                    connected = true;
                }
            }
        } catch (RocksDBException e) {
            throw new Exception("Error while connecting: " + e.toString());
        }
    }

    public void index() throws IOException, RocksDBException {
        index(fastaFilePath);
    }

    public void index(Path fastaFilePath) throws IOException, RocksDBException {
        index(fastaFilePath, Paths.get(fastaFilePath.toString() + ".rdb"));
    }

    public void index(Path fastaFilePath, Path fastaIndexFilePath) throws IOException, RocksDBException {
        FileUtils.checkFile(fastaFilePath);
        options = new Options()
                .setCreateIfMissing(true)
                .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
                .createStatistics();
        rocksDB = RocksDB.open(options, fastaIndexFilePath.toString());

        BufferedReader bufferedReader = FileUtils.newBufferedReader(fastaFilePath);

        // Some parameters initialization
        String sequenceName;
        StringBuilder sequenceStringBuilder = new StringBuilder();

        String line = bufferedReader.readLine();
        sequenceName = line.split(" ")[0].replace(">", "");
        while ((line = bufferedReader.readLine()) != null) {
            // We accumulate the complete sequence in a StringBuilder
            if (!line.startsWith(">")) {
                sequenceStringBuilder.append(line);
            } else {
                System.out.println(line);

                // New sequence has been found and we must insert it into RocksDB.
                // Note that the first time there is no sequence. Only HAP sequences are excluded.
                if (sequenceStringBuilder.length() > 0) {
                    int chunk = 0;
                    int start = 1;
                    if (sequenceStringBuilder.length() < CHUNK_SIZE) {
                        String key = sequenceName + "_" + chunk; // + "_" + chunkIdSuffix;
                        rocksDB.put(key.getBytes(), sequenceStringBuilder.toString().getBytes());
                        // Sequence to store is larger than CHUNK_SIZE
                    } else {
                        int sequenceLength = sequenceStringBuilder.length();

                        while (start < sequenceLength) {
                            String key = sequenceName + "_" + chunk; // + "_" + chunkIdSuffix;
                            if (start == 1) {   // First chunk of the chromosome
                                rocksDB.put(key.getBytes(), sequenceStringBuilder.substring(start - 1, CHUNK_SIZE - 1).getBytes());

                                start += CHUNK_SIZE - 1;
                            } else {    // Regular chunk
                                if ((start + CHUNK_SIZE) < sequenceLength) {
                                    rocksDB.put(key.getBytes(), sequenceStringBuilder.substring(start - 1, start + CHUNK_SIZE - 1).getBytes());

                                    start += CHUNK_SIZE;
                                } else {    // Last chunk of the chromosome
                                    rocksDB.put(key.getBytes(), sequenceStringBuilder.substring(start - 1, sequenceLength).getBytes());
                                    start = sequenceLength;
                                }
                            }
                            chunk++;
                        }
                    }
                    // initialize data structures
                    sequenceName = line.split(" ")[0].replace(">", "");
                    sequenceStringBuilder.delete(0, sequenceStringBuilder.length());
                }
            }
        }
        connected = true;
        bufferedReader.close();
    }

    public String query(String chromosome, int start, int end) throws RocksDBException {
        String sequence = "";
        if (rocksDB != null && connected) {
            StringBuilder stringBuilder = new StringBuilder();
            int chunkStart = start / CHUNK_SIZE;
            int chunkEnd = end / CHUNK_SIZE;
            for (int chunk = chunkStart; chunk <= chunkEnd; chunk++) {
                String key = chromosome + "_" + chunk;
                byte[] bytes = rocksDB.get(key.getBytes());
                stringBuilder.append(new String(bytes));
            }

            int stringStartIndex = (start % CHUNK_SIZE);
            int stringEndIndex = stringStartIndex + (end - start) + 1;
            if (chunkStart > 0) {
                if (stringBuilder.toString().length() > 0 && stringBuilder.toString().length() >= stringEndIndex) {
                    sequence = stringBuilder.toString().substring(stringStartIndex, stringEndIndex);
                }
            } else {
                if (stringBuilder.toString().length() > 0 && stringBuilder.toString().length() + 1 >= stringEndIndex) {
                    sequence = stringBuilder.toString().substring(stringStartIndex - 1, stringEndIndex - 1);
                }
            }
        }
        return sequence;
    }

    public String query(Region region) {
        return "";
    }

    public void close() {
        if (rocksDB != null) {
            rocksDB.close();
        }
        if (options != null) {
            options.dispose();
        }
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
