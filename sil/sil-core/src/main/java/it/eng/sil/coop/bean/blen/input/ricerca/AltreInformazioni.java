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
 * Java class for AltreInformazioni element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="AltreInformazioni">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/vacancy}N.O."/>
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
@XmlType(name = "", propOrder = { "no" })
@XmlRootElement(name = "AltreInformazioni")
public class AltreInformazioni {

	@XmlElement(name = "N.O.", namespace = "http://servizi.lavoro.gov.it/vacancy", required = true)
	protected SiNo no;

	/**
	 * Gets the value of the no property.
	 * 
	 * @return possible object is {@link SiNo }
	 * 
	 */
	public SiNo getNO() {
		return no;
	}

	/**
	 * Sets the value of the no property.
	 * 
	 * @param value
	 *            allowed object is {@link SiNo }
	 * 
	 */
	public void setNO(SiNo value) {
		this.no = value;
	}

}