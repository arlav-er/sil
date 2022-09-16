package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PosInpsUtils;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertUnitaAziendaVal extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertUnitaAziendaVal.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean unitaPresente = false;
		boolean errors = false;
		boolean atecoValido = false;

		Object flgSede = request.getAttribute("flgSede");
		String strNumeroInps = request.containsAttribute("strPosInps") ? request.getAttribute("strPosInps").toString()
				: "";
		String codAzStato = request.containsAttribute("codAzStato") ? request.getAttribute("codAzStato").toString()
				: "";
		String codAteco = request.containsAttribute("codAteco") ? request.getAttribute("codAteco").toString() : "";

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;

		atecoValido = it.eng.sil.module.anag.AtecoUtils.checkValiditaAteco(codAteco, true);

		if (!atecoValido) {
			request.delAttribute("PRGUNITA"); // GG - senno' le DIPENDENES
												// portano avanti un unita' MAI
												// inserita!
			response.delAttribute("ROWS"); // GG - senno' rimane il SB "ROWS"
											// della doSelect per il sequence (e
											// ho un PRGUNITA valorizzato ma NON
											// usato!)

			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.UnitaAzienda.ATECO_NON_VALIDO);
			errors = true;
			// Per il controllo dell'avvenuto inserimento o meno
			response.delAttribute("INSERT_OK");
			response.setAttribute("INSERT_OK", String.valueOf(!errors).toUpperCase());
			return;
		}

		/*
		 * Effettua il controllo di esistenza dell'unita che si sta inserendo in modo da non inserire duplicati
		 */
		this.setSectionQuerySelect("QUERY_CONTROLLA_ESISTENZA_UNITAAZ");
		SourceBean tmp = doSelect(request, response);
		if ((tmp != null) && (tmp.getAttribute("ROW.prgazienda") != null) && codAzStato.equals("1")) {
			// Unità già presente
			unitaPresente = true;
		}

		// Elimino dalla response il risultato della select
		response.delAttribute("ROWS");

		BigDecimal prgUnita = getPrgUnita(request, response);

		if (!unitaPresente) {
			try {
				if (!isTransactionEnabled()) {
					transExec = new TransactionQueryExecutor(getPool());
					enableTransactions(transExec);

					transExec.initTransaction();
				} else {
					// 12/06/2008 savino: FASE 2 trasferimento ramo azienda.
					// Se il modulo e' stato chiamato in un contesto transazionale a cui appartengono altri moduli,
					// recupero l'oggetto TransactionQueryExecutor che sara' presente nel RequestContainer
					transExec = (TransactionQueryExecutor) RequestContainer.getRequestContainer()
							.getAttribute("TQE_OBJECT");
				}
				if (prgUnita != null) {
					setKeyinRequest(prgUnita, request);
					// this.setMessageIdSuccess(idSuccess);
					this.setSectionQueryInsert("QUERY_INSERT");

					if (!strNumeroInps.equals("")) {
						if (!(PosInpsUtils.controllaInps(strNumeroInps))) {
							reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INPS_ERRATO, null,
									"controllaInps", "");
						}
					} else {
						reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INPS_NOVALORIZ, null,
								"controllaInps", "");
					}

					boolean ok = doInsert(request, response);
					if (!ok) {
						throw new Exception("ERRORE ESEGUENDO L'INSERIMENTO DELL'UNITA' AZIENDALE");
					}
					if (flgSede.toString().equals("S")) {
						boolean done = disassertSedePrincipale(request, response, transExec);
						if (done)
							reportOperation.reportSuccess(MessageCodes.UnitaAzienda.WAR_SEDE_LEGALE_MOD);
					}
				}
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"InsertUnitaAziendaVal::service(): Impossibile inserire l'unita aziendale!", ex);

				request.delAttribute("PRGUNITA"); // GG - senno' le DIPENDENES
													// portano avanti un unita'
													// MAI inserita!
				response.delAttribute("ROWS"); // GG - senno' rimane il SB
												// "ROWS" della doSelect per il
												// sequence (e ho un PRGUNITA
												// valorizzato ma NON usato!)

				reportOperation.reportFailure(idFail);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				errors = true;
			}
		} else {
			request.delAttribute("PRGUNITA"); // GG - senno' le DIPENDENES
												// portano avanti un unita' MAI
												// inserita!
			response.delAttribute("ROWS"); // GG - senno' rimane il SB "ROWS"
											// della doSelect per il sequence (e
											// ho un PRGUNITA valorizzato ma NON
											// usato!)

			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.ImportMov.ERR_UNITA_GIA_PRESENTE);
			errors = true;
		}
		// Per il controllo dell'avvenuto inserimento o meno
		response.delAttribute("INSERT_OK");
		response.setAttribute("INSERT_OK", String.valueOf(!errors).toUpperCase());
	}

	private BigDecimal getPrgUnita(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_SEQUENCE");

		SourceBean beanPrgUnita = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanPrgUnita.getAttribute("ROW.prgUnita");
	}

	private void setKeyinRequest(BigDecimal prgUnita, SourceBean request) throws Exception {
		if (request.getAttribute("PRGUNITA") != null) {
			request.delAttribute("PRGUNITA");
		}

		request.setAttribute("PRGUNITA", prgUnita);
	}

	/**
	 * effettua un update di tutte le unità per disasserire l'eventuale flag che identifica una data sede come
	 * principale. Questo, ovviamente, se (e solo se) l'unità che stiamo inserendo è stata definita sede principale. GG:
	 * rende TRUE se ha fatto la modifica; false altrimenti.
	 */
	private boolean disassertSedePrincipale(SourceBean request, SourceBean response, TransactionQueryExecutor transExec)
			throws Exception {

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		try {

			// imposto la chiamata per la stored procedure
			conn = transExec.getDataConnection();

			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY_DISASSERT_SEDE");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// imposto i valori di input
			Object prgUnita = request.getAttribute("prgUnita");
			Object prgAzienda = request.getAttribute("prgAzienda");

			// prelevo la session ed il codice utente necessario per la stored
			// procedure
			RequestContainer requestContainer = this.getRequestContainer();
			SessionContainer session = requestContainer.getSessionContainer();
			Object cdUtente = session.getAttribute("_CDUT_");

			// imposto i parametri
			ArrayList parameters = new ArrayList(4);
			parameters.add(conn.createDataField("result", Types.BIGINT, null));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("prgAzienda", Types.BIGINT, prgAzienda));
			command.setAsInputParameters(1);
			parameters.add(conn.createDataField("prgParUnita", Types.BIGINT, prgUnita));
			command.setAsInputParameters(2);
			parameters.add(conn.createDataField("cdnParUtMod", Types.BIGINT, cdUtente));
			command.setAsInputParameters(3);

			// eseguo!!
			dr = command.execute(parameters);

			// VALORE DI RITORNO (result)
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();

			String strResult = (String) df.getStringValue();
			_logger.debug("disassertSedePrincipale() - strResult=" + strResult);

			boolean almenoUno = StringUtils.isFilled(strResult) && !strResult.equals("0");
			return almenoUno;
		} catch (Exception ex) {
			LogUtils.logError("disassertSedePrincipale", "Error", ex, this);
			throw ex;
		}
	}

} // end class
