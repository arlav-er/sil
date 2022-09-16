package it.eng.sil.module.ido;

import java.math.BigDecimal;

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

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.security.User;

public class EsclusioneMassivaRosa extends AbstractModule {
	private String className = this.getClass().getName();

	private String dataFineDef = "01/01/2100";

	public EsclusioneMassivaRosa() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		boolean esitoGl = true;
		ReportOperationResult result = new ReportOperationResult(this, response);

		DataConnectionManager dcm = null;
		DataConnection conn = null;
		SQLCommand stmt = null;
		DataResult res = null;
		ScrollableDataResult sdr = null;
		SourceBean rowsSourceBean = null;
		SourceBean row = null;
		StringBuffer buf = new StringBuffer();
		String sVPrgNominativo = "";
		// String vPrgNominativo[] = null;
		String numKloLav = "";
		String prgNominativo = "";
		BigDecimal num = new BigDecimal(0);

		int i = 0;
		try {
			dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			// vPrgNominativo = request.getAttributeAsVector("V_PRGNOMINATIVO");
			sVPrgNominativo = (String) request.getAttribute("V_PRGNOMINATIVO");
			String vPrgNominativo[] = StringtoArray(sVPrgNominativo, ",");
			if (vPrgNominativo != null && vPrgNominativo.length > 0) {
				for (i = 0; i < vPrgNominativo.length; i++) {
					prgNominativo = vPrgNominativo[i];
					buf = new StringBuffer(
							"select NUMKLONOMINATIVO FROM DO_NOMINATIVO where PRGNOMINATIVO=" + prgNominativo);
					stmt = conn.createSelectCommand(buf.toString());
					// LogUtils.logDebug("EsclusioneMassivaRosa",
					// buf.toString(), this);
					res = stmt.execute();
					sdr = (ScrollableDataResult) res.getDataObject();
					rowsSourceBean = sdr.getSourceBean();
					num = (BigDecimal) rowsSourceBean.getAttribute("ROW.NUMKLONOMINATIVO");
					num = num.add(new BigDecimal(1));
					numKloLav = num.toString();
					if (!numKloLav.equals("")) {
						statement = (SourceBean) getConfig().getAttribute("QUERY");
						request.updAttribute("NUMKLONOMINATIVO", numKloLav);
						request.updAttribute("PRGNOMINATIVO", prgNominativo);
						// LogUtils.logDebug("EsclusioneMassivaRosa",
						// "Esclusione lavoratore", statement);
						esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
								pool, statement, "UPDATE");
						if (esito.booleanValue() == false) {
							esitoGl = false;
						}
					} // if(!numKloLav.equals(""))
				} // for(i=0; i<vCdnLav.size(); i++)
			} // if(vCdnLav!=null && vCdnLav.size()>0)
		} catch (Exception e) {
			esitoGl = false;
			result.reportFailure(e, className, "Errore nell'esclusione massiva dei candidati");
		} finally {
			Utils.releaseResources(conn, stmt, res);
		}

		if (esitoGl) {
			result.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}

	private String[] StringtoArray(String s, String sep) {
		// convert a String s to an Array, the elements
		// are delimited by sep
		StringBuffer buf = new StringBuffer(s);
		int arraysize = 1;
		for (int i = 0; i < buf.length(); i++) {
			if (sep.indexOf(buf.charAt(i)) != -1)
				arraysize++;
		}
		String[] elements = new String[arraysize];
		int y, z = 0;
		if (buf.toString().indexOf(sep) != -1) {
			while (buf.length() > 0) {
				if (buf.toString().indexOf(sep) != -1) {
					y = buf.toString().indexOf(sep);
					if (y != buf.toString().lastIndexOf(sep)) {
						elements[z] = buf.toString().substring(0, y);
						z++;
						buf.delete(0, y + 1);
					} else if (buf.toString().lastIndexOf(sep) == y) {
						elements[z] = buf.toString().substring(0, buf.toString().indexOf(sep));
						z++;
						buf.delete(0, buf.toString().indexOf(sep) + 1);
						elements[z] = buf.toString();
						z++;
						buf.delete(0, buf.length());
					}
				}
			}
		} else {
			elements[0] = buf.toString();
		}
		buf = null;
		return elements;
	}

}