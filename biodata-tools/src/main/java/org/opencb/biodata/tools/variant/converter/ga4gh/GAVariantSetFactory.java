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

import org.ga4gh.models.VariantSet;
import org.ga4gh.models.VariantSetMetadata;
import org.opencb.biodata.models.variant.VariantSource;

import java.util.*;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantSetFactory<VS, VSM> extends AbstractGa4ghVariantSetConverter<VariantSet, VariantSetMetadata>{

    /**
     * @deprecated Use {@link #apply(List)} instead
     */
    @Deprecated
    public static List<VariantSet> create(List<VariantSource> variantSources) {
        return new GAVariantSetFactory<>().apply(variantSources);
    }

    protected VariantSet newVariantSet(String id, String name, String datasetId, String referenceSetId, List<VariantSetMetadata> metadata) {
        return new VariantSet(id, name, datasetId, referenceSetId, metadata);
    }

    @Override
    protected VariantSetMetadata newVariantSetMetadata(String key, String value, String id, String type, String number, String description, Map<String, List<String>> info) {
        return new VariantSetMetadata(key, value, id, type, number, description, info);
    }

}
