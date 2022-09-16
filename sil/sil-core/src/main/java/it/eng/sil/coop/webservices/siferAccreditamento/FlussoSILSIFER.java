//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.02.04 alle 11:07:03 AM CET 
//

package it.eng.sil.coop.webservices.siferAccreditamento;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Classe Java per anonymous complex type.
 * 
 * <p>
 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="partecipante">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="codice_fiscale" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_fiscale_type"/>
 *                   &lt;element name="validita_cf" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}si_no_type"/>
 *                   &lt;element name="codice_fiscale_originale" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_fiscale_type" minOccurs="0"/>
 *                   &lt;element name="cdnlavoratore" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
 *                   &lt;element name="codice_provincia" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_provincia_type"/>
 *                   &lt;element name="cognome" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_40_type"/>
 *                   &lt;element name="nome" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_40_type"/>
 *                   &lt;element name="sesso" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}sesso_type"/>
 *                   &lt;element name="nascita_data" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
 *                   &lt;element name="nascita_codice_istat" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
 *                   &lt;element name="cittadinanza" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_istat_nazione_type"/>
 *                   &lt;element name="recapito_telefonico" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_20_type"/>
 *                   &lt;element name="email" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}email_type" minOccurs="0"/>
 *                   &lt;element name="residenza_codice_istat" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
 *                   &lt;element name="residenza_indirizzo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_60_type"/>
 *                   &lt;element name="residenza_cap" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}cap_type"/>
 *                   &lt;element name="domicilio_codice_istat" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
 *                   &lt;element name="domicilio_indirizzo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_60_type"/>
 *                   &lt;element name="domicilio_cap" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}cap_type"/>
 *                   &lt;element name="dt_mod_anagrafica" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="patti">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="profiling_patto" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="indice_svantaggio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
 *                             &lt;element name="indice_svantaggio_vecchio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
 *                             &lt;element name="indice_data_riferimento" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
 *                             &lt;element name="profiling_150" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
 *                             &lt;element name="profiling_150_p" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}decimal_type" minOccurs="0"/>
 *                             &lt;element name="data_riferimento_150" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
 *                             &lt;element name="patto_data" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
 *                             &lt;element name="patto_protocollo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
 *                             &lt;element name="patto_cpi" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}cpi_type"/>
 *                             &lt;element name="data_chiusura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
 *                             &lt;element name="motivo_chiusura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
 *                             &lt;element name="tipo_misura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
 *                             &lt;element name="data_adesione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
 *                             &lt;element name="anno_programmazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_4_type"/>
 *                             &lt;element name="titolo_studio_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}titolo_studio_patto_type"/>
 *                             &lt;element name="condizione_occupazionale" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}condizione_occupazione_type"/>
 *                             &lt;element name="durata_ricerca_occupazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}durata_ricerca_occupazione_type" minOccurs="0"/>
 *                             &lt;element name="durata_disoccupazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
 *                             &lt;element name="contratto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}contratto_type" minOccurs="0"/>
 *                             &lt;element name="Svantaggi" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="tipo_svantaggio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}tipo_svantaggio_type" maxOccurs="unbounded" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="nome_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
 *                             &lt;element name="cognome_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
 *                             &lt;element name="email_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}email_type" minOccurs="0"/>
 *                             &lt;element name="dt_mod_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
 *                             &lt;element name="PoliticheAttive">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="PoliticaAttiva" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="tipologia_azione_sifer" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_12_type"/>
 *                                                 &lt;element name="misura" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_5_type"/>
 *                                                 &lt;element name="prg_percorso" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
 *                                                 &lt;element name="prg_colloquio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
 *                                                 &lt;element name="durata_effettiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
 *                                                 &lt;element name="tipologia_durata" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_1_type" minOccurs="0"/>
 *                                                 &lt;element name="esito" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
 *                                                 &lt;element name="codice_organismo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
 *                                                 &lt;element name="codice_distretto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
 *                                                 &lt;element name="data_chiusura_politica_attiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
 *                                                 &lt;element name="dt_mod_politica" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
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
@XmlType(name = "", propOrder = { "partecipante", "patti" })
@XmlRootElement(name = "FlussoSIL-SIFER")
public class FlussoSILSIFER {

	@XmlElement(required = true)
	protected FlussoSILSIFER.Partecipante partecipante;
	@XmlElement(required = true)
	protected FlussoSILSIFER.Patti patti;

	/**
	 * Recupera il valore della proprietà partecipante.
	 * 
	 * @return possible object is {@link FlussoSILSIFER.Partecipante }
	 * 
	 */
	public FlussoSILSIFER.Partecipante getPartecipante() {
		return partecipante;
	}

	/**
	 * Imposta il valore della proprietà partecipante.
	 * 
	 * @param value
	 *            allowed object is {@link FlussoSILSIFER.Partecipante }
	 * 
	 */
	public void setPartecipante(FlussoSILSIFER.Partecipante value) {
		this.partecipante = value;
	}

	/**
	 * Recupera il valore della proprietà patti.
	 * 
	 * @return possible object is {@link FlussoSILSIFER.Patti }
	 * 
	 */
	public FlussoSILSIFER.Patti getPatti() {
		return patti;
	}

	/**
	 * Imposta il valore della proprietà patti.
	 * 
	 * @param value
	 *            allowed object is {@link FlussoSILSIFER.Patti }
	 * 
	 */
	public void setPatti(FlussoSILSIFER.Patti value) {
		this.patti = value;
	}

