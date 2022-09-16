package it.eng.sil.coop.webservices.mystage;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.agenda.appuntamento.Constants;
import it.eng.sil.coop.webservices.apapi.XmlUtils;
import it.eng.sil.coop.webservices.mystage.input.MovimentoTirocinio;
import it.eng.sil.coop.webservices.mystage.output.ComunicazioniObbligatorie;

public class GetMovimentoTirocinio {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetMovimentoTirocinio.class.getName());

	SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

	public static File inputSchemaFile = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "mystage" + File.separator + "inputXML_GetMovimentoTirocinio.xsd");

	public static File outputSchemaFile = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "mystage" + File.separator + "outputXML_GetMovimentoTirocinio.xsd");

	public static File MovTir_SchemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "mystage" + File.separator + "MovimentoTirocinio.xsd");

	/**
	 * Restiruisce l'XML con i dati della tabella di decodifica passata come parametro
	 * 
	 * @param XML
	 *            contente nomeTabella
	 * @return
	 */
	public String getTirocinio(String inputXML) {
		// risposta del servizio
		String outputXML = "";
		// oggetti per la connection
		DataConnection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet dataResult = null;

		try {

			MovimentoTirocinio dati = null;
			JAXBContext jaxbContext = null;
			// VALIDAZIONE INPUT
			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXML, inputSchemaFile);
			if (!inputXmlIsValid) {
				// ritornare errore validazione input
				outputXML = createXmlRispostaErrore(Constants.ESITO.ERRORE_VALIDAZIONE_INPUT);
				return outputXML;
			}

			try {
				jaxbContext = JAXBContext.newInstance(MovimentoTirocinio.class);
				dati = (MovimentoTirocinio) jaxbContext.createUnmarshaller().unmarshal(new StringReader(inputXML));
			} catch (JAXBException e) {
				_logger.error("Errore parsing input xml: " + inputXML, e);
				// ritornare errore generico
				outputXML = createXmlRispostaErroreGenerico();
				return outputXML;
			}

			String queryMovimento = getSqlQueryMovimento(dati);

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			preparedStatement = conn.getInternalConnection().prepareStatement(queryMovimento);
			dataResult = preparedStatement.executeQuery();

			outputXML = createXmlRisposta(dataResult);

			// VALIDAZIONE OUTPUT
			boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXML, outputSchemaFile);
			if (!outputXmlIsValid) {
				outputXML = createXmlRispostaErroreGenerico();
				return outputXML;
			} else {
				_logger.info("Operazione getTirocinio ha restituito i movimenti richiesti");
			}
		}

		catch (Exception e) {
			_logger.error("Eccezione: movimento tirocinio ", e);
			// ritornare errore generico
			outputXML = createXmlRispostaErroreGenerico();
			return outputXML;
		}

		finally {
			try {
				if (dataResult != null) {
					dataResult.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException eSql) {
				_logger.error("Eccezione: errore chiusura ResultSet o  PreparedStatement", eSql);
			}
			Utils.releaseResources(conn, null, null);
		}

		return outputXML;
	}

	private String getSqlQueryMovimento(MovimentoTirocinio dati) throws Exception {
		String cfLavoratore = dati.getCodiceFiscaleTirocinante();
		String cfAziendaOspitante = dati.getCodiceFiscaleOspitante();
		String cfAziendaPromotore = dati.getCodiceFiscalePromotore();
		String codQualificaSRQ = dati.getCodQualificaSRQ();
		String dataComunicazione = "";
		if (dati.getDataComunicazione() != null) {
			dataComunicazione = DateUtils.formatXMLGregorian(dati.getDataComunicazione());
		}
		String codComunicazione = dati.getCodComunicazione();
		String condizioneWhere = "";
		String query = "select mov.prgmovimento, mov.codcomunicazione, azOspitante.strcodicefiscale codfiscaledatore, "
				+ " azOspitante.strragionesociale ragsocdatore, unitaOspitante.strindirizzo indirizzodatore, deattOspitante.codatecodot atecoospitante, "
				+ " azPromotore.strcodicefiscale codfiscalepromotore, azPromotore.strragionesociale ragsocpromotore, "
				+ " unitaPromotore.strindirizzo indirizzopromotore, deattPromotore.codatecodot atecopromotore, to_char(mov.datiniziomov, 'dd/mm/yyyy') datiniziomov, "
				+ " to_char(mov.datfinemoveffettiva, 'dd/mm/yyyy') datfinemov, to_char(mov.datfinepf, 'dd/mm/yyyy') datfinepf, "
				+ " de_mansione.codmansionedot mansione, de_mansione.strdescrizione as descmansione, mov.codtipocontratto, mov.flglavoroagr, mov.flglavorostagionale, "
				+ " am_movimento_apprendist.strcognometutore, am_movimento_apprendist.strnometutore, am_movimento_apprendist.strcodicefiscaletutore, "
				+ " am_movimento_apprendist.codtipologiatir, am_movimento_apprendist.codcategoriatir, mov.codsoggpromotoremin, am_movimento_apprendist.codqualificasrq, "
				+ " mov.numconvenzione, to_char(mov.datconvenzione, 'dd/mm/yyyy') datconvenzione, mov.numoresett, "
				+ " decode(azOspitante.codtipoazienda, 'INT', decode(mov.flginterasspropria, 'S', '0', decode(mov.prgaziendautiliz, null, '0', decode(mov.datinizioraplav, "
				+ " null, '0', '1'))), '0') ismissione, "
				+ " to_char(mov.datcomunicaz, 'dd/mm/yyyy') datacomunicazione, de_orario.codmonoorario, "
				+ " unitaPromotore.codcom comUtilizz, unitaOspitante.codcom comOspitante, lav.strcodicefiscale codfiscaletirocinante "
				+ " from am_movimento mov "
				+ " inner join an_lavoratore lav on (mov.cdnlavoratore = lav.cdnlavoratore) "
				+ " inner join an_azienda azOspitante on (mov.prgazienda = azOspitante.prgazienda) "
				+ " inner join an_unita_azienda unitaOspitante on (mov.prgazienda = unitaOspitante.prgazienda and mov.prgunita = unitaOspitante.prgunita) "
				+ " left join de_attivita deattOspitante on (unitaOspitante.codateco = deattOspitante.codateco) "
				+ " left join de_orario on (mov.codorario = de_orario.codorario) "
				+ " left join am_movimento_apprendist on (mov.prgmovimento = am_movimento_apprendist.prgmovimento) "
				+ " left join de_mansione on (mov.codmansione = de_mansione.codmansione) "
				+ " left join an_azienda azPromotore on (mov.prgaziendautiliz = azPromotore.prgazienda) "
				+ " left join an_unita_azienda unitaPromotore on (mov.prgaziendautiliz = unitaPromotore.prgazienda and mov.prgunitautiliz = unitaPromotore.prgunita) "
				+ " left join de_attivita deattPromotore on (unitaPromotore.codateco = deattPromotore.codateco) ";

		if (cfLavoratore != null && !cfLavoratore.equals("")) {
			condizioneWhere = " where upper(lav.strcodicefiscale) = '" + cfLavoratore.toUpperCase() + "' ";
		}
		if (condizioneWhere.equals("")) {
			condizioneWhere = " where mov.codstatoatto = 'PR' and mov.codtipomov <> 'CES' and mov.codtipocontratto in ('C.01.00') ";
		} else {
			condizioneWhere = condizioneWhere
					+ " and mov.codstatoatto = 'PR' and mov.codtipomov <> 'CES' and mov.codtipocontratto in ('C.01.00') ";
		}
		if (cfAziendaPromotore != null && !cfAziendaPromotore.equals("")) {
			condizioneWhere = condizioneWhere + " and (upper(am_movimento_apprendist.strcodfiscpromotoretir) = '"
					+ cfAziendaPromotore.toUpperCase() + "' or " + " upper(azPromotore.strcodicefiscale) = '"
					+ cfAziendaPromotore.toUpperCase() + "') ";
		}
		if (cfAziendaOspitante != null && !cfAziendaOspitante.equals("")) {
			condizioneWhere = condizioneWhere + " and upper(azOspitante.strcodicefiscale) = '"
					+ cfAziendaOspitante.toUpperCase() + "' ";
		}
		if (codQualificaSRQ != null && !codQualificaSRQ.equals("")) {
			condizioneWhere = condizioneWhere + " and am_movimento_apprendist.codqualificasrq = '" + codQualificaSRQ
					+ "' ";
		}
		if (codComunicazione != null && !codComunicazione.equals("")) {
			condizioneWhere = condizioneWhere + " and mov.codcomunicazione = '" + codComunicazione + "' ";
		}
		if (dataComunicazione != null && !dataComunicazione.equals("")) {
			condizioneWhere = condizioneWhere + " and trunc(mov.datcomunicaz) = to_date('" + dataComunicazione
					+ "', 'dd/mm/yyyy') ";
		}

		query = query + condizioneWhere;

		query = query + " order by mov.datiniziomov asc, mov.dtmins asc";

		return query;
	}

	private String createXmlRisposta(ResultSet data) throws DatatypeConfigurationException, ParseException, Exception {

		StringWriter writer = null;
		JAXBContext jaxbContext = null;

		int indice = 0;

		ComunicazioniObbligatorie risposta = null;
		ComunicazioniObbligatorie.ComunicazioneObbligatoria datiMovimento = null;
		List<ComunicazioniObbligatorie.ComunicazioneObbligatoria> movimenti = null;

		if (data != null) {
			while (data.next()) {
				// ciclo su ResultSet
				if (indice == 0) {
					risposta = new ComunicazioniObbligatorie();
					movimenti = risposta.getComunicazioneObbligatoria();
				}
				datiMovimento = new ComunicazioniObbligatorie.ComunicazioneObbligatoria();

				String codiceComunicazione = data.getString("codcomunicazione");
				String cfDatore = data.getString("codfiscaledatore");
				String cfTirocinante = data.getString("codfiscaletirocinante");
				String ragSocDatore = data.getString("ragsocdatore");
				String indirizzoDatore = data.getString("indirizzodatore");
				String atecoDatore = data.getString("atecoospitante");
				String cfUtilizz = data.getString("codfiscalepromotore");
				String ragSocUtilizz = data.getString("ragsocpromotore");
				String indirizzoUtilizz = data.getString("indirizzopromotore");
				String atecoUtilizz = data.getString("atecopromotore");
				String dataInizioMov = data.getString("datiniziomov");
				String dataFineMov = data.getString("datfinemov");
				String dataFinePF = data.getString("datfinepf");
				String qualifica = data.getString("mansione");
				String descQualifica = data.getString("descmansione");
				String tipoContratto = data.getString("codtipocontratto");
				String flgLavoroAgr = data.getString("flglavoroagr");
				String flgLavoroStag = data.getString("flglavorostagionale");
				String modOrario = data.getString("codmonoorario");
				BigDecimal numoreSett = data.getBigDecimal("numoresett");
				String sedeLavoroIndirizzo = "";
				String sedeLavoroComune = "";
				String isMissione = data.getString("ismissione");
				if (isMissione.equals("1")) {
					sedeLavoroIndirizzo = indirizzoUtilizz;
					sedeLavoroComune = data.getString("comUtilizz");
				} else {
					sedeLavoroIndirizzo = indirizzoDatore;
					sedeLavoroComune = data.getString("comOspitante");
				}

				if ("T".equalsIgnoreCase(modOrario)) {
					modOrario = "FT";
				} else if ("P".equalsIgnoreCase(modOrario)) {
					modOrario = "PT";
				} else if ("N".equalsIgnoreCase(modOrario)) {
					if (numoreSett == null) {
						modOrario = "FT";
					} else {
						modOrario = "PT";
					}
				}

				String tipologiaPromotore = data.getString("codsoggpromotoremin");
				String catTirocinio = data.getString("codcategoriatir");
				String tipologiaTirocinio = data.getString("codtipologiatir");
				String cognomeTutore = data.getString("strcognometutore");
				String nomeTutore = data.getString("strnometutore");
				String cfTutore = data.getString("strcodicefiscaletutore");
				String qualificaSRQ = data.getString("codqualificasrq");
				String numConvenzione = data.getString("numconvenzione");
				String dataConvenzione = data.getString("datconvenzione");
				String dataInvio = data.getString("datacomunicazione");

				if (codiceComunicazione != null && !codiceComunicazione.equals("")) {
					datiMovimento.setCodiceComunicazioneAvviamento(codiceComunicazione);
				}
				if (cfDatore != null && !cfDatore.equals("")) {
					datiMovimento.setDatoreLavoroCodiceFiscale(cfDatore);
				}
				if (ragSocDatore != null && !ragSocDatore.equals("")) {
					datiMovimento.setDatoreLavoroDenominazione(ragSocDatore);
				}
				if (indirizzoDatore != null && !indirizzoDatore.equals("")) {
					datiMovimento.setDatoreLavoroIndirizzo(indirizzoDatore);
				}
				if (atecoDatore != null && !atecoDatore.equals("")) {
					datiMovimento.setDatoreLavoroSettore(atecoDatore);
				}
				if (cfUtilizz != null && !cfUtilizz.equals("")) {
					datiMovimento.setUtilizzatoreCodiceFiscale(cfUtilizz);
				}
				if (ragSocUtilizz != null && !ragSocUtilizz.equals("")) {
					datiMovimento.setUtilizzatoreDenominazione(ragSocUtilizz);
				}
				if (atecoUtilizz != null && !atecoUtilizz.equals("")) {
					datiMovimento.setUtilizzatoreSettore(atecoUtilizz);
				}
				if (dataInizioMov != null && !dataInizioMov.equals("")) {
					XMLGregorianCalendar datInizioGregorian = toXMLGregorianCalendarDateOnly(dataInizioMov);
					datiMovimento.setDataInizio(datInizioGregorian);
				}
				if (dataFineMov != null && !dataFineMov.equals("")) {
					XMLGregorianCalendar datFineGregorian = toXMLGregorianCalendarDateOnly(dataFineMov);
					datiMovimento.setDataFine(datFineGregorian);
				}
				if (dataFinePF != null && !dataFinePF.equals("")) {
					XMLGregorianCalendar datFinePFGregorian = toXMLGregorianCalendarDateOnly(dataFinePF);
					datiMovimento.setDataFine(datFinePFGregorian);
				}
				if (qualifica != null && !qualifica.equals("")) {
					datiMovimento.setQualificaProfessionale(qualifica);
				}
				if (descQualifica != null && !descQualifica.equals("")) {
					datiMovimento.setMansioni(descQualifica);
				}
				if (tipoContratto != null && !tipoContratto.equals("")) {
					datiMovimento.setTipoContratto(tipoContratto);
				}
				if (flgLavoroAgr != null && !flgLavoroAgr.equals("")) {
					datiMovimento.setFlagAgricoltura(flgLavoroAgr);
				}
				if (flgLavoroStag != null && !flgLavoroStag.equals("")) {
					datiMovimento.setFlagStagionale(flgLavoroStag);
				}
				if (modOrario != null && !modOrario.equals("")) {
					datiMovimento.setModalitaLavoro(modOrario);
				}
				if (sedeLavoroIndirizzo != null && !sedeLavoroIndirizzo.equals("")) {
					datiMovimento.setSedeLavoroIndirizzo(sedeLavoroIndirizzo);
				}
				if (sedeLavoroComune != null && !sedeLavoroComune.equals("")) {
					datiMovimento.setSedeLavoroCodiceCatastale(sedeLavoroComune);
				}
				if (tipologiaPromotore != null && !tipologiaPromotore.equals("")) {
					datiMovimento.setTipologiaSoggettoPromotore(tipologiaPromotore);
				}
				if (catTirocinio != null && !catTirocinio.equals("")) {
					datiMovimento.setTirocinioCategoria(catTirocinio);
				}
				if (tipologiaTirocinio != null && !tipologiaTirocinio.equals("")) {
					datiMovimento.setTirocinioTipologia(tipologiaTirocinio);
				}
				if (cognomeTutore != null && !cognomeTutore.equals("")) {
					datiMovimento.setTutoreCognome(cognomeTutore);
				}
				if (nomeTutore != null && !nomeTutore.equals("")) {
					datiMovimento.setTutoreNome(nomeTutore);
				}
				if (cfTutore != null && !cfTutore.equals("")) {
					datiMovimento.setTutoreCodiceFiscale(cfTutore);
				}
				if (qualificaSRQ != null && !qualificaSRQ.equals("")) {
					datiMovimento.setQualificaSrq(qualificaSRQ);
				}
				if (numConvenzione != null && !numConvenzione.equals("")) {
					datiMovimento.setConvenzioneNumero(numConvenzione);
				}
				if (dataConvenzione != null && !dataConvenzione.equals("")) {
					XMLGregorianCalendar datConvenzioneGregorian = toXMLGregorianCalendarDateOnly(dataConvenzione);
					datiMovimento.setConvenzioneData(datConvenzioneGregorian);
				}
				if (dataInvio != null && !dataInvio.equals("")) {
					XMLGregorianCalendar datInvioGregorian = toXMLGregorianCalendarDateOnly(dataInvio);
					datiMovimento.setDataInvioCo(datInvioGregorian);
				}
				if (cfTirocinante != null && !cfTirocinante.equals("")) {
					datiMovimento.setTirocinanteCodiceFiscale(cfTirocinante);
				}

				movimenti.add(indice, datiMovimento);
				indice = indice + 1;
			}
		} else {
			risposta = new ComunicazioniObbligatorie();
			movimenti = risposta.getComunicazioneObbligatoria();
		}

		if (risposta != null) {
			risposta.setCodice(Constants.ESITO.OK);
		} else {
			risposta = new ComunicazioniObbligatorie();
			risposta.setCodice(Constants.ESITO.ERRORE_GENERICO);
		}

		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(ComunicazioniObbligatorie.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
			throw new Exception(e1);
		}
		return writer.toString();
	}

	private String createXmlRispostaErroreGenerico() {

		JAXBContext jaxbContext;
		StringWriter writer = null;

		ComunicazioniObbligatorie risposta = new ComunicazioniObbligatorie();
		risposta.setCodice(Constants.ESITO.ERRORE_GENERICO);
		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(ComunicazioniObbligatorie.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
		}
		return writer.toString();
	}

	private String createXmlRispostaErrore(String codiceErrore) {
		JAXBContext jaxbContext;
		StringWriter writer = null;

		ComunicazioniObbligatorie risposta = new ComunicazioniObbligatorie();
		risposta.setCodice(codiceErrore);
		writer = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(ComunicazioniObbligatorie.class);
			jaxbContext.createMarshaller().marshal(risposta, writer);
		} catch (JAXBException e1) {
			_logger.error("Errore creazione output XML", e1);
		}
		return writer.toString();
	}

	private static XMLGregorianCalendar toXMLGregorianCalendarDateOnly(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}

}
