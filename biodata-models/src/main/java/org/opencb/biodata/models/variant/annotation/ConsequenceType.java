package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 19/11/14.
 */
public class ConsequenceType {
    private String geneName;
    private String ensemblGeneId;
    private String ensemblTranscriptId;
    private Integer SOAccession;
    private String SOName;
    private Integer relativePosition;
    private String codon;
    private String strand;
    private String biotype;
    private Integer cDnaPosition;
    private Integer cdsPosition;
    private Integer aPosition;
    private String aChange;


    //    private static ConsequenceTypeMappings consequenceTypeMappings = new ConsequenceTypeMappings();

    public ConsequenceType() {

    }

    public ConsequenceType(String SOName) {
        this.SOAccession = ConsequenceTypeMappings.termToAccession.get(SOName);
        this.SOName = SOName;
    }

    public ConsequenceType(String geneName, String ensemblGeneId, String ensemblTranscriptId, String SOName) {
        this.geneName = geneName;
        this.ensemblGeneId = ensemblGeneId;
        this.ensemblTranscriptId = ensemblTranscriptId;
//        this.SOAccession = consequenceTypeMappings.getAccession(SOName);
        this.SOAccession = ConsequenceTypeMappings.termToAccession.get(SOName);
        this.SOName = SOName;
    }

    public ConsequenceType(String geneName, String ensemblGeneId, String ensemblTranscriptId, String strand, String biotype, String SOName) {
        this.geneName = geneName;
        this.ensemblGeneId = ensemblGeneId;
        this.ensemblTranscriptId = ensemblTranscriptId;
        this.strand = strand;
//        this.SOAccession = consequenceTypeMappings.getAccession(SOName);
        this.SOAccession = ConsequenceTypeMappings.termToAccession.get(SOName);
        this.SOName = SOName;
        this.biotype = biotype;
        this.cDnaPosition = cDnaPosition;
    }

    public ConsequenceType(String geneName, String ensemblGeneId, String ensemblTranscriptId, String strand,
                           String biotype, Integer cDnaPosition, String SOName) {
        this.geneName = geneName;
        this.ensemblGeneId = ensemblGeneId;
        this.ensemblTranscriptId = ensemblTranscriptId;
        this.strand = strand;
//        this.SOAccession = consequenceTypeMappings.getAccession(SOName);
        this.SOAccession = ConsequenceTypeMappings.termToAccession.get(SOName);
        this.SOName = SOName;
        this.biotype = biotype;
        this.cDnaPosition = cDnaPosition;
    }

    public ConsequenceType(String geneName, String ensemblGeneId, String ensemblTranscriptId, String strand,
                           String biotype, Integer cDnaPosition, Integer cdsPosition, Integer aPosition,
                           String aChange, String codon, String SOName) {
        this.geneName = geneName;
        this.ensemblGeneId = ensemblGeneId;
        this.ensemblTranscriptId = ensemblTranscriptId;
        this.strand = strand;
//        this.SOAccession = consequenceTypeMappings.getAccession(SOName);
        this.SOAccession = ConsequenceTypeMappings.termToAccession.get(SOName);
        this.SOName = SOName;
        this.biotype = biotype;
        this.cDnaPosition = cDnaPosition;
        this.cdsPosition = cdsPosition;
        this.aPosition = aPosition;
        this.aChange = aChange;
        this.codon = codon;
    }


    public void setSOName(String SOName) {
        this.SOName = SOName;
    }

    public void setSOAccession(Integer SOAccession) {
        this.SOAccession = SOAccession;
    }

