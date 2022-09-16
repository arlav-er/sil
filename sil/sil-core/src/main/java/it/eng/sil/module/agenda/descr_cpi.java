package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce la descrizione del CPI attivo nel
 * contesto dell'Agenda Appuntamenti
 * 
 * @author: Stefania Orioli
 * 
 */

public class descr_cpi implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(descr_cpi.class.getName());

	public descr_cpi() {
	}

	private static final String SELECT_SQL_BASE = "SELECT INITCAP(strDescrizione) as strDescrizione FROM DE_CPI ";

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
		// Costruisco la query
		String query_totale = SELECT_SQL_BASE + " WHERE CODCPI='" + codCpi + "'";

		// Debug
		// TracerSingleton.log("PCalendario",
		// TracerSingleton.DEBUG,"sil.module.agenda.descr_cpi" + "::Stringa di
		// ricerca:" + query_totale.toString());
		LogUtils.logDebug("sil.module.agenda.descr_cpi", "Query=" + query_totale, this);

		return query_totale;

	}
}