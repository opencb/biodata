/**
 * 
 */
package org.opencb.biodata.tools.variant.merge;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 */
public class VariantMerger {

    private static final String VCF_FILTER = VariantVcfFactory.FILTER;
    public static final String GT_KEY = "GT";
    public static final String PASS_KEY = "PASS";
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
            se.setFormat(Arrays.asList(GT_KEY, VCF_FILTER));
            se.setSamplesPosition(new HashMap<String, Integer>());
            se.setSamplesData(new ArrayList<List<String>>());
            var.addStudyEntry(se);
        }
        return var;
    }

    public Variant merge(Variant current, Collection<Variant> load){
        // Validate variant information
        ensureGtFormat(current);
        if (getStudy(current).getFormat() == null || getStudy(current).getFormat().isEmpty()) {
            throw new IllegalArgumentException("Format of sample data is empty!!!!!!");
        }
        if (!StringUtils.equals(GT_KEY, getStudy(current).getFormat().get(0))) {
            throw new IllegalArgumentException("GT data is expected in first column!!!");
        }
        load.stream().forEach(v -> ensureGtFormat(v)); // ensure the GT is on the first position in FORMAT
        load.stream().forEach(v -> merge(current,v)); // Merge Each variant
        return current;
    }

    Variant merge(Variant current, Variant load){
        if(onSameVariant(current, load)){
            mergeSameVariant(current, load);
        } else if (current.overlapWith(load, true)){
            mergeOverlappingVariant(current,load);
        }
        return current;
        // else ignore
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
            if(pos >= sd.size()){
                sb.append(sn).append(":S;");
            } else if(null == sd.get(pos) || sd.get(pos).size() < 1) {
                sb.append(sn).append(":G;");
            } else{
                String gt = sd.get(pos).get(0); // GT
                sb.append(sn).append(":").append(gt).append(";");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @param current
     * @param other
     */
    void mergeOverlappingVariant(Variant current, Variant other) {
        Map<String, String> sampleToGt = sampleToGt(other);
        Map<String, String> sampleToFilter = sampleToAttribute(other, VCF_FILTER);
        StudyEntry se = getStudy(current);

        ensureFormat(se, VCF_FILTER, "-");

        Set<String> duplicates = se.getSamplesName().stream().filter(s -> sampleToGt.containsKey(s)).collect(Collectors.toSet());
        if (!duplicates.isEmpty()) {
            throw new IllegalStateException(String.format("Duplicated entries - issue with merge: %s; current: %s; other: %s;",
                    StringUtils.join(duplicates, ", "), variantToString(current), variantToString(other)));
        }
        // Secondary index: translate from e.g. 0/1 to 0/2
        List<AlternateCoordinate> otherSecondaryAlternates = buildAltList(other);
        List<Integer> secIdx = mergeSecondaryAlternates(current, otherSecondaryAlternates);

        // Add GT data for each sample to current Variant
        for (String sampleName : getStudy(other).getOrderedSamplesName()) {
            List<String> sampleDataList = Arrays.asList(updateGT(sampleToGt.get(sampleName), secIdx), sampleToFilter.getOrDefault(sampleName, "-"));
            se.addSampleData(sampleName, sampleDataList);
        }
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
    private List<Integer> mergeSecondaryAlternates(Variant variant, List<AlternateCoordinate> otherAlternates) {
        AlternateCoordinate mainAlternate = getMainAlternate(variant);
        List<AlternateCoordinate> secondaryAlternates = getStudy(variant).getSecondaryAlternates();

        List<Integer> idx = new ArrayList<>(secondaryAlternates.size() + otherAlternates.size());
        idx.add(0); // The reference is the same
        for (AlternateCoordinate alternateCoordinate : otherAlternates) {
            int indexOf = secondaryAlternates.indexOf(alternateCoordinate);
            if (indexOf >= 0) {
                idx.add(indexOf + 2);
            } else if (alternateCoordinate.equals(mainAlternate)) {
                idx.add(1);
            } else {
                idx.add(secondaryAlternates.size() + 2);
                secondaryAlternates.add(alternateCoordinate);
            }
        }
        return idx;
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
            alternates.addAll(se.getSecondaryAlternates());
        }
        return alternates;
    }

    public static AlternateCoordinate getMainAlternate(Variant other) {
        return new AlternateCoordinate(other.getChromosome(), other.getStart(), other.getEnd(),
                    other.getReference(), other.getAlternate(), other.getType());
    }

    /**
     * Add GT data from variant passing {{@link #onSameVariant(Variant, Variant)}
     * @param current Variant to merge into
     * @param same Variant to extract information and update the current variant with
     */
    void mergeSameVariant(Variant current, Variant same){
        Map<String, String> sampleToGt = sampleToGt(same);
        Map<String, String> sampleToFilter = sampleToAttribute(same,VCF_FILTER);
        StudyEntry se = getStudy(current);

        ensureFormat(se, VCF_FILTER, "-");

        Set<String> duplicates = se.getSamplesName().stream().filter(s -> sampleToGt.containsKey(s)).collect(Collectors.toSet());
        if(!duplicates.isEmpty()){
            throw new IllegalStateException(String.format("Duplicated entries - issue with merge: %s", StringUtils.join(duplicates,", ")));
        }

        List<AlternateCoordinate> otherSecondaryAlternates = buildAltList(same);
        List<Integer> secIdx = mergeSecondaryAlternates(current, otherSecondaryAlternates);

        // Add GT data for each sample to current Variant
        for (String sampleName : getStudy(same).getOrderedSamplesName()) {
//            List<String> sampleData = Arrays.asList(sampleToGt.get(sampleName), sampleToFilter.getOrDefault(sampleName, "-"));
            List<String> sampleData = Arrays.asList(updateGT(sampleToGt.get(sampleName), secIdx), sampleToFilter.getOrDefault(sampleName, "-"));
            se.addSampleData(sampleName, sampleData);
        }
    }

    private Map<String,String> sampleToAttribute(Variant var, String key){
        StudyEntry se = getStudy(var);
        return se.getSamplesName().stream()
                .filter(e -> StringUtils.isNotBlank(se.getSampleData(e, key))) // check for NULL or empty string
                .collect(Collectors.toMap(e -> e, e -> se.getSampleData(e,key)));
    }
    
    private Map<String, String> sampleToGt(Variant load) {
        return sampleToAttribute(load, GT_KEY);
    }

    StudyEntry getStudy(Variant load) {
        return load.getStudies().get(0);
    }

    private void ensureGtFormat(Variant v){
        String gt = getStudy(v).getFormat().get(0);
        if(!StringUtils.equals(gt, GT_KEY)){
            throw new IllegalArgumentException("Variant GT is not on first position, but found " + gt + " instead !!!");
        }
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
            for (String sampleName : studyEntry.getOrderedSamplesName()) {
                studyEntry.addSampleData(sampleName, VCF_FILTER, defaultValue);
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
}
