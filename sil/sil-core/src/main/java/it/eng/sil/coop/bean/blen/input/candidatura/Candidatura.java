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
 * Java class for Candidatura element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Candidatura">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}Lavoratore"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}DatiCurriculari"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}DatiSistema"/>
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
@XmlType(name = "", propOrder = { "lavoratore", "datiCurriculari", "datiSistema" })
@XmlRootElement(name = "Candidatura")
public class Candidatura {

	@XmlElement(name = "Lavoratore", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected Lavoratore lavoratore;
	@XmlElement(name = "DatiCurriculari", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected DatiCurriculari datiCurriculari;
	@XmlElement(name = "DatiSistema", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected DatiSistema datiSistema;

	/**
	 * Gets the value of the lavoratore property.
	 * 
	 * @return possible object is {@link Lavoratore }
	 * 
	 */
	public Lavoratore getLavoratore() {
		return lavoratore;
	}

	/**
	 * Sets the value of the lavoratore property.
	 * 
	 * @param value
	 *            allowed object is {@link Lavoratore }
	 * 
	 */
	public void setLavoratore(Lavoratore value) {
		this.lavoratore = value;
	}

	/**
	 * Gets the value of the datiCurriculari property.
	 * 
	 * @return possible object is {@link DatiCurriculari }
	 * 
	 */
	public DatiCurriculari getDatiCurriculari() {
		return datiCurriculari;
	}

	/**
	 * Sets the value of the datiCurriculari property.
	 * 
	 * @param value
	 *            allowed object is {@link DatiCurriculari }
	 * 
	 */
	public void setDatiCurriculari(DatiCurriculari value) {
		this.datiCurriculari = value;
	}

	/**
	 * Gets the value of the datiSistema property.
	 * 
	 * @return possible object is {@link DatiSistema }
	 * 
	 */
	public DatiSistema getDatiSistema() {
		return datiSistema;
	}

	/**
	 * Sets the value of the datiSistema property.
	 * 
	 * @param value
	 *            allowed object is {@link DatiSistema }
	 * 
	 */
	public void setDatiSistema(DatiSistema value) {
		this.datiSistema = value;
	}

}
