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

package org.opencb.biodata.tools.variant.stats;

import org.opencb.biodata.models.variant.*;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.run.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created on 19/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantGlobalStatsCalculator extends Task<Variant> {

    private final VariantFileMetadata metadata;
    private final String studyId;
    private VariantGlobalStats globalStats;
    private static Logger logger = LoggerFactory.getLogger(VariantGlobalStatsCalculator.class);

    public VariantGlobalStatsCalculator(String studyId, VariantFileMetadata metadata) {
        this.studyId = studyId;
        this.metadata = metadata;
    }

    @Override
    public boolean pre() {
        globalStats = new VariantGlobalStats();
        globalStats.setNumSamples(metadata.getSampleIds().size());
        return true;
    }

    @Override
    public boolean apply(List<Variant> batch) {

        for (Variant variant : batch) {
            updateGlobalStats(variant);
        }
        return true;
    }

    public synchronized void updateGlobalStats(Variant variant) {
        updateGlobalStats(variant, globalStats, studyId, metadata.getId());
    }

    public static void updateGlobalStats(Variant variant, VariantGlobalStats globalStats, String studyId, String fileId) {
        globalStats.setNumVariants(globalStats.getNumVariants() + 1);
        StudyEntry study = variant.getStudy(studyId);
        FileEntry file = study.getFile(fileId);
        if (file == null) {
            logger.warn("File \"{}\" not found in variant {}. Skip variant");
            return;
        }
        Map<String, String> attributes = file.getAttributes();

        globalStats.addChromosomeCount(variant.getChromosome(), 1);

        globalStats.addVariantTypeCount(variant.getType(), 1);

        if ("PASS".equalsIgnoreCase(attributes.get(StudyEntry.FILTER))) {
            globalStats.setNumPass(globalStats.getNumPass() + 1);
        }

        float qual = 0;
        if (attributes.containsKey(StudyEntry.QUAL) && !(".").equals(attributes.get(StudyEntry.QUAL))) {
            qual = Float.valueOf(attributes.get(StudyEntry.QUAL));
        }


        globalStats.setTransitionsCount(globalStats.getTransitionsCount() + (VariantStats.isTransition(variant.getReference(), variant.getAlternate()) ? 1 : 0));
        globalStats.setTransversionsCount(globalStats.getTransversionsCount() + (VariantStats.isTransversion(variant.getReference(), variant.getAlternate()) ? 1 : 0));
        globalStats.setAccumulatedQuality(globalStats.getAccumulatedQuality() + qual);
    }

    @Override
    public boolean post() {
        globalStats.setMeanQuality((float) (globalStats.getAccumulatedQuality() / globalStats.getNumVariants()));
        metadata.setStats(globalStats);
        return true;
    }
}
