package org.opencb.biodata.formats.alignment;

import net.sf.samtools.*;
import org.opencb.biodata.formats.alignment.AlignmentHelper;
import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;
import org.opencb.biodata.models.alignment.exceptions.ShortReferenceSequenceException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jacobo
 * Date: 16/06/14
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentFactory {

    public static Alignment buildAlignment(SAMRecord record){
        return buildAlignment(record, null);
    }
    public static Alignment buildAlignment(SAMRecord record, Map<String, Object> attributes, String referenceSequence) {
        Alignment alignment = new Alignment(record.getReadName(), record.getReferenceName(), record.getAlignmentStart(), record.getAlignmentEnd(),
                record.getUnclippedStart(), record.getUnclippedEnd(), record.getReadLength(),
                record.getMappingQuality(), record.getBaseQualityString(),//.replace("\\", "\\\\").replace("\"", "\\\""),
                record.getMateReferenceName(), record.getMateAlignmentStart(),
                record.getInferredInsertSize(), record.getFlags(),
                AlignmentHelper.getDifferencesFromCigar(record, referenceSequence, Integer.MAX_VALUE),
                attributes);
        return alignment;
    }
    public static Alignment buildAlignment(SAMRecord record, String referenceSequence) {
        Alignment alignment = new Alignment(record.getReadName(), record.getReferenceName(), record.getAlignmentStart(), record.getAlignmentEnd(),
                record.getUnclippedStart(), record.getUnclippedEnd(), record.getReadLength(),
                record.getMappingQuality(), record.getBaseQualityString(),//.replace("\\", "\\\\").replace("\"", "\\\""),
                record.getMateReferenceName(), record.getMateAlignmentStart(),
                record.getInferredInsertSize(), record.getFlags(),
                AlignmentHelper.getDifferencesFromCigar(record, referenceSequence, Integer.MAX_VALUE),
                null);
        Map<String, Object> attributes = new HashMap<>();
        for(SAMRecord.SAMTagAndValue tav : record.getAttributes()){
            attributes.put(tav.tag, tav.value);
        }
        alignment.setAttributes(attributes);
        return alignment;
    }

    public static SAMRecord buildSAMRecord(Alignment alignment, SAMFileHeader samFileHeader, String referenceSequence) throws ShortReferenceSequenceException {
        return buildSAMRecord(alignment, samFileHeader, referenceSequence, alignment.getStart());
    }
    public static SAMRecord buildSAMRecord(Alignment alignment, SAMFileHeader samFileHeader, String referenceSequence, long referenceSequenceStartPosition) throws ShortReferenceSequenceException {
        SAMRecord samRecord = new SAMRecord(samFileHeader);

        samRecord.setReadName(alignment.getName());
        samRecord.setReferenceName(alignment.getChromosome());
        samRecord.setAlignmentStart((int)alignment.getStart());
        //samRecord.setAlignmentEnd((int)end);

        samRecord.setMappingQuality(alignment.getMappingQuality());
        samRecord.setBaseQualityString(alignment.getQualities());
        samRecord.setMateReferenceName(alignment.getMateReferenceName());
        samRecord.setMateAlignmentStart(alignment.getMateAlignmentStart());
        samRecord.setInferredInsertSize(alignment.getInferredInsertSize());

        Cigar cigar = new Cigar();
        byte[] readSequence = AlignmentHelper.getSequenceFromDifferences(
                alignment.getDifferences(),
                alignment.getLength(),
                referenceSequence,
                cigar,
                (int) (alignment.getUnclippedStart() - referenceSequenceStartPosition)
        ).getBytes();
        samRecord.setReadBases(readSequence);

        samRecord.setCigar(cigar);
        samRecord.setFlags(alignment.getFlags());

        if (alignment.getAttributes() != null) {
            for (Map.Entry<String, Object> entry : alignment.getAttributes().entrySet()) {
                samRecord.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        return samRecord;
    }

    public static SAMFileHeader buildSAMFileHeader(AlignmentHeader alignmentHeader){

        SAMFileHeader samFileHeader = new SAMFileHeader();

        for(Map.Entry<String, String> entry : alignmentHeader.getAttributes().entrySet()) {
            samFileHeader.setAttribute(entry.getKey(), entry.getValue());
        }

        for(AlignmentHeader.SequenceRecord sq : alignmentHeader.getSequenceDiccionary()){
            SAMSequenceRecord samSequenceRecord = new SAMSequenceRecord(sq.getSequenceName(), sq.getSequenceLength());
            for(Map.Entry<String, String> entry : sq.getAttributes().entrySet())
                samSequenceRecord.setAttribute(entry.getKey(), entry.getValue());
            samFileHeader.addSequence(samSequenceRecord);
        }

        for(AlignmentHeader.ReadGroup rg : alignmentHeader.getReadGroups()){
            SAMReadGroupRecord samReadGroupRecord = new SAMReadGroupRecord(rg.getId());
            for(Map.Entry<String, String> entry : rg.getAttributes().entrySet()){
                samReadGroupRecord.setAttribute(entry.getKey(), entry.getValue());
            }
            samFileHeader.addReadGroup(samReadGroupRecord);
        }

        for(AlignmentHeader.ProgramRecord pg : alignmentHeader.getProgramRecords()){
            SAMProgramRecord samProgramRecord = new SAMProgramRecord(pg.getId());
            for(Map.Entry<String, String> entry : pg.getAttributes().entrySet()){
                samProgramRecord.setAttribute(entry.getKey(), entry.getValue());
            }
            samFileHeader.addProgramRecord(samProgramRecord);
        }

        samFileHeader.setComments(alignmentHeader.getComments());

        return samFileHeader;

    }
    public static AlignmentHeader buildAlignmentHeader(SAMFileHeader samHeader, String studyName){
        
        AlignmentHeader alignmentHeader = new AlignmentHeader();
        alignmentHeader.setStudyName(studyName);
        alignmentHeader.setOrigin("SAM");

        HashMap<String, String> attributes = new HashMap<>();
        List<AlignmentHeader.SequenceRecord> sequenceDiccionary = new LinkedList<>();
        List<AlignmentHeader.ReadGroup> readGroups = new LinkedList<>();
        List<AlignmentHeader.ProgramRecord> programRecords = new LinkedList<>();
        List<String> comments = new LinkedList<>();

        for(Map.Entry<String, String> entry : samHeader.getAttributes()) {
            attributes.put(entry.getKey(), entry.getValue());
        }

        for (SAMSequenceRecord sq : samHeader.getSequenceDictionary().getSequences()) {
            Map<String, String> attr = new HashMap<>();
            for (Map.Entry<String,String> entry :sq.getAttributes()){
                attr.put(entry.getKey(), entry.getValue());
            }
            alignmentHeader.addSequenceRecord(sq.getSequenceName(), sq.getSequenceLength(), attr);
        }
        for (SAMReadGroupRecord rg : samHeader.getReadGroups()) {
            Map<String, String> attr = new HashMap<>();
            for (Map.Entry<String,String> entry :rg.getAttributes()){
                attr.put(entry.getKey(), entry.getValue());
            }
            alignmentHeader.addReadGroup(rg.getId(), attr);
        }
        for (SAMProgramRecord pg : samHeader.getProgramRecords()) {
            Map<String, String> attr = new HashMap<>();
            for (Map.Entry<String,String> entry :pg.getAttributes()){
                attr.put(entry.getKey(), entry.getValue());
            }
            alignmentHeader.addProgramRecords(pg.getId(), attr);
        }

        alignmentHeader.getComments().addAll(samHeader.getComments());

        return alignmentHeader;
    }

}
