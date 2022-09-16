package it.eng.sil.util;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * Alcune cose utili per le ricerche e le liste.
 * 
 * @author Luigi Antenucci
 */
public abstract class RicercaUtils {

	private static final String EMPTY = "";
	private static final String SPACE = " ";

	/**
	 * Per mostrare a video (in JSP) una coppia chiave-valore. Nello specifico, modifica lo StringBuffer "txtOutBuf"
	 * aggiungendo (in modo opportunamente formattato) il testo "textToShow" facendolo seguire dal valore preso dal
	 * SourceBean "sb" nell'attributo indicato da "fieldInSB". I parametri opzionali "opening" e "closing" contengono
	 * stringhe di apertura e chiusura. NB: se il "valueToShow" è nullo o è una stringa vuota, nulla viene fatto (ossia
	 * il txtOutBuf *non* viene modificato).
	 * 
	 * Per default: - opening è stringa vuota ("") - between è uno spazio (" ") - closing è punto-e-virgola e spazio (";
	 * "). Nella versione "Spaziato": - closing è punto-e-virgola e spazio ("&nbsp;&nbsp;"). Nella versione "Punti": -
	 * closing è punto-e-virgola e spazio ("&nbsp;&nbsp;").
	 * 
	 * @author Luigi Antenucci
	 */
	public static final void addCoupleInfo(StringBuffer txtOutBuf, String textToShow, String valueToShow) {
		addCoupleInfo(txtOutBuf, textToShow, valueToShow, EMPTY, SPACE, "; ");
	}

	public static final void addCoupleInfoSpaziato(StringBuffer txtOutBuf, String textToShow, String valueToShow) {
		addCoupleInfo(txtOutBuf, textToShow, valueToShow, EMPTY, SPACE, "&nbsp;&nbsp;");
	}

	public static final void addCoupleInfoPunti(StringBuffer txtOutBuf, String textToShow, String valueToShow) {
		addCoupleInfo(txtOutBuf, textToShow, valueToShow, EMPTY, ":&nbsp;", "; ");
	}

	public static final void addCoupleInfoPuntiSpaziato(StringBuffer txtOutBuf, String textToShow, String valueToShow) {
		addCoupleInfo(txtOutBuf, textToShow, valueToShow, EMPTY, ":&nbsp;", "&nbsp;&nbsp;");
	}

	public static final void addCoupleInfo(StringBuffer txtOutBuf, String textToShow, String valueToShow,
			String opening, String between, String closing) {

		if (StringUtils.isFilled(valueToShow)) {

			txtOutBuf.append("<span class=\"filtriricercaTxt\">");
			txtOutBuf.append(opening);
			if (StringUtils.isFilled(textToShow)) {
				txtOutBuf.append(textToShow);
				txtOutBuf.append(between);
			}
			txtOutBuf.append("</span><span class=\"filtriricercaVal\">");
			txtOutBuf.append(valueToShow);
			txtOutBuf.append("</span><span class=\"filtriricercaTxt\">");
			txtOutBuf.append(closing);
			txtOutBuf.append("</span>");
		}
	}

	/**
	 * Per mostrare a video (in JSP) un campo usato come filtro di ricerca. Viene usata la "addCoupleInfo" con il valore
	 * dell'attributo preso dal SourceBean.
	 * 
	 * NB: se il valore contenuto nell'attributo indicato da "fieldInSB" è nullo o è stringa vuota, nulla viene fatto
	 * (ossia il txtOutBuf *non* viene modificato).
	 * 
	 * @author Luigi Antenucci
	 */
	public static final void addUsedFilter(StringBuffer txtOutBuf, String textToShow, SourceBean sb, String fieldInSB) {
		String valueToShow = SourceBeanUtils.getAttrStrNotNull(sb, fieldInSB);
		addCoupleInfo(txtOutBuf, textToShow, valueToShow);
	}

	public static final void addUsedFilterSpaziato(StringBuffer txtOutBuf, String textToShow, SourceBean sb,
			String fieldInSB) {
		String valueToShow = SourceBeanUtils.getAttrStrNotNull(sb, fieldInSB);
		addCoupleInfoSpaziato(txtOutBuf, textToShow, valueToShow);
	}

	public static final void addUsedFilterPunti(StringBuffer txtOutBuf, String textToShow, SourceBean sb,
			String fieldInSB) {
		String valueToShow = SourceBeanUtils.getAttrStrNotNull(sb, fieldInSB);
		addCoupleInfoPunti(txtOutBuf, textToShow, valueToShow);
	}

	public static final void addUsedFilterPuntiSpaziato(StringBuffer txtOutBuf, String textToShow, SourceBean sb,
			String fieldInSB) {
		String valueToShow = SourceBeanUtils.getAttrStrNotNull(sb, fieldInSB);
		addCoupleInfoPuntiSpaziato(txtOutBuf, textToShow, valueToShow);
	}

	public static final void addUsedFilter(StringBuffer txtOutBuf, String textToShow, SourceBean sb, String fieldInSB,
			String opening, String between, String closing) {

		String valueToShow = SourceBeanUtils.getAttrStrNotNull(sb, fieldInSB);
		addCoupleInfo(txtOutBuf, textToShow, valueToShow, opening, between, closing);
	}

	/**
	 * Usata alla fine delle "addUsedFilter" e "addCoupleInfo" per stampare a video (in realtà, per ottenere la stringa
	 * da stampare) tutti i filtri usati nella ricerca e tutte le coppie di chiavi-valori (ossia tutto ciò che contiene
	 * il "txtOutBuf". Cioè, stampa con opportuna formattazione la "txtOutBuf" - se non è vuota. NB: se txtOutBuf è
	 * nullo o vuoto rende semplicemente una stringa vuota! PS: per default (se non passato) headerMessage vale "Filtri
	 * di ricerca:".
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getTableWithData(StringBuffer txtOutBuf) {
		return getTableWithData(txtOutBuf, "Filtri di ricerca:");
	}

	public static final String getTableWithData(StringBuffer txtOutBuf, String headerMessage) {
		if ((txtOutBuf == null) || (txtOutBuf.length() == 0))
			return EMPTY;
		else {
			String resa;
			resa = "\n" + "<table class=\"filtriricerca\">\n";
			if (StringUtils.isFilled(headerMessage)) {
				resa += "\t<tr><td>" + headerMessage + "</td></tr>\n";
			}
			resa += "\t<tr><td>" + txtOutBuf.toString() + "</td></tr>\n" + "</table>\n";
			return resa;
		}
	}

}
