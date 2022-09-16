/*
 * Creato il 31-ago-05
 *
 
 */
package it.eng.sil.util;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.sil.Values;

/**
 * @author vuoto
 * 
 */
public class InfoRegioneSingleton {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfoRegioneSingleton.class.getName());

	private static InfoRegioneSingleton _instance = null;

	private String urlSito = "http://www.regione.emilia-romagna.it";
	// private String urlSitoCpi = urlSito;
	private String codice = "00";
	private String nome = "(Non definito)";
	private String nomeSIL = "SILER";
	private String strAccesso = "(Non definito)";

	public static InfoRegioneSingleton getInstance() {
		if (_instance == null) {
			synchronized (InfoRegioneSingleton.class) {
				if (_instance == null) {
					try {
						_instance = new InfoRegioneSingleton();
						_instance.loadPars();
					} // try
					catch (Exception ex) {

						it.eng.sil.util.TraceWrapper.fatal(_logger, "InfoRegioneSingleton: ", (Exception) ex);

					}
				}
			}
		}
		return _instance;
	}

	public static void release() {
		if (_instance != null) {
			synchronized (InfoRegioneSingleton.class) {
				_instance = null;
			}
		}
	}

	/**
	 * @return
	 */
	public String getCodice() {
		return codice;
	}

	/**
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @return
	 */
	public String getUrlSito() {
		return urlSito;
	}

	/**
	 * @return
	 */
	public String getStrAccesso() {
		return strAccesso;
	}

	/**
	 * @return
	 */
	/*
	 * public String getUrlSitoCpi() { return urlSitoCpi; }
	 */

	private void loadPars() {
		DataConnection dataConnection = null;

		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		DataResult dr = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("CARICA_INFO_REG");
			sqlCommand = dataConnection.createSelectCommand(statement);

			dataResult = sqlCommand.execute();

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			codice = (String) sb.getAttribute("ROW.codice");
			nome = (String) sb.getAttribute("ROW.nome");
			urlSito = (String) sb.getAttribute("ROW.urlsitoreg");
			nomeSIL = (String) sb.getAttribute("ROW.nomesil");
			strAccesso = (String) sb.getAttribute("ROW.strAccesso");
			// urlSitoCpi = (String) sb.getAttribute("ROW.urlsitoCPI");

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "InfoRegioneSingleton: ", (Exception) e);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);

		}

	}

	/**
	 * @return
	 */
	public String getNomeSIL() {
		return nomeSIL;
	}

	/**
	 * 
	 * @return displayname da mostrare sulla pagina d'accesso
	 */
	public String getDispAccesso() {
		StringBuffer ret = new StringBuffer();
		ret.append("Sistema Informativo Lavoro<br/>");

		if (this.codice.compareTo("2") == 0)// AOSTA
			ret.append("Regione Autonoma VALLE D'AOSTA");
		else if (this.codice.compareTo("22") == 0)// TN
			ret.append("Provincia Autonoma di Trento");
		else // Default
			ret.append("Regione " + this.getNome());

		return ret.toString();// DEFAULT
	}

	/**
	 * 
	 * @return il logo in HTML relativo alla regione
	 * 
	 */
	public String getLogoSIL() {
		StringBuffer sb = new StringBuffer();

		if ("8".equals(codice)) { // EMILIA ROMAGNA
			sb.append("<TD align=\"left\" valign=\"middle\" width=\"19%\"><B><FONT color=\"red\">S</FONT>istema<BR>");
			sb.append("<FONT color=\"red\">I</FONT>nformativo<BR>");
			sb.append("<FONT color=\"red\">L</FONT>avoro<BR>");
			sb.append("<FONT color=\"red\">E</FONT>milia<BR>");
			sb.append("<FONT color=\"red\">R</FONT>omagna</B></TD>");
		} else if ("10".equals(codice)) { // UMBRIA
			sb.append("<TD align=\"left\" valign=\"middle\" width=\"19%\"><B><FONT color=\"red\">S</FONT>istema<BR>");
			sb.append("<FONT color=\"red\">I</FONT>nformativo<BR>");
			sb.append("<FONT color=\"red\">U</FONT>mbria<BR>");
			sb.append("<FONT color=\"red\">L</FONT>avoro<BR></TD>");
		} else if ("22".equals(codice)) { // TRENTO
			sb.append("<TD align=\"left\" valign=\"middle\" width=\"19%\"><B><FONT color=\"red\">S</FONT>istema<BR>");
			sb.append("<FONT color=\"red\">P</FONT>rovinciale<BR>");
			sb.append("<FONT color=\"red\">I</FONT>nformativo<BR>");
			sb.append("<FONT color=\"red\">L</FONT>avoro<BR></TD>");
		} else { // VDA - Default
			sb.append("<TD align=\"left\" valign=\"middle\" width=\"19%\"><B><FONT color=\"red\">S</FONT>istema<BR>");
			sb.append("<FONT color=\"red\">I</FONT>nformativo<BR>");
			sb.append("<FONT color=\"red\">L</FONT>avoro<BR></TD>");
		}
		return sb.toString();
	}

}