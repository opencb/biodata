package org.opencb.biodata.tools.variant.converters.proto;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.protobuf.VariantAnnotationProto;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.tools.variant.converters.VariantContextConverter;
import org.opencb.commons.datastore.core.ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.opencb.biodata.formats.variant.vcf4.VcfUtils.*;

/**
 * Created by jtarraga on 07/02/17.
 */
public class VariantProtoToVariantContextConverter extends VariantContextConverter<VariantProto.Variant> {

    private final Logger logger = LoggerFactory.getLogger(VariantProtoToVariantContextConverter.class);

    @Deprecated
    public VariantProtoToVariantContextConverter(int studyId) {
        super(null, null, null, null);
    }

    public VariantProtoToVariantContextConverter(String study, List<String> sampleNames, List<String> annotations) {
        this(study, sampleNames, null, annotations);
    }

    public VariantProtoToVariantContextConverter(String study, List<String> sampleNames, List<String> sampleFormats,
                                                 List<String> annotations) {
        super(study, sampleNames, sampleFormats, annotations);
    }

    @Override
    public VariantContext convert(VariantProto.Variant variant) {
        init(variant);

        VariantProto.StudyEntry studyEntry = getStudy(variant);

        List<Map<String, String>> fileAttributes = studyEntry.getFilesList().stream()
                .map(VariantProto.FileEntry::getDataMap)
                .collect(Collectors.toList());

        // CHROM START END REFERENCE ALTERNATE
        String chromosome = variant.getChromosome();
        VariantProto.VariantType type = variant.getType();
        Map<Integer, Character> referenceAlleles = buildReferenceAllelesMap(studyEntry.getFilesList().stream()
                .map(fileEntry -> fileEntry.getCall()==null?null:fileEntry.getCall().getVariantId()).iterator());
        Pair<Integer, Integer> adjustedStartEndPositions = adjustedVariantStart(variant, studyEntry.getSecondaryAlternatesList(), referenceAlleles);
        int start = adjustedStartEndPositions.getLeft();
        int end = adjustedStartEndPositions.getRight();
        List<String> alleleList = buildAlleles(variant, adjustedStartEndPositions, referenceAlleles);
        Set<String> duplicatedAlleles = getDuplicatedAlleles(chromosome, start, alleleList);
        boolean isNoVariation = type.equals(VariantProto.VariantType.NO_VARIATION);

        // ID
        String idForVcf = getIdForVcf(variant.getId(), variant.getNamesList());


        // FILTER
        final Set<String> filters = getFilters(fileAttributes);

        // QUAL
        double qual = getQuality(fileAttributes);

        // INFO
        // Cohorts stats and annotations (consequence types and population frequencies)
        ObjectMap attributes = new ObjectMap();

        addCohortStats(studyEntry, attributes);

        // if asked variant annotations are exported
        // (corresponding to the CT and POPFREQ info lines from VCF header)
        if (annotations != null && annotations.size() > 0) {
            addAnnotations(variant, attributes);
        }

        // TODO: Add other file attributes?

        // FORMAT
        // Each variant can have different FORMAT so we need to recalculate the positions.
        final Map<String, Integer> formatPositions = new HashMap<>(studyEntry.getSampleDataKeysList().size());
        for (int i = 0; i < studyEntry.getSampleDataKeysList().size(); i++) {
            formatPositions.put(studyEntry.getSampleDataKeysList().get(i), i);
        }

        // SAMPLES
        BiFunction<String, String, String> getSampleData = (sampleName, id) -> getSampleData(studyEntry, formatPositions, sampleName, id);

        List<Genotype> genotypes = getGenotypes(alleleList, studyEntry.getSampleDataKeysList(), getSampleData, duplicatedAlleles);

        return makeVariantContext(chromosome, start, end, idForVcf, alleleList, isNoVariation, filters, qual, attributes, genotypes, duplicatedAlleles);
    }

    public String getSampleData(VariantProto.StudyEntry studyEntry, Map<String, Integer> formatPositions, String sampleName, String field) {
        if (samplePositions.containsKey(sampleName) && formatPositions.containsKey(field)) {
            VariantProto.SampleEntry sample = studyEntry.getSamples(samplePositions.get(sampleName));
            int formatPos = formatPositions.get(field);
            return formatPos < sample.getDataCount() ? sample.getData(formatPos) : null;
        }
        return null;
    }


