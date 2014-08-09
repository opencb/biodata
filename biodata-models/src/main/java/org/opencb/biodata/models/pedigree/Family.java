package org.opencb.biodata.models.pedigree;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class Family {

    private Individual father;
    private Individual mother;
    private Set<Individual> children;

    public Family(Individual father, Individual mother) {
        this.father = father;
        this.mother = mother;

        this.children = new TreeSet<>();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Family)) return false;

        Family family = (Family) o;


        return this.father.getId().equals(family.getFather().getId()) && this.mother.getId().equals(family.getMother().getId());
    }

    @Override
    public int hashCode() {
        int result = father != null ? father.hashCode() : 0;
        result = 31 * result + (mother != null ? mother.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
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

    public void addChild(Individual ind) {
        this.children.add(ind);
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append("father=");
        sb.append(father.getId());
        sb.append(", mother=");
        sb.append(mother.getId());

        if (children.size() > 0) {
            sb.append(", children=[");
            for (Individual ind : children) {
                sb.append(ind.getId() + " ");
            }
            sb.append("]");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
