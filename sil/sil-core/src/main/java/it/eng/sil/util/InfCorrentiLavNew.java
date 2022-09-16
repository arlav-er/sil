package it.eng.sil.util;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.message.MessageBundle;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.Values;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;
import oracle.jdbc.OracleTypes;

/**
 * Classe che implementa la generazione del codice HTML per la visualizzazione delle informazioni correnti inerenti il
 * lavoratore. Il codice HTML generato ha un link "Inf. correnti" ad una pagina che reperisce una serie di informazioni
 * correnti sul lavoratore. Tale pagina è cablata nel codice ma può anche essere impostata tremite il metodo
 * "setInfoCorrentiPage(String)". La pagina può essere aperta in pop-up (default) o nella stessa finestra utilizzando il
 * metotdo show(JspWriter out,boolean popUp) impostando il secondo parametro a false (se si utilizza il metodo
 * show(JspWriter out) la pagina è aperta in pop-up)
 * 
 * 
 * Esempio:
 * 
 * Inizializza l'oggetto InfCorrentiLav testata= new InfCorrentiLav(cdnLavoratore);
 * 
 * Inserire il codice seguente nel punto in cui si vuole che siano generate le informazioni <% testata.show(out,user);
 * %>
 * 
 * Esempio di risultato HTML: "Lavoratore Giggi Totti Inf. correnti: Elenco Anag. 01/01/2003 CPI Tit. BOLOGNA Stato
 * occupaz. Disoccupato, etc....."
 * 
 * E' anche possibile estrarre i singoli valori con i metodi "getNome()", "getCognome()",... in modo da avere codice
 * come questo:
 * <p>
 * Nome lavoratore: <%= testata.getNome() %>
 * </p>
 * 
 * @author Davide Giuliani (implements Corrado Vaccari)
 */
public class InfCorrentiLavNew {

	Object cdnLavotarore;

	private String cognome = "&nbsp;&nbsp;";
	private String nome = "&nbsp;&nbsp;";
	private String codFiscale = "&nbsp;&nbsp;";
	private String descCPI = "&nbsp;&nbsp;";
	private String statoOcc = "&nbsp;&nbsp;";
	private String statoOccRagg = "&nbsp;&nbsp;";
	private String dataInizio = "&nbsp;&nbsp;";
	private String strSesso = "&nbsp;&nbsp;";

	private String infoCorrentiPage = "AmstrInfoCorrentiPage";

	private boolean existInfo = true;
	private PageAttribs pageAttribs;
	private List sezioni;
	private int maxLenStatoOcc = 50; // non utilizzata

	// flag che indica se il link verrà aperto con una popUp
	public static boolean PopUp_NO = false;
	public static boolean PopUp_YES = true;

	// Var destinate ad usi futuri quando verra implementata la profilatura; i nomi sono puramente inventati
	private String user;

	private SessionContainer session = null;

	String paginaLista = "AnagRicercaPage";
	boolean skipLista = false;

	private InfCorrentiLavNew() {
	}

	public InfCorrentiLavNew(Object cdnLavotarore, User user) {
		this(null, cdnLavotarore, user);
	}

	public InfCorrentiLavNew(SessionContainer session, Object cdnLavotarore, User user) {
		this.cdnLavotarore = cdnLavotarore;
		this.user = Integer.toString(user.getCdnProfilo());
		this.session = session;
	}

	/**
	 * da utilizzare nelle sezioni dove il lavoratore è CRYPTATO
	 * 
	 * @param session
	 * @param cdnLavotaroreCryptato
	 * @param user
	 * @param checkCrypt
	 *            true
	 */
	public InfCorrentiLavNew(SessionContainer session, String cdnLavotaroreCryptato, User user, boolean checkCrypt) {
		if (checkCrypt) {
			String cdnLavotarore = EncryptDecryptUtils.decrypt(cdnLavotaroreCryptato);
			this.cdnLavotarore = cdnLavotarore;
			this.user = Integer.toString(user.getCdnProfilo());
			this.session = session;
		} else {
			this.cdnLavotarore = cdnLavotaroreCryptato;
			this.user = Integer.toString(user.getCdnProfilo());
			this.session = session;
		}
	}

