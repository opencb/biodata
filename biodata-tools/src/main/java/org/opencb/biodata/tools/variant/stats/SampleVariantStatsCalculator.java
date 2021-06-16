package org.opencb.biodata.tools.variant.stats;

import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.clinical.pedigree.Member;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.variant.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.metadata.*;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.pedigree.MendelianError;
import org.opencb.biodata.tools.variant.metadata.VariantMetadataManager;
import org.opencb.commons.run.Task;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

//import javafx.util.Pair;

public class SampleVariantStatsCalculator implements Task<Variant, Variant> {
    protected List<SampleVariantStats> statsList;

    protected int[] ti;
    protected int[] tv;
    protected int[] qualCount;
    protected double[] qualSum;
    protected double[] qualSumSq;

    protected List<Pedigree> pedigrees;
    protected Map<String, Member> validChildren;
    protected Map<String, String> sampleFileMap;
    protected Map<Integer, String> samplePosFileMap;
    protected List<String> samples;
    protected LinkedHashMap<String, Integer> samplesPos;

    /**
     * Create a sample stats calculator.
     *
     * @param studyMetadata VariantStudyMetadata with all the study metadata.
     */
    public SampleVariantStatsCalculator(VariantStudyMetadata studyMetadata) {
        VariantMetadata variantMetadata = new VariantMetadata();
        variantMetadata.setStudies(Collections.singletonList(studyMetadata));
        VariantMetadataManager mm = new VariantMetadataManager().setVariantMetadata(variantMetadata);
        pedigrees = mm.getPedigree(studyMetadata.getId());

        samples = mm.getSamples(studyMetadata.getId()).stream().map(Sample::getId).collect(Collectors.toList());
        sampleFileMap = new HashMap<>(samples.size());

        for (VariantFileMetadata file : studyMetadata.getFiles()) {
            for (String sampleId : file.getSampleIds()) {
                sampleFileMap.put(sampleId, file.getId());
            }
        }
    }

    /**
     * Assume that there will be only one file per study.
     *
     * @param pedigree      Pedigree with the family information
     * @param samples       Ordered list of samples
     */
    public SampleVariantStatsCalculator(Pedigree pedigree, List<String> samples) {
        this(pedigree, samples, null);
    }

    /**
     * Create a sample stats calculator for a multi-file study.
     *
     * @param pedigree      Pedigree with the family information
     * @param samples       Ordered list of samples
     * @param sampleFileMap Map with the files related to each sample
     */
    public SampleVariantStatsCalculator(Pedigree pedigree, List<String> samples, Map<String, String> sampleFileMap) {
        this.pedigrees = pedigree == null ? Collections.emptyList() : Collections.singletonList(pedigree);
        this.sampleFileMap = sampleFileMap;
        validChildren = Collections.emptyMap();
        this.samples = samples;

    }

    public List<SampleVariantStats> getSampleVariantStats() {
        return statsList;
    }

    @Override
    public synchronized List<Variant> apply(List<Variant> batch) {
        for (Variant variant : batch) {
            update(variant);
        }
        return batch;
    }

    @Override
    public void pre() {
        statsList = null;

        if (pedigrees != null) {
            validChildren = new HashMap<>();
            for (Pedigree pedigree : pedigrees) {

                for (Member member: pedigree.getMembers()) {
                    if (member.getFather() != null || member.getMother() != null) {
                        validChildren.put(member.getId(), member);
                    }
                }
            }
        }
        if (samples != null) {
            init(samples);
        }
    }

    public List<SampleVariantStats> compute(List<Variant> variants) {
        return compute(variants.iterator());
    }

    public List<SampleVariantStats> compute(Iterator<Variant> variantIterator) {
        pre();

        // Main loop
        Variant variant;
        while (variantIterator.hasNext()) {
            variant = variantIterator.next();

            update(variant);
        }

        // Post-processing
        post();

        return statsList;
    }

