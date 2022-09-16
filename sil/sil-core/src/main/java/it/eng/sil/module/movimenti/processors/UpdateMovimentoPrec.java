package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Aggiorna il movimento precedente a partire dai dati del movimento corrente appena inserito
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class UpdateMovimentoPrec implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateMovimentoPrec.class.getName());
	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di select */
	private TransactionQueryExecutor trans;
	/** Identificatore utente */
	private BigDecimal userId;

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 */
	public UpdateMovimentoPrec(String name, TransactionQueryExecutor transexec, BigDecimal user) {
		this.name = name;
		trans = transexec;
		this.userId = user;
	}

	/**
	 * Processa il record. Cerca le proprieta PRGMOVIMENTOPREC, PRGMOVIMENTO, DATAINIZIOMOV, CODTIPOMOV, NUMKLOMOVPREC e
	 * modifica i campi del movimento precedente sulla base di essi.
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

		// Se sto inserendo un'assunzione o un mov non collegato ritorno null e
		// sono a posto
		String codTipoMov = (String) record.get("CODTIPOMOV");
		String collegato = (String) record.get("COLLEGATO");
		if ("AVV".equalsIgnoreCase(codTipoMov) || collegato.equalsIgnoreCase("nessuno")) {
			return null;
		}

		// Controllo che i parametri necessario per la query di update ci siano
		BigDecimal prgMovimentoPrec = (BigDecimal) record.get("PRGMOVIMENTOPREC");
		BigDecimal prgMovimento = (BigDecimal) record.get("PRGMOVIMENTO");
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		String dataFineMovPrec = "";
		BigDecimal numKloMovPrec = (BigDecimal) record.get("NUMKLOMOVPREC");

		// DONA
		// in alcune circostanze il NUMKLOMOVPREC non viene aggiornato
		// e si ha una ECCEZIONE dal Trigger
		BigDecimal numKloMovPrecRecuperato = (BigDecimal) record.get("NUMKLOMOVPREC");
		if (prgMovimentoPrec != null) {
			String selectQueryNumKloPrec = "select numklomov from am_movimento where prgmovimento ="
					+ prgMovimentoPrec.toString();
			try {
				SourceBean sbNumKlo = ProcessorsUtils.executeSelectQuery(selectQueryNumKloPrec, trans);
				if (sbNumKlo != null && sbNumKlo.containsAttribute("ROW")) {
					numKloMovPrecRecuperato = (BigDecimal) sbNumKlo.getAttribute("ROW.NUMKLOMOV");
					numKloMovPrecRecuperato = numKloMovPrecRecuperato.add(new BigDecimal(1));
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile aggiornare il movimento precedente.", e);

				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
						"Impossibile aggiornare il movimento precedente.", warnings, nested);
			}

		}
		if (numKloMovPrecRecuperato.compareTo(numKloMovPrec) != 0) {
			numKloMovPrec = numKloMovPrecRecuperato;
		}

		BigDecimal prgStatoOccupaz = (BigDecimal) record.get("PRGSTATOOCCUPAZ");

		// DONA 19/02/2007: bisogna inserire anche le note nel movimento
		// precedente
		String strNote = (String) record.get("STRNOTEMOVPREC");
		if (strNote == null) {
			strNote = "";
		}

		if (prgMovimentoPrec == null || prgMovimento == null || datInizioMov == null || codTipoMov == null
				|| numKloMovPrec == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Impossibile aggiornare il movimento precedente, dati mancanti.", warnings, nested);
		}

		// Inizio modifica 13/05/2008 : è possibile inserire due trasformazioni nello stesso giorno (stessa azienda) in
		// due casi:
		// 1)motivo trasformazione differenti
		// 2)entrambi hanno motivo trasformazione DL (distacco lavoratore), nel caso di proroghe o cessazioni del
		// distacco
		try {
			if (codTipoMov.equalsIgnoreCase("TRA")) {
				String datInizioMovPrec = record.get("DATINIZIOMOVPREC") != null
						? (String) record.get("DATINIZIOMOVPREC")
						: "";
				if (!datInizioMovPrec.equals("")) {
					if (DateUtils.compare(datInizioMov, datInizioMovPrec) > 0) {
						dataFineMovPrec = DateUtils.giornoPrecedente(datInizioMov);
					} else {
						dataFineMovPrec = datInizioMovPrec;
					}

				} else {
					dataFineMovPrec = DateUtils.giornoPrecedente(datInizioMov);
				}
			} else {
				if (codTipoMov.equalsIgnoreCase("CES")) {
					dataFineMovPrec = datInizioMov;
				} else {
					dataFineMovPrec = DateUtils.giornoPrecedente(datInizioMov);
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore calcolo data fine movimento precedente.", e);
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Errore calcolo data fine movimento precedente.", warnings, nested);
		}
		// Fine modifica 13/05/2008

		// Creo la query e la eseguo
		String updatequery = "UPDATE AM_MOVIMENTO SET " + " PRGMOVIMENTOSUCC = " + prgMovimento + ", "
				+ " CODMONOTIPOFINE = '" + codTipoMov.substring(0, 1) + "', " + " cdnUtMod = " + userId
				+ ", dtmMod = sysdate, " + " strNote = '" + StringUtils.formatValue4Sql(strNote) + "', "
				+ " numKloMov = " + numKloMovPrec + ", "
				// + (prgStatoOccupaz!=null? (" prgStatoOccupaz =
				// "+prgStatoOccupaz + ", "): "")
				+ " DATFINEMOVEFFETTIVA = TO_DATE('" + dataFineMovPrec + "', 'DD/MM/YYYY') " + " WHERE PrgMovimento = "
				+ prgMovimentoPrec + " ";

		// Inserisco il record
		Object result = null;
		try {
			result = trans.executeQueryByStringStatement(updatequery, null, TransactionQueryExecutor.UPDATE);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile aggiornare il movimento precedente.", e);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile aggiornare il movimento precedente.", warnings, nested);
		}

		// Se ho un'eccezione nel risultato lo segnalo
		if (result instanceof Exception) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile aggiornare il movimento precedente.",
					(Exception) result);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile aggiornare il movimento precedente.", warnings, nested);
		} else if (result instanceof Boolean && ((Boolean) result).booleanValue() == true) {
			return null;
		} else {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
					"Impossibile aggiornare il movimento precedente.", warnings, nested);
		}
	}
}