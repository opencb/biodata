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

public class ChimerSeq {

    public static final String SOURCE  = "chimerseq";

    // 0    1               2       3           4           5       6       7           8           9       10      11          12
    // id	ChimerDB_Type	Source	webSource	Fusion_pair	H_gene	H_chr	H_position	H_strand	T_gene	T_chr	T_position	T_strand
    // 13                   14                      15          16          17              18                  19                  20
    // Genomic_breakpoint	Genome_Build_Version	Cancertype	BarcodeID	Seed_reads_num	Spanning_pairs_num	Junction_reads_num	Frame
    // 21       22      23          24          25                  26          27                      28      29          30
    // Chr_info	H_locus	H_kinase	H_oncogene	H_tumor_suppressor	H_receptor	H_transcription_factor	T_locus	T_kinase	T_oncogene
    // 31                   32           33                     34          35          36
    // T_tumor_suppressor	T_receptor	T_transcription_factor	ChimerKB	ChimerPub	Highly_Reliable_Seq


    private String id;
    private String chimerDbType;
    private String chimerSource;
    private String webSource;
    private String fusionPair;
    private ChimerSeqGeneBreakpoint headGene;
    private ChimerSeqGeneBreakpoint tailGene;
    private String genomicBreakpoint;
    private String genomeBuildVersion;
    private String cancerType;
    private String barcodeId;
    private int seedReadsNum;
    private int spanningPairsNum;
    private int junctionReadsNum;
    private String frame;
    private String chrInfo;
    private boolean chimerKb;
    private boolean chimerPub;
    private boolean highlyReliableSeq;

    private String source;

    public ChimerSeq() {
        this.headGene = new ChimerSeqGeneBreakpoint();
        this.tailGene = new ChimerSeqGeneBreakpoint();

        this.source = SOURCE;
    }

