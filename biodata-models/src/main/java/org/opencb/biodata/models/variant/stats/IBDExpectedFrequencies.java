package org.opencb.biodata.models.variant.stats;

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.SampleEntry;

import java.io.Serializable;
import java.util.List;

// Expected frequencies for IBS|IBD

public class IBDExpectedFrequencies implements Serializable {
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
        // Check chromosome sex or haploid, then return
        String chrom = variant.getChromosome();
        if (StringUtils.isNotEmpty(chrom)) {
            chrom = chrom.toUpperCase();
            if (chrom.equals("X") || chrom.equals("Y") || chrom.equals("MT")) {
                return;
            }
        }

        double x = 0;
        double Na = 0; // = # alleles = 2N where N is number of individuals

        for (SampleEntry samples: variant.getStudies().get(0).getSamples()) {
            Genotype gt = new Genotype(samples.getData().get(0)); // first element: genotype
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

    public double getE00() {
        return E00;
    }

    public IBDExpectedFrequencies setE00(double e00) {
        E00 = e00;
        return this;
    }

    public double getE10() {
        return E10;
    }

    public IBDExpectedFrequencies setE10(double e10) {
        E10 = e10;
        return this;
    }

    public double getE20() {
        return E20;
    }

    public IBDExpectedFrequencies setE20(double e20) {
        E20 = e20;
        return this;
    }

    public double getE01() {
        return E01;
    }

    public IBDExpectedFrequencies setE01(double e01) {
        E01 = e01;
        return this;
    }

    public double getE11() {
        return E11;
    }

    public IBDExpectedFrequencies setE11(double e11) {
        E11 = e11;
        return this;
    }

    public double getE21() {
        return E21;
    }

    public IBDExpectedFrequencies setE21(double e21) {
        E21 = e21;
        return this;
    }

    public double getE02() {
        return E02;
    }

    public IBDExpectedFrequencies setE02(double e02) {
        E02 = e02;
        return this;
    }

    public double getE12() {
        return E12;
    }

    public IBDExpectedFrequencies setE12(double e12) {
        E12 = e12;
        return this;
    }

    public double getE22() {
        return E22;
    }

    public IBDExpectedFrequencies setE22(double e22) {
        E22 = e22;
        return this;
    }

    public int getCounter() {
        return counter;
    }

    public IBDExpectedFrequencies setCounter(int counter) {
        this.counter = counter;
        return this;
    }
}
