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

package org.opencb.biodata.models.clinical;

public class ClinicalProperty {

    public enum ModeOfInheritance {
        AUTOSOMAL_DOMINANT,
        MONOALLELIC_NOT_IMPRINTED,
        MONOALLELIC_MATERNALLY_IMPRINTED,
        MONOALLELIC_PATERNALLY_IMPRINTED,
        AUTOSOMAL_RECESSIVE,
        MONOALLELIC_AND_BIALLELIC,
        MONOALLELIC_AND_MORE_SEVERE_BIALLELIC,
        X_LINKED_DOMINANT,
        X_LINKED_RECESSIVE,
        Y_LINKED,
        MITOCHONDRIAL,

        // Not modes of inheritance, but...
        DE_NOVO,
        COMPOUND_HETEROZYGOUS,
        MENDELIAN_ERROR,
        UNKNOWN
    }

    public enum ClinicalSignificance {
        PATHOGENIC,
        LIKELY_PATHOGENIC,
        UNCERTAIN_SIGNIFICANCE,
        LIKELY_BENIGN,
        BENIGN,
        NOT_ASSESSED
    }

    public enum Penetrance {
        COMPLETE,
        INCOMPLETE,
        UNKNOWN
    }

    public enum Confidence {
        HIGH,
        MEDIUM,
        LOW,
        REJECTED
    }

    public enum RoleInCancer {
        ONCOGENE,
        TUMOR_SUPPRESSOR_GENE,
        BOTH
    }

}
