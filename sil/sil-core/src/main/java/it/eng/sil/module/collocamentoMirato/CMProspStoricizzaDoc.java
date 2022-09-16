package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class CMProspStoricizzaDoc extends AbstractSimpleModule {
	private final String className = StringUtils.getClassName(this);
	private TransactionQueryExecutor transExecutor;
	BigDecimal userid;
	BigDecimal prgDocumento = null;
	BigDecimal numAnnoProt = null;
	BigDecimal numProtocollo = null;
	Vector rowsProt = null;
	SourceBean rowProt = null;
	String datProtocollazione = "";
	String docInOut = "";
	String docRif = "";
	String docTipo = "";
	String docCodRif = "";
	String docCodTipo = "";
	Object cdnUtIns = null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.UPDATE_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			ArrayList warnings = new ArrayList();
			transExecutor = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExecutor);

			transExecutor.initTransaction();

			boolean success = false;
			boolean successProt = false;

			this.setSectionQuerySelect("QUERY_SELECT_DOC");
			SourceBean rowDoc = this.doSelect(request, response);

			if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
				prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");
				docInOut = (String) rowDoc.getAttribute("ROW.striodoc");
				docRif = (String) rowDoc.getAttribute("ROW.strambitodoc");
				docTipo = (String) rowDoc.getAttribute("ROW.strtipodoc");
				docCodRif = (String) rowDoc.getAttribute("ROW.codambitodoc");
				docCodTipo = (String) rowDoc.getAttribute("ROW.codtipodoc");

			}

			String protocolla = StringUtils.getAttributeStrNotNull(request, "protocolla");

			if (prgDocumento != null) {
				if (protocolla.equals("S")) {
					this.setSectionQuerySelect("GET_PROTOCOLLAZIONE");
					SourceBean rowProt = this.doSelect(request, response);
					if (rowProt.containsAttribute("ROW")) {
						numAnnoProt = (BigDecimal) rowProt.getAttribute("ROW.NUMANNOPROT");
						numProtocollo = (BigDecimal) rowProt.getAttribute("ROW.NUMPROTOCOLLO");
						datProtocollazione = (String) rowProt.getAttribute("ROW.DATAORAPROT");

					}

					String numkloDocumento = String
							.valueOf(((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
					request.setAttribute("prgDocumento", prgDocumento);
					request.setAttribute("numkloDocumento", numkloDocumento);

					request.setAttribute("userid", userid);

					String datInizio = (String) rowDoc.getAttribute("ROW.DATINIZIO");
					String datAcqRil = (String) rowDoc.getAttribute("ROW.DATACQRIL");
					request.setAttribute("DatInizio", datInizio);
					request.setAttribute("DatAcqril", datAcqRil);

					request.setAttribute("numAnnoProt", numAnnoProt);
					request.setAttribute("numProtocollo", numProtocollo);
					request.setAttribute("dataOraProt", datProtocollazione);

					request.setAttribute("tipoProt", "S");
					request.setAttribute("codAmbito", docCodRif);
					request.setAttribute("strambitodoc", docRif);
					request.setAttribute("strtipodoc", docTipo);
					request.delAttribute("codStatoAtto");
					request.setAttribute("codStatoAtto", "PR");
					request.setAttribute("codTipoDocumento", docCodTipo);

					request.setAttribute("striodoc", docInOut);
					request.setAttribute("FlgCodMonoIO", docInOut);

					request.setAttribute("aggiornaDoc", "aggiornaDoc");
					String numannorifprospetto = StringUtils.getAttributeStrNotNull(request, "numannorifprospetto");
					String descr = "Prospetto Informativo " + numannorifprospetto;

					request.setAttribute("codCpi", user.getCodRif());
					request.setAttribute("strDescrizione", descr);
					CMProspDatiGenIns prosp = new CMProspDatiGenIns();
					prosp.insUpdDocumento(request, transExecutor);
					successProt = true;
				}
			} else
				throw new Exception();

			if (successProt) {
				transExecutor.commitTransaction();
				ProtocolloDocumentoUtil.cancellaFileDocarea();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExecutor.rollBackTransaction();
			}
		} catch (Exception e) {
			transExecutor.rollBackTransaction();
			reportOperation.reportFailure(e, "service()", "aggiornamento in transazione");
		}
	}
}