package org.opencb.biodata.models.clinical.qc;

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;
import org.opencb.commons.datastore.core.ObjectMap;

import java.util.List;

public class HRDetect {
    @DataField(id = "id", indexed = true,
            description = FieldConstants.GENERIC_ID_DESCRIPTION)
    private String id;

    @DataField(id = "description", indexed = true,
            description = FieldConstants.GENERIC_DESCRIPTION_DESCRIPTION)
    private String description;

    @DataField(id = "snvFittingId", indexed = true, uncommentedClasses = {"ObjectMap"},
            description = FieldConstants.HRDETECT_SNV_FITTING_ID_DESCRIPTION)
    private String snvFittingId;

    @DataField(id = "svFittingId", indexed = true, uncommentedClasses = {"ObjectMap"},
            description = FieldConstants.HRDETECT_SV_FITTING_ID_DESCRIPTION)
    private String svFittingId;

    @DataField(id = "cnvQuery", indexed = true, uncommentedClasses = {"ObjectMap"},
            description = FieldConstants.HRDETECT_CNV_QUERY_DESCRIPTION)
    private ObjectMap cnvQuery;

    @DataField(id = "indelQuery", indexed = true, uncommentedClasses = {"ObjectMap"},
            description = FieldConstants.HRDETECT_INDEL_QUERY_DESCRIPTION)
    private ObjectMap indelQuery;

    @DataField(id = "params", indexed = true, uncommentedClasses = {"ObjectMap"},
            description = FieldConstants.HRDETECT_PARAMS_DESCRIPTION)
    private ObjectMap params;

    @DataField(id = "scores", indexed = true,
            description = FieldConstants.HRDETECT_SCORES_DESCRIPTION)
    private ObjectMap scores;

    @DataField(id = "files", indexed = true,
            description = FieldConstants.HRDETECT_FILES_DESCRIPTION)
    private List<String> files;

    public HRDetect() {
    }

    public HRDetect(String id, String description, String snvFittingId, String svFittingId, ObjectMap cnvQuery, ObjectMap indelQuery,
                    ObjectMap params, ObjectMap scores, List<String> files) {
        this.id = id;
        this.description = description;
        this.snvFittingId = snvFittingId;
        this.svFittingId = svFittingId;
        this.cnvQuery = cnvQuery;
        this.indelQuery = indelQuery;
        this.params = params;
        this.scores = scores;
        this.files = files;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HRDetect{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", snvFittingId='").append(snvFittingId).append('\'');
        sb.append(", svFittingId='").append(svFittingId).append('\'');
        sb.append(", cnvQuery=").append(cnvQuery);
        sb.append(", indelQuery=").append(indelQuery);
        sb.append(", params=").append(params);
        sb.append(", scores=").append(scores);
        sb.append(", files=").append(files);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public HRDetect setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public HRDetect setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSnvFittingId() {
        return snvFittingId;
    }

    public HRDetect setSnvFittingId(String snvFittingId) {
        this.snvFittingId = snvFittingId;
        return this;
    }

    public String getSvFittingId() {
        return svFittingId;
    }

    public HRDetect setSvFittingId(String svFittingId) {
        this.svFittingId = svFittingId;
        return this;
    }

    public ObjectMap getCnvQuery() {
        return cnvQuery;
    }

    public HRDetect setCnvQuery(ObjectMap cnvQuery) {
        this.cnvQuery = cnvQuery;
        return this;
    }

    public ObjectMap getIndelQuery() {
        return indelQuery;
    }

    public HRDetect setIndelQuery(ObjectMap indelQuery) {
        this.indelQuery = indelQuery;
        return this;
    }

    public ObjectMap getParams() {
        return params;
    }

    public HRDetect setParams(ObjectMap params) {
        this.params = params;
        return this;
    }

    public ObjectMap getScores() {
        return scores;
    }

    public HRDetect setScores(ObjectMap scores) {
        this.scores = scores;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public HRDetect setFiles(List<String> files) {
        this.files = files;
        return this;
    }
}
