package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

public class InserisciPeriodoInterm extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InserisciPeriodoInterm.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		disableMessageIdFail();
		disableMessageIdSuccess();
		String dataInizioPeriodo = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInizioPeriodo");
		String dataFinePeriodo = StringUtils.getAttributeStrNotNull(serviceRequest, "dataFinePeriodo");
		if (DateUtils.compare(dataInizioPeriodo, dataFinePeriodo) > 0) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
			reportOperation.reportFailure(MessageCodes.ImportMov.ERR_GESTIONE_DATE_PERIODI_INTERMITTENTI);
		} else {
			setSectionQuerySelect("QUERY_INFO_MOVIMENTO");
			SourceBean movimento = doSelect(serviceRequest, serviceResponse, false);
			movimento = movimento.containsAttribute("ROW") ? (SourceBean) movimento.getAttribute("ROW") : movimento;
			String dataInizioMov = StringUtils.getAttributeStrNotNull(movimento, "datInizioMov");
			String dataFineMov = StringUtils.getAttributeStrNotNull(movimento, "datfinerapporto");

			if ((DateUtils.compare(dataInizioPeriodo, dataInizioMov) < 0)
					|| (!dataFineMov.equals("") && DateUtils.compare(dataInizioPeriodo, dataFineMov) > 0)
					|| (!dataFineMov.equals("") && DateUtils.compare(dataFinePeriodo, dataFineMov) > 0)) {
				ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
				reportOperation.reportFailure(MessageCodes.ImportMov.ERR_GESTIONE_DATE_PERIODI_INTERMITTENTI);
			} else {
				setSectionQuerySelect("QUERY_INFO_PERIODI");
				SourceBean periodo = doSelect(serviceRequest, serviceResponse, false);
				boolean check = true;
				Vector periodi = periodo.getAttributeAsVector("ROW");
				if (periodi != null && periodi.size() > 0) {
					int nPeriodi = periodi.size();
					for (int i = 0; (i < nPeriodi && check); i++) {
						SourceBean row = (SourceBean) periodi.get(i);
						String dataInizioPer = StringUtils.getAttributeStrNotNull(row, "DATAINIZIO");
						String dataFinePer = StringUtils.getAttributeStrNotNull(row, "DATAFINE");
						if ((DateUtils.compare(dataInizioPeriodo, dataInizioPer) >= 0
								&& DateUtils.compare(dataInizioPeriodo, dataFinePer) <= 0)
								|| (DateUtils.compare(dataFinePeriodo, dataInizioPer) >= 0
										&& DateUtils.compare(dataFinePeriodo, dataFinePer) <= 0)) {
							check = false;
						}
					}
				}

				ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
				if (check) {
					// CALCOLO GIORNI INTERMITTENTI
					int giorniIntermittentiCalc = MovimentoBean.calcolaGGPeriodo(dataInizioPeriodo, dataFinePeriodo);
					BigDecimal ggIntermittenti = new BigDecimal(giorniIntermittentiCalc);
					serviceRequest.setAttribute("NUMGGINTERMITTENTICALC", ggIntermittenti);
					setSectionQueryInsert("QUERY_INSERT_PERIODO");
					boolean ok = doInsert(serviceRequest, serviceResponse);
					if (ok) {
						reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
					} else {
						reportOperation.reportSuccess(MessageCodes.General.OPERATION_FAIL);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.ImportMov.ERR_GESTIONE_DATE_PERIODI_INTERMITTENTI);
				}
			}
		}
	}

}