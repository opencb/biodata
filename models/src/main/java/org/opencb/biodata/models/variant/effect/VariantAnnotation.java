package org.opencb.biodata.models.variant.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opencb.biodata.models.feature.Gene;


/**
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantAnnotation {

    private String chromosome;
    
    private int start;
    
    private int end;
    
    private String referenceAllele;
    
    private Set<Gene> genes;
    
    private Map<String, List<VariantEffect>> effects;
    
    private Frequencies frequencies;
    
    private ProteinSubstitutionScores proteinSubstitutionScores;
    
    private RegulatoryEffect regulatoryEffect;


    public VariantAnnotation() {
        this(null, 0, 0, null);
    }

    public VariantAnnotation(String chromosome, int start, int end, String referenceAllele) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.referenceAllele = referenceAllele;
        this.genes = new HashSet<>();
        this.effects = new HashMap<>();
        this.frequencies = new Frequencies();
        this.proteinSubstitutionScores = new ProteinSubstitutionScores();
        this.regulatoryEffect = new RegulatoryEffect();
    }

    
    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele;
    }

    public Set<Gene> getGenes() {
        return genes;
    }

    public void setGenes(Set<Gene> genes) {
        this.genes = genes;
    }

    public void addGene(Gene gene) {
        this.genes.add(gene);
    }
    
    public Map<String, List<VariantEffect>> getEffects() {
        return effects;
    }

    public void setEffects(Map<String, List<VariantEffect>> effects) {
        this.effects = effects;
    }

    public void addEffect(String key, VariantEffect consequenceType) {
        List<VariantEffect> ct = effects.get(key);
        if (ct == null) {
            ct = new ArrayList<>();
            effects.put(key, ct);
        }
        
        ct.add(consequenceType);
        
//        effects.put(key, consequenceType);
    }
    
    public Frequencies getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(Frequencies frequencies) {
        this.frequencies = frequencies;
    }

    public ProteinSubstitutionScores getProteinSubstitutionScores() {
        return proteinSubstitutionScores;
    }

    public void setProteinSubstitutionScores(ProteinSubstitutionScores proteinSubstitutionScores) {
        this.proteinSubstitutionScores = proteinSubstitutionScores;
    }

    public RegulatoryEffect getRegulatoryEffect() {
        return regulatoryEffect;
    }

    public void setRegulatoryEffect(RegulatoryEffect regulatoryEffect) {
        this.regulatoryEffect = regulatoryEffect;
    }

}
