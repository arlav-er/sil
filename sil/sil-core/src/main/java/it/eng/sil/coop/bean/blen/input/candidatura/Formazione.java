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
 * Java class for Formazione element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Formazione">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}titolocorso" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idsede" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}durata" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idtipologiadurata" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idattestazione" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idqualifica" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}descrqualifica" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "titolocorso", "idsede", "durata", "idtipologiadurata", "idattestazione",
		"idqualifica", "descrqualifica" })
@XmlRootElement(name = "Formazione")
public class Formazione {

	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String titolocorso;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String idsede;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String durata;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected TipologiaDurata idtipologiadurata;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected Attestazionecheck idattestazione;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String idqualifica;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String descrqualifica;

	/**
	 * Gets the value of the titolocorso property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTitolocorso() {
		return titolocorso;
	}

	/**
	 * Sets the value of the titolocorso property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTitolocorso(String value) {
		this.titolocorso = value;
	}

	/**
	 * Gets the value of the idsede property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdsede() {
		return idsede;
	}

	/**
	 * Sets the value of the idsede property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdsede(String value) {
		this.idsede = value;
	}

	/**
	 * Gets the value of the durata property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDurata() {
		return durata;
	}

	/**
	 * Sets the value of the durata property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDurata(String value) {
		this.durata = value;
	}

	/**
	 * Gets the value of the idtipologiadurata property.
	 * 
	 * @return possible object is {@link TipologiaDurata }
	 * 
	 */
	public TipologiaDurata getIdtipologiadurata() {
		return idtipologiadurata;
	}

	/**
	 * Sets the value of the idtipologiadurata property.
	 * 
	 * @param value
	 *            allowed object is {@link TipologiaDurata }
	 * 
	 */
	public void setIdtipologiadurata(TipologiaDurata value) {
		this.idtipologiadurata = value;
	}

	/**
	 * Gets the value of the idattestazione property.
	 * 
	 * @return possible object is {@link Attestazionecheck }
	 * 
	 */
	public Attestazionecheck getIdattestazione() {
		return idattestazione;
	}

	/**
	 * Sets the value of the idattestazione property.
	 * 
	 * @param value
	 *            allowed object is {@link Attestazionecheck }
	 * 
	 */
	public void setIdattestazione(Attestazionecheck value) {
		this.idattestazione = value;
	}

	/**
	 * Gets the value of the idqualifica property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdqualifica() {
		return idqualifica;
	}

	/**
	 * Sets the value of the idqualifica property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdqualifica(String value) {
		this.idqualifica = value;
	}

	/**
	 * Gets the value of the descrqualifica property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescrqualifica() {
		return descrqualifica;
	}

	/**
	 * Sets the value of the descrqualifica property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDescrqualifica(String value) {
		this.descrqualifica = value;
	}

}
