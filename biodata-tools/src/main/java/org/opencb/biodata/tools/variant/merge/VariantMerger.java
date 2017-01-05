/**
 *
 */
package org.opencb.biodata.tools.variant.merge;

import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
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
 */
public class VariantMerger {

    @Deprecated
    public static final String VCF_FILTER = VCFConstants.GENOTYPE_FILTER_KEY;

    public static final String GENOTYPE_FILTER_KEY = VCFConstants.GENOTYPE_FILTER_KEY;

    public static final String GT_KEY = VCFConstants.GENOTYPE_KEY;
    public static final String PASS_VALUE = "PASS";
    public static final String DEFAULT_FILTER_VALUE = ".";
    public static final String DEFAULT_MISSING_GT = Genotype.NOCALL;

    private final AtomicReference<String> gtKey = new AtomicReference<>();
    private final AtomicReference<String>  filterKey = new AtomicReference<>();
    private final AtomicReference<String>  annotationFilterKey = new AtomicReference<>();

    private final boolean collapseDeletions;
    private final Set<String> expectedSamples = new ConcurrentSkipListSet<>();
    private final Map<String, String> defaultValues = new ConcurrentHashMap<>();
    private final AtomicReference<String> studyId = new AtomicReference<>(null);

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

    /**
     * Adds Sample names to the sample name set.
     * Samples names are used to validate the completeness of a variant call.
     * If a sample is not seen in the merged variant, the sample will be added as the registered default value.
     * The default values are retrieved by {@link #getDefaultValue(String)} and set to {@link #DEFAULT_MISSING_GT} for GT_KEY.
     * @param sampleNames Collection of sample names.
     */
    public void addExpectedSamples(Collection<String> sampleNames) {
        this.expectedSamples.addAll(sampleNames);
    }

    /**
     * Calls {@link Set#clear()} before {@link #addExpectedSamples(Collection)}.
     * @param sampleNames Collection of Sample names.
     */
    public void setExpectedSamples(Collection<String> sampleNames) {
        this.expectedSamples.clear();
        this.addExpectedSamples(sampleNames);
    }

    public Set<String> getExpectedSamples(){
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
        this.filterKey.set(filterKey);
    }

    public String getAnnotationFilterKey() {
        return annotationFilterKey.get();
    }

    public void setAnnotationFilterKey(String annotationFilterKey) {
        updateDefaultKeys(this.annotationFilterKey.get(), annotationFilterKey);
        this.annotationFilterKey.set(annotationFilterKey);
    }

