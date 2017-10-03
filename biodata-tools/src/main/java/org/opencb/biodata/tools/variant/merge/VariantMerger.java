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

/**
 *
 */
package org.opencb.biodata.tools.variant.merge;

import htsjdk.variant.vcf.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.tools.variant.VariantNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 * TODO: Make this class inmutable. Remove all Atomicreferences
 * TODO: Check for duplicated files
 */
public class VariantMerger {
    private final Logger logger = LoggerFactory.getLogger(VariantMerger.class);

    @Deprecated
    public static final String VCF_FILTER = VCFConstants.GENOTYPE_FILTER_KEY;

    public static final String GENOTYPE_FILTER_KEY = VCFConstants.GENOTYPE_FILTER_KEY;

    public static final String GT_KEY = VCFConstants.GENOTYPE_KEY;
    public static final String PASS_VALUE = "PASS";
    public static final String DEFAULT_FILTER_VALUE = ".";
    public static final String DEFAULT_MISSING_GT = Genotype.NOCALL;

    private final AtomicReference<String> gtKey = new AtomicReference<>();
    private final AtomicReference<String> filterKey = new AtomicReference<>();
    private final AtomicReference<String> annotationFilterKey = new AtomicReference<>();

    private final boolean collapseDeletions;
    private final LinkedHashMap<String, Integer> expectedSamplesPosition = new LinkedHashMap<>();
    private final Set<String> expectedSamples = expectedSamplesPosition.keySet();
    private final Map<String, Integer> expectedFormatsPosition = new LinkedHashMap<>();
    private final Map<String, String> defaultValues = new ConcurrentHashMap<>();
    private final AtomicReference<String> studyId = new AtomicReference<>(null);
    private final VariantAlternateRearranger.Configuration rearrangerConf = new VariantAlternateRearranger.Configuration();


    public VariantMerger() {
        this(false);
    }

    public VariantMerger(boolean collapseDeletions) {
        this.gtKey.set(GT_KEY);
        this.filterKey.set(GENOTYPE_FILTER_KEY);
        this.annotationFilterKey.set(StudyEntry.FILTER);

        setDefaultValue(getGtKey(), DEFAULT_MISSING_GT);
        setDefaultValue(getFilterKey(), DEFAULT_FILTER_VALUE);
        this.collapseDeletions = collapseDeletions;


    }

    public void setStudyId(String studyId) {
        this.studyId.set(studyId);
    }

    private boolean hasStudyId() {
        return this.studyId.get() != null;
    }

    private String getStudyId() {
        return studyId.get();
    }

    public VariantMerger setExpectedFormats(List<String> formats) {
        expectedFormatsPosition.clear();
        for (String format : formats) {
            if (format.equals(getGtKey()) && !expectedFormatsPosition.isEmpty()) {
                throw new IllegalArgumentException("Genotype format field [" + getGtKey() + "] must be in the first position!");
            }
            expectedFormatsPosition.put(format, expectedFormatsPosition.size());
        }
        return this;
    }

    /**
     * Adds Sample names to the sample name set.
     * Samples names are used to validate the completeness of a variant call.
     * If a sample is not seen in the merged variant, the sample will be added as the registered default value.
     * The default values are retrieved by {@link #getDefaultValue(String)} and set to {@link #DEFAULT_MISSING_GT} for GT_KEY.
     *
     * @param sampleNames Collection of sample names.
     */
    public void addExpectedSamples(Collection<String> sampleNames) {
        sampleNames.forEach(sample -> expectedSamplesPosition.putIfAbsent(sample, expectedSamplesPosition.size()));
    }

    /**
     * Calls {@link Set#clear()} before {@link #addExpectedSamples(Collection)}.
     *
     * @param sampleNames Collection of Sample names.
     */
    public void setExpectedSamples(Collection<String> sampleNames) {
        this.expectedSamplesPosition.clear();
        this.addExpectedSamples(sampleNames);
    }

    public Set<String> getExpectedSamples() {
        return Collections.unmodifiableSet(this.expectedSamples);
    }

    public String getGtKey() {
        return this.gtKey.get();
    }

    public void setGtKey(String gtKey) {
        updateDefaultKeys(this.gtKey.get(), gtKey);
        this.gtKey.set(gtKey);
    }


    /**
     * Update a key
     * @param from
     * @param to
     * @return true if there was a value to be moved.
     */
    public boolean updateDefaultKeys(String from, String to) {
        if (null == from) {
            return false;
        }
        if (null == to) {
            return false;
        }
        if (StringUtils.equals(from, to)) {
            return false;
        }
        String value = this.defaultValues.remove(from);
        if (null == value) {
            return false;
        }
        this.defaultValues.put(to, value);
        return true;
    }

    /**
     * Gets the default value of a key or {@link StringUtils#EMPTY} if no key is registered.
     * @param key Key
     * @return value Registered default value or {@link StringUtils#EMPTY}.
     */
    public String getDefaultValue(String key) {
        return this.defaultValues.getOrDefault(key, StringUtils.EMPTY);
    }

