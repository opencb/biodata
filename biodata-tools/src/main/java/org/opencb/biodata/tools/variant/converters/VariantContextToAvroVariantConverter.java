package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.*;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.formats.variant.vcf4.VcfUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.datastore.core.ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.opencb.biodata.formats.variant.vcf4.VcfUtils.*;

/**
 * Created by jtarraga on 07/02/17.
 */
public class VariantContextToAvroVariantConverter extends VariantContextConverter<Variant> {

    private final Logger logger = LoggerFactory.getLogger(VariantContextToAvroVariantConverter.class);

    private int studyId;
//    private String studyIdString;
//    private List<String> sampleNames;
//    private List<String> sampleFormats;
//    private List<String> annotations;
//
//    private Map<String, String> studyNameMap;
//    private Map<String, Integer> samplePositions;

    @Deprecated
    public VariantContextToAvroVariantConverter(int studyId, List<String> sampleNames, List<String> annotations) {
        super(Integer.toString(studyId), sampleNames, null, annotations);
        this.studyId = studyId;
    }

    public VariantContextToAvroVariantConverter(String study, List<String> sampleNames, List<String> annotations) {
        this(study, sampleNames, VcfUtils.DEFAULT_SAMPLE_FORMAT, annotations);
    }

    public VariantContextToAvroVariantConverter(String study, List<String> sampleNames, List<String> sampleFormats,
                                                List<String> annotations) {
        super(study, sampleNames, sampleFormats, annotations);
    }

    @Override
    public Variant to(VariantContext obj) {
        return null;
    }

    @Override
    public VariantContext from(Variant variant) {

        if (this.studyNameMap == null || this.studyNameMap.size() == 0) {
            variant.getStudies().forEach(studyEntry -> {
                String s = studyEntry.getStudyId();

                this.studyNameMap.put(s, s);
                if (s.contains("@")) {
                    this.studyNameMap.put(s.split("@")[1], s);
                }
                if (s.contains(":")) {
                    this.studyNameMap.put(s.split(":")[1], s);
                }
            });
        }


        final String noCallAllele = String.valueOf(VCFConstants.NO_CALL_ALLELE);
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder();
        VariantType type = variant.getType();
        Pair<Integer, Integer> adjustedStartEndPositions = adjustedVariantStart(variant);
        List<String> alleleList = buildAlleles(variant, adjustedStartEndPositions);
        Set<Integer> nocallAlleles = IntStream.range(0,  alleleList.size()).boxed()
                .filter(i -> {
                    return noCallAllele.equals(alleleList.get(i));
                })
                .collect(Collectors.toSet());


        StudyEntry studyEntry = variant.getStudy(this.studyNameMap.get(this.studyIdString));

        String filter = "PASS";
        String sourceFilter = studyEntry.getAttribute("FILTER");
        if (sourceFilter != null && !filter.equals(sourceFilter)) {
            filter = ".";   // write PASS iff all sources agree that the filter is "PASS" or assumed if not present, otherwise write "."
        }

        String refAllele = alleleList.get(0);
        List<Genotype> genotypes = new ArrayList<>();
        if (this.sampleNames != null && this.sampleFormats != null) {

            if (samplePositions == null || samplePositions.size() == 0) {
                samplePositions = new HashMap<>(sampleNames.size());
                for (int i = 0; i < sampleNames.size(); i++) {
                    samplePositions.put(sampleNames.get(i), i);
                }
            }
            studyEntry.setSamplesPosition(samplePositions);

            for (String sampleName : this.sampleNames) {
                GenotypeBuilder genotypeBuilder = new GenotypeBuilder().name(sampleName);
                for (String id : this.sampleFormats) {
                    String value = studyEntry.getSampleData(sampleName, id);
                    switch (id) {
                        case "GT":
                            if (value == null) {
                                value = noCallAllele;
                            }
                            org.opencb.biodata.models.feature.Genotype genotype =
                                    new org.opencb.biodata.models.feature.Genotype(value, refAllele, alleleList.subList(1, alleleList.size()));
                            List<Allele> alleles = new ArrayList<>();
                            for (int gtIdx : genotype.getAllelesIdx()) {
                                if (gtIdx < alleleList.size() && gtIdx >= 0 && !nocallAlleles.contains(gtIdx)) { // .. AND NOT a nocall allele
                                    alleles.add(Allele.create(alleleList.get(gtIdx), gtIdx == 0)); // allele is ref. if the alleleIndex is 0
                                } else {
                                    alleles.add(Allele.create(noCallAllele, false)); // genotype of a secondary alternate, or an actual missing
                                }
                            }
                            genotypeBuilder.alleles(alleles).phased(genotype.isPhased());
                            break;
                        case "AD":
                            if (StringUtils.isNotEmpty(value)) {
                                String[] split = value.split(",");
                                genotypeBuilder.AD(new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])});
                            } else {
                                genotypeBuilder.noAD();
                            }
                            break;
                        case "DP":
                            if (StringUtils.isNotEmpty(value)) {
                                genotypeBuilder.DP(Integer.parseInt(value));
                            } else {
                                genotypeBuilder.noDP();
                            }
                            break;
                        case "GQ":
                            if (StringUtils.isNotEmpty(value)) {
                                genotypeBuilder.GQ(Integer.parseInt(value));
                            } else {
                                genotypeBuilder.noGQ();
                            }
                            break;
                        case "PL":
                            if (StringUtils.isNotEmpty(value)) {
                                String[] split = value.split(",");
                                genotypeBuilder.PL(new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])});
                            } else {
                                genotypeBuilder.noPL();
                            }
                            break;
                        default:
                            genotypeBuilder.attribute(id, value);
                            break;
                    }
                }

                genotypes.add(genotypeBuilder.make());

