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

public class ChimerKb {

    public static final String SOURCE  = "chimerkb";

    // 0    1               2       3           4           5               6               7       8       9          10
    // id	ChimerDB_Type	Source	webSource	Fusion_pair	5Gene_Junction	3Gene_Junction	H_gene	H_chr	H_position	H_strand
    // 11       12      13          14          15                  16                  17              18
    // T_gene	T_chr	T_position	T_strand	Genomic_breakpoint	Exonic_breakpoint	Breakpoint_Type	Genome_Build_Version
    // 19   20      21          22      23          24      25          26                  27          28
    // PMID	Disease	Validation	Frame	Chr_info	Kinase	Oncogene	Tumor_suppressor	Receptor	Transcription_Factor
    // 29           30          31
    // ChimerPub	ChimerSeq	ChimerSeq+

    private String id;
    private String chimerDbType;
    private String chimerSource;
    private String webSource;
    private String fusionPair;
    private String fiveGeneJunction;
    private String threeGeneJunction;
    private ChimerKbGeneBreakpoint headGene;
    private ChimerKbGeneBreakpoint tailGene;
    private boolean genomicBreakpoint;
    private boolean exonicBreakpoint;
    private String breakpointType;
    private String genomeBuildVersion;
    private List<String> pmid;
    private List<String> disease;
    private List<String> validation;
    private String frame;
    private String chrInfo;
    private boolean kinase;
    private boolean oncogene;
    private boolean tumorSuppressor;
    private boolean receptor;
    private boolean transcriptionFactor;
    private boolean chimerPub;
    private boolean chimerSeq;
    private boolean chimerSeqPlus;

    private String source;

    public ChimerKb() {
        this.pmid = new ArrayList<>();
        this.disease = new ArrayList<>();
        this.validation = new ArrayList<>();

        this.source = SOURCE;
    }

