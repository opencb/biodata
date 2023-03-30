package org.opencb.biodata.formats.pubmed;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.zip.GZIPInputStream;

public class PubMedParser {


    public final static String PUBMED_CONTEXT = "org.opencb.biodata.formats.pubmed.v233jaxb";

    public static void saveXMLInfo(Object obj, String filename) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext;
        jaxbContext = JAXBContext.newInstance(PUBMED_CONTEXT);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(obj, new FileOutputStream(filename));
    }

    /**
     * Checks if XML info path exists and loads it
     *
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static Object loadXMLInfo(String filename) throws JAXBException, IOException {
        System.setProperty("javax.xml.accessExternalDTD", "all");

        Object obj = null;
        JAXBContext jaxbContext = JAXBContext.newInstance(PUBMED_CONTEXT);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        if (filename.endsWith("gz")) {
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gis = new GZIPInputStream(fis);
            obj = unmarshaller.unmarshal(gis);
        } else {
            obj = unmarshaller.unmarshal(new File(filename));
        }


        return obj;
    }
}
