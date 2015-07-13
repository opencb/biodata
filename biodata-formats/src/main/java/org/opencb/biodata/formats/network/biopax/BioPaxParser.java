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

package org.opencb.biodata.formats.network.biopax;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BioPaxParser extends DefaultHandler {

    private String filename;

    private String currEntityId = null;
    private String currEntityClass = null;
    private BioPaxElement currBioPaxElement = null;

    private String paramId = null;
    private String paramName = null;
    private StringBuilder paramValue = new StringBuilder();

    private BioPax bioPax;

    public BioPaxParser(String filename) {
        super();
        this.filename = filename;
    }

    public BioPax parse() throws IOException, SAXException {
        return parse(filename);
    }

    public BioPax parse(String filename) throws IOException, SAXException {
        bioPax = new BioPax();
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("File " + filename + " does not exist");
        }

        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(this);
        xr.parse(new InputSource(new FileReader(filename)));

        return bioPax;
    }

    //------------------------------------------------------------------
    // Event handlers.
    //------------------------------------------------------------------

    public void startDocument() {
        System.out.println("Start document");
    }


    public void endDocument() {
        System.out.println("End document");
    }


    public void startElement(String uri, String name, String qName, Attributes atts) {
        String id = null;
        String resource = null;

        //System.out.println("--> startElement: " + name);

        String entityClass = BioPaxConstants.getEntityClass(name);
        if (entityClass != null) {

            //System.out.println("name = " + name + ", entity class = " + entityClass);

            if (currEntityId == null) {

                if (atts != null && atts.getLength() > 0) {
                    for (int i = 0; i < atts.getLength(); i++) {
                        if ("ID".equalsIgnoreCase(atts.getLocalName(i))) {
                            id = atts.getValue(i);
                            atts.getLocalName(i);
                            break;
                        }
                    }
                    if (id != null) {
                        currEntityId = id;
                        currEntityClass = entityClass;
                        currBioPaxElement = new BioPaxElement(id, name);
                        //System.out.println("  attribute " + i + ": " + atts.getLocalName(i) + " = " + atts.getValue(i));
                    }
                }
            }
        } else {
            if (currEntityId != null) {
                if (atts != null && atts.getLength() > 0) {
                    for (int i = 0; i < atts.getLength(); i++) {
                        if ("resource".equalsIgnoreCase(atts.getLocalName(i))) {
                            resource = atts.getValue(i);
                            if (resource.startsWith("#")) {
                                resource = resource.substring(1);
                            }
                            break;
                        }
                    }
                }
                paramId = resource;
                paramName = name;
                paramValue.setLength(0);
            }
        }
    }


    public void endElement(String uri, String name, String qName) {

        //System.out.println("<-- endElement: " + name);

        if (currBioPaxElement != null && currBioPaxElement.getBioPaxClassName() != null) {
            if (currBioPaxElement.getBioPaxClassName().equalsIgnoreCase(name)) {

                bioPax.addEntity(currEntityId, currEntityClass, currBioPaxElement);
                //System.out.println("entity: " + currBioPaxElement.getBioPaxClassName() + ", class = " + currEntityClass + ", id = " + currEntityId);

                currEntityId = null;
                currEntityClass = null;
                currBioPaxElement = null;

            } else if (paramName.equalsIgnoreCase(name)) {

                if (paramId != null) {
                    currBioPaxElement.put(name + "-id", paramId);
                }
                String aux = paramValue.toString();
                if (aux != null && aux.trim().length() > 0) {
                    currBioPaxElement.put(name, aux.trim());
                }

                paramId = null;
                paramName = null;
                paramValue.setLength(0);
            }
        }
    }


    public void characters(char ch[], int start, int length) {
        if (currEntityId != null && paramName != null) {
            for (int i = start; i < start + length; i++) {
                paramValue.append(ch[i]);
            }
        }
    }
}
