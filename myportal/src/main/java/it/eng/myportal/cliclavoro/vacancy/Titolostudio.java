//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.06 at 12:08:29 PM CET 
//


package it.eng.myportal.cliclavoro.vacancy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}idtitolostudio"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}descrizionestudio" minOccurs="0"/>
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
    "idtitolostudio",
    "descrizionestudio"
})
@XmlRootElement(name = "titolostudio")
public class Titolostudio {

    @XmlElement(required = true)
    protected String idtitolostudio;
    protected String descrizionestudio;

    /**
     * Gets the value of the idtitolostudio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdtitolostudio() {
        return idtitolostudio;
    }

    /**
     * Sets the value of the idtitolostudio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdtitolostudio(String value) {
        this.idtitolostudio = value;
    }

    /**
     * Gets the value of the descrizionestudio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizionestudio() {
        return descrizionestudio;
    }

    /**
     * Sets the value of the descrizionestudio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizionestudio(String value) {
        this.descrizionestudio = value;
    }

}
