package org.opencb.biodata.tools.variant.converter;

import htsjdk.variant.vcf.*;
import org.opencb.biodata.formats.variant.vcf4.*;
import org.opencb.biodata.models.variant.avro.VcfHeader;

import java.util.*;

/**
 * Created on 14/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VCFHeaderToAvroVcfHeaderConverter implements Converter<VCFHeader, VcfHeader> {



    @Override
    public VcfHeader convert(VCFHeader header) {

        VcfHeader avroVcfHeader = new VcfHeader();

        LinkedHashMap<String, List<Object>> meta = new LinkedHashMap<>();
        avroVcfHeader.setMeta(meta);

        for (VCFHeaderLine line : header.getMetaDataInInputOrder()) {
//            Map<String, String> map = new HashMap<>();
//            Object value = map;
            if (line.getKey().equalsIgnoreCase("fileFormat")) {
                avroVcfHeader.setFileFormat(line.getValue());
                continue;
            }
//            if (line instanceof VCFInfoHeaderLine) {
//                VCFInfoHeaderLine infoHeader = (VCFInfoHeaderLine) line;
//                map.put("id", infoHeader.getID());
//                map.put("description", infoHeader.getDescription());
//                if (infoHeader.isFixedCount()) map.put("number", Integer.toString(infoHeader.getCount()));
//                map.put("type", infoHeader.getType().toString());
//            } else if (line instanceof VCFFilterHeaderLine) {
//                VCFFilterHeaderLine filterHeader = (VCFFilterHeaderLine) line;
//                map.put("id", filterHeader.getID());
//                map.put("description", filterHeader.getValue());
//            } else if (line instanceof VCFFormatHeaderLine) {
//                VCFFormatHeaderLine formatHeader = (VCFFormatHeaderLine) line;
//                map.put("id", formatHeader.getID());
//                map.put("description", formatHeader.getDescription());
//                if (formatHeader.isFixedCount()) map.put("number", Integer.toString(formatHeader.getCount()));
//                map.put("type", formatHeader.getType().toString());
//            } else if (line instanceof VCFContigHeaderLine) {
//                VCFContigHeaderLine contigHeader = (VCFContigHeaderLine) line;
//                map.put("id", contigHeader.getID());
//                putNotNull(map, "index", Integer.toString(contigHeader.getContigIndex()));
//                putNotNull(map, "assembly", contigHeader.getSAMSequenceRecord().getAssembly());
//                putNotNull(map, "length", contigHeader.getSAMSequenceRecord().getSequenceLength());
//                putNotNull(map, "species", contigHeader.getSAMSequenceRecord().getSpecies());
//                putNotNull(map, "md5", contigHeader.getSAMSequenceRecord().getMd5());
//            } else if (line instanceof VCFSimpleHeaderLine) {
//                VCFSimpleHeaderLine simpleHeaderLine = (VCFSimpleHeaderLine) line;
//                putNotNull(map, "id", simpleHeaderLine.getID());
//                putNotNull(map, "value", simpleHeaderLine.getValue());
//            } else {
//                value = line.getValue();
//            }

            Object value;
            if (line.getValue().isEmpty()) {
                value = VCFHeaderLineTranslator.parseLine(VCFHeaderVersion.VCF4_2, line.toString(), null);
            } else {
                value = line.getValue();
            }
            if (!meta.containsKey(line.getKey())) {
                meta.put(line.getKey(), new LinkedList<>());
            }
            meta.get(line.getKey()).add(value);
        }
        return avroVcfHeader;
    }

    private void putNotNull(Map<String, String> map, String key, Object value) {
        if (value != null) {
            map.put(key, value.toString());
        }
    }
}
