//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.04 at 03:01:56 PM CEST 
//

package it.eng.sil.pojo.yg.sap.view;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element ref="{}dataultimoagg"/>
 *         &lt;element ref="{}identificativosap" minOccurs="0"/>
 *         &lt;element ref="{}codiceentetit"/>
 *         &lt;element ref="{}tipovariazione"/>
 *         &lt;element ref="{}datadinascita"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "dataultimoagg", "identificativosap", "codiceentetit", "tipovariazione",
		"datadinascita" })
@XmlRootElement(name = "datiinvio")
public class Datiinvio {

	@XmlElement(required = true)
	protected XMLGregorianCalendar dataultimoagg;
	protected String identificativosap;
	@XmlElement(required = true)
	protected String codiceentetit;
	@XmlElement(required = true)
	protected String tipovariazione;
	@XmlElement(required = true)
	protected XMLGregorianCalendar datadinascita;

	/**
	 * Gets the value of the dataultimoagg property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDataultimoagg() {
		return dataultimoagg;
	}

	/**
	 * Sets the value of the dataultimoagg property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDataultimoagg(XMLGregorianCalendar value) {
		this.dataultimoagg = value;
	}

	/**
	 * Gets the value of the identificativosap property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdentificativosap() {
		return identificativosap;
	}

	/**
	 * Sets the value of the identificativosap property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdentificativosap(String value) {
		this.identificativosap = value;
	}

	/**
	 * Gets the value of the codiceentetit property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodiceentetit() {
		return codiceentetit;
	}

	/**
	 * Sets the value of the codiceentetit property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodiceentetit(String value) {
		this.codiceentetit = value;
	}

	/**
	 * Gets the value of the tipovariazione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipovariazione() {
		return tipovariazione;
	}

	/**
	 * Sets the value of the tipovariazione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTipovariazione(String value) {
		this.tipovariazione = value;
	}

	/**
	 * Gets the value of the datadinascita property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDatadinascita() {
		return datadinascita;
	}

	/**
	 * Sets the value of the datadinascita property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDatadinascita(XMLGregorianCalendar value) {
		this.datadinascita = value;
	}

}
