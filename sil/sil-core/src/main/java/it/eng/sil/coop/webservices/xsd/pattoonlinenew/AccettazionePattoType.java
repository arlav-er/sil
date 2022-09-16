//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.10 at 03:35:13 PM CEST 
//

package it.eng.sil.coop.webservices.xsd.pattoonlinenew;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for accettazionePattoType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accettazionePattoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dtmAccettazione" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="tipoAccettazione">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="OTP"/>
 *               &lt;enumeration value="SPID"/>
 *               &lt;enumeration value="CIE"/>
 *               &lt;enumeration value="RV"/>
 *               &lt;enumeration value="SMS"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accettazionePattoType", propOrder = { "dtmAccettazione", "tipoAccettazione" })
public class AccettazionePattoType {

	@XmlElement(required = true, nillable = true)
	protected XMLGregorianCalendar dtmAccettazione;
	@XmlElement(required = true, nillable = true)
	protected String tipoAccettazione;

	/**
	 * Gets the value of the dtmAccettazione property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDtmAccettazione() {
		return dtmAccettazione;
	}

	/**
	 * Sets the value of the dtmAccettazione property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDtmAccettazione(XMLGregorianCalendar value) {
		this.dtmAccettazione = value;
	}

	/**
	 * Gets the value of the tipoAccettazione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipoAccettazione() {
		return tipoAccettazione;
	}

	/**
	 * Sets the value of the tipoAccettazione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTipoAccettazione(String value) {
		this.tipoAccettazione = value;
	}

}
