package it.eng.sil.coop.webservices.clicLavoro.candidatura;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.InvioCandidaturaMyBlenMessage;
import it.eng.sil.coop.messages.jmsmessages.InvioCandidaturaMyPortalMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.cigs.bean.BeanUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

public class CLCandidaturaInvia extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;

	private static final String CODTIPOCOMUNICAZIONE_BLEN = "01_BLEN";
	private static final String CODTIPOCOMUNICAZIONE_CLICLAVORO = "01";

	private static final String WS_LOGON_CLICLAVORO = "SELECT prgws, struserid, " + " strpassword FROM ts_ws "
			+ " WHERE  codservizio = 'SIL_CLICLAV_MYPORTAL' ";

	private static final String WS_LOGON_BLEN = "SELECT prgws, struserid, " + " strpassword FROM ts_ws "
			+ " WHERE  codservizio = 'SIL_CLICLAV_MYBLEN' ";

	private static final String SERVIZIO_CLIC_LAVORO = "InvioCandidaturaMyPortal";
	private static final String SERVIZIO_BLEN = "InvioCandidaturaMyBlen";

	static Logger _logger = Logger.getLogger(CLCandidaturaInvia.class.getName());

	// private String dataInvio;

	public void service(SourceBean request, SourceBean response)
			throws SourceBeanException, EMFInternalError, ParseException, EMFUserError {

		User objUser = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		// String codCPI = objUser.getCodRif(); // 12/09/2012 codCpi preso da request
		// String codCpiCapoluogo = "";
		// dataInvio = request.getAttribute("DATA_INVIO").toString();

		int idSuccess = this.disableMessageIdSuccess();
		String pageName = Utils.notNull(request.getAttribute("PAGE"));
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		///////////////////////
		// CHECK PRELIMINARI //
		///////////////////////

		String cdnLavoratore = BeanUtils.getObjectToString(request, "cdnLavoratore", null);
		String tipoComunicazione = "";

		if ("CL_INVIO_CANDIDATURA".equalsIgnoreCase(pageName)) {
			tipoComunicazione = CLUtility.TIPOCOMUNICAZIONECL;
		} else {
			if ("BL_INVIO_CANDIDATURA".equalsIgnoreCase(pageName)) {
				tipoComunicazione = CLUtility.TIPOCOMUNICAZIONEBLEN;
			}
		}

		if ("CL_INVIO_CANDIDATURA".equalsIgnoreCase(pageName) || "BL_INVIO_CANDIDATURA".equalsIgnoreCase(pageName)) {
			boolean hasError;
			// 20/09/2012 rimosso controllo su data scadenza candidatura
			// 29/11/2012 reintrodotto!
			hasError = checkPeriodo(request, response, reportOperation);
			if (hasError) {
				return;
			}
			hasError = checkCvValidoPeriodo(request, response, reportOperation);
			if (hasError) {
				return;
			}
			hasError = checkCittadinanzaValida(request, response, reportOperation, cdnLavoratore);
			if (hasError) {
				return;
			}
		}

		/////////////////////////////
		// LETTURA DATI DA REQUEST //
		/////////////////////////////

		String prgcandidatura = BeanUtils.getObjectToString(request, "prgcandidatura", null);
		String codiceFiscale = request.getAttribute("CODICEFISCALE").toString();
		String codCpiCapoluogo = BeanUtils.getObjectToString(request, "codCPI", null);
		String codtipocomunicazionecl = BeanUtils.getObjectToString(request, "codtipocomunicazionecl", null);
		String dataInvio = BeanUtils.getObjectToString(request, "DATA_INVIO", null);
		String dataScad = BeanUtils.getObjectToString(request, "DATA_SCAD_CAND", null);
		String dataScadenza = BeanUtils.getObjectToString(request, "DATA_SCAD_CAND", null);
		String codcandidatura = BeanUtils.getObjectToString(request, "codcandidatura", null);
		String titoloFiguraProf = BeanUtils.getObjectToString(request, "strFiguraProf", null);

		// Object cdnLavoratore = request.getAttribute("cdnLavoratore");
		// Object codtipocomunicazionecl = request.getAttribute("codtipocomunicazionecl");
		// Object DATA_INVIO = request.getAttribute("DATA_INVIO");
		// Object DATA_SCAD_CAND = request.getAttribute("DATA_SCAD_CAND");
		// Object codcandidatura = request.getAttribute("codcandidatura");

		// 21/09/2012 Ambito di diffusione è:
		// - unico (combobox)
		// - e obbligatorio (tutti i valori selezionabili nella combo non sono nulli)
		// per ridurre le modifiche si lascia comunque la vecchia logica basata
		// su ArrayList anche se non servirebbe. La vecchia logica era così perchè
		// nell'interfaccia grafica erano presenti checkbox e quindi era abilitata
		// la scelta multipla.
		//
		// Object objAmb = request.getAttribute("AMBITO_DIFFUSIONE");
		// ArrayList<String> codAmbDiff = new ArrayList<String>();
		// if (objAmb instanceof Vector) {
		// Vector new_name = (Vector) objAmb;
		// for (Object object : new_name) {
		// codAmbDiff.add(object.toString());
		// }
		// } else if (objAmb instanceof String) {
		// codAmbDiff.add((String) objAmb);
		// }
		List<String> codAmbDiff = new ArrayList<String>();
		String codAmbitoDiffusioneNew = BeanUtils.getObjectToString(request, "AMBITO_DIFFUSIONE", null);
		codAmbDiff.add(codAmbitoDiffusioneNew);

		boolean res;
		TransactionQueryExecutor t = null;

		try {
			t = new TransactionQueryExecutor(getPool());
			t.initTransaction();

			String codstatoinviocl = request.getAttribute("codstatoinviocl") == null ? ""
					: (String) request.getAttribute("codstatoinviocl");
			if ("PA".equals(codstatoinviocl)) {
				codstatoinviocl = "PA";
			} else {
				codstatoinviocl = "VA";
			}

			/////////////////////////////////////////
			// ANALISI ANAGRAFICA LAVORATORE DA DB //
			/////////////////////////////////////////

			Object[] inputParametersCF = new Object[1];
			inputParametersCF[0] = cdnLavoratore;
			SourceBean retCF = (SourceBean) t.executeQuery("GET_AN_LAVORATORE_ANAG", inputParametersCF,
					TransactionQueryExecutor.SELECT);
			String cognome = (String) retCF.getAttribute("ROW.STRCOGNOME");
			String nome = (String) retCF.getAttribute("ROW.STRNOME");
			String sesso = (String) retCF.getAttribute("ROW.STRSESSO");
			String dataNascita = (String) retCF.getAttribute("ROW.DATNASC");
			String comuneNascita = (String) retCF.getAttribute("ROW.CODCOMNAS");
			boolean checkCittadinanza = retCF.containsAttribute("ROW.CODCITTADINANZA");
			boolean checkComuneDomicilio = retCF.containsAttribute("ROW.CODCOMDOM");

			if (!checkCittadinanza) {
				t.rollBackTransaction();
				Vector<String> paramsCitt = new Vector<String>();
				paramsCitt.add("Nazione di cittadinanza");
				reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_CAMPOOBBLIGATORIO_NOTFOUND, "", "",
						paramsCitt);
				return;
			}

			if (!checkComuneDomicilio) {
				t.rollBackTransaction();
				Vector<String> paramsComDom = new Vector<String>();
				paramsComDom.add("Comune  di domicilio");
				reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_CAMPOOBBLIGATORIO_NOTFOUND, "", "",
						paramsComDom);
				return;
			}

			try {
				CF_utils.verificaCF(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita);
			} catch (CfException e) {
				t.rollBackTransaction();
				_logger.error("Eccezione nel formato del codice fiscale");
				reportOperation.reportFailure(e.getMessageIdFail());
				return;
			}

			/////////////////////////////////
			// ANALISI RECAPITI LAVORATORE //
			/////////////////////////////////

			// Object[] inputParametersRec = new Object[1];
			// inputParametersRec[0] = codCPIObj;
			// inputParametersRec[1] = cdnLavoratore;
			Object[] inputParametersRec = new Object[1];
			inputParametersRec[0] = cdnLavoratore;
			SourceBean retEmail = (SourceBean) t.executeQuery("CL_GET_RECAPITI", inputParametersRec,
					TransactionQueryExecutor.SELECT);
			String email = (String) retEmail.getAttribute("ROW.EMAIL");

			if (email == null || ("").equalsIgnoreCase(email)) {
				t.rollBackTransaction();
				Vector<String> paramsEmail = new Vector<String>();
				paramsEmail.add("Email lavoratore");
				reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_CAMPOOBBLIGATORIO_NOTFOUND, "", "",
						paramsEmail);
				return;
			}

			///////////////////////////////////////
			// ANALISI MANSIONE LAVORATORE //
			// Si controlla che al lavoratore //
			// sia collegata una mansione valida //
			///////////////////////////////////////

			Object[] inputParamProfDes = new Object[2];
			inputParamProfDes[0] = dataInvio;
			inputParamProfDes[1] = cdnLavoratore;
			SourceBean retProfDes = (SourceBean) t.executeQuery("CL_CONTROLLO_PROFESSIONE_DESIDERATA",
					inputParamProfDes, TransactionQueryExecutor.SELECT);
			Vector vecProfDes = retProfDes.getAttributeAsVector("ROW");
			if (vecProfDes.size() == 0) {
				t.rollBackTransaction();
				Vector<String> paramsProfDes = new Vector<String>();
				reportOperation.reportFailure(MessageCodes.ClicLavoro.NON_ESISTE_LAV_PROFESSIONE_DES, "", "",
						paramsProfDes);
				return;
			}
			for (int i = 0; i < vecProfDes.size(); i++) {
				SourceBean profIesima = (SourceBean) vecProfDes.get(i);
				boolean esisteProfDes = profIesima.containsAttribute("IDPROFESSIONE");
				if (!esisteProfDes) {
					t.rollBackTransaction();
					Vector<String> paramsProfDes = new Vector<String>();
					reportOperation.reportFailure(MessageCodes.ClicLavoro.NON_ESISTE_LAV_PROFESSIONE_DES, "", "",
							paramsProfDes);
					return;
				}
			}

			//////////////////////////////////////////
			// GENERAZIONE COD CANDIDATURA SE NUOVA //
			//////////////////////////////////////////

			if (!StringUtils.isFilledNoBlank(prgcandidatura)) {
				CLCandidaturaData clUtil = new CLCandidaturaData();
				codcandidatura = clUtil.getCodComunicazione(CLCandidaturaData.TIPO_CLICLAVORO_CANDIDATURA, null);
			}

			////////////////////////////////////
			// GENERAZIONE XML (CHECK FIELDS) //
			////////////////////////////////////

			String xmlGenerato = null;
			try {
				xmlGenerato = buildCandidatura(codiceFiscale, codCpiCapoluogo, dataInvio, codAmbDiff, codcandidatura,
						dataScad, titoloFiguraProf, tipoComunicazione);

			} catch (MandatoryFieldException e) {
				t.rollBackTransaction();
				reportOperation.reportFailure(e, "buildRichiestaDiPersonale", e.getExceptionMessage());
				_logger.error(e);
				int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
				String problem = e.getExceptionMessage();
				Vector<String> params = new Vector<String>();
				params.add(e.getExceptionMessage());
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				reportOperation.reportFailure(messageCode, "buildCandidatura", problem, params);
				// reportSuccess(reportOperation);
				return;
			} catch (FieldFormatException e) {
				t.rollBackTransaction();
				reportOperation.reportFailure(e, "buildRichiestaDiPersonale", e.getExceptionMessage());
				_logger.error(e);
				int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
				String problem = e.getExceptionMessage();
				Vector<String> params = new Vector<String>();
				params.add(e.getExceptionMessage());
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				reportOperation.reportFailure(messageCode, "buildCandidatura", problem, params);
				// reportSuccess(reportOperation);
				return;
			} catch (Exception e) {
				t.rollBackTransaction();
				reportOperation.reportFailure(e, "buildRichiestaDiPersonale", e.getMessage());
				_logger.error(e);
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				return;
			}

			/////////////////////////////////////////
			// SALVATAGGIO CANDIDATURA SU DATABASE //
			/////////////////////////////////////////

			Object[] fieldWhere = null;
			BigDecimal prgCand = null;
			if (StringUtils.isFilledNoBlank(prgcandidatura)) {

				// UPDATE

				int i = 0;
				fieldWhere = new Object[11];
				// res = doUpdate(request, response);
				fieldWhere[i++] = xmlGenerato;
				fieldWhere[i++] = cdnLavoratore;
				fieldWhere[i++] = codtipocomunicazionecl;
				fieldWhere[i++] = codCpiCapoluogo;
				fieldWhere[i++] = dataInvio;
				fieldWhere[i++] = dataScadenza;
				fieldWhere[i++] = codcandidatura;
				fieldWhere[i++] = codstatoinviocl;
				fieldWhere[i++] = objUser.getCodut();
				fieldWhere[i++] = titoloFiguraProf;
				fieldWhere[i++] = prgcandidatura;

				Object objRes = t.executeQuery("CL_UPDATE_CANDIDATURA", fieldWhere, TransactionQueryExecutor.UPDATE);
				res = Boolean.TRUE.equals(objRes);

				if (res) {
					i = 0;
					fieldWhere = new Object[1];
					fieldWhere[i++] = prgcandidatura;
					objRes = t.executeQuery("CL_DELETE_AMBITO_CANDIDATURA", fieldWhere,
							TransactionQueryExecutor.DELETE);
					res = res && Boolean.TRUE.equals(objRes);
					// cancello l'ambito diffusione precedente e lo re-inserisco
					if (res)
						for (String codambitodiffusione : codAmbDiff) {
							// Salvo anche l'ambito diffusione
							res = insertAmbitoDiffusione(prgcandidatura, codambitodiffusione, objUser, t);
							// se 1 scrittura va male lo salvo e facciamo rollback
							res = res && Boolean.TRUE.equals(objRes);
						}
				}

				prgCand = new BigDecimal(prgcandidatura);

			} else {

				// INSERT

				BigDecimal new_prgcandidatura = this.doNextVal(request, response);

				fieldWhere = new Object[12];
				int i = 0;
				fieldWhere[i++] = new_prgcandidatura;
				fieldWhere[i++] = cdnLavoratore;
				fieldWhere[i++] = codtipocomunicazionecl;
				fieldWhere[i++] = codCpiCapoluogo;
				fieldWhere[i++] = dataInvio;
				fieldWhere[i++] = dataScadenza;
				fieldWhere[i++] = codcandidatura;
				fieldWhere[i++] = codstatoinviocl;
				fieldWhere[i++] = objUser.getCodut();
				fieldWhere[i++] = objUser.getCodut();
				fieldWhere[i++] = xmlGenerato;
				fieldWhere[i++] = titoloFiguraProf;

				Object objRes = t.executeQuery("CL_INSERT_CANDIDATURA", fieldWhere, TransactionQueryExecutor.INSERT);
				res = Boolean.TRUE.equals(objRes);

				if (res) {
					for (String codambitodiffusione : codAmbDiff) {
						res = insertAmbitoDiffusione(new_prgcandidatura, codambitodiffusione, objUser, t);
						// se 1 scrittura va male lo salvo e facciamo rollback
						res = res && Boolean.TRUE.equals(objRes);
					}
				}
				prgCand = new_prgcandidatura;
				response.setAttribute("new_prgcandidatura", new_prgcandidatura);
			}
			if (res) {

				try {
					_logger.info("codtipocomunicazionecl: " + codtipocomunicazionecl);

					if (CODTIPOCOMUNICAZIONE_CLICLAVORO.equals(codtipocomunicazionecl)) {
						sendToClicLavoro(t, codstatoinviocl, xmlGenerato, prgCand);
					} else if (CODTIPOCOMUNICAZIONE_BLEN.equals(codtipocomunicazionecl)) {
						sendToBlen(t, codstatoinviocl, xmlGenerato, prgCand);
					} else
						throw new Exception("Servizio non gestito codtipocomunicazionecl = " + codtipocomunicazionecl);

					t.commitTransaction();
					reportOperation.reportSuccess(idSuccess);

				} catch (Exception e) {
					t.rollBackTransaction();
					///////// t.commitTransaction(); --->>>per test
					reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
				}

			} else {
				t.rollBackTransaction();
				reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
			}
			reportSuccess(reportOperation);
		} catch (Exception e) {
			if (t != null) {
				t.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
		}
	}

	private void sendToClicLavoro(TransactionQueryExecutor t, String codstatoinviocl, String xmlGenerato,
			BigDecimal prgCand) throws EMFInternalError, Exception {
		_logger.info("sendToClicLavoro");

		SourceBean userWs = (SourceBean) t.executeQueryByStringStatement(WS_LOGON_CLICLAVORO, null, "SELECT");
		String username = (String) userWs.getAttribute("ROW.STRUSERID");
		String pwd = (String) userWs.getAttribute("ROW.STRPASSWORD");

		// invio la candidatura a MyPortal tramite CODA
		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		OutQ outQ = new OutQ();
		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setServizio(SERVIZIO_CLIC_LAVORO);

		InvioCandidaturaMyPortalMessage sendCandidaturaMessage = new InvioCandidaturaMyPortalMessage(username, pwd,
				codstatoinviocl, prgCand.toString());

		// mando il messaggio
		sendCandidaturaMessage.setTestata(testataMessaggio);
		sendCandidaturaMessage.setDataSourceJndi(dataSourceJndiName);
		sendCandidaturaMessage.setDatiRichiestaXml(xmlGenerato);

		sendCandidaturaMessage.send(outQ);
	}

	private void sendToBlen(TransactionQueryExecutor t, String codstatoinviocl, String xmlGenerato, BigDecimal prgCand)
			throws EMFInternalError, Exception {
		SourceBean userWs = (SourceBean) t.executeQueryByStringStatement(WS_LOGON_BLEN, null, "SELECT");
		String username = (String) userWs.getAttribute("ROW.STRUSERID");
		String pwd = (String) userWs.getAttribute("ROW.STRPASSWORD");

		// invio la candidatura a MyPortal tramite CODA
		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		OutQ outQ = new OutQ();
		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setServizio(SERVIZIO_BLEN);

		InvioCandidaturaMyBlenMessage sendCandidaturaMessage = new InvioCandidaturaMyBlenMessage(username, pwd,
				codstatoinviocl, prgCand.toString());

		// mando il messaggio
		sendCandidaturaMessage.setTestata(testataMessaggio);
		sendCandidaturaMessage.setDataSourceJndi(dataSourceJndiName);
		sendCandidaturaMessage.setDatiRichiestaXml(xmlGenerato);

		sendCandidaturaMessage.send(outQ);
	}

	private boolean insertAmbitoDiffusione(Object prgcandidatura, String codambitodiffusione, User objUser,
			TransactionQueryExecutor t) throws EMFInternalError {
		int cdnUt = objUser.getCodut();
		Object[] fieldWhere;
		int i;
		Object objRes;
		BigDecimal prgambitocandidatura = DBKeyGenerator.getNextSequence(getPool(), "s_cl_ambito_candidatura");
		i = 0;
		fieldWhere = new Object[5];
		fieldWhere[i++] = prgambitocandidatura;
		fieldWhere[i++] = prgcandidatura;
		fieldWhere[i++] = codambitodiffusione;
		fieldWhere[i++] = cdnUt;
		fieldWhere[i++] = cdnUt;
		objRes = t.executeQuery("CL_INSERT_AMBITO_CANDIDATURA", fieldWhere, TransactionQueryExecutor.INSERT);
		// se 1 scrittura va male lo salvo e facciamo rollback
		return Boolean.TRUE.equals(objRes);
	}

	private boolean checkPeriodo(SourceBean request, SourceBean response, ReportOperationResult reportOperation)
			throws ParseException {
		BeanUtils bu = new BeanUtils(request);
		// BigDecimal cndLavoratore = bu.getBigDecimal0("cndLavoratore");
		Date DATA_INVIO = bu.getDateBySql("DATA_INVIO");
		Date DATA_SCAD_CAND = bu.getDateBySql("DATA_SCAD_CAND");
		// int max_gg = 45;
		int max_gg = CLCandidatura.GIORNI_SCADENZA_CANDIDATURA;
		if (request.containsAttribute("numGiorniScadenza")) {
			String valoreGiorni = request.getAttribute("numGiorniScadenza").toString();
			max_gg = (new Integer(valoreGiorni)).intValue();
		}
		Date maxDateScad = DateUtils.addDays(DATA_INVIO, max_gg);
		if (DATA_SCAD_CAND.after(maxDateScad)) {
			int messageCode = MessageCodes.ClicLavoro.PERIODO_CANDIDATURA_NON_VALIDO;
			String problem = "PERIODO CANDIDATURA NON VALIDO";
			Vector<String> params = new Vector<String>();
			params.add(String.valueOf(max_gg));
			String maxDateScadFormatted = DateFormatUtils.format(maxDateScad, "dd/MM/yyyy");
			params.add(maxDateScadFormatted);
			reportOperation.reportFailure(messageCode, "checkPeriodo", problem, params);
			reportSuccess(reportOperation);
			return true;
		}
		return false;
	}

	private boolean checkCvValidoPeriodo(SourceBean request, SourceBean response, ReportOperationResult reportOperation)
			throws EMFInternalError {

		SourceBean _esiste_cv = doSelect(request, response);
		boolean esiste = _esiste_cv.containsAttribute("ROW.esiste");
		if (!esiste) {
			int messageCode = MessageCodes.ClicLavoro.NON_ESISTE_CV_VALIDO_PERIODO_CANDIDATURA;
			String problem = "NON ESISTE CV VALIDO PERIODO CANDIDATURA";
			Vector<String> params = new Vector<String>();
			reportOperation.reportFailure(messageCode, "checkCvValidoPeriodo", problem, params);
			reportSuccess(reportOperation);
			return true;
		}
		return false;
	}

	private boolean checkCittadinanzaValida(SourceBean request, SourceBean response,
			ReportOperationResult reportOperation, String cdnLavoratore) throws EMFInternalError {

		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;
		SourceBean cittadinanzaValidaSB = (SourceBean) QueryExecutor.executeQuery("CL_CHECK_CITTADINANZA_VALIDA",
				inputParameters, TransactionQueryExecutor.SELECT, getPool());
		boolean esiste = cittadinanzaValidaSB.containsAttribute("ROW.esiste");
		if (!esiste) {
			// il messaggio di errore viene già generato dalla pagina target
			// int messageCode = MessageCodes.ClicLavoro.CITTADINANZA_NON_VALIDA;
			// String problem = "Attezione: la cittadinanza indicata per il lavoratore risulta essere una codifica
			// scaduta";
			// Vector<String> params = new Vector<String>();
			// reportOperation.reportFailure(messageCode,
			// "checkCittadinanzaValida", problem, params);
			// reportSuccess(reportOperation);
			return true;
		}
		return false;
	}

	/**
	 * Costruisce l'xml da inviare a clic lavoro relativo alla candidatura di una persona fisica
	 * 
	 * @param codiceFiscale
	 *            codice fiscale della persona della quale si vogliono inviare le informazioni
	 * @return xml costruito e validato, pronto per essere inviato al webService di clicLavoro
	 * @throws CLCandidaturaException
	 *             in caso di errori durante la costruzione dell'xml
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 */
	public String buildCandidatura(String codiceFiscale, String codCPI, String dataInvio, List<String> codAmbDiff,
			String tipoComunicazione) throws EMFUserError, MandatoryFieldException, FieldFormatException {
		CLCandidaturaData cLCandidaturaData = new CLCandidaturaData(codiceFiscale, codCPI, dataInvio, codAmbDiff,
				tipoComunicazione);
		cLCandidaturaData.costruisci();

		return cLCandidaturaData.generaXML();
	}

	public String buildCandidatura(String codiceFiscale, String codCPI, String dataInvio, List<String> codAmbDiff,
			String codcandidatura, String dataScad, String titolo, String tipoComunicazione)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		CLCandidaturaData cLCandidaturaData = new CLCandidaturaData(codiceFiscale, codCPI, dataInvio, codAmbDiff,
				codcandidatura, dataScad, titolo, tipoComunicazione);
		cLCandidaturaData.costruisci();

		return cLCandidaturaData.generaXML();
	}

	/*
	 * public String getDataInvio() { return dataInvio; }
	 * 
	 * public void setDataInvio(String dataInvio) { this.dataInvio = dataInvio; }
	 */
}
