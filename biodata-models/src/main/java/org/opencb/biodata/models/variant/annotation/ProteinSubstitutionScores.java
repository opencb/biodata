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

package org.opencb.biodata.models.variant.annotation;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class ProteinSubstitutionScores {
    
    public enum PolyphenEffect { PROBABLY_DAMAGING, POSSIBLY_DAMAGING, BENIGN, UNKNOWN};
    public enum SiftEffect { DELETERIOUS, TOLERATED };
    
    private float polyphenScore;
    
    private float siftScore;
    
    private PolyphenEffect polyphenEffect;
    
    private SiftEffect siftEffect;

    ProteinSubstitutionScores() {
        this(-1, -1, null, null);
    }

    public ProteinSubstitutionScores(float polyphenScore, float siftScore, PolyphenEffect polyphenEffect, SiftEffect siftEffect) {
        this.polyphenScore = polyphenScore;
        this.siftScore = siftScore;
        this.polyphenEffect = polyphenEffect;
        this.siftEffect = siftEffect;
    }

    public float getPolyphenScore() {
        return polyphenScore;
    }

    public void setPolyphenScore(float polyphenScore) {
        this.polyphenScore = polyphenScore;
    }

    public float getSiftScore() {
        return siftScore;
    }

    public void setSiftScore(float siftScore) {
        this.siftScore = siftScore;
    }

    public PolyphenEffect getPolyphenEffect() {
        return polyphenEffect;
    }

    public void setPolyphenEffect(PolyphenEffect polyphenEffect) {
        this.polyphenEffect = polyphenEffect;
    }

    public SiftEffect getSiftEffect() {
        return siftEffect;
    }

    public void setSiftEffect(SiftEffect siftEffect) {
        this.siftEffect = siftEffect;
    }

    
}
