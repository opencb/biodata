/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.variant.clinvar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

public class ClinvarParser {

    public final static String CLINVAR_CONTEXT_v19 = "org.opencb.biodata.formats.variant.clinvar.v19jaxb";
    public final static String CLINVAR_CONTEXT_v24 = "org.opencb.biodata.formats.variant.clinvar.v24jaxb";

    public static void saveXMLInfo(Object obj, String filename) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext;
        jaxbContext = JAXBContext.newInstance(CLINVAR_CONTEXT_v24);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(obj, new FileOutputStream(filename));
    }

    /**
     * Checks if XML info path exists and loads it
     *
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static Object loadXMLInfo(String filename) throws JAXBException {
        Object obj = null;
        JAXBContext jaxbContext = JAXBContext.newInstance(CLINVAR_CONTEXT_v24);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        obj = unmarshaller.unmarshal(new File(filename));
        return obj;
    }

    /**
     * Checks if XML info path exists and loads it
     *
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static Object loadXMLInfo(String filename, String clinvarVersion) throws JAXBException, IOException {
        Object obj = null;
        JAXBContext jaxbContext = JAXBContext.newInstance(clinvarVersion);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        // Reading GZip input stream
        InputStream inputStream;
        if (filename.endsWith(".gz")) {
            inputStream = new GZIPInputStream(new FileInputStream(new File(filename)));
        }else {
            inputStream = Files.newInputStream(Paths.get(filename));
        }
        obj = unmarshaller.unmarshal(inputStream);
        return obj;
    }
}
