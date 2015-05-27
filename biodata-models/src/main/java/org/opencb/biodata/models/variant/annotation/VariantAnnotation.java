/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.variant.annotation;


import org.opencb.biodata.models.feature.Gene;
import org.opencb.biodata.models.variation.PopulationFrequency;

import java.util.*;


/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class VariantAnnotation {

    private String chromosome;
    
    private int start;
    
    private int end;
    
    private String referenceAllele;

    private String alternativeAllele;

    private String id;

    private List<Xref> xrefs;

    private List<String> hgvs;

    private List<ConsequenceType> consequenceTypes;

    private List<Score> conservedRegionScores;

    private List<PopulationFrequency> populationFrequencies = null;

    private List<CaddScore> caddScores;

//    private List<ExpressionValue> expressionValues;

    private Map<String, Object> clinicalData;

    private Map<String, Object> additionalAttributes;

    public VariantAnnotation() {
        this(null, 0, 0, null);
    }

    public VariantAnnotation(String chromosome, int start, int end, String referenceAllele) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.referenceAllele = referenceAllele;
    }

    public VariantAnnotation(String chromosome, int start, int end, String referenceAllele, String alternativeAllele) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.referenceAllele = referenceAllele;
        this.alternativeAllele = alternativeAllele;
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

    public String getAlternativeAllele() {
        return alternativeAllele;
    }

    public void setAlternativeAllele(String alternativeAllele) {
        this.alternativeAllele = alternativeAllele;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Xref> getXrefs() {
        return xrefs;
    }

    public void setXrefs(List<Xref> xrefs) {
        this.xrefs = xrefs;
    }

    public List<String> getHgvs() {
        return hgvs;
    }

    public void setHgvs(List<String> hgvs) {
        this.hgvs = hgvs;
    }

    public List<ConsequenceType> getConsequenceTypes() {
        return consequenceTypes;
    }

    public void setConsequenceTypes(List<ConsequenceType> consequenceTypes) {
        this.consequenceTypes = consequenceTypes;
    }

    public List<Score> getConservedRegionScores() {
        return conservedRegionScores;
    }

    public void setConservedRegionScores(List<Score> conservedRegionScores) {
        this.conservedRegionScores = conservedRegionScores;
    }

    public List<PopulationFrequency> getPopulationFrequencies() {
        return populationFrequencies;
    }

    public void setPopulationFrequencies(List<PopulationFrequency> populationFrequencies) {
        this.populationFrequencies = populationFrequencies;
    }

    public void addPopulationFrequency(PopulationFrequency populationFrequency) {
        if(this.populationFrequencies==null) {
            this.populationFrequencies = new ArrayList<>();
        }
        this.populationFrequencies.add(populationFrequency);
    }

    public List<CaddScore> getCaddScores() {
        return caddScores;
    }

    public void setCaddScores(List<CaddScore> caddScores) {
        this.caddScores = caddScores;
    }

//    public List<ExpressionValue> getExpressionValues() {
//        return expressionValues;
//    }
//
//    public void setExpressionValues(List<ExpressionValue> expressionValues) {
//        this.expressionValues = expressionValues;
//    }

    public Map<String, Object> getClinicalData() {
        return clinicalData;
    }

    public void setClinicalData(Map<String, Object> clinicalData) {
        this.clinicalData = clinicalData;
    }

    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, Object> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

//    public String getChromosome() {
//        return chromosome;
//    }
//
//    public void setChromosome(String chromosome) {
//        this.chromosome = chromosome;
//    }
//
//    public int getStart() {
//        return start;
//    }
//
//    public void setStart(int start) {
//        this.start = start;
//    }
//
//    public int getEnd() {
//        return end;
//    }
//
//    public void setEnd(int end) {
//        this.end = end;
//    }
//
//    public String getReferenceAllele() {
//        return referenceAllele;
//    }
//
//    public void setReferenceAllele(String referenceAllele) {
//        this.referenceAllele = referenceAllele;
//    }

//    public Set<Gene> getGenes() {
//        return genes;
//    }

//    public void setGenes(Set<Gene> genes) {
//        this.genes = genes;
//    }

    public void addGene(Gene gene) {
        // TODO: broken compatibility with VariantGenesAnnotator
    }

//    public void addGene(Gene gene) {
//        this.genes.add(gene);
//    }
//
    public Map<String, List<VariantEffect>> getEffects() {
        // TODO: broken compatibility with VariantConsequenceTypeAnnotator
        return new HashMap<>();
    }

//    public Map<String, List<VariantEffect>> getEffects() {
//        return effects;
//    }
//
//    public void setEffects(Map<String, List<VariantEffect>> effects) {
//        this.effects = effects;
//    }
//
    public void addEffect(String key, VariantEffect effect) {
        // TODO: compatibility is broken with Variant object. We no longer want an effects attribute in VariantAnnotation
    }

//    public void addEffect(String key, VariantEffect effect) {
//        List<VariantEffect> ct = effects.get(key);
//        if (ct == null) {
//            ct = new ArrayList<>();
//            effects.put(key, ct);
//        }
//
//        ct.add(effect);
//    }
//

    public Map<String, Set<Frequency>> getFrequencies() {
        return new HashMap<>(); // TODO: broken compatibility with VariantEffectConverter
    }

//    public Map<String, Set<Frequency>> getFrequencies() {
//        return frequencies;
//    }
//
//    public Set<Frequency> getFrequenciesBySuperPopulation(String population) {
//        return frequencies.get(population);
//    }
//
//    public void setFrequencies(Map<String, Set<Frequency>> frequencies) {
//        this.frequencies = frequencies;
//    }
//
//    public boolean addFrequency(Frequency frequency) {
//        Set<Frequency> frequenciesBySuperPopulation = frequencies.get(frequency.getSuperPopulation());
//        if (frequenciesBySuperPopulation == null) {
//            frequenciesBySuperPopulation = new HashSet<>();
//            frequencies.put(frequency.getSuperPopulation(), frequenciesBySuperPopulation);
//        }
//        return frequenciesBySuperPopulation.add(frequency);
//    }

    public ProteinSubstitutionScores getProteinSubstitutionScores() {
        // TODO: broken compatibility with VariantPolyphenSIFTAnnotator. proteinSubstitutionScores is no longer within VariantAnnotation. Remove after compatibility is solved.
        return null;
    }

    public void setRegulatoryEffect(RegulatoryEffect regulatoryEffect) {}  // TODO: broken compatibility with VariantEffectConverter

    public RegulatoryEffect getRegulatoryEffect() {
        // TODO: broken compatibility with VariantEffectConverter
        return null;
    }

//    public RegulatoryEffect getRegulatoryEffect() {
//        return regulatoryEffect;
//    }

//    public void setRegulatoryEffect(RegulatoryEffect regulatoryEffect) {
//        this.regulatoryEffect = regulatoryEffect;
//    }

//    @Override
//    public String toString() {
//        return "VariantAnnotation{" +
//                "chromosome='" + chromosome + '\'' +
//                ", start=" + start +
//                ", end=" + end +
//                ", referenceAllele='" + referenceAllele + '\'' +
//                ", proteinSubstitutionScores=" + proteinSubstitutionScores +
//                '}';
//    }
}
