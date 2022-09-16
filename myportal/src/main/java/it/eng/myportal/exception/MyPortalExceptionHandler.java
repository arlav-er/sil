package it.eng.myportal.exception;

import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import javax.ejb.EJBException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Custom Exception Handler per la gestione delle eccezioni
 * lanciate dai servzi degli EJB.
 * 
 * @author Rodi A.
 *
 */
public class MyPortalExceptionHandler extends ExceptionHandlerWrapper {

	private static Log log = LogFactory.getLog(MyPortalExceptionHandler.class);
	
	private static Properties properties;
	static {
		String fileName = "errors.properties";
		if (ConstantsSingleton.COD_REGIONE == 22) {
			fileName = "errors_trento.properties";
		}
		
		URL url = Thread.currentThread().getContextClassLoader().getResource("messages/"+fileName);
		try {
			properties = new Properties();
			properties.load(url.openStream());
		} catch (java.io.IOException e) {
			log.fatal("Impossibile caricare errors.properties",e);
		}
	}
	
	//pattern composite
	private ExceptionHandler wrapped;

	public MyPortalExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {
		//Cicla su tutte le eccezioni non ancora gestite
		Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
		while (i.hasNext()) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

			//ottieni l'eccezione che è stata lanciata
			Throwable t = context.getException();
			Throwable root = getRootCause(t);
			
			//gestiscila
			if (root instanceof MyPortalException) {
				addErrorMessage(((MyPortalException) root).getCodErrore(), root);	
			}
			else if (root instanceof EJBException) {
				addErrorMessage("generic.error", root);
			}
			else {
				log.error("Errore non gestito: " + t.getMessage(), t);
				log.error("Errore non gestito (root): " + root.getMessage(), root);
			}
			i.remove();
		}
		// let the parent handle the rest
		getWrapped().handle();
	}
	
	
	/**
	 * Restituisce l'Exception "padre"
	 * @param t l'eccezione di cui cercare la root
	 * @return l'exception "padre"
	 */
	public Throwable getRootCause(Throwable t) {
		return (t.getCause() == null) ? t : getRootCause(t.getCause());
	}
	
	
	/**
	 * Aggiunge un errore da mostrare all'utente e stampa sul log lo stacktrace
	 * dell'eccezione gestita.
	 *
	 * @param codErrore
	 *            codice del messaggioche si trova in errors.properties
	 */
	protected void addErrorMessage(String codErrore, Throwable e) {
		String msgText = (String) properties.get(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
		log.error(msgText, e);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}