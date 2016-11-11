package org.opencb.biodata.tools.alignment.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by pfurio on 26/10/16.
 */
public abstract class AlignmentFilters<T> {

    protected List<Predicate<T>> filters = new ArrayList<>();

    public AlignmentFilters() {
    }

    public AlignmentFilters(List<Predicate<T>> filters) {
        this.filters = filters;
    }

    public boolean apply(T elem) {
        if (filters != null && filters.size() > 0) {
            for (Predicate<T> filter : filters) {
                if (!filter.test(elem)) {
                    return false;
                }
            }
        }
        return true;
    }

    public AlignmentFilters addFilter(Predicate<T> predicate) {
        filters.add(predicate);
        return this;
    }

    public abstract AlignmentFilters addMappingQualityFilter(int mappingQuality);

    public abstract AlignmentFilters addProperlyPairedFilter();

    public abstract AlignmentFilters addUnmappedFilter();


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlignmentFilters{");
        sb.append("filters=").append(filters);
        sb.append('}');
        return sb.toString();
    }

    public List<Predicate<T>> getFilters() {
        return filters;
    }

    public AlignmentFilters setFilters(List<Predicate<T>> filters) {
        this.filters = filters;
        return this;
    }

}
