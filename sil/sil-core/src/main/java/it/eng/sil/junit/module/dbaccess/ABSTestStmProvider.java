/*
 * Created on Aug 25, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.junit.module.dbaccess;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;

/**
 * @author savino
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class ABSTestStmProvider {

	public static class InsCredito implements IDynamicStatementProvider {
		public String getStatement(RequestContainer requestContainer, SourceBean config) {
			SourceBean request = requestContainer.getServiceRequest();
			SessionContainer session = requestContainer.getSessionContainer();
			String cdnUt = (String) session.getAttribute("_CDUT_").toString();
			String prgCredito = (String) request.getAttribute("prgCredito");
			String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
			String strSpecifica = (String) request.getAttribute("strSpecifica");

			return "insert into pr_credito (prgCredito, cdnLavoratore, strSpecifica, cdnUtIns, cdnUtMod, dtmIns, dtmMod)"
					+ "values (" + prgCredito + "," + cdnLavoratore + ",'" + strSpecifica + "'," + cdnUt + "," + cdnUt
					+ ",sysdate,sysdate)";
		}
	}

	public static class DelCredito implements IDynamicStatementProvider {
		public String getStatement(RequestContainer requestContainer, SourceBean config) {
			SourceBean request = requestContainer.getServiceRequest();
			String prgCredito = (String) request.getAttribute("prgCredito");
			return "delete from pr_credito where prgCredito = " + prgCredito;
		}
	}

	public static class UpdCredito implements IDynamicStatementProvider2 {
		public String getStatement(SourceBean request, SourceBean response) {
			String prgCredito = (String) request.getAttribute("prgCredito");
			String strSpecifica = (String) request.getAttribute("strSpecifica");
			String cdnUtMod = (String) request.getAttribute("cdnUtMod");
			return "update pr_credito set strSpecifica = '" + strSpecifica + " AGGIORNATO" + "', cdnUtMod=" + cdnUtMod
					+ ", dtmMod=sysdate where prgCredito = " + prgCredito;
		}
	}

	public static class SelCredito implements IDynamicStatementProvider2 {
		public String getStatement(SourceBean request, SourceBean response) {
			String prgCredito = (String) request.getAttribute("prgCredito");
			return "select * from pr_credito where prgCredito = " + prgCredito;
		}
	}

	public static class FailCredito implements IDynamicStatementProvider2 {
		public String getStatement(SourceBean request, SourceBean response) {
			if (true)
				throw new NullPointerException("FailCredito  ha generato una eccezione di runtime");
			return null;
		}
	}

	public static class NullCredito implements IDynamicStatementProvider2 {
		public String getStatement(SourceBean request, SourceBean response) {
			return null;
		}
	}
}