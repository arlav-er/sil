package it.eng.sil.action.report.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractHttpAction;

import it.eng.sil.bean.MobilitaExpThread;

/**
 * @author Alessandro Pegoraro 19/09/2007
 * 
 */
public class MobilitaEsportaDBF extends AbstractHttpAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MobilitaEsportaDBF.class.getName());
	private final String className = this.getClass().getName();

	public static final String MOBILITA_ESPORTATORE_THREAD = "_MOBILITA_ESPORTATORE_THREAD_";

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + "::service()");

		RequestContainer requestContainer = getRequestContainer();

		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		String exptut = request.getAttribute("sentoo") == null ? "N" : (String) request.getAttribute("sentoo");
		MobilitaExpThread mobT = (MobilitaExpThread) sessionContainer.getAttribute(MOBILITA_ESPORTATORE_THREAD);
		// String sid = getHttpRequest().getSession().getId();
		String sid = "" + System.currentTimeMillis();
		if (mobT == null) {

			mobT = new MobilitaExpThread(sessionContainer);
			sessionContainer.setAttribute(MOBILITA_ESPORTATORE_THREAD, mobT);
			sessionContainer.setAttribute("ESPORTA_ANCHE_INVIATI", exptut);
			sessionContainer.setAttribute("ID_SESSIONE_STAMPA", sid);

			// sessionContainer.setAttribute("HTTP_SESSION_ID", requestContainer.getSession().getId());
			Thread t = new Thread(mobT);
			t.start();

		}

	}
}