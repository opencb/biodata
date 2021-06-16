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

package org.opencb.biodata.formats.variant.annotation.io;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjlopez on 31/01/17.
 */
public class VepFormatWriterTest {
    @Test
    public void write() throws Exception {
        BufferedReader bufferedReader = FileUtils
                .newBufferedReader(Paths.get(getClass().getResource("/variant-test.json.gz").getPath()));
        ObjectMapper jsonObjectMapper;
        jsonObjectMapper = new ObjectMapper();
//        jsonObjectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
//        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Parse json variants to Variant objects
        String line = bufferedReader.readLine();
        List<Variant> variantList = new ArrayList<>(3);
        while (line != null) {
//            Variant variant = new Variant(jsonObjectMapper.convertValue(JSON.parse(line), VariantAvro.class));
            Variant variant = new Variant(jsonObjectMapper.readValue(line, VariantAvro.class));
            variantList.add(variant);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();

        // Write parsed variants with the VepFormatWriter
        VepFormatWriter vepFormatWriter = new VepFormatWriter("/tmp/test.vep");
        vepFormatWriter.open();
        vepFormatWriter.pre();
        vepFormatWriter.write(variantList);
        vepFormatWriter.post();
        vepFormatWriter.close();

        bufferedReader = FileUtils.newBufferedReader(Paths.get("/tmp/test.vep"));
        // Skip header
        line = bufferedReader.readLine();
        while (line.startsWith("#")) {
            line = bufferedReader.readLine();
        }

        assertEquals("rs762210018\t1:245334-245335\tTTTA\tENSG00000228463\tENST00000424587\tlincRNA\tnon_coding_transcript_variant,intron_variant\t-\t-\t-\t-\t-\t-\t-", line);
        line = bufferedReader.readLine();
        assertEquals("rs202029170\t1:247917-247919\t-\tENSG00000228463\tENST00000424587\tlincRNA\tnon_coding_transcript_variant,intron_variant\t-\t-\t-\t-\t-\t-\t-", line);
        line = bufferedReader.readLine();
        assertEquals("rs202029170\t1:247917-247919\t-\t-\t-\t-\tregulatory_region_variant\t-\t-\t-\t-\t-\t-\t-", line);
        line = bufferedReader.readLine();
        assertEquals("rs72502741\t1:251628\t-\tENSG00000228463\tENST00000424587\tlincRNA\tnon_coding_transcript_variant,intron_variant\t-\t-\t-\t-\t-\t-\t-", line);
        line = bufferedReader.readLine();
        assertEquals("rs72502741\t1:251628\t-\t-\t-\t-\tregulatory_region_variant\t-\t-\t-\t-\t-\t-\t-", line);
        bufferedReader.close();
    }

}