    public String getDefaultValue(String key, String valueIfNull) {
        return this.defaultValues.getOrDefault(key, valueIfNull);
    }

    public void setDefaultValue(String key, String value) {
        this.defaultValues.put(key, value);
    }

    public String getFilterKey() {
        return this.filterKey.get();
    }

    public void setFilterKey(String filterKey) {
        updateDefaultKeys(this.filterKey.get(), filterKey);
        this.filterKey.set(filterKey);
    }

    public String getAnnotationFilterKey() {
        return annotationFilterKey.get();
    }

    public void setAnnotationFilterKey(String annotationFilterKey) {
        updateDefaultKeys(this.annotationFilterKey.get(), annotationFilterKey);
        this.annotationFilterKey.set(annotationFilterKey);
    }

    public VariantMerger configure(VCFHeader header) {
        rearrangerConf.configure(header);
        return this;
    }

    public VariantMerger configure(Collection<? extends VCFHeaderLine> lines) {
        rearrangerConf.configure(lines);
        return this;
    }

    public VariantMerger configure(VCFCompoundHeaderLine line) {
        rearrangerConf.configure(line);
        return this;
    }

    public VariantMerger configure(String key, VCFHeaderLineCount number, VCFHeaderLineType type) {
        rearrangerConf.configure(key, number, type);
        return this;
    }

    public VariantMerger configure(VariantFileHeader header) {
        rearrangerConf.configure(header);
        return this;
    }

    /**
     * Create and returns a new Variant using the target as a
     * position template ONLY and merges the provided variants
     * for this position. <b> The target is not present in the
     * merged output!!!</b>
     *
     * @param template Template for position and study only
     * @param load     Variants to merge for position
     * @return Variant new Variant object with merged information
     */
    public Variant mergeNew(Variant template, Collection<Variant> load) {
        Variant current = createFromTemplate(template);
        merge(current, load);
        return current;
    }

    /**
     * Create an empty Variant (position, ref, alt) from a template with basic Study information without samples.
     * @param target Variant to take as a template
     * @return Variant filled with chromosome, start, end, ref, alt, study ID and format set to GT only, BUT no samples.
     */
    public Variant createFromTemplate(Variant target) {
        Variant var = new Variant(target.getChromosome(), target.getStart(), target.getEnd(), target.getReference(), target.getAlternate());
        var.setType(target.getType());
        for(StudyEntry tse : target.getStudies()){
            StudyEntry se = new StudyEntry(tse.getStudyId());
            se.setFiles(Collections.singletonList(new FileEntry("", "", new HashMap<>())));
            se.setFormat(Arrays.asList(getGtKey(), getFilterKey()));
            se.setSamplesPosition(new HashMap<>());
            se.setSamplesData(new ArrayList<>());
            var.addStudyEntry(se);
        }
        return var;
    }

    private void isValidVariant(Variant current) throws IllegalArgumentException{
        if (current.getType().equals(VariantType.NO_VARIATION)) {
            throw new IllegalStateException("Current variant can't be a NO_VARIANT");
        }

        // Validate variant information
//        ensureGtFormat(current);
        if (getStudy(current).getFormat() == null || getStudy(current).getFormat().isEmpty()) {
            throw new IllegalArgumentException("Format of sample data is empty!!!!!!");
        }
    }

    /**
     * Calls {@link #merge(Variant, Collection)}
     *
     * @param current {@link Variant} to update.
     * @param load    {@link Variant} to be merged.
     * @return Merged Variant object.
     */
    public Variant merge(Variant current, Variant load) {
        return merge(current, Collections.singleton(load));
    }

    /**
     * Merge a collection of variants into one variant.
     *
     * @param current {@link Variant} to update with collection of variants. This object will be modified.
     * @param load    {@link Variant} to be merged.
     * @return Modified {@link Variant} object (passed in as current.
     */
    public Variant merge(Variant current, Collection<Variant> load) {
        isValidVariant(current);
        // Build alt list
        List<Pair<Variant, List<AlternateCoordinate>>> loadAlts =
                updateCollapseDeletions(current,
                        load.stream()
                                .map(v -> new MutablePair<>(v, buildAltList(v)))
                                .filter(p -> hasAnyOverlap(current, p.getLeft(), p.getRight()))
                ).collect(Collectors.toList());

        mergeVariants(current, loadAlts);
        return current;
    }

    public static boolean isDeletion(AlternateCoordinate alt) {
        return isDeletion(alt.getType(), alt.getStart(), alt.getEnd());
    }

    public static boolean isDeletion(VariantType type, Integer start, Integer end) {
        if (type.equals(VariantType.DELETION)) {
            return true;
        }
        if (type.equals(VariantType.INDEL) && end >= start) {
            return true;
        }
        return false;
    }
    public static boolean isInsertion(AlternateCoordinate alt) {
        return isInsertion(alt.getType(), alt.getStart(), alt.getEnd());
    }

