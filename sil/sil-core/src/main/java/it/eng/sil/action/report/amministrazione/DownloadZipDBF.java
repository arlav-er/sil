/*
 * Created on 26-set-07
 *
 */
package it.eng.sil.action.report.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;

/**
 * @author vuoto
 * 
 */

public class DownloadZipDBF extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DownloadZipDBF.class.getName());
	private final String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + "::service()");

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		sessionContainer.delAttribute(MobilitaEsportaDBF.MOBILITA_ESPORTATORE_THREAD);

	}
}