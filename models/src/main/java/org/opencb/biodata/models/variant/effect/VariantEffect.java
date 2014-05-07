package org.opencb.biodata.models.variant.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantEffect {

    private String chromosome;
    
    private int start;
    
    private int end;
    
    private String referenceAllele;
    
    private Map<String, List<ConsequenceType>> consequenceTypes;
    
    private Frequencies frequencies;
    
    private ProteinSubstitutionScores proteinSubstitutionScores;
    
    private RegulatoryEffect regulatoryEffect;


    public VariantEffect() {
        this(null, 0, 0, null);
    }

    public VariantEffect(String chromosome, int start, int end, String referenceAllele) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.referenceAllele = referenceAllele;
        this.consequenceTypes = new HashMap<>();
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

    public Map<String, List<ConsequenceType>> getConsequenceTypes() {
        return consequenceTypes;
    }

    public void setConsequenceTypes(Map<String, List<ConsequenceType>> consequenceTypes) {
        this.consequenceTypes = consequenceTypes;
    }

    public void addConsequenceType(String key, ConsequenceType consequenceType) {
        List<ConsequenceType> ct = consequenceTypes.get(key);
        if (ct == null) {
            ct = new ArrayList<>();
            consequenceTypes.put(key, ct);
        }
        
        ct.add(consequenceType);
        
//        consequenceTypes.put(key, consequenceType);
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
