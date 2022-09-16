package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class SalvaRosaGrezza extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SalvaRosaGrezza.class.getName());

	public SalvaRosaGrezza() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		String prgIncrocio = (String) request.getAttribute("PRGINCROCIO");
		String prgRosa = (String) request.getAttribute("PRGROSA");
		ReportOperationResult ror = new ReportOperationResult(this, response);
		TransactionQueryExecutor queryExecutor = null;

		try {
			if ((prgIncrocio == null) || (prgRosa == null)) {
				throw new Exception("uno dei valori, o entrambi, prgIncrocio e prgRosa sono nulli!");
			}
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean stmRosa = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_ROSA");
			// Savino 31/05/05 l'aggiornamento dello stato dell'incrocio e' ora
			// a carico della procedure del matching
			// SourceBean stmIncrocio = (SourceBean)
			// getConfig().getAttribute("QUERIES.QUERY_INCR");
			SourceBean stmRichiesta = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_RICH");
			queryExecutor = new TransactionQueryExecutor(pool);
			queryExecutor.initTransaction();
			// Update Stato Rosa
			Boolean ris = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmRosa,
					"UPDATE");
			if (!ris.booleanValue()) {
				throw new Exception("Impossibile salvare la rosa grezza");
			}
			// Update Stato Incrocio
			/*
			 * Savino 31/05/05 vedi commento piu' su' ris = (Boolean) queryExecutor.executeQuery(getRequestContainer(),
			 * getResponseContainer(), stmIncrocio, "UPDATE"); if(!ris.booleanValue()) { throw new
			 * Exception("Impossibile modificare lo stato dell'incrocio durante il salvataggio della rosa grezza"); }
			 */
			// Update Stato Richiesta Originale e Copia di Lavoro
			Boolean rs = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					stmRichiesta, "UPDATE");
			// Se sono stati fatti precedenti incroci allora il risultato di
			// "rs" potrebbe essere false, per cui
			// il test sul valore booleano non deve essere fatto
			/*
			 * if(!rs.booleanValue()) { throw new Exception("Impossibile modificare lo stato della richiesta durante il
			 * salvataggio della rosa grezza"); }
			 */
			queryExecutor.commitTransaction();

		} catch (Exception e) {
			queryExecutor.rollBackTransaction();
			ror.reportFailure(MessageCodes.General.UPDATE_FAIL);
			it.eng.sil.util.TraceWrapper.error(_logger, className + ":: transaction update failure: ", e);

		}
	}
}