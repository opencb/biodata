package org.opencb.biodata.formats.variant.vcf4.io;

import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.commons.test.GenericTest;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantVcfEVSReaderTest extends GenericTest {

    private String inputFile = getClass().getResource("/evs.vcf.gz").getFile();
    private VariantSource source = new VariantSource(inputFile, "evs", "evs", "Exome Variant Server");

    @Test
    public void testRead() throws Exception {
        VariantReader reader = new VariantVcfEVSReader(source, inputFile);
        List<Variant> variants;

        assertTrue(reader.open());
        assertTrue(reader.pre());

        do {
            variants = reader.read();
        } while (variants != null);

        assertTrue(reader.post());
        assertTrue(reader.close());

    }
}
