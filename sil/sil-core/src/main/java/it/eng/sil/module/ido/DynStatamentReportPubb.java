package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

public class DynStatamentReportPubb implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "SELECT distinct cdnut FROM TS_PROFILATURA_UTENTE A";
	private static final String SELECT_SQL_TUTTE = "SELECT distinct cdnut FROM TS_UTENTE A";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String utric = (String) req.getAttribute("UTRIC");
		String cdnut = (String) req.getAttribute("UTENTE");
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		StringBuffer buf = new StringBuffer();
		StringBuffer query_totale = null;
		if (utric.equals("TUTTE")) {
			query_totale = new StringBuffer(SELECT_SQL_TUTTE);
		} else {
			query_totale = new StringBuffer(SELECT_SQL_BASE);
		}

		if ((utric != null) && ((utric.equals("MIE")) || (utric.equals("GRUP")))) {
			buf.append(" WHERE ");
			if (utric.equals("MIE"))
				buf.append(" CDNUT = " + cdnut);
			if (utric.equals("GRUP"))
				buf.append(" CDNUT IN " + "(SELECT distinct cdnut FROM TS_PROFILATURA_UTENTE A " + "WHERE cdngruppo="
						+ user.getCdnGruppo() + ")");
		}

		// buf.append(" order by az.strRagioneSociale, raz.prgRichiestaAz,
		// datRichiesta ");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}