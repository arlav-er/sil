package it.eng.sil.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspWriter;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.tags.Util;

import it.eng.sil.Values;
import it.eng.sil.security.User;

/**
 * Classe per caricare le linguette in base ad una configurazione predefinita e particolare per la medesima
 * regione/provincia
 * 
 * La particolarità della classe risiede nella QUERY per estrarre le linguette e sottolinguette viene utilizzata la
 * TS_VOCE_FUNZIONE insieme alle tabelle di codificazione TS_CONFIG_CODIFICA e TS_CONFIG_LOC con CODTIPOCONFIG =
 * 'TS_VCFUNZ'
 * 
 * @author coticone
 *
 */
public final class LinguetteConfigurazioneRegione {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(LinguetteConfigurazioneRegione.class.getName());
	Object cdnLavoratore;

	List linguette = new ArrayList(20);
	User user = null;
	String strCodiceConfigurazione = null;

	int codFunzione;
	int livelloMax = 0;
	private String codiceItem = "CDNLAVORATORE";
	private String page;
	private String pathLinguettaFogliaAttiva = "";
	private int[] numerazioni = new int[10];
	private String[] paths = new String[10];
	private int indexLinguettaSel = -1;
	private int size = 0;
	private String codStatoAtto;
	String parametro = null;

	/**
	 * @deprecated
	 * @param _codFunzione
	 * @param _page
	 * @param _cdnLavoratore
	 * @param _strConfigurazione
	 */
	public LinguetteConfigurazioneRegione(int _codFunzione, String _page, BigDecimal _cdnLavoratore, String _parametro,
			String _strConfigurazione) {
		this(null, _codFunzione, _page, _cdnLavoratore, _parametro, _strConfigurazione);
	}

	/**
	 * Unico costruttore.
	 * 
	 * @param user:
	 *            l'utente connesso al sistema. Serve perche' le linguette dipendono direttamente anche dal suo profilo.
	 * @param codFunzione:
	 *            il codice funzione (con questo codice si riesce ad che individuare tutti i componenti (le varie
	 *            linguette) della funzione.
	 * @param page:
	 *            la PAGE dove si e' posizionati
	 * @param cdnLavoratore:
	 *            il codice del lavoratore selezionato.
	 * @param strConfigurazione:
	 *            la CONFIGURAZIONE DATI da caricare
	 */
	public LinguetteConfigurazioneRegione(User user, int _codFunzione, String _page, BigDecimal _cdnLavoratore,
			String _parametro, String _strConfigurazione) {
		this.codFunzione = _codFunzione;
		this.page = _page.toUpperCase();
		this.cdnLavoratore = _cdnLavoratore;
		this.user = user;
		this.strCodiceConfigurazione = _strConfigurazione;
		this.parametro = _parametro;
		caricaLinguette();
	}

	/**
	 * Unico costruttore.
	 * 
	 * @param user:
	 *            l'utente connesso al sistema. Serve perche' le linguette dipendono direttamente anche dal suo profilo.
	 * @param codFunzione:
	 *            il codice funzione (con questo codice si riesce ad che individuare tutti i componenti (le varie
	 *            linguette) della funzione.
	 * @param page:
	 *            la PAGE dove si e' posizionati
	 * @param cdnLavoratore:
	 *            il codice del lavoratore selezionato.
	 * @param strConfigurazione:
	 *            la CONFIGURAZIONE DATI da caricare
	 */
	public LinguetteConfigurazioneRegione(User user, int _codFunzione, String _page, BigDecimal _cdnLavoratore,
			String _codStatoAtto, String _parametro, String _strConfigurazione) {
		this.codFunzione = _codFunzione;
		this.page = _page.toUpperCase();
		this.cdnLavoratore = _cdnLavoratore;
		this.codStatoAtto = _codStatoAtto;
		this.user = user;
		this.strCodiceConfigurazione = _strConfigurazione;
		this.parametro = _parametro;
		caricaLinguette();
	}

