package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di un operatore dato: - il cognome - o il nome - o nessuna delle precedenti (restituisce
 * TUTTO)
 * 
 */

public class DynRicercaAgOperatori implements IDynamicStatementProvider {
	public DynRicercaAgOperatori() {
	}

	private static final String SELECT_SQL_BASE = " SELECT PRGSPI,  " + " STRCOGNOME, " + " STRNOME " + ","
			+ " TO_CHAR(DATINIZIOVAL,'DD/MM/YYYY') DATINIZIOVAL " + ","
			+ " TO_CHAR(DATFINEVAL,'DD/MM/YYYY') DATFINEVAL " + " FROM AN_SPI ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String nome = (String) req.getAttribute("strNome_ric");
		String cognome = (String) req.getAttribute("strCognome_ric");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();
		buf.append("WHERE 1 = 1");

		if ((nome != null) && (!nome.equals(""))) {
			nome = StringUtils.replace(nome, "'", "''");
			buf.append(" AND upper(strnome) like upper('" + nome + "%')");
		}

		if ((cognome != null) && (!cognome.equals(""))) {
			cognome = StringUtils.replace(cognome, "'", "''");
			buf.append(" AND upper(strcognome) like upper('%" + cognome + "%')");
		}

		buf.append(" AND DATFINEVAL >= SYSDATE AND DATINIZIOVAL <= SYSDATE");
		buf.append(" ORDER BY STRCOGNOME, STRNOME");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}