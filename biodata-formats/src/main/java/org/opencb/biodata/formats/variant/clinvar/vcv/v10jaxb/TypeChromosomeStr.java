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
 * <p>Java class for typeChromosomeStr.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeChromosomeStr">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="X"/>
 *     &lt;enumeration value="Y"/>
 *     &lt;enumeration value="MT"/>
 *     &lt;enumeration value="PAR"/>
 *     &lt;enumeration value="Un"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeChromosomeStr")
@XmlEnum
public enum TypeChromosomeStr {

    X("X"),
    Y("Y"),
    MT("MT"),
    PAR("PAR"),
    @XmlEnumValue("Un")
    UN("Un");
    private final String value;

    TypeChromosomeStr(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeChromosomeStr fromValue(String v) {
        for (TypeChromosomeStr c: TypeChromosomeStr.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
