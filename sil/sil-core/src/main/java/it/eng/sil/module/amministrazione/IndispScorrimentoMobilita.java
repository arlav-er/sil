package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class IndispScorrimentoMobilita implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(IndispScorrimentoMobilita.class.getName());
	private String className = this.getClass().getName();

	private String SELECT_SQL_BASE = "SELECT to_char(am_indisp_temp.datInizio,'dd/mm/yyyy') datInizio, "
			+ "to_char(am_indisp_temp.datFine,'dd/mm/yyyy') datFine, am_indisp_temp.codIndispTemp, "
			+ "de_indisp_temp.strdescrizione FROM am_indisp_temp,  de_indisp_temp "
			+ " WHERE am_indisp_temp.codIndispTemp = de_indisp_temp.codIndispTemp ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();
		String cdnLav = req.containsAttribute("CDNLAVORATORE") ? req.getAttribute("CDNLAVORATORE").toString() : "";
		String datInizioMob = req.containsAttribute("DATINIZIOMOB") ? req.getAttribute("DATINIZIOMOB").toString() : "";
		String datFine = req.containsAttribute("DATMAXDIFF") ? req.getAttribute("DATMAXDIFF").toString() : "";
		if (!cdnLav.equals("")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND am_indisp_temp.cdnlavoratore = " + cdnLav;
		}
		SELECT_SQL_BASE = SELECT_SQL_BASE + " AND am_indisp_temp.codIndispTemp = 'I4' ";
		if (!datInizioMob.equals("")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND trunc(am_indisp_temp.datFine) >= to_date('" + datInizioMob
					+ "','dd/mm/yyyy')";
		}
		if (!datFine.equals("")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND trunc(am_indisp_temp.datFine) <= to_date('" + datFine
					+ "','dd/mm/yyyy') ";
		}
		return SELECT_SQL_BASE;
	}
}