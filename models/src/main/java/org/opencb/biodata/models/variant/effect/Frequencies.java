package org.opencb.biodata.models.variant.effect;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 * 
 * FREQS - Frequencies of overlapping variants used in filtering
 */
public class Frequencies {
    
    /**
     * Minor allele of existing variation in 1000 Genomes Phase 1
     */
    private String allele1000g;
    
    /**
     * Minor allele frequency of existing variation in 1000 Genomes Phase 1
     */
    private float maf1000G;
    
    /**
     * Minor allele frequency of existing variation in 1000 Genomes Phase 1 combined African population
     */
    private float maf1000GAfrican;
    
    /**
     * Minor allele frequency of existing variation in 1000 Genomes Phase 1 combined American population
     */
    private float maf1000GAmerican;
    
    /**
     * Minor allele frequency of existing variation in 1000 Genomes Phase 1 combined Asian population
     */
    private float maf1000GAsian;
    
    /**
     * Minor allele frequency of existing variation in 1000 Genomes Phase 1 combined European population
     */
    private float maf1000GEuropean;
    
    /**
     * Minor allele frequency of existing variant in NHLBI-ESP African American population
     */
    private float mafNhlbiEspAfricanAmerican;
    
    /**
     * Minor allele frequency of existing variant in NHLBI-ESP European American population
     */
    private float mafNhlbiEspEuropeanAmerican;
    
    Frequencies() { }

    public Frequencies(String allele1000g, float maf1000G, float maf1000GAfrican, float maf1000GAmerican, float maf1000GAsian, float maf1000GEuropean, float mafNhlbiEspAfricanAmerican, float mafNhlbiEspEuropeanAmerican) {
        this.allele1000g = allele1000g;
        this.maf1000G = maf1000G;
        this.maf1000GAfrican = maf1000GAfrican;
        this.maf1000GAmerican = maf1000GAmerican;
        this.maf1000GAsian = maf1000GAsian;
        this.maf1000GEuropean = maf1000GEuropean;
        this.mafNhlbiEspAfricanAmerican = mafNhlbiEspAfricanAmerican;
        this.mafNhlbiEspEuropeanAmerican = mafNhlbiEspEuropeanAmerican;
    }

    public String getAllele1000g() {
        return allele1000g;
    }

    public void setAllele1000g(String allele1000g) {
        this.allele1000g = allele1000g;
    }

    public float getMaf1000G() {
        return maf1000G;
    }

    public void setMaf1000G(float maf1000G) {
        this.maf1000G = maf1000G;
    }

    public float getMaf1000GAfrican() {
        return maf1000GAfrican;
    }

    public void setMaf1000GAfrican(float maf1000GAfrican) {
        this.maf1000GAfrican = maf1000GAfrican;
    }

    public float getMaf1000GAmerican() {
        return maf1000GAmerican;
    }

    public void setMaf1000GAmerican(float maf1000GAmerican) {
        this.maf1000GAmerican = maf1000GAmerican;
    }

    public float getMaf1000GAsian() {
        return maf1000GAsian;
    }

    public void setMaf1000GAsian(float maf1000GAsian) {
        this.maf1000GAsian = maf1000GAsian;
    }

    public float getMaf1000GEuropean() {
        return maf1000GEuropean;
    }

    public void setMaf1000GEuropean(float maf1000GEuropean) {
        this.maf1000GEuropean = maf1000GEuropean;
    }

    public float getMafNhlbiEspAfricanAmerican() {
        return mafNhlbiEspAfricanAmerican;
    }

    public void setMafNhlbiEspAfricanAmerican(float mafNhlbiEspAfricanAmerican) {
        this.mafNhlbiEspAfricanAmerican = mafNhlbiEspAfricanAmerican;
    }

    public float getMafNhlbiEspEuropeanAmerican() {
        return mafNhlbiEspEuropeanAmerican;
    }

    public void setMafNhlbiEspEuropeanAmerican(float mafNhlbiEspEuropeanAmerican) {
        this.mafNhlbiEspEuropeanAmerican = mafNhlbiEspEuropeanAmerican;
    }

}
