//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.11 at 12:04:20 PM CET 
//

package it.eng.sil.coop.bean.blen.input.ricerca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Vacancy element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Vacancy">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}DatoreLavoro"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}Richiesta"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}AltreInformazioni"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}DatiSistema"/>
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
@XmlType(name = "", propOrder = { "datoreLavoro", "richiesta", "altreInformazioni", "datiSistema" })
@XmlRootElement(name = "Vacancy")
public class Vacancy {

	@XmlElement(name = "DatoreLavoro", namespace = "http://servizi.lavoro.gov.it/vacancy", required = true)
	protected DatoreLavoro datoreLavoro;
	@XmlElement(name = "Richiesta", namespace = "http://servizi.lavoro.gov.it/vacancy", required = true)
	protected Richiesta richiesta;
	@XmlElement(name = "AltreInformazioni", namespace = "http://servizi.lavoro.gov.it/vacancy", required = true)
	protected AltreInformazioni altreInformazioni;
	@XmlElement(name = "DatiSistema", namespace = "http://servizi.lavoro.gov.it/vacancy", required = true)
	protected DatiSistema datiSistema;

	/**
	 * Gets the value of the datoreLavoro property.
	 * 
	 * @return possible object is {@link DatoreLavoro }
	 * 
	 */
	public DatoreLavoro getDatoreLavoro() {
		return datoreLavoro;
	}

	/**
	 * Sets the value of the datoreLavoro property.
	 * 
	 * @param value
	 *            allowed object is {@link DatoreLavoro }
	 * 
	 */
	public void setDatoreLavoro(DatoreLavoro value) {
		this.datoreLavoro = value;
	}

	/**
	 * Gets the value of the richiesta property.
	 * 
	 * @return possible object is {@link Richiesta }
	 * 
	 */
	public Richiesta getRichiesta() {
		return richiesta;
	}

	/**
	 * Sets the value of the richiesta property.
	 * 
	 * @param value
	 *            allowed object is {@link Richiesta }
	 * 
	 */
	public void setRichiesta(Richiesta value) {
		this.richiesta = value;
	}

	/**
	 * Gets the value of the altreInformazioni property.
	 * 
	 * @return possible object is {@link AltreInformazioni }
	 * 
	 */
	public AltreInformazioni getAltreInformazioni() {
		return altreInformazioni;
	}

	/**
	 * Sets the value of the altreInformazioni property.
	 * 
	 * @param value
	 *            allowed object is {@link AltreInformazioni }
	 * 
	 */
	public void setAltreInformazioni(AltreInformazioni value) {
		this.altreInformazioni = value;
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