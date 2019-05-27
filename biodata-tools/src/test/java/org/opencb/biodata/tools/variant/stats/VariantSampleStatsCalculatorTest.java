package org.opencb.biodata.tools.variant.stats;

import org.junit.Test;
import org.opencb.biodata.formats.variant.vcf4.io.VariantVcfReader;
import org.opencb.biodata.models.clinical.pedigree.Member;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.stats.VariantSampleStats;
import org.opencb.biodata.tools.variant.algorithm.IdentityByStateClusteringTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class VariantSampleStatsCalculatorTest {

    @Test
    public void test() {
        String fileName = "ibs.vcf";
        VariantFileMetadata fileMetadata = new VariantFileMetadata(fileName, "fid");
        VariantStudyMetadata metadata = fileMetadata.toVariantStudyMetadata("sid");

        VariantVcfReader variantReader = new VariantVcfReader(metadata, IdentityByStateClusteringTest.class.getClassLoader().getResource(fileName).getPath());
        variantReader.open();
        variantReader.pre();
        List<Variant> variants = variantReader.read(50);
        variantReader.post();
        variantReader.close();

        Pedigree pedigree = new Pedigree();
        List<Member> members = new ArrayList<>();
        Member s0 = new Member("s0", "s0", Member.Sex.MALE, Member.AffectionStatus.AFFECTED);
        Member s1 = new Member("s1", "s1", Member.Sex.FEMALE, Member.AffectionStatus.AFFECTED);
        Member s2 = new Member("s2", "s2", Member.Sex.MALE, Member.AffectionStatus.AFFECTED);
        Member s3 = new Member("s3", "s3", Member.Sex.FEMALE, Member.AffectionStatus.UNAFFECTED);
        Member s4 = new Member("s4", "s4", Member.Sex.MALE, Member.AffectionStatus.AFFECTED);
        Member s5 = new Member("s5", "s5", Member.Sex.MALE, Member.AffectionStatus.UNAFFECTED);
        s2.setFather(s0).setMother(s1);
        s3.setFather(s0).setMother(s1);
        s4.setFather(s0).setMother(s1);
        s5.setFather(s0).setMother(s1);
        members.add(s0);
        members.add(s1);
        members.add(s2);
        members.add(s3);
        members.add(s4);
        members.add(s5);
        pedigree.setMembers(members);


        VariantSampleStatsCalculator calculator = new VariantSampleStatsCalculator();
        List<VariantSampleStats> sampleStats = calculator.compute(variants, pedigree);

        for (VariantSampleStats stats: sampleStats) {
            System.out.println(stats.toString());
        }
    }

}