/*
 * Creato il 29-giu-05
 *
 */
package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.DidBean;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;

/**
 * @author Togna Cosimo
 * 
 *         Questa classe implementa il modulo di annullamento della DID
 */
public class AnnullaDid extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AnnullaDid.class.getName());

	private TransactionQueryExecutor transactionExecutor;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		boolean annullataDidInResponse = true;
		boolean esisteDidSuccProtocollata = false;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();
			enableTransactions(transactionExecutor);

			// query che annulla la did
			setSectionQueryUpdate("QUERY_UPDATE");
			if (!doUpdate(serviceRequest, serviceResponse)) {
				throw new Exception("Impossibile annullare la did");
			}

			// se esiste un patto 150 associato alla did viene chiuso
			String prgPatto = StringUtils.getAttributeStrNotNull(serviceRequest, "prgPatto");
			Vector rowsDidSuccessive = DBLoad.getDidSuccessive(
					new BigDecimal(serviceRequest.getAttribute("prgdichdisponibilita").toString()),
					new BigDecimal(serviceRequest.getAttribute("cdnlavoratore").toString()),
					serviceRequest.getAttribute("datDichiarazione").toString(), transactionExecutor);
			if (rowsDidSuccessive.size() > 0) {
				SourceBean sbDidSucc = null;
				String codStatoAtto = "";
				for (int i = 0; i < rowsDidSuccessive.size(); i++) {
					sbDidSucc = (SourceBean) rowsDidSuccessive.get(i);
					codStatoAtto = (String) sbDidSucc.getAttribute(DidBean.DB_COD_STATO_ATTO);
					if (codStatoAtto != null && codStatoAtto.equalsIgnoreCase("PR")) {
						esisteDidSuccProtocollata = true;
					}
				}
			}

			if (!(prgPatto.equals(""))) {
				// chisura del patto associato alla did annullata
				if (rowsDidSuccessive.size() == 0 && !esisteDidSuccProtocollata) {
					DBStore.chiudiPatto297(StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore"),
							DateUtils.getNow(), StringUtils.getAttributeStrNotNull(serviceRequest, "statoAtto"),
							RequestContainer.getRequestContainer(), transactionExecutor);
				}
			}
			// ricalcolo impatti
			SituazioneAmministrativaFactory
					.newInstance(StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore"),
							StringUtils.getAttributeStrNotNull(serviceRequest, "datDichiarazione"), transactionExecutor)
					.calcolaImpatti();

			// Annulla il documento associato alla did
			if (serviceRequest.getAttribute("prgDocumento") != null
					&& !serviceRequest.getAttribute("prgDocumento").equals("")
					&& !((String) serviceRequest.getAttribute("prgDocumento")).equalsIgnoreCase("null")) {

				Documento documento = new Documento(
						new BigDecimal((String) serviceRequest.getAttribute("prgDocumento")));
				documento.setCodStatoAtto((String) serviceRequest.getAttribute("statoAtto"));
				documento.setCodMotAnnullamentoAtto((String) serviceRequest.getAttribute("motivoAnnullamento"));
				documento.setNumKloDocumento(documento.getNumKloDocumento().add(new BigDecimal("1")));
				// recupero utente di modifica documento
				RequestContainer requestContainer = getRequestContainer();
				SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
				User user = (User) sessionContainer.getAttribute(User.USERID);
				BigDecimal userid = new BigDecimal(user.getCodut());
				documento.setCdnUtMod(userid);
				documento.update(transactionExecutor);

			}
			//
			rowsDidSuccessive = DBLoad.getDIDLavoratore(
					new BigDecimal(serviceRequest.getAttribute("cdnlavoratore").toString()), transactionExecutor);
			if (rowsDidSuccessive.size() > 0) {
				annullataDidInResponse = false;
			}
			transactionExecutor.commitTransaction();

			if (annullataDidInResponse) {
				// settando il parametro "ANNULLATA" nel caricamento della
				// pagina
				// DispoDettaglioPage non viene eseguito il module GetDispo
				serviceResponse.setAttribute("ANNULLATA", "true");
			} else {
				// visualizzazione della did aperta (successiva o meno a quella
				// annullata)
				if (!serviceResponse.containsAttribute("GET_DID_APERTA_DOPO_ANNULLAMENTO"))
					serviceResponse.setAttribute("GET_DID_APERTA_DOPO_ANNULLAMENTO", "true");
			}
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		}

		catch (MobilitaException me) {
			transactionExecutor.rollBackTransaction();
			if (!serviceResponse.containsAttribute("GET_DID_APERTA_DOPO_ANNULLAMENTO"))
				serviceResponse.setAttribute("GET_DID_APERTA_DOPO_ANNULLAMENTO", "true");
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in AnnullaDichiarazione.", (Exception) me);

			addConfirm(serviceRequest, serviceResponse, me.getCode());
		}

		catch (ControlliException ce) {
			transactionExecutor.rollBackTransaction();
			if (!serviceResponse.containsAttribute("GET_DID_APERTA_DOPO_ANNULLAMENTO"))
				serviceResponse.setAttribute("GET_DID_APERTA_DOPO_ANNULLAMENTO", "true");
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in AnnullaDichiarazione.", (Exception) ce);

			addConfirm(serviceRequest, serviceResponse, ce.getCode());
		}

		catch (ProTrasfoException proTraEx) {
			transactionExecutor.rollBackTransaction();
			reportOperation.reportFailure(proTraEx.getCode());
			it.eng.sil.util.TraceWrapper.error(_logger, "AnnullaDid.service()", (Exception) proTraEx);

		}

		catch (Exception e) {
			transactionExecutor.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			it.eng.sil.util.TraceWrapper.error(_logger, "AnnullaDid.service()", e);

		}
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
		SourceBean puResult = ProcessorsUtils.createResponse("", "CalcolaStatoOccupazionale", new Integer(code),
				"Situazione di incongruenza", warnings, nested);
		ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiOperazione",
				new String[] { forzaRicostruzione, continuaRicalcoloSOccManuale }, true);
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

}