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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.protobuf.VariantProto;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jacobo Coll;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl", "id", "sourceEntries", "studiesMap"})
public class Variant implements Serializable {

    private final VariantAvro impl;
    private Map<String, StudyEntry> studyEntries = null;

    public static final int SV_THRESHOLD = 50;

    public Variant() {
        impl = new VariantAvro("", -1, -1, "", "", "+", new LinkedList<>(), 0, null, new HashMap<>(), new LinkedList<>(), null);
    }

    public Variant(VariantAvro avro) {
        impl = avro;
    }

    /**
     *
     * @param variantString
     */
    public Variant(String variantString) {
        impl = new VariantAvro();
        if (variantString != null && !variantString.isEmpty()) {
            String[] fields = variantString.split(":", -1);
            if (fields.length == 3) {
                setChromosome(fields[0]);
                setStart(Integer.parseInt(fields[1]));
                setReference("");
                setAlternate(checkEmptySequence(fields[2]));
            } else {
                if (fields.length == 4) {
                    setChromosome(fields[0]);
                    setStart(Integer.parseInt(fields[1]));
                    setReference(checkEmptySequence(fields[2]));
                    setAlternate(checkEmptySequence(fields[3]));
                } else {
                    throw new IllegalArgumentException("Variant needs 3 or 4 fields separated by ':'");
                }
            }
        }
        resetType();
        setEnd(getStart() + getLength() - 1);
    }

    public Variant(String chromosome, int position, String reference, String alternate) {
        this(chromosome, position, position, reference, alternate, "+");
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate) {
        this(chromosome, start, end, reference, alternate, "+");
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate, String strand) {
        impl = new VariantAvro("",
                start,
                end,
                checkEmptySequence(reference),
                checkEmptySequence(alternate),
                strand,
                new LinkedList<>(),
                0,
                null,
                new HashMap<>(),
                new LinkedList<>(),
                null);
        if (start > end && !(reference.equals("-"))) {
            throw new IllegalArgumentException("End position must be greater than the start position");
        }

        this.setChromosome(chromosome);

        this.resetLength();
        this.resetType();
        this.resetHGVS();

//        this.annotation = new VariantAnnotation(this.chromosome, this.start, this.end, this.reference, this.alternate);
        studyEntries = new HashMap<>();
    }

    private String checkEmptySequence(String sequence) {
        return (sequence != null && !sequence.equals("-")) ? sequence : "";
    }

    private void resetType() {
        setType(inferType(getReference(), getAlternate(), getLength()));
    }
    public static VariantType inferType(String reference, String alternate, Integer length) {
        if (reference.length() == alternate.length()) {
            if (length > 1) {
                return VariantType.MNV;
            } else {
                return VariantType.SNV;
            }
        } else {
            if (length <= SV_THRESHOLD) {
            /*
            * 3 possibilities for being an INDEL:
            * - The value of the ALT field is <DEL> or <INS>
            * - The REF allele is not . but the ALT is
            * - The REF allele is . but the ALT is not
            * - The REF field length is different than the ALT field length
            */
                return VariantType.INDEL;
            } else {
                return VariantType.SV;
            }
        }
    }

    private void resetLength() {
        if (getReference() == null) {
            setLength(getAlternate() == null? 0 : getAlternate().length());
        } else if (getAlternate() == null) {
            setLength(getReference().length());
        } else {
            setLength(Math.max(getReference().length(), getAlternate().length()));
        }
    }

    public void resetHGVS() {
        if (this.getType() == VariantType.SNV || this.getType() == VariantType.SNP) { // Generate HGVS code only for SNVs
            List<String> hgvsCodes = new LinkedList<>();
            hgvsCodes.add(getChromosome() + ":g." + getStart() + getReference() + ">" + getAlternate());
            impl.getHgvs().put("genomic", hgvsCodes);
        }
    }
    public VariantAvro getImpl() {
        return impl;
    }