    public void update(Variant variant) {
        StudyEntry studyEntry = variant.getStudies().get(0);
        List<SampleEntry> samples = studyEntry.getSamples();
        Integer dpPos = studyEntry.getSampleDataKeyPosition(VCFConstants.DEPTH_KEY);
        IntFunction<String> getDp;
        if (dpPos != null) {
            getDp = samplePos -> samples.get(samplePos).getData().get(0);
        } else {
            getDp = samplePos -> getFileAttributes(studyEntry, samplePos).get(VCFConstants.DEPTH_KEY);
        }
        update(variant, variant.getAnnotation(),
                samplePos -> samples.get(samplePos).getData().get(0),
                getDp,
                samplePos -> getFileAttributes(studyEntry, samplePos).get(StudyEntry.QUAL),
                samplePos -> getFileAttributes(studyEntry, samplePos).get(StudyEntry.FILTER),
                studyEntry.getSamplesPosition());
    }

    /**
     * Update the stats given only the required elements.
     * @param variant    Minimal version of the variant. Only chr,pos,ref,alt
     * @param annotation Full annotation
     * @param gts        List of genotypes
     * @param dps        List of DP values
     * @param quals      List of quals
     * @param filters    List of filters
     */
    public void update(Variant variant, VariantAnnotation annotation, List<String> gts, List<String> dps, List<String> quals, List<String> filters) {
        update(variant, annotation, gts::get, dps::get, quals::get, filters::get, samplesPos);
    }

    /**
     * Update the stats given only the required elements.
     * @param variant    Minimal version of the variant. Only chr,pos,ref,alt
     * @param gts        List of genotypes
     * @param dps        List of DP values
     * @param quals      List of quals
     * @param filters    List of filters
     * @param cts        Set with consequence types in this variant
     * @param biotypes   Set with biotypes in this variant
     * @param clinicalSignificance Set with clinicalSignificances in this variant
     */
    public void update(Variant variant, List<String> gts, List<String> dps, List<String> quals, List<String> filters, Set<String> cts,
                       Set<String> biotypes, Set<String> clinicalSignificance) {
        update(variant, gts::get,  dps::get, quals::get, filters::get, samplesPos, cts, biotypes, clinicalSignificance);
    }

