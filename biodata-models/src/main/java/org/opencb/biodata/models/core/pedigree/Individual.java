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

import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.pedigree.IndividualProperty;
import org.opencb.biodata.models.pedigree.Multiples;

import java.util.List;
import java.util.Map;

/**
 * Created by imedina on 10/10/16.
 */
public class Individual {
    private String id;
    private String name;

    private Individual father;
    private Individual mother;
    private Multiples multiples;

    private Sex sex;
    private IndividualProperty.LifeStatus lifeStatus;
    private AffectionStatus affectionStatus;

    private List<Phenotype> phenotypes;

    private Map<String, Object> attributes;

    public enum Sex {
        MALE(1),
        FEMALE(2),
        UNKNOWN(0);

        private int value;
        Sex(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }

        public static Sex getEnum(String value) {
            switch (value) {
                case "1":
                case "MALE":
                    return MALE;
                case "2":
                case "FEMALE":
                    return FEMALE;
                default:
                    return UNKNOWN;
            }
        }
    }

    public enum AffectionStatus {
        UNKNOWN(0),
        UNAFFECTED(1),
        AFFECTED(2);

        private int value;
        AffectionStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static AffectionStatus getEnum(String value) {
            switch (value) {
                case "1":
                case "UNAFFECTED":
                    return UNAFFECTED;
                case "2":
                case "AFFECTED":
                    return AFFECTED;
                default:
                    return UNKNOWN;
            }
        }
    }

    /**
     * Empty constructor.
     */
    public Individual() {
    }

    /**
     * Constructor.
     *
     * @param id                Individual ID
     * @param name              Individual name
     * @param sex               Individual sex
     * @param affectionStatus   Individual affection status
     */
    public Individual(String id, String name, Sex sex, AffectionStatus affectionStatus) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.affectionStatus = affectionStatus;
    }

    /**
     * Constructor.
     *
     * @param name              Individual name
     * @param sex               Individual sex
     * @param affectionStatus   Individual affection status
     */
    public Individual(String name, Sex sex, AffectionStatus affectionStatus) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.affectionStatus = affectionStatus;
    }

    public Individual(String id, String name, Individual father, Individual mother, Multiples multiples, Sex sex,
                      IndividualProperty.LifeStatus lifeStatus, AffectionStatus affectionStatus, List<Phenotype> phenotypes,
                      Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.father = father;
        this.mother = mother;
        this.multiples = multiples;
        this.sex = sex;
        this.lifeStatus = lifeStatus;
        this.affectionStatus = affectionStatus;
        this.phenotypes = phenotypes;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public Individual setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Individual setName(String name) {
        this.name = name;
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

    public Multiples getMultiples() {
        return multiples;
    }

    public Individual setMultiples(Multiples multiples) {
        this.multiples = multiples;
        return this;
    }

    public Sex getSex() {
        return sex;
    }

    public Individual setSex(Sex sex) {
        this.sex = sex;
        return this;
    }

    public IndividualProperty.LifeStatus getLifeStatus() {
        return lifeStatus;
    }

    public Individual setLifeStatus(IndividualProperty.LifeStatus lifeStatus) {
        this.lifeStatus = lifeStatus;
        return this;
    }

    public AffectionStatus getAffectionStatus() {
        return affectionStatus;
    }

    public Individual setAffectionStatus(AffectionStatus affectionStatus) {
        this.affectionStatus = affectionStatus;
        return this;
    }

    public List<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public Individual setPhenotypes(List<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Individual setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
