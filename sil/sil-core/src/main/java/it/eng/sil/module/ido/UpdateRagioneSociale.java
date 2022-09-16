package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateRagioneSociale extends AbstractSimpleModule {
	final int LUNGHEZZA_CAMPO = 2000;

	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		SourceBean rowAzienda = (SourceBean) getResponseContainer().getServiceResponse()
				.getAttribute("M_GETTESTATAAZIENDA.ROWS.ROW");
		String dataCambio = StringUtils.getAttributeStrNotNull(request, "dataCambio");
		String dataComunic = StringUtils.getAttributeStrNotNull(request, "dataComunic");
		String strHistory = StringUtils.getAttributeStrNotNull(rowAzienda, "strHistory");
		// String strHistory = StringUtils.getAttributeStrNotNull(request,
		// "strHistory");
		String strRagioneSocialeOld = StringUtils.getAttributeStrNotNull(request, "STRRAGIONESOCIALE");
		String dataCambioNew = StringUtils.getAttributeStrNotNull(request, "dataCambioNew");
		try {
			strHistory += "\r\n" + dataCambio + " OGGETTO `CAMBIO RAGIONE SOCIALE` ";
			strHistory += strRagioneSocialeOld + " (comunicata il " + dataComunic + ")";
			if (strHistory.length() > LUNGHEZZA_CAMPO) {
				strHistory = strHistory.substring(0, LUNGHEZZA_CAMPO);
			}
			request.updAttribute("strHistory", strHistory);
			request.updAttribute("dataCambioNew", dataCambioNew);
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
		}
		doUpdate(request, response);
	}
}