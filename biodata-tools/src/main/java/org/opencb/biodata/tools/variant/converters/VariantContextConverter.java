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

package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.tools.Converter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jtarraga on 07/02/17.
 */
public abstract class VariantContextConverter<T> implements Converter<VariantContext, T> {

    public static final DecimalFormat DECIMAL_FORMAT_7 = new DecimalFormat("#.#######");
    public static final DecimalFormat DECIMAL_FORMAT_3 = new DecimalFormat("#.###");
    public static final String FIELD_SEPARATOR = "|";
    public static final String INFO_SEPARATOR = "&";

    protected String studyIdString;
    protected List<String> sampleNames;
    protected List<String> sampleFormats;
    protected List<String> annotations;

    protected Map<String, String> studyNameMap;
    protected Map<String, Integer> samplePositions;

    public VariantContextConverter(String study, List<String> sampleNames, List<String> sampleFormats, List<String> annotations) {
//        this.studyId = studyId;
        this.studyIdString = study;
        this.sampleNames = sampleNames;
        this.sampleFormats = sampleFormats;
        this.annotations = annotations;

        this.studyNameMap = new HashMap<>();
    }

    protected String buildAllele(String chromosome, Integer start, Integer end, String allele, Pair<Integer, Integer> adjustedRange) {
        if (start.equals(adjustedRange.getLeft()) && end.equals(adjustedRange.getRight())) {
            return allele; // same start / end
        }
        if (StringUtils.startsWith(allele, "*")) {
            return allele; // no need
        }
        return getReferenceBase(chromosome, adjustedRange.getLeft(), start) + allele
                + getReferenceBase(chromosome, end, adjustedRange.getRight());
    }

    /**
     * Get bases from reference sequence.
     * @param chromosome Chromosome.
     * @param from Start ( inclusive) position.
     * @param to End (exclusive) position.
     * @return String Reference sequence of length to - from.
     */
    private String getReferenceBase(String chromosome, Integer from, Integer to) {
        int length = to - from;
        if (length < 0) {
            throw new IllegalStateException(
                    "Sequence length is negative: chromosome " + chromosome + " from " + from + " to " + to);
        }
        return StringUtils.repeat('N', length); // current return default base TODO load reference sequence
    }

    protected String getIdForVcf(String id, List<String> names) {
        if (StringUtils.isNotEmpty(id) && !id.contains(":")) {
            StringBuilder ids = new StringBuilder(id);
            if (names != null) {
                for (String name : names) {
                    ids.append(VCFConstants.ID_FIELD_SEPARATOR).append(name);
                }
            }
            return ids.toString();
        } else {
            return VCFConstants.EMPTY_ID_FIELD;
        }
    }
}
