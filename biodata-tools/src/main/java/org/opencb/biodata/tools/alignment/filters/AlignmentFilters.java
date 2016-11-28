package org.opencb.biodata.tools.alignment.filters;

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.CommonsFilters;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by pfurio on 26/10/16.
 */
public abstract class AlignmentFilters<T> extends CommonsFilters<T> {

    public AlignmentFilters() {
    }

    public AlignmentFilters(List<Predicate<T>> filters) {
        super(filters);
    }

    @Override
    public AlignmentFilters<T> addFilter(Predicate<T> predicate) {
        super.addFilter(predicate);
        return this;
    }

    @Override
    public AlignmentFilters<T> addFilterList(List<Predicate<T>> predicates) {
        super.addFilterList(predicates);
        return this;
    }

    @Override
    public AlignmentFilters<T> addFilterList(List<Predicate<T>> predicates, boolean or) {
        super.addFilterList(predicates, or);
        return this;
    }

    public abstract AlignmentFilters<T> addMappingQualityFilter(int mappingQuality);

    public abstract AlignmentFilters<T> addProperlyPairedFilter();

    public abstract AlignmentFilters<T> addUnmappedFilter();

    public abstract AlignmentFilters<T> addRegionFilter(Region region, boolean contained);

    public abstract AlignmentFilters<T> addRegionFilter(List<Region> regions, boolean contained);
}
