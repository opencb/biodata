package org.opencb.biodata.models.core.pedigree;

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.commons.utils.ListUtils;

import java.util.HashSet;
import java.util.Set;

public class PedigreeManager {
    private Pedigree pedigree;

    public PedigreeManager(Pedigree pedigree) {
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
}
