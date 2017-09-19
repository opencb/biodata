/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.variant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import htsjdk.variant.variantcontext.Allele;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.avro.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author Jacobo Coll;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl", "ids", "sourceEntries", "studiesMap", "lengthReference", "lengthAlternate"})
public class Variant implements Serializable, Comparable<Variant> {

    public static final EnumSet<VariantType> SV_SUBTYPES = EnumSet.of(VariantType.INSERTION, VariantType.DELETION,
            VariantType.TRANSLOCATION, VariantType.INVERSION, VariantType.CNV, VariantType.DUPLICATION,
            VariantType.BREAKEND);
    private final VariantAvro impl;
    private volatile Map<String, StudyEntry> studyEntries = null;

    public static final int SV_THRESHOLD = 50;
    public static final int UNKNOWN_LENGTH = 0;

    public Variant() {
        impl = new VariantAvro(null, new LinkedList<>(), "", -1, -1, "", "", "+", null, 0, null, new HashMap<>(), new LinkedList<>(), null);
    }

    public Variant(VariantAvro avro) {
        Objects.requireNonNull(avro);
        impl = avro;
    }

    /**
     * Creates a variant parsing a string.
     *
     * Format : (chr):(start)[-(end)][:(ref)]:(alt)
     *
     * @see VariantBuilder
     * @param variantString Variant string
     * @throws IllegalArgumentException if the variant does not match with the pattern
     */
    public Variant(String variantString) {
        this();
        new VariantBuilder(variantString).build(this);
    }

    public Variant(String chromosome, int position, String reference, String alternate) {
        this(chromosome, position, position, reference, alternate, "+");
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate) {
        this(chromosome, start, end, reference, alternate, "+");
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate, String strand) {
        this();
        new VariantBuilder(chromosome, start, end, reference, alternate).setStrand(strand).build(this);
    }

    public static VariantBuilder newBuilder() {
        return new VariantBuilder();
    }

    public static VariantBuilder newBuilder(String str) {
        return new VariantBuilder(str);
    }

    public static VariantBuilder newBuilder(String chromosome, Integer start, Integer end, String reference, String alternate) {
        return new VariantBuilder(chromosome, start, end, reference, alternate);
    }

    public static Variant parseVariant(String variantString) {
        return new VariantBuilder(variantString).build();
    }

    public static List<Variant> parseVariants(String variantsString) {
        List<Variant> variants = null;
        if(variantsString != null && !variantsString.isEmpty()) {
            String[] variantItems = variantsString.split(",");
            variants = new ArrayList<>(variantItems.length);
            for(String variantString: variantItems) {
                variants.add(parseVariant(variantString));
            }
        }
        return variants;
    }

    @Deprecated
    public static int inferLength(String reference, String alternate, int start, int end, VariantType type) {
        return VariantBuilder.inferLength(reference, alternate, start, end, type);
    }

    @Deprecated
    public static VariantType inferType(String reference, String alternate) {
        return VariantBuilder.inferType(reference, alternate);
    }

    @Deprecated
    public static Variant getMateBreakend(Variant variant) {
        return VariantBuilder.getMateBreakend(variant);
    }

    @Deprecated
    public static StructuralVariantType getCNVSubtype(Integer copyNumber) {
        return VariantBuilder.getCNVSubtype(copyNumber);
    }

    public void reset() {
        resetType();
        resetLength();
    }

    public void resetType() {
        setType(VariantBuilder.inferType(getReference(), getAlternate()));
    }

    public void resetLength() {
        setLength(VariantBuilder.inferLength(getReference(), getAlternate(), getStart(), getEnd(), getType()));
    }

    public boolean isSV() {
        return VariantBuilder.isSV(getType());
    }

    public boolean isSymbolic() {
        return Allele.wouldBeSymbolicAllele(getAlternate().getBytes());
    }

    public VariantAvro getImpl() {
        return impl;
    }

    public final void setChromosome(String chromosome) {
        if (StringUtils.isEmpty(chromosome)) {
            throw new IllegalArgumentException("Chromosome must not be empty");
        }
        impl.setChromosome(Region.normalizeChromosome(chromosome));
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
//        resetLength();
    }

    public void setAlternate(String alternate) {
        impl.setAlternate(alternate);
//        resetLength();
    }

    public String getId() {
        return impl.getId();
    }

