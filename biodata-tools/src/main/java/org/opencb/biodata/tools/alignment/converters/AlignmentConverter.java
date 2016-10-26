package org.opencb.biodata.tools.alignment.converters;

import htsjdk.samtools.SAMRecord;
import org.opencb.biodata.tools.Converter;

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

}
