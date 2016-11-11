package org.opencb.biodata.tools.alignment.iterators;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.tools.alignment.converters.SAMRecordToAvroReadAlignmentConverter;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToAvroReadBamFileIterator extends BamFileIterator<ReadAlignment> {

    private SAMRecordToAvroReadAlignmentConverter samRecordToAvroReadAlignmentConverter;

    public SAMRecordToAvroReadBamFileIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null, true);
    }

    public SAMRecordToAvroReadBamFileIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        this(samRecordIterator, filters, true);
    }

    public SAMRecordToAvroReadBamFileIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters, boolean binQual) {
        super(samRecordIterator, filters);
        samRecordToAvroReadAlignmentConverter = new SAMRecordToAvroReadAlignmentConverter(binQual);
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
