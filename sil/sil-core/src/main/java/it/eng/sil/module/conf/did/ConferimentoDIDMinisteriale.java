package it.eng.sil.module.conf.did;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.security.User;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Profiling;
import it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy;
import it.gov.mlps.types.GestisciDIDOutput;

public class ConferimentoDIDMinisteriale extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4148874981526506710L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ConferimentoDIDMinisteriale.class.getName());

	private String END_POINT_NAME = "ConferimentoDID";

	private String dataObbligatorieta = "04/12/2017";

	private ConferimentoUtility utility = null;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		Object res = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		boolean checkObbl = false;
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String codCpiRif = user.getCodRif();
		String newPrgConfDid = "";
		String cdnLavoratore = "";

		try {
			disableMessageIdSuccess();
			disableMessageIdFail();
			enableSimpleQuery(true);
			cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
			String dataDid = serviceRequest.getAttribute("DATDID").toString();
			utility = new ConferimentoUtility(null, new BigDecimal(cdnLavoratore), dataDid, codCpiRif);

			if (DateUtils.compare(dataDid, dataObbligatorieta) >= 0) {
				checkObbl = true;
			}

			String svuotaCampi = serviceRequest.getAttribute("EMPTY").toString();
			if (!checkObbl && StringUtils.isFilledNoBlank(svuotaCampi) && svuotaCampi.equals("OK")) {
				utility.svuotaCampi(serviceRequest);
			} else if (!checkObbl && StringUtils.isFilledNoBlank(svuotaCampi) && svuotaCampi.equals("NO")) {
				checkObbl = true;
			}

			if (checkObbl) {
				String strEta = (String) serviceRequest.getAttribute("NUMETA");
				Integer eta = null;
				if (StringUtils.isFilledNoBlank(strEta)) {
					eta = Integer.parseInt(strEta);
					if (eta != null && eta.intValue() < ConferimentoUtility.ETA_MINIMA_CONF) {
						reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_ETA);
						return;
					}
				}
			}
			this.setSectionQuerySelect("SELECT_NEW_PRGCONFERIMENTODID");
			SourceBean tmpPrg = doSelect(serviceRequest, serviceResponse);
			if (tmpPrg != null) {
				newPrgConfDid = tmpPrg.getAttribute("ROW.PRGCONFERIMENTODID").toString();
				if (StringUtils.isFilledNoBlank(newPrgConfDid)) {

					if (serviceRequest.containsAttribute("PRGCONFERIMENTODID")) {
						serviceRequest.delAttribute("PRGCONFERIMENTODID");
					}
					serviceRequest.setAttribute("PRGCONFERIMENTODID", newPrgConfDid.trim());
					this.setSectionQueryInsert("QUERY_INSERT");
					res = doInsert(serviceRequest, serviceResponse, false);
					serviceResponse.setAttribute("PRGCONFERIMENTODID", newPrgConfDid.trim());
				} else {
					throw new Exception();
				}
			} else {
				throw new Exception();
			}
		} catch (Exception e) {

			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "service()",
					"errore durante l'inserimento di un nuovo conferimento did.");
		}

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

		boolean invocazioneConferimento = false;
		if (confDidNew) {
			conferimentoDidAnpal(serviceRequest, serviceResponse, reportOperation, checkObbl, user, newPrgConfDid,
					cdnLavoratore, invocazioneConferimento);
		} else {
			conferimentoDid(serviceRequest, serviceResponse, reportOperation, checkObbl, user, newPrgConfDid,
					cdnLavoratore, invocazioneConferimento);
		}

	}

	private void conferimentoDid(SourceBean serviceRequest, SourceBean serviceResponse,
			ReportOperationResult reportOperation, boolean checkObbl, User user, String newPrgConfDid,
			String cdnLavoratore, boolean invocazioneConferimento) {
		Object res;
		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String endPointConferimento = eps.getUrl(END_POINT_NAME);

			ConferimentoDID_PortTypeProxy serviziWSProxy = new ConferimentoDID_PortTypeProxy(endPointConferimento);

			GestisciDID_Input conferimentoDid = utility.getGestisciDIDWS(serviceRequest, checkObbl);

			GestisciDID_Output didResponse = serviziWSProxy.gestisciDID(conferimentoDid);

			invocazioneConferimento = true;

			if (didResponse.getEsito().equals(ConferimentoUtility.ESITOCONFERIMENTOOK)) {
				reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.CONFERIMENTO_OK);

				this.setSectionQuerySelect("QUERY_GET_NUMKLO");
				SourceBean numKLoBean = doSelect(serviceRequest, serviceResponse);
				String numKLo = numKLoBean.getAttribute("ROW.NUMKLOCONFDID").toString();
				Profiling resProf = didResponse.getProfiling();
				serviceRequest.updAttribute("NUMKLOCONFDID", numKLo);
				if (resProf != null) {
					serviceRequest.updAttribute("IDSPROFILING", resProf.getIDSProfiling());
					serviceRequest.updAttribute("CODPFCONDOCCUP_CALC",
							resProf.getCondizioneOccupazionaleAnnoPrecedenteCalcolata());
					serviceRequest.updAttribute("NUMMESIDISOCC_CALC", resProf.getDurataDisoccupazioneCalcolata());
					serviceRequest.updAttribute("DECPROFILING", resProf.getProbabilita());
					if (resProf.getDataInserimento() != null) {
						SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
						String strDatProfiling = formatter.format(resProf.getDataInserimento().getTime());
						serviceRequest.updAttribute("DATPROFILING", strDatProfiling);
					}
				}
				serviceRequest.updAttribute("CODMONOSTATOINVIO", ConferimentoUtility.STATOINVIATO);
				serviceRequest.updAttribute("PRGCONFERIMENTODID", new BigDecimal(newPrgConfDid));
				this.setSectionQueryUpdate("QUERY_UPDATE_WS");
				res = doUpdate(serviceRequest, serviceResponse, false);
				int resultWs = -1;

				if (res != null && res instanceof Boolean && ((Boolean) res).booleanValue()) {

					MultipleTransactionQueryExecutor transExec = null;
					int result;
					try {
						transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
						transExec.initTransaction();
						utility.setTransExec(transExec);
						result = utility.gestisciDID(user, getRequestContainer(), serviceRequest, serviceResponse);
						if (result == 0) {
							resultWs = 0;
							transExec.commitTransaction();
							reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_DID_INSERITA);

							// gestione presa in carico
							try {
								transExec.initTransaction();

								result = utility.gestisciPresaInCarico150(user);

								if (result == 0) {
									transExec.commitTransaction();
									reportOperation
											.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_PRESA_CARICO150);
								} else {
									transExec.rollBackTransaction();
									reportOperation.reportFailure(result);
								}
							} catch (Exception e2) {
								transExec.rollBackTransaction();
								it.eng.sil.util.TraceWrapper.error(_logger,
										"Errore in conferma did ministeriale:verifica azione presa in carico 150.",
										(Exception) e2);
							}

						} else {
							transExec.rollBackTransaction();
							reportOperation.reportFailure(result);
						}

					} catch (Exception e) {
						if (transExec != null) {
							transExec.rollBackTransaction();
						}
						it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferma did ministeriale.",
								(Exception) e);
					} finally {
						if (transExec != null) {
							transExec.closeConnection();
						}
					}
					if (resultWs == 0) {
						// Gestione invio SAP
						ModuleIFace moduleGestioneInvioSAP = null;
						try {
							// invio automatico sap dipendete da configurazione, valore SAP_DID
							// Se 0 allora l'invio in automatico della SAP a seguito della Stipula della DID e' attivo,
							// se 1 allora l'invio automatico dopo la stipula della DID deve essere disabilitato.
							UtilsConfig utilityConf = new UtilsConfig("SAP_DID");
							String valoreConfig = utilityConf.getValoreConfigurazione();
							if (StringUtils.isFilledNoBlank(valoreConfig) && valoreConfig.equalsIgnoreCase("0")) {
								serviceRequest.setAttribute("INVIASAPFROMDID", "S");
								serviceRequest.setAttribute("INVIASAPFROMCONFERIMENTO", "S");
								DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(),
										getResponseContainer());
								moduleGestioneInvioSAP = ModuleFactory.getModule("M_GestioneInvioSap");
								((AbstractModule) moduleGestioneInvioSAP).setRequestContext(drc);
								moduleGestioneInvioSAP.service(getRequestContainer().getServiceRequest(),
										serviceResponse);
								it.eng.sil.util.amministrazione.impatti.DidBean.aggiornaNoteDid(user,
										utility.getDidConf().getPrgDichDisponibilita());
								reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_INVIO_SAP);
							} else if (StringUtils.isFilledNoBlank(valoreConfig)
									&& valoreConfig.equalsIgnoreCase("1")) {
								it.eng.sil.util.TraceWrapper.warn(_logger,
										"ConvalidaDIDMinisteriale: inviaSAP non effettuato da configurazione", null);
							}
						} catch (Exception me) {
							if (!serviceResponse.containsAttribute("ERRORENOLOG")) {
								it.eng.sil.util.TraceWrapper.error(_logger,
										"ConvalidaDIDMinisteriale:service():errore inviaSAP", me);
							}
						}
					}
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
				}

			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO);
				String codEsitoRisposta = didResponse.getEsito();
				String descrizioneEsitoRisposta = ConferimentoUtility.getDescrizioneErroreMin(codEsitoRisposta);
				Vector paramV = new Vector<String>(2);
				paramV.add(codEsitoRisposta);
				paramV.add(descrizioneEsitoRisposta);
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ERR_MINISTERO, "CONFERIMENTO",
						"ERRORE WS MINISTERO", paramV);
			}

			GestisciDIDOutput outputWS = null;
			boolean esitoOutputTracciatura = utility.getGestisciOutputWS(didResponse);
			if (esitoOutputTracciatura) {
				outputWS = utility.getOutputConferimento();
			} else {
				if (utility.getOutputConferimento() != null) {
					outputWS = utility.getOutputConferimento();
				}
			}

			BigDecimal prgTracciamento = ConferimentoUtility.getProgressivoTracciamentoConferimento();
			boolean esitoTracciatura = ConferimentoUtility.inserisciTracciamentoConferimento(prgTracciamento,
					new BigDecimal(cdnLavoratore), new BigDecimal(newPrgConfDid.trim()), utility.getInputConferimento(),
					didResponse.getEsito(), outputWS);
			if (!esitoTracciatura) {
				it.eng.sil.util.TraceWrapper.error(_logger, "errore tracciatura conferimento xml",
						new Exception("errore tracciatura conferimento xml"));
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
			if (!invocazioneConferimento) {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_INVOCAZIONE_WS);
			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
			}
		}
	}

	private void conferimentoDidAnpal(SourceBean serviceRequest, SourceBean serviceResponse,
			ReportOperationResult reportOperation, boolean checkObbl, User user, String newPrgConfDid,
			String cdnLavoratore, boolean invocazioneConferimento) {

		Object res;
		try {
			serviceResponse.setAttribute("ESITO_ANPAL", "ESITO_ANPAL");

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

			String endPointConferimento = eps.getUrl(END_POINT_NAME);

			it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy serviziWSProxy = new it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy(
					endPointConferimento);

			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input conferimentoDid = utility
					.getGestisciDIDWS_Anpal(serviceRequest, checkObbl);

			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output didResponse = serviziWSProxy
					.gestisciDID(conferimentoDid);

			invocazioneConferimento = true;

			if (didResponse.getEsito().equals(ConferimentoUtility.ESITOCONFERIMENTOOK)) {
				reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.CONFERIMENTO_OK_ANPAL);

				this.setSectionQuerySelect("QUERY_GET_NUMKLO");
				SourceBean numKLoBean = doSelect(serviceRequest, serviceResponse);
				String numKLo = numKLoBean.getAttribute("ROW.NUMKLOCONFDID").toString();
				it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.Profiling resProf = didResponse
						.getProfiling();
				serviceRequest.updAttribute("NUMKLOCONFDID", numKLo);
				if (resProf != null) {
					serviceRequest.updAttribute("IDSPROFILING", resProf.getIDSProfiling());
					serviceRequest.updAttribute("CODPFCONDOCCUP_CALC",
							resProf.getCondizioneOccupazionaleAnnoPrecedenteCalcolata());
					serviceRequest.updAttribute("NUMMESIDISOCC_CALC", resProf.getDurataDisoccupazioneCalcolata());
					serviceRequest.updAttribute("DECPROFILING", resProf.getProbabilita());
					if (resProf.getDataInserimento() != null) {
						SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
						String strDatProfiling = formatter.format(resProf.getDataInserimento().getTime());
						serviceRequest.updAttribute("DATPROFILING", strDatProfiling);
					}
				}
				serviceRequest.updAttribute("CODMONOSTATOINVIO", ConferimentoUtility.STATOINVIATO);
				serviceRequest.updAttribute("PRGCONFERIMENTODID", new BigDecimal(newPrgConfDid));
				this.setSectionQueryUpdate("QUERY_UPDATE_WS");
				res = doUpdate(serviceRequest, serviceResponse, false);
				int resultWs = -1;

				if (res != null && res instanceof Boolean && ((Boolean) res).booleanValue()) {

					MultipleTransactionQueryExecutor transExec = null;
					int result;
					try {
						transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
						transExec.initTransaction();
						utility.setTransExec(transExec);
						result = utility.gestisciDID(user, getRequestContainer(), serviceRequest, serviceResponse);
						if (result == 0) {
							resultWs = 0;
							transExec.commitTransaction();
							reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_DID_INSERITA);

							// gestione presa in carico
							try {
								transExec.initTransaction();

								result = utility.gestisciPresaInCarico150(user);

								if (result == 0) {
									transExec.commitTransaction();
									reportOperation
											.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_PRESA_CARICO150);
								} else {
									transExec.rollBackTransaction();
									reportOperation.reportFailure(result);
								}
							} catch (Exception e2) {
								transExec.rollBackTransaction();
								it.eng.sil.util.TraceWrapper.error(_logger,
										"Errore in conferma did ministeriale:verifica azione presa in carico 150.",
										(Exception) e2);
							}

						} else {
							transExec.rollBackTransaction();
							reportOperation.reportFailure(result);
						}

					} catch (Exception e) {
						if (transExec != null) {
							transExec.rollBackTransaction();
						}
						it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferma did ministeriale.",
								(Exception) e);
					} finally {
						if (transExec != null) {
							transExec.closeConnection();
						}
					}
					if (resultWs == 0) {
						// Gestione invio SAP
						ModuleIFace moduleGestioneInvioSAP = null;
						try {
							// invio automatico sap dipendete da configurazione, valore SAP_DID
							// Se 0 allora l'invio in automatico della SAP a seguito della Stipula della DID e' attivo,
							// se 1 allora l'invio automatico dopo la stipula della DID deve essere disabilitato.
							UtilsConfig utilityConf = new UtilsConfig("SAP_DID");
							String valoreConfig = utilityConf.getValoreConfigurazione();
							if (StringUtils.isFilledNoBlank(valoreConfig) && valoreConfig.equalsIgnoreCase("0")) {
								serviceRequest.setAttribute("INVIASAPFROMDID", "S");
								serviceRequest.setAttribute("INVIASAPFROMCONFERIMENTO", "S");
								DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(),
										getResponseContainer());
								moduleGestioneInvioSAP = ModuleFactory.getModule("M_GestioneInvioSap");
								((AbstractModule) moduleGestioneInvioSAP).setRequestContext(drc);
								moduleGestioneInvioSAP.service(getRequestContainer().getServiceRequest(),
										serviceResponse);
								it.eng.sil.util.amministrazione.impatti.DidBean.aggiornaNoteDid(user,
										utility.getDidConf().getPrgDichDisponibilita());
								reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_INVIO_SAP);
							} else if (StringUtils.isFilledNoBlank(valoreConfig)
									&& valoreConfig.equalsIgnoreCase("1")) {
								it.eng.sil.util.TraceWrapper.warn(_logger,
										"ConvalidaDIDMinisteriale: inviaSAP non effettuato da configurazione", null);
							}
						} catch (Exception me) {
							if (!serviceResponse.containsAttribute("ERRORENOLOG")) {
								it.eng.sil.util.TraceWrapper.error(_logger,
										"ConvalidaDIDMinisteriale:service():errore inviaSAP", me);
							}
						}
					}
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
				}

			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ANPAL);
				String codEsitoRisposta = didResponse.getEsito();
				String descrizioneEsitoRisposta = ConferimentoUtility.getDescrizioneErroreMin(codEsitoRisposta);
				Vector paramV = new Vector<String>(2);
				paramV.add(codEsitoRisposta);
				paramV.add(descrizioneEsitoRisposta);
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ERR_MINISTERO, "CONFERIMENTO",
						"ERRORE WS MINISTERO", paramV);
			}

			GestisciDIDOutput outputWS = null;
			boolean esitoOutputTracciatura = utility.getGestisciOutputWS(didResponse);
			if (esitoOutputTracciatura) {
				outputWS = utility.getOutputConferimento();
			} else {
				if (utility.getOutputConferimento() != null) {
					outputWS = utility.getOutputConferimento();
				}
			}

			BigDecimal prgTracciamento = ConferimentoUtility.getProgressivoTracciamentoConferimento();
			boolean esitoTracciatura = ConferimentoUtility.inserisciTracciamentoConferimento(prgTracciamento,
					new BigDecimal(cdnLavoratore), new BigDecimal(newPrgConfDid.trim()), utility.getInputConferimento(),
					didResponse.getEsito(), outputWS);
			if (!esitoTracciatura) {
				it.eng.sil.util.TraceWrapper.error(_logger, "errore tracciatura conferimento xml",
						new Exception("errore tracciatura conferimento xml"));
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
			if (!invocazioneConferimento) {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_INVOCAZIONE_WS);
			} else {
				reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
			}
		}
	}
}