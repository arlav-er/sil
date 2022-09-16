/*
 * Creato il 22-gen-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.config;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.util.Utils;

/**
 * @author Rodi Alessandro
 * 
 *         Modulo per estrarre da database un valore per una certa configurazione. Il modulo accetta come parametro in
 *         ingresso COD_TIPO_CONFIG di tipo stringa.
 * 
 */

public class GetConfigValue extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetConfigValue.class.getName());
	private String parametroConf;

	public GetConfigValue() {
	}

	public void service(SourceBean request, SourceBean response) {

		try {
			parametroConf = (String) request.getAttribute("COD_TIPO_CONFIG");

			// SourceBean result = (SourceBean) ex.exec();
			SourceBean result = null;

			if (parametroConf != null && !"".equals(parametroConf)) {

				result = Utils.getConfigValue(parametroConf);
			}

			// System.out.println("");

			// String codprovinciasil = (String) result.getAttribute("ROW.CODPROVINCIASIL");
			// String codtipoconfig = (String) result.getAttribute("ROW.NUM");

			response.setAttribute(result);

		} catch (EMFInternalError e) {
			_logger.error("Errore nell'esecuzione di GetConfigValue.Parametro: " + parametroConf, e);
		} catch (SourceBeanException e) {
			_logger.error("Errore nell'esecuzione di GetConfigValue.Parametro: " + parametroConf, e);
		}

	}
}