    private void update(Variant variant,
                        VariantAnnotation annotation,
                        IntFunction<String> gts,
                        IntFunction<String> getDp,
                        IntFunction<String> getQual,
                        IntFunction<String> getFilter,
                        LinkedHashMap<String, Integer> samplesPos) {

        Set<String> biotypes = new HashSet<>();
        Set<String> cts = new HashSet<>();
        Set<String> clinicalSignificance = new HashSet<>();
        if (annotation != null) {
            if (CollectionUtils.isNotEmpty(annotation.getConsequenceTypes())) {
                for (ConsequenceType ct : annotation.getConsequenceTypes()) {
                    if (StringUtils.isNotEmpty(ct.getBiotype())) {
                        biotypes.add(ct.getBiotype());
                    }

                    if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
                        for (SequenceOntologyTerm so : ct.getSequenceOntologyTerms()) {
                            cts.add(so.getName());
                        }
                    }
                }
            }
            if (annotation.getTraitAssociation() != null) {
                for (EvidenceEntry evidenceEntry : annotation.getTraitAssociation()) {
                    if (evidenceEntry.getVariantClassification() != null
                            && evidenceEntry.getVariantClassification().getClinicalSignificance() != null) {
                        clinicalSignificance.add(evidenceEntry.getVariantClassification().getClinicalSignificance().name());
                    }
                }
            }
        }
        update(variant, gts, getDp, getQual, getFilter, samplesPos, cts, biotypes, clinicalSignificance);
    }

    private void update(Variant variant,
                        IntFunction<String> gts,
                        IntFunction<String> getDp,
                        IntFunction<String> getQual,
                        IntFunction<String> getFilter,
                        LinkedHashMap<String, Integer> samplesPos, Set<String> cts, Set<String> biotypes, Set<String> clinicalSignificance) {
        int numSamples = samplesPos.size();
        if (statsList == null) {
            init(new ArrayList<>(samplesPos.keySet()));
        }

        boolean transition = VariantStats.isTransition(variant.getReference(), variant.getAlternate());
        boolean transversion = VariantStats.isTransversion(variant.getReference(), variant.getAlternate());

        for (int samplePos = 0; samplePos < numSamples; samplePos++) {
            String gt = gts.apply(samplePos);
            if (gt != null) {
                String qual = getQual.apply(samplePos);
                String filter = getFilter.apply(samplePos);
                String dp = getDp.apply(samplePos);
                updateSample(variant, transition, transversion, samplePos, gts, gt, dp, qual, filter, biotypes, cts, clinicalSignificance);
            }
        }
    }

    private void updateSample(Variant variant, boolean transition, boolean transversion,
                              int samplePos, IntFunction<String> gts, String gt, String dpStr,
                              String qual, String filter,
                              Set<String> biotypes, Set<String> cts, Set<String> clinicalSignificance) {
        SampleVariantStats stats = statsList.get(samplePos);

//        if (gt.contains(".")) {
//            stats.setMissingPositions(stats.getMissingPositions() + variant.getLengthReference());
//        }

        // Compute mendelian error
        Member child = validChildren.get(stats.getId());
        if (child != null) {
            Genotype childGt = new Genotype(gts.apply(samplePos));
            Genotype fatherGt = getParentGt(gts, child.getFather());
            Genotype motherGt = getParentGt(gts, child.getMother());

            int errorCode = MendelianError.compute(fatherGt, motherGt, childGt, variant.getChromosome());
            if (errorCode > 0) {
                Map<String, Map<String, Integer>> mendelianErrors = stats.getMendelianErrorCount();
                String errorCodeKey = String.valueOf(errorCode);
                Map<String, Integer> map = mendelianErrors.computeIfAbsent(variant.getChromosome(), (key) -> new HashMap<>());
                incCount(map, errorCodeKey);
            }
        }

        // Only increase these counters if this sample has the mutation (i.e. has the main allele in the genotype)
        if (Genotype.hasMainAlternate(gt)) {
            stats.setVariantCount(stats.getVariantCount() + 1);
            incCount(stats.getGenotypeCount(), gt);

            // Chromosome counter
            incCount(stats.getChromosomeCount(), variant.getChromosome());

            // Type counter
            VariantType type = variant.getType();
            if (type == VariantType.SNP) {
                type = VariantType.SNV;
            } else if (type == VariantType.MNP) {
                type = VariantType.MNV;
            }
            incCount(stats.getTypeCount(), type.name());

            // Indel length
            if (variant.getType() == VariantType.INDEL
                    || variant.getType() == VariantType.INSERTION
                    || variant.getType() == VariantType.DELETION) {
                IndelLength indel = stats.getIndelLengthCount();
                Integer length = variant.getLength();
                if (length < 5) {
                    indel.setLt5(indel.getLt5() + 1);
                } else if (length < 10) {
                    indel.setLt10(indel.getLt10() + 1);
                } else if (length < 15) {
                    indel.setLt15(indel.getLt15() + 1);
                } else if (length < 20) {
                    indel.setLt20(indel.getLt20() + 1);
                } else {
                    indel.setGte20(indel.getGte20() + 1);
                }
            }

            if (StringUtils.isNumeric(dpStr)) {
                int dp = Integer.parseInt(dpStr);
                DepthCount dpCount = stats.getDepthCount();
                if (dp < 5) {
                    dpCount.setLt5(dpCount.getLt5() + 1);
                } else if (dp < 10) {
                    dpCount.setLt10(dpCount.getLt10() + 1);
                } else if (dp < 15) {
                    dpCount.setLt15(dpCount.getLt15() + 1);
                } else if (dp < 20) {
                    dpCount.setLt20(dpCount.getLt20() + 1);
                } else {
                    dpCount.setGte20(dpCount.getGte20() + 1);
                }
            } else {
                stats.getDepthCount().setNa(stats.getDepthCount().getNa() + 1);
            }

            // Accumulate transitions and transversions in order to compute ti/tv ratio later
            if (transition) {
                ti[samplePos]++;
            } else if (transversion) {
                tv[samplePos]++;
            }

            if (qual != null && !(".").equals(qual)) {
                float qualValue = Float.parseFloat(qual);
                qualCount[samplePos]++;
                qualSum[samplePos] += qualValue;
                qualSumSq[samplePos] += qualValue * qualValue;
            }
            if (filter == null || filter.isEmpty()) {
                filter = ".";
            }
            for (String subFilter : filter.split(";")) {
                stats.getFilterCount().merge(subFilter, 1, Integer::sum);
            }

            // Biotype counter
            for (String biotype : biotypes) {
                incCount(stats.getBiotypeCount(), biotype);
            }

            // ConsequenceType counter
            for (String ct : cts) {
                incCount(stats.getConsequenceTypeCount(), ct);
            }
            // ConsequenceType counter
            for (String cs : clinicalSignificance) {
                incCount(stats.getClinicalSignificanceCount(), cs);
            }

        }
    }

    private Genotype getParentGt(IntFunction<String> gts, Member parent) {
        if (parent == null) {
            return null;
        } else {
            String gtStr = gts.apply(samplesPos.get(parent.getId()));
            if (gtStr == null) {
                return null;
            } else {
                return new Genotype(gtStr);
            }
        }
    }

    public static SampleVariantStats merge(SampleVariantStats stats, SampleVariantStats otherStats) {
        stats.setVariantCount(stats.getVariantCount() + otherStats.getVariantCount());
//        stats.setNumPass(stats.getNumPass() + otherStats.getNumPass());
//        stats.setMissingPositions(stats.getMissingPositions() + otherStats.getMissingPositions());

        mergeCounts(stats.getFilterCount(), otherStats.getFilterCount());
        mergeCounts(stats.getGenotypeCount(), otherStats.getGenotypeCount());
        mergeCounts(stats.getTypeCount(), otherStats.getTypeCount());
        mergeCounts(stats.getChromosomeCount(), otherStats.getChromosomeCount());
        mergeCounts(stats.getBiotypeCount(), otherStats.getBiotypeCount());
        mergeCounts(stats.getConsequenceTypeCount(), otherStats.getConsequenceTypeCount());
        mergeCounts(stats.getClinicalSignificanceCount(), otherStats.getClinicalSignificanceCount());
        mergeMaps(stats.getMendelianErrorCount(), otherStats.getMendelianErrorCount(), SampleVariantStatsCalculator::mergeCounts);

        IndelLength indelLength = stats.getIndelLengthCount();
        IndelLength otherIndelLength = otherStats.getIndelLengthCount();

        indelLength.setLt5(indelLength.getLt5() + otherIndelLength.getLt5());
        indelLength.setLt10(indelLength.getLt10() + otherIndelLength.getLt10());
        indelLength.setLt15(indelLength.getLt15() + otherIndelLength.getLt15());
        indelLength.setLt20(indelLength.getLt20() + otherIndelLength.getLt20());
        indelLength.setGte20(indelLength.getGte20() + otherIndelLength.getGte20());


        DepthCount depthCount = stats.getDepthCount();
        DepthCount otherDepthCount = otherStats.getDepthCount();

        depthCount.setNa(depthCount.getNa() + otherDepthCount.getNa());
        depthCount.setLt5(depthCount.getLt5() + otherDepthCount.getLt5());
        depthCount.setLt10(depthCount.getLt10() + otherDepthCount.getLt10());
        depthCount.setLt15(depthCount.getLt15() + otherDepthCount.getLt15());
        depthCount.setLt20(depthCount.getLt20() + otherDepthCount.getLt20());
        depthCount.setGte20(depthCount.getGte20() + otherDepthCount.getGte20());

        return stats;
    }

    private static Map<String, Integer> mergeCounts(Map<String, Integer> map, Map<String, Integer> otherMap) {
        otherMap.forEach((key, count) -> map.merge(key, count, Integer::sum));
        return map;
    }

    private static <T> void mergeMaps(Map<String, T> map, Map<String, T> otherMap, BiFunction<? super T, ? super T, ? extends T> merge) {
        otherMap.forEach((key, v) -> map.merge(key, v, merge));
    }

    private Map<String, String> getFileAttributes(StudyEntry studyEntry, int samplePos) {
        FileEntry fileEntry = getFileEntry(studyEntry, samplePos);
        if (fileEntry == null) {
            return Collections.emptyMap();
        } else {
            return fileEntry.getData();
        }
    }

    private FileEntry getFileEntry(StudyEntry studyEntry, int samplePos) {
        if (samplePosFileMap != null) {
            String file = samplePosFileMap.get(samplePos);
            if (file != null) {
                return studyEntry.getFile(file);
            } else {
                return null;
            }
        } else {
            return studyEntry.getFiles().get(0);
        }
    }

    @Override
    public void post() {
        for (int i = 0; i < statsList.size(); i++) {
            SampleVariantStats stats = statsList.get(i);

            // Remove phase
            Map<Genotype, Integer> genotypeCount = stats.getGenotypeCount()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> new Genotype(e.getKey()), Map.Entry::getValue));
            genotypeCount = VariantStatsCalculator.removePhaseFromGenotypeCount(genotypeCount);
            stats.getGenotypeCount().clear();
            genotypeCount.forEach((key, value) -> stats.getGenotypeCount().put(key.toString(), value));
            stats.getGenotypeCount().remove("0/0"); // RemovePhase method may add genotype 0/0, even if it's not present

            // Compute number of variants from genotype counters
            int numVariants = stats.getVariantCount();
            int numHet = 0;

            for (Map.Entry<String, Integer> entry : stats.getGenotypeCount().entrySet()) {
                if (Genotype.isHet(entry.getKey())) {
                    numHet += entry.getValue();
                }
            }

            // Compute heterozygosity and missigness scores
            stats.setHeterozygosityRate(((float) numHet) / numVariants);
