package org.opencb.biodata.tools.alignment.iterators;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.tools.alignment.converters.SAMRecordToAvroReadAlignmentConverter;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToAvroReadAlignmentBamIterator extends BamIterator<ReadAlignment> {

    private SAMRecordToAvroReadAlignmentConverter samRecordToAvroReadAlignmentConverter;

    public SAMRecordToAvroReadAlignmentBamIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public SAMRecordToAvroReadAlignmentBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        this(samRecordIterator, filters, true);
    }

    public SAMRecordToAvroReadAlignmentBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters,
                                                   boolean binQualities) {
        super(samRecordIterator, filters);
        samRecordToAvroReadAlignmentConverter = new SAMRecordToAvroReadAlignmentConverter(binQualities);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public ReadAlignment next() {
        ReadAlignment readAlignment = samRecordToAvroReadAlignmentConverter.to(prevNext);
        findNextMatch();
        return readAlignment;
    }

}
