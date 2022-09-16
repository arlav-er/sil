package it.eng.sil.module.budget;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicListaDettaglioTotaliBudget implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT " + " oper_bdg.numannobudget, "
			+ " oper_bdg.datoperazione dataOpFilter," + " to_char(oper_bdg.datoperazione,'dd/MM/yyyy') datoperazione,"
			+ " CPI.STRDESCRIZIONE ,     " + "CASE WHEN oper_bdg.decimporto  = 0 then '0.00'   "
			+ "ELSE TRIM(TO_CHAR(oper_bdg.decimporto, '999999999.99')) END decimporto ,   "
			+ "CASE WHEN vch_oper.codoperazione  = 'E' then '+ ' || vch_oper.strdescrizione   "
			+ "ELSE '- '   || vch_oper.strdescrizione END operdescr     " + " from VCH_OPERAZIONI_BUDGET oper_bdg "
			+ " inner join de_cpi cpi               " + " on oper_bdg.codcpidestinazione = cpi.codcpi  "
			+ " inner join de_vch_operazione vch_oper " + " on oper_bdg.codoperazione = vch_oper.codoperazione "
			+ "where 1=1";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaDettaglioTotaliBudget.class.getName());

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		_logger.debug("Sono dentro il caricamento della lista dei dettagli");
		SourceBean serviceReq = requestContainer.getServiceRequest();

		// SessionContainer session = requestContainer.getSessionContainer();

		String codiceCpiSel = (String) serviceReq.getAttribute("CODICECPISEL");
		String annoSel = (String) serviceReq.getAttribute("AnnoSel");
		String anno = (String) serviceReq.getAttribute("NUMANNOBUDGET");
		String codCpi = (String) serviceReq.getAttribute("CODCPI");

		// Se anno e codCpi sono valorizzate significa che vengo dal dettaglio altrimenti sto tornando indietro dal
		// pulsante

		if (annoSel == null || "".equals(annoSel)) {
			if (anno != null && !"".equals(anno)) {
				annoSel = anno;
			}
		}
		/* perchè??? */
		if (codiceCpiSel == null || "".equals(codiceCpiSel)) {
			if (codCpi != null && !"".equals(codCpi)) {
				codiceCpiSel = codCpi;
			}
		}

		// annoSel=(String)session.getAttribute("anno");
		// codiceCpiSel=(String)session.getAttribute("codCpi");

		if (codiceCpiSel != null && !"".equals(codiceCpiSel)) {
			// buf.append(" AND CODCPI ='"+codiceCpiSel+"'");
			buf.append(" AND CODCPI ='" + codCpi + "'");
		}
		if (annoSel != null && !"".equals(annoSel)) {
			buf.append(" AND NUMANNOBUDGET=" + annoSel);
		}

		buf.append(" order by dataOpFilter desc");

		_logger.debug("Query ottenuta per la lista dettaglio ::: " + buf.toString());

		/*
		 * BigDecimal residuo=(BigDecimal)session.getAttribute("residuoTot"); BigDecimal
		 * speso=(BigDecimal)session.getAttribute("spesoTot");
		 * 
		 * requestContainer.setAttribute("residuoTot",residuo.toString());
		 * requestContainer.setAttribute("spesoTot",speso.toString());
		 */

		return buf.toString();
	}

}
