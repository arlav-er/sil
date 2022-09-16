package it.eng.sil.module.patto;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.sifer.ErroreSifer;
import it.eng.sil.coop.webservices.siferPartecipanteGG.PortTypeProxy;
import it.eng.sil.coop.webservices.siferPartecipanteGG.Request;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.ObjectFactory;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.ComunicazioniObbligatorie;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.ComunicazioniObbligatorie.ComunicazioneObbligatoria;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.Partecipante;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.Partecipante.Accorpamento;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.Patti;
import it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.Patti.ProfilingPatto;
import it.eng.sil.coop.webservices.siferPartecipanteGG.output.RegistraPartecipanteGaranziaGiovaniResponse;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.amministrazione.impatti.PattoBean;
import it.eng.sil.util.xml.XMLValidator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import oracle.jdbc.OracleTypes;

import org.apache.axis.types.Token;
import org.xml.sax.SAXException;

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

public class PartecipanteGGUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PartecipanteGGUtils.class.getName());

	private static String QUERY_STRING_ACCORPATO = "SELECT ac.cdnlavoratore, ac.cdnlavoratoreaccorpato, ac.strcodicefiscaleaccorpato, to_char(ac.dtmins, 'dd/mm/yyyy') AS dtmins FROM an_lavoratore_accorpa ac  WHERE ac.cdnlavoratore = ? ";
	private static String QUERY_STRING_POLITICHEATTIVE_SOGGACC = "SELECT tg.strcodicefiscale, g.strdenominazione FROM ts_utente u, ts_profilatura_utente pu, ts_profilo_gruppo pg, ts_gruppo g, ts_tipo_gruppo tg, ts_tipo t WHERE u.cdnut = pu.cdnut AND pu.cdngruppo = pg.cdngruppo AND pu.cdnprofilo = pg.cdnprofilo AND pg.cdngruppo = g.cdngruppo AND g.cdntipogruppo = tg.cdntipogruppo AND tg.codtipo = t.codtipo AND t.codtipo = 'S' AND u.cdnut = ? ";
	private static String QUERY_STRING_COUNT_PROFILATURE_SOGGACC = "SELECT COUNT(u.cdnut) AS countp FROM ts_utente u, ts_profilatura_utente pu, ts_profilo_gruppo pg, ts_gruppo g WHERE u.cdnut = pu.cdnut AND pu.cdngruppo = pg.cdngruppo AND pu.cdnprofilo = pg.cdnprofilo AND pg.cdngruppo = g.cdngruppo AND u.cdnut = ?";
	
	private static String QUERY_STRING_POLITICHE_ATTIVE = "SELECT    pc.prgpercorso,    pc.prgcolloquio,    az.codazionesifer,    az.codsottoazione,    az.prgazioneragg,    az.flgformazione,    pc.flggruppo, "
    + " pc.numpartecipanti,    to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio,    to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva,    to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata,    co.codcpi,    pc.numygdurataeff,    pc.numygduratamin,    pc.numygduratamax, "
    + " pc.codtipologiadurata,    pc.codesito,    pc.cdnutmod,    ut.strnome,    ut.strcognome,    to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione "
	+ " FROM    or_percorso_concordato   pc    JOIN am_lav_patto_scelta      ps ON ps.strchiavetabella = pc.prgpercorso    JOIN or_colloquio             co ON pc.prgcolloquio = co.prgcolloquio "
    + " JOIN de_azione                az ON az.prgazioni = pc.prgazioni    JOIN ts_utente                ut ON ut.cdnut = pc.cdnutmod    JOIN de_esito                 es ON es.codesito = pc.codesito "
	+ " WHERE    az.flgformazione = 'S'    AND es.flgformazione = 'S'    AND sysdate < es.datfineval    AND ps.codlsttab = 'OR_PER'    AND ps.prgpattolavoratore = ?";
	
	private static String QUERY_STRING_POLITICHE_ATTIVE_PROGRAMMA = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codsottoazione, az.prgazioneragg, az.flgformazione, pc.flggruppo, pc.numpartecipanti, "
			+ " to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, "
			+ " to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, pc.numygduratamin, pc.numygduratamax, "
			+ " pc.codtipologiadurata, pc.codesito, pc.cdnutmod, ut.strnome, ut.strcognome, de_servizio.codmonoprogramma, "
			+ " to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione, ragg.codmonopacchetto "
			+ " FROM or_percorso_concordato pc JOIN am_lav_patto_scelta ps ON (to_number(ps.strchiavetabella) = pc.prgpercorso) "
			+ " JOIN or_colloquio co ON pc.prgcolloquio = co.prgcolloquio JOIN de_servizio ON co.codservizio = de_servizio.codservizio "
			+ " JOIN de_azione az ON az.prgazioni = pc.prgazioni "
			+ " JOIN de_azione_ragg ragg ON az.prgazioneragg = ragg.prgazioniragg "
			+ " JOIN ts_utente ut ON ut.cdnut = pc.cdnutmod JOIN de_esito es ON es.codesito = pc.codesito WHERE az.flgformazione = 'S' AND es.flgformazione = 'S' AND SYSDATE < es.datfineval AND ps.codlsttab = 'OR_PER' AND ps.prgpattolavoratore = ? and co.prgcolloquio = ?";
	
	private static String QUERY_STRING_POLITICHE_ATTIVE_TN = "SELECT pc.prgpercorso, pc.prgcolloquio, az.codazionesifer, az.codsottoazione, az.prgazioneragg, az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutins as cdnutmod, ut.strnome, ut.strcognome, to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione "
			+ " FROM or_percorso_concordato pc JOIN am_lav_patto_scelta ps ON (to_number(ps.strchiavetabella) = pc.prgpercorso) JOIN or_colloquio co ON pc.prgcolloquio = co.prgcolloquio "
			+ " JOIN de_azione az ON az.prgazioni = pc.prgazioni "
			+ " JOIN de_azione_ragg ragg ON az.prgazioneragg = ragg.prgazioniragg "
			+ " JOIN ts_utente ut ON ut.cdnut = pc.cdnutins JOIN de_esito es ON es.codesito = pc.codesito WHERE az.flgformazione = 'S' AND es.flgformazione = 'S' AND SYSDATE < es.datfineval AND ps.codlsttab = 'OR_PER' AND ps.prgpattolavoratore = ?";
	
	private static String QUERY_STRING_TRACCIA_ERRORE = "INSERT INTO ts_tracciamento_formazione (prgtracform, datins, cdnlavoratore, coderrore, strmessaggioerrore, strxmlinviato) VALUES (s_ts_tracciamento_formazione.nextval, sysdate , ?, (SELECT coderrore FROM de_errore_formazione WHERE coderrore = ?), ?, ?)";
	private static String QUERY_STRING_DATA_TITOLO_STUDIO = "SELECT prst.numanno FROM pr_studio prst WHERE prst.cdnLavoratore = ? AND prst.codtitolo = ?";
	private static String QUERY_STRING_REGIONE_ER = "SELECT codregione FROM de_provincia INNER JOIN ts_generale ON (ts_generale.codprovinciasil = de_provincia.codprovincia) where ts_generale.prggenerale = 1";
	private static String QUERY_STRING_DATA_RIF_CONTROLLO_RER = "SELECT strvalore FROM ts_config_loc WHERE codtipoconfig = 'GG_A03FC' AND strcodrif = (SELECT codprovinciasil FROM ts_generale where ts_generale.prggenerale = 1)";
	private static String QUERY_STRING_SCADENZA_EVIDENZA = "UPDATE an_evidenza SET datdatascad = sysdate, strevidenza = strevidenza || ' Batch - Evidenza scaduta in seguito all''invio a Formazione eseguito in data ' || sysdate || '\n', numkloevidenza = numkloevidenza + 1 WHERE PRGTIPOEVIDENZA = (SELECT prgtipoevidenza FROM de_tipo_evidenza WHERE codtipoevidenza = 'EG') AND cdnlavoratore = ? AND datdatascad > sysdate";
	private static String QUERY_STRING_DATA_ADESIONE_PA = "select to_char(max(pt.DATADESIONEPA), 'dd/mm/yyyy') DATADESIONEPA from am_patto_lavoratore pt where pt.cdnlavoratore = ? and pt.codstatoatto = 'PR'";
	private static String QUERY_STRING_POLITICHE_ATTIVE_GU_NON_ASSOCIATE = "SELECT COUNT(*) AS NUMAZIONIGUNONASSOCIATE FROM or_colloquio co inner join or_percorso_concordato pc ON (co.prgcolloquio = pc.prgcolloquio) "
			+ " inner join de_azione az ON (pc.prgazioni = az.prgazioni) "
			+ " inner join de_azione_ragg ragg ON (az.prgazioneragg = ragg.prgazioniragg and ragg.codmonopacchetto = 'GU') "
			+ " inner join de_esito es on (pc.codesito = es.codesito) "
			+ " left join am_patto_lavoratore pt on (co.cdnlavoratore = pt.cdnlavoratore and pt.codstatoatto = 'PR' and "
			+ "	( (pt.codtipopatto = 'MGGU') or "
			+ "   (getEsisteProgrammaPatto(pt.prgpattolavoratore, '''MGGU''') > 0) "
			+ " ) "
			+ " ) "
			+ " left join am_lav_patto_scelta ps on (pt.prgpattolavoratore = ps.prgpattolavoratore and ps.codlsttab = 'OR_PER' and pc.prgpercorso = to_number(ps.strchiavetabella)) "
			+ " WHERE co.cdnlavoratore = ? and az.flgformazione = 'S' AND es.flgformazione = 'S' AND SYSDATE < es.datfineval and ps.prglavpattoscelta is null";
	
	private static String QUERY_STRING_CHECK_MISURA = "SELECT pc.prgpercorso, pc.prgcolloquio, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datacolloquio FROM or_percorso_concordato pc inner join or_colloquio co ON pc.prgcolloquio = co.prgcolloquio inner join de_azione az ON az.prgazioni = pc.prgazioni WHERE co.cdnlavoratore = ? and az.flgformazione = 'S' and az.codazionesifer = ? and pc.codesito = ? order by co.datcolloquio asc";
	
	private static SourceBean regioneBeanRows = null;
	
	private static String QUERY_PROGRAMMI_PATTO = 
		"select distinct de_servizio.codmonoprogramma "
		+ " from am_patto_lavoratore inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE) "
		+ " inner join or_percorso_concordato on (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso) "
		+ " inner join or_colloquio coll on (or_percorso_concordato.prgcolloquio = coll.prgcolloquio) "
	  	+ " inner join de_servizio on (coll.codservizio = de_servizio.codservizio) "
	  	+ " inner join de_azione on (or_percorso_concordato.prgazioni = de_azione.prgazioni) "
	  	+ " inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg) "
		+ " where am_patto_lavoratore.PRGPATTOLAVORATORE = ? and am_lav_patto_scelta.CODLSTTAB = 'OR_PER'";
	
	private static String QUERY_PROGRAMMI_PATTO_CODMONOPROG = 
			"select distinct coll.prgcolloquio "
			+ " from am_patto_lavoratore inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE) "
			+ " inner join or_percorso_concordato on (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso) "
			+ " inner join or_colloquio coll on (or_percorso_concordato.prgcolloquio = coll.prgcolloquio) "
		  	+ " inner join de_servizio on (coll.codservizio = de_servizio.codservizio) "
		  	+ " inner join de_azione on (or_percorso_concordato.prgazioni = de_azione.prgazioni) "
		  	+ " inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg) "
			+ " where am_patto_lavoratore.PRGPATTOLAVORATORE = ? and am_lav_patto_scelta.CODLSTTAB = 'OR_PER' and de_servizio.codmonoprogramma = ?";
	
	private static ObjectFactory factory = new ObjectFactory();
	
	public static final String WS_NOME_METODO_PARTECIPANTEGG = "registraPartecipanteGaranziaGiovani";
	private static final String INPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "PartecipanteGaranziaGiovani" + File.separator
			+ "PartecipanteGaranziaGiovani.xsd";
	private static final String OUTPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "PartecipanteGaranziaGiovani" + File.separator
			+ "registraPartecipanteGaranziaGiovani_response.xsd";

	public static ErroreSifer sendPartecipanteGG(BigDecimal cdnLavoratore, SourceBean anLavBeanRows,
			SourceBean permSoggBeanRows, SourceBean codProvinciaBeanRows, SourceBean titStudioBeanRows,
			SourceBean pattoBeanRows, SourceBean movimentazioneBeanRows, SourceBean wsRows, SourceBean pattoRegionaleRows,
			SourceBean conferimentoRows) throws Exception {
		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		
		try {
			qExec = getQueryExecutorObject();
			dc = qExec.getDataConnection();
			_logger.error("Connessione istanziata");
			
			qExec.setInputParameters(null);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(QUERY_STRING_REGIONE_ER);
			regioneBeanRows = (SourceBean) qExec.exec();
			
			PartecipanteGaranziaGiovani msg = factory.createPartecipanteGaranziaGiovani();
			Partecipante partecipante = factory.createPartecipanteGaranziaGiovaniPartecipante();
			Patti patti = factory.createPartecipanteGaranziaGiovaniPatti();
			ComunicazioniObbligatorie comunicazioniObbligatorie = factory
					.createPartecipanteGaranziaGiovaniComunicazioniObbligatorie();

			/* codice di errore di ritorno della funzione */
			ErroreSifer erroreSifer = null;
			
			erroreSifer = PartecipanteGGUtils.getPartecipante(qExec, dc, cdnLavoratore, partecipante, anLavBeanRows,
					permSoggBeanRows, codProvinciaBeanRows, titStudioBeanRows, conferimentoRows);
			if (erroreSifer.errCod != 0) {
				_logger.error("PartecipanteGG: Invio a formazione non effettuato: cdnlavoratore = " + cdnLavoratore);
				return erroreSifer;
			}
			
			partecipante.getAccorpamento().addAll(
					PartecipanteGGUtils.getAccorpamenti(qExec, dc, String.valueOf(cdnLavoratore)));
			erroreSifer = PartecipanteGGUtils.getPatti(qExec, dc, patti, pattoBeanRows, pattoRegionaleRows, cdnLavoratore);
			if (erroreSifer.errCod != 0) {
				_logger.error("PartecipanteGG: Invio a formazione non effettuato: cdnlavoratore = " + cdnLavoratore);
				return erroreSifer;
			}
			
			PartecipanteGGUtils.getComunicazioniObbligatorie(comunicazioniObbligatorie, movimentazioneBeanRows);

			msg.setPartecipante(partecipante);
			msg.setPatti(patti);
			msg.setComunicazioniObbligatorie(comunicazioniObbligatorie);
			
			String input = "";
			/* validazione xsd dell'input */
			try {
				input = PartecipanteGGUtils.convertInputToString(msg);
				_logger.debug("SIL -> SIFER: cdnLavoratore " + cdnLavoratore + " xml input\n" + input);
				PartecipanteGGUtils.validazioneXml(input, INPUT_XSD);
			} catch (Exception e) {
				_logger.error("PartecipanteGG: Errore in validazione input. cdnlavoratore = " + cdnLavoratore + " Tracciato = " + input);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,
						MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INPUT);
				String errMsg = error.getDescription();
				ErroreSifer returnError = new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INPUT, errMsg + ":\n" + e.getMessage());
				returnError.setStrXMLInviato(input);
				return returnError;
			}

			/* Invocazione del servizio */
			PortTypeProxy proxy = new PortTypeProxy();

			// END_POINT_NAME_PARTECIPANTEGG;
			proxy.setEndpoint(getEndpointUrl(dc.getInternalConnection()));

			String strUserId = null;
			String strPassword = null;
			if (wsRows != null) {
				strUserId = (String) wsRows.getAttribute("ROW.STRUSERID");
				strPassword = (String) wsRows.getAttribute("ROW.STRPASSWORD");
			}
			Token token = new Token(strPassword);
			Request request = new Request(strUserId, token, WS_NOME_METODO_PARTECIPANTEGG, input);
			String output = "";
			try {
				output = proxy.requestService(request);
			} // try
			catch (Exception ex) {
				_logger.error("Il servizio invocato ha restituito un errore. Errore ricevuto: " + ex.getMessage());
				ErroreSifer returnError = new ErroreSifer(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE);
				returnError.setStrXMLInviato(input);
				return returnError;
			//	return new ErroreSifer(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE);
			}
			_logger.debug("SIL -> SIFER: cdnLavoratore " + cdnLavoratore + " xml output\n" + output);

			/* validazione xsd dell'output */
			try {
				PartecipanteGGUtils.validazioneXml(output, OUTPUT_XSD);
			} catch (Exception e) {
				_logger.error("PartecipanteGG: Errore in validazione output. cdnlavoratore = " + cdnLavoratore);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,
						MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_OUTPUT);
				String errMsg = error.getDescription();
				ErroreSifer returnError = new  ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_OUTPUT, errMsg + ":\n" + e.getMessage());
				returnError.setStrXMLInviato(input);
				return returnError;
			}

			RegistraPartecipanteGaranziaGiovaniResponse xmlOutput = PartecipanteGGUtils
					.convertRegistraPartecipanteToString(output);
			String codiceRisposta = xmlOutput.getResponseCodice();

			/*
			 * deve iniziare per 0 perche' in test la risposta positiva non e' 0
			 * ma qualcosa tipo "0 - TEST", ma evidentemente non esattamente
			 * cosi'...
			 */
			if (codiceRisposta == null || !codiceRisposta.startsWith("0")) {
				_logger.error("PartecipanteGG: Errore codice risposta SIFER. codice=" + codiceRisposta
						+ " - cdnlavoratore = " + cdnLavoratore);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,
						MessageCodes.YG.ERR_KO_WS_PARTECIPANTE_GG_OUTPUT);

				String descrErroreRisposta = codiceRisposta + " - " + xmlOutput.getResponseDescrizione();

				String errMsg = error.getDescription();
				ErroreSifer returnError = new ErroreSifer(MessageCodes.YG.ERR_KO_WS_PARTECIPANTE_GG_OUTPUT, errMsg + ":\n"
						+ descrErroreRisposta);
				returnError.setStrXMLInviato(input);
				return returnError;
			} else {
				/* faccio scadere tutte le evidenze di tipo EG del lavoratore */
				List<DataField> param = new ArrayList<DataField>();
				param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
				qExec.setInputParameters(param);
				qExec.setType(QueryExecutorObject.UPDATE);
				qExec.setStatement(QUERY_STRING_SCADENZA_EVIDENZA);
				qExec.exec();
			}
		} catch (Throwable e) {
			_logger.error("Errore: " + e);
		} finally {
			_logger.debug("Connessione rilasciata");
			Utils.releaseResources(dc, command, dr);
		}

		return new ErroreSifer(0);
	}

	/* Converte l'oggetto che rappresenta il messaggio di input in xml */
	private static String convertInputToString(PartecipanteGaranziaGiovani msg) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(PartecipanteGaranziaGiovani.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(msg, writer);
		String xml = writer.getBuffer().toString();
		return xml;
	}

	private static RegistraPartecipanteGaranziaGiovaniResponse convertRegistraPartecipanteToString(String xmlOutput)
			throws JAXBException, SAXException {
		RegistraPartecipanteGaranziaGiovaniResponse responseOutput = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(RegistraPartecipanteGaranziaGiovaniResponse.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		responseOutput = (RegistraPartecipanteGaranziaGiovaniResponse) jaxbUnmarshaller.unmarshal(new StringReader(
				xmlOutput));
		return responseOutput;
	}

	/* Valida l'xml passato in input con l'xsd passato come secondo parametro */
	private static void validazioneXml(String xml, String xsdPath) throws SAXException, IOException {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		File schemaFile = new File(xsdPath);
		StreamSource streamSource = new StreamSource(schemaFile);
		Schema schema = factory.newSchema(streamSource);
		Validator validator = schema.newValidator();
		StreamSource datiXmlStreamSource = new StreamSource(new StringReader(xml));
		validator.validate(datiXmlStreamSource);
	}
	
	private static ErroreSifer getPartecipante(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore,
			Partecipante partecipante, SourceBean anLavBeanRows, SourceBean permSoggBeanRows,
			SourceBean codProvinciaBeanRows, SourceBean titStudioBeanRows, SourceBean conferimentoRows) throws DatatypeConfigurationException,
			ParseException {
		String codProvincia = null;
		String strCodiceFiscale = null;
		String strCognome = null;
		String strNome = null;
		String strSesso = null;
		String datNasc = null;
		XMLGregorianCalendar datNascGregorian = null;
		String codComNas = null;
		String codCittadinanza = null;
		String codCittadinanza2 = null;
		String strCell = null;
		String strEmail = null;
		String codComRes = null;
		String strIndirizzoRes = null;
		String strCapRes = null;
		String strTelRes = null;
		String codComDom = null;
		String strIndirizzoDom = null;
		String strCapDom = null;
		String strTelDom = null;
		String codStatus = null;
		String strNumdocumento = null;
		String datScadenza = null;
		XMLGregorianCalendar datScadenzaGregorian = null;
		String codMotivoRil = null;
		String codTitolo = null;
		BigDecimal numAnno = null;
		BigDecimal numIscrDisabili = null;
		XMLGregorianCalendar numAnnoGregorian = null;

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
			codCittadinanza2 = (String) anLavBeanRows.getAttribute("ROW.CODCITTADINANZA2");
			strCell = (String) anLavBeanRows.getAttribute("ROW.STRCELL");
			strEmail = (String) anLavBeanRows.getAttribute("ROW.STREMAIL");
			codComRes = (String) anLavBeanRows.getAttribute("ROW.CODCOMRES");
			strIndirizzoRes = (String) anLavBeanRows.getAttribute("ROW.STRINDIRIZZORES");
			strCapRes = (String) anLavBeanRows.getAttribute("ROW.STRCAPRES");
			strTelRes = (String) anLavBeanRows.getAttribute("ROW.STRTELRES");
			codComDom = (String) anLavBeanRows.getAttribute("ROW.CODCOMDOM");
			strIndirizzoDom = (String) anLavBeanRows.getAttribute("ROW.STRINDIRIZZODOM");
			strCapDom = (String) anLavBeanRows.getAttribute("ROW.STRCAPDOM");
			strTelDom = (String) anLavBeanRows.getAttribute("ROW.STRTELDOM");
			numIscrDisabili = (BigDecimal) anLavBeanRows.getAttribute("ROW.NUMISCRDISABILI");
		}
		if (permSoggBeanRows != null) {
			codStatus = (String) permSoggBeanRows.getAttribute("ROW.CODSTATUS");
			strNumdocumento = (String) permSoggBeanRows.getAttribute("ROW.STRNUMDOCUMENTO");
			datScadenza = (String) permSoggBeanRows.getAttribute("ROW.DATSCADENZA");
			if (datScadenza != null) {
				datScadenzaGregorian = toXMLGregorianCalendarDateOnly(datScadenza);
			}
			codMotivoRil = (String) permSoggBeanRows.getAttribute("ROW.CODMOTIVORIL");
		}
		if (codProvinciaBeanRows != null) {
			codProvincia = (String) codProvinciaBeanRows.getAttribute("ROW.CODMIN");
		}
		if (titStudioBeanRows != null) {
			codTitolo = (String) titStudioBeanRows.getAttribute("ROW.CODTITOLO");
		}

		/* query per data titolo di studio */
		if (codTitolo != null && !codTitolo.equals("")) {
			SourceBean prStudioBeanRows = null;
			List<DataField> param = new ArrayList<DataField>();
			param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
			param.add(dc.createDataField("", Types.VARCHAR, codTitolo));
			qExec.setInputParameters(param);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(QUERY_STRING_DATA_TITOLO_STUDIO);
			prStudioBeanRows = (SourceBean) qExec.exec();
	
			if (prStudioBeanRows != null) {
				if (prStudioBeanRows.getAttributeAsVector("ROW").size() == 1) {
					numAnno = (BigDecimal) prStudioBeanRows.getAttribute("ROW.NUMANNO");
					if (numAnno != null) {
						Calendar c = new GregorianCalendar();
						c.set(Calendar.YEAR, numAnno.toBigInteger().intValue());
						String numAnnoString = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
						numAnnoGregorian = toXMLGregorianCalendarYearOnly(numAnnoString);
					}
				}
			}
		}
		else {
			if ("10".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
				// recupero il titolo di studio dalla tabella del conferimento
				if (conferimentoRows != null) {
					codTitolo = (String) conferimentoRows.getAttribute("ROW.CODTITOLO");
				}	
			}
		}

		/* dati obbligatori */
		if (strCodiceFiscale == null || strCodiceFiscale.isEmpty() || codProvincia == null || codProvincia.isEmpty()
				|| strCognome == null || strCognome.isEmpty() || strNome == null || strNome.isEmpty()
				|| strSesso == null || strSesso.isEmpty() || datNasc == null || datNasc.isEmpty() || codComNas == null
				|| codComNas.isEmpty() || strEmail == null || strEmail.isEmpty()) {

			return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE);
		}

		if (codCittadinanza != null) {
			partecipante.setCittadinanza(codCittadinanza.trim());
		}
		if (codCittadinanza2 != null) {
			partecipante.setCittadinanzaSeconda(codCittadinanza2.trim());
		}
		if (strCodiceFiscale != null) {
			partecipante.setCodiceFiscale(strCodiceFiscale.trim());
		}
		if (strCognome != null) {
			partecipante.setCognome(strCognome.trim());
		}
		if (strCapDom != null) {
			partecipante.setDomicilioCap(strCapDom.trim());
		}
		if (codComDom != null) {
			partecipante.setDomicilioCodiceCatastale(codComDom.trim());
		}
		if (strIndirizzoDom != null) {
			partecipante.setDomicilioIndirizzo(strIndirizzoDom.trim());
		}
		if (strTelDom != null) {
			partecipante.setDomicilioTelefono(strTelDom.trim());
		}
		if (strEmail != null) {
			if (checkEmail(strEmail)) {
				partecipante.setEmail(strEmail.trim());
			} else {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_EMAIL);
			}
		}
		if (codComNas != null) {
			partecipante.setNascitaCodiceCatastale(codComNas.trim());
		}
		if (codProvincia != null) {
			partecipante.setCodiceProvincia(codProvincia.trim());
		}
		partecipante.setNascitaData(datNascGregorian);
		if (strNome != null) {
			partecipante.setNome(strNome.trim());
		}
		if (codStatus != null) {
			partecipante.setPermessoCarta(codStatus.trim());
		}
		partecipante.setPermessoCartaDataScadenza(datScadenzaGregorian);
		if (codMotivoRil != null) {
			partecipante.setPermessoCartaMotivo(codMotivoRil.trim());
		}
		if (strNumdocumento != null) {
			partecipante.setPermessoCartaNumero(strNumdocumento.trim());
		}
		if (strCapRes != null) {
			partecipante.setResidenzaCap(strCapRes.trim());
		}
		if (codComRes != null) {
			partecipante.setResidenzaCodiceCatastale(codComRes.trim());
		}
		if (strIndirizzoRes != null) {
			partecipante.setResidenzaIndirizzo(strIndirizzoRes.trim());
		}
		if (strTelRes != null) {
			partecipante.setResidenzaTelefono(strTelRes.trim());
		}
		if (strSesso != null) {
			partecipante.setSesso(strSesso.trim());
		}
		if (strCell != null) {
			partecipante.setTelefonoCellulare(strCell.trim());
		}
		if (codTitolo != null) {
			partecipante.setTitoloStudio(codTitolo.trim());
		}
		partecipante.setTitoloStudioAnnoConseguimento(numAnnoGregorian);
		if (numIscrDisabili != null) {
			if (numIscrDisabili.intValue() > 0) {
				partecipante.setFlagCatProtetta("S");
			}
			else {
				partecipante.setFlagCatProtetta("N");
			}
		}

		return new ErroreSifer(0);
	}

	public static List<Accorpamento> getAccorpamenti(QueryExecutorObject qExec, DataConnection dc, String cdnLavoratore)
			throws Exception {
		List<Accorpamento> accorpamentoList = new ArrayList<PartecipanteGaranziaGiovani.Partecipante.Accorpamento>();

		SourceBean anLavAccBeanRows = null;
		try {
			List<DataField> param = new ArrayList<DataField>();
			param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
			qExec.setInputParameters(param);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(QUERY_STRING_ACCORPATO);
			anLavAccBeanRows = (SourceBean) qExec.exec();
		} catch (Exception e) {
			_logger.error("Impossibile recuperare i dati di accormapento. cdnlavoratore/cdlavoratoreaccorpato: "
					+ cdnLavoratore);
			throw e;
		}

		if (anLavAccBeanRows != null) {
			Vector anLavAccBeanVector = anLavAccBeanRows.getAttributeAsVector("ROW");

			String cdnLavoratoreNew = null;
			String cdnLavoratoreAccorpato = null;
			String strCodiceFiscaleAccorpato = null;
			String dtmIns = null;
			XMLGregorianCalendar dtmInsGregorian = null;

			for (int i = 0; i < anLavAccBeanVector.size(); i++) {
				dtmInsGregorian = null;
				SourceBean anLavAccBeanRow = (SourceBean) anLavAccBeanVector.elementAt(i);
				Accorpamento accorpamento = factory.createPartecipanteGaranziaGiovaniPartecipanteAccorpamento();

				cdnLavoratoreNew = (String) anLavAccBeanRow.getAttribute("CDNLAVORATORE");
				cdnLavoratoreAccorpato = (String) anLavAccBeanRow.getAttribute("CDNLAVORATOREACCORPATO");

				strCodiceFiscaleAccorpato = (String) anLavAccBeanRow.getAttribute("STRCODICEFISCALEACCORPATO");
				dtmIns = (String) anLavAccBeanRow.getAttribute("DTMINS");
				if (dtmIns != null) {
					dtmInsGregorian = toXMLGregorianCalendarDateOnly(dtmIns);
				}

				if (strCodiceFiscaleAccorpato != null) {
					accorpamento.setCodiceFiscaleAccorpato(strCodiceFiscaleAccorpato.trim());
				}
				accorpamento.setDataAccorpamento(dtmInsGregorian);
				accorpamentoList.add(accorpamento);

				/* seguo la catena degli accorpamenti precedenti (se ce ne sono) */
				if (!cdnLavoratoreNew.equals(cdnLavoratoreAccorpato)) {
					accorpamentoList.addAll(getAccorpamenti(qExec, dc, cdnLavoratoreAccorpato));
				}
			}
		}

		return accorpamentoList;
	}

	private static ErroreSifer getPatti(QueryExecutorObject qExec, DataConnection dc, Patti patti,
			SourceBean pattiBeanRows, SourceBean pattoRegionaleRows, BigDecimal cdnLavoratore) throws Exception {
		boolean azPattoRegAssociata = false;
		
		ArrayList <BigDecimal>pattiAssociatiA2R = null;
		if (pattoRegionaleRows != null) {
			BigDecimal prgPattoLavoratore = null;
			Vector pattiBeanVector = pattoRegionaleRows.getAttributeAsVector("ROW");
			int numAzioniPattiReg = pattiBeanVector.size();
			if (numAzioniPattiReg > 0) {
				pattiAssociatiA2R = new ArrayList<BigDecimal>();
				for (int i = 0; i < numAzioniPattiReg; i++) {
					SourceBean pattiBeanRow = (SourceBean) pattiBeanVector.elementAt(i);
					prgPattoLavoratore = (BigDecimal) pattiBeanRow.getAttribute("PRGPATTOLAVORATORE");
					if (prgPattoLavoratore != null) {
						pattiAssociatiA2R.add(prgPattoLavoratore);
					}
				}
			}
		}
		
		/* controllo specifico per Umbria commentato in attesa di chiarimenti
		if ("10".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
			BigDecimal numeroAzGUNonAssociate = PartecipanteGGUtils.contaAzioniGUNonAssociate(qExec, dc, cdnLavoratore);
			if (numeroAzGUNonAssociate != null && numeroAzGUNonAssociate.intValue() > 0) {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_AZIONI_GU_NON_ASSOCIATE);
			}
		}*/
		
		if (pattiBeanRows != null) {
			Vector pattiBeanVector = pattiBeanRows.getAttributeAsVector("ROW");
			//recupero eventuale data adesione PA
			String dataAdesionePA = PartecipanteGGUtils.recuperaDataAdesionePA(qExec, dc, cdnLavoratore);
			
			BigDecimal prgPattoLavoratore = null;
			BigDecimal numIndiceSvantaggio = null;
			BigDecimal numIndiceSvantaggio2 = null;
			String datRiferimento = null;
			XMLGregorianCalendar datRiferimentoGregorian = null;
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
			XMLGregorianCalendar datStipulaGregorian = null;
			XMLGregorianCalendar dataChiusuraPattoGregorian = null;
			XMLGregorianCalendar datAdesioneGgGregorian = null;
			XMLGregorianCalendar dataScadenzaPattoGregorian = null;
			XMLGregorianCalendar dataStipulaDidGregorian = null;
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			
			for (int i = 0; i < pattiBeanVector.size(); i++) {
				datStipulaGregorian = null;
				dataChiusuraPattoGregorian = null;
				datAdesioneGgGregorian = null;
				dataScadenzaPattoGregorian = null;
				dataStipulaDidGregorian = null;
				boolean calcolaMesiAnzianitaDidAdesionePA = false;
				
				SourceBean pattiBeanRow = (SourceBean) pattiBeanVector.elementAt(i);

				prgPattoLavoratore = (BigDecimal) pattiBeanRow.getAttribute("PRGPATTOLAVORATORE");
				numIndiceSvantaggio = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO");
				numIndiceSvantaggio2 = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO2");
				datRiferimento = (String) pattiBeanRow.getAttribute("DATRIFERIMENTO");
				if (datRiferimento != null) {
					datRiferimentoGregorian = toXMLGregorianCalendarDateOnly(datRiferimento);
				}
				datStipula = (String) pattiBeanRow.getAttribute("DATSTIPULA");
				if (datStipula != null) {
					datStipulaGregorian = toXMLGregorianCalendarDateOnly(datStipula);
				}
				codCpi = (String) pattiBeanRow.getAttribute("CODCPI");
				dataChiusuraPatto = (String) pattiBeanRow.getAttribute("DATFINE");
				if (dataChiusuraPatto != null) {
					dataChiusuraPattoGregorian = toXMLGregorianCalendarDateOnly(dataChiusuraPatto);
				}
				motivoChiusuraPatto = (String) pattiBeanRow.getAttribute("CODMOTIVOFINEATTO");
				misuraPatto = (String) pattiBeanRow.getAttribute("CODTIPOPATTO");
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
				
				// inizio gestione profiling dinamica a seconda dei servizi inviabili
				if (Properties.TN.equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
					BigDecimal prgColloquioCurr = (BigDecimal)pattiBeanRow.getAttribute("PRGCOLLOQUIO");
					String codMonoProgramma = (String) pattiBeanRow.getAttribute("CODMONOPROGRAMMA");
					String dataInizioProgramma = (String) pattiBeanRow.getAttribute("DATINIZIOPROGRAMMA");
					String dataFineProgramma = (String) pattiBeanRow.getAttribute("DATFINEPROGRAMMA");
					XMLGregorianCalendar dataInizioProgrammaGregorian = null;
					XMLGregorianCalendar dataFineProgrammaGregorian = null;
					if (dataInizioProgramma != null) {
						dataInizioProgrammaGregorian = toXMLGregorianCalendarDateOnly(dataInizioProgramma);
					}
					if (dataFineProgramma != null) {
						dataFineProgrammaGregorian = toXMLGregorianCalendarDateOnly(dataFineProgramma);
					}
					
					ProfilingPatto profilingPatto = factory.createPartecipanteGaranziaGiovaniPattiProfilingPatto();
					
					if (codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
					
						ErroreSifer erroreSiferAdesione = getProfilingProgrammaDataAdesione(qExec, dc, prgColloquioCurr,
								prgPattoLavoratore, profilingPatto, cdnLavoratore, dataInizioProgramma);
						if (erroreSiferAdesione.errCod != 0) {
							/* riporto l'errore al chiamante */
							return erroreSiferAdesione;
						}
					}
					else {
						profilingPatto.setAdesioneGgData(dataInizioProgrammaGregorian);
					}
					
					profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
					
					if (numIndiceSvantaggio != null) {
						profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
					} else if (codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) &&
							   DateUtils.compare(datStipula, "01/02/2015") < 0) {
						Vector<String> params = new Vector<String>();
						params.add(datStipula);
						return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_OLD, params);
					}
					
					if (numIndiceSvantaggio2 != null) {
						profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
					} else {
						if (codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
							Vector<String> params = new Vector<String>();
							params.add(datStipula);
							return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_NEW, params);
						}
					}
					
					profilingPatto.setPattoGgData(datStipulaGregorian);
					if (codCpi != null) {
						profilingPatto.setPattoCpi(codCpi.trim());
					}
					profilingPatto.setDataChiusuraPatto(dataFineProgrammaGregorian);
					if (motivoChiusuraPatto != null) {
						profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
					}
					if (numProtocollo != null) {
						profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
					}
					if (codStatoOccupaz != null) {
						profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
					}
					
					profilingPatto.setTipoMisuraPatto(codMonoProgramma);
					
					profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
					profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
					
					PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
							.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttive();

					/* recupero delle azioni relative al patto */
					try {
						ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttiveProgramma(qExec, dc, prgPattoLavoratore,
								prgColloquioCurr, profilingPatto, politicheAttive, patti, cdnLavoratore, datStipula);
						
						if (erroreSifer.errCod != 0) {
							/* riporto l'errore al chiamante */
							return erroreSifer;
						}
					} catch (Exception e) {
						_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
						return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
					}
				}
				else {
					Vector<String>programmi = checkProgrammiPatto(qExec, dc, prgPattoLavoratore);
					boolean isPattoGG = false;
					boolean isPattoGGU = false;
					boolean isPattoOver30 = false;
					boolean isPattoGGUOver45 = false;
					boolean isPattoMInat = false;
					
					if (programmi != null && !programmi.isEmpty()) {
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
							isPattoGG = true;
						}
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
							isPattoGGU = true;
						}
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)) {
							isPattoOver30 = true;
						}
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)) {
							isPattoGGUOver45 = true;
						}
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
							isPattoMInat = true;
						}
					}
				
					// inizio gestione profiling Umbria per patti Adesione Nuovo Programma
					if ("10".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE")) && misuraPatto.equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {
						if (isPattoGG) {
							Vector<BigDecimal>programmiKey = checkProgrammiFromCodice(qExec, dc, prgPattoLavoratore, PattoBean.DB_MISURE_GARANZIA_GIOVANI);
							if (programmiKey != null && !programmiKey.isEmpty()) {
								for (int j = 0; j < programmiKey.size(); j++) {
									BigDecimal prgColloquioCurr = programmiKey.get(j);
									ProfilingPatto profilingPatto = factory.createPartecipanteGaranziaGiovaniPattiProfilingPatto();
									ErroreSifer erroreSiferAdesione = getProfilingProgrammaDataAdesione(qExec, dc, prgColloquioCurr,
											prgPattoLavoratore, profilingPatto, cdnLavoratore, datStipula);
									if (erroreSiferAdesione.errCod != 0) {
										/* riporto l'errore al chiamante */
										return erroreSiferAdesione;
									}
									
									profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
									/******/
									XMLGregorianCalendar datProgrammaGregorianIni = null;
									XMLGregorianCalendar datProgrammaGregorianFine = null;
									String datProgramma = null;
									String datFineProgramma = null;									
									SourceBean infoProg = PattoBean.getInfoProgramma(qExec, dc, prgColloquioCurr);
									if (infoProg != null) {
										datProgramma = (String) infoProg.getAttribute("row.datColloquio");
										if (datProgramma != null && !datProgramma.equals("")) {
											datProgrammaGregorianIni = toXMLGregorianCalendarDateOnly(datProgramma);										
										}
										datFineProgramma = (String) infoProg.getAttribute("row.datFineProgramma");
										if (datFineProgramma != null && !datFineProgramma.equals("")) {
											datProgrammaGregorianFine = toXMLGregorianCalendarDateOnly(datFineProgramma);										
										}
									}
								
									/*****/									
									if (numIndiceSvantaggio != null) {
										profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
									} else if (DateUtils.compare(datStipula, "01/02/2015") < 0) {
										Vector<String> params = new Vector<String>();
										params.add(datStipula);
										return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_OLD, params);
									}
									
									if (numIndiceSvantaggio2 != null) {
										profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
									} else {
										Vector<String> params = new Vector<String>();
										params.add(datStipula);
										return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_NEW, params);
									}
									
									profilingPatto.setPattoGgData(datProgrammaGregorianIni);
									if (codCpi != null) {
										profilingPatto.setPattoCpi(codCpi.trim());
									}
									profilingPatto.setDataChiusuraPatto(datProgrammaGregorianFine);
									if (motivoChiusuraPatto != null) {
										profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
									}
									if (numProtocollo != null) {
										profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
									}
									if (codStatoOccupaz != null) {
										profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
									}
									
									profilingPatto.setTipoMisuraPatto(PattoBean.DB_MISURE_GARANZIA_GIOVANI);
									
									profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
									profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
									
									PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
											.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttive();
	
									/* recupero delle azioni relative al patto */
									try {
										ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttiveProgramma(qExec, dc, prgPattoLavoratore,
												prgColloquioCurr, profilingPatto, politicheAttive, patti, cdnLavoratore, datStipula);
										
										if (erroreSifer.errCod != 0) {
											/* riporto l'errore al chiamante */
											return erroreSifer;
										}
									} catch (Exception e) {
										_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
										return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
									}
								}
							}
						}
						
						if (isPattoGGU) {
							Vector<BigDecimal>programmiKey = checkProgrammiFromCodice(qExec, dc, prgPattoLavoratore, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA);
							if (programmiKey != null && !programmiKey.isEmpty()) {
								for (int j = 0; j < programmiKey.size(); j++) {
									BigDecimal prgColloquioCurr = programmiKey.get(j);
									ProfilingPatto profilingPatto = factory.createPartecipanteGaranziaGiovaniPattiProfilingPatto();
									ErroreSifer erroreSiferAdesione = getProfilingProgrammaDataAdesione(qExec, dc, prgColloquioCurr,
											prgPattoLavoratore, profilingPatto, cdnLavoratore, datStipula);
									if (erroreSiferAdesione.errCod != 0) {
										/* riporto l'errore al chiamante */
										return erroreSiferAdesione;
									}
									
									profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
									/******/
									XMLGregorianCalendar datProgrammaGregorianIni = null;
									XMLGregorianCalendar datProgrammaGregorianFine = null;
									String datProgramma = null;
									String datFineProgramma = null;
									SourceBean infoProg = PattoBean.getInfoProgramma(qExec, dc, prgColloquioCurr);
									if (infoProg != null) {
										datProgramma = (String) infoProg.getAttribute("row.datColloquio");
										if (datProgramma != null && !datProgramma.equals("")) {
											datProgrammaGregorianIni = toXMLGregorianCalendarDateOnly(datProgramma);										
										}
										datFineProgramma = (String) infoProg.getAttribute("row.datFineProgramma");
										if (datFineProgramma != null && !datFineProgramma.equals("")) {
											datProgrammaGregorianFine = toXMLGregorianCalendarDateOnly(datFineProgramma);										
										}
									}
									
									/*****/
									if (numIndiceSvantaggio != null) {
										profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
									}
									if (numIndiceSvantaggio2 != null) {
										profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
									}
									profilingPatto.setPattoGgData(datProgrammaGregorianIni);
									if (codCpi != null) {
										profilingPatto.setPattoCpi(codCpi.trim());
									}
									profilingPatto.setDataChiusuraPatto(datProgrammaGregorianFine);
									if (motivoChiusuraPatto != null) {
										profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
									}
									if (numProtocollo != null) {
										profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
									}
									if (codStatoOccupaz != null) {
										profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
									}
									
									profilingPatto.setTipoMisuraPatto(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA);
									
									profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
									profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
									
									PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
											.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttive();
	
									/* recupero delle azioni relative al patto */
									try {
										ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttiveProgramma(qExec, dc, prgPattoLavoratore,
												prgColloquioCurr, profilingPatto, politicheAttive, patti, cdnLavoratore, datStipula);
										
										if (erroreSifer.errCod != 0) {
											/* riporto l'errore al chiamante */
											return erroreSifer;
										}
									} catch (Exception e) {
										_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
										return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
									}
								}
							}
						}
						
