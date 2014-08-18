package org.opencb.biodata.formats.feature.gtf;

import java.util.Map;

public class Gtf {

    private String sequenceName;
    private String source;
    private String feature;
    private int start;
    private int end;
    private String score;
    private String strand;
    private String frame;
    private Map<String, String> attributes;

    public Gtf(String sequenceName, String source, String feature, int start, int end, String score, String strand, String frame, Map<String, String> attributes) {
        super();
        this.sequenceName = sequenceName;
        this.source = source;
        this.feature = feature;
        this.start = start;
        this.end = end;
        this.score = score;
        this.strand = strand;
        this.frame = frame;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(sequenceName).append("\t");
        builder.append(source).append("\t");
        builder.append(feature).append("\t");
        builder.append(start).append("\t");
        builder.append(end).append("\t");
        builder.append(score).append("\t");
        builder.append(strand).append("\t");
        builder.append(frame).append("\t");
        builder.append(attributes.toString());
        return builder.toString();
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
