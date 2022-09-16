//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.06 at 03:22:04 PM CEST 
//


package it.eng.myportal.youthGuarantee.setStatoAdesione.input;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="CodiceFiscale" type="{}CodiceFiscale_Type"/>
 *         &lt;element name="DataAdesione" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="RegioneAdesione" type="{}RegioneAdesione_Type"/>
 *         &lt;element name="StatoAdesione" type="{}StatoAdesione_Type"/>
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
    "codiceFiscale",
    "dataAdesione",
    "regioneAdesione",
    "statoAdesione"
})
@XmlRootElement(name = "DatiStatoAdesione")
public class DatiStatoAdesione {

    @XmlElement(name = "CodiceFiscale", required = true)
    protected String codiceFiscale;
    @XmlElement(name = "DataAdesione", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataAdesione;
    @XmlElement(name = "RegioneAdesione", required = true)
    protected String regioneAdesione;
    @XmlElement(name = "StatoAdesione", required = true)
    protected String statoAdesione;

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
     * Gets the value of the dataAdesione property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAdesione() {
        return dataAdesione;
    }

    /**
     * Sets the value of the dataAdesione property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAdesione(XMLGregorianCalendar value) {
        this.dataAdesione = value;
    }

    /**
     * Gets the value of the regioneAdesione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegioneAdesione() {
        return regioneAdesione;
    }

    /**
     * Sets the value of the regioneAdesione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegioneAdesione(String value) {
        this.regioneAdesione = value;
    }

    /**
     * Gets the value of the statoAdesione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatoAdesione() {
        return statoAdesione;
    }

    /**
     * Sets the value of the statoAdesione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatoAdesione(String value) {
        this.statoAdesione = value;
    }

}
