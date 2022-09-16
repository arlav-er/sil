package it.eng.sil.module.coop;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.CalcolaCpiLav;

/**
 * @author savino
 * 
 */
public class CoopInsertOrSelectLavoratore extends CoopDoInsertModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CoopInsertOrSelectLavoratore.class.getName());

	public boolean doInsertModule(SourceBean request, SourceBean response) {
		SourceBean cache = (SourceBean) getRequestContainer().getSessionContainer()
				.getAttribute(it.eng.sil.module.coop.GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
		SourceBean testata = (SourceBean) cache.getAttribute("TESTATA");
		String provenienza = (String) testata.getAttribute("provenienza");
		if (provenienza.equals("LISTA_LAVORATORE"))
			return inserisciLavoratore(request, response);
		else
			return aggiornaLavoratore(request, response);
	}

	private int updateError = 0;

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean aggiornaLavoratore(SourceBean request, SourceBean response) {
		boolean result = false;
		String strCodiceFiscale = StringUtils.getAttributeStrNotNull(request, "strCodiceFiscale");
		String strCognome = StringUtils.getAttributeStrNotNull(request, "strCognome");
		String strNome = StringUtils.getAttributeStrNotNull(request, "strNome");
		String strSesso = StringUtils.getAttributeStrNotNull(request, "strSesso");
		String datNasc = StringUtils.getAttributeStrNotNull(request, "datNasc");
		String codComNas = StringUtils.getAttributeStrNotNull(request, "codComNas");
		// String cdnLavoratore = (String)
		// request.getAttribute("cdnLavoratore");
		try {
			// controllo dell'esistenza del lavoratore da aggiornare
			setSectionQuerySelect("SELECT_AN_LAVORATORE");
			SourceBean lav = doSelect(request, response, false);
			if (lav == null || !lav.containsAttribute("row.cdnLavoratore"))
				throw new Exception(
						"Il lavoratore " + strCodiceFiscale + " non e' presente. Impossibile eseguire l'aggiornamento");
			String cdnLavoratore = Utils.notNull(lav.getAttribute("row.cdnLavoratore"));

			// recupero il cdnLavoratore locale. Diciamo che ce l'ho gia' nella
			// request?
			// controllo dell'esattezza del codice fiscale: direi assoulutamente
			// inutile...
			/*
			 * 30/08/2006 Savino: cassato. Il controllo avviene in un modulo caricato quando viene fatto il reload della
			 * pagina dell'anagrafica
			 * 
			 * try{ if ( !Utils.notNull(request.getAttribute("FLGCFOK")).equalsIgnoreCase("S")) {
			 * CF_utils.verificaCF(strCodiceFiscale, strNome, strCognome, strSesso, datNasc, codComNas); } } catch
			 * (CfException cfEx) { //!!!!!!!!!!!!!!!!!!!1reportOperation.reportFailure(cfEx.getMessageIdFail()); }
			 */
			// cdnLavoratore = "000";
			request.updAttribute("cdnLavoratore", cdnLavoratore);
			setSectionQueryUpdate("UPDATE_LAVORATORE");
			result = doUpdate(request, response);
			if (!result)
				throw new Exception("errore aggiornamento lavoratore");
			aggiornaCPI(request, response);
			setCdnLavoratore(cdnLavoratore, response);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	private void aggiornaCPI(SourceBean request, SourceBean response) throws Exception {

		String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
		setSectionQuerySelect("SELECT_AN_LAV_STORIA_INF");
		SourceBean numklo = doSelect(request, response, false);
		BigDecimal numkloLavStoriaInf = (BigDecimal) numklo.getAttribute("ROW.NUMKLOLAVSTORIAINF");

		if (numkloLavStoriaInf == null) {
			updateError = 4;
			throw new Exception("Non è stato trovato il lavoratore " + cdnLavoratore + " in AN_LAV_STORIA_INF ");
		}
		numkloLavStoriaInf = numkloLavStoriaInf.add(new BigDecimal("1"));
		request.delAttribute("numKloLavStoriaInf");
		request.setAttribute("numKloLavStoriaInf", numkloLavStoriaInf.toString());

		String codCpiDomicilio = "";
		String codComDom, codComRes;
		codComDom = (String) request.getAttribute("codComDom");
		codComRes = (String) request.getAttribute("codComRes");
		codComDom = (String) request.getAttribute("codComDom");
		codComRes = (String) request.getAttribute("codComRes");
		String strCodiceFiscale = StringUtils.getAttributeStrNotNull(request, "strCodiceFiscale");
		if (codComDom != null || codComRes != null) {
			if (codComDom != null) {
				request.updAttribute("codComune", codComDom);
			} else if (codComRes != null) {
				request.updAttribute("codComune", codComRes);
			}
			setSectionQuerySelect("SELECT_CPI_DOMICILIO");
			SourceBean cpiDomicilioRow = doDynamicSelect(request, response);
			codCpiDomicilio = Utils.notNull(cpiDomicilioRow.getAttribute("row.codCpi"));
		} else {
			_logger.debug("Comune di domicilio e comune di residenza assenti per il lavoratore " + strCodiceFiscale);

		}
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		String codCpiOperatore = user.getCodRif();
		String codCpi = user.getCodRif();
		String strRegioneRif = "";
		String strRegioneRes = "";
		// String codCpiRequest =
		// StringUtils.getAttributeStrNotNull(request,"codCpi");
		// SourceBean regioneRif = ((SourceBean)
		// transExec.executeQuery("SELECT_REGIONE",objReg,"SELECT"));
		request.updAttribute("codCpi", codCpiOperatore);
		setSectionQuerySelect("SELECT_REGIONE");
		SourceBean regioneRif = doSelect(request, response, false);
		// CPI del luogo in cui risiede il lavoratore
		// objReg[0] = StringUtils.getAttributeStrNotNull(request,"codCpi");
		request.updAttribute("codCpi", codCpiDomicilio);
		// SourceBean regioneRes = ((SourceBean)
		// transExec.executeQuery("SELECT_REGIONE",objReg,"SELECT"));
		setSectionQuerySelect("SELECT_REGIONE");
		SourceBean regioneRes = doSelect(request, response, false);
		setSectionQuerySelect("GET_DATI_TS_GENERALE");
		SourceBean provinciaUser = doSelect(request, response, false);
		// SourceBean provinciaUser = ((SourceBean)
		// transExec.executeQuery("GET_DATI_TS_GENERALE",null,"SELECT"));
		String codCpiTit = "", codCpiOrig = "", codMonoTipoCPI = "";
		if ((regioneRif != null) && (regioneRes != null)) {
			if ((regioneRif.getAttribute("ROW.CODREGIONE") != null)
					&& !regioneRif.getAttribute("ROW.CODREGIONE").equals("")) {
				strRegioneRif = (String) regioneRif.getAttribute("ROW.CODREGIONE");
			}
			if ((regioneRes.getAttribute("ROW.CODREGIONE") != null)
					&& !regioneRes.getAttribute("ROW.CODREGIONE").equals("")) {
				strRegioneRes = (String) regioneRes.getAttribute("ROW.CODREGIONE");
			}

			// Calcolo dei dati relativi al cpi del lavoratore appena inserito
			// E' stato spostato in una classe apposita perché va utilizzato
			// anche nella validazione dei movimenti
			// ed è soggetto a modifiche in futuro.
			CalcolaCpiLav calcolatoreCpi = new CalcolaCpiLav(strRegioneRes, strRegioneRif,
					StringUtils.getAttributeStrNotNull(regioneRes, "ROW.CODPROVINCIA"),
					StringUtils.getAttributeStrNotNull(provinciaUser, "ROW.CODPROVINCIASIL").toString(),
					StringUtils.getAttributeStrNotNull(request, "codCpi"), // occhio
					codCpi); // TODO Savino: occhio al cpi

			codCpiTit = calcolatoreCpi.getCodCpiTit();
			codMonoTipoCPI = calcolatoreCpi.getCodMonoTipoCpi();
			codCpiOrig = calcolatoreCpi.getCodCpiOrig();
		} else {
			_logger.debug(
					"Impossibile impostare i dati del CPI in an_lav_storia_inf per il lavoratore " + strCodiceFiscale);

		}
		// updateOK = ((Boolean)
		// transExec.executeQuery("SAVE_AN_LAV_STORIA_INF_INSERT",obj,"UPDATE")).booleanValue();
		request.updAttribute("CODCPITIT", codCpiTit);
		request.updAttribute("CODMONOTIPOCPI", codMonoTipoCPI);
		request.updAttribute("CODCPIORIG", codCpiOrig);
		request.updAttribute("NUMKLOLAVSTORIAINF", numkloLavStoriaInf);

		setSectionQueryUpdate("SAVE_AN_LAV_STORIA_INF_INSERT");
		boolean updateOK = doUpdate(request, response);
		if (!updateOK) {
			updateError = 2;
			throw new Exception("impossibile aggiornare AN_LAV_STORIA_INF in transazione");
		}
	}

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean inserisciLavoratore(SourceBean request, SourceBean response) {
		boolean success = true;

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		String codCpi = user.getCodRif();
		BigDecimal userid = new BigDecimal(user.getCodut());

		String strCodiceFiscale = StringUtils.getAttributeStrNotNull(request, "strCodiceFiscale");
		String strCognome = StringUtils.getAttributeStrNotNull(request, "strCognome");
		String strNome = StringUtils.getAttributeStrNotNull(request, "strNome");
		String strSesso = StringUtils.getAttributeStrNotNull(request, "strSesso");
		String datNasc = StringUtils.getAttributeStrNotNull(request, "datNasc");
		String codComNas = StringUtils.getAttributeStrNotNull(request, "codComNas");
		String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// int updateError = 0;
		boolean updateOK = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		try {
			/*
			 * 30/08/2006 Savino: il controllo e' stato spostato in un modulo caricato in fase di reload della pagina
			 * dell'anagrafica sottostante la scheda lavoratore in coop.
			 * 
			 * if ( !Utils.notNull(request.getAttribute("FLGCFOK")).equalsIgnoreCase("S")) {
			 * CF_utils.verificaCF(strCodiceFiscale, strNome, strCognome, strSesso, datNasc, codComNas); //se c'è errore
			 * lancia un'eccezione di tipo CfException }
			 */
			setSectionQuerySelect("SELECT_AN_LAVORATORE");
			// SourceBean lav = ((SourceBean)
			// transExec.executeQuery("SELECT_AN_LAVORATORE",obj,"SELECT"));
			SourceBean lav = doSelect(request, response, false);
			if (lav != null && lav.containsAttribute("ROW.CDNLAVORATORE")) {
				updateError = 3;
				throw new Exception("Codice Fiscale già presente in archivio.");
			}
			setSectionQuerySelect("GET_AN_NEW_LAVORATORE");
			SourceBean newCdnLav = doSelect(request, response, false);
			cdnLavoratore = Utils.notNull(newCdnLav.getAttribute("row.cdnLavoratore"));
			request.updAttribute("cdnLavoratore", cdnLavoratore);
			// il trigger di an_lavoratore inserisce an_lav_storia_inf con i
			// campi del cpi nulli
			// l'update successiva provvede a valorizzarli.
			setSectionQueryInsert("INSERT_AN_LAVORATOREANAGIND");
			updateOK = doInsert(request, response);
			if (!updateOK) {
				updateError = 1;
				throw new Exception("impossibile aggiornare AN_LAVORATORE in transazione");
			}

			aggiornaCPI(request, response);

			reportOperation.reportSuccess(idSuccess);
			response.setAttribute("operationResult", "SUCCESS");
			setCdnLavoratore(cdnLavoratore, response);
		} /*
			 * catch (CfException cfEx) { success = false; reportOperation.reportFailure(cfEx.getMessageIdFail()); try {
			 * response.setAttribute("operationResult", "ERROR"); } catch (SourceBeanException sbEx) {
			 * reportOperation.reportFailure( MessageCodes.General.INSERT_FAIL, sbEx, getClass().getName() +
			 * "::service()", ""); try { response.setAttribute("errorMesg", "Codice fiscale errato"); } catch
			 * (SourceBeanException e1) { } } }
			 */
		catch (Exception e) {
			success = false;
			int msgcod = MessageCodes.General.INSERT_FAIL;
			try {
				SourceBean opRes = new SourceBean("OPERATIONRESULT");
				opRes.setAttribute("MESSAGE", "Messaggio_di_errore");
				response.setAttribute("operationResult", "ERROR");
				switch (updateError) {
				case 1:
					response.setAttribute("errorMesg", "Impossibile aggiornare AN_LAVORATORE");
					break;

				case 2:
					response.setAttribute("errorMesg", "Impossibile aggiornare AN_LAVORATORE_STORIA_INF");
					break;

				case 3:
					response.setAttribute("errorMesg", "Codice fiscale già presente in archivio");
					msgcod = MessageCodes.Anag.INSERT_FAIL_CF_ESISTENTE; // fornisce
																			// ulteriori
																			// dettagli
					break;

				case 4:
					response.setAttribute("errorMesg", "Non è stato trovato il nuovo lavoratore in AN_LAV_STORIA_INF");
					break;

				}
			} catch (SourceBeanException sbEx) {

			}
			reportOperation.reportFailure(msgcod, e, getClass().getName() + "::service() ", "update in transazione");
		} finally {
			return success;
		}
	}

	void setNextStepConsequences(SourceBean serviceRequest, SourceBean serviceResponse) {
		Object cdnLavoratore = getCdnLavoratore(serviceResponse);
		setConsequencesParameter("cdnLavoratore", cdnLavoratore.toString(), serviceResponse);
	}

	private void setCdnLavoratore(String cdnLavoratore, SourceBean serviceResponse) throws SourceBeanException {
		serviceResponse.setAttribute("cdnLavoratore", cdnLavoratore);
	}

	private String getCdnLavoratore(SourceBean serviceResponse) {
		return Utils.notNull(serviceResponse.getAttribute("cdnLavoratore"));
	}

}// end class
