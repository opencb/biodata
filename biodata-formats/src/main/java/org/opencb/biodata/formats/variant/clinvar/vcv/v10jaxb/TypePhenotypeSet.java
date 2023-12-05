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
 * <p>Java class for typePhenotypeSet.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typePhenotypeSet">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Disease"/>
 *     &lt;enumeration value="DrugResponse"/>
 *     &lt;enumeration value="Finding"/>
 *     &lt;enumeration value="PhenotypeInstruction"/>
 *     &lt;enumeration value="TraitChoice"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typePhenotypeSet")
@XmlEnum
public enum TypePhenotypeSet {

    @XmlEnumValue("Disease")
    DISEASE("Disease"),
    @XmlEnumValue("DrugResponse")
    DRUG_RESPONSE("DrugResponse"),
    @XmlEnumValue("Finding")
    FINDING("Finding"),
    @XmlEnumValue("PhenotypeInstruction")
    PHENOTYPE_INSTRUCTION("PhenotypeInstruction"),
    @XmlEnumValue("TraitChoice")
    TRAIT_CHOICE("TraitChoice");
    private final String value;

    TypePhenotypeSet(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypePhenotypeSet fromValue(String v) {
        for (TypePhenotypeSet c: TypePhenotypeSet.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}