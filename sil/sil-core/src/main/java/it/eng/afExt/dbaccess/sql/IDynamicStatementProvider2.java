/**
 * Interfaccia di classe che ritorna query di tipo select, update, delete in base ai parametri contenuti
 * nel SourceBean request
 */
package it.eng.afExt.dbaccess.sql;

import com.engiweb.framework.base.SourceBean;

/**
 * @author finessi_m
 */
public interface IDynamicStatementProvider2 {

	/**
	 * Ritorna la stringa SQL per la select
	 */
	public String getStatement(SourceBean request, SourceBean response);

	/**
	 * LA PARTE SOTTOSTANTE E' OBSOLETA. Modifiche effettuate da Alessio Rolfini
	 * 
	 * /** Ritorna la stringa SQL per l'update
	 * 
	 * public String getUpdateStatement(RequestContainer requestContainer,SourceBean config);
	 * 
	 * 
	 * /** Ritorna la stringa SQL per la delete
	 * 
	 * public String getDeleteStatement(RequestContainer requestContainer,SourceBean config);
	 */

}
