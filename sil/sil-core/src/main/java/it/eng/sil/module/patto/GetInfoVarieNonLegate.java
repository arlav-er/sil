package it.eng.sil.module.patto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Riceve la richiesta di una singola lettura nel caso si apra una popup relativa alle indisponibilita' temporanee per
 * es. oppure di piu' letture, nel caso si apra la popup relativa a tutte le info da legare al patto. I moduli saranno
 * disponibili con la chiave del cod_lst_tab (Es. "M_GETTABELLENONLEGATEALPATTO.AM_IND_T.ROWS.ROW")
 */
public class GetInfoVarieNonLegate extends AbstractSimpleModule {
	private static Map stms = new HashMap();

	static {
		stms.put("AM_IND_T", "QUERY_INDISP_TEMP");
		stms.put("AM_CM_IS", "QUERY_CM_ISCR");
		stms.put("PR_STU", "QUERY_PR_STUDIO");
		stms.put("PR_ESP_L", "QUERY_PR_ESP_LAV");
		stms.put("PR_COR", "QUERY_PR_CORSO");
		stms.put("PR_MAN", "QUERY_PR_MANSIONE");
		stms.put("AM_MB_IS", "QUERY_AM_MB_IS");
		stms.put("AM_OBBFO", "QUERY_AM_OBBFO");
		stms.put("AM_EX_PS", "QUERY_AM_EX_PS");
		stms.put("PR_IND", "QUERY_PR_IND");
		stms.put("OR_PER", "QUERY_OR_PER");
		stms.put("AG_LAV", "QUERY_AG_LAV");
		stms.put("DE_IMPE_L", "QUERY_DE_IMPE_L");
		stms.put("DE_IMPE_C", "QUERY_DE_IMPE_C");
		stms.put("DE_IMPE_C_AZ", "QUERY_DE_IMPE_C_AZ");
		stms.put("DE_IMPE_AZ", "QUERY_DE_IMPE_AZ");

	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		Map stmNames = getStmNames(request);
		SourceBean res = null;
		Iterator stmItr = stmNames.keySet().iterator();

		// SourceBean root = new SourceBean("M_GETTABELLENONLEGATEALPATTO");
		boolean error = false;
		int msgSuccess = disableMessageIdSuccess();
		int msgFail = disableMessageIdFail();

		while (stmItr.hasNext()) {
			String codLstTab = (String) stmItr.next();
			setSectionQuerySelect((String) stmNames.get(codLstTab));

			res = doSelect(request, response, false);

			if (res == null) {
				error = true;

				break;
			}

			response.setAttribute(codLstTab, res);
		}
		res = getPattoAperto(request, response);
		/*
		 * setSectionQuerySelect("QUERY_GET_PATTO_APERTO"); res = doSelect(request, response, false);
		 */
		if (res == null) {
			error = true;
		} else if (res.containsAttribute("row.prgpattolavoratore") || res.containsAttribute("row.prgpattounitaazienda"))
			response.setAttribute("PATTO_APERTO", res);

		if (error) {
			setMessageIdFail(MessageCodes.General.GET_ROW_FAIL);
		} else {
			setMessageIdSuccess(msgSuccess);
		}

		/***********************************************************************
		 * response.delAttribute("ROWS");/ response.setAttribute(root);
		 */
		LogUtils.logDebug("service()", "questo benedetto sourcebean", response);
	}

	private Map getStmNames(SourceBean request) {
		List l = null;
		Object o = request.getAttribute("COD_LST_TAB");

		if (o instanceof Vector) {
			l = (List) o;
		} else {
			l = new ArrayList();
			l.add(o);
		}

		Map stmMap = new HashMap();

		for (int i = 0; i < l.size(); i++) {
			stmMap.put(l.get(i), stms.get(l.get(i)));
		}

		return stmMap;
	}

	private boolean isPattoAzienda(SourceBean request) {
		String page = request.containsAttribute("pageChiamante") ? (String) request.getAttribute("pageChiamante")
				: null;
		if ((page != null) && page.equalsIgnoreCase("PattoImpegniAziendaPage"))
			return true;
		else
			return false;
	}

	private SourceBean getPattoAperto(SourceBean request, SourceBean response) {
		SourceBean res = null;
		String queryName = null;
		if (isPattoAzienda(request)) {
			queryName = "QUERY_GET_PATTO_AZIENDA_APERTO";
		} else {
			queryName = "QUERY_GET_PATTO_APERTO";
		}
		setSectionQuerySelect(queryName);
		res = doSelect(request, response, false);
		return res;
	}
}
