package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.blen.StringUtils;

public class VerificaModificheForzaturaMovimenti extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1312858030340691359L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(VerificaModificheForzaturaMovimenti.class.getName());

	private static final int ESITO_OK = 0;

	@SuppressWarnings("rawtypes")
	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		MultipleTransactionQueryExecutor trans = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;

		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		disableMessageIdFail();
		disableMessageIdSuccess();

		try {

			String strPrgMaster = null;
			BigDecimal prgMaster = null;
			trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);

			// se vengo da una forzatura elimino prima la precedente modifica
			if (serviceRequest.containsAttribute("PRGFORZATURA")) {
				strPrgMaster = (String) serviceRequest.getAttribute("PRGFORZATURA");
				prgMaster = new BigDecimal(strPrgMaster);
				if (StringUtils.isFilledNoBlank(strPrgMaster)) {
					trans.initTransaction();
					trans.executeQuery("DELETE_AMFORZAMOV_ESITO", new Object[] { prgMaster }, "DELETE");
					trans.executeQuery("DELETE_AMFORZAMOV", new Object[] { prgMaster }, "DELETE");
					trans.executeQuery("DELETE_AM_FORZATURA", new Object[] { prgMaster }, "DELETE");
					trans.commitTransaction();
				}
			}

			trans.initTransaction();
			String strNumeroRighe = (String) serviceRequest.getAttribute("numeroRighe");

			int numRighe = (new Integer(strNumeroRighe)).intValue();
			int numRigheTot = numRighe;
			// eventuali righe eliminate
			String righeEliminate = (String) serviceRequest.getAttribute("rigaEliminate");

			String[] indiciRigheDel;
			if (StringUtils.isFilledNoBlank(righeEliminate)) {
				indiciRigheDel = righeEliminate.split(",");
				numRighe = numRighe - indiciRigheDel.length;
			} else {
				indiciRigheDel = null;
			}

			if (prgMaster == null) {
				prgMaster = DBKeyGenerator.getNextSequence(trans.getDataConnection(), "S_AM_FORZATURA");
			}

			trans.executeQuery("INSERT_FORZATURA_MOV_MASTER",
					new Object[] { new BigDecimal(numRighe), userid, prgMaster }, "INSERT");

			outerloop: for (int i = 1; i <= numRigheTot; i++) {

				if (numRighe < numRigheTot && indiciRigheDel != null) {
					for (int j = 0; j < indiciRigheDel.length; j++) {
						String temp = indiciRigheDel[j];
						int iTemp = (new Integer(temp)).intValue();
						if (i == iTemp) {
							// salto gli indici di riga eliminati
							continue outerloop;
						}
					}
				}
				// sono in presenza di variazioni
				String strPrgMovimento = (String) serviceRequest.getAttribute("prgMovimento_" + i);
				BigDecimal prgMovimento = new BigDecimal(strPrgMovimento);
				BigDecimal prgPrecMod = null;
				BigDecimal prgSuccMod = null;
				// boolean doSelectDate = false;
				String dataInizioMod = (String) serviceRequest.getAttribute("name_" + i + "_dataInizio");
				if (StringUtils.isEmptyNoBlank(dataInizioMod)) {
					dataInizioMod = null;
				}
				String dataFineEffMod = (String) serviceRequest.getAttribute("name_" + i + "_dataFineEffettiva");
				if (StringUtils.isEmptyNoBlank(dataFineEffMod)) {
					dataFineEffMod = null;
				}
				String statoMod = (String) serviceRequest.getAttribute("name_" + i + "_stato");
				String strPrgPrecMod = (String) serviceRequest.getAttribute("name_" + i + "_prgPrec");
				if (StringUtils.isFilledNoBlank(strPrgPrecMod)) {
					prgPrecMod = new BigDecimal(strPrgPrecMod);
				}
				String strPrgSuccMod = (String) serviceRequest.getAttribute("name_" + i + "_prgSucc");
				if (StringUtils.isFilledNoBlank(strPrgSuccMod)) {
					prgSuccMod = new BigDecimal(strPrgSuccMod);
				}

				BigDecimal prgDetail = DBKeyGenerator.getNextSequence(trans.getDataConnection(), "S_AM_FORZA_MOV");

				trans.executeQuery("INSERT_FORZATURA_MOV_DETAIL", new Object[] { dataInizioMod, dataFineEffMod,
						prgSuccMod, statoMod, prgPrecMod, prgMovimento, prgMaster, prgDetail }, "INSERT");
			}
			// -- COMMIT TRANSAZIONE
			trans.commitTransaction();
			serviceResponse.setAttribute("PRGFORZATURA", prgMaster);

			// init transazione per procedura
			trans.initTransaction();
			// chiamata alla funzione di controllo
			Long esito = null;
			int paramIndex = 0;
			ArrayList parameters = null;

			command = (StoredProcedureCommand) trans.getDataConnection()
					.createStoredProcedureCommand("{ call PG_MOVIMENTI.ModificaForzataMovimenti(?,?,?) }");

			parameters = new ArrayList(3);

			// 1. prgAmForzatura
			parameters
					.add(trans.getDataConnection().createDataField("prgAmForzatura", java.sql.Types.BIGINT, prgMaster));
			command.setAsInputParameters(paramIndex++);
			// 2. userId utente che effettua la modifica
			parameters
					.add(trans.getDataConnection().createDataField("cdnUtenteModifica", java.sql.Types.BIGINT, userid));
			command.setAsInputParameters(paramIndex++);
			// 3. Parametro di output
			parameters.add(trans.getDataConnection().createDataField("risultato", java.sql.Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			// Recupero i valori di output della stored
			// 0. errCodeOut
			DataField df = pdr.getPunctualDatafield();
			Object errCodeOut = df.getObjectValue();
			esito = (Long) errCodeOut;

			if (esito.intValue() == ESITO_OK) {
				// -- COMMIT TRANSAZIONE PROCEDURA
				trans.commitTransaction();
				serviceResponse.setAttribute("ESITO", "OK");
				reportOperation.reportSuccess(MessageCodes.ForzaturaMovimenti.FORZATURA_OK);
			} else {
				// -- ROLLBACK TRANSAZIONE PROCEDURA
				trans.rollBackTransaction();
				serviceResponse.setAttribute("ESITO", "KO");
				SourceBean erroriDB = (SourceBean) trans.executeQuery("GET_LISTA_ERRORI_FORZATURA",
						new Object[] { prgMaster }, "SELECT");

				Vector errori = erroriDB.getAttributeAsVector("ROW");

				reportOperation.reportFailure(MessageCodes.ForzaturaMovimenti.ERROR_DEFAULT);
				for (int i = 0; i < errori.size(); i++) {
					SourceBean errore = (SourceBean) errori.get(i);

					BigDecimal codiceErrore = (BigDecimal) errore.getAttribute("NUMESITO");

					int msgCode = 0;
					switch (codiceErrore.intValue()) {
					case 1:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_1;
						break;
					case 2:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_2;
						break;
					case 3:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_3;
						break;
					case 4:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_4;
						break;
					case 5:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_5;
						break;
					case 6:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_6;
						break;
					case 7:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_7;
						break;
					case 8:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_8;
						break;
					case 9:
						msgCode = MessageCodes.ForzaturaMovimenti.MOV_SUCC_TD_TI;
						break;
					case 10:
						msgCode = MessageCodes.ForzaturaMovimenti.MOV_PREC_TI_TD;
						break;
					case 11:
						msgCode = MessageCodes.ForzaturaMovimenti.MOV_SUCC_CES;
						break;
					case 12:
						msgCode = MessageCodes.ForzaturaMovimenti.DATE_ERR;
						break;
					case 13:
						msgCode = MessageCodes.ForzaturaMovimenti.MOV_PREC_AVV;
						break;
					case 14:
						msgCode = MessageCodes.ForzaturaMovimenti.ERR_INS_DOC;
						break;
					case 15:
						msgCode = MessageCodes.ForzaturaMovimenti.ERR_MOD_DOC_AN_PR;
						break;
					case 16:
						msgCode = MessageCodes.ForzaturaMovimenti.ERR_MOV_FORZATO;
						break;
					case 17:
						msgCode = MessageCodes.ForzaturaMovimenti.ERR_AVV_SUCC;
						break;
					case 18:
						msgCode = MessageCodes.ForzaturaMovimenti.ERR_CES_PREC;
						break;
					case 19:
						msgCode = MessageCodes.ForzaturaMovimenti.DTIN_DTFEFF;
						break;
					case 20:
						msgCode = MessageCodes.ForzaturaMovimenti.DTEFF_DTFINE;
						break;
					case 21:
						msgCode = MessageCodes.ForzaturaMovimenti.CATENE_9;
						break;
					default:
						msgCode = MessageCodes.General.OPERATION_FAIL;
					}

					BigDecimal prgMovimento = (BigDecimal) errore.getAttribute("PRGMOVIMENTO");
					String prgMovStr = prgMovimento.toPlainString();
					Vector<String> params = new Vector<String>();
					params.add(prgMovStr);
					reportOperation.reportFailure(msgCode, "VerificaModificheForzaturaMovimenti.service",
							"errore controllo forzatura movimenti", params);
				}
			}
		} catch (Throwable e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();
				}
				_logger.error("Errore: " + e);
			} catch (Exception e1) {
				_logger.error("Errore: " + e1);
			}
		} finally {
			if (trans != null) {
				trans.closeConnTransaction();
			}
		}
	}

}
