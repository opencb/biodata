package org.opencb.biodata.tools.alignment.filters;

import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.models.core.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by imedina on 11/11/16.
 */
public class ReadAlignmentFilters extends AlignmentFilters<ReadAlignment> {

    public ReadAlignmentFilters() {
    }

    public ReadAlignmentFilters(List<Predicate<ReadAlignment>> filters) {
        this.filters = filters;
    }

    public static AlignmentFilters<ReadAlignment> create() {
        return new ReadAlignmentFilters();
    }

    @Override
    public AlignmentFilters<ReadAlignment> addMappingQualityFilter(int mappingQuality) {
        filters.add(readAlignment ->
                readAlignment.getAlignment() != null && readAlignment.getAlignment().getMappingQuality() >= mappingQuality);
        return this;
    }

    @Override
    public AlignmentFilters<ReadAlignment> addProperlyPairedFilter() {
        filters.add(readAlignment -> !readAlignment.getImproperPlacement());
        return this;
    }

    @Override
    public AlignmentFilters<ReadAlignment> addUnmappedFilter() {
        filters.add(readAlignment -> readAlignment.getAlignment() == null);
        return this;
    }

    @Override
    public AlignmentFilters<ReadAlignment> addDuplicatedFilter() {
        filters.add(readAlignment -> !readAlignment.getDuplicateFragment());
        return this;
    }

    @Override
    public AlignmentFilters<ReadAlignment> addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Arrays.asList(region), contained);
    }

    @Override
    public AlignmentFilters<ReadAlignment> addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<ReadAlignment>> predicates = new ArrayList<>();
        for (Region region: regions) {
            // estimate the end position of the alignment, it does not take into account the CIGAR code
            if (contained) {
                predicates.add(readAlignment -> readAlignment.getAlignment() != null
                        && readAlignment.getAlignment().getPosition().getReferenceName().equals(region.getChromosome())
                        && readAlignment.getAlignment().getPosition().getPosition() >= region.getStart()
                        && readAlignment.getAlignment().getPosition().getPosition()
                        + readAlignment.getAlignedSequence().length() <= region.getEnd());
            } else {
                predicates.add(readAlignment -> readAlignment.getAlignment() != null
                        && readAlignment.getAlignment().getPosition().getReferenceName().equals(region.getChromosome())
                        && readAlignment.getAlignment().getPosition().getPosition() <= region.getEnd()
                        && readAlignment.getAlignment().getPosition().getPosition()
                        + readAlignment.getAlignedSequence().length() >= region.getStart());
            }
        }
        addFilterList(predicates);
        return this;
    }
}
