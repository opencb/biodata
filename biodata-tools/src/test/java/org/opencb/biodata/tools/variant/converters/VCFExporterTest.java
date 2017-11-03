package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.writer.Options;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.VariantVcfHtsjdkReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VCFExporterTest {

    private Path outPath;

    @Before
    public void setUp() throws Exception {
        outPath = Paths.get("target/test-data", "junit-" + RandomStringUtils.randomAlphabetic(5), "out.vcf");
        Files.createDirectories(outPath.getParent());
    }

    @Test
    public void export() throws Exception {

        VariantStudyMetadata metadata = new VariantFileMetadata("1", "test.vcf").toVariantStudyMetadata("study");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(getClass().getResourceAsStream("/test.vcf"), metadata);
        reader.open();
        reader.pre();

        VCFExporter exporter = new VCFExporter(metadata);
        Options writerOptions = Options.USE_ASYNC_IO;
        exporter.open(outPath, writerOptions);

        List<Variant> read = reader.read();
        while (read != null && !read.isEmpty()) {
            exporter.export(read);
            read = reader.read();
        }

        exporter.close();

        reader.post();
        reader.close();


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