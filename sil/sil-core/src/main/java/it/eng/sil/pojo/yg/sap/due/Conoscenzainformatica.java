//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.13 at 11:44:00 AM CEST 
//

package it.eng.sil.pojo.yg.sap.due;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element ref="{}codconoscenzainformatica" minOccurs="0"/>
 *         &lt;element ref="{}codgrado"/>
 *         &lt;element ref="{}specificheinformatica" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codconoscenzainformatica", "codgrado", "specificheinformatica" })
@XmlRootElement(name = "conoscenzainformatica")
public class Conoscenzainformatica {

	protected String codconoscenzainformatica;
	@XmlElement(required = true)
	protected String codgrado;
	protected String specificheinformatica;

	/**
	 * Gets the value of the codconoscenzainformatica property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodconoscenzainformatica() {
		return codconoscenzainformatica;
	}

	/**
	 * Sets the value of the codconoscenzainformatica property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodconoscenzainformatica(String value) {
		this.codconoscenzainformatica = value;
	}

	/**
	 * Gets the value of the codgrado property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodgrado() {
		return codgrado;
	}

	/**
	 * Sets the value of the codgrado property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodgrado(String value) {
		this.codgrado = value;
	}

	/**
	 * Gets the value of the specificheinformatica property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSpecificheinformatica() {
		return specificheinformatica;
	}

	/**
	 * Sets the value of the specificheinformatica property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSpecificheinformatica(String value) {
		this.specificheinformatica = value;
	}

}
