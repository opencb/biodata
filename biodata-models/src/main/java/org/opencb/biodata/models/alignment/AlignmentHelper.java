package org.opencb.biodata.models.alignment;

import java.util.LinkedList;
import java.util.List;
import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMRecord;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cgonzalez@cipf.es>
 */
public class AlignmentHelper {
    
    /**
     * Given a cigar string, returns a list of alignment differences with 
     * the reference sequence.
     * 
     * @param cigar The input cigar string
     * @return The list of alignment differences
     */
    public static List<Alignment.AlignmentDifference> getDifferencesFromCigar(SAMRecord record, String refStr) {
        List<Alignment.AlignmentDifference> differences = new LinkedList<>();
        
        int index = 0, indexRef = 0, indexMismatchBlock = 0;
//        System.out.println("align start = " + record.getAlignmentStart() + 
//                "\t1st block start = " + record.getAlignmentBlocks().get(0).getReferenceStart() + 
//                "\n*****\n" + refStr + "\n" + record.getReadString());
        
        for (CigarElement element : record.getCigar().getCigarElements()) {
            int cigarLen = element.getLength();
            String subref = null, subread = null;
            Alignment.AlignmentDifference currentDifference = null;

            switch (element.getOperator()) {
                case M:
                case EQ:
                case X:
                    AlignmentBlock blk = record.getAlignmentBlocks().get(indexMismatchBlock);
                    int realStart = blk.getReferenceStart() - record.getAlignmentStart();
                    // Picard ignores hard clipping, the indices could be neccessary
                    indexRef = realStart >= indexRef ? realStart : indexRef;
                    subref = refStr.substring(indexRef, indexRef + blk.getLength());
                    subread = record.getReadString().substring(index, Math.min(index + blk.getLength(), record.getReadString().length()));
                    differences.addAll(getMismatchDiff(subref, subread, indexRef));
                    
                    index = index + record.getAlignmentBlocks().get(indexMismatchBlock).getLength();
                    indexRef = indexRef + record.getAlignmentBlocks().get(indexMismatchBlock).getLength();
                    indexMismatchBlock++;
                    break;
                case I:
                    if (cigarLen < 30) {
                        subread = record.getReadString().substring(index, index + cigarLen);
                    } else { // Get only first 30 characters in the sequence to copy
                        subread = subread.substring(index, index + 30).concat("...");
                    }
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.INSERTION, subread);
                    index = index + cigarLen;
                    break;
                case D:
                    if (cigarLen < 30) {
                        subref = refStr.substring(indexRef, indexRef + cigarLen);
                    } else { // Get only first 30 characters in the sequence to copy
                        subref = refStr.substring(indexRef, indexRef + 30).concat("...");
                    }
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.DELETION, subref);
                    indexRef = indexRef + cigarLen;
                    break;
                case N:
                    if (cigarLen < 30) {
                        subref = refStr.substring(indexRef, indexRef + cigarLen);
                    } else { // Get only first 30 characters in the sequence to copy
                        subref = refStr.substring(indexRef, indexRef + 30).concat("...");
                    }
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.SKIPPED_REGION, subref);
                    indexRef = indexRef + cigarLen;
                    break;
                case S:
                    subread = record.getReadString().substring(index, index + cigarLen);
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.SOFT_CLIPPING, subread);
                    index = index + cigarLen;
                    indexRef = indexRef + cigarLen;
                    break;
                case H:
                    subref = refStr.substring(indexRef, Math.min(indexRef + cigarLen, refStr.length()));
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.HARD_CLIPPING, subref);
                    indexRef = indexRef + cigarLen;
                    break;
                case P:
//                    subref = refStr.substring(indexRef, indexRef + cigarLen);
                    currentDifference = new Alignment.AlignmentDifference(indexRef, Alignment.AlignmentDifference.PADDING, "");
//                    indexRef = indexRef + cigarLen;
                    break;
            }
            
            if (currentDifference != null) {
                differences.add(currentDifference);
            }
        }
        
        return differences;
    }
    
    
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
    
}
