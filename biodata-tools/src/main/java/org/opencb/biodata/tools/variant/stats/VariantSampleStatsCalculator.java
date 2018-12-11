package org.opencb.biodata.tools.variant.stats;

import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.clinical.interpretation.VariantClassification;
import org.opencb.biodata.models.clinical.pedigree.Member;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;
import org.opencb.biodata.models.clinical.pedigree.PedigreeManager;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.stats.LoF;
import org.opencb.biodata.models.variant.stats.VariantSampleStats;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.pedigree.MendelianError;
import org.opencb.commons.utils.ListUtils;
import org.opencb.biodata.models.feature.Genotype;

import java.util.*;
import java.util.stream.Collectors;

public class VariantSampleStatsCalculator {
    private List<VariantSampleStats> variantSampleStatsList;
    private List<Map<String, Integer>> geneCounters;
    private List<Map<String, Integer>> varTraitCounters;

    private List<Integer> ti;
    private List<Integer> tv;

    private List<Map<String, Set<String>>> hpos;
    private List<Map<String, Integer>> numAltAlleles;

    private PedigreeManager pedigreeManager;
    private List<Member> validChildren;

    public VariantSampleStatsCalculator() {
    }

    public List<VariantSampleStats> compute(List<Variant> variants) {
        return compute(variants, null);
    }

    public List<VariantSampleStats> compute(List<Variant> variants, Pedigree pedigree) {
        if (pedigree != null) {
            pedigreeManager = new PedigreeManager(pedigree);
            validChildren = getValidChildren(pedigreeManager);
        }

        // Main loop
        for (Variant variant: variants) {
            update(variant, pedigree);
        }

        // Post-processing
        post(variantSampleStatsList);

        return variantSampleStatsList;
    }

    public List<VariantSampleStats> compute(Iterator<Variant> variantIterator) {
        return compute(variantIterator, null);
    }

    public List<VariantSampleStats> compute(Iterator<Variant> variantIterator, Pedigree pedigree) {
        if (pedigree != null) {
            pedigreeManager = new PedigreeManager(pedigree);
            validChildren = getValidChildren(pedigreeManager);
        }

        // Main loop
        while (variantIterator.hasNext()) {
            update(variantIterator.next(), pedigree);
        }

        // Post-processing
        post(variantSampleStatsList);

        return variantSampleStatsList;
    }

