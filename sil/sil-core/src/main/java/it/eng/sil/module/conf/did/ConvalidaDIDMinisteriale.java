package it.eng.sil.module.conf.did;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.util.QueryExecutor;

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

public class ConvalidaDIDMinisteriale extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6056069358335495589L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ConvalidaDIDMinisteriale.class.getName());

	private String END_POINT_NAME = "ConferimentoDID";

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		MultipleTransactionQueryExecutor transExec = null;
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
		String dataDid = StringUtils.getAttributeStrNotNull(serviceRequest, "DATDID");
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String codCpiRif = user.getCodRif();
		ConferimentoUtility conferimento = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();
		int result;
		try {
			transExec = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);

			conferimento = new ConferimentoUtility(transExec, new BigDecimal(cdnLavoratore), dataDid, codCpiRif);
			try {
				transExec.initTransaction();
				result = conferimento.gestisciDID(user, getRequestContainer(), serviceRequest, serviceResponse);
				if (result == 0) {
					transExec.commitTransaction();
					reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_DID_INSERITA);
				} else {
					transExec.rollBackTransaction();
					reportOperation.reportFailure(result);
				}

				// gestione presa in carico
				try {
					transExec.initTransaction();

					result = conferimento.gestisciPresaInCarico150(user);
					if (result == 0) {
						transExec.commitTransaction();
						reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_PRESA_CARICO150);
					} else {
						transExec.rollBackTransaction();
						reportOperation.reportFailure(result);
					}
				} catch (Exception e2) {
					transExec.rollBackTransaction();
					it.eng.sil.util.TraceWrapper.error(_logger,
							"Errore in conferma did ministerial:verifica azione presa in carico 150.", (Exception) e2);
				}

			} catch (Exception e1) {
				transExec.rollBackTransaction();
				it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferma did ministerial:inserimento did.",
						(Exception) e1);
			}
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferma did ministeriale.", (Exception) e);
		} finally {
			if (transExec != null) {
				transExec.closeConnection();
			}
		}

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
				DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(), getResponseContainer());
				moduleGestioneInvioSAP = ModuleFactory.getModule("M_GestioneInvioSap");
				((AbstractModule) moduleGestioneInvioSAP).setRequestContext(drc);
				moduleGestioneInvioSAP.service(getRequestContainer().getServiceRequest(), serviceResponse);
				reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.SUCCESS_INVIO_SAP);
			} else if (StringUtils.isFilledNoBlank(valoreConfig) && valoreConfig.equalsIgnoreCase("1")) {
				it.eng.sil.util.TraceWrapper.warn(_logger,
						"ConvalidaDIDMinisteriale: inviaSAP non effettuato da configurazione", null);
			}
		} catch (Exception me) {
			if (!serviceResponse.containsAttribute("ERRORENOLOG")) {
				it.eng.sil.util.TraceWrapper.error(_logger, "ConvalidaDIDMinisteriale:service():errore inviaSAP", me);
			}
			reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_INVIO_SAP);
		}

		try {

			BigDecimal prgConferimento = ConferimentoUtility.getProgressivoConferimento();

			boolean insertProfiling = false;
			String svuotaCampi = serviceRequest.getAttribute("EMPTY").toString();
			if (StringUtils.isFilledNoBlank(svuotaCampi) && svuotaCampi.equals("OK")) {
				conferimento.svuotaCampi(serviceRequest);
			} else if (StringUtils.isFilledNoBlank(svuotaCampi) && svuotaCampi.equals("NO")) {
				insertProfiling = true;
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

			Object params[] = new Object[21];
			params[0] = prgConferimento;
			params[1] = cdnLavoratore;
			params[2] = dataDid;
			params[3] = ConferimentoUtility.EVENTOCONVALIDA;
			params[4] = conferimento.getCodCpiMin();
			params[5] = ConferimentoUtility.STATODAINVIARE;
			if (!insertProfiling) {
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
			} else {
				params[6] = serviceRequest.getAttribute("NUMETA");
				params[7] = serviceRequest.getAttribute("STRSESSO");
				params[8] = serviceRequest.getAttribute("CODPFCITTADINANZA");
				params[9] = serviceRequest.getAttribute("codTitolo");
				params[10] = serviceRequest.getAttribute("CODPFCONDOCCUP");
				params[11] = serviceRequest.getAttribute("NUMMESIDISOCC");
				params[12] = serviceRequest.getAttribute("NUMMESIRICERCALAV");
				params[13] = serviceRequest.getAttribute("CODPFISCRCORSO");
				params[14] = serviceRequest.getAttribute("CODPROVINCIARES");
				params[15] = serviceRequest.getAttribute("CODPFPRESENZAIT");
				params[16] = serviceRequest.getAttribute("FLGESPLAVORO");
				params[17] = serviceRequest.getAttribute("CODPFPOSIZIONEPROF");
				params[18] = serviceRequest.getAttribute("NUMNUCLEOFAM");
				params[19] = serviceRequest.getAttribute("FLGFIGLIACARICO");
				params[20] = serviceRequest.getAttribute("FLGFIGLIMINORENNI");
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
			if (confDidNew) {
				convalidaDidAnpal(serviceRequest, serviceResponse, cdnLavoratore, conferimento, reportOperation,
						prgConferimento, insertProfiling, params);
			} else {
				convalidaDid(serviceRequest, serviceResponse, cdnLavoratore, conferimento, reportOperation,
						prgConferimento, insertProfiling, params);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferma did ministeriale.", (Exception) e);
		}
	}

	private void convalidaDid(SourceBean serviceRequest, SourceBean serviceResponse, String cdnLavoratore,
			ConferimentoUtility conferimento, ReportOperationResult reportOperation, BigDecimal prgConferimento,
			boolean insertProfiling, Object[] params) throws SourceBeanException {
		GestisciDID_Input convalidaDid = conferimento.getGestisciDIDWS(serviceRequest, insertProfiling);

		Boolean res = (Boolean) QueryExecutor.executeQuery("CDD_INSERT_AM_CONF_DID", params, "INSERT",
				Values.DB_SIL_DATI);

		if (!res.booleanValue()) {
			reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
		} else {
			serviceResponse.setAttribute("PRGCONFERIMENTODID", String.valueOf(prgConferimento));

			boolean invocazioneConferimento = false;
			try {
				DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
				String dataSourceJndiName = dataSourceJndi.getJndi();
				EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
				String endPointConferimento = eps.getUrl(END_POINT_NAME);

				ConferimentoDID_PortTypeProxy serviziWSProxy = new ConferimentoDID_PortTypeProxy(endPointConferimento);

				GestisciDID_Output outputConvalida = serviziWSProxy.gestisciDID(convalidaDid);

				invocazioneConferimento = true;

				if (outputConvalida.getEsito().equalsIgnoreCase(ConferimentoUtility.ESITOCONFERIMENTOOK)) {
					reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.CONVALIDA_OK);

					Object paramsOp[] = new Object[] { prgConferimento };
					SourceBean rowOperation = (SourceBean) QueryExecutor.executeQuery(
							"CCD_SELECT_Conferimento_Did_FROM_PRG", paramsOp, "SELECT", Values.DB_SIL_DATI);

					if (rowOperation != null) {
						rowOperation = rowOperation.containsAttribute("ROW")
								? (SourceBean) rowOperation.getAttribute("ROW")
								: rowOperation;
						String numkloConf = rowOperation.getAttribute("NUMKLOCONFDID").toString();

						Profiling resProf = outputConvalida.getProfiling();
						serviceRequest.updAttribute("NUMKLOCONFDID", numkloConf);
						serviceRequest.updAttribute("CODMONOSTATOINVIO", ConferimentoUtility.STATOINVIATO);
						if (resProf != null) {
							// not null PRGCONFERIMENTODID CDNLAVORATORE DATDID CODPFTIPOEVENTO CODENTETIT NUMKLOCONFDID
							if (resProf.getProvinciaDiResidenza() != null) {
								serviceRequest.setAttribute("CODMINPROV", resProf.getProvinciaDiResidenza());
							} else {
								serviceRequest.setAttribute("CODMINPROV", null);
							}
							this.setSectionQuerySelect("QUERY_GET_CODPROVINCIA");
							SourceBean sbResult = doSelect(serviceRequest, serviceResponse);
							Vector vecProvSil = sbResult.getAttributeAsVector("ROW");
							SourceBean provSil = null;
							String codProvinciaSil = null;
							if (vecProvSil != null && !vecProvSil.isEmpty() && vecProvSil.size() == 1) {
								provSil = (SourceBean) vecProvSil.elementAt(0);
								codProvinciaSil = StringUtils.getAttributeStrNotNull(provSil, "codprovincia");
							}
							serviceRequest.delAttribute("CODMINPROV");

							serviceRequest.updAttribute("IDSPROFILING", resProf.getIDSProfiling());
							if (resProf.getCondizioneOccupazionaleAnnoPrecedenteCalcolata() != null) {
								serviceRequest.updAttribute("CODPFCONDOCCUP_CALC",
										resProf.getCondizioneOccupazionaleAnnoPrecedenteCalcolata());
							}
							if (resProf.getDurataDisoccupazioneCalcolata() != null) {
								serviceRequest.updAttribute("NUMMESIDISOCC_CALC",
										resProf.getDurataDisoccupazioneCalcolata());
							}
							if (resProf.getProbabilita() != null) {
								serviceRequest.updAttribute("DECPROFILING", resProf.getProbabilita());
							}
							if (resProf.getDataInserimento() != null) {
								SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
								String strDatProfiling = formatter.format(resProf.getDataInserimento().getTime());
								serviceRequest.updAttribute("DATPROFILING", strDatProfiling);
							}
							serviceRequest.updAttribute("NUMETA", resProf.getEta());
							if (resProf.getGenere() != null && resProf.getGenere().getValue() != null) {
								serviceRequest.updAttribute("STRSESSO", resProf.getGenere().getValue());
							}
							if (resProf.getCittadinanza() != null) {
								serviceRequest.updAttribute("CODPFCITTADINANZA", resProf.getCittadinanza());
							}
							if (resProf.getTitoloDiStudio() != null) {
								serviceRequest.updAttribute("codTitolo", resProf.getTitoloDiStudio());
							}
							if (resProf.getCondizioneProfessionaleAnnoPrecedente() != null) {
								serviceRequest.updAttribute("CODPFCONDOCCUP",
										resProf.getCondizioneProfessionaleAnnoPrecedente());
							}
							if (resProf.getDurataDellaDisoccupazione() != null) {
								serviceRequest.updAttribute("NUMMESIDISOCC", resProf.getDurataDellaDisoccupazione());
							}
							serviceRequest.updAttribute("NUMMESIRICERCALAV", resProf.getDurataRicercaLavoro());
							if (resProf.getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione() != null) {
								serviceRequest.updAttribute("CODPFISCRCORSO",
										resProf.getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione());
							}

							serviceRequest.updAttribute("CODPROVINCIARES", codProvinciaSil);
							if (resProf.getDurataPresenzaInItalia() != null) {
								serviceRequest.updAttribute("CODPFPRESENZAIT", resProf.getDurataPresenzaInItalia());
							}
							serviceRequest.updAttribute("FLGESPLAVORO",
									resProf.isHaLavoratoAlmenoUnaVolta() ? "S" : "N");
							if (resProf.getPosizioneUltimaOccupazione() != null) {
								serviceRequest.updAttribute("CODPFPOSIZIONEPROF",
										resProf.getPosizioneUltimaOccupazione());
							}
							serviceRequest.updAttribute("NUMNUCLEOFAM", resProf.getNumeroComponentiFamiglia());
							serviceRequest.updAttribute("FLGFIGLIACARICO",
									resProf.isPresenzaFigliACarico() ? "S" : "N");
							if (resProf.getPresenzaFigliMinoriACarico() != null) {
								serviceRequest.updAttribute("FLGFIGLIMINORENNI",
										resProf.getPresenzaFigliMinoriACarico() ? "S" : "N");
							}
						}
						serviceRequest.updAttribute("PRGCONFERIMENTODID", prgConferimento);
						this.setSectionQueryUpdate("QUERY_UPDATE_PROFILING");
						Object resUpd = doUpdate(serviceRequest, serviceResponse, false);

						if (resUpd == null || ((resUpd instanceof Boolean) && !((Boolean) resUpd).booleanValue())) {
							reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
						}

					} else {
						reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO);
					String codEsitoRisposta = outputConvalida.getEsito();
					String descrizioneEsitoRisposta = ConferimentoUtility.getDescrizioneErroreMin(codEsitoRisposta);
					Vector paramV = new Vector<String>(2);
					paramV.add(codEsitoRisposta);
					paramV.add(descrizioneEsitoRisposta);
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ERR_MINISTERO, "CONFERMA",
							"ERRORE WS MINISTERO", paramV);
				}

				GestisciDIDOutput outputWS = null;
				boolean esitoOutputTracciatura = conferimento.getGestisciOutputWS(outputConvalida);
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
						outputConvalida.getEsito(), outputWS);
				if (!esitoTracciatura) {
					it.eng.sil.util.TraceWrapper.error(_logger, "errore tracciatura conferma xml",
							new Exception("errore tracciatura conferma xml"));
				}

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferma did ministeriale.", (Exception) e);
				if (!invocazioneConferimento) {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_INVOCAZIONE_WS);
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
				}
			}
		}
	}

	private void convalidaDidAnpal(SourceBean serviceRequest, SourceBean serviceResponse, String cdnLavoratore,
			ConferimentoUtility conferimento, ReportOperationResult reportOperation, BigDecimal prgConferimento,
			boolean insertProfiling, Object[] params) throws SourceBeanException {

		it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input convalidaDid = conferimento
				.getGestisciDIDWS_Anpal(serviceRequest, insertProfiling);

		Boolean res = (Boolean) QueryExecutor.executeQuery("CDD_INSERT_AM_CONF_DID", params, "INSERT",
				Values.DB_SIL_DATI);

		if (!res.booleanValue()) {
			reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
		} else {
			serviceResponse.setAttribute("ESITO_ANPAL", "ESITO_ANPAL");

			serviceResponse.setAttribute("PRGCONFERIMENTODID", String.valueOf(prgConferimento));

			boolean invocazioneConferimento = false;
			try {
				DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
				String dataSourceJndiName = dataSourceJndi.getJndi();
				EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
				String endPointConferimento = eps.getUrl(END_POINT_NAME);

				it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy serviziWSProxy = new it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy(
						endPointConferimento);

				it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output outputConvalida = serviziWSProxy
						.gestisciDID(convalidaDid);

				invocazioneConferimento = true;

				if (outputConvalida.getEsito().equalsIgnoreCase(ConferimentoUtility.ESITOCONFERIMENTOOK)) {
					reportOperation.reportSuccess(MessageCodes.CONFERIMENTO_DID.CONVALIDA_OK_ANPAL);

					Object paramsOp[] = new Object[] { prgConferimento };
					SourceBean rowOperation = (SourceBean) QueryExecutor.executeQuery(
							"CCD_SELECT_Conferimento_Did_FROM_PRG", paramsOp, "SELECT", Values.DB_SIL_DATI);

					if (rowOperation != null) {
						rowOperation = rowOperation.containsAttribute("ROW")
								? (SourceBean) rowOperation.getAttribute("ROW")
								: rowOperation;
						String numkloConf = rowOperation.getAttribute("NUMKLOCONFDID").toString();

						it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.Profiling resProf = outputConvalida
								.getProfiling();
						serviceRequest.updAttribute("NUMKLOCONFDID", numkloConf);
						serviceRequest.updAttribute("CODMONOSTATOINVIO", ConferimentoUtility.STATOINVIATO);
						if (resProf != null) {
							// not null PRGCONFERIMENTODID CDNLAVORATORE DATDID CODPFTIPOEVENTO CODENTETIT NUMKLOCONFDID
							if (resProf.getProvinciaDiResidenza() != null) {
								serviceRequest.setAttribute("CODMINPROV", resProf.getProvinciaDiResidenza());
							} else {
								serviceRequest.setAttribute("CODMINPROV", null);
							}
							this.setSectionQuerySelect("QUERY_GET_CODPROVINCIA");
							SourceBean sbResult = doSelect(serviceRequest, serviceResponse);
							Vector vecProvSil = sbResult.getAttributeAsVector("ROW");
							SourceBean provSil = null;
							String codProvinciaSil = null;
							if (vecProvSil != null && !vecProvSil.isEmpty() && vecProvSil.size() == 1) {
								provSil = (SourceBean) vecProvSil.elementAt(0);
								codProvinciaSil = StringUtils.getAttributeStrNotNull(provSil, "codprovincia");
							}
							serviceRequest.delAttribute("CODMINPROV");

							serviceRequest.updAttribute("IDSPROFILING", resProf.getIDSProfiling());
							if (resProf.getCondizioneOccupazionaleAnnoPrecedenteCalcolata() != null) {
								serviceRequest.updAttribute("CODPFCONDOCCUP_CALC",
										resProf.getCondizioneOccupazionaleAnnoPrecedenteCalcolata());
							}
							if (resProf.getDurataDisoccupazioneCalcolata() != null) {
								serviceRequest.updAttribute("NUMMESIDISOCC_CALC",
										resProf.getDurataDisoccupazioneCalcolata());
							}
							if (resProf.getProbabilita() != null) {
								serviceRequest.updAttribute("DECPROFILING", resProf.getProbabilita());
							}
							if (resProf.getDataInserimento() != null) {
								SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
								String strDatProfiling = formatter.format(resProf.getDataInserimento().getTime());
								serviceRequest.updAttribute("DATPROFILING", strDatProfiling);
							}
							serviceRequest.updAttribute("NUMETA", resProf.getEta());
							if (resProf.getGenere() != null && resProf.getGenere().getValue() != null) {
								serviceRequest.updAttribute("STRSESSO", resProf.getGenere().getValue());
							}
							if (resProf.getCittadinanza() != null) {
								serviceRequest.updAttribute("CODPFCITTADINANZA", resProf.getCittadinanza());
							}
							if (resProf.getTitoloDiStudio() != null) {
								serviceRequest.updAttribute("codTitolo", resProf.getTitoloDiStudio());
							}
							if (resProf.getCondizioneProfessionaleAnnoPrecedente() != null) {
								serviceRequest.updAttribute("CODPFCONDOCCUP",
										resProf.getCondizioneProfessionaleAnnoPrecedente());
							}
							if (resProf.getDurataDellaDisoccupazione() != null) {
								serviceRequest.updAttribute("NUMMESIDISOCC", resProf.getDurataDellaDisoccupazione());
							}
							serviceRequest.updAttribute("NUMMESIRICERCALAV", resProf.getDurataRicercaLavoro());
							if (resProf.getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione() != null) {
								serviceRequest.updAttribute("CODPFISCRCORSO",
										resProf.getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione());
							}

							serviceRequest.updAttribute("CODPROVINCIARES", codProvinciaSil);
							if (resProf.getDurataPresenzaInItalia() != null) {
								serviceRequest.updAttribute("CODPFPRESENZAIT", resProf.getDurataPresenzaInItalia());
							}
							serviceRequest.updAttribute("FLGESPLAVORO",
									resProf.isHaLavoratoAlmenoUnaVolta() ? "S" : "N");
							if (resProf.getPosizioneUltimaOccupazione() != null) {
								serviceRequest.updAttribute("CODPFPOSIZIONEPROF",
										resProf.getPosizioneUltimaOccupazione());
							}
							serviceRequest.updAttribute("NUMNUCLEOFAM", resProf.getNumeroComponentiFamiglia());
							serviceRequest.updAttribute("FLGFIGLIACARICO",
									resProf.isPresenzaFigliACarico() ? "S" : "N");
							if (resProf.getPresenzaFigliMinoriACarico() != null) {
								serviceRequest.updAttribute("FLGFIGLIMINORENNI",
										resProf.getPresenzaFigliMinoriACarico() ? "S" : "N");
							}
						}
						serviceRequest.updAttribute("PRGCONFERIMENTODID", prgConferimento);
						this.setSectionQueryUpdate("QUERY_UPDATE_PROFILING");
						Object resUpd = doUpdate(serviceRequest, serviceResponse, false);

						if (resUpd == null || ((resUpd instanceof Boolean) && !((Boolean) resUpd).booleanValue())) {
							reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
						}

					} else {
						reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
					}
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ANPAL);
					String codEsitoRisposta = outputConvalida.getEsito();
					String descrizioneEsitoRisposta = ConferimentoUtility.getDescrizioneErroreMin(codEsitoRisposta);
					Vector paramV = new Vector<String>(2);
					paramV.add(codEsitoRisposta);
					paramV.add(descrizioneEsitoRisposta);
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.KO_ERR_MINISTERO, "CONFERMA",
							"ERRORE WS MINISTERO", paramV);
				}

				GestisciDIDOutput outputWS = null;
				boolean esitoOutputTracciatura = conferimento.getGestisciOutputWS(outputConvalida);
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
						outputConvalida.getEsito(), outputWS);
				if (!esitoTracciatura) {
					it.eng.sil.util.TraceWrapper.error(_logger, "errore tracciatura conferma xml",
							new Exception("errore tracciatura conferma xml"));
				}

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferma did ministeriale.", (Exception) e);
				if (!invocazioneConferimento) {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_INVOCAZIONE_WS);
				} else {
					reportOperation.reportFailure(MessageCodes.CONFERIMENTO_DID.ERR_LOG_AM_CONFERIMENTO_DID);
				}
			}
		}
	}

}
