package org.opencb.biodata.tools.alignment.filters;

import org.opencb.biodata.models.core.Region;

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

    public AlignmentFilters<T> addFilter(Predicate<T> predicate) {
        filters.add(predicate);
        return this;
    }

    public AlignmentFilters<T> addFilterList(List<Predicate<T>> predicates) {
        return addFilterList(predicates, true);
    }

    public AlignmentFilters<T> addFilterList(List<Predicate<T>> predicates, boolean or) {
        return (or ? addFilterListOR(predicates) : addFilterListAND(predicates));
    }

    private AlignmentFilters<T> addFilterListOR(List<Predicate<T>> predicates) {
        Predicate<T> result = (element -> false);
        for (Predicate<T> predicate: predicates) {
            result = result.or(predicate);
        }
        filters.add(result);
        return this;
    }

    private AlignmentFilters<T> addFilterListAND(List<Predicate<T>> predicates) {
        Predicate<T> result = (element -> true);
        for (Predicate<T> predicate: predicates) {
            result = result.and(predicate);
        }
        filters.add(result);
        return this;
    }

    public abstract AlignmentFilters<T> addMappingQualityFilter(int mappingQuality);

    public abstract AlignmentFilters<T> addProperlyPairedFilter();

    public abstract AlignmentFilters<T> addUnmappedFilter();

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
