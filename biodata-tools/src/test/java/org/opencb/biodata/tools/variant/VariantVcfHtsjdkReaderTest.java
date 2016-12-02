package org.opencb.biodata.tools.variant;

import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantStudy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                "##INFO=<ID=AC,Number=1,Type=Integer,Description=\"Alele count in genotypes\">\n" +
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

    /**
     * Illumina produces variant calls, which are invalid for htsjdk. Make sure these are logged.
     * @throws Exception
     */
    @Test
    public void readInvalidFormat() throws Exception {
        String malformatedLine = "1\t1000000\t.\tTTTCCA\tTTTCCA\t100\tPASS\tAC=1\tGT\t0/1";
        String vcf = "##fileformat=VCFv4.1\n"
                + "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\ts0\n"
                + malformatedLine;
        VariantSource source = new VariantSource("test.vcf", "2", "1", "myStudy", VariantStudy.StudyType.CASE_CONTROL,
                VariantSource.Aggregation.NONE);
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(new ByteArrayInputStream(vcf.getBytes()), source);
        final List<String> malformated = new ArrayList<>();
        reader.registerMalformatedVcfHandler((a,b) -> malformated.add(a));
        reader.open();
        reader.pre();
        List<Variant> read = null;
        do{
            read = reader.read();
            System.out.println(read);
        } while(!read.isEmpty());
        reader.post();
        reader.close();

        assertEquals(1, malformated.size());
        assertEquals(malformatedLine, malformated.get(0));
    }
}