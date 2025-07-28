package org.opencb.biodata.formats.feature.chimerdb;


import org.junit.Assert;
import org.opencb.biodata.models.core.genefusion.GeneFusion;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChimerDbParserTest {

    
    public void testParse() throws IOException {
        Path xlsxPath = Paths.get(getClass().getResource("/ChimerKB4.small.xlsx").getPath());

        ChimerDbParserTest.MyCallback callback = new ChimerDbParserTest.MyCallback(">>> Testing message");

        ChimerDbParser.parse(xlsxPath, callback);
        Assert.assertEquals(50, callback.getCounter());

//        MiRnaGene mi0000060 = callback.getMiRnaGene("MI0000060");
    }


    // Implementation of the MirBaseParserCallback function
    public class MyCallback implements GeneFusionParserCallback {
        private String msg;
        private List<GeneFusion> geneFusionList;

        public MyCallback(String msg) {
            this.msg = msg;
            this.geneFusionList = new ArrayList<>();
        }

        @Override
        public boolean processGeneFusion(GeneFusion geneFusion) {
            System.out.println(msg);
            System.out.println(geneFusion.toString());
            geneFusionList.add(geneFusion);
            return true;
        }

        public List<GeneFusion> getGeneFusionList() {
            return geneFusionList;
        }

        public int getCounter() {
            return geneFusionList.size();
        }
    }

}