package it.eng.sil.util;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.security.PageAttribs;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

/**
 * Classe che implementa la generazione del codice HTML per la visualizzazione
 * delle informazioni correnti inerenti l'azienda. Il codice HTML generato ha un
 * link "Inf. correnti" ad una pagina che reperisce una serie di informazioni
 * correnti sulla azienda. Tale pagina è cablata nel codice ma può anche essere
 * impostata tremite il metodo "setInfoCorrentiPage(String)". La pagina può
 * essere aperta in pop-up (default) o nella stessa finestra settando la
 * variabile popUP = false tramite il metodo "openInPopUp(false)"
 * 
 * 
 * Esempio:
 * 
 * Inizializza l'oggetto InfCorrentiAzienda testata= new
 * InfCorrentiAzienda(prgAzienda,prgUnita);
 * 
 * Inserire il codice seguente nel punto in cui si vuole che siano generate le
 * informazioni <% testata.show(out); %>
 * 
 * Risultato HTML: "Ragione Sociale Antico Ristorante da Pippo CF 12345678972
 * P.IVA 23453245324 Indirizzo Via Clock, 1, Ferrara "
 * 
 * E' anche possibile estrarre i singoli valori con i metodi
 * "getRagioneSociale()", "getCodiceFiscale()", "getPIva()", "getIndirizzo()" in
 * modo da avere codice come questo:
 * <p>
 * Ragione Sociale: <%= testata.getRagioneSociale() %>
 * </p>
 * 
 * @author Cristian Mudadu
 */
