/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.formats.variant.clinvar.rcv;

import org.opencb.commons.utils.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class ClinvarParser {

    public final static String CLINVAR_CONTEXT_v64 = "org.opencb.biodata.formats.variant.clinvar.rcv.v64jaxb";

    public static void saveXMLInfo(Object obj, String filename) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(CLINVAR_CONTEXT_v64);
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
        return loadXMLInfo(filename, CLINVAR_CONTEXT_v64);
    }

    /**
     * Checks if XML info path exists and loads it
     *
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static Object loadXMLInfo(String filename, String clinvarVersion) throws JAXBException, IOException {
        InputStream inputStream = FileUtils.newInputStream(Paths.get(filename));
        JAXBContext jaxbContext = JAXBContext.newInstance(clinvarVersion);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(inputStream);
    }
}
