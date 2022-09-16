package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class GetDispo extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetDispo.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		ResponseContainer responseContainer = getResponseContainer();
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		SourceBean statement = null;
		boolean prgPresente = true;
		boolean ripetiOperazione = false;
		boolean visDidApertaDopoAnn = false;
		boolean didNonRiaperta = false;
		// controllo se sto forzando l'operazione di riapertura did o
		// annullamento did
		ripetiOperazione = (serviceResponse.containsAttribute("M_RiapriDid.CONFERMA_OPERAZIONE"));
		visDidApertaDopoAnn = serviceResponse.containsAttribute("M_AnnullaDid.GET_DID_APERTA_DOPO_ANNULLAMENTO");
		didNonRiaperta = serviceResponse.containsAttribute("M_RiapriDid.NON_RIAPERTA");
		if (ripetiOperazione) {
			prgPresente = false;
		} else {
			// Se si è in aggiornamento o in inserimento
			if (!request.containsAttribute("prgDichDisponibilita") || request.containsAttribute("Salva")
					|| visDidApertaDopoAnn || didNonRiaperta) {
				/*
				 * else { statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_CDNLAVORATORE");
				 */
				statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_PRGDICDISPO_CDNLAVORATORE");
				SourceBean rowsDichDispo = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
						getResponseContainer(), pool, statement, "SELECT");
				if (rowsDichDispo != null && rowsDichDispo.containsAttribute("ROW.PRGDICHDISPONIBILITA")
						&& !rowsDichDispo.getAttribute("ROW.PRGDICHDISPONIBILITA").equals("")) {
					BigDecimal prgDichDispo = (BigDecimal) rowsDichDispo.getAttribute("ROW.PRGDICHDISPONIBILITA");
					if (prgDichDispo != null) {
						try {
							request.delAttribute("PRGDICHDISPONIBILITA");
							request.setAttribute("PRGDICHDISPONIBILITA", prgDichDispo.toString());
						} catch (SourceBeanException sbEx) {
							it.eng.sil.util.TraceWrapper.fatal(_logger,
									className + "::select: response.setAttribute((SourceBean)rowObject)", sbEx);

							prgPresente = false;
						}
					}
				} else
					prgPresente = false;
			}
		}

		if (prgPresente) {
			statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_PROGRESSIVO");

			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), pool, statement, "SELECT");

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

			Object rowObject = null;

			try {
				response.setAttribute(rowsSourceBean);
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

			} // catch (Exception ex)
		}
	}// end service

}// class GetDispo
