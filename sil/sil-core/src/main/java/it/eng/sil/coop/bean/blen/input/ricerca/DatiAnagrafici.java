//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.18 at 03:54:43 PM CET 
//

package it.eng.sil.coop.bean.blen.input.ricerca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for DatiAnagrafici element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="DatiAnagrafici">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}codicefiscale" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}denominazione"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}settore"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}ampiezza" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}web" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "codicefiscale", "denominazione", "settore", "ampiezza", "web" })
@XmlRootElement(name = "DatiAnagrafici")
public class DatiAnagrafici {

	@XmlElement(namespace = "http://servizi.lavoro.gov.it/vacancy")
	protected String codicefiscale;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/vacancy", required = true)
	protected String denominazione;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/vacancy", required = true)
	protected String settore;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/vacancy")
	protected String ampiezza;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/vacancy")
	protected String web;

	/**
	 * Gets the value of the codicefiscale property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodicefiscale() {
		return codicefiscale;
	}

	/**
	 * Sets the value of the codicefiscale property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodicefiscale(String value) {
		this.codicefiscale = value;
	}

	/**
	 * Gets the value of the denominazione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDenominazione() {
		return denominazione;
	}

	/**
	 * Sets the value of the denominazione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDenominazione(String value) {
		this.denominazione = value;
	}

	/**
	 * Gets the value of the settore property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSettore() {
		return settore;
	}

	/**
	 * Sets the value of the settore property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSettore(String value) {
		this.settore = value;
	}

	/**
	 * Gets the value of the ampiezza property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAmpiezza() {
		return ampiezza;
	}

	/**
	 * Sets the value of the ampiezza property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAmpiezza(String value) {
		this.ampiezza = value;
	}

	/**
	 * Gets the value of the web property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getWeb() {
		return web;
	}

	/**
	 * Sets the value of the web property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWeb(String value) {
		this.web = value;
	}

}
