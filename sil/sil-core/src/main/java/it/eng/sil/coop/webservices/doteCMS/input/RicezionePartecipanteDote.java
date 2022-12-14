//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.20 at 12:32:13 PM CEST 
//

package it.eng.sil.coop.webservices.doteCMS.input;

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
 * Java class for RicezionePartecipanteDote element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="RicezionePartecipanteDote">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="partecipante">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="codiceFiscale" type="{}codice_fiscale_type"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="DomandaDote">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="IdDomandaDote" type="{}varchar_30_type"/>
 *                     &lt;element name="pattoInclusioneAttiva" type="{}si_no_type"/>
 *                     &lt;element name="prgPatto" type="{}int_type" minOccurs="0"/>
 *                     &lt;element name="CFEnteAccreditato" type="{}codice_fiscale_type"/>
 *                     &lt;element name="RagioneSocialeEnte" type="{}varchar_100_type"/>
 *                     &lt;element name="sedeEnteAccreditato" type="{}varchar_4_type"/>
 *                     &lt;element name="indirizzoEnteAccreditato" type="{}varchar_200_type" minOccurs="0"/>
 *                     &lt;element name="telefonoEnteAccreditato" type="{}varchar_20_type" minOccurs="0"/>
 *                     &lt;element name="PoliticheAttive">
 *                       &lt;complexType>
 *                         &lt;complexContent>
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                             &lt;sequence>
 *                               &lt;element name="PoliticaAttiva" maxOccurs="unbounded">
 *                                 &lt;complexType>
 *                                   &lt;complexContent>
 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                       &lt;sequence>
 *                                         &lt;element name="CodAzioneFormCal" type="{}varchar_8_type"/>
 *                                         &lt;element name="prgPercorso" type="{}int_type" minOccurs="0"/>
 *                                         &lt;element name="prgColloquio" type="{}int_type" minOccurs="0"/>
 *                                         &lt;element name="dataStimata" type="{}date_type"/>
 *                                         &lt;element name="esito" type="{}varchar_8_type"/>
 *                                       &lt;/sequence>
 *                                     &lt;/restriction>
 *                                   &lt;/complexContent>
 *                                 &lt;/complexType>
 *                               &lt;/element>
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
@XmlType(name = "", propOrder = { "partecipante", "domandaDote" })
@XmlRootElement(name = "RicezionePartecipanteDote")
public class RicezionePartecipanteDote {

	@XmlElement(required = true)
	protected Partecipante partecipante;
	@XmlElement(name = "DomandaDote", required = true)
	protected DomandaDote domandaDote;

	/**
	 * Gets the value of the partecipante property.
	 * 
	 * @return possible object is {@link Partecipante }
	 * 
	 */
	public Partecipante getPartecipante() {
		return partecipante;
	}

	/**
	 * Sets the value of the partecipante property.
	 * 
	 * @param value
	 *            allowed object is {@link Partecipante }
	 * 
	 */
	public void setPartecipante(Partecipante value) {
		this.partecipante = value;
	}

	/**
	 * Gets the value of the domandaDote property.
	 * 
	 * @return possible object is {@link DomandaDote }
	 * 
	 */
	public DomandaDote getDomandaDote() {
		return domandaDote;
	}

