/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.commons;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by joaquin on 11/14/16.
 */
public class CommonsFilters<T> implements Predicate<T> {

    protected List<Predicate<T>> filters;


    public CommonsFilters() {
        this(new ArrayList<>());
    }

    public CommonsFilters(List<Predicate<T>> filters) {
        this.setFilters(filters);
    }

    /**
     * This method executes a logical AND.
     * @param elem
     * @return true when all filters are satisfied
     */
    @Override
    public boolean test(T elem) {
        return this.test(elem, false);
    }

    public boolean test(T elem, boolean or) {
        if (or) {
            // This is equivalent to a logical OR
            for (Predicate<T> filter : filters) {
                if (filter.test(elem)) {
                    return true;
                }
            }
            return false;
        } else {
            // This is equivalent to a logical AND
            for (Predicate<T> filter : filters) {
                if (!filter.test(elem)) {
                    return false;
                }
            }
            return true;
        }
    }

    public CommonsFilters<T> addFilter(Predicate<T> predicate) {
        filters.add(predicate);
        return this;
    }

    public CommonsFilters<T> addFilters(List<Predicate<T>> predicates) {
        filters.addAll(predicates);
        return this;
    }


    public CommonsFilters<T> addFilterList(List<Predicate<T>> predicates) {
        return addFilterList(predicates, true);
    }

    public CommonsFilters<T> addFilterList(List<Predicate<T>> predicates, boolean or) {
        Predicate<T> result;
        if (or) {
            result = (element -> false);
            for (Predicate<T> predicate: predicates) {
                result = result.or(predicate);
            }
        } else {
            result = (element -> true);
            for (Predicate<T> predicate: predicates) {
                result = result.and(predicate);
            }
        }
        filters.add(result);
        return this;
    }


    protected String[] splitOperator(String value) {
        int first = StringUtils.indexOfAny(value, '=', '>', '<');
        int last = StringUtils.lastIndexOfAny(value, "=", ">", "<");

        if (first == StringUtils.INDEX_NOT_FOUND) {
            throw new IllegalArgumentException("Malformed filter. Expected <KEY><OP><VALUE>");
        }

        return new String[]{value.substring(0, first), value.substring(first, last + 1), value.substring(last + 1)};
    }

    protected static Predicate<String> buildPredicate(String op, String value) {
        return buildPredicate(op, value, false);
    }

    protected static Predicate<String> buildPredicate(String op, String value, boolean acceptNull) {
        Predicate<String> predicate;
        Double numValue;
        switch (op) {
            case "=":
            case "==":
                Set<String> values;
                if (value.contains(",") || acceptNull) {
                    values = new HashSet<>(Arrays.asList(value.split(",")));
                    if (acceptNull) {
                        values.add(null);
                    }
                } else {
                    values = Collections.singleton(value);
                }
                predicate = values::contains;
                break;
            case ">":
                numValue = Double.valueOf(value);
                predicate = v -> {
                    if (StringUtils.isEmpty(v) || v.equals(".")) {
                        return acceptNull;
                    } else {
                        try {
                            return Double.valueOf(v) > numValue;
                        } catch (NumberFormatException e) {
                            return acceptNull;
                        }
                    }
                };
                break;
            case ">=":
                numValue = Double.valueOf(value);
                predicate = v -> {
                    if (StringUtils.isEmpty(v) || v.equals(".")) {
                        return acceptNull;
                    } else {
                        try {
                            return Double.valueOf(v) >= numValue;
                        } catch (NumberFormatException e) {
                            return acceptNull;
                        }
                    }
                };
                break;
            case "<":
                numValue = Double.valueOf(value);
                predicate = v -> {
                    if (StringUtils.isEmpty(v) || v.equals(".")) {
                        return acceptNull;
                    } else {
                        try {
                            return Double.valueOf(v) < numValue;
                        } catch (NumberFormatException e) {
                            return acceptNull;
                        }
                    }
                };
                break;
            case "<=":
                numValue = Double.valueOf(value);
                predicate = v -> {
                    if (StringUtils.isEmpty(v) || v.equals(".")) {
                        return acceptNull;
                    } else {
                        try {
                            return Double.valueOf(v) <= numValue;
                        } catch (NumberFormatException e) {
                            return acceptNull;
                        }
                    }
                };
                break;
            default:
                throw new IllegalArgumentException("Unsupported operator " + op);
        }
        return predicate;
    }

    @Override
    public String toString() {
        return "Filters{" + "filters=" + filters + "}";
    }

    public List<Predicate<T>> getFilters() {
        return filters;
    }

    public CommonsFilters setFilters(List<Predicate<T>> filters) {
        this.filters = filters == null ? new ArrayList<>() : filters;
        return this;
    }

}
