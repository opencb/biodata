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

import java.util.Set;

/**
 * Created by imedina on 10/10/16.
 */
public class Family {

    private String id;
    private Individual father;
    private Individual mother;

    private int numGenerations;
    private Set<Individual> members;

    public Family() {
    }

    public Family(String id) {
        this.id = id;
    }

    public Family(String id, Individual father, Individual mother) {
        this.id = id;
        this.father = father;
        this.mother = mother;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Family{");
        sb.append("id='").append(id).append('\'');
        sb.append(", father=").append(father);
        sb.append(", mother=").append(mother);
        sb.append(", numGenerations=").append(numGenerations);
        sb.append(", members=").append(members);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Family setId(String id) {
        this.id = id;
        return this;
    }

    public Individual getFather() {
        return father;
    }

    public Family setFather(Individual father) {
        this.father = father;
        return this;
    }

    public Individual getMother() {
        return mother;
    }

    public Family setMother(Individual mother) {
        this.mother = mother;
        return this;
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public Family setNumGenerations(int numGenerations) {
        this.numGenerations = numGenerations;
        return this;
    }

    public Set<Individual> getMembers() {
        return members;
    }

    public Family setMembers(Set<Individual> members) {
        this.members = members;
        return this;
    }
}