	/**
	 * <p>
	 * Classe Java per anonymous complex type.
	 * 
	 * <p>
	 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="codice_fiscale" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_fiscale_type"/>
	 *         &lt;element name="validita_cf" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}si_no_type"/>
	 *         &lt;element name="codice_fiscale_originale" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_fiscale_type" minOccurs="0"/>
	 *         &lt;element name="cdnlavoratore" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
	 *         &lt;element name="codice_provincia" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_provincia_type"/>
	 *         &lt;element name="cognome" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_40_type"/>
	 *         &lt;element name="nome" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_40_type"/>
	 *         &lt;element name="sesso" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}sesso_type"/>
	 *         &lt;element name="nascita_data" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
	 *         &lt;element name="nascita_codice_istat" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
	 *         &lt;element name="cittadinanza" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}codice_istat_nazione_type"/>
	 *         &lt;element name="recapito_telefonico" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_20_type"/>
	 *         &lt;element name="email" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}email_type" minOccurs="0"/>
	 *         &lt;element name="residenza_codice_istat" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
	 *         &lt;element name="residenza_indirizzo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_60_type"/>
	 *         &lt;element name="residenza_cap" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}cap_type"/>
	 *         &lt;element name="domicilio_codice_istat" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
	 *         &lt;element name="domicilio_indirizzo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_60_type"/>
	 *         &lt;element name="domicilio_cap" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}cap_type"/>
	 *         &lt;element name="dt_mod_anagrafica" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "codiceFiscale", "validitaCf", "codiceFiscaleOriginale", "cdnlavoratore",
			"codiceProvincia", "cognome", "nome", "sesso", "nascitaData", "nascitaCodiceIstat", "cittadinanza",
			"recapitoTelefonico", "email", "residenzaCodiceIstat", "residenzaIndirizzo", "residenzaCap",
			"domicilioCodiceIstat", "domicilioIndirizzo", "domicilioCap", "dtModAnagrafica" })
	public static class Partecipante {

		@XmlElement(name = "codice_fiscale", required = true)
		protected String codiceFiscale;
		@XmlElement(name = "validita_cf", required = true)
		protected String validitaCf;
		@XmlElement(name = "codice_fiscale_originale")
		protected String codiceFiscaleOriginale;
		@XmlElement(required = true)
		@XmlSchemaType(name = "positiveInteger")
		protected BigInteger cdnlavoratore;
		@XmlElement(name = "codice_provincia", required = true)
		protected String codiceProvincia;
		@XmlElement(required = true)
		protected String cognome;
		@XmlElement(required = true)
		protected String nome;
		@XmlElement(required = true)
		protected String sesso;
		@XmlElement(name = "nascita_data", required = true)
		@XmlSchemaType(name = "date")
		protected XMLGregorianCalendar nascitaData;
		@XmlElement(name = "nascita_codice_istat", required = true)
		protected String nascitaCodiceIstat;
		@XmlElement(required = true)
		protected String cittadinanza;
		@XmlElement(name = "recapito_telefonico", required = true)
		protected String recapitoTelefonico;
		protected String email;
		@XmlElement(name = "residenza_codice_istat", required = true)
		protected String residenzaCodiceIstat;
		@XmlElement(name = "residenza_indirizzo", required = true)
		protected String residenzaIndirizzo;
		@XmlElement(name = "residenza_cap", required = true)
		protected String residenzaCap;
		@XmlElement(name = "domicilio_codice_istat", required = true)
		protected String domicilioCodiceIstat;
		@XmlElement(name = "domicilio_indirizzo", required = true)
		protected String domicilioIndirizzo;
		@XmlElement(name = "domicilio_cap", required = true)
		protected String domicilioCap;
		@XmlElement(name = "dt_mod_anagrafica", required = true)
		@XmlSchemaType(name = "date")
		protected XMLGregorianCalendar dtModAnagrafica;

		/**
		 * Recupera il valore della proprietà codiceFiscale.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodiceFiscale() {
			return codiceFiscale;
		}

		/**
		 * Imposta il valore della proprietà codiceFiscale.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodiceFiscale(String value) {
			this.codiceFiscale = value;
		}

		/**
		 * Recupera il valore della proprietà validitaCf.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getValiditaCf() {
			return validitaCf;
		}

		/**
		 * Imposta il valore della proprietà validitaCf.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setValiditaCf(String value) {
			this.validitaCf = value;
		}

		/**
		 * Recupera il valore della proprietà codiceFiscaleOriginale.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodiceFiscaleOriginale() {
			return codiceFiscaleOriginale;
		}

		/**
		 * Imposta il valore della proprietà codiceFiscaleOriginale.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodiceFiscaleOriginale(String value) {
			this.codiceFiscaleOriginale = value;
		}

		/**
		 * Recupera il valore della proprietà cdnlavoratore.
		 * 
		 * @return possible object is {@link BigInteger }
		 * 
		 */
		public BigInteger getCdnlavoratore() {
			return cdnlavoratore;
		}

		/**
		 * Imposta il valore della proprietà cdnlavoratore.
		 * 
		 * @param value
		 *            allowed object is {@link BigInteger }
		 * 
		 */
		public void setCdnlavoratore(BigInteger value) {
			this.cdnlavoratore = value;
		}

