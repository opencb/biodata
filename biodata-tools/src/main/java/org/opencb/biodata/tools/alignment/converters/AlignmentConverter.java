package org.opencb.biodata.tools.alignment.converters;

import htsjdk.samtools.SAMFormatException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.TagValueAndUnsignedArrayFlag;
import htsjdk.samtools.TextTagCodec;
import org.opencb.biodata.tools.Converter;

import java.util.Map;

/**
 * Created by pfurio on 25/10/16.
 */
public abstract class AlignmentConverter<T> implements Converter<SAMRecord, T> {

    // From SAM specification
    protected static final int QNAME_COL = 0;
    protected static final int FLAG_COL = 1;
    protected static final int RNAME_COL = 2;
    protected static final int POS_COL = 3;
    protected static final int MAPQ_COL = 4;
    protected static final int CIGAR_COL = 5;
    protected static final int MRNM_COL = 6;
    protected static final int MPOS_COL = 7;
    protected static final int ISIZE_COL = 8;
    protected static final int SEQ_COL = 9;
    protected static final int QUAL_COL = 10;

    protected static final int NUM_REQUIRED_FIELDS = 11;
    protected boolean adjustQuality = false;


    protected SAMRecord createFromFields(String[] fields) {
        SAMRecord samRecord = new SAMRecord(null);
        if (fields != null && fields.length > 11) {
            samRecord.setReadName(fields[QNAME_COL]);
            samRecord.setFlags(Integer.valueOf(fields[FLAG_COL]));
            samRecord.setReferenceName(fields[RNAME_COL]);
            samRecord.setAlignmentStart(Integer.valueOf(fields[POS_COL]));
            samRecord.setMappingQuality(Integer.valueOf(fields[MAPQ_COL]));
            samRecord.setCigarString(fields[CIGAR_COL]);
            samRecord.setMateReferenceName(fields[MRNM_COL].equals("=") ? samRecord.getReferenceName() : fields[MRNM_COL]);
            samRecord.setMateAlignmentStart(Integer.valueOf(fields[MPOS_COL]));
            samRecord.setInferredInsertSize(Integer.valueOf(fields[ISIZE_COL]));

            if (!fields[SEQ_COL].equals("*")) {
                samRecord.setReadString(fields[SEQ_COL]);
            } else {
                samRecord.setReadBases(SAMRecord.NULL_SEQUENCE);
            }
            if (!fields[QUAL_COL].equals("*")) {
                samRecord.setBaseQualityString(fields[QUAL_COL]);
            } else {
                samRecord.setBaseQualities(SAMRecord.NULL_QUALS);
            }
        }
        return samRecord;
    }

}
