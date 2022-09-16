package it.eng.sil.module.patto;

import it.eng.afExt.utils.LogUtils;

/**
 * Per le tabelle associate al patto tramite la tabella am_lav_patto_scelta questa classe permette la lettura dei record
 * associati o meno fondendo la query presente nel file di configurazione con una sotto-select
 * 
 */
public class StatementFromPatto implements SelectMerge {
	private static final String STM_ORDERBY = "";
	public final static String SELECT = "SELECT";
	public final static String FROM = "FROM";
	public final static String WHERE = "WHERE";
	public final static String ORDERBY = "ORDERBY";
	public final static String TAB_ASS = "tab_ass";
	private static final String STM_SELECT = "aps.prgpattolavoratore,am.datstipula,am.flgpatto297, am.datUltimoProtocollo, aps.datProtocollo, des.strdescrizione "
			+ "AS statoatto,aps.prglavpattoscelta,aps.strchiavetabella,am.codStatoAtto as codStatoPattoLavoratore, "
			+ "nvl(de_codifica_patto.strdescrizione, decode(am.FLGPATTO297, 'S', 'Patto 150', 'Accordo Generico')) as strDescrizionePatto ";
	private static final String STM_FROM = "am_lav_patto_scelta aps,am_patto_lavoratore am,de_stato_atto des,de_codifica_patto, ";
	private static final String STM_WHERE = "aps.prgpattolavoratore = am.prgpattolavoratore AND am.datfine IS NULL AND "
			+ "am.codstatoatto = des.codstatoatto AND am.codcodificapatto = de_codifica_patto.codcodificapatto(+)";
	/*
	 * private static final String STM_WHERE_AM = "AND NVL (" + TAB_ASS + ".datfine, SYSDATE) >= SYSDATE";
	 */
	private static final String STM_PARAMETER = "cdnlavoratore =";
	private static final String STM_IN_SELECT = "leg.prgpattolavoratore, TO_CHAR (leg.datstipula, 'dd/mm/yyyy') as datstipula,"
			+ "TO_CHAR (leg.datProtocollo, 'dd/mm/yyyy') as datProtocollo,"
			+ "leg.flgpatto297, leg.statoatto || ' / ' || TO_CHAR (leg.datUltimoProtocollo, 'dd/mm/yyyy') as statoAtto, nvl(leg.prglavpattoscelta,'-1') as prglavpattoscelta,"
			+ "leg.statoatto || ' / ' || TO_CHAR (leg.datProtocollo, 'dd/mm/yyyy') as statoAttoDatProtInf"
			+ " , leg.codStatoPattoLavoratore ";
	private static final String STM_IN_WHERE = "=leg.strchiavetabella(+)";
	private String table;
	private String codLstTab;
	private String keyName;
	private String paramValue;
	private String tableAlias;
	private boolean preselezione = true;
	private String otherCondition = "";
	private boolean neededMansione = false;

	/**
	 * @param table
	 *            tabella associata al patto (es. pr_mansione)
	 * @param tableAlias
	 *            alias associato alla tabella table (opzionale)
	 * @param codLstTab
	 *            codice della tabella associata al patto presente in am_lav_patto_scelta / de_lst_tab
	 * @param keyName
	 *            nome della chiave della tabella table (es. prgmansione)
	 * 
	 */
	public StatementFromPatto(String table, String tableAlias, String codLstTab, String keyName) {

		// Se "" assegno null perché internamente viene verificato il !null per
		// indicare la presenza dell'alias
		if ("".equals(tableAlias))
			tableAlias = null;

		this.table = table;
		this.codLstTab = codLstTab;
		this.keyName = keyName;
		this.tableAlias = tableAlias;
	}

