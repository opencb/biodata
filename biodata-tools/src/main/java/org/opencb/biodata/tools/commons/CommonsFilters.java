package org.opencb.biodata.tools.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by joaquin on 11/14/16.
 */
public class CommonsFilters<T> {
    protected List<Predicate<T>> filters = new ArrayList<>();

    public CommonsFilters() {
    }

    public CommonsFilters(List<Predicate<T>> filters) {
        this.filters = filters;
    }

    public boolean test(T elem) {
        if (filters != null && filters.size() > 0) {
            for (Predicate<T> filter : filters) {
                if (!filter.test(elem)) {
                    return false;
                }
            }
        }
        return true;
    }

    public CommonsFilters<T> addFilter(Predicate<T> predicate) {
        filters.add(predicate);
        return this;
    }

    public CommonsFilters<T> addFilterList(List<Predicate<T>> predicates) {
        return addFilterList(predicates, true);
    }

    public CommonsFilters<T> addFilterList(List<Predicate<T>> predicates, boolean or) {
        return (or ? addFilterListOR(predicates) : addFilterListAND(predicates));
    }

    private CommonsFilters<T> addFilterListOR(List<Predicate<T>> predicates) {
        Predicate<T> result = (element -> false);
        for (Predicate<T> predicate: predicates) {
            result = result.or(predicate);
        }
        filters.add(result);
        return this;
    }

    private CommonsFilters<T> addFilterListAND(List<Predicate<T>> predicates) {
        Predicate<T> result = (element -> true);
        for (Predicate<T> predicate: predicates) {
            result = result.and(predicate);
        }
        filters.add(result);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Filters{");
        sb.append("filters=").append(filters);
        sb.append('}');
        return sb.toString();
    }

    public List<Predicate<T>> getFilters() {
        return filters;
    }

    public CommonsFilters setFilters(List<Predicate<T>> filters) {
        this.filters = filters;
        return this;
    }
}
