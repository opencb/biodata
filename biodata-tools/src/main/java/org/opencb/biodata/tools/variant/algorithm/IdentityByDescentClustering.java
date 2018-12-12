package org.opencb.biodata.tools.variant.algorithm;

import org.opencb.biodata.models.variant.Variant;

import java.util.*;

public class IdentityByDescentClustering {

    // Expected frequencies for IBS|IBD
    private IBDExpectedFrequencies expectedFrequencies;
    private IdentityByStateClustering ibsClustering;

    public IdentityByDescentClustering() {
        this.expectedFrequencies = new IBDExpectedFrequencies();
        this.ibsClustering = new IdentityByStateClustering();
    }

    /**
     * @return an array of IBS of length: (samples.size()*(samples.size() -1))/2
     * which is samples.size() choose 2
     */
    public List<IdentityByDescent> countIBD(List<Variant> variants, List<String> samples) {
        return countIBD(variants.iterator(), samples);
    }

    /**
     * @return an array of IBS of length: (samples.size()*(samples.size() -1))/2
     * which is samples.size() choose 2
     */
    public List<IdentityByDescent> countIBD(Variant variant, List<String> samples) {
        return countIBD(Collections.singletonList(variant).iterator(), samples);
    }

    /**
     * @return an array of IBD of length: (samples.size()*(samples.size() -1))/2
     * which is samples.size() choose 2
     */
    public List<IdentityByDescent> countIBD(Iterator<Variant> iterator, List<String> samples) {
//        Iterator<Variant> it = preCalcGenomeIBD(iterator);

        List<IdentityByDescent> ibds = new ArrayList<>();

        double s, z0, z1, z2;
        double e00, e10, e20;
        double e01, e11, e21;
        double e02, e12, e22;

        // Compute IBS values and calculate IBS|IBD expected frequencies
        List<IdentityByState> identityByStates = ibsClustering.countIBS(iterator, samples, expectedFrequencies);

        for (IdentityByState ibs: identityByStates) {
            s = ibs.ibs[0] + ibs.ibs[1] + ibs.ibs[2];

            // E_IBS[row=IBS][col=IBD]
            // E(IBD)(IBS)

            e00 = expectedFrequencies.E00 * s;
            e10 = expectedFrequencies.E10 * s;
            e20 = expectedFrequencies.E20 * s;

            e01 = expectedFrequencies.E01 * s;
            e11 = expectedFrequencies.E11 * s;
            e21 = expectedFrequencies.E21 * s;

            e02 = expectedFrequencies.E02 * s;
            e12 = expectedFrequencies.E12 * s;
            e22 = expectedFrequencies.E22 * s;

            z0 =  ibs.ibs[0] / e00;
            z1 = (ibs.ibs[1] - z0 * e01) / e11;
            z2 = (ibs.ibs[2] - z0 * e02 - z1 * e12) / e22;

            // Bound IBD estimates to sum to 1
            // and fall within 0-1 range
            if (z0 > 1) {
                z0 = 1;
                z1 = 0;
                z2 = 0;
            }
            if (z1 > 1) {
                z1 = 1;
                z0 = 0;
                z2 = 0;
            }
            if (z2 > 1) {
                z2 = 1;
                z0 = 0;
                z1 = 0;
            }

            if (z0 < 0) {
                s = z1 + z2;
                z1 /= s;
                z2 /= s;
                z0 = 0;
            }
            if (z1 < 0) {
                s = z0 + z2;
                z0 /= s;
                z2 /= s;
                z1 = 0;
            }
            if (z2 < 0) {
                s = z0 + z1;
                z0 /= s;
                z1 /= s;
                z2 = 0;
            }

            // Possibly constrain IBD estimates to within possible triangle
            // i.e. 0.5 0.0 0.5 is invalid
            //
            // Constraint : z1^2 - 4 z0 z2 >= 0
            //            : x^2 - 2 pi x + z2  = 0
            //
            //              where pi = (z1 + 2 z2) / 2
            //
            // So the constaint can also be written as
            //
            //              pi^2 >=  z2

            double pihat = z1 / 2 + z2;

            // Create IBD
            IdentityByDescent ibd = new IdentityByDescent(z0, z1, z2, pihat);
            ibd.setIbs(ibs.ibs);

            // And add IBD to the list
            ibds.add(ibd);
        }

        return ibds;
    }

    public IBDExpectedFrequencies getExpectedFrequencies() {
        return expectedFrequencies;
    }

    public IdentityByDescentClustering setExpectedFrequencies(IBDExpectedFrequencies expectedFrequencies) {
        this.expectedFrequencies = expectedFrequencies;
        return this;
    }

    public IdentityByStateClustering getIbsClustering() {
        return ibsClustering;
    }

    public IdentityByDescentClustering setIbsClustering(IdentityByStateClustering ibsClustering) {
        this.ibsClustering = ibsClustering;
        return this;
    }
}
