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

import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;

/**
 * Created by jmmut on 2015-08-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantStatsCalculator {

    public static VariantStats calculate(Variant variant, StudyEntry study) {
        return calculate(variant, study, study.getSamplesName());
    }

    public static VariantStats calculate(Variant variant, StudyEntry study, Collection<String> sampleNames) {
        VariantStats variantStats = new VariantStats();

        Integer gtIdx = study.getFormatPositions().get("GT");
        LinkedHashMap<String, Integer> samplesPosition = study.getSamplesPosition();

        Map<Genotype, Integer> gtCount = new HashMap<>();
        Map<String, Genotype> gts = new TreeMap<>(String::compareTo);
        for (String sampleName : sampleNames) {
            Integer sampleIdx = samplesPosition.get(sampleName);
            if (sampleIdx == null) {
                continue;
            }
            String genotype = study.getSamplesData().get(sampleIdx).get(gtIdx);
            Genotype g = gts.computeIfAbsent(genotype, key -> new Genotype(genotype));
            gtCount.compute(g, (s, prev) -> prev == null ? 1 : prev + 1);

        }  // Finish all samples loop

        calculate(gtCount, variantStats, variant.getReference(), variant.getAlternate());

        // Calculate Hardy-Weinberg statistic       //FIXME
//        variantStats.getHw().calculate();

//        // Update variables finally used to update file_stats_t structure
//        if ("PASS".equalsIgnoreCase(attributes.get("FILTER"))) {
//            variantStats.setPassedFilters(true);
//        }
//
//        if (attributes.containsKey("QUAL") && !(".").equals(attributes.get("QUAL"))) {
//            float qualAux = Float.valueOf(attributes.get("QUAL"));
//            if (qualAux >= 0) {
//                variantStats.setQuality(qualAux);
//            }
//        }

        return variantStats;
    }

    public static VariantStats calculate(Variant variant, int homRefCount, int hetCount, int homAltCount, int missingCount) {
        Map<Genotype, Integer> gtCount = new HashMap<>(4);
        gtCount.put(new Genotype("0/0"), homRefCount);
        gtCount.put(new Genotype("0/1"), hetCount);
        gtCount.put(new Genotype("1/1"), homAltCount);
        gtCount.put(new Genotype("./."), missingCount);

        VariantStats variantStats = new VariantStats();
        calculate(gtCount, variantStats, variant.getReference(), variant.getAlternate());
        return variantStats;
    }

    public static VariantStats calculateHemizygous(Variant variant, int refCount, int altCount, int missingCount) {
        Map<Genotype, Integer> gtCount = new HashMap<>(4);
        gtCount.put(new Genotype("0"), refCount);
        gtCount.put(new Genotype("1"), altCount);
        gtCount.put(new Genotype("."), missingCount);

        VariantStats variantStats = new VariantStats();
        calculate(gtCount, variantStats, variant.getReference(), variant.getAlternate());
        return variantStats;
    }

    public static VariantStats calculate(Variant variant, Map<Genotype, Integer> genotypeCount) {
        return calculate(variant, genotypeCount, true);
    }

    public static VariantStats calculate(Variant variant, Map<Genotype, Integer> genotypeCount, boolean multiAllelic) {
        VariantStats variantStats = new VariantStats();
        calculate(genotypeCount, variantStats, variant.getReference(), variant.getAlternate(), multiAllelic);
        return variantStats;
    }

    public static void calculate(Map<Genotype, Integer> genotypeCount, VariantStats variantStats,
                                 String refAllele, String altAllele) {
        calculate(genotypeCount, variantStats, refAllele, altAllele, true);
    }

    public static void calculate(Map<Genotype, Integer> genotypeCount, VariantStats variantStats,
                                 String refAllele, String altAllele, boolean multiAllelic) {
//        Map<String, Genotype> gts = new TreeMap<>(String::compareTo);
        int[] allelesCount = new int[2];
        int totalAllelesCount = 0;
        int totalGenotypesCount = 0;
        int missingGenotypes = 0;
        int missingAlleles = 0;

        genotypeCount = removePhaseFromGenotypeCount(genotypeCount);

        for (Map.Entry<Genotype, Integer> entry : genotypeCount.entrySet()) {
            Genotype g = entry.getKey();
            Integer numGt = entry.getValue();

            if (g.getCode().equals(AllelesCode.MULTIPLE_ALTERNATES)) {
                if (multiAllelic) {
                    variantStats.addGenotype(g, numGt, false);
                }
            } else {
                variantStats.addGenotype(g, numGt, false);
            }

            // Check missing alleles and genotypeCounters
            switch (g.getCode()) {
                case MULTIPLE_ALTERNATES:
                case ALLELES_OK:
                    for (int i = 0; i < g.getPloidy(); i++) {
                        // Count only REF and ALT alleles.
                        if (g.getAllele(i) <= 1) {
                            allelesCount[g.getAllele(i)] += numGt;
                        }
                    }

                    totalAllelesCount += g.getPloidy() * numGt;
                    totalGenotypesCount += numGt;

                    // Counting genotypeCounters for Hardy-Weinberg (all phenotypes)
                    //FIXME//FIXME//FIXME//FIXME
//                    if (g.isAlleleRef(0) && g.isAlleleRef(1)) { // 0|0
//                        variantStats.getHw().incN_AA();
//                    } else if ((g.isAlleleRef(0) && g.getAllele(1) == 1) || (g.getAllele(0) == 1 && g.isAlleleRef(1))) {  // 0|1, 1|0
//                        variantStats.getHw().incN_Aa();
//
//                    } else if (g.getAllele(0) == 1 && g.getAllele(1) == 1) {
//                        variantStats.getHw().incN_aa();
//                    }

                    break;
                case ALLELES_MISSING:
                    // Missing genotype (one or both alleles missing)
                    missingGenotypes += numGt;
                    for (int i = 0; i < g.getPloidy(); i++) {
                        if (g.getAllele(i) < 0) {
                            missingAlleles += numGt;
                        } else {
                            allelesCount[g.getAllele(i)] += numGt;
                            totalAllelesCount += numGt;
                        }
                    }
//                    totalGenotypesCount += numGt;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown allele code " + g.getCode());
            }
        }


        // Set counts for each allele
        variantStats.setRefAlleleCount(allelesCount[0]);
        variantStats.setAltAlleleCount(allelesCount[1]);
        variantStats.setAlleleCount(totalAllelesCount);
        variantStats.setMissingAlleleCount(missingAlleles);
        variantStats.setMissingGenotypeCount(missingGenotypes);

        // Calculate MAF and MGF
        calculateAlleleFrequencies(totalAllelesCount, variantStats, refAllele, altAllele);
        calculateGenotypeFrequencies(totalGenotypesCount, variantStats);

    }

    /**
     * Calculates the statistics for some variants read from a set of files, and
     * optionally given pedigree information. Some statistics like inheritance
     * patterns can only be calculated if pedigree information is provided.
     *
     * @param variants The variants whose statistics will be calculated
     * @param ped Optional pedigree information to calculate some statistics
     */
    public static void calculateStatsForVariantsList(List<Variant> variants, Pedigree ped) {
        for (Variant variant : variants) {
            for (StudyEntry entry : variant.getStudies()) {
                VariantStats stats = calculate(variant, entry);
                entry.setStats(StudyEntry.DEFAULT_COHORT, stats);
            }
        }
    }

    private static void calculateAlleleFrequencies(int totalAllelesCount, VariantStats variantStats,
                                                   String refAllele, String altAllele) {
        if (totalAllelesCount < 0) {
            throw new IllegalArgumentException("The number of alleles must be equals or greater than zero");
        }

        if (totalAllelesCount == 0) {
            // Nothing to calculate here
            variantStats.setMaf((float) -1);
            variantStats.setMafAllele(null);
            return;
        }

        variantStats.setAlleleCount(totalAllelesCount);
        variantStats.setRefAlleleFreq(variantStats.getRefAlleleCount() / (float) totalAllelesCount);
        variantStats.setAltAlleleFreq(variantStats.getAltAlleleCount() / (float) totalAllelesCount);

        if (variantStats.getRefAlleleFreq() <= variantStats.getAltAlleleFreq()) {
            variantStats.setMaf(variantStats.getRefAlleleFreq());
            variantStats.setMafAllele(refAllele);
        } else {
            variantStats.setMaf(variantStats.getAltAlleleFreq());
            variantStats.setMafAllele(altAllele);
        }
    }

    public static void calculateGenotypeFrequencies(VariantStats variantStats) {
        int totalGenotypesCount = 0;
        for (Map.Entry<Genotype, Integer> entry : variantStats.getGenotypeCount().entrySet()) {
            if (!entry.getKey().getCode().equals(AllelesCode.ALLELES_MISSING)) {
                totalGenotypesCount += entry.getValue();
            }
        }

        calculateGenotypeFrequencies(totalGenotypesCount, variantStats);
    }

    private static void calculateGenotypeFrequencies(int totalGenotypesCount, VariantStats variantStats) {
        if (totalGenotypesCount < 0) {
            throw new IllegalArgumentException("The number of genotypeCounters must be equals or greater than zero");
        }

        if (variantStats.getGenotypeCount().isEmpty() || totalGenotypesCount == 0) {
            // Nothing to calculate here
            variantStats.setMgf((float) -1);
            variantStats.setMgfGenotype(null);
            return;
        }

        // Set all combinations of genotypeCounters to zero
        Map<Genotype, Float> genotypesFreq = new HashMap<>(variantStats.getGenotypeCount().size());
        genotypesFreq.put(new Genotype("0/0"), 0.0f);
        genotypesFreq.put(new Genotype("0/1"), 0.0f);
        genotypesFreq.put(new Genotype("1/1"), 0.0f);


        float mgf = Float.MAX_VALUE;
        Genotype mgfGenotype = null;

        for (Map.Entry<Genotype, Integer> entry : variantStats.getGenotypeCount().entrySet()) {
            Genotype gt = entry.getKey();
            if (gt.getCode() == AllelesCode.ALLELES_MISSING) {
                // Missing genotypeCounters shouldn't have frequencies calculated
                continue;
            }

            float freq = entry.getValue() /  (float) totalGenotypesCount;
            genotypesFreq.put(gt, freq);

            // Only use valid genotypes for calculating MGF
            if (gt.getCode() == AllelesCode.ALLELES_OK && freq < mgf) {
                mgf = freq;
                mgfGenotype = gt;
            }
        }

        variantStats.setGenotypeFreq(genotypesFreq);
        if (mgfGenotype != null) {
            variantStats.setMgf(mgf);
            variantStats.setMgfGenotype(mgfGenotype.toString());
        }
    }

    private static Map<Genotype, Integer> removePhaseFromGenotypeCount(Map<Genotype, Integer> genotypeCount) {
        Map<Genotype, Integer> unphasedGenotypeCount = new HashMap<>();
        unphasedGenotypeCount.put(new Genotype("0/0"), 0);
        unphasedGenotypeCount.put(new Genotype("0/1"), 0);
        unphasedGenotypeCount.put(new Genotype("1/1"), 0);

        for (Map.Entry<Genotype, Integer> entry : genotypeCount.entrySet()) {
            Genotype gt = entry.getKey();

            if (gt.isPhased()) {
                gt = new Genotype(gt);
                // Clean ref/alt if any
                gt.setReference(null);
                gt.setAlternates(Collections.emptyList());
                gt.setPhased(false);
                gt.normalizeAllelesIdx();
            } else if (gt.getReference() != null) {
                gt = new Genotype(gt);
                gt.setReference(null);
                gt.setAlternates(Collections.emptyList());
            }

            unphasedGenotypeCount.merge(gt, entry.getValue(), Integer::sum);
        }
        return unphasedGenotypeCount;
    }


}
