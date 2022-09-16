package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la selezione degli elementi della combo dei tipi di assunzione in base al tipo dell'azienda, natura
 * giuridica dell'azienda e tipo di contratto (determinato o indeterminato).
 */
public class DynSelectASL implements IDynamicStatementProvider2 {

	private String SELECT_AZIENDA_ASL = "select " + " ASL.CODAZIENDAASL as codice, "
			+ " ASL.strdescrizione AS descrizione " + " from DE_AZIENDA_ASL ASL ";

	public DynSelectASL() {
	}

	public String getStatement(SourceBean req, SourceBean response) {
		// SourceBean req = requestContainer.getServiceRequest();

		// Estraggo il criterio di ricerca
		String criterio = StringUtils.getAttributeStrNotNull(req, "CRITERIO");
		boolean cercaCodice = criterio.equalsIgnoreCase("codice");
		boolean cercaDescrizione = criterio.equalsIgnoreCase("descrizione");

		String codAziendaASL = StringUtils.getAttributeStrNotNull(req, "cod");
		String descrAziendaASL = StringUtils.getAttributeStrNotNull(req, "descr");

		String query = "";
		query = SELECT_AZIENDA_ASL;
		if (cercaCodice) {
			query = query + " where ASL.CODAZIENDAASL LIKE ";
			query = query + " upper('%" + codAziendaASL + "%') ";
		} else if (cercaDescrizione) {
			query = query + " where ASL.strdescrizione LIKE ";
			query = query + " upper('%" + descrAziendaASL + "%') ";
		}

		return query;
	}
}
