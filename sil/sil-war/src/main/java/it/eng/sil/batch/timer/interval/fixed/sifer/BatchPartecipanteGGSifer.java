package it.eng.sil.batch.timer.interval.fixed.sifer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.coop.webservices.sifer.ErroreSifer;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.PartecipanteGGUtils;

@Singleton
public class BatchPartecipanteGGSifer extends FixedTimerBatch {

	private static final String QUERY_RICERCA_LAVORATORI_DYNAMIC = "SELECT DISTINCT PATTO.CDNLAVORATORE "
			+ " FROM AM_PATTO_LAVORATORE PATTO "
			+ " INNER JOIN or_colloquio coll on (PATTO.cdnlavoratore = coll.cdnlavoratore) "
			+ " INNER JOIN or_percorso_concordato per on (coll.prgcolloquio = per.prgcolloquio) "
			+ " INNER JOIN am_lav_patto_scelta scelta on (PATTO.prgpattolavoratore = scelta.prgpattolavoratore "
			+ " 	and per.prgpercorso = to_number(scelta.strchiavetabella) " + " 	and scelta.codlsttab = 'OR_PER') "
			+ " INNER JOIN de_servizio on (coll.codservizio = de_servizio.codservizio) "
			+ " INNER JOIN de_azione az on (per.prgazioni = az.prgazioni) " + " WHERE (PATTO.codstatoatto = 'PR') "
			+ " and (nvl(de_servizio.flgServizioSifer, 'N') = 'S') " + " and (nvl(az.flgformazione, 'N') = 'S')";

	private static final String QUERY_RICERCA_LAVORATORI = " SELECT distinct AN.CDNLAVORATORE "
			+ " FROM OR_PERCORSO_CONCORDATO PC INNER JOIN OR_COLLOQUIO CO ON PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO "
			+ " INNER JOIN AN_LAVORATORE AN ON AN.CDNLAVORATORE = CO.CDNLAVORATORE "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI "
			+ " INNER JOIN DE_AZIONE_RAGG RAGG ON AZ.PRGAZIONERAGG = RAGG.PRGAZIONIRAGG "
			+ " WHERE az.codazionesifer in ('A02', 'A2R') and az.flgformazione = 'S' and pc.codesito = 'FC' and nvl(RAGG.codmonopacchetto, 'ZZ') != 'OR' "
			+ " union " + " SELECT distinct AN.CDNLAVORATORE "
			+ " FROM OR_PERCORSO_CONCORDATO PC INNER JOIN OR_COLLOQUIO CO ON PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO "
			+ " INNER JOIN AN_LAVORATORE AN ON AN.CDNLAVORATORE = CO.CDNLAVORATORE "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI "
			+ " INNER JOIN DE_AZIONE_RAGG RAGG ON AZ.PRGAZIONERAGG = RAGG.PRGAZIONIRAGG "
			+ " WHERE az.codazionesifer in ('A01', 'A02') and az.flgformazione = 'S' and pc.codesito = 'FC' and nvl(RAGG.codmonopacchetto, 'ZZ') = 'OR' and exists ("
			+ " select 1 from OR_PERCORSO_CONCORDATO PC1 INNER JOIN OR_COLLOQUIO CO1 ON PC1.PRGCOLLOQUIO = CO1.PRGCOLLOQUIO "
			+ " INNER JOIN DE_AZIONE AZ1 ON AZ1.PRGAZIONI = PC1.PRGAZIONI "
			+ " INNER JOIN DE_AZIONE_RAGG RAGG1 ON AZ1.PRGAZIONERAGG = RAGG1.PRGAZIONIRAGG "
			+ " WHERE CO1.cdnlavoratore = CO.CDNLAVORATORE and az1.codazionesifer in ('A01', 'A02') and az1.flgformazione = 'S' "
			+ " and az1.codazionesifer != az.codazionesifer and pc1.codesito = 'FC' and nvl(RAGG1.codmonopacchetto, 'ZZ') = 'OR') ";

	private static final String QUERY_CHECK_PERCORSOCONCORDATO = " SELECT COUNT(1) AS NUMPERCORSO  "
			+ " FROM OR_PERCORSO_CONCORDATO PC " + " INNER JOIN OR_COLLOQUIO CO ON PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO "
			+ " INNER JOIN AN_LAVORATORE AN ON AN.CDNLAVORATORE = CO.CDNLAVORATORE "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI " + " WHERE AZ.FLGFORMAZIONE = 'S' "
			+ " AND CO.CDNLAVORATORE = ? " + " AND TRUNC(PC.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') ";

