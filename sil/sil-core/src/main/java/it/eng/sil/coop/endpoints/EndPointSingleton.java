/*
 * Creato il 05-Apr-06
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.endpoints;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * @author rolfini
 */
public class EndPointSingleton {

	private static final Logger log = Logger.getLogger(EndPointSingleton.class.getName());
	private static EndPointSingleton _instance = null;
	private String dataSourceJndi = null;

	public static EndPointSingleton getInstance(String dataSourceJndi) {
		if (_instance == null) {
			synchronized (EndPointSingleton.class) {
				if (_instance == null) {
					try {
						_instance = new EndPointSingleton();
						_instance.setDataSourceJndi(dataSourceJndi);
						// Savino 20/11/2006: le informazioni vanno lette ad ogni richiesta di url o di lista delle
						// province in cooperazione.
						// Chiamo comunque i due nuovi metodi per controllare che le operazioni vengano portate a
						// termine correttamente e per
						// mantenere inalterato il comportamento della chiamata di istanza del singleton.

						// _instance.load();
						_instance.loadEndPoints();
						_instance.loadProvinceInCoop();
					} // try
					catch (Exception ex) {
						// System.out.println("SILEndPointSingleton: Errore nelle procedure di reperimento degli
						// endpoint" + ex.getMessage());
						log.fatal("Errore nelle procedure di reperimento degli endpoint", ex);
						_instance = null;
					}
				}
			}
		}
		return _instance;
	}

