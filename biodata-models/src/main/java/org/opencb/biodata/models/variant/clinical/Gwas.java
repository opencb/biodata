package org.opencb.biodata.models.variant.clinical;

/**
 * Created by lcruz on 26/05/14.
 */
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.ParseException;

/** @author Luis Miguel Cruz
 *  @version 1.2.3
 *  @since October 08, 2014  */
@JsonFilter("gwasFilter")
public class Gwas {
    public String dateAddedToCatalog;
    public String pubmedId;
    public String firstAuthor;
    public String date;
    public String journal;
    public String link;
    public String study;
    public String diseaseTrait;
    public String initialSampleSize;
    public String replicationSampleSize;
    public String region;
    public String chromosome;
    public Integer start;
    public Integer end;
    public String reportedGenes;
    public String mappedGene;
    public String upstreamGeneId;
    public String downstreamGeneId;
    public String snpGeneIds;
    public String upstreamGeneDistance;
    public String downstreamGeneDistance;
    public String strongestSNPRiskAllele;
    public String snps;
    public String merged;
    public String snpIdCurrent;
    public String context;
    public String intergenic;
    public Float riskAlleleFrequency;
    public Float pValue;
    public Float pValueMlog;
    public String pValueText;
    public String orBeta;
    public String percentCI;
    public String platform;
    public String cnv;
    public String reference;
    public String alternate;

    public Gwas(){
    }

    public Gwas(String[] values) throws ParseException {
        this.dateAddedToCatalog = values[0].trim();
        this.pubmedId = values[1].trim();
        this.firstAuthor = values[2].trim();
        this.date = values[3].trim();
        this.journal = values[4].trim();
        this.link = values[5].trim();
        this.study = values[6].trim();
        this.diseaseTrait = values[7].trim();
        this.initialSampleSize = values[8].trim();
        this.replicationSampleSize = values[9].trim();
        this.region = values[10].trim();
        if(!values[11].isEmpty()){
        	if(values[11].equalsIgnoreCase("23")){
        		this.chromosome = "X";
        	} else if(values[11].equalsIgnoreCase("24")) {
        		this.chromosome = "Y";	
        	} else if(values[11].equalsIgnoreCase("25")) {
        		this.chromosome = "MT";	
        	} else {
        		this.chromosome = values[11];	
        	}
        } else {
            this.chromosome = null;
        }
        try{
            this.start = Integer.parseInt(values[12]);
            this.end = this.start;
        } catch (NumberFormatException e){
            this.start = null;
            this.end = null;
        }
        this.reportedGenes = values[13].trim();
        this.mappedGene = values[14].trim();
        this.upstreamGeneId = values[15].trim();
        this.downstreamGeneId = values[16].trim();
        this.snpGeneIds = values[17].trim();
        this.upstreamGeneDistance = values[18].trim();
        this.downstreamGeneDistance = values[19].trim();
        this.strongestSNPRiskAllele = values[20].trim();
        this.snps = values[21].trim();
        this.merged = values[22].trim();
        this.snpIdCurrent = values[23].trim();
        this.context = values[24].trim();
        this.intergenic = values[25].trim();
        try {
            this.riskAlleleFrequency = Float.parseFloat(values[26]);
        } catch (NumberFormatException e){
            this.riskAlleleFrequency = null;
        }
        try {
            this.pValue = Float.parseFloat(values[27]);
        } catch (NumberFormatException e){
            this.pValue = null;
        }
        try {
            this.pValueMlog = Float.parseFloat(values[28]);
        } catch (NumberFormatException e){
            this.pValueMlog = null;
        }
        this.pValueText = values[29].trim();
        this.orBeta = values[30].trim();
        this.percentCI = values[31].trim();
        this.platform = values[32].trim();
        this.cnv = values[33].trim();
    }

    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("-------- GWAS OBJECT -------\n");
        result.append("\t Date Added to Catalog: \t"+dateAddedToCatalog+"\n");
        result.append("\t PUBMEDID: \t"+pubmedId+"\n");
        result.append("\t First Author: \t"+firstAuthor+"\n");
        result.append("\t Date: \t"+date+"\n");
        result.append("\t Journal: \t"+journal+"\n");
        result.append("\t Link: \t"+link+"\n");
        result.append("\t Study: \t"+study+"\n");
        result.append("\t Disease/Trait: \t"+diseaseTrait+"\n");
        result.append("\t Initial Sample Size: \t"+initialSampleSize+"\n");
        result.append("\t Replication Sample Size: \t"+replicationSampleSize+"\n");
        result.append("\t Region: \t"+region+"\n");
        result.append("\t Chromosome_id: \t"+chromosome+"\n");
        result.append("\t Chromosome_start: \t"+start+"\n");
        result.append("\t Chromosome_end: \t"+end+"\n");
        result.append("\t Reported Gene(s): \t"+reportedGenes+"\n");
        result.append("\t Mapped_gene: \t"+mappedGene+"\n");
        result.append("\t Upstream_gene_id: \t"+upstreamGeneId+"\n");
        result.append("\t Downstream_gene_id: \t"+downstreamGeneId+"\n");
        result.append("\t Snp_gene_ids: \t"+snpGeneIds+"\n");
        result.append("\t Upstream_gene_distance: \t"+upstreamGeneDistance+"\n");
        result.append("\t Downstream_gene_distance: \t"+downstreamGeneDistance+"\n");
        result.append("\t Strongest SNP-Risk Allele: \t"+strongestSNPRiskAllele+"\n");
        result.append("\t SNPs: \t"+snps+"\n");
        result.append("\t Merged: \t"+merged+"\n");
        result.append("\t Snp_id_current: \t"+snpIdCurrent+"\n");
        result.append("\t Context: \t"+context+"\n");
        result.append("\t Intergenic: \t"+intergenic+"\n");
        result.append("\t Risk Allele Frequency: \t"+riskAlleleFrequency+"\n");
        result.append("\t p-Value: \t"+pValue+"\n");
        result.append("\t Pvalue_mlog: \t"+pValueMlog+"\n");
        result.append("\t p-Value (text): \t"+pValueText+"\n");
        result.append("\t OR or beta: \t"+orBeta+"\n");
        result.append("\t 95% CI (text): \t"+percentCI+"\n");
        result.append("\t Platform [SNPs passing QC]: \t"+platform+"\n");
        result.append("\t CNV: \t"+cnv+"\n");
        result.append("----------------------------\n");