	private static final String QUERY_CHECK_MOVIMENTO_AVV = "SELECT COUNT(1) as NUMMOVAVV" + " FROM am_movimento am "
			+ " WHERE am.CDNLAVORATORE = ? " + " AND am.CODTIPOMOV= 'AVV' "
			+ " AND TRUNC(am.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy')";

	private static final String QUERY_CHECK_PATTO = "SELECT COUNT(1) AS NUMPATTO " + " FROM AM_PATTO_LAVORATORE PL "
			+ " INNER JOIN AM_DOCUMENTO_COLL DC ON (TO_NUMBER(DC.STRCHIAVETABELLA) = PL.PRGPATTOLAVORATORE) "
			+ " INNER JOIN AM_DOCUMENTO DO ON (DO.PRGDOCUMENTO = DC.PRGDOCUMENTO) "
			+ " WHERE PL.CDNLAVORATORE = ? AND ( (PL.CODTIPOPATTO in ('MGG','MGO30','MGO45','MINAT','MGGU')) or "
			+ "         (getEsisteProgrammaPatto(PL.prgpattolavoratore, '''MGG'',''MGO30'',''MGO45'',''MINAT'',''MGGU''') > 0) "
			+ "       ) " + " AND DO.CODTIPODOCUMENTO in ('PT297', 'ACLA') " + " AND PL.CODSTATOATTO = 'PR' "
			+ " AND DO.CODSTATOATTO = 'PR' " + " AND TRUNC(PL.DTMMOD) >= TO_DATE(?, 'dd/mm/yyyy') ";

	private static final String QUERY_CHECK_PATTO_DYNAMIC = "SELECT COUNT(1) AS NUMPATTO "
			+ " FROM AM_PATTO_LAVORATORE PL "
			+ " INNER JOIN or_colloquio coll on (PL.cdnlavoratore = coll.cdnlavoratore) "
			+ " INNER JOIN or_percorso_concordato per on (coll.prgcolloquio = per.prgcolloquio) "
			+ " INNER JOIN am_lav_patto_scelta scelta on (PL.prgpattolavoratore = scelta.prgpattolavoratore "
			+ " 	and per.prgpercorso = to_number(scelta.strchiavetabella) " + " 	and scelta.codlsttab = 'OR_PER') "
			+ " INNER JOIN de_servizio on (coll.codservizio = de_servizio.codservizio) "
			+ " INNER JOIN AM_DOCUMENTO_COLL DC ON (TO_NUMBER(DC.STRCHIAVETABELLA) = PL.PRGPATTOLAVORATORE) "
			+ " INNER JOIN AM_DOCUMENTO DO ON (DO.PRGDOCUMENTO = DC.PRGDOCUMENTO) " + " WHERE PL.CDNLAVORATORE = ? "
			+ " AND DO.CODTIPODOCUMENTO in ('PT297', 'ACLA') " + " AND PL.CODSTATOATTO = 'PR' "
			+ " AND DO.CODSTATOATTO = 'PR' " + " AND nvl(de_servizio.flgServizioSifer, 'N') = 'S' "
			+ " AND TRUNC(PL.DTMMOD) >= TO_DATE(?, 'dd/mm/yyyy') ";

	private static final String QUERY_CHECK_MOVIMENTI = "SELECT COUNT(1) AS NUMMOV " + " FROM AM_MOVIMENTO M "
			+ " WHERE M.CDNLAVORATORE = ? " + " AND TRUNC(M.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') "
			+ " AND M.CODSTATOATTO = 'PR' ";

	private static final String QUERY_CHECK_ACCORPAMENTO = "SELECT COUNT(1) AS NUMACCORP "
			+ " FROM AN_LAVORATORE_ACCORPA AC" + " WHERE (AC.CDNLAVORATORE = ? " + " OR AC.CDNLAVORATOREACCORPATO = ?) "
			+ " AND TRUNC(AC.DTMINS) >= TO_DATE(?,'dd/mm/yyyy') ";

	private static final String QUERY_DATA_BATCH_SIFER = "select to_char(DATETL, 'dd/mm/yyyy') as DATETL "
			+ " from ts_monitoraggio " + " where CODAMBITO = 'GG_SIFER' ";

