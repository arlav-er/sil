//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.21 at 11:22:05 AM CET 
//

package it.eng.sil.coop.webservices.assisterL14.output;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for Esito-FlussoASSISTER-SIL element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Esito-FlussoASSISTER-SIL">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="response_codice_esito" type="{}codice_esito_type"/>
 *           &lt;element name="codice_fiscale" type="{}codice_fiscale_type"/>
 *           &lt;element name="patto_data" type="{}date_type" minOccurs="0"/>
 *           &lt;element name="patto_numero_protocollo" type="{}varchar_20_type" minOccurs="0"/>
 *           &lt;element name="prg_patto" type="{}int_type" minOccurs="0"/>
 *           &lt;element name="PoliticheAttive" minOccurs="0">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="PoliticaAttiva" maxOccurs="unbounded" minOccurs="0">
 *                       &lt;complexType>
 *                         &lt;complexContent>
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                             &lt;sequence>
 *                               &lt;element name="tipologiaAzioneSifer" type="{}varchar_12_type"/>
 *                               &lt;element name="misura" type="{}varchar_8_type"/>
 *                               &lt;element name="id_intervento" type="{}int_type"/>
 *                               &lt;element name="prg_percorso" type="{}int_type"/>
 *                               &lt;element name="prg_colloquio" type="{}int_type"/>
 *                             &lt;/sequence>
 *                           &lt;/restriction>
 *                         &lt;/complexContent>
 *                       &lt;/complexType>
 *                     &lt;/element>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
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
@XmlType(name = "", propOrder = { "responseCodiceEsito", "codiceFiscale", "pattoData", "pattoNumeroProtocollo",
		"prgPatto", "politicheAttive" })
@XmlRootElement(name = "Esito-FlussoASSISTER-SIL")
public class EsitoFlussoASSISTERSIL {

	@XmlElement(name = "response_codice_esito", required = true)
	protected String responseCodiceEsito;
	@XmlElement(name = "codice_fiscale", required = true)
	protected String codiceFiscale;
	@XmlElement(name = "patto_data")
	protected XMLGregorianCalendar pattoData;
	@XmlElement(name = "patto_numero_protocollo")
	protected String pattoNumeroProtocollo;
	@XmlElement(name = "prg_patto")
	protected BigInteger prgPatto;
	@XmlElement(name = "PoliticheAttive")
	protected PoliticheAttive politicheAttive;

	/**
	 * Gets the value of the responseCodiceEsito property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getResponseCodiceEsito() {
		return responseCodiceEsito;
	}

	/**
	 * Sets the value of the responseCodiceEsito property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setResponseCodiceEsito(String value) {
		this.responseCodiceEsito = value;
	}

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
	 * Gets the value of the pattoData property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getPattoData() {
		return pattoData;
	}

	/**
	 * Sets the value of the pattoData property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setPattoData(XMLGregorianCalendar value) {
		this.pattoData = value;
	}

	/**
	 * Gets the value of the pattoNumeroProtocollo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPattoNumeroProtocollo() {
		return pattoNumeroProtocollo;
	}

	/**
	 * Sets the value of the pattoNumeroProtocollo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPattoNumeroProtocollo(String value) {
		this.pattoNumeroProtocollo = value;
	}

	/**
	 * Gets the value of the prgPatto property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getPrgPatto() {
		return prgPatto;
	}

	/**
	 * Sets the value of the prgPatto property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setPrgPatto(BigInteger value) {
		this.prgPatto = value;
	}

	/**
	 * Gets the value of the politicheAttive property.
	 * 
	 * @return possible object is {@link PoliticheAttive }
	 * 
	 */
	public PoliticheAttive getPoliticheAttive() {
		return politicheAttive;
	}

	/**
	 * Sets the value of the politicheAttive property.
	 * 
	 * @param value
	 *            allowed object is {@link PoliticheAttive }
	 * 
	 */
	public void setPoliticheAttive(PoliticheAttive value) {
		this.politicheAttive = value;
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
	 *         &lt;element name="PoliticaAttiva" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="tipologiaAzioneSifer" type="{}varchar_12_type"/>
	 *                   &lt;element name="misura" type="{}varchar_8_type"/>
	 *                   &lt;element name="id_intervento" type="{}int_type"/>
	 *                   &lt;element name="prg_percorso" type="{}int_type"/>
	 *                   &lt;element name="prg_colloquio" type="{}int_type"/>
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
	@XmlType(name = "", propOrder = { "politicaAttiva" })
	public static class PoliticheAttive {

		@XmlElement(name = "PoliticaAttiva", required = true)
		protected List<PoliticaAttiva> politicaAttiva;

		/**
		 * Gets the value of the politicaAttiva property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
		 * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
		 * method for the politicaAttiva property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getPoliticaAttiva().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link PoliticaAttiva }
		 * 
		 * 
		 */
		public List<PoliticaAttiva> getPoliticaAttiva() {
			if (politicaAttiva == null) {
				politicaAttiva = new ArrayList<PoliticaAttiva>();
			}
			return this.politicaAttiva;
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
		 *         &lt;element name="tipologiaAzioneSifer" type="{}varchar_12_type"/>
		 *         &lt;element name="misura" type="{}varchar_8_type"/>
		 *         &lt;element name="id_intervento" type="{}int_type"/>
		 *         &lt;element name="prg_percorso" type="{}int_type"/>
		 *         &lt;element name="prg_colloquio" type="{}int_type"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "tipologiaAzioneSifer", "misura", "idIntervento", "prgPercorso",
				"prgColloquio" })
		public static class PoliticaAttiva {

			@XmlElement(required = true)
			protected String tipologiaAzioneSifer;
			@XmlElement(required = true)
			protected String misura;
			@XmlElement(name = "id_intervento", required = true)
			protected BigInteger idIntervento;
			@XmlElement(name = "prg_percorso", required = true)
			protected BigInteger prgPercorso;
			@XmlElement(name = "prg_colloquio", required = true)
			protected BigInteger prgColloquio;

			/**
			 * Gets the value of the tipologiaAzioneSifer property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getTipologiaAzioneSifer() {
				return tipologiaAzioneSifer;
			}

			/**
			 * Sets the value of the tipologiaAzioneSifer property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setTipologiaAzioneSifer(String value) {
				this.tipologiaAzioneSifer = value;
			}

			/**
			 * Gets the value of the misura property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getMisura() {
				return misura;
			}

			/**
			 * Sets the value of the misura property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setMisura(String value) {
				this.misura = value;
			}

			/**
			 * Gets the value of the idIntervento property.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getIdIntervento() {
				return idIntervento;
			}

			/**
			 * Sets the value of the idIntervento property.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setIdIntervento(BigInteger value) {
				this.idIntervento = value;
			}

			/**
			 * Gets the value of the prgPercorso property.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getPrgPercorso() {
				return prgPercorso;
			}

			/**
			 * Sets the value of the prgPercorso property.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setPrgPercorso(BigInteger value) {
				this.prgPercorso = value;
			}

			/**
			 * Gets the value of the prgColloquio property.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getPrgColloquio() {
				return prgColloquio;
			}

			/**
			 * Sets the value of the prgColloquio property.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setPrgColloquio(BigInteger value) {
				this.prgColloquio = value;
			}

		}

	}

}
