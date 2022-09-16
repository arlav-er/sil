package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

public class DynGetDataInvioRosae implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynGetDataInvioRosae.class.getName());

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String query = "";

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();
		String prgRosa = (String) req.getAttribute("prgRosa");
		String prgRichAzienda = (String) req.getAttribute("prgRichAzienda");
		if (!prgRosa.equals("0")) {
			query = "SELECT TO_CHAR(DO_ROSA.DATINVIO,'DD/MM/YYYY') DATINVIO FROM DO_ROSA WHERE DO_ROSA.PRGROSA = "
					+ prgRosa;
		} else {
			query = "SELECT TO_CHAR(DO_ROSA.DATINVIO,'DD/MM/YYYY') DATINVIO FROM DO_ROSA, DO_INCROCIO "
					+ "WHERE DO_ROSA.PRGINCROCIO = DO_INCROCIO.PRGINCROCIO AND DO_INCROCIO.PRGRICHIESTAAZ ="
					+ prgRichAzienda;
		}

		_logger.debug(className + "::Recupera data invio: " + query);

		return query;

	}// getStatement

}// class DynGetDataInvioRosae
