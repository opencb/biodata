package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantVcfEVSFactory extends VariantVcfFactory {

    private final Pattern singleNuc = Pattern.compile("^[ACTG]$");
    private final Pattern singleRef = Pattern.compile("^R$");
    private final Pattern refAlt = Pattern.compile("^([ACTG])([ACTG])$");
    private final Pattern refRef = Pattern.compile("^R{2}$");
    private final Pattern altNum = Pattern.compile("^A(\\d+)$");
    private final Pattern altNumaltNum = Pattern.compile("^A(\\d+)A(\\d+)$");
    private final Pattern altNumRef = Pattern.compile("^A(\\d+)R$");

    /**
     * Creates a list of Variant objects using the fields in a record of a VCF
     * file. A new Variant object is created per allele, so several of them can
     * be created from a single line.
     *
     * @param source
     * @param line Contents of the line in the file
     * @return The list of Variant objects that can be created using the fields from a VCF record
     */
    @Override
    public List<Variant> create(VariantSource source, String line) throws IllegalArgumentException {
        String[] fields = line.split("\t");
        if (fields.length < 8) {
            throw new IllegalArgumentException("Not enough fields provided (min 8)");
        }

        List<Variant> variants = new LinkedList<>();

        String chromosome = fields[0];
        int position = Integer.parseInt(fields[1]);
        String id = fields[2].equals(".") ? "" : fields[2];
        String reference = fields[3].equals(".") ? "" : fields[3];
        String alternate = fields[4].equals(".") ? "" : fields[4];
        String[] alternateAlleles = alternate.split(",");
        float quality = fields[5].equals(".") ? -1 : Float.parseFloat(fields[5]);
        String filter = fields[6].equals(".") ? "" : fields[6];
        String info = fields[7].equals(".") ? "" : fields[7];
        String format = (fields.length <= 8 || fields[8].equals(".")) ? "" : fields[8];

        List<VariantKeyFields> generatedKeyFields = new ArrayList<>();

        for (int i = 0; i < alternateAlleles.length; i++) { // This index is necessary for getting the samples where the mutated allele is present
            String alt = alternateAlleles[i];
            VariantKeyFields keyFields;
            int referenceLen = reference.length();
            int alternateLen = alt.length();

            if (referenceLen == alternateLen) {
                keyFields = createVariantsFromSameLengthRefAlt(position, reference, alt);
            } else if (referenceLen == 0) {
                keyFields = createVariantsFromInsertionEmptyRef(position, alt);
            } else if (alternateLen == 0) {
                keyFields = createVariantsFromDeletionEmptyAlt(position, reference);
            } else {
                keyFields = createVariantsFromIndelNoEmptyRefAlt(position, reference, alt);
            }

            keyFields.setNumAllele(i);

            // Since the reference and alternate alleles won't necessarily match
            // the ones read from the VCF file but they are still needed for
            // instantiating the variants, they must be updated
            alternateAlleles[i] = keyFields.alternate;
            generatedKeyFields.add(keyFields);
        }

        // Now create all the Variant objects read from the VCF record
        for (int i = 0; i < alternateAlleles.length; i++) {
            VariantKeyFields keyFields = generatedKeyFields.get(i);
            Variant variant = new Variant(chromosome, keyFields.start, keyFields.end, keyFields.reference, keyFields.alternate);
            VariantSourceEntry file = new VariantSourceEntry(source.getFileId(), source.getStudyId());
            String[] secondaryAlternates = getSecondaryAlternates(variant, keyFields.getNumAllele(), alternateAlleles);
            file.setSecondaryAlternates(secondaryAlternates);
            file.setFormat(format);
            variant.addSourceEntry(file);

            try {
                parseSplitSampleData(variant, source, fields, alternateAlleles, secondaryAlternates, i + 1);
                setOtherFields(variant, source, id, quality, filter, info, format, keyFields.getNumAllele(), alternateAlleles, line);
                variants.add(variant);
            } catch (NonStandardCompliantSampleField ex) {
                Logger.getLogger(VariantFactory.class.getName()).log(Level.SEVERE,
                        String.format("Variant %s:%d:%s>%s will not be saved\n%s",
                                chromosome, position, reference, alternateAlleles[i], ex.getMessage()));
            }
        }

        return variants;
    }

    @Override
    protected void setOtherFields(Variant variant, VariantSource source, String id, float quality, String filter, 
            String info, String format, int numAllele, String[] alternateAlleles, String line) {
        // Fields not affected by the structure of REF and ALT fields
        variant.setId(id);
        if (quality > -1) {
            variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute("QUAL", String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute("FILTER", filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, source.getFileId(), source.getStudyId(), info, numAllele);
        }
        variant.getSourceEntry(source.getFileId(), source.getStudyId()).setFormat(format);
        variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute("src", line);
        
        parseEVSAttributes(variant, source, numAllele, alternateAlleles);
    }

    private void parseEVSAttributes(Variant variant, VariantSource source, int numAllele, String[] alternateAlleles) {
        VariantSourceEntry file = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        VariantStats stats = new VariantStats(variant);
        if (file.hasAttribute("MAF")) {
            String splitsMAF[] = file.getAttribute("MAF").split(",");
            if (splitsMAF.length == 3) {
                float maf = Float.parseFloat(splitsMAF[2]) / 100;
                stats.setMaf(maf);
            }
        }

        if (file.hasAttribute("GTS") && file.hasAttribute("GTC")) {
            String splitsGTS[] = file.getAttribute("GTS").split(",");
            String splitsGTC[] = file.getAttribute("GTC").split(",");

            if (splitsGTC.length == splitsGTS.length) {
                for (int i = 0; i < splitsGTC.length; i++) {
                    String gt = splitsGTS[i];
                    int gtCount = Integer.parseInt(splitsGTC[i]);


                    Genotype g = parseGenotype(gt, variant, numAllele, alternateAlleles);
                    if (g != null) {
                        stats.addGenotype(g, gtCount);
                    }
                }

                stats.setMafAllele("");
                stats.setMissingAlleles(0);
            }
        }
        file.setStats(stats);
    }

    private Genotype parseGenotype(String gt, Variant variant, int numAllele, String[] alternateAlleles) {
        Genotype g;
        Matcher m;

        m = singleNuc.matcher(gt);

        if (m.matches()) { // A,C,T,G
            g = new Genotype(gt + "/" + gt, variant.getReference(), variant.getAlternate());
            return g;
        }
        m = singleRef.matcher(gt);
        if (m.matches()) { // R
            g = new Genotype(variant.getReference() + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
            return g;
        }

        m = refAlt.matcher(gt);
        if (m.matches()) { // AA,AC,TT,GT,...
            String ref = m.group(1);
            String alt = m.group(2);

            int allele1 = (Arrays.asList(alternateAlleles).indexOf(ref) + 1);
            int allele2 = (Arrays.asList(alternateAlleles).indexOf(alt) + 1);

            if((allele1 == 0 || allele1 == (numAllele + 1)) && (allele2== 0 || allele2 == (numAllele + 1))){

                allele1 = allele1 > 1 ? 1 : allele1;
                allele2 = allele2 > 1 ? 1 : allele2;
                g = new Genotype(allele1 + "/" + allele2, variant.getReference(), variant.getAlternate());

                return g;
            }else{
                return new Genotype("./.", variant.getReference(), variant.getAlternate());
            }
        }

        m = refRef.matcher(gt);
        if (m.matches()) { // RR
            g = new Genotype(variant.getReference() + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
            return g;
        }

        m = altNum.matcher(gt);
        if (m.matches()) { // A1,A2,A3
            int val = Integer.parseInt(m.group(1));
            if (val == numAllele + 1) {
                g = new Genotype(variant.getAlternate() + "/" + variant.getAlternate(), variant.getReference(), variant.getAlternate());
                return g;
            } else {
                return new Genotype("./.", variant.getReference(), variant.getAlternate());
            }
        }

        m = altNumaltNum.matcher(gt);
        if (m.matches()) { // A1A2,A1A3...
            int val1 = Integer.parseInt(m.group(1));
            int val2 = Integer.parseInt(m.group(2));
            if (val1 == numAllele + 1 && val2 == numAllele + 1) {
                g = new Genotype(variant.getAlternate() + "/" + variant.getAlternate(), variant.getReference(), variant.getAlternate());
                return g;
            } else {
                return new Genotype("./.", variant.getReference(), variant.getAlternate());
            }
        }

        m = altNumRef.matcher(gt);
        if (m.matches()) { // A1R, A2R
            int val1 = Integer.parseInt(m.group(1));
            if (val1 == numAllele + 1) {
                g = new Genotype(variant.getAlternate() + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
                return g;
            } else {
                return new Genotype("./.", variant.getReference(), variant.getAlternate());
            }
        }

        return null;
    }

}
