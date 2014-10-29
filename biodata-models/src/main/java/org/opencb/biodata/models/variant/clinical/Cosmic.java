package org.opencb.biodata.models.variant.clinical;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author by antonior on 5/22/14.
 * @author Luis Miguel Cruz.
 * @since October 08, 2014 
 */
public class Cosmic implements Comparable {

    /** Alternate Allele */
    private String alternate;

    /** Alternate Reference */
    private String reference;

    /** Chromosome */
    private String chromosome;

    /** Variant position start */
    private int start;
    
    /** Variant position end */
    private int end;

    /** geneName */
    private String geneName;

    /** Mutation GRCh37 strand */
    private String mutationGRCh37Strand;

    /** Primary site */
    private String primarySite;

    /** Mutation zygosity */
    private String mutationZygosity;

    /** Mutation AA */
    private String mutationAA;

    /** Tumour origin */
    private String tumourOrigin;

    /** Histology subtype */
    private String histologySubtype;

    /** Accession Number */
    private String accessionNumber;

    /** Mutation ID */
    private String mutationID;

    /** Mutation CDS */
    private String mutationCDS;

    /** Sample name */
    private String sampleName;

    /** Primary histology */
    private String primaryHistology;

    /** Mutation GRCh37 genome position */
    private String mutationGRCh37GenomePosition;

    /** Mutation Description */
    private String mutationDescription;

    /** Genome-wide screen */
    private String genomeWideScreen;

    /** ID_tumour */
    private String ID_tumour;

    /** idSample */
    private String idSample;

    /** Mutation somatic status */
    private String mutationSomaticStatus;

    /** Site subtype */
    private String siteSubtype;

    /** Gene CDS length */
    private int geneCDSLength;

    /** HGNC ID */
    private String hgncId;

    /** Pubmed PMID */
    private String pubmedPMID;

    private String sampleSource;

    /** Age (may be null) */
    private Float age;

    /** Comments */
    private String comments;

    private boolean snp;

    private String fathmmPrediction;

    private Integer idStudy;

    public Cosmic(){

    }

//    public Cosmic(String[] fields) {
//        // COSMIC file is a tab-delimited file with the following fields (columns)
//        // 0 Gene name
//        // 1 Accession Number
//        // 2 Gene CDS length
//        // 3 HGNC ID
//        // 4 Sample name
//        // 5 ID sample
//        // 6 ID tumour
//        // 7 Primary site
//        // 8 Site subtype
//        // 9 Primary histology
//        // 10 Histology subtype
//        // 11 Genome-wide screen
//        // 12 Mutation ID
//        // 13 Mutation CDS
//        // 14 Mutation AA
//        // 15 Mutation Description
//        // 16 Mutation zygosity
//        // 17 Mutation GRCh37 genome position
//        // 18 Mutation GRCh37 strand
//        // 19 Snp
//        // 20 FATHMM Prediction
//        // 21 Mutation somatic status
//        // 22 PubMed PMID
//        // 23 ID STUDY
//        // 24 Tumour origin
//        // 25 Age
//        // 26 Comments
//
//    	this.geneName = fields[0];
//        this.accessionNumber = fields[1];
//        this.geneCDSLength = Integer.parseInt(fields[2]);
//        this.hgncId = fields[3];
//        this.sampleName = fields[4];
//        this.idSample = fields[5];
//        this.ID_tumour = fields[6];
//        this.primarySite = fields[7];
//        this.siteSubtype = fields[8];
//        this.primaryHistology = fields[9];
//        this.histologySubtype = fields[10];
//        this.genomeWideScreen = fields[11];
//        this.mutationID = fields[12];
//        this.mutationCDS = fields[13];
//        this.mutationAA = fields[14];
//        this.mutationDescription = fields[15];
//        this.mutationZygosity = fields[16];
//        this.mutationGRCh37GenomePosition = fields[17];
//        this.mutationGRCh37Strand = fields[18];
//        if(!fields[19].isEmpty() && fields[19].equalsIgnoreCase("y")){
//            this.snp = true;
//        }
//        this.fathmmPrediction = fields[20];
//        this.mutationSomaticStatus = fields[21];
//        this.pubmedPMID = fields[22];
//        if (!fields[23].isEmpty() && !fields[23].equals("NS")) {
//            this.idStudy = Integer.parseInt(fields[23]);
//        }
//        this.tumourOrigin = fields[24];
//        if(!fields[25].isEmpty() && !fields[25].equals("NS")) {
//            this.age = Float.parseFloat(fields[25]);
//        }
//        this.comments = fields[26];
//
//        // Calculate start and end
//        this.calculateStartAndEnd();
//
//        // Calculate reference and alternate
//        this.calculateAltAndRef();
//
//        // Calculate genome position
//        this.recalculateGenomePosition();
//    }


