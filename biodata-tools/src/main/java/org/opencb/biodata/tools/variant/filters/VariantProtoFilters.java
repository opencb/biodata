package org.opencb.biodata.tools.variant.filters;

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.protobuf.VariantProto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantProtoFilters extends VariantFilters<VariantProto.Variant> {

    private String datasetId;
    private String fileId;

    public VariantProtoFilters(String datasetId, String fileId) {
        super();
        this.datasetId = datasetId;
        this.fileId = fileId;
    }

    @Override
    public VariantFilters<VariantProto.Variant> addTypeFilter(String type) {
        filters.add(variant -> variant.getType().equals(type));
        return this;
    }

    @Override
    public VariantFilters<VariantProto.Variant> addSNPFilter() {
        filters.add(variant -> !variant.getId().equalsIgnoreCase(".")
                && !variant.getId().equalsIgnoreCase(""));
        return this;
    }

    @Override
    public VariantFilters<VariantProto.Variant> addQualFilter(double minQual) {
        throw new UnsupportedOperationException("Filter VariantProto.Variant by quality not supported yet!");
    }

    @Override
    public VariantFilters<VariantProto.Variant> addPassFilter() {
        return addPassFilter("PASS");
    }

    @Override
    public VariantFilters<VariantProto.Variant> addPassFilter(String name) {
        throw new UnsupportedOperationException("Filter VariantProto.Variant by PASS not supported yet!");
    }

    @Override
    public VariantFilters<VariantProto.Variant> addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Arrays.asList(region), contained);
    }

    @Override
    public VariantFilters<VariantProto.Variant> addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<VariantProto.Variant>> predicates = new ArrayList<>();
        for (Region region: regions) {
            if (contained) {
                predicates.add(variant -> variant.getChromosome().equals(region.getChromosome())
                        && variant.getStart() >= region.getStart()
                        && variant.getEnd() <= region.getEnd());
            } else {
                predicates.add(variant -> variant.getChromosome().equals(region.getChromosome())
                        && variant.getStart() <= region.getEnd()
                        && variant.getEnd() >= region.getStart());
            }
        }
        addFilterList(predicates);
        return this;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
