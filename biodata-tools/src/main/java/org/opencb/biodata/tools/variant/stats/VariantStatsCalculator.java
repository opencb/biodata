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

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.AllelesCode;
import org.opencb.biodata.models.variant.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;

/**
 * Created by jmmut on 2015-08-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantStatsCalculator {

    public static final String OTHER_ALLELE = "*";

    public static VariantStats calculate(Variant variant, StudyEntry study) {
        return calculate(variant, study, study.getSamplesName());
    }

    public static VariantStats calculate(Variant variant, StudyEntry study, Collection<String> sampleNames) {
        VariantStats variantStats = new VariantStats();

        Integer gtIdx = study.getSampleDataKeyPosition("GT");
        LinkedHashMap<String, Integer> samplesPosition = study.getSamplesPosition();

        Map<Genotype, Integer> gtCount = new HashMap<>();
        Map<String, Genotype> gts = new TreeMap<>(String::compareTo);
        for (String sampleName : sampleNames) {
            Integer sampleIdx = samplesPosition.get(sampleName);
            if (sampleIdx == null) {
                continue;
            }
            Genotype genotype;
            if (gtIdx != null) {
                String g = study.getSamples().get(sampleIdx).getData().get(gtIdx);
                genotype = gts.computeIfAbsent(g, key -> new Genotype(g));
            } else {
                genotype = null;
            }
            gtCount.merge(genotype, 1, Integer::sum);

        }  // Finish all samples loop

        calculate(gtCount, variantStats, variant.getReference(), variant.getAlternate());

        int numFilterFiles = 0;
        int numQualFiles = 0;
        double qualSum = 0;
        for (FileEntry file : study.getFiles()) {
            String filter = file.getData().get(StudyEntry.FILTER);
            if (StringUtils.isNotEmpty(filter)) {
                addFileFilter(filter, variantStats.getFilterCount());
                numFilterFiles++;
            }
            String qual = file.getData().get(StudyEntry.QUAL);
            if (StringUtils.isNotEmpty(qual) && !qual.equals(".")) {
                qualSum += Double.parseDouble(qual);
                numQualFiles++;
            }
        }
        calculateFilterFreq(variantStats, numFilterFiles, variantStats.getFilterCount());
        variantStats.setQualityAvg((float) (qualSum / numQualFiles));
        variantStats.setQualityCount(numQualFiles);

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

    public static void calculateFilterFreq(VariantStats variantStats, final int numFiles) {
        calculateFilterFreq(variantStats, numFiles, variantStats.getFilterCount());
    }

    public static void calculateFilterFreq(VariantStats variantStats, final int numFiles, Map<String, Integer> filterCount) {
        filterCount.putIfAbsent("PASS", 0);
        variantStats.setFilterCount(filterCount);
        if (numFiles > 0) {
            Map<String, Float> filterFreq = variantStats.getFilterFreq();
            filterCount.forEach((filter, count) -> filterFreq.put(filter, count / (float) numFiles));
        } // else -> do not fill freqs
        variantStats.setFileCount(numFiles);
    }

    public static void addFileFilter(String filter, Map<String, Integer> filterCount) {
        if (StringUtils.isEmpty(filter)) {
            filter = ".";
        }
        int endIndex = 0;
        do {
            int startIndex = endIndex;
            endIndex = filter.indexOf(";", endIndex);
            if (endIndex < 0) {
                filterCount.merge(filter.substring(startIndex), 1, Integer::sum);
                break;
            } else {
                filterCount.merge(filter.substring(startIndex, endIndex), 1, Integer::sum);
            }
            endIndex++;
        } while (true);
    }

    public static void calculate(Map<Genotype, Integer> genotypeCount, VariantStats variantStats,
                                 String refAllele, String altAllele, boolean multiAllelic) {
//        Map<String, Genotype> gts = new TreeMap<>(String::compareTo);
        int[] allelesCount = new int[2];
        int totalAllelesCount = 0;
        int nonMissingGenotypes = 0;
        int missingGenotypes = 0;
        int missingAlleles = 0;

        genotypeCount = removePhaseFromGenotypeCount(genotypeCount);

        Iterator<Map.Entry<Genotype, Integer>> iterator = genotypeCount.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Genotype, Integer> entry = iterator.next();
            Genotype g = entry.getKey();
            Integer numGt = entry.getValue();

            if (g == null) {
                // Do not count NA samples for samplesCount
                variantStats.addGenotype(genotypeToStringSimple(g, multiAllelic), numGt);
                // Skip rest of the loop
                continue;
            }

            // Check missing alleles and genotypes
            switch (g.getCode()) {
                case MULTIPLE_ALTERNATES:
                case ALLELES_OK:
                case PARTIAL_ALLELES_MISSING:
                    nonMissingGenotypes += numGt;
                    variantStats.addGenotype(genotypeToStringSimple(g, multiAllelic), numGt);
                    for (int allele : g.getAllelesIdx()) {
                        if (allele < 0) {
                            missingAlleles += numGt;
                        } else {
                            // Count only REF and ALT alleles.
                            if (allele <= 1) {
                                allelesCount[allele] += numGt;
                            }
                            totalAllelesCount += numGt;
                        }
                    }
                    break;
                case ALLELES_MISSING:
                    iterator.remove();
                    // Missing genotype (all alleles missing)
                    missingGenotypes += numGt;
                    for (int i = 0; i < g.getPloidy(); i++) {
                        if (g.getAllele(i) < 0) {
                            missingAlleles += numGt;
                        } else {
                            allelesCount[g.getAllele(i)] += numGt;
                            totalAllelesCount += numGt;
                        }
                    }
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
        variantStats.setSampleCount(nonMissingGenotypes);

        // Calculate MAF and MGF
        calculateAlleleFrequencies(totalAllelesCount, variantStats, refAllele, altAllele);
        calculateGenotypeFrequencies(variantStats, nonMissingGenotypes);

    }

    private static String genotypeToStringSimple(Genotype g, boolean multiAllelic) {
        if (g == null) {
            return Genotype.NA;
        } else {
            switch (g.getCode()) {
                case ALLELES_OK:
                case PARTIAL_ALLELES_MISSING:
                    return g.toString();
                case MULTIPLE_ALTERNATES:
                    if (multiAllelic) {
                        return g.toString();
                    } else {
                        return multiAllelicToStringSimple(g);
                    }
                case ALLELES_MISSING:
                    return null;
                default:
                    throw new IllegalArgumentException("Unknown allele code " + g.getCode());
            }
        }
    }

    private static String multiAllelicToStringSimple(Genotype g) {
        g = new Genotype(g);
        int[] allelesIdx = g.getAllelesIdx();
        for (int i = 0; i < allelesIdx.length; i++) {
            if (allelesIdx[i] > 2) {
                allelesIdx[i] = 2;
            }
        }
        return g.toString().replaceAll("2", OTHER_ALLELE);
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
                stats.setCohortId(StudyEntry.DEFAULT_COHORT);
                entry.addStats(stats);
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
        if (variantStats.getSampleCount() == null || variantStats.getSampleCount() < 0) {
            int totalGenotypesCount = 0;
            for (Map.Entry<String, Integer> entry : variantStats.getGenotypeCount().entrySet()) {
                String gtStr = entry.getKey();
                if (!gtStr.equals(Genotype.NA) && !new Genotype(gtStr).getCode().equals(AllelesCode.ALLELES_MISSING)) {
                    totalGenotypesCount += entry.getValue();
                }
            }
            variantStats.setSampleCount(totalGenotypesCount);
        }
        calculateGenotypeFrequencies(variantStats, variantStats.getSampleCount());
    }

    private static void calculateGenotypeFrequencies(VariantStats variantStats, final int totalGenotypesCount) {
        if (totalGenotypesCount < 0) {
            throw new IllegalArgumentException("The number of genotypes must be equals or greater than zero");
        }

        if (variantStats.getGenotypeCount().isEmpty() || totalGenotypesCount == 0) {
            // Nothing to calculate here
            variantStats.setMgf((float) -1);
            variantStats.setMgfGenotype(null);
            return;
        }

        // Set all combinations of genotypes to zero
        Map<String, Float> genotypesFreq = new HashMap<>(variantStats.getGenotypeCount().size());
        genotypesFreq.put("0/0", 0.0f);
        genotypesFreq.put("0/1", 0.0f);
        genotypesFreq.put("1/1", 0.0f);

        float mgf = Float.MAX_VALUE;
        String mgfGenotype = null;

        for (Map.Entry<String, Integer> entry : variantStats.getGenotypeCount().entrySet()) {
            String gtStr = entry.getKey();
            if (gtStr.equals(Genotype.NA)) {
                // Ignore gt NA from genotype freq
                continue;
            }
            float freq = entry.getValue() /  (float) totalGenotypesCount;
            genotypesFreq.put(gtStr, freq);

            // Only use valid genotypes for calculating MGF
            if (!gtStr.contains(OTHER_ALLELE) && freq < mgf) {
                mgf = freq;
                mgfGenotype = gtStr;
            }
        }

        variantStats.setGenotypeFreq(genotypesFreq);
        if (mgfGenotype != null) {
            variantStats.setMgf(mgf);
            variantStats.setMgfGenotype(mgfGenotype);
        }
    }

    public static Map<Genotype, Integer> removePhaseFromGenotypeCount(Map<Genotype, Integer> genotypeCount) {
        Map<Genotype, Integer> unphasedGenotypeCount = new HashMap<>();
        unphasedGenotypeCount.put(new Genotype("0/0"), 0);
        unphasedGenotypeCount.put(new Genotype("0/1"), 0);
        unphasedGenotypeCount.put(new Genotype("1/1"), 0);

        for (Map.Entry<Genotype, Integer> entry : genotypeCount.entrySet()) {
            Genotype gt = entry.getKey();
            if (gt != null) {
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
            }
            unphasedGenotypeCount.merge(gt, entry.getValue(), Integer::sum);
        }
        return unphasedGenotypeCount;
    }


}
