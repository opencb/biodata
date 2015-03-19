package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.ArrayUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;
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
     * contains tag mapping for aggregation data.
     * A valid example structure of this file is:
     * EUR.AF=EUR_AF
     * EUR.AC=AC_EUR
     * EUR.AN=EUR_AN
     * EUR.GTC=EUR_GTC
     * ALL.AF=AF
     * ALL.AC=TAC
     * ALL.AN=AN
     * ALL.GTC=GTC
     * GROUPS_ORDER=EUR,ALL
     * 
     * where the right side of the '=' is how the values appear in the vcf, and left side is how it will loaded.
     * It must be a bijection, i.e. there must not be repeated entries in any side.
     * The part before the '.' can be any string naming the group. The part after the '.' must be one of AF, AC, AN or GTC.
     * The special tag 'GROUPS_ORDER' can be used to specify the order of the comma separated values for populations in tags such as MAF.
     */
    private Properties tagMap;
    private Map<String, String> reverseTagMap;

    public VariantVcfEVSFactory() {
        this(null);
    }

    public VariantVcfEVSFactory(Properties tagMap) {
        this.tagMap = tagMap;
        if (tagMap != null) {
            this.reverseTagMap = new LinkedHashMap<>(tagMap.size());
            for (String tag : tagMap.stringPropertyNames()) {
                this.reverseTagMap.put(tagMap.getProperty(tag), tag);
            }
        } else {
            this.reverseTagMap = null;
        }
    }


    /**
     * Creates a list of Variant objects using the fields in a record of a VCF
     * file. A new Variant object is created per allele, so several of them can
     * be created from a single line.
     *
     * @param source
     * @param line   Contents of the line in the file
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
        Set<String> ids = new HashSet<>(Arrays.asList(id.split(";")));
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
            String[] secondaryAlternates = getSecondaryAlternates(variant, keyFields.getNumAllele(), alternateAlleles);
            VariantSourceEntry file = new VariantSourceEntry(source.getFileId(), source.getStudyId(), secondaryAlternates, format);
            variant.addSourceEntry(file);

            try {
                parseSplitSampleData(variant, source, fields, alternateAlleles, secondaryAlternates, i + 1);
                setOtherFields(variant, source, ids, quality, filter, info, format, keyFields.getNumAllele(), alternateAlleles, line);
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
    protected void setOtherFields(Variant variant, VariantSource source, Set<String> ids, float quality, String filter,
                                  String info, String format, int numAllele, String[] alternateAlleles, String line) {
        // Fields not affected by the structure of REF and ALT fields
        variant.setIds(ids);
        VariantSourceEntry sourceEntry = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        if (quality > -1) {
            sourceEntry.addAttribute("QUAL", String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            sourceEntry.addAttribute("FILTER", filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, source.getFileId(), source.getStudyId(), info, numAllele);
        }
        sourceEntry.setFormat(format);
        sourceEntry.addAttribute("src", line);


        if (tagMap == null) {   // whether we can parse population stats or not
            parseEVSAttributes(variant, source, numAllele, alternateAlleles);
        } else {
            parseCohortEVSInfo(variant, sourceEntry, info, numAllele, alternateAlleles);
        }
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
            String splitsGTC[] = file.getAttribute("GTC").split(",");
            addGenotype(variant, file, splitsGTC, alternateAlleles, numAllele, stats);
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

            int val1 = mapToMultiallelicIndex(allele1, numAllele);
            int val2 = mapToMultiallelicIndex(allele2, numAllele);

            return new Genotype(val1 + "/" + val2, variant.getReference(), variant.getAlternate());
            
//            if ((allele1 == 0 || allele1 == (numAllele + 1)) && (allele2 == 0 || allele2 == (numAllele + 1))) {
//
//                allele1 = allele1 > 1 ? 1 : allele1;
//                allele2 = allele2 > 1 ? 1 : allele2;
//                g = new Genotype(allele1 + "/" + allele2, variant.getReference(), variant.getAlternate());
//
//                return g;
//            } else {
//                return new Genotype("./.", variant.getReference(), variant.getAlternate());
//            }
        }

        m = refRef.matcher(gt);
        if (m.matches()) { // RR
            g = new Genotype(variant.getReference() + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
            return g;
        }

        m = altNum.matcher(gt);
        if (m.matches()) { // A1,A2,A3
            int val = Integer.parseInt(m.group(1));
            val = mapToMultiallelicIndex(val, numAllele);
            return new Genotype(val + "/" + val, variant.getReference(), variant.getAlternate());
        }

        m = altNumaltNum.matcher(gt);
        if (m.matches()) { // A1A2,A1A3...
            int val1 = Integer.parseInt(m.group(1));
            int val2 = Integer.parseInt(m.group(2));
            val1 = mapToMultiallelicIndex(val1, numAllele);
            val2 = mapToMultiallelicIndex(val2, numAllele);
            return new Genotype(val1 + "/" + val2, variant.getReference(), variant.getAlternate());
        }

        m = altNumRef.matcher(gt);
        if (m.matches()) { // A1R, A2R
            int val1 = Integer.parseInt(m.group(1));
            val1 = mapToMultiallelicIndex(val1, numAllele);
            return new Genotype(val1 + "/" + 0, variant.getReference(), variant.getAlternate());
        }

        return null;
    }

    /**
     * In multiallelic variants, we have a list of alternates, where numAllele is the one whose variant we are parsing now.
     * If we are parsing the first variant (numAllele == 0) A1 refers to first alternative, (i.e. alternateAlleles[0]), A2 to 
     * second alternative (alternateAlleles[1]), and so on.
     * However, if numAllele == 1, A1 refers to second alternate (alternateAlleles[1]), A2 to first (alternateAlleles[0]) and higher alleles remain unchanged.
     * Moreover, if NumAllele == 2, A1 is third alternate, A2 is first alternate and A3 is second alternate.
     * It's also assumed that A0 would be the reference, so it remains unchanged too.
     * 
     * This pattern of the first allele moving along (and swapping) is what describes this function. 
     * Also, look VariantVcfFactory.getSecondaryAlternates().
     * @param parsedAllele the value of parsed alleles. e.g. 1 if genotype was "A1" (first allele).
     * @param numAllele current variant of the alternates.
     * @return the correct allele index depending on numAllele.
     */
    private int mapToMultiallelicIndex(int parsedAllele, int numAllele) {
        int correctedAllele = parsedAllele;
        if (parsedAllele != 0) {
            if (parsedAllele == numAllele + 1) {
                correctedAllele = 1;
            } else if (parsedAllele < numAllele + 1) {
                correctedAllele = parsedAllele + 1;
            }
        }
        return correctedAllele;
    }


    private void parseCohortEVSInfo(Variant variant, VariantSourceEntry sourceEntry, String info
            , int numAllele, String[] alternateAlleles) {
        if (tagMap != null) {
            for (String key : sourceEntry.getAttributes().keySet()) {
                String opencgaTag = reverseTagMap.get(key);
                String[] values = sourceEntry.getAttribute(key).split(",");
                if (opencgaTag != null) {
                    String[] opencgaTagSplit = opencgaTag.split("\\."); // a literal point
                    if (opencgaTagSplit.length == 2) {
                        String cohort = opencgaTagSplit[0];
                        VariantStats cohortStats = sourceEntry.getCohortStats(cohort);
                        if (cohortStats == null) {
                            cohortStats = new VariantStats(variant);
                            sourceEntry.setCohortStats(cohort, cohortStats);
                        }
                        switch (opencgaTagSplit[1]) {
                            case "AC":
                                cohortStats.setAltAlleleCount(Integer.parseInt(values[numAllele]));
                                cohortStats.setRefAlleleCount(Integer.parseInt(values[values.length - 1]));    // ref allele count is the last one
                                break;
                            case "AF":
                                cohortStats.setAltAlleleFreq(Float.parseFloat(values[numAllele]));
                                cohortStats.setRefAlleleFreq(Float.parseFloat(values[values.length - 1]));
                                break;
                            case "AN":
                                // TODO implement this. also, take into account that needed fields may not be processed yet
                                break;
                            case "GTC":
                                addGenotype(variant, sourceEntry, values, alternateAlleles, numAllele, cohortStats);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (key.equals("MAF")) {
                    String groups_order = tagMap.getProperty("GROUPS_ORDER");
                    if (groups_order != null) {
                        String[] populations = groups_order.split(",");
                        if (populations.length == values.length) {
                            for (int i = 0; i < values.length; i++) {   // each value has the maf of each population
                                float maf = Float.parseFloat(values[i]) / 100;  // from [0, 100] (%) to [0, 1]
                                VariantStats cohortStats = sourceEntry.getCohortStats(populations[i]);
                                if (cohortStats == null) {
                                    cohortStats = new VariantStats(variant);
                                    sourceEntry.setCohortStats(populations[i], cohortStats);
                                }
                                cohortStats.setMaf(maf);
                            }
                        }
                    }
                }
            }
            // TODO reprocess stats to complete inferable values. A StatsHolder may be needed to keep values not storables in VariantStats
        }
    }

    private void addGenotype(Variant variant, VariantSourceEntry sourceEntry, String[] splitsGTC, String[] alternateAlleles
            , int numAllele, VariantStats cohortStats) {
        if (sourceEntry.hasAttribute("GTS")) {
            String splitsGTS[] = sourceEntry.getAttribute("GTS").split(",");
            if (splitsGTC.length == splitsGTS.length) {
                for (int i = 0; i < splitsGTC.length; i++) {
                    String gt = splitsGTS[i];
                    int gtCount = Integer.parseInt(splitsGTC[i]);
                    
                    Genotype g = parseGenotype(gt, variant, numAllele, alternateAlleles);
                    if (g != null) {
                        cohortStats.addGenotype(g, gtCount);
                    }
                }
            }
        }
    }

}

