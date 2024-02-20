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

package org.opencb.biodata.formats.variant.clinvar;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.formats.variant.clinvar.rcv.ClinvarParser;
import org.opencb.biodata.formats.variant.clinvar.rcv.v64jaxb.PublicSetType;
import org.opencb.biodata.formats.variant.clinvar.rcv.v64jaxb.ReleaseType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;

public class ClinvarParserTest {

    @Test
    public void loadXMLInfo() {
        try {
            JAXBElement<ReleaseType> objectFactory = (JAXBElement<ReleaseType>) ClinvarParser
                    .loadXMLInfo("/home/imedina/Downloads/ClinVarFullRelease_00-latest.xml.gz", ClinvarParser.CLINVAR_CONTEXT_v64);
            for (PublicSetType publicSetType : objectFactory.getValue().getClinVarSet()) {
                System.out.println("publicSetType.getTitle() = " + publicSetType.getTitle());
                break;
            }
            System.out.println(objectFactory.getValue().getClinVarSet().size());

        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }
}