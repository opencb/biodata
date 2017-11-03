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

package org.opencb.biodata.models.variant.stats;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: aleman
 * Date: 8/28/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class VariantGroupStats {

    private String group;
    private Map<String, List<VariantStats>> variantStats;
    private Object samples;

    public VariantGroupStats(String group, Set<String> groupValues) {
        this.group = group;
        variantStats = new LinkedHashMap<>(groupValues.size());
        List<VariantStats> list;
        for (String groupVal : groupValues) {
            list = new ArrayList<>(1000);
            variantStats.put(groupVal, list);
        }
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, List<VariantStats>> getVariantStats() {
        return variantStats;
    }

    public void setVariantStats(Map<String, List<VariantStats>> variantStats) {
        this.variantStats = variantStats;
    }

    public Object getSamples() {
        return samples;
    }

    public void setSamples(Object samples) {
        this.samples = samples;
    }
}
