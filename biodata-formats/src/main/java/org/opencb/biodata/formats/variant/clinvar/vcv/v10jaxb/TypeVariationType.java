//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.15 at 01:36:40 AM GMT 
//


package org.opencb.biodata.formats.variant.clinvar.vcv.v10jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeVariationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeVariationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Diplotype"/>
 *     &lt;enumeration value="CompoundHeterozygote"/>
 *     &lt;enumeration value="Distinct chromosomes"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeVariationType")
@XmlEnum
public enum TypeVariationType {

    @XmlEnumValue("Diplotype")
    DIPLOTYPE("Diplotype"),
    @XmlEnumValue("CompoundHeterozygote")
    COMPOUND_HETEROZYGOTE("CompoundHeterozygote"),
    @XmlEnumValue("Distinct chromosomes")
    DISTINCT_CHROMOSOMES("Distinct chromosomes");
    private final String value;

    TypeVariationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeVariationType fromValue(String v) {
        for (TypeVariationType c: TypeVariationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
