package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce il sommario settimanale degli appuntamenti 
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_app_settimana implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_app_settimana.class.getName());

	public cpi_app_settimana() {
	}

	private static final String SELECT_SQL_BASE = "select to_char(data, 'dd/mm/yyyy') as data, "
			+ "CODCPI, to_char(NUMTIPO) AS NUMTIPO, STRDESCTIPO, to_char(FASCIA) as NUMFASCIAORA, "
			+ "STRDESCFASCIA, data AS DATDATA, numero as NUMNUMERO " + "from vw_report_settima " + "where "
			+ "data between ";

	private static int mapping_mesiDB[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CODCPI");
		if (codCpi.equalsIgnoreCase("")) {
			if (cdnTipoGruppo == 1) {
				codCpi = user.getCodRif();
			}
		}

		String nrosDB = (String) req.getAttribute("nrosDB");
		String annoDB = (String) req.getAttribute("annoDB");

		String datai = "";
		String dataf = "";
		int giorno, mese, anno;
		// Determino l'intervallo delle date su cui effettuare la ricerca
		// settimanale nel DB
		Calendar cal = Calendar.getInstance();
		cal.clear(Calendar.MONTH);
		cal.clear(Calendar.DAY_OF_YEAR);
		cal.set(Calendar.YEAR, Integer.parseInt(annoDB));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(nrosDB));
		giorno = cal.get(Calendar.DATE);
		mese = mapping_mesiDB[cal.get(Calendar.MONTH)];
		anno = cal.get(Calendar.YEAR);
		datai = Integer.toString(giorno) + "/" + Integer.toString(mese) + "/" + Integer.toString(anno);
		cal.add(Calendar.DATE, 6);
		giorno = cal.get(Calendar.DATE);
		mese = mapping_mesiDB[cal.get(Calendar.MONTH)];
		anno = cal.get(Calendar.YEAR);
		dataf = Integer.toString(giorno) + "/" + Integer.toString(mese) + "/" + Integer.toString(anno);

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append("to_date('" + datai + "','dd/mm/yyyy') ");
		buf.append("and ");
		buf.append("to_date('" + dataf + "','dd/mm/yyyy') ");

		buf.append(" and codcpi='" + codCpi + "' ");
		buf.append(" order by data, numFasciaOra, numTipo ");

		// Costruisco la query
		query_totale.append(buf.toString());

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_settimana" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}