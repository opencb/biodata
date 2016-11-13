package org.opencb.biodata.tools.alignment.filters;

import org.ga4gh.models.ReadAlignment;

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
                readAlignment.getAlignment() != null && readAlignment.getAlignment().getMappingQuality() > mappingQuality);
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
}
