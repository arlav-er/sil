package it.eng.sil.coop.bean.blen.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.bean.blen.constant.ServiziConstant;

public class Rosa {

	private static Logger logger = Logger.getLogger(Rosa.class);

	public static RosaInsert insertRosaNominativa(TransactionQueryExecutor transExec, BigDecimal prgRichiestaAz)
			throws EMFInternalError {
		// TODO vedi CreaRosaNomGrezza.java MATCH_CREA_ROSA_NOMINATIVA

		DataConnection conn = transExec.getDataConnection();

		String sqlStr = SQLStatements.getStatement("MATCH_CREA_ROSA_NOMINATIVA_TRANS");
		StoredProcedureCommand command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		ArrayList parameters = new ArrayList(6);
		int paramIndex = 0;

		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// preparazione dei Parametri di Input
		// 2. p_prgRichiestaAz
		parameters.add(conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, prgRichiestaAz.toBigInteger()));
		command.setAsInputParameters(paramIndex++);
		// 3. p_cdnUtente
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, ServiziConstant.UTENTE_BLEN));
		command.setAsInputParameters(paramIndex++);
		// 4. p_isCommitted
		parameters.add(conn.createDataField("p_isCommitted", java.sql.Types.BIGINT, new BigInteger("0")));
		command.setAsInputParameters(paramIndex++);

		// parametri di Output
		// 5. p_errCode
		parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// 6. p_out_prgIncrocio
		parameters.add(conn.createDataField("p_out_prgIncrocio", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// 7. p_out_prgRosa
		parameters.add(conn.createDataField("p_out_prgRosa", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// Chiamata alla Stored Procedure
		DataResult dr = command.execute(parameters);

		CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
		List outputParams = cdr.getContainedDataResult();
		// Reperisco i valori di output della stored
		// 0. Codice di Ritorno
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
		DataField df = pdr.getPunctualDatafield();
		String codiceRit = df.getStringValue();
		// 1. ErrCodeOut
		pdr = (PunctualDataResult) outputParams.get(1);
		df = pdr.getPunctualDatafield();
		String errCode = df.getStringValue();
		if (errCode != null) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"Errore nell'inserimento della rosa nominativa errCode: " + errCode);
		}

		// 1. p_out_prgIncrocio
		pdr = (PunctualDataResult) outputParams.get(2);
		df = pdr.getPunctualDatafield();
		String prgIncrocio = (String) df.getStringValue();
		if (prgIncrocio == null) {
			prgIncrocio = "";
		}
		// 2. p_out_prgRosa
		pdr = (PunctualDataResult) outputParams.get(3);
		df = pdr.getPunctualDatafield();
		String prgRosa = (String) df.getStringValue();

		return new RosaInsert(prgRosa, prgIncrocio);
	}

	public static void insertLavARosaNominativa(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore,
			BigDecimal prgRichiestaAz, String prgIncrocio, String prgRosa, String codCpiCompLav)
			throws EMFInternalError {
		// TODO vedi AggiungiLavRosaNomGrezza.java AGGIUNGI_LAV_ROSA_GREZZA

		DataConnection conn = transExec.getDataConnection();

		String sqlStr = SQLStatements.getStatement("AGGIUNGI_LAV_ROSA_GREZZA_TRANS");
		StoredProcedureCommand command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		ArrayList parameters = new ArrayList(6);
		int paramIndex = 0;
		parameters = new ArrayList(10);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// preparazione dei Parametri di Input
		// 2. p_prgRichiestaAz
		parameters.add(conn.createDataField("p_prgRichiestaAz", java.sql.Types.BIGINT, prgRichiestaAz.toBigInteger()));
		command.setAsInputParameters(paramIndex++);
		// 3. p_cdnUtente
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.VARCHAR, ServiziConstant.UTENTE_BLEN));
		command.setAsInputParameters(paramIndex++);
		// 4. p_prgIncrocio
		parameters.add(conn.createDataField("p_prgIncrocio", java.sql.Types.VARCHAR, prgIncrocio));
		command.setAsInputParameters(paramIndex++);
		// 5. p_prgRosa
		parameters.add(conn.createDataField("p_prgRosa", java.sql.Types.VARCHAR, prgRosa));
		command.setAsInputParameters(paramIndex++);
		// 6. p_cdnLavoratore
		parameters.add(conn.createDataField("p_cdnLavoratore", java.sql.Types.VARCHAR, cdnLavoratore));
		command.setAsInputParameters(paramIndex++);
		// 7. p_codCpi
		parameters.add(conn.createDataField("p_codCpi", java.sql.Types.VARCHAR, null)); // dati per il contatto, non
																						// utilizzato per rosa blen
		command.setAsInputParameters(paramIndex++);
		// 8. p_prgSpiContatto
		parameters.add(conn.createDataField("p_prgSpiContatto", java.sql.Types.VARCHAR, null)); // dati per il contatto,
																								// non utilizzato per
																								// rosa blen
		command.setAsInputParameters(paramIndex++);
		// 9. p_prgTipoContatto
		parameters.add(conn.createDataField("p_prgTipoContatto", java.sql.Types.VARCHAR, null)); // dati per il
																									// contatto, non
																									// utilizzato per
																									// rosa blen
		command.setAsInputParameters(paramIndex++);
		// 10. p_isCommitted
		parameters.add(conn.createDataField("p_isCommitted", java.sql.Types.BIGINT, new BigInteger("0")));
		command.setAsInputParameters(paramIndex++);
		// parametri di Output
		// 11. p_errCode
		parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		DataResult dr = command.execute(parameters);
		CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
		List outputParams = cdr.getContainedDataResult();
		// Reperisco i valori di output della stored
		// 0. Codice di Ritorno
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
		DataField df = pdr.getPunctualDatafield();
		String codiceRit = df.getStringValue();
		// 1. ErrCodeOut
		pdr = (PunctualDataResult) outputParams.get(1);
		df = pdr.getPunctualDatafield();
		String errCode = df.getStringValue();
		if (errCode != null) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"Errore nell'inserimento del lavoratore nella rosa nominativa errCode: " + errCode);
		}

	}

}
