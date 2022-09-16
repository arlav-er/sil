package it.eng.sil.util.amministrazione.impatti;

import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class ControlliRicostruzioneStoria {
	public static boolean ricostruisciStoriaAvv(SourceBean movimento, StatoOccupazionaleBean statoOccupazionaleIniziale,
			Object cdnLavoratore, TransactionQueryExecutor transExec) throws Exception {
		boolean flagRicostruisci = false;
		String dataInizio = null;
		String dataInizioStatoOccAperto = "";
		dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		dataInizioStatoOccAperto = (String) statoOccupazionaleIniziale.getDataInizio();
		if (!dataInizioStatoOccAperto.equals("") && DateUtils.compare(dataInizio, dataInizioStatoOccAperto) < 0)
			flagRicostruisci = true;

		if (!flagRicostruisci && DateUtils.compare(dataInizio, DateUtils.getNow()) < 0) {
			SourceBean ultimoMovimento = DBLoad.getUltimoMovimento(cdnLavoratore, transExec);
			String dataInizioUltimoMov = (String) ultimoMovimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);

			if (dataInizioUltimoMov != null && !dataInizioUltimoMov.equals("")
					&& DateUtils.compare(dataInizio, dataInizioUltimoMov) < 0)
				flagRicostruisci = true;

			if (!flagRicostruisci) {
				Vector dids = DBLoad.getDichiarazioniDisponibilita(cdnLavoratore, "01/01/0001" /* dataIizio */,
						transExec);
				if (dids.size() > 0) {
					SourceBean ultimaDid = (SourceBean) dids.get(dids.size() - 1);
					String dataDichiarazione = (String) ultimaDid.getAttribute("datDichiarazione");
					if (DateUtils.compare(dataInizio, dataDichiarazione) <= 0)
						flagRicostruisci = true;
				}
			}
		}
		return flagRicostruisci;
	}

	public static boolean ricostruisciStoriaCes(SourceBean avviamento, String cdnLavoratore,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {

		SourceBean ultimoMovimento = DBLoad.getUltimoMovimento(cdnLavoratore, transExec);
		String dataInizioUltimoMov = (String) ultimoMovimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataInizioMov = (String) avviamento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		boolean skipRicostruzione = requestContainer.getServiceRequest().containsAttribute("FLAG_SKIP_RICOSTRUZIONE");
		boolean ricostruisciStoria = false;
		if (dataInizioUltimoMov != null && !dataInizioUltimoMov.equals("") && dataInizioMov != null
				&& !dataInizioMov.equals("") && DateUtils.compare(dataInizioMov, dataInizioUltimoMov) < 0)
			ricostruisciStoria = true;
		if (!ricostruisciStoria) {
			// leggi la did aperta o chiusa che sia
			Vector dids = DBLoad.getDichiarazioniDisponibilita(cdnLavoratore, "01/01/0001" /* dataInizio */, transExec);
			if (dids.size() > 0) {
				SourceBean ultimaDid = (SourceBean) dids.get(dids.size() - 1);
				String dataFine = (String) ultimaDid.getAttribute("datFine");
				ricostruisciStoria = dataFine != null && !dataFine.equals("")
						&& DateUtils.compare(dataInizioMov, dataFine) < 0;
			}
		}
		if (skipRicostruzione)
			ricostruisciStoria = false;
		return ricostruisciStoria;
	}

	public static void aggiungiAlert(StatoOccupazionaleBean old, StatoOccupazionaleBean nuovoSO, SourceBean request)
			throws Exception {
		String msg = "";
		String vecchio = "";
		String nuovo = "";
		String soAperto = "";
		vecchio = old.getDescrizioneCompleta();
		/*
		 * Per gli avviamenti a T.D. (metodo avviamento di StatoOccupazionaleManager): in caso di visualizzazione
		 * dell'alert di non ricostruzione, deve essere fatto visualizzare lo s.o. aperto e non quello assegnato al
		 * movimento gestito, che è l'ultimo e che nel nostro caso è la cessazione virtuale per il T.D.
		 */
		if (request.containsAttribute("STATO_OCC_APERTO"))
			soAperto = request.getAttribute("STATO_OCC_APERTO").toString();
		nuovo = nuovoSO.getDescrizioneCompleta();
		if ((!nuovo.equals(soAperto)) && (!soAperto.equals("")))
			nuovo = soAperto;
		msg = vecchio + " -> " + nuovo;
		ArrayList alerts = null;
		if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE))
			alerts = (java.util.ArrayList) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
		else {
			alerts = new java.util.ArrayList();
			request.setAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE, alerts);
		}
		alerts.add("Stato occupazionale cambiato: \\n\\r" + msg);
	}
}
