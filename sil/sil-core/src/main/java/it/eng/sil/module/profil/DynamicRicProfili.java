package it.eng.sil.module.profil;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicRicProfili implements IDynamicStatementProvider {
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String statement = SQLStatements.getStatement("PROF_LISTA_PROFILI");
		StringBuffer buf = new StringBuffer(statement);

		SourceBean serviceReq = requestContainer.getServiceRequest();

		String denominazione = (String) serviceReq.getAttribute("denominazione");
		String flagStandard = (String) serviceReq.getAttribute("FlagStandard");

		if (denominazione != null && flagStandard != null) {
			if (!denominazione.equals("") || !flagStandard.equals("")) {
				buf.append(" where ");

				if (!denominazione.equals("")) {
					buf.append("UPPER(strdenominazione) like '%" + denominazione.toUpperCase() + "%' ");
				}

				if (!flagStandard.equals("")) {
					if (!denominazione.equals(""))
						buf.append(" and ");

					buf.append("flgstandard='" + flagStandard + "' ");
				}
			}
		}
		buf.append(" order by strdenominazione");

		return buf.toString();
	}
}