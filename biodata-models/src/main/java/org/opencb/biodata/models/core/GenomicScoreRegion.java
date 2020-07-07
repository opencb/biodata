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

package org.opencb.biodata.models.core;

import java.util.List;
import java.util.Objects;

/**
 * Created by imedina on 06/11/15.
 */
public class GenomicScoreRegion<T> {

    private String chromosome;
    private int start;
    private int end;
    private String source;
    private List<T> values;

    public GenomicScoreRegion() {
    }

    public GenomicScoreRegion(String chromosome, int start, int end, String type, List<T> values) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.source = type;
        this.values = values;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenomicPositionScore{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", source='").append(source).append('\'');
        sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public GenomicScoreRegion<T> setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public GenomicScoreRegion<T> setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public GenomicScoreRegion<T> setEnd(int end) {
        this.end = end;
        return this;
    }

    public String getSource() {
        return source;
    }

    public GenomicScoreRegion<T> setSource(String source) {
        this.source = source;
        return this;
    }

    public List<T> getValues() {
        return values;
    }

    public GenomicScoreRegion<T> setValues(List<T> values) {
        this.values = values;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenomicScoreRegion)) return false;
        GenomicScoreRegion<?> that = (GenomicScoreRegion<?>) o;
        return getStart() == that.getStart() &&
                getEnd() == that.getEnd() &&
                getChromosome().equals(that.getChromosome()) &&
                getSource().equals(that.getSource()) &&
                getValues().equals(that.getValues());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChromosome(), getStart(), getEnd(), getSource(), getValues());
    }
}
