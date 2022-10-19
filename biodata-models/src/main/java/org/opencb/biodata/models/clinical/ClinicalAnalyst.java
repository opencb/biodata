package org.opencb.biodata.models.clinical;

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

public class ClinicalAnalyst {

    @DataField(id = "id", indexed = true,
            description = FieldConstants.GENERIC_ID_DESCRIPTION)
    private String id;

    @DataField(id = "name", indexed = true,
            description = FieldConstants.GENERIC_NAME_DESCRIPTION)
    private String name;

    @DataField(id = "email", indexed = true,
            description = FieldConstants.CLINICAL_ANALYST_EMAIL_DESCRIPTION)
    private String email;

    @DataField(id = "signer",
            description = FieldConstants.CLINICAL_ANALYST_SIGNER_DESCRIPTION)
    private boolean signer;

    @DataField(id = "signature", indexed = true, description = FieldConstants.CLINICAL_ANALYST_SIGNATURE_DESCRIPTION)
    private String signature;

    public ClinicalAnalyst() {
    }

    public ClinicalAnalyst(String id, String name, String email, boolean signer, String signature) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.signer = signer;
        this.signature = signature;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalAnalyst{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", signer=").append(signer);
        sb.append(", signature='").append(signature).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public ClinicalAnalyst setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClinicalAnalyst setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ClinicalAnalyst setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean isSigner() {
        return signer;
    }

    public ClinicalAnalyst setSigner(boolean signer) {
        this.signer = signer;
        return this;
    }

    public String getSignature() {
        return signature;
    }

    public ClinicalAnalyst setSignature(String signature) {
        this.signature = signature;
        return this;
    }
}
