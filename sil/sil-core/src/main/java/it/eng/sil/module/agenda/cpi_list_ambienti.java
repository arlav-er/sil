package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce la lista degli ambienti
 * relativi ad un CpI
 * 
 * @author: Giovanni Landi
 * 
 */

public class cpi_list_ambienti implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_list_ambienti.class.getName());

	public cpi_list_ambienti() {
	}

	private static final String SELECT_SQL_BASE = "SELECT " + "DE_AMBIENTE.CODCPI, " + "DE_AMBIENTE.PRGAMBIENTE, "
			+ "DE_AMBIENTE.STRDESCRIZIONE, " + "TO_CHAR(DE_AMBIENTE.DATINIZIOVAL, 'DD/MM/YYYY') DATINIZIOVAL, "
			+ "TO_CHAR(DE_AMBIENTE.DATFINEVAL, 'DD/MM/YYYY') DATFINEVAL, " + "DE_AMBIENTE.NUMCAPACITA, "
			+ "DE_AMBIENTE.NUMCAPIENZA " + "FROM DE_AMBIENTE, DE_CPI " + "WHERE DE_AMBIENTE.CODCPI=DE_CPI.CODCPI ";

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

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		query_totale.append("AND DE_AMBIENTE.CODCPI='" + codCpi + "'");

		// Debug
		_logger.debug("sil.module.agenda.cpi_list_ambienti" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();
	}
}