    public void setEnsemblTranscriptId(String ensemblTranscriptId) {
        this.ensemblTranscriptId = ensemblTranscriptId;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public void setRelativePosition(Integer relativePosition) {
        this.relativePosition = relativePosition;
    }

    public void setCodon(String codon) { this.codon = codon; }

    public void setStrand(String strand) { this.strand = strand; }

    public void setBiotype(String biotype) { this.biotype = biotype; }

    public void setcDnaPosition(Integer cDnaPosition) { this.cDnaPosition = cDnaPosition; }

    public void setCdsPosition(Integer cdsPosition) { this.cdsPosition = cdsPosition; }

    public void setaPosition(Integer aPosition) { this.aPosition = aPosition; }

    public void setaChange(String aChange) { this.aChange = aChange; }

    public String getGeneName() {
        return geneName;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public String getEnsemblGeneId(String defaultString) {
        if(ensemblGeneId!=null) {
            return ensemblGeneId;
        } else {
            return defaultString;
        }
    }

    public String getEnsemblTranscriptId() {
        return ensemblTranscriptId;
    }

    public Integer getSOAccession() {
        return SOAccession;
    }

    public String getSOName() {
        return SOName;
    }

    public Integer getRelativePosition() {
        return relativePosition;
    }

    public String getCodon() { return codon; }

    public String getCodon(String defaultString) {
        if(codon!=null) {
            return codon;
        } else {
            return defaultString;
        }
    }

    public String getStrand() { return strand; }

    public String getStrand(String defaultString) {
        if(strand!=null) {
            return strand;
        } else {
            return defaultString;
        }
    }

    public String getBiotype() { return biotype; }

    public String getBiotype(String defaultString) {
        if(biotype!=null) {
            return biotype;
        } else {
            return defaultString;
        }
    }

    public Integer getcDnaPosition() { return cDnaPosition; }

    public Integer getCdsPosition() { return cdsPosition; }

    public Integer getaPosition() { return aPosition; }

    public String getaChange() { return aChange; }

    public String getaChange(String defaultString) {
        if(aChange!=null) {
            return aChange;
        } else {
            return defaultString;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsequenceType)) return false;

        ConsequenceType that = (ConsequenceType) o;

        if (SOAccession != that.SOAccession) return false;
        if (aPosition != that.aPosition) return false;
        if (cDnaPosition != that.cDnaPosition) return false;
        if (cdsPosition != that.cdsPosition) return false;
        if (relativePosition != that.relativePosition) return false;
        if (SOName != null ? !SOName.equals(that.SOName) : that.SOName != null) return false;
        if (aChange != null ? !aChange.equals(that.aChange) : that.aChange != null) return false;
        if (biotype != null ? !biotype.equals(that.biotype) : that.biotype != null) return false;
        if (codon != null ? !codon.equals(that.codon) : that.codon != null) return false;
        if (ensemblGeneId != null ? !ensemblGeneId.equals(that.ensemblGeneId) : that.ensemblGeneId != null)
            return false;
        if (ensemblTranscriptId != null ? !ensemblTranscriptId.equals(that.ensemblTranscriptId) : that.ensemblTranscriptId != null)
            return false;
        if (geneName != null ? !geneName.equals(that.geneName) : that.geneName != null) return false;
        if (strand != null ? !strand.equals(that.strand) : that.strand != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = geneName != null ? geneName.hashCode() : 0;
        result = 31 * result + (ensemblGeneId != null ? ensemblGeneId.hashCode() : 0);
        result = 31 * result + (ensemblTranscriptId != null ? ensemblTranscriptId.hashCode() : 0);
        result = 31 * result + SOAccession;
        result = 31 * result + (SOName != null ? SOName.hashCode() : 0);
        result = 31 * result + relativePosition;
        result = 31 * result + (codon != null ? codon.hashCode() : 0);
        result = 31 * result + (strand != null ? strand.hashCode() : 0);
        result = 31 * result + (biotype != null ? biotype.hashCode() : 0);
        result = 31 * result + cDnaPosition;
        result = 31 * result + cdsPosition;
        result = 31 * result + aPosition;
        result = 31 * result + (aChange != null ? aChange.hashCode() : 0);
        return result;
    }
}
