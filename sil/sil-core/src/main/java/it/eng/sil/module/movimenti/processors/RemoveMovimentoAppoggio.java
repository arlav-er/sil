package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Toglie dalla tabella AM_MOVIMENTO_APPOGGIO o AM_MOV_APP_ARCHIVIO i record correttamente inseriti nel DB
 * <p>
 * 
 * @author Paolo Roccetti modified 30-07-2007 Mauro Riccardi
 */
public class RemoveMovimentoAppoggio implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RemoveMovimentoAppoggio.class.getName());
	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di delete */
	private TransactionQueryExecutor trans;

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 */
	public RemoveMovimentoAppoggio(String name, TransactionQueryExecutor transexec) {
		this.name = name;
		trans = transexec;
	}

	/**
	 * Processa il record. Cerca la proprieta PRGMOVIMENTOAPP e rimuove il record associato nella tabella
	 * AM_MOVIMENTO_APPOGGIO o AM_MOV_APP_ARCHIVIO Se esiste anche un progressivo del movimento di avviamento da CVE
	 * rimuove anche quello.
	 * <p>
	 * 
	 * @exception SourceBeanException
	 *                Se avviene un errore nella creazione del SourceBean di risposta
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}

		// Controllo che il parametro necessario per la query di delete ci sia
		Object prgmovapp = record.get("PRGMOVIMENTOAPP");
		Object curContext = record.get("CONTEXT");

		if (prgmovapp == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Impossibile trovare il progressivo del movimento da cancellare ", warnings, nested);
		}

		String table = "AM_MOVIMENTO_APPOGGIO";
		String tableAgevolazioni = "AM_MOV_AGEV_APP";

		// Cancello i riferimenti al record nella tabella di appoggio o di
		// archivio
		// Creo la query e la eseguo
		if (curContext.equals("validaArchivio")) {
			table = "AM_MOV_APP_ARCHIVIO";
			tableAgevolazioni = "AM_MOV_AGEV_APP_ARCHIVIO";
		}

		String deleteparentquery = "UPDATE " + table
				+ " SET PRGMOVIMENTOAPPCVE = null, NUMKLOMOVAPP = NUMKLOMOVAPP + 1 WHERE PRGMOVIMENTOAPPCVE = "
				+ prgmovapp;
		Object result = null;
		try {
			result = trans.executeQueryByStringStatement(deleteparentquery, null, TransactionQueryExecutor.DELETE);
		} catch (EMFInternalError error) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"RemoveMovimentoAppoggio::processRecord(): query di rimozione dei movimenti parent fallita!",
					(Exception) error);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rimuovere il movimento dalla lista dei movimenti da validare", warnings, nested);
		}

		// Esamino il risultato
		if (!((result instanceof Boolean) && (((Boolean) result).booleanValue() == true))) {
			_logger.debug("RemoveMovimentoAppoggio::processRecord(): query di rimozione dei movimenti parent fallita!");

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rimuovere il movimento dalla lista dei movimenti da validare", warnings, nested);
		}

		// Cancello il movimento processato dalla tabella
		// Creo la query e la eseguo
		String deletequery = "DELETE FROM " + tableAgevolazioni + " WHERE PRGMOVIMENTOAPP = " + prgmovapp;
		result = null;
		try {
			result = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
		} catch (EMFInternalError error) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"RemoveMovimentoAppoggio::processRecord(): query di rimozione del movimento processato fallita!",
					(Exception) error);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rimuovere il movimento dalla lista dei movimenti da validare", warnings, nested);
		}

		deletequery = "DELETE FROM " + table + " WHERE PRGMOVIMENTOAPP = " + prgmovapp;
		result = null;
		try {
			result = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
		} catch (Exception error) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"RemoveMovimentoAppoggio::processRecord(): query di rimozione del movimento processato fallita!",
					(Exception) error);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rimuovere il movimento dalla lista dei movimenti da validare", warnings, nested);
		}

		// Esamino il risultato
		if (result instanceof Exception) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"RemoveMovimentoAppoggio::processRecord(): query di rimozione del movimento processato fallita!",
					(Exception) result);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rimuovere il movimento dalla lista dei movimenti da validare", warnings, nested);
		} else if ((result instanceof Boolean) && (((Boolean) result).booleanValue() == true)) {
			// Controllo se esiste un progressivo dell'avviamento collegato alla
			// cessazione (da cancellare comunque)
			if (record.containsKey("PRGMOVIMENTOAPPCVE")) {
				result = null;
				// Creo la query e la eseguo
				deletequery = "DELETE FROM " + tableAgevolazioni + " WHERE PRGMOVIMENTOAPP = "
						+ record.get("PRGMOVIMENTOAPPCVE");
				try {
					result = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
				} catch (Exception error) {
					// Se la query fallisce non segnalo nulla all'utente, al
					// limite rimane un record fantasma sulla tabella di
					// appoggio
				}
				result = null;
				deletequery = "DELETE FROM " + table + " WHERE PRGMOVIMENTOAPP = " + record.get("PRGMOVIMENTOAPPCVE");
				try {
					result = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
				} catch (EMFInternalError error) {
					// Se la query fallisce non segnalo nulla all'utente, al
					// limite rimane un record fantasma sulla tabella di
					// appoggio
				}
			}
			return null;
		} else {
			_logger.debug(
					"RemoveMovimentoAppoggio::processRecord(): query di rimozione del movimento processato fallita!");

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile rimuovere il movimento dalla lista dei movimenti da validare", warnings, nested);
		}
	}
}