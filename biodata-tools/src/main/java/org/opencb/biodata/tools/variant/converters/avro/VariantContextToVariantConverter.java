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
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
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
        return getBuilder(variantContext).build(reuse);
    }

    /**
     *
     * @param variantContext
     * @param reuse an instance to reuse.
     * @return
     */
    public VariantProto.Variant convertToProto(VariantContext variantContext, VariantProto.Variant reuse) {
        return getBuilder(variantContext).buildProtoVariant(reuse);
    }

    /**
     *
     * @param variantContext
     * @return
     */
    private VariantBuilder getBuilder(VariantContext variantContext) {
        VariantBuilder builder = Variant.newBuilder()
                .setChromosome(variantContext.getContig())
                .setStart(variantContext.getStart())
                .setEnd(variantContext.getEnd())
                .setStudyId(studyId)
                .setFileId(fileId);

        // Setting reference and alternate alleles
        String reference = variantContext.getReference().getDisplayString();
        builder.setReference(reference);

        List<Allele> alternateAlleleList = variantContext.getAlternateAlleles();
        List<String> alternates;
        if (alternateAlleleList.isEmpty()) {
            alternates = Collections.singletonList("");
            builder.setAlternate("");
        } else {
            alternates = new ArrayList<>(alternateAlleleList.size());
            for (Allele alternate : alternateAlleleList) {
                alternates.add(alternate.toString());
            }
            builder.setAlternates(alternates);
        }

        //Do not need to store dot ID. It means that this variant does not have any ID
        String[] idsArray = variantContext.getID().split(VCFConstants.ID_FIELD_SEPARATOR);
        List<String> ids = new ArrayList<>(idsArray.length);
        for (String id : idsArray) {
            if (!id.equals(".")) {
                ids.add(id);
            }
        }
        builder.setNames(ids);

        if (variantContext.getType().equals(VariantContext.Type.NO_VARIATION)) {
            builder.setType(VariantType.NO_VARIATION);
        }

        // INFO
        for (String key : variantContext.getAttributes().keySet()) {
            // Do not use "getAttributeAsString" for lists.
            // It will add brackets surrounding the values
            if (variantContext.getAttribute(key, "") instanceof List) {
                builder.addAttribute(key, variantContext.getAttributeAsList(key));
            } else {
                builder.addAttribute(key, variantContext.getAttributeAsString(key, ""));
            }
        }

        //TODO: Call to the Variant Aggregated Stats Parser ??
//        builder.setStats(new HashMap<>());

        // QUAL
        if (variantContext.getLog10PError() != VariantContext.NO_LOG10_PERROR) {
            builder.setQuality(variantContext.getPhredScaledQual());
        }

        // FILTER
        Set<String> filter = variantContext.getFiltersMaybeNull();
        if (filter == null) {
            builder.setFilter(VCFConstants.UNFILTERED);
        } else if (filter.isEmpty()) {
            builder.setFilter(VCFConstants.PASSES_FILTERS_v4);
        } else {
            if (filter.size() == 1) {
                builder.setFilter(filter.iterator().next());
            } else {
                builder.setFilter(filter.stream()
                        .sorted()
                        .collect(Collectors.joining(VCFConstants.FILTER_CODE_SEPARATOR)));
            }
        }

        // FORMAT
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
        builder.setFormat(formatFields);

        Map<Allele, String> allelesMap = getAlleleStringMap(variantContext);

        // GENOTYPES
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
                            value = new Genotype(genotypeValue, reference, alternates).toString();
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
        builder.setSamplesPosition(samplesPosition);
        builder.setSamplesData(sampleDataList);

        return builder;
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