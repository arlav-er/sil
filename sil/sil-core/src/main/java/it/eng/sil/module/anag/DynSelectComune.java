package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la selezione degli elementi della combo dei tipi di assunzione in base al tipo dell'azienda, natura
 * giuridica dell'azienda e tipo di contratto (determinato o indeterminato).
 */
public class DynSelectComune implements IDynamicStatementProvider2 {

	private String SELECT_COMUNE = "select " + " CODCOM as codice, " + " STRDENOMINAZIONE AS descrizione, "
			+ " STRCAP AS CAP " + " from DE_COMUNE" + " where ";

	public DynSelectComune() {
	}

	public String getStatement(SourceBean req, SourceBean response) {
		// SourceBean req = requestContainer.getServiceRequest();

		// Estraggo il criterio di ricerca
		String criterio = StringUtils.getAttributeStrNotNull(req, "CRITERIO");
		boolean cercaCodice = criterio.equalsIgnoreCase("codice");
		boolean cercaDescrizione = criterio.equalsIgnoreCase("descrizione");
		boolean cercaCap = criterio.equalsIgnoreCase("cap");

		String codComune = StringUtils.getAttributeStrNotNull(req, "cod");
		String descrComune = StringUtils.getAttributeStrNotNull(req, "descr");
		String cap = StringUtils.getAttributeStrNotNull(req, "cap");

		String query = "";
		query = SELECT_COMUNE;
		if (cercaCodice) {
			query = query + "CODCOM LIKE";
			query = query + " upper('%" + codComune + "%') ";
		}
		if (cercaDescrizione) {
			query = query + "STRDENOMINAZIONE LIKE";
			query = query + " upper('%" + descrComune + "%') ";
		}
		if (cercaCap) {
			query = query + "STRCAP LIKE";
			query = query + " upper('%" + codComune + "%') ";
		}

		return query;
	}
}
