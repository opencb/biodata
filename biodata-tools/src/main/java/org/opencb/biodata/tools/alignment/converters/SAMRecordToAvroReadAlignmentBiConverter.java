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

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;
import org.ga4gh.models.*;
import org.opencb.biodata.tools.alignment.BamUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToAvroReadAlignmentBiConverter extends AlignmentBiConverter<ReadAlignment> {

    private static final String FIELD_SEPARATOR = "\t";

    public SAMRecordToAvroReadAlignmentBiConverter() {
        adjustQuality = true;
    }

    public SAMRecordToAvroReadAlignmentBiConverter(boolean adjustQuality) {
        this.adjustQuality = adjustQuality;
    }


    @Override
    public ReadAlignment to(SAMRecord in) {
        //id
        String id = in.getReadName();

        // read group id
        String readGroupId;
        if (in.getReadGroup() != null) {
            readGroupId = in.getReadGroup().getId();
        } else {
            readGroupId = "no-group";
        }

        // reference name
        String fragmentName = in.getReferenceName();

        // the read is mapped in a proper pair
        boolean properPlacement = in.getReadPairedFlag() && in.getProperPairFlag();

        // the read is either a PCR duplicate or an optical duplicate.
        boolean duplicateFragment = in.getDuplicateReadFlag();

        // the number of reads in the fragment (extension to SAM flag 0x1)
        int numberReads = in.getReadPairedFlag() ? 2 : 1;

        // the observed length of the fragment, equivalent to TLEN in SAM
        int fragmentLength = in.getReadPairedFlag() ? in.getInferredInsertSize() : 0;

        // The read number in sequencing. 0-based and less than numberReads.
        // This field replaces SAM flag 0x40 and 0x80
        int readNumber = 0;
        if (in.getReadPairedFlag() && in.getSecondOfPairFlag()) {
            readNumber = numberReads - 1;
        }

        // the read fails platform/vendor quality checks
        boolean failedVendorQualityChecks = in.getReadFailsVendorQualityCheckFlag();

        // alignment
        Position position = new Position();
        position.setPosition((long) in.getAlignmentStart());
        position.setReferenceName(in.getReferenceName());
//        position.setSequenceId("");
        position.setStrand(in.getReadNegativeStrandFlag() ? Strand.NEG_STRAND : Strand.POS_STRAND);
        int mappingQuality = in.getMappingQuality();

        List<CigarUnit> cigar = new ArrayList<>();
        for (CigarElement e: in.getCigar().getCigarElements()) {
            CigarOperation op;
            switch (e.getOperator()) {
                case M:
                    op = CigarOperation.ALIGNMENT_MATCH;
                    break;
                case I:
                    op = CigarOperation.INSERT;
                    break;
                case D:
                    op = CigarOperation.DELETE;
                    break;
                case N:
                    op = CigarOperation.SKIP;
                    break;
                case S:
                    op = CigarOperation.CLIP_SOFT;
                    break;
                case H:
                    op = CigarOperation.CLIP_HARD;
                    break;
                case P:
                    op = CigarOperation.PAD;
                    break;
                case EQ:
                    op = CigarOperation.SEQUENCE_MATCH;
                    break;
                case X:
                    op = CigarOperation.SEQUENCE_MISMATCH;
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized CigarOperator: " + e);
            }
            cigar.add(new CigarUnit(op, (long) e.getLength(), null));
        }
        LinearAlignment alignment = new LinearAlignment(position, mappingQuality, cigar);

        // the read is the second read in a pair
        boolean secondaryAlignment = in.getSupplementaryAlignmentFlag();

        // the alignment is supplementary
        boolean supplementaryAlignment = in.getSupplementaryAlignmentFlag();

        // read sequence
        String alignedSequence = in.getReadString();

        // aligned quality
        byte[] baseQualities = in.getBaseQualities();
        int size = baseQualities.length;
        List<Integer> alignedQuality = new ArrayList<>(size);
        if (adjustQuality) {
            for (byte baseQuality : baseQualities) {
                int adjustedQuality = BamUtils.adjustQuality(baseQuality);
                alignedQuality.add(adjustedQuality);
            }
        } else {
            for (byte baseQuality : baseQualities) {
                alignedQuality.add((int) baseQuality);
            }
        }

        // next mate position
        Position nextMatePosition = null;
        if (in.getReadPairedFlag()) {
            nextMatePosition = new Position();
            nextMatePosition.setPosition((long) in.getMateAlignmentStart());
            nextMatePosition.setReferenceName(in.getMateReferenceName());
//            nextMatePosition.setSequenceId("");
            nextMatePosition.setStrand(in.getMateNegativeStrandFlag() ? Strand.NEG_STRAND : Strand.POS_STRAND);
        }

        // A map of additional read alignment information.
        Map<String, List<String>> info = new HashMap<>();
        List<SAMRecord.SAMTagAndValue> attributes = in.getAttributes();
        for (SAMRecord.SAMTagAndValue tv : attributes) {
            List<String> list = new ArrayList<>();
            if (tv.value instanceof Integer) {
                list.add("i");
            } else if (tv.value instanceof Float) {
                list.add("f");
            } else {
                list.add("Z");
            }
            list.add("" + tv.value);
            info.put(tv.tag, list);
        }

        ReadAlignment out = new ReadAlignment(id, readGroupId, fragmentName, !properPlacement, duplicateFragment,
                numberReads, fragmentLength, readNumber, failedVendorQualityChecks, alignment, secondaryAlignment,
                supplementaryAlignment, alignedSequence, alignedQuality, nextMatePosition, info);

        return out;
    }

    @Override
    public SAMRecord from(ReadAlignment in) {
        final String samLine = getSamString(in);
        return super.from(samLine);
    }

    private String getSamString(ReadAlignment ra) {
        StringBuilder res = new StringBuilder();
        LinearAlignment la = ra.getAlignment();

        // id
        res.append(ra.getId()).append(FIELD_SEPARATOR);

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
            if (la.getPosition().getStrand() == Strand.NEG_STRAND) {
                flags |= 0x10;
            }
        }
        if (ra.getNextMatePosition() != null) {
            if (ra.getNextMatePosition().getStrand() == Strand.NEG_STRAND) {
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
            res.append(la.getPosition().getPosition());
            res.append(FIELD_SEPARATOR);

            // mapping quality
            res.append(la.getMappingQuality());
            res.append(FIELD_SEPARATOR);

            // cigar
            for (CigarUnit e : la.getCigar()) {
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
        res.append(ra.getAlignedSequence());
        res.append(FIELD_SEPARATOR);

        // quality
        for (int v: ra.getAlignedQuality()) {
            res.append((char) (v + 33)); // Add ASCII offset
        }

        // optional fields
        for (CharSequence key: ra.getInfo().keySet()) {
            res.append(FIELD_SEPARATOR);
            res.append(key.toString());
            for (CharSequence val : ra.getInfo().get(key)) {
                res.append((":" + val.toString()));
            }
        }

        return res.toString();
    }

}
