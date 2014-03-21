package org.opencb.biodata.models.variant;

import java.util.Arrays;
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

    private VariantFactory() {}
    
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
        String format = (fields.length <= 8 || fields[8].equals(".")) ? "" : fields[8];
        
        for (int i = 0; i < alternateAlleles.length; i++) { // This index is necessary for getting the samples where the mutated allele is present
            String alt = alternateAlleles[i];
            List<VariantKeyFields> keyFields;
            int referenceLen = reference.length();
            int alternateLen = alt.length();
            
            if (referenceLen == alternateLen) {
//                keyFields = createVariantsFromSameLengthRefAlt(position, reference, alt);
                keyFields = createVariantsFromIndelNoEmptyRefAlt(position, reference, alt);
            } else if (referenceLen == 0) {
                keyFields = createVariantsFromInsertionEmptyRef(position, alt);
            } else if (alternateLen == 0) {
                keyFields = createVariantsFromDeletionEmptyAlt(position, reference);
            } else {
                keyFields = createVariantsFromIndelNoEmptyRefAlt(position, reference, alt);
            }
            
            if (!keyFields.isEmpty()) {
                if (keyFields.size() == 1) {
                    VariantKeyFields vkf = keyFields.get(0);
                    Variant variant = new Variant(chromosome, vkf.start, vkf.end, vkf.reference, vkf.alternate);
                    setOtherFields(variant, id, quality, filter, info, format);
                    // TODO Copy only the samples that correspond to each specific mutation
//                    parseSampleData(variant, fields, sampleNames);
                    variants.add(variant);
                } else {
                    System.out.println("Multiple KeyFields");
                    // TODO More complex calculations should be performed
                    for (VariantKeyFields vkf : keyFields) {
                        Variant variant = new Variant(chromosome, vkf.start, vkf.end, vkf.reference, vkf.alternate);
                        setOtherFields(variant, id, quality, filter, info, format);
                        // TODO Copy only the samples that correspond to each specific mutation
//                        parseSplitSampleData(variant, fields, sampleNames, alternateAlleles, i);
                        variants.add(variant);
                    }
                } 
            }
            
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
    
    private static void parseSplitSampleData(Variant variant, String[] fields, List<String> sampleNames, 
            String[] alternateAlleles, int alleleIdx) {
        String[] formatFields = variant.getFormat().split(":");

        for (int i = 9; i < fields.length; i++) {
            Map<String, String> map = new HashMap<>(5);

            // Fill map of a sample
            String[] sampleFields = fields[i].split(":");
            for (int j = 0; j < formatFields.length; j++) {
                String formatField = formatFields[j];
                String sampleField = sampleFields[j];
                if (formatField.equalsIgnoreCase("GT")) {
                    // If current allele index is not found in the genotype,
                    // the sample is not added to the list
                    if (!sampleField.contains(String.valueOf(alleleIdx))) {
                        break;
                    }
                    
                    // Replace numerical indexes with the bases
                    // TODO Could this be done with Java 8 streams? :)
                    for (int k = 0; k < alternateAlleles.length; k++) {
                        sampleField = sampleField.replace(String.valueOf(k), alternateAlleles[k]);
                    }
                } else if (formatField.equalsIgnoreCase("GL")) {
                    // TODO Genotype likelihood must be distributed following similar criteria as genotypes
                }
                
                map.put(formatField.toUpperCase(), sampleField);
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

//    private static List<VariantKeyFields> createVariantsFromSameLengthRefAlt(int position, String reference, String alt) {
//        int previousIndexOfDifference = 0;
//        List<VariantKeyFields> variants = new LinkedList<>();
//        
//        while (!reference.isEmpty()) {
//            int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
//            if (indexOfDifference < 0) {
//                return variants;
//            }
//            
//            // The 'difference' substring stores ALL remaining characters from the first difference, 
//            // even if some of them are equal, so they must be checked
//            int i;
//            for (i = indexOfDifference; i < reference.length(); i++) {
//                if (reference.charAt(i) == alt.charAt(i)) {
//                    break;
//                }
//            }
//            
//            // Create variant
//            int start = position + previousIndexOfDifference + indexOfDifference;
//            int end = position + previousIndexOfDifference + i - 1;
//            String ref = reference.substring(indexOfDifference, i);
//            String inAlt = alt.substring(indexOfDifference, i);
//            variants.add(new VariantKeyFields(start, end, ref, inAlt));
//            
//            reference = reference.substring(i);
//            alt = alt.substring(i);
//            previousIndexOfDifference = indexOfDifference + 1;
//        }
//        
//        return variants;
//    }

    private static List<VariantKeyFields> createVariantsFromInsertionEmptyRef(int position, String alt) {
        return Arrays.asList(new VariantKeyFields(position-1, position + alt.length(), "", alt));
    }

    private static List<VariantKeyFields> createVariantsFromDeletionEmptyAlt(int position, String reference) {
        return Arrays.asList(new VariantKeyFields(position, position + reference.length() - 1, reference, ""));
    }

    private static List<VariantKeyFields> createVariantsFromIndelNoEmptyRefAlt(int position, String reference, String alt) {
        int previousIndexOfDifference = 0;
        List<VariantKeyFields> variants = new LinkedList<>();
        
        int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return variants;
        } else if (indexOfDifference == 0) {
            if (reference.length() > alt.length()) {
                variants.add(new VariantKeyFields(position, position + reference.length() - 1, reference, alt));
            } else {
                variants.add(new VariantKeyFields(position-1, position + alt.length(), reference, alt));
            }
        } else {
            int start = position + previousIndexOfDifference + indexOfDifference;
            int end = position + Math.max(reference.length(), alt.length()) - 1;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            variants.add(new VariantKeyFields(start, end, ref, inAlt));
        }
        
        return variants;
    }

    private static void setOtherFields(Variant variant, String id, float quality, String filter, String info, String format) {
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
        variant.setFormat(format);
    }
    
    private static class VariantKeyFields {
        int start, end;
        String reference, alternate;

        public VariantKeyFields(int start, int end, String reference, String alternate) {
            this.start = start;
            this.end = end;
            this.reference = reference;
            this.alternate = alternate;
        }
    }
    
}
