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

package org.opencb.biodata.tools.variant.converters.proto;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.protobuf.CommonModel;
import org.opencb.biodata.models.variant.protobuf.VariantAnnotationProto;
import org.opencb.biodata.models.variant.protobuf.VariantAnnotationProto.ConsequenceType;
import org.opencb.biodata.models.variant.protobuf.VariantAnnotationProto.ProteinVariantAnnotation;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.models.variant.protobuf.VariantProto.AlternateCoordinate;
import org.opencb.biodata.tools.Converter;
import org.opencb.biodata.tools.variant.converters.avro.VariantContextToVariantConverter;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Pawan Pal & Kalyan
 *
 */
public class VariantContextToVariantProtoConverter implements Converter<VariantContext, VariantProto.Variant> {

    private final String studyId;
    private final String fileId;

    public VariantContextToVariantProtoConverter(){
        this("", "");
    }

    public VariantContextToVariantProtoConverter(String studyId, String fileId) {
        this.studyId = studyId;
        this.fileId = fileId;
    }

    @Override
    public VariantProto.Variant convert(VariantContext variantContext) {
        return convert(variantContext, VariantProto.Variant.newBuilder().build());
    }

    /**
     *
     * @param variantContext
     * @param reuse an instance to reuse.
     * @return
     */
    public VariantProto.Variant convert(VariantContext variantContext, VariantProto.Variant reuse) {
        VariantProto.Variant.Builder variant = reuse.toBuilder();

        variant.setChromosome(variantContext.getContig());
        variant.setStart(variantContext.getStart());
        variant.setEnd(variantContext.getEnd());

        // Setting reference and alternate alleles
        variant.setReference(variantContext.getReference().getDisplayString());
        List<Allele> alternateAlleleList = variantContext.getAlternateAlleles();
        if (alternateAlleleList != null && !alternateAlleleList.isEmpty()) {
            variant.setAlternate(alternateAlleleList.get(0).toString());
        } else {
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

        if (!ids.isEmpty()) {
            variant.setId(ids.get(0));
            variant.addAllNames(ids.subList(1, ids.size()));
        }

        variant.setLength(Math.max(variant.getReference().length(), variant.getAlternate().length()));

        variant.setType(getEnumFromString(VariantProto.VariantType.class, variantContext.getType().toString()));

        // TODO
//        variant.resetHGVS();

        // set variantSourceEntry fields
        List<VariantProto.StudyEntry> studies = new ArrayList<>();
        VariantProto.StudyEntry.Builder variantSourceEntry = VariantProto.StudyEntry.newBuilder();

        // For time being setting the hard coded values for FileId and Study ID
        variantSourceEntry.setStudyId(studyId);

        VariantProto.FileEntry.Builder fileEntry = VariantProto.FileEntry.newBuilder();
        fileEntry.setFileId(fileId);
        fileEntry.setCall(variantContext.getStart()
                + ":" + variantContext.getReference()
                + ":" + StringUtils.join(variantContext.getAlternateAlleles(), ","));
        Map<String, String> attributes = new HashMap<>();
        for (String key : variantContext.getAttributes().keySet()) {
            attributes.put(key, variantContext.getAttributeAsString(key, ""));
        }
        fileEntry.putAllAttributes(attributes);
        variantSourceEntry.addAllFiles(Arrays.asList(fileEntry.build()));
//        variantSourceEntry.setFiles(0, fileEntry);


        // We need to convert Allele object to String
        // We skip the first alternate allele since these are the secondaries
        List<AlternateCoordinate> secondaryAlternateList = new ArrayList<AlternateCoordinate>();
        for (int i = 1; i < variantContext.getAlternateAlleles().size(); i++) {
            Allele allele = variantContext.getAlternateAlleles().get(i);
            AlternateCoordinate.Builder altBuilder = AlternateCoordinate.newBuilder().setAlternate(allele.toString());
            AlternateCoordinate alt = altBuilder.build();
            secondaryAlternateList.add(alt);
        }
        variantSourceEntry.addAllSecondaryAlternates(secondaryAlternateList);

        // set variant format
        // FIXME: This code is not respecting the original format order
        LinkedList<String> formatFields = new LinkedList<>();
        if (variantContext.getGenotypes().size() > 1) {
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
        variantSourceEntry.addAllFormat(formatFields);

        Map<Allele, String> allelesMap = VariantContextToVariantConverter.getAlleleStringMap(variantContext);

        // set sample data parameters Eg: GT:GQ:GQX:DP:DPF:AD 1/1:63:29:22:7:0,22
//        List<List<String>> sampleDataList = new ArrayList<>(variantContext.getSamplesName().size());
        List<VariantProto.StudyEntry.SamplesDataInfoEntry> sampleDataList = new ArrayList<>(formatFields.size());
        for (String sampleName : variantContext.getSampleNames()) {
            htsjdk.variant.variantcontext.Genotype genotype = variantContext.getGenotype(sampleName);
            List<String> sampleList = new ArrayList<>(formatFields.size());

            for (String formatField : formatFields) {
                final String value;
                switch (formatField) {
                    case VCFConstants.GENOTYPE_KEY:
                        value = VariantContextToVariantConverter.genotypeToString(allelesMap, genotype);
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
//            sampleDataList.add(sampleList);
            sampleDataList.add(VariantProto.StudyEntry.SamplesDataInfoEntry.newBuilder().addAllInfo(sampleList).build());
        }
        variantSourceEntry.addAllSamplesData(sampleDataList);


        /*
         * set stats fields. Putting hard coded values for time
         * being as these value will not be getting from HTSJDK
         * currently.
         */
        Map<String, VariantProto.VariantStats> stats = new HashMap<>();
        //TODO: Call to the Variant Aggregated Stats Parser
//        stats.put(
//                "2",
//                setVariantStatsParams(
//                        setVariantHardyWeinbergStatsParams(),
//                        variantContext));
        variantSourceEntry.putAllStats(stats);

        studies.add(variantSourceEntry.build());
        variant.addAllStudies(studies);


        // set VariantAnnotation parameters
        // TODO: Read annotation from info column
//        variant.setAnnotation(setVariantAnnotationParams());


        return variant.build();
    }

    /**
     * method to set Consequence Type Parameters
     * @return consequenceTypeList
     */
    private List<ConsequenceType> setConsequenceTypeParams(){

        List<ConsequenceType> consequenceTypeList = new ArrayList<>();
        ConsequenceType.Builder consequenceType = ConsequenceType.newBuilder();
        consequenceType.setGeneName(null);
        consequenceType.setEnsemblGeneId(null);
        consequenceType.setEnsemblTranscriptId(null);
        consequenceType.setStrand(null);
        consequenceType.setBiotype(null);
        consequenceType.setCDnaPosition(0);
        consequenceType.setCdsPosition(0);
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


        ProteinVariantAnnotation.Builder proteinVariantAnnotation = ProteinVariantAnnotation.newBuilder();
        proteinVariantAnnotation.addAllSubstitutionScores(Arrays.asList());
        consequenceType.setProteinVariantAnnotation(proteinVariantAnnotation);

        /*
         * set SoTerms list type parameter
         */
        List<VariantAnnotationProto.SequenceOntologyTerm> sequenceOntologyTerms = new ArrayList<>();
        VariantAnnotationProto.SequenceOntologyTerm.Builder sequenceOntologyTerm = VariantAnnotationProto.SequenceOntologyTerm.newBuilder();
        sequenceOntologyTerm.setAccession(null);
        sequenceOntologyTerm.setName(null);
        sequenceOntologyTerms.add(sequenceOntologyTerm.build());
        consequenceType.addAllSequenceOntologyTerms(sequenceOntologyTerms);

        consequenceType.setStrand(null);
        /*
         * Add consequenceType final bean to list
         */
        consequenceTypeList.add(consequenceType.build());
        return consequenceTypeList;
    }

    /**
     * method to set Population Frequency Parameters
     * @return populationFrequencyList
     */
    private List<VariantAnnotationProto.PopulationFrequency> setPopulationFrequencyParams(){

        List<VariantAnnotationProto.PopulationFrequency> populationFrequencyList = new ArrayList<>();
        VariantAnnotationProto.PopulationFrequency.Builder populationFrequency = VariantAnnotationProto.PopulationFrequency.newBuilder();
        populationFrequency.setAltAllele(null);
        populationFrequency.setAltAlleleFreq(0.0f);
        populationFrequency.setAltHomGenotypeFreq(0.0f);
        populationFrequency.setHetGenotypeFreq(0.0f);
        populationFrequency.setPopulation(null);
        populationFrequency.setRefAllele(null);
        populationFrequency.setRefAlleleFreq(0.0f);
        populationFrequency.setRefHomGenotypeFreq(0.0f);
        populationFrequency.setStudy(null);

        populationFrequencyList.add(populationFrequency.build());
        return populationFrequencyList;
    }


    /**
     * method to set Varaint Annotation Parameters
     * @return variantAnnotation
     */
    private VariantAnnotationProto.VariantAnnotation setVaraintAnnotationParams(){
        VariantAnnotationProto.VariantAnnotation.Builder variantAnnotation = VariantAnnotationProto.VariantAnnotation.newBuilder();
        /*
         * set AdditionalAttributes map type parameter
         */
        //additionalAttributesMap.put(null, null);
        HashMap<String, VariantAnnotationProto.VariantAnnotation.AdditionalAttribute> map = new HashMap<>();
        variantAnnotation.putAllAdditionalAttributes(map);
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
        VariantAnnotationProto.VariantTraitAssociation.Builder variantTraitAssociation = VariantAnnotationProto.VariantTraitAssociation.newBuilder();
        variantTraitAssociation.addAllClinvar(Arrays.asList());
        variantTraitAssociation.addAllCosmic(Arrays.asList());
        variantTraitAssociation.addAllGwas(Arrays.asList());
        variantAnnotation.setVariantTraitAssociation(variantTraitAssociation);

        /*
         * set ConsequenceTypes list type parameter
         */
        variantAnnotation.addAllConsequenceTypes(setConsequenceTypeParams());
        /*
         * set ConservationScores list type parameter
         */
        List<VariantAnnotationProto.Score> conservationScoreList = new ArrayList<>();
        VariantAnnotationProto.Score.Builder score = VariantAnnotationProto.Score.newBuilder();
        /*score.setDescription(null);
        score.setScore(null);
        score.setSource(null);    */
        conservationScoreList.add(score.build());
        variantAnnotation.addAllConservation(conservationScoreList);

        variantAnnotation.setEnd(0);
        /*
         * set GeneDrugInteraction map of list type parameter
         */
//        Map<String, List<String>> geneDrugInteractionMap = new HashMap<>();
        List<CommonModel.GeneDrugInteraction> geneDrugInteractionList = new ArrayList<>();
//        List<String> geneDrugInteractionList = new ArrayList<>();
        //geneDrugInteractionList.add("AAA");
        //geneDrugInteractionMap.put("000", geneDrugInteractionList);
        variantAnnotation.addAllGeneDrugInteraction(geneDrugInteractionList);

        /*
         * set Hgvs list type parameter
         */
        List<String> hgvsList = new ArrayList<>();
        //hgvsList.add(null);
        variantAnnotation.addAllHgvs(hgvsList);

        variantAnnotation.setId(null);
        /*
         * set PopulationFrequencies list type parameter
         */
        variantAnnotation.addAllPopulationFrequencies(setPopulationFrequencyParams());

        variantAnnotation.setReference(null);
        variantAnnotation.setStart(0);
        /*
         * set Xref list type parameter
         */
        List<VariantAnnotationProto.VariantAnnotation.Xref> xrefsList = new ArrayList<>();
        VariantAnnotationProto.VariantAnnotation.Xref.Builder xref = VariantAnnotationProto.VariantAnnotation.Xref.newBuilder();
        /*xref.setId(null);
        xref.setSrc(null);*/
        xrefsList.add(xref.build());
        variantAnnotation.addAllXrefs(xrefsList);
        /*
         * return variantAnnotation bean
         */
        return variantAnnotation.build();
    }

    /**
     * method to set Variant Stats Parameters
     * @param variantHardyWeinbergStats
     * @param variantContext
     * @return variantStats
     */
    private VariantProto.VariantStats setVariantStatsParams(
            VariantProto.VariantHardyWeinbergStats variantHardyWeinbergStats,
            VariantContext variantContext) {

        VariantProto.VariantStats.Builder variantStats = VariantProto.VariantStats.newBuilder();
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
        variantStats.setVariantType(getEnumFromString(VariantProto.VariantType.class, variantContext.getType()
                        .toString()));

        return variantStats.build();
    }

    /**
     * method to set VariantHardyWeinberg Stats Parameters
     * @return variantHardyWeinbergStats
     */
    private VariantProto.VariantHardyWeinbergStats setVariantHardyWeinbergStatsParams() {
        VariantProto.VariantHardyWeinbergStats.Builder variantHardyWeinbergStats = VariantProto.VariantHardyWeinbergStats.newBuilder();
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
        return variantHardyWeinbergStats.build();
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