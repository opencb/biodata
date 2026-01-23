package org.opencb.biodata.formats.variant.civic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.models.core.civic.CivicVariant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CivicParserTest {

    // Implementation of the LineCallback function
    public class MyCallback implements CivicParserCallback {
        private String msg;
        private List<CivicVariant> civicVariants;

        public MyCallback(String msg) {
            this.msg = msg;
            this.civicVariants = new ArrayList<>();
        }

        @Override
        public boolean processCivicVariant(CivicVariant civicVariant) {
            System.out.println(msg + ": CIViC ID = " + civicVariant.getVariantId() + ", position: " + civicVariant.getChromosome()
                    + ":" + civicVariant.getStart() + ":" + civicVariant.getReferenceBases() + ":" + civicVariant.getVariantBases());
            civicVariants.add(civicVariant);
            return true;
        }

        public List<CivicVariant> getCivicVariants() {
            return civicVariants;
        }
    }

    @Test
    public void testCivicParser() throws IOException, FileFormatException {
        Path civicPath = Paths.get("/opt/civic-data/");
        Assume.assumeTrue(Files.exists(civicPath));

        String assembly = "grch38";
        String version = "v1";
        Path variantSummariesFile = civicPath.resolve("01-Sep-2025-VariantSummaries.tsv");
        Path featureSummariesFile = civicPath.resolve("01-Sep-2025-FeatureSummaries.tsv");
        Path molecularProfileSummariesFile = civicPath.resolve("01-Sep-2025-MolecularProfileSummaries.tsv");
        Path assertionSummariesFile = civicPath.resolve("01-Sep-2025-AssertionSummaries.tsv");
        Path clinicalEvidenceSummariesFile = civicPath.resolve("01-Sep-2025-ClinicalEvidenceSummaries.tsv");

        MyCallback callback = new MyCallback(">>> Testing message");

        CivicParser parser = new CivicParser(variantSummariesFile, featureSummariesFile, molecularProfileSummariesFile,
                assertionSummariesFile, clinicalEvidenceSummariesFile, version, assembly, callback);

        parser.parse();
        List<CivicVariant> civicVariants = callback.getCivicVariants();

        // Only 2 variants are GRCh38
        Assert.assertEquals(2, civicVariants.size());
        for (CivicVariant civicVariant : civicVariants) {
            System.out.println(new ObjectMapper().writerFor(CivicVariant.class).writeValueAsString(civicVariant));
        }
    }
}