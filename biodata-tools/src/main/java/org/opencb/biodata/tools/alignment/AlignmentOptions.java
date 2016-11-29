package org.opencb.biodata.tools.alignment;

/**
 * Created by pfurio on 26/10/16.
 */
public class AlignmentOptions {

    private boolean contained;
    private boolean binQualities;
    private boolean calculateMD;
    private int limit;

    public static final int DEFAULT_LIMIT = 50000;

    public AlignmentOptions() {
        this(false, false, false, DEFAULT_LIMIT);
    }

    public AlignmentOptions(boolean contained, boolean binQualities, boolean calculateMD, int limit) {
        this.contained = contained;
        this.binQualities = binQualities;
        this.calculateMD = calculateMD;
        this.limit = limit;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlignmentOptions{");
        sb.append("contained=").append(contained);
        sb.append(", binQualities=").append(binQualities);
        sb.append(", calculateMD=").append(calculateMD);
        sb.append(", limit=").append(limit);
        sb.append('}');
        return sb.toString();
    }

    public AlignmentOptions setContained(boolean contained) {
        this.contained = contained;
        return this;
    }

    public AlignmentOptions setBinQualities(boolean binQualities) {
        this.binQualities = binQualities;
        return this;
    }

    public AlignmentOptions setCalculateMD(boolean calculateMD) {
        this.calculateMD = calculateMD;
        return this;
    }

    public AlignmentOptions setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public boolean isContained() {
        return contained;
    }

    public boolean isBinQualities() {
        return binQualities;
    }

    public boolean isCalculateMD() {
        return calculateMD;
    }

    public int getLimit() {
        return limit;
    }
}
