package org.opencb.biodata.tools.pedigree;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.biodata.models.core.pedigree.PedigreeManager;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;

import java.util.*;
import java.util.stream.Collectors;

public class ModeOfInheritance {

    public static final int GENOTYPE_0_0 = 0;
    public static final int GENOTYPE_0_1 = 1;
    public static final int GENOTYPE_1_1 = 2;

    public static final int GENOTYPE_0 = 3;
    public static final int GENOTYPE_1 = 4;


    public static Map<String, List<String>> dominant(Pedigree pedigree, Phenotype phenotype, boolean incompletePenetrance) {
        PedigreeManager pedigreeManager = new PedigreeManager(pedigree);

        // Get affected individuals for that phenotype
        Set<Individual> affectedIndividuals = pedigreeManager.getAffectedIndividuals(phenotype);

        // Get all possible genotypes for each individual
        Map<String, Set<Integer>> genotypes = new HashMap<>();
        for (Individual individual : pedigree.getMembers()) {
            genotypes.put(individual.getId(), calculateDominant(affectedIndividuals.contains(individual), incompletePenetrance));
        }

        // Validate genotypes using relationships
        validateGenotypes(genotypes, pedigreeManager);

        if (!isValidModeOfInheritance(genotypes, pedigree, affectedIndividuals)) {
            return null;
        }

        // Return a readable output, i.e., returning "0/0, "0/1", "1/1"
        return prepareOutput(genotypes);
    }

    public static Map<String, List<String>> recessive(Pedigree pedigree, Phenotype phenotype, boolean incompletePenetrance) {
        PedigreeManager pedigreeManager = new PedigreeManager(pedigree);

        // Get affected individuals for that phenotype
        Set<Individual> affectedIndividuals = pedigreeManager.getAffectedIndividuals(phenotype);

        // Get all possible genotypes for each individual
        Map<String, Set<Integer>> genotypes = new HashMap<>();
        for (Individual individual : pedigree.getMembers()) {
            genotypes.put(individual.getId(), calculateRecessive(affectedIndividuals.contains(individual), incompletePenetrance));
        }

        // Validate genotypes using relationships
        validateGenotypes(genotypes, pedigreeManager);

        if (!isValidModeOfInheritance(genotypes, pedigree, affectedIndividuals)) {
            return null;
        }

        // Return a readable output, i.e., returning "0/0, "0/1", "1/1"
        return prepareOutput(genotypes);
    }

    public static Map<String, List<String>> xLinked(Pedigree pedigree, Phenotype phenotype, boolean isDominant) {
        PedigreeManager pedigreeManager = new PedigreeManager(pedigree);

        // Get affected individuals for that phenotype
        Set<Individual> affectedIndividuals = pedigreeManager.getAffectedIndividuals(phenotype);

        // Get all possible genotypes for each individual
        Map<String, Set<Integer>> genotypes = new HashMap<>();

        for (Individual individual : pedigree.getMembers()) {
            if (affectedIndividuals.contains(individual)) {
                if (individual.getSex() == Individual.Sex.MALE) {
                    Set<Integer> genotype = new HashSet<>();
                    genotype.add(GENOTYPE_1);
                    genotypes.put(individual.getId(), genotype);
                } else {
                    // Female
                    Set<Integer> genotype = new HashSet<>();
                    if (isDominant) {
                        genotype.add(GENOTYPE_0_1);
                    }
                    genotype.add(GENOTYPE_1_1);
                    genotypes.put(individual.getId(), genotype);
                }
            } else {
                if (individual.getSex() == Individual.Sex.MALE) {
                    Set<Integer> genotype = new HashSet<>();
                    genotype.add(GENOTYPE_0);
                    genotypes.put(individual.getId(), genotype);
                } else {
                    Set<Integer> genotype = new HashSet<>();
                    genotype.add(GENOTYPE_0_0);
                    genotype.add(GENOTYPE_0_1);
                    genotypes.put(individual.getId(), genotype);
                }
            }
        }

        // Validate genotypes using relationships
        validateGenotypes(genotypes, pedigreeManager);

        if (!isValidModeOfInheritance(genotypes, pedigree, affectedIndividuals)) {
            return null;
        }

        // Return a readable output
        return prepareOutput(genotypes);
    }

