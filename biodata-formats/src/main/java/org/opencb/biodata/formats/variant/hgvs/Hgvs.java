package org.opencb.biodata.formats.variant.hgvs;

import net.sf.picard.reference.FastaSequenceIndex;
import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;
import org.apache.commons.lang.StringEscapeUtils;
import org.opencb.biodata.formats.feature.refseq.Refseq;
import org.opencb.biodata.formats.variant.clinvar.v19jaxb.SequenceLocationType;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.math.BigInteger;
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
    private static final int SEQ_WINDOWS_SIZE = 50;

    private String accession;
    private String type;
    private BigInteger start;
    private BigInteger stop;
    private String change;

    public Hgvs(String hgvs) {
        // parse the hgvs
        String regEx = "(?<"+ACCESSION+">N\\S+):(?<"+TYPE+">\\w+).((?<"+START+">\\d*)_?(?<"+STOP+">\\d*))(?<"+CHANGE+">.+)";
        Pattern pattern = Pattern.compile(regEx);
        
        Matcher matcher = pattern.matcher(hgvs);
        matcher.find();

        this.accession = matcher.group(Hgvs.ACCESSION);
        this.type = matcher.group(Hgvs.TYPE);
        this.start = new BigInteger(matcher.group(Hgvs.START));
        // TODO: que pasa si no hay stop
        if (stop != null && !"".equals(stop)) {
            this.stop = new BigInteger(matcher.group(Hgvs.STOP));
        } else {
            stop = this.start;
        }
        this.change = StringEscapeUtils.unescapeXml(matcher.group(Hgvs.CHANGE));
    }

    public SequenceLocationType getSequenceLocation(IndexedFastaSequenceFile genomeSequenceFastaFile) {

        SequenceLocationType location = new SequenceLocationType();
        // check that the HGVS is genomic
        if (type.equals(GENOMIC_HGVS_TYPE)) {
            // chr, start and stop
            location.setChr(Refseq.refseqNCAccessionToChromosome(accession));
            location.setStart(start);
            location.setStop(stop);
            location.setAccession(accession);

            // TODO: meter deleccion aqui, es para filtrar los cambios de tipo "insATGdelGTA"
            if (change.split("ins").length > 2) {
                System.out.println("Doble");
            }
            // process change to obtain reference, alternative and shift start if needed
            if (change.contains(">")) {
                location = locationFromSNV(location);
            } else if (change.contains("ins") || change.contains("dup")) {
                location = locationFromInsertion(location, genomeSequenceFastaFile);
            } else if (change.contains("del")) {
                location = locationFromDeletion(location, genomeSequenceFastaFile);
            } else {
                // TODO: ¿o devolvemos un location vacio para que el cliente sepa que es?
                location = null;
            }
        }

        return location;
    }

    private SequenceLocationType locationFromSNV(SequenceLocationType location) {
        String[] changeNucleotides = change.split(">");
        location.setReferenceAllele(changeNucleotides[0]);
        location.setAlternateAllele(changeNucleotides[1]);
        return location;
    }

    private SequenceLocationType locationFromInsertion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        String insertion = null;
        try {
            insertion = change.split("(ins)|(dup)")[1];
            String referenceString = null;

            if (insertion.length() == 1) {
                long sequenceStart = location.getStart().longValue()-SEQ_WINDOWS_SIZE < 1 ? 1 : location.getStart().longValue()-SEQ_WINDOWS_SIZE;
                ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(location.getChr(), sequenceStart, location.getStart().longValue());

                try {
                    char[] reference = new String(seq.getBases(), "UTF-8").toCharArray();
                    int offset = offset(reference, insertion.charAt(0));
                    referenceString = "" + reference[reference.length - (offset+1)];
                    // TODO: no me gusta ese constructor de biginteger. Estudiar forma de trabajar con biginteger
                    location.setStart(location.getStart().subtract(new BigInteger("" + offset)));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(location.getChr(), location.getStart().longValue(), location.getStart().longValue());
                try {
                    referenceString = new String(seq.getBases(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            location.setReferenceAllele(referenceString);
            location.setAlternateAllele(referenceString + insertion);
        } catch (ArrayIndexOutOfBoundsException e) {
            // TODO: de momento no expcepcion
            //throw new ParseException("Hgvs change malformed: " + hgvsChange, hgvsChange.length());
            // TODO: ¿mensaje de log?
            location = null;
        }



        return location;
    }


    private SequenceLocationType locationFromDeletion(SequenceLocationType location, IndexedFastaSequenceFile genomeSequenceFastaFile) {
        try {
            String deletion = change.split("del")[1];
            if (deletion.length() == 1) {
                long sequenceStart = location.getStart().longValue()-SEQ_WINDOWS_SIZE < 1 ? 1 : location.getStart().longValue()-SEQ_WINDOWS_SIZE;
                ReferenceSequence seq = genomeSequenceFastaFile.getSubsequenceAt(location.getChr(), sequenceStart, location.getStop().longValue());
                char[] reference = new String(seq.getBases(), "UTF-8").toCharArray();
                int offset = offset(reference, deletion.charAt(0));
                String referenceString = "" + reference[reference.length - (offset+1)] + reference[reference.length - (offset)];
                location.setReferenceAllele(referenceString);
                location.setAlternateAllele(referenceString.substring(0,1));
                location.setStart(location.getStart().subtract(new BigInteger("" + offset)));
            } else {
                // TODO: deletion of >1 nucleotides
                location = null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            // TODO: de momento no expcepcion
            //throw new ParseException("Hgvs change malformed: " + hgvsChange, hgvsChange.length());
            // TODO: ¿mensaje de log?
            location = null;
        }
        return location;
    }

    // TODO: cambiarle el nombre al metodo
    private int offset(char[] reference, char nucleotide) {
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
