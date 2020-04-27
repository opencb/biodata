package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.RoleInCancer;

import java.util.List;

public class Cancer {

    private boolean somatic;
    private boolean germline;
    private RoleInCancer role;
    private List<String> tissues;
    private List<String> somaticTumourTypes;
    private List<String> germlineTumourTypes;
    private List<String> fusionPartners;

    public Cancer() {
    }

    public Cancer(boolean somatic, boolean germline, RoleInCancer role, List<String> tissues,
                  List<String> somaticTumourTypes, List<String> germlineTumourTypes, List<String> fusionPartners) {
        this.somatic = somatic;
        this.germline = germline;
        this.role = role;
        this.tissues = tissues;
        this.somaticTumourTypes = somaticTumourTypes;
        this.germlineTumourTypes = germlineTumourTypes;
        this.fusionPartners = fusionPartners;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cancer{");
        sb.append("somatic=").append(somatic);
        sb.append(", germline=").append(germline);
        sb.append(", role=").append(role);
        sb.append(", tissues=").append(tissues);
        sb.append(", somaticTumourTypes=").append(somaticTumourTypes);
        sb.append(", germlineTumourTypes=").append(germlineTumourTypes);
        sb.append(", fusionPartners=").append(fusionPartners);
        sb.append('}');
        return sb.toString();
    }

    public boolean isSomatic() {
        return somatic;
    }

    public Cancer setSomatic(boolean somatic) {
        this.somatic = somatic;
        return this;
    }

    public boolean isGermline() {
        return germline;
    }

    public Cancer setGermline(boolean germline) {
        this.germline = germline;
        return this;
    }

    public RoleInCancer getRole() {
        return role;
    }

    public Cancer setRole(RoleInCancer role) {
        this.role = role;
        return this;
    }

    public List<String> getTissues() {
        return tissues;
    }

    public Cancer setTissues(List<String> tissues) {
        this.tissues = tissues;
        return this;
    }

    public List<String> getSomaticTumourTypes() {
        return somaticTumourTypes;
    }

    public Cancer setSomaticTumourTypes(List<String> somaticTumourTypes) {
        this.somaticTumourTypes = somaticTumourTypes;
        return this;
    }

    public List<String> getGermlineTumourTypes() {
        return germlineTumourTypes;
    }

    public Cancer setGermlineTumourTypes(List<String> germlineTumourTypes) {
        this.germlineTumourTypes = germlineTumourTypes;
        return this;
    }

    public List<String> getFusionPartners() {
        return fusionPartners;
    }

    public Cancer setFusionPartners(List<String> fusionPartners) {
        this.fusionPartners = fusionPartners;
        return this;
    }
}
