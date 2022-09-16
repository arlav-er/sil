package it.eng.sil.module.consenso;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class VerificaConsenso extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(VerificaConsenso.class.getName());

	public void service(SourceBean request, SourceBean response) {

		_logger.info("MODULO GESTIONE CONSENSO");

		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		String esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		String cdnLavoratore = (String) request.getAttribute("cdnlavoratore");
		String prgAzienda = (String) request.getAttribute("prgazienda");
		String bottoneVerifica = (String) request.getAttribute("BTNVERIFICA");
		String bottoneInserisci = (String) request.getAttribute("BTNINSERISCI");
		String bottoneRevoca = (String) request.getAttribute("BTNREVOCA");
		SourceBean anLavoratore = doSelect(request, response);
		String codiceFiscaleLavoratore = (String) anLavoratore.getAttribute("ROW.strcodicefiscale");

		String dataRaccoltaOggi = (String) request.getAttribute("dataRaccoltaOggi");

		if (_logger.isDebugEnabled()) {
			_logger.debug("cdnLavoratore: " + cdnLavoratore);
			_logger.debug("codiceFiscaleLavoratore: " + codiceFiscaleLavoratore);
			_logger.debug("prgAzienda: " + prgAzienda);
			_logger.debug("user: " + user.getCodut());
		}

		/*
		 * ESEMPIO PIU' QUERY DA MODULE.XML NELLA STESSA TRANSAZIONE
		 */
		/*
		 * TransactionQueryExecutor transExec = new TransactionQueryExecutor(getPool()); enableTransactions(transExec);
		 * 
		 * setSectionQuerySelect("QUERY_GET_CF"); SourceBean anLavoratore2 = doSelect(request, response); String
		 * codiceFiscaleLavoratore2 = (String) anLavoratore.getAttribute("ROW.strcodicefiscale");
		 * 
		 * setSectionQuerySelect("QUERY_GET_CONSENSO"); SourceBean queryGetConsenso = doSelect(request, response);
		 * String cdnLavoratoreQ = (String) anLavoratore.getAttribute("ROW.cdnlavoratore");
		 * transExec.commitTransaction();
		 */

		try {

			/** Inizializza parametri **/
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("codiceFiscaleLavoratore", codiceFiscaleLavoratore);
			params.put("cdnLavoratore", cdnLavoratore);
			params.put("prgAzienda", prgAzienda);
			params.put("cdnUtins", user.getCodut());
			params.put("cdnUtmod", user.getCodut());
			params.put("dataRaccoltaOggi", dataRaccoltaOggi);

			Consenso consenso = new Consenso(params);

			try {

				if (StringUtils.isNotEmpty(bottoneVerifica)) {

					_logger.debug("GESTIONE CONSENSO -> VERIFICA");

					SourceBean resConsenso = consenso.verificaConsenso();
					setDataConsensoToResponse(resConsenso, result, response);

				} else if (StringUtils.isNotEmpty(bottoneInserisci)) {

					_logger.debug("GESTIONE CONSENSO -> INSERIMENTO");

					SourceBean resConsenso = consenso.inserisciConsenso();
					setDataConsensoToResponse(resConsenso, result, response);

				} else if (StringUtils.isNotEmpty(bottoneRevoca)) {

					_logger.debug("GESTIONE CONSENSO -> REVOCA");

					SourceBean resConsenso = consenso.revocaConsenso();
					setDataConsensoToResponse(resConsenso, result, response);

				}

			} catch (SourceBeanException e) {
				result.reportFailure(e, className, "Errore durante la verifica del consenso");
				esito = null;
				_logger.error(e);
			}

			// System.out.println("AM CONSENSO FIRMA: " +
			// ((ConsensoFirmaBean)consenso.getConsensoFirma(cdnLavoratore)).getCodiceStatoConsenso());

		} catch (RemoteException e) {
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
			esito = null;
			_logger.error("Error: " + e.getMessage(), e);
		} catch (ServiceException e) {
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
			esito = null;
			_logger.error("Error: " + e.getMessage(), e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setDataConsensoToResponse(SourceBean resConsenso, ReportOperationResult result, SourceBean response)
			throws SourceBeanException {

		if (resConsenso != null) {
			String code = (String) resConsenso.getAttribute("code");
			result.reportSuccess(getMessageCode(code));
			response.setAttribute("AzioneConsenso", code);

			if (resConsenso.getAttribute("dateRec") != "") {
				Date dataRec = (Date) resConsenso.getAttribute("dateRec");
				if (dataRec != null) {
					response.setAttribute("DataRegistrazione", dataRec);
				}
			}

			if (resConsenso.getAttribute("dateRev") != "") {
				Date dataRev = (Date) resConsenso.getAttribute("dateRev");
				if (dataRev != null) {
					response.setAttribute("DataRevoca", dataRev);
				}
			}

			_logger.info("Codice Verica Consenso: " + code);
		} else {
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
			_logger.error("Errore: esito Verica Consenso nullo");
		}

	}

	/***
	 * Recupera il codice del Messaggio relativo alla Verifica del Consenso ricevuta
	 * 
	 * 
	 * @param code
	 * @return
	 */
	private int getMessageCode(String code) {

		int messageCode = MessageCodes.GestioneConsenso.CONSENSO_NON_DISPONIBILE;

		if (code.equalsIgnoreCase(GConstants.CONSENSO_ASSENTE_CODICE)) {
			messageCode = MessageCodes.GestioneConsenso.CONSENSO_ASSENTE;
		} else if (code.equalsIgnoreCase(GConstants.CONSENSO_ATTIVO_CODICE)) {
			messageCode = MessageCodes.GestioneConsenso.CONSENSO_ATTIVO;
		} else if (code.equalsIgnoreCase(GConstants.CONSENSO_NON_DISPONIBILE_CODICE)) {
			messageCode = MessageCodes.GestioneConsenso.CONSENSO_NON_DISPONIBILE;
		} else if (code.equalsIgnoreCase(GConstants.CONSENSO_REVOCATO_CODICE)) {
			messageCode = MessageCodes.GestioneConsenso.CONSENSO_REVOCATO;
		}

		return messageCode;

	}

}
