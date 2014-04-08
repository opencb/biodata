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
     * @param fileName The name of the file the variant was read from
     * @param fileId The unique identifier of the file the variant was read from
     * @param studyId The study to which the file belongs
     * @param sampleNames Names of the samples in the file
     * @param fields Contents of the line in the file
     * @return The list of Variant objects that can be created using the fields from a VCF record
     */
    public static List<Variant> createVariantFromVcf(String fileName, String fileId, String studyId, 
            List<String> sampleNames, String... fields) {
        if (fields.length < 8) {
            throw new IllegalArgumentException("Not enough fields provided (min 8)");
        }
        
        List<Variant> variants = new LinkedList<>();

        String chromosome = fields[0];
        int position = Integer.parseInt(fields[1]);
        String id = fields[2].equals(".") ? "" : fields[2];
        String reference = fields[3].equals(".") ? "" : fields[3];
        String alternate = fields[4].equals(".") ? "" : fields[4];
        String[] alternateAlleles = alternate.split(",");
        float quality = fields[5].equals(".") ? -1 : Float.parseFloat(fields[5]);
        String filter = fields[6].equals(".") ? "" : fields[6];
        String info = fields[7].equals(".") ? "" : fields[7];
        String format = (fields.length <= 8 || fields[8].equals(".")) ? "" : fields[8];
        
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
            
            if (keyFields != null) {
                Variant variant = new Variant(chromosome, keyFields.start, keyFields.end, keyFields.reference, keyFields.alternate);
                variant.addFile(new ArchivedVariantFile(fileName, fileId, studyId));
                setOtherFields(variant, fileId, id, quality, filter, info, format);
                // TODO Copy only the samples that correspond to each specific mutation
                parseSplitSampleData(variant, fileId, fields, sampleNames, alternateAlleles, i+1);
                variants.add(variant);
            }
        }
        
        return variants;
    }

    public static String getVcfInfo(Variant variant, String fileId) {
        StringBuilder info = new StringBuilder();

        for (Map.Entry<String, String> entry : variant.getFile(fileId).getAttributes().entrySet()) {
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

    public static String getVcfSampleRawData(Variant variant, String fileId, String sampleName) {
        if (!variant.getFile(fileId).getSamplesData().containsKey(sampleName)) {
            return "";
        }

        StringBuilder info = new StringBuilder();
        Map<String, String> data = variant.getFile(fileId).getSampleData(sampleName);

        for (String formatField : variant.getFile(fileId).getFormat().split(":")) {
            info.append(data.get(formatField)).append(":");
        }

        return info.toString().length() > 0 ? info.substring(0, info.length() - 1) : ".";
    }

    private static void parseSplitSampleData(Variant variant, String fileId, String[] fields, List<String> sampleNames, 
            String[] alternateAlleles, int alleleIdx) {
        String[] formatFields = variant.getFile(fileId).getFormat().split(":");

        for (int i = 9; i < fields.length; i++) {
            Map<String, String> map = new HashMap<>(5);

            // Fill map of a sample
            boolean shouldAddSample = true;
            String[] sampleFields = fields[i].split(":");
            
            for (int j = 0; j < formatFields.length; j++) {
                String formatField = formatFields[j];
                String sampleField = sampleFields[j];
                if (formatField.equalsIgnoreCase("GT")) {
                    shouldAddSample = shouldAddSampleToVariant(sampleField, alleleIdx);
                    
                    if (shouldAddSample) {
                        // Replace numerical indexes with the bases
                        // TODO Could this be done with Java 8 streams? :)
                        sampleField = sampleField.replace("0", variant.getReference());
                        for (int k = 0; k < alternateAlleles.length; k++) {
                            sampleField = sampleField.replace(String.valueOf(k+1), alternateAlleles[k]);
                        }
                    } else {
                        break;
                    }
                } else if (formatField.equalsIgnoreCase("GL")) {
                    // TODO Genotype likelihood must be distributed following similar criteria as genotypes
                }
                
                map.put(formatField.toUpperCase(), sampleField);
            }

            // If the genotype of the sample did not match the alleles of this variant, do not add it to the list
            if (shouldAddSample) {
                variant.getFile(fileId).addSampleData(sampleNames.get(i - 9), map);
            }
        }
    }

    /**
     * Checks whether a sample should be included in a variant's list of samples.
     * If current allele index is not found in the genotype and not all alleles 
     * are references/missing, then the sample must not be included.
     * 
     * @param genotype The genotype
     * @param alleleIdx The index of the allele
     * @return If the sample should be associated to the variant
     */
    private static boolean shouldAddSampleToVariant(String genotype, int alleleIdx) {
        if (!genotype.contains(String.valueOf(alleleIdx))) {
            if(!genotype.contains("0")) {
                return false;
            } else { 
                String[] alleles = genotype.split("[/|]");
                for (String allele : alleles) {
                    if (!allele.equals("0") && !allele.equals(".")) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private static void parseInfo(Variant variant, String fileId, String info) {
        for (String var : info.split(";")) {
            String[] splits = var.split("=");
            if (splits.length == 2) {
                variant.getFile(fileId).addAttribute(splits[0], splits[1]);
            } else {
                variant.getFile(fileId).addAttribute(splits[0], "");
            }
        }
    }

    private static VariantKeyFields createVariantsFromSameLengthRefAlt(int position, String reference, String alt) {
        int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return null;
        } else if (indexOfDifference == 0) {
            return new VariantKeyFields(position, position + alt.length(), reference, alt);
        } else {
            int start = position + indexOfDifference;
            int end = position + Math.max(reference.length(), alt.length()) - 1;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            return new VariantKeyFields(start, end, ref, inAlt);
        }
    }

    private static VariantKeyFields createVariantsFromInsertionEmptyRef(int position, String alt) {
        return new VariantKeyFields(position-1, position + alt.length(), "", alt);
    }

    private static VariantKeyFields createVariantsFromDeletionEmptyAlt(int position, String reference) {
        return new VariantKeyFields(position, position + reference.length() - 1, reference, "");
    }

    private static VariantKeyFields createVariantsFromIndelNoEmptyRefAlt(int position, String reference, String alt) {
        int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return null;
        } else if (indexOfDifference == 0) {
            if (reference.length() > alt.length()) {
                return new VariantKeyFields(position, position + reference.length() - 1, reference, alt);
            } else {
                return new VariantKeyFields(position-1, position + alt.length(), reference, alt);
            }
        } else {
            int start = position + indexOfDifference;
            int end = position + Math.max(reference.length(), alt.length()) - 1;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            return new VariantKeyFields(start, end, ref, inAlt);
        }
    }

    private static void setOtherFields(Variant variant, String fileId, String id, float quality, String filter, String info, String format) {
        // Fields not affected by the structure of REF and ALT fields
        variant.setId(id);
        if (quality > -1) {
            variant.getFile(fileId).addAttribute("QUAL", String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            variant.getFile(fileId).addAttribute("FILTER", filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, fileId, info);
        }
        variant.getFile(fileId).setFormat(format);
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
