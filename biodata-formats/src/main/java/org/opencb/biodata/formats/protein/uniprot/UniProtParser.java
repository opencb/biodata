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

package org.opencb.biodata.formats.protein.uniprot;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by imedina on 25/09/15.
 */
public class UniProtParser {

    public final static String UNIPROT_CONTEXT_v202003 = "org.opencb.biodata.formats.protein.uniprot.v202003jaxb";

    public static void saveXMLInfo(Object obj, String filename) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext;
        jaxbContext = JAXBContext.newInstance(UNIPROT_CONTEXT_v202003);
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
        JAXBContext jaxbContext = JAXBContext.newInstance(UNIPROT_CONTEXT_v202003);
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
    public static Object loadXMLInfo(String filename, String uniprotVersion) throws JAXBException {
        Object obj = null;
        JAXBContext jaxbContext = JAXBContext.newInstance(uniprotVersion);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        obj = unmarshaller.unmarshal(new File(filename));
        return obj;
    }
}
