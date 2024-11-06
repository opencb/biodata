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

import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.tools.commons.Converter;
import org.opencb.commons.datastore.core.ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by jtarraga on 07/02/17.
 */
public abstract class VariantContextConverter<T> implements Converter<T, VariantContext> {
    private final Logger logger = LoggerFactory.getLogger(VariantContextConverter.class);

    public static final DecimalFormat DECIMAL_FORMAT_7 = new DecimalFormat("#.#######");
    public static final DecimalFormat DECIMAL_FORMAT_3 = new DecimalFormat("#.###");
    public static final String FIELD_SEPARATOR = "|";
    public static final String INFO_SEPARATOR = "&";
    public static final String NO_CALL_ALLELE = String.valueOf(VCFConstants.NO_CALL_ALLELE);

    protected final String studyIdString;
    protected final List<String> sampleNames;
    protected final List<String> sampleFormats;
    protected final Map<String, Integer> samplePositions;
    protected final List<String> annotations;
    protected Map<String, String> studyNameMap;


    public VariantContextConverter(String study, List<String> sampleNames, List<String> sampleFormats, List<String> annotations) {
        this.studyIdString = study;
        this.sampleNames = sampleNames;
        // If samples format is NULL, all given formats will be shown
        this.sampleFormats = sampleFormats;
        this.annotations = annotations;

        this.studyNameMap = null;

        if (this.sampleNames != null) {
            samplePositions = new HashMap<>(sampleNames.size());
            for (int i = 0; i < sampleNames.size(); i++) {
                samplePositions.put(sampleNames.get(i), i);
            }
        } else {
            samplePositions = Collections.emptyMap();
        }
    }

    protected abstract List<String> buildAlleles(T variant, Pair<Integer, Integer> adjustedRange, Map<Integer, Character> referenceAlleles);

    protected static String buildAllele(String chromosome, Integer start, Integer end, String allele, Pair<Integer, Integer> adjustedRange, Map<Integer, Character> referenceAlleles) {
        if (start.equals(adjustedRange.getLeft()) && end.equals(adjustedRange.getRight())) {
            return allele; // same start / end
        }
        if (StringUtils.startsWith(allele, "*") || StringUtils.startsWith(allele, "<")) {
            return allele; // no need for overlapping deletions and symbolic alleles
        }
        if (StringUtils.startsWithAny(allele, "]", "[")) {
            if (allele.endsWith(".")) {
                allele = allele.substring(0, allele.length() - 1);
            }
            return allele + getReferenceBase(chromosome, adjustedRange.getLeft(), adjustedRange.getLeft() + 1, referenceAlleles);
        }
        if (StringUtils.endsWithAny(allele, "]", "[")) {
            if (allele.startsWith(".")) {
                allele = allele.substring(1);
            }
            return getReferenceBase(chromosome, adjustedRange.getLeft(), start, referenceAlleles) + allele;
        }
        return getReferenceBase(chromosome, adjustedRange.getLeft(), start, referenceAlleles)
                + allele
                + getReferenceBase(chromosome, end + 1, adjustedRange.getRight() + 1, referenceAlleles);
    }

    protected static MutablePair<Integer, Integer> adjustedVariantStart(
            final int start, final int end, String reference, String alternate,
            Map<Integer, Character> referenceAlleles, MutablePair<Integer, Integer> adjustedPosition) {
        int adjustedStart;
        int adjustedEnd;

        if (adjustedPosition == null) {
            adjustedStart = start;
            adjustedEnd = end;
        } else {
            adjustedStart = Math.min(start, adjustedPosition.getLeft());
            adjustedEnd = Math.max(end, adjustedPosition.getRight());
        }

        // Add a context base if reference or alternate are empty,
        if (StringUtils.isBlank(reference) || StringUtils.isBlank(alternate)) {
            // Add the context base at the beginning or at the end, depending if it's contained in the referenceAlleles map.
            // If none is present, add from the start

            // Do not add a context base if the adjusted position already includes that position.
            if (start - 1 < adjustedStart && end + 1 > adjustedEnd) {
                if (referenceAlleles.containsKey(start - 1) || !referenceAlleles.containsKey(end + 1)) {
                    adjustedStart = start - 1;
                } else {
                    adjustedEnd = end + 1;
                }
            }
        }
        adjustedEnd = Math.max(adjustedEnd, adjustedStart);
        if (adjustedPosition == null) {
            adjustedPosition = new MutablePair<>(adjustedStart, adjustedEnd);
        } else {
            adjustedPosition.setLeft(adjustedStart);
            adjustedPosition.setRight(adjustedEnd);
        }
        return adjustedPosition;
    }

