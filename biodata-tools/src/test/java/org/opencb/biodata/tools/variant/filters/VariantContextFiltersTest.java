package org.opencb.biodata.tools.variant.filters;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Created by joaquin on 11/18/16.
 */
public class VariantContextFiltersTest {

    @Test
    public void test() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/ibs.vcf").toURI());

        VariantContextFilters filters = new VariantContextFilters();
        //filters.addPassFilter();
        filters.addQualFilter(100);

        //Reader
        VCFFileReader reader = new VCFFileReader(inputPath.toFile(), false);
        Iterator<VariantContext> iterator = reader.iterator();
        VCFHeader fileHeader = reader.getFileHeader();

        int count = 0;
        while (iterator.hasNext()) {
            VariantContext variant = iterator.next();
            if (filters.test(variant)) {
                count++;
            }
        }
        reader.close();

        assertEquals(3, count);

        /*
        Path inputPath = Paths.get(getClass().getResource("/ibs.vcf.avro").toURI());
        //Path inputPath = Paths.get("/home/jtarraga/data/ibs.vcf.avro");
        VariantAvroFilters filters = new VariantAvroFilters("", "");
        filters.addPassFilter();
        SpecificDatumReader<VariantAvro> reader = new SpecificDatumReader<>(VariantAvro.class);
        int count = 0;
        try (DataFileReader<VariantAvro> in = new DataFileReader<>(inputPath.toFile(), reader)) {
            for (VariantAvro v : in) {
                Variant variant = new Variant(v);
                if (filters.test(variant)) {
                    System.out.println(">>> " + variant);
                    count++;
                }
            }
        }
        */

    }
}