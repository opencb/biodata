package org.opencb.biodata.models.core.pedigree;

import java.util.*;

/**
 * Created by imedina on 10/10/16.
 */
public class Pedigree {

    private Map<String, VariableField> variables;
    private Map<String, Individual> individuals;
    private Map<String, Family> families;

    public Pedigree(Map<String, Individual> individuals) {
        this.individuals = individuals;
    }

//    public Pedigree() {
//        init(null, null);
//    }
//
//    public Pedigree(List<Individual> individuals) {
//        init(individuals, null);
//    }
//
//    public Pedigree(List<Individual> individuals, List<VariableField> variables) {
//        init(individuals, variables);
//    }

    private void init(List<Individual> individuals, List<VariableField> variables) {
        this.variables = new LinkedHashMap<>();
        this.individuals = new LinkedHashMap<>();
        this.families = new LinkedHashMap<>();

        if (variables != null) {
            // TODO
        }

        addIndividuals(individuals);
    }

    public void addIndividual(Individual individual) {
        addIndividuals(Collections.singletonList(individual));
    }

    public void addIndividuals(List<Individual> individualList) {
        if (individualList != null) {

            if (this.individuals == null) {
                this.individuals = new LinkedHashMap<>(individualList.size() * 2);
            }

            for (Individual individual : individualList) {
                this.individuals.put(individual.getId(), individual);
            }

            calculateFamilies();
        }
    }

    /**
     * This method calculate the families and the individual partners.
     */
    private void calculateFamilies() {
        if (this.individuals != null) {
            Iterator<String> iterator = this.individuals.keySet().iterator();
            while (iterator.hasNext()) {
                String individualId = iterator.next();

                Individual individual = this.individuals.get(individualId);
                if (this.families.get(individual.getId()) == null) {
                    this.families.put(individual.getFamily(), new Family(individual.getFamily()));
                }

                // TODO
            }
        }
    }

    public static String key(Individual individual) {
        return (individual.getFamily() + "_" + individual.getId());
    }

    public static String key(String family, String id) {
        return (family + "_" + id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pedigree{");
        sb.append("variables=").append(variables);
        sb.append(", individuals=").append(individuals);
        sb.append(", families=").append(families);
        sb.append('}');
        return sb.toString();
    }

    public Map<String, VariableField> getVariables() {
        return variables;
    }

    public Pedigree setVariables(Map<String, VariableField> variables) {
        this.variables = variables;
        return this;
    }

    public Map<String, Individual> getIndividuals() {
        return individuals;
    }

    public Pedigree setIndividuals(Map<String, Individual> individuals) {
        this.individuals = individuals;
        return this;
    }

    public Map<String, Family> getFamilies() {
        return families;
    }

}
