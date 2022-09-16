package it.eng.afExt.utils;

import java.io.Serializable;

/**
 * Classe di utilità per la creazione degli statement in modo dinamico. Utile nelle classi per fare query dinamiche,
 * cioè quelle che implementano l'interfaccia IDynamicStatementProvider.
 * 
 * NB: non ha metodi statici ma è proprio un oggetto da istanziare e usare per mantenere la query in fase di
 * manipolazione.
 * 
 * @author Luigi Antenucci
 */
public class DynamicStatementUtils implements Serializable, Cloneable {

	/* pubbliche, da usare come parametri di input dei metodi di questa classe */
	public static final String OPERATOR_no = ""; // nessun operatore!
	public static final String OPERATOR_UGUALE = "=";
	public static final String OPERATOR_DIVERSO = "<>";
	public static final String OPERATOR_MAGGIORE = ">";
	public static final String OPERATOR_MINORE = "<";
	public static final String OPERATOR_MAGUGU = ">=";
	public static final String OPERATOR_MINUGU = "<=";

	public static final String OPERATOR_LIKE = "LIKE";

	public static final String FUNCTION_no = null;
	public static final String FUNCTION_UPPER = "UPPER";

	public static final int DO_LIKE_no = 0;
	public static final int DO_LIKE_INIZIA = 1;
	public static final int DO_LIKE_FINISCE = 2;
	public static final int DO_LIKE_CONTIENE = 4;

	public static final String FORMAT_DATE = "DD/MM/YYYY";
	public static final String FORMAT_TIME = "HH24:MI:SS";
	public static final String FORMAT_DATETIME = "DD/MM/YYYY HH24:MI:SS";

	/* private, usate per costruire la query SQL */
	private static final String PREFISSO_SELECT_0 = "SELECT";
	private static final String PREFISSO_SELECT_1 = ", ";

	private static final String PREFISSO_FROM_0 = " FROM ";
	private static final String PREFISSO_FROM_1 = ", ";

	private static final String PREFISSO_WHERE_0 = " WHERE ";
	private static final String PREFISSO_WHERE_1 = " AND   ";

	private static final String PREFISSO_ORDER_0 = " ORDER BY ";
	private static final String PREFISSO_ORDER_1 = ", ";

	private static final String LIKE_SYMBOL = "%";

	private static final String BETWEEN_INST = " BETWEEN ";
	private static final String BETWEEN_CONN = " AND ";

	// Stato dell'elaborazione
	private StringBuffer sqlSelect;
	private StringBuffer sqlFrom;
	private StringBuffer sqlWhere;
	private StringBuffer sqlOrder;
	// Clausola da usare alla prossima "add"
	private String prefissoSelect;
	private String prefissoFrom;
	private String prefissoWhere;
	private String prefissoOrder;

	public DynamicStatementUtils() {
		clear();
	}

	public void clear() {
		sqlSelect = new StringBuffer();
		sqlFrom = new StringBuffer();
		sqlWhere = new StringBuffer();
		sqlOrder = new StringBuffer();
		prefissoSelect = PREFISSO_SELECT_0;
		prefissoFrom = PREFISSO_FROM_0;
		prefissoWhere = PREFISSO_WHERE_0;
		prefissoOrder = PREFISSO_ORDER_0;
	}

	/**
	 * Clonazione Rende una copia profonda dell'oggetto. Tutte le stringhe di select-from-where vengono copiate nel
	 * nuovo oggetto (non si riferiranno alla medesima). Sovrascrive il metodo "clone" della classe Object
	 */
	public Object clone() {
		try {
			DynamicStatementUtils copy = (DynamicStatementUtils) super.clone();

			copy.sqlSelect = new StringBuffer(this.sqlSelect.toString());
			copy.sqlFrom = new StringBuffer(this.sqlFrom.toString());
			copy.sqlWhere = new StringBuffer(this.sqlWhere.toString());
			copy.sqlOrder = new StringBuffer(this.sqlOrder.toString());
			// in "prefissoXxx" ho stringhe (sono riferimenti già copiati dal
			// "super.clone()")

			return copy;
		} catch (CloneNotSupportedException e) {
			throw new Error("This should not occur since we implement Cloneable");
		}
	}

	/**
	 * setXxx: imposta (la parte "Xxx") con l'intero sql passato, senza l'aggiunta automatica di prefissi. Lo stato dei
	 * prefissi non viene neppure aggiornato: occorre farlo manualmente chiamando la "setNonVuotaXxx" se necessario.
	 */
	public void setSelect(String sql) {
		sqlSelect = new StringBuffer(sql);
	}

