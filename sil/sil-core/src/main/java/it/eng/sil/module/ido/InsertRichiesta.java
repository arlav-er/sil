package it.eng.sil.module.ido;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.util.Sottosistema;

public class InsertRichiesta extends AbstractSimpleModule {

	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;
	private static final String TRUE = "TRUE";

	/*
	 * public void service(SourceBean request, SourceBean response) {
	 * 
	 * doInsert(request, response); }
	 */
	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		int idSuccess = this.disableMessageIdSuccess();
		try {
			BigDecimal prgRichiestaAz = getprgRichiestaAz(request, response);

			if (prgRichiestaAz != null) {
				BigDecimal nNumeroRichiestaAnno = getNumeroRichiestaAnno(request, response);
				setNewKeyinRequest("numRichiesta", nNumeroRichiestaAnno, request);
				sessionContainer.setAttribute("IDO_NEW_PRGRICHIESTAAZ", prgRichiestaAz);
				setKeyinRequest(prgRichiestaAz, request);
				this.setMessageIdSuccess(idSuccess);
				this.setSectionQuerySelect("QUERY_INSERT");

				// INIT-PARTE-TEMP
				if (Sottosistema.CM.isOff()) {
					// END-PARTE-TEMP
					doInsertAS(request, response);
					// INIT-PARTE-TEMP
				} else {
					// END-PARTE-TEMP
					doInsertASCM(request, response);
					// INIT-PARTE-TEMP
				}
				// END-PARTE-TEMP

				// doInsert(request,response);
			}
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);

		}
	}

	/**
	 * @param request
	 * @param response
	 */
	private void doInsertAS(SourceBean request, SourceBean response) {
		this.setSectionQueryInsert("QUERY_INSERT_AS");
		doInsert(request, response);
	}

	/**
	 * @param request
	 * @param response
	 */
	private void doInsertASCM(SourceBean request, SourceBean response) {
		this.setSectionQueryInsert("QUERY_INSERT_CM");
		doInsert(request, response);
	}

	/**
	 * @param request
	 * @param response
	 */
	private boolean doInsertOld(SourceBean request, SourceBean response) {
		String fieldProgressivo = null;
		// recupero lo statement senza paramtri per l'Art.16
		SourceBean statement = (SourceBean) getConfig().getAttribute("INSERT_DO_RICHIESTA_AZ_OLD");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		if ((fieldProgressivo != null) && (fieldProgressivo.length() > 0)) {
			int idSuccess = disableMessageIdSuccess();
			Object prog = doNextVal(request, response);
			setMessageIdSuccess(idSuccess);

			if (prog == null) {
				return false;
			}

			try {
				request.updAttribute(fieldProgressivo, prog);
			} catch (SourceBeanException ex) {
				reportOperation.reportFailure(this.messageIdFail, ex, "doInsert", "request.updAttributefailed");

				return false;
			}
		}

		Boolean esito = new Boolean(false);

		try {
			esito = (Boolean) queryStrategy.executeQuery(statement, "INSERT");
		} catch (QueryStrategyException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doInsert", "queryStrategy.executeQuery failed");

			return false;
		}

		LogUtils.logDebug("doInsert",
				"request [" + ((request == null) ? "" : request.toXML(false)) + "] esito [" + esito + "]", this);

		if ((esito == null) || !esito.booleanValue()) {
			reportOperation.reportFailure(this.messageIdFail);

			return false;
		}

		try {
			response.setAttribute(INSERT_OK, TRUE);
		} catch (SourceBeanException ex) {
			reportOperation.reportFailure(this.messageIdFail, ex, "doInsert", "Cannot assign INSERT_OK in response");
		}

		reportSuccess(reportOperation);

		return true;
	}

	private BigDecimal getprgRichiestaAz(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_SEQUENCE");

		SourceBean beanprgRichiestaAz = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanprgRichiestaAz.getAttribute("ROW.prgRichiestaAz");
	}

	private BigDecimal getNumeroRichiestaAnno(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NUMERO_RICHIESTA");

		SourceBean beanprgNumeroRichiesta = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanprgNumeroRichiesta.getAttribute("ROW.NUMERORICHIESTA");
	}

	private void setKeyinRequest(BigDecimal prgRichiestaAz, SourceBean request) throws Exception {
		if (request.getAttribute("prgRichiestaAz") != null) {
			request.delAttribute("prgRichiestaAz");
		}

		request.setAttribute("prgRichiestaAz", prgRichiestaAz);
	}

	private void setNewKeyinRequest(String nomeCampoRequest, BigDecimal valoreCampoRequest, SourceBean request)
			throws Exception {
		if (request.getAttribute(nomeCampoRequest) != null) {
			request.delAttribute(nomeCampoRequest);
		}
		request.setAttribute(nomeCampoRequest, valoreCampoRequest);
	}

}