    public static Map<String, List<String>> yLinked(Pedigree pedigree, Phenotype phenotype) {
        PedigreeManager pedigreeManager = new PedigreeManager(pedigree);

        // Get affected individuals for that phenotype
        Set<Individual> affectedIndividuals = pedigreeManager.getAffectedIndividuals(phenotype);

        // Get all possible genotypes for each individual
        Map<String, Set<Integer>> genotypes = new HashMap<>();

        for (Individual individual : pedigree.getMembers()) {
            if (affectedIndividuals.contains(individual)) {
                if (individual.getSex() == Individual.Sex.MALE) {
                    Set<Integer> genotype = new HashSet<>();
                    genotype.add(GENOTYPE_1);
                    genotypes.put(individual.getId(), genotype);
                } else {
                    // Found affected female!!??
                    return null;
                }
            } else {
                if (individual.getSex() == Individual.Sex.MALE) {
                    Set<Integer> genotype = new HashSet<>();
                    genotype.add(GENOTYPE_0);
                    genotypes.put(individual.getId(), genotype);
                } else {
                    genotypes.put(individual.getId(), new HashSet<>());
                }
            }
        }

        // Check for impossible situations
        Queue<Individual> queue = new LinkedList<>(pedigreeManager.getWithoutChildren());
        while (!queue.isEmpty()) {
            Individual child = queue.remove();

            if (child.getSex() == Individual.Sex.MALE && child.getFather() != null && StringUtils.isNotEmpty(child.getFather().getId())) {
                // Both or none of them should be affected
                Set<Integer> childGenotypes = genotypes.get(child.getId());
                Set<Integer> fatherGenotypes = genotypes.get(child.getFather().getId());

                if (!childGenotypes.containsAll(fatherGenotypes)) {
                    // Father and son have different genotypes, which shouldn't be possible
                    return null;
                }
            }

            if (child.getFather() != null && StringUtils.isNotEmpty(child.getFather().getId())) {
                queue.add(child.getFather());
            }
            if (child.getMother() != null && StringUtils.isNotEmpty(child.getMother().getId())) {
                queue.add(child.getMother());
            }
        }

        // Return a readable output, i.e., returning "-", "0", "1"
        return prepareOutput(genotypes);
    }

    public static List<Variant> compoundHeterozygosity(Pedigree pedigree, Iterator<Variant> variantIterator) throws Exception {
        Individual child = pedigree.getProband();

        if (child == null || StringUtils.isEmpty(child.getId())) {
            throw new Exception("Missing proband in pedigree");
        }

        if (child.getFather() == null) {
            throw new Exception("Missing father for " + child.getId());
        }
        if (child.getMother() == null) {
            throw new Exception("Missing mother for " + child.getId());
        }

        Individual father = child.getFather();
        Individual mother = child.getMother();

        // Here we will put all the variant ids that would match parents (0/0 0/1) -> child (0/1)
        List<Variant> fatherExplainedVariantList = new ArrayList<>();
        List<Variant> motherExplainedVariantList = new ArrayList<>();

        while (variantIterator.hasNext()) {
            Variant variant = variantIterator.next();

            // We assume the variant iterator will always contain information for one study
            StudyEntry study = variant.getStudies().get(0);

            Genotype childGt = new Genotype(study.getSampleData(child.getId(), "GT"));

            // Child is 0/1 or 0|1
            if (childGt.getAllelesIdx().length == 2 && ((childGt.getAllelesIdx()[0] == 0 && childGt.getAllelesIdx()[1] == 1)
                    || (childGt.getAllelesIdx()[0] == 1 && childGt.getAllelesIdx()[1] == 0))) {
                Genotype fatherGt = new Genotype(study.getSampleData(father.getId(), "GT"));
                if (fatherGt.getAllelesIdx().length == 2) {
                    if (fatherGt.getAllelesIdx()[0] == 0 && fatherGt.getAllelesIdx()[1] == 0) {
                        Genotype motherGt = new Genotype(study.getSampleData(mother.getId(), "GT"));
                        if (motherGt.getAllelesIdx().length == 2 && ((motherGt.getAllelesIdx()[0] == 0 && motherGt.getAllelesIdx()[1] ==
                                1) || (motherGt.getAllelesIdx()[0] == 1 && motherGt.getAllelesIdx()[1] == 0))) {
                            motherExplainedVariantList.add(variant);
                        }
                    } else if ((fatherGt.getAllelesIdx()[0] == 0 && fatherGt.getAllelesIdx()[1] == 1) || (fatherGt.getAllelesIdx()[0] ==
                            1 && fatherGt.getAllelesIdx()[1] == 0)) {
                        Genotype motherGt = new Genotype(study.getSampleData(mother.getId(), "GT"));
                        if (motherGt.getAllelesIdx().length == 2 && motherGt.getAllelesIdx()[0] == 0 && motherGt.getAllelesIdx()[1] == 0) {
                            fatherExplainedVariantList.add(variant);
                        }
                    }
                }
            }
        }

        if (!fatherExplainedVariantList.isEmpty() && !motherExplainedVariantList.isEmpty()) {
            List<Variant> variantList = new ArrayList<>(fatherExplainedVariantList.size() + motherExplainedVariantList.size());
            variantList.addAll(fatherExplainedVariantList);
            variantList.addAll(motherExplainedVariantList);
            return variantList;
        }

        // No variants support the model
        return Collections.emptyList();
    }

