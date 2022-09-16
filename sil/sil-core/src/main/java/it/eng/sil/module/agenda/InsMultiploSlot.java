package it.eng.sil.module.agenda;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

public class InsMultiploSlot extends AbstractModule {
	private String className = this.getClass().getName();

	public InsMultiploSlot() {
	}

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String dataParInizio = StringUtils.getAttributeStrNotNull(request, "dataParInizio");
			String dataParFine = StringUtils.getAttributeStrNotNull(request, "dataParFine");
			String prgSpi = StringUtils.getAttributeStrNotNull(request, "PRGSPI");
			String codServizio = StringUtils.getAttributeStrNotNull(request, "CODSERVIZIO");
			String prgAmbiente = StringUtils.getAttributeStrNotNull(request, "PRGAMBIENTE");
			String numAziende = StringUtils.getAttributeStrNotNull(request, "NUMAZIENDE");
			String numLavoratori = StringUtils.getAttributeStrNotNull(request, "NUMLAVORATORI");
			String flgPubblico = StringUtils.getAttributeStrNotNull(request, "flgPubblico");
			String strOraDalle = StringUtils.getAttributeStrNotNull(request, "STRORADALLE");
			String strOraAlle = StringUtils.getAttributeStrNotNull(request, "STRORAALLE");
			String numMinuti = StringUtils.getAttributeStrNotNull(request, "NUMMINUTI");
			String numQta = StringUtils.getAttributeStrNotNull(request, "NUMQTA");
			String codStatoSlot = StringUtils.getAttributeStrNotNull(request, "codStatoSlot");
			String codCpi = StringUtils.getAttributeStrNotNull(request, "CODCPI");
			String cdnParUtente = StringUtils.getAttributeStrNotNull(request, "CDNPARUTENTE");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(17);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("p_dataInizio", Types.VARCHAR, dataParInizio));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_dataFine", Types.VARCHAR, dataParFine));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_prgSpi", Types.VARCHAR, prgSpi));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_codServizio", Types.VARCHAR, codServizio));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_prgAmbiente", Types.VARCHAR, prgAmbiente));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_numAzienda", Types.VARCHAR, numAziende));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_numLavoratori", Types.VARCHAR, numLavoratori));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_flgPubblico", Types.VARCHAR, flgPubblico));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_oraInizio", Types.VARCHAR, strOraDalle));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_oraFine", Types.VARCHAR, strOraAlle));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_numMinuti", Types.VARCHAR, numMinuti));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_numQta", Types.VARCHAR, numQta));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_codStatoSlot", Types.VARCHAR, codStatoSlot));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_codCpi", Types.VARCHAR, codCpi));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("p_cdnUtente", Types.VARCHAR, cdnParUtente));
			command.setAsInputParameters(paramIndex++);
			// parametri di Output
			parameters.add(conn.createDataField("strCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String ErrMesg = df.getStringValue();
			if (ErrMesg == null) {
				ErrMesg = "";
			}

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("strMessOut", ErrMesg);
			response.setAttribute((SourceBean) row);

			ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

}