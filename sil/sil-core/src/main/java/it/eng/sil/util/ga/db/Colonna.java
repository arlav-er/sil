package it.eng.sil.util.ga.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Astrae le informazioni relative a una colonna di una tabella
 * 
 * @author Luigi Antenucci
 */

public class Colonna {

	private String nome = "";
	private int tipo = 0; // vedere java.sql.Types.*
	private String nometipo = ""; // "VARCHAR2", "NUMBER", "DATE", ecc..
	private int dimensione = 0;
	private boolean nullabile = false;
	private int posizione = -1;
	private String commento = "";
	private String nomeschema = "";
	private String nometabella = "";

	// Parte relativa ai dati di PRIMARY KEY (se isPK = true)
	private boolean isPK = false;
	private short pksequanza = -1;
	private String pknome = "";

	// Parte relativa ai dati di FOREIGN KEY (se isFK = true)
	private boolean isFK = false;
	private short fksequanza = -1;
	private String fknome = "";
	private String fknometabella = "";
	private String fknomecolonna = "";

	private Colonna() {
	}

	/**
	 * Recupera i dati di UNA colonna
	 */
	public Colonna(Connection conn, String pSchema, String pTabella, String pColonna) throws SQLException {

		ResultSet rsCol = null;
		try {

			DatabaseMetaData connMeta = conn.getMetaData();

			rsCol = connMeta.getColumns(null, pSchema, pTabella, pColonna);
			if (rsCol.next()) {
				setFromResultSet(rsCol);
			}
			rsCol.close();
			/*
			 * Esamina la tabella "nomeTabellaInit" e rende una tabella con le seg.colonne: TABLE_CAT String => table
			 * catalog (may be null) TABLE_SCHEM String => table schema (may be null) TABLE_NAME String => table name
			 * COLUMN_NAME String => column name DATA_TYPE short => SQL type from java.sql.Types TYPE_NAME String =>
			 * Data source dependent type name, for a UDT the type name is fully qualified COLUMN_SIZE int => column
			 * size. For char or date types this is the maximum number of characters, for numeric or decimal types this
			 * is precision. DECIMAL_DIGITS int => the number of fractional digits NUM_PREC_RADIX int => Radix
			 * (typically either 10 or 2) NULLABLE int => is NULL allowed? - columnNoNulls - might not allow NULL values
			 * - columnNullable - definitely allows NULL values - columnNullableUnknown - nullability unknown REMARKS
			 * String => comment describing column (may be null) COLUMN_DEF String => default value (may be null)
			 * CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column ORDINAL_POSITION int =>
			 * index of column in table (starting at 1) IS_NULLABLE String => "NO" means column definitely does not
			 * allow NULL values; "YES" means the column might allow NULL values. An empty string means nobody knows.
			 */

			// Parte addizionale per recuperare info sulla CHIAVE PRIMARIA (PK)
			// della tabella ---------
			rsCol = connMeta.getPrimaryKeys(null, pSchema, pTabella);
			setFromResultSetPK(rsCol, this);
			rsCol.close();
			/*
			 * Tabella resa: TABLE_CAT String => table catalog (may be null) TABLE_SCHEM String => table schema (may be
			 * null) TABLE_NAME String => table name COLUMN_NAME String => column name KEY_SEQ short => sequence number
			 * within primary key PK_NAME String => primary key name (may be null)
			 */

			// Parte addizionale per recuperare info sulle CHIAVI FORESTIERE
			// (FK) della tabella ---------
			rsCol = connMeta.getImportedKeys(null, pSchema, pTabella);
			setFromResultSetFK(rsCol, this);
			rsCol.close();
			/*
			 * Tabella resa: PKTABLE_CAT String => primary key table catalog being imported (may be null) PKTABLE_SCHEM
			 * String => primary key table schema being imported (may be null) PKTABLE_NAME String => primary key table
			 * name being imported PKCOLUMN_NAME String => primary key column name being imported FKTABLE_CAT String =>
			 * foreign key table catalog (may be null) FKTABLE_SCHEM String => foreign key table schema (may be null)
			 * FKTABLE_NAME String => foreign key table name FKCOLUMN_NAME String => foreign key column name KEY_SEQ
			 * short => sequence number within foreign key UPDATE_RULE short => What happens to foreign key when primary
			 * is updated: importedNoAction - do not allow update of primary key if it has been imported
			 * importedKeyCascade - change imported key to agree with primary key update importedKeySetNull - change
			 * imported key to NULL if its primary key has been updated importedKeySetDefault - change imported key to
			 * default values if its primary key has been updated importedKeyRestrict - same as importedKeyNoAction (for
			 * ODBC 2.x compatibility) DELETE_RULE short => What happens to the foreign key when primary is deleted.
			 * importedKeyNoAction - do not allow delete of primary key if it has been imported importedKeyCascade -
			 * delete rows that import a deleted key importedKeySetNull - change imported key to NULL if its primary key
			 * has been deleted importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x compatibility)
			 * importedKeySetDefault - change imported key to default if its primary key has been deleted FK_NAME String
			 * => foreign key name (may be null) PK_NAME String => primary key name (may be null) DEFERRABILITY short =>
			 * can the evaluation of foreign key constraints be deferred until commit importedKeyInitiallyDeferred - see
			 * SQL92 for definition importedKeyInitiallyImmediate - see SQL92 for definition importedKeyNotDeferrable -
			 * see SQL92 for definition
			 */

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsCol != null)
				try {
					rsCol.close();
				} catch (SQLException e) {
				}
		}
	} // costruttore

	/**
	 * Rende tutte le Colonne della Tabella passato. La List contiene tanti oggetti di classe Colonna quante sono le
	 * colonne della tabella.
	 */
	public static List getColonneTabella(Connection conn, String pSchema, String pTabella) throws SQLException {

		List arrColonne = new ArrayList();
		ResultSet rsCol = null;

		try {

			// FV 17/07/2004
			// recupero commenti

			Map colsMap = new HashMap();
			Statement statement = conn.createStatement();

			rsCol = statement.executeQuery("SELECT COLUMN_NAME, COMMENTS FROM ALL_COL_COMMENTS " + " WHERE OWNER = user"
					+ " AND TABLE_NAME = '" + pTabella + "'");

			while (rsCol.next()) {
				String nomeCol = rsCol.getString("COLUMN_NAME");
				String commento = rsCol.getString("COMMENTS");
				if (commento == null)
					commento = "";
				colsMap.put(nomeCol, commento);
			}

			rsCol.close();

			DatabaseMetaData connMeta = conn.getMetaData();
			rsCol = connMeta.getColumns(null, pSchema, pTabella, null);

			while (rsCol.next()) { // Spulcia tutte le info sulla tabella

				Colonna colonna = new Colonna();

				colonna.setFromResultSet(rsCol);

				colonna.setCommento((String) colsMap.get(colonna.getNome().toUpperCase()));
				arrColonne.add(colonna);
			}
			rsCol.close();

			// Parte addizionale per recuperare info sulla CHIAVE PRIMARIA (PK)
			// della tabella ---------
			rsCol = connMeta.getPrimaryKeys(null, pSchema, pTabella);
			setFromResultSetPK(rsCol, arrColonne);
			rsCol.close();

			// Parte addizionale per recuperare info sulle CHIAVI FORESTIERE
			// (FK) della tabella ---------
			rsCol = connMeta.getImportedKeys(null, pSchema, pTabella);
			setFromResultSetFK(rsCol, arrColonne);
			rsCol.close();

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsCol != null)
				try {
					rsCol.close();
				} catch (SQLException e) {
				}
		}

		return arrColonne;
	}

	/**
	 * Rende le sole stringhe con i nomi di tutte le tabelle dello schema dato La List contiene tanti oggetti di classe
	 * String
	 */
	public static List getNomiColonneTabella(Connection conn, String pSchema, String pTabella) throws SQLException {

		List arrNomiColonne = new ArrayList();
		ResultSet rsCol = null;

		try {

			DatabaseMetaData connMeta = conn.getMetaData();
			rsCol = connMeta.getColumns(null, pSchema, pTabella, null);

			while (rsCol.next()) { // Spulcia tutte le info sulla tabella

				arrNomiColonne.add(rsCol.getString("COLUMN_NAME"));
			}
			rsCol.close();

		} catch (SQLException ex) {
			throw ex;
		} finally {
			// in ogni caso (ok o eccezione) chiude gli eventuali oggetti aperti
			if (rsCol != null)
				try {
					rsCol.close();
				} catch (SQLException e) {
				}
		}

		return arrNomiColonne;
	}

	/**
	 * INTERNA! Nota: recupera il "rs" corrente (occorre aver già fatto il "next")
	 */
	private void setFromResultSet(ResultSet rsCol) throws SQLException {

		nome = rsCol.getString("COLUMN_NAME");
		tipo = rsCol.getInt("DATA_TYPE");
		nometipo = rsCol.getString("TYPE_NAME");
		dimensione = rsCol.getInt("COLUMN_SIZE");
		nullabile = (rsCol.getString("IS_NULLABLE")).equalsIgnoreCase("YES");
		posizione = rsCol.getInt("ORDINAL_POSITION");
		commento = rsCol.getString("REMARKS");
		nomeschema = rsCol.getString("TABLE_SCHEM");
		nometabella = rsCol.getString("TABLE_NAME");

		if (nome == null)
			nome = "";
		if (nometipo == null)
			nometipo = "";
		if (commento == null)
			commento = "";
		if (nomeschema == null)
			nomeschema = "";
		if (nometabella == null)
			nometabella = "";
	}

	/**
	 * INTERNA! Come la precedente ma per la parte relativa alla PrimaryKey (PK) INTERNA! Come la precedente ma per la
	 * parte relativa alla ForeignKey (FK) Il secondo parametro può essere un oggetto di classe Colonna oppure una lista
	 * (List) di oggetti Colonna (e verrà scorsa per recuperare l'oggetto che ha il nome uguale a quello letto di volta
	 * in volta dal recordset).
	 */
	private static void setFromResultSetPK(ResultSet rsCol, Object colonna) throws SQLException {
		while (rsCol.next()) {

			String nomeCol = rsCol.getString("COLUMN_NAME");
			// Recupero la colonna già salvata con quel nome e le aggiungo le
			// info
			Colonna myColonna = getColonnaConNome(colonna, nomeCol);

			if (myColonna != null) {

				myColonna.isPK = true;
				myColonna.pksequanza = rsCol.getShort("KEY_SEQ");
				myColonna.pknome = rsCol.getString("PK_NAME");

				if (myColonna.pknome == null)
					myColonna.pknome = "";

			}
		}
	}

	/**
	 * INTERNA! Come la precedente ma per la parte relativa alla ForeignKey (FK) Il secondo parametro può essere un
	 * oggetto di classe Colonna oppure una lista (List) di oggetti Colonna (e verrà scorsa per recuperare l'oggetto che
	 * ha il nome uguale a quello letto di volta in volta dal recordset).
	 */
	private static void setFromResultSetFK(ResultSet rsCol, Object colonna) throws SQLException {
		while (rsCol.next()) {

			String nomeCol = rsCol.getString("FKCOLUMN_NAME");
			// Recupero la colonna già salvata con quel nome e le aggiungo le
			// info
			Colonna myColonna = getColonnaConNome(colonna, nomeCol);

			if (myColonna != null) {

				myColonna.isFK = true;
				myColonna.fksequanza = rsCol.getShort("KEY_SEQ");
				myColonna.fknome = rsCol.getString("FK_NAME");
				myColonna.fknometabella = rsCol.getString("PKTABLE_NAME");
				myColonna.fknomecolonna = rsCol.getString("PKCOLUMN_NAME");

				if (myColonna.fknome == null)
					myColonna.fknome = "";
				if (myColonna.fknometabella == null)
					myColonna.fknometabella = "";
				if (myColonna.fknomecolonna == null)
					myColonna.fknomecolonna = "";

			}
		}
	}

	/**
	 * INTERNA. Il secondo parametro può essere un oggetto di classe Colonna oppure una lista (List) di oggetti Colonna
	 * (e verrà scorsa per recuperare l'oggetto che ha il nome uguale a quello letto di volta in volta dal recordset).
	 */
	private static Colonna getColonnaConNome(Object arrColonne, String nomeColonna) {
		if (arrColonne == null)
			return null;
		if (arrColonne instanceof Colonna) {
			Colonna myColonna = (Colonna) arrColonne;
			if (nomeColonna.equalsIgnoreCase(myColonna.getNome()))
				return myColonna;
		} else if (arrColonne instanceof List) {
			Iterator iter = ((List) arrColonne).iterator();
			while (iter.hasNext()) {
				Colonna myColonna = (Colonna) iter.next();
				if (nomeColonna.equalsIgnoreCase(myColonna.getNome()))
					return myColonna;
			}
		}
		return null;
	} // getColonnaConNome

	public String getNome() {
		return nome;
	}

	public int getTipo() {
		return tipo;
	}

	public String getNometipo() {
		return nometipo;
	}

	public int getDimensione() {
		return dimensione;
	}

	public boolean isNullabile() {
		return nullabile;
	}

	public int getPosizione() {
		return posizione;
	}

	public String getCommento() {
		return commento;
	}

	public String getNomeschema() {
		return nomeschema;
	}

	public String getNomeTabella() {
		return nometabella;
	}

	public boolean isPK() {
		return isPK;
	}

	public short getPksequanza() {
		return pksequanza;
	}

	public String getPknome() {
		return pknome;
	}

	public boolean isFK() {
		return isFK;
	}

	public short getFksequanza() {
		return fksequanza;
	}

	public String getFknome() {
		return fknome;
	}

	public String getFknometabella() {
		return fknometabella;
	}

	public String getFknomecolonna() {
		return fknomecolonna;
	}

	/**
	 * @param string
	 */
	public void setCommento(String string) {
		commento = string;
	}

}