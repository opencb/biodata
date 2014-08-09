package org.opencb.biodata.formats.feature.bed;

public class Bed {

    private String chromosome;
    private int start;
    private int end;
    private String name;
    private int score;
    private String strand;
    private int thickStart;
    private int thickEnd;
    private String itemRgb;
    private int blockCount;
    private String blockSizes;
    private String blockStarts;


    public Bed(String chromosome, Integer start, Integer end) {
        this(chromosome, start, end, "", 0, "", 0, 0, "", 0, "", "");
    }

    /**
     * @param chromosome
     * @param start
     * @param end
     * @param name
     * @param score
     * @param strand
     * @param thickStart
     * @param thickEnd
     * @param itemRgb
     * @param blockCount
     * @param blockSizes
     * @param blockStarts
     */
    public Bed(String chromosome, Integer start, Integer end, String name, Integer score, String strand, Integer thickStart, Integer thickEnd, String itemRgb, Integer blockCount, String blockSizes, String blockStarts) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.name = name;
        this.score = score;
        this.strand = strand;
        this.thickStart = thickStart;
        this.thickEnd = thickEnd;
        this.itemRgb = itemRgb;
        this.blockCount = blockCount;
        this.blockSizes = blockSizes;
        this.blockStarts = blockStarts;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(chromosome).append("\t");
        builder.append(start).append("\t");
        builder.append(end).append("\t");
        builder.append(name).append("\t");
        builder.append(score).append("\t");
        builder.append(strand).append("\t");
        builder.append(thickStart).append("\t");
        builder.append(thickEnd).append("\t");
        builder.append(itemRgb).append("\t");
        builder.append(blockCount).append("\t");
        builder.append(blockSizes).append("\t");
        builder.append(blockStarts);
        return builder.toString();
    }


    /**
     * @return the chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }


    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }


    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }


    /**
     * @return the strand
     */
    public String getStrand() {
        return strand;
    }

    /**
     * @param strand the strand to set
     */
    public void setStrand(String strand) {
        this.strand = strand;
    }


    /**
     * @return the thickStart
     */
    public int getThickStart() {
        return thickStart;
    }

    /**
     * @param thickStart the thickStart to set
     */
    public void setThickStart(int thickStart) {
        this.thickStart = thickStart;
    }


    /**
     * @return the thickEnd
     */
    public int getThickEnd() {
        return thickEnd;
    }

    /**
     * @param thickEnd the thickEnd to set
     */
    public void setThickEnd(int thickEnd) {
        this.thickEnd = thickEnd;
    }


    /**
     * @return the itemRgb
     */
    public String getItemRgb() {
        return itemRgb;
    }

    /**
     * @param itemRgb the itemRgb to set
     */
    public void setItemRgb(String itemRgb) {
        this.itemRgb = itemRgb;
    }


    /**
     * @return the blockCount
     */
    public int getBlockCount() {
        return blockCount;
    }

    /**
     * @param blockCount the blockCount to set
     */
    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }


    /**
     * @return the blockSizes
     */
    public String getBlockSizes() {
        return blockSizes;
    }

    /**
     * @param blockSizes the blockSizes to set
     */
    public void setBlockSizes(String blockSizes) {
        this.blockSizes = blockSizes;
    }


    /**
     * @return the blockStarts
     */
    public String getBlockStarts() {
        return blockStarts;
    }

    /**
     * @param blockStarts the blockStarts to set
     */
    public void setBlockStarts(String blockStarts) {
        this.blockStarts = blockStarts;
    }

}
