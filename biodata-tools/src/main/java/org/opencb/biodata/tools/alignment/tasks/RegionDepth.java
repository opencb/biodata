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

    public void merge(RegionDepth value) {
        mergeChunk(value, value.chunk);
    }

    public void mergeChunk(RegionDepth value, long chunk) {

        int start = (int) Math.max(value.position, chunk * CHUNK_SIZE);
        int end = (int) Math.min(value.position + value.size - 1, (chunk + 1) * CHUNK_SIZE - 1);

        int srcOffset = (int) value.position;
        int destOffset = (int) (chunk * CHUNK_SIZE);

        for (int i = start ; i <= end; i++) {
            array[i - destOffset] += value.array[i - srcOffset];
        }
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < size; i++) {
            res.append(chrom + "\t" +  (position + i) + "\t" + array[i] + "\n");
        }
        return res.toString();
    }




}
