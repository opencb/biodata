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

import org.opencb.biodata.models.variant.annotation.VariantAnnotation;
import org.opencb.biodata.models.variant.annotation.VariantEffect;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;

/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class Variant {

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
    public enum VariantType {
        SNV, MNV, INDEL, SV, CNV
    }

    /**
     * Chromosome where the genomic variation occurred.
     */
    private String chromosome;

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
    private int start;

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
    private int end;

    /**
     * Length of the genomic variation, which depends on the variation type.
     * <ul>
     * <li>SNVs have a length of 1 nucleotide</li>
     * <li>Indels have the length of the largest allele</li>
     * </ul>
     */
    private int length;

    /**
     * Reference allele.
     */
    private String reference;

    /**
     * Alternate allele.
     */
    private String alternate;

    /**
     * Set of identifiers used for this genomic variation.
     */
    private Set<String> ids;

    /**
     * Type of variation: single nucleotide, indel or structural variation.
     */
    private VariantType type;

    /**
     * Unique identifier following the HGVS nomenclature.
     */
    private Map<String, Set<String>> hgvs;

    /**
     * Information specific to each file the variant was read from, such as
     * samples or statistics.
     */
    private Map<String, VariantSourceEntry> sourceEntries;

//    /**
//     * Statistics of the genomic variation, such as its alleles/genotypes count 
//     * or its minimum allele frequency.
//     */
//    private VariantStats stats;

    /**
     * Annotations of the genomic variation.
     */
    private VariantAnnotation annotation;


    public Variant() {
        this("", -1, -1, "", "");
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate) {
        if (start > end && !(reference.equals("-"))) {
            throw new IllegalArgumentException("End position must be greater than the start position");
        }

        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.reference = (reference != null) ? reference : "";
        this.alternate = (alternate != null) ? alternate : "";

        this.length = Math.max(this.reference.length(), this.alternate.length());
        this.resetType();

        this.hgvs = new HashMap<>();
        if (this.type == VariantType.SNV) { // Generate HGVS code only for SNVs
            Set<String> hgvsCodes = new HashSet<>();
            hgvsCodes.add(chromosome + ":g." + start + reference + ">" + alternate);
            this.hgvs.put("genomic", hgvsCodes);
        }

        this.sourceEntries = new HashMap<>();
        this.annotation = new VariantAnnotation(this.chromosome, this.start, this.end, this.reference, this.alternate);
    }

    public VariantType getType() {
        return type;
    }

    public void setType(VariantType type) {
        this.type = type;
    }

    private void resetType() {
        if (this.reference.length() == this.alternate.length()) {
            if (this.length > 1) {
                this.type = VariantType.MNV;
            } else {
                this.type = VariantType.SNV;
            }
        } else if (this.length <= SV_THRESHOLD) {
            /*
            * 3 possibilities for being an INDEL:
            * - The value of the ALT field is <DEL> or <INS>
            * - The REF allele is not . but the ALT is
            * - The REF allele is . but the ALT is not
            * - The REF field length is different than the ALT field length
            */
            this.type = VariantType.INDEL;
        } else {
            this.type = VariantType.SV;
        }
    }

    public String getChromosome() {
        return chromosome;
    }

    public final void setChromosome(String chromosome) {
        if (chromosome == null || chromosome.length() == 0) {
            throw new IllegalArgumentException("Chromosome must not be empty");
        }
        // Replace "chr" references only at the beginning of the chromosome name
        // For instance, tomato has SL2.40ch00 and that should be kept that way
        if (chromosome.startsWith("chrom") || chromosome.startsWith("chrm")
                || chromosome.startsWith("chr") || chromosome.startsWith("ch")) {
            this.chromosome = chromosome.replaceFirst("chrom|chrm|chr|ch", "");
        } else {
            this.chromosome = chromosome;
        }
    }

    public int getStart() {
        return start;
    }

    public final void setStart(int start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start must be positive");
        }
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public final void setEnd(int end) {
        if (end < 0) {
            throw new IllegalArgumentException("End must be positive");
        }
        this.end = end;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
        this.length = Math.max(reference.length(), alternate.length());
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
        this.length = Math.max(reference.length(), alternate.length());
    }

    @Deprecated
    public String getId() {
        if (ids == null) {
            return null;
        } else {
            Iterator<String> iterator = ids.iterator();
            return iterator.hasNext() ? iterator.next() : null;
        }
    }

    @Deprecated
    public void setId(String id) {
        if (ids == null) {
            ids = new HashSet<>();
        }
        ids.add(id);
    }

    public Set<String> getIds() {
        return ids;
    }

    public void setIds(Set<String> ids) {
        this.ids = ids;
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

    public Map<String, Set<String>> getHgvs() {
        return hgvs;
    }

    public Set<String> getHgvs(String type) {
        return hgvs.get(type);
    }

    public boolean addHgvs(String type, String value) {
        Set<String> listByType = hgvs.get(type);
        if (listByType == null) {
            listByType = new HashSet<>();
        }
        return listByType.add(value);
    }

    public Map<String, VariantSourceEntry> getSourceEntries() {
        return sourceEntries;
    }

    public VariantSourceEntry getSourceEntry(String fileId, String studyId) {
        return sourceEntries.get(composeId(studyId, fileId));
    }

    public void setSourceEntries(Map<String, VariantSourceEntry> sourceEntries) {
        this.sourceEntries = sourceEntries;
    }

    public void addSourceEntry(VariantSourceEntry sourceEntry) {
        this.sourceEntries.put(composeId(sourceEntry.getStudyId(), sourceEntry.getFileId()), sourceEntry);
    }

    public VariantStats getStats(String studyId, String fileId) {
        VariantSourceEntry file = sourceEntries.get(composeId(studyId, fileId));
        if (file == null) {
            return null;
        }
        return file.getStats();
    }

//    public void setStats(VariantStats stats) {
//        this.stats = stats;
//    }

    public VariantAnnotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(VariantAnnotation annotation) {
        this.annotation = annotation;
    }

    @Deprecated
    public void addEffect(String allele, VariantEffect ct) {
//        annotation.addEffect(allele, ct);
    }

    public Iterable<String> getSampleNames(String studyId, String fileId) {
        VariantSourceEntry file = sourceEntries.get(composeId(studyId, fileId));
        if (file == null) {
            return null;
        }
        return file.getSampleNames();
    }

    public void transformToEnsemblFormat() {
        if (type == VariantType.INDEL || type == VariantType.SV || length > 1) {
            if (reference.charAt(0) == alternate.charAt(0)) {
                reference = reference.substring(1);
                alternate = alternate.substring(1);
                start++;
                if (reference.length() < alternate.length()) {
                    end--;
                }

                if (reference.equals("")) {
                    reference = "-";
                }
                if (alternate.equals("")) {
                    alternate = "-";
                }

                length = Math.max(reference.length(), alternate.length());

            }
        }
    }


    @Override
    public String toString() {
        return "Variant{" +
                "chromosome='" + chromosome + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", reference='" + reference + '\'' +
                ", alternate='" + alternate + '\'' +
                ", ids=" + ids +
                ", type=" + type +
                ", hgvs=" + hgvs +
                '}';
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

    @Override
    public int hashCode() {
        int result = getChromosome() != null ? getChromosome().hashCode() : 0;
        result = 31 * result + getStart();
        result = 31 * result + getEnd();
        result = 31 * result + (getReference() != null ? getReference().hashCode() : 0);
        result = 31 * result + (getAlternate() != null ? getAlternate().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }

    private String composeId(String studyId, String fileId) {
        return studyId + (fileId == null ? "" : "_" + fileId);
    }

}