    public Variant setId(String id) {
        impl.setId(id);
        return this;
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

    public Variant setStrand(String strand) {
        impl.setStrand(strand);
        return this;
    }

    public StructuralVariation getSv() {
        return impl.getSv();
    }

    public Variant setSv(StructuralVariation sv) {
        impl.setSv(sv);
        return this;
    }

    @Deprecated
    public List<String> getIds() {
        if (StringUtils.isNotEmpty(impl.getId())) {
            if (impl.getNames() != null) {
                List<String> ids = new ArrayList<>(1 + impl.getNames().size());
                ids.add(impl.getId());
                ids.addAll(impl.getNames());
                return ids;
            } else {
                return Collections.singletonList(impl.getId());
            }
        } else {
            return impl.getNames();
        }
    }

    @Deprecated
    public void setIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            impl.setId(null);
            impl.setNames(Collections.emptyList());
        } else {
            impl.setId(ids.get(0));
            impl.setNames(ids.subList(1, ids.size()));
        }
    }

    public List<String> getNames() {
        return impl.getNames();
    }

    public Variant setNames(List<String> names) {
        impl.setNames(names);
        return this;
    }

    public Integer getLength() {
        return impl.getLength();
    }

    public Integer getLengthReference() {
        return VariantBuilder.getLengthReference(getReference(), getType(), getLength());
    }

    public Integer getLengthAlternate() {
        return VariantBuilder.getLengthAlternate(getAlternate(), getType(), getLength());
    }

    public Variant setLength(Integer value) {
        impl.setLength(value);
        return this;
    }

    public VariantType getType() {
        return impl.getType();
    }

    public Variant setType(VariantType value) {
        impl.setType(value);
        return this;
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
        if (studies == null) {
            studyEntries = null;
            impl.setStudies(new ArrayList<>());
        } else {
            studyEntries = new HashMap<>(studies.size());
            impl.setStudies(new ArrayList<>(studies.size()));
            for (StudyEntry study : studies) {
                impl.getStudies().add(study.getImpl());
                studyEntries.put(study.getStudyId(), study);
            }
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
                    studyEntries.put(sourceEntry.getStudyId(), new StudyEntry(sourceEntry));
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
            return getStudiesMap().get(studyId);
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
        this.studyEntries.put(studyEntry.getStudyId(), studyEntry);
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

    public String toStringSimple() {
        return getChromosome() + ":" + getStart() + ":" + (getReference().isEmpty() ? "-" : getReference()) + ":" + (getAlternate().isEmpty() ? "-" : getAlternate());
    }

    @Override
    public String toString() {
        int start = getStart();
        int end = getEnd();
        StringBuilder sb = new StringBuilder().append(getChromosome()).append(":");
        StructuralVariation sv = getSv();

        // Start
        if (sv != null && (sv.getCiStartLeft() != null || sv.getCiStartRight() != null)) {
            sb.append(sv.getCiStartLeft() == null ? start : sv.getCiStartLeft())
                    .append('<').append(start).append('<')
                    .append(sv.getCiStartRight() == null ? start : sv.getCiStartRight());
        } else {
            sb.append(start);
        }

        // Optional end
        if (start != end && getLengthReference() != getReference().length()) {
            sb.append("-");
            if (sv != null && (sv.getCiEndLeft() != null || sv.getCiEndRight() != null)) {
                sb.append(sv.getCiEndLeft() == null ? end : sv.getCiEndLeft())
                        .append('<').append(end).append('<')
                        .append(sv.getCiEndRight() == null ? end : sv.getCiEndRight());
            } else {
                sb.append(end);
            }
        }

        sb.append(":");
        if (this.getReference() != null) {
            sb.append(getReference().isEmpty() ? "-" : getReference()).append(":");
        }
        if (getAlternate().isEmpty()) {
            if (getType().equals(VariantType.NO_VARIATION)) {
                sb.append(".");
            } else {
                sb.append("-");
            }
        } else {
            sb.append(getAlternate());
        }
        return sb.toString();
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

    public boolean overlapWith(Variant other, boolean inclusive) {
        return overlapWith(other.getChromosome(), other.getStart(), other.getEnd(), inclusive);
    }

    public boolean overlapWith(String chromosome, int start, int end, boolean inclusive) {
        if (!StringUtils.equals(this.getChromosome(), chromosome)) {
            return false; // Different Chromosome
        } else {
            int aStart = this.getStart();
            int aEnd = this.getEnd();

            if (aStart > aEnd) { // Insertion
                aStart = aEnd;
            }
            if (start > end){ // Insertion
                start = end;
            }

            if (inclusive) {
                return aStart <= end && aEnd >= start;
            } else {
                return aStart < end && aEnd > start;
            }
        }
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

    /**
     * Return all VariantTypes subtypes given a VariantType.
     * {@link VariantType} represents a hierarchical structure where SNV includes all SNP, MNV includes MNP
     * and SV includes  INSERTION, DELETION, TRANSLOCATION, INVERSION and CNV
     *
     * @param variantType   Variant Type
     * @return  Set of subtypes
     */
    public static Set<VariantType> subTypes(VariantType variantType) {
        if(variantType.equals(VariantType.SNV)) {
            return Collections.singleton(VariantType.SNP);
        } else if (variantType.equals(VariantType.MNV)) {
            return Collections.singleton(VariantType.MNP);
        } else if (variantType.equals(VariantType.SV)) {
            return  SV_SUBTYPES;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public int compareTo(Variant o) {
        if (this.equals(o)) {
            return 0;
        }
        return this.getImpl().compareTo(o.getImpl());
    }
}

