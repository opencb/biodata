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

package org.opencb.biodata.models.clinical.pedigree;

import org.opencb.biodata.models.clinical.Disorder;
import org.opencb.biodata.models.clinical.Phenotype;
import org.opencb.biodata.models.core.OntologyTermAnnotation;
import org.opencb.biodata.models.core.SexOntologyTermAnnotation;
import org.opencb.biodata.models.pedigree.IndividualProperty;
import org.opencb.biodata.models.pedigree.Multiples;

import java.util.List;
import java.util.Map;

/**
 * Created by imedina on 10/10/16.
 */
public class Member {

    private String id;
    private String name;

    private Member father;
    private Member mother;
    private Multiples multiples;

    private SexOntologyTermAnnotation sex;
    private IndividualProperty.LifeStatus lifeStatus;

    private List<Phenotype> phenotypes;
    private List<Disorder> disorders;

    private Map<String, Object> attributes;

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
    public Member() {
    }

    /**
     * Constructor.
     *  @param id                Individual ID
     * @param name              Individual name
     * @param sex               Individual sex
     */
    public Member(String id, String name, SexOntologyTermAnnotation sex) {
        this.id = id;
        this.name = name;
        this.sex = sex;
    }

    /**
     * Constructor.
     *  @param name              Individual name
     * @param sex               Individual sex
     */
    public Member(String name, SexOntologyTermAnnotation sex) {
        this.id = name;
        this.name = name;
        this.sex = sex;
    }

    public Member(String id, String name, Member father, Member mother, Multiples multiples, SexOntologyTermAnnotation sex,
                  IndividualProperty.LifeStatus lifeStatus, List<Phenotype> phenotypes,
                  List<Disorder> disorders, Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.father = father;
        this.mother = mother;
        this.multiples = multiples;
        this.sex = sex;
        this.lifeStatus = lifeStatus;
        this.disorders = disorders;
        this.phenotypes = phenotypes;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Member{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", father=").append(father);
        sb.append(", mother=").append(mother);
        sb.append(", multiples=").append(multiples);
        sb.append(", sex=").append(sex);
        sb.append(", lifeStatus=").append(lifeStatus);
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", disorders=").append(disorders);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Member setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Member setName(String name) {
        this.name = name;
        return this;
    }

    public Member getFather() {
        return father;
    }

    public Member setFather(Member father) {
        this.father = father;
        return this;
    }

    public Member getMother() {
        return mother;
    }

    public Member setMother(Member mother) {
        this.mother = mother;
        return this;
    }

    public Multiples getMultiples() {
        return multiples;
    }

    public Member setMultiples(Multiples multiples) {
        this.multiples = multiples;
        return this;
    }

    public SexOntologyTermAnnotation getSex() {
        return sex;
    }

    public Member setSex(SexOntologyTermAnnotation sex) {
        this.sex = sex;
        return this;
    }

    public IndividualProperty.LifeStatus getLifeStatus() {
        return lifeStatus;
    }

    public Member setLifeStatus(IndividualProperty.LifeStatus lifeStatus) {
        this.lifeStatus = lifeStatus;
        return this;
    }

    public List<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public Member setPhenotypes(List<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public List<Disorder> getDisorders() {
        return disorders;
    }

    public Member setDisorders(List<Disorder> disorders) {
        this.disorders = disorders;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Member setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
