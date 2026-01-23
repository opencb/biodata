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

    public final static String UNIPROT_202003_CONTEXT = "org.opencb.biodata.formats.protein.uniprot.v202003jaxb";
    public final static String UNIPROT_202502_CONTEXT = "org.opencb.biodata.formats.protein.uniprot.v202502jaxb";
    public final static String UNIPROT_LATEST_CONTEXT = UNIPROT_202502_CONTEXT;

    @Deprecated
    public static void saveXMLInfo(Object obj, String filename) throws FileNotFoundException, JAXBException {
        saveXMLInfo(obj, UNIPROT_LATEST_CONTEXT, filename);
    }

    public static void saveXMLInfo(Object obj, String uniprotContext, String filename) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext;
        jaxbContext = JAXBContext.newInstance(uniprotContext);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(obj, new FileOutputStream(filename));
    }

    public static Object loadXMLInfo(String filename) throws JAXBException {
        return loadXMLInfo(filename, UNIPROT_LATEST_CONTEXT);
    }

    public static Object loadXMLInfo(String filename, String uniprotContext) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(uniprotContext);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(new File(filename));
    }
}
