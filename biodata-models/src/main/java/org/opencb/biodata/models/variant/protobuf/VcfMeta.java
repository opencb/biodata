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

package org.opencb.biodata.models.variant.protobuf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 16/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
@Deprecated
public class VcfMeta {
    public static final String ID_DEFAULT = "ID_DEFAULT";
    public static final String FILTER_DEFAULT = "FILTER_DEFAULT";
    public static final String FORMAT_DEFAULT = "FORMAT_DEFAULT";
    public static final String QUALITY_DEFAULT = "QUALITY_DEFAULT";
    public static final String INFO_QUALITY = "INFO_QUALITY";

    private String id;
    private String filter;
    private List<String> format;
    private int quality;
    private List<String> info;
    private final Map<String, String> metadata = new HashMap<>();

    public VcfMeta() {
        readValues();
    }

    private void readValues() {
        id = metadata.getOrDefault(ID_DEFAULT, ".");
        filter = metadata.getOrDefault(FILTER_DEFAULT, "PASS");
        format = Arrays.asList(metadata.getOrDefault(FORMAT_DEFAULT, "GT").split(":"));
        info = Arrays.asList(metadata.getOrDefault(INFO_QUALITY, "").split(","));
        quality = Integer.parseInt(metadata.getOrDefault(QUALITY_DEFAULT, "100"));
    }

    public String getIdDefault() {
        return id;
    }

    public void setIdDefault(String id) {
        this.id = id;
        metadata.put(ID_DEFAULT, id);
    }

    public String getFilterDefault() {
        return filter;
    }

    public void setFilterDefault(String filter) {
        this.filter = filter;
        metadata.put(FILTER_DEFAULT, filter);
    }

    public List<String> getFormatDefault() {
        return format;
    }

    public void setFormatDefault(List<String> format) {
        this.format = format;
        metadata.put(FORMAT_DEFAULT, String.join(":", format));
    }

    public int getQualityDefault() {
        return quality;
    }


    public void setQualityDefault(int quality) {
        this.quality = quality;
        metadata.put(QUALITY_DEFAULT, Integer.toString(quality));
    }

    public List<String> getInfoDefault() {
        return info;
    }

    public void setInfoDefault(List<String> info) {
        this.info = info;
        metadata.put(INFO_QUALITY, String.join(",", info));
    }


}

