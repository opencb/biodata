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

package org.opencb.biodata.formats.gaf;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.formats.sequence.fasta.Fasta;
import org.opencb.biodata.models.core.OboTerm;
import org.opencb.biodata.models.core.OntologyAnnotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class GafParser {

    public Map<String, List<OntologyAnnotation>> parseGaf(BufferedReader bufferedReader) throws IOException {

        Map<String, List<OntologyAnnotation>> annotations = new HashMap<>();

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("!")) {
                continue;
            }
            String[] array = line.split("\t", -1); // keep trailing empty Strings
            if (array.length < 13) {
                throw new IllegalArgumentException("Not enough elements (should be > 13 not "
                        + array.length + ") in line: " + line);
            }

            String dbObjectId = array[1]; // protein
            String goId = array[4];
            String qualifier = array[3];
            String evidenceCode = array[6];
            String pubmeds = array[7];

            OntologyAnnotation ontologyAnnotation = new OntologyAnnotation();
            ontologyAnnotation.setOboTermId(goId);
            ontologyAnnotation.setEvidenceCodes(Collections.singletonList(evidenceCode));
            if (StringUtils.isNotEmpty(qualifier)) {
                ontologyAnnotation.setQualifier(qualifier);
            }
            ontologyAnnotation.setPublications(Arrays.asList(pubmeds.split(",")));
            annotations.computeIfAbsent(dbObjectId, k -> new ArrayList<>()).add(ontologyAnnotation);
        }
        return annotations;
    }
}
