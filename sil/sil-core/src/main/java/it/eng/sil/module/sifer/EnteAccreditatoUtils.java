package it.eng.sil.module.sifer;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.sifer.ErroreSifer;
import it.eng.sil.coop.webservices.siferAccreditamento.FlussoSILSIFER;
import it.eng.sil.coop.webservices.siferAccreditamento.FlussoSILSIFER.Partecipante;
import it.eng.sil.coop.webservices.siferAccreditamento.FlussoSILSIFER.Patti;
import it.eng.sil.coop.webservices.siferAccreditamento.FlussoSILSIFER.Patti.ProfilingPatto;
import it.eng.sil.coop.webservices.siferAccreditamento.ObjectFactory;
import it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePortProxy;
import it.eng.sil.util.amministrazione.impatti.PattoBean;
import it.eng.sil.util.xml.XMLValidator;

public class EnteAccreditatoUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EnteAccreditatoUtils.class.getName());

	private static String QUERY_STRING_TRACCIA_ERRORE = "INSERT INTO ts_tracciamento_formazione (prgtracform, datins, cdnlavoratore, coderrore, strmessaggioerrore, strxmlinviato) VALUES (s_ts_tracciamento_formazione.nextval, sysdate, ?,  ?, ?, ?)";

	private static String QUERY_STRING_AZIONI_COLLEGATE = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codmisura, az.prgazioneragg, az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, "
			+ " to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, de_cpi.codcpimin, "
			+ " pc.numygdurataeff, pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, ut.strnome, ut.strcognome, pc.codorganismo, pc.coddistretto, "
			+ " to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione, to_char(pc.dtmmod, 'dd/mm/yyyy hh24:mi') AS dtmmod "
			+ "	FROM or_percorso_concordato pc INNER JOIN am_lav_patto_scelta ps ON (to_char(pc.prgpercorso) = ps.strchiavetabella AND ps.codlsttab = 'OR_PER') "
			+ " INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_cpi ON (co.codcpi = de_cpi.codcpi) "
			+ " INNER JOIN ts_utente ut ON (ut.cdnut = pc.cdnutmod) INNER JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " WHERE ps.prgpattolavoratore = ? and az.codazionesifer is not null AND az.codmisura is not null ";

	private static String QUERY_STRING_AZIONI_COLLEGATE_PROGRAMMA = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codmisura, az.prgazioneragg, az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, "
			+ " to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, de_cpi.codcpimin, "
			+ " pc.numygdurataeff, pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, ut.strnome, ut.strcognome, pc.codorganismo, pc.coddistretto, "
			+ " to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione, to_char(pc.dtmmod, 'dd/mm/yyyy hh24:mi') AS dtmmod "
			+ "	FROM or_percorso_concordato pc INNER JOIN am_lav_patto_scelta ps ON (to_char(pc.prgpercorso) = ps.strchiavetabella AND ps.codlsttab = 'OR_PER') "
			+ " INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_cpi ON (co.codcpi = de_cpi.codcpi) "
			+ " INNER JOIN ts_utente ut ON (ut.cdnut = pc.cdnutmod) INNER JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " WHERE ps.prgpattolavoratore = ? and co.prgcolloquio = ? and az.codazionesifer is not null AND az.codmisura is not null ";

	private static String QUERY_STRING_ACCORPAMENTO = "SELECT STRCODICEFISCALEACCORPATO FROM AN_LAVORATORE_ACCORPA WHERE CDNLAVORATORE = ? AND STRCODICEFISCALE = ?";

	private static String QUERY_STRING_SCHEDA_PARTECIPANTE = "SELECT codstudio, codoccupazione, coddurata, codcontratto FROM or_scheda_partecipante WHERE prgpattolavoratore = ?";

	private static String QUERY_STRING_DATI_PROGRAMMA = "SELECT nome_responsabile, cognome_responsabile, email_responsabile, rif_pa FROM am_dati_programma WHERE prgpattolavoratore = ?";

	private static String QUERY_STRING_SVANTAGGI = "SELECT CODSVANTAGGIO FROM OR_SCHEDA_SVANTAGGIO WHERE prgpattolavoratore = ?";

	private static String QUERY_STRING_DATI_ENTE_ORGANISMO = "SELECT codorganismo, rif_pa FROM an_ente_organismo WHERE strcodicefiscale = ?";

	private static String QUERY_STRING_TITOLO_STUDIO = "select SUBSTR(max(studio.codtitolo),1,1)  titolo from pr_studio studio where studio.cdnlavoratore = ? AND studio.codtitolo != 'NT'";

	private static String QUERY_STRING_CONDIZIONE_OCCUPAZIONALE = "select (case when occ.codstatooccupaz IN ('A223', 'A22') then 1 "
			+ " when occ.codstatooccupaz IN ('B', 'A212','B3','A1','B2','B1') then 2 "
			+ " when occ.codstatooccupaz IN ('A21', 'A213') then 3 "
			+ " when occ.codstatooccupaz IN ('C','C0','C1','C11','C12','C14','C13') then 5 " + " else NULL "
			+ " end) condizioneoccupazionale " + " from am_stato_occupaz occ " + " where occ.cdnlavoratore = ? "
			+ " and to_date(?,'dd/mm/yyyy') between trunc(occ.datinizio) and trunc(nvl(occ.datfine, sysdate))";

	private static String QUERY_STRING_DURATA_RICERCA_OCC = "select substr(PG_MOVIMENTI.CalcolaAnzianita(?, ?), 1, "
			+ "instr(PG_MOVIMENTI.CalcolaAnzianita(?, ?), '-', 1)-1) mesi_anz from dual";

	private static ObjectFactory factory = new ObjectFactory();

	public static final String WS_NOME_METODO_ENTE_ACCREDITATO = "";
	public static final String END_POINT_NAME_ENTE_ACCREDITATO = "";

	public static final String AREA1 = "AREA1";
	public static final String AREA2 = "AREA2";
	public static final String NGG = "NGG";

	public static final int WS_OK_DB = 0;
	public static final int ERR_GENERICO_DB = 99;
	public static final int ERR_INPUT_DB = 4;
	public static final int ERR_WS_DATI_LAVORATORE_DB = 1;
	public static final int ERR_WS_EMAIL_LAV_DB = 2;
	public static final int ERR_RECUPERO_ANAGRAFICA_LAVORATORE_DB = 3;
	public static final int ERR_WS_SCHEDA_PARTECIPANTE_PATTO_DB = 5;
	public static final int ERR_WS_SVANTAGGI_PATTO_DB = 6;
	public static final int ERR_WS_PROGRAMMI_PATTO_DB = 7;
	public static final int ERR_WS_NO_ATTIVITA_PATTO_DB = 8;
	public static final int ERR_WS_RECUPERO_AZIONI_DB = 9;
	public static final int ERR_WS_NO_PATTI_POC_DB = 10;
	public static final int ERR_WS_NO_PATTI_DB = 11;
	public static final int WS_KO_DB = 12;
	public static final int ERR_WS_DATA_ADESIONE_GG_DB = 13;

	/*
	 * PROMEMORIA DECODIFICA MESSAGGI ERRORE # Messaggi relativi a flusso SIL e SIFER Accreditamento per logging DB
	 * TS_TRACCIAMENTO_FORMAZIONE 0 = Invio Effettuato con successo 1 = Attenzione: invio dati non eseguito in quanto
	 * uno dei seguenti dati e' nullo: "Codice fiscale, Cognome, Nome, Sesso, Data di nascita, Comune di nascita". 2 =
	 * Errore nel formato dell'indirizzo e-mail del lavoratore 3 = Errore recupero dati lavoratore da inviare a SIFER 4
	 * = Errore nella validazione output xml 5 = Errore nel recupero dati scheda partecipante associati al patto del %0
	 * 6 = Errore nel recupero svantaggi associati al patto del %0 7 = Errore nel recupero dati del programma associati
	 * al patto del %0 8 = Errore nel recupero attivita associate al patto del %0 9 = Errore nel recupero attivita
	 * associate al programma 10 = Invio dati fallito: il lavoratore non ha un programma
	 * "Piano d'intervento per l'occupazione" 11 = Invio dati fallito: il lavoratore non ha un programma
	 * "Programma personalizzato L.14" o un programma "Piano d'intervento per l'occupazione" o un programma \u201CNuova
	 * Garanzia Giovani\u201D 12 = Errore invio dati lavoratore 99 = Errore generico: impossibile contattare il server
	 */

	private static final String INPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "sifer" + File.separator + "ACCREDITAMENTO_SIFER.xsd";

	private static final String OUTPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "sifer" + File.separator + ".xsd";

	public static ErroreSifer sendFlussoEnteAccreditato(BigDecimal cdnLavoratore, SourceBean anLavBeanRows,
			SourceBean codProvinciaBeanRows, SourceBean pattiBeanRows, String area) throws Exception {

		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		DataResult dr = null;
		int sizePattiProfiling = 0;
		String input = "";

		try {
			qExec = getQueryExecutorObject();
			dc = qExec.getDataConnection();
			_logger.debug("EnteAccreditatoUtils connessione istanziata");

			FlussoSILSIFER msg = factory.createFlussoSILSIFER();
			Partecipante partecipante = factory.createFlussoSILSIFERPartecipante();
			Patti patti = factory.createFlussoSILSIFERPatti();

			it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante partecipanteWS = new it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante();

			ErroreSifer erroreSifer = null;

			erroreSifer = EnteAccreditatoUtils.getPartecipante(qExec, dc, cdnLavoratore, partecipante, partecipanteWS,
					anLavBeanRows, codProvinciaBeanRows);
			if (erroreSifer.errCod != 0) {
				_logger.error(
						"EnteAccreditatoUtils: Invio a formazione non effettuato: cdnlavoratore = " + cdnLavoratore);
				return erroreSifer;
			}

			msg.setPartecipante(partecipante);

			if (pattiBeanRows != null) {
				Vector pattiBeanVector = pattiBeanRows.getAttributeAsVector("ROW");
				sizePattiProfiling = pattiBeanVector.size();
				/*
				 * int sizePatti = pattiBeanVector.size(); if (sizePatti > 0) { for (int i = 0; i < sizePatti; i++) {
				 * SourceBean pattoBeanRow = (SourceBean) pattiBeanVector.elementAt(i); BigDecimal prgPattoLavoratore =
				 * (BigDecimal) pattoBeanRow.getAttribute("PRGPATTOLAVORATORE"); String codTipoPatto = (String)
				 * pattoBeanRow.getAttribute("CODTIPOPATTO"); if
				 * (codTipoPatto.equalsIgnoreCase(PattoBean.DB_PATTO_PROGRAMMA_PERSONALIZZATO) ||
				 * codTipoPatto.equalsIgnoreCase(PattoBean.DB_PATTO_PIANO_INTERVENTO_OCC)) { sizePattiProfiling =
				 * sizePattiProfiling + 1; } else { Vector<BigDecimal>programmiPOC =
				 * PartecipanteGGUtils.checkProgrammiFromCodice(qExec, dc, prgPattoLavoratore,
				 * PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE); Vector<BigDecimal>programmiL14 =
				 * PartecipanteGGUtils.checkProgrammiFromCodice(qExec, dc, prgPattoLavoratore, PattoBean.DB_MISURE_L14);
				 * 
				 * sizePattiProfiling = sizePattiProfiling + programmiPOC.size() + programmiL14.size(); } } }
				 */
			}

			it.eng.sil.coop.webservices.siferAccreditamento.request.Patto pattiWS[] = new it.eng.sil.coop.webservices.siferAccreditamento.request.Patto[sizePattiProfiling];
			erroreSifer = EnteAccreditatoUtils.getPatti(qExec, dc, patti, pattiWS, pattiBeanRows, cdnLavoratore, area,
					sizePattiProfiling);
			if (erroreSifer.errCod != 0) {
				_logger.error(
						"EnteAccreditatoUtils: Invio a formazione non effettuato: cdnlavoratore = " + cdnLavoratore);
				return erroreSifer;
			}

			msg.setPatti(patti);

			// Validazione xsd dell'input
			try {
				input = EnteAccreditatoUtils.convertInputToString(msg);
				_logger.debug("SIL -> SIFER Accreditamento: cdnLavoratore " + cdnLavoratore + " xml input\n" + input);
				/* validazione xsd dell'input */
				// if (!EnteAccreditatoUtils.validazioneXml(input, INPUT_XSD)) {
				// erroreSifer = new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_INPUT, ERR_INPUT_DB );
				// erroreSifer.setStrXMLInviato(input);
				// _logger.error("EnteAccreditatoUtils: Errore in validazione input. cdnlavoratore = " + cdnLavoratore);
				// return erroreSifer;
				// }
			} catch (Exception e) {
				_logger.error("EnteAccreditatoUtils: Errore in validazione input. cdnlavoratore = " + cdnLavoratore
						+ " Tracciato = " + input);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,
						MessageCodes.SIFERACCREDITAMENTO.ERR_INPUT);
				String errMsg = error.getDescription();
				erroreSifer = new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_INPUT,
						errMsg + ":\n" + e.getMessage(), ERR_INPUT_DB);
				erroreSifer.setStrXMLInviato(input);
				return erroreSifer;
			}

			/* Invocazione del servizio */
			String addressWS = getEndpointUrl(dc.getInternalConnection());
			SilSoapServicePortProxy proxy = new SilSoapServicePortProxy(addressWS);

			Object output = proxy.request(partecipanteWS, pattiWS);

			_logger.debug("SIL -> SIFER Accreditamento: cdnLavoratore " + cdnLavoratore + " xml output\n" + output);
			String p_output = "";
			Boolean p_outputB;

			ErroreSifer esitoFinale = null;
			if (output != null) {
				if (output instanceof String) {
					p_output = (String) output;
					if (p_output != null && p_output.equalsIgnoreCase("true")) {
						esitoFinale = new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.WS_OK,
								MessageBundle.getMessage(MessageCodes.SIFERACCREDITAMENTO.WS_OK), WS_OK_DB);
						esitoFinale.setStrXMLInviato(input);
						return esitoFinale;
					}
				} else {
					if (output instanceof Boolean) {
						p_outputB = (Boolean) output;
						if (p_outputB.booleanValue()) {
							esitoFinale = new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.WS_OK,
									MessageBundle.getMessage(MessageCodes.SIFERACCREDITAMENTO.WS_OK), WS_OK_DB);
							esitoFinale.setStrXMLInviato(input);
							return esitoFinale;
						}
					}
				}
			}
			esitoFinale = new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.WS_KO, WS_KO_DB);
			esitoFinale.setStrXMLInviato(input);
			return esitoFinale;
		} catch (Throwable e) {
			_logger.error("EnteAccreditatoUtils errore: " + e);
			ErroreSifer esitoFinale = new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_GENERICO, ERR_GENERICO_DB);
			esitoFinale.setStrXMLInviato(input);
			return esitoFinale;
		} finally {
			_logger.debug("EnteAccreditatoUtils connessione rilasciata");
			Utils.releaseResources(dc, null, dr);
		}
	}

	private static ErroreSifer getPartecipante(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore,
			Partecipante partecipante,
			it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante partecipanteWS,
			SourceBean anLavBeanRows, SourceBean codProvinciaBeanRows)
			throws DatatypeConfigurationException, ParseException {
		String codProvincia = null;
		String strCodiceFiscale = null;
		String strCognome = null;
		String strNome = null;
		String strSesso = null;
		String datNasc = null;
		XMLGregorianCalendar datNascGregorian = null;
		String codComNas = null;
		String codComNasIstat = null;
		String codComResIstat = null;
		String codComDomIstat = null;
		String codCittadinanza = null;
		String strEmail = null;
		String codComRes = null;
		String strIndirizzoRes = null;
		String strCapRes = null;
		String codComDom = null;
		String strIndirizzoDom = null;
		String strCapDom = null;
		String validoCF = null;
		String recapitoTel = null;
		String dtmMod = null;
		XMLGregorianCalendar datModGregorian = null;

		if (anLavBeanRows != null) {
			strCodiceFiscale = (String) anLavBeanRows.getAttribute("ROW.STRCODICEFISCALE");
			strCognome = (String) anLavBeanRows.getAttribute("ROW.STRCOGNOME");
			strNome = (String) anLavBeanRows.getAttribute("ROW.STRNOME");
			strSesso = (String) anLavBeanRows.getAttribute("ROW.STRSESSO");
			datNasc = (String) anLavBeanRows.getAttribute("ROW.DATNASC");
			if (datNasc != null) {
				datNascGregorian = toXMLGregorianCalendarDateOnly(datNasc);
			}
			codComNas = (String) anLavBeanRows.getAttribute("ROW.CODCOMNAS");
			codCittadinanza = (String) anLavBeanRows.getAttribute("ROW.CODCITTADINANZA");
			strEmail = (String) anLavBeanRows.getAttribute("ROW.STREMAIL");
			codComRes = (String) anLavBeanRows.getAttribute("ROW.CODCOM");
			strIndirizzoRes = (String) anLavBeanRows.getAttribute("ROW.STRINDIRIZZORES");
			strCapRes = (String) anLavBeanRows.getAttribute("ROW.STRCAPRES");
			codComDom = (String) anLavBeanRows.getAttribute("ROW.CODCOMDOM");
			strIndirizzoDom = (String) anLavBeanRows.getAttribute("ROW.STRINDIRIZZODOM");
			codComNasIstat = (String) anLavBeanRows.getAttribute("ROW.CODNASCISTAT");
			codComResIstat = (String) anLavBeanRows.getAttribute("ROW.CODRESISTAT");
			codComDomIstat = (String) anLavBeanRows.getAttribute("ROW.CODDOMISTAT");
			strCapDom = (String) anLavBeanRows.getAttribute("ROW.STRCAPDOM");
			validoCF = (String) anLavBeanRows.getAttribute("ROW.FLGCFOK");
			recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRCELL");
			if (recapitoTel == null || recapitoTel.equals("")) {
				recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRTELRES");
				if (recapitoTel == null || recapitoTel.equals("")) {
					recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRTELDOM");
					if (recapitoTel == null || recapitoTel.equals("")) {
						recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRTELALTRO");
					}
				}
			}
			if (recapitoTel == null || recapitoTel.equals("")) {
				recapitoTel = "00000";
			}
			dtmMod = (String) anLavBeanRows.getAttribute("ROW.DTMMOD");
			if (dtmMod != null) {
				datModGregorian = toXMLGregorianCalendarDateOnly(dtmMod);
			}

			if (codProvinciaBeanRows != null) {
				codProvincia = (String) codProvinciaBeanRows.getAttribute("ROW.CODMIN");
			}

			if (validoCF == null || validoCF.equals("")) {
				validoCF = Values.FLAG_FALSE;
			}

			if (codComNasIstat == null || codComNasIstat.trim().equals("")) {
				codComNasIstat = codComNas;
			}
			if (codComResIstat == null || codComResIstat.trim().equals("")) {
				codComResIstat = codComRes;
			}
			if (codComDomIstat == null || codComDomIstat.trim().equals("")) {
				codComDomIstat = codComDom;
			}

			// dati obbligatori
			if (strCodiceFiscale == null || strCodiceFiscale.isEmpty() || codProvincia == null || codProvincia.isEmpty()
					|| strCognome == null || strCognome.isEmpty() || strNome == null || strNome.isEmpty()
					|| strSesso == null || strSesso.isEmpty() || datNasc == null || datNasc.isEmpty()
					|| codComNasIstat == null || codComNasIstat.isEmpty() || codComResIstat == null
					|| codComResIstat.isEmpty() || codComDomIstat == null || codComDomIstat.isEmpty()
					|| strIndirizzoRes == null || strIndirizzoRes.isEmpty() || strIndirizzoDom == null
					|| strIndirizzoDom.isEmpty() || strCapRes == null || strCapRes.isEmpty() || strCapDom == null
					|| strCapDom.isEmpty()) {
				return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_DATI_LAVORATORE,
						ERR_WS_DATI_LAVORATORE_DB);
			}

			partecipante.setCodiceFiscale(strCodiceFiscale.trim());
			partecipanteWS.setCodice_fiscale(strCodiceFiscale.trim());

			if (validoCF != null) {
				partecipante.setValiditaCf(validoCF.trim());
				partecipanteWS.setValidita_cf(validoCF.trim());
			}

			try {
				SourceBean prAccorpamentoRow = null;
				List<DataField> paramAcc = new ArrayList<DataField>();
				paramAcc.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
				paramAcc.add(dc.createDataField("", Types.VARCHAR, strCodiceFiscale));
				qExec.setInputParameters(paramAcc);
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setStatement(QUERY_STRING_ACCORPAMENTO);
				prAccorpamentoRow = (SourceBean) qExec.exec();

				if (prAccorpamentoRow != null) {
					String codFiscaleAccorpato = (String) prAccorpamentoRow
							.getAttribute("ROW.STRCODICEFISCALEACCORPATO");
					if (codFiscaleAccorpato != null) {
						partecipante.setCodiceFiscaleOriginale(codFiscaleAccorpato.trim());
						partecipanteWS.setCodice_fiscale_originale(codFiscaleAccorpato.trim());
					}
				}
			} catch (Exception e) {
				_logger.error("Impossibile recuperare i dati relativi all'accorpamento lavoratore");
			}

			if (cdnLavoratore != null) {
				partecipante.setCdnlavoratore(cdnLavoratore.toBigInteger());
				partecipanteWS.setCdnlavoratore(new Integer(cdnLavoratore.toString()));
			}

			partecipante.setCodiceProvincia(codProvincia.trim());
			partecipanteWS.setCodice_provincia(codProvincia.trim());

			partecipante.setCognome(strCognome.trim());
			partecipanteWS.setCognome(strCognome.trim());

			partecipante.setNome(strNome.trim());
			partecipanteWS.setNome(strNome.trim());

			partecipante.setSesso(strSesso.trim());
			partecipanteWS.setSesso(strSesso.trim());

			partecipante.setNascitaData(datNascGregorian);
			partecipanteWS.setNascita_data(datNasc);

			if (codComNasIstat != null) {
				partecipante.setNascitaCodiceIstat(codComNasIstat.trim());
				partecipanteWS.setNascita_codice_istat(codComNasIstat.trim());
			}

			if (codCittadinanza != null) {
				partecipante.setCittadinanza(codCittadinanza.trim());
				partecipanteWS.setCittadinanza(codCittadinanza.trim());
			}

			if (recapitoTel != null) {
				partecipante.setRecapitoTelefonico(recapitoTel.trim());
				partecipanteWS.setRecapito_telefonico(recapitoTel.trim());
			}

			if (strEmail != null && !strEmail.equals("")) {
				if (checkEmail(strEmail)) {
					partecipante.setEmail(strEmail.trim());
					partecipanteWS.setEmail(strEmail.trim());
				} else {
					return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_EMAIL_LAV, ERR_WS_EMAIL_LAV_DB);
				}
			}

			if (codComResIstat != null) {
				partecipante.setResidenzaCodiceIstat(codComResIstat.trim());
				partecipanteWS.setResidenza_codice_istat(codComResIstat.trim());
			}

			partecipante.setResidenzaIndirizzo(strIndirizzoRes.trim());
			partecipanteWS.setResidenza_indirizzo(strIndirizzoRes.trim());

			partecipante.setResidenzaCap(strCapRes.trim());
			partecipanteWS.setResidenza_cap(strCapRes.trim());

			if (codComDomIstat != null) {
				partecipante.setDomicilioCodiceIstat(codComDomIstat.trim());
				partecipanteWS.setDomicilio_codice_istat(codComDomIstat.trim());
			}

			partecipante.setDomicilioIndirizzo(strIndirizzoDom.trim());
			partecipanteWS.setDomicilio_indirizzo(strIndirizzoDom.trim());

			partecipante.setDomicilioCap(strCapDom.trim());
			partecipanteWS.setDomicilio_cap(strCapDom.trim());

			if (datModGregorian != null) {
				partecipante.setDtModAnagrafica(datModGregorian);
				partecipanteWS.setDt_mod_anagrafica(dtmMod);
			}
		} else {
			return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_RECUPERO_ANAGRAFICA_LAVORATORE,
					ERR_RECUPERO_ANAGRAFICA_LAVORATORE_DB);
		}

		return new ErroreSifer(0);
	}

	private static ErroreSifer getPatti(QueryExecutorObject qExec, DataConnection dc, Patti patti,
			it.eng.sil.coop.webservices.siferAccreditamento.request.Patto pattiWS[], SourceBean pattiBeanRows,
			BigDecimal cdnLavoratore, String area, int sizePattiProfiling) throws Exception {
		ErroreSifer erroreSifer = null;
		if (pattiBeanRows != null) {
			Vector pattiBeanVector = pattiBeanRows.getAttributeAsVector("ROW");
			int sizePatti = pattiBeanVector.size();
			if (sizePatti > 0) {
				for (int i = 0; i < sizePatti; i++) {
					XMLGregorianCalendar datRiferimentoGregorian = null;
					XMLGregorianCalendar datRiferimento150Gregorian = null;
					XMLGregorianCalendar datStipulaGregorian = null;
					XMLGregorianCalendar dataChiusuraPattoGregorian = null;
					XMLGregorianCalendar dataScadenzaPattoGregorian = null;
					XMLGregorianCalendar dataModPattoGregorian = null;
					BigDecimal numProfiling = null;
					String datRiferimento150 = null;
					BigDecimal numindicesvantaggio150 = null;
					XMLGregorianCalendar datDichiarazioneGregorian = null;
					String datDichiarazione = null;

					SourceBean pattoBeanRow = (SourceBean) pattiBeanVector.elementAt(i);

					BigDecimal prgPattoLavoratore = (BigDecimal) pattoBeanRow.getAttribute("PRGPATTOLAVORATORE");
					BigDecimal numIndiceSvantaggio = (BigDecimal) pattoBeanRow.getAttribute("NUMINDICESVANTAGGIO");
					BigDecimal numIndiceSvantaggio2 = (BigDecimal) pattoBeanRow.getAttribute("NUMINDICESVANTAGGIO2");
					numProfiling = (BigDecimal) pattoBeanRow.getAttribute("NUMPROFILING");
					String datRiferimento = (String) pattoBeanRow.getAttribute("DATRIFERIMENTO");
					datRiferimento150 = (String) pattoBeanRow.getAttribute("DATRIFERIMENTO150");
					numindicesvantaggio150 = (BigDecimal) pattoBeanRow.getAttribute("numindicesvantaggio150");
					String dtModPatto = (String) pattoBeanRow.getAttribute("DTMMOD");
					datDichiarazione = (String) pattoBeanRow.getAttribute("DATDICHIARAZIONE");

					if (dtModPatto != null) {
						dataModPattoGregorian = toXMLGregorianCalendarDateOnly(dtModPatto);
					}
					if (datRiferimento != null) {
						datRiferimentoGregorian = toXMLGregorianCalendarDateOnly(datRiferimento);
					}
					if (datRiferimento150 != null) {
						datRiferimento150Gregorian = toXMLGregorianCalendarDateOnly(datRiferimento150);
					}
					String datStipula = (String) pattoBeanRow.getAttribute("DATSTIPULA");
					if (datStipula != null) {
						datStipulaGregorian = toXMLGregorianCalendarDateOnly(datStipula);
					}
					String codCpi = (String) pattoBeanRow.getAttribute("CODCPI");
					String dataChiusuraPatto = (String) pattoBeanRow.getAttribute("DATFINE");
					if (dataChiusuraPatto != null) {
						dataChiusuraPattoGregorian = toXMLGregorianCalendarDateOnly(dataChiusuraPatto);
					}
					String motivoChiusuraPatto = (String) pattoBeanRow.getAttribute("CODMOTIVOFINEATTO");
					String dataScadenzaPatto = (String) pattoBeanRow.getAttribute("DATSCADCONFERMA");
					if (dataScadenzaPatto != null) {
						dataScadenzaPattoGregorian = toXMLGregorianCalendarDateOnly(dataScadenzaPatto);
					}
					BigDecimal numProtocollo = (BigDecimal) pattoBeanRow.getAttribute("NUMPROTOCOLLO");
					BigDecimal prgColloquioCurr = (BigDecimal) pattoBeanRow.getAttribute("PRGCOLLOQUIO");
					String servizioProgrammaPatto = (String) pattoBeanRow.getAttribute("CODSERVIZIO");

					if (datDichiarazione != null) {
						datDichiarazioneGregorian = toXMLGregorianCalendarDateOnly(datDichiarazione);
					}

					// recupero titolo_studio_patto, condizione_occupazionale, durata_ricerca_occupazione, contratto
					// relative al patto
					String codContratto = null;
					String titoloStudioPatto = null;
					String condizioneOcc = null;
					String durataRicercaOcc = null;
					try {
						SourceBean prSchedaBeanRow = null;
						List<DataField> paramScheda = new ArrayList<DataField>();
						paramScheda.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
						qExec.setInputParameters(paramScheda);
						qExec.setType(QueryExecutorObject.SELECT);
						qExec.setStatement(QUERY_STRING_SCHEDA_PARTECIPANTE);
						prSchedaBeanRow = (SourceBean) qExec.exec();

						if (prSchedaBeanRow != null) {
							titoloStudioPatto = (String) prSchedaBeanRow.getAttribute("ROW.codstudio");
							condizioneOcc = (String) prSchedaBeanRow.getAttribute("ROW.codoccupazione");
							durataRicercaOcc = (String) prSchedaBeanRow.getAttribute("ROW.coddurata");
							codContratto = (String) prSchedaBeanRow.getAttribute("ROW.codcontratto");
							// gestione valori scheda partecipante
							if (condizioneOcc != null) {
								if (condizioneOcc.equalsIgnoreCase("2")) {
									durataRicercaOcc = null;
								} else {
									if (condizioneOcc.equalsIgnoreCase("4") || condizioneOcc.equalsIgnoreCase("5")) {
										durataRicercaOcc = null;
										codContratto = null;
									}
								}
							}
						}
					} catch (Exception e) {
						_logger.error("Impossibile recuperare i dati scheda partecipante collegati al patto: "
								+ prgPattoLavoratore);
						Vector<String> params = new Vector<String>();
						params.add(datStipula);
						return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_SCHEDA_PARTECIPANTE_PATTO,
								params, ERR_WS_SCHEDA_PARTECIPANTE_PATTO_DB);
					}

					// recupero svantaggi relative al patto
					SourceBean svantaggiRows = null;
					try {

						List<DataField> param = new ArrayList<DataField>();
						param = new ArrayList<DataField>();
						param.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
						qExec.setInputParameters(param);
						qExec.setType(QueryExecutorObject.SELECT);
						qExec.setStatement(QUERY_STRING_SVANTAGGI);

						svantaggiRows = (SourceBean) qExec.exec();
					} catch (Exception e) {
						_logger.error(
								"Impossibile recuperare i dati svantaggi relativi al patto: " + prgPattoLavoratore);
						Vector<String> params = new Vector<String>();
						params.add(datStipula);
						return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_SVANTAGGI_PATTO, params,
								ERR_WS_SVANTAGGI_PATTO_DB);
					}

					// recupero am_dati_programma : nome_responsabile, cognome_responsabile, email_responsabile, rif_pa
					// per L14
					String nomeResp = null;
					String cognomeResp = null;
					String mailResp = null;
					String rifPa = null;
					try {
						SourceBean prDatiBeanRow = null;
						List<DataField> paramDati = new ArrayList<DataField>();
						paramDati.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
						qExec.setInputParameters(paramDati);
						qExec.setType(QueryExecutorObject.SELECT);
						qExec.setStatement(QUERY_STRING_DATI_PROGRAMMA);
						prDatiBeanRow = (SourceBean) qExec.exec();

						if (prDatiBeanRow != null) {
							nomeResp = (String) prDatiBeanRow.getAttribute("ROW.nome_responsabile");
							cognomeResp = (String) prDatiBeanRow.getAttribute("ROW.cognome_responsabile");
							mailResp = (String) prDatiBeanRow.getAttribute("ROW.email_responsabile");
							rifPa = (String) prDatiBeanRow.getAttribute("ROW.rif_pa");
						}
					} catch (Exception e) {
						_logger.error(
								"Impossibile recuperare i dati programma collegati al patto: " + prgPattoLavoratore);
						Vector<String> params = new Vector<String>();
						params.add(datStipula);
						return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_PROGRAMMI_PATTO, params,
								ERR_WS_PROGRAMMI_PATTO_DB);
					}

					ProfilingPatto profilingPatto = null;
					it.eng.sil.coop.webservices.siferAccreditamento.request.Patto pattoWS = null;

					// gestione patti con programmi
					XMLGregorianCalendar datProgrammaGregorian = null;
					XMLGregorianCalendar datFineProgrammaGregorian = null;
					String datProgramma = null;
					String datFineProgramma = null;

					SourceBean infoProg = PattoBean.getInfoProgramma(qExec, dc, prgColloquioCurr);
					String enteCodiceFiscaleProgramma = PattoBean.soggettoAccreditatoProgramma(qExec, dc,
							prgPattoLavoratore, prgColloquioCurr);
					if (infoProg != null) {
						datProgramma = (String) infoProg.getAttribute("row.datColloquio");
						datFineProgramma = (String) infoProg.getAttribute("row.datFineProgramma");
					}
					if (datProgramma != null) {
						datProgrammaGregorian = toXMLGregorianCalendarDateOnly(datProgramma);

						SourceBean profilingDid150 = PattoBean.getProfilingDid150(qExec, dc, cdnLavoratore,
								datProgramma);
						if (profilingDid150 != null && profilingDid150.getAttribute("ROW.NUMPROFILING") != null
								&& profilingDid150.getAttribute("ROW.DATPROFILING") != null
								&& profilingDid150.getAttribute("ROW.decprofiling") != null) {
							numProfiling = (BigDecimal) profilingDid150.getAttribute("ROW.NUMPROFILING");
							datRiferimento150 = (String) profilingDid150.getAttribute("ROW.DATPROFILING");
							datRiferimento150Gregorian = toXMLGregorianCalendarDateOnly(datRiferimento150);
							numindicesvantaggio150 = (BigDecimal) profilingDid150.getAttribute("ROW.decprofiling");
						}

					}

					if (datFineProgramma != null) {
						datFineProgrammaGregorian = toXMLGregorianCalendarDateOnly(datFineProgramma);
					}

					profilingPatto = factory.createFlussoSILSIFERPattiProfilingPatto();
					pattoWS = new it.eng.sil.coop.webservices.siferAccreditamento.request.Patto();

					if (servizioProgrammaPatto.equalsIgnoreCase(EnteAccreditatoUtils.NGG)) {
						SourceBean datiYgProfilingBean = PattoBean.getDatiYgProfiling(qExec, dc, cdnLavoratore,
								datProgramma);
						if (datiYgProfilingBean != null && datiYgProfilingBean.getAttribute("ROW.NUMINDICE2") != null
								&& datiYgProfilingBean.getAttribute("ROW.DATCALCOLO") != null) {
							numIndiceSvantaggio2 = (BigDecimal) datiYgProfilingBean.getAttribute("ROW.NUMINDICE2");
							datRiferimento = (String) datiYgProfilingBean.getAttribute("ROW.DATCALCOLO");
							datRiferimentoGregorian = toXMLGregorianCalendarDateOnly(datRiferimento);
						}

						if (numIndiceSvantaggio2 != null) {
							profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
							pattoWS.setIndice_svantaggio(new Integer(numIndiceSvantaggio2.toString()));
						}
						if (numIndiceSvantaggio != null) {
							profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
							pattoWS.setIndice_svantaggio_vecchio(new Integer(numIndiceSvantaggio.toString()));
						}

						if (datRiferimentoGregorian != null) {
							profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
							pattoWS.setIndice_data_riferimento(datRiferimento);
						}
					}

					if (numProfiling != null) {
						profilingPatto.setProfiling150(numProfiling.toBigInteger());
						pattoWS.setProfiling_150(new Integer(numProfiling.toString()));
					}
					if (numindicesvantaggio150 != null) {
						profilingPatto.setProfiling150P(numindicesvantaggio150);
						pattoWS.setProfiling_150_p(numindicesvantaggio150.floatValue());
					}
					if (datRiferimento150Gregorian != null) {
						profilingPatto.setDataRiferimento150(datRiferimento150Gregorian);
						pattoWS.setData_riferimento_150(datRiferimento150);
					}
					if (datProgrammaGregorian != null) {
						profilingPatto.setPattoData(datProgrammaGregorian);
						pattoWS.setPatto_data(datProgramma);
					}
					if (numProtocollo != null) {
						profilingPatto.setPattoProtocollo(numProtocollo.toBigInteger());
						pattoWS.setPatto_protocollo(new Integer(numProtocollo.toString()));
					}
					if (codCpi != null) {
						profilingPatto.setPattoCpi(codCpi.trim());
						pattoWS.setPatto_cpi(codCpi.trim());
					}
					if (datFineProgrammaGregorian != null) {
						profilingPatto.setDataChiusuraPatto(datFineProgrammaGregorian);
						pattoWS.setData_chiusura_patto(datFineProgramma);
					}
					if (motivoChiusuraPatto != null) {
						profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
						pattoWS.setMotivo_chiusura_patto(motivoChiusuraPatto.trim());
					}
					profilingPatto.setTipoMisuraPatto(servizioProgrammaPatto);
					pattoWS.setTipo_misura_patto(servizioProgrammaPatto);

					if (servizioProgrammaPatto.equalsIgnoreCase(EnteAccreditatoUtils.NGG)) {
						SourceBean dataAdesioneGGBean = PattoBean.getDataAdesioneGG(qExec, dc, prgColloquioCurr);
						if (dataAdesioneGGBean != null) {
							String dataAdesioneGG = (String) dataAdesioneGGBean.getAttribute("ROW.DATADESIONEGG");
							if (dataAdesioneGG != null) {
								profilingPatto.setDataAdesione(toXMLGregorianCalendarDateOnly(dataAdesioneGG));
								pattoWS.setData_adesione(dataAdesioneGG);
							} else if (area.equalsIgnoreCase(EnteAccreditatoUtils.AREA1))
								return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_DATA_ADESIONE_GG,
										ERR_WS_DATA_ADESIONE_GG_DB);

						}
					}

					// TO DO come gestire anno programmazione PL_2018 o PL_2019?
					if (servizioProgrammaPatto != null && servizioProgrammaPatto
							.equalsIgnoreCase(PattoBean.DB_PATTO_PROGRAMMA_PERSONALIZZATO_2018)) {
						profilingPatto.setAnnoProgrammazione(PattoBean.ANNO_PROGRAMMA_PERSONALIZZATO_2018); // aggiunta
																											// il
																											// 22/01/2020
						pattoWS.setAnno_programmazione(new Integer(PattoBean.ANNO_PROGRAMMA_PERSONALIZZATO_2018));
					} else if (servizioProgrammaPatto != null && servizioProgrammaPatto
							.equalsIgnoreCase(PattoBean.DB_PATTO_PROGRAMMA_PERSONALIZZATO_2019)) {
						profilingPatto.setAnnoProgrammazione(PattoBean.ANNO_PROGRAMMA_PERSONALIZZATO_2019);
						pattoWS.setAnno_programmazione(new Integer(PattoBean.ANNO_PROGRAMMA_PERSONALIZZATO_2019));
					} else if (datProgramma != null) {
						profilingPatto.setAnnoProgrammazione(Integer.toString(DateUtils.getAnno(datProgramma))); // aggiunta
																													// il
																													// 22/01/2020
						pattoWS.setAnno_programmazione(DateUtils.getAnno(datProgramma));
					}

					// titolo_studio_patto, condizione_occupazionale, durata_ricerca_occupazione, contratto
					if (titoloStudioPatto != null) {
						profilingPatto.setTitoloStudioPatto(titoloStudioPatto.trim());
						pattoWS.setTitolo_studio_patto(titoloStudioPatto.trim());
					}
					if (condizioneOcc != null) {
						profilingPatto.setCondizioneOccupazionale(condizioneOcc.trim());
						pattoWS.setCondizione_occupazionale(new Integer(condizioneOcc.trim()));
						if (durataRicercaOcc != null) {
							profilingPatto.setDurataRicercaOccupazione(durataRicercaOcc.trim());
							pattoWS.setDurata_ricerca_occupazione(new Integer(durataRicercaOcc.trim()));
						}
					}
					if (datDichiarazione != null) {
						int durata_disoccupazione = (DateUtils.daysBetween(datDichiarazione, datProgramma) + 1);
						if (durata_disoccupazione >= 0) {
							int gggSospensioni = PattoBean.getSospensioni(qExec, dc, cdnLavoratore, datDichiarazione,
									datProgramma);
							if (gggSospensioni > 0) {
								durata_disoccupazione = durata_disoccupazione - gggSospensioni;
							}
						}
						if (durata_disoccupazione >= 0) {
							profilingPatto.setDurataDisoccupazione(BigInteger.valueOf(durata_disoccupazione));
							pattoWS.setDurata_disoccupazione(durata_disoccupazione);
						}
					}
					if (codContratto != null) {
						profilingPatto.setContratto(codContratto.trim());
						pattoWS.setContratto(codContratto.trim());
					}

					// svantaggi relative al patto
					erroreSifer = EnteAccreditatoUtils.getSvantaggi(qExec, dc, svantaggiRows, profilingPatto, pattoWS,
							servizioProgrammaPatto);
					if (erroreSifer.errCod != 0) {
						return erroreSifer;
					}

					// am_dati_programma : nome_responsabile, cognome_responsabile, email_responsabile, codorganismo e
					// rif_pa per POC
					BigDecimal codOrganismoCurr = null;
					if (enteCodiceFiscaleProgramma != null) {
						SourceBean prDatiEnteOrg = null;
						List<DataField> paramEnteOrg = new ArrayList<DataField>();
						paramEnteOrg.add(dc.createDataField("", Types.VARCHAR, enteCodiceFiscaleProgramma));
						qExec.setInputParameters(paramEnteOrg);
						qExec.setType(QueryExecutorObject.SELECT);
						qExec.setStatement(QUERY_STRING_DATI_ENTE_ORGANISMO);
						prDatiEnteOrg = (SourceBean) qExec.exec();
						if (prDatiEnteOrg != null) {
							codOrganismoCurr = (BigDecimal) prDatiEnteOrg.getAttribute("ROW.codorganismo");
							String rifPAPoc = (String) prDatiEnteOrg.getAttribute("ROW.rif_pa");
							if (rifPAPoc != null) {
								// profilingPatto.setRifPa(rifPAPoc); ???????????????????? // 21/01/2020
							}
						}
					}
					if (nomeResp != null) {
						profilingPatto.setNomeResponsabile(nomeResp.trim());
						pattoWS.setNome_responsabile(nomeResp.trim());
					}
					if (cognomeResp != null) {
						profilingPatto.setCognomeResponsabile(cognomeResp.trim());
						pattoWS.setCognome_responsabile(cognomeResp.trim());
					}
					if (mailResp != null) {
						profilingPatto.setEmailResponsabile(mailResp.trim());
						pattoWS.setEmail_responsabile(mailResp.trim());
					}

					if (dataModPattoGregorian != null) {
						profilingPatto.setDtModPatto(dataModPattoGregorian);
						pattoWS.setDt_mod_patto(dtModPatto);
					}

					// recupero delle azioni relative al patto
					FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
							.createFlussoSILSIFERPattiProfilingPattoPoliticheAttive();
					int sizePolticheAttive = 0;

					try {
						SourceBean azioniBeanRows = null;
						List<DataField> param = new ArrayList<DataField>();
						param = new ArrayList<DataField>();
						param.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
						param.add(dc.createDataField("", Types.BIGINT, prgColloquioCurr));
						qExec.setInputParameters(param);
						qExec.setType(QueryExecutorObject.SELECT);
						qExec.setStatement(QUERY_STRING_AZIONI_COLLEGATE_PROGRAMMA);

						azioniBeanRows = (SourceBean) qExec.exec();

						if (azioniBeanRows != null && !azioniBeanRows.getAttributeAsVector("ROW").isEmpty()) {
							Vector azioniBeanVector = azioniBeanRows.getAttributeAsVector("ROW");
							sizePolticheAttive = azioniBeanVector.size();
						}

						it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva politicheAttiveWS[] = new it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva[sizePolticheAttive];

						erroreSifer = EnteAccreditatoUtils.getAzioni(qExec, dc, datStipula, politicheAttive,
								politicheAttiveWS, azioniBeanRows, codOrganismoCurr);

						if (erroreSifer.errCod != 0) {
							return erroreSifer;
						}

						if (politicheAttive.getPoliticaAttiva().size() == 0) {
							Vector<String> params = new Vector<String>();
							params.add(datStipula);
							return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_NO_ATTIVITA_PATTO, params,
									ERR_WS_NO_ATTIVITA_PATTO_DB);
						}

						profilingPatto.setPoliticheAttive(politicheAttive);
						pattoWS.setPoliticheAttive(politicheAttiveWS);

						patti.getProfilingPatto().add(profilingPatto);
						pattiWS[i] = pattoWS;
					} catch (Exception e) {
						_logger.error("Impossibile recuperare i dati relativi alle azioni collegate al patto: "
								+ prgPattoLavoratore);
						return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_RECUPERO_AZIONI,
								ERR_WS_RECUPERO_AZIONI_DB);
					}
				}
			} else {
				if (area.equalsIgnoreCase(AREA1)) {
					return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_NO_PATTI_POC,
							ERR_WS_NO_PATTI_POC_DB);
				} else {
					return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_NO_PATTI, ERR_WS_NO_PATTI_DB);
				}
			}
		} else {
			if (area.equalsIgnoreCase(AREA1)) {
				return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_NO_PATTI_POC, ERR_WS_NO_PATTI_POC_DB);
			} else {
				return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_NO_PATTI, ERR_WS_NO_PATTI_DB);
			}
		}

		return new ErroreSifer(0);
	}

	private static ErroreSifer getAzioni(QueryExecutorObject qExec, DataConnection dc, String datStipula,
			FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive politicheAttive,
			it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva politicheAttiveWS[],
			SourceBean azioniBeanRows, BigDecimal codOrganismo) throws Exception {

		if (azioniBeanRows == null || azioniBeanRows.getAttributeAsVector("ROW").isEmpty()) {
			Vector<String> params = new Vector<String>();
			params.add(datStipula);
			return new ErroreSifer(MessageCodes.SIFERACCREDITAMENTO.ERR_WS_NO_ATTIVITA_PATTO, params,
					ERR_WS_NO_ATTIVITA_PATTO_DB);
		} else if (azioniBeanRows != null) {
			Vector azioniBeanVector = azioniBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgPercorso = null;
			BigDecimal prgColloquio = null;
			String codAzioneSifer = null;
			BigDecimal numYgDurataEff = null;
			String codTipologiaDurata = null;
			String codEsito = null;
			String dataMod = null;
			String codMisura = null;
			BigDecimal codDistrettoAz = null;
			BigDecimal codOrganismoAzione = null;
			String datEffettiva = null;

			if (codOrganismo != null) {
				codOrganismoAzione = codOrganismo;
			}

			for (int i = 0; i < azioniBeanVector.size(); i++) {
				XMLGregorianCalendar datModGregorian = null;
				SourceBean politicheAttiveBeanRow = (SourceBean) azioniBeanVector.elementAt(i);

				prgPercorso = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGPERCORSO");
				prgColloquio = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGCOLLOQUIO");
				codAzioneSifer = (String) politicheAttiveBeanRow.getAttribute("CODAZIONESIFER");
				dataMod = (String) politicheAttiveBeanRow.getAttribute("DTMMOD");
				datEffettiva = (String) politicheAttiveBeanRow.getAttribute("DATEFFETTIVA");

				if (dataMod != null) {
					datModGregorian = toXMLGregorianCalendarDateOnly(dataMod);
				}
				numYgDurataEff = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAEFF");
				codTipologiaDurata = (String) politicheAttiveBeanRow.getAttribute("CODTIPOLOGIADURATA");
				codEsito = (String) politicheAttiveBeanRow.getAttribute("CODESITO");
				codMisura = (String) politicheAttiveBeanRow.getAttribute("codmisura");
				if (codOrganismo == null) {
					codOrganismoAzione = (BigDecimal) politicheAttiveBeanRow.getAttribute("codorganismo");
				}
				codDistrettoAz = (BigDecimal) politicheAttiveBeanRow.getAttribute("coddistretto");

				FlussoSILSIFER.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva politicaAttiva = factory
						.createFlussoSILSIFERPattiProfilingPattoPoliticheAttivePoliticaAttiva();
				it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva politicaAttivaWS = new it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva();

				if (codAzioneSifer != null) {
					politicaAttiva.setTipologiaAzioneSifer(codAzioneSifer.trim());
					politicaAttivaWS.setTipologia_azione_sifer(codAzioneSifer.trim());
				}
				if (codMisura != null) {
					politicaAttiva.setMisura(codMisura);
					politicaAttivaWS.setMisura(codMisura);
				}
				if (prgPercorso != null) {
					politicaAttiva.setPrgPercorso(prgPercorso.toBigInteger());
					politicaAttivaWS.setPrg_percorso(new Integer(prgPercorso.toString()));
				}
				if (prgColloquio != null) {
					politicaAttiva.setPrgColloquio(prgColloquio.toBigInteger());
					politicaAttivaWS.setPrg_colloquio(new Integer(prgColloquio.toString()));
				}
				if (numYgDurataEff != null && (numYgDurataEff.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataEffettiva(numYgDurataEff.toBigInteger());
					politicaAttivaWS.setDurata_effettiva(new Integer(numYgDurataEff.toString()));
				}
				if (codTipologiaDurata != null) {
					politicaAttiva.setTipologiaDurata(codTipologiaDurata.trim());
					politicaAttivaWS.setTipologia_durata(codTipologiaDurata.trim());
				}
				if (codEsito != null) {
					politicaAttiva.setEsito(codEsito.trim());
					politicaAttivaWS.setEsito(codEsito.trim());
				}
				if (codOrganismoAzione != null) {
					politicaAttiva.setCodiceOrganismo(codOrganismoAzione.toBigInteger());
					politicaAttivaWS.setCodice_organismo(new Integer(codOrganismoAzione.toString()));
				}
				if (codDistrettoAz != null) {
					politicaAttiva.setCodiceDistretto(codDistrettoAz.toBigInteger());
					politicaAttivaWS.setCodice_distretto(new Integer(codDistrettoAz.toString()));
				}

				if (datEffettiva != null && codEsito != null && codEsito.trim().equalsIgnoreCase("FC")) {
					politicaAttiva.setDataChiusuraPoliticaAttiva(toXMLGregorianCalendarDateOnly(datEffettiva));
					politicaAttivaWS.setData_chiusura_politica_attiva(datEffettiva);
				}

				if (datModGregorian != null) {
					politicaAttiva.setDtModPolitica(datModGregorian);
					politicaAttivaWS.setDt_mod_politica(dataMod);
				}
				politicheAttive.getPoliticaAttiva().add(politicaAttiva);
				politicheAttiveWS[i] = politicaAttivaWS;
			}
		}
		return new ErroreSifer(0);
	}

	private static ErroreSifer getSvantaggi(QueryExecutorObject qExec, DataConnection dc, SourceBean svantaggiBeanRows,
			ProfilingPatto profilingPatto, it.eng.sil.coop.webservices.siferAccreditamento.request.Patto pattoWS,
			String servizioProgrammaPatto) throws Exception {

		if (svantaggiBeanRows != null && !svantaggiBeanRows.getAttributeAsVector("ROW").isEmpty()) {
			Vector svantaggiBeanVector = svantaggiBeanRows.getAttributeAsVector("ROW");
			FlussoSILSIFER.Patti.ProfilingPatto.Svantaggi svantaggi = factory
					.createFlussoSILSIFERPattiProfilingPattoSvantaggi();
			List<String> listaSvantaggi = svantaggi.getTipoSvantaggio();
			it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio svantaggiWS[] = new it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio[svantaggiBeanVector
					.size()];
			for (int i = 0; i < svantaggiBeanVector.size(); i++) {
				SourceBean svantaggioBeanRow = (SourceBean) svantaggiBeanVector.elementAt(i);
				String codSvantaggio = (String) svantaggioBeanRow.getAttribute("CODSVANTAGGIO");
				it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio svantaggioCurr = new it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio();
				svantaggioCurr.setTipo_svantaggio(new Integer(codSvantaggio));
				listaSvantaggi.add(codSvantaggio);
				svantaggiWS[i] = svantaggioCurr;
			}
			profilingPatto.setSvantaggi(svantaggi);
			pattoWS.setSvantaggi(svantaggiWS);
		} else {
			FlussoSILSIFER.Patti.ProfilingPatto.Svantaggi svantaggi = factory
					.createFlussoSILSIFERPattiProfilingPattoSvantaggi();
			List<String> listaSvantaggi = svantaggi.getTipoSvantaggio();
			if (servizioProgrammaPatto != null && (servizioProgrammaPatto
					.equalsIgnoreCase(PattoBean.DB_PATTO_PROGRAMMA_PERSONALIZZATO_2018)
					|| servizioProgrammaPatto.equalsIgnoreCase(PattoBean.DB_PATTO_PROGRAMMA_PERSONALIZZATO_2019))) {
				listaSvantaggi.add(PattoBean.TIPO_SVANTAGGIO_AREA2);
			} else {
				listaSvantaggi.add(PattoBean.TIPO_SVANTAGGIO_AREA1);
			}
			profilingPatto.setSvantaggi(svantaggi);
			it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio svantaggiWS[] = new it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio[1];
			it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio svantaggioCurr = new it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio();
			if (servizioProgrammaPatto != null && (servizioProgrammaPatto
					.equalsIgnoreCase(PattoBean.DB_PATTO_PROGRAMMA_PERSONALIZZATO_2018)
					|| servizioProgrammaPatto.equalsIgnoreCase(PattoBean.DB_PATTO_PROGRAMMA_PERSONALIZZATO_2019))) {
				svantaggioCurr.setTipo_svantaggio(new Integer(PattoBean.TIPO_SVANTAGGIO_AREA2));
			} else if (servizioProgrammaPatto != null
					&& (servizioProgrammaPatto.equalsIgnoreCase(PattoBean.DB_COD_SERVIZIO_186)
							|| servizioProgrammaPatto.equalsIgnoreCase(PattoBean.DB_COD_SERVIZIO_NGG))) {
				svantaggioCurr.setTipo_svantaggio(new Integer(PattoBean.TIPO_SVANTAGGIO_AREA1));
			}
			svantaggiWS[0] = svantaggioCurr;
			pattoWS.setSvantaggi(svantaggiWS);
		}
		return new ErroreSifer(0);
	}

	/* Converte l'oggetto che rappresenta il messaggio di input in xml */
	private static String convertInputToString(FlussoSILSIFER msg) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(FlussoSILSIFER.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(msg, writer);
		String xml = writer.getBuffer().toString();
		return xml;
	}

	/* Valida l'xml passato in input con l'xsd passato come secondo parametro */
	private static boolean validazioneXml(String xml, String xsdPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			File schemaFile = new File(xsdPath);
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			StreamSource datiXmlStreamSource = new StreamSource(new StringReader(xml));
			validator.validate(datiXmlStreamSource);
			return true;
		} catch (Exception e) {
			_logger.error(e);
			return false;
		}
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

	public static QueryExecutorObject getQueryExecutorObject() throws NamingException, SQLException, EMFInternalError {
		InitialContext ctx = new InitialContext();
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = new QueryExecutorObject();

			qExec.setRequestContainer(null);
			qExec.setResponseContainer(null);
			qExec.setDataConnection(dc);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setTransactional(true);
			qExec.setDontForgetException(false);
		} else {
			_logger.error("Impossibile ottenere una connessione");
			return null;
		}
		return qExec;
	}

	public static void tracciaErrore(Connection conn, BigDecimal cdnLavoratore, ErroreSifer erroreSifer)
			throws Exception {
		PreparedStatement psUrl = null;
		psUrl = conn.prepareStatement(QUERY_STRING_TRACCIA_ERRORE);

		String err = "";
		if (erroreSifer.erroreEsteso != null && erroreSifer.erroreEsteso.length() > 500) {
			err = erroreSifer.erroreEsteso.substring(0, 500);
		} else {
			err = erroreSifer.erroreEsteso;
		}

		psUrl.setBigDecimal(1, cdnLavoratore);
		psUrl.setString(2, String.valueOf(erroreSifer.errCodDB));
		psUrl.setString(3, err);
		psUrl.setString(4, erroreSifer.getStrXMLInviato());

		// execute insert SQL stetement
		psUrl.executeUpdate();
		psUrl.close();
	}

	private static boolean checkEmail(String email) {
		if (email != null) {
			Matcher emailMatcher = XMLValidator.emailRegEx.matcher(email.trim());
			return emailMatcher.matches();
		} else {
			return false;
		}
	}

	public static String getEndpointUrl(Connection conn) throws Exception {
		PreparedStatement psUrl = null;
		String endPoint = null;
		ResultSet rsUrl = null;

		String statementUrl = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT WHERE strName = 'AccreditamentoPartecipante'";
		psUrl = conn.prepareStatement(statementUrl);

		rsUrl = psUrl.executeQuery();
		if (rsUrl.next()) {
			endPoint = rsUrl.getString("strUrl");
		}

		rsUrl.close();
		psUrl.close();

		return endPoint;
	}
}