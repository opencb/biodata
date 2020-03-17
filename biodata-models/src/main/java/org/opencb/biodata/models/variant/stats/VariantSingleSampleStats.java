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

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/29/13
 * Time: 10:21 AM
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
/**
 * @deprecated use {@link org.opencb.biodata.models.variant.metadata.SampleVariantStats}
 */
@Deprecated
public class VariantSingleSampleStats {

    private String id;
    private int numMendelianErrors;
    private int numMissingGenotypes;
    private int numHomozygous;

    VariantSingleSampleStats() {
        this(null);
    }

    public VariantSingleSampleStats(String id) {
        this.id = id;
        this.numMendelianErrors = 0;
        this.numMissingGenotypes = 0;
        this.numHomozygous = 0;
    }

    public void incrementMendelianErrors() {
        this.numMendelianErrors++;
    }

    public void incrementMissingGenotypes() {
        this.numMissingGenotypes++;
    }

    public void incrementHomozygous() {
        this.numHomozygous++;
    }

    public String getId() {
        return id;
    }

    public int getNumMendelianErrors() {
        return numMendelianErrors;
    }

    public int getNumMissingGenotypes() {
        return numMissingGenotypes;
    }

    public int getNumHomozygous() {
        return numHomozygous;
    }

    public void incrementMendelianErrors(int mendelianErrors) {
        this.numMendelianErrors += mendelianErrors;
    }

    public void incrementMissingGenotypes(int missingGenotypes) {
        this.numMissingGenotypes += missingGenotypes;
    }

    public void incrementHomozygotesNumber(int homozygotesNumber) {
        this.numHomozygous += homozygotesNumber;

    }
}
