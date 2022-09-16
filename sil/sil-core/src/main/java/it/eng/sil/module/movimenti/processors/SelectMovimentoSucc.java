/*
 * Creato il 14-ott-04
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Sceglie il movimento successivo a partire dai dati del movimento corrente
 * <p>
 * 
 * @author roccetti
 */
public class SelectMovimentoSucc implements RecordProcessor {
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
	public SelectMovimentoSucc(String name, TransactionQueryExecutor transexec) {
		super();
		this.name = name;
		trans = transexec;
	}

	/**
	 * Processa il record. Cerca le proprieta PRGAZIENDA, PRGUNITA, CDNLAVORATORE, DATINIZIOMOV, PRGMOVIMENTO ed esegue
	 * la query per cercare l'eventuale presenza di movimenti successivi collegabili
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

		// Controllo che i parametri necessario per la query di select ci siano
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		BigDecimal prgAzienda = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
		BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");
		BigDecimal prgMov = (BigDecimal) record.get("PRGMOVIMENTO");

		if (prgAzienda == null || prgUnita == null || cdnLav == null || datInizioMov == null) {
			// Errore nella ricerca del successivo
			warnings.add(new Warning(MessageCodes.ImportMov.ERR_FIND_MOV_SUCC, ""));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Testo della query
		String statement = "SELECT PRGMOVIMENTO " + "FROM AM_MOVIMENTO MOV " + "WHERE MOV.CDNLAVORATORE = " + cdnLav
				+ " " + "AND MOV.PRGAZIENDA = " + prgAzienda + " " + "AND MOV.PRGUNITA = " + prgUnita + " "
				+ "AND NVL(TO_CHAR(MOV.PRGMOVIMENTOPREC), 'null') = 'null' " + "AND MOV.DATINIZIOMOV >= TO_DATE('"
				+ datInizioMov + "', 'DD/MM/YYYY') " + "AND PRGMOVIMENTO != "
				+ (prgMov == null ? "null" : prgMov.toString()) + " " + "AND MOV.CODTIPOMOV != 'AVV' "
				+ "AND MOV.CODSTATOATTO = 'PR' ";

		// Eseguo la query
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(statement, trans);
		} catch (Exception e) {
			// Errore nella ricerca del successivo
			warnings.add(new Warning(MessageCodes.ImportMov.ERR_FIND_MOV_SUCC, ""));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Esamino il risultato
		if (result.containsAttribute("ROW.PRGMOVIMENTO")) {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_SUCC_COMP, ""));
		}

		if (warnings.size() > 0) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else
			return null;

	}
}