	/**
	 * Assegna il nome alla Page apribile dal link, solo nel caso in cui si voglia utilizzare una page alternativa a
	 * quella impostata di default.
	 * 
	 * @param nomeDettaglioUtentePage
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
		sb.append("   var opened =  window.open (\"AdapterHTTP?PAGE=");
		sb.append(this.infoCorrentiPage);
		sb.append("&cdnLavoratore=");
		sb.append(codLavoratore);
		// sb.append("&cognome=");
		// sb.append(cognome);
		// sb.append("&nome=");
		// sb.append(nome);
		sb.append(
				"\", \"InfoCorrenti\", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');\r\n ");
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
		List righe = null;
		SourceBean datiLavoratore = null;
		String sezione, sezionePrec = "";
		String etichetta;
		String campo;
		String campo1;// data di nascita del lavoratore
		String newLine;

		if (cdnLavotarore != null) { // se necessario inserisco lo script per aprire la finestra in pop-up
			if (popUp == true) {
				out.println(addScript(cdnLavotarore));
			}

			/* Modifiche per le stondature S.O. 19/02/2004 */
			/*
			 * out.println("<div class=\"contentInfoC\"><div class=\"contentIC\">"); out.
			 * println("<img class=\"borderTL10\" src=\"../../img/angoli/tl_lista.gif\" alt=\"&nbsp;\" width=\"14\" height=\"14\" border=\"0\"/>"
			 * ); out.
			 * println("<img class=\"borderTR10\" src=\"../../img/angoli/tr_lista.gif\" alt=\"&nbsp;\" width=\"14\" height=\"14\" border=\"0\"/>"
			 * ); //
			 */
			// Stondature - Stefania Orioli - 10/03/2004 - Elemento TOP
			out.println(
					"<br><table maxwidth=\"96%\" width=\"96%\" align=\"center\" margin=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr><td class=\"infoNew\" valign=\"top\" align=\"left\" width=\"0\" height=\"0px\">");
			// out.println("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
			// out.println("<td class=\"infoNew\" height=\"0px\">&nbsp;</td>");
			if (!skipLista) {
				if (session != null) {
					String token = "_TOKEN_" + paginaLista; // "AnagRicercaPage";
					String urlDiLista = (String) session.getAttribute(token.toUpperCase());
					if (urlDiLista != null) {

						out.println("<td rowspan=\"3\" class=\"infoNew\" height=\"0px\"><a href=\"" + "#\""
								+ " onClick=\"goTo('" + urlDiLista + "')"
								+ "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></td>");
					}
				}
			}

			// out.println("<td class=\"infoNew\" valign=\"top\" align=\"right\" width=\"0\" height=\"0px\">");
			// out.println("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\"></td></tr>");

			// out.println("<tr><td class=\"infoNew\" width=\"0\">&nbsp;</td>");
			// out.println("<td class=\"infoNew\" align=\"center\">");
			out.println("<p class=\"info_lav\">");

			// Recupero informazioni lavoratore
			try {
				righe = getInfoLavoratore(cdnLavotarore);
			} catch (SourceBeanException e) {
				righe = null;
			}

