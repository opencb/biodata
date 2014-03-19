package org.opencb.biodata.formats.sequence.qseq;

import java.util.Arrays;


public class Qual {

    /**
     * Qual ID
     */
    private String id;

    /**
     * Qual Description
     */
    private String description;

    /**
     * Qualities vector
     */
    private int[] qualityArray;

    public static final String SEQ_ID_CHAR = ">";

    public Qual(String id, String description, int[] qual) {
        super();
        this.id = id;
        this.description = description;
        this.qualityArray = qual;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int[] getQualityArray() {
        return qualityArray;
    }

    public void setQualityArray(int[] qualityArray) {
        this.qualityArray = qualityArray;
    }

    public int size() {
        return this.qualityArray.length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(Qual.SEQ_ID_CHAR + this.id);
        if (this.description != null && !this.description.equals("")) {
            sb.append(" " + this.description);
        }

        sb.append("\n" + Arrays.toString(this.qualityArray));
        return (sb.toString());
    }

}
