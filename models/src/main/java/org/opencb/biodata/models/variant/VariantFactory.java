package org.opencb.biodata.models.variant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantFactory {

    public static Variant createVariantFromVcf(List<String> sampleNames, String... fields) {
        if (fields.length < 8) {
            throw new IllegalArgumentException("Not enough fields provided (min 8)");
        }

        // TODO End must be properly calculated!
        Variant variant = new Variant(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[1]),fields[3], fields[4]);
        variant.setId(fields[2]);
        variant.addAttribute("QUAL", fields[5]);
        variant.addAttribute("FILTER", fields[6]);
        parseInfo(variant, fields[7]);

        if (fields.length > 8) {
            variant.setFormat(fields[8]);
            parseSampleData(variant, fields, sampleNames);
        }

        return variant;
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

        if (info.equalsIgnoreCase(".")) {
            return;
        }

        for (String var : info.split(";")) {
            String[] splits = var.split("=");
            if (splits.length == 2) {
                variant.addAttribute(splits[0], splits[1]);
            } else {
                variant.addAttribute(splits[0], "");
            }

        }

    }


}
