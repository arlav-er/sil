package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class SaveIscrListeSpeciali extends AbstractSimpleModule {
	private TransactionQueryExecutor transExec;
	private final String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) throws Exception {

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean success = false;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();

			request.delAttribute("NUMKLOCMISCRART1");
			String dataIscrLocal = (String) request.getAttribute("DATISCRLISTAPROV");
			String datFineDB = (String) request.getAttribute("DATFINE");
			String datFine = "";
			setSectionQuerySelect("QUERY_SELECT");
			success = updateIscrListe(request, response);

			if (success) {
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "service()",
					"aggiornamento in transazione");
		}
	}

	private boolean updateIscrListe(SourceBean request, SourceBean response) throws Exception {

		boolean ret = false;
		String CDNUTPUNTEGGIO = "";
		this.setSectionQuerySelect("QUERY_SELECT_NUMKLOCMISCRART1");
		SourceBean row = doSelect(request, response);
		String NUMKLOCMISCRART1 = String.valueOf(((BigDecimal) row.getAttribute("ROW.NUMKLOCMISCRART1")).intValue());
		String PrgIscrArt1 = String.valueOf(((BigDecimal) row.getAttribute("ROW.PRGISCRART1")));
		String numPunteggio = (BigDecimal) row.getAttribute("ROW.NUMPUNTEGGIO") == null ? ""
				: String.valueOf((BigDecimal) row.getAttribute("ROW.NUMPUNTEGGIO"));

		request.setAttribute("NUMKLOCMISCRART1", NUMKLOCMISCRART1);
		String punteggio = StringUtils.getAttributeStrNotNull(request, "NUMPUNTEGGIO");
		// i campi cdnutpunteggio e datmodifpunteggio in fase di modifica,
		// vanno valorizzati solo se il punteggio passa da x -> y oppure da NULL --> y
		if (!punteggio.equals("")) {
			if ((!numPunteggio.equals(punteggio))) {
				Date dataNow = new Date();
				String DATMODIFPUNTEGGIO = "";
				if (dataNow != null) {
					// formattatore per trasformare stringhe in date
					SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					DATMODIFPUNTEGGIO = fd.format(dataNow);
				}
				RequestContainer requestContainer = getRequestContainer();
				SessionContainer sessionContainer = requestContainer.getSessionContainer();
				User user = (User) sessionContainer.getAttribute(User.USERID);
				CDNUTPUNTEGGIO = String.valueOf(user.getCodut());
				request.updAttribute("DATMODIFPUNTEGGIO", DATMODIFPUNTEGGIO);
				request.updAttribute("CDNUTPUNTEGGIO", CDNUTPUNTEGGIO);
			}
		}
		this.setSectionQueryUpdate("QUERY_UPDATE_LISTESPECIALI");

		ret = doUpdate(request, response);

		response.delAttribute("PRGISCRART1");
		response.setAttribute("PRGISCRART1", PrgIscrArt1);
		response.delAttribute("CDNUTPUNTEGGIO");
		response.setAttribute("CDNUTPUNTEGGIO", CDNUTPUNTEGGIO);

		return ret;
	}
}
