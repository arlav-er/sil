package it.eng.sil.module.ido.art16OnLine;

import java.math.BigDecimal;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.art16online.istanze.CandidaturaType;
import it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnlineProxy;
import it.eng.sil.coop.webservices.art16online.istanze.RequestIstanzaArt16;
import it.eng.sil.coop.webservices.art16online.istanze.ResponseIstanzaArt16;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.security.User;
import it.eng.sil.util.blen.StringUtils;

public class ScaricoIstanze extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8991855991701028914L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ScaricoIstanze.class.getName());

	public static final String END_POINT_NAME = "GetIstanzeOnline";

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal cdnUtente = new BigDecimal(user.getCodut());

		String strPrgRichiestaAz = (String) serviceRequest.getAttribute("PRGRICHIESTAAZ");
		String strPrgRosa = (String) serviceRequest.getAttribute("PRGROSA");
		String strRichiesta = (String) serviceRequest.getAttribute("numrichiesta");
		String strAnno = (String) serviceRequest.getAttribute("numanno");
		String codCpi = (String) serviceRequest.getAttribute("codCpi");

		int numRichiesta = -1, numAnno = -1;

		if (StringUtils.isFilledNoBlank(strRichiesta)) {
			numRichiesta = (new Integer(strRichiesta)).intValue();
		}
		if (StringUtils.isFilledNoBlank(strAnno)) {
			numAnno = (new Integer(strAnno)).intValue();
		}

		MultipleTransactionQueryExecutor transExec = null;

		IstanzeBeanUtils utils = new IstanzeBeanUtils();
		RequestIstanzaArt16 request = new RequestIstanzaArt16();
		ResponseIstanzaArt16 response = null;
		try {
			transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			// nessuna precedente azione di scarico in atto in sessione

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			// recupera endpoint servizio
			String endpointServizio = eps.getUrl(END_POINT_NAME);

			if (numAnno > 0 && numRichiesta > 0 && endpointServizio != null) {
				GestioneIstanzeOnlineProxy proxy = new GestioneIstanzeOnlineProxy();
				proxy.setEndpoint(endpointServizio);
				request.setNumero(numRichiesta);
				request.setAnno(numAnno);
				response = proxy.getIstanze(request);

				if (response != null && response.getEsito() != null) {
					if (response.getEsito().getCodice().equalsIgnoreCase("OK")) {
						serviceResponse.setAttribute("ESITO", "ESITO_OK");
						BigDecimal prgIstanza = utils.gestioneIstanza(response, cdnUtente, strPrgRosa,
								strPrgRichiestaAz, transExec);
						if (prgIstanza != null) {
							serviceRequest.updAttribute("TIPO", "VIEW_SCARICO");
							serviceResponse.setAttribute("PRGISTANZA", prgIstanza);
							CandidaturaType[] elencoCandidature = response.getIstanzaArt16().getListaCandidature();
							BigDecimal cdnGruppo = new BigDecimal(user.getCdnGruppo());
							EseguiAzioniCandidatura threadCandidature = new EseguiAzioniCandidatura(prgIstanza,
									utils.getResponseXsd(), elencoCandidature, cdnUtente, cdnGruppo, strPrgRosa,
									strPrgRichiestaAz, codCpi, sessionContainer);
							threadCandidature.setName("ISTANZA_" + prgIstanza);

							// Avvio l'elaborazione delle candidature
							_logger.info("=========== Scarico istanze online iniziato ===========");
							threadCandidature.start();
							reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
						} else {
							reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
						}
					} else if (response.getEsito().getCodice().equalsIgnoreCase("01")) {
						reportOperation.reportFailure(MessageCodes.IDO.ERR_ASONLINE_RACC_NON_TERM);
					} else if (response.getEsito().getCodice().equalsIgnoreCase("02")) {
						reportOperation.reportFailure(MessageCodes.IDO.ERR_ASONLINE_RICH_ASSENTE);
					} else if (response.getEsito().getCodice().equalsIgnoreCase("03")) {
						reportOperation.reportFailure(MessageCodes.IDO.ERR_ASONLINE_NO_CANDIDAT);
					} else {
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				}
			}

		} catch (Exception e) {
			_logger.error("ScaricoIstanze", e);
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
		} finally {
			if (transExec != null) {
				transExec.closeConnection();
			}
		}
	}

}
