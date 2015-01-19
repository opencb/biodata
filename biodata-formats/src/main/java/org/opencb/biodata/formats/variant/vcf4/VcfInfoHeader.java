package org.opencb.biodata.formats.variant.vcf4;


public class VcfInfoHeader {

    private String id;
    private String number;
    private Type type;
    private String description;

    private enum Type {Integer, Float, Flag, Character, String};

    public VcfInfoHeader(String infoLine) {
        // ##INFO=<ID=DP,Number=1,Type=Integer,Description="Total Depth">
        String[] fields = infoLine.replaceAll("[\"]", "").split("=", 6);
        // fields[2] ==> DP,Number
        this.id = fields[2].split(",")[0];
        // fields[3] ==> 1,Type
        this.number = fields[3].split(",")[0];
        // fields[4] ==> Integer,Description
        this.type = Type.valueOf(fields[4].split(",")[0]);
        // fields[5] ==> Total Depth>
        if (fields[5].endsWith(">")) {
            this.description = fields[5].substring(0, fields[5].length() - 1);
        } else {
            this.description = fields[5];
        }
    }

    public VcfInfoHeader(String id, String number, String type, String description) {
        this.id = id;
        this.number = number;
        this.type = Type.valueOf(type);
        this.description = description.replaceAll("\"", "");
    }

    @Override
    public String toString() {
        return "##INFO=<ID=" + id + ",Number=" + number + ",Type=" + type + ",Description=\"" + description + "\">";
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