    public final void setChromosome(String chromosome) {
        if (chromosome == null || chromosome.length() == 0) {
            throw new IllegalArgumentException("Chromosome must not be empty");
        }
        // Replace "chr" references only at the beginning of the chromosome name
        // For instance, tomato has SL2.40ch00 and that should be kept that way
        if (chromosome.startsWith("chrom") || chromosome.startsWith("chrm")
                || chromosome.startsWith("chr") || chromosome.startsWith("ch")) {
            impl.setChromosome(chromosome.replaceFirst("chrom|chrm|chr|ch", ""));
        } else {
            impl.setChromosome(chromosome);
        }
    }

    public final void setStart(Integer start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start must be positive");
        }
        impl.setStart(start);
    }

    public final void setEnd(Integer end) {
        if (end < 0) {
            throw new IllegalArgumentException("End must be positive");
        }
        impl.setEnd(end);
    }

    public void setReference(String reference) {
        impl.setReference(reference);
        resetLength();
    }

    public void setAlternate(String alternate) {
        impl.setAlternate(alternate);
        resetLength();
    }

    @Deprecated
    public String getId() {
        if (impl.getIds() == null) {
            return null;
        } else {
            return impl.getIds().stream().collect(Collectors.joining(";"));
        }
    }

    @Deprecated
    public void setId(String id) {
        if (impl.getIds() == null) {
            impl.setIds(new LinkedList<>());
        }
        impl.getIds().add(id);
    }

    public String getChromosome() {
        return impl.getChromosome();
    }

    public Integer getStart() {
        return impl.getStart();
    }

    public Integer getEnd() {
        return impl.getEnd();
    }

    public String getReference() {
        return impl.getReference();
    }

    public String getAlternate() {
        return impl.getAlternate();
    }

    public String getStrand() {
        return impl.getStrand();
    }

    public void setStrand(String strand) {
        impl.setStrand(strand);
    }

    public List<String> getIds() {
        return impl.getIds();
    }

    public void setIds(List<String> value) {
        impl.setIds(value);
    }

    public Integer getLength() {
        return impl.getLength();
    }

    public void setLength(Integer value) {
        impl.setLength(value);
    }

    public VariantType getType() {
        return impl.getType();
    }

    public void setType(VariantType value) {
        impl.setType(value);
    }

    public Map<String, List<String>> getHgvs() {
        return impl.getHgvs();
    }

    public void setHgvs(Map<String, List<String>> value) {
        impl.setHgvs(value);
    }

    public VariantAnnotation getAnnotation() {
        return impl.getAnnotation();
    }

    public void setAnnotation(VariantAnnotation value) {
        impl.setAnnotation(value);
    }

    public boolean addHgvs(String type, String value) {
        List<String> listByType = getHgvs().get(type);
        if (listByType == null) {
            listByType = new LinkedList<>();
        }
        if (!listByType.contains(value)) {
            return listByType.add(value);
        } else {
            return false; //Collection has not changed
        }
    }

    public List<StudyEntry> getStudies() {
        return getStudiesMap() == null ? null : Collections.unmodifiableList(new ArrayList<>(getStudiesMap().values()));
    }

    public void setStudies(List<StudyEntry> studies) {
        studyEntries = new HashMap<>(studies.size());
        impl.setStudies(new ArrayList<>(studies.size()));
        for (StudyEntry study : studies) {
            impl.getStudies().add(study.getImpl());
            studyEntries.put(composeId(study.getStudyId()), study);
        }
    }

    @Deprecated
    public Map<String, StudyEntry> getSourceEntries() {
        return getStudiesMap();
    }

    public Map<String, StudyEntry> getStudiesMap() {
        if (impl.getStudies() != null) {
            if (studyEntries == null) {
                studyEntries = new HashMap<>();
                for (org.opencb.biodata.models.variant.avro.StudyEntry sourceEntry : impl.getStudies()) {
                    studyEntries.put(composeId(sourceEntry.getStudyId()), new StudyEntry(sourceEntry));
                }
            }
            return Collections.unmodifiableMap(studyEntries);
        }
        return null;
    }

    @Deprecated
    public StudyEntry getSourceEntry(String studyId) {
        return getStudy(studyId);
    }

    @Deprecated
    public StudyEntry getSourceEntry(String fileId, String studyId) {
        return getStudy(studyId);
    }

    public StudyEntry getStudy(String studyId) {
        if (impl.getStudies() != null) {
            return getStudiesMap().get(composeId(studyId));
        }
        return null;
    }

    public void addStudyEntry(StudyEntry studyEntry) {
        if (studyEntries == null) {
            studyEntries = new HashMap<>();
        }
        if (impl.getStudies() == null) {
            impl.setStudies(new ArrayList<>());
        }
        this.studyEntries.put(composeId(studyEntry.getStudyId()), studyEntry);
        impl.getStudies().add(studyEntry.getImpl());
    }

    public Iterable<String> getSampleNames(String studyId, String fileId) {
        StudyEntry file = getSourceEntry(studyId, fileId);
        if (file == null) {
            return null;
        }
        return file.getSamplesName();
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
        if (this.getReference() == null) {
            return getChromosome() + ":" + getStart() + ":" + (getAlternate().isEmpty() ? "-" : getAlternate());
        } else {
            return getChromosome() + ":" + getStart() + ":" + (getReference().isEmpty() ? "-" : getReference()) + ":" + (getAlternate().isEmpty() ? "-" : getAlternate());
        }
    }

    public String toJson() {
        return impl.toString();
    }

    public boolean sameGenomicVariant(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Variant)) {
            return false;
        }

        Variant variant = (Variant) o;

        if (!Objects.equals(getStart(), variant.getStart())) {
            return false;
        }
        if (!Objects.equals(getEnd(), variant.getEnd())) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variant)) return false;

        Variant variant = (Variant) o;

        return !(impl != null ? !impl.equals(variant.impl) : variant.impl != null);

    }

    @Override
    public int hashCode() {
        return impl != null ? impl.hashCode() : 0;
    }


