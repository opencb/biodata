package org.opencb.biodata.formats.variant.vcf4.io;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantVcfEVSReader extends VariantVcfReader {

    private Pattern singleNuc = Pattern.compile("^[ACTG]$");
    private Pattern singleRef = Pattern.compile("^R$");
    private Pattern refAlt = Pattern.compile("^([ACTG])([ACTG])$");
    private Pattern refRef = Pattern.compile("^R{2}$");
    private Pattern altNum = Pattern.compile("^A(\\d+)$");
    private Pattern altNumaltNum = Pattern.compile("^A(\\d+)A(\\d+)$");
    private Pattern altNumRef = Pattern.compile("^A(\\d+)R$");

    public VariantVcfEVSReader(VariantSource source, String filename) {
        super(source, filename);
    }

    @Override
    public List<Variant> read() {
        List<Variant> variants = super.read();

        if (variants != null) {
            for (Variant variant : variants) {
                for (Map.Entry<String, ArchivedVariantFile> fileEntry : variant.getFiles().entrySet()) {
                    VariantStats stats = new VariantStats(variant);
                    ArchivedVariantFile file = fileEntry.getValue();
                    if (file.hasAttribute("MAF")) {
                        String splitsMaf[] = file.getAttribute("MAF").split(",");
                        if (splitsMaf.length == 3) {
                            float maf = Float.parseFloat(splitsMaf[2]) / 100;
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

                                Genotype g = parseGenotype(gt, variant);

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
            }
        }
        
        return variants;
    }

    private Genotype parseGenotype(String gt, Variant variant) {
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
            g = new Genotype(ref + "/" + alt, variant.getReference(), variant.getAlternate());
            return g;
        }

        m = refRef.matcher(gt);
        if (m.matches()) { // RR
            g = new Genotype(variant.getReference() + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
            return g;
        }

        m = altNum.matcher(gt);
        if (m.matches()) { // A1,A2,A3
            int val = Integer.parseInt(m.group(1));
            String altN = variant.getAltAlleles()[val - 1];
            g = new Genotype(altN + "/" + altN, variant.getReference(), variant.getAlternate());
            return g;
        }

        m = altNumaltNum.matcher(gt);
        if (m.matches()) { // A1A2,A1A3...
            int val1 = Integer.parseInt(m.group(1));
            String altN1 = variant.getAltAlleles()[val1 - 1];

            int val2 = Integer.parseInt(m.group(2));
            String altN2 = variant.getAltAlleles()[val2 - 1];

            g = new Genotype(altN1 + "/" + altN2, variant.getReference(), variant.getAlternate());
            return g;
        }

        m = altNumRef.matcher(gt);
        if (m.matches()) {
            int val1 = Integer.parseInt(m.group(1));
            String altN1 = variant.getAltAlleles()[val1 - 1];

            g = new Genotype(altN1 + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
            return g;
        }

        return null;
    }
}
