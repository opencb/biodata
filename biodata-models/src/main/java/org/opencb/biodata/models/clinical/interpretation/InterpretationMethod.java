package org.opencb.biodata.models.clinical.interpretation;

import java.util.Collections;
import java.util.List;

public class InterpretationMethod {

    private String name;
    private String version;
    private String commit;
    private List<Software> dependencies;

    public InterpretationMethod() {
    }

    public InterpretationMethod(String name, String version, String commit, List<Software> dependencies) {
        this.name = name;
        this.version = version;
        this.commit = commit;
        this.dependencies = dependencies;
    }

    public static InterpretationMethod init() {
        return new InterpretationMethod("", "", "", Collections.emptyList());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InterpretationMethod{");
        sb.append("name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", commit='").append(commit).append('\'');
        sb.append(", dependencies=").append(dependencies);
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public InterpretationMethod setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public InterpretationMethod setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getCommit() {
        return commit;
    }

    public InterpretationMethod setCommit(String commit) {
        this.commit = commit;
        return this;
    }

    public List<Software> getDependencies() {
        return dependencies;
    }

    public InterpretationMethod setDependencies(List<Software> dependencies) {
        this.dependencies = dependencies;
        return this;
    }
}
