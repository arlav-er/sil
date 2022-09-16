/*
 * Creato il 26-mag-04
 * Author: roccetti
 * 
 */
package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Seleziona il CodComDom del lavoratore sull base del CPILav passato
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class SelectComDomLavFromCPI implements RecordProcessor {
	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di select */
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
	public SelectComDomLavFromCPI(String name, TransactionQueryExecutor transexec) throws NullPointerException {
		this.name = name;
		trans = transexec;
	}

	/**
	 * Processa il record. Cerca la proprieta CODCOMDOM e se non la trova cerca il CODCPILAV e a partire da esso cerca
	 * di ricavare il CODCOMDOM per inserirlo nel record. Se non trova nemmeno il CODCPILAV o se non riesce a ricavare
	 * un COCOMDOM valido lancia una warning.
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

		// Controllo se ho già il codcomdom del lavoratore
		String codcomdom = (String) record.get("CODCOMDOM");
		if (codcomdom != null && !codcomdom.equals("")) {
			return null;
		}

		String codcpilav = (String) record.get("CODCPILAV");

		// Se non ho nemmeno il codcpilav lancio una warning e ritorno
		if (codcpilav == null) {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_COMDOMLAV, "CPI del lavoratore non presente"));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Creo la query e la eseguo
		codcomdom = null;
		String selectquery = "SELECT CODCOM FROM DE_CPI WHERE CODCPI='" + codcpilav + "'";
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
		} catch (Exception e) {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_COMDOMLAV, ""));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Esamino il risultato
		codcomdom = (String) result.getAttribute("ROW.CODCOM");

		// Aggiungo nel record il campo trovato
		if (codcomdom != null) {
			record.put("CODCOMDOM", codcomdom);
			return null;
		} else {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_COMDOMLAV, null));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

	}

}