if (isPattoOver30) {
						Vector<BigDecimal>programmiKey = checkProgrammiFromCodice(qExec, dc, prgPattoLavoratore, PattoBean.DB_MISURE_OVER_30);
						if (programmiKey != null && !programmiKey.isEmpty()) {
							for (int j = 0; j < programmiKey.size(); j++) {
								BigDecimal prgColloquioCurr = programmiKey.get(j);
								ProfilingPatto profilingPatto = factory.createPartecipanteGaranziaGiovaniPattiProfilingPatto();
								XMLGregorianCalendar datAdesioneGgGregorianCurr = null;
								String datProgramma = null;
								String dataAdesionePortale  = null;
								String datAdesionePACurr = dataAdesionePA;
								
								XMLGregorianCalendar datProgrammaGregorianIni = null;
								XMLGregorianCalendar datProgrammaGregorianFine = null;
								String datFineProgramma = null;
								
								SourceBean infoProg = PattoBean.getInfoProgramma(qExec, dc, prgColloquioCurr);
								if (infoProg != null) {
									datProgramma = (String) infoProg.getAttribute("row.datColloquio");
									if (datProgramma != null && !datProgramma.equals("")) {
										dataAdesionePortale = recuperaDataAdesionePortale(qExec, dc, cdnLavoratore, datProgramma);
										if (dataAdesionePortale != null && !dataAdesionePortale.equals("")) {
											datAdesionePACurr = dataAdesionePortale;
										}
										datProgrammaGregorianIni = toXMLGregorianCalendarDateOnly(datProgramma);																			
									}
									datFineProgramma = (String) infoProg.getAttribute("row.datFineProgramma");
									if (datFineProgramma != null && !datFineProgramma.equals("")) {
										datProgrammaGregorianFine = toXMLGregorianCalendarDateOnly(datFineProgramma);										
									}
								}
								
								/* recupero della data adesione pacchetto adulti relativa al patto Pacchetto Adulti*/
								if (datAdesionePACurr != null && !datAdesionePACurr.equals("")) {
									datAdesioneGgGregorianCurr = toXMLGregorianCalendarDateOnly(datAdesionePACurr);
									if (dataStipulaDid != null && DateUtils.compare(datAdesionePACurr, dataStipulaDid) >= 0) {
										calcolaMesiAnzianitaDidAdesionePA = true;
									}
								}
								else {
									Vector<String> params = new Vector<String>();
									params.add(datStipula);
									return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PA_NULLA, params);
								}
								
								profilingPatto.setAdesioneGgData(datAdesioneGgGregorianCurr);
								profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
								
								if (numIndiceSvantaggio != null) {
									profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
								}
								
								if (numIndiceSvantaggio2 != null) {
									profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
								}
								
								profilingPatto.setPattoGgData(datProgrammaGregorianIni);
								
								if (codCpi != null) {
									profilingPatto.setPattoCpi(codCpi.trim());
								}
								profilingPatto.setDataChiusuraPatto(datProgrammaGregorianFine);
								
								if (motivoChiusuraPatto != null) {
									profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
								}
								if (numProtocollo != null) {
									profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
								}
								if (codStatoOccupaz != null) {
									profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
								}
								
								profilingPatto.setTipoMisuraPatto(PattoBean.DB_MISURE_OVER_30);
								
								profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
								profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
								
								if (calcolaMesiAnzianitaDidAdesionePA) {
									//setto numero mesi tra la did e la data adesione pa
									Calendar inizioCalendar = new GregorianCalendar();
									inizioCalendar.setTime(format.parse(dataStipulaDid));
									Calendar adesioneCalendar = new GregorianCalendar();
									adesioneCalendar.setTime(format.parse(datAdesionePACurr));
									int diffMonth = DateUtils.monthsBetween(inizioCalendar, adesioneCalendar);
									if (diffMonth > 0) {
										profilingPatto.setNumMesiDidAdesione(new BigDecimal(diffMonth).toBigInteger());
									}
								}
								
								PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
										.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttive();

								/* recupero delle azioni relative al patto */
								try {
									ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttiveProgramma(qExec, dc, prgPattoLavoratore,
											prgColloquioCurr, profilingPatto, politicheAttive, patti, cdnLavoratore, datStipula);
									
									if (erroreSifer.errCod != 0) {
										/* riporto l'errore al chiamante */
										return erroreSifer;
									}
								} catch (Exception e) {
									_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
									return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
								}
							}
						}
					}
					
					if (isPattoGGUOver45) {
						Vector<BigDecimal>programmiKey = checkProgrammiFromCodice(qExec, dc, prgPattoLavoratore, PattoBean.DB_MISURE_OVER_45);
						if (programmiKey != null && !programmiKey.isEmpty()) {
							for (int j = 0; j < programmiKey.size(); j++) {
								BigDecimal prgColloquioCurr = programmiKey.get(j);
								ProfilingPatto profilingPatto = factory.createPartecipanteGaranziaGiovaniPattiProfilingPatto();
								XMLGregorianCalendar datAdesioneGgGregorianCurr = null;
								String datProgramma = null;
								String dataAdesionePortale  = null;
								String datAdesionePACurr = dataAdesionePA;
								
								XMLGregorianCalendar datProgrammaGregorianIni = null;																
								XMLGregorianCalendar datProgrammaGregorianFine = null;
								String datFineProgramma = null;
								
								SourceBean infoProg = PattoBean.getInfoProgramma(qExec, dc, prgColloquioCurr);
								if (infoProg != null) {
									datProgramma = (String) infoProg.getAttribute("row.datColloquio");
									if (datProgramma != null && !datProgramma.equals("")) {
										dataAdesionePortale = recuperaDataAdesionePortale(qExec, dc, cdnLavoratore, datProgramma);
										if (dataAdesionePortale != null && !dataAdesionePortale.equals("")) {
											datAdesionePACurr = dataAdesionePortale;
										}
										datProgrammaGregorianIni = toXMLGregorianCalendarDateOnly(datProgramma);			
									}
									datFineProgramma = (String) infoProg.getAttribute("row.datFineProgramma");
									if (datFineProgramma != null && !datFineProgramma.equals("")) {
										datProgrammaGregorianFine = toXMLGregorianCalendarDateOnly(datFineProgramma);										
									}
								}
								
								/* recupero della data adesione pacchetto adulti relativa al patto Pacchetto Adulti*/
								if (datAdesionePACurr != null && !datAdesionePACurr.equals("")) {
									datAdesioneGgGregorianCurr = toXMLGregorianCalendarDateOnly(datAdesionePACurr);
									if (dataStipulaDid != null && DateUtils.compare(datAdesionePACurr, dataStipulaDid) >= 0) {
										calcolaMesiAnzianitaDidAdesionePA = true;
									}
								}
								else {
									Vector<String> params = new Vector<String>();
									params.add(datStipula);
									return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PA_NULLA, params);
								}
								
								profilingPatto.setAdesioneGgData(datAdesioneGgGregorianCurr);
								profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
								
								if (numIndiceSvantaggio != null) {
									profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
								}
								
								if (numIndiceSvantaggio2 != null) {
									profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
								}
								
								profilingPatto.setPattoGgData(datProgrammaGregorianIni);
								
								if (codCpi != null) {
									profilingPatto.setPattoCpi(codCpi.trim());
								}
								profilingPatto.setDataChiusuraPatto(datProgrammaGregorianFine);
								if (motivoChiusuraPatto != null) {
									profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
								}
								if (numProtocollo != null) {
									profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
								}
								if (codStatoOccupaz != null) {
									profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
								}
								
								profilingPatto.setTipoMisuraPatto(PattoBean.DB_MISURE_OVER_45);
								
								profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
								profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
								
								if (calcolaMesiAnzianitaDidAdesionePA) {
									//setto numero mesi tra la did e la data adesione pa
									Calendar inizioCalendar = new GregorianCalendar();
									inizioCalendar.setTime(format.parse(dataStipulaDid));
									Calendar adesioneCalendar = new GregorianCalendar();
									adesioneCalendar.setTime(format.parse(datAdesionePACurr));
									int diffMonth = DateUtils.monthsBetween(inizioCalendar, adesioneCalendar);
									if (diffMonth > 0) {
										profilingPatto.setNumMesiDidAdesione(new BigDecimal(diffMonth).toBigInteger());
									}
								}
								
								PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
										.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttive();

								/* recupero delle azioni relative al patto */
								try {
									ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttiveProgramma(qExec, dc, prgPattoLavoratore,
											prgColloquioCurr, profilingPatto, politicheAttive, patti, cdnLavoratore, datStipula);
									
									if (erroreSifer.errCod != 0) {
										/* riporto l'errore al chiamante */
										return erroreSifer;
									}
								} catch (Exception e) {
									_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
									return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
								}
							}
						}
					}                                                                                                                                            
						
						if (isPattoMInat) {
							Vector<BigDecimal>programmiKey = checkProgrammiFromCodice(qExec, dc, prgPattoLavoratore, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA);
							if (programmiKey != null && !programmiKey.isEmpty()) {
								for (int j = 0; j < programmiKey.size(); j++) {
									BigDecimal prgColloquioCurr = programmiKey.get(j);
									ProfilingPatto profilingPatto = factory.createPartecipanteGaranziaGiovaniPattiProfilingPatto();
									XMLGregorianCalendar datAdesioneGgGregorianCurr = null;
									String datProgramma = null;
									String dataAdesionePortale  = null;
									String datAdesionePACurr = dataAdesionePA;
									
									XMLGregorianCalendar datProgrammaGregorianIni = null;		
									XMLGregorianCalendar datProgrammaGregorianFine = null;
									String datFineProgramma = null;								
									
									SourceBean infoProg = PattoBean.getInfoProgramma(qExec, dc, prgColloquioCurr);
									if (infoProg != null) {
										datProgramma = (String) infoProg.getAttribute("row.datColloquio");
										if (datProgramma != null && !datProgramma.equals("")) {
											dataAdesionePortale = recuperaDataAdesionePortale(qExec, dc, cdnLavoratore, datProgramma);
											if (dataAdesionePortale != null && !dataAdesionePortale.equals("")) {
												datAdesionePACurr = dataAdesionePortale;
											}
											datProgrammaGregorianIni = toXMLGregorianCalendarDateOnly(datProgramma);	
										}
										datFineProgramma = (String) infoProg.getAttribute("row.datFineProgramma");
										if (datFineProgramma != null && !datFineProgramma.equals("")) {
											datProgrammaGregorianFine = toXMLGregorianCalendarDateOnly(datFineProgramma);										
										}
									}
									
									/* recupero della data adesione pacchetto adulti relativa al patto Pacchetto Adulti*/
									if (datAdesionePACurr != null && !datAdesionePACurr.equals("")) {
										datAdesioneGgGregorianCurr = toXMLGregorianCalendarDateOnly(datAdesionePACurr);
										if (dataStipulaDid != null && DateUtils.compare(datAdesionePACurr, dataStipulaDid) >= 0) {
											calcolaMesiAnzianitaDidAdesionePA = true;
										}
									}
									else {
										Vector<String> params = new Vector<String>();
										params.add(datStipula);
										return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PA_NULLA, params);
									}
	
									profilingPatto.setAdesioneGgData(datAdesioneGgGregorianCurr);
									profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
									
									if (numIndiceSvantaggio != null) {
										profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
									}
									
									if (numIndiceSvantaggio2 != null) {
										profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
									}
									
									profilingPatto.setPattoGgData(datProgrammaGregorianIni);
									if (codCpi != null) {
										profilingPatto.setPattoCpi(codCpi.trim());
									}
									profilingPatto.setDataChiusuraPatto(datProgrammaGregorianFine);
									if (motivoChiusuraPatto != null) {
										profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
									}
									if (numProtocollo != null) {
										profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
									}
									if (codStatoOccupaz != null) {
										profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
									}
									
									profilingPatto.setTipoMisuraPatto(PattoBean.DB_MISURE_INCLUSIONE_ATTIVA);
									
									profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
									profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
									
									if (calcolaMesiAnzianitaDidAdesionePA) {
										//setto numero mesi tra la did e la data adesione pa
										Calendar inizioCalendar = new GregorianCalendar();
										inizioCalendar.setTime(format.parse(dataStipulaDid));
										Calendar adesioneCalendar = new GregorianCalendar();
										adesioneCalendar.setTime(format.parse(datAdesionePACurr));
										int diffMonth = DateUtils.monthsBetween(inizioCalendar, adesioneCalendar);
										if (diffMonth > 0) {
											profilingPatto.setNumMesiDidAdesione(new BigDecimal(diffMonth).toBigInteger());
										}
									}
									
									PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
											.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttive();
	
									/* recupero delle azioni relative al patto */
									try {
										ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttiveProgramma(qExec, dc, prgPattoLavoratore,
												prgColloquioCurr, profilingPatto, politicheAttive, patti, cdnLavoratore, datStipula);
										
										if (erroreSifer.errCod != 0) {
											/* riporto l'errore al chiamante */
											return erroreSifer;
										}
									} catch (Exception e) {
										_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
										return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
									}
								}
							}
						}
						
						//controlli azione A2R, se esiste sia associata al patto GG
						if (pattiAssociatiA2R != null && pattiAssociatiA2R.contains(prgPattoLavoratore)) {
							azPattoRegAssociata = true;
						}
					}
					// fine gestione profiling Umbria per patti Adesione Nuovo Programma
					else {
						String datAdesionePACurr = null;
						if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) || misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) ||
							isPattoGG || isPattoGGU) {
							/* recupero della data adesione GG relativa al patto Garanzia Giovani*/
							try {
								Connection connection = dc.getInternalConnection();
								String codErrore = "";
								CallableStatement stmtCreaSap = connection.prepareCall("{? = call checkAdesionePatto(?, ?) }");
			
								stmtCreaSap.registerOutParameter(1, OracleTypes.VARCHAR);
								stmtCreaSap.setBigDecimal(2, prgPattoLavoratore);
								stmtCreaSap.registerOutParameter(3, OracleTypes.VARCHAR);
								stmtCreaSap.execute();
			
								codErrore = (String) stmtCreaSap.getString(1);
								datAdesioneGg = (String) stmtCreaSap.getString(3);
			
								Vector<String> params = new Vector<String>();
								params.add(datStipula);
								if ("00".equals(codErrore)) {
									if (datAdesioneGg != null) {
										datAdesioneGgGregorian = toXMLGregorianCalendarDateOnly(datAdesioneGg);
									} else {
										return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_NULLA, params);
									}
								} else if ("01".equals(codErrore)) {
									return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_DIVERSE, params);
								} else if ("02".equals(codErrore)) {
									return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_NULLA, params);
								} else {
									return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO, params);
								}
							} catch (Exception e) {
								_logger.error("Impossibile invocare la funzione CHECKADESIONEPATTO: cdnlavoratore = "
										+ cdnLavoratore);
								throw e;
							}
						}
						else {
							/* recupero della data adesione pacchetto adulti relativa al patto Pacchetto Adulti*/
							datAdesionePACurr = dataAdesionePA;
							
							if (datAdesionePACurr != null && !datAdesionePACurr.equals("")) {
								datAdesioneGgGregorian = toXMLGregorianCalendarDateOnly(datAdesionePACurr);
								if (dataStipulaDid != null && DateUtils.compare(datAdesionePACurr, dataStipulaDid) >= 0) {
									calcolaMesiAnzianitaDidAdesionePA = true;
								}
							}
							else {
								Vector<String> params = new Vector<String>();
								params.add(datStipula);
								return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PA_NULLA, params);
							}
						}
						
						ProfilingPatto profilingPatto = factory.createPartecipanteGaranziaGiovaniPattiProfilingPatto();
		
						profilingPatto.setAdesioneGgData(datAdesioneGgGregorian);
						profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
						
						if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) || isPattoGG) {
							if (numIndiceSvantaggio != null) {
								profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
							} else if (DateUtils.compare(datStipula, "01/02/2015") < 0) {
								Vector<String> params = new Vector<String>();
								params.add(datStipula);
								return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_OLD, params);
							}
						}
						else {
							if (numIndiceSvantaggio != null) {
								profilingPatto.setIndiceSvantaggioVecchio(numIndiceSvantaggio.toBigInteger());
							}
						}
		
						if (misuraPatto.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) || isPattoGG) {
							if (numIndiceSvantaggio2 != null) {
								profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
							} else {
								Vector<String> params = new Vector<String>();
								params.add(datStipula);
								return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INDICE_SVAN_NEW, params);
							}
						}
						else {
							if (numIndiceSvantaggio2 != null) {
								profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
							}
						}
		
						profilingPatto.setPattoGgData(datStipulaGregorian);
						if (codCpi != null) {
							profilingPatto.setPattoCpi(codCpi.trim());
						}
						profilingPatto.setDataChiusuraPatto(dataChiusuraPattoGregorian);
						if (motivoChiusuraPatto != null) {
							profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
						}
						if (numProtocollo != null) {
							profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
						}
						if (codStatoOccupaz != null) {
							profilingPatto.setStatoOccupazionale(codStatoOccupaz.trim());
						}
						// controllo per flusso RER - si passa sempre MGG
						if ("8".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
							profilingPatto.setTipoMisuraPatto(PattoBean.DB_MISURE_GARANZIA_GIOVANI);
						}
						else {
							if (misuraPatto != null) {
								profilingPatto.setTipoMisuraPatto(misuraPatto);
							}
						}
						profilingPatto.setDataScadenzaPatto(dataScadenzaPattoGregorian);
						profilingPatto.setDataStipulaDid(dataStipulaDidGregorian);
						if (calcolaMesiAnzianitaDidAdesionePA) {
							//setto numero mesi tra la did e la data adesione pa
							Calendar inizioCalendar = new GregorianCalendar();
							inizioCalendar.setTime(format.parse(dataStipulaDid));
							Calendar adesioneCalendar = new GregorianCalendar();
							adesioneCalendar.setTime(format.parse(datAdesionePACurr));
							int diffMonth = DateUtils.monthsBetween(inizioCalendar, adesioneCalendar);
							if (diffMonth > 0) {
								profilingPatto.setNumMesiDidAdesione(new BigDecimal(diffMonth).toBigInteger());
							}
						}
						
						PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive = factory
								.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttive();
		
						/* recupero delle azioni relative al patto */
						try {
							SourceBean politicheAttiveBeanRows = null;
							List<DataField> param = new ArrayList<DataField>();
							param = new ArrayList<DataField>();
							param.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
							qExec.setInputParameters(param);
							qExec.setType(QueryExecutorObject.SELECT);
							/* controllo specifico per Trento */
							if ("22".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
								qExec.setStatement(QUERY_STRING_POLITICHE_ATTIVE_TN);
							}
							else {
								qExec.setStatement(QUERY_STRING_POLITICHE_ATTIVE);
							}
							politicheAttiveBeanRows = (SourceBean) qExec.exec();
							ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttive(qExec, dc, politicheAttive,
									politicheAttiveBeanRows, cdnLavoratore, datStipula);
							if (erroreSifer.errCod != 0) {
								/* riporto l'errore al chiamante */
								return erroreSifer;
							}
		
							profilingPatto.setPoliticheAttive(politicheAttive);
		
							patti.getProfilingPatto().add(profilingPatto);
							
							//controlli azione A2R, se esiste sia associata al patto GG
							if (pattiAssociatiA2R != null && pattiAssociatiA2R.contains(prgPattoLavoratore)) {
								azPattoRegAssociata = true;
							}
						} catch (Exception e) {
							_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
							return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
						}
					}
				}	
			}
		}
		
		/* controllo di avere almeno un patto o un programma inviabile associato al patto */
		if (patti.getProfilingPatto().size() == 0) {
			/* controllo specifico per Umbria */
			if ("10".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE")) ||
				Properties.TN.equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_NO_PATTI_UMBRIA);
			}
			else {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_NO_PATTI);
			}
		}
		
		
		// controllo di avere un patto associato all'azione A2R tranne per Trento 
		// check azione A02 per Trento viene eseguito nel metodo getPoliticheAttive
		if (!Properties.TN.equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
			if (pattiAssociatiA2R != null && !azPattoRegAssociata) {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_NO_PATTOGG_A2R);
			}
		}

		return new ErroreSifer(0);
	}

	private static ErroreSifer getPoliticheAttive(QueryExecutorObject qExec, DataConnection dc,
			PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive,
			SourceBean politicheAttiveBeanRows, BigDecimal cdnLavoratore, String datStipula) throws Exception {

		if (politicheAttiveBeanRows == null || politicheAttiveBeanRows.getAttributeAsVector("ROW").isEmpty()) {
			/* ho piu' di un soggetto accreditato */
			Vector<String> params = new Vector<String>();
			params.add(datStipula);
			return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_NULLA, params);
		} else if (politicheAttiveBeanRows != null) {
			Vector politicheAttiveBeanVector = politicheAttiveBeanRows.getAttributeAsVector("ROW");

			Map<BigInteger, String> politicheEsitoMap = new HashMap<BigInteger, String>();

			BigDecimal prgPercorso = null;
			String flgFormazione = null;
			BigDecimal prgColloquio = null;
			String codAzioneSifer = null;
			String codSottoAzione = null;
			BigDecimal prgAzioneRagg = null;
			String flgGruppo = null;
			BigDecimal numPartecipanti = null;
			String datColloquio = null;
			String datEffettiva = null;
			String datStimata = null;
			String codCpi = null;
			BigDecimal numYgDurataEff = null;
			BigDecimal numYgDurataMin = null;
			BigDecimal numYgDurataMax = null;
			String codTipologiaDurata = null;
			String codEsito = null;
			BigDecimal cdnUtMod = null;
			String strNomePoliticheAttive = null;
			String strCognomePoliticheAttive = null;
			String datAvvioAzione = null;
			String codMonoProgramma = null;
			String codMonoPacchetto = null;
			XMLGregorianCalendar datAvvioAzioneGregorian = null;
			boolean checkA02 = false;
			// A02 o A2R non obbligatoria per il programma di formazione di Trento
			if (!Properties.TN.equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
				checkA02 = true;
			}

			for (int i = 0; i < politicheAttiveBeanVector.size(); i++) {
				XMLGregorianCalendar datColloquioGregorian = null;
				XMLGregorianCalendar datEffettivaGregorian = null;
				XMLGregorianCalendar datStimataGregorian = null;
				SourceBean politicheAttiveBeanRow = (SourceBean) politicheAttiveBeanVector.elementAt(i);

				prgPercorso = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGPERCORSO");
				flgFormazione = (String) politicheAttiveBeanRow.getAttribute("FLGFORMAZIONE");
				prgColloquio = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGCOLLOQUIO");
				codAzioneSifer = (String) politicheAttiveBeanRow.getAttribute("CODAZIONESIFER");
				codMonoProgramma = (String) politicheAttiveBeanRow.getAttribute("CODMONOPROGRAMMA");
				codMonoPacchetto = (String) politicheAttiveBeanRow.getAttribute("CODMONOPACCHETTO");
				codSottoAzione = (String) politicheAttiveBeanRow.getAttribute("CODSOTTOAZIONE");
				prgAzioneRagg = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGAZIONERAGG");
				flgGruppo = (String) politicheAttiveBeanRow.getAttribute("FLGGRUPPO");
				numPartecipanti = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMPARTECIPANTI");
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
				codCpi = (String) politicheAttiveBeanRow.getAttribute("CODCPI");
				numYgDurataEff = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAEFF");
				numYgDurataMin = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAMIN");
				numYgDurataMax = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAMAX");
				codTipologiaDurata = (String) politicheAttiveBeanRow.getAttribute("CODTIPOLOGIADURATA");
				codEsito = (String) politicheAttiveBeanRow.getAttribute("CODESITO");
				cdnUtMod = (BigDecimal) politicheAttiveBeanRow.getAttribute("CDNUTMOD");
				strNomePoliticheAttive = (String) politicheAttiveBeanRow.getAttribute("STRNOME");
				strCognomePoliticheAttive = (String) politicheAttiveBeanRow.getAttribute("STRCOGNOME");
				datAvvioAzione = (String) politicheAttiveBeanRow.getAttribute("DATAVVIOAZIONE");
				if (datAvvioAzione != null) {
					datAvvioAzioneGregorian = toXMLGregorianCalendarDateOnly(datAvvioAzione);
				}

				/* dati obbligatori */
				if (codAzioneSifer == null || prgPercorso == null || prgColloquio == null || prgAzioneRagg == null
						|| codEsito == null | codEsito.isEmpty()) {
					// Nel caso in cui uno di questi valori sia vuoto, la
					// politica attiva (azione SIL), non viene estratta.
					continue;
				}
				
				if (!checkA02) {
					if (codMonoProgramma != null && codMonoProgramma.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
						if ( (codAzioneSifer.equalsIgnoreCase("A02") || codAzioneSifer.equalsIgnoreCase("A2R")) && 
						     (codEsito.equalsIgnoreCase("FC")) && (codMonoPacchetto == null || !codMonoPacchetto.equalsIgnoreCase("OR")) ) {
							checkA02 = true;	
						}
					}
					else {
						// controllo non applicato per programmi di formazione di Trento
						checkA02 = true;
					}
				}
				
				PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva politicaAttiva = factory
						.createPartecipanteGaranziaGiovaniPattiProfilingPattoPoliticheAttivePoliticaAttiva();
				politicaAttiva.setDataColloquio(datColloquioGregorian);
				politicaAttiva.setDataFineAttivita(datEffettivaGregorian);
				politicaAttiva.setDataStimataFineAttivita(datStimataGregorian);
				if (numYgDurataEff != null && (numYgDurataEff.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataEffettiva(numYgDurataEff.toBigInteger());
				}
				if (numYgDurataMax != null && (numYgDurataMax.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataMassima(numYgDurataMax.toBigInteger());
				}
				if (numYgDurataMin != null && (numYgDurataMin.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataMinima(numYgDurataMin.toBigInteger());
				}
				if (codCpi != null) {
					politicaAttiva.setCodiceEnteTitolare(codCpi.trim());
				}
				if (codEsito != null) {
					politicaAttiva.setEsito(codEsito.trim());
				}
				if (flgGruppo != null) {
					politicaAttiva.setFlgGruppo(flgGruppo.trim());
				}
				if (prgAzioneRagg != null) {
					politicaAttiva.setMisura(prgAzioneRagg.toBigInteger());
				}
				if (numPartecipanti != null && !numPartecipanti.equals(BigInteger.ZERO)) {
					politicaAttiva.setNumPartecipanti(numPartecipanti.toBigInteger());
				}
				if (prgColloquio != null) {
					politicaAttiva.setPrgColloquio(prgColloquio.toBigInteger());
				}
				if (prgPercorso != null) {
					politicaAttiva.setPrgPercorso(prgPercorso.toBigInteger());
				}
				if (codCpi != null) {
					politicaAttiva.setCpiUtente(codCpi.trim());
				}
				if (cdnUtMod != null) {
					politicaAttiva.setIdUtente(cdnUtMod.toBigInteger());
				}
				if (strCognomePoliticheAttive != null) {
					politicaAttiva.setCognomeUtente(strCognomePoliticheAttive.trim());
				}
				if (strNomePoliticheAttive != null) {
					politicaAttiva.setNomeUtente(strNomePoliticheAttive.trim());
				}
				if (codAzioneSifer != null) {
					politicaAttiva.setTipoAttivita(codAzioneSifer.trim());
				}
				if (codSottoAzione != null) {
					politicaAttiva.setSottoAttivita(codSottoAzione.trim());
				}
				if (codTipologiaDurata != null) {
					politicaAttiva.setTipologiaDurata(codTipologiaDurata.trim());
				}
				politicaAttiva.setDataAvvioAttivita(datAvvioAzioneGregorian);

				/*
				 * dati del soggetto accreditato (per ora utilizzati solo da
				 * TRENTO)
				 */
				if (cdnUtMod != null) {
					SourceBean politicheAttiveSoggAccBeanRows = null;
					try {
						List<DataField> param = new ArrayList<DataField>();
						param.add(dc.createDataField("", Types.BIGINT, cdnUtMod));
						qExec.setInputParameters(param);
						qExec.setType(QueryExecutorObject.SELECT);
						qExec.setStatement(QUERY_STRING_POLITICHEATTIVE_SOGGACC);
						politicheAttiveSoggAccBeanRows = (SourceBean) qExec.exec();

						if (politicheAttiveSoggAccBeanRows != null) {
							String strCodiceFiscale = null;
							String strDenominazione = null;

							strCodiceFiscale = (String) politicheAttiveSoggAccBeanRows
									.getAttribute("ROW.STRCODICEFISCALE");
							strDenominazione = (String) politicheAttiveSoggAccBeanRows
									.getAttribute("ROW.STRDENOMINAZIONE");

							if (strCodiceFiscale != null) {
								politicaAttiva.setCodiceFiscaleUtente(strCodiceFiscale.trim());
							}

							if (strDenominazione != null) {
								politicaAttiva.setUfficioUtente(strDenominazione.trim());
							}
						}

						if (politicaAttiva.getCodiceFiscaleUtente() != null) {
							SourceBean countProfilatureSoggAccBeanRows = null;
							param = new ArrayList<DataField>();
							param.add(dc.createDataField("", Types.BIGINT, politicaAttiva.getIdUtente()));
							qExec.setInputParameters(param);
							qExec.setType(QueryExecutorObject.SELECT);
							qExec.setStatement(QUERY_STRING_COUNT_PROFILATURE_SOGGACC);
							countProfilatureSoggAccBeanRows = (SourceBean) qExec.exec();

							BigDecimal count = null;
							if (countProfilatureSoggAccBeanRows != null) {
								count = (BigDecimal) countProfilatureSoggAccBeanRows.getAttribute("ROW.COUNTP");
								if (count != null && count.compareTo(new BigDecimal(1)) > 0) {
									/* ho piu' di un soggetto accreditato */
									Vector<String> params = new Vector<String>();
									params.add(datStipula);
									return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_POLITICHE_ATTIVE_SOGGACC, params);
								}
							}
						}
					} catch (Exception e) {
						_logger.error("Impossibile recuperare i dati relativi alle politiche attive (secondo le specifiche di Trento): "
								+ cdnLavoratore);
						throw e;
					}
				}

				politicheEsitoMap.put(prgPercorso.toBigInteger(), flgFormazione);
				politicheAttive.getPoliticaAttiva().add(politicaAttiva);
			}

			/* controllo specifico RER per azioni B06 e C06 */
			if ("8".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
				SourceBean dataRifRows = null;
				qExec.setInputParameters(null);
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setStatement(QUERY_STRING_DATA_RIF_CONTROLLO_RER);
				dataRifRows = (SourceBean) qExec.exec();
				String dataRif = (String) dataRifRows.getAttribute("ROW.STRVALORE");

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Calendar datStipulaCal = Calendar.getInstance();
				datStipulaCal.setTime(sdf.parse(datStipula));
				Calendar datInstallazioneCal = Calendar.getInstance();
				datInstallazioneCal.setTime(sdf.parse(dataRif));
				if (!datStipulaCal.before(datInstallazioneCal)) {
					boolean b06orc06 = false;
					boolean sottoAzione = false;
					for (it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva politicaAttiva : politicheAttive
							.getPoliticaAttiva()) {
						if ("B06".equals(politicaAttiva.getTipoAttivita())
								|| "C06".equals(politicaAttiva.getTipoAttivita())) {
							b06orc06 = true;
						}
						if ("A03FC".equals(politicaAttiva.getSottoAttivita())
								&& "S".equals(politicheEsitoMap.get(politicaAttiva.getPrgPercorso()))) {
							sottoAzione = true;
						}
					}
					if (b06orc06 && !sottoAzione) {
						Vector<String> params = new Vector<String>();
						params.add(datStipula);
						return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_RER_SOTTOAZIONE, params);
					}
				}
			}
			else {
				/* controllo specifico UMBRIA per azione C06 */
				if ("10".equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
					int countC06ProAvv = 0;
					int countC07ProAvv = 0;
					int countC05ProAvv = 0;
					int countC02ProAvv = 0;
					String dateColloquioC06 = "";
					String dateColloquioC02 = "";
					String dateColloquioC05 = "";
					String dateColloquioC07 = "";
					for (it.eng.sil.coop.webservices.siferPartecipanteGG.input.PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva politicaAttiva : politicheAttive
							.getPoliticaAttiva()) {
						if ( ("C06".equalsIgnoreCase(politicaAttiva.getTipoAttivita())) && 
							 ("PRO".equalsIgnoreCase(politicaAttiva.getEsito()) || "AVV".equalsIgnoreCase(politicaAttiva.getEsito())) ) {
							String datColl = DateUtils.formatXMLGregorian(politicaAttiva.getDataColloquio());
							if (datColl != null) {
								if (dateColloquioC06.equals("")) {
									dateColloquioC06 = datColl;
								}
								else {
									dateColloquioC06 = dateColloquioC06 + ", " + datColl;
								}
							}
							countC06ProAvv = countC06ProAvv + 1;
						}
						else {
							if ( ("C07".equalsIgnoreCase(politicaAttiva.getTipoAttivita())) && 
								 ("PRO".equalsIgnoreCase(politicaAttiva.getEsito()) || "AVV".equalsIgnoreCase(politicaAttiva.getEsito())) ) {
								String datColl = DateUtils.formatXMLGregorian(politicaAttiva.getDataColloquio());
								if (datColl != null) {
									if (dateColloquioC07.equals("")) {
										dateColloquioC07 = datColl;
									}
									else {
										dateColloquioC07 = dateColloquioC07 + ", " + datColl;
									}
								}
								countC07ProAvv = countC07ProAvv + 1;
							}
							else {
								if ( ("C05".equalsIgnoreCase(politicaAttiva.getTipoAttivita())) && 
									 ("PRO".equalsIgnoreCase(politicaAttiva.getEsito()) || "AVV".equalsIgnoreCase(politicaAttiva.getEsito())) ) {
									String datColl = DateUtils.formatXMLGregorian(politicaAttiva.getDataColloquio());
									if (datColl != null) {
										if (dateColloquioC05.equals("")) {
											dateColloquioC05 = datColl;
										}
										else {
											dateColloquioC05 = dateColloquioC05 + ", " + datColl;
										}
									}
									countC05ProAvv = countC05ProAvv + 1;
								}
								else {
									if ( ("C02".equalsIgnoreCase(politicaAttiva.getTipoAttivita())) && 
										 ("PRO".equalsIgnoreCase(politicaAttiva.getEsito()) || "AVV".equalsIgnoreCase(politicaAttiva.getEsito())) ) {
										String datColl = DateUtils.formatXMLGregorian(politicaAttiva.getDataColloquio());
										if (datColl != null) {
											if (dateColloquioC02.equals("")) {
												dateColloquioC02 = datColl;
											}
											else {
												dateColloquioC02 = dateColloquioC02 + ", " + datColl;
											}
										}
										countC02ProAvv = countC02ProAvv + 1;
									}
								}
							}
						}
					}
					if (countC06ProAvv > 1) {
						Vector<String> params = new Vector<String>();
						params.add(dateColloquioC06);
						return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_AZIONI_C06_PRO_AVV, params);
					}
					if (countC07ProAvv > 1) {
						Vector<String> params = new Vector<String>();
						params.add(dateColloquioC07);
						return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_AZIONI_C07_PRO_AVV, params);
					}
					if (countC05ProAvv > 1) {
						Vector<String> params = new Vector<String>();
						params.add(dateColloquioC05);
						return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_AZIONI_C05_PRO_AVV, params);
					}
					if (countC02ProAvv > 1) {
						Vector<String> params = new Vector<String>();
						params.add(dateColloquioC02);
						return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_AZIONI_C02_PRO_AVV, params);
					}
				}
				else {
					if (Properties.TN.equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
						if (!checkA02) {
							return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_NO_PATTO_PROGRAMMA_GG_A2R);
						}
					}
				}
			}
		}
		return new ErroreSifer(0);
	}

	private static ErroreSifer getComunicazioniObbligatorie(ComunicazioniObbligatorie comunicazioniObbligatorie,
			SourceBean movimentazioneBeanRows) throws DatatypeConfigurationException, ParseException {
		if (movimentazioneBeanRows != null) {
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
			String codQualificaSrqOriginale = null;
			String indirizzoLavoro = null;
			String comuneLavoro = null;
			BigDecimal prgMovimento = null;
			BigDecimal prgMovimentoRett = null;
			String dataInvioCo = null;
			String dataInvioCoOriginale = null;

			for (int i = 0; i < movimentazioneBeanVector.size(); i++) {
				XMLGregorianCalendar datInizioMovGregorian = null;
				XMLGregorianCalendar datFinePfGregorian = null;
				XMLGregorianCalendar datFineMovGregorian = null;

				SourceBean movimentazioneBeanRow = (SourceBean) movimentazioneBeanVector.elementAt(i);

				strCodiceFiscaleDatore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALE");
				strRagioneSociale = (String) movimentazioneBeanRow.getAttribute("STRRAGIONESOCIALE");
				codComunicazione = (String) movimentazioneBeanRow.getAttribute("CODCOMUNICAZIONE");
				strIndirizzo = (String) movimentazioneBeanRow.getAttribute("STRINDIRIZZO");
				codAzAteco = (String) movimentazioneBeanRow.getAttribute("CODAZATECO");
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
				codQualificaSrqOriginale = (String) movimentazioneBeanRow.getAttribute("CODQUALIFICASRQORIGINALE");
				prgMovimento = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTO");
				prgMovimentoRett = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTORETT");
				dataInvioCo = (String) movimentazioneBeanRow.getAttribute("DATCOMUNICAZ");
				dataInvioCoOriginale = (String) movimentazioneBeanRow.getAttribute("DATCOMUNICAZORIGINALE");

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

				/* dati obbligatori */
				if (codComunicazione == null || codComunicazione.isEmpty() || strCodiceFiscaleDatore == null
						|| strCodiceFiscaleDatore.isEmpty() || codAzAteco == null || codAzAteco.isEmpty()
						|| datInizioMov == null || datInizioMov.isEmpty()) {
					// Nel caso in cui uno di questi valori sia vuoto, la
					// comunicazione obbligatoria, non viene estratta.
					continue;
				}

				ComunicazioneObbligatoria comunicazioniObbligatoria = factory
						.createPartecipanteGaranziaGiovaniComunicazioniObbligatorieComunicazioneObbligatoria();
				if (codComunicazione != null) {
					comunicazioniObbligatoria.setCodiceComunicazioneAvviamento(codComunicazione.trim());
				}
				comunicazioniObbligatoria.setConvenzioneData(datConvenzione);
				if (numConvenzione != null) {
					comunicazioniObbligatoria.setConvenzioneNumero(numConvenzione.trim());
				}
				comunicazioniObbligatoria.setDataFine(datFineMovGregorian);
				comunicazioniObbligatoria.setDataFinePeriodoFormativo(datFinePfGregorian);
				comunicazioniObbligatoria.setDataInizio(datInizioMovGregorian);
				if (strCodiceFiscaleDatore != null) {
					strCodiceFiscaleDatore = strCodiceFiscaleDatore.trim();
					strCodiceFiscaleDatore = strCodiceFiscaleDatore.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroCodiceFiscale(strCodiceFiscaleDatore);
				}
				if (strRagioneSociale != null) {
					strRagioneSociale = strRagioneSociale.trim();
					strRagioneSociale = strRagioneSociale.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroDenominazione(strRagioneSociale);
				}
				if (strIndirizzo != null) {
					strIndirizzo = strIndirizzo.trim();
					strIndirizzo = strIndirizzo.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strIndirizzo);
				}
				if (codAzAteco != null) {
					codAzAteco = codAzAteco.trim();
					codAzAteco = codAzAteco.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codAzAteco);
				}
				if (flgLavoroAgr != null) {
					flgLavoroAgr = flgLavoroAgr.trim();
					flgLavoroAgr = flgLavoroAgr.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(flgLavoroAgr);
				}
				if (flgLavoroStagional != null) {
					flgLavoroStagional = flgLavoroStagional.trim();
					flgLavoroStagional = flgLavoroStagional.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(flgLavoroStagional);
				}
				if (mansioneDesc != null) {
					mansioneDesc = mansioneDesc.trim();
					mansioneDesc = mansioneDesc.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(mansioneDesc);
				}
				if (codOrario != null) {
					codOrario = codOrario.trim();
					codOrario = codOrario.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codOrario);
				}
				if (codMansioneMin != null) {
					codMansioneMin = codMansioneMin.trim();
					codMansioneMin = codMansioneMin.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codMansioneMin);
				}
				if (codQualificaSrq != null) {
					codQualificaSrq = codQualificaSrq.trim();
					codQualificaSrq = codQualificaSrq.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codQualificaSrq);
				}
				if (codQualificaSrqOriginale != null) {
					codQualificaSrqOriginale = codQualificaSrqOriginale.trim();
					codQualificaSrqOriginale = codQualificaSrqOriginale.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codQualificaSrqOriginale);
				}
				if (comuneLavoro != null) {
					comuneLavoro = comuneLavoro.trim();
					comuneLavoro = comuneLavoro.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(comuneLavoro);
				}
				if (indirizzoLavoro != null) {
					indirizzoLavoro = indirizzoLavoro.trim();
					indirizzoLavoro = indirizzoLavoro.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(indirizzoLavoro);
				}
				if (codContratto != null) {
					codContratto = codContratto.trim();
					codContratto = codContratto.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codContratto);
				}
				if (codTipoEntePromotore != null) {
					codTipoEntePromotore = codTipoEntePromotore.trim();
					codTipoEntePromotore = codTipoEntePromotore.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codTipoEntePromotore);
				}
				if (codCategoriaTir != null) {
					codCategoriaTir = codCategoriaTir.trim();
					codCategoriaTir = codCategoriaTir.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codCategoriaTir);
				}
				if (codTipologiaTir != null) {
					codTipologiaTir = codTipologiaTir.trim();
					codTipologiaTir = codTipologiaTir.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codTipologiaTir);
				}
				if (strCodiceFiscaleTutore != null) {
					strCodiceFiscaleTutore = strCodiceFiscaleTutore.trim();
					strCodiceFiscaleTutore = strCodiceFiscaleTutore.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strCodiceFiscaleTutore);
				}
				if (strCognomeTutore != null) {
					strCognomeTutore = strCognomeTutore.trim();
					strCognomeTutore = strCognomeTutore.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strCognomeTutore);
				}
				if (strNomeTutore != null) {
					strNomeTutore = strNomeTutore.trim();
					strNomeTutore = strNomeTutore.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strNomeTutore);
				}
				if (azUtiCf != null) {
					azUtiCf = azUtiCf.trim();
					azUtiCf = azUtiCf.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(azUtiCf);
				}
				if (azUtiDenom != null) {
					azUtiDenom = azUtiDenom.trim();
					azUtiDenom = azUtiDenom.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(azUtiDenom);
				}
				if (azUtiInd != null) {
					azUtiInd = azUtiInd.trim();
					azUtiInd = azUtiInd.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(azUtiInd);
				}
				if (codAzutiAteco != null) {
					codAzutiAteco = codAzutiAteco.trim();
					codAzutiAteco = codAzutiAteco.replaceAll("\n|\r", "");
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(codAzutiAteco);
				}
				comunicazioniObbligatoria.setPrgMovimentoSil(prgMovimento.toBigInteger());
				comunicazioniObbligatoria.setPrgMovimentoSilOriginario(prgMovimentoRett.toBigInteger());
				comunicazioniObbligatoria.setDataInvioCo(dataInvioCo);
				comunicazioniObbligatoria.setDataInvioCoOriginale(dataInvioCoOriginale);

				comunicazioniObbligatorie.getComunicazioneObbligatoria().add(comunicazioniObbligatoria);
			}
		}

		return new ErroreSifer(0);
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

	private static XMLGregorianCalendar toXMLGregorianCalendarYearOnly(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance()
				.newXMLGregorianCalendarDate(gc.get(Calendar.YEAR), DatatypeConstants.FIELD_UNDEFINED,
						DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}
	
	private static String recuperaDataAdesionePA(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore) {
		SourceBean adesioneRows = null;
		String dataAdesionePA = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_STRING_DATA_ADESIONE_PA);
		adesioneRows = (SourceBean) qExec.exec();
		
		if (adesioneRows != null) {
			dataAdesionePA = (String)adesioneRows.getAttribute("ROW.DATADESIONEPA");
		}
		
		return dataAdesionePA;
	}
	
	private static String recuperaDataAdesionePortale(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore, String dataProgramma) {
		SourceBean adesioneRows = null;
		String dataAdesione = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
		param.add(dc.createDataField("", Types.VARCHAR, dataProgramma));
		param.add(dc.createDataField("", Types.VARCHAR, PattoBean.BANDO_UMBRIA_ATTIVA));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(PattoBean.QUERY_STRING_DATA_ADESIONE_PORTALE);
		adesioneRows = (SourceBean) qExec.exec();
		
		if (adesioneRows != null) {
			dataAdesione = (String)adesioneRows.getAttribute("ROW.SDATADESIONE");
		}
		
		return dataAdesione;
	}
	
	private static BigDecimal contaAzioniGUNonAssociate(QueryExecutorObject qExec, DataConnection dc, BigDecimal cdnLavoratore) {
		SourceBean azRows = null;
		BigDecimal contatore = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_STRING_POLITICHE_ATTIVE_GU_NON_ASSOCIATE);
		azRows = (SourceBean) qExec.exec();
		
		if (azRows != null) {
			contatore = (BigDecimal)azRows.getAttribute("ROW.NUMAZIONIGUNONASSOCIATE");
		}
		
		return contatore;
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

	private static boolean checkEmail(String email) {
		if (email != null) {
			Matcher emailMatcher = XMLValidator.emailRegEx.matcher(email.trim());
			return emailMatcher.matches();
		} else {
			return false;
		}
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
		psUrl.setString(2, "" + (erroreSifer.errCod));
		psUrl.setString(3, err);
		psUrl.setString(4, erroreSifer.getStrXMLInviato());
		// execute insert SQL stetement
		psUrl.executeUpdate();
		psUrl.close();
	}

	public static String getEndpointUrl(Connection conn) throws Exception {
		PreparedStatement psUrl = null;
		String endPoint = null;
		ResultSet rsUrl = null;

		String statementUrl = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT WHERE strName = 'PartecipanteGG'";
		psUrl = conn.prepareStatement(statementUrl);

		rsUrl = psUrl.executeQuery();
		if (rsUrl.next()) {
			endPoint = rsUrl.getString("strUrl");
		}

		rsUrl.close();
		psUrl.close();

		return endPoint;
	}
	
	public static Vector<String> checkProgrammiPatto (QueryExecutorObject qExec, DataConnection dc, BigDecimal prgPattoLavoratore) throws Exception {
		SourceBean programmiBeanRows = null;
		List<DataField> paramProg = new ArrayList<DataField>();
		paramProg = new ArrayList<DataField>();
		paramProg.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
		qExec.setInputParameters(paramProg);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_PROGRAMMI_PATTO);
		programmiBeanRows = (SourceBean) qExec.exec();
		
		Vector<String> programmi = new Vector<String>();
		
		if (programmiBeanRows != null && !programmiBeanRows.getAttributeAsVector("ROW").isEmpty()) {
			Vector programmiVector = programmiBeanRows.getAttributeAsVector("ROW");
			for (int i = 0; i < programmiVector.size(); i++) {
				SourceBean programma = (SourceBean) programmiVector.elementAt(i);
				String servizio = programma.getAttribute("codmonoprogramma")!=null?programma.getAttribute("codmonoprogramma").toString():"";
				if (!servizio.equals("")) {
					programmi.add(servizio);
				}
			}
		}
		return programmi;
	}
	
	public static Vector<BigDecimal> checkProgrammiFromCodice (QueryExecutorObject qExec, DataConnection dc, BigDecimal prgPattoLavoratore, 
			String codMonoProgramma) throws Exception {
		SourceBean programmiBeanRows = null;
		List<DataField> paramProg = new ArrayList<DataField>();
		paramProg = new ArrayList<DataField>();
		paramProg.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
		paramProg.add(dc.createDataField("", Types.VARCHAR, codMonoProgramma));
		qExec.setInputParameters(paramProg);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_PROGRAMMI_PATTO_CODMONOPROG);
		programmiBeanRows = (SourceBean) qExec.exec();
		
		Vector<BigDecimal> programmiKey = new Vector<BigDecimal>();
		
		if (programmiBeanRows != null && !programmiBeanRows.getAttributeAsVector("ROW").isEmpty()) {
			Vector programmiVector = programmiBeanRows.getAttributeAsVector("ROW");
			for (int i = 0; i < programmiVector.size(); i++) {
				SourceBean programma = (SourceBean) programmiVector.elementAt(i);
				BigDecimal prgColloquio = (BigDecimal)programma.getAttribute("prgcolloquio");
				if (prgColloquio != null) {
					programmiKey.add(prgColloquio);
				}
			}
		}
		return programmiKey;
	}
	
	private static ErroreSifer getProfilingProgrammaDataAdesione(QueryExecutorObject qExec, DataConnection dc, BigDecimal prgColloquioCurr,
		BigDecimal prgPattoLavoratore, ProfilingPatto profilingPatto, BigDecimal cdnLavoratore, String datStipula) throws Exception {
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
					return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PROGRAMMA_NULLA, params);
				}
			} else if ("01".equals(codErrore)) {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PROGRAMMA_DIVERSE, params);
			} else if ("02".equals(codErrore)) {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_PROGRAMMA_NULLA, params);
			} else {
				return new ErroreSifer(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO, params);
			}
		} catch (Exception e) {
			_logger.error("Impossibile invocare la funzione CHECKADESIONEPROGRAMMAPATTO: cdnlavoratore = " + cdnLavoratore);
			throw e;
		}
		
		profilingPatto.setAdesioneGgData(datAdesioneGgGregorianCurr);
		return new ErroreSifer(0);
	}
	
	private static ErroreSifer getPoliticheAttiveProgramma(QueryExecutorObject qExec, DataConnection dc, BigDecimal prgPattoLavoratore,
			BigDecimal prgColloquioCurr, ProfilingPatto profilingPatto, PartecipanteGaranziaGiovani.Patti.ProfilingPatto.PoliticheAttive politicheAttive,
			Patti patti, BigDecimal cdnLavoratore, String datStipula) throws Exception {
		try {
			SourceBean politicheAttiveBeanRows = null;
			List<DataField> param = new ArrayList<DataField>();
			param = new ArrayList<DataField>();
			param.add(dc.createDataField("", Types.BIGINT, prgPattoLavoratore));
			param.add(dc.createDataField("", Types.BIGINT, prgColloquioCurr));
			qExec.setInputParameters(param);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(QUERY_STRING_POLITICHE_ATTIVE_PROGRAMMA);
			
			politicheAttiveBeanRows = (SourceBean) qExec.exec();
			ErroreSifer erroreSifer = PartecipanteGGUtils.getPoliticheAttive(qExec, dc, politicheAttive, politicheAttiveBeanRows, cdnLavoratore, datStipula);
			if (erroreSifer.errCod != 0) {
				/* riporto l'errore al chiamante */
				return erroreSifer;
			}
			
			if (Properties.TN.equals(regioneBeanRows.getAttribute("ROW.CODREGIONE"))) {
				if (politicheAttive.getPoliticaAttiva().size() > 0) {
					profilingPatto.setPoliticheAttive(politicheAttive);
					patti.getProfilingPatto().add(profilingPatto);
				}
			}
			else {
				profilingPatto.setPoliticheAttive(politicheAttive);
				patti.getProfilingPatto().add(profilingPatto);
			}
			
			return erroreSifer;
		} catch (Exception e) {
			_logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
			return new ErroreSifer(MessageCodes.YG.ERR_EXEC_WS_YG);
		}
	}
	
}
