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

package org.opencb.biodata.models.alignment.stats;

import org.opencb.biodata.models.core.Region;

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

    public MeanCoverage(int size, Region region, float coverage) {
        this.size = size;
        this.region = region;
        this.coverage = coverage;
        this.name = sizeToNameConvert(size);
    }

    public MeanCoverage(String name, String chromosome, int regionId, float coverage) {
        this.name = name = name.toLowerCase();
        this.coverage = coverage;
        this.size = nameToSizeConvert(name);
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

    public static String sizeToNameConvert(int size){
        String chunkId;
        if(size%1000000 == 0 && size/1000000 != 0){
            chunkId = size/1000000+"m";
        } else if(size%1000 == 0 && size/1000 != 0){
            chunkId = size/1000+"k";
        } else {
            chunkId = size+"";
        }
        return chunkId;
    }
    public static int nameToSizeConvert(String name){
        int size = 1;
        String numerical = name;
        switch(name.charAt(name.length() - 1)){
            case 'm':
                size = 1000000;
                numerical = name.substring(0, name.length()-1);
                break;
            case 'k':
                size = 1000;
                numerical = name.substring(0, name.length()-1);
                break;
        }
        return (int) (size * Integer.parseInt(numerical));
    }

}