package it.eng.sil.module.agenda;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.sms.ContattoSMS;

public class InvioSMSCig extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();

		SourceBean response_rows = new SourceBean("ROWS");
		SourceBean response_row = new SourceBean("ROW");

		response_row.setAttribute("textSms", request.getAttribute("textSms"));
		response_row.setAttribute("strCell", request.getAttribute("strCell"));
		response_row.setAttribute("cdnLavoratore", request.getAttribute("cdnLavoratore"));

		response_rows.setAttribute(response_row);

		ContattoSMS contattoSms = new ContattoSMS();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		// richiamo la classe per la creazione e l'invio del SMS
		SourceBean nonInviatiSB = contattoSms.creaPerPromemoriaCIG(response_rows, user);

		Vector smsNonInviati = nonInviatiSB.getAttributeAsVector("LAVORATORE");

		int numSmsNonInviati = smsNonInviati.size();

		String errCode = (String) nonInviatiSB.getAttribute("ERROR");

		if (numSmsNonInviati > 0)
			reportOperation.reportFailure(MessageCodes.CIG.ERROR_INVIO_SMS_PROMEMORIA_CIG);
		else if ((errCode != null) || ("".equals(errCode))) {
			reportOperation.reportFailure(MessageCodes.SMS.ERRORE_INVIO_SERVER);
		} else
			reportOperation.reportSuccess(idSuccess);

		response.setAttribute(response_rows);
		response.setAttribute(nonInviatiSB);
	}

}
