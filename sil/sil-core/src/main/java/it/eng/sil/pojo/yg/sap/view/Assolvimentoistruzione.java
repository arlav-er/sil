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
 *         &lt;element ref="{}obbligoformativo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "obbligoformativo" })
@XmlRootElement(name = "assolvimentoistruzione")
public class Assolvimentoistruzione {

	protected String obbligoformativo;

	/**
	 * Gets the value of the obbligoformativo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getObbligoformativo() {
		return obbligoformativo;
	}

	/**
	 * Sets the value of the obbligoformativo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setObbligoformativo(String value) {
		this.obbligoformativo = value;
	}

}
