package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.writer.Options;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.junit.Test;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.tools.variant.metadata.VariantMetadataManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class VCFExporterTest {

    @Test
    public void export() throws Exception {

        Path inputPath = Paths.get(getClass().getResource("/test.vcf.avro").toURI());
        Path metadataPath = Paths.get(getClass().getResource("/test.vcf.avro.meta.json").toURI());
        Path outPath = Paths.get("/tmp/out.vcf");

        DatumReader<VariantAvro> datumReader = new SpecificDatumReader<>(VariantAvro.class);
        DataFileReader<VariantAvro> dataFileReader = new DataFileReader<>(inputPath.toFile(), datumReader);

        //VariantAvro variantAvro = null;
        //while (dataFileReader.hasNext()) {
        //    variantAvro = dataFileReader.next(variantAvro);
        //    System.out.println(variantAvro.toString());
        //}

        VariantMetadataManager manager = new VariantMetadataManager();
        manager.load(metadataPath);

        VCFExporter exporter = new VCFExporter(manager.getVariantMetadata().getStudies().get(0));

        Options writerOptions = Options.USE_ASYNC_IO;
        exporter.export(dataFileReader, writerOptions, outPath);

        dataFileReader.close();
    }

}

/*


    public AvroReader(InputStream inputStream, Schema schema) {
        this.inputStream = inputStream;
        datumReader = new SpecificDatumReader<>(schema);
    }

    @Override
    public boolean open() {
        try {
            dataFileStream = new DataFileStream<>(inputStream, datumReader);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean close() {
        try {
            dataFileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean pre() {
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public List<T> read() {
        if (dataFileStream.hasNext()) {
            T item = null;
            try {
                item = dataFileStream.next(item);
                return Collections.singletonList(item);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public List<T> read(int batchSize) {
        int counter = 0;
        T item = null;
        List<T> list = new ArrayList<T>();
        while (dataFileStream.hasNext()) {
            try {
                item = dataFileStream.next(item);
                list.add(item);

                if ((++counter) >= batchSize) {
                    return list;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
 */