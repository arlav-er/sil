package it.eng.sil.tags;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.agenda.CalUtils;
import it.eng.sil.util.Utils;

/*
 * @author: Stefania Orioli - Agosto 2003
 *  Parametri:
 *  @p_giorno   \
 *  @p_mese      - parametri di navigazione sul calendario
 *  @p_anno     /
 *
 *  @giornoDB \
 *  @meseDB     - parametri di selezione sul DB
 *  @annoDB   /  
 *
 *  @cod_vista -> codice della vista scelta
 *  @mod -> modulo da caricare nella sezione relativa agli appuntamenti
 *          Es. mod=0 -> Sommario Giornaliero
 *              mod=1 -> Sommario Settimanale
 *              mod=2 -> Risultato Ricerca
 *  
 *
 *  Questa Tag Library disegna la griglia (tabella HTML) per
 *  il calendario mensile.
 *
 *  ----------------------------------------------
 *  |         << Agosto >>  << 2003 >>           |
 *  ----------------------------------------------
 *  |  | LUN | MAR | MER | GIO | VEN | SAB | DOM |
 *  ----------------------------------------------
 *  |> |     |     |     |     |   1 |   2 |   3 |
 *  ----------------------------------------------
 *  |> |   4 |   5 |   6 |   7 |   8 |   9 |  10 |
 *  ----------------------------------------------
 *  |> |  11 |  12 |  13 |  14 |  15 |  16 |  17 |
 *  ----------------------------------------------
 *  |> |  18 |  19 |  20 |  21 |  22 |  23 |  24 |
 *  ----------------------------------------------
 *  |> |  25 |  26 |  27 |  28 |  29 |  30 |  31 |
 *  ----------------------------------------------
 *  |  Oggi: 14 Agosto 2003                      |
 *  ----------------------------------------------
 *
 *  Se i parametri @p_mese, @p_anno sono valorizzati disegna 
 *  il calendario relativo al @p_mese/@p_anno, altrimenti 
 *  visualizza il mese corrente.
 *
 *  I giorni del mese sono ripartiti su 5/6 righe.
 *  La 6' riga serve solamente nel caso in cui il
 *  mese inizia di Sabato oppure di Domenica.
 *
 */