		/**
		 * Recupera il valore della proprietà codiceProvincia.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodiceProvincia() {
			return codiceProvincia;
		}

		/**
		 * Imposta il valore della proprietà codiceProvincia.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodiceProvincia(String value) {
			this.codiceProvincia = value;
		}

		/**
		 * Recupera il valore della proprietà cognome.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCognome() {
			return cognome;
		}

		/**
		 * Imposta il valore della proprietà cognome.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCognome(String value) {
			this.cognome = value;
		}

		/**
		 * Recupera il valore della proprietà nome.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getNome() {
			return nome;
		}

		/**
		 * Imposta il valore della proprietà nome.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setNome(String value) {
			this.nome = value;
		}

		/**
		 * Recupera il valore della proprietà sesso.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getSesso() {
			return sesso;
		}

		/**
		 * Imposta il valore della proprietà sesso.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setSesso(String value) {
			this.sesso = value;
		}

		/**
		 * Recupera il valore della proprietà nascitaData.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getNascitaData() {
			return nascitaData;
		}

		/**
		 * Imposta il valore della proprietà nascitaData.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setNascitaData(XMLGregorianCalendar value) {
			this.nascitaData = value;
		}

		/**
		 * Recupera il valore della proprietà nascitaCodiceIstat.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getNascitaCodiceIstat() {
			return nascitaCodiceIstat;
		}

		/**
		 * Imposta il valore della proprietà nascitaCodiceIstat.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setNascitaCodiceIstat(String value) {
			this.nascitaCodiceIstat = value;
		}

		/**
		 * Recupera il valore della proprietà cittadinanza.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCittadinanza() {
			return cittadinanza;
		}

		/**
		 * Imposta il valore della proprietà cittadinanza.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCittadinanza(String value) {
			this.cittadinanza = value;
		}

		/**
		 * Recupera il valore della proprietà recapitoTelefonico.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getRecapitoTelefonico() {
			return recapitoTelefonico;
		}

		/**
		 * Imposta il valore della proprietà recapitoTelefonico.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setRecapitoTelefonico(String value) {
			this.recapitoTelefonico = value;
		}

		/**
		 * Recupera il valore della proprietà email.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * Imposta il valore della proprietà email.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setEmail(String value) {
			this.email = value;
		}

		/**
		 * Recupera il valore della proprietà residenzaCodiceIstat.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getResidenzaCodiceIstat() {
			return residenzaCodiceIstat;
		}

		/**
		 * Imposta il valore della proprietà residenzaCodiceIstat.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setResidenzaCodiceIstat(String value) {
			this.residenzaCodiceIstat = value;
		}

		/**
		 * Recupera il valore della proprietà residenzaIndirizzo.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getResidenzaIndirizzo() {
			return residenzaIndirizzo;
		}

		/**
		 * Imposta il valore della proprietà residenzaIndirizzo.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setResidenzaIndirizzo(String value) {
			this.residenzaIndirizzo = value;
		}

		/**
		 * Recupera il valore della proprietà residenzaCap.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getResidenzaCap() {
			return residenzaCap;
		}

		/**
		 * Imposta il valore della proprietà residenzaCap.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setResidenzaCap(String value) {
			this.residenzaCap = value;
		}

		/**
		 * Recupera il valore della proprietà domicilioCodiceIstat.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDomicilioCodiceIstat() {
			return domicilioCodiceIstat;
		}

		/**
		 * Imposta il valore della proprietà domicilioCodiceIstat.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDomicilioCodiceIstat(String value) {
			this.domicilioCodiceIstat = value;
		}

		/**
		 * Recupera il valore della proprietà domicilioIndirizzo.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDomicilioIndirizzo() {
			return domicilioIndirizzo;
		}

		/**
		 * Imposta il valore della proprietà domicilioIndirizzo.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDomicilioIndirizzo(String value) {
			this.domicilioIndirizzo = value;
		}

		/**
		 * Recupera il valore della proprietà domicilioCap.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDomicilioCap() {
			return domicilioCap;
		}

		/**
		 * Imposta il valore della proprietà domicilioCap.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDomicilioCap(String value) {
			this.domicilioCap = value;
		}

		/**
		 * Recupera il valore della proprietà dtModAnagrafica.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getDtModAnagrafica() {
			return dtModAnagrafica;
		}

		/**
		 * Imposta il valore della proprietà dtModAnagrafica.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setDtModAnagrafica(XMLGregorianCalendar value) {
			this.dtModAnagrafica = value;
		}

	}

	/**
	 * <p>
	 * Classe Java per anonymous complex type.
	 * 
	 * <p>
	 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="profiling_patto" maxOccurs="unbounded">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="indice_svantaggio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
	 *                   &lt;element name="indice_svantaggio_vecchio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
	 *                   &lt;element name="indice_data_riferimento" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
	 *                   &lt;element name="profiling_150" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
	 *                   &lt;element name="profiling_150_p" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}decimal_type" minOccurs="0"/>
	 *                   &lt;element name="data_riferimento_150" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
	 *                   &lt;element name="patto_data" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
	 *                   &lt;element name="patto_protocollo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
	 *                   &lt;element name="patto_cpi" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}cpi_type"/>
	 *                   &lt;element name="data_chiusura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
	 *                   &lt;element name="motivo_chiusura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
	 *                   &lt;element name="tipo_misura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
	 *                   &lt;element name="data_adesione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
	 *                   &lt;element name="anno_programmazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_4_type"/>
	 *                   &lt;element name="titolo_studio_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}titolo_studio_patto_type"/>
	 *                   &lt;element name="condizione_occupazionale" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}condizione_occupazione_type"/>
	 *                   &lt;element name="durata_ricerca_occupazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}durata_ricerca_occupazione_type" minOccurs="0"/>
	 *                   &lt;element name="durata_disoccupazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
	 *                   &lt;element name="contratto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}contratto_type" minOccurs="0"/>
	 *                   &lt;element name="Svantaggi" minOccurs="0">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="tipo_svantaggio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}tipo_svantaggio_type" maxOccurs="unbounded" minOccurs="0"/>
	 *                           &lt;/sequence>
	 *                         &lt;/restriction>
	 *                       &lt;/complexContent>
	 *                     &lt;/complexType>
	 *                   &lt;/element>
	 *                   &lt;element name="nome_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
	 *                   &lt;element name="cognome_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
	 *                   &lt;element name="email_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}email_type" minOccurs="0"/>
	 *                   &lt;element name="dt_mod_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
	 *                   &lt;element name="PoliticheAttive">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="PoliticaAttiva" maxOccurs="unbounded">
	 *                               &lt;complexType>
	 *                                 &lt;complexContent>
	 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                                     &lt;sequence>
	 *                                       &lt;element name="tipologia_azione_sifer" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_12_type"/>
	 *                                       &lt;element name="misura" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_5_type"/>
	 *                                       &lt;element name="prg_percorso" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
	 *                                       &lt;element name="prg_colloquio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
	 *                                       &lt;element name="durata_effettiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
	 *                                       &lt;element name="tipologia_durata" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_1_type" minOccurs="0"/>
	 *                                       &lt;element name="esito" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
	 *                                       &lt;element name="codice_organismo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
	 *                                       &lt;element name="codice_distretto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
	 *                                       &lt;element name="data_chiusura_politica_attiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
	 *                                       &lt;element name="dt_mod_politica" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
	 *                                     &lt;/sequence>
	 *                                   &lt;/restriction>
	 *                                 &lt;/complexContent>
	 *                               &lt;/complexType>
	 *                             &lt;/element>
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
	@XmlType(name = "", propOrder = { "profilingPatto" })
	public static class Patti {

		@XmlElement(name = "profiling_patto", required = true)
		protected List<FlussoSILSIFER.Patti.ProfilingPatto> profilingPatto;

		/**
		 * Gets the value of the profilingPatto property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
		 * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
		 * method for the profilingPatto property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getProfilingPatto().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link FlussoSILSIFER.Patti.ProfilingPatto }
		 * 
		 * 
		 */
		public List<FlussoSILSIFER.Patti.ProfilingPatto> getProfilingPatto() {
			if (profilingPatto == null) {
				profilingPatto = new ArrayList<FlussoSILSIFER.Patti.ProfilingPatto>();
			}
			return this.profilingPatto;
		}

