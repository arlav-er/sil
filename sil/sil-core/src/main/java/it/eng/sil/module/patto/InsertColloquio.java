package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class InsertColloquio extends AbstractSimpleModule {
	private final String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) throws Exception {
		// System.out.println("InsertColloquio chiamato");
		// if (1==1) return ;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		boolean fail = false;

		try {
			String codServizio = Utils.notNull(request.getAttribute("codServizio"));
			if (codServizio.equals(MessageCodes.Patto.COD_SERVIZIO_NGG)) {
				fail = checkCondizioniInserimentoProgramma(reportOperation, request, response, "B", codServizio);
				if (!fail) {
					fail = checkCondizioniInserimentoProgramma(reportOperation, request, response, "W", codServizio);
				}
			}

			if (!fail) {
				this.setSectionQuerySelect("QUERY_SELECT_CHECK_SERVIZIO_INI");
				SourceBean rowsIni = doSelect(request, response);
				if (rowsIni.containsAttribute("ROW.codtsprogramma")) {
					String codTsProgramma = Utils.notNull(rowsIni.getAttribute("ROW.codtsprogramma"));
					fail = checkCondizioniInserimentoProgramma(reportOperation, request, response, "B", codTsProgramma);
				}
			}

			if (!fail) {
				this.setSectionQuerySelect("QUERY_SELECT_EXIST_PROGRAMMA_APERTO");
				SourceBean rowsP = doSelect(request, response);
				BigDecimal numCollAperti = (BigDecimal) rowsP.getAttribute("ROW.numProgrammiAperti");
				if (numCollAperti.intValue() > 0) {
					reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_PROGRAMMA_APERTO);
					response.setAttribute("CHECK_KO", "true");
					fail = true;
				}
			}

			if (!fail) {
				this.setSectionQuerySelect("QUERY_SELECT_CHECK_SERVIZIO_REI");
				SourceBean rowsRei = doSelect(request, response);
				if (rowsRei.containsAttribute("ROW.codtsprogramma")) {
					String codTsProgramma = Utils.notNull(rowsRei.getAttribute("ROW.codtsprogramma"));
					fail = checkCondizioniInserimentoProgramma(reportOperation, request, response, "B", codTsProgramma);
				}
			}

			if (!fail) {
				this.setSectionQuerySelect("QUERY_SELECT_CHECK_SERVIZIO_UMBAT");
				SourceBean rowsUmbat = doSelect(request, response);
				if (rowsUmbat.containsAttribute("ROW.codtsprogramma")) {
					String codTsProgramma = Utils.notNull(rowsUmbat.getAttribute("ROW.codtsprogramma"));
					fail = checkCondizioniInserimentoProgramma(reportOperation, request, response, "B", codTsProgramma);
				}
			}

			if (!fail) {
				transExec = new TransactionQueryExecutor(getPool());
				this.enableTransactions(transExec);

				//
				transExec.initTransaction();

				BigDecimal prgColloquio = doNextVal(request, response);

				if (prgColloquio == null) {
					throw new Exception("Impossibile leggere s_or_colloquio.nextval");
				}

				request.delAttribute("prgColloquio");
				request.setAttribute("prgColloquio", prgColloquio.toString());

				// Riccardi : non è possibile inserire un colloquio con una iscrizione CIG associata ad un altro
				// colloquio già esistente
				String prgAltraIscr = Utils.notNull(request.getAttribute("PRGALTRAISCR"));
				if (prgAltraIscr.equals("")) {
					inserisci(transExec, request, response);
					reportOperation.reportSuccess(idSuccess);
				} else {
					this.setSectionQuerySelect("QUERY_SELECT_UNICO_CIG_COLLOQUIO");
					SourceBean rows = doSelect(request, response);
					BigDecimal numCigColl = (BigDecimal) rows.getAttribute("ROW.numCigColl");

					if (numCigColl.intValue() > 0) {
						transExec.commitTransaction();
						response.setAttribute("numCigColl", "1");
						reportOperation.reportFailure(MessageCodes.Patto.COLLOQUIO_CON_CIG_COLLEGATA);
					} else {
						inserisci(transExec, request, response);
						reportOperation.reportSuccess(idSuccess);
					}
				}

				response.delAttribute("prgColloquio");
				response.setAttribute("prgColloquio", prgColloquio);
			}
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}

	private boolean checkCondizioniInserimentoProgramma(ReportOperationResult reportOperation, SourceBean request,
			SourceBean response, String codMonoErrore, String codTsProgramma) throws Exception {
		boolean failCheck = false;

		DataConnection conn = null;
		try {
			conn = getDataConnection();
			String statement = SQLStatements.getStatement("GET_CHECK_INS_QUERY_TSPROGRAMMA");
			SQLCommand sqlCommand = conn.createSelectCommand(statement);
			List<DataField> inputParameter = new ArrayList<DataField>();
			inputParameter.add(conn.createDataField("", Types.VARCHAR, codMonoErrore));
			inputParameter.add(conn.createDataField("", Types.VARCHAR, codTsProgramma));

			DataResult dataResult = sqlCommand.execute(inputParameter);
			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();
			String flgAttivo = SourceBeanUtils.getAttrStrNotNull(sb, "ROW.flgAttivo");

			if ("S".equals(flgAttivo)) {
				String cdnLav = Utils.notNull(request.getAttribute("cdnLavoratore"));
				String strSqlQuery = SourceBeanUtils.getAttrStrNotNull(sb, "ROW.strSqlQuery");
				String strMessErrore = SourceBeanUtils.getAttrStrNotNull(sb, "ROW.strMessErrore");

				sqlCommand = conn.createSelectCommand(strSqlQuery);
				inputParameter = new ArrayList<DataField>();
				inputParameter.add(conn.createDataField("", Types.VARCHAR, cdnLav));

				dataResult = sqlCommand.execute(inputParameter);
				scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
				sb = scrollableDataResult.getSourceBean();

				boolean reportFailOrWarn = false;
				if (sb.containsAttribute("ROW.numProgrammi")) {
					BigDecimal numProgrammi = SourceBeanUtils.getAttrBigDecimal(sb, "ROW.numProgrammi");
					if (numProgrammi.intValue() > 0)
						reportFailOrWarn = true;
				} else if (sb.containsAttribute("ROW.numAdesioni")) {
					BigDecimal numAdesioni = SourceBeanUtils.getAttrBigDecimal(sb, "ROW.numAdesioni");
					if (numAdesioni.intValue() == 0)
						reportFailOrWarn = true;
				}

				if (reportFailOrWarn) {
					Vector paramV = new Vector(1);
					paramV.add(strMessErrore);

					if ("B".equals(codMonoErrore)) {
						reportOperation.reportFailure(MessageCodes.General.RIPORTA_ERRORE, className,
								"Errore Inserimento Programma", paramV);
						response.setAttribute("CHECK_KO", "true");
						failCheck = true;
					} else if ("W".equals(codMonoErrore)) {
						reportOperation.reportWarning(MessageCodes.General.RIPORTA_WARNING, className,
								"Warning Inserimento Programma", paramV);
					}
				}
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		return failCheck;
	}

	private void inserisci(TransactionQueryExecutor transExec, SourceBean request, SourceBean response)
			throws Exception {
		boolean ret = false;
		this.setSectionQueryInsert("QUERY_INSERT_COLLOQUIO");
		ret = doInsert(request, response);

		if (!ret) {
			throw new Exception("impossibile inserire in or_colloquio in transazione");
		}

		this.setSectionQueryInsert("QUERY_INSERT_SCHEDA_COLLOQUIO");
		ret = doInsert(request, response);

		if (!ret) {
			throw new Exception("impossibile inserire in or_scheda_colloquio in transazione");
		}

		transExec.commitTransaction();
	}
}
