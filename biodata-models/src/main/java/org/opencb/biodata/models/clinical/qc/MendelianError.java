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

package org.opencb.biodata.models.clinical.qc;

import org.opencb.biodata.models.common.Image;
import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;
import org.opencb.commons.datastore.core.ObjectMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//-------------------------------------------------------------------------
// M E N D E L I A N     E R R O R S     R E P O R T
//-------------------------------------------------------------------------

public class MendelianError {

    @DataField(id = "numErrors", description = FieldConstants.MENDELIAN_ERROR_NUM_ERRORS_DESCRIPTION)
    private int numErrors;

    @DataField(id = "sampleAggregation", description = FieldConstants.MENDELIAN_ERROR_SAMPLE_AGGREGATION_DESCRIPTION)
    private List<SampleAggregation> sampleAggregation;

    @DataField(id = "images", description = FieldConstants.MENDELIAN_ERROR_IMAGES_DESCRIPTION)
    private List<Image> images;

    @DataField(id = "attributes", description = FieldConstants.MENDELIAN_ERROR_ATTRIBUTES_DESCRIPTION)
    private ObjectMap attributes;


    //-------------------------------------------------------------------------
    // S A M P L E     A G G R E G A T I O N
    //-------------------------------------------------------------------------

    public static class SampleAggregation {

        // Sample
        private String sample;

        // Number of errors
        private int numErrors;

        // Ratio for that sample = total / number_of_variants
        private double ratio;


        // Aggregation per chromosome
        private List<ChromosomeAggregation> chromAggregation;

        public SampleAggregation() {
            chromAggregation = new ArrayList<>();
        }

        public SampleAggregation(String sample, int numErrors, double ratio, List<ChromosomeAggregation> chromAggregation) {
            this.sample = sample;
            this.numErrors = numErrors;
            this.ratio = ratio;
            this.chromAggregation = chromAggregation;
        }

        public String getSample() {
            return sample;
        }

        public SampleAggregation setSample(String sample) {
            this.sample = sample;
            return this;
        }

        public int getNumErrors() {
            return numErrors;
        }

        public SampleAggregation setNumErrors(int numErrors) {
            this.numErrors = numErrors;
            return this;
        }

        public double getRatio() {
            return ratio;
        }

        public SampleAggregation setRatio(double ratio) {
            this.ratio = ratio;
            return this;
        }

        public List<ChromosomeAggregation> getChromAggregation() {
            return chromAggregation;
        }

        public SampleAggregation setChromAggregation(List<ChromosomeAggregation> chromAggregation) {
            this.chromAggregation = chromAggregation;
            return this;
        }

        //-------------------------------------------------------------------------
        // C H R O M O S O M E     A G G R E G A T I O N
        //-------------------------------------------------------------------------

        public static class ChromosomeAggregation {

            // Chromosome
            private String chromosome;

            // Number of errors
            private int numErrors;

            // Aggregation per error code for that chromosome
            private Map<String, Integer> errorCodeAggregation;

            public ChromosomeAggregation() {
            }

            public ChromosomeAggregation(String chromosome, int numErrors, Map<String, Integer> errorCodeAggregation) {
                this.chromosome = chromosome;
                this.numErrors = numErrors;
                this.errorCodeAggregation = errorCodeAggregation;
            }

            public String getChromosome() {
                return chromosome;
            }

            public ChromosomeAggregation setChromosome(String chromosome) {
                this.chromosome = chromosome;
                return this;
            }

            public int getNumErrors() {
                return numErrors;
            }

            public ChromosomeAggregation setNumErrors(int numErrors) {
                this.numErrors = numErrors;
                return this;
            }

            public Map<String, Integer> getErrorCodeAggregation() {
                return errorCodeAggregation;
            }

            public ChromosomeAggregation setErrorCodeAggregation(Map<String, Integer> errorCodeAggregation) {
                this.errorCodeAggregation = errorCodeAggregation;
                return this;
            }
        }
    }

    public MendelianError() {
        this(0, new ArrayList<>(), new ArrayList<>(), new ObjectMap());
    }

    public MendelianError(int numErrors, List<SampleAggregation> sampleAggregation, List<Image> images, ObjectMap attributes) {
        this.numErrors = numErrors;
        this.sampleAggregation = sampleAggregation;
        this.images = images;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MendelianError{");
        sb.append("numErrors=").append(numErrors);
        sb.append(", sampleAggregation=").append(sampleAggregation);
        sb.append(", images=").append(images);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public int getNumErrors() {
        return numErrors;
    }

    public MendelianError setNumErrors(int numErrors) {
        this.numErrors = numErrors;
        return this;
    }

    public List<SampleAggregation> getSampleAggregation() {
        return sampleAggregation;
    }

    public MendelianError setSampleAggregation(List<SampleAggregation> sampleAggregation) {
        this.sampleAggregation = sampleAggregation;
        return this;
    }

    public List<Image> getImages() {
        return images;
    }

    public MendelianError setImages(List<Image> images) {
        this.images = images;
        return this;
    }

    public ObjectMap getAttributes() {
        return attributes;
    }

    public MendelianError setAttributes(ObjectMap attributes) {
        this.attributes = attributes;
        return this;
    }
}
