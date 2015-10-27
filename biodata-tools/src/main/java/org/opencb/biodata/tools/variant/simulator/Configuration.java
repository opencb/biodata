package org.opencb.biodata.tools.variant.simulator;

/**
 * Created by kalyanreddyemani on 09/10/15.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencb.biodata.models.core.Region;

/**
 * Created by imedina on 08/10/15.
 */
public class Configuration {

    private int numSamples;
    private List<Region> regions;
    private Map<String,Float> genotypeProbabilities;
    private String[] genotypeValues;

    public Configuration() {
        regions = new ArrayList<>();
        for (int i = 0; i <= 22; i++) {
            Region region = new Region("" + i, 1);
            regions.add(region);

        }
        regions.add(new Region("X", 1));
        regions.add(new Region("Y", 1));

        /*genotypeProbabilities = new HashMap<String, Float>();
        genotypeProbabilities.put("0/0",0.7f);
        genotypeProbabilities.put("0/1",0.2f);
        genotypeProbabilities.put("1/1",0.09f);
        genotypeProbabilities.put("./.",0.01f);*/

        genotypeProbabilities = Configuration.this.getGenotypeProbabilities();
        // 0/0, 0/1, 1/1, ./.
        //genotypeProbabilities = new float[]{0.7f, 0.2f, 0.09f, 0.01f};
        init();
    }

    /**
     * @param regions
     * @param genotypeProbabilities
     */
    public Configuration(List<Region> regions, Map<String, Float> genotypeProbabilities) {
        this.regions = regions;
        this.genotypeProbabilities = genotypeProbabilities;
        init();
    }

    private void init () {
        String [] genotypeProbablitiesArryay = initGenotypeProbablities(1000);
    }

    /**
     * @param genotypeRowCount
     * @return genotypeValues
     */
    private String[] initGenotypeProbablities(int genotypeRowCount) {
        genotypeValues = new String[1000];

        int arryaIndex = 0;
        for(Map.Entry<String, Float> genotypeProbabilitiesMapValue: genotypeProbabilities.entrySet()){
            for(int j = 0; j < (Math.round(genotypeProbabilitiesMapValue.getValue() * genotypeRowCount)); j++){
                genotypeValues[j + arryaIndex] = genotypeProbabilitiesMapValue.getKey();
            }
            arryaIndex = arryaIndex + (Math.round(genotypeProbabilitiesMapValue.getValue() * genotypeRowCount));
        }
        return genotypeValues;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public Map<String, Float> getGenotypeProbabilities() {
        return genotypeProbabilities;
    }

    public void setGenotypeProbabilities(Map<String,Float> genotypeProbabilities) {
        this.genotypeProbabilities = genotypeProbabilities;
        initGenotypeProbablities(1000);
    }

    public String[] getGenotypeValues() {
        return genotypeValues;
    }
}