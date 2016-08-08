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

import java.util.List;
import java.util.Map;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantFactory extends AbstractGa4ghVariantConverter<Variant, Call> {

    @Override
    protected Variant newVariant(String id, String variantSetId, List<String> names, Long created, Long updated,
                                 String referenceName, Long start, Long end, String reference, List<String> alternates,
                                 Map<String, List<String>> info, List<Call> calls) {
        return new Variant(id, variantSetId, names, created, updated, referenceName,
                start,                // Ga4gh uses 0-based positions.
                end,                  // 0-based end does not change
                reference, alternates, info, calls);
    }

    @Override
    protected Call newCall(String callSetName, String callSetId, List<Integer> allelesIdx, String phaseSet, List<Double> genotypeLikelihood,
                           Map<String, List<String>> info) {
        return new Call(callSetName, callSetId, allelesIdx, phaseSet, genotypeLikelihood, info);
    }
}

