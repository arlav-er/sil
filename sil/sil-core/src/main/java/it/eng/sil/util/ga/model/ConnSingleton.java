/**
 * Mantiene le info relative alla connessione (è un SINGLETON).
 * @author Luigi Antenucci
 */
package it.eng.sil.util.ga.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnSingleton {
	// GG: per essere un Singleton, la classe deve essere final

	// ----------------------------- PARTE DI GESTIONE "SINGLETON"
	// --------------------------

	private static ConnSingleton instance = null;

	// GG: per essere un Singleton, la classe deve avere una variabile privata
	// che contiene il riferimento all'unica istanza creata.

	private ConnSingleton() {
		// GG: per essere un Singleton, il costruttore deve essere privato!

		// nulla da inizializzare
	}

	public static ConnSingleton getInstance() {
		// GG: per essere un Singleton, deve avere il metodo di classe
		// "getInstance" per recuperare l'unica istanza.

		if (instance == null) {
			// accesso protetto da monitor di classe
			synchronized (ConnSingleton.class) {
				if (instance == null) {
					// invocazione del costruttore (protetto)
					instance = new ConnSingleton();
				} // if
			} // synchronized
		} // if

		return instance; // rende l'istanza

	} // getInstance()

	// --------------------------------- OGGETTO VERO E PROPRIO
	// ---------------------------------

	// PER ACCEDERE AL DB:
	private static final String NO_CONNESSIONE = "jdbc:oracle:thin:@<HostName>:1521:<Sid>";
	private String stringaDiConnessione = NO_CONNESSIONE;
	// NOTA sintassi generale:
	// "jdbc:oracle:<drivertype>:<user>/<password>@<database>"
	// con <database> = HostName:PortNumber:Sid (solo per driver THIN)
	// oppure: "jdbc:oracle:oci8:@(description=(address=(host=
	// myhost)(protocol=tcp)(port=1521))(connect_data=(sid=orcl)))"
	private String username = "<username>";
	private String password = "<password>";
	private String nomeClasseDriver = "oracle.jdbc.driver.OracleDriver";

	/**
	 * Rende la connessione al DB
	 */
	public Connection getConnection() throws Exception {
		try {

			if ((stringaDiConnessione == null) || (stringaDiConnessione.length() == 0)
					|| stringaDiConnessione.equalsIgnoreCase(NO_CONNESSIONE)) {
				throw new SQLException("ConnSingleton::getConnection hasn't a valid connection string!");
			}

			Class.forName(nomeClasseDriver); // Carica la classe del DRIVER

			Connection conn; // Crea una nuova connessione
			if ((username == null) || (username.equals("")))
				conn = DriverManager.getConnection(stringaDiConnessione);
			else
				conn = DriverManager.getConnection(stringaDiConnessione, username, password);

			return conn;
		} catch (ClassNotFoundException cnfex) {
			System.err.println("ConnSingleton::getConnection can't find the Class Driver '" + nomeClasseDriver + "'!");
			throw cnfex;
		} catch (Exception ex) {
			System.err.println("ConnSingleton::getConnection failed; reason: " + ex.getMessage() + "!");
			throw ex;
		}
	}

	public String getStringaDiConnessione() {
		return stringaDiConnessione;
	}

	public void setStringaDiConnessione(String stringaDiConnessione) {
		this.stringaDiConnessione = stringaDiConnessione;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNomeClasseDriver() {
		return nomeClasseDriver;
	}

	public void setNomeClasseDriver(String nomeClasseDriver) {
		this.nomeClasseDriver = nomeClasseDriver;
	}
}