//            String gtStr = studyEntry.getSampleData(sampleName, "GT");
//            String genotypeFilter = studyEntry.getSampleData(sampleName, "FT");
//
//            if (Objects.isNull(gtStr)) {
//                gtStr = noCallAllele;
//                genotypeFilter = noCallAllele;
//            }
//
//            List<String> gtSplit = new ArrayList<>(Arrays.asList(gtStr.split(",")));
//            List<String> ftSplit = new ArrayList<>(Arrays.asList(
//                    (StringUtils.isBlank(genotypeFilter) ? "" : genotypeFilter).split(",")));
//            while (gtSplit.size() > 1) {
//                int idx = gtSplit.indexOf(noCallAllele);
//                if (idx < 0) {
//                    idx = gtSplit.indexOf("0/0");
//                }
//                if (idx < 0) {
//                    break;
//                }
//                gtSplit.remove(idx);
//                ftSplit.remove(idx);
//            }
//            String gt = gtSplit.get(0);
//            String ft = ftSplit.get(0);
//
//            org.opencb.biodata.models.feature.Genotype genotype =
//                    new org.opencb.biodata.models.feature.Genotype(gt, refAllele, alleleList.subList(1, alleleList.size()));
//            List<Allele> alleles = new ArrayList<>();
//            for (int gtIdx : genotype.getAllelesIdx()) {
//                if (gtIdx < alleleList.size() && gtIdx >= 0 && !nocallAlleles.contains(gtIdx)) { // .. AND NOT a nocall allele
//                    alleles.add(Allele.create(alleleList.get(gtIdx), gtIdx == 0)); // allele is ref. if the alleleIndex is 0
//                } else {
//                    alleles.add(Allele.create(noCallAllele, false)); // genotype of a secondary alternate, or an actual missing
//                }
//            }
//
//            if (StringUtils.isBlank(ft)) {
//                genotypeFilter = null;
//            } else if (StringUtils.equals("PASS", ft)) {
//                genotypeFilter = "1";
//            } else {
//                genotypeFilter = "0";
//            }
////            GenotypeBuilder genotypeBuilder = new GenotypeBuilder()
////                    .name(this.sampleNameMapping.get(sampleName));
//            GenotypeBuilder genotypeBuilder = new GenotypeBuilder().name(sampleName);
//            if (studyEntry.getFormatPositions().containsKey("GT")) {
//                genotypeBuilder.alleles(alleles).phased(genotype.isPhased());
//            }
//            if (genotypeFilter != null) {
//                genotypeBuilder.attribute("PF", genotypeFilter);
//            }
//
//            System.out.println("studyEntry.getFormat() = " + studyEntry.getFormat());
//            for (String id : studyEntry.getFormat()) {
//                if (id.equals("GT") || id.equals("FT")) {
//                    continue;
//                }
//                String value = studyEntry.getSampleData(sampleName, id);
//                genotypeBuilder.attribute(id, value);
//            }
//
//            genotypes.add(genotypeBuilder.make());
            }


        }


        variantContextBuilder
                .chr(variant.getChromosome())
                .start(adjustedStartEndPositions.getLeft())
                .stop(adjustedStartEndPositions.getLeft() + refAllele.length() - 1) //TODO mh719: check what happens for Insertions
                .filter(filter); // TODO jmmut: join attributes from different source entries? what to do on a collision?

        if (type.equals(VariantType.NO_VARIATION) && alleleList.get(1).isEmpty()) {
            variantContextBuilder.alleles(refAllele);
        } else {
            variantContextBuilder.alleles(alleleList.stream().filter(a -> !a.equals(noCallAllele)).collect(Collectors.toList()));
        }

        if (genotypes.isEmpty()) {
            variantContextBuilder.noGenotypes();
        } else {
            variantContextBuilder.genotypes(genotypes);
        }

        //Attributes for INFO column (cohorts stats and annotations (consequence types and population frequencies)
        ObjectMap attributes = new ObjectMap();

        addCohortStats(studyEntry, attributes);

        // if asked variant annotations are exported
        // (corresponding to the CT and POPFREQ info lines from VCF header)
        if (annotations != null && annotations.size() > 0) {
            addAnnotations(variant, attributes);
        }

        variantContextBuilder.attributes(attributes);


        if (StringUtils.isNotEmpty(variant.getId()) && !variant.toString().equals(variant.getId())) {
            StringBuilder ids = new StringBuilder();
            ids.append(variant.getId());
            if (variant.getNames() != null) {
                for (String name : variant.getNames()) {
                    ids.append(VCFConstants.ID_FIELD_SEPARATOR).append(name);
                }
            }
            variantContextBuilder.id(StringUtils.join(ids, ","));
        } else {
            variantContextBuilder.id(VCFConstants.EMPTY_ID_FIELD);
        }

        return variantContextBuilder.make();
    }

    /**
     * Adjust start/end if a reference base is required due to an empty allele. All variants are checked due to SecAlts.
     * @param variant {@link Variant} object.
     * @return Pair<Integer, Integer> The adjusted (or same) start/end position e.g. SV and MNV as SecAlt, INDEL, etc.
     */
    protected Pair<Integer, Integer> adjustedVariantStart(Variant variant) {
        int start = variant.getStart();
        int end = variant.getEnd();
        if (StringUtils.isBlank(variant.getReference()) || StringUtils.isBlank(variant.getAlternate())) {
            start = start - 1;
        }
//        for (AlternateCoordinate alternateCoordinate : variant.getStudy(this.studyIdString).getSecondaryAlternates()) {
        for (AlternateCoordinate alternateCoordinate : variant.getStudy(this.studyNameMap.get(this.studyIdString)).getSecondaryAlternates()) {
            start = Math.min(start, alternateCoordinate.getStart());
            end = Math.max(end, alternateCoordinate.getEnd());
            if (StringUtils.isBlank(alternateCoordinate.getAlternate()) || StringUtils.isBlank(alternateCoordinate.getReference())) {
                start = Math.min(start, alternateCoordinate.getStart() - 1);
            }
        }
        return new ImmutablePair<>(start, end);
    }

    public List<String> buildAlleles(Variant variant, Pair<Integer, Integer> adjustedRange) {
        String reference = variant.getReference();
        String alternate = variant.getAlternate();
//        List<AlternateCoordinate> secAlts = variant.getStudy(this.studyIdString).getSecondaryAlternates();
        List<AlternateCoordinate> secAlts = variant.getStudy(this.studyNameMap.get(this.studyIdString)).getSecondaryAlternates();
        List<String> alleles = new ArrayList<>(secAlts.size() + 2);
        int origStart = variant.getStart();
        int origEnd = variant.getEnd();
        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, reference, adjustedRange));
        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, alternate, adjustedRange));
        secAlts.forEach(alt -> {
            alleles.add(buildAllele(variant.getChromosome(), alt.getStart(), alt.getEnd(), alt.getAlternate(), adjustedRange));
        });
        return alleles;
    }

