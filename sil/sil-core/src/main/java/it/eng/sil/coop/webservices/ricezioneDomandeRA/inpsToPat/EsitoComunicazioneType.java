//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.13 at 03:31:10 PM CEST 
//

package it.eng.sil.coop.webservices.ricezioneDomandeRA.inpsToPat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for esitoComunicazioneType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="esitoComunicazioneType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="categoria" type="{http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1}categoriaEsitoType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "esitoComunicazioneType", namespace = "http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", propOrder = {
		"codice", "descrizione", "categoria" })
public class EsitoComunicazioneType {

	@XmlElement(namespace = "http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", required = true)
	protected String codice;
	@XmlElement(namespace = "http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", required = true)
	protected String descrizione;
	@XmlElement(namespace = "http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", required = true)
	protected CategoriaEsitoType categoria;

	/**
	 * Gets the value of the codice property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodice() {
		return codice;
	}

	/**
	 * Sets the value of the codice property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodice(String value) {
		this.codice = value;
	}

	/**
	 * Gets the value of the descrizione property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the value of the descrizione property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDescrizione(String value) {
		this.descrizione = value;
	}

	/**
	 * Gets the value of the categoria property.
	 * 
	 * @return possible object is {@link CategoriaEsitoType }
	 * 
	 */
	public CategoriaEsitoType getCategoria() {
		return categoria;
	}

	/**
	 * Sets the value of the categoria property.
	 * 
	 * @param value
	 *            allowed object is {@link CategoriaEsitoType }
	 * 
	 */
	public void setCategoria(CategoriaEsitoType value) {
		this.categoria = value;
	}

}
