package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaDBFInviatiNoApi extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaNullaOsta.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String tipoFile = "";
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			setStrDescrizione("Mobilita Esportate");
			tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("MobilitaEsportate." + tipoFile);
			else
				setStrNomeDoc("MobilitaEsportate.pdf");
			setReportPath("Amministrazione/MobilitaEsportate_CC.rpt");

			// impostazione parametri del report
			Map prompts = new HashMap();

			RequestContainer requestContainer = getRequestContainer();
			SessionContainer session = requestContainer.getSessionContainer();
			String strSessId = (String) session.getAttribute("ID_SESSIONE_STAMPA");

			prompts.put("strSessId", strSessId);

			// solo se e' richiesta la protocollazione i parametri vengono
			// inseriti nella Map
			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "StampaMobilitaDettaglio.service()", "");
			}

			// ora si chiede di usare il passaggio dei parametri per nome e
			// non per posizione (col vettore, passaggio di default)
			setPromptFields(prompts);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);
			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			try {
				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					insertDocument(request, response);
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					showDocument(request, response);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
				return;
			}

			/* Aggiorno quelli esportati */
			doUpdateExported();
		}
	}

	/* Una volta esportati e stampati, devo registrarli come inviati */
	public void doUpdateExported() {

		String pool = getPool();
		SourceBean statement = getSelectStatement("QUERY_UPD_APPOGGIO");

		QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement, "UPDATE");
		try {
			LogUtils.logDebug("doUpdateExported", "update dbf esportati completo", this);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::doSelect()", ex);

		}
	}

	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	/**
	 * 
	 */
	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}
}
