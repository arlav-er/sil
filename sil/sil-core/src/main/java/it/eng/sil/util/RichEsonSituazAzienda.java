/*
 * Creato il 7-dic-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import oracle.jdbc.OracleTypes;

/**
 * @author melandri
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class RichEsonSituazAzienda {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RichEsonSituazAzienda.class.getName());

	public static final String thisClassName = StringUtils.getClassName(EncryptDecryptUtils.class);

	private SourceBean serviceResponse;
	private String prgRichEsonero; // Chiave della richiesta di esonero
	private String DATINIZIOVALIDITA; // Data inizio validità della richiesta
										// di esonero
	private String DATPAGAMENTO; // Data rispetto la quale si vuole calcolare
									// la situazione dei pagamenti
	private double costoGiornaliero; // Costo giornaliero
	private int numGiorniLavorativi; // Numero dei giorni lavorativi tra le
										// date considerate
	private double valorePresunto; // Valore calcolato = (Numero dei giorni
									// lavorativi) * (Costo giornaliero)
	private int importoTotale; // Somma dei pagamenti effettuati
	private String isOk; // isOk = "S" se l'azienda è in regola con i
							// pagamenti, altrimenti isOk = "N"

	public RichEsonSituazAzienda(String prgRichEsonero, String DATINIZIOVALIDITA, String DATPAGAMENTO,
			SourceBean serviceResponse) {
		this.prgRichEsonero = prgRichEsonero;
		this.DATINIZIOVALIDITA = DATINIZIOVALIDITA;
		this.DATPAGAMENTO = DATPAGAMENTO;
		this.serviceResponse = serviceResponse;
	}

	/**
	 * Gestione completa del calcolo della situazione delle richieste di esonero
	 * 
	 * il metodo chiama una procedura che calcola: 1) giorni lavorativi 2) costo presunto totale 3) totale pagamento
	 * 
	 * 
	 */
	public HashMap getCalcoloSituazione() {

		_logger.debug(thisClassName + "::getCalcoloSituazione() CALLED...");

		HashMap valoriCalcolati = new HashMap();
		String returnValue = null;
		DataConnection conn = null;
		try {

			SessionContainer sessionContainer = RequestContainer.getRequestContainer().getSessionContainer();

			String codiceRit = "-1";
			String p_out_giorniLavorativi = "0";
			String p_out_costoTotale = "0";
			String p_out_costoVersato = "0";
			String p_out_query = "";
			String isOk = "S";
			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			StoredProcedureCommand command = null;
			DataResult dr = null;

			String p_prgrichesonero = prgRichEsonero;
			String p_dataCalcolo = DATPAGAMENTO;

			String sqlFunction = SQLStatements.getStatement("GET_SITUAZIONE_RICH_ESONERO");
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlFunction);
			ArrayList parameters = new ArrayList(6);
			int paramIndex = 0;

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// 2.
			parameters.add(
					conn.createDataField("p_prgrichesonero", java.sql.Types.BIGINT, new BigDecimal(p_prgrichesonero)));
			command.setAsInputParameters(paramIndex++);
			// 3.
			parameters.add(conn.createDataField("p_dataCalcolo", java.sql.Types.VARCHAR, p_dataCalcolo));
			command.setAsInputParameters(paramIndex++);
			// 4.
			parameters.add(conn.createDataField("p_out_giorniLavorativi", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 5.
			parameters.add(conn.createDataField("p_out_costoTotale", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 6.
			parameters.add(conn.createDataField("p_out_costoVersato", java.sql.Types.VARCHAR, null));
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

			if (("0").equalsIgnoreCase(codiceRit)) {

				// 1. p_out_giorniLavorativi
				pdr = (PunctualDataResult) outputParams.get(1);
				df = pdr.getPunctualDatafield();
				p_out_giorniLavorativi = (String) df.getStringValue();

				// 2. p_out_costoTotale
				pdr = (PunctualDataResult) outputParams.get(2);
				df = pdr.getPunctualDatafield();
				p_out_costoTotale = (String) df.getStringValue();

				// 3. p_out_costoVersato
				pdr = (PunctualDataResult) outputParams.get(3);
				df = pdr.getPunctualDatafield();
				p_out_costoVersato = (String) df.getStringValue();

			}

			BigDecimal versato = new BigDecimal(Double.parseDouble(p_out_costoVersato));
			BigDecimal costoTotale = new BigDecimal(Double.parseDouble(p_out_costoTotale));

			// verifica se l'azienda è in regola
			int checkRegola = versato.compareTo(costoTotale);
			if (checkRegola < 0) {
				isOk = "N";
			}

			valoriCalcolati.put("giorniCalcolati", p_out_giorniLavorativi);
			valoriCalcolati.put("costoTotale", p_out_costoTotale);
			valoriCalcolati.put("costoVersato", p_out_costoVersato);
			valoriCalcolati.put("isOk", isOk);

		} catch (EMFInternalError xe) {
			LogUtils.logError("RichEsonSituazAzienda.getCalcoloSituazione",
					"errore durante il calcolo della situazione richiesta esonero", xe);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
		}

		return valoriCalcolati;
	}

	/**
	 * Lista calcolo della situazione delle richieste di esonero
	 * 
	 * @return SourceBean
	 */
	public static SourceBean getListaCalcoloSituazione(String p_prgrichesonero, String p_dataCalcolo)
			throws SourceBeanException {

		_logger.debug(thisClassName + "::getListaCalcoloSituazione() CALLED...");

		SourceBean rowsSourceBean = new SourceBean("ROWS");
		String returnValue = null;
		DataConnection conn = null;
		try {

			SessionContainer sessionContainer = RequestContainer.getRequestContainer().getSessionContainer();

			String codiceRit = "-1";
			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);
			ScrollableDataResult sdr = null;
			StoredProcedureCommand command = null;
			DataResult dr = null;

			// String p_prgrichesonero = prgRichEsonero;
			// String p_dataCalcolo = DATPAGAMENTO;

			String sqlFunction = SQLStatements.getStatement("LISTA_SITUAZIONE_RICH_ESONERO");
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlFunction);
			ArrayList parameters = new ArrayList(3);
			int paramIndex = 0;

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", OracleTypes.CURSOR, null));
			command.setAsOutputParameters(paramIndex++);

			// 2.
			parameters.add(
					conn.createDataField("p_prgrichesonero", java.sql.Types.BIGINT, new BigDecimal(p_prgrichesonero)));
			command.setAsInputParameters(paramIndex++);
			// 3.
			parameters.add(conn.createDataField("p_dataCalcolo", java.sql.Types.VARCHAR, p_dataCalcolo));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			// dr = command.execute(parameters);

			dr = command.execute(parameters);
			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}

			// recupero il SourceBean
			rowsSourceBean = sdr.getSourceBean();

		} catch (EMFInternalError xe) {
			LogUtils.logError("RichEsonSituazAzienda.getCalcoloSituazione",
					"errore durante il calcolo della situazione richiesta esonero", xe);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
		}
		return rowsSourceBean;
	}
}