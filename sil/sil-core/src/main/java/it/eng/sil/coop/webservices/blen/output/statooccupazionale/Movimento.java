//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.27 at 02:51:16 PM CET 
//

package it.eng.sil.coop.webservices.blen.output.statooccupazionale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Movimento element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Movimento">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}Settore" minOccurs="0"/>
 *           &lt;element ref="{}Ccnl" minOccurs="0"/>
 *           &lt;element ref="{}CodProfessione" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;attribute name="DataAssunzione" use="required" type="{}CustomData" />
 *         &lt;attribute name="DataPresuntaFine" use="required" type="{}CustomData" />
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "settore", "ccnl", "codProfessione" })
@XmlRootElement(name = "Movimento")
public class Movimento {

	@XmlElement(name = "Settore")
	protected String settore;
	@XmlElement(name = "Ccnl")
	protected String ccnl;
	@XmlElement(name = "CodProfessione")
	protected String codProfessione;
	@XmlAttribute(name = "DataAssunzione", required = true)
	protected String dataAssunzione;
	@XmlAttribute(name = "DataPresuntaFine", required = true)
	protected String dataPresuntaFine;

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
	 * Gets the value of the ccnl property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCcnl() {
		return ccnl;
	}

	/**
	 * Sets the value of the ccnl property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCcnl(String value) {
		this.ccnl = value;
	}

	/**
	 * Gets the value of the codProfessione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodProfessione() {
		return codProfessione;
	}

	/**
	 * Sets the value of the codProfessione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodProfessione(String value) {
		this.codProfessione = value;
	}

	/**
	 * Gets the value of the dataAssunzione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDataAssunzione() {
		return dataAssunzione;
	}

	/**
	 * Sets the value of the dataAssunzione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDataAssunzione(String value) {
		this.dataAssunzione = value;
	}

	/**
	 * Gets the value of the dataPresuntaFine property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDataPresuntaFine() {
		return dataPresuntaFine;
	}

	/**
	 * Sets the value of the dataPresuntaFine property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDataPresuntaFine(String value) {
		this.dataPresuntaFine = value;
	}

}
