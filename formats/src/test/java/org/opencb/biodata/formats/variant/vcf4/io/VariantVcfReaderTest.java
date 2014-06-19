package org.opencb.biodata.formats.variant.vcf4.io;

import org.junit.Test;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantVcfFactory;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.opencb.biodata.models.variant.VariantVcfEVSFactory;

public class VariantVcfReaderTest {

    @Test
    public void readFile() {
        String inputFile = getClass().getResource("/variant-test-file.vcf.gz").getFile();
        VariantSource source = new VariantSource(inputFile, "test", "test", "Test file");
    
        VariantReader reader = new VariantVcfReader(source, inputFile);

        List<Variant> variants;

        assertTrue(reader.open());
        assertTrue(reader.pre());

        int i = 0;
        do {
            variants = reader.read();
            if(variants != null){
                i+=variants.size();
            }
        } while (variants != null);

        assertEquals(i, 999);

        assertTrue(reader.post());
        assertTrue(reader.close());
    }

    @Test
    public void readAggregatedFile() {
        String inputFile = getClass().getResource("/evs.vcf.gz").getFile();
        VariantSource source = new VariantSource(inputFile, "evs", "evs", "Exome Variant Server");
        
        VariantReader reader = new VariantVcfReader(source, inputFile, new VariantVcfEVSFactory());

        List<Variant> variants;

        assertTrue(reader.open());
        assertTrue(reader.pre());

        int i = 0;
        do {
            variants = reader.read();
            if(variants != null){
                i+=variants.size();
            }
        } while (variants != null);

        assertEquals(i, 1000);

        assertTrue(reader.post());
        assertTrue(reader.close());
    }
}
