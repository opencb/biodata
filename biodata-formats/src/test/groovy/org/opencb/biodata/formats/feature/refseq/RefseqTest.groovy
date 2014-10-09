package org.opencb.biodata.formats.feature.refseq

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by parce on 5/29/14.
 */
class RefseqTest extends Specification {

    @Unroll
    def "Refseq NC accession #accession should be translated to chromosome #chr"() {
        expect:
        chr == Refseq.refseqNCAccessionToChromosome(accession)

        where:
        accession       || chr
        "NC_000001.10"  || "1"
        "NC_000001.11"  || "1"
        "NC_000002.11"  || "2"
        "NC_000002.12"  || "2"
        "NC_000003.11"  || "3"
        "NC_000003.12"  || "3"
        "NC_000004.11"  || "4"
        "NC_000004.12"  || "4"
        "NC_000005.10"  || "5"
        "NC_000005.9"   || "5"
        "NC_000006.11"  || "6"
        "NC_000006.12"  || "6"
        "NC_000007.13"  || "7"
        "NC_000007.14"  || "7"
        "NC_000008.10"  || "8"
        "NC_000008.11"  || "8"
        "NC_000009.11"  || "9"
        "NC_000009.12"  || "9"
        "NC_000010.10"  || "10"
        "NC_000010.11"  || "10"
        "NC_000011.10"  || "11"
        "NC_000011.9"   || "11"
        "NC_000012.11"  || "12"
        "NC_000012.12"  || "12"
        "NC_000013.10"  || "13"
        "NC_000013.11"  || "13"
        "NC_000014.8"   || "14"
        "NC_000014.9"   || "14"
        "NC_000015.10"  || "15"
        "NC_000015.9"   || "15"
        "NC_000016.10"  || "16"
        "NC_000016.9"   || "16"
        "NC_000017.10"  || "17"
        "NC_000017.11"  || "17"
        "NC_000018.10"  || "18"
        "NC_000018.9"   || "18"
        "NC_000019.10"  || "19"
        "NC_000019.9"   || "19"
        "NC_000020.10"  || "20"
        "NC_000020.11"  || "20"
        "NC_000021.8"   || "21"
        "NC_000021.9"   || "21"
        "NC_000022.10"  || "22"
        "NC_000022.11"  || "22"
        "NC_000023.10"  || "X"
        "NC_000023.11"  || "X"
        "NC_000024.10"  || "Y"
        "NC_000024.9"   || "Y"
        "NC_012920.1"   || "MT"
    }
}
