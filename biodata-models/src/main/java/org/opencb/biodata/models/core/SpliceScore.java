package org.opencb.biodata.models.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpliceScore {
    private String chromosome;
    private int position;
    private String refAllele;
    private String geneId;
    private String geneName;
    private String transcritptId;
    private String exonId;
    private int exonNumber;
    private String source;
    private List<AlternateSpliceScore> alternates;

    public SpliceScore() {
        this.alternates = new ArrayList<>();
    }

    public SpliceScore(String chromosome, int position, String refAllele, String geneId, String geneName, String transcritptId,
                       String exonId, int exonNumber, String source, List<AlternateSpliceScore> alternates) {
        this.chromosome = chromosome;
        this.position = position;
        this.refAllele = refAllele;
        this.geneId = geneId;
        this.geneName = geneName;
        this.transcritptId = transcritptId;
        this.exonId = exonId;
        this.exonNumber = exonNumber;
        this.source = source;
        this.alternates = alternates;
    }

    public static class AlternateSpliceScore {
        private String altAllele;
        private Map<String, Object> scores;

        public AlternateSpliceScore() {
            this.scores = new HashMap<>();
        }

        public AlternateSpliceScore(String altAllele, Map<String, Object> scores) {
            this.altAllele = altAllele;
            this.scores = scores;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("AlternateSpliceScore{");
            sb.append("altAllele='").append(altAllele).append('\'');
            sb.append(", scores=").append(scores);
            sb.append('}');
            return sb.toString();
        }

        public String getAltAllele() {
            return altAllele;
        }

        public AlternateSpliceScore setAltAllele(String altAllele) {
            this.altAllele = altAllele;
            return this;
        }

        public Map<String, Object> getScores() {
            return scores;
        }

        public AlternateSpliceScore setScores(Map<String, Object> scores) {
            this.scores = scores;
            return this;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SpliceScore{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", refAllele='").append(refAllele).append('\'');
        sb.append(", geneId='").append(geneId).append('\'');
        sb.append(", geneName='").append(geneName).append('\'');
        sb.append(", transcritptId='").append(transcritptId).append('\'');
        sb.append(", exonId='").append(exonId).append('\'');
        sb.append(", exonNumber=").append(exonNumber);
        sb.append(", source='").append(source).append('\'');
        sb.append(", alternates=").append(alternates);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public SpliceScore setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public SpliceScore setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getRefAllele() {
        return refAllele;
    }

    public SpliceScore setRefAllele(String refAllele) {
        this.refAllele = refAllele;
        return this;
    }

    public String getGeneId() {
        return geneId;
    }

    public SpliceScore setGeneId(String geneId) {
        this.geneId = geneId;
        return this;
    }

    public String getGeneName() {
        return geneName;
    }

    public SpliceScore setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public String getTranscritptId() {
        return transcritptId;
    }

    public SpliceScore setTranscritptId(String transcritptId) {
        this.transcritptId = transcritptId;
        return this;
    }

    public String getExonId() {
        return exonId;
    }

    public SpliceScore setExonId(String exonId) {
        this.exonId = exonId;
        return this;
    }

    public int getExonNumber() {
        return exonNumber;
    }

    public SpliceScore setExonNumber(int exonNumber) {
        this.exonNumber = exonNumber;
        return this;
    }

    public String getSource() {
        return source;
    }

    public SpliceScore setSource(String source) {
        this.source = source;
        return this;
    }

    public List<AlternateSpliceScore> getAlternates() {
        return alternates;
    }

    public SpliceScore setAlternates(List<AlternateSpliceScore> alternates) {
        this.alternates = alternates;
        return this;
    }
}
