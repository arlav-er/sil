/*
 * Creato il 12-May-04
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.ejbUtils.db;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */

public class EjbDbConnection {

	private static final Logger log = Logger.getLogger(EjbDbConnection.class.getName());
	private javax.naming.InitialContext ctx = null;
	private String dataSourceJndi = null;
	private Connection dataConnection = null;

	public EjbDbConnection() {
		try {
			ctx = new javax.naming.InitialContext();
			dataSourceJndi = (String) ctx.lookup("java:comp/env/DBdatasource");
		} catch (Exception sex) {
			log.fatal("Eccezione nell'inizializzazione del contesto JNDI " + sex.getMessage());
			sex.printStackTrace();
		}
	}

	// GETTER CONNECTION

	public Connection getConnection() {
		try {
			DataSource dbDS = (DataSource) ctx.lookup(dataSourceJndi);
			dataConnection = dbDS.getConnection();

		} catch (Exception sex) {
			sex.printStackTrace();
			// System.out.println("EjbDbConnection:: Exception: " + sex.getMessage());
			log.fatal("Eccezione nell'accesso al datasource " + sex.getMessage());
		}
		return dataConnection;
	}

	public String getDataSourceJndi() {
		return dataSourceJndi;
	}

}