    /**
     * Get bases from reference sequence.
     *
     * @param chromosome        Chromosome
     * @param from              Start ( inclusive) position
     * @param to                End (exclusive) position
     * @param referenceAlleles  Reference alleles
     * @return String Reference sequence of length to - from
     */
    protected static String getReferenceBase(String chromosome, int from, int to, Map<Integer, Character> referenceAlleles) {
        int length = to - from;
        if (length < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = from; i < to; i++) {
            sb.append(referenceAlleles.getOrDefault(i, 'N'));
        }
        return sb.toString();
    //        return StringUtils.repeat('N', length); // current return default base TODO load reference sequence
    }

    protected void init(T variant) {
        if (this.studyNameMap == null) {
            studyNameMap = new HashMap<>();
            getStudiesId(variant).forEachRemaining(studyId -> {
                this.studyNameMap.put(studyId, studyId);
                if (studyId.contains("@")) {
                    this.studyNameMap.put(studyId.split("@")[1], studyId);
                }
                if (studyId.contains(":")) {
                    this.studyNameMap.put(studyId.split(":")[1], studyId);
                }
            });
        }
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

    protected Set<Integer> getNoCallAlleleIdx(List<String> alleleList) {
        Set<Integer> nocallAlleles = new HashSet<>();
        for (int i = 0; i < alleleList.size(); i++) {
            String anObject = alleleList.get(i);
            if (NO_CALL_ALLELE.equals(anObject)) {
                nocallAlleles.add(i);
            }
        }
        return nocallAlleles;
    }

    protected static Map<Integer, Character> buildReferenceAllelesMap(Iterator<String> callsIterator) {
        Map<Integer, Character> referenceAlleles = new HashMap<>();
        callsIterator.forEachRemaining(call -> {
            if (call != null) {
                Variant originalVariant = new Variant(call.split(",")[0]);
                String originalReference = originalVariant.getReference();
                Integer originalPosition = originalVariant.getStart();
                for (int i = 0; i < originalReference.length(); i++) {
                    referenceAlleles.put(originalPosition + i, originalReference.charAt(i));
                }
            }
        });
        return referenceAlleles;
    }

    protected Set<String> getFilters(List<Map<String, String>> fileAttributes) {
        Set<String> filters;
        Set<String> sourceFilter = fileAttributes
                .stream()
                .map(attributes -> attributes.get(StudyEntry.FILTER))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
        if (sourceFilter.size() == 1) {
            String filter = sourceFilter.iterator().next();
            filters = new HashSet<>(Arrays.asList(filter.split(VCFConstants.FILTER_CODE_SEPARATOR)));
        } else {
            // If there are more than one different filters, or none, write missing value
            // TODO: join attributes from different file entries? what to do on a collision?
            filters = Collections.singleton(VCFConstants.MISSING_VALUE_v4);
        }
        return filters;
    }

    protected double getQuality(List<Map<String, String>> fileAttributes) {
        // TODO: get quality from FileEntries
        // By default, take the minimum 'valid' QUAL
        double qual = Double.MAX_VALUE; //VariantContext.NO_LOG10_PERROR;
        if (fileAttributes != null && !fileAttributes.isEmpty()) {
            for (Map<String, String> attrs: fileAttributes) {
                if (attrs.containsKey(StudyEntry.QUAL)) {
                    try {
                        // Take the minimum 'valid' QUAL
                        if (qual > Double.parseDouble(attrs.get(StudyEntry.QUAL))) {
                            qual = Double.parseDouble(attrs.get(StudyEntry.QUAL));
                        }
                    } catch (NumberFormatException e) {
                        // Nothing to do
                        logger.warn("Invalid QUAL value found: " + attrs.get(StudyEntry.QUAL));
                    }
                }
            }
        }
        return (qual == Double.MAX_VALUE ? VariantContext.NO_LOG10_PERROR : (-0.1 * qual));
    }

    protected List<Genotype> getGenotypes(List<String> alleleList, List<String> sampleDataKeys, BiFunction<String, String, String> getSampleData, Set<Integer> discardedAlleles) {
        String refAllele = alleleList.get(0);
        Set<Integer> noCallAlleles = getNoCallAlleleIdx(alleleList);

        List<Genotype> genotypes = new ArrayList<>();
        if (this.sampleNames != null) {

            if (this.sampleFormats != null) {
                sampleDataKeys = this.sampleFormats;
            }

            samplesLoop: for (String sampleName : this.sampleNames) {
                GenotypeBuilder genotypeBuilder = new GenotypeBuilder().name(sampleName);
                for (String key : sampleDataKeys) {
                    String value = getSampleData.apply(sampleName, key);
                    switch (key) {
                        case "GT":
                            if (value == null || value.equals("?/?") || value.equals("NA")) {
                                value = NO_CALL_ALLELE;
                            }
                            org.opencb.biodata.models.variant.Genotype genotype =
                                    new org.opencb.biodata.models.variant.Genotype(value, refAllele, alleleList.subList(1, alleleList.size()));
                            List<Allele> alleles = new ArrayList<>();
                            for (int gtIdx : genotype.getAllelesIdx()) {
                                if (gtIdx < alleleList.size() && gtIdx >= 0 && !noCallAlleles.contains(gtIdx)) { // .. AND NOT a nocall allele
                                    String alternate = alleleList.get(gtIdx);
                                    if (discardedAlleles.contains(gtIdx)) {
                                        // If this genotype contains a duplicated allele, and it is not the first occurrence, skip it
                                        logger.warn("Skipping allele '" + alleleToString(alternate) + "' for sample '" + sampleName + "'");
                                        genotypes.add(new GenotypeBuilder().name(sampleName)
                                                .alleles(Arrays.asList(
                                                        Allele.create(NO_CALL_ALLELE, false),
                                                        Allele.create(NO_CALL_ALLELE, false)
                                                )).make()
                                        );
                                        // skip the rest of the sample data
                                        continue samplesLoop;
                                    }
                                    alleles.add(Allele.create(alternate, gtIdx == 0)); // allele is ref. if the alleleIndex is 0
                                } else {
                                    alleles.add(Allele.create(NO_CALL_ALLELE, false)); // genotype of a secondary alternate, or an actual missing
                                }
                            }
                            genotypeBuilder.alleles(alleles).phased(genotype.isPhased());
                            break;
                        case "AD":
                            if (StringUtils.isNotEmpty(value) && !value.equals(".")) {
                                int[] ad = getInts(value);
                                genotypeBuilder.AD(ad);
                            } else {
                                genotypeBuilder.noAD();
                            }
                            break;
                        case "DP":
                            if (StringUtils.isNotEmpty(value) && !value.equals(".")) {
                                genotypeBuilder.DP(Integer.parseInt(value));
                            } else {
                                genotypeBuilder.noDP();
                            }
                            break;
                        case "GQ":
                            if (StringUtils.isNotEmpty(value) && !value.equals(".")) {
                                genotypeBuilder.GQ(Integer.parseInt(value));
                            } else {
                                genotypeBuilder.noGQ();
                            }
                            break;
                        case "PL":
                            if (StringUtils.isNotEmpty(value) && !value.equals(".")) {
                                int[] pl = getInts(value);
                                genotypeBuilder.PL(pl);
                            } else {
                                genotypeBuilder.noPL();
                            }
                            break;
                        default:
                            genotypeBuilder.attribute(key, value);
                            break;
                    }
                }
                genotypes.add(genotypeBuilder.make());
            }
        }
        return genotypes;
    }

    private int[] getInts(String value) {
        String[] split = value.split(",");
        int[] ints = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            try {
                ints[i] = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                ints[i] = 0;
            }
        }
        return ints;
    }

