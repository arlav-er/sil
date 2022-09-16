package it.eng.sil.util.ga.model;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import it.eng.sil.util.ga.db.Colonna;
import it.eng.sil.util.ga.db.Tabella;

/**
 * Crea le query relative a una tabella
 * 
 * @author Luigi Antenucci
 */

public class GenQuery {

	private static final String ALIAS_TABELLA_PRINCIPALE = "A";
	private static final String ALIAS_TABELLA_DECODIFICA = "D";

	private static final String CAPOLINEA = "\n";
	private static final String CAPOLINEA_SELECT = "\n       ";
	private static final String CAPOLINEA_WHERE = "\n ";

	private static final String BEGIN_SELECT = "SELECT ";
	private static final String BEGIN_INSERT = "INSERT INTO ";
	private static final String BEGIN_INSERT_VALUES = " VALUES ";
	private static final String BEGIN_UPDATE = "UPDATE ";
	private static final String UPDATE_SET = " SET ";
	private static final String UPDATE_SET_OPERATOR = "=";
	private static final String BEGIN_DELETE = "DELETE FROM ";
	private static final String BEGIN_FROM = " FROM  ";
	private static final String BEGIN_WHERE = " WHERE ";

	private static final String PARAMETER = "?";

	private static final String SEPARATORE_SELECT = ", ";
	private static final String SEPARATORE_FROM = ", ";
	private static final String SEPARATORE_WHERE = " AND ";

	private static final String DEFINE_ALIAS_SELECT = " ";
	private static final String RIFERIM_ALIAS_SELECT = ".";
	private static final String JOIN_KEY_WHERE = "=";
	private static final String OUTER_JOIN_WHERE = "(+)=";

	private Tabella tabella;
	private Connection conn;

	private String select = "";
	private String insert = "";
	private String update = "";
	private String delete = "";
	private String selectDecoded = "";
	private boolean noWhere = false;

	/**
	 * Inizializzazione. Crea le query di SELECT, INSERT, UPDATE e DELETE (ma NON quella di SELECT-DECODED poiché serve
	 * un oggetto "Connection").
	 */
	public GenQuery(Tabella tabella) {
		this(tabella, null);
	}

	/**
	 * Inizializzazione. Crea le query di SELECT, INSERT, UPDATE, DELETE e di SELECT-DECODED.
	 */
	public GenQuery(Tabella tabella, Connection conn) {
		this.tabella = tabella;
		this.conn = conn;
		// generaStatement();
	}

	/**
	 * Genera gli statement di SELECT (semplice), INSERT, UPDATE e DELETE. Le stringhe con gli statement creati possono
	 * essere recuperate tramite i metodi getSelect(), getInsert(), getUpdate() e getDelete().
	 */
	public void generaStatement() {

		String nomeTabella = tabella.getNome();
		List arrColonne = tabella.getArrColonne();

		if (nomeTabella.equals(""))
			return;

		// -- Creazione della query di SELECT,INSERT,UPDATE e DELETE
		// ---------------

		select = BEGIN_SELECT;
		insert = BEGIN_INSERT + nomeTabella + " (";
		update = BEGIN_UPDATE + nomeTabella + " SET ";
		delete = BEGIN_DELETE + nomeTabella;

		String sep = ""; // separatore per SELECT
		String sepU = ""; // separatore per UPDATE
		String sepW = ""; // separatore per WHERE
		String insertParams = ""; // Sequenza dei parametri per l'INSERT
		String whereKeys = BEGIN_WHERE; // per la clausola "WHERE keys=.."
		Iterator iter = arrColonne.iterator();
		while (iter.hasNext()) {

			Colonna colonna = (Colonna) iter.next();

			// imposta il parametro a "?" o qualcosa di analogo
			String paramSet = getParamSet(colonna.getTipo());
			String nomeColonna = colonna.getNome();

			select += sep + nomeColonna;
			insert += sep + nomeColonna;
			insertParams += sep + paramSet;
			if (sep.equals(""))
				sep = SEPARATORE_SELECT;

			if (!colonna.isPK()) { // nota: non faccio UPDATE dei campi CHIAVE
				update += sepU + nomeColonna + UPDATE_SET_OPERATOR + paramSet;
				if (sepU.equals(""))
					sepU = SEPARATORE_SELECT;
			}

			if (colonna.isPK()) { // creo parte WHERE con le chiavi
				String paramWhere = getParamWhere(colonna.getTipo());
				whereKeys += sepW + nomeColonna + JOIN_KEY_WHERE + paramWhere;
				if (sepW.equals(""))
					sepW = SEPARATORE_WHERE;
			}

		}
		if (noWhere) {
			whereKeys = "";
		}

		select += BEGIN_FROM + nomeTabella + whereKeys;
		insert += ")" + BEGIN_INSERT_VALUES + "(" + insertParams + ")"; // niente
																		// WHERE
																		// per
																		// INSERT
		update += whereKeys;
		delete += whereKeys;
	}

