/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.variant.annotation;

import org.opencb.biodata.models.variant.annotation.exceptions.SOTermNotAvailableException;

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
        termToAccession.put("non_coding_transcript_exon_variant", 1792);
        termToAccession.put("non_coding_transcript_variant", 1619);
        termToAccession.put("intron_variant", 1627);
        termToAccession.put("NMD_transcript_variant", 1621);
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
        termToAccession.put("downstream_gene_variant", 1632);
        termToAccession.put("2KB_downstream_gene_variant", 1632);
        termToAccession.put("upstream_gene_variant", 1631);
        termToAccession.put("2KB_upstream_gene_variant", 1631);
        termToAccession.put("SNV", 1483);
        termToAccession.put("SNP", 694);
        termToAccession.put("RNA_polymerase_promoter", 1203);
        termToAccession.put("CpG_island", 307);
        termToAccession.put("DNAseI_hypersensitive_site", 685);
        termToAccession.put("polypeptide_variation_site", 336);
        termToAccession.put("protein_altering_variant", 1818);
        termToAccession.put("start_lost", 2012);

        // Fill the accession to term map
        for(String key : termToAccession.keySet()) {
            accessionToTerm.put(termToAccession.get(key), key);
        }

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
