/*
 * Creato il 23-gen-08
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InsertIscrLavL68DaDid extends AbstractSimpleModule {

	private String className = this.getClass().getName();
	private TransactionQueryExecutor transExec;
	BigDecimal userid, prgDocumento;
	String dataOdierna, userCodRif;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());
		userCodRif = user.getCodRif();

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		int codiceMessaggio = MessageCodes.General.INSERT_FAIL;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			dataOdierna = DateUtils.getNow();

			boolean success = false;
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
			String encryptKey = (String) sessionContainer.getAttribute("_ENCRYPTER_KEY_");

			setInRequestOldIscrCM(serviceRequest, serviceResponse);

			String p_dataAccertSanitario = "";
			String codMonoTipoRagg = StringUtils.getAttributeStrNotNull(serviceRequest, "codMonoTipoRagg");
			String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore);
			String codProvincia = StringUtils.getAttributeStrNotNull(serviceRequest, "CODPROVINCIACMOLD");
			BigDecimal numIscrizioneL68 = getNumIscrLavL68(codMonoTipoRagg, cdnLavoratoreEncrypt, codProvincia);
			if (numIscrizioneL68 != null) {
				BigDecimal key = getPrgCM(serviceRequest, serviceResponse);
				setParameterForCM(key, "PRGCMISCR", serviceRequest);
				setParameterForCM(numIscrizioneL68, "NUMISCRIZIONE", serviceRequest);

				DataConnection dataConnection = null;

				DataResult dr = null;
				dataConnection = transExec.getDataConnection();

				String p_prgCmIscr = serviceRequest.getAttribute("PRGCMISCR").toString();
				String p_numIscrizione = serviceRequest.getAttribute("NUMISCRIZIONE").toString();
				String p_codCmTipoIscr = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCMTIPOISCR");
				String p_datDataInizio = dataOdierna;
				String p_codTipoInvalidita = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOINVALIDITA");
				Object p_numPercInvalidita = serviceRequest.getAttribute("NUMPERCINVALIDITA");
				String p_codAccertSanitario = StringUtils.getAttributeStrNotNull(serviceRequest, "CODACCERTSANITARIO");
				p_dataAccertSanitario = StringUtils.getAttributeStrNotNull(serviceRequest, "DATACCERTSANITARIO");
				String p_strNote = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOTE");
				Object p_prgSpiMod = serviceRequest.getAttribute("PRGSPIMOD");
				String p_cdnUtente = String.valueOf(user.getCodut());
				String p_datAnzianita68 = StringUtils.getAttributeStrNotNull(serviceRequest, "DATANZIANITA68");
				String p_datUltimaIscr = dataOdierna;
				Object p_prgVerbaleAcc = serviceRequest.getAttribute("PRGVERBALEACC");
				Object p_codCmAnnota = serviceRequest.getAttribute("CODCMANNOTA");

				SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY_INSERT_ISCR");
				String statement = statementSB.getAttribute("STATEMENT").toString();
				String sqlStr = SQLStatements.getStatement(statement);

				List inputParameter = new ArrayList();
				inputParameter
						.add(dataConnection.createDataField("PRGCMISCR", Types.BIGINT, new BigInteger(p_prgCmIscr)));
				inputParameter
						.add(dataConnection.createDataField("CDNLAVORATORE", Types.VARCHAR, cdnLavoratoreEncrypt));
				inputParameter.add(
						dataConnection.createDataField("NUMISCRIZIONE", Types.BIGINT, new BigInteger(p_numIscrizione)));
				inputParameter.add(dataConnection.createDataField("CODCMTIPOISCR", Types.VARCHAR, p_codCmTipoIscr));
				inputParameter.add(dataConnection.createDataField("DATDATAINIZIO", Types.VARCHAR, p_datDataInizio));
				inputParameter.add(dataConnection.createDataField("DATDATAFINE", Types.VARCHAR, ""));
				inputParameter
						.add(dataConnection.createDataField("CODTIPOINVALIDITA", Types.VARCHAR, p_codTipoInvalidita));
				inputParameter
						.add(dataConnection.createDataField("NUMPERCINVALIDITA", Types.BIGINT, p_numPercInvalidita));
				inputParameter
						.add(dataConnection.createDataField("CODACCERTSANITARIO", Types.VARCHAR, p_codAccertSanitario));
				inputParameter.add(
						dataConnection.createDataField("DATACCERTSANITARIO", Types.VARCHAR, p_dataAccertSanitario));
				inputParameter.add(dataConnection.createDataField("STRNOTE", Types.VARCHAR, p_strNote));
				inputParameter.add(dataConnection.createDataField("PRGSPIMOD", Types.BIGINT, p_prgSpiMod));
				inputParameter
						.add(dataConnection.createDataField("p_cdnUtente", Types.BIGINT, new BigInteger(p_cdnUtente)));
				inputParameter
						.add(dataConnection.createDataField("p_cdnUtente", Types.BIGINT, new BigInteger(p_cdnUtente)));
				inputParameter.add(dataConnection.createDataField("DATANZIANITA68", Types.VARCHAR, p_datAnzianita68));
				inputParameter.add(dataConnection.createDataField("DATULTIMAISCR", Types.VARCHAR, p_datUltimaIscr));
				inputParameter.add(dataConnection.createDataField("CODMOTIVOFINEATTO", Types.VARCHAR, ""));
				inputParameter.add(dataConnection.createDataField("PRGVERBALEACC", Types.BIGINT, p_prgVerbaleAcc));
				inputParameter.add(dataConnection.createDataField("CODSTATOATTO", Types.VARCHAR, "PR"));
				inputParameter.add(dataConnection.createDataField("CODCMANNOTA", Types.VARCHAR, p_codCmAnnota));
				inputParameter.add(dataConnection.createDataField("DATANZORDPREGRESSA", Types.VARCHAR, null));
				inputParameter.add(dataConnection.createDataField("CODPROVINCIA", Types.VARCHAR, codProvincia));

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
					success = true;
				} else {

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

			if (success) {
				transExec.commitTransaction();
				ProtocolloDocumentoUtil.cancellaFileDocarea();
				reportOperation.reportSuccess(idSuccess);
				serviceResponse.setAttribute("esito", "true");
			} else {
				transExec.rollBackTransaction();
				serviceResponse.setAttribute("esito", "false");
			}
		} catch (EMFInternalError ex) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
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
			reportOperation.reportFailure(codiceMessaggio, e, "services()", "insert in transazione");
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

	private void setInRequestOldIscrCM(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_SELECT_OLD_ISCR");

		SourceBean sbOldIscr = doSelect(request, response);

		if (sbOldIscr != null) {
			String codMonoTipoRagg = getAttributeAsString(sbOldIscr, "ROW.codMonoTipoRagg");
			String codCmTipoIscr = getAttributeAsString(sbOldIscr, "ROW.CODCMTIPOISCR");
			String codTipoInvalidita = getAttributeAsString(sbOldIscr, "ROW.CODTIPOINVALIDITA");
			BigDecimal numPercInvalidita = getAttributeAsBigDecimal(sbOldIscr, "ROW.NUMPERCINVALIDITA");
			String codAccertSanitario = getAttributeAsString(sbOldIscr, "ROW.CODACCERTSANITARIO");
			String dataAccertSanitario = getAttributeAsString(sbOldIscr, "ROW.DATACCERTSANITARIO");
			String strNote = getAttributeAsString(sbOldIscr, "ROW.STRNOTE");
			BigDecimal prgSpiMod = getAttributeAsBigDecimal(sbOldIscr, "ROW.PRGSPIMOD");
			BigDecimal prgVerbaleAcc = getAttributeAsBigDecimal(sbOldIscr, "ROW.PRGVERBALEACC");
			String codProvincia = getAttributeAsString(sbOldIscr, "ROW.CODPROVINCIA");

			setParameterForCM(codMonoTipoRagg, "codMonoTipoRagg", request);
			setParameterForCM(codCmTipoIscr, "CODCMTIPOISCR", request);
			setParameterForCM(codTipoInvalidita, "CODTIPOINVALIDITA", request);
			if (numPercInvalidita != null) {
				setParameterForCM(numPercInvalidita, "NUMPERCINVALIDITA", request);
			}
			setParameterForCM(codAccertSanitario, "CODACCERTSANITARIO", request);
			setParameterForCM(dataAccertSanitario, "DATACCERTSANITARIO", request);
			setParameterForCM(codProvincia, "CODPROVINCIACMOLD", request);
			setParameterForCM(strNote, "STRNOTE", request);
			if (prgSpiMod != null) {
				setParameterForCM(prgSpiMod, "PRGSPIMOD", request);
			}
			if (prgVerbaleAcc != null) {
				setParameterForCM(prgVerbaleAcc, "PRGVERBALEACC", request);
			}
		} else {
			throw new Exception("Nessun valore della vecchia iscrizione recuperato");
		}
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
		ArrayList parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("QUERY_PROC");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		parameters = new ArrayList(4);
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

		boolean checkSuccess = false;
		DataConnection conn = null;
		StoredProcedureCommand command = null;

		conn = transExec.getDataConnection();
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";

		int paramIndex = 0;
		ArrayList parameters = null;

		statementSB = (SourceBean) getConfig().getAttribute("QUERY_PROC_FLAG");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		parameters = new ArrayList(3);
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

	private void insDocumento(SourceBean request) throws Exception {
		Documento doc = new Documento();
		SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataEOraProtocollo = fd.format(new Date());
		String annoProtocollo = dataOdierna.substring(6, 10);

		doc.setPrgDocumento(null);

		doc.setCodCpi(userCodRif);
		doc.setCdnLavoratore(getAttributeAsBigDecimal(request, "cdnLavoratore"));
		doc.setCodTipoDocumento("L68");
		doc.setDatInizio(dataOdierna);
		doc.setStrEnteRilascio(userCodRif);
		doc.setCodMonoIO("I");
		doc.setDatAcqril(dataOdierna);
		doc.setTipoProt("S");
		doc.setChiaveTabella((getAttributeAsBigDecimal(request, "PRGCMISCR")).toString());

		doc.setCodStatoAtto("PR");
		// valori generici necessari per la protocollazione
		doc.setDatProtocollazione(dataEOraProtocollo);
		doc.setNumAnnoProt(new BigDecimal(annoProtocollo));
		doc.setNumProtocollo(new BigDecimal(0));

		doc.setPagina(getAttributeAsString(request, "FROM_PAGE"));
		doc.setCdnUtMod(userid);
		doc.setCdnUtIns(userid);
		ProtocolloDocumentoUtil.putInRequest(doc);
		doc.insert(transExec);
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