package org.opencb.biodata.models.variant.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 11/11/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariantStatsWrapper {

    private List<VariantStats> variantStats;
    private List<VariantGlobalStats> variantGlobalStats;
    private List<VariantSampleStats> variantSampleStats;
    private Map<String, VariantGroupStats> groupStats;
    private Map<String, List<VariantSampleGroupStats>> sampleGroupStats;
    private List<String> sampleNames;

    public VariantStatsWrapper() {
        variantGlobalStats = new ArrayList<>();
        variantSampleStats = new ArrayList<>();
        groupStats = new LinkedHashMap<>();
        sampleGroupStats = new LinkedHashMap<>();
    }

    public List<String> getSampleNames() {
        return sampleNames;
    }

    public void setSampleNames(List<String> sampleNames) {
        this.sampleNames = sampleNames;
    }

    public List<VariantStats> getVariantStats() {
        return variantStats;
    }

    public void setVariantStats(List<VariantStats> variantStats) {
        this.variantStats = variantStats;
    }

    public void addGlobalStats(VariantGlobalStats gs) {
        variantGlobalStats.add(gs);
    }

    public void addSampleStats(VariantSampleStats ss) {
        variantSampleStats.add(ss);
    }

    public List<VariantGlobalStats> getVariantGlobalStats() {
        return variantGlobalStats;
    }

    public List<VariantSampleStats> getVariantSampleStats() {
        return variantSampleStats;
    }

    public void addGroupStats(String group) {
        if (!groupStats.containsKey(group)) {
            groupStats.put(group, null);
        }
    }

    public void addGroupStats(String group, VariantGroupStats gs) {
        groupStats.put(group, gs);

    }

    public VariantGroupStats getGroupStats(String group) {
        return groupStats.get(group);
    }

    public void addSampleGroupStats(String group, VariantSampleGroupStats sgs) {

        List<VariantSampleGroupStats> list;
        System.out.println("group = " + group);
        if (!sampleGroupStats.containsKey(group)) {
            System.out.println("group = " + group);
            list = new ArrayList<>();
            sampleGroupStats.put(group, list);
        } else {
            list = sampleGroupStats.get(group);
        }
        list.add(sgs);
    }

    public List<VariantSampleGroupStats> getSampleGroupStats(String group) {
        return sampleGroupStats.get(group);
    }

    public VariantGlobalStats getFinalGlobalStats() {

        VariantGlobalStats gsFinal = new VariantGlobalStats();

        for (VariantGlobalStats gs : this.variantGlobalStats) {
            gsFinal.updateStats(gs.getVariantsCount(), gs.getSamplesCount(),
                    gs.getSnpsCount(), gs.getIndelsCount(), gs.getPassCount(),
                    gs.getTransitionsCount(), gs.getTransversionsCount(), gs.getBiallelicsCount(), gs.getMultiallelicsCount(), gs.getAccumQuality());
        }
        return gsFinal;
    }

    public VariantSampleStats getFinalSampleStats() {

        VariantSampleStats ssFinal = new VariantSampleStats(this.getSampleNames());

        String sampleName;
        VariantSingleSampleStats ss, ssAux;
        Map<String, VariantSingleSampleStats> map;

        for (VariantSampleStats variantSampleStats : this.variantSampleStats) {
            map = variantSampleStats.getSamplesStats();
            for (Map.Entry<String, VariantSingleSampleStats> entry : map.entrySet()) {
                sampleName = entry.getKey();
                ss = entry.getValue();
                ssAux = ssFinal.getSamplesStats().get(sampleName);
                ssAux.incrementMendelianErrors(ss.getMendelianErrors());
                ssAux.incrementMissingGenotypes(ss.getMissingGenotypes());
                ssAux.incrementHomozygotesNumber(ss.getHomozygotesNumber());
            }
        }

        return ssFinal;

    }

    public VariantSampleGroupStats getFinalSampleGroupStat(String group) {
        VariantSampleGroupStats sgsFinal = new VariantSampleGroupStats();

        VariantSampleStats variantSampleStatsAux;
        VariantSingleSampleStats variantSingleSampleStats;

        System.out.println(this.sampleGroupStats);
        for (VariantSampleGroupStats sgs : this.sampleGroupStats.get(group)) {
            sgsFinal.setGroup(sgs.getGroup());
            for (Map.Entry<String, VariantSampleStats> ss : sgs.getSampleStats().entrySet()) {
                if (!sgsFinal.getSampleStats().containsKey(ss.getKey())) {
                    sgsFinal.getSampleStats().put(ss.getKey(), ss.getValue());
                } else {
                    variantSampleStatsAux = sgsFinal.getSampleStats().get(ss.getKey());
                    for (Map.Entry<String, VariantSingleSampleStats> entry : variantSampleStatsAux.getSamplesStats().entrySet()) {
                        variantSingleSampleStats = entry.getValue();
                        variantSingleSampleStats.incrementHomozygotesNumber(ss.getValue().getSamplesStats().get(entry.getKey()).getHomozygotesNumber());
                        variantSingleSampleStats.incrementMendelianErrors(ss.getValue().getSamplesStats().get(entry.getKey()).getMendelianErrors());
                        variantSingleSampleStats.incrementMissingGenotypes(ss.getValue().getSamplesStats().get(entry.getKey()).getMissingGenotypes());
                    }
                }
            }
        }

        return sgsFinal;
    }
}
