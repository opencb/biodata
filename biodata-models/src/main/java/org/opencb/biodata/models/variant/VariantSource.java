package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;

import java.util.*;


/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantSource {

    private String fileName;
    private String fileId;

    private String studyId;
    private String studyName;

    private Map<String, Integer> samplesPosition;

    private Pedigree pedigree; // TODO Decide something about this field

    private Map<String, String> metadata;

    private VariantGlobalStats stats;

    private VariantStudy.StudyType type;

    VariantSource() {
    }

    public VariantSource(String fileName, String fileId, String studyId, String studyName) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.studyId = studyId;
        this.studyName = studyName;
        this.samplesPosition = new LinkedHashMap<>();
        this.metadata = new HashMap<>();
        this.type = VariantStudy.StudyType.CASE_CONTROL;
    }

    public VariantSource(String fileName, String fileId, String studyId, String studyName, VariantStudy.StudyType type) {
        this(fileName, fileId, studyId, studyName);
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public Map<String, Integer> getSamplesPosition() {
        return samplesPosition;
    }

    public void setSamplesPosition(Map<String, Integer> samplesPosition) {
        this.samplesPosition = samplesPosition;
    }

    public List<String> getSamples() {
        return new ArrayList(samplesPosition.keySet());
    }

    public void setSamples(List<String> newSamples) {
        int index = samplesPosition.size();
        for (String s : newSamples) {
            samplesPosition.put(s, index++);
        }
    }

    public Pedigree getPedigree() {
        return pedigree;
    }

    public void setPedigree(Pedigree pedigree) {
        this.pedigree = pedigree;
    }

    public VariantGlobalStats getStats() {
        return stats;
    }

    public void setStats(VariantGlobalStats stats) {
        this.stats = stats;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }

    public VariantStudy.StudyType getType() {
        return type;
    }

    public void setType(VariantStudy.StudyType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VariantStudy{" +
                "name='" + fileName + '\'' +
                ", alias='" + fileId + '\'' +
                ", samples=" + samplesPosition +
                ", metadata=" + metadata +
                ", stats=" + stats +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.fileName);
        hash = 37 * hash + Objects.hashCode(this.fileId);
        hash = 37 * hash + Objects.hashCode(this.studyId);
        hash = 37 * hash + Objects.hashCode(this.studyName);
        hash = 37 * hash + Objects.hashCode(this.samplesPosition);
        hash = 37 * hash + Objects.hashCode(this.pedigree);
        hash = 37 * hash + Objects.hashCode(this.metadata);
        hash = 37 * hash + Objects.hashCode(this.stats);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VariantSource other = (VariantSource) obj;
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        if (!Objects.equals(this.fileId, other.fileId)) {
            return false;
        }
        if (!Objects.equals(this.studyId, other.studyId)) {
            return false;
        }
        if (!Objects.equals(this.studyName, other.studyName)) {
            return false;
        }
        if (!Objects.equals(this.samplesPosition, other.samplesPosition)) {
            return false;
        }
        if (!Objects.equals(this.pedigree, other.pedigree)) {
            return false;
        }
        if (!Objects.equals(this.metadata, other.metadata)) {
            return false;
        }
        if (!Objects.equals(this.stats, other.stats)) {
            return false;
        }
        return true;
    }


}
