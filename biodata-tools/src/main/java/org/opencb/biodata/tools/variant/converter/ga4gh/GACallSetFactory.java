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

import org.ga4gh.models.CallSet;

import java.util.*;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GACallSetFactory extends AbstractGa4ghCallSetConverter<CallSet> {

    public GACallSetFactory() {
        super();
    }

    /**
     * @deprecated Use {@link #convert(List, List)} instead
     */
    @Deprecated
    public static List<CallSet> create(List<String> variantSetNames, List<List<String>> callSets) {
        return new GACallSetFactory().convert(variantSetNames, callSets);
    }

    @Override
    protected CallSet newCallSet(String callSetId, String callSetName, String sampleId, ArrayList<String> variantSetIds, long created, long updated, Map<String, List<String>> info) {
        return new CallSet(callSetId, callSetName, sampleId, variantSetIds, created, updated, info);
    }
}
