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

package org.opencb.biodata.tools.variant.filtering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.opencb.biodata.models.variant.Variant;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class VariantBedFilter extends VariantFilter {
    private Map<String, SortedSet<Region>> regions;


    public VariantBedFilter(String filename) {
        regions = new LinkedHashMap<>(20);
        populateRegionList(filename);
    }

    public VariantBedFilter(String filename, int priority) {
        super(priority);
        regions = new LinkedHashMap<>(20);
        populateRegionList(filename);
    }

    private void populateRegionList(String filename) {

        SortedSet<Region> regionList;
        BufferedReader br;

        String line, chr;
        long start, end;
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    String[] splits = line.split("\t");
                    chr = splits[0];
                    start = Long.parseLong(splits[1]);
                    end = Long.parseLong(splits[2]);

                    if (regions.containsKey(chr)) {
                        regionList = regions.get(chr);
                    } else {
                        regionList = new TreeSet<>();
                        regions.put(chr, regionList);
                    }

                    regionList.add(new Region(start, end));
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean apply(Variant variant) {
        if (regions.containsKey(variant.getChromosome())) {
            SortedSet<Region> regionList = regions.get(variant.getChromosome());
            for (Region r : regionList) {
                if (r.contains(variant.getStart())) {
                    return true;
                }
            }
        }
        return false;
    }

    private class Region implements Comparable<Region> {
        private long start, end;

        private Region(long start, long end) {
            this.start = start;
            this.end = end;
        }

        private long getStart() {
            return start;
        }

        private void setStart(long start) {
            this.start = start;
        }

        private long getEnd() {
            return end;
        }

        private void setEnd(long end) {
            this.end = end;
        }

        public boolean contains(long pos) {
            return pos >= this.start && pos <= this.end;
        }

        @Override
        public int compareTo(Region o) {
            return (int) (this.start - o.getStart());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Region)) return false;

            Region region = (Region) o;

            if (end != region.end) return false;
            if (start != region.start) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (start ^ (start >>> 32));
            result = 31 * result + (int) (end ^ (end >>> 32));
            return result;
        }
    }
}
