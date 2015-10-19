/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.annotation.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.commons.io.DataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by fjlopez on 01/04/15.
 */
public class JsonAnnotationWriter implements DataWriter<VariantAnnotation> {

    private String filename;
    private BufferedWriter bw;
    private int writtenVariantAnnotations = 0;
    private Logger logger;

    private ObjectWriter jsonObjectWriter;

    public JsonAnnotationWriter() {
        this(null);
    }

    public JsonAnnotationWriter(String filename) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.filename = filename;
    }

    @Override
    public boolean open() {
        try {
            bw = Files.newBufferedWriter(Paths.get(filename), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean pre() {
        ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonObjectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        jsonObjectWriter = jsonObjectMapper.writer();
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(VariantAnnotation variantAnnotation) {
        try {
            bw.write(jsonObjectWriter.writeValueAsString(variantAnnotation)+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean write(List<VariantAnnotation> list) {

        if (list != null) {
            for(VariantAnnotation variantAnnotation : list) {
                write(variantAnnotation);
            }

            writtenVariantAnnotations +=list.size();
            if ((writtenVariantAnnotations % 2000) == 0) {
                logger.info("{} written annotations.", writtenVariantAnnotations);
            }

            return true;
        } else {
            logger.warn("JsonAnnotationWriter: list is null");
        }
        return false;
    }
}
