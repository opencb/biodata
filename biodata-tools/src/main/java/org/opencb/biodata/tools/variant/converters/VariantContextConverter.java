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
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.tools.Converter;
import org.opencb.commons.datastore.core.ObjectMap;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by jtarraga on 07/02/17.
 */
public abstract class VariantContextConverter<T> implements Converter<VariantContext, T> {

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

    protected String buildAllele(String chromosome, Integer start, Integer end, String allele, Pair<Integer, Integer> adjustedRange, Map<Integer, Character> referenceAlleles) {
        if (start.equals(adjustedRange.getLeft()) && end.equals(adjustedRange.getRight())) {
            return allele; // same start / end
        }
        if (StringUtils.startsWith(allele, "*")) {
            return allele; // no need
        }
        return getReferenceBase(chromosome, adjustedRange.getLeft(), start, referenceAlleles)
                + allele
                + getReferenceBase(chromosome, end, adjustedRange.getRight(), referenceAlleles);
    }

    /**
     * Get bases from reference sequence.
     * @param chromosome Chromosome.
     * @param from Start ( inclusive) position.
     * @param to End (exclusive) position.
     * @param referenceAlleles
     * @return String Reference sequence of length to - from.
     */
    private String getReferenceBase(String chromosome, int from, int to, Map<Integer, Character> referenceAlleles) {
        int length = to - from;
        if (length < 0) {
            throw new IllegalStateException(
                    "Sequence length is negative: chromosome " + chromosome + " from " + from + " to " + to);
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

    protected Map<Integer, Character> buildReferenceAllelesMap(Iterator<String> callsIterator) {
        Map<Integer, Character> referenceAlleles = new HashMap<>();
        callsIterator.forEachRemaining(call -> {
            if (StringUtils.isNotEmpty(call)) {
                String[] split = StringUtils.split(call, ':');
                String originalReference = VariantContextConverter.getOriginalReference(split);
                Integer originalPosition = VariantContextConverter.getOriginalPosition(split);
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
                        e.getMessage();
                    }
                }
            }
        }
        return (qual == Double.MAX_VALUE ? VariantContext.NO_LOG10_PERROR : (-0.1 * qual));
    }

    protected List<Genotype> getGenotypes(List<String> alleleList, List<String> variantFormats, BiFunction<String, String, String> getSampleData) {
        String refAllele = alleleList.get(0);
        Set<Integer> noCallAlleles = getNoCallAlleleIdx(alleleList);

        List<Genotype> genotypes = new ArrayList<>();
        if (this.sampleNames != null) {

            List<String> formats;
            if (this.sampleFormats != null) {
                formats = this.sampleFormats;
            } else {
                formats = variantFormats;
            }

            for (String sampleName : this.sampleNames) {
                GenotypeBuilder genotypeBuilder = new GenotypeBuilder().name(sampleName);
                for (String id : formats) {
                    String value = getSampleData.apply(sampleName, id);
                    switch (id) {
                        case "GT":
                            if (value == null) {
                                value = NO_CALL_ALLELE;
                            }
                            org.opencb.biodata.models.feature.Genotype genotype =
                                    new org.opencb.biodata.models.feature.Genotype(value, refAllele, alleleList.subList(1, alleleList.size()));
                            List<Allele> alleles = new ArrayList<>();
                            for (int gtIdx : genotype.getAllelesIdx()) {
                                if (gtIdx < alleleList.size() && gtIdx >= 0 && !noCallAlleles.contains(gtIdx)) { // .. AND NOT a nocall allele
                                    alleles.add(Allele.create(alleleList.get(gtIdx), gtIdx == 0)); // allele is ref. if the alleleIndex is 0
                                } else {
                                    alleles.add(Allele.create(NO_CALL_ALLELE, false)); // genotype of a secondary alternate, or an actual missing
                                }
                            }
                            genotypeBuilder.alleles(alleles).phased(genotype.isPhased());
                            break;
                        case "AD":
                            if (StringUtils.isNotEmpty(value) && !value.contains(".")) {
                                String[] split = value.split(",");
                                genotypeBuilder.AD(new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])});
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
                            if (StringUtils.isNotEmpty(value) && !value.contains(".")) {
                                String[] split = value.split(",");
                                genotypeBuilder.PL(new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])});
                            } else {
                                genotypeBuilder.noPL();
                            }
                            break;
                        default:
                            genotypeBuilder.attribute(id, value);
                            break;
                    }
                }

                genotypes.add(genotypeBuilder.make());

