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

import org.opencb.biodata.models.pedigree.Multiples;

import java.util.Map;

/**
 * Created by imedina on 10/10/16.
 */
public class Individual {
    private String name;

    private Individual father;
    private Individual mother;
    private Multiples multiples;

    private Sex sex;
    private LifeStatus lifeStatus;
    private AffectionStatus affectionStatus;
    //private List<OntologyTerm> ontologyTerms;
    //private List<Sample> samples;

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

    public enum LifeStatus {
        ALIVE, ABORTED, DECEASED, UNBORN, STILLBORN, MISCARRIAGE, UNKNOWN
    }

    /**
     * Empty constructor.
     */
    public Individual() {
    }

    /**
     * Constructor.
     *
     * @param name              Individual name
     * @param sex               Individual sex
     * @param affectionStatus   Individual affection status
     */
    public Individual(String name, Sex sex, AffectionStatus affectionStatus) {
        this.name = name;
        this.sex = sex;
        this.affectionStatus = affectionStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Individual getFather() {
        return father;
    }

    public void setFather(Individual father) {
        this.father = father;
    }

    public Individual getMother() {
        return mother;
    }

    public void setMother(Individual mother) {
        this.mother = mother;
    }

    public Multiples getMultiples() {
        return multiples;
    }

    public void setMultiples(Multiples multiples) {
        this.multiples = multiples;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setSex(String sex){
        setSex(Individual.Sex.getEnum(sex));
    }

    public LifeStatus getLifeStatus() {
        return lifeStatus;
    }

    public void setLifeStatus(LifeStatus lifeStatus) {
        this.lifeStatus = lifeStatus;
    }

    public AffectionStatus getAffectionStatus() {
        return affectionStatus;
    }

    public void setAffectionStatus(AffectionStatus affectionStatus) {
        this.affectionStatus = affectionStatus;
    }

    public void setAffectionStatus(String affectionStatus){
        setAffectionStatus(Individual.AffectionStatus.getEnum(affectionStatus));
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