//    //    public int hashCode() {
//        int result = getChromosome() != null ? getChromosome().hashCode() : 0;
//        result = 31 * result + getStart();
//        result = 31 * result + getEnd();
//        result = 31 * result + (getReference() != null ? getReference().hashCode() : 0);
//        result = 31 * result + (getAlternate() != null ? getAlternate().hashCode() : 0);
//        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
//        return result;
//    }

    private String composeId(String studyId) {
        return composeId(studyId, null);
    }

    @Deprecated
    private String composeId(String studyId, String fileId) {
        return studyId;
    }

    public static Variant parseVariant(String variantString) {
        return new Variant(variantString);
    }

    public static List<Variant> parseVariants(String variantsString) {
        List<Variant> variants = null;
        if(variantsString != null && !variantsString.isEmpty()) {
            String[] variantItems = variantsString.split(",");
            variants = new ArrayList<>(variantItems.length);
            for(String variantString: variantItems) {
                variants.add(new Variant(variantString));
            }
        }
        return variants;
    }

    public boolean overlapWith(Variant other, boolean inclusive){
        if(! StringUtils.equals(this.getChromosome(), other.getChromosome()))
            return false; // Different Chromosome
        if(inclusive) // a.getStart() <= b.getEnd() && a.getEnd() >= b.getStart();
            return this.getStart() <= other.getEnd() && this.getEnd() >= other.getStart() ;
        else
            return this.getStart() < other.getEnd() && this.getEnd() > other.getStart();
    }

    public boolean onSameStartPosition (Variant other){
        return StringUtils.equals(this.getChromosome(), other.getChromosome()) 
                && this.getStart().equals(other.getStart());
    }

    /**
     * Check if Variant covers the same region (chromosome, start, end)
     * @param other Variant to check against
     * @return True if chromosome, start and end are the same
     */
    public boolean onSameRegion (Variant other){
        return onSameStartPosition(other) && this.getEnd().equals(other.getEnd());
    }
}

