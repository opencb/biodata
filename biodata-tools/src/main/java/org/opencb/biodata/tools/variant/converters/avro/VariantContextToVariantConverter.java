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

package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.formats.variant.annotation.VepParser;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.variant.StudyEntry.isSamplesPositionMapSorted;


/**
 * @author Pawan Pal & Kalyan
 *
 */
public class VariantContextToVariantConverter implements Converter<VariantContext, Variant>, Serializable {

    private static final EnumSet<VariantType> SV_TYPES = EnumSet.of(VariantType.INSERTION, VariantType.DELETION,
            VariantType.TRANSLOCATION, VariantType.INVERSION, VariantType.CNV, VariantType.DUPLICATION,
            VariantType.BREAKEND, VariantType.SV, VariantType.SYMBOLIC);
    private final String studyId;
    private final String fileId;
    private LinkedHashMap<String, Integer> samplesPosition;
    private List<String> consequenceTypeFields;

    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    private static final String CIPOS_STRING = "CIPOS";
    private static final String CIEND_STRING = "CIEND";
    private static final String SVINSSEQ = "SVINSSEQ";
    private static final String LEFT_SVINSSEQ = "LEFT_SVINSSEQ";
    private static final String RIGHT_SVINSSEQ = "RIGHT_SVINSSEQ";
    private static final String DUPSTR = "<DUP>";
    private static final String TANDEMDUPSTR = "<DUP:TANDEM>";


    VariantContextToVariantConverter(){
        this("", "", null);
    }

    @Deprecated
    public VariantContextToVariantConverter(String studyId, String fileId) {
        this(studyId, fileId, null);
    }

    public VariantContextToVariantConverter(String studyId, String fileId, List<String> samples) {
        this.studyId = studyId;
        this.fileId = fileId;

        // TODO this must be parsed from VCF header
        consequenceTypeFields = Arrays.asList();

        samplesPosition = createSamplesPositionMap(samples);

    }

    private static LinkedHashMap<String, Integer> createSamplesPositionMap(List<String> samples) {
        if (samples == null) {
            return null;
        }
        LinkedHashMap<String, Integer> samplesPosition = new LinkedHashMap<>();
        int position = 0;
        for (String sample : samples) {
            samplesPosition.put(sample, position++);
        }
        isSamplesPositionMapSorted(samplesPosition);
        return samplesPosition;
    }

    @Override
    public Variant convert(VariantContext variantContext) {
        return convert(variantContext, new Variant());
    }

