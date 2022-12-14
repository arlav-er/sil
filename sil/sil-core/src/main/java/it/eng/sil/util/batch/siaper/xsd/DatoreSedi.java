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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatoreSedi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatoreSedi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DatoreAnagraficaCompleta" type="{http://servizi.lavoro.gov.it}DatoreAnagraficaCompleta" minOccurs="0"/>
 *         &lt;element name="SedeLegale" type="{http://servizi.lavoro.gov.it}IndirizzoConRecapitiItaliano"/>
 *         &lt;element name="SedeLavoro" type="{http://servizi.lavoro.gov.it}IndirizzoConRecapitiItaliano"/>
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
@XmlType(name = "DatoreSedi", propOrder = {
    "datoreAnagraficaCompleta",
    "sedeLegale",
    "sedeLavoro"
})
@XmlSeeAlso({
	it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.DatoreLavoro.class,
	it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.DittaUtilizzatrice.class
})
public class DatoreSedi {

    @XmlElement(name = "DatoreAnagraficaCompleta")
    protected DatoreAnagraficaCompleta datoreAnagraficaCompleta;
    @XmlElement(name = "SedeLegale", required = true)
    protected IndirizzoConRecapitiItaliano sedeLegale;
    @XmlElement(name = "SedeLavoro", required = true)
    protected IndirizzoConRecapitiItaliano sedeLavoro;
    @XmlAttribute(name = "codiceFiscale", required = true)
    protected String codiceFiscale;
    @XmlAttribute(name = "denominazione", required = true)
    protected String denominazione;

    /**
     * Gets the value of the datoreAnagraficaCompleta property.
     * 
     * @return
     *     possible object is
     *     {@link DatoreAnagraficaCompleta }
     *     
     */
    public DatoreAnagraficaCompleta getDatoreAnagraficaCompleta() {
        return datoreAnagraficaCompleta;
    }

    /**
     * Sets the value of the datoreAnagraficaCompleta property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatoreAnagraficaCompleta }
     *     
     */
    public void setDatoreAnagraficaCompleta(DatoreAnagraficaCompleta value) {
        this.datoreAnagraficaCompleta = value;
    }

    /**
     * Gets the value of the sedeLegale property.
     * 
     * @return
     *     possible object is
     *     {@link IndirizzoConRecapitiItaliano }
     *     
     */
    public IndirizzoConRecapitiItaliano getSedeLegale() {
        return sedeLegale;
    }

    /**
     * Sets the value of the sedeLegale property.
     * 
     * @param value
     *     allowed object is
     *     {@link IndirizzoConRecapitiItaliano }
     *     
     */
    public void setSedeLegale(IndirizzoConRecapitiItaliano value) {
        this.sedeLegale = value;
    }

    /**
     * Gets the value of the sedeLavoro property.
     * 
     * @return
     *     possible object is
     *     {@link IndirizzoConRecapitiItaliano }
     *     
     */
    public IndirizzoConRecapitiItaliano getSedeLavoro() {
        return sedeLavoro;
    }

    /**
     * Sets the value of the sedeLavoro property.
     * 
     * @param value
     *     allowed object is
     *     {@link IndirizzoConRecapitiItaliano }
     *     
     */
    public void setSedeLavoro(IndirizzoConRecapitiItaliano value) {
        this.sedeLavoro = value;
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
