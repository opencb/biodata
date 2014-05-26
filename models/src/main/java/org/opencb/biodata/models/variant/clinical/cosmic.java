package org.opencb.biodata.models.variant.clinical;


/**
 * Created by antonior on 5/22/14.
 */
public class cosmic {

    /***
    Alternate Allele
     ***/
    private String allele;

    /***
     Alternate Reference
     ***/
    private String reference;


    /***
     Chromosome
     ***/
    private String chr;

    /***
     Variant position
     ***/
    private int pos;

    /***
     Gene_name
     ***/
    private String Gene_name;

    /***
     Mutation GRCh37 strand
     ***/
    private String Mutation_GRCh37_strand;

    /***
     Primary site
     ***/
    private String Primary_site;

    /***
     Mutation zygosity
     ***/
    private String Mutation_zygosity;

    /***
     Mutation AA
     ***/
    private String Mutation_AA;


    /***
     Tumour origin
     ***/
    private String Tumour_origin;


    /***
     Histology subtype
     ***/
    private String Histology_subtype;


    /***
     Sample source
     ***/
    private String Sample_source;

    /***
     Accession Number
     ***/
    private String Accession_Number;


    /***
     Mutation ID
     ***/
    private String Mutation_ID;


    /***
     Mutation CDS
     ***/
    private String Mutation_CDS;


    /***
     Sample name
     ***/
    private String Sample_name;


    /***
     Primary histology
     ***/
    private String Primary_histology;


    /***
     Mutation GRCh37 genome position
     ***/
    private String Mutation_GRCh37_genome_position;



    /***
     Mutation Description
     ***/
    private String Mutation_Description;


    /***
     Genome-wide screen
     ***/
    private String Genome_wide_screen;


    /***
     ID_tumour
     ***/
    private String ID_tumour;


    /***
     ID_sample
     ***/
    private String ID_sample;


    /***
     Mutation somatic status
     ***/
    private String Mutation_somatic_status;

    /***
     Site subtype
     ***/
    private String Site_subtype;


    /***
     Mutation NCBI36 strand
     ***/
    private String Mutation_NCBI36_strand;


    /***
     Mutation NCBI36 genome position
     ***/
    private String Mutation_NCBI36_genome_position;


    /***
     * Gene CDS length
     */
    private int gene_CDS_length;


    /***
     * HGNC ID
     */
    private String HGNC_id;

    /***
     * Pubmed PMID
     */
    private String Pubmed_PMID;


    /***
     * Age
     */
    private int age;

    /***
     * Comments
     */
    private String comments;


    public cosmic(String allele, String reference, String chr, int pos, String gene_name, String mutation_GRCh37_strand, String primary_site, String mutation_zygosity, String mutation_AA, String tumour_origin, String histology_subtype, String sample_source, String accession_Number, String mutation_ID, String mutation_CDS, String sample_name, String primary_histology, String mutation_GRCh37_genome_position, String mutation_Description, String genome_wide_screen, String ID_tumour, String ID_sample, String mutation_somatic_status, String site_subtype, String mutation_NCBI36_strand, String mutation_NCBI36_genome_position, int gene_cds_length, String hgnc_id, String pubmed_pmid, int Age, String Comments) {
        this.allele = allele;
        this.reference=reference;
        this.chr = chr;
        this.pos = pos;
        this.Gene_name = gene_name;
        this.Mutation_GRCh37_strand = mutation_GRCh37_strand;
        this.Primary_site = primary_site;
        this.Mutation_zygosity = mutation_zygosity;
        this.Mutation_AA = mutation_AA;
        this.Tumour_origin = tumour_origin;
        this.Histology_subtype = histology_subtype;
        this.Sample_source = sample_source;
        this.Accession_Number = accession_Number;
        this.Mutation_ID = mutation_ID;
        this.Mutation_CDS = mutation_CDS;
        this.Sample_name = sample_name;
        this.Primary_histology = primary_histology;
        this.Mutation_GRCh37_genome_position = mutation_GRCh37_genome_position;
        this.Mutation_Description = mutation_Description;
        this.Genome_wide_screen = genome_wide_screen;
        this.ID_tumour = ID_tumour;
        this.ID_sample = ID_sample;
        this.Mutation_somatic_status = mutation_somatic_status;
        this.Site_subtype = site_subtype;
        this.Mutation_NCBI36_strand = mutation_NCBI36_strand;
        this.Mutation_NCBI36_genome_position = mutation_NCBI36_genome_position;
        this.gene_CDS_length=gene_cds_length;
        this.HGNC_id=hgnc_id;
        this.Pubmed_PMID=pubmed_pmid;
        this.age=Age;
        this.comments=Comments;


    }


    public String getAllele() {
        return allele;
    }