    public static boolean isInsertion(VariantType type, Integer start, Integer end) {
        if (type.equals(VariantType.INSERTION)) {
            return true;
        }
        if (type.equals(VariantType.INDEL) && end < start) {
            return true;
        }
        return false;
    }

    private Stream<MutablePair<Variant, List<AlternateCoordinate>>> updateCollapseDeletions(
            Variant current,
            Stream<MutablePair<Variant, List<AlternateCoordinate>>> stream) {
        if (this.collapseDeletions) {
            AlternateCoordinate currAlt = buildAltList(current).get(0);
            Integer start = current.getStart();
            Integer end = current.getEnd();
            Consumer<AlternateCoordinate> updateAlt = a -> {
                a.setStart(start);
                a.setEnd(end);
                a.setReference(current.getReference());
                a.setAlternate("*"); // set deletion to * Alternate
                a.setType(VariantType.MIXED); // set all to the same
            };
            if (current.getType().equals(VariantType.SNP)
                    || current.getType().equals(VariantType.SNV)
                    || current.getType().equals(VariantType.MNV)) {
                return stream.map(pair -> {
                    pair.getValue().stream()
                            .filter(a -> {
                                if (a.getType().equals(VariantType.INDEL)) {
                                    return current.overlapWith(a.getChromosome(), a.getStart(), a.getEnd(), true);
                                }
                                return false; // Not for other SNPs
                            })
                            .forEach(updateAlt);
                    return pair;
                });
            } else if (isDeletion(current.getType(), start, end)) {
                return stream.map(pair -> {
                    pair.getValue().stream()
                            .filter(a -> !a.equals(currAlt)) // not same as current variant
                            .filter(a -> start >= a.getStart() && end <= a.getEnd())
                            .forEach(updateAlt);
                    return pair;
                });
            } else if (isInsertion(current.getType(), start, end)) {
                return stream.map(pair -> {
                    pair.getValue().stream()
                            .filter(a -> !a.equals(currAlt)) // not same as current variant
                            .filter(a -> {
                                        if (isInsertion(a)) {
                                            return (start.equals(a.getStart())
                                                    && end.equals(a.getEnd())
                                                    && a.getAlternate().length() >= currAlt.getAlternate().length()
                                            );
                                        }
                                        return !a.getType().equals(VariantType.NO_VARIATION);
                                    }
                            ) // only longer insertions
                            .forEach(updateAlt);
                    return pair;
                });
            }
        }
        return stream;
    }

    public Variant merge(Variant current, List<Pair<Variant, List<AlternateCoordinate>>> vatToAlts) {
        isValidVariant(current);
        // Filter alt list
        List<Pair<Variant, List<AlternateCoordinate>>> loadAlts = vatToAlts.stream()
                .filter(p -> hasAnyOverlap(current, p.getLeft(), p.getRight()))
                .collect(Collectors.toList());
        mergeVariants(current, loadAlts);
        return current;
    }

    public static boolean hasAnyOverlap(Variant current, Variant other) {
        if (current.overlapWith(other, true)) {
            return true;
        }
        // SecAlt of query
        return other.getStudies().stream()
                .filter( s -> // foreach study
                        s.getSecondaryAlternates().stream()
                                .filter(a -> {
                                            // Avoid NPE
                                            a = copyAlt(other, a);
                                            return current.overlapWith(a.getChromosome(), a.getStart(), a.getEnd(), true);
                                        }
                                )
                                .findAny()
                                .isPresent()
                )
                .findAny()
                .isPresent();
    }

    public static boolean hasAnyOverlap(Variant current, Variant other, Collection<AlternateCoordinate> alts) {
        if (current.overlapWith(other, true)) {
            return true; // Important for REF region
        }
        return alts.stream().filter(a -> current.overlapWith(a.getChromosome(), a.getStart(), a.getEnd(), true))
                .findAny().isPresent();
    }

