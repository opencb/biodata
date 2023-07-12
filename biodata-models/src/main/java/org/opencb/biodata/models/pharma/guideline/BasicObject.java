package org.opencb.biodata.models.pharma.guideline;

public class BasicObject {
    private String objCls;
    private String id;
    private String symbol;
    private String name;
    private float version;

    public String getObjCls() {
        return objCls;
    }

    public BasicObject setObjCls(String objCls) {
        this.objCls = objCls;
        return this;
    }

    public String getId() {
        return id;
    }

    public BasicObject setId(String id) {
        this.id = id;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public BasicObject setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getName() {
        return name;
    }

    public BasicObject setName(String name) {
        this.name = name;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public BasicObject setVersion(float version) {
        this.version = version;
        return this;
    }
}
