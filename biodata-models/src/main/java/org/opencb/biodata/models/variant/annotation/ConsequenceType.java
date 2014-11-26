package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 19/11/14.
 */
public class ConsequenceType {
    private String geneName;
    private String ensemblGeneId;
    private String ensemblTranscriptId;
    private int SOAccession;
    private String SOName;
    private int relativePosition;
    private String codon;
    private String aaChange;

//    private static ConsequenceTypeMappings consequenceTypeMappings = new ConsequenceTypeMappings();

    public ConsequenceType(String geneName, String ensemblGeneId, String ensemblTranscriptId, String SOName) {
        this.geneName = geneName;
        this.ensemblGeneId = ensemblGeneId;
        this.ensemblTranscriptId = ensemblTranscriptId;
//        this.SOAccession = consequenceTypeMappings.getAccession(SOName);
        this.SOAccession = ConsequenceTypeMappings.termToAccession.get(SOName);
        this.SOName = SOName;
    }

    public void setSOName(String SOName) {
        this.SOName = SOName;
    }

    public void setSOAccession(int SOAccession) {
        this.SOAccession = SOAccession;
    }

    public void setEnsemblTranscriptId(String ensemblTranscriptId) {
        this.ensemblTranscriptId = ensemblTranscriptId;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public void setRelativePosition(int relativePosition) {
        this.relativePosition = relativePosition;
    }

    public void setAaChange(String aaChange) {
        this.aaChange = aaChange;
    }

    public void setCodon(String codon) {
        this.codon = codon;
    }

    public String getGeneName() {
        return geneName;
    }

    public String getEnsemblTranscriptId() {
        return ensemblTranscriptId;
    }

    public int getSOAccession() {
        return SOAccession;
    }

    public String getSOName() {
        return SOName;
    }

    public int getRelativePosition() {
        return relativePosition;
    }

    public String getAaChange() {
        return aaChange;
    }

    public String getCodon() {
        return codon;
    }

//    public boolean equals(ConsequenceType consequenceType) {
//        return (this.ensemblTranscriptId.equals(consequenceType.ensemblTranscriptId) &&
//                this.SOAccession==consequenceType.SOAccession && this.geneName.equals(consequenceType.geneName) &&
//                this.ensemblGeneId.equals(consequenceType.ensemblGeneId));
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsequenceType that = (ConsequenceType) o;

        if (SOAccession != that.SOAccession) return false;
        if (relativePosition != that.relativePosition) return false;
        if (SOName != null ? !SOName.equals(that.SOName) : that.SOName != null) return false;
        if (aaChange != null ? !aaChange.equals(that.aaChange) : that.aaChange != null) return false;
        if (codon != null ? !codon.equals(that.codon) : that.codon != null) return false;
        if (ensemblGeneId != null ? !ensemblGeneId.equals(that.ensemblGeneId) : that.ensemblGeneId != null)
            return false;
        if (ensemblTranscriptId != null ? !ensemblTranscriptId.equals(that.ensemblTranscriptId) : that.ensemblTranscriptId != null)
            return false;
        if (geneName != null ? !geneName.equals(that.geneName) : that.geneName != null) return false;

        return true;
    }

}
