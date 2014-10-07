package org.opencb.biodata.models.alignment.stats;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 2/7/14
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */

public class MeanCoverage {
    private int size;
    private String name;
    private float[] coverage;
    private int initPosition;   //Position of the first coverage

    public MeanCoverage() {
    }

    public MeanCoverage(int size, String name) {
        this.size = size;
        this.name = name;
    }

    public int getInitPosition() {
        return initPosition;
    }

    public void setInitPosition(int initPosition) {
        this.initPosition = initPosition;
    }

    public float[] getCoverage() {
        return coverage;
    }

    public void setCoverage(float[] coverage) {
        this.coverage = coverage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}