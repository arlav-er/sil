package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.action.report.UtilsConfig;

public class InfStorIndispTemp implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfStorIndispTemp.class.getName());

	private static final String SELECT_SQL_BASE = " SELECT AN_LAVORATORE.STRNOME NOME,"
			+ "        AN_LAVORATORE.STRCOGNOME COGNOME," + "        AN_LAVORATORE.STRCODICEFISCALE CF,"
			+ "        am_indisp_temp.prgindisptemp, " + "        am_indisp_temp.cdnlavoratore, "
			+ "        am_indisp_temp.codindisptemp, " + " 			 de_indisp_temp.strdescrizione descrizione, "
			+ " 			 TO_CHAR (am_indisp_temp.datinizio, 'DD/MM/YYYY') datinizio, "
			+ "            am_indisp_temp.datinizio datainiziosort, "
			+ " 			 TO_CHAR (am_indisp_temp.datfine, 'DD/MM/YYYY') datfine, "
			+ " 			 am_indisp_temp.strnote, "
			+ " 			 TO_CHAR (am_indisp_temp.dtmins, 'DD/MM/YYYY') dtmins, "
			+ " 			 TO_CHAR (am_indisp_temp.dtmmod, 'DD/MM/YYYY') dtmmod, "
			+ " 			 am_indisp_temp.cdnutins, am_indisp_temp.cdnutmod        "
			+ "   FROM am_indisp_temp, de_indisp_temp, AN_LAVORATORE "
			+ "  WHERE (am_indisp_temp.codindisptemp = de_indisp_temp.codindisptemp) "
			+ " 	 AND NVL (am_indisp_temp.datfine, SYSDATE) < SYSDATE "
			+ "    AND AN_LAVORATORE.cdnLavoratore =am_indisp_temp.cdnlavoratore ";

	private static final String SELECT_SQL_BASE_CUSTOM = " SELECT AN_LAVORATORE.STRNOME NOME,"
			+ "        AN_LAVORATORE.STRCOGNOME COGNOME," + "        AN_LAVORATORE.STRCODICEFISCALE CF,"
			+ "        am_indisp_temp.prgindisptemp, " + "        am_indisp_temp.cdnlavoratore, "
			+ "        am_indisp_temp.codindisptemp, " + " 			 de_indisp_temp.strdescrizione descrizione, "
			+ " 			 TO_CHAR (am_indisp_temp.datinizio, 'DD/MM/YYYY') datinizio, "
			+ "            am_indisp_temp.datinizio datainiziosort, "
			+ " 			 TO_CHAR (am_indisp_temp.datfine, 'DD/MM/YYYY') datfine, "
			+ " 			 am_indisp_temp.strnote, "
			+ " 			 TO_CHAR (am_indisp_temp.dtmins, 'DD/MM/YYYY') dtmins, "
			+ " 			 TO_CHAR (am_indisp_temp.dtmmod, 'DD/MM/YYYY') dtmmod, "
			+ " 			 am_indisp_temp.cdnutins, am_indisp_temp.cdnutmod        "
			+ "   FROM am_indisp_temp, de_indisp_temp, AN_LAVORATORE "
			+ "  WHERE (am_indisp_temp.codindisptemp = de_indisp_temp.codindisptemp) "
			+ " 	 AND TRUNC(NVL(am_indisp_temp.datfine, SYSDATE)) < TRUNC(SYSDATE) "
			+ "    AND AN_LAVORATORE.cdnLavoratore =am_indisp_temp.cdnlavoratore ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		StringBuffer query_totale = null;
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");
		UtilsConfig utilsObject = new UtilsConfig("CONFCOND");
		String configIndisp = utilsObject.getConfigurazioneDefault_Custom();
		if (configIndisp.equals("0")) {
			query_totale = new StringBuffer(SELECT_SQL_BASE);
		} else {
			query_totale = new StringBuffer(SELECT_SQL_BASE_CUSTOM);
		}
		query_totale.append(" AND am_indisp_temp.cdnlavoratore =  ");
		query_totale.append(cdnLavoratore);
		query_totale.append(" ORDER BY am_indisp_temp.DATINIZIO ASC");
		// Debug
		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}// class InfStorIndispTemp
