package org.opencb.biodata.formats.alignment.samtools.io;

import org.junit.Test;
import org.opencb.biodata.formats.alignment.samtools.SamtoolsFlagstats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class SamtoolsFlagstatsParserTest {

    String flagstats =
                      "75 + 25 in total (QC-passed reads + QC-failed reads)\n"
                    + "101 + 0 secondary\n"
                    + "102 + 0 supplementary\n"
                    + "103 + 0 duplicates\n"
                    + "104 + 0 mapped (99.75% : N/A)\n"
                    + "105 + 0 paired in sequencing\n"
                    + "106 + 0 read1\n"
                    + "107 + 0 read2\n"
                    + "108 + 0 properly paired (99.15% : N/A)\n"
                    + "109 + 0 with itself and mate mapped\n"
                    + "110 + 0 singletons (0.25% : N/A)\n"
                    + "111 + 0 with mate mapped to a different chr\n"
                    + "112 + 0 with mate mapped to a different chr (mapQ>=5)\n";



    @Test
    public void testParse() throws IOException {
        SamtoolsFlagstats actual = SamtoolsFlagstatsParser.parse(new InputStreamReader(new ByteArrayInputStream(flagstats.getBytes())));

        SamtoolsFlagstats expected = new SamtoolsFlagstats()
                .setTotalReads(100)
                .setTotalQcPassed(75)
                .setSecondaryAlignments(101)
                .setSupplementary(102)
                .setDuplicates(103)
                .setMapped(104)
                .setPairedInSequencing(105)
                .setRead1(106)
                .setRead2(107)
                .setProperlyPaired(108)
                .setSelfAndMateMapped(109)
                .setSingletons(110)
                .setMateMappedToDiffChr(111)
                .setDiffChrMapQ5(112);

        assertEquals(expected, actual);

    }

}