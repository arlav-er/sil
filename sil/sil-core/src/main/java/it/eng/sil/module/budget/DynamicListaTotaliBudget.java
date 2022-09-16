/**
 * 
 */
package it.eng.sil.module.budget;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author Fatale
 *
 */
public class DynamicListaTotaliBudget implements IDynamicStatementProvider {
	/*
	 * case when DECTOTBUDGET = 0 then '0.00' else trim(to_char( DECTOTBUDGET, '9999999.99')) end
	 */

	private static final String SELECT_SQL_BASE = " SELECT " + "  cpi.codcpi codcpi,          "
			+ "  CPI.STRDESCRIZIONE,         " + "  NUMANNOBUDGET  ANNOFILTER,        "
			+ "  TO_CHAR(NUMANNOBUDGET) NUMANNOBUDGET  ,           "
			+ "  CASE WHEN DECTOTBUDGET  = 0 then '0.00'           "
			+ "  ELSE TRIM(TO_CHAR(DECTOTBUDGET, '999999999.99')) END DECTOTBUDGET ,           "
			+ "  CASE WHEN DECTOTIMPEGNATO  = 0 then '0.00'           "
			+ "  ELSE TRIM(TO_CHAR(DECTOTIMPEGNATO, '999999999.99')) END DECTOTIMPEGNATO ,           "
			+ "  CASE WHEN DECTOTSPESO  = 0 then '0.00'           "
			+ "  ELSE TRIM(TO_CHAR(DECTOTSPESO, '999999999.99')) END DECTOTSPESO ,           "
			+ "  CASE WHEN DECTOTRESIDUO  = 0 then '0.00'           "
			+ "  ELSE TRIM(TO_CHAR(DECTOTRESIDUO, '999999999.99')) END  DECTOTRESIDUO          "
			+ "  from VCH_BUDGET_CPI CPI_BDG " + "  inner join de_cpi cpi       " + "  on CPI_BDG.CODCPI=cpi.codcpi "
			+ "  WHERE 1=1                  ";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaTotaliBudget.class.getName());

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug("Sono dentro l'azione della ricerca per lista per budget");

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);
		// buf.append(SELECT_SQL_BASE);

		SourceBean serviceReq = requestContainer.getServiceRequest();
		SessionContainer session = requestContainer.getSessionContainer();

		// String codiceCpiSel=(String) serviceReq.getAttribute("codiceCPISel");
		// String annoSel=(String)serviceReq.getAttribute("AnnoSel");

		String codiceCpiSel = "";
		String annoSel = "";

		String[] inputParam = (String[]) session.getAttribute("tipoRicerca");

		if (inputParam != null && inputParam.length > 0) {
			annoSel = inputParam[0];
			codiceCpiSel = inputParam[1];
		} else {
			codiceCpiSel = (String) serviceReq.getAttribute("codiceCPISel");
			annoSel = (String) serviceReq.getAttribute("AnnoSel");
		}

		if (codiceCpiSel != null && !"".equals(codiceCpiSel)) {
			buf.append(" AND cpi.CODCPI ='" + codiceCpiSel + "'");
		}
		if (annoSel != null && !"".equals(annoSel)) {
			buf.append(" AND NUMANNOBUDGET =" + annoSel);
		}

		buf.append(" Order by NUMANNOBUDGET DESC, STRDESCRIZIONE ");

		_logger.debug("Query ottenuta per la lista dettaglio ::: " + buf.toString());

		return buf.toString();
	}

}
