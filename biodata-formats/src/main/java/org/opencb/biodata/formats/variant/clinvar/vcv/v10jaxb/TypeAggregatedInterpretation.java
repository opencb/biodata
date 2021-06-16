//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.15 at 01:36:40 AM GMT 
//


package org.opencb.biodata.formats.variant.clinvar.vcv.v10jaxb;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeAggregatedInterpretation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeAggregatedInterpretation">
 *   &lt;complexContent>
 *     &lt;extension base="{}typeSingleInterpretation">
 *       &lt;sequence>
 *         &lt;element name="DescriptionHistory" type="{}typeDescriptionHistory" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ConditionList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="TraitSet" type="{}ClinAsserTraitSetType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="NumberOfSubmitters" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="NumberOfSubmissions" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeAggregatedInterpretation", propOrder = {
    "descriptionHistory",
    "conditionList"
})
public class TypeAggregatedInterpretation
    extends TypeSingleInterpretation
{

    @XmlElement(name = "DescriptionHistory")
    protected List<TypeDescriptionHistory> descriptionHistory;
    @XmlElement(name = "ConditionList")
    protected TypeAggregatedInterpretation.ConditionList conditionList;
    @XmlAttribute(name = "NumberOfSubmitters", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numberOfSubmitters;
    @XmlAttribute(name = "NumberOfSubmissions", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numberOfSubmissions;

    /**
     * Gets the value of the descriptionHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descriptionHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescriptionHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeDescriptionHistory }
     * 
     * 
     */
    public List<TypeDescriptionHistory> getDescriptionHistory() {
        if (descriptionHistory == null) {
            descriptionHistory = new ArrayList<TypeDescriptionHistory>();
        }
        return this.descriptionHistory;
    }

    /**
     * Gets the value of the conditionList property.
     * 
     * @return
     *     possible object is
     *     {@link TypeAggregatedInterpretation.ConditionList }
     *     
     */
    public TypeAggregatedInterpretation.ConditionList getConditionList() {
        return conditionList;
    }

    /**
     * Sets the value of the conditionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeAggregatedInterpretation.ConditionList }
     *     
     */
    public void setConditionList(TypeAggregatedInterpretation.ConditionList value) {
        this.conditionList = value;
    }

    /**
     * Gets the value of the numberOfSubmitters property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfSubmitters() {
        return numberOfSubmitters;
    }

    /**
     * Sets the value of the numberOfSubmitters property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfSubmitters(BigInteger value) {
        this.numberOfSubmitters = value;
    }

    /**
     * Gets the value of the numberOfSubmissions property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfSubmissions() {
        return numberOfSubmissions;
    }

    /**
     * Sets the value of the numberOfSubmissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfSubmissions(BigInteger value) {
        this.numberOfSubmissions = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="TraitSet" type="{}ClinAsserTraitSetType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "traitSet"
    })
    public static class ConditionList {

        @XmlElement(name = "TraitSet", required = true)
        protected List<ClinAsserTraitSetType> traitSet;

        /**
         * Gets the value of the traitSet property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the traitSet property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTraitSet().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ClinAsserTraitSetType }
         * 
         * 
         */
        public List<ClinAsserTraitSetType> getTraitSet() {
            if (traitSet == null) {
                traitSet = new ArrayList<ClinAsserTraitSetType>();
            }
            return this.traitSet;
        }

    }

}
