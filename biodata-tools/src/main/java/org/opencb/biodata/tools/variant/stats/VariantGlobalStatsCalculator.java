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

    private final VariantSource source;
    private VariantGlobalStats globalStats;
    private static Logger logger = LoggerFactory.getLogger(VariantGlobalStatsCalculator.class);

    public VariantGlobalStatsCalculator(VariantSource source) {
        this.source = source;
    }

    @Override
    public boolean pre() {
        globalStats = new VariantGlobalStats();
        globalStats.setSamplesCount(source.getSamples().size());
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
        updateGlobalStats(variant, globalStats, source);
    }

    public static void updateGlobalStats(Variant variant, VariantGlobalStats globalStats, VariantSource source) {
        globalStats.setNumRecords(globalStats.getNumRecords() + 1);
        StudyEntry study = variant.getStudy(source.getStudyId());
        FileEntry file = study.getFile(source.getFileId());
        if (file == null) {
            logger.warn("File \"{}\" not found in variant {}. Skip variant");
            return;
        }
        Map<String, String> attributes = file.getAttributes();

        globalStats.addChromosomeCount(variant.getChromosome(), 1);

        globalStats.addVariantTypeCount(variant.getType(), 1);

        if ("PASS".equalsIgnoreCase(attributes.get(StudyEntry.FILTER))) {
            globalStats.setPassCount(globalStats.getPassCount() + 1);
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
        globalStats.setMeanQuality(globalStats.getAccumulatedQuality() / globalStats.getVariantsCount());
        source.setStats(globalStats);
        return true;
    }
}
