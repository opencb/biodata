/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.alignment.converters;

import htsjdk.samtools.SAMFormatException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.TagValueAndUnsignedArrayFlag;
import htsjdk.samtools.TextTagCodec;
import htsjdk.samtools.util.StringUtil;
import org.opencb.biodata.tools.BiConverter;

import java.util.Map;

/**
 * Created by pfurio on 25/10/16.
 */
public abstract class AlignmentBiConverter<T> implements BiConverter<SAMRecord, T> {

    // From SAM specification
    private static final int QNAME_COL = 0;
    private static final int FLAG_COL = 1;
    private static final int RNAME_COL = 2;
    private static final int POS_COL = 3;
    private static final int MAPQ_COL = 4;
    private static final int CIGAR_COL = 5;
    private static final int MRNM_COL = 6;
    private static final int MPOS_COL = 7;
    private static final int ISIZE_COL = 8;
    private static final int SEQ_COL = 9;
    private static final int QUAL_COL = 10;

    private static final int NUM_REQUIRED_FIELDS = 11;
    protected boolean adjustQuality = false;

    protected SAMRecord from(String samLine) {
        final String[] fields = new String[1000];
        final int numFields = StringUtil.split(samLine, fields, '\t');
        if (numFields < NUM_REQUIRED_FIELDS) {
            throw new IllegalArgumentException("Not enough fields");
        }
        if (numFields == fields.length) {
            throw new IllegalArgumentException("Too many fields in SAM text record.");
        }
        for (int i = 0; i < numFields; ++i) {
            if (fields[i].isEmpty()) {
                throw new IllegalArgumentException("Empty field at position " + i + " (zero-based)");
            }
        }

        SAMRecord out = new SAMRecord(null);
        if (fields.length > 11) {
            out.setReadName(fields[QNAME_COL]);
            out.setFlags(Integer.valueOf(fields[FLAG_COL]));
            out.setReferenceName(fields[RNAME_COL]);
            out.setAlignmentStart(Integer.valueOf(fields[POS_COL]));
            out.setMappingQuality(Integer.valueOf(fields[MAPQ_COL]));
            out.setCigarString(fields[CIGAR_COL]);
            out.setMateReferenceName(fields[MRNM_COL].equals("=") ? out.getReferenceName() : fields[MRNM_COL]);
            out.setMateAlignmentStart(Integer.valueOf(fields[MPOS_COL]));
            out.setInferredInsertSize(Integer.valueOf(fields[ISIZE_COL]));

            if (!fields[SEQ_COL].equals("*")) {
                out.setReadString(fields[SEQ_COL]);
            } else {
                out.setReadBases(SAMRecord.NULL_SEQUENCE);
            }
            if (!fields[QUAL_COL].equals("*")) {
                out.setBaseQualityString(fields[QUAL_COL]);
            } else {
                out.setBaseQualities(SAMRecord.NULL_QUALS);
            }
        }

        TextTagCodec tagCodec = new TextTagCodec();
        for (int i = NUM_REQUIRED_FIELDS; i < numFields; ++i) {
            Map.Entry<String, Object> entry;
            try {
                entry = tagCodec.decode(fields[i]);
            } catch (SAMFormatException e) {
                throw new IllegalArgumentException("Unable to decode field \"" + fields[i] + "\"", e);
            }
            if (entry != null) {
                if (entry.getValue() instanceof TagValueAndUnsignedArrayFlag) {
                    final TagValueAndUnsignedArrayFlag valueAndFlag = (TagValueAndUnsignedArrayFlag) entry.getValue();
                    if (valueAndFlag.isUnsignedArray) {
                        out.setUnsignedArrayAttribute(entry.getKey(), valueAndFlag.value);
                    } else {
                        out.setAttribute(entry.getKey(), valueAndFlag.value);
                    }
                } else {
                    out.setAttribute(entry.getKey(), entry.getValue());
                }
            }
        }

        return out;
    }

}
