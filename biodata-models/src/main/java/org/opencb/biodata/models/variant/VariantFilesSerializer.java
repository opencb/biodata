package org.opencb.biodata.models.variant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

/**
 * @author Alejandro Alemán Ramos <aaleman@cipf.es>
 */
public class VariantFilesSerializer extends JsonSerializer<Map<String, ArchivedVariantFile>> {
    @Override
    public void serialize(Map<String, ArchivedVariantFile> map, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeObject(map.values());
    }
}