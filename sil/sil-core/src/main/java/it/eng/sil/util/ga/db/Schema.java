package it.eng.sil.util.ga.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Astrae le informazioni relative a una schema del database
 * 
 * @author Luigi Antenucci
 */

public class Schema {

	private String nome = "";

	private List arrTabelle = new ArrayList(); // contiene oggetti di classe
												// "Tabella"

	private Schema() {
	}

	/**
	 * nota: altera sia i dati dello schema sia quelli delle tabelle
	 */
	public Schema(Connection conn, String pSchema) throws SQLException {

		ResultSet rsSchema = null;
		try {

			DatabaseMetaData connMeta = conn.getMetaData();
			rsSchema = connMeta.getSchemas();
			/*
			 * Esamina il database e rende una tabella con le seg.colonne: TABLE_SCHEM String => table schema (may be
			 * null)
			 */
			if (rsSchema.next()) {

				setFromResultSet(rsSchema);

				// popola tutte le tabelle (usa il metodo di classe di Tabella)
				arrTabelle = Tabella.getTabelleSchema(conn, pSchema);

			}
		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsSchema != null)
				try {
					rsSchema.close();
				} catch (SQLException e) {
				}
		}
	} // costruttore

	/**
	 * Rende tutti gli schema del database La List contiene tanti oggetti di classe Schema quante sono gli schema del db
	 */
	public static List getSchemaDatabase(Connection conn) throws SQLException {

		List arrSchema = new ArrayList();
		ResultSet rsSchema = null;

		try {

			DatabaseMetaData connMeta = conn.getMetaData();
			rsSchema = connMeta.getSchemas();

			while (rsSchema.next()) { // Spulcia tutte le info sullo schema

				Schema schema = new Schema();

				schema.setFromResultSet(rsSchema);
				// popola tutte le colonne della schema (usa il metodo di classe
				// di Tabella)
				schema.arrTabelle = Tabella.getTabelleSchema(conn, schema.getNome());

				arrSchema.add(schema);
			}

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsSchema != null)
				try {
					rsSchema.close();
				} catch (SQLException e) {
				}
		}

		return arrSchema;
	}

	/**
	 * Rende le sole stringhe con i nomi di tutti gli schema del database La List contiene tanti oggetti di classe
	 * String
	 */
	public static List getNomiSchemaDatabase(Connection conn) throws SQLException {

		List arrNomiSchema = new ArrayList();

		ResultSet rsSchema = null;
		try {

			DatabaseMetaData connMeta = conn.getMetaData();
			rsSchema = connMeta.getSchemas();

			while (rsSchema.next()) { // Spulcia tutte le info sullo schema

				arrNomiSchema.add(rsSchema.getString("TABLE_SCHEM"));
			}

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsSchema != null)
				try {
					rsSchema.close();
				} catch (SQLException e) {
				}
		}

		return arrNomiSchema;
	}

	/**
	 * INTERNA! Nota: recupera il "rs" corrente (occorre aver già fatto il "next")
	 */
	private void setFromResultSet(ResultSet rsSchema) throws SQLException {

		nome = rsSchema.getString("TABLE_SCHEM");

		if (nome == null)
			nome = "";
	}

	public String getNome() {
		return nome;
	}

	public List getTabelle() {
		return arrTabelle;
	}

}