public class InfCorrentiAzienda {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfCorrentiAzienda.class.getName());

	private String paginaLista = "IdoListaRichiestePage";

	private SessionContainer session;

	private boolean skipLista;

	/**
	 * Valori possibili per i prg: un numero valido o null
	 */
	Object prgAzienda = null;
	Object prgUnita = null;
	Object prgRichiesta = null;
	//
	String annoRichiesta = "";
	BigDecimal numStorico = null;

	String strModEvasione = "";
	String strStatoEvasione = "";

	String ragioneSociale = "&nbsp;&nbsp;";
	String codiceFiscale = "&nbsp;&nbsp;";
	String pIva = "&nbsp;&nbsp;";
	String indirizzo = "&nbsp;&nbsp;";
	String comune = "&nbsp;&nbsp;";
	// Campi sotto aggiunti da Paolo Roccetti per il recupero dei dati nel
	// dettaglio generale del movimento
	String codiceCCNL = "";
	String descrCCNL = "";
	String tipoAz = "";
	String telAz = "";
	String faxAz = "";
	String capAz = "";
	String descrTipoAz = "";
	String numAlboInter = "";
	String numRegComm = "";
	String codAtecoAz = "";
	String descrAtecoAz = "";
	String descrNatGiurAz = "";
	String codNatGiurAz = "";
	String strFlgDatiOk = "";

	String infoCorrentiPage = "AmstrInfoCorrentiPage";

	private boolean existInfo = true;
	private int maxLenStatoOcc = 50;
	private PageAttribs pageAttribs;
	private List sezioni;

	// flag che indica se il link verrà aperto con una popUp
	public static boolean PopUp_NO = false;
	public static boolean PopUp_YES = true;

	// Var destinate ad usi futuri quando verra implementata la profilatura; i
	// nomi sono puramente inventati
	public static int LAV_GENERICO = 1;
	public static int LAV_METAL = 2;

	/**
	 * 
	 * @param prgAzienda
	 * @param prgUnita
	 * @param prgRichiesta
	 */
	public InfCorrentiAzienda(Object prgAzienda, Object prgUnita, Object prgRichiesta) {
		this(null, prgAzienda, prgUnita, prgRichiesta);
	}

	/**
	 * @param session
	 * @param prgAzienda
	 * @param prgUnita
	 * @param prgRichiesta
	 */
	public InfCorrentiAzienda(SessionContainer session, Object prgAzienda, Object prgUnita, Object prgRichiesta) {
		this.prgAzienda = getStrNumberNull(prgAzienda);
		this.prgUnita = getStrNumberNull(prgUnita);
		this.prgRichiesta = getStrNumberNull(prgRichiesta);
		this.session = session;
		init();
	}

	/**
	 * @DEPRECATED
	 * @param prgAzienda
	 * @param prgUnita
	 */
	public InfCorrentiAzienda(SessionContainer session, Object prgAzienda, Object prgUnita) {

		this(session, prgAzienda, prgUnita, null);
	}

	/**
	 * @DEPRECATED
	 * @param prgAzienda
	 * @param prgUnita
	 */
	public InfCorrentiAzienda(Object prgAzienda, Object prgUnita) {
		this(null, prgAzienda, prgUnita, null);
	}

	/**
	 * Costruttore.
	 * 
	 * @param prgAzienda
	 * @param prgUnita
	 */
	private void init() {
		// this.prgAzienda = prgAzienda;
		// this.prgUnita = prgUnita;

		SourceBean datiAzienda = null;
		if (prgAzienda != null && prgUnita != null)
			datiAzienda = getInfoAzienda();

		if (datiAzienda != null) {
			ragioneSociale = getMyAttrib(datiAzienda, "ragioneSociale", ragioneSociale);
			codiceFiscale = getMyAttrib(datiAzienda, "codiceFiscale", codiceFiscale);
			pIva = getMyAttrib(datiAzienda, "pIva", pIva);
			indirizzo = getMyAttrib(datiAzienda, "indirizzo", indirizzo);
			comune = getMyAttrib(datiAzienda, "denominazione", comune);

			// Campi sotto aggiunti da Paolo Roccetti per il recupero dei dati
			// nel dettaglio generale del movimento
			codiceCCNL = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "CCNLAZ");
			descrCCNL = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "DESCRCCNL");
			tipoAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "CODTIPOAZ");
			telAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "STRTELAZ");
			faxAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "STRFAXAZ");
			capAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "STRCAPAZ");
			descrTipoAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "DESCRTIPOAZ");
			numAlboInter = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "STRNUMALBOINT");
			numRegComm = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "STRNUMREGCOMM");
			codAtecoAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "CODATECO");
			descrAtecoAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "DESCRATECO");
			descrNatGiurAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "DESCRNATGIURAZ");
			codNatGiurAz = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "CODNATGIURIDICA");
			strFlgDatiOk = SourceBeanUtils.getAttrStrNotNull(datiAzienda, "FLGDATIOK");

		} else {
			this.existInfo = false;
		}
		// nel caso in cui sia stato passato un valore non nullo ma vuoto si
		// evita di generare un errore nei log
		if (prgRichiesta != null) {
			getInfoRichiesta();
			getInfoEvasione();
		}

	} // InfCorrentiLav(Object cdnLavotarore)

	private String getMyAttrib(SourceBean sb, String attrib, String defaultValue) {
		String value = SourceBeanUtils.getAttrStr(sb, attrib, defaultValue);
		return StringUtils.notNull(value);
	}

	/**
	 * Assegna il nome alla Page apribile dal link, solo nel caso in cui si
	 * voglia utilizzare una page alternativa a quella impostata di default.
	 * 
	 * @param nomeDettaglioAziendaPage
	 */
	public void setInfoCorrentiPage(String infoCorrentiPage) {
		this.infoCorrentiPage = infoCorrentiPage;
	}

	/**
	 * Costruisce lo script che permette di aprire le informazioni in popUP
	 * 
	 * @param codLavoratore
	 */
	public String addScript(Object codLavoratore) {
		StringBuffer sb = new StringBuffer();
		sb.append("<script language=\"JavaScript\">\r\n ");
		sb.append(" function openPopUP() {\r\n ");
		sb.append("   var opened = window.open (\"AdapterHTTP?PAGE=");
		sb.append(this.infoCorrentiPage);
		sb.append("&prgAzienda=");
		sb.append(prgAzienda);
		sb.append("&prgUnita=");
		sb.append(prgUnita);
		sb
				.append("\", \"InfoCorrenti\", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');\r\n ");
		sb.append("   opened.focus();\r\n ");
		sb.append(" }\r\n ");
		sb.append("</script>\r\n");
		return sb.toString();
	}

	/**
	 * Genera il codice HTML basato sui parametri assegnati all'oggetto.
	 * 
	 * @param out
	 *            Writer della pagina JSP
	 * @throws IOException
	 */
	public void show(JspWriter out) throws IOException {
		this.show(out, true);
	} // show(_)

	/**
	 * Genera il codice HTML basato sui parametri assegnati all'oggetto.
	 * 
	 * @param out
	 *            Writer della pagina JSP
	 * @param popUp
	 *            specifica se la pagina deve essere aperta in pop-up
	 * @throws IOException
	 */
	public void show(JspWriter out, boolean popUp) throws IOException {
		/*
		 * String stOccRag = null; String strTMP = ""; int c = -2; int MAXlength =
		 * this.maxLenStatoOcc;
		 */
		if (this.existInfo) {
			// controllo che la stringa statoOccRagg non superi un certo num di
			// caratteri per far si che sia stampata
			// in un'unica linea. In caso contrario tronco e aggiungo dei
			// puntini
			// if(statoOccRagg.length() > MAXlength) {stOccRag =
			// this.statoOccRagg.substring(0,MAXlength-1)+"...";}
			// else {stOccRag = this.statoOccRagg;}

			// sostituisco lo spazio con dei &nbsp; per evitare che nel
			// ridimensionamento della finestra venga spezzata la stringa
			/*
			 * c = stOccRag.indexOf(" "); while(c > -1){ strTMP +=
			 * stOccRag.substring(0,c)+"&nbsp;"; stOccRag =
			 * stOccRag.substring(c+1); c = stOccRag.indexOf(" "); }//while
			 * strTMP += stOccRag; stOccRag = strTMP;
			 */
			// se necessario inserisco lo script per aprire la finestra in
			// pop-up
			// if(popUp == true) { out.println(addScript(cdnLavotarore)); }
			// Stondature - Stefania Orioli - 10/03/2004 - Elemento TOP
			out
					.println("<br><table maxwidth=\"96%\" width=\"96%\" align=\"center\" margin=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr><td class=\"info\" valign=\"top\" align=\"left\" width=\"6\" height=\"6px\">");
			out.println("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
			out.println("<td class=\"info\" height=\"6px\">&nbsp;</td>");
			if (!skipLista) {
				if (session != null) {
					String s = formatBackList(session, paginaLista);
					if (s != null)
						out.println("<td rowspan=\"3\" class=\"info\" height=\"6px\">" + s + "</td>");
					/*
					 * String token = "_TOKEN_" + paginaLista; //
					 * IdoListaRichiestePage String urlDiLista = (String)
					 * session.getAttribute(token.toUpperCase()); if (urlDiLista !=
					 * null) {
					 * 
					 * out.println( "<td rowspan=\"3\" class=\"info\" height=\"6px\"><a
					 * href=\"" + "#\"" + " onClick=\"goTo('" + urlDiLista +"')" +
					 * "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></td>"); }
					 */
				}
			}
			out.println("<td class=\"info\" valign=\"top\" align=\"right\" width=\"6\" height=\"6px\">");
			out.println("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\"></td></tr>");
			out.println("<tr><td class=\"info\" width=\"6\">&nbsp;</td>");
			out.println("<td class=\"info\" align=\"center\">");

			out.println("<table width='100%' border=0>");

			// GG 14/12/2004 - aggiunta qualche info in più
			out.println("<tr>");
			out.println("<td>Codice fiscale <STRONG>" + codiceFiscale + "</STRONG></td>");
			out.println("<td>Partita IVA <STRONG>" + pIva + "</STRONG></td>");
			out.println("</tr>");

			out.println("<tr>");

			out.println("<script language=\"JavaScript\">");
			out.println(" function openRich_PopUP(prgRich) {");
			out
					.println("   var opened = window.open(\"AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=\" +prgRich +\"&POPUP=1\", \"DettaglioSintetico\", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES')");
			out.println("   opened.focus();");
			out.println(" }");
			out.println("</script>");

			String strDettaglio = "";

			if (prgRichiesta != null) {
				strDettaglio = "<a class=\"info_lav\" href=\"#\" onClick=\"openRich_PopUP('" + prgRichiesta
						+ "')\"><img src=\"../../img/info_soggetto.gif\" alt=\"Dettaglio\"/></a>&nbsp;";
			}
			out.println("<td>" + strDettaglio + "Ragione Sociale <STRONG>" + ragioneSociale + "</STRONG></td>");

			// if (annoRichiesta!=null && !annoRichiesta.equals("")) {
			// out.println("<td>Numero richiesta <STRONG>" + annoRichiesta +
			// "</td>");
			// }
			// out.println("</tr>");

			// out.println("<tr>");
			out.println("<td>Indirizzo <STRONG>" + indirizzo + " " + comune + "</STRONG></td>");
			if (annoRichiesta != null && !annoRichiesta.equals("")) {
				out.println("<td>Numero richiesta <STRONG>" + annoRichiesta + "</td>");
			}
			if (numStorico != null) {
				if (numStorico.compareTo(new BigDecimal("0")) > 0) {
					out.println("<td><STRONG>Copia di lavoro per l'incrocio</STRONG></td>");
				}
			}
			out.println("</tr>");

			if (strModEvasione != null && !strModEvasione.equals("") && strStatoEvasione != null
					&& !strStatoEvasione.equals("")) {
				out.println("<tr>");
				out.println("<td>Modalit&agrave; Evasione <STRONG>" + strModEvasione + "</STRONG></td>");
				out.println("<td>Stato Evasione <STRONG>" + strStatoEvasione + "</STRONG></td>");
				out.println("</tr>");
			}
			out.println("</table>");

			/*
			 * if(popUp == true) { out.println("<br/><a class=\"info_lav\"
			 * href=\"#\" onClick=\"openPopUP()\" >Inf.&nbsp;correnti:</a>"); }
			 * else { out.println("<br/><a class=\"info_lav\"
			 * href=\"AdapterHTTP?PAGE="+this.infoCorrentiPage+"&cdnLavoratore="+cdnLavotarore+"\">Inf.
			 * correnti </a>"); }
			 * 
			 * out.println(" Elenco&nbsp;Anag.&nbsp;<STRONG>" +dataInizio+ "</STRONG>
			 * CPI&nbsp;Tit.&nbsp;<STRONG>" +descCPI+ "</STRONG>");
			 * out.println(" Stato&nbsp;occupaz.&nbsp;<STRONG>" +stOccRag+ "</STRONG>");
			 * out.println("<br/>");
			 * 
			 * if(sezioni != null) { String sz = null; Iterator it =
			 * sezioni.iterator(); for(int i=0; it.hasNext(); i++) { sz =
			 * (String) it.next(); if(sz.equalsIgnoreCase("OBB_FORM")) {
			 * out.println(" "+ i +".<STRONG>OBBLIGO FORMATIVO</STRONG>; "); }
			 * else if(sz.equalsIgnoreCase("STATO_OCC")) { out.println(" "+ i
			 * +".<STRONG>STATO OCCUPAZIONALE</STRONG>; "); } else
			 * if(sz.equalsIgnoreCase("LISTE_MOB")) { out.println(" "+ i +".<STRONG>MOBILITA'</STRONG>;
			 * "); } else if(sz.equalsIgnoreCase("INDISP_TEMP")) { out.println(" "+
			 * i +".<STRONG>INDISP. TEMPORANEA</STRONG>; "); } else
			 * if(sz.equalsIgnoreCase("ESP_PROF")) { out.println(" "+ i +".<STRONG>ESPERIENZE
			 * PROFESSIONALI</STRONG>; "); } else
			 * if(sz.equalsIgnoreCase("TITOLI_STU")) { out.println(" "+ i +".<STRONG>TITOLO
			 * DI STUDIO</STRONG>; "); } else
			 * if(sz.equalsIgnoreCase("FORM_PROF")) { out.println(" "+ i +".<STRONG>FORMAZIONE
			 * PROFESSIONALE</STRONG>; "); } else
			 * if(sz.equalsIgnoreCase("PERM_SOGG")) { out.println(" "+ i +".<STRONG>PERMESSO
			 * DI SOGGIORNO</STRONG>; "); } else
			 * if(sz.equalsIgnoreCase("COLL_MIRATO")) { out.println(" "+ i +".<STRONG>COLLOCAMENTO
			 * MIRATO</STRONG>; "); } } //for } //if
			 */
			// out.println("</p>");
			// Stondature - Stefania Orioli - 10/03/2004 - Elemento BOTTOM
			out.println("</td><td class=\"info\" width=\"6\">&nbsp;</td></tr>");
			out.println("<tr valign=\"bottom\">");
			out.println("<td class=\"info\" valign=\"bottom\" align=\"left\" width=\"6\" height=\"6px\">");
			out.println("<img src=\"../../img/angoli/bia4.gif\" width=\"6\" height=\"6\"></td>");
			out.println("<td class=\"info\" height=\"6px\">&nbsp;</td>");
			out.println("<td class=\"info\" valign=\"bottom\" align=\"right\" width=\"6\" height=\"6px\">");
			out.println("<img src=\"../../img/angoli/bia3.gif\" width=\"6\" height=\"6\"></td></tr></table><br>");
		} // if existInfo

	} // show
	
	public void showInfoCodEvasione(JspWriter out, boolean popUp) throws IOException {
	
			if (strModEvasione != null && !strModEvasione.equals("") && strStatoEvasione != null
					&& !strStatoEvasione.equals("")) {
				out.println("<tr valign=\"top\">");
				out.println("<td class=\"etichetta2\">Modalit&agrave; Evasione</td>");
				out.println("<td class=\"campo2\" valign=\"top\"><STRONG>" + strModEvasione + "</STRONG></td>");
				out.println("</tr>");
			}
	

	} // showInfoCodEvasione

	public void showMinimalInfo(JspWriter out) throws IOException {
		this.showInfoCodEvasione(out, true);
	}
	
	/*
	 * //metodo destinato ad usi futuri quando verra implementata la profilatura
	 * public void show(int profilo, JspWriter out) throws IOException {
	 * //codice da implementare... }
	 */

	// vecchi metodi da eliminare
	// ======================================================
	/**
	 * Ritorna un SourceBean con Ragione Sociale, cCodice Fiscale, Partita IVA
	 * ed indirizzo relative all'unita dell'azienda, prelevate attraverso la
	 * query al DB chiamata GET_InfUnitaAzienda
	 * 
	 * @param prgAzienda
	 *            progressivo azienda
	 * @param prgUnita
	 *            progressivo unita
	 * @return SourceBean contenente informazioni relative l'unita e l'azienda
	 */
	private SourceBean getInfoAzienda() {
		SourceBean riga = null;
		// Map result = new HashMap();

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("GET_InfUnitaAzienda");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, prgAzienda));
			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, prgUnita));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");
			if (righe.size() == 1) {
				riga = (SourceBean) righe.get(0);
			} else {
				LogUtils.logError("getInfoAzienda", "L'azienda con codice " + prgAzienda
						+ " non è stata trovata nel DB\n oppure sono state trovate più occorenze", this);
			}
		} // try
		catch (com.engiweb.framework.error.EMFInternalError ex) {
			LogUtils.logError("getInfoAzienda", "Internal Error", ex, this);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		return riga;
	} // getInfoAzienda(_)

	private void getInfoRichiesta() {
		SourceBean riga = null;
		// Map result = new HashMap();

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("GET_InfPrgRichiesta");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, prgRichiesta));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");
			if ((righe != null) && (righe.size() == 1)) {
				riga = (SourceBean) righe.get(0);
				annoRichiesta = (String) riga.getAttribute("annoRichiestaVis");
				if (annoRichiesta == null || annoRichiesta.equals("")) {
					annoRichiesta = (String) riga.getAttribute("AnnoRichiesta");
				}
				numStorico = (BigDecimal) riga.getAttribute("numStorico");

			} else {
				LogUtils.logError("getInfoAzienda", "L'azienda con codice " + prgAzienda
						+ " non è stata trovata nel DB\n oppure sono state trovate più occorenze", this);
			}
		} // try
		catch (com.engiweb.framework.error.EMFInternalError ex) {
			LogUtils.logError("getInfoAzienda", "Internal Error", ex, this);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}

	} // getInfoAzienda(_)

	private void getInfoEvasione() {
		SourceBean riga = null;
		// Map result = new HashMap();

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("GET_InfPrgEvasione");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, prgRichiesta));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");
			if ((righe != null) && (righe.size() == 1)) {
				riga = (SourceBean) righe.get(0);
				strModEvasione = (String) riga.getAttribute("ModEvasione");
				strStatoEvasione = (String) riga.getAttribute("StatoEvasione");
			} else {
				// per capire il motivo di malfunzionamenti stampo tutte le
				// informazione disponibili nel log
				String info = "";
				if (righe.size() > 0) {
					for (int i = 0; i < righe.size(); i++) {
						riga = (SourceBean) righe.get(0);
						strModEvasione = (String) riga.getAttribute("ModEvasione");
						strStatoEvasione = (String) riga.getAttribute("StatoEvasione");
						info += "{ModEvasione=" + strModEvasione + " StatoEvasione=" + strStatoEvasione + "}";
					}
				}
				try {
					info += " " + RequestContainer.getRequestContainer().getServiceRequest().toString();
				} catch (Exception e) {
				}
				_logger.debug("getInfoEvasione(): La richiesta con codice " + prgRichiesta + " ha trovato "
						+ righe.size() + " occorrenze; dati letti:" + info);

				/*
				 * LogUtils.logError( "getInfoEvasione", "La richiesta con
				 * codice " + prgRichiesta + " non è stata trovata nel DB\n
				 * oppure sono state trovate più occorrenze", this);
				 */
			}
		} // try
		catch (com.engiweb.framework.error.EMFInternalError ex) {
			LogUtils.logError("getInfoEvasione", "Internal Error", ex, this);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
	} // getInfoEvasione(_)

	/**
	 * Fornisce una rappresentazione delle proprietà dell'oggetto; utile in fase
	 * di debug.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(this.getClass());
		sb.append(" PrgAzienda [");
		sb.append(this.prgAzienda);
		sb.append("], PrgUnita [");
		sb.append(this.prgUnita);
		sb.append("], RagioneSociale [");
		sb.append(this.ragioneSociale);
		sb.append("]}");

		return sb.toString();
	} // toString()

	/**
	 * 
	 */
	public String getRagioneSociale() {
		return this.ragioneSociale;
	}

	/**
	 * 
	 */
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	/**
	 * 
	 */
	public String getPIva() {
		return this.pIva;
	}

	/**
	 * 
	 */
	public String getIndirizzo() {
		return this.indirizzo;
	}

	/**
	 * 
	 */
	public String getComune() {
		return this.comune;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getCCNL() {
		return this.codiceCCNL;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrCCNL() {
		return this.descrCCNL;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getTipoAz() {
		return this.tipoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getTelAz() {
		return this.telAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getFaxAz() {
		return this.faxAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getCapAz() {
		return this.capAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrTipoAz() {
		return this.descrTipoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getNumAlboInter() {
		return this.numAlboInter;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getNumRegComm() {
		return this.numRegComm;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getCodAtecoAz() {
		return this.codAtecoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrAtecoAz() {
		return this.descrAtecoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrNatGiurAz() {
		return this.descrNatGiurAz;
	}

	/**
	 * @author Giuseppe De Simone
	 */
	public String getCodNatGiurAz() {
		return this.codNatGiurAz;
	}

	/**
	 * @author Giuseppe De Simone
	 */
	public String getFlgDatiOk() {
		return this.strFlgDatiOk;
	}

	/**
	 * @return
	 */
	public Object getPrgRichiesta() {
		return prgRichiesta;
	}

	/**
	 * @param object
	 */
	public void setPrgRichiesta(Object object) {
		prgRichiesta = object;
	}

	/**
	 * @return
	 */
	public String getPaginaLista() {
		return paginaLista;
	}

	/**
	 * @return
	 */
	public boolean isSkipLista() {
		return skipLista;
	}

	/**
	 * @param string
	 */
	public void setPaginaLista(String string) {
		paginaLista = string;
	}

	/**
	 * @param b
	 */
	public void setSkipLista(boolean b) {
		skipLista = b;
	}

	/**
	 * In alcune pagine il pulsante viene visualizzato al di fuori della
	 * testata.
	 * 
	 * @param session
	 * @param paginaLista
	 * @return
	 */
	public static String formatBackList(SessionContainer session, String paginaLista) {
		String token = "_TOKEN_" + paginaLista; // IdoListaRichiestePage
		String urlDiLista = (String) session.getAttribute(token.toUpperCase());
		String s = null;
		if (urlDiLista != null) {
			s = "<a href=\"" + "#\"" + " onClick=\"goTo('" + urlDiLista + "')"
					+ "\"><img src=\"../../img/rit_lista.gif\" border=\"0\">";
		}
		return s;
	}
	
	
	public static String formatBackListCopiaProsp(String urlDiLista) {
		String s = null;
		if (urlDiLista != null) {
			s = "<a href=\"" + "#\"" + " onClick=\"goTo('" + urlDiLista + "');window.top.menu.location='AdapterHTTP?PAGE=MenuCompletoPage'"
					+ "\"><img src=\"../../img/rit_lista_copia_prosp.gif\" border=\"0\">";
		}
		return s;
	}
	

	/**
	 * Converte l'oggetto passato in un numero in formato String
	 * 
	 * @param o
	 *            un prg in formato String o BigDecimal
	 * @return il numero in formato stringa o null
	 */
	private String getStrNumberNull(Object o) {
		String ret = null;
		try {
			if (o != null) {
				if (o instanceof String)
					ret = String.valueOf(Integer.parseInt(o.toString()));
				else
					ret = o.toString(); // si tratta di un BigDecimal o Integer
			}
		} catch (Exception e) {
			String req = "";
			try {
				req = RequestContainer.getRequestContainer().getServiceRequest().toString();
			} catch (Exception ee) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "impossibile recuperare la service request", ee);

			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "passato al costruttore un prg non numerico: " + o + req, e);

		}
		return ret;
	}

} // InfCorrentiAzienda
