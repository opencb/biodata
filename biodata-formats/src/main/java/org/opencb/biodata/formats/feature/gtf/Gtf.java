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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gtf)) return false;

        Gtf gtf = (Gtf) o;

        if (start != gtf.start) return false;
        if (end != gtf.end) return false;
        if (sequenceName != null ? !sequenceName.equals(gtf.sequenceName) : gtf.sequenceName != null) return false;
        if (source != null ? !source.equals(gtf.source) : gtf.source != null) return false;
        if (feature != null ? !feature.equals(gtf.feature) : gtf.feature != null) return false;
        if (score != null ? !score.equals(gtf.score) : gtf.score != null) return false;
        if (strand != null ? !strand.equals(gtf.strand) : gtf.strand != null) return false;
        if (frame != null ? !frame.equals(gtf.frame) : gtf.frame != null) return false;
        return !(attributes != null ? !attributes.equals(gtf.attributes) : gtf.attributes != null);

    }

    @Override
    public int hashCode() {
        int result = sequenceName != null ? sequenceName.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (feature != null ? feature.hashCode() : 0);
        result = 31 * result + start;
        result = 31 * result + end;
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (strand != null ? strand.hashCode() : 0);
        result = 31 * result + (frame != null ? frame.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
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
