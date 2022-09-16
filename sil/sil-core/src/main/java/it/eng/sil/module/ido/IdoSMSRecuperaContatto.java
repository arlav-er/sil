/*
 * Creato il 22-ago-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import java.util.StringTokenizer;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.sms.ContattoSMS;
import it.eng.sil.sms.Sms;

/**
 * @author gritti
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class IdoSMSRecuperaContatto extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		SourceBean response_rows = new SourceBean("ROWS");
		// valore del CHECKBOX nella lista
		String prgStr = (String) request.getAttribute("V_PRGNOMINATIVO");
		// tokenizer per reperire il prgNominativo dalla stringa separata da ","
		// nella request
		StringTokenizer st = new StringTokenizer(prgStr, ",");
		while (st.hasMoreTokens()) {
			prgStr = st.nextToken();

			request.updAttribute("V_PRGNOMINATIVO", prgStr);

			// query per ottenere parametri per l'sms
			this.setSectionQuerySelect("QUERY_SELECT_DATI");
			SourceBean recupera = doSelect(request, response, false);

			// SourceBean di risposta
			SourceBean response_row = new SourceBean("ROW");

			response_row.setAttribute("STRCELL",
					Sms.cleanCellNumber(StringUtils.getAttributeStrNotNull(recupera, "ROW.STRCELL")));
			response_row.setAttribute("STRCOGNOME", StringUtils.getAttributeStrNotNull(recupera, "ROW.STRCOGNOME"));
			response_row.setAttribute("STRNOME", StringUtils.getAttributeStrNotNull(recupera, "ROW.STRNOME"));
			response_row.setAttribute("STRCODICEFISCALE",
					StringUtils.getAttributeStrNotNull(recupera, "ROW.STRCODICEFISCALE"));
			response_row.setAttribute("CDNLAVORATORE", recupera.getAttribute("ROW.CDNLAVORATORE"));

			// response_row.setAttribute("DATA",
			// StringUtils.getAttributeStrNotNull(recupera, "ROW.DATA"));
			// response_row.setAttribute("ORA",
			// StringUtils.getAttributeStrNotNull(recupera, "ROW.ORA"));
			// response_row.setAttribute("STRDESCRIZIONE",
			// StringUtils.getAttributeStrNotNull(recupera,
			// "ROW.STRDESCRIZIONE"));

			response_row.setAttribute("PRGRICHIESTAORIG", request.getAttribute("PRGRICHIESTAORIG"));
			response_row.setAttribute("PRGRICHIESTAAZ", request.getAttribute("PRGRICHIESTAAZ"));
			response_row.setAttribute("PRGROSA", request.getAttribute("PRGROSA"));
			response_row.setAttribute("testosms", request.getAttribute("testosms"));
			response_row.setAttribute("PRGTIPOROSA", request.getAttribute("PRGTIPOROSA"));

			response_rows.setAttribute(response_row);

		}

		ContattoSMS contattoSms = new ContattoSMS();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		// richiamo la classe per la creazione e l'invio del SMS
		SourceBean nonInviatiSB = contattoSms.creaPerRosaGrezza(response_rows, user);
		response.setAttribute(response_rows);
		response.setAttribute(nonInviatiSB);

	}
}
