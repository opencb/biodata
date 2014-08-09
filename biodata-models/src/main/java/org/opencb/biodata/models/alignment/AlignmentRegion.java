package org.opencb.biodata.models.alignment;

import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cgonzalez@cipf.es>
 */
public class AlignmentRegion {
    
    private String chromosome;
    private long start;
    private long end;
    
    private List<Alignment> alignments;
    private RegionCoverage coverage;

    public AlignmentRegion() {
    }

    public AlignmentRegion(String chromosome, long start, long end) {
        this(chromosome, start, end, null, null);
    }

    public AlignmentRegion(String chromosome, long start, long end, List<Alignment> alignments, RegionCoverage coverage) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.alignments = alignments;
        this.coverage = coverage;
    }

    public List<Alignment> getAlignments() {
        return alignments;
    }

    public void setAlignments(List<Alignment> alignments) {
        this.alignments = alignments;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public RegionCoverage getCoverage() {
        return coverage;
    }

    public void setCoverage(RegionCoverage coverage) {
        this.coverage = coverage;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
    
    
}