	/**
	 * Costruttore che prevede la decrypatzione del cdnlavoratore pre caricare le linguette
	 * 
	 * @param user:
	 *            l'utente connesso al sistema. Serve perche' le linguette dipendono direttamente anche dal suo profilo.
	 * @param codFunzione:
	 *            il codice funzione (con questo codice si riesce ad che individuare tutti i componenti (le varie
	 *            linguette) della funzione.
	 * @param page:
	 *            la PAGE dove si e' posizionati
	 * @param cdnLavoratoreEncrypt:
	 *            il codice del lavoratore selezionato.cryptato
	 * @param strConfigurazione:
	 *            la CONFIGURAZIONE DATI da caricare
	 */
	public LinguetteConfigurazioneRegione(User user, int _codFunzione, String _page, String cdnLavoratoreEncrypt,
			String _parametro, boolean checkCrypt, String _strConfigurazione) {
		if (checkCrypt) {
			// String cdnLavoratore =
			// EncryptDecryptUtils.decrypt(cdnLavoratoreEncrypt);

			// if (cdnLavoratore != null) {
			this.cdnLavoratore = cdnLavoratoreEncrypt;
			// }

		} else {
			this.cdnLavoratore = new BigDecimal(cdnLavoratoreEncrypt);
		}
		this.codFunzione = _codFunzione;
		this.page = _page.toUpperCase();
		this.strCodiceConfigurazione = _strConfigurazione;
		this.user = user;
		this.parametro = _parametro;
		caricaLinguette();
	}

	/**
	 * Non ammesso
	 */
	private LinguetteConfigurazioneRegione() {
	}

	public void setCodiceItem(String newCodiceItem) {
		codiceItem = newCodiceItem;
	}

	public String getCodiceItem() {
		return codiceItem;
	}

	/**
	 * Genera l'HTML delle linguette. Prende l'oggetto predefinito 'out' della pagina JSP per poter direttamente
	 * scrivere sullo stream di output della JSP.
	 * 
	 */
	public void show(JspWriter out) throws IOException {
		calcolaPaths();

		for (int liv = 1; liv <= livelloMax; liv++) {
			showLivello(out, liv);
		}

		generateJs(out);
	}

	/**
	 * Ritorna il nome della PAGE successiva a quella selezionata. Se quella corrente e' l'ultima, ritorna null;
	 */
	private String getNextPage() {
		int lingSize = linguette.size();
		Linguetta linCurr = (Linguetta) linguette.get(indexLinguettaSel);
		Linguetta linNext = null;

		if ((indexLinguettaSel + 1) < lingSize) {
			linNext = (Linguetta) linguette.get(indexLinguettaSel + 1);
		} else {
			return null;
		}

		int livCurr = linCurr.getLivello().intValue();
		int livNext = linNext.getLivello().intValue();

		if (livNext == livCurr) {
			return linNext.getStrpage();
		}

		return null;
	}

	/**
	 * Ritorna il nome della PAGE precedente a quella selezionata. Se quella corrente e' la prima, ritorna null;
	 */
	private String getPrevPage() {
		Linguetta linCurr = (Linguetta) linguette.get(indexLinguettaSel);
		Linguetta linPrev = null;

		if ((indexLinguettaSel - 1) >= 0) {
			linPrev = (Linguetta) linguette.get(indexLinguettaSel - 1);
		} else {
			return null;
		}

		int livCurr = linCurr.getLivello().intValue();
		int livPrev = linPrev.getLivello().intValue();

		if (livPrev == livCurr) {
			return linPrev.getStrpage();
		}

		return null;
	}

	private String calcolaPage(String path, int currIndex) {
		// Non so se funziona a tre livelli
		if ((currIndex + 1) >= linguette.size()) {
			return null;
		}

		Linguetta lin = (Linguetta) linguette.get(currIndex + 1);
		String pathLin = lin.getPath() + "/";

		if (pathLin.startsWith(path)) {
			return lin.getStrpage();
		} else {
			return calcolaPage(path, currIndex + 1);
		}
	}

	private void calcolaPaths() {
		StringTokenizer token = new StringTokenizer(pathLinguettaFogliaAttiva, "/");
		paths[0] = "/";

		int i = 1;

		while (token.hasMoreTokens()) {
			String pezzo = token.nextToken();
			paths[i] = paths[i - 1] + pezzo + "/";
			i++;
		}
	}

