package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

public class DelRipGSett extends AbstractModule {
	private String className = this.getClass().getName();

	private String dataFineDef = "01/01/2100";

	public DelRipGSett() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		// SessionContainer sessionContainer =
		// getRequestContainer().getSessionContainer();
		// User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		Boolean esitoIns = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		String prgGiornoNl = (String) request.getAttribute("PRGGIORNONL");
		String codCpi = (String) request.getAttribute("CODCPI");
		String tipoUpdf = (String) request.getAttribute("TIPOUPDF");

		int numGSett = Integer.parseInt(request.getAttribute("NUMGSETT").toString());
		String datInizioVal = StringUtils.getAttributeStrNotNull(request, "DATINIZIOVAL");
		String datFineVal = StringUtils.getAttributeStrNotNull(request, "DATFINEVAL");
		int giornoDB = Integer.parseInt(request.getAttribute("giornoDB").toString());
		int meseDB = Integer.parseInt(request.getAttribute("meseDB").toString());
		int annoDB = Integer.parseInt(request.getAttribute("annoDB").toString());

		String data = (String) request.getAttribute("data");

		Calendar gc = Calendar.getInstance();
		int g, m, a;

		int i = 0;
		try {

			if (tipoUpdf.equals("2")) {
				// UPDATE + INSERT
				// a. Update
				gc.set(annoDB, meseDB - 1, giornoDB);
				gc.add(Calendar.DATE, -1);
				g = gc.get(Calendar.DATE);
				m = gc.get(Calendar.MONTH) + 1;
				a = gc.get(Calendar.YEAR);
				request.updAttribute("DATFINEVAL", g + "/" + m + "/" + a);
				statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");
				esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
						statement, "UPDATE");
				LogUtils.logDebug("MDelRipetizione", "Caso2: UPD -Modifica Regola Riposo Settimanale", statement);
				// b. Insert
				gc.set(annoDB, meseDB - 1, giornoDB);
				gc.add(Calendar.DATE, 1);
				g = gc.get(Calendar.DATE);
				m = gc.get(Calendar.MONTH) + 1;
				a = gc.get(Calendar.YEAR);
				request.updAttribute("DATINIZIOVAL", g + "/" + m + "/" + a);
				request.updAttribute("DATFINEVAL", datFineVal);
				statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");
				esitoIns = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
						statement, "INSERT");
				LogUtils.logDebug("MDelRipetizione", "Caso 2: INS - Inserimento Regola Riposo Settimanale", statement);

				// fine if (tipoUpdf.equals("2"))
			} else {
				// UPDATE
				request.updAttribute("DATFINEVAL", data);
				statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");
				esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
						statement, "UPDATE");
			}
		} catch (Exception e) {
			esito = new Boolean(false);
			result.reportFailure(e, className, "Errore durante la cancellazione regola riposo settimanale");
		}

		if ((esito != null) && (esito.booleanValue() == true)) {
			result.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}
}
