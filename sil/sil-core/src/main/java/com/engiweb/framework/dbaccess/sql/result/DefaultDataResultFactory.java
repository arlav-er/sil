package com.engiweb.framework.dbaccess.sql.result;

import java.sql.ResultSet;
import java.util.List;

import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.legacy.LegacyScrollableDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.DefaultScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa Classe rappresenta una factory concreta per la creazione di oggetti di tipo DataResultInterface legati a
 * driver jdbc di tipo 2
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class DefaultDataResultFactory implements DataResultFactory {
	/**
	 * Crea un oggetto DataResultInterface di tipo ScrollableDataResult
	 * 
	 * @param <B>SQLCommand
	 *            </B> command - Il comando che ha generato come risultato l'oggetto ScrollableDataResult che verrà
	 *            creato da questa funzione
	 * @param <B>List
	 *            </B> inputParameters - La lista dei parametri di input del comando che ha generato come risultato
	 *            l'oggetto ScrollableDataResult che verrà creato da questa funzione
	 * @param <B>ResultSet
	 *            </B> rs - Il ResultSet alla base dell'oggetto ScrollableDataResult creato da questa funzione
	 * @return un oggetto di tipo <B>DataResultInterface</B> - che in realtà è di tipo ScrollableDataResult
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica nella creazione dell'oggetto fi tipo ScrollableDataResult
	 */
	public DataResultInterface createScrollableDataResult(SQLCommand command, List inputParameters, ResultSet rs,
			boolean std) throws EMFInternalError {
		if (std)
			return new DefaultScrollableDataResult(command, inputParameters, rs);
		else
			return new LegacyScrollableDataResult(command, inputParameters, rs);
	} // public DataResultInterface createScrollableDataResult(SQLCommand

	// command, List inputParameters, ResultSet rs, boolean std) throws
	// EMFInternalError

	/**
	 * Crea un oggetto DataResultInterface di tipo PunctualDataResult
	 * 
	 * @param <B>SQLCommand
	 *            </B> command - Il comando che ha generato come risultato l'oggetto PunctualDataResult che verrà creato
	 *            da questa funzione
	 * @param <B>List
	 *            </B> inputParameters - La lista dei parametri di input del comando che ha generato come risultato
	 *            l'oggetto PunctualDataResult che verrà creato da questa funzione
	 * @param <B>DataField
	 *            </B> punctualData - Il dataField che costituice il dato puntuale alla base dell'oggetto
	 *            PunctualDataResult creato da questa funzione
	 * @return un oggetto di tipo <B>DataResultInterface</B> - che in realtà è di tipo PunctualDataResult
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica nella creazione dell'oggetto fi tipo PunctualDataResult
	 */
	public DataResultInterface createPunctualDataResult(SQLCommand command, List inputParameters,
			DataField punctualData) throws EMFInternalError {
		return new PunctualDataResult(command, inputParameters, punctualData);
	} // public DataResultInterface createPunctualDataResult(SQLCommand

	// command, List inputParameters, DataField punctualData) throws
	// EMFInternalError

	/**
	 * Crea un oggetto DataResultInterface di tipo InformationDataResult
	 * 
	 * @param <B>SQLCommand
	 *            </B> command - Il comando che ha generato come risultato l'oggetto InformationDataResult che verrà
	 *            creato da questa funzione
	 * @param <B>List
	 *            </B> inputParameters - La lista dei parametri di input del comando che ha generato come risultato
	 *            l'oggetto InformationDataResult che verrà creato da questa funzione
	 * @param boolean
	 *            correctExecution - true se l'esecuzione del comando è avvenuta correttamente
	 * @param int
	 *            rowsAffected - il numero di righe coinvolte nll'esecuzione del comando
	 * @param String
	 *            executionMessage - eventuale messaggio dell'InformationDataResult da creare
	 * @return un oggetto di tipo <B>DataResultInterface</B> - che in realtà è di tipo InformationDataResult
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica nella creazione dell'oggetto fi tipo InformationDataResult
	 */
	public DataResultInterface createInformationDataResult(SQLCommand command, List inputParameters,
			boolean correctExecution, int rowsAffected, String executionMessage) throws EMFInternalError {
		return new InformationDataResult(command, inputParameters, correctExecution, rowsAffected, executionMessage);
	} // public DataResultInterface createInformationDataResult(SQLCommand

	// command, List inputParameters, boolean
	// correctExecution, int rowsAffected, String executionMessage) throws
	// EMFInternalError

	/**
	 * Crea un oggetto DataResultInterface di tipo InformationDataResult
	 * 
	 * @param <B>SQLCommand
	 *            </B> command - Il comando che ha generato come risultato l'oggetto CompositeDataResult che verrà
	 *            creato da questa funzione
	 * @param <B>List
	 *            </B> inputParameters - La lista dei parametri di input del comando che ha generato come risultato
	 *            l'oggetto CompositeDataResult che verrà creato da questa funzione
	 * @return un oggetto di tipo <B>DataResultInterface</B> - che in realtà è di tipo CompositeDataResult
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica nella creazione dell'oggetto fi tipo CompositeDataResult
	 */
	public DataResultInterface createCompositeDataResult(SQLCommand command, List inputParameters) {
		return new CompositeDataResult(command, inputParameters);
	} // public DataResultInterface createCompositeDataResult(SQLCommand
		// command, List inputParameters)
}
