/*
 * Created on 13-mar-07
 */

package it.eng.sil.module.profil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.security.handlers.Utility;

/**
 * @author vuoto
 */

public class ProfWsSecuritySalva extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfWsSecuritySalva.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + "::service()");

		HttpServletRequest httpReq = getHttpRequest();

		List parNames = new ArrayList();
		Enumeration _enum = httpReq.getParameterNames();
		while (_enum.hasMoreElements()) {
			String name = (String) _enum.nextElement();
			if (!name.equalsIgnoreCase("page"))
				parNames.add(name);
		}

		Collections.sort(parNames);

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(Utility.getWsSecurityPropsFile()));

			Iterator iter = parNames.iterator();
			while (iter.hasNext()) {
				String ordName = (String) iter.next();
				String value = httpReq.getParameter(ordName);
				String name = ordName.substring(4)/* .replace('_', '.') */;

				if (name.equals("mittente")) {
					value = Utility.encrypt(value);
				}

				String riga = name + "=" + value;

				bw.write(riga + "\r\n");

				_logger.debug(riga);

			}

			bw.close();

			MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_SUCCESS);

		} catch (Exception ex) {

			it.eng.sil.util.TraceWrapper.debug(_logger, "Errora nella scrittura del file di proprietà", ex);

			this.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));

		}

	}
}