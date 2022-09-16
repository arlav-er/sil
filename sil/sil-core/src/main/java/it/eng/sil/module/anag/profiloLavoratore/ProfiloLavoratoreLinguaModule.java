package it.eng.sil.module.anag.profiloLavoratore;

import java.math.BigDecimal;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.anag.profiloLavoratore.bean.ProfiloLavoratore;
import it.eng.sil.security.User;

public class ProfiloLavoratoreLinguaModule extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ProfiloLavoratoreLinguaModule.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor tqe = null;
		try {
			String controlloCalcolo = StringUtils.getAttributeStrNotNull(serviceRequest, "TIPO_OPERAZIONE");
			String prgProfiloLavStr = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGLAVORATOREPROFILO");
			String cdnLavoratoreStr = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
			String flgLingua = StringUtils.getAttributeStrNotNull(serviceRequest, "flgLinguaIta");
			BigDecimal cdnLavoratore = new BigDecimal(cdnLavoratoreStr);
			SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			BigDecimal userid = new BigDecimal(user.getCodut());
			String codCpiRif = user.getCodRif();
			boolean esito = false;
			ProfiloLavoratore profLav = null;
			boolean isNuovo = StringUtils.isEmptyNoBlank(prgProfiloLavStr);
			if (StringUtils.isFilledNoBlank(controlloCalcolo)
					&& controlloCalcolo.equalsIgnoreCase(Decodifica.SALVA_FLG_LINGUA)) {
				tqe = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				tqe.initTransaction();

				if (isNuovo) {
					profLav = new ProfiloLavoratore(tqe, cdnLavoratore, codCpiRif, userid);
				} else {
					// caso update
					BigDecimal prgProfiloLav = new BigDecimal(prgProfiloLavStr);
					profLav = new ProfiloLavoratore(tqe, prgProfiloLav, cdnLavoratore, codCpiRif, userid);
				}
				profLav.setFlgConoscenzaIta(flgLingua);
				if (flgLingua.equalsIgnoreCase("N")) {
					profLav.setNumValoreProfilo(new Float("10"));
					profLav.setCodVchProfiling(Decodifica.IndiceProfilatura.MOLTO_ALTA);
				}
				esito = isNuovo ? profLav.creaProfiloLavoratore() : profLav.aggiornaProfiloFlgLingua();

				if (esito) {
					serviceResponse.setAttribute("PRGLAVORATOREPROFILO", profLav.getPrgLavoratoreProfilo());
					if (isNuovo) {
						reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
					} else {
						reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
					}
					tqe.commitTransaction();
				} else {
					if (StringUtils.isEmptyNoBlank(prgProfiloLavStr)) {
						reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
					} else {
						serviceResponse.setAttribute("PRGLAVORATOREPROFILO", profLav.getPrgLavoratoreProfilo());
						reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
					}
					tqe.rollBackTransaction();
				}
			}

		} catch (Exception ex) {
			tqe.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.debug(_logger, "ProfiloLavoratoreLinguaModule::service()", ex);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}

	}

}
