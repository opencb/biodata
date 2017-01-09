package org.opencb.biodata.models.core.pedigree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by imedina on 10/10/16.
 */
public class Pedigree {

    private Map<String, VariableField> variables;
    private Map<String, Individual> individuals;
    private Map<String, Family> families;

    /**
     * Constructor.
     *
     * @param individuals   Map of individuals
     */
    public Pedigree(Map<String, Individual> individuals) {
        init(individuals);
        //this.individuals = individuals;
    }

    /**
     * Pedigree initialization.
     *
     * @param individuals   Map of individuals
     */
    private void init(Map<String, Individual> individuals) {
        this.individuals = individuals;

        initFamilies();
        initVariables();
    }

    /**
     * Families map initialization.
     */
    private void initFamilies() {
        Family family;
        String familyID;
        families = new HashMap<>();
        for (Individual individual: individuals.values()) {
            familyID = individual.getFamily();
            if (!families.containsKey(familyID)) {
                families.put(familyID, new Family(familyID));
            }
            family = families.get(familyID);
            // set family father and mother
            if (individual.getFather() == null && individual.getMother() == null) {
                if (individual.getSex() == Individual.Sex.MALE) {
                    // set father
                    if (family.getFather() == null) {
                        family.setFather(individual);
                    } else {
                        if (computeNumberOfGenerations(individual) > computeNumberOfGenerations(family.getFather())) {
                            family.setFather(individual);
                        }
                    }
                } else if (individual.getSex() == Individual.Sex.FEMALE) {
                    // set mother
                    if (family.getMother() == null) {
                        family.setMother(individual);
                    } else {
                        if (computeNumberOfGenerations(individual) > computeNumberOfGenerations(family.getMother())) {
                            family.setMother(individual);
                        }
                    }
                }
            }
            // finally set members
            family.getMembers().add(individual);
        }

        // compute number of generations for each family from the father or the mother
        for (Family f: families.values()) {
            if (f.getFather() != null) {
                f.setNumGenerations(computeNumberOfGenerations(f.getFather()));
            } else if (f.getMother() != null) {
                f.setNumGenerations(computeNumberOfGenerations(f.getMother()));
            } else {
                // it does not have to occurr ever !!
                throw new InternalError("Unexpected family without parents, something may be wrong in your data!");
            }
        }
    }


    public static void updateIndividuals(Individual father, Individual mother, Individual child) {
        // setting father and children
        if (father != null) {
            child.setFather(father);
            if (father.getChildren() == null) {
                father.setChildren(new LinkedHashSet<>());
            }
            father.getChildren().add(child);
        }

        // setting mother and children
        if (mother != null) {
            child.setMother(mother);
            if (mother.getChildren() == null) {
                mother.setChildren(new LinkedHashSet<>());
            }
            mother.getChildren().add(child);
        }

        // setting partners
        if (father != null && mother != null) {
            father.setPartner(mother);
            mother.setPartner(father);
        }
    }

    /**
     * Recursive function to compute the number of generations of a given individual.
     *
     * @param   individual  Target individual
     * @return              Number of generations
     */
    private int computeNumberOfGenerations(Individual individual) {
        int max = 1;

        if (individual.getChildren() != null) {
            Iterator it = individual.getChildren().iterator();
            while (it.hasNext()) {
                Individual child = (Individual) it.next();
                max = Math.max(max, 1 + computeNumberOfGenerations(child));
            }
        }
        return max;
    }

    /**
     * Variables map initialization.
     */
    public void initVariables() {
        Map<String, Object> individualVars;

        VariableField.VariableType type;
        variables = new HashMap<>();

        // iterate all individuals, checking their variables
        for (Individual individual: individuals.values()) {
            individualVars = individual.getVariables();
            if (individualVars != null) {
                for (String key: individualVars.keySet()) {
                    // is this variable in the map ?
                    if (!variables.containsKey(key)) {
                        // identify the type of variable
                        if (individualVars.get(key) instanceof Boolean) {
                            type = VariableField.VariableType.BOOLEAN;
                        } else if (individualVars.get(key) instanceof Double) {
                            type = VariableField.VariableType.DOUBLE;
                        } else if (individualVars.get(key) instanceof Integer) {
                            type = VariableField.VariableType.INTEGER;
                        } else {
                            type = VariableField.VariableType.STRING;
                        }
                        // and finally, add this variable into the map
                        variables.put(key, new VariableField(key, type));
                    }
                }
            }
        }
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
/*
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
*/
    /**
     * This method calculate the families and the individual partners.
     *//*
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
*/
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
