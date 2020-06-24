package org.opencb.biodata.formats.sequence.fastqc;

import java.util.*;

public class FastQc {

    private Summary summary;
    private Map<String, String> basicStats;
    private PerBaseSeqQuality perBaseSeqQuality;
    private PerSeqQualityScore perSeqQualityScore;
    private PerTileSeqQuality perTileSeqQuality;
    private PerBaseSeqContent perBaseSeqContent;
    private PerSeqGcContent perSeqGcContent;
    private PerBaseNContent perBaseNContent;
    private SeqLengthDistribution seqLengthDistribution;
    private SeqDuplicationLevel seqDuplicationLevel;
    private List<OverrepresentedSeq> overrepresentedSeq;
    private AdapterContent adapterContent;
    private KmerContent kmerContent;

    public FastQc() {
        summary = new Summary();
        basicStats = new LinkedHashMap<>();
        perBaseSeqQuality = new PerBaseSeqQuality();
        perSeqQualityScore = new PerSeqQualityScore();
        perTileSeqQuality = new PerTileSeqQuality();
        perBaseSeqContent = new PerBaseSeqContent();
        perSeqGcContent = new PerSeqGcContent();
        perBaseNContent = new PerBaseNContent();
        seqLengthDistribution = new SeqLengthDistribution();
        seqDuplicationLevel = new SeqDuplicationLevel();
        overrepresentedSeq = new LinkedList<>();
        adapterContent = new AdapterContent();
        kmerContent = new KmerContent();
    }

    public FastQc(Summary summary, Map<String, String> basicStats, PerBaseSeqQuality perBaseSeqQuality,
                  PerSeqQualityScore perSeqQualityScore, PerTileSeqQuality perTileSeqQuality,
                  PerBaseSeqContent perBaseSeqContent, PerSeqGcContent perSeqGcContent, PerBaseNContent perBaseNContent,
                  SeqLengthDistribution seqLengthDistribution, SeqDuplicationLevel seqDuplicationLevel,
                  List<OverrepresentedSeq> overrepresentedSeq, AdapterContent adapterContent,
                  KmerContent kmerContent) {
        this.summary = summary;
        this.basicStats = basicStats;
        this.perBaseSeqQuality = perBaseSeqQuality;
        this.perSeqQualityScore = perSeqQualityScore;
        this.perTileSeqQuality = perTileSeqQuality;
        this.perBaseSeqContent = perBaseSeqContent;
        this.perSeqGcContent = perSeqGcContent;
        this.perBaseNContent = perBaseNContent;
        this.seqLengthDistribution = seqLengthDistribution;
        this.seqDuplicationLevel = seqDuplicationLevel;
        this.overrepresentedSeq = overrepresentedSeq;
        this.adapterContent = adapterContent;
        this.kmerContent = kmerContent;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FastQc{");
        sb.append("summary=").append(summary);
        sb.append(", basicStats=").append(basicStats);
        sb.append(", perBaseSeqQuality=").append(perBaseSeqQuality);
        sb.append(", perSeqQualityScore=").append(perSeqQualityScore);
        sb.append(", perTileSeqQuality=").append(perTileSeqQuality);
        sb.append(", perBaseSeqContent=").append(perBaseSeqContent);
        sb.append(", perSeqGcContent=").append(perSeqGcContent);
        sb.append(", perBaseNContent=").append(perBaseNContent);
        sb.append(", seqLengthDistribution=").append(seqLengthDistribution);
        sb.append(", seqDuplicationLevel=").append(seqDuplicationLevel);
        sb.append(", overrepresentedSeq=").append(overrepresentedSeq);
        sb.append(", adapterContent=").append(adapterContent);
        sb.append(", kmerContent=").append(kmerContent);
        sb.append('}');
        return sb.toString();
    }

    public Summary getSummary() {
        return summary;
    }

    public FastQc setSummary(Summary summary) {
        this.summary = summary;
        return this;
    }

    public Map<String, String> getBasicStats() {
        return basicStats;
    }

    public FastQc setBasicStats(Map<String, String> basicStats) {
        this.basicStats = basicStats;
        return this;
    }

    public PerBaseSeqQuality getPerBaseSeqQuality() {
        return perBaseSeqQuality;
    }

    public FastQc setPerBaseSeqQuality(PerBaseSeqQuality perBaseSeqQuality) {
        this.perBaseSeqQuality = perBaseSeqQuality;
        return this;
    }

    public PerSeqQualityScore getPerSeqQualityScore() {
        return perSeqQualityScore;
    }

    public FastQc setPerSeqQualityScore(PerSeqQualityScore perSeqQualityScore) {
        this.perSeqQualityScore = perSeqQualityScore;
        return this;
    }

    public PerTileSeqQuality getPerTileSeqQuality() {
        return perTileSeqQuality;
    }

    public FastQc setPerTileSeqQuality(PerTileSeqQuality perTileSeqQuality) {
        this.perTileSeqQuality = perTileSeqQuality;
        return this;
    }

    public PerBaseSeqContent getPerBaseSeqContent() {
        return perBaseSeqContent;
    }

    public FastQc setPerBaseSeqContent(PerBaseSeqContent perBaseSeqContent) {
        this.perBaseSeqContent = perBaseSeqContent;
        return this;
    }

    public PerSeqGcContent getPerSeqGcContent() {
        return perSeqGcContent;
    }

    public FastQc setPerSeqGcContent(PerSeqGcContent perSeqGcContent) {
        this.perSeqGcContent = perSeqGcContent;
        return this;
    }

    public PerBaseNContent getPerBaseNContent() {
        return perBaseNContent;
    }

    public FastQc setPerBaseNContent(PerBaseNContent perBaseNContent) {
        this.perBaseNContent = perBaseNContent;
        return this;
    }

    public SeqLengthDistribution getSeqLengthDistribution() {
        return seqLengthDistribution;
    }

    public FastQc setSeqLengthDistribution(SeqLengthDistribution seqLengthDistribution) {
        this.seqLengthDistribution = seqLengthDistribution;
        return this;
    }

    public SeqDuplicationLevel getSeqDuplicationLevel() {
        return seqDuplicationLevel;
    }

    public FastQc setSeqDuplicationLevel(SeqDuplicationLevel seqDuplicationLevel) {
        this.seqDuplicationLevel = seqDuplicationLevel;
        return this;
    }

    public List<OverrepresentedSeq> getOverrepresentedSeq() {
        return overrepresentedSeq;
    }

    public FastQc setOverrepresentedSeq(List<OverrepresentedSeq> overrepresentedSeq) {
        this.overrepresentedSeq = overrepresentedSeq;
        return this;
    }

    public AdapterContent getAdapterContent() {
        return adapterContent;
    }

    public FastQc setAdapterContent(AdapterContent adapterContent) {
        this.adapterContent = adapterContent;
        return this;
    }

    public KmerContent getKmerContent() {
        return kmerContent;
    }

    public FastQc setKmerContent(KmerContent kmerContent) {
        this.kmerContent = kmerContent;
        return this;
    }
}
