/*
 * Creato il 14-set-04
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
 * Seleziona il CPI dell'azienda dal Codice del comune della stessa e lo inserisce nel record
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class SelectCPIAziendaFromComune implements RecordProcessor {
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
	 * @param dbconection
	 *            connessione da utilizzare per le query
	 */
	public SelectCPIAziendaFromComune(String name, TransactionQueryExecutor transexec) {
		this.name = name;
		trans = transexec;
	}

	/**
	 * Processa il record. Cerca la proprieta CODUACOM e se la trova cerca il CODCPI associato a tale comune per
	 * inserirlo nel record. Se non trova la proprietà CODUACOM o se non riesce a ricavare un CODCPI valido lancia una
	 * warning.
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

		// Controllo se ho già il codcpi dell'azienda del lavoratore
		String codCpiUaz = (String) record.get("CODCPI");
		if (codCpiUaz != null && !codCpiUaz.equals("")) {
			return null;
		}

		String codComUaz = (String) record.get("CODUACOM");

		// Se non ho nemmeno il codcpilav lancio una warning e ritorno
		if (codComUaz == null || codComUaz.equals("")) {
			warnings.add(new Warning(MessageCodes.ImportMov.ERR_FIND_CODUAZCOM,
					"Comune dell'unita produttiva non presente nel movimento"));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Creo la query e la eseguo
		codCpiUaz = null;
		String selectquery = "SELECT CODCPI FROM DE_COMUNE WHERE CODCOM='" + codComUaz + "'";
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
		} catch (Exception e) {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_CPIUAZ, ""));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Esamino il risultato
		codCpiUaz = (String) result.getAttribute("ROW.CODCPI");

		// Aggiungo nel record il campo trovato
		if (codCpiUaz != null && !codCpiUaz.equals("")) {
			record.put("CODCPI", codCpiUaz);
			return null;
		} else {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_CPIUAZ, null));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}
	}

}