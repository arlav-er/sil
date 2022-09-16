/*
 * Creato il 26-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;
import com.engiweb.framework.dispatching.action.AbstractHttpAction;
import com.engiweb.framework.dispatching.module.AbstractModule;

/**
 * @author savino
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * <p>
 * Estensione della classe AbstractSimpleModule che permette di utilizzare i suoi metodi di accesso al db
 * indifferentemente da un modulo o da una action.<br>
 * Esempio: AccessoSemplificato as = new AccessoSemplificato(this);<br>
 * as.setSectionQuerySelect("GET_LAV");<br>
 * as.doSelect(request, response);
 * </p>
 * <p>
 * Classe che fornisce una implementazione standardizzata delle operazioni tipiche gestite dal modulo e dalla action,
 * quali inserimento, cancellazione ecc.
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
 * <li>Istanziare la classe passando al costruttore il riferimento all' action o al modulo</li>
 * <li>Opzionalmente specificare messaggi di errore alternativi con <code>setMessageIdSuccess</code> e/o
 * <code>setMessageIdFail</code></li>
 * <li>Opzionalmente implementare il metodo <code>getStatement</code> se lo statement da usare non è ottenibile con
 * <code>getConfig().getAttribute("QUERY")</code>, ad esempio nel caso venisse creato in maniera non standard.</li>
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
public class AccessoSemplificato extends AbstractSimpleModule {

	private ContestoComune _config;

	/**
	 * Di default non utilizza la transazione.
	 */
	public AccessoSemplificato(AbstractModule module) {
		_config = new ContestoComune(module);
		enableSimpleQueryNew();
	}

	public AccessoSemplificato(AbstractAction action) {
		_config = new ContestoComune(action);
		enableSimpleQueryNew();
	}

	public AccessoSemplificato(AbstractHttpAction action) {
		_config = new ContestoComune(action);
		enableSimpleQueryNew();
	}

	public String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	/**
	 * Abilita la gestione alle query semplici (senza transazioni) per doInsert, doUpdate, doDelete, quando richiamati
	 * successivamente a questo metodo.
	 */
	public void enableSimpleQueryNew() {
		queryStrategy = new SimpleQueryStrategyExt(_config);
	}

	/**
	 * Override del metodo ereditato. E' necessario perche' il costruttore di default dell'AbstractSimpeModule chiama
	 * questo metodo, che dunque va 'disabilitato'.
	 */
	public void enableSimpleQuery() {
	}

	/**
	 * Abilita la gestione alle query semplici (senza transazioni) per doInsert, doUpdate, doDelete, quando richiamati
	 * successivamente a questo metodo.
	 */
	public void enableSimpleQuery(boolean reportException) {
		queryStrategy = new SimpleQueryStrategyExt(_config, reportException);
	}

	public void enableTransactions(TransactionQueryExecutor executor) {
		queryStrategy = new TransQueryStrategyExt(_config, executor);
	}

	public ResponseContainer getResponseContainer() {
		return _config.getResponseContainer();
	}

	/**
	 * @return
	 */
	public RequestContainer getRequestContainer() {
		return _config.getRequestContainer();
	}

	public SourceBean getConfig() {
		return _config.getConfig();
	}

	/**
	 * E' necessario implementare il metodo astratto della superclasse
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
	}

}
