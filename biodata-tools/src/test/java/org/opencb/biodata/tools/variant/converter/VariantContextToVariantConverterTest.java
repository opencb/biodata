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

package org.opencb.biodata.tools.variant.converter;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.LazyGenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantNormalizer;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.commons.run.ParallelTaskRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by imedina on 27/09/15.
 */
public class VariantContextToVariantConverterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testReadVCFFile() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/CEU-1409-01_5000.vcf.gz").toURI());
        File folder = temporaryFolder.newFolder();
        Path outPath = Paths.get(folder.getPath()).resolve("CEU-1409-01_5000.vcf.gz.avro");
        writeFile(inputPath, outPath);
    }

    private long writeFile(Path inputPath, Path outPath) throws IOException {
        // Write file
        VariantContextToVariantConverter variantContextToVariantConverter = new VariantContextToVariantConverter("", "CEU-1409-01_5000.vcf.gz");
        variantContextToVariantConverter.setCodec(CodecFactory.nullCodec()); // Avoid codec issues
        return variantContextToVariantConverter.readVCFFile(inputPath, outPath);
    }

    private long readFile(Path outPath) throws IOException {
        // And read file again
        SpecificDatumReader<VariantAvro> reader = new SpecificDatumReader<VariantAvro>(VariantAvro.class);
        long cnt = 0;
        try(DataFileReader<VariantAvro> in = new DataFileReader<VariantAvro>(outPath.toFile(), reader);){
            for(VariantAvro v : in){
                Variant var = new Variant(v);
                cnt += 1;
                List<String> ids = var.getIds();
            }
        };
        return cnt;
    }

    @Test
    public void testRoundTrip() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/CEU-1409-01_5000.vcf.gz").toURI());
        File folder = temporaryFolder.newFolder();
        Path outPath = Paths.get(folder.getPath()).resolve("CEU-1409-01_5000.vcf.gz.avro");

        long cnt = writeFile(inputPath, outPath);
        long readCnt = readFile(outPath);
        Assert.assertEquals(cnt, readCnt);
    }

    @Test
    public void testReadAndNormalizeVCFFile() throws Exception {
//        File folder = temporaryFolder.newFolder();
        File folder = Paths.get("/tmp").toFile();
        Path inputPath = Paths.get(getClass().getResource("/CEU-1409-01_5000.vcf.gz").toURI());
        Path outputPath = folder.toPath().resolve("CEU-1409-01_5000.vcf.gz.avro");
        if (outputPath.toFile().exists()) {
            outputPath.toFile().delete();
        }

        //Reader
        VCFFileReader reader = new VCFFileReader(inputPath.toFile(), false);
        final Iterator<VariantContext> iterator = reader.iterator();

        //Task
        VariantContextToVariantConverter converter = new VariantContextToVariantConverter("1000g", "CEU-1409-01_5000");
        VariantNormalizer normalizer = new VariantNormalizer();

        //Writer
        FileOutputStream outputStream = new FileOutputStream(outputPath.toFile());
        DatumWriter<VariantAvro> vcfDatumWriter = new SpecificDatumWriter<>(VariantAvro.class);
        DataFileWriter<VariantAvro> writer = new DataFileWriter<>(vcfDatumWriter);
        writer.create(VariantAvro.getClassSchema(), outputStream);


        ParallelTaskRunner<VariantContext, Variant> ptr = new ParallelTaskRunner<>(
                batchSize -> {
                    List<VariantContext> batch = new ArrayList<>(batchSize);
                    for (int i = 0; i < batchSize && iterator.hasNext(); i++) {
                        VariantContext variantContext = iterator.next();
                        if (variantContext.getGenotypes().isLazyWithData()) {
                            ((LazyGenotypesContext) variantContext.getGenotypes()).decode();
                        }
                        batch.add(variantContext);
                    }
                    return batch;
                },
                batch -> {
                    return normalizer.apply(converter.apply(batch));
                },
                batch -> {
                    try {
                        for (Variant variant : batch) {
                            writer.append(variant.getImpl());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                },
                new ParallelTaskRunner.Config(4, 200, 8, true, false)
        );

        ptr.run();

        reader.close();
        writer.close();

    }
}