package org.opencb.biodata.models.variant.protobuf;

import org.opencb.biodata.models.variant.VariantSource;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 16/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfMeta {
    public static final String ID_DEFAULT = "ID_DEFAULT";
    public static final String FILTER_DEFAULT = "FILTER_DEFAULT";
    public static final String FORMAT_DEFAULT = "FORMAT_DEFAULT";
    public static final String QUALITY_DEFAULT = "QUALITY_DEFAULT";
    public static final String INFO_QUALITY = "INFO_QUALITY";

    private final VariantSource variantSource;

    private String id;
    private String filter;
    private List<String> format;
    private int quality;
    private List<String> info;

    public VcfMeta(VariantSource variantSource) {
        readValues(variantSource);
        this.variantSource = variantSource;
    }

    private void readValues(VariantSource variantSource) {
        id = variantSource.getMetadata().getOrDefault(ID_DEFAULT, ".").toString();
        filter = variantSource.getMetadata().getOrDefault(FILTER_DEFAULT, "PASS").toString();
        format = Arrays.asList(variantSource.getMetadata().getOrDefault(FORMAT_DEFAULT, "GT").toString().split(":"));
        info = Arrays.asList(variantSource.getMetadata().getOrDefault(INFO_QUALITY, "").toString().split(","));
        quality = Integer.parseInt(variantSource.getMetadata().getOrDefault(QUALITY_DEFAULT, 100).toString());
    }

    public VariantSource getVariantSource() {
        return variantSource;
    }

    public String getIdDefault() {
        return id;
    }

    public void setIdDefault(String id) {
        this.id = id;
        variantSource.getMetadata().put(ID_DEFAULT, id);
    }

    public String getFilterDefault() {
        return filter;
    }

    public void setFilterDefault(String filter) {
        this.filter = filter;
        variantSource.getMetadata().put(FILTER_DEFAULT, filter);
    }

    public List<String> getFormatDefault() {
        return format;
    }

    public void setFormatDefault(List<String> format) {
        this.format = format;
        variantSource.getMetadata().put(FORMAT_DEFAULT, String.join(":", format));
    }

    public int getQualityDefault() {
        return quality;
    }


    public void setQualityDefault(int quality) {
        this.quality = quality;
        variantSource.getMetadata().put(QUALITY_DEFAULT, Integer.toString(quality));
    }

    public List<String> getInfoDefault() {
        return info;
    }

    public void setInfoDefault(List<String> info) {
        this.info = info;
        variantSource.getMetadata().put(INFO_QUALITY, String.join(",", info));
    }


}

