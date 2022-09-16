package it.eng.sil.module.ido;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

public class CiclaGenericSelect extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CiclaGenericSelect.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		String prgAzienda = "";
		int count = 0;
		String StrCount = "";
		int numero = 0;
		SourceBean rowVec = null;

		SourceBean row = doSelect(request, response);

		if (!row.containsAttribute("ROW")) {
			reportOperation.reportFailure(MessageCodes.Pubblicazioni.ERR_PUBB);
		}

		else {
			StrCount = (String) request.getAttribute("count");
			if (StrCount == null)
				StrCount = "0";
			else {
				Integer calcola = Integer.valueOf(StrCount);
				count = calcola.intValue();
			}
			String maxNum = ((BigDecimal) request.getAttribute("maxNum")).toString();
			Integer progressivo = Integer.valueOf(maxNum);
			numero = progressivo.intValue();
			Vector vecPubb = response.getAttributeAsVector("ROWS.ROW");
			response.clearBean();

			if (count == numero) {
				count = 0;
				StrCount = String.valueOf(0);
			}
			row = (SourceBean) vecPubb.get(count);

			try {
				response.setAttribute(row);
				response.setAttribute("count", StrCount);

			} catch (SourceBeanException e) {
				_logger.fatal(className + "::service: " + e.getMessage());

			}
		}
	}
}
