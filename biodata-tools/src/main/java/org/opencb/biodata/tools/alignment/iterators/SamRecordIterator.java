package org.opencb.biodata.tools.alignment.iterators;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.filtering.AlignmentFilters;

/**
 * Created by pfurio on 27/10/16.
 */
public class SamRecordIterator extends AlignmentIterator<SAMRecord> {

    public SamRecordIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public SamRecordIterator(SAMRecordIterator samRecordIterator, AlignmentFilters filters) {
        super(samRecordIterator, filters);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public SAMRecord next() {
        SAMRecord next = prevNext;
        moveIterator();
        return next;
    }
}
