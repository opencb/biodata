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

package org.opencb.biodata.models.variant.stats;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/29/13
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariantSampleStats {

    private Map<String, VariantSingleSampleStats> samplesStats;


    public VariantSampleStats(List<String> sampleNames) {
        samplesStats = new LinkedHashMap<>(sampleNames.size());
        VariantSingleSampleStats s;

        for (String name : sampleNames) {
            s = new VariantSingleSampleStats(name);
            samplesStats.put(name, s);
        }
    }

    public VariantSampleStats(List<String> sampleNames, List<VariantSampleStats> variantSampleStatses) {
        this(sampleNames);
        String sampleName;
        VariantSingleSampleStats ss, ssAux;
        Map<String, VariantSingleSampleStats> map;
        for (VariantSampleStats variantSampleStat : variantSampleStatses) {
            map = variantSampleStat.getSamplesStats();
            for (Map.Entry<String, VariantSingleSampleStats> entry : map.entrySet()) {
                sampleName = entry.getKey();
                ss = entry.getValue();
                ssAux = this.getSamplesStats().get(sampleName);
                ssAux.incrementMendelianErrors(ss.getNumMendelianErrors());
                ssAux.incrementMissingGenotypes(ss.getNumMissingGenotypes());
                ssAux.incrementHomozygotesNumber(ss.getNumHomozygous());
            }
        }
    }

    public Map<String, VariantSingleSampleStats> getSamplesStats() {
        return samplesStats;
    }

    public void incrementMendelianErrors(String sampleName) {
        VariantSingleSampleStats s = samplesStats.get(sampleName);
        s.incrementMendelianErrors();
    }

    public void incrementMissingGenotypes(String sampleName) {
        VariantSingleSampleStats s = samplesStats.get(sampleName);
        s.incrementMissingGenotypes();
    }

    public void incrementHomozygotesNumber(String sampleName) {
        VariantSingleSampleStats s = samplesStats.get(sampleName);
        s.incrementHomozygous();
    }

    @Override
    public String toString() {
        VariantSingleSampleStats s;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s%-10s%-10s%-10s\n", "Sample", "MissGt", "Mendel Err", "Homoz Count"));
        for (Map.Entry<String, VariantSingleSampleStats> entry : samplesStats.entrySet()) {
            s = entry.getValue();
            sb.append(String.format("%-10s%-10d%-10d%10d\n", s.getId(), s.getNumMissingGenotypes(), s.getNumMendelianErrors(), s.getNumHomozygous()));

        }
        return sb.toString();
    }
}
