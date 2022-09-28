//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.05.23 at 12:52:46 PM UTC 
//


package org.opencb.biodata.formats.pubmed.v233jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
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
 *       &lt;attribute name="lang"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="AF"/&gt;
 *             &lt;enumeration value="AR"/&gt;
 *             &lt;enumeration value="AZ"/&gt;
 *             &lt;enumeration value="BG"/&gt;
 *             &lt;enumeration value="CS"/&gt;
 *             &lt;enumeration value="DA"/&gt;
 *             &lt;enumeration value="DE"/&gt;
 *             &lt;enumeration value="EN"/&gt;
 *             &lt;enumeration value="EL"/&gt;
 *             &lt;enumeration value="ES"/&gt;
 *             &lt;enumeration value="FA"/&gt;
 *             &lt;enumeration value="FI"/&gt;
 *             &lt;enumeration value="FR"/&gt;
 *             &lt;enumeration value="HE"/&gt;
 *             &lt;enumeration value="HU"/&gt;
 *             &lt;enumeration value="HY"/&gt;
 *             &lt;enumeration value="IN"/&gt;
 *             &lt;enumeration value="IS"/&gt;
 *             &lt;enumeration value="IT"/&gt;
 *             &lt;enumeration value="IW"/&gt;
 *             &lt;enumeration value="JA"/&gt;
 *             &lt;enumeration value="KA"/&gt;
 *             &lt;enumeration value="KO"/&gt;
 *             &lt;enumeration value="LT"/&gt;
 *             &lt;enumeration value="MK"/&gt;
 *             &lt;enumeration value="ML"/&gt;
 *             &lt;enumeration value="NL"/&gt;
 *             &lt;enumeration value="NO"/&gt;
 *             &lt;enumeration value="PL"/&gt;
 *             &lt;enumeration value="PT"/&gt;
 *             &lt;enumeration value="PS"/&gt;
 *             &lt;enumeration value="RO"/&gt;
 *             &lt;enumeration value="RU"/&gt;
 *             &lt;enumeration value="SL"/&gt;
 *             &lt;enumeration value="SK"/&gt;
 *             &lt;enumeration value="SQ"/&gt;
 *             &lt;enumeration value="SR"/&gt;
 *             &lt;enumeration value="SV"/&gt;
 *             &lt;enumeration value="SW"/&gt;
 *             &lt;enumeration value="TH"/&gt;
 *             &lt;enumeration value="TR"/&gt;
 *             &lt;enumeration value="UK"/&gt;
 *             &lt;enumeration value="VI"/&gt;
 *             &lt;enumeration value="ZH"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Type"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="FullText"/&gt;
 *             &lt;enumeration value="Summary"/&gt;
 *             &lt;enumeration value="fulltext"/&gt;
 *             &lt;enumeration value="summary"/&gt;
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
@XmlRootElement(name = "URL")
public class URL {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String lang;
    @XmlAttribute(name = "Type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
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

}