    protected VariantContext makeVariantContext(String chromosome, int start, int end, String idForVcf, List<String> alleleList,
                                                boolean isNoVariation, Set<String> filters, double qual, ObjectMap attributes,
                                                List<Genotype> genotypes, Set<Integer> discardedAlleles) {
        String refAllele = alleleList.get(0);
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder()
                .chr(chromosome)
                .start(start)
                .stop(end)
//                .stop(start + refAllele.length() - 1) //TODO mh719: check what happens for Insertions
                .log10PError(qual)
                .filters(filters);


        if (isNoVariation && alleleList.get(1).isEmpty()) {
            variantContextBuilder.alleles(refAllele);
        } else {
            List<String> finalAlleles = new ArrayList<>(alleleList);
            for (Integer i : discardedAlleles) {
                finalAlleles.set(i, null);
            }
            finalAlleles = finalAlleles.stream()
                    .filter(Objects::nonNull)
                    .filter(a -> !a.equals(NO_CALL_ALLELE))
                    .collect(Collectors.toList());
            variantContextBuilder.alleles(finalAlleles);
        }

        if (genotypes.isEmpty()) {
            variantContextBuilder.noGenotypes();
        } else {
            variantContextBuilder.genotypes(genotypes);
        }

        if (isSymbolic(alleleList.get(1))) {
            attributes.append(VCFConstants.END_KEY, end);
        }
        variantContextBuilder.attributes(attributes);

        variantContextBuilder.id(idForVcf);

        try {
            return variantContextBuilder.make();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Error creating VariantContext: " + chromosome + ":" + start + "-" + end + ":" + alleleList, e);
        }
    }

