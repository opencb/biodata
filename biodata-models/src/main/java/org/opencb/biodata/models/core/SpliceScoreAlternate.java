package org.opencb.biodata.models.core;

import java.util.HashMap;
import java.util.Map;

public class SpliceScoreAlternate {
    private String altAllele;
    private Map<String, Double> scores;

    public SpliceScoreAlternate() {
        scores = new HashMap<>();
    }

    public SpliceScoreAlternate(String altAllele, Map<String, Double> scores) {
        this.altAllele = altAllele;
        this.scores = scores;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SpliceScoreAlternate{");
        sb.append("altAllele='").append(altAllele).append('\'');
        sb.append(", scores=").append(scores);
        sb.append('}');
        return sb.toString();
    }

    public String getAltAllele() {
        return altAllele;
    }

    public SpliceScoreAlternate setAltAllele(String altAllele) {
        this.altAllele = altAllele;
        return this;
    }

    public Map<String, Double> getScores() {
        return scores;
    }

    public SpliceScoreAlternate setScores(Map<String, Double> scores) {
        this.scores = scores;
        return this;
    }
}
