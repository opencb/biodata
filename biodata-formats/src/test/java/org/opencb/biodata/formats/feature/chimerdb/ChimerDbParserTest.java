package org.opencb.biodata.formats.feature.chimerdb;


import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.models.core.GeneFusion;
import org.opencb.biodata.models.core.chimerdb.ChimerKb;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChimerDbParserTest {

    public void testParse() throws IOException {
        Path xlsxPath = Paths.get(getClass().getResource("/ChimerKB4.small.xlsx").getPath());

        ChimerDbParserTest.MyCallback callback = new ChimerDbParserTest.MyCallback(">>> Testing message");

        ChimerKbParser.parse(xlsxPath, callback);
        Assert.assertEquals(50, callback.getGeneFusions().getChimerKb().size());
    }

    // Implementation of the MirBaseParserCallback function
    public class MyCallback implements ChimerDbParserCallback<ChimerKb> {
        private String msg;
        private GeneFusion geneFusions;

        public MyCallback(String msg) {
            this.msg = msg;
            this.geneFusions = new GeneFusion();
        }

        @Override
        public boolean processChimerDbObject(ChimerKb chimerKb) {
            System.out.println(msg);
            System.out.println(chimerKb.toString());
            geneFusions.getChimerKb().add(chimerKb);
            return true;
        }

        public GeneFusion getGeneFusions() {
            return geneFusions;
        }
    }

}