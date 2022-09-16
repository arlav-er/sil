package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;

public class CMProspLavL68Save extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMProspLavL68Save.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		ResponseContainer responseContainer = getResponseContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		// sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
		boolean check = true;
		boolean isOKControlli = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			String codAssProtetta = serviceRequest.getAttribute("CODASSPROTETTA") == null ? ""
					: (String) serviceRequest.getAttribute("CODASSPROTETTA");
			if (codAssProtetta.equalsIgnoreCase(ProspettiConstant.CATEGORIA_ASS_PROTETTA_SOMM)) {
				String datInizioRapp = serviceRequest.getAttribute("datInizioRapp") == null ? ""
						: (String) serviceRequest.getAttribute("datInizioRapp");
				String datFineRapp = serviceRequest.getAttribute("datFineRapp") == null ? ""
						: (String) serviceRequest.getAttribute("datFineRapp");
				if (!datInizioRapp.equals("") && !datFineRapp.equals("")) {
					int giorni = (DateUtils.daysBetween(DateUtils.giornoSuccessivo(datInizioRapp), datFineRapp)) + 1;
					if ((giorni / 30) < 12) {
						reportOperation.reportFailure(
								MessageCodes.CollocamentoMirato.ERR_MISSIONE_TIPO_ASSUNZIONE_PROTETTA_SOMM);
						isOKControlli = false;
					}
				}
			}

			if (isOKControlli) {
				transExec = new TransactionQueryExecutor(getPool());
				this.enableTransactions(transExec);

				transExec.initTransaction();

				String prgProspettoInf = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPROSPETTOINF");
				String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
				String prgLavRiserva = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGLAVRISERVA");
				String codfiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceFiscaleLavoratore");
				String message = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");

				/*
				 * TODO controlli sul codice fiscale nel caso l'inserimento sia manuale
				 * 
				 * codfiscale = codfiscale.toUpperCase(); //"normalizzo" il codice fiscale
				 * 
				 * //controllo della lunghezza if (!CF_utils.checkLength(codfiscale)) { //se il controllo non va a buon
				 * fine throw new CfException( MessageCodes.CodiceFiscale.ERR_LUNGHEZZA ); //exception: segnalo l'errore
				 * con messaggio "CF_LEN" }
				 * 
				 * //controllo della correttezza posizionale dei caratteri numerici
				 * if(!CF_utils.checkCaratteriNumerici(codfiscale)) { //se il controllo non va a buon fine throw new
				 * CfException(MessageCodes.CodiceFiscale.ERR_CARATTERI_NUMERICI); // imposto il codice di errore }
				 * 
				 * 
				 * //controllo della correttezza posizionale dei caratteri alfabetici
				 * if(!CF_utils.checkCaratteriAlfabetici(codfiscale)) { //se il controllo non va a buon fine throw new
				 * CfException(MessageCodes.CodiceFiscale.ERR_CARATTERI_ALFABETICI); }
				 */

				// controllo sulla percentuale di invalidità
				String checkPercInvalidita = "0";
				if (cdnLavoratore != null && !("").equals(cdnLavoratore)) {
					checkPercInvalidita = getPercInvalidita(transExec, serviceRequest, serviceResponse, prgProspettoInf,
							cdnLavoratore);
				}

				String oreLavorate = serviceRequest.getAttribute("DECORELAVORATE") == null ? ""
						: (String) serviceRequest.getAttribute("DECORELAVORATE");
				String oreTotali = serviceRequest.getAttribute("DECORETOTALI") == null ? ""
						: (String) serviceRequest.getAttribute("DECORETOTALI");
				String copertura = serviceRequest.getAttribute("DECCOPERTURA") == null ? ""
						: (String) serviceRequest.getAttribute("DECCOPERTURA");

				// new flgdisplim LAV68
				String flgdisplim = serviceRequest.getAttribute("flgdisplim") == null ? ""
						: (String) serviceRequest.getAttribute("flgdisplim");

				SourceBean rowscSourceBean = (SourceBean) QueryExecutor.executeQuery("ST_GetConfig_CMPART", null,
						"SELECT", "SIL_DATI");
				String Config_CMPART = (String) rowscSourceBean.getAttribute("ROW.NUM");
				if (Config_CMPART == null) {
					Config_CMPART = "0";
				}

				// recupero il flag
				Object[] inputParameter = new Object[2];

				// cdnlavoratore e chiave
				inputParameter[0] = cdnLavoratore;
				String encryptKey = (String) getRequestContainer().getSessionContainer()
						.getAttribute("_ENCRYPTER_KEY_");
				inputParameter[1] = encryptKey;

				SourceBean rowsDFSourceBean = (SourceBean) QueryExecutor.executeQuery("ST_Getflgdisplim",
						inputParameter, "SELECT", "SIL_DATI");
				String flgdisplimDF = (String) rowsDFSourceBean.getAttribute("ROW.flgdisplim");
				// dataConnectioManager.release();
				// dc.close();

				String checkCMPART = "0";
				// se c'è la conf e nessuno dei due flag è = N e almeno uno flag è uguale a S la copertura è 1
				if ("1".equals(Config_CMPART)) {
					if ((("S").equalsIgnoreCase(flgdisplim) || ("S").equalsIgnoreCase(flgdisplimDF))) {
						checkCMPART = "1";
					}

					if ((("N").equalsIgnoreCase(flgdisplim) || ("N").equalsIgnoreCase(flgdisplimDF))) {
						checkCMPART = "0";
					}
				}

				if ((checkPercInvalidita != null && ("1").equalsIgnoreCase(checkPercInvalidita))
						|| "1".equals(checkCMPART)) {
					copertura = "1";
				} else {
					BigDecimal numCopertura = new BigDecimal("0");

					if (!("").equals(oreLavorate) && !("").equals(oreTotali)) {
						BigDecimal numOreLavorate = new BigDecimal(oreLavorate);
						BigDecimal numOreTotali = new BigDecimal(oreTotali);
						if (numOreTotali.compareTo(new BigDecimal("0")) > 0) {
							numCopertura = numOreLavorate.divide(numOreTotali, 2, BigDecimal.ROUND_HALF_UP);
							BigDecimal comp = new BigDecimal("0.50");
							if (comp.compareTo(numCopertura) == -1) {
								numCopertura = new BigDecimal("1");
							}

							copertura = numCopertura.toString();
						} else {
							copertura = "0";
						}
					}
				}

				serviceRequest.delAttribute("DECCOPERTURA");
				serviceRequest.setAttribute("DECCOPERTURA", copertura);

				if (("INSERT").equalsIgnoreCase(message)) {
					this.setSectionQueryInsert("QUERY_INSERT");
					check = doInsert(serviceRequest, serviceResponse);
				} else {
					this.setSectionQueryUpdate("QUERY_UPDATE");
					check = doUpdate(serviceRequest, serviceResponse);
				}

				if (!check) {
					throw new Exception("Errore durante l'aggiornamento del la l68. Operazione interrotta");
				}

				transExec.commitTransaction();

				reportOperation.reportSuccess(idSuccess);
			}

		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");

		} finally {
		}

	}

	public String getPercInvalidita(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response,
			String p_prgprospettoInf, String p_cdnLavoratore) {

		String checkPercInvalidita = "";
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			conn = txExecutor.getDataConnection();

			statementSB = (SourceBean) getConfig().getAttribute("CHECK_INVALIDITA_L68");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String encryptKey = (String) getRequestContainer().getSessionContainer().getAttribute("_ENCRYPTER_KEY_");

			parameters = new ArrayList(5);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. p_prgRichiestaAz
			parameters.add(conn.createDataField("p_prgprospettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgprospettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_prgTipoIncrocio
			parameters.add(
					conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, new BigInteger(p_cdnLavoratore)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_key
			parameters.add(conn.createDataField("p_encrypterKey", java.sql.Types.VARCHAR, encryptKey));
			command.setAsInputParameters(paramIndex++);
			// 5. checkPercInvalidita
			parameters.add(conn.createDataField("p_checkPercInvalidita", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			// 1. checkPercInvalidita
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			checkPercInvalidita = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Check verifica perc invalidità lavoratori L68. sqlCode=" + errCode;
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Check verifica perc invalidità lavoratori L68. SqlCode=" + errCode;
				}
				_logger.debug(msg);
			}
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure perc invalidità lavoratori L68";
			_logger.debug(msg);
		}

		return checkPercInvalidita;

	}
}
