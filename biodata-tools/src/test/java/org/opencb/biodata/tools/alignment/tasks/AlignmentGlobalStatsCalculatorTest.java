package org.opencb.biodata.tools.alignment.tasks;

import htsjdk.samtools.SAMRecord;
import org.ga4gh.models.ReadAlignment;
import org.junit.Test;
import org.opencb.biodata.tools.alignment.BamManager;
import org.opencb.biodata.tools.alignment.AlignmentOptions;
import org.opencb.biodata.tools.alignment.iterators.BamIterator;
import org.opencb.biodata.tools.alignment.stats.AlignmentGlobalStats;
import org.opencb.biodata.tools.alignment.stats.AvroAlignmentGlobalStatsCalculator;
import org.opencb.biodata.tools.alignment.stats.SamRecordAlignmentGlobalStatsCalculator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by pfurio on 28/10/16.
 */
public class AlignmentGlobalStatsCalculatorTest {

    @Test
    public void calculateStatsAvroVSSamRecord() throws Exception {
        // SAM stats calculator
        SamRecordAlignmentGlobalStatsCalculator samCalculator = new SamRecordAlignmentGlobalStatsCalculator();

        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        BamManager BamManager = new BamManager(inputPath);

        AlignmentGlobalStats samAlignmentStats = new AlignmentGlobalStats();

        try(BamIterator<SAMRecord> iterator = BamManager.iterator()) {
            while (iterator.hasNext()) {
                AlignmentGlobalStats computed = samCalculator.compute(iterator.next());
                samCalculator.update(computed, samAlignmentStats);
            }
        }

        AlignmentOptions alignmentOptions = new AlignmentOptions().setBinQualities(false);
        // Avro stats calculator
        AvroAlignmentGlobalStatsCalculator avroCalculator = new AvroAlignmentGlobalStatsCalculator();
        AlignmentGlobalStats avroAlignmentStats = new AlignmentGlobalStats();
        try(BamIterator<ReadAlignment> iterator1 = BamManager.iterator(null, alignmentOptions, ReadAlignment.class)) {
            while (iterator1.hasNext()) {
                AlignmentGlobalStats computed = avroCalculator.compute(iterator1.next());
                avroCalculator.update(computed, avroAlignmentStats);
            }
        }

        assertEquals(avroAlignmentStats.accInsert, samAlignmentStats.accInsert);
        assertEquals(avroAlignmentStats.accMappingQuality, samAlignmentStats.accMappingQuality);
        assertEquals(avroAlignmentStats.NM, samAlignmentStats.NM);
        assertEquals(avroAlignmentStats.numDel, samAlignmentStats.numDel);
        assertEquals(avroAlignmentStats.numHardC, samAlignmentStats.numHardC);
        assertEquals(avroAlignmentStats.numIn, samAlignmentStats.numIn);
        assertEquals(avroAlignmentStats.numMapped, samAlignmentStats.numMapped);
        assertEquals(avroAlignmentStats.numMappedFirst, samAlignmentStats.numMappedFirst);
        assertEquals(avroAlignmentStats.numMappedSecond, samAlignmentStats.numMappedSecond);
        assertEquals(avroAlignmentStats.numPad, samAlignmentStats.numPad);
        assertEquals(avroAlignmentStats.numPaired, samAlignmentStats.numPaired);
        assertEquals(avroAlignmentStats.numSkip, samAlignmentStats.numSkip);
        assertEquals(avroAlignmentStats.numSoftC, samAlignmentStats.numSoftC);
        assertEquals(avroAlignmentStats.numUnmapped, samAlignmentStats.numUnmapped);

        Set<Integer> avroInsert = avroAlignmentStats.insertMap.keySet();
        Set<Integer> samInsert = samAlignmentStats.insertMap.keySet();
        assertEquals(avroInsert.size(), samInsert.size());
        for (Integer integer : avroInsert) {
            assertEquals(avroAlignmentStats.insertMap.get(integer), samAlignmentStats.insertMap.get(integer));
        }

        Set<Integer> avroQuality = avroAlignmentStats.mappingQualityMap.keySet();
        Set<Integer> samQuality = samAlignmentStats.mappingQualityMap.keySet();
        assertEquals(avroQuality.size(), samQuality.size());
        for (Integer integer : avroQuality) {
            assertEquals(avroAlignmentStats.mappingQualityMap.get(integer), samAlignmentStats.mappingQualityMap.get(integer));
        }

        assertEquals(avroAlignmentStats.toJSON(), samAlignmentStats.toJSON());
        System.out.println("AVRO: " + avroAlignmentStats.toJSON());
        System.out.println("Sam: " + samAlignmentStats.toJSON());

    }

}