        return result.toString();
    }

    // ---------------------------------- GETTERS / SETTERS ------------------------------
    public String getDateAddedToCatalog() {
        return dateAddedToCatalog;
    }

    public void setDateAddedToCatalog(String dateAddedToCatalog) {
        this.dateAddedToCatalog = dateAddedToCatalog;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getFirstAuthor() {
        return firstAuthor;
    }

    public void setFirstAuthor(String firstAuthor) {
        this.firstAuthor = firstAuthor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getDiseaseTrait() {
        return diseaseTrait;
    }

    public void setDiseaseTrait(String diseaseTrait) {
        this.diseaseTrait = diseaseTrait;
    }

    public String getInitialSampleSize() {
        return initialSampleSize;
    }

    public void setInitialSampleSize(String initialSampleSize) {
        this.initialSampleSize = initialSampleSize;
    }

    public String getReplicationSampleSize() {
        return replicationSampleSize;
    }

    public void setReplicationSampleSize(String replicationSampleSize) {
        this.replicationSampleSize = replicationSampleSize;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public String getReportedGenes() {
        return reportedGenes;
    }

    public void setReportedGenes(String reportedGenes) {
        this.reportedGenes = reportedGenes;
    }

    public String getMappedGene() {
        return mappedGene;
    }

    public void setMappedGene(String mappedGene) {
        this.mappedGene = mappedGene;
    }

    public String getUpstreamGeneId() {
        return upstreamGeneId;
    }

    public void setUpstreamGeneId(String upstreamGeneId) {
        this.upstreamGeneId = upstreamGeneId;
    }

    public String getDownstreamGeneId() {
        return downstreamGeneId;
    }

    public void setDownstreamGeneId(String downstreamGeneId) {
        this.downstreamGeneId = downstreamGeneId;
    }

    public String getSnpGeneIds() {
        return snpGeneIds;
    }

    public void setSnpGeneIds(String snpGeneIds) {
        this.snpGeneIds = snpGeneIds;
    }

    public String getUpstreamGeneDistance() {
        return upstreamGeneDistance;
    }

    public void setUpstreamGeneDistance(String upstreamGeneDistance) {
        this.upstreamGeneDistance = upstreamGeneDistance;
    }

    public String getDownstreamGeneDistance() {
        return downstreamGeneDistance;
    }

    public void setDownstreamGeneDistance(String downstreamGeneDistance) {
        this.downstreamGeneDistance = downstreamGeneDistance;
    }

    public String getStrongestSNPRiskAllele() {
        return strongestSNPRiskAllele;
    }

    public void setStrongestSNPRiskAllele(String strongestSNPRiskAllele) {
        this.strongestSNPRiskAllele = strongestSNPRiskAllele;
    }

    public String getSnps() {
        return snps;
    }

    public void setSnps(String snps) {
        this.snps = snps;
    }

    public String getMerged() {
        return merged;
    }

    public void setMerged(String merged) {
        this.merged = merged;
    }

    public String getSnpIdCurrent() {
        return snpIdCurrent;
    }

    public void setSnpIdCurrent(String snpIdCurrent) {
        this.snpIdCurrent = snpIdCurrent;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getIntergenic() {
        return intergenic;
    }

    public void setIntergenic(String intergenic) {
        this.intergenic = intergenic;
    }

    public Float getRiskAlleleFrequency() {
        return riskAlleleFrequency;
    }

    public void setRiskAlleleFrequency(Float riskAlleleFrequency) {
        this.riskAlleleFrequency = riskAlleleFrequency;
    }

    public Float getpValue() {
        return pValue;
    }

    public void setpValue(Float pValue) {
        this.pValue = pValue;
    }

    public Float getpValueMlog() {
        return pValueMlog;
    }

    public void setpValueMlog(Float pValueMlog) {
        this.pValueMlog = pValueMlog;
    }

    public String getpValueText() {
        return pValueText;
    }

    public void setpValueText(String pValueText) {
        this.pValueText = pValueText;
    }

    public String getOrBeta() {
        return orBeta;
    }

    public void setOrBeta(String orBeta) {
        this.orBeta = orBeta;
    }

    public String getPercentCI() {
        return percentCI;
    }

    public void setPercentCI(String percentCI) {
        this.percentCI = percentCI;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCnv() {
        return cnv;
    }

    public void setCnv(String cnv) {
        this.cnv = cnv;
    }

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}
}