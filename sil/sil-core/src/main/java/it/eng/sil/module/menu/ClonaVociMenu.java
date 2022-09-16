/*
 * Creato il 10-nov-04
 * Author: vuoto
 * 
 */

package it.eng.sil.module.menu;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

/**
 * @author vuoto
 * 
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.bean.menu.Menu;

public class ClonaVociMenu extends AbstractModule {

	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult result = new ReportOperationResult(this, response);

		SessionContainer session = getRequestContainer().getSessionContainer();
		Menu menu = (Menu) session.getAttribute("menu");

		try {
			int menuClonato = menu.clonaMenu();
			if (menuClonato >= 0) {
				menu.setCdnMenu(menuClonato);
				result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			} else {
				result.reportFailure(MessageCodes.General.OPERATION_FAIL);
			}
		} catch (Exception e) {
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}

	}
}
