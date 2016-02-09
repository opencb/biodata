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

package org.opencb.biodata.formats.variant.hgvs

import htsjdk.samtools.reference.FastaSequenceIndex
import htsjdk.samtools.reference.IndexedFastaSequenceFile
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
    def "variant corresponding to HGVS #hgvs should be #chr:#start:#reference -> #alternate"() {
        when: "obtain the hgvs location"
        Variant variant = new Hgvs(hgvs).getVariant(genomeSequenceFastaFile)

        then: "validate the location chromosome, start, reference and alternate"
        variant.getChromosome() == chr
        variant.getStart() == start
        variant.getEnd() == end
        variant.getReference() == reference
        variant.getAlternate() == alternate

        where: "single nucleotide mutations"
        hgvs                           || chr   | start  | end    | reference | alternate
        "NC_000001.11:g.374354G>A"     || "1"   | 374354 | 374354 | "G"       | "A"
        "NC_000014.9:g.481_482insA"    || "14"  | 481    | 482    | ""       | "A"
        "NC_000024.10:g.35delG"        || "Y"   | 35     | 35     | "G"       | ""
        "NC_000019.10:g.14del"         || "19"  | 14     | 14     | "C"       | ""
        "NC_000017.11:g.46739dupC"     || "17"  | 46739  | 46739  | ""       | "C"
        "NC_000019.10:g.14dup"         || "19"  | 14     | 14     | ""       | "C"
        and: "multiple nucleotide indels"
        "NC_000023.11:g.692_694delGAC" || "X"   | 692    | 694    | "GAC"     | ""
        "NC_000019.10:g.34_36del"      || "19"  | 34     | 36     | "TGC"     | ""
        "NC_000007.14:g.792_793insGAG" || "7"   | 792    | 793    | ""       | "GAG"
        "NC_000002.12:g.92_94dupGAC"   || "2"   | 92     | 94     | ""       | "GAC"
        "NC_000019.10:g.34_36dup"      || "19"  | 34     | 36     | ""       | "TGC"
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
        hgvs << ["NC_000016.10:g.2088677_2088679delTGAinsT", "NC_000019.9:c.14ins"]
    }
}
