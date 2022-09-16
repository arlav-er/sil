package it.eng.sil.module.agenda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class ShowApp {
	/* @author: Stefania Orioli */
	public ShowApp() {
	}

	private static String mesi[] = { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto",
			"Settembre", "Ottobre", "Novembre", "Dicembre" };
	private static String giorni[] = { "&nbsp;", "LUN", "MAR", "MER", "GIO", "VEN", "SAB", "DOM" };
	private static String giorniEst[] = { "&nbsp;", "Luned&igrave;", "Marted&igrave;", "Mercoled&igrave;",
			"Gioved&igrave;", "Venerd&igrave;", "Sabati", "Domeniche" };
	private static int mapping_mesiDB[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	// Giorni festivi e/o non lavorativi
	private static ArrayList giorniNL = null;
	// Giorni della settimana definiti come non lavorativi
	private static ArrayList gsett = null;

	// ArrayList con le date della settimana selezionata per il ciclo con la
	// griglia
	// degli appuntamenti
	private static ArrayList settimana = null;

	private static SourceBean content = null;

	public static String listaAppSettimana(RequestContainer requestContainer, SourceBean serviceResponse,
			String codCpi) {
		StringBuffer buf = new StringBuffer();
		SourceBean req = requestContainer.getServiceRequest();

		int nrosDB = Integer.parseInt(req.getAttribute("nrosDB").toString());
		int annoDB = Integer.parseInt(req.getAttribute("annoDB").toString());
		String mod = "1";
		String cod_vista = "";
		int giornoDB, meseDB, mese, anno;
		mese = Integer.parseInt(req.getAttribute("mese").toString());
		anno = Integer.parseInt(req.getAttribute("anno").toString());

		if (req.containsAttribute("MOD")) {
			mod = req.getAttribute("MOD").toString();
		}
		if (req.containsAttribute("cod_vista")) {
			cod_vista = req.getAttribute("cod_vista").toString();
		}

		content = (SourceBean) serviceResponse.getAttribute("MGIORNI_NL");
		// setGiorniNL(annoDB);
		giorniNL = new ArrayList(CalUtils.setGiorniNL(anno, serviceResponse, "MGIORNI_NL"));
		gsett = new ArrayList(CalUtils.setGsett(anno, serviceResponse, "MGIORNI_NL"));

		int i, j;
		SourceBean row;
		String data;
		boolean trovato = false;

		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.clear(Calendar.MONTH);
		cal.clear(Calendar.DAY_OF_YEAR);
		cal.set(Calendar.YEAR, annoDB);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.WEEK_OF_YEAR, nrosDB);
		int gi, mi, ai;
		String mesei = "";
		gi = cal.get(Calendar.DATE);
		mi = cal.get(Calendar.MONTH);
		mesei = mesi[mi];
		ai = cal.get(Calendar.YEAR);
		cal.add(Calendar.DATE, 6);
		int gf, mf, af;
		String mesef = "";
		gf = cal.get(Calendar.DATE);
		mf = cal.get(Calendar.MONTH);
		mesef = mesi[mf];
		af = cal.get(Calendar.YEAR);

		cal.set(ai, mi, gi);
		// Predisposizione LINK DI SCORRIMENTO
		Calendar settPrec = Calendar.getInstance();
		Calendar settSucc = Calendar.getInstance();
		int nrop, annop, nros, annos, m;
		settPrec.set(ai, mi, gi);
		settPrec.add(Calendar.WEEK_OF_YEAR, -1);
		nrop = settPrec.get(Calendar.WEEK_OF_YEAR);
		annop = settPrec.get(Calendar.YEAR);
		if ((nrop == 1) && (annop < ai)) {
			annop += 1;
		}
		String linkSettPrec = "<a href=\"AdapterHTTP?PAGE=PCalendario" + "&CODCPI=" + codCpi;
		linkSettPrec += "&nrosDB=" + nrop + "&annoDB=" + annop;
		linkSettPrec += "&mese=" + mese + "&anno=" + anno;
		linkSettPrec += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";
		settSucc.set(ai, mi, gi);
		settSucc.add(Calendar.WEEK_OF_YEAR, 1);
		nros = settSucc.get(Calendar.WEEK_OF_YEAR);
		annos = settSucc.get(Calendar.YEAR);
		m = settSucc.get(Calendar.MONTH);
		if ((m == 11) && (nros == 1)) {
			annos += 1;
		}
		String linkSettSucc = "<a href=\"AdapterHTTP?PAGE=PCalendario" + "&CODCPI=" + codCpi;
		linkSettSucc += "&nrosDB=" + nros + "&annoDB=" + annos;
		linkSettSucc += "&mese=" + mese + "&anno=" + anno;
		linkSettSucc += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";

		// Stondature TOP - Modifica del 10/03/2004 - Stefania Orioli
		buf.append("<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"19\" class=\"cal_header\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		// end top
		// buf.append("<table class=\"lista\" align=\"center\">");
		// INTESTAZIONE
		// buf.append("<tr valign=\"middle\"><td colspan=\"7\"
		// class=\"cal_header\">");
		buf.append(linkSettPrec);
		buf.append("<img src=\"../../img/previous.gif\" alt=\"&lt;&lt;\"></a> ");
		buf.append(gi + " " + mesei + " " + ai + " - ");
		buf.append(gf + " " + mesef + " " + af + " ");
		buf.append(linkSettSucc);
		buf.append("<img src=\"../../img/next.gif\" alt=\"&gt;&gt;\"></a> ");
		// buf.append("</td></tr>");
		// Stondature TOP
		buf.append("</td>");
		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"6\" height=\"19\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr class=\"cal\">");
		buf.append("<td class=\"cal\" width=\"6\"></td>");
		buf.append("<td class=\"cal\" width=\"100%\" align=\"center\">");
		buf.append("<TABLE class=\"appuntamenti\" margin=\"0\" cellspacing=\"0\">");
		// END TOP

		buf.append("<tr><td colspan=\"7\" class=\"cal_titolo\">Appuntamenti della Settimana</td></tr>");
		buf.append("<tr><td class=\"cal_bordato\" width=\"16%\">&nbsp;</td>");

		// GIORNI DELLA SETTIMANA
		String gc = "";
		StringBuffer stringagiorno;
		settimana = new ArrayList();
		for (i = 1; i < 7; i++) {
			giornoDB = cal.get(Calendar.DATE);
			meseDB = mapping_mesiDB[cal.get(Calendar.MONTH)];
			annoDB = cal.get(Calendar.YEAR);
			gc = giornoDB + "/" + cal.get(Calendar.MONTH) + "/" + annoDB;

			stringagiorno = new StringBuffer();
			if (Integer.toString(giornoDB).length() == 1) {
				stringagiorno.append("0" + Integer.toString(giornoDB));
			} else {
				stringagiorno.append(Integer.toString(giornoDB));
			}
			stringagiorno.append("/");
			if (Integer.toString(meseDB).length() == 1) {
				stringagiorno.append("0" + Integer.toString(meseDB));
			} else {
				stringagiorno.append(Integer.toString(meseDB));
			}
			stringagiorno.append("/" + Integer.toString(annoDB));
			settimana.add(stringagiorno.toString());

			if ((gsett.contains(Integer.toString(i)) && CalUtils.checkGsett(i, cal, serviceResponse, "MGIORNI_NL"))
					|| giorniNL.contains(gc)) {
				buf.append("<td class=\"cal_vuoto\" align=\"center\" width=\"14%\">");
				buf.append(giorni[i] + "<br><b>" + giornoDB + "</td>");
			} else {
				buf.append("<td class=\"cal_settimana\" width=\"14%\">");
				buf.append("<a class=\"calh\" href=\"AdapterHTTP?PAGE=PCalendario" + "&CODCPI=" + codCpi);
				buf.append("&MOD=0");
				buf.append("&cod_vista=" + cod_vista);
				buf.append("&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB);
				buf.append("&mese=" + mese + "&anno=" + anno);
				buf.append("\">");
				buf.append(giorni[i] + "<br><b>" + giornoDB + "</b></a></td>");
			}
			cal.add(Calendar.DATE, 1);
		}
		buf.append("</tr>");

		// Nro Eventi
		content = (SourceBean) serviceResponse.getAttribute("MEVENTIS");
		buf.append("<tr><td class=\"cal_titolo\">Eventi</td>");
		if (content != null) {
			Vector rowsEv = content.getAttributeAsVector("ROWS.ROW");
			for (i = 0; i < 6; i++) {
				buf.append("<td class=\"cal_bordato\">");
				j = 0;
				trovato = false;
				while (j < rowsEv.size() && !trovato) {
					row = (SourceBean) rowsEv.elementAt(j);
					data = row.getAttribute("DATA").toString();
					if (data.equals(settimana.get(i))) {
						trovato = true;
						buf.append(row.getAttribute("NRO_EV").toString());
					}
					j++;
				}
				if (!trovato) {
					buf.append("&nbsp;");
				}
				buf.append("</td>");
			}
			buf.append("</tr>");
		}

		// GRIGLIA
		String moduleName = "MSETTIMANA";
		SourceBean app = (SourceBean) serviceResponse.getAttribute(moduleName);
		String strDescFascia = "";
		int numFascia = 0;
		// Fascia 0->8
		strDescFascia = "0->8";
		numFascia = 1;
		Vector rows1 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "1");
		buf.append(showAppFascia(rows1, numFascia, strDescFascia));
		// Fascia 8->10
		strDescFascia = "8->10";
		numFascia = 2;
		Vector rows2 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "2");
		buf.append(showAppFascia(rows2, numFascia, strDescFascia));
		// Fascia 10->12
		strDescFascia = "10->12";
		numFascia = 3;
		Vector rows3 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "3");
		buf.append(showAppFascia(rows3, numFascia, strDescFascia));
		// Fascia 12->14
		strDescFascia = "12->14";
		numFascia = 4;
		Vector rows4 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "4");
		buf.append(showAppFascia(rows4, numFascia, strDescFascia));
		// Fascia 14->16
		strDescFascia = "14->16";
		numFascia = 5;
		Vector rows5 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "5");
		buf.append(showAppFascia(rows5, numFascia, strDescFascia));
		// Fascia Dalle 16
		strDescFascia = "Dalle 16";
		numFascia = 6;
		Vector rows6 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "6");
		buf.append(showAppFascia(rows6, numFascia, strDescFascia));
		// Fascia Non Assegnata
		// strDescFascia = "Non Assegnata";
		// numFascia = 0;
		// Vector rows0 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW",
		// "NUMFASCIAORA", "0");
		// buf.append(showAppFascia(rows0, numFascia, strDescFascia));

		buf.append("</table>");
		// Stondature BOTTOM - Modifiche 10/03/2004 - Stefania Orioli
		buf.append("</td><td class=\"cal\" width=\"6\"></td></tr>");
		buf.append(
				"<tr class=\"cal\"><td class=\"cal_header\" align=\"left\" valign=\"bottom\" width=\"13\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"cal_header\" align=\"right\" valign=\"bottom\" width=\"19\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		// end BOTTOM
		return (buf.toString());
	}

	private static String showAppFascia(Vector rows, int numFascia, String strDescFascia) {
		boolean trovato = false;
		String data = "";
		BigDecimal nroTotali, nro;
		BigDecimal nroSlot;
		SourceBean row = null;
		StringBuffer bf = new StringBuffer();
		String desc = "";
		String numTipo = "";
		int i, j;

		bf.append("<tr class=\"note\"><td class=\"cal_bordato\" align=\"center\">" + strDescFascia + "</td>");
		for (i = 0; i < 6; i++) {
			bf.append("<td class=\"cal_fascia\">");
			j = 0;
			trovato = false;
			while (j < rows.size() && !trovato) {
				row = (SourceBean) rows.elementAt(j);
				data = row.getAttribute("DATA").toString();
				if (data.equals(settimana.get(i))) {
					trovato = true;
					numTipo = row.getAttribute("NUMTIPO").toString();
					if (numTipo.equals("0")) {
						nroTotali = (BigDecimal) row.getAttribute("NUMNUMERO");
						desc = row.getAttribute("STRDESCTIPO").toString();
						bf.append(desc + "&nbsp;" + nroTotali.toString());
					} else {
						nroTotali = new BigDecimal(0);
					}
					if (numTipo.equals("3")) {
						nroSlot = (BigDecimal) row.getAttribute("NUMNUMERO");
						desc = row.getAttribute("STRDESCTIPO").toString();
						bf.append(desc + "&nbsp;" + nroSlot.toString());
					}
				}
				j++;
			}
			if (!trovato) {
				bf.append("&nbsp;");
			} else {
				if (j < rows.size()) {
					row = (SourceBean) rows.elementAt(j);
					if (data.equals(row.getAttribute("DATA").toString())) {
						// Controllo nro app. incongruenti (o non completi)
						numTipo = row.getAttribute("NUMTIPO").toString();
						nro = (BigDecimal) row.getAttribute("NUMNUMERO");
						desc = row.getAttribute("STRDESCTIPO").toString();
						bf.append("<br>" + desc + "&nbsp;" + nro.toString());

						j++;
						if (j < rows.size()) {
							row = (SourceBean) rows.elementAt(j);
							if (data.equals(row.getAttribute("DATA").toString())) {
								// Controllo eventuali app. non completi
								numTipo = row.getAttribute("NUMTIPO").toString();
								nro = (BigDecimal) row.getAttribute("NUMNUMERO");
								desc = row.getAttribute("STRDESCTIPO").toString();
								bf.append("<br>" + desc + "&nbsp;" + nro.toString());
							}
						}
						// Controllo gli Slot...
						j++;
						if (j < rows.size()) {
							row = (SourceBean) rows.elementAt(j);
							if (data.equals(row.getAttribute("DATA").toString())) {
								// Controllo eventuali app. non completi
								numTipo = row.getAttribute("NUMTIPO").toString();
								nro = (BigDecimal) row.getAttribute("NUMNUMERO");
								desc = row.getAttribute("STRDESCTIPO").toString();
								bf.append("<br>" + desc + "&nbsp;" + nro.toString());
							}
						}
					}
				}
			}
			bf.append("</td>");
		}
		bf.append("</tr>");
		return (bf.toString());
	}

	public static String listaEventiGiorno(RequestContainer requestContainer, SourceBean serviceResponse,
			String codCpi) {
		StringBuffer buf = new StringBuffer();
		SourceBean req = requestContainer.getServiceRequest();
		content = (SourceBean) serviceResponse.getAttribute("MEVENTIG");

		String giornoDB = "";
		String meseDB = "";
		String annoDB = "";
		// Istanzio giornoDB, meseDb, annoDb per l'inserimento di un nuovo
		// appuntamento
		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		giornoDB = Integer.toString(oggi.get(5));
		meseDB = Integer.toString(oggi.get(2) + 1);
		annoDB = Integer.toString(oggi.get(1));

		/*
		 * String giorno = (String) req.getAttribute("giorno"); String mese = (String) req.getAttribute("mese"); String
		 * anno = (String) req.getAttribute("anno"); String cod_vista = (String) req.getAttribute("cod_vista");
		 */

		// Profilatura - Stefy 15/07/2004
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "PCalendario");
		boolean canInsEv = attributi.containsButton("NEW_EV");
		boolean canDelEv = attributi.containsButton("DEL_EV");
		// --

		String giorno = StringUtils.getAttributeStrNotNull(req, "giorno");
		String mese = StringUtils.getAttributeStrNotNull(req, "mese");
		String anno = StringUtils.getAttributeStrNotNull(req, "anno");
		String cod_vista = StringUtils.getAttributeStrNotNull(req, "cod_vista");
		String mod = StringUtils.getAttributeStrNotNull(req, "MOD");

		if (req.containsAttribute("giornoDB")) {
			giornoDB = req.getAttribute("giornoDB").toString();
		}
		if (req.containsAttribute("meseDB")) {
			meseDB = req.getAttribute("meseDB").toString();
		}
		if (req.containsAttribute("annoDB")) {
			annoDB = req.getAttribute("annoDB").toString();
		}

		String linkDett = "AdapterHTTP?PAGE=PEvento" + "&CODCPI=" + codCpi + "&giornoDB=" + giornoDB + "&meseDB="
				+ meseDB + "&annoDB=" + annoDB + "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno + "&cod_vista="
				+ cod_vista + "&MOD=" + mod;

		// buf.append("<tr><td class=\"cal_titolo\" align=\"center\"
		// colspan=\"2\">");
		// buf.append("Eventi" + "</td></tr>\n");
		buf.append("<form name=\"formNuovoEv\" action=\"AdapterHTTP\" method=\"POST\" "
				+ "onSubmit=\"return controllaNullaTL();\">"); // GG 8-10-2004
		buf.append("<input name=\"PAGE\" type=\"hidden\" value=\"PInsEvento\"/>");
		buf.append("<input name=\"CODCPI\" type=\"hidden\" value=\"" + codCpi + "\"/>");
		buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>");
		buf.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>");
		buf.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>");
		buf.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>");
		buf.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>");
		buf.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>");
		buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");
		buf.append("<input name=\"cod_vista\" type=\"hidden\" value=\"" + cod_vista + "\"/>");
		buf.append("<tr><td width=\"100%\" class=\"cal_titolo2\" align=\"center\" colspan=\"2\">");
		// Profilatura - Stefy 15/07/2004
		if (canInsEv) {
			buf.append("Eventi <input type=\"submit\" value=\"Inserisci\" class=\"ag_pulsanti\">");
		} else {
			buf.append("Eventi");
		}
		// --
		buf.append("</td></tr>\n");
		buf.append("</form>");

		if (content != null) {
			Vector rows = content.getAttributeAsVector("ROWS.ROW");
			SourceBean row = null;
			String desc, prgEvento;
			int i;
			for (i = 0; i < rows.size(); i++) {
				row = (SourceBean) rows.elementAt(i);
				prgEvento = row.getAttribute("PRGEVENTO").toString();
				desc = row.getAttribute("DESCR_EVENTO").toString();
				buf.append("<tr>\n");
				buf.append("<td class=\"ag_lista\" width=\"16px\">");
				buf.append("<a href=\"" + linkDett + "&prgEvento=" + prgEvento + "\">");
				buf.append("<img src=\"../../img/detail.gif\" alt=\"Dettaglio\"></a>");
				buf.append("</td>");
				buf.append("<td class=\"ag_lista\" align=\"justify\">");
				buf.append(desc);
				buf.append("</td></tr>");
			}
		}
		// buf.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
		/*
		 * buf.append("<form name=\"formNuovoEv\" action=\"AdapterHTTP\" method=\"POST\" " +
		 * "onSubmit=\"return controllaNullaTL();\">"); // GG 8-10-2004 buf.append("<input name=\"PAGE\" type=\"hidden\"
		 * value=\"PInsEvento\"/>"); buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\""+giornoDB+"\"/>");
		 * buf.append("<input name=\"meseDB\" type=\"hidden\" value=\""+meseDB+"\"/>");
		 * buf.append("<input name=\"annoDB\" type=\"hidden\" value=\""+annoDB+"\"/>");
		 * buf.append("<input name=\"giorno\" type=\"hidden\" value=\""+giorno+"\"/>"); buf.append("<input
		 * name=\"mese\" type=\"hidden\" value=\""+mese+"\"/>"); buf.append("<input
		 * name=\"anno\" type=\"hidden\" value=\""+anno+"\"/>"); buf.append("<input
		 * name=\"MOD\" type=\"hidden\" value=\"0\"/>"); buf.append("<input
		 * name=\"cod_vista\" type=\"hidden\" value=\""+cod_vista+"\"/>");
		 * buf.append("<tr><td align=\"center\" colspan=\"2\">");
		 * buf.append("<input type=\"submit\" value=\"Inserisci Nuovo Evento\" class=\"pulsanti\">");
		 * buf.append("</td></tr>"); buf.append("</form>");
		 */
		buf.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
		return buf.toString();
	}

	public static String infoGiorno(RequestContainer requestContainer, SourceBean serviceResponse) {
		StringBuffer buf = new StringBuffer();
		SourceBean req = requestContainer.getServiceRequest();
		int nro_giorno = 0; // LUN=1, ..., SAB=6, DOM=7
		int gg, mm, aa;
		Calendar giorno_attivo = Calendar.getInstance();
		String giornoDB = StringUtils.getAttributeStrNotNull(req, "giornoDB");
		String meseDB = StringUtils.getAttributeStrNotNull(req, "meseDB");
		String annoDB = StringUtils.getAttributeStrNotNull(req, "annoDB");
		String giorno = StringUtils.getAttributeStrNotNull(req, "giorno");
		String mese = StringUtils.getAttributeStrNotNull(req, "mese");
		String anno = StringUtils.getAttributeStrNotNull(req, "anno");
		String mod = StringUtils.getAttributeStrNotNull(req, "MOD");

		if (mod.equals("")) {
			mod = "0";
		}
		if (!giornoDB.equals("") && !meseDB.equals("") && !annoDB.equals("")) {
			giorno_attivo.set(Integer.parseInt(annoDB), Integer.parseInt(meseDB) - 1, Integer.parseInt(giornoDB));
		}
		if (giorno.equals("") && mese.equals("") && anno.equals("")) {
			giorno = Integer.toString(giorno_attivo.get(Calendar.DATE));
			mese = Integer.toString(giorno_attivo.get(Calendar.MONTH));
			anno = Integer.toString(giorno_attivo.get(Calendar.YEAR));
		}
		if (giornoDB.equals("") && meseDB.equals("") && annoDB.equals("")) {
			giornoDB = Integer.toString(giorno_attivo.get(Calendar.DATE));
			meseDB = Integer.toString(giorno_attivo.get(Calendar.MONTH) + 1);
			annoDB = Integer.toString(giorno_attivo.get(Calendar.YEAR));
		}
		nro_giorno = giorno_attivo.get(Calendar.DAY_OF_WEEK) - 1;
		if (nro_giorno == 0) {
			nro_giorno = 7;
		}
		gg = giorno_attivo.get(Calendar.DATE);
		mm = giorno_attivo.get(Calendar.MONTH) + 1;
		aa = giorno_attivo.get(Calendar.YEAR);
		// gc --> tipo istanza calendar
		String gc = gg + "/" + (mm - 1) + "/" + aa;
		String linkGC = "";
		linkGC += "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB;
		linkGC += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
		linkGC += "&MOD=" + mod;

		// Link giorni attigui
		Calendar giornoPrec = Calendar.getInstance();
		Calendar giornoSucc = Calendar.getInstance();
		// Giorno Precedente
		giornoPrec.set(aa, mm - 1, gg);
		int gp, mp, ap;
		giornoPrec.add(Calendar.DATE, -1);
		gp = giornoPrec.get(Calendar.DATE);
		mp = giornoPrec.get(Calendar.MONTH);
		ap = giornoPrec.get(Calendar.YEAR);
		String linkGPrec = "<a href=\"AdapterHTTP?PAGE=FestiviPage";
		linkGPrec += "&giornoDB=" + gp + "&meseDB=" + mapping_mesiDB[mp] + "&annoDB=" + ap;
		linkGPrec += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
		linkGPrec += "&MOD=" + mod + "\">";
		// Giorno Successivo
		giornoSucc.set(aa, mm - 1, gg);
		int gs, ms, as;
		giornoSucc.add(Calendar.DATE, 1);
		gs = giornoSucc.get(Calendar.DATE);
		ms = giornoSucc.get(Calendar.MONTH);
		as = giornoSucc.get(Calendar.YEAR);
		String linkGSucc = "<a href=\"AdapterHTTP?PAGE=FestiviPage";
		linkGSucc += "&giornoDB=" + gs + "&meseDB=" + mapping_mesiDB[ms] + "&annoDB=" + as;
		linkGSucc += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
		linkGSucc += "&MOD=" + mod + "\">";

		// Stondature TOP - Modifica del 10/03/2004 - Stefania Orioli
		buf.append("<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"19\" class=\"cal_header\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		// end top
		// INTESTAZIONE GIORNO
		// buf.append("<TABLE class=\"lista\" align=\"center\">");
		// buf.append("<tr><td class=\"cal_header\" colspan=\"2\">");
		buf.append(linkGPrec);
		buf.append("<img src=\"../../img/previous.gif\" alt=\"&lt;&lt;\">");
		buf.append("</a> ");
		buf.append(gg + " " + mesi[mm - 1] + " " + aa);
		buf.append(" ");
		buf.append(linkGSucc);
		buf.append("<img src=\"../../img/next.gif\" alt=\"&gt;&gt;\">");
		buf.append("</a>");
		// buf.append("</td></tr>");
		// buf.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
		// Stondature TOP
		buf.append("</td>");
		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"6\" height=\"19\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr class=\"cal\">");
		buf.append("<td class=\"cal\" width=\"6\"></td>");
		buf.append("<td class=\"cal\" width=\"100%\" align=\"center\">");
		buf.append("<TABLE class=\"appuntamenti\" margin=\"0\" cellspacing=\"0\">");
		// END TOP

		buf.append("<form name=\"form\" action=\"AdapterHTTP\" method=\"POST\" "
				+ "onSubmit=\"return controllaNullaTL();\">"); // GG 8-10-2004
		String submitValue = "";

		SourceBean content = (SourceBean) serviceResponse.getAttribute("MFESTIVO");
		Vector rows = content.getAttributeAsVector("ROWS.ROW");
		SourceBean row = null;
		giorniNL = new ArrayList(CalUtils.setGiorniNL(aa, serviceResponse, "MGIORNI_NL"));
		gsett = new ArrayList(CalUtils.setGsett(aa, serviceResponse, "MGIORNI_NL"));
		String tipo = "";

		String numaaaa = "";
		String codCpi = "";
		String prgGiornoNl = "";

		boolean check = CalUtils.checkGsett(nro_giorno, giorno_attivo, serviceResponse, "MGIORNI_NL");
		// Primo caso rows non ha righe -> Giorno Feriale o di Riposo
		// Settimanale
		if (rows.size() == 0) {
			if (gsett.contains(Integer.toString(nro_giorno)) && check) {
				buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"NuovoGNLPage\">");
				buf.append("<input type=\"hidden\" name=\"tipo\" value=\"O\">");
				submitValue = "Trasforma in Festivo";
			} else {
				// Giorno Feriale
				tipo = "F";
				buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"NuovoGNLPage\">");
				buf.append("<input type=\"hidden\" name=\"tipo\" value=\"O\">");
				buf.append("<tr><td colspan=\"2\" align=\"center\"><b>Giorno Lavorativo</b></td></tr>");
				submitValue = "Trasforma in Festivo";
			}
		} else {
			// Controllo se è un giorno festivo
			// String numaaaa, codCpi, prgGiornoNl;
			if (giorniNL.contains(gc)) // GIORNO FESTIVO
			{
				row = (SourceBean) rows.elementAt(0);
				numaaaa = StringUtils.getAttributeStrNotNull(row, "NUMAAAA");
				codCpi = StringUtils.getAttributeStrNotNull(row, "CODCPI");
				if (codCpi.equals("")) {
					SessionContainer sessionContainer = requestContainer.getSessionContainer();
					User user = (User) sessionContainer.getAttribute(User.USERID);
					int cdnUt = user.getCodut();
					int cdnTipoGruppo = user.getCdnTipoGruppo();
					if (cdnTipoGruppo == 1) {
						codCpi = user.getCodRif();
					} else {
						codCpi = requestContainer.getAttribute("agenda_codCpi").toString();
					}
				}
				submitValue = "Modifica";
				buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"ModGNLPage\">");
				BigDecimal prgGG = (BigDecimal) row.getAttribute("PRGGIORNONL");
				if (prgGG != null) {
					prgGiornoNl = prgGG.toString();
				} else {
					prgGiornoNl = "";
				}
				buf.append("<input type=\"hidden\" name=\"PRGGIORNONL\" value=\"" + prgGiornoNl + "\">");
				buf.append("<input type=\"hidden\" name=\"CODCPI\" value=\"" + codCpi + "\">");
				buf.append("<tr><td colspan=\"2\" align=\"center\"><b>Giorno Festivo</b></td></tr>");
				buf.append("<tr><td colspan=\"2\"><br></td></tr>");
				buf.append("<tr><td colspan=\"2\" align=\"left\"><div class=\"sezione2\">Ripetizioni</div></td></tr>");
				if (!numaaaa.equals("")) {
					// Giorno Festivo Puntuale
					tipo = "O";
					buf.append("<input type=\"hidden\" name=\"tipo\" value=\"O\">");
					buf.append("<tr>");
					buf.append("<td class=\"etichetta\">Solo Oggi</td>");
					buf.append("<td class=\"campo\"><img src=\"../../img/check.gif\" alt=\"S&igrave;\"></td>");
					buf.append("</tr>");
					buf.append("<tr>");
					buf.append("<td class=\"etichetta\">Ogni Anno</td>");
					buf.append("<td class=\"campo\"><img src=\"../../img/no_check.gif\" alt=\"No\"></td>");
					buf.append("</tr>");
				} else {
					// Giorno Festivo con ripetizione annuale
					tipo = "A";
					buf.append("<input type=\"hidden\" name=\"tipo\" value=\"A\">");
					buf.append("<tr>");
					buf.append("<td class=\"etichetta\">Solo Oggi</td>");
					buf.append("<td class=\"campo\"><img src=\"../../img/no_check.gif\" alt=\"No\"></td>");
					buf.append("</tr>");
					buf.append("<tr>");
					buf.append("<td class=\"etichetta\">Ogni Anno</td>");
					buf.append("<td class=\"campo\"><img src=\"../../img/check.gif\" alt=\"S&igrave;\"></td>");
					buf.append("</tr>");
					buf.append(
							"<tr><td colspan=\"2\" align=\"left\"><div class=\"sezione2\">Periodo di Validit&agrave;</div></td></tr>");
					buf.append("<tr>");
					buf.append("<td class=\"etichetta\">Data Inizio</td>");
					buf.append("<td class=\"campo\">" + row.getAttribute("DATINIZIOVAL").toString());
					buf.append("</tr>");
					buf.append("<tr>");
					buf.append("<td class=\"etichetta\">Data Fine</td>");
					buf.append("<td class=\"campo\">" + row.getAttribute("DATFINEVAL").toString());
					buf.append("</tr>");
				} // if(!numaaaa.equals(""))
			} // else if(giorniNL.contains(gc))
		} // else if(rows.size() == 0)

		buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>");
		buf.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>");
		buf.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>");
		buf.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>");
		buf.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>");
		buf.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>");
		buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");

		buf.append("<tr><td colspan=\"2\" align=\"center\">&nbsp;</td></tr>");
		if (!submitValue.equals("")) {
			buf.append("<tr><td colspan=\"2\" align=\"center\">");
			buf.append("<input type=\"submit\" class=\"pulsanti\" value=\"" + submitValue + "\">");
			buf.append("</td></tr>");
		}
		buf.append("</form>");
		if (tipo.equals("O") || tipo.equals("A")) {
			buf.append("<form name=\"form\" action=\"AdapterHTTP\" method=\"POST\" "
					+ "onSubmit=\"return controllaNullaTL();\">"); // GG
																	// 8-10-2004
			if (tipo.equals("O")) {
				buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"FestiviPage\">");
				buf.append("<input type=\"hidden\" name=\"DEL\" value=\"true\">");
			} else {
				buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"DelGNLPage\">");
			}
			buf.append("<input type=\"hidden\" name=\"PRGGIORNONL\" value=\"" + prgGiornoNl + "\">");
			buf.append("<input type=\"hidden\" name=\"CODCPI\" value=\"" + codCpi + "\">");
			buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>");
			buf.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>");
			buf.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>");
			buf.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>");
			buf.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>");
			buf.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>");
			buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");
			buf.append("<tr><td colspan=\"2\" align=\"center\">&nbsp;</td></tr>");
			buf.append("<tr><td colspan=\"2\" align=\"center\">");
			buf.append("<input type=\"submit\" class=\"pulsanti\" value=\"Cancella Regola\">");
			buf.append("</td></tr>");
			buf.append("</form>");
		}

		// if(gsett.contains(Integer.toString(nro_giorno)) &&
		// CalUtils.checkGsett(nro_giorno,giorno_attivo,serviceResponse,"MGIORNI_NL"))
		// {
		if (gsett.contains(Integer.toString(nro_giorno)) && check) {
			// Giorno di Riposo Settimanale
			tipo = "R";
			buf.append("<form name=\"formGsettMod\" action=\"AdapterHTTP\" method=\"POST\" "
					+ "onSubmit=\"return controllaNullaTL();\">"); // GG
																	// 8-10-2004
			buf.append("<input type=\"hidden\" name=\"tipo\" value=\"R\">");
			buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>");
			buf.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>");
			buf.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>");
			buf.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>");
			buf.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>");
			buf.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>");
			buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");
			buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"ModGSettPage\">");
			buf.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			buf.append("<tr><td colspan=\"2\" align=\"center\"><b>Riposo Settimanale</b></td></tr>");
			buf.append("<tr><td colspan=\"2\"><br></td></tr>");
			buf.append("<tr><td colspan=\"2\" align=\"left\"><div class=\"sezione2\">Ripetizioni</div></td></tr>");
			if (nro_giorno == 7) {
				buf.append("<tr><td class=\"etichetta\">Tutte le</td>");
			} else {
				buf.append("<tr><td class=\"etichetta\">Tutti i</td>");
			}
			buf.append("<td class=\"campo\">" + giorniEst[nro_giorno] + "</td></tr>");
			content = (SourceBean) serviceResponse.getAttribute("MGIORNI_NL");
			Vector rowsNL = content.getAttributeAsVector("ROWS.ROW");
			int i = 0;
			boolean trovato = false;
			String datInizio = "";
			String datFine = "";
			BigDecimal progressivo = null;
			String cod = "";
			int gi, mi, ai, gf, mf, af;
			ai = mi = gi = 0;
			af = mf = gf = 0;
			Calendar dt = Calendar.getInstance();
			dt.set(Integer.parseInt(annoDB), Integer.parseInt(meseDB) - 1, Integer.parseInt(giornoDB));
			Calendar dI = Calendar.getInstance();
			Calendar dF = Calendar.getInstance();
			while (i < rowsNL.size() && !trovato) {
				row = (SourceBean) rowsNL.elementAt(i);
				if (row.containsAttribute("NUMGSETT")) {
					if (Integer.parseInt(row.getAttribute("NUMGSETT").toString()) == nro_giorno) {
						gi = Integer.parseInt(row.getAttribute("GI").toString());
						mi = Integer.parseInt(row.getAttribute("MI").toString()) - 1;
						ai = Integer.parseInt(row.getAttribute("AI").toString());
						gf = Integer.parseInt(row.getAttribute("GF").toString());
						mf = Integer.parseInt(row.getAttribute("MF").toString()) - 1;
						af = Integer.parseInt(row.getAttribute("AF").toString());
						dI.set(ai, mi, gi);
						dF.set(af, mf, gf);
						if ((dt.after(dI) || dt.equals(dI)) && (dt.before(dF) || dt.equals(dF))) {
							trovato = true;
							datInizio = row.getAttribute("DATINIZIO").toString();
							datFine = row.getAttribute("DATFINE").toString();
							progressivo = (BigDecimal) row.getAttribute("PRGGIORNONL");
							cod = (String) row.getAttribute("CODCPI");
						}
					}
				}
				i++;
			}
			buf.append("<input type=\"hidden\" name=\"PRGGIORNONL\" value=\"" + progressivo + "\">");
			buf.append("<input type=\"hidden\" name=\"CODCPI\" value=\"" + cod + "\">");
			buf.append(
					"<tr><td colspan=\"2\" align=\"left\"><div class=\"sezione2\">Periodo di Validit&agrave;</div></td></tr>");
			buf.append("<tr>");
			buf.append("<td class=\"etichetta\">Data Inizio</td>");
			buf.append("<td class=\"campo\">" + datInizio);
			buf.append("</tr>");
			buf.append("<tr>");
			buf.append("<td class=\"etichetta\">Data Fine</td>");
			buf.append("<td class=\"campo\">" + datFine);
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			submitValue = "Modifica Riposo Settimanale";
			buf.append("<tr><td colspan=\"2\" align=\"center\">");
			buf.append("<input type=\"submit\" class=\"pulsanti\" value=\"" + submitValue + "\">");
			buf.append("</td></tr>");
			buf.append("</form>");

			buf.append("<form name=\"formGsettDel\" action=\"AdapterHTTP\" method=\"POST\" "
					+ "onSubmit=\"return controllaNullaTL();\">"); // GG
																	// 8-10-2004
			buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"DelGSettPage\">");
			buf.append("<input type=\"hidden\" name=\"PRGGIORNONL\" value=\"" + progressivo + "\">");
			buf.append("<input type=\"hidden\" name=\"CODCPI\" value=\"" + cod + "\">");
			buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>");
			buf.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>");
			buf.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>");
			buf.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>");
			buf.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>");
			buf.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>");
			buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");
			buf.append("<tr><td colspan=\"2\" align=\"center\">&nbsp;</td></tr>");
			buf.append("<tr><td colspan=\"2\" align=\"center\">");
			buf.append("<input type=\"submit\" class=\"pulsanti\" value=\"Cancella Regola\">");
			buf.append("</td></tr>");
			buf.append("</form>");

		} else {
			buf.append("<form name=\"formGsettNew\" action=\"AdapterHTTP\" method=\"POST\" "
					+ "onSubmit=\"return controllaNullaTL();\">"); // GG
																	// 8-10-2004
			buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"NuovoGSettPage\">");
			buf.append("<input type=\"hidden\" name=\"CODCPI\" value=\"" + codCpi + "\">");
			buf.append("<input type=\"hidden\" name=\"tipo\" value=\"R\">");
			buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>");
			buf.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>");
			buf.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>");
			buf.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>");
			buf.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>");
			buf.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>");
			buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");
			buf.append("<tr><td colspan=\"2\" align=\"center\">&nbsp;</td></tr>");
			buf.append("<tr><td colspan=\"2\" align=\"center\">");
			buf.append("<input type=\"submit\" class=\"pulsanti\" value=\"Trasforma in Riposo Settimanale\">");
			buf.append("</td></tr>");
			buf.append("</form>");
		}

		buf.append("<tr><td colspan=\"2\" align=\"center\">&nbsp;</td></tr>");
		buf.append("</table>");
		// Stondature BOTTOM - Modifiche 10/03/2004 - Stefania Orioli
		buf.append("</td><td class=\"cal\" width=\"6\"></td></tr>");
		buf.append(
				"<tr class=\"cal\"><td class=\"cal_header\" align=\"left\" valign=\"bottom\" width=\"13\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"cal_header\" align=\"right\" valign=\"bottom\" width=\"19\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		// end BOTTOM

		return (buf.toString());
	} // public static String infoGiorno(RequestContainer requestContainer,
		// SourceBean serviceResponse)

	public static String infoSettimana(RequestContainer requestContainer, SourceBean serviceResponse) {
		StringBuffer buf = new StringBuffer();
		SourceBean req = requestContainer.getServiceRequest();

		int nrosDB = Integer.parseInt(req.getAttribute("nrosDB").toString());
		int annoDB = Integer.parseInt(req.getAttribute("annoDB").toString());
		String mod = "1";
		String cod_vista = "";
		int giornoDB, meseDB, mese, anno;
		mese = Integer.parseInt(req.getAttribute("mese").toString());
		anno = Integer.parseInt(req.getAttribute("anno").toString());

		if (req.containsAttribute("MOD")) {
			mod = req.getAttribute("MOD").toString();
		}
		if (req.containsAttribute("cod_vista")) {
			cod_vista = req.getAttribute("cod_vista").toString();
		}

		content = (SourceBean) serviceResponse.getAttribute("MGIORNI_NL");
		// setGiorniNL(annoDB);
		giorniNL = new ArrayList(CalUtils.setGiorniNL(anno, serviceResponse, "MGIORNI_NL"));
		gsett = new ArrayList(CalUtils.setGsett(anno, serviceResponse, "MGIORNI_NL"));

		int i, j;
		SourceBean row;
		String data;
		boolean trovato = false;

		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.clear(Calendar.MONTH);
		cal.clear(Calendar.DAY_OF_YEAR);
		cal.set(Calendar.YEAR, annoDB);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.WEEK_OF_YEAR, nrosDB);
		int gi, mi, ai;
		String mesei = "";
		gi = cal.get(Calendar.DATE);
		mi = cal.get(Calendar.MONTH);
		mesei = mesi[mi];
		ai = cal.get(Calendar.YEAR);
		cal.add(Calendar.DATE, 6);
		int gf, mf, af;
		String mesef = "";
		gf = cal.get(Calendar.DATE);
		mf = cal.get(Calendar.MONTH);
		mesef = mesi[mf];
		af = cal.get(Calendar.YEAR);

		cal.set(ai, mi, gi);
		// Predisposizione LINK DI SCORRIMENTO
		Calendar settPrec = Calendar.getInstance();
		Calendar settSucc = Calendar.getInstance();
		int nrop, annop, nros, annos, m;
		settPrec.set(ai, mi, gi);
		settPrec.add(Calendar.WEEK_OF_YEAR, -1);
		nrop = settPrec.get(Calendar.WEEK_OF_YEAR);
		annop = settPrec.get(Calendar.YEAR);
		if ((nrop == 1) && (annop < ai)) {
			annop += 1;
		}
		String linkSettPrec = "<a href=\"AdapterHTTP?PAGE=FestiviPage";
		linkSettPrec += "&nrosDB=" + nrop + "&annoDB=" + annop;
		linkSettPrec += "&mese=" + mese + "&anno=" + anno;
		linkSettPrec += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";
		settSucc.set(ai, mi, gi);
		settSucc.add(Calendar.WEEK_OF_YEAR, 1);
		nros = settSucc.get(Calendar.WEEK_OF_YEAR);
		annos = settSucc.get(Calendar.YEAR);
		m = settSucc.get(Calendar.MONTH);
		if ((m == 11) && (nros == 1)) {
			annos += 1;
		}
		String linkSettSucc = "<a href=\"AdapterHTTP?PAGE=FestiviPage";
		linkSettSucc += "&nrosDB=" + nros + "&annoDB=" + annos;
		linkSettSucc += "&mese=" + mese + "&anno=" + anno;
		linkSettSucc += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";

		// Stondature TOP - Modifica del 10/03/2004 - Stefania Orioli
		buf.append("<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"19\" class=\"cal_header\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		// end top
		// buf.append("<table class=\"lista\" align=\"center\">");
		// INTESTAZIONE
		// buf.append("<tr valign=\"middle\"><td colspan=\"7\"
		// class=\"cal_header\">");
		buf.append(linkSettPrec);
		buf.append("<img src=\"../../img/previous.gif\" alt=\"&lt;&lt;\"></a> ");
		buf.append(gi + " " + mesei + " " + ai + " - ");
		buf.append(gf + " " + mesef + " " + af + " ");
		buf.append(linkSettSucc);
		buf.append("<img src=\"../../img/next.gif\" alt=\"&gt;&gt;\"></a> ");
		// buf.append("</td></tr>");
		// buf.append("<tr><td class=\"cal_settimana\"
		// width=\"16%\">&nbsp;</td>");
		// Stondature TOP
		buf.append("</td>");
		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"6\" height=\"19\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr class=\"cal\">");
		buf.append("<td class=\"cal\" width=\"6\"></td>");
		buf.append("<td class=\"cal\" width=\"100%\" align=\"center\">");
		buf.append("<TABLE class=\"appuntamenti\" margin=\"0\" cellspacing=\"0\">");
		// END TOP
		// GIORNI DELLA SETTIMANA
		buf.append("<tr>");
		String gc = "";
		StringBuffer stringagiorno;
		settimana = new ArrayList();
		for (i = 1; i <= 7; i++) {
			giornoDB = cal.get(Calendar.DATE);
			meseDB = mapping_mesiDB[cal.get(Calendar.MONTH)];
			annoDB = cal.get(Calendar.YEAR);
			gc = giornoDB + "/" + cal.get(Calendar.MONTH) + "/" + annoDB;

			stringagiorno = new StringBuffer();
			stringagiorno.append(Integer.toString(giornoDB));
			stringagiorno.append("/");
			stringagiorno.append(Integer.toString(meseDB));
			stringagiorno.append("/" + Integer.toString(annoDB));
			settimana.add(stringagiorno.toString());

			if ((gsett.contains(Integer.toString(i)) && CalUtils.checkGsett(i, cal, serviceResponse, "MGIORNI_NL"))
					|| giorniNL.contains(gc)) {
				buf.append("<td class=\"cal_vuoto\" width=\"14%\">");
			} else {
				buf.append("<td class=\"cal_settimana\" width=\"14%\">");
			}
			buf.append("<a class=\"calh\" href=\"AdapterHTTP?PAGE=FestiviPage");
			buf.append("&MOD=0");
			buf.append("&cod_vista=" + cod_vista);
			buf.append("&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB);
			buf.append("&mese=" + mese + "&anno=" + anno);
			buf.append("\">");
			buf.append(giorni[i] + "<br><b>" + giornoDB + "</b></a></td>");
			cal.add(Calendar.DATE, 1);
		}
		buf.append("</tr>");

		// Info giorni
		cal.set(ai, mi, gi);
		buf.append("<tr class=\"note\">");
		for (i = 1; i <= 7; i++) {
			giornoDB = cal.get(Calendar.DATE);
			meseDB = mapping_mesiDB[cal.get(Calendar.MONTH)];
			annoDB = cal.get(Calendar.YEAR);
			gc = giornoDB + "/" + cal.get(Calendar.MONTH) + "/" + annoDB;

			buf.append("<td class=\"cal_bordato\" width=\"14%\">");
			if ((gsett.contains(Integer.toString(i)) && CalUtils.checkGsett(i, cal, serviceResponse, "MGIORNI_NL"))
					|| giorniNL.contains(gc)) {
				if (giorniNL.contains(gc)) {
					buf.append("Festivo<br>");
				}
				if (gsett.contains(Integer.toString(i)) && CalUtils.checkGsett(i, cal, serviceResponse, "MGIORNI_NL")) {
					buf.append("Riposo<br>");
				}
			} else {
				// buf.append("Lavorativo<br>");
				buf.append("&nbsp;");
			}
			buf.append("</td>");
			cal.add(Calendar.DATE, 1);
		}
		buf.append("</tr>");

		buf.append("</table>");
		// Stondature BOTTOM - Modifiche 10/03/2004 - Stefania Orioli
		buf.append("</td><td class=\"cal\" width=\"6\"></td></tr>");
		buf.append(
				"<tr class=\"cal\"><td class=\"cal_header\" align=\"left\" valign=\"bottom\" width=\"13\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"cal_header\" align=\"right\" valign=\"bottom\" width=\"19\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		// end BOTTOM
		return (buf.toString());
	}

	public static String listaContattiGiorno(RequestContainer requestContainer, SourceBean serviceResponse,
			ResponseContainer responseContainer, String codCpi) {
		StringBuffer buf = new StringBuffer();
		SourceBean req = requestContainer.getServiceRequest();
		content = (SourceBean) serviceResponse.getAttribute("MCONTATTI");
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
				"MCONTATTI");

		// Profilatura - Stefy 15/07/2004
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "PCalendario");
		boolean canInsCont = attributi.containsButton("NEW_CONT");
		boolean canDelCont = attributi.containsButton("DEL_CONT");
		// --

		String giornoDB = "";
		String meseDB = "";
		String annoDB = "";
		// Istanzio giornoDB, meseDb, annoDb per l'inserimento di un nuovo
		// appuntamento
		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		giornoDB = Integer.toString(oggi.get(Calendar.DATE));
		meseDB = Integer.toString(oggi.get(Calendar.MONTH) + 1);
		annoDB = Integer.toString(oggi.get(Calendar.YEAR));

		String giorno = StringUtils.getAttributeStrNotNull(req, "giorno");
		String mese = StringUtils.getAttributeStrNotNull(req, "mese");
		String anno = StringUtils.getAttributeStrNotNull(req, "anno");
		String cod_vista = StringUtils.getAttributeStrNotNull(req, "cod_vista");
		String mod = StringUtils.getAttributeStrNotNull(req, "MOD");

		if (anno.equals("")) {
			anno = Integer.toString(oggi.get(Calendar.YEAR));
		}

		if (req.containsAttribute("giornoDB")) {
			giornoDB = req.getAttribute("giornoDB").toString();
		}
		if (req.containsAttribute("meseDB")) {
			meseDB = req.getAttribute("meseDB").toString();
		}
		if (req.containsAttribute("annoDB")) {
			annoDB = req.getAttribute("annoDB").toString();
		}

		// String linkDett = "AdapterHTTP?PAGE=ModContattoPage" + ...
		String linkDett = "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB + "&giorno=" + giorno
				+ "&mese=" + mese + "&anno=" + anno + "&cod_vista=" + cod_vista + "&MOD=" + mod;
		linkDett += "&data_cod=" + giornoDB + "/" + meseDB + "/" + annoDB;

		Calendar giornoAtt = Calendar.getInstance();
		giornoAtt.set(Integer.parseInt(annoDB), Integer.parseInt(meseDB) - 1, Integer.parseInt(giornoDB));
		int gds = giornoAtt.get(Calendar.DAY_OF_WEEK) - 1;
		if (gds == 0) {
			gds = 7;
		}
		String gc = giornoDB + "/" + Integer.toString(Integer.parseInt(meseDB) - 1) + "/" + annoDB;
		giorniNL = new ArrayList(CalUtils.setGiorniNL(Integer.parseInt(anno), serviceResponse, "MGIORNI_NL"));
		gsett = new ArrayList(CalUtils.setGsett(Integer.parseInt(anno), serviceResponse, "MGIORNI_NL"));
		boolean giornoF = false;
		if ((gsett.contains((Integer.toString(gds)))
				&& CalUtils.checkGsett(gds, giornoAtt, serviceResponse, "MGIORNI_NL")) || giorniNL.contains(gc)) {
			giornoF = true;
		} else {
			giornoF = false;
		}

		if (!giornoF) {
			buf.append("<table width=\"100%\" margin=\"0\" cellspacing=\"0\" class=\"appuntamenti\">");
			buf.append("<form name=\"formNuovoCont\" action=\"AdapterHTTP\" method=\"POST\" "
					+ "onSubmit=\"return controllaNullaTL();\">"); // GG
																	// 8-10-2004
			buf.append("<input name=\"PAGE\" type=\"hidden\" value=\"NuovoContattoPage\"/>");
			buf.append("<input name=\"CODCPI\" type=\"hidden\" value=\"" + codCpi + "\"/>");
			buf.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>");
			buf.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>");
			buf.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>");
			buf.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>");
			buf.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>");
			buf.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>");
			buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");
			buf.append("<input name=\"cod_vista\" type=\"hidden\" value=\"" + cod_vista + "\"/>");
			buf.append("<input name=\"data_cod\" type=\"hidden\" value=\"" + giornoDB + "/" + meseDB + "/" + annoDB
					+ "\"/>");
			buf.append("<tr valign=\"middle\"><td class=\"cal_titolo2\" align=\"center\" width=\"100%\">");
			buf.append(
					"<img src=\"../../img/list_next.gif\" id=\"img_cont\" onClick=\"onoff_contatti()\">&nbsp;&nbsp;");
			// Profilatura - Stefy 15/07/2004
			if (canInsCont) {
				buf.append("Contatti <input type=\"submit\" value=\"Inserisci\" class=\"ag_pulsanti\">");
			} else {
				buf.append("Contatti");
			}
			// --
			buf.append("</td></tr></form>\n");
			// buf.append("<tr id=\"lista_contatti\" name=\"lista_contatti\"
			// style=\"display:none\"><td><table class=\"main\">");
			buf.append(
					"<tr id=\"lista_contatti\" name=\"lista_contatti\" style=\"display:none\"><td width=\"100%\"><table width=\"100%\" margin=\"0\">");
			buf.append(CalUtils.generaColonne(moduleBean));
			if (content != null) {
				buf.append(CalUtils.generaRighe(moduleBean, serviceResponse, "MCONTATTI", requestContainer,
						responseContainer, linkDett, canDelCont));
				buf.append(CalUtils.makeJScript("MCONTATTI"));
			}
			buf.append("</table></td></tr>");
			buf.append("<script type=\"text/javascript\">\n");
			buf.append("var onContatti = new Image();\n");
			buf.append("var offContatti = new Image();\n");
			buf.append("onContatti.src=\"../../img/list_next.gif\";\n");
			buf.append("offContatti.src=\"../../img/btn_su.gif\";\n");
			buf.append("function onoff_contatti() {\n");
			buf.append("var oContatti = document.getElementById(\"lista_contatti\");\n");
			buf.append("var oImgContatti = document.getElementById(\"img_cont\");\n");
			buf.append("if(oContatti.style.display==\"\") {\n");
			buf.append("oContatti.style.display=\"none\";\n");
			buf.append("oImgContatti.src = onContatti.src;\n");
			buf.append("} else {\n");
			buf.append("oContatti.style.display=\"\";");
			buf.append("oImgContatti.src = offContatti.src;\n");
			buf.append("}\n");
			buf.append("}\n");
			buf.append("</script>");
			// buf.append("<tr><td>&nbsp;</td></tr>");
			/*
			 * buf.append("<form name=\"formNuovoCont\" action=\"AdapterHTTP\" method=\"POST\" " +
			 * "onSubmit=\"return controllaNullaTL();\">"); // GG 8-10-2004
			 * buf.append("<input name=\"PAGE\" type=\"hidden\" value=\"NuovoContattoPage\"/>"); buf.append("<input
			 * name=\"giornoDB\" type=\"hidden\" value=\""+giornoDB+"\"/>");
			 * buf.append("<input name=\"meseDB\" type=\"hidden\" value=\""+meseDB+"\"/>");
			 * buf.append("<input name=\"annoDB\" type=\"hidden\" value=\""+annoDB+"\"/>"); buf.append("<input
			 * name=\"giorno\" type=\"hidden\" value=\""+giorno+"\"/>");
			 * buf.append("<input name=\"mese\" type=\"hidden\" value=\""+mese+"\"/>"); buf.append("<input name=\"anno\"
			 * type=\"hidden\" value=\""+anno+"\"/>"); buf.append("<input name=\"MOD\" type=\"hidden\" value=\"0\"/>");
			 * buf.append("<input name=\"cod_vista\" type=\"hidden\" value=\""+cod_vista+"\"/>");
			 * buf.append("<input name=\"data_cod\" type=\"hidden\" value=\""+giornoDB + "/" + meseDB + "/" + annoDB
			 * +"\"/>"); buf.append("<tr><td align=\"center\" >"); buf.append("<input
			 * type=\"submit\" value=\"Inserisci Nuovo Contatto\" class=\"pulsanti\">"); buf.append("</td></tr>");
			 * buf.append("</form>");
			 */
			buf.append("<tr><td>&nbsp;</td></tr>");
			buf.append("</table>");
		}
		return buf.toString();
	} // public static String listaContattiGiorno(..., ...)

	public static String repSettimanaTipo(RequestContainer requestContainer, SourceBean serviceResponse) {
		StringBuffer buf = new StringBuffer();
		String moduleOp = "MVSETTOPERATORI";
		String moduleSer = "MVSETTSERVIZI";
		SourceBean contSer = (SourceBean) serviceResponse.getAttribute(moduleSer);
		SourceBean contOp = (SourceBean) serviceResponse.getAttribute(moduleOp);
		int i;

		SourceBean serviceRequest = requestContainer.getServiceRequest();
		String prgSettTipo = (String) serviceRequest.getAttribute("PRGSETTIPO");
		String codCpi = (String) serviceRequest.getAttribute("CODCPI");
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("function sub_frm(num) {\n");
		buf.append("document.frm.NUMGIORNOSETT.value=num;\n");
		buf.append("document.frm.submit();\n");
		buf.append("}");
		buf.append("</script>");
		buf.append("<form name=\"frm\" action=\"AdapterHTTP\" method=\"POST\" "
				+ "onSubmit=\"return controllaNullaTL();\">"); // GG 8-10-2004
		buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"VGiornoTipoPage\">");
		buf.append("<input type=\"hidden\" name=\"PRGSETTIPO\" value=\"" + prgSettTipo + "\">");
		buf.append("<input type=\"hidden\" name=\"CODCPI\" value=\"" + codCpi + "\">");
		buf.append("<input type=\"hidden\" name=\"NUMGIORNOSETT\" value=\"\">");
		buf.append("</form>");

		// Stondature TOP - Modifica del 10/03/2004 - Stefania Orioli
		buf.append(
				"<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade align=\"center\">");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"19\" class=\"cal_header\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append(
				"<td class=\"cal_header\" align=\"center\" valign=\"middle\" width=\"4%\" cellpadding=\"2px\">&nbsp;</td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		buf.append("<b>Per Servizio</b></td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		buf.append("<b>Per Operatore</b></td>");
		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"6\" height=\"19\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		// END TOP
		// buf.append("<table width=\"96%\">");
		// buf.append("<tr><td width=\"4%\"
		// class=\"cal_settimana\">&nbsp;</td>");
		// buf.append("<td width=\"48%\" class=\"cal_header\"><b>Per
		// Servizio</b></td>");
		// buf.append("<td width=\"48%\" class=\"cal_header\"><b>Per
		// Operatore</b></td>");

		for (i = 1; i < 7; i++) {
			buf.append("<tr class=\"cal\">");
			buf.append("<td class=\"cal\" width=\"6\"></td>");
			buf.append("<td class=\"cal_settimana\" width=\"4%\"><a class=\"calh\" href=\"#\" onClick=\"sub_frm(" + i
					+ ")\">" + giorni[i] + "</a></td>");
			buf.append("<td class=\"cal_bordato\">" + repXQuantita(i, contSer) + "</td>");
			buf.append("<td class=\"cal_bordato\">" + repXQuantita(i, contOp) + "</td>");
			buf.append("<td class=\"cal\" width=\"6\"></td>");
			buf.append("</tr>");
		}
		// buf.append("</table>");
		// Stondature BOTTOM - Modifiche 10/03/2004 - Stefania Orioli
		buf.append(
				"<tr class=\"cal\"><td class=\"cal_header\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"cal_header\" width=\"4%\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;</td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;</td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;</td>");
		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		// end BOTTOM
		return (buf.toString());
	} // public static String repSettimanaTipo(..., ...)

	private static String repXQuantita(int num, SourceBean content) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		String descr = "";
		BigDecimal nro = null;
		SourceBean row = null;

		// buf.append("&nbsp;<ul type=\"square\">");
		Vector rows = content.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMGIORNOSETT",
				Integer.toString(num));
		if (rows.size() == 0) {
			buf.append("&nbsp;");
		} else {
			buf.append("<ul type=\"square\">");
		}
		for (i = 0; i < rows.size(); i++) {
			row = (SourceBean) rows.elementAt(i);
			descr = (String) row.getAttribute("DESCR");
			nro = (BigDecimal) row.getAttribute("NRO");
			buf.append("<li>" + descr + " - numero slot: " + nro.toString() + "</li>");
		}
		if (rows.size() > 0) {
			buf.append("</ul>");
		}

		return (buf.toString());
	}

	public static String repGiornoTipo(RequestContainer requestContainer, SourceBean serviceResponse) {
		StringBuffer buf = new StringBuffer();
		String moduleOp = "MVGIORNOOPERATORI";
		String moduleSer = "MVGIORNOSERVIZI";
		SourceBean contSer = (SourceBean) serviceResponse.getAttribute(moduleSer);
		SourceBean contOp = (SourceBean) serviceResponse.getAttribute(moduleOp);
		int i;

		SourceBean serviceRequest = requestContainer.getServiceRequest();
		String prgSettTipo = (String) serviceRequest.getAttribute("PRGSETTIPO");
		String codCpi = (String) serviceRequest.getAttribute("CODCPI");
		String numGiornoSett = (String) serviceRequest.getAttribute("NUMGIORNOSETT");

		buf.append("<script type=\"text/javascript\">\n");
		buf.append("function sub_frmDett(num,cod) {\n");
		buf.append("document.frm.PRGGIORNO.value=num;\n");
		buf.append("document.frm.CODSERVIZIO.value=cod;\n");
		buf.append("document.frm.PAGE.value=\"DettSlotTipoPage\";\n");
		buf.append("document.frm.submit();\n");
		buf.append("}\n");
		buf.append("function sub_frmIns(cod, n) {\n");
		buf.append("switch(n) {\n");
		buf.append("case 0:\n");
		buf.append("document.frm.CODSERVIZIO.value=\"\";\n");
		buf.append("document.frm.PRGSPI.value=\"\";\n");
		buf.append("break;\n");
		buf.append("case 1:\n");
		buf.append("document.frm.CODSERVIZIO.value=cod;\n");
		buf.append("document.frm.PRGSPI.value=\"\";\n");
		buf.append("break;\n");
		buf.append("case 2:\n");
		buf.append("document.frm.CODSERVIZIO.value=\"\";\n");
		buf.append("document.frm.PRGSPI.value=cod;\n");
		buf.append("break;\n");
		buf.append("} //switch\n");
		buf.append("document.frm.PAGE.value=\"NuovoSlotTipoPage\";\n");
		buf.append("document.frm.submit();\n");
		buf.append("}\n");
		buf.append("function sub_frmDel(num) {\n");
		buf.append("if(confirm(\"Confermi la cancellazione?\")) {\n");
		buf.append("document.frm.PRGGIORNO.value=num;\n");
		buf.append("document.frm.PAGE.value=\"VGiornoTipoPage\";\n");
		buf.append("document.frm.MODULE.value=\"MDelSlotTipo\";\n");
		buf.append("document.frm.submit();\n");
		buf.append("}\n");
		buf.append("}\n");
		buf.append("</script>\n");
		buf.append("<form name=\"frm\" action=\"AdapterHTTP\" method=\"POST\" "
				+ "onSubmit=\"return controllaNullaTL();\">"); // GG 8-10-2004
		buf.append("<input type=\"hidden\" name=\"PAGE\" value=\"\">");
		buf.append("<input type=\"hidden\" name=\"PRGSETTIPO\" value=\"" + prgSettTipo + "\">");
		buf.append("<input type=\"hidden\" name=\"CODCPI\" value=\"" + codCpi + "\">");
		buf.append("<input type=\"hidden\" name=\"NUMGIORNOSETT\" value=\"" + numGiornoSett + "\">");
		buf.append("<input type=\"hidden\" name=\"PRGGIORNO\" value=\"\">");
		buf.append("<input type=\"hidden\" name=\"CODSERVIZIO\" value=\"\">");
		buf.append("<input type=\"hidden\" name=\"PRGSPI\" value=\"\">");
		buf.append("<input type=\"hidden\" name=\"MODULE\" value=\"\">");
		buf.append("</form>");

		/*
		 * buf.append("<table width=\"96%\">"); buf.append("<tr>");
		 * buf.append("<td width=\"50%\" class=\"cal_header\"><br><b>PER SERVIZIO</b>"); buf.append("&nbsp;&nbsp;(<a
		 * href=\"#\" class=\"cals\" onClick=\"sub_frmIns('',0)\">"); buf.append("<img
		 * src=\"../../img/add2.gif\"> Aggiungi Servizio</a>)<br>&nbsp;"); buf.append("</td>");
		 * buf.append("<td width=\"50%\" class=\"cal_header\"><br><b>PER OPERATORE</b>"); buf.append("&nbsp;&nbsp;(<a
		 * href=\"#\" class=\"cals\" onClick=\"sub_frmIns('',0)\">"); buf.append("<img
		 * src=\"../../img/add2.gif\"> Aggiungi Operatore</a>)<br>&nbsp;"); buf.append("</td>");
		 */
		// Stondature TOP - Modifica del 10/03/2004 - Stefania Orioli
		buf.append(
				"<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade align=\"center\">");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"19\" class=\"cal_header\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\"><br>");
		buf.append("<b>Per Servizio</b>");
		buf.append("&nbsp;&nbsp;&nbsp;<span class=\"ListButtonChangePage\" onClick=\"sub_frmIns('',0)\">");
		buf.append("<img src=\"../../img/add2.gif\"> Aggiungi Servizio</span><br>&nbsp;");
		buf.append("</td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\"><br>");
		buf.append("<b>Per Operatore</b>");
		buf.append("&nbsp;&nbsp;&nbsp;<span class=\"ListButtonChangePage\" onClick=\"sub_frmIns('',0)\">");
		buf.append("<img src=\"../../img/add2.gif\"> Aggiungi Operatore</span><br>&nbsp;");
		buf.append("</td>");
		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"6\" height=\"19\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		// END TOP

		buf.append("<tr>");
		buf.append("<td class=\"cal\" width=\"6\"></td>");
		buf.append("<td class=\"cal_bordato\">");
		if (contSer != null) {
			buf.append(dettXServizio(contSer));
		} else {
			buf.append("&nbsp;");
		}
		buf.append("</td>");
		buf.append("<td class=\"cal_bordato\">");
		if (contOp != null) {
			buf.append(dettXOperatore(contOp));
		} else {
			buf.append("&nbsp;");
		}
		buf.append("</td>");
		buf.append("<td class=\"cal\" width=\"6\"></td>");
		buf.append("</tr>");

		// buf.append("</table>");
		// Stondature BOTTOM - Modifiche 10/03/2004 - Stefania Orioli
		buf.append(
				"<tr class=\"cal\"><td class=\"cal_header\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;</td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;</td>");
		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		// end BOTTOM

		return (buf.toString());
	} // public static String repGiornoTipo(..., ...)

	private static String dettXServizio(SourceBean contSer) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		String codSer = "...";
		String prgSpi = "...";
		String prg = "";
		int no = 0;
		int ns = 0;
		SourceBean row = null;

		buf.append("<ul type=\"square\">");
		Vector rows = contSer.getAttributeAsVector("ROWS.ROW");
		for (i = 0; i < rows.size(); i++) {
			row = (SourceBean) rows.elementAt(i);
			prg = (String) row.getAttribute("PRGSPI");
			if (!codSer.equals(StringUtils.getAttributeStrNotNull(row, "CODSERVIZIO"))) {
				no = 0;
				if (ns > 0) {
					buf.append("</li></ul></li><br>");
					// codSer = (String) row.getAttribute("CODSERVIZIO");
					prgSpi = "";
				}
				codSer = StringUtils.getAttributeStrNotNull(row, "CODSERVIZIO");
				buf.append("<li><b>" + StringUtils.getAttributeStrNotNull(row, "SERVIZIO"));
				buf.append("</b>&nbsp;&nbsp;(<span class=\"ListButtonChangePage\" onClick=\"sub_frmIns('" + codSer
						+ "',1)\">");
				buf.append("<img src=\"../../img/add3.gif\"> Aggiungi Operatore</span>)<br>");
				buf.append("<ul type=\"circle\">");
				ns++;
			}
			if (!prgSpi.equals(prg)) {
				if (no > 0) {
					buf.append("</li>");
					no++;
				}
				prgSpi = (String) row.getAttribute("PRGSPI");
				buf.append("<li class=\"cal_legenda\">" + row.getAttribute("OPERATORE").toString());
			}
			buf.append("<br><a href=\"#\" onClick=\"sub_frmDett(" + row.getAttribute("PRGGIORNO").toString() + ",'"
					+ codSer + "')\">");
			buf.append("<img src=\"../../img/detail.gif\" alt=\"Dettaglio\"></a>");
			// buf.append("&nbsp;Dalle&nbsp;" +
			// row.getAttribute("STRORADALLE").toString());
			buf.append("&nbsp;Dalle&nbsp;" + StringUtils.getAttributeStrNotNull(row, "STRORADALLE"));
			buf.append("&nbsp;-&nbsp;");
			// buf.append("Alle&nbsp;" +
			// row.getAttribute("STRORAALLE").toString());
			buf.append("Alle&nbsp;" + StringUtils.getAttributeStrNotNull(row, "STRORAALLE"));
			buf.append("&nbsp;<a href=\"#\" onClick=\"sub_frmDel(" + row.getAttribute("PRGGIORNO").toString() + ")\">");
			buf.append("<img src=\"../../img/del.gif\" alt=\"Cancella\"></a>");
		}
		if (ns > 0 && no > 0) {
			buf.append("</li></ul>");
		}
		buf.append("</li></ul>");

		return (buf.toString());
	} // private static String repXServizio(..., ...)

	private static String dettXOperatore(SourceBean contOp) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		String codSer = "...";
		String prgSpi = "...";
		String prg = "";
		int no = 0;
		int ns = 0;
		SourceBean row = null;

		buf.append("<ul type=\"square\">");
		Vector rows = contOp.getAttributeAsVector("ROWS.ROW");
		for (i = 0; i < rows.size(); i++) {
			row = (SourceBean) rows.elementAt(i);
			prg = (String) row.getAttribute("PRGSPI");
			if (!prgSpi.equals(prg)) {
				ns = 0;
				if (no > 0) {
					buf.append("</li></ul></li><br>");
					codSer = "...";
					// prgSpi = (String) row.getAttribute("PRGSPI");
				}
				prgSpi = (String) row.getAttribute("PRGSPI");
				buf.append("<li><b>" + row.getAttribute("OPERATORE").toString());
				buf.append("</b>&nbsp;&nbsp;(<span class=\"ListButtonChangePage\" onClick=\"sub_frmIns('" + prgSpi
						+ "',2)\">");
				buf.append("<img src=\"../../img/add3.gif\"> Aggiungi Servizio</span>)<br>");
				buf.append("<ul type=\"circle\">");
				no++;
			}
			if (!codSer.equals(StringUtils.getAttributeStrNotNull(row, "CODSERVIZIO"))) {
				if (ns > 0) {
					buf.append("</li>");
					ns++;
				}
				codSer = (String) row.getAttribute("CODSERVIZIO");
				buf.append("<li class=\"cal_legenda\">" + row.getAttribute("SERVIZIO").toString());
			}
			// buf.append("<br><img
			// src=\"../../img/detail.gif\">&nbsp;Dalle&nbsp;" +
			// row.getAttribute("STRORADALLE").toString());
			buf.append("<br><a href=\"#\" onClick=\"sub_frmDett(" + row.getAttribute("PRGGIORNO").toString() + ",'"
					+ codSer + "')\">");
			buf.append("<img src=\"../../img/detail.gif\" alt=\"Dettaglio\"></a>");
			buf.append("&nbsp;Dalle&nbsp;" + StringUtils.getAttributeStrNotNull(row, "STRORADALLE"));
			buf.append("&nbsp;-&nbsp;");
			// buf.append("Alle&nbsp;" +
			// row.getAttribute("STRORAALLE").toString());
			buf.append("Alle&nbsp;" + StringUtils.getAttributeStrNotNull(row, "STRORAALLE"));
			buf.append("&nbsp;<a href=\"#\" onClick=\"sub_frmDel(" + row.getAttribute("PRGGIORNO").toString() + ")\">");
			buf.append("<img src=\"../../img/del.gif\" alt=\"Cancella\"></a>");
		}
		if (ns > 0 && no > 0) {
			buf.append("</li></ul>");
		}
		buf.append("</li></ul>");

		return (buf.toString());
	} // private static String repXOperatore(..., ...)

	public static String listaSlotSettimana(RequestContainer requestContainer, SourceBean serviceResponse,
			String codCpi) {
		StringBuffer buf = new StringBuffer();
		SourceBean req = requestContainer.getServiceRequest();

		int nrosDB = Integer.parseInt(req.getAttribute("nrosDB").toString());
		int annoDB = Integer.parseInt(req.getAttribute("annoDB").toString());
		String mod = "1";
		String cod_vista = "";
		int giornoDB, meseDB, mese, anno;
		mese = Integer.parseInt(req.getAttribute("mese").toString());
		anno = Integer.parseInt(req.getAttribute("anno").toString());

		if (req.containsAttribute("MOD")) {
			mod = req.getAttribute("MOD").toString();
		}
		if (req.containsAttribute("cod_vista")) {
			cod_vista = req.getAttribute("cod_vista").toString();
		}

		content = (SourceBean) serviceResponse.getAttribute("MGIORNI_NL");
		giorniNL = new ArrayList(CalUtils.setGiorniNL(anno, serviceResponse, "MGIORNI_NL"));
		gsett = new ArrayList(CalUtils.setGsett(anno, serviceResponse, "MGIORNI_NL"));

		int i, j;
		SourceBean row;
		String data;
		boolean trovato = false;

		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.clear(Calendar.MONTH);
		cal.clear(Calendar.DAY_OF_YEAR);
		cal.set(Calendar.YEAR, annoDB);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.WEEK_OF_YEAR, nrosDB);
		int gi, mi, ai;
		String mesei = "";
		gi = cal.get(Calendar.DATE);
		mi = cal.get(Calendar.MONTH);
		mesei = mesi[mi];
		ai = cal.get(Calendar.YEAR);
		cal.add(Calendar.DATE, 6);
		int gf, mf, af;
		String mesef = "";
		gf = cal.get(Calendar.DATE);
		mf = cal.get(Calendar.MONTH);
		mesef = mesi[mf];
		af = cal.get(Calendar.YEAR);

		cal.set(ai, mi, gi);
		// Predisposizione LINK DI SCORRIMENTO
		Calendar settPrec = Calendar.getInstance();
		Calendar settSucc = Calendar.getInstance();
		int nrop, annop, nros, annos, m;
		settPrec.set(ai, mi, gi);
		settPrec.add(Calendar.WEEK_OF_YEAR, -1);
		nrop = settPrec.get(Calendar.WEEK_OF_YEAR);
		annop = settPrec.get(Calendar.YEAR);
		if ((nrop == 1) && (annop < ai)) {
			annop += 1;
		}
		String linkSettPrec = "<a href=\"AdapterHTTP?PAGE=GestSlotPage" + "&CODCPI=" + codCpi;
		linkSettPrec += "&nrosDB=" + nrop + "&annoDB=" + annop;
		linkSettPrec += "&mese=" + mese + "&anno=" + anno;
		linkSettPrec += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";
		settSucc.set(ai, mi, gi);
		settSucc.add(Calendar.WEEK_OF_YEAR, 1);
		nros = settSucc.get(Calendar.WEEK_OF_YEAR);
		annos = settSucc.get(Calendar.YEAR);
		m = settSucc.get(Calendar.MONTH);
		if ((m == 11) && (nros == 1)) {
			annos += 1;
		}
		String linkSettSucc = "<a href=\"AdapterHTTP?PAGE=GestSlotPage" + "&CODCPI=" + codCpi;
		linkSettSucc += "&nrosDB=" + nros + "&annoDB=" + annos;
		linkSettSucc += "&mese=" + mese + "&anno=" + anno;
		linkSettSucc += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";

		buf.append("<table class=\"lista\" align=\"center\">");
		// INTESTAZIONE
		buf.append("<tr valign=\"middle\"><td colspan=\"7\" class=\"cal_header\">");
		buf.append(linkSettPrec);
		buf.append("<img src=\"../../img/previous.gif\" alt=\"&lt;&lt;\"></a> ");
		buf.append(gi + " " + mesei + " " + ai + " - ");
		buf.append(gf + " " + mesef + " " + af + " ");
		buf.append(linkSettSucc);
		buf.append("<img src=\"../../img/next.gif\" alt=\"&gt;&gt;\"></a> ");
		buf.append("</td></tr>");
		buf.append("<tr><td colspan=\"7\" class=\"bordato\">Slot nella Settimana</td></tr>");
		buf.append("<tr><td class=\"cal_settimana\" width=\"16%\">&nbsp;</td>");

		// GIORNI DELLA SETTIMANA
		String gc = "";
		StringBuffer stringagiorno;
		settimana = new ArrayList();
		for (i = 1; i < 7; i++) {
			giornoDB = cal.get(Calendar.DATE);
			meseDB = mapping_mesiDB[cal.get(Calendar.MONTH)];
			annoDB = cal.get(Calendar.YEAR);
			gc = giornoDB + "/" + cal.get(Calendar.MONTH) + "/" + annoDB;

			stringagiorno = new StringBuffer();
			if (Integer.toString(giornoDB).length() == 1) {
				stringagiorno.append("0" + Integer.toString(giornoDB));
			} else {
				stringagiorno.append(Integer.toString(giornoDB));
			}
			stringagiorno.append("/");
			if (Integer.toString(meseDB).length() == 1) {
				stringagiorno.append("0" + Integer.toString(meseDB));
			} else {
				stringagiorno.append(Integer.toString(meseDB));
			}
			stringagiorno.append("/" + Integer.toString(annoDB));
			settimana.add(stringagiorno.toString());

			if ((gsett.contains(Integer.toString(i)) && CalUtils.checkGsett(i, cal, serviceResponse, "MGIORNI_NL"))
					|| giorniNL.contains(gc)) {
				buf.append("<td class=\"cal_vuoto\" width=\"14%\">");
				buf.append(giorni[i] + "<br><b>" + giornoDB + "</td>");
			} else {
				buf.append("<td class=\"cal_settimana\" width=\"14%\">");
				buf.append("<a class=\"calh\" href=\"AdapterHTTP?PAGE=GestSlotPage" + "&CODCPI=" + codCpi);
				buf.append("&MOD=0");
				buf.append("&cod_vista=" + cod_vista);
				buf.append("&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB);
				buf.append("&mese=" + mese + "&anno=" + anno);
				buf.append("\">");
				buf.append(giorni[i] + "<br><b>" + giornoDB + "</b></a></td>");
			}
			cal.add(Calendar.DATE, 1);
		}
		buf.append("</tr>");

		// GRIGLIA
		String moduleName = "MSLOTSETTIMANA";
		SourceBean app = (SourceBean) serviceResponse.getAttribute(moduleName);
		String strDescFascia = "";
		int numFascia = 0;
		// Fascia 0->8
		strDescFascia = "0->8";
		numFascia = 1;
		Vector rows1 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "1");
		buf.append(showSlotFascia(rows1, numFascia, strDescFascia));
		// Fascia 8->10
		strDescFascia = "8->10";
		numFascia = 2;
		Vector rows2 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "2");
		buf.append(showSlotFascia(rows2, numFascia, strDescFascia));
		// Fascia 10->12
		strDescFascia = "10->12";
		numFascia = 3;
		Vector rows3 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "3");
		buf.append(showSlotFascia(rows3, numFascia, strDescFascia));
		// Fascia 12->14
		strDescFascia = "12->14";
		numFascia = 4;
		Vector rows4 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "4");
		buf.append(showSlotFascia(rows4, numFascia, strDescFascia));
		// Fascia 14->16
		strDescFascia = "14->16";
		numFascia = 5;
		Vector rows5 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "5");
		buf.append(showSlotFascia(rows5, numFascia, strDescFascia));
		// Fascia Dalle 16
		strDescFascia = "Dalle 16";
		numFascia = 6;
		Vector rows6 = app.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "NUMFASCIAORA", "6");
		buf.append(showSlotFascia(rows6, numFascia, strDescFascia));

		buf.append("</table>");
		return (buf.toString());
	}

	private static String showSlotFascia(Vector rows, int numFascia, String strDescFascia) {
		boolean trovato = false;
		String data = "";
		BigDecimal nro;
		SourceBean row = null;
		StringBuffer bf = new StringBuffer();
		String desc = "";
		String numTipo = "";
		int i, j;

		bf.append("<tr class=\"note\"><td class=\"cal_fascia\" align=\"center\">" + strDescFascia + "</td>");
		for (i = 0; i < 6; i++) {
			bf.append("<td class=\"cal_bordato\">");
			j = 0;
			trovato = false;
			while (j < rows.size() && !trovato) {
				row = (SourceBean) rows.elementAt(j);
				data = row.getAttribute("DATA").toString();
				if (data.equals(settimana.get(i))) {
					trovato = true;
					numTipo = row.getAttribute("NUMTIPO").toString();
					nro = (BigDecimal) row.getAttribute("NUMNUMERO");
					desc = row.getAttribute("STRDESCTIPO").toString();
					bf.append(desc + "&nbsp;" + nro.toString());
				}
				j++;
			}
			if (!trovato) {
				bf.append("&nbsp;");
			} else {
				if (j < rows.size()) {
					row = (SourceBean) rows.elementAt(j);
					data = row.getAttribute("DATA").toString();
					// while(data.equals(row.getAttribute("DATA").toString()) &&
					// j < rows.size())
					while (data.equals(settimana.get(i)) && j < rows.size())
					// if(data.equals(row.getAttribute("DATA").toString()))
					{
						numTipo = row.getAttribute("NUMTIPO").toString();
						nro = (BigDecimal) row.getAttribute("NUMNUMERO");
						desc = row.getAttribute("STRDESCTIPO").toString();
						bf.append("<br>" + desc + "&nbsp;" + nro.toString());
						j++;
						if (j < rows.size()) {
							row = (SourceBean) rows.elementAt(j);
							data = row.getAttribute("DATA").toString();
						}
					}
				}
			}
			bf.append("</td>");
		}
		bf.append("</tr>");
		return (bf.toString());
	}

	public static String listContattiRicerca(RequestContainer requestContainer, SourceBean serviceResponse,
			ResponseContainer responseContainer, String codCpi) {
		StringBuffer buf = new StringBuffer();
		SourceBean _serviceRequest = requestContainer.getServiceRequest();
		content = (SourceBean) serviceResponse.getAttribute("MCONTATTIRICERCA");
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
				"MCONTATTIRICERCA");

		// Profilatura - Stefy 15/07/2004
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "PCalendario");
		boolean canInsCont = attributi.containsButton("NEW_CONT");
		boolean canDelCont = attributi.containsButton("DEL_CONT");
		// --

		buf.append("<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"19\" class=\"cal_header\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");

		buf.append(" Contatti - Risultato della Ricerca");
		buf.append("</td>");

		buf.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"13\" height=\"19\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr class=\"cal\">");
		buf.append("<td class=\"cal\" width=\"6\"></td>");
		buf.append("<td class=\"cal\" width=\"100%\" align=\"center\">");
		// buf.append("<TABLE class=\"appuntamenti\" margin=\"0\"
		// cellspacing=\"0\"");
		// buf.append("</TABLE>");

		String giorno = StringUtils.getAttributeStrNotNull(_serviceRequest, "giorno");
		String mese = StringUtils.getAttributeStrNotNull(_serviceRequest, "mese");
		String anno = StringUtils.getAttributeStrNotNull(_serviceRequest, "anno");
		String cod_vista = StringUtils.getAttributeStrNotNull(_serviceRequest, "cod_vista");
		String mod = StringUtils.getAttributeStrNotNull(_serviceRequest, "MOD");

		String linkDett = "&mese=" + mese + "&anno=" + anno;
		linkDett += "&cod_vista=" + cod_vista + "&MOD=" + mod;
		String sel_operatore = (String) _serviceRequest.getAttribute("sel_operatore");
		if (sel_operatore != null) {
			linkDett += "&sel_operatore=" + sel_operatore;
		}
		String sel_tipo = (String) _serviceRequest.getAttribute("sel_tipo");
		if (sel_tipo != null) {
			linkDett += "&sel_tipo=" + sel_tipo;
		}
		String STRIO = (String) _serviceRequest.getAttribute("STRIO");
		if (STRIO != null) {
			linkDett += "&STRIO=" + STRIO;
		}
		String sel_motivo = (String) _serviceRequest.getAttribute("sel_motivo");
		if (sel_motivo != null) {
			linkDett += "&sel_motivo=" + sel_motivo;
		}
		String effettoCon = (String) _serviceRequest.getAttribute("effettoCon");
		if (effettoCon != null) {
			linkDett += "&effettoCon=" + effettoCon;
		}

		String strCodiceFiscaleAz = (String) _serviceRequest.getAttribute("strCodiceFiscaleAz");
		if (strCodiceFiscaleAz != null) {
			linkDett += "&strCodiceFiscaleAz=" + strCodiceFiscaleAz;
		}
		String strRagSoc = (String) _serviceRequest.getAttribute("strRagSoc");
		if (strRagSoc != null) {
			linkDett += "&strRagSoc=" + strRagSoc;
		}
		String piva = (String) _serviceRequest.getAttribute("piva");
		if (piva != null) {
			linkDett += "&piva=" + piva;
		}

		String strCodiceFiscale = (String) _serviceRequest.getAttribute("strCodiceFiscale");
		if (strCodiceFiscale != null) {
			linkDett += "&strCodiceFiscale=" + strCodiceFiscale;
		}
		String strCognome = (String) _serviceRequest.getAttribute("strCognome");
		if (strCognome != null) {
			linkDett += "&strCognome=" + strCognome;
		}
		String strNome = (String) _serviceRequest.getAttribute("strNome");
		if (strNome != null) {
			linkDett += "&strNome=" + strNome;
		}

		String dataDal = (String) _serviceRequest.getAttribute("dataDal");
		if (dataDal != null) {
			linkDett += "&dataDal=" + dataDal;
		}
		String dataAl = (String) _serviceRequest.getAttribute("dataAl");
		if (dataDal != null) {
			linkDett += "&dataAl=" + dataAl;
		}

		buf.append("<table width=\"100%\" margin=\"0\">");
		buf.append(CalUtils.generaColonne(moduleBean));
		if (content != null) {
			buf.append(CalUtils.generaRighe2(moduleBean, serviceResponse, "MCONTATTIRICERCA", requestContainer,
					responseContainer, linkDett, canDelCont));
			buf.append(CalUtils.makeJScript("MCONTATTIRICERCA"));
		}
		buf.append("</TABLE>");
		buf.append("</td><td class=\"cal\" width=\"6\"></td></tr>");
		buf.append(
				"<tr class=\"cal\"><td class=\"cal_header\" align=\"left\" valign=\"bottom\" width=\"13\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"cal_header\" align=\"right\" valign=\"bottom\" width=\"19\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");

		return buf.toString();
	} // public static String listaContattiGiorno(..., ...)
}