package org.opencb.biodata.models.variant;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Variant builder.
 * <p>
 * Builder for a single-study single-file variant.
 */
public class VariantBuilder {

    // Known INFO gags
    private static final String CIPOS_INFO = "CIPOS";
    private static final String CIEND_INFO = "CIEND";
    private static final String SVINSSEQ_INFO = "SVINSSEQ";
    private static final String LEFT_SVINSSEQ_INFO = "LEFT_SVINSSEQ";
    private static final String RIGHT_SVINSSEQ_INFO = "RIGHT_SVINSSEQ";
    private static final String END_INFO = "END";

    // Known FORMAT tags
    private static final String COPY_NUMBER_FORMAT = "CN";

    // Known symbolic alternates
    // TODO: Support alternates like DEL:ME, INS:ME, ...
    private static final String CNV_ALT = "<CN";
    private static final String DUP_ALT = "<DUP>";
    private static final String DUP_TANDEM_ALT = "<DUP:TANDEM>";
    private static final String DEL_ALT = "<DEL>";
    private static final String INV_ALT = "<INV>";
    private static final String INS_ALT = "<INS>";
    private static final Pattern CNV_ALT_PATTERN = Pattern.compile("<CN([0-9]+)>");

    private static final Set<String> VALID_NTS = new HashSet<>(Arrays.asList("A", "C", "G", "T", "N"));
    protected static final String VARIANT_STRING_FORMAT
            = "(chr):[(cipos_left)<](start)[<(cipos_right)][-[(ciend_left)<](end)[<(ciend_right)]][:(ref)]:(alt)";

    private static final EnumSet<VariantType> SV_TYPES;
    // Variant types where the reference is incomplete.
    private static final EnumSet<VariantType> INCOMPLETE_REFERENCE_TYPES;

    protected static Logger logger = LoggerFactory.getLogger(VariantBuilder.class);

    static {
        SV_TYPES = EnumSet.copyOf(Variant.SV_SUBTYPES);
        SV_TYPES.add(VariantType.SV);
        SV_TYPES.add(VariantType.SYMBOLIC);

        INCOMPLETE_REFERENCE_TYPES = EnumSet.copyOf(SV_TYPES);
        INCOMPLETE_REFERENCE_TYPES.remove(VariantType.INSERTION);
        INCOMPLETE_REFERENCE_TYPES.add(VariantType.NO_VARIATION);
    }

    private String id;
    private List<String> names;
    private String chromosome;
    private Integer start;
    private Integer end;
    private Integer length;
    private String reference;
    private ArrayList<String> alternates;
    private VariantType type;
    private StructuralVariation sv;
    private String strand = "+";
    private String studyId;
    private String fileId;
    private LinkedHashMap<String, Integer> samplesPosition;
    private List<List<String>> samplesData;
    private Map<String, String> attributes;
    private List<String> format;

    private String variantString;

    public VariantBuilder() {
    }

    /**
     * Creates a variant parsing a string.
     *
     * Format : (chr):[(cipos_left)<](start)[<(cipos_right)][-[(ciend_left)<](end)[<(ciend_right)]][:(ref)]:(alt)
     *
     * @param variantString Variant string
     * @throws IllegalArgumentException if the variant does not match with the pattern
     */
    public VariantBuilder(String variantString) {
        this();
        this.variantString = variantString;
        if (variantString != null && !variantString.isEmpty()) {
            String[] fields = variantString.split(":", -1);
            if (fields.length == 3) {
                setChromosome(fields[0]);
                setAlternate(fields[2]);
                setReference("");

                // Structural variant (except <INS>) needs start-end coords
                if (fields[1].contains("-")) {
                    String[] coordinatesParts = fields[1].split("-");
                    parseStart(coordinatesParts[0], variantString);
                    parseEnd(coordinatesParts[1], variantString);

                    // Short variant or <INS>, no reference specified
                } else {
                    parseStart(fields[1], variantString);
                }
            } else if (fields.length == 4) {
                setChromosome(fields[0]);
                setReference(fields[2]);
                setAlternate(fields[3]);

                // Structural variant (except <INS>) needs start-end coords (<INS> may be missing end)
                if (fields[1].contains("-")) {
                    String[] coordinatesParts = fields[1].split("-");
                    parseStart(coordinatesParts[0], variantString);
                    parseEnd(coordinatesParts[1], variantString);
                } else {
                    parseStart(fields[1], variantString);
                }
            } else {
                throw new IllegalArgumentException("Variant " + variantString + " needs 3 or 4 fields separated by ':'. "
                        + "Format: \"" + VARIANT_STRING_FORMAT + "\"");
            }
        }
    }

