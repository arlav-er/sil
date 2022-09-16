package it.eng.sil.module.seta;

import java.io.File;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.seta.ws.Ws_RendicontazioneProxy;
import it.eng.sil.coop.webservices.seta.ws.XmlResult;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.ComunicazioniObbligatorie;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.ComunicazioniObbligatorie.ComunicazioneObbligatoria;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.Partecipante;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.Patti;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.Patti.ProfilingPatto;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva;
import it.eng.sil.coop.webservices.seta.ws.input.ObjectFactory;
import it.eng.sil.coop.webservices.utils.TsEndpoint;
import it.eng.sil.coop.webservices.utils.ValidaXML;
import it.eng.sil.crypto.ConvBase64;
import it.eng.sil.module.flussi.bean.AzioneTracciato;
import it.eng.sil.module.flussi.bean.Flusso;
import it.eng.sil.module.flussi.bean.LavoratoreTracciato;
import it.eng.sil.module.flussi.bean.MovimentoTracciato;
import it.eng.sil.module.flussi.bean.PattoTracciato;
import it.eng.sil.module.movimenti.enumeration.CodMonoTipoEnum;
import it.eng.sil.module.patto.PartecipanteGGUtils;
import it.eng.sil.util.amministrazione.impatti.PattoBean;
import it.eng.sil.util.xml.XMLValidator;
import oracle.jdbc.OracleTypes;

