package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class PrivacyManager {

	public static boolean hasNuovaAutorizzazione(SourceBean request) {
		return ((String) request.getAttribute("PRIVACY_OP")).startsWith("NEW");
	}

	public static boolean hasUpdateAutorizzazione(SourceBean request) {
		return ((String) request.getAttribute("PRIVACY_OP")).startsWith("UPD");
	}

	/**
	 * aggiorno l'eventuale record di privacy con flgAutoriz = N
	 */
	public static void updatePrivacy(SourceBean request, SourceBean response, AbstractSimpleModule module)
			throws Exception {
		String newFlagAut = ((String) request.getAttribute("PRIVACY_OP")).equals("UPD__N") ? "N" : "S";
		request.delAttribute("flgAutoriz");
		request.setAttribute("flgAutoriz", newFlagAut);
		module.setSectionQueryUpdate("QUERY_UPDATE_PRIVACY");

		if (!module.doUpdate(request, response)) {
			throw new Exception("Impossibile aggiornare la privacy del lavoratore");
		}
	}

	/**
	 * Chiude il record attuale (datfine=sysdate) e crea un nuovo record
	 */
	public static void newPrivacy(SourceBean request, SourceBean response, AbstractSimpleModule module)
			throws Exception {
		if (!((String) request.getAttribute("prgPrivacy")).equals("")) {
			module.setSectionQueryUpdate("QUERY_UPDATE_PRIVACY");
			request.setAttribute("datFinePrivacy", DateUtils.getNow());

			if (!module.doUpdate(request, response)) {
				throw new Exception("Impossibile aggiornare la privacy del lavoratore");
			}

			request.delAttribute("datFinePrivacy");
		}

		module.setSectionQueryInsert("QUERY_INSERT_PRIVACY");
		request.delAttribute("flgAutoriz");
		request.delAttribute("datPrivacy");
		String dataInizio = (String) request.getAttribute("datInizio");
		String dataPrivacy = null;
		if (dataInizio.length() == 0)
			dataPrivacy = DateUtils.getNow();
		else
			dataPrivacy = dataInizio;
		request.setAttribute("datPrivacy", dataPrivacy);
		String newFlagAut = ((String) request.getAttribute("PRIVACY_OP")).equals("NEW_S") ? "S" : "N";
		request.setAttribute("flgAutoriz", newFlagAut);

		if (!module.doInsert(request, response)) {
			throw new Exception("Impossibile inserire una nuova di privacy");
		}
	}
}