package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.stats.VariantStats;
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
public class VariantAvroToVariantContextConverter extends VariantContextConverter<Variant> {

    private final Logger logger = LoggerFactory.getLogger(VariantAvroToVariantContextConverter.class);

    @Deprecated
    public VariantAvroToVariantContextConverter(int studyId, List<String> sampleNames, List<String> annotations) {
        super(Integer.toString(studyId), sampleNames, null, annotations);
    }

    public VariantAvroToVariantContextConverter(String study, List<String> sampleNames, List<String> annotations) {
        this(study, sampleNames, null, annotations);
    }

    public VariantAvroToVariantContextConverter(String study, List<String> sampleNames, List<String> sampleFormats,
                                                List<String> annotations) {
        super(study, sampleNames, sampleFormats, annotations);
    }

    @Override
    public VariantContext convert(Variant variant) {
        try {
            return createContext(variant);
        } catch (RuntimeException e) {
            try {
                logger.warn("Error creating VariantContext for variant " + variant);
                logger.warn("JSON : " + variant.toJson());
            } catch (RuntimeException e2) {
                e.addSuppressed(e2);
            }
            throw e;
        }
    }

    protected VariantContext createContext(Variant variant) {
        init(variant);

        StudyEntry studyEntry = getStudy(variant);
        List<Map<String, String>> fileAttributes = studyEntry.getFiles().stream()
                .map(FileEntry::getData)
                .collect(Collectors.toList());

        // CHROM START END REFERENCE ALTERNATE
        String chromosome = variant.getChromosome();
        VariantType type = variant.getType();
        Map<Integer, Character> referenceAlleles = buildReferenceAllelesMap(studyEntry.getFiles()
                .stream()
                .map(entry -> entry.getCall() == null ? null : entry.getCall().getVariantId())
                .iterator());
        Pair<Integer, Integer> adjustedStartEndPositions = adjustedVariantStart(variant, studyEntry, referenceAlleles, Collections.emptySet());
        int start = adjustedStartEndPositions.getLeft();
        int end = adjustedStartEndPositions.getRight();
        List<String> alleleList = buildAlleles(variant, adjustedStartEndPositions, referenceAlleles);
        Set<Integer> discardedAlleleIdx = getDiscardedAlleles(variant, alleleList);
        if (!discardedAlleleIdx.isEmpty()) {
            Set<String> duplicatedAlleles = getDuplicatedAlleles(alleleList);
            List<String> otherDiscardedAlleles = new ArrayList<>(discardedAlleleIdx.size());
            for (Integer alleleIdx: discardedAlleleIdx) {
                String allele = alleleList.get(alleleIdx);
                if (!duplicatedAlleles.contains(allele)) {
                    otherDiscardedAlleles.add(allele);
                }
            }
            // If there are duplicated alleles, we need to re-adjust the start/end positions and the alleles
            adjustedStartEndPositions = adjustedVariantStart(variant, studyEntry, referenceAlleles, discardedAlleleIdx);
            start = adjustedStartEndPositions.getLeft();
            end = adjustedStartEndPositions.getRight();
            alleleList = buildAlleles(variant, adjustedStartEndPositions, referenceAlleles);

            logger.warn("Discard alleles from variant " + chromosome + ":" + start + "-" + end + " , "
                    + "Alleles : " + allelesToString(alleleList) + " , "
                    + "discarded allele indexes : " + discardedAlleleIdx + ", "
                    + "duplicated alleles: " + allelesToString(duplicatedAlleles) + ", "
                    + "other discarded alleles: " + allelesToString(otherDiscardedAlleles));
        }
        boolean isNoVariation = type.equals(VariantType.NO_VARIATION);

        // ID
        String idForVcf = getIdForVcf(variant.getId(), variant.getNames());

        // FILTER
        final Set<String> filters = getFilters(fileAttributes);

        // QUAL
        double qual = getQuality(fileAttributes);

        studyEntry.setSamplesPosition(samplePositions);
        BiFunction<String, String, String> getSampleData = studyEntry::getSampleData;

        // INFO
        // Cohorts stats and annotations (consequence types and population frequencies)
        ObjectMap attributes = new ObjectMap();

        addCohortStatsMultiInfoField(studyEntry, attributes);

        // If asked variant annotations are exported
        // (corresponding to the CT and POPFREQ info lines from VCF header)
        if (annotations != null && annotations.size() > 0) {
            addAnnotations(variant, attributes);
        }

        // Add file attributes
        for (Map<String, String> map: fileAttributes) {
            if (map != null && !map.isEmpty()) {
                for (String key: map.keySet()) {
                    if (!key.equals(StudyEntry.FILTER) && !key.equals(StudyEntry.QUAL)) {
                        attributes.put(key, map.get(key));
                        //System.out.println(key + " -> " + map.get(key));
                    }
                }
            }
        }

        // SAMPLES
        List<Genotype> genotypes = getGenotypes(alleleList, studyEntry.getSampleDataKeys(), getSampleData, discardedAlleleIdx);
        return makeVariantContext(chromosome, start, end, idForVcf, alleleList, isNoVariation, filters, qual, attributes, genotypes, discardedAlleleIdx);
    }

