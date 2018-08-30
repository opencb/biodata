package org.opencb.biodata.models.core.pedigree;

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.commons.utils.ListUtils;

import java.util.*;

public class PedigreeManager {
    private Pedigree pedigree;
    private List<Individual> withoutParents;
    private List<Individual> withOneParent;
    private List<Individual> withoutChildren;
    private Map<String, Individual> individualMap;
    private Map<String, List<Individual>> partner;
    private Map<String, List<Individual>> children;

    public PedigreeManager(Pedigree pedigree) {
        this.pedigree = pedigree;

        withoutParents = new ArrayList<>();
        withOneParent = new ArrayList<>();
        withoutChildren = new ArrayList<>();
        individualMap = new HashMap<>();
        partner = new HashMap<>();
        children = new HashMap<>();

        for (Individual individual: pedigree.getMembers()) {
            individualMap.put(individual.getId(), individual);

            // Parent and partner management
            if (individual.getFather() == null && individual.getMother() == null) {
                withoutParents.add(individual);
            } else if (individual.getFather() == null || individual.getMother() == null) {
                withOneParent.add(individual);
            } else {
                if (!partner.containsKey(individual.getFather().getId())) {
                    partner.put(individual.getFather().getId(), new ArrayList<>());
                }
                partner.get(individual.getFather().getId()).add(individual.getMother());

                if (!partner.containsKey(individual.getMother().getId())) {
                    partner.put(individual.getMother().getId(), new ArrayList<>());
                }
                partner.get(individual.getMother().getId()).add(individual.getFather());
            }

            // Children management
            if (individual.getFather() != null) {
                if (!children.containsKey(individual.getFather().getId())) {
                    children.put(individual.getFather().getId(), new ArrayList<>());
                }
                children.get(individual.getFather().getId()).add(individual);
            }
            if (individual.getMother() != null) {
                if (!children.containsKey(individual.getMother().getId())) {
                    children.put(individual.getMother().getId(), new ArrayList<>());
                }
                children.get(individual.getMother().getId()).add(individual);
            }
        }

        // Without children management
        for (Individual individual: pedigree.getMembers()) {
            if (!children.containsKey(individual.getId())) {
                withoutChildren.add(individual);
            }
        }
    }

    public Set<Individual> getAffectedIndividuals(Phenotype phenotype) {
        Set<Individual> individuals = new HashSet<>();
        for (Individual individual: pedigree.getMembers()) {
            if (ListUtils.isNotEmpty(individual.getPhenotypes())) {
                for (Phenotype pheno: individual.getPhenotypes()) {
                    if (StringUtils.isNotEmpty(pheno.getId()) && pheno.getId().equals(phenotype.getId())) {
                        individuals.add(individual);
                        break;
                    }
                }
            }
        }
        return individuals;
    }

    public Set<Individual> getUnaffectedIndividuals(Phenotype phenotype) {
        Set<Individual> individuals = new HashSet<>();
        for (Individual individual: pedigree.getMembers()) {
            boolean affected = false;
            if (ListUtils.isNotEmpty(individual.getPhenotypes())) {
                for (Phenotype pheno: individual.getPhenotypes()) {
                    if (StringUtils.isNotEmpty(pheno.getId()) && pheno.getId().equals(phenotype.getId())) {
                        affected = true;
                        break;
                    }
                }
            }
            if (!affected) {
                individuals.add(individual);
            }
        }
        return individuals;
    }

    public Pedigree getPedigree() {
        return pedigree;
    }

    public List<Individual> getWithoutParents() {
        return withoutParents;
    }

    public List<Individual> getWithOneParent() {
        return withOneParent;
    }

    public List<Individual> getWithoutChildren() {
        return withoutChildren;
    }

    public Map<String, Individual> getIndividualMap() {
        return individualMap;
    }

    public Map<String, List<Individual>> getPartner() {
        return partner;
    }

    public Map<String, List<Individual>> getChildren() {
        return children;
    }
}
