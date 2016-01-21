/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.alignment;

import htsjdk.samtools.*;
import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.exceptions.ShortReferenceSequenceException;

import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cgonzalez@cipf.es&gt;
 */
public class AlignmentUtils {


    /**
     * Given a cigar string, returns a list of alignment differences with
     * the reference sequence.
     *
     * TODO: Implement throw ShortReferenceSequenceException
     * TODO: Use MD:Z:XXXX SAM reader attribute
     * TODO: Store original CIGAR
     *
     * @param record The input cigar string
     * @param refStr reference sequence.
     * @param maxStoredSequence Max length for stored sequences. Default = 30
     * @return The list of alignment differences
     */
    public static List<Alignment.AlignmentDifference> getDifferencesFromCigar(SAMRecord record, String refStr, int maxStoredSequence) {
        List<Alignment.AlignmentDifference> differences = new LinkedList<>();

        int index = 0, indexRef = 0, indexMismatchBlock = 0, realStart;
        AlignmentBlock blk;
//        System.out.println("align start = " + record.getAlignmentStart() +
//                "\t1st block start = " + record.getAlignmentBlocks().get(0).getReferenceStart() +
//                "\n*****\n" + refStr + "\n" + record.getReadString());

        if ((record.getFlags() & Alignment.SEGMENT_UNMAPPED) != 0) {   // umnmapped, return the read as MATCH_MISMATCH
            Alignment.AlignmentDifference alignmentDifference = new Alignment.AlignmentDifference(
                    0, Alignment.AlignmentDifference.MATCH_MISMATCH, record.getReadString());
            differences.add(alignmentDifference);
            return differences;
        }


        for (CigarElement element : record.getCigar().getCigarElements()) {
            int cigarLen = element.getLength();
            String subref = null, subread = null;
            Alignment.AlignmentDifference currentDifference = null;

            switch (element.getOperator()) {
                case EQ:
                    blk = record.getAlignmentBlocks().get(indexMismatchBlock);
                    realStart = blk.getReferenceStart() - record.getAlignmentStart();
                    // Picard ignores hard clipping, the indices could be necessary
                    indexRef = realStart >= indexRef ? realStart : indexRef;

                    index = index + record.getAlignmentBlocks().get(indexMismatchBlock).getLength();
                    indexRef = indexRef + record.getAlignmentBlocks().get(indexMismatchBlock).getLength();
                    indexMismatchBlock++;
                    break;
                case M:
                case X:
                    blk = record.getAlignmentBlocks().get(indexMismatchBlock);
                    realStart = blk.getReferenceStart() - record.getAlignmentStart();
                    // Picard ignores hard clipping, the indices could be necessary
                    indexRef = realStart >= indexRef ? realStart : indexRef;
                    subread = record.getReadString().substring(index, Math.min(index + blk.getLength(), record.getReadString().length()));
                    if (refStr == null) {
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.MATCH_MISMATCH, cigarLen);
                        currentDifference.setSeq(subread);
                    } else {
                        subref = refStr.substring(indexRef, indexRef + blk.getLength());
                        differences.addAll(getMismatchDiff(subref, subread, indexRef));
                    }
                    index = index + record.getAlignmentBlocks().get(indexMismatchBlock).getLength();
                    indexRef = indexRef + record.getAlignmentBlocks().get(indexMismatchBlock).getLength();
                    indexMismatchBlock++;
                    break;
                case I:
                    if (cigarLen < maxStoredSequence) {
                        subread = record.getReadString().substring(index, index + cigarLen);
                    } else { // Get only first 30 characters in the sequence to copy
                        subread = record.getReadString().substring(index, index + maxStoredSequence-3).concat("...");
                    }
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.INSERTION, subread, cigarLen);
                    index = index + cigarLen;
                    break;
                case D:
                    if (refStr == null) {
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.DELETION, cigarLen);
                    } else {
                        if (cigarLen < maxStoredSequence) {
                            subref = refStr.substring(indexRef, indexRef + cigarLen);
                        } else { // Get only first 30 characters in the sequence to copy
                            subref = refStr.substring(indexRef, indexRef + maxStoredSequence-3).concat("...");
                        }
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.DELETION, subref, cigarLen);
                    }
                    indexRef = indexRef + cigarLen;
                    break;
                case N:
                    if (refStr == null) {
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.SKIPPED_REGION, cigarLen);
                    } else {
                        if (cigarLen < maxStoredSequence) {
                            subref = refStr.substring(indexRef, indexRef + cigarLen);
                        } else { // Get only first 30 characters in the sequence to copy
                            subref = refStr.substring(indexRef, indexRef + maxStoredSequence-3).concat("...");
                        }
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.SKIPPED_REGION, subref, cigarLen);
                    }
                    indexRef = indexRef + cigarLen;
                    break;
                case S:
                    subread = record.getReadString().substring(index, index + cigarLen);


                    if (refStr == null || index+cigarLen > refStr.length()) {
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.SOFT_CLIPPING, subread);
                    } else {
                        subref = refStr.substring(index, index+cigarLen);
                        if(subread.equals(subref)){
                            currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.SOFT_CLIPPING, cigarLen);
                        } else {
                            currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.SOFT_CLIPPING, subread);
                        }
                    }
                    index = index + cigarLen;
                    indexRef = indexRef + cigarLen;
                    break;
                case H:
                    if (refStr == null) {
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.HARD_CLIPPING, cigarLen);
                    } else {
                        subref = refStr.substring(indexRef, Math.min(indexRef + cigarLen, refStr.length()));
                        currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.HARD_CLIPPING, subref);
                    }
                    indexRef = indexRef + cigarLen;
                    break;
                case P:
