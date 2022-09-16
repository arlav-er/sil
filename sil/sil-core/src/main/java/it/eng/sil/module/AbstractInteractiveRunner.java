package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/**
 * Implementazione generica dei servizi interattivi, fornisce un costruttore di default e gli oggetti che servono per
 * l'esecuzione del servizio.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public abstract class AbstractInteractiveRunner implements Runnable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AbstractInteractiveRunner.class.getName());
	private String className = this.getClass().getName();

	/** Oggetti per l'esecuzione del servizio */
	protected SourceBean request;
	protected SourceBean response;
	protected AbstractInteractiveModule serviceUtils;

	/**
	 * Costruttore standard, fornisce la request, la response e i metodi dell'InteractiveSimpleModule
	 */
	public AbstractInteractiveRunner(SourceBean request, SourceBean response, AbstractInteractiveModule module) {
		this.request = request;
		this.response = response;
		serviceUtils = module;
	}

	/** Metodo che lancia l'esecuzione del servizio */
	public void run() {
		_logger.debug(className + "::run():" + Thread.currentThread().getName() + " in attesa!");

		try {
			synchronized (serviceUtils) {
				_logger.debug(className + "::run():" + Thread.currentThread().getName() + " partito!");

			}
			service();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.className + "::run():Eccezione non gestita durante l'esecuzione del servizio: "
							+ Thread.currentThread().getName(),
					e);

		}
	}

	/** Metodo da implementare per l'esecuzione del servizio */
	abstract public void service() throws SourceBeanException;

}