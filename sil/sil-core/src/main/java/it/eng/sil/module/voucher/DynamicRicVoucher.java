/**
 * 
 */
package it.eng.sil.module.voucher;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author Fatale
 *
 */
public class DynamicRicVoucher implements IDynamicStatementProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.eng.afExt.dbaccess.sql.IDynamicStatementProvider#getStatement(com.engiweb.framework.base.RequestContainer,
	 * com.engiweb.framework.base.SourceBean)
	 */
	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		// TODO Auto-generated method stub

		String statement = SQLStatements.getStatement("CARICA_DESCR_VOUCHER");
		StringBuffer buf = new StringBuffer(statement);

		SourceBean serviceReq = requestContainer.getServiceRequest();

		/*
		 * String cdnTipoOrg = (String) serviceReq.getAttribute("ComboTipoOrg"); String cdnOrg = (String)
		 * serviceReq.getAttribute("ComboOrg"); String cdnRuolo = (String) serviceReq.getAttribute("ComboRuolo");
		 */

		String prgAzioni = "232";

		/*
		 * if (!cdnTipoOrg.equals("")) { buf.append(" where "); buf.append("prgAzioni=" + prgAzioni + ""); }
		 */

		return buf.toString();
	}

}
