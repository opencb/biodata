package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jmmut on 2015-03-25.
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantVcfExacFactory extends VariantAggregatedVcfFactory {

    private static final String AC_HOM = "AC_Hom";
    private static final String AC_HET = "AC_Het";
    private static final String AN_ADJ = "AN_Adj";
    private static final String AC_ADJ = "AC_Adj";
    private static final String COMMA = ",";

    public VariantVcfExacFactory() {
        this(null);
    }

    public VariantVcfExacFactory(Properties tagMap) {
        super(tagMap);
    }

    @Override
    protected void addStats(Variant variant, VariantSource source, int numAllele, String[] alternateAlleles, String info) {
        if (tagMap == null) {
            parseExacAttributes(variant, source, numAllele, alternateAlleles, info);
        } else {
            parseExacCohortAttributes(variant, source, numAllele, alternateAlleles, info);
        }
    }

    private void parseExacAttributes(Variant variant, VariantSource source, int numAllele, String[] alternateAlleles, String info) {
        VariantSourceEntry sourceEntry = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        VariantStats stats = new VariantStats(variant);
        
        if (sourceEntry.hasAttribute(AC_HET)) {   // heterozygous genotype count
            String[] hetCounts = sourceEntry.getAttribute(AC_HET).split(COMMA);
            addHeterozygousGenotypes(variant, numAllele, alternateAlleles, stats, hetCounts);
        }

        if (sourceEntry.hasAttribute(AC_HOM)) {   // homozygous genotype count
            String[] homCounts = sourceEntry.getAttribute(AC_HOM).split(COMMA);
            addHomozygousGenotype(variant, numAllele, alternateAlleles, stats, homCounts);
        }

        if (sourceEntry.hasAttribute(AC_ADJ)) {   // alternative allele counts
            String[] acCounts = sourceEntry.getAttribute(AC_ADJ).split(COMMA);
            if (acCounts.length == alternateAlleles.length) {
                stats.setAltAlleleCount(Integer.parseInt(acCounts[numAllele]));
            }
        }

        if (sourceEntry.hasAttribute(AN_ADJ) && sourceEntry.hasAttribute(AC_ADJ)) { // inferring implicit refallele count
            setRefAlleleCount(stats, Integer.parseInt(sourceEntry.getAttribute(AN_ADJ)), sourceEntry.getAttribute(AC_ADJ).split(COMMA));
        }
        
        if (sourceEntry.hasAttribute(AC_HOM) && sourceEntry.hasAttribute(AC_HET) && sourceEntry.hasAttribute(AN_ADJ)) {   // inferring implicit 0/0 count
            int an = Integer.parseInt(sourceEntry.getAttribute(AN_ADJ));
            addReferenceGenotype(variant, stats, an);
        }

        sourceEntry.setStats(stats);
    }



    private void parseExacCohortAttributes(Variant variant, VariantSource source, int numAllele, String[] alternateAlleles, String info) {
        VariantSourceEntry sourceEntry = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        String[] attributes = info.split(";");
        Map<String, Integer> ans = new LinkedHashMap<>();
        Map<String, String[]> acs = new LinkedHashMap<>();
        for (String attribute : attributes) {
            String[] equalSplit = attribute.split("=");
            if (equalSplit.length == 2) {
                String mappedTag = reverseTagMap.get(equalSplit[0]);
                String[] values = equalSplit[1].split(COMMA);
                if (mappedTag != null) {
                    String[] opencgaTagSplit = mappedTag.split("\\.");   // a literal dot
                    String cohortName = opencgaTagSplit[0];
                    VariantStats cohortStats = sourceEntry.getCohortStats(cohortName);
                    if (cohortStats == null) {
                        cohortStats = new VariantStats(variant);
                        sourceEntry.setCohortStats(cohortName, cohortStats);
                    }
                    switch (opencgaTagSplit[1]) {
                        case "AC":
                            cohortStats.setAltAlleleCount(Integer.parseInt(values[numAllele]));
                            acs.put(cohortName, values);
                            break;
                        case "AN":
                            ans.put(cohortName, Integer.parseInt(values[0]));
                            break;
                        case "HET":
                            addHeterozygousGenotypes(variant, numAllele, alternateAlleles, cohortStats, values);
                            break;
                        case "HOM":
                            addHomozygousGenotype(variant, numAllele, alternateAlleles, cohortStats, values);
                            break;
                    }
                }
            }
        }
        for (String cohortName : sourceEntry.getCohortStats().keySet()) {
            if (ans.containsKey(cohortName)) {
                VariantStats cohortStats = sourceEntry.getCohortStats(cohortName);
                Integer alleleNumber = ans.get(cohortName);
                addReferenceGenotype(variant, cohortStats, alleleNumber);
                setRefAlleleCount(cohortStats, alleleNumber, acs.get(cohortName));
            }
        }
    }

    private static void setRefAlleleCount(VariantStats stats, Integer alleleNumber, String alleleCounts[]) {
        int sum = 0;
        for (String ac : alleleCounts) {
            sum += Integer.parseInt(ac);
        }
        stats.setRefAlleleCount(alleleNumber - sum);
    }
    
    /**
     * Infers the 0/0 genotype count, given that: sum(Heterozygous) + sum(Homozygous) + sum(Reference) = alleleNumber/2
     * @param variant to retrieve the alleles to construct the genotype
     * @param stats where to add the 0/0 genotype count
     * @param alleleNumber total sum of alleles.
     */
    private static void addReferenceGenotype(Variant variant, VariantStats stats, int alleleNumber) {
        int gtSum = 0;
        for (Integer gtCounts : stats.getGenotypesCount().values()) {
            gtSum += gtCounts;
        }
        Genotype genotype = new Genotype("0/0", variant.getReference(), variant.getAlternate());
        stats.addGenotype(genotype, alleleNumber/2 - gtSum);  // assuming diploid sample! be careful if you copy this code!
    }

    /**
     * Adds the heterozygous genotypes to a variant stats. Those are (in this order):
     * 0/1, 0/2, 0/3, 0/4... 1/2, 1/3, 1/4... 2/3, 2/4... 3/4...
     * for a given amount n of alleles, the number of combinations is (latex): \sum_{i=1}^n( \sum_{j=i}^n( 1 ) ), which resolved is n*(n+1)/2
     * @param variant to retrieve the alleles to construct the genotype
     * @param numAllele 
     * @param alternateAlleles
     * @param stats where to add the genotypes count
     * @param hetCounts parsed string
     */
    private static void addHeterozygousGenotypes(Variant variant, int numAllele, String[] alternateAlleles, VariantStats stats, String[] hetCounts) {
        
        if (hetCounts.length == alternateAlleles.length * (alternateAlleles.length + 1) / 2) {
            for (int i = 0; i < hetCounts.length; i++) {
                Integer alleles[] = new Integer[2];
                getHeterozygousGenotype(i, alternateAlleles.length, alleles);
                String gt = mapToMultiallelicIndex(alleles[0], numAllele) + "/" + mapToMultiallelicIndex(alleles[1], numAllele);
                Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAllele]);
                stats.addGenotype(genotype, Integer.parseInt(hetCounts[i]));
            }
        }
    }

    /**
     * Adds the homozygous genotypes to a variant stats. Those are (in this order):
     * 1/1, 2/2, 3/3...
     * @param variant
     * @param numAllele
     * @param alternateAlleles
     * @param stats
     * @param homCounts parsed string
     */
    private void addHomozygousGenotype(Variant variant, int numAllele, String[] alternateAlleles, VariantStats stats, String[] homCounts) {
        if (homCounts.length == alternateAlleles.length) {
            for (int i = 0; i < homCounts.length; i++) {
                Integer alleles[] = new Integer[2];
                getHomozygousGenotype(i+1, alleles);
                String gt = mapToMultiallelicIndex(alleles[0], numAllele) + "/" + mapToMultiallelicIndex(alleles[1], numAllele);
                Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAllele]);
                stats.addGenotype(genotype, Integer.parseInt(homCounts[i]));
            }
        }
    }

    /**
     * returns in alleles[] the heterozygous genotype specified in index in the sequence (in this example for 3 ALT alleles):
     * 0/1, 0/2, 0/3, 1/2, 1/3, 2/3
     * @param index in this sequence, starting in 0
     * @param numAlternativeAlleles note that this ordering requires knowing how many alleles there are
     * @param alleles returned genotype.
     */
    public static void getHeterozygousGenotype(int index, int numAlternativeAlleles, Integer alleles[]) {
//        index++;
//        double value = (-3 + Math.sqrt(1 + 8 * index)) / 2;    // slower than the iterating version, right?
//        alleles[1] = new Double(Math.ceil(value)).intValue();
//        alleles[0] = alleles[1] - ((alleles[1] + 1) * (alleles[1] +2) / 2 - index);

        int cursor = 0;
        for (int i = 0; i < numAlternativeAlleles; i++) {
            for (int j = i+1; j < numAlternativeAlleles +1; j++) {
                if (i != j) {
                    if (cursor == index) {
                        alleles[0] = i;
                        alleles[1] = j;
                        return;
                    }
                    cursor++;
                }
            }
        }
    }
    
    /**
     * returns in alleles[] the homozygous genotype specified in index in the sequence:
     * 0/0, 1/1, 2/2, 3/3
     * @param index in this sequence, starting in 0
     * @param alleles returned genotype.
     */
    public static void getHomozygousGenotype(int index, Integer alleles[]) {
        alleles[0] = alleles[1] = index;
    }
}
