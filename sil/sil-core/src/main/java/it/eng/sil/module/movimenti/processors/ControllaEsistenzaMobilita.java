package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Processor che si occupa di effettuare il controlli sulla presenza nel db di una mobilità compatibile con quella che
 * si sta validando
 * 
 * @author Landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ControllaEsistenzaMobilita implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;
	private boolean mobilitaStatoValorizzato = false;

	public ControllaEsistenzaMobilita(String name, TransactionQueryExecutor transexec) {
		this.name = name;
		this.trans = transexec;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		String dataInizioMob = "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			dataInizioMob = record.get("DATINIZIOHID") != null ? record.get("DATINIZIOHID").toString() : "";
		} else {
			// validazione massiva
			dataInizioMob = record.get("DATINIZIO") != null ? record.get("DATINIZIO").toString() : "";
		}
		String cdnlavoratoreMob = record.get("CDNLAVORATORE") != null ? record.get("CDNLAVORATORE").toString() : "";
		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}
		try {
			// CONTROLLO ESISTENZA MOBILITA'
			Object paramsMobilita[] = new Object[1];
			paramsMobilita[0] = cdnlavoratoreMob;
			SourceBean sbMobilita = (SourceBean) trans.executeQuery("CERCA_MOBILITA_DA_VALIDARE_IN_MOB_ISCR",
					paramsMobilita, "SELECT");
			if (sbMobilita == null) {
				return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.GET_ROW_FAIL),
						"errore nel recupero di una mobilità compatibile con quella da validare.", warnings, nested);
			}
			Vector mobilitaLavoratore = sbMobilita.getAttributeAsVector("ROW");
			Vector mobilitaCompatibili = getMobilitaData(mobilitaLavoratore, dataInizioMob);
			if (mobilitaCompatibili.size() == 1) {
				SourceBean ris = new SourceBean("ROWS");
				ris.setAttribute((SourceBean) mobilitaCompatibili.get(0));
				record.put("MOBILITAPRESENTE", ris);
				String codfisc = (String) record.get("STRCODICEFISCALE");
				String cognome = (String) record.get("STRCOGNOME");
				String nome = (String) record.get("STRNOME");
				warnings.add(new Warning(MessageCodes.LogOperazioniValidazioneMobilita.UPDATE_MOB_VALIDAZIONE_MASSIVA,
						"Il lavoratore '" + cognome + " " + nome + "' con codice fiscale: " + codfisc));
			} else {
				if (mobilitaCompatibili.size() > 1) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.LogOperazioniValidazioneMobilita.TROVATE_MOBILITA_COMPATIBILI),
							"Impossibile validare la mobilità.", warnings, nested);
				}
			}

			if (mobilitaStatoValorizzato) {
				warnings.add(new Warning(
						MessageCodes.LogOperazioniValidazioneMobilita.PRESENTE_MOBILITA_STATO_VALORIZZATO, ""));
			}

		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_GESTIONE_MOBILITA),
					"Validazione mobilità fallita.", warnings, nested);
		}

		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}

	public Vector getMobilitaData(Vector mobilitaLavoratore, String dataInizioMob) {
		Vector listaMob = new Vector();
		for (int i = 0; i < mobilitaLavoratore.size(); i++) {
			SourceBean mob = (SourceBean) mobilitaLavoratore.get(i);
			String dataInizio = mob.containsAttribute("DATINIZIOMOBILITA")
					? mob.getAttribute("DATINIZIOMOBILITA").toString()
					: "";
			if (dataInizio.equals(dataInizioMob)) {
				listaMob.add(mob);
			} else {
				BigDecimal statoMob = mob.containsAttribute("CDNMBSTATORICH")
						? new BigDecimal(mob.getAttribute("CDNMBSTATORICH").toString())
						: null;
				if (statoMob != null) {
					mobilitaStatoValorizzato = true;
				}
			}
		}
		return listaMob;
	}

}
