package org.opencb.biodata.tools.alignment.filtering;

import htsjdk.samtools.SAMRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by pfurio on 26/10/16.
 */
public class AlignmentFilter {

    List<Predicate<SAMRecord>> filters = new ArrayList<>();

    public AlignmentFilter() {
    }

    public AlignmentFilter(List<Predicate<SAMRecord>> filters) {
        this.filters = filters;
    }


    public AlignmentFilter addFilter(Predicate<SAMRecord> predicate) {
        filters.add(predicate);
        return this;
    }

    public AlignmentFilter addMappingQualityFilter(int mappingQuality) {
        filters.add(samRecord -> samRecord.getMappingQuality() > mappingQuality);
        return this;
    }

    public AlignmentFilter addProperlyPairedFilter() {
        filters.add(samRecord -> samRecord.getProperPairFlag());
        return this;
    }

    public List<Predicate<SAMRecord>> getFilters() {
        return filters;
    }

    public AlignmentFilter setFilters(List<Predicate<SAMRecord>> filters) {
        this.filters = filters;
        return this;
    }
}
