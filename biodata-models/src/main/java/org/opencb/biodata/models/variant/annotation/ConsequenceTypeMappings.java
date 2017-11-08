/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.variant.annotation;

import org.opencb.biodata.models.variant.annotation.exceptions.SOTermNotAvailableException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * 
 * TODO Handle duplicated terms in tmpTermToAccession (synonymous_variant...)
 * TODO Load using ontology file: http://song.cvs.sourceforge.net/viewvc/song/ontology/so.obo
 */
public class ConsequenceTypeMappings {

    public static final Map<String, Integer> termToAccession;

    public static final Map<Integer, String> accessionToTerm;

    static {

        Map<String, Integer> tmpTermToAccession = new HashMap<>();

        // Fill the term to accession map
        tmpTermToAccession.put("transcript_ablation", 1893);
        tmpTermToAccession.put("copy_number_change", 1563);
        tmpTermToAccession.put("structural_variant", 1537);
        tmpTermToAccession.put("terminator_codon_variant", 1590);
        tmpTermToAccession.put("splice_donor_variant", 1575);
        tmpTermToAccession.put("splice_acceptor_variant", 1574);
        tmpTermToAccession.put("stop_gained", 1587);
        tmpTermToAccession.put("frameshift_variant", 1589);
        tmpTermToAccession.put("stop_lost", 1578);
        tmpTermToAccession.put("initiator_codon_variant", 1582);
        tmpTermToAccession.put("inframe_insertion", 1821);
        tmpTermToAccession.put("inframe_deletion", 1822);
        tmpTermToAccession.put("inframe_variant", 1650);
        tmpTermToAccession.put("missense_variant", 1583);
        tmpTermToAccession.put("transcript_amplification", 1889);
        tmpTermToAccession.put("splice_region_variant", 1630);
        tmpTermToAccession.put("incomplete_terminal_codon_variant", 1626);
        tmpTermToAccession.put("synonymous_variant", 1819);
        tmpTermToAccession.put("stop_retained_variant", 1567);
        tmpTermToAccession.put("start_retained_variant", 2019);
        tmpTermToAccession.put("coding_sequence_variant", 1580);
        tmpTermToAccession.put("miRNA", 276);
        tmpTermToAccession.put("miRNA_target_site", 934);
        tmpTermToAccession.put("mature_miRNA_variant", 1620);
        tmpTermToAccession.put("5_prime_UTR_variant", 1623);
        tmpTermToAccession.put("3_prime_UTR_variant", 1624);
        tmpTermToAccession.put("exon_variant", 1791);
        tmpTermToAccession.put("non_coding_transcript_exon_variant", 1792);
        tmpTermToAccession.put("non_coding_transcript_variant", 1619);
        tmpTermToAccession.put("intron_variant", 1627);
        tmpTermToAccession.put("NMD_transcript_variant", 1621);
        tmpTermToAccession.put("TFBS_ablation", 1895);
        tmpTermToAccession.put("TFBS_amplification", 1892);
        tmpTermToAccession.put("TF_binding_site_variant", 1782);
        tmpTermToAccession.put("regulatory_region_variant", 1566);
        tmpTermToAccession.put("regulatory_region_ablation", 1894);
        tmpTermToAccession.put("regulatory_region_amplification", 1891);
        tmpTermToAccession.put("feature_elongation", 1907);
        tmpTermToAccession.put("feature_truncation", 1906);
        tmpTermToAccession.put("feature_variant", 1878);
        tmpTermToAccession.put("intergenic_variant", 1628);
        tmpTermToAccession.put("lincRNA", 1463);
        tmpTermToAccession.put("downstream_gene_variant", 1632);
        tmpTermToAccession.put("2KB_downstream_variant", 2083);
        tmpTermToAccession.put("upstream_gene_variant", 1631);
        tmpTermToAccession.put("2KB_upstream_variant", 1636);
        tmpTermToAccession.put("SNV", 1483);
        tmpTermToAccession.put("SNP", 694);
        tmpTermToAccession.put("RNA_polymerase_promoter", 1203);
        tmpTermToAccession.put("CpG_island", 307);
        tmpTermToAccession.put("DNAseI_hypersensitive_site", 685);
        tmpTermToAccession.put("polypeptide_variation_site", 336);
        tmpTermToAccession.put("protein_altering_variant", 1818);
        tmpTermToAccession.put("start_lost", 2012);

        Map<Integer, String> tmpAccessionToTerm = new HashMap<>();
        // Fill the accession to term map
        for(String key : tmpTermToAccession.keySet()) {
            tmpAccessionToTerm.put(tmpTermToAccession.get(key), key);
        }

        /********************************************************
         * *********     DEPRECATED !!!!!!!! *******************
         ********************************************************/
        tmpTermToAccession.put("downstream_gene_variant", 1632);
        tmpTermToAccession.put("2KB_downstream_gene_variant", 1632);
        tmpTermToAccession.put("upstream_gene_variant", 1631);
        tmpTermToAccession.put("2KB_upstream_gene_variant", 1631);

        termToAccession = Collections.unmodifiableMap(tmpTermToAccession);
        accessionToTerm = Collections.unmodifiableMap(tmpAccessionToTerm);
    }

    public static String getSoAccessionString(String SOName) throws SOTermNotAvailableException {
        if (termToAccession.get(SOName) == null) {
            throw new SOTermNotAvailableException(SOName);
        }else {
            String soAccession = Integer.toString(termToAccession.get(SOName));
            return String.format("SO:%0" + (7 - soAccession.length()) + "d%s", 0, soAccession);
        }
    }
}
