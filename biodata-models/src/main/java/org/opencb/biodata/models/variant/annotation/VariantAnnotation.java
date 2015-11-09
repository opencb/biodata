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

import org.opencb.biodata.models.variation.PopulationFrequency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Deprecated
public class VariantAnnotation {

    private String chromosome;
    private int start;
    private int end;
    private String reference;
    private String alternate;
    private String id;
    private List<Xref> xrefs;
    private List<String> hgvs;
    private List<ConsequenceType> consequenceTypes;
    private List<Score> conservation;
    private List<PopulationFrequency> populationFrequencies = null;
//    private List<CaddScore> caddScores;

    private List<ExpressionValue> expressionValues;
    private List<GeneDrugInteraction> geneDrugInteraction;
    private VariantTraitAssociation variantTraitAssociation;

    private Map<String, Object> additionalAttributes;


    public VariantAnnotation() {
        this(null, 0, 0, null, null);
    }

    public VariantAnnotation(String chromosome, int start, int end, String reference, String alternate) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.reference = reference;
        this.alternate = alternate;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
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

    public List<Score> getConservation() {
        return conservation;
    }

    public void setConservation(List<Score> conservation) {
        this.conservation = conservation;
    }

    public List<PopulationFrequency> getPopulationFrequencies() {
        return populationFrequencies;
    }

    public void setPopulationFrequencies(List<PopulationFrequency> populationFrequencies) {
        this.populationFrequencies = populationFrequencies;
    }

    public void addPopulationFrequency(PopulationFrequency populationFrequency) {
        if (this.populationFrequencies == null) {
            this.populationFrequencies = new ArrayList<>();
        }
        this.populationFrequencies.add(populationFrequency);
    }

//    public List<CaddScore> getCaddScores() {
//        return caddScores;
//    }
//
//    public void setCaddScores(List<CaddScore> caddScores) {
//        this.caddScores = caddScores;
//    }

    public List<GeneDrugInteraction> getGeneDrugInteraction() {
        return geneDrugInteraction;
    }

    public void setGeneDrugInteraction(List<GeneDrugInteraction> geneDrugInteraction) {
        this.geneDrugInteraction = geneDrugInteraction;
    }

    public List<ExpressionValue> getExpressionValues() {
        return expressionValues;
    }

    public void setExpressionValues(List<ExpressionValue> expressionValues) {
        this.expressionValues = expressionValues;
    }

    public VariantTraitAssociation getVariantTraitAssociation() {
        return variantTraitAssociation;
    }

    public void setVariantTraitAssociation(VariantTraitAssociation variantTraitAssociation) {
        this.variantTraitAssociation = variantTraitAssociation;
    }

    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, Object> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

}
//    public void addEffect(String key, VariantEffect effect) {
//        // TODO: compatibility is broken with Variant object. We no longer want an effects attribute in VariantAnnotation
//    }



