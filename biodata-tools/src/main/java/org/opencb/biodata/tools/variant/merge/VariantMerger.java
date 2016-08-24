/**
 *
 */
package org.opencb.biodata.tools.variant.merge;

import htsjdk.variant.vcf.VCFConstants;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 */
public class VariantMerger {

    @Deprecated
    public static final String VCF_FILTER = VCFConstants.GENOTYPE_FILTER_KEY;

    public static final String GENOTYPE_FILTER_KEY = VCFConstants.GENOTYPE_FILTER_KEY;

    //    public static final String VCF_FILTER = "FILTER";
    public static final String GT_KEY = VCFConstants.GENOTYPE_KEY;
    public static final String PASS_VALUE = "PASS";
    public static final String DEFAULT_FILTER_VALUE = ".";
    //    public static final String CALL_KEY = "CALL";

    /**
     *
     */
    public VariantMerger() {
        // TODO Auto-generated constructor stub
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
            se.setFormat(Arrays.asList(GT_KEY, GENOTYPE_FILTER_KEY));
            se.setSamplesPosition(new HashMap<String, Integer>());
            se.setSamplesData(new ArrayList<List<String>>());
            var.addStudyEntry(se);
        }
        return var;
    }

    public Variant merge(Variant current, Collection<Variant> load){
        // Validate variant information
//        ensureGtFormat(current);
        if (getStudy(current).getFormat() == null || getStudy(current).getFormat().isEmpty()) {
            throw new IllegalArgumentException("Format of sample data is empty!!!!!!");
        }
//        load.stream().forEach(v -> ensureGtFormat(v)); // ensure the GT is on the first position in FORMAT
        load.stream().forEach(v -> merge(current,v)); // Merge Each variant
        return current;
    }

    Variant merge(Variant current, Variant load){
        if (current.overlapWith(load, true)){
            mergeVariants(current,load);
        } // else ignore
        return current;
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
     *
     * @param current Variant to merge into
     * @param other Variant to extract information and update the current variant with
     */
    void mergeVariants(Variant current, Variant other) {
        StudyEntry currentStudy = getStudy(current);
        StudyEntry otherStudy = getStudy(other);
        Map<String, String> sampleToGt;
        if (currentStudy.getFormat().contains(GT_KEY)) {
            sampleToGt = sampleToGt(other);
        } else {
            sampleToGt = null;
        }
        Map<String, String> sampleToFilter;
        if (otherStudy.getFormat().contains(GENOTYPE_FILTER_KEY)) {
            sampleToFilter = sampleToSampleData(other, GENOTYPE_FILTER_KEY);
        } else {
            sampleToFilter = sampleToAttribute(other, VariantVcfFactory.FILTER);
        }

        String defaultFilterValue = currentStudy.getFiles().isEmpty() ? DEFAULT_FILTER_VALUE
                : currentStudy.getFiles().get(0).getAttributes().getOrDefault(VariantVcfFactory.FILTER, DEFAULT_FILTER_VALUE);
        ensureFormat(currentStudy, GENOTYPE_FILTER_KEY, defaultFilterValue);

        checkForDuplicates(current, other, currentStudy, otherStudy);
        // Secondary index: translate from e.g. 0/1 to 0/2
        List<AlternateCoordinate> otherSecondaryAlternates = buildAltList(other);
        List<Integer> secIdx = mergeSecondaryAlternates(current, otherSecondaryAlternates);

        // Add GT data for each sample to current Variant
        for (String sampleName : otherStudy.getOrderedSamplesName()) {
            boolean alreadyInStudy = currentStudy.getSamplesName().contains(sampleName);
            List<String> sampleDataList = new LinkedList<>();
            for (String format : currentStudy.getFormat()) {
                switch (format) {
                    case GT_KEY:
                        String gt = sampleToGt.get(sampleName);
                        if (StringUtils.isBlank(gt)) {
                            throw new IllegalStateException(String.format("No GT found for sample %s in \nVariant: %s\nIndex:%s",
                                    sampleName, other.getImpl(), sampleToGt));
                        }
                        String updatedGt = updateGT(gt, secIdx);
                        if (alreadyInStudy) {
                            String currGT = currentStudy.getSampleData(sampleName, GT_KEY);
                            Set<String> gtlst = new HashSet<>(Arrays.asList(currGT.split(",")));
                            gtlst.add(updatedGt);
                            updatedGt = StringUtils.join(gtlst, ',');
                        }
                        sampleDataList.add(updatedGt);
                        break;
                    case GENOTYPE_FILTER_KEY:
                        String filter = sampleToFilter.getOrDefault(sampleName, DEFAULT_FILTER_VALUE);
                        if (alreadyInStudy) {
                            // TODO decide if using the first set filter is the correct way
                            filter = currentStudy.getSampleData(sampleName, GENOTYPE_FILTER_KEY);
                        }
                        sampleDataList.add(filter);
                        break;
                    default:
                        // FIXME: Normalize information when merging data from different variants.
                        String value = otherStudy.getSampleData(sampleName, format);
                        sampleDataList.add(value == null ? "" : value);
//                        sampleDataList.add(value);
                        break;
                }
            }
            currentStudy.addSampleData(sampleName, sampleDataList);
        }

        mergeFiles(current, other);
    }

    private boolean checkForDuplicates(Variant current, Variant other, StudyEntry currentStudy, StudyEntry otherStudy) {
        if (isSameVariant(current, other)) {
            Set<String> duplicates = currentStudy.getSamplesName().stream()
                    .filter(s -> otherStudy.getSamplesName().contains(s))
                    .collect(Collectors.toSet());
            if (!duplicates.isEmpty()) {
                throw new IllegalStateException(String.format("Duplicated entries - issue with merge: %s; current: %s; other: %s;",

                        StringUtils.join(duplicates, ", "), variantToString(current), variantToString(other)));
            }
        } else {
            List<AlternateCoordinate> currSecAlts = getStudy(current).getSecondaryAlternates();
            Map<String, String> currSampleToGts = sampleToGt(current);
            for (int i = 0; i < currSecAlts.size(); i++) {
                // check same secondary hits
                AlternateCoordinate currSec = currSecAlts.get(i);
                if (isSameVariant(other, currSec) ) {
                    int gtIdx = i+2;
                    for (Map.Entry<String, String> sgte : currSampleToGts.entrySet()) {
                        if (otherStudy.getSamplesName().contains(sgte.getKey())) { // contains same individual
                            Set<String> gtIdxSet = Arrays.stream(sgte.getValue().split(","))
                                    .flatMap(e -> Arrays.stream(e.split("/")))
                                    .flatMap(e -> Arrays.stream(e.split("\\|"))).collect(Collectors.toSet());
                            if (gtIdxSet.contains(Integer.toString(gtIdx))){ // contains Alternate
                                throw new IllegalStateException(String.format(
                                        "Duplicated entries - issue with merge: %s; value: %s; gtidx: %s\ncurrent: %s; other: %s;\ncurrVar: %s\notherVar: %s",
                                        sgte.getKey(), sgte.getValue(), gtIdx,
                                        variantToString(current), variantToString(other), current.toJson(), other.toJson()));
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Map from a GT e.g. 1/2 to 4/5 using the provided mapping file. If no mapping is found, the same Allele is used.
     * @param gt Current GT
     * @param mapping Mapping from old to new allele index
     * @return Updated GT
     */
    private String updateGT(String gt, List<Integer> mapping) {
        Genotype gto = new Genotype(gt);
        int[] idx = gto.getAllelesIdx();
        int len = idx.length;
        IntStream.range(0, len).boxed().filter(i -> idx[i] >= 0 && idx[i] < mapping.size())
                .forEach(i -> gto.updateAlleleIdx(i, mapping.get(idx[i])));
        return gto.toGenotypeString();
    }

    /**
     * Adds {@link AlternateCoordinate} if missing.
     *
     * @param variant            Variant to modify
     * @param otherAlternates    All the alternates from the other variant
     * @return Mapping from the alleleIds of the otherAlternates to the mergedAlternates
     */
    protected List<Integer> mergeSecondaryAlternates(Variant variant, List<AlternateCoordinate> otherAlternates) {
        AlternateCoordinate mainAlternate = getMainAlternate(variant);
        List<AlternateCoordinate> secondaryAlternates = getStudy(variant).getSecondaryAlternates();

        List<Integer> idx = new ArrayList<>(secondaryAlternates.size() + otherAlternates.size());
        idx.add(0); // The reference is the same
        for (AlternateCoordinate alternateCoordinate : otherAlternates) {
            validateAlternate(alternateCoordinate);
            if (equals(alternateCoordinate, mainAlternate)) {
                idx.add(1);
                continue;
            }
            int indexOf = -1;
            for (int i = 0; i < secondaryAlternates.size(); i++) {
                AlternateCoordinate s = secondaryAlternates.get(i);
                if (equals(alternateCoordinate, s)) {
                    indexOf = i;
                    break;
                }
            }
            if (indexOf >= 0) {
                idx.add(indexOf + 2);
            } else {
                idx.add(secondaryAlternates.size() + 2);
                secondaryAlternates.add(alternateCoordinate);
            }
        }
        return idx;
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
    private List<AlternateCoordinate> buildAltList(Variant variant) {
        AlternateCoordinate mainAlternate = getMainAlternate(variant);
        List<AlternateCoordinate> alternates = new ArrayList<>();
        if (!mainAlternate.getType().equals(VariantType.NO_VARIATION)) {
            alternates.add(mainAlternate);
        }
        StudyEntry se = getStudy(variant);
        if(se.getSecondaryAlternates() != null){
            for (AlternateCoordinate alt : se.getSecondaryAlternates()) {
                if (alt.getChromosome() == null) {
                    alt.setChromosome(variant.getChromosome());
                }
                if (alt.getStart() == null) {
                    alt.setStart(variant.getStart());
                }
                if (alt.getEnd() == null) {
                    alt.setEnd(variant.getEnd());
                }
                if (alt.getReference() == null) {
                    alt.setReference(variant.getReference());
                }
                if (alt.getAlternate() == null) {
                    alt.setAlternate(variant.getAlternate());
                }
                alternates.add(alt);
            }
        }
        return alternates;
    }

    public static AlternateCoordinate getMainAlternate(Variant variant) {
        return new AlternateCoordinate(variant.getChromosome(), variant.getStart(), variant.getEnd(),
                variant.getReference(), variant.getAlternate(), variant.getType());
    }

    private void mergeFiles(Variant current, Variant other) {
        List<FileEntry> files = getStudy(other).getFiles().stream().map(fileEntry -> FileEntry.newBuilder(fileEntry).build()).collect(Collectors.toList());
        if (!current.toString().equals(other.toString())) {
            for (FileEntry file : files) {
                if (file.getCall() == null || file.getCall().isEmpty()) {
                    file.setCall(other.getStart() + ":" + other.getReference() + ":" + other.getAlternate() + ":0");
                }
            }
        }
        StudyEntry study = getStudy(current);
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
        return sampleToSampleData(load, GT_KEY);
    }

    private Map<String, String> sampleToSampleData(Variant var, String key){
        StudyEntry se = getStudy(var);
        return se.getSamplesName().stream()
                .filter(e -> StringUtils.isNotBlank(se.getSampleData(e, key))) // check for NULL or empty string
                .collect(Collectors.toMap(e -> e, e -> se.getSampleData(e, key)));
    }

    StudyEntry getStudy(Variant variant) {
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
                    studyEntry.addSampleData(sampleName, GENOTYPE_FILTER_KEY, defaultValue);
                }
            }
        }
    }

    static boolean onSameVariant (Variant a, Variant b){
        return a.onSameRegion(b)
                && StringUtils.equals(a.getReference(), b.getReference())
                && StringUtils.equals(a.getAlternate(), b.getAlternate());
    }

    public static boolean isSameVariant(Variant a, Variant b){
        return onSameVariant(a, b);
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

    public boolean overlapWith(Variant a, AlternateCoordinate b, boolean inclusive) {
        if (!StringUtils.equals(a.getChromosome(), b.getChromosome())) {
            return false; // Different Chromosome
        } else if (inclusive) {
            return a.getStart() <= b.getEnd() && a.getEnd() >= b.getStart();
        } else {
            return a.getStart() < b.getEnd() && a.getEnd() > b.getStart();
        }
    }
}
