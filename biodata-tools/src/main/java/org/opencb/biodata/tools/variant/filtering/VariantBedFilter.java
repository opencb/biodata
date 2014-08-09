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
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
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
