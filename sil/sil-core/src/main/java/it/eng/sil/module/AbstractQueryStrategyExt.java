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

/**
 * Classe base astratta per le strategie di esecuzione delle query. Implementa lo "Strategy" Design Pattern.
 * 
 * @author Corrado Vaccari
 * @created November 28, 2003
 */
public abstract class AbstractQueryStrategyExt extends AbstractQueryStrategy {

	private it.eng.sil.module.ContestoComune _module;

	/**
	 * Costruttore. Sono costretto per non modificare la superclasse a passare al suo costruttore un oggetto che estenda
	 * AbstractSimpleModule qualsiasi, nella fattispecie una classe anonima.
	 * 
	 * @param module
	 *            Modulo che utilizza questo strategy
	 */
	public AbstractQueryStrategyExt(it.eng.sil.module.ContestoComune module) {
		super(new AbstractSimpleModule() {
			public void service(SourceBean req, SourceBean res) {
			}
		});
		/*
		 * if (module == null) { throw new IllegalArgumentException("ContestoComune required"); }
		 */
		_module = module;
	}

	/**
	 * Gets the module attribute of the AbstractQueryStrategy object
	 * 
	 * @return The module value
	 */
	public SourceBean getConfig() {
		return _module.getConfig();
	}

	public RequestContainer getRequestContainer() {
		return _module.getRequestContainer();
	}

	public ResponseContainer getResponseContainer() {
		return _module.getResponseContainer();
	}

	/**
	 * Esegue la query indicata.
	 * 
	 * @param statement
	 *            Statement della query
	 * @param type
	 *            Tipo di query ("UPDATE" ecc.)
	 * @return Risultato della query, dipende dal tipo di query
	 * @exception QueryStrategyException
	 *                Exception lanciata in caso di errore
	 */
	public abstract Object executeQuery(SourceBean statement, String type) throws QueryStrategyException;
}
