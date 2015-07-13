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

package org.opencb.biodata.formats.feature.refseq;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

/**
 * Created by parce on 5/26/14.
 */
public class RefseqAccession {

    public static final String REFSEQ_CHROMOSOME_ACCESION_TAG = "NC";

    private static Map<String, String> accessionToChromosomesMap;
    private final String accession;
    private String type;
    private static final String REFERENCE_ASSEMBLY_COMPLETE_GENOMIC = "NC";

    private static final String ASSEMBLY_37 = "GRCh37";
    private static final String ASSEMBLY_38 = "GRCh38";

    private static Set<String> assembly37Chromosomes;
    private static Set<String> assembly38Chromosomes;
    static {
        assembly37Chromosomes = new HashSet<>(Arrays.asList("NC_000001.10", "NC_000002.11", "NC_000003.11", "NC_000004.11",
                "NC_000005.9", "NC_000006.11", "NC_000007.13", "NC_000008.10", "NC_000009.11", "NC_000010.10", "NC_000011.9",
                "NC_000012.11", "NC_000013.10", "NC_000014.8", "NC_000015.9", "NC_000016.9", "NC_000017.10", "NC_000018.9",
                "NC_000019.9", "NC_000020.10", "NC_000021.8", "NC_000022.10", "NC_000023.10", "NC_000024.9"));
        assembly38Chromosomes = new HashSet<>(Arrays.asList("NC_000001.11", "NC_000002.12", "NC_000003.12", "NC_000004.12",
                "NC_000005.10", "NC_000006.12", "NC_000007.14", "NC_000008.11", "NC_000009.12", "NC_000010.11", "NC_000011.10",
                "NC_000012.12", "NC_000013.11", "NC_000014.9", "NC_000015.10", "NC_000016.10", "NC_000017.11", "NC_000018.10",
                "NC_000019.10", "NC_000020.11", "NC_000021.9", "NC_000022.11", "NC_000023.11", "NC_000024.10"));
    }


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

    public String getAssembly() {
        if (assembly37Chromosomes.contains(accession)) {
            return ASSEMBLY_37;
        } else if (assembly38Chromosomes.contains(accession)) {
            return ASSEMBLY_38;
        } else {
            return "";
        }

    }
}