    /**
     *
     * @param variantContext
     * @param reuse an instance to reuse.
     * @return
     */
    public Variant convert(VariantContext variantContext, Variant reuse) {
        Variant variant = reuse;

        variant.setChromosome(variantContext.getContig());
        variant.setStart(variantContext.getStart());
        variant.setEnd(variantContext.getEnd());

        // Setting reference and alternate alleles
        variant.setReference(variantContext.getReference().getDisplayString());
        List<Allele> alternateAlleleList = variantContext.getAlternateAlleles();
        if (alternateAlleleList != null && !alternateAlleleList.isEmpty()) {
            String alternateString = alternateAlleleList.get(0).toString();
//            if (Allele.wouldBeSymbolicAllele(alternateString.getBytes())) {
//                structuralVariation = new StructuralVariation();
//            }
//            if (alternateString.equals(TANDEMDUPSTR)) {
//                variant.setAlternate(DUPSTR);
//                structuralVariation.setType(StructuralVariantType.TANDEM_DUPLICATION);
//            } else {

            // Be aware! alternate may be modified later in this code: <INS> variants with known sequence will
            // set the inserted sequence in the alternate, once the inserted sequence is parsed from the INFO field
            // Also, <DUP:TANDEM> alternates will later be replaced by <DUP>
            variant.setAlternate(alternateString);
//            }
        } else {
            alternateAlleleList = Collections.emptyList();
            variant.setAlternate("");
        }

        //Do not need to store dot ID. It means that this variant does not have any ID
        String[] idsArray = variantContext.getID().split(VCFConstants.ID_FIELD_SEPARATOR);
        List<String> ids = new ArrayList<>(idsArray.length);
        for (String id : idsArray) {
            if (!id.equals(".")) {
                ids.add(id);
            }
        }
        variant.setIds(ids);



        // TODO Nacho please add CNV when symbolic

        final VariantType type;
        if (!variantContext.getType().equals(VariantContext.Type.NO_VARIATION)) {
            type = Variant.inferType(variant.getReference(), variant.getAlternate());
        } else {
            type = VariantType.NO_VARIATION;
        }
        variant.setType(type);

        // Create and initialize StructuralVariation object if needed
        StructuralVariation structuralVariation = null;
        if (SV_TYPES.contains(variant.getType())) {
            variant.resetSV();
            structuralVariation = variant.getSv();
            if (variant.getAlternate().equals(TANDEMDUPSTR)) {
                variant.setAlternate(DUPSTR);
                variant.setType(VariantType.DUPLICATION);
                structuralVariation.setType(StructuralVariantType.TANDEM_DUPLICATION);
            }
        }

        variant.resetLength();

        // set variantSourceEntry fields
        List<StudyEntry> studies = new ArrayList<>();
        StudyEntry studyEntry = new StudyEntry();

        // For time being setting the hard coded values for FileId and Study ID
        studyEntry.setStudyId(studyId);


        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileId(fileId);
        fileEntry.setCall("");
        Map<String, String> attributes = new HashMap<>();
        for (String key : variantContext.getAttributes().keySet()) {
            // Do not use "getAttributeAsString" for lists.
            // It will add brackets surrounding the values
            if (variantContext.getAttribute(key, "") instanceof List) {
                attributes.put(key, StringUtils.join(variantContext.getAttributeAsList(key), VCFConstants.INFO_FIELD_ARRAY_SEPARATOR));
            } else {
                attributes.put(key, variantContext.getAttributeAsString(key, ""));
            }
            // Be aware! this method below may change variant.alternate
            parseStructuralVariationAttributes(structuralVariation, variant, key, attributes.get(key));
        }

        // QUAL
        if (variantContext.getLog10PError() != VariantContext.NO_LOG10_PERROR) {
            attributes.put(StudyEntry.QUAL, Double.toString(variantContext.getPhredScaledQual()));
        }

        // FILTER
        Set<String> filter = variantContext.getFiltersMaybeNull();
        if (filter == null) {
            attributes.put(StudyEntry.FILTER, VCFConstants.UNFILTERED);
        } else if (filter.isEmpty()) {
            attributes.put(StudyEntry.FILTER, VCFConstants.PASSES_FILTERS_v4);
        } else {
            if (filter.size() == 1) {
                attributes.put(StudyEntry.FILTER, filter.iterator().next());
            } else {
                attributes.put(StudyEntry.FILTER, filter
                        .stream().sorted().collect(Collectors.joining(VCFConstants.FILTER_CODE_SEPARATOR)));
            }
        }

        fileEntry.setAttributes(attributes);
        studyEntry.setFiles(Collections.singletonList(fileEntry));


        // We need to convert Allele object to String
        // We skip the first alternate allele since these are the secondaries
        List<AlternateCoordinate> secondaryAlternateList = new ArrayList<>(Math.max(alternateAlleleList.size() - 1, 0));
        List<String> alternates = new ArrayList<>(alternateAlleleList.size());
        if (alternateAlleleList.size() > 0) {
            alternates.add(alternateAlleleList.get(0).toString());
        }
        for (int i = 1; i < alternateAlleleList.size(); i++) {
            String allele = alternateAlleleList.get(i).toString();
            alternates.add(allele);
            secondaryAlternateList.add(new AlternateCoordinate(null, null, null, null, allele, variant.getType()));
//            secondaryAlternateList.add(new AlternateCoordinate(null, null, null, null, allele, variantType));
        }
        studyEntry.setSecondaryAlternates(secondaryAlternateList);


        // set variant format
        // FIXME: This code is not respecting the original format order
        List<String> formatFields = new ArrayList<>(10);
        if (!variantContext.getGenotypes().isEmpty()) {
            htsjdk.variant.variantcontext.Genotype gt = variantContext.getGenotypes().get(0);

            //FullVCFCodec saves ALL the format fields in the ExtendedAttributes map.
            for (String key : gt.getExtendedAttributes().keySet()) {
                if (key.equals(VCFConstants.GENOTYPE_KEY)) {
                    //GT must be the first one
                    formatFields.add(0, key);
                } else {
                    formatFields.add(key);
                }
            }
        }
        studyEntry.setFormat(formatFields);

        Map<Allele, String> allelesMap = getAlleleStringMap(variantContext);

        if (samplesPosition == null) {
            logger.warn("Using alphabetical order for samples position!");
            samplesPosition = createSamplesPositionMap(variantContext.getSampleNamesOrderedByName());
        }
        List<List<String>> sampleDataList = new ArrayList<>(samplesPosition.size());
        for (String sampleName : samplesPosition.keySet()) {
            htsjdk.variant.variantcontext.Genotype genotype = variantContext.getGenotype(sampleName);
            List<String> sampleList = new ArrayList<>(formatFields.size());

            for (String formatField : formatFields) {
                final String value;
                switch (formatField) {
                    case VCFConstants.GENOTYPE_KEY:
                        String genotypeValue = genotypeToString(allelesMap, genotype);
                        // sometimes (FreeBayes) a single '.' is written for some samples
                        if (genotypeValue.equals(".")) {
                            value = "./.";
                        } else {
                            value = new Genotype(genotypeValue, variant.getReference(), alternates).toString();
                        }
                        break;
                    default:
                        Object attribute = genotype.getAnyAttribute(formatField);
                        if (attribute != null) {
                            if (attribute instanceof Collection) {
                                value = ((List<Object>) attribute).stream().map(Object::toString).collect(Collectors.joining(","));
                            } else {
                                value = attribute.toString();
                            }
                        } else {
                            //Can hts return null fields?
                            //ABSOLUTELY, for missing values
                            value = ".";
                        }
                        break;
                }
                sampleList.add(value);
            }
            sampleDataList.add(sampleList);
        }
        studyEntry.setSamplesData(sampleDataList);
        studyEntry.setSamplesPosition(samplesPosition);


        /*
         * set stats fields. Putting hard coded values for time
         * being as these value will not be getting from HTSJDK
         * currently.
         */
        Map<String, VariantStats> stats = new HashMap<>();
        //TODO: Call to the Variant Aggregated Stats Parser
//        stats.put(
//                "2",
//                setVariantStatsParams(
//                        setVariantHardyWeinbergStatsParams(),
//                        variantContext));
        studyEntry.setStats(stats);

        studies.add(studyEntry);
        variant.setStudies(studies);

        // Set the CNV type
//        if (variant.getType().equals(VariantType.CNV)) {
//            Integer copyNumber = Variant.getCopyNumberFromAlternate(variant.getAlternate());
//            if (copyNumber != null) {
//                structuralVariation.setCopyNumber(copyNumber);
//                structuralVariation.setType(Variant.getCNVSubtype(copyNumber));
//            }
//        }
//
//        if (structuralVariation != null) {
//            structuralVariation.setCiStartLeft(variant.getStart());
//            structuralVariation.setCiStartRight(variant.getStart());
//            structuralVariation.setCiEndLeft(variant.getEnd());
//            structuralVariation.setCiEndRight(variant.getEnd());
//            variant.setSv(structuralVariation);
//        }

        // set VariantAnnotation parameters
        // TODO: Read annotation from info column
        if (consequenceTypeFields != null && !consequenceTypeFields.isEmpty()) {
            variant.setAnnotation(VepParser.parseInfoCsq(consequenceTypeFields, variantContext.getAttributes().get("CSQ").toString()));
        }

        return variant;
    }

