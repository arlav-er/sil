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
 *         &lt;element ref="{http://servizi.lavoro.gov.it/messaggio}Mittente"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/messaggio}Destinatario"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/messaggio}Corpo"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/messaggio}DatiSistema"/>
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
    "mittente",
    "destinatario",
    "corpo",
    "datiSistema"
})
@XmlRootElement(name = "Messaggio")
public class Messaggio {

    @XmlElement(name = "Mittente", required = true)
    protected Mittente mittente;
    @XmlElement(name = "Destinatario", required = true)
    protected Destinatario destinatario;
    @XmlElement(name = "Corpo", required = true)
    protected Corpo corpo;
    @XmlElement(name = "DatiSistema", required = true)
    protected DatiSistema datiSistema;

    /**
     * Gets the value of the mittente property.
     * 
     * @return
     *     possible object is
     *     {@link Mittente }
     *     
     */
    public Mittente getMittente() {
        return mittente;
    }

    /**
     * Sets the value of the mittente property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mittente }
     *     
     */
    public void setMittente(Mittente value) {
        this.mittente = value;
    }

    /**
     * Gets the value of the destinatario property.
     * 
     * @return
     *     possible object is
     *     {@link Destinatario }
     *     
     */
    public Destinatario getDestinatario() {
        return destinatario;
    }

    /**
     * Sets the value of the destinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link Destinatario }
     *     
     */
    public void setDestinatario(Destinatario value) {
        this.destinatario = value;
    }

    /**
     * Gets the value of the corpo property.
     * 
     * @return
     *     possible object is
     *     {@link Corpo }
     *     
     */
    public Corpo getCorpo() {
        return corpo;
    }

    /**
     * Sets the value of the corpo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Corpo }
     *     
     */
    public void setCorpo(Corpo value) {
        this.corpo = value;
    }

    /**
     * Gets the value of the datiSistema property.
     * 
     * @return
     *     possible object is
     *     {@link DatiSistema }
     *     
     */
    public DatiSistema getDatiSistema() {
        return datiSistema;
    }

    /**
     * Sets the value of the datiSistema property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiSistema }
     *     
     */
    public void setDatiSistema(DatiSistema value) {
        this.datiSistema = value;
    }

}
