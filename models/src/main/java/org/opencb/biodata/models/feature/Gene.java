package org.opencb.biodata.models.feature;

public class Gene {

    private String id;
    private String name;
    private String biotype;
    private String status;
    private String chromosome;
    private int start;
    private int end;
    private String strand;
    private String source;
    private String description;
//    private List<Transcript> transcripts;
//    private MiRNAGene mirna;

    public Gene() {
        start = -1;
        end = -1;
    }

    public Gene(String id, String name, String biotype, String status, 
            String chromosome, int start, int end, String strand, String source, String description) {
        this.id = id;
        this.name = name;
        this.biotype = biotype;
        this.status = status;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.source = source;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Gene [id=" + id + ", name=" + name
                + ", biotype=" + biotype + ", status=" + status
                + ", chromosome=" + chromosome + ", start=" + start + ", end="
                + end + ", strand=" + strand + ", source=" + source
                + ", description=" + description + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiotype() {
        return biotype;
    }

    public void setBiotype(String biotype) {
        this.biotype = biotype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