    /**
     * Adjust start/end if a reference base is required due to an empty allele. All variants are checked due to SecAlts.
     * @param variant {@link VariantProto.Variant} object.
     * @param referenceAlleles
     * @return Pair<Integer, Integer> The adjusted (or same) start/end position e.g. SV and MNV as SecAlt, INDEL, etc.
     */
    protected Pair<Integer, Integer> adjustedVariantStart(VariantProto.Variant variant,
                                                          List<VariantProto.AlternateCoordinate> secAlts,
                                                          Map<Integer, Character> referenceAlleles) {
        if (variant.getType().equals(VariantProto.VariantType.NO_VARIATION)) {
            return new ImmutablePair<>(variant.getStart(), variant.getEnd());
        }
        MutablePair<Integer, Integer> pos = adjustedVariantStart(variant.getStart(), variant.getEnd(), variant.getReference(), variant.getAlternate(), referenceAlleles, null);

        for (VariantProto.AlternateCoordinate alternateCoordinate: secAlts) {
            adjustedVariantStart(alternateCoordinate.getStart(), alternateCoordinate.getEnd(), alternateCoordinate.getReference(), alternateCoordinate.getAlternate(), referenceAlleles, pos);
        }

        return pos;
    }

    @Override
    public List<String> buildAlleles(VariantProto.Variant variant,
                                     Pair<Integer, Integer> adjustedRange, Map<Integer, Character> referenceAlleles) {
        String reference = variant.getReference();
        String alternate = variant.getAlternate();
        if (variant.getType() == VariantProto.VariantType.TANDEM_DUPLICATION && alternate.equals(VariantBuilder.DUP_ALT)) {
            alternate = VariantBuilder.DUP_TANDEM_ALT;
        }
        List<VariantProto.AlternateCoordinate> secAlts = getStudy(variant).getSecondaryAlternatesList();

        List<String> alleles = new ArrayList<>(secAlts.size() + 2);
        int origStart = variant.getStart();
        int origEnd;
        if (variant.getLength() == Variant.UNKNOWN_LENGTH) {
            origEnd = variant.getStart() + variant.getReference().length() - 1;
        } else {
            origEnd = variant.getEnd();
        }

        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, reference, adjustedRange, referenceAlleles));
        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, alternate, adjustedRange, referenceAlleles));
        for (VariantProto.AlternateCoordinate alt : secAlts) {
            alleles.add(buildAllele(variant.getChromosome(), alt.getStart(), alt.getEnd(), alt.getAlternate(), adjustedRange, referenceAlleles));
        }
        return alleles;
    }

    private void addCohortStats(VariantProto.StudyEntry studyEntry, Map<String, Object> attributes) {
        if (studyEntry.getStatsCount() == 0) {
            return;
        }

        List<String> statsList = new ArrayList<>();
        for (VariantProto.VariantStats stats : studyEntry.getStatsList()) {
            String cohortId = stats.getCohortId();
//            if (cohortId.equals(StudyEntry.DEFAULT_COHORT)) {
//                int an = stats.getAlleleCount();
//                if (an >= 0) {
//                    attributes.put(cohortId + VCFConstants.ALLELE_NUMBER_KEY, String.valueOf(an));
//                }
//                if (stats.getAltAlleleCount() >= 0) {
//                    attributes.put(cohortId + VCFConstants.ALLELE_COUNT_KEY, String.valueOf(stats.getAltAlleleCount()));
//                }
//            }
            statsList.add(cohortId + ":" + DECIMAL_FORMAT_7.format(stats.getAltAlleleFreq()));
        }
        // set cohort stats attributes
        attributes.put(STATS_INFO_KEY, String.join(FIELD_SEPARATOR, statsList));
    }

    private void addAnnotations(VariantProto.Variant variant, Map<String, Object> attributes) {
        if (variant.getAnnotation() == null) {
            return;
        }

        // consequence type
        if (variant.getAnnotation().getConsequenceTypesList() != null && !variant.getAnnotation().getConsequenceTypesList().isEmpty()) {
            List<String> ctList = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < variant.getAnnotation().getConsequenceTypesCount(); i++) {
                VariantAnnotationProto.ConsequenceType consequenceType = variant.getAnnotation().getConsequenceTypes(i);
                stringBuilder.delete(0, stringBuilder.length());
                // allele
                stringBuilder.append(variant.getAlternate());
                stringBuilder.append(FIELD_SEPARATOR);
                // gene name
                if (consequenceType.getGeneName() != null) {
                    stringBuilder.append(consequenceType.getGeneName());
                }
                stringBuilder.append(FIELD_SEPARATOR);
                // ensembl gene
                if (consequenceType.getEnsemblGeneId() != null) {
                    stringBuilder.append(consequenceType.getEnsemblGeneId());
                }
                stringBuilder.append(FIELD_SEPARATOR);
                // ensembl transcript
                if (consequenceType.getEnsemblTranscriptId() != null) {
                    stringBuilder.append(consequenceType.getEnsemblTranscriptId());
                }
                stringBuilder.append(FIELD_SEPARATOR);
                // biotype
                if (consequenceType.getBiotype() != null) {
                    stringBuilder.append(consequenceType.getBiotype());
                }
                stringBuilder.append(FIELD_SEPARATOR);
                // consequenceType
                stringBuilder.append(consequenceType.getSequenceOntologyTermsList().stream()
                        .map(VariantAnnotationProto.SequenceOntologyTerm::getName)
                        .collect(Collectors.joining(",")));
                stringBuilder.append(FIELD_SEPARATOR);

                // protein position
                if (consequenceType.getProteinVariantAnnotation() != null && consequenceType.getProteinVariantAnnotation().getPosition() > 0) {
                    stringBuilder.append(consequenceType.getProteinVariantAnnotation().getPosition());
                    stringBuilder.append(FIELD_SEPARATOR);
                    stringBuilder.append(consequenceType.getProteinVariantAnnotation().getReference())
                            .append("/")
                            .append(consequenceType.getProteinVariantAnnotation().getAlternate());
                    stringBuilder.append(FIELD_SEPARATOR);
                    if (consequenceType.getProteinVariantAnnotation().getSubstitutionScoresList() != null) {
                        List<String> sift = consequenceType.getProteinVariantAnnotation().getSubstitutionScoresList().stream()
                                .filter(t -> t.getSource().equalsIgnoreCase("sift"))
                                .map(VariantAnnotationProto.Score::getDescription)
                                .collect(Collectors.toList());
                        if (sift.size() > 0) {
                            stringBuilder.append(sift.get(0));
                        }
                        stringBuilder.append(FIELD_SEPARATOR);

                        List<String> polyphen = consequenceType.getProteinVariantAnnotation().getSubstitutionScoresList().stream()
                                .filter(t -> t.getSource().equalsIgnoreCase("polyphen"))
                                .map(VariantAnnotationProto.Score::getDescription)
                                .collect(Collectors.toList());
                        if (polyphen.size() > 0) {
                            stringBuilder.append(polyphen.get(0));
                        }
                        stringBuilder.append(FIELD_SEPARATOR);
                    }
                } else {
                    // We need to add four '|'
                    stringBuilder.append(FIELD_SEPARATOR).append(FIELD_SEPARATOR).append(FIELD_SEPARATOR).append(FIELD_SEPARATOR);
                }

                // add to ct list
                ctList.add(stringBuilder.toString());
            }

            // set consequence type attributes
            attributes.put(ANNOTATION_INFO_KEY, String.join(INFO_SEPARATOR, ctList));
        }

        // population frequencies
        List<VariantAnnotationProto.PopulationFrequency> populationFrequencies = variant.getAnnotation()
                .getPopulationFrequenciesList();
        if (populationFrequencies != null && !populationFrequencies.isEmpty()) {
            List<String> popFreqList = new ArrayList<>();
            for (VariantAnnotationProto.PopulationFrequency pf: populationFrequencies) {
                popFreqList.add(pf.getStudy() + "_" + pf.getPopulation() + ":" + DECIMAL_FORMAT_7.format(pf.getAltAlleleFreq()));
            }
            // set population frequency attributes
            attributes.put(POPFREQ_INFO_KEY, String.join(FIELD_SEPARATOR, popFreqList));
        }
    }

    @Override
    protected VariantProto.StudyEntry getStudy(VariantProto.Variant variant) {
        VariantProto.StudyEntry studyEntry = null;
        for (int i = 0; i < variant.getStudiesCount(); i++) {
//            if (this.studyIdString.equals(studyNameMap.get(variant.getStudies(i).getStudyId()))) {
            if (variant.getStudies(i).getStudyId().equals(this.studyNameMap.get(this.studyIdString))) {
                studyEntry = variant.getStudies(i);
                break;
            }
        }
        return studyEntry;
    }

    @Override
    protected Iterator<String> getStudiesId(VariantProto.Variant variant) {
        return variant.getStudiesList().stream().map(VariantProto.StudyEntry::getStudyId).iterator();
    }

