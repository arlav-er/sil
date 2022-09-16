package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class ChiusuraTabelleManager {
	/**
	 * Inserisce un nuovo record solo se esiste un record precedente nello stato di disoccupato o inoccupato NOTA:
	 * scatta il trigger in inserimento che chiude quello precedente.
	 */
	public static void statoOccupazionale(SourceBean request, SourceBean response, AbstractSimpleModule module)
			throws Exception {
		String datFine = (String) request.getAttribute("datFine");

		if ((datFine == null) || datFine.equals("")) {
			datFine = it.eng.afExt.utils.DateUtils.getNow();
		}

		Object datInizio = request.getAttribute("datInizio");
		int i = 0;
		try {
			request.delAttribute("datInizio");
			request.setAttribute("datInizio", datFine);
			String codMotivoFineAtto = (String) request.getAttribute("codMotivoFineAtto");
			StatoOccupazionaleBean.setStatoOccupazionaleAssociatoAlMotivoFineAtto(request, "AM_DIC_D", datFine);
			String codStatoOccupaz = (String) request.getAttribute("codStatoOccupaz");
			if ((codStatoOccupaz != null) && !codStatoOccupaz.equals("")) {
				module.setSectionQueryInsert("QUERY_INSERT_STATO_OCCUPAZ_DEFAULT");
				boolean ret = module.doInsert(request, response);
				if (!ret)
					throw new Exception("Impossibile inserire un nuovo stato occupazionale");
			}
		} finally {
			request.delAttribute("datInizio");
			if (datInizio != null)
				request.setAttribute("datInizio", datInizio);
		}
	}

	public static void statoOccupazionaleOccupato(SourceBean request, SourceBean response, AbstractSimpleModule module)
			throws Exception {
		String datFine = (String) request.getAttribute("datFine");

		if ((datFine == null) || datFine.equals("")) {
			datFine = it.eng.afExt.utils.DateUtils.getNow();
		}

		Object datInizio = request.getAttribute("datInizio");
		int i = 0;
		try {
			request.delAttribute("datInizio");
			request.setAttribute("datInizio", datFine);
			String codStatoOccupaz = "B"; // occupato
			if ((codStatoOccupaz != null) && !codStatoOccupaz.equals("")) {
				request.updAttribute("codStatoOccupaz", codStatoOccupaz);
				module.setSectionQueryInsert("QUERY_INSERT_STATO_OCCUPAZ_DEFAULT");
				boolean ret = module.doInsert(request, response);
				if (!ret)
					throw new Exception("Impossibile inserire un nuovo stato occupazionale");
			}
		} finally {
			request.delAttribute("datInizio");
			if (datInizio != null)
				request.setAttribute("datInizio", datInizio);
		}
	}

	public static void patto297(SourceBean request, SourceBean response, AbstractSimpleModule module) throws Exception {
		// leggo il patto 150
		// se esiste lo chiudo
		module.setSectionQuerySelect("QUERY_SELECT_PATTO");
		SourceBean row = module.doSelect(request, response, false);
		if (row == null)
			throw new Exception("impossibile leggere il patto del lavoratore");
		String tipoPatto = (String) row.getAttribute("ROW.flgPatto297");
		if (tipoPatto != null && tipoPatto.equals("S")) {
			// abbiamo un patto 150: lo chiudo
			// la datfine e' la stessa della did
			module.setSectionQueryUpdate("QUERY_CHIUDI_PATTO");
			if (!module.doUpdate(request, response))
				throw new Exception("impossibile chiudere il patto 150 del lavoratore");
		}
	}
}