    /**
     * Adjust start/end if a reference base is required due to an empty allele. All variants are checked due to SecAlts.
     *
     * @param variant          {@link Variant} object.
     * @param study            Study
     * @param discardedAlleles Set of alleles that are going to be discarded
     * @return Pair<Integer, Integer> The adjusted (or same) start/end position e.g. SV and MNV as SecAlt, INDEL, etc.
     */
    public static Pair<Integer, Integer> adjustedVariantStart(Variant variant, StudyEntry study, Map<Integer, Character> referenceAlleles,
                                                              Set<Integer> discardedAlleles) {
        if (variant.getType().equals(VariantType.NO_VARIATION)) {
            return new ImmutablePair<>(variant.getStart(), variant.getEnd());
        }
        MutablePair<Integer, Integer> pos = adjustedVariantStart(variant.getStart(), variant.getEnd(), variant.getReference(), variant.getAlternate(), referenceAlleles, null);

        int alleleIdx = 2;
        for (AlternateCoordinate alternateCoordinate : study.getSecondaryAlternates()) {
            if (discardedAlleles.contains(alleleIdx)) {
                // Do not adjust start/end based on discarded alleles
            } else {
                int alternateStart = alternateCoordinate.getStart() == null ? variant.getStart() : alternateCoordinate.getStart().intValue();
                int alternateEnd = alternateCoordinate.getEnd() == null ? variant.getEnd() : alternateCoordinate.getEnd().intValue();

                String reference = alternateCoordinate.getReference() == null ? variant.getReference() : alternateCoordinate.getReference();
                String alternate = alternateCoordinate.getAlternate() == null ? variant.getAlternate() : alternateCoordinate.getAlternate();

                adjustedVariantStart(alternateStart, alternateEnd, reference, alternate, referenceAlleles, pos);
            }
            alleleIdx++;
        }
        return pos;
    }