/*
    @Override
    public VariantContext from(VariantProto.Variant variant) {
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder();
        int start = variant.getStart();
        int end = variant.getEnd();
        String reference = variant.getReference();
        String alternate = variant.getAlternate();

        VariantProto.VariantType type = variant.getType();
        if (type == VariantProto.VariantType.INDEL) {
            reference = "N" + reference;
            alternate = "N" + alternate;
            start -= 1; // adjust start
        }

        String filter = "PASS";
        String prk = studyId + "_PR";
        String crk = studyId + "_CR";
        String oprk = studyId + "_OPR";

        //Attributes for INFO column
        ObjectMap attributes = new ObjectMap();

        List<String> allelesArray = Arrays.asList(reference, alternate);  // TODO jmmut: multiallelic
        ArrayList<Genotype> genotypes = new ArrayList<>();
        Integer originalPosition = null;
        List<String> originalAlleles = null;
        for (VariantProto.StudyEntry studyEntry : variant.getStudiesList()) {
            String[] ori = getOri(studyEntry);
            Integer auxOriginalPosition = getOriginalPosition(ori);
            if (originalPosition != null && auxOriginalPosition != null && !originalPosition.equals(auxOriginalPosition)) {
                throw new IllegalStateException("Two or more VariantSourceEntries have different origin. Unable to merge");
            }
            originalPosition = auxOriginalPosition;
            originalAlleles = getOriginalAlleles(ori);
            if (originalAlleles == null) {
                originalAlleles = allelesArray;
            }

            //Only print those variants in which the alternate is the first alternate from the multiallelic alternatives
            if (originalAlleles.size() > 2 && !"0".equals(getOriginalAlleleIndex(ori))) {
                logger.debug("Skip multi allelic variant! " + variant);
                return null;
            }

            // TODO: check getAttributes, getOrderedSamplesName and getSampleData for proto
            try {
                String sourceFilter = studyEntry.getFiles(0).getAttributesOrDefault("FILTER", ".");
                if (sourceFilter != null && !filter.equals(sourceFilter)) {
                    filter = ".";   // write PASS iff all sources agree that the filter is "PASS" or assumed if not present, otherwise write "."
                }
            } catch (Exception e) {
                filter = ".";
            }

            attributes.putIfNotNull(prk, studyEntry.getFiles(0).getAttributes().get("PR"));
            attributes.putIfNotNull(crk, studyEntry.getFiles(0).getAttributes().get("CR"));
            attributes.putIfNotNull(oprk, studyEntry.getFiles(0).getAttributes().get("OPR"));

//            for (VariantProto.StudyEntry.SamplesDataInfoEntry sample: studyEntry.getSamplesDataList()) {
//                String gtStr = sample.getInfo(0); // studyEntry.getSampleData(sampleName, "GT");
//                String genotypeFilter = null; // studyEntry.getSampleData(sampleName, "FT");
//
//                if (gtStr != null) {
//                    List<String> gtSplit = new ArrayList<>(Arrays.asList(gtStr.split(",")));
//                    List<String> ftSplit = new ArrayList<>(Arrays.asList(
//                            (StringUtils.isBlank(genotypeFilter) ? "" : genotypeFilter).split(",")));
//                    boolean filterIsMatching = gtSplit.size() == ftSplit.size();
//                    while (gtSplit.size() > 1) {
//                        int idx = gtSplit.indexOf(".");
//                        if (idx < 0) {
//                            idx = gtSplit.indexOf("0/0");
//                        }
//                        if (idx < 0) {
//                            break;
//                        }
//                        gtSplit.remove(idx);
//                        ftSplit.remove(idx);
//                    }
//                    String gt = gtSplit.get(0);
//                    String ft = ftSplit.get(0);
//
//                    org.opencb.biodata.models.variant.Genotype genotype =
//                            new org.opencb.biodata.models.variant.Genotype(gt, reference, alternate);
//                    List<Allele> alleles = new ArrayList<>();
//                    for (int gtIdx : genotype.getAllelesIdx()) {
//                        if (gtIdx < originalAlleles.size() && gtIdx >= 0) {
//                            alleles.add(Allele.create(originalAlleles.get(gtIdx), gtIdx == 0)); // allele is ref. if the alleleIndex is 0
//                        } else {
//                            alleles.add(Allele.create(".", false)); // genotype of a secondary alternate, or an actual missing
//                        }
//                    }
//
//                    if (StringUtils.isBlank(ft)) {
//                        genotypeFilter = null;
//                    } else if (StringUtils.equals("PASS", ft)) {
//                        genotypeFilter = "1";
//                    } else {
//                        genotypeFilter = "0";
//                    }
//
//                    GenotypeBuilder builder = new GenotypeBuilder()
//                            .name(sampleName)
//                            .alleles(alleles)
//                            .phased(genotype.isPhased());
//                    if (genotypeFilter != null) {
//                        builder.attribute("PF", genotypeFilter);
//                    }
//                    for (String id : studyEntry.getFormat()) {
//                        if (id.equals("GT") || id.equals("FT")) {
//                            continue;
//                        }
//                        String value = studyEntry.getSampleData(sampleName, id);
//                        builder.attribute(id, value);
//                    }
//
//                    genotypes.add(builder.make());
//                }
//            }

            addStats(studyEntry, attributes);
        }


        if (originalAlleles == null) {
            originalAlleles = allelesArray;
        }

        variantContextBuilder.start(originalPosition == null ? start : originalPosition)
                .stop((originalPosition == null ? start : originalPosition) + originalAlleles.get(0).length() - 1)
                .chr(variant.getChromosome())
                .filter(filter); // TODO jmmut: join attributes from different source entries? what to do on a collision?

        if (genotypes.isEmpty()) {
            variantContextBuilder.noGenotypes();
        } else {
            variantContextBuilder.genotypes(genotypes);
        }

        if (type.equals(VariantProto.VariantType.NO_VARIATION) && alternate.isEmpty()) {
            variantContextBuilder.alleles(reference);
        } else {
            variantContextBuilder.alleles(originalAlleles);
        }

        // if asked variant annotations are exported
        if (annotations != null) {
            addAnnotations(variant, annotations, attributes);
        }

        variantContextBuilder.attributes(attributes);


        if (StringUtils.isNotEmpty(variant.getId()) && !variant.toString().equals(variant.getId())) {
            StringBuilder ids = new StringBuilder();
            ids.append(variant.getId());
            if (variant.getNamesList() != null) {
                for (String name : variant.getNamesList()) {
                    ids.append(VCFConstants.ID_FIELD_SEPARATOR).append(name);
                }
            }
            variantContextBuilder.id(ids.toString());
        } else {
            variantContextBuilder.id(VCFConstants.EMPTY_ID_FIELD);
        }

        return variantContextBuilder.make();
    }

    private Map<String, Object> addAnnotations(VariantProto.Variant variant, List<String> annotations, Map<String, Object> attributes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (variant.getAnnotation() == null) {
            return attributes;
        }
//        for (ConsequenceType consequenceType : variant.getAnnotation().getConsequenceTypes()) {
        for (int i = 0; i < variant.getAnnotation().getConsequenceTypesCount(); i++) {
            VariantAnnotationProto.ConsequenceType consequenceType = variant.getAnnotation().getConsequenceTypes(i);
            for (int j = 0; j < annotations.size(); j++) {
                switch (annotations.get(j)) {
                    case "allele":
                        stringBuilder.append(variant.getAlternate());
                        break;
                    case "consequenceType":
                        stringBuilder.append(consequenceType.getSequenceOntologyTermsList().stream()
                                .map(VariantAnnotationProto.SequenceOntologyTerm::getName).collect(Collectors.joining(",")));
                        break;
                    case "gene":
                        if (consequenceType.getGeneName() != null) {
                            stringBuilder.append(consequenceType.getGeneName());
                        }
                        break;
                    case "ensemblGene":
                        if (consequenceType.getEnsemblGeneId() != null) {
                            stringBuilder.append(consequenceType.getEnsemblGeneId());
                        }
                        break;
                    case "ensemblTranscript":
                        if (consequenceType.getEnsemblTranscriptId() != null) {
                            stringBuilder.append(consequenceType.getEnsemblTranscriptId());
                        }
                        break;
                    case "biotype":
                        if (consequenceType.getBiotype() != null) {
                            stringBuilder.append(consequenceType.getBiotype());
                        }
                        break;
                    case "phastCons":
                        if (variant.getAnnotation().getConservationCount() > 0) {
                            List<Double> phastCons = variant.getAnnotation().getConservationList().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("phastCons"))
                                    .map(VariantAnnotationProto.Score::getScore)
                                    .collect(Collectors.toList());
                            if (phastCons.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(phastCons.get(0)));
                            }
                        }
                        break;
                    case "phylop":
                        if (variant.getAnnotation().getConservationCount() > 0) {
                            List<Double> phylop = variant.getAnnotation().getConservationList().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("phylop"))
                                    .map(VariantAnnotationProto.Score::getScore)
                                    .collect(Collectors.toList());
                            if (phylop.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(phylop.get(0)));
                            }
                        }
                        break;
                    case "populationFrequency":
                        List<VariantAnnotationProto.PopulationFrequency> populationFrequencies = variant
                                .getAnnotation().getPopulationFrequenciesList();
                        if (populationFrequencies != null) {
                            stringBuilder.append(populationFrequencies.stream()
                                    .map(t -> t.getPopulation() + ":" + t.getAltAlleleFreq())
                                    .collect(Collectors.joining(",")));
                        }
                        break;
                    case "cDnaPosition":
                        stringBuilder.append(consequenceType.getCDnaPosition());
                        break;
                    case "cdsPosition":
                        stringBuilder.append(consequenceType.getCdsPosition());
                        break;
                    case "proteinPosition":
                        if (consequenceType.getProteinVariantAnnotation() != null) {
                            stringBuilder.append(consequenceType.getProteinVariantAnnotation().getPosition());
                        }
                        break;
                    case "sift":
                        if (consequenceType.getProteinVariantAnnotation() != null
                                && consequenceType.getProteinVariantAnnotation().getSubstitutionScoresList() != null) {
                            List<Double> sift = consequenceType.getProteinVariantAnnotation()
                                    .getSubstitutionScoresList().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("sift"))
                                    .map(VariantAnnotationProto.Score::getScore)
                                    .collect(Collectors.toList());
                            if (sift.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(sift.get(0)));
                            }
                        }
                        break;
                    case "polyphen":
                        if (consequenceType.getProteinVariantAnnotation() != null
                                && consequenceType.getProteinVariantAnnotation().getSubstitutionScoresList() != null) {
                            List<Double> polyphen = consequenceType.getProteinVariantAnnotation()
                                    .getSubstitutionScoresList().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("polyphen"))
                                    .map(VariantAnnotationProto.Score::getScore)
                                    .collect(Collectors.toList());
                            if (polyphen.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(polyphen.get(0)));
                            }
                        }
                        break;
                    case "clinvar":
                        if (variant.getAnnotation().getTraitAssociation() != null
                                && variant.getAnnotation().getTraitAssociation().getClinvarList() != null) {
                            stringBuilder.append(variant.getAnnotation().getTraitAssociation().getClinvarList().stream()
                                    .map(VariantAnnotationProto.ClinVar::getTraitsList).flatMap(Collection::stream)
                                    .collect(Collectors.joining(",")));
                        }
                        break;
                    case "cosmic":
                        if (variant.getAnnotation().getTraitAssociation() != null
                                && variant.getAnnotation().getTraitAssociation().getCosmicList() != null) {
                            stringBuilder.append(variant.getAnnotation().getTraitAssociation().getCosmicList().stream()
                                    .map(VariantAnnotationProto.Cosmic::getPrimarySite)
                                    .collect(Collectors.joining(",")));
                        }
                        break;
                    case "gwas":
                        if (variant.getAnnotation().getTraitAssociation() != null
                                && variant.getAnnotation().getTraitAssociation().getGwasList() != null) {
                            stringBuilder.append(variant.getAnnotation().getTraitAssociation().getGwasList().stream()
                                    .map(VariantAnnotationProto.Gwas::getTraitsList).flatMap(Collection::stream)
                                    .collect(Collectors.joining(",")));
                        }
                        break;
                    // TODO: check GeneDrugInteraction for proto
//                    case "drugInteraction":
//                        stringBuilder.append(variant.getAnnotation().getGeneDrugInteractionList().stream()
//                                .map(GeneDrugInteraction::getDrugName).collect(Collectors.joining(",")));
//                        break;
                    default:
                        logger.error("Unknown annotation: " + annotations.get(j));
                        break;
                }
                if (j < annotations.size() - 1) {
                    stringBuilder.append("|");
                }
            }
            if (i < variant.getAnnotation().getConsequenceTypesCount() - 1) {
                stringBuilder.append("&");
            }
        }

        attributes.put("CSQ", stringBuilder.toString());
//        infoAnnotations.put("CSQ", stringBuilder.toString().replaceAll("&|$", ""));
        return attributes;
    }

    private void addStats(VariantProto.StudyEntry studyEntry, Map<String, Object> attributes) {
        if (studyEntry.getStats() == null) {
            return;
        }
        for (Map.Entry<String, VariantProto.VariantStats> entry : studyEntry.getStats().entrySet()) {
            String cohortName = entry.getKey();
            VariantProto.VariantStats stats = entry.getValue();

            if (cohortName.equals("ALL")) { // TODO: define a VariantProto.StudyEntry.DEFAULT_COHORT)) {
                cohortName = "";
                int an = stats.getAltAlleleCount() + stats.getRefAlleleCount();
                if (an >= 0) {
                    attributes.put(cohortName + VCFConstants.ALLELE_NUMBER_KEY, String.valueOf(an));
                }
                if (stats.getAltAlleleCount() >= 0) {
                    attributes.put(cohortName + VCFConstants.ALLELE_COUNT_KEY, String.valueOf(stats.getAltAlleleCount()));
                }
            } else {
                cohortName = cohortName + "_";
            }
            attributes.put(cohortName + VCFConstants.ALLELE_FREQUENCY_KEY, DECIMAL_FORMAT_7.format(stats.getAltAlleleFreq()));
        }
    }

    /**
     * Assumes that ori is in the form "POS:REF:ALT_0(,ALT_N)*:ALT_IDX".
     * ALT_N is the n-th allele if this is the n-th variant resultant of a multiallelic vcf row
     *
     * @param ori
     * @return
     */
