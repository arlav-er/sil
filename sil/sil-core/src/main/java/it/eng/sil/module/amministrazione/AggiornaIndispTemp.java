package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class AggiornaIndispTemp extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AggiornaIndispTemp.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean Request, SourceBean Response) throws Exception {
		String changes = (String) Request.getAttribute("associazioneLavPattoCambiata");
		ReportOperationResult result = new ReportOperationResult(this, Response);

		try {
			if (changes == null) {
				throw new Exception("il parametro associazioneLavPattoCambiata e' null");
			}

			switch (changes.charAt(0)) {
			case '0':
				doSimpleUpdate(Request, Response);

				break;

			case '1':
				doTransactionUpdate(Request, Response);

				break;

			default:
				throw new Exception("il valore di associazioneLavPattoCambiata=" + changes + " non e' ammesso");
			}

			result.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::update: request", Request);

		} catch (Exception e) {
			result.reportFailure(MessageCodes.General.UPDATE_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::update:", e);

		}
	}

	protected void doSimpleUpdate(SourceBean Request, SourceBean Response) throws Exception {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_IND_T");

		Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "UPDATE");

		if ((esito == null) || (!esito.booleanValue())) {
			throw new Exception("impossibile aggiornare la indisponibilità temporanea");
		}
	}

	protected void doTransactionUpdate(SourceBean Request, SourceBean Response) throws Exception {
		String prgPattoLavoratore = (String) Request.getAttribute("PRG_PATTO_LAVORATORE");
		TransactionQueryExecutor queryExecutor = null;

		if (prgPattoLavoratore == null) {
			throw new Exception("il prgpattolavoratore e' null");
		}

		try {
			if (prgPattoLavoratore.equals("")) {
				// cancella la associazione al patto
				SourceBean stmUp = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_IND_T");
				SourceBean stmDel = (SourceBean) getConfig().getAttribute("QUERIES.DEL_ASSOCIAZIONE_PATTO");
				String pool = (String) getConfig().getAttribute("POOL");
				queryExecutor = new TransactionQueryExecutor(pool);

				// ReportOperationResult result = new
				// ReportOperationResult(this, Response);
				queryExecutor.initTransaction();

				Boolean res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						stmDel, "DELETE");

				if (!res.booleanValue()) {
					throw new Exception("impossibile cancellare la associazione al patto");
				}

				res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmUp,
						"UPDATE");

				if (!res.booleanValue()) {
					throw new Exception("impossibile aggiornare la indisponibilità temporanea");
				}

				queryExecutor.commitTransaction();
			} else {
				// inserisci una nuova associazione al patto
				SourceBean stmUp = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_IND_T");
				SourceBean insPatto = (SourceBean) getConfig().getAttribute("QUERIES.INS_ASSOCIAZIONE_PATTO");
				// SourceBean stmPkPatto = (SourceBean)
				// getConfig().getAttribute("QUERIES.NEW_AM_LAV_PATTO_SCELTA");
				String pool = (String) getConfig().getAttribute("POOL");
				queryExecutor = new TransactionQueryExecutor(pool);
				queryExecutor.initTransaction();

				Boolean res = null;
				// SourceBean o=
				// (SourceBean)queryExecutor.executeQuery(getRequestContainer(),
				// getResponseContainer(),
				// stmPkPatto, "SELECT");
				// Vector v2 = (Vector)o.getAttributeAsVector("ROW");
				// if (v2.size()==0) throw new Exception("impossibile leggere la
				// chiave per am_lav_patto_scelta");
				// BigDecimal pk =
				// (BigDecimal)((SourceBean)v2.get(0)).getAttribute("PRGLAVPATTOSCELTA");
				// Request.delAttribute("PRG_LAV_PATTO_SCELTA");
				// Request.setAttribute("PRG_LAV_PATTO_SCELTA",pk);

				res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), insPatto,
						"INSERT");

				if (!res.booleanValue()) {
					throw new Exception("impossibile inserire la associazione in am_lav_patto_scelta");
				}

				res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmUp,
						"UPDATE");

				if (!res.booleanValue()) {
					throw new Exception("impossibile aggiornare la indisponibilita temporanea");
				}
				queryExecutor.commitTransaction();
			}
		} catch (Exception e) {
			queryExecutor.rollBackTransaction();
			throw e;
		}
	}
}