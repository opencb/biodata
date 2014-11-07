package org.opencb.biodata.formats.variant.hgvs;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;
import org.apache.commons.lang.StringEscapeUtils;
import org.opencb.biodata.formats.feature.RefseqAccession;
import org.opencb.biodata.models.variant.Variant;

import java.io.UnsupportedEncodingException;
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
    private static final int SEQ_WINDOWS_SIZE = 50;

    private String accession;
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

        this.accession = matcher.group(Hgvs.ACCESSION);
        this.type = matcher.group(Hgvs.TYPE);
        this.start = Integer.parseInt(matcher.group(Hgvs.START));
        String stopString = matcher.group(Hgvs.STOP);
        if (stopString != null && !"".equals(stopString)) {
            this.stop = Integer.parseInt(stopString);
        } else {
            // TODO: check that if there is no stop, then stop = start
            stop = this.start;
        }
        this.change = StringEscapeUtils.unescapeXml(matcher.group(Hgvs.CHANGE));
        // TODO: is it necessary the unescapeXml call? Maybe this method should get an "unescaped" string
    }

    public Variant getVariant(IndexedFastaSequenceFile genomeSequenceFastaFile) {

        Variant variant = null;
        // check that the HGVS is genomic
        if (type.equals(GENOMIC_HGVS_TYPE)) {
            // chr, start and stop
            String chromosome = new RefseqAccession(accession).getChromosome();

            // process change to obtain reference, alternative and shift start if needed
            if (change.contains(">")) {
                variant = getVariantFromSNV(chromosome);
            } else if (change.contains("del") && change.contains("ins")) {
                variant = getVariantFromComplexRearrangement(chromosome, genomeSequenceFastaFile);
            } else if (change.contains("ins") || change.contains("dup")) {
                variant = getVariantFromInsertion(chromosome, genomeSequenceFastaFile);
            } else if (change.contains("del")) {
                variant = getVariantFromDeletion(chromosome, genomeSequenceFastaFile);
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
        Variant variant = new Variant(chromosome, start, stop, changeNucleotides[0], changeNucleotides[1]);
        return variant;
    }

    private Variant getVariantFromInsertion(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        Variant variant = null;
        try {
            String insertion = change.split("(ins)|(dup)")[1];
            String referenceString = null;

            if (insertion.length() == 1) {
                referenceString = referenceStringFromSingleNucleotideInsertion(chromosome, genomeSequenceFastaFile, insertion, referenceString);
            } else {
                referenceString = referenceStringFromSeveralNucleotideInsertion(chromosome, genomeSequenceFastaFile, referenceString);
            }
            variant = new Variant(chromosome, start, stop, referenceString, referenceString + insertion);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new ParseException("Hgvs change malformed: " + hgvsChange, hgvsChange.length());
//        }
        } catch (Exception e) {
            variant = null;
        }

        return variant;
    }

    private String referenceStringFromSeveralNucleotideInsertion(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile, String referenceString) throws UnsupportedEncodingException {
        ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(chromosome, start, start);
        try {
            referenceString = new String(seq.getBases(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        return referenceString;
    }

    private String referenceStringFromSingleNucleotideInsertion(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile, String insertion, String referenceString) throws UnsupportedEncodingException {
        int sequenceStart = start - SEQ_WINDOWS_SIZE < 1 ? 1 : start - SEQ_WINDOWS_SIZE;
        ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(chromosome, sequenceStart, start);

        try {
            char[] reference = new String(seq.getBases(), "UTF-8").toCharArray();
            int offset = distanceToPreviousNearestDifferentNucleotide(reference, insertion.charAt(0));
            referenceString = "" + reference[reference.length - (offset+1)];
            start = start - offset;

        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        return referenceString;
    }



    private Variant getVariantFromDeletion(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        Variant variant = null;
        try {
            String deletion = change.split("del")[1];
            if (deletion.length() == 1) {
                variant = getVariantFromSingleNucleotideDeletion(chromosome, genomeSequenceFastaFile, deletion);
            } else {
                variant = getVariantFromSeveralNucleotideDeletion(chromosome, genomeSequenceFastaFile, deletion);
            }
        } catch (UnsupportedEncodingException e) {
            variant = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            //throw new ParseException("Hgvs change malformed: " + hgvsChange, hgvsChange.length());
            variant = null;
        }
        return variant;
    }

    private Variant getVariantFromSeveralNucleotideDeletion(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile, String deletion) {
        // TODO: deletion of >1 nucleotides
        return null;
    }

    private Variant getVariantFromSingleNucleotideDeletion(String chromosome, IndexedFastaSequenceFile genomeSequenceFastaFile, String deletion) throws UnsupportedEncodingException {
        long sequenceStart = start-SEQ_WINDOWS_SIZE < 1 ? 1 : start-SEQ_WINDOWS_SIZE;
        ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(chromosome, sequenceStart, stop);
        char[] reference = new String(seq.getBases(), "UTF-8").toCharArray();
        int offset = distanceToPreviousNearestDifferentNucleotide(reference, deletion.charAt(0));
        String referenceString = "" + reference[reference.length - (offset+1)] + reference[reference.length - (offset)];
        Variant variant = new Variant(chromosome, start - offset, stop, referenceString, referenceString.substring(0, 1));
        return variant;
    }

    private int distanceToPreviousNearestDifferentNucleotide(char[] reference, char nucleotide) {
        int offset = 0;
        for (int j = reference.length-1; j >= 0; j--) {
            if (reference[j] == nucleotide) {
                offset++;
            } else {
                break;
            }
        }
        return offset;
    }

}