    @Override
    public List<String> buildAlleles(Variant variant, Pair<Integer, Integer> adjustedRange, Map<Integer, Character> referenceAlleles) {
        String reference = variant.getReference();
        String alternate = variant.getAlternate();
        if (variant.getType() == VariantType.TANDEM_DUPLICATION && alternate.equals(VariantBuilder.DUP_ALT)) {
            alternate = VariantBuilder.DUP_TANDEM_ALT;
        }
        if (variant.getType().equals(VariantType.NO_VARIATION)) {
            return Arrays.asList(reference, ".");
        }
        StudyEntry study = getStudy(variant);
        List<AlternateCoordinate> secAlts = study.getSecondaryAlternates();

        List<String> alleles = new ArrayList<>(secAlts.size() + 2);
        int origStart = variant.getStart();
        int origEnd;
        if (variant.getLength() == Variant.UNKNOWN_LENGTH) {
            // Variant::getLengthReference would return UNKNOWN_LENGTH, as the reference could have incomplete reference length
            origEnd = variant.getStart() + variant.getReference().length() - 1;
        } else {
            origEnd = variant.getEnd();
        }
        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, reference, adjustedRange, referenceAlleles));
        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, alternate, adjustedRange, referenceAlleles));
        secAlts.forEach(alt -> {
            int alternateStart = alt.getStart() == null ? variant.getStart() : alt.getStart().intValue();
            int alternateEnd = alt.getEnd() == null ? variant.getEnd() : alt.getEnd().intValue();
            alleles.add(buildAllele(variant.getChromosome(), alternateStart, alternateEnd, alt.getAlternate(), adjustedRange, referenceAlleles));
        });
        return alleles;
    }

    protected Set<Integer> getDiscardedAlleles(Variant variant, List<String> alleleList) {
        if (alleleList.size() <= 2) {
            return Collections.emptySet();
        }

        String mainAlternate = alleleList.get(1);
        Set<String> allelesSet = new HashSet<>();
        allelesSet.add(alleleList.get(0));
        allelesSet.add(mainAlternate);

        boolean symbolicMainAlt = isSymbolic(mainAlternate);
        Set<Integer> discardedAlleles = new HashSet<>();
        for (int i = 2; i < alleleList.size(); i++) {
            String allele = alleleList.get(i);
            if (!allelesSet.add(allele)) {
                // Remove duplicated
                discardedAlleles.add(i);
            } else if (symbolicMainAlt != isSymbolic(allele) && !isNonRef(allele)) {
                // If the main alternate is symbolic, all other alleles must be symbolic
                // If the main alternate is not symbolic, all other alleles must not be symbolic
                // Do not discard <NON_REF> or <*>
                discardedAlleles.add(i);
            } else if (symbolicMainAlt && isSymbolic(allele)) {
                // If the main alternate is symbolic, and the allele is symbolic, check if they have the same coordinates
                AlternateCoordinate alternateCoordinate = variant.getStudies().get(0).getSecondaryAlternates().get(i - 2);
                if ((alternateCoordinate.getStart() != null && !alternateCoordinate.getStart().equals(variant.getStart()))
                        || (alternateCoordinate.getEnd() != null && !alternateCoordinate.getEnd().equals(variant.getEnd()))) {
                    discardedAlleles.add(i);
                }
            }
        }

        return discardedAlleles;
    }

    private void addCohortStatsMultiInfoField(StudyEntry studyEntry, Map<String, Object> attributes) {
        if (studyEntry.getStats() == null || studyEntry.getStats().size() == 0) {
            return;
        }
        for (VariantStats stats : studyEntry.getStats()) {
            String cohortId = stats.getCohortId();

            if (cohortId.equals(StudyEntry.DEFAULT_COHORT)) {
                cohortId = "";
                int an = stats.getAltAlleleCount();
                if (an >= 0) {
                    attributes.put(cohortId + VCFConstants.ALLELE_NUMBER_KEY, String.valueOf(an));
                }
                if (stats.getAltAlleleCount() >= 0) {
                    attributes.put(cohortId + VCFConstants.ALLELE_COUNT_KEY, String.valueOf(stats.getAltAlleleCount()));
                }
            } else {
                cohortId = cohortId + "_";
            }
            attributes.put(cohortId + VCFConstants.ALLELE_FREQUENCY_KEY, DECIMAL_FORMAT_7.format(stats.getAltAlleleFreq()));
        }
    }


    private void addCohortStatsSingleInfoField(StudyEntry studyEntry, Map<String, Object> attributes) {
        if (studyEntry.getStats() == null || studyEntry.getStats().size() == 0) {
            return;
        }

        List<String> statsList = new ArrayList<>();
        for (VariantStats stats : studyEntry.getStats()) {
            String cohortId = stats.getCohortId();
//            if (cohortId.equals(StudyEntry.DEFAULT_COHORT)) {
//                int an = stats.getAltAlleleCount() + stats.getRefAlleleCount();
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

    private void addAnnotations(Variant variant, Map<String, Object> attributes) {
        if (variant.getAnnotation() == null) {
            return;
        }

        // consequence type
        if (variant.getAnnotation().getConsequenceTypes() != null) {
            List<String> ctList = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < variant.getAnnotation().getConsequenceTypes().size(); i++) {
                ConsequenceType consequenceType = variant.getAnnotation().getConsequenceTypes().get(i);
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
                stringBuilder.append(consequenceType.getSequenceOntologyTerms().stream()
                        .map(SequenceOntologyTerm::getName)
                        .collect(Collectors.joining(",")));
                stringBuilder.append(FIELD_SEPARATOR);

                // protein position
                if (consequenceType.getProteinVariantAnnotation() != null) {
                    stringBuilder.append(consequenceType.getProteinVariantAnnotation().getPosition());
                    stringBuilder.append(FIELD_SEPARATOR);
                    stringBuilder.append(consequenceType.getProteinVariantAnnotation().getReference())
                            .append("/")
                            .append(consequenceType.getProteinVariantAnnotation().getAlternate());
                    stringBuilder.append(FIELD_SEPARATOR);
                    if (consequenceType.getProteinVariantAnnotation().getSubstitutionScores() != null) {
                        List<String> sift = consequenceType.getProteinVariantAnnotation().getSubstitutionScores().stream()
                                .filter(t -> t.getSource().equalsIgnoreCase("sift"))
                                .map(Score::getDescription)
                                .collect(Collectors.toList());
                        if (sift.size() > 0) {
                            stringBuilder.append(sift.get(0));
                        }
                        stringBuilder.append(FIELD_SEPARATOR);

                        List<String> polyphen = consequenceType.getProteinVariantAnnotation().getSubstitutionScores().stream()
                                .filter(t -> t.getSource().equalsIgnoreCase("polyphen"))
                                .map(Score::getDescription)
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
                ctList.add(stringBuilder.toString().replace(' ', '_'));
            }

            // set consequence type attributes
            attributes.put(ANNOTATION_INFO_KEY, String.join(INFO_SEPARATOR, ctList));
        }

        // population frequencies
        List<PopulationFrequency> populationFrequencies = variant.getAnnotation().getPopulationFrequencies();
        if (populationFrequencies != null) {
            List<String> popFreqList = new ArrayList<>();
            for (PopulationFrequency pf: populationFrequencies) {
                popFreqList.add(pf.getStudy() + "_" + pf.getPopulation() + ":" + DECIMAL_FORMAT_7.format(pf.getAltAlleleFreq()));
            }
            // set population frequency attributes
            attributes.put(POPFREQ_INFO_KEY, String.join(FIELD_SEPARATOR, popFreqList));
        }
    }


    @Deprecated
    private Map<String, Object> addAnnotations(Variant variant, List<String> annotations, Map<String, Object> attributes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (variant.getAnnotation() == null) {
            return attributes;
        }
//        for (ConsequenceType consequenceType : variant.getAnnotation().getConsequenceTypes()) {
        for (int i = 0; i < variant.getAnnotation().getConsequenceTypes().size(); i++) {
            ConsequenceType consequenceType = variant.getAnnotation().getConsequenceTypes().get(i);

            for (int j = 0; j < annotations.size(); j++) {
                switch (annotations.get(j)) {
                    case "allele":
                        stringBuilder.append(variant.getAlternate());
                        break;
                    case "consequenceType":
                        stringBuilder.append(consequenceType.getSequenceOntologyTerms().stream()
                                .map(SequenceOntologyTerm::getName).collect(Collectors.joining(",")));
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
                        if (variant.getAnnotation().getConservation() != null) {
                            List<Double> phastCons = variant.getAnnotation().getConservation().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("phastCons"))
                                    .map(Score::getScore)
                                    .collect(Collectors.toList());
                            if (phastCons.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(phastCons.get(0)));
                            }
                        }
                        break;
                    case "phylop":
                        if (variant.getAnnotation().getConservation() != null) {
                            List<Double> phylop = variant.getAnnotation().getConservation().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("phylop"))
                                    .map(Score::getScore)
                                    .collect(Collectors.toList());
                            if (phylop.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(phylop.get(0)));
                            }
                        }
                        break;
                    case "populationFrequency":
                        List<PopulationFrequency> populationFrequencies = variant.getAnnotation().getPopulationFrequencies();
                        if (populationFrequencies != null) {
                            stringBuilder.append(populationFrequencies.stream()
                                    .map(t -> t.getPopulation() + ":" + t.getAltAlleleFreq())
                                    .collect(Collectors.joining(",")));
                        }
                        break;
                    case "cDnaPosition":
                        stringBuilder.append(consequenceType.getCdnaPosition());
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
                                && consequenceType.getProteinVariantAnnotation().getSubstitutionScores() != null) {
                            List<Double> sift = consequenceType.getProteinVariantAnnotation().getSubstitutionScores().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("sift"))
                                    .map(Score::getScore)
                                    .collect(Collectors.toList());
                            if (sift.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(sift.get(0)));
                            }
                        }
                        break;
                    case "polyphen":
                        if (consequenceType.getProteinVariantAnnotation() != null
                                && consequenceType.getProteinVariantAnnotation().getSubstitutionScores() != null) {
                            List<Double> polyphen = consequenceType.getProteinVariantAnnotation().getSubstitutionScores().stream()
                                    .filter(t -> t.getSource().equalsIgnoreCase("polyphen"))
                                    .map(Score::getScore)
                                    .collect(Collectors.toList());
                            if (polyphen.size() > 0) {
                                stringBuilder.append(DECIMAL_FORMAT_3.format(polyphen.get(0)));
                            }
                        }
                        break;
                    case "drugInteraction":
                        stringBuilder.append(variant.getAnnotation().getGeneDrugInteraction().stream()
                                .map(GeneDrugInteraction::getDrugName).collect(Collectors.joining(",")));
                        break;
                    default:
                        logger.error("Unknown annotation: " + annotations.get(j));
                        break;
                }
                if (j < annotations.size() - 1) {
                    stringBuilder.append(FIELD_SEPARATOR);
                }
            }
            if (i < variant.getAnnotation().getConsequenceTypes().size() - 1) {
                stringBuilder.append("&");
            }
        }

        attributes.put(ANNOTATION_INFO_KEY, stringBuilder.toString());
//        infoAnnotations.put("CSQ", stringBuilder.toString().replaceAll("&|$", ""));
        return attributes;
    }

    @Override
    protected StudyEntry getStudy(Variant variant) {
        return variant.getStudy(this.studyNameMap.get(this.studyIdString));
    }

    @Override
    protected Iterator<String> getStudiesId(Variant variant) {
        return variant.getStudies().stream().map(StudyEntry::getStudyId).iterator();
    }

}

