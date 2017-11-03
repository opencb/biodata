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

package org.opencb.biodata.models.variant.annotation;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class RegulatoryEffect {
    
    /**
     * Source and identifier of a transcription factor binding profile aligned at this position
     */
    private String motifName;
    
    /**
     * Relative position of the variation in the aligned TFBP
     */
    private int motifPosition;
    
    /**
     * Difference in motif score of the reference and variant sequences for the TFBP
     */
    private float motifScoreChange;
    
    /**
     * If the variant falls in a high information position of a transcription factor binding profile (TFBP)
     */
    private boolean highInformationPosition;
    
    /**
     * List of cell types and classifications for regulatory feature
     */
    private String cellType;

    RegulatoryEffect() { }

    public RegulatoryEffect(String motifName, int motifPosition, float motifScoreChange, boolean highInformationPosition, String cellType) {
        this.motifName = motifName;
        this.motifPosition = motifPosition;
        this.motifScoreChange = motifScoreChange;
        this.highInformationPosition = highInformationPosition;
        this.cellType = cellType;
    }

    public String getMotifName() {
        return motifName;
    }

    public void setMotifName(String motifName) {
        this.motifName = motifName;
    }

    public int getMotifPosition() {
        return motifPosition;
    }

    public void setMotifPosition(int motifPosition) {
        this.motifPosition = motifPosition;
    }

    public float getMotifScoreChange() {
        return motifScoreChange;
    }

    public void setMotifScoreChange(float motifScoreChange) {
        this.motifScoreChange = motifScoreChange;
    }

    public boolean isHighInformationPosition() {
        return highInformationPosition;
    }

    public void setHighInformationPosition(boolean highInformationPosition) {
        this.highInformationPosition = highInformationPosition;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }
    
    
}