    public ChimerKb(String id, String chimerDbType, String chimerSource, String webSource, String fusionPair, String fiveGeneJunction,
                    String threeGeneJunction, ChimerKbGeneBreakpoint headGene, ChimerKbGeneBreakpoint tailGene, boolean genomicBreakpoint,
                    boolean exonicBreakpoint, String breakpointType, String genomeBuildVersion, List<String> pmid, List<String> disease,
                    List<String> validation, String frame, String chrInfo, boolean kinase, boolean oncogene, boolean tumorSuppressor,
                    boolean receptor, boolean transcriptionFactor, boolean chimerPub, boolean chimerSeq, boolean chimerSeqPlus,
                    String source) {
        this.id = id;
        this.chimerDbType = chimerDbType;
        this.chimerSource = chimerSource;
        this.webSource = webSource;
        this.fusionPair = fusionPair;
        this.fiveGeneJunction = fiveGeneJunction;
        this.threeGeneJunction = threeGeneJunction;
        this.headGene = headGene;
        this.tailGene = tailGene;
        this.genomicBreakpoint = genomicBreakpoint;
        this.exonicBreakpoint = exonicBreakpoint;
        this.breakpointType = breakpointType;
        this.genomeBuildVersion = genomeBuildVersion;
        this.pmid = pmid;
        this.disease = disease;
        this.validation = validation;
        this.frame = frame;
        this.chrInfo = chrInfo;
        this.kinase = kinase;
        this.oncogene = oncogene;
        this.tumorSuppressor = tumorSuppressor;
        this.receptor = receptor;
        this.transcriptionFactor = transcriptionFactor;
        this.chimerPub = chimerPub;
        this.chimerSeq = chimerSeq;
        this.chimerSeqPlus = chimerSeqPlus;
        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChimerKb{");
        sb.append("id='").append(id).append('\'');
        sb.append(", chimerDbType='").append(chimerDbType).append('\'');
        sb.append(", chimerSource='").append(chimerSource).append('\'');
        sb.append(", webSource='").append(webSource).append('\'');
        sb.append(", fusionPair='").append(fusionPair).append('\'');
        sb.append(", fiveGeneJunction='").append(fiveGeneJunction).append('\'');
        sb.append(", threeGeneJunction='").append(threeGeneJunction).append('\'');
        sb.append(", headGene=").append(headGene);
        sb.append(", tailGene=").append(tailGene);
        sb.append(", genomicBreakpoint='").append(genomicBreakpoint).append('\'');
        sb.append(", exonicBreakpoint='").append(exonicBreakpoint).append('\'');
        sb.append(", breakpointType='").append(breakpointType).append('\'');
        sb.append(", genomeBuildVersion='").append(genomeBuildVersion).append('\'');
        sb.append(", pmid=").append(pmid);
        sb.append(", disease=").append(disease);
        sb.append(", validation=").append(validation);
        sb.append(", frame='").append(frame).append('\'');
        sb.append(", chrInfo='").append(chrInfo).append('\'');
        sb.append(", kinase=").append(kinase);
        sb.append(", oncogene=").append(oncogene);
        sb.append(", tumorSuppressor=").append(tumorSuppressor);
        sb.append(", receptor=").append(receptor);
        sb.append(", transcriptionFactor=").append(transcriptionFactor);
        sb.append(", chimerPub=").append(chimerPub);
        sb.append(", chimerSeq=").append(chimerSeq);
        sb.append(", chimerSeqPlus=").append(chimerSeqPlus);
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public ChimerKb setId(String id) {
        this.id = id;
        return this;
    }

    public String getChimerDbType() {
        return chimerDbType;
    }

    public ChimerKb setChimerDbType(String chimerDbType) {
        this.chimerDbType = chimerDbType;
        return this;
    }

    public String getChimerSource() {
        return chimerSource;
    }

    public ChimerKb setChimerSource(String chimerSource) {
        this.chimerSource = chimerSource;
        return this;
    }

    public String getWebSource() {
        return webSource;
    }

    public ChimerKb setWebSource(String webSource) {
        this.webSource = webSource;
        return this;
    }

    public String getFusionPair() {
        return fusionPair;
    }

    public ChimerKb setFusionPair(String fusionPair) {
        this.fusionPair = fusionPair;
        return this;
    }

    public String getFiveGeneJunction() {
        return fiveGeneJunction;
    }

    public ChimerKb setFiveGeneJunction(String fiveGeneJunction) {
        this.fiveGeneJunction = fiveGeneJunction;
        return this;
    }

    public String getThreeGeneJunction() {
        return threeGeneJunction;
    }

    public ChimerKb setThreeGeneJunction(String threeGeneJunction) {
        this.threeGeneJunction = threeGeneJunction;
        return this;
    }

    public ChimerKbGeneBreakpoint getHeadGene() {
        return headGene;
    }

    public ChimerKb setHeadGene(ChimerKbGeneBreakpoint headGene) {
        this.headGene = headGene;
        return this;
    }

    public ChimerKbGeneBreakpoint getTailGene() {
        return tailGene;
    }

    public ChimerKb setTailGene(ChimerKbGeneBreakpoint tailGene) {
        this.tailGene = tailGene;
        return this;
    }

    public boolean getGenomicBreakpoint() {
        return genomicBreakpoint;
    }

    public ChimerKb setGenomicBreakpoint(boolean genomicBreakpoint) {
        this.genomicBreakpoint = genomicBreakpoint;
        return this;
    }

    public boolean getExonicBreakpoint() {
        return exonicBreakpoint;
    }

    public ChimerKb setExonicBreakpoint(boolean exonicBreakpoint) {
        this.exonicBreakpoint = exonicBreakpoint;
        return this;
    }

    public String getBreakpointType() {
        return breakpointType;
    }

    public ChimerKb setBreakpointType(String breakpointType) {
        this.breakpointType = breakpointType;
        return this;
    }

    public String getGenomeBuildVersion() {
        return genomeBuildVersion;
    }

    public ChimerKb setGenomeBuildVersion(String genomeBuildVersion) {
        this.genomeBuildVersion = genomeBuildVersion;
        return this;
    }

    public List<String> getPmid() {
        return pmid;
    }

    public ChimerKb setPmid(List<String> pmid) {
        this.pmid = pmid;
        return this;
    }

    public List<String> getDisease() {
        return disease;
    }

    public ChimerKb setDisease(List<String> disease) {
        this.disease = disease;
        return this;
    }

    public List<String> getValidation() {
        return validation;
    }

    public ChimerKb setValidation(List<String> validation) {
        this.validation = validation;
        return this;
    }

    public String getFrame() {
        return frame;
    }

    public ChimerKb setFrame(String frame) {
        this.frame = frame;
        return this;
    }

    public String getChrInfo() {
        return chrInfo;
    }

    public ChimerKb setChrInfo(String chrInfo) {
        this.chrInfo = chrInfo;
        return this;
    }

    public boolean isKinase() {
        return kinase;
    }

    public ChimerKb setKinase(boolean kinase) {
        this.kinase = kinase;
        return this;
    }

    public boolean isOncogene() {
        return oncogene;
    }

    public ChimerKb setOncogene(boolean oncogene) {
        this.oncogene = oncogene;
        return this;
    }

    public boolean isTumorSuppressor() {
        return tumorSuppressor;
    }

    public ChimerKb setTumorSuppressor(boolean tumorSuppressor) {
        this.tumorSuppressor = tumorSuppressor;
        return this;
    }

    public boolean isReceptor() {
        return receptor;
    }

    public ChimerKb setReceptor(boolean receptor) {
        this.receptor = receptor;
        return this;
    }

    public boolean isTranscriptionFactor() {
        return transcriptionFactor;
    }

    public ChimerKb setTranscriptionFactor(boolean transcriptionFactor) {
        this.transcriptionFactor = transcriptionFactor;
        return this;
    }

    public boolean isChimerPub() {
        return chimerPub;
    }

    public ChimerKb setChimerPub(boolean chimerPub) {
        this.chimerPub = chimerPub;
        return this;
    }

    public boolean isChimerSeq() {
        return chimerSeq;
    }

    public ChimerKb setChimerSeq(boolean chimerSeq) {
        this.chimerSeq = chimerSeq;
        return this;
    }

    public boolean isChimerSeqPlus() {
        return chimerSeqPlus;
    }

    public ChimerKb setChimerSeqPlus(boolean chimerSeqPlus) {
        this.chimerSeqPlus = chimerSeqPlus;
        return this;
    }

    public String getSource() {
        return source;
    }

    public ChimerKb setSource(String source) {
        this.source = source;
        return this;
    }
}
