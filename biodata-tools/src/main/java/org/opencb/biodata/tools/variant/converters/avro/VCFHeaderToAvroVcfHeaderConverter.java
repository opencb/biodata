/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineTranslator;
import htsjdk.variant.vcf.VCFHeaderVersion;
import org.opencb.biodata.models.variant.avro.VcfHeader;
import org.opencb.biodata.tools.variant.converters.Converter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

            if (line.getKey().equalsIgnoreCase("fileFormat")) {
                avroVcfHeader.setFileFormat(line.getValue());
                continue;
            }

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