		/**
		 * <p>
		 * Classe Java per anonymous complex type.
		 * 
		 * <p>
		 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="indice_svantaggio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
		 *         &lt;element name="indice_svantaggio_vecchio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
		 *         &lt;element name="indice_data_riferimento" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
		 *         &lt;element name="profiling_150" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
		 *         &lt;element name="profiling_150_p" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}decimal_type" minOccurs="0"/>
		 *         &lt;element name="data_riferimento_150" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
		 *         &lt;element name="patto_data" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
		 *         &lt;element name="patto_protocollo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
		 *         &lt;element name="patto_cpi" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}cpi_type"/>
		 *         &lt;element name="data_chiusura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
		 *         &lt;element name="motivo_chiusura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
		 *         &lt;element name="tipo_misura_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
		 *         &lt;element name="data_adesione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
		 *         &lt;element name="anno_programmazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_4_type"/>
		 *         &lt;element name="titolo_studio_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}titolo_studio_patto_type"/>
		 *         &lt;element name="condizione_occupazionale" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}condizione_occupazione_type"/>
		 *         &lt;element name="durata_ricerca_occupazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}durata_ricerca_occupazione_type" minOccurs="0"/>
		 *         &lt;element name="durata_disoccupazione" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
		 *         &lt;element name="contratto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}contratto_type" minOccurs="0"/>
		 *         &lt;element name="Svantaggi" minOccurs="0">
		 *           &lt;complexType>
		 *             &lt;complexContent>
		 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                 &lt;sequence>
		 *                   &lt;element name="tipo_svantaggio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}tipo_svantaggio_type" maxOccurs="unbounded" minOccurs="0"/>
		 *                 &lt;/sequence>
		 *               &lt;/restriction>
		 *             &lt;/complexContent>
		 *           &lt;/complexType>
		 *         &lt;/element>
		 *         &lt;element name="nome_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
		 *         &lt;element name="cognome_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_500_type" minOccurs="0"/>
		 *         &lt;element name="email_responsabile" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}email_type" minOccurs="0"/>
		 *         &lt;element name="dt_mod_patto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
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
		 *                             &lt;element name="tipologia_azione_sifer" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_12_type"/>
		 *                             &lt;element name="misura" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_5_type"/>
		 *                             &lt;element name="prg_percorso" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
		 *                             &lt;element name="prg_colloquio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
		 *                             &lt;element name="durata_effettiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
		 *                             &lt;element name="tipologia_durata" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_1_type" minOccurs="0"/>
		 *                             &lt;element name="esito" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
		 *                             &lt;element name="codice_organismo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
		 *                             &lt;element name="codice_distretto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
		 *                             &lt;element name="data_chiusura_politica_attiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
		 *                             &lt;element name="dt_mod_politica" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
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
		@XmlType(name = "", propOrder = { "indiceSvantaggio", "indiceSvantaggioVecchio", "indiceDataRiferimento",
				"profiling150", "profiling150P", "dataRiferimento150", "pattoData", "pattoProtocollo", "pattoCpi",
				"dataChiusuraPatto", "motivoChiusuraPatto", "tipoMisuraPatto", "dataAdesione", "annoProgrammazione",
				"titoloStudioPatto", "condizioneOccupazionale", "durataRicercaOccupazione", "durataDisoccupazione",
				"contratto", "svantaggi", "nomeResponsabile", "cognomeResponsabile", "emailResponsabile", "dtModPatto",
				"politicheAttive" })
		public static class ProfilingPatto {

			@XmlElement(name = "indice_svantaggio")
			@XmlSchemaType(name = "positiveInteger")
			protected BigInteger indiceSvantaggio;
			@XmlElement(name = "indice_svantaggio_vecchio")
			@XmlSchemaType(name = "positiveInteger")
			protected BigInteger indiceSvantaggioVecchio;
			@XmlElement(name = "indice_data_riferimento")
			@XmlSchemaType(name = "date")
			protected XMLGregorianCalendar indiceDataRiferimento;
			@XmlElement(name = "profiling_150")
			@XmlSchemaType(name = "positiveInteger")
			protected BigInteger profiling150;
			@XmlElement(name = "profiling_150_p")
			protected BigDecimal profiling150P;
			@XmlElement(name = "data_riferimento_150")
			@XmlSchemaType(name = "date")
			protected XMLGregorianCalendar dataRiferimento150;
			@XmlElement(name = "patto_data", required = true)
			@XmlSchemaType(name = "date")
			protected XMLGregorianCalendar pattoData;
			@XmlElement(name = "patto_protocollo", required = true)
			@XmlSchemaType(name = "positiveInteger")
			protected BigInteger pattoProtocollo;
			@XmlElement(name = "patto_cpi", required = true)
			protected String pattoCpi;
			@XmlElement(name = "data_chiusura_patto")
			@XmlSchemaType(name = "date")
			protected XMLGregorianCalendar dataChiusuraPatto;
			@XmlElement(name = "motivo_chiusura_patto")
			protected String motivoChiusuraPatto;
			@XmlElement(name = "tipo_misura_patto", required = true)
			protected String tipoMisuraPatto;
			@XmlElement(name = "data_adesione")
			@XmlSchemaType(name = "date")
			protected XMLGregorianCalendar dataAdesione;
			@XmlElement(name = "anno_programmazione", required = true)
			protected String annoProgrammazione;
			@XmlElement(name = "titolo_studio_patto", required = true)
			protected String titoloStudioPatto;
			@XmlElement(name = "condizione_occupazionale", required = true)
			protected String condizioneOccupazionale;
			@XmlElement(name = "durata_ricerca_occupazione")
			protected String durataRicercaOccupazione;
			@XmlElement(name = "durata_disoccupazione")
			@XmlSchemaType(name = "positiveInteger")
			protected BigInteger durataDisoccupazione;
			protected String contratto;
			@XmlElement(name = "Svantaggi")
			protected FlussoSILSIFER.Patti.ProfilingPatto.Svantaggi svantaggi;
			@XmlElement(name = "nome_responsabile")
			protected String nomeResponsabile;
			@XmlElement(name = "cognome_responsabile")
			protected String cognomeResponsabile;
			@XmlElement(name = "email_responsabile")
			protected String emailResponsabile;
			@XmlElement(name = "dt_mod_patto", required = true)
			@XmlSchemaType(name = "date")
			protected XMLGregorianCalendar dtModPatto;
			@XmlElement(name = "PoliticheAttive", required = true)
			protected FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive politicheAttive;

			/**
			 * Recupera il valore della proprietà indiceSvantaggio.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getIndiceSvantaggio() {
				return indiceSvantaggio;
			}

			/**
			 * Imposta il valore della proprietà indiceSvantaggio.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setIndiceSvantaggio(BigInteger value) {
				this.indiceSvantaggio = value;
			}

			/**
			 * Recupera il valore della proprietà indiceSvantaggioVecchio.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getIndiceSvantaggioVecchio() {
				return indiceSvantaggioVecchio;
			}

			/**
			 * Imposta il valore della proprietà indiceSvantaggioVecchio.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setIndiceSvantaggioVecchio(BigInteger value) {
				this.indiceSvantaggioVecchio = value;
			}

			/**
			 * Recupera il valore della proprietà indiceDataRiferimento.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getIndiceDataRiferimento() {
				return indiceDataRiferimento;
			}

			/**
			 * Imposta il valore della proprietà indiceDataRiferimento.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setIndiceDataRiferimento(XMLGregorianCalendar value) {
				this.indiceDataRiferimento = value;
			}

			/**
			 * Recupera il valore della proprietà profiling150.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getProfiling150() {
				return profiling150;
			}

			/**
			 * Imposta il valore della proprietà profiling150.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setProfiling150(BigInteger value) {
				this.profiling150 = value;
			}

			/**
			 * Recupera il valore della proprietà profiling150P.
			 * 
			 * @return possible object is {@link BigDecimal }
			 * 
			 */
			public BigDecimal getProfiling150P() {
				return profiling150P;
			}

