package org.opencb.biodata.tools.variant.stats;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantAggregatedVcfFactory;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jmmut on 2015-08-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedStatsCalculator {
    protected Properties tagMap;
    protected Map<String, String> reverseTagMap;

    protected final static String COMMA = ",";
    protected final static String DOT = "\\.";   // a literal dot. extracted to avoid confusion and avoid using the wrong "." with split()
    private final static Pattern numNum = Pattern.compile("^(\\d+)[|/](\\d+)$");
    protected final static String cohortSeparator = "_";
    protected final List<String> statsTags = new ArrayList<>(Arrays.asList("AC", "AN", "AF", "GTC", "GTS"));

    public VariantAggregatedStatsCalculator() {
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
    public VariantAggregatedStatsCalculator(Properties tagMap) {
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

    public void calculate(List<Variant> variants) {
        for (Variant variant : variants) {
            calculate(variant);
        }
    }

    public void calculate(Variant variant) {
        for (StudyEntry study : variant.getStudies()) {
            calculate(variant, study);
        }
    }

    /**
     * @param variant remains unchanged if the VariantSourceEntry is not inside
     * @param study stats are written here
     */
    public void calculate(Variant variant, StudyEntry study) {
//        Map<String, String> infoMap = VariantAggregatedVcfFactory.getInfoMap(info);
        if (study.getFiles().isEmpty()) {
            return;
        }
        FileEntry fileEntry = study.getFiles().get(0);
        Map<String, String> infoMap = fileEntry.getAttributes();
        int numAllele = 0;
        String reference;
        String[] alternateAlleles;
        if (fileEntry.getCall() != null && !fileEntry.getCall().isEmpty()) {
            String[] ori = fileEntry.getCall().split(":");
            numAllele = Integer.parseInt(ori[3]);
            alternateAlleles = ori[2].split(",");
            reference = ori[1];
        } else {
            reference = variant.getReference();
            alternateAlleles = new String[]{variant.getAlternate()};
        }
        if (tagMap != null) {
            parseMappedStats(variant, study, numAllele, reference, alternateAlleles, infoMap);
        } else {
            parseStats(variant, study, numAllele, reference, alternateAlleles, infoMap);
        }
    }

    /**
     * Looks for tags contained in statsTags and calculates stats parsing them.
     * @param variant
     * @param file
     * @param numAllele
     * @param reference
     * @param alternateAlleles
     * @param info
     */
    protected void parseStats(Variant variant, StudyEntry file, int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        VariantStats vs = new VariantStats(variant);
        Map<String, String> stats = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : info.entrySet()) {

            String infoTag = entry.getKey();
            String infoValue = entry.getValue();

            if (statsTags.contains(infoTag)) {
                stats.put(infoTag, infoValue);
            }
        }

        calculate(variant, file, numAllele, reference, alternateAlleles, stats, vs);

        file.setStats(StudyEntry.DEFAULT_COHORT, vs);
    }

    /**
     * Looks in the info map for keys present as values in the tagMap passed in the constructor
     * VariantAggregatedStatsCalculator(Properties tagMap), and calculates stats for each cohort described in the tagMap.
     * @param variant
     * @param file
     * @param numAllele
     * @param reference
     * @param alternateAlleles
     * @param info
     */
    protected void parseMappedStats(Variant variant, StudyEntry file, int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        Map<String, Map<String, String>> cohortStats = new LinkedHashMap<>();   // cohortName -> (statsName -> statsValue): EUR->(AC->3,2)
        for (Map.Entry<String, String> entry : info.entrySet()) {
            if (reverseTagMap.containsKey(entry.getKey())) {
                String opencgaTag = reverseTagMap.get(entry.getKey());
                String[] tagSplit = opencgaTag.split(DOT);
                String cohortName = tagSplit[0];
                String statName = tagSplit[1];
                Map<String, String> parsedValues = cohortStats.get(cohortName);
                if (parsedValues == null) {
                    parsedValues = new LinkedHashMap<>();
                    cohortStats.put(cohortName, parsedValues);
                }
                parsedValues.put(statName, entry.getValue());
            }
        }

        for (String cohortName : cohortStats.keySet()) {
            VariantStats vs = new VariantStats(variant);
            calculate(variant, file, numAllele, reference, alternateAlleles, cohortStats.get(cohortName), vs);
            file.setCohortStats(cohortName, vs);
        }
    }

    /**
     * sets (if the map of attributes contains AF, AC, AF and GTC) alleleCount, refAlleleCount, maf, mafAllele, alleleFreq and genotypeCounts,
     * @param variant
     * @param sourceEntry
     * @param numAllele
     * @param reference
     * @param alternateAlleles
     * @param attributes
     * @param variantStats results are returned by reference here
     */
    protected void calculate(Variant variant, StudyEntry sourceEntry, int numAllele, String reference, String[] alternateAlleles,
                             Map<String, String> attributes, VariantStats variantStats) {

        if (attributes.containsKey("AN") && attributes.containsKey("AC")) {
            int total = Integer.parseInt(attributes.get("AN"));
            String[] alleleCountString = attributes.get("AC").split(COMMA);

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
            String[] afs = attributes.get("AF").split(COMMA);
            if (afs.length == alternateAlleles.length) {
                variantStats.setAltAlleleFreq(Float.parseFloat(afs[numAllele]));
                if (variantStats.getMaf() == -1) {  // in case that we receive AFs but no ACs
                    if (variantStats.getRefAlleleFreq() < 0) {
                        variantStats.setRefAlleleFreq(1 - variantStats.getAltAlleleFreq());
                    }

                    float sumFreq = 0;
                    for (String af : afs) {
                        sumFreq += Float.parseFloat(af);
                    }
                    float maf = 1 - sumFreq;
                    String mafAllele = variantStats.getRefAllele();

                    for (int i = 0; i < afs.length; i++) {
                        float auxMaf = Float.parseFloat(afs[i]);
                        if (auxMaf < maf) {
                            maf = auxMaf;
                            mafAllele = alternateAlleles[i];
                        }
                    }
                    variantStats.setMaf(maf);
                    variantStats.setMafAllele(mafAllele);
                }
            }
        }

        if (attributes.containsKey("MAF")) {
            String[] mafs = attributes.get("MAF").split(COMMA);
            if (mafs.length == alternateAlleles.length) {
                float maf = Float.parseFloat(mafs[numAllele]);
                variantStats.setMaf(maf);
                if (attributes.containsKey("MA")) { // Get the minor allele
                    String ma = attributes.get("MA");
                    variantStats.setMafAllele(ma);
                    if (variantStats.getAltAlleleFreq() < 0 || variantStats.getRefAlleleFreq() < 0) {
                        if (ma.equals(variantStats.getRefAllele())) {
                            variantStats.setRefAlleleFreq(maf);
                            variantStats.setAltAlleleFreq(1 - maf);
                        } else if (ma.equals(variantStats.getAltAllele())) {
                            variantStats.setRefAlleleFreq(1 - maf);
                            variantStats.setAltAlleleFreq(maf);
                        } // It may happen that the MA is none of the variant alleles. Just skip
                    }
                }
            }
        }
        if (attributes.containsKey("GTC")) {
            String[] gtcs = attributes.get("GTC").split(COMMA);
            if (attributes.containsKey("GTS")) {    // GTS contains the format like: GTS=GG,GT,TT or GTS=A1A1,A1R,RR
                addGenotypeWithGTS(attributes, gtcs, reference, alternateAlleles, numAllele, variantStats);
            } else {
                for (int i = 0; i < gtcs.length; i++) {
                    String[] gtcSplit = gtcs[i].split(":");
                    Integer alleles[] = new Integer[2];
                    Integer gtc = 0;
                    String gt = null;
                    boolean parseable = true;
                    if (gtcSplit.length == 1) { // GTC=0,5,8
                        getGenotype(i, alleles);
                        gtc = Integer.parseInt(gtcs[i]);
                        gt = VariantVcfFactory.mapToMultiallelicIndex(alleles[0], numAllele) + "/" + VariantVcfFactory.mapToMultiallelicIndex(alleles[1], numAllele);
                    } else {    // GTC=0/0:0,0/1:5,1/1:8
                        Matcher matcher = numNum.matcher(gtcSplit[0]);
                        if (matcher.matches()) {    // number/number:number
                            alleles[0] = Integer.parseInt(matcher.group(1));
                            alleles[1] = Integer.parseInt(matcher.group(2));
                            gtc = Integer.parseInt(gtcSplit[1]);
                            gt = VariantVcfFactory.mapToMultiallelicIndex(alleles[0], numAllele) + "/" + VariantVcfFactory.mapToMultiallelicIndex(alleles[1], numAllele);
                        } else {
                            if (gtcSplit[0].equals("./.")) {    // ./.:number
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
    }

    public static void addGenotypeWithGTS(Map<String, String> attributes, String[] splitsGTC,
                                          String reference, String[] alternateAlleles, int numAllele, VariantStats cohortStats) {
        if (attributes.containsKey("GTS")) {
            String splitsGTS[] = attributes.get("GTS").split(COMMA);
            if (splitsGTC.length == splitsGTS.length) {
                for (int i = 0; i < splitsGTC.length; i++) {
                    String gt = splitsGTS[i];
                    int gtCount = Integer.parseInt(splitsGTC[i]);

                    Genotype g = VariantAggregatedVcfFactory.parseGenotype(gt, numAllele, reference, alternateAlleles);
                    if (g != null) {
                        cohortStats.addGenotype(g, gtCount);
                    }
                }
            }
        }
    }

    public static Set<String> getCohorts(Properties tagMap) {
        Set<String> cohorts = null;
        if (tagMap != null) {
            cohorts = new LinkedHashSet<>();
            for (String key : tagMap.stringPropertyNames()) {
                int index = key.indexOf(".");
                if (index >= 0) {
                    cohorts.add(key.substring(0, index));
                }
            }
        }
        
        return cohorts;
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
