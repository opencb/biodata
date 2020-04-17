package org.opencb.biodata.tools.variant.stats;

import htsjdk.variant.vcf.VCFHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.models.clinical.pedigree.Member;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.metadata.SampleVariantStats;
import org.opencb.biodata.models.variant.metadata.VariantMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.VariantVcfHtsjdkReader;
import org.opencb.biodata.tools.variant.algorithm.IdentityByStateClusteringTest;
import org.opencb.biodata.tools.variant.metadata.VariantMetadataManager;

import java.util.*;

public class SampleVariantStatsCalculatorTest {

    private List<Variant> variants;
    private Pedigree pedigree;
    private List<String> samples;

    @Before
    public void setUp() throws Exception {
        samples = Arrays.asList(                                "s0",  "s1",  "s2",  "s3",  "s4",  "s5");
        variants = Arrays.asList(
                variant("1:100:A:C",         123.0, "PASS",    "1/1", "1|1", "0/0", "0/0", "0/0", "0/0", "protein_coding", "missense_variant"),
                variant("1:200:T:C",         193.0, "PASS",    "0/1", "1|0", "1/1", "./.", "0/1", "0/0", "protein_coding", "missense_variant"),
                variant("2:300:ATC:-",        14.0, "LowQual", "1/0", "1|1", "1/1", "0/1", "0/1", "1|0", "protein_coding", "intergenic"),
                variant("2:400:AAAAAAATC:-",  84.0, "LowQual", "1/0", "1|0", "0/1", "./.", "0/1", "1|0", "protein_coding", "stop_lost")
        );

        pedigree = new Pedigree();
        Member s0 = new Member("s0", "s0", Member.Sex.MALE);
        Member s1 = new Member("s1", "s1", Member.Sex.FEMALE);
        Member s2 = new Member("s2", "s2", Member.Sex.MALE).setFather(null).setMother(s1); // Only mother
        Member s3 = new Member("s3", "s3", Member.Sex.FEMALE).setFather(s0).setMother(null); // Only father
        Member s4 = new Member("s4", "s4", Member.Sex.MALE).setFather(s0).setMother(s1);
        Member s5 = new Member("s5", "s5", Member.Sex.MALE).setFather(s0).setMother(s1);
        pedigree.setMembers(Arrays.asList(s0, s1, s2, s3, s4, s5));
    }

    @Test
    public void test() {
        VariantFileMetadata fileMetadata = new VariantFileMetadata("file", "file");
        VariantStudyMetadata metadata = fileMetadata.toVariantStudyMetadata("study");

        // Load metadata from file
        VariantReader variantReader = new VariantVcfHtsjdkReader(IdentityByStateClusteringTest.class.getClassLoader().getResourceAsStream("ibs.vcf"), metadata);
//        List<Variant> variants = variantReader.stream().limit(50).collect(Collectors.toList());
        variantReader.open();
        variantReader.pre();
        variantReader.post();
        variantReader.close();

        VariantMetadataManager mm = new VariantMetadataManager(metadata);
        mm.loadPedigree(pedigree, "study");

        SampleVariantStatsCalculator calculator = new SampleVariantStatsCalculator(metadata);

        List<SampleVariantStats> sampleStats = calculator.compute(variants);

        for (SampleVariantStats stats: sampleStats) {
            System.out.println(stats.toString());
        }

        checkStats(sampleStats);
    }

    @Test
    public void testFromPedigree() {
        SampleVariantStatsCalculator calculator = new SampleVariantStatsCalculator(pedigree, samples);

        List<SampleVariantStats> sampleStats = calculator.compute(variants);

        checkStats(sampleStats);
    }

    @Test
    public void testFromMetadata() {
        VariantMetadataManager metadataManager = new VariantMetadataManager();
        metadataManager.addVariantStudyMetadata("study");
        metadataManager.addFile("file", new VCFHeader(new HashSet<>(), samples), "study");
        VariantMetadata metadata = metadataManager.loadPedigree(pedigree, "study");
        SampleVariantStatsCalculator calculator = new SampleVariantStatsCalculator(metadata.getStudies().get(0));

        List<SampleVariantStats> sampleStats = calculator.compute(variants);

        checkStats(sampleStats);
    }

    private void checkStats(List<SampleVariantStats> sampleStats) {
        Assert.assertTrue(sampleStats.get(0).getMendelianErrorCount().isEmpty());
        Assert.assertTrue(sampleStats.get(1).getMendelianErrorCount().isEmpty());
        Assert.assertFalse(sampleStats.get(2).getMendelianErrorCount().isEmpty());
        Assert.assertFalse(sampleStats.get(3).getMendelianErrorCount().isEmpty());
        Assert.assertFalse(sampleStats.get(4).getMendelianErrorCount().isEmpty());
        Assert.assertFalse(sampleStats.get(5).getMendelianErrorCount().isEmpty());

        for (SampleVariantStats sampleStat : sampleStats) {
            Assert.assertFalse(Float.isNaN(sampleStat.getQualityAvg()));
            Assert.assertFalse(Float.isInfinite(sampleStat.getQualityAvg()));
        }
    }

    private Variant variant(String v, double qual, String filter,
                                   String s0Gt,
                                   String s1Gt,
                                   String s2Gt,
                                   String s3Gt,
                                   String s4Gt,
                                   String s5Gt, String biotype, String so) {
        Variant variant = Variant.newBuilder(v)
                .setStudyId("study")
                .setFileId("file")
                .setSampleDataKeys("GT")
                .setSampleNames(samples)
                .addSample("s0", s0Gt)
                .addSample("s1", s1Gt)
                .addSample("s2", s2Gt)
                .addSample("s3", s3Gt)
                .addSample("s4", s4Gt)
                .addSample("s5", s5Gt)
                .setQuality(qual)
                .setFilter(filter)
                .build();
        VariantAnnotation annotation = new VariantAnnotation();
        annotation.setConsequenceTypes(new ArrayList<>());
        ConsequenceType ct = new ConsequenceType();
        ct.setBiotype(biotype);
        ct.setSequenceOntologyTerms(Collections.singletonList(new SequenceOntologyTerm(so, so)));

        annotation.getConsequenceTypes().add(ct);
        variant.setAnnotation(annotation);
        return variant;
    }

}