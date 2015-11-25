/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.alignment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Information about a sequence alignment.
 * 
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cgonzalez@cipf.es&gt;
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
    private Map<String, Object> attributes;

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
            int inferredInsertSize, int flags, List<AlignmentDifference> differences, Map<String, Object> attributes) {
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

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public boolean addAttribute(String key, Object value) {
        return attributes.put(key, value) != null;
    }

    public Object removeAttribute(String key) {
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


    /**
     * Checks that the alignment is the same. This check is stricter than necessary.
     *
     * Note that null reads
     * (e.g. alignment1.read = "acgt" and alignment2.read = null)
     * doesn't imply that they are different. The read may be implicitly
     * defined in the alignmentDifferences, retrievable with the reference read.
     *
     * Also, the CIGAR may change, having 'M' in alignment1 and 'X or '=' in alignment2.
     *
     * *1*
     * From the sam specs: http://samtools.github.io/hts-specs/SAMv1.pdf
     * "Bit 0x4 is the only reliable place to tell whether the read is unmapped.
     * If 0x4 is set, no assumptions can be made about RNAME, POS, CIGAR, MAPQ, bits 0x2, 0x10, 0x100 and 0x800,
     * and the bit 0x20 of the previous read in the template."
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alignment alignment = (Alignment) o;

        if ((flags | SEGMENTS_PROPERLY_ALIGNED | SEQUENCE_REVERSE_COMPLEMENTED | SECONDARY_ALIGNMENT |SUPPLEMENTARY_ALIGNMENT)
                != (alignment.flags | SEGMENTS_PROPERLY_ALIGNED | SEQUENCE_REVERSE_COMPLEMENTED | SECONDARY_ALIGNMENT |SUPPLEMENTARY_ALIGNMENT)) {
            return false;
        }

        if ((flags & SEGMENT_UNMAPPED) == 0) {   // segment NOT unmapped, we have to check extra fields, see *1* above
            if (start != alignment.start) return false;
            if (end != alignment.end) return false;
            if (unclippedStart != alignment.unclippedStart) return false;
            if (unclippedEnd != alignment.unclippedEnd) return false;
            if (!mateReferenceName.equals(alignment.mateReferenceName)) return false;
            if (!differences.equals(alignment.differences)) return false;
            if (mappingQuality != alignment.mappingQuality) return false;
            if (flags != alignment.flags) return false;
        }

        if (inferredInsertSize != alignment.inferredInsertSize) return false;
        if (length != alignment.length) return false;
        if (mateAlignmentStart != alignment.mateAlignmentStart) return false;
        if (!attributes.equals(alignment.attributes)) return false;
        if (!chromosome.equals(alignment.chromosome)) return false;
        if (!name.equals(alignment.name)) return false;
        if (!qualities.equals(alignment.qualities)) return false;

//        if (readSequence == null ^ alignment.readSequence == null) { // only one is null
//            return false;
//        } else if (readSequence != null && !Arrays.equals(readSequence, alignment.readSequence)) {  // both are not null and different
//            return false;
//        }

        return true;
    }

    public static class AlignmentDifference {

        private final int pos;  // in the reference sequence
        private final char op;
        private String seq;   // seq might not store the complete sequence: seq.length() will be shorter
        private final int length;   // this length is the real length of the sequence

        public static final char INSERTION = 'I';
        public static final char DELETION = 'D';
        public static final char MISMATCH = 'X';
        public static final char MATCH_MISMATCH = 'M';
        public static final char SKIPPED_REGION = 'N';
        public static final char SOFT_CLIPPING = 'S';
        public static final char HARD_CLIPPING = 'H';
        public static final char PADDING = 'P';

        public AlignmentDifference(int pos, char op, String seq, int length) {
            this.pos = pos;
            this.op = op;
            this.seq = seq;
            this.length = length;
        }

        public AlignmentDifference(int pos, char op, String seq) {
            this(pos, op, seq, seq.length());
        }

        public AlignmentDifference(int pos, char op, int length) {
            this(pos, op, null, length);
        }
        public AlignmentDifference() { 
            this(0,'M',0);
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
        public void setSeq(String seq) {
            this.seq = seq;
        }

        public int getLength() {
            return this.length;
        }
        
        public boolean isAllSequenceStored() {  //Maybe is only stored a partial sequence
            return seq != null && seq.length() == length;
        }
        public boolean isSequenceStored() {
            return seq != null;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AlignmentDifference)) { return false; }

            AlignmentDifference other = (AlignmentDifference) obj;
            if (isSequenceStored()) {
                return pos == other.pos && op == other.op && seq.equalsIgnoreCase(other.seq) && length == other.length;
            } else {
                return pos == other.pos && op == other.op && length == other.length;
            }
        }

        @Override
        public String toString() {
            return String.format("%d: %d %c %s", pos, length, op, seq);
        }

    }

}