//            if (stats.getGenotypeCount().containsKey("./.")) {
//                stats.setMissingnessScore(1.0D * stats.getGenotypeCount().get("./.") / numVariants);
//            } else {
//                stats.setMissingnessScore(0.0D);
//            }


            // Compute ti/tv ratio
            stats.setTiTvRatio(((float) ti[i]) / tv[i]);

            float meanQuality = (float) (qualSum[i] / qualCount[i]);
            stats.setQualityAvg(meanQuality);
            //Var = SumSq / n - mean * mean
            stats.setQualityStdDev((float) Math.sqrt(qualSumSq[i] / qualCount[i] - meanQuality * meanQuality));
        }
    }


    private void incCount(Map<String, Integer> map, String key) {
        if (StringUtils.isEmpty(key)) {
            // Nothing to do
            return;
        }

        map.merge(key, 1, Integer::sum);
    }

    private int getNumAltAlleles(String gt) {
        switch (gt) {
            case "0/0":
                return 0;
            case "0/1":
            case "0|1":
            case "1|0":
                return 1;
            case "1/1":
            case "1|1":
                return 2;
        }
        int num = 0;
        for (int allelesIdx : new Genotype(gt).getAllelesIdx()) {
            if (allelesIdx != 1) {
                // TODO: Should count only main alternates?
                num++;
            }
        }
        return num;
    }

    private List<Pair<String, Integer>> getTop50(Map<String, Integer> map) {
        // Set most affected genes (top 50)
        Map<String, Integer> sorted = map.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int j = 0;
        List<Pair<String, Integer>> top50 = new LinkedList<>();
        for (Map.Entry<String, Integer> entry: sorted.entrySet()) {
            top50.add(Pair.of(entry.getKey(), entry.getValue()));
            if (++j >= 50) {
                break;
            }
        }

        return top50;
    }

    private void init(List<String> samples) {
        this.samples = samples;
        this.samplesPos = new LinkedHashMap<>();
        for (String sample : samples) {
            samplesPos.put(sample, samplesPos.size());
        }

        if (sampleFileMap != null) {
            Set<String> files = new HashSet<>(sampleFileMap.values());
            // Only use the samplePosFileMap if there are more than one file. Otherwise, always take the first file
            if (files.size() != 1) {
                samplePosFileMap = new HashMap<>(sampleFileMap.size());
                sampleFileMap.forEach((sample, file) -> {
                    samplePosFileMap.put(samplesPos.get(sample), file);
                });
            }
        }

        int numSamples = samples.size();
        statsList = new ArrayList<>(numSamples);

        ti = new int[numSamples];
        tv = new int[numSamples];
        qualCount = new int[numSamples];
        qualSum = new double[numSamples];
        qualSumSq = new double[numSamples];

        for (String sample : samples) {
            SampleVariantStats stats = new SampleVariantStats(
                    sample,
                    0,
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new IndelLength(0, 0, 0, 0, 0),
                    new HashMap<>(),
                    0f,
                    0f,
                    0f,
                    0f,
                    new HashMap<>(),
                    new DepthCount(0, 0, 0, 0, 0, 0),
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>()
            );

            statsList.add(stats);
        }
    }
}
