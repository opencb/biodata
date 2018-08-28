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
}
