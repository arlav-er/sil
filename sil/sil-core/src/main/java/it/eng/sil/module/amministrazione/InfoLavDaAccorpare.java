/*
 * Created on Jun 21, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author savino
 * 
 */
public class InfoLavDaAccorpare extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfoLavDaAccorpare.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		try {
			String cdnLavoratore1 = (String) serviceRequest.getAttribute("cdnLavoratore1");
			String cdnLavoratore2 = (String) serviceRequest.getAttribute("cdnLavoratore2");
			disableMessageIdSuccess();
			// lavoratore 1
			serviceRequest.updAttribute("cdnLavoratore", cdnLavoratore1);
			SourceBean lav1 = new SourceBean("LAV1");
			executeQuery("GET_INFOCORRENTIPOPUP", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_OBBLIGO_FORMATIVO", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_PERM_SOGG", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_STATO_OCCUPAZ", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_181_CAT", lav1, serviceRequest, serviceResponse);
			executeQuery("AMSTR_GET_MOBILITA_ISCR", lav1, serviceRequest, serviceResponse);
			executeQueryCM("AMSTR_GET_AM_CM_ISCR", lav1, serviceRequest, serviceResponse);
			executeQuery("AMSTR_GET_INDISP_TEMP", lav1, serviceRequest, serviceResponse);
			executeQuery("AMSTR_GET_MOVIMENTO", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_INFO_CORR_LAV_TITOLO_PRINCIPALE", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_INFOCORRENTICORSOLAV", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_INFO_ANNOTAZIONI", lav1, serviceRequest, serviceResponse);
			// lavoratore 2
			serviceRequest.updAttribute("cdnLavoratore", cdnLavoratore2);
			SourceBean lav2 = new SourceBean("LAV2");
			executeQuery("GET_INFOCORRENTIPOPUP", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_OBBLIGO_FORMATIVO", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_PERM_SOGG", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_STATO_OCCUPAZ", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_181_CAT", lav2, serviceRequest, serviceResponse);
			executeQuery("AMSTR_GET_MOBILITA_ISCR", lav2, serviceRequest, serviceResponse);
			executeQueryCM("AMSTR_GET_AM_CM_ISCR", lav2, serviceRequest, serviceResponse);
			executeQuery("AMSTR_GET_INDISP_TEMP", lav2, serviceRequest, serviceResponse);
			executeQuery("AMSTR_GET_MOVIMENTO", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_INFO_CORR_LAV_TITOLO_PRINCIPALE", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_INFOCORRENTICORSOLAV", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_INFO_ANNOTAZIONI", lav2, serviceRequest, serviceResponse);
			serviceResponse.setAttribute(lav1);
			serviceResponse.setAttribute(lav2);
		} catch (Exception e) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
			reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		}

	}

	private void executeQueryCM(String query, SourceBean lav, SourceBean serviceRequest, SourceBean serviceResponse)
			throws Exception {
		setSectionQuerySelect(query);
		SourceBean row = doSelect(serviceRequest, serviceResponse, false);
		if (row == null)
			throw new Exception("impossibile recuperare le informazioni relative a " + query + " per il lavoratore "
					+ lav.getName());
		Vector rowCM = row.getAttributeAsVector("ROW");
		if (rowCM != null && rowCM.size() > 1) {
			SourceBean ris = new SourceBean("ROWS");
			row = (SourceBean) rowCM.elementAt(0);
			ris.setAttribute(row);
			lav.setAttribute(query, ris);
		} else {
			lav.setAttribute(query, row);
		}
	}

	private void executeQuery(String query, SourceBean lav, SourceBean serviceRequest, SourceBean serviceResponse)
			throws Exception {
		setSectionQuerySelect(query);
		SourceBean row = doSelect(serviceRequest, serviceResponse, false);
		if (row == null)
			throw new Exception("impossibile recuperare le informazioni relative a " + query + " per il lavoratore "
					+ lav.getName());
		lav.setAttribute(query, row);
	}

}