	public void setFrom(String sql) {
		sqlFrom = new StringBuffer(sql);
	}

	public void setWhere(String sql) {
		sqlWhere = new StringBuffer(sql);
	}

	public void setOrder(String sql) {
		sqlOrder = new StringBuffer(sql);
	}

	/**
	 * addToXxx: aggiunge (alla parte "Xxx") l'intero pezzo di sql passato, senza l'aggiunta automatica di prefissi. Lo
	 * stato dei prefissi non viene neppure aggiornato: occorre farlo manualmente chiamando la "setNonVuotaXxx" se
	 * necessario.
	 */
	public void addToSelect(String sql) {
		sqlSelect.append(sql);
	}

	public void addToFrom(String sql) {
		sqlFrom.append(sql);
	}

	public void addToWhere(String sql) {
		sqlWhere.append(sql);
	}

	public void addToOrder(String sql) {
		sqlOrder.append(sql);
	}

	/**
	 * getXxx: rende la parte "Xxx" così com'è correntemente definita.
	 */
	public String getSelect() {
		return sqlSelect.toString();
	}

	public String getFrom() {
		return sqlFrom.toString();
	}

	public String getWhere() {
		return sqlWhere.toString();
	}

	public String getOrder() {
		return sqlOrder.toString();
	}

	/**
	 * Rende l'intero statement sql (la composizione delle tre parti rese dalle "getXxx")
	 */
	public String getStatement() {
		StringBuffer temp = new StringBuffer();
		temp.append(sqlSelect.toString());
		temp.append(' ');
		temp.append(sqlFrom.toString());
		temp.append(' ');
		temp.append(sqlWhere.toString());
		temp.append(' ');
		temp.append(sqlOrder.toString());
		return temp.toString();
	}

	/**
	 * setNonVuotaXxx: imposta la generazione automatica dei prefissi (per la parte "Xxx") in modo tale che che venga
	 * generato il secondo tipo (come se la parte "Xxx" fosse "non vuota", ossia con già almeno un elemento). Per es.
	 * nella "Where" c'è già la "where", verrà usato l'"and".
	 */
	public void setNonVuotaSelect() {
		prefissoSelect = PREFISSO_SELECT_1;
	}

	public void setNonVuotaFrom() {
		prefissoFrom = PREFISSO_FROM_1;
	}

	public void setNonVuotaWhere() {
		prefissoWhere = PREFISSO_WHERE_1;
	}

	public void setNonVuotaOrder() {
		prefissoOrder = PREFISSO_ORDER_1;
	}

	/**
	 * Aggiunge alla parte di "select" una clausola del tipo: SELECT/, sqlField Note: - per il prefisso si usa SELECT o
	 * "," a seconda dello stato di "select-non-vuota" (vedi "setNonVuotaSelect");
	 */
	public void addSelect(String sqlField) {

		sqlSelect.append(prefissoSelect);
		setNonVuotaSelect(); // per le volte successive ho ","

		sqlSelect.append(sqlField);
	}

	/**
	 * Aggiunge alla parte di "from" una clausola del tipo: FROM/, sqlTable Note: - per il prefisso si usa FROM o "," a
	 * seconda dello stato di "from-non-vuota" (vedi "setNonVuotaFrom");
	 */
	public void addFrom(String sqlTable) {

		sqlFrom.append(prefissoFrom);
		setNonVuotaFrom(); // per le volte successive ho ","

		sqlFrom.append(sqlTable);
	}

	/**
	 * Aggiunge alla parte di "order by" una clausola del tipo: ORDER BY/, sqlTable Note: - per il prefisso si usa
	 * "ORDER BY" o "," a seconda dello stato di "order-non-vuota" (vedi "setNonVuotaOrder");
	 */
	public void addOrder(String sqlTable) {

		sqlOrder.append(prefissoOrder);
		setNonVuotaOrder(); // per le volte successive ho ","

		sqlOrder.append(sqlTable);
	}

	/**
	 * Aggiunge alla parte di "where" una clausola del tipo: WHERE/AND sqlClause Note: - per il prefisso si usa WHERE o
	 * AND a seconda dello stato di "where-non-vuota" (vedi "setNonVuotaWhere");
	 */
	public void addWhere(String sqlClause) {

		sqlWhere.append(prefissoWhere);
		setNonVuotaWhere(); // per le volte successive ho "AND"

		sqlWhere.append(sqlClause);
	}

