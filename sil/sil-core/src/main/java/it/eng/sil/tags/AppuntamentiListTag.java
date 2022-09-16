package it.eng.sil.tags;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.service.detail.impl.DelegatedDetailService;
import com.engiweb.framework.dispatching.service.list.basic.impl.DelegatedBasicListService;
import com.engiweb.framework.util.ContextScooping;
import com.engiweb.framework.util.JavaScript;

/*
 * Esegue il rendering della lista appuntamenti (costituita da una sola pagina)
 * 
 * @author: Stefania Orioli - august 2003
 */

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.agenda.CalUtils;
import it.eng.sil.module.agenda.ShowApp;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class AppuntamentiListTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AppuntamentiListTag.class.getName());
	private String _actionName = null;
	private String _moduleName = null;
	private String _serviceName = null;
	private SourceBean _content = null;
	private SourceBean _layout = null;
	private String _providerURL = null;
	private RequestContainer _requestContainer = null;
	private SourceBean _serviceRequest = null;
	private ResponseContainer _responseContainer = null;
	private SourceBean _serviceResponse = null;
	private StringBuffer _htmlStream = null;
	private Vector _columns = null;

	private static String mesiJ[] = { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto",
			"Settembre", "Ottobre", "Novembre", "Dicembre" };
	private static String mesi[] = { "", "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio",
			"Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre" };
	private static int mapping_mesiDB[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	// Giorni festivi e/o non lavorativi
	private static ArrayList giorniNL = null;
	// Giorni della settimana definiti come non lavorativi
	private static ArrayList gsett = null;
	// Indica se il giorno considerato è festivo oppure no
	boolean giornoF = false;

	private static SourceBean content = null;

	// Parametri data
	private String giornoDB = "";
	private String meseDB = "";
	private String annoDB = "";
	private String giorno = "";
	private String mese = "";
	private String anno = "";
	private String cod_vista = "";
	private String linkDett = "";

	private String mod = "0";

	public void setMod(String mMod) {
		mod = mMod;
	}

	private String titolo = "";

	public void setTitolo(String tit) {
		titolo = tit;
	}

	private String extra = "0";

	public void setExtra(String t) {
		extra = t;
	}

	private String linkPage = "";

	public void setLinkPage(String t) {
		linkPage = t;
	}

	private String nuovoPage = "";

	public void setNuovoPage(String t) {
		nuovoPage = t;
	}

	private String nuovoTxt = "";

	public void setNuovoTxt(String t) {
		nuovoTxt = t;
	}

	/*
	 * private String tipoLista = ""; public void setTipoLista(String t) { tipoLista = t; }
	 */

	private String codCpi = null;

	public void setCodCpi(String mCodCpi) {
		codCpi = mCodCpi;
	}

	private String configLabelServizio = "0";

	public void setConfigLabelServizio(String mLabelServizio) {
		configLabelServizio = mLabelServizio;
	}

	public int doStartTag() throws JspException {
		_logger.debug("AppuntamentiListTag::doStartTag:: invocato");

		HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
		_requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
		_serviceRequest = _requestContainer.getServiceRequest();
		_responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		_serviceResponse = _responseContainer.getServiceResponse();
		ConfigSingleton configure = ConfigSingleton.getInstance();

		if (_serviceRequest.containsAttribute("giornoDB")) {
			giornoDB = _serviceRequest.getAttribute("giornoDB").toString();
		}
		if (_serviceRequest.containsAttribute("meseDB")) {
			meseDB = _serviceRequest.getAttribute("meseDB").toString();
		}
		if (_serviceRequest.containsAttribute("annoDB")) {
			annoDB = _serviceRequest.getAttribute("annoDB").toString();
		}
		if (_serviceRequest.containsAttribute("giorno")) {
			giorno = _serviceRequest.getAttribute("giorno").toString();
		}
		if (_serviceRequest.containsAttribute("mese")) {
			mese = _serviceRequest.getAttribute("mese").toString();
		}
		if (_serviceRequest.containsAttribute("anno")) {
			anno = _serviceRequest.getAttribute("anno").toString();
		}
		// if(_serviceRequest.containsAttribute("MOD")) { mod =
		// _serviceRequest.getAttribute("MOD").toString(); }
		if (_serviceRequest.containsAttribute("cod_vista")) {
			cod_vista = _serviceRequest.getAttribute("cod_vista").toString();
		}

		if (_actionName != null) {
			_serviceName = _actionName;
			_content = _serviceResponse;
			SourceBean actionBean = (SourceBean) configure.getFilteredSourceBeanAttribute("ACTIONS.ACTION", "NAME",
					_actionName);
			_layout = (SourceBean) actionBean.getAttribute("CONFIG");
			_providerURL = "ACTION_NAME=" + _actionName + "&";
		} // if (_actionName != null)
		else if (_moduleName != null) {
			_serviceName = _moduleName;
			_content = (SourceBean) _serviceResponse.getAttribute(_moduleName);
			SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					_moduleName);
			_layout = (SourceBean) moduleBean.getAttribute("CONFIG");
			String pageName = (String) _serviceRequest.getAttribute("PAGE");
			_providerURL = "PAGE=" + pageName + "&MODULE=" + _moduleName + "&";
		} // if (_moduleName != null)
		else {
			_logger.fatal("AppuntamentiListTag::doStartTag:: service name non specificato !");

			throw new JspException("Business name non specificato !");
		} // if (_content == null)
		if (_content == null) {
			_logger.warn("AppuntamentiListTag::doStartTag:: content nullo");

			return SKIP_BODY;
		} // if (_content == null)
		if (_layout == null) {
			_logger.warn("AppuntamentiListTag::doStartTag:: layout nullo");

			return SKIP_BODY;
		} // if (_layout == null)
		_htmlStream = new StringBuffer();
		makeForm();
		if (mod.equals("0") && !giornoF) {
			makeFormNuovoApp();
		}
		try {
			pageContext.getOut().print(_htmlStream);
		} // try
		catch (Exception ex) {
			_logger.fatal("AppuntamentiListTag::doStartTag:: Impossibile inviare lo stream");

			throw new JspException("Impossibile inviare lo stream");
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	// Predispone la form all'interno della pagina
	private void makeForm() throws JspException {
		// Profilatura - Stefy 15/07/2004
		SessionContainer sessionContainer = _requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, linkPage);
		boolean canInsApp = attributi.containsButton("NEW_APP");
		boolean canDelApp = attributi.containsButton("DEL_APP");
		// --
		// Stondature TOP - Modifica del 09/03/2004 - Stefania Orioli
		_htmlStream.append(
				"<table cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		_htmlStream.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"19\" class=\"cal_header\">");
		_htmlStream.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		_htmlStream.append("<td class=\"cal_header\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		_htmlStream.append(
				"<input name=\"configLabelServizio\" type=\"hidden\" value=\"" + configLabelServizio + "\"/>\n");

		// end top
		// _htmlStream.append("<TABLE class=\"lista\" align=\"center\">");
		if (mod.equals("0")) {
			// _htmlStream.append("<tr><td class=\"cal_header\"
			// colspan=\"2\">");
			// Data Odierna
			Calendar oggi = Calendar.getInstance();
			int oggi_gg = oggi.get(5);
			int oggi_mm = oggi.get(2);
			int oggi_aa = oggi.get(1);

			if (giornoDB.equals("")) {
				giornoDB = Integer.toString(oggi_gg);
			}
			if (meseDB.equals("")) {
				meseDB = Integer.toString(mapping_mesiDB[oggi_mm]);
			}
			if (annoDB.equals("")) {
				annoDB = Integer.toString(oggi_aa);
			}
			if (anno.equals("")) {
				anno = Integer.toString(oggi_aa);
			}

			linkDett = "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB;
			linkDett += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
			linkDett += "&MOD=" + mod + "&cod_vista=" + cod_vista;
			linkDett += "&data_cod=" + giornoDB + "/" + meseDB + "/" + annoDB;

			Calendar giornoAtt = Calendar.getInstance();
			content = (SourceBean) _serviceResponse.getAttribute("MGIORNI_NL");
			// setGiorniNL(Integer.parseInt(annoDB));
			giorniNL = new ArrayList(CalUtils.setGiorniNL(Integer.parseInt(anno), _serviceResponse, "MGIORNI_NL"));
			gsett = new ArrayList(CalUtils.setGsett(Integer.parseInt(anno), _serviceResponse, "MGIORNI_NL"));

			giornoAtt.set(Integer.parseInt(annoDB), Integer.parseInt(meseDB) - 1, Integer.parseInt(giornoDB));
			int gds = giornoAtt.get(Calendar.DAY_OF_WEEK) - 1;
			if (gds == 0) {
				gds = 7;
			}
			String gc = giornoDB + "/" + Integer.toString(Integer.parseInt(meseDB) - 1) + "/" + annoDB;
			if ((gsett.contains((Integer.toString(gds)))
					&& CalUtils.checkGsett(gds, giornoAtt, _serviceResponse, "MGIORNI_NL")) || giorniNL.contains(gc)) {
				giornoF = true;
			} else {
				giornoF = false;
			}

			Calendar giornoPrec = Calendar.getInstance();
			Calendar giornoSucc = Calendar.getInstance();
			// Giorno Precedente
			giornoPrec.set(Integer.parseInt(annoDB), Integer.parseInt(meseDB) - 1, Integer.parseInt(giornoDB));
			int gp, mp, ap;
			gp = giornoPrec.get(Calendar.DATE);
			mp = giornoPrec.get(Calendar.MONTH);
			ap = giornoPrec.get(Calendar.YEAR);
			boolean gok = false;
			while (!gok) {
				giornoPrec.add(Calendar.DATE, -1);
				gp = giornoPrec.get(Calendar.DATE);
				mp = giornoPrec.get(Calendar.MONTH);
				ap = giornoPrec.get(Calendar.YEAR);
				gc = gp + "/" + mp + "/" + ap;
				gds = giornoPrec.get(Calendar.DAY_OF_WEEK) - 1;
				if (gds == 0) {
					gds = 7;
				}
				if ((gsett.contains(Integer.toString(gds))
						&& CalUtils.checkGsett(gds, giornoPrec, _serviceResponse, "MGIORNI_NL"))
						|| giorniNL.contains(gc)) {
					gok = false;
				} else {
					gok = true;
				}
			}
			String linkGPrec = "<a href=\"AdapterHTTP?PAGE=" + linkPage + "&CODCPI=" + codCpi;
			linkGPrec += "&giornoDB=" + gp + "&meseDB=" + mapping_mesiDB[mp] + "&annoDB=" + ap;
			linkGPrec += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
			linkGPrec += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";

			// Giorno Successivo
			giornoSucc.set(Integer.parseInt(annoDB), Integer.parseInt(meseDB) - 1, Integer.parseInt(giornoDB));
			int gs, ms, as;
			gs = giornoSucc.get(Calendar.DATE);
			ms = giornoSucc.get(Calendar.MONTH);
			as = giornoSucc.get(Calendar.YEAR);
			gok = false;
			while (!gok) {
				giornoSucc.add(Calendar.DATE, 1);
				gs = giornoSucc.get(Calendar.DATE);
				ms = giornoSucc.get(Calendar.MONTH);
				as = giornoSucc.get(Calendar.YEAR);
				gc = gs + "/" + ms + "/" + as;
				gds = giornoSucc.get(Calendar.DAY_OF_WEEK) - 1;
				if (gds == 0) {
					gds = 7;
				}
				if ((gsett.contains(Integer.toString(gds))
						&& CalUtils.checkGsett(gds, giornoSucc, _serviceResponse, "MGIORNI_NL"))
						|| giorniNL.contains(gc)) {
					gok = false;
				} else {
					gok = true;
				}
			}
			String linkGSucc = "<a href=\"AdapterHTTP?PAGE=" + linkPage + "&CODCPI=" + codCpi;
			linkGSucc += "&giornoDB=" + gs + "&meseDB=" + mapping_mesiDB[ms] + "&annoDB=" + as;
			linkGSucc += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
			linkGSucc += "&MOD=" + mod + "&cod_vista=" + cod_vista + "\">";

			_htmlStream.append(linkGPrec);
			_htmlStream.append("<img src=\"../../img/previous.gif\" alt=\"&lt;&lt;\">");
			_htmlStream.append("</a> ");
			_htmlStream.append(giornoDB + " " + mesi[Integer.parseInt(meseDB)] + " " + annoDB);
			_htmlStream.append(" ");
			_htmlStream.append(linkGSucc);
			_htmlStream.append("<img src=\"../../img/next.gif\" alt=\"&gt;&gt;\">");
			_htmlStream.append("</a>");

			// Stondature TOP
			_htmlStream.append("</td>");
			_htmlStream.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"6\" height=\"19\">");
			_htmlStream.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
			_htmlStream.append("</td></tr>");
			_htmlStream.append("<tr class=\"cal\">");
			_htmlStream.append("<td class=\"cal\" width=\"6\"></td>");
			_htmlStream.append("<td class=\"cal\" width=\"100%\" align=\"center\">");
			_htmlStream.append("<TABLE class=\"appuntamenti\" margin=\"0\" cellspacing=\"0\">");
			// _htmlStream.append("<TABLE width=\"98%\" align=\"center\"
			// margin=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			// END TOP

			// _htmlStream.append("</td></tr>")
			if (!giornoF) {
				// Lista degli Eventi
				if (extra.equals("1")) {
					_htmlStream.append(ShowApp.listaEventiGiorno(_requestContainer, _serviceResponse, codCpi));
				}

				makeFormNuovoApp();
				_htmlStream.append("<tr><td class=\"cal_titolo2\" align=\"center\" colspan=\"2\">");
				// _htmlStream.append(titolo + " <input type=\"submit\"
				// class=\"ag_pulsanti\" value=\"" + nuovoTxt + "\"/>");
				// Profilatura - Stefy 15/07/2004
				if (canInsApp) {
					_htmlStream.append(
							titolo + " <input type=\"submit\" class=\"ag_pulsanti\" value=\"" + nuovoTxt + "\"/>");
				} else {
					_htmlStream.append(titolo);
				}
				// --
				_htmlStream.append("</td></tr></form>\n");

			}

		}
		if (mod.equals("2")) {
			giornoF = false;
			// _htmlStream.append("<tr><td class=\"cal_titolo\"
			// align=\"center\">");
			_htmlStream.append(titolo + " - Risultato della Ricerca");
			// _htmlStream.append("</td></tr>");
			// Stondature TOP
			_htmlStream.append("</td>");
			_htmlStream.append("<td class=\"cal_header\" align=\"right\" valign=\"top\" width=\"13\" height=\"19\">");
			_htmlStream.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
			_htmlStream.append("</td></tr>");
			_htmlStream.append("<tr class=\"cal\">");
			_htmlStream.append("<td class=\"cal\" width=\"6\"></td>");
			_htmlStream.append("<td class=\"cal\" width=\"100%\" align=\"center\">");
			_htmlStream.append("<TABLE class=\"appuntamenti\" margin=\"0\" cellspacing=\"0\"");
			// _htmlStream.append("<TABLE width=\"98%\" align=\"center\"
			// margin=\"0\" cellspacing=\"0\">");
			// END TOP

			linkDett = "&mese=" + mese + "&anno=" + anno;
			linkDett += "&cod_vista=" + cod_vista + "&MOD=" + mod;
			String sel_operatore = (String) _serviceRequest.getAttribute("sel_operatore");
			if (sel_operatore != null) {
				linkDett += "&sel_operatore=" + sel_operatore;
			}
			String sel_servizio = (String) _serviceRequest.getAttribute("sel_servizio");
			if (sel_servizio != null) {
				linkDett += "&sel_servizio=" + sel_servizio;
			}
			String sel_aula = (String) _serviceRequest.getAttribute("sel_aula");
			if (sel_aula != null) {
				linkDett += "&sel_aula=" + sel_aula;
			}
			String esitoApp = (String) _serviceRequest.getAttribute("esitoApp");
			if (esitoApp != null) {
				linkDett += "&esitoApp=" + esitoApp;
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
		}
		_htmlStream.append("</TABLE>");

		if (!giornoF) {
			// makeNavigationButton();
			_htmlStream.append("<TABLE class=\"appuntamenti\" margin=\"0\" cellspacing=\"0\">\n");
			// _htmlStream.append("<TABLE width=\"98%\" align=\"center\"
			// margin=\"0\" cellspacing=\"0\">");
			makeColumns();
			// makeRows();
			// Profilatura - Stefy 15/07/2004
			makeRows(canDelApp);
			// --
			_htmlStream.append("<tr><td colspan=\"" + (_columns.size() + 1) + "\">&nbsp;</td>");
			// makeButton();
			_htmlStream.append("</TABLE>\n");
		}

		if (!giornoF && mod.equals("0")) {
			// Lista dei contatti
			if (extra.equals("1")) {
				_htmlStream.append(
						ShowApp.listaContattiGiorno(_requestContainer, _serviceResponse, _responseContainer, codCpi));
			}
		}
		// Stondature BOTTOM - Modifiche 09/03/2004 - Stefania Orioli
		_htmlStream.append("</td><td class=\"cal\" width=\"6\"></td></tr>");
		_htmlStream.append(
				"<tr class=\"cal\"><td class=\"cal_header\" align=\"left\" valign=\"bottom\" width=\"13\" height=\"6\">");
		_htmlStream.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		_htmlStream.append("<td class=\"cal_header\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		_htmlStream
				.append("</td><td class=\"cal_header\" align=\"right\" valign=\"bottom\" width=\"19\" height=\"6\">");
		_htmlStream.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		// end BOTTOM
		makeJavaScript();
		// _htmlStream.append("</FORM>\n");
	} // public void makeForm()

	// Inserisce la form per inserire il nuovo appuntamento
	private void makeFormNuovoApp() {
		SessionContainer sessionContainer = _requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		// SO - 16/02/2005 - Il codCpi viene ora passato come parametro
		// obbligatorio
		/*
		 * String codCpi = ""; //int cdnUt = user.getCodut(); int cdnTipoGruppo = user.getCdnTipoGruppo();
		 * if(cdnTipoGruppo==1) { codCpi = user.getCodRif(); } else { codCpi =
		 * _requestContainer.getAttribute("agenda_codCpi").toString(); }
		 */

		// _htmlStream.append("<br>\n");
		_htmlStream.append("<form name=\"formNuovoApp\" action=\"AdapterHTTP\" method=\"POST\" "
				+ "onSubmit=\"return controllaNullaTL();\">\n"); // GG
																	// 8-10-2004
		_htmlStream.append("<input name=\"PAGE\" type=\"hidden\" value=\"" + nuovoPage + "\"/>\n");
		_htmlStream.append("<input name=\"CODCPI\" type=\"hidden\" value=\"" + codCpi + "\"/>\n");
		_htmlStream.append("<input name=\"giornoDB\" type=\"hidden\" value=\"" + giornoDB + "\"/>\n");
		_htmlStream.append("<input name=\"meseDB\" type=\"hidden\" value=\"" + meseDB + "\"/>\n");
		_htmlStream.append("<input name=\"annoDB\" type=\"hidden\" value=\"" + annoDB + "\"/>\n");
		_htmlStream.append("<input name=\"giorno\" type=\"hidden\" value=\"" + giorno + "\"/>\n");
		_htmlStream.append("<input name=\"mese\" type=\"hidden\" value=\"" + mese + "\"/>\n");
		_htmlStream.append("<input name=\"anno\" type=\"hidden\" value=\"" + anno + "\"/>\n");
		_htmlStream.append("<input name=\"MOD\" type=\"hidden\" value=\"" + mod + "\"/>\n");
		_htmlStream.append("<input name=\"cod_vista\" type=\"hidden\" value=\"" + cod_vista + "\"/>\n");
		_htmlStream.append(
				"<input name=\"configLabelServizio\" type=\"hidden\" value=\"" + configLabelServizio + "\"/>\n");
		_htmlStream.append(
				"<input name=\"data_cod\" type=\"hidden\" value=\"" + giornoDB + "/" + meseDB + "/" + annoDB + "\"/>");
		// _htmlStream.append("<input type=\"submit\" class=\"pulsanti\"
		// value=\"Inserisci Nuovo Appuntamento\"/>");
		// _htmlStream.append("<input type=\"submit\" class=\"ag_pulsanti\"
		// value=\"" + nuovoTxt + "\"/>");
		// _htmlStream.append("</form>");
	}

	// Inserisce il codice javascript
	private void makeJavaScript() {
		_htmlStream.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n");
		_htmlStream.append("<!--\n");
		_htmlStream.append("function goConfirm" + _serviceName + "(url, alertFlag) {\n");

		// GG 1/10/2004 - il corpo della funzione è stato spostato nella
		// funzione "goConfirmGenericCustomTL()" nel file customTL.js
		_htmlStream.append("  goConfirmGenericCustomTL(url, alertFlag);  // in customTL.js\n");

		_htmlStream.append("}\n");

		_htmlStream.append("function gruppoLavoratori(url) {\n");
		_htmlStream.append("var f=\"PAGE=PGruppoLav_Popup&\";\n");
		_htmlStream.append(
				"var feat=\"height=300,width=300,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,titlebar=no\";\n");
		_htmlStream.append("var _url=\"AdapterHTTP?\" + f + url;\n");
		_htmlStream.append("window.open(_url,\"gruppolav\",feat);\n");
		_htmlStream.append("}\n");

		_htmlStream.append("function gruppoOperatori(url) {\n");
		_htmlStream.append("var f=\"PAGE=PGruppoOpe_Popup&\";\n");
		_htmlStream.append(
				"var feat=\"height=300,width=300,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,titlebar=no\";\n");
		_htmlStream.append("var _url=\"AdapterHTTP?\" + f + url;\n");
		_htmlStream.append("window.open(_url,\"gruppolav\",feat);\n");
		_htmlStream.append("}\n");

		_htmlStream.append("// -->\n");
		_htmlStream.append("</SCRIPT>\n");
	} // private void makeJavaScript()

	// Crea l'header della tabella con i nomi delle colonne
	private void makeColumns() throws JspException {
		_htmlStream.append("<TR>\n");
		_htmlStream.append("<TH class=\"ag_lista\">&nbsp;</TH>\n");

		_columns = _layout.getAttributeAsVector("COLUMNS.COLUMN");
		if ((_columns == null) || (_columns.size() == 0)) {
			_logger.fatal("AppuntamentitListTag::makeColumns:: nomi delle colonne non definiti");

			throw new JspException("Nomi delle colonne non definiti");
		} // if ((_columns == null) || (_columns.size() == 0))

		for (int i = 0; i < _columns.size(); i++) {
			String nomeColonna = (String) ((SourceBean) _columns.elementAt(i)).getAttribute("LABEL");
			String nomeColonna2 = null;
			if (((SourceBean) _columns.elementAt(i)).getAttribute("LABEL2") != null) {
				nomeColonna2 = (String) ((SourceBean) _columns.elementAt(i)).getAttribute("LABEL2");
			}
			if (configLabelServizio != null && configLabelServizio.equalsIgnoreCase("1") && nomeColonna2 != null) {
				nomeColonna = nomeColonna2;
			}

			_htmlStream.append("<TH class=\"ag_lista\">&nbsp;" + nomeColonna + "&nbsp;</TH>\n");
		} // for (int i = 0; i < _columns.size(); i++)
		_htmlStream.append("</TR>\n");
	} // private void makeColumns() throws JspException

	private void makeRows(boolean canDelete) throws JspException {
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean selectCaption = (SourceBean) _layout.getAttribute("CAPTIONS.SELECT_CAPTION");
		SourceBean deleteCaption = (SourceBean) _layout.getAttribute("CAPTIONS.DELETE_CAPTION");
		Vector genericCaption = _layout.getAttributeAsVector("CAPTIONS.CAPTION");
		Vector rows = _content.getAttributeAsVector("PAGED_LIST.ROWS.ROW");
		String cdnTipoErr = "";
		int tipoDel = 0;

		for (int i = 0; i < rows.size(); i++) {
			boolean viewCapt = true;
			SourceBean row = (SourceBean) rows.elementAt(i);
			_htmlStream.append("<TR valign=\"top\">\n");
			_htmlStream.append("<TD class=\"ag_lista\">\n");
			_htmlStream.append("<TABLE border=0><TR>\n");
			if (selectCaption != null) {
				String labelSelect = (String) selectCaption.getAttribute("LABEL");
				if ((labelSelect == null) || (labelSelect.length() == 0))
					labelSelect = "Selezionare un dettaglio";

				String imageSelect = (String) selectCaption.getAttribute("IMAGE");
				if ((imageSelect == null) || (imageSelect.length() == 0))
					imageSelect = "../../img/detail.gif";
				if (row.containsAttribute("APP_ERR")) {
					cdnTipoErr = row.getAttribute("APP_ERR").toString();
					if ((cdnTipoErr != null) && !cdnTipoErr.equals("")) {
						if (cdnTipoErr.equals("-1")) {
							imageSelect = "../../img/caselle.gif";
						} else {
							imageSelect = "../../img/warning.gif";
						}
					}
				}

				String confirmSelect = (String) selectCaption.getAttribute("CONFIRM");
				if ((confirmSelect == null) || (confirmSelect.length() == 0))
					confirmSelect = "FALSE";

				Vector parameters = selectCaption.getAttributeAsVector("PARAMETER");
				StringBuffer parametersStr = getParametersList(parameters, row);
				parametersStr.append("MESSAGE=" + DelegatedDetailService.DETAIL_SELECT + "&");
				_htmlStream.append("<TD align=\"left\"><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName
						+ "(" + "'" + parametersStr + linkDett + "'" + ", " + "'" + confirmSelect + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageSelect + "\" alt=\""
						+ labelSelect + "\"/></A></TD>\n");
			} // if (selectCaption != null)
			/*
			 * tipoDel = Integer.parseInt(row.getAttribute("TIPO").toString()); if (deleteCaption != null && tipoDel!=4
			 * && canDelete) { String labelDelete = (String) deleteCaption.getAttribute("LABEL"); if ((labelDelete ==
			 * null) || (labelDelete.length() == 0)) labelDelete = "Cancellare una riga";
			 * 
			 * String imageDelete = (String) deleteCaption.getAttribute("IMAGE"); if ((imageDelete == null) ||
			 * (imageDelete.length() == 0)) imageDelete = "../../img/del.gif";
			 * 
			 * String confirmDelete = (String) deleteCaption.getAttribute("CONFIRM"); if ((confirmDelete == null) ||
			 * (confirmDelete.length() == 0)) confirmDelete = "FALSE";
			 * 
			 * Vector parameters = deleteCaption.getAttributeAsVector("PARAMETER"); StringBuffer parametersStr =
			 * getParametersList(parameters, row); parametersStr.append("NAVIGATOR_DISABLED=TRUE&");
			 * //parametersStr.append(_providerURL); parametersStr.append( "MESSAGE=" +
			 * DelegatedBasicListService.LIST_DELETE + "&LIST_PAGE=" + _content.getAttribute("PAGED_LIST.PAGE_NUMBER"));
			 * 
			 * _htmlStream.append( "<TD align=\"left\"><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName +
			 * "(" + "'" + parametersStr + linkDett + "'" + ", " + "'" + confirmDelete + "'" +
			 * "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageDelete + "\" alt=\" " +
			 * labelDelete + "\"/ ></A></TD>\n"); }
			 */// if (deleteCaption != null)
			/*
			 * for (int j = 0; j < genericCaption.size(); j++) { SourceBean caption = (SourceBean)
			 * genericCaption.elementAt(j); String labelCaption = (String) caption.getAttribute("LABEL"); if
			 * ((labelCaption == null) || (labelCaption.length() == 0)) labelCaption = "";
			 * 
			 * String imageCaption = (String) caption.getAttribute("IMAGE"); if ((imageCaption == null) ||
			 * (imageCaption.length() == 0)) imageCaption = "";
			 * 
			 * String confirmCaption = (String) caption.getAttribute("CONFIRM"); if ((confirmCaption == null) ||
			 * (confirmCaption.length() == 0)) confirmCaption = "FALSE";
			 * 
			 * Vector parameters = caption.getAttributeAsVector("PARAMETER"); StringBuffer parametersStr =
			 * getParametersList(parameters, row);
			 * 
			 * String onClickCaption = (String) caption.getAttribute("ONCLICK"); if ((onClickCaption == null) ||
			 * (onClickCaption.length() == 0)) { onClickCaption = "goConfirm" + _serviceName; }
			 * 
			 * if(onClickCaption.equals("conferma_del_slot")) { // sono negli slot e controllo il canDelete
			 * if(canDelete) { _htmlStream.append( "<TD align=\"left\"><A href=\"javascript://\" onclick=\"" +
			 * onClickCaption + "(" + "'" + parametersStr + linkDett + "'" + ", " + "'" + confirmCaption + "'" +
			 * ");return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageCaption + "\" alt=\"" +
			 * labelCaption + "\"/></A></TD>\n"); } } else { _htmlStream.append( "<TD align=\"left\"><A
			 * href=\"javascript://\" onclick=\"" + onClickCaption + "(" + "'" + parametersStr + linkDett + "'" + ", " +
			 * "'" + confirmCaption + "'" + ");return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" +
			 * imageCaption + "\" alt=\"" + labelCaption + "\"/></A></TD>\n"); } }
			 */// for (int i = 0; i < genericCaption.size(); i++)
			_htmlStream.append("</TR></TABLE>\n");
			_htmlStream.append("</TD>\n");
			String imgGruppo = "<img src=\"../../img/omini.gif\" border=\"0\" alt=\"+\">";
			String imgSingolo = "<img src=\"../../img/omino.gif\" border=\"0\" alt=\"\">";
			String imgAzienda = "<img src=\"../../img/azienda.gif\" border=\"0\" alt=\"\">";
			String imgAziLav = "<img src=\"../../img/azi-lav.gif\" border=\"0\" alt=\"+\">";
			String ico = "";
			String ico2 = "";
			String linkGruppo = "";
			for (int j = 0; j < _columns.size(); j++) {
				String nomeColonna = (String) ((SourceBean) _columns.elementAt(j)).getAttribute("NAME");
				Object fieldObject = row.getAttribute(nomeColonna);
				String field = null;
				if (nomeColonna.toUpperCase().equals("LAV_AZ")) {
					if (row.containsAttribute("TIPO")) {
						int tipo = Integer.parseInt(row.getAttribute("TIPO").toString());
						switch (tipo) {
						case 1:
							// E' un lavoratore oppure non ci sono nè lavoratori
							// nè Azienda
							if (Integer.parseInt(row.getAttribute("NRO").toString()) > 1) {
								// Ci sono n lavoratori
								String modulePopup = "MGruppoLav_Popup";
								SourceBean popupBean = (SourceBean) configure
										.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME", modulePopup);
								SourceBean gruppoLayout = (SourceBean) popupBean.getAttribute("CONFIG");
								SourceBean gruppoPar = (SourceBean) gruppoLayout.getAttribute("QUERIES.QUERY");
								Vector parameters = gruppoPar.getAttributeAsVector("PARAMETER");
								StringBuffer parametersStr = getPopupParameters(parameters, row);
								linkGruppo = "<a href=\"javascript://\" onclick=\"gruppoLavoratori('" + parametersStr
										+ "');return false;\">";
								linkGruppo += imgGruppo + "</a>&nbsp;&nbsp;&nbsp;";
								ico = linkGruppo;
								ico2 = "";
							} else {
								// Solo 1 lavoratore
								if (Integer.parseInt(row.getAttribute("NRO").toString()) == 1) {
									ico = imgSingolo + "&nbsp;&nbsp;&nbsp;";
									ico2 = "";
									linkGruppo = "";
								} else {
									ico = "&nbsp;&nbsp;";
									ico2 = "&nbsp;&nbsp;";
									linkGruppo = "";
								}
							}
							break;
						case 2:
							// E' un'azienda
							ico = imgAzienda + "&nbsp;&nbsp;&nbsp;";
							ico2 = "";
							linkGruppo = "";
							break;
						case 3:
							// Azienda + Lavoratori
							String modulePopup = "MGruppoLav_Popup";
							SourceBean popupBean = (SourceBean) configure
									.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME", modulePopup);
							SourceBean gruppoLayout = (SourceBean) popupBean.getAttribute("CONFIG");
							SourceBean gruppoPar = (SourceBean) gruppoLayout.getAttribute("QUERIES.QUERY");
							Vector parameters = gruppoPar.getAttributeAsVector("PARAMETER");
							StringBuffer parametersStr = getPopupParameters(parameters, row);
							linkGruppo = "<a href=\"javascript://\" onclick=\"gruppoLavoratori('" + parametersStr
									+ "');return false;\">";
							linkGruppo += imgAziLav + "</a>&nbsp;&nbsp;&nbsp;";
							/*
							 * ico2 = linkGruppo; ico = imgAzienda + "&nbsp;&nbsp;&nbsp;";
							 */
							ico = linkGruppo;
							ico2 = "";
							break;
						case 4:
							// Slot
							ico = "";
							ico2 = "";
							linkGruppo = "";
							break;
						} // end switch
					} // end if(row.containsAttribute("TIPO"))...
				} // end if(nomeColonna.toUpperCase().equals("LAV_AZ"))...

				if (nomeColonna.toUpperCase().equals("OPERATORE")) {
					if (Integer.parseInt(row.getAttribute("NRO_OPE").toString()) > 1) {
						// Ci sono n operatori
						String modulePopup = "MGruppoOpe_Popup";
						SourceBean popupBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE",
								"NAME", modulePopup);
						SourceBean gruppoLayout = (SourceBean) popupBean.getAttribute("CONFIG");
						SourceBean gruppoPar = (SourceBean) gruppoLayout.getAttribute("QUERIES.QUERY");
						Vector parameters = gruppoPar.getAttributeAsVector("PARAMETER");
						StringBuffer parametersStr = getPopupParameters(parameters, row);
						linkGruppo = "<a href=\"javascript://\" onclick=\"gruppoOperatori('" + parametersStr
								+ "');return false;\">";
						linkGruppo += imgGruppo + "</a>&nbsp;&nbsp;&nbsp;";
						ico = linkGruppo;
						ico2 = "";
					} else {
						// Solo 1 operatore
						if (Integer.parseInt(row.getAttribute("NRO_OPE").toString()) == 1) {
							ico = imgSingolo + "&nbsp;&nbsp;&nbsp;";
							ico2 = "";
							linkGruppo = "";
						} else {
							ico = "&nbsp;&nbsp;";
							ico2 = "&nbsp;&nbsp;";
							linkGruppo = "";
						}
					}
				} else {
					if (!nomeColonna.toUpperCase().equals("LAV_AZ")) {
						ico = "";
						ico2 = "";
						linkGruppo = "";
					}
				} // end if(nomeColonna.toUpperCase().equals("OPERATORE"))...

				if (fieldObject != null) {
					field = fieldObject.toString();
				} else {
					field = "&nbsp;";
				}
				if ((field.equals("")) || (field == null) || (field.equals(" "))) {
					field = "&nbsp;";
				}

				if (nomeColonna.toUpperCase().equals("LAV_AZ")) {
					if (Integer.parseInt(row.getAttribute("NRO").toString()) == 0) {
						field = "&nbsp;";
					}
					if (Integer.parseInt(row.getAttribute("TIPO").toString()) == 3) {
						field += "&nbsp;" + ico2;
					}
					_htmlStream.append("<TD class=\"ag_lista\"> " + ico + field + "</TD>\n");
				} else {
					_htmlStream.append("<TD class=\"ag_lista\"> " + linkGruppo + field + "</TD>\n");
				}
			} // for (int j = 0; j < _columns.size(); j++)

			tipoDel = Integer.parseInt(row.getAttribute("TIPO").toString());
			viewCapt = viewCaptionButton(deleteCaption, row);
			if (deleteCaption != null && tipoDel != 4 && canDelete) {
				if (viewCapt) {
					String labelDelete = (String) deleteCaption.getAttribute("LABEL");
					if ((labelDelete == null) || (labelDelete.length() == 0))
						labelDelete = "Cancellare una riga";

					String imageDelete = (String) deleteCaption.getAttribute("IMAGE");
					if ((imageDelete == null) || (imageDelete.length() == 0))
						imageDelete = "../../img/del.gif";

					String confirmDelete = (String) deleteCaption.getAttribute("CONFIRM");
					if ((confirmDelete == null) || (confirmDelete.length() == 0))
						confirmDelete = "FALSE";

					Vector parameters = deleteCaption.getAttributeAsVector("PARAMETER");
					StringBuffer parametersStr = getParametersList(parameters, row);
					parametersStr.append("NAVIGATOR_DISABLED=TRUE&");
					// parametersStr.append(_providerURL);
					parametersStr.append("MESSAGE=" + DelegatedBasicListService.LIST_DELETE + "&LIST_PAGE="
							+ _content.getAttribute("PAGED_LIST.PAGE_NUMBER"));

					_htmlStream.append("<TD align=\"left\"><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName
							+ "(" + "'" + parametersStr + linkDett + "'" + ", " + "'" + confirmDelete + "'"
							+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageDelete
							+ "\" alt=\" " + labelDelete + "\"/ ></A></TD>\n");
				}
			} // if (deleteCaption != null)
			for (int j = 0; j < genericCaption.size(); j++) {
				SourceBean caption = (SourceBean) genericCaption.elementAt(j);
				String labelCaption = (String) caption.getAttribute("LABEL");
				if ((labelCaption == null) || (labelCaption.length() == 0))
					labelCaption = "";

				String imageCaption = (String) caption.getAttribute("IMAGE");
				if ((imageCaption == null) || (imageCaption.length() == 0))
					imageCaption = "";

				String confirmCaption = (String) caption.getAttribute("CONFIRM");
				if ((confirmCaption == null) || (confirmCaption.length() == 0))
					confirmCaption = "FALSE";

				Vector parameters = caption.getAttributeAsVector("PARAMETER");
				StringBuffer parametersStr = getParametersList(parameters, row);

				String onClickCaption = (String) caption.getAttribute("ONCLICK");
				if ((onClickCaption == null) || (onClickCaption.length() == 0)) {
					onClickCaption = "goConfirm" + _serviceName;
				}

				if (onClickCaption.equals("conferma_del_slot")) {
					// sono negli slot e controllo il canDelete
					if (canDelete) {
						_htmlStream.append("<TD align=\"left\"><A href=\"javascript://\" onclick=\"" + onClickCaption
								+ "(" + "'" + parametersStr + linkDett + "'" + ", " + "'" + confirmCaption + "'"
								+ ");return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageCaption
								+ "\" alt=\"" + labelCaption + "\"/></A></TD>\n");
					}
				} else {
					_htmlStream.append("<TD align=\"left\"><A href=\"javascript://\" onclick=\"" + onClickCaption + "("
							+ "'" + parametersStr + linkDett + "'" + ", " + "'" + confirmCaption + "'"
							+ ");return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageCaption
							+ "\" alt=\"" + labelCaption + "\"/></A></TD>\n");
				}
			} // for (int i = 0; i < genericCaption.size(); i++)

			_htmlStream.append("</TR>\n");
		} // for (int i = 0; i < rows.size(); i++)
			// _htmlStream.append("</TABLE>\n");
	} // private void makeRows()

	private void makeButton() throws JspException {
		// ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean insertButton = (SourceBean) _layout.getAttribute("BUTTONS.INSERT_BUTTON");
		Vector genericButton = _layout.getAttributeAsVector("BUTTONS.BUTTON");
		_htmlStream.append("<TR class=\"lista\">\n");
		_htmlStream.append("<TD>\n");
		_htmlStream.append("<TABLE border=0 cellpadding=0 cellspacing=0><TR>\n");
		_htmlStream.append("<TR><TD><BR></TD><TD><BR></TD></TR>\n");
		_htmlStream.append("<TABLE width=\"90%\" align=\"center\"><TR>\n");
		if (insertButton != null) {
			String labelInsert = (String) insertButton.getAttribute("LABEL");
			if ((labelInsert == null) || (labelInsert.length() == 0))
				labelInsert = "NUOVO";
			String imageInsert = (String) insertButton.getAttribute("IMAGE");
			String confirmInsert = (String) insertButton.getAttribute("CONFIRM");
			if ((confirmInsert == null) || (confirmInsert.length() == 0))
				confirmInsert = "FALSE";
			Vector parameters = insertButton.getAttributeAsVector("PARAMETER");
			StringBuffer parametersStr = getParametersList(parameters, null);
			parametersStr.append("MESSAGE=" + DelegatedDetailService.DETAIL_NEW + "&");
			if ((imageInsert != null) && (imageInsert.length() > 0))
				_htmlStream.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName
						+ "(" + "'" + parametersStr + "'" + ", " + "'" + confirmInsert + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageInsert + "\" alt=\""
						+ labelInsert + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD align=\"center\"><INPUT type=\"Button\" class=\"ButtonChangePage\" value=\""
						+ labelInsert + "\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr + "'" + ", "
						+ "'" + confirmInsert + "'" + "); return false\"></TD>\n");
		} // if(insertButton != null )
		for (int i = 0; i < genericButton.size(); i++) {
			SourceBean button = (SourceBean) genericButton.elementAt(i);
			String labelButton = (String) button.getAttribute("LABEL");
			if ((labelButton == null) || (labelButton.length() == 0))
				labelButton = "BOTTONE";

			String imageButton = (String) button.getAttribute("IMAGE");

			String confirmButton = (String) button.getAttribute("CONFIRM");
			if ((confirmButton == null) || (confirmButton.length() == 0))
				confirmButton = "FALSE";

			Vector parameters = button.getAttributeAsVector("PARAMETER");
			StringBuffer parametersStr = getParametersList(parameters, null);

			if ((imageButton != null) && (imageButton.length() > 0))
				_htmlStream.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName
						+ "(" + "'" + parametersStr + "'" + ", " + "'" + confirmButton + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageButton + "\" alt=\""
						+ labelButton + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD align=\"center\"><INPUT type=\"Button\" class=\"ButtonChangePage\" value=\""
						+ labelButton + "\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr + "'" + ", "
						+ "'" + confirmButton + "'" + "); return false\"></TD>\n");
		} // for (int i = 0; i < genericButton.size(); i++)
		_htmlStream.append("</TR></TABLE>\n");
		_htmlStream.append("</TD>\n");
		_htmlStream.append("</TR>\n");
	} // private void makeButton() throws JspException

	private StringBuffer getParametersList(Vector parameters, SourceBean row) throws JspException {
		StringBuffer parametersList = new StringBuffer();
		for (int i = 0; i < parameters.size(); i++) {
			String name = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("NAME");
			String type = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("TYPE");
			String value = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("VALUE");
			String scope = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("SCOPE");
			if (name != null) {
				parametersList.append(JavaScript.escape(name.toUpperCase()) + "=");
				if ((type != null) && type.equalsIgnoreCase("RELATIVE")) {
					if ((scope != null) && scope.equalsIgnoreCase("LOCAL")) {
						if (row == null) {
							_logger.fatal(
									"AppuntamentiListTag::getParametersList: Non è possibile associare a questo bottone lo scope LOCAL");

							throw new JspException("Non è possibile associare a questo bottone lo scope LOCAL");
						} // if (row == null)
						Object valueObject = row.getAttribute(value);
						if (valueObject != null)
							value = valueObject.toString();
					} // if ((scope != null) &&
						// scope.equalsIgnoreCase("LOCAL"))
					else
						value = (String) ContextScooping.getScopedParameter(_requestContainer, _responseContainer,
								value, scope);
				} // if ((type != null) && type.equalsIgnoreCase("RELATIVE"))
				if (value == null)
					value = "";
				parametersList.append(JavaScript.escape(value) + "&");
			} // if (name != null)
		} // for (int i = 0; i < parameters.size(); i++)
		return parametersList;
	} // private StringBuffer getParametersList(Vector parameters, SourceBean
		// row) throws JspException

	private String getQueryString() {
		StringBuffer queryStringBuffer = new StringBuffer();
		Vector queryParameters = _serviceRequest.getContainedAttributes();
		for (int i = 0; i < queryParameters.size(); i++) {
			SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
			String parameterKey = parameter.getKey();
			if (parameterKey.equalsIgnoreCase("ACTION_NAME") || parameterKey.equalsIgnoreCase("PAGE")
					|| parameterKey.equalsIgnoreCase("MODULE") || parameterKey.equalsIgnoreCase("MESSAGE")
					|| parameterKey.equalsIgnoreCase("LIST_PAGE") || parameterKey.equalsIgnoreCase("LIST_NOCACHE"))
				continue;
			String parameterValue = parameter.getValue().toString();
			queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
			queryStringBuffer.append("=");
			queryStringBuffer.append(JavaScript.escape(parameterValue));
			queryStringBuffer.append("&");
		} // for (int i = 0; i < queryParameters.size(); i++)
		return queryStringBuffer.toString();
	} // private String getQueryString()

	private StringBuffer getPopupParameters(Vector parameters, SourceBean row) throws JspException {
		StringBuffer parametersList = new StringBuffer();
		for (int i = 0; i < parameters.size(); i++) {
			String type = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("TYPE");
			String name = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("VALUE");
			String scope = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("SCOPE");
			String value = "";
			if (name != null) {
				parametersList.append(JavaScript.escape(name.toUpperCase()) + "=");
				if ((type != null) && type.equalsIgnoreCase("RELATIVE")) {
					if (row == null) {
						_logger.fatal(
								"AppuntamentiListTag::getPopupParameters: Non è possibile associare a questo bottone lo scope LOCAL");

						throw new JspException("Non è possibile associare a questo bottone lo scope LOCAL");
					} // if (row == null)
					Object valueObject = row.getAttribute(name);
					if (valueObject != null)
						value = valueObject.toString();
					else
						value = (String) ContextScooping.getScopedParameter(_requestContainer, _responseContainer,
								value, scope);
				} // if ((type != null) && type.equalsIgnoreCase("RELATIVE"))
				if (value == null) {
					value = "";
				}
				parametersList.append(JavaScript.escape(value) + "&");
			} // if (name != null)
		} // for (int i = 0; i < parameters.size(); i++)
		return parametersList;
	} // private StringBuffer getPopupParameters(Vector parameters, SourceBean
		// row) throws JspException

	public void setActionName(String actionName) {
		_logger.debug("DefaultDetailTag::setActionName:: actionName [" + actionName + "]");

		_actionName = actionName;
	} // public void setActionName(String actionName)

	public void setModuleName(String moduleName) {
		_logger.debug("AppuntamentiListTag::setModuleName:: moduleName [" + moduleName + "]");

		_moduleName = moduleName;
	} // public void setModuleName(String moduleName)

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		_logger.debug("AppuntamentiListTag::doEndTag:: invocato");

		_actionName = null;
		_moduleName = null;
		_serviceName = null;
		_content = null;
		_layout = null;
		_providerURL = null;
		_requestContainer = null;
		_serviceRequest = null;
		_responseContainer = null;
		_serviceResponse = null;
		_htmlStream = null;
		_columns = null;
		return super.doEndTag();
	} // public int doEndTag() throws JspException

	// Marianna Borriello: aggiunto hiddenColumn 21/01/2020
	protected boolean viewCaptionButton(SourceBean caption, SourceBean row) {
		String hiddenValue = "";
		String hiddenColumn = "";
		if (caption != null) {
			hiddenColumn = StringUtils.getAttributeStrNotNull(caption, "hiddenColumn");
		}
		if (!hiddenColumn.equals("")) {
			hiddenValue = StringUtils.getAttributeStrNotNull(row, hiddenColumn);
		}
		if (hiddenValue.equals("0")) {
			return (false);
		} else {
			return (true);
		}
	}

}