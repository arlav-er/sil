package it.eng.sil.coop.webservices.clicLavoro.candidatura;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.cigs.bean.BeanUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

public class CLCandidatura extends AbstractSimpleModule {

	public static final int GIORNI_SCADENZA_CANDIDATURA = 60;

	private static final long serialVersionUID = 1L;
	static Logger _logger = Logger.getLogger(CLCandidatura.class.getName());
	private String dataInvio;

	public final static int CODE_XML_VALIDATION = 1;
	public final static String ERR_XML_VALIDATION = "Errore nella generazione dell'xml di risposta";

	public void service(SourceBean request, SourceBean response)
			throws SourceBeanException, EMFInternalError, ParseException, EMFUserError {
		User objUser = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String codCPI = objUser.getCodRif();
		dataInvio = request.getAttribute("DATA_INVIO").toString();

		int idSuccess = this.disableMessageIdSuccess();
		String pageName = Utils.notNull(request.getAttribute("PAGE"));
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		if ("CL_INVIO_CANDIDATURA".equalsIgnoreCase(pageName) || "BL_INVIO_CANDIDATURA".equalsIgnoreCase(pageName)) {
			boolean hasError;
			// 20/09/2012 rimosso controllo su data scadenza candidatura
			// hasError = checkPeriodo(request, response,reportOperation);
			// if (hasError) {
			// return;
			// }
			hasError = checkCvValidoPeriodo(request, response, reportOperation);
			if (hasError) {
				return;
			}
		}

		String prgcandidatura = BeanUtils.getObjectToString(request, "prgcandidatura", null);
		Object objAmb = request.getAttribute("AMBITO_DIFFUSIONE");
		ArrayList<String> codAmbDiff = new ArrayList<String>();
		if (objAmb instanceof Vector) {
			Vector new_name = (Vector) objAmb;
			for (Object object : new_name) {
				codAmbDiff.add(object.toString());
			}
		} else if (objAmb instanceof String) {
			codAmbDiff.add((String) objAmb);
		}

		String codiceFiscale = request.getAttribute("CODICEFISCALE").toString();

		boolean res;
		TransactionQueryExecutor t = new TransactionQueryExecutor(getPool());
		t.initTransaction();
		Object cdnLavoratore = request.getAttribute("cdnLavoratore");
		Object codtipocomunicazionecl = request.getAttribute("codtipocomunicazionecl");
		Object DATA_INVIO = request.getAttribute("DATA_INVIO");
		Object DATA_SCAD_CAND = request.getAttribute("DATA_SCAD_CAND");
		Object codstatoinviocl = request.getAttribute("codstatoinviocl");
		Object codCPIObj = request.getAttribute("codCPI");
		Object titoloFiguraProf = request.getAttribute("strFiguraProf");
		if ("PA".equals(codstatoinviocl)) {
			codstatoinviocl = "PA";
		} else {
			codstatoinviocl = "VA";
		}
		Object codcandidatura = request.getAttribute("codcandidatura");

		// XXX
		// Validità Codice Fiscale
		Object[] inputParametersCF = new Object[1];
		inputParametersCF[0] = cdnLavoratore;
		SourceBean retCF = (SourceBean) t.executeQuery("GET_AN_LAVORATORE_ANAG", inputParametersCF,
				TransactionQueryExecutor.SELECT);

		String cognome = (String) retCF.getAttribute("ROW.STRCOGNOME");
		String nome = (String) retCF.getAttribute("ROW.STRNOME");
		String sesso = (String) retCF.getAttribute("ROW.STRSESSO");
		String data_nascita = (String) retCF.getAttribute("ROW.DATNASC");
		String comune = (String) retCF.getAttribute("ROW.CODCOMNAS");
		boolean checkCittadinanza = retCF.containsAttribute("ROW.CODCITTADINANZA");
		boolean checkComuneDomicilio = retCF.containsAttribute("ROW.CODCOMDOM");

		if (!checkCittadinanza) {
			Vector<String> paramsCitt = new Vector<String>();
			paramsCitt.add("Nazione di cittadinanza");
			reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_CAMPOOBBLIGATORIO_NOTFOUND, "", "", paramsCitt);
			return;
		}

