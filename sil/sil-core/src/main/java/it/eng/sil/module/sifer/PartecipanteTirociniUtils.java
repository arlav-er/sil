package it.eng.sil.module.sifer;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
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
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.axis.types.Token;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.PortTypeProxy;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.Request;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.input.ObjectFactory;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.input.PartecipanteTirocini;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.input.PartecipanteTirocini.ComunicazioniObbligatorie;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.input.PartecipanteTirocini.ComunicazioniObbligatorie.ComunicazioneObbligatoria;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.input.PartecipanteTirocini.Partecipante;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.input.PartecipanteTirocini.Partecipante.Accorpamento;
import it.eng.sil.coop.webservices.siferPartecipanteTirocini.output.RegistraPartecipanteTirociniResponse;
import it.eng.sil.util.xml.XMLValidator;

public class PartecipanteTirociniUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(PartecipanteTirociniUtils.class.getName());

	private static String QUERY_STRING_ACCORPATO = "SELECT ac.cdnlavoratore, ac.cdnlavoratoreaccorpato, ac.strcodicefiscaleaccorpato, to_char(ac.dtmins, 'dd/mm/yyyy') AS dtmins FROM an_lavoratore_accorpa ac  WHERE ac.cdnlavoratore = ? ";
	private static String QUERY_STRING_DATA_TITOLO_STUDIO = "SELECT prst.numanno FROM pr_studio prst WHERE prst.cdnLavoratore = ? AND prst.codtitolo = ?";

	private static ObjectFactory factory = new ObjectFactory();

	public static final String WS_NOME_METODO_PARTECIPANTETIROCINI = "registraPartecipanteTirocini";
	public static final String END_POINT_NAME_PARTECIPANTETIROCINI = "PartecipanteTirocini";

	private static final String INPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "PartecipanteTirocini" + File.separator + "PartecipanteTirocini.xsd";

	private static final String OUTPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "PartecipanteTirocini" + File.separator
			+ "registraPartecipanteTirocini_response.xsd";

	public static int sendPartecipanteTirocini(String cdnLavoratore, SourceBean anLavBeanRows,
			SourceBean permSoggBeanRows, SourceBean codProvinciaBeanRows, SourceBean titStudioBeanRows,
			SourceBean movimentazioneBeanRows, SourceBean wsRows) throws Exception {

		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		DataResult dr = null;

		try {

			qExec = getQueryExecutorObject();
			dc = qExec.getDataConnection();
			_logger.error("PartecipanteTirociniUtils connessione istanziata");

			PartecipanteTirocini msg = factory.createPartecipanteTirocini();
			Partecipante partecipante = factory.createPartecipanteTirociniPartecipante();
			ComunicazioniObbligatorie comunicazioniObbligatorie = factory
					.createPartecipanteTirociniComunicazioniObbligatorie();

			int partecipanteErrCod = PartecipanteTirociniUtils.getPartecipante(qExec, dc, cdnLavoratore, partecipante,
					anLavBeanRows, permSoggBeanRows, codProvinciaBeanRows, titStudioBeanRows);
			switch (partecipanteErrCod) {
			case MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE:
				/* mancanza di dati obbligatori */
				_logger.error(
						"PartecipanteTirocini: Invio a formazione non effettuato: dati obbligatori mancanti. cdnlavoratore = "
								+ cdnLavoratore);
				return partecipanteErrCod;
			case MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_EMAIL:
				/* formato email errato */
				_logger.error(
						"PartecipanteTirocini: Invio a formazione non effettuato: formato e-mail errato. cdnlavoratore = "
								+ cdnLavoratore);
				return partecipanteErrCod;
			}
			partecipante.getAccorpamento().addAll(PartecipanteTirociniUtils.getAccorpamenti(qExec, dc, cdnLavoratore));

			PartecipanteTirociniUtils.getComunicazioniObbligatorie(comunicazioniObbligatorie, movimentazioneBeanRows);

			msg.setPartecipante(partecipante);
			msg.setComunicazioniObbligatorie(comunicazioniObbligatorie);

			String input = PartecipanteTirociniUtils.convertInputToString(msg);

			_logger.debug("SIL -> SIFER: cdnLavoratore " + cdnLavoratore + " xml input\n" + input);

			/* validazione xsd dell'input */
			if (!PartecipanteTirociniUtils.validazioneXml(input, INPUT_XSD)) {
				int errCod = MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_INPUT;
				_logger.error("PartecipanteTirocini: Errore in validazione input. cdnlavoratore = " + cdnLavoratore);
				return errCod;
			}

			/* Invocazione del servizio */
			PortTypeProxy proxy = new PortTypeProxy();

			proxy.setEndpoint(getEndpointUrl(dc.getInternalConnection()));

			String strUserId = null;
			String strPassword = null;
			if (wsRows != null) {
				strUserId = (String) wsRows.getAttribute("ROW.STRUSERID");
				strPassword = (String) wsRows.getAttribute("ROW.STRPASSWORD");
			}
			Token token = new Token(strPassword);
			Request request = new Request(strUserId, token, WS_NOME_METODO_PARTECIPANTETIROCINI, input);
			String output = "";
			try {
				output = proxy.requestService(request);
			} // try
			catch (Exception ex) {
				_logger.error(ex);
				int errCod = MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE;
				return errCod;
			}
			_logger.debug("SIL -> SIFER: cdnLavoratore " + cdnLavoratore + " xml output\n" + output);

			/* validazione xsd dell'output */
			if (!PartecipanteTirociniUtils.validazioneXml(output, OUTPUT_XSD)) {
				int errCod = MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_OUTPUT;
				_logger.error("PartecipanteTirocini: Errore in validazione output. cdnlavoratore = " + cdnLavoratore);
				return errCod;
			} else {
				RegistraPartecipanteTirociniResponse xmlOutput = PartecipanteTirociniUtils
						.convertRegistraPartecipanteToString(output);
				String codiceRisposta = xmlOutput.getResponseCodice();
				if (!"0".equalsIgnoreCase(codiceRisposta)) {
					int errCod = MessageCodes.YG.ERR_KO_WS_PARTECIPANTE_GG_OUTPUT;
					_logger.error("PartecipanteTirocini: Errore codice risposta SIFER. codice=" + codiceRisposta
							+ " - cdnlavoratore = " + cdnLavoratore);
					return errCod;
				}
			}
		} catch (Throwable e) {
			_logger.error("PartecipanteTirociniUtils errore: " + e);
		} finally {
			_logger.debug("PartecipanteGGUtils connessione rilasciata");
			Utils.releaseResources(dc, null, dr);
		}

		return 0;
	}

	/* Converte l'oggetto che rappresenta il messaggio di input in xml */
	private static String convertInputToString(PartecipanteTirocini msg) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(PartecipanteTirocini.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(msg, writer);
		String xml = writer.getBuffer().toString();
		return xml;
	}

	private static RegistraPartecipanteTirociniResponse convertRegistraPartecipanteToString(String xmlOutput)
			throws JAXBException, SAXException {
		RegistraPartecipanteTirociniResponse responseOutput = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(RegistraPartecipanteTirociniResponse.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		responseOutput = (RegistraPartecipanteTirociniResponse) jaxbUnmarshaller.unmarshal(new StringReader(xmlOutput));
		return responseOutput;
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

	private static int getPartecipante(QueryExecutorObject qExec, DataConnection dc, String cdnLavoratore,
			Partecipante partecipante, SourceBean anLavBeanRows, SourceBean permSoggBeanRows,
			SourceBean codProvinciaBeanRows, SourceBean titStudioBeanRows)
			throws DatatypeConfigurationException, ParseException {

		BigDecimal cdnLav = new BigDecimal(cdnLavoratore);
		_logger.error("cdnlavoratore: " + cdnLavoratore);

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
		SourceBean prStudioBeanRows = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLav));
		param.add(dc.createDataField("CODTITOLO", Types.VARCHAR, codTitolo));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_STRING_DATA_TITOLO_STUDIO);
		prStudioBeanRows = (SourceBean) qExec.exec();

		if (prStudioBeanRows != null) {
			if (prStudioBeanRows.getAttributeAsVector("ROW").size() == 1) {
				Object numAnno = prStudioBeanRows.getAttribute("ROW.NUMANNO");
				if (numAnno != null) {
					Calendar c = new GregorianCalendar();
					c.set(Calendar.YEAR, ((BigDecimal) numAnno).toBigInteger().intValue());
					String numAnnoString = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
					numAnnoGregorian = toXMLGregorianCalendarYearOnly(numAnnoString);
				}
			}
		}

		/* dati obbligatori */
		if (strCodiceFiscale == null || strCodiceFiscale.isEmpty() || codProvincia == null || codProvincia.isEmpty()
				|| strCognome == null || strCognome.isEmpty() || strNome == null || strNome.isEmpty()
				|| strSesso == null || strSesso.isEmpty() || datNasc == null || datNasc.isEmpty() || codComNas == null
				|| codComNas.isEmpty()) {

			return MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE;
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
			}
			// else {
			// return MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_EMAIL;
			// }
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
		if (numAnnoGregorian != null) {
			partecipante.setTitoloStudioAnnoConseguimento(numAnnoGregorian);
		}

		return 0;
	}

	public static List<Accorpamento> getAccorpamenti(QueryExecutorObject qExec, DataConnection dc, String cdnLavoratore)
			throws Exception {
		List<Accorpamento> accorpamentoList = new ArrayList<PartecipanteTirocini.Partecipante.Accorpamento>();

		SourceBean anLavAccBeanRows = null;
		try {
			List<DataField> param = new ArrayList<DataField>();
			param.add(dc.createDataField("", Types.VARCHAR, cdnLavoratore));
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

			for (int i = 0; i < anLavAccBeanVector.size(); i++) {
				XMLGregorianCalendar dtmInsGregorian = null;
				SourceBean anLavAccBeanRow = (SourceBean) anLavAccBeanVector.elementAt(i);
				Accorpamento accorpamento = factory.createPartecipanteTirociniPartecipanteAccorpamento();

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

	private static int getComunicazioniObbligatorie(ComunicazioniObbligatorie comunicazioniObbligatorie,
			SourceBean movimentazioneBeanRows) throws DatatypeConfigurationException, ParseException {
		if (movimentazioneBeanRows != null) {
			Vector movimentazioneBeanVector = movimentazioneBeanRows.getAttributeAsVector("ROW");

			String strCodiceFiscaleDatore = null;
			String strRagioneSociale = null;
			String codComunicazione = null;
			String codComunicazioneOrig = null;
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
			String datComunicazione = null;
			String codCategoriaTir = null;
			String codTipologiaTir = null;
			String strCodiceFiscaleTutore = null;
			String strCognomeTutore = null;
			String strNomeTutore = null;
			String codQualificaSrq = null;
			String indirizzoLavoro = null;
			String comuneLavoro = null;

			for (int i = 0; i < movimentazioneBeanVector.size(); i++) {
				XMLGregorianCalendar datInizioMovGregorian = null;
				XMLGregorianCalendar datConvenzioneGregorian = null;
				XMLGregorianCalendar datComunicazioneGregorian = null;
				XMLGregorianCalendar datFinePfGregorian = null;
				XMLGregorianCalendar datFineMovGregorian = null;

				SourceBean movimentazioneBeanRow = (SourceBean) movimentazioneBeanVector.elementAt(i);

				strCodiceFiscaleDatore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALE");
				strRagioneSociale = (String) movimentazioneBeanRow.getAttribute("STRRAGIONESOCIALE");
				codComunicazione = (String) movimentazioneBeanRow.getAttribute("CODCOMUNICAZIONE");
				codComunicazioneOrig = (String) movimentazioneBeanRow.getAttribute("CODCOMUNICAZIONEORIG");
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
				datComunicazione = (String) movimentazioneBeanRow.getAttribute("DATCOMUNICAZ");
				if (datConvenzione != null) {
					datConvenzioneGregorian = toXMLGregorianCalendarDateOnly(datConvenzione);
				}
				if (datComunicazione != null) {
					datComunicazioneGregorian = toXMLGregorianCalendarDateOnly(datComunicazione);
				}
				codCategoriaTir = (String) movimentazioneBeanRow.getAttribute("CODCATEGORIATIR");
				codTipologiaTir = (String) movimentazioneBeanRow.getAttribute("CODTIPOLOGIATIR");
				strCodiceFiscaleTutore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALETUTORE");
				strCognomeTutore = (String) movimentazioneBeanRow.getAttribute("STRCOGNOMETUTORE");
				strNomeTutore = (String) movimentazioneBeanRow.getAttribute("STRNOMETUTORE");
				codQualificaSrq = (String) movimentazioneBeanRow.getAttribute("CODQUALIFICASRQ");

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
				if (codComunicazione == null || codComunicazione.isEmpty() || codComunicazioneOrig == null
						|| codComunicazioneOrig.isEmpty() || strCodiceFiscaleDatore == null
						|| strCodiceFiscaleDatore.isEmpty() || codAzAteco == null || codAzAteco.isEmpty()
						|| datInizioMov == null || datInizioMov.isEmpty()) {
					// Nel caso in cui uno di questi valori sia vuoto, la
					// comunicazione obbligatoria, non viene estratta.
					continue;
				}

				ComunicazioneObbligatoria comunicazioniObbligatoria = factory
						.createPartecipanteTirociniComunicazioniObbligatorieComunicazioneObbligatoria();
				if (codComunicazione != null) {
					comunicazioniObbligatoria.setCodiceComunicazioneAvviamento(codComunicazione.trim());
				}
				if (codComunicazioneOrig != null) {
					comunicazioniObbligatoria.setCodiceComunicazioneAvviamentoOriginario(codComunicazioneOrig.trim());
				}
				comunicazioniObbligatoria.setDataInvioCo(datComunicazioneGregorian);
				comunicazioniObbligatoria.setConvenzioneData(datConvenzioneGregorian);
				if (numConvenzione != null) {
					comunicazioniObbligatoria.setConvenzioneNumero(numConvenzione.trim());
				}
				comunicazioniObbligatoria.setDataFine(datFineMovGregorian);
				comunicazioniObbligatoria.setDataFinePeriodoFormativo(datFinePfGregorian);
				comunicazioniObbligatoria.setDataInizio(datInizioMovGregorian);
				if (strCodiceFiscaleDatore != null) {
					comunicazioniObbligatoria.setDatoreLavoroCodiceFiscale(strCodiceFiscaleDatore.trim());
				}
				if (strRagioneSociale != null) {
					comunicazioniObbligatoria.setDatoreLavoroDenominazione(strRagioneSociale.trim());
				}
				if (strIndirizzo != null) {
					comunicazioniObbligatoria.setDatoreLavoroIndirizzo(strIndirizzo.trim());
				}
				if (codAzAteco != null) {
					comunicazioniObbligatoria.setDatoreLavoroSettore(codAzAteco.trim());
				}
				if (flgLavoroAgr != null) {
					comunicazioniObbligatoria.setFlagAgricoltura(flgLavoroAgr.trim());
				}
				if (flgLavoroStagional != null) {
					comunicazioniObbligatoria.setFlagStagionale(flgLavoroStagional.trim());
				}
				if (mansioneDesc != null) {
					comunicazioniObbligatoria.setMansioni(mansioneDesc.trim());
				}
				if (codOrario != null) {
					comunicazioniObbligatoria.setModalitaLavoro(codOrario.trim());
				}
				if (codMansioneMin != null) {
					comunicazioniObbligatoria.setQualificaProfessionale(codMansioneMin.trim());
				}
				if (codQualificaSrq != null) {
					comunicazioniObbligatoria.setQualificaSrq(codQualificaSrq.trim());
				}
				if (comuneLavoro != null) {
					comunicazioniObbligatoria.setSedeLavoroCodiceCatastale(comuneLavoro.trim());
				}
				if (indirizzoLavoro != null) {
					comunicazioniObbligatoria.setSedeLavoroIndirizzo(indirizzoLavoro.trim());
				}
				if (codContratto != null) {
					comunicazioniObbligatoria.setTipoContratto(codContratto.trim());
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

				comunicazioniObbligatorie.getComunicazioneObbligatoria().add(comunicazioniObbligatoria);
			}
		}

		return 0;
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

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED);

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

		String statementUrl = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT WHERE strName = 'PartecipanteTirocini'";
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
