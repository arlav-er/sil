//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.06 at 05:02:09 PM CEST 
//

package it.eng.sil.coop.webservices.siferPartecipanteGG.output;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codice_fiscale" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="response_date_time" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="response_codice" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="response_descrizione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codiceFiscale", "responseDateTime", "responseCodice", "responseDescrizione" })
@XmlRootElement(name = "registraPartecipanteGaranziaGiovani_response")
public class RegistraPartecipanteGaranziaGiovaniResponse {

	@XmlElement(name = "codice_fiscale", required = true)
	protected String codiceFiscale;
	@XmlElement(name = "response_date_time", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar responseDateTime;
	@XmlElement(name = "response_codice", required = true)
	protected String responseCodice;
	@XmlElement(name = "response_descrizione", required = true)
	protected String responseDescrizione;

	/**
	 * Gets the value of the codiceFiscale property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the value of the codiceFiscale property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodiceFiscale(String value) {
		this.codiceFiscale = value;
	}

	/**
	 * Gets the value of the responseDateTime property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getResponseDateTime() {
		return responseDateTime;
	}

	/**
	 * Sets the value of the responseDateTime property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setResponseDateTime(XMLGregorianCalendar value) {
		this.responseDateTime = value;
	}

	/**
	 * Gets the value of the responseCodice property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getResponseCodice() {
		return responseCodice;
	}

	/**
	 * Sets the value of the responseCodice property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setResponseCodice(String value) {
		this.responseCodice = value;
	}

	/**
	 * Gets the value of the responseDescrizione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getResponseDescrizione() {
		return responseDescrizione;
	}

	/**
	 * Sets the value of the responseDescrizione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setResponseDescrizione(String value) {
		this.responseDescrizione = value;
	}

}
