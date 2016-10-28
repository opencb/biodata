package org.opencb.biodata.tools.alignment.tasks;

import htsjdk.samtools.SAMRecord;
import org.ga4gh.models.ReadAlignment;
import org.junit.Test;
import org.opencb.biodata.tools.alignment.AlignmentManager;
import org.opencb.biodata.tools.alignment.AlignmentOptions;
import org.opencb.biodata.tools.alignment.iterators.AlignmentIterator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by pfurio on 28/10/16.
 */
public class AlignmentStatsCalculatorTest {

    @Test
    public void calculateStatsAvroVSSamRecord() throws Exception {
        // SAM stats calculator
        SamAlignmentStatsCalculator samCalculator = new SamAlignmentStatsCalculator();

        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        AlignmentManager alignmentManager = new AlignmentManager(inputPath);

        AlignmentStats samAlignmentStats = new AlignmentStats();

        try(AlignmentIterator<SAMRecord> iterator = alignmentManager.iterator()) {
            while (iterator.hasNext()) {
                AlignmentStats computed = samCalculator.compute(iterator.next());
                samCalculator.update(computed, samAlignmentStats);
            }
        }

        AlignmentOptions alignmentOptions = new AlignmentOptions().setBinQualities(false);
        // Avro stats calculator
        AvroAlignmentStatsCalculator avroCalculator = new AvroAlignmentStatsCalculator();
        AlignmentStats avroAlignmentStats = new AlignmentStats();
        try(AlignmentIterator<ReadAlignment> iterator1 = alignmentManager.iterator(alignmentOptions, null, ReadAlignment.class)) {
            while (iterator1.hasNext()) {
                AlignmentStats computed = avroCalculator.compute(iterator1.next());
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