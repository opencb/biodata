package org.opencb.biodata.formats.feature.mirbase;

import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.models.core.MiRnaGene;
import org.opencb.biodata.models.core.MiRnaMature;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MirBaseParserTest {



    // Implementation of the MirBaseParserCallback function
    public class MyCallback implements MirBaseParserCallback {
        private String msg;
        private List<MiRnaGene> miRnaGenes;

        public MyCallback(String msg) {
            this.msg = msg;
            this.miRnaGenes = new ArrayList<>();
        }

        @Override
        public boolean processMiRnaGene(MiRnaGene miRnaGene) {
            System.out.println(msg);
            System.out.println(miRnaGene.toString());
            miRnaGenes.add(miRnaGene);
            return true;
        }

        public List<MiRnaGene> getMiRnaGenes() {
            return miRnaGenes;
        }

        public MiRnaGene getMiRnaGene(String accession) {
            for (MiRnaGene miRnaGene : miRnaGenes) {
                if (accession.equals(miRnaGene.getAccession())) {
                    return miRnaGene;
                }
            }
            return null;
        }

        public int getCounter() {
            return miRnaGenes.size();
        }
    }

    @Test
    public void testMirBaseParser() throws IOException {
        Path datFile = Paths.get(getClass().getResource("/miRNA.small.dat.gz").getPath());

        MyCallback callback = new MyCallback(">>> Testing message");

        MirBaseParser.parse(datFile, "Homo sapiens", callback);
        Assert.assertEquals(50, callback.getCounter());

        MiRnaGene mi0000060 = callback.getMiRnaGene("MI0000060");
        Assert.assertEquals("hsa-let-7a-1", mi0000060.getId());
        Assert.assertEquals("ugggaUGAGGUAGUAGGUUGUAUAGUUuuagggucacacccaccacugggagauaaCUAUACAAUCUACUGUCUUUCcua".toUpperCase(), mi0000060.getSequence().toUpperCase());
        int found = 0;
        for (MiRnaMature mature : mi0000060.getMatures()) {
            if ("MIMAT0000062".equals(mature.getAccession())) {
                found++;
                Assert.assertEquals("hsa-let-7a-5p", mature.getId());
                Assert.assertEquals("UGAGGUAGUAGGUUGUAUAGUU".toUpperCase(), mature.getSequence().toUpperCase());
                Assert.assertEquals(6, mature.getStart());
                Assert.assertEquals(27, mature.getEnd());
            } else if ("MIMAT0004481".equals(mature.getAccession())) {
                found++;
                Assert.assertEquals("hsa-let-7a-3p", mature.getId());
                Assert.assertEquals("CUAUACAAUCUACUGUCUUUC".toUpperCase(), mature.getSequence().toUpperCase());
                Assert.assertEquals(57, mature.getStart());
                Assert.assertEquals(77, mature.getEnd());
            }
        }
        Assert.assertEquals(2, found);

        MiRnaGene mi0000077 = callback.getMiRnaGene("MI0000077");
        Assert.assertEquals("hsa-mir-21", mi0000077.getId());
        Assert.assertEquals("ugucgggUAGCUUAUCAGACUGAUGUUGAcuguugaaucucauggCAACACCAGUCGAUGGGCUGUcugaca".toUpperCase(), mi0000077.getSequence().toUpperCase());
        found = 0;
        for (MiRnaMature mature : mi0000077.getMatures()) {
            if ("MIMAT0000076".equals(mature.getAccession())) {
                found++;
                Assert.assertEquals("hsa-miR-21-5p", mature.getId());
                Assert.assertEquals("UAGCUUAUCAGACUGAUGUUGA".toUpperCase(), mature.getSequence().toUpperCase());
                Assert.assertEquals(8, mature.getStart());
                Assert.assertEquals(29, mature.getEnd());
            } else if ("MIMAT0004494".equals(mature.getAccession())) {
                found++;
                Assert.assertEquals("hsa-miR-21-3p", mature.getId());
                Assert.assertEquals("CAACACCAGUCGAUGGGCUGU".toUpperCase(), mature.getSequence().toUpperCase());
                Assert.assertEquals(46, mature.getStart());
                Assert.assertEquals(66, mature.getEnd());
            }
        }
        Assert.assertEquals(2, found);
    }
}