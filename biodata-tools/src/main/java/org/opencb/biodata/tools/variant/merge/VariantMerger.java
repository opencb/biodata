/**
 * 
 */
package org.opencb.biodata.tools.variant.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 */
public class VariantMerger {

    private static final String VCF_FILTER = "FILTER";
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
            se.setFormat(Arrays.asList(new String[]{GT_KEY,PASS_KEY}));
            se.setSamplesPosition(new HashMap<String, Integer>());
            se.setSamplesData(new ArrayList<List<String>>());
            var.addStudyEntry(se);
        }
        return var;
    }

    public void merge(Variant current, Collection<Variant> load){
        // Validate variant information
        ensureGtFormat(current);
        if (getStudy(current).getFormat() == null || getStudy(current).getFormat().isEmpty()) {
            throw new IllegalArgumentException("Format of sample data is empty!!!!!!");
        }
        if (! StringUtils.equals(GT_KEY, getStudy(current).getFormat().get(0))) {
            throw new IllegalArgumentException("GT data is expected in first column!!!");
        }
        load.stream().forEach(v -> ensureGtFormat(v)); // ensure the GT is on the first position in FORMAT
        load.stream().forEach(v -> merge(current,v)); // Merge Each variant
    }
    void merge(Variant current, Variant load){
        if(onSameVariant(current, load)){
            mergeSameVariant(current, load);
        } else if (current.overlapWith(load, true)){
            mergeOverlappingVariant(current,load);
        } 
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
     * @param load
     */
    void mergeOverlappingVariant(Variant current, Variant other) {
        Map<String, String> sampleToGt = sampleToGt(other);
        Map<String, String> sampleToFilter = sampleToAttribute(other, VCF_FILTER);
        StudyEntry se = getStudy(current);
        Set<String> duplicates = se.getSamplesName().stream().filter(s -> sampleToGt.containsKey(s)).collect(Collectors.toSet());
        if (!duplicates.isEmpty()) {
            throw new IllegalStateException(String.format("Duplicated entries - issue with merge: %s; current: %s; other: %s;",
                    StringUtils.join(duplicates, ", "), variantToString(current), variantToString(other)));
        }
        // Secondary index: translate from e.g. 0/1 to 0/2
        List<Integer> secIdx = buildSecIndex(se, buildSecAltList(other));
        int newSecGtOffset = 2; // 2 -> 0 Ref, 1 Alt, 2+ secAlt
        Map<Integer, Integer> otherToCurrent = IntStream.range(0, secIdx.size()).mapToObj(i -> i)
                .collect(Collectors.toMap(i -> i + 1, i -> secIdx.get(i) + newSecGtOffset));
        // fix issue with UnsupportedOperationException during calling
        // StudyEntry.addSampleData(StudyEntry.java:253)
        List<List<String>> sd = se.getSamplesData().stream().map(l -> new ArrayList<>(l)).collect(Collectors.toList());
        se.setSamplesData(sd);
        sampleToGt.entrySet().stream()
            .forEach(e -> se.addSampleData(e.getKey(),
                    Arrays.asList(updateGT(e.getValue(), otherToCurrent), sampleToFilter.getOrDefault(e, "-"))));
    }

    /**
     * Map from a GT e.g. 1/2 to 4/5 using the provided mapping file. If no mapping is found, the same Allele is used.
     * @param gt Current GT
     * @param mapping Mapping from old to new allele index
     * @return Updated GT
     */
    private String updateGT(String gt, Map<Integer, Integer> mapping) {
        Genotype gto = new Genotype(gt);
        int[] idx = gto.getAllelesIdx();
        int len = idx.length;
        IntStream.range(0, len).boxed().filter(i -> mapping.containsKey(idx[i])).forEach(i -> gto.updateAlleleIdx(i, mapping.get(idx[i])));
        return gto.toGenotypeString();
    }

    /**
     * Adds {@link AlternateCoordinate} if missing and returns the indexes of the provided list
     * @param se
     * @param secLst
     * @return index of the provided coordinats in the {@link StudyEntry}
     */
    private List<Integer> buildSecIndex(StudyEntry se, List<AlternateCoordinate> secLst) {
        secLst.stream().filter(s -> !se.getSecondaryAlternates().contains(s)).forEach(s -> se.getSecondaryAlternates().add(s));
        Map<AlternateCoordinate, Integer> idxMap = IntStream.range(0, se.getSecondaryAlternates().size()).mapToObj(i -> i)
                .collect(Collectors.toMap(i -> (AlternateCoordinate) se.getSecondaryAlternates().get(i), i -> i));
        List<Integer> idx = secLst.stream().map(s -> idxMap.get(s)).collect(Collectors.toList());
        return idx;
    }

    private List<AlternateCoordinate> buildSecAltList(Variant other) {
        AlternateCoordinate secAlt = new AlternateCoordinate(other.getChromosome(), other.getStart(), other.getEnd(), other.getReference(),
                other.getAlternate(), other.getType());
        List<AlternateCoordinate> secLst = new ArrayList<AlternateCoordinate>();
        secLst.add(secAlt);
        StudyEntry ose = getStudy(other);
        if(ose.getSecondaryAlternates() != null){
            secLst.addAll(ose.getSecondaryAlternates());
        }
        return secLst;
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
        Set<String> duplicates = se.getSamplesName().stream().filter(s -> sampleToGt.containsKey(s)).collect(Collectors.toSet());
        if(!duplicates.isEmpty()){
            throw new IllegalStateException(String.format("Duplicated entries - issue with merge: %s", StringUtils.join(duplicates,", ")));
        }
        List<List<String>> sd = se.getSamplesData().stream().map(l -> new ArrayList<>(l)).collect(Collectors.toList());
        se.setSamplesData(sd); // fix for AbstractList exception
        // Add GT data for each sample to current Variant
        sampleToGt.entrySet().forEach(e -> se.addSampleData(e.getKey(), Arrays.asList(e.getValue(),sampleToFilter.getOrDefault(e, "-"))));
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

    static boolean onSameVariant (Variant a, Variant b){
        return a.onSameRegion(b)
                && StringUtils.equals(a.getReference(), b.getReference())
                && StringUtils.equals(a.getAlternate(), b.getAlternate());
    }

    public static boolean isSameVariant(Variant a, Variant b){
        return onSameVariant(a, b);
    }
}
