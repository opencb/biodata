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
 * <p>Java class for typeIncludedRecordReviewStatusValue.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeIncludedRecordReviewStatusValue">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="no interpretation for the single variant"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeIncludedRecordReviewStatusValue")
@XmlEnum
public enum TypeIncludedRecordReviewStatusValue {

    @XmlEnumValue("no interpretation for the single variant")
    NO_INTERPRETATION_FOR_THE_SINGLE_VARIANT("no interpretation for the single variant");
    private final String value;

    TypeIncludedRecordReviewStatusValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeIncludedRecordReviewStatusValue fromValue(String v) {
        for (TypeIncludedRecordReviewStatusValue c: TypeIncludedRecordReviewStatusValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
