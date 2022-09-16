package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * @author: Stefania Orioli 
 */

public class cpi_list_sett_tipo implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_list_sett_tipo.class.getName());

	public cpi_list_sett_tipo() {
	}

	private static final String SELECT_SQL_BASE = "select " + "CODCPI, " + "PRGSETTIPO, "
			+ "STRDESCRIZIONESETTIMANA AS DESCR, " + "to_char(DATINIZIOVAL, 'dd/mm/yyyy') as DATINIZIOVAL, "
			+ "to_char(DATFINEVAL, 'dd/mm/yyyy') as DATFINEVAL " + "FROM DE_SETTIMANA_TIPO " + "WHERE CODCPI=";

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

		String query_totale = SELECT_SQL_BASE + "'" + codCpi + "'";

		// Debug
		_logger.debug("sil.module.agenda.cpi_list_sett_tipo" + "::Stringa di ricerca:" + query_totale.toString());

		return (query_totale);

	}

}