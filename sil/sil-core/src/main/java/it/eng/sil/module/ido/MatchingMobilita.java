package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

public class MatchingMobilita extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MatchingMobilita.class.getName());

	public MatchingMobilita() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			String EM = StringUtils.getAttributeStrNotNull(request, "EM");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			String query_select = "";
			String query_from = "";
			String query_join = "";
			String query_where = "";
			// String flgXCpi = "";
			String where_territorio = "";
			String codiceRit = "";
			String ErrCode = "";
			String query_sql = "";
			String prgIncrocio = "";
			String prgRosa = "";
			String p_checkCM = "0";
			// String newPrgRichiestaAz = "";
			boolean matchOk = false;

			String p_prgAlternativa = "";
			String flagNoMansione = "";
			String flgXCpi = "0";

			String paramPrgAlternativa = StringUtils.getAttributeStrNotNull(request, "PRGALTERNATIVA");
			Vector alternativeVector = StringUtils.split(paramPrgAlternativa, "-");
			if (alternativeVector.size() > 0) {
				// recupero prgAlternativa
				p_prgAlternativa = (String) alternativeVector.get(0);
				// recupero il tipo di incrocio se per Provincia o per CPI
				flgXCpi = (String) alternativeVector.get(1);
			}

			// Prelevo i valori dei parametri di Input dalla Request
			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			// String p_prgAlternativa = StringUtils.getAttributeStrNotNull(request, "PRGALTERNATIVA");
			// String p_prgRosa = StringUtils.getAttributeStrNotNull(request,
			// "PRGROSA");
			String p_prgRosa = (String) request.getAttribute("PRGROSA");
			String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");
			String p_dataValCV = (String) request.getAttribute("dataValCV");
			String p_db = StringUtils.getAttributeStrNotNull(request, "db");

			String p_codCpi = StringUtils.getAttributeStrNotNull(request, "P_CODCPI");
			String paramFlagNoMansione = StringUtils.getAttributeStrNotNull(request, "flagNoMansione");
			Vector flagNoMansioneVector = StringUtils.split(paramFlagNoMansione, "-");
			if (flagNoMansioneVector.size() > 0) {
				// recupero prgAlternativa
				flagNoMansione = (String) flagNoMansioneVector.get(0);
				// recupero il tipo di incrocio se per Provincia o per CPI
				flgXCpi = (String) flagNoMansioneVector.get(1);
			}

			String paramFlagNoMansioneXCpi = StringUtils.getAttributeStrNotNull(request, "flagNoMansioneXCpi");
			Vector flagNoMansioneVectorXCpi = StringUtils.split(paramFlagNoMansioneXCpi, "-");
			if (flagNoMansioneVectorXCpi.size() > 0) {
				// recupero prgAlternativa
				flagNoMansione = (String) flagNoMansioneVectorXCpi.get(0);
				// recupero il tipo di incrocio se per Provincia o per CPI
				flgXCpi = (String) flagNoMansioneVectorXCpi.get(1);
			}

			// flgXCpi = StringUtils.getAttributeStrNotNull(request, "flagxCPI");
			// if (("1").compareToIgnoreCase(flgXCpi) != 0)
			// flgXCpi = "0";

			if (EM.equals("9")) { // MOBILITA'
				if (flagNoMansione.equals("1")) {
					statementSB = (SourceBean) getConfig().getAttribute("QUERY_MOBILITA_NO_MANSIONE");
					parameters = new ArrayList(11);
				} else {
					statementSB = (SourceBean) getConfig().getAttribute("QUERY_MOBILITA");
					parameters = new ArrayList(12);
				}
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// preparazione dei Parametri di Input
				// 2. p_prgRichiestaAz
				parameters.add(conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT,
						new BigInteger(p_prgRichiestaAz)));
				command.setAsInputParameters(paramIndex++);
				if (!flagNoMansione.equals("1")) {
					// 3. p_prgAlternativa
					parameters.add(conn.createDataField("p_prgAlternativa", java.sql.Types.BIGINT,
							new BigInteger(p_prgAlternativa)));
					command.setAsInputParameters(paramIndex++);
				}
				// 4. p_prgRosa
				parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.VARCHAR,
						StringUtils.getStringValueNotNull(p_prgRosa)));
				command.setAsInputParameters(paramIndex++);
				// 5. p_db
				parameters.add(conn.createDataField("p_db", java.sql.Types.VARCHAR, p_db));
				command.setAsInputParameters(paramIndex++);
				// 6. p_codCpi
				parameters.add(conn.createDataField("p_codCpi", java.sql.Types.VARCHAR, p_codCpi));
				command.setAsInputParameters(paramIndex++);
				// 7. p_cdnUtente
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
				command.setAsInputParameters(paramIndex++);

				// 8. Incrocio per CPI di riferimento
				parameters.add(conn.createDataField("p_flgxcpi", java.sql.Types.VARCHAR, flgXCpi));
				command.setAsInputParameters(paramIndex++);

				// parametri di Output
				// 8. p_errCode
				parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 9. p_out_query_select
				parameters.add(conn.createDataField("p_out_query", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 10 p_out_prgIncrocio
				parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 11. p_out_prgRosa
				parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);

			}

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			/*
			 * codiceRit = (String) df.getObjectValue(); if(codiceRit==null) { codiceRit = "-1"; } else { codiceRit =
			 * codiceRit.trim(); }
			 */
			codiceRit = df.getStringValue();
			// 1. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			/*
			 * ErrCode = (String) df.getObjectValue(); if(ErrCode==null) { ErrCode = ""; } else { ErrCode =
			 * ErrCode.trim(); }
			 */
			ErrCode = df.getStringValue();
			// 2. p_out_query
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			query_select = (String) df.getStringValue();
			if (query_select == null) {
				query_select = "";
			}
			// 3. p_out_prgIncrocio
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			prgIncrocio = (String) df.getStringValue();
			if (prgIncrocio == null) {
				prgIncrocio = "";
			}
			// 4. p_out_prgRosa
			pdr = (PunctualDataResult) outputParams.get(4);
			df = pdr.getPunctualDatafield();
			prgRosa = (String) df.getStringValue();
			if (prgRosa == null) {
				prgRosa = "";
			}

			// response.setAttribute(cdr.getSourceBean());
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			if (("0").equals(codiceRit)) {
				response.setAttribute("MATCH_PRGROSA", prgRosa);
				response.setAttribute("MATCH_PRGINCROCIO", prgIncrocio);
				response.setAttribute((SourceBean) row);

			} else {
				response.setAttribute("MATCH_OK", "0");
				response.setAttribute((SourceBean) row);
			}

			// TracerSingleton.log("Risultato S.P. MATCHING",
			// TracerSingleton.DEBUG, this.className, row);

			ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}
}