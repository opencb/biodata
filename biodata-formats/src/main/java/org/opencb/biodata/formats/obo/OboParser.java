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
import org.apache.commons.lang3.StringUtils;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.opencb.biodata.models.core.OntologyTerm;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


public class OboParser {

    private Map<String, OntologyTerm> oboTerms;

    public OboParser() {
    }

    public List<OntologyTerm> parseOBO(BufferedReader bufferedReader) throws IOException {
        return parseOBO(bufferedReader, null);
    }

    public List<OntologyTerm> parseOBO(Path path, String ontologyName) throws IOException {
        BufferedReader bufferedReader = FileUtils.newBufferedReader(path);
        return parseOBO(bufferedReader, ontologyName);
    }

    public List<OntologyTerm> parseOBO(BufferedReader bufferedReader, String ontologyName) throws IOException {
        OBOFormatParser parser = new OBOFormatParser();
        OBODoc oboDoc = parser.parse(bufferedReader);
        Collection<Frame> frames = oboDoc.getTermFrames();
        oboTerms = new LinkedHashMap<>();
        for (Frame frame : frames) {
            String oboId = frame.getId();
            OntologyTerm ontologyTerm = getOntologyTerm(oboId);
            if (StringUtils.isNotEmpty(ontologyName)) {
                ontologyTerm.setSource(ontologyName);
            }
            for (String tag : frame.getTags()) {
                switch(tag) {
                    case "name":
                        ontologyTerm.setName((String) frame.getTagValue(tag));
                        break;
                    case "def":
                        ontologyTerm.setDescription((String) frame.getTagValue(tag));
                        break;
                    case "namespace":
                        ontologyTerm.setNamespace((String) frame.getTagValue(tag));
                        break;
                    case "xref":
                        List<String> existingXrefs = ontologyTerm.getXrefs();
                        Collection<Object> xrefs = frame.getTagValues(tag);
                        for (Object xref : xrefs) {
                            existingXrefs.add(((Xref) xref).getIdref());
                        }
                        ontologyTerm.setXrefs(existingXrefs);
                        break;
                    case "comment":
                        ontologyTerm.setComment((String) frame.getTagValue(tag));
                        break;
                    case "synonym":
                        List<String> existingSynonyms = ontologyTerm.getSynonyms();
                        Collection<Object> synonyms = frame.getTagValues(tag);
                        for (Object synonym : synonyms) {
                            existingSynonyms.add(String.valueOf(synonym));
                        }
                        ontologyTerm.setSynonyms(existingSynonyms);
                        break;
                    case "is_a":
                        List<String> existingParents = ontologyTerm.getParents();
                        Collection<Object> parents = frame.getTagValues(tag);
                        for (Object parent : parents) {
                            existingParents.add(String.valueOf(parent));
                            addChild(String.valueOf(parent), oboId);
                        }
                        ontologyTerm.setParents(existingParents);
                        break;
                    default:
                        // new tag we don't parse ignore
                }
            }

        }
        return new ArrayList<>(oboTerms.values());
    }

    private void addChild(String parentId, String childId) {
        OntologyTerm parentTerm = getOntologyTerm(parentId);
        List<String> children = parentTerm.getChildren();
        children.add(childId);
        parentTerm.setChildren(children);
    }

    private OntologyTerm getOntologyTerm(String id) {
        OntologyTerm ontologyTerm = oboTerms.get(id);
        if (ontologyTerm == null) {
            ontologyTerm = new OntologyTerm(id, null, null, null, null, null,
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            oboTerms.put(id, ontologyTerm);
        }
        return ontologyTerm;
    }
}
