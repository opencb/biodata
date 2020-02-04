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
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyStats;
import org.opencb.biodata.models.variant.stats.VariantSetStats;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.run.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 19/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantSetStatsCalculator implements Task<Variant, Variant> {

    private final String studyId;
    private final Set<String> files;
    private final int numSamples;
    private final Map<String, Integer> chrLengthMap;
    private static Logger logger = LoggerFactory.getLogger(VariantSetStatsCalculator.class);

    protected VariantSetStats stats;

    protected int transitionsCount = 0;
    protected int transversionsCount = 0;
    protected double qualCount = 0;
    protected double qualSum = 0;
    protected double qualSumSq = 0;

    /**
     * Calculate global statistics for the whole study. i.e. cohort ALL
     * @param metadata VariantStudyMetadata
     */
    public VariantSetStatsCalculator(VariantStudyMetadata metadata) {
        this.studyId = metadata.getId();
        files = metadata.getFiles()
                .stream()
                .map(org.opencb.biodata.models.variant.metadata.VariantFileMetadata::getId)
                .collect(Collectors.toSet());
        numSamples = metadata.getFiles()
                .stream()
                .flatMap(fileMetadata -> fileMetadata.getSampleIds().stream())
                .collect(Collectors.toSet()).size();
        chrLengthMap = getChromosomeLengthsMap(metadata.getAggregatedHeader());
        stats = new VariantSetStats();
        if (metadata.getStats() == null) {
            metadata.setStats(new VariantStudyStats(new HashMap<>(), new HashMap<>()));
        }
        if (metadata.getStats().getCohortStats() == null) {
            metadata.getStats().setCohortStats(new HashMap<>());
        }
        metadata.getStats().getCohortStats().put(StudyEntry.DEFAULT_COHORT, stats.getImpl());
    }

    /**
     * Calculates VariantSetStats for a file.
     * @param studyId       StudyId
     * @param fileMetadata  VariantFileMetadata
     */
    public VariantSetStatsCalculator(String studyId, VariantFileMetadata fileMetadata) {
        this(studyId, Collections.singleton(fileMetadata.getId()), fileMetadata.getSampleIds().size(),
                getChromosomeLengthsMap(fileMetadata.getHeader()));
        fileMetadata.setStats(stats);
    }

    public VariantSetStatsCalculator(String studyId, Set<String> files, int numSamples, Map<String, Integer> chrLengthMap) {
        this.studyId = studyId;
        this.files = files;
        this.numSamples = numSamples;
        this.chrLengthMap = chrLengthMap == null ? Collections.emptyMap() : chrLengthMap;
        stats = new VariantSetStats();
    }

    @Override
    public void pre() {
    }

    @Override
    public synchronized List<Variant> apply(List<Variant> batch) {
        for (Variant variant : batch) {
            updateVariantSetStats(variant);
        }
        return batch;
    }

    private void updateVariantSetStats(Variant variant) {
        StudyEntry study = variant.getStudy(studyId);
        if (study == null) {
            return;
        }
        boolean validVariant = false;
        List<FileEntry> fileEntries;
        if (files == null) {
            fileEntries = study.getFiles();
        } else {
            fileEntries = new ArrayList<>(files.size());
            for (FileEntry fileEntry : study.getFiles()) {
                if (!files.contains(fileEntry.getFileId())) {
//                logger.warn("File \"{}\" not found in variant {}. Skip variant", fileId, variant);
                    continue;
                }
                fileEntries.add(fileEntry);
            }
        }
        if (!fileEntries.isEmpty()) {
            validVariant = true;
            updateVariantSetStats(fileEntries);
        }
        if (validVariant) {
            stats.setNumVariants(stats.getNumVariants() + 1);
            stats.addChromosomeCount(variant.getChromosome(), 1);
            stats.addVariantTypeCount(variant.getType(), 1);
            if (VariantStats.isTransition(variant.getReference(), variant.getAlternate())) {
                transitionsCount++;
            }
            if (VariantStats.isTransversion(variant.getReference(), variant.getAlternate())) {
                transversionsCount++;
            }
            updateVariantSetStats(variant.getAnnotation());
        }
    }

    private void updateVariantSetStats(List<FileEntry> files) {

        int numPass = 0;
        for (FileEntry file : files) {
            Map<String, String> attributes = file.getAttributes();

            if (attributes.containsKey(StudyEntry.QUAL) && !(".").equals(attributes.get(StudyEntry.QUAL))) {
                float qual = Float.valueOf(attributes.get(StudyEntry.QUAL));
                qualCount++;
                qualSum += qual;
                qualSumSq += qual * qual;
            }
            if ("PASS".equalsIgnoreCase(attributes.get(StudyEntry.FILTER))) {
                numPass++;
            }
        }

        // Count +1 PASS variant if ANY of the files is PASS
        if (numPass > 0) {
            stats.addNumPass(1);
        }

    }

    private void updateVariantSetStats(VariantAnnotation annotation) {
        if (annotation != null) {
            for (ConsequenceType consequenceType : annotation.getConsequenceTypes()) {
                String biotype = consequenceType.getBiotype();
                if (StringUtils.isNotEmpty(biotype)) {
                    stats.addVariantBiotypeCounts(biotype, 1);
                }
                if (consequenceType.getSequenceOntologyTerms() != null) {
                    for (SequenceOntologyTerm term : consequenceType.getSequenceOntologyTerms()) {
                        stats.getConsequenceTypesCounts().merge(term.getName(), 1, Integer::sum);
                    }
                }
            }
        }
    }

    @Override
    public synchronized void post() {
        stats.setNumSamples(numSamples);
        float meanQuality = (float) (qualSum / qualCount);
        stats.setMeanQuality(meanQuality);
        //Var = SumSq / n - mean * mean
        stats.setStdDevQuality((float) Math.sqrt(qualSumSq / qualCount - meanQuality * meanQuality));
        stats.setTiTvRatio(transitionsCount, transversionsCount);
        stats.getChromosomeCounts().forEach((chr, count) -> {
            Integer length = chrLengthMap.get(chr);
            if (length != null && length > 0) {
                stats.getChromosomeDensity().put(chr, count / (float) length);
            }
        });
    }

    public VariantSetStats getStats() {
        return stats;
    }

    public static void merge(VariantSetStats thisStats, VariantSetStats otherStats) {
        merge(thisStats.getImpl(), otherStats.getImpl());
    }

    public static void merge(org.opencb.biodata.models.variant.metadata.VariantSetStats thisStats,
                                        org.opencb.biodata.models.variant.metadata.VariantSetStats otherStats) {

        thisStats.setNumVariants(thisStats.getNumVariants() + otherStats.getNumVariants());
//        thisStats.setNumSamples(thisStats.getNumSamples() + otherStats.getNumSamples());
        thisStats.setNumPass(thisStats.getNumPass() + otherStats.getNumPass());
//        thisStats.setTiTvRatio(thisStats.getTiTvRatio() + otherStats.getTiTvRatio());
//        thisStats.setMeanQuality(thisStats.getMeanQuality() + otherStats.getMeanQuality());
//        thisStats.setStdDevQuality(thisStats.getStdDevQuality() + otherStats.getStdDevQuality());

//        mergeCounts(thisStats.getNumRareVariants(), otherStats.getNumRareVariants());
        mergeCounts(thisStats.getVariantTypeCounts(), otherStats.getVariantTypeCounts());
        mergeCounts(thisStats.getVariantBiotypeCounts(), otherStats.getVariantBiotypeCounts());
        mergeCounts(thisStats.getConsequenceTypesCounts(), otherStats.getConsequenceTypesCounts());

        otherStats.getChromosomeCounts()
                .forEach((key, count) -> thisStats.getChromosomeCounts().merge(key, count, Integer::sum));
    }

    private static void mergeCounts(Map<String, Integer> map, Map<String, Integer> otherMap) {
        otherMap.forEach((key, count) -> map.merge(key, count, Integer::sum));
    }

    private static Map<String, Integer> getChromosomeLengthsMap(VariantFileHeader header) {
        return header.getComplexLines()
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
    }
}