	/**
	 * Aggiunge alla parte di "where" una clausola del tipo: WHERE/AND function(sqlField) operator function(paramValue)
	 * Note: - per il prefisso si usa WHERE o AND a seconda dello stato di "where-non-vuota" (vedi metodo
	 * "setNonVuotaWhere"); - il "IfFilled" indica che se paramValue è null o stringa vuota, non viene fatto nulla!
	 * Ossia al ritorno, la parte "where" non è stata alterata! - il parametro isString indica (se true) che paramValue
	 * è una stringa e non un numero (e che quindi che bisogna farne l'escape e usarlo tra apici), - il parametro
	 * operator indica l'operazione, come "=", "<>", "LIKE", ecc; - il parametro function indica un'eventuale funzione a
	 * unico argomento (per es. "UPPER") da applicare sia all'sqlField sia al paramValue. E' facoltativa: se manca non
	 * la si usa.
	 */
	public void addWhereIfFilled(String sqlField, String paramValue, boolean isString, String operator,
			String function) {

		if (StringUtils.isEmpty(paramValue))
			return;

		boolean o = StringUtils.isFilled(operator);
		boolean f = StringUtils.isFilled(function);
		boolean s = isString;

		// escaping del valore (es. raddoppio apice)
		if (s)
			paramValue = StringUtils.formatValue4Sql(paramValue);

		// prefisso:
		sqlWhere.append(prefissoWhere);
		setNonVuotaWhere(); // per le volte successive ho "AND"

		if (f)
			sqlWhere.append(function).append('(');
		sqlWhere.append(sqlField);
		if (f)
			sqlWhere.append(')');
		if (o)
			sqlWhere.append(' ').append(operator);
		sqlWhere.append(' ');
		if (f)
			sqlWhere.append(function).append('(');
		if (s)
			sqlWhere.append('\'');
		sqlWhere.append(paramValue);
		if (s)
			sqlWhere.append('\'');
		if (f)
			sqlWhere.append(')');
	}

	/**
	 * Caso particolare di "addWhereIfFilled" con "function" non definita: WHERE/AND sqlField operator paramValue
	 */
	public void addWhereIfFilled(String sqlField, String paramValue, boolean isString, String operator) {
		addWhereIfFilled(sqlField, paramValue, isString, operator, FUNCTION_no);
	}

	/**
	 * Caso particolare di "addWhereIfFilled" con "function" non definita e operatore "=": WHERE/AND sqlField =
	 * paramValue
	 */
	public void addWhereIfFilled(String sqlField, String paramValue, boolean isString) {
		addWhereIfFilled(sqlField, paramValue, isString, OPERATOR_UGUALE, FUNCTION_no);
	}

	/**
	 * "addWhereIfFilled" specifica per i numeri. Ci sono anche le specializzazioni senza "funzione" e anche con
	 * operatore "=".
	 */
	public void addWhereIfFilledNum(String sqlField, String paramValue, String operator, String function) {
		addWhereIfFilled(sqlField, paramValue, false, operator, function);
	}

	public void addWhereIfFilledNum(String sqlField, String paramValue, String operator) {
		addWhereIfFilled(sqlField, paramValue, false, operator, FUNCTION_no);
	}

	public void addWhereIfFilledNum(String sqlField, String paramValue) {
		addWhereIfFilled(sqlField, paramValue, false, OPERATOR_UGUALE, FUNCTION_no);
	}

	/**
	 * "addWhereIfFilledNum" specifica per i numeri in un INTERVALLO "da-a". Genera del codice SQL differente a seconda
	 * di quali parametri sono sono riempiti (non vuoti o nulli): - se sono ambo riempiti, - se sono identici, genera:
	 * AND sqlField = paramValueDa - altrimenti, genera: AND sqlField BETWEEN paramValueDa AND paramValueA - altrimenti,
	 * a) se riempito paramValueDa: AND sqlField >= paramValueDa b) se riempito paramValueA: AND sqlField <= paramValueA
	 */
	public void addWhereIfFilledNumBetween(String sqlField, String paramValueDa, String paramValueA) {
		// Se ambo riempiti, uso costrutto "BETWEEN da TO a", sennò uso coppia
		// di "AND" condizionale
		if (StringUtils.isFilled(paramValueDa) && StringUtils.isFilled(paramValueA)) {
			if (paramValueDa.equals(paramValueA)) {
				// Nel caso di AMBO I CAMPI UGUALI, trasformo predicato in "="
				// per un solo parametro
				addWhereIfFilledNum(sqlField, paramValueDa);
			} else {
				addWhere(sqlField + BETWEEN_INST + paramValueDa + BETWEEN_CONN + paramValueA);
			}
		} else {
			addWhereIfFilledNum(sqlField, paramValueDa, OPERATOR_MAGUGU);
			addWhereIfFilledNum(sqlField, paramValueA, OPERATOR_MINUGU);
		}
	}

