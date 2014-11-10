package org.opencb.biodata.formats.variant.hgvs

import net.sf.picard.reference.FastaSequenceIndex
import net.sf.picard.reference.IndexedFastaSequenceFile
import org.opencb.biodata.models.variant.Variant
import spock.lang.Unroll

import java.nio.file.Paths
import java.text.ParseException

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
    def "variant of #hgvs should be #chr:#start:#reference->#alternate"() {
        when: "obtain the hgvs location"
        Variant variant = new Hgvs(hgvs).getVariant(genomeSequenceFastaFile)

        then: "validate the location chromosome, start, reference and alternate"
        variant.getChromosome() == chr
        variant.getStart() == start
        variant.getEnd() == end
        variant.getReference() == reference
        variant.getAlternate() == alternate

        where:
        hgvs                          || chr   | start  | end | reference | alternate
        "NC_000001.9:g.35G>A"         || "1"   | 35     | 35  | "G"       | "A"
        "NC_000014.9:g.35insA"        || "14"  | 35     | 35  | "-"       | "A"
        "NC_000024.10:g.35delG"       || "Y"   | 35     | 35  | "G"       | "-"
        "NC_000019.9:g.14del"         || "19"  | 14     | 14  | "C"       | "-"
        and: "one nucleotide duplication is treated like one insertion"
        "NC_000019.9:g.14dupC"        || "19"  | 14     | 14  | "-"       | "C"
        "NC_000019.9:g.14dup"         || "19"  | 14     | 14  | "-"       | "C"
        and: "multiple nucleotide indels"
        "NC_000004.8:g.692_694delGAC" || "4"   | 692    | 694 | "GAC"     | "-"
        "NC_000019.9:g.34_36del"      || "19"  | 34     | 36  | "TGC"     | "-"
    }

    @Unroll
    def "malformed hgvs #hgvs location should throwns ParseException"() {
        when: "obtain the hgvs location"
        new Hgvs(hgvs).getVariant(genomeSequenceFastaFile)

        then:
        thrown(ParseException)

        where:
        hgvs << ["NC_000019.9:g.14ins", "NC_000018.9:g.20573598_20573599delTAdelTA"]
    }

    @Unroll
    def "unimplemented hgvs #hgvs transformation should return null"() {
        when: "obtain the hgvs location"
        def variant = new Hgvs(hgvs).getVariant(genomeSequenceFastaFile)

        then:
        variant == null

        where:
        hgvs << ["NC_000016.10:g.2088677_2088679delTGAinsT"]
    }
}
