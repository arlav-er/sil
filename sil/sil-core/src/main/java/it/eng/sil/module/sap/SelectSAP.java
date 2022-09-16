package it.eng.sil.module.sap;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.sil.Values;

/**
 * Gestisce una select sul DB SIL.
 * 
 * @author Guido Zuccaro
 * @since 25/03/2016
 */
public class SelectSAP {
	private static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelectSAP.class.getName());

	private DataConnection dataConnection;
	SQLCommand sqlCommand;
	private List<Object> lstParametri = new ArrayList<Object>();

	/**
	 * @param xmlSelect
	 */
	public SelectSAP(String xmlSelect) {
		try {
			// si connette al db
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// recupera lo statement dai file di configurazione di spago
			String statement = SQLStatements.getStatement(xmlSelect);
			sqlCommand = dataConnection.createSelectCommand(statement);

		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
	}

	/**
	 * Esegue la select e restituisce il risultato.
	 * 
	 * @param xmlSelect
	 * @return Restituisce il risultato della select.
	 */
	public ScrollableDataResult esegui() {
		ScrollableDataResult scrollableDataResult = null;
		try {

			// esegue la query
			DataResult dataResult = sqlCommand.execute(lstParametri);
			scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();

		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return scrollableDataResult;
	}

	/**
	 * Imposta una condizione per la select.
	 * 
	 * @param chiave
	 *            Nome del campo.
	 * @param valore
	 *            Valroe del campo.
	 */
	public void parametro(String chiave, String valore) {
		lstParametri.add(dataConnection.createDataField(chiave, Types.VARCHAR, valore));
	}

	/**
	 * Chiude la connessione al DB.
	 */
	public void chiudi() {
		try {
			dataConnection.close();
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
	}

	public static org.apache.log4j.Logger getLogger() {
		return _logger;
	}

}
