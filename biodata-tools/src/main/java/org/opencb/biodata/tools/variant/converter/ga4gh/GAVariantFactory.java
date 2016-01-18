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

package org.opencb.biodata.tools.variant.converter.ga4gh;

import org.ga4gh.models.Call;
import org.ga4gh.models.Variant;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.avro.FileEntry;

import java.util.*;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantFactory {


    /**
     * Given a list of variants, creates the equivalent set using the GA4GH API.
     * 
     * @param variants List of variants to transform
     * @return GA4GH variants representing the same data as the internal API ones
     */
    public List<Variant> create(List<org.opencb.biodata.models.variant.Variant> variants){
        List<Variant> gaVariants = new ArrayList<>(variants.size());

        for (org.opencb.biodata.models.variant.Variant variant : variants) {
            String id = variant.toString();

            List<String> variantIds = new ArrayList<>(variant.getIds());

            for (StudyEntry study : variant.getStudies()) {
                List<String> alternates = new ArrayList<>(study.getSecondaryAlternatesAlleles().size() + 1);
                alternates.add(variant.getAlternate());
                alternates.addAll(study.getSecondaryAlternatesAlleles());

                //Optional
                Long time = System.currentTimeMillis();

                //Only required for "graph" mode
                List<String> alleleIds = null;

                //VariableSet should be the study, the file, or be provided?
                String variantSetId = study.getStudyId();


                Variant ga = new Variant(id, variantSetId, variantIds, time, time, variant.getChromosome(),
                        Long.valueOf(to0BasedStart(variant.getStart())), // Ga4gh uses 0-based positions.
                        Long.valueOf(variant.getEnd()),                  // 0-based end does not change
                        variant.getReference(), alternates, alleleIds, parseInfo(study.getFiles()), parseCalls(null, study));

                gaVariants.add(ga);
            }
        }


        return gaVariants;
    }

    /**
     *
     * 0-based -> [start,end)
     * 1-based -> [start,end]
     *
     * a b c d e f
     * 0 1 2 3 4 5  <- 0-based from B to E : [1, 5)
     * 1 2 3 4 5 6  <- 1-based from B to E : [2, 5]
     *
     * @param start
     * @return
     */
    private Integer to0BasedStart(Integer start) {
        return start - 1;
    }


    private Map<String, List<String>> parseInfo(List<FileEntry> files) {
        Map<String, List<String>> parsedInfo = new HashMap<>();

        List<String> fileIds = new ArrayList<>(files.size());
        List<String> ori = new ArrayList<>(files.size());
        parsedInfo.put("FID", fileIds);
        parsedInfo.put("ORI", ori);
        int fileIdx = 0;
        for (FileEntry file : files) {
            fileIds.add(file.getFileId());
            ori.add(file.getCall());
            Map<String, String> attributes = file.getAttributes();
            for (Map.Entry<String, String> field : attributes.entrySet()) {
                List<String> value;
                if (parsedInfo.containsKey(field.getKey())) {
                    value = parsedInfo.get(field.getKey());
                } else {
                    value = Arrays.asList(new String[files.size()]);
                    parsedInfo.put(field.getKey(), value);
                }
                value.set(fileIdx, field.getValue());
            }
            fileIdx++;
        }
        return parsedInfo;
    }

    /**
     *
     * @param variantId Optional field. Only required if the calls is not being returned from the
     *                  server already contained within its `Variant`.
     * @param study
     * @return
     */
    private List<Call> parseCalls(String variantId, StudyEntry study) {
        List<Call> calls = new LinkedList<>();

        for (String sample : study.getOrderedSamplesName()) {

            // Create empty call object
            Call call = new Call();
            call.setCallSetId(sample);
            call.setCallSetName(null);
            call.setVariantId(variantId);
            Map<String, List<String>> info = new HashMap<>();
            call.setInfo(info);

            for (String formatField : study.getFormat()) {
                String sampleData = study.getSampleData(sample, formatField);
                if ("GT".equals(formatField)) {
                    // Transform genotype with form like 0|0 to the GA4GH style
                    Genotype genotype = new Genotype(sampleData);
                    List<Integer> allelesIdx = new ArrayList<>(genotype.getAllelesIdx().length);
                    for (int alleleIdx : genotype.getAllelesIdx()) {
                        if (alleleIdx >= 0) {
                            allelesIdx.add(alleleIdx);
                        }
                    }
                    call.setGenotype(allelesIdx);

                    String phaseSet = genotype.isPhased() ? "phased" : "unphased";
                    call.setPhaseset(phaseSet);
                } else if ("GL".equals(formatField)) {
                    String[] split = sample.split(",");
                    List<Double> genotypeLikelihood = new ArrayList<>(split.length);
                    for (String gl : split) {
                        genotypeLikelihood.add(Double.parseDouble(gl));
                    }
                    call.setGenotypeLikelihood(genotypeLikelihood);
                } else {
                    info.put(formatField, Collections.singletonList(sampleData));
                }
            }
            calls.add(call);
        }

        return calls;
    }

}
