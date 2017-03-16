/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant.simulator;

/**
 * Created by kalyanreddyemani on 09/10/15.
 */

import org.opencb.biodata.models.core.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by imedina on 08/10/15.
 */
public class VariantSimulatorConfiguration {

    private int numSamples;
    private List<Region> regions;
    private Map<String, Double> genotypeProbabilities;
    private String[] genotypeValues;

    public VariantSimulatorConfiguration() {
        regions = getDefaultRegions();

        genotypeProbabilities = new HashMap<>();
        genotypeProbabilities.put("0/0", 0.7d);
        genotypeProbabilities.put("0/1", 0.2d);
        genotypeProbabilities.put("1/1", 0.09d);
        genotypeProbabilities.put("./.", 0.01d);

        // 0/0, 0/1, 1/1, ./.
        //genotypeProbabilities = new float[]{0.7f, 0.2f, 0.09f, 0.01f};
        init();
    }

    private List<Region> getDefaultRegions() {
        List<Region> regions = new ArrayList<>();
        regions.add(new Region("1", 1, 249250621));
        regions.add(new Region("2", 1, 243199373));
        regions.add(new Region("3", 1, 198022430));
        regions.add(new Region("4", 1, 191154276));
        regions.add(new Region("5", 1, 180915260));
        regions.add(new Region("6", 1, 171115067));
        regions.add(new Region("7", 1, 159138663));
        regions.add(new Region("8", 1, 146364022));
        regions.add(new Region("9", 1, 141213431));
        regions.add(new Region("10", 1, 135534747));
        regions.add(new Region("11", 1, 135006516));
        regions.add(new Region("12", 1, 133851895));
        regions.add(new Region("13", 1, 115169878));
        regions.add(new Region("14", 1, 107349540));
        regions.add(new Region("15", 1, 102531392));
        regions.add(new Region("16", 1, 90354753));
        regions.add(new Region("17", 1, 81195210));
        regions.add(new Region("18", 1, 78077248));
        regions.add(new Region("19", 1, 59128983));
        regions.add(new Region("20", 1, 63025520));
        regions.add(new Region("21", 1, 48129895));
        regions.add(new Region("22", 1, 50818468));
        regions.add(new Region("X", 1, 156040895));
        regions.add(new Region("Y", 1, 57227415));
        return regions;
    }

    /**
     * @param regions
     * @param genotypeProbabilities
     */
    public VariantSimulatorConfiguration(List<Region> regions, Map<String, Double> genotypeProbabilities) {
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

        int arrayIndex = 0;
        for(Map.Entry<String, Double> genotypeProbabilitiesMapValue: genotypeProbabilities.entrySet()){
            for(int j = 0; j < (Math.round(genotypeProbabilitiesMapValue.getValue() * genotypeRowCount)); j++){
                genotypeValues[j + arrayIndex] = genotypeProbabilitiesMapValue.getKey();
            }
            arrayIndex += (Math.round(genotypeProbabilitiesMapValue.getValue() * genotypeRowCount));
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

    public Map<String, Double> getGenotypeProbabilities() {
        return genotypeProbabilities;
    }

    public void setGenotypeProbabilities(Map<String, Double> genotypeProbabilities) {
        this.genotypeProbabilities = genotypeProbabilities;
        initGenotypeProbablities(1000);
    }

    public String[] getGenotypeValues() {
        return genotypeValues;
    }
}