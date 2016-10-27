package org.opencb.biodata.tools.alignment;

/**
 * Created by pfurio on 26/10/16.
 */
public class AlignmentOptions {
    private boolean contained;
    private boolean binQualities;
    private boolean updateMD;
    private int limit;

    public AlignmentOptions() {
        this.contained = true;
        this.binQualities = false;
        this.updateMD = false;
        this.limit = 50000;
    }

    public AlignmentOptions setContained(boolean contained) {
        this.contained = contained;
        return this;
    }

    public AlignmentOptions setBinQualities(boolean binQualities) {
        this.binQualities = binQualities;
        return this;
    }

    public AlignmentOptions setUpdateMD(boolean updateMD) {
        this.updateMD = updateMD;
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

    public boolean isUpdateMD() {
        return updateMD;
    }

    public int getLimit() {
        return limit;
    }
}
