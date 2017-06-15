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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final String CNVSTR = "<CN";
    private static final String DUPSTR = "<DUP>";
    private static final String DELSTR = "<DEL>";
    private static final String INVSTR = "<INV>";
    private static final String INSSTR = "<INS>";
    private static final Pattern CNVPATTERN = Pattern.compile("<CN([0-9]+)>");

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
     * @param variantString Variant string
     * @throws IllegalArgumentException if the variant does not match with the pattern
     */
    public Variant(String variantString) {
        this();
        if (variantString != null && !variantString.isEmpty()) {
            String[] fields = variantString.split(":", -1);
            if (fields.length == 3) {
                setChromosome(fields[0]);
                setAlternate(checkEmptySequence(fields[2]));
                // Structural variant (except <INS>) needs start-end coords
                if (fields[1].contains("-")) {
                    String[] coordinatesParts = fields[1].split("-");
                    setReference("");
                    setStart(Integer.parseInt(coordinatesParts[0]));
                    setEnd(Integer.parseInt(coordinatesParts[1]));
//                    setLength(inferLengthSymbolic(getAlternate(), getStart(), getEnd()));
                // Short variant or <INS>, no reference specified
                } else {
                    setStart(Integer.parseInt(fields[1]));
                    setReference("");
//                    setLength(inferLengthSimpleVariant(getReference(), getAlternate()));
                    setEnd(getStart() + getLengthReference() - 1);
                }
            } else {
                if (fields.length == 4) {
                    setChromosome(fields[0]);
                    setAlternate(checkEmptySequence(fields[3]));
                    // Structural variant (except <INS>) needs start-end coords (<INS> may be missing end)
                    if (fields[1].contains("-")) {
                        String[] coordinatesParts = fields[1].split("-");
                        setReference(checkEmptySequence(fields[2]));
                        setStart(Integer.parseInt(coordinatesParts[0]));
                        setEnd(Integer.parseInt(coordinatesParts[1]));
//                        setLength(inferLengthSymbolic(getAlternate(), getStart(), getEnd()));
                    } else {
                        setStart(Integer.parseInt(fields[1]));
                        setReference(checkEmptySequence(fields[2]));
//                        setLength(inferLengthSimpleVariant(getReference(), getAlternate()));
                        setEnd(getStart() + getLengthReference() - 1);
                    }
                } else {
                    throw new IllegalArgumentException("Variant " + variantString + " needs 3 or 4 fields separated by ':'. "
                            + "Format: \"(chr):(start)[-(end)][:(ref)]:(alt)\"");
                }
            }
        }
        resetLength();
        resetType();
        resetSV();
    }

    public Variant(String chromosome, int position, String reference, String alternate) {
        this(chromosome, position, position, reference, alternate, "+");
        setEnd(getStart() + getLengthReference() - 1);
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate) {
        this(chromosome, start, end, reference, alternate, "+");
    }

    public Variant(String chromosome, int start, int end, String reference, String alternate, String strand) {
        impl = new VariantAvro(
                null,
                new LinkedList<>(),
                "",
                start,
                end,
                checkEmptySequence(reference),
                checkEmptySequence(alternate),
                strand,
                null,
                0,
                null,
                new HashMap<>(),
                new LinkedList<>(),
                null);
        if (start > end && !(reference.equals("-") || reference.isEmpty())) {
            throw new IllegalArgumentException("End position must be greater than the start position for variant: "
                    + chromosome + ":" + start + "-" + end + ":" + reference + ":" + alternate);
        }

        this.setChromosome(chromosome);

        this.resetLength();
        this.resetType();
        this.resetSV();

//        this.resetHGVS();

//        this.annotation = new VariantAnnotation(this.chromosome, this.start, this.end, this.reference, this.alternate);
        studyEntries = new HashMap<>();
    }

    public static Integer getCopyNumberFromAlternate(String alternate) {
        Matcher matcher = CNVPATTERN.matcher(alternate);
        if (matcher.matches()) {
            return Integer.valueOf(matcher.group(1));
        } else {
            return null;
        }
    }

    public static StructuralVariantType getCNVSubtype(Integer copyNumber) {
        if (copyNumber != null) {
            if (copyNumber > 2) {
                return StructuralVariantType.COPY_NUMBER_GAIN;
            } else if (copyNumber < 2) {
                return StructuralVariantType.COPY_NUMBER_LOSS;
            }
        }
        return null;
    }

    private String checkEmptySequence(String sequence) {
        return (sequence != null && !sequence.equals("-")) ? sequence : "";
    }

    public void resetType() {
        setType(inferType(getReference(), getAlternate()));
    }

    public static VariantType inferType(String reference, String alternate) {
        byte[] alternateBytes = alternate.getBytes();
//        if (Allele.wouldBeSymbolicAllele(alternateBytes) || Allele.wouldBeSymbolicAllele(reference.getBytes())) {
        // Symbolic variants shall contain empty reference, no need to check
        if (Allele.wouldBeSymbolicAllele(alternateBytes)) {
            if (alternate.startsWith(CNVSTR)) {
                return VariantType.CNV;
            } else if (alternate.equals(DUPSTR)){
                return VariantType.DUPLICATION;
            } else if (alternate.equals(DELSTR)){
                return VariantType.DELETION;
            } else if (alternate.equals(INVSTR)){
                return VariantType.INVERSION;
            } else if (alternate.equals(INSSTR)){
                return VariantType.INSERTION;
            } else if (alternate.contains("[") || alternate.contains("]")  // mated breakend
                    || alternateBytes[0] == '.' || alternateBytes[alternateBytes.length - 1] == '.')  { // single breakend
                return VariantType.BREAKEND;
            } else {
                return VariantType.SYMBOLIC;
            }
        } else if (alternate.equals(Allele.NO_CALL_STRING)) {
            return VariantType.NO_VARIATION;
        } else {
            if (reference.length() == alternate.length()) {
                if (reference.length() > 1) {
                    return VariantType.MNV;
                } else {
                    return VariantType.SNV;
                }
            } else {
                if (inferLengthSimpleVariant(reference, alternate) <= SV_THRESHOLD) {
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
    }

    public void resetLength() {
        setLength(inferLength(getReference(), getAlternate(), getStart(), getEnd(), getType()));
    }

    public static int inferLength(String reference, String alternate, int start, int end, VariantType type) {
        final int length;
        if (reference == null || Allele.wouldBeSymbolicAllele(alternate.getBytes())) {
            length = inferLengthSymbolic(alternate, start, end);
        } else {
            length = inferLengthSimpleVariant(reference, alternate);
        }
        return length;
    }

    private static int inferLengthSimpleVariant(String reference, String alternate) {
        final int length;
        if (alternate == null) {
            length = reference.length();
        } else {
            length = Math.max(reference.length(), alternate.length());
        }
        return length;
    }

    private static int inferLengthSymbolic(String alternate, int start, int end) {
        int length;
        if (StringUtils.startsWith(alternate, CNVSTR) || StringUtils.equals(alternate, DELSTR)
                || StringUtils.equals(alternate, DUPSTR) || StringUtils.equals(alternate, INVSTR)) {
            length = end - start + 1;
        } else if (alternate.contains("[") || alternate.contains("]")  // mated breakend
                || alternate.startsWith(".") || alternate.endsWith(".")) { // single breakend
            length = UNKNOWN_LENGTH; // WARNING: breakends length set to UNKNOWN_LENGTH in any case - breakends shall
                        // not be stored in the future translocations formed by 4 breakends must be parsed and managed
                        // instead
        } else if (alternate == null || Allele.wouldBeSymbolicAllele(alternate.getBytes())) {
            length = UNKNOWN_LENGTH;
        } else {
            length = alternate.length();
        }
        return length;
    }

    public void resetSV() {
        switch (getType()) {
            case DUPLICATION:
            case DELETION:
            case INVERSION:
            case INSERTION:
            case BREAKEND:
                setSv(new StructuralVariation(getStart(), getStart(), getEnd(), getEnd(), null,
                        null, null, null));
                break;
            case CNV:
                Integer copyNumber = getCopyNumberFromAlternate(this.getAlternate());
                setSv(new StructuralVariation(getStart(), getStart(), getEnd(), getEnd(), copyNumber, null,
                        null, getCNVSubtype(copyNumber)));
                break;
        }
    }

//    public void resetHGVS() {
//        if (this.getType() == VariantType.SNV || this.getType() == VariantType.SNP) { // Generate HGVS code only for SNVs
//            List<String> hgvsCodes = new LinkedList<>();
//            hgvsCodes.add(getChromosome() + ":g." + getStart() + getReference() + ">" + getAlternate());
//            if (impl.getHgvs() == null) {
//                impl.setHgvs(new HashMap<>());
//            }
//            impl.getHgvs().put("genomic", hgvsCodes);
//        }
//    }

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

    public void setId(String id) {
        impl.setId(id);
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

    public void setNames(List<String> names) {
        impl.setNames(names);
    }

    public Integer getLength() {
        return impl.getLength();
    }

    public Integer getLengthReference() {
        if (EnumSet.of(VariantType.NO_VARIATION, VariantType.CNV, VariantType.SV, VariantType.SYMBOLIC,
                VariantType.BREAKEND, VariantType.DELETION, VariantType.DUPLICATION,
                VariantType.INVERSION).contains(getType())) {
            return getLength();
        } else {
            return getReference().length();
        }
    }

    public Integer getLengthAlternate() {
        return getAlternate().length();
    }

    public void setLength(Integer value) {
        impl.setLength(value);
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

