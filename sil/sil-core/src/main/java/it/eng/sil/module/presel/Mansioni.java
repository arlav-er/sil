package it.eng.sil.module.presel;

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
 * @author Rolfini_A
 * @version 2.0, 02/10/2003
 */

/**
 * Libreria di funzioni per il reperimento dei codici delle mansioni dal database. La procedura per il reperimento dei
 * dati è ricorsiva. Ogni passo della ricorsione accende una connessione sul database ed effettua una query. MARGINI DI
 * MIGLIORAMENTO: 1) Effettuare un solo accesso al db e ricorrere sul resultset - problema del reperimento del
 * sottoalbero: la query (cosi' come è strutturata ora) preleva un solo livello dell'albero. 2) Effettuare un solo
 * accesso al db prelevando tutto il sottoalbero e costruire l'xml in maniera iterativa sul resultset.
 * 
 */
public class Mansioni {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Mansioni.class.getName());

	public Mansioni() {
	}

	/***************************************************************************
	 * FUNZIONI PUBLIC
	 **************************************************************************/

	/**
	 * getMansioni() Interroga il database per ottenere un elenco delle mansioni presenti e lo restituisce come stream
	 * XML tra i tag <DE_MANSIONE> e </DE_MANSIONE>. I dati risultanti sono organizzati ad albero.
	 * 
	 * @return Stream XML.
	 */
	public static String getMansioni(boolean flgFrequente, String flgIdo) {
		String XMLString = "<DE_MANSIONE>" + getMansioniFiglie("0", flgFrequente, flgIdo) + "</DE_MANSIONE>";
		return XMLString;
	}

	/**
	 * getMansioni(String codpadre, boolean flgFrequente) Reperisce le mansioni dal db e le restituisce come stream XML.
	 * Se flgFrequente è true, la ricerca viene effettuata solo sulle mansioni definite come "a frequente utilizzo" (nel
	 * db il flag flgFrequente è "S").
	 */
	public static String getMansioni(String codPadre, boolean flgFrequente, String flgIdo) {
		if (codPadre.equals("0")) {
			return getMansioni(flgFrequente, flgIdo);
		} else {
			return "<DE_MANSIONE>" + getPadre(codPadre, flgFrequente, flgIdo) + "</DE_MANSIONE>";
		}
	}

	/**
	 * getMansioni(String codPadre) Override della funzione getMansioni(). Interroga il database per ottenere un elenco
	 * delle mansioni presenti nel sottoalbero avente come radice il nodo con il codice specificato. I dati risultanti
	 * sono organizzati ad albero e restituiti come stream XML tra i tag <DE_MANSIONE> e </DE_MANSIONE>.
	 * 
	 * @param codPadre
	 *            Codice della radice del sottoalbero che si vuole ottenere.
	 * @return Stream XML.
	 * @see getMansioni()
	 */
	public static String getMansioni(String codPadre) {

		return getMansioni(codPadre, false, "");

	}

	/***************************************************************************
	 * FUNZIONI PRIVATE
	 **************************************************************************/

	/**
	 * getMansioniFiglie() Interroga il database per ottenere il sottoalbero delle mansioni presenti a partire dalla
	 * radice specificata. I dati sono prelevati, organizzati ad albero e restituiti con uno stream XML. Il meccanismo
	 * di reperimento, al fine di costruire l'albero, agisce tramite ricorsione richiamando la funzione stessa nel
	 * momento in cui incontra un nodo che non sia una foglia.
	 * 
	 * @param codPadre_ricerca
	 *            il codice della radice del sottoalbero che si vuole ottenere
	 * @return Stream XML.
	 */
	private static String getMansioniFiglie(String codPadre_ricerca, boolean flgFrequente, String flgIdo) {
		String XMLString = "";

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
			String query = "";
			if (flgFrequente) {
				if (flgIdo.equalsIgnoreCase("S")) {
					query = SQLStatements.getStatement("GET_DE_MANSIONE_FREQUENTE_IDO");
				} else {
					query = SQLStatements.getStatement("GET_DE_MANSIONE_FREQUENTE");
				}
			} else {
				if (flgIdo.equalsIgnoreCase("S")) {
					query = SQLStatements.getStatement("GET_DE_MANSIONE_IDO");
				} else {
					query = SQLStatements.getStatement("GET_DE_MANSIONE");
				}
			}

			// Imposto il parametro di ricerca (come array :( )
			List inputParameter = new ArrayList(1);
			inputParameter.add(dcSil.createDataField("codPadre", Types.VARCHAR, codPadre_ricerca));
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

					if (Integer.parseInt(cdnLivello) < 2) {
						XMLString = XMLString + getMansioniFiglie(codTitolo, flgFrequente, flgIdo);
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

		return XMLString;
	}

	private static String getPadre(String codPadre_ricerca, boolean flgFrequente, String flgIdo) {
		String XMLString = "";
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
			String query = "";
			if (flgFrequente) {
				if (flgIdo.equalsIgnoreCase("S")) {
					query = SQLStatements.getStatement("GET_DE_MANSIONE_PADRE_FREQUENTE_IDO");
				} else {
					query = SQLStatements.getStatement("GET_DE_MANSIONE_PADRE_FREQUENTE");
				}
			} else {
				if (flgIdo.equalsIgnoreCase("S")) {
					query = SQLStatements.getStatement("GET_DE_MANSIONE_PADRE_IDO");
				} else {
					query = SQLStatements.getStatement("GET_DE_MANSIONE_PADRE");
				}
			}

			// Imposto il parametro di ricerca (come array :( )
			List inputParameter = new ArrayList(1);
			inputParameter.add(dcSil.createDataField("codPadre", Types.VARCHAR, codPadre_ricerca));
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
				// reperisco la riga
				drow = srs.getDataRow(1);
				// prelevo i dati
				String codMansione = drow.getColumn("codMansione").getStringValue();
				String strDescrizione = drow.getColumn("strDescrizione").getStringValue();
				String codPadre = drow.getColumn("codPadre").getStringValue();
				String cdnLivello = drow.getColumn("cdnLivello").getStringValue();
				String desTipologia = drow.getColumn("desTipologia").getStringValue();
				// imposto la stringa che verrà restituita. Ciò che ho trovato è
				// un nodo
				XMLString = XMLString + "<mansione codMansione=\"" + codMansione + "\" strDescrizione=\""
						+ strDescrizione + "\" codPadre=\"" + codPadre + "\" cdnLivello=\"" + cdnLivello
						+ "\" desTipologia=\"" + desTipologia + "\">";
				// ma non è detto che sotto di lui non ce ne siano degli altri.
				// RICORSIONE
				// Se il nodo analizzato non è una foglia richiamo la funzione
				// con,
				// come parametro di ricerca, il nodo analizzato, che farà
				// funzioni
				// di radice. Cosi' prelevo il sottoalbero.
				if (Integer.parseInt(cdnLivello) < 2) {
					XMLString = XMLString + getMansioniFiglie(codMansione, flgFrequente, flgIdo);
				}
				// FINE RICORSIONE
				// Chiudo il tag aperto in precedenza
				// Chiudo il tag aperto in precedenza
				XMLString = XMLString + "</mansione>";
			}
		} catch (Exception ex) {
			_logger.debug("sil.anag.presel.Mansioni" + "::ERRORE DI CONNESSIONE::Non è possibile recuperare i dati"
					+ ex.getMessage());

			ex.printStackTrace();
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dcSil, cmd, rs);
		}

		return XMLString;
	}

}