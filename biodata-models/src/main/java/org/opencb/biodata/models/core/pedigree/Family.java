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
