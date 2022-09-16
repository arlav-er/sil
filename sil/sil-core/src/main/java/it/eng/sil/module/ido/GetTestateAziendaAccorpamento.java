package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author Luigi Antenucci
 */
public class GetTestateAziendaAccorpamento extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetTestateAziendaAccorpamento.class.getName());

	private final String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + ".service() INIZIO");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {

			String pool = getPool();
			SourceBean queries = (SourceBean) getConfig().getAttribute("QUERIES");
			RequestContainer reqCont = getRequestContainer();
			ResponseContainer resCont = getResponseContainer();
			SourceBean dummy = new SourceBean("dummy");

			SourceBean queryTestata1 = (SourceBean) queries.getAttribute("SELECT_TESTATA1");
			SourceBean testata1rows = (SourceBean) QueryExecutor.executeQuery(reqCont, resCont, pool, queryTestata1,
					"SELECT");
			if (testata1rows == null)
				testata1rows = dummy;
			_logger.debug("testata 1 rows [" + testata1rows.toXML(false) + "]");

			SourceBean queryTestata2 = (SourceBean) queries.getAttribute("SELECT_TESTATA2");
			SourceBean testata2rows = (SourceBean) QueryExecutor.executeQuery(reqCont, resCont, pool, queryTestata2,
					"SELECT");
			if (testata2rows == null)
				testata2rows = dummy;
			_logger.debug("testata 2 rows [" + testata2rows.toXML(false) + "]");

			SourceBean queryUnita1 = (SourceBean) queries.getAttribute("SELECT_UNITA1");
			SourceBean unita1rows = (SourceBean) QueryExecutor.executeQuery(reqCont, resCont, pool, queryUnita1,
					"SELECT");
			if (unita1rows == null)
				unita1rows = dummy;
			_logger.debug("unita 1 rows [" + unita1rows.toXML(false) + "]");

			SourceBean queryUnita2 = (SourceBean) queries.getAttribute("SELECT_UNITA2");
			SourceBean unita2rows = (SourceBean) QueryExecutor.executeQuery(reqCont, resCont, pool, queryUnita2,
					"SELECT");
			if (unita2rows == null)
				unita2rows = dummy;
			_logger.debug("unita 2 rows [" + unita2rows.toXML(false) + "]");

			// Creo il SB di risposta

			// Metto in "1" quello con "DTMINS" minore!
			String dtmins1 = (String) testata1rows.getAttribute("ROW.DTMINS");
			String dtmins2 = (String) testata2rows.getAttribute("ROW.DTMINS");
			_logger.debug("confronto dtmins: 1=[" + dtmins1 + "] 2=[" + dtmins2 + "]");

			if (dtmins1 != null && dtmins2 != null) {
				int icomp = DateUtils.compare(dtmins1, dtmins2); // rende 1
																	// se date1
																	// > date2
				if (icomp == 1) {
					_logger.debug("ha 1 > 2: SCAMBIO tra loro le aziende");

					SourceBean tmp;
					// SWAPPING
					tmp = testata1rows;
					testata1rows = testata2rows;
					testata2rows = tmp;
					tmp = unita1rows;
					unita1rows = unita2rows;
					unita2rows = tmp;
				}
			}

			SourceBean testata1 = new SourceBean("TESTATA1");
			testata1.setAttribute(testata1rows);
			response.setAttribute(testata1);

			SourceBean testata2 = new SourceBean("TESTATA2");
			testata2.setAttribute(testata2rows);
			response.setAttribute(testata2);

			SourceBean unita1 = new SourceBean("UNITA1");
			unita1.setAttribute(unita1rows);
			response.setAttribute(unita1);

			SourceBean unita2 = new SourceBean("UNITA2");
			unita2.setAttribute(unita2rows);
			response.setAttribute(unita2);

			response.setAttribute(SELECT_OK, "TRUE");
			// non notifico se ok: reportSuccess(reportOperation);

			_logger.debug("service() FINE");

		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, ex, "service", "method failed");
		}
	}

}// class GetTestateAziendaAccorpamento
