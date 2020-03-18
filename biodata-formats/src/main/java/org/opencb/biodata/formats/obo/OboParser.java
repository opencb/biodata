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

package org.opencb.biodata.formats.obo;

import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.opencb.biodata.models.core.OboTerm;

import java.io.*;
import java.util.*;


public class OboParser {

    public OboParser() {

    }

    public List<OboTerm> parseOBO(BufferedReader bufferedReader) throws IOException {
        OBOFormatParser parser = new OBOFormatParser();
        OBODoc oboDoc = parser.parse(bufferedReader);
        Collection<Frame> frames = oboDoc.getTermFrames();
        List<OboTerm> oboTerms = new ArrayList<>();
        for (Frame frame : frames) {
            OboTerm oboTerm = new OboTerm();
            oboTerm.setId(frame.getId());
            for (String tag : frame.getTags()) {
                switch(tag) {
                    case "name":
                        oboTerm.setName((String) frame.getTagValue(tag));
                        break;
                    case "def":
                        oboTerm.setDescription((String) frame.getTagValue(tag));
                        break;
                    case "namespace":
                        oboTerm.setNamespace((String) frame.getTagValue(tag));
                        break;
                    case "xref":
                        List<String> xrefs = oboTerm.getXrefs();
                        if (xrefs == null) {
                            xrefs = new ArrayList<>();
                        }
                        Xref xref = (Xref) frame.getTagValue(tag);
                        xrefs.add(xref.getIdref());
                        oboTerm.setXrefs(xrefs);
                        break;
                    case "comment":
                        oboTerm.setComment((String) frame.getTagValue(tag));
                        break;
                    case "synonym":
                        List<String> synonyms = oboTerm.getSynonyms();
                        if (synonyms == null) {
                            synonyms = new ArrayList<>();
                        }
                        synonyms.add((String) frame.getTagValue(tag));
                        oboTerm.setSynonyms(synonyms);
                        break;
                    case "is_a":
                        List<String> parents = oboTerm.getParents();
                        if (parents == null) {
                            parents = new ArrayList<>();
                        }
                        String parentId = (String) frame.getTagValue(tag);
                        parents.add(parentId);
                        oboTerm.setParents(parents);
                        break;
                    default:
                        // code block
                }
            }
            oboTerms.add(oboTerm);
        }
        return oboTerms;
    }

//    private OboTerm getOboTerm(String id) {
//        OboTerm oboTerm = oboTermMap.get(id);
//        if (oboTerm == null) {
//            oboTerm = new OboTerm();
//            oboTerm.setId(id);
//            oboTermMap.put(id, oboTerm);
//        }
//        return oboTerm;
//    }
}
