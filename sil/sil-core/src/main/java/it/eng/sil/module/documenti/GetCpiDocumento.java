package it.eng.sil.module.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * Recupera le informazioni del CPI per il documento passatogli (nella REQUESTE come oggetto di classe Documento). Se
 * l'oggetto non viene passato oppure se contiene un CODCPI nullo o vuoto, viene usato il CODCPI dell'UTENTE CORRENTE e
 * recuperati e resi i suoi dati.
 * 
 * @author Luigi Antenucci
 */
public class GetCpiDocumento extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetCpiDocumento.class.getName());

	protected String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		_logger.debug(className + ".service() INIZIO");

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		String codCpiDaUsare = user.getCodRif(); // default: uso codice cpi
													// dell'UTENTE!
		_logger.debug("CODCPI utente: [" + codCpiDaUsare + "]");

		// Se c'è un documento, provo a recuperarlo da l'
		Object docObj = request.getAttribute("documento");
		if ((docObj != null) && (docObj instanceof Documento)) {
			Documento doc = (Documento) docObj;
			String codCpiDoc = doc.getCodCpi();

			if (StringUtils.isFilled(codCpiDoc)) {
				_logger.debug("CODCPI letto da documento: [" + codCpiDoc + "]");

				codCpiDaUsare = codCpiDoc;
			}
		}

		request.setAttribute("codCpi", codCpiDaUsare);
		doSelect(request, response);

		_logger.debug(className + ".service() FINE");

	}

}