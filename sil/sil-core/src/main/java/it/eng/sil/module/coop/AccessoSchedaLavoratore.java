package it.eng.sil.module.coop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.sil.security.User;
import it.eng.sil.util.Linguetta;
import it.eng.sil.util.Linguette;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class AccessoSchedaLavoratore {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccessoSchedaLavoratore.class.getName());
	Vector modules;

	public AccessoSchedaLavoratore() {

		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean pageBean = (SourceBean) configure.getFilteredSourceBeanAttribute("PAGES.PAGE", "NAME",
				"coop_scheda_lavoratore");
		modules = pageBean.getAttributeAsVector("MODULES.MODULE");
	}

	public List moduliAccessibili(User user) {
		ArrayList moduliAccessibili = new ArrayList();
		Linguette l = new Linguette(user, 140, "CoopAnagDatiPersonaliPage", new BigDecimal(0));
		List linguette = l.getLinguette();
		for (int i = 0; i < linguette.size(); i++) {
			Linguetta linguetta = (Linguetta) linguette.get(i);
			if (!Utils.notNull(linguetta.getStrpage()).equals("")) {
				String moduleName = getModuloDallaPage(linguetta.getStrpage());
				if (!moduleName.equals(""))
					moduliAccessibili.add(moduleName);
				_logger.debug("impossibile recuperare il nome del modulo del servizio relativo alla page "
						+ linguetta.getStrpage() + ". L'informazione per questa page non verra' richiesta.");

			}

		}
		return moduliAccessibili;

	}

	/**
	 * @param string
	 * @return
	 */
	private String getModuloDallaPage(String pageName) {
		String moduleName = "";
		for (int i = 0; i < modules.size(); i++) {
			SourceBean module = (SourceBean) modules.get(i);
			String pageRef, name;
			pageRef = (String) module.getAttribute("PAGE_REF");
			name = (String) module.getAttribute("NAME");
			if (pageRef.equalsIgnoreCase(pageName)) {
				moduleName = name;
				break;
			}
		}
		return moduleName;
	}

	public HashMap keyDettaglioModuli() {
		HashMap keyDettaglioModuli = new HashMap();
		for (int i = 0; i < modules.size(); i++) {
			SourceBean module = (SourceBean) modules.get(i);
			String pageRef, responseName, prefixInfo;
			pageRef = (String) module.getAttribute("PAGE_REF");
			responseName = (String) module.getAttribute("response_name");
			prefixInfo = (String) module.getAttribute("prefix_info");
			if (prefixInfo == null || prefixInfo.trim().equals(""))
				prefixInfo = "ROWS.ROW";
			String responseKey = responseName.toUpperCase() + "." + prefixInfo.toUpperCase();
			keyDettaglioModuli.put(pageRef.toUpperCase(), responseKey);
		}
		return keyDettaglioModuli;
	}

	public List moduliInserimento() {
		List l = new ArrayList();
		try {

			for (int i = 0; i < modules.size(); i++) {
				SourceBean module = (SourceBean) modules.get(i);
				String moduleInsert, responseName, prefixInfo;
				moduleInsert = (String) module.getAttribute("module_insert");
				if (moduleInsert != null && !moduleInsert.equals("")) {
					SourceBean res = new SourceBean("MOD_INS");
					responseName = (String) module.getAttribute("response_name");
					prefixInfo = (String) module.getAttribute("prefix_info");
					if (prefixInfo == null || prefixInfo.trim().equals(""))
						prefixInfo = "ROWS.ROW";
					String responseKey = responseName.toUpperCase() + "." + prefixInfo.toUpperCase();
					res.setAttribute("module_insert_name", moduleInsert);
					res.setAttribute("module_cache_key", responseKey);
					l.add(res);
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		}
		return l;
	}

	public List moduliCancellazione() {
		List l = new ArrayList();
		try {

			for (int i = modules.size() - 1; i >= 0; i--) {
				SourceBean module = (SourceBean) modules.get(i);
				String moduleDelete, responseName, prefixInfo;
				moduleDelete = (String) module.getAttribute("module_delete");
				if (moduleDelete != null && !moduleDelete.equals("")) {
					SourceBean res = new SourceBean("MOD_DEL");
					responseName = (String) module.getAttribute("response_name");
					prefixInfo = (String) module.getAttribute("prefix_info");
					if (prefixInfo == null || prefixInfo.trim().equals(""))
						prefixInfo = "ROWS.ROW";
					String responseKey = responseName.toUpperCase() + "." + prefixInfo.toUpperCase();
					res.setAttribute("module_delete_name", moduleDelete);
					res.setAttribute("module_cache_key", responseKey);
					res.setAttribute("response_name", responseName.toUpperCase());
					l.add(res);
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		}
		return l;
	}

	public String getDescrizioneModuloInsert(User user, String moduleInsert) {
		String pageRef = null;
		for (int i = 0; i < modules.size(); i++) {
			SourceBean module = (SourceBean) modules.get(i);
			String moduleInsertName = Utils.notNull(module.getAttribute("module_insert"));
			pageRef = (String) module.getAttribute("PAGE_REF");
			if (moduleInsertName.equalsIgnoreCase(moduleInsert)) {
				break;
			} else
				pageRef = null;
		}
		if (pageRef == null)
			return " - ";

		return descrLinguette(user, pageRef);
	}

	public String getDescrizioneModuloDelete(User user, String moduleDelete) {
		String pageRef = null;
		for (int i = 0; i < modules.size(); i++) {
			SourceBean module = (SourceBean) modules.get(i);
			String moduleDeleteName = Utils.notNull(module.getAttribute("module_delete"));
			pageRef = (String) module.getAttribute("PAGE_REF");
			if (moduleDeleteName.equalsIgnoreCase(moduleDelete)) {
				break;
			} else
				pageRef = null;
		}
		if (pageRef == null)
			return " - ";

		return descrLinguette(user, pageRef);
	}

	private String descrLinguette(User user, String pageRef) {
		Linguette l = new Linguette(user, 140, "CoopAnagDatiPersonaliPage", new BigDecimal(0));
		String sezione = null, sottoSezione = null;
		List linguette = l.getLinguette();
		for (int i = 0; i < linguette.size(); i++) {
			Linguetta linguetta = (Linguetta) linguette.get(i);
			if (Utils.notNull(linguetta.getStrpage()).equals("")) {
				sezione = linguetta.getStrdescliv();
				continue;
			}
			String page = linguetta.getStrpage();
			if (page.equalsIgnoreCase(pageRef)) {
				sottoSezione = linguetta.getStrdescliv();
				break;
			}
		}
		return sezione + ": " + sottoSezione;
	}

	public String getModuleSessionName(String pageName) {
		String moduleName = "";
		for (int i = 0; i < modules.size(); i++) {
			SourceBean module = (SourceBean) modules.get(i);
			String pageRef = (String) module.getAttribute("PAGE_REF");
			if (pageRef.equalsIgnoreCase(pageName)) {
				moduleName = (String) module.getAttribute("response_name");
				break;
			}
		}
		return moduleName;
	}
}