package org.opencb.biodata.tools.alignment.tasks;

/**
 * Created by hpccoll1 on 15/05/15.
 */
public class RegionDepth {

    public final static int CHUNK_SIZE = 4000;

    public String chrom;
    public long position;
    public long chunk;
    public int size;
    public short[] array;

    public RegionDepth() {
    }

    public RegionDepth(String chrom, long pos, long chunk, int size) {
        this.chrom = chrom;
        this.position = pos;
        this.chunk = chunk;
        this.size = size;
        this.array = (size > 0 ? new short[size] : null);
    }

    public String toFormat() {
        StringBuilder res = new StringBuilder();
        int i, pos = 0;
        short curr = array[pos];

        for (i = 1; i < size; i++) {
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

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < size; i++) {
            res.append(chrom + "\t" +  (position + i) + "\t" + array[i] + "\n");
        }
        return res.toString();
    }
}
