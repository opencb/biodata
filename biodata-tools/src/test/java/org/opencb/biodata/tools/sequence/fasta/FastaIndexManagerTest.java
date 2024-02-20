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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.rules.TemporaryFolder;
import org.opencb.biodata.tools.sequence.FastaIndexManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
/**
 * Created by imedina on 20/01/16.
 */
public class FastaIndexManagerTest {

    @TempDir
    public File testFolder;
    private File rocksdb;

    @BeforeEach
    public void setUp() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/homo_sapiens_grch37_small.fa.gz").toURI());
        FastaIndexManager fastaIndexManager = new FastaIndexManager(inputPath);
        rocksdb = new File(testFolder.getAbsolutePath()+"rocksdb");
        rocksdb.mkdir();
        fastaIndexManager.index(inputPath, rocksdb.toPath());
    }

    @AfterEach
    public void tearDown() throws Exception {
        testFolder.delete();
    }


    @Test
    public void testQuery() throws Exception {
//        Path inputPath = Paths.get(getClass().getResource("/homo_sapiens_grch37_small.fa.gz").toURI());
        FastaIndexManager fastaIndexManager = new FastaIndexManager(rocksdb.toPath(), true);
        String query = fastaIndexManager.query("GL000207.1", 4252, 4262);
        assertEquals("Error querying the sequence", "CTGGTGGGATC", query);

        query = fastaIndexManager.query("1", 11821, 11829);
        assertEquals("Error querying the sequence", "TTTAAACGA", query);

        query = fastaIndexManager.query("1", 1, 1999);
        assertEquals("Error querying the sequence", 1999, query.length());

        query = fastaIndexManager.query("1", 2000, 3999);
        assertEquals("Error querying the sequence", 2000, query.length());
    }
}