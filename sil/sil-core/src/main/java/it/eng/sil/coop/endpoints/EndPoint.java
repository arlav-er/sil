/*
 * Creato il 05-Apr-06
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.endpoints;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * 
 * @author rolfini
 *
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class EndPoint {

	private static final Logger log = Logger.getLogger(EndPoint.class.getName());

	private String dataSourceJndi;

	private String name = null;
	private String url = null;

	public EndPoint() {

	}

	private void fetchUrl() throws Exception {

		if (name == null) {
			return;
		}

		EndPointSingleton endPointSingleton = EndPointSingleton.getInstance(dataSourceJndi);
		url = endPointSingleton.getUrl(name);

	}

	// GETTER

	public void init(String _dataSourceJndi, String endPointName) throws Exception {
		dataSourceJndi = _dataSourceJndi;
		name = endPointName;
		fetchUrl();
	}

	public String getUrl() {
		return url;
	}

	public String readConfigurazione(String codTipoConfig) throws Exception {

		Connection dataConnection = null;
		ResultSet dr = null;
		Statement command = null;
		String valoreConfig = "0";

		if (dataSourceJndi.equals(""))
			throw new Exception("Impossibile inizializzare la connessione al db: JNDI datasource nullo");

		try {
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			DataSource dbDS = (DataSource) ctx.lookup(dataSourceJndi);
			dataConnection = dbDS.getConnection();
		} catch (Exception dbEx) {
			log.fatal("Eccezione nell'accesso al db utilizzando il datasource", dbEx);

			throw new Exception("Eccezione nell'accesso al database", dbEx);
		}

		try {
			String statement = "select to_char(ts_config_loc.num) num from ts_config_loc, de_tipo_config, ts_generale "
					+ " where ts_generale.codprovinciasil = ts_config_loc.strcodrif and de_tipo_config.codtipoconfig = ts_config_loc.codtipoconfig "
					+ " and trunc(sysdate) between trunc(de_tipo_config.datinizioval) and trunc(de_tipo_config.datfineval) "
					+ " and ts_config_loc.codtipoconfig = '" + codTipoConfig + "'";
			command = dataConnection.createStatement();

			dr = command.executeQuery(statement);

			while (dr.next()) {
				valoreConfig = dr.getString(1);
			}
			return valoreConfig;

		} catch (Exception ex) {
			log.fatal("Errore nella lettura della configurazione " + codTipoConfig, ex);
			return valoreConfig;
		} finally {
			try {
				if (dr != null) {
					dr.close();
				}
				if (command != null) {
					command.close();
				}
				if (dataConnection != null) {
					dataConnection.close();
				}
			} catch (Exception ex) {
				log.fatal("Eccezione nella chiusura delle connessioni al db ", ex);
			}
		}
	}

	// SETTER
	/**
	 * @deprecated
	 */
	public void setDataSourceJndi(String _dataSourceJndi) {
		dataSourceJndi = _dataSourceJndi;
	}

	/**
	 * @deprecated
	 * @param endPointName
	 * @throws Exception
	 */
	public void setName(String endPointName) throws Exception {
		name = endPointName;
		fetchUrl();
	}

}
