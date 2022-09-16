package it.eng.sil.module.patto;

import java.util.List;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.AbstractSimpleModule;

public class DeleteAziendaPattoScelta extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		if (request.getAttribute("PRG_PATTO_SCELTA_UNITA_AZIENDA") instanceof String) {
			doDelete(request, response);
		} else {
			List keys = (List) request.getAttribute("PRG_PATTO_SCELTA_UNITA_AZIENDA");
			boolean ret = false;
			disableMessageIdSuccess();

			for (int i = 0; i < keys.size(); i++) {
				Object key = keys.get(i);
				request.delAttribute("PRG_PATTO_SCELTA_UNITA_AZIENDA");
				request.setAttribute("PRG_PATTO_SCELTA_UNITA_AZIENDA", key);
				ret = doDelete(request, response);

				if (!ret) {
					break;
				}
			}

			if (ret) {
				setMessageIdSuccess(MessageCodes.General.DELETE_SUCCESS);
			}
		}
	}
}