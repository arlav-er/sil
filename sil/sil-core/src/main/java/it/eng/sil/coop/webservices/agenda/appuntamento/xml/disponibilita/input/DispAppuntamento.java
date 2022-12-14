//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.26 at 11:38:30 AM CEST 
//

package it.eng.sil.coop.webservices.agenda.appuntamento.xml.disponibilita.input;

import java.math.BigInteger;

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
 *         &lt;element name="UtenteServizio">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="1"/>
 *               &lt;enumeration value="L"/>
 *               &lt;enumeration value="A"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IdProvincia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parametriAppuntamento">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="IdCPI">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;length value="9"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="codiceRichiesta">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="8"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="datiRicerca">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="dataDal" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                             &lt;element name="dataAl" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                             &lt;element name="mattina_pomeriggio" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;length value="1"/>
 *                                   &lt;enumeration value="M"/>
 *                                   &lt;enumeration value="P"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="ambiente" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="ampiezza" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *                   &lt;element name="numMaxSlot" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "utenteServizio", "idProvincia", "parametriAppuntamento" })
@XmlRootElement(name = "DispAppuntamento")
public class DispAppuntamento {

	@XmlElement(name = "UtenteServizio", required = true)
	protected String utenteServizio;
	@XmlElement(name = "IdProvincia", required = true)
	protected String idProvincia;
	@XmlElement(required = true)
	protected DispAppuntamento.ParametriAppuntamento parametriAppuntamento;

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

	/**
	 * Gets the value of the parametriAppuntamento property.
	 * 
	 * @return possible object is {@link DispAppuntamento.ParametriAppuntamento }
	 * 
	 */
	public DispAppuntamento.ParametriAppuntamento getParametriAppuntamento() {
		return parametriAppuntamento;
	}

	/**
	 * Sets the value of the parametriAppuntamento property.
	 * 
	 * @param value
	 *            allowed object is {@link DispAppuntamento.ParametriAppuntamento }
	 * 
	 */
	public void setParametriAppuntamento(DispAppuntamento.ParametriAppuntamento value) {
		this.parametriAppuntamento = value;
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
	 *         &lt;element name="IdCPI">
	 *           &lt;simpleType>
	 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *               &lt;length value="9"/>
	 *             &lt;/restriction>
	 *           &lt;/simpleType>
	 *         &lt;/element>
	 *         &lt;element name="codiceRichiesta">
	 *           &lt;simpleType>
	 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *               &lt;maxLength value="8"/>
	 *             &lt;/restriction>
	 *           &lt;/simpleType>
	 *         &lt;/element>
	 *         &lt;element name="datiRicerca">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="dataDal" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
	 *                   &lt;element name="dataAl" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
	 *                   &lt;element name="mattina_pomeriggio" minOccurs="0">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                         &lt;length value="1"/>
	 *                         &lt;enumeration value="M"/>
	 *                         &lt;enumeration value="P"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
	 *                   &lt;element name="ambiente" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
	 *                 &lt;/sequence>
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *         &lt;element name="ampiezza" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
	 *         &lt;element name="numMaxSlot" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "idCPI", "codiceRichiesta", "datiRicerca", "ampiezza", "numMaxSlot" })
	public static class ParametriAppuntamento {

		@XmlElement(name = "IdCPI", required = true)
		protected String idCPI;
		@XmlElement(required = true)
		protected String codiceRichiesta;
		@XmlElement(required = true)
		protected DispAppuntamento.ParametriAppuntamento.DatiRicerca datiRicerca;
		protected BigInteger ampiezza;
		protected BigInteger numMaxSlot;

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
		 * Gets the value of the codiceRichiesta property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodiceRichiesta() {
			return codiceRichiesta;
		}

		/**
		 * Sets the value of the codiceRichiesta property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodiceRichiesta(String value) {
			this.codiceRichiesta = value;
		}

		/**
		 * Gets the value of the datiRicerca property.
		 * 
		 * @return possible object is {@link DispAppuntamento.ParametriAppuntamento.DatiRicerca }
		 * 
		 */
		public DispAppuntamento.ParametriAppuntamento.DatiRicerca getDatiRicerca() {
			return datiRicerca;
		}

		/**
		 * Sets the value of the datiRicerca property.
		 * 
		 * @param value
		 *            allowed object is {@link DispAppuntamento.ParametriAppuntamento.DatiRicerca }
		 * 
		 */
		public void setDatiRicerca(DispAppuntamento.ParametriAppuntamento.DatiRicerca value) {
			this.datiRicerca = value;
		}

		/**
		 * Gets the value of the ampiezza property.
		 * 
		 * @return possible object is {@link BigInteger }
		 * 
		 */
		public BigInteger getAmpiezza() {
			return ampiezza;
		}

		/**
		 * Sets the value of the ampiezza property.
		 * 
		 * @param value
		 *            allowed object is {@link BigInteger }
		 * 
		 */
		public void setAmpiezza(BigInteger value) {
			this.ampiezza = value;
		}

		/**
		 * Gets the value of the numMaxSlot property.
		 * 
		 * @return possible object is {@link BigInteger }
		 * 
		 */
		public BigInteger getNumMaxSlot() {
			return numMaxSlot;
		}

		/**
		 * Sets the value of the numMaxSlot property.
		 * 
		 * @param value
		 *            allowed object is {@link BigInteger }
		 * 
		 */
		public void setNumMaxSlot(BigInteger value) {
			this.numMaxSlot = value;
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
		 *         &lt;element name="dataDal" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
		 *         &lt;element name="dataAl" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
		 *         &lt;element name="mattina_pomeriggio" minOccurs="0">
		 *           &lt;simpleType>
		 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *               &lt;length value="1"/>
		 *               &lt;enumeration value="M"/>
		 *               &lt;enumeration value="P"/>
		 *             &lt;/restriction>
		 *           &lt;/simpleType>
		 *         &lt;/element>
		 *         &lt;element name="ambiente" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "dataDal", "dataAl", "mattinaPomeriggio", "ambiente" })
		public static class DatiRicerca {

			protected XMLGregorianCalendar dataDal;
			protected XMLGregorianCalendar dataAl;
			@XmlElement(name = "mattina_pomeriggio")
			protected String mattinaPomeriggio;
			protected BigInteger ambiente;

			/**
			 * Gets the value of the dataDal property.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getDataDal() {
				return dataDal;
			}

			/**
			 * Sets the value of the dataDal property.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setDataDal(XMLGregorianCalendar value) {
				this.dataDal = value;
			}

			/**
			 * Gets the value of the dataAl property.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getDataAl() {
				return dataAl;
			}

			/**
			 * Sets the value of the dataAl property.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setDataAl(XMLGregorianCalendar value) {
				this.dataAl = value;
			}

			/**
			 * Gets the value of the mattinaPomeriggio property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getMattinaPomeriggio() {
				return mattinaPomeriggio;
			}

			/**
			 * Sets the value of the mattinaPomeriggio property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setMattinaPomeriggio(String value) {
				this.mattinaPomeriggio = value;
			}

			/**
			 * Gets the value of the ambiente property.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getAmbiente() {
				return ambiente;
			}

			/**
			 * Sets the value of the ambiente property.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setAmbiente(BigInteger value) {
				this.ambiente = value;
			}

		}

	}

}
