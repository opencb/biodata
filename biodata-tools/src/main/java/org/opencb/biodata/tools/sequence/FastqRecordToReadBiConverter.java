package org.opencb.biodata.tools.sequence;

import htsjdk.samtools.fastq.FastqRecord;
import org.opencb.biodata.models.sequence.Read;
import org.opencb.biodata.tools.BiConverter;

/**
 * Created by joaquin on 9/5/17.
 */
public class FastqRecordToReadBiConverter implements BiConverter<FastqRecord, Read> {
    @Override
    public Read to(FastqRecord obj) {
        return new Read(obj.getReadHeader(), obj.getReadString(), obj.getBaseQualityString());
    }

    @Override
    public FastqRecord from(Read obj) {
        return new FastqRecord(obj.getId().toString(), obj.getSequence().toString(), "+", obj.getQuality().toString());
    }
}
