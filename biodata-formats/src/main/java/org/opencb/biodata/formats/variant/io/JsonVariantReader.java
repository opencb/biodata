package org.opencb.biodata.formats.variant.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mortbay.util.ajax.JSON;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.commons.io.DataReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Created by fjlopez on 08/06/16.
 */
public class JsonVariantReader implements DataReader<Variant> {

    private BufferedReader reader;
    private Path path;
    private String filename;

    private static ObjectMapper jsonObjectMapper;

    static {
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public JsonVariantReader(String filename) { this.filename = filename; }

    @Override
    public boolean open() {

        try {
            this.path = Paths.get(this.filename);
            Files.exists(this.path);

            if (path.toFile().getName().endsWith(".gz")) {
                this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
            } else {
                this.reader = Files.newBufferedReader(path, Charset.defaultCharset());
            }

        } catch (IOException ex) {
            Logger.getLogger(JsonVariantReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean pre() { return true; }

    @Override
    public boolean close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean post() { return true; }


    @Override
    public List<Variant> read() {

        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line != null
                ? Collections.singletonList(new Variant(jsonObjectMapper.convertValue(JSON.parse(line), VariantAvro.class)))
                : null;
    }

    @Override
    public List<Variant> read(int batchSize) {
        List<Variant> batch = new ArrayList<>(batchSize);
        List<Variant> readRecords;
        int i = 0;
        while ((i < batchSize) && (readRecords = this.read()) != null) {
            batch.add(readRecords.get(0));
            i++;
        }
        return batch;
    }

}
