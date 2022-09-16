package it.eng.sil.module.movimenti;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Utilizzata solo per la cancellazione dei movimenti dalla tabella di appoggio, quando si cancellano occorre
 * controllare che non vi siano eventuali movimenti collegati con il campo PRGMOVIMENTOAPPCVE.
 */
public class CancellaMovimento extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CancellaMovimento.class.getName());
	private String className;
	private int cdnUtente = -1;
	private StringBuffer bufQuery = new StringBuffer("INSERT INTO LG_AM_MOVIMENTO_APPOGGIO (\r\n");

	public CancellaMovimento() {
		className = this.getClass().getName();
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		SourceBean rows = null;
		Object prgMovAppCVE = null;
		Object prgMovAppCurr = null;
		Object prgMovApp = null;
		int index = 0;
		String listMovDelete = request.containsAttribute("PRGLISTAMOVAPP")
				? request.getAttribute("PRGLISTAMOVAPP").toString()
				: "";
		Vector vectMovDelete = StringUtils.split(listMovDelete, "#");
		MultipleTransactionQueryExecutor trans = null;
		String context = StringUtils.getAttributeStrNotNull(request, "CONTEXT");
		String nomeTabella = "";

		if (context.equals("validaArchivio")) {
			this.setSectionQuerySelect("QUERY_SELECT_ARC");
			this.setSectionQueryDelete("QUERY_DELETE_ARC");
			nomeTabella = "AM_MOV_APP_ARCHIVIO";
		} else {
			this.setSectionQuerySelect("QUERY_SELECT_APP");
			this.setSectionQueryDelete("QUERY_DELETE_APP");
			nomeTabella = "AM_MOVIMENTO_APPOGGIO";
		}

		try {
			trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			LogMovimentoAppoggio logMov = new LogMovimentoAppoggio(RequestContainer.getRequestContainer(), trans);
			logMov.loadColonneTabella(nomeTabella);
			if (vectMovDelete.size() > 0) {
				for (int i = 0; i < vectMovDelete.size(); i++) {
					try {
						trans.initTransaction();
						enableTransactions(trans);
						prgMovAppCurr = vectMovDelete.get(i);
						logMov.lg_Movimento_App_Cancellato(prgMovAppCurr);
						request.delAttribute("PRGMOVAPP");
						request.setAttribute("PRGMOVAPP", prgMovAppCurr);
						rows = doSelect(request, response);
						prgMovAppCVE = rows.getAttribute("ROW.PRGMOVIMENTOAPPCVE");
						if (prgMovAppCVE != null) {
							// Imposto il valore del progressivo nel record
							// collegato
							request.delAttribute("PRGMOVAPP");
							request.setAttribute("PRGMOVAPP", prgMovAppCVE);
							cancellaRecord(request, response, context);
							// Ripristino il progressivo originale
							request.delAttribute("PRGMOVAPP");
							request.setAttribute("PRGMOVAPP", prgMovAppCurr);
						}
						// cancello il record originale
						cancellaRecord(request, response, context);
						trans.commitTransaction();
					} catch (Exception e) {
						it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella cancellazione dei movimenti", e);
						trans.rollBackTransaction();
					}
				}
			} else {
				// Guardo se il campo PRGMOVIMENTOAPPCVE è valorizzato
				try {
					trans.initTransaction();
					enableTransactions(trans);
					rows = doSelect(request, response);
					prgMovApp = request.getAttribute("PRGMOVAPP");
					prgMovAppCVE = rows.getAttribute("ROW.PRGMOVIMENTOAPPCVE");
					logMov.lg_Movimento_App_Cancellato(prgMovApp);
					if (prgMovAppCVE != null) {
						// Imposto il valore del progressivo nel record
						// collegato
						request.delAttribute("PRGMOVAPP");
						request.setAttribute("PRGMOVAPP", prgMovAppCVE);
						cancellaRecord(request, response, context);
						// Ripristino il progressivo originale
						request.delAttribute("PRGMOVAPP");
						request.setAttribute("PRGMOVAPP", prgMovApp);
					}
					// cancello il record originale
					cancellaRecord(request, response, context);
					trans.commitTransaction();
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella cancellazione del movimento", e);
					trans.rollBackTransaction();
				}
			}
			trans.closeConnection();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile recuperare la connessione", e);
			if (trans != null) {
				trans.closeConnection();
			}
		}
	}

	private void cancellaRecord(SourceBean request, SourceBean response, String context) throws Exception {
		if (context.equals("validaArchivio")) {
			this.setSectionQueryDelete("QUERY_DELETE_AGEV_ARC");
		} else {
			this.setSectionQueryDelete("QUERY_DELETE_AGEV_APP");
		}

		doDelete(request, response);

		if (context.equals("validaArchivio")) {
			this.setSectionQueryDelete("QUERY_DELETE_ARC");
		} else {
			this.setSectionQueryDelete("QUERY_DELETE_APP");
		}

		doDelete(request, response);
	}

}