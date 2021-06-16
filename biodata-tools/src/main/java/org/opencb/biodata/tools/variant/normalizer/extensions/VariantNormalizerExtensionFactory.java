package org.opencb.biodata.tools.variant.normalizer.extensions;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.commons.run.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VariantNormalizerExtensionFactory {

    private final Logger logger = LoggerFactory.getLogger(VariantNormalizerExtensionFactory.class);

    public static final Set<String> ALL_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "FILE_DP_TO_SAMPLE",
            "SAMPLE_DP_TO_FORMAT",
            "VAF",
            "SV",
            "CUSTOM"
    )));
    private final Set<String> enabledExtensions;

    public VariantNormalizerExtensionFactory() {
        this(ALL_EXTENSIONS);
    }

    public VariantNormalizerExtensionFactory(Set<String> enabledExtensions) {
        this.enabledExtensions = enabledExtensions;
    }

    public Task<Variant, Variant> buildExtensions(VariantFileMetadata fileMetadata) {
        Task<Variant, Variant> extensionTask = null;
        for (String normalizerExtension : enabledExtensions) {
            VariantNormalizerExtension extension;
            switch (normalizerExtension) {
                case "FILE_DP_TO_SAMPLE":
                    extension = new VariantNormalizerExtensionFileToSample("DP");
                    break;
                case "FILE_AD_TO_SAMPLE_DP":
                    extension = new VariantNormalizerExtensionFileToSample("AD", "DP",
                            new VariantFileHeaderComplexLine("FORMAT", "DP", "", "1", "Integer", Collections.emptyMap()),
                            ad -> {
                                String[] split = ad.split(",");
                                int dp = 0;
                                for (String s : split) {
                                    dp += Integer.parseInt(s);
                                }
                                return String.valueOf(dp);
                            });
                    break;
                case "VAF":
                    extension = new VafVariantNormalizerExtension();
                    break;
                case "SV":
                    extension = new SvVariantNormalizerExtension();
                    break;
                case "CUSTOM":
                    extension = new CustomNormalizerExtension();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown normalizer extension " + normalizerExtension);
            }

            // Init the extension
            extension.init(fileMetadata);
            // Check is the extension can be applied
            if (extension.canUseExtension(fileMetadata)) {
                logger.info("Using VariantNormalizerExtension : " + normalizerExtension);
                if (extensionTask == null) {
                    extensionTask = extension;
                } else {
                    extensionTask = extensionTask.then(extension);
                }
            }
        }
        return extensionTask;
    }

}
