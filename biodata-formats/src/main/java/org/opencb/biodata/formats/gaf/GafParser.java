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

import org.apache.commons.collections.keyvalue.MultiKey;
import org.opencb.biodata.models.core.AnnotationEvidence;
import org.opencb.biodata.models.core.FeatureOntologyTermAnnotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class GafParser {

    public Map<String, List<FeatureOntologyTermAnnotation>> parseGaf(BufferedReader bufferedReader) throws IOException {

        // protein + term --> evidence code + qualifier --> publications
        Map<MultiKey, Map<MultiKey, Set<String>>> transcriptTermToAnnotation = new HashMap();

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

            String dbObjectId = array[1]; // eg protein, transcript
            String goId = array[4];
            String qualifier = array[3];
            String evidenceCode = array[6];
            String pubmeds = array[5];
            Set<String> publications = new HashSet(Arrays.asList(pubmeds.split(",")));

            MultiKey featureTermMultiKey = new MultiKey(dbObjectId, goId);
            MultiKey codeQualifierMultiKey = new MultiKey(evidenceCode, qualifier);

            Map<MultiKey, Set<String>> codeToPublications = transcriptTermToAnnotation.get(featureTermMultiKey);

            // new protein + term pair
            if (codeToPublications == null) {
                codeToPublications = new HashMap<>();
                codeToPublications.put(codeQualifierMultiKey, publications);
                transcriptTermToAnnotation.put(featureTermMultiKey, codeToPublications);
            } else {
                // we've seen this protein + term before
                Set<String> alreadySeenPublications = codeToPublications.get(codeQualifierMultiKey);
                // new evidence code + qualifier
                if (alreadySeenPublications == null) {
                    alreadySeenPublications = new HashSet<>();
                }
                alreadySeenPublications.addAll(publications);
                codeToPublications.put(codeQualifierMultiKey, alreadySeenPublications);
                // annotations.computeIfAbsent(dbObjectId, k -> new ArrayList<>()).add(featureOntologyTermAnnotation);
            }
        }
        Map<String, List<FeatureOntologyTermAnnotation>> annotations = new HashMap<>();

        // for every protein + term
        for (Map.Entry<MultiKey, Map<MultiKey, Set<String>>> entry : transcriptTermToAnnotation.entrySet()) {
            MultiKey featureTermMultiKey = entry.getKey();
            String dbObjectId = featureTermMultiKey.getKeys()[0].toString();
            String goId = featureTermMultiKey.getKeys()[1].toString();
            Map<MultiKey, Set<String>> codeToPublications = entry.getValue();
            List<AnnotationEvidence> evidenceList = new ArrayList<>();
            // for this protein + term, every evidence code + qualifier pair
            for (Map.Entry<MultiKey, Set<String>> evidenceEntry : codeToPublications.entrySet()) {
                MultiKey codeQualifierMultiKey = evidenceEntry.getKey();
                String evidenceCode = codeQualifierMultiKey.getKeys()[0].toString();
                String qualifier = codeQualifierMultiKey.getKeys()[1].toString();
                if (qualifier.isEmpty()) {
                    qualifier = null;
                }
                Set<String> publications = evidenceEntry.getValue();
                AnnotationEvidence annotationEvidence = new AnnotationEvidence(evidenceCode, publications, qualifier);
                evidenceList.add(annotationEvidence);
            }
            FeatureOntologyTermAnnotation featureOntologyTermAnnotation = new FeatureOntologyTermAnnotation(goId, null, "GO", null,
                    evidenceList);
            annotations.computeIfAbsent(dbObjectId, k -> new ArrayList<>()).add(featureOntologyTermAnnotation);
        }
        return annotations;
    }
}

