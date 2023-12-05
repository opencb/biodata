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
 * This is a record of one or more simple alleles on the same chromosome molecule.
 * 
 * <p>Java class for typeHaplotype complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeHaplotype">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SimpleAllele" type="{}typeAllele" maxOccurs="unbounded"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VariationType" type="{}typeHaplotypeVariationType"/>
 *         &lt;element name="OtherNameList" type="{}typeNames" minOccurs="0"/>
 *         &lt;element name="HGVSlist" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="HGVS" type="{}typeHGVSExpression" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Interpretations" type="{}typeAggregatedInterpretationSet" minOccurs="0"/>
 *         &lt;element name="FunctionalConsequence" type="{}typeFunctionalConsequence" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="XRefList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="XRef" type="{}typeXref" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Comment" type="{}typeComment" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="VariationID" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="NumberOfCopies" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="NumberOfChromosomes" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeHaplotype", propOrder = {
    "simpleAllele",
    "name",
    "variationType",
    "otherNameList",
    "hgvSlist",
    "interpretations",
    "functionalConsequence",
    "xRefList",
    "comment"
})
public class TypeHaplotype {

    @XmlElement(name = "SimpleAllele", required = true)
    protected List<TypeAllele> simpleAllele;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "VariationType", required = true)
    @XmlSchemaType(name = "string")
    protected TypeHaplotypeVariationType variationType;
    @XmlElement(name = "OtherNameList")
    protected TypeNames otherNameList;
    @XmlElement(name = "HGVSlist")
    protected TypeHaplotype.HGVSlist hgvSlist;
    @XmlElement(name = "Interpretations")
    protected TypeAggregatedInterpretationSet interpretations;
    @XmlElement(name = "FunctionalConsequence")
    protected List<TypeFunctionalConsequence> functionalConsequence;
    @XmlElement(name = "XRefList")
    protected TypeHaplotype.XRefList xRefList;
    @XmlElement(name = "Comment")
    protected List<TypeComment> comment;
    @XmlAttribute(name = "VariationID", required = true)
    protected int variationID;
    @XmlAttribute(name = "NumberOfCopies")
    protected Integer numberOfCopies;
    @XmlAttribute(name = "NumberOfChromosomes")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger numberOfChromosomes;

    /**
     * Gets the value of the simpleAllele property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleAllele property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleAllele().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeAllele }
     * 
     * 
     */
    public List<TypeAllele> getSimpleAllele() {
        if (simpleAllele == null) {
            simpleAllele = new ArrayList<TypeAllele>();
        }
        return this.simpleAllele;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the variationType property.
     * 
     * @return
     *     possible object is
     *     {@link TypeHaplotypeVariationType }
     *     
     */
    public TypeHaplotypeVariationType getVariationType() {
        return variationType;
    }

    /**
     * Sets the value of the variationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeHaplotypeVariationType }
     *     
     */
    public void setVariationType(TypeHaplotypeVariationType value) {
        this.variationType = value;
    }

    /**
     * Gets the value of the otherNameList property.
     * 
     * @return
     *     possible object is
     *     {@link TypeNames }
     *     
     */
    public TypeNames getOtherNameList() {
        return otherNameList;
    }

    /**
     * Sets the value of the otherNameList property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeNames }
     *     
     */
    public void setOtherNameList(TypeNames value) {
        this.otherNameList = value;
    }

    /**
     * Gets the value of the hgvSlist property.
     * 
     * @return
     *     possible object is
     *     {@link TypeHaplotype.HGVSlist }
     *     
     */
    public TypeHaplotype.HGVSlist getHGVSlist() {
        return hgvSlist;
    }

    /**
     * Sets the value of the hgvSlist property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeHaplotype.HGVSlist }
     *     
     */
    public void setHGVSlist(TypeHaplotype.HGVSlist value) {
        this.hgvSlist = value;
    }

    /**
     * Gets the value of the interpretations property.
     * 
     * @return
     *     possible object is
     *     {@link TypeAggregatedInterpretationSet }
     *     
     */
    public TypeAggregatedInterpretationSet getInterpretations() {
        return interpretations;
    }

    /**
     * Sets the value of the interpretations property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeAggregatedInterpretationSet }
     *     
     */
    public void setInterpretations(TypeAggregatedInterpretationSet value) {
        this.interpretations = value;
    }

    /**
     * Gets the value of the functionalConsequence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the functionalConsequence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFunctionalConsequence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeFunctionalConsequence }
     * 
     * 
     */
    public List<TypeFunctionalConsequence> getFunctionalConsequence() {
        if (functionalConsequence == null) {
            functionalConsequence = new ArrayList<TypeFunctionalConsequence>();
        }
        return this.functionalConsequence;
    }

    /**
     * Gets the value of the xRefList property.
     * 
     * @return
     *     possible object is
     *     {@link TypeHaplotype.XRefList }
     *     
     */
    public TypeHaplotype.XRefList getXRefList() {
        return xRefList;
    }

    /**
     * Sets the value of the xRefList property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeHaplotype.XRefList }
     *     
     */
    public void setXRefList(TypeHaplotype.XRefList value) {
        this.xRefList = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the comment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeComment }
     * 
     * 
     */
    public List<TypeComment> getComment() {
        if (comment == null) {
            comment = new ArrayList<TypeComment>();
        }
        return this.comment;
    }

    /**
     * Gets the value of the variationID property.
     * 
     */
    public int getVariationID() {
        return variationID;
    }

    /**
     * Sets the value of the variationID property.
     * 
     */
    public void setVariationID(int value) {
        this.variationID = value;
    }

    /**
     * Gets the value of the numberOfCopies property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfCopies() {
        return numberOfCopies;
    }

    /**
     * Sets the value of the numberOfCopies property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfCopies(Integer value) {
        this.numberOfCopies = value;
    }

    /**
     * Gets the value of the numberOfChromosomes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfChromosomes() {
        return numberOfChromosomes;
    }

    /**
     * Sets the value of the numberOfChromosomes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfChromosomes(BigInteger value) {
        this.numberOfChromosomes = value;
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
     *         &lt;element name="HGVS" type="{}typeHGVSExpression" maxOccurs="unbounded" minOccurs="0"/>
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
        "hgvs"
    })
    public static class HGVSlist {

        @XmlElement(name = "HGVS")
        protected List<TypeHGVSExpression> hgvs;

        /**
         * Gets the value of the hgvs property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the hgvs property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHGVS().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeHGVSExpression }
         * 
         * 
         */
        public List<TypeHGVSExpression> getHGVS() {
            if (hgvs == null) {
                hgvs = new ArrayList<TypeHGVSExpression>();
            }
            return this.hgvs;
        }

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
     *         &lt;element name="XRef" type="{}typeXref" maxOccurs="unbounded" minOccurs="0"/>
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
        "xRef"
    })
    public static class XRefList {

        @XmlElement(name = "XRef")
        protected List<TypeXref> xRef;

        /**
         * Gets the value of the xRef property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the xRef property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getXRef().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeXref }
         * 
         * 
         */
        public List<TypeXref> getXRef() {
            if (xRef == null) {
                xRef = new ArrayList<TypeXref>();
            }
            return this.xRef;
        }

    }

}