public class RendicontazioneUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RendicontazioneUtils.class.getName());

	private static String QUERY_STRING_ACCORPAMENTO = "SELECT STRCODICEFISCALEACCORPATO FROM AN_LAVORATORE_ACCORPA WHERE CDNLAVORATORE = ? AND STRCODICEFISCALE = ?";

	private static String QUERY_STRING_POLITICHE_ATTIVE = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codsottoazione, az.prgazioneragg, "
			+ " az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, pc.strcfdatorelavoro, "
			+ " to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, "
			+ " pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, to_char(pc.dtmmod, 'dd/mm/yyyy') AS dtmmod, "
			+ " to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione, nvl(de_azione_ragg.flg_misurayei, 'N') flg_misurayei " // ,
																																	// doc.numprotocollo
																																	// "
			+ " FROM or_percorso_concordato pc INNER JOIN am_lav_patto_scelta ps ON (to_number(ps.strchiavetabella) = pc.prgpercorso) "
			+ " INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_azione_ragg on (az.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " INNER JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " INNER JOIN MN_YG_EVENTO ON (es.codeventomin = MN_YG_EVENTO.codevento) "
			// + " LEFT JOIN AM_DOCUMENTO_COLL coll ON (coll.strchiavetabella = pc.prgpercorso) "
			// + " LEFT JOIN AM_DOCUMENTO doc ON (doc.prgdocumento = coll.prgdocumento) "
			+ " WHERE ps.prgpattolavoratore = ? AND az.flgformazione = 'S' AND az.codazionesifer is not null AND es.flgformazione = 'S' and pc.codesito <> 'PRO' "
			+ " AND SYSDATE < es.datfineval AND ps.codlsttab = 'OR_PER' and nvl(de_azione_ragg.flg_misurayei, 'N') = 'S' AND SYSDATE < MN_YG_EVENTO.datfineval";

	private static String QUERY_STRING_POLITICHE_ATTIVE_PROGRAMMA = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codsottoazione, az.prgazioneragg, "
			+ " az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, pc.strcfdatorelavoro, "
			+ " to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, "
			+ " pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, to_char(pc.dtmmod, 'dd/mm/yyyy') AS dtmmod, "
			+ " to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione, nvl(de_azione_ragg.flg_misurayei, 'N') flg_misurayei " // ,
																																	// doc.numprotocollo
																																	// "
			+ " FROM or_percorso_concordato pc INNER JOIN am_lav_patto_scelta ps ON (to_number(ps.strchiavetabella) = pc.prgpercorso) "
			+ " INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_azione_ragg on (az.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " INNER JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " INNER JOIN MN_YG_EVENTO ON (es.codeventomin = MN_YG_EVENTO.codevento) "
			// + " LEFT JOIN AM_DOCUMENTO_COLL coll ON (coll.strchiavetabella = pc.prgpercorso) "
			// + " LEFT JOIN AM_DOCUMENTO doc ON (doc.prgdocumento = coll.prgdocumento) "
			+ " WHERE ps.prgpattolavoratore = ? AND co.prgcolloquio = ? AND az.flgformazione = 'S' AND az.codazionesifer is not null AND es.flgformazione = 'S' and pc.codesito <> 'PRO' "
			+ " AND SYSDATE < es.datfineval AND ps.codlsttab = 'OR_PER' and nvl(de_azione_ragg.flg_misurayei, 'N') = 'S' AND SYSDATE < MN_YG_EVENTO.datfineval";

	private static String QUERY_STRING_POLITICHE_ATTIVE_DOTE = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codsottoazione, az.prgazioneragg, "
			+ " az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, pc.strcfdatorelavoro, "
			+ " to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, "
			+ " pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, to_char(pc.dtmmod, 'dd/mm/yyyy') AS dtmmod, "
			+ " to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione, nvl(de_azione_ragg.flg_misurayei, 'N') flg_misurayei "
			+ " FROM or_percorso_concordato pc INNER JOIN am_lav_patto_scelta ps ON (to_number(ps.strchiavetabella) = pc.prgpercorso) "
			+ " INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_azione_ragg on (az.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " INNER JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " INNER JOIN MN_YG_EVENTO ON (es.codeventomin = MN_YG_EVENTO.codevento) "
			+ " WHERE ps.prgpattolavoratore = ? AND az.flgformazione = 'S' AND az.codazionesifer is not null AND es.flgformazione = 'S' and pc.codesito <> 'PRO' "
			+ " AND SYSDATE < es.datfineval AND ps.codlsttab = 'OR_PER' and nvl(de_azione_ragg.flg_misurayei, 'N') = 'N' AND SYSDATE < MN_YG_EVENTO.datfineval";

	private static String QUERY_STRING_POLITICHE_ATTIVE_DOTE_PROGRAMMA = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codsottoazione, az.prgazioneragg, "
			+ " az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, pc.strcfdatorelavoro, "
			+ " to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, "
			+ " pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, to_char(pc.dtmmod, 'dd/mm/yyyy') AS dtmmod, "
			+ " to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione, nvl(de_azione_ragg.flg_misurayei, 'N') flg_misurayei "
			+ " FROM or_percorso_concordato pc INNER JOIN am_lav_patto_scelta ps ON (to_number(ps.strchiavetabella) = pc.prgpercorso) "
			+ " INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_azione_ragg on (az.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " INNER JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " INNER JOIN MN_YG_EVENTO ON (es.codeventomin = MN_YG_EVENTO.codevento) "
			+ " WHERE ps.prgpattolavoratore = ? AND co.prgcolloquio = ? AND az.flgformazione = 'S' AND az.codazionesifer is not null AND es.flgformazione = 'S' and pc.codesito <> 'PRO' "
			+ " AND SYSDATE < es.datfineval AND ps.codlsttab = 'OR_PER' and nvl(de_azione_ragg.flg_misurayei, 'N') = 'N' AND SYSDATE < MN_YG_EVENTO.datfineval";

	private static String QUERY_A02_PATTO_GG = "SELECT pc.prgpercorso, pc.prgcolloquio, "
			+ " to_char(pcAdesione.datadesionegg, 'dd/mm/yyyy') AS datadesionegg "
			+ " FROM or_percorso_concordato pc INNER JOIN am_lav_patto_scelta ps ON (to_number(ps.strchiavetabella) = pc.prgpercorso) "
			+ " INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_azione_ragg on (az.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " LEFT JOIN or_percorso_concordato pcAdesione on (pc.prgpercorsoadesione = pcAdesione.prgpercorso and pc.prgcolloquioadesione = pcAdesione.prgcolloquio) "
			+ " WHERE ps.prgpattolavoratore = ? AND ps.codlsttab = 'OR_PER' AND az.flgformazione = 'S' AND az.codazionesifer = 'A02' "
			+ " AND de_azione_ragg.flg_misurayei = 'S' AND pc.codesito = 'FC' order by pc.datavvioazione desc";

	private static String QUERY_STRING_TRACCIA_ERRORE = "INSERT INTO TS_TRACCIAMENTO_FORMAZIONE (PRGTRACFORM, DATINS, CDNLAVORATORE, STRMESSAGGIOERRORE) VALUES (S_TS_TRACCIAMENTO_FORMAZIONE.NEXTVAL, SYSDATE , ?, ?)";

	private static String QUERY_SOGGETTO_ACCREDITATO_PROGRAMMIA_PATTO = "select strentecodicefiscale, codsede from am_programma_ente where prgpattolavoratore = ? and prgcolloquio = ?";

	private static String QUERY_INFO_PROGRAMMA_PATTO = "select to_char(coll.datcolloquio, 'dd/mm/yyyy') datcolloquio, to_char(coll.datfineprogramma, 'dd/mm/yyyy') datfineprogramma "
			+ " from or_colloquio coll where coll.prgcolloquio = ?";

	private static String QUERY_INFO_ENTE_PATTO_PROGRAMMA = "select strpartitaiva from an_vch_ente where strcodicefiscale = ? and codsede = ?";

	private static ObjectFactory factory = new ObjectFactory();

	private static final String INPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "rendicontazione" + File.separator + "rendicontazione.xsd";

	// gestione patto
	private HashMap<BigDecimal, String> pattiGGDote = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiTirocinio = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiAccompagnamento = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiCFEntePromotore = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiSedeEntePromotore = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiOspitante = new HashMap<BigDecimal, String>();

	// gestione programmi
	private HashMap<BigDecimal, String> pattiGGDoteProgramma = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiTirocinioProgramma = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiAccompagnamentoProgramma = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiCFEntePromotoreProgramma = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiSedeEntePromotoreProgramma = new HashMap<BigDecimal, String>();
	private HashMap<BigDecimal, String> pattiOspitanteProgramma = new HashMap<BigDecimal, String>();

	public RendicontazioneUtils() {
	}

	public ErroreSeta sendPartecipante(BigDecimal cdnLavoratore, SourceBean anLavBeanRows,
			SourceBean codProvinciaBeanRows, SourceBean pattoBeanRows, SourceBean movimentazioneBeanRows,
			SourceBean wsRows, boolean isFromBatch) throws Exception {
		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		boolean isTransactional = true;
		try {
			qExec = getQueryExecutorObject(isTransactional);
			dc = qExec.getDataConnection();
			_logger.error("Connessione istanziata");

			Formazione msg = factory.createFormazione();
			Partecipante partecipante = factory.createFormazionePartecipante();
			Patti patti = factory.createFormazionePatti();
			ComunicazioniObbligatorie comunicazioniObbligatorie = factory.createFormazioneComunicazioniObbligatorie();

			/* codice di errore di ritorno della funzione */
			ErroreSeta erroreSeta = null;

			erroreSeta = getPartecipante(qExec, dc, cdnLavoratore, partecipante, anLavBeanRows, codProvinciaBeanRows);
			if (erroreSeta.errCod != 0) {
				_logger.error("Rendicontazione: Invio non effettuato: cdnlavoratore = " + cdnLavoratore);
				return erroreSeta;
			}

			erroreSeta = getPatti(qExec, dc, patti, pattoBeanRows, cdnLavoratore);
			if (erroreSeta.errCod != 0) {
				_logger.error("Rendicontazione: Invio non effettuato: cdnlavoratore = " + cdnLavoratore);
				return erroreSeta;
			}

			getComunicazioniObbligatorie(qExec, dc, comunicazioniObbligatorie, movimentazioneBeanRows);

			msg.setPartecipante(partecipante);
			msg.setPatti(patti);
			msg.setComunicazioniObbligatorie(comunicazioniObbligatorie);

			String input = "";
			// validazione xsd dell'input
			try {
				input = RendicontazioneUtils.convertInputToString(msg);
				_logger.debug("SIL -> RENDICONTAZIONE: cdnLavoratore " + cdnLavoratore + " xml input\n" + input);
				ValidaXML validatore = new ValidaXML(input, INPUT_XSD);
				validatore.valida();
			} catch (Exception e) {
				_logger.error("Rendicontazione: Errore in validazione input. cdnlavoratore = " + cdnLavoratore
						+ " Tracciato = " + input);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,
						MessageCodes.RENDICONTAZIONE.ERR_WS_INPUT);
				String errMsg = error.getDescription();
				return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_INPUT, errMsg + ":\n" + e.getMessage());
			}

			boolean checkInvioTracciato = false;
			int numPatti = 0;
			int numAzioni = 0;
			int numMovimenti = 0;
			Flusso flussoLav = null;
			Flusso flussoCorrente = null;

			try {
				if (msg.getPatti() != null && msg.getPatti().getProfilingPatto() != null) {
					numPatti = msg.getPatti().getProfilingPatto().size();
					List<ProfilingPatto> listaProfiling = msg.getPatti().getProfilingPatto();
					for (int iP = 0; iP < listaProfiling.size(); iP++) {
						ProfilingPatto profiling = listaProfiling.get(iP);
						if (profiling.getPoliticheAttive() != null
								&& profiling.getPoliticheAttive().getPoliticaAttiva() != null) {
							numAzioni = numAzioni + profiling.getPoliticheAttive().getPoliticaAttiva().size();
						}
					}
				}

				if (msg.getPatti() != null && msg.getPatti().getProfilingPatto() != null) {
					numPatti = msg.getPatti().getProfilingPatto().size();
				}
				if (msg.getComunicazioniObbligatorie() != null
						&& msg.getComunicazioniObbligatorie().getComunicazioneObbligatoria() != null) {
					numMovimenti = msg.getComunicazioniObbligatorie().getComunicazioneObbligatoria().size();
				}
				// flusso del lavoratore inviato
				flussoLav = new Flusso(msg.getPartecipante().getCodiceFiscale(),
						it.eng.sil.coop.webservices.utils.Utils.SERVIZIORENDICONTAZIONE, qExec, dc);
				// flusso del lavoratore che devo inviare
				flussoCorrente = new Flusso(msg.getPartecipante().getCodiceFiscale(),
						it.eng.sil.coop.webservices.utils.Utils.SERVIZIORENDICONTAZIONE, numPatti, numAzioni,
						numMovimenti);

				if (isFromBatch) {
					if (flussoLav.esisteUltimoFlusso()) {
						if (flussoLav.compareTo(flussoCorrente) > 0) {
							checkInvioTracciato = true;
						}
						if (!checkInvioTracciato) {
							if (msg.getPartecipante() != null) {
								checkInvioTracciato = confrontaPartecipante(msg, flussoLav, flussoCorrente, qExec, dc);
							}
						}
						if (!checkInvioTracciato) {
							if (msg.getPatti() != null && msg.getPatti().getProfilingPatto() != null) {
								checkInvioTracciato = confrontaProfiling(msg, flussoLav, flussoCorrente, qExec, dc);
							}
						}
						if (!checkInvioTracciato) {
							if (msg.getComunicazioniObbligatorie() != null
									&& msg.getComunicazioniObbligatorie().getComunicazioneObbligatoria() != null) {
								checkInvioTracciato = confrontaMovimenti(msg, flussoLav, flussoCorrente, qExec, dc);
							}
						}
					} else {
						checkInvioTracciato = true;
					}
				} else {
					checkInvioTracciato = true;
				}
			} catch (Throwable e) {
				_logger.error("Il servizio invocato ha restituito un errore. Errore ricevuto: " + e.getMessage());
				checkInvioTracciato = true;
			}

			if (checkInvioTracciato) {
				/* Invocazione del servizio */
				Ws_RendicontazioneProxy proxy = new Ws_RendicontazioneProxy();

				// END_POINT_NAME_RENDICONTAZIONE
				TsEndpoint wspoint = new TsEndpoint(dc.getInternalConnection(),
						it.eng.sil.coop.webservices.utils.Utils.SERVIZIORENDICONTAZIONE);
				proxy.setEndpoint(wspoint.getEndpointUrl());

				String strUserId = null;
				String strPassword = null;
				if (wsRows != null) {
					wsRows = wsRows.containsAttribute("ROW") ? (SourceBean) wsRows.getAttribute("ROW") : wsRows;
					strUserId = (String) wsRows.getAttribute("STRUSERID");
					strPassword = (String) wsRows.getAttribute("CLN_PWD");
				}
				// String fileName = "cdnlavoratore.xml";
				String cdnLavStr = String.valueOf(cdnLavoratore);
				cdnLavStr += ".xml";
				String fileName = cdnLavStr;

				XmlResult output = null;
				String inputBase64 = ConvBase64.codificaStr(input);
				try {
					// output = proxy.verifyXml(strUserId, strPassword, fileName, input);
					output = proxy.verifyXml(strUserId, strPassword, fileName, inputBase64);
				} // try
				catch (Exception ex) {
					_logger.error("Il servizio invocato ha restituito un errore. Errore ricevuto: " + ex.getMessage());
					return new ErroreSeta(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE);
				}
				_logger.debug("SIL -> RENDICONTAZIONE: cdnLavoratore " + cdnLavoratore + " xml output\n" + output);

				String codiceRisposta = output.getCode();
				String descrErroreRisposta = output.getResult();
				if (codiceRisposta == null || !codiceRisposta.equals("0")) {
					_logger.error("Rendicontazione: Errore codice risposta RENDICONTAZIONE. errore ="
							+ descrErroreRisposta + " - cdnlavoratore = " + cdnLavoratore);
					return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_KO_WS_OUTPUT, descrErroreRisposta);
				} else {
					try {
						dc.initTransaction();
						if (flussoLav != null && flussoLav.esisteUltimoFlusso()) {
							boolean isOkDelete = flussoLav
									.deleteFlusso((BigDecimal) flussoLav.getAttribute("PRGFLUSSO"), qExec, dc);
							if (!isOkDelete) {
								throw new Exception("Errore nella cancellazione dell'ultimo flusso");
							}
						}
						boolean isOkFlusso = true;
						if (flussoCorrente != null) {
							isOkFlusso = flussoCorrente.insertFlusso(qExec, dc);
							if (!isOkFlusso) {
								throw new Exception("Errore nel salvataggio del flusso corrente");
							}
							// PARTECIPANTE
							if (msg.getPartecipante() != null) {
								LavoratoreTracciato lavPartecipante = new LavoratoreTracciato(
										(BigDecimal) flussoCorrente.getAttribute("PRGFLUSSO"), msg.getPartecipante());
								isOkFlusso = lavPartecipante.insertPartecipante(qExec, dc);
								if (!isOkFlusso) {
									throw new Exception("Errore nel salvataggio del flusso corrente:partecipante");
								}
								flussoCorrente.setInfoLavoratore(lavPartecipante);
							}
							// PROFILING
							if (msg.getPatti() != null && msg.getPatti().getProfilingPatto() != null) {
								for (int iPatti = 0; iPatti < msg.getPatti().getProfilingPatto().size(); iPatti++) {
									ProfilingPatto profilingCurr = msg.getPatti().getProfilingPatto().get(iPatti);
									PattoTracciato ptTracciato = new PattoTracciato(
											(BigDecimal) flussoCorrente.getAttribute("PRGFLUSSO"), profilingCurr);
									isOkFlusso = ptTracciato.insertProfiling(qExec, dc);
									if (!isOkFlusso) {
										throw new Exception("Errore nel salvataggio del flusso corrente:profiling");
									}
									flussoCorrente.setInfoPatto(ptTracciato);
									// AZIONI
									if (profilingCurr.getPoliticheAttive() != null
											&& profilingCurr.getPoliticheAttive().getPoliticaAttiva() != null) {
										for (int iAz = 0; iAz < profilingCurr.getPoliticheAttive().getPoliticaAttiva()
												.size(); iAz++) {
											PoliticaAttiva polProfiling = profilingCurr.getPoliticheAttive()
													.getPoliticaAttiva().get(iAz);
											AzioneTracciato az = new AzioneTracciato(
													(BigDecimal) flussoCorrente.getAttribute("PRGFLUSSO"),
													polProfiling);
											isOkFlusso = az.insertPolitica(qExec, dc);
											if (!isOkFlusso) {
												throw new Exception(
														"Errore nel salvataggio del flusso corrente:politica attiva");
											}
											flussoCorrente.setInfoAzione(az);
										}
									}
								}
							}
							// MOVIMENTI
							if (msg.getComunicazioniObbligatorie() != null
									&& msg.getComunicazioniObbligatorie().getComunicazioneObbligatoria() != null) {
								for (int iMov = 0; iMov < msg.getComunicazioniObbligatorie()
										.getComunicazioneObbligatoria().size(); iMov++) {
									ComunicazioneObbligatoria com = msg.getComunicazioniObbligatorie()
											.getComunicazioneObbligatoria().get(iMov);
									MovimentoTracciato movimento = new MovimentoTracciato(
											(BigDecimal) flussoCorrente.getAttribute("PRGFLUSSO"), com);
									isOkFlusso = movimento.insertCO(qExec, dc);
									if (!isOkFlusso) {
										throw new Exception(
												"Errore nel salvataggio del flusso corrente:comunicazione obbligatoria");
									}
									flussoCorrente.setInfoMovimento(movimento);
								}
							}
						}
						dc.commitTransaction();
					} catch (Throwable eFlusso) {
						dc.rollBackTransaction();
						_logger.error("Errore: " + eFlusso);
					}
				}
			} else {
				return new ErroreSeta(MessageCodes.Webservices.WS_TRACCIATO_INVARIATO);
			}
		} catch (Throwable e) {
			_logger.error("Errore: " + e);
			return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_GENERICO_TRACCIATO);
		} finally {
			_logger.error("Connessione rilasciata");
			Utils.releaseResources(dc, command, dr);
		}

		return new ErroreSeta(0);
	}

	/* Converte l'oggetto che rappresenta il messaggio di input in xml */
	private static String convertInputToString(Formazione msg) throws JAXBException, UnsupportedEncodingException {
		JAXBContext jc = JAXBContext.newInstance(Formazione.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(msg, writer);
		String xml = writer.getBuffer().toString();
		byte[] xmlBytes = xml.getBytes();
		byte[] xmlUtf8 = null;
		xmlUtf8 = new String(xmlBytes, "utf-8").getBytes("UTF-8");

		return new String(xmlUtf8);
	}

	private ErroreSeta getPartecipante(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore,
			Partecipante partecipante, SourceBean anLavBeanRows, SourceBean codProvinciaBeanRows)
			throws DatatypeConfigurationException, ParseException {
		String codProvincia = null;
		String strCodiceFiscale = null;
		String flgCFOk = null;
		String strCognome = null;
		String strNome = null;
		String strSesso = null;
		String datNasc = null;
		XMLGregorianCalendar datNascGregorian = null;
		String codComNas = null;
		String codCittadinanza = null;
		String strEmail = null;
		String codComRes = null;
		String strIndirizzoRes = null;
		String strCapRes = null;
		String codComDom = null;
		String strIndirizzoDom = null;
		String strCapDom = null;
		String recapitoTel = null;
		String dtmMod = null;

		if (anLavBeanRows != null) {
			strCodiceFiscale = (String) anLavBeanRows.getAttribute("ROW.STRCODICEFISCALE");
			flgCFOk = (String) anLavBeanRows.getAttribute("ROW.FLGCFOK");
			strCognome = (String) anLavBeanRows.getAttribute("ROW.STRCOGNOME");
			strNome = (String) anLavBeanRows.getAttribute("ROW.STRNOME");
			strSesso = (String) anLavBeanRows.getAttribute("ROW.STRSESSO");
			datNasc = (String) anLavBeanRows.getAttribute("ROW.DATNASC");
			if (datNasc != null) {
				datNascGregorian = toXMLGregorianCalendarDateOnly(datNasc);
			}
			codComNas = (String) anLavBeanRows.getAttribute("ROW.CODCOMNAS");
			codCittadinanza = (String) anLavBeanRows.getAttribute("ROW.CODCITTADINANZA");
			recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRCELL");
			strEmail = (String) anLavBeanRows.getAttribute("ROW.STREMAIL");
			codComRes = (String) anLavBeanRows.getAttribute("ROW.CODCOMRES");
			strIndirizzoRes = (String) anLavBeanRows.getAttribute("ROW.STRINDIRIZZORES");
			strCapRes = (String) anLavBeanRows.getAttribute("ROW.STRCAPRES");
			codComDom = (String) anLavBeanRows.getAttribute("ROW.CODCOMDOM");
			strIndirizzoDom = (String) anLavBeanRows.getAttribute("ROW.STRINDIRIZZODOM");
			strCapDom = (String) anLavBeanRows.getAttribute("ROW.STRCAPDOM");
			dtmMod = (String) anLavBeanRows.getAttribute("ROW.DTMMOD");
			if (recapitoTel == null || recapitoTel.equals("")) {
				recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRTELRES");
				if (recapitoTel == null || recapitoTel.equals("")) {
					recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRTELDOM");
					if (recapitoTel == null || recapitoTel.equals("")) {
						recapitoTel = (String) anLavBeanRows.getAttribute("ROW.STRTELALTRO");
					}
				}
			}
		}
		if (codProvinciaBeanRows != null) {
			codProvincia = (String) codProvinciaBeanRows.getAttribute("ROW.CODMIN");
		}

		/* dati obbligatori */
		if (strCodiceFiscale == null || strCodiceFiscale.isEmpty() || codProvincia == null || codProvincia.isEmpty()
				|| strCognome == null || strCognome.isEmpty() || strNome == null || strNome.isEmpty()
				|| strSesso == null || strSesso.isEmpty() || datNasc == null || datNasc.isEmpty() || codComNas == null
				|| codComNas.isEmpty() || strEmail == null || strEmail.isEmpty()) {
			return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATI_LAVORATORE);
		}

		partecipante.setCdnlavoratore(cdnLavoratore.toBigInteger());
		partecipante.setCodiceFiscale(strCodiceFiscale.trim());
		partecipante.setValiditaCf(flgCFOk);
		try {
			SourceBean prAccorpamentoRow = null;
			List<DataField> paramAcc = new ArrayList<DataField>();
			paramAcc.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
			paramAcc.add(dc.createDataField("", Types.VARCHAR, strCodiceFiscale.trim()));
			qExec.setInputParameters(paramAcc);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(QUERY_STRING_ACCORPAMENTO);
			prAccorpamentoRow = (SourceBean) qExec.exec();

			if (prAccorpamentoRow != null) {
				String codFiscaleAccorpato = (String) prAccorpamentoRow.getAttribute("ROW.STRCODICEFISCALEACCORPATO");
				if (codFiscaleAccorpato != null) {
					partecipante.setCodiceFiscaleOriginale(codFiscaleAccorpato.trim());
				}
			}
		} catch (Exception e) {
			_logger.error("Impossibile recuperare i dati relativi all'accorpamento lavoratore");
		}
		partecipante.setCodiceProvincia(codProvincia.trim());
		partecipante.setCognome(strCognome.trim());
		partecipante.setNome(strNome.trim());
		partecipante.setSesso(strSesso.trim());
		partecipante.setNascitaData(datNascGregorian);
		partecipante.setNascitaCodiceCatastale(codComNas.trim());
		if (codCittadinanza != null) {
			partecipante.setCittadinanza(codCittadinanza.trim());
		}
		if (recapitoTel != null) {
			partecipante.setRecapitoTelefonico(recapitoTel);
		}
		if (checkEmail(strEmail)) {
			partecipante.setEmail(strEmail.trim());
		} else {
			return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_EMAIL);
		}
		if (codComRes != null) {
			partecipante.setResidenzaCodiceCatastale(codComRes.trim());
		}
		if (strIndirizzoRes != null) {
			partecipante.setResidenzaIndirizzo(strIndirizzoRes.trim());
		}
		if (strCapRes != null) {
			partecipante.setResidenzaCap(strCapRes.trim());
		}
		if (codComDom != null) {
			partecipante.setDomicilioCodiceCatastale(codComDom.trim());
		}
		if (strIndirizzoDom != null) {
			partecipante.setDomicilioIndirizzo(strIndirizzoDom.trim());
		}
		if (strCapDom != null) {
			partecipante.setDomicilioCap(strCapDom.trim());
		}
		if (dtmMod != null) {
			XMLGregorianCalendar datModGregorian = toXMLGregorianCalendarDateOnly(dtmMod);
			partecipante.setDtModAnagrafica(datModGregorian);
		}

		return new ErroreSeta(0);
	}

	@SuppressWarnings("rawtypes")
	private ErroreSeta getPatti(QueryExecutorObject qExec, DataConnection dc, Patti patti, SourceBean pattiBeanRows,
			BigDecimal cdnLavoratore) throws Exception {

		if (pattiBeanRows != null) {
			Vector pattiBeanVector = pattiBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgPattoLavoratore = null;
			BigDecimal numProfiling = null;
			BigDecimal numIndiceSvantaggio = null;
			BigDecimal numIndiceSvantaggio2 = null;
			BigDecimal numIndiceSvantaggio150 = null;
			String datRiferimento = null;
			String datRiferimento150 = null;
			String datStipula = null;
			String codCpi = null;
			String dataChiusuraPatto = null;
			String motivoChiusuraPatto = null;
			String datAdesioneGg = null;
			BigDecimal numProtocollo = null;
			String codStatoOccupaz = null;
			String misuraPatto = null;
			String dataScadenzaPatto = null;
			String dataStipulaDid = null;
			String dtmMod = null;
			String cfEntePromotore = null;
			String codSedePromotore = null;
			XMLGregorianCalendar datStipulaGregorian = null;
			XMLGregorianCalendar dataChiusuraPattoGregorian = null;
			XMLGregorianCalendar datAdesioneGgGregorian = null;
			XMLGregorianCalendar dataScadenzaPattoGregorian = null;
			XMLGregorianCalendar dataStipulaDidGregorian = null;

			for (int i = 0; i < pattiBeanVector.size(); i++) {
				datStipulaGregorian = null;
				dataChiusuraPattoGregorian = null;
				datAdesioneGgGregorian = null;
				dataScadenzaPattoGregorian = null;
				dataStipulaDidGregorian = null;

				SourceBean pattiBeanRow = (SourceBean) pattiBeanVector.elementAt(i);

				dtmMod = (String) pattiBeanRow.getAttribute("DTMMOD");
				datStipula = (String) pattiBeanRow.getAttribute("DATSTIPULA");
				misuraPatto = (String) pattiBeanRow.getAttribute("CODTIPOPATTO");
				prgPattoLavoratore = (BigDecimal) pattiBeanRow.getAttribute("PRGPATTOLAVORATORE");

				ProfilingPatto profilingPatto = null;

				Vector<String> programmi = PartecipanteGGUtils.checkProgrammiPatto(qExec, dc, prgPattoLavoratore);
				boolean isPattoGG = false;
				boolean isPattoDote = false;
				boolean isPattoDoteIA = false;

				if (programmi != null && !programmi.isEmpty()) {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
						isPattoGG = true;
					}
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_DOTE)) {
						isPattoDote = true;
					}
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_DOTE_IA)) {
						isPattoDoteIA = true;
					}
				}

				// inizio gestione patto nuovo Adesione Nuovo Programma
				if (misuraPatto.equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {
					// programmi GG
					if (isPattoGG) {
						Vector<BigDecimal> programmiKey = PartecipanteGGUtils.checkProgrammiFromCodice(qExec, dc,
								prgPattoLavoratore, PattoBean.DB_MISURE_GARANZIA_GIOVANI);
						if (programmiKey != null && !programmiKey.isEmpty()) {
							for (int j = 0; j < programmiKey.size(); j++) {
								BigDecimal prgColloquioCurr = programmiKey.get(j);
								String cfEntePromotoreProgramma = null;
								String codSedePromotoreProgramma = null;
								SourceBean infoEnteProgramma = soggettoAccreditatoProgramma(qExec, dc,
										prgPattoLavoratore, prgColloquioCurr);
								if (infoEnteProgramma != null) {
									cfEntePromotoreProgramma = (String) infoEnteProgramma
											.getAttribute("row.strentecodicefiscale");
									codSedePromotoreProgramma = (String) infoEnteProgramma.getAttribute("row.codsede");
								}
								if (cfEntePromotoreProgramma != null) {
									SourceBean programmaRow = infoProgramma(qExec, dc, prgColloquioCurr);
									String dataInizioProgramma = "";
									String datFineProgramma = "";
									if (programmaRow != null) {
										dataInizioProgramma = programmaRow.getAttribute("row.datcolloquio") != null
												? programmaRow.getAttribute("row.datcolloquio").toString()
												: "";
										if (!dataInizioProgramma.equals("")) {
											pattiGGDoteProgramma.put(prgColloquioCurr, dataInizioProgramma);
										}
										datFineProgramma = programmaRow.getAttribute("row.datfineprogramma") != null
												? programmaRow.getAttribute("row.datfineprogramma").toString()
												: "";
									}
									profilingPatto = factory.createFormazionePattiProfilingPatto();
									ErroreSeta erroreProfiling = creaProfilingPatto(profilingPatto, prgColloquioCurr,
											cdnLavoratore, PattoBean.DB_MISURE_GARANZIA_GIOVANI, pattiBeanRow, qExec,
											dc, dataInizioProgramma, datFineProgramma, cfEntePromotoreProgramma,
											codSedePromotoreProgramma);

									if (erroreProfiling.errCod != 0) {
										return erroreProfiling;
									}

									profilingPatto.setCfSoggettoPromotore(cfEntePromotoreProgramma);
									pattiCFEntePromotoreProgramma.put(prgColloquioCurr, cfEntePromotoreProgramma);

									if (codSedePromotoreProgramma != null) {
										pattiSedeEntePromotoreProgramma.put(prgColloquioCurr,
												codSedePromotoreProgramma);
									}
									if (dtmMod != null) {
										profilingPatto.setDtModPatto(toXMLGregorianCalendarDateOnly(dtmMod));
									}

									Formazione.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
											.createFormazionePattiProfilingPattoPoliticheAttive();

									/* recupero delle azioni relative al patto */
									try {
										ErroreSeta erroreSeta = getPoliticheAttiveProgramma(qExec, dc,
												prgPattoLavoratore, prgColloquioCurr, profilingPatto, politicheAttive,
												patti, cdnLavoratore, datStipula, cfEntePromotoreProgramma,
												PattoBean.DB_MISURE_GARANZIA_GIOVANI,
												QUERY_STRING_POLITICHE_ATTIVE_PROGRAMMA);

										if (erroreSeta.errCod != 0) {
											/* riporto l'errore al chiamante */
											return erroreSeta;
										}
									} catch (Exception e) {
										_logger.error("Impossibile recuperare i dati relativi alle politiche attive: "
												+ cdnLavoratore);
										return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_RECUPERO_POLITICHE);
									}
								}
							}
						}
					}

					// programmi DOTE
					if (isPattoDote) {
						Vector<BigDecimal> programmiKey = PartecipanteGGUtils.checkProgrammiFromCodice(qExec, dc,
								prgPattoLavoratore, PattoBean.DB_MISURE_DOTE);
						if (programmiKey != null && !programmiKey.isEmpty()) {
							for (int j = 0; j < programmiKey.size(); j++) {
								BigDecimal prgColloquioCurr = programmiKey.get(j);
								String cfEntePromotoreProgramma = null;
								String codSedePromotoreProgramma = null;
								SourceBean infoEnteProgramma = soggettoAccreditatoProgramma(qExec, dc,
										prgPattoLavoratore, prgColloquioCurr);
								if (infoEnteProgramma != null) {
									cfEntePromotoreProgramma = (String) infoEnteProgramma
											.getAttribute("row.strentecodicefiscale");
									codSedePromotoreProgramma = (String) infoEnteProgramma.getAttribute("row.codsede");
								}
								if (cfEntePromotoreProgramma != null) {
									SourceBean programmaRow = infoProgramma(qExec, dc, prgColloquioCurr);
									String dataInizioProgramma = "";
									String datFineProgramma = "";
									if (programmaRow != null) {
										dataInizioProgramma = programmaRow.getAttribute("row.datcolloquio") != null
												? programmaRow.getAttribute("row.datcolloquio").toString()
												: "";
										if (!dataInizioProgramma.equals("")) {
											pattiGGDoteProgramma.put(prgColloquioCurr, dataInizioProgramma);
										}
										datFineProgramma = programmaRow.getAttribute("row.datfineprogramma") != null
												? programmaRow.getAttribute("row.datfineprogramma").toString()
												: "";
									}
									profilingPatto = factory.createFormazionePattiProfilingPatto();
									ErroreSeta erroreProfiling = creaProfilingPatto(profilingPatto, prgColloquioCurr,
											cdnLavoratore, PattoBean.DB_MISURE_DOTE, pattiBeanRow, qExec, dc,
											dataInizioProgramma, datFineProgramma, cfEntePromotoreProgramma,
											codSedePromotoreProgramma);

									if (erroreProfiling.errCod != 0) {
										return erroreProfiling;
									}

									profilingPatto.setCfSoggettoPromotore(cfEntePromotoreProgramma);
									pattiCFEntePromotoreProgramma.put(prgColloquioCurr, cfEntePromotoreProgramma);

									if (codSedePromotoreProgramma != null) {
										pattiSedeEntePromotoreProgramma.put(prgColloquioCurr,
												codSedePromotoreProgramma);
									}
									if (dtmMod != null) {
										profilingPatto.setDtModPatto(toXMLGregorianCalendarDateOnly(dtmMod));
									}

									Formazione.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
											.createFormazionePattiProfilingPattoPoliticheAttive();

									/* recupero delle azioni relative al patto */
									try {
										ErroreSeta erroreSeta = getPoliticheAttiveProgramma(qExec, dc,
												prgPattoLavoratore, prgColloquioCurr, profilingPatto, politicheAttive,
												patti, cdnLavoratore, datStipula, cfEntePromotoreProgramma,
												PattoBean.DB_MISURE_DOTE, QUERY_STRING_POLITICHE_ATTIVE_DOTE_PROGRAMMA);

										if (erroreSeta.errCod != 0) {
											/* riporto l'errore al chiamante */
											return erroreSeta;
										}
									} catch (Exception e) {
										_logger.error("Impossibile recuperare i dati relativi alle politiche attive: "
												+ cdnLavoratore);
										return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_RECUPERO_POLITICHE);
									}
								}
							}
						}
					}

					// programmi DOTEIA
					if (isPattoDoteIA) {
						Vector<BigDecimal> programmiKey = PartecipanteGGUtils.checkProgrammiFromCodice(qExec, dc,
								prgPattoLavoratore, PattoBean.DB_MISURE_DOTE_IA);
						if (programmiKey != null && !programmiKey.isEmpty()) {
							for (int j = 0; j < programmiKey.size(); j++) {
								BigDecimal prgColloquioCurr = programmiKey.get(j);
								String cfEntePromotoreProgramma = null;
								String codSedePromotoreProgramma = null;
								SourceBean infoEnteProgramma = soggettoAccreditatoProgramma(qExec, dc,
										prgPattoLavoratore, prgColloquioCurr);
								if (infoEnteProgramma != null) {
									cfEntePromotoreProgramma = (String) infoEnteProgramma
											.getAttribute("row.strentecodicefiscale");
									codSedePromotoreProgramma = (String) infoEnteProgramma.getAttribute("row.codsede");
								}
								if (cfEntePromotoreProgramma != null) {
									SourceBean programmaRow = infoProgramma(qExec, dc, prgColloquioCurr);
									String dataInizioProgramma = "";
									String datFineProgramma = "";
									if (programmaRow != null) {
										dataInizioProgramma = programmaRow.getAttribute("row.datcolloquio") != null
												? programmaRow.getAttribute("row.datcolloquio").toString()
												: "";
										if (!dataInizioProgramma.equals("")) {
											pattiGGDoteProgramma.put(prgColloquioCurr, dataInizioProgramma);
										}
										datFineProgramma = programmaRow.getAttribute("row.datfineprogramma") != null
												? programmaRow.getAttribute("row.datfineprogramma").toString()
												: "";
									}
									profilingPatto = factory.createFormazionePattiProfilingPatto();
									ErroreSeta erroreProfiling = creaProfilingPatto(profilingPatto, prgColloquioCurr,
											cdnLavoratore, PattoBean.DB_MISURE_DOTE_IA, pattiBeanRow, qExec, dc,
											dataInizioProgramma, datFineProgramma, cfEntePromotoreProgramma,
											codSedePromotoreProgramma);

									if (erroreProfiling.errCod != 0) {
										return erroreProfiling;
									}

									profilingPatto.setCfSoggettoPromotore(cfEntePromotoreProgramma);
									pattiCFEntePromotoreProgramma.put(prgColloquioCurr, cfEntePromotoreProgramma);

									if (codSedePromotoreProgramma != null) {
										pattiSedeEntePromotoreProgramma.put(prgColloquioCurr,
												codSedePromotoreProgramma);
									}
									if (dtmMod != null) {
										profilingPatto.setDtModPatto(toXMLGregorianCalendarDateOnly(dtmMod));
									}

									Formazione.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
											.createFormazionePattiProfilingPattoPoliticheAttive();

									/* recupero delle azioni relative al patto */
									try {
										ErroreSeta erroreSeta = getPoliticheAttiveProgramma(qExec, dc,
												prgPattoLavoratore, prgColloquioCurr, profilingPatto, politicheAttive,
												patti, cdnLavoratore, datStipula, cfEntePromotoreProgramma,
												PattoBean.DB_MISURE_DOTE_IA,
												QUERY_STRING_POLITICHE_ATTIVE_DOTE_PROGRAMMA);

										if (erroreSeta.errCod != 0) {
											/* riporto l'errore al chiamante */
											return erroreSeta;
										}
									} catch (Exception e) {
										_logger.error("Impossibile recuperare i dati relativi alle politiche attive: "
												+ cdnLavoratore);
										return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_RECUPERO_POLITICHE);
									}
								}
							}
						}
					}
				} else {
					numProfiling = (BigDecimal) pattiBeanRow.getAttribute("NUMPROFILING");
					numIndiceSvantaggio = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO");
					numIndiceSvantaggio2 = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO2");
					numIndiceSvantaggio150 = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO150");
					datRiferimento = (String) pattiBeanRow.getAttribute("DATRIFERIMENTO");
					datRiferimento150 = (String) pattiBeanRow.getAttribute("DATRIFERIMENTO150");
					if (datStipula != null) {
						datStipulaGregorian = toXMLGregorianCalendarDateOnly(datStipula);
					}
					codCpi = (String) pattiBeanRow.getAttribute("CODCPI");
					dataChiusuraPatto = (String) pattiBeanRow.getAttribute("DATFINE");
					if (dataChiusuraPatto != null) {
						dataChiusuraPattoGregorian = toXMLGregorianCalendarDateOnly(dataChiusuraPatto);
					}
					motivoChiusuraPatto = (String) pattiBeanRow.getAttribute("CODMOTIVOFINEATTO");
					dataScadenzaPatto = (String) pattiBeanRow.getAttribute("DATSCADCONFERMA");
					if (dataScadenzaPatto != null) {
						dataScadenzaPattoGregorian = toXMLGregorianCalendarDateOnly(dataScadenzaPatto);
					}
					numProtocollo = (BigDecimal) pattiBeanRow.getAttribute("NUMPROTOCOLLO");
					codStatoOccupaz = (String) pattiBeanRow.getAttribute("CODSTATOOCCUPAZ");
					dataStipulaDid = (String) pattiBeanRow.getAttribute("DATDICHIARAZIONE");
					if (dataStipulaDid != null) {
						dataStipulaDidGregorian = toXMLGregorianCalendarDateOnly(dataStipulaDid);
					}
					cfEntePromotore = (String) pattiBeanRow.getAttribute("STRENTECODICEFISCALE");
					codSedePromotore = (String) pattiBeanRow.getAttribute("CODSEDE");
					if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
						Vector<String> params = new Vector<String>();
						params.add(datStipula);
						/* recupero della data adesione GG relativa al patto Garanzia Giovani */
						try {
							Connection connection = dc.getInternalConnection();
							String codErrore = "";
							CallableStatement stmtCreaSap = connection
									.prepareCall("{? = call checkAdesionePatto(?, ?) }");

							stmtCreaSap.registerOutParameter(1, OracleTypes.VARCHAR);
							stmtCreaSap.setBigDecimal(2, prgPattoLavoratore);
							stmtCreaSap.registerOutParameter(3, OracleTypes.VARCHAR);
							stmtCreaSap.execute();

							codErrore = (String) stmtCreaSap.getString(1);
							datAdesioneGg = (String) stmtCreaSap.getString(3);

							if ("00".equals(codErrore)) {
								if (datAdesioneGg != null) {
									datAdesioneGgGregorian = toXMLGregorianCalendarDateOnly(datAdesioneGg);
								} else {
									return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_NULLA,
											params);
								}
							} else if ("01".equals(codErrore)) {
								return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_DIVERSE,
										params);
							} else if ("02".equals(codErrore)) {
								return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_NULLA, params);
							} else {
								return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_GENERICO,
										params);
							}
						} catch (Exception e) {
							_logger.error("Impossibile invocare la funzione CHECKADESIONEPATTO: cdnlavoratore = "
									+ cdnLavoratore);
							throw e;
						}

						// Controlli azione A02 - se esiste sia associata al patto GG - se non associata il patto non
						// deve essere inviato nel flusso
						// MessageCodes.RENDICONTAZIONE.ERR_WS_NO_PATTOGG_A02
						// boolean azioneA02Associata = false;
						// SourceBean politicaA02Rows = null;
						// List<DataField> paramA02 = new ArrayList<DataField>();
						// paramA02 = new ArrayList<DataField>();
						// paramA02.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
						// qExec.setInputParameters(paramA02);
						// qExec.setType(QueryExecutorObject.SELECT);
						// qExec.setStatement(QUERY_A02_PATTO_GG);
						//
						// politicaA02Rows = (SourceBean) qExec.exec();
						//
						// if (politicaA02Rows != null) {
						// Vector politicheA02Vector = politicaA02Rows.getAttributeAsVector("ROW");
						// if (politicheA02Vector.size() > 0) {
						// azioneA02Associata = true;
						// SourceBean politicaRow = (SourceBean) politicheA02Vector.elementAt(0);
						// datAdesioneGg = (String) politicaRow.getAttribute("DATADESIONEGG");
						// }
						// }

						//
						profilingPatto = factory.createFormazionePattiProfilingPatto();

						profilingPatto.setPrgpatto(prgPattoLavoratore.toBigInteger());
						if (numIndiceSvantaggio2 != null) {
							profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
						} else {
							return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_INDICE_SVAN_NEW, params);
						}
						// IndiceSvantaggioVecchio non piu' gestito per la calabria
						// if (numIndiceSvantaggio != null) {
						// profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
						// }
						if (datRiferimento != null) {
							profilingPatto.setIndiceDataRiferimento(toXMLGregorianCalendarDateOnly(datRiferimento));
						}
						profilingPatto.setAdesioneGgData(datAdesioneGgGregorian);
						// if (numIndiceSvantaggio150 != null) {
						// profilingPatto.setProfiling150(numIndiceSvantaggio150.toBigInteger());
						// }
						// segnalazione redmine 7301
						if (numProfiling != null) {
							profilingPatto.setProfiling150(numProfiling.toBigInteger());
						}
						if (datRiferimento150 != null) {
							profilingPatto.setDataRiferimento150(toXMLGregorianCalendarDateOnly(datRiferimento150));
						}
						profilingPatto.setPattoData(datStipulaGregorian);
						profilingPatto.setPattoInclusioneAttiva("N");
						profilingPatto.setPattoDote("N");
						profilingPatto.setPattoGG("S");
						if (numProtocollo != null) {
							profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
						}
						if (codStatoOccupaz != null) {
							profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
						}
						if (codCpi != null) {
							profilingPatto.setPattoCpi(codCpi.trim());
						}
						if (dataChiusuraPattoGregorian != null) {
							profilingPatto.setDataChiusuraPatto(dataChiusuraPattoGregorian);
						}
						if (motivoChiusuraPatto != null) {
							profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
						}
						if (misuraPatto != null) {
							profilingPatto.setTipoMisuraPatto(misuraPatto);
						}
						if (dataScadenzaPattoGregorian != null) {
							profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
						}
						if (dataStipulaDidGregorian != null) {
							profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
						}
						if (cfEntePromotore != null) {
							profilingPatto.setCfSoggettoPromotore(cfEntePromotore);
						}
						if (dtmMod != null) {
							profilingPatto.setDtModPatto(toXMLGregorianCalendarDateOnly(dtmMod));
						}
						if (cfEntePromotore != null) {
							pattiCFEntePromotore.put(prgPattoLavoratore, cfEntePromotore);
						}
						if (codSedePromotore != null) {
							pattiSedeEntePromotore.put(prgPattoLavoratore, codSedePromotore);
						}
					} else {
						if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_DOTE)
								|| misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_DOTE_IA)) {
							profilingPatto = factory.createFormazionePattiProfilingPatto();

							profilingPatto.setPrgpatto(prgPattoLavoratore.toBigInteger());
							if (numIndiceSvantaggio2 != null) {
								profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
							}
							// IndiceSvantaggioVecchio non piu' gestito per la calabria
							// if (numIndiceSvantaggio != null) {
							// profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
							// }
							if (datRiferimento != null) {
								profilingPatto.setIndiceDataRiferimento(toXMLGregorianCalendarDateOnly(datRiferimento));
							}
							if (numProfiling != null) {
								profilingPatto.setProfiling150(numProfiling.toBigInteger());
							}
							if (datRiferimento150 != null) {
								profilingPatto.setDataRiferimento150(toXMLGregorianCalendarDateOnly(datRiferimento150));
							}
							profilingPatto.setPattoData(datStipulaGregorian);
							if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_DOTE)) {
								profilingPatto.setPattoInclusioneAttiva("N");
								profilingPatto.setPattoDote("S");
							} else {
								profilingPatto.setPattoInclusioneAttiva("S");
								profilingPatto.setPattoDote("N");
							}
							profilingPatto.setPattoGG("N");
							if (numProtocollo != null) {
								profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
							}
							if (codStatoOccupaz != null) {
								profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
							}
							if (codCpi != null) {
								profilingPatto.setPattoCpi(codCpi.trim());
							}
							if (dataChiusuraPattoGregorian != null) {
								profilingPatto.setDataChiusuraPatto(dataChiusuraPattoGregorian);
							}
							if (motivoChiusuraPatto != null) {
								profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
							}
							if (misuraPatto != null) {
								profilingPatto.setTipoMisuraPatto(misuraPatto);
							}
							if (dataScadenzaPattoGregorian != null) {
								profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
							}
							if (dataStipulaDidGregorian != null) {
								profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
							}
							if (cfEntePromotore != null) {
								profilingPatto.setCfSoggettoPromotore(cfEntePromotore);
							}
							if (dtmMod != null) {
								profilingPatto.setDtModPatto(toXMLGregorianCalendarDateOnly(dtmMod));
							}
							if (cfEntePromotore != null) {
								pattiCFEntePromotore.put(prgPattoLavoratore, cfEntePromotore);
							}
							if (codSedePromotore != null) {
								pattiSedeEntePromotore.put(prgPattoLavoratore, codSedePromotore);
							}
						}
					}

					if (profilingPatto != null) {
						pattiGGDote.put(prgPattoLavoratore, datStipula);

						Formazione.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
								.createFormazionePattiProfilingPattoPoliticheAttive();
						try {
							SourceBean politicheAttiveBeanRows = null;
							List<DataField> param = new ArrayList<DataField>();
							param = new ArrayList<DataField>();
							param.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
							qExec.setInputParameters(param);
							qExec.setType(QueryExecutorObject.SELECT);

							if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
								qExec.setStatement(QUERY_STRING_POLITICHE_ATTIVE);
							} else {
								qExec.setStatement(QUERY_STRING_POLITICHE_ATTIVE_DOTE);
							}

							politicheAttiveBeanRows = (SourceBean) qExec.exec();
							ErroreSeta erroreSeta = getPoliticheAttive(politicheAttive, politicheAttiveBeanRows,
									prgPattoLavoratore, cdnLavoratore, misuraPatto, datStipula, cfEntePromotore, null);
							if (erroreSeta.errCod != 0) {
								return erroreSeta;
							}

							profilingPatto.setPoliticheAttive(politicheAttive);

						} catch (Exception e) {
							_logger.error(
									"Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
							return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_RECUPERO_POLITICHE);
						}

						patti.getProfilingPatto().add(profilingPatto);
					}

				}
			}
		}

		/* controllo di avere almeno un patto */
		if (patti.getProfilingPatto().size() == 0) {
			return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_NO_PATTI);
		}

		return new ErroreSeta(0);
	}

	@SuppressWarnings("rawtypes")
	private ErroreSeta getPoliticheAttive(Formazione.Patti.ProfilingPatto.PoliticheAttive politicheAttive,
			SourceBean politicheAttiveBeanRows, BigDecimal prgPattoLavoratore, BigDecimal cdnLavoratore,
			String misuraPatto, String datStipula, String cfEntePromotore, BigDecimal prgProgramma) throws Exception {

		if (politicheAttiveBeanRows == null || politicheAttiveBeanRows.getAttributeAsVector("ROW").isEmpty()) {
			return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_NO_POLITICHE_ATTIVE);
		} else if (politicheAttiveBeanRows != null) {
			Vector politicheAttiveBeanVector = politicheAttiveBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgPercorso = null;
			BigDecimal prgColloquio = null;
			String codAzioneSifer = null;
			BigDecimal prgAzioneRagg = null;
			String datColloquio = null;
			String datEffettiva = null;
			String datStimata = null;
			BigDecimal numYgDurataEff = null;
			BigDecimal numYgDurataMin = null;
			BigDecimal numYgDurataMax = null;
			String codTipologiaDurata = null;
			String codEsito = null;
			String dtmMod = null;
			String datAvvioAzione = null;
			XMLGregorianCalendar datAvvioAzioneGregorian = null;
			String flgMisuraYei = null;
			String datMinTirocinio = null;
			String cfAziendaOspitante = null;
			String datMinAccompagnamento = null;
			String strCFDatoreLavoro = null;
			// BigDecimal m5numprotocollo = null;

			for (int i = 0; i < politicheAttiveBeanVector.size(); i++) {
				XMLGregorianCalendar datColloquioGregorian = null;
				XMLGregorianCalendar datEffettivaGregorian = null;
				XMLGregorianCalendar datStimataGregorian = null;
				SourceBean politicheAttiveBeanRow = (SourceBean) politicheAttiveBeanVector.elementAt(i);

				prgPercorso = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGPERCORSO");
				prgColloquio = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGCOLLOQUIO");
				codAzioneSifer = (String) politicheAttiveBeanRow.getAttribute("CODAZIONESIFER");
				prgAzioneRagg = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGAZIONERAGG");
				datColloquio = (String) politicheAttiveBeanRow.getAttribute("DATCOLLOQUIO");
				if (datColloquio != null) {
					datColloquioGregorian = toXMLGregorianCalendarDateOnly(datColloquio);
				}
				datEffettiva = (String) politicheAttiveBeanRow.getAttribute("DATEFFETTIVA");
				if (datEffettiva != null) {
					datEffettivaGregorian = toXMLGregorianCalendarDateOnly(datEffettiva);
				}
				datStimata = (String) politicheAttiveBeanRow.getAttribute("DATSTIMATA");
				if (datStimata != null) {
					datStimataGregorian = toXMLGregorianCalendarDateOnly(datStimata);
				}
				numYgDurataEff = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAEFF");
				numYgDurataMin = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAMIN");
				numYgDurataMax = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAMAX");
				codTipologiaDurata = (String) politicheAttiveBeanRow.getAttribute("CODTIPOLOGIADURATA");
				codEsito = (String) politicheAttiveBeanRow.getAttribute("CODESITO");
				datAvvioAzione = (String) politicheAttiveBeanRow.getAttribute("DATAVVIOAZIONE");
				if (datAvvioAzione != null) {
					datAvvioAzioneGregorian = toXMLGregorianCalendarDateOnly(datAvvioAzione);
				}
				dtmMod = (String) politicheAttiveBeanRow.getAttribute("DTMMOD");
				flgMisuraYei = (String) politicheAttiveBeanRow.getAttribute("FLG_MISURAYEI");
				strCFDatoreLavoro = (String) politicheAttiveBeanRow.getAttribute("STRCFDATORELAVORO");

				/* dati obbligatori */
				if (codAzioneSifer == null || prgPercorso == null || prgColloquio == null || prgAzioneRagg == null
						|| codEsito == null | codEsito.isEmpty()) {
					// Nel caso in cui uno di questi valori sia vuoto, la
					// politica attiva (azione SIL), non viene estratta.
					continue;
				}

				if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
					// m5numprotocollo = (BigDecimal) politicheAttiveBeanRow.getAttribute("numprotocollo");
					if (codAzioneSifer != null && codAzioneSifer.equalsIgnoreCase("C06") && flgMisuraYei != null
							&& flgMisuraYei.equalsIgnoreCase("S")) {
						if (datMinTirocinio == null) {
							if (datAvvioAzione != null && !datAvvioAzione.equals("")) {
								datMinTirocinio = datAvvioAzione;
								cfAziendaOspitante = strCFDatoreLavoro;
							}
						} else {
							if (datAvvioAzione != null && !datAvvioAzione.equals("")) {
								if (DateUtils.compare(datAvvioAzione, datMinTirocinio) < 0) {
									datMinTirocinio = datAvvioAzione;
									cfAziendaOspitante = strCFDatoreLavoro;
								}
							}
						}
					} else {
						if (codAzioneSifer != null && codAzioneSifer.equalsIgnoreCase("B03") && flgMisuraYei != null
								&& flgMisuraYei.equalsIgnoreCase("S")) {
							if (datMinAccompagnamento == null) {
								if (datEffettiva != null && !datEffettiva.equals("")) {
									datMinAccompagnamento = datEffettiva;
								}
							} else {
								if (datEffettiva != null && !datEffettiva.equals("")) {
									if (DateUtils.compare(datEffettiva, datMinAccompagnamento) < 0) {
										datMinAccompagnamento = datEffettiva;
									}
								}
							}
						}
					}
				} else {
					if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_DOTE)
							|| misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_DOTE_IA)) {
						if ((codAzioneSifer != null) && (codAzioneSifer.equalsIgnoreCase("C06"))
								&& (flgMisuraYei == null || !flgMisuraYei.equalsIgnoreCase("S"))) {
							if (datMinTirocinio == null) {
								if (datAvvioAzione != null && !datAvvioAzione.equals("")) {
									datMinTirocinio = datAvvioAzione;
									cfAziendaOspitante = strCFDatoreLavoro;
								}
							} else {
								if (datAvvioAzione != null && !datAvvioAzione.equals("")) {
									if (DateUtils.compare(datAvvioAzione, datMinTirocinio) < 0) {
										datMinTirocinio = datAvvioAzione;
										cfAziendaOspitante = strCFDatoreLavoro;
									}
								}
							}
						} else {
							if ((codAzioneSifer != null)
									&& (codAzioneSifer.equalsIgnoreCase("D2") || codAzioneSifer.equalsIgnoreCase("D5"))
									&& (flgMisuraYei == null || !flgMisuraYei.equalsIgnoreCase("S"))) {
								if (datMinAccompagnamento == null) {
									if (datEffettiva != null && !datEffettiva.equals("")) {
										datMinAccompagnamento = datEffettiva;
									}
								} else {
									if (datEffettiva != null && !datEffettiva.equals("")) {
										if (DateUtils.compare(datEffettiva, datMinAccompagnamento) < 0) {
											datMinAccompagnamento = datEffettiva;
										}
									}
								}
							}
						}
					}
				}

				Formazione.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva politicaAttiva = factory
						.createFormazionePattiProfilingPattoPoliticheAttivePoliticaAttiva();

				if (prgPercorso != null) {
					politicaAttiva.setPrgPercorso(prgPercorso.toBigInteger());
				}
				if (prgColloquio != null) {
					politicaAttiva.setPrgColloquio(prgColloquio.toBigInteger());
				}
				politicaAttiva.setCodazioneformcal(codAzioneSifer);
				politicaAttiva.setDataColloquio(datColloquioGregorian);
				/*
				 * if (m5numprotocollo != null) { politicaAttiva.setM5NumeroProtocollo(m5numprotocollo.toBigInteger());
				 * }
				 */
				politicaAttiva.setDataStimataFineAttivita(datStimataGregorian);
				politicaAttiva.setDataAvvioAttivita(datAvvioAzioneGregorian);
				politicaAttiva.setDataFineAttivita(datEffettivaGregorian);
				if (numYgDurataMin != null && (numYgDurataMin.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataMinima(numYgDurataMin.toBigInteger());
				}
				if (numYgDurataMax != null && (numYgDurataMax.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataMassima(numYgDurataMax.toBigInteger());
				}
				if (numYgDurataEff != null && (numYgDurataEff.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataEffettiva(numYgDurataEff.toBigInteger());
				}
				if (codTipologiaDurata != null) {
					politicaAttiva.setTipologiaDurata(codTipologiaDurata.trim());
				}
				if (codEsito != null) {
					politicaAttiva.setEsito(codEsito.trim());
				}
				if (dtmMod != null) {
					politicaAttiva.setDtModPolitica(toXMLGregorianCalendarDateOnly(dtmMod));
				}

				politicheAttive.getPoliticaAttiva().add(politicaAttiva);
			}

			if (datMinTirocinio != null) {
				if (prgProgramma == null) {
					pattiTirocinio.put(prgPattoLavoratore, datMinTirocinio);
					if (cfAziendaOspitante != null) {
						pattiOspitante.put(prgPattoLavoratore, cfAziendaOspitante);
					}
				} else {
					pattiTirocinioProgramma.put(prgProgramma, datMinTirocinio);
					if (cfAziendaOspitante != null) {
						pattiOspitanteProgramma.put(prgProgramma, cfAziendaOspitante);
					}
				}
			}

			if (datMinAccompagnamento != null) {
				if (prgProgramma == null) {
					pattiAccompagnamento.put(prgPattoLavoratore, datMinAccompagnamento);
				} else {
					pattiAccompagnamentoProgramma.put(prgProgramma, datMinAccompagnamento);
				}
			}
		}

		return new ErroreSeta(0);
	}

	@SuppressWarnings("rawtypes")
	private ErroreSeta getComunicazioniObbligatorie(QueryExecutorObject qExec, DataConnection dc,
			ComunicazioniObbligatorie comunicazioniObbligatorie, SourceBean movimentazioneBeanRows) throws Exception {

		boolean inviaCO = false;
		HashMap<BigDecimal, String> listaMovimenti = new HashMap<BigDecimal, String>();

		if (pattiGGDote.size() > 0 || pattiGGDoteProgramma.size() > 0) {
			inviaCO = true;
		}
		if (inviaCO && movimentazioneBeanRows != null) {
			Vector movimentazioneBeanVector = movimentazioneBeanRows.getAttributeAsVector("ROW");

			String strCodiceFiscaleDatore = null;
			String strRagioneSociale = null;
			String codComunicazione = null;
			String strIndirizzo = null;
			String codAzAteco = null;
			String azUtiCf = null;
			String azUtiDenom = null;
			String azUtiInd = null;
			String codAzutiAteco = null;
			String datInizioMov = null;
			String datFineMov = null;
			String datFinePf = null;
			String codMansioneMin = null;
			String mansioneDesc = null;
			String codContratto = null;
			String flgLavoroStagional = null;
			String flgLavoroAgr = null;
			String codOrario = null;
			BigDecimal numoreSett = null;
			String azUtiCom = null;
			String azCom = null;
			String isMissione = null;
			String numConvenzione = null;
			String codTipoEntePromotore = null;
			String datConvenzione = null;
			String codCategoriaTir = null;
			String codTipologiaTir = null;
			String strCodiceFiscaleTutore = null;
			String strCognomeTutore = null;
			String strNomeTutore = null;
			String codQualificaSrq = null;
			String indirizzoLavoro = null;
			String comuneLavoro = null;
			BigDecimal prgMovimento = null;
			BigDecimal prgMovimentoRett = null;
			String dataInvioCo = null;
			String codTipoMov = null;
			String codMonoTipo = null;
			// gestione patto
			HashMap<BigDecimal, String> aziendaAssunzioneAccompagnamento = new HashMap<BigDecimal, String>();
			HashMap<BigDecimal, String> pattiCOTirocinio = new HashMap<BigDecimal, String>();
			HashMap<BigDecimal, String> pattiCOAccompagnamento = new HashMap<BigDecimal, String>();
			HashMap<BigDecimal, String> pattiCOPrecAccompagnamento = new HashMap<BigDecimal, String>();
			// gestione programmi
			HashMap<BigDecimal, String> aziendaAssunzioneAccompagnamentoProgramma = new HashMap<BigDecimal, String>();
			HashMap<BigDecimal, String> programmiCOTirocinio = new HashMap<BigDecimal, String>();
			HashMap<BigDecimal, String> programmiCOAccompagnamento = new HashMap<BigDecimal, String>();
			HashMap<BigDecimal, String> programmiCOPrecAccompagnamento = new HashMap<BigDecimal, String>();

			for (int i = 0; i < movimentazioneBeanVector.size(); i++) {
				XMLGregorianCalendar datInizioMovGregorian = null;
				XMLGregorianCalendar datFinePfGregorian = null;
				XMLGregorianCalendar datFineMovGregorian = null;

				SourceBean movimentazioneBeanRow = (SourceBean) movimentazioneBeanVector.elementAt(i);
				// ospitante
				strCodiceFiscaleDatore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALE");
				strRagioneSociale = (String) movimentazioneBeanRow.getAttribute("STRRAGIONESOCIALE");
				codComunicazione = (String) movimentazioneBeanRow.getAttribute("CODCOMUNICAZIONE");
				strIndirizzo = (String) movimentazioneBeanRow.getAttribute("STRINDIRIZZO");
				codAzAteco = (String) movimentazioneBeanRow.getAttribute("CODAZATECO");
				// promotore
				azUtiCf = (String) movimentazioneBeanRow.getAttribute("AZUTICF");
				azUtiDenom = (String) movimentazioneBeanRow.getAttribute("AZUTIDENOM");
				azUtiInd = (String) movimentazioneBeanRow.getAttribute("AZUTIIND");
				codAzutiAteco = (String) movimentazioneBeanRow.getAttribute("CODAZUTIATECO");
				datInizioMov = (String) movimentazioneBeanRow.getAttribute("DATINIZIOMOV");
				if (datInizioMov != null) {
					datInizioMovGregorian = toXMLGregorianCalendarDateOnly(datInizioMov);
				}
				datFineMov = (String) movimentazioneBeanRow.getAttribute("DATFINEMOV");
				if (datFineMov != null) {
					datFineMovGregorian = toXMLGregorianCalendarDateOnly(datFineMov);
				}
				datFinePf = (String) movimentazioneBeanRow.getAttribute("DATFINEPF");
				if (datFinePf != null) {
					datFinePfGregorian = toXMLGregorianCalendarDateOnly(datFinePf);
				}
				codMansioneMin = (String) movimentazioneBeanRow.getAttribute("CODMANSIONE_MIN");
				mansioneDesc = (String) movimentazioneBeanRow.getAttribute("MANSIONE_DESC");
				codContratto = (String) movimentazioneBeanRow.getAttribute("CODCONTRATTO");
				flgLavoroStagional = (String) movimentazioneBeanRow.getAttribute("FLGLAVOROSTAGIONALE");
				flgLavoroAgr = (String) movimentazioneBeanRow.getAttribute("FLGLAVOROAGR");
				codOrario = (String) movimentazioneBeanRow.getAttribute("CODORARIO");
				numoreSett = (BigDecimal) movimentazioneBeanRow.getAttribute("NUMORESETT");
				azUtiCom = (String) movimentazioneBeanRow.getAttribute("AZUTICOM");
				azCom = (String) movimentazioneBeanRow.getAttribute("AZCOM");
				isMissione = (String) movimentazioneBeanRow.getAttribute("ISMISSIONE");
				numConvenzione = (String) movimentazioneBeanRow.getAttribute("NUMCONVENZIONE");
				codTipoEntePromotore = (String) movimentazioneBeanRow.getAttribute("CODSOGGPROMOTOREMIN");
				datConvenzione = (String) movimentazioneBeanRow.getAttribute("DATCONVENZIONE");
				codCategoriaTir = (String) movimentazioneBeanRow.getAttribute("CODCATEGORIATIR");
				codTipologiaTir = (String) movimentazioneBeanRow.getAttribute("CODTIPOLOGIATIR");
				strCodiceFiscaleTutore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALETUTORE");
				strCognomeTutore = (String) movimentazioneBeanRow.getAttribute("STRCOGNOMETUTORE");
				strNomeTutore = (String) movimentazioneBeanRow.getAttribute("STRNOMETUTORE");
				codQualificaSrq = (String) movimentazioneBeanRow.getAttribute("CODQUALIFICASRQ");
				prgMovimento = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTO");
				prgMovimentoRett = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTORETT");
				dataInvioCo = (String) movimentazioneBeanRow.getAttribute("DATCOMUNICAZ");
				codTipoMov = (String) movimentazioneBeanRow.getAttribute("CODTIPOMOV");
				codMonoTipo = (String) movimentazioneBeanRow.getAttribute("CODMONOTIPO");

				// controllo dati obbligatori
				if (codComunicazione == null || codComunicazione.isEmpty() || strCodiceFiscaleDatore == null
						|| strCodiceFiscaleDatore.isEmpty() || codAzAteco == null || codAzAteco.isEmpty()
						|| datInizioMov == null || datInizioMov.isEmpty()) {
					// Nel caso in cui uno di questi valori sia vuoto la comunicazione obbligatoria non viene estratta.
					continue;
				}

				boolean inviaCOFlusso = false;

				if (!listaMovimenti.containsKey(prgMovimento)) {
					if (codMonoTipo != null && codMonoTipo.equalsIgnoreCase(CodMonoTipoEnum.TIROCINIO.getCodice())) {
						// gestione patto
						if (pattiGGDote.size() > 0) {
							Set set = pattiGGDote.entrySet();
							Iterator iterator = set.iterator();
							while (iterator.hasNext() && !listaMovimenti.containsKey(prgMovimento)) {
								Map.Entry mentry = (Map.Entry) iterator.next();
								BigDecimal keyPatto = (BigDecimal) mentry.getKey();
								String dataPatto = (String) mentry.getValue();
								String dataTirocinioPatto = pattiTirocinio.get(keyPatto);
								String cfPromotore = pattiCFEntePromotore.get(keyPatto);
								String cfOspitante = pattiOspitante.get(keyPatto);
								boolean isOKPromotore = false;
								if (cfPromotore != null && !cfPromotore.equals("") && azUtiCf != null
										&& cfPromotore.equalsIgnoreCase(azUtiCf)) {
									isOKPromotore = true;
								}
								if (!isOKPromotore) {
									String codSedeEntePromotore = pattiSedeEntePromotore.get(keyPatto);
									if (cfPromotore != null && codSedeEntePromotore != null) {
										SourceBean rowEnte = infoEnte(qExec, dc, cfPromotore, codSedeEntePromotore);
										if (rowEnte != null) {
											String strPartitaIva = (String) rowEnte.getAttribute("row.strpartitaiva");
											if (strPartitaIva != null && !strPartitaIva.equals("") && azUtiCf != null
													&& strPartitaIva.equalsIgnoreCase(azUtiCf)) {
												isOKPromotore = true;
											}
										}
									}
								}

								if (datInizioMov != null && !datInizioMov.equals("") && dataPatto != null
										&& !dataPatto.equals("") && DateUtils.compare(datInizioMov, dataPatto) >= 0
										&& dataTirocinioPatto != null && !dataTirocinioPatto.equals("")
										&& DateUtils.compare(datInizioMov, dataTirocinioPatto) >= 0 && isOKPromotore
										&& cfOspitante != null && strCodiceFiscaleDatore != null
										&& cfOspitante.equalsIgnoreCase(strCodiceFiscaleDatore)) {
									if (!pattiCOTirocinio.containsKey(keyPatto)) {
										listaMovimenti.put(prgMovimento, "1");
										pattiCOTirocinio.put(keyPatto, "1");
										inviaCOFlusso = true;
									}
								}
							}
						}
						// gestione programmi
						if (pattiGGDoteProgramma.size() > 0) {
							Set set = pattiGGDoteProgramma.entrySet();
							Iterator iterator = set.iterator();
							while (iterator.hasNext() && !listaMovimenti.containsKey(prgMovimento)) {
								Map.Entry mentry = (Map.Entry) iterator.next();
								BigDecimal keyProgramma = (BigDecimal) mentry.getKey();
								String dataProgramma = (String) mentry.getValue();
								String dataTirocinioProgramma = pattiTirocinioProgramma.get(keyProgramma);
								String cfPromotoreProgramma = pattiCFEntePromotoreProgramma.get(keyProgramma);
								String cfOspitanteProgramma = pattiOspitanteProgramma.get(keyProgramma);
								boolean isOKPromotore = false;
								if (cfPromotoreProgramma != null && !cfPromotoreProgramma.equals("") && azUtiCf != null
										&& cfPromotoreProgramma.equalsIgnoreCase(azUtiCf)) {
									isOKPromotore = true;
								}
								if (!isOKPromotore) {
									String codSedeEntePromotore = pattiSedeEntePromotoreProgramma.get(keyProgramma);
									if (cfPromotoreProgramma != null && codSedeEntePromotore != null) {
										SourceBean rowEnte = infoEnte(qExec, dc, cfPromotoreProgramma,
												codSedeEntePromotore);
										if (rowEnte != null) {
											String strPartitaIva = (String) rowEnte.getAttribute("row.strpartitaiva");
											if (strPartitaIva != null && !strPartitaIva.equals("") && azUtiCf != null
													&& strPartitaIva.equalsIgnoreCase(azUtiCf)) {
												isOKPromotore = true;
											}
										}
									}
								}

								if (datInizioMov != null && !datInizioMov.equals("") && dataProgramma != null
										&& !dataProgramma.equals("")
										&& DateUtils.compare(datInizioMov, dataProgramma) >= 0
										&& dataTirocinioProgramma != null && !dataTirocinioProgramma.equals("")
										&& DateUtils.compare(datInizioMov, dataTirocinioProgramma) >= 0 && isOKPromotore
										&& cfOspitanteProgramma != null && strCodiceFiscaleDatore != null
										&& cfOspitanteProgramma.equalsIgnoreCase(strCodiceFiscaleDatore)) {
									if (!programmiCOTirocinio.containsKey(keyProgramma)) {
										listaMovimenti.put(prgMovimento, "1");
										programmiCOTirocinio.put(keyProgramma, "1");
										inviaCOFlusso = true;
									}
								}
							}
						}
					} else {
						// gestione patto
						if (pattiGGDote.size() > 0) {
							Set set = pattiGGDote.entrySet();
							Iterator iterator = set.iterator();
							while (iterator.hasNext() && !listaMovimenti.containsKey(prgMovimento)) {
								Map.Entry mentry = (Map.Entry) iterator.next();
								BigDecimal keyPatto = (BigDecimal) mentry.getKey();
								String dataPatto = (String) mentry.getValue();
								String datAccompagnamento = pattiAccompagnamento.get(keyPatto);

								if (datInizioMov != null && !datInizioMov.equals("") && dataPatto != null
										&& !dataPatto.equals("") && DateUtils.compare(datInizioMov, dataPatto) >= 0
										&& datAccompagnamento != null && !datAccompagnamento.equals("")
										&& DateUtils.compare(datInizioMov, datAccompagnamento) >= 0) {
									if (!listaMovimenti.containsKey(prgMovimento)) {
										if (!pattiCOAccompagnamento.containsKey(keyPatto)) {
											listaMovimenti.put(prgMovimento, "1");
											pattiCOAccompagnamento.put(keyPatto, "1");
											inviaCOFlusso = true;
											aziendaAssunzioneAccompagnamento.put(keyPatto, strCodiceFiscaleDatore);
										}
									}
								}
							}
						}
						// gestione programmi
						if (pattiGGDoteProgramma.size() > 0) {
							Set set = pattiGGDoteProgramma.entrySet();
							Iterator iterator = set.iterator();
							while (iterator.hasNext() && !listaMovimenti.containsKey(prgMovimento)) {
								Map.Entry mentry = (Map.Entry) iterator.next();
								BigDecimal keyProgramma = (BigDecimal) mentry.getKey();
								String dataProgramma = (String) mentry.getValue();
								String datAccompagnamento = pattiAccompagnamentoProgramma.get(keyProgramma);

								if (datInizioMov != null && !datInizioMov.equals("") && dataProgramma != null
										&& !dataProgramma.equals("")
										&& DateUtils.compare(datInizioMov, dataProgramma) >= 0
										&& datAccompagnamento != null && !datAccompagnamento.equals("")
										&& DateUtils.compare(datInizioMov, datAccompagnamento) >= 0) {
									if (!listaMovimenti.containsKey(prgMovimento)) {
										if (!programmiCOAccompagnamento.containsKey(keyProgramma)) {
											listaMovimenti.put(prgMovimento, "1");
											programmiCOAccompagnamento.put(keyProgramma, "1");
											inviaCOFlusso = true;
											aziendaAssunzioneAccompagnamentoProgramma.put(keyProgramma,
													strCodiceFiscaleDatore);
										}
									}
								}
							}
						}
					}
				}

				if (inviaCOFlusso) {
					if ("1".equals(isMissione)) {
						indirizzoLavoro = azUtiInd;
						comuneLavoro = azUtiCom;
					} else {
						indirizzoLavoro = strIndirizzo;
						comuneLavoro = azCom;
					}

					if ("T".equals(codOrario)) {
						codOrario = "FT";
					} else if ("P".equals(codOrario)) {
						codOrario = "PT";
					} else if ("N".equals(codOrario)) {
						if (numoreSett == null) {
							codOrario = "FT";
						} else {
							codOrario = "PT";
						}
					}

					ComunicazioneObbligatoria comunicazioniObbligatoria = factory
							.createFormazioneComunicazioniObbligatorieComunicazioneObbligatoria();

					comunicazioniObbligatoria.setPrgMovimentoSil(prgMovimento.toBigInteger());
					if (prgMovimentoRett != null) {
						comunicazioniObbligatoria.setPrgMovimentoSilRett(prgMovimentoRett.toBigInteger());
					}
					if (codComunicazione != null) {
						comunicazioniObbligatoria.setCodiceComunicazioneAvviamento(codComunicazione.trim());
					}
					if (codTipoMov != null) {
						comunicazioniObbligatoria.setTipoMovimento(codTipoMov);
					}
					comunicazioniObbligatoria.setStatoMovimento("PR");
					if (strCodiceFiscaleDatore != null) {
						comunicazioniObbligatoria.setDatoreLavoroCodiceFiscale(strCodiceFiscaleDatore.trim());
					}
					if (strRagioneSociale != null) {
						comunicazioniObbligatoria.setDatoreLavoroDenominazione(strCodiceFiscaleDatore.trim());
					}
					if (strIndirizzo != null) {
						comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strIndirizzo.trim());
					}
					if (codAzAteco != null) {
						comunicazioniObbligatoria.setDatoreLavoroSettore(codAzAteco.trim());
					}
					if (azUtiCf != null) {
						comunicazioniObbligatoria.setUtilizzatoreCodiceFiscale(azUtiCf.trim());
					}
					if (azUtiDenom != null) {
						comunicazioniObbligatoria.setUtilizzatoreDenominazione(azUtiDenom.trim());
					}
					if (azUtiInd != null) {
						comunicazioniObbligatoria.setUtilizzatoreIndirizzo(azUtiInd.trim());
					}
					if (codAzutiAteco != null) {
						comunicazioniObbligatoria.setUtilizzatoreSettore(codAzutiAteco.trim());
					}
					comunicazioniObbligatoria.setDataInizio(datInizioMovGregorian);
					if (datFineMovGregorian != null) {
						comunicazioniObbligatoria.setDataFine(datFineMovGregorian);
					}
					if (datFinePfGregorian != null) {
						comunicazioniObbligatoria.setDataFinePeriodoFormativo(datFinePfGregorian);
					}
					if (codMansioneMin != null) {
						comunicazioniObbligatoria.setQualificaProfessionale(codMansioneMin.trim());
					}
					if (mansioneDesc != null) {
						comunicazioniObbligatoria.setMansioni(mansioneDesc.trim());
					}
					if (codContratto != null) {
						comunicazioniObbligatoria.setTipoContratto(codContratto.trim());
					}
					if (flgLavoroStagional != null) {
						comunicazioniObbligatoria.setFlagStagionale(flgLavoroStagional.trim());
					}
					if (flgLavoroAgr != null) {
						comunicazioniObbligatoria.setFlagAgricoltura(flgLavoroAgr.trim());
					}
					if (codOrario != null) {
						comunicazioniObbligatoria.setModalitaLavoro(codOrario.trim());
					}
					if (indirizzoLavoro != null) {
						comunicazioniObbligatoria.setSedeLavoroIndirizzo(indirizzoLavoro.trim());
					}
					if (comuneLavoro != null) {
						comunicazioniObbligatoria.setSedeLavoroCodiceCatastale(comuneLavoro.trim());
					}
					if (codTipoEntePromotore != null) {
						comunicazioniObbligatoria.setTipologiaSoggettoPromotore(codTipoEntePromotore.trim());
					}
					if (codCategoriaTir != null) {
						comunicazioniObbligatoria.setTirocinioCategoria(codCategoriaTir.trim());
					}
					if (codTipologiaTir != null) {
						comunicazioniObbligatoria.setTirocinioTipologia(codTipologiaTir.trim());
					}
					if (strCodiceFiscaleTutore != null) {
						comunicazioniObbligatoria.setTutoreCodiceFiscale(strCodiceFiscaleTutore.trim());
					}
					if (strCognomeTutore != null) {
						comunicazioniObbligatoria.setTutoreCognome(strCognomeTutore.trim());
					}
					if (strNomeTutore != null) {
						comunicazioniObbligatoria.setTutoreNome(strNomeTutore.trim());
					}
					if (codQualificaSrq != null) {
						comunicazioniObbligatoria.setQualificaSrq(codQualificaSrq.trim());
					}
					if (datConvenzione != null) {
						comunicazioniObbligatoria.setConvenzioneData(datConvenzione);
					}
					if (numConvenzione != null) {
						comunicazioniObbligatoria.setConvenzioneNumero(numConvenzione.trim());
					}
					if (dataInvioCo != null) {
						comunicazioniObbligatoria.setDataInvioCo(dataInvioCo);
					}
					comunicazioniObbligatorie.getComunicazioneObbligatoria().add(comunicazioniObbligatoria);
				}
			}
			// gestione patto
			boolean inviaCOPrec = false;
			if (aziendaAssunzioneAccompagnamento.size() > 0) {
				for (int j = (movimentazioneBeanVector.size() - 1); j >= 0; j--) {
					XMLGregorianCalendar datInizioMovGregorian = null;
					XMLGregorianCalendar datFinePfGregorian = null;
					XMLGregorianCalendar datFineMovGregorian = null;
					SourceBean movimentazioneBeanRow = (SourceBean) movimentazioneBeanVector.elementAt(j);
					prgMovimento = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTO");
					azUtiCf = (String) movimentazioneBeanRow.getAttribute("AZUTICF");
					strCodiceFiscaleDatore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALE");
					datInizioMov = (String) movimentazioneBeanRow.getAttribute("DATINIZIOMOV");
					inviaCOPrec = false;
					if (!listaMovimenti.containsKey(prgMovimento)) {
						Set set = aziendaAssunzioneAccompagnamento.entrySet();
						Iterator iterator = set.iterator();
						while (iterator.hasNext() && !listaMovimenti.containsKey(prgMovimento)) {
							Map.Entry mentry = (Map.Entry) iterator.next();
							BigDecimal prgPattoLav = (BigDecimal) mentry.getKey();
							String cfAziendaAssPostAcc = (String) mentry.getValue();
							String dataPatto = pattiGGDote.get(prgPattoLav);
							if (datInizioMov != null && !datInizioMov.equals("") && dataPatto != null
									&& !dataPatto.equals("") && DateUtils.compare(datInizioMov, dataPatto) < 0
									&& ((strCodiceFiscaleDatore != null
											&& cfAziendaAssPostAcc.equalsIgnoreCase(strCodiceFiscaleDatore))
											|| (azUtiCf != null && cfAziendaAssPostAcc.equalsIgnoreCase(azUtiCf)))) {
								if (!pattiCOPrecAccompagnamento.containsKey(prgPattoLav)) {
									listaMovimenti.put(prgMovimento, "1");
									pattiCOPrecAccompagnamento.put(prgPattoLav, "1");
									inviaCOPrec = true;
								}
							}
						}
					}

					if (inviaCOPrec) {
						strRagioneSociale = (String) movimentazioneBeanRow.getAttribute("STRRAGIONESOCIALE");
						codComunicazione = (String) movimentazioneBeanRow.getAttribute("CODCOMUNICAZIONE");
						strIndirizzo = (String) movimentazioneBeanRow.getAttribute("STRINDIRIZZO");
						codAzAteco = (String) movimentazioneBeanRow.getAttribute("CODAZATECO");
						azUtiDenom = (String) movimentazioneBeanRow.getAttribute("AZUTIDENOM");
						azUtiInd = (String) movimentazioneBeanRow.getAttribute("AZUTIIND");
						codAzutiAteco = (String) movimentazioneBeanRow.getAttribute("CODAZUTIATECO");
						if (datInizioMov != null) {
							datInizioMovGregorian = toXMLGregorianCalendarDateOnly(datInizioMov);
						}
						datFineMov = (String) movimentazioneBeanRow.getAttribute("DATFINEMOV");
						if (datFineMov != null) {
							datFineMovGregorian = toXMLGregorianCalendarDateOnly(datFineMov);
						}
						datFinePf = (String) movimentazioneBeanRow.getAttribute("DATFINEPF");
						if (datFinePf != null) {
							datFinePfGregorian = toXMLGregorianCalendarDateOnly(datFinePf);
						}
						codMansioneMin = (String) movimentazioneBeanRow.getAttribute("CODMANSIONE_MIN");
						mansioneDesc = (String) movimentazioneBeanRow.getAttribute("MANSIONE_DESC");
						codContratto = (String) movimentazioneBeanRow.getAttribute("CODCONTRATTO");
						flgLavoroStagional = (String) movimentazioneBeanRow.getAttribute("FLGLAVOROSTAGIONALE");
						flgLavoroAgr = (String) movimentazioneBeanRow.getAttribute("FLGLAVOROAGR");
						codOrario = (String) movimentazioneBeanRow.getAttribute("CODORARIO");
						numoreSett = (BigDecimal) movimentazioneBeanRow.getAttribute("NUMORESETT");
						azUtiCom = (String) movimentazioneBeanRow.getAttribute("AZUTICOM");
						azCom = (String) movimentazioneBeanRow.getAttribute("AZCOM");
						isMissione = (String) movimentazioneBeanRow.getAttribute("ISMISSIONE");
						numConvenzione = (String) movimentazioneBeanRow.getAttribute("NUMCONVENZIONE");
						codTipoEntePromotore = (String) movimentazioneBeanRow.getAttribute("CODSOGGPROMOTOREMIN");
						datConvenzione = (String) movimentazioneBeanRow.getAttribute("DATCONVENZIONE");
						codCategoriaTir = (String) movimentazioneBeanRow.getAttribute("CODCATEGORIATIR");
						codTipologiaTir = (String) movimentazioneBeanRow.getAttribute("CODTIPOLOGIATIR");
						strCodiceFiscaleTutore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALETUTORE");
						strCognomeTutore = (String) movimentazioneBeanRow.getAttribute("STRCOGNOMETUTORE");
						strNomeTutore = (String) movimentazioneBeanRow.getAttribute("STRNOMETUTORE");
						codQualificaSrq = (String) movimentazioneBeanRow.getAttribute("CODQUALIFICASRQ");
						prgMovimentoRett = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTORETT");
						dataInvioCo = (String) movimentazioneBeanRow.getAttribute("DATCOMUNICAZ");
						codTipoMov = (String) movimentazioneBeanRow.getAttribute("CODTIPOMOV");
						if ("1".equals(isMissione)) {
							indirizzoLavoro = azUtiInd;
							comuneLavoro = azUtiCom;
						} else {
							indirizzoLavoro = strIndirizzo;
							comuneLavoro = azCom;
						}

						if ("T".equals(codOrario)) {
							codOrario = "FT";
						} else if ("P".equals(codOrario)) {
							codOrario = "PT";
						} else if ("N".equals(codOrario)) {
							if (numoreSett == null) {
								codOrario = "FT";
							} else {
								codOrario = "PT";
							}
						}

						ComunicazioneObbligatoria comunicazioniObbligatoria = factory
								.createFormazioneComunicazioniObbligatorieComunicazioneObbligatoria();

						comunicazioniObbligatoria.setPrgMovimentoSil(prgMovimento.toBigInteger());
						if (prgMovimentoRett != null) {
							comunicazioniObbligatoria.setPrgMovimentoSilRett(prgMovimentoRett.toBigInteger());
						}
						if (codComunicazione != null) {
							comunicazioniObbligatoria.setCodiceComunicazioneAvviamento(codComunicazione.trim());
						}
						if (codTipoMov != null) {
							comunicazioniObbligatoria.setTipoMovimento(codTipoMov);
						}
						comunicazioniObbligatoria.setStatoMovimento("PR");
						if (strCodiceFiscaleDatore != null) {
							comunicazioniObbligatoria.setDatoreLavoroCodiceFiscale(strCodiceFiscaleDatore.trim());
						}
						if (strRagioneSociale != null) {
							comunicazioniObbligatoria.setDatoreLavoroDenominazione(strCodiceFiscaleDatore.trim());
						}
						if (strIndirizzo != null) {
							comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strIndirizzo.trim());
						}
						if (codAzAteco != null) {
							comunicazioniObbligatoria.setDatoreLavoroSettore(codAzAteco.trim());
						}
						if (azUtiCf != null) {
							comunicazioniObbligatoria.setUtilizzatoreCodiceFiscale(azUtiCf.trim());
						}
						if (azUtiDenom != null) {
							comunicazioniObbligatoria.setUtilizzatoreDenominazione(azUtiDenom.trim());
						}
						if (azUtiInd != null) {
							comunicazioniObbligatoria.setUtilizzatoreIndirizzo(azUtiInd.trim());
						}
						if (codAzutiAteco != null) {
							comunicazioniObbligatoria.setUtilizzatoreSettore(codAzutiAteco.trim());
						}
						comunicazioniObbligatoria.setDataInizio(datInizioMovGregorian);
						if (datFineMovGregorian != null) {
							comunicazioniObbligatoria.setDataFine(datFineMovGregorian);
						}
						if (datFinePfGregorian != null) {
							comunicazioniObbligatoria.setDataFinePeriodoFormativo(datFinePfGregorian);
						}
						if (codMansioneMin != null) {
							comunicazioniObbligatoria.setQualificaProfessionale(codMansioneMin.trim());
						}
						if (mansioneDesc != null) {
							comunicazioniObbligatoria.setMansioni(mansioneDesc.trim());
						}
						if (codContratto != null) {
							comunicazioniObbligatoria.setTipoContratto(codContratto.trim());
						}
						if (flgLavoroStagional != null) {
							comunicazioniObbligatoria.setFlagStagionale(flgLavoroStagional.trim());
						}
						if (flgLavoroAgr != null) {
							comunicazioniObbligatoria.setFlagAgricoltura(flgLavoroAgr.trim());
						}
						if (codOrario != null) {
							comunicazioniObbligatoria.setModalitaLavoro(codOrario.trim());
						}
						if (indirizzoLavoro != null) {
							comunicazioniObbligatoria.setSedeLavoroIndirizzo(indirizzoLavoro.trim());
						}
						if (comuneLavoro != null) {
							comunicazioniObbligatoria.setSedeLavoroCodiceCatastale(comuneLavoro.trim());
						}
						if (codTipoEntePromotore != null) {
							comunicazioniObbligatoria.setTipologiaSoggettoPromotore(codTipoEntePromotore.trim());
						}
						if (codCategoriaTir != null) {
							comunicazioniObbligatoria.setTirocinioCategoria(codCategoriaTir.trim());
						}
						if (codTipologiaTir != null) {
							comunicazioniObbligatoria.setTirocinioTipologia(codTipologiaTir.trim());
						}
						if (strCodiceFiscaleTutore != null) {
							comunicazioniObbligatoria.setTutoreCodiceFiscale(strCodiceFiscaleTutore.trim());
						}
						if (strCognomeTutore != null) {
							comunicazioniObbligatoria.setTutoreCognome(strCognomeTutore.trim());
						}
						if (strNomeTutore != null) {
							comunicazioniObbligatoria.setTutoreNome(strNomeTutore.trim());
						}
						if (codQualificaSrq != null) {
							comunicazioniObbligatoria.setQualificaSrq(codQualificaSrq.trim());
						}
						if (datConvenzione != null) {
							comunicazioniObbligatoria.setConvenzioneData(datConvenzione);
						}
						if (numConvenzione != null) {
							comunicazioniObbligatoria.setConvenzioneNumero(numConvenzione.trim());
						}
						if (dataInvioCo != null) {
							comunicazioniObbligatoria.setDataInvioCo(dataInvioCo);
						}
						comunicazioniObbligatorie.getComunicazioneObbligatoria().add(comunicazioniObbligatoria);
					}
				}
			}
			// gestione programmi
			if (aziendaAssunzioneAccompagnamentoProgramma.size() > 0) {
				for (int j = (movimentazioneBeanVector.size() - 1); j >= 0; j--) {
					inviaCOPrec = false;
					XMLGregorianCalendar datInizioMovGregorian = null;
					XMLGregorianCalendar datFinePfGregorian = null;
					XMLGregorianCalendar datFineMovGregorian = null;
					SourceBean movimentazioneBeanRow = (SourceBean) movimentazioneBeanVector.elementAt(j);
					prgMovimento = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTO");
					azUtiCf = (String) movimentazioneBeanRow.getAttribute("AZUTICF");
					strCodiceFiscaleDatore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALE");
					datInizioMov = (String) movimentazioneBeanRow.getAttribute("DATINIZIOMOV");
					if (!listaMovimenti.containsKey(prgMovimento)) {
						Set set = aziendaAssunzioneAccompagnamentoProgramma.entrySet();
						Iterator iterator = set.iterator();
						while (iterator.hasNext() && !listaMovimenti.containsKey(prgMovimento)) {
							Map.Entry mentry = (Map.Entry) iterator.next();
							BigDecimal prgProgramma = (BigDecimal) mentry.getKey();
							String cfAziendaAssPostAccProgramma = (String) mentry.getValue();
							String dataProgramma = pattiGGDoteProgramma.get(prgProgramma);
							if (datInizioMov != null && !datInizioMov.equals("") && dataProgramma != null
									&& !dataProgramma.equals("") && DateUtils.compare(datInizioMov, dataProgramma) < 0
									&& ((strCodiceFiscaleDatore != null
											&& cfAziendaAssPostAccProgramma.equalsIgnoreCase(strCodiceFiscaleDatore))
											|| (azUtiCf != null
													&& cfAziendaAssPostAccProgramma.equalsIgnoreCase(azUtiCf)))) {
								if (!programmiCOPrecAccompagnamento.containsKey(prgProgramma)) {
									listaMovimenti.put(prgMovimento, "1");
									programmiCOPrecAccompagnamento.put(prgProgramma, "1");
									inviaCOPrec = true;
								}
							}
						}
					}

					if (inviaCOPrec) {
						strRagioneSociale = (String) movimentazioneBeanRow.getAttribute("STRRAGIONESOCIALE");
						codComunicazione = (String) movimentazioneBeanRow.getAttribute("CODCOMUNICAZIONE");
						strIndirizzo = (String) movimentazioneBeanRow.getAttribute("STRINDIRIZZO");
						codAzAteco = (String) movimentazioneBeanRow.getAttribute("CODAZATECO");
						azUtiDenom = (String) movimentazioneBeanRow.getAttribute("AZUTIDENOM");
						azUtiInd = (String) movimentazioneBeanRow.getAttribute("AZUTIIND");
						codAzutiAteco = (String) movimentazioneBeanRow.getAttribute("CODAZUTIATECO");
						if (datInizioMov != null) {
							datInizioMovGregorian = toXMLGregorianCalendarDateOnly(datInizioMov);
						}
						datFineMov = (String) movimentazioneBeanRow.getAttribute("DATFINEMOV");
						if (datFineMov != null) {
							datFineMovGregorian = toXMLGregorianCalendarDateOnly(datFineMov);
						}
						datFinePf = (String) movimentazioneBeanRow.getAttribute("DATFINEPF");
						if (datFinePf != null) {
							datFinePfGregorian = toXMLGregorianCalendarDateOnly(datFinePf);
						}
						codMansioneMin = (String) movimentazioneBeanRow.getAttribute("CODMANSIONE_MIN");
						mansioneDesc = (String) movimentazioneBeanRow.getAttribute("MANSIONE_DESC");
						codContratto = (String) movimentazioneBeanRow.getAttribute("CODCONTRATTO");
						flgLavoroStagional = (String) movimentazioneBeanRow.getAttribute("FLGLAVOROSTAGIONALE");
						flgLavoroAgr = (String) movimentazioneBeanRow.getAttribute("FLGLAVOROAGR");
						codOrario = (String) movimentazioneBeanRow.getAttribute("CODORARIO");
						numoreSett = (BigDecimal) movimentazioneBeanRow.getAttribute("NUMORESETT");
						azUtiCom = (String) movimentazioneBeanRow.getAttribute("AZUTICOM");
						azCom = (String) movimentazioneBeanRow.getAttribute("AZCOM");
						isMissione = (String) movimentazioneBeanRow.getAttribute("ISMISSIONE");
						numConvenzione = (String) movimentazioneBeanRow.getAttribute("NUMCONVENZIONE");
						codTipoEntePromotore = (String) movimentazioneBeanRow.getAttribute("CODSOGGPROMOTOREMIN");
						datConvenzione = (String) movimentazioneBeanRow.getAttribute("DATCONVENZIONE");
						codCategoriaTir = (String) movimentazioneBeanRow.getAttribute("CODCATEGORIATIR");
						codTipologiaTir = (String) movimentazioneBeanRow.getAttribute("CODTIPOLOGIATIR");
						strCodiceFiscaleTutore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALETUTORE");
						strCognomeTutore = (String) movimentazioneBeanRow.getAttribute("STRCOGNOMETUTORE");
						strNomeTutore = (String) movimentazioneBeanRow.getAttribute("STRNOMETUTORE");
						codQualificaSrq = (String) movimentazioneBeanRow.getAttribute("CODQUALIFICASRQ");
						prgMovimentoRett = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTORETT");
						dataInvioCo = (String) movimentazioneBeanRow.getAttribute("DATCOMUNICAZ");
						codTipoMov = (String) movimentazioneBeanRow.getAttribute("CODTIPOMOV");
						if ("1".equals(isMissione)) {
							indirizzoLavoro = azUtiInd;
							comuneLavoro = azUtiCom;
						} else {
							indirizzoLavoro = strIndirizzo;
							comuneLavoro = azCom;
						}

						if ("T".equals(codOrario)) {
							codOrario = "FT";
						} else if ("P".equals(codOrario)) {
							codOrario = "PT";
						} else if ("N".equals(codOrario)) {
							if (numoreSett == null) {
								codOrario = "FT";
							} else {
								codOrario = "PT";
							}
						}

						ComunicazioneObbligatoria comunicazioniObbligatoria = factory
								.createFormazioneComunicazioniObbligatorieComunicazioneObbligatoria();

						comunicazioniObbligatoria.setPrgMovimentoSil(prgMovimento.toBigInteger());
						if (prgMovimentoRett != null) {
							comunicazioniObbligatoria.setPrgMovimentoSilRett(prgMovimentoRett.toBigInteger());
						}
						if (codComunicazione != null) {
							comunicazioniObbligatoria.setCodiceComunicazioneAvviamento(codComunicazione.trim());
						}
						if (codTipoMov != null) {
							comunicazioniObbligatoria.setTipoMovimento(codTipoMov);
						}
						comunicazioniObbligatoria.setStatoMovimento("PR");
						if (strCodiceFiscaleDatore != null) {
							comunicazioniObbligatoria.setDatoreLavoroCodiceFiscale(strCodiceFiscaleDatore.trim());
						}
						if (strRagioneSociale != null) {
							comunicazioniObbligatoria.setDatoreLavoroDenominazione(strCodiceFiscaleDatore.trim());
						}
						if (strIndirizzo != null) {
							comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strIndirizzo.trim());
						}
						if (codAzAteco != null) {
							comunicazioniObbligatoria.setDatoreLavoroSettore(codAzAteco.trim());
						}
						if (azUtiCf != null) {
							comunicazioniObbligatoria.setUtilizzatoreCodiceFiscale(azUtiCf.trim());
						}
						if (azUtiDenom != null) {
							comunicazioniObbligatoria.setUtilizzatoreDenominazione(azUtiDenom.trim());
						}
						if (azUtiInd != null) {
							comunicazioniObbligatoria.setUtilizzatoreIndirizzo(azUtiInd.trim());
						}
						if (codAzutiAteco != null) {
							comunicazioniObbligatoria.setUtilizzatoreSettore(codAzutiAteco.trim());
						}
						comunicazioniObbligatoria.setDataInizio(datInizioMovGregorian);
						if (datFineMovGregorian != null) {
							comunicazioniObbligatoria.setDataFine(datFineMovGregorian);
						}
						if (datFinePfGregorian != null) {
							comunicazioniObbligatoria.setDataFinePeriodoFormativo(datFinePfGregorian);
						}
						if (codMansioneMin != null) {
							comunicazioniObbligatoria.setQualificaProfessionale(codMansioneMin.trim());
						}
						if (mansioneDesc != null) {
							comunicazioniObbligatoria.setMansioni(mansioneDesc.trim());
						}
						if (codContratto != null) {
							comunicazioniObbligatoria.setTipoContratto(codContratto.trim());
						}
						if (flgLavoroStagional != null) {
							comunicazioniObbligatoria.setFlagStagionale(flgLavoroStagional.trim());
						}
						if (flgLavoroAgr != null) {
							comunicazioniObbligatoria.setFlagAgricoltura(flgLavoroAgr.trim());
						}
						if (codOrario != null) {
							comunicazioniObbligatoria.setModalitaLavoro(codOrario.trim());
						}
						if (indirizzoLavoro != null) {
							comunicazioniObbligatoria.setSedeLavoroIndirizzo(indirizzoLavoro.trim());
						}
						if (comuneLavoro != null) {
							comunicazioniObbligatoria.setSedeLavoroCodiceCatastale(comuneLavoro.trim());
						}
						if (codTipoEntePromotore != null) {
							comunicazioniObbligatoria.setTipologiaSoggettoPromotore(codTipoEntePromotore.trim());
						}
						if (codCategoriaTir != null) {
							comunicazioniObbligatoria.setTirocinioCategoria(codCategoriaTir.trim());
						}
						if (codTipologiaTir != null) {
							comunicazioniObbligatoria.setTirocinioTipologia(codTipologiaTir.trim());
						}
						if (strCodiceFiscaleTutore != null) {
							comunicazioniObbligatoria.setTutoreCodiceFiscale(strCodiceFiscaleTutore.trim());
						}
						if (strCognomeTutore != null) {
							comunicazioniObbligatoria.setTutoreCognome(strCognomeTutore.trim());
						}
						if (strNomeTutore != null) {
							comunicazioniObbligatoria.setTutoreNome(strNomeTutore.trim());
						}
						if (codQualificaSrq != null) {
							comunicazioniObbligatoria.setQualificaSrq(codQualificaSrq.trim());
						}
						if (datConvenzione != null) {
							comunicazioniObbligatoria.setConvenzioneData(datConvenzione);
						}
						if (numConvenzione != null) {
							comunicazioniObbligatoria.setConvenzioneNumero(numConvenzione.trim());
						}
						if (dataInvioCo != null) {
							comunicazioniObbligatoria.setDataInvioCo(dataInvioCo);
						}
						comunicazioniObbligatorie.getComunicazioneObbligatoria().add(comunicazioniObbligatoria);
					}
				}
			}
		}

		return new ErroreSeta(0);
	}

	@SuppressWarnings("rawtypes")
	private boolean confrontaPartecipante(Formazione msg, Flusso flussoLav, Flusso flussoCorrente,
			QueryExecutorObject qExec, DataConnection dc) throws Exception {
		boolean checkInvio = false;
		LavoratoreTracciato flussoPartecipante = new LavoratoreTracciato(
				(BigDecimal) flussoLav.getAttribute("PRGFLUSSO"), qExec, dc);
		Vector listaPartecipanti = flussoPartecipante.getAttributeAsVector("ROW");
		for (int iP = 0; iP < listaPartecipanti.size(); iP++) {
			SourceBean rowCurr = (SourceBean) listaPartecipanti.get(iP);
			LavoratoreTracciato partecipante = new LavoratoreTracciato(rowCurr);
			flussoLav.setInfoLavoratore(partecipante);
		}
		if (flussoLav.getInfoLavoratore() != null) {
			Partecipante p = msg.getPartecipante();
			LavoratoreTracciato ptCurr = new LavoratoreTracciato(p);
			String cfP = p.getCodiceFiscale();
			boolean checkPartecipanteTracciato = false;
			for (int j = 0; j < flussoLav.getInfoLavoratore().size(); j++) {
				LavoratoreTracciato ptInviato = flussoLav.getInfoLavoratore().get(j);
				String cfInviato = (String) ptInviato.getAttribute("STRCODICEFISCALELAV");
				if (cfP.equalsIgnoreCase(cfInviato)) {
					int confrontoID = ptInviato.compareTo(ptCurr);
					if (confrontoID == 0) {
						checkPartecipanteTracciato = true;
						j = flussoLav.getInfoLavoratore().size();
					} else {
						j = flussoLav.getInfoLavoratore().size();
					}
				}
			}
			if (!checkPartecipanteTracciato) {
				checkInvio = true;
			}
		} else {
			checkInvio = true;
		}
		return checkInvio;
	}

	@SuppressWarnings("rawtypes")
	private boolean confrontaProfiling(Formazione msg, Flusso flussoLav, Flusso flussoCorrente,
			QueryExecutorObject qExec, DataConnection dc) throws Exception {
		boolean checkInvio = false;
		PattoTracciato flussoPattiLav = new PattoTracciato((BigDecimal) flussoLav.getAttribute("PRGFLUSSO"), qExec, dc);
		AzioneTracciato azPattiLav = new AzioneTracciato((BigDecimal) flussoLav.getAttribute("PRGFLUSSO"), qExec, dc);
		Vector listaPattiFlusso = flussoPattiLav.getAttributeAsVector("ROW");
		Vector listaAzFlusso = azPattiLav.getAttributeAsVector("ROW");
		for (int iP = 0; iP < listaPattiFlusso.size(); iP++) {
			SourceBean rowCurr = (SourceBean) listaPattiFlusso.get(iP);
			PattoTracciato pt = new PattoTracciato(rowCurr);
			flussoLav.setInfoPatto(pt);
		}
		for (int iP = 0; iP < listaAzFlusso.size(); iP++) {
			SourceBean rowCurr = (SourceBean) listaAzFlusso.get(iP);
			AzioneTracciato az = new AzioneTracciato(rowCurr);
			az.setqExec(qExec);
			az.setDc(dc);
			flussoLav.setInfoAzione(az);
		}

		if (flussoLav.getInfoPatto() != null) {
			List<ProfilingPatto> listaProfiling = msg.getPatti().getProfilingPatto();
			for (int iP = 0; iP < listaProfiling.size(); iP++) {
				List<PoliticaAttiva> politiche = null;
				ProfilingPatto profiling = listaProfiling.get(iP);
				if (profiling.getPoliticheAttive() != null
						&& profiling.getPoliticheAttive().getPoliticaAttiva() != null) {
					politiche = profiling.getPoliticheAttive().getPoliticaAttiva();
				}
				PattoTracciato pattoProfiling = new PattoTracciato(profiling);
				// BigDecimal prgPattoCurr = (BigDecimal)pattoProfiling.getAttribute("PRGPATTOLAVORATORE");
				BigDecimal prgPattoCurr = pattoProfiling.getAttribute("PRGPATTOLAVORATORE") != null
						? Flusso.getBigDecimal(pattoProfiling.getAttribute("PRGPATTOLAVORATORE"))
						: new BigDecimal("0");
				boolean checkPattoTracciato = false;
				for (int j = 0; j < flussoLav.getInfoPatto().size(); j++) {
					PattoTracciato ptInviato = flussoLav.getInfoPatto().get(j);
					// BigDecimal prgPattoInviato = (BigDecimal)ptInviato.getAttribute("PRGPATTOLAVORATORE");
					BigDecimal prgPattoInviato = ptInviato.getAttribute("PRGPATTOLAVORATORE") != null
							? Flusso.getBigDecimal(ptInviato.getAttribute("PRGPATTOLAVORATORE"))
							: new BigDecimal("0");
					if (prgPattoCurr.equals(prgPattoInviato)) {
						int confrontoID = ptInviato.compareTo(pattoProfiling);
						if (confrontoID == 0) {
							// devo confrontare le politiche da inviare
							boolean checkAzTracciato = false;
							if (flussoLav.getInfoAzione() != null && politiche != null && politiche.size() > 0) {
								for (int k = 0; k < flussoLav.getInfoAzione().size(); k++) {
									AzioneTracciato azInviata = (AzioneTracciato) flussoLav.getInfoAzione().get(k);
									BigDecimal prgPercorsoInviato = azInviata.getAttribute("PRGPERCORSO") != null
											? Flusso.getBigDecimal(azInviata.getAttribute("PRGPERCORSO"))
											: new BigDecimal("0");
									BigDecimal prgColloquioInviato = azInviata.getAttribute("PRGCOLLOQUIO") != null
											? Flusso.getBigDecimal(azInviata.getAttribute("PRGCOLLOQUIO"))
											: new BigDecimal("0");
									// BigDecimal prgPercorsoInviato =
									// (BigDecimal)azInviata.getAttribute("PRGPERCORSO");
									// BigDecimal prgColloquioInviato =
									// (BigDecimal)azInviata.getAttribute("PRGCOLLOQUIO");
									checkAzTracciato = false;
									for (int p = 0; p < politiche.size(); p++) {
										PoliticaAttiva polCurr = politiche.get(p);
										AzioneTracciato azCurr = new AzioneTracciato(polCurr);
										azCurr.setqExec(qExec);
										azCurr.setDc(dc);
										BigDecimal prgPercorsoCurr = azCurr.getAttribute("PRGPERCORSO") != null
												? Flusso.getBigDecimal(azCurr.getAttribute("PRGPERCORSO"))
												: new BigDecimal("0");
										BigDecimal prgColloquioCurr = azCurr.getAttribute("PRGCOLLOQUIO") != null
												? Flusso.getBigDecimal(azCurr.getAttribute("PRGCOLLOQUIO"))
												: new BigDecimal("0");
										// BigDecimal prgPercorsoCurr = (BigDecimal)azCurr.getAttribute("PRGPERCORSO");
										// BigDecimal prgColloquioCurr =
										// (BigDecimal)azCurr.getAttribute("PRGCOLLOQUIO");
										if (prgPercorsoCurr.equals(prgPercorsoInviato)
												&& prgColloquioCurr.equals(prgColloquioInviato)) {
											int confrontoAz = azInviata.compareTo(azCurr);
											if (confrontoAz == 0) {
												checkAzTracciato = true;
												p = politiche.size();
											} else {
												p = politiche.size();
											}
										}
									}
									if (!checkAzTracciato) {
										checkInvio = true;
										return checkInvio;
									}
								}
							}
							checkPattoTracciato = true;
							j = flussoLav.getInfoPatto().size();
						}

						else {
							j = flussoLav.getInfoPatto().size();
						}
					}
				}
				if (!checkPattoTracciato) {
					checkInvio = true;
					iP = listaProfiling.size();
				}
			}
		} else {
			checkInvio = true;
		}
		return checkInvio;
	}

	@SuppressWarnings("rawtypes")
	private boolean confrontaMovimenti(Formazione msg, Flusso flussoLav, Flusso flussoCorrente,
			QueryExecutorObject qExec, DataConnection dc) throws Exception {
		boolean checkInvio = false;
		MovimentoTracciato flussoMovimentiLav = new MovimentoTracciato((BigDecimal) flussoLav.getAttribute("PRGFLUSSO"),
				qExec, dc);
		Vector listaMovimentiFlusso = flussoMovimentiLav.getAttributeAsVector("ROW");
		for (int iP = 0; iP < listaMovimentiFlusso.size(); iP++) {
			SourceBean rowCurr = (SourceBean) listaMovimentiFlusso.get(iP);
			MovimentoTracciato mov = new MovimentoTracciato(rowCurr);
			flussoLav.setInfoMovimento(mov);
		}
		if (flussoLav.getInfoMovimento() != null) {
			List<ComunicazioneObbligatoria> listaMovimenti = msg.getComunicazioniObbligatorie()
					.getComunicazioneObbligatoria();
			for (int iP = 0; iP < listaMovimenti.size(); iP++) {
				ComunicazioneObbligatoria comunicazioneObb = listaMovimenti.get(iP);
				MovimentoTracciato movimentoCurr = new MovimentoTracciato(comunicazioneObb);
				BigDecimal prgMovCurr = movimentoCurr.getAttribute("PRGMOVIMENTO") != null
						? Flusso.getBigDecimal(movimentoCurr.getAttribute("PRGMOVIMENTO"))
						: new BigDecimal("0");
				// BigDecimal prgMovCurr = (BigDecimal)movimentoCurr.getAttribute("PRGMOVIMENTO");
				boolean checkMovimentoTracciato = false;
				for (int j = 0; j < flussoLav.getInfoMovimento().size(); j++) {
					MovimentoTracciato movInviato = flussoLav.getInfoMovimento().get(j);
					BigDecimal prgMovInviato = movInviato.getAttribute("PRGMOVIMENTO") != null
							? Flusso.getBigDecimal(movInviato.getAttribute("PRGMOVIMENTO"))
							: new BigDecimal("0");
					// BigDecimal prgMovInviato = (BigDecimal)movInviato.getAttribute("PRGMOVIMENTO");
					if (prgMovCurr.equals(prgMovInviato)) {
						int confrontoID = movInviato.compareTo(movimentoCurr);
						if (confrontoID == 0) {
							checkMovimentoTracciato = true;
							j = flussoLav.getInfoMovimento().size();
						} else {
							j = flussoLav.getInfoMovimento().size();
						}
					}
				}
				if (!checkMovimentoTracciato) {
					checkInvio = true;
					iP = listaMovimenti.size();
				}
			}
		} else {
			checkInvio = true;
		}
		return checkInvio;
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

	public static QueryExecutorObject getQueryExecutorObject(boolean isTransactional)
			throws NamingException, SQLException, EMFInternalError {
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
			qExec.setTransactional(isTransactional);
			qExec.setDontForgetException(false);
		} else {
			_logger.error("Impossibile ottenere una connessione");
			return null;
		}

		return qExec;
	}

	private static boolean checkEmail(String email) {
		if (email != null) {
			Matcher emailMatcher = XMLValidator.emailRegEx.matcher(email.trim());
			return emailMatcher.matches();
		} else {
			return false;
		}
	}

	public static void tracciaErrore(Connection conn, BigDecimal cdnLavoratore, ErroreSeta erroreSeta)
			throws Exception {
		PreparedStatement psUrl = null;
		psUrl = conn.prepareStatement(QUERY_STRING_TRACCIA_ERRORE);

		String err = "";
		if (erroreSeta.erroreEsteso != null && erroreSeta.erroreEsteso.length() > 500) {
			err = erroreSeta.erroreEsteso.substring(0, 500);
		} else {
			err = erroreSeta.erroreEsteso;
		}

		psUrl.setBigDecimal(1, cdnLavoratore);
		psUrl.setString(2, err);

		// execute insert SQL stetement
		psUrl.executeUpdate();
		psUrl.close();
	}

	private static ErroreSeta getProfilingProgrammaDataAdesione(QueryExecutorObject qExec, DataConnection dc,
			BigDecimal prgColloquioCurr, BigDecimal prgPattoLavoratore, ProfilingPatto profilingPatto,
			BigDecimal cdnLavoratore, String datStipula) throws Exception {
		String datAdesioneGgCurr = null;
		XMLGregorianCalendar datAdesioneGgGregorianCurr = null;
		try {
			Connection connection = dc.getInternalConnection();
			String codErrore = "";
			CallableStatement stmtCreaSap = connection.prepareCall("{? = call checkAdesioneProgrammaPatto(?, ?, ?) }");

			stmtCreaSap.registerOutParameter(1, OracleTypes.VARCHAR);
			stmtCreaSap.setBigDecimal(2, prgPattoLavoratore);
			stmtCreaSap.setBigDecimal(3, prgColloquioCurr);
			stmtCreaSap.registerOutParameter(4, OracleTypes.VARCHAR);
			stmtCreaSap.execute();

			codErrore = (String) stmtCreaSap.getString(1);
			datAdesioneGgCurr = (String) stmtCreaSap.getString(4);

			Vector<String> params = new Vector<String>();
			params.add(datStipula);
			if ("00".equals(codErrore)) {
				if (datAdesioneGgCurr != null) {
					datAdesioneGgGregorianCurr = toXMLGregorianCalendarDateOnly(datAdesioneGgCurr);
				} else {
					return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_NULLA, params);
				}
			} else if ("01".equals(codErrore)) {
				return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_DIVERSE, params);
			} else if ("02".equals(codErrore)) {
				return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_NULLA, params);
			} else {
				return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_DATA_ADESIONE_GENERICO, params);
			}
		} catch (Exception e) {
			_logger.error(
					"Impossibile invocare la funzione CHECKADESIONEPROGRAMMAPATTO: cdnlavoratore = " + cdnLavoratore);
			throw e;
		}

		profilingPatto.setAdesioneGgData(datAdesioneGgGregorianCurr);
		return new ErroreSeta(0);
	}

	private ErroreSeta getPoliticheAttiveProgramma(QueryExecutorObject qExec, DataConnection dc,
			BigDecimal prgPattoLavoratore, BigDecimal prgColloquioCurr, ProfilingPatto profilingPatto,
			Formazione.Patti.ProfilingPatto.PoliticheAttive politicheAttive, Patti patti, BigDecimal cdnLavoratore,
			String datStipula, String cfEntePromotore, String misuraPatto, String queryPolitiche) throws Exception {
		try {
			SourceBean politicheAttiveBeanRows = null;
			List<DataField> param = new ArrayList<DataField>();
			param = new ArrayList<DataField>();
			param.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
			param.add(dc.createDataField("", Types.BIGINT, prgColloquioCurr));
			qExec.setInputParameters(param);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(queryPolitiche);

			politicheAttiveBeanRows = (SourceBean) qExec.exec();

			ErroreSeta erroreSeta = getPoliticheAttive(politicheAttive, politicheAttiveBeanRows, prgPattoLavoratore,
					cdnLavoratore, misuraPatto, datStipula, cfEntePromotore, prgColloquioCurr);
			if (erroreSeta.errCod != 0) {
				return erroreSeta;
			}

			profilingPatto.setPoliticheAttive(politicheAttive);

			profilingPatto.setPoliticheAttive(politicheAttive);

			patti.getProfilingPatto().add(profilingPatto);

			return erroreSeta;
		} catch (Exception e) {
			_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
			return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_RECUPERO_POLITICHE);
		}
	}

	private ErroreSeta creaProfilingPatto(ProfilingPatto profilingPatto, BigDecimal prgColloquioCurr,
			BigDecimal cdnLavoratore, String codMonoProgramma, SourceBean pattiBeanRow, QueryExecutorObject qExec,
			DataConnection dc, String dataInizioProgr, String dataFineProgr, String cfEnte, String codSede)
			throws Exception {
		BigDecimal prgPattoLavoratore = null;
		BigDecimal numProfiling = null;
		BigDecimal numIndiceSvantaggio = null;
		BigDecimal numIndiceSvantaggio2 = null;
		BigDecimal numIndiceSvantaggio150 = null;
		String datRiferimento = null;
		String datRiferimento150 = null;
		String datStipula = null;
		String codCpi = null;
		String motivoChiusuraPatto = null;
		BigDecimal numProtocollo = null;
		String codStatoOccupaz = null;
		String dataScadenzaPatto = null;
		String dataStipulaDid = null;
		String dataChiusuraPatto = null;
		String pIvaEntePromotore = null;

		XMLGregorianCalendar datInizioProgrGregorian = null;
		XMLGregorianCalendar datFineProgrGregorian = null;
		XMLGregorianCalendar dataScadenzaPattoGregorian = null;
		XMLGregorianCalendar dataStipulaDidGregorian = null;
		XMLGregorianCalendar dataChiusuraPattoGregorian = null;

		prgPattoLavoratore = (BigDecimal) pattiBeanRow.getAttribute("PRGPATTOLAVORATORE");
		numProfiling = (BigDecimal) pattiBeanRow.getAttribute("NUMPROFILING");
		numIndiceSvantaggio = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO");
		numIndiceSvantaggio2 = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO2");
		numIndiceSvantaggio150 = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO150");
		datRiferimento = (String) pattiBeanRow.getAttribute("DATRIFERIMENTO");
		datRiferimento150 = (String) pattiBeanRow.getAttribute("DATRIFERIMENTO150");
		datStipula = (String) pattiBeanRow.getAttribute("DATSTIPULA");
		if (!dataInizioProgr.equals("")) {
			datInizioProgrGregorian = toXMLGregorianCalendarDateOnly(dataInizioProgr);
		}
		if (!dataFineProgr.equals("")) {
			datFineProgrGregorian = toXMLGregorianCalendarDateOnly(dataFineProgr);
		}
		codCpi = (String) pattiBeanRow.getAttribute("CODCPI");
		motivoChiusuraPatto = (String) pattiBeanRow.getAttribute("CODMOTIVOFINEATTO");
		dataScadenzaPatto = (String) pattiBeanRow.getAttribute("DATSCADCONFERMA");
		if (dataScadenzaPatto != null) {
			dataScadenzaPattoGregorian = toXMLGregorianCalendarDateOnly(dataScadenzaPatto);
		}
		numProtocollo = (BigDecimal) pattiBeanRow.getAttribute("NUMPROTOCOLLO");
		codStatoOccupaz = (String) pattiBeanRow.getAttribute("CODSTATOOCCUPAZ");
		dataChiusuraPatto = (String) pattiBeanRow.getAttribute("DATFINE");
		if (dataChiusuraPatto != null) {
			dataChiusuraPattoGregorian = toXMLGregorianCalendarDateOnly(dataChiusuraPatto);
		}
		dataStipulaDid = (String) pattiBeanRow.getAttribute("DATDICHIARAZIONE");
		if (dataStipulaDid != null) {
			dataStipulaDidGregorian = toXMLGregorianCalendarDateOnly(dataStipulaDid);
		}

		Vector<String> params = new Vector<String>();
		params.add(datStipula);

		profilingPatto.setPrgpatto(prgPattoLavoratore.toBigInteger());
		profilingPatto.setPrgprogramma(prgColloquioCurr.toBigInteger());
		if (numIndiceSvantaggio2 != null) {
			profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
		} else {
			return new ErroreSeta(MessageCodes.RENDICONTAZIONE.ERR_WS_INDICE_SVAN_NEW, params);
		}
		// IndiceSvantaggioVecchio non piu' gestito per la calabria
		// if (numIndiceSvantaggio != null) {
		// profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
		// }
		if (datRiferimento != null) {
			profilingPatto.setIndiceDataRiferimento(toXMLGregorianCalendarDateOnly(datRiferimento));
		}
		if (codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
			ErroreSeta erroreSetaAdesione = getProfilingProgrammaDataAdesione(qExec, dc, prgColloquioCurr,
					prgPattoLavoratore, profilingPatto, cdnLavoratore, datStipula);
			if (erroreSetaAdesione.errCod != 0) {
				/* riporto l'errore al chiamante */
				return erroreSetaAdesione;
			}
		}
		// if (numIndiceSvantaggio150 != null) {
		// profilingPatto.setProfiling150(numIndiceSvantaggio150.toBigInteger());
		// }
		// segnalazione redmine 7301
		if (numProfiling != null) {
			profilingPatto.setProfiling150(numProfiling.toBigInteger());
		}
		if (datRiferimento150 != null) {
			profilingPatto.setDataRiferimento150(toXMLGregorianCalendarDateOnly(datRiferimento150));
		}
		profilingPatto.setPattoData(datInizioProgrGregorian);
		if (codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
			profilingPatto.setPattoInclusioneAttiva("N");
			profilingPatto.setPattoDote("N");
			profilingPatto.setPattoGG("S");
		} else {
			if (codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_DOTE)) {
				profilingPatto.setPattoInclusioneAttiva("N");
				profilingPatto.setPattoDote("S");
				profilingPatto.setPattoGG("N");
			} else {
				if (codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_DOTE_IA)) {
					profilingPatto.setPattoInclusioneAttiva("S");
					profilingPatto.setPattoDote("N");
					profilingPatto.setPattoGG("N");
				}
			}
		}
		if (numProtocollo != null) {
			profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
		}
		if (codStatoOccupaz != null) {
			profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
		}
		if (codCpi != null) {
			profilingPatto.setPattoCpi(codCpi.trim());
		}
		if (datFineProgrGregorian != null) {
			profilingPatto.setDataChiusuraProgramma(datFineProgrGregorian);
		}
		if (dataChiusuraPattoGregorian != null) {
			profilingPatto.setDataChiusuraPatto(dataChiusuraPattoGregorian);
		}
		if (motivoChiusuraPatto != null) {
			profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
		}
		profilingPatto.setTipoMisuraPatto(codMonoProgramma);
		if (dataScadenzaPattoGregorian != null) {
			profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
		}
		if (dataStipulaDidGregorian != null) {
			profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
		}

		pIvaEntePromotore = pattiBeanRow.getAttribute("pivaSoggettoPromotore") != null
				? pattiBeanRow.getAttribute("pivaSoggettoPromotore").toString()
				: "";
		if (StringUtils.isFilledNoBlank(cfEnte) && StringUtils.isFilledNoBlank(codSede)) {
			SourceBean rowEnte = infoEnte(qExec, dc, cfEnte, codSede);
			if (rowEnte != null) {
				String strPartitaIva = (String) rowEnte.getAttribute("row.strpartitaiva");
				if (StringUtils.isFilledNoBlank(strPartitaIva)) {
					pIvaEntePromotore = strPartitaIva;
				}
			}
		}

		if (StringUtils.isFilledNoBlank(pIvaEntePromotore)) {
			profilingPatto.setPivaSoggettoPromotore(pIvaEntePromotore);
		}

		return new ErroreSeta(0);
	}

	public SourceBean soggettoAccreditatoProgramma(QueryExecutorObject qExec, DataConnection dc,
			BigDecimal prgPattoLavoratore, BigDecimal prgColloquio) throws Exception {
		SourceBean programmiBeanRows = null;

		List<DataField> paramProg = new ArrayList<DataField>();
		paramProg = new ArrayList<DataField>();
		paramProg.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
		paramProg.add(dc.createDataField("", Types.BIGINT, prgColloquio));
		qExec.setInputParameters(paramProg);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_SOGGETTO_ACCREDITATO_PROGRAMMIA_PATTO);
		programmiBeanRows = (SourceBean) qExec.exec();

		return programmiBeanRows;
	}

	public SourceBean infoProgramma(QueryExecutorObject qExec, DataConnection dc, BigDecimal prgColloquio)
			throws Exception {
		SourceBean programmiBeanRow = null;

		List<DataField> paramProg = new ArrayList<DataField>();
		paramProg = new ArrayList<DataField>();
		paramProg.add(dc.createDataField("", Types.BIGINT, prgColloquio));
		qExec.setInputParameters(paramProg);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_INFO_PROGRAMMA_PATTO);
		programmiBeanRow = (SourceBean) qExec.exec();

		return programmiBeanRow;
	}

	public SourceBean infoEnte(QueryExecutorObject qExec, DataConnection dc, String cf, String codSede)
			throws Exception {
		SourceBean enteRow = null;

		List<DataField> param = new ArrayList<DataField>();
		param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.VARCHAR, cf.toUpperCase().trim()));
		param.add(dc.createDataField("", Types.VARCHAR, codSede.toUpperCase().trim()));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_INFO_ENTE_PATTO_PROGRAMMA);
		enteRow = (SourceBean) qExec.exec();

		return enteRow;
	}

}
