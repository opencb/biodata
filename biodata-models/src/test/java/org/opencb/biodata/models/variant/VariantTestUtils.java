package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 06/07/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantTestUtils {
    public static final String STUDY_ID = "";

    public static Variant generateVariant(String chr, int pos, String ref, String alt, VariantType vt) {
        return generateVariant(chr, pos, ref, alt, vt, Collections.emptyList(),Collections.emptyList());
    }

    public static Variant generateVariant(String chr, int pos, String ref, String alt, VariantType vt,
                                          List<String> sampleIds, List<String> sampleGt) {
        return generateVariant(new Variant(chr, pos, pos + Math.max(ref.length(), alt.length()) - 1, ref, alt), vt, Collections.singletonList("GT"), sampleIds, sampleGt.stream().map(s -> Collections.singletonList(s))
                .collect(Collectors.toList()), Collections.emptyMap());
    }

    public static Variant generateVariantWithFormat(String var, String format, String... samplesDataArray) {
        return generateVariantWithFormat(var, "PASS", 100f, format, samplesDataArray);
    }

    public static Variant generateVariantWithFormat(String var, String filter, Float qual, String format, String... samplesDataArray) {
        return generateVariantWithFormat(var, filter, qual, Collections.emptyMap(), format, samplesDataArray);
    }

    public static Variant generateVariantWithFormat(String var, String filter, Float qual, Map<String, String> attributes, String format, String... samplesDataArray) {

        List<String> secAlts = Collections.emptyList();
        if (var.contains(",")) {
            String[] split = var.split(",");
            var = split[0];
            secAlts = Arrays.asList(split).subList(1, split.length);
        }
        Variant variant = new Variant(var);
        variant.setIds(Collections.emptyList());
        variant.setStrand("+");
        variant.setHgvs(new HashMap<>());
//        variant.resetHGVS();

        attributes = new HashMap<>(attributes);
        attributes.put(VariantVcfFactory.FILTER, filter);
        String qualStr;
        if (qual == null) {
            qualStr = ".";
        } else {
            qualStr = qual.toString();
            if (qualStr.endsWith(".0")) {
                qualStr = qualStr.substring(0, qualStr.lastIndexOf(".0"));
            }
        }
        attributes.put(VariantVcfFactory.QUAL, qualStr);

        List<List<String>> samplesData = new LinkedList<>();
        String[] formats = format.split(":");
        List<String> samplesName = new LinkedList<>();
        for (int i = 0; i < samplesDataArray.length; i = i + formats.length + 1) {
            String sampleName = samplesDataArray[i];
            samplesName.add(sampleName);
            List<String> sampleData = new LinkedList<>();
            for (int j = 0; j < formats.length; j++) {
                sampleData.add(samplesDataArray[i + j + 1]);
            }
            samplesData.add(sampleData);
        }
        variant = generateVariant(variant, variant.getType(), Arrays.asList(formats), samplesName, samplesData, attributes);

        if (!secAlts.isEmpty()) {
            ArrayList<AlternateCoordinate> secondaryAlternates = new ArrayList<>(secAlts.size());
            for (String secAlt : secAlts) {
                secondaryAlternates.add(new AlternateCoordinate(variant.getChromosome(), variant.getStart(), variant.getEnd(),
                        variant.getReference(), secAlt, variant.getType()));
            }
            variant.getStudies().get(0).setSecondaryAlternates(secondaryAlternates);
//            VariantNormalizer normalizer = new VariantNormalizer();
//            try {
//                variant = normalizer.normalize(Collections.singletonList(variant), true).get(0);
//            } catch (NonStandardCompliantSampleField e) {
//                throw new RuntimeException(e);
//            }
        }
        return variant;
    }

    public static Variant generateVariant(String var, String... samplesData) {
        Variant variant = new Variant(var);
        List<String> sampleIds = new ArrayList<>(samplesData.length / 2);
        List<String> sampleGt = new ArrayList<>(samplesData.length / 2);
        for (int i = 0; i < samplesData.length; i = i + 2) {
            sampleIds.add(samplesData[i]);
            sampleGt.add(samplesData[i+1]);
        }
        return generateVariant(variant, variant.getType(), Collections.singletonList("GT"), sampleIds, sampleGt.stream().map(s -> Collections.singletonList(s))
                .collect(Collectors.toList()), Collections.emptyMap());
    }

    public static Variant generateVariant(Variant variant, VariantType vt,
                                          List<String> format, List<String> sampleIds, List<List<String>> samplesData, Map<String, String> fileAttributes) {
        Variant var = variant;
        var.setType(vt);
        StudyEntry se = new StudyEntry(STUDY_ID);
        se.setFiles(Collections.singletonList(new FileEntry("", "", fileAttributes)));
        se.setFormat(format);
        Map<String, Integer> sp = new HashMap<String, Integer>();
        for(int i = 0; i < sampleIds.size(); ++i){
            sp.put(sampleIds.get(i), i);
        }
        se.setSamplesPosition(sp);
        se.setSamplesData(samplesData);
        var.addStudyEntry(se );
        return var;
    }
}