    /**
     * Get all the de novo variants identified.
     *
     * @param pedigree        Pedigree object.
     * @param variantIterator Variant iterator.
     * @return A map of variant - List of individuals containing a de novo variant.
     */
    public static Map<Variant, List<String>> alldeNovoVariants(Pedigree pedigree, Iterator<Variant> variantIterator) {
        PedigreeManager pedigreeManager = new PedigreeManager(pedigree);

        // We get all children so we can check upwards
        List<Individual> allChildren = pedigreeManager.getWithoutChildren();

        Map<Variant, List<String>> retDenovoVariants = new HashMap<>();

        while (variantIterator.hasNext()) {
            Variant variant = variantIterator.next();

            // List of individuals with de novo variants
            List<String> individualIds = new ArrayList<>();

            Queue<String> queue = new LinkedList<>();
            queue.addAll(allChildren.stream().map(Individual::getId).collect(Collectors.toList()));

            while (!queue.isEmpty()) {
                String individualId = queue.remove();
                Individual childIndividual = pedigreeManager.getIndividualMap().get(individualId);

                if (isDeNovoVariant(childIndividual, variant)) {
                    individualIds.add(individualId);
                }

                // Add parents to the queue
                if (childIndividual.getFather() != null) {
                    if (!queue.contains(childIndividual.getFather().getId())) {
                        queue.add(childIndividual.getFather().getId());
                    }
                }

                if (childIndividual.getMother() != null) {
                    if (!queue.contains(childIndividual.getMother().getId())) {
                        queue.add(childIndividual.getMother().getId());
                    }
                }
            }

            if (!individualIds.isEmpty()) {
                retDenovoVariants.put(variant, individualIds);
            }
        }

        return retDenovoVariants;
    }

    /**
     * Get all the de novo variants identified for the proband.
     *
     * @param individual      Child proband.
     * @param variantIterator Variant iterator.
     * @return A list of variants.
     */
    public static List<Variant> deNovoVariants(Individual individual, Iterator<Variant> variantIterator) {
        List<Variant> variantList = new ArrayList<>();

        while (variantIterator.hasNext()) {
            Variant variant = variantIterator.next();

            if (isDeNovoVariant(individual, variant)) {
                variantList.add(variant);
            }
        }

        return variantList;
    }