public class calendario extends TagSupport {
	private static String mesi[] = { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto",
			"Settembre", "Ottobre", "Novembre", "Dicembre" };
	private static String giorni[] = { "&nbsp;", "LUN", "MAR", "MER", "GIO", "VEN", "SAB", "DOM" };
	private static int mapping_mesiDB[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	private String mese_prec;
	private String mese_succ;
	private String anno_prec;
	private String anno_succ;
	private String oggi_link;

	private RequestContainer requestContainer = null;
	private SourceBean serviceRequest = null;
	private ResponseContainer responseContainer = null;
	private SourceBean serviceResponse = null;
	private SourceBean content = null;

	// Parametri
	private String moduleName = null;

	public void setModuleName(String mName) {
		moduleName = mName;
	}

	private String mod = "";

	public void setMod(String mMod) {
		mod = mMod;
	}

	private String pageName = null;

	public void setPageName(String pName) {
		pageName = pName;
	}

	private String attivaFestivi = "0";

	public void setAttivaFestivi(String t) {
		attivaFestivi = t;
	}

	private String modVista = null;

	public void setModVista(String mName) {
		modVista = mName;
	}

	private String codCpi = null;

	public void setCodCpi(String mCodCpi) {
		codCpi = mCodCpi;
	}

	private String p_giorno = "";
	private String p_mese = "";
	private String p_anno = "";

	private String giornoDB = "";
	private String meseDB = "";
	private String annoDB = "";

	private String cod_vista = "";

	// Istanza usata per il confronto con la data DB
	private Calendar calDB = null;
	// Giorni festivi e/o non lavorativi
	private ArrayList giorniNL = null;
	// Giorni della settimana non lavorativi
	private ArrayList gsett = null;
	// giorni da Evidenziare nel calendario
	private ArrayList giorniVista = new ArrayList();

	// Link Base
	private String baseHref = null;

	// Inizio Tag Library
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
			requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
			serviceRequest = requestContainer.getServiceRequest();
			responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
			serviceResponse = responseContainer.getServiceResponse();

			if (serviceRequest.containsAttribute("giorno")) {
				p_giorno = serviceRequest.getAttribute("giorno").toString();
			}
			if (serviceRequest.containsAttribute("mese")) {
				p_mese = serviceRequest.getAttribute("mese").toString();
			}
			if (serviceRequest.containsAttribute("anno")) {
				p_anno = serviceRequest.getAttribute("anno").toString();
			}
			if (serviceRequest.containsAttribute("giornoDB")) {
				giornoDB = serviceRequest.getAttribute("giornoDB").toString();
			}
			if (serviceRequest.containsAttribute("meseDB")) {
				meseDB = serviceRequest.getAttribute("meseDB").toString();
			}
			if (serviceRequest.containsAttribute("annoDB")) {
				annoDB = serviceRequest.getAttribute("annoDB").toString();
			}
			if (serviceRequest.containsAttribute("cod_vista")) {
				cod_vista = serviceRequest.getAttribute("cod_vista").toString();
			}

			JspWriter out = pageContext.getOut();

			baseHref = "<a class=\"cal\" href=\"AdapterHTTP?PAGE=" + pageName + "&CODCPI=" + codCpi;

			// Data Odierna
			Calendar oggi = Calendar.getInstance();
			int oggi_gg = oggi.get(5);
			int oggi_mm = oggi.get(2);
			String oggi_mese = mesi[oggi_mm];
			int oggi_aa = oggi.get(1);
			oggi_link = baseHref + "&giornoDB=" + oggi_gg + "&meseDB=" + mapping_mesiDB[oggi_mm] + "&annoDB=" + oggi_aa;
			oggi_link += "&mese=" + oggi_mm + "&anno=" + oggi_aa + "&giorno=" + oggi_gg;
			oggi_link += "&MOD=0" + "&cod_vista=" + cod_vista + "\">";

			// Istanza usata per la creazione della griglia
			Calendar cal = Calendar.getInstance();
			// Istanza usata per il confronto con la data odierna
			Calendar giorno_corrente = Calendar.getInstance();

			// Confronto con la data DB
			calDB = Calendar.getInstance();
			int mese_db;
			if (!giornoDB.equals("") && !meseDB.equals("") && !annoDB.equals("")) {
				mese_db = Integer.parseInt(meseDB) - 1;
				if (mese_db < 0) {
					mese_db = 11;
				}
				calDB.set(Integer.parseInt(annoDB), mese_db, Integer.parseInt(giornoDB));
			} else {
				giornoDB = Integer.toString(oggi_gg);
				meseDB = Integer.toString(mapping_mesiDB[oggi_mm]);
				annoDB = Integer.toString(oggi_aa);
				calDB.set(oggi_aa, oggi_mm, oggi_gg);
			}

			// Parametri derivanti da una ricerca (presenti se mod=2)
			String sel_operatore = (String) serviceRequest.getAttribute("sel_operatore");
			String sel_servizio = (String) serviceRequest.getAttribute("sel_servizio");
			String sel_aula = (String) serviceRequest.getAttribute("sel_aula");
			String strCodiceFiscaleAz = (String) serviceRequest.getAttribute("strCodiceFiscaleAz");
			String piva = (String) serviceRequest.getAttribute("piva");
			String strRagSoc = (String) serviceRequest.getAttribute("strRagSoc");
			String strCodiceFiscale = (String) serviceRequest.getAttribute("strCodiceFiscale");
			String strCognome = (String) serviceRequest.getAttribute("strCognome");
			String strNome = (String) serviceRequest.getAttribute("strNome");
			String dataDal = (String) serviceRequest.getAttribute("dataDal");
			String dataAl = (String) serviceRequest.getAttribute("dataAl");
			String esitoApp = (String) serviceRequest.getAttribute("esitoApp");
			String sel_tipo = (String) serviceRequest.getAttribute("sel_tipo");
			String STRIO = (String) serviceRequest.getAttribute("STRIO");
			String sel_motivo = (String) serviceRequest.getAttribute("sel_motivo");
			String effettoCon = (String) serviceRequest.getAttribute("effettoCon");

			// Link Base
			String aHref = null;
			switch (Integer.parseInt(mod)) {
			case 0: // Sommario Giornaliero
				aHref = baseHref + "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB;
				break;
			case 1: // Sommario Settimanale
				aHref = baseHref + "&nrosDB=" + serviceRequest.getAttribute("nrosDB").toString() + "&annoDB="
						+ serviceRequest.getAttribute("annoDB");
				break;
			case 2: // Risultato Ricerca
				aHref = baseHref + "&sel_operatore=" + Utils.notNull(sel_operatore) + "&sel_servizio="
						+ Utils.notNull(sel_servizio) + "&sel_aula=" + Utils.notNull(sel_aula) + "&strCodiceFiscaleAz="
						+ Utils.notNull(strCodiceFiscaleAz) + "&piva=" + Utils.notNull(piva) + "&strRagSoc="
						+ Utils.notNull(strRagSoc) + "&strCodiceFiscale=" + Utils.notNull(strCodiceFiscale)
						+ "&strCognome=" + Utils.notNull(strCognome) + "&strNome=" + Utils.notNull(strNome)
						+ "&dataDal=" + Utils.notNull(dataDal) + "&dataAl=" + Utils.notNull(dataAl) + "&esitoApp="
						+ Utils.notNull(esitoApp);
				break;
			case 3: // Risultato Ricerca
				aHref = baseHref + "&sel_operatore=" + Utils.notNull(sel_operatore) + "&sel_tipo="
						+ Utils.notNull(sel_tipo) + "&STRIO=" + Utils.notNull(STRIO) + "&sel_motivo="
						+ Utils.notNull(sel_motivo) + "&effettoCon=" + Utils.notNull(effettoCon)
						+ "&strCodiceFiscaleAz=" + Utils.notNull(strCodiceFiscaleAz) + "&piva=" + Utils.notNull(piva)
						+ "&strRagSoc=" + Utils.notNull(strRagSoc) + "&strCodiceFiscale="
						+ Utils.notNull(strCodiceFiscale) + "&strCognome=" + Utils.notNull(strCognome) + "&strNome="
						+ Utils.notNull(strNome) + "&dataDal=" + Utils.notNull(dataDal) + "&dataAl="
						+ Utils.notNull(dataAl);
				break;
			default:
				aHref = baseHref;
				break;
			}

			// Se i parametri in ingresso @p_mese e @p_anno non sono nulli
			// allora va visualizzato il calendario relativo a @p_mese/@p_anno
			if (!p_mese.equals("") && !p_anno.equals("")) {
				cal.set(Integer.parseInt(p_anno), Integer.parseInt(p_mese), 1);
			}
			int giorno = cal.get(5);
			int mese = cal.get(2);
			int anno = cal.get(1);
			String nome_mese = mesi[mese];

			/* GIORNI NON LAVORATIVI E GIORNI DA EVIDENZIARE IN BASE ALLA VISTA */
			if (moduleName != null) {
				content = (SourceBean) serviceResponse.getAttribute(moduleName);
			}
			// setGiorniNL(anno);
			giorniNL = new ArrayList(CalUtils.setGiorniNL(anno, serviceResponse, moduleName));
			gsett = new ArrayList(CalUtils.setGsett(anno, serviceResponse, moduleName));
			setGiorniVista();

			// Imposto la data al primo giorno del mese corrente
			cal.set(5, 1);

			// Imposto i link di scorrimento del calendario
			mese_prec = aHref + "&MOD=" + mod;
			if ((cod_vista != null) && !cod_vista.equals("")) {
				mese_prec += "&cod_vista=" + cod_vista;
			}
			if ((mese - 1) < 0) {
				mese_prec += "&mese=11&anno=" + (anno - 1);
			} else {
				mese_prec += "&mese=" + (mese - 1) + "&anno=" + anno;
			}
			mese_prec += "\">";
			mese_succ = aHref + "&MOD=" + mod;
			if ((cod_vista != null) && !cod_vista.equals("")) {
				mese_succ += "&cod_vista=" + cod_vista;
			}
			if ((mese + 1) == 12) {
				mese_succ += "&mese=0&anno=" + (anno + 1);
			} else {
				mese_succ += "&mese=" + (mese + 1) + "&anno=" + anno;
			}
			mese_succ += "\">";
			anno_prec = aHref + "&MOD=" + mod;
			if ((cod_vista != null) && !cod_vista.equals("")) {
				anno_prec += "&cod_vista=" + cod_vista;
			}
			anno_prec += "&mese=" + mese + "&anno=" + (anno - 1) + "\">";
			anno_succ = aHref + "&MOD=" + mod;
			if ((cod_vista != null) && !cod_vista.equals("")) {
				anno_succ += "&cod_vista=" + cod_vista;
			}
			anno_succ += "&mese=" + mese + "&anno=" + (anno + 1) + "\">";

			// Giorno della settimana (espresso come numero) per il primo giorno
			// del mese
			// day_of_week -> Sunday(1), Monday(2), Tuesday(3), Wednesday(4),
			// Thursday(5), Friday(6), Saturday(7)
			// il calendario italiano invece parte da Lunedì
			// cal.setFirstDayOfWeek(cal.MONDAY); int gds = cal.get(7);// <- non
			// funziona???
			int gds = cal.get(7) - 1;
			// Se il primo giorno del mese cade di Domenica ==> gds=0, ma per
			// noi deve valere 7
			if (gds == 0) {
				gds = 7;
			}
			// contatore con il numero del giorno
			int ngiorno = 1;
			// ultimo giorno del mese
			int ultimog = cal.getActualMaximum(5);
			int i;

			// Memorizzo il codice HTML per disegnare la griglia (tabella) con
			// il
			// calendario nella stringa "txt"
			String txt = "<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" noshade>";

			txt += "<tr><td align=\"left\" valign=\"top\" width=\"13\" height=\"19\" class=\"cal_header\">";
			txt += "<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\">";

			// Header della tabella, es.: << Agosto >> << 2003 >>
			// txt += "<th class=\"cal_header\" colspan=\"8\" valign=\"middle\"
			// cellpadding=\"2px\">";
			txt += "<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">";

			txt += mese_prec + "<img src=\"../../img/previous.gif\" border=\"0\" alt=\"&lt;&lt;\" /></a>&nbsp;";
			txt += nome_mese + "&nbsp;";
			txt += mese_succ + "<img src=\"../../img/next.gif\" border=\"0\" alt=\"&gt;&gt;\" /></a>&nbsp;";
			txt += anno_prec + "<img src=\"../../img/previous.gif\" border=\"0\" alt=\"&lt;&lt;\" /></a>&nbsp;";
			txt += anno + "&nbsp;";
			txt += anno_succ + "<img src=\"../../img/next.gif\" border=\"0\" alt=\"&gt;&gt;\" /></a>";
			// txt += "</th>";

			txt += "</td>";
			txt += "<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"13\" height=\"19\">";
			txt += "<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">";
			txt += "</td></tr>";

			// Corpo della tabella
			// txt += "<tbody><tr>";
			txt += "<tr class=\"cal\">";
			txt += "<td class=\"cal\" width=\"13px\">&nbsp;</td>";
			txt += "<td class=\"cal\" align=\"center\"><table width=\"223\" class=\"cal\" cellspacing=\"0\" align=\"center\">";
			txt += "<tr>";
			// Intestazione con i giorni della settimana
			// for(i=0;i<8;i++) { txt += "<td class=\"cal_settimana\"
			// align=\"center\">" + giorni[i] + "</td>"; }
			i = 0;
			txt += "<td width=\"25\" height=\"25\" align=\"center\">" + giorni[i] + "</td>";
			for (i = 1; i < 8; i++) {
				txt += "<td class=\"cal_settimana\" align=\"center\" width=\"25\" height=\"25\">" + giorni[i] + "</td>";
			}
			txt += "</tr>";

			// 1' Riga
			txt += "<tr valign=\"center\"><td width=\"25\" height=\"25\" align=\"center\" style=\"cursor: pointer\"";
			txt += " onClick=\"goTo('" + linkSett(ngiorno, mese, anno, anno) + "')\">";
			txt += "<img src=\"../../img/freccia.gif\" border=\"0\" alt=\"&gt;\"/></td>";
			for (i = 1; i < gds; i++) {
				txt += "<td class=\"cal_vuoto\" width=\"25\" height=\"25\">&nbsp;</td>";
			}
			for (i = gds; i < 8; i++) {
				giorno_corrente.set(anno, mese, ngiorno);
				txt += td_giorno(ngiorno, mese, anno, giorno_corrente, i);
				ngiorno++;
			}
			txt += "</tr>";

			// 2', 3', 4' Riga
			// contatore di riga
			int r;
			for (r = 2; r < 5; r++) {
				txt += "<tr><td width=\"25\" height=\"25\" align=\"center\" style=\"cursor: pointer\"";
				giorno_corrente.set(anno, mese, ngiorno);
				txt += " onClick=\"goTo('" + linkSett(ngiorno, mese, anno, anno) + "')\">";
				txt += "<img src=\"../../img/freccia.gif\" border=\"0\" alt=\"&gt;\" /></td>";
				for (i = 1; i < 8; i++) {
					giorno_corrente.set(anno, mese, ngiorno);
					txt += td_giorno(ngiorno, mese, anno, giorno_corrente, i);
					ngiorno++;
				}
				txt += "</tr>";
			}

			// 5' Riga
			txt += "<tr><td width=\"25\" height=\"25\" align=\"center\" style=\"cursor: pointer\"";
			giorno_corrente.set(anno, mese, ngiorno);
			txt += " onClick=\"goTo('" + linkSett(ngiorno, mese, anno, anno) + "')\">";
			txt += "<img src=\"../../img/freccia.gif\" border=\"0\" alt=\"&gt;\" /></td>";
			for (i = 1; i < 8; i++) {
				if (ngiorno <= ultimog) {
					giorno_corrente.set(anno, mese, ngiorno);
					txt += td_giorno(ngiorno, mese, anno, giorno_corrente, i);
					ngiorno++;
				} else {
					txt += "<td class=\"cal_vuoto\" width=\"25\" height=\"25\">&nbsp;</td>";
				}
			}
			txt += "</tr>";

			// Se il mese comincia di Sabato oppure di Domenica (e non è il mese
			// di Febbraio)
			// allora serve una 6' Riga...
			if ((gds == 6 || gds == 7) && (ultimog >= ngiorno)) {
				txt += "<tr><td width=\"25\" height=\"25\" align=\"center\" style=\"cursor: pointer\"";
				giorno_corrente.set(anno, mese, ngiorno);
				txt += " onClick=\"goTo('" + linkSett(ngiorno, mese, anno, anno) + "')\">";
				txt += "<img src=\"../../img/freccia.gif\" border=\"0\" alt=\"&gt;\" /></td>";
				for (i = 1; i < 8; i++) {
					if (ngiorno <= ultimog) {
						giorno_corrente.set(anno, mese, ngiorno);
						txt += td_giorno(ngiorno, mese, anno, giorno_corrente, i);
						ngiorno++;
					} else {
						txt += "<td class=\"cal_vuoto\" width=\"25\" height=\"25\">&nbsp;</td>";
					}
				}
				txt += "</tr>";
			}

			// Ultima riga: contiene il link al giorno corrente
			txt += "</table></td>";
			txt += "<td class=\"cal\" width=\"13px\"></td></tr>";
			txt += "<tr class=\"cal\"><td class=\"cal\" align=\"left\" valign=\"bottom\" width=\"13\" height=\"19\">";
			txt += "<img src=\"../../img/angoli/bia4.gif\">";
			txt += "</td>";
			txt += "<td class=\"cal\" height=\"19\" align=\"center\" valign=\"middle\"><b>";
			txt += oggi_link + "Oggi:&nbsp;" + oggi_gg + "&nbsp;" + oggi_mese + "&nbsp;" + oggi_aa;
			txt += "</b></td><td class=\"cal\" align=\"right\" valign=\"bottom\" width=\"19\" height=\"19\">";
			txt += "<img src=\"../../img/angoli/bia3.gif\">";
			txt += "</td></tr>";

			// chiudo la tabella
			txt += "</table>";

			// Stampo la griglia del calendario
			out.println(txt);
			// Debug -> da togliere poi
			// out.println(giorniNL);
			// out.println(giorniVista);

		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}
		return (SKIP_BODY);
	}

	private void setGiorniVista() {
		giorniVista = new ArrayList();
		SourceBean content = null;

		// if(serviceResponse.containsAttribute("MGIORNI_VISTA"))
		if (serviceResponse.containsAttribute(modVista)) {
			// content = (SourceBean)
			// serviceResponse.getAttribute("MGIORNI_VISTA");
			content = (SourceBean) serviceResponse.getAttribute(modVista);
		}

		if (content != null) {
			Vector rowsVista = content.getAttributeAsVector("ROWS.ROW");
			SourceBean row = null;
			String gvista;
			int i, num_gg, num_mm, num_aa;
			for (i = 0; i < rowsVista.size(); i++) {
				row = (SourceBean) rowsVista.elementAt(i);
				gvista = "";
				if (row.containsAttribute("NUMGIORNO") && row.containsAttribute("NUMMESE")
						&& row.containsAttribute("NUMANNO")) {
					num_gg = Integer.parseInt(row.getAttribute("NUMGIORNO").toString());
					num_mm = Integer.parseInt(row.getAttribute("NUMMESE").toString()) - 1;
					num_aa = Integer.parseInt(row.getAttribute("NUMANNO").toString());
					gvista = num_gg + "/" + num_mm + "/" + num_aa;
					giorniVista.add(gvista);
				}
				gvista = null;
			} // for
		} // if(content!=null)
	}

	private String td_giorno(int ngiorno, int mese, int anno, Calendar giorno_corrente, int i) {
		String buf = "";
		String gc = "";
		gc = ngiorno + "/" + mese + "/" + anno;

		if (calDB.equals(giorno_corrente) && mod.equals("0")) {
			// Giorno selezionato (attivo)
			buf += "<td class=\"cal_sel\" width=\"25\" height=\"25\" align=\"center\" "; // ">";
			buf += "onClick=\"goTo('PAGE=" + pageName + "&CODCPI=" + codCpi;
			buf += "&giornoDB=" + ngiorno + "&meseDB=" + mapping_mesiDB[mese] + "&annoDB=" + anno;
			buf += "&giorno=" + ngiorno + "&mese=" + mese + "&anno=" + anno;
			buf += "&MOD=0";
			if ((cod_vista != null) && !cod_vista.equals("")) {
				buf += "&cod_vista=" + cod_vista;
			}
			if (!attivaFestivi.equals("1") && ((gsett.contains(Integer.toString(i))
					&& CalUtils.checkGsett(i, giorno_corrente, serviceResponse, moduleName))
					|| giorniNL.contains(gc))) { // Il
													// giorno
													// attivo
													// è un
													// festivo!
				buf += "&GNL=true";
			}
			buf += "')\">";
			buf += "<b>(" + ngiorno + ")</b></td>";
		} else {
			// Altro giorno
			if (!attivaFestivi.equals("1") && ((gsett.contains(Integer.toString(i))
					&& CalUtils.checkGsett(i, giorno_corrente, serviceResponse, moduleName))
					|| giorniNL.contains(gc))) {
				// buf += "<td class=\"cal_vuoto\" align=\"center\">" + ngiorno
				// + "</td>";
				buf += "<td class=\"cal_vuoto\" align=\"center\" width=\"25\" height=\"25\">" + ngiorno + "</td>";
			} else {
				if (giorniVista.contains(gc)) {
					buf += "<td class=\"cal_evid\" ";
				} else {
					if (attivaFestivi.equals("1") && ((gsett.contains(Integer.toString(i))
							&& CalUtils.checkGsett(i, giorno_corrente, serviceResponse, moduleName))
							|| giorniNL.contains(gc))) {
						buf += "<td class=\"cal_vuoto_sel\" ";
					} else {
						buf += "<td class=\"cal_giorno\" ";
					}
				}
				buf += " width=\"25\" height=\"25\" ";
				buf += " align=\"center\"";
				buf += " onClick=\"goTo('PAGE=" + pageName + "&CODCPI=" + codCpi;
				buf += "&giornoDB=" + ngiorno + "&meseDB=" + mapping_mesiDB[mese] + "&annoDB=" + anno;
				buf += "&giorno=" + ngiorno + "&mese=" + mese + "&anno=" + anno;
				buf += "&MOD=0";
				if ((cod_vista != null) && !cod_vista.equals("")) {
					buf += "&cod_vista=" + cod_vista;
				}
				buf += "')\">" + ngiorno + "</a></td>";
			}
		}
		return (buf);
	}

	private String linkSett(int g, int m, int a, int adb) {
		Calendar csett = Calendar.getInstance();
		csett.set(a, m, g);
		int nrosDB = csett.get(Calendar.WEEK_OF_YEAR);
		if ((m == 11) && (nrosDB == 1)) {
			adb += 1;
		}
		String link = "PAGE=" + pageName + "&CODCPI=" + codCpi + "&MOD=1";
		link += "&cod_vista=" + cod_vista;
		link += "&nrosDB=" + nrosDB + "&annoDB=" + adb;
		link += "&mese=" + m + "&anno=" + a;
		return (link);
	}

}
