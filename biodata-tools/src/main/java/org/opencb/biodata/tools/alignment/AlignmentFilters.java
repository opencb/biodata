package org.opencb.biodata.tools.alignment;

import htsjdk.samtools.SAMRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by pfurio on 26/10/16.
 */
public class AlignmentFilters {

    private List<Predicate<SAMRecord>> filters = new ArrayList<>();

    public AlignmentFilters() {
    }

    public AlignmentFilters(List<Predicate<SAMRecord>> filters) {
        this.filters = filters;
    }

    public static AlignmentFilters create() {
        return new AlignmentFilters();
    }


    public AlignmentFilters addFilter(Predicate<SAMRecord> predicate) {
        filters.add(predicate);
        return this;
    }

    public AlignmentFilters addMappingQualityFilter(int mappingQuality) {
        filters.add(samRecord -> samRecord.getMappingQuality() > mappingQuality);
        return this;
    }

    public AlignmentFilters addProperlyPairedFilter() {
        filters.add(samRecord -> samRecord.getProperPairFlag());
        return this;
    }

    public AlignmentFilters addUnmappedFilter() {
        filters.add(samRecord -> samRecord.getReadUnmappedFlag());
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlignmentFilters{");
        sb.append("filters=").append(filters);
        sb.append('}');
        return sb.toString();
    }

    public List<Predicate<SAMRecord>> getFilters() {
        return filters;
    }

    public AlignmentFilters setFilters(List<Predicate<SAMRecord>> filters) {
        this.filters = filters;
        return this;
    }

}
