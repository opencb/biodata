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

package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class Variant extends VariantAvro {

    public static final int SV_THRESHOLD = 50;

    /**
     * Type of variation, which depends mostly on its length.
     * <ul>
     * <li>SNVs involve a single nucleotide, without changes in length</li>
     * <li>MNVs involve multiple nucleotides, without changes in length</li>
     * <li>Indels are insertions or deletions of less than SV_THRESHOLD (50) nucleotides</li>
     * <li>Structural variations are large changes of more than SV_THRESHOLD nucleotides</li>
     * <li>Copy-number variations alter the number of copies of a region</li>
     * </ul>
     */
//    public enum VariantType {
//        SNV, MNV, INDEL, SV, CNV
//    }

    /**
     * Chromosome where the genomic variation occurred.
     */
//    private String chromosome;

    /**
     * Position where the genomic variation starts.
     * <ul>
     * <li>SNVs have the same start and end position</li>
     * <li>Insertions start in the last present position: if the first nucleotide
     * is inserted in position 6, the start is position 5</li>
     * <li>Deletions start in the first previously present position: if the first
     * deleted nucleotide is in position 6, the start is position 6</li>
     * </ul>
     */
//    private int start;

    /**
     * Position where the genomic variation ends.
     * <ul>
     * <li>SNVs have the same start and end positions</li>
     * <li>Insertions end in the first present position: if the last nucleotide
     * is inserted in position 9, the end is position 10</li>
     * <li>Deletions ends in the last previously present position: if the last
     * deleted nucleotide is in position 9, the end is position 9</li>
     * </ul>
     */
//    private int end;

    /**
     * Length of the genomic variation, which depends on the variation type.
     * <ul>
     * <li>SNVs have a length of 1 nucleotide</li>
     * <li>Indels have the length of the largest allele</li>
     * </ul>
     */
//    private int length;

    /**
     * Reference allele.
     */
//    private String reference;

    /**
     * Alternate allele.
     */
//    private String alternate;

    /**
     * Set of identifiers used for this genomic variation.
     */
//    private Set<String> ids;

    /**
     * Type of variation: single nucleotide, indel or structural variation.
     */
//    private VariantType type;

    /**
     * Unique identifier following the HGVS nomenclature.
     */
//    private Map<String, Set<String>> hgvs;

    /**
     * Information specific to each file the variant was read from, such as
     * samples or statistics.
     */
//    private Map<String, VariantSourceEntry> sourceEntries;

//    /**
//     * Statistics of the genomic variation, such as its alleles/genotypes count 
//     * or its minimum allele frequency.
//     */
//    private VariantStats stats;

    /**
     * Annotations of the genomic variation.
     */
//    private VariantAnnotation annotation;


    public Variant() {
        this("", -1, -1, "", "");
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate) {
        super(chromosome,
                start,
                end,
                (reference != null) ? reference : "",
                (alternate != null) ? alternate : "",
                new LinkedList<>(),
                0,
                null,
                new HashMap<>(),
                new LinkedList<>(),
                null);
        if (start > end && !(reference.equals("-"))) {
            throw new IllegalArgumentException("End position must be greater than the start position");
        }

        this.resetLength();
        this.resetType();

        if (this.getType() == VariantType.SNV) { // Generate HGVS code only for SNVs
            List<String> hgvsCodes = new LinkedList<>();
            hgvsCodes.add(chromosome + ":g." + start + reference + ">" + alternate);
            super.getHgvs().put("genomic", hgvsCodes);
        }

//        this.annotation = new VariantAnnotation(this.chromosome, this.start, this.end, this.reference, this.alternate);
    }

//    public VariantType getType() {
//        return type;
//    }

//    public void setType(VariantType type) {
//        this.type = type;
//    }

    private void resetType() {
        if (getReference().length() == getAlternate().length()) {
            if (getLength() > 1) {
                setType(VariantType.MNV);
            } else {
                setType(VariantType.SNV);
            }
        } else if (getLength() <= SV_THRESHOLD) {
            /*
            * 3 possibilities for being an INDEL:
            * - The value of the ALT field is <DEL> or <INS>
            * - The REF allele is not . but the ALT is
            * - The REF allele is . but the ALT is not
            * - The REF field length is different than the ALT field length
            */
            setType(VariantType.INDEL);
        } else {
            setType(VariantType.SV);
        }
    }

//    public String getChromosome() {
//        return chromosome;
//    }

    @Override
    public final void setChromosome(String chromosome) {
        if (chromosome == null || chromosome.length() == 0) {
            throw new IllegalArgumentException("Chromosome must not be empty");
        }
        // Replace "chr" references only at the beginning of the chromosome name
        // For instance, tomato has SL2.40ch00 and that should be kept that way
        if (chromosome.startsWith("chrom") || chromosome.startsWith("chrm")
                || chromosome.startsWith("chr") || chromosome.startsWith("ch")) {
            super.setChromosome(chromosome.replaceFirst("chrom|chrm|chr|ch", ""));
        } else {
            super.setChromosome(chromosome);
        }
    }

    @Override
    public final void setStart(Integer start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start must be positive");
        }
        super.setStart(start);
    }

    @Override
    public final void setEnd(Integer end) {
        if (end < 0) {
            throw new IllegalArgumentException("End must be positive");
        }
        super.setEnd(end);
    }

    @Override
    public void setReference(String reference) {
        super.setReference(reference);
        resetLength();
    }

    @Override
    public void setAlternate(String alternate) {
        super.setAlternate(alternate);
        resetLength();
    }

    protected void resetLength() {
        setLength(Math.max(getReference().length(), getAlternate().length()));
    }

    @Deprecated
    public String getId() {
        if (super.getIds() == null) {
            return null;
        } else {
            return super.getIds().stream().collect(Collectors.joining(";"));
        }
    }

    @Deprecated
    public void setId(String id) {
        if (super.getIds() == null) {
            super.setIds(new LinkedList<>());
        }
        super.getIds().add(id);
    }


    // TODO Insert in attributes?
