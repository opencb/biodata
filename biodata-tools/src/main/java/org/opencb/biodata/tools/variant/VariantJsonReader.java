package org.opencb.biodata.tools.variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mortbay.util.ajax.JSON;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.slf4j.LoggerFactory;

import java.io.*;
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
public class VariantJsonReader implements VariantReader {

    private final VariantNormalizer normalizer;
    private BufferedReader reader;
    private Path path;

    private static ObjectMapper jsonObjectMapper;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(VariantJsonReader.class);

    static {
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private int lineNumber=1;

    public VariantJsonReader(String filename) {
        this(filename, null);
    }

    public VariantJsonReader(Path input) {
        this(input, null);
    }

    public VariantJsonReader(String filename, VariantNormalizer normalizer) {
        this(Paths.get(filename), normalizer);
    }

    public VariantJsonReader(Path input, VariantNormalizer normalizer) {
        this.path = input;
        this.normalizer = normalizer;
    }

    @Override
    public boolean open() {

        try {
            Files.exists(this.path);

            if (path.toFile().getName().endsWith(".gz")) {
                this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
            } else {
                this.reader = Files.newBufferedReader(path, Charset.defaultCharset());
            }

        } catch (IOException ex) {
            Logger.getLogger(VariantJsonReader.class.getName()).log(Level.SEVERE, null, ex);
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
    public List<Variant> read(int batchSize) {
        List<Variant> variants = new ArrayList<>(batchSize);
        String line;
        int i = 0;
        while ((i < batchSize) && (line = readLine()) != null) {
            Variant variant = new Variant(jsonObjectMapper.convertValue(JSON.parse(line), VariantAvro.class));

            // Read variants may not have the variant type set and this might cause NPE
            if (variant.getType() == null) {
                variant.setType(VariantBuilder.inferType(variant.getReference(), variant.getAlternate()));
                variant.resetLength();
            }

            variants.add(variant);
            lineNumber++;
            i++;
        }

        if (normalizer != null) {
            variants = normalizer.apply(variants);
        }

        return variants;
    }

    private String readLine() {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return line;
    }

    @Override
    public List<String> getSampleNames() {
        return null;
    }

    @Override
    public VariantFileMetadata getVariantFileMetadata() {
        return null;
    }
}
