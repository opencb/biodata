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

import java.util.Map;
import java.util.Set;

/**
 * Created by imedina on 10/10/16.
 */
public class Individual {

    private String id;
    private String family;
    private Individual father;
    private Individual mother;
    private Individual partner;
    private Sex sex;
    private Phenotype phenotype;
    private Map<String, Object> variables;
    private Set<Individual> children;

    public enum Sex {
        MALE,
        FEMALE,
        UNKNOWN_SEX
    }

    public enum Phenotype {
        MISSING,
        AFFECTED,
        UNAFFECTED
    }


    public Individual() {
    }

    public Individual(String id, String family, Individual father, Individual mother, Sex sex, Phenotype phenotype) {
        this.id = id;
        this.family = family;
        this.father = father;
        this.mother = mother;
        this.sex = sex;
        this.phenotype = phenotype;

    }

    public Individual(String id, String family, Individual father, Individual mother, Sex sex, Phenotype phenotype,
                      Map<String, Object>  variables) {
        this(id, family, father, mother, sex, phenotype);
        this.variables = variables;
    }

    public Individual(String id, String family, Individual father, Individual mother, Individual partner, Sex sex, Phenotype phenotype,
                      Map<String, Object> variables, Set<Individual> children) {
        this(id, family, father, mother, sex, phenotype, variables);
        this.partner = partner;
        this.children = children;
    }




    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Individual{");
        sb.append("id='").append(id).append('\'');
        sb.append(", family='").append(family).append('\'');
        sb.append(", father=").append(father);
        sb.append(", mother=").append(mother);
        sb.append(", partner=").append(partner);
        sb.append(", sex=").append(sex);
        sb.append(", phenotype=").append(phenotype);
        sb.append(", variables=").append(variables);
        sb.append(", children=").append(children);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Individual setId(String id) {
        this.id = id;
        return this;
    }

    public String getFamily() {
        return family;
    }

    public Individual setFamily(String family) {
        this.family = family;
        return this;
    }

    public Individual getFather() {
        return father;
    }

    public Individual setFather(Individual father) {
        this.father = father;
        return this;
    }

    public Individual getMother() {
        return mother;
    }

    public Individual setMother(Individual mother) {
        this.mother = mother;
        return this;
    }

    public Individual getPartner() {
        return partner;
    }

    public Individual setPartner(Individual partner) {
        this.partner = partner;
        return this;
    }

    public Sex getSex() {
        return sex;
    }

    public Individual setSex(Sex sex) {
        this.sex = sex;
        return this;
    }

    public Phenotype getPhenotype() {
        return phenotype;
    }

    public Individual setPhenotype(Phenotype phenotype) {
        this.phenotype = phenotype;
        return this;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public Individual setVariables(Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    public Set<Individual> getChildren() {
        return children;
    }

    public Individual setChildren(Set<Individual> children) {
        this.children = children;
        return this;
    }
}
