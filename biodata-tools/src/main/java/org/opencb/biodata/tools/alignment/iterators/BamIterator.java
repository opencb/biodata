package org.opencb.biodata.tools.alignment.iterators;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;
import org.opencb.biodata.tools.alignment.filters.SamRecordFilters;

import java.util.Iterator;

/**
 * Created by pfurio on 25/10/16.
 */
public abstract class BamIterator<T> implements Iterator<T>, AutoCloseable {

    private SAMRecordIterator samRecordIterator;
    protected AlignmentFilters<SAMRecord> filters;

    protected SAMRecord prevNext;

    public BamIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public BamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        this.samRecordIterator = samRecordIterator;
        if (filters == null) {
            filters = new SamRecordFilters();
        }
        this.filters = filters;

        findNextMatch();
    }

    protected void findNextMatch() {
        prevNext = null;
        while (samRecordIterator.hasNext()) {
            SAMRecord next = samRecordIterator.next();
            if (filters.test(next)) {
                prevNext = next;
                return;
            }
        }
    }

    @Override
    public void close() throws Exception {
        samRecordIterator.close();
    }

}