//            String gtStr = studyEntry.getSampleData(sampleName, "GT");
//            String genotypeFilter = studyEntry.getSampleData(sampleName, "FT");
//
//            if (Objects.isNull(gtStr)) {
//                gtStr = noCallAllele;
//                genotypeFilter = noCallAllele;
//            }
//
//            List<String> gtSplit = new ArrayList<>(Arrays.asList(gtStr.split(",")));
//            List<String> ftSplit = new ArrayList<>(Arrays.asList(
//                    (StringUtils.isBlank(genotypeFilter) ? "" : genotypeFilter).split(",")));
//            while (gtSplit.size() > 1) {
//                int idx = gtSplit.indexOf(noCallAllele);
//                if (idx < 0) {
//                    idx = gtSplit.indexOf("0/0");
//                }
//                if (idx < 0) {
//                    break;
//                }
//                gtSplit.remove(idx);
//                ftSplit.remove(idx);
//            }
//            String gt = gtSplit.get(0);
//            String ft = ftSplit.get(0);
//
//            org.opencb.biodata.models.feature.Genotype genotype =
//                    new org.opencb.biodata.models.feature.Genotype(gt, refAllele, alleleList.subList(1, alleleList.size()));
//            List<Allele> alleles = new ArrayList<>();
//            for (int gtIdx : genotype.getAllelesIdx()) {
//                if (gtIdx < alleleList.size() && gtIdx >= 0 && !nocallAlleles.contains(gtIdx)) { // .. AND NOT a nocall allele
//                    alleles.add(Allele.create(alleleList.get(gtIdx), gtIdx == 0)); // allele is ref. if the alleleIndex is 0
//                } else {
//                    alleles.add(Allele.create(noCallAllele, false)); // genotype of a secondary alternate, or an actual missing
//                }
//            }
//
//            if (StringUtils.isBlank(ft)) {
//                genotypeFilter = null;
//            } else if (StringUtils.equals("PASS", ft)) {
//                genotypeFilter = "1";
//            } else {
//                genotypeFilter = "0";
//            }
////            GenotypeBuilder genotypeBuilder = new GenotypeBuilder()
////                    .name(this.sampleNameMapping.get(sampleName));
//            GenotypeBuilder genotypeBuilder = new GenotypeBuilder().name(sampleName);
//            if (studyEntry.getFormatPositions().containsKey("GT")) {
//                genotypeBuilder.alleles(alleles).phased(genotype.isPhased());
//            }
//            if (genotypeFilter != null) {
//                genotypeBuilder.attribute("PF", genotypeFilter);
//            }
//
//            System.out.println("studyEntry.getFormat() = " + studyEntry.getFormat());
//            for (String id : studyEntry.getFormat()) {
//                if (id.equals("GT") || id.equals("FT")) {
//                    continue;
//                }
//                String value = studyEntry.getSampleData(sampleName, id);
//                genotypeBuilder.attribute(id, value);
//            }
//
//            genotypes.add(genotypeBuilder.make());
            }

        }
        return genotypes;
    }

    protected VariantContext makeVariantContext(String chromosome, int start, String idForVcf, List<String> alleleList, boolean isNoVariation, Set<String> filters, double qual, ObjectMap attributes, List<Genotype> genotypes) {
        String refAllele = alleleList.get(0);
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder()
                .chr(chromosome)
                .start(start)
                .stop(start + refAllele.length() - 1) //TODO mh719: check what happens for Insertions
                .log10PError(qual)
                .filters(filters);


        if (isNoVariation && alleleList.get(1).isEmpty()) {
            variantContextBuilder.alleles(refAllele);
        } else {
            variantContextBuilder.alleles(alleleList.stream().filter(a -> !a.equals(NO_CALL_ALLELE)).collect(Collectors.toList()));
        }

        if (genotypes.isEmpty()) {
            variantContextBuilder.noGenotypes();
        } else {
            variantContextBuilder.genotypes(genotypes);
        }


        variantContextBuilder.attributes(attributes);

        variantContextBuilder.id(idForVcf);

        return variantContextBuilder.make();
    }

    /**
     * Assumes that ori is in the form "POS:REF:ALT_0(,ALT_N)*:ALT_IDX".
     * ALT_N is the n-th allele if this is the n-th variant resultant of a multiallelic vcf row
     *
     * @param ori
     * @return
     */
    protected static List<String> getOriginalAlleles(String[] ori) {
        if (ori != null && ori.length == 4) {
            String[] multiAllele = ori[2].split(",");
            if (multiAllele.length != 1) {
                ArrayList<String> alleles = new ArrayList<>(multiAllele.length + 1);
                alleles.add(ori[1]);
                alleles.addAll(Arrays.asList(multiAllele));
                return alleles;
            } else {
                return Arrays.asList(ori[1], ori[2]);
            }
        }

        return null;
    }

    protected static String getOriginalReference(String[] ori) {
        if (ori != null && ori.length == 4) {
            return ori[1];
        }
        return null;
    }

    protected static String getOriginalAlleleIndex(String[] ori) {
        if (ori != null && ori.length == 4) {
            return ori[3];
        }
        return null;
    }

    /**
     * Assumes that ori is in the form "POS:REF:ALT_0(,ALT_N)*:ALT_IDX".
     *
     * @param ori
     * @return
     */
    protected static Integer getOriginalPosition(String[] ori) {

        if (ori != null && ori.length == 4) {
            return Integer.parseInt(ori[0]);
        }

        return null;
    }

    protected abstract Object getStudy(T variant);

    protected abstract Iterator<String> getStudiesId(T variant);
}
