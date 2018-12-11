package org.opencb.biodata.tools.pedigree;

import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;

public class MendelianError {

    public static final String CHROMOSOME_X = "X";
    public static final String CHROMOSOME_Y = "Y";

    public enum GenotypeCode {
        HOM_REF, HOM_VAR, HET
    }

    public static Integer compute(Genotype fatherGt, Genotype motherGt, Genotype childGt, String chromosome) {
        // The error classification is available at:
        // https://www.cog-genomics.org/plink2/basic_stats#mendel
        // HOM_VAR = 0/0, HOM_REF = 1/1, HOM_VAR = 1/0, 0/1
        //
        // Code Father    Mother    Child   Copy State  Implicated
        //
        //  1   HomVar    HomVar    Het     Auto        Father, mother, child
        //  2   HomRef    HomRef    Het     Auto        Father, mother, child
        //  3   HomRef    ~HomRef   HomVar  Auto        Father, child
        //  4   ~HomRef   HomRef    HomVar  Auto        Mother, child
        //  5   HomRef    HomRef    HomVar  Auto        Child
        //  6   HomVar    ~HomVar   HomRef  Auto        Father, child
        //  7   ~HomVar   HomVar    HomRef  Auto        Mother, child
        //  8   HomVar    HomVar    HomRef  Auto        Child
        //  9   Any       HomVar    HomRef  HemiX       Mother, child
        // 10   Any       HomRef    HomVar  HemiX       Mother, child
        // 11   HomVar    Any       HomRef  HemiY       Father, child
        // 12   HomRef    Any       HomVar  HemiY       Father, child

        final int code;

        if (fatherGt.getCode() != AllelesCode.ALLELES_MISSING
                && motherGt.getCode() != AllelesCode.ALLELES_MISSING
                && childGt.getCode() != AllelesCode.ALLELES_MISSING) {
            GenotypeCode fatherCode = getAlternateAlleleCount(fatherGt);
            GenotypeCode motherCode = getAlternateAlleleCount(motherGt);
            GenotypeCode childCode = getAlternateAlleleCount(childGt);

            String chrom = chromosome == null ? "" : chromosome.toUpperCase();
            if (chrom.equals(CHROMOSOME_X)) {
                if (motherCode == GenotypeCode.HOM_VAR && childCode == GenotypeCode.HOM_REF) {
                    code = 9;
                } else if (motherCode == GenotypeCode.HOM_REF && childCode == GenotypeCode.HOM_VAR) {
                    code = 10;
                } else {
                    code = 0;
                }
            } else if (chrom.equals(CHROMOSOME_Y)) {
                if (fatherCode == GenotypeCode.HOM_VAR && childCode == GenotypeCode.HOM_REF) {
                    code = 11;
                } else if (fatherCode == GenotypeCode.HOM_REF && childCode == GenotypeCode.HOM_VAR) {
                    code = 12;
                } else {
                    code = 0;
                }
            } else {
                if (childCode == GenotypeCode.HET) {
                    if (fatherCode == GenotypeCode.HOM_VAR && motherCode == GenotypeCode.HOM_VAR) {
                        code = 1;
                    } else if (fatherCode == GenotypeCode.HOM_REF && motherCode == GenotypeCode.HOM_REF) {
                        code = 2;
                    } else {
                        code = 0;
                    }
                } else if (childCode == GenotypeCode.HOM_VAR) {
                    if (fatherCode == GenotypeCode.HOM_REF && motherCode != GenotypeCode.HOM_REF) {
                        code = 3;
                    } else if (fatherCode != GenotypeCode.HOM_REF && motherCode == GenotypeCode.HOM_REF) {
                        code = 4;
                    } else if (fatherCode == GenotypeCode.HOM_REF && motherCode == GenotypeCode.HOM_REF) {
                        code = 5;
                    } else {
                        code = 0;
                    }
                } else if (childCode == GenotypeCode.HOM_REF) {
                    if (fatherCode == GenotypeCode.HOM_VAR && motherCode != GenotypeCode.HOM_VAR) {
                        code = 6;
                    } else if (fatherCode != GenotypeCode.HOM_VAR && motherCode == GenotypeCode.HOM_VAR) {
                        code = 7;
                    } else if (fatherCode == GenotypeCode.HOM_VAR && motherCode == GenotypeCode.HOM_VAR) {
                        code = 8;
                    } else {
                        code = 0;
                    }
                } else {
                    code = 0;
                }
            }
        } else {
            code = 0;
        }

        return code;
    }

    public static GenotypeCode getAlternateAlleleCount(Genotype gt) {
        int count = 0;
        for (int i: gt.getAllelesIdx()) {
            if (i > 0) {
                count++;
            }
        }
        switch (count) {
            case 0:
                return GenotypeCode.HOM_REF;
            case 1:
                return GenotypeCode.HET;
            default:
                return GenotypeCode.HOM_VAR;
        }
    }
}