	private void caricaLinguette() {
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		// StringBuffer buf = new StringBuffer();

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("CARICA_LINGUETTE_CODIFICA");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, new Integer(this.codFunzione)));
			inputParameter
					.add(dataConnection.createDataField("", Types.NUMERIC, new Integer(this.user.getCdnProfilo())));
			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, this.strCodiceConfigurazione));
			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, this.strCodiceConfigurazione));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");

			size = righe.size();

			for (int i = 0; i < size; i++) {
				SourceBean riga = (SourceBean) righe.get(i);

				// BigDecimal _cdnvocefunzione = (BigDecimal)
				// riga.getAttribute("cdnvocefunzione");
				// String _strcodliv = (String) riga.getAttribute("strcodliv");
				String _strdescliv = (String) riga.getAttribute("strdescliv");

				// BigDecimal _cdnVoceFunzionePadre = (BigDecimal)
				// riga.getAttribute(
				// "cdnVoceFunzionePadre");
				// String _strnumpar = (String)
				// riga.getAttribute("strnumparagrafo");
				// BigDecimal _cdnfunzione = (BigDecimal)
				// riga.getAttribute("cdnfunzione");
				// BigDecimal _cdncomponente = (BigDecimal)
				// riga.getAttribute("cdncomponente");
				// String _strdenominazioneComp = (String)
				// riga.getAttribute("strdenominazioneComp");
				// String _strdescrizioneComp = (String)
				// riga.getAttribute("strdescrizioneComp");
				// String _flgvisibileComp = (String)
				// riga.getAttribute("flgvisibileComp");
				String _strpage = (String) riga.getAttribute("strpage");

				if (_strpage == null) {
					_strpage = "";
				}

				// BigDecimal _numordine = (BigDecimal)
				// riga.getAttribute("numordine");
				String _PATH = (String) riga.getAttribute("PATH");
				BigDecimal _livello = (BigDecimal) riga.getAttribute("livello");

				// Alcune impostazioni utili
				if (_livello.intValue() > livelloMax) {
					livelloMax = _livello.intValue();
				}

				if (_strpage.equalsIgnoreCase(page)) {
					pathLinguettaFogliaAttiva = _PATH + "/";
					indexLinguettaSel = i;
				}

				Linguetta l = new Linguetta(
						// _cdnvocefunzione, _
						// strcodliv,
						_strdescliv,
						// _cdnVoceFunzionePadre,
						// _strnumpar,
						// _cdnfunzione,
						// _cdncomponente,
						// _strdenominazioneComp,
						// _strdescrizioneComp,
						// _flgvisibileComp,
						_strpage,
						// _numordine,
						_PATH, _livello);
				linguette.add(l);
			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
	}

	private void generateJs(JspWriter out) throws IOException {
		if (indexLinguettaSel == -1) {
			return;
		}

		String prevPage = getPrevPage();
		String nextPage = getNextPage();

		out.println(" <script language=\"javascript\">");

		// ************************************************
		// Funzioni di salto sul link (spostata in customTL.js)
		// *************************************************

		// out.println(" function goTo(url){");
		//
		// out.println(" try{");
		// out.println(" if (flagChanged==true){");
		// out.println(
		// " if (!confirm(\"I dati sono cambiati.\\r\\nProcedere lo stesso
		// ?\")){");
		// out.println(" return; ");
		// out.println(" } ");
		// out.println(" } ");
		// out.println(" }catch(e) {} ");
		//
		// out.println(" window.location=\"AdapterHTTP?\" + url;");
		// out.println(" }");

		// ************************************************
		// Funzioni di controllo della pulsantiera in basso
		// *************************************************
		// Pagina di indietro
		if (prevPage != null) {
			out.println("        function indietro(){");
			/*
			 * ORIGINALE: // out.println(" try{"); out.println(" if (flagChanged==true){"); out.println( " if
			 * (!confirm(\"I dati sono cambiati.\\r\\nProcedere lo stesso ?\")){"); out.println(" return;
			 * "); out.println(" } "); out.println(" } "); // out.println(" }catch(e) {} ");
			 * 
			 * out.println(" url=\"AdapterHTTP?\""); out.println(" url+=\"PAGE=" + prevPage + "\";");
			 * out.println(" url+=\"&" + codiceItem + "=" + cdnLavoratore + "\";"); out.println(" url+=\"&CDNFUNZIONE="
			 * + codFunzione + "\";"); out.println(" window.location=url; ");
			 */
			out.println("             var qs = \"PAGE=" + prevPage + "\";");
			out.println("                 qs += \"&" + codiceItem + "=" + cdnLavoratore + "\";");
			out.println("                 qs += \"&CDNFUNZIONE=" + codFunzione + "\";");
			out.println("                 qs += \"&parametro=" + parametro + "\";");
			out.println("             goTo(qs);");

			out.println("        }");
		}

		// Pagina di avanti
		if (nextPage != null) {
			out.println("        function avanti(){");
			/*
			 * ORIGINALE: // out.println(" try{"); out.println(" if (flagChanged==true){"); out.println( " if
			 * (!confirm(\"I dati sono cambiati.\\r\\nProcedere lo stesso ?\")){"); out.println(" return;
			 * "); out.println(" } "); out.println(" } "); // out.println(" }catch(e) {} ");
			 * 
			 * out.println(" url=\"AdapterHTTP?\""); out.println(" url+=\"PAGE=" + nextPage + "\";");
			 * out.println(" url+=\"&" + codiceItem + "=" + cdnLavoratore + "\";"); out.println(" url+=\"&CDNFUNZIONE="
			 * + codFunzione + "\";"); out.println(" window.location=url; ");
			 */
			out.println("             var qs = \"PAGE=" + nextPage + "\";");
			out.println("                 qs += \"&" + codiceItem + "=" + cdnLavoratore + "\";");
			out.println("                 qs += \"&CDNFUNZIONE=" + codFunzione + "\";");
			out.println("                 qs += \"&parametro=" + parametro + "\";");
			out.println("             goTo(qs);");

			out.println("        }");
		}

		out.println("   rinfresca();");

		out.println("   </script>");
	}

	private void showLivello(JspWriter out, int liv) throws IOException {
		if (liv == 1) {
			out.println(" <div class='menu'>");
		} else {
			out.println(" <div class='menu2'>");
		}

		int numLivello = 0;
		numerazioni[liv] = 0;

		String prefixNum = "";

		for (int i = 1; i < liv; i++) {
			prefixNum += (" " + numerazioni[i] + ".");
		}

		for (int i = 0; i < linguette.size(); i++) {
			Linguetta linguetta = (Linguetta) linguette.get(i);

			if (linguetta.getLivello().intValue() == liv) {
				String path = linguetta.getPath() + "/";
				String desc = linguetta.getStrdescliv();
				String page = linguetta.getStrpage();

				String pathsLiv = paths[liv - 1];
				if ((pathsLiv != null) && path.startsWith(pathsLiv)) {
					numLivello++;

					String etichetta = prefixNum + numLivello + " " + desc;
					etichetta = Util.replace(etichetta, " ", "&nbsp;");

					int numStile = (liv % 3);

					if (!pathLinguettaFogliaAttiva.startsWith(path)) {
						// non è un nodo che appartiene allo stesso ramo di
						// quelllo selezionato
						if (page.equals("")) {
							page = calcolaPage(path, i);
						}

						if (page != null) {
							// modifica dona
							/*
							 * String queryString = "PAGE=" + page + "&" + this.codiceItem + "=" +
							 * this.cdnLavoratore.intValue() + "&CDNFUNZIONE=" + this.codFunzione + "&parametro=" +
							 * this.parametro;
							 */

							String queryString = "PAGE=" + page + "&" + this.codiceItem + "="
									+ this.cdnLavoratore.toString() + "&CDNFUNZIONE=" + this.codFunzione + "&parametro="
									+ this.parametro;

							// GG 22/12/2004 - per gestione documenti
							if (page.equalsIgnoreCase("DocumentiAssociatiPage")) {

								if (codiceItem.equalsIgnoreCase("cdnLavoratore")) {
									queryString += "&lookLavoratore=false" + "&lookAzienda=true" + "&contesto=L";
								} else if (codiceItem.equalsIgnoreCase("prgAzienda")) {
									queryString += "&lookLavoratore=true" + "&lookAzienda=false" + "&contesto=A";
								}
							}

							out.println("<a href='#' onclick='goTo(\"" + queryString
									+ "\");return false' class='bordato" + numStile + "'>" + "<span class='tr_round"
									+ numStile + numStile + "'>" + etichetta + "</span></a>");
						}
					} else {
						// Nodo selezionato
						out.println("<a href='#' onclick='return false' class='sel" + numStile + "'>"
								+ "<span class='tr_round" + numStile + "'>" + etichetta + "</span>" + "</a>");
						numerazioni[liv] = numLivello;
					}
				}
			}
		}

		out.println("</div>");
	}

	/**
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return
	 */
	public List getLinguette() {
		return linguette;
	}

}