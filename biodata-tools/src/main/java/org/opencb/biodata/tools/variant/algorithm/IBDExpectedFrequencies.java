package org.opencb.biodata.tools.variant.algorithm;

import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;

import java.util.List;

// Expected frequencies for IBS|IBD


public class IBDExpectedFrequencies {
    public double E00;
    public double E10;
    public double E20;
    public double E01;
    public double E11;
    public double E21;
    public double E02;
    public double E12;
    public double E22;

    private int counter;

    public IBDExpectedFrequencies() {
        E00 = 0;
        E10 = 0;
        E20 = 0;
        E01 = 0;
        E11 = 0;
        E21 = 0;
        E02 = 0;
        E12 = 0;
        E22 = 0;

        counter = 0;
    }

    public void update(Variant variant) {
        // TODO: Chromosome sex or haploid
//            if ( par::chr_sex[locus[l]->chr] ||
//            par::chr_haploid[locus[l]->chr] ) continue;

        double x = 0;
        double Na = 0; // = # alleles = 2N where N is number of individuals

        for (List<String> samplesData: variant.getStudies().get(0).getSamplesData()) {
            Genotype gt = new Genotype(samplesData.get(0)); // first element: genotype
            if (gt.getCode() == AllelesCode.ALLELES_MISSING) {
                continue;
            }
            // TODO: check multiple alternates
//                if (gt.getCode() == AllelesCode.MULTIPLE_ALTERNATES) {
//                }

            Na += 2;
            if (gt.getAllele(0) == 0) {
                x++;
            }
            if (gt.getAllele(1) == 0) {
                x++;
            }
        }

        // Sanity check
        // TODO: check Na == 0

        double y = Na - x;
        double p = x / Na;
        double q = 1 - p;

        double a00 = 2 * p * p * q * q * ((x - 1) / x * (y - 1) / y * (Na / (Na - 1)) * (Na / (Na - 2))
                * (Na / (Na - 3)));
        double a01 = 4 * p * p * p * q * ((x - 1) / x * (x - 2) / x * (Na / (Na - 1)) * (Na / (Na - 2))
                * (Na / (Na - 3)))
                + 4 * p * q * q * q * ((y - 1) / y * (y - 2) / y * (Na / (Na - 1)) * (Na / (Na - 2))
                * (Na / (Na - 3)));

        double a02 = q * q * q * q * ((y - 1) / y * (y - 2) / y * (y - 3) / y * (Na / (Na - 1)) * (Na / (Na - 2))
                * (Na / (Na - 3)))
                + p * p * p * p * ((x - 1) / x * (x - 2) / x * (x - 3) / x * (Na / (Na - 1)) * (Na / (Na - 2))
                * (Na / (Na - 3)))
                + 4 * p * p * q * q * ((x - 1) / x * (y - 1)/y * (Na / (Na - 1)) * (Na / (Na - 2))
                * (Na / (Na - 3)));

        double a11 = 2 * p * p * q * ((x - 1) / x * Na / (Na - 1) * Na / (Na - 2))
                + 2 * p * q * q * ((y - 1) / y * Na / (Na - 1) * Na / (Na - 2));

        double a12 = p * p * p * ((x - 1) / x * (x - 2) / x *  Na / (Na - 1) * Na / (Na - 2))
                + q * q * q * ((y - 1) / y * (y - 2) / y * Na / (Na - 1) * Na/(Na-2))
                + p * p * q * ((x - 1) / x * Na / (Na - 1) * Na / (Na - 2))
                + p * q * q * ((y - 1) / y * Na / (Na - 1) * Na / (Na - 2));

        E00 += a00;
        E01 += a01;
        E02 += a02;
        E11 += a11;
        E12 += a12;

        counter++;
    }

    public void done() {
        E00 /= counter;
        E10 = 0;
        E20 = 0;

        E01 /= counter;
        E11 /= counter;
        E21 = 0;

        E02 /= counter;
        E12 /= counter;
        E22 = 1;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IBDExpectedFrequencies{");
        sb.append("E00=").append(E00);
        sb.append(", E10=").append(E10);
        sb.append(", E20=").append(E20);
        sb.append(", E01=").append(E01);
        sb.append(", E11=").append(E11);
        sb.append(", E21=").append(E21);
        sb.append(", E02=").append(E02);
        sb.append(", E12=").append(E12);
        sb.append(", E22=").append(E22);
        sb.append(", counter=").append(counter);
        sb.append('}');
        return sb.toString();
    }
}
