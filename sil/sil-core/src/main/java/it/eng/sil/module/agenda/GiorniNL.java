package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce i giorni non lavorativi 
 * relativi al mese e all'anno 
 * (presenti come parametri nella request, oppure mese/anno correnti)
 * 
 * @author: Stefania Orioli
 */

public class GiorniNL implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GiorniNL.class.getName());

	public GiorniNL() {
	}

	private static final String SELECT_SQL_BASE = "select numgg, nummm, numaaaa, numgsett, "
			+ "to_char(DATINIZIOVAL,'dd') as GI, to_char(DATINIZIOVAL, 'mm') as MI, to_char(DATINIZIOVAL, 'yyyy') as AI, "
			+ "to_char(DATINIZIOVAL, 'dd/mm/yyyy') AS DATINIZIO, "
			+ "to_char(DATFINEVAL,'dd') as GF, to_char(DATFINEVAL, 'mm') as MF, to_char(DATFINEVAL, 'yyyy') as AF, "
			+ "to_char(DATFINEVAL, 'dd/mm/yyyy') AS DATFINE, prggiornonl, codcpi " + "from ag_giornonl where ";

	private static final String SELECT_SQL_FINE = "union select numgg, nummm, numaaaa, numgsett, "
			+ "to_char(DATINIZIOVAL,'dd') as GI, to_char(DATINIZIOVAL, 'mm') as MI, to_char(DATINIZIOVAL, 'yyyy') as AI, "
			+ "to_char(DATINIZIOVAL, 'dd/mm/yyyy') AS DATINIZIO, "
			+ "to_char(DATFINEVAL,'dd') as GF, to_char(DATFINEVAL, 'mm') as MF, to_char(DATFINEVAL, 'yyyy') as AF, "
			+ "to_char(DATFINEVAL, 'dd/mm/yyyy') AS DATFINE, prggiornonl, codcpi "
			+ "from ag_giornonl where (numgsett is not null) ";

	private static int mapping_mesiDB[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		/*
		 * Quando faremo i collegamenti con la profilatura allora questi parametri li devo reperire dall'oggetto
		 * sessionContainer.
		 * 
		 * Verificare con Franco i nomi dei parametri
		 * 
		 */
		// String codCpi = "048301100";
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
		String mese = (String) req.getAttribute("mese");
		String anno = (String) req.getAttribute("anno");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		int oggi_mm = oggi.get(2);
		int oggi_aa = oggi.get(1);
		int m, a;

		buf.append(" ( ");
		// Filtro sul MESE CORRENTE
		buf.append("(");
		if ((mese != null) && (!mese.equals(""))) {
			buf.append(" (NUMMM = " + mapping_mesiDB[Integer.parseInt(mese)] + ") ");
			m = Integer.parseInt(mese);
		} else {
			buf.append(" (NUMMM = " + mapping_mesiDB[oggi_mm] + ") ");
			m = oggi_mm;
		}
		buf.append(" and ");
		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" (NUMAAAA = " + anno + " OR NUMAAAA is null) ");
			a = Integer.parseInt(anno);
		} else {
			buf.append(" (NUMAAAA = " + oggi_aa + " OR NUMAAAA is null) ");
			a = oggi_aa;
		}
		buf.append(")");
		// Filtro sul MESE PRECEDENTE
		buf.append(" OR ");
		int mp, ap;
		if (m == 0) {
			mp = 11;
			ap = a - 1;
		} else {
			mp = m - 1;
			ap = a;
		}
		buf.append("(");
		buf.append(" (NUMMM = " + mapping_mesiDB[mp] + ") ");
		buf.append(" and ");
		buf.append(" (NUMAAAA = " + ap + " OR NUMAAAA is null) ");
		buf.append(")");
		// Filtro sul MESE SEGUENTE
		buf.append(" OR ");
		int ms, as;
		if (m == 11) {
			ms = 0;
			as = a + 1;
		} else {
			ms = m + 1;
			as = a;
		}
		buf.append("(");
		buf.append(" (NUMMM = " + mapping_mesiDB[ms] + ") ");
		buf.append(" and ");
		buf.append(" (NUMAAAA = " + as + " OR NUMAAAA is null) ");
		buf.append(")");

		buf.append(" ) ");
		/*
		 * In questa sezione devo inserire il codCpi che è in sessione oppure il codice scelto dal Lavoratore/Azienda,
		 * quindi codcpi non deve essere mai nullo.
		 */
		if ((codCpi != null) && (!codCpi.equals(""))) {
			buf.append(" and (codcpi='" + codCpi + "' or codcpi is null) ");
		}

		buf.append(SELECT_SQL_FINE);
		/* Aggiunto in data 4/12/2003 */
		// buf.append(" and (to_number(to_char(DATINIZIOVAL, 'mm'))<=" + m + "
		// and to_number(to_char(DATINIZIOVAL, 'yyyy'))<=" + a +") ");
		/*
		 * In questa sezione devo inserire il codCpi che è in sessione oppure il codice scelto dal Lavoratore/Azienda,
		 * quindi codcpi non deve essere mai nullo.
		 */
		if ((codCpi != null) && (!codCpi.equals(""))) {
			buf.append(" and (codcpi='" + codCpi + "' or codcpi is null) ");
		}

		// Debug - Log
		query_totale.append(buf.toString());
		_logger.debug("sil.module.agenda.GiorniNL" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}

}