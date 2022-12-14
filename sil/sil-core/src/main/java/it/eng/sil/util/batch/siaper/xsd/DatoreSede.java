//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.12 at 11:27:57 AM CET 
//


package it.eng.sil.util.batch.siaper.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatoreSede complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatoreSede">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Sede" type="{http://servizi.lavoro.gov.it}IndirizzoConRecapitiItaliano"/>
 *       &lt;/sequence>
 *       &lt;attribute name="codiceFiscale" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://servizi.lavoro.gov.it}CodiceFiscale">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="denominazione" use="required" type="{http://servizi.lavoro.gov.it}StringaXLunga" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatoreSede", propOrder = {
    "sede"
})
public class DatoreSede {

    @XmlElement(name = "Sede", required = true)
    protected IndirizzoConRecapitiItaliano sede;
    @XmlAttribute(name = "codiceFiscale", required = true)
    protected String codiceFiscale;
    @XmlAttribute(name = "denominazione", required = true)
    protected String denominazione;

    /**
     * Gets the value of the sede property.
     * 
     * @return
     *     possible object is
     *     {@link IndirizzoConRecapitiItaliano }
     *     
     */
    public IndirizzoConRecapitiItaliano getSede() {
        return sede;
    }

    /**
     * Sets the value of the sede property.
     * 
     * @param value
     *     allowed object is
     *     {@link IndirizzoConRecapitiItaliano }
     *     
     */
    public void setSede(IndirizzoConRecapitiItaliano value) {
        this.sede = value;
    }

    /**
     * Gets the value of the codiceFiscale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Sets the value of the codiceFiscale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceFiscale(String value) {
        this.codiceFiscale = value;
    }

    /**
     * Gets the value of the denominazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDenominazione() {
        return denominazione;
    }

    /**
     * Sets the value of the denominazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDenominazione(String value) {
        this.denominazione = value;
    }

}
