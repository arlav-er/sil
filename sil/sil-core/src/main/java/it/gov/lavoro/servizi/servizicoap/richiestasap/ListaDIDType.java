//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.19 at 04:27:22 PM CEST 
//

package it.gov.lavoro.servizi.servizicoap.richiestasap;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for ListaDID_type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListaDID_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codicefiscale" type="{}codiceFiscale_type" minOccurs="0"/>
 *         &lt;element name="codiceentetit" type="{}CodiceEnte_type" minOccurs="0"/>
 *         &lt;element name="dataultimoagg" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="indiceprofiling" type="{}IndiceProfiling_type" minOccurs="0"/>
 *         &lt;element name="dataevento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="disponibilita" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="N00" type="{}politica_attiva" minOccurs="0"/>
 *         &lt;element name="A02" type="{}politica_attiva" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListaDID_type", propOrder = { "codicefiscale", "codiceentetit", "dataultimoagg", "indiceprofiling",
		"dataevento", "disponibilita", "n00", "a02" })
public class ListaDIDType {

	protected String codicefiscale;
	protected String codiceentetit;
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar dataultimoagg;
	protected BigDecimal indiceprofiling;
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar dataevento;
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar disponibilita;
	@XmlElement(name = "N00")
	protected PoliticaAttiva n00;
	@XmlElement(name = "A02")
	protected PoliticaAttiva a02;

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
	 * Gets the value of the indiceprofiling property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getIndiceprofiling() {
		return indiceprofiling;
	}

	/**
	 * Sets the value of the indiceprofiling property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setIndiceprofiling(BigDecimal value) {
		this.indiceprofiling = value;
	}

	/**
	 * Gets the value of the dataevento property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDataevento() {
		return dataevento;
	}

	/**
	 * Sets the value of the dataevento property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDataevento(XMLGregorianCalendar value) {
		this.dataevento = value;
	}

	/**
	 * Gets the value of the disponibilita property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDisponibilita() {
		return disponibilita;
	}

	/**
	 * Sets the value of the disponibilita property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDisponibilita(XMLGregorianCalendar value) {
		this.disponibilita = value;
	}

	/**
	 * Gets the value of the n00 property.
	 * 
	 * @return possible object is {@link PoliticaAttiva }
	 * 
	 */
	public PoliticaAttiva getN00() {
		return n00;
	}

	/**
	 * Sets the value of the n00 property.
	 * 
	 * @param value
	 *            allowed object is {@link PoliticaAttiva }
	 * 
	 */
	public void setN00(PoliticaAttiva value) {
		this.n00 = value;
	}

	/**
	 * Gets the value of the a02 property.
	 * 
	 * @return possible object is {@link PoliticaAttiva }
	 * 
	 */
	public PoliticaAttiva getA02() {
		return a02;
	}

	/**
	 * Sets the value of the a02 property.
	 * 
	 * @param value
	 *            allowed object is {@link PoliticaAttiva }
	 * 
	 */
	public void setA02(PoliticaAttiva value) {
		this.a02 = value;
	}

}
