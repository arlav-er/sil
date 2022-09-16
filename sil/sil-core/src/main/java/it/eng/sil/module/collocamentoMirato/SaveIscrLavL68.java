/*
 * Creato il 23-apr-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class SaveIscrLavL68 extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 337710861830322606L;

	private TransactionQueryExecutor transExec;
	BigDecimal userid, prgDocumento;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		String message = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			boolean success = false;
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
			String encryptKey = (String) sessionContainer.getAttribute("_ENCRYPTER_KEY_");
			String codStatoAtto = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");

			if (message.equals("INSERT")) {
				String codMonoTipoRagg = StringUtils.getAttributeStrNotNull(serviceRequest, "codMonoTipoRagg");
				String cdnLavoratoreEncrypt = StringUtils.getAttributeStrNotNull(serviceRequest,
						"cdnLavoratoreEncrypt");
				String codProvincia = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVINCIA_ISCR");
				BigDecimal numIscrizioneL68 = getNumIscrLavL68(codMonoTipoRagg, cdnLavoratoreEncrypt, codProvincia);
				if (numIscrizioneL68 != null) {
					BigDecimal key = getPrgCM(serviceRequest, serviceResponse);
					setParameterForCM(key, "PRGCMISCR", serviceRequest);
					setParameterForCM(numIscrizioneL68, "NUMISCRIZIONE", serviceRequest);

					DataConnection dataConnection = null;

					dataConnection = transExec.getDataConnection();

					String p_prgCmIscr = serviceRequest.getAttribute("PRGCMISCR").toString();
					String p_cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATOREENCRYPT");
					String p_numIscrizione = serviceRequest.getAttribute("NUMISCRIZIONE").toString();
					String p_codCmTipoIscr = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCMTIPOISCR");
					String p_datDataInizio = StringUtils.getAttributeStrNotNull(serviceRequest, "DATDATAINIZIO");
					String p_datDataFine = StringUtils.getAttributeStrNotNull(serviceRequest, "DATDATAFINE");
					String p_codTipoInvalidita = StringUtils.getAttributeStrNotNull(serviceRequest,
							"CODTIPOINVALIDITA");
					Object p_numPercInvalidita = serviceRequest.getAttribute("NUMPERCINVALIDITA");
					String p_codAccertSanitario = StringUtils.getAttributeStrNotNull(serviceRequest,
							"CODACCERTSANITARIO");
					String p_dataAccertSanitario = StringUtils.getAttributeStrNotNull(serviceRequest,
							"DATACCERTSANITARIO");
					String p_strNote = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOTE");
					Object p_prgSpiMod = serviceRequest.getAttribute("PRGSPIMOD");
					String p_cdnUtente = String.valueOf(user.getCodut());
					String p_datAnzianita68 = StringUtils.getAttributeStrNotNull(serviceRequest, "DATANZIANITA68");
					String p_datUltimaIscr = StringUtils.getAttributeStrNotNull(serviceRequest, "DATULTIMAISCR");
					String p_codMotivoFineAtto = StringUtils.getAttributeStrNotNull(serviceRequest,
							"CODMOTIVOFINEATTO");
					Object p_prgVerbaleAcc = serviceRequest.getAttribute("PRGVERBALEACC");
					String p_datAnzianitaOrdinaria = StringUtils.getAttributeStrNotNull(serviceRequest,
							"DATANZORDINARIAPREGRESSA");
					String p_codCmAnnota = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCMANNOTA");
					String p_codCmProvincia = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVINCIA_ISCR");

					SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY_INSERT_ISCR");
					String statement = statementSB.getAttribute("STATEMENT").toString();
					String sqlStr = SQLStatements.getStatement(statement);

					List<DataField> inputParameter = new ArrayList<DataField>();
					inputParameter.add(
							dataConnection.createDataField("PRGCMISCR", Types.BIGINT, new BigInteger(p_prgCmIscr)));
					inputParameter.add(dataConnection.createDataField("CDNLAVORATORE", Types.VARCHAR, p_cdnLavoratore));
					inputParameter.add(dataConnection.createDataField("NUMISCRIZIONE", Types.BIGINT,
							new BigInteger(p_numIscrizione)));
					inputParameter.add(dataConnection.createDataField("CODCMTIPOISCR", Types.VARCHAR, p_codCmTipoIscr));
					inputParameter.add(dataConnection.createDataField("DATDATAINIZIO", Types.VARCHAR, p_datDataInizio));
					inputParameter.add(dataConnection.createDataField("DATDATAFINE", Types.VARCHAR, p_datDataFine));
					inputParameter.add(
							dataConnection.createDataField("CODTIPOINVALIDITA", Types.VARCHAR, p_codTipoInvalidita));
					inputParameter.add(
							dataConnection.createDataField("NUMPERCINVALIDITA", Types.BIGINT, p_numPercInvalidita));
					inputParameter.add(
							dataConnection.createDataField("CODACCERTSANITARIO", Types.VARCHAR, p_codAccertSanitario));
					inputParameter.add(
							dataConnection.createDataField("DATACCERTSANITARIO", Types.VARCHAR, p_dataAccertSanitario));
					inputParameter.add(dataConnection.createDataField("STRNOTE", Types.VARCHAR, p_strNote));
					inputParameter.add(dataConnection.createDataField("PRGSPIMOD", Types.BIGINT, p_prgSpiMod));
					inputParameter.add(
							dataConnection.createDataField("p_cdnUtente", Types.BIGINT, new BigInteger(p_cdnUtente)));
					inputParameter.add(
							dataConnection.createDataField("p_cdnUtente", Types.BIGINT, new BigInteger(p_cdnUtente)));
					inputParameter
							.add(dataConnection.createDataField("DATANZIANITA68", Types.VARCHAR, p_datAnzianita68));
					inputParameter.add(dataConnection.createDataField("DATULTIMAISCR", Types.VARCHAR, p_datUltimaIscr));
					inputParameter.add(
							dataConnection.createDataField("CODMOTIVOFINEATTO", Types.VARCHAR, p_codMotivoFineAtto));
					inputParameter.add(dataConnection.createDataField("PRGVERBALEACC", Types.BIGINT, p_prgVerbaleAcc));
					inputParameter.add(dataConnection.createDataField("CODSTATOATTO", Types.VARCHAR, codStatoAtto));
					inputParameter.add(dataConnection.createDataField("CODCMANNOTA", Types.VARCHAR, p_codCmAnnota));
					inputParameter.add(dataConnection.createDataField("DATANZORDPREGRESSA", Types.VARCHAR,
							p_datAnzianitaOrdinaria));
					inputParameter.add(dataConnection.createDataField("CODPROVINCIA", Types.VARCHAR, p_codCmProvincia));

					QueryExecutorObject queryExecObj = new QueryExecutorObject();

					queryExecObj.setRequestContainer(null);
					queryExecObj.setResponseContainer(null);
					queryExecObj.setDataConnection(dataConnection);
					queryExecObj.setInputParameters(inputParameter);
					queryExecObj.setType(QueryExecutorObject.INSERT);
					queryExecObj.setStatement(sqlStr);

					queryExecObj.setTransactional(true);
					queryExecObj.setDontForgetException(true);

					Object returned = queryExecObj.exec();

					if (returned instanceof Boolean && ((Boolean) returned).booleanValue() == true) {
						if (getAttributeAsBigDecimal(serviceRequest, "PRGVERBALEACC") != null) {
							checkVerbaleAccertamento(serviceRequest, serviceResponse, reportOperation);
						}
						insDocumento(serviceRequest);
						setFlagIscrLavCM(cdnLavoratore, userid, encryptKey);
						if (!codStatoAtto.equals("NP")) {
							aggiornaSchedaIscrLavCM(cdnLavoratore, userid);
							if (codMonoTipoRagg.equalsIgnoreCase(Properties.CM_RAGG_DISABILE)) {
								SourceBean sbGenerale = DBLoad.getInfoGenerali();
								sbGenerale = sbGenerale.containsAttribute("ROW")
										? (SourceBean) sbGenerale.getAttribute("ROW")
										: sbGenerale;
								String data150 = (String) sbGenerale.getAttribute("DAT150");
								if (data150 != null && !data150.equals("")) {
									if (!requestContainer.getServiceRequest().containsAttribute("FORZA_INSERIMENTO")) {
										requestContainer.getServiceRequest().setAttribute("FORZA_INSERIMENTO", "true");
									}
									if (!requestContainer.getServiceRequest()
											.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
										requestContainer.getServiceRequest().setAttribute("CONTINUA_CALCOLO_SOCC",
												"true");
									}
									if (!requestContainer.getServiceRequest()
											.containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
										requestContainer.getServiceRequest().setAttribute("FORZA_CHIUSURA_MOBILITA",
												"true");
									}
									StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
											.newInstance(cdnLavoratore, DateUtils.giornoPrecedente(data150), transExec)
											.calcolaImpatti();
									reportOperation.reportSuccess(MessageCodes.StatoOccupazionale.ESEGUITO_RICALCOLO);
								}
							}
						}
						success = true;
					} else {
						int codiceMessaggio = MessageCodes.General.INSERT_FAIL;

						if (((EMFInternalError) returned).getNativeException() instanceof java.sql.SQLException) {
							int sqlCode = this.getSqlCode(returned);
							if (sqlCode == 20012) {
								codiceMessaggio = MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE;
							} else if (sqlCode == 20014) {
								codiceMessaggio = MessageCodes.General.ERR_ESISTE_ISCRIZIONE_NON_PROT;
							}

						}
						reportOperation.reportFailure(codiceMessaggio, (EMFInternalError) returned, "services()",
								"insert in transazione");
					}
				} else {
					transExec.rollBackTransaction();
					reportOperation.reportFailure(MessageCodes.CollocamentoMirato.ERR_NUMERAZIONE_AMBITO_TERRITORIALE);
					return;
				}
			} else if (message.equals("UPDATE")) {
				if (serviceRequest.containsAttribute("aggiornaDoc")) {
					this.setSectionQuerySelect("QUERY_SELECT_DOC");
					SourceBean rowDoc = doSelect(serviceRequest, serviceResponse);
					if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
						prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");
					}
					String codStatoAttoOld = (String) rowDoc.getAttribute("ROW.CODSTATOATTO");
					if (prgDocumento != null) {
						if (!codStatoAtto.equalsIgnoreCase(codStatoAttoOld)) {
							this.setSectionQueryUpdate("QUERY_UPD_STATO_ATTO_ISCR");
							success = this.doUpdate(serviceRequest, serviceResponse);
							if (success) {
								String numkloDocumento = String.valueOf(
										((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
								serviceRequest.setAttribute("prgDocumento", prgDocumento);
								serviceRequest.setAttribute("numkloDocumento", numkloDocumento);

								String datInizio = (String) rowDoc.getAttribute("ROW.DATINIZIO");
								String datAcqRil = (String) rowDoc.getAttribute("ROW.DATACQRIL");

								setParameterForCM(datInizio, "DatInizio", serviceRequest);
								setParameterForCM(datAcqRil, "DatAcqRil", serviceRequest);
								updDocumento(serviceRequest);
								setFlagIscrLavCM(cdnLavoratore, userid, encryptKey);
								if (!(codStatoAtto.equals("NP")
										|| (codStatoAtto.equals("AN") && codStatoAttoOld.equals("NP")))) {
									aggiornaSchedaIscrLavCM(cdnLavoratore, userid);
									Object params[] = { serviceRequest.getAttribute("PRGCMISCR") };
									SourceBean rowCM = (SourceBean) transExec
											.executeQuery("AMSTR_GET_DETTAGLIO_AM_CM_ISCR", params, "SELECT");
									if (rowCM != null) {
										rowCM = rowCM.containsAttribute("ROW") ? (SourceBean) rowCM.getAttribute("ROW")
												: rowCM;
										String codMonoTipoRagg = StringUtils.getAttributeStrNotNull(rowCM,
												"codMonoTipoRagg");
										if (codMonoTipoRagg.equalsIgnoreCase(Properties.CM_RAGG_DISABILE)) {
											SourceBean sbGenerale = DBLoad.getInfoGenerali();
											sbGenerale = sbGenerale.containsAttribute("ROW")
													? (SourceBean) sbGenerale.getAttribute("ROW")
													: sbGenerale;
											String data150 = (String) sbGenerale.getAttribute("DAT150");
											if (data150 != null && !data150.equals("")) {
												if (!requestContainer.getServiceRequest()
														.containsAttribute("FORZA_INSERIMENTO")) {
													requestContainer.getServiceRequest()
															.setAttribute("FORZA_INSERIMENTO", "true");
												}
												if (!requestContainer.getServiceRequest()
														.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
													requestContainer.getServiceRequest()
															.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
												}
												if (!requestContainer.getServiceRequest()
														.containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
													requestContainer.getServiceRequest()
															.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
												}
												StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
														.newInstance(cdnLavoratore, DateUtils.giornoPrecedente(data150),
																transExec)
														.calcolaImpatti();
												reportOperation.reportSuccess(
														MessageCodes.StatoOccupazionale.ESEGUITO_RICALCOLO);
											}
										}
									}
								}
							}
						}
					} else
						throw new Exception();
				} else if (!serviceRequest.containsAttribute("aggiornaDoc")) {
					this.setSectionQueryUpdate("QUERY_UPDATE_ISCR");
					success = this.doUpdate(serviceRequest, serviceResponse);
					if (success) {
						if (getAttributeAsBigDecimal(serviceRequest, "PRGVERBALEACC") != null) {
							checkVerbaleAccertamento(serviceRequest, serviceResponse, reportOperation);
						}
						setFlagIscrLavCM(cdnLavoratore, userid, encryptKey);
						if (!codStatoAtto.equals("NP")) {
							aggiornaSchedaIscrLavCM(cdnLavoratore, userid);
						}
						String insStorico = StringUtils.getAttributeStrNotNull(serviceRequest, "INSSTORICO");
						if (insStorico.equals("TRUE")) {
							BigDecimal keySto = getPrgCMStorico(serviceRequest, serviceResponse);
							setParameterForCM(keySto, "PRGCMISCRSTORICO", serviceRequest);
							this.setSectionQueryInsert("QUERY_INSERT_ISCR_STORICO");
							success = this.doInsert(serviceRequest, serviceResponse);
						}
					}
				}
			} else if (message.equals("UPDATE_DIAGNOSI")) {
				this.setSectionQueryUpdate("QUERY_UPDATE_ISCR_DA_DIAGNOSI");
				success = this.doUpdate(serviceRequest, serviceResponse);
				if (success) {
					if (getAttributeAsBigDecimal(serviceRequest, "PRGVERBALEACC") != null) {
						checkVerbaleAccertamento(serviceRequest, serviceResponse, reportOperation);
					}
					String cdnLavoratoreDecrypt = EncryptDecryptUtils.decrypt(cdnLavoratore);
					setFlagIscrLavCM(cdnLavoratoreDecrypt, userid, encryptKey);
					if (!codStatoAtto.equals("NP")) {
						aggiornaSchedaIscrLavCM(cdnLavoratoreDecrypt, userid);
					}
					String insStorico = StringUtils.getAttributeStrNotNull(serviceRequest, "INSSTORICO");
					if ("TRUE".equals(insStorico)) {
						BigDecimal keySto = getPrgCMStorico(serviceRequest, serviceResponse);
						setParameterForCM(keySto, "PRGCMISCRSTORICO", serviceRequest);
						this.setSectionQueryInsert("QUERY_INSERT_ISCR_STORICO");
						success = this.doInsert(serviceRequest, serviceResponse);
					}
				}
			}

			if (success) {
				transExec.commitTransaction();
				ProtocolloDocumentoUtil.cancellaFileDocarea();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}
		} catch (EMFInternalError ex) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			int codiceMessaggio = MessageCodes.General.INSERT_FAIL;
			Throwable errOrigine = ((EMFInternalError) ex).getNativeException();
			int code = this.getSqlCode(ex);
			int sqlCode = 0;
			if (sqlCode == 20012) {
				codiceMessaggio = MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE;
			} else if (sqlCode == 20014) {
				codiceMessaggio = MessageCodes.General.ERR_ESISTE_ISCRIZIONE_NON_PROT;
			}

			reportOperation.reportFailure(codiceMessaggio, ex, "services()", "insert in transazione");
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			if (message.equals("INSERT")) {
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()",
						"insert in transazione");
			} else {
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "services()",
						"update in transazione");
			}
		}
	}

	private void setParameterForCM(BigDecimal pk, String attribute, SourceBean request) throws Exception {
		if (request.getAttribute(attribute) != null) {
			request.delAttribute(attribute);
		}

		request.setAttribute(attribute, pk);
	}

	private void setParameterForCM(String pk, String attribute, SourceBean request) throws Exception {
		if (request.getAttribute(attribute) != null) {
			request.delAttribute(attribute);
		}

		request.setAttribute(attribute, pk);
	}

	private BigDecimal getPrgCM(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("QUERY_SEQUENCE");

		SourceBean sb = doSelect(request, response);

		if (sb != null) {
			return (BigDecimal) sb.getAttribute("ROW.PRGCMISCR");
		} else {
			return null;
		}
	}

	private BigDecimal getPrgCMStorico(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("QUERY_SEQUENCE_STORICO");

		SourceBean sb = doSelect(request, response);

		if (sb != null) {
			return (BigDecimal) sb.getAttribute("ROW.PRGCMISCRSTORICO");
		} else {
			return null;
		}
	}

	private BigDecimal getNumIscrLavL68(String p_codMonoTipoRagg, String p_cdnLavoratore, String p_codProvincia)
			throws Exception {

		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		conn = transExec.getDataConnection();
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		BigDecimal numIscr = null;

		int paramIndex = 0;
		ArrayList<DataField> parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("QUERY_PROC");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		parameters = new ArrayList<DataField>(4);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("p_numIscr", java.sql.Types.BIGINT, null));
		command.setAsOutputParameters(paramIndex++);
		// preparazione dei Parametri di Input
		// 2. p_codMonoTipoRagg
		parameters.add(conn.createDataField("p_codMonoTipoRagg", java.sql.Types.VARCHAR, p_codMonoTipoRagg));
		command.setAsInputParameters(paramIndex++);
		// 3. p_cdnLavoratore
		parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.VARCHAR, p_cdnLavoratore));
		command.setAsInputParameters(paramIndex++);
		// 4. p_codProvincia
		parameters.add(conn.createDataField("p_codProvincia", java.sql.Types.VARCHAR, p_codProvincia));
		command.setAsInputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);
		PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();

		// Reperisco il valore di output della stored
		DataField df = pdr.getPunctualDatafield();
		if (df.getStringValue() != null) {
			numIscr = new BigDecimal(df.getStringValue());
		}

		return numIscr;

	}

	private void setFlagIscrLavCM(String p_cdnLavoratore, BigDecimal p_cdnUtMod, String p_encryptKey) throws Exception {

		DataConnection conn = null;
		StoredProcedureCommand command = null;

		conn = transExec.getDataConnection();
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";

		int paramIndex = 0;
		ArrayList<DataField> parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("QUERY_PROC_FLAG");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		parameters = new ArrayList<DataField>(3);
		// preparazione dei Parametri di Input
		// 1. p_cdnLavoratore
		parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, new BigDecimal(p_cdnLavoratore)));
		command.setAsInputParameters(paramIndex++);
		// 2. p_cdnUtMod
		parameters.add(conn.createDataField("p_cdnUtMod", java.sql.Types.BIGINT, p_cdnUtMod));
		command.setAsInputParameters(paramIndex++);
		// 3. p_encryptKey
		parameters.add(conn.createDataField("p_encryptKey", java.sql.Types.VARCHAR, p_encryptKey));
		command.setAsInputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		command.execute(parameters);

	}

	private void aggiornaSchedaIscrLavCM(String p_cdnLavoratore, BigDecimal p_cdnUtMod) throws Exception {

		DataConnection conn = null;
		StoredProcedureCommand command = null;

		conn = transExec.getDataConnection();
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";

		int paramIndex = 0;
		ArrayList<DataField> parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("QUERY_PROC_SCHEDA");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		parameters = new ArrayList<DataField>(2);
		// preparazione dei Parametri di Input
		// 1. p_cdnLavoratore
		parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, new BigDecimal(p_cdnLavoratore)));
		command.setAsInputParameters(paramIndex++);
		// 2. p_cdnUtMod
		parameters.add(conn.createDataField("p_cdnUtMod", java.sql.Types.BIGINT, p_cdnUtMod));
		command.setAsInputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		command.execute(parameters);

	}

	private void insDocumento(SourceBean request) throws Exception {
		Documento doc = new Documento();

		doc.setPrgDocumento(null);

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
		doc.setChiaveTabella((getAttributeAsBigDecimal(request, "PRGCMISCR")).toString());
		doc.setCodMotAnnullamentoAtto(getAttributeAsString(request, "codMotAnnullamentoAtto"));
		doc.setCodStatoAtto(getAttributeAsString(request, "CODSTATOATTO"));

		String pagina = getAttributeAsString(request, "PAGE");
		if (!pagina.equalsIgnoreCase("null"))
			doc.setPagina(pagina);
		doc.setCdnUtMod(userid);
		doc.setCdnUtIns(userid);
		ProtocolloDocumentoUtil.putInRequest(doc);
		doc.insert(transExec);
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
		doc.setChiaveTabella((getAttributeAsBigDecimal(request, "PRGCMISCR")).toString());
		doc.setCodMotAnnullamentoAtto(getAttributeAsString(request, "codMotAnnullamentoAtto"));
		doc.setCodStatoAtto(getAttributeAsString(request, "CODSTATOATTO"));

		String pagina = getAttributeAsString(request, "PAGE");
		if (!pagina.equalsIgnoreCase("null"))
			doc.setPagina(pagina);
		doc.setCdnUtMod(userid);
		ProtocolloDocumentoUtil.putInRequest(doc);
		doc.update(transExec);
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

	private void checkVerbaleAccertamento(SourceBean request, SourceBean response, ReportOperationResult rptOp)
			throws Exception {
		this.setSectionQuerySelect("QUERY_DAT_VERB_ACC");

		SourceBean sb = doSelect(request, response);

		if (sb != null && sb.containsAttribute("ROW.DATVERBALEDAREVISIONARE")) {
			String datVerbDaRev = (String) sb.getAttribute("ROW.DATVERBALEDAREVISIONARE");
			String dataOdierna = DateUtils.getNow();
			if (DateUtils.compare(datVerbDaRev, dataOdierna) < 1) {
				rptOp.reportWarning(MessageCodes.CollocamentoMirato.WARNING_DAT_VERB_ACC_MINORE_DAT_ODIERNA);
			}
		}
	}

}
