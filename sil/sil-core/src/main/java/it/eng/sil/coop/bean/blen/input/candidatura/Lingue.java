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

/**
 * <p>
 * Java class for Lingue element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Lingue">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idlingua" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idlivelloletto" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idlivelloscritto" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idlivelloparlato" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "idlingua", "idlivelloletto", "idlivelloscritto", "idlivelloparlato" })
@XmlRootElement(name = "Lingue")
public class Lingue {

	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String idlingua;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String idlivelloletto;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String idlivelloscritto;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String idlivelloparlato;

	/**
	 * Gets the value of the idlingua property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdlingua() {
		return idlingua;
	}

	/**
	 * Sets the value of the idlingua property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdlingua(String value) {
		this.idlingua = value;
	}

	/**
	 * Gets the value of the idlivelloletto property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdlivelloletto() {
		return idlivelloletto;
	}

	/**
	 * Sets the value of the idlivelloletto property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdlivelloletto(String value) {
		this.idlivelloletto = value;
	}

	/**
	 * Gets the value of the idlivelloscritto property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdlivelloscritto() {
		return idlivelloscritto;
	}

	/**
	 * Sets the value of the idlivelloscritto property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdlivelloscritto(String value) {
		this.idlivelloscritto = value;
	}

	/**
	 * Gets the value of the idlivelloparlato property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdlivelloparlato() {
		return idlivelloparlato;
	}

	/**
	 * Sets the value of the idlivelloparlato property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdlivelloparlato(String value) {
		this.idlivelloparlato = value;
	}

}
