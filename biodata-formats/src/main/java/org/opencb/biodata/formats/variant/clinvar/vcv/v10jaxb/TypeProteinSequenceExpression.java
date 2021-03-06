//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.15 at 01:36:40 AM GMT 
//


package org.opencb.biodata.formats.variant.clinvar.vcv.v10jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeProteinSequenceExpression complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeProteinSequenceExpression">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Expression" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sequenceAccessionVersion" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="sequenceAccession" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="sequenceVersion" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="change" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeProteinSequenceExpression", propOrder = {
    "expression"
})
public class TypeProteinSequenceExpression {

    @XmlElement(name = "Expression", required = true)
    protected String expression;
    @XmlAttribute(name = "sequenceAccessionVersion")
    @XmlSchemaType(name = "anySimpleType")
    protected String sequenceAccessionVersion;
    @XmlAttribute(name = "sequenceAccession")
    @XmlSchemaType(name = "anySimpleType")
    protected String sequenceAccession;
    @XmlAttribute(name = "sequenceVersion")
    @XmlSchemaType(name = "anySimpleType")
    protected String sequenceVersion;
    @XmlAttribute(name = "change")
    @XmlSchemaType(name = "anySimpleType")
    protected String change;

    /**
     * Gets the value of the expression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpression(String value) {
        this.expression = value;
    }

    /**
     * Gets the value of the sequenceAccessionVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequenceAccessionVersion() {
        return sequenceAccessionVersion;
    }

    /**
     * Sets the value of the sequenceAccessionVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequenceAccessionVersion(String value) {
        this.sequenceAccessionVersion = value;
    }

    /**
     * Gets the value of the sequenceAccession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequenceAccession() {
        return sequenceAccession;
    }

    /**
     * Sets the value of the sequenceAccession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequenceAccession(String value) {
        this.sequenceAccession = value;
    }

    /**
     * Gets the value of the sequenceVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequenceVersion() {
        return sequenceVersion;
    }

    /**
     * Sets the value of the sequenceVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequenceVersion(String value) {
        this.sequenceVersion = value;
    }

    /**
     * Gets the value of the change property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChange() {
        return change;
    }

    /**
     * Sets the value of the change property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChange(String value) {
        this.change = value;
    }

}
