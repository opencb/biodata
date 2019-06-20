package org.opencb.biodata.tools.clinical;

import org.apache.commons.collections.CollectionUtils;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;

import java.util.*;

public class ClinicalUtils {

    public static final String ASSEMBLY = "ASSEMBLY";
    public static final String DISORDER = "DISORDER";
    public static final String PANELS = "PANELS";
    public static final String BIOTYPES = "BIOTYPES";
    public static final String SEQUENCE_ONTOLOGY_TERMS = "SEQUENCE_ONTOLOGY_TERMS";
    public static final String MODE_OF_INHERITANCE = "MODE_OF_INHERITANCE";
    public static final String PENETRANCE = "PENETRANCE";
    public static final String ROLE_IN_CANCER_MANAGER = "ROLE_IN_CANCER_MANAGER";
    public static final String ACTIONABLE_VARIANT_MANAGER = "ACTIONABLE_VARIANT_MANAGER";
    public static final String TIERING = "TIERING";
    public static final String SET_TIER = "SET_TIER";

    public static Map<String, Set<DiseasePanel>> getVariantToPanelMap(List<DiseasePanel> diseasePanels) {
        Map<String, Set<DiseasePanel>> idToPanelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            if (CollectionUtils.isNotEmpty(diseasePanels)) {
                for (DiseasePanel panel : diseasePanels) {
                    // Put gene IDs
                    if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                        for (DiseasePanel.VariantPanel variantPanel : panel.getVariants()) {
                            if (variantPanel.getId() != null) {
                                if (!idToPanelMap.containsKey(variantPanel.getId())) {
                                    idToPanelMap.put(variantPanel.getId(), new HashSet<>());
                                }
                                idToPanelMap.get(variantPanel.getId()).add(panel);
                            }
                        }
                    }
                }
            }
        }
        return idToPanelMap;
    }

    public static Map<String, Set<DiseasePanel>> getGeneToPanelMap(List<DiseasePanel> diseasePanels) {
        Map<String, Set<DiseasePanel>> idToPanelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel : diseasePanels) {
                // Put gene IDs
                if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                    for (DiseasePanel.GenePanel genePanel : panel.getGenes()) {
                        if (genePanel.getId() != null) {
                            if (!idToPanelMap.containsKey(genePanel.getId())) {
                                idToPanelMap.put(genePanel.getId(), new HashSet<>());
                            }
                            idToPanelMap.get(genePanel.getId()).add(panel);
                        }
                    }
                }
            }
        }
        return idToPanelMap;
    }

}