    /**
     * Method to check whether a variant is de novo.
     *
     * @param individual      Child proband.
     * @param variant         Variant to be checked.
     * @return a boolean indicating whether the variant is de novo.
     */
    private static boolean isDeNovoVariant(Individual individual, Variant variant) {
        // We assume the variant iterator will always contain information for one study
        StudyEntry study = variant.getStudies().get(0);

        Genotype childGt = new Genotype(study.getSampleData(individual.getId(), "GT"));

        int[] childAlleles = childGt.getAllelesIdx();
        if (childAlleles.length > 0) {
            // If the individual has parents

            if (individual.getFather() != null && StringUtils.isNotEmpty(individual.getFather().getId()) && individual.getMother() != null
                    && StringUtils.isNotEmpty(individual.getMother().getId())) {
                Genotype fatherGt = new Genotype(study.getSampleData(individual.getFather().getId(), "GT"));
                Genotype motherGt = new Genotype(study.getSampleData(individual.getMother().getId(), "GT"));

                int[] fatherAlleles = fatherGt.getAllelesIdx();
                int[] motherAlleles = motherGt.getAllelesIdx();

                if (fatherAlleles.length == 2 && motherAlleles.length == 2 && childAlleles.length == 2 && childAlleles[0] >= 0
                        && childAlleles[1] >= 0) { // ChildAlleles cannot be -1
                    Set<Integer> fatherAllelesSet = new HashSet<>();
                    for (int fatherAllele : fatherAlleles) {
                        fatherAllelesSet.add(fatherAllele);
                    }
                    Set<Integer> motherAllelesSet = new HashSet<>();
                    for (int motherAllele : motherAlleles) {
                        motherAllelesSet.add(motherAllele);
                    }

                    int allele1 = childAlleles[0];
                    int allele2 = childAlleles[1];
                    if (fatherAllelesSet.contains(allele1) && motherAllelesSet.contains(allele1)) {
                        // both parents have the same allele. We need to check for allele 2 in both parents as well
                        if (!fatherAllelesSet.contains(allele2) && !motherAllelesSet.contains(allele2) && !fatherAllelesSet.contains(-1)
                                && !motherAllelesSet.contains(-1)) {
                            // None of them have allele 2 -> de novo !
                            return true;
                        }
                    } else if (fatherAllelesSet.contains(allele2) && motherAllelesSet.contains(allele2)) {
                        // both parents have the same allele. We need to check for allele 1 in both parents as well
                        if (!fatherAllelesSet.contains(allele1) && !motherAllelesSet.contains(allele1) && !fatherAllelesSet.contains(-1)
                                && !motherAllelesSet.contains(-1)) {
                            // None of them have allele 2 -> de novo !
                            return true;
                        }
                    }
                    if (fatherAllelesSet.contains(allele1) && !motherAllelesSet.contains(-1)) {
                        // only the father has the same allele1
                        // None of them have allele 2 -> de novo !
                        return !motherAllelesSet.contains(allele2);
                    } else if (motherAllelesSet.contains(allele1) && !fatherAllelesSet.contains(-1)) {
                        // only the mother has the same allele1
                        // None of them have allele 2 -> de novo !
                        return !fatherAllelesSet.contains(allele2);
                    } else if (fatherAllelesSet.contains(allele2) && !motherAllelesSet.contains(-1)) {
                        // only the father has the same allele2
                        // None of them have allele 1 -> de novo !
                        return !motherAllelesSet.contains(allele1);
                    } else if (motherAllelesSet.contains(allele2) && !fatherAllelesSet.contains(-1)) {
                        // only the mother has the same allele2
                        // None of them have allele 1 -> de novo !
                        return !fatherAllelesSet.contains(allele1);
                    }

                }
            }
        }
        return false;
    }