    /**
     * Create and returns a new Variant using the target as a
     * position template ONLY and merges the provided variants
     * for this position. <b> The target is not present in the
     * merged output!!!</b>
     *
     * @param template Template for position and study only
     * @param load Variants to merge for position
     * @return Variant new Variant object with merged information
     */
    public Variant mergeNew(Variant template,Collection<Variant> load){
        Variant current = createFromTemplate(template);
        merge(current,load);
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
     * @param current {@link Variant} to update.
     * @param load {@link Variant} to be merged.
     * @return Merged Variant object.
     */
    Variant merge(Variant current, Variant load){
        return merge(current, Collections.singleton(load));
    }

    /**
     * Merge a collection of variants into one variant.
     * @param current {@link Variant} to update with collection of variants. This object will be modified.
     * @param load {@link Variant} to be merged.
     * @return Modified {@link Variant} object (passed in as current.
     */
    public Variant merge(Variant current, Collection<Variant> load){
        isValidVariant(current);
        // Build alt list
        List<Pair<Variant, List<AlternateCoordinate>>> loadAlts =
                updateCollapseDeletions(current,
                    load.stream()
                    .map(v -> new MutablePair<>(v, buildAltList(v)))
                    .filter(p -> hasAnyOverlap(current, p.getLeft(), p.getRight()))
                ).collect(Collectors.toList());

        mergeVariants(current, loadAlts);
        fillMissingGt(current);
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
                    || current.getType().equals(VariantType.SNV)) {
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

    private void fillMissingGt(Variant variant){
        if (this.getExpectedSamples().isEmpty()) {
            return; // Nothing to do.
        }
        Set<String> missing = new HashSet<>(getExpectedSamples());
        StudyEntry study = getStudy(variant);
        missing.removeAll(study.getSamplesName());
        if (missing.isEmpty()) {
            return; // Nothing to do.
        }
        // Prepare one data template for all missing samples.
        Map<String, Integer> formatPositions = study.getFormatPositions();
        String[] dataList = new String[formatPositions.size()];
        Arrays.fill(dataList, StringUtils.EMPTY);
        dataList[formatPositions.get(getGtKey())] = getDefaultValue(getGtKey()); //set Missing GT
        if (formatPositions.containsKey(getFilterKey())) {
            dataList[formatPositions.get(getFilterKey())] = getDefaultValue(getFilterKey());
        }
        // Register template for all missing samples.
        missing.forEach(sample -> study.addSampleData(sample, new ArrayList<String>(Arrays.asList(dataList))));
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
        StudyEntry se = v.getStudies().get(0);
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
    void mergeVariants(Variant current, List<Pair<Variant, List<AlternateCoordinate>>> varToAlts) {
        StudyEntry currentStudy = getStudy(current);
        String defaultFilterValue = currentStudy.getFiles().isEmpty() ? getDefaultValue(getFilterKey())
                : currentStudy.getFiles().get(0).getAttributes().getOrDefault(getAnnotationFilterKey(), getDefaultValue(getFilterKey()));
        ensureFormat(currentStudy, getFilterKey(), defaultFilterValue);

        final List<String> orderedSamplesName = new ArrayList<>(currentStudy.getOrderedSamplesName());
        final Set<String> currSampleNames = new HashSet<>(currentStudy.getSamplesName());

        // Build ALT index
        List<AlternateCoordinate> altList = buildAltsList(current, varToAlts.stream().map(Pair::getRight).collect(Collectors.toList()));
        Map<AlternateCoordinate, Integer> altIdx = index(altList);

        // Update SecALt list
        currentStudy.setSecondaryAlternates(altList.subList(1,altList.size()));
        final Map<String, Integer> formatPositions = new HashMap<>(currentStudy.getFormatPositions());
        final Map<String, Integer> additionalFormats = new HashMap<>(formatPositions);
        additionalFormats.remove(getGtKey());
        additionalFormats.remove(getFilterKey());

        final Map<String, String> sampleToGt;
        if (formatPositions.keySet().contains(getGtKey())) {
            if (!(formatPositions.get(getGtKey()).equals(0))) {
                throw new IllegalStateException("Current study expected to be in order of 'GT'");
            }
            sampleToGt = sampleToGt(current);
        } else {
            sampleToGt  = null;
        }
        final Map<String, String> sampleToFilter = sampleToSampleData(current, getFilterKey());
        final Map<String, Map<Integer, String>> sampleToAdditional = sampleToAdditionalData(additionalFormats, current);

        varToAlts.forEach(e -> {
            Variant other = e.getKey();
            Map<Integer, AlternateCoordinate> otherAltIdx = index(e.getValue()).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            final StudyEntry otherStudy = getStudy(other);
            final Map<String, String> otherSampleToGt = sampleToGt(other);
            final Map<String, String> otherSampleToFilter = otherStudy.getFormat().contains(getFilterKey())
                    ? sampleToSampleData(other, getFilterKey())
                    : sampleToAttribute(other, getAnnotationFilterKey());

            final Map<String, Map<Integer, String>> otherSampleToAdditionalFormats = sampleToAdditionalData(additionalFormats, other);

            checkForDuplicates(current, other, currentStudy, otherStudy, e.getValue());

            // Add GT data for each sample to current Variant
            otherStudy.getOrderedSamplesName().forEach(sampleName -> {
                boolean alreadyInStudy = currSampleNames.contains(sampleName);
                if (!alreadyInStudy) {
                    orderedSamplesName.add(sampleName);
                    currSampleNames.add(sampleName);
                }

                // GT data
                boolean isGtUpdated = false;
                List<Integer> updatedGtPositions = Collections.emptyList();
                if (sampleToGt != null) {
                    String gt = otherSampleToGt.get(sampleName);
                    if (StringUtils.isBlank(gt)) {
                        throw new IllegalStateException(String.format(
                            "No GT [%s] found for sample %s in \nVariant: %s\nIndexOther:%s\nIndex:%s\nOtherSe:%s\nOtherSp:%s",
                            getGtKey(), sampleName, other.getImpl(), otherSampleToGt, sampleToGt, otherStudy.getSamplesData(),
                            otherStudy.getSamplesPosition()));
                    }
                    String updatedGt = updateGT(gt, altIdx, otherAltIdx);
                    if (alreadyInStudy) {
                        String currGT = sampleToGt.get(sampleName);
                        List<String> gtlst = new ArrayList<>(Arrays.asList(currGT.split(",")));
                        if (!gtlst.contains(updatedGt)) {
                            gtlst.add(updatedGt);
                            updatedGtPositions = collapseGT(gtlst);
                            updatedGt = StringUtils.join(updatedGtPositions.stream()
                                    .map(p -> gtlst.get(p)).collect(Collectors.toList()), ',');
                            isGtUpdated = true;
                        }
                        sampleToGt.put(sampleName, updatedGt);
                    }
                    sampleToGt.put(sampleName, updatedGt);
                }

                // Filter
                String filter = otherSampleToFilter.getOrDefault(sampleName, getDefaultValue(getFilterKey()));
                if (alreadyInStudy && isGtUpdated) {
                    String currFilter = sampleToFilter.get(sampleName);
                    List<String> filterLst = new ArrayList<>(Arrays.asList(currFilter.split(",")));
                    filterLst.add(filter);
                    if (Objects.isNull(sampleToGt)) {
                        filter = StringUtils.join(filterLst, ',');
                    } else {
                        filter = StringUtils.join(updatedGtPositions.stream()
                                .map(p -> filterLst.get(p)).collect(Collectors.toList()), ',');
                    }
                }
                sampleToFilter.put(sampleName, filter);

                // Additional data
                Map<Integer, String> otherAdditional = otherSampleToAdditionalFormats.get(sampleName);
                if (otherAdditional != null) {
                    if (!sampleToAdditional.containsKey(sampleName)) {
                        sampleToAdditional.put(sampleName, otherAdditional);
                    } else {
                        throw new IllegalStateException("TODO - merge additional formats!!!");
                    }
                }
            });
            mergeFiles(current, other, currentStudy, otherStudy);
        });
        updateStudy(currentStudy, sampleToGt, sampleToFilter, sampleToAdditional, orderedSamplesName);
    }

    /**
     * Update study with Annotations.
     * @param study
     * @param sampleToGt
     * @param sampleToFilter
     * @param sampleToAdditional
     * @param orderedSamplesName
     */
    private void updateStudy(StudyEntry study, Map<String, String> sampleToGt, Map<String, String> sampleToFilter,
            Map<String, Map<Integer, String>> sampleToAdditional, List<String> orderedSamplesName) {

        // Build empty sample data.
        List<String> format = study.getFormat();
        String[] formatTemplate = new String[format.size()];
        Arrays.fill(formatTemplate, StringUtils.EMPTY);

        // Build samples position
        LinkedHashMap<String, Integer> samplesPosition = new LinkedHashMap<>();
        int sampleSize = orderedSamplesName.size();
        for (int i = 0; i < sampleSize; i++) {
            samplesPosition.put(orderedSamplesName.get(i), i);
        }

        // Init samples data with empty string.
        List<List<String>> samplesData = new ArrayList<>(sampleSize);
        for (int i = 0; i < sampleSize; ++i) {
            samplesData.add(new ArrayList<>(Arrays.asList(formatTemplate)));
        }

        // Add genotypes, filer
        for (int pos = 0; pos < format.size(); pos++) {
            String currFormat = format.get(pos);
            if (StringUtils.equals(currFormat, getGtKey())) {
                for (Map.Entry<String, String> entry : sampleToGt.entrySet()) {
                    samplesData.get(samplesPosition.get(entry.getKey())).set(pos, entry.getValue());
                }
            } else if (StringUtils.equals(currFormat, getFilterKey())) {
                for (Map.Entry<String, String> entry : sampleToFilter.entrySet()) {
                    samplesData.get(samplesPosition.get(entry.getKey())).set(pos, entry.getValue());
                }
            }
        }
        // and additional values
        sampleToAdditional.forEach((sample, m) -> m.forEach((pos, val) -> samplesData.get(samplesPosition.get(sample)).set(pos, val)));

//        for (int i = 0; i < samplesData.size(); i++) {
//            if (null == samplesData.get(i)) {
//                throw new IllegalStateException("Position " + i + " of " + samplesData.size() + " not filled!!!");
//            }
//        }
        study.setSamplesPosition(samplesPosition);
        study.setSamplesData(samplesData);
    }

    private <T> List<Integer> getMatchingPositions(List<T> list, Predicate<T> p){
        List<Integer> matching = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (p.test(list.get(i))) {
                matching.add(Integer.valueOf(i));
            }
        }
        return matching;
    }

    /**
     * Collapses a list of GT to a minimal set.
     * @param gtlst
     * @return
     */
    private List<Integer> collapseGT(List<String> gtlst) {
        if (gtlst.isEmpty()) {
            return Collections.emptyList();
        }
        if (gtlst.size() == 1) {
            return Collections.singletonList(0);
        }

        // only get GT with an ALT e.g 0/1 0/2 1/2 etc. (ignore ./. and 0/0 GT)
        Predicate<String> findAlts = gt -> Arrays.stream(new Genotype(gt).getAllelesIdx()).anyMatch(i -> i > 0);
        Predicate<String> findHomRef = gt -> gt.equals(Genotype.HOM_REF);
        Predicate<String> findOneRef = gt -> Arrays.stream(new Genotype(gt).getAllelesIdx()).anyMatch(i -> i == 0);
        Predicate<String> findNoCalls = gt -> Arrays.stream(new Genotype(gt).getAllelesIdx()).anyMatch(i -> i < 0);

        List<Integer> oneAltAllele = getMatchingPositions(gtlst, findAlts);
        if (!oneAltAllele.isEmpty()) {
            return oneAltAllele;
        }
        List<Integer> reference = getMatchingPositions(gtlst, findHomRef);
        if (!reference.isEmpty()) {
            return reference;
        }

        List<Integer> oneReferenceAllele = getMatchingPositions(gtlst, findOneRef);
        if (!oneReferenceAllele.isEmpty()) {
            return oneReferenceAllele;
        }
        // only no-calls left -> try to collapse
        List<Integer> nocalls = getMatchingPositions(gtlst, findNoCalls);
        if (nocalls.size() == gtlst.size()) { // all GT found
            return Collections.singletonList(nocalls.get(0));
        }
        // don't know that could be left!!!
        if (this.collapseDeletions) {
            throw new IllegalStateException("Not able to resolve GT: " + StringUtils.join(gtlst, ","));
        }
        return IntStream.range(0, gtlst.size()-1).boxed().collect(Collectors.toList());
    }

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
                    gto.updateAlleleIdx(i, allele);
                });
        if (!gto.isPhased()) {
            Arrays.sort(idx);
        }
        return gto.toGenotypeString();
    }

    private List<AlternateCoordinate> buildAltsList (Variant current, Collection<List<AlternateCoordinate>> alts) {
        Integer start = current.getStart();
        Integer end = current.getEnd();
        final List<AlternateCoordinate> currAlts = buildAltList(current);
        final Set<AlternateCoordinate> altSets = new HashSet<>(currAlts);
        if (this.collapseDeletions && isDeletion(current.getType(), current.getStart(), current.getEnd())) {
            // remove all alts that are NOT fully overlap current deletion -> keep only larger or same
            alts.forEach(l -> l.stream().filter(a -> (start >= a.getStart() && end <= a.getEnd())).forEach(a -> altSets.add(a)));
        } else if (this.collapseDeletions && isInsertion(current.getType(), current.getStart(), current.getEnd())) {
            // remove all alts that are NOT fully overlap current deletion -> keep only larger or same
            alts.forEach(l -> l.stream().filter(a -> {
                        if (isInsertion(a)) {
                            return (start.equals(a.getStart())
                                    && end.equals(a.getEnd())
                                    && (a.getAlternate().equals("*")
                                      || a.getAlternate().length() >= current.getAlternate().length())
                            );
                        }
                        return true;
                    }).forEach(a -> altSets.add(a)));
        } else if (this.collapseDeletions && current.getType().equals(VariantType.SNP)) {
            alts.forEach(l -> l.stream()
                    .filter(a ->current.overlapWith(a.getChromosome(), a.getStart(), a.getEnd(), true))
                    .forEach(a -> altSets.add(a)));
        } else {
            alts.forEach(l -> altSets.addAll(l));
        }
        // remove current alts
        altSets.removeAll(currAlts);
        currAlts.addAll(altSets);
        return currAlts;
    }

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
            Map<String, String> currSampleToGts = sampleToGt(current);
            List<AlternateCoordinate> currAlts = new ArrayList<>();
            currAlts.add(getMainAlternate(current));
            currAlts.addAll(currentStudy.getSecondaryAlternates());
            for (String dupSample : duplicateSamples) {
                String currGt = currSampleToGts.get(dupSample);
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
        return getStart(alt2).equals(getStart(alt1)) && getEnd(alt2).equals(getEnd(alt1))
                && getReference(alt2).equals(getReference(alt1))
                && getAlternate(alt2).equals(getAlternate(alt1));
    }

    private String getReference(AlternateCoordinate s) {
        return s.getReference();
    }

    private String getAlternate(AlternateCoordinate s) {
        return s.getAlternate();
    }

    private Integer getStart(AlternateCoordinate s) {
        return s.getStart();
    }

    private Integer getEnd(AlternateCoordinate s) {
        return s.getEnd();
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

    public static AlternateCoordinate getMainAlternate(Variant variant) {
        return new AlternateCoordinate(variant.getChromosome(), variant.getStart(), variant.getEnd(),
                variant.getReference(), variant.getAlternate(), variant.getType());
    }

    private void mergeFiles(Variant current, Variant other, StudyEntry currentStudy, StudyEntry otherStudy) {
        String call = other.getStart() + ":" + other.getReference() + ":" + other.getAlternate() + ":0";

        List<FileEntry> files = otherStudy.getFiles().stream().map(fileEntry -> FileEntry.newBuilder(fileEntry).build()).collect(Collectors.toList());
        if (!current.toString().equals(other.toString())) {
            for (FileEntry file : files) {
                if (file.getCall() == null || file.getCall().isEmpty()) {
                    file.setCall(call);
                }
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

    private Map<String, String> sampleToAttribute(Variant var, String key) {
        StudyEntry se = getStudy(var);
        String value = se.getFiles().get(0).getAttributes().getOrDefault(key, "");
        if (StringUtils.isBlank(value)) {
            return Collections.emptyMap();
        }
        return se.getSamplesName().stream().collect(Collectors.toMap(e -> e, e -> value));
    }

    private Map<String, String> sampleToGt(Variant load) {
        return sampleToSampleData(load, getGtKey());
    }

    private Map<String, Map<Integer, String>> sampleToAdditionalData(Map<String, Integer> additionalFormat, Variant var) {
        Map<String, Map<Integer, String>> sampleToAdditional = new HashMap<>();
        additionalFormat.forEach((key, idx) -> {
            Map<String, String> sampleToValue = sampleToSampleData(var, key);
            sampleToValue.forEach((sample, value) -> {
                Map<Integer, String> map = sampleToAdditional.get(sample);
                if (map == null) {
                    map = new HashMap<>();
                    sampleToAdditional.put(sample, map);
                }
                map.put(idx, value);
            });
        });
        return sampleToAdditional;
    }

    private Map<String, String> sampleToSampleData(Variant var, String key){
        Map<String, String> retMap = new HashMap<>();
        StudyEntry se = getStudy(var);
        LinkedHashMap<String, Integer> samplesPosition = se.getSamplesPosition();
        List<List<String>> samplesData = se.getSamplesData();
        if (Objects.isNull(samplesData)) {
            throw new IllegalStateException("No samplesData retrieved: " + var.getImpl());
        }
        Map<String, Integer> formatPositions = se.getFormatPositions();
        Integer formatPosition = formatPositions.get(key);
        if (Objects.isNull(formatPosition)) {
            throw new IllegalStateException("No format position registered for " + key + " in " + formatPositions + " in " + var.getImpl());
        }
        samplesPosition.forEach((s,samplePosition) -> {
            List<String> values = samplesData.get(samplePosition);
            if (Objects.isNull(values)) {
                throw new IllegalStateException("No values for sample " + s + " at position " + samplePosition + " in " + samplesData);
            }
            String val = values.get(formatPosition);
            if (StringUtils.isNotBlank(val)) {
                retMap.put(s, val);
            }
        });
        return retMap;
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
