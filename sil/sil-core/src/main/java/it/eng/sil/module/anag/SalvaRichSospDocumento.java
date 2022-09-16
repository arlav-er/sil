/*
 * Creato il 12-dic-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class SalvaRichSospDocumento extends AbstractSimpleModule {
	private final String className = StringUtils.getClassName(this);
	private final String updRichGrad = "Aggiornamento Richiesta Sospensione effettuata con successo";
	private TransactionQueryExecutor transExecutor;
	BigDecimal userid, prgDocumento = null;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExecutor = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExecutor);

			transExecutor.initTransaction();

			boolean ret = false;

			this.setSectionQuerySelect("QUERY_SELECT_DOC");
			SourceBean rowDoc = this.doSelect(request, response);

			if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
				prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");
			}

			String codStatoAtto = StringUtils.getAttributeStrNotNull(request, "CODSTATOATTO");
			String codStatoAttoOld = (String) rowDoc.getAttribute("ROW.CODSTATOATTO");

			if (prgDocumento != null) {
				if (!codStatoAtto.equalsIgnoreCase(codStatoAttoOld)) {
					String numkloDocumento = String
							.valueOf(((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
					request.setAttribute("prgDocumento", prgDocumento);
					request.setAttribute("numkloDocumento", numkloDocumento);

					String datInizio = (String) rowDoc.getAttribute("ROW.DATINIZIO");
					String datAcqRil = (String) rowDoc.getAttribute("ROW.DATACQRIL");
					request.setAttribute("DatInizio", datInizio);
					request.setAttribute("DatAcqril", datAcqRil);
					updDocumento(request);
					ret = true;
				}
			} else
				throw new Exception();

			if (ret) {
				transExecutor.commitTransaction();
				ProtocolloDocumentoUtil.cancellaFileDocarea();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExecutor.rollBackTransaction();
			}
		} catch (Exception e) {
			transExecutor.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "update in transazione");
		}
	}

	private void updDocumento(SourceBean request) throws Exception {
		Documento doc = new Documento(prgDocumento);

		doc.setPrgDocumento(prgDocumento);
		doc.setNumKloDocumento(getAttributeAsBigDecimal(request, "numkloDocumento"));
		doc.setCodCpi(getAttributeAsString(request, "codCpi"));
		doc.setCdnLavoratore(getAttributeAsBigDecimal(request, "cdnLavoratore"));
		doc.setPrgAzienda(getAttributeAsBigDecimal(request, "prgAzienda"));
		doc.setPrgUnita(getAttributeAsBigDecimal(request, "prgUnita"));

		doc.setCodTipoDocumento(getAttributeAsString(request, "codTipoDocumento"));
		doc.setFlgAutocertificazione(getAttributeAsString(request, "flgAutocertificazione"));
		doc.setStrDescrizione(getAttributeAsString(request, "strDescrizione"));
		doc.setFlgDocAmm(getAttributeAsString(request, "flgDocAmm"));
		doc.setFlgDocIdentifP(getAttributeAsString(request, "flgDocIdentifP"));
		doc.setDatInizio(getAttributeAsString(request, "DatInizio"));
		doc.setStrNumDoc(getAttributeAsString(request, "StrNumDoc"));
		doc.setStrEnteRilascio(getAttributeAsString(request, "StrEnteRilascio"));
		doc.setCodMonoIO(getAttributeAsString(request, "FlgCodMonoIO"));
		doc.setDatAcqril(getAttributeAsString(request, "DatAcqril"));
		doc.setCodModalitaAcqril(getAttributeAsString(request, "codModalitaAcqril"));
		doc.setCodTipoFile(getAttributeAsString(request, "codTipoFile"));
		doc.setStrNomeDoc(getAttributeAsString(request, "strNomeDoc"));
		doc.setDatFine(getAttributeAsString(request, "dataFine"));
		doc.setNumAnnoProt(getAttributeAsBigDecimal(request, "numAnnoProt"));
		doc.setNumProtocollo(getAttributeAsBigDecimal(request, "numProtocollo"));
		doc.setDatProtocollazione(getAttributeAsString(request, "dataOraProt"));
		doc.setTipoProt(getAttributeAsString(request, "tipoProt"));
		doc.setStrNote(getAttributeAsString(request, "strNote"));
		doc.setNumKeyLock(getAttributeAsBigDecimal(request, "KLOCKPROT"));
		doc.setChiaveTabella(getAttributeAsString(request, "strChiaveTabella"));
		doc.setCodMotAnnullamentoAtto(getAttributeAsString(request, "codMotAnnullamentoAtto"));
		doc.setCodStatoAtto(getAttributeAsString(request, "CODSTATOATTO_P"));

		String pagina = getAttributeAsString(request, "PAGE");
		if (!pagina.equalsIgnoreCase("null"))
			doc.setPagina(pagina);
		doc.setCdnUtMod(userid);
		// 03/04/2007 DOCAREA.
		ProtocolloDocumentoUtil.putInRequest(doc);
		doc.update(transExecutor);
	}

	private String getAttributeAsString(SourceBean request, String param) {
		return SourceBeanUtils.getAttrStrNotNull(request, param);
	}

	private BigDecimal getAttributeAsBigDecimal(SourceBean request, String param) {
		String tmp = SourceBeanUtils.getAttrStrNotNull(request, param);
		if (!tmp.equals("")) {
			return new BigDecimal(tmp);
		}
		return null;
	}
}