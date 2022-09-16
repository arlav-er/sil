package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce le informazioni sulla festività o meno del
 * giorno selezionato relativamente al CpI di apparteneneza o al CpI scelto
 * (se lavoratore o azienda)
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_giorno_festivo implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_giorno_festivo.class.getName());

	public cpi_giorno_festivo() {
	}

	private static final String SELECT_SQL_BASE = "select " + "PRGGIORNONL, " + "CODCPI, " + "NUMGG, " + "NUMMM, "
			+ " to_char(NUMAAAA) as NUMAAAA, " + " NUMGSETT, " + "to_char(DATINIZIOVAL,'dd/mm/yyyy') as DATINIZIOVAL, "
			+ "to_char(DATFINEVAL,'dd/mm/yyyy') as DATFINEVAL, " + "NUMKLOGIORNONL " + "from ag_giornonl ";

	/*
	 * private static final String SELECT_SQL_UNION = "select * from ag_giornonl " + "where (numgsett is not null) ";
	 */

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		String codCpi;
		if (cdnTipoGruppo == 1) {
			codCpi = user.getCodRif();
		} else {
			codCpi = req.getAttribute("agenda_codCpi").toString();
		}

		String giorno = (String) req.getAttribute("giornoDB");
		String mese = (String) req.getAttribute("meseDB");
		String anno = (String) req.getAttribute("annoDB");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" WHERE ");

		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		int oggi_gg = oggi.get(5);
		int oggi_mm = oggi.get(2);
		int oggi_aa = oggi.get(1);

		if ((giorno != null) && (!giorno.equals(""))) {
			buf.append(" (NUMGG=" + giorno + ") ");
		} else {
			buf.append(" (NUMGG=" + oggi_gg + ") ");
		}

		buf.append(" and ");

		if ((mese != null) && (!mese.equals(""))) {
			buf.append(" (NUMMM=" + mese + ") ");
		} else {
			buf.append(" (NUMMM=" + (oggi_mm + 1) + ") ");
		}

		buf.append(" and ");

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" (NUMAAAA=" + anno + " OR NUMAAAA is null) ");
		} else {
			buf.append(" (NUMAAAA=" + oggi_aa + " OR NUMAAAA is null) ");
		}

		/* FILTRI */
		buf.append(" and (codcpi='" + codCpi + "' or codcpi is null) ");

		buf.append("and (to_date(to_char(NUMGG) || '/' || to_char(NUMMM) || '/' || nvl(to_char(NUMAAAA),'");
		if (anno != null) {
			buf.append(anno);
		} else {
			buf.append(oggi_aa);
		}
		buf.append("'),'dd/mm/yyyy') between to_date(to_char(DATINIZIOVAL,'dd/mm/yyyy'),'dd/mm/yyyy')");
		buf.append("and to_date(to_char(DATFINEVAL,'dd/mm/yyyy'),'dd/mm/yyyy')) ");
		buf.append("and (NUMGG is not null and NUMMM is not null)");

		// Costruisco la query
		query_totale.append(buf.toString());
		// query_totale.append(" UNION ");
		// query_totale.append(SELECT_SQL_UNION + " and (codcpi='" + codCpi + "'
		// OR codcpi is null) ");

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_giorno" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}