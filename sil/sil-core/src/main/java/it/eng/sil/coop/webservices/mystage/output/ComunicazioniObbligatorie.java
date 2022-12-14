//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.02.17 at 06:09:12 PM CET 
//

package it.eng.sil.coop.webservices.mystage.output;

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
 *       &lt;element minOccurs="1" maxOccurs="1" name="codice" type="{}varchar_2_type"/>
 *         &lt;element name="comunicazione_obbligatoria" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="codice_comunicazione_avviamento" type="{}varchar_20_type"/>
 *                   &lt;element name="datore_lavoro_codice_fiscale" type="{}CodiceFiscale"/>
 *                   &lt;element name="datore_lavoro_denominazione" type="{}varchar_100_type"/>
 *                   &lt;element name="datore_lavoro_indirizzo" type="{}varchar_200_type"/>
 *                   &lt;element name="datore_lavoro_settore" type="{}ateco_type"/>
 *                   &lt;element name="utilizzatore_codice_fiscale" type="{}CodiceFiscale"/>
 *                   &lt;element name="utilizzatore_denominazione" type="{}varchar_100_type"/>
 *                   &lt;element name="utilizzatore_indirizzo" type="{}varchar_200_type"/>
 *                   &lt;element name="utilizzatore_settore" type="{}ateco_type"/>
 *                   &lt;element name="data_inizio" type="{}date_type"/>
 *                   &lt;element name="data_fine" type="{}date_type"/>
 *                   &lt;element name="data_fine_periodo_formativo" type="{}date_type"/>
 *                   &lt;element name="qualifica_professionale" type="{}mansioni_type"/>
 *                   &lt;element name="mansioni" type="{}varchar_300_type"/>
 *                   &lt;element name="tipo_contratto" type="{}tipo_contratto_type"/>
 *                   &lt;element name="flag_stagionale" type="{}si_no_type"/>
 *                   &lt;element name="flag_agricoltura" type="{}si_no_type"/>
 *                   &lt;element name="modalita_lavoro" type="{}modalita_lavoro_type"/>
 *                   &lt;element name="sede_lavoro_indirizzo" type="{}varchar_200_type"/>
 *                   &lt;element name="sede_lavoro_codice_catastale" type="{}codice_catastale_type"/>
 *                   &lt;element name="tipologia_soggetto_promotore" type="{}soggetto_promotore_type"/>
 *                   &lt;element name="tirocinio_categoria" type="{}categoria_tirocinio_type"/>
 *                   &lt;element name="tirocinio_tipologia" type="{}tipologia_tirocinio_type"/>
 *                   &lt;element name="tutore_codice_fiscale" type="{}CodiceFiscale"/>
 *                   &lt;element name="tutore_cognome" type="{}varchar_40_type"/>
 *                   &lt;element name="tutore_nome" type="{}varchar_40_type"/>
 *                   &lt;element name="qualifica_srq" type="{}qualifica_srq_type"/>
 *                   &lt;element name="convenzione_numero" type="{}varchar_30_type"/>
 *                   &lt;element name="convenzione_data" type="{}date_type"/>
 *                   &lt;element name="data_invio_co" type="{}date_type"/>
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
@XmlType(name = "", propOrder = { "codice", "comunicazioneObbligatoria" })
@XmlRootElement(name = "ComunicazioniObbligatorie")
public class ComunicazioniObbligatorie {

	@XmlElement(name = "codice", required = true)
	protected String codice;

