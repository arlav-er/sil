package it.eng.sil.module.ido;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

/**
 * 
 * Inserimento del lavoratore in do_nominativo per ciascna rosa selezionata
 * 
 * @author coticone
 * 
 */
public class ASAssociaRichiesteAdesione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ASAssociaRichiesteAdesione.class
			.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		ReportOperationResult ror = new ReportOperationResult(this, response);

		String allPrgRosaRequest = StringUtils.getAttributeStrNotNull(request, "CKBOX_ADESIONE");

		Vector prgRosaVector = StringUtils.split(allPrgRosaRequest, "@");

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		boolean success = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// ciclo su tutti i prgRosa che sono stati checkati
			for (int i = 0; i < prgRosaVector.size(); i++) {
				success = insertAdesioneAS(transExec, request, response, (String) prgRosaVector.get(i));

				if (!success) {
					break;
				}
			}

			if (success) {
				ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				transExec.commitTransaction();
				ror.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ASAssociaRichiesteAdesione::service(): Impossibile inserire l'adesione!", ex);

			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

	public boolean insertAdesioneAS(TransactionQueryExecutor txExecutor, SourceBean request, SourceBean response,
			String p_prgRosaIesima) {

		boolean checkSuccess = false;
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			conn = txExecutor.getDataConnection();
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			String errCode = "";
			String prgIncrocio = "";
			String prgRosa = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			statementSB = (SourceBean) getConfig().getAttribute("AS_INSERT_ADESIONE");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String p_prgRosa = p_prgRosaIesima;
			String p_cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
			String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");
			String p_cdnGruppo = StringUtils.getAttributeStrNotNull(request, "P_CDNGRUPPO");
			String p_prgSpiContatto = StringUtils.getAttributeStrNotNull(request, "prgSpi");

			String p_codMonoDid = null;
			String p_codMonoISEE = null;
			String p_numcaricofam = null;
			String p_flgprofessionalita = null;
			String p_flgfamdec = null;
			BigInteger p_PersoneACarico = null;
			
			//TODO aggiungeri parametri per VDA
			//prima leggo la configurazione ASATTRIB se vale 1 allora leggo i valori altrimenti no
			SourceBean beanConfigRows = (SourceBean) QueryExecutor.executeQuery("AS_CONFIG_PUNTEGGI",null,"SELECT","SIL_DATI");
			String configValue = (String) beanConfigRows.getAttribute("ROW.STRVALORECONFIG");	
			BigDecimal numConfigValue = (BigDecimal) beanConfigRows.getAttribute("ROW.NUMVALORECONFIG");	
			if(configValue.equalsIgnoreCase("1") || (numConfigValue!=null && numConfigValue.intValue() == 1)){
				p_codMonoDid =(String) request.getAttribute("codMonoDid"); 
				p_codMonoISEE =(String) request.getAttribute("codMonoIsee");
			}
			//VDA
			if( numConfigValue!=null && numConfigValue.intValue() == 2 ){
			//	p_codMonoDid =(String) request.getAttribute("codMonoDid"); 
				p_codMonoISEE =(String) request.getAttribute("codMonoIsee");
				p_numcaricofam =(String) request.getAttribute("numCaricoFam");
				if (p_numcaricofam != null && !p_numcaricofam.equals("")){
					p_PersoneACarico = new BigInteger(p_numcaricofam);
				}
				p_flgprofessionalita =(String) request.getAttribute("flgProfessionalita");
				p_flgfamdec =(String) request.getAttribute("flgFamDec");
			}
			//CALABRIA
			if( numConfigValue!=null && numConfigValue.intValue() == 3 ){
				p_codMonoISEE =(String) request.getAttribute("codMonoIsee");
				p_numcaricofam =(String) request.getAttribute("numCaricoFam");
				if (p_numcaricofam != null && !p_numcaricofam.equals("")){
					p_PersoneACarico = new BigInteger(p_numcaricofam);
				}
			}

			parameters = new ArrayList(12);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgRosa
			parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.BIGINT, new BigInteger(p_prgRosa)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_cdnLavoratore
			parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.BIGINT, new BigInteger(
					p_cdnLavoratore)));
			command.setAsInputParameters(paramIndex++);
			// 4. p_prgSpiContatto
			parameters.add(conn.createDataField("p_prgSpiContatto", java.sql.Types.BIGINT, new BigInteger(
					p_prgSpiContatto)));
			command.setAsInputParameters(paramIndex++);
			// 5. p_cdnUtente
			parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
			command.setAsInputParameters(paramIndex++);
			// 6. p_cdnGruppo
			parameters.add(conn.createDataField("p_cdnGruppo", java.sql.Types.BIGINT, new BigInteger(p_cdnGruppo)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			// 7. p_errCode
			parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			
			// altri Parametri di Input
			// 8. p_codMonoDid
			parameters.add(conn.createDataField("p_codMonoDid", java.sql.Types.VARCHAR, p_codMonoDid));
			command.setAsInputParameters(paramIndex++);
			// 9. p_codMonoIsee
			parameters.add(conn.createDataField("p_codMonoIsee", java.sql.Types.VARCHAR, p_codMonoISEE));
			command.setAsInputParameters(paramIndex++);
			
			// 10. p_numcaricofam
			parameters.add(conn.createDataField("p_numcaricofam", java.sql.Types.BIGINT, p_PersoneACarico));
			command.setAsInputParameters(paramIndex++);
			
			// 11. p_flgprofessionalita
			parameters.add(conn.createDataField("p_flgprofessionalita", java.sql.Types.VARCHAR, p_flgprofessionalita));
			command.setAsInputParameters(paramIndex++);
			
			// 12. p_flgfamdec
			parameters.add(conn.createDataField("p_flgfamdec", java.sql.Types.VARCHAR, p_flgfamdec));
			command.setAsInputParameters(paramIndex++);
			
			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();
			// 1. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			errCode = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				int msgCode = 0;//
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case 1: // XXX valutare errore da inserire
					msgCode = MessageCodes.IDO.ERR_INS_LAV_RNG;
					msg = "Inserimento Adesione: errore insert do_nominativo";
					
					break;
				case 2: // XXX ci deve essere almeno un record dello stato occ
						// da storicizzare
					msgCode = MessageCodes.IDO.ERR_INS_STORIA_STATO_OCC;
					msg = "Inserimento Adesione: errore insert as_storia_stato_occ";
					
					break;
				case 3: // XXX da inserire il msg errore!!!!!!
					msgCode = MessageCodes.IDO.ERR_INS_ADESIONE_MORE_PROFILO;
					msg = "Inserimento Adesione: errore non è possibile aderire a più profili per lo stesso tipo";
					
					break;
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserimento Adesion: sqlCode=" + errCode;
					
					break;
				case -2: // // errore data pubblicazione e/o data chiamata
					msgCode = MessageCodes.IDO.ERR_DATAPUBBLICAZIONE_DATACHIAMATA;
					msg = "La data inizio pubblicazione dell'asta deve essere precedente o uguale alla data chiamata.";
					
					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "Inserimento Adesion: errore di ritorno non ammesso. SqlCode=" + errCode;
					

				}
				response.setAttribute("error", "true");
				ror.reportFailure(msgCode);
				_logger.debug(msg);

			} else {
				checkSuccess = true;
			}
			response.setAttribute((SourceBean) row);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
		} finally {
			// Utils.releaseResources(null, command2, dr2);
			// Utils.releaseResources(conn, command, dr);
		}

		return checkSuccess;
	}
}