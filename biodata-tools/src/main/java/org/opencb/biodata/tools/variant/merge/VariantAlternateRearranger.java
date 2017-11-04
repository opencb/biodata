package org.opencb.biodata.tools.variant.merge;

import htsjdk.variant.vcf.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.tools.variant.converters.avro.VariantFileHeaderToVCFHeaderConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created on 16/06/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantAlternateRearranger {

    // Default ploidy as defined in vcf-spec
    public static final int DEFAULT_PLOIDY = 2;
    // Map from reordered allele position to original allele position
    private final int[] map;
    // Map from original allele position to reordered allele position
    private final int[] reverseMap;
    private final Configuration configuration;

    // Lazy initialization
    private int[] gMapP1; // Ploidy = 1
    private int[] gMapP2; // Ploidy = 2
    private Map<Integer, int[]> gMapP;  // Ploidy = key

    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    public <T> VariantAlternateRearranger(List<T> originalAlternates, List<T> reorderedAlternates) {
        this(originalAlternates, reorderedAlternates, null);
    }

    public <T> VariantAlternateRearranger(List<T> originalAlternates, List<T> reorderedAlternates,
                                          Configuration configuration) {
        this.configuration = configuration == null ? new Configuration() : configuration;
//        if (originalAlternates.size() > reorderedAlternates.size()) {
//            throw new IllegalArgumentException("Expected all original alternates in reordered alternates");
//        }

        this.map = new int[reorderedAlternates.size()];
        this.reverseMap = new int[originalAlternates.size()];
        for (int i = 0; i < reorderedAlternates.size(); i++) {
            map[i] = originalAlternates.indexOf(reorderedAlternates.get(i));
        }
        for (int i = 0; i < originalAlternates.size(); i++) {
            reverseMap[i] = reorderedAlternates.indexOf(originalAlternates.get(i));
//            if (reverseMap[i] < 0) {
//                throw new IllegalArgumentException("Missing alternate in list of reordered alternates");
//            }
        }
    }

    public Genotype rearrangeGenotype(String genotypeString, String reference, List<String> alternateAlleles) {
        return rearrangeGenotype(new Genotype(genotypeString, reference, alternateAlleles));
    }

    public Genotype rearrangeGenotype(Genotype genotype) {
        int[] allelesIdx = genotype.getAllelesIdx();
        for (int i = 0; i < allelesIdx.length; i++) {
            allelesIdx[i] = remapAlleleReverse(allelesIdx[i]);
        }
        return genotype;
    }

    public String rearrangeNumberR(String data) {
        return rearrangeNumberR(data, ".");
    }

    public String rearrangeNumberR(String data, String missingValue) {
        List<String> values = Arrays.asList(StringUtils.splitPreserveAllTokens(data, ','));
        return rearrange(values, missingValue, true, ",", map);
    }

    public <T> List<T> rearrangeNumberR(List<T> values, T missingValue) {
        return rearrange(values, missingValue, true, map);
    }

    public String rearrangeNumberA(String data) {
        return rearrangeNumberA(data, ".");
    }

    public String rearrangeNumberA(String data, String missingValue) {
        List<String> values = Arrays.asList(StringUtils.splitPreserveAllTokens(data, ','));
        return rearrange(values, missingValue, false, ",", map);
    }

    public <T> List<T> rearrangeNumberA(List<T> values, T missingValue) {
        return rearrange(values, missingValue, false, map);
    }

    public String rearrangeNumberG(String data, String missingValue, @Nullable Integer ploidy) {
        int[] gMap = getGenotypeReorderingMap(ploidy);
        List<String> values = Arrays.asList(StringUtils.splitPreserveAllTokens(data, ','));
        return rearrange(values, missingValue, false, ",", gMap);
    }

    public <T> List<T> rearrangeNumberG(List<T> values, T missingValue, @Nullable Integer ploidy) {
        int[] gMap = getGenotypeReorderingMap(ploidy);
        return rearrange(values, missingValue, false, gMap);
    }

    private <T> List<T> rearrange(List<T> originalValues, T missingValue, boolean includeReference, int[] map) {
        List<T> rearrangedValues = new ArrayList<>(includeReference ? this.map.length + 1 : this.map.length);
        rearrange(originalValues, missingValue, rearrangedValues::add, includeReference, map);
        return rearrangedValues;
    }

    private String rearrange(List<String> originalValues, String missingValue, boolean includeReference, String separator, int[] map) {
        StringBuilder sb = new StringBuilder();
        rearrange(originalValues, missingValue, str -> {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(str);
        }, includeReference, map);
        return sb.toString();
    }

    private <T> void rearrange(List<T> originalValues, T missingValue, Consumer<T> consumer, boolean includeReference, int[] map) {
        int expectedLength = map.length;
        if (includeReference) {
            expectedLength++;
        }

        if (originalValues.size() > expectedLength) {
            throw new IllegalArgumentException("Expected no more than " + expectedLength + " values. "
                    + "Got " + originalValues.size() + " : " + originalValues);
        }

        if (includeReference) {
            consumer.accept(originalValues.get(0));
        }

        for (int originalPosition : map) {
            if (originalPosition < 0) {
                consumer.accept(missingValue);
            } else {
                if (includeReference) {
                    originalPosition++;
                }
                if (originalPosition < originalValues.size()) {
                    consumer.accept(originalValues.get(originalPosition));
                } else {
                    consumer.accept(missingValue);
                }
            }
        }
    }

    private int[] getGenotypeReorderingMap(@Nullable Integer ploidy) {
        if (ploidy == null || ploidy <= 0) {
            // Ploidy 2 is assumed when missing.
            ploidy = DEFAULT_PLOIDY;
        }
        // Lazy initialization
        switch (ploidy) {
            case 1:
                if (gMapP1 == null) {
                    gMapP1 = getGenotypeReorderingMap(ploidy, map);
                }
                return gMapP1;
            case 2:
                if (gMapP2 == null) {
                    gMapP2 = getGenotypeReorderingMap(ploidy, map);
                }
                return gMapP2;
            default:
                if (gMapP == null) {
                    gMapP = new HashMap<>();
                }
                return gMapP.computeIfAbsent(ploidy, p -> getGenotypeReorderingMap(p, map));
        }
    }

    /**
     * Gets an array for reordering the positions of a format field with Number=G and ploidy=2.
     *
     * In those fields where there is a value per genotype, if we change the order of the alleles,
     * the order of the genotypes will also change.
     *
     * The genotypes ordering is defined in the Vcf spec : https://samtools.github.io/hts-specs/VCFv4.3.pdf as "Genotype Ordering"
     * Given a number of alleles N and a ploidy of P, the order algorithm is:
     *   for (a_p = 0; a_p < N: a_p++)
     *      for (a_p-1 = 0; a_p-1 <= a_p: a_p-1++)
     *          ...
     *              for (a_1 = 0; a_1 <= a_2: a_1++)
     *                  print(a_1, a_2, ..., a_p)
     *
     * i.e:
     *  N=2,P=2:  00,01,11,02,12,22
     *  N=3,P=2:  00,01,11,02,12,22,03,13,23,33
     *
     *  With P=2, given a genotype a/b, where a<b, its position is b(b+1)/2+a
     *
     *  For each genotype, map the alleles using the alleleMap, and calculate
     *  the position of the new mapped genotype in the original order.
     *
     * int posInReorderedList;
     * int posInOriginalList = map[posInReorderedList];
     *
     * @param ploidy    Num allele that defines the alleleMap.
     * @return          Map between the position in the new reordered list and the original one.
     */
    private static int[] getGenotypeReorderingMap(int ploidy, int[] map) {
        int[] gMap;
        if (ploidy == 1) {
            gMap = new int[map.length + 1];
            gMap[0] = 0;
            for (int i = 0; i < map.length; i++) {
                gMap[i + 1] = map[i] + 1;
            }
        } else if (ploidy == 2) {
            int l = map.length;
            gMap = new int[l * (l + 1) / 2 + l + 1];
            for (int i = 0; i < gMap.length; i++) {
                gMap[i] = -1;
            }
            int pos = 0;
            for (int a2 = 0; a2 <= map.length; a2++) {
                int newA2 = remapAlleleReverse(map, a2);
                if (newA2 < 0) {
                    continue;
                }
                for (int a1 = 0; a1 <= a2; a1++) {
                    int newA1 = remapAlleleReverse(map, a1);
                    if (newA1 < 0) {
                        continue;
                    }
                    // With P=2, given a genotype a/b, where a<b, its position is b(b+1)/2+a
                    if (newA2 > newA1) {
                        gMap[newA2 * (newA2 + 1) / 2 + newA1] = pos++;
                    } else {
                        gMap[newA1 * (newA1 + 1) / 2 + newA2] = pos++;
                    }
                }
            }
        } else {
            List<String> originalOrdering = getOrdering(ploidy, map.length, null);
            List<String> reorderedOrdering = getOrdering(ploidy, map.length, map);

            gMap = new int[originalOrdering.size()];

            for (int i = 0; i < reorderedOrdering.size(); i++) {
                gMap[i] = originalOrdering.indexOf(reorderedOrdering.get(i));
            }
        }
        return gMap;
    }

    private int remapAlleleReverse(int a) {
        return remapAllele(reverseMap, a);
    }

    private static int remapAlleleReverse(int[] map, int a) {
        // Map does not contain reference (0). Reference never changes
        if (a > 0) {
            // Decrement. Map keys does not contain reference
            a = ArrayUtils.indexOf(map, a - 1);
            if (a >= 0) {
                // Increment if not missing. Map values does not contain reference
                a++;
            }
        }
        return a;
    }

    private static int remapAllele(int[] map, int a) {
        // Map does not contain reference (0). Reference never changes
        if (a > 0) {
            // Decrement. Map keys does not contain reference
            a = map[a - 1];
            if (a >= 0) {
                // Increment if not missing. Map values does not contain reference
                a++;
            }
        }
        return a;
    }

    public static List<String> getOrdering(int p, int n, int[] map) {
        ArrayList<String> list = new ArrayList<>();
        getOrdering(p, n, list, new int[p], p - 1, map);
        return list;
    }

    /**
     * Recursive genotype ordering algorithm as seen in VCF-spec.
     *
     * @link https://samtools.github.io/hts-specs/VCFv4.3.pdf
     *
     * @param p     ploidy
     * @param n     Number of alternates
     * @param list  List where to store the genotypes
     * @param gt    Shared array
     * @param pos   Position where to write in the array
     * @param map   Map from reordered allele position to original allele position
     */
    public static void getOrdering(int p, int n, List<String> list, int[] gt, int pos, int[] map) {
        for (int a = 0; a <= n; a++) {
            if (map == null) {
                gt[pos] = a;
            } else {
                // Remap allele
                gt[pos] = remapAllele(map, a);
            }
            if (p == 1) {
                int prev = gt[0];
                int[] gtSorted = gt;
                // Check if sorted
                for (int g : gt) {
                    if (prev > g) {
                        // Sort if needed. Do not modify original array
                        gtSorted = Arrays.copyOf(gt, gt.length);
                        Arrays.sort(gtSorted);
                        break;
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (int g : gtSorted) {
                    sb.append(g);
                }
                list.add(sb.toString());
            } else {
                getOrdering(p - 1, a, list, gt, pos - 1, map);
            }
        }
    }

    public String rearrange(String key, String data) {
        return rearrange(key, data, null);
    }

    public String rearrange(String key, String data, @Nullable Integer ploidy) {
        if (StringUtils.isEmpty(data) || data.equals(".")) {
            // Do not rearrange missing values
            return data;
        }
        Pair<VCFHeaderLineType, VCFHeaderLineCount> pair =
                configuration.otherFieldsMap.getOrDefault(key, Pair.of(VCFHeaderLineType.String, VCFHeaderLineCount.UNBOUNDED));
        VCFHeaderLineType type = pair.getLeft();
        String missingValue = type.equals(VCFHeaderLineType.Float) || type.equals(VCFHeaderLineType.Integer) ? "0" : ".";
        switch (pair.getRight()) {
            case A:
                data = rearrangeNumberA(data, missingValue);
                break;
            case R:
                data = rearrangeNumberR(data, missingValue);
                break;
            case G:
                data = rearrangeNumberG(data, missingValue, ploidy);
                break;
            case INTEGER:
            case UNBOUNDED:
            default:
                // Do not rearrange other fields
        }
        return data;
    }

    public static class Configuration {
        private Map<String, Pair<VCFHeaderLineType, VCFHeaderLineCount>> otherFieldsMap;

        public Configuration() {
            otherFieldsMap = new HashMap<>();
            otherFieldsMap.put("AD", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.R));
            otherFieldsMap.put("ADF", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.R));
            otherFieldsMap.put("ADR", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.R));

            otherFieldsMap.put("AF", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.A));
            otherFieldsMap.put("AC", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.A));

            otherFieldsMap.put("GL", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.G));
            otherFieldsMap.put("GP", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.G));
            otherFieldsMap.put("PL", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.G));
            otherFieldsMap.put("CNL", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.G));
            otherFieldsMap.put("CNP", Pair.of(VCFHeaderLineType.Integer, VCFHeaderLineCount.G));
        }


        public Configuration configure(VCFHeader header) {
            configure(header.getInfoHeaderLines());
            configure(header.getFormatHeaderLines());
            return this;
        }

        public Configuration configure(Collection<? extends VCFHeaderLine> lines) {
            lines.stream()
                    .filter(VCFCompoundHeaderLine.class::isInstance)
                    .map(VCFCompoundHeaderLine.class::cast)
                    .forEach(this::configure);
            return this;
        }

        public Configuration configure(VCFCompoundHeaderLine line) {
            this.otherFieldsMap.put(line.getKey(), Pair.of(line.getType(), line.getCountType()));
            return this;
        }

        public Configuration configure(String key, VCFHeaderLineCount number, VCFHeaderLineType type) {
            this.otherFieldsMap.put(key, Pair.of(type, number));
            return this;
        }

        public void configure(VariantFileHeader header) {
            for (VariantFileHeaderComplexLine line : header.getComplexLines()) {
                if (line.getKey().equalsIgnoreCase("FORMAT") || line.getKey().equalsIgnoreCase("INFO")) {
                    VCFHeaderLineCount number = VariantFileHeaderToVCFHeaderConverter.getVCFHeaderLineCount(line);
                    VCFHeaderLineType type = VariantFileHeaderToVCFHeaderConverter.getVCFHeaderLineType(line);
                    configure(line.getId(), number, type);
                }
            }
        }
    }

}
