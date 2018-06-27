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

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import htsjdk.samtools.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by imedina on 21/10/16.
 */
public class SamtoolsFastaIndex implements SequenceAdaptor {

    private ReferenceSequenceFile indexedFastaSequenceFile;
    private String samtoolsBin;

    public SamtoolsFastaIndex() {

    }

    public SamtoolsFastaIndex(String fastaFileName) throws IOException {
        if (!ReferenceSequenceFileFactory.canCreateIndexedFastaReader(Paths.get(fastaFileName))) {
            throw new IOException("Fasta file '" + fastaFileName + "' is not indexed.");
        }
        this.indexedFastaSequenceFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(Paths.get(fastaFileName));
    }

//    public void index(Path fastaFilePath) throws IOException, RocksDBException {
//        index(fastaFilePath, Paths.get(fastaFilePath.toString() + ".fai"));
//    }

    /**
     * Checks if the set FASTA file is indexed
     * @return
     */
    public Boolean hasIndex() {

        Boolean hasIndex = false;
        if (this.indexedFastaSequenceFile != null) {
            hasIndex = this.indexedFastaSequenceFile.isIndexed();
        }
        return hasIndex;
    }

    public void index(Path fastaFilePath) throws IOException {
        FileUtils.checkFile(fastaFilePath);

        String samtoolspath = "samtools";
        if (StringUtils.isNotEmpty(samtoolsBin) ) {
            samtoolspath = samtoolsBin;
        }
        System.out.println(samtoolspath + " faidx " + fastaFilePath.toFile().toString());
        Runtime.getRuntime().exec(samtoolspath + " faidx " + fastaFilePath.toFile().toString());
    }

    public void index(Path fastaFilePath, Path fastaIndexFilePath) throws IOException {
        throw new UnsupportedOperationException();
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
}
