//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.31 at 12:20:27 PM CEST 
//


package it.eng.myportal.cliclavoro.messaggio;

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
 *         &lt;element ref="{http://servizi.lavoro.gov.it/messaggio}tiposoggetto"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/messaggio}Interlocutore"/>
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
    "tiposoggetto",
    "interlocutore"
})
@XmlRootElement(name = "Mittente")
public class Mittente {

    @XmlElement(required = true)
    protected String tiposoggetto;
    @XmlElement(name = "Interlocutore", required = true)
    protected Interlocutore interlocutore;

    /**
     * Gets the value of the tiposoggetto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiposoggetto() {
        return tiposoggetto;
    }

    /**
     * Sets the value of the tiposoggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiposoggetto(String value) {
        this.tiposoggetto = value;
    }

    /**
     * Gets the value of the interlocutore property.
     * 
     * @return
     *     possible object is
     *     {@link Interlocutore }
     *     
     */
    public Interlocutore getInterlocutore() {
        return interlocutore;
    }

    /**
     * Sets the value of the interlocutore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Interlocutore }
     *     
     */
    public void setInterlocutore(Interlocutore value) {
        this.interlocutore = value;
    }

}
