package it.eng.sil.module.movimenti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author Alessandro Pegoraro
 * 
 *         Classe associata al modulo di forzatura movimenti. Espone una semplice interfaccia per modificare i
 *         riferimenti succ-prec e le date fine e inizio.
 * @since 2.8.4.5
 */
public class UpdateMovimentoForz extends AbstractSimpleModule {

	private static final long serialVersionUID = -5414073970693030643L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateMovimento.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		String prgMov = (String) serviceRequest.getAttribute("PRGMOV");
		String dataInizioMov = (String) serviceRequest.getAttribute("DATINIZIOMOV");
		String dataFineMov = (String) serviceRequest.getAttribute("DATFINEMOV");
		BigDecimal numKloMov = new BigDecimal((String) serviceRequest.getAttribute("NUMKLOMOV"));
		BigDecimal user = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");

		String movseg = (String) serviceRequest.getAttribute("MOVSEG");

		Boolean updateMov = new Boolean(false);

		String prgprec = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOPREC");
		String prgsucc = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOSUCC");

		String codmonofine = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOTIPOFINE");

		Object params[] = new Object[1];
		TransactionQueryExecutor transExec = null;

		try {
			// Preparazione della query di UPDATE, eventualmente sbianca i campi
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			params = new Object[10];

			params[0] = dataInizioMov;
			params[1] = dataInizioMov;
			params[2] = numKloMov;
			params[3] = user;
			params[4] = prgprec;
			params[5] = prgsucc;
			params[6] = dataFineMov;
			params[7] = dataFineMov;
			params[8] = movseg;
			params[9] = prgMov;

			updateMov = (Boolean) transExec.executeQuery("UPDATE_MOV_FORZ", params, "UPDATE");

			if (!updateMov.booleanValue()) {
				throw new Exception("Errore nell'aggiornamento");
			}

			StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
					.newInstance(cdnLavoratore, dataInizioMov, transExec).calcolaImpatti();

			transExec.commitTransaction();
			MessageAppender.appendMessage(serviceResponse, MessageCodes.General.UPDATE_SUCCESS);
			serviceResponse.setAttribute("ESITO", "OK");

		} catch (Exception ex) {
			try {
				if (transExec != null) {
					MessageAppender.appendMessage(serviceResponse, MessageCodes.General.UPDATE_FAIL);
					transExec.rollBackTransaction();
					serviceResponse.setAttribute("ESITO", "NO");
				}
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);

			}
		}

	}

}