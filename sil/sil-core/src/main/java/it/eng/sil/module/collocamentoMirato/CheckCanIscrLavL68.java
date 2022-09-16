/*
 * Creato il 7-mag-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CheckCanIscrLavL68 extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckCanIscrLavL68.class.getName());

	public void service(SourceBean request, SourceBean response) {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		// int msgCode = MessageCodes.General.GET_ROW_FAIL;
		// ReportOperationResult reportOperation = new
		// ReportOperationResult(this, response);
		SourceBean statoOccupazRagg = doSelect(request, response);
		try {
			String codstatooccupazragg = (String) statoOccupazRagg.getAttribute("row.codstatooccupazragg");
			if (codstatooccupazragg.equals("I") || codstatooccupazragg.equals("D")) {
				response.setAttribute("IscrizionePossibile", "true");
			} else {
				response.setAttribute("IscrizionePossibile", "false");
			}

			if (request.containsAttribute("CDNLAVINCHIARO")) {
				// provengo dalla lista dettaglio
				String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVINCHIARO");
				User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
				// Controllo residenza e cpi utente loggato
				Object[] params = new Object[] { cdnLavoratore, user.getCodRif() };
				SourceBean rowResidenza = (SourceBean) QueryExecutor.executeQuery("GET_RESIDENZALAV_CPI_UTENTE", params,
						"SELECT", Values.DB_SIL_DATI);
				if (rowResidenza != null) {
					rowResidenza = rowResidenza.containsAttribute("ROW") ? (SourceBean) rowResidenza.getAttribute("ROW")
							: rowResidenza;
					int numCount = new Integer(rowResidenza.getAttribute("numCount").toString()).intValue();
					if (numCount == 0) {
						response.setAttribute("IscrizioneFuoriResidenza", "true");
					}
				}
			}

		} catch (Exception e) {
			// reportOperation.reportFailure(msgCode, e , "services()",
			// "Impossibile sapere lo stato occupazionale del lavoratore");
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile sapere lo stato occupazionale del lavoratore", e);

		}
	}
}