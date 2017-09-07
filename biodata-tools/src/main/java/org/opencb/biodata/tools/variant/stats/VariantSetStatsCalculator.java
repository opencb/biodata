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

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.*;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.stats.VariantSetStats;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.run.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 19/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantSetStatsCalculator extends Task<Variant> {

    private final VariantFileMetadata metadata;
    private final String studyId;
    private VariantSetStats variantSetStats;
    private static Logger logger = LoggerFactory.getLogger(VariantSetStatsCalculator.class);

    public VariantSetStatsCalculator(String studyId, VariantFileMetadata metadata) {
        this.studyId = studyId;
        this.metadata = metadata;
    }

    @Override
    public boolean pre() {
        variantSetStats = new VariantSetStats();
        variantSetStats.setNumSamples(metadata.getSampleIds().size());
        return true;
    }

    @Override
    public synchronized boolean apply(List<Variant> batch) {

        for (Variant variant : batch) {
            updateVariantSetStats(variant);
        }
        return true;
    }

    public void updateVariantSetStats(Variant variant) {
        updateVariantSetStats(variant, variantSetStats, studyId, metadata.getId());
    }

    public static void updateVariantSetStats(Variant variant, VariantSetStats stats, String studyId, String fileId) {
        stats.setNumVariants(stats.getNumVariants() + 1);
        StudyEntry study = variant.getStudy(studyId);
        FileEntry file = study.getFile(fileId);
        if (file == null) {
            logger.warn("File \"{}\" not found in variant {}. Skip variant");
            return;
        }
        Map<String, String> attributes = file.getAttributes();

        stats.addChromosomeCount(variant.getChromosome(), 1);

        stats.addVariantTypeCount(variant.getType(), 1);

        if ("PASS".equalsIgnoreCase(attributes.get(StudyEntry.FILTER))) {
            stats.setNumPass(stats.getNumPass() + 1);
        }

        float qual = 0;
        if (attributes.containsKey(StudyEntry.QUAL) && !(".").equals(attributes.get(StudyEntry.QUAL))) {
            qual = Float.valueOf(attributes.get(StudyEntry.QUAL));
        }


        stats.setTransitionsCount(stats.getTransitionsCount() + (VariantStats.isTransition(variant.getReference(), variant.getAlternate()) ? 1 : 0));
        stats.setTransversionsCount(stats.getTransversionsCount() + (VariantStats.isTransversion(variant.getReference(), variant.getAlternate()) ? 1 : 0));
        stats.setAccumulatedQuality(stats.getAccumulatedQuality() + qual);
    }

    @Override
    public boolean post() {
        variantSetStats.setMeanQuality((float) (variantSetStats.getAccumulatedQuality() / variantSetStats.getNumVariants()));
        variantSetStats.updateTiTvRatio();
        Map<String, Integer> chrLengthMap = metadata.getHeader().getComplexLines()
                .stream()
                .filter(line -> line.getKey().equalsIgnoreCase("contig"))
                .collect(Collectors.toMap(line -> Region.normalizeChromosome(line.getId()), line -> {
                    String length = line.getGenericFields().get("length");
                    if (StringUtils.isNumeric(length)) {
                        return Integer.parseInt(length);
                    } else {
                        return -1;
                    }
                }));
        variantSetStats.getChromosomeStats().forEach((chr, stats) -> {
            Integer length = chrLengthMap.get(chr);
            if (length != null && length > 0) {
                stats.setDensity(stats.getCount() / (float) length);
            }
        });
        metadata.setStats(variantSetStats);
        return true;
    }
}
