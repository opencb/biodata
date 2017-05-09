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

package org.opencb.biodata.tools.variant.stats;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jmmut on 2015-03-25.
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedExacStatsCalculator extends VariantAggregatedStatsCalculator {

    private static final String AC_HOM = "AC_Hom";
    private static final String AC_HET = "AC_Het";
    private static final String AN_ADJ = "AN_Adj";
    private static final String AC_ADJ = "AC_Adj";

    public VariantAggregatedExacStatsCalculator() {
        super();
    }

    /**
     * @param tagMap Extends the VariantAggregatedVcfFactory(Properties properties) with two extra tags: Het and Hom. Example:
     *
     * SAS.AC = AC_SAS
     * SAS.AN = AN_SAS
     * SAS.HET=Het_SAS
     * SAS.HOM=Hom_SAS
     * ALL.AC =AC_Adj
     * ALL.AN =AN_Adj
     * ALL.HET=AC_Het
     * ALL.HOM=AC_Hom
     *
     * Het is the list of heterozygous counts as listed by VariantVcfExacFactory.getHeterozygousGenotype()
     * Hom is the list of homozygous counts as listed by VariantVcfExacFactory.getHomozygousGenotype()
     *
     */
    public VariantAggregatedExacStatsCalculator(Properties tagMap) {
        super(tagMap);
    }

    @Override
    protected void parseStats(Variant variant, StudyEntry source, int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        StudyEntry sourceEntry = variant.getStudy(source.getStudyId());
        VariantStats stats = new VariantStats(variant);

        if (info.containsKey(AC_HET)) {   // heterozygous genotype count
            String[] hetCounts = info.get(AC_HET).split(COMMA);
            addHeterozygousGenotypes(variant, numAllele, alternateAlleles, stats, hetCounts);
        }

        if (info.containsKey(AC_HOM)) {   // homozygous genotype count
            String[] homCounts = info.get(AC_HOM).split(COMMA);
            addHomozygousGenotype(variant, numAllele, alternateAlleles, stats, homCounts);
        }

        String[] acCounts = null;
        if (info.containsKey(AC_ADJ)) {   // alternative allele counts
            acCounts = info.get(AC_ADJ).split(COMMA);
            if (acCounts.length == alternateAlleles.length) {
                stats.setAltAlleleCount(Integer.parseInt(acCounts[numAllele]));
            }
        }

        if (info.containsKey(AN_ADJ) && info.containsKey(AC_ADJ)) { // inferring implicit refallele count
            setRefAlleleCount(stats, Integer.parseInt(info.get(AN_ADJ)), info.get(AC_ADJ).split(COMMA));
        }

        if (info.containsKey(AC_HOM) && info.containsKey(AC_HET) && info.containsKey(AN_ADJ)) {   // inferring implicit 0/0 count
            int an = Integer.parseInt(info.get(AN_ADJ));
            addReferenceGenotype(variant, stats, an);
        }

        if (info.containsKey(AC_ADJ) && info.containsKey(AN_ADJ)) {
            int an = Integer.parseInt(info.get(AN_ADJ));
            setMaf(an, acCounts, alternateAlleles, stats);
        }

        sourceEntry.setStats(StudyEntry.DEFAULT_COHORT, stats);
    }

    @Override
    protected void parseMappedStats(Variant variant, StudyEntry sourceEntry, int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        Map<String, Integer> ans = new LinkedHashMap<>();
        Map<String, String[]> acs = new LinkedHashMap<>();
        for (Map.Entry<String, String> infoElem : info.entrySet()) {

            String infoTag = infoElem.getKey();
            String infoValue = infoElem.getValue();

            String mappedTag = reverseTagMap.get(infoTag);
            String[] values = infoValue.split(COMMA);
            if (mappedTag != null) {
                String[] opencgaTagSplit = mappedTag.split(DOT);
                String cohortName = opencgaTagSplit[0];
                VariantStats cohortStats = sourceEntry.getCohortStats(cohortName);
                if (cohortStats == null) {
                    cohortStats = new VariantStats(variant);
                    sourceEntry.setCohortStats(cohortName, cohortStats);
                }
                switch (opencgaTagSplit[1]) {
                    case "AC":
                        cohortStats.setAltAlleleCount(Integer.parseInt(values[numAllele]));
                        acs.put(cohortName, values);
                        break;
                    case "AN":
                        ans.put(cohortName, Integer.parseInt(values[0]));
                        break;
                    case "HET":
                        addHeterozygousGenotypes(variant, numAllele, alternateAlleles, cohortStats, values);
                        break;
                    case "HOM":
                        addHomozygousGenotype(variant, numAllele, alternateAlleles, cohortStats, values);
                        break;
                }
            }
        }
        for (String cohortName : sourceEntry.getStats().keySet()) {
            if (ans.containsKey(cohortName)) {
                VariantStats cohortStats = sourceEntry.getStats(cohortName);
                Integer alleleNumber = ans.get(cohortName);
                addReferenceGenotype(variant, cohortStats, alleleNumber);
                setRefAlleleCount(cohortStats, alleleNumber, acs.get(cohortName));
                setMaf(alleleNumber, acs.get(cohortName), alternateAlleles, cohortStats);
            }
        }
    }

    private static void setRefAlleleCount(VariantStats stats, Integer alleleNumber, String alleleCounts[]) {
        int sum = 0;
        for (String ac : alleleCounts) {
            sum += Integer.parseInt(ac);
        }
        stats.setRefAlleleCount(alleleNumber - sum);
    }

    /**
     * Infers the 0/0 genotype count, given that: sum(Heterozygous) + sum(Homozygous) + sum(Reference) = alleleNumber/2
     * @param variant to retrieve the alleles to construct the genotype
     * @param stats where to add the 0/0 genotype count
     * @param alleleNumber total sum of alleles.
     */
    private static void addReferenceGenotype(Variant variant, VariantStats stats, int alleleNumber) {
        int gtSum = 0;
        for (Integer gtCounts : stats.getGenotypesCount().values()) {
            gtSum += gtCounts;
        }
        Genotype genotype = new Genotype("0/0", variant.getReference(), variant.getAlternate());
        stats.addGenotype(genotype, alleleNumber / 2 - gtSum);  // assuming diploid sample! be careful if you copy this code!
    }

    /**
     * Adds the heterozygous genotypes to a variant stats. Those are (in this order):
     * 0/1, 0/2, 0/3, 0/4... 1/2, 1/3, 1/4... 2/3, 2/4... 3/4...
     * for a given amount n of alleles, the number of combinations is (latex): \sum_{i=1}^n( \sum_{j=i}^n( 1 ) ), which resolved is n*(n+1)/2
     * @param variant to retrieve the alleles to construct the genotype
     * @param numAllele
     * @param alternateAlleles
     * @param stats where to add the genotypes count
     * @param hetCounts parsed string
     */
    private static void addHeterozygousGenotypes(Variant variant, int numAllele, String[] alternateAlleles, VariantStats stats, String[] hetCounts) {

        if (hetCounts.length == alternateAlleles.length * (alternateAlleles.length + 1) / 2) {
            for (int i = 0; i < hetCounts.length; i++) {
                Integer alleles[] = new Integer[2];
                getHeterozygousGenotype(i, alternateAlleles.length, alleles);
                String gt = VariantVcfFactory.mapToMultiallelicIndex(alleles[0], numAllele) + "/" + VariantVcfFactory.mapToMultiallelicIndex(alleles[1], numAllele);
                Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAllele]);
                stats.addGenotype(genotype, Integer.parseInt(hetCounts[i]));
            }
        }
    }

    /**
     * Adds the homozygous genotypes to a variant stats. Those are (in this order):
     * 1/1, 2/2, 3/3...
     * @param variant
     * @param numAllele
     * @param alternateAlleles
     * @param stats
     * @param homCounts parsed string
     */
    private void addHomozygousGenotype(Variant variant, int numAllele, String[] alternateAlleles, VariantStats stats, String[] homCounts) {
        if (homCounts.length == alternateAlleles.length) {
            for (int i = 0; i < homCounts.length; i++) {
                Integer alleles[] = new Integer[2];
                getHomozygousGenotype(i + 1, alleles);
                String gt = VariantVcfFactory.mapToMultiallelicIndex(alleles[0], numAllele) + "/" + VariantVcfFactory.mapToMultiallelicIndex(alleles[1], numAllele);
                Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAllele]);
                stats.addGenotype(genotype, Integer.parseInt(homCounts[i]));
            }
        }
    }

    private void setMaf(int totalAlleleCount, String alleleCounts[], String alternateAlleles[], VariantStats stats) {
        if (stats.getMaf() == -1) {

            int referenceCount = stats.getRefAlleleCount();
            float maf = (float) referenceCount / totalAlleleCount;

            String mafAllele = stats.getRefAllele();
            for (int i = 0; i < alleleCounts.length; i++) {
                float auxMaf = (float) Integer.parseInt(alleleCounts[i]) / totalAlleleCount;
                if (auxMaf < maf) {
                    maf = auxMaf;
                    mafAllele = alternateAlleles[i];
                }
            }

            stats.setMaf(maf);
            stats.setMafAllele(mafAllele);
        }
    }

    /**
     * returns in alleles[] the heterozygous genotype specified in index in the sequence (in this example for 3 ALT alleles):
     * 0/1, 0/2, 0/3, 1/2, 1/3, 2/3
     * @param index in this sequence, starting in 0
     * @param numAlternativeAlleles note that this ordering requires knowing how many alleles there are
     * @param alleles returned genotype.
     */
    public static void getHeterozygousGenotype(int index, int numAlternativeAlleles, Integer alleles[]) {
//        index++;
//        double value = (-3 + Math.sqrt(1 + 8 * index)) / 2;    // slower than the iterating version, right?
//        alleles[1] = new Double(Math.ceil(value)).intValue();
//        alleles[0] = alleles[1] - ((alleles[1] + 1) * (alleles[1] +2) / 2 - index);

        int cursor = 0;
        for (int i = 0; i < numAlternativeAlleles; i++) {
            for (int j = i+1; j < numAlternativeAlleles +1; j++) {
                if (i != j) {
                    if (cursor == index) {
                        alleles[0] = i;
                        alleles[1] = j;
                        return;
                    }
                    cursor++;
                }
            }
        }
    }

    /**
     * returns in alleles[] the homozygous genotype specified in index in the sequence:
     * 0/0, 1/1, 2/2, 3/3
     * @param index in this sequence, starting in 0
     * @param alleles returned genotype.
     */
    public static void getHomozygousGenotype(int index, Integer alleles[]) {
        alleles[0] = alleles[1] = index;
    }
}
