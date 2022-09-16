package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
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

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

public class InsertLavoratoriL68FromMovimento implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InsertLavoratoriL68FromMovimento.class.getName());
	private String className;
	private String prc;
	// PrgMovimento appena inserito
	private String keyTable;
	private String codTipoMov;
	private String codTipoAss;
	private String flgLegge68;
	private String prgProspettoInf;
	private BigDecimal userId;
	private TransactionQueryExecutor trans;
	private ArrayList warnings = null;

	public InsertLavoratoriL68FromMovimento(BigDecimal user, TransactionQueryExecutor transexec) {
		className = this.getClass().getName();
		prc = "Inserisci Lavoratore L68 da Movimento";
		userId = user;
		trans = transexec;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		SourceBean row = null;
		String checkNumber = "";
		String encryptKey = "";
		keyTable = ((BigDecimal) record.get("PRGMOVIMENTO")).toString();

		if (keyTable != null) {
			try {
				codTipoMov = (String) record.get("CODTIPOMOV");

				// l'automatisomo parte solamente se il movimento è di tipo CODTIPOASS IN ('NOH','NU2') oppure ha
				// FLGLEGGE68 = 'S'
				codTipoAss = (String) record.get("CODTIPOASS");
				flgLegge68 = (String) record.get("FLGLEGGE68");
				boolean continueAutomatismo = false;

				if (("N02").equalsIgnoreCase(codTipoAss) || ("NOH").equalsIgnoreCase(codTipoAss)) {
					continueAutomatismo = true;
				}

				if (("S").equalsIgnoreCase(flgLegge68) && flgLegge68 != null) {
					continueAutomatismo = true;
				}

				if (continueAutomatismo) {
					// recupero la chiave di crittazione
					SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
					encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");
					;
					// recupero l'utente
					String strCdnUtente = ((BigDecimal) sessione.getAttribute("_CDUT_")).toString();
					// recupero il prgProspetto
					checkNumber = getProspettoFromMovimento(encryptKey, strCdnUtente);

					// inserimento avvenuto correttamente
					if (("0").equalsIgnoreCase(checkNumber)) {
						return null;
					}
				}

				return null;
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "InsertLavoratoriL68FromMovimento", e);

			}

		}
		return null;
	}

	/**
	 * richiama la procedura per il recupero del prg del prospetto compatibile con il movimento che si sta validando o
	 * inserendo Richiama la procedura per l'inserimento o l'aggiornamento della tabella CM_PI_LAV_RISERVA per i
	 * lavoratori L68 in forza
	 * 
	 * possibili errori sono: -1 per errore sql -2 la provincia del movimento non è uguale alla provincia del sil
	 * 
	 * @return codice ritorno della procedura
	 * @throws EMFInternalError
	 */
	private String getProspettoFromMovimento(String encryptKey, String cdnUtente) throws EMFInternalError {
		String codiceRit = "";
		int paramIndex = 0;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		DataConnection conn = trans.getDataConnection();
		String sqlStr = SQLStatements.getStatement("GET_PROSPETTO_FROM_MOVIMENTO");
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		ArrayList parameters = new ArrayList(6);
		// Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", Types.BIGINT, null));
		command.setAsOutputParameters(paramIndex++);
		// preparazione dei Parametri di Input
		// 1 p_prgMovimento
		parameters.add(conn.createDataField("p_prgMovimento", java.sql.Types.BIGINT, new BigInteger(keyTable)));
		command.setAsInputParameters(paramIndex++);
		// 2 cdnUtente
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(cdnUtente)));
		command.setAsInputParameters(paramIndex++);
		// 3 encryptKey
		parameters.add(conn.createDataField("p_key", java.sql.Types.VARCHAR, encryptKey));
		command.setAsInputParameters(paramIndex++);
		// 4 codTipoMov
		parameters.add(conn.createDataField("p_codTipoMov", java.sql.Types.VARCHAR, codTipoMov));
		command.setAsInputParameters(paramIndex++);
		// parametri di Output
		// 5. p_prgProspettoOut
		parameters.add(conn.createDataField("p_prgProspettoOut", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);

		CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
		List outputParams = cdr.getContainedDataResult();
		// Reperisco i valori di output della stored
		// 0. Codice di Ritorno
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
		DataField df = pdr.getPunctualDatafield();
		codiceRit = df.getStringValue();
		// 1. prgProspetto
		pdr = (PunctualDataResult) outputParams.get(1);
		df = pdr.getPunctualDatafield();
		prgProspettoInf = df.getStringValue();

		if (!codiceRit.equals("0")) {
			int msgCode = 0, debugLevel = 0;
			String msg = null;
			switch (Integer.parseInt(codiceRit)) {
			case -1:
				msg = "Recupero Prospetto da Movimento e insert Lavorarore L68: errore sql procedura";
				break;
			case -2:
				msg = "Recupero Prospetto da Movimento e insert Lavorarore L68: provincia movimento diversa da qualla del SIL";
				break;
			case -3:
				msg = "Recupero Prospetto da Movimento e insert Lavorarore L68: non esiste il movimento di avviamento corrispondente";
				break;
			case -4:
				msg = "Recupero Prospetto da Movimento e insert Lavorarore L68: non esiste il prospetto associato";
				break;
			case -5:
				msg = "Recupero Prospetto da Movimento e insert Lavorarore L68: errore sql aggiornamento ass. in convenzione";
				break;
			default:
				msg = "Recupero Prospetto da Movimento e insert Lavorarore L68: errore sql procedura";
			}
			_logger.debug(msg);

		} else {
			_logger.debug("Recupero Prospetto da Movimento e insert Lavorarore L68: OK");

		}
		return codiceRit;

	}

}