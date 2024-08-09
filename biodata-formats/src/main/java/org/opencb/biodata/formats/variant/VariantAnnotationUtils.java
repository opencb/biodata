/*
 * Copyright 2015-2020 OpenCB
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

package org.opencb.biodata.formats.variant;

import org.opencb.biodata.models.variant.avro.AlleleOrigin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fjlopez on 22/06/15.
 */
public class VariantAnnotationUtils {

    private static final Map<String, AlleleOrigin> ORIGIN_STRING_TO_ALLELE_ORIGIN = new HashMap<>();
    private static final Map<Character, Character> COMPLEMENTARY_NT = new HashMap<>();

    static {

        ///////////////////////////////////////////////////////////////////////
        /////   ClinVar and Cosmic allele origins to SO terms   ///////////////
        ///////////////////////////////////////////////////////////////////////
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("germline", AlleleOrigin.germline_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("maternal", AlleleOrigin.maternal_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("de novo", AlleleOrigin.de_novo_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("paternal", AlleleOrigin.paternal_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("somatic", AlleleOrigin.somatic_variant);

        COMPLEMENTARY_NT.put('A', 'T');
        COMPLEMENTARY_NT.put('a', 't');
        COMPLEMENTARY_NT.put('C', 'G');
        COMPLEMENTARY_NT.put('c', 'g');
        COMPLEMENTARY_NT.put('G', 'C');
        COMPLEMENTARY_NT.put('g', 'c');
        COMPLEMENTARY_NT.put('T', 'A');
        COMPLEMENTARY_NT.put('t', 'a');
        COMPLEMENTARY_NT.put('N', 'N');
        COMPLEMENTARY_NT.put('n', 'n');
    }

    public static String reverseComplement(String string) {
        return reverseComplement(string, false);
    }

    public static String reverseComplement(String string, boolean failOnUnknownNt) {
        StringBuilder stringBuilder = new StringBuilder(string).reverse();
        for (int i = 0; i < stringBuilder.length(); i++) {
            char nextNt = stringBuilder.charAt(i);
            // Protection against weird characters, e.g. alternate:"TBS" found in ClinVar
            if (VariantAnnotationUtils.COMPLEMENTARY_NT.containsKey(nextNt)) {
                stringBuilder.setCharAt(i, VariantAnnotationUtils.COMPLEMENTARY_NT.get(nextNt));
            } else {
                if (failOnUnknownNt) {
                    throw new IllegalArgumentException("Unknown nucleotide: '" + nextNt+ "'. "
                            + "Unable to reverse-complement sequence '" + string + "'.");
                } else {
                    return null;
                }
            }
        }
        return stringBuilder.toString();
    }

    public static AlleleOrigin parseAlleleOrigin(String alleleOrigin) {
        return ORIGIN_STRING_TO_ALLELE_ORIGIN.get(alleleOrigin);
    }

}
