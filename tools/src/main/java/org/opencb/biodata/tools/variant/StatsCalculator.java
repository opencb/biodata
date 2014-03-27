package org.opencb.biodata.tools.variant;

import java.util.*;
import org.opencb.biodata.models.feature.AllelesCode;
import static org.opencb.biodata.models.feature.AllelesCode.ALLELES_OK;
import static org.opencb.biodata.models.feature.AllelesCode.HAPLOID;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.pedigree.Condition;
import org.opencb.biodata.models.pedigree.Individual;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.pedigree.Sex;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;
import org.opencb.biodata.models.variant.stats.VariantGroupStats;
import org.opencb.biodata.models.variant.stats.VariantHardyWeinbergStats;
import org.opencb.biodata.models.variant.stats.VariantSampleGroupStats;
import org.opencb.biodata.models.variant.stats.VariantSampleStats;

import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class StatsCalculator {

    public static List<VariantStats> variantStats(List<Variant> variants, Pedigree ped) {
        List<VariantStats> statList = new ArrayList<>(variants.size());

        for (Variant variant : variants) {
            for (ArchivedVariantFile file : variant.getFiles().values()) {
                int transitionsCount = 0, transversionsCount = 0;

                int genotypeCurrentPos;
                int totalAllelesCount = 0;
                int totalGenotypesCount = 0;
                String mgfGenotype = "";

                float maf = Float.MAX_VALUE;
                float mgf = Float.MAX_VALUE;
                float currentGtFreq;

                float controlsDominant = 0;
                float casesDominant = 0;
                float controlsRecessive = 0;
                float casesRecessive = 0;

                
                VariantStats vcfStat = new VariantStats(variant);

                int[] allelesCount = new int[vcfStat.getNumAlleles()];
                int[] genotypesCount = new int[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];
                float[] allelesFreq = new float[vcfStat.getNumAlleles()];
                float[] genotypesFreq = new float[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];

                vcfStat.setNumSamples(file.getSampleNames().size());
                
                for (Map.Entry<String, Map<String, String>> sample : file.getSamplesData().entrySet()) {
                    String sampleName = sample.getKey();
                    Genotype g = new Genotype(sample.getValue().get("GT"));
                    vcfStat.addGenotype(g);

                    // Check missing alleles and genotypes
                    switch (g.getCode()) {
                        case ALLELES_OK:
                            // Both alleles set
                            genotypeCurrentPos = g.getAllele1() * (vcfStat.getNumAlleles()) + g.getAllele2();

                            allelesCount[g.getAllele1()]++;
                            allelesCount[g.getAllele2()]++;
                            genotypesCount[genotypeCurrentPos]++;

                            totalAllelesCount += 2;
                            totalGenotypesCount++;

                            // Counting genotypes for Hardy-Weinberg (all phenotypes)

                            if (g.isAllele1Ref() && g.isAllele2Ref()) { // 0|0
                                vcfStat.getHw().incN_AA();
                            } else if ((g.isAllele1Ref() && g.getAllele2() == 1) || (g.getAllele1() == 1 && g.isAllele2Ref())) {  // 0|1, 1|0
                                vcfStat.getHw().incN_Aa();

                            } else if (g.getAllele1() == 1 && g.getAllele2() == 1) {
                                vcfStat.getHw().incN_aa();
                            }

                            break;
                        case HAPLOID:
                            // Haploid (chromosome X/Y)
                            try {
                                allelesCount[g.getAllele1()]++;
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.println("vcfRecord = " + variant);
                                System.out.println("g = " + g);
                            }
                            totalAllelesCount++;
                            break;
                        default:
                            // Missing genotype (one or both alleles missing)
                            vcfStat.setMissingGenotypes(vcfStat.getMissingGenotypes() + 1);
                            if (g.getAllele1() == null) {
                                vcfStat.setMissingAlleles(vcfStat.getMissingAlleles() + 1);
                            } else {
                                allelesCount[g.getAllele1()]++;
                                totalAllelesCount++;
                            }

                            if (g.getAllele2() == null) {
                                vcfStat.setMissingAlleles(vcfStat.getMissingAlleles() + 1);
                            } else {
                                allelesCount[g.getAllele2()]++;
                                totalAllelesCount++;
                            }
                            break;

                    }

                    // Include statistics that depend on pedigree information
                    if (ped != null) {
                        if (g.getCode() == AllelesCode.ALLELES_OK || g.getCode() == AllelesCode.HAPLOID) {
                            Individual ind = ped.getIndividual(sampleName);
//                            if (isMendelianError(ind, g, variant, file)) {
//                                vcfStat.setMendelinanErrors(vcfStat.getMendelinanErrors() + 1);
//
//                            }
                            if (g.getCode() == AllelesCode.ALLELES_OK) {
                                // Check inheritance models
                                if (ind.getCondition() == Condition.UNAFFECTED) {
                                    if (g.isAllele1Ref() && g.isAllele2Ref()) { // 0|0
                                        controlsDominant++;
                                        controlsRecessive++;

                                    } else if ((g.isAllele1Ref() && !g.isAllele2Ref()) || (!g.isAllele1Ref() || g.isAllele2Ref())) { // 0|1 or 1|0
                                        controlsRecessive++;

                                    }
                                } else if (ind.getCondition() == Condition.AFFECTED) {
                                    if (!g.isAllele1Ref() && !g.isAllele2Ref() && g.getAllele1().equals(g.getAllele2())) {// 1|1, 2|2, and so on
                                        casesRecessive++;
                                        casesDominant++;
                                    } else if (!g.isAllele1Ref() || !g.isAllele2Ref()) { // 0|1, 1|0, 1|2, 2|1, 1|3, and so on
                                        casesDominant++;

                                    }
                                }

                            }

                        }
                    }

                }  // Finish all samples loop


                // MAF
                for (int i = 0; i < vcfStat.getNumAlleles(); i++) {
                    allelesFreq[i] = (totalAllelesCount > 0) ? allelesCount[i] / (float) totalAllelesCount : 0;
                    if (allelesFreq[i] < maf) {
                        maf = allelesFreq[i];
                        vcfStat.setMafAllele((i == 0) ? vcfStat.getRefAlleles() : vcfStat.getAltAlleles()[i - 1]);
                    }
                }

                vcfStat.setMaf(maf);

                for (int i = 0; i < vcfStat.getNumAlleles() * vcfStat.getNumAlleles(); i++) {
                    genotypesFreq[i] = (totalGenotypesCount > 0) ? genotypesCount[i] / (float) totalGenotypesCount : 0;


                }


                // MGF
                for (int i = 0; i < vcfStat.getNumAlleles(); i++) {
                    for (int j = 0; j < vcfStat.getNumAlleles(); j++) {
                        int idx1 = i * vcfStat.getNumAlleles() + j;
                        if (i == j) {
                            currentGtFreq = genotypesFreq[idx1];
                        } else {
                            int idx2 = j * vcfStat.getNumAlleles() + i;
                            currentGtFreq = genotypesFreq[idx1] + genotypesFreq[idx2];
                        }

                        if (currentGtFreq < mgf) {
                            String firstAllele = (i == 0) ? vcfStat.getRefAlleles() : vcfStat.getAltAlleles()[i - 1];
                            String secondAllele = (j == 0) ? vcfStat.getRefAlleles() : vcfStat.getAltAlleles()[j - 1];
                            mgfGenotype = firstAllele + "|" + secondAllele;
                            mgf = currentGtFreq;

                        }
                    }
                }

                vcfStat.setMgf(mgf);
                vcfStat.setMgfAllele(mgfGenotype);

                vcfStat.setAllelesCount(allelesCount);
                vcfStat.setGenotypesCount(genotypesCount);

                vcfStat.setAllelesFreq(allelesFreq);
                vcfStat.setGenotypesFreq(genotypesFreq);

                vcfStat.getHw().calculate();

                // INDELS
                /*
                 * 3 possibilities for being an INDEL:
                 * - The value of the ALT field is <DEL> or <INS>
                 * - The REF allele is not . but the ALT is
                 * - The REF allele is . but the ALT is not
                 * - The REF field length is different than the ALT field length
                 */
                if ((!vcfStat.getRefAlleles().equals(".") && variant.getAlternate().equals(".")) ||
                        (variant.getAlternate().equals(".") && !vcfStat.getRefAlleles().equals(".")) ||
                        (variant.getAlternate().equals("<INS>")) ||
                        (variant.getAlternate().equals("<DEL>")) ||
                        variant.getReference().length() != variant.getAlternate().length()) {
                    vcfStat.setIndel(true);
                } else {
                    vcfStat.setIndel(false);
                }

                // Transitions and transversions

                String ref = variant.getReference().toUpperCase();
                String alt = variant.getAlternate().toUpperCase();

                if (ref.length() == 1 && alt.length() == 1) {
                    switch (ref) {
                        case "C":
                            if (alt.equals("T")) {
                                transitionsCount++;
                            } else {
                                transversionsCount++;
                            }
                            break;
                        case "T":
                            if (alt.equals("C")) {
                                transitionsCount++;
                            } else {
                                transversionsCount++;
                            }
                            break;
                        case "A":
                            if (alt.equals("G")) {
                                transitionsCount++;

                            } else {
                                transversionsCount++;
                            }
                            break;
                        case "G":
                            if (alt.equals("A")) {
                                transitionsCount++;
                            } else {
                                transversionsCount++;
                            }
                            break;
                    }
                }

                // Update variables finally used to update file_stats_t structure
                if (variant.getId() != null && !variant.getId().equals(".")) {
                    vcfStat.setSNP(true);
                }
                if (file.hasAttribute("FILTER") && "PASS".equalsIgnoreCase(file.getAttribute("FILTER"))) {
                    vcfStat.setPass(true);
                }

                if (file.hasAttribute("QUAL") && !(".").equals(file.getAttribute("QUAL"))) {
                    float qualAux = Float.valueOf(file.getAttribute("QUAL"));
                    if (qualAux >= 0) {
                        vcfStat.setQual(qualAux);
                    }
                }

                // Once all samples have been traversed, calculate % that follow inheritance model
                controlsDominant = controlsDominant * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
                casesDominant = casesDominant * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
                controlsRecessive = controlsRecessive * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
                casesRecessive = casesRecessive * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());


                vcfStat.setTransitionsCount(transitionsCount);
                vcfStat.setTransversionsCount(transversionsCount);

                vcfStat.setCasesPercentDominant(casesDominant);
                vcfStat.setControlsPercentDominant(controlsDominant);
                vcfStat.setCasesPercentRecessive(casesRecessive);
                vcfStat.setControlsPercentRecessive(controlsRecessive);


                statList.add(vcfStat);
                file.setStats(vcfStat); // TODO Correct?
            }
        }

        return statList;
    }