    private void parseStructuralVariationAttributes(StructuralVariation structuralVariation, Variant variant,
                                                    String attribute, String value) {
        switch (attribute) {
            case SVINSSEQ:
                // Seen DELETIONS with this field set - makes no sense
                if (VariantType.INSERTION.equals(variant.getType())) {
                    variant.setAlternate(value);
                }
                break;
            case LEFT_SVINSSEQ:
                if (VariantType.INSERTION.equals(variant.getType())) {
                    structuralVariation.setLeftSvInsSeq(value);
                }
                break;
            case RIGHT_SVINSSEQ:
                // Seen DELETIONS with this field set - makes no sense
                if (VariantType.INSERTION.equals(variant.getType())) {
                    structuralVariation.setRightSvInsSeq(value);
                }
                break;
            case CIPOS_STRING:
                String[] parts = value.split(",");
                structuralVariation.setCiStartLeft(variant.getStart() + Integer.parseInt(parts[0]));
                structuralVariation.setCiStartRight(variant.getStart() + Integer.parseInt(parts[1]));
                break;
            case CIEND_STRING:
                parts = value.split(",");
                structuralVariation.setCiEndLeft(variant.getEnd() + Integer.parseInt(parts[0]));
                structuralVariation.setCiEndRight(variant.getEnd() + Integer.parseInt(parts[1]));
                break;
        }

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
            Integer copyNumber = Variant.getCopyNumberFromAlternate(variant.getAlternate());
            if (copyNumber != null) {
                sv.setCopyNumber(copyNumber);
                sv.setType(Variant.getCNVSubtype(copyNumber));
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
            if (variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(LEFT_SVINSSEQ)) {
                leftSvInsSeq = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(LEFT_SVINSSEQ);
            }
            if (variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(RIGHT_SVINSSEQ)) {
                rightSvInsSeq = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(RIGHT_SVINSSEQ);
            }
        }

        return new String[]{leftSvInsSeq, rightSvInsSeq};
    }

