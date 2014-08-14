package org.opencb.biodata.models.variant.effect;

import java.util.Objects;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class Frequency {
    
    private String superPopulation;
    
    private String population;
    
    private float frequency;
    
    Frequency() { }

    public Frequency(String superPopulation, String population, float frequency) {
        this.superPopulation = superPopulation;
        this.population = population;
        this.frequency = frequency;
    }

    public String getSuperPopulation() {
        return superPopulation;
    }

    public void setSuperPopulation(String superPopulation) {
        this.superPopulation = superPopulation;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.superPopulation);
        hash = 67 * hash + Objects.hashCode(this.population);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Frequency other = (Frequency) obj;
        if (!Objects.equals(this.superPopulation, other.superPopulation)) {
            return false;
        }
        if (!Objects.equals(this.population, other.population)) {
            return false;
        }
        return true;
    }

    
}