			if (righe != null && righe.size() >= 1) {
				if (((SourceBean) righe.get(0)).getName().equalsIgnoreCase("SQLEXCEPTION")) {
					// Qualcosa è andato male, mostro il messaggio opportuno all'utente
					Integer sqlcode = (Integer) ((SourceBean) righe.get(0)).getAttribute("CODE");
					// Trasformo il codice nell'equivalente sil
					int silcode = sqlcode.intValue() + 5000;
					out.println("<strong>" + MessageBundle.getMessage(String.valueOf(silcode)) + "</strong>");
				} else {
					// Dovrebbe essere andato tutto bene, mostro i risultati
					out.println("Lavoratore ");
					ListIterator iter = righe.listIterator();

					if (iter.hasNext()) {
						datiLavoratore = (SourceBean) iter.next();
						sezione = (String) datiLavoratore.getAttribute("STRSEZIONE");
						etichetta = (String) datiLavoratore.getAttribute("STRDENOMINAZIONE");
						campo = (String) datiLavoratore.getAttribute("CONTENUTO_CAMPO");
						// 15/06/2004 Aggiunta data di nascita
						campo1 = (String) datiLavoratore.getAttribute("CONTENUTO_CAMPO1");// Data di nascita del
																							// lavoratore
						out.print("<strong>" + sezione + "&nbsp;" + etichetta
								+ "</strong> codice&nbsp;fiscale&nbsp;<strong>" + campo + "</strong>&nbsp;"
								+ "data&nbsp;di&nbsp;nascita&nbsp;<strong>" + campo1 + "</strong>");
					}

					RequestContainer requestContainer = RequestContainer.getRequestContainer();
					SessionContainer sessionContainer = requestContainer.getSessionContainer();
					User user = (User) sessionContainer.getAttribute(User.USERID);
					PageAttribs attributi = new PageAttribs(user, "AmstrInfCorrentiLav");

					boolean canLink = false;

					canLink = attributi.containsButton("INFOCORRENTE");

					if (canLink) {
						if (popUp == true) {
							out.println(
									"<br/><a class=\"info_lav\" href=\"#\" onClick=\"openPopUP()\" >Inf.&nbsp;correnti </a>");
						} else {
							out.println("<br/><a class=\"info_lav\" href=\"../../servlet/AdapterHTTP?PAGE="
									+ this.infoCorrentiPage + "&cdnLavoratore=" + cdnLavotarore
									+ "\">Inf. correnti </a>");
						}
					}

					while (iter.hasNext()) {
						datiLavoratore = (SourceBean) iter.next();
						sezione = (String) datiLavoratore.getAttribute("STRSEZIONE");
						etichetta = (String) datiLavoratore.getAttribute("STRDENOMINAZIONE");
						campo = (String) datiLavoratore.getAttribute("CONTENUTO_CAMPO");
						newLine = (String) datiLavoratore.getAttribute("NUOVALINEA");

						if (newLine != null && newLine.equalsIgnoreCase("S")) {
							out.print("<BR/>");
						}

						if (!sezione.equalsIgnoreCase(sezionePrec))
							out.print(sezione + ": ");
						// out.print("<BR>"+sezione+": ");
						sezionePrec = sezione;

						out.print(" " + etichetta);

						// if(campo != null && !campo.equalsIgnoreCase("null"))
						if (campo != null)
							out.print(" <strong>" + campo + "</strong>");
						else
							out.print("&nbsp;&nbsp;&nbsp;");
						out.print("; ");
					}
				}
			} else {
				out.println("<strong>" + MessageBundle.getMessage("25114") + "</strong>");
			}
			out.println("</p>");
			/* Modifiche per le stondature (chiusura) S.O. 19/02/2004 */
			/*
			 * out.println("<div class=\"roundedCornerSpacer\">&nbsp;</div></div>");
			 * out.println("<div class=\"bottomCorners\">"); out.
			 * println("<img class=\"borderBL10\" src=\"../../img/angoli/bl_lista.gif\" alt=\"&nbsp;\" width=\"14\" height=\"14\" border=\"0\"/>"
			 * ); out.
			 * println("<img class=\"borderBR10\" src=\"../../img/angoli/br_lista.gif\" alt=\"&nbsp;\" width=\"14\" height=\"14\" border=\"0\" />"
			 * ); out.println("</div></div>");
			 */
			// Stondature - Stefania Orioli - 10/03/2004 - Elemento BOTTOM
			// out.println("</td><td class=\"infoNew\" width=\"0\">&nbsp;</td></tr>");
			// out.println("<tr valign=\"bottom\">");
			// out.println("<td class=\"infoNew\" valign=\"bottom\" align=\"left\" width=\"0\" height=\"0px\">");
			// out.println("<img src=\"../../img/angoli/bia4.gif\" width=\"6\" height=\"6\"></td>");
			// out.println("<td class=\"infoNew\" height=\"0px\">&nbsp;</td>");
			// out.println("<td class=\"infoNew\" valign=\"bottom\" align=\"right\" width=\"0\" height=\"0px\">");
			out.println("</td></tr></table>");
		}

	} // show

	/**
	 * Ritorna un SourceBean con nome, cognome e una serie di informazioni relative al lavoratore corrispondente al
	 * codice passato, prelevate attraverso la query al DB chiamata GET_InfoCorrentiLav
	 * 
	 * @param codLavoratore
	 *            Codice del laoratore
	 * @return SourceBean contenente informazioni relative al lavoratore
	 */
	private List getInfoLavoratore(Object codLavoratore) throws SourceBeanException {
		// SourceBean riga = null;
		// Map result = new HashMap();

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		Connection connection = null;
		List righe = null;

		Monitor monitor = null;

		try {
			monitor = MonitorFactory.start(this.getClass().getName() + "::getInfoLavoratore");
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			connection = dataConnection.getInternalConnection();

			// String statement = SQLStatements.getStatement("INFCORRLAV_TESTATA");
			// sqlCommand = dataConnection.createSelectCommand(statement);

			String encrypterKey = System.getProperty("_ENCRYPTER_KEY_");

			CallableStatement stmt = connection.prepareCall("{? = call pg_info_riass.infotestatalav (?, ?, ?) }");

			stmt.registerOutParameter(1, OracleTypes.CLOB);
			// stmt.registerOutParameter(1, OracleTypes.VARCHAR);
			stmt.setString(2, codLavoratore.toString());
			stmt.setString(3, encrypterKey);
			stmt.setString(4, user);
			stmt.execute();

			String xml = (String) stmt.getString(1);

			SourceBean sb = SourceBean.fromXMLString(xml);

			return sb.getAttributeAsVector("row");

		} // try
		catch (com.engiweb.framework.error.EMFInternalError ex) {
			LogUtils.logError("InfCorrentiLav.getInfoLavoratore()", "Internal Error", ex, this);
		} catch (SQLException ex) {
			LogUtils.logError("InfCorrentiLav.getInfoLavoratore()", "SQLException", ex, this);
			righe = new Vector();
			SourceBean sqlex = new SourceBean("SQLEXCEPTION");
			sqlex.setAttribute("CODE", new Integer(ex.getErrorCode()));
			righe.add(sqlex);
		} catch (Exception ex) {
			LogUtils.logError("InfCorrentiLav.getInfoLavoratore()", "Exception", ex, this);
		} finally {
			monitor.stop();
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		return righe;
	} // getInfoLavoratore(_)

	/**
	 * Fornisce una rappresentazione delle proprietà dell'oggetto; utile in fase di debug.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(this.getClass());
		sb.append(" CodLavotratore [");
		sb.append(this.cdnLavotarore);
		sb.append("], Cognome [");
		sb.append(this.cognome);
		sb.append("], Nome [");
		sb.append(this.nome);
		sb.append("], El Anag. [");
		sb.append(this.dataInizio);
		sb.append("], Stato Occupazionale [");
		sb.append(this.statoOccRagg);
		sb.append("]}");

		return sb.toString();
	} // toString()

	/**
	 * 
	 */
	public String getCognome() {
		return this.cognome;
	}

	/**
	 * 
	 */
	public String getNome() {
		return this.nome;
	}

	/**
	 * 
	 */
	public String getCodFiscale() {
		return this.codFiscale;
	}

	/**
	 * 
	 */
	public String getDescCPI() {
		return this.descCPI;
	}

	/**
	 * 
	 */
	public String getStatoOcc() {
		return this.statoOcc;
	}

	/**
	 * 
	 */
	public String getStatoOccRagg() {
		return this.statoOccRagg;
	}

	/**
	 * 
	 */
	public String getdataInizio() {
		return this.dataInizio;
	}

	public int getMaxLenStatoOcc() {
		return maxLenStatoOcc;
	}

	public void setMaxLenStatoOcc(int newMaxLenStatoOcc) {
		maxLenStatoOcc = newMaxLenStatoOcc;
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
	public String getSesso() {
		return this.strSesso;
	}

	/**
	 * @param string
	 */
	public void setPaginaLista(String string) {
		paginaLista = string;
	}

	/**
	 * @return
	 */
	public boolean isSkipLista() {
		return skipLista;
	}

	/**
	 * @param b
	 */
	public void setSkipLista(boolean b) {
		skipLista = b;
	}

	/**
	 * @param strSesso
	 */
	public void setSesso(String sesso) {
		this.strSesso = sesso;
	}

} // InfCorrentiLav