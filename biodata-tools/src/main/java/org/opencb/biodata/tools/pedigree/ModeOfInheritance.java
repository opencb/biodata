package org.opencb.biodata.tools.pedigree;

import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.biodata.models.core.pedigree.PedigreeManager;

import java.util.*;

public class ModeOfInheritance {

    public static final int GENOTYPE_0_0 = 0;
    public static final int GENOTYPE_0_1 = 1;
    public static final int GENOTYPE_1_1 = 2;

    public static Map<String, List<String>> dominant(Pedigree pedigree, Phenotype phenotype) {
        PedigreeManager pedigreeManager = new PedigreeManager(pedigree);

        // Get affected individuals for that phenotype
        Set<Individual> affectedIndividuals = pedigreeManager.getAffectedIndividuals(phenotype);

        // Get all possible genotypes for each individual
        Map<String, Set<Integer>> genotypes = new HashMap<>();
        for (Individual individual: pedigree.getMembers()) {
            genotypes.put(individual.getId(), calculateDominant(affectedIndividuals.contains(individual)));
        }

        // Validate genotypes using relationships
        validateGenotypes(genotypes, pedigreeManager);

        // Return a readable output, i.e., returning "0/0, "0/1", "1/1"
        return prepareOutput(genotypes);
    }

    public static Map<String, List<String>> recessive(Pedigree pedigree, Phenotype phenotype) {
        return null;
    }

    public static Map<String, List<String>> xLinked(Pedigree pedigree, Phenotype phenotype) {
        return null;
    }

    public static Map<String, List<String>> yLinked(Pedigree pedigree, Phenotype phenotype) {
        return null;
    }

    private static Set<Integer> calculateDominant(boolean affected) {
        Set<Integer> gt = new HashSet<>();
        if (affected) {
            gt.add(GENOTYPE_0_1);
            gt.add(GENOTYPE_1_1);
        } else {
            gt.add(GENOTYPE_0_0);
        }
        return gt;
    }

    private static Set<Integer> calculateRecessive(boolean affected) {
        Set<Integer> gt = new HashSet<>();
        if (affected) {
            gt.add(GENOTYPE_1_1);
        } else {
            gt.add(GENOTYPE_0_0);
            gt.add(GENOTYPE_0_1);
        }
        return gt;
    }

    private static void validateGenotypes(Map<String, Set<Integer>> gt, PedigreeManager pedigreeManager) {
        List<Individual> withoutChildren = pedigreeManager.getWithoutChildren();

        for (Individual individual: withoutChildren) {
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
    }



    private static Set<Integer> validate(Set<Integer> from, Set<Integer> to) {
        Set<Integer> validGt = new HashSet<>();
        for (int gtFrom: from) {
            for (int gtTo: to) {
                if (gtFrom == GENOTYPE_0_0) {
                    // 0/0 in parent should be...
                    if (gtTo == GENOTYPE_0_0 || gtTo == GENOTYPE_1_1) {
                        validGt.add(gtTo);
                    }
                } else if (gtFrom == GENOTYPE_1_1) {
                    // 1/1 in parent should be 0/1 or 1/1 in child
                    if (gtTo == GENOTYPE_0_1 || gtTo == GENOTYPE_1_1) {
                        validGt.add(gtTo);
                    }
                } else {
                    // 0/1 in parent can be whatever in child
                    validGt.add(gtTo);
                }
            }
        }
        return validGt;
    }

    private static Map<String, List<String>> prepareOutput(Map<String, Set<Integer>> genotypes) {
        Map<String, List<String>> output = new HashMap<>();
        for (String key: genotypes.keySet()) {
            List<String> gtList = new ArrayList<>();
            Iterator<Integer> it = genotypes.get(key).iterator();
            while (it.hasNext()) {
                gtList.add(toGenotypeString(it.next()));
            }
            output.put(key, gtList);
        }
        return output;
    }

    private static String toGenotypeString(int gt) {
        switch (gt) {
            case GENOTYPE_0_0:
                return "0/0";
            case GENOTYPE_0_1:
                return "0/1";
            case GENOTYPE_1_1:
                return "1/1";
            default:
                return "-";
        }
    }
}