//    public static VariantGroupStats groupStats(List<Variant> variants, Pedigree ped, String group) {
//        Set<String> groupValues = getGroupValues(ped, group);
//        List<String> sampleList;
//        VariantGroupStats groupStats = null;
//
//        if (groupValues != null) {
//            groupStats = new VariantGroupStats(group, groupValues);
//
//            for (String val : groupValues) {
//                sampleList = getSamplesValueGroup(val, group, ped);
//                List<VariantStats> variantStatses = variantStats(variants, ped);
//                groupStats.getVariantStats().put(val, variantStatses);
//            }
//
//        }
//        return groupStats;
//    }

//    public static VariantSampleGroupStats sampleGroupStats(List<Variant> batch, Pedigree ped, String group) {
//        VariantSampleGroupStats variantSampleGroupStats = new VariantSampleGroupStats();
//
//        Set<String> groupValues = getGroupValues(ped, group);
//        VariantSampleStats variantSampleStats;
//
//        List<String> sampleList;
//
//        if (variantSampleGroupStats.getGroup() == null) {
//            variantSampleGroupStats.setGroup(group);
//        }
//
//        if (variantSampleGroupStats.getSampleStats().isEmpty()) {
//            for (String groupVal : groupValues) {
//                sampleList = getSamplesValueGroup(groupVal, group, ped);
//                variantSampleStats = new VariantSampleStats(sampleList);
//                variantSampleGroupStats.getSampleStats().put(groupVal, variantSampleStats);
//            }
//        }
//
//        for (Map.Entry<String, VariantSampleStats> entry : variantSampleGroupStats.getSampleStats().entrySet()) {
//            sampleList = getSamplesValueGroup(entry.getKey(), group, ped);
//            entry.setValue(sampleStats(batch, sampleList, ped));
////            variantSampleStats = entry.getValue();
////            sampleStats(batch, sampleList, ped);
//        }
//        return variantSampleGroupStats;
//    }
//
//    private static List<String> getSamplesValueGroup(String val, String group, Pedigree ped) {
//        List<String> list = new ArrayList<>(100);
//        Individual ind;
//        for (Map.Entry<String, Individual> entry : ped.getIndividuals().entrySet()) {
//            ind = entry.getValue();
//            if (group.toLowerCase().equals("phenotype")) {
//                if (ind.getPhenotype().equals(val)) {
//                    list.add(ind.getId());
//                }
//            } else if (group.toLowerCase().equals("family")) {
//                if (ind.getFamily().equals(val)) {
//                    list.add(ind.getId());
//                }
//            }
//        }
//
//        return list;
//    }
//
//    private static Set<String> getGroupValues(Pedigree ped, String group) {
//        Set<String> values = new TreeSet<>();
//        Individual ind;
//        for (Map.Entry<String, Individual> entry : ped.getIndividuals().entrySet()) {
//            ind = entry.getValue();
//            if (group.toLowerCase().equals("phenotype")) {
//                values.add(ind.getPhenotype());
//            } else if (group.toLowerCase().equals("family")) {
//                values.add(ind.getFamily());
//            }
//        }
//        
//        return values;
//    }
//
//    private static boolean isMendelianError(Individual ind, Genotype g, Variant variant, ArchivedVariantFile file) {
//        Genotype gFather;
//        Genotype gMother;
//
//        if (ind.getFather() == null || ind.getMother() == null) {
//            return false;
//        }
//
//        gFather = new Genotype(file.getSampleData(ind.getFather().getId(), "GT"));
//        gMother = new Genotype(file.getSampleData(ind.getMother().getId(), "GT"));
//
//        if (gFather.getCode() != AllelesCode.ALLELES_OK || gMother.getCode() != AllelesCode.ALLELES_OK) {
//            return false;
//        }
//
//        return checkMendel(variant.getChromosome(), gFather, gMother, g, ind.getSexCode()) > 0;
//    }
//
//    private static int checkMendel(String chromosome, Genotype gFather, Genotype gMother, Genotype gInd, Sex sex) {
//        // Ignore if any allele is missing
//        if (gFather.getAllele1() < 0 ||
//                gFather.getAllele2() < 0 ||
//                gMother.getAllele1() < 0 ||
//                gMother.getAllele2() < 0 ||
//                gInd.getAllele1() < 0 ||
//                gInd.getAllele2() < 0) {
//            return -1;
//        }
//
//        // Ignore haploid chromosomes
//        if (chromosome.toUpperCase().equals("Y") || chromosome.toUpperCase().equals("MT")) {
//            return -2;
//        }
//
//        int mendelType = 0;
//
//        if (!chromosome.toUpperCase().equals("X") || sex == Sex.FEMALE) {
//            if ((!gInd.isAllele1Ref() && gInd.isAllele2Ref()) ||
//                    (gInd.isAllele1Ref() && !gInd.isAllele2Ref())) {
//                // KID = 01/10
//                // 00x00 -> 01  (m1)
//                // 11x11 -> 01  (m2)
//                if ((gFather.isAllele1Ref() && gFather.isAllele2Ref()) &&
//                        (gMother.isAllele1Ref() && gMother.isAllele2Ref())) {
//                    mendelType = 1;
//                } else if ((!gFather.isAllele1Ref() && !gFather.isAllele2Ref()) &&
//                        (!gMother.isAllele1Ref() && !gMother.isAllele2Ref())) {
//                    mendelType = 2;
//                }
//            } else if (gInd.isAllele1Ref() && gInd.isAllele2Ref()) {
//                // KID = 00
//                // 00x11 -> 00 (m3) P11->00
//                // 01x11 -> 00 (m3)
//                // ??x11 -> 00 (m3)
//
//                // 11x00 -> 00 (m4) M11->00
//                // 11x01 -> 00 (m4)
//                // 11x?? -> 00 (m4)
//
//                // 11x11 -> 00 (m5) P11+M11->00
//
//                // Hom parent can't breed opposite hom child
//
//                // rule = at least one '11' parent
//                if ((!gFather.isAllele1Ref() && !gFather.isAllele2Ref()) ||
//                        !gMother.isAllele1Ref() && !gMother.isAllele2Ref()) {
//
//                    if (!gFather.isAllele1Ref() && !gFather.isAllele2Ref() &&
//                            !gMother.isAllele1Ref() && !gMother.isAllele2Ref()
//                            ) {
//                        mendelType = 5;
//                    } else if (!gFather.isAllele1Ref() && !gFather.isAllele2Ref()) {
//                        mendelType = 4;
//                    } else {
//                        mendelType = 3;
//                    }
//
//
//                }
//            } else {
//                // KID = 11
//
//                // 00x01 -> 11 (m6)
//                // 00x11 -> 11
//                // 00x?? -> 11
//
//                // 01x00 -> 11 (m7)
//                // 11x00 -> 11
//                // ??x00 -> 11
//
//                // 00x00 -> 11 (m8) P00+M00->11
//
//                // rule = at least one '00' parent
//
//                if ((gFather.isAllele1Ref() && gFather.isAllele2Ref()) ||
//                        (gMother.isAllele1Ref() && gMother.isAllele2Ref())
//                        ) {
//                    if (gFather.isAllele1Ref() && gFather.isAllele2Ref() &&
//                            gMother.isAllele1Ref() && gMother.isAllele2Ref()) {
//                        mendelType = 8;
//                    } else if (gFather.isAllele1Ref() && gFather.isAllele2Ref()) {
//                        mendelType = 6;
//
//                    } else {
//                        mendelType = 7;
//                    }
//                }
//
//            }
//
//
//        } else {
//            // Chromosome X in inherited only from the mother and it is haploid
//            if (!gInd.isAllele1Ref() && gMother.isAllele1Ref() && gMother.isAllele2Ref()) {
//                mendelType = 9;
//            }
//            if (gInd.isAllele1Ref() && !gMother.isAllele1Ref() && !gMother.isAllele2Ref()) {
//                mendelType = 10;
//            }
//        }
//
//        return mendelType;
//    }

}
