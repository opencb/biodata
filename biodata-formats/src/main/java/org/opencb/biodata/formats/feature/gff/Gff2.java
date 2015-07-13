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

package org.opencb.biodata.formats.feature.gff;

public class Gff2 {

    private String sequenceName;
    private String source;
    private String feature;
    private int start;
    private int end;
    private String score;
    private String strand;
    private String frame;
    private String attribute;

    /**
     * @param sequenceName
     * @param source
     * @param feature
     * @param start
     * @param end
     * @param score
     * @param strand
     * @param frame
     * @param attribute
     */
    public Gff2(String sequenceName, String source, String feature, int start, int end, String score, String strand, String frame, String attribute) {
        this.sequenceName = sequenceName;
        this.source = source;
        this.feature = feature;
        this.start = start;
        this.end = end;
        this.score = score;
        this.strand = strand;
        this.frame = frame;
        this.attribute = attribute;
    }

    /**
     * @param sequenceName
     * @param source
     * @param feature
     * @param start
     * @param end
     * @param score
     * @param strand
     * @param frame
     */
    public Gff2(String sequenceName, String source, String feature, Integer start, Integer end, String score, String strand, String frame) {
        this(sequenceName, source, feature, start, end, score, strand, frame, "");
    }

    /**
     * @param sequenceName
     * @param source
     * @param feature
     * @param start
     * @param end
     * @param score
     * @param strand
     * @param frame
     * @param attribute
     */
    public Gff2(String sequenceName, String source, String feature, Integer start, Integer end, String score, String strand, String frame, String attribute) {
        this.sequenceName = sequenceName;
        this.source = source;
        this.feature = feature;
        this.start = start;
        this.end = end;
        this.score = score;
        this.strand = strand;
        this.frame = frame;
        this.attribute = attribute;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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
        builder.append(attribute);
        return builder.toString();
    }


    /**
     * @return the sequenceName
     */
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * @param sequenceName the sequenceName to set
     */
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the feature
     */
    public String getFeature() {
        return feature;
    }

    /**
     * @param feature the feature to set
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @return the score
     */
    public String getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * @return the strand
     */
    public String getStrand() {
        return strand;
    }

    /**
     * @param strand the strand to set
     */
    public void setStrand(String strand) {
        this.strand = strand;
    }

    /**
     * @return the frame
     */
    public String getFrame() {
        return frame;
    }

    /**
     * @param frame the frame to set
     */
    public void setFrame(String frame) {
        this.frame = frame;
    }

    /**
     * @return the group
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param group the group to set
     */
    public void setAttribute(String group) {
        this.attribute = group;
    }
}
