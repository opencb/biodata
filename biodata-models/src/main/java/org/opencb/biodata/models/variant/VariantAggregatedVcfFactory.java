package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactory extends VariantVcfFactory {

    // TODO add AC_NO_REF?
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
    protected Properties tagMap;
    protected Map<String, String> reverseTagMap;

    public VariantAggregatedVcfFactory() {
        this(null);
    }

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

        addStats(variant, source, numAllele, alternateAlleles, info);
    }

    protected void addStats(Variant variant, VariantSource source, int numAllele, String[] alternateAlleles, String info) {
        VariantSourceEntry file = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        VariantStats vs = new VariantStats(variant);
        
        String[] splittedInfo = info.split(";");
        String[] alleleCountString = new String[0];
        for (String attribute : splittedInfo) {
            String[] assignment = attribute.split("=");
            if (assignment.length == 2 && assignment[0].equals("AC")) {
                alleleCountString = assignment[1].split(",");
            }
        }
        if (file.hasAttribute("AN") && file.hasAttribute("AC")) {
            int total = Integer.parseInt(file.getAttribute("AN"));

            if (alleleCountString.length != alternateAlleles.length) {
                return;
            }

            int[] alleleCount = new int[alleleCountString.length];

            String mafAllele = variant.getReference();
            int referenceCount = total;

            for (int i = 0; i < alleleCountString.length; i++) {
                alleleCount[i] = Integer.parseInt(alleleCountString[i]);
                referenceCount -= alleleCount[i];
            }

            float maf = (float) referenceCount / total;

            for (int i = 0; i < alleleCount.length; i++) {
                float auxMaf = (float) alleleCount[i] / total;
                if (auxMaf < maf) {
                    maf = auxMaf;
                    mafAllele = alternateAlleles[i];
                }
            }

            vs.setMaf(maf);
            vs.setMafAllele(mafAllele);
        }
        
        if (file.hasAttribute("GTC")) {
            String[] gtcs = file.getAttribute("GTC").split(",");
            for (int i = 0; i < gtcs.length; i++) {
                Integer alleles[] = new Integer[2];
                getGenotype(i, alleles);
                String gt = mapToMultiallelicIndex(alleles[0], numAllele) + "/" + mapToMultiallelicIndex(alleles[1], numAllele);
                Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAllele]);
                vs.addGenotype(genotype, Integer.parseInt(gtcs[i]));
            }
        }
        
        file.setStats(vs);
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
