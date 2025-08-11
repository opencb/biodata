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

package org.opencb.biodata.models.core.chimerdb;

import java.util.ArrayList;
import java.util.List;

public class ChimerPub {

    public static final String SOURCE  = "chimerpub";

    // 0    1           2               3       4       5       6      7        8           9       10          11                  12
    // id	Fusion_pair	Translocation	H_gene	T_gene	PMID	Score  Disease	Validation	Kinase	Oncogene	Tumor_suppressor	Receptor
    // 13                   14          15          16          17                  18                  19
    // Transcription_Factor	ChimerKB	ChimerSeq	ChimerSeq+	Sentence_highlight	H_gene_highlight	T_gene_highlight
    // 20                   21
    // Disease_highlight	Validation_highlight

    private String id;
    private String fusionPair;
    private String translocation;
    private ChimerPubGeneBreakpoint headGene;
    private ChimerPubGeneBreakpoint tailGene;
    private List<String> pmid;
    private double score;
    private List<String> diseases;
    private List<String> validations;
    private boolean kinase;
    private boolean oncogene;
    private boolean tumorSuppressor;
    private boolean receptor;
    private boolean transcriptionFactor;
    private boolean chimerKb;
    private boolean chimerSeq;
    private boolean chimerSeqPlus;
    private String senteceHighlight;
    private String diseaseHighlight;
    private String validationHighlight;

    private String source;

    public ChimerPub() {
        this.headGene = new ChimerPubGeneBreakpoint();
        this.tailGene = new ChimerPubGeneBreakpoint();
        this.pmid = new ArrayList<>();
        this.diseases = new ArrayList<>();
        this.validations = new ArrayList<>();

        this.source = SOURCE;
    }

