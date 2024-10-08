//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.05.23 at 12:52:46 PM UTC 
//


package org.opencb.biodata.formats.pubmed.v233jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}Journal"/&gt;
 *         &lt;element ref="{}ArticleTitle"/&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element ref="{}Pagination"/&gt;
 *             &lt;element ref="{}ELocationID" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;element ref="{}ELocationID" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element ref="{}Abstract" minOccurs="0"/&gt;
 *         &lt;element ref="{}AuthorList" minOccurs="0"/&gt;
 *         &lt;element ref="{}Language" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{}DataBankList" minOccurs="0"/&gt;
 *         &lt;element ref="{}GrantList" minOccurs="0"/&gt;
 *         &lt;element ref="{}PublicationTypeList"/&gt;
 *         &lt;element ref="{}VernacularTitle" minOccurs="0"/&gt;
 *         &lt;element ref="{}ArticleDate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="PubModel" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="Print"/&gt;
 *             &lt;enumeration value="Print-Electronic"/&gt;
 *             &lt;enumeration value="Electronic"/&gt;
 *             &lt;enumeration value="Electronic-Print"/&gt;
 *             &lt;enumeration value="Electronic-eCollection"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "Article")
public class Article {

    @XmlElementRefs({
        @XmlElementRef(name = "Journal", type = Journal.class, required = false),
        @XmlElementRef(name = "ArticleTitle", type = ArticleTitle.class, required = false),
        @XmlElementRef(name = "Pagination", type = Pagination.class, required = false),
        @XmlElementRef(name = "ELocationID", type = ELocationID.class, required = false),
        @XmlElementRef(name = "Abstract", type = Abstract.class, required = false),
        @XmlElementRef(name = "AuthorList", type = AuthorList.class, required = false),
        @XmlElementRef(name = "Language", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataBankList", type = DataBankList.class, required = false),
        @XmlElementRef(name = "GrantList", type = GrantList.class, required = false),
        @XmlElementRef(name = "PublicationTypeList", type = PublicationTypeList.class, required = false),
        @XmlElementRef(name = "VernacularTitle", type = VernacularTitle.class, required = false),
        @XmlElementRef(name = "ArticleDate", type = ArticleDate.class, required = false)
    })
    protected List<java.lang.Object> content;
    @XmlAttribute(name = "PubModel", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pubModel;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "ELocationID" is used by two different parts of a schema. See: 
     * line 240 of file:/home/jtarraga/data/cellbase/pubmed/dtd/pubmed_190101.xsd
     * line 238 of file:/home/jtarraga/data/cellbase/pubmed/dtd/pubmed_190101.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Journal }
     * {@link ArticleTitle }
     * {@link Pagination }
     * {@link ELocationID }
     * {@link Abstract }
     * {@link AuthorList }
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link DataBankList }
     * {@link GrantList }
     * {@link PublicationTypeList }
     * {@link VernacularTitle }
     * {@link ArticleDate }
     * 
     * 
     */
    public List<java.lang.Object> getContent() {
        if (content == null) {
            content = new ArrayList<java.lang.Object>();
        }
        return this.content;
    }

    /**
     * Gets the value of the pubModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubModel() {
        return pubModel;
    }

    /**
     * Sets the value of the pubModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubModel(String value) {
        this.pubModel = value;
    }

}
