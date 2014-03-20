package org.opencb.biodata.tools.variant;

import java.util.*;
import org.opencb.biodata.models.feature.AllelesCode;
import static org.opencb.biodata.models.feature.AllelesCode.ALLELES_OK;
import static org.opencb.biodata.models.feature.AllelesCode.HAPLOID;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.feature.Genotypes;
import org.opencb.biodata.models.pedigree.Condition;
import org.opencb.biodata.models.pedigree.Individual;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.pedigree.Sex;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;
import org.opencb.biodata.models.variant.stats.VariantGroupStats;
import org.opencb.biodata.models.variant.stats.VariantHardyWeinbergStats;
import org.opencb.biodata.models.variant.stats.VariantSampleGroupStats;
import org.opencb.biodata.models.variant.stats.VariantSampleStats;

import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/26/13
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatsCalculator {


    public static List<VariantStats> variantStats(List<Variant> vcfRecordsList, List<String> sampleNames, Pedigree ped) {
        List<VariantStats> statList = new ArrayList<>(vcfRecordsList.size());

        for (Variant variant : vcfRecordsList) {
            int transitionsCount = 0, transversionsCount = 0;


            int genotypeCurrentPos;
            int totalAllelesCount = 0;
            int totalGenotypesCount = 0;
            String mgfGenotype = "";
            Individual ind;

            float maf = Float.MAX_VALUE;
            float mgf = Float.MAX_VALUE;
            float currentGtFreq;


            float controlsDominant = 0;
            float casesDominant = 0;
            float controlsRecessive = 0;
            float casesRecessive = 0;


            VariantStats vcfStat = new VariantStats();

            vcfStat.setChromosome(variant.getChromosome());
            vcfStat.setPosition((long) variant.getStart());
            vcfStat.setRefAllele(variant.getReference());
            vcfStat.setId(variant.getId());

//            String[] altAlleles = variant.getAltAlleles();
//
//            vcfStat.setAltAlleles(altAlleles);
//            vcfStat.setNumAlleles(altAlleles.length + 1);
            vcfStat.setAltAlleles(new String[] { variant.getAlternate() });
            vcfStat.setNumAlleles(1);

            int[] allelesCount = new int[vcfStat.getNumAlleles()];
            int[] genotypesCount = new int[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];
            float[] allelesFreq = new float[vcfStat.getNumAlleles()];
            float[] genotypesFreq = new float[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];

            vcfStat.setSamples(sampleNames.size());

            for (String sampleName : sampleNames) {

//                Genotype g = variant.getSampleGenotype(sampleName);
                Genotype g = new Genotype(variant.getSampleData(sampleName, "GT"));

                Genotypes.addGenotypeToList(vcfStat.getGenotypes(), g);

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
                            vcfStat.getHw().incNAA();
                        } else if ((g.isAllele1Ref() && g.getAllele2() == 1) || (g.getAllele1() == 1 && g.isAllele2Ref())) {  // 0|1, 1|0
                            vcfStat.getHw().incNAa();

                        } else if (g.getAllele1() == 1 && g.getAllele2() == 1) {
                            vcfStat.getHw().incNaa();
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
                        ind = ped.getIndividual(sampleName);
                        if (isMendelianError(ind, g, variant)) {
                            vcfStat.setMendelinanErrors(vcfStat.getMendelinanErrors() + 1);

                        }
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
                                if (!g.isAllele1Ref() && !g.isAllele2Ref() && g.getAllele1() == g.getAllele2()) {// 1|1, 2|2, and so on
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

            hardyWeinbergTest(vcfStat.getHw());


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
            if (variant.hasAttribute("FILTER") && variant.getAttribute("FILTER").toUpperCase().equals("PASS")) {
                vcfStat.setPass(true);
            }

            if (variant.hasAttribute("FILTER") && !variant.getAttribute("QUAL").equals(".")) {
                float qualAux = Float.valueOf(variant.getAttribute("QUAL"));
                if (qualAux >= 0) {
                    vcfStat.setQual(qualAux);
                }
            }

            // Once all samples have been traverse, calculate % that follow inheritance model
            controlsDominant = controlsDominant * 100 / (sampleNames.size() - vcfStat.getMissingGenotypes());
            casesDominant = casesDominant * 100 / (sampleNames.size() - vcfStat.getMissingGenotypes());
            controlsRecessive = controlsRecessive * 100 / (sampleNames.size() - vcfStat.getMissingGenotypes());
            casesRecessive = casesRecessive * 100 / (sampleNames.size() - vcfStat.getMissingGenotypes());


            vcfStat.setTransitionsCount(transitionsCount);
            vcfStat.setTransversionsCount(transversionsCount);

            vcfStat.setCasesPercentDominant(casesDominant);
            vcfStat.setControlsPercentDominant(controlsDominant);
            vcfStat.setCasesPercentRecessive(casesRecessive);
            vcfStat.setControlsPercentRecessive(controlsRecessive);


            statList.add(vcfStat);
        }

        calculateHardyWeinberChiSquareTest(statList);

//        samplesCount = sampleNames.size();

//        globalStats.updateStats(variantsCount, samplesCount, snpsCount, indelsCount,
//                passCount, transitionsCount, transversionsCount, biallelicsCount,
//                multiallelicsCount, accumQuality);

        return statList;
    }

    public static VariantGlobalStats globalStats(List<VariantStats> variantStatses) {

        VariantGlobalStats gs = new VariantGlobalStats();

        gs.setVariantsCount(variantStatses.size());
        for (VariantStats vs : variantStatses) {
            gs.setSamplesCount(vs.getSamples());
            if (vs.isIndel()) {
                gs.addIndel();
            }
            if (vs.isSNP()) {
                gs.addSNP();
            }
            if (vs.isPass()) {
                gs.addPass();
            }

            if (vs.getNumAlleles() > 2) {
                gs.addMultiallelic();
            } else if (vs.getNumAlleles() > 1) {
                gs.addBiallelic();
            }

            gs.addTransitions(vs.getTransitionsCount());
            gs.addTransversions(vs.getTransversionsCount());
            gs.addAccumQuality(vs.getQual());
        }

        return gs;

    }

    public static VariantGroupStats groupStats(List<Variant> variants, Pedigree ped, String group) {

        Set<String> groupValues = getGroupValues(ped, group);
        List<String> sampleList;
        VariantGroupStats groupStats = null;
        List<VariantStats> variantStatses;

        VariantGlobalStats variantGlobalStats = new VariantGlobalStats();

        if (groupValues != null) {
            groupStats = new VariantGroupStats(group, groupValues);


            for (String val : groupValues) {
                sampleList = getSamplesValueGroup(val, group, ped);
                variantStatses = variantStats(variants, sampleList, ped);
                groupStats.getVariantStats().put(val, variantStatses);
            }

        }
        return groupStats;
    }

    public static VariantSampleStats sampleStats(List<Variant> variants, List<String> sampleNames, Pedigree ped) {

        Genotype g;
        Individual ind;
        VariantSampleStats variantSampleStats = new VariantSampleStats(sampleNames);

        for (Variant record : variants) {

            for (String sample : sampleNames) {


//                g = record.getSampleGenotype(sample);
                g = new Genotype(record.getSampleData(sample, "GT"));

                // Find the missing alleles
                if (g.getCode() != AllelesCode.ALLELES_OK) {                   // Missing genotype (one or both alleles missing)

                    variantSampleStats.incrementMissingGenotypes(sample);
                }
                // Check mendelian errors
                if (ped != null) {
                    ind = ped.getIndividual(sample);
                    if (g.getCode() == AllelesCode.ALLELES_OK && isMendelianError(ind, g, record)) {
                        variantSampleStats.incrementMendelianErrors(sample);

                    }


                }
                //Count homozygotes
                if (g.getAllele1() == g.getAllele2()) {
                    variantSampleStats.incrementHomozygotesNumber(sample);
                }
            }
        }
        return variantSampleStats;
    }

    public static VariantSampleGroupStats sampleGroupStats(List<Variant> batch, Pedigree ped, String group) {


        VariantSampleGroupStats variantSampleGroupStats = new VariantSampleGroupStats();

        Set<String> groupValues = getGroupValues(ped, group);
        VariantSampleStats variantSampleStats;

        List<String> sampleList;

        if (variantSampleGroupStats.getGroup() == null) {
            variantSampleGroupStats.setGroup(group);
        }

        if (variantSampleGroupStats.getSampleStats().isEmpty()) {
            for (String groupVal : groupValues) {
                sampleList = getSamplesValueGroup(groupVal, group, ped);
                variantSampleStats = new VariantSampleStats(sampleList);

                variantSampleGroupStats.getSampleStats().put(groupVal, variantSampleStats);

            }


        }

        for (Map.Entry<String, VariantSampleStats> entry : variantSampleGroupStats.getSampleStats().entrySet()) {
            sampleList = getSamplesValueGroup(entry.getKey(), group, ped);
            entry.setValue(sampleStats(batch, sampleList, ped));
//            variantSampleStats = entry.getValue();
//            sampleStats(batch, sampleList, ped);
        }
        return variantSampleGroupStats;
    }

    private static List<String> getSamplesValueGroup(String val, String group, Pedigree ped) {
        List<String> list = new ArrayList<>(100);
        Individual ind;
        for (Map.Entry<String, Individual> entry : ped.getIndividuals().entrySet()) {
            ind = entry.getValue();
            if (group.toLowerCase().equals("phenotype")) {
                if (ind.getPhenotype().equals(val)) {
                    list.add(ind.getId());
                }
            } else if (group.toLowerCase().equals("family")) {
                if (ind.getFamily().equals(val)) {
                    list.add(ind.getId());
                }
            }
        }

        return list;
    }

    private static Set<String> getGroupValues(Pedigree ped, String group) {
        Set<String> values = new TreeSet<>();
        Individual ind;
        for (Map.Entry<String, Individual> entry : ped.getIndividuals().entrySet()) {
            ind = entry.getValue();
            if (group.toLowerCase().equals("phenotype")) {
                values.add(ind.getPhenotype());
            } else if (group.toLowerCase().equals("family")) {
                values.add(ind.getFamily());
            }


        }
        return values;
    }

    private static boolean isMendelianError(Individual ind, Genotype g, Variant variant) {
        Genotype gFather;
        Genotype gMother;

        if (ind.getFather() == null || ind.getMother() == null) {
            return false;
        }

//        gFather = variant.getSampleGenotype(ind.getFather().getId());
//        gMother = variant.getSampleGenotype(ind.getMother().getId());
        gFather = new Genotype(variant.getSampleData(ind.getFather().getId(), "GT"));
        gMother = new Genotype(variant.getSampleData(ind.getMother().getId(), "GT"));

        if (gFather.getCode() != AllelesCode.ALLELES_OK || gMother.getCode() != AllelesCode.ALLELES_OK) {
            return false;
        }

        return checkMendel(variant.getChromosome(), gFather, gMother, g, ind.getSexCode()) > 0;
    }

    private static int checkMendel(String chromosome, Genotype gFather, Genotype gMother, Genotype gInd, Sex sex) {

        // Ignore if any allele is missing
        if (gFather.getAllele1() < 0 ||
                gFather.getAllele2() < 0 ||
                gMother.getAllele1() < 0 ||
                gMother.getAllele2() < 0 ||
                gInd.getAllele1() < 0 ||
                gInd.getAllele2() < 0) {
            return -1;
        }


        // Ignore haploid chromosomes
        if (chromosome.toUpperCase().equals("Y") || chromosome.toUpperCase().equals("MT")) {
            return -2;
        }

        int mendelType = 0;

        if (!chromosome.toUpperCase().equals("X") || sex == Sex.FEMALE) {
            if ((!gInd.isAllele1Ref() && gInd.isAllele2Ref()) ||
                    (gInd.isAllele1Ref() && !gInd.isAllele2Ref())) {
                // KID = 01/10
                // 00x00 -> 01  (m1)
                // 11x11 -> 01  (m2)
                if ((gFather.isAllele1Ref() && gFather.isAllele2Ref()) &&
                        (gMother.isAllele1Ref() && gMother.isAllele2Ref())) {
                    mendelType = 1;
                } else if ((!gFather.isAllele1Ref() && !gFather.isAllele2Ref()) &&
                        (!gMother.isAllele1Ref() && !gMother.isAllele2Ref())) {
                    mendelType = 2;
                }
            } else if (gInd.isAllele1Ref() && gInd.isAllele2Ref()) {
                // KID = 00
                // 00x11 -> 00 (m3) P11->00
                // 01x11 -> 00 (m3)
                // ??x11 -> 00 (m3)

                // 11x00 -> 00 (m4) M11->00
                // 11x01 -> 00 (m4)
                // 11x?? -> 00 (m4)

                // 11x11 -> 00 (m5) P11+M11->00

                // Hom parent can't breed opposite hom child

                // rule = at least one '11' parent
                if ((!gFather.isAllele1Ref() && !gFather.isAllele2Ref()) ||
                        !gMother.isAllele1Ref() && !gMother.isAllele2Ref()) {

                    if (!gFather.isAllele1Ref() && !gFather.isAllele2Ref() &&
                            !gMother.isAllele1Ref() && !gMother.isAllele2Ref()
                            ) {
                        mendelType = 5;
                    } else if (!gFather.isAllele1Ref() && !gFather.isAllele2Ref()) {
                        mendelType = 4;
                    } else {
                        mendelType = 3;
                    }


                }
            } else {
                // KID = 11

                // 00x01 -> 11 (m6)
                // 00x11 -> 11
                // 00x?? -> 11

                // 01x00 -> 11 (m7)
                // 11x00 -> 11
                // ??x00 -> 11

                // 00x00 -> 11 (m8) P00+M00->11

                // rule = at least one '00' parent

                if ((gFather.isAllele1Ref() && gFather.isAllele2Ref()) ||
                        (gMother.isAllele1Ref() && gMother.isAllele2Ref())
                        ) {
                    if (gFather.isAllele1Ref() && gFather.isAllele2Ref() &&
                            gMother.isAllele1Ref() && gMother.isAllele2Ref()) {
                        mendelType = 8;
                    } else if (gFather.isAllele1Ref() && gFather.isAllele2Ref()) {
                        mendelType = 6;

                    } else {
                        mendelType = 7;
                    }
                }

            }


        } else {
            // Chromosome X in inherited only from the mother and it is haploid
            if (!gInd.isAllele1Ref() && gMother.isAllele1Ref() && gMother.isAllele2Ref()) {
                mendelType = 9;
            }
            if (gInd.isAllele1Ref() && !gMother.isAllele1Ref() && !gMother.isAllele2Ref()) {
                mendelType = 10;
            }

        }


        return mendelType;
    }

    private static void calculateHardyWeinberChiSquareTest(List<VariantStats> statList) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private static void hardyWeinbergTest(VariantHardyWeinbergStats hw) {

        hw.setN(hw.getN_AA() + hw.getN_Aa() + hw.getN_aa());
        int n = hw.getN();
        int n_AA = hw.getN_AA();
        int n_Aa = hw.getN_Aa();
        int n_aa = hw.getN_aa();

//        ChiSquareTest chiSquareTest = new ChiSquareTest();

        float chi, pValue;


        if (n > 0) {
            float p = (float) ((2.0 * n_AA + n_Aa) / (2 * n));
            float q = 1 - p;

            hw.setP(p);
            hw.setQ(q);

            hw.setE_AA(p * p * n);
            hw.setE_Aa(2 * p * q * n);
            hw.setE_aa(q * q * n);

            if (hw.getE_AA() == n_AA) {
                n_AA = 1;
                hw.setE_AA(n_AA);
            }

            if (hw.getE_Aa() == n_Aa) {
                n_Aa = 1;
                hw.setE_Aa(n_Aa);
            }

            if (hw.getE_aa() == n_aa) {
                n_aa = 1;
                hw.setE_aa(n_aa);
            }


            chi = (n_AA - hw.getE_AA()) * (n_AA - hw.getE_AA()) / hw.getE_AA()
                    + (n_Aa - hw.getE_Aa()) * (n_Aa - hw.getE_Aa()) / hw.getE_Aa()
                    + (n_aa - hw.getE_aa()) * (n_aa - hw.getE_aa()) / hw.getE_aa();

//            pValue = chiSquareTest.chiSquare(chi, 1);
            hw.setChi2(chi);

            //hw->p_value = 1-gsl_cdf_chisq_P(hw->chi2,1);


        }


    }


}
