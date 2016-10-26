package org.opencb.biodata.tools.alignment;

/**
 * Created by pfurio on 26/10/16.
 */
public class AlignmentOptions {
    private boolean contained;
    private boolean binQualities;
    private boolean updateMD;
    private int maxNumberRecords;

    public AlignmentOptions() {
        this.contained = true;
        this.binQualities = false;
        this.updateMD = false;
        this.maxNumberRecords = 50000;
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

    public AlignmentOptions setMaxNumberRecords(int maxNumberRecords) {
        this.maxNumberRecords = maxNumberRecords;
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

    public int getMaxNumberRecords() {
        return maxNumberRecords;
    }
}