    public static int[] getImpreciseStart(Variant variant) {
        if (variant.getStudies()!= null
                && !variant.getStudies().isEmpty()
                && !variant.getStudies().get(0).getFiles().isEmpty()
                && variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(CIPOS_STRING)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(CIPOS_STRING).split(",");
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
                && variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(CIEND_STRING)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(CIEND_STRING).split(",");
            return new int[]{variant.getEnd() + Integer.parseInt(parts[0]),
                    variant.getEnd() + Integer.parseInt(parts[1])};
        } else {
            return new int[]{variant.getEnd(), variant.getEnd()};
        }
    }

    public static Map<Allele, String> getAlleleStringMap(VariantContext variantContext) {
        List<Allele> alleles = variantContext.getAlleles();
        Map<Allele, String> allelesMap = new HashMap<>(alleles.size() + 1);
        for (Allele allele : alleles) {
            allelesMap.put(allele, String.valueOf(allelesMap.size()));
        }
        allelesMap.put(Allele.NO_CALL, VCFConstants.EMPTY_ALLELE);
        return allelesMap;
    }

    // TODO: Move to an abstract class
    public static String genotypeToString(Map<Allele, String> allelesMap, htsjdk.variant.variantcontext.Genotype genotype) {
        String genotypeValue;
        StringBuilder gt = new StringBuilder();
        for (Allele allele : genotype.getAlleles()) {
            if (gt.length() > 0) {
                gt.append(genotype.isPhased() ? VCFConstants.PHASED : VCFConstants.UNPHASED);
            }
            gt.append(allelesMap.get(allele));
        }
        genotypeValue = gt.toString();
        return genotypeValue;
    }

    /**
     * @param variantType
     * @param string
     * @return
     */
    private static <E extends Enum<E>> E getEnumFromString(Class<E> variantType, String string) {
        if (variantType != null && string != null) {
            try {
                return Enum.valueOf(variantType, string.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown variantType " + string);
            }
        }
        return null;
    }
}