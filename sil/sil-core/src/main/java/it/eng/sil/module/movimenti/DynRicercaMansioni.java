package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * Effettua la ricerca dinamica di una mansione data una stringa della descrizione:
 * 
 * @author Paolo Roccetti
 * 
 */
public class DynRicercaMansioni implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String mans = (String) req.getAttribute("nomeMansione");

		return " SELECT MANS.codMansione codiceMansione, " + " MANS.strDescrizione descrizioneMansione "
				+ " FROM DE_MANSIONE MANS WHERE upper(MANS.strDescrizione) LIKE " + " upper('%" + mans + "%') "
				+ " ORDER BY descrizioneMansione";
	}
}
