package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.RoleInCancer;

import java.util.List;

public class CancerPanel {

    private boolean somatic;
    private boolean germline;
    private RoleInCancer role;
    private List<String> tissues;
    private List<String> somaticTumourTypes;
    private List<String> germlineTumourTypes;
    private List<String> fusionPartners;

    public CancerPanel() {
    }

    public CancerPanel(boolean somatic, boolean germline, RoleInCancer role, List<String> tissues,
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

    public CancerPanel setSomatic(boolean somatic) {
        this.somatic = somatic;
        return this;
    }

    public boolean isGermline() {
        return germline;
    }

    public CancerPanel setGermline(boolean germline) {
        this.germline = germline;
        return this;
    }

    public RoleInCancer getRole() {
        return role;
    }

    public CancerPanel setRole(RoleInCancer role) {
        this.role = role;
        return this;
    }

    public List<String> getTissues() {
        return tissues;
    }

    public CancerPanel setTissues(List<String> tissues) {
        this.tissues = tissues;
        return this;
    }

    public List<String> getSomaticTumourTypes() {
        return somaticTumourTypes;
    }

    public CancerPanel setSomaticTumourTypes(List<String> somaticTumourTypes) {
        this.somaticTumourTypes = somaticTumourTypes;
        return this;
    }

    public List<String> getGermlineTumourTypes() {
        return germlineTumourTypes;
    }

    public CancerPanel setGermlineTumourTypes(List<String> germlineTumourTypes) {
        this.germlineTumourTypes = germlineTumourTypes;
        return this;
    }

    public List<String> getFusionPartners() {
        return fusionPartners;
    }

    public CancerPanel setFusionPartners(List<String> fusionPartners) {
        this.fusionPartners = fusionPartners;
        return this;
    }
}