		if (!checkComuneDomicilio) {
			Vector<String> paramsComDom = new Vector<String>();
			paramsComDom.add("Comune  di domicilio");
			reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_CAMPOOBBLIGATORIO_NOTFOUND, "", "",
					paramsComDom);
			return;
		}

		try {
			CF_utils.verificaCF(codiceFiscale, nome, cognome, sesso, data_nascita, comune);
		} catch (CfException e) {
			_logger.error("Eccezione nel formato del codice fiscale");
			reportOperation.reportFailure(e.getMessageIdFail());
			return;
		}

		// Object[] inputParametersRec = new Object[2];
		// inputParametersRec[0] = codCPIObj;
		// inputParametersRec[1] = cdnLavoratore;
		Object[] inputParametersRec = new Object[1];
		inputParametersRec[0] = cdnLavoratore;

		SourceBean retEmail = (SourceBean) t.executeQuery("CL_GET_RECAPITI", inputParametersRec,
				TransactionQueryExecutor.SELECT);
		String email = (String) retEmail.getAttribute("ROW.EMAIL");
		if (email == null || ("").equalsIgnoreCase(email)) {
			Vector<String> paramsEmail = new Vector<String>();
			paramsEmail.add("Email lavoratore");
			reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_CAMPOOBBLIGATORIO_NOTFOUND, "", "", paramsEmail);
			return;
		}

		// controllo che al lavoratore sia collegata una mansione valida. In caso di errore stampo un messaggio.
		Object[] inputParamProfDes = new Object[2];
		inputParamProfDes[0] = dataInvio;
		inputParamProfDes[1] = cdnLavoratore;
		SourceBean retProfDes = (SourceBean) t.executeQuery("CL_CONTROLLO_PROFESSIONE_DESIDERATA", inputParamProfDes,
				TransactionQueryExecutor.SELECT);
		Vector vecProfDes = retProfDes.getAttributeAsVector("ROW");
		if (vecProfDes.size() == 0) {
			Vector<String> paramsProfDes = new Vector<String>();
			reportOperation.reportFailure(MessageCodes.ClicLavoro.NON_ESISTE_LAV_PROFESSIONE_DES, "", "",
					paramsProfDes);
			t.rollBackTransaction();
			return;
		}
		for (int i = 0; i < vecProfDes.size(); i++) {
			SourceBean profIesima = (SourceBean) vecProfDes.get(i);
			boolean esisteProfDes = profIesima.containsAttribute("IDPROFESSIONE");
			if (!esisteProfDes) {
				Vector<String> paramsProfDes = new Vector<String>();
				reportOperation.reportFailure(MessageCodes.ClicLavoro.NON_ESISTE_LAV_PROFESSIONE_DES, "", "",
						paramsProfDes);
				return;
			}
		}

		String xmlGenerato = null;
		try {
			xmlGenerato = buildCandidatura(codiceFiscale, codCPI, dataInvio, codAmbDiff);
			if (StringUtils.isFilledNoBlank(xmlGenerato)) {
				xmlGenerato = xmlGenerato.replace("<cliclavoro", "\n<cliclavoro");
				xmlGenerato = xmlGenerato.replace("></", ">\n</");
				response.setAttribute("xmlGenerato", xmlGenerato);
			}
		} catch (MandatoryFieldException e) {
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
		}

		Object[] fieldWhere = null;
		// salvataggio sul db
		if (StringUtils.isFilledNoBlank(prgcandidatura)) {
			int i = 0;
			fieldWhere = new Object[11];
			// res = doUpdate(request, response);
			fieldWhere[i++] = xmlGenerato;
			fieldWhere[i++] = cdnLavoratore;
			fieldWhere[i++] = codtipocomunicazionecl;
			fieldWhere[i++] = codCPIObj;
			fieldWhere[i++] = DATA_INVIO;
			fieldWhere[i++] = DATA_SCAD_CAND;
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
				objRes = t.executeQuery("CL_DELETE_AMBITO_CANDIDATURA", fieldWhere, TransactionQueryExecutor.DELETE);
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

		} else {
			BigDecimal new_prgcandidatura = this.doNextVal(request, response);
			CLCandidaturaData clUtil = new CLCandidaturaData(codiceFiscale, codCPI, xmlGenerato, codAmbDiff);
			// codcandidatura = clUtil.getCodComunicazione(CLCandidaturaData.TIPO_CLICLAVORO_CANDIDATURA, null);
			codcandidatura = clUtil.getCodComunicazione();

			fieldWhere = new Object[12];
			int i = 0;
			fieldWhere[i++] = new_prgcandidatura;
			fieldWhere[i++] = cdnLavoratore;
			fieldWhere[i++] = codtipocomunicazionecl;
			fieldWhere[i++] = codCPI;
			fieldWhere[i++] = DATA_INVIO;
			fieldWhere[i++] = DATA_SCAD_CAND;
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
			response.setAttribute("new_prgcandidatura", new_prgcandidatura);
		}
		if (res) {
			t.commitTransaction();
			reportOperation.reportSuccess(idSuccess);
		} else {
			t.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
		}
		reportSuccess(reportOperation);
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
			max_gg = (Integer.valueOf(valoreGiorni)).intValue();
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
	public String buildCandidatura(String codiceFiscale, String codCPI, String dataInvio, ArrayList<String> codAmbDiff)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		CLCandidaturaData cLCandidaturaData = new CLCandidaturaData(codiceFiscale, codCPI, dataInvio, codAmbDiff);
		cLCandidaturaData.costruisci();

		return cLCandidaturaData.generaXML();
	}

	public String getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(String dataInvio) {
		this.dataInvio = dataInvio;
	}

}