    public ChimerPub(String id, String fusionPair, String translocation, ChimerPubGeneBreakpoint headGene, ChimerPubGeneBreakpoint tailGene,
                     List<String> pmid, double score, List<String> diseases, List<String> validations, boolean kinase, boolean oncogene,
                     boolean tumorSuppressor, boolean receptor, boolean transcriptionFactor, boolean chimerKb, boolean chimerSeq,
                     boolean chimerSeqPlus, String senteceHighlight, String diseaseHighlight, String validationHighlight, String source) {
        this.id = id;
        this.fusionPair = fusionPair;
        this.translocation = translocation;
        this.headGene = headGene;
        this.tailGene = tailGene;
        this.pmid = pmid;
        this.score = score;
        this.diseases = diseases;
        this.validations = validations;
        this.kinase = kinase;
        this.oncogene = oncogene;
        this.tumorSuppressor = tumorSuppressor;
        this.receptor = receptor;
        this.transcriptionFactor = transcriptionFactor;
        this.chimerKb = chimerKb;
        this.chimerSeq = chimerSeq;
        this.chimerSeqPlus = chimerSeqPlus;
        this.senteceHighlight = senteceHighlight;
        this.diseaseHighlight = diseaseHighlight;
        this.validationHighlight = validationHighlight;
        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChimerPub{");
        sb.append("id='").append(id).append('\'');
        sb.append(", fusionPair='").append(fusionPair).append('\'');
        sb.append(", translocation='").append(translocation).append('\'');
        sb.append(", headGene=").append(headGene);
        sb.append(", tailGene=").append(tailGene);
        sb.append(", pmid=").append(pmid);
        sb.append(", score=").append(score);
        sb.append(", diseases=").append(diseases);
        sb.append(", validations=").append(validations);
        sb.append(", kinase=").append(kinase);
        sb.append(", oncogene=").append(oncogene);
        sb.append(", tumorSuppressor=").append(tumorSuppressor);
        sb.append(", receptor=").append(receptor);
        sb.append(", transcriptionFactor=").append(transcriptionFactor);
        sb.append(", chimerKb=").append(chimerKb);
        sb.append(", chimerSeq=").append(chimerSeq);
        sb.append(", chimerSeqPlus=").append(chimerSeqPlus);
        sb.append(", senteceHighlight='").append(senteceHighlight).append('\'');
        sb.append(", diseaseHighlight='").append(diseaseHighlight).append('\'');
        sb.append(", validationHighlight='").append(validationHighlight).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public ChimerPub setId(String id) {
        this.id = id;
        return this;
    }

    public String getFusionPair() {
        return fusionPair;
    }

    public ChimerPub setFusionPair(String fusionPair) {
        this.fusionPair = fusionPair;
        return this;
    }

    public String getTranslocation() {
        return translocation;
    }

    public ChimerPub setTranslocation(String translocation) {
        this.translocation = translocation;
        return this;
    }

    public ChimerPubGeneBreakpoint getHeadGene() {
        return headGene;
    }

    public ChimerPub setHeadGene(ChimerPubGeneBreakpoint headGene) {
        this.headGene = headGene;
        return this;
    }

    public ChimerPubGeneBreakpoint getTailGene() {
        return tailGene;
    }

    public ChimerPub setTailGene(ChimerPubGeneBreakpoint tailGene) {
        this.tailGene = tailGene;
        return this;
    }

    public List<String> getPmid() {
        return pmid;
    }

    public ChimerPub setPmid(List<String> pmid) {
        this.pmid = pmid;
        return this;
    }

    public double getScore() {
        return score;
    }

    public ChimerPub setScore(double score) {
        this.score = score;
        return this;
    }

    public List<String> getDiseases() {
        return diseases;
    }

    public ChimerPub setDiseases(List<String> diseases) {
        this.diseases = diseases;
        return this;
    }

    public List<String> getValidations() {
        return validations;
    }

    public ChimerPub setValidations(List<String> validations) {
        this.validations = validations;
        return this;
    }

    public boolean isKinase() {
        return kinase;
    }

    public ChimerPub setKinase(boolean kinase) {
        this.kinase = kinase;
        return this;
    }

    public boolean isOncogene() {
        return oncogene;
    }

    public ChimerPub setOncogene(boolean oncogene) {
        this.oncogene = oncogene;
        return this;
    }

    public boolean isTumorSuppressor() {
        return tumorSuppressor;
    }

    public ChimerPub setTumorSuppressor(boolean tumorSuppressor) {
        this.tumorSuppressor = tumorSuppressor;
        return this;
    }

    public boolean isReceptor() {
        return receptor;
    }

    public ChimerPub setReceptor(boolean receptor) {
        this.receptor = receptor;
        return this;
    }

    public boolean isTranscriptionFactor() {
        return transcriptionFactor;
    }

    public ChimerPub setTranscriptionFactor(boolean transcriptionFactor) {
        this.transcriptionFactor = transcriptionFactor;
        return this;
    }

    public boolean isChimerKb() {
        return chimerKb;
    }

    public ChimerPub setChimerKb(boolean chimerKb) {
        this.chimerKb = chimerKb;
        return this;
    }

    public boolean isChimerSeq() {
        return chimerSeq;
    }

    public ChimerPub setChimerSeq(boolean chimerSeq) {
        this.chimerSeq = chimerSeq;
        return this;
    }

    public boolean isChimerSeqPlus() {
        return chimerSeqPlus;
    }

    public ChimerPub setChimerSeqPlus(boolean chimerSeqPlus) {
        this.chimerSeqPlus = chimerSeqPlus;
        return this;
    }

    public String getSenteceHighlight() {
        return senteceHighlight;
    }

    public ChimerPub setSenteceHighlight(String senteceHighlight) {
        this.senteceHighlight = senteceHighlight;
        return this;
    }

    public String getDiseaseHighlight() {
        return diseaseHighlight;
    }

    public ChimerPub setDiseaseHighlight(String diseaseHighlight) {
        this.diseaseHighlight = diseaseHighlight;
        return this;
    }

    public String getValidationHighlight() {
        return validationHighlight;
    }

    public ChimerPub setValidationHighlight(String validationHighlight) {
        this.validationHighlight = validationHighlight;
        return this;
    }

    public String getSource() {
        return source;
    }

    public ChimerPub setSource(String source) {
        this.source = source;
        return this;
    }
}
