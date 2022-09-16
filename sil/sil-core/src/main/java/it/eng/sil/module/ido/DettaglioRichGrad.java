/*
 * Creato il 24-ott-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DettaglioRichGrad extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DettaglioRichGrad.class.getName());

	private final String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + ".service() INIZIO");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			String pool = getPool();
			SourceBean queries = (SourceBean) getConfig().getAttribute("QUERIES");
			RequestContainer reqCont = getRequestContainer();
			ResponseContainer resCont = getResponseContainer();

			SourceBean queryRich = (SourceBean) queries.getAttribute("QUERY_SELECT");
			SourceBean richRow = (SourceBean) QueryExecutor.executeQuery(reqCont, resCont, pool, queryRich, "SELECT");

			SourceBean richiesta = new SourceBean("RICHIESTA");
			richiesta.setAttribute(richRow);
			response.setAttribute(richiesta);

			String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(richRow, "ROW.CODSTATOATTO_P");
			/*
			 * if (!codStatoAtto.equals("PA")) { SourceBean queryDoc = (SourceBean)
			 * queries.getAttribute("QUERY_SELECT_DOC"); SourceBean docRow = (SourceBean)
			 * QueryExecutor.executeQuery(reqCont, resCont, pool, queryDoc, "SELECT");
			 * 
			 * SourceBean documenti = new SourceBean("DOCUMENTO"); documenti.setAttribute(docRow);
			 * response.setAttribute(documenti); }
			 */
			response.setAttribute(SELECT_OK, "TRUE");

			_logger.debug("service() FINE");

		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "method failed");

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

		}
	}
}
