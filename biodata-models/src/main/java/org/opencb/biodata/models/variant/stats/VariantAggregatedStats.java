package org.opencb.biodata.models.variant.stats;

import java.util.Map;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.Variant;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantAggregatedStats extends VariantStats {

    public VariantAggregatedStats() {
    }

    public VariantAggregatedStats(Variant variant) {
        super(variant);
    }

    @Override
    public VariantStats calculate(Map<String, Map<String, String>> samplesData, Map<String, String> attributes, Pedigree pedigree) {
        super.calculate(samplesData, attributes, pedigree);
        
        if (attributes.containsKey("AN") && attributes.containsKey("AC")) {
            int total = Integer.parseInt(attributes.get("AN"));
            String[] alleleCountString = attributes.get("AC").split(",");

//            if (alleleCountString.length != alternateAlleles.length) {
//                return;
//            }

            int[] alleleCount = new int[alleleCountString.length];

            String mafAllele = this.getRefAllele();
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
                    mafAllele = this.getAltAllele();
//                    mafAllele = alternateAlleles[i];
                }
            }

            setMaf(maf);
            setMafAllele(mafAllele);
        }
        
        return this;
    }
    
}
