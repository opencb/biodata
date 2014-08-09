package org.opencb.biodata.models.alignment;

import java.util.List;
import java.util.Map;
import net.sf.samtools.SAMRecord;

/**
 * Information about a sequence alignment.
 * 
 * @author Cristina Yenyxe Gonzalez Garcia <cgonzalez@cipf.es>
 */
public class Alignment {
    
    private String name;
    
    private String chromosome;
    private long start;
    private long end;
    private long unclippedStart;
    private long unclippedEnd;
    
    private int length;
    private int mappingQuality;
    private String qualities;   // TODO Find an alternative way to store qualities
    private String mateReferenceName;
    private int mateAlignmentStart;
    private int inferredInsertSize;
    
    /**
     * List of differences between the reference sequence and this alignment. 
     * Each one is defined by its position, type of difference and the changes it
     * introduces.
     */
    private List<AlignmentDifference> differences;
    
    /**
     * Optional attributes that probably depend on the format of the file the 
     * alignment was initially read.
     */
    private Map<String, String> attributes;
    
    /**
     * Bitmask with information about structure, quality and other properties 
     * of the alignment.
     */
    private int flags;
    
    public static final int ALIGNMENT_MULTIPLE_SEGMENTS = 0x01;
    public static final int SEGMENTS_PROPERLY_ALIGNED = 0x02;
    public static final int SEGMENT_UNMAPPED = 0x04;
    public static final int NEXT_SEGMENT_UNMAPPED = 0x08;
    public static final int SEQUENCE_REVERSE_COMPLEMENTED = 0x10;
    public static final int SEQUENCE_NEXT_SEGMENT_REVERSED = 0x20;
    public static final int FIRST_SEGMENT = 0x40;
    public static final int LAST_SEGMENT = 0x80;
    public static final int SECONDARY_ALIGNMENT = 0x100;
    public static final int NOT_PASSING_QC = 0x200;
    public static final int PCR_OR_OPTICAL_DUPLICATE = 0x400;
    public static final int SUPPLEMENTARY_ALIGNMENT = 0x800;

    public Alignment() { }
    
    public Alignment(String name, String chromosome, long start, long end, long unclippedStart, long unclippedEnd, 
            int length, int mappingQuality, String qualities, String mateReferenceName, int mateAlignmentStart, 
            int inferredInsertSize, int flags, List<AlignmentDifference> differences, Map<String, String> attributes) {
        this.name = name;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.unclippedStart = unclippedStart;
        this.unclippedEnd = unclippedEnd;
        this.length = length;
        this.mappingQuality = mappingQuality;
        this.qualities = qualities;
        this.mateReferenceName = mateReferenceName;
        this.mateAlignmentStart = mateAlignmentStart;
        this.inferredInsertSize = inferredInsertSize;
        this.flags = flags;
        this.differences = differences;
        this.attributes = attributes;
    }

    public Alignment(SAMRecord record, Map<String, String> attributes, String referenceSequence) {
        this(record.getReadName(), record.getReferenceName(), record.getAlignmentStart(), record.getAlignmentEnd(), 
                record.getUnclippedStart(), record.getUnclippedEnd(), record.getReadLength(), 
                record.getMappingQuality(), record.getBaseQualityString(),//.replace("\\", "\\\\").replace("\"", "\\\""), 
                record.getMateReferenceName(), record.getMateAlignmentStart(), 
                record.getInferredInsertSize(), record.getFlags(), 
                AlignmentHelper.getDifferencesFromCigar(record, referenceSequence), 
                attributes);
    }

    
    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public List<AlignmentDifference> getDifferences() {
        return differences;
    }

    public void setDifferences(List<AlignmentDifference> differences) {
        this.differences = differences;
    }

    public boolean addDifference(AlignmentDifference d) {
        return differences.add(d);
    }
    
    public boolean removeDifference(int position) {
        for (AlignmentDifference d : differences) {
            if (d.getPos() == position) {
                return differences.remove(d);
            }
        }
        return false;
    }
    
    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
    
    public void addFlag(int flag) {
        this.flags |= flag;
    }
    
    public void removeFlag(int flag) {
        this.flags = this.flags & ~flag;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public boolean addAttribute(String key, String value) {
        return attributes.put(key, value) != null;
    }
    
    public String removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    public int getInferredInsertSize() {
        return inferredInsertSize;
    }

    public void setInferredInsertSize(int inferredInsertSize) {
        this.inferredInsertSize = inferredInsertSize;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getMappingQuality() {
        return mappingQuality;
    }

    public void setMappingQuality(int mappingQuality) {
        this.mappingQuality = mappingQuality;
    }

    public int getMateAlignmentStart() {
        return mateAlignmentStart;
    }

    public void setMateAlignmentStart(int mateAlignmentStart) {
        this.mateAlignmentStart = mateAlignmentStart;
    }

    public String getMateReferenceName() {
        return mateReferenceName;
    }

    public void setMateReferenceName(String mateReferenceName) {
        this.mateReferenceName = mateReferenceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualities() {
        return qualities;
    }

    public void setQualities(String qualities) {
        this.qualities = qualities;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getUnclippedEnd() {
        return unclippedEnd;
    }

    public void setUnclippedEnd(long unclippedEnd) {
        this.unclippedEnd = unclippedEnd;
    }

    public long getUnclippedStart() {
        return unclippedStart;
    }

    public void setUnclippedStart(long unclippedStart) {
        this.unclippedStart = unclippedStart;
    }
    
    
    public static class AlignmentDifference {
        
        private final int pos;
        private final char op;
        private final String seq;

        public static final char INSERTION = 'I';
        public static final char DELETION = 'D';
        public static final char MISMATCH = 'X';
        public static final char SKIPPED_REGION = 'N';
        public static final char SOFT_CLIPPING = 'S';
        public static final char HARD_CLIPPING = 'H';
        public static final char PADDING = 'P';
        
        public AlignmentDifference(int pos, char op, String seq) {
            this.pos = pos;
            this.op = op;
            this.seq = seq;
        }

        public char getOp() {
            return op;
        }

        public int getPos() {
            return pos;
        }

        public String getSeq() {
            return seq;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AlignmentDifference)) { return false; }
            
            AlignmentDifference other = (AlignmentDifference) obj;
            return pos == other.pos && op == other.op && seq.equalsIgnoreCase(other.seq);
        }

        @Override
        public String toString() {
            return String.format("%d: %c %s", pos, op, seq);
        }
        
        
    }
    
}
