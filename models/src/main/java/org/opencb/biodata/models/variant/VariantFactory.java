package org.opencb.biodata.models.variant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantFactory {

    /**
     * Creates a list of Variant objects using the fields in a record of a VCF 
     * file. A new Variant object is created per allele, so several of them can 
     * be created from a single line.
     * 
     * @param sampleNames Names of the samples in the file
     * @param fields Contents of the line in the file
     * @return The list of Variant objects that can be created using the fields from a VCF record
     */
    public static List<Variant> createVariantFromVcf(List<String> sampleNames, String... fields) {
        if (fields.length < 8) {
            throw new IllegalArgumentException("Not enough fields provided (min 8)");
        }
        
        List<Variant> variants = new LinkedList<>();

        String chromosome = fields[0];
        int position = Integer.parseInt(fields[1]);
        String id = fields[2];
        String reference = fields[3].equals(".") ? "" : fields[3];
        String alternate = fields[4].equals(".") ? "" : fields[4];
        String[] alternateAlleles = alternate.split(",");
        float quality = fields[5].equals(".") ? -1 : Float.parseFloat(fields[5]);
        String filter = fields[6].equals(".") ? "" : fields[6];
        String info = fields[7].equals(".") ? "" : fields[7];
        
        for (int i = 0; i < alternateAlleles.length; i++) { // TODO This index is necessary for getting the samples where the variant is present
            String alt = alternateAlleles[i];
            List<Variant> variantsFromAllele = null;
            int referenceLen = reference.length();
            int alternateLen = alt.length();
            
            if (referenceLen == alternateLen) {
                variantsFromAllele = createVariantsFromSameLengthRefAlt(chromosome, position, reference, alt);
            } else if (referenceLen == 0) {
                Variant variant = createVariantsFromInsertionEmptyRef(chromosome, position, alt);
                setOtherFields(variant, id, quality, filter, info);
                variants.add(variant);
            } else if (alternateLen == 0) {
                Variant variant = createVariantsFromDeletionEmptyAlt(chromosome, position, reference);
                setOtherFields(variant, id, quality, filter, info);
                variants.add(variant);
            } else {
                variantsFromAllele = createVariantsFromIndelNoEmptyRefAlt(chromosome, position, reference, alt);
            }
            
            if (variantsFromAllele == null || variantsFromAllele.isEmpty()) {
                continue;
            }

//            // Fields not affected by the structure of REF and ALT fields
//            variant.setId(id);
//            if (quality > -1) {
//                variant.addAttribute("QUAL", String.valueOf(quality));
//            }
//            if (!filter.isEmpty()) {
//                variant.addAttribute("FILTER", filter);
//            }
//            if (!info.isEmpty()) {
//                parseInfo(variant, info);
//            }
            
            // TODO Set samples content
            
            variants.addAll(variantsFromAllele);
        }
        
        return variants;
    }

    public static String getVcfInfo(Variant variant) {
        StringBuilder info = new StringBuilder();

        for (Map.Entry<String, String> entry : variant.getAttributes().entrySet()) {
            String key = entry.getKey();
            if (!key.equalsIgnoreCase("QUAL") && !key.equalsIgnoreCase("FILTER")) {
                info.append(key);

                String value = entry.getValue();
                if (value.length() > 0) {
                    info.append("=");
                    info.append(value);
                }

                info.append(";");
            }
        }

        return (info.toString().length() == 0) ? "." : info.substring(0, info.length() - 1);
    }

    public static String getVcfSampleRawData(Variant variant, String sampleName) {
        if (!variant.getSamplesData().containsKey(sampleName)) {
            return "";
        }

        StringBuilder info = new StringBuilder();

        Map<String, String> data = variant.getSampleData(sampleName);

        for (String formatField : variant.getFormat().split(":")) {
            info.append(data.get(formatField)).append(":");
        }

        return info.toString().length() > 0 ? info.substring(0, info.length() - 1) : ".";

    }


    private static void parseSampleData(Variant variant, String[] fields, List<String> sampleNames) {
        String[] formatFields = variant.getFormat().split(":");

        for (int i = 9; i < fields.length; i++) {
            Map<String, String> map = new HashMap<>(5);

            // Fill map of a sample
            String[] sampleFields = fields[i].split(":");
            for (int j = 0; j < formatFields.length; j++) {
                map.put(formatFields[j].toUpperCase(), sampleFields[j]);
            }

            variant.addSampleData(sampleNames.get(i - 9), map);
        }
    }

    private static void parseInfo(Variant variant, String info) {
        for (String var : info.split(";")) {
            String[] splits = var.split("=");
            if (splits.length == 2) {
                variant.addAttribute(splits[0], splits[1]);
            } else {
                variant.addAttribute(splits[0], "");
            }
        }
    }

    private static List<Variant> createVariantsFromSameLengthRefAlt(String chromosome, int position, String reference, String alt) {
        int previousIndexOfDifference = 0;
        List<Variant> variants = new LinkedList<>();
        
        while (!reference.isEmpty()) {
            int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
            if (indexOfDifference < 0) {
                return variants;
            }
            
            // The 'difference' substring stores ALL remaining characters from the first difference, 
            // even if some of them are equal, so they must be checked
            int i;
            for (i = indexOfDifference; i < reference.length(); i++) {
                if (reference.charAt(i) == alt.charAt(i)) {
                    break;
                }
            }
            
            // Create variant
            int start = position + previousIndexOfDifference + indexOfDifference;
            int end = position + previousIndexOfDifference + i - 1;
            String ref = reference.substring(indexOfDifference, i);
            String inAlt = alt.substring(indexOfDifference, i);
            variants.add(new Variant(chromosome, start, end, ref, inAlt));
            
            reference = reference.substring(i);
            alt = alt.substring(i);
            previousIndexOfDifference = indexOfDifference + 1;
        }
        
        return variants;
    }

    private static Variant createVariantsFromInsertionEmptyRef(String chromosome, int position, String alt) {
        return new Variant(chromosome, position-1, position + alt.length(), "", alt);
    }

    private static Variant createVariantsFromDeletionEmptyAlt(String chromosome, int position, String reference) {
        return new Variant(chromosome, position, position + reference.length() - 1, reference, "");
    }

    private static List<Variant> createVariantsFromIndelNoEmptyRefAlt(String chromosome, int position, String reference, String alt) {
        int previousIndexOfDifference = 0;
        List<Variant> variants = new LinkedList<>();
        
        int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return variants;
        } else if (indexOfDifference == 0) {
            if (reference.length() > alt.length()) {
                variants.add(new Variant(chromosome, position, position + reference.length() - 1, reference, alt));
            } else {
                variants.add(new Variant(chromosome, position-1, position + alt.length(), reference, alt));
            }
        } else {
            int start = position + previousIndexOfDifference + indexOfDifference;
            int end = position + Math.max(reference.length(), alt.length()) - 1;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            variants.add(new Variant(chromosome, start, end, ref, inAlt));
        }
        
        return variants;
    }

    private static void setOtherFields(Variant variant, String id, float quality, String filter, String info) {
        // Fields not affected by the structure of REF and ALT fields
        variant.setId(id);
        if (quality > -1) {
            variant.addAttribute("QUAL", String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            variant.addAttribute("FILTER", filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, info);
        }
    }
}
