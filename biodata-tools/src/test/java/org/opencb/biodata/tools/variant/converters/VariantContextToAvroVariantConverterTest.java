package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.junit.Test;
import org.opencb.biodata.formats.variant.vcf4.VcfUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jtarraga on 07/03/17.
 */
public class VariantContextToAvroVariantConverterTest {

//    @Test
    public void exportVcf() {
/*
        Path avroPath = null;
        try {
            VariantContextToVariantConverterTest auxTest = new VariantContextToVariantConverterTest();
            Path inputPath = Paths.get(getClass().getResource("/CEU-1409-01_5000.vcf.gz").toURI());
            File folder = new File("/tmp/");
            avroPath = Paths.get(folder.getPath()).resolve("CEU-1409-01_5000.vcf.gz.avro");
            auxTest.writeFile(inputPath, avroPath, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("avroPath = " + avroPath);
*/
        OutputStream outputStream = System.out;

        List<String> samples = Arrays.asList("NA07037");
        List<String> cohorts = Arrays.asList("ALL");
        List<String> annotations = Arrays.asList("gene");
        List<String> formats = Arrays.asList("GT");
        List<String> formatTypes = Arrays.asList("String");
        List<String> formatDescriptions = Arrays.asList("Desc");


        // create VCF header and writer
        VCFHeader vcfHeader = VariantConverterUtils.createVCFHeader(cohorts, annotations, formats, formatTypes, formatDescriptions, samples, null);
        VariantContextWriter writer = VariantConverterUtils.createVariantContextWriter(outputStream, vcfHeader.getSequenceDictionary(),
                null);

        // write VCF header
        writer.writeHeader(vcfHeader);

        // main loop
        VariantContextToAvroVariantConverter converter = new VariantContextToAvroVariantConverter("1000g", samples, annotations);
        SpecificDatumReader<VariantAvro> reader = new SpecificDatumReader<>(VariantAvro.class);
        long cnt = 0;
        try {
//            File file = new File("/media/data100/jtarraga/data/spark/chr22.variants.annot.avro");
            File file = new File("/media/data100/jtarraga/data/spark/test.vcf.annot.avro");
            DataFileReader<VariantAvro> in = new DataFileReader<>(file, reader);
            for (VariantAvro v : in) {
                Variant variant = new Variant(v);
                System.out.println(variant.toJson());
                VariantContext variantContext = converter.from(variant);
                writer.add(variantContext);
                cnt += 1;
                break;
            }
            System.out.println("counter = " + cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        writer.close();
    }
}