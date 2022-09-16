package it.eng.sil.module.trento;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class MSalvaDocAllegatoStampaParamLav extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MSalvaDocAllegatoStampaParamLav.class.getName());

	public MSalvaDocAllegatoStampaParamLav() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.INSERT_FAIL;
		TransactionQueryExecutor trans = null;
		boolean result = true;
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());
		Object prgDocPadre = request.getAttribute("PRGDOCUMENTO");
		String listaDocAllegati = request.containsAttribute("LISTADOCALLEGATI")
				? request.getAttribute("LISTADOCALLEGATI").toString()
				: "";

		try {
			trans = new TransactionQueryExecutor(getPool());
			this.enableTransactions(trans);
			trans.initTransaction();

			if (!listaDocAllegati.equals("")) {
				Vector vettPrg = StringUtils.split(listaDocAllegati, "#");
				for (int i = 0; i < vettPrg.size(); i++) {
					BigDecimal prgDocCurr = new BigDecimal(vettPrg.get(i).toString());
					Object params[] = new Object[6];
					params[0] = prgDocPadre;
					params[1] = prgDocCurr;
					params[2] = request.getAttribute("presaVisione");
					params[3] = request.getAttribute("caricatoSucc");
					params[4] = userid;
					params[5] = userid;

					Boolean res = (Boolean) trans.executeQuery("INSERT_DOC_ALLEGATO_STAMPA_PARAM", params, "INSERT");
					if (!res.booleanValue())
						throw new Exception("Impossibile inserire il collegamento tra documento padre e allegato");
				}
			}
			trans.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MSalvaDocAllegatoStampaParamLav.service()", ex);
			reportOperation.reportFailure(errorCode);
		}
	}
}
