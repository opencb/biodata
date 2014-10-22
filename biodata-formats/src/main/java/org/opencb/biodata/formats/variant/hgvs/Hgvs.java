package org.opencb.biodata.formats.variant.hgvs;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;
import org.apache.commons.lang.StringEscapeUtils;
import org.opencb.biodata.formats.feature.refseq.Refseq;
import org.opencb.biodata.formats.variant.clinvar.v19jaxb.SequenceLocationType;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
    private BigInteger start;
    private BigInteger stop;
    private String change;

    public Hgvs(String hgvs) {
        // parse the hgvs
        String regEx = "(?<"+ACCESSION+">N\\S+):(?<"+TYPE+">\\w+).((?<"+START+">\\d*)_?(?<"+STOP+">\\d*))(?<"+CHANGE+">.+)";
        // TODO: make pattern static to be compiled just once
        Pattern pattern = Pattern.compile(regEx);
        
        Matcher matcher = pattern.matcher(hgvs);
        matcher.find();

        this.accession = matcher.group(Hgvs.ACCESSION);
        this.type = matcher.group(Hgvs.TYPE);
        this.start = new BigInteger(matcher.group(Hgvs.START));
        if (stop != null && !"".equals(stop)) {
            this.stop = new BigInteger(matcher.group(Hgvs.STOP));
        } else {
            // TODO: check that if there is no stop, then stop = start
            stop = this.start;
        }
        this.change = StringEscapeUtils.unescapeXml(matcher.group(Hgvs.CHANGE));
    }

    public SequenceLocationType getSequenceLocation(IndexedFastaSequenceFile genomeSequenceFastaFile) {

        SequenceLocationType location = null;
        // check that the HGVS is genomic
        if (type.equals(GENOMIC_HGVS_TYPE)) {
            location = new SequenceLocationType();
            // chr, start and stop
            location.setChr(Refseq.refseqNCAccessionToChromosome(accession));
            location.setStart(start);
            location.setStop(stop);
            location.setAccession(accession);

            // process change to obtain reference, alternative and shift start if needed
            if (change.contains(">")) {
                location = locationFromSNV(location);
            } else if (change.contains("del") && change.contains("ins")) {
                location = locationFromComplexRearrangement(location, genomeSequenceFastaFile);
            } else if (change.contains("ins") || change.contains("dup")) {
                location = locationFromInsertion(location, genomeSequenceFastaFile);
            } else if (change.contains("del")) {
                location = locationFromDeletion(location, genomeSequenceFastaFile);
            } else {
                location = null;
            }
        }

        return location;
    }

    private SequenceLocationType locationFromComplexRearrangement(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        // TODO: implement parser for changes like "NC_000016.10:g.2088677_2088679delTGAins5"
        return null;
    }

    private SequenceLocationType locationFromSNV(SequenceLocationType location) {
        String[] changeNucleotides = change.split(">");
        location.setReferenceAllele(changeNucleotides[0]);
        location.setAlternateAllele(changeNucleotides[1]);
        return location;
    }

    private SequenceLocationType locationFromInsertion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        try {
            String insertion = change.split("(ins)|(dup)")[1];
            String referenceString = null;

            if (insertion.length() == 1) {
                referenceString = referenceStringFromSingleNucleotideInsertion(location, genomeSequenceFastaFile, insertion, referenceString);
            } else {
                referenceString = referenceStringFromSeveralNucleotideInsertion(location, genomeSequenceFastaFile, referenceString);
            }
            location.setReferenceAllele(referenceString);
            location.setAlternateAllele(referenceString + insertion);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new ParseException("Hgvs change malformed: " + hgvsChange, hgvsChange.length());
//        }
        } catch (Exception e) {
            location = null;
        }

        return location;
    }

    private String referenceStringFromSeveralNucleotideInsertion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile, String referenceString) throws UnsupportedEncodingException {
        ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(location.getChr(), location.getStart().longValue(), location.getStart().longValue());
        try {
            referenceString = new String(seq.getBases(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        return referenceString;
    }

    private String referenceStringFromSingleNucleotideInsertion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile, String insertion, String referenceString) throws UnsupportedEncodingException {
        long sequenceStart = location.getStart().longValue()-SEQ_WINDOWS_SIZE < 1 ? 1 : location.getStart().longValue()-SEQ_WINDOWS_SIZE;
        ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(location.getChr(), sequenceStart, location.getStart().longValue());

        try {
            char[] reference = new String(seq.getBases(), "UTF-8").toCharArray();
            int offset = distanceToPreviousNearestDifferentNucleotide(reference, insertion.charAt(0));
            referenceString = "" + reference[reference.length - (offset+1)];
            location.setStart(location.getStart().subtract(BigInteger.valueOf(offset)));

        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        return referenceString;
    }



    private SequenceLocationType locationFromDeletion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        try {
            String deletion = change.split("del")[1];
            if (deletion.length() == 1) {
                location = locationFromSingleNucleotideDeletion(location, genomeSequenceFastaFile, deletion);
            } else {
                location = locationFromSeveralNucleotideDeletion(location, genomeSequenceFastaFile, deletion);
            }
        } catch (UnsupportedEncodingException e) {
            location = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            //throw new ParseException("Hgvs change malformed: " + hgvsChange, hgvsChange.length());
            location = null;
        }
        return location;
    }

    private SequenceLocationType locationFromSeveralNucleotideDeletion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile, String deletion) {
        // TODO: deletion of >1 nucleotides
        return null;
    }

    private SequenceLocationType locationFromSingleNucleotideDeletion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile, String deletion) throws UnsupportedEncodingException {
        long sequenceStart = location.getStart().longValue()-SEQ_WINDOWS_SIZE < 1 ? 1 : location.getStart().longValue()-SEQ_WINDOWS_SIZE;
        ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(location.getChr(), sequenceStart, location.getStop().longValue());
        char[] reference = new String(seq.getBases(), "UTF-8").toCharArray();
        int offset = distanceToPreviousNearestDifferentNucleotide(reference, deletion.charAt(0));
        String referenceString = "" + reference[reference.length - (offset+1)] + reference[reference.length - (offset)];
        location.setReferenceAllele(referenceString);
        location.setAlternateAllele(referenceString.substring(0,1));
        location.setStart(location.getStart().subtract(BigInteger.valueOf(offset)));
        return location;
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
