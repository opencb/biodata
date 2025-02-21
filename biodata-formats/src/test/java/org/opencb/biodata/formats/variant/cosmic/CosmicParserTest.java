package org.opencb.biodata.formats.variant.cosmic;

import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.models.sequence.SequenceLocation;
import org.opencb.biodata.models.variant.avro.EvidenceEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmicParserTest {

    // Implementation of the LineCallback function
    public class MyCallback implements CosmicParserCallback {
        private String msg;
        private Map<String, List<EvidenceEntry>> results;

        public MyCallback(String msg) {
            this.msg = msg;
            this.results = new HashMap<>();
        }

        @Override
        public boolean processEvidenceEntries(SequenceLocation sequenceLocation, List<EvidenceEntry> evidenceEntries) {
            System.out.println(msg);
            System.out.println("Sequence location = " + sequenceLocation);
            System.out.println("Num. evidences = " + evidenceEntries.size());
            for (EvidenceEntry evidenceEntry : evidenceEntries) {
                System.out.println("evidences = " + evidenceEntry);
            }
            String seqLoc = SeqLocationtoString(sequenceLocation);
            if (results.containsKey(seqLoc)) {
                System.out.println(">>>> " + seqLoc);
                results.get(seqLoc).addAll(evidenceEntries);
            } else {
                results.put(seqLoc, evidenceEntries);
            }

            return true;
        }

        public Map<String, List<EvidenceEntry>> getResults() {
            return results;
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
        Map<String, List<EvidenceEntry>> results = callback.getResults();

        for (Map.Entry<String, List<EvidenceEntry>> entry : results.entrySet()) {
            System.out.println(entry.getKey() + " --> size = " + entry.getValue().size());
            for (EvidenceEntry evidenceEntry : entry.getValue()) {
                System.out.println("\t\tid = " + evidenceEntry.getId());
            }
        }

        Assert.assertEquals(89, results.size());
    }

    @Test
    public void testCosmicParserV101() throws IOException, FileFormatException {
        Path genomeScreensMutantFile = Paths.get(getClass().getResource("/Small_Cosmic_GenomeScreensMutant_v101_GRCh38.tsv.gz").getPath());
        Path classificationFile = Paths.get(getClass().getResource("/Small_Cosmic_Classification_v101_GRCh38.tsv.gz").getPath());
        String version = "v101";
        String name = "cosmic";
        String assembly = "GRCh38";

        MyCallback callback = new MyCallback(">>> Testing message");

        CosmicParser101.parse(genomeScreensMutantFile, classificationFile, version, name, assembly, callback);
        Map<String, List<EvidenceEntry>> results = callback.getResults();

        Assert.assertEquals(6, results.size());

        for (Map.Entry<String, List<EvidenceEntry>> entry : results.entrySet()) {
            System.out.println(entry.getKey() + " --> size = " + entry.getValue().size());
            for (EvidenceEntry evidenceEntry : entry.getValue()) {
                System.out.println("\t\tid = " + evidenceEntry.getId());
            }
        }

        SequenceLocation sequenceLocation = new SequenceLocation("20", 17605163, 17605163, "A", "G", "+");
        System.out.println("sequenceLocation.toString() = " + sequenceLocation);
        Assert.assertTrue(results.containsKey(SeqLocationtoString(sequenceLocation)));
        List<EvidenceEntry> evidenceEntries = results.get(SeqLocationtoString(sequenceLocation));
        Assert.assertEquals(1, evidenceEntries.size());
        EvidenceEntry entry = evidenceEntries.get(0);
        Assert.assertEquals("COSV55713044", entry.getId());
        Assert.assertEquals("ovary", entry.getSomaticInformation().getPrimarySite());
        Assert.assertTrue(entry.getAdditionalProperties().stream().anyMatch(p -> p.getId().equals("HGVSG") && p.getValue().equals("20:g.17605163A>G")));
    }

    private String SeqLocationtoString(SequenceLocation location) {
        return location.getChromosome() + ":" + location.getStart() + "-" + location.getEnd() + ":" + location.getReference() + ":"
                + location.getAlternate();
    }
}