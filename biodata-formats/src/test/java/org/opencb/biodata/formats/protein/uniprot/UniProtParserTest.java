package org.opencb.biodata.formats.protein.uniprot;

import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.formats.protein.uniprot.v202502jaxb.Uniprot;

import javax.xml.bind.JAXBException;

public class UniProtParserTest {

    @Test
    public void testParse() throws JAXBException {

        String fullFilename = getClass().getResource("/uniprot-202502/uniprot-test.xml").getPath();

        Uniprot uniprot = (Uniprot) UniProtParser.loadXMLInfo(fullFilename, UniProtParser.UNIPROT_202502_CONTEXT);

        System.out.println("fullFilename = " + fullFilename);
        System.out.println("uniprot.getEntry().size() = " + uniprot.getEntry().size());
        Assert.assertEquals(1, uniprot.getEntry().size());
    }
}