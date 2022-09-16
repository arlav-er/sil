package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;

/*
 * Creato il 20-giu-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.movimenti.processors.Warning;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.DidBean;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleManager;

/**
 * @author Togna
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class RiapriDid extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RiapriDid.class.getName());

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		if (serviceRequest.getAttribute("codMotivoRiapertura") == null
				|| ((String) serviceRequest.getAttribute("codMotivoRiapertura")).equals("")) {
			ArrayList warn = (ArrayList) addWarning(MessageCodes.DID.MOTIVO_RIAPERTURA_OBBLIGATORIO, serviceRequest);

			ProcessorsUtils.createResponse("RiapriDid", this.getClass().getName(), null, null, warn, null);
			serviceResponse.setAttribute("NON_RIAPERTA", "true");
			// ProcessorsUtils.createResponse("M_RiapriDId",this.getClass().getName(),
			// throw new Exception();
		} else {
			Object cdnUser = getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
			TransactionQueryExecutor txExecutor = null;
			try {
				txExecutor = new TransactionQueryExecutor(getPool(), this);
				txExecutor.initTransaction();
				enableTransactions(txExecutor);
				SourceBean row = null;
				Object prgDichDisponibilita = serviceRequest.getAttribute("prgDichDisponibilita");
				Object paramsSelect[] = new Object[1];
				paramsSelect[0] = prgDichDisponibilita;
				row = (SourceBean) txExecutor.executeQuery("GET_ELENCO_ANAGRAFICO_APERTO", paramsSelect, "SELECT");
				if (row == null) {
					throw new Exception("impossibile leggere prgelencoanagrafico dalla tabella am_elenco_anagrafico");
				}
				Object prgElencoAnagrafico = row.getAttribute("ROW.PRGELENCOANAGRAFICO");
				Object cdnlavoratore = row.getAttribute("ROW.CDNLAVORATORE");
				if (cdnlavoratore == null) {
					cdnlavoratore = serviceRequest.getAttribute("cdnLavoratore");
				}
				if (!serviceRequest.containsAttribute("prgElencoAnagrafico")) {
					serviceRequest.setAttribute("prgElencoAnagrafico", prgElencoAnagrafico);
				}
				setSectionQueryUpdate("QUERY_UPDATE");
				doUpdate(serviceRequest, serviceResponse);

				String prgPatto = StringUtils.getAttributeStrNotNull(serviceRequest, "prgPatto");
				String numKloPatto = StringUtils.getAttributeStrNotNull(serviceRequest, "numKloPatto");

				// riapro il patto associato alla DID se rambe sono diverse
				// dalla stringa vuota
				if (!(prgPatto.equals("") || numKloPatto.equals(""))) {
					DBStore.riapriPatto(new BigDecimal(prgPatto), null,
							new BigDecimal(numKloPatto).add(new BigDecimal("1")), getRequestContainer(), txExecutor);
				}
				// ricstruisco la storia del lavoratore
				String datDichiarazione = (String) serviceRequest.getAttribute("datDichiarazione");
				StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
						.newInstance((String) serviceRequest.getAttribute("cdnLavoratore"), datDichiarazione,
								txExecutor)
						.calcolaImpatti();

				SourceBean did = DBLoad
						.getDID(StringUtils.getAttributeStrNotNull(serviceRequest, "prgDichDisponibilita"), txExecutor);
				// se lo stato occupazionale è Altro o Occupato, la did è stata
				// riaperta è chiusa
				if (statoOccupazionale != null
						&& (statoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_A
								|| statoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_O)
						&& !((String) did.getAttribute("CODSTATOATTO")).equals("PA")) {
					serviceResponse.setAttribute("NON_RIAPERTA", "true");
				} else {
					String datFine = did.getAttribute("DATFINE") != null ? did.getAttribute("DATFINE").toString() : "";
					String codStatoAtto = did.getAttribute("CODSTATOATTO") != null
							? did.getAttribute("CODSTATOATTO").toString()
							: "";
					if (codStatoAtto.equalsIgnoreCase(DidBean.STATO_PROTOCOLLATO) && datFine.equals("")) {
						Boolean resAut = null;
						Object paramsGetA02[] = new Object[3];
						paramsGetA02[0] = cdnlavoratore;
						paramsGetA02[1] = datDichiarazione;
						paramsGetA02[2] = datDichiarazione;
						SourceBean rowAzione = (SourceBean) txExecutor
								.executeQuery("GET_AZIONE_PRESA_CARICO_150_RIAPRI_DID", paramsGetA02, "SELECT");
						rowAzione = rowAzione.containsAttribute("ROW") ? (SourceBean) rowAzione.getAttribute("ROW")
								: rowAzione;
						BigDecimal numAzA02 = (BigDecimal) rowAzione.getAttribute("numazioni");
						if (numAzA02 != null && numAzA02.intValue() > 0) {
							Object paramsA02[] = new Object[4];
							paramsA02[0] = cdnUser;
							paramsA02[1] = cdnlavoratore;
							paramsA02[2] = datDichiarazione;
							paramsA02[3] = datDichiarazione;
							resAut = (Boolean) txExecutor.executeQuery("RIAPRI_AZIONE_PRESA_CARICO_150_RIAPRI_DID",
									paramsA02, "UPDATE");
						}
					}
				}

				txExecutor.commitTransaction();

				reportOperation.reportSuccess(MessageCodes.DID.RIAPERTURA_AVVENUTA);

			} catch (MobilitaException me) {
				if (txExecutor != null) {
					txExecutor.rollBackTransaction();
				}
				if (!serviceResponse.containsAttribute("CONFERMA_OPERAZIONE")) {
					serviceResponse.setAttribute("CONFERMA_OPERAZIONE", "true");
				}
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in RiapriDid.", (Exception) me);

				addConfirm(serviceRequest, serviceResponse, me.getCode());
			}

			catch (ControlliException ce) {
				if (txExecutor != null) {
					txExecutor.rollBackTransaction();
				}
				if (!serviceResponse.containsAttribute("CONFERMA_OPERAZIONE")) {
					serviceResponse.setAttribute("CONFERMA_OPERAZIONE", "true");
				}
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in RiapriDid.", (Exception) ce);

				addConfirm(serviceRequest, serviceResponse, ce.getCode());
			}

			catch (ProTrasfoException proTrasfEx) {
				if (txExecutor != null) {
					txExecutor.rollBackTransaction();
				}
				serviceResponse.setAttribute("NON_RIAPERTA", "true");
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella ricostruzione della storia.",
						(Exception) proTrasfEx);
				int code = proTrasfEx.getCode();
				if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
					reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA);
				} else if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
					reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA);
				}

			} catch (Exception e) {
				if (txExecutor != null) {
					txExecutor.rollBackTransaction();
				}
				serviceResponse.setAttribute("NON_RIAPERTA", "true");
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
				it.eng.sil.util.TraceWrapper.debug(_logger, "RiapriDid.service()", e);

			}
		}
	}

	public List addWarning(int code, SourceBean serviceRequest) throws Exception {
		ArrayList warnings = null;
		if (serviceRequest.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE))
			warnings = (ArrayList) serviceRequest.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
		else {
			warnings = new ArrayList();
			serviceRequest.setAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE, warnings);
		}
		boolean esisteWarning = false;
		Warning objWarning = null;
		for (int i = 0; i < warnings.size(); i++) {
			Object objList = warnings.get(i);
			if (objList instanceof Warning) {
				objWarning = (Warning) objList;
				if (objWarning != null && objWarning.getCode() == code) {
					esisteWarning = true;
					break;
				}
			}
		}
		if (!esisteWarning) {
			warnings.add(new Warning(code, ""));
		}
		return warnings;
	}

	private void addConfirm(SourceBean request, SourceBean response, int code) throws Exception {
		String forzaRicostruzione = "";
		String continuaRicalcoloSOccManuale = "";
		ArrayList warnings = new ArrayList();
		ArrayList nested = new ArrayList();
		switch (code) {
		case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
			continuaRicalcoloSOccManuale = "true";
			if (request.containsAttribute("FORZA_INSERIMENTO")) {
				forzaRicostruzione = request.getAttribute("FORZA_INSERIMENTO").toString();
			}
			break;
		default:
			forzaRicostruzione = "true";
			if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				continuaRicalcoloSOccManuale = request.getAttribute("CONTINUA_CALCOLO_SOCC").toString();
			}

		}
		SourceBean puResult = ProcessorsUtils.createResponse("", "RiapriDid", new Integer(code),
				"Situazione di incongruenza", warnings, nested);
		ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiOperazione",
				new String[] { forzaRicostruzione, continuaRicalcoloSOccManuale }, true);
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

}