package org.opencb.biodata.tools.variant.stats;

import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.clinical.interpretation.VariantClassification;
import org.opencb.biodata.models.clinical.pedigree.Member;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;
import org.opencb.biodata.models.clinical.pedigree.PedigreeManager;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.stats.*;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.pedigree.MendelianError;
import org.opencb.biodata.tools.variant.algorithm.IdentityByDescentClustering;
import org.opencb.commons.run.Task;
import org.opencb.commons.utils.ListUtils;
import org.opencb.biodata.models.feature.Genotype;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class VariantSampleStatsCalculator implements Task<Variant, Variant> {
    private List<VariantSampleStats> variantSampleStatsList;
    private List<Map<String, Integer>> geneCounters;
    private List<Map<String, Integer>> varTraitCounters;

    private List<Integer> ti;
    private List<Integer> tv;

    private List<Map<String, Set<String>>> hpos;
    private List<Map<String, Integer>> numAltAlleles;

    private PedigreeManager pedigreeManager;
    private Pedigree pedigree;
    private List<Member> validChildren;

    private List<IdentityByState> identityByStates;
    private IBDExpectedFrequencies ibdExpFreqs;
    private IdentityByDescentClustering ibdc;
    private List<String> samples;


    public VariantSampleStatsCalculator() {
        this(null);
    }

    public VariantSampleStatsCalculator(Pedigree pedigree) {
        this.pedigree = pedigree;
        validChildren = null;
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
        variantSampleStatsList = null;
        ibdExpFreqs = new IBDExpectedFrequencies();
        ibdc = new IdentityByDescentClustering();

        if (pedigree != null) {
            pedigreeManager = new PedigreeManager(pedigree);

            samples = new ArrayList<>(pedigreeManager.getIndividualMap().keySet());
            validChildren = getValidChildren(pedigreeManager);
            identityByStates = ibdc.getIbsClustering().initCounts(samples);

        }
    }

    public List<VariantSampleStats> compute(List<Variant> variants) {
        return compute(variants.iterator(), null);
    }

    public List<VariantSampleStats> compute(List<Variant> variants, Pedigree pedigree) {
        return compute(variants.iterator(), pedigree);
    }

    public List<VariantSampleStats> compute(Iterator<Variant> variantIterator) {
        return compute(variantIterator, null);
    }

    public List<VariantSampleStats> compute(Iterator<Variant> variantIterator, Pedigree pedigree) {
        this.pedigree = pedigree;

        pre();

        // Main loop
        Variant variant;
        while (variantIterator.hasNext()) {
            variant = variantIterator.next();

            update(variant);
        }

        // Post-processing
        post();

        return variantSampleStatsList;
    }

    public void update(Variant variant) {
        List<List<String>> samplesData = variant.getStudies().get(0).getSamplesData();
        update(variant, variant.getAnnotation(),
                variant.getStudies().get(0).getSamplesData().size(),
                idx -> samplesData.get(idx).get(0),
                variant.getStudies().get(0).getSamplesPosition());
    }

    public void update(Variant variant, VariantAnnotation annotation, LinkedHashMap<String, Integer> samplesPos, List<String> gts) {
        update(variant, annotation, gts.size(), gts::get, samplesPos);
    }

    private void update(Variant variant, VariantAnnotation annotation, int numSamples, IntFunction<String> gts,
                        LinkedHashMap<String, Integer> samplesPos) {
        // TODO: Remove need of full Variant object, so it is more handy to use from schema-less frameworks
        if (variantSampleStatsList == null) {
            init(numSamples);
        }

        boolean transition = VariantStats.isTransition(variant.getReference(), variant.getAlternate());
        boolean transversion = VariantStats.isTransversion(variant.getReference(), variant.getAlternate());
        Set<String> biotypes = new HashSet<>();
        Set<String> genes = new HashSet<>();
        Set<String> cts = new HashSet<>();
        Set<String> traits = new HashSet<>();

        boolean isLof = false;
        String ensemblGeneId = null;
        if (annotation != null) {
            if (ListUtils.isNotEmpty(annotation.getConsequenceTypes())) {
                for (ConsequenceType ct : annotation.getConsequenceTypes()) {
                    ensemblGeneId = ct.getEnsemblGeneId();

                    if (StringUtils.isNotEmpty(ct.getBiotype())) {
                        biotypes.add(ct.getBiotype());
                    }
                    if (StringUtils.isNotEmpty(ensemblGeneId)) {
                        genes.add(ensemblGeneId);
                    }

                    if (ListUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
                        for (SequenceOntologyTerm so : ct.getSequenceOntologyTerms()) {
                            cts.add(so.getAccession());

                            if (VariantClassification.LOF.contains(so.getName())) {
                                isLof = true;
                            }
                        }
                    }
                }

                // Update trait association counters
                if (ListUtils.isNotEmpty(annotation.getTraitAssociation())) {
                    for (EvidenceEntry evidenceEntry : annotation.getTraitAssociation()) {
                        if (StringUtils.isNotEmpty(evidenceEntry.getId())) {
                            traits.add(evidenceEntry.getId());
                        }
                    }
                }
            }
        }

        for (int i = 0; i < numSamples; i++) {
            VariantSampleStats stats = variantSampleStatsList.get(i);

            String gt = gts.apply(i);
            boolean hasMainAlternate = Genotype.hasMainAlternate(gt);
            // Update genotype: missing or non-variation or ... ?
            if (gt.equals("0/0") || gt.equals("0|0")) {
                incCounter(stats.getGenotypeCounter(), "0/0");
            } else if (gt.equals("./.") || gt.equals(".|.") || gt.equals(".")) {
                incCounter(stats.getGenotypeCounter(), "./.");
            } else {
                incCounter(stats.getGenotypeCounter(), gt);
            }

            // Compute mendelian error
            if (ListUtils.isNotEmpty(validChildren)) {
                for (Member child: validChildren) {
                    Genotype childGt = new Genotype(gts.apply(samplesPos.get(child.getId())));
                    Genotype fatherGt = new Genotype(gts.apply(samplesPos.get(child.getFather().getId())));
                    Genotype motherGt = new Genotype(gts.apply(samplesPos.get(child.getMother().getId())));

                    int errorCode = MendelianError.compute(fatherGt, motherGt, childGt, variant.getChromosome());
                    if (errorCode > 0) {
                        Map<Integer, Integer> mendelianErrors = stats.getMendelianErrorCounters();
                        if (!mendelianErrors.containsKey(errorCode)) {
                            mendelianErrors.put(errorCode, 0);
                        } else {
                            mendelianErrors.put(errorCode, 1 + mendelianErrors.get(errorCode));
                        }
                    }
                }
            }

            // Only increase these counters if this sample has the mutation (i.e. has the main allele in the genotype)
            if (hasMainAlternate) {
                stats.setNumVariants(stats.getNumVariants() + 1);

                // Chromosome counter
                incCounter(stats.getChromosomeCounter(), variant.getChromosome());

                // Type counter
                incCounter(stats.getTypeCounter(), variant.getType().name());

                // Indel length
                if (variant.getType() == VariantType.INDEL) {
                    int index;
                    if (variant.getLength() > 20) {
                        index = 5;
                    } else {
                        index = variant.getLength() % 5;
                    }
                    stats.getIndelLength().set(index, stats.getIndelLength().get(index) + 1);
                }

                // Accumulate transitions and transversions in order to compute ti/tv ratio later
                if (transition) {
                    ti.set(i, ti.get(i) + 1);
                } else if (transversion) {
                    tv.set(i, tv.get(i) + 1);
                }

                // Biotype and consequence type counters
                // Biotype counter
                for (String biotype : biotypes) {
                    incCounter(stats.getBiotypeCounter(), biotype);
                }

                // Gene counter
                for (String gene : genes) {
                    incCounter(geneCounters.get(i), gene);
                }

                for (String ct : cts) {
                    incCounter(stats.getConsequenceTypeCounter(), ct);
                }

                for (String trait : traits) {
                    incCounter(varTraitCounters.get(i), trait);
                }

                if (isLof && (ensemblGeneId != null)) {
                    // Update HPO and number of alternate alleles
                    numAltAlleles.get(i).compute(ensemblGeneId, (k, v) -> (v == null ? 0 : v) + getNumAltAlleles(gt));

                    if (ListUtils.isNotEmpty(annotation.getGeneTraitAssociation())) {
                        for (GeneTraitAssociation trait : annotation.getGeneTraitAssociation()) {
                            if (StringUtils.isNotEmpty(trait.getHpo())) {
                                hpos.get(i).computeIfAbsent(ensemblGeneId, v -> new HashSet<>()).add(trait.getHpo());
                            }
                        }
                    }
                }
            }
        }

        if (pedigree != null) {
            ibdExpFreqs.update(variant);
            ibdc.getIbsClustering().countIBS(variant, samples, identityByStates);
        }
    }

    @Override
    public void post() {
        if (pedigree != null) {
            ibdExpFreqs.done();
            List<IdentityByDescent> identityByDescents = ibdc.countIBD(identityByStates, ibdExpFreqs);

            // TODO: check samples vs samples data size!
            // (samples.size() != variant.getStudies().get(0).getSamplesData().size())

            List<String> finalSamples = samples;
            ibdc.getIbsClustering()
                    .forEachPair(samples, (int firstSampleIndex, int secondSampleIndex, int compoundIndex) -> {
                        variantSampleStatsList.get(firstSampleIndex).getRelatednessScores()
                                .put(finalSamples.get(secondSampleIndex), identityByDescents.get(compoundIndex));
                        variantSampleStatsList.get(secondSampleIndex).getRelatednessScores()
                                .put(finalSamples.get(firstSampleIndex), identityByDescents.get(compoundIndex));
                    });
        }

        for (int i = 0; i < variantSampleStatsList.size(); i++) {
            VariantSampleStats stats = variantSampleStatsList.get(i);

            // Compute number of variants from genotype counters
            int numVariants = stats.getNumVariants();
            int numHet = 0;
            for (String gt: stats.getGenotypeCounter().keySet()) {
//                numVariants += stats.getGenotypeCounter().get(gt);
                if (Genotype.isHet(gt)) {
                    numHet += stats.getGenotypeCounter().get(gt);
                }
            }

            // Set most affected genes (top 50)
            stats.setMostMutatedGenes(getTop50(geneCounters.get(i)));

            // Set most frequent variant traits (top 50)
            stats.setMostFrequentVarTraits(getTop50(varTraitCounters.get(i)));

            // Compute heterozygosity and missigness scores
            stats.setHeterozygosityScore(1.0D * numHet / numVariants);
            if (stats.getGenotypeCounter().containsKey("./.")) {
                stats.setMissingnessScore(1.0D * stats.getGenotypeCounter().get("./.") / numVariants);
            } else {
                stats.setMissingnessScore(0.0D);
            }


            // Compute ti/tv ratio
            stats.setTiTvRatio(1.D * ti.get(i) / tv.get(i));

            // Update LoF
            LoF lof = new LoF();
            Set<String> hpoSet = new HashSet<>();
            for (String geneId: hpos.get(i).keySet()) {
                hpoSet.addAll(hpos.get(i).get(geneId));

                LoF.Gene lofGene = lof.newGene();
                lofGene.setEnsemblGeneId(geneId);
                lofGene.getHpos().addAll(hpos.get(i).get(geneId));
                lofGene.setNumAltAlleles(numAltAlleles.get(i).get(geneId));

                lof.getGenes().add(lofGene);
            }
            lof.getHpos().addAll(hpoSet);
            stats.setLof(lof);
        }
    }


    private List<Member> getValidChildren(PedigreeManager pedigreeManager) {
        List<Member> children = new ArrayList<>();
        Map<String, Member> individualMap = pedigreeManager.getIndividualMap();

        for (Member member: individualMap.values()) {
            if (member.getFather() != null && member.getMother() != null) {
                children.add(member);
            }
        }
        return children;
    }

    private void incCounter(Map<String, Integer> map, String key) {
        if (StringUtils.isEmpty(key)) {
            // Nothing to do
            return;
        }

        map.compute(key, (k, v) -> v == null ? 1 : v + 1);
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
            top50.add(new Pair<>(entry.getKey(), entry.getValue()));
            if (++j >= 50) {
                break;
            }
        }

        return top50;
    }

    private void init(int numSamples) {

        variantSampleStatsList = new ArrayList<>(numSamples);
        geneCounters = new ArrayList<>(numSamples);
        varTraitCounters = new ArrayList<>(numSamples);

        ti = new ArrayList<>(numSamples);
        tv = new ArrayList<>(numSamples);

        hpos = new ArrayList<>();
        numAltAlleles = new ArrayList<>();

        for (int i = 0; i < numSamples; i++) {
            variantSampleStatsList.add(new VariantSampleStats());
            geneCounters.add(new HashMap<>());
            varTraitCounters.add(new HashMap<>());

            ti.add(0);
            tv.add(0);

            hpos.add(new HashMap<>());
            numAltAlleles.add(new HashMap<>());
        }
    }
}
