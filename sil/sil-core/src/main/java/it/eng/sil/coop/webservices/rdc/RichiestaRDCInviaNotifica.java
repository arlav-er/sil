//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.11.27 at 10:55:38 AM CET 
//

package it.eng.sil.coop.webservices.rdc;

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
 *         &lt;element name="datiInvio">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="IdComunicazione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DataInvio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="invioRDC">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cod_cap_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cod_cap_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cod_cittadinanza" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cod_comune_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cod_comune_nascita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cod_comune_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cod_fiscale" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cod_fiscale_richiedente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cod_protocollo_inps" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cod_ruolo_beneficiario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cod_sesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cod_stato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="des_cognome" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="des_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="des_indirizzo_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="des_indirizzo_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="des_nome" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="des_telefono" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="dtt_domanda" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="dtt_nascita" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="dtt_rendicontazione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="des_comune_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="des_comune_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="des_comune_nascita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cod_sap" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cod_cpi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fonte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="dtt_trasformazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="tipo_domanda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="dtt_variazione_stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "datiInvio", "invioRDC" })
@XmlRootElement(name = "richiesta_RDC_InviaNotifica")
public class RichiestaRDCInviaNotifica {

	@XmlElement(required = true)
	protected RichiestaRDCInviaNotifica.DatiInvio datiInvio;
	@XmlElement(required = true)
	protected RichiestaRDCInviaNotifica.InvioRDC invioRDC;

	/**
	 * Gets the value of the datiInvio property.
	 * 
	 * @return possible object is {@link RichiestaRDCInviaNotifica.DatiInvio }
	 * 
	 */
	public RichiestaRDCInviaNotifica.DatiInvio getDatiInvio() {
		return datiInvio;
	}

	/**
	 * Sets the value of the datiInvio property.
	 * 
	 * @param value
	 *            allowed object is {@link RichiestaRDCInviaNotifica.DatiInvio }
	 * 
	 */
	public void setDatiInvio(RichiestaRDCInviaNotifica.DatiInvio value) {
		this.datiInvio = value;
	}

	/**
	 * Gets the value of the invioRDC property.
	 * 
	 * @return possible object is {@link RichiestaRDCInviaNotifica.InvioRDC }
	 * 
	 */
	public RichiestaRDCInviaNotifica.InvioRDC getInvioRDC() {
		return invioRDC;
	}

