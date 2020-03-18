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

import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
@Deprecated
public class VariantSourceStats {

    private final String fileId;
    private final String studyId;
    private List<String> sampleNames;
    private VariantSetStats fileStats;
    private Map<String, VariantSingleSampleStats> samplesStats;

    VariantSourceStats() {
        this(null, null);
    }

    public VariantSourceStats(String fileId, String studyId) {
        this.fileId = fileId;
        this.studyId = studyId;
        this.sampleNames = new ArrayList<>();
        this.fileStats = new VariantSetStats();
        this.samplesStats = new LinkedHashMap<>();
    }

    public List<String> getSampleNames() {
        return sampleNames;
    }

    public void setSampleNames(List<String> sampleNames) {
        this.sampleNames = sampleNames;
        fileStats.setNumSamples(sampleNames.size());
    }

    public VariantSetStats getFileStats() {
        return fileStats;
    }

    public void setFileStats(VariantSetStats fileStats) {
        this.fileStats = fileStats;
    }

    public void updateFileStats(List<Variant> variants) {
        int incompleteVariantStats = 0;
        for (Variant v : variants) {
            StudyEntry studyEntry = v.getStudy(studyId);
            if (studyEntry == null) {
                // The variant is not contained in this file
                continue;
            }
            try {
                VariantStats stats = studyEntry.getStats(StudyEntry.DEFAULT_COHORT);
                if (stats != null) {
                    fileStats.update(stats);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                incompleteVariantStats++;
            }
        }
        if (incompleteVariantStats != 0) {
            Logger logger = LoggerFactory.getLogger(VariantSourceStats.class);
            logger.warn("{} VariantStats have needed members as null", incompleteVariantStats);
        }
    }
        
    public Map<String, VariantSingleSampleStats> getSamplesStats() {
        return samplesStats;
    }

    public VariantSingleSampleStats getSampleStats(String sampleName) {
        return samplesStats.get(sampleName);
    }
    
    public void setSamplesStats(Map<String, VariantSingleSampleStats> variantSampleStats) {
        this.samplesStats = variantSampleStats;
    }

    public String getFileId() {
        return fileId;
    }

    public String getStudyId() {
        return studyId;
    }
}
