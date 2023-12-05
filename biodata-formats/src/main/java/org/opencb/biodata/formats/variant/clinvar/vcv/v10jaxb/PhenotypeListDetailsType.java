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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PhenotypeListDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PhenotypeListDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Phenotype" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="XRefList" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="XRef" type="{}typeXref" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="target_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="AffectedStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PhenotypeDetails" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="XRefList" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="XRef" type="{}typeXref" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{}SubmitterIdentifiers"/>
 *                 &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="PersonID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="DateEvaluated" type="{http://www.w3.org/2001/XMLSchema}date" />
 *                 &lt;attribute name="PhenotypeName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="AffectedStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="LOINC" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ObservedValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Interpretation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="SourceLaboratory" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="PhenotypeSetType" type="{}typePhenotypeSet" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhenotypeListDetailsType", propOrder = {
    "phenotype",
    "phenotypeDetails"
})
public class PhenotypeListDetailsType {

    @XmlElement(name = "Phenotype", required = true)
    protected List<PhenotypeListDetailsType.Phenotype> phenotype;
    @XmlElement(name = "PhenotypeDetails")
    protected List<PhenotypeListDetailsType.PhenotypeDetails> phenotypeDetails;
    @XmlAttribute(name = "PhenotypeSetType")
    protected TypePhenotypeSet phenotypeSetType;

    /**
     * Gets the value of the phenotype property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phenotype property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhenotype().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PhenotypeListDetailsType.Phenotype }
     * 
     * 
     */
    public List<PhenotypeListDetailsType.Phenotype> getPhenotype() {
        if (phenotype == null) {
            phenotype = new ArrayList<PhenotypeListDetailsType.Phenotype>();
        }
        return this.phenotype;
    }

    /**
     * Gets the value of the phenotypeDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phenotypeDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhenotypeDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PhenotypeListDetailsType.PhenotypeDetails }
     * 
     * 
     */
    public List<PhenotypeListDetailsType.PhenotypeDetails> getPhenotypeDetails() {
        if (phenotypeDetails == null) {
            phenotypeDetails = new ArrayList<PhenotypeListDetailsType.PhenotypeDetails>();
        }
        return this.phenotypeDetails;
    }

    /**
     * Gets the value of the phenotypeSetType property.
     * 
     * @return
     *     possible object is
     *     {@link TypePhenotypeSet }
     *     
     */
    public TypePhenotypeSet getPhenotypeSetType() {
        return phenotypeSetType;
    }