			/**
			 * Imposta il valore della proprietà profiling150P.
			 * 
			 * @param value
			 *            allowed object is {@link BigDecimal }
			 * 
			 */
			public void setProfiling150P(BigDecimal value) {
				this.profiling150P = value;
			}

			/**
			 * Recupera il valore della proprietà dataRiferimento150.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getDataRiferimento150() {
				return dataRiferimento150;
			}

			/**
			 * Imposta il valore della proprietà dataRiferimento150.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setDataRiferimento150(XMLGregorianCalendar value) {
				this.dataRiferimento150 = value;
			}

			/**
			 * Recupera il valore della proprietà pattoData.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getPattoData() {
				return pattoData;
			}

			/**
			 * Imposta il valore della proprietà pattoData.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setPattoData(XMLGregorianCalendar value) {
				this.pattoData = value;
			}

			/**
			 * Recupera il valore della proprietà pattoProtocollo.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getPattoProtocollo() {
				return pattoProtocollo;
			}

			/**
			 * Imposta il valore della proprietà pattoProtocollo.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setPattoProtocollo(BigInteger value) {
				this.pattoProtocollo = value;
			}

			/**
			 * Recupera il valore della proprietà pattoCpi.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getPattoCpi() {
				return pattoCpi;
			}

			/**
			 * Imposta il valore della proprietà pattoCpi.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setPattoCpi(String value) {
				this.pattoCpi = value;
			}

			/**
			 * Recupera il valore della proprietà dataChiusuraPatto.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getDataChiusuraPatto() {
				return dataChiusuraPatto;
			}

			/**
			 * Imposta il valore della proprietà dataChiusuraPatto.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setDataChiusuraPatto(XMLGregorianCalendar value) {
				this.dataChiusuraPatto = value;
			}

			/**
			 * Recupera il valore della proprietà motivoChiusuraPatto.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getMotivoChiusuraPatto() {
				return motivoChiusuraPatto;
			}

			/**
			 * Imposta il valore della proprietà motivoChiusuraPatto.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setMotivoChiusuraPatto(String value) {
				this.motivoChiusuraPatto = value;
			}

			/**
			 * Recupera il valore della proprietà tipoMisuraPatto.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getTipoMisuraPatto() {
				return tipoMisuraPatto;
			}

			/**
			 * Imposta il valore della proprietà tipoMisuraPatto.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setTipoMisuraPatto(String value) {
				this.tipoMisuraPatto = value;
			}

			/**
			 * Recupera il valore della proprietà dataAdesione.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getDataAdesione() {
				return dataAdesione;
			}

			/**
			 * Imposta il valore della proprietà dataAdesione.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setDataAdesione(XMLGregorianCalendar value) {
				this.dataAdesione = value;
			}

			/**
			 * Recupera il valore della proprietà annoProgrammazione.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getAnnoProgrammazione() {
				return annoProgrammazione;
			}

			/**
			 * Imposta il valore della proprietà annoProgrammazione.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setAnnoProgrammazione(String value) {
				this.annoProgrammazione = value;
			}

			/**
			 * Recupera il valore della proprietà titoloStudioPatto.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getTitoloStudioPatto() {
				return titoloStudioPatto;
			}

			/**
			 * Imposta il valore della proprietà titoloStudioPatto.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setTitoloStudioPatto(String value) {
				this.titoloStudioPatto = value;
			}

			/**
			 * Recupera il valore della proprietà condizioneOccupazionale.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getCondizioneOccupazionale() {
				return condizioneOccupazionale;
			}

			/**
			 * Imposta il valore della proprietà condizioneOccupazionale.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setCondizioneOccupazionale(String value) {
				this.condizioneOccupazionale = value;
			}

			/**
			 * Recupera il valore della proprietà durataRicercaOccupazione.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getDurataRicercaOccupazione() {
				return durataRicercaOccupazione;
			}

			/**
			 * Imposta il valore della proprietà durataRicercaOccupazione.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setDurataRicercaOccupazione(String value) {
				this.durataRicercaOccupazione = value;
			}

			/**
			 * Recupera il valore della proprietà durataDisoccupazione.
			 * 
			 * @return possible object is {@link BigInteger }
			 * 
			 */
			public BigInteger getDurataDisoccupazione() {
				return durataDisoccupazione;
			}