	/**
	 * Come la "addWhereIfFilledNumBetween", ma se il parametro "paramValueA" *NON* è riempito genera il solo codice SQL
	 * del tipo "AND (sqlField >= paramValueDa OR sqlField IS NULL)" così da recuperare anche i campi a NULL. In realtà
	 * genera la versione ottimizzata: AND NVL(sqlField,paramValueDa) >= paramValueDa Nota: se "paramValueA" è riempito
	 * si comporta come la "addWhereIfFilledNumBetween".
	 */
	public void addWhereIfFilledNumBetweenOrNull(String sqlField, String paramValueDa, String paramValueA) {

		// Se "paramValueA" è riempito, uso normale "addWhereIfFilledNumBetween"
		if (StringUtils.isFilled(paramValueA)) {
			addWhereIfFilledNumBetween(sqlField, paramValueDa, paramValueA);
		} else {
			if (StringUtils.isFilled(paramValueDa)) { // paramValueDa riempito
														// ma paramValueA nullo:
				addWhere("NVL(" + sqlField + "," + paramValueDa + ") " + OPERATOR_MAGUGU + paramValueDa);
			}
			// altrimenti, non faccio nulla (nessun parametro è stato riempito)
		}
	}

	/**
	 * "addWhereIfFilled" specifica per le stringhe. Ci sono anche le specializzazioni senza "funzione" e anche con
	 * operatore "=".
	 */
	public void addWhereIfFilledStr(String sqlField, String paramValue, String operator, String function) {
		addWhereIfFilled(sqlField, paramValue, true, operator, function);
	}

	public void addWhereIfFilledStr(String sqlField, String paramValue, String operator) {
		addWhereIfFilled(sqlField, paramValue, true, operator, FUNCTION_no);
	}

	public void addWhereIfFilledStr(String sqlField, String paramValue) {
		addWhereIfFilled(sqlField, paramValue, true, OPERATOR_UGUALE, FUNCTION_no);
	}

	/**
	 * Specializzazione di "addWhereIfFilledStr" con funzione di "UPPER" e operatore "=".
	 */
	public void addWhereIfFilledStrUpper(String sqlField, String paramValue) {
		addWhereIfFilled(sqlField, paramValue, true, OPERATOR_UGUALE, FUNCTION_UPPER);
	}

	/**
	 * Aggiunge una clausola del tipo: WHERE/AND function(sqlField) LIKE function('paramValue') A seconda del
	 * "doLikeType" vengono aggiunti i simboli di "%" alla stringa paramValue.
	 */
	public void addWhereIfFilledStrLike(String sqlField, String paramValue, String function, int doLikeType) {

		if (StringUtils.isEmpty(paramValue))
			return;

		switch (doLikeType) {
		case DO_LIKE_no:
			break;
		case DO_LIKE_INIZIA:
			paramValue = paramValue + LIKE_SYMBOL;
			break;
		case DO_LIKE_FINISCE:
			paramValue = LIKE_SYMBOL + paramValue;
			break;
		case DO_LIKE_CONTIENE:
			paramValue = LIKE_SYMBOL + paramValue + LIKE_SYMBOL;
			break;
		}
		addWhereIfFilledStr(sqlField, paramValue, OPERATOR_LIKE, function);
	}

	public void addWhereIfFilledStrLikeUpper(String sqlField, String paramValue, int doLikeType) {
		addWhereIfFilledStrLike(sqlField, paramValue, FUNCTION_UPPER, doLikeType);
	}

	/**
	 * privata - dato un campo DATA, rende la stringa SQL che lo formatta come una nuova data nel formato passato.
	 */
	private String getToDateField(String sqlField, String format) {
		// Piccola ottimizzazione per le date.
		if ((format == FORMAT_DATE) || format.equalsIgnoreCase(FORMAT_DATE))
			return " TRUNC(" + sqlField + ") "; // tronca la data al giorno
		else
			return " TO_DATE(TO_CHAR(" + sqlField + ",'" + format + "'),'" + format + "') ";
	}

	/**
	 * privata - dato un parametro STRINGA, rende la stringa SQL che lo formatta come una data nel formato passato.
	 */
	private String getToDateParam(String paramValue, String format) {
		return " TO_DATE('" + paramValue + "','" + format + "') ";
	}

