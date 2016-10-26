package org.opencb.biodata.tools.alignment.iterators;

import htsjdk.samtools.SAMRecordIterator;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.tools.alignment.converters.SAMRecordToAvroReadAlignmentConverter;
import org.opencb.biodata.tools.alignment.filtering.AlignmentFilter;

/**
 * Created by pfurio on 25/10/16.
 */
public class AvroIterator extends AlignmentIterator<ReadAlignment> {

    private SAMRecordToAvroReadAlignmentConverter samRecordToAvroReadAlignmentConverter;

    public AvroIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public AvroIterator(SAMRecordIterator samRecordIterator, AlignmentFilter filters) {
        super(samRecordIterator, filters);
        samRecordToAvroReadAlignmentConverter = new SAMRecordToAvroReadAlignmentConverter();
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public ReadAlignment next() {
        ReadAlignment readAlignment = samRecordToAvroReadAlignmentConverter.to(prevNext);
        moveIterator();
        return readAlignment;
    }

}
