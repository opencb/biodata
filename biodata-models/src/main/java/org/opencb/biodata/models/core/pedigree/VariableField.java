package org.opencb.biodata.models.core.pedigree;

/**
 * Created by imedina on 10/10/16.
 */
public class VariableField {

    private String id;
    private VariableType type;

    public enum VariableType {
        BOOLEAN,
        INTEGER,
        DOUBLE,
        STRING
    }

    public VariableField() {
    }

    public VariableField(String id, VariableType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariableField{");
        sb.append("id='").append(id).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public VariableField setId(String id) {
        this.id = id;
        return this;
    }

    public VariableType getType() {
        return type;
    }

    public VariableField setType(VariableType type) {
        this.type = type;
        return this;
    }
}
