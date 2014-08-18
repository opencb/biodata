package org.opencb.biodata.tools.variant;

import java.util.*;
import org.opencb.biodata.models.feature.AllelesCode;
import static org.opencb.biodata.models.feature.AllelesCode.ALLELES_OK;
import static org.opencb.biodata.models.feature.AllelesCode.HAPLOID;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.pedigree.Condition;
import org.opencb.biodata.models.pedigree.Individual;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@Deprecated
public class StatsCalculator {

//    public static List<VariantStats> variantStats(List<Variant> variants, Pedigree ped) {
//        List<VariantStats> statList = new ArrayList<>(variants.size());
//
//        for (Variant variant : variants) {
//            for (ArchivedVariantFile file : variant.getFiles().values()) {
//                int genotypeCurrentPos;
//                int totalAllelesCount = 0;
//                int totalGenotypesCount = 0;
//
//                float controlsDominant = 0;
//                float casesDominant = 0;
//                float controlsRecessive = 0;
//                float casesRecessive = 0;
//
//                VariantStats vcfStat = new VariantStats(variant);
//                vcfStat.setNumSamples(file.getSampleNames().size());
//
//                int[] allelesCount = new int[vcfStat.getNumAlleles()];
//                int[] genotypesCount = new int[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];
//
//                for (Map.Entry<String, Map<String, String>> sample : file.getSamplesData().entrySet()) {
//                    String sampleName = sample.getKey();
//                    Genotype g = new Genotype(sample.getValue().get("GT"));
//                    vcfStat.addGenotype(g);
//
//                    // Check missing alleles and genotypes
//                    switch (g.getCode()) {
//                        case ALLELES_OK:
//                            // Both alleles set
//                            genotypeCurrentPos = g.getAllele(0) * (vcfStat.getNumAlleles()) + g.getAllele(1);
//
//                            allelesCount[g.getAllele(0)]++;
//                            allelesCount[g.getAllele(1)]++;
//                            genotypesCount[genotypeCurrentPos]++;
//
//                            totalAllelesCount += 2;
//                            totalGenotypesCount++;
//
//                            // Counting genotypes for Hardy-Weinberg (all phenotypes)
//                            if (g.isAlleleRef(0) && g.isAlleleRef(1)) { // 0|0
//                                vcfStat.getHw().incN_AA();
//                            } else if ((g.isAlleleRef(0) && g.getAllele(1) == 1) || (g.getAllele(0) == 1 && g.isAlleleRef(1))) {  // 0|1, 1|0
//                                vcfStat.getHw().incN_Aa();
//
//                            } else if (g.getAllele(0) == 1 && g.getAllele(1) == 1) {
//                                vcfStat.getHw().incN_aa();
//                            }
//
//                            break;
//                        case HAPLOID:
//                            // Haploid (chromosome X/Y)
//                            try {
//                                allelesCount[g.getAllele(0)]++;
//                            } catch (ArrayIndexOutOfBoundsException e) {
//                                System.out.println("vcfRecord = " + variant);
//                                System.out.println("g = " + g);
//                            }
//                            totalAllelesCount++;
//                            break;
//                        default:
//                            // Missing genotype (one or both alleles missing)
//                            vcfStat.setMissingGenotypes(vcfStat.getMissingGenotypes() + 1);
//                            if (g.getAllele(0) < 0) {
//                                vcfStat.setMissingAlleles(vcfStat.getMissingAlleles() + 1);
//                            } else {
//                                allelesCount[g.getAllele(0)]++;
//                                totalAllelesCount++;
//                            }
//
//                            if (g.getAllele(1) < 0) {
//                                vcfStat.setMissingAlleles(vcfStat.getMissingAlleles() + 1);
//                            } else {
//                                allelesCount[g.getAllele(1)]++;
//                                totalAllelesCount++;
//                            }
//                            break;
//
//                    }
//
//                    // Include statistics that depend on pedigree information
//                    if (ped != null) {
//                        if (g.getCode() == AllelesCode.ALLELES_OK || g.getCode() == AllelesCode.HAPLOID) {
//                            Individual ind = ped.getIndividual(sampleName);
////                            if (isMendelianError(ind, g, variant, file)) {
////                                vcfStat.setMendelinanErrors(vcfStat.getMendelinanErrors() + 1);
////
////                            }
//                            if (g.getCode() == AllelesCode.ALLELES_OK) {
//                                // Check inheritance models
//                                if (ind.getCondition() == Condition.UNAFFECTED) {
//                                    if (g.isAlleleRef(0) && g.isAlleleRef(1)) { // 0|0
//                                        controlsDominant++;
//                                        controlsRecessive++;
//
//                                    } else if ((g.isAlleleRef(0) && !g.isAlleleRef(1)) || (!g.isAlleleRef(0) || g.isAlleleRef(1))) { // 0|1 or 1|0
//                                        controlsRecessive++;
//
//                                    }
//                                } else if (ind.getCondition() == Condition.AFFECTED) {
//                                    if (!g.isAlleleRef(0) && !g.isAlleleRef(1) && g.getAllele(0) == g.getAllele(1)) {// 1|1, 2|2, and so on
//                                        casesRecessive++;
//                                        casesDominant++;
//                                    } else if (!g.isAlleleRef(0) || !g.isAlleleRef(1)) { // 0|1, 1|0, 1|2, 2|1, 1|3, and so on
//                                        casesDominant++;
//
//                                    }
//                                }
//
//                            }
//
//                        }
//                    }
//
//                }  // Finish all samples loop
//
//                vcfStat.setAllelesCount(allelesCount);
//                vcfStat.setGenotypesCount(genotypesCount);
//
//                // Calculate MAF and MGF
//                calculateAlleleAndGenotypeFrequencies(vcfStat, totalAllelesCount, totalGenotypesCount);
//
//                // Calculate Hardy-Weinberg statistic
//                vcfStat.getHw().calculate();
//
//                // Indels
////                /*
////                 * 3 possibilities for being an INDEL:
////                 * - The value of the ALT field is <DEL> or <INS>
////                 * - The REF allele is not . but the ALT is
////                 * - The REF allele is . but the ALT is not
////                 * - The REF field length is different than the ALT field length
////                 */
////                if ((!vcfStat.getRefAlleles().equals(".") && variant.getAlternate().equals(".")) ||
////                        (variant.getAlternate().equals(".") && !vcfStat.getRefAlleles().equals(".")) ||
////                        (variant.getAlternate().equals("<INS>")) ||
////                        (variant.getAlternate().equals("<DEL>")) ||
////                        variant.getReference().length() != variant.getAlternate().length()) {
////                    vcfStat.setIndel(true);
////                } else {
////                    vcfStat.setIndel(false);
////                }
////                
////                if (variant.getId() != null && !variant.getId().equals(".")) {
////                    vcfStat.setSNP(true);
////                }
//                // Transitions and transversions
//                calculateTransitionsAndTransversions(vcfStat, variant.getReference(), variant.getAlternate());
//
//                // Update variables finally used to update file_stats_t structure
//                if (file.hasAttribute("FILTER") && "PASS".equalsIgnoreCase(file.getAttribute("FILTER"))) {
//                    vcfStat.setPassedFilters(true);
//                }
//
//                if (file.hasAttribute("QUAL") && !(".").equals(file.getAttribute("QUAL"))) {
//                    float qualAux = Float.valueOf(file.getAttribute("QUAL"));
//                    if (qualAux >= 0) {
//                        vcfStat.setQual(qualAux);
//                    }
//                }
//
//                // Once all samples have been traversed, calculate % that follow inheritance model
//                controlsDominant = controlsDominant * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
//                casesDominant = casesDominant * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
//                controlsRecessive = controlsRecessive * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
//                casesRecessive = casesRecessive * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
//
//                vcfStat.setCasesPercentDominant(casesDominant);
//                vcfStat.setControlsPercentDominant(controlsDominant);
//                vcfStat.setCasesPercentRecessive(casesRecessive);
//                vcfStat.setControlsPercentRecessive(controlsRecessive);
//
//                statList.add(vcfStat);
//                file.setStats(vcfStat); // TODO Correct?
//            }
//        }
//
//        return statList;
//    }
//
//    private static void calculateAlleleAndGenotypeFrequencies(VariantStats vcfStat, int totalAllelesCount, int totalGenotypesCount) {
//        String mgfGenotype = "";
//
//        float maf = Float.MAX_VALUE;
//        float mgf = Float.MAX_VALUE;
//        float currentGtFreq;
//
//        float[] allelesFreq = new float[vcfStat.getNumAlleles()];
//        float[] genotypesFreq = new float[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];
//
//        // MAF
//        for (int i = 0; i < vcfStat.getNumAlleles(); i++) {
//            allelesFreq[i] = (totalAllelesCount > 0) ? vcfStat.getAllelesCount()[i] / (float) totalAllelesCount : 0;
//            if (allelesFreq[i] < maf) {
//                maf = allelesFreq[i];
//                vcfStat.setMafAllele((i == 0) ? vcfStat.getRefAllele() : vcfStat.getAltAlleles()[i - 1]);
//            }
//        }
//
//        for (int i = 0; i < vcfStat.getNumAlleles() * vcfStat.getNumAlleles(); i++) {
//            genotypesFreq[i] = (totalGenotypesCount > 0) ? vcfStat.getGenotypesCount()[i] / (float) totalGenotypesCount : 0;
//        }
//
//        // MGF
//        for (int i = 0; i < vcfStat.getNumAlleles(); i++) {
//            for (int j = 0; j < vcfStat.getNumAlleles(); j++) {
//                int idx1 = i * vcfStat.getNumAlleles() + j;
//                if (i == j) {
//                    currentGtFreq = genotypesFreq[idx1];
//                } else {
//                    int idx2 = j * vcfStat.getNumAlleles() + i;
//                    currentGtFreq = genotypesFreq[idx1] + genotypesFreq[idx2];
//                }
//
//                if (currentGtFreq < mgf) {
//                    String firstAllele = (i == 0) ? vcfStat.getRefAllele() : vcfStat.getAltAlleles()[i - 1];
//                    String secondAllele = (j == 0) ? vcfStat.getRefAllele() : vcfStat.getAltAlleles()[j - 1];
//                    mgfGenotype = firstAllele + "/" + secondAllele;
//                    mgf = currentGtFreq;
//
//                }
//            }
//        }
//
//        vcfStat.setMaf(maf);
//        vcfStat.setMgf(mgf);
//        vcfStat.setMgfGenotype(mgfGenotype);
//
//        vcfStat.setAllelesFreq(allelesFreq);
//        vcfStat.setGenotypesFreq(genotypesFreq);
//    }

    private static void calculateTransitionsAndTransversions(VariantStats vcfStat, String reference, String alternate) {
        int transitionsCount = 0, transversionsCount = 0;

        if (reference.length() == 1 && alternate.length() == 1) {
            switch (reference.toUpperCase()) {
                case "C":
                    if (alternate.equalsIgnoreCase("T")) {
                        transitionsCount++;
                    } else {
                        transversionsCount++;
                    }
                    break;
                case "T":
                    if (alternate.equalsIgnoreCase("C")) {
                        transitionsCount++;
                    } else {
                        transversionsCount++;
                    }
                    break;
                case "A":
                    if (alternate.equalsIgnoreCase("G")) {
                        transitionsCount++;
                    } else {
                        transversionsCount++;
                    }
                    break;
                case "G":
                    if (alternate.equalsIgnoreCase("A")) {
                        transitionsCount++;
                    } else {
                        transversionsCount++;
                    }
                    break;
            }
        }

        vcfStat.setTransitionsCount(transitionsCount);
        vcfStat.setTransversionsCount(transversionsCount);
    }

}
