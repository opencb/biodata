package org.opencb.biodata.formats.variant.cosmic;

import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.models.sequence.SequenceLocation;
import org.opencb.biodata.models.variant.avro.EvidenceEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CosmicParserTest {

    // Implementation of the LineCallback function
    public class MyCallback implements CosmicParserCallback {
        private String msg;
        private int counter;

        public MyCallback(String msg) {
            this.msg = msg;
            this.counter = 0;
        }

        @Override
        public boolean processEvidenceEntries(SequenceLocation sequenceLocation, List<EvidenceEntry> evidenceEntries) {
            System.out.println(msg);
            System.out.println("Sequence location = " + sequenceLocation);
            System.out.println("Num. evidences = " + evidenceEntries.size());
            for (EvidenceEntry evidenceEntry : evidenceEntries) {
                System.out.println("evidences = " + evidenceEntry);
                counter++;
            }
            return true;
        }

        public int getCounter() {
            return counter;
        }
    }

    @Test
    public void testCosmicParser() throws IOException, FileFormatException {
        Path cosmicFile = Paths.get(getClass().getResource("/cosmic.small.tsv.gz").getPath());
        String version = "v95";
        String name = "cosmic";
        String assembly = "GRCh38";

        MyCallback callback = new MyCallback(">>> Testing message");

        CosmicParser.parse(cosmicFile, version, name, assembly, callback);
        Assert.assertEquals(90, callback.getCounter());
    }
}