    /**
     * Sets the value of the phenotypeSetType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypePhenotypeSet }
     *     
     */
    public void setPhenotypeSetType(TypePhenotypeSet value) {
        this.phenotypeSetType = value;
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
     *       &lt;/sequence>
     *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="target_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="AffectedStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "xRefList"
    })
    public static class Phenotype {

        @XmlElement(name = "XRefList")
        protected PhenotypeListDetailsType.Phenotype.XRefList xRefList;
        @XmlAttribute(name = "Name", required = true)
        protected String name;
        @XmlAttribute(name = "target_id", required = true)
        protected int targetId;
        @XmlAttribute(name = "AffectedStatus")
        protected String affectedStatus;

        /**
         * Gets the value of the xRefList property.
         * 
         * @return
         *     possible object is
         *     {@link PhenotypeListDetailsType.Phenotype.XRefList }
         *     
         */
        public PhenotypeListDetailsType.Phenotype.XRefList getXRefList() {
            return xRefList;
        }

        /**
         * Sets the value of the xRefList property.
         * 
         * @param value
         *     allowed object is
         *     {@link PhenotypeListDetailsType.Phenotype.XRefList }
         *     
         */
        public void setXRefList(PhenotypeListDetailsType.Phenotype.XRefList value) {
            this.xRefList = value;
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
         * Gets the value of the targetId property.
         * 
         */
        public int getTargetId() {
            return targetId;
        }

        /**
         * Sets the value of the targetId property.
         * 
         */
        public void setTargetId(int value) {
            this.targetId = value;
        }

        /**
         * Gets the value of the affectedStatus property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffectedStatus() {
            return affectedStatus;
        }

        /**
         * Sets the value of the affectedStatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffectedStatus(String value) {
            this.affectedStatus = value;
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
     *       &lt;/sequence>
     *       &lt;attGroup ref="{}SubmitterIdentifiers"/>
     *       &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="PersonID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="DateEvaluated" type="{http://www.w3.org/2001/XMLSchema}date" />
     *       &lt;attribute name="PhenotypeName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="AffectedStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="LOINC" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="ObservedValue" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Interpretation" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="SourceLaboratory" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "xRefList"
    })
    public static class PhenotypeDetails {

        @XmlElement(name = "XRefList")
        protected PhenotypeListDetailsType.PhenotypeDetails.XRefList xRefList;
        @XmlAttribute(name = "Type", required = true)
        protected String type;
        @XmlAttribute(name = "PersonID", required = true)
        protected String personID;
        @XmlAttribute(name = "DateEvaluated")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar dateEvaluated;
        @XmlAttribute(name = "PhenotypeName")
        protected String phenotypeName;
        @XmlAttribute(name = "AffectedStatus")
        protected String affectedStatus;
        @XmlAttribute(name = "LOINC")
        protected String loinc;
        @XmlAttribute(name = "ObservedValue")
        protected String observedValue;
        @XmlAttribute(name = "Interpretation")
        protected String interpretation;
        @XmlAttribute(name = "SourceLaboratory")
        protected String sourceLaboratory;
        @XmlAttribute(name = "SubmitterName", required = true)
        protected String submitterName;
        @XmlAttribute(name = "OrgID", required = true)
        @XmlSchemaType(name = "positiveInteger")
        protected BigInteger orgID;
        @XmlAttribute(name = "OrganizationCategory", required = true)
        protected String organizationCategory;
        @XmlAttribute(name = "OrgAbbreviation")
        protected String orgAbbreviation;

        /**
         * Gets the value of the xRefList property.
         * 
         * @return
         *     possible object is
         *     {@link PhenotypeListDetailsType.PhenotypeDetails.XRefList }
         *     
         */
        public PhenotypeListDetailsType.PhenotypeDetails.XRefList getXRefList() {
            return xRefList;
        }

        /**
         * Sets the value of the xRefList property.
         * 
         * @param value
         *     allowed object is
         *     {@link PhenotypeListDetailsType.PhenotypeDetails.XRefList }
         *     
         */
        public void setXRefList(PhenotypeListDetailsType.PhenotypeDetails.XRefList value) {
            this.xRefList = value;
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
         * Gets the value of the personID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPersonID() {
            return personID;
        }

        /**
         * Sets the value of the personID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPersonID(String value) {
            this.personID = value;
        }

        /**
         * Gets the value of the dateEvaluated property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getDateEvaluated() {
            return dateEvaluated;
        }

        /**
         * Sets the value of the dateEvaluated property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setDateEvaluated(XMLGregorianCalendar value) {
            this.dateEvaluated = value;
        }

        /**
         * Gets the value of the phenotypeName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPhenotypeName() {
            return phenotypeName;
        }

        /**
         * Sets the value of the phenotypeName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPhenotypeName(String value) {
            this.phenotypeName = value;
        }

        /**
         * Gets the value of the affectedStatus property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAffectedStatus() {
            return affectedStatus;
        }

        /**
         * Sets the value of the affectedStatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAffectedStatus(String value) {
            this.affectedStatus = value;
        }

        /**
         * Gets the value of the loinc property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLOINC() {
            return loinc;
        }

        /**
         * Sets the value of the loinc property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLOINC(String value) {
            this.loinc = value;
        }

        /**
         * Gets the value of the observedValue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getObservedValue() {
            return observedValue;
        }

        /**
         * Sets the value of the observedValue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setObservedValue(String value) {
            this.observedValue = value;
        }

        /**
         * Gets the value of the interpretation property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInterpretation() {
            return interpretation;
        }

        /**
         * Sets the value of the interpretation property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInterpretation(String value) {
            this.interpretation = value;
        }

        /**
         * Gets the value of the sourceLaboratory property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSourceLaboratory() {
            return sourceLaboratory;
        }

        /**
         * Sets the value of the sourceLaboratory property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSourceLaboratory(String value) {
            this.sourceLaboratory = value;
        }

        /**
         * Gets the value of the submitterName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubmitterName() {
            return submitterName;
        }

        /**
         * Sets the value of the submitterName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubmitterName(String value) {
            this.submitterName = value;
        }

        /**
         * Gets the value of the orgID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getOrgID() {
            return orgID;
        }

        /**
         * Sets the value of the orgID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setOrgID(BigInteger value) {
            this.orgID = value;
        }

        /**
         * Gets the value of the organizationCategory property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOrganizationCategory() {
            return organizationCategory;
        }

        /**
         * Sets the value of the organizationCategory property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOrganizationCategory(String value) {
            this.organizationCategory = value;
        }

        /**
         * Gets the value of the orgAbbreviation property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOrgAbbreviation() {
            return orgAbbreviation;
        }

        /**
         * Sets the value of the orgAbbreviation property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOrgAbbreviation(String value) {
            this.orgAbbreviation = value;
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

}