	private static final String QUERY_UPDATE_DATA_BATCH_SIFER = "update ts_monitoraggio set dateTL = sysdate where codambito = 'GG_SIFER' ";

	private static final String QUERY_ORA_EXEC_BATCH = "select ORAEXECBATCHSIFER, CODREGIONESIL from ts_generale where prggenerale = 1 ";

	static Logger logger = Logger.getLogger(BatchPartecipanteGGSifer.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "21", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {

			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			Connection connection = null;
			Statement statement = null;
			boolean oldAutoCommit = false;

			try {

				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(true);

				ResultSet resultSet = null;
				boolean checkInvio = false;

				if (connection != null) {

					boolean startScheduling = false;
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());

					String oraEsecuzione = null;
					String codRegioneSil = null;
					PreparedStatement preparedStatementOraExecBatch = null;
					preparedStatementOraExecBatch = connection.prepareStatement(QUERY_ORA_EXEC_BATCH);
					ResultSet resultSetOraExecBatch = preparedStatementOraExecBatch.executeQuery();
					if (resultSetOraExecBatch != null) {
						while (resultSetOraExecBatch.next()) {
							oraEsecuzione = resultSetOraExecBatch.getString("ORAEXECBATCHSIFER");
							codRegioneSil = resultSetOraExecBatch.getString("CODREGIONESIL");
						}
					}
					preparedStatementOraExecBatch.close();
					resultSetOraExecBatch.close();

					if (oraEsecuzione != null && c.get(Calendar.HOUR_OF_DAY) == (Integer.valueOf(oraEsecuzione))) {
						startScheduling = true;
					}

					if (startScheduling) {
						logger.info("Batch Notturno BatchPartecipanteGGSifer START effectiveStartDate:"
								+ df.format(effectiveStartDate));

						String dataEsecuzioneBatch = null;
						PreparedStatement preparedStatementDataBatch = null;
						preparedStatementDataBatch = connection.prepareStatement(QUERY_DATA_BATCH_SIFER);
						ResultSet resultSetDataBatch = preparedStatementDataBatch.executeQuery();
						if (resultSetDataBatch != null) {
							while (resultSetDataBatch.next()) {
								dataEsecuzioneBatch = resultSetDataBatch.getString("DATETL");
							}
						}
						resultSetDataBatch.close();

						BigDecimal cdnLavoratore = null;
						// QUERY RICERCA LAVORATORI DA SPEDIRE A SIFER
						PreparedStatement preparedStatement = null;
						if (codRegioneSil != null && codRegioneSil.equalsIgnoreCase(Properties.TN)) {
							preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI_DYNAMIC);
						} else {
							preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI);
						}

						resultSet = preparedStatement.executeQuery();

						if (resultSet != null && dataEsecuzioneBatch != null) {
							while (resultSet.next()) {
								try {
									checkInvio = false;
									cdnLavoratore = resultSet.getBigDecimal("CDNLAVORATORE");

									gestisciInvioLavoratore(checkInvio, dataEsecuzioneBatch, cdnLavoratore,
											codRegioneSil);

								} catch (Exception e) {
									logger.error("Errore Batch Notturno BatchPartecipanteGGSifer: ", e);
								}
							}
						}

						// aggiorna la data di esecuzione del batch
						aggiornaDataBatch(connection);

						preparedStatement.close();
						resultSet.close();

						final Date stopDate = new Date();
						logger.info("Batch Notturno BatchPartecipanteGGSifer STOP at:" + df.format(stopDate));
					} else {
						logger.info("Batch Notturno BatchPartecipanteGGSifer NON SCHEDULABILE: Carico non specificato");
					}
				}

			} catch (Exception e) {
				logger.error("Errore Batch Notturno BatchPartecipanteGGSifer: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchPartecipanteGGSifer] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void gestisciInvioLavoratore(boolean checkInvio, String dataEsecuzioneBatch, BigDecimal cdnLavoratore,
			String codRegioneSil) throws Exception {
		logger.debug(
				"BatchPartecipanteGGSifer inizio gestisciInvioLavoratore cdnlavoratore:" + cdnLavoratore.toString());
		// istanzio una nuova connessione per ogni lavoratore e la chiudo in fondo all'esecuzione
		Connection connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
				.getInternalConnection();
		try {
			if (!checkInvio) {
				// verifica esistenza patto e/o profilatura
				BigDecimal numPerc = null;
				PreparedStatement preparedStatementCheckPerc = null;
				preparedStatementCheckPerc = connection.prepareStatement(QUERY_CHECK_PERCORSOCONCORDATO);
				preparedStatementCheckPerc.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckPerc.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckPerc = preparedStatementCheckPerc.executeQuery();
				if (resultSetCheckPerc != null) {
					while (resultSetCheckPerc.next()) {
						numPerc = resultSetCheckPerc.getBigDecimal("NUMPERCORSO");
					}
				}
				preparedStatementCheckPerc.close();
				resultSetCheckPerc.close();

				if (numPerc != null && numPerc.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			if (!checkInvio) {
				// verifica esistenza patto e/o profilatura
				BigDecimal numPatto = null;
				PreparedStatement preparedStatementCheckPatto = null;
				if (codRegioneSil != null && codRegioneSil.equalsIgnoreCase(Properties.TN)) {
					preparedStatementCheckPatto = connection.prepareStatement(QUERY_CHECK_PATTO_DYNAMIC);
				} else {
					preparedStatementCheckPatto = connection.prepareStatement(QUERY_CHECK_PATTO);
				}
				preparedStatementCheckPatto.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckPatto.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckPatto = preparedStatementCheckPatto.executeQuery();
				if (resultSetCheckPatto != null) {
					while (resultSetCheckPatto.next()) {
						numPatto = resultSetCheckPatto.getBigDecimal("NUMPATTO");
					}
				}
				preparedStatementCheckPatto.close();
				resultSetCheckPatto.close();

				if (numPatto != null && numPatto.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			if (!checkInvio) {
				// verifica esistenza mov tirocinio
				BigDecimal numMovTir = null;
				PreparedStatement preparedStatementCheckMov = null;
				preparedStatementCheckMov = connection.prepareStatement(QUERY_CHECK_MOVIMENTO_AVV);
				preparedStatementCheckMov.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckMov.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckMov = preparedStatementCheckMov.executeQuery();
				if (resultSetCheckMov != null) {
					while (resultSetCheckMov.next()) {
						numMovTir = resultSetCheckMov.getBigDecimal("NUMMOVAVV");
					}
				}
				preparedStatementCheckMov.close();
				resultSetCheckMov.close();

				if (numMovTir != null && numMovTir.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			if (!checkInvio) {
				// verifica esistenza nuovi movimenti
				BigDecimal numMov = null;
				PreparedStatement preparedStatementCheckNewMov = null;
				preparedStatementCheckNewMov = connection.prepareStatement(QUERY_CHECK_MOVIMENTI);
				preparedStatementCheckNewMov.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckNewMov.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckNewMov = preparedStatementCheckNewMov.executeQuery();
				if (resultSetCheckNewMov != null) {
					while (resultSetCheckNewMov.next()) {
						numMov = resultSetCheckNewMov.getBigDecimal("NUMMOV");
					}
				}
				preparedStatementCheckNewMov.close();
				resultSetCheckNewMov.close();

				if (numMov != null && numMov.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			if (!checkInvio) {
				// verifica esistenza nuovi movimenti
				BigDecimal numAccorp = null;
				PreparedStatement preparedStatementCheckAccorp = null;
				preparedStatementCheckAccorp = connection.prepareStatement(QUERY_CHECK_ACCORPAMENTO);
				preparedStatementCheckAccorp.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckAccorp.setBigDecimal(2, cdnLavoratore);
				preparedStatementCheckAccorp.setString(3, dataEsecuzioneBatch);
				ResultSet resultSetCheckNewAccorp = preparedStatementCheckAccorp.executeQuery();
				if (resultSetCheckNewAccorp != null) {
					while (resultSetCheckNewAccorp.next()) {
						numAccorp = resultSetCheckNewAccorp.getBigDecimal("NUMACCORP");
					}
				}
				preparedStatementCheckAccorp.close();
				resultSetCheckNewAccorp.close();

				if (numAccorp != null && numAccorp.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			// per ogni lavoratore trovato richiamo l'invio dei dati
			// a SIFER
			// stesso modulo del pulsante nella lista colloquio
			QueryExecutorObject qExec = null;
			DataConnection dc = null;

			if (checkInvio) {
				try {
					qExec = PartecipanteGGUtils.getQueryExecutorObject();
					dc = qExec.getDataConnection();
					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_anlav"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("_ENCRYPTER_KEY_", Types.VARCHAR,
							System.getProperty("_ENCRYPTER_KEY_")));
					params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(params);
					SourceBean anLavBeanRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_perm_sogg"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsPerm = new ArrayList<DataField>();
					paramsPerm.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsPerm);
					SourceBean permSoggBeanRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_codprovincia"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsProv = new ArrayList<DataField>();
					qExec.setInputParameters(paramsProv);
					SourceBean codProvinciaBeanRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_titstudio"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsTit = new ArrayList<DataField>();
					paramsTit.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsTit);
					SourceBean titStudioBeanRows = (SourceBean) qExec.exec();

					if (codRegioneSil != null && codRegioneSil.equalsIgnoreCase(Properties.TN)) {
						qExec.setStatement(
								SQLStatements.getStatement("GET_Partecipante_GG_DatiProfiling_Patto_Dynamic"));
					} else {
						qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiProfiling_patto"));
					}
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsPatto = new ArrayList<DataField>();
					paramsPatto.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					paramsPatto.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					paramsPatto.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsPatto);
					SourceBean pattoBeanRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_Movimentazione"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsMov = new ArrayList<DataField>();
					paramsMov.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsMov);
					SourceBean movimentazioneBeanRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_PattoAttivazione_Reg"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsPattoReg = new ArrayList<DataField>();
					paramsPattoReg.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsPattoReg);
					SourceBean pattoRegionaleRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_DATI_PROFILING_DID"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsConferimento = new ArrayList<DataField>();
					paramsConferimento.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsConferimento);
					SourceBean conferimentoRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_CredWS"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsWS = new ArrayList<DataField>();
					qExec.setInputParameters(paramsWS);
					SourceBean wsRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_PoliticheAttiveNonValide"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsPolNoVal = new ArrayList<DataField>();
					paramsPolNoVal.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsPolNoVal);
					SourceBean politicheAttiveNonValideBeanRows = (SourceBean) qExec.exec();

					logger.debug("BatchPartecipanteGGSifer chiamata sendPartecipanteGG cdnlavoratore:"
							+ cdnLavoratore.toString());
					ErroreSifer erroreSifer = PartecipanteGGUtils.sendPartecipanteGG(cdnLavoratore, anLavBeanRows,
							permSoggBeanRows, codProvinciaBeanRows, titStudioBeanRows, pattoBeanRows,
							movimentazioneBeanRows, wsRows, pattoRegionaleRows, conferimentoRows);
					logger.debug(
							"BatchPartecipanteGGSifer return sendPartecipanteGG erroreSifer:" + erroreSifer.errCod);

					String msgErr = "";
					if (erroreSifer.errCod != 0) {
						/* traccio l'errore in DB */
						PartecipanteGGUtils.tracciaErrore(connection, cdnLavoratore, erroreSifer);

						// inserimento EVIDENZA
						if (MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE == erroreSifer.errCod) {
							int koWs = MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE;
							msgErr = MessageBundle.getMessage(String.valueOf(koWs));
							msgErr = msgErr
									+ "Una volta corretti, si prega di inviare a Formazione attraverso il relativo pulsante 'Inoltra a Formazione'";
						} else {
							msgErr = erroreSifer.erroreEsteso;
						}

						insertEvidenzaErr(connection, cdnLavoratore, msgErr);
					}

					/*
					 * mostro l'elenco delle azioni non inviate, sempre escluso il caso in cui non abbia inviato niente
					 * a causa dei dati obbligatori mancanti
					 */
					if (erroreSifer.errCod != MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE) {
						// aggiungiamo possibile polite attive non
						// valise
						reportPoliticheAttiveNonValide(politicheAttiveNonValideBeanRows, connection, cdnLavoratore);
					}
				} catch (Exception ex) {
					logger.error("BatchPartecipanteGGSifer errore: " + ex);
				} finally {
					if (dc != null) {
						dc.close();
					}
				}
			}
		} catch (Throwable e) {
			logger.error("BatchPartecipanteGGSifer errore: " + e);
		} finally {
			logger.debug(
					"BatchPartecipanteGGSifer fine gestisciInvioLavoratore cdnlavoratore:" + cdnLavoratore.toString());
			releaseResources(connection, null, true);
		}
	}

	private void releaseResources(Connection connection, Statement statement, boolean oldAutoCommit) {

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}

		if (connection != null) {
			try {
				connection.setAutoCommit(oldAutoCommit);
				connection.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}

	}

	public static void insertEvidenzaErr(Connection connection, BigDecimal cdnLavoratore, String msgErr) {
		logger.debug("BatchPartecipanteGGSifer inserimento evidenza insertEvidenzaErr cdnlavoratore:"
				+ cdnLavoratore.toString());
		PreparedStatement preparedStatementIns = null;

		try {
			String statementInsertEv = "insert into AN_EVIDENZA (PRGEVIDENZA, CDNLAVORATORE, "
					+ " DATDATASCAD, STREVIDENZA, PRGTIPOEVIDENZA, CDNUTINS, DTMINS, CDNUTMOD, DTMMOD) "
					+ " values (S_AN_EVIDENZA.nextVal, ?, " + " to_date('01/01/2100','dd/mm/yyyy'), "
					+ " ? , (select prgtipoevidenza from de_tipo_evidenza where codtipoevidenza = 'EG'),"
					+ " 365, sysdate, " + " 365, sysdate)";

			preparedStatementIns = connection.prepareStatement(statementInsertEv);
			preparedStatementIns.setBigDecimal(1, cdnLavoratore);
			preparedStatementIns.setString(2, msgErr);
			preparedStatementIns.executeUpdate();

			preparedStatementIns.close();

		} catch (Exception e) {
			logger.error("Errore: inserimento evidenza batch partecipante GG SIFER", e);
		} finally {
			if (preparedStatementIns != null) {
				try {
					preparedStatementIns.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}
	}

	public static void insertEvidenzaInvioOk(Connection connection, BigDecimal cdnLavoratore, String msgErr) {

		PreparedStatement preparedStatementIns = null;

		try {
			String statementInsertEv = "insert into AN_EVIDENZA (PRGEVIDENZA, CDNLAVORATORE, "
					+ " DATDATASCAD, STREVIDENZA, PRGTIPOEVIDENZA, CDNUTINS, DTMINS, CDNUTMOD, DTMMOD) "
					+ " values (S_AN_EVIDENZA.nextVal, ?, " + " to_date('01/01/2100','dd/mm/yyyy'), "
					+ " ? , (select prgtipoevidenza from de_tipo_evidenza where codtipoevidenza = 'IG'),"
					+ " 365, sysdate, " + " 365, sysdate)";

			preparedStatementIns = connection.prepareStatement(statementInsertEv);
			preparedStatementIns.setBigDecimal(1, cdnLavoratore);
			preparedStatementIns.setString(2, msgErr);
			preparedStatementIns.executeUpdate();

			preparedStatementIns.close();

		} catch (Exception e) {
			logger.error("Errore: inserimento evidenza batch partecipante GG SIFER", e);
		} finally {
			if (preparedStatementIns != null) {
				try {
					preparedStatementIns.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}
	}

	public static void aggiornaDataBatch(Connection connection) {
		logger.debug("BatchPartecipanteGGSifer aggiornamento data chiamata batch");
		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(QUERY_UPDATE_DATA_BATCH_SIFER);
			preparedStatement.executeUpdate();

			preparedStatement.close();

		} catch (Exception e) {
			logger.error("Errore: aggiornamento data batch partecipante GG SIFER", e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}
	}

	private void reportPoliticheAttiveNonValide(SourceBean politicheAttiveNonValideBeanRows, Connection connection,
			BigDecimal cdnLavoratore) {
		if (politicheAttiveNonValideBeanRows != null) {
			Vector politicheAttiveNonValideBeanVector = politicheAttiveNonValideBeanRows.getAttributeAsVector("ROW");

			String descrizione = null;

			for (int i = 0; i < politicheAttiveNonValideBeanVector.size(); i++) {
				SourceBean politicheAttiveNonValideBeanRow = (SourceBean) politicheAttiveNonValideBeanVector
						.elementAt(i);
				descrizione = (String) politicheAttiveNonValideBeanRow.getAttribute("STRDESCRIZIONE");

				String msgEvidenza = "La politica attiva " + descrizione
						+ " non e' stata inviata per esito non conforme alla formazione.";
				insertEvidenzaErr(connection, cdnLavoratore, msgEvidenza);
			}
		}
	}
}
