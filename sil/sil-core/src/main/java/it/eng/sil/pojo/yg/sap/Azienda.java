//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.13 at 11:45:47 AM CEST 
//

package it.eng.sil.pojo.yg.sap;

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
 *         &lt;element ref="{}codicefiscale"/>
 *         &lt;element ref="{}datorelavoro"/>
 *         &lt;element ref="{}indirizzoazienda" minOccurs="0"/>
 *         &lt;element ref="{}codateco"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codicefiscale", "datorelavoro", "indirizzoazienda", "codateco" })
@XmlRootElement(name = "azienda")
public class Azienda {

	@XmlElement(required = true)
	protected String codicefiscale;
	@XmlElement(required = true)
	protected String datorelavoro;
	protected String indirizzoazienda;
	@XmlElement(required = true)
	protected String codateco;

	/**
	 * Gets the value of the codicefiscale property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodicefiscale() {
		return codicefiscale;
	}

	/**
	 * Sets the value of the codicefiscale property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodicefiscale(String value) {
		this.codicefiscale = value;
	}

	/**
	 * Gets the value of the datorelavoro property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDatorelavoro() {
		return datorelavoro;
	}

	/**
	 * Sets the value of the datorelavoro property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDatorelavoro(String value) {
		this.datorelavoro = value;
	}

	/**
	 * Gets the value of the indirizzoazienda property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIndirizzoazienda() {
		return indirizzoazienda;
	}

	/**
	 * Sets the value of the indirizzoazienda property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIndirizzoazienda(String value) {
		this.indirizzoazienda = value;
	}

	/**
	 * Gets the value of the codateco property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodateco() {
		return codateco;
	}

	/**
	 * Sets the value of the codateco property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodateco(String value) {
		this.codateco = value;
	}

}
