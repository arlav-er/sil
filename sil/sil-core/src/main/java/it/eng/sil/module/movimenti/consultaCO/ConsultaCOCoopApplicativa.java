package it.eng.sil.module.movimenti.consultaCO;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.gov.lavoro.servizi.RicercaCO._2_0.COPerLavoratoreResponseCOPerLavoratoreResult;
import it.gov.lavoro.servizi.RicercaCO._2_0.CodiceFiscaleSoggettoFisico_Type;
import it.gov.lavoro.servizi.RicercaCO._2_0.IRicercaCOProxy;
import it.gov.lavoro.servizi.RicercaCO._2_0.UNILAV_Type;
import it.gov.lavoro.servizi.RicercaCO._2_0.UNISOMM_Type;
import it.gov.lavoro.servizi.RicercaCO._2_0.VARDATORI_Type;
import it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Esito;
import it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.GetRapportoLavoroAttivo_Input;
import it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.GetRapportoLavoroAttivo_Output;
import it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_PortTypeProxy;

public class ConsultaCOCoopApplicativa extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1552568516565634941L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ConsultaCOCoopApplicativa.class.getName());

	private String END_POINT_NAME_LAVORO_ATTIVO = "RapportiAttivi";
	private String END_POINT_NAME_STORICO_CO = "StoricoCO";

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		disableMessageIdFail();
		disableMessageIdSuccess();

		String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
		String codiceFiscaleLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cfLavoratore");
		String dataInizio = StringUtils.getAttributeStrNotNull(serviceRequest, "DATINIZIO");
		String dataFine = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINE");

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String endPoint = null;

			if (serviceRequest.containsAttribute("richiestaStoricoCO")) {
				endPoint = eps.getUrl(END_POINT_NAME_STORICO_CO);
				CodiceFiscaleSoggettoFisico_Type cf = new CodiceFiscaleSoggettoFisico_Type();
				cf.setCodiceFiscale_TypeValue(codiceFiscaleLavoratore);
				IRicercaCOProxy ricercaCoProxy = new IRicercaCOProxy(endPoint);
				COPerLavoratoreResponseCOPerLavoratoreResult coResult = ricercaCoProxy.COPerLavoratore(cf,
						DateUtils.getDate(dataInizio), DateUtils.getDate(dataFine));

				if (coResult.getEsito().getCodice().equalsIgnoreCase("E100")) {
					serviceResponse.setAttribute("WS_OUTPUT_STORICO_CO", coResult.getCOLavoratore());
					serviceResponse.setAttribute("DATA_INIZIO", dataInizio);
					serviceResponse.setAttribute("DATA_FINE", dataFine);
					UNILAV_Type[] unilav = coResult.getCOLavoratore().getUNILAV();
					if (unilav != null && unilav.length > 0) {
						serviceResponse.setAttribute("UNILAV", "UNILAV");
					}
					/*
					 * UNIMARE_Type [] unimare = coResult.getCOLavoratore().getUNIMARE(); if(unimare!=null &&
					 * unimare.length > 0){ serviceResponse.setAttribute("UNIMARE", unimare); }
					 */
					UNISOMM_Type[] unisomm = coResult.getCOLavoratore().getUNISOMM();
					if (unisomm != null && unisomm.length > 0) {
						serviceResponse.setAttribute("UNISOMM", "UNISOMM");
					}
					/*
					 * UNIURG_Type [] uniurg = coResult.getCOLavoratore().getUNIURG(); if(uniurg!=null && uniurg.length
					 * > 0){ serviceResponse.setAttribute("UNIURG", uniurg); }
					 */
					VARDATORI_Type[] vardatori = coResult.getCOLavoratore().getVARDATORI();
					if (vardatori != null && vardatori.length > 0) {
						serviceResponse.setAttribute("VARDATORI", "VARDATORI");
					}
					reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				} else if (coResult.getEsito().getCodice().equalsIgnoreCase("E103")) {
					reportOperation.reportFailure(Properties.NO_RESULT_FOUND);
					return;
				} else {
					reportOperation.reportFailure(Properties.WS_STORICO_CO_NA);
					return;
				}

			} else if (serviceRequest.containsAttribute("richiestaRapportiLavoriAttivi")) {
				endPoint = eps.getUrl(END_POINT_NAME_LAVORO_ATTIVO);
				GetRapportoLavoroAttivo_Input inputRLA = new GetRapportoLavoroAttivo_Input();
				inputRLA.setCodiceFiscaleLavoratore(codiceFiscaleLavoratore);
				inputRLA.setDataRiferimentoDal(DateUtils.datestringToXml(dataInizio).toGregorianCalendar());
				inputRLA.setDataRiferimentoAl(DateUtils.datestringToXml(dataFine).toGregorianCalendar());
				VerificaRapportoLavoroAttivo_PortTypeProxy serviziWSProxy = new VerificaRapportoLavoroAttivo_PortTypeProxy(
						endPoint);
				GetRapportoLavoroAttivo_Output rlaOutput = serviziWSProxy.getRapportiAttivi(inputRLA);

				if (rlaOutput.getEsito().equals(Esito.E103)) {
					reportOperation.reportFailure(Properties.NO_RESULT_FOUND);
					return;
				}
				if (rlaOutput.getEsito().equals(Esito.E100)) {
					serviceResponse.setAttribute("WS_OUTPUT_RLA", rlaOutput);
					serviceResponse.setAttribute("DATA_INIZIO", dataInizio);
					serviceResponse.setAttribute("DATA_FINE", dataFine);
					reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				}
			}
			_logger.info(cdnLavoratore);
		} catch (Throwable e) {
			if (serviceRequest.containsAttribute("richiestaStoricoCO")) {
				reportOperation.reportFailure(Properties.WS_STORICO_CO_NA);
			} else if (serviceRequest.containsAttribute("richiestaRapportiLavoriAttivi")) {
				reportOperation.reportFailure(Properties.WS_RLA_NA);
			}
			_logger.error("Errore: " + e);
		}

	}

}
