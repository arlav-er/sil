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
 *         &lt;element ref="{}codtipodocumento" minOccurs="0"/>
 *         &lt;element ref="{}numero" minOccurs="0"/>
 *         &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element ref="{}validoal" minOccurs="0"/>
 *         &lt;element name="dataultimomantiscr" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codtipodocumento", "numero", "motivo", "validoal", "dataultimomantiscr" })
@XmlRootElement(name = "datistranieri")
public class Datistranieri {

	protected String codtipodocumento;
	protected String numero;
	protected Object motivo;
	protected XMLGregorianCalendar validoal;
	protected XMLGregorianCalendar dataultimomantiscr;

	/**
	 * Gets the value of the codtipodocumento property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodtipodocumento() {
		return codtipodocumento;
	}

	/**
	 * Sets the value of the codtipodocumento property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodtipodocumento(String value) {
		this.codtipodocumento = value;
	}

	/**
	 * Gets the value of the numero property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * Sets the value of the numero property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNumero(String value) {
		this.numero = value;
	}

	/**
	 * Gets the value of the motivo property.
	 * 
	 * @return possible object is {@link Object }
	 * 
	 */
	public Object getMotivo() {
		return motivo;
	}

	/**
	 * Sets the value of the motivo property.
	 * 
	 * @param value
	 *            allowed object is {@link Object }
	 * 
	 */
	public void setMotivo(Object value) {
		this.motivo = value;
	}

	/**
	 * Gets the value of the validoal property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getValidoal() {
		return validoal;
	}

	/**
	 * Sets the value of the validoal property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setValidoal(XMLGregorianCalendar value) {
		this.validoal = value;
	}

	/**
	 * Gets the value of the dataultimomantiscr property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDataultimomantiscr() {
		return dataultimomantiscr;
	}

	/**
	 * Sets the value of the dataultimomantiscr property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDataultimomantiscr(XMLGregorianCalendar value) {
		this.dataultimomantiscr = value;
	}

}
