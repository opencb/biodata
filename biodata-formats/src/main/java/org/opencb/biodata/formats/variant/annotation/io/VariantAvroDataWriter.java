package org.opencb.biodata.formats.variant.annotation.io;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.commons.ProgressLogger;
import org.opencb.commons.io.DataWriter;
import org.opencb.commons.io.avro.AvroDataWriter;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fjlopez on 07/04/17.
 */
public class VariantAvroDataWriter implements DataWriter<Variant> {

    private final AvroDataWriter<VariantAvro> writer;

    public VariantAvroDataWriter(Path outputPath, boolean gzip) {
        this.writer = new AvroDataWriter<>(outputPath, gzip, VariantAvro.getClassSchema());
    }

    @Override
    public boolean open() {
        return writer.open();
    }

    @Override
    public boolean close() {
        return writer.close();
    }

    @Override
    public boolean pre() {
        return writer.pre();
    }

    @Override
    public boolean post() {
        return writer.post();
    }

    @Override
    public boolean write(List<Variant> batch) {
        List<VariantAvro> variantAvros = batch
                .stream()
                .map(Variant::getImpl)
                .collect(Collectors.toList());
        return writer.write(variantAvros);
    }

    public VariantAvroDataWriter setProgressLogger(ProgressLogger progressLogger) {
        writer.setProgressLogger(progressLogger);
        return this;
    }

}
