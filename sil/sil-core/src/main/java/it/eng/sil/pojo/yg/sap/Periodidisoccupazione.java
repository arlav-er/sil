//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.13 at 11:45:47 AM CEST 
//

package it.eng.sil.pojo.yg.sap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

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
 *         &lt;element ref="{}dataingresso" minOccurs="0"/>
 *         &lt;element ref="{}tipoingresso" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "dataingresso", "tipoingresso" })
@XmlRootElement(name = "periodidisoccupazione")
public class Periodidisoccupazione {

	protected XMLGregorianCalendar dataingresso;
	protected String tipoingresso;

	/**
	 * Gets the value of the dataingresso property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDataingresso() {
		return dataingresso;
	}

	/**
	 * Sets the value of the dataingresso property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDataingresso(XMLGregorianCalendar value) {
		this.dataingresso = value;
	}

	/**
	 * Gets the value of the tipoingresso property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipoingresso() {
		return tipoingresso;
	}

	/**
	 * Sets the value of the tipoingresso property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTipoingresso(String value) {
		this.tipoingresso = value;
	}

}