    // ----------------------- GETTERS / SETTERS --------------------------------
    
    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getMutationGRCh37Strand() {
        return mutationGRCh37Strand;
    }

    public void setMutationGRCh37Strand(String mutationGRCh37Strand) {
        this.mutationGRCh37Strand = mutationGRCh37Strand;
    }

    public String getPrimarySite() {
        return primarySite;
    }

    public void setPrimarySite(String primarySite) {
        this.primarySite = primarySite;
    }

    public String getMutationZygosity() {
        return mutationZygosity;
    }

    public void setMutationZygosity(String mutationZygosity) {
        this.mutationZygosity = mutationZygosity;
    }

    public String getMutationAA() {
        return mutationAA;
    }

    public void setMutationAA(String mutationAA) {
        this.mutationAA = mutationAA;
    }

    public String getTumourOrigin() {
        return tumourOrigin;
    }

    public void setTumourOrigin(String tumourOrigin) {
        this.tumourOrigin = tumourOrigin;
    }

    public String getHistologySubtype() {
        return histologySubtype;
    }

    public void setHistologySubtype(String histologySubtype) {
        this.histologySubtype = histologySubtype;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getMutationID() {
        return mutationID;
    }

    public void setMutationID(String mutationID) {
        this.mutationID = mutationID;
    }

    public String getMutationCDS() {
        return mutationCDS;
    }

    public void setMutationCDS(String mutationCDS) {
        this.mutationCDS = mutationCDS;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getPrimaryHistology() {
        return primaryHistology;
    }

    public void setPrimaryHistology(String primaryHistology) {
        this.primaryHistology = primaryHistology;
    }

    public String getMutationGRCh37GenomePosition() {
        return mutationGRCh37GenomePosition;
    }

    public void setMutationGRCh37GenomePosition(String mutationGRCh37GenomePosition) {
        this.mutationGRCh37GenomePosition = mutationGRCh37GenomePosition;
    }

    public String getMutationDescription() {
        return mutationDescription;
    }

    public void setMutationDescription(String mutationDescription) {
        this.mutationDescription = mutationDescription;
    }

    public String getGenomeWideScreen() {
        return genomeWideScreen;
    }

    public void setGenomeWideScreen(String genomeWideScreen) {
        this.genomeWideScreen = genomeWideScreen;
    }

    public String getID_tumour() {
        return ID_tumour;
    }

    public void setID_tumour(String ID_tumour) {
        this.ID_tumour = ID_tumour;
    }

    public String getIdSample() {
        return idSample;
    }

    public void setIdSample(String idSample) {
        this.idSample = idSample;
    }

    public String getMutationSomaticStatus() {
        return mutationSomaticStatus;
    }

    public void setMutationSomaticStatus(String mutationSomaticStatus) {
        this.mutationSomaticStatus = mutationSomaticStatus;
    }

    public String getSiteSubtype() {
        return siteSubtype;
    }

    public void setSiteSubtype(String siteSubtype) {
        this.siteSubtype = siteSubtype;
    }

    public int getGeneCDSLength() {
        return geneCDSLength;
    }

    public void setGeneCDSLength(int geneCDSLength) {
        this.geneCDSLength = geneCDSLength;
    }

    public boolean isSnp() {
        return snp;
    }

    public void setSnp(boolean snp) {
        this.snp = snp;
    }

    public String getFathmmPrediction() {
        return fathmmPrediction;
    }

    public void setFathmmPrediction(String fathmmPrediction) {
        this.fathmmPrediction = fathmmPrediction;
    }

    public Integer getIdStudy() {
        return idStudy;
    }

    public void setIdStudy(Integer idStudy) {
        this.idStudy = idStudy;
    }

    public String getHgncId() {
        return hgncId;
    }

    public void setHgncId(String hgncId) {
        this.hgncId = hgncId;
    }

    public String getPubmedPMID() {
        return pubmedPMID;
    }

    public void setPubmedPMID(String pubmedPMID) {
        this.pubmedPMID = pubmedPMID;
    }

    public Float getAge() {
        return age;
    }

    public void setAge(Float age) {
        this.age = age;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    
    // ----------------------- ADITIONAL FUNCTIONS --------------------------------
    
    public int compareTo(Object o) {
        Cosmic otherCosmic = (Cosmic)o;
        int chr1 = Integer.parseInt(this.getChromosome());
        int chr2 = Integer.parseInt(otherCosmic.getChromosome());
        if (chr1 != chr2) {
            return chr1 - chr2;
        } else {
            return this.getStart() - otherCosmic.getStart();
        }
    }

    public String getSampleSource() {
        return sampleSource;
    }

    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }
}