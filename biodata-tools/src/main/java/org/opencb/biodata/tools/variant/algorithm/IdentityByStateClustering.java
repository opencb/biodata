/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant.algorithm;

import org.opencb.biodata.models.variant.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.stats.IBDExpectedFrequencies;
import org.opencb.biodata.models.variant.stats.IdentityByState;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Created by jmmut on 2015-11-13.
 *
 * assumptions:
 * - samples.size() > 1 && samples.size() < 10000
 * - only one study
 * - only 2 alleles per genotype
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class IdentityByStateClustering {

    public static final int MAX_SAMPLES_ALLOWED = 10000;

    /**
     * @return an array of IBS of length: (samples.size()*(samples.size() -1))/2
     * which is samples.size() choose 2
     */
    public List<IdentityByState> countIBS(List<Variant> variants, List<String> samples) {
        return countIBS(variants.iterator(), samples, null);
    }

    public List<IdentityByState> countIBS(List<Variant> variants, List<String> samples,
                                          IBDExpectedFrequencies expectedFreqs) {
        return countIBS(variants.iterator(), samples, expectedFreqs);
    }

    /**
     * @return an array of IBS of length: (samples.size()*(samples.size() -1))/2
     * which is samples.size() choose 2
     */
    public List<IdentityByState> countIBS(Variant variant, List<String> samples) {
        return countIBS(Collections.singletonList(variant).iterator(), samples, null);
    }

    public List<IdentityByState> countIBS(Variant variant, List<String> samples, IBDExpectedFrequencies expectedFreqs) {
        return countIBS(Collections.singletonList(variant).iterator(), samples, expectedFreqs);
    }

    /**
     * @return an array of IBS of length: (samples.size()*(samples.size() -1))/2
     * which is samples.size() choose 2
     */
    public List<IdentityByState> countIBS(Iterator<Variant> iterator, List<String> samples) {
        return countIBS(iterator, samples, null);
    }

    public List<IdentityByState> countIBS(Iterator<Variant> iterator, List<String> samples,
                                          IBDExpectedFrequencies expectedFreqs) {

        // assumptions
        if (samples.size() < 1 || samples.size() > MAX_SAMPLES_ALLOWED) {
            throw new IllegalArgumentException("samples.size() is " + samples.size()
                    + " and it should be between 1 and" + MAX_SAMPLES_ALLOWED);
        }
        final int studyIndex = 0;

        // loops
        List<IdentityByState> counts = new ArrayList<>(getAmountOfPairs(samples.size()));

        for (int i = 0; i < getAmountOfPairs(samples.size()); i++) {
            counts.add(new IdentityByState());
        }

        while (iterator.hasNext()) {
            Variant variant = iterator.next();
            if (expectedFreqs != null) {
                expectedFreqs.update(variant);
            }

            forEachPair(samples, (int i, int j, int compoundIndex) -> {
                StudyEntry studyEntry = variant.getStudies().get(studyIndex);
                String gtI = studyEntry.getSampleData(samples.get(i), "GT");
                String gtJ = studyEntry.getSampleData(samples.get(j), "GT");
                Genotype genotypeI = new Genotype(gtI);
                Genotype genotypeJ = new Genotype(gtJ);
                int whichIBS = countSharedAlleles(genotypeI.getAllelesIdx().length, genotypeI, genotypeJ);
                counts.get(compoundIndex).ibs[whichIBS]++;
            });
        }
        if (expectedFreqs != null) {
            expectedFreqs.done();
        }
        return counts;
    }

    public List<IdentityByState> initCounts(List<String> samples) {
        // loops
        List<IdentityByState> counts = new ArrayList<>(getAmountOfPairs(samples.size()));

        for (int i = 0; i < getAmountOfPairs(samples.size()); i++) {
            counts.add(new IdentityByState());
        }

        // assumptions
        if (samples.size() < 1 || samples.size() > MAX_SAMPLES_ALLOWED) {
            throw new IllegalArgumentException("samples.size() is " + samples.size()
                    + " and it should be between 1 and" + MAX_SAMPLES_ALLOWED);
        }

        return counts;
    }

    public void countIBS(Variant variant, List<String> samples, List<IdentityByState> counts) {
        final int studyIndex = 0;

        forEachPair(samples, (int i, int j, int compoundIndex) -> {
            StudyEntry studyEntry = variant.getStudies().get(studyIndex);
            String gtI = studyEntry.getSampleData(samples.get(i), "GT");
            String gtJ = studyEntry.getSampleData(samples.get(j), "GT");
            Genotype genotypeI = new Genotype(gtI);
            Genotype genotypeJ = new Genotype(gtJ);
            int whichIBS = countSharedAlleles(genotypeI.getAllelesIdx().length, genotypeI, genotypeJ);
            counts.get(compoundIndex).ibs[whichIBS]++;
        });
    }

    /**
     * Counts the amount of shared alleles in two individuals.
     * This is which IBS kind is this pair: IBS0, IBS1 or IBS2.
     * The idea is to count how many alleles there are of each kind: for instance, 0 reference alleles for individual 1 
     * and 2 reference alleles for individual 2, and then count the alternate alleles. Then take the minimum of each 
     * kind and sum all the minimums.
     * @param allelesCount ploidy
     * @param genotypeFirst first individual's genotype
     * @param genotypeSecond second individual's genotype
     * @return shared alleles count.
     */
    public int countSharedAlleles(int allelesCount, Genotype genotypeFirst, Genotype genotypeSecond) {

        // amount of different alleles: reference, alternate. other alleles (missing, or multiallelic) are ignored
        int[] allelesCountsFirst = new int[2];
        int[] allelesCountsSecond = new int[2];

        for (int k = 0; k < allelesCount; k++) {
            if (genotypeFirst.getAllele(k) == 0) {
                allelesCountsFirst[0]++;
            } else if (genotypeFirst.getAllele(k) == 1) {
                allelesCountsFirst[1]++;
            }
            if (genotypeSecond.getAllele(k) == 0) {
                allelesCountsSecond[0]++;
            } else if (genotypeSecond.getAllele(k) == 1) {
                allelesCountsSecond[1]++;
            }
        }

        int whichIBS = Math.min(allelesCountsFirst[0], allelesCountsSecond[0])
                + Math.min(allelesCountsFirst[1], allelesCountsSecond[1]);

        return whichIBS;
    }

    /**
     * n choose 2
     * @param samplesCount amount of individuals
     * @return amount of combinations of pairs
     */
    public int getAmountOfPairs(int samplesCount) {
        return getCompoundIndex(samplesCount - 2, samplesCount - 1) + 1;
    }

    /**
     *     j
     *    /_0__1__2__3__4_
     * i 0| -  0  1  3  6 |
     *   1|    -  2  4  7 |
     *   2|       -  5  8 |
     *   3|          -  9 |
     *   4|             - |
     *
     *  `(j*(j-1)) / 2` is the amount of numbers in the triangular matrix before the column `j`.
     *
     *  for example, with i=2, j=4:
     *  (j*(j-1)) / 2 == 6;
     *  6+i == 8
     */
    public int getCompoundIndex(int first, int second) {
        if (first >= second) {
            throw new IllegalArgumentException("first (" + first + ") and second (" + second
                    + ") sample indexes, must comply with: 0 <= first < second");
        }
        return (second*second - second)/2 + first;

    }

    /**
     *

     n = (j^2 - j)/2 + i
     0 <= i < j

     replacing i as j:

     n >= (j^2 - j/2 + 0

     n < (j^2 + j)/2

     2*n < j^2 + j

     2*n + 1/4 < j^2 + j + 1/4

     2*n + 1/4 < (j + 1/2)^2 

     sqrt(2*n + 1/4) - 1/2 < j

     similarly, from 

     n = (j^2 - j)/2 + i
     0 <= i < j

     replacing i as 0, gives

     sqrt(2*n + 1/4) + 1/2 >= j

     so we have both boundaries

     sqrt(2*n + 1/4) - 1/2 < j <= sqrt(2*n + 1/4) + 1/2

     lets rename this as

     a < j <= b

     as j is integer, and a + 1 = b, and a is strictly less than j, floor(b) will always be j.
     */
    public int getSecondSampleIndex(int compoundIndex) {
        return (int) Math.round(Math.floor(Math.sqrt(2 * compoundIndex + 0.25) + 0.5));
    }

    /**
     * n = (j^2 - j)/2 + i
     */
    public int getFirstSampleIndex(int compoundIndex, int secondSampleIndex) {
        return compoundIndex - (secondSampleIndex * secondSampleIndex - secondSampleIndex) / 2;
    }

    @FunctionalInterface
    public interface SamplePairConsumer<E extends Exception> { void apply(int sampleI, int sampleJ, int compoundIndex) throws E; }

    public <E extends Exception> void forEachPair(List<String> samples, SamplePairConsumer<E> loopBody) throws E {
        int compound = 0;
        for (int i = 1; i < samples.size(); i++) {
            for (int j = 0; j < i; j++) {
                loopBody.apply(j, i, compound);
                compound++;
            }
        }
    }

    public void write(OutputStream outputStream, List<IdentityByState> ibsList, List<String> samples) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write("IID1\tIID2\tDST\tZ0\tZ1\tZ2\n");
        forEachPair(samples,  (int firstSampleIndex, int secondSampleIndex, int compoundIndex) -> {
            outputStreamWriter.write(samples.get(firstSampleIndex));
            outputStreamWriter.write("\t");
            outputStreamWriter.write(samples.get(secondSampleIndex));
            outputStreamWriter.write("\t");
            outputStreamWriter.write(String.valueOf(ibsList.get(compoundIndex).getDistance()));
            for (int i = 0; i < 3; i++) {
                outputStreamWriter.write("\t");
                outputStreamWriter.write(String.valueOf(ibsList.get(compoundIndex).ibs[i]));
            }
            outputStreamWriter.write("\n");
        });
        outputStreamWriter.flush();
    }
}
