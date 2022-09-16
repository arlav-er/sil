/*
 * Creato il 15-nov-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Classe per la generazione della query per il controllo dell'esistenza delle chiavi sul DB a partire dalla chiave ,
 * dal nome della colonna e da quello della tabella
 * 
 * @author roccetti
 */
public class DynSelectEsistenzaChiave implements IDynamicStatementProvider {
	/**
	 * 
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String chiave = StringUtils.getAttributeStrNotNull(req, "CHIAVE");
		String nomeColonna = StringUtils.getAttributeStrNotNull(req, "NOMECOLONNA");
		String nomeTabella = StringUtils.getAttributeStrNotNull(req, "NOMETABELLA");

		return "SELECT * FROM " + nomeTabella + " WHERE " + nomeColonna + " = '" + chiave + "'";
	}
}
