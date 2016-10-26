package org.opencb.biodata.tools.alignment;

import htsjdk.samtools.*;
import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;
import org.ga4gh.models.Strand;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by pfurio on 25/10/16.
 */
public class AlignmentUtils {

//    private static final String FIELD_SEPARATOR = "\t";

    protected AlignmentUtils() {
    }

//    public static String getSamString(ReadAlignment ra) {
//        StringBuilder res = new StringBuilder();
//        LinearAlignment la = (LinearAlignment) ra.getAlignment();
//
//        // id
//        res.append(ra.getId().toString()).append(FIELD_SEPARATOR);
//
//        // flags
//        int flags = 0;
//        if (ra.getNumberReads() > 0) {
//            flags |= 0x1;
//        }
//        // TODO Check this line, it was getProperPlacement() before
//        if (!ra.getImproperPlacement()) {
//            flags |= 0x2;
//        }
//        if (la == null) {
//            flags |= 0x4;
//        } else {
//            if (la.getPosition().getStrand() == Strand.NEG_STRAND) {
//                flags |= 0x10;
//            }
//        }
//        if (ra.getNextMatePosition() != null) {
//            if (ra.getNextMatePosition().getStrand() == Strand.NEG_STRAND) {
//                flags |= 0x20;
//            }
//        } else {
//            if (ra.getNumberReads() > 0) {
//                flags |= 0x8;
//            }
//        }
//        if (ra.getReadNumber() == 0) {
//            flags |= 0x40;
//        }
//        if (ra.getNumberReads() > 0 && ra.getReadNumber() == ra.getNumberReads() - 1) {
//            flags |= 0x80;
//        }
//        if (ra.getSecondaryAlignment()) {
//            flags |= 0x100;
//        }
//        if (ra.getFailedVendorQualityChecks()) {
//            flags |= 0x200;
//        }
//        if (ra.getDuplicateFragment()) {
//            flags |= 0x400;
//        }
//        res.append(flags);
//        res.append(FIELD_SEPARATOR);
//
//        if (la == null) {
//            res.append("*").append(FIELD_SEPARATOR);        // chromosome
//            res.append("0").append(FIELD_SEPARATOR);        // position
//            res.append("0").append(FIELD_SEPARATOR);        // mapping quality
//            res.append(ra.getAlignedSequence().length()).append("M").append(FIELD_SEPARATOR);    // cigar
//        } else {
//            // chromosome
//            res.append(la.getPosition().getReferenceName());
//            res.append(FIELD_SEPARATOR);
//
//            // position
//            res.append(la.getPosition().getPosition() + 1); //0-based to 1-based
//            res.append(FIELD_SEPARATOR);
//
//            // mapping quality
//            res.append(la.getMappingQuality());
//            res.append(FIELD_SEPARATOR);
//
//            // cigar
//            for (CigarUnit e : la.getCigar()) {
//                res.append(e.getOperationLength());
//                switch (e.getOperation()) {
//                    case ALIGNMENT_MATCH:
//                        res.append("M");
//                        break;
//                    case INSERT:
//                        res.append("I");
//                        break;
//                    case DELETE:
//                        res.append("D");
//                        break;
//                    case SKIP:
//                        res.append("N");
//                        break;
//                    case CLIP_SOFT:
//                        res.append("S");
//                        break;
//                    case CLIP_HARD:
//                        res.append("H");
//                        break;
//                    case PAD:
//                        res.append("P");
//                        break;
//                    case SEQUENCE_MATCH:
//                        res.append("=");
//                        break;
//                    case SEQUENCE_MISMATCH:
//                        res.append("X");
//                        break;
//                    default:
//                        break;
//                }
//            }
//            res.append(FIELD_SEPARATOR);
//        }
//
//        // mate chromosome
//        if (ra.getNextMatePosition() != null) {
//            if (la != null && ra.getNextMatePosition().getReferenceName().equals(la.getPosition().getReferenceName())) {
//                res.append("=");
//            } else {
//                res.append(ra.getNextMatePosition().getReferenceName());
//            }
//        } else {
//            res.append("*");
//        }
//        res.append(FIELD_SEPARATOR);
//
//        // mate position
//        if (ra.getNextMatePosition() != null) {
//            res.append(ra.getNextMatePosition().getPosition());
//        } else {
//            res.append(0);
//        }
//        res.append(FIELD_SEPARATOR);
//
//        // tlen
//        res.append(ra.getFragmentLength());
//        res.append(FIELD_SEPARATOR);
//
//        // sequence
//        res.append(ra.getAlignedSequence().toString());
//        res.append(FIELD_SEPARATOR);
//
//        // quality
//        for (int v: ra.getAlignedQuality()) {
//            res.append((char) (v + 33)); // Add ASCII offset
//        }
//
//        // optional fields
//        for (CharSequence key: ra.getInfo().keySet()) {
//            res.append(FIELD_SEPARATOR);
//            res.append(key.toString());
//            for (CharSequence val : ra.getInfo().get(key)) {
//                res.append((":" + val.toString()));
//            }
//        }
//
//        return res.toString();
//    }

