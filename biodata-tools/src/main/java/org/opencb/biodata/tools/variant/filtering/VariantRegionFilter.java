package org.opencb.biodata.tools.variant.filtering;


import java.util.ArrayList;
import java.util.List;
import org.opencb.biodata.models.feature.Region;
import org.opencb.biodata.models.variant.Variant;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantRegionFilter extends VariantFilter {

    private List<Region> regionList;

    public VariantRegionFilter(String chromosome, int start, int end) {
        super();
        regionList = new ArrayList<>();
        regionList.add(new Region(chromosome, start, end));
    }

    public VariantRegionFilter(String chromosome, int start, int end, int priority) {
        super(priority);
        regionList = new ArrayList<>();
        regionList.add(new Region(chromosome, start, end));

    }

    public VariantRegionFilter(String regions) {
        super();
        regionList = new ArrayList<>();

        String[] splits = regions.split(",");
        for (String split : splits) {
            regionList.add(new Region(split));
        }
    }

    public VariantRegionFilter(String regions, int priority) {
        super(priority);
        regionList = new ArrayList<>();

        String[] splits = regions.split(",");
        for (String split : splits) {
            regionList.add(new Region(split));
        }
    }

    @Override
    public boolean apply(Variant variant) {
        for (Region r : regionList) {
            if (r.contains(variant.getChromosome(), variant.getStart())) {
                return true;
            }
        }
        return false;
    }
}