	/**
	 * @param table
	 *            tabella associata al patto (es. pr_mansione)
	 * @param codLstTab
	 *            codice della tabella associata al patto presente in am_lav_patto_scelta / de_lst_tab
	 * @param keyName
	 *            nome della chiave della tabella table (es. prgmansione)
	 */
	public StatementFromPatto(String table, String codLstTab, String keyName) {
		this(table, null, codLstTab, keyName);
	}/*
		 * public StatementFromPatto(String table, String codLstTab, String keyName) { this(table, null, codLstTab,
		 * keyName, false); }
		 *//*
			 * public StatementFromPatto(String table,String tableAlias, String codLstTab, String keyName) { this(table,
			 * tableAlias, codLstTab, keyName); }
			 */

	/*
	 * public SourceBean getStmToBean() { SourceBean sb = null;
	 * 
	 * try { sb = new SourceBean("ROW"); sb.setAttribute(SELECT, getSelect()); sb.setAttribute(FROM, getFrom());
	 * sb.setAttribute(WHERE, getWhere()); sb.setAttribute(ORDERBY, getOrderBy()); } catch (Exception e) { }
	 * 
	 * return sb; }
	 */
	/**
	 * Nel caso in cui la tabella collegata non abbia il campo cdnLavoratore e sia legata anche a pr_mansione, chiamando
	 * questo metodo si ottiene un join fra queste due tabelle ed il controllo sul cdnLavoratore viene fatto con la
	 * tabella pr_mansione
	 * 
	 */
	public void neededMansione(boolean b) {
		this.neededMansione = b;
	}

	/**
	 * @param condition
	 *            una condizione da aggiungere alla select interna (es. "and AM_CM_ISCR.datdatafine is null") cosi' come
	 *            e' scritta nella query censita nel file statement.xml (occhio a scecificare la condizione)
	 * 
	 */
	public void addCondition(String condition) {
		if (tableAlias != null) {
			// StringBuffer sb = new StringBuffer(condition);
			int a = condition.indexOf(tableAlias + ".");
			String s1 = condition.substring(0, a);
			String s2 = condition.substring(a + tableAlias.length());
			condition = s1 + TAB_ASS + s2;
		} else {
			int a = condition.indexOf(table + ".");
			String s1 = condition.substring(0, a);
			String s2 = condition.substring(a + table.length());
			condition = s1 + TAB_ASS + s2;
		}
		this.otherCondition += condition;
	}

	private String getSelect() {
		return STM_SELECT;
	}

	private String getWhere() {
		StringBuffer stm = new StringBuffer(STM_WHERE);
		// if (this.)
		stm.append(" AND ");
		if (neededMansione) {
			stm.append("tab_man.cdnLavoratore=");
			stm.append(paramValue);
			stm.append(" AND tab_man.prgMansione=");
			stm.append(TAB_ASS);
			stm.append(".prgMansione");
		} else {
			stm.append(TAB_ASS);
			stm.append(".");
			stm.append(STM_PARAMETER);
			stm.append(paramValue);
		}
		stm.append("  ");
		stm.append("AND aps.strchiavetabella=");
		stm.append(TAB_ASS);
		stm.append(".");
		stm.append(keyName);
		stm.append(" AND (aps.codlsttab = '");
		stm.append(codLstTab);
		stm.append("') ");
		stm.append(otherCondition);

		return stm.toString();
	}

	private String getFrom() {
		StringBuffer sb = new StringBuffer();
		sb.append(STM_FROM);
		sb.append(" ");
		sb.append(table);
		sb.append(" ");
		sb.append(TAB_ASS);
		if (neededMansione) {
			sb.append(", pr_mansione tab_man");
		}
		return sb.toString();
	}

	private String getOrderBy() {
		return STM_ORDERBY;
	}

	private String getStatement() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(getSelect());
		sb.append(" FROM ");
		sb.append(getFrom());
		sb.append(" WHERE ");
		sb.append(getWhere());

