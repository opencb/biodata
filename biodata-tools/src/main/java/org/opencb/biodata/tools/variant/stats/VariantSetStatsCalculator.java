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

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.metadata.*;
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
    private final long sampleCount;
    private final Map<String, Long> chrLengthMap;
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
        sampleCount = metadata.getFiles()
                .stream()
                .flatMap(fileMetadata -> fileMetadata.getSampleIds().stream())
                .collect(Collectors.toSet()).size();
        chrLengthMap = getChromosomeLengthsMap(metadata.getAggregatedHeader());
        stats = new VariantSetStats(
                0L,
                0L,
                new HashMap<String, Long>(),
                0L,
                0f,
                0f,
                0f,
                new HashMap<String, Long>(),
                new HashMap<String, Long>(),
                new HashMap<String, Long>(),
                new HashMap<String, Long>(),
                new HashMap<String, Float>()
        );
        if (metadata.getStats() == null) {
            metadata.setStats(new VariantStudyStats(new HashMap<>(), new HashMap<>()));
        }
        if (metadata.getStats().getCohortStats() == null) {
            metadata.getStats().setCohortStats(new HashMap<>());
        }
        metadata.getStats().getCohortStats().put(StudyEntry.DEFAULT_COHORT, stats);
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

    public VariantSetStatsCalculator(String studyId, Set<String> files, int sampleCount, Map<String, Long> chrLengthMap) {
        this.studyId = studyId;
        this.files = files;
        this.sampleCount = sampleCount;
        this.chrLengthMap = chrLengthMap == null ? Collections.emptyMap() : chrLengthMap;
        stats = new VariantSetStats(
                0L,
                0L,
                new HashMap<String, Long>(),
                0L,
                0f,
                0f,
                0f,
                new HashMap<String, Long>(),
                new HashMap<String, Long>(),
                new HashMap<String, Long>(),
                new HashMap<String, Long>(),
                new HashMap<String, Float>()
        );
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
            stats.setVariantCount(stats.getVariantCount() + 1);
            stats.getChromosomeCount().merge(variant.getChromosome(), 1L, Long::sum);
            stats.getTypeCount().merge(variant.getType().toString(), 1L, Long::sum);
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
        for (FileEntry file : files) {
            Map<String, String> fileData = file.getData();

            if (fileData.containsKey(StudyEntry.QUAL) && !(".").equals(fileData.get(StudyEntry.QUAL))) {
                float qual = Float.parseFloat(fileData.get(StudyEntry.QUAL));
                qualCount++;
                qualSum += qual;
                qualSumSq += qual * qual;
            }
            String filter = fileData.get(StudyEntry.FILTER);
            if (filter == null || filter.isEmpty()) {

            } else {
                for (String f : filter.split(";")) {
                    stats.getFilterCount().merge(f, 1L, Long::sum);
                }
            }
        }
    }

    private void updateVariantSetStats(VariantAnnotation annotation) {
        if (annotation != null) {
            for (ConsequenceType consequenceType : annotation.getConsequenceTypes()) {
                String biotype = consequenceType.getBiotype();
                if (StringUtils.isNotEmpty(biotype)) {
                    stats.getBiotypeCount().merge(biotype, 1L, Long::sum);
                }
                if (consequenceType.getSequenceOntologyTerms() != null) {
                    for (SequenceOntologyTerm term : consequenceType.getSequenceOntologyTerms()) {
                        stats.getConsequenceTypeCount().merge(term.getName(), 1L, Long::sum);
                    }
                }
            }
        }
    }

    @Override
    public synchronized void post() {
        stats.setSampleCount(sampleCount);
        if (files != null) {
            stats.setFilesCount((long) files.size());
        }
        float qualityAvg = (float) (qualSum / qualCount);
        stats.setQualityAvg(qualityAvg);
        //Var = SumSq / n - mean * mean
        stats.setQualityStdDev((float) Math.sqrt(qualSumSq / qualCount - qualityAvg * qualityAvg));
        stats.setTiTvRatio(((float) transitionsCount) / ((float) transversionsCount));
        stats.getChromosomeCount().forEach((chr, count) -> {
            Long length = chrLengthMap.get(chr);
            if (length != null && length > 0) {
                stats.getChromosomeDensity().put(chr, count / (float) length);
            }
        });
    }

    public VariantSetStats getStats() {
        return stats;
    }

    public static void merge(VariantSetStats thisStats, VariantSetStats otherStats) {

        thisStats.setVariantCount(thisStats.getVariantCount() + otherStats.getVariantCount());
//        thisStats.setNumSamples(thisStats.getNumSamples() + otherStats.getNumSamples());
//        thisStats.setNumPass(thisStats.getNumPass() + otherStats.getNumPass());
//        thisStats.setTiTvRatio(thisStats.getTiTvRatio() + otherStats.getTiTvRatio());
//        thisStats.setMeanQuality(thisStats.getMeanQuality() + otherStats.getMeanQuality());
//        thisStats.setStdDevQuality(thisStats.getStdDevQuality() + otherStats.getStdDevQuality());

//        mergeCounts(thisStats.getNumRareVariants(), otherStats.getNumRareVariants());
        mergeCounts(thisStats.getTypeCount(), otherStats.getTypeCount());
        mergeCounts(thisStats.getFilterCount(), otherStats.getFilterCount());
        mergeCounts(thisStats.getBiotypeCount(), otherStats.getBiotypeCount());
        mergeCounts(thisStats.getConsequenceTypeCount(), otherStats.getConsequenceTypeCount());
        mergeCounts(thisStats.getChromosomeCount(), otherStats.getChromosomeCount());
    }

    private static void mergeCounts(Map<String, Long> map, Map<String, Long> otherMap) {
        otherMap.forEach((key, count) -> map.merge(key, count, Long::sum));
    }

    private static Map<String, Long> getChromosomeLengthsMap(VariantFileHeader header) {
        Map<String, Long> lengths = new HashMap<>();
        for (VariantFileHeaderComplexLine line : header.getComplexLines()) {
            if (line.getKey().equalsIgnoreCase("contig")) {
                String length = line.getGenericFields().get("length");
                String chr = Region.normalizeChromosome(line.getId());
                if (StringUtils.isNumeric(length)) {
                    lengths.put(chr, Long.valueOf(length));
                } else {
                    lengths.put(chr, -1L);
                }
            }
        }
        return lengths;
    }
}