    private String variantToString(Variant v) {
        StringBuilder sb = new StringBuilder(v.getChromosome());
        sb.append(":").append(v.getStart()).append("-").append(v.getEnd());
        sb.append(v.getReference().isEmpty() ? "-" : v.getReference());
        sb.append(":").append(v.getAlternate().isEmpty() ? "-" : v.getAlternate()).append("[");
        StudyEntry se = getStudy(v);
        List<List<String>> sd = se.getSamplesData();
        for(String sn : se.getSamplesName()){
            Integer pos = se.getSamplesPosition().get(sn);
            if (pos >= sd.size()) {
                sb.append(sn).append(":S;");
            } else if (null == sd.get(pos) || sd.get(pos).size() < 1) {
                sb.append(sn).append(":G;");
            } else {
                String gt = sd.get(pos).get(0); // GT
                sb.append(sn).append(":").append(gt).append(";");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Merge variants into current object.
     * @param current Variant object - in/out
     * @param varToAlts List of Variant with matching ALTs (ALT and secondary ALTs)
     */
    private void mergeVariants(Variant current, List<Pair<Variant, List<AlternateCoordinate>>> varToAlts) {
        StudyEntry currentStudy = getStudy(current);
        String defaultFilterValue = currentStudy.getFiles().isEmpty() ? getDefaultValue(getFilterKey())
                : currentStudy.getFiles().get(0).getAttributes().getOrDefault(getAnnotationFilterKey(), getDefaultValue(getFilterKey()));
        ensureFormat(currentStudy, getFilterKey(), defaultFilterValue);

        // Build ALT index
        List<AlternateCoordinate> altList = buildAltsList(current, varToAlts.stream()
                .map(Pair::getRight).collect(Collectors.toList()));
//        Map<AlternateCoordinate, Integer> altIdx = index(altList);

        // Update SecALt list
        currentStudy.setSecondaryAlternates(altList.subList(1, altList.size()));

        // Find new formats
        final Map<String, Integer> newFormatPositions;
        final List<String> newFormat;
        if (expectedFormatsPosition.isEmpty()) {
            // Find all formats
            Set<String> allFormats = varToAlts.stream()
                    .map(Pair::getKey)
                    .map(this::getStudy)
                    .map(StudyEntry::getFormat)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            newFormatPositions = new HashMap<>(currentStudy.getFormatPositions());
            newFormatPositions.putIfAbsent(getFilterKey(), newFormatPositions.size());
            for (String format : allFormats) {
                newFormatPositions.putIfAbsent(format, newFormatPositions.size());
            }
            newFormat = Arrays.asList(new String[newFormatPositions.size()]);
            newFormatPositions.forEach((format, formatIdx) -> {
                newFormat.set(formatIdx, format);
            });
        } else {
            // TODO: Try to avoid this copy
            newFormatPositions = new HashMap<>(expectedFormatsPosition);
            newFormat = new ArrayList<>(expectedFormatsPosition.keySet());
        }
        Map<String, Integer> extraFormats = newFormatPositions.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(getGtKey()) && !e.getKey().equals(getFilterKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Find new samples
        LinkedHashMap<String, Integer> newSamplesPosition;
        if (expectedSamplesPosition.isEmpty()) {
            newSamplesPosition = new LinkedHashMap<>(currentStudy.getSamplesPosition());
            varToAlts.stream()
                    .map(Pair::getKey)
                    .map(this::getStudy)
                    .map(StudyEntry::getOrderedSamplesName)
                    .flatMap(List::stream)
                    .forEach(sample -> newSamplesPosition.putIfAbsent(sample, newSamplesPosition.size()));
        } else {
            // TODO: Try to avoid this copy
            newSamplesPosition = new LinkedHashMap<>(expectedSamplesPosition);
        }

        // Create new Samples data
        List<List<String>> newSamplesData = newSamplesData(newSamplesPosition.size(), newFormatPositions);
        boolean[] alreadyMergedSamples = new boolean[newSamplesPosition.size()];
        // Copy current samples data into new samples data
        List<List<String>> currentSamplesData = currentStudy.getSamplesData();
        int currentSampleIdx = 0;
        for (String sample : currentStudy.getOrderedSamplesName()) {
            List<String> currentSampleData = currentSamplesData.get(currentSampleIdx);
            Integer newSampleIdx = newSamplesPosition.get(sample);
            alreadyMergedSamples[newSampleIdx] = true;
            List<String> newSampleData = newSamplesData.get(newSampleIdx);
            int formatIdx = 0;
            for (String format : currentStudy.getFormat()) {
                Integer newFormatIdx = newFormatPositions.get(format);
                if (newFormatIdx != null) {
                    newSampleData.set(newFormatIdx, currentSampleData.get(formatIdx));
                }
                formatIdx++;
            }
            currentSampleIdx++;
        }

        Integer newGtIdx = newFormatPositions.get(getGtKey());
        Integer newFilterIdx = newFormatPositions.get(getFilterKey());

        for (Pair<Variant, List<AlternateCoordinate>> e : varToAlts) {
            Variant other = e.getKey();
            List<AlternateCoordinate> otherAlternates = e.getValue();


//            Map<Integer, AlternateCoordinate> otherAltIdx = index(alternates).entrySet().stream()
//                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            final StudyEntry otherStudy = getStudy(other);
            Map<String, Integer> otherStudyFormatPositions = otherStudy.getFormatPositions();
            checkForDuplicates(current, other, currentStudy, otherStudy, otherAlternates);

            VariantAlternateRearranger rearranger;
            // It may happen that the new list of alternates does not contains some of the other alternates.
            // In that case, use the rearranger
            if (altList.size() == 1 && altList.equals(otherAlternates)) {
                rearranger = null;
            } else {
                rearranger = new VariantAlternateRearranger(otherAlternates, altList, rearrangerConf);
            }
            // Add GT data for each sample to current Variant

            List<String> otherOrderedSamplesName = otherStudy.getOrderedSamplesName();
            for (int sampleIdx = 0; sampleIdx < otherOrderedSamplesName.size(); sampleIdx++) {
                String sampleName = otherOrderedSamplesName.get(sampleIdx);
                List<String> otherSampleData = otherStudy.getSamplesData().get(sampleIdx);
                Integer newSampleIdx = newSamplesPosition.get(sampleName);
                List<String> newSampleData = newSamplesData.get(newSampleIdx);

                boolean alreadyMergedSample = alreadyMergedSamples[newSampleIdx];
                alreadyMergedSamples[newSampleIdx] = true;

                // GT data
                int ploidy = -1;
                boolean isGtUpdated = false;
                List<Integer> updatedGtPositions = Collections.emptyList();
                if (newGtIdx != null) {
                    String gt = otherSampleData.get(otherStudyFormatPositions.get(getGtKey()));
                    if (StringUtils.isBlank(gt)) {
                        throw new IllegalStateException(String.format(
                                "No GT [%s] found for sample %s in \nVariant: %s\nOtherSe:%s\nOtherSp:%s",
                                getGtKey(), sampleName, other.getImpl(), otherStudy.getSamplesData(),
                                otherStudy.getSamplesPosition()));
                    }
                    String updatedGt;
                    Genotype genotype;
                    if (rearranger != null) {
                        genotype = rearranger.rearrangeGenotype(new Genotype(gt));
                    } else {
                        genotype = new Genotype(gt);
                    }
                    if (!genotype.isPhased()) {
                        genotype.normalizeAllelesIdx();
                    }
                    if (collapseDeletions) {
                        int[] allelesIdx = genotype.getAllelesIdx();
                        for (int i = 0; i < allelesIdx.length; i++) {
                            if (allelesIdx[i] < 0) {
                                allelesIdx[i] = 0; // change to '0' for 'missing' reference (missing because change to '0' GT)
                            }
                        }
                    }
                    updatedGt = genotype.toString();
//                    updatedGt = updateGT(gt, altIdx, otherAltIdx);
                    ploidy = genotype.getPloidy();
                    if (alreadyMergedSample) {
                        String currGT = newSampleData.get(newGtIdx);
                        List<String> gtlst;
                        if (currGT.contains(",")) {
                            gtlst = new ArrayList<>(Arrays.asList(currGT.split(",")));
                        } else {
                            gtlst = new ArrayList<>(1);
                            gtlst.add(currGT);
                        }
                        if (!gtlst.contains(updatedGt)) {
                            gtlst.add(updatedGt);
                            updatedGtPositions = collapseGT(gtlst);
                            updatedGt = StringUtils.join(updatedGtPositions.stream()
                                    .map(gtlst::get).collect(Collectors.toList()), ',');
                            isGtUpdated = true;
                        }
                    }
                    newSampleData.set(newGtIdx, updatedGt);
                }

                // Filter
                if (newFilterIdx != null) {
                    Integer otherFilterIdx = otherStudyFormatPositions.get(getFilterKey());
                    String filter;
                    if (otherFilterIdx != null) {
                        filter = otherSampleData.get(otherFilterIdx);
                    } else {
                        filter = otherStudy.getFiles().get(0).getAttributes()
                                .getOrDefault(StudyEntry.FILTER, getDefaultValue(getFilterKey()));
                    }

                    if (alreadyMergedSample && isGtUpdated) {
                        String currFilter = newSampleData.get(newFilterIdx);
                        if (currFilter != null && !currFilter.equals(getDefaultValue(getFilterKey()))) {
                            List<String> filterLst = new ArrayList<>(Arrays.asList(currFilter.split(",")));
                            filterLst.add(filter);
                            if (/*Objects.isNull(sampleToGt)*/updatedGtPositions.isEmpty()) {
                                filter = StringUtils.join(filterLst, ',');
                            } else {
                                filter = StringUtils.join(updatedGtPositions.stream()
                                        .map(filterLst::get).collect(Collectors.toList()), ',');
                            }
                        }
                    }
                    newSampleData.set(newFilterIdx, filter);
                }

                // Additional data
                for (Map.Entry<String, Integer> entry : extraFormats.entrySet()) {
                    Integer idx = otherStudyFormatPositions.get(entry.getKey());
                    String data = idx == null ? getDefaultValue(entry.getKey()) : otherSampleData.get(idx);
                    if (StringUtils.isNotEmpty(data)) {
                        if (rearranger != null) {
                            data = rearranger.rearrange(entry.getKey(), data, ploidy);
                        }
                        String old = newSampleData.set(entry.getValue(), data);
//                        if (old != null) {
//                            throw new IllegalStateException("TODO - merge additional formats!!!");
//                        }
                    }
                }

            }
            mergeFile(current, other, rearranger, currentStudy, otherStudy);
        }
        currentStudy.setSamplesData(newSamplesData);
        currentStudy.setSortedSamplesPosition(newSamplesPosition);
        currentStudy.setFormat(newFormat);
    }

    private List<List<String>> newSamplesData(int samplesSize, Map<String, Integer> formats) {
        List<List<String>> newSampleData;
        List<String> defaultSamplesData = Arrays.asList(new String[formats.size()]);
        formats.forEach((format, formatIdx) -> defaultSamplesData.set(formatIdx, getDefaultValue(format)));
        newSampleData = new ArrayList<>(samplesSize);
        for (int i = 0; i < samplesSize; i++) {
            newSampleData.add(new ArrayList<>(defaultSamplesData));
        }
        return newSampleData;
    }

    private <T> List<Integer> getMatchingPositions(List<T> list, Predicate<T> p){
        List<Integer> matching = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (p.test(list.get(i))) {
                matching.add(i);
            }
        }
        return matching;
    }

    /**
     * Collapses a list of GT to a minimal set.
     * @param gtsStr
     * @return
     */
    private List<Integer> collapseGT(List<String> gtsStr) {
        if (gtsStr.isEmpty()) {
            return Collections.emptyList();
        }
        if (gtsStr.size() == 1) {
            return Collections.singletonList(0);
        }

        List<Genotype> gts = gtsStr.stream().map(Genotype::new).collect(Collectors.toList());

        // only get GT with an ALT e.g 0/1 0/2 1/2 etc. (ignore ./. and 0/0 GT)
        Predicate<Genotype> findAlts = gt -> Arrays.stream(gt.getAllelesIdx()).anyMatch(i -> i > 0);
        Predicate<Genotype> findHomRef = gt -> Arrays.stream(gt.getAllelesIdx()).allMatch(i -> i == 0);
        Predicate<Genotype> findOneRef = gt -> Arrays.stream(gt.getAllelesIdx()).anyMatch(i -> i == 0);
        Predicate<Genotype> findNoCalls = gt -> Arrays.stream(gt.getAllelesIdx()).anyMatch(i -> i < 0);

        List<Integer> oneAltAllele = getMatchingPositions(gts, findAlts);
        if (!oneAltAllele.isEmpty()) {
            return oneAltAllele;
        }
        List<Integer> reference = getMatchingPositions(gts, findHomRef);
        if (!reference.isEmpty()) {
            return reference;
        }

        List<Integer> oneReferenceAllele = getMatchingPositions(gts, findOneRef);
        if (!oneReferenceAllele.isEmpty()) {
            return oneReferenceAllele;
        }
        // only no-calls left -> try to collapse
        List<Integer> nocalls = getMatchingPositions(gts, findNoCalls);
        if (nocalls.size() == gtsStr.size()) { // all GT found
            return Collections.singletonList(nocalls.get(0));
        }
        // don't know that could be left!!!
        if (this.collapseDeletions) {
            throw new IllegalStateException("Not able to resolve GT: " + StringUtils.join(gtsStr, ","));
        }
        return IntStream.range(0, gtsStr.size() - 1).boxed().collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link VariantAlternateRearranger#rearrangeGenotype}
     */
    @Deprecated
    private String updateGT(String gt, Map<AlternateCoordinate, Integer> curr, Map<Integer, AlternateCoordinate> other) {
        Genotype gto = new Genotype(gt);
        int[] idx = gto.getAllelesIdx();
        int len = idx.length;
        IntStream.range(0, len).boxed().filter(i -> idx[i] > 0 && idx[i] <= other.size())
                .forEach(i -> {
                    Integer allele = curr.get(other.get(idx[i]));
                    if (this.collapseDeletions && Objects.isNull(allele)) {
                        allele = 0; // change to '0' for 'missing' reference (missing because change to '0' GT)
                    }
                    gto.setAlleleIdx(i, allele);
                });
        if (!gto.isPhased()) {
            Arrays.sort(idx);
        }
        return gto.toString();
    }

    private List<AlternateCoordinate> buildAltsList(Variant current, Collection<List<AlternateCoordinate>> alts) {
        Integer start = current.getStart();
        Integer end = current.getEnd();
        final List<AlternateCoordinate> currAlts = buildAltList(current);
        final Set<AlternateCoordinate> altSets = new HashSet<>(currAlts);
        if (this.collapseDeletions && isDeletion(current.getType(), current.getStart(), current.getEnd())) {
            // remove all alts that are NOT fully overlap current deletion -> keep only larger or same
            alts.stream()
                    .flatMap(Collection::stream)
                    .filter(a -> (start >= a.getStart() && end <= a.getEnd()))
                    .forEach(altSets::add);
        } else if (this.collapseDeletions && isInsertion(current.getType(), current.getStart(), current.getEnd())) {
            // remove all alts that are NOT fully overlap current deletion -> keep only larger or same
            alts.stream()
                    .flatMap(Collection::stream)
                    .filter(a -> {
                        if (isInsertion(a)) {
                            return (start.equals(a.getStart())
                                    && end.equals(a.getEnd())
                                    && (a.getAlternate().equals("*")
                                    || a.getAlternate().length() >= current.getAlternate().length())
                            );
                        }
                        return true;
                    })
                    .forEach(altSets::add);
        } else if (this.collapseDeletions && current.getType().equals(VariantType.SNP)) {
            alts.forEach(l -> l.stream()
                    .filter(a -> current.overlapWith(a.getChromosome(), a.getStart(), a.getEnd(), true))
                    .forEach(altSets::add));
        } else {
            alts.forEach(altSets::addAll);
        }
        // remove current alts
        altSets.removeAll(currAlts);
        currAlts.addAll(altSets);
        return currAlts;
    }

    @Deprecated
    private Map<AlternateCoordinate, Integer> index(List<AlternateCoordinate> alts) {
        Map<AlternateCoordinate, Integer> altIdx = new HashMap<>();
        AtomicInteger pos = new AtomicInteger(1); // Start at 1 -> first Alt genotype
        for (AlternateCoordinate a : alts) {
            altIdx.put(a, pos.getAndIncrement());
        }
        return altIdx;
    }

    private boolean checkForDuplicates(Variant current, Variant other, StudyEntry currentStudy, StudyEntry otherStudy, List<AlternateCoordinate> otherAlts) {
        Set<String> duplicateSamples = otherStudy.getSamplesName().stream()
                .filter(s -> currentStudy.getSamplesName().contains(s))
                .collect(Collectors.toSet());
        if (!duplicateSamples.isEmpty()) {
            List<AlternateCoordinate> currAlts = new ArrayList<>();
            currAlts.add(getMainAlternate(current));
            currAlts.addAll(currentStudy.getSecondaryAlternates());
            for (String dupSample : duplicateSamples) {
                String currGt = getStudy(current).getSampleData(dupSample, getGtKey());
                Set<Integer> gtIdxSet = Arrays.stream(currGt.split(","))
                        .flatMap(e -> Arrays.stream(e.split("/")))
                        .flatMap(e -> Arrays.stream(e.split("\\|")))
                        .filter(g -> NumberUtils.isNumber(g))
                        .map(g -> Integer.valueOf(g))
                        .filter(g -> g > 0)
                        .collect(Collectors.toSet());
                // Find same alleles in current and other for this individual
                List<AlternateCoordinate> currConflict = gtIdxSet.stream()
                        .map(g -> currAlts.get(g - 1))
                        .filter(a -> otherAlts.stream().filter(o -> isSameVariant(a, o)).count() > 0)
                        .collect(Collectors.toList());
                // Only Throw Exception in case of duplicated Alt for same individual
                if (!currConflict.isEmpty()) {
                    StringBuilder sb = new StringBuilder("Duplicated entries during merging: Issue with ID ");
                    sb.append(dupSample).append("; Variants: ");
                    currConflict.forEach(a -> sb.append("\n").append(a));
                    sb.append(";");
                    throw new IllegalStateException(sb.toString());
                }
            }
        }
        return false;
    }


    protected void validateAlternate(AlternateCoordinate alt) {
        if (alt.getChromosome() == null) {
            throw new IllegalStateException("Chromosome of alt is null: " + alt);
        }
        if (alt.getStart() == null) {
            throw new IllegalStateException("Start of alt is null: " + alt);
        }
        if (alt.getEnd() == null) {
            throw new IllegalStateException("End of alt is null: " + alt);
        }
        if (alt.getReference() == null) {
            throw new IllegalStateException("Reference of alt is null: " + alt);
        }
        if (alt.getAlternate() == null) {
            throw new IllegalStateException("Alternate of alt is null: " + alt);
        }
    }

    protected boolean equals(AlternateCoordinate alt1, AlternateCoordinate alt2) {
        return alt2.getStart().equals(alt1.getStart())
                && alt2.getEnd().equals(alt1.getEnd())
                && alt2.getReference().equals(alt1.getReference())
                && alt2.getAlternate().equals(alt1.getAlternate());
    }


    /**
     * Build a list of all the alternates from a variant. Includes the main and the secondary alternates.
     * @param variant
     * @return
     */
    public List<AlternateCoordinate> buildAltList(Variant variant) {
        AlternateCoordinate mainAlternate = getMainAlternate(variant);
        List<AlternateCoordinate> alternates = new ArrayList<>();
        if (!mainAlternate.getType().equals(VariantType.NO_VARIATION)) {
            alternates.add(mainAlternate);
        }
        StudyEntry se = getStudy(variant);
        if(se.getSecondaryAlternates() != null){
            se.getSecondaryAlternates().forEach( alt -> alternates.add(copyAlt(variant, alt)));
        }
        return alternates;
    }

    private static AlternateCoordinate copyAlt(Variant var, AlternateCoordinate orig) {
        AlternateCoordinate copy = new AlternateCoordinate();
        copy.setChromosome(orig.getChromosome() == null ? var.getChromosome() : orig.getChromosome());
        copy.setStart(orig.getStart() == null ? var.getStart() : orig.getStart());
        copy.setEnd(orig.getEnd() == null ? var.getEnd() : orig.getEnd());
        copy.setReference(orig.getReference() == null ? var.getReference() : orig.getReference());
        copy.setAlternate(orig.getAlternate() == null ? var.getAlternate() : orig.getAlternate());
        copy.setType(orig.getType() == null ? var.getType() : orig.getType());
        return copy;
    }

    /**
     * Get the variant as Alternate Coordinate.
     *
     * At this point, we don't care if the Alternate is SNP or SNV.
     * In case that the variant is SV, recalculate the type, just in case the size has changed.
     *
     * @param variant Variant
     * @return Variant as AlternateCoordinate
     */
    public static AlternateCoordinate getMainAlternate(Variant variant) {
        VariantType type;
        switch (variant.getType()) {
            case SNP:
                type = VariantType.SNV;
                break;
            case MNP:
                type = VariantType.MNV;
                break;
            case SV:
                type = VariantBuilder.inferType(variant.getReference(), variant.getAlternate());
                break;
            default:
                type = variant.getType();

        }
        return new AlternateCoordinate(variant.getChromosome(), variant.getStart(), variant.getEnd(),
                variant.getReference(), variant.getAlternate(), type);
    }

    private void mergeFile(Variant current, Variant other, VariantAlternateRearranger rearranger,
                           StudyEntry currentStudy, StudyEntry otherStudy) {
        String call = other.getStart() + ":" + other.getReference() + ":" + other.getAlternate() + ":0";

        List<FileEntry> files = otherStudy.getFiles().stream()
                .map(fileEntry -> FileEntry.newBuilder(fileEntry).build())
                .collect(Collectors.toList());
        if (!current.toString().equals(other.toString())) {
            for (FileEntry file : files) {
                if (StringUtils.isEmpty(file.getCall())) {
                    file.setCall(call);
                }
            }
        }
        for (FileEntry file : files) {
            for (Map.Entry<String, String> entry : file.getAttributes().entrySet()) {
                String data = entry.getValue();
                if (rearranger != null) {
                    data = rearranger.rearrange(entry.getKey(), data);
                }
                entry.setValue(data);
            }
        }

        StudyEntry study = currentStudy;
        try {
            study.getFiles().addAll(files);
        } catch (UnsupportedOperationException e) {
            // If the files list was unmodifiable, clone the list and add.
            study.setFiles(new LinkedList<>(study.getFiles()));
            study.getFiles().addAll(files);
        }
    }

    StudyEntry getStudy(Variant variant) {
        if (hasStudyId()) {
            StudyEntry study = variant.getStudy(getStudyId());
            if (Objects.isNull(study)) {
                throw new IllegalStateException("No study found for " + getStudyId());
            }
            return study;
        }
        return variant.getStudies().get(0);
    }

    /**
     * Ensures that all the samples contains the required format value.
     * @param studyEntry
     * @param formatValue
     * @param defaultValue
     */
    private void ensureFormat(StudyEntry studyEntry, String formatValue, String defaultValue) {
        if (!studyEntry.getFormat().contains(formatValue)) {
            studyEntry.addFormat(formatValue);
            if (studyEntry.getSamplesData() != null && !studyEntry.getSamplesData().isEmpty()) {
                for (String sampleName : studyEntry.getOrderedSamplesName()) {
                    studyEntry.addSampleData(sampleName, getFilterKey(), defaultValue);
                }
            }
        }
    }

    static boolean onSameVariant (Variant a, Variant b){
        return a.onSameRegion(b)
                && StringUtils.equals(a.getReference(), b.getReference())
                && StringUtils.equals(a.getAlternate(), b.getAlternate());
    }

    public static boolean isSameVariant(Variant a, AlternateCoordinate b){
        return StringUtils.equals(a.getChromosome(), b.getChromosome())
                && a.getStart().equals(b.getStart())
                && a.getEnd().equals(b.getEnd())
                && StringUtils.equals(a.getReference(), b.getReference())
                && StringUtils.equals(a.getAlternate(), b.getAlternate());
    }

    public static boolean isSameVariant(AlternateCoordinate a, AlternateCoordinate b){
        return StringUtils.equals(a.getChromosome(), b.getChromosome())
                && a.getStart().equals(b.getStart())
                && a.getEnd().equals(b.getEnd())
                && StringUtils.equals(a.getReference(), b.getReference())
                && StringUtils.equals(a.getAlternate(), b.getAlternate());
    }
}
