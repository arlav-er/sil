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
import java.util.Date;
import java.util.List;

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
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.coop.webservices.sifer.ErroreSifer;
import it.eng.sil.module.sifer.EnteAccreditatoUtils;

@Singleton
public class BatchEntiAccreditatiSifer extends FixedTimerBatch {

	private static final String QUERY_RICERCA_LAVORATORI = "SELECT DISTINCT PATTO.CDNLAVORATORE "
			+ " FROM AM_PATTO_LAVORATORE PATTO "
			+ " INNER JOIN or_colloquio coll on (PATTO.cdnlavoratore = coll.cdnlavoratore) "
			+ " INNER JOIN or_percorso_concordato per on (coll.prgcolloquio = per.prgcolloquio) "
			+ " INNER JOIN am_lav_patto_scelta scelta on (PATTO.prgpattolavoratore = scelta.prgpattolavoratore "
			+ " 	and per.prgpercorso = to_number(scelta.strchiavetabella) " + " 	and scelta.codlsttab = 'OR_PER') "
			+ " INNER JOIN de_servizio on (coll.codservizio = de_servizio.codservizio) "
			+ " WHERE (PATTO.codstatoatto = 'PR') "
			+ " and (de_servizio.codservizio in ('186', 'L14_2018', 'L14_2019', 'NGG')) ";

	private static final String QUERY_CHECK_PATTI_MOD = "SELECT COUNT(DISTINCT PATTO.PRGPATTOLAVORATORE) AS NUMCOUNT "
			+ " FROM AM_PATTO_LAVORATORE PATTO, or_colloquio coll, or_percorso_concordato per, am_lav_patto_scelta scelta, "
			+ " de_servizio, " + " TS_PROFILATURA_UTENTE PROFILATURA, TS_GRUPPO, TS_TIPO_GRUPPO "
			+ " WHERE PATTO.cdnlavoratore = coll.cdnlavoratore and coll.prgcolloquio = per.prgcolloquio "
			+ " and PATTO.prgpattolavoratore = scelta.prgpattolavoratore and per.prgpercorso = to_number(scelta.strchiavetabella) "
			+ " and scelta.codlsttab = 'OR_PER' and coll.codservizio = de_servizio.codservizio "
			+ " AND PATTO.CDNLAVORATORE = ? "
			+ " AND de_servizio.codservizio in ('186', 'L14_2018', 'L14_2019', 'NGG') "
			+ " AND PATTO.CDNUTMOD = PROFILATURA.CDNUT (+) " + " AND PROFILATURA.CDNGRUPPO = TS_GRUPPO.CDNGRUPPO (+) "
			+ " AND TS_GRUPPO.CDNTIPOGRUPPO = TS_TIPO_GRUPPO.CDNTIPOGRUPPO (+) " + " AND PATTO.codstatoatto = 'PR' "
			+ " AND TRUNC(PATTO.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') AND (PATTO.CDNUTMOD = 400 OR nvl(TS_TIPO_GRUPPO.CODTIPO, 'N') = 'S') ";

	private static final String QUERY_CHECK_PERCORSOCONCORDATO = "SELECT COUNT(DISTINCT PC.PRGPERCORSO) AS NUMPERCORSO  "
			+ " FROM OR_PERCORSO_CONCORDATO PC " + " INNER JOIN OR_COLLOQUIO CO ON (PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO) "
			+ " INNER JOIN AM_PATTO_LAVORATORE PATTO ON (CO.CDNLAVORATORE = PATTO.CDNLAVORATORE) "
			+ " INNER JOIN AM_LAV_PATTO_SCELTA SCELTA ON (PATTO.PRGPATTOLAVORATORE = SCELTA.PRGPATTOLAVORATORE "
			+ " AND TO_NUMBER(SCELTA.STRCHIAVETABELLA) = PC.PRGPERCORSO AND SCELTA.CODLSTTAB = 'OR_PER') "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI "
			+ " INNER JOIN DE_SERVIZIO ON CO.CODSERVIZIO = DE_SERVIZIO.CODSERVIZIO "
			+ " LEFT JOIN TS_PROFILATURA_UTENTE PROFILATURA ON PC.CDNUTMOD = PROFILATURA.CDNUT "
			+ " LEFT JOIN TS_GRUPPO ON PROFILATURA.CDNGRUPPO = TS_GRUPPO.CDNGRUPPO "
			+ " LEFT JOIN TS_TIPO_GRUPPO ON TS_GRUPPO.CDNTIPOGRUPPO = TS_TIPO_GRUPPO.CDNTIPOGRUPPO "
			+ " WHERE CO.CDNLAVORATORE = ? AND AZ.CODAZIONESIFER IS NOT NULL AND AZ.CODMISURA IS NOT NULL "
			+ " AND PATTO.codstatoatto = 'PR' "
			+ " AND DE_SERVIZIO.CODSERVIZIO in ('186', 'L14_2018', 'L14_2019', 'NGG') "
			+ " AND TRUNC(PC.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') AND (PC.CDNUTMOD = 400 OR nvl(TS_TIPO_GRUPPO.CODTIPO, 'N') = 'S') ";

