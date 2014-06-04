//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.25 at 02:19:47 PM CEST 
//


package org.opencb.biodata.models.variant.clinical.clinvar.v1_5jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReviewStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReviewStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="not classified by submitter"/>
 *     &lt;enumeration value="classified by single submitter"/>
 *     &lt;enumeration value="classified by multiple submitters"/>
 *     &lt;enumeration value="reviewed by expert panel"/>
 *     &lt;enumeration value="reviewed by professional society"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReviewStatusType")
@XmlEnum
public enum ReviewStatusType {

    @XmlEnumValue("not classified by submitter")
    NOT_CLASSIFIED_BY_SUBMITTER("not classified by submitter"),
    @XmlEnumValue("classified by single submitter")
    CLASSIFIED_BY_SINGLE_SUBMITTER("classified by single submitter"),
    @XmlEnumValue("classified by multiple submitters")
    CLASSIFIED_BY_MULTIPLE_SUBMITTERS("classified by multiple submitters"),
    @XmlEnumValue("reviewed by expert panel")
    REVIEWED_BY_EXPERT_PANEL("reviewed by expert panel"),
    @XmlEnumValue("reviewed by professional society")
    REVIEWED_BY_PROFESSIONAL_SOCIETY("reviewed by professional society");
    private final String value;

    ReviewStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReviewStatusType fromValue(String v) {
        for (ReviewStatusType c: ReviewStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
