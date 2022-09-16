package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Processor che si occupa di effettuare controlli server sulla mobilità da validare manualmente
 * 
 * @author Landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ControllaCampiValidazioneMobilita implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;
	private String contestoValidazione = "";

	public ControllaCampiValidazioneMobilita(String name, String contesto, TransactionQueryExecutor transexec)
			throws Exception {
		this.name = name;
		this.trans = transexec;
		this.contestoValidazione = contesto;
	}

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
		// Inizio controllo sulle date
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		Object dataInizioMob = null;
		String dataFineMov = "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			dataInizioMob = record.get("DATINIZIOHID");
			dataFineMov = record.containsKey("DATFINEMOVHID") ? record.get("DATFINEMOVHID").toString() : "";
		} else {
			// validazione massiva
			dataInizioMob = record.get("DATINIZIO");
			dataFineMov = record.containsKey("DATFINEMOV") ? record.get("DATFINEMOV").toString() : "";
		}
		try {
			if (!dataFineMov.equals("")) {
				String dataInizioMobCorretta = DateUtils.giornoSuccessivo(dataFineMov);
				if (dataInizioMob == null || !dataInizioMob.toString().equals(dataInizioMobCorretta)) {
					record.put("DATINIZIO", dataInizioMobCorretta);
					if (context.equalsIgnoreCase("valida")) {
						record.put("DATINIZIOHID", dataInizioMobCorretta);
					}
					warnings.add(new Warning(
							MessageCodes.LogOperazioniValidazioneMobilita.AGGIORNATA_DATA_INIZIO_MOBILITA, ""));
				}
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Fallito controllo sulla data inizio mobilità", warnings, nested);
		}

		String datInizioMob = "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			datInizioMob = record.containsKey("DATINIZIOHID") ? record.get("DATINIZIOHID").toString() : "";
		} else {
			// validazione massiva
			datInizioMob = record.containsKey("DATINIZIO") ? record.get("DATINIZIO").toString() : "";
		}
		String datFineMob = record.containsKey("DATFINE") ? record.get("DATFINE").toString() : "";
		String datFineMobOrig = record.containsKey("DATFINEORIG") ? record.get("DATFINEORIG").toString() : "";
		String datMaxDiff = record.containsKey("DATMAXDIFF") ? record.get("DATMAXDIFF").toString() : "";
		String datFineIndennita = record.containsKey("DATFINEINDENNITA") ? record.get("DATFINEINDENNITA").toString()
				: "";

		try {
			if (!datInizioMob.equals("") && !datFineMobOrig.equals("")
					&& DateUtils.compare(datInizioMob, datFineMobOrig) > 0) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.Mobilita.ERR_DATFINEMOBORIG_DATINIZIO), "", warnings, nested);
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Fallito controllo sulla data fine mobilità originaria", warnings, nested);
		}
		try {
			if (!datInizioMob.equals("") && !datFineMob.equals("") && DateUtils.compare(datInizioMob, datFineMob) > 0) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.Mobilita.ERR_DATFINEMOB_DATINIZIO), "", warnings, nested);
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Fallito controllo sulla data fine mobilità", warnings, nested);
		}
		try {
			if (!datFineMob.equals("") && !datMaxDiff.equals("") && DateUtils.compare(datFineMob, datMaxDiff) > 0) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.Mobilita.ERR_DATAMAXDIFF_DATFINEMOB), "", warnings, nested);
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Fallito controllo sulla data fine max differimento", warnings, nested);
		}
		try {
			if (!datMaxDiff.equals("") && !datFineIndennita.equals("")
					&& DateUtils.compare(datFineIndennita, datMaxDiff) > 0) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.Mobilita.ERR_DATAMAXDIFF_DATFINEINDENNITA), "", warnings, nested);
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Fallito controllo sulla data fine max differimento", warnings, nested);
		}
		// Fine controlli sulle date

		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}

}
