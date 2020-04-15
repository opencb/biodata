package org.opencb.biodata.tools.variant;

import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created on 16/05/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantVcfHtsjdkReaderTest {

    private static final String DP_TAG = "DP";
    private static final String AU_TAG = "AU";
    private static final String FDP_TAG = "FDP";

    @Test
    public void missingPhaseTest() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/phasemissing.vcf");
        VariantStudyMetadata metadata = new VariantFileMetadata("phasemissing.vcf", "2")
                .toVariantStudyMetadata("sid");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(inputStream, metadata).setIgnorePhaseSet(false);
        reader.open();
        reader.pre();

        // The third variant contains missing value - must not be included as part of the first batch
        List<Variant> variantList = reader.read(2);
        assertEquals(2, variantList.size());

        Variant variant = getVariant(variantList, "rs1");
        assertNotNull(variant);
        variant = getVariant(variantList, "rs2");
        assertNotNull(variant);

        variantList = reader.read(2);
        assertEquals(1, variantList.size());
        variant = getVariant(variantList, "rs3");
        assertNotNull(variant);

        reader.post();
        reader.close();
    }

    @Test
    public void breakendParsingTest() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/bnd.vcf");
        VariantStudyMetadata metadata = new VariantFileMetadata("bnd.vcf", "2")
                .toVariantStudyMetadata("sid");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(inputStream, metadata)
                .setIgnorePhaseSet(false)
                .setCombineBreakends(true);
        reader.open();
        reader.pre();

        List<Variant> variantList = reader.read(2);
        // First batch will be empty since no paired BND are found, and both of them are saved aiming at finding their
        // corresponding mates in posterior iterations
        assertEquals(0, variantList.size());

        // Second batch finds one BND without MATEID
        variantList = reader.read(2);
        assertEquals(2, variantList.size());

        // This is a BND that should have been combined from two VCF lines (1st and 3rd)
        Variant variant = getVariant(variantList, "MantaBND:16:1:2:1:0:0:1");
        assertNotNull(variant);
        assertEquals(Integer.valueOf(245657), variant.getSv().getCiStartLeft());
        assertEquals(Integer.valueOf(245697), variant.getSv().getCiStartRight());
        assertNotNull(variant.getSv().getBreakend());
        assertNotNull(variant.getSv().getBreakend().getMate());
        assertEquals("1", variant.getSv().getBreakend().getMate().getChromosome());
        assertEquals(Integer.valueOf(797179), variant.getSv().getBreakend().getMate().getPosition());
        assertEquals(Integer.valueOf(797179), variant.getSv().getBreakend().getMate().getCiPositionLeft());
        assertEquals(Integer.valueOf(797219), variant.getSv().getBreakend().getMate().getCiPositionRight());

        // This is a BND without MATEID field
        variant = getVariant(variantList, "MantaBND:16:1:2:1:0:0:2");
        assertNotNull(variant);

        // Third batch reads two non BND
        variantList = reader.read(2);
        assertEquals(2, variantList.size());

        // CNV must have been returned
        variant = getVariant(variantList, "Canvas:GAIN:1:815030:824821");
        assertNotNull(variant);

        // SNV must have been returned
        variant = getVariant(variantList, "rstest");
        assertNotNull(variant);

        // Last batch reaches end of file and drains
        variantList = reader.read(2);
        assertEquals(1, variantList.size());

        // Singleton with mate BND specified in VCF line though, must have been drained
        variant = getVariant(variantList, "MantaBND:16:1:2:0:0:0:0");
        assertNotNull(variant);




        reader.post();
        reader.close();
    }

    private Variant getVariant(List<Variant> variantList, String id) {
        for (Variant variant : variantList) {
            if (variant.getNames().contains(id)) {
                return variant;
            }
        }
        return null;
    }

    @Test
    public void batchSplitsPhaseTest() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/phase.vcf");
        VariantStudyMetadata metadata = new VariantFileMetadata("phase.vcf", "2")
                .toVariantStudyMetadata("sid");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(inputStream, metadata).setIgnorePhaseSet(false);
        reader.open();
        reader.pre();

        List<Variant> variantList = reader.read(2);
        assertEquals(3, variantList.size());
        assertEquals("99166",
                variantList.get(1).getStudies().get(0).getSampleData("S1", "PS"));
        assertEquals("99166",
                variantList.get(2).getStudies().get(0).getSampleData("S1", "PS"));

        reader.post();
        reader.close();
    }

    @Test
    public void batchSplitsPhaseIgnorePhaseTest() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/phase.vcf");
        VariantStudyMetadata metadata = new VariantFileMetadata("phase.vcf", "2")
                .toVariantStudyMetadata("sid");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(inputStream, metadata).setIgnorePhaseSet(true);
        reader.open();
        reader.pre();

        List<Variant> variantList = reader.read(2);
        assertEquals(2, variantList.size());

        Variant variant = variantList.get(1);
        assertEquals("1", variant.getChromosome());
        assertEquals(Integer.valueOf(99166), variant.getStart());
        assertEquals("C", variant.getReference());
        assertEquals("T", variant.getAlternate());
        assertEquals("99166",
                variant.getStudies().get(0).getSampleData("S1", "PS"));

        variantList = reader.read(2);
        assertEquals(1, variantList.size());
        variant = variantList.get(0);
        assertEquals("1", variant.getChromosome());
        assertEquals(Integer.valueOf(99580), variant.getStart());
        assertEquals("T", variant.getReference());
        assertEquals("C", variant.getAlternate());
        assertEquals("99166",
                variant.getStudies().get(0).getSampleData("S1", "PS"));

        reader.post();
        reader.close();
    }

    @Test
    public void readFileTest() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/ibs.vcf");
        VariantStudyMetadata metadata = new VariantFileMetadata("ibs.vcf", "2").toVariantStudyMetadata("sid");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(inputStream, metadata);
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
     */
    @Test
    public void readInvalidFormat() throws Exception {
        String malformatedLine = "1\t1000000\t.\tTTTCCA\tTTTCCA\t100\tPASS\tAC=1\tGT\t0/1";
        String vcf = "##fileformat=VCFv4.1\n"
                + "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\ts0\n"
                + malformatedLine;
        VariantStudyMetadata metadata = new VariantFileMetadata("test.vcf", "2").toVariantStudyMetadata("sid");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(new ByteArrayInputStream(vcf.getBytes()), metadata);
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

    @Test
    public void sampleDataKeepsOriginalOrderTest() throws Exception {

        InputStream inputStream = getClass().getResourceAsStream("/sample1_sample2.vcf");
        VariantStudyMetadata metadata = new VariantFileMetadata("sample1_sample2.vcf", "2")
                .toVariantStudyMetadata("sid");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(inputStream, metadata).setIgnorePhaseSet(true);
        reader.open();
        reader.pre();
        // The third variant contains missing value - must not be included as part of the first batch
        List<Variant> variantList = reader.read(2);

        reader.post();
        reader.close();

        assertEquals(1, variantList.size());
        Variant variant = variantList.get(0);
        assertEquals(1, variant.getStudies().size());
        List<String> format = variant.getStudies().get(0).getSampleDataKeys();
        List<SampleEntry> samples = variant.getStudies().get(0).getSamples();

        // Check samples data is exactly in the same order as in the VCF (not alphabetical!)
        assertEquals(2, samples.size());
        int dpPosition = format.indexOf(DP_TAG);
        assertEquals("54", samples.get(0).getData().get(dpPosition));
        assertEquals("152", samples.get(1).getData().get(dpPosition));
        int auPosition = format.indexOf(AU_TAG);
        assertEquals("0,5", samples.get(0).getData().get(auPosition));
        assertEquals("3,26", samples.get(1).getData().get(auPosition));
        int fdpPosition = format.indexOf(FDP_TAG);
        assertEquals("14", samples.get(0).getData().get(fdpPosition));
        assertEquals("42", samples.get(1).getData().get(fdpPosition));

    }
}