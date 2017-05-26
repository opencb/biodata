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
import org.opencb.biodata.tools.variant.converters.Converter;
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

    private final String studyId;
    private final String fileId;
    private LinkedHashMap<String, Integer> samplesPosition;

    private List<String> consequenceTypeFields;
    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());


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
            variant.setAlternate(alternateAlleleList.get(0).toString());
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
                        String genotypeValue;
//                        if (variantType.equals(VariantType.SYMBOLIC)) {
                        if (variant.getType().equals(VariantType.SYMBOLIC) || variant.getType().equals(VariantType.CNV)) {
                            genotypeValue = genotype.getGenotypeString(false).replaceAll("\\*", "");
                        } else {
                            genotypeValue = genotype.getGenotypeString(true);
                        }
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

        // set VariantAnnotation parameters
        // TODO: Read annotation from info column
        if (consequenceTypeFields != null && !consequenceTypeFields.isEmpty()) {
            variant.setAnnotation(VepParser.parseInfoCsq(consequenceTypeFields, variantContext.getAttributes().get("CSQ").toString()));
        }

        return variant;
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