	private static final String QUERY_CHECK_SCHEDA_PARTECIPANTE = "SELECT COUNT(DISTINCT SCHEDA.PRGPATTOLAVORATORE) AS NUMCOUNT  "
			+ " FROM AM_PATTO_LAVORATORE PATTO "
			+ " INNER JOIN or_colloquio coll on (PATTO.cdnlavoratore = coll.cdnlavoratore) "
			+ " INNER JOIN or_percorso_concordato per on (coll.prgcolloquio = per.prgcolloquio) "
			+ " INNER JOIN am_lav_patto_scelta scelta on (PATTO.prgpattolavoratore = scelta.prgpattolavoratore "
			+ " 	and per.prgpercorso = to_number(scelta.strchiavetabella) " + " 	and scelta.codlsttab = 'OR_PER') "
			+ " INNER JOIN de_servizio on (coll.codservizio = de_servizio.codservizio) "
			+ " INNER JOIN OR_SCHEDA_PARTECIPANTE SCHEDA ON (PATTO.PRGPATTOLAVORATORE = SCHEDA.PRGPATTOLAVORATORE) "
			+ " LEFT JOIN TS_PROFILATURA_UTENTE PROFILATURA ON SCHEDA.CDNUTMOD = PROFILATURA.CDNUT "
			+ " LEFT JOIN TS_GRUPPO ON PROFILATURA.CDNGRUPPO = TS_GRUPPO.CDNGRUPPO "
			+ " LEFT JOIN TS_TIPO_GRUPPO ON TS_GRUPPO.CDNTIPOGRUPPO = TS_TIPO_GRUPPO.CDNTIPOGRUPPO "
			+ " WHERE PATTO.CDNLAVORATORE = ? " + " AND PATTO.codstatoatto = 'PR' "
			+ " AND de_servizio.codservizio in ('186', 'L14_2018', 'L14_2019', 'NGG') "
			+ " AND TRUNC(SCHEDA.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') AND (SCHEDA.CDNUTMOD = 400 OR nvl(TS_TIPO_GRUPPO.CODTIPO, 'N') = 'S') ";

	private static final String QUERY_CHECK_ANAGRAFICA = "SELECT COUNT(DISTINCT LAV.CDNLAVORATORE) AS NUMLAV "
			+ " FROM AN_LAVORATORE LAV "
			+ " LEFT JOIN TS_PROFILATURA_UTENTE PROFILATURA ON LAV.CDNUTMOD = PROFILATURA.CDNUT "
			+ " LEFT JOIN TS_GRUPPO ON PROFILATURA.CDNGRUPPO = TS_GRUPPO.CDNGRUPPO "
			+ " LEFT JOIN TS_TIPO_GRUPPO ON TS_GRUPPO.CDNTIPOGRUPPO = TS_TIPO_GRUPPO.CDNTIPOGRUPPO "
			+ " WHERE LAV.CDNLAVORATORE = ? "
			+ " AND TRUNC(LAV.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') AND (LAV.CDNUTMOD = 400 OR nvl(TS_TIPO_GRUPPO.CODTIPO, 'N') = 'S') ";

	private static final String QUERY_DATA_BATCH_SIFER = "select to_char(DATETL, 'dd/mm/yyyy') as DATETL, MAXCARICOSAP "
			+ " from ts_monitoraggio where CODAMBITO = 'EA_SIFER' ";

