package it.eng.sil.module.voucher;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.profil.Properties;
import it.eng.sil.security.User;

public class GetComboRisultatoTDA extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7542127843473063715L;

	@SuppressWarnings("unused")
	public void service(SourceBean request, SourceBean response) {

		disableMessageIdFail();
		disableMessageIdSuccess();

		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		if (user.getCodTipo().equalsIgnoreCase(Properties.TTPO_GRUPPO_SOGGETTI_ACCREDITATI)) {
			this.setSectionQuerySelect("QUERIES.SELECT_QUERY_SOGG_ACC");
		} else {
			this.setSectionQuerySelect("QUERIES.SELECT_QUERY_CPI");
		}

		doSelect(request, response);

	}
}
