package org.opencb.biodata.tools.alignment.iterators;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;

/**
 * Created by pfurio on 27/10/16.
 */
public class SamRecordBamIterator extends BamIterator<SAMRecord> {

    public SamRecordBamIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public SamRecordBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        super(samRecordIterator, filters);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public SAMRecord next() {
        SAMRecord next = prevNext;
        findNextMatch();
        return next;
    }
}
