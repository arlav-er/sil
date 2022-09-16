package it.eng.sil.module;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.patto.SelectMerge;

/**
 * <p>
 * Classe che fornisce una implementazione standardizzata delle operazioni tipiche gestite dal modulo, quali
 * inserimento, cancellazione ecc.
 * </p>
 * 
 * <p>
 * Se una operazione ha successo di default viene usato il messaggio
 * <code>MessageCodes.General.OPERATION_SUCCESS</code>, mentre se c'è errore viene usato
 * <code>MessageCodes.General.OPERATION_FAIL</code>. Una classe derivata può usare i metodi setMessageIdSuccess e
 * setMessageIdFail per specificare messaggi alternativi.
 * </p>
 * 
 * <p>
 * Per usare questa classe occorre:
 * <ol>
 * <li>Derivare il proprio modulo da <code>AbstractSimpleModule</code></li>
 * <li>Opzionalmente specificare messaggi di errore alternativi con <code>setMessageIdSuccess</code> e/o
 * <code>setMessageIdFail</code></li>
 * <li>Opzionalmente implementare il metodo <code>getStatement</code> se lo statement da usare non è ottenibile con
 * <code>getConfig().getAttribute("QUERY")</code>, ad esempio nel caso venisse creato in maniera non standard.</li>
 * <li>Nel metodo <code>service(...)</code> richiamare uno dei metodi "do..." ad esempio <code>doSelect</code> se si
 * vuole eseguire una select.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Ogni metodo "do..." esegue le seguenti operazioni:
 * <ol>
 * <li>Esegue la query</li>
 * <li>Usando la classe <code>ReportOperationResult</code> inserisce il risultato dell'operazione</li>
 * <li>Inserisce nella response un attribute "..._OK" (es. SELECT_OK) se l'operazione ha avuto successo, in questo modo
 * un Publisher Java può richiamare un publisher opportuno basandosi sul risultato dell'operazione.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Sono presenti metodi speciali, quali <code>doInsertNoDuplicate</code> e <code>doUpdateNoDuplicate</code>, che
 * gestiscono l'inserimento e l'aggiornamento di un record verificando che non sia già esistente; per usare questo
 * meccanismo il modulo che deriva questa classe deve semplicemente chiamare <code>doInsertNoDuplicate</code> o
 * <code>doUpdateNoDuplicate</code> anziché <code>onInsert</code> o <code>onUpdate</code> e definire due query:
 * <code>QUERY_INSERT</code> (o <code>QUERY_UPDATE</code>) e <code>QUERY_SELECT</code>.
 * </p>
 * 
 * <p>
 * Gestione transazioni<br/>
 * Le transazioni sono gestite nei metodi <code>doInsert</code>, <code>doUpdate</code>, <code>doDelete</code>; di
 * default la classe non gestisce le transazioni; la gestione può essere attivata col metodo
 * <code>enableTransactions</code> a cui deve essere passata una istanza di è <code>TransactionQueryExecutor</code>, la
 * chiamata ai metodi di commit e rollback rimane a carico della classe derivata. Nel momento in cui si vuole ripassare
 * alla gestione senza transazioni si deve richiamare il metodo <code>elableSimpleQuery</code>. <br/>
 * Esempio in una classe MyModule derivata da <code>AbstractSimpleModule</code>: <code><br/>
 * &nbsp;service(...) {<br/>
 * &nbsp;&nbsp;TransactionQueryExecutor transExec= new TransactionQueryExecutor...<br/>
 * &nbsp;&nbsp;enableTransactions(transExec);<br/>
 * <br/>
 * &nbsp;&nbsp;doInsert(...) // Utilizza transExec<br/>
 * &nbsp;&nbsp;doDelete(...) // Utilizza transExec<br/>
 * &nbsp;&nbsp;...<br/>
 * <br/>
 * &nbsp;&nbsp;transExec.commit/rollback<br/>
 * &nbsp;}<br/>
 * </code>
 * 
 * <p>
 * Gestione Merge dei parametri per le Select (classe <code>SelectMerge</code>) <br/>
 * Prima di eseguire una select (qualsiasi metodo doSelect...) occorre richiamare <code>enableMergeOnSelect</code> e
 * dopo, se si vuole ripristinare il normale funzionamento delle select, richiamare <code>disableMergeOnSelect</code>.
 * <br/>
 * Esempio:</br>
 * <code>
 * &nbsp;service(...) {<br/>
 * &nbsp;&nbsp;SelectMerge selMerge= new [implementazione dell'interfaccia];<br/>
 * &nbsp;&nbsp;enableMergeOnSelect(selMerge);<br/>
 * &nbsp;&nbsp;...esecuzione di select con lo stesso merge...<br/>
 * &nbsp;&nbsp;doSelect...(...)<br/>
 * &nbsp;&nbsp;// Per cambiare strategia di merge<br/>
 * &nbsp;&nbsp;enableMergeOnSelect(selMerge2);<br/>
 * &nbsp;&nbsp;// Per ripristinare il funzionamento consueto delle select<br/>
 * &nbsp;&nbsp;disableMergeOnSelect();<br/>
 * &nbsp;}<br/>
 * </code>
 * </p>
 * 
 * <p>
 * Calcolo dei NextVal (progressivi, sequence) <br/>
 * Il metodo <code>doNextVal</code> ritorna il prossimo elemento della Sequence (cioè il progressivo univoco). Si
 * rimanda alla descrizione del metodo per i dettagli.
 * </p>
 * 
 * @author Corrado Vaccari
 * @created December 1, 2003
 */
