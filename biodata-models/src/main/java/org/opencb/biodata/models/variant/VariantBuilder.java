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

import java.util.*;
import java.util.function.Consumer;
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
    public static final String CNV_ALT = "<CNV>";
    public static final String DUP_ALT = "<DUP>";
    public static final String DUP_TANDEM_ALT = "<DUP:TANDEM>";
    public static final String DEL_ALT = "<DEL>";
    public static final String INV_ALT = "<INV>";
    public static final String INS_ALT = "<INS>";
    private static final String CNV_PREFIX_ALT = "<CN";
    private static final Pattern CNV_ALT_PATTERN = Pattern.compile("<CN([0-9]+)>");
    public static final String NON_REF_ALT = Allele.NON_REF_STRING;
    public static final String REF_ONLY_ALT = "<*>";

    private static final Set<String> VALID_NTS = new HashSet<>(Arrays.asList("A", "C", "G", "T", "N"));
    protected static final String VARIANT_STRING_FORMAT
            = "(chr)"
            + ":[(cipos_left)<](start)[<(cipos_right)]" + "[-[(ciend_left)<](end)[<(ciend_right)]]"
            + "[:(ref)]"
            + ":[(alt)|(left_ins_seq)...(right_ins_seq)]";

    private static final EnumSet<VariantType> SV_TYPES;
    // Variant types where the reference is incomplete.
    private static final EnumSet<VariantType> INCOMPLETE_REFERENCE_TYPES;
    protected static final String DUP_ALT_EXTENDED = "<DUP:";
    protected static final String DEL_ALT_EXTENDED = "<DEL:";
    protected static final String INV_ALT_EXTENDED = "<INV:";
    protected static final String INS_ALT_EXTENDED = "<INS:";
    private static final Pattern BREAKEND_MATED_PATTERN = Pattern.compile("(.*)([\\[\\]])(.+):(\\p{Digit}+)([\\[\\]])(.*)");

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
    private List<String> sampleDataKeys;
    private List<SampleEntry> samples;
    private Map<String, String> fileData;
    private String call;

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
            String[] fields;

            // Symbolic and breakend variants may use ':' within the alternate.
            //  If contains '>', is a symbolic variant
            //  If contains ']' or "[", is a breakend
            // Split in 4 segments. If reference (field[2]) contains a '<', '>', ']', '[', the reference was missing,
            // so it has to split in 3 segments.
            // Get last index of '<'. Start and end may use '<' for imprecise positions.
            if (StringUtils.containsAny(variantString, '>', ']', '[')) {
                fields = variantString.split(":", 4);
                if (fields.length == 4 && StringUtils.containsAny(fields[2], '<', '>', ']', '[')) {
                    fields = variantString.split(":", 3);
                }
            } else {
                fields = variantString.split(":", -1);
            }
            if (fields.length == 3) {
                setChromosome(fields[0]);
                parseAlternate(fields[2]);
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
                parseAlternate(fields[3]);

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

    private void parseAlternate(String alternate) {
        int idx = alternate.indexOf("...");
        if (idx >= 0) {
            setAlternate(INS_ALT);
            initSv();
            sv.setLeftSvInsSeq(alternate.substring(0, idx));
            sv.setRightSvInsSeq(alternate.substring(idx + 3));
        } else {
            setAlternate(alternate);
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
        call = null;
    }
    
    public VariantBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @Deprecated
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
        if (names != null && !names.isEmpty()) {
            addFileData(StudyEntry.VCF_ID, String.join(",", names));
        }
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
        if (alternate != null && alternate.contains(",")) {
            return setAlternates(Arrays.asList(alternate.split(",")));
        } else {
            return setAlternates(Collections.singletonList(alternate));
        }
    }

    public VariantBuilder setAlternates(List<String> alternates) {
        this.alternates = new ArrayList<>(alternates.size());
        for (String alternate : alternates) {
            addAlternate(alternate);
        }
        return this;
    }

    public VariantBuilder addAlternate(String alternate) {
        if (alternates == null) {
            alternates = new ArrayList<>(1);
        }
        if (!alternates.isEmpty()) {
            // A study entry is required if there are more than one alternate
            checkStudy("add alternate");
        }
        alternates.add(checkEmptySequence(alternate));
        return this;
    }

    public VariantBuilder setType(VariantType type) {
        this.type = type;
        return this;
    }

    public VariantBuilder setCiStart(int left, int right) {
        initSv();
        sv.setCiStartLeft(left);
        sv.setCiStartRight(right);
        return this;
    }

    public VariantBuilder setCiEnd(int left, int right) {
        initSv();
        sv.setCiEndLeft(left);
        sv.setCiEndRight(right);
        return this;
    }

    public VariantBuilder setCopyNumber(int copyNumber) {
        initSv();
        sv.setCopyNumber(copyNumber);
        sv.setType(getCNVSubtype(copyNumber));
        return this;
    }

    public VariantBuilder setSvInsSeq(String left, String right) {
        initSv();
        sv.setLeftSvInsSeq(left);
        sv.setRightSvInsSeq(right);
        return this;
    }

    public VariantBuilder setStrand(String strand) {
        this.strand = strand;
        return this;
    }

    public VariantBuilder setFilter(String filter) {
        addFileData(StudyEntry.FILTER, filter);
        return this;
    }

    public VariantBuilder setQuality(String quality) {
        addFileData(StudyEntry.QUAL, quality);
        return this;
    }

    public VariantBuilder setQuality(Double quality) {
        if (quality == null || quality == VariantContext.NO_LOG10_PERROR) {
            addFileData(StudyEntry.QUAL, ".");
        } else {
            addFileData(StudyEntry.QUAL, quality.toString());
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

    public boolean hasFileId() {
        return fileId != null;
    }

    public VariantBuilder setFileData(Map<String, String> fileData) {
        checkFile("set file data");
        this.fileData = fileData;
        return this;
    }

    public VariantBuilder addFileData(String key, List<?> values) {
        return addFileData(key, StringUtils.join(values, VCFConstants.INFO_FIELD_ARRAY_SEPARATOR));
    }

    public VariantBuilder addFileData(String key, Number value) {
        return addFileData(key, value.toString());
    }

    public VariantBuilder addFileData(String key, String value) {
        checkFile("add file data");
        if (fileData == null) {
            fileData = new HashMap<>();
        }
        try {
            fileData.put(key, value);
        } catch (UnsupportedOperationException e) {
            fileData = new HashMap<>(fileData);
            fileData.put(key, value);
        }
        return this;
    }

    public VariantBuilder setCall(String call) {
        checkFile("set call");
        this.call = call;
        return this;
    }

    public VariantBuilder setSampleDataKeys(String... sampleDataKeys) {
        return setSampleDataKeys(Arrays.asList(sampleDataKeys));
    }

    public VariantBuilder setSampleDataKeys(List<String> sampleDataKeys) {
        checkStudy("set sampleDataKeys");
        this.sampleDataKeys = sampleDataKeys;
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

    public VariantBuilder setSamples(List<SampleEntry> samples) {
        checkStudy("set samples data");
        this.samples = samples;
        return this;
    }

    public VariantBuilder addSample(String sampleName, String... data) {
        return addSample(sampleName, Arrays.asList(data));
    }

    public VariantBuilder addSample(String sampleName, List<String> data) {
        checkStudy("add sample");
        if (samples == null) {
            samples = new ArrayList<>(samplesPosition != null ? samplesPosition.size() : 1);
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
        if (samples.size() == idx) {
            // Append
            samples.add(new SampleEntry(null, null, data));
        } else if (samples.size() < idx) {
            // Replace
            samples.set(idx, new SampleEntry(null, null, data));
        } else {
            // Fill with nulls
            for (int i = samples.size(); i < idx; i++) {
                samples.add(null);
            }
            samples.add(new SampleEntry(null, null, data));
        }
    }

    public Variant build() {
        return build(null);
    }

    public Variant build(Variant reuse) {
        return buildAvroVariant(reuse);
    }

    protected static VariantAvro buildAvroVariant(String chromosome, int start, Integer end, String reference, String alternate) {
        chromosome = Region.normalizeChromosome(chromosome);
        reference = checkEmptySequence(reference);
        alternate = checkEmptySequence(alternate);

        VariantType type = VariantBuilder.inferType(reference, alternate);
        if (isSV(type)) {
            // Skip shortcut for structural variants
            return new VariantBuilder(chromosome, start, end, reference, alternate).setType(type).build().getImpl();
        } else {
            if (end == null) {
                end = start + inferLengthReference(reference, alternate, type, null, null) - 1;
            }
            int length = VariantBuilder.inferLength(reference, alternate, start, end, type);
            return new VariantAvro(null,
                    new ArrayList<>(),
                    chromosome, start, end, reference, alternate, "+", null, length, type,
                    new HashMap<>(), null, null);
        }
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
                FileEntry fileEntry = new FileEntry(fileId, call, fileData);
                studyEntry.setFiles(Collections.singletonList(fileEntry));
            }
            studyEntry.setSampleDataKeys(sampleDataKeys);
            if (alternates.size() > 0) {
                List<AlternateCoordinate> secondaryAlternates = new ArrayList<>(alternates.size() - 1);
                for (int i = 1; i < alternates.size(); i++) {
                    secondaryAlternates.add(new AlternateCoordinate(chromosome, start, end, reference, alternates.get(i), inferType(reference, alternates.get(i))));
                }
                studyEntry.setSecondaryAlternates(secondaryAlternates);
            }
            studyEntry.setSortedSamplesPosition(samplesPosition);
            studyEntry.setSamples(samples);
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
                .setType(getProtoVariantType(type))
                .setLength(length)
                .setStrand(strand);

        if (sv != null) {
            VariantProto.StructuralVariation.Builder svBuilder = VariantProto.StructuralVariation.newBuilder();
            ifNotNull(sv.getCiStartLeft(), svBuilder::setCiStartLeft);
            ifNotNull(sv.getCiStartRight(), svBuilder::setCiStartRight);
            ifNotNull(sv.getCiEndLeft(), svBuilder::setCiEndLeft);
            ifNotNull(sv.getCiEndRight(), svBuilder::setCiEndRight);
            ifNotNull(sv.getCopyNumber(), svBuilder::setCopyNumber);
            ifNotNull(sv.getRightSvInsSeq(), svBuilder::setRightSvInsSeq);
            ifNotNull(sv.getLeftSvInsSeq(), svBuilder::setLeftSvInsSeq);
            ifNotNull(sv.getType(), type -> svBuilder.setType(VariantProto.StructuralVariantType.valueOf(sv.getType().toString())));
            if (sv.getBreakend() != null) {
                Breakend bnd = sv.getBreakend();
                VariantProto.Breakend.Builder bndBuilder = VariantProto.Breakend.newBuilder();
                ifNotNull(bnd.getInsSeq(), bndBuilder::setInsSeq);

                if (bnd.getMate() != null) {
                    VariantProto.BreakendMate.Builder bndMateBuilder = VariantProto.BreakendMate.newBuilder();
                    ifNotNull(bnd.getMate().getChromosome(), bndMateBuilder::setChromosome);
                    ifNotNull(bnd.getMate().getPosition(), bndMateBuilder::setPosition);
                    ifNotNull(bnd.getMate().getCiPositionLeft(), bndMateBuilder::setCiPositionLeft);
                    ifNotNull(bnd.getMate().getCiPositionRight(), bndMateBuilder::setCiPositionRight);
                    bndBuilder.setMate(bndMateBuilder);
                }
                ifNotNull(bnd.getOrientation(), type -> bndBuilder.setOrientation(VariantProto.BreakendOrientation.valueOf(type.toString())));

                svBuilder.setBreakend(bndBuilder);
            }
            builder.setSv(svBuilder);
        }
        if (hasStudyId()) {
            VariantProto.StudyEntry.Builder studyBuilder = VariantProto.StudyEntry.newBuilder()
                    .setStudyId(studyId);

            if (fileId != null) {
                studyBuilder.addFiles(VariantProto.FileEntry.newBuilder()
                        .setFileId(fileId)
                        .putAllData(fileData));
            }

            for (int i = 1; i < alternates.size(); i++) {
                studyBuilder.addSecondaryAlternates(VariantProto.AlternateCoordinate.newBuilder()
                        .setStart(start)
                        .setEnd(end)
                        .setReference(reference)
                        .setAlternate(alternates.get(i))
                        .setType(getProtoVariantType(inferType(reference, alternates.get(i)))));
            }

            if (sampleDataKeys != null) {
                studyBuilder.addAllSampleDataKeys(sampleDataKeys);
            }
            for (SampleEntry sample : samples) {
                studyBuilder.addSamples(VariantProto.SampleEntry.newBuilder().addAllData(sample.getData()));
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

        if (fileData != null) {
            String fileDataEndStr = fileData.get(END_INFO);
            if (StringUtils.isNumeric(fileDataEndStr)) {
                Integer fileDataEnd = Integer.valueOf(fileDataEndStr);
                if (end == null) {
                    end = fileDataEnd;
                } else if (!Objects.equals(end, fileDataEnd)) {
                    throw new IllegalArgumentException("Conflict END position at variant " + toString() + ". "
                            + "Variant end = '" + end + "', "
                            + "file data END = '" + fileDataEnd + "'");
                }
            }
        } else {
            fileData = new HashMap<>();
        }

        if (end == null) {
            end = start + inferLengthReference(reference, alternates.get(0), type, length) - 1;
        }

        // Create and initialize StructuralVariation object if needed
        inferSV();

        if (length == null) {
            length = inferLength(reference, alternates.get(0), start, end, type);
        }

        if (start > end && !reference.isEmpty() && length != Variant.UNKNOWN_LENGTH) {
            throw new IllegalArgumentException("End position must be greater than the start position for variant: "
                    + toString());
        }

    }

    private void checkParams() {
        Objects.requireNonNull(chromosome, "Chromosome required");
        Objects.requireNonNull(start, "Start required");
        Objects.requireNonNull(reference, "Reference required");
        Objects.requireNonNull(alternates, "Alternate required");

        if (samplesPosition != null && samplesPosition.size() > 0) {
            int dataSize = samples == null ? 0 : samples.size();
            if (samplesPosition.size() > dataSize) {
                throw new IllegalArgumentException("Missing data from " + (samplesPosition.size() - dataSize) + " samples at variant " + this);
            } else if (samplesPosition.size() < dataSize) {
                throw new IllegalArgumentException("Missing name or identifier for " + (samplesPosition.size() - dataSize) + " samples at variant " + this);
            }
        }
    }

    static Integer getLengthReference(String reference, VariantType type, int length) {
        if (hasIncompleteReference(type)) {
            return length;
        } else {
            return reference.length();
        }
    }

    private Integer inferLengthReference(String reference, String alternate, VariantType type, Integer length) {
        return inferLengthReference(reference, alternate, type, length, this);
    }

    private static Integer inferLengthReference(String reference, String alternate, VariantType type, Integer length, Object variant) {
        if (hasIncompleteReference(alternate, type)) {
            if (length == null) {
                // Default length 1 for type NO_VARIATION
                if (type == VariantType.NO_VARIATION) {
                    return 1;
                } else if (type == VariantType.BREAKEND || type == VariantType.TRANSLOCATION) {
                    return Variant.UNKNOWN_LENGTH;
                } else {
//                    return Variant.UNKNOWN_LENGTH;
                    throw new IllegalArgumentException("Unknown end or length of the variant '" + variant + "', type '" + type + "'");
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
            setStudyId("");
//            throw new IllegalArgumentException("Can not " + method + " without study.");
        }
    }

    private void checkFile(String method) {
        if (!hasFileId()) {
            setFileId("");
//            throw new IllegalArgumentException("Can not " + method + " without file.");
        }
    }

    public static VariantType inferType(String reference, String alternate) {
        if (alternate.length() == 1 && reference.length() == 1 && !alternate.equals(Allele.NO_CALL_STRING)) {
            // Shortcut for 99% of scenarios
            return VariantType.SNV;
        }
        byte[] alternateBytes = alternate.getBytes();
//        if (Allele.wouldBeSymbolicAllele(alternateBytes) || Allele.wouldBeSymbolicAllele(reference.getBytes())) {
        // Symbolic variants shall contain empty reference, no need to check
        if (Allele.wouldBeSymbolicAllele(alternateBytes)) {
            if (alternate.startsWith(CNV_PREFIX_ALT)) {
                return VariantType.CNV;
            } else if (alternate.equals(DUP_ALT) || alternate.startsWith(DUP_ALT_EXTENDED)){
                return VariantType.DUPLICATION;
            } else if (alternate.equals(DEL_ALT) || alternate.startsWith(DEL_ALT_EXTENDED)) {
                return VariantType.DELETION;
            } else if (alternate.equals(INV_ALT) || alternate.startsWith(INV_ALT_EXTENDED)) {
                return VariantType.INVERSION;
            } else if (alternate.equals(INS_ALT) || alternate.startsWith(INS_ALT_EXTENDED)) {
                return VariantType.INSERTION;
            } else if (alternate.contains("[") || alternate.contains("]")  // mated breakend
                    || alternateBytes[0] == '.' || alternateBytes[alternateBytes.length - 1] == '.') { // single breakend
                return VariantType.BREAKEND;
            } else if (alternate.equals(Allele.NON_REF_STRING) || alternate.equals(REF_ONLY_ALT)) {
                return VariantType.NO_VARIATION;
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
                // TODO: Check file data SVLEN?
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

    public static Variant getMateBreakend(Variant variant) {
        // Check variant does have a mate
        if (variant.getSv() != null && variant.getSv().getBreakend() != null
                && variant.getSv().getBreakend().getMate() != null) {
            Variant mate =  new Variant(variant.getSv().getBreakend().getMate().getChromosome(),
                    variant.getSv().getBreakend().getMate().getPosition(), null, null);
            mate.setType(VariantType.BREAKEND);
            mate.setSv(getMateStructuralVariation(variant));

            return mate;

        } else {
            return null;
        }
    }

    private static StructuralVariation getMateStructuralVariation(Variant variant) {
        StructuralVariation structuralVariation = new StructuralVariation();
        // Check whether mate has CIPOS
        if (variant.getSv().getBreakend().getMate().getCiPositionLeft() != null
                && variant.getSv().getBreakend().getMate().getCiPositionRight() != null) {
            structuralVariation.setCiStartLeft(variant.getSv().getBreakend().getMate().getCiPositionLeft());
            structuralVariation.setCiStartRight(variant.getSv().getBreakend().getMate().getCiPositionRight());
        }
        Breakend breakend = new Breakend(new BreakendMate(variant.getChromosome(), variant.getStart(),
                variant.getSv().getCiStartLeft() != null ? variant.getSv().getCiStartLeft() : null,
                variant.getSv().getCiStartRight() != null ? variant.getSv().getCiStartRight() : null),
                getMateOrientation(variant), null);
        structuralVariation.setBreakend(breakend);

        return structuralVariation;
    }

    private static BreakendOrientation getMateOrientation(Variant variant) {
        switch (variant.getSv().getBreakend().getOrientation()) {
            case ES:
                return BreakendOrientation.SE;
            case SE:
                return BreakendOrientation.ES;
            default:
                return variant.getSv().getBreakend().getOrientation();
        }
    }

//    /**
//     * For VariantType.BREAKEND variants only. Parses the alternate string of a breakend (e.g  A]2:321681]) and
//     * generates a new Variant object with the coordinates and CIPOS/CIEND of the breakend mate.
//     * @param variant BREAKEND Variant object containing:
//     *                1.- variant.chromosome, variant.start: coordinates of the first breakend
//     *                2.- variant.alternate: string containing the mate coordinates in a VCF-like format e.g  A]2:321681]
//     *                It could happen that the BREAKEND doesn't have any mate, the alternate could be a '.' for example
//     *                3.- variant.sv: it should be present althougth it's allowed to be null. If exists, then the
//     *                following interpretation is expected from the fields:
//     *                  * variant.sv.CiStartLeft, variant.sv.CiStartRight: CIPOS of the first breakend, the one with
//     *                  coordinates in variant.chromosome,variant.start
//     *                  * variant.sv.CiEndLeft, variant.sv.CiEndRight: CIPOS of the second (mate) breakend, the one with
//     *                  coordinates in variant.alternate
//     * @return A Variant object filled in with the coordinates and CIPOS of the mate breakend. IF the input variant
//     * does not have a mate breakend (e.g. alternate='.'), null will be returned. The returned variant object will be
//     * filled in as follows:
//     *  1.- variant.chromosome, variant.start: coordinates of the mate breakend
//     *  2.- variant.sv: will be null if the input variant.sv is null. Otherwise:
//     *    * variant.sv.CiStartLeft, variant.sv.CiStartRight: CIPOS of the MATE breakend
//     *    * variant.sv.CiEndLeft, variant.sv.CiEndRight: CIPOS of the FIRST breakend
//     *    PLEASE NOTE: that the values in CiStart/CiEnd of the coordenates is swapped with respect to the input variant
//     */
//    @Deprecated
//    public static Variant getMateBreakend(Variant variant) {
//        // e.g. A]2:321681]
//        Variant newvariant = parseMateBreakendFromAlternate(variant.getAlternate());
//        if (newvariant != null) {
//            if (variant.getSv() != null) {
//                newvariant.setSv(new StructuralVariation(variant.getSv().getCiEndLeft(), variant.getSv().getCiEndRight(),
//                        variant.getSv().getCiStartLeft(), variant.getSv().getCiStartRight(), null,
//                        null, null, null, null));
//            }
//            return newvariant;
//        }
//        return null;
//    }
//
//    /**
//     * Generates a new variant object by parsing the alternate string of a breakend (e.g  A]2:321681])
//     * @param alternate String containing details of a mate breakend. Expected VCF-like format, e.g. A]2:321681]. Can
//     *                  also be "." to indicate there's no mate.
//     * @return A Variant object filled in with the coordinates parsed from the alternate string. IF there's no mate
//     * breakend (e.g. alternate='.'), null will be returned. Just the variant.chromosome and variant.start fields
//     * of the new Variant object will be filled in.
//     */
//    @Deprecated
//    public static Variant parseMateBreakendFromAlternate(String alternate) {
//        String[] parts = alternate.split(":");
//        if (parts.length == 2) {
//            String chromosome = parts[0].split("[\\[\\]]")[1];
//            chromosome = Region.normalizeChromosome(chromosome);
//            Integer start = Integer.valueOf(parts[1].split("[\\[\\]]")[0]);
//            Variant newVariant = new Variant(chromosome, start, null, null);
//            return newVariant;
//        }
//        return null;
//    }

    /**
     * Generates a new Breakend object by parsing the alternate string of a breakend (e.g  A]2:321681])
     * @param alternate String containing details of a mate breakend. Expected VCF-like format, e.g. A]2:321681]. Can
     *                  also be "." to indicate there's no mate (single breakend).
     * @return A Breakend object filled in with the coordinates parsed from the alternate string. IF there's no mate
     * breakend (e.g. alternate='.'), null will be returned.
     */
    public static Breakend parseBreakend(String reference, String alternate) {
        if (isMateBreakend(alternate)) {
            Matcher matcher = BREAKEND_MATED_PATTERN.matcher(alternate);
            if (matcher.matches()) {
                String insSeqLeft = matcher.group(1);
                String bracket = matcher.group(2);
                String chromosome = matcher.group(3);
                Integer start = Integer.valueOf(matcher.group(4));
                String bracket2 = matcher.group(5);
                String insSeqRight = matcher.group(6);

                chromosome = Region.normalizeChromosome(chromosome);

                if (!bracket.equals(bracket2) || bracket.isEmpty()) {
                    throw breakendParseException(alternate);
                }

                String insSeq;
                BreakendOrientation type;
                char thisJunctionOrientation;
                char mateJunctionOrientation;

                if (insSeqLeft.isEmpty()) {
                    if (insSeqRight.isEmpty()) {
                        throw breakendParseException(alternate);
                    } else {
                        insSeq = insSeqRight;
                        thisJunctionOrientation = 'E';
                        if (insSeq.endsWith(reference)) {
                            insSeq = insSeq.substring(0, insSeq.length() - reference.length());
                        }
                    }
                } else {
                    if (insSeqRight.isEmpty()) {
                        insSeq = insSeqLeft;
                        thisJunctionOrientation = 'S';
                        if (insSeq.startsWith(reference)) {
                            insSeq = insSeq.substring(reference.length());
                        }
                    } else {
                        throw breakendParseException(alternate);
                    }
                }
                if (insSeq.isEmpty() || insSeq.equals(".")) {
                    insSeq = null;
                }

                mateJunctionOrientation = bracket.equals("]") ? 'S' : 'E';
                if (thisJunctionOrientation == 'S') {
                    if (mateJunctionOrientation == 'S') {
                        type = BreakendOrientation.SS;
                    } else { // 'E'
                        type = BreakendOrientation.SE;
                    }
                } else { // 'E'
                    if (mateJunctionOrientation == 'S') {
                        type = BreakendOrientation.ES;
                    } else { // 'E'
                        type = BreakendOrientation.EE;
                    }
                }

                return new Breakend(new BreakendMate(chromosome, start, null, null), type, insSeq);
            } else {
                throw breakendParseException(alternate);
            }
        }
        return null;
    }

    public static boolean isMateBreakend(String alternate) {
        return StringUtils.contains(alternate, ']') || StringUtils.contains(alternate, '[');
    }

    private static RuntimeException breakendParseException(String alternate) {
        return new IllegalArgumentException("Error parsing breakend '" + alternate + "'.");
    }


    public static boolean isSV(VariantType type) {
        return SV_TYPES.contains(type);
    }

    public static boolean hasIncompleteReference(String alternate, VariantType type) {
        if (alternate != null) {
            return hasIncompleteReference(type) && Allele.wouldBeSymbolicAllele(alternate.getBytes());
        } else {
            return hasIncompleteReference(type);
        }
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
                    Breakend bnd = parseBreakend(reference, alternates.get(0));
                    if (bnd != null) {
                        sv.setBreakend(bnd);
                    }
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
                    if (copyNumber != null) {
                        sv.setCopyNumber(copyNumber);
                        sv.setType(getCNVSubtype(copyNumber));
                    }
                    break;
            }

            if (fileData != null) {
                fileData.forEach(this::parseStructuralVariationFileData);
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
    private void parseStructuralVariationFileData(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        switch (key) {
            case SVINSSEQ_INFO:
                // Seen DELETIONS with this field set. Makes no sense
                if (VariantType.INSERTION.equals(type)) {
                    // SVINSSEQ contains the sequence inserted AFTER the reference.
                    // To represent correctly the alternate, given that reference, we need to add the reference as allele context
                    // If the variant is normalized, the alleles will be trimmed, removing this "context"
                    if (alternates.size() > 1) {
                        throw new IllegalArgumentException("Found SVINSSEQ in a multi allelic variant!");
                    } else {
                        setCall(start + ":" + reference + ":" + alternates.get(0) + ":" + 0);
                        setAlternate(reference + value);
                    }
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
        // Fast fail
        if (alternate.isEmpty() || alternate.charAt(0) != '<') {
            return null;
        }
        Matcher matcher = CNV_ALT_PATTERN.matcher(alternate);
        if (matcher.matches()) {
            return Integer.valueOf(matcher.group(1));
        } else {
            return null;
        }
    }

    public Integer getCopyNumberFromFormat() {
        if (sampleDataKeys == null) {
            return null;
        }
        int cnIdx = sampleDataKeys.indexOf(COPY_NUMBER_FORMAT);
        if (cnIdx < 0) {
            return null;
        }
        Integer cn = null;
        for (SampleEntry sample : samples) {
            String cdStr = sample.getData().get(cnIdx);
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

    public static VariantProto.VariantType getProtoVariantType(VariantType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case SNV: return VariantProto.VariantType.SNV;
            case SNP: return VariantProto.VariantType.SNP;
            case MNV: return VariantProto.VariantType.MNV;
            case MNP: return VariantProto.VariantType.MNP;
            case INDEL: return VariantProto.VariantType.INDEL;
            case SV: return VariantProto.VariantType.SV;
            case INSERTION: return VariantProto.VariantType.INSERTION;
            case DELETION: return VariantProto.VariantType.DELETION;
            case TRANSLOCATION: return VariantProto.VariantType.TRANSLOCATION;
            case INVERSION: return VariantProto.VariantType.INVERSION;
            case CNV: return VariantProto.VariantType.CNV;
            case NO_VARIATION: return VariantProto.VariantType.NO_VARIATION;
            case SYMBOLIC: return VariantProto.VariantType.SYMBOLIC;
            case MIXED: return VariantProto.VariantType.MIXED;
            case DUPLICATION: return VariantProto.VariantType.DUPLICATION;
            case BREAKEND: return VariantProto.VariantType.BREAKEND;
            default: throw new EnumConstantNotPresentException(VariantProto.VariantType.class, type.name());
        }
    }

    @Deprecated
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

    @Deprecated
    private static String[] getSvInsSeq(Variant variant) {
        String leftSvInsSeq = null;
        String rightSvInsSeq = null;
        if (variant.getStudies()!= null
                && !variant.getStudies().isEmpty()
                && !variant.getStudies().get(0).getFiles().isEmpty()) {
            if (variant.getStudies().get(0).getFiles().get(0).getData().containsKey(LEFT_SVINSSEQ_INFO)) {
                leftSvInsSeq = variant.getStudies().get(0).getFiles().get(0).getData().get(LEFT_SVINSSEQ_INFO);
            }
            if (variant.getStudies().get(0).getFiles().get(0).getData().containsKey(RIGHT_SVINSSEQ_INFO)) {
                rightSvInsSeq = variant.getStudies().get(0).getFiles().get(0).getData().get(RIGHT_SVINSSEQ_INFO);
            }
        }

        return new String[]{leftSvInsSeq, rightSvInsSeq};
    }

    @Deprecated
    public static int[] getImpreciseStart(Variant variant) {
        if (variant.getStudies()!= null
                && !variant.getStudies().isEmpty()
                && !variant.getStudies().get(0).getFiles().isEmpty()
                && variant.getStudies().get(0).getFiles().get(0).getData().containsKey(CIPOS_INFO)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getData().get(CIPOS_INFO).split(",", 2);
            return new int[]{variant.getStart() + Integer.parseInt(parts[0]),
                    variant.getStart() + Integer.parseInt(parts[1])};
        } else {
            return new int[]{variant.getStart(), variant.getStart()};
        }
    }

    @Deprecated
    public static int[] getImpreciseEnd(Variant variant) {
        if (variant.getStudies()!= null
                && !variant.getStudies().isEmpty()
                && !variant.getStudies().get(0).getFiles().isEmpty()
                && variant.getStudies().get(0).getFiles().get(0).getData().containsKey(CIEND_INFO)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getData().get(CIEND_INFO).split(",", 2);
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

    private static <T> void ifNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}