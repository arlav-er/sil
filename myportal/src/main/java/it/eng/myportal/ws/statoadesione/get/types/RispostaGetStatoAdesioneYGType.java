
package it.eng.myportal.ws.statoadesione.get.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for risposta_getStatoAdesioneYG_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="risposta_getStatoAdesioneYG_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Esito">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="OK"/>
 *               &lt;enumeration value="KO"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="MessaggioErrore" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DataAdesione" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="StatoAdesione">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DataStatoAdesione" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "risposta_getStatoAdesioneYG_Type", propOrder = {
    "esito",
    "messaggioErrore",
    "dataAdesione",
    "statoAdesione",
    "dataStatoAdesione"
})
public class RispostaGetStatoAdesioneYGType {

    @XmlElement(name = "Esito", required = true)
    protected String esito;
    @XmlElement(name = "MessaggioErrore", required = true)
    protected String messaggioErrore;
    @XmlElement(name = "DataAdesione", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataAdesione;
    @XmlElement(name = "StatoAdesione", required = true)
    protected String statoAdesione;
    @XmlElement(name = "DataStatoAdesione", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataStatoAdesione;

    /**
     * Gets the value of the esito property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsito() {
        return esito;
    }

    /**
     * Sets the value of the esito property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsito(String value) {
        this.esito = value;
    }

    /**
     * Gets the value of the messaggioErrore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessaggioErrore() {
        return messaggioErrore;
    }

    /**
     * Sets the value of the messaggioErrore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessaggioErrore(String value) {
        this.messaggioErrore = value;
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

    /**
     * Gets the value of the dataStatoAdesione property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataStatoAdesione() {
        return dataStatoAdesione;
    }

    /**
     * Sets the value of the dataStatoAdesione property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataStatoAdesione(XMLGregorianCalendar value) {
        this.dataStatoAdesione = value;
    }

}
