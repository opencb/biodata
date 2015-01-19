package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactory extends VariantVcfFactory {

    @Override
    protected void parseSplitSampleData(Variant variant, VariantSource source, String[] fields, 
            String[] alternateAlleles, String[] secondaryAlternates, int alleleIdx) 
            throws NonStandardCompliantSampleField {
        // Nothing to do
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

        addStats(variant, source, alternateAlleles);
    }

    private void addStats(Variant variant, VariantSource source, String[] alternateAlleles) {
        VariantSourceEntry file = variant.getSourceEntry(source.getFileId(), source.getStudyId());

        if (file.hasAttribute("AN") && file.hasAttribute("AC")) {
            int total = Integer.parseInt(file.getAttribute("AN"));
            String[] alleleCountString = file.getAttribute("AC").split(",");

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

            VariantStats vs = new VariantStats();

            vs.setMaf(maf);
            vs.setMafAllele(mafAllele);

            file.setStats(vs);
        }

    }

}
