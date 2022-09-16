//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.18 at 03:56:22 PM CET 
//

package it.eng.sil.coop.bean.blen.input.candidatura;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for EsperienzeLavorative element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="EsperienzeLavorative">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}tipoesperienza" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}qualificasvolta" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}descrqualificasvolta" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}principalimansioni" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}nomedatore" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}datainizio" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}datafine" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}indirizzodatore" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "tipoesperienza", "qualificasvolta", "descrqualificasvolta", "principalimansioni",
		"nomedatore", "datainizio", "datafine", "indirizzodatore" })
@XmlRootElement(name = "EsperienzeLavorative")
public class EsperienzeLavorative {

	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String tipoesperienza;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String qualificasvolta;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String descrqualificasvolta;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String principalimansioni;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String nomedatore;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected XMLGregorianCalendar datainizio;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected XMLGregorianCalendar datafine;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String indirizzodatore;

	/**
	 * Gets the value of the tipoesperienza property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipoesperienza() {
		return tipoesperienza;
	}

	/**
	 * Sets the value of the tipoesperienza property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTipoesperienza(String value) {
		this.tipoesperienza = value;
	}

	/**
	 * Gets the value of the qualificasvolta property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getQualificasvolta() {
		return qualificasvolta;
	}

	/**
	 * Sets the value of the qualificasvolta property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setQualificasvolta(String value) {
		this.qualificasvolta = value;
	}

	/**
	 * Gets the value of the descrqualificasvolta property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescrqualificasvolta() {
		return descrqualificasvolta;
	}

	/**
	 * Sets the value of the descrqualificasvolta property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDescrqualificasvolta(String value) {
		this.descrqualificasvolta = value;
	}

	/**
	 * Gets the value of the principalimansioni property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPrincipalimansioni() {
		return principalimansioni;
	}

	/**
	 * Sets the value of the principalimansioni property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPrincipalimansioni(String value) {
		this.principalimansioni = value;
	}

	/**
	 * Gets the value of the nomedatore property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNomedatore() {
		return nomedatore;
	}

	/**
	 * Sets the value of the nomedatore property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNomedatore(String value) {
		this.nomedatore = value;
	}

	/**
	 * Gets the value of the datainizio property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDatainizio() {
		return datainizio;
	}

	/**
	 * Sets the value of the datainizio property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDatainizio(XMLGregorianCalendar value) {
		this.datainizio = value;
	}

	/**
	 * Gets the value of the datafine property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDatafine() {
		return datafine;
	}

	/**
	 * Sets the value of the datafine property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDatafine(XMLGregorianCalendar value) {
		this.datafine = value;
	}

	/**
	 * Gets the value of the indirizzodatore property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIndirizzodatore() {
		return indirizzodatore;
	}

	/**
	 * Sets the value of the indirizzodatore property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIndirizzodatore(String value) {
		this.indirizzodatore = value;
	}

}
