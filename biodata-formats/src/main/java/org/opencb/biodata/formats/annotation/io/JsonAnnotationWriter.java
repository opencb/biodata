package org.opencb.biodata.formats.annotation.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.opencb.biodata.models.variant.annotation.ConsequenceType;
import org.opencb.biodata.models.variant.annotation.VariantAnnotation;
import org.opencb.commons.io.DataWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by fjlopez on 01/04/15.
 */
public class JsonAnnotationWriter implements DataWriter<VariantAnnotation> {

    String filename;
    BufferedWriter bw;
    private ObjectWriter jsonObjectWriter;

    public JsonAnnotationWriter() {}

    public JsonAnnotationWriter(String filename) {
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

        for(VariantAnnotation variantAnnotation : list) {
            write(variantAnnotation);
        }
        return true;
    }
}