	/**
	 * "addWhereIfFilled" specifica per le STRINGHE contenenti una DATA in formato (di default DD/MM/YYYY). Il paramtero
	 * facoltativo "format" può contenere il formato (vedi FORMAT_XXX).
	 */
	public void addWhereIfFilledDate(String sqlField, String paramValue, String operator, String format) {

		if (StringUtils.isFilled(paramValue)) {
			addWhere(getToDateField(sqlField, format) + operator + getToDateParam(paramValue, format));
		}
	}

	public void addWhereIfFilledDate(String sqlField, String paramValue) {
		addWhereIfFilledDate(sqlField, paramValue, OPERATOR_UGUALE, FORMAT_DATE);
	}

	public void addWhereIfFilledDate(String sqlField, String paramValue, String operator) {
		addWhereIfFilledDate(sqlField, paramValue, operator, FORMAT_DATE);
	}

	/**
	 * "addWhereIfFilledDate" specifica per le date in un INTERVALLO "da-a". Genera del codice SQL differente a seconda
	 * di quali parametri sono sono riempiti (non vuoti o nulli): - se sono ambo riempiti, - se sono identici, genera:
	 * AND sqlField = paramValueDa - altrimenti, genera: AND sqlField BETWEEN paramValueDa AND paramValueA - altrimenti,
	 * a) se riempito paramValueDa: AND sqlField >= paramValueDa b) se riempito paramValueA: AND sqlField <= paramValueA
	 */
	public void addWhereIfFilledDateBetween(String sqlField, String paramValueDa, String paramValueA) {
		addWhereIfFilledDateBetween(sqlField, paramValueDa, paramValueA, FORMAT_DATE);
	}

	public void addWhereIfFilledDateBetween(String sqlField, String paramValueDa, String paramValueA, String format) {

		// Se ambo riempiti, uso costrutto "BETWEEN da TO a", sennò uso coppia
		// di "AND" condizionale
		if (StringUtils.isFilled(paramValueDa) && StringUtils.isFilled(paramValueA)) {
			if (paramValueDa.equals(paramValueA)) {
				// Nel caso di AMBO I CAMPI UGUALI, trasformo predicato in "="
				// per un solo parametro
				addWhereIfFilledDate(sqlField, paramValueDa, OPERATOR_UGUALE, format);
			} else {
				addWhere(getToDateField(sqlField, format) + BETWEEN_INST + getToDateParam(paramValueDa, format)
						+ BETWEEN_CONN + getToDateParam(paramValueA, format));
			}
		} else {
			addWhereIfFilledDate(sqlField, paramValueDa, OPERATOR_MAGUGU, format);
			addWhereIfFilledDate(sqlField, paramValueA, OPERATOR_MINUGU, format);
		}
	}

	/**
	 * Come la "addWhereIfFilledDateBetween", ma se il parametro "paramValueA" *NON* è riempito genera il solo codice
	 * SQL del tipo "AND (sqlField >= paramValueDa OR sqlField IS NULL)" così da recuperare anche i campi a NULL. In
	 * realtà genera la versione ottimizzata: AND NVL(sqlField,paramValueDa) >= paramValueDa Nota: se "paramValueA" è
	 * riempito si comporta come la "addWhereIfFilledDateBetween".
	 */
	public void addWhereIfFilledDateBetweenOrNull(String sqlField, String paramValueDa, String paramValueA) {
		addWhereIfFilledDateBetweenOrNull(sqlField, paramValueDa, paramValueA, FORMAT_DATE);
	}

	public void addWhereIfFilledDateBetweenOrNull(String sqlField, String paramValueDa, String paramValueA,
			String format) {

		// Se "paramValueA" è riempito, uso normale
		// "addWhereIfFilledDateBetween"
		if (StringUtils.isFilled(paramValueA)) {
			addWhereIfFilledDateBetween(sqlField, paramValueDa, paramValueA, format);
		} else {
			if (StringUtils.isFilled(paramValueDa)) { // paramValueDa riempito
														// ma paramValueA nullo:
				// In realtà, anziché comporre un
				// "(sqlField >= paramValueDa OR sqlField IS NULL)"
				// genero un
				// "NVL(sqlField,paramValueDa) >= paramValueDa)"
				String paramValueDaFrm = getToDateParam(paramValueDa, format);
				addWhere("NVL(" + getToDateField(sqlField, format) + "," + paramValueDaFrm + ") " + OPERATOR_MAGUGU
						+ paramValueDaFrm);
			}
			// altrimenti, non faccio nulla (nessun parametro è stato riempito)
		}
	}

}
