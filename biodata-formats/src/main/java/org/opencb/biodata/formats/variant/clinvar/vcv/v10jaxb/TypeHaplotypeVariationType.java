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
 * <p>Java class for typeHaplotypeVariationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeHaplotypeVariationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Haplotype"/>
 *     &lt;enumeration value="Haplotype, single variant"/>
 *     &lt;enumeration value="Variation"/>
 *     &lt;enumeration value="Phase unknown"/>
 *     &lt;enumeration value="Haplotype defined by a single variant"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeHaplotypeVariationType")
@XmlEnum
public enum TypeHaplotypeVariationType {

    @XmlEnumValue("Haplotype")
    HAPLOTYPE("Haplotype"),
    @XmlEnumValue("Haplotype, single variant")
    HAPLOTYPE_SINGLE_VARIANT("Haplotype, single variant"),
    @XmlEnumValue("Variation")
    VARIATION("Variation"),
    @XmlEnumValue("Phase unknown")
    PHASE_UNKNOWN("Phase unknown"),
    @XmlEnumValue("Haplotype defined by a single variant")
    HAPLOTYPE_DEFINED_BY_A_SINGLE_VARIANT("Haplotype defined by a single variant");
    private final String value;

    TypeHaplotypeVariationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeHaplotypeVariationType fromValue(String v) {
        for (TypeHaplotypeVariationType c: TypeHaplotypeVariationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}