		return sb.toString();
	}

	public String getSelectFields() {
		return STM_IN_SELECT;
	}

	public String getFromTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(getStatement());
		sb.append(") leg ");

		return sb.toString();
	}

	public String getWhereCondition() {
		StringBuffer sb = new StringBuffer();

		if (tableAlias == null) {
			sb.append(table);
		} else {
			sb.append(tableAlias);
		}

		sb.append(".");
		sb.append(keyName);
		sb.append(STM_IN_WHERE);

		return sb.toString();
	}

	public static void main(String[] st) {
		try {
			StatementFromPatto s = new StatementFromPatto("pr_esp_lavoro", "el", "PR_ESP_L", "prgesplavoro");
			s.setParameter("", "12");
			s.neededMansione(true);
			// s.addCondition("and pr_mansione.datdatafine is null");
			String select = "SELECT el.prgEspLavoro, dm.strdescrizione as desMansione,el.numannoinizio, el.numannofine,dc.strdescrizione desContratto FROM pr_esp_lavoro el,  pr_mansione m,  de_mansione dm, de_contratto dc  WHERE   m.cdnlavoratore=?  AND m.prgmansione=el.prgmansione AND m.codmansione=dm.codmansione  AND el.codcontratto=dc.codcontratto(+) ORDER BY el.numannofine desc";
			/*
			 * SourceBean sb = s.getStmToBean(); System.out.println(sb.getAttribute("SELECT"));
			 * System.out.println(sb.getAttribute("FROM")); System.out.println(sb.getAttribute("WHERE"));
			 * System.out.println(sb.getAttribute("ORDERBY"));
			 */
			//
			String p = s.merge(select);
			System.out.println(p);
		} catch (Exception e) {
			e.printStackTrace();
			;
		}

	}

	/**
	 * Viene utilizzato in QueryExecutor
	 * 
	 * @param s
	 *            lo statement in cui inglobare la sottoselect
	 */
	public String merge(String s) throws Exception {
		int iSelect = 0;
		int iFrom = 0;
		int iWhere = 0;
		int iGroupBy = 0;
		int iOrderBy = 0;
		String stm = s.toUpperCase();
		iSelect = stm.indexOf("SELECT");
		iFrom = stm.indexOf("FROM");
		iWhere = stm.indexOf("WHERE");
		iGroupBy = stm.indexOf("ORDER"); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
		iOrderBy = stm.indexOf("GROUP");

		String select = null;
		String from = null;
		String whereToEnd = null;
		String orderBy = null;
		String groupBy = null;

		if ((iSelect < 0) || (iFrom < 0) || (iWhere < 0)) {
			throw new Exception("Impossibile eseguire il merge: clausola SELECT, FROM o WHERE mancante");
		}

		//
		// select ......
		select = s.substring(0, iFrom);

		// from ........
		from = s.substring(iFrom, iWhere);

		/*
		 * int iEndWhere=0; if (iGroupBy>=0 && iOrderBy>=0) { iEndWhere = iGroupBy>iOrderBy ? iOrderBy:iGroupBy; orderBy
		 * = stm.substring(); } else if (iGroupBy<0 && iGroupBy<0) iEndWhere=stm.length(); else iEndWhere=iGroupBy<0 ?
		 * iOrderBy:iGroupBy; // where ...... where = s.substring(iWhere, iEndWhere);
		 * 
		 * if (iOrderBy>=0) d = s.substring();
		 * 
		 * 
		 */
		whereToEnd = s.substring(iWhere + "WHERE".length() + 1);

		StringBuffer sb = new StringBuffer();
		sb.append(select);
		sb.append(",");
		sb.append(getSelectFields());
		sb.append(" ");

		//
		sb.append(from);
		sb.append(",");
		sb.append(getFromTable());
		sb.append(" ");

		//
		sb.append("WHERE ");
		sb.append(getWhereCondition());
		sb.append(" and ");
		sb.append(whereToEnd);

		LogUtils.logDebug("merge", "statement [" + s + "] --> [" + sb.toString() + "]", this);

		return sb.toString();
	}

	/**
	 * E' fondamentale specificare il valore del cdnlavoratore(per il momento) da ricercare
	 * 
	 * @param name
	 *            il nome del parametro a cui associare il valore
	 * @param value
	 *            il valore del parametro
	 */
	public void setParameter(String name, String value) {

		if (name == null || value == null)
			throw new IllegalArgumentException("parametri query null");

		this.paramValue = value;
	}
}
