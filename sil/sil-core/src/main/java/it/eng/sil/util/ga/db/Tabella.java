package it.eng.sil.util.ga.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Astrae le informazioni relative a una tabella del database
 * 
 * @author Luigi Antenucci
 */

public class Tabella {

	private String nome = "";
	private String tipo = "";
	// es: "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL
	// TEMPORARY", "ALIAS", "SYNONYM".
	private String commento = "";
	private String nomeschema = "";

	private List arrColonne = new ArrayList(); // contiene oggetti di classe
												// "Colonna"

	private Tabella() {
	}

	/**
	 * nota: altera sia i dati della tabella sia quelli delle colonne
	 */
	public Tabella(Connection conn, String pSchema, String pTabella) throws SQLException {

		ResultSet rsTable = null;
		try {

			DatabaseMetaData connMeta = conn.getMetaData();
			rsTable = connMeta.getTables(null, pSchema, pTabella, null);
			/*
			 * Esamina la tabella "nomeTabellaInit" e rende una tabella con le seg.colonne: TABLE_CAT String => table
			 * catalog (may be null) TABLE_SCHEM String => table schema (may be null) TABLE_NAME String => table name
			 * TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
			 * "LOCAL TEMPORARY", "ALIAS", "SYNONYM". REMARKS String => explanatory comment on the table
			 */
			if (rsTable.next()) {

				setFromResultSet(rsTable);

				// popola tutte le colonne (usa il metodo di classe di Colonna)
				arrColonne = Colonna.getColonneTabella(conn, pSchema, pTabella);

			}

			rsTable.close();
			// FV 14/07/2004
			Statement statement = conn.createStatement();
			rsTable = statement.executeQuery("SELECT COMMENTS FROM ALL_TAB_COMMENTS " + " WHERE OWNER = user"
					+ " AND TABLE_NAME = '" + pTabella + "'");

			while (rsTable.next()) {
				String commentoTmp = rsTable.getString("COMMENTS");
				if (commentoTmp != null)
					commento = commentoTmp;

			}

			rsTable.close();

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsTable != null)
				try {
					rsTable.close();
				} catch (SQLException e) {
				}
		}
	} // costruttore

	/**
	 * Rende tutte le tabelle dello Schema passato La List contiene tanti oggetti di classe Tabella quante sono le
	 * tabelle dello schema.
	 */
	public static List getTabelleSchema(Connection conn, String pSchema) throws SQLException {

		List arrTabelle = new ArrayList();
		ResultSet rsTable = null;

		try {

			DatabaseMetaData connMeta = conn.getMetaData();
			rsTable = connMeta.getTables(null, pSchema, null, null);

			while (rsTable.next()) { // Spulcia tutte le info sullo schema

				Tabella tabella = new Tabella();

				tabella.setFromResultSet(rsTable);
				// popola tutte le colonne della tabella (usa il metodo di
				// classe di Colonna)
				tabella.arrColonne = Colonna.getColonneTabella(conn, pSchema, tabella.getNome());

				arrTabelle.add(tabella);
			}

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsTable != null)
				try {
					rsTable.close();
				} catch (SQLException e) {
				}
		}

		return arrTabelle;
	}

	/**
	 * Rende le sole stringhe con i nomi di tutte le tabelle dello schema dato La List contiene tanti oggetti di classe
	 * String
	 */
	public static List getNomiTabelleSchema(Connection conn, String pSchema) throws SQLException {

		List arrNomiTabelle = new ArrayList();
		ResultSet rsTable = null;

		try {

			DatabaseMetaData connMeta = conn.getMetaData();
			rsTable = connMeta.getTables(null, pSchema, null, null);

			while (rsTable.next()) { // Spulcia tutte le info sullo schema

				arrNomiTabelle.add(rsTable.getString("TABLE_NAME"));
			}

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsTable != null)
				try {
					rsTable.close();
				} catch (SQLException e) {
				}
		}

		return arrNomiTabelle;
	}

	/**
	 * INTERNA! Nota: recupera il "rs" corrente (occorre aver già fatto il "next")
	 */
	private void setFromResultSet(ResultSet rsTable) throws SQLException {

		nome = rsTable.getString("TABLE_NAME");
		tipo = rsTable.getString("TABLE_TYPE");
		commento = rsTable.getString("REMARKS");
		nomeschema = rsTable.getString("TABLE_SCHEM");

		if (nome == null)
			nome = "";
		if (tipo == null)
			tipo = "";
		if (commento == null)
			commento = "";
		if (nomeschema == null)
			nomeschema = "";
	}

	public String getNome() {
		return nome;
	}

	public String getTipo() {
		return tipo;
	}

	public String getCommento() {
		return commento;
	}

	public List getArrColonne() {
		return arrColonne;
	}

	public String getNomeschema() {
		return nomeschema;
	}

}