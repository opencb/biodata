/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.tools.variant.simulator;

import org.opencb.biodata.models.core.Region;

import java.util.List;

/**
 * Created by imedina on 08/10/15.
 */
public class Configuration {

    private int numSamples;
    private List<Region> regions;
    private float[] genotypeProbabilities;


    private String[] genotypeValues;

    public Configuration() {
        for (int i = 0; i <= 22; i++) {
            regions.add(new Region("" + i, 1));
        }
        regions.add(new Region("X", 1));
        regions.add(new Region("Y", 1));


        // 0/0, 0/1, 1/1, ./.
        genotypeProbabilities = new float[]{0.7f, 0.2f, 0.09f, 0.01f};

        init();
    }

    public Configuration(List<Region> regions, float[] genotypeProbabilities) {
        this.regions = regions;
        this.genotypeProbabilities = genotypeProbabilities;

        init();
    }

    private void init () {
        initGenotypeProbablities();
        // ...

    }

    private void initGenotypeProbablities() {
        genotypeValues = new String[1000];
//        for () {
//
//        }
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

    public float[] getGenotypeProbabilities() {
        return genotypeProbabilities;
    }

    public void setGenotypeProbabilities(float[] genotypeProbabilities) {
        this.genotypeProbabilities = genotypeProbabilities;
        initGenotypeProbablities();
    }

    public String[] getGenotypeValues() {
        return genotypeValues;
    }
}
