package it.eng.sil.module.fs;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class MainBlank extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MainBlank.class.getName());
	private String className = this.getClass().getName();

	public MainBlank() {
	}

	public void service(SourceBean request, SourceBean response) {
		SessionContainer session = getRequestContainer().getSessionContainer();
		Set chiavi = session.getVisibleAttributes().keySet();
		// System.out.println(chiavi);
		try {
			Iterator iter = chiavi.iterator();
			while (iter.hasNext()) {

				String key = (String) iter.next();

				if (key.startsWith("_TOKEN_")) {
					iter.remove();
				}

				else if (key.equalsIgnoreCase("_BACKPAGE_")) {
					iter.remove();
				} else if (key.equalsIgnoreCase("_BACKURL_")) {
					iter.remove();
				}

				else if (key.startsWith("BackStack")) {
					iter.remove();
				}

			}
		} catch (ConcurrentModificationException ex) {

			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::service() Errore di concorrenza nella rimozione di attributi dalla sessione", ex);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::service()", ex);

		}

	}

}