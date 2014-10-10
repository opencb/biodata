package org.opencb.biodata.formats.variant.hgvs

import net.sf.picard.reference.FastaSequenceIndex
import net.sf.picard.reference.IndexedFastaSequenceFile
import spock.lang.Unroll

import java.nio.file.Paths

/**
 * Created by parce on 5/28/14.
 */
class HgvsTest extends spock.lang.Specification {

    private static genomeSequenceFastaFile

    void setupSpec() {
        setup: "load the reference fasta file"
        URL referenceFastaResource = getClass().getClassLoader().getResource("referenceSequence/testReferenceSequence.fasta")
        URL referenceFastaIndexResource = getClass().getClassLoader().getResource("referenceSequence/testReferenceSequence.fasta.fai")
        def referenceFastaFile = Paths.get(referenceFastaResource.toURI()).toFile()
        def referenceFastaIndexFile = Paths.get(referenceFastaIndexResource.toURI()).toFile()
        genomeSequenceFastaFile = new IndexedFastaSequenceFile(referenceFastaFile, new FastaSequenceIndex(referenceFastaIndexFile))
    }

    @Unroll
    def "sequence Location of #hgvs should be #chr - #start - #reference - #alternate"() {
        when: "obtain the hgvs location"
        def location = new Hgvs(hgvs).getSequenceLocation(genomeSequenceFastaFile)

        then: "validate the location chromosome, start, reference and alternate"
        location.getChr() == chr
        location.getStart() == start
        location.getReferenceAllele() == reference
        location.getAlternateAllele() == alternate

        where:
        hgvs                    || chr   | start  | reference | alternate
        "NC_000019.9:g.35G>A"   || "19"  | 35     | "G"       | "A"
        "NC_000019.9:g.35insA"  || "19"  | 35     | "G"       | "GA"
        "NC_000019.9:g.35delG"  || "19"  | 34     | "TG"      | "T"
        and: "one nucleotide duplication is treated like one insertion"
        "NC_000019.9:g.14dupC"  || "19"  | 13     | "T"       | "TC"
        and: "one nucleotide insertion in the middle of a poly-T should be shifted to the previous non-T nucleotide"
        "NC_000019.9:g.27insT"  || "19"  | 24     | "G"       | "GT"
        and: "one nucleotide deletion in the middle of a poly-T should be shifted to the previous non-T nucleotide"
        "NC_000019.9:g.28delT"  || "19"  | 24     | "GT"       | "G"

    }

    @Unroll
    def "malformed hgvs #hgvs location should be null"() {
        when: "obtain the hgvs location"
        def location = new Hgvs(hgvs).getSequenceLocation(genomeSequenceFastaFile)

        then:
        location == null

        where:
        hgvs << ["NC_000019.9:g.14ins", "NC_000019.9:g.14del", "NC_000019.9:g.14dup",
                "NC_000018.9:g.20573598_20573599delTAdelTA", "NC_000016.10:g.2088677_2088679delTGAinsT"]
    }
}
