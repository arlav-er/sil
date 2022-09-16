package it.eng.sil.module.patto;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/** Classe per la creazione di un nuovo patto a partire dal precedente. */
public class DuplicaPatto extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DuplicaPatto.class.getName());

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		int prevIdSuccess = 0;
		int prevIdFail = 0;
		boolean esitoPat_Lav = false;
		boolean esitoLav_Pat_Scelta = false;
		TransactionQueryExecutor transExec = null;
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		String prgPattoLav = request.containsAttribute("PRGPATTOLAVORATORE")
				? request.getAttribute("PRGPATTOLAVORATORE").toString()
				: "";
		String newPrgPattoLav = "";
		prevIdSuccess = disableMessageIdSuccess();
		prevIdFail = disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();

			// imposto il nuovo prgpattolavoratore, per la selezione successiva
			this.setSectionQuerySelect("SELECT_NEW_PRGPATTOLAV");// SELECT_CURR_PRGPATTOLAV
			SourceBean tmpPrg = doSelect(request, response);
			if (tmpPrg != null) {
				newPrgPattoLav = tmpPrg.getAttribute("ROW.prgPattoLavoratore").toString();
				sessionContainer.setAttribute("PRGPATTOLAVORATORE", newPrgPattoLav);
			}

			// Si prosegue solo se il nuovo prgpattoLavoratore è stato
			// recuperato
			if ((newPrgPattoLav != null) && (!newPrgPattoLav.equals(""))) {
				// **** Inserimento in AM_PATTO_LAVORATORE ****
				this.setSectionQuerySelect("SELECT_AM_PATTO_LAV");
				SourceBean am = doSelect(request, response);
				if (am != null) {
					// Cancella gli attributi della request
					pulisciRequest(request);
					// Setta gli attributi della request
					riempiRequest(request, am, "LAV_PATTO");
					request.setAttribute("PRGPATTOLAVORATORE", newPrgPattoLav);
					this.setSectionQueryInsert("INSERT_AM_PATTO_LAV");
					esitoPat_Lav = doInsert(request, response);
				} else {
					esitoPat_Lav = false;
				}
				// **** Inserimento in AM_LAV_PATTO_SCELTA ****
				// Si prosegue solo se la prima operazione di insert è andata a
				// buon fine
				if (esitoPat_Lav) {
					pulisciRequest(request);
					// Settaggio del prgpattolavoratore per la select
					request.setAttribute("PRGPATTOLAVORATORE", prgPattoLav);
					this.setSectionQuerySelect("SELECT_AM_LAV_PATTO_SCELTA");
					SourceBean tmp = doSelect(request, response);
					Vector amLavVec = tmp.getAttributeAsVector("ROW");
					SourceBean amLav = null;
					if ((amLavVec != null) && (amLavVec.size() > 0)) {
						// Cancella gli attributi della request
						for (int i = 0; i < amLavVec.size(); i++) {
							pulisciRequest(request);
							// Setta gli attributi della request
							amLav = (SourceBean) amLavVec.elementAt(i);
							riempiRequest(request, amLav, "LAV_PATTO_SCELTA");
							request.setAttribute("PRGPATTOLAVORATORE", newPrgPattoLav);
							this.setSectionQueryInsert("INSERT_AM_LAV_PATTO_SCELTA");
							esitoLav_Pat_Scelta = doInsert(request, response);
							if (!esitoLav_Pat_Scelta) {
								i = amLavVec.size();
							}
						}
					} else {
						if (amLavVec.size() > 0)
							esitoLav_Pat_Scelta = false;
						else
							esitoLav_Pat_Scelta = true;
					}
				}
			} else {
				esitoPat_Lav = false;
			}
			setMessageIdSuccess(prevIdSuccess);
			setMessageIdFail(prevIdFail);
			if (esitoPat_Lav && esitoLav_Pat_Scelta) {
				transExec.commitTransaction();
				reportSuccess(reportOperation);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);

			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "service()",
					"errore durante l'inserimento di un nuovo patto dal precedente.");
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
					sessionContainer.delAttribute("PRGPATTOLAVORATORE");
				}
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);

			}
		}
	}

	private void pulisciRequest(SourceBean request) {
		request.delContainedAttributes();
	}

	private void riempiRequest(SourceBean request, SourceBean am, String context) throws SourceBeanException {
		if (context.equals("LAV_PATTO")) {
			// try {
			request.setAttribute("CODCPI",
					it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(am, "ROW.CODCPI").toString());
			request.setAttribute("CDNLAVORATORE",
					am.containsAttribute("ROW.CDNLAVORATORE") ? am.getAttribute("ROW.CDNLAVORATORE").toString() : "");
			request.setAttribute("PRGDICHDISPONIBILITA",
					am.containsAttribute("ROW.PRGDICHDISPONIBILITA")
							? am.getAttribute("ROW.PRGDICHDISPONIBILITA").toString()
							: "");
			request.setAttribute("DATSTIPULA",
					am.containsAttribute("ROW.DATSTIPULA") ? am.getAttribute("ROW.DATSTIPULA").toString() : "");
			// request.setAttribute("CODSTATOATTO",am.containsAttribute("ROW.CODSTATOATTO")?am.getAttribute("ROW.CODSTATOATTO").toString():"");
			request.setAttribute("CODSTATOATTO", "PP");
			request.setAttribute("PRGSTATOOCCUPAZ",
					am.containsAttribute("ROW.PRGSTATOOCCUPAZ") ? am.getAttribute("ROW.PRGSTATOOCCUPAZ").toString()
							: "");
			request.setAttribute("FLGCOMUNICAZESITI",
					am.containsAttribute("ROW.FLGCOMUNICAZESITI") ? am.getAttribute("ROW.FLGCOMUNICAZESITI").toString()
							: "");
			request.setAttribute("CODMOTIVOFINEATTO",
					am.containsAttribute("ROW.CODMOTIVOFINEATTO") ? am.getAttribute("ROW.CODMOTIVOFINEATTO").toString()
							: "");
			request.setAttribute("DATSCADCONFERMA",
					am.containsAttribute("ROW.DATSCADCONFERMA") ? am.getAttribute("ROW.DATSCADCONFERMA").toString()
							: "");
			request.setAttribute("DATFINE",
					am.containsAttribute("ROW.DATFINE") ? am.getAttribute("ROW.DATFINE").toString() : "");
			request.setAttribute("STRNOTE",
					am.containsAttribute("ROW.STRNOTE") ? am.getAttribute("ROW.STRNOTE").toString() : "");
			request.setAttribute("CDNUTINS",
					am.containsAttribute("ROW.CDNUTINS") ? am.getAttribute("ROW.CDNUTINS").toString() : "");
			request.setAttribute("DTMINS",
					am.containsAttribute("ROW.DTMINS") ? am.getAttribute("ROW.DTMINS").toString() : "");
			request.setAttribute("CDNUTMOD",
					am.containsAttribute("ROW.CDNUTMOD") ? am.getAttribute("ROW.CDNUTMOD").toString() : "");
			request.setAttribute("DTMMOD",
					am.containsAttribute("ROW.DTMMOD") ? am.getAttribute("ROW.DTMMOD").toString() : "");
			request.setAttribute("NUMKLOPATTOLAVORATORE",
					am.containsAttribute("ROW.NUMKLOPATTOLAVORATORE")
							? am.getAttribute("ROW.NUMKLOPATTOLAVORATORE").toString()
							: "");
			request.setAttribute("FLGPATTO297",
					am.containsAttribute("ROW.FLGPATTO297") ? am.getAttribute("ROW.FLGPATTO297").toString() : "");
			request.setAttribute("CODTIPOPATTO",
					am.containsAttribute("ROW.CODTIPOPATTO") ? am.getAttribute("ROW.CODTIPOPATTO") : "");
			request.setAttribute("STRNOTEAMBITOPROF",
					am.containsAttribute("ROW.STRNOTEAMBITOPROF") ? am.getAttribute("ROW.STRNOTEAMBITOPROF").toString()
							: "");
			request.setAttribute("STRNOTESITUAZIONEAMM",
					am.containsAttribute("ROW.STRNOTESITUAZIONEAMM")
							? am.getAttribute("ROW.STRNOTESITUAZIONEAMM").toString()
							: "");
			request.setAttribute("STRNOTEAZIONI",
					am.containsAttribute("ROW.STRNOTEAZIONI") ? am.getAttribute("ROW.STRNOTEAZIONI").toString() : "");
			request.setAttribute("STRNOTEIMPEGNI",
					am.containsAttribute("ROW.STRNOTEIMPEGNI") ? am.getAttribute("ROW.STRNOTEIMPEGNI").toString() : "");
			request.setAttribute("CODSERVIZIO",
					am.containsAttribute("ROW.CODSERVIZIO") ? am.getAttribute("ROW.CODSERVIZIO").toString() : "");
			// }catch(Exception e){}
		} else {
			if (context.equals("LAV_PATTO_SCELTA")) {
				request.setAttribute("CODLSTTAB",
						am.containsAttribute("CODLSTTAB") ? am.getAttribute("CODLSTTAB") : "");
				request.setAttribute("STRCHIAVETABELLA",
						am.containsAttribute("STRCHIAVETABELLA") ? am.getAttribute("STRCHIAVETABELLA") : "");
				request.setAttribute("STRCHIAVETABELLA2",
						am.containsAttribute("STRCHIAVETABELLA2") ? am.getAttribute("STRCHIAVETABELLA2") : "");
				request.setAttribute("PRGLAVPATTOSCELTAINFCOLL",
						am.containsAttribute("PRGLAVPATTOSCELTAINFCOLL") ? am.getAttribute("PRGLAVPATTOSCELTAINFCOLL")
								: "");
				request.setAttribute("STRCHIAVETABELLA3",
						am.containsAttribute("STRCHIAVETABELLA3") ? am.getAttribute("STRCHIAVETABELLA3") : "");
				request.setAttribute("STRALTREINF",
						am.containsAttribute("STRALTREINF") ? am.getAttribute("STRALTREINF") : "");
			}
		}
	}
}