/*
 * Creato il 17-mar-05
 * ultima modifica: 12-lug-2005 
 * Author: rolfini
 * 
 */
package it.eng.sil.bean.SilProLabor;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.MobilitaManager;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;
import oracle.sql.CLOB;

/**
 * @author rolfini
 * 
 */
public class DbManager {

	private TransactionQueryExecutor transEx = null;
	private ConfigSingleton config = null;
	private String opResult = null; // variabile che contiene il risultato
									// dell'ultima operazione

	// variabili per il recupero dello stato occupazionale (in genere null)
	private boolean forzaturaChiusuraMobilita = false;
	private String statoOccCOD = "";
	private String statoOccDATAANZ = "";

	protected DbManager(ConfigSingleton config) throws Exception {
		this.config = config;
		initConnection();
	}

	protected DbManager(ConfigSingleton config, TransactionQueryExecutor transEx) throws Exception {
		this.config = config;
		this.transEx = transEx;
	}

	private void initConnection() throws Exception {

		// Inizializzo la connessione
		try {
			transEx = new TransactionQueryExecutor(Values.DB_SIL_DATI);
		} catch (Exception ex) {
			throw new Exception("*** ERRORE DI CONNESSIONE");
		}

	}

	private String findCdnLavoratore(String cf, DataConnection conn) throws Exception {
		// cerco il cdnlavoratore del soggetto in esame
		// è una nuova chiamata a stored

		StoredProcedureCommand command = null;
		DataResult dr = null;
		String cdnLav = "";

		SourceBean statementSB = (SourceBean) config.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT", "NAME",
				"SILPROLABOR_FINDLAVORATORE");
		String statement = statementSB.getAttribute("QUERY").toString();
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

		ArrayList paramFindLav = new ArrayList(2);
		paramFindLav.add(conn.createDataField("result", Types.DECIMAL, null));
		command.setAsOutputParameters(0);
		paramFindLav.add(conn.createDataField("CodiceFiscale", Types.VARCHAR, cf));
		command.setAsInputParameters(1);

		// eseguo!!
		dr = command.execute(paramFindLav);
		PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
		DataField df = pdr.getPunctualDatafield();
		if (df.getObjectValue() != null) {
			cdnLav = df.getStringValue();
		}

