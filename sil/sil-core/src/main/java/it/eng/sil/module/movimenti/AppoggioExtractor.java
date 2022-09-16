package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.xml.sax.helpers.DefaultHandler;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

/**
 * Estrattore che estrae i record dalla tabella AM_MOVIMENTO_APPOGGIO e li ritorna uno per uno al validatore. Funziona
 * per qualsiasi modifica della tabella AM_MOVIMENTO_APPOGGIO ed è in grado di convertire automaticamente i dati
 * estratti nel formato corrispondente utilizzando i meta dati contenuti nel risultato delle Query. I record verranno
 * estratti nell'ordine inverso rispetto a quello contenuto nell'array passato al costruttore.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class AppoggioExtractor extends DefaultHandler implements RecordExtractor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AppoggioExtractor.class.getName());
	/** Array dei movimenti da estrarre */
	private BigDecimal[] objList;
	/**
	 * Indicatore del prossimo movimento da estrarre nell'array di progressivi indicati
	 */
	private int objListPosition;
	/** Formattatore di date */
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	private BigDecimal currentPrgObjApp = null;

	// Altre variabili interne
	private String className = this.getClass().getName();
	private Hashtable nextRecord;
	private boolean calledNext;
	private boolean lastHasNext;
	private String nameSatatementExtractor = "";
	private TransactionQueryExecutor tex;

	/**
	 * Costruttore.
	 * <p>
	 * 
	 * @param prgMovimentoAppList
	 *            Array con i progressivi dei movimenti da estrarre, i movimenti saranno estratti in ordine inverso
	 *            rispetto a quello con cui sono indicati nell'array.
	 *            <p>
	 * @throws NullPointerException
	 *             se è nullo l'array dei movimenti da estrarre
	 */
	public AppoggioExtractor(BigDecimal[] prgAppList, String statement, TransactionQueryExecutor newTex)
			throws NullPointerException {
		if (prgAppList == null) {
			throw new NullPointerException("Array dei movimenti da estrarre nullo");
		}
		// Memorizzo l'array per l'estrazione dei movimenti
		objList = prgAppList;
		objListPosition = objList.length - 1;
		// setto gli altri parametri
		nextRecord = null;
		calledNext = true;
		lastHasNext = true;
		this.nameSatatementExtractor = statement;
		this.tex = newTex;
	}

	/**
	 * Indica se ci sono ancora record da estrarre dalla sorgente.
	 * <p>
	 * 
	 * @return true se ci sono ancora record da estrarre, false se non è possibile estrarre alcun altro record
	 * 
	 */
	public boolean hasNext() {

		// Se è stata chiamata la next eseguo un'altra estrazione, altrimenti
		// ritorno il risultato precedente
		if (!calledNext) {
			return lastHasNext;
		} else {
			calledNext = false;

			// Leggo il record successivo e lo memorizzo in nextRecord
			try {
				nextRecord = this.estrai();
				lastHasNext = true;
			} catch (NoSuchElementException e) {
				nextRecord = null;
				lastHasNext = false;
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Eccezione nell'estrazione del record con identificativo "
						+ currentPrgObjApp + " dalla tabella di appoggio ", e);

				nextRecord = null;
				lastHasNext = false;
			}

			// Ritorno il risultato;
			return lastHasNext;
		}
	}

	/**
	 * Estrae il record successivo dalla sorgente, lo elabora e restituisce una Hashtable contenente i dati indicati nel
	 * file di configurazione caricato
	 * <p>
	 * 
	 * @return Un oggetto di tipo Hashtable che contiene i dati estratti dal record.
	 */
	public Object next() throws NoSuchElementException {

		// Se il file è già finito in precedenza ritorno la
		// NoSuchElementException
		if (!lastHasNext) {
			throw new NoSuchElementException();
		} else if (!calledNext) {

			// Non ho ancora chiamato la next sull'elemento corrente, quindi non
			// ne devo estrarre un'altro!
			calledNext = true;
			return nextRecord;
		} else {

			// Chiamo la hasNext per estrarre il prossimo elemento
			boolean has = hasNext();
			if (has = false) {
				throw new NoSuchElementException();
			}
			// Se il prossimo elemento esiste lo elaboro e ritorno il risultato
			else {
				return nextRecord;
			}
		}
	}

	/**
	 * Numero di record totale da processare
	 */
	public int getNumRecord() {
		return objList.length;
	}

	/**
	 * Operazione non supportata, lancerà una UnsupportedOperationException
	 */
	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Estrae il record successivo, se riesce lo ritorna sotto forma di Hashtable, altrimenti lancia un'eccezione.
	 */
	private Hashtable estrai() throws SQLException, NoSuchElementException {

		if (objListPosition < 0) {
			throw new NoSuchElementException("Array dei movimenti da estrarre esaurito");
		}

		currentPrgObjApp = objList[objListPosition];
		Object[] args = new Object[1];
		args[0] = currentPrgObjApp;

		// Decremento il contatore per estrarre il prossimo Movimento
		objListPosition = objListPosition - 1;
		Object result = null;
		try {
			// 11/06/2008 savino: FASE 2
			// se siamo in un trasferimento ramo azienda allora bisogna usare la transazione esterna
			// il movimento da ricercare nella am_movimento_appoggio e' appena stato importato e la transazione non e'
			// stata ancora chiusa.
			if (this.tex != null)
				result = (SourceBean) tex.executeQuery(nameSatatementExtractor, args, "SELECT");
			else
				result = QueryExecutor.executeQuery(nameSatatementExtractor, args, "SELECT", Values.DB_SIL_DATI);
		} catch (Exception e) {
			_logger.debug("Impossibile estrarre il record con progressivo " + currentPrgObjApp
					+ " dalla tabella di appoggio. Dettagli: " + e.getMessage()
					+ ". Proseguo con il record successivo.");

			return estrai();
		}

		// Controllo il risultato
		if (result instanceof SourceBean) {
			// Se ho un record lo elaboro
			SourceBean row = (SourceBean) ((SourceBean) result).getAttribute("ROW");
			if (row != null) {
				return elabora(row);
			} else {
				_logger.debug("Impossibile estrarre il record con progressivo " + currentPrgObjApp
						+ " dalla tabella di appoggio. Proseguo con il record successivo.");

				return estrai();
			}
		} else if (result instanceof Exception) {
			_logger.debug("Impossibile estrarre il record con progressivo " + currentPrgObjApp
					+ " dalla tabella di appoggio. Dettagli: " + result.toString()
					+ ". Proseguo con il record successivo.");

			return estrai();
		} else {
			_logger.debug("Impossibile estrarre il record con progressivo " + currentPrgObjApp
					+ " dalla tabella di appoggio. Proseguo con il record successivo.");

			return estrai();
		}
	}

	/**
	 * Elabora il record corrente secondo quanto indicato nel costruttore e restituisce la Hashtable creata. <br>
	 * Tale Hashtable conterra i valori del record corrente sotto forma di BigDecimal, di String, di Date o di qualsiasi
	 * altro tipo estratto dalla tabella. Ogni valore sara associato al nome della corrispondente colonna estratto dal
	 * ResultSet utilizzando il ResultSetMetaData associato. I valori nulli non saranno inseriti.
	 */
	private Hashtable elabora(SourceBean result) throws SQLException {

		Hashtable recordTable = new Hashtable();

		// estraggo gli attributi dal SourceBean e popolo la map
		Vector v = result.getContainedAttributes();
		for (int i = 0; i < v.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) v.get(i);
			// Ritrovo il nome della colonna
			String colName = attribute.getKey();
			// Ritrovo il dato associato
			Object colValue = attribute.getValue();
			// Se non è nullo lo inserisco in tabella
			if ((colName != null) && (colValue != null)) {
				// Se è una data la riformatto a stringa DD/MM/YYYY, è
				// necessario per come sono stati
				// implementati i controlli
				if (colValue instanceof java.util.Date) {
					colValue = formatter.format(colValue);
				}
				recordTable.put(colName, colValue);
			}
		}
		recordTable.put("CONTEXT", "validazioneMassiva");
		return recordTable;
	}
}