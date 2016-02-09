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

package org.opencb.biodata.tools.variant.converter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.formats.variant.annotation.VepParser;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.stats.VariantStats;
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


        variant.setLength(Math.max(variant.getReference().length(), variant.getAlternate().length()));

        // TODO Nacho please add CNV when symbolic
//        variant.setType(getEnumFromString(VariantType.class, variantContext.getType().toString()));
        VariantType variantType = getEnumFromString(VariantType.class, variantContext.getType().toString());
        switch (variantType) {
            case SNP:
                if (variant.getIds().isEmpty()) {
                    variant.setType(VariantType.SNV);
                } else {
                    variant.setType(VariantType.SNP);
                }
                break;
            case INDEL:
                if (variant.getLength() > Variant.SV_THRESHOLD) {
                    if (variant.getReference().isEmpty()) {
                        variant.setType(VariantType.INSERTION);
                    } else if (variant.getAlternate().isEmpty()) {
                        variant.setType(VariantType.DELETION);
                    } else {
                        variant.setType(VariantType.SV);
                    }
                } else {
                    variant.setType(VariantType.INDEL);
                }
                break;
            default:
                variant.setType(variantType);
        }

        variant.resetHGVS();

        // set variantSourceEntry fields
        List<StudyEntry> studies = new ArrayList<>();
        StudyEntry studyEntry = new StudyEntry();

        // For time being setting the hard coded values for FileId and Study ID
        studyEntry.setStudyId(studyId);


        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileId(fileId);
        fileEntry.setCall(variantContext.getStart()
                + ":" + variantContext.getReference()
                + ":" + StringUtils.join(alternateAlleleList, ","));
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
        attributes.put(VariantVcfFactory.QUAL, Double.toString(variantContext.getPhredScaledQual()));

        // FILTER
        Set<String> filter = variantContext.getFilters();
        if (filter.isEmpty()) {
            attributes.put(VariantVcfFactory.FILTER, VCFConstants.PASSES_FILTERS_v4);
        } else {
            attributes.put(VariantVcfFactory.FILTER, StringUtils.join(filter, VCFConstants.FILTER_CODE_SEPARATOR));
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
            secondaryAlternateList.add(new AlternateCoordinate(null, null, null, null, allele, variantType));
        }
        studyEntry.setSecondaryAlternates(secondaryAlternateList);


        // set variant format
        // FIXME: This code is not respecting the original format order
        List<String> formatFields = new ArrayList<>(10);
        if (!variantContext.getGenotypes().isEmpty()) {
            htsjdk.variant.variantcontext.Genotype gt = variantContext.getGenotypes().get(0);

            //GT Field is mandatory and MUST be the first one
            formatFields.add(VCFConstants.GENOTYPE_KEY);

            if (gt.hasGQ()) {
                formatFields.add(VCFConstants.GENOTYPE_QUALITY_KEY);
            }
            if (gt.hasAD()) {
                formatFields.add(VCFConstants.GENOTYPE_ALLELE_DEPTHS);
            }
            if (gt.hasDP()) {
                formatFields.add(VCFConstants.DEPTH_KEY);
            }
            if (gt.hasPL()) {
                formatFields.add(VCFConstants.GENOTYPE_PL_KEY);
            }

            for (String key : gt.getExtendedAttributes().keySet()) {
                formatFields.add(key);
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
                        if (variantType.equals(VariantType.SYMBOLIC)) {
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
                            //TODO: Can hts return null fields?
                            value = ".";
                            System.err.println("Null value");
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
     * method to set Consequence Type Parameters
     * @return consequenceTypeList
     */
    private List<ConsequenceType> setConsequenceTypeParams(){

        List<ConsequenceType> consequenceTypeList = new ArrayList<>();
        ConsequenceType consequenceType = new ConsequenceType();
        consequenceType.setGeneName(null);
        consequenceType.setEnsemblGeneId(null);
        consequenceType.setEnsemblTranscriptId(null);
        consequenceType.setStrand(null);
        consequenceType.setBiotype(null);
        consequenceType.setCdnaPosition(null);
        consequenceType.setCdsPosition(null);
        consequenceType.setCodon(null);

        /*
         * set ExpressionValues list type parameter
         */
//        List<ExpressionValue> expressionValueList = new ArrayList<>();
//        ExpressionValue expressionValue = new ExpressionValue();
//        expressionValue.setExpression(getEnumFromString(org.opencb.biodata.models.variant.avro.ExpressionCall.class, "UP"));
        /*expressionValue.setExperimentalFactor(null);
        expressionValue.setExperimentId(null);
        expressionValue.setExpression(null);
        expressionValue.setFactorValue(null);
        expressionValue.setPvalue(null);
        expressionValue.setTechnologyPlatform(null);*/
//        expressionValueList.add(expressionValue);
//        consequenceType.setExpression(expressionValueList);

        /*
         * set ProteinSubstitutionScores list type parameter
         */
//        List<Score> proteinSubstitutionScoreList = new ArrayList<>();
//        Score score = new Score(null, null, null);
//        proteinSubstitutionScoreList.add(score);


        ProteinVariantAnnotation proteinVariantAnnotation = new ProteinVariantAnnotation();
        proteinVariantAnnotation.setSubstitutionScores(Collections.emptyList());
        consequenceType.setProteinVariantAnnotation(proteinVariantAnnotation);

        /*
         * set SoTerms list type parameter
         */
        List<SequenceOntologyTerm> sequenceOntologyTerms = new ArrayList<>();
        SequenceOntologyTerm sequenceOntologyTerm = new SequenceOntologyTerm();
        sequenceOntologyTerm.setAccession(null);
        sequenceOntologyTerm.setName(null);
        sequenceOntologyTerms.add(sequenceOntologyTerm);
        consequenceType.setSequenceOntologyTerms(sequenceOntologyTerms);

        consequenceType.setStrand(null);
        /*
         * Add consequenceType final bean to list
         */
        consequenceTypeList.add(consequenceType);
        return consequenceTypeList;
    }

    /**
     * method to set Population Frequency Parameters
     * @return populationFrequencyList
     */
    private List<PopulationFrequency> setPopulationFrequencyParams(){

        List<PopulationFrequency> populationFrequencyList = new ArrayList<>();
        PopulationFrequency populationFrequency = new PopulationFrequency();
        populationFrequency.setAltAllele(null);
        populationFrequency.setAltAlleleFreq(null);
        populationFrequency.setAltHomGenotypeFreq(null);
        populationFrequency.setHetGenotypeFreq(null);
        populationFrequency.setPopulation(null);
        populationFrequency.setRefAllele(null);
        populationFrequency.setRefAlleleFreq(null);
        populationFrequency.setRefHomGenotypeFreq(null);
        populationFrequency.setStudy(null);
//        populationFrequency.setSuperPopulation(null);

        populationFrequencyList.add(populationFrequency);
        return populationFrequencyList;
    }


    /**
     * method to set Varaint Annotation Parameters
     * @return variantAnnotation
     */
    private VariantAnnotation setVaraintAnnotationParams(){
        VariantAnnotation variantAnnotation = new VariantAnnotation();
        /*
         * set AdditionalAttributes map type parameter
         */
        Map<String, Object> additionalAttributesMap = new HashMap();
        //additionalAttributesMap.put(null, null);
        variantAnnotation.setAdditionalAttributes(additionalAttributesMap);
        /*
         * set AlternateAllele parameter
         */
        variantAnnotation.setAlternate(null);
        /*
         * set CaddScore list type parameter
         */
//        List<CaddScore> caddScoreList = new ArrayList<>();
//        CaddScore caddScore = new CaddScore();
        /*caddScore.setCScore(null);
        caddScore.setRawScore(null);
        caddScore.setTranscriptId(null);*/
//        caddScoreList.add(caddScore);
//        variantAnnotation.setCaddScore(caddScoreList);
        /*
         * set Chromosome parameter
         */
        variantAnnotation.setChromosome(null);

        /*
         * set Clinical map type parameter
         */
        variantAnnotation.setVariantTraitAssociation(new VariantTraitAssociation(Arrays.asList(), Arrays.asList(),Arrays.asList()));

        /*
         * set ConsequenceTypes list type parameter
         */
        variantAnnotation.setConsequenceTypes(setConsequenceTypeParams());
        /*
         * set ConservationScores list type parameter
         */
        List<Score> conservationScoreList = new ArrayList<>();
        Score score = new Score();
        /*score.setDescription(null);
        score.setScore(null);
        score.setSource(null);    */
        conservationScoreList.add(score);
        variantAnnotation.setConservation(conservationScoreList);

//        variantAnnotation.setEnd(0);
        /*
         * set GeneDrugInteraction map of list type parameter
         */
//        Map<String, List<String>> geneDrugInteractionMap = new HashMap<>();
        List<GeneDrugInteraction> geneDrugInteractionList = new ArrayList<>();
//        List<String> geneDrugInteractionList = new ArrayList<>();
        //geneDrugInteractionList.add("AAA");
        //geneDrugInteractionMap.put("000", geneDrugInteractionList);
        variantAnnotation.setGeneDrugInteraction(geneDrugInteractionList);

        /*
         * set Hgvs list type parameter
         */
        List<String> hgvsList = new ArrayList<>();
        //hgvsList.add(null);
        variantAnnotation.setHgvs(hgvsList);

        variantAnnotation.setId(null);
        /*
         * set PopulationFrequencies list type parameter
         */
        variantAnnotation.setPopulationFrequencies(setPopulationFrequencyParams());

        variantAnnotation.setReference(null);
        variantAnnotation.setStart(0);
        /*
         * set Xref list type parameter
         */
        List<Xref> xrefsList = new ArrayList<>();
        Xref xref = new Xref();
        /*xref.setId(null);
        xref.setSrc(null);*/
        xrefsList.add(xref);
        variantAnnotation.setXrefs(xrefsList);
        /*
         * return variantAnnotation bean
         */
        return variantAnnotation;
    }

    /**
     * method to set Variant Stats Parameters
     * @param variantHardyWeinbergStats
     * @param variantContext
     * @return variantStats
     */
    private VariantStats setVariantStatsParams(
            VariantHardyWeinbergStats variantHardyWeinbergStats,
            VariantContext variantContext) {

        VariantStats variantStats = new VariantStats();
        variantStats.setAltAllele("aa");
        variantStats.setAltAlleleCount(1);
        variantStats.setAltAlleleFreq(2.1f);
        variantStats.setCasesPercentDominant(3.1f);
        variantStats.setCasesPercentRecessive(5.1f);
        variantStats.setControlsPercentDominant(1.0f);
        variantStats.setControlsPercentRecessive(3.1f);
        variantStats.setMaf(4f);
        variantStats.setMafAllele("ss");
        variantStats.setMendelianErrors(4);
        variantStats.setMgf(3f);
        variantStats.setMgfGenotype("AA");
        variantStats.setMissingAlleles(3);
        variantStats.setMissingGenotypes(3);
        variantStats.setNumSamples(4);
        variantStats.setPassedFilters(true);
        variantStats.setQuality((float) variantContext.getPhredScaledQual());
        variantStats.setRefAllele("SS");
        variantStats.setRefAlleleCount(4);
        variantStats.setRefAlleleFreq(2f);
        variantStats.setHw(variantHardyWeinbergStats);
        variantStats.setVariantType(getEnumFromString(
                VariantType.class, variantContext.getType()
                        .toString()));

        return variantStats;
    }

    /**
     * method to set VariantHardyWeinberg Stats Parameters
     * @return variantHardyWeinbergStats
     */
    private VariantHardyWeinbergStats setVariantHardyWeinbergStatsParams() {
        VariantHardyWeinbergStats variantHardyWeinbergStats = new VariantHardyWeinbergStats();
        variantHardyWeinbergStats.setChi2(1f);
        variantHardyWeinbergStats.setEAa00(2f);
        variantHardyWeinbergStats.setEAa10(3f);
        variantHardyWeinbergStats.setEAA11(4f);
        variantHardyWeinbergStats.setN(1);
        variantHardyWeinbergStats.setNAa00(2);
        variantHardyWeinbergStats.setNAa10(3);
        variantHardyWeinbergStats.setNAA11(4);
        variantHardyWeinbergStats.setP(1f);
        variantHardyWeinbergStats.setQ(2f);
        return variantHardyWeinbergStats;
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