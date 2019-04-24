package org.opencb.biodata.tools.variant.stats.writer;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantTestUtils;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.tools.variant.stats.VariantStatsCalculator;

import java.util.Arrays;

/**
 * Created on 17/04/19.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsTsvExporterTest {

    @Test
    public void testExport() {
        VariantStatsTsvExporter exporter = new VariantStatsTsvExporter(System.out, VariantTestUtils.STUDY_ID,
                Arrays.asList("ALL", "C1", "C2"));


        exporter.open();
        exporter.pre();

        exporter.write(getVariant("1:1000:A:C", "0/0", "0/1", "1/1"));
        exporter.write(getVariant("1:1001:A:C", "1/1", "0/1", "1/1"));
        exporter.write(getVariant("1:1002:A:C", "0/0", "0|1", "1|1"));
        exporter.write(getVariant("1:1003:A:C", "0/0", "./.", "1/1"));
        exporter.write(getVariant("1:1004:A:C", "0/0", "./.", "./."));

        exporter.post();
        exporter.close();

    }

    private Variant getVariant(String s, String gt1, String gt2, String gt3) {
        Variant variant = VariantTestUtils.generateVariant(s, "s1", gt1, "s2", gt2, "s3", gt3);
        StudyEntry study = variant.getStudy("");
        study.setStats("ALL", VariantStatsCalculator.calculate(variant, study));
        study.setStats("C1", VariantStatsCalculator.calculate(variant, study, Arrays.asList("s1", "s2")));
        study.setStats("C2", VariantStatsCalculator.calculate(variant, study, Arrays.asList("s2", "s3")));

        VariantAnnotation annotation = new VariantAnnotation();
        annotation.setId("rs" + RandomUtils.nextInt());
        annotation.setConsequenceTypes(Arrays.asList(ct("G1"), ct("G1"), ct("G2")));
        variant.setAnnotation(annotation);
//        System.out.println("variant.toJson() = " + variant.toJson());
        return variant;
    }

    private ConsequenceType ct(String gene) {
        ConsequenceType consequenceType = new ConsequenceType();
        consequenceType.setGeneName(gene);
        return consequenceType;
    }
}