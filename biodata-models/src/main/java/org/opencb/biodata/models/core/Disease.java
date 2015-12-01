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

package org.opencb.biodata.models.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by fjlopez on 20/05/15.
 */
@Deprecated
public class Disease {

    private String id;
    private String name;
    private String hpo;
    private Float score;
    private Integer numberOfPubmeds;
    private List<String> associationTypes;
    private Set<String> sources;
    private String source;

    public Disease(String id, String name, String hpo, Float score, Integer numberOfPubmeds, String associationType, Set<String> sources, String source) {
        this.id = id;
        this.hpo = hpo;
        this.name = name;
        this.score = score;
        this.numberOfPubmeds = numberOfPubmeds;
        this.associationTypes = new ArrayList<>();
        this.associationTypes.add(associationType);
        this.sources = sources;
        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Disease{");
        sb.append("id='").append(id).append('\'');
        sb.append(", hpo='").append(hpo).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", score=").append(score);
        sb.append(", numberOfPubmeds=").append(numberOfPubmeds);
        sb.append(", associationTypes=").append(associationTypes);
        sb.append(", sources=").append(sources);
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHpo() {
        return hpo;
    }

    public void setHpo(String hpo) {
        this.hpo = hpo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getNumberOfPubmeds() {
        return numberOfPubmeds;
    }

    public void setNumberOfPubmeds(Integer numberOfPubmeds) {
        this.numberOfPubmeds = numberOfPubmeds;
    }

    public List<String> getAssociationTypes() {
        return associationTypes;
    }

    public void setAssociationTypes(List<String> associationTypes) {
        this.associationTypes = associationTypes;
    }

    public Set<String> getSources() {
        return sources;
    }

    public void setSources(Set<String> sources) {
        sources = sources;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
