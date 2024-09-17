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

package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

import java.util.HashMap;
import java.util.Map;

public class Software {

    @DataField(id = "name",
            description = FieldConstants.SOFTWARE_NAME)
    private String name;

    @DataField(id = "version",
            description = FieldConstants.SOFTWARE_VERSION)
    private String version;

    @DataField(id = "repository",
            description = FieldConstants.SOFTWARE_REPOSITORY)
    private String repository;

    @DataField(id = "commit",
            description = FieldConstants.SOFTWARE_COMMIT)
    private String commit;

    @DataField(id = "website",
            description = FieldConstants.SOFTWARE_WEBSITE)
    private String website;

    @DataField(id = "params",
            description = FieldConstants.SOFTWARE_PARAMS)
    private Map<String, String> params;

    public Software() {
        this("", "", "", "", "", new HashMap<>());
    }

    public Software(String name, String version, String repository, String commit, String website, Map<String, String> params) {
        this.name = name;
        this.version = version;
        this.repository = repository;
        this.commit = commit;
        this.website = website;
        this.params = params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Software{");
        sb.append("name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", repository='").append(repository).append('\'');
        sb.append(", commit='").append(commit).append('\'');
        sb.append(", website='").append(website).append('\'');
        sb.append(", params=").append(params);
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Software setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Software setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getRepository() {
        return repository;
    }

    public Software setRepository(String repository) {
        this.repository = repository;
        return this;
    }

    public String getCommit() {
        return commit;
    }

    public Software setCommit(String commit) {
        this.commit = commit;
        return this;
    }

    public String getWebsite() {
        return website;
    }

    public Software setWebsite(String website) {
        this.website = website;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Software setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }
}
