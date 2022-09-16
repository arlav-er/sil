package it.eng.sil.util;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

public class ComboPair {
	private String codRef;
	private String JSArray;

	public ComboPair(Vector rows, String codTipoDoc, String refKeyName) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			String codice = Utils.notNull(row.getAttribute("codice"));
			String descrizione = it.eng.afExt.utils.StringUtils.replace((String) row.getAttribute("descrizione"), "'",
					"\\'");
			String ref = Utils.notNull(row.getAttribute(refKeyName));
			if (codice.equals(codTipoDoc) && codRef == null)
				codRef = ref;
			//
			sb.append("arrayFiglio[" + i + "]=new Option('");
			sb.append(descrizione);
			sb.append("','");
			sb.append(codice);
			sb.append("');\r\n");
			sb.append("arrayFiglio[" + i + "].parent='");
			sb.append(ref);
			sb.append("';\r\n");
		}
		JSArray = sb.toString();
	}

	/*
	 * var persons = [ {firstname : "Malcom", lastname: "Reynolds"}, {firstname : "Kaylee", lastname: "Frye"},
	 * {firstname : "Jayne", lastname: "Cobb"} ];
	 */
	public ComboPair(Vector rows, String codTipoDoc, String refKeyName, String nomeColonnaCod, String nomeColonnaDescr,
			String arrayName) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			String codice = Utils.notNull(row.getAttribute(nomeColonnaCod));
			String descrizione = it.eng.afExt.utils.StringUtils.replace((String) row.getAttribute(nomeColonnaDescr),
					"'", "\\'");
			String ref = Utils.notNull(row.getAttribute(refKeyName));
			if (codice.equals(codTipoDoc) && codRef == null)
				codRef = ref;
			//
			sb.append(arrayName + "[" + i + "]={prgAzione: '");
			sb.append(codice);
			sb.append("', descrPrestazione: '");
			sb.append(descrizione);
			sb.append("'};\r\n");
			/*
			 * sb.append(arrayName+"[" + i + "].parent='"); sb.append(ref); sb.append("';\r\n");
			 */
		}
		JSArray = sb.toString();
	}

	public String makeArrayJSChild() {
		return JSArray;
	}

	public String getCodRef() {
		return this.codRef;
	}
}