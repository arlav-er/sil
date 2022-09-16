package it.eng.sil.module.cig;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.webservices.corsoCIG.InvioWsSifer;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class InvioWsSiferModule extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertDomandaCig.class.getName());
	private final String className = StringUtils.getClassName(this);
	private String prc;

	BigDecimal userid;
	private ReportOperationResult reportOperation;
	private int msgCode;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		if (user != null) {
			userid = new BigDecimal(user.getCodut());
		}

		int idSuccess = this.disableMessageIdSuccess();

		reportOperation = new ReportOperationResult(this, response);
		// String statement = "";
		msgCode = MessageCodes.General.OPERATION_FAIL;
		// String msg = null;
		// SourceBean result = null;
		InvioWsSifer wsSifer = new InvioWsSifer();
		SimpleDateFormat str2date = new SimpleDateFormat("dd/MM/yyyy");
		String strInizio = StringUtils.getAttributeStrNotNull(request, "INIZIO");
		Date inizio = str2date.parse(strInizio);
		String strFine = StringUtils.getAttributeStrNotNull(request, "FINE");
		Date fine = str2date.parse(strFine);
		String codiceFiscale = StringUtils.getAttributeStrNotNull(request, "CODICEFISCALE");
		// FIXME - solo per fini di test
		String xmlGenerato = wsSifer.inviaWsLavAll(inizio, fine, codiceFiscale);
		if (StringUtils.isFilledNoBlank(xmlGenerato)) {
			response.setAttribute("xmlGenerato", xmlGenerato);
		}
	}

	private BigDecimal getprgAltraIscr(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");
		SourceBean beanprgAltraIscr = (SourceBean) doSelect(request, response);
		return (BigDecimal) beanprgAltraIscr.getAttribute("ROW.PRGALTRAISCR");
	}

	private void setKeyinRequest(BigDecimal PRGALTRAISCR, SourceBean request) throws Exception {
		if (request.getAttribute("PRGALTRAISCR") != null) {
			request.delAttribute("PRGALTRAISCR");
		}
		request.setAttribute("PRGALTRAISCR", PRGALTRAISCR);
		request.setAttribute("strChiaveTabella", PRGALTRAISCR.toString());
	}
}
