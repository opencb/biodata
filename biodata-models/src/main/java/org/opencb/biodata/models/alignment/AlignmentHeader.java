package org.opencb.biodata.models.alignment;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jacobo
 * Date: 8/06/14
 * Time: 11:33
 */
public class AlignmentHeader {

    static public class SequenceRecord {
        private String sequenceName;
        private int sequenceLength;
        private Map<String, String> attributes;

        public SequenceRecord() {}
        public SequenceRecord(String sequenceName, int sequenceLength, Map<String, String> attributes) {
            this.sequenceName = sequenceName;
            this.sequenceLength = sequenceLength;
            this.attributes = attributes;
        }

        public String getSequenceName() {
            return sequenceName;
        }

        public void setSequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
        }

        public int getSequenceLength() {
            return sequenceLength;
        }

        public void setSequenceLength(int sequenceLength) {
            this.sequenceLength = sequenceLength;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }
    }
    static public class ReadGroup {
        private String id;
        private Map<String, String> attributes;

        public ReadGroup(){}
        public ReadGroup(String id, Map<String, String> attributes) {
            this.id = id;
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }
    }
    static public class ProgramRecord {
        private String id;
        private Map<String, String> attributes;

        public ProgramRecord(){}
        public ProgramRecord(String id, Map<String, String> attributes) {
            this.id = id;
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }
    }

    private Map<String, String>     attributes;
    private List<SequenceRecord>    sequenceDiccionary;
    private List<ReadGroup>         readGroups;
    private List<ProgramRecord>     programRecords;
    private List<String>            comments;

    private String studyName;
    private String sampleName;
    private String origin;


    public AlignmentHeader(Map<String, String> attributes, List<SequenceRecord> sequenceDiccionary, List<ReadGroup> readGroups, List<ProgramRecord> programRecords, List<String> comments) {
        this.attributes = attributes;
        this.sequenceDiccionary = sequenceDiccionary;
        this.readGroups = readGroups;
        this.programRecords = programRecords;
        this.comments = comments;
    }

    public AlignmentHeader(){
        this.attributes = new HashMap<>();
        this.sequenceDiccionary = new LinkedList<>();
        this.readGroups = new LinkedList<>();
        this.programRecords = new LinkedList<>();
        this.comments = new LinkedList<>();
    }

    public AlignmentHeader(String studyName, String sampleName, String origin) {
        this.studyName = studyName;
        this.sampleName = sampleName;
        this.origin = origin;
    }


    public Map<String, String> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }



    public List<SequenceRecord> getSequenceDiccionary() {
        return sequenceDiccionary;
    }
    public void addSequenceRecord(String sequenceName, int sequenceLength, Map<String, String> attributes){
        sequenceDiccionary.add(new SequenceRecord(sequenceName, sequenceLength, attributes));
    }
    public void setSequenceDiccionary(List<SequenceRecord> sequenceDiccionary) {
        this.sequenceDiccionary = sequenceDiccionary;
    }



    public void setReadGroups(List<ReadGroup> readGroups) {
        this.readGroups = readGroups;
    }
    public List<ReadGroup> getReadGroups() {
        return readGroups;
    }
    public void addReadGroup(String id,  Map<String, String> attributes){
        readGroups.add(new ReadGroup(id, attributes));
    }



    public List<ProgramRecord> getProgramRecords() {
        return programRecords;
    }
    public void setProgramRecords(List<ProgramRecord> programRecords) {
        this.programRecords = programRecords;
    }
    public void addProgramRecords(String id,  Map<String, String> attributes){
        programRecords.add(new ProgramRecord(id, attributes));
    }




    public List<String> getComments() {
        return comments;
    }
    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getStudyName() {
        return studyName;
    }
    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getSampleName() {
        return sampleName;
    }
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }


    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }



}
