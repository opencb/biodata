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

package org.opencb.biodata.models.variant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.avro.CuratedVariantAvro;
import org.opencb.biodata.models.variant.avro.CurationClassification;
import org.opencb.biodata.models.variant.avro.CurationScore;
import org.opencb.biodata.models.variant.avro.EvidenceEntry;
import org.opencb.biodata.models.variant.avro.Comment;
import org.opencb.biodata.models.variant.avro.CurationHistoryEntry;

import java.io.Serializable;
import java.util.*;

/**
 * @author Pablo Riesgo;
 */
@JsonIgnoreProperties({"impl", "variant"})
public class CuratedVariant implements Serializable {

    private CuratedVariantAvro impl;
    private Variant variant;

    public CuratedVariant() {
        this.variant = new Variant();
        this.impl = new CuratedVariantAvro(
                this.variant.getImpl(),
                this.getDefaultCurationClassification(),
                this.getDefaultCurationScore(),
                this.getDefaultCurationHistory(),
                this.getDefaultEvidences(),
                this.getDefaultComments()
        );
    }

    public CuratedVariant(CuratedVariantAvro avro) {
        Objects.requireNonNull(avro);
        this.variant = new Variant(avro.getVariant());
        this.impl = avro;
    }

    public CuratedVariant(Variant variant) {
        //TODO: perform checks on the Variant, we don't want to store information from multiple samples
        // so we may want to delete it
        this.variant = variant;
        this.impl = new CuratedVariantAvro(
                this.variant.getImpl(),
                this.getDefaultCurationClassification(),
                this.getDefaultCurationScore(),
                this.getDefaultCurationHistory(),
                this.getDefaultEvidences(),
                this.getDefaultComments()
        );
    }

    public CuratedVariant(Variant variant, String curationClassification,
                          Integer curationScore, List curationHistory,
                          List evidences, List comments) {
        this(variant);
        this.setCurationClassification(curationClassification);
        this.setCurationScore(curationScore);
        this.setCurationHistory(curationHistory);
        this.setEvidences(evidences);
        this.setComments(comments);
    }

    private CurationClassification getDefaultCurationClassification() {
        return CurationClassification.VUS;
    }

    private CurationScore getDefaultCurationScore() {
        return new CurationScore(0);
    }

    private List getDefaultCurationHistory() {
        return new LinkedList<CurationHistoryEntry>();
    }

    private List getDefaultEvidences() {
        return new LinkedList<EvidenceEntry>();
    }

    private List getDefaultComments() {
        return new LinkedList<Comment>();
    }

    public void setCurationClassification(String curationClassification) {
        if (curationClassification == null) {
            impl.setClassification(this.getDefaultCurationClassification());
        }
        else {
            impl.setClassification(CurationClassification.valueOf(curationClassification));
        }
    }

    public String getCurationClassification() {
        return impl.getClassification().toString();
    }

    public void setCurationScore(Integer curationScore) {
        if (curationScore == null) {
            impl.setCurationScore(this.getDefaultCurationScore());
        }
        else if (curationScore < 0 || curationScore > 5) {
            throw new IllegalArgumentException("The curation score must be in the interval [0, 5]");
        }
        else {
            impl.setCurationScore(new CurationScore(curationScore));
        }
    }

    public Integer getCurationScore() {
        return impl.getCurationScore().getVariantScore();
    }

    public void setCurationHistory(List curationHistory) {
        if (curationHistory == null) {
            impl.setHistory(this.getDefaultCurationHistory());
        }
        else {
            impl.setHistory(curationHistory);
        }
    }

    public List getCurationHistory() {
        return impl.getHistory();
    }

    public void setEvidences(List evidences) {
        if (evidences == null) {
            impl.setEvidences(this.getDefaultEvidences());
        }
        else {
            impl.setEvidences(evidences);
        }
    }

    public List getEvidences() {
        return impl.getEvidences();
    }

    public void setComments(List comments) {
        if (comments == null) {
            impl.setComments(this.getDefaultComments());
        }
        else {
            impl.setComments(comments);
        }
    }

    public List getComments() {
        return impl.getComments();
    }
}

