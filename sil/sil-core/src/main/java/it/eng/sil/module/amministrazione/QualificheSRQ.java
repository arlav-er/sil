/*
 * Creato il 15-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

/**
 * @author landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class QualificheSRQ {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(QualificheSRQ.class.getName());

	public QualificheSRQ() {
	}

	public static String getMansioniFiglie(String codPadre_ricerca) {
		String XMLString = "<DE_QUALIFICA_SRQ>";

		DataConnection dcSil = null;
		SQLCommand cmd = null;
		DataResult rs = null;
		try {
			// Apro la connessione
			dcSil = DataConnectionManager.getInstance().getConnection("SIL_DATI");
			// La query è censita all'interno di statements.xml.
			// posso eseguire due query, a seconda se sto cercando su tutte le
			// mansioni
			// o solo sulle mansioni ad utilizzo più frequente
			String query = SQLStatements.getStatement("SELECT_DETTAGLIO_QUALIFICHE_SRQ_DA_PADRE");
			// Imposto il parametro di ricerca (come array :( )
			List inputParameter = new ArrayList(1);
			inputParameter.add(dcSil.createDataField("codQualificaSrq", Types.VARCHAR, codPadre_ricerca));
			// dichiaro il comando
			cmd = dcSil.createSelectCommand(query);
			// eseguo il comando con il parametro impostato
			rs = cmd.execute(inputParameter);
			// Il resultset contiene i dati di tutti i figli sottostanti al nodo
			// dato come parametro. Per ognuno di essi (tranne le foglie) verrà
			// richiamata ricorsivamente la procedura.
			if (rs.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				// definisco il mio oggetto come scrollable (modalità di
				// gestione)
				ScrollableDataResult srs = (ScrollableDataResult) rs.getDataObject();
				// I dati vengono restituiti riga per riga. Imposto quindi
				// l'oggetto
				// che verrà riempito ogni volta con i dati di una riga diversa.
				DataRow drow = null;
				// CICLO DI FETCH
				int nroRows = srs.getRowsNumber(); // imposto il numero delle
													// righe da prelevare
				int i = 1; // ciclo da uno al numero di righe
				while (i <= nroRows) // che mi sono state restuite
				{
					// reperisco la riga
					drow = srs.getDataRow(i);

					// prelevo i dati
					String codTitolo = drow.getColumn("codMansione").getStringValue();
					String strDescrizione = drow.getColumn("strDescrizione").getStringValue();
					String codPadre = drow.getColumn("codPadre").getStringValue();
					String cdnLivello = drow.getColumn("cdnLivello").getStringValue();
					String desTipologia = drow.getColumn("desTipologia").getStringValue();

					// imposto la stringa che verrà restituita. Ciò che ho
					// trovato è un nodo
					XMLString = XMLString + "<mansione codMansione=\"" + codTitolo + "\" strDescrizione=\""
							+ strDescrizione + "\" codPadre=\"" + codPadre + "\" cdnLivello=\"" + cdnLivello
							+ "\" desTipologia=\"" + desTipologia + "\">";
					// ma non è detto che sotto di lui non ce ne siano degli
					// altri.
					// RICORSIONE
					// Se il nodo analizzato non è una foglia richiamo la
					// funzione con,
					// come parametro di ricerca, il nodo analizzato, che farà
					// funzioni
					// di radice. Cosi' prelevo il sottoalbero.

					if (Integer.parseInt(cdnLivello) < 0) {
						XMLString = XMLString + getMansioniFiglie(codTitolo);
					}
					// FINE RICORSIONE
					// Chiudo il tag aperto in precedenza
					XMLString = XMLString + "</mansione>";
					i++;
				} // END WHILE
			}
		} catch (Exception ex) {
			_logger.debug("sil.anag.presel.Mansioni" + "::ERRORE DI CONNESSIONE::Non è possibile recuperare i dati"
					+ ex.getMessage());

			ex.printStackTrace();
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dcSil, cmd, rs);
		}

		XMLString = XMLString + "</DE_QUALIFICA_SRQ>";
		return XMLString;
	}

}