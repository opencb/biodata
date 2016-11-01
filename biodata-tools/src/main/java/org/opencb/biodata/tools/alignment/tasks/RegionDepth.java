package org.opencb.biodata.tools.alignment.tasks;

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.filtering.RegionFilter;

import java.util.List;

/**
 * Created by hpccoll1 on 15/05/15.
 */
public class RegionDepth {

    public final static int CHUNK_SIZE = 4000;

    public String chrom;
    public int position;
    public int chunk;
    public int size;
    public short[] array;

    public RegionDepth() {
    }

    public RegionDepth(String chrom, int pos, int chunk, int size) {
        this.chrom = chrom;
        this.position = pos;
        this.chunk = chunk;
        this.size = size;
        this.array = (size > 0 ? new short[size] : null);
    }

    public int meanDepth() {
        int depth = 0;
        if (size <= 0) {
            return depth;
        }
        for (int i = 0; i < size; i++) {
            depth += array[i];
        }
        return (depth / size);
    }

    private String toFormat(int start, int end) {
        StringBuilder res = new StringBuilder();
        int i, pos = start;
        short curr = array[pos];

        for (i = start + 1; i < end; i++) {
            if (curr != array[i]) {
                res.append(chrom + "\t" + (position + pos) + "\t" + (position + i - 1) + "\t" + curr + "\n");
                pos = i;
                curr = array[i];
            }
        }
        //res.append(chrom + "\t" + (position + pos) + "\t" + (position + i - 1) + "\t" + curr + "\n");
        res.append(chrom + "\t" + (position + pos) + "\t" + (position + i - 1) + "\t" + curr);
        return res.toString();
    }

    public String toFormat() {
        return toFormat(0, size);
    }

    public String toFormat(RegionFilter filter) {
        StringBuilder res = new StringBuilder();

        int min, max;
        int end = position + size - 1;
        List<Region> regionList = filter.getRegionList();

        for (Region r : regionList) {
            if (r.overlaps(chrom, position, end)) {
                min = (r.getStart() <= position ? 0 : r.getStart() - position);
                max = (r.getEnd() >= end ? size : r.getEnd() - position + 1);
                return toFormat(min, max);
            }
        }

        return res.toString();
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < size; i++) {
            res.append(chrom + "\t" +  (position + i) + "\t" + array[i] + "\n");
        }
        return res.toString();
    }
}
