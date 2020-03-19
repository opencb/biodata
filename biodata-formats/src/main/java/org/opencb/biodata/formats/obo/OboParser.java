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

import org.apache.commons.collections.CollectionUtils;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.opencb.biodata.models.core.OboTerm;

import java.io.*;
import java.util.*;


public class OboParser {

    Map<String, OboTerm> oboTerms;

    public OboParser() {

    }

    public List<OboTerm> parseOBO(BufferedReader bufferedReader) throws IOException {
        OBOFormatParser parser = new OBOFormatParser();
        OBODoc oboDoc = parser.parse(bufferedReader);
        Collection<Frame> frames = oboDoc.getTermFrames();
        oboTerms = new HashMap<>();
        for (Frame frame : frames) {
            String oboId = frame.getId();
            OboTerm oboTerm = getOboTerm(oboId);
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
                        List<String> existingXrefs = oboTerm.getXrefs();
                        if (existingXrefs == null) {
                            existingXrefs = new ArrayList<>();
                        }
                        Collection<Object> xrefs = frame.getTagValues(tag);
                        for (Object xref : xrefs) {
                            existingXrefs.add(((Xref) xref).getIdref());
                        }
                        oboTerm.setXrefs(existingXrefs);
                        break;
                    case "comment":
                        oboTerm.setComment((String) frame.getTagValue(tag));
                        break;
                    case "synonym":
                        List<String> existingSynonyms = oboTerm.getSynonyms();
                        if (existingSynonyms == null) {
                            existingSynonyms = new ArrayList<>();
                        }
                        Collection<Object> synonyms = frame.getTagValues(tag);
                        for (Object synonym : synonyms) {
                            existingSynonyms.add(String.valueOf(synonym));
                        }
                        oboTerm.setSynonyms(existingSynonyms);
                        break;
                    case "is_a":
                        List<String> existingParents = oboTerm.getParents();
                        if (existingParents == null) {
                            existingParents = new ArrayList<>();
                        }
                        Collection<Object> parents = frame.getTagValues(tag);
                        for (Object parent : parents) {
                            existingParents.add(String.valueOf(parent));
                            addChild(String.valueOf(parent), oboId);
                        }
                        oboTerm.setParents(existingParents);
                        break;
                    default:
                        // new tag we don't parse ignore
                }
            }

        }
        return new ArrayList<>(oboTerms.values());
    }

    private void addChild(String parentId, String childId) {
        OboTerm parentTerm = getOboTerm(parentId);
        List<String> children = parentTerm.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            children = new ArrayList<>();
        }
        children.add(childId);
        parentTerm.setChildren(children);
    }

    private OboTerm getOboTerm(String id) {
        OboTerm oboTerm = oboTerms.get(id);
        if (oboTerm == null) {
            oboTerm = new OboTerm();
            oboTerm.setId(id);
            oboTerms.put(id, oboTerm);
        }
        return oboTerm;
    }
}
