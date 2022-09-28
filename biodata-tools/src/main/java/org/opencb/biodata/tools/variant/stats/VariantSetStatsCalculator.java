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

import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.models.variant.metadata.VariantSetStats;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
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
    private Set<String> files;
    private Set<String> samples;
    private List<Integer> samplePositions;
    private long sampleCount;
    private long filesCount;
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
        this(metadata, null);
    }

    /**
     * Calculate statistics for a set of files from the whole study
     * @param metadata VariantStudyMetadata
     * @param files    files
     */
    public VariantSetStatsCalculator(VariantStudyMetadata metadata, Collection<String> files) {
        this(metadata, files, null);
    }

    /**
     * Calculate statistics for a set of files and samples from the whole study
     * @param metadata VariantStudyMetadata
     * @param files    files
     * @param samples  samples
     */
    public VariantSetStatsCalculator(VariantStudyMetadata metadata, final Collection<String> files, final Collection<String> samples) {
        this.studyId = metadata.getId();
        if (files == null) {
            this.files = null;
            this.filesCount = metadata.getFiles().size();
            samplePositions = null;
            sampleCount = metadata.getFiles()
                    .stream()
                    .flatMap(fileMetadata -> fileMetadata.getSampleIds().stream())
                    .collect(Collectors.toSet()).size();
        } else {
            this.files = new HashSet<>(files);
            this.filesCount = files.size();
        }
        if (samples == null) {
            if (files == null) {
                this.samples = null;
            } else {
                this.samples = new HashSet<>();
                sampleCount = 0;
                for (org.opencb.biodata.models.variant.metadata.VariantFileMetadata fileMetadata : metadata.getFiles()) {
                    if (files.contains(fileMetadata.getId())) {
                        for (String s : fileMetadata.getSampleIds()) {
                            this.samples.add(s);
                            sampleCount++;
                        }
                    }
                }
            }
        } else {
            this.samples = new HashSet<>(samples);
            sampleCount = this.samples.size();
        }
        chrLengthMap = getChromosomeLengthsMap(metadata.getAggregatedHeader());
        stats = new VariantSetStats(
                0L,
                0L,
                new HashMap<>(),
                new HashMap<>(),
                0L,
                0f,
                0f,
                0f,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        );
    }

    /**
     * Calculates VariantSetStats for a file.
     * @param studyId       StudyId
     * @param fileMetadata  VariantFileMetadata
     */
    public VariantSetStatsCalculator(String studyId, VariantFileMetadata fileMetadata) {
        this(studyId, fileMetadata.getSampleIds().size(),
                getChromosomeLengthsMap(fileMetadata.getHeader()));
        fileMetadata.setStats(stats);
    }

    public VariantSetStatsCalculator(String studyId, int sampleCount, Map<String, Long> chrLengthMap) {
        this(studyId, 1, sampleCount, chrLengthMap);
    }

    public VariantSetStatsCalculator(String studyId, int filesCount, int sampleCount, Map<String, Long> chrLengthMap) {
        this(studyId, null, null, filesCount, sampleCount, chrLengthMap);
    }

    public VariantSetStatsCalculator(String studyId, Collection<String> files, Collection<String> samples, Map<String, Long> chrLengthMap) {
        this(studyId, files, samples, files.size(), samples.size(), chrLengthMap);
    }

    protected VariantSetStatsCalculator(String studyId, Collection<String> files, Collection<String> samples,
                                        int filesCount, int sampleCount, Map<String, Long> chrLengthMap) {
        this.studyId = studyId;
        this.files = files == null ? null : new HashSet<>(files);
        this.samples = samples == null ? null : new HashSet<>(samples);
        this.sampleCount = sampleCount;
        this.filesCount = filesCount;
        this.chrLengthMap = chrLengthMap == null ? Collections.emptyMap() : chrLengthMap;
        stats = new VariantSetStats(
                0L,
                0L,
                new HashMap<>(),
                new HashMap<>(),
                0L,
                0f,
                0f,
                0f,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        );
    }

    @Override
    public void pre() {
    }

    @Override
    public synchronized List<Variant> apply(List<Variant> batch) {
        for (Variant variant : batch) {
            updateFileEntries(variant);
        }
        return batch;
    }

    private void updateFileEntries(Variant variant) {
        StudyEntry study = variant.getStudy(studyId);
        if (study == null) {
            return;
        }
        int numFiles = updateFileEntries(study.getFiles());
        boolean validVariant = numFiles != 0;
        if (validVariant) {
            updateSampleEntries(study);
            stats.setVariantCount(stats.getVariantCount() + 1);
            stats.getChromosomeCount().merge(variant.getChromosome(), 1L, Long::sum);
            stats.getTypeCount().merge(variant.getType().toString(), 1L, Long::sum);
            if (VariantStats.isTransition(variant.getReference(), variant.getAlternate())) {
                transitionsCount++;
            }
            if (VariantStats.isTransversion(variant.getReference(), variant.getAlternate())) {
                transversionsCount++;
            }
            updateAnnotation(variant.getAnnotation());
        }
    }

    private int updateFileEntries(List<FileEntry> files) {
        Iterator<FileEntry> fileEntries;
        if (this.files == null) {
            fileEntries = files.iterator();
        } else {
            fileEntries = files.stream().filter(file -> this.files.contains(file.getFileId())).iterator();
        }
        int numFiles = 0;
        while (fileEntries.hasNext()) {
            FileEntry file = fileEntries.next();
            Map<String, String> fileData = file.getData();
            if (fileData.containsKey(StudyEntry.QUAL) && !(".").equals(fileData.get(StudyEntry.QUAL))) {
                float qual = Float.parseFloat(fileData.get(StudyEntry.QUAL));
                qualCount++;
                qualSum += qual;
                qualSumSq += qual * qual;
            }
            String filter = fileData.get(StudyEntry.FILTER);
            if (filter != null && !filter.isEmpty()) {
                for (String f : filter.split(";")) {
                    stats.getFilterCount().merge(f, 1L, Long::sum);
                }
            }
            numFiles++;
        }
        return numFiles;
    }

    private void updateSampleEntries(StudyEntry studyEntry) {
        List<SampleEntry> samples = studyEntry.getSamples();
        Integer gtIdx = studyEntry.getSampleDataKeyPosition(VCFConstants.GENOTYPE_KEY);
        if (gtIdx == null) {
            stats.getGenotypeCount().merge(Genotype.NA, sampleCount, Long::sum);
        } else {
            Iterator<SampleEntry> sampleEntries;
            if (this.samples == null) {
                sampleEntries = samples.iterator();
            } else {
                if (samplePositions == null) {
                    samplePositions = getFilteredSamplePositions(studyEntry.getSamplesPosition());
                }
                sampleEntries = samplePositions.stream().map(samples::get).iterator();
            }

            while (sampleEntries.hasNext()) {
                SampleEntry sampleEntry = sampleEntries.next();
                String gt = sampleEntry.getData().get(gtIdx);
                stats.getGenotypeCount().merge(gt, 1L, Long::sum);
            }
        }
    }

    private synchronized List<Integer> getFilteredSamplePositions(LinkedHashMap<String, Integer> samplesFromStudyEntry) {
        List<Integer> samplePositions = new ArrayList<>(this.samples.size());
        for (String sample : samples) {
            samplePositions.add(samplesFromStudyEntry.get(sample));
        }
        return samplePositions;
    }

    private void updateAnnotation(VariantAnnotation annotation) {
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
        stats.setFilesCount(filesCount);
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
