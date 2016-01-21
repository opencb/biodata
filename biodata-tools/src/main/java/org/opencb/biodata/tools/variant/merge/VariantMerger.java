/**
 * 
 */
package org.opencb.biodata.tools.variant.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 */
public class VariantMerger {

    private static final String HOM_REF = "0/0";
    private static final String GT_KEY = "GT";

    /**
     * 
     */
    public VariantMerger() {
        // TODO Auto-generated constructor stub
    }

    public void merge(Variant current, Collection<Variant> load){
        // Validate variant information
        ensureGtFormat(current);
        if(getStudy(current).getFormat().size() > 1){
            throw new IllegalArgumentException("Variant from Analysis talbe only supports GT data!!!");
        }
        load.stream().forEach(v -> ensureGtFormat(v)); // ensure the GT is on the first position in FORMAT
        load.stream().forEach(v -> merge(current,v)); // Merge Each variant
    }
    void merge(Variant current, Variant load){
        if(onSameVariant(current, load)){
            mergeSameVariant(current, load);
        } else if (onOverlappingPosition(current, load)){
            mergeOverlappingVariant(current,load);
        } 
        // else ignore
    }

    /**
     * 
     * @param current
     * @param load
     */
    void mergeOverlappingVariant(Variant current, Variant other) {
        Map<String, String> sampleToGt = sampleToGt(other);
        StudyEntry se = getStudy(current);
        List<Integer> secIdx = buildSecIndex(se,buildSecAltList(other));
        // Translate from e.g. 1 -> 2 which would end up as 0/1 0/2
        int newSecGtOffset = 2; // 2 -> 0 Ref, 1 Alt, 2+ secAlt
        Map<String, String> otherToCurrent = IntStream.range(0, secIdx.size()).mapToObj(i -> i)
                .collect(Collectors.toMap(i -> Integer.toString(i + 1), i -> Integer.toString( secIdx.get(i) + newSecGtOffset)));
        sampleToGt.entrySet().stream()
            .forEach(e -> se.addSampleData(e.getKey(),Collections.singletonList(updateGT(e.getValue(),otherToCurrent))));
    }

    /**
     * Map from a GT e.g. 1/2 to 4/5 using the provided mapping file. If no mapping is found, the same Allele is used.
     * @param gt Current GT
     * @param mapping Mapping from old to new allele index
     * @return Updated GT
     */
    private String updateGT(String gt, Map<String, String> mapping) {
        String ngt = Arrays.stream(gt.split("/")).map(s -> mapping.containsKey(s)?mapping.get(s):s).collect(Collectors.joining("/"));
        return ngt;
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
        StudyEntry se = getStudy(current);
        // Add GT data for each sample to current Variant
        sampleToGt.entrySet().forEach(e -> se.addSampleData(e.getKey(), Collections.singletonList(e.getValue())));
    }

    private Map<String, String> sampleToGt(Variant load) {
        StudyEntry se = getStudy(load);
        Map<String, String> mapgt = se.getSamplesName().stream().collect(Collectors.toMap(e -> e, e -> se.getSampleData(e, GT_KEY)));
        return mapgt;
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
    
    static boolean onOverlappingPosition(Variant a, Variant b){
        return a.getStart() <= b.getEnd() && a.getEnd() >= b.getStart();
    }
    
    static boolean onSameStartPosition (Variant a, Variant b){
        return StringUtils.equals(a.getChromosome(), b.getChromosome()) 
                && a.getStart().equals(b.getStart());
    }

    static boolean onSameVariant (Variant a, Variant b){
        return onSameStartPosition(a, b) 
                && StringUtils.equals(a.getReference(), b.getReference())
                && StringUtils.equals(a.getAlternate(), b.getAlternate());
    }

}
