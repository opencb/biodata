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

package org.opencb.biodata.tools.clinical;

import org.apache.commons.collections.CollectionUtils;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.clinical.interpretation.ReportedVariant;
import org.opencb.biodata.models.variant.Variant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ReportedVariantCreator {

    public abstract List<ReportedVariant> create(List<Variant> variants);

    protected Map<String, List<String>> getGeneToPanelIdMap(List<DiseasePanel> diseasePanels) {
        Map<String, List<String>> geneToPanelIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel: diseasePanels) {
                for (DiseasePanel.GenePanel genePanel : panel.getGenes()) {
                    if (genePanel.getId() != null) {
                        if (!geneToPanelIdMap.containsKey(genePanel.getId())) {
                            geneToPanelIdMap.put(genePanel.getId(), new ArrayList<>());
                        }
                        geneToPanelIdMap.get(genePanel.getId()).add(panel.getId());
                    }
                    geneToPanelIdMap.put(genePanel.getId(), null);
                }
            }
        }
        return geneToPanelIdMap;
    }
}
