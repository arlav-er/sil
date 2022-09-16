package it.eng.sil.module.conf.did;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output;
import it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy;
import it.gov.mlps.types.GestisciDIDOutput;

public class RevocaDIDMinisteriale extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6500495854828802216L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RevocaDIDMinisteriale.class.getName());

	private String END_POINT_NAME = "ConferimentoDID";

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
		String dataDid = "";
		if (serviceRequest.containsAttribute("dataDicSap")) {
			dataDid = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDicSap");
		} else {
			dataDid = StringUtils.getAttributeStrNotNull(serviceRequest, "DATDID");
		}
		String dataDichNonPresente = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDichNonPresente");
		if (StringUtils.isFilledNoBlank(dataDichNonPresente) && dataDichNonPresente.equalsIgnoreCase("SI")) {
			if (serviceRequest.containsAttribute("dataDichSapN00")) {
				dataDid = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDichSapN00");
			}
		}
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String codCpiRif = user.getCodRif();
		ConferimentoUtility conferimento = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();

		try {
			conferimento = new ConferimentoUtility(null, new BigDecimal(cdnLavoratore), dataDid, codCpiRif);

			GestisciDID_Input revocaDid = conferimento.getGestisciDIDWS(serviceRequest, false);

			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input revocaDidAnpal = conferimento
					.getGestisciDIDWS_Anpal(serviceRequest, false);

			BigDecimal prgConferimento = ConferimentoUtility.getProgressivoConferimento();

			Object params[] = new Object[21];
			params[0] = prgConferimento;
			params[1] = cdnLavoratore;
			params[2] = dataDid;
			params[3] = ConferimentoUtility.EVENTOREVOCA;
			params[4] = conferimento.getCodCpiMin();
			params[5] = ConferimentoUtility.STATODAINVIARE;
			params[6] = null;
			params[7] = null;
			params[8] = null;
			params[9] = null;
			params[10] = null;
			params[11] = null;
			params[12] = null;
			params[13] = null;
			params[14] = null;
			params[15] = null;
			params[16] = null;
			params[17] = null;
			params[18] = null;
			params[19] = null;
			params[20] = null;

			Boolean res = (Boolean) QueryExecutor.executeQuery("CDD_INSERT_AM_CONF_DID", params, "INSERT",
					Values.DB_SIL_DATI);

			if (!res.booleanValue()) {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
			} else {
				serviceResponse.setAttribute("PRGCONFERIMENTODID", String.valueOf(prgConferimento));

				this.setSectionQuerySelect("QUERY_CHECK_CONF_DID");
				SourceBean sbDataConfDid = doSelect(serviceRequest, serviceResponse);
				boolean confDidNew = false;
				if (sbDataConfDid != null) {
					BigDecimal differenzaGiorni = (BigDecimal) sbDataConfDid.getAttribute("ROW.DATEDIFF");
					if (differenzaGiorni != null && differenzaGiorni.intValue() >= 0) {
						confDidNew = true;
					} else {
						confDidNew = false;
					}
				} else {
					confDidNew = false;
				}

				if (confDidNew) {
					serviceResponse.setAttribute("ESITO_ANPAL", "ESITO_ANPAL");

					revocaDidAnpal(cdnLavoratore, conferimento, reportOperation, revocaDidAnpal, prgConferimento);
				} else {
					revocaDid(cdnLavoratore, conferimento, reportOperation, revocaDid, prgConferimento);
				}

			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in revoca did ministeriale.", (Exception) e);
		}
	}

	private void revocaDid(String cdnLavoratore, ConferimentoUtility conferimento,
			ReportOperationResult reportOperation, GestisciDID_Input revocaDid, BigDecimal prgConferimento) {
		Boolean res;
		boolean invocazioneConferimento = false;
		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String endPointConferimento = eps.getUrl(END_POINT_NAME);

			ConferimentoDID_PortTypeProxy serviziWSProxy = new ConferimentoDID_PortTypeProxy(endPointConferimento);

			GestisciDID_Output outputRevoca = serviziWSProxy.gestisciDID(revocaDid);

			invocazioneConferimento = true;

			if (outputRevoca.getEsito().equalsIgnoreCase(ConferimentoUtility.ESITOCONFERIMENTOOK)) {
				reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.REVOCA_OK);

				Object paramsOp[] = new Object[] { prgConferimento };
				SourceBean rowOperation = (SourceBean) QueryExecutor
						.executeQuery("CCD_SELECT_Conferimento_Did_FROM_PRG", paramsOp, "SELECT", Values.DB_SIL_DATI);

				if (rowOperation != null) {
					rowOperation = rowOperation.containsAttribute("ROW") ? (SourceBean) rowOperation.getAttribute("ROW")
							: rowOperation;
					BigDecimal numkloConf = (BigDecimal) rowOperation.getAttribute("NUMKLOCONFDID");
					numkloConf = numkloConf.add(new BigDecimal(1));
					paramsOp = new Object[] { numkloConf, ConferimentoUtility.STATOINVIATO, prgConferimento };
					res = (Boolean) QueryExecutor.executeQuery("UPDATE_AM_CONF_DID_ESITO", paramsOp, "UPDATE",
							Values.DB_SIL_DATI);
					if (!res.booleanValue()) {
						reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
				}
			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO);
				String codEsitoRisposta = outputRevoca.getEsito();
				String descrizioneEsitoRisposta = ConferimentoUtility.getDescrizioneErroreMin(codEsitoRisposta);
				Vector paramV = new Vector<String>(2);
				paramV.add(codEsitoRisposta);
				paramV.add(descrizioneEsitoRisposta);
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ERR_MINISTERO, "REVOCA",
						"ERRORE WS MINISTERO", paramV);
			}

			GestisciDIDOutput outputWS = null;
			boolean esitoOutputTracciatura = conferimento.getGestisciOutputWS(outputRevoca);
			if (esitoOutputTracciatura) {
				outputWS = conferimento.getOutputConferimento();
			} else {
				if (conferimento.getOutputConferimento() != null) {
					outputWS = conferimento.getOutputConferimento();
				}
			}

			BigDecimal prgTracciamento = ConferimentoUtility.getProgressivoTracciamentoConferimento();
			boolean esitoTracciatura = ConferimentoUtility.inserisciTracciamentoConferimento(prgTracciamento,
					new BigDecimal(cdnLavoratore), prgConferimento, conferimento.getInputConferimento(),
					outputRevoca.getEsito(), outputWS);
			if (!esitoTracciatura) {
				it.eng.sil.util.TraceWrapper.error(_logger, "errore tracciatura revoca xml",
						new Exception("errore tracciatura revoca xml"));
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in revoca did ministeriale.", (Exception) e);
			if (!invocazioneConferimento) {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_INVOCAZIONE_WS);
			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
			}
		}
	}

	private void revocaDidAnpal(String cdnLavoratore, ConferimentoUtility conferimento,
			ReportOperationResult reportOperation,
			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input revocaDid,
			BigDecimal prgConferimento) {
		Boolean res;
		boolean invocazioneConferimento = false;
		try {

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String endPointConferimento = eps.getUrl(END_POINT_NAME);

			it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy serviziWSProxy = new it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy(
					endPointConferimento);

			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output outputRevoca = serviziWSProxy
					.gestisciDID(revocaDid);

			invocazioneConferimento = true;

			if (outputRevoca.getEsito().equalsIgnoreCase(ConferimentoUtility.ESITOCONFERIMENTOOK)) {
				reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.REVOCA_OK_ANPAL);

				Object paramsOp[] = new Object[] { prgConferimento };
				SourceBean rowOperation = (SourceBean) QueryExecutor
						.executeQuery("CCD_SELECT_Conferimento_Did_FROM_PRG", paramsOp, "SELECT", Values.DB_SIL_DATI);

				if (rowOperation != null) {
					rowOperation = rowOperation.containsAttribute("ROW") ? (SourceBean) rowOperation.getAttribute("ROW")
							: rowOperation;
					BigDecimal numkloConf = (BigDecimal) rowOperation.getAttribute("NUMKLOCONFDID");
					numkloConf = numkloConf.add(new BigDecimal(1));
					paramsOp = new Object[] { numkloConf, ConferimentoUtility.STATOINVIATO, prgConferimento };
					res = (Boolean) QueryExecutor.executeQuery("UPDATE_AM_CONF_DID_ESITO", paramsOp, "UPDATE",
							Values.DB_SIL_DATI);
					if (!res.booleanValue()) {
						reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
				}
			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ANPAL);
				String codEsitoRisposta = outputRevoca.getEsito();
				String descrizioneEsitoRisposta = ConferimentoUtility.getDescrizioneErroreMin(codEsitoRisposta);
				Vector paramV = new Vector<String>(2);
				paramV.add(codEsitoRisposta);
				paramV.add(descrizioneEsitoRisposta);
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ERR_MINISTERO, "REVOCA",
						"ERRORE WS MINISTERO", paramV);
			}

			GestisciDIDOutput outputWS = null;
			boolean esitoOutputTracciatura = conferimento.getGestisciOutputWS(outputRevoca);
			if (esitoOutputTracciatura) {
				outputWS = conferimento.getOutputConferimento();
			} else {
				if (conferimento.getOutputConferimento() != null) {
					outputWS = conferimento.getOutputConferimento();
				}
			}

			BigDecimal prgTracciamento = ConferimentoUtility.getProgressivoTracciamentoConferimento();
			boolean esitoTracciatura = ConferimentoUtility.inserisciTracciamentoConferimento(prgTracciamento,
					new BigDecimal(cdnLavoratore), prgConferimento, conferimento.getInputConferimento(),
					outputRevoca.getEsito(), outputWS);
			if (!esitoTracciatura) {
				it.eng.sil.util.TraceWrapper.error(_logger, "errore tracciatura revoca xml",
						new Exception("errore tracciatura revoca xml"));
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in revoca did ministeriale.", (Exception) e);
			if (!invocazioneConferimento) {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_INVOCAZIONE_WS);
			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
			}
		}
	}

}