    private void parseStart(String start, String variantString) {
        if (StringUtils.contains(start, '<')) {
            String[] split = start.split("<", -1);
            if (split.length != 3) {
                throw new IllegalArgumentException("Error parsing start from variant " + variantString + ". Expected 3 fields separated by '<'. "
                        + "Format: \"" + VARIANT_STRING_FORMAT + "\"");
            }
            initSv();
            if (!split[0].isEmpty()) {
                sv.setCiStartLeft(Integer.parseInt(split[0]));
            }
            setStart(Integer.parseInt(split[1]));
            if (!split[2].isEmpty()) {
                sv.setCiStartRight(Integer.parseInt(split[2]));
            }
        } else {
            setStart(Integer.parseInt(start));
        }
    }

    private void parseEnd(String end, String variantString) {
        if (StringUtils.contains(end, '<')) {
            String[] split = end.split("<", -1);
            if (split.length != 3) {
                throw new IllegalArgumentException("Error parsing end from variant " + variantString + ". Expected 3 fields separated by '<'. "
                        + "Format: \"" + VARIANT_STRING_FORMAT + "\"");
            }
            initSv();
            if (!split[0].isEmpty()) {
                sv.setCiEndLeft(Integer.parseInt(split[0]));
            }
            setEnd(Integer.parseInt(split[1]));
            if (!split[2].isEmpty()) {
                sv.setCiEndRight(Integer.parseInt(split[2]));
            }
        } else {
            setEnd(Integer.parseInt(end));
        }
    }


