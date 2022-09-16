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
public class InfoProvinciaSingleton {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfoProvinciaSingleton.class.getName());

	private static InfoProvinciaSingleton _instance = null;

	private String urlSito = "http://www.regione.emilia-romagna.it";
	private String urlSitoCpi = urlSito;
	private String codice = "00";
	private String nome = "(Non definito)";
	private String versione = "0.0.0";
	private String flgPoloReg = "";
	private String codiceRegione = "";

	public static InfoProvinciaSingleton getInstance() {
		if (_instance == null) {
			synchronized (InfoProvinciaSingleton.class) {
				if (_instance == null) {
					try {
						InfoProvinciaSingleton _instance_tmp = new InfoProvinciaSingleton();
						_instance_tmp.loadPars();
						_instance = _instance_tmp;
					} // try
					catch (Exception ex) {

						it.eng.sil.util.TraceWrapper.fatal(_logger, "InfoProvinciaSingleton: ", (Exception) ex);

					}
				}
			}
		}
		return _instance;
	}

	public static void release() {
		if (_instance != null) {
			synchronized (InfoProvinciaSingleton.class) {
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

	public String getDispAccesso() {
		if (this.codice.compareTo("7") == 0)// AOSTA
			return "Accesso Regione Autonoma VALLE D'AOSTA";
		else if (this.codiceRegione != null && this.codiceRegione.compareTo("8") == 0)// EMILIA ROMAGNA
			return "Regione Emilia Romagna";

		return "Accesso Provincia di " + this.getNome();// DEFAULT
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
	public String getUrlSitoCpi() {
		return urlSitoCpi;
	}

	public String getVersione() {
		return versione;
	}

	public String getFlgPoloReg() {
		return flgPoloReg;
	}

	public String getCodiceRegione() {
		return codiceRegione;
	}

	private void loadPars() {
		DataConnection dataConnection = null;

		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("CARICA_INFO_PROV");
			sqlCommand = dataConnection.createSelectCommand(statement);

			dataResult = sqlCommand.execute();

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			codice = (String) sb.getAttribute("ROW.codice");
			nome = (String) sb.getAttribute("ROW.nome");
			urlSito = (String) sb.getAttribute("ROW.urlsitoprov");
			urlSitoCpi = (String) sb.getAttribute("ROW.urlsitoCPI");
			versione = (String) sb.getAttribute("ROW.versione");
			flgPoloReg = (String) sb.getAttribute("ROW.flgpoloreg");
			codiceRegione = (String) sb.getAttribute("ROW.codicereg");

			InfoRegioneSingleton.getInstance();

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "InfoProvinciaSingleton: ", (Exception) e);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);

		}

	}
}