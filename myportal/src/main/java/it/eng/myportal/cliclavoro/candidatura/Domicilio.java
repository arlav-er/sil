//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.30 at 02:59:33 PM CEST 
//


package it.eng.myportal.cliclavoro.candidatura;

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
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idcomune"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}cap" minOccurs="0"/>
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
    "idcomune",
    "cap"
})
@XmlRootElement(name = "Domicilio")
public class Domicilio {

    @XmlElement(required = true)
    protected String idcomune;
    protected String cap;

    /**
     * Gets the value of the idcomune property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdcomune() {
        return idcomune;
    }

    /**
     * Sets the value of the idcomune property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdcomune(String value) {
        this.idcomune = value;
    }

    /**
     * Gets the value of the cap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCap() {
        return cap;
    }

    /**
     * Sets the value of the cap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCap(String value) {
        this.cap = value;
    }

}
