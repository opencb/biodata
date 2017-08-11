/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant.converters.ga4gh;

import ga4gh.Variants;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.tools.variant.converters.ga4gh.factories.Ga4ghVariantFactory;
import org.opencb.biodata.tools.variant.converters.ga4gh.factories.ProtoGa4GhVariantFactory;
import org.opencb.biodata.tools.variant.converters.Converter;

import java.util.*;

/**
 * Created on 08/08/16.
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class Ga4ghCallSetConverter<CS> implements Converter<VariantFileMetadata, List<CS>> {

    private final Ga4ghVariantFactory<?, ?, CS, ?, ?> factory;

    public Ga4ghCallSetConverter(Ga4ghVariantFactory<?, ?, CS, ?, ?> factory) {
        this.factory = factory;
    }

    public Ga4ghCallSetConverter() {
        this((Ga4ghVariantFactory) new ProtoGa4GhVariantFactory());
    }

    public static Ga4ghCallSetConverter<Variants.CallSet> converter() {
        return new Ga4ghCallSetConverter<>(new ProtoGa4GhVariantFactory());
    }

    @Override
    public List<CS> convert(VariantFileMetadata fileMetadata) {
        return convert(fileMetadata.getId(), fileMetadata.getSampleIds());
    }

    public List<CS> convert(List<String> variantSetIds, List<List<String>> callSets) {
        List<CS> sets = new ArrayList<>();

        Iterator<String> variantSetIdIterator = variantSetIds.iterator();
        Iterator<List<String>> callSetsIterator = callSets.iterator();

        while (variantSetIdIterator.hasNext() && callSetsIterator.hasNext()) {
            String fileId = variantSetIdIterator.next();
            List<String> callsInFile = callSetsIterator.next();

            convert(fileId, callsInFile, sets);
        }

        return sets;
    }

    private List<CS> convert(String fileId, List<String> callsInFile) {
        return convert(fileId, callsInFile, new ArrayList<>());
    }

    private List<CS> convert(String fileId, List<String> callsInFile, List<CS> sets) {
        long time = 0;

        // Add all samples in the file
        for (String callName : callsInFile) {
            ArrayList<String> variantSetIds = new ArrayList<>(1);
            variantSetIds.add(fileId);
            CS callset = factory.newCallSet("", callName, "", variantSetIds, time, time, Collections.emptyMap());
            sets.add(callset);
        }
        return sets;
    }

}