	/**
	 * Sets the value of the domandaDote property.
	 * 
	 * @param value
	 *            allowed object is {@link DomandaDote }
	 * 
	 */
	public void setDomandaDote(DomandaDote value) {
		this.domandaDote = value;
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
	 *         &lt;element name="IdDomandaDote" type="{}varchar_30_type"/>
	 *         &lt;element name="pattoInclusioneAttiva" type="{}si_no_type"/>
	 *         &lt;element name="prgPatto" type="{}int_type" minOccurs="0"/>
	 *         &lt;element name="CFEnteAccreditato" type="{}codice_fiscale_type"/>
	 *         &lt;element name="RagioneSocialeEnte" type="{}varchar_100_type"/>
	 *         &lt;element name="sedeEnteAccreditato" type="{}varchar_4_type"/>
	 *         &lt;element name="indirizzoEnteAccreditato" type="{}varchar_200_type" minOccurs="0"/>
	 *         &lt;element name="telefonoEnteAccreditato" type="{}varchar_20_type" minOccurs="0"/>
	 *         &lt;element name="PoliticheAttive">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="PoliticaAttiva" maxOccurs="unbounded">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="CodAzioneFormCal" type="{}varchar_8_type"/>
	 *                             &lt;element name="prgPercorso" type="{}int_type" minOccurs="0"/>
	 *                             &lt;element name="prgColloquio" type="{}int_type" minOccurs="0"/>
	 *                             &lt;element name="dataStimata" type="{}date_type"/>
	 *                             &lt;element name="esito" type="{}varchar_8_type"/>
	 *                           &lt;/sequence>
	 *                         &lt;/restriction>
	 *                       &lt;/complexContent>
	 *                     &lt;/complexType>
	 *                   &lt;/element>
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
	@XmlType(name = "", propOrder = { "idDomandaDote", "pattoInclusioneAttiva", "prgPatto", "cfEnteAccreditato",
			"ragioneSocialeEnte", "sedeEnteAccreditato", "indirizzoEnteAccreditato", "telefonoEnteAccreditato",
			"politicheAttive" })
	public static class DomandaDote {

		@XmlElement(name = "IdDomandaDote", required = true)
		protected String idDomandaDote;
		@XmlElement(required = true)
		protected String pattoInclusioneAttiva;
		protected BigInteger prgPatto;
		@XmlElement(name = "CFEnteAccreditato", required = true)
		protected String cfEnteAccreditato;
		@XmlElement(name = "RagioneSocialeEnte", required = true)
		protected String ragioneSocialeEnte;
		@XmlElement(required = true)
		protected String sedeEnteAccreditato;
		protected String indirizzoEnteAccreditato;
		protected String telefonoEnteAccreditato;
		@XmlElement(name = "PoliticheAttive", required = true)
		protected PoliticheAttive politicheAttive;

		/**
		 * Gets the value of the idDomandaDote property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getIdDomandaDote() {
			return idDomandaDote;
		}

		/**
		 * Sets the value of the idDomandaDote property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setIdDomandaDote(String value) {
			this.idDomandaDote = value;
		}

		/**
		 * Gets the value of the pattoInclusioneAttiva property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getPattoInclusioneAttiva() {
			return pattoInclusioneAttiva;
		}

		/**
		 * Sets the value of the pattoInclusioneAttiva property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setPattoInclusioneAttiva(String value) {
			this.pattoInclusioneAttiva = value;
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
		 * Gets the value of the cfEnteAccreditato property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCFEnteAccreditato() {
			return cfEnteAccreditato;
		}

		/**
		 * Sets the value of the cfEnteAccreditato property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCFEnteAccreditato(String value) {
			this.cfEnteAccreditato = value;
		}

		/**
		 * Gets the value of the ragioneSocialeEnte property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getRagioneSocialeEnte() {
			return ragioneSocialeEnte;
		}

		/**
		 * Sets the value of the ragioneSocialeEnte property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setRagioneSocialeEnte(String value) {
			this.ragioneSocialeEnte = value;
		}

		/**
		 * Gets the value of the sedeEnteAccreditato property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getSedeEnteAccreditato() {
			return sedeEnteAccreditato;
		}

		/**
		 * Sets the value of the sedeEnteAccreditato property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setSedeEnteAccreditato(String value) {
			this.sedeEnteAccreditato = value;
		}

		/**
		 * Gets the value of the indirizzoEnteAccreditato property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getIndirizzoEnteAccreditato() {
			return indirizzoEnteAccreditato;
		}

		/**
		 * Sets the value of the indirizzoEnteAccreditato property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setIndirizzoEnteAccreditato(String value) {
			this.indirizzoEnteAccreditato = value;
		}

		/**
		 * Gets the value of the telefonoEnteAccreditato property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTelefonoEnteAccreditato() {
			return telefonoEnteAccreditato;
		}

		/**
		 * Sets the value of the telefonoEnteAccreditato property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTelefonoEnteAccreditato(String value) {
			this.telefonoEnteAccreditato = value;
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
		 *         &lt;element name="PoliticaAttiva" maxOccurs="unbounded">
		 *           &lt;complexType>
		 *             &lt;complexContent>
		 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                 &lt;sequence>
		 *                   &lt;element name="CodAzioneFormCal" type="{}varchar_8_type"/>
		 *                   &lt;element name="prgPercorso" type="{}int_type" minOccurs="0"/>
		 *                   &lt;element name="prgColloquio" type="{}int_type" minOccurs="0"/>
		 *                   &lt;element name="dataStimata" type="{}date_type"/>
		 *                   &lt;element name="esito" type="{}varchar_8_type"/>
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
			 * make to the returned list will be present inside the JAXB object. This is why there is not a
			 * <CODE>set</CODE> method for the politicaAttiva property.
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
			 *         &lt;element name="CodAzioneFormCal" type="{}varchar_8_type"/>
			 *         &lt;element name="prgPercorso" type="{}int_type" minOccurs="0"/>
			 *         &lt;element name="prgColloquio" type="{}int_type" minOccurs="0"/>
			 *         &lt;element name="dataStimata" type="{}date_type"/>
			 *         &lt;element name="esito" type="{}varchar_8_type"/>
			 *       &lt;/sequence>
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "codAzioneFormCal", "prgPercorso", "prgColloquio", "dataStimata",
					"esito" })
			public static class PoliticaAttiva {

				@XmlElement(name = "CodAzioneFormCal", required = true)
				protected String codAzioneFormCal;
				protected BigInteger prgPercorso;
				protected BigInteger prgColloquio;
				@XmlElement(required = true)
				protected XMLGregorianCalendar dataStimata;
				@XmlElement(required = true)
				protected String esito;

				/**
				 * Gets the value of the codAzioneFormCal property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getCodAzioneFormCal() {
					return codAzioneFormCal;
				}

				/**
				 * Sets the value of the codAzioneFormCal property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setCodAzioneFormCal(String value) {
					this.codAzioneFormCal = value;
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

				/**
				 * Gets the value of the dataStimata property.
				 * 
				 * @return possible object is {@link XMLGregorianCalendar }
				 * 
				 */
				public XMLGregorianCalendar getDataStimata() {
					return dataStimata;
				}

				/**
				 * Sets the value of the dataStimata property.
				 * 
				 * @param value
				 *            allowed object is {@link XMLGregorianCalendar }
				 * 
				 */
				public void setDataStimata(XMLGregorianCalendar value) {
					this.dataStimata = value;
				}

				/**
				 * Gets the value of the esito property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getEsito() {
					return esito;
				}

				/**
				 * Sets the value of the esito property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setEsito(String value) {
					this.esito = value;
				}

			}

		}

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
	 *         &lt;element name="codiceFiscale" type="{}codice_fiscale_type"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "codiceFiscale" })
	public static class Partecipante {

		@XmlElement(required = true)
		protected String codiceFiscale;

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

	}

}
