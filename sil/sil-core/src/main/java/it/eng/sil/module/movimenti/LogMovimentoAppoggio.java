package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

/**
 * Utilizzata solo per la cancellazione dei movimenti dalla tabella di appoggio, quando si cancellano occorre
 * controllare che non vi siano eventuali movimenti collegati con il campo PRGMOVIMENTOAPPCVE.
 */
public class LogMovimentoAppoggio {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CancellaMovimento.class.getName());
	private int cdnUtente = -1;
	private StringBuffer bufQuery = null;
	private RequestContainer reqCont = null;
	private TransactionQueryExecutor trans = null;

	public LogMovimentoAppoggio(RequestContainer reqContainer, TransactionQueryExecutor transExec) {
		reqCont = reqContainer;
		trans = transExec;
		if (reqCont != null) {
			SessionContainer session = reqCont.getSessionContainer();
			if (session != null) {
				BigDecimal cdnUtenteObj = (BigDecimal) session.getAttribute("_CDUT_");
				if (cdnUtenteObj != null)
					cdnUtente = cdnUtenteObj.intValue();
			}
		}

	}

	public void lg_Movimento_App_Cancellato(Object prgMovApp) throws Exception {
		String queryFinal = "";
		try {
			String parteWhere = " WHERE PRGMOVIMENTOAPP = " + prgMovApp;
			queryFinal = bufQuery.toString() + parteWhere;
			Object result = null;
			result = trans.executeQueryByStringStatement(queryFinal, null, MultipleTransactionQueryExecutor.INSERT);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore scrittura nella tabella di log:" + queryFinal, e);
			throw e;
		}
	}

	public void loadColonneTabella(String nomeTabella) throws Exception {
		bufQuery = new StringBuffer("INSERT INTO LG_AM_MOVIMENTO_APPOGGIO (\r\n");
		String selectquery = "select c.COLUMN_NAME from cols c where c.table_name = '" + nomeTabella + "'";
		SourceBean result = null;
		result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
		Vector v = result.getAttributeAsVector("ROW");
		if (v.size() > 0) {
			for (int i = 0; i < v.size(); i++) {
				SourceBean row = (SourceBean) v.get(i);
				String nomeCol = row.getAttribute("COLUMN_NAME").toString();
				bufQuery.append(nomeCol + ",\r\n");
			}
			bufQuery.append("PRGLOG, STRTIPOOP, CDNUTLOG, DTMMODLOG )");
			bufQuery.append("SELECT \r\n");
			for (int i = 0; i < v.size(); i++) {
				SourceBean row = (SourceBean) v.get(i);
				String nomeCol = row.getAttribute("COLUMN_NAME").toString();
				bufQuery.append(nomeCol + ",\r\n");
			}
			bufQuery.append(" S_LG_AM_MOVIMENTO_APPOGGIO.NEXTVAL , 'D'," + cdnUtente + ", SYSDATE  \r\n");
			bufQuery.append("FROM " + nomeTabella + " \r\n");
		}
	}

}