    public void setAllele(String allele) {
        this.allele = allele;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getGene_name() {
        return Gene_name;
    }

    public void setGene_name(String gene_name) {
        Gene_name = gene_name;
    }

    public String getMutation_GRCh37_strand() {
        return Mutation_GRCh37_strand;
    }

    public void setMutation_GRCh37_strand(String mutation_GRCh37_strand) {
        Mutation_GRCh37_strand = mutation_GRCh37_strand;
    }

    public String getPrimary_site() {
        return Primary_site;
    }

    public void setPrimary_site(String primary_site) {
        Primary_site = primary_site;
    }

    public String getMutation_zygosity() {
        return Mutation_zygosity;
    }

    public void setMutation_zygosity(String mutation_zygosity) {
        Mutation_zygosity = mutation_zygosity;
    }

    public String getMutation_AA() {
        return Mutation_AA;
    }

    public void setMutation_AA(String mutation_AA) {
        Mutation_AA = mutation_AA;
    }

    public String getTumour_origin() {
        return Tumour_origin;
    }

    public void setTumour_origin(String tumour_origin) {
        Tumour_origin = tumour_origin;
    }

    public String getHistology_subtype() {
        return Histology_subtype;
    }

    public void setHistology_subtype(String histology_subtype) {
        Histology_subtype = histology_subtype;
    }

    public String getSample_source() {
        return Sample_source;
    }

    public void setSample_source(String sample_source) {
        Sample_source = sample_source;
    }

    public String getAccession_Number() {
        return Accession_Number;
    }

    public void setAccession_Number(String accession_Number) {
        Accession_Number = accession_Number;
    }

    public String getMutation_ID() {
        return Mutation_ID;
    }

    public void setMutation_ID(String mutation_ID) {
        Mutation_ID = mutation_ID;
    }

    public String getMutation_CDS() {
        return Mutation_CDS;
    }

    public void setMutation_CDS(String mutation_CDS) {
        Mutation_CDS = mutation_CDS;
    }

    public String getSample_name() {
        return Sample_name;
    }

    public void setSample_name(String sample_name) {
        Sample_name = sample_name;
    }

    public String getPrimary_histology() {
        return Primary_histology;
    }

    public void setPrimary_histology(String primary_histology) {
        Primary_histology = primary_histology;
    }

    public String getMutation_GRCh37_genome_position() {
        return Mutation_GRCh37_genome_position;
    }

    public void setMutation_GRCh37_genome_position(String mutation_GRCh37_genome_position) {
        Mutation_GRCh37_genome_position = mutation_GRCh37_genome_position;
    }

    public String getMutation_Description() {
        return Mutation_Description;
    }

    public void setMutation_Description(String mutation_Description) {
        Mutation_Description = mutation_Description;
    }

    public String getGenome_wide_screen() {
        return Genome_wide_screen;
    }

    public void setGenome_wide_screen(String genome_wide_screen) {
        Genome_wide_screen = genome_wide_screen;
    }

    public String getID_tumour() {
        return ID_tumour;
    }

    public void setID_tumour(String ID_tumour) {
        this.ID_tumour = ID_tumour;
    }

    public String getID_sample() {
        return ID_sample;
    }

    public void setID_sample(String ID_sample) {
        this.ID_sample = ID_sample;
    }

    public String getMutation_somatic_status() {
        return Mutation_somatic_status;
    }

    public void setMutation_somatic_status(String mutation_somatic_status) {
        Mutation_somatic_status = mutation_somatic_status;
    }

    public String getSite_subtype() {
        return Site_subtype;
    }

    public void setSite_subtype(String site_subtype) {
        Site_subtype = site_subtype;
    }

    public String getMutation_NCBI36_strand() {
        return Mutation_NCBI36_strand;
    }

    public void setMutation_NCBI36_strand(String mutation_NCBI36_strand) {
        Mutation_NCBI36_strand = mutation_NCBI36_strand;
    }

    public String getMutation_NCBI36_genome_position() {
        return Mutation_NCBI36_genome_position;
    }

    public void setMutation_NCBI36_genome_position(String mutation_NCBI36_genome_position) {
        Mutation_NCBI36_genome_position = mutation_NCBI36_genome_position;
    }


    public int getGene_CDS_length() {
        return gene_CDS_length;
    }

    public void setGene_CDS_length(int gene_CDS_length) {
        this.gene_CDS_length = gene_CDS_length;
    }

    public String getHGNC_id() {
        return HGNC_id;
    }

    public void setHGNC_id(String HGNC_id) {
        this.HGNC_id = HGNC_id;
    }

    public String getPubmed_PMID() {
        return Pubmed_PMID;
    }

    public void setPubmed_PMID(String pubmed_PMID) {
        Pubmed_PMID = pubmed_PMID;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

