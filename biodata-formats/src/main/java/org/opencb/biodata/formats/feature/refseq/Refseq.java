package org.opencb.biodata.formats.feature.refseq;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by parce on 5/26/14.
 */
public class Refseq {

    public static final String REFSEQ_CHROMOSOME_ACCESION_TAG = "NC";

    private static Map<String, String> accesionToChromosomesMap;

    public static String refseqNCAccessionToChromosome(String refseqNCAccesion) {
        if (accesionToChromosomesMap == null) {
            accesionToChromosomesMap = new HashMap<>();
        }

        String chr = accesionToChromosomesMap.get(refseqNCAccesion);
        if (chr == null) {
            chr = Refseq.uncachedRefseqNCAccessionToChromosome(refseqNCAccesion);
            accesionToChromosomesMap.put(refseqNCAccesion, chr);
        }
        return chr;
    }

    public static String uncachedRefseqNCAccessionToChromosome(String refseqNCAccesion) {
        String chr = null;

        Integer chrNumber = Integer.parseInt(refseqNCAccesion.split("NC_0*")[1].split("\\.")[0]);
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
