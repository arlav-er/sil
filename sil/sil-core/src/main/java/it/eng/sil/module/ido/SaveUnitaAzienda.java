package it.eng.sil.module.ido;

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

public class SaveUnitaAzienda extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SaveUnitaAzienda.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean unitaPresente = false;
		boolean atecoValido = false;

		String codAzStato = request.containsAttribute("codAzStato") ? request.getAttribute("codAzStato").toString()
				: "";
		String strNumeroInps = request.containsAttribute("strPosInps") ? request.getAttribute("strPosInps").toString()
				: "";
		String codAteco = request.containsAttribute("codAteco") ? request.getAttribute("codAteco").toString() : "";
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		Object flgSede = request.getAttribute("flgSede");
		String strIsSedeLegale = StringUtils.getAttributeStrNotNull(request, "isSedeLegale");
		boolean isSedeLegale = strIsSedeLegale.equalsIgnoreCase("TRUE");

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		TransactionQueryExecutor transExec = null;

		try {

			if (!it.eng.sil.module.anag.AtecoUtils.checkValiditaAteco(codAteco, true)) {
				reportOperation.reportFailure(idFail);
				reportOperation.reportFailure(MessageCodes.UnitaAzienda.ATECO_NON_VALIDO);
				return;
			}

			transExec = new TransactionQueryExecutor(getPool(), this);
			/*
			 * Effettua il controllo di esistenza dell'unita che si sta inserendo in modo da non inserire duplicati
			 */
			try {
				unitaPresente = controllaEsistenzaUnita(request, response, codAzStato, transExec);
			} catch (Exception el) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "SaveUnitaAzienda: ", (Exception) el);
				// In caso di errori non permetto l'inserimento
				unitaPresente = true;
			}

			/*
			 * Non è possibile modificare il campo stato azienda in 'In Attività', se vi è già un'azienda con lo stesso
			 * stato con gli stessi dati (CF,indirizzo, comune, cap)
			 */
			if (!unitaPresente) {
				try {
					enableTransactions(transExec);
					transExec.initTransaction();
					if (!strNumeroInps.equals("")) {
						if (!(PosInpsUtils.controllaInps(strNumeroInps))) {
							reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INPS_ERRATO, null,
									"controllaInps", "");
						}
					} else {
						reportOperation.reportFailure(MessageCodes.ImportMov.WAR_NUM_INPS_NOVALORIZ, null,
								"controllaInps", "");
					}
					this.setSectionQueryUpdate("QUERY");
					boolean updateSucces = doUpdate(request, response);
					if (!updateSucces) {
						throw new Exception();
					}
					if (flgSede.toString().equals("S")) {
						boolean done = disassertSedePrincipale(request, response, transExec);
						if (done && !isSedeLegale)
							reportOperation.reportSuccess(MessageCodes.UnitaAzienda.WAR_SEDE_LEGALE_MOD);
					}

					transExec.commitTransaction();
					reportOperation.reportSuccess(idSuccess);
				} catch (Exception ex) {
					transExec.rollBackTransaction();
					reportOperation.reportFailure(idFail);
					reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
				}
			} else {
				if (transExec != null) {
					transExec.getDataConnection().close();
				}
				reportOperation.reportFailure(idFail);
				reportOperation.reportFailure(MessageCodes.ImportMov.ERR_UNITA_GIA_PRESENTE);
			}
		} catch (Exception el) {
			if (transExec != null) {
				transExec.getDataConnection().close();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "SaveUnitaAzienda: ", (Exception) el);
		}
	}

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

	private boolean controllaEsistenzaUnita(SourceBean request, SourceBean response, String codAzStato,
			TransactionQueryExecutor transExec) throws Exception {
		boolean risultato = false;
		Object obj[] = new Object[4];

		// ResultSet rs = null;
		String codFis = "";
		String indirizzo = "";
		String comune = "";
		String cap = "";

		codFis = StringUtils.getAttributeStrNotNull(request, "strCodicefiscaleInsUnita");
		indirizzo = StringUtils.getAttributeStrNotNull(request, "strIndirizzo");
		comune = StringUtils.getAttributeStrNotNull(request, "CODCOM");
		cap = StringUtils.getAttributeStrNotNull(request, "strCap");

		obj[0] = codFis;
		obj[1] = indirizzo;
		obj[2] = comune;
		obj[3] = cap;

		SourceBean tmp = (SourceBean) transExec.executeQuery("CONTROLLA_ESISTENZA_UNITAAZ", obj, "SELECT");
		String prgSelected = tmp.containsAttribute("ROW.prgUnita") ? tmp.getAttribute("ROW.prgUnita").toString() : "";
		String prgUnita = request.getAttribute("prgUnita").toString();

		if ((tmp != null) && (tmp.getAttribute("ROW.prgUnita") != null) && (!prgUnita.equals(prgSelected))
				&& (codAzStato.equals("1"))) {
			risultato = true;
		}
		return risultato;
	}
}