	/**
	 * Sets the value of the invioRDC property.
	 * 
	 * @param value
	 *            allowed object is {@link RichiestaRDCInviaNotifica.InvioRDC }
	 * 
	 */
	public void setInvioRDC(RichiestaRDCInviaNotifica.InvioRDC value) {
		this.invioRDC = value;
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
	 *         &lt;element name="IdComunicazione" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="DataInvio" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "idComunicazione", "dataInvio" })
	public static class DatiInvio {

		@XmlElement(name = "IdComunicazione", required = true)
		protected String idComunicazione;
		@XmlElement(name = "DataInvio", required = true)
		protected String dataInvio;

		/**
		 * Gets the value of the idComunicazione property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getIdComunicazione() {
			return idComunicazione;
		}

		/**
		 * Sets the value of the idComunicazione property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setIdComunicazione(String value) {
			this.idComunicazione = value;
		}

		/**
		 * Gets the value of the dataInvio property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDataInvio() {
			return dataInvio;
		}

		/**
		 * Sets the value of the dataInvio property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDataInvio(String value) {
			this.dataInvio = value;
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
	 *         &lt;element name="cod_cap_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="cod_cap_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="cod_cittadinanza" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="cod_comune_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="cod_comune_nascita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="cod_comune_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="cod_fiscale" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="cod_fiscale_richiedente" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="cod_protocollo_inps" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="cod_ruolo_beneficiario" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="cod_sesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="cod_stato" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="des_cognome" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="des_email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="des_indirizzo_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="des_indirizzo_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="des_nome" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="des_telefono" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="dtt_domanda" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="dtt_nascita" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="dtt_rendicontazione" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="des_comune_domicilio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="des_comune_residenza" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="des_comune_nascita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="cod_sap" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="cod_cpi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="fonte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="dtt_trasformazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="tipo_domanda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="dtt_variazione_stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "codCapDomicilio", "codCapResidenza", "codCittadinanza", "codComuneDomicilio",
			"codComuneNascita", "codComuneResidenza", "codFiscale", "codFiscaleRichiedente", "codProtocolloInps",
			"codRuoloBeneficiario", "codSesso", "codStato", "desCognome", "desEmail", "desIndirizzoDomicilio",
			"desIndirizzoResidenza", "desNome", "desTelefono", "dttDomanda", "dttNascita", "dttRendicontazione",
			"desComuneDomicilio", "desComuneResidenza", "desComuneNascita", "codSap", "codCpi", "fonte",
			"dttTrasformazione", "tipoDomanda", "dttVariazioneStato" })
	public static class InvioRDC {

		@XmlElement(name = "cod_cap_domicilio")
		protected String codCapDomicilio;
		@XmlElement(name = "cod_cap_residenza", required = true)
		protected String codCapResidenza;
		@XmlElement(name = "cod_cittadinanza")
		protected String codCittadinanza;
		@XmlElement(name = "cod_comune_domicilio")
		protected String codComuneDomicilio;
		@XmlElement(name = "cod_comune_nascita")
		protected String codComuneNascita;
		@XmlElement(name = "cod_comune_residenza", required = true)
		protected String codComuneResidenza;
		@XmlElement(name = "cod_fiscale", required = true)
		protected String codFiscale;
		@XmlElement(name = "cod_fiscale_richiedente", required = true)
		protected String codFiscaleRichiedente;
		@XmlElement(name = "cod_protocollo_inps", required = true)
		protected String codProtocolloInps;
		@XmlElement(name = "cod_ruolo_beneficiario", required = true)
		protected String codRuoloBeneficiario;
		@XmlElement(name = "cod_sesso")
		protected String codSesso;
		@XmlElement(name = "cod_stato", required = true)
		protected String codStato;
		@XmlElement(name = "des_cognome", required = true)
		protected String desCognome;
		@XmlElement(name = "des_email")
		protected String desEmail;
		@XmlElement(name = "des_indirizzo_domicilio")
		protected String desIndirizzoDomicilio;
		@XmlElement(name = "des_indirizzo_residenza", required = true)
		protected String desIndirizzoResidenza;
		@XmlElement(name = "des_nome", required = true)
		protected String desNome;
		@XmlElement(name = "des_telefono")
		protected String desTelefono;
		@XmlElement(name = "dtt_domanda", required = true)
		protected String dttDomanda;
		@XmlElement(name = "dtt_nascita", required = true)
		protected String dttNascita;
		@XmlElement(name = "dtt_rendicontazione", required = true)
		protected String dttRendicontazione;
		@XmlElement(name = "des_comune_domicilio")
		protected String desComuneDomicilio;
		@XmlElement(name = "des_comune_residenza", required = true)
		protected String desComuneResidenza;
		@XmlElement(name = "des_comune_nascita")
		protected String desComuneNascita;
		@XmlElement(name = "cod_sap")
		protected String codSap;
		@XmlElement(name = "cod_cpi")
		protected String codCpi;
		protected String fonte;
		@XmlElement(name = "dtt_trasformazione")
		protected String dttTrasformazione;
		@XmlElement(name = "tipo_domanda")
		protected String tipoDomanda;
		@XmlElement(name = "dtt_variazione_stato")
		protected String dttVariazioneStato;

		/**
		 * Gets the value of the codCapDomicilio property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodCapDomicilio() {
			return codCapDomicilio;
		}

		/**
		 * Sets the value of the codCapDomicilio property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodCapDomicilio(String value) {
			this.codCapDomicilio = value;
		}

		/**
		 * Gets the value of the codCapResidenza property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodCapResidenza() {
			return codCapResidenza;
		}

		/**
		 * Sets the value of the codCapResidenza property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodCapResidenza(String value) {
			this.codCapResidenza = value;
		}

		/**
		 * Gets the value of the codCittadinanza property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodCittadinanza() {
			return codCittadinanza;
		}

		/**
		 * Sets the value of the codCittadinanza property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodCittadinanza(String value) {
			this.codCittadinanza = value;
		}

		/**
		 * Gets the value of the codComuneDomicilio property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodComuneDomicilio() {
			return codComuneDomicilio;
		}

		/**
		 * Sets the value of the codComuneDomicilio property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodComuneDomicilio(String value) {
			this.codComuneDomicilio = value;
		}

		/**
		 * Gets the value of the codComuneNascita property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodComuneNascita() {
			return codComuneNascita;
		}

		/**
		 * Sets the value of the codComuneNascita property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodComuneNascita(String value) {
			this.codComuneNascita = value;
		}

		/**
		 * Gets the value of the codComuneResidenza property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodComuneResidenza() {
			return codComuneResidenza;
		}

		/**
		 * Sets the value of the codComuneResidenza property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodComuneResidenza(String value) {
			this.codComuneResidenza = value;
		}

		/**
		 * Gets the value of the codFiscale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodFiscale() {
			return codFiscale;
		}

		/**
		 * Sets the value of the codFiscale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodFiscale(String value) {
			this.codFiscale = value;
		}

		/**
		 * Gets the value of the codFiscaleRichiedente property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodFiscaleRichiedente() {
			return codFiscaleRichiedente;
		}

		/**
		 * Sets the value of the codFiscaleRichiedente property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodFiscaleRichiedente(String value) {
			this.codFiscaleRichiedente = value;
		}

		/**
		 * Gets the value of the codProtocolloInps property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodProtocolloInps() {
			return codProtocolloInps;
		}

		/**
		 * Sets the value of the codProtocolloInps property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodProtocolloInps(String value) {
			this.codProtocolloInps = value;
		}

		/**
		 * Gets the value of the codRuoloBeneficiario property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodRuoloBeneficiario() {
			return codRuoloBeneficiario;
		}

		/**
		 * Sets the value of the codRuoloBeneficiario property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodRuoloBeneficiario(String value) {
			this.codRuoloBeneficiario = value;
		}

		/**
		 * Gets the value of the codSesso property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodSesso() {
			return codSesso;
		}

		/**
		 * Sets the value of the codSesso property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodSesso(String value) {
			this.codSesso = value;
		}

		/**
		 * Gets the value of the codStato property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodStato() {
			return codStato;
		}

		/**
		 * Sets the value of the codStato property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodStato(String value) {
			this.codStato = value;
		}

		/**
		 * Gets the value of the desCognome property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesCognome() {
			return desCognome;
		}

		/**
		 * Sets the value of the desCognome property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesCognome(String value) {
			this.desCognome = value;
		}

		/**
		 * Gets the value of the desEmail property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesEmail() {
			return desEmail;
		}

		/**
		 * Sets the value of the desEmail property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesEmail(String value) {
			this.desEmail = value;
		}

		/**
		 * Gets the value of the desIndirizzoDomicilio property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesIndirizzoDomicilio() {
			return desIndirizzoDomicilio;
		}

		/**
		 * Sets the value of the desIndirizzoDomicilio property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesIndirizzoDomicilio(String value) {
			this.desIndirizzoDomicilio = value;
		}

		/**
		 * Gets the value of the desIndirizzoResidenza property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesIndirizzoResidenza() {
			return desIndirizzoResidenza;
		}

		/**
		 * Sets the value of the desIndirizzoResidenza property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesIndirizzoResidenza(String value) {
			this.desIndirizzoResidenza = value;
		}

		/**
		 * Gets the value of the desNome property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesNome() {
			return desNome;
		}

		/**
		 * Sets the value of the desNome property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesNome(String value) {
			this.desNome = value;
		}

		/**
		 * Gets the value of the desTelefono property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesTelefono() {
			return desTelefono;
		}

		/**
		 * Sets the value of the desTelefono property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesTelefono(String value) {
			this.desTelefono = value;
		}

		/**
		 * Gets the value of the dttDomanda property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDttDomanda() {
			return dttDomanda;
		}

		/**
		 * Sets the value of the dttDomanda property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDttDomanda(String value) {
			this.dttDomanda = value;
		}

		/**
		 * Gets the value of the dttNascita property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDttNascita() {
			return dttNascita;
		}

		/**
		 * Sets the value of the dttNascita property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDttNascita(String value) {
			this.dttNascita = value;
		}

		/**
		 * Gets the value of the dttRendicontazione property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDttRendicontazione() {
			return dttRendicontazione;
		}

		/**
		 * Sets the value of the dttRendicontazione property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDttRendicontazione(String value) {
			this.dttRendicontazione = value;
		}

		/**
		 * Gets the value of the desComuneDomicilio property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesComuneDomicilio() {
			return desComuneDomicilio;
		}

		/**
		 * Sets the value of the desComuneDomicilio property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesComuneDomicilio(String value) {
			this.desComuneDomicilio = value;
		}

		/**
		 * Gets the value of the desComuneResidenza property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesComuneResidenza() {
			return desComuneResidenza;
		}

		/**
		 * Sets the value of the desComuneResidenza property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesComuneResidenza(String value) {
			this.desComuneResidenza = value;
		}

		/**
		 * Gets the value of the desComuneNascita property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDesComuneNascita() {
			return desComuneNascita;
		}

		/**
		 * Sets the value of the desComuneNascita property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDesComuneNascita(String value) {
			this.desComuneNascita = value;
		}

		/**
		 * Gets the value of the codSap property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodSap() {
			return codSap;
		}

		/**
		 * Sets the value of the codSap property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodSap(String value) {
			this.codSap = value;
		}

		/**
		 * Gets the value of the codCpi property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodCpi() {
			return codCpi;
		}

		/**
		 * Sets the value of the codCpi property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodCpi(String value) {
			this.codCpi = value;
		}

		/**
		 * Gets the value of the fonte property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getFonte() {
			return fonte;
		}

		/**
		 * Sets the value of the fonte property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setFonte(String value) {
			this.fonte = value;
		}

		/**
		 * Gets the value of the dttTrasformazione property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDttTrasformazione() {
			return dttTrasformazione;
		}

		/**
		 * Sets the value of the dttTrasformazione property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDttTrasformazione(String value) {
			this.dttTrasformazione = value;
		}

		/**
		 * Gets the value of the tipoDomanda property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTipoDomanda() {
			return tipoDomanda;
		}

		/**
		 * Sets the value of the tipoDomanda property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTipoDomanda(String value) {
			this.tipoDomanda = value;
		}

		/**
		 * Gets the value of the dttVariazioneStato property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDttVariazioneStato() {
			return dttVariazioneStato;
		}

		/**
		 * Sets the value of the dttVariazioneStato property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDttVariazioneStato(String value) {
			this.dttVariazioneStato = value;
		}

	}

}
