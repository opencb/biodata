package org.opencb.biodata.formats.sequence.fastqc;

import java.util.*;

public class FastQc {

    private Summary summary;
    private Map<String, String> basicStats;
    private List<PerBaseSeqQuality> perBaseSeqQualities;
    private Map<Integer, Double> perSeqQualityScores;
    private List<PerTileSeqQuality> perTileSeqQuality;
    private List<PerBaseSeqContent> perBaseSeqContent;
    private double[] perSeqGcContent;
    private Map<String, Double> perBaseNContent;
    private Map<Integer, Double> seqLengthDistribution;
    private List<SeqDuplicationLevel> seqDuplicationLevels;
    private List<OverrepresentedSeq> overrepresentedSeqs;
    private List<AdapterContent> adapterContent;
    private List<KmerContent> kmerContent;

    public FastQc() {
        summary = new Summary();
        basicStats = new LinkedHashMap<>();
        perBaseSeqQualities = new LinkedList<>();
        perSeqQualityScores = new LinkedHashMap<>();
        perTileSeqQuality = new LinkedList<>();
        perBaseSeqContent = new LinkedList<>();
        perSeqGcContent = new double[101];
        perBaseNContent = new LinkedHashMap<>();
        seqLengthDistribution = new LinkedHashMap<>();
        seqDuplicationLevels = new LinkedList<>();
        overrepresentedSeqs = new LinkedList<>();
        adapterContent = new LinkedList<>();
        kmerContent = new LinkedList<>();
    }

    public FastQc(Summary summary, Map<String, String> basicStats, List<PerBaseSeqQuality> perBaseSeqQualities,
                  Map<Integer, Double> perSeqQualityScores, List<PerTileSeqQuality> perTileSeqQuality,
                  List<PerBaseSeqContent> perBaseSeqContent, double[] perSeqGcContent, Map<String, Double> perBaseNContent,
                  Map<Integer, Double> seqLengthDistribution, List<SeqDuplicationLevel> seqDuplicationLevels,
                  List<OverrepresentedSeq> overrepresentedSeqs, List<AdapterContent> adapterContent,
                  List<KmerContent> kmerContent) {
        this.summary = summary;
        this.basicStats = basicStats;
        this.perBaseSeqQualities = perBaseSeqQualities;
        this.perSeqQualityScores = perSeqQualityScores;
        this.perTileSeqQuality = perTileSeqQuality;
        this.perBaseSeqContent = perBaseSeqContent;
        this.perSeqGcContent = perSeqGcContent;
        this.perBaseNContent = perBaseNContent;
        this.seqLengthDistribution = seqLengthDistribution;
        this.seqDuplicationLevels = seqDuplicationLevels;
        this.overrepresentedSeqs = overrepresentedSeqs;
        this.adapterContent = adapterContent;
        this.kmerContent = kmerContent;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FastQc{");
        sb.append("summary=").append(summary);
        sb.append(", basicStats=").append(basicStats);
        sb.append(", perBaseSeqQualities=").append(perBaseSeqQualities);
        sb.append(", perSeqQualityScores=").append(perSeqQualityScores);
        sb.append(", perTileSeqQuality=").append(perTileSeqQuality);
        sb.append(", perBaseSeqContent=").append(perBaseSeqContent);
        sb.append(", perSeqGcContent=").append(Arrays.toString(perSeqGcContent));
        sb.append(", perBaseNContent=").append(perBaseNContent);
        sb.append(", seqLengthDistribution=").append(seqLengthDistribution);
        sb.append(", seqDuplicationLevels=").append(seqDuplicationLevels);
        sb.append(", overrepresentedSeqs=").append(overrepresentedSeqs);
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

    public List<PerBaseSeqQuality> getPerBaseSeqQualities() {
        return perBaseSeqQualities;
    }

    public FastQc setPerBaseSeqQualities(List<PerBaseSeqQuality> perBaseSeqQualities) {
        this.perBaseSeqQualities = perBaseSeqQualities;
        return this;
    }

    public Map<Integer, Double> getPerSeqQualityScores() {
        return perSeqQualityScores;
    }

    public FastQc setPerSeqQualityScores(Map<Integer, Double> perSeqQualityScores) {
        this.perSeqQualityScores = perSeqQualityScores;
        return this;
    }

    public List<PerTileSeqQuality> getPerTileSeqQualities() {
        return perTileSeqQuality;
    }

    public FastQc setPerTileSeqQuality(List<PerTileSeqQuality> perTileSeqQuality) {
        this.perTileSeqQuality = perTileSeqQuality;
        return this;
    }

    public List<PerBaseSeqContent> getPerBaseSeqContent() {
        return perBaseSeqContent;
    }

    public FastQc setPerBaseSeqContent(List<PerBaseSeqContent> perBaseSeqContent) {
        this.perBaseSeqContent = perBaseSeqContent;
        return this;
    }

    public double[] getPerSeqGcContent() {
        return perSeqGcContent;
    }

    public FastQc setPerSeqGcContent(double[] perSeqGcContent) {
        this.perSeqGcContent = perSeqGcContent;
        return this;
    }

    public Map<String, Double> getPerBaseNContent() {
        return perBaseNContent;
    }

    public FastQc setPerBaseNContent(Map<String, Double> perBaseNContent) {
        this.perBaseNContent = perBaseNContent;
        return this;
    }

    public Map<Integer, Double> getSeqLengthDistribution() {
        return seqLengthDistribution;
    }

    public FastQc setSeqLengthDistribution(Map<Integer, Double> seqLengthDistribution) {
        this.seqLengthDistribution = seqLengthDistribution;
        return this;
    }

    public List<SeqDuplicationLevel> getSeqDuplicationLevels() {
        return seqDuplicationLevels;
    }

    public FastQc setSeqDuplicationLevels(List<SeqDuplicationLevel> seqDuplicationLevels) {
        this.seqDuplicationLevels = seqDuplicationLevels;
        return this;
    }

    public List<OverrepresentedSeq> getOverrepresentedSeqs() {
        return overrepresentedSeqs;
    }

    public FastQc setOverrepresentedSeqs(List<OverrepresentedSeq> overrepresentedSeqs) {
        this.overrepresentedSeqs = overrepresentedSeqs;
        return this;
    }

    public List<AdapterContent> getAdapterContent() {
        return adapterContent;
    }

    public FastQc setAdapterContent(List<AdapterContent> adapterContent) {
        this.adapterContent = adapterContent;
        return this;
    }

    public List<KmerContent> getKmerContent() {
        return kmerContent;
    }

    public FastQc setKmerContent(List<KmerContent> kmerContent) {
        this.kmerContent = kmerContent;
        return this;
    }
}
