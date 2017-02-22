package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.*;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

/**
 * Created by jtarraga on 07/02/17.
 */
public class VariantContextToAvroVariantConverter extends VariantConverter<Variant> {

    private static final DecimalFormat DECIMAL_FORMAT_7 = new DecimalFormat("#.#######");
    private static final DecimalFormat DECIMAL_FORMAT_3 = new DecimalFormat("#.###");
    private final Logger logger = LoggerFactory.getLogger(VariantContextToAvroVariantConverter.class);

    int studyId;
    String studyIdString;
    List<String> sampleNames;
    List<String> annotations;

    public VariantContextToAvroVariantConverter(int studyId, List<String> sampleNames, List<String> annotations) {
        this.studyId = studyId;
        this.studyIdString = Integer.toString(studyId);
        this.sampleNames = sampleNames;
        this.annotations = annotations;
    }

    @Override
    public Variant to(VariantContext obj) {
        return null;
    }

    @Override
    public VariantContext from(Variant variant) {
        final String noCallAllele = String.valueOf(VCFConstants.NO_CALL_ALLELE);
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder();
        VariantType type = variant.getType();
        Pair<Integer, Integer> adjustedRange = adjustedVariantStart(variant);
        List<String> allelesArray = buildAlleles(variant, adjustedRange);
        Set<Integer> nocallAlleles = IntStream.range(0,  allelesArray.size()).boxed()
                .filter(i -> {
                    return noCallAllele.equals(allelesArray.get(i));
                })
                .collect(Collectors.toSet());
        String filter = "PASS";
//        String prk = "PR";
//        String crk = "CR";
//        String oprk = "OPR";

        //Attributes for INFO column
        ObjectMap attributes = new ObjectMap();
        ArrayList<Genotype> genotypes = new ArrayList<>();
        StudyEntry studyEntry = variant.getStudy(this.studyIdString);

//        Integer originalPosition = null;
//        List<String> originalAlleles = null;
        // TODO work out properly how to deal with multi allelic sites.
//        String[] ori = getOri(studyEntry);
//        Integer auxOriginalPosition = getOriginalPosition(ori);
//        if (originalPosition != null && auxOriginalPosition != null && !originalPosition.equals(auxOriginalPosition)) {
//            throw new IllegalStateException("Two or more VariantSourceEntries have different origin. Unable to merge");
//        }
//        originalPosition = auxOriginalPosition;
//        originalAlleles = getOriginalAlleles(ori);
//        if (originalAlleles == null) {
//            originalAlleles = allelesArray;
//        }
//
//        //Only print those variants in which the alternate is the first alternate from the multiallelic alternatives
//        if (originalAlleles.size() > 2 && !"0".equals(getOriginalAlleleIndex(ori))) {
//            logger.debug("Skip multi allelic variant! " + variant);
//            return null;
//        }

        String sourceFilter = studyEntry.getAttribute("FILTER");
        if (sourceFilter != null && !filter.equals(sourceFilter)) {
            filter = ".";   // write PASS iff all sources agree that the filter is "PASS" or assumed if not present, otherwise write "."
        }

//        attributes.putIfNotNull(prk, DECIMAL_FORMAT_7.format(Double.valueOf(studyEntry.getAttributes().get("PR"))));
//        attributes.putIfNotNull(crk, DECIMAL_FORMAT_7.format(Double.valueOf(studyEntry.getAttributes().get("CR"))));
//        attributes.putIfNotNull(oprk, DECIMAL_FORMAT_7.format(Double.valueOf(studyEntry.getAttributes().get("OPR"))));

        String refAllele = allelesArray.get(0);
        for (String sampleName : this.sampleNames) {
            String gtStr = studyEntry.getSampleData(sampleName, "GT");
            String genotypeFilter = studyEntry.getSampleData(sampleName, "FT");

            if (Objects.isNull(gtStr)) {
                gtStr = noCallAllele;
                genotypeFilter = noCallAllele;
            }

            List<String> gtSplit = new ArrayList<>(Arrays.asList(gtStr.split(",")));
            List<String> ftSplit = new ArrayList<>(Arrays.asList(
                    (StringUtils.isBlank(genotypeFilter) ? "" : genotypeFilter).split(",")));
            while (gtSplit.size() > 1) {
                int idx = gtSplit.indexOf(noCallAllele);
                if (idx < 0) {
                    idx = gtSplit.indexOf("0/0");
                }
                if (idx < 0) {
                    break;
                }
                gtSplit.remove(idx);
                ftSplit.remove(idx);
            }
            String gt = gtSplit.get(0);
            String ft = ftSplit.get(0);

            org.opencb.biodata.models.feature.Genotype genotype =
                    new org.opencb.biodata.models.feature.Genotype(gt, refAllele, allelesArray.subList(1, allelesArray.size()));
            List<Allele> alleles = new ArrayList<>();
            for (int gtIdx : genotype.getAllelesIdx()) {
                if (gtIdx < allelesArray.size() && gtIdx >= 0 && !nocallAlleles.contains(gtIdx)) { // .. AND NOT a nocall allele
                    alleles.add(Allele.create(allelesArray.get(gtIdx), gtIdx == 0)); // allele is ref. if the alleleIndex is 0
                } else {
                    alleles.add(Allele.create(noCallAllele, false)); // genotype of a secondary alternate, or an actual missing
                }
            }

            if (StringUtils.isBlank(ft)) {
                genotypeFilter = null;
            } else if (StringUtils.equals("PASS", ft)) {
                genotypeFilter = "1";
            } else {
                genotypeFilter = "0";
            }
//            GenotypeBuilder builder = new GenotypeBuilder()
//                    .name(this.sampleNameMapping.get(sampleName));
            GenotypeBuilder builder = new GenotypeBuilder()
                    .name(sampleName);
            if (studyEntry.getFormatPositions().containsKey("GT")) {
                builder.alleles(alleles)
                        .phased(genotype.isPhased());
            }
            if (genotypeFilter != null) {
                builder.attribute("PF", genotypeFilter);
            }
            for (String id : studyEntry.getFormat()) {
                if (id.equals("GT") || id.equals("FT")) {
                    continue;
                }
                String value = studyEntry.getSampleData(sampleName, id);
                builder.attribute(id, value);
            }

            genotypes.add(builder.make());
        }

        addStats(studyEntry, attributes);

        variantContextBuilder.start(adjustedRange.getLeft())
                .stop(adjustedRange.getLeft() + refAllele.length() - 1) //TODO mh719: check what happens for Insertions
                .chr(variant.getChromosome())
                .filter(filter); // TODO jmmut: join attributes from different source entries? what to do on a collision?

        if (genotypes.isEmpty()) {
            variantContextBuilder.noGenotypes();
        } else {
            variantContextBuilder.genotypes(genotypes);
        }

        if (type.equals(VariantType.NO_VARIATION) && allelesArray.get(1).isEmpty()) {
            variantContextBuilder.alleles(refAllele);
        } else {
            variantContextBuilder.alleles(allelesArray.stream().filter(a -> !a.equals(noCallAllele)).collect(Collectors.toList()));
        }

        // if asked variant annotations are exported
        if (annotations != null) {
            addAnnotations(variant, annotations, attributes);
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
            variantContextBuilder.id(ids.toString());
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
        Integer start = variant.getStart();
        Integer end = variant.getEnd();
        if (StringUtils.isBlank(variant.getReference()) || StringUtils.isBlank(variant.getAlternate())) {
            start = start - 1;
        }
        for (AlternateCoordinate alternateCoordinate : variant.getStudy(this.studyIdString).getSecondaryAlternates()) {
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
        List<AlternateCoordinate> secAlts = variant.getStudy(this.studyIdString).getSecondaryAlternates();
        List<String> alleles = new ArrayList<>(secAlts.size() + 2);
        Integer origStart = variant.getStart();
        Integer origEnd = variant.getEnd();
        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, reference, adjustedRange));
        alleles.add(buildAllele(variant.getChromosome(), origStart, origEnd, alternate, adjustedRange));
        secAlts.forEach(alt -> {
            alleles.add(buildAllele(variant.getChromosome(), alt.getStart(), alt.getEnd(), alt.getAlternate(), adjustedRange));
        });
        return alleles;
    }

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

    /**
     * Get bases from reference sequence.
     * @param chromosome Chromosome.
     * @param from Start ( inclusive) position.
     * @param to End (exclusive) position.
     * @return String Reference sequence of length to - from.
     */
    private String getReferenceBase(String chromosome, Integer from, Integer to) {
        int length = to - from;
        if (length < 0) {
            throw new IllegalStateException(
                    "Sequence length is negative: chromosome " + chromosome + " from " + from + " to " + to);
        }
        return StringUtils.repeat('N', length); // current return default base TODO load reference sequence
    }

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
                    stringBuilder.append("|");
                }
            }
            if (i < variant.getAnnotation().getConsequenceTypes().size() - 1) {
                stringBuilder.append("&");
            }
        }

        attributes.put("CSQ", stringBuilder.toString());
//        infoAnnotations.put("CSQ", stringBuilder.toString().replaceAll("&|$", ""));
        return attributes;
    }

    private void addStats(StudyEntry studyEntry, Map<String, Object> attributes) {
        if (studyEntry.getStats() == null) {
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

