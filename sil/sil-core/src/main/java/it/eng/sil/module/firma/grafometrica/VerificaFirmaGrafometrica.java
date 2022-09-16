package it.eng.sil.module.firma.grafometrica;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.consenso.Consenso;
import it.eng.sil.module.consenso.ConsensoFirmaBean;
import it.eng.sil.module.consenso.GConstants;
import it.eng.sil.security.ProfileDataFilter;
import it.eng.sil.security.User;

public class VerificaFirmaGrafometrica extends AbstractHttpModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
			String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");

			if (prgAzienda == null || prgAzienda.equals("")) {
				if (cdnLavoratore.equals("")) {
					cdnLavoratore = null;
				}
				if (cdnLavoratore != null) {
					Consenso consenso = new Consenso(null);
					ConsensoFirmaBean cfb = consenso.getConsensoFirma(cdnLavoratore);

					String codiceConsenso = cfb.getCodiceStatoConsenso();
					if (StringUtils.isEmpty(codiceConsenso)) {
						codiceConsenso = GConstants.CONSENSO_ASSENTE_CODICE;
					}

					String statoConsenso = consenso.getStatoConsensoFirma(codiceConsenso);

					serviceResponse.setAttribute("statoConsenso", statoConsenso);
					serviceResponse.setAttribute("codStatoConsenso", codiceConsenso);

					if (StringUtils.isEmpty(codiceConsenso)
							|| codiceConsenso.equals(GConstants.CONSENSO_ASSENTE_CODICE)) {
						serviceResponse.setAttribute("viewGestioneConsensoBtn", "si");
						User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
						ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
						Vector param = new Vector();
						if (filterConsenso.canView()) {
							param.add(
									"<a href=\"#\" name=\"btnGestioneConsenso\" onClick=\"apriGestioneConsenso()\" >\"Gestione Consenso\"</a>");

						} else {
							param.add("\"Gestione Consenso\"");
						}
						MessageAppender.appendMessage(serviceResponse, MessageCodes.FirmaGrafometrica.CONSENSO_ASSENTE,
								param);
					} else if (codiceConsenso.equals(GConstants.CONSENSO_NON_DISPONIBILE_CODICE)) {
						reportOperation.reportSuccess(MessageCodes.FirmaGrafometrica.CONSENSO_NON_DISPONIBILE);
					} else if (codiceConsenso.equals(GConstants.CONSENSO_REVOCATO_CODICE)) {
						reportOperation.reportSuccess(MessageCodes.FirmaGrafometrica.CONSENSO_REVOCATO);
					} else if (codiceConsenso.equals(GConstants.CONSENSO_ATTIVO_CODICE)) {
						serviceResponse.setAttribute("consensoFirmaAttivo", "si");
						HttpServletRequest request = getHttpRequest();
						String remoteHost = request.getRemoteHost();
						String ipRemoteHost = request.getRemoteAddr();

						String address = "127.0.1.1";
						if (!StringUtils.isEmpty(ipRemoteHost)) {
							address = ipRemoteHost;
						}

						if (!StringUtils.isEmpty(remoteHost)) {
							if (!ipRemoteHost.equalsIgnoreCase(remoteHost)) {
								address += "/" + remoteHost;
							}
						}

						serviceResponse.setAttribute("ipOperatore", address);
						reportOperation.reportSuccess(MessageCodes.FirmaGrafometrica.CONSENSO_ATTIVO);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, true, "CdnLavoratore null");
				}
			}
		} catch (SourceBeanException e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			e.printStackTrace();
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			e.printStackTrace();
		}
	}

}
