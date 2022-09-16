package it.eng.sil.util.collectionexecutor;

import java.util.Collection;
import java.util.Iterator;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/**
 * Classe che esegue una particolare action per tutti i valori contenuti in una collezione.
 * 
 * Ad esempio nel caso delle Disponibilità Territoriali: per ogni mansione scelta (di cui ho i progressivi PRGMANSIONE
 * in "attributeValues") viene inserita la provincia scelta (questa è l'action specifica).
 * 
 * @author Corrado Vaccari
 * @created November 13, 2003
 */
public class CollectionExecutor implements Action {

	private Collection attributeValues;
	private Action action;
	private String attributeName;

	/**
	 * Costruttore.
	 * 
	 * @param attributeValues
	 *            Collezione con i valori dell'attributo "attributeName" da usare
	 * @param action
	 *            Action (funzione specifica) da eseguire
	 * @param attributeName
	 *            Nome dell'attributo da modificare nella request
	 */
	public CollectionExecutor(Collection attributeValues, Action action, String attributeName) {

		this.attributeValues = attributeValues;
		this.action = action;
		this.attributeName = attributeName;
	}

	/**
	 * Ogni valore contenuto nella collection viene assegnato nella request all'attributo "attributeName" ed eseguita
	 * l'action.
	 * 
	 * @param request
	 * @param response
	 * @exception ActionException
	 * @exception SourceBeanException
	 */
	public void execute(SourceBean request, SourceBean response) throws ActionException, SourceBeanException {

		for (Iterator iter = attributeValues.iterator(); iter.hasNext();) {

			String attributeValue = (String) iter.next();

			// Siccome potrei avere più di un field codice
			// presente (ad esempio ho i codice per i quattro
			// turni che voglio inserire),
			// allora faccio il del per rimuoverli
			// tutti e inserire quello nuovo
			request.delAttribute(this.attributeName);
			request.setAttribute(this.attributeName, attributeValue);

			action.execute(request, response);
		}
	}
}
