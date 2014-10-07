package org.opencb.biodata.formats.feature.refseq;

/**
 * Created by parce on 5/26/14.
 */
public class Refseq {

    public static final String REFSEQ_CHROMOSOME_ACCESION_TAG = "NC";

    public static String refseqNCAccessionToChromosome(String refseqNCAccesion) {
        String chr = null;

        // TODO: "cachear" las transformaciones para que sea mas rapido??
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
