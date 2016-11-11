package org.opencb.biodata.tools.alignment.iterators;

import ga4gh.Reads;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.converters.SAMRecordToProtoReadAlignmentConverter;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToProtoReadBamFileIterator extends BamFileIterator<Reads.ReadAlignment> {

    private SAMRecordToProtoReadAlignmentConverter protoReadAlignmentConverter;

    public SAMRecordToProtoReadBamFileIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null, true);
    }

    public SAMRecordToProtoReadBamFileIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        this(samRecordIterator, filters, true);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentConverter();
    }

    public SAMRecordToProtoReadBamFileIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters, boolean binQual) {
        super(samRecordIterator, filters);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentConverter(binQual);
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
