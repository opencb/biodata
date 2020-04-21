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

package org.opencb.biodata.tools.sequence;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.reference.*;
import htsjdk.samtools.util.GZIIndex;
import htsjdk.samtools.util.IOUtil;
import htsjdk.samtools.util.StringUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by imedina on 21/10/16.
 */
public class FastaIndex implements SequenceAdaptor {

    private ReferenceSequenceFile indexedFastaSequenceFile;

    public FastaIndex() {
    }

    /**
     * Creates an index for Fasta to query. if the fasta index (.fai) file doesn't exist, it will be created. Works on both raw fasta
     * and block compressed files. Does not work on gzipped files.
     *
     * If block compressed file, the .gzi file will also be created if not present.
     *
     * @param fastaFile fasta file
     * @throws IOException if fasta file can't be read
     */
    public FastaIndex(Path fastaFile) throws IOException {
        if (IOUtil.isBlockCompressed(fastaFile, true)) {
            initBlockCompressed(fastaFile);
        } else {
            init(fastaFile);
        }
    }

    private void init(Path fastaFile) throws IOException {
        FastaSequenceIndex fastaSequenceIndex = getFaiIndex(fastaFile, true);
        this.indexedFastaSequenceFile = new IndexedFastaSequenceFile(fastaFile, fastaSequenceIndex);
    }

    private void initBlockCompressed(Path fastaFile) throws IOException {
        FastaSequenceIndex fastaSequenceIndex = getFaiIndex(fastaFile, true);
        GZIIndex gziIndex = getGziIndex(fastaFile);
        this.indexedFastaSequenceFile = new BlockCompressedIndexedFastaSequenceFile(fastaFile, fastaSequenceIndex, gziIndex);
    }

    /**
     * Will write a .fai index file for provided fasta file. Replaces the old fasta index that used rocksdb.
     *
     * If overwrite is FALSE and there is already a file, samtools will throw an exception.
     *
     * @param fastaFile Path to the fasta file to be indexed.
     * @param overwrite if TRUE, will overwrite the .fai file if present
     * @throws IOException if fasta file can't be read or index file can't be written.
     */
    public FastaSequenceIndex getFaiIndex(Path fastaFile, boolean overwrite) throws IOException {
        Path faiIndexFilePath =  Paths.get(fastaFile.toAbsolutePath().toString() + ".fai");
        if (!Files.exists(faiIndexFilePath)) {
            FastaSequenceIndexCreator.create(fastaFile, overwrite);
            return new FastaSequenceIndex(faiIndexFilePath);
        } else {
            return new FastaSequenceIndex(faiIndexFilePath);
        }
    }

    /**
     * Will write a .gzi index file for provided fasta file if it doesn't exist. Valid for bgzipped files ONLY.
     *
     * @param fastaFile Path to the bgzipped fasta file to be indexed.
     * @throws IOException if fasta file can't be read or index file can't be written.
     */
    public GZIIndex getGziIndex(Path fastaFile) throws IOException {
        Path gziIndexPath =  Paths.get(fastaFile.toAbsolutePath().toString() + ".gzi");
        if (!Files.exists(gziIndexPath)) {
            return GZIIndex.buildIndex(fastaFile);
        } else {
            return GZIIndex.loadIndex(gziIndexPath);
        }
    }

    public String query(String chromosome, int start, int end) {
        ReferenceSequence subsequenceAt = indexedFastaSequenceFile.getSubsequenceAt(chromosome, start, end);
        return StringUtil.bytesToString(subsequenceAt.getBases());
    }

    public ReferenceSequence queryReferenceSequence(String chromosome, int start, int end) {
        ReferenceSequence referenceSequence = indexedFastaSequenceFile.getSubsequenceAt(chromosome, start, end);
        return referenceSequence;
    }

    public SAMSequenceDictionary getSequenceDictionary() {
        return indexedFastaSequenceFile.getSequenceDictionary();
    }

    public void close() throws IOException {
        indexedFastaSequenceFile.close();
    }
}