//                  subref = refStr.substring(indexRef, indexRef + cigarLen);
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.PADDING, cigarLen);

//                  indexRef = indexRef + cigarLen;
                    break;
            }

            if (currentDifference != null) {
                differences.add(currentDifference);
            }
        }

        return differences;
    }
    public static List<Alignment.AlignmentDifference> getDifferencesFromCigar(SAMRecord record, String refStr) {
        return getDifferencesFromCigar(record, refStr, 30);
    }

    /**
     * Compares all differences with the referenceSequence in order to reduce the stored sequence.
     * Also adds sequence for deletion differences.
     *
     * @param alignment The Alignment
     * @param referenceSequence Reference sequence
     * @param referenceSequenceStart Reference sequence start
     * @throws org.opencb.biodata.models.alignment.exceptions.ShortReferenceSequenceException
     */
    public static void completeDifferencesFromReference(Alignment alignment, String referenceSequence, long referenceSequenceStart) throws ShortReferenceSequenceException {
        int offset = (int) (alignment.getUnclippedStart() - referenceSequenceStart);
        String subRef;
        String subRead;

        if ((alignment.getFlags() & Alignment.SEGMENT_UNMAPPED) != 0) {   // umnmapped, return as is
            return;
        }

        List<Alignment.AlignmentDifference> newDifferences =  new LinkedList<>();
        for(Alignment.AlignmentDifference alignmentDifference : alignment.getDifferences()){
            Alignment.AlignmentDifference currentDifference = null;

            switch (alignmentDifference.getOp()){
                case Alignment.AlignmentDifference.DELETION:
                    //If is a deletion, there is no seq.
                    try{
                        if(!alignmentDifference.isAllSequenceStored()){
                            subRef = referenceSequence.substring(
                                    alignmentDifference.getPos() + offset,
                                    alignmentDifference.getPos() + offset + alignmentDifference.getLength()
                            );
                            alignmentDifference.setSeq( subRef );
                        }
                    } catch (StringIndexOutOfBoundsException e){
                        throw new ShortReferenceSequenceException("ReferenceSequence Out of Bounds in Alignment.completeDifferences()");
                    }
                    currentDifference = alignmentDifference;
                    break;
                case Alignment.AlignmentDifference.MATCH_MISMATCH:
                case Alignment.AlignmentDifference.MISMATCH:
                    //
                    try{
                        subRef = referenceSequence.substring(
                                alignmentDifference.getPos() + offset,
                                alignmentDifference.getPos() + offset + alignmentDifference.getLength()
                        );
                    } catch (StringIndexOutOfBoundsException e){
                        throw new ShortReferenceSequenceException("ReferenceSequence Out of Bounds in Alignment.completeDifferences()");
                    }
                    subRead = alignmentDifference.getSeq();
                    newDifferences.addAll(getMismatchDiff(subRef, subRead, alignmentDifference.getPos()));
                    break;
                case Alignment.AlignmentDifference.HARD_CLIPPING:
                   /* subRef = referenceSequence.substring(
                            alignmentDifference.getPos() + offset,
                            alignmentDifference.getPos() + offset + alignmentDifference.getLength()
                    );
                    alignmentDifference.setSeq(subRef);*/
                    currentDifference = alignmentDifference;
                    break;
                case Alignment.AlignmentDifference.SOFT_CLIPPING:
                    currentDifference = alignmentDifference;
                    try{
                        if(alignmentDifference.isAllSequenceStored()){
                            subRef = referenceSequence.substring(
                                    alignmentDifference.getPos() + offset,
                                    alignmentDifference.getPos() + offset + alignmentDifference.getLength()
                            );
                            if(subRef.equals(alignmentDifference.getSeq())){
                                currentDifference.setSeq(null);
                            }
                        }
                    } catch (StringIndexOutOfBoundsException e){
                        //This Soft clipping difference will not be compacted. It's not a bug, just a feature.
                        //TODO: Check for 2.0 version
                    }

                    break;
                //offset -= alignmentDifference.getLength();
                case Alignment.AlignmentDifference.INSERTION:
                case Alignment.AlignmentDifference.PADDING:
                case Alignment.AlignmentDifference.SKIPPED_REGION:
                    //
                    currentDifference = alignmentDifference;
                    break;
            }

            if(currentDifference != null){
                newDifferences.add(currentDifference);
            }
        }
        alignment.setDifferences(newDifferences);
    }

    /**
     *
     * @param referenceSequence
     * @param readSequence
     * @param baseIndex Position of the subSequence inside the whole sequence
     * @return
     */
    private static List<Alignment.AlignmentDifference> getMismatchDiff(String referenceSequence, String readSequence, int baseIndex) {
        List<Alignment.AlignmentDifference> differences = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        int foundIndex = 0;
        for (int i = 0; i < Math.min(referenceSequence.length(), readSequence.length()); i++) {
            if (referenceSequence.charAt(i) != readSequence.charAt(i)) {
                if (sb.length() == 0) {
                    foundIndex = i;
                }
                sb.append(readSequence.charAt(i));
            } else {
                if (sb.length() > 0) {
                    Alignment.AlignmentDifference difference =
                            new Alignment.AlignmentDifference(baseIndex + foundIndex, Alignment.AlignmentDifference.MISMATCH, sb.toString());
                    differences.add(difference);
                    sb.setLength(0);
                }
            }
        }

        // If a mismatch was found at the end, it can't be appended inside the loop
        if (sb.length() > 0) {
            Alignment.AlignmentDifference difference =
                    new Alignment.AlignmentDifference(baseIndex + foundIndex, Alignment.AlignmentDifference.MISMATCH, sb.toString());
            differences.add(difference);
        }

        return differences;
    }



    public static String getSequenceFromDifferences(List<Alignment.AlignmentDifference> differences, int sequenceSize, String referenceSequence) throws ShortReferenceSequenceException {
        return getSequenceFromDifferences(differences, sequenceSize, referenceSequence, null , 0);
    }
    public static String getSequenceFromDifferences(List<Alignment.AlignmentDifference> differences, int sequenceSize, String referenceSequence, final int offset) throws ShortReferenceSequenceException {
        return getSequenceFromDifferences(differences, sequenceSize, referenceSequence, null , offset);
    }
    public static String getSequenceFromDifferences(List<Alignment.AlignmentDifference> differences, int sequenceSize, String referenceSequence, Cigar cigar) throws ShortReferenceSequenceException {
        return getSequenceFromDifferences(differences, sequenceSize, referenceSequence, cigar, 0);
    }
    public static String getSequenceFromDifferences(List<Alignment.AlignmentDifference> differences, int sequenceSize, String referenceSequence, Cigar cigar, final int offset) throws ShortReferenceSequenceException {
        String sequence = "";
        String subSeq;
        int index = 0;
        int indexRef = offset;
        if(cigar == null){
            cigar = new Cigar();    //Will be calculated, but not returned.
        }
        try {
            for(Alignment.AlignmentDifference alignmentDifference : differences){

                if(indexRef - offset < alignmentDifference.getPos()){
                    subSeq = referenceSequence.substring(indexRef, offset+alignmentDifference.getPos());
                    sequence += subSeq;
                    indexRef += subSeq.length();
                    index    += subSeq.length();
                    cigar.add(new CigarElement(subSeq.length(), CigarOperator.EQ));
                } else if(indexRef - offset > alignmentDifference.getPos()) {
                    System.out.println("[ERROR] BAD DIFFERENCES ");
                }


                switch (alignmentDifference.getOp()){
                    case Alignment.AlignmentDifference.INSERTION:
                        cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.INSERTION));

                        if(alignmentDifference.isAllSequenceStored()){
                            sequence += alignmentDifference.getSeq();
                        } else {
                            System.out.println("[WARNING] Missing insertion information");
                            for(int i = 0; i < alignmentDifference.getLength(); i++){
                                sequence += '*';
                            }
                        }
                        index += alignmentDifference.getLength();
                        break;

                    case Alignment.AlignmentDifference.DELETION:
                        cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.DELETION));
                        indexRef += alignmentDifference.getLength();
                        break;

                    case Alignment.AlignmentDifference.MATCH_MISMATCH:
                    case Alignment.AlignmentDifference.MISMATCH:
                        if(alignmentDifference.getOp() == Alignment.AlignmentDifference.MATCH_MISMATCH){
                            cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.M));
                        } else {
                            cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.X));
                        }
                        if(alignmentDifference.isAllSequenceStored()){
                            sequence += alignmentDifference.getSeq();
                        } else {
                            sequence += referenceSequence.substring(indexRef, indexRef+alignmentDifference.getLength());
                        }
                        indexRef += alignmentDifference.getLength();
                        index += alignmentDifference.getLength();
                        break;

                    case Alignment.AlignmentDifference.SOFT_CLIPPING:
                        cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.SOFT_CLIP));
                        if(alignmentDifference.isAllSequenceStored()) {
                            sequence += alignmentDifference.getSeq();
                        } else {
                            sequence += referenceSequence.substring(indexRef, indexRef+alignmentDifference.getLength());
                        }

                        indexRef += alignmentDifference.getLength();
                        index += alignmentDifference.getLength();
                        break;

                    case Alignment.AlignmentDifference.HARD_CLIPPING:
                        cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.HARD_CLIP));
                        indexRef += alignmentDifference.getLength();    //Increases the position in reference, but not in sequence. Hard clipping is not stored
                        break;

                    case Alignment.AlignmentDifference.SKIPPED_REGION:
                        cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.SKIPPED_REGION));
                        indexRef += alignmentDifference.getLength();
                        break;
                    case Alignment.AlignmentDifference.PADDING:
                        cigar.add(new CigarElement(alignmentDifference.getLength(), CigarOperator.PADDING));
                        break;
                }

            }

            if(sequence.length() < sequenceSize){
                subSeq = referenceSequence.substring(indexRef, indexRef + sequenceSize - sequence.length());
                sequence += subSeq;
                cigar.add(new CigarElement(subSeq.length(), CigarOperator.EQ));
            } else if(index > sequenceSize) {
                System.out.println("[ERROR] TOO MUCH DIFFERENCES ");
            }
        } catch (StringIndexOutOfBoundsException e) {
            throw new ShortReferenceSequenceException("ReferenceSequence Out of Bounds in Alignment.getSequenceFromDifferences()");
        }

        return sequence;
    }

}
