package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.vcf.*;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderSimpleLine;
import org.opencb.biodata.tools.commons.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created on 09/08/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VCFHeaderToVariantFileHeaderConverter  implements Converter<VCFHeader, VariantFileHeader> {

    @Override
    public VariantFileHeader convert(VCFHeader header) {

        List<VariantFileHeaderComplexLine> complexLines = new ArrayList<>();
        List<VariantFileHeaderSimpleLine> simpleLines = new ArrayList<>();

        VariantFileHeader avroHeader = new VariantFileHeader();
        for (VCFHeaderLine line : header.getMetaDataInInputOrder()) {
            if (line.getKey().equalsIgnoreCase("fileFormat")) {
                avroHeader.setVersion(line.getValue());
                continue;
            }

            if (line.getValue().isEmpty()) {
                if ( line instanceof VCFCompoundHeaderLine)  {
                    complexLines.add(convertComplexLine((VCFCompoundHeaderLine) line));
                } else if ( line instanceof VCFSimpleHeaderLine ) {
                    complexLines.add(convertSimpleLine(line));
                }
            } else {
                simpleLines.add(new VariantFileHeaderSimpleLine(line.getKey(), line.getValue()));
            }

        }
        avroHeader.setComplexLines(complexLines);
        avroHeader.setSimpleLines(simpleLines);

        return avroHeader;
    }

    public static VariantFileHeaderComplexLine convertComplexLine(VCFCompoundHeaderLine vcfLine) {
        String number;
        if (vcfLine.isFixedCount()) {
            number = String.valueOf(vcfLine.getCount());
        } else if (vcfLine.getCountType().equals(VCFHeaderLineCount.UNBOUNDED)) {
            number = ".";
        } else {
            number = vcfLine.getCountType().toString();
        }
        return VariantFileHeaderComplexLine.newBuilder()
                .setKey(vcfLine.getKey())
                .setId(vcfLine.getID())
                .setDescription(vcfLine.getDescription())
                .setType(vcfLine.getType().toString())
                .setNumber(number).build();
    }

    public static VariantFileHeaderComplexLine convertSimpleLine(VCFHeaderLine line) {
        Map<String, String> map = VCFHeaderLineTranslator.parseLine(VCFHeaderVersion.VCF4_2, line.toString(), null);
        VariantFileHeaderComplexLine.Builder builder = VariantFileHeaderComplexLine.newBuilder();
        setValue(map, "ID", builder::setId);
        setValue(map, "Description", builder::setDescription);
        setValue(map, "Number", builder::setNumber);
        setValue(map, "Type", builder::setType);
        builder.setKey(line.getKey());
        builder.setGenericFields(map);
        return builder.build();
    }

    private static void setValue(Map<String, String> map, String key, Function<String, VariantFileHeaderComplexLine.Builder> method) {
        if (map.containsKey(key)) {
            method.apply(map.remove(key));
        }
    }

    private static void putNotNull(Map<String, String> map, String key, Object value) {
        if (value != null) {
            map.put(key, value.toString());
        }
    }
}
