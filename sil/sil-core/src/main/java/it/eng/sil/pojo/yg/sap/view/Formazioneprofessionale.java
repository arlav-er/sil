//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.04 at 03:01:56 PM CEST 
//

package it.eng.sil.pojo.yg.sap.view;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
 *         &lt;element ref="{}titolocorso" minOccurs="0"/>
 *         &lt;element ref="{}ente" minOccurs="0"/>
 *         &lt;element ref="{}codregione" minOccurs="0"/>
 *         &lt;element ref="{}durata" minOccurs="0"/>
 *         &lt;element ref="{}codtipologiadurata" minOccurs="0"/>
 *         &lt;element ref="{}certificazioniattestati" minOccurs="0"/>
 *         &lt;element ref="{}stage" minOccurs="0"/>
 *         &lt;element ref="{}denominazioneaziendastage" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "titolocorso", "ente", "codregione", "durata", "codtipologiadurata",
		"certificazioniattestati", "stage", "denominazioneaziendastage" })
@XmlRootElement(name = "formazioneprofessionale")
public class Formazioneprofessionale {

	protected String titolocorso;
	protected String ente;
	protected String codregione;
	protected Integer durata;
	protected String codtipologiadurata;
	protected String certificazioniattestati;
	protected String stage;
	protected String denominazioneaziendastage;

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
	 * Gets the value of the ente property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEnte() {
		return ente;
	}

	/**
	 * Sets the value of the ente property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEnte(String value) {
		this.ente = value;
	}

	/**
	 * Gets the value of the codregione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodregione() {
		return codregione;
	}

	/**
	 * Sets the value of the codregione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodregione(String value) {
		this.codregione = value;
	}

	/**
	 * Gets the value of the durata property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getDurata() {
		return durata;
	}

	/**
	 * Sets the value of the durata property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setDurata(Integer value) {
		this.durata = value;
	}

	/**
	 * Gets the value of the codtipologiadurata property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodtipologiadurata() {
		return codtipologiadurata;
	}

	/**
	 * Sets the value of the codtipologiadurata property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodtipologiadurata(String value) {
		this.codtipologiadurata = value;
	}

	/**
	 * Gets the value of the certificazioniattestati property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCertificazioniattestati() {
		return certificazioniattestati;
	}

	/**
	 * Sets the value of the certificazioniattestati property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCertificazioniattestati(String value) {
		this.certificazioniattestati = value;
	}

	/**
	 * Gets the value of the stage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getStage() {
		return stage;
	}

	/**
	 * Sets the value of the stage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStage(String value) {
		this.stage = value;
	}

	/**
	 * Gets the value of the denominazioneaziendastage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDenominazioneaziendastage() {
		return denominazioneaziendastage;
	}

	/**
	 * Sets the value of the denominazioneaziendastage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDenominazioneaziendastage(String value) {
		this.denominazioneaziendastage = value;
	}

}
