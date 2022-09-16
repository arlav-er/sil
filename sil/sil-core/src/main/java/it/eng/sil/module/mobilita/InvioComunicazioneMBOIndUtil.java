package it.eng.sil.module.mobilita;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.coop.webservices.mobilita.AdapterWS;
import it.eng.sil.coop.webservices.mobilita.AdapterWSServiceLocator;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.Utils;

/**
 * Classe di utilita' per l'invio di un'iscrizione di mobilita' individuale all'NCR.<br>
 * Contiene la costruzione e la validazione dell'XML e la chiamata al web service per l'invio all'NCR.
 * 
 * @author uberti
 * @see it.eng.sil.module.mobilita.InvioComunicazioneMBOInd
 */
public class InvioComunicazioneMBOIndUtil {

	public static final String XML_ISCRIZIONE = "xmlIscrizione";
	public static final String CODICE_COMUNICAZIONE = "codiceComunicazione";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InvioComunicazioneMBOIndUtil.class.getName());

	/**
	 * Costruisce l'XML da inviare all'NCR
	 * 
	 * @param iscrizione
	 *            SourceBean con i dati per l'XML
	 * @return String con l'XML da inviare, oppure <code>null</code> in caso di errore
	 * @throws InvioComunicazioneException
	 * @throws ValidazioneXMLException
	 * @throws CfException
	 */
	public static Map<String, String> buildXml(SourceBean iscrizione) throws MandatoryFieldException,
			FieldFormatException, ValidazioneXMLException, CfException, EMFInternalError {

		Map<String, String> result = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
		try {

			parser = factory.newDocumentBuilder();
			Document doc = parser.newDocument();
			Element domandaElem = doc.createElement("mbo:DomandaMBO");

			String codiceComunicazione = getCodiceComunicazione();
			codiceComunicazione = XMLValidator.isFieldLengthEqual(codiceComunicazione, 16,
					"\"codice della domanda da inviare generato\"");

			domandaElem.setAttribute(CODICE_COMUNICAZIONE, codiceComunicazione);
			String dataInvio = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataInvio"), true, null,
					"\"data invio della domanda\"");
			domandaElem.setAttribute("dataInvio", dataInvio);
			domandaElem.setAttribute("statoDomanda", "I");
			domandaElem.setAttribute("tipoComunicazione", "N");
			domandaElem.setAttribute("schemaVersion", "1");
			domandaElem.setAttribute("xmlns:mbo", "http://regione.emilia-romagna.it/sare/mbo/1");
			domandaElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			domandaElem.setAttribute("xsi:schemaLocation",
					"http://regione.emilia-romagna.it/sare/mbo/1 comunicazioneMBOIndividuale.xsd");
			doc.appendChild(domandaElem);

			// DATI GENERALI MOBILITA
			Element datiGenMBOElem = doc.createElement("mbo:datiGeneraliMobilita");
			String dataFineIscrMbo = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataFineIscrMbo"), true, null,
					"\"data fine dell'iscrizione\"");
			datiGenMBOElem.setAttribute("dataFineIscrMbo", dataFineIscrMbo);
			String dataInizioIscrMbo = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataInizioIscrMbo"), true, null,
					"\"data inizio dell'iscrizione\"");
			datiGenMBOElem.setAttribute("dataInizioIscrMbo", dataInizioIscrMbo);
			datiGenMBOElem.setAttribute("dataMaxDiffIscrMbo",
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataMaxDiffIscrMbo"));
			String tipoIscrMbo = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.tipoIscrMbo"), true, null,
					"\"tipo iscrizione della domanda\"");
			datiGenMBOElem.setAttribute("tipoIscrMbo", tipoIscrMbo);

			SourceBean conf_sb_MBDUBBIO = Utils.getConfigValue("MBDUBBIO");
			String str_conf_MBDUBBIO = (String) conf_sb_MBDUBBIO.getAttribute("row.num");
			String flgCasoDubbio = "";
			if (str_conf_MBDUBBIO.equals("1")) {
				flgCasoDubbio = XMLValidator.existAndFormat(
						StringUtils.getAttributeStrNotNull(iscrizione, "ROW.CASODUBBIO"), true, null,
						"\"flag caso dubbio\"");
				Vector<String> enumCasoDubbio = new Vector<String>();
				enumCasoDubbio.add("S");
				enumCasoDubbio.add("N");
				flgCasoDubbio = XMLValidator.isFieldInEnumeration(flgCasoDubbio, enumCasoDubbio,
						"\"flag caso dubbio\"");
				datiGenMBOElem.setAttribute("flgCasoDubbio", flgCasoDubbio);
				if (flgCasoDubbio.equals("S")) {
					String motivoCasoDubbio = XMLValidator.existAndFormat(
							StringUtils.getAttributeStrNotNull(iscrizione, "ROW.STRDESCCASODUBBIO"), true, null,
							"\"motivo sintetico caso dubbio\"");
					datiGenMBOElem.setAttribute("motivoCasoDubbio", motivoCasoDubbio);
				}

			}

			domandaElem.appendChild(datiGenMBOElem);

			// AZIENDA
			Element aziendaElem = doc.createElement("mbo:Azienda");
			String codiceFiscaleAz = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.codiceFiscale"), true,
					XMLValidator.codFiscRegEx, "\"codice fiscale dell'azienda\"");
			aziendaElem.setAttribute("codiceFiscale", codiceFiscaleAz);
			aziendaElem.setAttribute("qualificazione",
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.qualificazione"));
			String ragioneSociale = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.ragioneSociale"), true, null,
					"\"ragione sociale dell'azienda\"");
			aziendaElem.setAttribute("ragioneSociale", ragioneSociale);
			String tipologiaAzienda = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.tipologiaAzienda"), true, null,
					"\"tipologia dell'azienda\"");
			Vector<String> enumeration = new Vector<String>();
			enumeration.add("AUT");
			enumeration.add("AGR");
			enumeration.add("INT");
			enumeration.add("PA");
			enumeration.add("AZI");
			tipologiaAzienda = XMLValidator.isFieldInEnumeration(tipologiaAzienda, enumeration,
					"\"tipologia dell'azienda\"");
			aziendaElem.setAttribute("tipologiaAzienda", tipologiaAzienda);
			String flgNonImprenditore = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.flgNonImprenditore"), true, null,
					"\"flag Datore di lavoro non imprenditore del lavoratore \"");
			flgNonImprenditore = StringUtils.getStringValueNotNull(flgNonImprenditore);
			flgNonImprenditore = flgNonImprenditore.equals("S") ? "SI" : (flgNonImprenditore.equals("N") ? "NO" : "");
			if (flgNonImprenditore.length() > 0) {
				aziendaElem.setAttribute("nonImprenditore", flgNonImprenditore);
			}

			// SEDE (PADRE: AZIENDA)
			Element sedeElem = doc.createElement("mbo:Sede");
			String ccnlApplicato = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.ccnlApplicato");
			if (!"".equals(ccnlApplicato)) {
				ccnlApplicato = XMLValidator.existAndFormat(ccnlApplicato, false, XMLValidator.ccnlRegEx,
						"\"CCNL applicato\"");
				sedeElem.setAttribute("ccnlApplicato", ccnlApplicato);
			}
			String codAzStato = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.codAzStato"), true, null,
					"\"codice stato dell'azienda\"");
			enumeration.clear();
			enumeration.add("1");
			enumeration.add("2");
			enumeration.add("3");
			enumeration.add("NT");
			codAzStato = XMLValidator.isFieldInEnumeration(codAzStato, enumeration, "\"codice stato dell'azienda\"");
			sedeElem.setAttribute("codAzStato", codAzStato);
			String denominazioneSede = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.denominazioneSede");
			if (!"".equals(denominazioneSede)) {
				sedeElem.setAttribute("denominazioneSede", denominazioneSede);
			}
			/*
			 * String matricolaInps = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.matricolaINPS"); if
			 * (!"".equals(matricolaInps)) { matricolaInps = XMLValidator.getNumber(matricolaInps); matricolaInps =
			 * XMLValidator.isFieldLengthBetween(matricolaInps, 0, 10, "\"matricola INPS\""); }
			 */
			sedeElem.setAttribute("matricolaINPS", "");

			String settore = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.settore");
			sedeElem.setAttribute("settore", getCodAtecoDot(settore));

			// INDIRIZZO COMPLETO (PADRE: SEDE)
			Element indirizzoCompletoElem = doc.createElement("mbo:IndirizzoCompleto");
			String indirizzoSede = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.indirizzo");
			if (!"".equals(indirizzoSede)) {
				indirizzoSede = XMLValidator.isFieldLengthMax(indirizzoSede, 60, "\"indirizzo della sede\"");
				UtilityXml.appendTextChild("mbo:indirizzo", indirizzoSede, indirizzoCompletoElem, doc);
			}
			String comuneSede = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.comune");
			if (!"".equals(comuneSede)) {
				comuneSede = XMLValidator.existAndFormat(comuneSede, false, XMLValidator.comuneRegEx,
						"\"comune della sede\"");
				UtilityXml.appendTextChild("mbo:comune", comuneSede, indirizzoCompletoElem, doc);
			}
			String capAz = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.cap");
			if ("".equals(capAz)) {
				capAz = "00000";
			} else {
				capAz = XMLValidator.getNumber(capAz);
				capAz = XMLValidator.existAndFormat(capAz, false, XMLValidator.capRegEx, "\"C.A.P. dell'azienda\"");
			}
			UtilityXml.appendTextChild("mbo:cap", capAz, indirizzoCompletoElem, doc);
			sedeElem.appendChild(indirizzoCompletoElem);

			// RECAPITI (PADRE: SEDE)
			Element recapitiElem = doc.createElement("mbo:Recapiti");
			// String telefonoSede = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.telefono");
			String telefonoSede = "00000000";
			if (!"".equals(telefonoSede)) {
				telefonoSede = XMLValidator.getNumber(telefonoSede);
				telefonoSede = XMLValidator.isFieldLengthMax(telefonoSede, 20, "\"telefono della sede\"");
				UtilityXml.appendTextChild("mbo:telefono", telefonoSede, recapitiElem, doc);
			}
			String faxSede = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.fax");
			if (!"".equals(faxSede)) {
				faxSede = XMLValidator.getNumber(faxSede);
				faxSede = XMLValidator.isFieldLengthMax(faxSede, 20, "\"fax della sede\"");
				UtilityXml.appendTextChild("mbo:fax", faxSede, recapitiElem, doc);
			}
			String emailSede = XMLValidator.existAndFormat(StringUtils.getAttributeStrNotNull(iscrizione, "ROW.email"),
					false, XMLValidator.emailRegEx, "\"e-mail della sede\"");
			if (!"".equals(emailSede)) {
				UtilityXml.appendTextChild("mbo:e-mail", emailSede, recapitiElem, doc);
			}
			sedeElem.appendChild(recapitiElem);

			// LAVORATORE (PADRE: SEDE)
			Element lavoratoreElem = doc.createElement("mbo:Lavoratore");
			String cittadinanza = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.cittadinanza"), true, null,
					"\"cittadinanza del lavoratore\"");
			lavoratoreElem.setAttribute("cittadinanza", cittadinanza);
			String cognome = XMLValidator.existAndFormat(StringUtils.getAttributeStrNotNull(iscrizione, "ROW.cognome"),
					true, null, "\"cognome del lavoratore\"");
			lavoratoreElem.setAttribute("cognome", cognome);
			String comuneNascita = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.comuneNascita"), true, null,
					"\"comune di nascita del lavoratore\"");
			lavoratoreElem.setAttribute("comuneNascita", comuneNascita);
			String dataNascita = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataNascita"), true, null,
					"\"data di nascita del lavoratore\"");
			lavoratoreElem.setAttribute("dataNascita", dataNascita);
			String nome = XMLValidator.existAndFormat(StringUtils.getAttributeStrNotNull(iscrizione, "ROW.nome"), true,
					null, "\"nome del lavoratore\"");
			lavoratoreElem.setAttribute("nome", nome);
			String sesso = XMLValidator.existAndFormat(StringUtils.getAttributeStrNotNull(iscrizione, "ROW.sesso"),
					true, null, "\"sesso del lavoratore\"");
			lavoratoreElem.setAttribute("sesso", sesso);
			String codiceFiscaleLav = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.codiceFiscaleLav"), true,
					XMLValidator.codFiscRegEx, "\"codice fiscale del lavoratore\"");
			SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdfSource.parse(dataNascita);
			SimpleDateFormat sdfDestination = new SimpleDateFormat("dd/MM/yyyy");
			dataNascita = sdfDestination.format(date);
			CF_utils.verificaCF(codiceFiscaleLav, nome, cognome, sesso, dataNascita, comuneNascita);
			lavoratoreElem.setAttribute("codiceFiscale", codiceFiscaleLav);

			// INDIRIZZI (PADRE: LAVORATORE)
			Element indirizziElem = doc.createElement("mbo:Indirizzi");

			// DOMICILIO (PADRE: INDIRIZZI)
			Element domicilioElem = doc.createElement("mbo:domicilio");
			String indirizzoDom = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.indirizzoDom"), true, null,
					"\"indirizzo di domicilio del lavoratore\"");
			indirizzoDom = XMLValidator.isFieldLengthMax(indirizzoDom, 60, "\"indirizzo di domicilio del lavoratore\"");
			UtilityXml.appendTextChild("mbo:indirizzo", indirizzoDom, domicilioElem, doc);
			String comuneDom = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.comuneDom");
			if (!"".equals(comuneDom)) {
				comuneDom = XMLValidator.existAndFormat(comuneDom, false, XMLValidator.comuneRegEx,
						"\"comune di domicilio del lavoratore\"");
				UtilityXml.appendTextChild("mbo:comune", comuneDom, domicilioElem, doc);
			}
			String capDom = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.capDom");
			if ("".equals(capDom)) {
				capDom = "00000";
			} else {
				capDom = XMLValidator.getNumber(capDom);
				capDom = XMLValidator.existAndFormat(capDom, false, XMLValidator.capRegEx,
						"\"C.A.P. di domicilio del lavoratore\"");
			}
			UtilityXml.appendTextChild("mbo:cap", capDom, domicilioElem, doc);
			indirizziElem.appendChild(domicilioElem);

			// RESIDENZA (PADRE: INDIRIZZI)
			Element residenzaElem = doc.createElement("mbo:residenza");
			String indirizzoRes = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.indirizzoRes"), true, null,
					"\"indirizzo di residenza del lavoratore\"");
			indirizzoRes = XMLValidator.isFieldLengthMax(indirizzoRes, 60, "\"indirizzo di residenza del lavoratore\"");
			UtilityXml.appendTextChild("mbo:indirizzo", indirizzoRes, residenzaElem, doc);
			String comuneRes = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.comuneRes");
			if (!"".equals(comuneRes)) {
				comuneRes = XMLValidator.existAndFormat(comuneRes, false, XMLValidator.comuneRegEx,
						"\"comune di residenza del lavoratore\"");
				UtilityXml.appendTextChild("mbo:comune", comuneRes, residenzaElem, doc);
			}
			String capRes = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.capRes");
			if ("".equals(capRes)) {
				capRes = "00000";
			} else {
				capRes = XMLValidator.getNumber(capRes);
				capRes = XMLValidator.existAndFormat(capRes, false, XMLValidator.capRegEx,
						"\"C.A.P. di residenza del lavoratore\"");
			}
			UtilityXml.appendTextChild("mbo:cap", capRes, residenzaElem, doc);
			indirizziElem.appendChild(residenzaElem);

			lavoratoreElem.appendChild(indirizziElem);

			// RECAPITI (PADRE: LAVORATORE): se entrambi non sono valorizzati, prevalorizzo il telefono
			Element recapitiLavElem = doc.createElement("mbo:Recapiti");
			String cellulare = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.cellulare");
			String telefonoLav = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.telefonoLav");
			if ("".equals(cellulare) && "".equals(telefonoLav)) {
				telefonoLav = "00000000";
			} else {
				telefonoLav = XMLValidator.getNumber(telefonoLav);
				telefonoLav = XMLValidator.isFieldLengthMax(telefonoLav, 20, "\"telefono del lavoratore\"");
			}
			if (!"".equals(cellulare)) {
				cellulare = XMLValidator.getNumber(cellulare);
				cellulare = XMLValidator.isFieldLengthMax(cellulare, 20, "\"cellulare del lavoratore\"");
				UtilityXml.appendTextChild("mbo:cellulare", cellulare, recapitiLavElem, doc);
			}
			UtilityXml.appendTextChild("mbo:telefono", telefonoLav, recapitiLavElem, doc);
			lavoratoreElem.appendChild(recapitiLavElem);

			// DATI INDIVIDUALI MOBILITA (PADRE: LAVORATORE)
			Element datiIndMob = doc.createElement("mbo:datiIndividualiMobilita");
			String dataLicenziamento = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataLicenziamento"), true, null,
					"\"data di licenziamento del lavoratore\"");
			datiIndMob.setAttribute("dataLicenziamento", dataLicenziamento);

			// RAPPORTO LAVORO (PADRE: DATI INDIVIDUALI MOBILITA)
			Element rapportoLavoro = doc.createElement("mbo:rapportoLavoro");
			String categoria = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.categoria"), true, null, "\"categoria\"");
			rapportoLavoro.setAttribute("categoria", categoria);
			String dataAssunzione = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataAssunzione"), true, null,
					"\"data di assunzione\"");
			rapportoLavoro.setAttribute("dataAssunzione", dataAssunzione);
			String qualifica = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.qualifica"), true, null, "\"qualifica\"");
			qualifica = XMLValidator.isFieldLengthMax(qualifica, 10, "\"qualifica\"");
			rapportoLavoro.setAttribute("qualifica", qualifica);
			String tipoContratto = XMLValidator.existAndFormat(
					StringUtils.getAttributeStrNotNull(iscrizione, "ROW.tipoContratto"), true, null,
					"\"tipo contratto\"");
			enumeration.clear();
			enumeration.add("AP");
			enumeration.add("LP");
			enumeration.add("NT");
			tipoContratto = XMLValidator.isFieldInEnumeration(tipoContratto, enumeration, "\"tipo contratto\"");
			rapportoLavoro.setAttribute("tipoContratto", tipoContratto);
			String ccnl = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.ccnl");
			if (!"".equals(ccnl)) {
				ccnl = XMLValidator.existAndFormat(ccnl, false, XMLValidator.ccnlRegEx, "\"CCNL\"");
				rapportoLavoro.setAttribute("ccnl", ccnl);
			}
			datiIndMob.appendChild(rapportoLavoro);

			lavoratoreElem.appendChild(datiIndMob);

			sedeElem.appendChild(lavoratoreElem);

			aziendaElem.appendChild(sedeElem);

			domandaElem.appendChild(aziendaElem);

			try {
				returnString = UtilityXml.domToString(doc);

				// validazione xml
				final File schemaFile = new File(
						ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
								+ File.separator + "mobOrdinaria" + File.separator + "comunicazioneMBOIndividuale.xsd");
				String validityErrors = XMLValidator.getValidityErrors(schemaFile, returnString);
				if (!StringUtils.isEmptyNoBlank(validityErrors)) {
					// formatto il messaggio di errore user-friendly
					throw new ValidazioneXMLException(validityErrors);
				}

				result = new HashMap<String, String>();

				result.put(CODICE_COMUNICAZIONE, codiceComunicazione);
				result.put(XML_ISCRIZIONE, returnString);

				return result;

			} catch (TransformerException e) {
				_logger.error("Errore nella trasformazione dell'xml da inviare", e);
				return result;
			}

		} catch (ParserConfigurationException e) {
			_logger.error("Errore nel parsing dell'xml da inviare", e);
			return result;
		} catch (ParseException e) {
			_logger.error("Errore nel parsing dell'xml da inviare", e);
			return result;
		}

	}

	/**
	 * Recupera il codice ateco in notazione puntata
	 * 
	 * @param codAteco
	 *            codice ateco
	 * @param codiceComunicazione
	 *            codice della domanda
	 * @return String codice ateco corretto
	 * @throws MandatoryFieldException
	 * @throws FieldFormatException
	 * @see it.eng.sil.module.mobilita.InvioComunicazioneMBOIndUtil#buildXml(SourceBean)
	 */
	private static String getCodAtecoDot(String codAteco) throws MandatoryFieldException, FieldFormatException {

		if (StringUtils.isEmptyNoBlank(codAteco)) {
			throw new MandatoryFieldException("\"settore della sede aziendale\"");
		}
		String result = "";
		try {
			Object[] params = new Object[1];
			params[0] = codAteco;
			SourceBean sb = (SourceBean) QueryExecutor.executeQuery("GET_COD_ATECO_DOT", params, "SELECT",
					Values.DB_SIL_DATI);
			if (sb != null) {
				sb = sb.containsAttribute("ROW") ? (SourceBean) sb.getAttribute("ROW") : sb;
				result = StringUtils.getAttributeStrNotNull(sb, "codatecodot");
			}

			if (StringUtils.isEmptyNoBlank(result)) {
				throw new FieldFormatException("\"settore della sede aziendale\"");
			}

			return result;

		} catch (FieldFormatException e) {
			_logger.error("Errore nella costruzione del codice ateco", e);
			throw new FieldFormatException("\"settore della sede aziendale\"");

		}
	}

	/**
	 * Costruisce il codice della comunicazione di mobilita' individuale da inviare, salvando poi nel record della
	 * TS_PROGRESSIVO il progressivo aggiornato.<br>
	 * Regola per la costruzione del codice della comunicazione:<br>
	 * <ul>
	 * <li>MB - indica che la comunicazione e' di mobilita'</li>
	 * <li>Codice provincia - codice della provincia che invia la comunicazione (3 cifre)</li>
	 * <li>Anno - anno corrente (2 cifre)</li>
	 * <li>1 - indica che la comunicazione da inviare e' di tipo individuale</li>
	 * <li>Progressivo - ricavato dalla tabella TS_PROGRESSIVO (8 cifre)</li>
	 * </ul>
	 * 
	 * @return String codice comunicazione o <code>null</code> in caso di errore nella creazione
	 * @throws MandatoryFieldException
	 * @see it.eng.sil.module.mobilita.InvioComunicazioneMBOIndUtil#buildXml(SourceBean)
	 */
	private static String getCodiceComunicazione() throws MandatoryFieldException {
		StringBuffer sb = new StringBuffer();

		sb.append("MB");

		TransactionQueryExecutor trans = null;
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			trans.initTransaction();

			// recupero il codice provinca (colonna CODMIN nella DE_PROVINCIA)
			String codProvincia = InfoProvinciaSingleton.getInstance().getCodice();
			Object[] params = new Object[1];
			params[0] = codProvincia;
			SourceBean result = (SourceBean) trans.executeQuery("GET_CODMIN_PROVINCIA", params,
					TransactionQueryExecutor.SELECT);
			String codMin = StringUtils.getAttributeStrNotNull(result, "ROW.CODMIN");
			sb.append(codMin);

			Date today = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yy");
			String year = dateFormat.format(today);
			sb.append(year);

			sb.append("1");

			// recupero il progressivo
			params = new Object[1];
			params[0] = DateUtils.getAnno();
			result = (SourceBean) trans.executeQuery("GET_CODICE_COMUNICAZIONE_PROG", params,
					TransactionQueryExecutor.SELECT);
			String progressivo = StringUtils.getAttributeStrNotNull(result, "ROW.CODCOM");
			sb.append(progressivo);

			// aggiorno la TS_PROGRESSIVO con il nuovo progressivo
			result = (SourceBean) trans.executeQuery("GET_CODICE_COMUNICAZIONE_INSERT", params,
					TransactionQueryExecutor.SELECT);
			BigDecimal progToIns = (BigDecimal) result.getAttribute("ROW.CODCOM");
			params = new Object[2];
			params[0] = progToIns;
			params[1] = DateUtils.getAnno();
			boolean executeQuery = ((Boolean) trans.executeQuery("UPDATE_TS_PROGRESSIVO", params,
					TransactionQueryExecutor.UPDATE)).booleanValue();
			if (!executeQuery) {
				_logger.error("Errore nell'aggiornamento del progressivo nella tabella TS_PROGRESSIVO");
				throw new MandatoryFieldException("\"codice della domanda\"");
			}

			trans.commitTransaction();

		} catch (Exception e) {
			_logger.error("Errore nella costruzione del codice della domanda", e);
			if (trans != null)
				try {
					trans.rollBackTransaction();
				} catch (EMFInternalError e1) {
					_logger.error("Errore nella costruzione del codice della domanda", e1);
					throw new MandatoryFieldException("\"codice della domanda\"");
				}
			throw new MandatoryFieldException("\"codice della domanda\"");
		}

		return sb.toString();
	}

	/**
	 * Metodo per l'invio dell'XML della comunicazione di mobilita' all'NCR
	 * 
	 * @param xmlIscrizione
	 *            String contente l'XML da inviare
	 * @return <code>true</code> se l'invio va a buon fine
	 */
	public static boolean sendComunicazioneToNCR(String xmlIscrizione, String tokenUrl) throws Exception {
		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();

		AdapterWSServiceLocator locator = new AdapterWSServiceLocator();

		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndiName, "ComunicazioneMBOInd");
		String address = endPoint.getUrl();

		_logger.debug("Endpoint address: " + address);
		locator.setAdapterWSEndpointAddress(address);
		AdapterWS service = locator.getAdapterWS();

		String token = tokenUrl;
		String mittente = "SIL";
		String destinatario = "NCR_ER";
		String nomeServizio = "ComunicazioneMBOInd";
		String nomeMetodo = "importa";
		String retCode = service.execute(mittente, destinatario, nomeServizio, nomeMetodo, xmlIscrizione, token);

		_logger.debug("Valore tornato dal WS invio comunicazione di mobilita' all'NCR: " + retCode);

		if ("999".equals(retCode))
			return false;

		return true;
	}
}