public abstract class AbstractSimpleModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractSimpleModule.class.getName());
	private static final String TRUE = "TRUE";

	/**
	 * Indica "delete" andato a buon fine.
	 */
	protected final static String DELETE_OK = "DELETE_OK";

	/**
	 * Indica "insert" andato a buon fine.
	 */
	protected final static String INSERT_OK = "INSERT_OK";

	/**
	 * Indica "select" andato a buon fine.
	 */
	protected final static String SELECT_OK = "SELECT_OK";

	/**
	 * Indica "update" andato a buon fine.
	 */
	protected final static String UPDATE_OK = "UPDATE_OK";
	protected final static String QUERY = "QUERY";

	/**
	 * Nome standard dato alla sezione del modulo.
	 */
	private String sectionQueryInsert = "QUERY_INSERT";

	/**
	 * Nome standard dato alla sezione del modulo.
	 */
	private String sectionQueryDelete = "QUERY_DELETE";

	/**
	 * Nome standard dato alla sezione del modulo.
	 */
	private String sectionQuerySelect = "QUERY_SELECT";

	/**
	 * Nome standard dato alla sezione del modulo.
	 */
	private String sectionQueryUpdate = "QUERY_UPDATE";

	/**
	 * Nome standard dato alla sezione del modulo contenente la query per le generazione del NextVal cioè il prossimo
	 * Progressivo.
	 */
	private String sectionQueryNextVal = "QUERY_NEXTVAL";

	/**
	 * Se assegnato (non null) per le SELECT utilizza il SelectMerge, altrimenti le SELECT funionano nel modo consueto.
	 */
	private SelectMerge mergeOnSelect;
	private int messageIdSuccess = MessageCodes.General.OPERATION_SUCCESS;
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;
	private int messageIdElementDuplicate = MessageCodes.General.ELEMENT_DUPLICATED;

	private String keyForElementDuplicate = null; // GG 20/1/2005

	/**
	 * Strategia scelta per le query. può essere con o senza transazione
	 */
	protected AbstractQueryStrategy queryStrategy;

	/**
	 * Costruttore. Di default non utilizza le transazioni
	 */
	public AbstractSimpleModule() {
		enableSimpleQuery();
	}

	/**
	 * Id del messaggio da usare in caso di successo dell'operazione (select, delete ecc).
	 * 
	 * @param messageIdSuccess
	 * @return Valore precedente di messageIdSuccess
	 */
	public int setMessageIdSuccess(int messageIdSuccess) {
		int prev = this.messageIdSuccess;
		this.messageIdSuccess = messageIdSuccess;

		return prev;
	}

	/**
	 * Disabilita il messaggio da usare in caso di successo dell'operazione (select, delete ecc).
	 * 
	 * @return Valore precedente di messageIdSuccess
	 */
	public int disableMessageIdSuccess() {
		return setMessageIdSuccess(0);
	}

	/**
	 * Id del messaggio da usare in caso di fallimento dell'operazione (select, delete ecc).
	 * 
	 * @param messageIdFail
	 * @return Valore precedente di messageIdFail
	 */
	public int setMessageIdFail(int messageIdFail) {
		int prev = this.messageIdFail;
		this.messageIdFail = messageIdFail;

		return prev;
	}

	/**
	 * Disabilita il messaggio da usare in caso di fallimento dell'operazione (select, delete ecc).
	 * 
	 * @return Valore precedente di messageIdFail
	 */
	public int disableMessageIdFail() {
		return setMessageIdFail(0);
	}

	/**
	 * Id del messaggio da usare in caso fallimento dell'operazione per elemento duplicato.
	 * 
	 * @param messageIdElementDuplicate
	 *            The new messageIdElementDuplicate value
	 * @return Valore precedente di messageIdElementDuplicate
	 */
	public int setMessageIdElementDuplicate(int messageIdElementDuplicate) {
		int prev = this.messageIdElementDuplicate;
		this.messageIdElementDuplicate = messageIdElementDuplicate;

		return prev;
	}

	/**
	 * Disabilita il messaggio da usare in caso di fallimento dell'operazione per elemento duplicato.
	 * 
	 * @return Description of the Return Value
	 */
	public int disableMessageIdElementDuplicate() {
		return setMessageIdElementDuplicate(0);
	}

	/**
	 * @return The messageIdFail value
	 */
	protected int getMessageIdFail() {
		return this.messageIdFail;
	}

	/**
	 * @return The messageIdSuccess value
	 */
	protected int getMessageIdSuccess() {
		return this.messageIdSuccess;
	}

	/**
	 * @return The messageIdElementDuplicate value
	 */
	protected int getMessageIdElementDuplicate() {
		return this.messageIdElementDuplicate;
	}

	/**
	 * Ritorna il bean con lo statement da usare, questo metodo può essere reimplementato da una classe derivata se
	 * occorre una diversa elaborazione.
	 * 
	 * @return SourceBean contenente lo statement
	 */
	protected SourceBean getStatement() {
		checkStatementNames();

		return (SourceBean) getConfig().getAttribute(QUERY);
	}

	/**
	 * Metodo di controllo inteno alla classe che verifica eventuali errori nella configurazione del modulo, ad esempio
	 * un modulo che ha una sola query deve avere una sezione QUERY ma, se possiede più query, tutte le sezioni devono
	 * avere un nome diverso da QUERY (ad esempio QUERY_INSERT QUERY_DELETE) altrimenti QUERY può creare confusione.
	 */
	protected void checkStatementNames() {
		boolean queryPresente = getConfig().getAttribute(QUERY) != null;
		boolean otherQueryPresenti = (getConfig().getAttribute(this.sectionQueryInsert) != null)
				|| (getConfig().getAttribute(this.sectionQueryDelete) != null)
				|| (getConfig().getAttribute(this.sectionQuerySelect) != null)
				|| (getConfig().getAttribute(this.sectionQueryUpdate) != null)
				|| (getConfig().getAttribute(this.sectionQueryNextVal) != null);

		if (queryPresente && otherQueryPresenti) {
			LogUtils.logWarning("checkStatementNames", "Query [" + QUERY
					+ "] mixed with other tags (like QUERY_SELECT etc.), " + "check the module configuration", this);
		}
	}

	/**
	 * Ritorna il bean con lo statement. Prima viene cercato con getStatement per compatibilità con i moduli esistenti
	 * che hanno in configurazione i tag <QUERY>, poi viene cercato <QUERY_INSERT>.
	 * 
	 * @return The insertStatement value
	 */
	protected SourceBean getInsertStatement() {
		checkStatementNames();

		SourceBean beanQuery = getStatement();

		if (beanQuery == null) {
			beanQuery = (SourceBean) getConfig().getAttribute(this.sectionQueryInsert);

			checkBeanQuery(beanQuery, sectionQueryInsert, "getInsertStatement");
		}

		return beanQuery;
	}

	/**
	 * Ritorna il bean con lo statement. Prima viene cercato con getStatement per compatibilità con i moduli esistenti
	 * che hanno in configurazione i tag <QUERY>, poi viene cercato <QUERY_SELECT>.
	 * 
	 * @return The selectStatement value
	 */
	protected SourceBean getSelectStatement() {
		checkStatementNames();

		SourceBean beanQuery = getStatement();

		if (beanQuery == null) {
			beanQuery = (SourceBean) getConfig().getAttribute(this.sectionQuerySelect);

			checkBeanQuery(beanQuery, sectionQuerySelect, "getSelectStatement");
		}

		return beanQuery;
	}

	/**
	 * Verifica la presenza del bean e, in caso sia mancante, inserisce un log opportuno.
	 * 
	 * Questo metodo è studiato per funzionare con i metodi getSelectStatement, getDeleteStatement ecc.
	 */
	private void checkBeanQuery(SourceBean beanQuery, String nameQuery, String nameMethod) {
		if (beanQuery == null) {
			LogUtils.logWarning(nameMethod, "Query [" + QUERY + "] or [" + nameQuery + "] not found in module config",
					this);
		}
	}

	/**
	 * Ritorna il bean con lo statement. Prima viene cercato con getStatement per compatibilità con i moduli esistenti
	 * che hanno in configurazione i tag <QUERY>, poi viene cercato <QUERY_DELETE>.
	 * 
	 * @return The deleteStatement value
	 */
	protected SourceBean getDeleteStatement() {
		checkStatementNames();

		SourceBean beanQuery = getStatement();

		if (beanQuery == null) {
			beanQuery = (SourceBean) getConfig().getAttribute(this.sectionQueryDelete);

			checkBeanQuery(beanQuery, sectionQueryDelete, "getDeleteStatement");
		}

		return beanQuery;
	}

	/**
	 * Ritorna il bean con lo statement. Prima viene cercato con getStatement per compatibilità con i moduli esistenti
	 * che hanno in configurazione i tag <QUERY>, poi viene cercato <QUERY_UPDATE>.
	 * 
	 * @return The updateStatement value
	 */
	protected SourceBean getUpdateStatement() {
		checkStatementNames();

		SourceBean beanQuery = getStatement();

		if (beanQuery == null) {
			beanQuery = (SourceBean) getConfig().getAttribute(this.sectionQueryUpdate);

			checkBeanQuery(beanQuery, sectionQueryUpdate, "getUpdateStatement");
		}

		return beanQuery;
	}

	/**
	 * @return
	 */
	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	/**
	 * Esegue una Select standardizzata. Utilizza lo statement passato e alla fine ripristina lo statement precedente.
	 * 
	 * @param request
	 * @param response
	 * @param statementName
	 *            nome dello statement alternativo da utilizzare.
	 * @return Source bean con le rows selezionate, null in caso di errore.
	 */
	public SourceBean doSelectWithStatement(SourceBean request, SourceBean response, String statementName) {
		String prevStatement = this.setSectionQuerySelect(statementName);

		SourceBean result = doSelect(request, response);

		this.setSectionQuerySelect(prevStatement);

		return result;
	}

	/**
	 * Ritorna il prossimo NextVal (cioè il progressivo univoco); per usare il metodo occorre fornire al modulo una
	 * nuova query chiamata QUERY_NEXTVAL (oppure un nome alternativo assegnabile con setSectionNextVal); tale query
	 * deve eseguire una SELECT nella sequence e ritornare il risultato in un field numerico (che deve essere convertito
	 * in BigDeciamal da SQL) chiamato "DO_NEXTVAL", infatti sarà questo il field da cui sarà letto il nuovo
	 * progressivo.
	 * 
	 * @param request
	 * @param response
	 * @return Nuovo progressivo calcolato, oppure null in caso di errore (opportunamente segnalato nel log).
	 */
	public BigDecimal doNextVal(SourceBean request, SourceBean response) {
		BigDecimal prog = null;

		// Utilizza temporanemanete la query per la sequence come query di
		// select
		String prev = this.setSectionQuerySelect(this.sectionQueryNextVal);

		SourceBean bean = (SourceBean) doSelect(request, response);

		if (bean != null) {
			prog = (BigDecimal) bean.getAttribute("ROW.DO_NEXTVAL");
		}

		this.setSectionQuerySelect(prev);

		return prog;
	}

	/**
	 * Esegue una Select standardizzata.
	 * 
	 * @param request
	 * @param response
	 * @return Source bean con le rows selezionate, null in caso di errore.
	 */
	public SourceBean doSelect(SourceBean request, SourceBean response) {
		return doSelect(request, response, true);
	}

	/**
	 * Esegue una Select standardizzata. E' possibile non inserire il risultato nella response; potrebbero esserci casi
	 * in cui si desidera elaborare il risultato ma non inserirlo in response, magari perché parte di una elaborazione
	 * più complessa.
	 * 
	 * @param request
	 * @param response
	 * @param insertRowsInResponse
	 *            Indica se inserire il risultato della select nella response
	 * @return Source bean con le rows selezionate, null in caso di errore.
	 */
	public SourceBean doSelect(SourceBean request, SourceBean response, boolean insertRowsInResponse) {
		String pool = getPool();
		SourceBean statement = getSelectStatement();

		SourceBean beanRows = null;
		/*
		 * if (mergeOnSelect != null) { beanRows = (SourceBean) QueryExecutor.executeQuery( getRequestContainer(),
		 * getResponseContainer(), pool, statement, mergeOnSelect, "SELECT"); } else { beanRows = (SourceBean)
		 * QueryExecutor.executeQuery( getRequestContainer(), getResponseContainer(), pool, statement, "SELECT"); }
		 */

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {

			if (mergeOnSelect == null) {
				beanRows = (SourceBean) queryStrategy.executeQuery(statement, "SELECT");
			} else {
				beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
						statement, mergeOnSelect, "SELECT");
			}

			LogUtils.logDebug("doSelect", "bean rows [" + ((beanRows == null) ? "" : beanRows.toXML(false)) + "]",
					this);

			if (insertRowsInResponse) {
				response.setAttribute(beanRows);
			}

			response.setAttribute(SELECT_OK, TRUE);

			reportSuccess(reportOperation);

		} catch (Exception ex) {
			EMFUserError error = reportOperation.reportFailure(this.messageIdFail, ex, "doSelect", "method failed");
			// NOTA 11/09/2006 Savino: gestione id dell'errore generato dalla
			// doSelect()
			try {
				// si associa l'errore al modulo che lo ha generato. Questo
				// permette di ricreare in locale l'errore se viene
				// generato in una chiamata remota tramite web service.
				if (error != null)
					response.setAttribute("ERROR_ID", String.valueOf(error.hashCode()));
			} catch (SourceBeanException e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"Impossibile impostare l'hashcode dell'errore nel modulo " + this.getModule(), e);

			}
		}

		return beanRows;
	}

	/**
	 * Esegue una DELETE standardizzata.
	 * 
	 * @param request
	 * @param response
	 * @return Risultato dell'operazione
	 */
	public boolean doDelete(SourceBean request, SourceBean response) {
		SourceBean statement = getDeleteStatement();

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		Boolean esito = Boolean.valueOf(false);

		try {
			esito = (Boolean) queryStrategy.executeQuery(statement, "DELETE");

			/*
			 * DEBUG: Commentato per inserimento nuova gestione Boolean esito= (Boolean)QueryExecutor.executeQuery(
			 * getRequestContainer(), getResponseContainer(), pool, statement, "DELETE");
			 */
		} catch (QueryStrategyException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doDelete", "queryStrategy.executeQuery failed");

			return false;
		}

		LogUtils.logDebug("doDelete",
				"request [" + ((request == null) ? "" : request.toXML(false)) + "] esito [" + esito + "]", this);

		if ((esito == null) || !esito.booleanValue()) {
			reportOperation.reportFailure(this.messageIdFail);

			return false;
		}

		try {
			response.setAttribute(DELETE_OK, TRUE);
		} catch (SourceBeanException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doDelete", "Cannot assign DELETE_OK in response");
		}

		reportSuccess(reportOperation);

		return true;
	}

	/**
	 * Esegue una INSERT con controllo di record non duplicato. Viene prima di tutto eseguito un onSelect per verificare
	 * se il record esiste, se esiste allora viene inserito un messaggio di errore di "elemento duplicato" usando
	 * ReportOperationResult; se il record non esiste allora viene eseguito un onInsert. Nella configurazione del
	 * modullo è sufficente specificare due query distinte: QUERY_INSERT e QUERY_SELECT, di tutto il resto si occupa
	 * questo metodo.
	 * 
	 * @param request
	 * @param response
	 * @return Risultato dell'operazione, se il record esiste già torna comunque true perché non è un errore
	 */
	public boolean doInsertNoDuplicate(SourceBean request, SourceBean response) {
		// Non voglio che doSelect produca alcun
		// messaggio di successo
		int messageIdSuccess = this.getMessageIdSuccess();
		this.setMessageIdSuccess(0);

		SourceBean beanSelect = doSelect(request, response, false);

		this.setMessageIdSuccess(messageIdSuccess);

		Vector vect = beanSelect.getAttributeAsVector("ROW");

		boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);

		if (isElemEsistente && (messageIdElementDuplicate != 0)) {

			ReportOperationResult reportOperation = new ReportOperationResult(this, response);

			// GG 20-1-05: se ho impostato una chiave, ne prelevo il valore dal
			// SB corrente e creo un messaggio opportuno
			if ((keyForElementDuplicate != null) && (keyForElementDuplicate.length() > 0)) {

				Vector paramV = new Vector();
				SourceBean sb0 = (SourceBean) vect.get(0);
				Object keyValue = sb0.getAttribute(keyForElementDuplicate);
				paramV.add((keyValue == null) ? "" : keyValue.toString());
				reportOperation.reportFailure(
						new EMFUserError(EMFErrorSeverity.INFORMATION, messageIdElementDuplicate, paramV), "", "");
			} else {
				reportOperation.reportFailure(messageIdElementDuplicate);
			}
			return true;
		}

		return doInsert(request, response);
	}

	/**
	 * Esegue una INSERT con controllo di record non duplicato. Viene prima di tutto eseguito un onSelect per verificare
	 * se il record esiste, se esiste allora non viene eseguito un onInsert; se il record non esiste allora viene
	 * eseguito un onInsert. Nella configurazione del modullo è sufficente specificare due query distinte: QUERY_INSERT
	 * e QUERY_SELECT, di tutto il resto si occupa questo metodo.
	 * 
	 * @param request
	 * @param response
	 * @return Risultato dell'operazione, se il record esiste già torna comunque true perché non è un errore
	 */
	public boolean doInsertNoDuplicateNoReport(SourceBean request, SourceBean response) {
		// Non voglio che doSelect produca alcun
		// messaggio di successo
		int messageIdSuccess = this.getMessageIdSuccess();
		this.setMessageIdSuccess(0);

		SourceBean beanSelect = doSelect(request, response, false);

		this.setMessageIdSuccess(messageIdSuccess);

		Vector vect = beanSelect.getAttributeAsVector("ROW");

		boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);

		if (isElemEsistente) {
			return true;
		}

		return doInsert(request, response);
	}

	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * Esegue una UPDATE standardizzata. Il campo "numlko" per la gestione del lock ottimistico non è stato gestito.
	 * 
	 * @param request
	 * @param response
	 * @return Risultato dell'operazione
	 */
	public Object doUpdate(SourceBean request, SourceBean response, boolean reportException) {
		if (reportException) {
			SourceBean statement = getUpdateStatement();

			ReportOperationResult reportOperation = new ReportOperationResult(this, response);

			Boolean esito = Boolean.valueOf(false);
			Object ret = null;
			try {
				ret = queryStrategy.executeQuery(statement, "UPDATE");
			} catch (QueryStrategyException ex) {
				reportOperation.reportFailure(this.messageIdFail, ex, "doUpdate", "queryStrategy.executeQuery failed");

				ret = ex;
			}
			if (ret instanceof Exception) {

				reportOperation.reportFailure(this.messageIdFail, (Exception) ret, "doUpdate",
						"queryStrategy.executeQuery failed");

				return ret;
			} else
				esito = (Boolean) ret;

			StringBuffer sb = new StringBuffer();
			sb.append("request [");
			if (request != null)
				sb.append(request.toXML(false));
			sb.append("] esito [");
			sb.append(esito);
			sb.append("]");

			if ((esito != null) && !esito.booleanValue()) {
				sb.append(" se non c'è un errore precedente a questo log,");
				sb.append("il problema potrebbe essere che il record da aggiornare");
				sb.append("non è stato trovato, vedere i valori dei parametri usati");
				sb.append("per la query di UPDATE.");
			}

			LogUtils.logDebug("doUpdate", sb.toString(), this);

			if ((esito == null) || !esito.booleanValue()) {
				reportOperation.reportFailure(this.messageIdFail);

				return esito;
			}

			try {
				response.setAttribute(UPDATE_OK, TRUE);
			} catch (SourceBeanException ex) {
				reportOperation.reportFailure(this.messageIdFail, ex, "doUpdate",
						"Cannot assign UPDATE_OK in response");
			}

			reportSuccess(reportOperation);

			return esito;

		} else {
			return Boolean.valueOf(doUpdate(request, response));
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * <p>
	 * Esegue una UPDATE con controllo di record non duplicato. Viene prima di tutto eseguito un doSelect per verificare
	 * se il record esiste, se esiste allora viene inserito un messaggio di errore di "elemento duplicato" usando
	 * ReportOperationResult; se il record non esiste allora viene eseguito un doUpdate.
	 * </p>
	 * <p>
	 * La select del doSelect deve essere costruita in modo da restituire il progressivo (id) della tabella sulla quale
	 * esiste il vincolo di unicita' (unique); questo per verificare se l'eventuale duplicato trovato è il record stesso
	 * in modifica oppure è un altro record; Il messaggio di errore scatta solo nel secondo caso.
	 * </p>
	 * <p>
	 * Nella configurazione del modulo è sufficente specificare due query distinte: <code>QUERY_UPDATE</code> e
	 * <code>QUERY_SELECT</code>, di tutto il resto si occupa questo metodo; <code>QUERY_SELECT</code> deve essere
	 * costruita in modo da restituire il progressivo della tabella.
	 * </p>
	 * 
	 * @param request
	 * @param response
	 * @param progressivo
	 * @return Risultato dell'operazione, se il record esiste già torna comunque true perché non è un errore
	 */
	public boolean doUpdateNoDuplicate(SourceBean request, SourceBean response, String progressivo) {
		return canUpdateWithNoDuplicate(request, response, progressivo) ? doUpdate(request, response) : true;
	}

	public boolean canUpdateWithNoDuplicate(SourceBean request, SourceBean response, String progressivo) {
		boolean do_update = true;

		// Non voglio che doSelect produca alcun
		// messaggio di successo
		int messageIdSuccess = this.getMessageIdSuccess();
		this.setMessageIdSuccess(0);

		SourceBean beanSelect = doSelect(request, response, false);

		this.setMessageIdSuccess(messageIdSuccess);

		Vector vect = beanSelect.getAttributeAsVector("ROW");

		boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);

		if (isElemEsistente) {
			Object prgSelect = ((SourceBean) vect.get(0)).getAttribute(progressivo).toString();
			Object prgRequest = request.getAttribute(progressivo);

			if (!(prgSelect.equals(prgRequest))) {
				do_update = false;
			}
		}

		return do_update;
	}

	public Object doInsert(SourceBean request, SourceBean response, boolean reportException) {
		if (reportException) {
			Boolean esito = Boolean.valueOf(false);
			Object ret = null;
			SourceBean statement = getInsertStatement();
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			try {
				ret = queryStrategy.executeQuery(statement, "INSERT");
			} catch (QueryStrategyException ex) {
				reportOperation.reportFailure(this.messageIdFail, ex, "doInsert", "queryStrategy.executeQuery failed");

				return ex;
			}

			LogUtils.logDebug("doInsert",
					"request [" + ((request == null) ? "" : request.toXML(false)) + "] esito [" + esito + "]", this);
			if (ret instanceof Exception) {

				reportOperation.reportFailure(this.messageIdFail);

				return ret;
			} else
				esito = (Boolean) ret;

			try {
				response.setAttribute(INSERT_OK, TRUE);
			} catch (SourceBeanException ex) {
				reportOperation.reportFailure(this.messageIdFail, ex, "doInsert",
						"Cannot assign INSERT_OK in response");
			}

			reportSuccess(reportOperation);
			return esito;
		} else {
			return Boolean.valueOf(doInsert(request, response, null));
		}
	}

	public boolean doInsert(SourceBean request, SourceBean response) {
		return doInsert(request, response, null);
	}

	/**
	 * Esegue una INSERT standardizzata.
	 * 
	 * @param request
	 * @param response
	 * @param fieldProgressivo
	 *            Se presente indica di generare un nuovo progressivo e inserirne in requesto il valore con questo nome.
	 *            quello già presente in request.
	 * @return Risultato dell'operazione
	 */
	public boolean doInsert(SourceBean request, SourceBean response, String fieldProgressivo) {
		SourceBean statement = getInsertStatement();

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		if ((fieldProgressivo != null) && (fieldProgressivo.length() > 0)) {
			int idSuccess = disableMessageIdSuccess();
			Object prog = doNextVal(request, response);
			setMessageIdSuccess(idSuccess);

			if (prog == null) {
				return false;
			}

			try {
				request.updAttribute(fieldProgressivo, prog);
			} catch (SourceBeanException ex) {
				reportOperation.reportFailure(this.messageIdFail, ex, "doInsert", "request.updAttributefailed");

				return false;
			}
		}

		Boolean esito = Boolean.valueOf(false);

		try {
			esito = (Boolean) queryStrategy.executeQuery(statement, "INSERT");
		} catch (QueryStrategyException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doInsert", "queryStrategy.executeQuery failed");

			return false;
		}

		LogUtils.logDebug("doInsert",
				"request [" + ((request == null) ? "" : request.toXML(false)) + "] esito [" + esito + "]", this);

		if ((esito == null) || !esito.booleanValue()) {
			reportOperation.reportFailure(this.messageIdFail);

			return false;
		}

		try {
			response.setAttribute(INSERT_OK, TRUE);
		} catch (SourceBeanException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doInsert", "Cannot assign INSERT_OK in response");
		}

		reportSuccess(reportOperation);

		return true;
	}

	/**
	 * Esegue una UPDATE standardizzata. Il campo "numlko" per la gestione del lock ottimistico non è stato gestito.
	 * 
	 * @param request
	 * @param response
	 * @return Risultato dell'operazione
	 */
	public boolean doUpdate(SourceBean request, SourceBean response) {
		SourceBean statement = getUpdateStatement();

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		Boolean esito = Boolean.valueOf(false);

		try {
			esito = (Boolean) queryStrategy.executeQuery(statement, "UPDATE");
		} catch (QueryStrategyException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doUpdate", "queryStrategy.executeQuery failed");

			return false;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("request [");
		if (request != null)
			sb.append(request.toXML(false));
		sb.append("] esito [");
		sb.append(esito);
		sb.append("]");

		if ((esito != null) && !esito.booleanValue()) {
			sb.append(" se non c'è un errore precedente a questo log,");
			sb.append("il problema potrebbe essere che il record da aggiornare");
			sb.append("non è stato trovato, vedere i valori dei parametri usati");
			sb.append("per la query di UPDATE.");
		}

		LogUtils.logDebug("doUpdate", sb.toString(), this);

		if ((esito == null) || !esito.booleanValue()) {
			reportOperation.reportFailure(this.messageIdFail);

			return false;
		}

		try {
			response.setAttribute(UPDATE_OK, TRUE);
		} catch (SourceBeanException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doUpdate", "Cannot assign UPDATE_OK in response");
		}

		reportSuccess(reportOperation);

		return true;
	}

	/**
	 * Esegue una INSERT o una UPDATE a seconda dell'esito del controllo di record non duplicato. Viene prima di tutto
	 * eseguito un onSelect per verificare se il record esiste, se il record non esiste allora viene eseguito un
	 * doInsert, altriementi un doUpdate. Nella configurazione del modullo è sufficente specificare tre query distinte:
	 * QUERY_INSERT, QUERY_SELECT, QUERY_UPDATE di tutto il resto si occupa questo metodo.
	 * 
	 * @param request
	 * @param response
	 * @return Risultato dell'operazione
	 */
	public boolean doInsertUpdate(SourceBean request, SourceBean response) {
		// Non voglio che doSelect produca alcun
		// messaggio di successo
		int messageIdSuccess = this.getMessageIdSuccess();
		this.setMessageIdSuccess(0);

		SourceBean beanSelect = doSelect(request, response);

		this.setMessageIdSuccess(messageIdSuccess);

		Vector vect = beanSelect.getAttributeAsVector("ROW");

		boolean isElemEsistente = (beanSelect != null) && (vect.size() > 0);

		return isElemEsistente ? doUpdate(request, response) : doInsert(request, response);
	}

	public SourceBean doDynamicSelect(SourceBean request, SourceBean response) {
		return doDynamicSelect(request, response, true);
	}

	/**
	 * Esegue una SELECT dinamica utilizzando come query la stringa restituita da una classe (statement provider)
	 * censita nel file di configurazione. Puo' essere usata anche in un contesto transazionale.
	 * 
	 * @param request
	 * @param response
	 * @return risultato dell'operazione
	 */
	public SourceBean doDynamicSelect(SourceBean request, SourceBean response, boolean insertRowsInResponse) {

		DataConnection dc = null;
		String statement = null;
		SourceBean beanRows = null;

		SourceBean query = getSelectStatement();

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");
			statement = getDynamicStatement(statementProviderClassName, request, response);
			dc = getDataConnection();
			QueryExecutorObject queryExecObj = new QueryExecutorObject();
			queryExecObj.setRequestContainer(getRequestContainer());
			queryExecObj.setResponseContainer(getResponseContainer());
			queryExecObj.setDataConnection(dc);
			queryExecObj.setStatement(statement);
			queryExecObj.setType(QueryExecutorObject.SELECT);
			queryExecObj.setTransactional(isTransactionEnabled());
			queryExecObj.setDontForgetException(false);

			beanRows = (SourceBean) queryExecObj.exec();

			if (insertRowsInResponse) {
				response.setAttribute(beanRows);
			}
			reportOperation.reportSuccess(this.messageIdSuccess);
			response.setAttribute(SELECT_OK, TRUE);
		} catch (Exception e) {
			LogUtils.logError("doDynamicSelect", "Error", e, this);
			reportOperation.reportFailure(this.messageIdFail, e, "doDynamicSelect", "method failed");
		} finally { // se tutto e' andato bene la connessione risulta gia'
					// chiusa a meno che
			// sia in corso una transazione; in questo caso evito di chiuderela
			if (!isTransactionEnabled())
				Utils.releaseResources(dc, null, null);
		}

		return beanRows;

		/*
		 * 
		 * try { String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS"); String
		 * statement = getDynamicStatement(statementProviderClassName); dc = getTXDataConnection(); if (dc == null) {
		 * LogUtils.logError("doDynamicSelect", "dc null", this); } cmdSelect = dc.createSelectCommand(statement);
		 * //eseguiamo la query dr = cmdSelect.execute(); //crea la lista con il dataresult ScrollableDataResult sdr =
		 * null; if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) { sdr =
		 * (ScrollableDataResult) dr.getDataObject(); } beanRows = sdr.getSourceBean(); } catch (Exception e) {
		 * LogUtils.logError("doDynamicSelect", "Error", e, this); } finally { // se e' in corso una transazione evito
		 * di chiudere la connessione if (queryStrategy.getTXDataConnection()==null) Utils.releaseResources(dc,
		 * cmdSelect, dr); } ReportOperationResult reportOperation = new ReportOperationResult(this, response); try {
		 * LogUtils.logDebug( "doDynamicSelect", "bean rows [" + ((beanRows == null) ? "" : beanRows.toXML(false)) +
		 * "]", this);
		 * 
		 * response.setAttribute(beanRows); response.setAttribute(SELECT_OK, TRUE); reportSuccess(reportOperation);
		 * 
		 * //response.setAttribute(SELECT_OK, "TRUE"); //if (this.messageIdSuccess>0)
		 * reportOperation.reportSuccess(this.messageIdSuccess); } catch (Exception ex) { reportOperation.reportFailure(
		 * this.messageIdFail, ex, "doDynamicSelect", "method failed"); }
		 * 
		 * return beanRows;
		 */
	}

	/**
	 * Esegue una UPDATE dinamica utilizzando come query la stringa restituita da una classe (statement provider)
	 * censita nel file di configurazione. Puo' essere usata anche in un contesto transazionale.
	 * 
	 * @param request
	 * @param response
	 * @return risultato dell'operazione
	 */
	public boolean doDynamicUpdate(SourceBean request, SourceBean response) {

		// oggetti per l'esecuzione della query.
		// non verrà usato il query executor perché la query non
		// e' definita nei file di configurazione, ma viene reperita tramite
		// uno statement provider
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		String statement = null;

		SourceBean query = getUpdateStatement();

		try {
			String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");
			statement = getDynamicStatement(statementProviderClassName, request, response);
			dc = getDataConnection();
			QueryExecutorObject queryExecObj = new QueryExecutorObject();
			queryExecObj.setRequestContainer(getRequestContainer());
			queryExecObj.setResponseContainer(getResponseContainer());
			queryExecObj.setDataConnection(dc);
			queryExecObj.setStatement(statement);
			queryExecObj.setType(QueryExecutorObject.UPDATE);
			queryExecObj.setTransactional(isTransactionEnabled());
			queryExecObj.setDontForgetException(false);

			Boolean esito = (Boolean) queryExecObj.exec();

			if (esito == null || (esito != null && (esito.booleanValue() == false))) {
				ReportOperationResult reportOperation = new ReportOperationResult(this, response);
				reportOperation.reportFailure(this.messageIdFail, (Exception) null, "doDynamicUpdate",
						"doDynamicUpdate: esito=" + esito);
				return false;
			}
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			reportOperation.reportSuccess(this.messageIdSuccess);
			return true;
		} catch (Exception ex) {
			LogUtils.logError("doDynamicUpdate", "Error", ex, this);
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			reportOperation.reportFailure(this.messageIdFail, ex, "doDynamicUpdate", "method failed");
			return false;
		} finally { //
			// se e' in corso una transazione evito di chiudere la connessione
			if (!isTransactionEnabled())
				Utils.releaseResources(dc, null, null);
		}
	}

	/**
	 * Esegue una INSERT dinamica utilizzando come query la stringa restituita da una classe (statement provider)
	 * censita nel file di configurazione. Puo' essere usata anche in un contesto transazionale.
	 * 
	 * @param request
	 * @param response
	 * @return risultato dell'operazione
	 */
	public boolean doDynamicInsert(SourceBean request, SourceBean response) {

		DataConnection dc = null;
		String statement = null;

		SourceBean query = getInsertStatement();

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			String statementProviderClassName = (String) query.getAttribute("STATEMENT_PROVIDER.CLASS");
			statement = getDynamicStatement(statementProviderClassName, request, response);
			dc = getDataConnection();
			if (dc == null) {
				// LogUtils.logError("doDynamicInsert", "dc null", this);
				throw new Exception("DataConnection is null");
			}
			QueryExecutorObject queryExecObj = new QueryExecutorObject();
			queryExecObj.setRequestContainer(getRequestContainer());
			queryExecObj.setResponseContainer(getResponseContainer());
			queryExecObj.setDataConnection(dc);
			queryExecObj.setStatement(statement);
			queryExecObj.setType(QueryExecutorObject.INSERT);
			queryExecObj.setTransactional(isTransactionEnabled());
			queryExecObj.setDontForgetException(false);
			Boolean esito = (Boolean) queryExecObj.exec();
			if (esito == null || (esito != null && (esito.booleanValue() == false))) {
				reportOperation.reportFailure(this.messageIdFail, (Exception) null, "doDynamicInsert",
						"doDynamicInsert: esito=" + esito);
				return false;
			}

			reportOperation.reportSuccess(this.messageIdSuccess);
			response.setAttribute(INSERT_OK, TRUE);
			return true;
		} catch (Exception e) {
			LogUtils.logError("doDynamicInsert", "Error", e, this);
			reportOperation.reportFailure(this.messageIdFail, e, "doDynamicInsert", "method failed");
			return false;
		} finally {
			// se e' in corso una transazione evito di chiudere la connessione
			// altrimenti la connessione risultera' gia' chiusa
			if (!isTransactionEnabled())
				Utils.releaseResources(dc, null, null);
		}
	}

	/**
	 * Inserisce il messaggio di successo. Se il messaggio è <= 0 allora non inserisce nulla, in questo modo impostando
	 * messageIdSuccess a zero è possibile fare in modo che il messaggio non sia inserito.
	 * 
	 * @param destination
	 *            Description of the Parameter
	 * @destination Destinazione del messaggio
	 */
	public void reportSuccess(ReportOperationResult destination) {
		if (this.messageIdSuccess > 0) {
			destination.reportSuccess(this.messageIdSuccess);
		}
	}

	/**
	 * Assegna un nuovo nome alla sezione.
	 * 
	 * @param newName
	 */
	public void setSectionQueryDelete(String newName) {
		this.sectionQueryDelete = newName;
	}

	/**
	 * Assegna un nuovo nome alla sezione.
	 * 
	 * @param newName
	 */
	public void setSectionQueryInsert(String newName) {
		this.sectionQueryInsert = newName;
	}

	/**
	 * Assegna un nuovo nome alla sezione.
	 * 
	 * @param newName
	 * @return Il contenuto precedente di sectionQuerySelect
	 */
	public String setSectionQuerySelect(String newName) {
		String prev = sectionQuerySelect;
		this.sectionQuerySelect = newName;

		return prev;
	}

	/**
	 * Assegna un nuovo nome alla query per la sequence.
	 * 
	 * @param newName
	 * @return Il contenuto precedente di sectionQueryNextVal
	 */
	public String setSectionQueryNextVal(String newName) {
		String prev = this.sectionQueryNextVal;
		this.sectionQueryNextVal = newName;

		return prev;
	}

	/**
	 * Assegna un nuovo nome alla sezione.
	 * 
	 * @param newName
	 */
	public void setSectionQueryUpdate(String newName) {
		this.sectionQueryUpdate = newName;
	}

	/**
	 * Abilita la gestione alle query semplici (senza transazioni) per doInsert, doUpdate, doDelete, quando richiamati
	 * successivamente a questo metodo.
	 */
	public void enableSimpleQuery() {
		queryStrategy = new SimpleQueryStrategy(this);
	}

	/**
	 * Abilita la gestione alle query semplici (senza transazioni) per doInsert, doUpdate, doDelete, quando richiamati
	 * successivamente a questo metodo.
	 */
	public void enableSimpleQuery(boolean reportException) {
		queryStrategy = new SimpleQueryStrategy(this, reportException);
	}

	/**
	 * Abilita la gestione alle query col merge dei parametri (vedere la classe SelectMerge per dettagli) per le
	 * successive SELECT.
	 * 
	 * @param selMerge
	 *            Gestore del merge on select
	 */
	public void enableMergeOnSelect(SelectMerge selMerge) {
		mergeOnSelect = selMerge;
	}

	/**
	 * Disabilita la gestione alle query col merge dei parametri. Le successive SELECT, che funzioneranno nel modo
	 * consueto (vedere la classe SelectMerge per dettagli).
	 */
	public void disableMergeOnSelect() {
		mergeOnSelect = null;
	}

	/**
	 * Abilita il supporto alle transazioni per doInsert, doUpdate, doDelete quando richiamati successivamente a questo
	 * metodo.
	 * 
	 * @param executor
	 *            Istanza da usare per l'esecuzione delle query con transazione
	 */
	public void enableTransactions(TransactionQueryExecutor executor) {
		queryStrategy = new TransQueryStrategy(this, executor);
	}

	/**
	 * Estrae dalla request i valori associati all'argomento, tali valori possono essere uno o più di uno (ad esempio se
	 * seleziono uno o più righe di una combobox multivalue).
	 * 
	 * @param request
	 * @param attributeName
	 *            Nome dell'attributo
	 * @return Collection con i valori dell'attributo attributeName
	 */
	public static Collection getArgumentValues(SourceBean request, String attributeName) {
		Vector values = new Vector();

		Object obj = request.getAttribute(attributeName);

		if (obj instanceof Vector) {
			// Si sono selezionati più elementi in cui inserire
			// (mansioni, disponibilità)
			values = (Vector) obj;
		} else {
			if (obj instanceof String) {
				String value = (String) obj;

				if (value.length() > 0) {
					// Si è selezionato solo questo valore
					values.add(value);
				}
			} else {
				values.add(obj);
			}
		}

		return values;
	}

	public boolean errorOccurred(Object err) {
		if (err instanceof QueryStrategyException)
			return true;
		if (err instanceof Exception)
			return true;
		return false;
	}

	public int getSqlCode(Object err) {
		int errorCode = 0;
		Throwable ex = null;
		if (err instanceof Exception)
			ex = (Exception) err;
		if (ex instanceof QueryStrategyException)
			ex = (Exception) ((QueryStrategyException) err).getException();
		if (ex instanceof EMFInternalError)
			ex = ((EMFInternalError) ex).getNativeException();
		if (ex instanceof SQLException) {
			errorCode = ((SQLException) ex).getErrorCode();
		}
		return errorCode;
	}

	public void raiseQueryStrategyException(Object err) throws QueryStrategyException {
		if (err instanceof QueryStrategyException)
			throw (QueryStrategyException) err;
		if (err instanceof Exception)
			throw new QueryStrategyException("", (Exception) err);
		else
			throw new QueryStrategyException("impossibile generare un errore coerente", null);
	}

	/**
	 * 20/1/2005 by Luigi Antenucci Imposta/rende il vettore con i parametri usati nel messaggio di errore di "elemento
	 * duplicato" (il cui codice è impostato dal metodo "setMessageIdElementDuplicate").
	 */
	public String getKeyForElementDuplicate() {
		return keyForElementDuplicate;
	}

	public String setKeyForElementDuplicate(String params) {
		String prev = this.keyForElementDuplicate;
		this.keyForElementDuplicate = params;
		return prev;
	}

	protected String getSectionQuerySelect() {
		return this.sectionQuerySelect;
	}

	protected String getSectionQueryInsert() {
		return this.sectionQueryInsert;
	}

	protected String getSectionQueryNextVal() {
		return this.sectionQueryNextVal;
	}

	protected String getSectionQueryDelete() {
		return this.sectionQueryDelete;
	}

	protected String getSectionQueryUpdate() {
		return this.sectionQueryUpdate;
	}

	/**
	 * 
	 * @return DataConnection gia' aperta da una transazione o recuperata dal DataConnectionManager
	 * @throws EMFInternalError
	 */
	public DataConnection getDataConnection() throws EMFInternalError {
		DataConnection dc = null;
		if (isTransactionEnabled() == false) {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			if (dcm == null) {
				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "DataConnectionManager is null");
			}
			dc = dcm.getConnection(getPool());
		} else
			dc = queryStrategy.getTXDataConnection();
		return dc;
	}

	public boolean isTransactionEnabled() {
		// il SimpleQueryStrategy ritorna sempre null
		return queryStrategy.getTXDataConnection() != null;
	}

	/**
	 * Recupera lo statement sql dalla classe specificata. La classe deve implementare l'interfaccia
	 * IDynamicStatementProvider2 o IDynamicStatementProvider
	 * 
	 * @param statementProviderClassName
	 *            il nome della classe da istanziare
	 * @param serviceRequest
	 * @param serviceResponse
	 * @return lo statement sql o null se viene passata una classe che non implementa ne' IDynamicStatementProvider2 ne'
	 *         IDynamicStatementProvider
	 * @throws Exception
	 */
	private String getDynamicStatement(String statementProviderClassName, SourceBean serviceRequest,
			SourceBean serviceResponse) throws Exception {
		String dynamicStatement = "";
		Object statementProvider = Class.forName(statementProviderClassName).getDeclaredConstructor().newInstance();
		if (statementProvider instanceof IDynamicStatementProvider) {
			dynamicStatement = ((IDynamicStatementProvider) statementProvider).getStatement(getRequestContainer(),
					getConfig());
		} else if (statementProvider instanceof IDynamicStatementProvider2) {
			dynamicStatement = ((IDynamicStatementProvider2) statementProvider).getStatement(serviceRequest,
					serviceResponse);
		}

		return dynamicStatement;
	}
}