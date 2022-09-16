package it.eng.sil.util.collectionexecutor;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/**
 * Interfaccia base per le action.
 * 
 * NB: Da non confondersi con le action del framework, non ha nulla a che fare con it.eng.sil.action
 * 
 * E' stato implementato il "Composite" Design Pattern in cui l'action è il "Component", la base; InsertElementAction è
 * il "Leaf", la foglia che esegue la funzione di inserimento. CollectionExecutor è il "Composite" che contiene al suo
 * interno una action che può essere leaf o composite.
 * 
 * Il metodo execute è eseguito per tutti i sottonodi di quello corrente.
 * 
 * @author Corrado Vaccari
 * @created November 13, 2003
 */
public interface Action {

	/**
	 * Esegue l'action. Quale action dipende dalla implementazione data a questo metodo dalla classe che implementa
	 * questa interfaccia.
	 * 
	 * @param request
	 * @param response
	 * @exception ActionException
	 *                Invocata se si verifica un problema durante l'esecuzione dell'action
	 * @exception SourceBeanException
	 */
	void execute(SourceBean request, SourceBean response) throws ActionException, SourceBeanException;
}
