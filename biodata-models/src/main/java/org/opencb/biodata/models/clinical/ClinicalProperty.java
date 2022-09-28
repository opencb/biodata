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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class ClinicalProperty {

    public enum ModeOfInheritance {
        AUTOSOMAL_DOMINANT("monoallelic"),
        AUTOSOMAL_RECESSIVE("biallelic"),
        X_LINKED_DOMINANT,
        X_LINKED_RECESSIVE,
        Y_LINKED,
        MITOCHONDRIAL,

        DE_NOVO,
        MENDELIAN_ERROR("me"),
        COMPOUND_HETEROZYGOUS("ch"),

        UNKNOWN;

        private static Map<String, ModeOfInheritance> namesMap;

        static {
            namesMap = new HashMap<>();
            for (ModeOfInheritance mode : values()) {
                namesMap.put(mode.name().toLowerCase(), mode);
                namesMap.put(mode.name().replace("_", "").toLowerCase(), mode);
                if (mode.names != null) {
                    for (String name : mode.names) {
                        namesMap.put(name.toLowerCase(), mode);
                    }
                }
            }
        }

        private final String[] names;

        ModeOfInheritance(String... names) {
            this.names = names;
        }

        @Nullable
        public static ModeOfInheritance parseOrNull(String name) {
            return namesMap.get(name.toLowerCase());
        }

        @Nonnull
        public static ModeOfInheritance parse(String name) {
            ModeOfInheritance moi = namesMap.get(name.toLowerCase());
            if (moi == null) {
                throw new InvalidParameterException("Unknown ModeOfInheritance value: '" + name + "'");
            }
            return moi;
        }
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

    public enum Imprinted {
        NOT,
        MATERNALLY,
        PATERNALLY,
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
        FUSION,
        BOTH,
        NA,
        UNKNOWN
    }

}
