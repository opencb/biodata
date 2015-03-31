package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactory extends VariantVcfFactory {
    
    protected Properties tagMap;
    protected Map<String, String> reverseTagMap;

    private final Pattern numNum = Pattern.compile("^(\\d+)[|/](\\d+)$");
    private final Pattern missing = Pattern.compile("^(\\.)[|/](\\.)$");

    public VariantAggregatedVcfFactory() {
        this(null);
    }

    /**
     * @param tagMap Properties that contains case-sensitive tag mapping for aggregation data.
     * A valid example structure of this file is:
     * EUR.AF=EUR_AF
     * EUR.AC=AC_EUR
     * EUR.AN=EUR_AN
     * EUR.GTC=EUR_GTC
     * ALL.AF=AF
     * ALL.AC=TAC
     * ALL.AN=AN
     * ALL.GTC=GTC
     *
     * where the right side of the '=' is how the values appear in the vcf, and left side is how it will loaded.
     * It must be a bijection, i.e. there must not be repeated entries in any side.
     * The part before the '.' can be any string naming the group. The part after the '.' must be one of AF, AC, AN or GTC.
     */
    public VariantAggregatedVcfFactory(Properties tagMap) {
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
    @Override
    protected void parseSplitSampleData(Variant variant, VariantSource source, String[] fields, 
            String[] alternateAlleles, String[] secondaryAlternates, int alleleIdx) 
            throws NonStandardCompliantSampleField {
        // Nothing to do
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


        if (tagMap == null) {
            parseStats(variant, source, numAllele, alternateAlleles, info);
        } else {
            parseCohortStats(variant, source, numAllele, alternateAlleles, info);
        }
    }

    protected void parseStats(Variant variant, VariantSource source, int numAllele, String[] alternateAlleles, String info) {
        VariantSourceEntry file = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        VariantStats vs = new VariantStats(variant);
        Map<String, String> stats = new LinkedHashMap<>();
        String[] splittedInfo = info.split(";");
        for (String attribute : splittedInfo) {
            String[] assignment = attribute.split("=");
            
            if (assignment.length == 2 && (assignment[0].equals("AC") || assignment[0].equals("AN") 
                    || assignment[0].equals("AF") || assignment[0].equals("GTC"))) {
                stats.put(assignment[0], assignment[1]);
            }
        }
        
        addStats(variant, numAllele, alternateAlleles, stats, vs);
        
        file.setStats(vs);
    }
    
    protected void parseCohortStats (Variant variant, VariantSource source, int numAllele, String[] alternateAlleles, String info) {
        VariantSourceEntry file = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        Map<String, Map<String, String>> cohortStats = new LinkedHashMap<>();   // cohortName -> (statsName -> statsValue): EUR->(AC->3,2)
        String[] splittedInfo = info.split(";");
        for (String attribute : splittedInfo) {
            String[] assignment = attribute.split("=");
            
            if (assignment.length == 2 && reverseTagMap.containsKey(assignment[0])) {
                String opencgaTag = reverseTagMap.get(assignment[0]);
                String[] tagSplit = opencgaTag.split("\\.");
                String cohortName = tagSplit[0];
                String statName = tagSplit[1];
                Map<String, String> parsedValues = cohortStats.get(cohortName);
                if (parsedValues == null) {
                    parsedValues = new LinkedHashMap<>();
                    cohortStats.put(cohortName, parsedValues);
                }
                parsedValues.put(statName, assignment[1]);
            }
        }

        for (String cohortName : cohortStats.keySet()) {
            VariantStats vs = new VariantStats(variant);
            addStats(variant, numAllele, alternateAlleles, cohortStats.get(cohortName), vs);
            file.setCohortStats(cohortName, vs);
        }
        
    }

    protected void addStats(Variant variant, int numAllele, String[] alternateAlleles, Map<String, String> attributes, VariantStats variantStats) {

        if (attributes.containsKey("AN") && attributes.containsKey("AC")) {
            int total = Integer.parseInt(attributes.get("AN"));
            String[] alleleCountString = attributes.get("AC").split(",");
            
            if (alleleCountString.length != alternateAlleles.length) {
                return;
            }

            int[] alleleCount = new int[alleleCountString.length];

            String mafAllele = variant.getReference();
            int referenceCount = total;

            for (int i = 0; i < alleleCountString.length; i++) {
                alleleCount[i] = Integer.parseInt(alleleCountString[i]);
                if (i == numAllele) {
                    variantStats.setAltAlleleCount(alleleCount[i]);
                }
                referenceCount -= alleleCount[i];
            }

            variantStats.setRefAlleleCount(referenceCount);
            float maf = (float) referenceCount / total;

            for (int i = 0; i < alleleCount.length; i++) {
                float auxMaf = (float) alleleCount[i] / total;
                if (auxMaf < maf) {
                    maf = auxMaf;
                    mafAllele = alternateAlleles[i];
                }
            }

            variantStats.setMaf(maf);
            variantStats.setMafAllele(mafAllele);
        }

        if (attributes.containsKey("AF")) {
            String[] afs = attributes.get("AF").split(",");
            if (afs.length == alternateAlleles.length) {
                variantStats.setAltAlleleFreq(Float.parseFloat(afs[numAllele]));
            }
        }
        if (attributes.containsKey("GTC")) {
            String[] gtcs = attributes.get("GTC").split(",");
            for (int i = 0; i < gtcs.length; i++) {
                String[] gtcSplit = gtcs[i].split(":");
                Integer alleles[] = new Integer[2];
                Integer gtc = 0;
                String gt = null;
                boolean parseable = true;
                if (gtcSplit.length == 1) {
                    getGenotype(i, alleles);
                    gtc = Integer.parseInt(gtcs[i]);
                    gt = mapToMultiallelicIndex(alleles[0], numAllele) + "/" + mapToMultiallelicIndex(alleles[1], numAllele);
                } else {
                    Matcher matcher = numNum.matcher(gtcSplit[0]);
                    if (matcher.matches()) {
                        alleles[0] = Integer.parseInt(matcher.group(1));
                        alleles[1] = Integer.parseInt(matcher.group(2));
                        gtc = Integer.parseInt(gtcSplit[1]);
                        gt = mapToMultiallelicIndex(alleles[0], numAllele) + "/" + mapToMultiallelicIndex(alleles[1], numAllele);
                    } else {
                        if (gtcSplit[0].equals("./.")) {
                            alleles[0] = -1;
                            alleles[1] = -1;
                            gtc = Integer.parseInt(gtcSplit[1]);
                            gt = "./.";
                        } else {
                            parseable = false;
                        }
                    }
                }
                if (parseable) {
                    Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAllele]);
                    variantStats.addGenotype(genotype, gtc);
                }
            }
        }

    }
    /**
     * returns in alleles[] the genotype specified in index in the sequence:
     * 0/0, 0/1, 1/1, 0/2, 1/2, 2/2, 0/3...
     * @param index in this sequence, starting in 0
     * @param alleles returned genotype.
     */
    public static void getGenotype(int index, Integer alleles[]) {
//        index++;
//        double value = (-3 + Math.sqrt(1 + 8 * index)) / 2;    // slower than the iterating version, right?
//        alleles[1] = new Double(Math.ceil(value)).intValue();
//        alleles[0] = alleles[1] - ((alleles[1] + 1) * (alleles[1] +2) / 2 - index);
        
        int cursor = 0;
        final int MAX_ALLOWED_ALLELES = 100;   // should we allow more than 100 alleles?
        for (int i = 0; i < MAX_ALLOWED_ALLELES; i++) {
            for (int j = 0; j <= i; j++) {
                if (cursor == index) {
                    alleles[0] = j;
                    alleles[1] = i;
                    return;
                }
                cursor++;
            }
        }
    }
}
