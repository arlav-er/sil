package it.eng.sil.coop.webservices.movimenti;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.bean.LavoratoreBean;
import it.eng.sil.coop.webservices.bean.RichiestaBeanCessazione;
import it.eng.sil.coop.webservices.bean.RichiestaBeanUniLav;
import it.eng.sil.coop.webservices.bean.RichiestaBeanUniSomm;
import it.eng.sil.coop.webservices.bean.UserBean;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.coop.webservices.movimenti.constant.ErroriUniLavConstant;
import it.eng.sil.coop.webservices.movimenti.constant.ErroriUniSommConstant;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ValidatorGeneral;
import it.eng.sil.module.movimenti.processors.ControllaMovimenti;
import it.eng.sil.module.movimenti.processors.ControlloDurataTD;
import it.eng.sil.module.movimenti.processors.ControlloMovimentoSimile;
import it.eng.sil.module.movimenti.processors.ControlloPermessi;
import it.eng.sil.module.movimenti.processors.ControlloTipoAssunzione;
import it.eng.sil.module.movimenti.processors.CrossController;
import it.eng.sil.module.movimenti.processors.EseguiImpatti;
import it.eng.sil.module.movimenti.processors.InsertApprendistato;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.InsertDocumento;
import it.eng.sil.module.movimenti.processors.InsertTirocinio;
import it.eng.sil.module.movimenti.processors.ProcControlloMbCmEtaLav;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.movimenti.processors.RettificaMovimento;
import it.eng.sil.module.movimenti.processors.SelectMovimentoPrecManuale;
import it.eng.sil.module.movimenti.processors.SelectMovimentoSucc;
import it.eng.sil.module.movimenti.processors.UpdateMovimentoPrec;
import it.eng.sil.security.User;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class GetUltimoMovimento {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetUltimoMovimento.class.getName());
	final String INDIRIZZO_DEFAULT = "Non presente";
	final String SCHEMA_XSD_INPUT = "movimenti_in.xsd";
	final String SCHEMA_XSD_INPUT_CES = "cessazione_in.xsd";
	final String SCHEMA_XSD_OUTPUT = "movimenti_out.xsd";

	final String SCHEMA_XSD_INPUT_UNILAV = "unilav_in.xsd";
	final String SCHEMA_XSD_INPUT_UNISOMM = "unisomm_in.xsd";

	private static final BigDecimal user = new BigDecimal("150");
	private static final String serviceName = "GetUltimoMovimento";
	public static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");
	File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "ultimoMovimento" + File.separator + SCHEMA_XSD_INPUT);

	File schemaFileout = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "ultimoMovimento" + File.separator + SCHEMA_XSD_OUTPUT);

	File schemaFileUnilav = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "ultimoMovimento" + File.separator + SCHEMA_XSD_INPUT_UNILAV);
	File schemaFileUniSomm = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "ultimoMovimento" + File.separator + SCHEMA_XSD_INPUT_UNISOMM);

	String DELIMETER = "\\|";
	String DELIMETERCatena = "-";

	/**
	 * Restiruisce l'XML con i dati dell'ultima catena di movimenti
	 * 
	 * @param XML
	 *            codice fiscale e codice provincia
	 * @return xml con i dati lavoratore, azienda, stipulabilità DID, movimenti del lavoratore
	 */
	public String getUltimoMovimento(String inputXML) {

		_logger.info("Il servizio di info MOV lavoratore e' stato chiamato");
		DataConnection dataConnection = null;
		StoredProcedureCommand command = null;
		DataResult dataResult = null;
		String xmlOut = "";
		String codiceFiscale = "";
		String IdProvincia = "";
		String strDid = "";
		VerificaDid verificaDid = new VerificaDid();
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			// dataConnection.initTransaction();
			// Ricupero i dati dello xml e valido il file xml in entrata
			String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
			if (validityErrors != null && validityErrors.length() > 0) {
				final String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				_logger.warn(inputXML);
				xmlOut = createXMLRispostaEsitoNegativo("99", "Errore generico");
				return xmlOut;
			} else {
				// Prendo il CF e Cod Regione
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("CodiceFiscale");
				NodeList nListReg = doc.getElementsByTagName("IdProvincia");
				Element ecodiceFiscale = (Element) nList.item(0);
				Element eIdProvincia = (Element) nListReg.item(0);
				codiceFiscale = ecodiceFiscale.getFirstChild().getNodeValue();
				IdProvincia = eIdProvincia.getFirstChild().getNodeValue();
			}

			// Valido xml
			xmlOut = validaXML(codiceFiscale, inputXML);
			if ("".equals(xmlOut)) {
				// Dati lavoratore
				Object[] inputParameters = new Object[1];
				inputParameters[0] = codiceFiscale;
				SourceBean infoLavSB = null;
				infoLavSB = executeSelect("GET_LAV_WS", inputParameters, dataConnection);
				if (!infoLavSB.containsAttribute("ROW")) {
					xmlOut = createXMLRispostaEsitoNegativo("03", "Codice fiscale non trovato");
					return xmlOut;
				}
				// Rapporto lavorativo
				String result = "";
				String sqlStr = "";
				sqlStr = SQLStatements.getStatementInit("GET_ULT_CATENA_MOV");
				command = (StoredProcedureCommand) dataConnection.createStoredProcedureCommand(sqlStr);
				ArrayList inputParametersA = null;
				int paramIndex = 0;
				inputParametersA = new ArrayList(2);
				// 1.Parametro di Ritorno
				inputParametersA.add(dataConnection.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 2.codicefiscale
				inputParametersA
						.add(dataConnection.createDataField("codiceFiscale", java.sql.Types.VARCHAR, codiceFiscale));
				command.setAsInputParameters(paramIndex++);
				DataResult dr = command.execute(inputParametersA);
				PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
				DataField df = pdr.getPunctualDatafield();
				result = df.getStringValue();
				result = result.trim();
				// n movimenti generale
				String[] tempList = result.split(DELIMETER);
				// int sizeOfArray = tempList.length;
				// Il primo risultato tempList ci dici quanti catene aperte ci sono.
				// -- codice ritorno - 0 = nessuno aperto, 1 .. N = N aperto, -1 nessuno movimento
				int numCateneAperte = Integer.parseInt(tempList[0]);
				int numproxCatena = 1;
				SourceBean movSB = null;
				String prgMov;
				Vector<SourceBean> movVector = new Vector<SourceBean>();
				// Set set = new HashSet();
				Object auxprgAzienda;
				Object auxprgUnitaAzienda;
				String auxAzi = "";
				Vector<String> arrayAzi = new Vector<String>();
				Vector<String> strDataInizioRapporto = new Vector<String>();
				Vector<String> strDataFineRapporto = new Vector<String>();

				String strTipoMovimento = "";
				String strcodtipoAzienda = "";
				String strtipoRapporto = "";
				String dataInizioRapportoAppoggio = "";

				String[] tempListNumCatenaPrgmov = null;
				boolean checkLastmovCatena = true;
				String auxnumCatenaPrec = "0";
				String auxnumCatena = "";

				// 1 invece 0 - Il primo è il num di catene - dopo i prgMov
				for (int i = 1; i < tempList.length; i++) {
					tempListNumCatenaPrgmov = tempList[i].split(DELIMETERCatena);
					auxnumCatena = tempListNumCatenaPrgmov[0];
					prgMov = tempListNumCatenaPrgmov[1];

					// Recupero il movimento
					inputParameters[0] = prgMov;
					movSB = executeSelect("GET_MOV_WS", inputParameters, dataConnection);
					strTipoMovimento = StringUtils.getAttributeStrNotNull(movSB, "row.CODTIPOMOV");
					strtipoRapporto = StringUtils.getAttributeStrNotNull(movSB, "row.CODMONOTEMPO");

					// Verifico se sto passando alla catena successiva
					if (!auxnumCatenaPrec.equals(auxnumCatena)) {
						checkLastmovCatena = true;
						// sto cambiando catena e quindi setto la data inizio rapporto della catena precedente
						// che ho memorizzato nella variabile dataInizioRapportoAppoggio
						strDataInizioRapporto.add(dataInizioRapportoAppoggio);
					}

					auxnumCatenaPrec = auxnumCatena;
					dataInizioRapportoAppoggio = StringUtils.getAttributeStrNotNull(movSB, "row.DATINIZIOMOV");

					// Recupero i prgAzienda e prgUnita dell'ultimo movimento della catena - vedi Analisi - Ricupero
					// anche la data fine
					if (checkLastmovCatena) {
						// aziUtilizatrice
						strcodtipoAzienda = (String) movSB.getAttribute("row.aziUtilizatrice");
						if ("S".equals(strcodtipoAzienda)) {
							auxprgAzienda = movSB.getAttribute("row.PRGAZIENDAUTILIZ");
							auxprgUnitaAzienda = movSB.getAttribute("row.PRGUNITAUTILIZ");
						} else {
							auxprgAzienda = movSB.getAttribute("row.prgaziendaM");
							auxprgUnitaAzienda = movSB.getAttribute("row.prgunitaM");
						}
						auxAzi = auxprgAzienda.toString() + "|" + auxprgUnitaAzienda.toString();
						if (auxprgAzienda != null) {
							arrayAzi.add(auxAzi);
						}

						// data fine Rapporto
						if ("CES".equals(strTipoMovimento)) {
							strDataFineRapporto.add(StringUtils.getAttributeStrNotNull(movSB, "row.DATINIZIOMOV"));
						} else if ("PRO".equals(strTipoMovimento) && "D".equals(strtipoRapporto)) {
							strDataFineRapporto
									.add(StringUtils.getAttributeStrNotNull(movSB, "row.datfinemoveffettiva"));
						} else if ("AVV".equals(strTipoMovimento)) {
							strDataFineRapporto
									.add(StringUtils.getAttributeStrNotNull(movSB, "row.datfinemoveffettiva"));
						} else {
							strDataFineRapporto
									.add(StringUtils.getAttributeStrNotNull(movSB, "row.datfinemoveffettiva"));
						}
						checkLastmovCatena = false;
					}

					movSB.setAttribute("row.nCatena", auxnumCatena);
					// Aggiungo il movimento al vettore dei movimenti
					movVector.add(movSB);
				}

				// Se ho delle catene, devo settare la data inizio rapporto dell'ultima catena (in quanto per l'ultima
				// catena non può 
				// esserci il passaggio ad una catena successiva, controllo che viene fatto nel ciclo con il controllo
				// auxnumCatenaPrec <> auxnumCatena
				if (!dataInizioRapportoAppoggio.equals("")) {
					strDataInizioRapporto.add(dataInizioRapportoAppoggio);
				}

				// se non ci sono movimenti
				if (movVector.size() == 0) {
					xmlOut = createXMLRispostaEsitoNegativo("06",
							"Il lavoratore non ha nessuna posizione lavorativa registrata.");
					return xmlOut;
				}

				// Azienda
				SourceBean AziSB = null;
				SourceBean AziLegaleSB = null;
				SourceBean AziOperativaSB = null;
				SourceBean lastAziSB = null;
				Object[] inputParametersAzi = new Object[1];
				String sede = "";
				String[] tempListAzi = null;
				Vector<SourceBean> rows = new Vector<SourceBean>();
				Vector<SourceBean> vAziLegale = new Vector<SourceBean>();
				Vector<SourceBean> vAziOperativa = new Vector<SourceBean>();

				BigDecimal auxprgunita = null;
				int prgazienda;
				int prgunita;

				for (int i = 0; i < arrayAzi.size(); i++) {

					tempListAzi = arrayAzi.get(i).split(DELIMETER);
					prgazienda = Integer.valueOf(tempListAzi[0]);
					prgunita = Integer.valueOf(tempListAzi[1]);
					inputParametersAzi[0] = prgazienda;
					AziSB = executeSelect("GET_AZI_WS", inputParametersAzi, dataConnection);
					if (AziSB != null) {
						rows = AziSB.getAttributeAsVector("row");
					}
					for (int a = 0; a < rows.size(); a++) {
						SourceBean rowAzi = (SourceBean) rows.get(a);
						sede = StringUtils.getAttributeStrNotNull(rowAzi, "sede");
						auxprgunita = (BigDecimal) rowAzi.getAttribute("PRGUNITA");
						if (prgunita == auxprgunita.intValue()) {
							vAziOperativa.add(rowAzi);
							lastAziSB = rowAzi;
						}
						if (("S").equalsIgnoreCase(sede)) {
							AziLegaleSB = rowAzi;
							vAziLegale.add(rowAzi);
						}
					}
					if (vAziLegale.size() < vAziOperativa.size()) {
						vAziLegale.add(lastAziSB);
					}
				}
				// dati DID Stipulabile
				strDid = verificaDid.ritornaDID(codiceFiscale);
				// Creo lo XML
				xmlOut = createXML(movVector, infoLavSB, vAziLegale, vAziOperativa, strDid, numCateneAperte,
						strDataInizioRapporto, strDataFineRapporto);
			}
		} catch (Exception e) {
			/*
			 * try { dataConnection.rollBackTransaction(); } catch (EMFInternalError e1) { _logger.debug("Errore Mov " ,
			 * e1); }
			 */
			_logger.debug("Errore Mov ", e);
		} finally {
			Utils.releaseResources(dataConnection, command, dataResult);
		}
		// se non ci sono errore e lo xml è vuoto.
		if ("".equals(xmlOut)) {
			try {
				xmlOut = createXMLRispostaEsitoNegativo("99", "Errore Generico");
			} catch (Exception e) {
				_logger.debug("Errore Mov ", e);
			}
		}
		return xmlOut;
	}

	public String putComunicaCessazione(String inputXML) throws Exception {
		Document doc = null;
		String outputXML = null;
		String codiceFisc = "";
		RequestContainer requestContainer = null;
		SessionContainer sessionContainer = null;
		SourceBean request = null;
		SourceBean response = null;
		RichiestaBeanCessazione checkRichiesta = null;
		BigDecimal cdnlavoratore = null;
		ArrayList<String> listMovimenti = null;
		UserBean usrSP = null;
		LavoratoreBean lavService = null;
		MultipleTransactionQueryExecutor transExec = null;

		_logger.info("Il servizio di comunica cessazione e' stato chiamato");

		try {
			File schemaFileCes = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "ultimoMovimento" + File.separator + SCHEMA_XSD_INPUT_CES);

			String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFileCes);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				_logger.warn(inputXML);
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("99", "Errore generico");
				return outputXML;
			}

			InputStream is = new ByteArrayInputStream(inputXML.getBytes());
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			doc = documentBuilder.parse(is);
			doc.getDocumentElement().normalize();

			checkRichiesta = new RichiestaBeanCessazione(doc);
			outputXML = checkRichiesta.getOutputXML();
			if (outputXML != null) {
				return outputXML;
			}
			codiceFisc = checkRichiesta.getCodiceFiscale();
			lavService = new LavoratoreBean(codiceFisc);
			cdnlavoratore = lavService.getCdnLavoratore();
			boolean flgCompetenza = false;
			if (lavService.getCodMonoTipoCpi() != null && lavService.getCodMonoTipoCpi().equalsIgnoreCase("C")) {
				flgCompetenza = true;
			}
			if (!flgCompetenza) {
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("10",
						"Lavoratore non competente");
				return outputXML;
			}
			usrSP = new UserBean(user, cdnlavoratore);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaCessazione", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

		try {
			requestContainer = new RequestContainer();
			sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", user);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			sessionContainer.setAttribute(User.USERID, usrSP.getUser());
			requestContainer.setSessionContainer(sessionContainer);
			request = new SourceBean("SERVICE_REQUEST");
			response = new SourceBean("SERVICE_RESPONSE");
			request.setAttribute("FORZA_INSERIMENTO", "true");
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			request.setAttribute("cdnLavoratore", cdnlavoratore.toString());
			requestContainer.setServiceRequest(request);
			RequestContainer.setRequestContainer(requestContainer);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SanaMovimento:putSanatoriaReddito", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

		try {
			transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			listMovimenti = checkRichiesta.getMovimenti();

			String listaMov = getListaMov(listMovimenti);
			if (!listaMov.equals("")) {
				Vector movSanato = catenaAperta(listaMov, transExec);
				int nSize = movSanato.size();
				String codTipoMovUltimoMov = "";
				if (nSize > 0) {
					SourceBean ultimoMov = (SourceBean) movSanato.get(0);
					codTipoMovUltimoMov = (String) ultimoMov.getAttribute("codTipoMov");
					if (codTipoMovUltimoMov.equalsIgnoreCase("CES")) {
						BigDecimal prgMovimento = (BigDecimal) ultimoMov.getAttribute("PRGMOVIMENTO");
						String codMotivoCessazione = ultimoMov.getAttribute("codmvcessazione") != null
								? (String) ultimoMov.getAttribute("codmvcessazione")
								: "";
						if (!codMotivoCessazione.equals("") && !codMotivoCessazione.equalsIgnoreCase("SC")) {
							checkRichiesta.setMotivoCessazione(codMotivoCessazione);
						}
						Vector prgdocs = documentiAssociati(prgMovimento, transExec);
						if (prgdocs != null) {
							sessionContainer.setAttribute("PRGDOCUMENTI", prgdocs);
						}

						boolean ris = rettificaMovimento(ultimoMov, prgMovimento, request, response, requestContainer,
								transExec);
						if (!ris) {
							transExec.rollBackTransaction();
							transExec.closeConnection();
							outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("09",
									"Rettifica data cessazione");
							return outputXML;
						}
						ris = inserisciMovimento(prgMovimento, ultimoMov, checkRichiesta.getDataCessazione(),
								checkRichiesta.getMotivoCessazione(), lavService.getCodCpi(), transExec);
						if (!ris) {
							transExec.rollBackTransaction();
							transExec.closeConnection();
							outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("08",
									"Cessazione non inserita");
							return outputXML;
						}
					} else {
						boolean ris = inserisciMovimento(null, ultimoMov, checkRichiesta.getDataCessazione(),
								checkRichiesta.getMotivoCessazione(), lavService.getCodCpi(), transExec);
						if (!ris) {
							transExec.rollBackTransaction();
							transExec.closeConnection();
							outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("08",
									"Cessazione non inserita");
							return outputXML;
						}
					}
				}
			}

			transExec.commitTransaction();
			transExec.closeConnection();

			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("00", "OK");
			return outputXML;
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
				transExec.closeConnection();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaCessazione", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

	}

	public SourceBean executeSelect(String query_name, Object[] inputParameters, DataConnection dc)
			throws EMFUserError {
		SourceBean ret = null;
		if (dc != null) {
			DBAccess dbAcc = new DBAccess();
			try {
				ret = dbAcc.selectToSourceBean(query_name, inputParameters, dc);
			} catch (EMFInternalError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}
		return ret;
	}

	private final String createXML(Vector<SourceBean> movVector, SourceBean infoLavSB, Vector<SourceBean> AziLegale,
			Vector<SourceBean> AziOperativa, String strDid, int numCateneAperte, Vector<String> strDataInizioRapporto,
			Vector<String> strDataFineRapporto) {

		_logger.debug("buildXml() - start - genero xml ");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";

		String capTrasf = "";
		String strCittadinanza = "";
		String strCittadinanzaLegrapp = "";
		// Did
		String stresitoDid = "";
		String strdescrizioneDid = "";
		String[] tempDid = null;
		// Azi
		int numCateneAperteFor;
		SourceBean rowAzi = null;
		SourceBean rowAziLegale = null;
		String capSedeOperativa = "";
		// Mov
		String codtipomov = "";
		SourceBean rowMov = null;
		String strcodtipoAzienda = "";
		boolean checkMov;
		String dataAvviamentoLast = "";
		try {
			parser = factory.newDocumentBuilder();
			// Create blank DOM Document
			Document doc = parser.newDocument();
			// Insert the root element node
			Element rootElement = doc.createElement("UltimoPeriodo");
			rootElement.setAttribute("schemaVersion", "1");
			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			doc.appendChild(rootElement);

			// Esito
			Element esito = doc.createElement("Esito");
			rootElement.appendChild(esito);
			UtilityXml.appendTextChild("codice", "00", esito, doc);
			UtilityXml.appendTextChild("descrizione", "Nessun Errore", esito, doc);

			// Dati Lavoratore
			String strLavCittadinzanza = StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.flgcee");
			Element DatiLavoratore = doc.createElement("DatiLavoratore");
			rootElement.appendChild(DatiLavoratore);
			UtilityXml.appendTextChild("CodiceFiscale",
					StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.strcodicefiscale"), DatiLavoratore, doc);
			UtilityXml.appendTextChild("Cognome", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.strcognome"),
					DatiLavoratore, doc);
			UtilityXml.appendTextChild("Nome", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.strnome"),
					DatiLavoratore, doc);
			UtilityXml.appendTextChild("DataNascita", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.DTNASC"),
					DatiLavoratore, doc);
			UtilityXml.appendTextChild("CodComNascita", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.codcomnas"),
					DatiLavoratore, doc);
			UtilityXml.appendTextChild("ComNascita", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.STRCOMNASC"),
					DatiLavoratore, doc);
			UtilityXml.appendTextChild("TargaComNascita", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.provNasc"),
					DatiLavoratore, doc);
			UtilityXml.appendTextChild("Sesso", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.strsesso"),
					DatiLavoratore, doc);

			strCittadinanza = StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.cittadinanza");

			if ("COR".equalsIgnoreCase(strCittadinanza) || "CUR".equalsIgnoreCase(strCittadinanza)
					|| "JUG".equalsIgnoreCase(strCittadinanza) || "SDR".equalsIgnoreCase(strCittadinanza)
					|| "NT".equalsIgnoreCase(strCittadinanza)) {
				// Italia
				UtilityXml.appendTextChild("Cittadinanza", "000", DatiLavoratore, doc);
			} else {
				UtilityXml.appendTextChild("Cittadinanza",
						StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.cittadinanza"), DatiLavoratore, doc);
			}

			// residenza
			Element EResidenza = doc.createElement("Residenza");
			DatiLavoratore.appendChild(EResidenza);
			UtilityXml.appendTextChild("CodiceComune", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.CODCOMRES"),
					EResidenza, doc);
			UtilityXml.appendTextChild("DescrComune", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.STRCOMRES"),
					EResidenza, doc);
			UtilityXml.appendTextChild("Targa", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.codProvRes"),
					EResidenza, doc);
			String strIndirizzoRes = StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.strindirizzores");
			if (strIndirizzoRes.equals("")) {
				strIndirizzoRes = INDIRIZZO_DEFAULT;
			}
			UtilityXml.appendTextChild("Indirizzo", strIndirizzoRes, EResidenza, doc);

			// Domicilio
			Element EDomicilio = doc.createElement("Domicilio");
			DatiLavoratore.appendChild(EDomicilio);
			UtilityXml.appendTextChild("CodiceComune", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.CODCOMDOM"),
					EDomicilio, doc);
			UtilityXml.appendTextChild("DescrComune", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.STRCOMDOM"),
					EDomicilio, doc);
			UtilityXml.appendTextChild("Targa", StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.provDom"),
					EDomicilio, doc);
			String strIndirizzoDom = StringUtils.getAttributeStrNotNull(infoLavSB, "ROW.STRINDIRIZZODOM");
			if (strIndirizzoDom.equals("")) {
				strIndirizzoDom = INDIRIZZO_DEFAULT;
			}
			UtilityXml.appendTextChild("Indirizzo", strIndirizzoDom, EDomicilio, doc);

			// Did Stipulabile
			Element EdidStipulabile = doc.createElement("DidStipulabile");
			rootElement.appendChild(EdidStipulabile);
			tempDid = strDid.split(DELIMETER);
			stresitoDid = tempDid[0];
			strdescrizioneDid = tempDid[1];
			UtilityXml.appendTextChild("codice", stresitoDid, EdidStipulabile, doc);
			UtilityXml.appendTextChild("descrizione", strdescrizioneDid, EdidStipulabile, doc);
			if (numCateneAperte == 0) {
				numCateneAperteFor = 1;
			} else {
				numCateneAperteFor = numCateneAperte;
			}

			for (int auxcatene = 0; auxcatene < numCateneAperteFor; auxcatene++) {
				// Rapporto Lavorativo
				Element ERapportolavorativo = doc.createElement("RapportoLavorativo");
				rootElement.appendChild(ERapportolavorativo);
				UtilityXml.appendTextChild("DataInizioRapporto", strDataInizioRapporto.get(auxcatene),
						ERapportolavorativo, doc);
				if (!"".equals(strDataFineRapporto.get(auxcatene))) {
					UtilityXml.appendNullableTextChild("DataFineRapporto", strDataFineRapporto.get(auxcatene),
							ERapportolavorativo, doc);
				} else {
					UtilityXml.appendNullableTextChild("DataFineRapporto", "", ERapportolavorativo, doc);
				}

				// Azi
				Element EDatiAzienda = doc.createElement("DatiAzienda");
				ERapportolavorativo.appendChild(EDatiAzienda);

				rowAzi = (SourceBean) AziOperativa.get(auxcatene);
				UtilityXml.appendTextChild("CodiceFiscale",
						StringUtils.getAttributeStrNotNull(rowAzi, "strcodicefiscale"), EDatiAzienda, doc);
				UtilityXml.appendTextChild("RagioneSociale",
						StringUtils.getAttributeStrNotNull(rowAzi, "strragionesociale"), EDatiAzienda, doc);
				UtilityXml.appendTextChild("CodComuneSede", StringUtils.getAttributeStrNotNull(rowAzi, "codcom"),
						EDatiAzienda, doc);
				UtilityXml.appendTextChild("ComuneSede", StringUtils.getAttributeStrNotNull(rowAzi, "strdenominazione"),
						EDatiAzienda, doc);
				UtilityXml.appendTextChild("TargaSede", StringUtils.getAttributeStrNotNull(rowAzi, "strtarga"),
						EDatiAzienda, doc);
				UtilityXml.appendTextChild("Indirizzo", StringUtils.getAttributeStrNotNull(rowAzi, "strindirizzo"),
						EDatiAzienda, doc);

				capSedeOperativa = StringUtils.getAttributeStrNotNull(rowAzi, "strcap");
				if ("".equals(capSedeOperativa)) {
					UtilityXml.appendTextChild("Cap", "00000", EDatiAzienda, doc);
				} else {
					UtilityXml.appendTextChild("Cap", capSedeOperativa, EDatiAzienda, doc);
				}

				UtilityXml.appendTextChild("CodAteco", StringUtils.getAttributeStrNotNull(rowAzi, "codateco"),
						EDatiAzienda, doc);
				UtilityXml.appendNullableTextChild("Telefono", StringUtils.getAttributeStrNotNull(rowAzi, "strtel"),
						EDatiAzienda, doc);
				UtilityXml.appendNullableTextChild("Fax", StringUtils.getAttributeStrNotNull(rowAzi, "strfax"),
						EDatiAzienda, doc);
				UtilityXml.appendNullableTextChild("Email", StringUtils.getAttributeStrNotNull(rowAzi, "stremail"),
						EDatiAzienda, doc);
				UtilityXml.appendNullableTextChild("NumAlbo",
						StringUtils.getAttributeStrNotNull(rowAzi, "STRNUMALBOINTERINALI"), EDatiAzienda, doc);
				/*
				 * UtilityXml.appendTextChild("codtipoazienda",StringUtils.getAttributeStrNotNull(rowAzi,
				 * "ROW.codtipoazienda"), ERapportolavorativo, doc);
				 * UtilityXml.appendTextChild("sede",StringUtils.getAttributeStrNotNull(rowAzi, "ROW.sede"),
				 * ERapportolavorativo, doc);
				 * UtilityXml.appendTextChild("prgazienda",StringUtils.getAttributeStrNotNull(rowAzi, "ROW.prgazienda"),
				 * ERapportolavorativo, doc);
				 * UtilityXml.appendTextChild("prgunita  ",StringUtils.getAttributeStrNotNull(rowAzi, "ROW.prgunita  "),
				 * ERapportolavorativo, doc);
				 */

				Element EDatiAziendaLegale = doc.createElement("DatiAziendaLegale");
				ERapportolavorativo.appendChild(EDatiAziendaLegale);

				rowAziLegale = (SourceBean) AziLegale.get(auxcatene);
				String capSedeLegale = StringUtils.getAttributeStrNotNull(rowAziLegale, "strcap");
				if (capSedeLegale.equals("")) {
					capSedeLegale = "00000";
				}
				/*
				 * UtilityXml.appendTextChild("strcodicefiscale",StringUtils.getAttributeStrNotNull(rowAziLegale,
				 * "ROW.strcodicefiscale"), EDatiAziendaLegale, doc);
				 * UtilityXml.appendTextChild("strragionesociale",StringUtils.getAttributeStrNotNull(rowAziLegale,
				 * "ROW.strragionesociale"), EDatiAziendaLegale, doc);
				 */
				UtilityXml.appendTextChild("CodComuneLegale",
						StringUtils.getAttributeStrNotNull(rowAziLegale, "codcom"), EDatiAziendaLegale, doc);
				UtilityXml.appendTextChild("ComuneLegale",
						StringUtils.getAttributeStrNotNull(rowAziLegale, "strdenominazione"), EDatiAziendaLegale, doc);
				UtilityXml.appendTextChild("TargaLegale", StringUtils.getAttributeStrNotNull(rowAziLegale, "strtarga"),
						EDatiAziendaLegale, doc);
				UtilityXml.appendTextChild("IndirizzoLegale",
						StringUtils.getAttributeStrNotNull(rowAziLegale, "strindirizzo"), EDatiAziendaLegale, doc);
				UtilityXml.appendTextChild("CapLegale", capSedeLegale, EDatiAziendaLegale, doc);
				UtilityXml.appendNullableTextChild("TelefonoLegale",
						StringUtils.getAttributeStrNotNull(rowAziLegale, "strtel"), EDatiAziendaLegale, doc);
				UtilityXml.appendNullableTextChild("FaxLegale",
						StringUtils.getAttributeStrNotNull(rowAziLegale, "strfax"), EDatiAziendaLegale, doc);
				UtilityXml.appendNullableTextChild("EmailLegale",
						StringUtils.getAttributeStrNotNull(rowAziLegale, "stremail"), EDatiAziendaLegale, doc);
				/*
				 * UtilityXml.appendTextChild("codateco",StringUtils.getAttributeStrNotNull(rowAziLegale,
				 * "ROW.codateco"), EDatiAziendaLegale, doc);
				 * UtilityXml.appendTextChild("STRNUMALBOINTERINALI",StringUtils.getAttributeStrNotNull(rowAziLegale,
				 * "ROW.STRNUMALBOINTERINALI"), EDatiAziendaLegale, doc);
				 * UtilityXml.appendTextChild("codtipoazienda",StringUtils.getAttributeStrNotNull(rowAziLegale,
				 * "ROW.codtipoazienda"), EDatiAziendaLegale, doc);
				 * UtilityXml.appendTextChild("sede",StringUtils.getAttributeStrNotNull(rowAziLegale, "ROW.sede"),
				 * EDatiAziendaLegale, doc);
				 * UtilityXml.appendTextChild("prgazienda",StringUtils.getAttributeStrNotNull(rowAziLegale,
				 * "ROW.prgazienda"), EDatiAziendaLegale, doc);
				 * UtilityXml.appendTextChild("prgunita  ",StringUtils.getAttributeStrNotNull(rowAziLegale,
				 * "ROW.prgunita  "), EDatiAziendaLegale, doc);
				 */

				String nCatena;
				// Movimenti
				for (int a = 0; a < movVector.size(); a++) {

					rowMov = (SourceBean) movVector.get(a);
					nCatena = StringUtils.getAttributeStrNotNull(rowMov, "ROW.nCatena");
					// verifico se il movimento appartiene a questo rapporto lavorativo
					if (Integer.valueOf(nCatena) == auxcatene) {
						checkMov = true;
					} else {
						checkMov = false;
					}

					if (checkMov) {
						dataAvviamentoLast = StringUtils.getAttributeStrNotNull(rowMov, "ROW.datinizioavv");
						Element EMovimento = doc.createElement("Movimento");
						// Attr attrMov = doc.createAttribute("tipo");
						ERapportolavorativo.appendChild(EMovimento);

						codtipomov = StringUtils.getAttributeStrNotNull(rowMov, "ROW.codtipomov");
						/*
						 * if("AVV".equals(codtipomov)){ attrMov.setValue("AVV"); EMovimento.setAttributeNode(attrMov);
						 * } if("TRA".equals(codtipomov)){ attrMov.setValue("TRA");
						 * EMovimento.setAttributeNode(attrMov);
						 * 
						 * } if("PRO".equals(codtipomov)){ attrMov.setValue("PRO");
						 * EMovimento.setAttributeNode(attrMov); } if("CES".equals(codtipomov)){
						 * attrMov.setValue("CES"); EMovimento.setAttributeNode(attrMov); }
						 */
						UtilityXml.appendTextChild("ChiaveMovimento",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.strprgmovimento"), EMovimento, doc);
						UtilityXml.appendTextChild("TipoMovimento",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.codtipomov"), EMovimento, doc);
						// if(!"AVV".equals(codtipomov)){
						UtilityXml.appendNullableTextChild("DataInizioAvviamento",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.datinizioavv"), EMovimento, doc);
						// }

						UtilityXml.appendTextChild("DataInizio",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.datiniziomov"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("DataFine",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.datfinemoveffettiva"), EMovimento, doc);

						if ("TRA".equals(codtipomov)) {
							UtilityXml.appendTextChild("CodTipoTrasformazione",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODTIPOTRASF"), EMovimento, doc);
							UtilityXml.appendTextChild("DescTipoTrasformazione",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.desctipotra"), EMovimento, doc);
							UtilityXml.appendNullableTextChild("DataFineDistacco",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.DATFINEDISTACCO"), EMovimento, doc);
							UtilityXml.appendNullableTextChild("FlagDistaccoParziale",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.flgdistparziale"), EMovimento, doc);
							UtilityXml.appendTextChild("FlagDistaccoAzEstero",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.flgdistazestera"), EMovimento, doc);
							Element EAziDistacco = doc.createElement("DatiAziendaDistaccaria");
							EMovimento.appendChild(EAziDistacco);
							UtilityXml.appendTextChild("CodiceFiscale",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.strcodicefiscale"), EAziDistacco,
									doc);
							UtilityXml.appendTextChild("RagioneSociale",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.strragionesociale"), EAziDistacco,
									doc);
							UtilityXml.appendTextChild("CodComuneSede",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.codcom"), EAziDistacco, doc);
							UtilityXml.appendTextChild("ComuneSede",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.strdenominazione"), EAziDistacco,
									doc);
							UtilityXml.appendTextChild("TargaSede",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.strtarga"), EAziDistacco, doc);
							UtilityXml.appendTextChild("Indirizzo",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.strindirizzo"), EAziDistacco, doc);
							capTrasf = StringUtils.getAttributeStrNotNull(rowMov, "ROW.strcap");
							if ("".equals(capTrasf)) {
								UtilityXml.appendTextChild("Cap", "00000", EAziDistacco, doc);
							} else {
								UtilityXml.appendTextChild("Cap", capTrasf, EAziDistacco, doc);
							}
							UtilityXml.appendTextChild("CodAteco",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.codateco"), EAziDistacco, doc);
							UtilityXml.appendNullableTextChild("Telefono",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.strtel"), EAziDistacco, doc);
							UtilityXml.appendNullableTextChild("Fax",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.strfax"), EAziDistacco, doc);
							UtilityXml.appendNullableTextChild("Email",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.stremail"), EAziDistacco, doc);
						}

						if ("CES".equals(codtipomov)) {
							UtilityXml.appendTextChild("CodMotivoCessazione",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.codmvcessazione"), EMovimento, doc);
							UtilityXml.appendTextChild("DescrizioneMotivoCessazione",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.desccessazione"), EMovimento, doc);
						}

						UtilityXml.appendNullableTextChild("CodLivelloIstruzione",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.codtitolo"), EMovimento, doc);
						UtilityXml.appendTextChild("CodTipoContratto",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.codtipocontratto"), EMovimento, doc);
						UtilityXml.appendTextChild("DescTipoContratto",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.descContratto"), EMovimento, doc);
						UtilityXml.appendTextChild("CodQualificaProf",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.codmansione"), EMovimento, doc);
						UtilityXml.appendTextChild("CodCCNL", StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODCCNL"),
								EMovimento, doc);
						UtilityXml.appendTextChild("CodOrario",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODORARIO"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("NumOrePartTime",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.numoresett"), EMovimento, doc);
						UtilityXml.appendTextChild("RedditoSanato",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.redditoSanato"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("RetribuzioneMensile",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.retribuizioneMensile"), EMovimento,
								doc);
						UtilityXml.appendNullableTextChild("Livello",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.NUMLIVELLO"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("FlagLegge68",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.FLGLEGGE68"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("DatConvenzioneNullaOstaLegge68",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.DATCONVENZIONE"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("NumConvenzioneLegge68",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.NUMCONVENZIONE"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("FlagSocio",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.FLGSOCIO"), EMovimento, doc);

						UtilityXml.appendTextChild("CodEnte", StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODENTE"),
								EMovimento, doc);
						UtilityXml.appendTextChild("CodEntePrev",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRCODICEENTEPREV"), EMovimento, doc);
						UtilityXml.appendTextChild("PatINAL",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRPATINAIL"), EMovimento, doc);
						UtilityXml.appendTextChild("FlagLavoroAgricoltura",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.FLGLAVOROAGR"), EMovimento, doc);
						UtilityXml.appendNullableTextChild("GiornatePrevisteLavoroAgricoltura",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.NUMGGPREVISTIAGR"), EMovimento, doc);
						UtilityXml.appendTextChild("TipoLavAgricoltura",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.codlavorazione"), EMovimento, doc);
						UtilityXml.appendTextChild("PubblicaAmministrazione",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.pubblicaAm"), EMovimento, doc);

						Element EDatiLegaleRap = doc.createElement("DatiLegaleRappresentante");
						EMovimento.appendChild(EDatiLegaleRap);
						UtilityXml.appendNullableTextChild("Cognome",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRCOGNOMELEGRAPP"), EDatiLegaleRap,
								doc);
						UtilityXml.appendNullableTextChild("Nome",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRNOMELEGRAPP"), EDatiLegaleRap, doc);
						UtilityXml.appendNullableTextChild("Sesso",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRSESSOLEGRAPP"), EDatiLegaleRap, doc);
						UtilityXml.appendNullableTextChild("DataNascita",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.DATNASCLEGRAPP"), EDatiLegaleRap, doc);
						UtilityXml.appendNullableTextChild("Comune",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODCOMNASCLEGRAPP"), EDatiLegaleRap,
								doc);

						strCittadinanzaLegrapp = StringUtils.getAttributeStrNotNull(rowMov,
								"ROW.CODCITTADINANZALEGRAPP");
						if ("COR".equalsIgnoreCase(strCittadinanzaLegrapp)
								|| "CUR".equalsIgnoreCase(strCittadinanzaLegrapp)
								|| "JUG".equalsIgnoreCase(strCittadinanzaLegrapp)
								|| "SDR".equalsIgnoreCase(strCittadinanzaLegrapp)
								|| "NT".equalsIgnoreCase(strCittadinanzaLegrapp)) {
							// Italia
							UtilityXml.appendTextChild("Cittadinanza", "000", EDatiLegaleRap, doc);
						} else {
							UtilityXml.appendNullableTextChild("Cittadinanza",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODCITTADINANZALEGRAPP"),
									EDatiLegaleRap, doc);
						}

						UtilityXml.appendNullableTextChild("NumTitoloSoggiorno",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRNUMDOCEXLEGRAPP"), EDatiLegaleRap,
								doc);
						String strflgcee = StringUtils.getAttributeStrNotNull(rowMov, "ROW.flgcee");
						if ("N".equals(strflgcee)) {
							UtilityXml.appendNullableTextChild("TitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODTIPODOCEXLEGRAPP"),
									EDatiLegaleRap, doc);
							UtilityXml.appendNullableTextChild("MotTitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODMOTIVOPERMSOGGEXLEGRAPP"),
									EDatiLegaleRap, doc);
							UtilityXml.appendNullableTextChild("ScadenzaTitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.DATSCADENZALEGRAPP"),
									EDatiLegaleRap, doc);
							UtilityXml.appendNullableTextChild("QuesturaTitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODQUESTURALEGRAPP"),
									EDatiLegaleRap, doc);
						}

						String strtirocinio = StringUtils.getAttributeStrNotNull(rowMov, "ROW.tirocinio");
						if ("S".equals(strtirocinio)) {
							Element ETirocinio = doc.createElement("Tirocinio");
							EMovimento.appendChild(ETirocinio);
							UtilityXml.appendTextChild("CodiceQualificaSRQ",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODQUALIFICASRQ"), ETirocinio, doc);
							UtilityXml.appendTextChild("DescQualificaSRQ",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.descsrq"), ETirocinio, doc);
						}

						if ("N".equals(strLavCittadinzanza)) {
							Element ETitotoloSoggiorno = doc.createElement("TitoloSoggiorno");
							EMovimento.appendChild(ETitotoloSoggiorno);
							UtilityXml.appendNullableTextChild("TitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODTIPODOCEX"), ETitotoloSoggiorno,
									doc);
							UtilityXml.appendNullableTextChild("NumTitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRNUMDOCEX"), ETitotoloSoggiorno,
									doc);
							UtilityXml.appendNullableTextChild("MotTitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODMOTIVOPERMSOGGEX"),
									ETitotoloSoggiorno, doc);
							UtilityXml.appendNullableTextChild("ScadenzaTitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.DATSCADENZA"), ETitotoloSoggiorno,
									doc);
							UtilityXml.appendNullableTextChild("QuesturaTitoloSoggiorno",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.CODQUESTURA"), ETitotoloSoggiorno,
									doc);

							Element EDatiModelloQ = doc.createElement("ModelloQ");
							EMovimento.appendChild(EDatiModelloQ);
							UtilityXml.appendNullableTextChild("SussistenzaAlloggiativa",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.FLGSISTEMAZIONEALLOGGIATIVA"),
									EDatiModelloQ, doc);
							UtilityXml.appendNullableTextChild("ImpegnoSpeseRimp",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.FLGPAGAMENTORIMPATRIO"),
									EDatiModelloQ, doc);

						}

						UtilityXml.appendTextChild("ChiaveMovPrec",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.PRGMOVIMENTOPREC"), EMovimento, doc);
						UtilityXml.appendTextChild("ChiaveMovSucc",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.PRGMOVIMENTOSUCC"), EMovimento, doc);
						UtilityXml.appendTextChild("CodLock",
								StringUtils.getAttributeStrNotNull(rowMov, "ROW.NUMKLOMOV"), EMovimento, doc);
						strcodtipoAzienda = StringUtils.getAttributeStrNotNull(rowMov, "ROW.tipoazigen");
						if ("I".equals(strcodtipoAzienda)) {
							Element EMovSomministrazione = doc.createElement("Somministrazione");
							EMovimento.appendChild(EMovSomministrazione);
							UtilityXml.appendNullableTextChild("NumContrattoSomm",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.STRNUMAGSOMMINISTRAZIONE"),
									EMovSomministrazione, doc);
							UtilityXml.appendNullableTextChild("DataInizioSomm",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.DATINIZIORAPLAV"),
									EMovSomministrazione, doc);
							UtilityXml.appendNullableTextChild("FlagDittaEstOperaItalia",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.FLGAZUTILIZESTERA"),
									EMovSomministrazione, doc);
							UtilityXml.appendNullableTextChild("FlagAziendaEst",
									StringUtils.getAttributeStrNotNull(rowMov, "ROW.FLGAZESTERA"), EMovSomministrazione,
									doc);
						}
					}
				}
			}
			try {
				returnString = UtilityXml.domToString(doc);
				String validityErrors = XMLValidator.getValidityErrors(returnString, schemaFileout);
				if (validityErrors != null && validityErrors.length() > 0) {
					final String validityError = "Errore di validazione xml: " + validityErrors;
					_logger.error(validityError);
					_logger.warn(returnString);
					try {
						returnString = createXMLRispostaEsitoNegativo("99", "Errore Generico");
					} catch (Exception e) {
						_logger.debug("Errore Mov ", e);
					}
					return returnString;
				} else {
					return returnString;
				}
			} catch (TransformerException e) {
				_logger.error("Errore nella trasformazione del xml da inviare", e);
			}
			_logger.debug("buildXml() - end");

		} catch (ParserConfigurationException e) {
			_logger.error("Errore nel parsing dell'xml da inviare", e);
		}
		return returnString;

	}

	public static String createXMLRispostaEsitoNegativo(String codice, String descrizione) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
		parser = factory.newDocumentBuilder();
		Document doc = parser.newDocument();
		Element Elem = doc.createElement("UltimoPeriodo");
		Elem.setAttribute("schemaVersion", "1");
		Elem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		doc.appendChild(Elem);
		Element esito = doc.createElement("Esito");
		Elem.appendChild(esito);
		UtilityXml.appendTextChild("codice", codice, esito, doc);
		UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);
		returnString = UtilityXml.domToString(doc);
		return returnString;
	}

	private final String validaXML(String codiceFiscale, String inputXML) throws Exception {
		String returnString = "";
		BigDecimal cdnLavoratore = null;
		Date today = null;
		String dataCalcolo = it.eng.afExt.utils.DateUtils.getNow();
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codiceFiscale;
		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!lavoratore.containsAttribute("ROW")) {
			returnString = createXMLRispostaEsitoNegativo("03", "Codice fiscale non trovato");
			return returnString;
		} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");
		}
		try {
			XMLValidator.checkObjectFieldExists(codiceFiscale, "codicefiscale", true, codiceFiscaleCheck,
					"\"Codice fiscale\"");
		} catch (MandatoryFieldException e1) {
			returnString = createXMLRispostaEsitoNegativo("04", "Codice fiscale non valido");
			return returnString;
		} catch (FieldFormatException e2) {
			returnString = createXMLRispostaEsitoNegativo("04", "Codice fiscale non valido");
			return returnString;
		}
		Object[] inputParametersCpi = new Object[3];
		inputParametersCpi[0] = cdnLavoratore;
		inputParametersCpi[1] = dataCalcolo;
		inputParametersCpi[2] = dataCalcolo;
		SourceBean cpiLav = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
				inputParametersCpi, "SELECT", Values.DB_SIL_DATI);
		String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
		String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");
		if (codCpiLav == null) {
			returnString = createXMLRispostaEsitoNegativo("99", "Errore generico");
			return returnString;
		} else {
			if (codTipoCpi != null && !"C".equalsIgnoreCase(codTipoCpi)) {
				returnString = createXMLRispostaEsitoNegativo("05", "Lavoratore non competente");
				return returnString;
			}
		}
		return returnString;
	}

	private String getListaMov(ArrayList<String> listMovimenti) {
		String listaMov = "";
		int nSize = listMovimenti.size();
		for (int i = 0; i < nSize; i++) {
			String currPrg = listMovimenti.get(i);
			if (listaMov.equals("")) {
				listaMov = currPrg;
			} else {
				listaMov = listaMov + "," + currPrg;
			}
		}
		return listaMov;
	}

	private Vector catenaAperta(String lista, TransactionQueryExecutor transExec) throws Exception {
		String selectquery = "select mov.prgmovimento, to_char(mov.datiniziomov, 'dd/mm/yyyy') datInizio, mov.numklomov, "
				+ "to_char(mov.datfinemoveffettiva, 'dd/mm/yyyy') datFine, mov.codTipoMov, mov.codTipoContratto, mov.codmvcessazione, "
				+ "mov.codMansione, mov.codMonoTempo, mov.codOrario, mov.numLivello, mov.cdnLavoratore, mov.prgAzienda, mov.prgUnita, "
				+ "mov.prgAziendaUtiliz, mov.prgUnitaUtiliz, mov.flginterasspropria flgAssPropria, to_char(mov.numggprevistiagr) numGGPrevistiAgr, "
				+ "mov.codLavorazione, mov.flglavoroagr, mov.codQualificaSRQ, movapprendist.flgArtigiana, movapprendist.strcognometutore cognomeTutore, "
				+ "movapprendist.strnometutore nomeTutore, movapprendist.strcodicefiscaletutore codFiscTutore, movapprendist.flgTitolareTutore, "
				+ "movapprendist.codMansioneTutore, movapprendist.strlivellotutore livelloTutore, to_char(movapprendist.numanniesptutore) numAnniEspTutore "
				+ "from am_movimento mov left join am_movimento_apprendist movapprendist on (mov.prgmovimento = movapprendist.prgmovimento) "
				+ "where mov.prgmovimento in (" + lista + ") and mov.codstatoatto = 'PR' "
				+ "order by mov.datiniziomov desc, mov.dtmins desc";

		SourceBean row = ProcessorsUtils.executeSelectQuery(selectquery, transExec);
		if (row != null)
			return row.getAttributeAsVector("ROW");
		else
			return null;
	}

	private boolean inserisciMovimento(BigDecimal prgMovRett, SourceBean movDaCessare, String dataCessazione,
			String motivoCess, String codCpiLav, MultipleTransactionQueryExecutor trans) throws Exception {

		Map record = new HashMap();
		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;
		String codTipoContrattoPrec = movDaCessare.containsAttribute("codTipoContratto")
				? movDaCessare.getAttribute("codTipoContratto").toString()
				: null;
		String codMansionePrec = movDaCessare.containsAttribute("codMansione")
				? movDaCessare.getAttribute("codMansione").toString()
				: null;
		String codMonoTempoPrec = movDaCessare.containsAttribute("codMonoTempo")
				? movDaCessare.getAttribute("codMonoTempo").toString()
				: null;
		String codOrarioPrec = movDaCessare.containsAttribute("codOrario")
				? movDaCessare.getAttribute("codOrario").toString()
				: null;
		String numLivelloPrec = movDaCessare.containsAttribute("numLivello")
				? movDaCessare.getAttribute("numLivello").toString()
				: null;
		BigDecimal cdnLav = (BigDecimal) movDaCessare.getAttribute("cdnLavoratore");
		BigDecimal prgAzienda = (BigDecimal) movDaCessare.getAttribute("prgAzienda");
		BigDecimal prgUnita = (BigDecimal) movDaCessare.getAttribute("prgUnita");
		BigDecimal prgAziendaUtiliz = (BigDecimal) movDaCessare.getAttribute("prgAziendaUtiliz");
		BigDecimal prgUnitaUtiliz = (BigDecimal) movDaCessare.getAttribute("prgUnitaUtiliz");
		String flgAssPropria = movDaCessare.containsAttribute("flgAssPropria")
				? movDaCessare.getAttribute("flgAssPropria").toString()
				: null;
		String numGGPrevistiAgr = movDaCessare.containsAttribute("numGGPrevistiAgr")
				? movDaCessare.getAttribute("numGGPrevistiAgr").toString()
				: null;
		String codLavorazioneAgr = movDaCessare.containsAttribute("codLavorazione")
				? movDaCessare.getAttribute("codLavorazione").toString()
				: null;
		String flgLavoroAgr = movDaCessare.containsAttribute("flgLavoroAgr")
				? movDaCessare.getAttribute("flgLavoroAgr").toString()
				: null;
		String codQualificaSRQ = movDaCessare.containsAttribute("codQualificaSRQ")
				? movDaCessare.getAttribute("codQualificaSRQ").toString()
				: null;
		String cognomeTutore = movDaCessare.containsAttribute("cognomeTutore")
				? movDaCessare.getAttribute("cognomeTutore").toString()
				: null;
		String nomeTutore = movDaCessare.containsAttribute("nomeTutore")
				? movDaCessare.getAttribute("nomeTutore").toString()
				: null;
		String codFiscTutore = movDaCessare.containsAttribute("codFiscTutore")
				? movDaCessare.getAttribute("codFiscTutore").toString()
				: null;
		String flgTitolareTutore = movDaCessare.containsAttribute("flgTitolareTutore")
				? movDaCessare.getAttribute("flgTitolareTutore").toString()
				: null;
		String codMansioneTutore = movDaCessare.containsAttribute("codMansioneTutore")
				? movDaCessare.getAttribute("codMansioneTutore").toString()
				: null;
		String livelloTutore = movDaCessare.containsAttribute("livelloTutore")
				? movDaCessare.getAttribute("livelloTutore").toString()
				: null;
		String numAnniEspTutore = movDaCessare.containsAttribute("numAnniEspTutore")
				? movDaCessare.getAttribute("numAnniEspTutore").toString()
				: null;
		String flgArtigiana = movDaCessare.containsAttribute("flgArtigiana")
				? movDaCessare.getAttribute("flgArtigiana").toString()
				: null;

		record.put("CODSTATOATTO", "PA");
		record.put("CODTIPOMOV", "CES");
		record.put("CONTEXT", "inserisci");
		record.put("DATINIZIOMOV", dataCessazione);
		record.put("COLLEGATO", "precedente");
		if (prgMovRett == null) {
			BigDecimal prgmovprec = (BigDecimal) movDaCessare.getAttribute("prgMovimento");
			record.put("PRGMOVIMENTOPREC", prgmovprec);
		} else {
			record.put("PRGMOVIMENTORETT", prgMovRett);
		}

		record.put("CODMVCESSAZIONE", motivoCess);
		record.put("CODMONOMOVDICH", "C");
		record.put("CODMONOPROV", "M");
		record.put("PERMETTIIMPATTI", "true");
		record.put("CODCPILAV", codCpiLav);
		record.put("DATCOMUNICAZ", DateUtils.getNow());

		String codCpiAz = codCpiAzienda(prgAzienda, prgUnita, trans);
		record.put("CODCPI", codCpiAz);

		if (codOrarioPrec == null) {
			codOrarioPrec = "F";
		}
		record.put("CODORARIO", codOrarioPrec);

		if (numLivelloPrec != null) {
			record.put("NUMLIVELLO", numLivelloPrec);
		}
		if (codTipoContrattoPrec != null) {
			record.put("CODTIPOASS", codTipoContrattoPrec);
		}
		if (codMansionePrec != null) {
			record.put("CODMANSIONE", codMansionePrec);
		}
		if (codMonoTempoPrec != null) {
			record.put("CODMONOTEMPO", codMonoTempoPrec);
		}
		if (cdnLav != null) {
			record.put("CDNLAVORATORE", cdnLav);
		}
		if (prgAzienda != null) {
			record.put("PRGAZIENDA", prgAzienda);
		}
		if (prgUnita != null) {
			record.put("PRGUNITAPRODUTTIVA", prgUnita);
			record.put("PRGUNITA", prgUnita);
		}
		if (prgAziendaUtiliz != null) {
			record.put("PRGAZIENDAUTIL", prgAziendaUtiliz);
		}

		if (prgUnitaUtiliz != null) {
			record.put("PRGUNITAUTIL", prgUnitaUtiliz);
		}
		if (flgAssPropria != null) {
			record.put("FLGASSPROPRIA", flgAssPropria);
		}

		if (flgLavoroAgr != null && flgLavoroAgr.equalsIgnoreCase("S")) {
			record.put("FLGLAVOROAGR", flgLavoroAgr);
			if (numGGPrevistiAgr != null) {
				record.put("NUMGGPREVISTIAGR", numGGPrevistiAgr);
				record.put("NUMGGEFFETTUATIAGR", numGGPrevistiAgr);
			}
			if (codLavorazioneAgr != null) {
				record.put("CODLAVORAZIONE", codLavorazioneAgr);
			}

		} else {
			record.put("FLGLAVOROAGR", "N");
		}

		if (codQualificaSRQ != null) {
			record.put("CODQUALIFICASRQ", codQualificaSRQ);
		}

		if (cognomeTutore != null) {
			record.put("STRCOGNOMETUTORE", cognomeTutore);
		}
		if (nomeTutore != null) {
			record.put("STRNOMETUTORE", nomeTutore);
		}
		if (codFiscTutore != null) {
			record.put("STRCODICEFISCALETUTORE", codFiscTutore);
		}
		if (flgTitolareTutore != null) {
			record.put("FLGTITOLARETUTORE", flgTitolareTutore);
		}
		if (codMansioneTutore != null) {
			record.put("CODMANSIONETUTORE", codMansioneTutore);
		}
		if (numAnniEspTutore != null) {
			record.put("NUMANNIESPTUTORE", numAnniEspTutore);
		}
		if (flgArtigiana != null) {
			record.put("FLGARTIGIANA", flgArtigiana);
		}
		if (livelloTutore != null) {
			record.put("STRLIVELLOTUTORE", livelloTutore);
		}

		SourceBean rowProt = (SourceBean) trans.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");

		rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
		BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
		BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
		String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");
		String oraProt = "";
		String dataProt = "";
		if (datProtocollazione != null && !datProtocollazione.equals("")) {
			if (datProtocollazione.length() > 11) {
				oraProt = datProtocollazione.substring(11, 16);
			} else {
				datProtocollazione = "00:00";
			}
			dataProt = datProtocollazione.substring(0, 10);

			record.put("DATAPROT", dataProt);
			record.put("ORAPROT", oraProt);

		}
		record.put("NUMANNOPROT", numAnnoProt);
		record.put("NUMPROTOCOLLO", numProtocollo);
		record.put("TIPOPROT", "S");

		ValidatorGeneral validator = new ValidatorGeneral(record);
		// Processore che seleziona il movimento precedente
		validator.addProcessor(new SelectMovimentoPrecManuale("Seleziona Precedente", trans, null));
		// Controlli sulle autorizzazioni
		validator.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
		// controllo dei dati sensibili del lavoratore
		validator.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
		// Processore per il controllo dell'esistenza di movimenti simili a
		// quello in inserimento
		validator.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbGenerale));
		// controllo sul tipo di assunzione
		validator.addProcessor(new ControlloTipoAssunzione("Controllo tipo assunzione", trans));
		// Processore che controlla la durata dei movimenti a TD
		validator.addProcessor(new ControlloDurataTD("Controlla durata movimenti a TD"));
		// Processore che controlla i dati del movimento.
		validator.addProcessor(new ControllaMovimenti(sbGenerale, trans, user));
		// Processore per ulteriori controlli
		validator.addProcessor(new CrossController("Controllore Incrociato"));
		// Processore per l'esecuzione degli impatti
		RecordProcessor eseguiImpatti = new EseguiImpatti("Esecuzione impatti", sbGenerale, trans, user);
		validator.addProcessor(eseguiImpatti);
		// Inserimento Movimento
		validator.addProcessor(new InsertData("Inserimento Movimento", trans, configbase + "insertMovimento.xml",
				"INSERT_MOVIMENTO", user));
		// Processore che aggiorna il movimento precedente
		validator.addProcessor(new UpdateMovimentoPrec("Aggiorna Precedente", trans, user));
		// Processore per l'inserimento in am_movimento_apprendist
		validator.addProcessor(new InsertApprendistato(user, trans));
		// Processore per l'inserimento in am_movimento_apprendist delle info relative al tirocinio
		validator.addProcessor(new InsertTirocinio(user, trans, sbGenerale));
		validator.addProcessor(new SelectMovimentoSucc("CercaMovimentoSuccessivo", trans));
		// Processors per l'inserimento del documento
		validator.addProcessor(new InsertDocumento(user, trans));

		SourceBean result = validator.importRecords(trans);

		Vector processorResult = result.getAttributeAsVector("PROCESSOR");
		for (int i = 0; i < processorResult.size(); i++) {
			SourceBean sbProcessor = (SourceBean) processorResult.get(i);
			String tipoErrore = sbProcessor.containsAttribute("RESULT") ? sbProcessor.getAttribute("RESULT").toString()
					: "";

			if (tipoErrore.equalsIgnoreCase("ERROR")) {
				return false;
			}
		}
		return true;
	}

	private boolean rettificaMovimento(SourceBean movDaCessare, BigDecimal prgMovRett, SourceBean request,
			SourceBean response, RequestContainer req, MultipleTransactionQueryExecutor trans) throws Exception {

		Map record = new HashMap();
		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;

		BigDecimal numKloMovRett = (BigDecimal) movDaCessare.getAttribute("NUMKLOMOV");
		String dataOdierna = DateUtils.getNow();

		record.put("DATCOMUNICAZRETT", dataOdierna);
		record.put("PRGMOVIMENTORETT", prgMovRett);
		record.put("NUMKLOMOVRETT", numKloMovRett);
		record.put("CODSTATOATTORETT", "AR");
		record.put("CODMOTANNULLAMENTORETT", "COR");
		record.put("CODMONOMOVDICHRETT", "C");
		record.put("PERMETTIIMPATTI", "true");

		ValidatorGeneral validator = new ValidatorGeneral(record);
		validator.addProcessor(new RettificaMovimento("Registra rettifica", trans, user, request, response, req));

		SourceBean result = validator.importRecords(trans);

		Vector processorResult = result.getAttributeAsVector("PROCESSOR");
		for (int i = 0; i < processorResult.size(); i++) {
			SourceBean sbProcessor = (SourceBean) processorResult.get(i);
			String tipoErrore = sbProcessor.containsAttribute("RESULT") ? sbProcessor.getAttribute("RESULT").toString()
					: "";

			if (tipoErrore.equalsIgnoreCase("ERROR")) {
				return false;
			}
		}
		return true;
	}

	private boolean rettificaMovimento(String dataOdierna, BigDecimal numklomov, BigDecimal prgMovRett,
			SourceBean request, SourceBean response, RequestContainer req, MultipleTransactionQueryExecutor trans)
			throws Exception {

		Map record = new HashMap();
		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;

		record.put("DATCOMUNICAZRETT", dataOdierna);
		record.put("PRGMOVIMENTORETT", prgMovRett);
		record.put("NUMKLOMOVRETT", numklomov);
		record.put("CODSTATOATTORETT", "AR");
		record.put("CODMOTANNULLAMENTORETT", "COR");
		record.put("CODMONOMOVDICHRETT", "C");
		record.put("PERMETTIIMPATTI", "true");

		ValidatorGeneral validator = new ValidatorGeneral(record);
		validator.addProcessor(new RettificaMovimento("Registra rettifica", trans, user, request, response, req));

		SourceBean result = validator.importRecords(trans);

		Vector processorResult = result.getAttributeAsVector("PROCESSOR");
		for (int i = 0; i < processorResult.size(); i++) {
			SourceBean sbProcessor = (SourceBean) processorResult.get(i);
			String tipoErrore = sbProcessor.containsAttribute("RESULT") ? sbProcessor.getAttribute("RESULT").toString()
					: "";
			if (tipoErrore.equalsIgnoreCase("ERROR")) {
				return false;
			}
		}
		return true;
	}

	private String codCpiAzienda(BigDecimal prgAzienda, BigDecimal prgUnita, TransactionQueryExecutor transExec)
			throws Exception {
		String codCpi = "";
		String selectquery = "select com.codcpi "
				+ "from de_comune com inner join an_unita_azienda uaz on (com.codcom = uaz.codcom) "
				+ "where uaz.prgazienda = " + prgAzienda + " and uaz.prgunita = " + prgUnita;

		SourceBean row = ProcessorsUtils.executeSelectQuery(selectquery, transExec);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			codCpi = (String) row.getAttribute("codcpi");
		}
		return codCpi;
	}

	private Vector documentiAssociati(BigDecimal prgMovimento, TransactionQueryExecutor transExec) throws Exception {
		Vector documenti = new Vector<BigDecimal>();
		Object[] params = new Object[] { prgMovimento };
		SourceBean row = (SourceBean) transExec.executeQuery("DETTAGLIO_DOC_PRGMOV", params, "SELECT");
		if (row == null) {
			return null;
		} else {
			Vector res = row.getAttributeAsVector("ROW");
			for (int i = 0; i < res.size(); i++) {
				SourceBean sb = (SourceBean) res.get(i);
				documenti.add((BigDecimal) sb.getAttribute("prgdocumento"));
			}
		}
		return documenti;
	}

	public String putComunicaUniLav(String inputXML) throws Exception {
		Document doc = null;
		String outputXML = null;
		String codiceFisc = "";
		RequestContainer requestContainer = null;
		SessionContainer sessionContainer = null;
		SourceBean request = null;
		SourceBean response = null;
		RichiestaBeanUniLav checkRichiesta = null;
		BigDecimal cdnlavoratore = null;
		UserBean usrSP = null;
		LavoratoreBean lavService = null;
		MultipleTransactionQueryExecutor transExec = null;

		_logger.info("Il servizio di comunica movimento e' stato chiamato");

		try {

			String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFileUnilav);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				_logger.warn(inputXML);
				outputXML = it.eng.sil.coop.webservices.utils.Utils
						.createXMLRisposta(ErroriUniLavConstant.ERRORE_RICHIESTA_ERRATA, "Richiesta non valida");
				return outputXML;
			}

			InputStream is = new ByteArrayInputStream(inputXML.getBytes());
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			doc = documentBuilder.parse(is);
			doc.getDocumentElement().normalize();

			checkRichiesta = new RichiestaBeanUniLav(doc);

			codiceFisc = checkRichiesta.getCFLav();
			lavService = new LavoratoreBean(codiceFisc);
			if (lavService.getOutputXml() != null) {
				return lavService.getOutputXml();
			}
			cdnlavoratore = lavService.getCdnLavoratore();
			boolean flgCompetenza = false;
			if (lavService.getCodMonoTipoCpi() != null && lavService.getCodMonoTipoCpi().equalsIgnoreCase("C")) {
				flgCompetenza = true;
			}
			if (!flgCompetenza) {
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
						ErroriUniLavConstant.ERRORE_COMPETENZA_LAVORATORE, "Lavoratore non competente");
				return outputXML;
			}

			// Controlli sui dati della richiesta
			outputXML = checkRichiesta.checkDati();
			if (outputXML != null) {
				return outputXML;
			}

			usrSP = new UserBean(user, cdnlavoratore);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaUniLav", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_GENERICO,
					"Errore generico");
			return outputXML;
		}

		try {
			requestContainer = new RequestContainer();
			sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", user);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			sessionContainer.setAttribute(User.USERID, usrSP.getUser());
			requestContainer.setSessionContainer(sessionContainer);
			request = new SourceBean("SERVICE_REQUEST");
			response = new SourceBean("SERVICE_RESPONSE");
			request.setAttribute("FORZA_INSERIMENTO", "true");
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			request.setAttribute("cdnLavoratore", cdnlavoratore.toString());
			requestContainer.setServiceRequest(request);
			RequestContainer.setRequestContainer(requestContainer);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SanaMovimento:putSanatoriaReddito", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_GENERICO,
					"Errore generico");
			return outputXML;
		}

		try {
			String dataOdierna = DateUtils.getNow();

			transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			try {
				if (!checkRichiesta.getPrgRettifica().equals("")) {
					// Procedo alla rettifica
					BigDecimal prgMov = new BigDecimal(checkRichiesta.getPrgRettifica());
					SourceBean mov = checkRichiesta.getInfoMov(prgMov, user, transExec);
					if (mov == null) {
						transExec.rollBackTransaction();
						transExec.closeConnection();
						outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
								ErroriUniLavConstant.ERRORE_MOVIMENTO_NON_AGGIORNABILE, "Movimento non aggiornabile");
						return outputXML;
					}
					mov = (mov.containsAttribute("ROW") ? (SourceBean) mov.getAttribute("ROW") : mov);
					BigDecimal numklomov = (BigDecimal) mov.getAttribute("numklomov");

					// Annullo per rettifica il documento associato
					Vector prgdocs = documentiAssociati(prgMov, transExec);
					if (prgdocs != null) {
						sessionContainer.setAttribute("PRGDOCUMENTI", prgdocs);
					}

					boolean ris = rettificaMovimento(dataOdierna, numklomov, prgMov, request, response,
							requestContainer, transExec);
					if (!ris) {
						transExec.rollBackTransaction();
						transExec.closeConnection();
						outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
								ErroriUniLavConstant.ERRORE_RETTIFICA_MOVIMENTO, "Errore in rettifica movimento");
						return outputXML;
					}
				}
			}

			catch (Exception e) {
				if (transExec != null) {
					transExec.rollBackTransaction();
					transExec.closeConnection();
				}
				it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaUniLav", e);
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
						ErroriUniLavConstant.ERRORE_RETTIFICA_MOVIMENTO, "Errore in rettifica movimento");
				return outputXML;
			}

			int risIns = checkRichiesta.inserisciMovimento(lavService, user, transExec);

			if (risIns == 0) {
				transExec.commitTransaction();
				transExec.closeConnection();
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("00", "OK");
				return outputXML;
			} else {
				switch (risIns) {
				case MessageCodes.ImportMov.ERR_FIND_MOV_PREC:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniLavConstant.ERRORE_MOVIMENTO_PRECEDENTE,
							"Impossibile collegarlo al movimento precedente");
					return outputXML;

				case MessageCodes.ImportMov.ERR_DATA_FINE_PRO:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniLavConstant.ERRORE_MOVIMENTO_PRECEDENTE,
							"Impossibile collegarlo al movimento precedente");
					return outputXML;

				case MessageCodes.ImportMov.ERR_DATA_INIZIO_PRO:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniLavConstant.ERRORE_MOVIMENTO_PRECEDENTE,
							"Impossibile collegarlo al movimento precedente");
					return outputXML;

				case MessageCodes.ImportMov.ERR_DOPPIO_MOV:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniLavConstant.ERRORE_MOVIMENTO_DOPPIO, "Esiste già un movimento per il lavoratore");
					return outputXML;

				default:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniLavConstant.ERRORE_INSERIMENTO_MOVIMENTO, "Errore in inserimento movimento");
					return outputXML;
				}
			}
		}

		catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
				transExec.closeConnection();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaUniLav", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(ErroriUniLavConstant.ERRORE_GENERICO,
					"Errore generico");
			return outputXML;
		}

	}

	public String putComunicaUniSomm(String inputXML) throws Exception {
		Document doc = null;
		String outputXML = null;
		String codiceFisc = "";
		RequestContainer requestContainer = null;
		SessionContainer sessionContainer = null;
		SourceBean request = null;
		SourceBean response = null;
		RichiestaBeanUniSomm checkRichiesta = null;
		BigDecimal cdnlavoratore = null;
		UserBean usrSP = null;
		LavoratoreBean lavService = null;
		MultipleTransactionQueryExecutor transExec = null;

		_logger.info("Il servizio di comunica movimento e' stato chiamato");

		try {

			String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFileUniSomm);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				_logger.warn(inputXML);
				outputXML = it.eng.sil.coop.webservices.utils.Utils
						.createXMLRisposta(ErroriUniSommConstant.ERRORE_RICHIESTA_ERRATA, "Richiesta non valida");
				return outputXML;
			}

			InputStream is = new ByteArrayInputStream(inputXML.getBytes());
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			doc = documentBuilder.parse(is);
			doc.getDocumentElement().normalize();

			checkRichiesta = new RichiestaBeanUniSomm(doc);

			codiceFisc = checkRichiesta.getCFLav();
			lavService = new LavoratoreBean(codiceFisc);
			if (lavService.getOutputXml() != null) {
				return lavService.getOutputXml();
			}
			cdnlavoratore = lavService.getCdnLavoratore();
			boolean flgCompetenza = false;
			if (lavService.getCodMonoTipoCpi() != null && lavService.getCodMonoTipoCpi().equalsIgnoreCase("C")) {
				flgCompetenza = true;
			}
			if (!flgCompetenza) {
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
						ErroriUniSommConstant.ERRORE_COMPETENZA_LAVORATORE, "Lavoratore non competente");
				return outputXML;
			}

			// Controlli sui dati della richiesta
			outputXML = checkRichiesta.checkDati();
			if (outputXML != null) {
				return outputXML;
			}

			usrSP = new UserBean(user, cdnlavoratore);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaUniSomm", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_GENERICO,
					"Errore generico");
			return outputXML;
		}

		try {
			requestContainer = new RequestContainer();
			sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", user);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			sessionContainer.setAttribute(User.USERID, usrSP.getUser());
			requestContainer.setSessionContainer(sessionContainer);
			request = new SourceBean("SERVICE_REQUEST");
			response = new SourceBean("SERVICE_RESPONSE");
			request.setAttribute("FORZA_INSERIMENTO", "true");
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			request.setAttribute("cdnLavoratore", cdnlavoratore.toString());
			requestContainer.setServiceRequest(request);
			RequestContainer.setRequestContainer(requestContainer);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SanaMovimento:putSanatoriaReddito", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_GENERICO,
					"Errore generico");
			return outputXML;
		}

		try {
			String dataOdierna = DateUtils.getNow();

			transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			try {
				if (!checkRichiesta.getPrgRettifica().equals("")) {
					// Procedo alla rettifica
					BigDecimal prgMov = new BigDecimal(checkRichiesta.getPrgRettifica());
					SourceBean mov = checkRichiesta.getInfoMov(prgMov, user, transExec);
					if (mov == null) {
						transExec.rollBackTransaction();
						transExec.closeConnection();
						outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
								ErroriUniSommConstant.ERRORE_MOVIMENTO_NON_AGGIORNABILE, "Movimento non aggiornabile");
						return outputXML;
					}
					mov = (mov.containsAttribute("ROW") ? (SourceBean) mov.getAttribute("ROW") : mov);
					BigDecimal numklomov = (BigDecimal) mov.getAttribute("numklomov");

					// Annullo per rettifica il documento associato
					Vector prgdocs = documentiAssociati(prgMov, transExec);
					if (prgdocs != null) {
						sessionContainer.setAttribute("PRGDOCUMENTI", prgdocs);
					}

					boolean ris = rettificaMovimento(dataOdierna, numklomov, prgMov, request, response,
							requestContainer, transExec);
					if (!ris) {
						transExec.rollBackTransaction();
						transExec.closeConnection();
						outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
								ErroriUniSommConstant.ERRORE_RETTIFICA_MOVIMENTO, "Errore in rettifica movimento");
						return outputXML;
					}
				}
			}

			catch (Exception e) {
				if (transExec != null) {
					transExec.rollBackTransaction();
					transExec.closeConnection();
				}
				it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaUniSomm", e);
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
						ErroriUniSommConstant.ERRORE_RETTIFICA_MOVIMENTO, "Errore in rettifica movimento");
				return outputXML;
			}

			int risIns = checkRichiesta.inserisciMovimento(lavService, user, transExec);

			if (risIns == 0) {
				transExec.commitTransaction();
				transExec.closeConnection();
				outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta("00", "OK");
				return outputXML;
			} else {
				switch (risIns) {
				case MessageCodes.ImportMov.ERR_FIND_MOV_PREC:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniSommConstant.ERRORE_MOVIMENTO_PRECEDENTE,
							"Impossibile collegarlo al movimento precedente");
					return outputXML;

				case MessageCodes.ImportMov.ERR_DATA_FINE_PRO:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniSommConstant.ERRORE_MOVIMENTO_PRECEDENTE,
							"Impossibile collegarlo al movimento precedente");
					return outputXML;

				case MessageCodes.ImportMov.ERR_DATA_INIZIO_PRO:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniSommConstant.ERRORE_MOVIMENTO_PRECEDENTE,
							"Impossibile collegarlo al movimento precedente");
					return outputXML;

				case MessageCodes.ImportMov.ERR_DOPPIO_MOV:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniSommConstant.ERRORE_MOVIMENTO_DOPPIO, "Esiste già un movimento per il lavoratore");
					return outputXML;

				default:
					transExec.rollBackTransaction();
					transExec.closeConnection();
					outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(
							ErroriUniSommConstant.ERRORE_INSERIMENTO_MOVIMENTO, "Errore in inserimento movimento");
					return outputXML;
				}
			}
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
				transExec.closeConnection();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "GetUltimoMovimento:putComunicaUniSomm", e);
			outputXML = it.eng.sil.coop.webservices.utils.Utils.createXMLRisposta(ErroriUniSommConstant.ERRORE_GENERICO,
					"Errore generico");
			return outputXML;
		}
	}

}
