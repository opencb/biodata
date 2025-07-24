package org.opencb.biodata.formats.feature.chemirdb;


import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.formats.feature.mirbase.MirBaseParser;
import org.opencb.biodata.formats.feature.mirbase.MirBaseParserCallback;
import org.opencb.biodata.formats.feature.mirbase.MirBaseParserTest;
import org.opencb.biodata.models.core.MiRnaGene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChemirDbParserTest {


    @Test
    public void testParse() throws IOException {
        Path xlsxPath = Paths.get(getClass().getResource("/ChimerKB4.small.xlsx").getPath());

        ChemirDbParserTest.MyCallback callback = new ChemirDbParserTest.MyCallback(">>> Testing message");

        ChemirDbParser.parse(xlsxPath, callback);
        Assert.assertEquals(50, callback.getCounter());

//        MiRnaGene mi0000060 = callback.getMiRnaGene("MI0000060");
    }


    // Implementation of the MirBaseParserCallback function
    public class MyCallback implements ChemirDbParserCallback {
        private String msg;
        private List<Object> objects;

        public MyCallback(String msg) {
            this.msg = msg;
            this.objects = new ArrayList<>();
        }

        @Override
        public boolean processChemirDbItem(Object object) {
            System.out.println(msg);
            System.out.println(objects.toString());
            objects.add(object);
            return true;
        }

        public List<Object> getMiRnaGenes() {
            return objects;
        }

//        public MiRnaGene getMiRnaGene(String accession) {
//            for (MiRnaGene miRnaGene : miRnaGenes) {
//                if (accession.equals(miRnaGene.getAccession())) {
//                    return miRnaGene;
//                }
//            }
//            return null;
//        }

        public int getCounter() {
            return objects.size();
        }
    }

}