/*
    private static List<String> getOriginalAlleles(String[] ori) {
        if (ori != null && ori.length == 4) {
            String[] multiAllele = ori[2].split(",");
            if (multiAllele.length != 1) {
                ArrayList<String> alleles = new ArrayList<>(multiAllele.length + 1);
                alleles.add(ori[1]);
                alleles.addAll(Arrays.asList(multiAllele));
                return alleles;
            } else {
                return Arrays.asList(ori[1], ori[2]);
            }
        }

        return null;
    }

    private static String getOriginalAlleleIndex(String[] ori) {
        if (ori != null && ori.length == 4) {
            return ori[3];
        }
        return null;
    }
*/
    /**
     * Assumes that ori is in the form "POS:REF:ALT_0(,ALT_N)*:ALT_IDX".
     *
     * @param ori
     * @return
     */
    /*
    private static Integer getOriginalPosition(String[] ori) {

        if (ori != null && ori.length == 4) {
            return Integer.parseInt(ori[0]);
        }

        return null;
    }

    private static String[] getOri(VariantProto.StudyEntry studyEntry) {

        List<VariantProto.FileEntry> files = studyEntry.getFilesList();
        if (!files.isEmpty()) {
            String call = files.get(0).getCall();
            if (call != null && !call.isEmpty()) {
                return call.split(":");
            }
        }
        return null;
    }
    */
}

