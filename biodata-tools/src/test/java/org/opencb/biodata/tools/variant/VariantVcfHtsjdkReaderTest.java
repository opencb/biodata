package org.opencb.biodata.tools.variant;

import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantStudy;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created on 16/05/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantVcfHtsjdkReaderTest {

    @Test
    public void readFileTest() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/ibs.vcf");
        VariantSource source = new VariantSource("ibs.vcf", "2", "1", "myStudy", VariantStudy.StudyType.FAMILY, VariantSource.Aggregation.NONE);
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(inputStream, source);
        reader.open();
        reader.pre();

        assertEquals(Arrays.asList("s0", "s1", "s2", "s3", "s4", "s5"), reader.getSampleNames());
        assertEquals("##fileformat=VCFv4.1\n" +
                "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\ts0\ts1\ts2\ts3\ts4\ts5", reader.getHeader());
        List<Variant> read;
        int i = 0;
        do {
            read = reader.read();
            for (Variant variant : read) {
                i++;
                System.out.println("variant = " + variant.toJson());
            }
        } while (!read.isEmpty());

        assertEquals(3, i);
        reader.post();
        reader.close();
    }
}