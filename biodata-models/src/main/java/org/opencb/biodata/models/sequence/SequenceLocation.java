package org.opencb.biodata.models.sequence;

public class SequenceLocation {
    private String chromosome;
    private int start;
    private int end;
    private String reference;
    private String alternate;
    private String strand;

    public SequenceLocation() {
    }

    public SequenceLocation(String chromosome, int start, int end, String reference, String alternate) {
        this(chromosome, start, end, reference, alternate, "+");
    }

    public SequenceLocation(String chromosome, int start, int end, String reference, String alternate, String strand) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.reference = reference;
        this.alternate = alternate;
        this.strand = strand;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SequenceLocation{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", reference='").append(reference).append('\'');
        sb.append(", alternate='").append(alternate).append('\'');
        sb.append(", strand='").append(strand).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getReference() {
        return reference;
    }

    public String getAlternate() {
        return alternate;
    }

    public String getStrand() {
        return strand;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }
}
