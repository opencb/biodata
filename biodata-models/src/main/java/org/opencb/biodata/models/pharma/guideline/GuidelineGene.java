package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class GuidelineGene {
    private float id;
    private List<Allele> alleles;
    private Gene gene;
    private float version;

    public float getId() {
        return id;
    }

    public GuidelineGene setId(float id) {
        this.id = id;
        return this;
    }

    public List<Allele> getAlleles() {
        return alleles;
    }

    public GuidelineGene setAlleles(List<Allele> alleles) {
        this.alleles = alleles;
        return this;
    }

    public Gene getGene() {
        return gene;
    }

    public GuidelineGene setGene(Gene gene) {
        this.gene = gene;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public GuidelineGene setVersion(float version) {
        this.version = version;
        return this;
    }
}
