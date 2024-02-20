package org.opencb.biodata.models.clinical.interpretation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.clinical.ClinicalAcmg;
import org.opencb.biodata.models.clinical.ClinicalProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VariantClassificationTest {

    @Test
    public void clinicalSignificanceTest() {
        List<ClinicalAcmg> acmgs;
        ClinicalProperty.ClinicalSignificance clinicalSignificance;


        // PATHOGENIC_VARIANT

        acmgs = Arrays.asList(
                new ClinicalAcmg("PVS1", "", "", "", ""),
                new ClinicalAcmg("PS1", "", "", "", ""),
                new ClinicalAcmg("PS2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PVS1", "", "", "", ""),
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PM2", "", "", "", ""),
                new ClinicalAcmg("PM3", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PVS1", "", "", "", ""),
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PP2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PVS1", "", "", "", ""),
                new ClinicalAcmg("PP1", "", "", "", ""),
                new ClinicalAcmg("PP2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PS1", "", "", "", ""),
                new ClinicalAcmg("PS2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PS1", "", "", "", ""),
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PM2", "", "", "", ""),
                new ClinicalAcmg("PM3", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PS1", "", "", "", ""),
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PM2", "", "", "", ""),
                new ClinicalAcmg("PP1", "", "", "", ""),
                new ClinicalAcmg("PP2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PS1", "", "", "", ""),
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PP1", "", "", "", ""),
                new ClinicalAcmg("PP2", "", "", "", ""),
                new ClinicalAcmg("PP3", "", "", "", ""),
                new ClinicalAcmg("PP4", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.PATHOGENIC);

        // LIKELY_PATHOGENIC_VARIANT

        acmgs = Arrays.asList(
                new ClinicalAcmg("PVS1", "", "", "", ""),
                new ClinicalAcmg("PM2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PS1", "", "", "", ""),
                new ClinicalAcmg("PM1", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PS1", "", "", "", ""),
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PM2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PM2", "", "", "", ""),
                new ClinicalAcmg("PM3", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PM2", "", "", "", ""),
                new ClinicalAcmg("PP1", "", "", "", ""),
                new ClinicalAcmg("PP2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_PATHOGENIC);

        acmgs = Arrays.asList(
                new ClinicalAcmg("PM1", "", "", "", ""),
                new ClinicalAcmg("PP1", "", "", "", ""),
                new ClinicalAcmg("PP2", "", "", "", ""),
                new ClinicalAcmg("PP3", "", "", "", ""),
                new ClinicalAcmg("PP4", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_PATHOGENIC);

        // BENIGN_VARIANT

        acmgs = Collections.singletonList(new ClinicalAcmg("BA1", "", "", "", ""));
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.BENIGN);

        acmgs = Arrays.asList(
                new ClinicalAcmg("BS1", "", "", "", ""),
                new ClinicalAcmg("BS2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.BENIGN);

        // LIKELY_BENIGN_VARIANT

        acmgs = Arrays.asList(
                new ClinicalAcmg("BS1", "", "", "", ""),
                new ClinicalAcmg("BP2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_BENIGN);

        acmgs = Arrays.asList(
                new ClinicalAcmg("BP1", "", "", "", ""),
                new ClinicalAcmg("BP2", "", "", "", "")
        );
        clinicalSignificance = VariantClassification.computeClinicalSignificance(acmgs);
        System.out.println(StringUtils.join(acmgs, ",") + " -> " + clinicalSignificance);
        assertEquals(clinicalSignificance, ClinicalProperty.ClinicalSignificance.LIKELY_BENIGN);
    }

}