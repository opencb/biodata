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

package org.opencb.biodata.formats.variant.hgvs;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import org.opencb.biodata.formats.feature.refseq.RefseqAccession;
import org.opencb.biodata.models.variant.Variant;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by parce on 5/26/14.
 */
public class Hgvs {

    private static final String ACCESSION = "accesion";
    private static final String TYPE = "type";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String CHANGE = "change";

    public static final String HGVS = "hgvs";
    private static final String GENOMIC_HGVS_TYPE = "g";

    private RefseqAccession accession;
    private String type;
    private int start;
    private int stop;
    private String change;

    private static Pattern pattern;

    static {
        String regEx = "(?<"+ACCESSION+">N\\S+):(?<"+TYPE+">\\w+).((?<"+START+">\\d*)_?(?<"+STOP+">\\d*))(?<"+CHANGE+">.+)";
        pattern = Pattern.compile(regEx);
    }

    public Hgvs(String hgvs) {
        // parse the hgvs string
        Matcher matcher = pattern.matcher(hgvs);
        matcher.find();

        this.accession = new RefseqAccession(matcher.group(Hgvs.ACCESSION));
        this.type = matcher.group(Hgvs.TYPE);
        this.start = Integer.parseInt(matcher.group(Hgvs.START));
        String stopString = matcher.group(Hgvs.STOP);
        if (stopString != null && !"".equals(stopString)) {
            this.stop = Integer.parseInt(stopString);
        } else {
            stop = this.start;
        }
        this.change = matcher.group(Hgvs.CHANGE);
        //this.change = StringEscapeUtils.unescapeXml(matcher.group(Hgvs.CHANGE));
    }

    public Variant getVariant() throws ParseException {
        return getVariant(null);
    }

    public Variant getVariant(IndexedFastaSequenceFile genomeSequenceFastaFile) throws ParseException {

        Variant variant = null;
        // check that the HGVS is genomic
        if (type.equals(GENOMIC_HGVS_TYPE)) {
            // chr, start and stop
            String chromosome = accession.getChromosome();

            // process change to obtain reference, alternative and shift start if needed
            if (change.contains(">")) {
                variant = getVariantFromSNV(chromosome);
            } else if (change.contains("del") && change.contains("ins")) {
                variant = getVariantFromComplexRearrangement(chromosome, genomeSequenceFastaFile);
            } else if (change.contains("dup")){
                variant = getVariantFromDuplication(chromosome, genomeSequenceFastaFile);
            } else if (change.contains("del")) {
                variant = getVariantFromDeletion(chromosome, genomeSequenceFastaFile);
            } else if (change.contains("ins")) {
                variant = getVariantFromInsertion(chromosome);
            } else {
                variant = null;
            }
        }

        return variant;
    }

    private Variant getVariantFromComplexRearrangement(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        // TODO: implement parser for changes like "NC_000016.10:g.2088677_2088679delTGAins5"
        return null;
    }

    private Variant getVariantFromSNV(String chromosome) {
        String[] changeNucleotides = change.split(">");
        return new Variant(chromosome, start, stop, changeNucleotides[0], changeNucleotides[1]);
    }

    private Variant getVariantFromInsertion(String chromosome) throws ParseException {
        Variant variant;
        try {
            String insertion = change.split("(ins)")[1];
            String referenceString = "-";
            variant = new Variant(chromosome, start, stop, referenceString, insertion);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Hgvs insertion malformed: " + change, change.length());
        }

        return variant;
    }

    private Variant getVariantFromDuplication(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        String duplicatedNucleotides;
        String[] fields = change.split("(dup)");
        if (fields.length == 2) {
            // example g.187120195dupA or g.307_308dupTG
            duplicatedNucleotides = fields[1];
        } else {
            // example g.413dup or g.307_308dup
            if (genomeSequenceFastaFile != null) {
                duplicatedNucleotides = new String(genomeSequenceFastaFile.getSubsequenceAt(chromosome, start, stop).getBases());
            } else {
                // in this case, without genome sequence fasta file, duplicated nucleotides cannot be obtained
                duplicatedNucleotides = null;
            }
        }

        Variant variant = null;
        if (duplicatedNucleotides != null) {
            String referenceString = "-";
            variant = new Variant(chromosome, start, stop, referenceString, duplicatedNucleotides);
        }

        return variant;
    }

    private Variant getVariantFromDeletion(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile) throws ParseException {
        String deletedNucleotides;
        String[] fields = change.split("(del)");
        if (fields.length == 2) {
            deletedNucleotides = fields[1];
        } else if (fields.length > 2) {
            throw new ParseException("Hgvs deletion malformed: " + change, change.lastIndexOf("del"));
        } else {
            if (genomeSequenceFastaFile != null) {
                deletedNucleotides = new String(genomeSequenceFastaFile.getSubsequenceAt(chromosome, start, stop).getBases());
            } else {
                // in this case, without genome sequence fasta file, deleted nucleotides cannot be obtained
                deletedNucleotides = null;
            }
        }

        Variant variant = null;
        if (deletedNucleotides != null) {
            String alternateString = "-";
            variant = new Variant(chromosome, start, stop, deletedNucleotides, alternateString);
        }

        return variant;
    }

    public String getAssembly() {
        return this.accession.getAssembly();
    }
}
