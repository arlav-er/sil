package it.eng.sil.module.profil;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicRicGruppi implements IDynamicStatementProvider {
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String statement = SQLStatements.getStatement("PROF_LISTA_GRUPPI");
		StringBuffer buf = new StringBuffer(statement);

		SourceBean serviceReq = requestContainer.getServiceRequest();

		String denominazione = (String) serviceReq.getAttribute("STRDENOMINAZIONERIC");
		String tipoGruppo = (String) serviceReq.getAttribute("TIPOGRUPPORIC");
		String flagStandard = (String) serviceReq.getAttribute("FLGSTANDARDRIC");

		if (denominazione != null || tipoGruppo != null || flagStandard != null) {
			if (!denominazione.equals("") || !tipoGruppo.equals("") || !flagStandard.equals("")) {
				// buf.append(" where ");

				if (!denominazione.equals("")) {
					String denTemp = StringUtils.replace(denominazione, "'", "''");
					buf.append(" AND UPPER(A.strdenominazione) like '%" + denTemp.toUpperCase() + "%' ");
				}

				if (!tipoGruppo.equals("")) {
					buf.append(" AND A.CDNTIPOGRUPPO =" + tipoGruppo);
				}

				if (!flagStandard.equals("")) {
					buf.append(" AND a.flgstandard='" + flagStandard + "' ");
				}
			}
		}
		buf.append(" ORDER BY A.STRDENOMINAZIONE");

		return buf.toString();
	}
}