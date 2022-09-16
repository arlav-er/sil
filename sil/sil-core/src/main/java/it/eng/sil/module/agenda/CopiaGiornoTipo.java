package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.security.User;

public class CopiaGiornoTipo extends AbstractModule {
	private String className = this.getClass().getName();

	public CopiaGiornoTipo() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		String prgSettTipo = StringUtils.getAttributeStrNotNull(request, "PRGSETTIPO");
		String codCpi = StringUtils.getAttributeStrNotNull(request, "CODCPI");
		String gCopia = StringUtils.getAttributeStrNotNull(request, "GCOPIA");
		String gDest = StringUtils.getAttributeStrNotNull(request, "GDEST");
		String retCod = "0";

		boolean okSlot = false;
		boolean okIns = false;
		StringBuffer buf = new StringBuffer();

		DataConnectionManager dcm = null;
		DataConnection conn = null;
		SQLCommand stmt = null;
		DataResult res = null;
		ScrollableDataResult sdr = null;
		SourceBean rowsSourceBean = null;
		SourceBean row = null;

		int i = 0;
		try {
			dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			// Devo controllare che per il giorno selezionato non ci siano altri
			// slot tipo
			buf.append("SELECT to_char(COUNT(*)) AS NRO FROM AG_SLOT_TIPO WHERE CODCPI='" + codCpi + "'  and ");
			buf.append(" PRGSETTIPO=" + prgSettTipo);
			buf.append(" and NUMGIORNOSETT=" + gDest);
			stmt = conn.createSelectCommand(buf.toString());
			LogUtils.logDebug("CopiaGiornoTipo_Check", buf.toString(), this);
			res = stmt.execute();
			sdr = (ScrollableDataResult) res.getDataObject();
			rowsSourceBean = sdr.getSourceBean();
			String nro = StringUtils.getAttributeStrNotNull(rowsSourceBean, "ROW.NRO");
			if (nro.equals("0")) {
				okSlot = true;
			}
			if (okSlot) {
				statement = (SourceBean) getConfig().getAttribute("QUERY");
				esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
						statement, "INSERT");
				LogUtils.logDebug("CopiaGiornoTipo_Ins", "Copia giorno tipo", statement);
				if (esito.booleanValue() == true) {
					retCod = "0";
					ror.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
				} else {
					retCod = "-1";
					ror.reportFailure(MessageCodes.General.INSERT_FAIL);
				}
			} else {
				retCod = "1"; // Il giorno selezionato come destinazione non è
								// vuoto
			}

			row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", retCod);
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			ror.reportFailure(e, className, "Eccezione durante la cpia del giorno tipo");
		} finally {
			Utils.releaseResources(conn, stmt, res);
		}

	}
}