	@XmlElement(name = "comunicazione_obbligatoria")
	protected List<ComunicazioniObbligatorie.ComunicazioneObbligatoria> comunicazioneObbligatoria;

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
	 * Gets the value of the comunicazioneObbligatoria property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the comunicazioneObbligatoria property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getComunicazioneObbligatoria().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ComunicazioniObbligatorie.ComunicazioneObbligatoria }
	 * 
	 * 
	 */
	public List<ComunicazioniObbligatorie.ComunicazioneObbligatoria> getComunicazioneObbligatoria() {
		if (comunicazioneObbligatoria == null) {
			comunicazioneObbligatoria = new ArrayList<ComunicazioniObbligatorie.ComunicazioneObbligatoria>();
		}
		return this.comunicazioneObbligatoria;
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
	 *         &lt;element name="codice_comunicazione_avviamento" type="{}varchar_20_type"/>
	 *         &lt;element name="datore_lavoro_codice_fiscale" type="{}CodiceFiscale"/>
	 *         &lt;element name="datore_lavoro_denominazione" type="{}varchar_100_type"/>
	 *         &lt;element name="datore_lavoro_indirizzo" type="{}varchar_200_type"/>
	 *         &lt;element name="datore_lavoro_settore" type="{}ateco_type"/>
	 *         &lt;element name="utilizzatore_codice_fiscale" type="{}CodiceFiscale"/>
	 *         &lt;element name="utilizzatore_denominazione" type="{}varchar_100_type"/>
	 *         &lt;element name="utilizzatore_indirizzo" type="{}varchar_200_type"/>
	 *         &lt;element name="utilizzatore_settore" type="{}ateco_type"/>
	 *         &lt;element name="data_inizio" type="{}date_type"/>
	 *         &lt;element name="data_fine" type="{}date_type"/>
	 *         &lt;element name="data_fine_periodo_formativo" type="{}date_type"/>
	 *         &lt;element name="qualifica_professionale" type="{}mansioni_type"/>
	 *         &lt;element name="mansioni" type="{}varchar_300_type"/>
	 *         &lt;element name="tipo_contratto" type="{}tipo_contratto_type"/>
	 *         &lt;element name="flag_stagionale" type="{}si_no_type"/>
	 *         &lt;element name="flag_agricoltura" type="{}si_no_type"/>
	 *         &lt;element name="modalita_lavoro" type="{}modalita_lavoro_type"/>
	 *         &lt;element name="sede_lavoro_indirizzo" type="{}varchar_200_type"/>
	 *         &lt;element name="sede_lavoro_codice_catastale" type="{}codice_catastale_type"/>
	 *         &lt;element name="tipologia_soggetto_promotore" type="{}soggetto_promotore_type"/>
	 *         &lt;element name="tirocinio_categoria" type="{}categoria_tirocinio_type"/>
	 *         &lt;element name="tirocinio_tipologia" type="{}tipologia_tirocinio_type"/>
	 *         &lt;element name="tutore_codice_fiscale" type="{}CodiceFiscale"/>
	 *         &lt;element name="tutore_cognome" type="{}varchar_40_type"/>
	 *         &lt;element name="tutore_nome" type="{}varchar_40_type"/>
	 *         &lt;element name="qualifica_srq" type="{}qualifica_srq_type"/>
	 *         &lt;element name="convenzione_numero" type="{}varchar_30_type"/>
	 *         &lt;element name="convenzione_data" type="{}date_type"/>
	 *         &lt;element name="data_invio_co" type="{}date_type"/>
	 *         &lt;element name="tirocinante_codice_fiscale" type="{}CodiceFiscale"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "codiceComunicazioneAvviamento", "datoreLavoroCodiceFiscale",
			"datoreLavoroDenominazione", "datoreLavoroIndirizzo", "datoreLavoroSettore", "utilizzatoreCodiceFiscale",
			"utilizzatoreDenominazione", "utilizzatoreIndirizzo", "utilizzatoreSettore", "dataInizio", "dataFine",
			"dataFinePeriodoFormativo", "qualificaProfessionale", "mansioni", "tipoContratto", "flagStagionale",
			"flagAgricoltura", "modalitaLavoro", "sedeLavoroIndirizzo", "sedeLavoroCodiceCatastale",
			"tipologiaSoggettoPromotore", "tirocinioCategoria", "tirocinioTipologia", "tutoreCodiceFiscale",
			"tutoreCognome", "tutoreNome", "qualificaSrq", "convenzioneNumero", "convenzioneData", "dataInvioCo",
			"tirocinanteCodiceFiscale" })
	public static class ComunicazioneObbligatoria {

		@XmlElement(name = "codice_comunicazione_avviamento", required = true, nillable = true)
		protected String codiceComunicazioneAvviamento;
		@XmlElement(name = "datore_lavoro_codice_fiscale", required = true, nillable = true)
		protected String datoreLavoroCodiceFiscale;
		@XmlElement(name = "datore_lavoro_denominazione", required = true, nillable = true)
		protected String datoreLavoroDenominazione;
		@XmlElement(name = "datore_lavoro_indirizzo", required = true, nillable = true)
		protected String datoreLavoroIndirizzo;
		@XmlElement(name = "datore_lavoro_settore", required = true, nillable = true)
		protected String datoreLavoroSettore;
		@XmlElement(name = "utilizzatore_codice_fiscale", required = true, nillable = true)
		protected String utilizzatoreCodiceFiscale;
		@XmlElement(name = "utilizzatore_denominazione", required = true, nillable = true)
		protected String utilizzatoreDenominazione;
		@XmlElement(name = "utilizzatore_indirizzo", required = true, nillable = true)
		protected String utilizzatoreIndirizzo;
		@XmlElement(name = "utilizzatore_settore", required = true, nillable = true)
		protected String utilizzatoreSettore;
		@XmlElement(name = "data_inizio", required = true, nillable = true)
		protected XMLGregorianCalendar dataInizio;
		@XmlElement(name = "data_fine", required = true, nillable = true)
		protected XMLGregorianCalendar dataFine;
		@XmlElement(name = "data_fine_periodo_formativo", required = true, nillable = true)
		protected XMLGregorianCalendar dataFinePeriodoFormativo;
		@XmlElement(name = "qualifica_professionale", required = true, nillable = true)
		protected String qualificaProfessionale;
		@XmlElement(required = true, nillable = true)
		protected String mansioni;
		@XmlElement(name = "tipo_contratto", required = true, nillable = true)
		protected String tipoContratto;
		@XmlElement(name = "flag_stagionale", required = true, nillable = true)
		protected String flagStagionale;
		@XmlElement(name = "flag_agricoltura", required = true, nillable = true)
		protected String flagAgricoltura;
		@XmlElement(name = "modalita_lavoro", required = true, nillable = true)
		protected String modalitaLavoro;
		@XmlElement(name = "sede_lavoro_indirizzo", required = true, nillable = true)
		protected String sedeLavoroIndirizzo;
		@XmlElement(name = "sede_lavoro_codice_catastale", required = true, nillable = true)
		protected String sedeLavoroCodiceCatastale;
		@XmlElement(name = "tipologia_soggetto_promotore", required = true, nillable = true)
		protected String tipologiaSoggettoPromotore;
		@XmlElement(name = "tirocinio_categoria", required = true, nillable = true)
		protected String tirocinioCategoria;
		@XmlElement(name = "tirocinio_tipologia", required = true, nillable = true)
		protected String tirocinioTipologia;
		@XmlElement(name = "tutore_codice_fiscale", required = true, nillable = true)
		protected String tutoreCodiceFiscale;
		@XmlElement(name = "tutore_cognome", required = true, nillable = true)
		protected String tutoreCognome;
		@XmlElement(name = "tutore_nome", required = true, nillable = true)
		protected String tutoreNome;
		@XmlElement(name = "qualifica_srq", required = true, nillable = true)
		protected String qualificaSrq;
		@XmlElement(name = "convenzione_numero", required = true, nillable = true)
		protected String convenzioneNumero;
		@XmlElement(name = "convenzione_data", required = true, nillable = true)
		protected XMLGregorianCalendar convenzioneData;
		@XmlElement(name = "data_invio_co", required = true, nillable = true)
		protected XMLGregorianCalendar dataInvioCo;
		@XmlElement(name = "tirocinante_codice_fiscale", required = true, nillable = true)
		protected String tirocinanteCodiceFiscale;

		/**
		 * Gets the value of the codiceComunicazioneAvviamento property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodiceComunicazioneAvviamento() {
			return codiceComunicazioneAvviamento;
		}

		/**
		 * Sets the value of the codiceComunicazioneAvviamento property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodiceComunicazioneAvviamento(String value) {
			this.codiceComunicazioneAvviamento = value;
		}

		/**
		 * Gets the value of the datoreLavoroCodiceFiscale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDatoreLavoroCodiceFiscale() {
			return datoreLavoroCodiceFiscale;
		}

		/**
		 * Sets the value of the datoreLavoroCodiceFiscale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDatoreLavoroCodiceFiscale(String value) {
			this.datoreLavoroCodiceFiscale = value;
		}

		/**
		 * Gets the value of the datoreLavoroDenominazione property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDatoreLavoroDenominazione() {
			return datoreLavoroDenominazione;
		}

		/**
		 * Sets the value of the datoreLavoroDenominazione property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDatoreLavoroDenominazione(String value) {
			this.datoreLavoroDenominazione = value;
		}

		/**
		 * Gets the value of the datoreLavoroIndirizzo property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDatoreLavoroIndirizzo() {
			return datoreLavoroIndirizzo;
		}

		/**
		 * Sets the value of the datoreLavoroIndirizzo property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDatoreLavoroIndirizzo(String value) {
			this.datoreLavoroIndirizzo = value;
		}

		/**
		 * Gets the value of the datoreLavoroSettore property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDatoreLavoroSettore() {
			return datoreLavoroSettore;
		}

		/**
		 * Sets the value of the datoreLavoroSettore property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDatoreLavoroSettore(String value) {
			this.datoreLavoroSettore = value;
		}

		/**
		 * Gets the value of the utilizzatoreCodiceFiscale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getUtilizzatoreCodiceFiscale() {
			return utilizzatoreCodiceFiscale;
		}

		/**
		 * Sets the value of the utilizzatoreCodiceFiscale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setUtilizzatoreCodiceFiscale(String value) {
			this.utilizzatoreCodiceFiscale = value;
		}

		/**
		 * Gets the value of the utilizzatoreDenominazione property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getUtilizzatoreDenominazione() {
			return utilizzatoreDenominazione;
		}

		/**
		 * Sets the value of the utilizzatoreDenominazione property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setUtilizzatoreDenominazione(String value) {
			this.utilizzatoreDenominazione = value;
		}

		/**
		 * Gets the value of the utilizzatoreIndirizzo property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getUtilizzatoreIndirizzo() {
			return utilizzatoreIndirizzo;
		}

		/**
		 * Sets the value of the utilizzatoreIndirizzo property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setUtilizzatoreIndirizzo(String value) {
			this.utilizzatoreIndirizzo = value;
		}

		/**
		 * Gets the value of the utilizzatoreSettore property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getUtilizzatoreSettore() {
			return utilizzatoreSettore;
		}

		/**
		 * Sets the value of the utilizzatoreSettore property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setUtilizzatoreSettore(String value) {
			this.utilizzatoreSettore = value;
		}

		/**
		 * Gets the value of the dataInizio property.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getDataInizio() {
			return dataInizio;
		}

		/**
		 * Sets the value of the dataInizio property.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setDataInizio(XMLGregorianCalendar value) {
			this.dataInizio = value;
		}

		/**
		 * Gets the value of the dataFine property.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getDataFine() {
			return dataFine;
		}

		/**
		 * Sets the value of the dataFine property.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setDataFine(XMLGregorianCalendar value) {
			this.dataFine = value;
		}

		/**
		 * Gets the value of the dataFinePeriodoFormativo property.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getDataFinePeriodoFormativo() {
			return dataFinePeriodoFormativo;
		}

		/**
		 * Sets the value of the dataFinePeriodoFormativo property.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setDataFinePeriodoFormativo(XMLGregorianCalendar value) {
			this.dataFinePeriodoFormativo = value;
		}

		/**
		 * Gets the value of the qualificaProfessionale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getQualificaProfessionale() {
			return qualificaProfessionale;
		}

		/**
		 * Sets the value of the qualificaProfessionale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setQualificaProfessionale(String value) {
			this.qualificaProfessionale = value;
		}

		/**
		 * Gets the value of the mansioni property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getMansioni() {
			return mansioni;
		}

		/**
		 * Sets the value of the mansioni property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setMansioni(String value) {
			this.mansioni = value;
		}

		/**
		 * Gets the value of the tipoContratto property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTipoContratto() {
			return tipoContratto;
		}

		/**
		 * Sets the value of the tipoContratto property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTipoContratto(String value) {
			this.tipoContratto = value;
		}

		/**
		 * Gets the value of the flagStagionale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getFlagStagionale() {
			return flagStagionale;
		}

		/**
		 * Sets the value of the flagStagionale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setFlagStagionale(String value) {
			this.flagStagionale = value;
		}

		/**
		 * Gets the value of the flagAgricoltura property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getFlagAgricoltura() {
			return flagAgricoltura;
		}

		/**
		 * Sets the value of the flagAgricoltura property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setFlagAgricoltura(String value) {
			this.flagAgricoltura = value;
		}

		/**
		 * Gets the value of the modalitaLavoro property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getModalitaLavoro() {
			return modalitaLavoro;
		}

		/**
		 * Sets the value of the modalitaLavoro property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setModalitaLavoro(String value) {
			this.modalitaLavoro = value;
		}

		/**
		 * Gets the value of the sedeLavoroIndirizzo property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getSedeLavoroIndirizzo() {
			return sedeLavoroIndirizzo;
		}

		/**
		 * Sets the value of the sedeLavoroIndirizzo property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setSedeLavoroIndirizzo(String value) {
			this.sedeLavoroIndirizzo = value;
		}

		/**
		 * Gets the value of the sedeLavoroCodiceCatastale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getSedeLavoroCodiceCatastale() {
			return sedeLavoroCodiceCatastale;
		}

		/**
		 * Sets the value of the sedeLavoroCodiceCatastale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setSedeLavoroCodiceCatastale(String value) {
			this.sedeLavoroCodiceCatastale = value;
		}

		/**
		 * Gets the value of the tipologiaSoggettoPromotore property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTipologiaSoggettoPromotore() {
			return tipologiaSoggettoPromotore;
		}

		/**
		 * Sets the value of the tipologiaSoggettoPromotore property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTipologiaSoggettoPromotore(String value) {
			this.tipologiaSoggettoPromotore = value;
		}

		/**
		 * Gets the value of the tirocinioCategoria property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTirocinioCategoria() {
			return tirocinioCategoria;
		}

		/**
		 * Sets the value of the tirocinioCategoria property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTirocinioCategoria(String value) {
			this.tirocinioCategoria = value;
		}

		/**
		 * Gets the value of the tirocinioTipologia property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTirocinioTipologia() {
			return tirocinioTipologia;
		}

		/**
		 * Sets the value of the tirocinioTipologia property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTirocinioTipologia(String value) {
			this.tirocinioTipologia = value;
		}

		/**
		 * Gets the value of the tutoreCodiceFiscale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTutoreCodiceFiscale() {
			return tutoreCodiceFiscale;
		}

		/**
		 * Sets the value of the tutoreCodiceFiscale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTutoreCodiceFiscale(String value) {
			this.tutoreCodiceFiscale = value;
		}

		/**
		 * Gets the value of the tutoreCognome property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTutoreCognome() {
			return tutoreCognome;
		}

		/**
		 * Sets the value of the tutoreCognome property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTutoreCognome(String value) {
			this.tutoreCognome = value;
		}

		/**
		 * Gets the value of the tutoreNome property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTutoreNome() {
			return tutoreNome;
		}

		/**
		 * Sets the value of the tutoreNome property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTutoreNome(String value) {
			this.tutoreNome = value;
		}

		/**
		 * Gets the value of the qualificaSrq property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getQualificaSrq() {
			return qualificaSrq;
		}

		/**
		 * Sets the value of the qualificaSrq property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setQualificaSrq(String value) {
			this.qualificaSrq = value;
		}

		/**
		 * Gets the value of the convenzioneNumero property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getConvenzioneNumero() {
			return convenzioneNumero;
		}

		/**
		 * Sets the value of the convenzioneNumero property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setConvenzioneNumero(String value) {
			this.convenzioneNumero = value;
		}

		/**
		 * Gets the value of the convenzioneData property.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getConvenzioneData() {
			return convenzioneData;
		}

		/**
		 * Sets the value of the convenzioneData property.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setConvenzioneData(XMLGregorianCalendar value) {
			this.convenzioneData = value;
		}

		/**
		 * Gets the value of the dataInvioCo property.
		 * 
		 * @return possible object is {@link XMLGregorianCalendar }
		 * 
		 */
		public XMLGregorianCalendar getDataInvioCo() {
			return dataInvioCo;
		}

		/**
		 * Sets the value of the dataInvioCo property.
		 * 
		 * @param value
		 *            allowed object is {@link XMLGregorianCalendar }
		 * 
		 */
		public void setDataInvioCo(XMLGregorianCalendar value) {
			this.dataInvioCo = value;
		}

		/**
		 * Gets the value of the tirocinanteCodiceFiscale property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getTirocinanteCodiceFiscale() {
			return tirocinanteCodiceFiscale;
		}

		/**
		 * Sets the value of the tirocinanteCodiceFiscale property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setTirocinanteCodiceFiscale(String value) {
			this.tirocinanteCodiceFiscale = value;
		}

	}

}