//    public void addId(String newId) {
//        if (!this.id.contains(newId)) {
//            if (this.id.equals(".")) {
//                this.id = newId;
//            } else {
//                this.id += ";" + newId;
//            }
//        }
//    }

    public boolean addHgvs(String type, String value) {
        List<String> listByType = getHgvs().get(type);
        if (listByType == null) {
            listByType = new LinkedList<>();
        }
        return listByType.add(value);
    }

    public Map<String, VariantSourceEntry> getSourceEntries() {
        HashMap<String, VariantSourceEntry> sourceEntries = new HashMap<>();
        for (org.opencb.biodata.models.variant.avro.VariantSourceEntry sourceEntry : getStudies()) {
            if (sourceEntry instanceof VariantSourceEntry) {
                sourceEntries.put(composeId(sourceEntry.getStudyId(), sourceEntry.getFileId()), (VariantSourceEntry) sourceEntry);
            } else {
                sourceEntries.put(composeId(sourceEntry.getStudyId(), sourceEntry.getFileId()), new VariantSourceEntry(sourceEntry));
            }
        }
        return sourceEntries;
    }

    public VariantSourceEntry getSourceEntry(String fileId, String studyId) {
        List<org.opencb.biodata.models.variant.avro.VariantSourceEntry> studies = super.getStudies();
        if (studies != null) {
            int position = 0;
            for (org.opencb.biodata.models.variant.avro.VariantSourceEntry sourceEntry : studies) {
                if (sourceEntry.getStudyId().equals(studyId) && Objects.equals(sourceEntry.getFileId(), fileId)) {
                    if (sourceEntry instanceof VariantSourceEntry) {
                        return ((VariantSourceEntry) sourceEntry);
                    } else {
                        VariantSourceEntry entry = new VariantSourceEntry(sourceEntry);
                        super.getStudies().set(position, entry);
                        return entry;
                    }
                }
                position++;
            }
        }
        return null;
    }

    public void setSourceEntries(Map<String, VariantSourceEntry> sourceEntries) {
        for (VariantSourceEntry sourceEntry : sourceEntries.values()) {
            getStudies().add(sourceEntry);
        }
    }

    public void addSourceEntry(VariantSourceEntry sourceEntry) {
        getStudies().add(sourceEntry);
    }
//
//    public VariantStats getStats(String studyId, String fileId) {
//        VariantSourceEntry file = getSourceEntry(studyId, fileId);
//        if (file == null) {
//            return null;
//        }
//        return file.getStats();
//    }

//    public void setStats(VariantStats stats) {
//        this.stats = stats;
//    }

//    public VariantAnnotation getAnnotation() {
//        return annotation;
//    }

//    public void setAnnotation(VariantAnnotation annotation) {
//        this.annotation = annotation;
//    }

//    @Deprecated
//    public void addEffect(String allele, VariantEffect ct) {
////        annotation.addEffect(allele, ct);
//    }

    public Iterable<String> getSampleNames(String studyId, String fileId) {
        VariantSourceEntry file = getSourceEntry(studyId, fileId);
        if (file == null) {
            return null;
        }
        return file.getSampleNames();
    }

    public void transformToEnsemblFormat() {
        if (getType() == VariantType.INDEL || getType() == VariantType.SV || getLength() > 1) {
            if (getReference().charAt(0) == getAlternate().charAt(0)) {
                setReference(getReference().substring(1));
                setAlternate(getAlternate().substring(1));
                setStart(getStart() + 1);
                if (getReference().length() < getAlternate().length()) {
                    setEnd(getEnd() - 1);
                }

                if (getReference().equals("")) {
                    setReference("-");
                }
                if (getAlternate().equals("")) {
                    setAlternate("-");
                }

                resetLength();
            }
        }
    }


    @Override
    public String toString() {
        return "Variant{" +
                "chromosome='" + getChromosome() + '\'' +
                ", start=" + getStart() +
                ", end=" + getEnd() +
                ", reference='" + getReference() + '\'' +
                ", alternate='" + getAlternate() + '\'' +
                ", ids=" + getIds() +
                ", type=" + getType() +
                ", hgvs=" + getHgvs() +
                '}';
    }

    public String toJson() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Variant)) {
            return false;
        }

        Variant variant = (Variant) o;

        if (getStart() != variant.getStart()) {
            return false;
        }
        if (getEnd() != variant.getEnd()) {
            return false;
        }
        if (getChromosome() != null ? !getChromosome().equals(variant.getChromosome()) : variant.getChromosome() != null) {
            return false;
        }
        if (getReference() != null ? !getReference().equals(variant.getReference()) : variant.getReference() != null) {
            return false;
        }
        if (getAlternate() != null ? !getAlternate().equals(variant.getAlternate()) : variant.getAlternate() != null) {
            return false;
        }
        return getType() == variant.getType();

    }


//    @Override
//    public int hashCode() {
//        int result = getChromosome() != null ? getChromosome().hashCode() : 0;
//        result = 31 * result + getStart();
//        result = 31 * result + getEnd();
//        result = 31 * result + (getReference() != null ? getReference().hashCode() : 0);
//        result = 31 * result + (getAlternate() != null ? getAlternate().hashCode() : 0);
//        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
//        return result;
//    }

    private String composeId(String studyId, String fileId) {
        return studyId + (fileId == null ? "" : "_" + fileId);
    }

}

