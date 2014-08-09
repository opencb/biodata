package org.opencb.biodata.models.variant.stats;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aleman
 * Date: 8/29/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariantSampleGroupStats {

    private String group;
    private Map<String, VariantSampleStats> sampleStats;

    public VariantSampleGroupStats() {
        sampleStats = new LinkedHashMap<>();

    }

    public VariantSampleGroupStats(List<VariantSampleGroupStats> sampleGroup) {
        this();


        VariantSampleStats variantSampleStatsAux;
        VariantSingleSampleStats variantSingleSampleStats;


        for (VariantSampleGroupStats sgs : sampleGroup) {
            this.setGroup(sgs.getGroup());
            for (Map.Entry<String, VariantSampleStats> ss : sgs.getSampleStats().entrySet()) {
                if (!this.sampleStats.containsKey(ss.getKey())) {
                    this.sampleStats.put(ss.getKey(), ss.getValue());
                } else {
                    variantSampleStatsAux = this.sampleStats.get(ss.getKey());
                    for (Map.Entry<String, VariantSingleSampleStats> entry : variantSampleStatsAux.getSamplesStats().entrySet()) {
                        variantSingleSampleStats = entry.getValue();
                        variantSingleSampleStats.incrementHomozygotesNumber(ss.getValue().getSamplesStats().get(entry.getKey()).getNumHomozygous());
                        variantSingleSampleStats.incrementMendelianErrors(ss.getValue().getSamplesStats().get(entry.getKey()).getNumMendelianErrors());
                        variantSingleSampleStats.incrementMissingGenotypes(ss.getValue().getSamplesStats().get(entry.getKey()).getNumMissingGenotypes());
                    }


                }


            }
        }
    }

    public Map<String, VariantSampleStats> getSampleStats() {
        return sampleStats;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return sampleStats.toString();
    }
}