	/*
	 * private void load() throws Exception {
	 * 
	 * 
	 * if (dataSourceJndi.equals("")) throw new
	 * Exception("Impossibile inizializzare la connessione al db: JNDI datasource nullo");
	 * 
	 * //attivo la connessione try { javax.naming.InitialContext ctx = new javax.naming.InitialContext(); DataSource
	 * dbDS = (DataSource) ctx.lookup(dataSourceJndi); dataConnection=dbDS.getConnection(); } catch (Exception dbEx) {
	 * log.fatal("Eccezione nell'accesso al db utilizzando il datasource " +dbEx.getMessage()); dbEx.printStackTrace();
	 * throw new Exception("Eccezione nell'accesso al database " + dbEx.getMessage()); }
	 * 
	 * 
	 * 
	 * try {
	 * 
	 * 
	 * 
	 * String statement = "SELECT strName, strUrl, codProvincia FROM TS_ENDPOINT"; command =
	 * dataConnection.createStatement();
	 * 
	 * dr = command.executeQuery(statement);
	 * 
	 * endPoints=new HashMap(); provinceInCoop=new HashSet();
	 * 
	 * while (dr.next()) { EndPointBean endPoint=new EndPointBean(); String name = dr.getString(1); String url =
	 * dr.getString(2); String provincia=dr.getString(3);
	 * 
	 * endPoint.setName(name); endPoint.setUrl(url);
	 * 
	 * endPoints.put(name, endPoint);
	 * 
	 * //inserisco la provincia letta dal db //alla fine del ciclo avrà l'insieme (Set) delle //province che sono
	 * effettivamente in cooperazione //con *questo* sistema (proprio perché censite nella tabella degli endpoint) if
	 * (provincia!=null) { //scarto i valori nulli provinceInCoop.add(dr.getString(3)); } }
	 * 
	 * } catch (Exception ex) {
	 * //System.out.println("SILEndPointSingleton: errore nell'inizializzazione della lista degli endpoint " +
	 * ex.getMessage()); log.fatal("Errore nell'inizializzazione della lista degli endpoint " + ex.getMessage());
	 * ex.printStackTrace();
	 * 
	 * 
	 * 
	 * } finally { dr.close(); command.close(); dataConnection.close(); }
	 * 
	 * }
	 */
	// Savino 20/11/2006: aggiunto metodo
	/**
	 * @return La map di EndPointBean che rappreta tutte le righe della tabella ts_endpoint.
	 * @throws Exception
	 *             se si verifica una eccezione nel recupero della data connection
	 */
	private Map<String, EndPointBean> loadEndPoints() throws Exception {

		Connection dataConnection = null;
		ResultSet dr = null;
		Statement command = null;

		if (dataSourceJndi.equals(""))
			throw new Exception("Impossibile inizializzare la connessione al db: JNDI datasource nullo");

		// attivo la connessione
		try {
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			DataSource dbDS = (DataSource) ctx.lookup(dataSourceJndi);
			dataConnection = dbDS.getConnection();
		} catch (Exception dbEx) {
			log.fatal("Eccezione nell'accesso al db utilizzando il datasource", dbEx);

			throw new Exception("Eccezione nell'accesso al database", dbEx);
		}

		try {

			// Savino 20/11/2006: aggiunto campo flgpoloattivo
			String statement = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT";
			command = dataConnection.createStatement();

			dr = command.executeQuery(statement);

			Map<String, EndPointBean> endPoints = new HashMap<String, EndPointBean>();

			while (dr.next()) {
				EndPointBean endPoint = new EndPointBean();
				String name = dr.getString(1);
				String url = dr.getString(2);
				String provincia = dr.getString(3);
				String flgPoloAttivo = dr.getString(4);
				endPoint.setName(name);
				endPoint.setUrl(url);
				// Savino 20/11/2006: aggiunti due campi al bean EndPointBean
				endPoint.setCodProvincia(provincia);
				endPoint.setFlgPoloAttivo(flgPoloAttivo);

				endPoints.put(name, endPoint);
			}
			return endPoints;

		} catch (Exception ex) {
			// System.out.println("SILEndPointSingleton: errore nell'inizializzazione della lista degli endpoint " +
			// ex.getMessage());
			log.fatal("Errore nell'inizializzazione della lista degli endpoint", ex);
			// Savino 20/11/2006: in precendenza nel metodo load() se si verificava un errore la proprieta' endPoint
			// restava vuota e non venivano rilanciate eccezioni..
			// per cui ritorno una HashMap vuota
			return new HashMap<String, EndPointBean>();
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

	// Savino 20/11/2006: aggiunto metodo
	/**
	 * @return l'insieme dei codProvincia in cooperazione
	 * @throws Exception
	 *             se si verifica una eccezione nel recupero della data connection
	 */
	private Set loadProvinceInCoop() throws Exception {

		Connection dataConnection = null;
		ResultSet dr = null;
		Statement command = null;

		if (dataSourceJndi.equals(""))
			throw new Exception("Impossibile inizializzare la connessione al db: JNDI datasource nullo");

		// attivo la connessione
		try {
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			DataSource dbDS = (DataSource) ctx.lookup(dataSourceJndi);
			dataConnection = dbDS.getConnection();
		} catch (Exception dbEx) {
			log.fatal("Eccezione nell'accesso al db utilizzando il datasource ", dbEx);
			throw new Exception("Eccezione nell'accesso al database " + dbEx.getMessage());
		}

		try {
			String statement = "SELECT codProvincia FROM TS_ENDPOINT";
			command = dataConnection.createStatement();

			dr = command.executeQuery(statement);

			Set provinceInCoop = new HashSet();

			while (dr.next()) {
				String provincia = dr.getString(1);

				// inserisco la provincia letta dal db
				// alla fine del ciclo avrà l'insieme (Set) delle
				// province che sono effettivamente in cooperazione
				// con *questo* sistema (proprio perché censite nella tabella degli endpoint)
				if (provincia != null) { // scarto i valori nulli
					provinceInCoop.add(dr.getString(1));
				}
			}
			return provinceInCoop;
		} catch (Exception ex) {
			// System.out.println("SILEndPointSingleton: errore nell'inizializzazione della lista degli endpoint " +
			// ex.getMessage());
			log.fatal("Errore nell'inizializzazione della lista degli endpoint", ex);
			// ex.printStackTrace();
			// Savino 20/11/2006: in precendenza nel metodo load() se si verificava un errore la proprieta' province
			// restava vuota e non venivano rilanciate eccezioni..
			// per cui ritorno una HashSet vuota
			return new HashSet();
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

	public String getUrl(String endPointName) {
		// Savino 20/11/2006: gli endpoints vanno letti ad ogni richiesta di url (vanificando il pattern singleton!)
		EndPointBean endPoint = null;
		try {
			endPoint = (EndPointBean) loadEndPoints().get(endPointName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (endPoint == null) {
			log.warn("End point [" + endPointName + "]  non trovato");
			return null;
		}

		return endPoint.getUrl();
	}

	/**
	 * @author Pegoraro
	 * 
	 * @param endPointName
	 * @return true se il flag vale 'S'
	 */
	public boolean isActive(String endPointName) {
		EndPointBean endPoint = null;
		try {
			endPoint = (EndPointBean) loadEndPoints().get(endPointName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (endPoint == null) {
			log.warn("End point [" + endPointName + "]  non trovato");
			return false;
		}

		String polo = endPoint.getFlgPoloAttivo();
		if ("S".equalsIgnoreCase(polo))
			return true;
		else
			return false;
	}

	/**
	 * Restituisce l'insieme (praticamente una lista) delle province che sono effettivamente in cooperazione con questo
	 * sistema (codProvincia is not null)
	 * 
	 * @return
	 */
	public Set getProvinceInCoop() {
		// Savino 20/11/2006: la lista delle province va letta ad ogni chiamata.
		Set provinceInCoop = null;
		try {
			provinceInCoop = loadProvinceInCoop();
		} catch (Exception e) {
			e.printStackTrace();
			provinceInCoop = new HashSet();
		}
		return provinceInCoop;

	}

	protected void setDataSourceJndi(String _dataSourceJndi) {
		this.dataSourceJndi = _dataSourceJndi;

	}

}
