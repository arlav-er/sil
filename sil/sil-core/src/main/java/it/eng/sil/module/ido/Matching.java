package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SessionContainer;
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

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Sottosistema;

// @author: Stefania Orioli

public class Matching extends AbstractModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2156960858107292083L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Matching.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		DataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		SessionContainer session = this.getRequestContainer().getSessionContainer();
		String encrypterKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			String EM = StringUtils.getAttributeStrNotNull(request, "EM");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList<DataField> parameters = null;
			String query_select = "";
			String query_from = "";
			String query_join = "";
			String query_where = "";
			String incrocio_cm_query_join = "";
			String incrocio_cm_query_where = "";
			String where_territorio = "";
			String codiceRit = "";
			// String ErrCode = "";
			String query_sql = "";
			String prgIncrocio = "";
			String prgRosa = "";
			String p_checkCM = "0";
			// String newPrgRichiestaAz = "";
			boolean matchOk = false;

			// Prelevo i valori dei parametri di Input dalla Request
			String p_prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
			String p_prgAlternativa = StringUtils.getAttributeStrNotNull(request, "PRGALTERNATIVA");
			// String p_prgRosa = StringUtils.getAttributeStrNotNull(request,
			// "PRGROSA");
			String p_prgRosa = (String) request.getAttribute("PRGROSA");
			String p_cdnUtente = StringUtils.getAttributeStrNotNull(request, "P_CDNUTENTE");
			String p_dataValCV = (String) request.getAttribute("dataValCV");
			String p_db = StringUtils.getAttributeStrNotNull(request, "db");

			// Parametri specifici - MATCHING ESATTO
			String p_statoCv = StringUtils.getAttributeStrNotNull(request, "statoCV");
			String p_usaPref = StringUtils.getAttributeStrNotNull(request, "usaPref");
			String p_usaNonInd = StringUtils.getAttributeStrNotNull(request, "usaNonInd");
			String p_flagDI = StringUtils.getAttributeStrNotNull(request, "flagDI");
			String p_flagGG = StringUtils.getAttributeStrNotNull(request, "flagGG");
			String p_codCpi = StringUtils.getAttributeStrNotNull(request, "P_CODCPI");
			String flagNoMansione = StringUtils.getAttributeStrNotNull(request, "flagNoMansione");

			// Parametri specifici - MATCHING PESATO
			String p_numPFasciaEtaPrima = StringUtils.getAttributeStrNotNull(request, "numPFasciaEtaPrima");
			String p_numPFasciaEtaSeconda = StringUtils.getAttributeStrNotNull(request, "numPFasciaEtaSeconda");
			String p_numPStudioGruppo = StringUtils.getAttributeStrNotNull(request, "numPStudioGruppo");
			String p_numPStudioAlias = StringUtils.getAttributeStrNotNull(request, "numPStudioAlias");
			String p_numPMansioneGruppo = StringUtils.getAttributeStrNotNull(request, "numPMansioneGruppo");
			String p_numPMansioneAlias = StringUtils.getAttributeStrNotNull(request, "numPMansioneAlias");
			String p_numPNoEsperienza = StringUtils.getAttributeStrNotNull(request, "numPNoEsperienza");
			String p_numPEsperienzaAlias = StringUtils.getAttributeStrNotNull(request, "numPEsperienzaAlias");
			String p_numPInfoMin = StringUtils.getAttributeStrNotNull(request, "numPInfoMin");
			String p_numPInfoGruppo = StringUtils.getAttributeStrNotNull(request, "numPInfoGruppo");
			String p_numPInfoGruppoMin = StringUtils.getAttributeStrNotNull(request, "numPInfoGruppoMin");
			String p_numPLinguaInf = StringUtils.getAttributeStrNotNull(request, "numPLinguaInf");
			String p_numPSogliaRichiesta = StringUtils.getAttributeStrNotNull(request, "numPSogliaRichiesta");
			String p_flgIncMir = "";
			String p_codMonoCMCategoria = "";
			boolean incrocio_no_mansioni_CM = false;

			// INIT-PARTE-TEMP
			if (Sottosistema.CM.isOff()) {
				// END-PARTE-TEMP

				// INIT-PARTE-TEMP
			} else {
				// END-PARTE-TEMP

				// valorizzo il parametro da passare alle varie stored per
				// codificare del Collocamento Mirato
				p_checkCM = "1";
				// parametro che identifica il tipo di incrocio del collocamento
				// mirato da fare
				p_flgIncMir = StringUtils.getAttributeStrNotNull(request, "flgIncMir");
				p_codMonoCMCategoria = StringUtils.getAttributeStrNotNull(request, "codMonoCMCategoria");

				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			if (EM.equals("1")) // MATCHING ESATTO
			{
				if (flagNoMansione.equals("1")) {
					if (p_flgIncMir.equals("1")) {
						statementSB = (SourceBean) getConfig().getAttribute("QUERY_ESATTO_NO_MANSIONE_CM");
						incrocio_no_mansioni_CM = true;
					} else {
						statementSB = (SourceBean) getConfig().getAttribute("QUERY_ESATTO_NO_MANSIONE");
					}
				} else {
					if (p_flgIncMir.equals("1")) {
						statementSB = (SourceBean) getConfig().getAttribute("QUERY_ESATTO_CM");
					} else {
						statementSB = (SourceBean) getConfig().getAttribute("QUERY_ESATTO");
					}
				}
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				if (incrocio_no_mansioni_CM) {
					parameters = new ArrayList<DataField>(25);
				} else {
					parameters = new ArrayList<DataField>(23);
				}
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// preparazione dei Parametri di Input
				// 2. p_prgRichiestaAz
				parameters.add(conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT,
						new BigInteger(p_prgRichiestaAz)));
				command.setAsInputParameters(paramIndex++);
				// 3. p_prgAlternativa
				parameters.add(conn.createDataField("p_prgAlternativa", java.sql.Types.BIGINT,
						new BigInteger(p_prgAlternativa)));
				command.setAsInputParameters(paramIndex++);
				// 4. p_prgRosa
				parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.VARCHAR, p_prgRosa));
				command.setAsInputParameters(paramIndex++);
				// 5. p_statoCv
				parameters.add(conn.createDataField("p_prgStatoCv", java.sql.Types.VARCHAR, p_statoCv));
				command.setAsInputParameters(paramIndex++);
				// 6. p_usaPref
				parameters.add(conn.createDataField("p_usaPref", java.sql.Types.VARCHAR, p_usaPref));
				command.setAsInputParameters(paramIndex++);
				// 7. p_usaNonInd
				parameters.add(conn.createDataField("p_usaNonInd", java.sql.Types.VARCHAR, p_usaNonInd));
				command.setAsInputParameters(paramIndex++);
				// 8. p_flagDI
				parameters.add(conn.createDataField("p_flagDI", java.sql.Types.VARCHAR, p_flagDI));
				command.setAsInputParameters(paramIndex++);
				// 9. p_flgIncMir
				parameters.add(conn.createDataField("p_flgIncMir", java.sql.Types.VARCHAR, p_flgIncMir));
				command.setAsInputParameters(paramIndex++);
				// 10. p_db
				parameters.add(conn.createDataField("p_db", java.sql.Types.VARCHAR, p_db));
				command.setAsInputParameters(paramIndex++);
				// 11. p_codCpi
				parameters.add(conn.createDataField("p_codCpi", java.sql.Types.VARCHAR, p_codCpi));
				command.setAsInputParameters(paramIndex++);
				// 12. p_cdnUtente
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
				command.setAsInputParameters(paramIndex++);
				// 13. p_dataValCV
				parameters.add(conn.createDataField("p_dataValCV", java.sql.Types.VARCHAR, p_dataValCV));
				command.setAsInputParameters(paramIndex++);
				// 14. checkCM
				parameters.add(conn.createDataField("p_checkCM", java.sql.Types.BIGINT, new BigInteger(p_checkCM)));
				command.setAsInputParameters(paramIndex++);
				// 15. encrypterKey
				parameters.add(conn.createDataField("encrypterKey", java.sql.Types.VARCHAR, encrypterKey));
				command.setAsInputParameters(paramIndex++);
				// p_p_codMonoCMCategoria
				parameters.add(
						conn.createDataField("p_codMonoCMCategoria", java.sql.Types.VARCHAR, p_codMonoCMCategoria));
				command.setAsInputParameters(paramIndex++);
				// p_flagGG
				parameters.add(conn.createDataField("p_flagGG", java.sql.Types.VARCHAR, p_flagGG));
				command.setAsInputParameters(paramIndex++);

				// parametri di Output
				// 16. p_errCode
				parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 17. p_out_query_select
				parameters.add(conn.createDataField("p_out_query_select", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 18. p_out_query_from
				parameters.add(conn.createDataField("p_out_query_from", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 19. p_out_query_join
				parameters.add(conn.createDataField("p_out_query_join", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 20. p_out_query_where
				parameters.add(conn.createDataField("p_out_query_where", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// per l'incriocio CM no mansioni vi sono altri due parametri di
				// out
				if (incrocio_no_mansioni_CM) {
					parameters.add(conn.createDataField("p_out_cm_query_join", java.sql.Types.VARCHAR, null));
					command.setAsOutputParameters(paramIndex++);

					parameters.add(conn.createDataField("p_out_cm_query_where", java.sql.Types.VARCHAR, null));
					command.setAsOutputParameters(paramIndex++);
				}
				// 21 p_out_prgIncrocio
				parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 22. p_out_prgRosa
				parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
			}

			if (EM.equals("2")) // MATCHING PESATO
			{
				if (p_flgIncMir.equals("1")) {
					statementSB = (SourceBean) getConfig().getAttribute("QUERY_PESATO_CM");
				} else {
					statementSB = (SourceBean) getConfig().getAttribute("QUERY_PESATO");
				}
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				parameters = new ArrayList<DataField>(31);
				// 1.Parametro di Ritorno
				parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// preparazione dei Parametri di Input
				// 2. p_prgRichiestaAz
				parameters.add(conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT,
						new BigInteger(p_prgRichiestaAz)));
				command.setAsInputParameters(paramIndex++);
				// 3. p_prgAlternativa
				parameters.add(conn.createDataField("p_prgAlternativa", java.sql.Types.BIGINT,
						new BigInteger(p_prgAlternativa)));
				command.setAsInputParameters(paramIndex++);
				// 4. p_prgRosa
				parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.VARCHAR, p_prgRosa));
				command.setAsInputParameters(paramIndex++);
				// 5. p_db
				parameters.add(conn.createDataField("p_db", java.sql.Types.VARCHAR, p_db));
				command.setAsInputParameters(paramIndex++);
				// 6. p_dataValCV
				parameters.add(conn.createDataField("p_dataValCV", java.sql.Types.VARCHAR, p_dataValCV));
				command.setAsInputParameters(paramIndex++);
				// 7. p_cdnUtente
				parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
				command.setAsInputParameters(paramIndex++);
				// 8. p_numPFasciaEtaPrima
				parameters.add(
						conn.createDataField("p_numPFasciaEtaPrima", java.sql.Types.VARCHAR, p_numPFasciaEtaPrima));
				command.setAsInputParameters(paramIndex++);
				// 9. p_numPFasciaEtaSec
				parameters.add(
						conn.createDataField("p_numPFasciaEtaSec", java.sql.Types.VARCHAR, p_numPFasciaEtaSeconda));
				command.setAsInputParameters(paramIndex++);
				// 10. p_numPStudioGruppo
				parameters.add(conn.createDataField("p_numPStudioGruppo", java.sql.Types.VARCHAR, p_numPStudioGruppo));
				command.setAsInputParameters(paramIndex++);
				// 11. p_numPStudioAlias
				parameters.add(conn.createDataField("p_numPStudioAlias", java.sql.Types.VARCHAR, p_numPStudioAlias));
				command.setAsInputParameters(paramIndex++);
				// 12. p_numPMansioneGruppo
				parameters.add(
						conn.createDataField("p_numPMansioneGruppo", java.sql.Types.VARCHAR, p_numPMansioneGruppo));
				command.setAsInputParameters(paramIndex++);
				// 13. p_numPMansioneAlias
				parameters
						.add(conn.createDataField("p_numPMansioneAlias", java.sql.Types.VARCHAR, p_numPMansioneAlias));
				command.setAsInputParameters(paramIndex++);
				// 14. p_numPNoEsperienza
				parameters.add(conn.createDataField("p_numPNoEsperienza", java.sql.Types.VARCHAR, p_numPNoEsperienza));
				command.setAsInputParameters(paramIndex++);
				// 15. p_numPEsperienzaAlias
				parameters.add(
						conn.createDataField("p_numPEsperienzaAlias", java.sql.Types.VARCHAR, p_numPEsperienzaAlias));
				command.setAsInputParameters(paramIndex++);
				// 16. p_numPInfoMin
				parameters.add(conn.createDataField("p_numPInfoMin", java.sql.Types.VARCHAR, p_numPInfoMin));
				command.setAsInputParameters(paramIndex++);
				// 17. p_numPInfoGruppo
				parameters.add(conn.createDataField("p_numPInfoGruppo", java.sql.Types.VARCHAR, p_numPInfoGruppo));
				command.setAsInputParameters(paramIndex++);
				// 18. p_numPInfoGruppoMin
				parameters
						.add(conn.createDataField("p_numPInfoGruppoMin", java.sql.Types.VARCHAR, p_numPInfoGruppoMin));
				command.setAsInputParameters(paramIndex++);
				// 19. p_numPLinguaInf
				parameters.add(conn.createDataField("p_numPLinguaInf", java.sql.Types.VARCHAR, p_numPLinguaInf));
				command.setAsInputParameters(paramIndex++);
				// 20. p_numPSogliaRichiesta
				parameters.add(
						conn.createDataField("p_numPSogliaRichiesta", java.sql.Types.VARCHAR, p_numPSogliaRichiesta));
				command.setAsInputParameters(paramIndex++);
				// 21. p_flgIncMir
				parameters.add(conn.createDataField("p_flgIncMir", java.sql.Types.VARCHAR, p_flgIncMir));
				command.setAsInputParameters(paramIndex++);
				// 22. p_checkCM
				parameters.add(conn.createDataField("p_checkCM", java.sql.Types.BIGINT, new BigInteger(p_checkCM)));
				command.setAsInputParameters(paramIndex++);
				// p_p_codMonoCMCategoria
				parameters.add(
						conn.createDataField("p_codMonoCMCategoria", java.sql.Types.VARCHAR, p_codMonoCMCategoria));
				command.setAsInputParameters(paramIndex++);

				// parametri di Output
				// 23. p_errCode
				parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 24. p_out_query_select
				parameters.add(conn.createDataField("p_out_query_select", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 25. p_out_query_from
				parameters.add(conn.createDataField("p_out_query_from", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 26. p_out_query_join
				parameters.add(conn.createDataField("p_out_query_join", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 27. p_out_query_where
				parameters.add(conn.createDataField("p_out_query_where", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 28. p_out_prgIncrocio
				parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 29. p_out_prgRosa
				parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
				// 30. p_out_where_territorio
				parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
				command.setAsOutputParameters(paramIndex++);
			}

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List<PunctualDataResult> outputParams = cdr.getContainedDataResult();
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
			// ErrCode = df.getStringValue();
			// 2. p_out_query_select
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			query_select = (String) df.getStringValue();
			if (query_select == null) {
				query_select = "";
			}
			// 3. p_out_query_from
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			query_from = (String) df.getStringValue();
			if (query_from == null) {
				query_from = "";
			}
			// 4. p_out_query_join
			pdr = (PunctualDataResult) outputParams.get(4);
			df = pdr.getPunctualDatafield();
			query_join = (String) df.getStringValue();
			if (query_join == null) {
				query_join = "";
			}
			// 5. p_out_query_where
			pdr = (PunctualDataResult) outputParams.get(5);
			df = pdr.getPunctualDatafield();
			query_where = (String) df.getStringValue();
			if (query_where == null) {
				query_where = "";
			}

			// incrocio no mansioni CM
			if (incrocio_no_mansioni_CM) {
				// 6. p_out_cm_query_join
				pdr = (PunctualDataResult) outputParams.get(6);
				df = pdr.getPunctualDatafield();
				incrocio_cm_query_join = (String) df.getStringValue();
				if (incrocio_cm_query_join == null) {
					incrocio_cm_query_join = "";
				}
				// 7. p_out_cm_query_where
				pdr = (PunctualDataResult) outputParams.get(7);
				df = pdr.getPunctualDatafield();
				incrocio_cm_query_where = (String) df.getStringValue();
				if (incrocio_cm_query_where == null) {
					incrocio_cm_query_where = "";
				}

				// 8. p_out_prgIncrocio
				pdr = (PunctualDataResult) outputParams.get(8);
				df = pdr.getPunctualDatafield();
				prgIncrocio = (String) df.getStringValue();
				if (prgIncrocio == null) {
					prgIncrocio = "";
				}
				// 9. p_out_prgRosa
				pdr = (PunctualDataResult) outputParams.get(9);
				df = pdr.getPunctualDatafield();
				prgRosa = (String) df.getStringValue();
				if (prgRosa == null) {
					prgRosa = "";
				}
			} else {
				// 6. p_out_prgIncrocio
				pdr = (PunctualDataResult) outputParams.get(6);
				df = pdr.getPunctualDatafield();
				prgIncrocio = (String) df.getStringValue();
				if (prgIncrocio == null) {
					prgIncrocio = "";
				}
				// 7. p_out_prgRosa
				pdr = (PunctualDataResult) outputParams.get(7);
				df = pdr.getPunctualDatafield();
				prgRosa = (String) df.getStringValue();
				if (prgRosa == null) {
					prgRosa = "";
				}
			}

			if (EM.equals("2")) // MATCHING PESATO
			{
				// 8. p_out_where_territorio
				pdr = (PunctualDataResult) outputParams.get(8);
				df = pdr.getPunctualDatafield();
				where_territorio = (String) df.getStringValue();
				if (where_territorio == null) {
					where_territorio = "";
				}
				LogUtils.logDebug(this.className, "WHERE_TERRITORIO: " + where_territorio, this);
			}
			// 8. p_out_prgRichiestaAz
			// pdr = ( PunctualDataResult ) outputParams.get(8);
			// df = pdr.getPunctualDatafield();
			// String newPrgRichiestaAz = (String) df.getStringValue();
			// if(newPrgRichiestaAz == null) { newPrgRichiestaAz =
			// p_prgRichiestaAz; }
			query_sql = query_select + "\r\n" + query_from + "\r\n" + query_join + "\r\n" + query_where;
			LogUtils.logDebug(this.className, "Query SQL:\r\n " + query_sql, this);

			if (codiceRit.equals("0")) {
				matchOk = true;
			}
			if (query_select.equals("") || query_from.equals("") || query_join.equals("") || query_where.equals("")) {
				matchOk = false;
			}
			if (query_select == null || query_from == null || query_join == null || query_where == null) {
				matchOk = false;
			}
			command = null;

			dr2 = null;
			CompositeDataResult cdr2 = null;
			PunctualDataResult pdr2 = null;
			String codiceRit2 = "";
			String ErrCode2 = "";
			parameters = null;
			outputParams = null;
			paramIndex = 0;

			if (matchOk) // Procedo con l'esecuzione del Matching
			{
				// Chiamo la seconda funzione
				if (EM.equals("1")) {// MATCHING ESATTO

					if (incrocio_no_mansioni_CM) {
						statementSB = (SourceBean) getConfig().getAttribute("QUERY_EXEC_CM_NO_MANSIONE");
					} else {
						if (p_flgIncMir.equals("1")) {
							statementSB = (SourceBean) getConfig().getAttribute("QUERY_EXEC_CM");
						} else {
							statementSB = (SourceBean) getConfig().getAttribute("QUERY_EXEC");
						}
					}
					statement = statementSB.getAttribute("STATEMENT").toString();
					sqlStr = SQLStatements.getStatement(statement);
					command2 = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

					// parameters = new ArrayList(11);
					if (incrocio_no_mansioni_CM) {
						parameters = new ArrayList<DataField>(14);
					} else {
						parameters = new ArrayList<DataField>(12);
					}
					// Parametro di Ritorno
					parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
					command2.setAsOutputParameters(paramIndex++);
					// preparazione dei Parametri di Input
					parameters.add(conn.createDataField("p_query_select", java.sql.Types.VARCHAR, query_select));
					command2.setAsInputParameters(paramIndex++);
					parameters.add(conn.createDataField("p_query_from", java.sql.Types.VARCHAR, query_from));
					command2.setAsInputParameters(paramIndex++);
					parameters.add(conn.createDataField("p_query_join", java.sql.Types.VARCHAR, query_join));
					command2.setAsInputParameters(paramIndex++);
					parameters.add(conn.createDataField("p_query_where", java.sql.Types.VARCHAR, query_where));
					command2.setAsInputParameters(paramIndex++);
					if (incrocio_no_mansioni_CM) {
						parameters.add(conn.createDataField("p_cm_query_join", java.sql.Types.VARCHAR,
								incrocio_cm_query_join));
						command2.setAsInputParameters(paramIndex++);
						parameters.add(conn.createDataField("p_cm_query_where", java.sql.Types.VARCHAR,
								incrocio_cm_query_where));
						command2.setAsInputParameters(paramIndex++);
					}
					parameters.add(conn.createDataField("p_prgIncrocio", java.sql.Types.VARCHAR, prgIncrocio));
					command2.setAsInputParameters(paramIndex++);
					parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.VARCHAR, prgRosa));
					command2.setAsInputParameters(paramIndex++);
					parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
					command2.setAsInputParameters(paramIndex++);
					// N.B. parametro dummy per l'esecuzione in locale
					parameters.add(conn.createDataField("p_mem", java.sql.Types.VARCHAR, "1"));
					command2.setAsInputParameters(paramIndex++);

					parameters.add(conn.createDataField("encrypterKey", java.sql.Types.VARCHAR, encrypterKey));
					command2.setAsInputParameters(paramIndex++);

					parameters.add(
							conn.createDataField("p_codMonoCMCategoria", java.sql.Types.VARCHAR, p_codMonoCMCategoria));
					command2.setAsInputParameters(paramIndex++);
					// parametri di Output
					parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
					command2.setAsOutputParameters(paramIndex++);
					// commentato per la consegna del 4 Aprile
					// parameters.add(conn.createDataField("p_cursor",????????,
					// null));
					// command2.setAsOutputParameters(paramIndex++);
				}
				if (EM.equals("2")) {// MATCHING PESATO
					if (p_flgIncMir.equals("1")) {
						statementSB = (SourceBean) getConfig().getAttribute("QUERY_EXEC_PESATO_CM");
					} else {
						statementSB = (SourceBean) getConfig().getAttribute("QUERY_EXEC_PESATO");
					}
					statement = statementSB.getAttribute("STATEMENT").toString();
					sqlStr = SQLStatements.getStatement(statement);
					command2 = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

					parameters = new ArrayList<DataField>(30);
					// parameters = new ArrayList(25);
					// 1. Parametro di Ritorno
					parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
					command2.setAsOutputParameters(paramIndex++);
					// preparazione dei Parametri di Input
					// 2. p_in_query_select
					parameters.add(conn.createDataField("p_in_query_select", java.sql.Types.VARCHAR, query_select));
					command2.setAsInputParameters(paramIndex++);
					// 3. p_in_query_from
					parameters.add(conn.createDataField("p_in_query_from", java.sql.Types.VARCHAR, query_from));
					command2.setAsInputParameters(paramIndex++);
					// 4. p_in_query_join
					parameters.add(conn.createDataField("p_query_join", java.sql.Types.VARCHAR, query_join));
					command2.setAsInputParameters(paramIndex++);
					// 5. p_in_query_where
					parameters.add(conn.createDataField("p_query_where", java.sql.Types.VARCHAR, query_where));
					command2.setAsInputParameters(paramIndex++);
					// 5b. p_in_where_territorio
					parameters.add(
							conn.createDataField("p_in_where_territorio", java.sql.Types.VARCHAR, where_territorio));
					command2.setAsInputParameters(paramIndex++);
					// 6. p_prgRichiestaAz
					parameters.add(conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT,
							new BigInteger(p_prgRichiestaAz)));
					command2.setAsInputParameters(paramIndex++);
					// 7. p_prgAlternativa
					parameters.add(conn.createDataField("p_prgAlternativa", java.sql.Types.BIGINT,
							new BigInteger(p_prgAlternativa)));
					command2.setAsInputParameters(paramIndex++);
					// 8. p_in_prgIncrocio
					parameters.add(conn.createDataField("p_in_prgIncrocio", java.sql.Types.VARCHAR, prgIncrocio));
					command2.setAsInputParameters(paramIndex++);
					// 9. p_in_prgRosa
					parameters.add(conn.createDataField("p_in_prgRosa", java.sql.Types.VARCHAR, prgRosa));
					command2.setAsInputParameters(paramIndex++);
					// 10. p_cdnUtente
					parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, p_cdnUtente));
					command2.setAsInputParameters(paramIndex++);
					// 11. N.B. parametro dummy per l'esecuzione in locale
					parameters.add(conn.createDataField("p_mem", java.sql.Types.VARCHAR, "1"));
					command2.setAsInputParameters(paramIndex++);
					// 12. p_numPFasciaEtaPrima
					parameters.add(
							conn.createDataField("p_numPFasciaEtaPrima", java.sql.Types.VARCHAR, p_numPFasciaEtaPrima));
					command2.setAsInputParameters(paramIndex++);
					// 13. p_numPFasciaEtaSec
					parameters.add(
							conn.createDataField("p_numPFasciaEtaSec", java.sql.Types.VARCHAR, p_numPFasciaEtaSeconda));
					command2.setAsInputParameters(paramIndex++);
					// 14. p_numPStudioGruppo
					parameters.add(
							conn.createDataField("p_numPStudioGruppo", java.sql.Types.VARCHAR, p_numPStudioGruppo));
					command2.setAsInputParameters(paramIndex++);
					// 15. p_numPStudioAlias
					parameters
							.add(conn.createDataField("p_numPStudioAlias", java.sql.Types.VARCHAR, p_numPStudioAlias));
					command2.setAsInputParameters(paramIndex++);
					// 16. p_numPMansioneGruppo
					parameters.add(
							conn.createDataField("p_numPMansioneGruppo", java.sql.Types.VARCHAR, p_numPMansioneGruppo));
					command2.setAsInputParameters(paramIndex++);
					// 17. p_numPMansioneAlias
					parameters.add(
							conn.createDataField("p_numPMansioneAlias", java.sql.Types.VARCHAR, p_numPMansioneAlias));
					command2.setAsInputParameters(paramIndex++);
					// 18. p_numPNoEsperienza
					parameters.add(
							conn.createDataField("p_numPNoEsperienza", java.sql.Types.VARCHAR, p_numPNoEsperienza));
					command2.setAsInputParameters(paramIndex++);
					// 19. p_numPEsperienzaAlias
					parameters.add(conn.createDataField("p_numPEsperienzaAlias", java.sql.Types.VARCHAR,
							p_numPEsperienzaAlias));
					command2.setAsInputParameters(paramIndex++);
					// 20. p_numPInfoMin
					parameters.add(conn.createDataField("p_numPInfoMin", java.sql.Types.VARCHAR, p_numPInfoMin));
					command2.setAsInputParameters(paramIndex++);
					// 21. p_numPInfoGruppo
					parameters.add(conn.createDataField("p_numPInfoGruppo", java.sql.Types.VARCHAR, p_numPInfoGruppo));
					command2.setAsInputParameters(paramIndex++);
					// 22. p_numPInfoGruppoMin
					parameters.add(
							conn.createDataField("p_numPInfoGruppoMin", java.sql.Types.VARCHAR, p_numPInfoGruppoMin));
					command2.setAsInputParameters(paramIndex++);
					// 23. p_numPLinguaInf
					parameters.add(conn.createDataField("p_numPLinguaInf", java.sql.Types.VARCHAR, p_numPLinguaInf));
					command2.setAsInputParameters(paramIndex++);
					// 24. p_numPSogliaRichiesta
					parameters.add(conn.createDataField("p_numPSogliaRichiesta", java.sql.Types.VARCHAR,
							p_numPSogliaRichiesta));
					command2.setAsInputParameters(paramIndex++);
					// 25. p_flgIncMir
					parameters.add(conn.createDataField("p_flgIncMir", java.sql.Types.VARCHAR, p_flgIncMir));
					command2.setAsInputParameters(paramIndex++);
					// 26. p_checkCM
					parameters.add(conn.createDataField("p_checkCM", java.sql.Types.BIGINT, new BigInteger(p_checkCM)));
					command2.setAsInputParameters(paramIndex++);

					parameters.add(conn.createDataField("encrypterKey", java.sql.Types.VARCHAR, encrypterKey));
					command2.setAsInputParameters(paramIndex++);

					parameters.add(
							conn.createDataField("p_codMonoCMCategoria", java.sql.Types.VARCHAR, p_codMonoCMCategoria));
					command2.setAsInputParameters(paramIndex++);
					// parametri di Output
					// 27. p_errCode
					parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
					command2.setAsOutputParameters(paramIndex++);
					// 26. commentato per la mancanza della cooperazione
					// applicativa
					// parameters.add(conn.createDataField("p_cursor",????????,
					// null));
					// command2.setAsOutputParameters(paramIndex++);
				}
				// Chiamata alla Stored Procedure
				dr2 = command2.execute(parameters);
				cdr2 = (CompositeDataResult) dr2.getDataObject();
				outputParams = cdr2.getContainedDataResult();
				// Reperisco i valori di output della stored
				// 0. Codice di Ritorno
				pdr2 = (PunctualDataResult) outputParams.get(0);
				df = pdr2.getPunctualDatafield();
				codiceRit2 = (String) df.getObjectValue();
				if (codiceRit2 == null) {
					codiceRit2 = "-1";
				} else {
					codiceRit2 = codiceRit2.trim();
				}
				// 1. ErrCodeOut
				pdr2 = (PunctualDataResult) outputParams.get(1);
				df = pdr2.getPunctualDatafield();
				ErrCode2 = (String) df.getObjectValue();
				if (ErrCode2 == null) {
					ErrCode2 = "";
				} else {
					ErrCode2 = ErrCode2.trim();
				}

				if (codiceRit2.equals("0")) {
					matchOk = true;
				}
				if (codiceRit2.equals("1")) {
					matchOk = false;
				}
			}
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit1", codiceRit);
			row.setAttribute("CodiceRit2", codiceRit2);
			if (codiceRit2.equals("")) {
				row.setAttribute("CodiceRit", codiceRit);
			} else {
				row.setAttribute("CodiceRit", codiceRit2);
			}
			if (matchOk) {
				if (EM.equals("1")) {
					response.setAttribute("MATCH_OK", "1");
				}
				if (EM.equals("2")) {
					response.setAttribute("MATCH_OK", "2");
				}
				response.setAttribute("MATCH_PRGROSA", prgRosa);
				response.setAttribute("MATCH_PRGINCROCIO", prgIncrocio);
				response.setAttribute((SourceBean) row);
			} else {
				response.setAttribute("MATCH_OK", "0");
				response.setAttribute((SourceBean) row);
			}
			ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(null, command2, dr2);
			Utils.releaseResources(conn, command, dr);
		}
	}
}