	private BigDecimal nCarico = null;
	private int nLavoratoriTrattati;

	private static final String QUERY_UPDATE_DATA_BATCH_SIFER = "update ts_monitoraggio set dateTL = sysdate where codambito = 'EA_SIFER' ";

	static Logger logger = Logger.getLogger(BatchEntiAccreditatiSifer.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "01", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();
			this.nLavoratoriTrattati = 0;
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
					String dataEsecuzioneBatch = null;
					PreparedStatement preparedStatementDataBatch = null;
					preparedStatementDataBatch = connection.prepareStatement(QUERY_DATA_BATCH_SIFER);
					ResultSet resultSetDataBatch = preparedStatementDataBatch.executeQuery();
					if (resultSetDataBatch != null) {
						while (resultSetDataBatch.next()) {
							dataEsecuzioneBatch = resultSetDataBatch.getString("DATETL");
							this.nCarico = resultSetDataBatch.getBigDecimal("MAXCARICOSAP");
						}
					}
					resultSetDataBatch.close();
					if (this.nCarico != null && this.nCarico.intValue() > 0) {
						startScheduling = true;
					}

					if (startScheduling) {
						logger.info("Batch Notturno BatchEntiAccreditatiSifer START effectiveStartDate:"
								+ df.format(effectiveStartDate));

						BigDecimal cdnLavoratore = null;
						// QUERY RICERCA LAVORATORI DA SPEDIRE A SIFER
						PreparedStatement preparedStatement = null;
						preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI);
						resultSet = preparedStatement.executeQuery();

						if (resultSet != null && dataEsecuzioneBatch != null) {
							while ((resultSet.next()) && (this.nLavoratoriTrattati < this.nCarico.intValue())) {
								try {
									cdnLavoratore = resultSet.getBigDecimal("CDNLAVORATORE");
									checkInvio = false;
									gestisciInvioLavoratore(checkInvio, dataEsecuzioneBatch, cdnLavoratore);
								} catch (Exception e) {
									logger.error("Errore Batch Notturno BatchEntiAccreditatiSifer: ", e);
								}
							}
						}

						// aggiorna la data di esecuzione del batch
						aggiornaDataBatch(connection);

						preparedStatement.close();
						resultSet.close();

						final Date stopDate = new Date();
						logger.info("Batch Notturno BatchEntiAccreditatiSifer STOP at:" + df.format(stopDate));
					} else {
						logger.info(
								"Batch Notturno BatchEntiAccreditatiSifer non schedulabile: Carico non specificato");
					}
				}
			} catch (Exception e) {
				logger.error("Errore Batch Notturno BatchEntiAccreditatiSifer: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchEntiAccreditatiSifer] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void gestisciInvioLavoratore(boolean checkInvio, String dataEsecuzioneBatch, BigDecimal cdnLavoratore)
			throws Exception {
		logger.debug(
				"BatchEntiAccreditatiSifer inizio gestisciInvioLavoratore cdnlavoratore:" + cdnLavoratore.toString());
		Connection connection = null;
		// istanzio una nuova connessione per ogni lavoratore e la chiudo in fondo all'esecuzione
		try {
			connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI).getInternalConnection();
			if (!checkInvio) {
				// verifica patti L14 o POC modificati
				BigDecimal numPatti = null;
				PreparedStatement preparedStatementCheckPatti = null;
				preparedStatementCheckPatti = connection.prepareStatement(QUERY_CHECK_PATTI_MOD);
				preparedStatementCheckPatti.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckPatti.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckPatti = preparedStatementCheckPatti.executeQuery();
				if (resultSetCheckPatti != null) {
					while (resultSetCheckPatti.next()) {
						numPatti = resultSetCheckPatti.getBigDecimal("NUMCOUNT");
					}
				}
				preparedStatementCheckPatti.close();
				resultSetCheckPatti.close();

				if (numPatti != null && numPatti.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			if (!checkInvio) {
				// verifica azioni associate a patti L14 o POC modificate
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
				// verifica modifica anagrafica lavoratore
				BigDecimal numAnag = null;
				PreparedStatement preparedStatementCheckAnag = null;
				preparedStatementCheckAnag = connection.prepareStatement(QUERY_CHECK_ANAGRAFICA);
				preparedStatementCheckAnag.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckAnag.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckAnag = preparedStatementCheckAnag.executeQuery();
				if (resultSetCheckAnag != null) {
					while (resultSetCheckAnag.next()) {
						numAnag = resultSetCheckAnag.getBigDecimal("NUMLAV");
					}
				}
				preparedStatementCheckAnag.close();
				resultSetCheckAnag.close();

				if (numAnag != null && numAnag.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			if (!checkInvio) {
				// verifica informazioni scheda partecipante associata a patti L14 o POC modificate
				BigDecimal numModScheda = null;
				PreparedStatement preparedStatementCheckScheda = null;
				preparedStatementCheckScheda = connection.prepareStatement(QUERY_CHECK_SCHEDA_PARTECIPANTE);
				preparedStatementCheckScheda.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckScheda.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckScheda = preparedStatementCheckScheda.executeQuery();
				if (resultSetCheckScheda != null) {
					while (resultSetCheckScheda.next()) {
						numModScheda = resultSetCheckScheda.getBigDecimal("NUMCOUNT");
					}
				}
				preparedStatementCheckScheda.close();
				resultSetCheckScheda.close();

				if (numModScheda != null && numModScheda.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			// per ogni lavoratore trovato richiamo l'invio dei dati a SIFER
			QueryExecutorObject qExec = null;
			DataConnection dc = null;

			if (checkInvio) {
				this.nLavoratoriTrattati = this.nLavoratoriTrattati + 1;
				try {
					// INFO LAVORATORE
					qExec = EnteAccreditatoUtils.getQueryExecutorObject();
					dc = qExec.getDataConnection();
					qExec.setStatement(SQLStatements.getStatement("GET_AN_LAVORATORE_ANAG_SIFER"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(params);
					SourceBean anLavBeanRows = (SourceBean) qExec.exec();

					// INFO PROVINCIA
					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_codprovincia"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsProv = new ArrayList<DataField>();
					qExec.setInputParameters(paramsProv);
					SourceBean codProvinciaBeanRows = (SourceBean) qExec.exec();

					// INFO PATTI
					qExec.setStatement(SQLStatements.getStatement("GET_EnteAccreditato_SIFER_Dati_patto"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsPatti = new ArrayList<DataField>();
					paramsPatti.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					paramsPatti.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					paramsPatti.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(paramsPatti);
					SourceBean pattiBeanRows = (SourceBean) qExec.exec();

					logger.debug("BatchEntiAccreditatiSifer chiamata sendFlussoEnteAccreditato cdnlavoratore:"
							+ cdnLavoratore.toString());
					ErroreSifer erroreSifer = EnteAccreditatoUtils.sendFlussoEnteAccreditato(cdnLavoratore,
							anLavBeanRows, codProvinciaBeanRows, pattiBeanRows, EnteAccreditatoUtils.AREA2);
					logger.debug("BatchEntiAccreditatiSifer return sendFlussoEnteAccreditato erroreSifer:"
							+ erroreSifer.errCod);

					// if (erroreSifer.errCod != 0) { // Commento perchè devo tracciare anche quando la spedizione è ok
					// traccio l'errore sul DB ed eventualmente inserimento evidenza sul lavoratore come fatto per il
					// batch BatchPartecipanteGGSifer
					EnteAccreditatoUtils.tracciaErrore(connection, cdnLavoratore, erroreSifer);
					// }
				} catch (Exception ex) {
					logger.error("BatchEntiAccreditatiSifer errore: " + ex);
				} finally {
					if (dc != null) {
						dc.close();
					}
				}
			}
		} catch (Throwable e) {
			logger.error("BatchEntiAccreditatiSifer errore: " + e);
		} finally {
			logger.debug(
					"BatchEntiAccreditatiSifer fine gestisciInvioLavoratore cdnlavoratore:" + cdnLavoratore.toString());
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

	public static void aggiornaDataBatch(Connection connection) {
		logger.debug("BatchEntiAccreditatiSifer aggiornamento data chiamata batch");
		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(QUERY_UPDATE_DATA_BATCH_SIFER);
			preparedStatement.executeUpdate();

			preparedStatement.close();

		} catch (Exception e) {
			logger.error("Errore: aggiornamento data batch ente accreditato SIFER", e);
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

}
