package org.opencb.biodata.tools.feature;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceRecord;
import org.junit.Test;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.AlignmentOptions;
import org.opencb.biodata.tools.alignment.BamManager;
import org.opencb.biodata.tools.alignment.BamUtils;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by imedina on 25/11/16.
 */
public class BigWigManagerTest {

    @Test
    public void query() throws Exception {
        // this reads a file from src/test/resources folder
        Path inputPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());

        BigWigManager bigWigManager = new BigWigManager(inputPath);
        float[] chr21 = bigWigManager.query(new Region("chr21", 9411190, 9411291));

        int start = 9411190;
        for (float v: chr21) {
            System.out.println((start++) + " :" + v);
        }

        assertEquals(9411291 - 9411190, chr21.length);
    }

    @Test
    public void index() throws Exception {
        // this reads a file from src/test/resources folder
        Path bamPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        Path bwPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());
        Path dbPath = Paths.get(bamPath + ".db");

//        Path bamPath = Paths.get("/home/jtarraga/data150/bam/NA12877_chr1.bam");
//        Path bwPath = Paths.get("/home/jtarraga/data150/bam/wgEncodeBroadHistoneH1hescH4k20me1StdSig.bigWig");
//        Path dbPath = Paths.get(bwPath + ".db");

        dbPath.toFile().delete();

        // initialize chunkFrequencyManager and DB
        int chunkSize = 1000;
        ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(dbPath, chunkSize);
        SAMFileHeader fileHeader = BamUtils.getFileHeader(bamPath);
        List chromNames = new ArrayList<String>();
        List chromLengths = new ArrayList<Integer>();
        for (SAMSequenceRecord samSequenceRecord: fileHeader.getSequenceDictionary().getSequences()) {
            chromNames.add(samSequenceRecord.getSequenceName());
            chromLengths.add(samSequenceRecord.getSequenceLength());
        }
        chunkFrequencyManager.init(chromNames, chromLengths);

        // now, we can index
        BigWigManager bigWigManager = new BigWigManager(bwPath);
        bigWigManager.index(bamPath, chunkFrequencyManager);

//        Region region = new Region("chr21", 10000000 - 1000, 10001000 - 1000);
//        float[] values = bigWigManager.query(region);
//        int total = 0;
//        for (float v : values) {
//            System.out.println(v);
//            total += v;
//        }
//        System.out.println("**** mean = " + (total / 1000));
//
//        ChunkFrequencyManager.ChunkFrequency res = chunkFrequencyManager.query(region, bamPath, 1000);
//        for (short i : res.getValues()) {
//            System.out.println("---> " + i);
//        }
    }

}