			/**
			 * Imposta il valore della proprietà durataDisoccupazione.
			 * 
			 * @param value
			 *            allowed object is {@link BigInteger }
			 * 
			 */
			public void setDurataDisoccupazione(BigInteger value) {
				this.durataDisoccupazione = value;
			}

			/**
			 * Recupera il valore della proprietà contratto.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getContratto() {
				return contratto;
			}

			/**
			 * Imposta il valore della proprietà contratto.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setContratto(String value) {
				this.contratto = value;
			}

			/**
			 * Recupera il valore della proprietà svantaggi.
			 * 
			 * @return possible object is {@link FlussoSILSIFER.Patti.ProfilingPatto.Svantaggi }
			 * 
			 */
			public FlussoSILSIFER.Patti.ProfilingPatto.Svantaggi getSvantaggi() {
				return svantaggi;
			}

			/**
			 * Imposta il valore della proprietà svantaggi.
			 * 
			 * @param value
			 *            allowed object is {@link FlussoSILSIFER.Patti.ProfilingPatto.Svantaggi }
			 * 
			 */
			public void setSvantaggi(FlussoSILSIFER.Patti.ProfilingPatto.Svantaggi value) {
				this.svantaggi = value;
			}

			/**
			 * Recupera il valore della proprietà nomeResponsabile.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getNomeResponsabile() {
				return nomeResponsabile;
			}

			/**
			 * Imposta il valore della proprietà nomeResponsabile.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setNomeResponsabile(String value) {
				this.nomeResponsabile = value;
			}

			/**
			 * Recupera il valore della proprietà cognomeResponsabile.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getCognomeResponsabile() {
				return cognomeResponsabile;
			}

			/**
			 * Imposta il valore della proprietà cognomeResponsabile.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setCognomeResponsabile(String value) {
				this.cognomeResponsabile = value;
			}

			/**
			 * Recupera il valore della proprietà emailResponsabile.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getEmailResponsabile() {
				return emailResponsabile;
			}

			/**
			 * Imposta il valore della proprietà emailResponsabile.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setEmailResponsabile(String value) {
				this.emailResponsabile = value;
			}

			/**
			 * Recupera il valore della proprietà dtModPatto.
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getDtModPatto() {
				return dtModPatto;
			}

			/**
			 * Imposta il valore della proprietà dtModPatto.
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setDtModPatto(XMLGregorianCalendar value) {
				this.dtModPatto = value;
			}

			/**
			 * Recupera il valore della proprietà politicheAttive.
			 * 
			 * @return possible object is {@link FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive }
			 * 
			 */
			public FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive getPoliticheAttive() {
				return politicheAttive;
			}

