/*
 * Creato il Mar 30, 2005
 *
 */
package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author Savino
 * 
 *         Estrae la lista (codice, descrizione) dei modelli disponibili. Il parametro della query dinamica e':
 *         MODELLIDAVISUALIZZARE Questo parametro puo' non essere presente oppure la sua occorrenza puo' essere
 *         multipla.
 * 
 *         N:B: Sostituisce anche la query SELECT_COMBO_MODELLO_STAMPA che utilizzava un parametro per il controllo di
 *         validita' delle date, che pero' non veniva utilizzato, per cui per il momento non lo ho aggiunto.
 */
public class DynamicStatementTipoModello implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean request = requestContainer.getServiceRequest();
		StringBuffer query = new StringBuffer();
		query.append("select prgTipoModello as codice, strdescrizione as descrizione ");
		query.append("from de_tipo_modello ");
		query.append("where trunc(sysdate) between trunc(datInizioVal) and trunc(nvl(datFineVal, sysdate))");
		Vector modelliDaVisualizzare = request.getAttributeAsVector("MODELLIDAVISUALIZZARE");
		if (modelliDaVisualizzare.size() > 0) {
			query.append(" and prgTipoModello in (");
			for (int i = 0; i < modelliDaVisualizzare.size(); i++) {
				String prgTipoModello = (String) modelliDaVisualizzare.get(i);
				query.append(prgTipoModello);
				if (i < modelliDaVisualizzare.size() - 1)
					query.append(",");
			}
			query.append(")");
		}

		return query.toString();
	}

}