    public VariantBuilder(String chromosome, Integer start, Integer end, String reference, String alternate) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        setReference(reference);
        setAlternate(alternate);
    }
    
    public VariantBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public VariantBuilder setIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            id = null;
            names = null;
        } else {
            id = ids.get(0);
            names = ids.subList(1, ids.size());
        }
        return this;
    }

    public VariantBuilder setNames(List<String> names) {
        this.names = names;
        return this;
    }

    public VariantBuilder setChromosome(String chromosome) {
        if (StringUtils.isEmpty(chromosome)) {
            throw new IllegalArgumentException("Chromosome must not be empty");
        }
        this.chromosome = chromosome;
        return this;
    }

    public VariantBuilder setStart(Integer start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start must be positive");
        }
        this.start = start;
        return this;
    }

    public VariantBuilder setEnd(Integer end) {
        if (end < 0) {
            throw new IllegalArgumentException("End must be positive");
        }
        this.end = end;
        return this;
    }

    public VariantBuilder setLength(Integer length) {
        this.length = length;
        return this;
    }

    public VariantBuilder setReference(String reference) {
        this.reference = checkEmptySequence(reference);
        return this;
    }

    public VariantBuilder setAlternate(String alternate) {
        if (alternate.contains(",")) {
            return setAlternates(Arrays.asList(alternate.split(",")));
        }
        this.alternates = new ArrayList<>(1);
        alternates.add(checkEmptySequence(alternate));
        return this;
    }

    public VariantBuilder setAlternates(List<String> alternates) {
        this.alternates = new ArrayList<>(alternates.size());
        for (String alternate : alternates) {
            this.alternates.add(checkEmptySequence(alternate));
        }
        return this;
    }

    public VariantBuilder addAlternate(String alternate) {
        if (alternates == null) {
            alternates = new ArrayList<>(1);
        }
        alternates.add(alternate);
        return this;
    }

    public VariantBuilder setType(VariantType type) {
        this.type = type;
        return this;
    }

    public VariantBuilder setStrand(String strand) {
        this.strand = strand;
        return this;
    }

    public VariantBuilder setFilter(String filter) {
        addAttribute(StudyEntry.FILTER, filter);
        return this;
    }

    public VariantBuilder setQuality(String quality) {
        addAttribute(StudyEntry.QUAL, quality);
        return this;
    }

    public VariantBuilder setQuality(Double quality) {
        if (quality == null || quality == VariantContext.NO_LOG10_PERROR) {
            addAttribute(StudyEntry.QUAL, ".");
        } else {
            addAttribute(StudyEntry.QUAL, quality.toString());
        }
        return this;
    }

    public VariantBuilder setStudyId(String studyId) {
        this.studyId = studyId;
        return this;
    }

    public boolean hasStudyId() {
        return studyId != null;
    }

    public VariantBuilder setFileId(String fileId) {
        checkStudy("set file id");
        this.fileId = fileId;
        return this;
    }

    public VariantBuilder setAttributes(Map<String, String> attributes) {
        checkStudy("set attributes");
        this.attributes = attributes;
        return this;
    }

    public VariantBuilder addAttribute(String key, List<?> values) {
        return addAttribute(key, StringUtils.join(values, VCFConstants.INFO_FIELD_ARRAY_SEPARATOR));
    }

    public VariantBuilder addAttribute(String key, Number value) {
        return addAttribute(key, value.toString());
    }

    public VariantBuilder addAttribute(String key, String value) {
        checkStudy("add attribute");
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
        return this;
    }

    public VariantBuilder setFormat(String... format) {
        return setFormat(Arrays.asList(format));
    }

    public VariantBuilder setFormat(List<String> format) {
        checkStudy("set format");
        this.format = format;
        return this;
    }

    public VariantBuilder setSamplesPosition(LinkedHashMap<String, Integer> samplesPosition) {
        checkStudy("set samples position");
        this.samplesPosition = samplesPosition;
        return this;
    }

    public VariantBuilder setSampleNames(List<String> samples) {
        checkStudy("set sample names");
        this.samplesPosition = new LinkedHashMap<>();
        for (String sample : samples) {
            this.samplesPosition.put(sample, this.samplesPosition.size());
        }
        return this;
    }

    public void setSamplesData(List<List<String>> samplesData) {
        checkStudy("set samples data");
        this.samplesData = samplesData;
    }

    public VariantBuilder addSample(String sampleName, String... data) {
        return addSample(sampleName, Arrays.asList(data));
    }

    public VariantBuilder addSample(String sampleName, List<String> data) {
        checkStudy("add sample");
        if (samplesData == null) {
            samplesData = new ArrayList<>(samplesPosition != null ? samplesPosition.size() : 1);
        }
        if (samplesPosition == null) {
            samplesPosition = new LinkedHashMap<>();
        }
        Integer idx = samplesPosition.computeIfAbsent(sampleName, k -> samplesPosition.size());
        addSample(idx, data);
        return this;
    }

    public void addSample(Integer idx, List<String> data) {
        checkStudy("add sample");
        if (samplesData.size() == idx) {
            // Append
            samplesData.add(data);
        } else if (samplesData.size() < idx) {
            // Replace
            samplesData.set(idx, data);
        } else {
            // Fill with nulls
            for (int i = samplesData.size(); i < idx; i++) {
                samplesData.add(null);
            }
            samplesData.add(data);
        }
    }

    public Variant build() {
        return build(null);
    }

    public Variant build(Variant reuse) {
        return buildAvroVariant(reuse);
    }

    public Variant buildAvroVariant(Variant reuse) {
        prepare();

        Variant variant = reuse == null ? new Variant() : reuse;
        variant.setId(id);
        variant.setNames(names);
        variant.setChromosome(chromosome);
        variant.setStart(start);
        variant.setEnd(end);
        variant.setReference(reference);
        variant.setAlternate(alternates.get(0));
        variant.setType(type);
        variant.setLength(length);
        variant.setStrand(strand);
        variant.setSv(sv);
        if (hasStudyId()) {
            StudyEntry studyEntry = new StudyEntry(studyId);
            if (fileId != null) {
                FileEntry fileEntry = new FileEntry(fileId, null, attributes);
                studyEntry.setFiles(Collections.singletonList(fileEntry));
            }
            studyEntry.setFormat(format);
            if (alternates.size() > 0) {
                List<AlternateCoordinate> secondaryAlternates = new ArrayList<>(alternates.size() - 1);
                for (int i = 1; i < alternates.size(); i++) {
                    secondaryAlternates.add(new AlternateCoordinate(chromosome, start, end, reference, alternates.get(i), type));
                }
                studyEntry.setSecondaryAlternates(secondaryAlternates);
            }
            studyEntry.setSortedSamplesPosition(samplesPosition);
            studyEntry.setSamplesData(samplesData);
            variant.addStudyEntry(studyEntry);
        } else {
            variant.setStudies(null);
        }
        return variant;
    }

    public VariantProto.Variant buildProtoVariant() {
        return buildProtoVariant(null);
    }

    public VariantProto.Variant buildProtoVariant(VariantProto.VariantOrBuilder reuse) {
        prepare();

        VariantProto.Variant.Builder builder;
        if (reuse == null) {
            builder = VariantProto.Variant.newBuilder();
        } else if (reuse instanceof VariantProto.Variant) {
            builder = VariantProto.Variant.newBuilder((VariantProto.Variant) reuse);
        } else {
            builder = (VariantProto.Variant.Builder) reuse;
        }

        builder.setId(id)
                .addAllNames(names)
                .setChromosome(chromosome)
                .setStart(start)
                .setEnd(end)
                .setReference(reference)
                .setAlternate(alternates.get(0))
                .setType(VariantProto.VariantType.valueOf(type.toString()))
                .setLength(length)
                .setStrand(strand);

        if (sv != null) {
            VariantProto.StructuralVariation.Builder svBuilder = VariantProto.StructuralVariation.newBuilder();
            svBuilder.setCiStartLeft(sv.getCiStartLeft())
                    .setCiStartRight(sv.getCiStartRight())
                    .setCiEndLeft(sv.getCiEndLeft())
                    .setCiEndRight(sv.getCiEndRight());
            if (sv.getCopyNumber() != null) {
                svBuilder.setCopyNumber(sv.getCopyNumber());
            }
            if (sv.getRightSvInsSeq() != null) {
                svBuilder.setRightSvInsSeq(sv.getRightSvInsSeq());
            }
            if (sv.getLeftSvInsSeq() != null) {
                svBuilder.setLeftSvInsSeq(sv.getLeftSvInsSeq());
            }
            if (sv.getType() != null) {
                svBuilder.setType(VariantProto.StructuralVariantType.valueOf(sv.getType().toString()));
            }
            builder.setSv(svBuilder);
        }
        if (hasStudyId()) {
            VariantProto.StudyEntry.Builder studyBuilder = VariantProto.StudyEntry.newBuilder()
                    .setStudyId(studyId);

            if (fileId != null) {
                studyBuilder.addFiles(VariantProto.FileEntry.newBuilder()
                        .setFileId(fileId)
                        .putAllAttributes(attributes));
            }

            for (int i = 1; i < alternates.size(); i++) {
                studyBuilder.addSecondaryAlternates(VariantProto.AlternateCoordinate.newBuilder()
                        .setStart(start)
                        .setEnd(end)
                        .setReference(reference)
                        .setAlternate(alternates.get(i))
                        .setType(builder.getType()));
            }

            if (format != null) {
                studyBuilder.addAllFormat(format);
            }
            for (List<String> samplesDatum : samplesData) {
                studyBuilder.addSamplesData(VariantProto.StudyEntry.SamplesDataInfoEntry.newBuilder().addAllInfo(samplesDatum));
            }

            builder.addStudies(studyBuilder.build());
        }
        return builder.build();
    }

    private void prepare() {
        checkParams();

        if (names == null) {
            names = new ArrayList<>();
        }

        // FIXME: Should this line be moved to VariantNormalizer?
        chromosome = Region.normalizeChromosome(chromosome);

        if (type == null) {
            type = inferType(reference, alternates.get(0));
        }

        if (type.equals(VariantType.NO_VARIATION) && alternates.get(0).equals(Allele.NO_CALL_STRING)) {
            alternates.set(0, "");
        }

        if (attributes != null) {
            String attributeEndStr = attributes.get(END_INFO);
            if (StringUtils.isNumeric(attributeEndStr)) {
                Integer attributeEnd = Integer.valueOf(attributeEndStr);
                if (end == null) {
                    end = attributeEnd;
                } else if (!Objects.equals(end, attributeEnd)) {
                    throw new IllegalArgumentException("Conflict END position at variant " + toString() + ". "
                            + "Variant end = '" + end + "', "
                            + "file attribute END = '" + attributeEnd + "'");
                }
            }
        }

        if (end == null) {
            end = start + getLengthReference(reference, type, length, this) - 1;
        }

        if (start > end && !(reference.isEmpty())) {
            throw new IllegalArgumentException("End position must be greater than the start position for variant: "
                    + toString());
        }
        // Create and initialize StructuralVariation object if needed
        inferSV();

        if (length == null) {
            length = inferLength(reference, alternates.get(0), start, end, type);
        }

    }

    private void checkParams() {
        Objects.requireNonNull(chromosome, "Chromosome required");
        Objects.requireNonNull(start, "Start required");
        Objects.requireNonNull(reference, "Reference required");
        Objects.requireNonNull(alternates, "Alternate required");

        if (samplesPosition != null && samplesPosition.size() > 0) {
            int dataSize = samplesData == null ? 0 : samplesData.size();
            if (samplesPosition.size() > dataSize) {
                throw new IllegalArgumentException("Missing data from " + (samplesPosition.size() - dataSize) + " samples at variant " + this);
            } else if (samplesPosition.size() < dataSize) {
                throw new IllegalArgumentException("Missing name or identifier for " + (samplesPosition.size() - dataSize) + " samples at variant " + this);
            }
        }
    }

    static Integer getLengthReference(String reference, VariantType type, Integer length) {
        Objects.requireNonNull(length);
        return getLengthReference(reference, type, length, null);
    }

    private static Integer getLengthReference(String reference, VariantType type, @Nullable  Integer length, Object obj) {
        if (hasIncompleteReference(type)) {
            if (length == null) {
                // Default length 1 for type NO_VARIATION
                if (type == VariantType.NO_VARIATION) {
                    return 1;
                } else {
//                    return Variant.UNKNOWN_LENGTH;
                    throw new IllegalArgumentException("Unknown end or length of the variant '" + obj + "', type '" + type + "'");
                }
            } else {
                return length;
            }
        } else {
            return reference.length();
        }
    }

    static Integer getLengthAlternate(String alternate, VariantType type, Integer length) {
        if (VariantType.DELETION.equals(type)) {
            return 0;
        } else if (isSV(type) || type.equals(VariantType.NO_VARIATION)) {
            return length;
        } else {
            return alternate.length();
        }
    }

    private void checkStudy(String method) {
        if (!hasStudyId()) {
            throw new IllegalArgumentException("Can not " + method + " without study.");
        }
    }

    public static VariantType inferType(String reference, String alternate) {
        byte[] alternateBytes = alternate.getBytes();
//        if (Allele.wouldBeSymbolicAllele(alternateBytes) || Allele.wouldBeSymbolicAllele(reference.getBytes())) {
        // Symbolic variants shall contain empty reference, no need to check
        if (Allele.wouldBeSymbolicAllele(alternateBytes)) {
            if (alternate.startsWith(CNV_ALT)) {
                return VariantType.CNV;
            } else if (alternate.equals(DUP_ALT) || alternate.equals(DUP_TANDEM_ALT)){
                return VariantType.DUPLICATION;
            } else if (alternate.equals(DEL_ALT)){
                return VariantType.DELETION;
            } else if (alternate.equals(INV_ALT)){
                return VariantType.INVERSION;
            } else if (alternate.equals(INS_ALT)){
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
                if (inferLengthSimpleVariant(reference, alternate) <= Variant.SV_THRESHOLD) {
                /*
                * 3 possibilities for being an INDEL:
                * - The REF allele is not . but the ALT is
                * - The REF allele is . but the ALT is not
                * - The REF field length is different than the ALT field length
                */
                    return VariantType.INDEL;
                } else {
                    if (reference.isEmpty()
                            || alternate.startsWith(reference)
                            || alternate.endsWith(reference)) {
                        return VariantType.INSERTION;
                    } else if (alternate.isEmpty()
                            || reference.startsWith(alternate)
                            || reference.endsWith(alternate)){
                        return VariantType.DELETION;
                    } else {
                        return VariantType.SV;
                    }
                }
            }
        }
    }

    public static int inferLength(String reference, String alternate, int start, int end, VariantType type) {
        final int length;
        if (isSV(type) || type.equals(VariantType.NO_VARIATION)) {
            length = inferLengthSymbolic(type, alternate, start, end);
        } else {
            length = inferLengthSimpleVariant(reference, alternate);
        }
        return length;
    }

    private static int inferLengthSimpleVariant(String reference, String alternate) {
        final int length;
        //TODO: Can alternate be null?
        if (alternate == null) {
            length = reference.length();
        } else {
            length = Math.max(reference.length(), alternate.length());
        }
        return length;
    }

    private static int inferLengthSymbolic(VariantType type, String alternate, int start, int end) {
        int length;
        if (type.equals(VariantType.INSERTION)) {
            if (!Allele.wouldBeSymbolicAllele(alternate.getBytes())) {
                length = alternate.length();
            } else {
                // TODO: Check attribute SVLEN?
                length = Variant.UNKNOWN_LENGTH;
            }
        } else if (type.equals(VariantType.BREAKEND) || type.equals(VariantType.TRANSLOCATION)) {
            length = Variant.UNKNOWN_LENGTH; // WARNING: breakends length set to UNKNOWN_LENGTH in any case - breakends shall
            // not be stored in the future translocations formed by 4 breakends must be parsed and managed
            // instead
        } else {
            length = end - start + 1;
        }
        return length;
    }

    /**
     * For VariantType.BREAKEND variants only. Parses the alternate string of a breakend (e.g  A]2:321681]) and
     * generates a new Variant object with the coordinates and CIPOS/CIEND of the breakend mate.
     * @param variant BREAKEND Variant object containing:
     *                1.- variant.chromosome, variant.start: coordinates of the first breakend
     *                2.- variant.alternate: string containing the mate coordinates in a VCF-like format e.g  A]2:321681]
     *                It could happen that the BREAKEND doesn't have any mate, the alternate could be a '.' for example
     *                3.- variant.sv: it should be present althougth it's allowed to be null. If exists, then the
     *                following interpretation is expected from the fields:
     *                  * variant.sv.CiStartLeft, variant.sv.CiStartRight: CIPOS of the first breakend, the one with
     *                  coordinates in variant.chromosome,variant.start
     *                  * variant.sv.CiEndLeft, variant.sv.CiEndRight: CIPOS of the second (mate) breakend, the one with
     *                  coordinates in variant.alternate
     * @return A Variant object filled in with the coordinates and CIPOS of the mate breakend. IF the input variant
     * does not have a mate breakend (e.g. alternate='.'), null will be returned. The returned variant object will be
     * filled in as follows:
     *  1.- variant.chromosome, variant.start: coordinates of the mate breakend
     *  2.- variant.sv: will be null if the input variant.sv is null. Otherwise:
     *    * variant.sv.CiStartLeft, variant.sv.CiStartRight: CIPOS of the MATE breakend
     *    * variant.sv.CiEndLeft, variant.sv.CiEndRight: CIPOS of the FIRST breakend
     *    PLEASE NOTE: that the values in CiStart/CiEnd of the coordenates is swapped with respect to the input variant
     */
    public static Variant getMateBreakend(Variant variant) {
        // e.g. A]2:321681]
        Variant newvariant = parseMateBreakendFromAlternate(variant.getAlternate());
        if (newvariant != null) {
            if (variant.getSv() != null) {
                newvariant.setSv(new StructuralVariation(variant.getSv().getCiEndLeft(), variant.getSv().getCiEndRight(),
                        variant.getSv().getCiStartLeft(), variant.getSv().getCiStartRight(), null,
                        null, null, null));
            }
            return newvariant;
        }
        return null;
    }

    /**
     * Generates a new variant object by parsing the alternate string of a breakend (e.g  A]2:321681])
     * @param alternate String containing details of a mate breakend. Expected VCF-like format, e.g. A]2:321681]. Can
     *                  also be "." to indicate there's no mate.
     * @return A Variant object filled in with the coordinates parsed from the alternate string. IF there's no mate
     * breakend (e.g. alternate='.'), null will be returned. Just the variant.chromosome and variant.start fields
     * of the new Variant object will be filled in.
     */
    public static Variant parseMateBreakendFromAlternate(String alternate) {
        String[] parts = alternate.split(":");
        if (parts.length == 2) {
            String chromosome = parts[0].split("[\\[\\]]")[1];
            chromosome = Region.normalizeChromosome(chromosome);
            Integer start = Integer.valueOf(parts[1].split("[\\[\\]]")[0]);
            Variant newVariant = new Variant(chromosome, start, null, null);
            return newVariant;
        }
        return null;
    }


    public static boolean isSV(VariantType type) {
        return SV_TYPES.contains(type);
    }

    public static boolean hasIncompleteReference(VariantType type) {
        return INCOMPLETE_REFERENCE_TYPES.contains(type);
    }

    public void inferSV() {
        if (isSV(type)) {
            initSv();
            switch (type) {
                // Breakends use the variant.sv.CiStart/CiEnd in a special manner:
                //   * variant.sv.CiStartLeft, variant.sv.CiStartRight: CIPOS of the first breakend, the one with
                //   coordinates in variant.chromosome,variant.start
                //   * variant.sv.CiEndLeft, variant.sv.CiEndRight: CIPOS of the second (mate) breakend, the one with
                //   coordinates in variant.alternate
                // IF, such as in this case, there's no actual CIPOS for the first nor the second breakend,
                // variant.sv.CiStartLeft, variant.sv.CiStartRight are initialized with the FIRST breakend start and
                // variant.sv.CiEndLeft, variant.sv.CiEndRight are initialized with the SECOND (mate) breakend start
                case BREAKEND:
                    Variant mate = parseMateBreakendFromAlternate(alternates.get(0));
                    sv.setCiEndLeft(mate.getStart());
                    sv.setCiEndRight(mate.getEnd());
                    break;
                case DUPLICATION:
                    if (alternates.get(0).equals(DUP_TANDEM_ALT)) {
                        alternates.set(0, DUP_ALT);
                        sv.setType(StructuralVariantType.TANDEM_DUPLICATION);
                    }
                    break;
                case CNV:
                    Integer copyNumber = getCopyNumberFromAlternate(alternates.get(0));
                    if (copyNumber == null) {
                        copyNumber = getCopyNumberFromFormat();
                    }
                    sv.setCopyNumber(copyNumber);
                    sv.setType(getCNVSubtype(copyNumber));
                    break;
            }

            if (attributes != null) {
                attributes.forEach(this::parseStructuralVariationAttributes);
            }
        }

    }

    private void initSv() {
        // Do not initialize any value by default
        if (sv == null) {
            sv = new StructuralVariation();
        }
//        if (sv == null) {
//            sv = new StructuralVariation(start, start, end, end, null, null, null, null);
//        } else {
//            if (sv.getCiStartLeft() == null) {
//                sv.setCiStartLeft(start);
//            }
//            if (sv.getCiStartRight() == null) {
//                sv.setCiStartRight(start);
//            }
//            if (sv.getCiEndLeft() == null) {
//                sv.setCiEndLeft(end);
//            }
//            if (sv.getCiEndRight() == null) {
//                sv.setCiEndRight(end);
//            }
//        }
    }

    /**
     * Be aware! this method may change the main alternate
     * @param key
     * @param value
     */
    private void parseStructuralVariationAttributes(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        switch (key) {
            case SVINSSEQ_INFO:
                // Seen DELETIONS with this field set. Makes no sense
                if (VariantType.INSERTION.equals(type)) {
                    alternates.set(0, value);
                }
                break;
            case LEFT_SVINSSEQ_INFO:
                // Seen DELETIONS with this field set. Makes no sense
                if (VariantType.INSERTION.equals(type)) {
                    sv.setLeftSvInsSeq(value);
                }
                break;
            case RIGHT_SVINSSEQ_INFO:
                // Seen DELETIONS with this field set. Makes no sense
                if (VariantType.INSERTION.equals(type)) {
                    sv.setRightSvInsSeq(value);
                }
                break;
            case CIPOS_INFO:
                String[] parts = value.split(",");
                sv.setCiStartLeft(start + Integer.parseInt(parts[0]));
                sv.setCiStartRight(start + Integer.parseInt(parts[1]));
                break;
            case CIEND_INFO:
                parts = value.split(",");
                sv.setCiEndLeft(end + Integer.parseInt(parts[0]));
                sv.setCiEndRight(end + Integer.parseInt(parts[1]));
                break;
        }

    }

    public static Integer getCopyNumberFromAlternate(String alternate) {
        Matcher matcher = CNV_ALT_PATTERN.matcher(alternate);
        if (matcher.matches()) {
            return Integer.valueOf(matcher.group(1));
        } else {
            return null;
        }
    }

    public Integer getCopyNumberFromFormat() {
        if (format == null) {
            return null;
        }
        int cnIdx = format.indexOf(COPY_NUMBER_FORMAT);
        if (cnIdx < 0) {
            return null;
        }
        Integer cn = null;
        for (List<String> samplesDatum : samplesData) {
            String cdStr = samplesDatum.get(cnIdx);
            if (StringUtils.isNumeric(cdStr)) {
                Integer aux = Integer.valueOf(cdStr);
                if (cn == null) {
                    cn = aux;
                } else if (!Objects.equals(cn, aux)) {
                    logger.warn("Found multiple samples with different CN format values at variant '{}'", this);
                    return null;
                }
            }
        }
        return cn;
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

    public static StructuralVariation getStructuralVariation(Variant variant, StructuralVariantType tandemDuplication) {
        int[] impreciseStart = getImpreciseStart(variant);
        int[] impreciseEnd = getImpreciseEnd(variant);
        String[] svInsSeq = getSvInsSeq(variant);

        StructuralVariation sv = new StructuralVariation();
        sv.setCiStartLeft(impreciseStart[0]);
        sv.setCiStartRight(impreciseStart[1]);
        sv.setCiEndLeft(impreciseEnd[0]);
        sv.setCiEndRight(impreciseEnd[1]);

        sv.setLeftSvInsSeq(svInsSeq[0]);
        sv.setRightSvInsSeq(svInsSeq[1]);

        // If it's not a tandem duplication, this will set the type to null
        sv.setType(tandemDuplication);

        // Will properly set the type if it's a CNV
        if (variant.getType().equals(VariantType.CNV)) {
            Integer copyNumber = getCopyNumberFromAlternate(variant.getAlternate());
            if (copyNumber != null) {
                sv.setCopyNumber(copyNumber);
                sv.setType(getCNVSubtype(copyNumber));
            }
        }
        return sv;

    }

    private static String[] getSvInsSeq(Variant variant) {
        String leftSvInsSeq = null;
        String rightSvInsSeq = null;
        if (variant.getStudies()!= null
                && !variant.getStudies().isEmpty()
                && !variant.getStudies().get(0).getFiles().isEmpty()) {
            if (variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(LEFT_SVINSSEQ_INFO)) {
                leftSvInsSeq = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(LEFT_SVINSSEQ_INFO);
            }
            if (variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(RIGHT_SVINSSEQ_INFO)) {
                rightSvInsSeq = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(RIGHT_SVINSSEQ_INFO);
            }
        }

        return new String[]{leftSvInsSeq, rightSvInsSeq};
    }

    public static int[] getImpreciseStart(Variant variant) {
        if (variant.getStudies()!= null
                && !variant.getStudies().isEmpty()
                && !variant.getStudies().get(0).getFiles().isEmpty()
                && variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(CIPOS_INFO)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(CIPOS_INFO).split(",", 2);
            return new int[]{variant.getStart() + Integer.parseInt(parts[0]),
                    variant.getStart() + Integer.parseInt(parts[1])};
        } else {
            return new int[]{variant.getStart(), variant.getStart()};
        }
    }

    public static int[] getImpreciseEnd(Variant variant) {
        if (variant.getStudies()!= null
                && !variant.getStudies().isEmpty()
                && !variant.getStudies().get(0).getFiles().isEmpty()
                && variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(CIEND_INFO)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(CIEND_INFO).split(",", 2);
            return new int[]{variant.getEnd() + Integer.parseInt(parts[0]),
                    variant.getEnd() + Integer.parseInt(parts[1])};
        } else {
            return new int[]{variant.getEnd(), variant.getEnd()};
        }
    }

    private static String checkEmptySequence(String sequence) {
        return (sequence != null && !sequence.equals("-")) ? sequence : "";
    }

    @Override
    public String toString() {
        return variantString != null ? variantString
                : chromosome + ":"
                    + start + "-"
                    + end + ":"
                    + reference + ":"
                    + (alternates == null ? "null" : String.join(",", alternates));
    }

}