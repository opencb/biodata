//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.25 at 02:19:47 PM CEST 
//


package org.opencb.biodata.models.variant.clinical.clinvar.v1_5jaxb;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ReferenceAssertionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenceAssertionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClinVarAccession">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="Acc" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Version" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                 &lt;attribute name="Type" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="RCV"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="DateUpdated" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RecordStatus">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="current"/>
 *               &lt;enumeration value="replaced"/>
 *               &lt;enumeration value="removed"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ClinicalSignificance" type="{}ClinicalSignificanceType" minOccurs="0"/>
 *         &lt;element name="Assertion" type="{}AssertionTypeRCV"/>
 *         &lt;element name="ExternalID" type="{}XrefType" minOccurs="0"/>
 *         &lt;element name="AttributeSet" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Attribute">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;>AttributeType">
 *                           &lt;attribute name="Type" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="ModeOfInheritance"/>
 *                                 &lt;enumeration value="Penetrance"/>
 *                                 &lt;enumeration value="AgeOfOnset"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Citation" type="{}CitationType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="XRef" type="{}XrefType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="Comment" type="{}CommentType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ObservedIn" type="{}ObservationSet" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MeasureSet" type="{}MeasureSetType"/>
 *         &lt;element name="TraitSet" type="{}TraitSetType"/>
 *         &lt;element name="Citation" type="{}CitationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Comment" type="{}CommentType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}CVIdentifiers"/>
 *       &lt;attribute name="DateCreated" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="DateLastUpdated" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="SubmissionDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceAssertionType", propOrder = {
    "clinVarAccession",
    "recordStatus",
    "clinicalSignificance",
    "assertion",
    "externalID",
    "attributeSet",
    "observedIn",
    "measureSet",
    "traitSet",
    "citation",
    "comment"
})
public class ReferenceAssertionType {