    public ChimerSeq(String id, String chimerDbType, String chimerSource, String webSource, String fusionPair,
                     ChimerSeqGeneBreakpoint headGene, ChimerSeqGeneBreakpoint tailGene, String genomicBreakpoint,
                     String genomeBuildVersion, String cancerType, String barcodeId, int seedReadsNum, int spanningPairsNum,
                     int junctionReadsNum, String frame, String chrInfo, boolean chimerKb, boolean chimerPub, boolean highlyReliableSeq,
                     String source) {
        this.id = id;
        this.chimerDbType = chimerDbType;
        this.chimerSource = chimerSource;
        this.webSource = webSource;
        this.fusionPair = fusionPair;
        this.headGene = headGene;
        this.tailGene = tailGene;
        this.genomicBreakpoint = genomicBreakpoint;
        this.genomeBuildVersion = genomeBuildVersion;
        this.cancerType = cancerType;
        this.barcodeId = barcodeId;
        this.seedReadsNum = seedReadsNum;
        this.spanningPairsNum = spanningPairsNum;
        this.junctionReadsNum = junctionReadsNum;
        this.frame = frame;
        this.chrInfo = chrInfo;
        this.chimerKb = chimerKb;
        this.chimerPub = chimerPub;
        this.highlyReliableSeq = highlyReliableSeq;

        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChimerSeq{");
        sb.append("id='").append(id).append('\'');
        sb.append(", chimerDbType='").append(chimerDbType).append('\'');
        sb.append(", chimerSource='").append(chimerSource).append('\'');
        sb.append(", webSource='").append(webSource).append('\'');
        sb.append(", fusionPair='").append(fusionPair).append('\'');
        sb.append(", headGene=").append(headGene);
        sb.append(", tailGene=").append(tailGene);
        sb.append(", genomicBreakpoint='").append(genomicBreakpoint).append('\'');
        sb.append(", genomeBuildVersion='").append(genomeBuildVersion).append('\'');
        sb.append(", cancerType='").append(cancerType).append('\'');
        sb.append(", barcodeId='").append(barcodeId).append('\'');
        sb.append(", seedReadsNum=").append(seedReadsNum);
        sb.append(", spanningPairsNum=").append(spanningPairsNum);
        sb.append(", junctionReadsNum=").append(junctionReadsNum);
        sb.append(", frame='").append(frame).append('\'');
        sb.append(", chrInfo='").append(chrInfo).append('\'');
        sb.append(", chimerKb=").append(chimerKb);
        sb.append(", chimerPub=").append(chimerPub);
        sb.append(", highlyReliableSeq='").append(highlyReliableSeq).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public ChimerSeq setId(String id) {
        this.id = id;
        return this;
    }

    public String getChimerDbType() {
        return chimerDbType;
    }

    public ChimerSeq setChimerDbType(String chimerDbType) {
        this.chimerDbType = chimerDbType;
        return this;
    }

    public String getChimerSource() {
        return chimerSource;
    }

    public ChimerSeq setChimerSource(String chimerSource) {
        this.chimerSource = chimerSource;
        return this;
    }

    public String getWebSource() {
        return webSource;
    }

    public ChimerSeq setWebSource(String webSource) {
        this.webSource = webSource;
        return this;
    }

    public String getFusionPair() {
        return fusionPair;
    }

    public ChimerSeq setFusionPair(String fusionPair) {
        this.fusionPair = fusionPair;
        return this;
    }

    public ChimerSeqGeneBreakpoint getHeadGene() {
        return headGene;
    }

    public ChimerSeq setHeadGene(ChimerSeqGeneBreakpoint headGene) {
        this.headGene = headGene;
        return this;
    }

    public ChimerSeqGeneBreakpoint getTailGene() {
        return tailGene;
    }

    public ChimerSeq setTailGene(ChimerSeqGeneBreakpoint tailGene) {
        this.tailGene = tailGene;
        return this;
    }

    public String getGenomicBreakpoint() {
        return genomicBreakpoint;
    }

    public ChimerSeq setGenomicBreakpoint(String genomicBreakpoint) {
        this.genomicBreakpoint = genomicBreakpoint;
        return this;
    }

    public String getGenomeBuildVersion() {
        return genomeBuildVersion;
    }

    public ChimerSeq setGenomeBuildVersion(String genomeBuildVersion) {
        this.genomeBuildVersion = genomeBuildVersion;
        return this;
    }

    public String getCancerType() {
        return cancerType;
    }

    public ChimerSeq setCancerType(String cancerType) {
        this.cancerType = cancerType;
        return this;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public ChimerSeq setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
        return this;
    }

    public int getSeedReadsNum() {
        return seedReadsNum;
    }

    public ChimerSeq setSeedReadsNum(int seedReadsNum) {
        this.seedReadsNum = seedReadsNum;
        return this;
    }

    public int getSpanningPairsNum() {
        return spanningPairsNum;
    }

    public ChimerSeq setSpanningPairsNum(int spanningPairsNum) {
        this.spanningPairsNum = spanningPairsNum;
        return this;
    }

    public int getJunctionReadsNum() {
        return junctionReadsNum;
    }

    public ChimerSeq setJunctionReadsNum(int junctionReadsNum) {
        this.junctionReadsNum = junctionReadsNum;
        return this;
    }

    public String getFrame() {
        return frame;
    }

    public ChimerSeq setFrame(String frame) {
        this.frame = frame;
        return this;
    }

    public String getChrInfo() {
        return chrInfo;
    }

    public ChimerSeq setChrInfo(String chrInfo) {
        this.chrInfo = chrInfo;
        return this;
    }

    public boolean isChimerKb() {
        return chimerKb;
    }

    public ChimerSeq setChimerKb(boolean chimerKb) {
        this.chimerKb = chimerKb;
        return this;
    }

    public boolean isChimerPub() {
        return chimerPub;
    }

    public ChimerSeq setChimerPub(boolean chimerPub) {
        this.chimerPub = chimerPub;
        return this;
    }

    public boolean getHighlyReliableSeq() {
        return highlyReliableSeq;
    }

    public ChimerSeq setHighlyReliableSeq(boolean highlyReliableSeq) {
        this.highlyReliableSeq = highlyReliableSeq;
        return this;
    }

    public String getSource() {
        return source;
    }

    public ChimerSeq setSource(String source) {
        this.source = source;
        return this;
    }
}
