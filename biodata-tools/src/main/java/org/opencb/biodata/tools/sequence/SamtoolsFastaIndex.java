package org.opencb.biodata.tools.sequence;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import org.apache.commons.lang3.StringUtils;
import org.opencb.commons.utils.FileUtils;
import org.rocksdb.RocksDBException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by imedina on 21/10/16.
 */
public class SamtoolsFastaIndex {

    private IndexedFastaSequenceFile indexedFastaSequenceFile;
    private String samtoolsBin;

    public SamtoolsFastaIndex() {
    }

    public SamtoolsFastaIndex(String fastaFileName) throws FileNotFoundException {
        this.indexedFastaSequenceFile = new IndexedFastaSequenceFile(new File(fastaFileName));
    }

//    public void index(Path fastaFilePath) throws IOException, RocksDBException {
//        index(fastaFilePath, Paths.get(fastaFilePath.toString() + ".fai"));
//    }

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

    public String query(String chromosome, int start, int end) throws RocksDBException {
        ReferenceSequence subsequenceAt = indexedFastaSequenceFile.getSubsequenceAt(chromosome, start, end);
        return new String(subsequenceAt.getBases());
    }

}
