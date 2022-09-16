package it.eng.sil.module.movimenti.runners;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractInteractiveModule;
import it.eng.sil.module.AbstractInteractiveRunner;

/**
 * Classe che effettua la validazione automatica interattiva
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class ValidatoreRunnable extends AbstractInteractiveRunner {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidatoreRunnable.class.getName());
	private String className = this.getClass().getName();

	/**
	 * Costruttore standard, fornisce la request, la response e i metodi dell'InteractiveSimpleModule
	 */
	public ValidatoreRunnable(SourceBean request, SourceBean response, AbstractInteractiveModule module) {
		super(request, response, module);
	}

	/**
	 * Metodo chiamato per la validazione
	 */
	public void service() throws SourceBeanException {

		SourceBean reqparam = new SourceBean("PARAMETRI");
		reqparam.setAttribute("PARAM1", "CIAO1!");

		// chiamata di prova all'utente e recupero del risultato
		SourceBean respparam = null;
		try {
			respparam = serviceUtils.askToUser(reqparam);
		} catch (InterruptedException ie) {
			// Abortisco perché è cambiato il contesto
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + "::service():L'utente ha avviato un'altra richiesta, servizio abortito.", ie);

			return;
		}

		String userresp;
		if (respparam != null && respparam.containsAttribute("RESP1")) {
			userresp = (String) respparam.getAttribute("RESP1");
		} else
			userresp = "PRIMA RISPOSTA NON TROVATA!! PROBABILE TIMEOUT";

		SourceBean reqparam2 = new SourceBean("PARAMETRI");
		reqparam2.setAttribute("PARAM1", "CIAO2!");

		// seconda chaiamata di prova all'utente e recupero del risultato
		SourceBean respparam2 = null;
		try {
			respparam2 = serviceUtils.askToUser(reqparam2);
		} catch (InterruptedException ie) {
			// Abortisco perché è cambiato il contesto
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + "::service():L'utente ha avviato un'altra richiesta, servizio abortito.", ie);

			return;
		}

		String userresp2;
		if (respparam2 != null && respparam2.containsAttribute("RESP1")) {
			userresp2 = (String) respparam2.getAttribute("RESP1");
		} else
			userresp2 = "SECONDA RISPOSTA NON TROVATA!! PROBABILE TIMEOUT";

		SourceBean result = new SourceBean("RESULT");
		result.setAttribute("RES1", userresp + "  " + userresp2);

		// Chiamata di terminazione di prova
		serviceUtils.done(result);
	}
}