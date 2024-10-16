/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.core;

import java.util.ArrayList;
import java.util.List;


public class Region {

    private String chromosome;
    private int start;
    private int end;

    private static final String MITOCHONDRIA_M = "M";

    public Region() {
        this(null, 0, Integer.MAX_VALUE);
    }

    public Region(String chromosome, int position) {
        this(chromosome, position, position);
    }

    public Region(String chromosome, int start, int end) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
    }

    public Region(String region) {
        if (region != null && !region.isEmpty()) {
            int idx1 = region.indexOf(':');
            if (idx1 > 0) {
                this.chromosome = region.substring(0, idx1);
                int idx2 = region.indexOf('-', idx1);
                if (idx2 > 0) {
                    this.start = Integer.parseInt(region.substring(idx1 + 1, idx2));
                    this.end = Integer.parseInt(region.substring(idx2 + 1));
                } else {
                    this.start = Integer.parseInt(region.substring(idx1 + 1));
                    this.end = this.start;
                }
            } else {
                this.chromosome = region;
                this.start = 0;
                this.end = Integer.MAX_VALUE;
            }
        }
    }

    public static Region parseRegion(String regionString) {
        return new Region(regionString);
    }

    public static List<Region> parseRegions(String regionsString) {
        return parseRegions(regionsString, false);
    }

    public static List<Region> parseRegions(String regionsString, boolean normalize) {
        List<Region> regions = null;
        if (regionsString != null && !regionsString.isEmpty()) {
            String[] regionItems = regionsString.split(",");
            regions = new ArrayList<>(regionItems.length);
            for (String regionString : regionItems) {
                Region region = new Region(regionString);
                if (normalize) {
                    region.normalizeChromosome();
                }
                regions.add(region);
            }
        }
        return regions;
    }

    public String normalizeChromosome() {
        chromosome = normalizeChromosome(chromosome);
        return chromosome;
    }

    public static String normalizeChromosome(String chromosome) {
        // Replace "chr" references only at the beginning of the chromosome name
        // For instance, tomato has SL2.40ch00 and that should be kept that way
        if (chromosome.startsWith("ch")) {
            if (chromosome.startsWith("chrom")) {
                chromosome = chromosome.substring(5);
            } else if (chromosome.startsWith("chrm")) {
                chromosome = chromosome.substring(4);
            } else if (chromosome.startsWith("chr")) {
                chromosome = chromosome.substring(3);
            } else {
                // Only starts with ch
                chromosome = chromosome.substring(2);
            }
        }
        if (chromosome.equals(MITOCHONDRIA_M)) {
            chromosome = "MT";
        }
        return chromosome;
    }

    public int size() {
        return end - start + 1;
    }

    public boolean contains(String chromosome, int position) {
        return (this.chromosome.equals(chromosome) && this.start <= position && this.end >= position);
    }

    public boolean overlaps(String chromosome, int start, int end) {
        return (this.chromosome.equals(chromosome) && end >= this.start && start <= this.end);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.chromosome);
        if (this.end != Integer.MAX_VALUE && this.start != 0) {
            sb.append(":").append(this.start).append("-").append(this.end);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Region)) {
            return false;
        }

        Region region = (Region) o;

        if (getStart() != region.getStart()) {
            return false;
        }
        if (getEnd() != region.getEnd()) {
            return false;
        }
        return !(getChromosome() != null ? !getChromosome().equals(region.getChromosome()) : region.getChromosome() != null);

    }

    @Override
    public int hashCode() {
        int result = getChromosome() != null ? getChromosome().hashCode() : 0;
        result = 31 * result + getStart();
        result = 31 * result + getEnd();
        return result;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Region setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public Region setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public Region setEnd(int end) {
        this.end = end;
        return this;
    }
}
