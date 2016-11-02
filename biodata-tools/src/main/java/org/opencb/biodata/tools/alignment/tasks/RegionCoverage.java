package org.opencb.biodata.tools.alignment.tasks;

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.filtering.RegionFilter;

import java.util.List;

/**
 * Created by hpccoll1 on 15/05/15.
 */
public class RegionCoverage extends Region {

    public int windowSize;
    public int arraySize;
    public short[] array;

    public RegionCoverage() {
    }

    public RegionCoverage(Region region) {
        this(region.getChromosome(), region.getStart(), region.getEnd());
    }

    public RegionCoverage(String chromosome, int start, int end) {
        super(chromosome, start, end);
        windowSize = 1;
        arraySize = end - start + 1;
        array = (arraySize > 0 ? new short[arraySize] : null);
    }

    public int meanDepth() {
        if (arraySize <= 0) {
            return 0;
        }
        int depth = 0;
        for (int i = 0; i < arraySize; i++) {
            depth += array[i];
        }
        return (depth / arraySize);
    }

    private String toFormat(int start, int end) {
        StringBuilder res = new StringBuilder();
        int i, pos = start;
        short curr = array[pos];

        for (i = start + 1; i < end; i++) {
            if (curr != array[i]) {
                res.append(getChromosome() + "\t" + (getStart() + pos) + "\t" + (getStart() + i - 1) + "\t" + curr + "\n");
                pos = i;
                curr = array[i];
            }
        }
        //res.append(chrom + "\t" + (position + pos) + "\t" + (position + i - 1) + "\t" + curr + "\n");
        res.append(getChromosome() + "\t" + (getStart() + pos) + "\t" + (getStart() + i - 1) + "\t" + curr);
        return res.toString();
    }

    public String toFormat() {
        return toFormat(0, arraySize);
    }

    public String toFormat(RegionFilter filter) {
        StringBuilder res = new StringBuilder();

        int min, max;
        List<Region> regionList = filter.getRegionList();

        for (Region r : regionList) {
            if (r.overlaps(getChromosome(), getStart(), getEnd())) {
                min = (r.getStart() <= getStart() ? 0 : r.getStart() - getStart());
                max = (r.getEnd() >= getEnd() ? arraySize : r.getEnd() - getStart() + 1);
                return toFormat(min, max);
            }
        }

        return res.toString();
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < arraySize; i++) {
            res.append(getChromosome() + "\t" +  (getStart() + i) + "\t" + array[i] + "\n");
        }
        return res.toString();
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }

    public short[] getArray() {
        return array;
    }

    public void setArray(short[] array) {
        this.array = array;
    }
}
