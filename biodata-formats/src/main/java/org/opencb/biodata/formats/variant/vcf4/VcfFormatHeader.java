package org.opencb.biodata.formats.variant.vcf4;

public class VcfFormatHeader {

    private String id;
    private String number;

    private enum Type {
        Integer, Float, Character, String
    };

    private Type type;
    private String description;

    public VcfFormatHeader(String foramtLine) {
        // ##FORMAT=<ID=GQ,Number=1,Type=Float,Description="Genotype Quality">
        String[] fields = foramtLine.replaceAll("[\"<>]", "").split("=", 6);
        // fields[2] ==> GQ,Number
        this.id = fields[2].split(",")[0];
        // fields[3] ==> 1,Type
        this.number = fields[3].split(",")[0];
        // fields[4] ==> Float,Description
        this.type = Type.valueOf(fields[4].split(",")[0]);
        // fields[5] ==> "Genotype Quality"
        this.description = fields[5];
    }

    public VcfFormatHeader(String id, String number, String type, String description) {
        this.id = id;
        this.number = number;
        this.type = Type.valueOf(type);
        this.description = description;
    }

    @Override
    public String toString() {
        return "##FORMAT=<ID=" + id + ",Number=" + number + ",Type=" + type + ",Description=\"" + description + "\">";
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type.toString();
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = Type.valueOf(type);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
