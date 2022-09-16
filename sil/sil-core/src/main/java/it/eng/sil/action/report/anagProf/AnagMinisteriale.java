package it.eng.sil.action.report.anagProf;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.patto.CheckRiaperturaDid;

public class AnagMinisteriale extends AbstractSimpleReport {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckRiaperturaDid.class.getName());

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("AnagraficaProfessionaleMinisteriale." + tipoFile);
				else
					setStrNomeDoc("AnagraficaProfessionaleMinisteriale.pdf");

				setStrDescrizione("Scheda anagrafica professionale ministeriale");
				setReportPath("AnagProf/AnagProfessionaleMinisteriale_CC.rpt");

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("pCodLav", request.getAttribute("cdnLavoratore"));
				prompts.put("cdnLavoratoreEncrypt", request.getAttribute("cdnLavoratoreEncrypt"));
				prompts.put("categoria181", getCategoria181(request, response));
				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);

				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);

			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "AnagMinisteriale.service()", "");
			}
		} // else
	}

	private String getCategoria181(SourceBean request, SourceBean response) {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		ReportOperationResult result = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY_CAT181");
			String statement = (String) statementSB.getAttribute("STATEMENT");
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("cdnLavoratore", Types.INTEGER, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("dataParCalcolo", Types.DATE,
					DateUtils.convertStringToDate(DateUtils.getNow())));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			return (codiceRit == null ? "" : codiceRit);

		} catch (Exception e) {
			result.reportSuccess(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.fatal(_logger, "AnagMinisteriale:getCategoria181", e);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

		return "";
	}
}
