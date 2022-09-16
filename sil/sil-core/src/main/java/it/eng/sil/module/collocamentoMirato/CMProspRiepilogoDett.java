package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;

public class CMProspRiepilogoDett extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMProspRiepilogoDett.class.getName());

	private String className = this.getClass().getName();

	/**
	 * Dopo aver chiamato il metodo per ricalcolare i dati esegue la select
	 */
	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult ror = new ReportOperationResult(this, response);
		boolean checkCalcolo = false;
		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		String messaggiVerifica = "";
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			checkCalcolo = getRicalcoloRiepilogo(request, response);

			if (checkCalcolo) {

				// eseguo la select per recuperare i dati
				this.setSectionQuerySelect("SELECT_RIEPILOGO");
				SourceBean prospetto = doSelect(request, response);
				transExec.commitTransaction();

				String codMonoStatoProspetto = (String) prospetto.getAttribute("ROW.codMonoStatoProspetto");

				if (codMonoStatoProspetto != null
						&& codMonoStatoProspetto.equalsIgnoreCase(ProspettiConstant.STATO_IN_CORSO_ANNO)) {

					BigDecimal numLavComputabili = (BigDecimal) prospetto.getAttribute("ROW.NUMLAVCOMPUTABILI");
					BigDecimal numTotaleDisEsclusioni = (BigDecimal) prospetto
							.getAttribute("ROW.NUMTOTALEDISESCLUSIONI");

					if (numLavComputabili != null && numTotaleDisEsclusioni != null
							&& numLavComputabili.compareTo(numTotaleDisEsclusioni) < 0) {
						if (messaggiVerifica.equals("")) {
							messaggiVerifica = ProspettiConstant.WARNING_DISABILI_IN_FORZA;
						} else {
							messaggiVerifica = messaggiVerifica + "<br>" + ProspettiConstant.WARNING_DISABILI_IN_FORZA;
						}
					}

					BigDecimal numLavComputabiliCentr = (BigDecimal) prospetto
							.getAttribute("ROW.NUMLAVCOMPUTABILICENTR");
					BigDecimal numCentrNonVedenti = (BigDecimal) prospetto.getAttribute("ROW.NUMCENTRNONVEDENTI");

					if (numLavComputabiliCentr != null && numCentrNonVedenti != null
							&& numLavComputabiliCentr.compareTo(numCentrNonVedenti) < 0) {
						if (messaggiVerifica.equals("")) {
							messaggiVerifica = ProspettiConstant.WARNING_CENTRALINISTI;
						} else {
							messaggiVerifica = messaggiVerifica + "<br>" + ProspettiConstant.WARNING_CENTRALINISTI;
						}
					}

					BigDecimal numLavComputabiliMasso = (BigDecimal) prospetto
							.getAttribute("ROW.NUMLAVCOMPUTABILIMASSO");
					BigDecimal numMassoNonVedenti = (BigDecimal) prospetto.getAttribute("ROW.NUMMASSONONVEDENTI");

					if (numLavComputabiliMasso != null && numMassoNonVedenti != null
							&& numLavComputabiliMasso.compareTo(numMassoNonVedenti) < 0) {
						if (messaggiVerifica.equals("")) {
							messaggiVerifica = ProspettiConstant.WARNING_MASSOFISIOTERAPISTI;
						} else {
							messaggiVerifica = messaggiVerifica + "<br>"
									+ ProspettiConstant.WARNING_MASSOFISIOTERAPISTI;
						}
					}

					BigDecimal numLavComputabiliSomm = (BigDecimal) prospetto.getAttribute("ROW.NUMLAVCOMPUTABILISOMM");
					BigDecimal numSomministrati = (BigDecimal) prospetto.getAttribute("ROW.NUMSOMMINISTRATI");

					if (numLavComputabiliSomm != null && numSomministrati != null
							&& numLavComputabiliSomm.compareTo(numSomministrati) < 0) {
						if (messaggiVerifica.equals("")) {
							messaggiVerifica = ProspettiConstant.WARNING_DISABILI_SOMMINISTRATI;
						} else {
							messaggiVerifica = messaggiVerifica + "<br>"
									+ ProspettiConstant.WARNING_DISABILI_SOMMINISTRATI;
						}
					}

					BigDecimal numLavComputabiliConv = (BigDecimal) prospetto.getAttribute("ROW.NUMLAVCOMPUTABILICONV");
					BigDecimal numDisConvenzione = (BigDecimal) prospetto.getAttribute("ROW.NUMDISCONVENZIONE");

					if (numLavComputabiliConv != null && numDisConvenzione != null
							&& numLavComputabiliConv.compareTo(numDisConvenzione) < 0) {
						if (messaggiVerifica.equals("")) {
							messaggiVerifica = ProspettiConstant.WARNING_DISABILI_CONVENZIONE;
						} else {
							messaggiVerifica = messaggiVerifica + "<br>"
									+ ProspettiConstant.WARNING_DISABILI_CONVENZIONE;
						}
					}

					if (!messaggiVerifica.equals("")) {
						response.setAttribute("MSGCONTROLLICONGRUENZA", messaggiVerifica);
					}
				}

			} else {
				transExec.rollBackTransaction();
			}

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMProspRiepilogoDett::service(): Impossibile calcolare il riepilogo prospetto!", ex);
			ror.reportFailure(idFail);
			ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
				} catch (EMFInternalError e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"CMProspRiepilogoDett::service(): Impossibile calcolare il riepilogo prospetto!", ex);

				}
			}
			errors = true;
		}
	}

	/**
	 * Esegue il ricalcolo dei dati del riepilogo, utilizzata per calcolare la scopertura La procedura oltre a
	 * calcolarli ad una certa data (data riferimento se è presente oppure alla data odierna) esegue l'aggiornamento
	 * della tabella CM_PROSPETTO_INF
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean getRicalcoloRiepilogo(SourceBean request, SourceBean response) {
		ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		String codiceRit = "-1";
		boolean check = false;
		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			SQLCommand stmt = null;
			DataResult res = null;
			ScrollableDataResult sdr = null;
			SourceBean rowsSourceBean = null;
			String p_prgProspettoInf = "";
			String p_flgConvenzione = "S";

			Object prgProspettoInf = request.getAttribute("PRGPROSPETTOINF");
			if (prgProspettoInf == null)
				throw new Exception();
			if (prgProspettoInf instanceof BigDecimal) {
				p_prgProspettoInf = ((BigDecimal) prgProspettoInf).toString();
			} else if (prgProspettoInf instanceof String) {
				p_prgProspettoInf = (String) prgProspettoInf;
			}

			statementSB = (SourceBean) getConfig().getAttribute("RICALCOLO_PROSPETTO");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);
			// 3. p_flagconvenzione
			parameters.add(conn.createDataField("p_flgConvenzione", java.sql.Types.VARCHAR, p_flgConvenzione));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();
			DataField df = cdr.getPunctualDatafield();
			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			codiceRit = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);

			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				check = false;
			} else {
				check = true;
			}
		} catch (Exception e) {
			String msg = "Errore nel ricalcolo del prospetto ";
			ror.reportFailure(e, className, msg);
			check = false;
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
		return check;
	}

}