    private static boolean isValidModeOfInheritance(Map<String, Set<Integer>> genotypes, Pedigree pedigree,
                                                    Set<Individual> affectedIndividuals) {
        for (Individual individual : pedigree.getMembers()) {
            if (individual.getMother() != null && individual.getFather() != null) {
                Set<Integer> childGenotypes = genotypes.get(individual.getId());
                Set<Integer> motherGenotypes = genotypes.get(individual.getMother().getId());
                Set<Integer> fatherGenotypes = genotypes.get(individual.getFather().getId());

                if (childGenotypes.isEmpty()) {
                    return false;
                }

                if (childGenotypes.size() == 1 && childGenotypes.contains(GENOTYPE_0_0)) {
                    if (affectedIndividuals.contains(individual)) {
                        return false;
                    }

                    if (motherGenotypes.size() == 1 && motherGenotypes.contains(GENOTYPE_1_1) && fatherGenotypes.size() == 1
                            && fatherGenotypes.contains(GENOTYPE_1_1)) {
                        return false;
                    }
                } else if (childGenotypes.size() == 1 && childGenotypes.contains(GENOTYPE_1_1)) {
                    if (!affectedIndividuals.contains(individual)) {
                        return false;
                    }

                    if (motherGenotypes.size() == 1 && motherGenotypes.contains(GENOTYPE_0_0) && fatherGenotypes.size() == 1
                            && fatherGenotypes.contains(GENOTYPE_0_0)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static Set<Integer> calculateDominant(boolean affected, boolean incompletePenetrance) {
        Set<Integer> gt = new HashSet<>();
        if (affected) {
            gt.add(GENOTYPE_0_1);
            gt.add(GENOTYPE_1_1);
        } else {
            gt.add(GENOTYPE_0_0);
            if (incompletePenetrance) {
                gt.add(GENOTYPE_0_1);
                gt.add(GENOTYPE_1_1);
            }
        }
        return gt;
    }

    private static Set<Integer> calculateRecessive(boolean affected, boolean incompletePenetrance) {
        Set<Integer> gt = new HashSet<>();
        if (affected) {
            gt.add(GENOTYPE_1_1);
        } else {
            gt.add(GENOTYPE_0_0);
            gt.add(GENOTYPE_0_1);
            if (incompletePenetrance) {
                gt.add(GENOTYPE_1_1);
            }
        }
        return gt;
    }

    /**
     * Validate and removes and genotypes that does not make sense given the parent - child relation.
     * This method should only be called under dominant, recessive and x-linked modes of inheritance. It does not support y-linked modes
     * where the mother does not have a possible genotype.
     *
     * @param gt              Map of individual id - set of possible genotypes.
     * @param pedigreeManager Pedigree manager.
     */
    private static void validateGenotypes(Map<String, Set<Integer>> gt, PedigreeManager pedigreeManager) {
        List<Individual> withoutChildren = pedigreeManager.getWithoutChildren();

        Queue<String> queue = new LinkedList<>();

        for (Individual individual : withoutChildren) {
            queue.add(individual.getId());
        }

        while (!queue.isEmpty()) {
            String individualId = queue.remove();
            Individual individual = pedigreeManager.getIndividualMap().get(individualId);
            processIndividual(individual, gt);

            if (individual.getFather() != null) {
                if (!queue.contains(individual.getFather().getId())) {
                    queue.add(individual.getFather().getId());
                }
            }

            if (individual.getMother() != null) {
                if (!queue.contains(individual.getMother().getId())) {
                    queue.add(individual.getMother().getId());
                }
            }
        }


    }

    private static void processIndividual(Individual individual, Map<String, Set<Integer>> gt) {
        // 1. We first process them independently so the possible genotypes are reduced

        // From father to child
        if (individual.getFather() != null) {
            gt.put(individual.getId(), validate(gt.get(individual.getFather().getId()), gt.get(individual.getId())));
        }

        // From mother to child
        if (individual.getMother() != null) {
            gt.put(individual.getId(), validate(gt.get(individual.getMother().getId()), gt.get(individual.getId())));
        }

        // From child to father
        if (individual.getFather() != null) {
            gt.put(individual.getFather().getId(), validate(gt.get(individual.getId()), gt.get(individual.getFather().getId())));
        }

        // From child to mother
        if (individual.getMother() != null) {
            gt.put(individual.getMother().getId(), validate(gt.get(individual.getId()), gt.get(individual.getMother().getId())));
        }

        // 2. We now perform a comparison having a look at both parents
        if (individual.getMother() != null && individual.getFather() != null) {
            Set<Integer> fatherGenotypes = gt.get(individual.getFather().getId());
            Set<Integer> motherGenotypes = gt.get(individual.getMother().getId());
            Set<Integer> childGenotypes = gt.get(individual.getId());

            Set<Integer> finalGenotypes = new HashSet<>();
            for (int childGenotype : childGenotypes) {
                if (childGenotype == GENOTYPE_0_0) {
                    if ((fatherGenotypes.contains(GENOTYPE_0_0) || fatherGenotypes.contains(GENOTYPE_0_1)
                            || fatherGenotypes.contains(GENOTYPE_0))
                            && (motherGenotypes.contains(GENOTYPE_0_0) || motherGenotypes.contains(GENOTYPE_0_1))) {
                        finalGenotypes.add(GENOTYPE_0_0);
                    }
                } else if (childGenotype == GENOTYPE_0_1) {
                    if (((fatherGenotypes.contains(GENOTYPE_0_0) || fatherGenotypes.contains(GENOTYPE_0_1)
                            || fatherGenotypes.contains(GENOTYPE_0))
                            && (motherGenotypes.contains(GENOTYPE_0_1) || motherGenotypes.contains(GENOTYPE_1_1)))
                            || ((motherGenotypes.contains(GENOTYPE_0_0) || motherGenotypes.contains(GENOTYPE_0_1))
                            && (fatherGenotypes.contains(GENOTYPE_0_1) || fatherGenotypes.contains(GENOTYPE_1_1)
                            || fatherGenotypes.contains(GENOTYPE_1)))) {
                        finalGenotypes.add(GENOTYPE_0_1);
                    }
                } else if (childGenotype == GENOTYPE_1_1) {
                    if ((fatherGenotypes.contains(GENOTYPE_0_1) || fatherGenotypes.contains(GENOTYPE_1_1)
                            || fatherGenotypes.contains(GENOTYPE_1))
                            && (motherGenotypes.contains(GENOTYPE_0_1) || motherGenotypes.contains(GENOTYPE_1_1))) {
                        finalGenotypes.add(GENOTYPE_1_1);
                    }
                } else {
                    finalGenotypes.add(childGenotype);
                }
            }

            gt.put(individual.getId(), finalGenotypes);
        }
    }

    private static void processIndividualCopy(Individual individual, Map<String, Set<Integer>> gt) {
        // From father to child
        if (individual.getFather() != null) {
            gt.put(individual.getId(), validate(gt.get(individual.getFather().getId()), gt.get(individual.getId())));
        }

        // From mother to child
        if (individual.getMother() != null) {
            gt.put(individual.getId(), validate(gt.get(individual.getMother().getId()), gt.get(individual.getId())));
        }

        // From child to father
        if (individual.getFather() != null) {
            gt.put(individual.getFather().getId(), validate(gt.get(individual.getId()), gt.get(individual.getFather().getId())));
        }

        // From child to mother
        if (individual.getMother() != null) {
            gt.put(individual.getMother().getId(), validate(gt.get(individual.getId()), gt.get(individual.getMother().getId())));
        }
    }

    private static Set<Integer> validate(Set<Integer> from, Set<Integer> to) {
        Set<Integer> validGt = new HashSet<>();
        for (int gtFrom : from) {
            for (int gtTo : to) {
                if (gtFrom == GENOTYPE_0_0) {
                    // 0/0 in parent should be...
                    if (gtTo == GENOTYPE_0_0 || gtTo == GENOTYPE_0_1) {
                        validGt.add(gtTo);
                    }
                    // This case should only happen if the gtFrom is from the mother and the gtTo from her son (x-linked)
                    if (gtTo == GENOTYPE_0) {
                        validGt.add(gtTo);
                    }
                } else if (gtFrom == GENOTYPE_1_1) {
                    // 1/1 in parent should be 0/1 or 1/1 in child
                    if (gtTo == GENOTYPE_0_1 || gtTo == GENOTYPE_1_1) {
                        validGt.add(gtTo);
                    }
                    // This case should only happen if the gtFrom is from the mother and the gtTo from her son (x-linked)
                    if (gtTo == GENOTYPE_1) {
                        validGt.add(gtTo);
                    }
                } else if (gtFrom == GENOTYPE_0_1) {
                    // 0/1 in parent can be whatever in child
                    validGt.add(gtTo);
                } else if (gtFrom == GENOTYPE_0) {
                    // Comparison of son (gtFrom) with mother (gtTo)
                    if (gtTo == GENOTYPE_0_0 || gtTo == GENOTYPE_0_1) {
                        validGt.add(gtTo);
                    }
                    // Comparison of son (gtFrom) with father (gtTo)
                    if (gtTo == GENOTYPE_0 || gtTo == GENOTYPE_1) {
                        // The father is not affected by the x-linked genotype
                        validGt.add(gtTo);
                    }
                } else if (gtFrom == GENOTYPE_1) {
                    // Comparison of son (gtFrom) with mother (gtTo)
                    if (gtTo == GENOTYPE_0_1 || gtTo == GENOTYPE_1_1) {
                        validGt.add(gtTo);
                    }
                    // Comparison of son (gtFrom) with father (gtTo)
                    if (gtTo == GENOTYPE_0 || gtTo == GENOTYPE_1) {
                        // The father is not affected by the x-linked genotype
                        validGt.add(gtTo);
                    }
                }
            }
        }
        return validGt;
    }

    private static Map<String, List<String>> prepareOutput(Map<String, Set<Integer>> genotypes) {
        Map<String, List<String>> output = new HashMap<>();
        for (String key : genotypes.keySet()) {
            List<String> gtList = new ArrayList<>();
            Iterator<Integer> it = genotypes.get(key).iterator();
            while (it.hasNext()) {
                gtList.add(toGenotypeString(it.next()));
            }
            output.put(key, gtList);
        }
        return output;
    }

    public static String toGenotypeString(int gt) {
        switch (gt) {
            case GENOTYPE_0_0:
                return "0/0";
            case GENOTYPE_0_1:
                return "0/1";
            case GENOTYPE_1_1:
                return "1/1";
            case GENOTYPE_0:
                return "0";
            case GENOTYPE_1:
                return "1";
            default:
                return "-";
        }
    }
}
