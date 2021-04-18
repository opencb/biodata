/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.core;

import org.opencb.biodata.models.clinical.ClinicalProperty;

import java.util.List;

public class CancerGeneAssociation {

    private String id;
    private String name;
    private String source;
    private String chromosome;
    private int start;
    private int end;
    private String cytoband;
    private String tier;
    private boolean somatic;
    private boolean germline;
    private List<String> somaticTumourTypes;
    private List<String> germlineTumourTypes;;
    private List<String> syndromes;
    private List<String> tissues;
    private List<ClinicalProperty.ModeOfInheritance> modeOfInheritance;
    private List<ClinicalProperty.RoleInCancer> roleInCancer;
    private List<String> mutationTypes;
    private List<String> translocationPartners;
    private List<String> otherSyndromes;
    private String synonyms;


    public CancerGeneAssociation(String id, String name, String source, String chromosome, int start, int end, String cytoband, String tier,
                                 boolean somatic, boolean germline, List<String> somaticTumourTypes, List<String> germlineTumourTypes,
                                 List<String> syndromes, List<String> tissues, List<ClinicalProperty.ModeOfInheritance> modeOfInheritance,
                                 List<ClinicalProperty.RoleInCancer> roleInCancer, List<String> mutationTypes,
                                 List<String> translocationPartners, List<String> otherSyndromes, String synonyms) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.cytoband = cytoband;
        this.tier = tier;
        this.somatic = somatic;
        this.germline = germline;
        this.somaticTumourTypes = somaticTumourTypes;
        this.germlineTumourTypes = germlineTumourTypes;
        this.syndromes = syndromes;
        this.tissues = tissues;
        this.modeOfInheritance = modeOfInheritance;
        this.roleInCancer = roleInCancer;
        this.mutationTypes = mutationTypes;
        this.translocationPartners = translocationPartners;
        this.otherSyndromes = otherSyndromes;
        this.synonyms = synonyms;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneCancerCensus{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", cytoband='").append(cytoband).append('\'');
        sb.append(", tier='").append(tier).append('\'');
        sb.append(", somatic=").append(somatic);
        sb.append(", germline=").append(germline);
        sb.append(", somaticTumourTypes=").append(somaticTumourTypes);
        sb.append(", germlineTumourTypes=").append(germlineTumourTypes);
        sb.append(", syndromes=").append(syndromes);
        sb.append(", tissues=").append(tissues);
        sb.append(", modeOfInheritance=").append(modeOfInheritance);
        sb.append(", roleInCancer=").append(roleInCancer);
        sb.append(", mutationTypes=").append(mutationTypes);
        sb.append(", translocationPartners=").append(translocationPartners);
        sb.append(", otherSyndromes=").append(otherSyndromes);
        sb.append(", synonyms='").append(synonyms).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public CancerGeneAssociation setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CancerGeneAssociation setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public CancerGeneAssociation setSource(String source) {
        this.source = source;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public CancerGeneAssociation setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public CancerGeneAssociation setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public CancerGeneAssociation setEnd(int end) {
        this.end = end;
        return this;
    }

    public String getCytoband() {
        return cytoband;
    }

    public CancerGeneAssociation setCytoband(String cytoband) {
        this.cytoband = cytoband;
        return this;
    }

    public String getTier() {
        return tier;
    }

    public CancerGeneAssociation setTier(String tier) {
        this.tier = tier;
        return this;
    }

    public boolean isSomatic() {
        return somatic;
    }

    public CancerGeneAssociation setSomatic(boolean somatic) {
        this.somatic = somatic;
        return this;
    }

    public boolean isGermline() {
        return germline;
    }

    public CancerGeneAssociation setGermline(boolean germline) {
        this.germline = germline;
        return this;
    }

    public List<String> getSomaticTumourTypes() {
        return somaticTumourTypes;
    }

    public CancerGeneAssociation setSomaticTumourTypes(List<String> somaticTumourTypes) {
        this.somaticTumourTypes = somaticTumourTypes;
        return this;
    }

    public List<String> getGermlineTumourTypes() {
        return germlineTumourTypes;
    }

    public CancerGeneAssociation setGermlineTumourTypes(List<String> germlineTumourTypes) {
        this.germlineTumourTypes = germlineTumourTypes;
        return this;
    }

    public List<String> getSyndromes() {
        return syndromes;
    }

    public CancerGeneAssociation setSyndromes(List<String> syndromes) {
        this.syndromes = syndromes;
        return this;
    }

    public List<String> getTissues() {
        return tissues;
    }

    public CancerGeneAssociation setTissues(List<String> tissues) {
        this.tissues = tissues;
        return this;
    }

    public List<ClinicalProperty.ModeOfInheritance> getModeOfInheritance() {
        return modeOfInheritance;
    }

    public CancerGeneAssociation setModeOfInheritance(List<ClinicalProperty.ModeOfInheritance> modeOfInheritance) {
        this.modeOfInheritance = modeOfInheritance;
        return this;
    }

    public List<ClinicalProperty.RoleInCancer> getRoleInCancer() {
        return roleInCancer;
    }

    public CancerGeneAssociation setRoleInCancer(List<ClinicalProperty.RoleInCancer> roleInCancer) {
        this.roleInCancer = roleInCancer;
        return this;
    }

    public List<String> getMutationTypes() {
        return mutationTypes;
    }

    public CancerGeneAssociation setMutationTypes(List<String> mutationTypes) {
        this.mutationTypes = mutationTypes;
        return this;
    }

    public List<String> getTranslocationPartners() {
        return translocationPartners;
    }

    public CancerGeneAssociation setTranslocationPartners(List<String> translocationPartners) {
        this.translocationPartners = translocationPartners;
        return this;
    }

    public List<String> getOtherSyndromes() {
        return otherSyndromes;
    }

    public CancerGeneAssociation setOtherSyndromes(List<String> otherSyndromes) {
        this.otherSyndromes = otherSyndromes;
        return this;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public CancerGeneAssociation setSynonyms(String synonyms) {
        this.synonyms = synonyms;
        return this;
    }
}
