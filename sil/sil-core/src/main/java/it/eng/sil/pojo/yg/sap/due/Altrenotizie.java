//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.13 at 11:44:00 AM CEST 
//

package it.eng.sil.pojo.yg.sap.due;

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
 *         &lt;element ref="{}categorieprotette" minOccurs="0"/>
 *         &lt;element ref="{}indiceisee" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "categorieprotette", "indiceisee" })
@XmlRootElement(name = "altrenotizie")
public class Altrenotizie {

	protected String categorieprotette;
	protected String indiceisee;

	/**
	 * Gets the value of the categorieprotette property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCategorieprotette() {
		return categorieprotette;
	}

	/**
	 * Sets the value of the categorieprotette property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCategorieprotette(String value) {
		this.categorieprotette = value;
	}

	/**
	 * Gets the value of the indiceisee property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIndiceisee() {
		return indiceisee;
	}

	/**
	 * Sets the value of the indiceisee property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIndiceisee(String value) {
		this.indiceisee = value;
	}

}
