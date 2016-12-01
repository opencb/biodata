package org.opencb.biodata.tools.alignment.iterators;

import ga4gh.Reads;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.converters.SAMRecordToProtoReadAlignmentConverter;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToProtoReadAlignmentBamIterator extends BamIterator<Reads.ReadAlignment> {

    private SAMRecordToProtoReadAlignmentConverter protoReadAlignmentConverter;

    public SAMRecordToProtoReadAlignmentBamIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public SAMRecordToProtoReadAlignmentBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        this(samRecordIterator, filters, true);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentConverter();
    }

    public SAMRecordToProtoReadAlignmentBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters,
                                                    boolean binQualities) {
        super(samRecordIterator, filters);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentConverter(binQualities);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public Reads.ReadAlignment next() {
        Reads.ReadAlignment readAlignment = protoReadAlignmentConverter.to(prevNext);
        findNextMatch();
        return readAlignment;
    }

}
