/*
 * Creato il 11-nov-04
 * Author: vuoto
 * 
 */
package it.eng.sil.module.menu;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author vuoto
 * 
 */
public class DynamicRicMenu implements IDynamicStatementProvider {
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String statement = SQLStatements.getStatement("LISTA_MENU");
		StringBuffer buf = new StringBuffer(statement);

		SourceBean serviceReq = requestContainer.getServiceRequest();

		String strdescrizione = (String) serviceReq.getAttribute("strdescrizione");

		if (strdescrizione != null && !strdescrizione.equals("")) {
			buf.append(" WHERE UPPER(strdescrizione) like '%" + strdescrizione.toUpperCase() + "%' ");
		}

		buf.append(" order by upper(strdescrizione)");

		return buf.toString();
	}
}