    /**
     * Check if the allele is a symbolic allele other than <NON_REF> or <*>
     * @param allele   Allele
     * @return True if the allele is a symbolic allele
     */
    protected static boolean isSymbolic(String allele) {
        return allele.startsWith("<") && allele.endsWith(">") && !isNonRef(allele);
    }

    protected static boolean isNonRef(String allele) {
        return allele.equals(Allele.NON_REF_STRING) || allele.equals(Allele.UNSPECIFIED_ALTERNATE_ALLELE_STRING);
    }

    protected Set<String> getDuplicatedAlleles(List<String> alleleList) {
        Set<String> duplicatedAlleles;
        if (alleleList.size() > 2 && new HashSet<>(alleleList).size() != alleleList.size()) {
            Set<String> allelesSet = new HashSet<>();

            duplicatedAlleles = new HashSet<>();
            for (String allele : alleleList) {
                if (!allelesSet.add(allele)) {
                    duplicatedAlleles.add(allele);
                }
            }
        } else {
            duplicatedAlleles = Collections.emptySet();
        }
        return duplicatedAlleles;
    }

    protected abstract Object getStudy(T variant);

    protected abstract Iterator<String> getStudiesId(T variant);

    protected static String allelesToString(Collection<String> alleles) {
        return alleles.stream()
                .map(VariantContextConverter::alleleToString)
                .collect(Collectors.joining(","));
    }

    private static String alleleToString(String a) {
        return a.length() > 10 ? (a.substring(0, 10) + "...[" + a.length() + "]") : a;
    }

}
