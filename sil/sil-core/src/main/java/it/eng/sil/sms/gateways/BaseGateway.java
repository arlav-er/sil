/*
 * Creato il 16-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.sms.gateways;

import java.util.List;
import java.util.Map;

import it.eng.sil.sms.SmsException;

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public abstract class BaseGateway implements IGateway {

	protected Map parameters = null;

	protected List smsList = null;

	/**
	 * @return
	 */
	public Map getParameters() {
		return parameters;
	}

	/**
	 * @param map
	 */
	public void setParameters(Map map) {
		parameters = map;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.sil.sms.gateways.IGateway#setSmsList(java.util.List)
	 */
	public void setSmsList(List list) {
		// TODO Stub di metodo generato automaticamente

		smsList = list;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.sil.sms.gateways.IGateway#send()
	 */
	public void send() throws SmsException {
		// TODO Stub di metodo generato automaticamente

	}

}