/*
    // this function was moved to the parent class: VariantContextConverter
    public String buildAllele(String chromosome, Integer start, Integer end, String allele, Pair<Integer, Integer> adjustedRange) {
        if (start.equals(adjustedRange.getLeft()) && end.equals(adjustedRange.getRight())) {
            return allele; // same start / end
        }
        if (StringUtils.startsWith(allele, "*")) {
            return allele; // no need
        }
        return getReferenceBase(chromosome, adjustedRange.getLeft(), start) + allele
                + getReferenceBase(chromosome, end, adjustedRange.getRight());
    }
*/

    /*
    // this function was moved to the parent class: VariantContextConverter
    private String getReferenceBase(String chromosome, Integer from, Integer to) {
        int length = to - from;
        if (length < 0) {
            throw new IllegalStateException(
                    "Sequence length is negative: chromosome " + chromosome + " from " + from + " to " + to);
        }
        return StringUtils.repeat('N', length); // current return default base TODO load reference sequence
    }
*/

    @Deprecated
    private void addCohortStatsOLD(StudyEntry studyEntry, Map<String, Object> attributes) {
        if (studyEntry.getStats() == null || studyEntry.getStats().size() == 0) {
            return;
        }
        for (Map.Entry<String, VariantStats> entry : studyEntry.getStats().entrySet()) {
            String cohortName = entry.getKey();
            VariantStats stats = entry.getValue();

            if (cohortName.equals(StudyEntry.DEFAULT_COHORT)) {
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


    private void addCohortStats(StudyEntry studyEntry, Map<String, Object> attributes) {
        if (studyEntry.getStats() == null || studyEntry.getStats().size() == 0) {
            return;
        }

        List<String> statsList = new ArrayList<>();
        for (Map.Entry<String, VariantStats> entry : studyEntry.getStats().entrySet()) {
            String cohortName = entry.getKey();
            VariantStats stats = entry.getValue();

//            if (cohortName.equals(StudyEntry.DEFAULT_COHORT)) {
//                int an = stats.getAltAlleleCount() + stats.getRefAlleleCount();
//                if (an >= 0) {
//                    attributes.put(cohortName + VCFConstants.ALLELE_NUMBER_KEY, String.valueOf(an));
//                }
//                if (stats.getAltAlleleCount() >= 0) {
//                    attributes.put(cohortName + VCFConstants.ALLELE_COUNT_KEY, String.valueOf(stats.getAltAlleleCount()));
//                }
//            }
            statsList.add(cohortName + ":" + DECIMAL_FORMAT_7.format(stats.getAltAlleleFreq()));
        }
        // set cohort stats attributes
        attributes.put(STATS_INFO_KEY, String.join(FIELD_SEPARATOR, statsList));
    }

    private void addAnnotations(Variant variant, Map<String, Object> attributes) {
        // consequence type
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
            ctList.add(stringBuilder.toString());
        }

        // set consequence type attributes
        attributes.put(ANNOTATION_INFO_KEY, String.join(INFO_SEPARATOR, ctList));

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
                    case "clinvar":
                        if (variant.getAnnotation().getVariantTraitAssociation() != null
                                && variant.getAnnotation().getVariantTraitAssociation().getClinvar() != null) {
                            stringBuilder.append(variant.getAnnotation().getVariantTraitAssociation().getClinvar().stream()
                                    .map(ClinVar::getTraits).flatMap(Collection::stream)
                                    .collect(Collectors.joining(",")));
                        }
                        break;
                    case "cosmic":
                        if (variant.getAnnotation().getVariantTraitAssociation() != null
                                && variant.getAnnotation().getVariantTraitAssociation().getCosmic() != null) {
                            stringBuilder.append(variant.getAnnotation().getVariantTraitAssociation().getCosmic().stream()
                                    .map(Cosmic::getPrimarySite)
                                    .collect(Collectors.joining(",")));
                        }
                        break;
                    case "gwas":
                        if (variant.getAnnotation().getVariantTraitAssociation() != null
                                && variant.getAnnotation().getVariantTraitAssociation().getGwas() != null) {
                            stringBuilder.append(variant.getAnnotation().getVariantTraitAssociation().getGwas().stream()
                                    .map(Gwas::getTraits).flatMap(Collection::stream)
                                    .collect(Collectors.joining(",")));
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


    /**
     * Assumes that ori is in the form "POS:REF:ALT_0(,ALT_N)*:ALT_IDX".
     * ALT_N is the n-th allele if this is the n-th variant resultant of a multiallelic vcf row
     *
     * @param ori
     * @return
     */
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

    /**
     * Assumes that ori is in the form "POS:REF:ALT_0(,ALT_N)*:ALT_IDX".
     *
     * @param ori
     * @return
     */
    private static Integer getOriginalPosition(String[] ori) {

        if (ori != null && ori.length == 4) {
            return Integer.parseInt(ori[0]);
        }

        return null;
    }

    private static String[] getOri(StudyEntry studyEntry) {

        List<FileEntry> files = studyEntry.getFiles();
        if (!files.isEmpty()) {
            String call = files.get(0).getCall();
            if (call != null && !call.isEmpty()) {
                return call.split(":");
            }
        }
        return null;
    }
}

