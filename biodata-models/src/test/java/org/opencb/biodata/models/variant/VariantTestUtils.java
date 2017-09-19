package org.opencb.biodata.models.variant;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created on 06/07/16
 *
 * @see VariantBuilder
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantTestUtils {
    public static final String STUDY_ID = "";
    public static final String FILE_ID = "";

    public static Variant generateVariant(String var, String... samplesData) {
        return generateVariantWithFormat(var, "GT", samplesData);
    }

    public static Variant generateVariantWithFormat(String var, String format, String... samplesDataArray) {
        return generateVariantWithFormat(var, "PASS", 100f, format, samplesDataArray);
    }

    public static Variant generateVariantWithFormat(String var, String filter, Float qual, String format, String... samplesDataArray) {
        return generateVariantWithFormat(var, filter, qual, Collections.emptyMap(), format, samplesDataArray);
    }

    public static Variant generateVariantWithFormat(String var, String filter, Float qual, Map<String, String> attributes, String format, String... samplesDataArray) {
        String qualStr;
        if (qual == null) {
            qualStr = ".";
        } else {
            qualStr = qual.toString();
            if (qualStr.endsWith(".0")) {
                qualStr = qualStr.substring(0, qualStr.lastIndexOf(".0"));
            }
        }

        String[] formats = format.split("[:,]");

        VariantBuilder variantBuilder = Variant.newBuilder(var)
                .setAttributes(attributes)
                .setQuality(qualStr)
                .setFilter(filter)
                .setFormat(formats)
                .setStudyId(STUDY_ID)
                .setFileId(FILE_ID);

        for (int i = 0; i < samplesDataArray.length; i = i + formats.length + 1) {
            String sampleName = samplesDataArray[i];
            List<String> sampleData = new LinkedList<>();
            for (int j = 0; j < formats.length; j++) {
                sampleData.add(samplesDataArray[i + j + 1]);
            }
            variantBuilder.addSample(sampleName, sampleData);
        }
        return variantBuilder.build();
    }

}
