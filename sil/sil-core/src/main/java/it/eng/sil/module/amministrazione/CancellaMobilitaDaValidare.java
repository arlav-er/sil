package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;

public class CancellaMobilitaDaValidare extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CancellaMobilitaDaValidare.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.DELETE_RISULTATO_MOBILITA_APPOGGIO");
		SourceBean statement1 = (SourceBean) getConfig().getAttribute("QUERIES.DELETE_MOBILITA_APPOGGIO");
		MultipleTransactionQueryExecutor queryExecutor = null;
		String prgMobCanc = StringUtils.getAttributeStrNotNull(request, "PRGMOBAPPCANC");
		Vector vettPrgMob = null;
		Object prgMob = null;
		Boolean esito = null;
		if (!prgMobCanc.equals("")) {
			try {
				queryExecutor = new MultipleTransactionQueryExecutor(pool);
				vettPrgMob = StringUtils.split(prgMobCanc, "#");
				if (vettPrgMob.size() == 1) {
					eseguiCancellazione(request, response, queryExecutor, statement, statement1);
				} else {
					for (int i = 0; i < vettPrgMob.size(); i++) {
						prgMob = vettPrgMob.get(i);
						request.delAttribute("PRGMOBAPPCANC");
						request.setAttribute("PRGMOBAPPCANC", prgMob);
						eseguiCancellazione(request, response, queryExecutor, statement, statement1);
					}
				}
				queryExecutor.closeConnection();
			} catch (Exception e) {
				queryExecutor.closeConnection();
				it.eng.sil.util.TraceWrapper.debug(_logger, "::CancellaMobilitaDaValidare():", e);

			}
		}
	}

	private void eseguiCancellazione(SourceBean request, SourceBean response,
			MultipleTransactionQueryExecutor queryExecutor, SourceBean statement, SourceBean statement1)
			throws Exception {
		Boolean esito = null;
		try {
			queryExecutor.initTransaction();
			esito = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement,
					"DELETE");
			if (esito.booleanValue()) {
				esito = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement1,
						"DELETE");
				if (esito.booleanValue()) {
					queryExecutor.commitTransaction();
				} else {
					queryExecutor.rollBackTransaction();
				}
			} else {
				queryExecutor.rollBackTransaction();
			}
		} catch (Exception e) {
			queryExecutor.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.debug(_logger, "::eseguiCancellazione():", e);

		}
	}

}