			/**
			 * Imposta il valore della proprietà politicheAttive.
			 * 
			 * @param value
			 *            allowed object is {@link FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive }
			 * 
			 */
			public void setPoliticheAttive(FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive value) {
				this.politicheAttive = value;
			}

			/**
			 * <p>
			 * Classe Java per anonymous complex type.
			 * 
			 * <p>
			 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
			 *                   &lt;element name="tipologia_azione_sifer" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_12_type"/>
			 *                   &lt;element name="misura" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_5_type"/>
			 *                   &lt;element name="prg_percorso" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
			 *                   &lt;element name="prg_colloquio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
			 *                   &lt;element name="durata_effettiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
			 *                   &lt;element name="tipologia_durata" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_1_type" minOccurs="0"/>
			 *                   &lt;element name="esito" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
			 *                   &lt;element name="codice_organismo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
			 *                   &lt;element name="codice_distretto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
			 *                   &lt;element name="data_chiusura_politica_attiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
			 *                   &lt;element name="dt_mod_politica" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
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
				protected List<FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva> politicaAttiva;

				/**
				 * Gets the value of the politicaAttiva property.
				 * 
				 * <p>
				 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
				 * you make to the returned list will be present inside the JAXB object. This is why there is not a
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
				 * Objects of the following type(s) are allowed in the list
				 * {@link FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva }
				 * 
				 * 
				 */
				public List<FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva> getPoliticaAttiva() {
					if (politicaAttiva == null) {
						politicaAttiva = new ArrayList<FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva>();
					}
					return this.politicaAttiva;
				}