		return cdnLav;

	}

	/**
	 * normalizzaStatoOcc normalizza lo stato occupazionale secondo i bisogni di prolabor. Questa normalizzazione
	 * aggiunge a sinistra tanti spazi quanti sono i caratteri mancanti ad arrivare alla lunghezza di 4
	 * 
	 * @param statoOcc
	 */
	private String normalizzaStatoOcc(String statoOcc) {

		String statoOccNormalizzato = statoOcc;
		for (int i = 0; i < (4 - statoOcc.length()); i++) {
			statoOccNormalizzato = statoOccNormalizzato.concat(" ");
		}
		return statoOccNormalizzato;
	}

	/**
	 * aggiornaValoriStatoOccPerLog preleva dal bean lo stato occupazionale e la data di anzianità normalizzandoli e
	 * riformattandoli ai fini di una corretta visualizzazione nei log
	 * 
	 * @param statoOcc
	 */
	private void aggiornaValoriStatoOccPerLog(StatoOccupazionaleBean statoOcc) {
		// aggiorno i valori dello stato occupazionale (utili per il log)
		this.statoOccCOD = normalizzaStatoOcc(statoOcc.getStatoOccupaz());
		String dataCalcoloAnzianita = statoOcc.getDataAnzianita();
		if (dataCalcoloAnzianita != null && !dataCalcoloAnzianita.equals("")) {
			dataCalcoloAnzianita.replace('/', '-');
		}
		this.statoOccDATAANZ = dataCalcoloAnzianita;
	}

	/**
	 * getAggiornamento Lav
	 * 
	 * AGGIORNAMENTO DEI DATI DEL LAVORATORE
	 * 
	 * 
	 * @param cf
	 * @param dataModifica
	 * @param risultato
	 * @return
	 * @throws Exception
	 */

	protected CLOB getAggiornamentoLav(String cf, String dataModifica) throws Exception {

		// reset opResult
		this.opResult = null;

		CLOB tracciatoXML = null;
		String risultato = "";
		// DataConnectionManager dcm=null;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;

		try {

			// richiamo l'apposita stored procedure per l'elaborazione del file
			// xml
			conn = transEx.getDataConnection();
			SourceBean statementSB = (SourceBean) config.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT", "NAME",
					"SILPROLABOR_GETDATILAVORATORE");
			String statement = statementSB.getAttribute("QUERY").toString();

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			// imposto i parametri
			ArrayList parameters = new ArrayList(4);
			parameters.add(conn.createDataField("result", Types.VARCHAR, risultato));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("CodiceFiscale", Types.VARCHAR, cf));
			command.setAsInputParameters(1);
			parameters.add(conn.createDataField("DataUltimaMod", Types.VARCHAR, dataModifica));
			command.setAsInputParameters(2);
			parameters.add(conn.createDataField("tracciatoXML", Types.CLOB, tracciatoXML));
			command.setAsOutputParameters(3);

			// eseguo!!
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// VALORE DI RITORNO (result)
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			if (df.getObjectValue() != null) {
				risultato = df.getStringValue();
			}

			conn.commitTransaction();

			// IL CLOB
			// contiene l'xml prodotto dalla stored procedure
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();

			this.opResult = risultato;
			tracciatoXML = (CLOB) df.getObjectValue();

			return tracciatoXML;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Errore nell'elaborazione anagrafica");
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}

	protected CLOB getCFModified(String dataOra) throws Exception {

		this.opResult = null;

		String risultato = "";

		CLOB cfList = null;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		try {
			// richiamo l'apposita stored procedure per l'elaborazione del file
			// xml
			conn = transEx.getDataConnection();
			SourceBean statementSB = (SourceBean) config.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT", "NAME",
					"SILPROLABOR_GETMODIFIEDCFLIST");
			String statement = statementSB.getAttribute("QUERY").toString();

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			// imposto i parametri
			ArrayList parameters = new ArrayList(3);
			parameters.add(conn.createDataField("result", Types.VARCHAR, risultato));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("dataControllo", Types.VARCHAR, dataOra));
			command.setAsInputParameters(1);
			parameters.add(conn.createDataField("cfList", Types.CLOB, cfList));
			command.setAsOutputParameters(2);

			// eseguo!!
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// VALORE DI RITORNO (result)
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			if (df.getObjectValue() != null) {
				risultato = df.getStringValue();
			}

			conn.commitTransaction();
			// IL CLOB
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();

			this.opResult = risultato;
			cfList = (CLOB) df.getObjectValue();

			return cfList;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Errore nell'elaborazione della lista dei codici fiscali modificati");
		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

	/**
	 * saveCM
	 * 
	 * AGGIORNAMENTO DEL COLLOCAMENTO MIRATO - L68
	 * 
	 * procedura per la chiamata alla stored procedure che salva i dati del collocamento mirato provenienti da ProLabor.
	 * 
	 * @param cf
	 *            codice fiscale del soggetto
	 * @param codCmTipoIscr
	 *            tipo di iscrizione
	 * @param codAccertSanitario
	 *            tipo di accertamento sanitario
	 * @param numPercInvalidita
	 *            percentuale di invalidità
	 * @param codTipoInvalidita
	 *            tipo di invalidità
	 * @param strDatAccertSanitario
	 *            data dell'accertamento sanitario
	 * @param strDatDataInizio
	 *            data di inizio del collocamento mirato
	 * @return boolean (sempre true - il valore di ritorno *non è usato!*)
	 * @throws Exception
	 */
	protected void saveCM(String cf, String codCmTipoIscr, String codAccertSanitario, String numPercInvalidita,
			String codTipoInvalidita, String strDatAccertSanitario, String strDatDataInizio, String dataIscr,
			String dataAnzianita) throws Exception {

		// reset opResult
		this.opResult = null;

		String risultato = "";
		// DataConnectionManager dcm=null;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;

		// scrivo il log
		conn = transEx.getDataConnection();
		conn.initTransaction();

		try {
			// dcm = DataConnectionManager.getInstance();
			// conn = dcm.getConnection(Values.DB_SIL_DATI);

			SourceBean statementSB = (SourceBean) config.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT", "NAME",
					"SILPROLABOR_SAVECM");
			String statement = statementSB.getAttribute("QUERY").toString();

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			// imposto i parametri
			ArrayList parameters = new ArrayList(8);
			parameters.add(conn.createDataField("result", Types.VARCHAR, risultato));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("codFis", Types.VARCHAR, cf));
			command.setAsInputParameters(1);
			parameters.add(conn.createDataField("codCmTipoIscr", Types.VARCHAR, codCmTipoIscr));
			command.setAsInputParameters(2);
			parameters.add(conn.createDataField("codAccertSanitario", Types.VARCHAR, codAccertSanitario));
			command.setAsInputParameters(3);
			parameters.add(conn.createDataField("numPercInvalidita", Types.VARCHAR, numPercInvalidita));
			command.setAsInputParameters(4);
			parameters.add(conn.createDataField("codTipoInvalidita", Types.VARCHAR, codTipoInvalidita));
			command.setAsInputParameters(5);
			parameters.add(conn.createDataField("strDatAccertSanitario", Types.VARCHAR, strDatAccertSanitario));
			command.setAsInputParameters(6);
			parameters.add(conn.createDataField("strDatDataInizio", Types.VARCHAR, strDatDataInizio));
			command.setAsInputParameters(7);
			parameters.add(conn.createDataField("strDatDataIscr", Types.VARCHAR, dataIscr));
			command.setAsInputParameters(8);
			parameters.add(conn.createDataField("strDatDataAnzianita", Types.VARCHAR, dataAnzianita));
			command.setAsInputParameters(9);
			// eseguo!!
			dr = command.execute(parameters);

			// VALORE DI RITORNO
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			risultato = df.getStringValue();
			if (df.getObjectValue() != null) {
				this.opResult = risultato;
			}
			conn.commitTransaction();

		} catch (Exception ex) {
			conn.rollBackTransaction();
			ex.printStackTrace();
			throw new Exception("Errore nell'elaborazione del Collocamento Mirato: " + ex.getMessage());

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

	protected void saveMob(String cf, String tipoLista, String datInizio, String datFine, String dataAss,
			String dataCes, String flgIndennita, String datInizioIndennita, String datFineIndennita, String maxDiff,
			String motivoFine, String qualifica, String dataCRT, String provCRT, String codFisAzienda)
			throws Exception {

		// reset opResult
		this.opResult = null;

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		String cdnLav = "";
		conn = transEx.getDataConnection();
		conn.initTransaction();
		try {
			try {
				SourceBean statementSB = (SourceBean) config.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT",
						"NAME", "SILPROLABOR_SAVEMOB");
				String statement = statementSB.getAttribute("QUERY").toString();
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);
				// imposto i parametri
				ArrayList parameters = new ArrayList(15);
				parameters.add(conn.createDataField("cf", Types.VARCHAR, cf));
				command.setAsInputParameters(0);
				parameters.add(conn.createDataField("tipoMob", Types.VARCHAR, tipoLista));
				command.setAsInputParameters(1);
				parameters.add(conn.createDataField("strInizio", Types.VARCHAR, datInizio));
				command.setAsInputParameters(2);
				parameters.add(conn.createDataField("strFine", Types.VARCHAR, datFine));
				command.setAsInputParameters(3);
				parameters.add(conn.createDataField("strdatAss", Types.VARCHAR, dataAss));
				command.setAsInputParameters(4);
				parameters.add(conn.createDataField("strdataCess", Types.VARCHAR, dataCes));
				command.setAsInputParameters(5);
				parameters.add(conn.createDataField("flgIndenn", Types.VARCHAR, flgIndennita));
				command.setAsInputParameters(6);
				parameters.add(conn.createDataField("strInizioIndennita", Types.VARCHAR, datInizioIndennita));
				command.setAsInputParameters(7);
				parameters.add(conn.createDataField("strFineIndennita", Types.VARCHAR, datFineIndennita));
				command.setAsInputParameters(8);
				parameters.add(conn.createDataField("strMaxDiff", Types.VARCHAR, maxDiff));
				command.setAsInputParameters(9);
				parameters.add(conn.createDataField("motivoFine", Types.VARCHAR, motivoFine));
				command.setAsInputParameters(10);
				parameters.add(conn.createDataField("codMansione", Types.VARCHAR, qualifica));
				command.setAsInputParameters(11);
				parameters.add(conn.createDataField("strdatCRT", Types.VARCHAR, dataCRT));
				command.setAsInputParameters(12);
				parameters.add(conn.createDataField("provCRT", Types.VARCHAR, provCRT));
				command.setAsInputParameters(13);
				parameters.add(conn.createDataField("cfAz", Types.VARCHAR, codFisAzienda));
				command.setAsInputParameters(14);

				// eseguo!!
				dr = command.execute(parameters);

				// recupero il cdnLav
				cdnLav = findCdnLavoratore(cf, conn);

				// chiamo l'aggiornamento dello stato occupazionale

				MobilitaManager mobMan = new MobilitaManager();
				if ((datFine != null) && !datFine.equals("")) {
					datFine = datFine.replace('-', '/');
				}

				StatoOccupazionaleBean statoOcc = mobMan.aggiornaDaMobilita(datInizio.replace('-', '/'), datFine,
						cdnLav, new Integer(199), false, transEx, null);
				conn.commitTransaction();

				// aggiorno i valori dello stato occupazionale (utili per il
				// log)
				aggiornaValoriStatoOccPerLog(statoOcc);
				this.opResult = "OK"; // questa operazione non restituisce
										// alcun codice di errore applicativo
				// sarà meglio fare in modo che anche la mobilità abbia un
				// codice di errore applicativo.

			}

			catch (MobilitaException mobEx) {
				MobilitaManager mobMan = new MobilitaManager();
				if ((datFine != null) && !datFine.equals("")) {
					datFine = datFine.replace('-', '/');
				}
				StatoOccupazionaleBean statoOcc = mobMan.aggiornaDaMobilita(datInizio.replace('-', '/'), datFine,
						cdnLav, new Integer(199), true, transEx, null);
				conn.commitTransaction();
				// aggiorno i valori dello stato occupazionale (utili per il
				// log)
				aggiornaValoriStatoOccPerLog(statoOcc);
				this.forzaturaChiusuraMobilita = true;
				this.opResult = "OK";
			}

			catch (ProTrasfoException proTrasfEx) {
				this.opResult = "ERR";
				conn.rollBackTransaction();
				int code = proTrasfEx.getCode();
				if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
					throw new Exception(
							"Impossibile calcolare il nuovo stato occupazionale:proroga senza movimento collegato");
				} else if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
					throw new Exception(
							"Impossibile calcolare il nuovo stato occupazionale:trasformazione t.d. senza movimento collegato");
				}
			}

			catch (ControlliException cEx) {
				int code = cEx.getCode();
				MobilitaManager mobMan = new MobilitaManager();
				if ((datFine != null) && !datFine.equals("")) {
					datFine = datFine.replace('-', '/');
				}
				StatoOccupazionaleBean statoOcc = mobMan.aggiornaDaMobilita(datInizio.replace('-', '/'), datFine,
						cdnLav, new Integer(199), true, transEx, null);
				conn.commitTransaction();
				// aggiorno i valori dello stato occupazionale (utili per il
				// log)
				aggiornaValoriStatoOccPerLog(statoOcc);
				this.opResult = "OK";
			}

			catch (Exception ex) {
				this.opResult = "ERR";
				conn.rollBackTransaction();
				ex.printStackTrace();
				throw new Exception("Errore nell'elaborazione della Mobilità: " + ex.getMessage());
			}
		}

		catch (Exception ex) {
			this.opResult = "ERR";
			conn.rollBackTransaction();
			ex.printStackTrace();
			throw new Exception("Errore nell'elaborazione della Mobilità: " + ex.getMessage());
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}

	protected void processCLA181(String cf) throws Exception {

		String codStatoOcc = "";
		String dataAnzianita = "";

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		String cdnLav = "";
		conn = transEx.getDataConnection();
		conn.initTransaction();
		try {
			SourceBean statementSB = (SourceBean) config.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT", "NAME",
					"SILPROLABOR_GETSTATOOCCUPAZIONALE");
			String statement = statementSB.getAttribute("QUERY").toString();

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(statement);

			// imposto i parametri
			ArrayList parameters = new ArrayList(3);
			parameters.add(conn.createDataField("cf", Types.VARCHAR, cf));
			command.setAsInputParameters(0);
			parameters.add(conn.createDataField("statoOcc", Types.VARCHAR, codStatoOcc));
			command.setAsOutputParameters(1);
			parameters.add(conn.createDataField("dataAnzianita", Types.VARCHAR, dataAnzianita));
			command.setAsOutputParameters(2);

			// eseguo!!
			dr = command.execute(parameters);
			conn.commitTransaction();
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// VALORE DI RITORNO (result)
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			if (df.getObjectValue() != null) {
				codStatoOcc = df.getStringValue();
				this.statoOccCOD = normalizzaStatoOcc(codStatoOcc.trim());
			}

			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			if (df.getObjectValue() != null) {
				dataAnzianita = df.getStringValue();
				this.statoOccDATAANZ = dataAnzianita;
			}

			this.opResult = "OK";
		} catch (Exception ex) {
			throw new Exception("Errore nell'elaborazione dello stato occupazionale");

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

	/*
	 * 
	 * METODI GETTER
	 * 
	 */

	protected String getOpResult() {
		return this.opResult;
	}

	protected boolean getForzaturaChiusuraMobilita() throws Exception {
		return this.forzaturaChiusuraMobilita;
	}

	protected String getStatoOccCOD() throws Exception {

		/*
		 * if (this.statoOccCF!=null) { if (cf.equalsIgnoreCase(this.statoOccCF)) {
		 */
		return this.statoOccCOD;
		/*
		 * } } else {
		 * 
		 * throw new Exception("Nessun dato elaborato"); }
		 * 
		 * return null;
		 */

	}

	protected String getStatoOccDATAANZ() throws Exception {

		/*
		 * if (this.statoOccCF!=null) { if (cf.equalsIgnoreCase(this.statoOccCF)) {
		 */
		return this.statoOccDATAANZ;
		/*
		 * } } else { throw new Exception("Nessun dato elaborato"); }
		 * 
		 * return null;
		 */
	}

}