    @XmlElement(name = "ClinVarAccession", required = true)
    protected ReferenceAssertionType.ClinVarAccession clinVarAccession;
    @XmlElement(name = "RecordStatus", required = true, defaultValue = "current")
    protected String recordStatus;
    @XmlElement(name = "ClinicalSignificance")
    protected ClinicalSignificanceType clinicalSignificance;
    @XmlElement(name = "Assertion", required = true)
    protected AssertionTypeRCV assertion;
    @XmlElement(name = "ExternalID")
    protected XrefType externalID;
    @XmlElement(name = "AttributeSet")
    protected List<ReferenceAssertionType.AttributeSet> attributeSet;
    @XmlElement(name = "ObservedIn")
    protected List<ObservationSet> observedIn;
    @XmlElement(name = "MeasureSet", required = true)
    protected MeasureSetType measureSet;
    @XmlElement(name = "TraitSet", required = true)
    protected TraitSetType traitSet;
    @XmlElement(name = "Citation")
    protected List<CitationType> citation;
    @XmlElement(name = "Comment")
    protected List<CommentType> comment;
    @XmlAttribute(name = "DateCreated")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateCreated;
    @XmlAttribute(name = "DateLastUpdated")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateLastUpdated;
    @XmlAttribute(name = "SubmissionDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar submissionDate;
    @XmlAttribute(name = "ID")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger id;

    /**
     * Gets the value of the clinVarAccession property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceAssertionType.ClinVarAccession }
     *     
     */
    public ReferenceAssertionType.ClinVarAccession getClinVarAccession() {
        return clinVarAccession;
    }

    /**
     * Sets the value of the clinVarAccession property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceAssertionType.ClinVarAccession }
     *     
     */
    public void setClinVarAccession(ReferenceAssertionType.ClinVarAccession value) {
        this.clinVarAccession = value;
    }

    /**
     * Gets the value of the recordStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordStatus() {
        return recordStatus;
    }

    /**
     * Sets the value of the recordStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordStatus(String value) {
        this.recordStatus = value;
    }

    /**
     * Gets the value of the clinicalSignificance property.
     * 
     * @return
     *     possible object is
     *     {@link ClinicalSignificanceType }
     *     
     */
    public ClinicalSignificanceType getClinicalSignificance() {
        return clinicalSignificance;
    }

    /**
     * Sets the value of the clinicalSignificance property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClinicalSignificanceType }
     *     
     */
    public void setClinicalSignificance(ClinicalSignificanceType value) {
        this.clinicalSignificance = value;
    }

    /**
     * Gets the value of the assertion property.
     * 
     * @return
     *     possible object is
     *     {@link AssertionTypeRCV }
     *     
     */
    public AssertionTypeRCV getAssertion() {
        return assertion;
    }

    /**
     * Sets the value of the assertion property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssertionTypeRCV }
     *     
     */
    public void setAssertion(AssertionTypeRCV value) {
        this.assertion = value;
    }

    /**
     * Gets the value of the externalID property.
     * 
     * @return
     *     possible object is
     *     {@link XrefType }
     *     
     */
    public XrefType getExternalID() {
        return externalID;
    }

    /**
     * Sets the value of the externalID property.
     * 
     * @param value
     *     allowed object is
     *     {@link XrefType }
     *     
     */
    public void setExternalID(XrefType value) {
        this.externalID = value;
    }

    /**
     * Gets the value of the attributeSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceAssertionType.AttributeSet }
     * 
     * 
     */
    public List<ReferenceAssertionType.AttributeSet> getAttributeSet() {
        if (attributeSet == null) {
            attributeSet = new ArrayList<ReferenceAssertionType.AttributeSet>();
        }
        return this.attributeSet;
    }

    /**
     * Gets the value of the observedIn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the observedIn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObservedIn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObservationSet }
     * 
     * 
     */
    public List<ObservationSet> getObservedIn() {
        if (observedIn == null) {
            observedIn = new ArrayList<ObservationSet>();
        }
        return this.observedIn;
    }

    /**
     * Gets the value of the measureSet property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureSetType }
     *     
     */
    public MeasureSetType getMeasureSet() {
        return measureSet;
    }

    /**
     * Sets the value of the measureSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureSetType }
     *     
     */
    public void setMeasureSet(MeasureSetType value) {
        this.measureSet = value;
    }

    /**
     * Gets the value of the traitSet property.
     * 
     * @return
     *     possible object is
     *     {@link TraitSetType }
     *     
     */
    public TraitSetType getTraitSet() {
        return traitSet;
    }

    /**
     * Sets the value of the traitSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link TraitSetType }
     *     
     */
    public void setTraitSet(TraitSetType value) {
        this.traitSet = value;
    }

    /**
     * Gets the value of the citation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the citation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCitation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CitationType }
     * 
     * 
     */
    public List<CitationType> getCitation() {
        if (citation == null) {
            citation = new ArrayList<CitationType>();
        }
        return this.citation;
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
     * {@link CommentType }
     * 
     * 
     */
    public List<CommentType> getComment() {
        if (comment == null) {
            comment = new ArrayList<CommentType>();
        }
        return this.comment;
    }

    /**
     * Gets the value of the dateCreated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the value of the dateCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateCreated(XMLGregorianCalendar value) {
        this.dateCreated = value;
    }

    /**
     * Gets the value of the dateLastUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateLastUpdated() {
        return dateLastUpdated;
    }

    /**
     * Sets the value of the dateLastUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateLastUpdated(XMLGregorianCalendar value) {
        this.dateLastUpdated = value;
    }

    /**
     * Gets the value of the submissionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Sets the value of the submissionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSubmissionDate(XMLGregorianCalendar value) {
        this.submissionDate = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setID(BigInteger value) {
        this.id = value;
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
     *         &lt;element name="Attribute">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;>AttributeType">
     *                 &lt;attribute name="Type" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="ModeOfInheritance"/>
     *                       &lt;enumeration value="Penetrance"/>
     *                       &lt;enumeration value="AgeOfOnset"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Citation" type="{}CitationType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="XRef" type="{}XrefType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="Comment" type="{}CommentType" maxOccurs="unbounded" minOccurs="0"/>
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
        "attribute",
        "citation",
        "xRef",
        "comment"
    })
    public static class AttributeSet {

        @XmlElement(name = "Attribute", required = true)
        protected ReferenceAssertionType.AttributeSet.Attribute attribute;
        @XmlElement(name = "Citation")
        protected List<CitationType> citation;
        @XmlElement(name = "XRef")
        protected List<XrefType> xRef;
        @XmlElement(name = "Comment")
        protected List<CommentType> comment;

        /**
         * Gets the value of the attribute property.
         * 
         * @return
         *     possible object is
         *     {@link ReferenceAssertionType.AttributeSet.Attribute }
         *     
         */
        public ReferenceAssertionType.AttributeSet.Attribute getAttribute() {
            return attribute;
        }

        /**
         * Sets the value of the attribute property.
         * 
         * @param value
         *     allowed object is
         *     {@link ReferenceAssertionType.AttributeSet.Attribute }
         *     
         */
        public void setAttribute(ReferenceAssertionType.AttributeSet.Attribute value) {
            this.attribute = value;
        }

        /**
         * Gets the value of the citation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the citation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCitation().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CitationType }
         * 
         * 
         */
        public List<CitationType> getCitation() {
            if (citation == null) {
                citation = new ArrayList<CitationType>();
            }
            return this.citation;
        }

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
         * {@link XrefType }
         * 
         * 
         */
        public List<XrefType> getXRef() {
            if (xRef == null) {
                xRef = new ArrayList<XrefType>();
            }
            return this.xRef;
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
         * {@link CommentType }
         * 
         * 
         */
        public List<CommentType> getComment() {
            if (comment == null) {
                comment = new ArrayList<CommentType>();
            }
            return this.comment;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;>AttributeType">
         *       &lt;attribute name="Type" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="ModeOfInheritance"/>
         *             &lt;enumeration value="Penetrance"/>
         *             &lt;enumeration value="AgeOfOnset"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Attribute
            extends AttributeType
        {

            @XmlAttribute(name = "Type", required = true)
            protected String type;

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

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
     *       &lt;attribute name="Acc" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Version" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *       &lt;attribute name="Type" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="RCV"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="DateUpdated" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ClinVarAccession {

        @XmlAttribute(name = "Acc", required = true)
        protected String acc;
        @XmlAttribute(name = "Version", required = true)
        protected BigInteger version;
        @XmlAttribute(name = "Type", required = true)
        protected String type;
        @XmlAttribute(name = "DateUpdated")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar dateUpdated;

        /**
         * Gets the value of the acc property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAcc() {
            return acc;
        }

        /**
         * Sets the value of the acc property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAcc(String value) {
            this.acc = value;
        }

        /**
         * Gets the value of the version property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getVersion() {
            return version;
        }

        /**
         * Sets the value of the version property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setVersion(BigInteger value) {
            this.version = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Gets the value of the dateUpdated property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getDateUpdated() {
            return dateUpdated;
        }

        /**
         * Sets the value of the dateUpdated property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setDateUpdated(XMLGregorianCalendar value) {
            this.dateUpdated = value;
        }

    }

}
