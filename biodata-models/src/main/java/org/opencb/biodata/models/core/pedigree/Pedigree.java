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
import org.opencb.biodata.models.commons.Phenotype;

import java.util.*;

/**
 * Created by imedina on 10/10/16.
 */
public class Pedigree {
    private String name;

    private List<Phenotype> phenotypes;
    private List<Individual> members;

    private Individual proband;

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
        this(name, members, null, new ArrayList<>(), attributes);
    }

    /**
     * Constructor.
     *
     * @param name          Family name
     * @param members       Individuals belonging to this family
     * @param phenotypes    List of phenotypes present in the members of the family
     * @param attributes    Family attributes
     */
    public Pedigree(String name, List<Individual> members, List<Phenotype> phenotypes, Map<String, Object> attributes) {
        this(name, members, null, phenotypes, attributes);
    }

    /**
     * Constructor.
     *
     * @param name          Family name
     * @param members       Individuals belonging to this family
     * @param proband       Proband individual
     * @param phenotypes    List of phenotypes present in the members of the family
     * @param attributes    Family attributes
     */
    public Pedigree(String name, List<Individual> members, Individual proband, List<Phenotype> phenotypes, Map<String, Object> attributes) {
        this.name = name;
        this.members = members;
        this.proband = proband;
        this.phenotypes = phenotypes;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public Pedigree setName(String name) {
        this.name = name;
        return this;
    }

    public List<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public Pedigree setPhenotypes(List<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public List<Individual> getMembers() {
        return members;
    }

    public Pedigree setMembers(List<Individual> members) {
        this.members = members;
        return this;
    }

    public Individual getProband() {
        return proband;
    }

    public Pedigree setProband(Individual proband) {
        this.proband = proband;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Pedigree setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String toJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this).toString();
        } catch (Exception e) {
            return "";
        }
    }
}
