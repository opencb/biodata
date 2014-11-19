package org.opencb.biodata.models.variant.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opencb.biodata.models.feature.Gene;


/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class VariantAnnotation {

    private String chromosome;
    
    private int start;
    
    private int end;
    
    private String referenceAllele;
    
    private Set<Gene> genes;
    
    private Map<String, List<VariantEffect>> effects;
    
    private Map<String, Set<Frequency>> frequencies;
    
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
        this.frequencies = new HashMap<>();
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

    public void addEffect(String key, VariantEffect effect) {
        List<VariantEffect> ct = effects.get(key);
        if (ct == null) {
            ct = new ArrayList<>();
            effects.put(key, ct);
        }
        
        ct.add(effect);
    }

    public Map<String, Set<Frequency>> getFrequencies() {
        return frequencies;
    }

    public Set<Frequency> getFrequenciesBySuperPopulation(String population) {
        return frequencies.get(population);
    }
    
    public void setFrequencies(Map<String, Set<Frequency>> frequencies) {
        this.frequencies = frequencies;
    }
    
    public boolean addFrequency(Frequency frequency) {
        Set<Frequency> frequenciesBySuperPopulation = frequencies.get(frequency.getSuperPopulation());
        if (frequenciesBySuperPopulation == null) {
            frequenciesBySuperPopulation = new HashSet<>();
            frequencies.put(frequency.getSuperPopulation(), frequenciesBySuperPopulation);
        }
        return frequenciesBySuperPopulation.add(frequency);
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

    @Override
    public String toString() {
        return "VariantAnnotation{" +
                "chromosome='" + chromosome + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", referenceAllele='" + referenceAllele + '\'' +
                ", genes=" + genes +
                ", effects=" + effects +
                ", frequencies=" + frequencies +
                ", proteinSubstitutionScores=" + proteinSubstitutionScores +
                ", regulatoryEffect=" + regulatoryEffect +
                '}';
    }
}
