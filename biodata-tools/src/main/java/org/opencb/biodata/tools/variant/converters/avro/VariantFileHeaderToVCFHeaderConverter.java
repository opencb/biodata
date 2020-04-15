package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.vcf.*;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.tools.commons.Converter;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created on 24/08/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantFileHeaderToVCFHeaderConverter implements Converter<VariantFileHeader, VCFHeader> {

    @Override
    public VCFHeader convert(VariantFileHeader variantHeader) {
        HashSet<VCFHeaderLine> meta = new HashSet<>(variantHeader.getSimpleLines().size() + variantHeader.getComplexLines().size());

        int contigIndex = 0;
        variantHeader.getSimpleLines().forEach(line -> meta.add(new VCFHeaderLine(line.getKey(), line.getValue())));
        for (VariantFileHeaderComplexLine line : variantHeader.getComplexLines()) {
            VCFHeaderLine headerLine;
            VCFHeaderLineCount count = getVCFHeaderLineCount(line);
            VCFHeaderLineType type = getVCFHeaderLineType(line);
            switch (line.getKey()) {
                case "FORMAT":
                    if (count.equals(VCFHeaderLineCount.INTEGER)) {
                        headerLine = new VCFFormatHeaderLine(line.getId(), Integer.parseInt(line.getNumber()), type, line.getDescription());
                    } else {
                        headerLine = new VCFFormatHeaderLine(line.getId(), count, type, line.getDescription());
                    }
                    break;
                case "INFO":
                    if (count.equals(VCFHeaderLineCount.INTEGER)) {
                        headerLine = new VCFInfoHeaderLine(line.getId(), Integer.parseInt(line.getNumber()), type, line.getDescription());
                    } else {
                        headerLine = new VCFInfoHeaderLine(line.getId(), count, type, line.getDescription());
                    }
                    break;
                case "FILTER":
                    headerLine = new VCFFilterHeaderLine(line.getId(), line.getDescription());
                    break;
                case "contig":
                case "ALT":
                default:
                    HashMap<String, String> map = new HashMap<>();
                    putIfNotEmpty(map, "ID", line.getId());
                    putIfNotEmpty(map, "Description", line.getDescription());
                    putIfNotEmpty(map, "Number", line.getNumber());
                    putIfNotEmpty(map, "Type", line.getType());
                    map.putAll(line.getGenericFields());
                    if (line.getKey().equals("contig")) {
                        String length = map.get("length");
                        if (StringUtils.isEmpty(length) || !StringUtils.isNumeric(length)) {
                            map.remove("length");
                        }
                        headerLine = new VCFContigHeaderLine(map, contigIndex++);
                    } else {
                        headerLine = new VCFSimpleHeaderLine(line.getKey(), map);
                    }
                    break;

            }
            meta.add(headerLine);
        }

        return new VCFHeader(meta);
    }

    public static VCFHeaderLineType getVCFHeaderLineType(VariantFileHeaderComplexLine line) {
        if (StringUtils.isEmpty(line.getType())) {
            return null;
        } else {
            return VCFHeaderLineType.valueOf(line.getType());
        }
    }

    public static VCFHeaderLineCount getVCFHeaderLineCount(VariantFileHeaderComplexLine line) {
        if (StringUtils.isEmpty(line.getNumber())) {
            return null;
        } else if (StringUtils.isNumeric(line.getNumber())) {
            return VCFHeaderLineCount.INTEGER;
        } else {
            switch (line.getNumber()) {
                case "R":
                    return VCFHeaderLineCount.R;
                case "A":
                    return VCFHeaderLineCount.A;
                case "G":
                    return VCFHeaderLineCount.G;
                default:
                    return VCFHeaderLineCount.UNBOUNDED;
            }
        }
    }

    private void putIfNotEmpty(HashMap<String, String> map, String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            map.put(key, value);
        }
    }
}
