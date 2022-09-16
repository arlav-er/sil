package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.webservices.sifer.ErroreSifer;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.sifer.EnteAccreditatoUtils;

public class AccreditamentoArea1 extends AbstractSimpleModule {

	private static final long serialVersionUID = -427006718869451470L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccreditamentoArea1.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		String strCdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
		BigDecimal cdnLavoratore = null;
		if (strCdnLavoratore != null && !strCdnLavoratore.equals("")) {
			try {
				cdnLavoratore = new BigDecimal(strCdnLavoratore);
			} catch (NumberFormatException e) {
				_logger.error("AccreditamentoArea1: impossibile recuperare cdnlavoratore");
				return;
			}
		} else {
			_logger.error("AccreditamentoArea1: impossibile recuperare cdnlavoratore");
			return;
		}

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();

		setSectionQuerySelect("GET_ANAG_LAV");
		SourceBean anLavBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_CODPROVINCIA");
		SourceBean codProvinciaBeanRows = doSelect(serviceRequest, serviceResponse, false);
		setSectionQuerySelect("GET_DATIPROFILING_PATTO");
		SourceBean pattiBeanRows = doSelect(serviceRequest, serviceResponse, false);

		// setSectionQuerySelect("GET_ACCREDITAMENTO_CRED_WS");
		// SourceBean wsRows = doSelect(serviceRequest, serviceResponse, false);

		ErroreSifer erroreSifer = EnteAccreditatoUtils.sendFlussoEnteAccreditato(cdnLavoratore, anLavBeanRows,
				codProvinciaBeanRows, pattiBeanRows, EnteAccreditatoUtils.AREA1);

		// traccio errore in DB
		DataConnection dc = null;
		String dettaglioErrore = "";

		try {
			QueryExecutorObject qExec = EnteAccreditatoUtils.getQueryExecutorObject();
			dc = qExec.getDataConnection();
			EnteAccreditatoUtils.tracciaErrore(dc.getInternalConnection(), cdnLavoratore, erroreSifer);
			if (erroreSifer.erroreEsteso != null) {
				dettaglioErrore = erroreSifer.erroreEsteso;
			}
		} catch (Throwable e) {
			_logger.error("AccreditamentoArea1: impossibile tracciare l'errore", (Exception) e);
		} finally {
			if (dc != null) {
				dc.close();
			}
		}

		if (erroreSifer.errCodDB == 0) {
			reportOperation.reportSuccess(MessageCodes.SIFERACCREDITAMENTO.WS_OK);
		} else {
			reportOperation.reportFailure(erroreSifer.errCod, "", "", dettaglioErrore, erroreSifer.params);
		}
		return;
	}

}
