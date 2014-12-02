package org.opencb.biodata.formats.feature.refseq;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by parce on 5/26/14.
 */
public class RefseqAccession {

    public static final String REFSEQ_CHROMOSOME_ACCESION_TAG = "NC";

    private static Map<String, String> accessionToChromosomesMap;
    private final String accession;
    private String type;
    private static final String REFERENCE_ASSEMBLY_COMPLETE_GENOMIC = "NC";

    public RefseqAccession(String accession) {
        this.accession = accession;
        this.type = accession.split("_")[0];
    }

    public boolean isReferenceAssemblyCompleteGenomicMolecule() {
        return this.type.equals(REFERENCE_ASSEMBLY_COMPLETE_GENOMIC);
    }

    public String getChromosome() {
        String chr = null;
        if (isReferenceAssemblyCompleteGenomicMolecule()) {
            if (accessionToChromosomesMap == null) {
                accessionToChromosomesMap = new HashMap<>();
            }

            chr = accessionToChromosomesMap.get(accession);
            if (chr == null) {
                chr = uncachedRefseqNCAccessionToChromosome();
                accessionToChromosomesMap.put(accession, chr);
            }
        }
        return chr;
    }

    public String uncachedRefseqNCAccessionToChromosome() {
        String chr = null;

        Integer chrNumber = Integer.parseInt(accession.split("NC_0*")[1].split("\\.")[0]);
        if (chrNumber < 23) {
            chr = chrNumber.toString();
        } else if (chrNumber == 23) {
            chr = "X";
        } else if (chrNumber == 24) {
            chr = "Y";
        } else if (chrNumber == 12920) {
            chr = "MT";
        }
        return chr;
    }
}
