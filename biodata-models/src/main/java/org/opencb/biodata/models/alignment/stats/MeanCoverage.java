package org.opencb.biodata.models.alignment.stats;

import org.opencb.biodata.models.feature.Region;

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
    private Region region;
    private float coverage;

    public MeanCoverage() {
    }

    public MeanCoverage(int size, String name, Region region, float coverage) {
        this.size = size;
        this.name = name;
        this.region = region;
        this.coverage = coverage;
    }

    public MeanCoverage(String name, String chromosome, int regionId, float coverage) {
        this.name = name;
        this.coverage = coverage;
        size = 1;
        String numerical = name;
        switch(name.charAt(name.length() - 1)){
            case 'M':
                size = 1000000;
                numerical = name.substring(0, name.length()-1);
                break;
            case 'K':
                size = 1000;
                numerical = name.substring(0, name.length()-1);
                break;
        }
        size = (int) (size * Float.parseFloat(numerical));
        region = new Region(chromosome, regionId*size+1, regionId*size+size);
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public float getCoverage() {
        return coverage;
    }

    public void setCoverage(float coverage) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeanCoverage)) return false;

        MeanCoverage that = (MeanCoverage) o;

        if (Float.compare(that.coverage, coverage) != 0) return false;
        if (size != that.size) return false;
        if (!name.equals(that.name)) return false;
        if (!region.equals(that.region)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + name.hashCode();
        result = 31 * result + region.hashCode();
        result = 31 * result + (coverage != +0.0f ? Float.floatToIntBits(coverage) : 0);
        return result;
    }

}