package org.opencb.biodata.tools.alignment.converters;

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import ga4gh.Common;
import ga4gh.Reads;
import htsjdk.samtools.*;
import htsjdk.samtools.util.StringUtil;
import org.opencb.biodata.tools.alignment.AlignmentUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToProtoReadAlignmentConverter extends AlignmentConverter<Reads.ReadAlignment> {

    private static final String FIELD_SEPARATOR = "\t";

    public SAMRecordToProtoReadAlignmentConverter() {
        this(true);
    }

    public SAMRecordToProtoReadAlignmentConverter(boolean adjustQuality) {
        this.adjustQuality = adjustQuality;
    }

    @Override
    public Reads.ReadAlignment to(SAMRecord in) {

        Reads.ReadAlignment.Builder readBuilder = Reads.ReadAlignment.newBuilder();

        // id
        readBuilder.setId(in.getReadName());

        // read group id
        if (in.getReadGroup() != null) {
            readBuilder.setReadGroupId(in.getReadGroup().getId());
        } else {
            readBuilder.setReadGroupId("no-group");
        }

        // reference name
        readBuilder.setFragmentName(in.getReferenceName());

        // the read is mapped in a proper pair
        // TODO: Check again !
        readBuilder.setImproperPlacement(!in.getReadPairedFlag() || !in.getProperPairFlag());

        // the read is either a PCR duplicate or an optical duplicate.
        readBuilder.setDuplicateFragment(in.getDuplicateReadFlag());

        // the number of reads in the fragment (extension to SAM flag 0x1)
        readBuilder.setNumberReads(in.getReadPairedFlag() ? 2 : 1);

        // the observed length of the fragment, equivalent to TLEN in SAM
        readBuilder.setFragmentLength(in.getReadPairedFlag() ? in.getInferredInsertSize() : 0);

        // The read number in sequencing. 0-based and less than numberReads.
        // This field replaces SAM flag 0x40 and 0x80
        if (in.getReadPairedFlag() && in.getSecondOfPairFlag()) {
            readBuilder.setReadNumber(readBuilder.getNumberReads() - 1);
        }

        // the read fails platform/vendor quality checks
        readBuilder.setFailedVendorQualityChecks(in.getReadFailsVendorQualityCheckFlag());

        // alignment
        Reads.LinearAlignment.Builder linearAlignment = Reads.LinearAlignment.newBuilder();
        Common.Position.Builder position = Common.Position.newBuilder();
        position.setPosition((long) in.getAlignmentStart() - 1); // from 1-based to 0-based
        position.setReferenceName(in.getReferenceName());
//        position.setSequenceId("");
        position.setStrand(in.getReadNegativeStrandFlag() ? Common.Strand.NEG_STRAND : Common.Strand.POS_STRAND);

        linearAlignment.setPosition(position);
        linearAlignment.setMappingQuality(in.getMappingQuality());

        List<Common.CigarUnit> cigar = new ArrayList<>();
        for (CigarElement e: in.getCigar().getCigarElements()) {
            Common.CigarUnit.Builder op = Common.CigarUnit.newBuilder();
            switch (e.getOperator()) {
                case M:
                    op.setOperation(Common.CigarUnit.Operation.ALIGNMENT_MATCH);
                    break;
                case I:
                    op = op.setOperation(Common.CigarUnit.Operation.INSERT);
                    break;
                case D:
                    op = op.setOperation(Common.CigarUnit.Operation.DELETE);
                    break;
                case N:
                    op = op.setOperation(Common.CigarUnit.Operation.SKIP);
                    break;
                case S:
                    op = op.setOperation(Common.CigarUnit.Operation.CLIP_SOFT);
                    break;
                case H:
                    op = op.setOperation(Common.CigarUnit.Operation.CLIP_HARD);
                    break;
                case P:
                    op = op.setOperation(Common.CigarUnit.Operation.PAD);
                    break;
                case EQ:
                    op = op.setOperation(Common.CigarUnit.Operation.SEQUENCE_MATCH);
                    break;
                case X:
                    op = op.setOperation(Common.CigarUnit.Operation.SEQUENCE_MISMATCH);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized CigarOperator: " + e);
            }
            op.setOperationLength((long) e.getLength());
            cigar.add(op.build());
        }
        linearAlignment.addAllCigar(cigar);
        readBuilder.setAlignment(linearAlignment);

        // the read is the second read in a pair
        readBuilder.setSecondaryAlignment(in.getSupplementaryAlignmentFlag());

        // the alignment is supplementary
        readBuilder.setSupplementaryAlignment(in.getSupplementaryAlignmentFlag());

        // read sequence
        readBuilder.setAlignedSequence(in.getReadString());

        // aligned quality
        byte[] baseQualities = in.getBaseQualities();
        int size = baseQualities.length;
        List<Integer> alignedQuality = new ArrayList<>(size);
        if (adjustQuality) {
            for (byte baseQuality : baseQualities) {
                int adjustedQuality = AlignmentUtils.adjustQuality(baseQuality);
                alignedQuality.add(adjustedQuality);
            }
        } else {
            for (byte baseQuality : baseQualities) {
                alignedQuality.add((int) baseQuality);
            }
        }
        readBuilder.addAllAlignedQuality(alignedQuality);

        // next mate position
        if (in.getReadPairedFlag()) {
            Common.Position.Builder nextMatePosition = Common.Position.newBuilder();
            nextMatePosition.setPosition((long) in.getMateAlignmentStart());
            nextMatePosition.setReferenceName(in.getMateReferenceName());
//            nextMatePosition.setSequenceId("");
            nextMatePosition.setStrand(in.getMateNegativeStrandFlag() ? Common.Strand.NEG_STRAND : Common.Strand.POS_STRAND);
            readBuilder.setNextMatePosition(nextMatePosition.build());
        }

        // A map of additional read alignment information.
        Map<String, ListValue> info = new HashMap<>();
        List<SAMRecord.SAMTagAndValue> attributes = in.getAttributes();
        for (SAMRecord.SAMTagAndValue tv : attributes) {
            ListValue.Builder list = ListValue.newBuilder();
            if (tv.value instanceof Character || tv.value instanceof String) {
                list.addValues(Value.newBuilder().setStringValue("Z").build());
//                list.addValues(Value.newBuilder().setStringValue("" + tv.value));
            } else if (tv.value instanceof Float) {
                list.addValues(Value.newBuilder().setStringValue("f"));
//                list.addValues(Value.newBuilder().setNumberValue((float) tv.value));
            } else {
                list.addValues(Value.newBuilder().setStringValue("i"));
//                list.addValues(Value.newBuilder().setNumberValue((int) tv.value));
            }
            list.addValues(Value.newBuilder().setStringValue("" + tv.value));
            info.put(tv.tag, list.build());
        }
        readBuilder.putAllInfo(info);

        return readBuilder.build();
    }

    @Override
    public SAMRecord from(Reads.ReadAlignment in) {
        final String samLine = getSamString(in);

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

        SAMRecord out = createFromFields(fields);

        TextTagCodec tagCodec = new TextTagCodec();
        for (int i = NUM_REQUIRED_FIELDS; i < numFields; ++i) {
            Map.Entry<String, Object> entry = null;
            try {
                entry = tagCodec.decode(fields[i]);
            } catch (SAMFormatException e) {
                throw new IllegalArgumentException("Unable to decode field \"" + fields[i] + "\"", e);
            }
            if (entry != null) {
                if (entry.getValue() instanceof TagValueAndUnsignedArrayFlag) {
                    final TagValueAndUnsignedArrayFlag valueAndFlag =
                            (TagValueAndUnsignedArrayFlag) entry.getValue();
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

    private String getSamString(Reads.ReadAlignment ra) {
        StringBuilder res = new StringBuilder();
        Reads.LinearAlignment la = ra.getAlignment();

        // id
        res.append(ra.getId().toString()).append(FIELD_SEPARATOR);

        // flags
        int flags = 0;
        if (ra.getNumberReads() > 0) {
            flags |= 0x1;
        }
        // TODO Check this line, it was getProperPlacement() before
        if (!ra.getImproperPlacement()) {
            flags |= 0x2;
        }
        if (la == null) {
            flags |= 0x4;
        } else {
            if (la.getPosition().getStrand() == Common.Strand.NEG_STRAND) {
                flags |= 0x10;
            }
        }
        if (ra.getNextMatePosition() != null) {
            if (ra.getNextMatePosition().getStrand() == Common.Strand.NEG_STRAND) {
                flags |= 0x20;
            }
        } else {
            if (ra.getNumberReads() > 0) {
                flags |= 0x8;
            }
        }
        if (ra.getReadNumber() == 0) {
            flags |= 0x40;
        }
        if (ra.getNumberReads() > 0 && ra.getReadNumber() == ra.getNumberReads() - 1) {
            flags |= 0x80;
        }
        if (ra.getSecondaryAlignment()) {
            flags |= 0x100;
        }
        if (ra.getFailedVendorQualityChecks()) {
            flags |= 0x200;
        }
        if (ra.getDuplicateFragment()) {
            flags |= 0x400;
        }
        res.append(flags);
        res.append(FIELD_SEPARATOR);

        if (la == null) {
            res.append("*").append(FIELD_SEPARATOR);        // chromosome
            res.append("0").append(FIELD_SEPARATOR);        // position
            res.append("0").append(FIELD_SEPARATOR);        // mapping quality
            res.append(ra.getAlignedSequence().length()).append("M").append(FIELD_SEPARATOR);    // cigar
        } else {
            // chromosome
            res.append(la.getPosition().getReferenceName());
            res.append(FIELD_SEPARATOR);

            // position
            res.append(la.getPosition().getPosition() + 1); //0-based to 1-based
            res.append(FIELD_SEPARATOR);

            // mapping quality
            res.append(la.getMappingQuality());
            res.append(FIELD_SEPARATOR);

            // cigar
            for (Common.CigarUnit e : la.getCigarList()) {
                res.append(e.getOperationLength());
                switch (e.getOperation()) {
                    case ALIGNMENT_MATCH:
                        res.append("M");
                        break;
                    case INSERT:
                        res.append("I");
                        break;
                    case DELETE:
                        res.append("D");
                        break;
                    case SKIP:
                        res.append("N");
                        break;
                    case CLIP_SOFT:
                        res.append("S");
                        break;
                    case CLIP_HARD:
                        res.append("H");
                        break;
                    case PAD:
                        res.append("P");
                        break;
                    case SEQUENCE_MATCH:
                        res.append("=");
                        break;
                    case SEQUENCE_MISMATCH:
                        res.append("X");
                        break;
                    default:
                        break;
                }
            }
            res.append(FIELD_SEPARATOR);
        }

        // mate chromosome
        if (ra.getNextMatePosition() != null) {
            if (la != null && ra.getNextMatePosition().getReferenceName().equals(la.getPosition().getReferenceName())) {
                res.append("=");
            } else {
                res.append(ra.getNextMatePosition().getReferenceName());
            }
        } else {
            res.append("*");
        }
        res.append(FIELD_SEPARATOR);

        // mate position
        if (ra.getNextMatePosition() != null) {
            res.append(ra.getNextMatePosition().getPosition());
        } else {
            res.append(0);
        }
        res.append(FIELD_SEPARATOR);

        // tlen
        res.append(ra.getFragmentLength());
        res.append(FIELD_SEPARATOR);

        // sequence
        res.append(ra.getAlignedSequence().toString());
        res.append(FIELD_SEPARATOR);

        // quality
        for (int v: ra.getAlignedQualityList()) {
            res.append((char) (v + 33)); // Add ASCII offset
        }

        // optional fields
        for (CharSequence key: ra.getInfo().keySet()) {
            res.append(FIELD_SEPARATOR);
            res.append(key.toString());
            for (Value val : ra.getInfo().get(key).getValuesList()) {
                switch (val.getKindCase()) {
                    case NUMBER_VALUE:
                        res.append((":" + val.getNumberValue()));
                        break;
                    case STRING_VALUE:
                    default:
                        res.append((":" + val.getStringValue()));
                        break;
                }
            }
        }

        return res.toString();
    }
}
