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

package org.opencb.biodata.formats.variant.annotation;

import org.opencb.biodata.models.variant.avro.VariantAnnotation;

import java.util.Arrays;
import java.util.List;

/**
 * Created by imedina on 20/10/15.
 */
public class VepParser {


    /**
     * Convert a text from VEP int a single Variant Annotation
     * @param text Several lines refering to the same variant
     * @return
     */
    public static VariantAnnotation parse(String text) {
        if (text != null) {
            return parse(Arrays.asList(text.split("\n")));
        }
        return null;
    }

    /**
     * Convert a bunch of lines from VEP int a single Variant Annotation
     * @param lines All line must refer to the same variant
     * @return
     */
    public static VariantAnnotation parse(List<String> lines) {
        VariantAnnotation variantAnnotation = new VariantAnnotation();

        return null;
    }


    /**
     *
     * @param csqFormatFields A String list with all the fields annotated, ie.
     *                        Allele|Consequence|SYMBOL|Feature_type|BIOTYPEcDNA_position|CDS_position|Protein_position
     * @param csqInfoField The CSQ field from the VCF INFO column
     * @return
     */
    public static VariantAnnotation parseInfoCsq(List<String> csqFormatFields, String csqInfoField) {
        if (csqInfoField.startsWith("CSQ")) {
            csqInfoField = csqInfoField.replaceFirst("CSQ=", "");
        }

        VariantAnnotation variantAnnotation = new VariantAnnotation();


        return null;
    }

}
