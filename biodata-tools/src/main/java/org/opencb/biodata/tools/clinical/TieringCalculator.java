package org.opencb.biodata.tools.clinical;

import org.apache.commons.collections.CollectionUtils;
import org.opencb.biodata.models.clinical.interpretation.ReportedVariant;

import java.util.List;

public abstract class TieringCalculator {

    public void setTier(List<ReportedVariant> reportedVariants) {
        if (CollectionUtils.isNotEmpty(reportedVariants)) {
            for (ReportedVariant reportedVariant : reportedVariants) {
                setTier(reportedVariant);
            }
        }
    }

    public abstract void setTier(ReportedVariant reportedVariant);
}
