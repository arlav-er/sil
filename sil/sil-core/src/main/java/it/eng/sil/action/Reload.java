package it.eng.sil.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.action.AbstractAction;

/**
 * @author Franco Vuoto
 * 
 *         Rilascia i file di configurazione.
 * 
 */
public class Reload extends AbstractAction {
	public void init(SourceBean config) {
	}

	public void service(SourceBean request, SourceBean response) {

		ConfigSingleton.release();
	}

}