				/**
				 * <p>
				 * Classe Java per anonymous complex type.
				 * 
				 * <p>
				 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
				 * 
				 * <pre>
				 * &lt;complexType>
				 *   &lt;complexContent>
				 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
				 *       &lt;sequence>
				 *         &lt;element name="tipologia_azione_sifer" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_12_type"/>
				 *         &lt;element name="misura" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_5_type"/>
				 *         &lt;element name="prg_percorso" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
				 *         &lt;element name="prg_colloquio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
				 *         &lt;element name="durata_effettiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
				 *         &lt;element name="tipologia_durata" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_1_type" minOccurs="0"/>
				 *         &lt;element name="esito" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}varchar_8_type"/>
				 *         &lt;element name="codice_organismo" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type"/>
				 *         &lt;element name="codice_distretto" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}int_type" minOccurs="0"/>
				 *         &lt;element name="data_chiusura_politica_attiva" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type" minOccurs="0"/>
				 *         &lt;element name="dt_mod_politica" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}date_type"/>
				 *       &lt;/sequence>
				 *     &lt;/restriction>
				 *   &lt;/complexContent>
				 * &lt;/complexType>
				 * </pre>
				 * 
				 * 
				 */
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "tipologiaAzioneSifer", "misura", "prgPercorso", "prgColloquio",
						"durataEffettiva", "tipologiaDurata", "esito", "codiceOrganismo", "codiceDistretto",
						"dataChiusuraPoliticaAttiva", "dtModPolitica" })
				public static class PoliticaAttiva {

					@XmlElement(name = "tipologia_azione_sifer", required = true)
					protected String tipologiaAzioneSifer;
					@XmlElement(required = true)
					protected String misura;
					@XmlElement(name = "prg_percorso", required = true)
					@XmlSchemaType(name = "positiveInteger")
					protected BigInteger prgPercorso;
					@XmlElement(name = "prg_colloquio", required = true)
					@XmlSchemaType(name = "positiveInteger")
					protected BigInteger prgColloquio;
					@XmlElement(name = "durata_effettiva")
					@XmlSchemaType(name = "positiveInteger")
					protected BigInteger durataEffettiva;
					@XmlElement(name = "tipologia_durata")
					protected String tipologiaDurata;
					@XmlElement(required = true)
					protected String esito;
					@XmlElement(name = "codice_organismo", required = true)
					@XmlSchemaType(name = "positiveInteger")
					protected BigInteger codiceOrganismo;
					@XmlElement(name = "codice_distretto")
					@XmlSchemaType(name = "positiveInteger")
					protected BigInteger codiceDistretto;
					@XmlElement(name = "data_chiusura_politica_attiva")
					@XmlSchemaType(name = "date")
					protected XMLGregorianCalendar dataChiusuraPoliticaAttiva;
					@XmlElement(name = "dt_mod_politica", required = true)
					@XmlSchemaType(name = "date")
					protected XMLGregorianCalendar dtModPolitica;

					/**
					 * Recupera il valore della proprietà tipologiaAzioneSifer.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getTipologiaAzioneSifer() {
						return tipologiaAzioneSifer;
					}

					/**
					 * Imposta il valore della proprietà tipologiaAzioneSifer.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setTipologiaAzioneSifer(String value) {
						this.tipologiaAzioneSifer = value;
					}

					/**
					 * Recupera il valore della proprietà misura.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getMisura() {
						return misura;
					}

					/**
					 * Imposta il valore della proprietà misura.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setMisura(String value) {
						this.misura = value;
					}

					/**
					 * Recupera il valore della proprietà prgPercorso.
					 * 
					 * @return possible object is {@link BigInteger }
					 * 
					 */
					public BigInteger getPrgPercorso() {
						return prgPercorso;
					}

					/**
					 * Imposta il valore della proprietà prgPercorso.
					 * 
					 * @param value
					 *            allowed object is {@link BigInteger }
					 * 
					 */
					public void setPrgPercorso(BigInteger value) {
						this.prgPercorso = value;
					}

					/**
					 * Recupera il valore della proprietà prgColloquio.
					 * 
					 * @return possible object is {@link BigInteger }
					 * 
					 */
					public BigInteger getPrgColloquio() {
						return prgColloquio;
					}

					/**
					 * Imposta il valore della proprietà prgColloquio.
					 * 
					 * @param value
					 *            allowed object is {@link BigInteger }
					 * 
					 */
					public void setPrgColloquio(BigInteger value) {
						this.prgColloquio = value;
					}

					/**
					 * Recupera il valore della proprietà durataEffettiva.
					 * 
					 * @return possible object is {@link BigInteger }
					 * 
					 */
					public BigInteger getDurataEffettiva() {
						return durataEffettiva;
					}

					/**
					 * Imposta il valore della proprietà durataEffettiva.
					 * 
					 * @param value
					 *            allowed object is {@link BigInteger }
					 * 
					 */
					public void setDurataEffettiva(BigInteger value) {
						this.durataEffettiva = value;
					}

					/**
					 * Recupera il valore della proprietà tipologiaDurata.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getTipologiaDurata() {
						return tipologiaDurata;
					}

					/**
					 * Imposta il valore della proprietà tipologiaDurata.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setTipologiaDurata(String value) {
						this.tipologiaDurata = value;
					}

					/**
					 * Recupera il valore della proprietà esito.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getEsito() {
						return esito;
					}

					/**
					 * Imposta il valore della proprietà esito.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setEsito(String value) {
						this.esito = value;
					}

					/**
					 * Recupera il valore della proprietà codiceOrganismo.
					 * 
					 * @return possible object is {@link BigInteger }
					 * 
					 */
					public BigInteger getCodiceOrganismo() {
						return codiceOrganismo;
					}

					/**
					 * Imposta il valore della proprietà codiceOrganismo.
					 * 
					 * @param value
					 *            allowed object is {@link BigInteger }
					 * 
					 */
					public void setCodiceOrganismo(BigInteger value) {
						this.codiceOrganismo = value;
					}

					/**
					 * Recupera il valore della proprietà codiceDistretto.
					 * 
					 * @return possible object is {@link BigInteger }
					 * 
					 */
					public BigInteger getCodiceDistretto() {
						return codiceDistretto;
					}

					/**
					 * Imposta il valore della proprietà codiceDistretto.
					 * 
					 * @param value
					 *            allowed object is {@link BigInteger }
					 * 
					 */
					public void setCodiceDistretto(BigInteger value) {
						this.codiceDistretto = value;
					}

					/**
					 * Recupera il valore della proprietà dataChiusuraPoliticaAttiva.
					 * 
					 * @return possible object is {@link XMLGregorianCalendar }
					 * 
					 */
					public XMLGregorianCalendar getDataChiusuraPoliticaAttiva() {
						return dataChiusuraPoliticaAttiva;
					}

					/**
					 * Imposta il valore della proprietà dataChiusuraPoliticaAttiva.
					 * 
					 * @param value
					 *            allowed object is {@link XMLGregorianCalendar }
					 * 
					 */
					public void setDataChiusuraPoliticaAttiva(XMLGregorianCalendar value) {
						this.dataChiusuraPoliticaAttiva = value;
					}

					/**
					 * Recupera il valore della proprietà dtModPolitica.
					 * 
					 * @return possible object is {@link XMLGregorianCalendar }
					 * 
					 */
					public XMLGregorianCalendar getDtModPolitica() {
						return dtModPolitica;
					}

					/**
					 * Imposta il valore della proprietà dtModPolitica.
					 * 
					 * @param value
					 *            allowed object is {@link XMLGregorianCalendar }
					 * 
					 */
					public void setDtModPolitica(XMLGregorianCalendar value) {
						this.dtModPolitica = value;
					}

				}

			}

			/**
			 * <p>
			 * Classe Java per anonymous complex type.
			 * 
			 * <p>
			 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
			 * 
			 * <pre>
			 * &lt;complexType>
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="tipo_svantaggio" type="{https://sifer.regione.emilia-romagna.it/WebService/GaranziaGiovani/PartecipanteGaranziaGiovani}tipo_svantaggio_type" maxOccurs="unbounded" minOccurs="0"/>
			 *       &lt;/sequence>
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "tipoSvantaggio" })
			public static class Svantaggi {

				@XmlElement(name = "tipo_svantaggio")
				protected List<String> tipoSvantaggio;

				/**
				 * Gets the value of the tipoSvantaggio property.
				 * 
				 * <p>
				 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
				 * you make to the returned list will be present inside the JAXB object. This is why there is not a
				 * <CODE>set</CODE> method for the tipoSvantaggio property.
				 * 
				 * <p>
				 * For example, to add a new item, do as follows:
				 * 
				 * <pre>
				 * getTipoSvantaggio().add(newItem);
				 * </pre>
				 * 
				 * 
				 * <p>
				 * Objects of the following type(s) are allowed in the list {@link String }
				 * 
				 * 
				 */
				public List<String> getTipoSvantaggio() {
					if (tipoSvantaggio == null) {
						tipoSvantaggio = new ArrayList<String>();
					}
					return this.tipoSvantaggio;
				}

			}

		}

	}

}
