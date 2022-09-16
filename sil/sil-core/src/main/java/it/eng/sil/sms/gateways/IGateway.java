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
public interface IGateway {
	public void send() throws SmsException;

	public void setSmsList(List list);

	/**
	 * Il Gateway e' in grado di spedire gli SMS?
	 * 
	 * @return <code>true</code> se riesce a spedire gli SMS
	 */
	public boolean isReachable();

	public void setParameters(Map parameters);

}