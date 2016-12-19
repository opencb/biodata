package org.opencb.biodata.tools.alignment.coverage;

import org.opencb.biodata.models.alignment.RegionCoverage;

import java.util.ArrayList;
import java.util.List;


public abstract class RegionCoverageCalculator<T> {

    protected int minBaseQuality;

    public RegionCoverageCalculator() {
        this(0);
    }

    public RegionCoverageCalculator(int minBaseQuality) {
        this.minBaseQuality = minBaseQuality;
    }

    public abstract void update(T alignment, RegionCoverage dest);

    public void update(RegionCoverage src, RegionCoverage dest) {
        if (!src.getChromosome().equals(dest.getChromosome())) {
            // nothing to do
            return;
        }

        int start = Math.max(src.getStart(), dest.getStart());
        int end = Math.min(src.getEnd(), dest.getEnd());

        for (int i = start ; i <= end; i++) {
            dest.getValues()[i - dest.getStart()] += src.getValues()[i - src.getStart()];
        }
    }

    public int getMinBaseQuality() {
        return minBaseQuality;
    }

    public void setMinBaseQuality(int minBaseQuality) {
        this.minBaseQuality = minBaseQuality;
    }
}
