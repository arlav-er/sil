/*
 * Creato il 14-set-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.security;

import com.engiweb.framework.dbaccess.sql.DataConnection;

/**
 * @author desimone
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class TransactionProfileDataFilter extends ProfileDataFilter {

	public TransactionProfileDataFilter(User user, String _page, DataConnection _dataConnection) {
		super(user, _page);

		this.dataConnection = _dataConnection;
		isTransactional = true;
	}

	public DataConnection getDataConnection() {
		return dataConnection;
	}

}