    /**
     * Adjusts the quality value for optimized 8-level mapping quality scores.
     *
     * Quality range -> Mapped quality
     * 1     ->  1
     * 2-9   ->  6
     * 10-19 ->  15
     * 20-24 ->  22
     * 25-29 ->  27
     * 30-34 ->  33
     * 35-39 ->  27
     * >=40  ->  40
     *
     * Read more: http://www.illumina.com/documents/products/technotes/technote_understanding_quality_scores.pd
     *
     * @param quality original quality
     * @return Adjusted quality
     */
    public static int adjustQuality(int quality) {
        final int adjustedQuality;

        if (quality <= 1) {
            adjustedQuality = quality;
        } else {
            int qualRange = quality / 5;
            switch (qualRange) {
                case 0:
                case 1:
                    adjustedQuality = 6;
                    break;
                case 2:
                case 3:
                    adjustedQuality = 15;
                    break;
                case 4:
                    adjustedQuality = 22;
                    break;
                case 5:
                    adjustedQuality = 27;
                    break;
                case 6:
                    adjustedQuality = 33;
                    break;
                case 7:
                    adjustedQuality = 37;
                    break;
                case 8:
                default:
                    adjustedQuality = 40;
                    break;
            }
        }
        return adjustedQuality;
    }


    public static SAMFileHeader getFileHeader(Path input) throws IOException {
        FileUtils.checkFile(input);

        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        SamReader reader = srf.open(SamInputResource.of(input.toFile()));
        SAMFileHeader fileHeader = reader.getFileHeader();
        reader.close();

        return fileHeader;
    }

    /**
     * Check if the file is a sorted binary bam file.
     * @param is            Bam InputStream
     * @param bamFileName   Bam FileName
     * @throws IOException
     */
    public static void checkBamOrCramFile(InputStream is, String bamFileName) throws IOException {
        checkBamOrCramFile(is, bamFileName, true);
    }

    /**
     * Check if the file is a sorted binary bam file.
     * @param is            Bam InputStream
     * @param bamFileName   Bam FileName
     * @param checkSort
     * @throws IOException
     */
    public static void checkBamOrCramFile(InputStream is, String bamFileName, boolean checkSort) throws IOException {
        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);

        SamReader reader = srf.open(SamInputResource.of(is));
        SAMFileHeader fileHeader = reader.getFileHeader();
        SAMFileHeader.SortOrder sortOrder = fileHeader.getSortOrder();
        reader.close();

        if (reader.type().equals(SamReader.Type.SAM_TYPE)) {
            throw new IOException("Expected binary SAM file. File " + bamFileName + " is not binary.");
        }

        if (checkSort) {
            switch (sortOrder) {
                case coordinate:
                    break;
                case queryname:
                case unsorted:
                default:
                    throw new IOException("Expected sorted file. File '" + bamFileName + "' is not sorted by coordinates("
                            + sortOrder.name() + ")");
            }
        }
    }

}
