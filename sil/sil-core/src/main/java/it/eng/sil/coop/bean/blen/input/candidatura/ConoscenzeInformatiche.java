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
 * Java class for ConoscenzeInformatiche element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="ConoscenzeInformatiche">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}tipoconoscenza" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}specifiche" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "tipoconoscenza", "specifiche" })
@XmlRootElement(name = "ConoscenzeInformatiche")
public class ConoscenzeInformatiche {

	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String tipoconoscenza;
	@XmlElement(namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected String specifiche;

	/**
	 * Gets the value of the tipoconoscenza property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipoconoscenza() {
		return tipoconoscenza;
	}

	/**
	 * Sets the value of the tipoconoscenza property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTipoconoscenza(String value) {
		this.tipoconoscenza = value;
	}

	/**
	 * Gets the value of the specifiche property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSpecifiche() {
		return specifiche;
	}

	/**
	 * Sets the value of the specifiche property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSpecifiche(String value) {
		this.specifiche = value;
	}

}
