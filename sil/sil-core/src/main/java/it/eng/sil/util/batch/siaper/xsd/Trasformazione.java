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
 * <p>Java class for Trasformazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Trasformazione">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Contratto" type="{http://servizi.lavoro.gov.it}Contratto"/>
 *         &lt;element name="DatoreLavoroPrec" type="{http://servizi.lavoro.gov.it}DatoreSedePrecedente" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="codiceTrasformazione" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://servizi.lavoro.gov.it}Stringa">
 *             &lt;pattern value="[A-Z]{2}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Trasformazione", propOrder = {
    "contratto",
    "datoreLavoroPrec"
})
@XmlSeeAlso({
	it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.Trasformazione.class
})
public class Trasformazione {

    @XmlElement(name = "Contratto", required = true)
    protected Contratto contratto;
    @XmlElement(name = "DatoreLavoroPrec")
    protected DatoreSedePrecedente datoreLavoroPrec;
    @XmlAttribute(name = "codiceTrasformazione", required = true)
    protected String codiceTrasformazione;

    /**
     * Gets the value of the contratto property.
     * 
     * @return
     *     possible object is
     *     {@link Contratto }
     *     
     */
    public Contratto getContratto() {
        return contratto;
    }

    /**
     * Sets the value of the contratto property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contratto }
     *     
     */
    public void setContratto(Contratto value) {
        this.contratto = value;
    }

    /**
     * Gets the value of the datoreLavoroPrec property.
     * 
     * @return
     *     possible object is
     *     {@link DatoreSedePrecedente }
     *     
     */
    public DatoreSedePrecedente getDatoreLavoroPrec() {
        return datoreLavoroPrec;
    }

    /**
     * Sets the value of the datoreLavoroPrec property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatoreSedePrecedente }
     *     
     */
    public void setDatoreLavoroPrec(DatoreSedePrecedente value) {
        this.datoreLavoroPrec = value;
    }

    /**
     * Gets the value of the codiceTrasformazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceTrasformazione() {
        return codiceTrasformazione;
    }

    /**
     * Sets the value of the codiceTrasformazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceTrasformazione(String value) {
        this.codiceTrasformazione = value;
    }

}
