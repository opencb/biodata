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
 * Created by fjlopez on 20/11/14.
 */
@Deprecated
public class ExpressionValue {

    public enum Expression {UP, DOWN};

    private String geneId;
    private String transcriptId;
    private String experimentalFactor;
    private String factorValue;
    private String experimentId;
    private String technologyPlatform;
    private Expression expression;
    private Float pvalue;
    private String source;

    public ExpressionValue() { }

    @Deprecated
    public ExpressionValue(String experimentalFactor, String factorValue, String experimentId,
                           String technologyPlatform, Expression expression, Float pvalue) {
        this(null, null, experimentalFactor, factorValue, experimentId, technologyPlatform, expression, pvalue);
    }

    public ExpressionValue(String geneId, String transcriptId, String experimentalFactor, String factorValue,
                           String experimentId, String technologyPlatform, Expression expression, Float pvalue) {
        this.geneId = geneId;
        this.transcriptId = transcriptId;
        this.experimentalFactor = experimentalFactor;
        this.factorValue = factorValue;
        this.experimentId = experimentId;
        this.technologyPlatform = technologyPlatform;
        this.expression = expression;
        this.pvalue = pvalue;
    }

    public String getExperimentalFactor() {
        return experimentalFactor;
    }

    public void setExperimentalFactor(String experimentalFactor) {
        this.experimentalFactor = experimentalFactor;
    }

    public String getFactorValue() {
        return factorValue;
    }

    public void setFactorValue(String factorValue) {
        this.factorValue = factorValue;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getTechnologyPlatform() {
        return technologyPlatform;
    }

    public void setTechnologyPlatform(String technologyPlatform) {
        this.technologyPlatform = technologyPlatform;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Float getPvalue() {
        return pvalue;
    }

    public void setPvalue(Float pvalue) {
        this.pvalue = pvalue;
    }

    public String getGeneId() { return geneId; }

    public void setGeneId(String geneId) { this.geneId = geneId; }

    public String getTranscriptId() { return transcriptId; }

    public void setTranscriptId(String transcriptId) { this.transcriptId = transcriptId; }
}
