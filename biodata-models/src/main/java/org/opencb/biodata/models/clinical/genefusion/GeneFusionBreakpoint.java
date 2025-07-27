package org.opencb.biodata.models.clinical.genefusion;

public class GeneFusionBreakpoint {

    private String geneName;
    private String chromosome;
    private int position;
    private String strand;

    public GeneFusionBreakpoint() {
    }

    public GeneFusionBreakpoint(String geneName, String chromosome, int position, String strand) {
        this.geneName = geneName;
        this.chromosome = chromosome;
        this.position = position;
        this.strand = strand;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneFusionBreakpoint{");
        sb.append("geneName='").append(geneName).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", strand=").append(strand);
        sb.append('}');
        return sb.toString();
    }

    public String getGeneName() {
        return geneName;
    }

    public GeneFusionBreakpoint setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public GeneFusionBreakpoint setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public GeneFusionBreakpoint setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getStrand() {
        return strand;
    }

    public GeneFusionBreakpoint setStrand(String strand) {
        this.strand = strand;
        return this;
    }
}
