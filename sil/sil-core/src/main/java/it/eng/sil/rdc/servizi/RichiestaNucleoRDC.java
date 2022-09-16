package it.eng.sil.rdc.servizi;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;
import it.gov.lavoro.servizi.serviceRDC.RDCProxy;
import it.gov.lavoro.servizi.serviceRDC.types.Beneficiario_Type;
import it.gov.lavoro.servizi.serviceRDC.types.Richiesta_RDC_beneficiari_dato_CodiceFiscale;
import it.gov.lavoro.servizi.serviceRDC.types.Richiesta_RDC_beneficiari_dato_codProtocolloInps;
import it.gov.lavoro.servizi.serviceRDC.types.Risposta_servizio_RDC_Type;

public class RichiestaNucleoRDC extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2968393982471851527L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RichiestaNucleoRDC.class.getName());

	private String END_POINT_NAME_CF = "ricevi_RDC_by_codiceFiscale";
	private String END_POINT_NAME_INPS = "ricevi_RDC_by_codProtocolloInps";

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		String strCodiceFiscale = null;
		String codProtocolloInps = null;

		Richiesta_RDC_beneficiari_dato_CodiceFiscale cfInput = null;
		Richiesta_RDC_beneficiari_dato_codProtocolloInps inpsInput = null;
		Risposta_servizio_RDC_Type rispostaWS = null;

		RDCBean supportoRdc = new RDCBean();

		String tipoRicercaName = Utils.notNull(serviceRequest.getAttribute("TIPO_RICERCA"));

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

			String endPoint = null;
			RDCProxy proxy = null;

			if (StringUtils.isFilledNoBlank(tipoRicercaName) && tipoRicercaName.equalsIgnoreCase("CF")) {
				strCodiceFiscale = Utils.notNull(serviceRequest.getAttribute("cfBeneficiario"));
				cfInput = new Richiesta_RDC_beneficiari_dato_CodiceFiscale(strCodiceFiscale);
				endPoint = eps.getUrl(END_POINT_NAME_CF);
				proxy = new RDCProxy(endPoint);
				rispostaWS = proxy.ricevi_RDC_by_codiceFiscale(cfInput);
			} else if (StringUtils.isFilledNoBlank(tipoRicercaName) && tipoRicercaName.equalsIgnoreCase("INPS")) {
				codProtocolloInps = Utils.notNull(serviceRequest.getAttribute("codDomandaINPS"));
				inpsInput = new Richiesta_RDC_beneficiari_dato_codProtocolloInps(codProtocolloInps);
				endPoint = eps.getUrl(END_POINT_NAME_INPS);
				proxy = new RDCProxy(endPoint);
				rispostaWS = proxy.ricevi_RDC_by_codProtocolloInps(inpsInput);
			}

			if (rispostaWS.getEsito().getCodEsito().equalsIgnoreCase("E100")) {
				Beneficiario_Type[] allNucleo = rispostaWS.getBeneficiari();
				if (allNucleo != null && allNucleo.length > 0) {

					for (int i = 0; i < allNucleo.length; i++) {
						// getCodiceComune
						Beneficiario_Type beneficiario = allNucleo[i];
						beneficiario.setCod_comune_nascita(
								supportoRdc.getCodiceComune(beneficiario.getCod_comune_nascita()));
						allNucleo[i] = beneficiario;
					}

					serviceResponse.setAttribute("SIZE", new Integer(allNucleo.length));
					serviceResponse.setAttribute("RDC_NUCELO_FAM", allNucleo);
				}
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			} else if (rispostaWS.getEsito().getCodEsito().equalsIgnoreCase("E101")) {
				reportOperation.reportFailure(MessageCodes.RDC.NO_RESULT_FOUND);
				return;
			} else {
				String codiceEsito = rispostaWS.getEsito().getCodEsito();
				String descrizione = rispostaWS.getEsito().getMessaggioErrore();
				serviceResponse.setAttribute("RDC_ERRORE", codiceEsito.concat(" - ").concat(descrizione));
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				return;
			}
		} catch (Throwable e) {
			reportOperation.reportFailure(MessageCodes.RDC.WS_NUCLEOFAM_RDC);
			_logger.error("Errore: " + e);
		}

	}
}