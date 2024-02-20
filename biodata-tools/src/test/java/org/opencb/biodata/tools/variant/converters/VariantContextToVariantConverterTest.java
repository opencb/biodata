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

package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.LazyGenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderVersion;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.rules.TemporaryFolder;
import org.opencb.biodata.formats.variant.vcf4.FullVcfCodec;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.StructuralVariantType;
import org.opencb.biodata.models.variant.avro.StructuralVariation;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileMetadata;
import org.opencb.biodata.tools.variant.VariantNormalizer;
import org.opencb.biodata.tools.variant.converters.avro.VCFHeaderToVariantFileHeaderConverter;
import org.opencb.biodata.tools.variant.converters.avro.VariantContextToVariantConverter;
import org.opencb.commons.run.ParallelTaskRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by imedina on 27/09/15.
 */
public class VariantContextToVariantConverterTest {

    @TempDir
    public File temporaryFolder;

    @Test
    public void testReadVCFFile() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/CEU-1409-01_5000.vcf.gz").toURI());
        File folder = new File(temporaryFolder.getAbsolutePath());
        folder.mkdir();
       // File folder = temporaryFolder.newFolder();
        Path outPath = Paths.get(folder.getPath()).resolve("CEU-1409-01_5000.vcf.gz.avro");
        writeFile(inputPath, outPath, false);
    }

    @Test
    public void testConvertVariant() throws Exception {
        VCFCodec vcfCodec = new FullVcfCodec();
        List<String> sampleNames = Arrays.asList("HG04584", "HG03234", "HG05023");
        vcfCodec.setVCFHeader(new VCFHeader(Collections.emptySet(), sampleNames), VCFHeaderVersion.VCF4_1);
        String vcfLine = "22\t16050984\trs188945759\tC\tG\t100\tPASS\t" +
                "ERATE=0.0004;AN=2184;THETA=0.0266;VT=SNP;AA=.;LDAF=0.0028;AC=5;RSQ=0.7453;" +
                "SNPSOURCE=LOWCOV;AVGPOST=0.9984;AF=0.0023;AFR_AF=0.01;ACC=esa00000L019V9WM\t" +
                "GT:DS:GL\t0|0:0.000:-0.01,-1.84,-5.00\t1/1:0.000:-0.05,-0.95,-5.00\t0|1:0.000:-0.02,-1.45,-5.00";

        String studyId = "1";
        Consumer<Variant> checkVariant = (variant) -> {
            assertEquals("rs188945759", variant.getNames().get(0));
            assertEquals(1, variant.getNames().size());

            assertEquals("0|0", variant.getStudy(studyId).getSampleData(sampleNames.get(0), "GT"));
            assertEquals("1/1", variant.getStudy(studyId).getSampleData(sampleNames.get(1), "GT"));
            assertEquals("0|1", variant.getStudy(studyId).getSampleData(sampleNames.get(2), "GT"));

            assertEquals("0.000", variant.getStudy(studyId).getSampleData(sampleNames.get(0), "DS"));
            assertEquals("0.000", variant.getStudy(studyId).getSampleData(sampleNames.get(1), "DS"));
            assertEquals("0.000", variant.getStudy(studyId).getSampleData(sampleNames.get(2), "DS"));

            assertEquals("-0.01,-1.84,-5.00", variant.getStudy(studyId).getSampleData(sampleNames.get(0), "GL"));
            assertEquals("-0.05,-0.95,-5.00", variant.getStudy(studyId).getSampleData(sampleNames.get(1), "GL"));
            assertEquals("-0.02,-1.45,-5.00", variant.getStudy(studyId).getSampleData(sampleNames.get(2), "GL"));

            assertEquals("PASS", variant.getStudy(studyId).getFile(studyId).getData().get(StudyEntry.FILTER));
            assertEquals(new HashSet<>(Arrays.asList("GT", "DS", "GL")), new HashSet<>(variant.getStudy(studyId).getSampleDataKeys()));
        };
        VariantContext variantContext = vcfCodec.decode(vcfLine);

        VariantContextToVariantConverter converter = new VariantContextToVariantConverter(studyId, studyId, sampleNames);
        Variant variant = converter.convert(variantContext);
        assertEquals(sampleNames,
                new LinkedList<>(variant.getStudy(studyId).getSamplesPosition().keySet()));
        checkVariant.accept(variant);

        converter = new VariantContextToVariantConverter(studyId, studyId, sampleNames);
        variant = converter.convert(variantContext);
        assertEquals(sampleNames, new LinkedList<>(variant.getStudy(studyId).getSamplesPosition().keySet()));
        checkVariant.accept(variant);

        assertSame(variant.getStudy(studyId).getSamplesPosition(), converter.convert(variantContext).getStudy(studyId).getSamplesPosition());
    }

    @Test
    public void testConvertVariantFilterNull() throws Exception {
        VCFCodec vcfCodec = new FullVcfCodec();
        vcfCodec.setVCFHeader(new VCFHeader(Collections.emptySet(), Collections.emptyList()), VCFHeaderVersion.VCF4_1);
        String vcfLine = "22\t16050984\trs188945759\tC\tG\t100\t.\t.";
        VariantContext variantContext = vcfCodec.decode(vcfLine);
        String studyId = "1";

        VariantContextToVariantConverter converter = new VariantContextToVariantConverter(studyId, studyId, Collections.emptyList());
        Variant variant = converter.convert(variantContext);

        assertEquals(".", variant.getStudy(studyId).getFile(studyId).getData().get(StudyEntry.FILTER));

    }

    @Test
    public void testConvertVariantCorrectFormat() throws Exception {
        VCFCodec vcfCodec = new FullVcfCodec();
        List<String> sampleNames = Arrays.asList("HG04584", "HG03234", "HG05023");
        vcfCodec.setVCFHeader(new VCFHeader(Collections.emptySet(), sampleNames), VCFHeaderVersion.VCF4_1);
        String vcfLine = "22\t16050984\trs188945759\tC\tG\t100\t.\t.\tGT:AD\t0/0:.\t0/1:10\t1/1:20";
        VariantContext variantContext = vcfCodec.decode(vcfLine);
        String studyId = "1";

        VariantContextToVariantConverter converter = new VariantContextToVariantConverter(studyId, studyId, Collections.emptyList());
        Variant variant = converter.convert(variantContext);

        assertEquals("GT:AD", variant.getStudy(studyId).getSampleDataKeysAsString());

    }

    @Test
    public void testConvertVariantSymbolicVariant() throws Exception {
        VCFCodec vcfCodec = new FullVcfCodec();
        List<String> sampleNames = Arrays.asList("HG04584", "HG03234", "HG05023");
        vcfCodec.setVCFHeader(new VCFHeader(Collections.emptySet(), sampleNames), VCFHeaderVersion.VCF4_1);
        String vcfLine = "22\t16050984\trs188945759\tC\t<DEL>\t100\t.\tEND=16050988\tGT:AD\t./0:.\t0/1:10\t1/1:20";
        VariantContext variantContext = vcfCodec.decode(vcfLine);
        String studyId = "1";

        VariantContextToVariantConverter converter = new VariantContextToVariantConverter(studyId, studyId, sampleNames);
        Variant variant = converter.convert(variantContext);

        assertEquals(16050988, variant.getEnd().intValue());
        assertEquals("./0", variant.getStudy(studyId).getSampleData("HG04584", "GT"));
        assertEquals("0/1", variant.getStudy(studyId).getSampleData("HG03234", "GT"));
        assertEquals("1/1", variant.getStudy(studyId).getSampleData("HG05023", "GT"));

        vcfLine = "22\t16050984\trs188945759\tC\t<INS>\t100\t.\tEND=16050984;"
                + "SVINSSEQ=AGAACCTTAATACCCTAGTCTCGATGGTCTTTACATTTTGGCATGATTTTGCAGCGGCTGGTACCGG;"
                + "\tGT:AD\t./0:.\t0/1:10\t1/1:20";
        variantContext = vcfCodec.decode(vcfLine);
        variant = converter.convert(variantContext);
        assertEquals("CAGAACCTTAATACCCTAGTCTCGATGGTCTTTACATTTTGGCATGATTTTGCAGCGGCTGGTACCGG",
                variant.getAlternate());
        assertEquals(new StructuralVariation(), variant.getSv());

        vcfLine = "22\t16050984\trs188945759\tC\t<INS>\t100\t.\tEND=16050984;"
                + "LEFT_SVINSSEQ=AGAACCTTAATACCCTAGTCTCGATGGTCTTTACATTTTGGCATGATTTTGCAGCGGCTGGTACCGG;"
                + "RIGHT_SVINSSEQ=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "\tGT:AD\t./0:.\t0/1:10\t1/1:20";
        variantContext = vcfCodec.decode(vcfLine);
        variant = converter.convert(variantContext);
        assertEquals("<INS>", variant.getAlternate());
        assertEquals(new StructuralVariation(null, null, null, null, null,
                        "AGAACCTTAATACCCTAGTCTCGATGGTCTTTACATTTTGGCATGATTTTGCAGCGGCTGGTACCGG",
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, null),
                variant.getSv());

        vcfLine = "22\t16050984\trs188945759\tC\t<DUP:TANDEM>\t100\t.\tEND=16050988;CIPOS=-10,10\tGT:AD\t./0:.\t0/1:10\t1/1:20";
        variantContext = vcfCodec.decode(vcfLine);
        variant = converter.convert(variantContext);
        assertEquals(VariantType.TANDEM_DUPLICATION, variant.getType());
        assertEquals(new StructuralVariation(16050974, 16050994, null, null, null, null, null,
                        StructuralVariantType.TANDEM_DUPLICATION, null),
                variant.getSv());

    }

    private long readFile(Path outPath) throws IOException {
        // And read file again
        SpecificDatumReader<VariantAvro> reader = new SpecificDatumReader<>(VariantAvro.class);
        long cnt = 0;
        try (DataFileReader<VariantAvro> in = new DataFileReader<>(outPath.toFile(), reader);) {
            for (VariantAvro v : in) {
                Variant var = new Variant(v);
                cnt += 1;
                List<String> ids = var.getIds();
            }
        }
        return cnt;
    }

    @Test
    public void testRoundTrip() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/CEU-1409-01_5000.vcf.gz").toURI());
        File folder = new File(temporaryFolder.getAbsolutePath());
        folder.mkdir();
        Path outPath = Paths.get(folder.getPath()).resolve("CEU-1409-01_5000.vcf.gz.avro");

        long cnt = writeFile(inputPath, outPath, false);
        long readCnt = readFile(outPath);
        assertEquals(4929, readCnt);
        assertEquals(cnt, readCnt);
    }

    @Test
    public void testReadAndNormalizeVCFFile() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/CEU-1409-01_5000.vcf.gz").toURI());

        File folder = Paths.get("/tmp").toFile();
        Path outputPath = folder.toPath().resolve("CEU-1409-01_5000.vcf.gz.avro");

        long count = writeFile(inputPath, outputPath, true);
        assertEquals(4931, count);
        assertEquals(4931, readFile(outputPath));
    }

    private long writeFile(Path inputPath, Path outputPath, boolean normalize) throws Exception {
//        File folder = temporaryFolder.newFolder();
        if (outputPath.toFile().exists()) {
            outputPath.toFile().delete();
        }
        Path metaOutputPath = Paths.get(outputPath.toAbsolutePath().toString().replace(".avro", ".meta.avro"));
        if (metaOutputPath.toFile().exists()) {
            metaOutputPath.toFile().delete();
        }

        String studyId = "1";
        String fileId = "2";
        String studyName = "1000g";
        String fileName = "CEU-1409-01_5000";

        //Reader
        VCFFileReader reader = new VCFFileReader(inputPath.toFile(), false);
        final Iterator<VariantContext> iterator = reader.iterator();
        VCFHeader fileHeader = reader.getFileHeader();

        //Task
        VariantContextToVariantConverter converter = new VariantContextToVariantConverter(studyId, fileId, Collections.emptyList());
        VariantNormalizer normalizer = new VariantNormalizer();

        //Writer
        FileOutputStream outputStream = new FileOutputStream(outputPath.toFile());
        DatumWriter<VariantAvro> vcfDatumWriter = new SpecificDatumWriter<>(VariantAvro.class);
        DataFileWriter<VariantAvro> writer = new DataFileWriter<>(vcfDatumWriter);
        writer.create(VariantAvro.getClassSchema(), outputStream);

        final int[] writtenVariants = {0};

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
                normalize ? batch -> normalizer.apply(converter.apply(batch)) : batch -> converter.apply(batch),
                batch -> {
                    try {
                        for (Variant variant : batch) {
                            writtenVariants[0]++;
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

        VariantFileHeader variantFileHeader = new VCFHeaderToVariantFileHeaderConverter().convert(fileHeader);
        VariantFileMetadata fileMetadata = new VariantFileMetadata(fileId, fileName, fileHeader.getSampleNamesInOrder(),
                null, variantFileHeader, Collections.emptyMap());
        System.out.println(fileMetadata.toString());
        FileOutputStream metaOutputStream = new FileOutputStream(metaOutputPath.toFile());
        DatumWriter<VariantFileMetadata> fileMetaDatumWriter = new SpecificDatumWriter<>(VariantFileMetadata.class);
        DataFileWriter<VariantFileMetadata> fileMetaWriter = new DataFileWriter<>(fileMetaDatumWriter);
        fileMetaWriter.create(org.opencb.biodata.models.variant.metadata.VariantFileMetadata.getClassSchema(), metaOutputStream);
        fileMetaWriter.append(fileMetadata);
        fileMetaWriter.close();


        return writtenVariants[0];
    }
}