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

package org.opencb.biodata.models.clinical.qc;

import org.apache.commons.collections4.CollectionUtils;
import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

import java.util.List;
import java.util.Map;

public class GenomePlotConfig {

    @DataField(id = "title", indexed = true,
            description = FieldConstants.GENOME_PLOT_CONFIG_TITLE_DESCRIPTION)
    private String title;

    @DataField(id = "density", indexed = true,
            description = FieldConstants.GENOME_PLOT_CONFIG_DENSITY_DESCRIPTION)
    private String density;

    @DataField(id = "generalQuery", indexed = true,
            description = FieldConstants.GENOME_PLOT_CONFIG_GENERAL_QUERY_DESCRIPTION)
    private Map<String, String> generalQuery;

    @DataField(id = "tracks", indexed = true,
            description = FieldConstants.GENOME_PLOT_CONFIG_TRACKS_DESCRIPTION)
    private List<GenomePlotTrack> tracks;

    public GenomePlotConfig() {
    }

    public GenomePlotConfig(String title, String density, Map<String, String> generalQuery, List<GenomePlotTrack> tracks) {
        this.title = title;
        this.density = density;
        this.generalQuery = generalQuery;
        this.tracks = tracks;
    }

    public GenomePlotTrack getTrack(String type) {
        if (CollectionUtils.isNotEmpty(tracks)) {
            for (GenomePlotTrack track : tracks) {
                if (type.equals(track.getType())) {
                    return track;
                }

            }
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenomePlotConfig{");
        sb.append("title='").append(title).append('\'');
        sb.append(", density='").append(density).append('\'');
        sb.append(", generalQuery=").append(generalQuery);
        sb.append(", tracks=").append(tracks);
        sb.append('}');
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public GenomePlotConfig setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDensity() {
        return density;
    }

    public GenomePlotConfig setDensity(String density) {
        this.density = density;
        return this;
    }

    public Map<String, String> getGeneralQuery() {
        return generalQuery;
    }

    public GenomePlotConfig setGeneralQuery(Map<String, String> generalQuery) {
        this.generalQuery = generalQuery;
        return this;
    }

    public List<GenomePlotTrack> getTracks() {
        return tracks;
    }

    public GenomePlotConfig setTracks(List<GenomePlotTrack> tracks) {
        this.tracks = tracks;
        return this;
    }
}