	/**
	 * Genera lo statement di SELECT DECODIFICATA, ossia una SELECT che recupera anche tutti i dati relativi alle
	 * tabelle referenziate (ossia ai campi che nella tabella corrente sono delle chiavi forestiere - Foreign Keys). La
	 * stringa con lo statement creato può essere recuperata tramite il metodo getSelectDecoded(). Il parametro
	 * "livelloMax" indica la profondità della decodifica; se si passa uno "0" si otterrà la semplice query di select
	 * normale (senza nessuna decodifica); se si passa "1" si otterrà la decodifica relative a tutte le tabelle
	 * referenziate di primo livello. Se si passa un "2" si otterrà la decodifica anche di tutte le tabelle incluse
	 * precedentemente. E così via. NB: è necessario aver passato una "Connection" al costruttore di questo oggetto
	 * poiché l'algoritmo deve recuperare le info di tutte le altre tabelle.
	 */
	public void generaSelectDecoded(int livelloMax) {

		if (conn != null) {
			try {
				int posSelect = 0; // Indicatore di posizione per stabilire il
									// separatore per SELECT
				int posFrom = 0; // Indicatore di posizione per stabilire il
									// separatore per FROM
				int posWhere = 0; // Indicatore di posizione per stabilire il
									// separatore per WHERE

				StringBuffer clauseSelect = new StringBuffer(); // per la parte
																// di "SELECT
																// campi.."
				StringBuffer clauseFrom = new StringBuffer(); // per la parte
																// di "FROM
																// tabelle.."
				StringBuffer clauseWhere = new StringBuffer(); // per la parte
																// di "WHERE
																// chiave"

				riempiPezziClausola(tabella, clauseSelect, posSelect, clauseFrom, posFrom, clauseWhere, posWhere,
						livelloMax);

				selectDecoded = BEGIN_SELECT + clauseSelect + CAPOLINEA + BEGIN_FROM + clauseFrom + CAPOLINEA
						+ BEGIN_WHERE + clauseWhere;

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * INTERNA. Usa la ricorsione per creare i pezzi di clausole del la SELECT-DECODED.
	 */
	private void riempiPezziClausola(Tabella tabella, StringBuffer clauseSelect, int posSelect, StringBuffer clauseFrom,
			int posFrom, StringBuffer clauseWhere, int posWhere, int livelloMax) throws SQLException {
		riempiPezziClausola(tabella, clauseSelect, posSelect, clauseFrom, posFrom, clauseWhere, posWhere, 0, livelloMax,
				ALIAS_TABELLA_PRINCIPALE);
	}

	private void riempiPezziClausola(Tabella tabella, StringBuffer clauseSelect, int posSelect, StringBuffer clauseFrom,
			int posFrom, StringBuffer clauseWhere, int posWhere, int livello, int livelloMax, String aliasTabella)
			throws SQLException {

		String nomeTabella = tabella.getNome();
		List arrColonne = tabella.getArrColonne();

		livello++;
		int decodifica = 0;

		// Clausola di "FROM"
		if ((++posFrom) > 1)
			clauseFrom.append(SEPARATORE_FROM); // aggiunta del separatore
		clauseFrom.append(nomeTabella);
		clauseFrom.append(DEFINE_ALIAS_SELECT);
		clauseFrom.append(aliasTabella);

		// Ciclo per tutte le colonne
		Iterator iter = arrColonne.iterator();
		boolean suPrimaColonna = true;
		while (iter.hasNext()) {

			Colonna colonna = (Colonna) iter.next();

			String nomeColonna = aliasTabella + RIFERIM_ALIAS_SELECT + colonna.getNome(); // "TABELLA.COLONNA"

			// Clausola di "SELECT"
			if ((++posSelect) > 1)
				clauseSelect.append(SEPARATORE_SELECT);
			if ((livello > 1) && suPrimaColonna)
				clauseSelect.append(CAPOLINEA_SELECT);
			String paramSelect = getParamSelect(colonna.getTipo(), nomeColonna);
			clauseSelect.append(paramSelect);

			// Clausola di "WHERE" (usando la CHIAVE ma solo per la tabella
			// PRIMARIA
			if (colonna.isPK() && (livello == 1)) {
				if ((++posWhere) > 1)
					clauseWhere.append(SEPARATORE_WHERE);
				clauseWhere.append(nomeColonna);
				clauseWhere.append(JOIN_KEY_WHERE);
				String paramWhere = getParamWhere(colonna.getTipo());
				clauseWhere.append(paramWhere);
			}

			// Aggiunta parte relativa alla FOREIGN KEY: decodifica e ricorsione
			if (colonna.isFK()) {
				if (livello <= livelloMax) {
					// NB: la 2' e la 3' condizione serve per forzare
					// l'interruzione della ricorsione

					String nomeTabellaForestiera = colonna.getFknometabella();
					decodifica++;

					String aliasTabellaFutura; // Crea nuovo ALIAS per tabela
												// futura
					if (livello == 1)
						aliasTabellaFutura = ALIAS_TABELLA_DECODIFICA + decodifica;
					else
						aliasTabellaFutura = aliasTabella + decodifica;

					clauseWhere.append(CAPOLINEA_WHERE);

					if ((++posWhere) > 1)
						clauseWhere.append(SEPARATORE_WHERE);
					clauseWhere.append(aliasTabellaFutura);
					clauseWhere.append(RIFERIM_ALIAS_SELECT);
					clauseWhere.append(colonna.getFknomecolonna());
					clauseWhere.append(OUTER_JOIN_WHERE);
					clauseWhere.append(nomeColonna);

					// Crea la tabella di decodifica e la esamina in maniera
					// RICORSIVA!
					Tabella tabellaDec = new Tabella(conn, tabella.getNomeschema(), nomeTabellaForestiera);
					riempiPezziClausola(tabellaDec, clauseSelect, posSelect, clauseFrom, posFrom, clauseWhere, posWhere,
							livello, livelloMax, aliasTabellaFutura);
				}
			} // if FK

			suPrimaColonna = true;
		} // for
	}

	/**
	 * INTERNA: rende il parametro a "?" (o una sua rielaborazione in base al "tipoColonna") per essere usato nella
	 * parte di "SET" di "UPDATE" o di "INSERT". Es. "SET DESC=?"
	 */
	private String getParamSet(int tipoColonna) {

		String nomeCampo = PARAMETER;
		switch (tipoColonna) {
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return "TO_DATE(" + nomeCampo + ",'DD/MM/YYYY')";
		default:
			return nomeCampo;
		}
	}

	/**
	 * INTERNA: rende il parametro a "?" (o una sua rielaborazione in base al "tipoColonna") per essere usato nella
	 * parte di "WHERE" di tutte le query. Es. "WHERE DESC=?", "WHERE DATA < TO_DATE(?,'DD/MM/YYYY')"
	 */
	private String getParamWhere(int tipoColonna) {

		return getParamSet(tipoColonna);
	}

	/**
	 * INTERNA: rende il parametro "nomeCampo" passato (o una sua rielaborazione in base al "tipoColonna") per essere
	 * usato nella parte di "SELECT" di tutte le query. Es. "SELECT DESC", "SELECT TO_CHAR(MIADATA,'DD/MM/YYYY')"
	 */
	private String getParamSelect(int tipoColonna, String nomeCampo) {

		switch (tipoColonna) {
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return "TO_CHAR(" + nomeCampo + ",'DD/MM/YYYY')";
		default:
			return nomeCampo;
		}
	}

	public String getSelect() {
		return select;
	}

	public String getInsert() {
		return insert;
	}

	public String getUpdate() {
		return update;
	}

	public String getDelete() {
		return delete;
	}

	public String getSelectDecoded() {
		return selectDecoded;
	}

	private final String getXmlTag(int livello, boolean isOpen, String nomeTag) {
		String ret = "";
		for (int i = 0; i < livello; i++)
			ret += "    ";

		ret += "<" + (isOpen ? "" : "/") + nomeTag + ">\n";
		return ret;
	}

	private final String getXmlTag(int livello, String nomeTag, String valoreTag) {
		String ret = "";
		for (int i = 0; i < livello; i++)
			ret += "    ";

		if ((valoreTag == null) || (valoreTag.trim().length() == 0)) {
			ret += "<" + nomeTag + "/>\n";
		} else {
			ret += "<" + nomeTag + ">" + valoreTag + "</" + nomeTag + ">\n";
		}
		return ret;
	}

	private final String getXmlTag(int livello, String nomeTag, int valoreTag) {
		return getXmlTag(livello, nomeTag, String.valueOf(valoreTag));
	}

	private final String getXmlTag(int livello, String nomeTag, boolean valoreTag) {
		return getXmlTag(livello, nomeTag, String.valueOf(valoreTag));
	}

	private String convertSqlToJava(String nomeSql, boolean firstLetterUp) {
		if (nomeSql == null)
			return nomeSql;

		StringBuffer nomeJava = new StringBuffer();

		String nome = nomeSql.toLowerCase(); // tutto in minuscolo (per ora)

		if ((nome.length() > 2) && (nome.charAt(1) == '_')) {
			nome = nome.substring(2); // tolgo i primi 2 caratteri se tipo
										// "x_"
		}

		boolean doUp = firstLetterUp;
		int pos = 0;
		while (pos < nome.length()) {
			char carPos = nome.charAt(pos);
			switch (carPos) {
			case '_': {
				doUp = true; // il prossimo car. sarà in maiuscolo
				break;
			}
			default: {
				if (doUp) {
					carPos = Character.toUpperCase(carPos);
					doUp = false;
				}
				nomeJava.append(carPos);
			}
			}
			pos++;
		}
		return nomeJava.toString();
	}

	public void writeToXml(PrintStream printStream) {

		StringBuffer str = new StringBuffer();
		str.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		str.append("<?xml-stylesheet type=\"text/xsl\" href=\"GenDAOjava.xsl\"?>\n");

		str.append(getXmlTag(0, true, "schema"));
		str.append(getXmlTag(1, "nome", tabella.getNomeschema()));

		str.append(getXmlTag(1, true, "tabella"));
		str.append(getXmlTag(2, "nome", tabella.getNome()));
		str.append(getXmlTag(2, "nomeJava", this.convertSqlToJava(tabella.getNome(), false)));
		str.append(getXmlTag(2, "nomeJavaUp", this.convertSqlToJava(tabella.getNome(), true)));
		str.append(getXmlTag(2, "tipo", tabella.getTipo()));
		str.append(getXmlTag(2, "commento", tabella.getCommento()));

		str.append(getXmlTag(2, true, "chiavi"));
		str.append("\n");
		printStream.print(str.toString());

		List arrColonne = tabella.getArrColonne();

		Iterator iterCol = arrColonne.iterator();
		while (iterCol.hasNext()) {

			Colonna colonna = (Colonna) iterCol.next();

			if (colonna.isPK()) {
				str = new StringBuffer();
				str.append(getXmlTag(3, true, "chiave"));
				str.append(getXmlTag(4, "nome", colonna.getNome()));
				str.append(getXmlTag(4, "nomeJava", this.convertSqlToJava(colonna.getNome(), false)));
				str.append(getXmlTag(4, "nomeJavaUp", this.convertSqlToJava(colonna.getNome(), true)));
				str.append(getXmlTag(4, "tipo", colonna.getTipo()));
				str.append(getXmlTag(4, "nometipo", colonna.getNometipo()));
				str.append(getXmlTag(4, "dimensione", colonna.getDimensione()));
				str.append(getXmlTag(4, "nullabile", colonna.isNullabile()));
				str.append(getXmlTag(4, "posizione", colonna.getPosizione()));
				str.append(getXmlTag(4, "commento", colonna.getCommento()));
				str.append("\n");
				// inutile: str.append(getXmlTag(4, "isPK", colonna.isPK()));
				str.append(getXmlTag(4, "pksequanza", colonna.getPksequanza()));
				str.append(getXmlTag(4, "pknome", colonna.getPknome()));
				str.append("\n");
				str.append(getXmlTag(4, "isFK", colonna.isFK()));
				str.append(getXmlTag(4, "fksequanza", colonna.getFksequanza()));
				str.append(getXmlTag(4, "fknome", colonna.getFknome()));
				str.append(getXmlTag(4, "fknometabella", colonna.getFknometabella()));
				str.append(getXmlTag(4, "fknomecolonna", colonna.getFknomecolonna()));
				str.append(getXmlTag(3, false, "chiave"));
				printStream.print(str.toString());
			}
		}

		str = new StringBuffer();
		str.append("\n");
		str.append(getXmlTag(2, false, "chiavi"));
		str.append(getXmlTag(2, true, "colonne"));
		str.append("\n");
		printStream.print(str.toString());

		iterCol = arrColonne.iterator();
		while (iterCol.hasNext()) {

			Colonna colonna = (Colonna) iterCol.next();

			str = new StringBuffer();
			str.append(getXmlTag(3, true, "colonna"));
			str.append(getXmlTag(4, "nome", colonna.getNome()));
			str.append(getXmlTag(4, "nomeJava", this.convertSqlToJava(colonna.getNome(), false)));
			str.append(getXmlTag(4, "nomeJavaUp", this.convertSqlToJava(colonna.getNome(), true)));
			str.append(getXmlTag(4, "tipo", colonna.getTipo()));
			str.append(getXmlTag(4, "nometipo", colonna.getNometipo()));
			str.append(getXmlTag(4, "dimensione", colonna.getDimensione()));
			str.append(getXmlTag(4, "nullabile", colonna.isNullabile()));
			str.append(getXmlTag(4, "posizione", colonna.getPosizione()));
			str.append(getXmlTag(4, "commento", colonna.getCommento()));
			str.append("\n");
			str.append(getXmlTag(4, "isPK", colonna.isPK()));
			str.append(getXmlTag(4, "pksequanza", colonna.getPksequanza()));
			str.append(getXmlTag(4, "pknome", colonna.getPknome()));
			str.append("\n");
			str.append(getXmlTag(4, "isFK", colonna.isFK()));
			str.append(getXmlTag(4, "fksequanza", colonna.getFksequanza()));
			str.append(getXmlTag(4, "fknome", colonna.getFknome()));
			str.append(getXmlTag(4, "fknometabella", colonna.getFknometabella()));
			str.append(getXmlTag(4, "fknomecolonna", colonna.getFknomecolonna()));
			str.append(getXmlTag(3, false, "colonna"));
			printStream.print(str.toString());
		}

		str = new StringBuffer();
		str.append("\n");
		str.append(getXmlTag(2, false, "colonne"));
		printStream.print(str.toString());

		str = new StringBuffer();
		str.append(getXmlTag(3, true, "GenQuery"));
		str.append(getXmlTag(4, "select", this.getSelect()));
		str.append(getXmlTag(4, "insert", this.getInsert()));
		str.append(getXmlTag(4, "update", this.getUpdate()));
		str.append(getXmlTag(4, "delete", this.getDelete()));
		printStream.print(str.toString());
		str = new StringBuffer();
		str.append(getXmlTag(4, true, "selectDecoded"));
		str.append(this.getSelectDecoded());
		str.append("\n");
		str.append(getXmlTag(4, false, "selectDecoded"));
		str.append(getXmlTag(3, false, "GenQuery"));
		printStream.print(str.toString());

		str = new StringBuffer();
		str.append(getXmlTag(1, false, "tabella"));
		str.append(getXmlTag(0, false, "schema"));
		printStream.print(str.toString());

	}

	/*
	 * ....GG... TO DO: XML+XSL --> JAVA try { StreamSource source = new StreamSource( new File( "book.xml" ) );
	 * StreamResult result = new StreamResult( System.out ); TransformerFactory factory =
	 * TransformerFactory.newInstance(); Transformer transformer = factory.newTransformer( new StreamSource( new File(
	 * "books.xsl" ) ) ); transformer.transform( source, result ); } catch( Exception e ) { e.printStackTrace(); }
	 */

	public boolean isNoWhere() {
		return noWhere;
	}

	public void setNoWhere(boolean b) {
		noWhere = b;
	}

}