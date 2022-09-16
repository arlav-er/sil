//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.25 at 05:11:29 PM CEST 
//

package it.eng.sil.coop.webservices.agenda.appuntamento.xml.verifica.input;

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
 *         &lt;element name="codiceFiscale" type="{}CodiceFiscale"/>
 *         &lt;element name="UtenteServizio">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="1"/>
 *               &lt;enumeration value="L"/>
 *               &lt;enumeration value="A"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="datiAppuntamento">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="dataAppuntamento" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                   &lt;element name="oraAppuntamento">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;length value="5"/>
 *                         &lt;pattern value="[0-9]{2}:[0-9]{2}"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="IdCPI">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;length value="9"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="IdProvincia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codiceFiscale", "utenteServizio", "datiAppuntamento" })
@XmlRootElement(name = "VerificaApp")
public class VerificaApp {

	@XmlElement(required = true)
	protected String codiceFiscale;
	@XmlElement(name = "UtenteServizio", required = true)
	protected String utenteServizio;
	@XmlElement(required = true)
	protected VerificaApp.DatiAppuntamento datiAppuntamento;

	/**
	 * Gets the value of the codiceFiscale property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the value of the codiceFiscale property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodiceFiscale(String value) {
		this.codiceFiscale = value;
	}

	/**
	 * Gets the value of the utenteServizio property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUtenteServizio() {
		return utenteServizio;
	}

	/**
	 * Sets the value of the utenteServizio property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUtenteServizio(String value) {
		this.utenteServizio = value;
	}

	/**
	 * Gets the value of the datiAppuntamento property.
	 * 
	 * @return possible object is {@link VerificaApp.DatiAppuntamento }
	 * 
	 */
	public VerificaApp.DatiAppuntamento getDatiAppuntamento() {
		return datiAppuntamento;
	}

	/**
	 * Sets the value of the datiAppuntamento property.
	 * 
	 * @param value
	 *            allowed object is {@link VerificaApp.DatiAppuntamento }
	 * 
	 */
	public void setDatiAppuntamento(VerificaApp.DatiAppuntamento value) {
		this.datiAppuntamento = value;
	}

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
	 *         &lt;element name="dataAppuntamento" type="{http://www.w3.org/2001/XMLSchema}date"/>
	 *         &lt;element name="oraAppuntamento">
	 *           &lt;simpleType>
	 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *               &lt;length value="5"/>
	 *               &lt;pattern value="[0-9]{2}:[0-9]{2}"/>
	 *             &lt;/restriction>
	 *           &lt;/simpleType>
	 *         &lt;/element>
	 *         &lt;element name="IdCPI">
	 *           &lt;simpleType>
	 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *               &lt;length value="9"/>
	 *             &lt;/restriction>
	 *           &lt;/simpleType>
	 *         &lt;/element>
	 *         &lt;element name="IdProvincia" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "codiceRichiesta", "dataAppuntamento", "oraAppuntamento", "idCPI",
			"idProvincia" })
	public static class DatiAppuntamento {

		@XmlElement(required = true)
		protected String codiceRichiesta;
		@XmlElement(required = true)
		protected XMLGregorianCalendar dataAppuntamento;
		@XmlElement(required = true)
		protected String oraAppuntamento;
		@XmlElement(name = "IdCPI", required = true)
		protected String idCPI;
		@XmlElement(name = "IdProvincia", required = true)
		protected String idProvincia;

		public String getCodiceRichiesta() {
			return codiceRichiesta;
		}

		public void setCodiceRichiesta(String value) {
			this.codiceRichiesta = value;
		}

		/**
		 * Gets the value of the dataAppuntamento property.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getDataAppuntamento() {
			return dataAppuntamento;
		}

		/**
		 * Sets the value of the dataAppuntamento property.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setDataAppuntamento(XMLGregorianCalendar value) {
			this.dataAppuntamento = value;
		}

		/**
		 * Gets the value of the oraAppuntamento property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getOraAppuntamento() {
			return oraAppuntamento;
		}

		/**
		 * Sets the value of the oraAppuntamento property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setOraAppuntamento(String value) {
			this.oraAppuntamento = value;
		}

		/**
		 * Gets the value of the idCPI property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getIdCPI() {
			return idCPI;
		}

		/**
		 * Sets the value of the idCPI property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setIdCPI(String value) {
			this.idCPI = value;
		}

		/**
		 * Gets the value of the idProvincia property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getIdProvincia() {
			return idProvincia;
		}

		/**
		 * Sets the value of the idProvincia property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setIdProvincia(String value) {
			this.idProvincia = value;
		}

	}

}