    private void update(Variant variant, Pedigree pedigree) {
        if (ListUtils.isEmpty(variantSampleStatsList)) {
            init(variant);
        }

        List<List<String>> samplesData = variant.getStudies().get(0).getSamplesData();
        int size = variant.getStudies().get(0).getSamplesData().size();

        for (int i = 0; i < size; i++) {
            VariantSampleStats stats = variantSampleStatsList.get(i);

            // Update genotype: missing or non-variation or ... ?
            String gt = samplesData.get(i).get(0);
            if (gt.equals("0/0") || gt.equals("0|0")) {
                incCounter(stats.getGenotypeCounter(), "0/0");
            } else if (gt.equals("./.") || gt.equals(".|.") || gt.equals(".")) {
                incCounter(stats.getGenotypeCounter(), "./.");
            } else {
                incCounter(stats.getGenotypeCounter(), gt);
            }

            // Chromosome counter
            incCounter(stats.getChromosomeCounter(), variant.getChromosome());

            // Type counter
            incCounter(stats.getTypeCounter(), variant.getType().name());

            // Biotype and consequence type counters
            if (variant.getAnnotation() != null) {
                boolean isLof = false;
                String ensemblGeneId = null;
                if (ListUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        ensemblGeneId = ct.getEnsemblGeneId();

                        // Biotype counter
                        incCounter(stats.getBiotypeCounter(), ct.getBiotype());

                        // Gene counter
                        incCounter(geneCounters.get(i), ensemblGeneId);

                        if (ListUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
                            for (SequenceOntologyTerm so : ct.getSequenceOntologyTerms()) {
                                // Consequence type counter by SO accession
                                incCounter(stats.getConsequenceTypeCounter(), so.getAccession());

                                if (VariantClassification.LOF.contains(so.getName())) {
                                    isLof = true;
                                }
                            }
                        }
                    }

                    // Update trait association counters
                    if (ListUtils.isNotEmpty(variant.getAnnotation().getTraitAssociation())) {
                        for (EvidenceEntry evidenceEntry : variant.getAnnotation().getTraitAssociation()) {
                            if (StringUtils.isNotEmpty(evidenceEntry.getId())) {
                                incCounter(varTraitCounters.get(i), evidenceEntry.getId());
                            }
                        }
                    }
                }
                if (isLof) {
                    if (ensemblGeneId != null) {
                        if (!numAltAlleles.get(i).containsKey(ensemblGeneId)) {
                            numAltAlleles.get(i).put(ensemblGeneId, 0);
                            hpos.get(i).put(ensemblGeneId, new HashSet<>());
                        }
                        // Update HPO and number of alternate alleles
                        numAltAlleles.get(i).put(ensemblGeneId,
                                getNumAltAlleles(gt) + numAltAlleles.get(i).get(ensemblGeneId));

                        if (ListUtils.isNotEmpty(variant.getAnnotation().getGeneTraitAssociation())) {
                            for (GeneTraitAssociation trait : variant.getAnnotation().getGeneTraitAssociation()) {
                                if (StringUtils.isNotEmpty(trait.getHpo())) {
                                    hpos.get(i).get(ensemblGeneId).add(trait.getHpo());
                                }
                            }
                        }
                    }
                }
            }

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
            if (VariantStats.isTransition(variant.getReference(), variant.getAlternate())) {
                ti.set(i, ti.get(i) + 1);
            } else if (VariantStats.isTransversion(variant.getReference(), variant.getAlternate())) {
                tv.set(i, tv.get(i) + 1);
            }

            // Compute mendelian error
            if (ListUtils.isNotEmpty(validChildren)) {
                LinkedHashMap<String, Integer> samplesPos = variant.getStudies().get(0).getSamplesPosition();
                for (Member child: validChildren) {
                    Genotype childGt = new Genotype(samplesData.get(samplesPos.get(child.getId())).get(0));
                    Genotype fatherGt = new Genotype(samplesData.get(samplesPos.get(child.getFather().getId())).get(0));
                    Genotype motherGt = new Genotype(samplesData.get(samplesPos.get(child.getMother().getId())).get(0));

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
        }
    }

    private void post(List<VariantSampleStats> variantSampleStatsList) {
        for (int i = 0; i < variantSampleStatsList.size(); i++) {
            VariantSampleStats stats = variantSampleStatsList.get(i);

            // Compute number of variants from genotype counters
            int numVariants = 0;
            int numHet = 0;
            for (String gt: stats.getGenotypeCounter().keySet()) {
                numVariants += stats.getGenotypeCounter().get(gt);
                if (isHet(gt)) {
                    numHet += stats.getGenotypeCounter().get(gt);
                }
            }
            stats.setNumVariants(numVariants);

            // Set most affected genes (top 50)
            stats.setMostMutatedGenes(getTop50(geneCounters.get(i)));

            // Set most frequent variant traits (top 50)
            stats.setMostFrequentVarTraits(getTop50(varTraitCounters.get(i)));

            // Compute heterozigosity and missigness scores
            stats.setHeterozigosityScore(1.0D * numHet / numVariants);
            stats.setMissingnessScore(1.0D * stats.getGenotypeCounter().get("./.") / numVariants);

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

        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    private boolean isHet(String gt) {
        return !isHom(gt);
    }

    private boolean isHom(String gt) {
        String[] split = gt.split("[|/]");
        return (split[0].equals(split[1]));
    }

    private int getNumAltAlleles(String gt) {
        String[] split = gt.split("[|/]");
        int num = 0;
        if (!split[0].equals("0")) {
            num++;
        }
        if (!split[1].equals("0")) {
            num++;
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

    private void init(Variant variant) {
        int numSamples = variant.getStudies().get(0).getSamplesData().size();

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
