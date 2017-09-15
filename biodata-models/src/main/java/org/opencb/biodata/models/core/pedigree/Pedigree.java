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

package org.opencb.biodata.models.core.pedigree;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.*;

/**
 * Created by imedina on 10/10/16.
 */
public class Pedigree {

    private long id;
    private String name;

    //private List<OntologyTerm> diseases;
    private List<Individual> members;

    private Map<String, Object> attributes;

    /**
     * Empty constructor.
     */
    public Pedigree() {
    }

    /**
     * Constructor.
     *
     * @param name          Family name
     * @param members       Individuals belonging to this family
     * @param attributes    Family attributes
     */
    public Pedigree(String name, List<Individual> members, Map<String, Object> attributes) {
        this.name = name;
        this.members = members;
        this.attributes = attributes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Individual> getMembers() {
        return members;
    }

    public void setMembers(List<Individual> members) {
        this.members = members;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String toJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this).toString();
        } catch (Exception e) {
            return "";
        }
    }

    //    /**
//     * Pedigree initialization.
//     *
//     * @param individuals   Map of individuals
//     */
//    private void init(Map<String, Individual> individuals) {
//        this.individuals = individuals;
//
//        // now init families and variables
//        initFamilies();
//        initVariables();
//    }
//
//    /**
//     * Families map initialization.
//     */
//    private void initFamilies() {
//        Family family;
//        String familyID;
//        families = new HashMap<>();
//        for (Individual individual: individuals.values()) {
//            familyID = individual.getFamily();
//            if (!families.containsKey(familyID)) {
//                families.put(familyID, new Family(familyID));
//            }
//            family = families.get(familyID);
//            // set family father and mother
//            if (individual.getFather() == null && individual.getMother() == null) {
//                if (individual.getSex() == Individual.Sex.MALE) {
//                    // set father
//                    if (family.getFather() == null) {
//                        family.setFather(individual);
//                    } else {
//                        if (computeNumberOfGenerations(individual) > computeNumberOfGenerations(family.getFather())) {
//                            family.setFather(individual);
//                        }
//                    }
//                } else if (individual.getSex() == Individual.Sex.FEMALE) {
//                    // set mother
//                    if (family.getMother() == null) {
//                        family.setMother(individual);
//                    } else {
//                        if (computeNumberOfGenerations(individual) > computeNumberOfGenerations(family.getMother())) {
//                            family.setMother(individual);
//                        }
//                    }
//                }
//            }
//            // finally set members
//            family.getMembers().add(individual);
//        }
//
//        // compute number of generations for each family from the father or the mother
//        for (Family f: families.values()) {
//            if (f.getFather() != null) {
//                f.setNumGenerations(computeNumberOfGenerations(f.getFather()));
//            } else if (f.getMother() != null) {
//                f.setNumGenerations(computeNumberOfGenerations(f.getMother()));
//            } else {
//                // it does not have to occurr ever !!
//                f.setNumGenerations(1);
//                System.err.println("Warning: family without parents, setting number of generations to 1!");
//                //throw new InternalError("Unexpected family without parents, something may be wrong in your data!");
//            }
//        }
//    }
//
//
//    public static void updateIndividuals(Individual father, Individual mother, Individual child) {
//        // setting father and children
//        if (father != null) {
//            child.setFather(father);
//            if (father.getChildren() == null) {
//                father.setChildren(new LinkedHashSet<>());
//            }
//            father.getChildren().add(child);
//        }
//
//        // setting mother and children
//        if (mother != null) {
//            child.setMother(mother);
//            if (mother.getChildren() == null) {
//                mother.setChildren(new LinkedHashSet<>());
//            }
//            mother.getChildren().add(child);
//        }
//
//        // setting partners
//        if (father != null && mother != null) {
//            father.setPartner(mother);
//            mother.setPartner(father);
//        }
//    }
//
//    /**
//     * Recursive function to compute the number of generations of a given individual.
//     *
//     * @param   individual  Target individual
//     * @return              Number of generations
//     */
//    private int computeNumberOfGenerations(Individual individual) {
//        int max = 1;
//
//        if (individual.getChildren() != null) {
//            Iterator it = individual.getChildren().iterator();
//            while (it.hasNext()) {
//                Individual child = (Individual) it.next();
//                max = Math.max(max, 1 + computeNumberOfGenerations(child));
//            }
//        }
//        return max;
//    }
//
//    /**
//     * Variables map initialization.
//     */
//    public void initVariables() {
//        Map<String, Object> individualVars;
//
//        VariableField.VariableType type;
//        variables = new HashMap<>();
//
//        // iterate all individuals, checking their variables
//        for (Individual individual: individuals.values()) {
//            individualVars = individual.getVariables();
//            if (individualVars != null) {
//                for (String key: individualVars.keySet()) {
//                    // is this variable in the map ?
//                    if (!variables.containsKey(key)) {
//                        // identify the type of variable
//                        if (individualVars.get(key) instanceof Boolean) {
//                            type = VariableField.VariableType.BOOLEAN;
//                        } else if (individualVars.get(key) instanceof Double) {
//                            type = VariableField.VariableType.DOUBLE;
//                        } else if (individualVars.get(key) instanceof Integer) {
//                            type = VariableField.VariableType.INTEGER;
//                        } else {
//                            type = VariableField.VariableType.STRING;
//                        }
//                        // and finally, add this variable into the map
//                        variables.put(key, new VariableField(key, type));
//                    }
//                }
//            }
//        }
//    }
//
//    public static String key(Individual individual) {
//        return (individual.getFamily() + "_" + individual.getId());
//    }
//
//    public static String key(String family, String id) {
//        return (family + "_" + id);
//    }
//
//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("Pedigree{");
//        sb.append("variables=").append(variables);
//        sb.append(", individuals=").append(individuals);
//        sb.append(", families=").append(families);
//        sb.append('}');
//        return sb.toString();
//    }
//
//    public Map<String, VariableField> getVariables() {
//        return variables;
//    }
//
//    public Pedigree setVariables(Map<String, VariableField> variables) {
//        this.variables = variables;
//        return this;
//    }
//
//    public Map<String, Individual> getIndividuals() {
//        return individuals;
//    }
//
//    public Pedigree setIndividuals(Map<String, Individual> individuals) {
//        this.individuals = individuals;
//        return this;
//    }
//
//    public Map<String, Family> getFamilies() {
//        return families;
//    }
//
}
