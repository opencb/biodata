package org.opencb.biodata.formats.annotation.io;

import org.junit.Test;

import static org.junit.Assert.*;

public class VepFormatReaderTest {

    @Test
    public void testRead() throws Exception {
        VepFormatReader vepFormatReader = new VepFormatReader("/tmp/test.vep");
        vepFormatReader.open();
        vepFormatReader.pre();
        vepFormatReader.read(3);
        vepFormatReader.post();
        vepFormatReader.close();
    }
}