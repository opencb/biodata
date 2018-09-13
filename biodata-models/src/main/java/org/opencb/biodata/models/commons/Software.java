package org.opencb.biodata.models.commons;

import java.util.Map;

public class Software {

    private String name;
    private String version;
    private String repository;
    private String commit;
    private String website;
    private Map<String, String> params;

    public Software() {
    }

    @Deprecated
    public Software(String name, String version, String commit, String website, Map<String, String> params) {
        this.name = name;
        this.version = version;
        this.commit = commit;
        this.website = website;
        this.params = params;
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
