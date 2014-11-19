package org.opencb.biodata.models.variant.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * 
 * TODO Handle duplicated terms in termToAccession (synonymous_variant...)
 * TODO Load using ontology file: http://song.cvs.sourceforge.net/viewvc/song/ontology/so.obo
 */
public class ConsequenceTypeMappings {

    public static final Map<String, Integer> termToAccession = new HashMap<>();

    public static final Map<Integer, String> accessionToTerm = new HashMap<>();

    static {

        // Fill the term to accession map
        termToAccession.put("transcript_ablation", 1893);
        termToAccession.put("splice_donor_variant", 1575);
        termToAccession.put("splice_acceptor_variant", 1574);
        termToAccession.put("stop_gained", 1587);
        termToAccession.put("frameshift_variant", 1589);
        termToAccession.put("stop_lost", 1578);
        termToAccession.put("initiator_codon_variant", 1582);
        termToAccession.put("inframe_insertion", 1821);
        termToAccession.put("inframe_deletion", 1822);
        termToAccession.put("missense_variant", 1583);
        termToAccession.put("transcript_amplification", 1889);
        termToAccession.put("splice_region_variant", 1630);
        termToAccession.put("incomplete_terminal_codon_variant", 1626);
        termToAccession.put("synonymous_variant", 1819);
        termToAccession.put("stop_retained_variant", 1567);
        termToAccession.put("coding_sequence_variant", 1580);
        termToAccession.put("miRNA", 276);
        termToAccession.put("miRNA_target_site", 934);
        termToAccession.put("mature_miRNA_variant", 1620);
        termToAccession.put("5_prime_UTR_variant", 1623);
        termToAccession.put("3_prime_UTR_variant", 1624);
        termToAccession.put("exon_variant", 1791);
        termToAccession.put("non_coding_exon_variant", 1792);
        termToAccession.put("nc_transcript_variant", 1619);
        termToAccession.put("intron_variant", 1627);
        termToAccession.put("NMD_transcript_variant", 1621);
        termToAccession.put("upstream_gene_variant", 1631);
        termToAccession.put("downstream_gene_variant", 1632);
        termToAccession.put("TFBS_ablation", 1895);
        termToAccession.put("TFBS_amplification", 1892);
        termToAccession.put("TF_binding_site_variant", 1782);
        termToAccession.put("regulatory_region_variant", 1566);
        termToAccession.put("regulatory_region_ablation", 1894);
        termToAccession.put("regulatory_region_amplification", 1891);
        termToAccession.put("feature_elongation", 1907);
        termToAccession.put("feature_truncation", 1906);
        termToAccession.put("intergenic_variant", 1628);
        termToAccession.put("lincRNA", 1463);
        termToAccession.put("5KB_downstream_variant", 1633);
        termToAccession.put("5KB_upstream_variant", 1635);
        termToAccession.put("SNV", 1483);
        termToAccession.put("SNP", 694);
        termToAccession.put("RNA_polymerase_promoter", 1203);
        termToAccession.put("CpG_island", 307);
        termToAccession.put("DNAseI_hypersensitive_site", 685);
        termToAccession.put("polypeptide_variation_site", 336);

        // Fill the accession to term map
        accessionToTerm.put(1893, "transcript_ablation");
        accessionToTerm.put(1575, "splice_donor_variant");
        accessionToTerm.put(1574, "splice_acceptor_variant");
        accessionToTerm.put(1587, "stop_gained");
        accessionToTerm.put(1589, "frameshift_variant");
        accessionToTerm.put(1578, "stop_lost");
        accessionToTerm.put(1582, "initiator_codon_variant");
        accessionToTerm.put(1821, "inframe_insertion");
        accessionToTerm.put(1822, "inframe_deletion");
        accessionToTerm.put(1583, "missense_variant");
        accessionToTerm.put(1889, "transcript_amplification");
        accessionToTerm.put(1630, "splice_region_variant");
        accessionToTerm.put(1626, "incomplete_terminal_codon_variant");
        accessionToTerm.put(1819, "synonymous_variant");
        accessionToTerm.put(1588, "synonymous_variant"); // TODO How to handle duplicated terms in termToAccession
        accessionToTerm.put(1567, "stop_retained_variant");
        accessionToTerm.put(1580, "coding_sequence_variant");
        accessionToTerm.put(276, "miRNA");
        accessionToTerm.put(934, "miRNA_target_site");
        accessionToTerm.put(1620, "mature_miRNA_variant");
        accessionToTerm.put(1623, "5_prime_UTR_variant");
        accessionToTerm.put(1624, "3_prime_UTR_variant");
        accessionToTerm.put(1791, "exon_variant");
        accessionToTerm.put(1792, "non_coding_exon_variant");
        accessionToTerm.put(1619, "nc_transcript_variant");
        accessionToTerm.put(1627, "intron_variant");
        accessionToTerm.put(1621, "NMD_transcript_variant");
        accessionToTerm.put(1631, "upstream_gene_variant");
        accessionToTerm.put(1632, "downstream_gene_variant");
        accessionToTerm.put(1895, "TFBS_ablation");
        accessionToTerm.put(1892, "TFBS_amplification");
        accessionToTerm.put(1782, "TF_binding_site_variant");
        accessionToTerm.put(1566, "regulatory_region_variant");
        accessionToTerm.put(1894, "regulatory_region_ablation");
        accessionToTerm.put(1891, "regulatory_region_amplification");
        accessionToTerm.put(1907, "feature_elongation");
        accessionToTerm.put(1906, "feature_truncation");
        accessionToTerm.put(1628, "intergenic_variant");
        accessionToTerm.put(1463, "lincRNA");
        accessionToTerm.put(1633, "5KB_downstream_variant");
        accessionToTerm.put(1635, "5KB_upstream_variant");
        accessionToTerm.put(1483, "SNV");
        accessionToTerm.put(694, "SNP");
        accessionToTerm.put(1203, "RNA_polymerase_promoter");
        accessionToTerm.put(307, "CpG_island");
        accessionToTerm.put(685, "DNAseI_hypersensitive_site");
        accessionToTerm.put(336, "polypeptide_variation_site");
    }

}
