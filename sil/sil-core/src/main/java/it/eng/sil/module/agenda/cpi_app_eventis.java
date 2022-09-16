package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce il numero giornaliero degli eventi di una 
 * determinata settimana relativi ad un CpI (ristretto agli appuntamenti 
 * pubblici se si tratta di un lavoratore oppure di una azienda).
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_app_eventis implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_app_eventis.class.getName());

	public cpi_app_eventis() {
	}

	private static final String SELECT_SQL_BASE = "select to_char(datevento, 'dd/mm/yyyy') as data, "
			+ "count(prgevento) as nro_ev " + "from ag_evento " + "where "
			+ "to_date(to_char(datevento, 'dd/mm/yyyy'),'dd/mm/yyyy') between ";

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
		// String codCpi = sessionContainer.getAttribute("codCpi").toString();
		// int cdnUt =
		// Integer.parseInt(sessionContainer.getAttribute("codCpi").toString());
		// int cdnTipoGruppo =
		// Integer.parseInt(sessionContainer.getAttribute("cdnTipoGruppo").toString());
		// String codCpi = "048301100";
		// int cdnTipoGruppo = 1;
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

		if (cdnTipoGruppo == 1) {
			buf.append(" and ag_evento.codcpievento='" + codCpi + "' ");
		} // else ==4 cittadino, ==5 azienda ....

		buf.append(" group by to_char(datevento, 'dd/mm/yyyy') ");

		// Costruisco la query
		query_totale.append(buf.toString());

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_eventis" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}