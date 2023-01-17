package org.opencb.biodata.models.clinical.interpretation;

import org.apache.commons.collections4.CollectionUtils;
import org.opencb.biodata.models.clinical.ClinicalProperty.RoleInCancer;

import java.util.Collections;
import java.util.List;

public class CancerPanel {

    private boolean somatic;
    private boolean germline;
    private List<RoleInCancer> roles;
    private List<String> tissues;
    private List<String> somaticTumourTypes;
    private List<String> germlineTumourTypes;
    private List<String> fusionPartners;

    public CancerPanel() {
    }

    @Deprecated
    public CancerPanel(boolean somatic, boolean germline, RoleInCancer role, List<String> tissues,
                       List<String> somaticTumourTypes, List<String> germlineTumourTypes, List<String> fusionPartners) {
        this.somatic = somatic;
        this.germline = germline;
        this.roles = role != null ? Collections.singletonList(role) : null;
        this.tissues = tissues;
        this.somaticTumourTypes = somaticTumourTypes;
        this.germlineTumourTypes = germlineTumourTypes;
        this.fusionPartners = fusionPartners;
    }

    public CancerPanel(boolean somatic, boolean germline, List<RoleInCancer> roles, List<String> tissues,
                       List<String> somaticTumourTypes, List<String> germlineTumourTypes, List<String> fusionPartners) {
        this.somatic = somatic;
        this.germline = germline;
        this.roles = roles;
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
        sb.append(", roles=").append(roles);
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

    @Deprecated
    public RoleInCancer getRole() {
        return CollectionUtils.isNotEmpty(roles) ? roles.get(0) : null;
    }

    @Deprecated
    public CancerPanel setRole(RoleInCancer role) {
        this.roles = role != null ? Collections.singletonList(role) : null;
        return this;
    }

    public List<RoleInCancer> getRoles() {
        return roles;
    }

    public CancerPanel setRoles(List<RoleInCancer> roles) {
        this.roles = roles;
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
