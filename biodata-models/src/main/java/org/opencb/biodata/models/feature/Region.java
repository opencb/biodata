package org.opencb.biodata.models.feature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alejandro Aleman Ramos
 * @author Cristina Yenyxe Gonzalez Garcia
 */
public class Region {

    private String chromosome;
    private int start;
    private int end;

    public Region(String chromosome, int start) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = Integer.MAX_VALUE;
    }

    public Region(String chromosome, int start, int end) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
    }

    public Region(String region) {
        if (region != null && !region.equals("")) {
            if (region.indexOf(':') != -1) {
                String[] fields = region.split("[:-]", -1);
                if (fields.length == 3) {
                    this.chromosome = fields[0];
                    this.start = Integer.parseInt(fields[1]);
                    this.end = Integer.parseInt(fields[2]);
                } else if (fields.length == 2) {
                    this.chromosome = fields[0];
                    this.start = Integer.parseInt(fields[1]);
                    this.end = Integer.MAX_VALUE;
                }
            } else {
                this.chromosome = region;
                this.start = 0;
                this.end = Integer.MAX_VALUE;
            }
        }
    }

    public static Region parseRegion(String regionString) {
        Region region = null;
        if (regionString != null && !regionString.equals("")) {
            if (regionString.indexOf(':') != -1) {
                String[] fields = regionString.split("[:-]", -1);
                if (fields.length == 3) {
                    region = new Region(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                } else if (fields.length == 2) {
                    region = new Region(fields[0], Integer.parseInt(fields[1]), Integer.MAX_VALUE);
                }
            } else {
                region = new Region(regionString, 0, Integer.MAX_VALUE);
            }
        }
        return region;
    }

    public static List<Region> parseRegions(String regionsString) {
        List<Region> regions = null;
        if (regionsString != null && !regionsString.equals("")) {
            String[] regionItems = regionsString.split(",");
            regions = new ArrayList<>(regionItems.length);
            String[] fields;
            for (String regionString : regionItems) {
                if (regionString.indexOf(':') != -1) {
                    fields = regionString.split("[:-]", -1);
                    if (fields.length == 3) {
                        regions.add(new Region(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2])));
                    } else {
                        regions.add(null);
                    }
                } else {
                    regions.add(new Region(regionString, 0, Integer.MAX_VALUE));
                }
            }
        }
        return regions;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Region region = (Region) o;

        if (end != region.end) {
            return false;
        }
        if (start != region.start) {
            return false;
        }
        if (!chromosome.equals(region.chromosome)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = chromosome.hashCode();
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (end ^ (end >>> 32));
        return result;
    }

    public boolean contains(String chr, int pos) {
        if (this.chromosome.equals(chr) && this.start <= pos && this.end >= pos) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.chromosome);

        if (this.start != 0 && this.end != Integer.MAX_VALUE) {
            sb.append(":").append(this.start).append("-").append(this.end);
        } else if (this.start != 0 && this.end == Integer.MAX_VALUE) {
            sb.append(":").append(this.start);
        }

        return sb.toString();
    }
}
