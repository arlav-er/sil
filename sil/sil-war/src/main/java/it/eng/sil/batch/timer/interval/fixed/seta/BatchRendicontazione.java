package it.eng.sil.batch.timer.interval.fixed.seta;

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
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.module.seta.ErroreSeta;
import it.eng.sil.module.seta.RendicontazioneUtils;

@Singleton
public class BatchRendicontazione extends FixedTimerBatch {

	private static final String QUERY_RICERCA_LAVORATORI = "SELECT distinct AN.CDNLAVORATORE "
			+ " FROM OR_PERCORSO_CONCORDATO PC INNER JOIN OR_COLLOQUIO CO ON PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO "
			+ " INNER JOIN AN_LAVORATORE AN ON AN.CDNLAVORATORE = CO.CDNLAVORATORE "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI "
			+ " INNER JOIN DE_AZIONE_RAGG RAGG ON AZ.PRGAZIONERAGG = RAGG.PRGAZIONIRAGG "
			+ " WHERE az.codazionesifer = 'A02' and nvl(az.flgformazione, 'N') = 'S' and nvl(RAGG.flg_misurayei, 'N') = 'S' and pc.codesito = 'FC'"
			+ " union " + " SELECT distinct AN.CDNLAVORATORE "
			+ " FROM OR_PERCORSO_CONCORDATO PC INNER JOIN OR_COLLOQUIO CO ON PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO "
			+ " INNER JOIN AN_LAVORATORE AN ON AN.CDNLAVORATORE = CO.CDNLAVORATORE "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI "
			+ " INNER JOIN DE_AZIONE_RAGG RAGG ON AZ.PRGAZIONERAGG = RAGG.PRGAZIONIRAGG "
			+ " INNER JOIN de_esito es ON (es.codesito = PC.codesito) "
			+ " INNER JOIN MN_YG_EVENTO ON (es.codeventomin = MN_YG_EVENTO.codevento) "
			+ " WHERE az.codazionesifer is not null and az.flgformazione = 'S' and es.flgformazione = 'S' and pc.codesito <> 'PRO' "
			+ " and nvl(RAGG.flg_misurayei, 'N') = 'N'";

	private static final String QUERY_CHECK_PERCORSOCONCORDATO = "SELECT COUNT(1) AS NUMPERCORSO  "
			+ " FROM OR_PERCORSO_CONCORDATO PC INNER JOIN OR_COLLOQUIO CO ON PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO "
			+ " INNER JOIN AN_LAVORATORE AN ON AN.CDNLAVORATORE = CO.CDNLAVORATORE "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI "
			+ " INNER JOIN DE_AZIONE_RAGG RAGG ON AZ.PRGAZIONERAGG = RAGG.PRGAZIONIRAGG "
			+ " WHERE CO.CDNLAVORATORE = ? AND NVL(AZ.FLGFORMAZIONE, 'N') = 'S' AND NVL(RAGG.FLG_MISURAYEI, 'N') = 'S' "
			+ " AND AZ.CODAZIONESIFER IS NOT NULL AND TRUNC(PC.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy')";

	private static final String QUERY_CHECK_PERCORSOCONCORDATO_DOTE = "SELECT COUNT(1) AS NUMPERCORSO  "
			+ " FROM OR_PERCORSO_CONCORDATO PC INNER JOIN OR_COLLOQUIO CO ON PC.PRGCOLLOQUIO = CO.PRGCOLLOQUIO "
			+ " INNER JOIN AN_LAVORATORE AN ON AN.CDNLAVORATORE = CO.CDNLAVORATORE "
			+ " INNER JOIN DE_AZIONE AZ ON AZ.PRGAZIONI = PC.PRGAZIONI "
			+ " INNER JOIN DE_AZIONE_RAGG RAGG ON AZ.PRGAZIONERAGG = RAGG.PRGAZIONIRAGG "
			+ " WHERE CO.CDNLAVORATORE = ? AND NVL(AZ.FLGFORMAZIONE, 'N') = 'S' AND NVL(RAGG.FLG_MISURAYEI, 'N') = 'N' "
			+ " AND AZ.CODAZIONESIFER IS NOT NULL AND TRUNC(PC.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy')";

	private static final String QUERY_CHECK_PATTO = "SELECT COUNT(1) AS NUMPATTO " + " FROM AM_PATTO_LAVORATORE PL "
			+ " INNER JOIN AM_DOCUMENTO_COLL DC ON (TO_NUMBER(DC.STRCHIAVETABELLA) = PL.PRGPATTOLAVORATORE) "
			+ " INNER JOIN AM_DOCUMENTO DO ON (DO.PRGDOCUMENTO = DC.PRGDOCUMENTO) "
			+ " WHERE PL.CDNLAVORATORE = ? AND ( (PL.CODTIPOPATTO in ('MGG', 'DOTE', 'DOTE_IA')) or "
			+ "         (getEsisteProgrammaPatto(PL.prgpattolavoratore, '''MGG'',''DOTE'',''DOTE_IA''') > 0) "
			+ "       ) " + " AND DO.CODTIPODOCUMENTO in ('PT297', 'ACLA') " + " AND PL.CODSTATOATTO = 'PR' "
			+ " AND DO.CODSTATOATTO = 'PR' " + " AND TRUNC(PL.DTMMOD) >= TO_DATE(?, 'dd/mm/yyyy')";

	private static final String QUERY_CHECK_MOVIMENTI = "SELECT COUNT(1) AS NUMMOV FROM AM_MOVIMENTO M "
			+ " WHERE M.CDNLAVORATORE = ? AND M.CODSTATOATTO = 'PR' AND TRUNC(M.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy')";

	private static final String QUERY_CHECK_ACCORPAMENTO = "SELECT COUNT(1) AS NUMACCORP "
			+ " FROM AN_LAVORATORE_ACCORPA AC" + " WHERE (AC.CDNLAVORATORE = ? " + " OR AC.CDNLAVORATOREACCORPATO = ?) "
			+ " AND TRUNC(AC.DTMINS) >= TO_DATE(?,'dd/mm/yyyy')";

	private static final String QUERY_CHECK_ANAGRAFICA = "SELECT COUNT(1) AS NUMVARIAZIONE " + " FROM AN_LAVORATORE AN"
			+ " WHERE AN.CDNLAVORATORE = ?" + " AND TRUNC(AN.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy')";

	private static final String QUERY_DATA_BATCH_SETA = "select to_char(DATETL, 'dd/mm/yyyy') as DATETL "
			+ " from ts_monitoraggio where CODAMBITO = 'RENDICON' ";

	private static final String QUERY_UPDATE_DATA_BATCH_SETA = "update ts_monitoraggio set dateTL = sysdate where codambito = 'RENDICON' ";

	static Logger logger = Logger.getLogger(BatchRendicontazione.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "02", minute = "0", persistent = false)
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

				if (connection != null) {

					boolean startScheduling = false;

					String dataEsecuzioneBatch = null;
					PreparedStatement preparedStatementDataBatch = null;
					preparedStatementDataBatch = connection.prepareStatement(QUERY_DATA_BATCH_SETA);
					ResultSet resultSetDataBatch = preparedStatementDataBatch.executeQuery();
					if (resultSetDataBatch != null) {
						while (resultSetDataBatch.next()) {
							dataEsecuzioneBatch = resultSetDataBatch.getString("DATETL");
							if (dataEsecuzioneBatch != null && !dataEsecuzioneBatch.equals("")) {
								startScheduling = true;
							}
						}
					}
					resultSetDataBatch.close();

					if (startScheduling) {
						logger.info("Batch Notturno BatchRendicontazione START effectiveStartDate:"
								+ df.format(effectiveStartDate));
						BigDecimal cdnLavoratore = null;
						// QUERY RICERCA LAVORATORI DA INVIARE A RENDICONTAZIONE
						PreparedStatement preparedStatement = null;
						preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI);
						resultSet = preparedStatement.executeQuery();

						if (resultSet != null && dataEsecuzioneBatch != null) {
							while (resultSet.next()) {
								try {
									cdnLavoratore = resultSet.getBigDecimal("CDNLAVORATORE");

									gestisciInvioLavoratore(cdnLavoratore, dataEsecuzioneBatch, connection);

								} catch (Exception e) {
									logger.error("Errore Batch Notturno BatchRendicontazione: ", e);
								}
							}
						}

						// aggiorna la data di esecuzione del batch
						aggiornaDataBatch(connection);

						preparedStatement.close();
						resultSet.close();

						final Date stopDate = new Date();
						logger.info("Batch Notturno BatchRendicontazione STOP at:" + df.format(stopDate));
					} else {
						logger.info("Batch Notturno BatchRendicontazione NON SCHEDULABILE: Carico non specificato");
					}
				}

			} catch (Exception e) {
				logger.error("Errore Batch Notturno BatchRendicontazione: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchRendicontazione] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void gestisciInvioLavoratore(BigDecimal cdnLavoratore, String dataEsecuzioneBatch, Connection connection)
			throws Exception {
		logger.debug("BatchRendicontazione inizio gestisciInvioLavoratore cdnlavoratore:" + cdnLavoratore.toString());
		boolean checkInvio = false;

		try {
			if (!checkInvio) {
				// verifica esistenza azioni GG
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
				// verifica esistenza azioni DOTE
				BigDecimal numPerc1 = null;
				PreparedStatement preparedStatementCheckPerc1 = null;
				preparedStatementCheckPerc1 = connection.prepareStatement(QUERY_CHECK_PERCORSOCONCORDATO_DOTE);
				preparedStatementCheckPerc1.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckPerc1.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckPerc1 = preparedStatementCheckPerc1.executeQuery();
				if (resultSetCheckPerc1 != null) {
					while (resultSetCheckPerc1.next()) {
						numPerc1 = resultSetCheckPerc1.getBigDecimal("NUMPERCORSO");
					}
				}
				preparedStatementCheckPerc1.close();
				resultSetCheckPerc1.close();

				if (numPerc1 != null && numPerc1.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			if (!checkInvio) {
				// verifica esistenza patto
				BigDecimal numPatto = null;
				PreparedStatement preparedStatementCheckPatto = null;
				preparedStatementCheckPatto = connection.prepareStatement(QUERY_CHECK_PATTO);
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
				// verifica accorpamento
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

			if (!checkInvio) {
				// verifica anagrafica
				BigDecimal numVaria = null;
				PreparedStatement preparedStatementCheckAnagrafica = null;
				preparedStatementCheckAnagrafica = connection.prepareStatement(QUERY_CHECK_ANAGRAFICA);
				preparedStatementCheckAnagrafica.setBigDecimal(1, cdnLavoratore);
				preparedStatementCheckAnagrafica.setString(2, dataEsecuzioneBatch);
				ResultSet resultSetCheckNewAnagrafica = preparedStatementCheckAnagrafica.executeQuery();
				if (resultSetCheckNewAnagrafica != null) {
					while (resultSetCheckNewAnagrafica.next()) {
						numVaria = resultSetCheckNewAnagrafica.getBigDecimal("NUMVARIAZIONE");
					}
				}
				preparedStatementCheckAnagrafica.close();
				resultSetCheckNewAnagrafica.close();

				if (numVaria != null && numVaria.compareTo(new BigDecimal(0)) > 0) {
					checkInvio = true;
				}
			}

			// per ogni lavoratore trovato richiamo l'invio dei dati a rendicontazione
			QueryExecutorObject qExec = null;
			DataConnection dc = null;
			boolean isTransactional = true;

			if (checkInvio) {
				try {
					qExec = RendicontazioneUtils.getQueryExecutorObject(isTransactional);
					dc = qExec.getDataConnection();
					qExec.setStatement(SQLStatements.getStatement("GET_PARTECIPANTE"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
					qExec.setInputParameters(params);
					SourceBean anLavBeanRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_codprovincia"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsProv = new ArrayList<DataField>();
					qExec.setInputParameters(paramsProv);
					SourceBean codProvinciaBeanRows = (SourceBean) qExec.exec();

					qExec.setStatement(SQLStatements.getStatement("GET_PARTECIPANTE_PROFILING_PATTO"));
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

					qExec.setStatement(SQLStatements.getStatement("GET_WS_CREDENTIALS"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> paramsWS = new ArrayList<DataField>();
					paramsWS.add(dc.createDataField("CODSERVIZIO", Types.VARCHAR,
							it.eng.sil.coop.webservices.utils.Utils.SERVIZIORENDICONTAZIONE));
					qExec.setInputParameters(paramsWS);
					SourceBean wsRows = (SourceBean) qExec.exec();

					logger.debug(
							"BatchRendicontazione chiamata sendPartecipante cdnlavoratore:" + cdnLavoratore.toString());
					RendicontazioneUtils rendicontazione = new RendicontazioneUtils();
					boolean isFromBatch = true;
					ErroreSeta erroreSeta = rendicontazione.sendPartecipante(cdnLavoratore, anLavBeanRows,
							codProvinciaBeanRows, pattoBeanRows, movimentazioneBeanRows, wsRows, isFromBatch);
					logger.debug("BatchRendicontazione return sendPartecipanteGG erroreSifer:" + erroreSeta.errCod);

					String msgErr = "";
					if (erroreSeta.errCod != 0) {

						// traccio l'errore sul DB
						RendicontazioneUtils.tracciaErrore(connection, cdnLavoratore, erroreSeta);

						if (erroreSeta.errCod != MessageCodes.Webservices.WS_TRACCIATO_INVARIATO) {

							// INSERIMENTO EVIDENZA
							if (MessageCodes.RENDICONTAZIONE.ERR_WS_DATI_LAVORATORE == erroreSeta.errCod) {
								int koWs = MessageCodes.RENDICONTAZIONE.ERR_WS_DATI_LAVORATORE;
								msgErr = MessageBundle.getMessage(String.valueOf(koWs));
							} else {
								msgErr = erroreSeta.erroreEsteso;
							}

							insertEvidenzaErr(connection, cdnLavoratore, msgErr);
						} else {
							logger.error("BatchRendicontazione: " + MessageBundle
									.getMessage(String.valueOf(MessageCodes.Webservices.WS_TRACCIATO_INVARIATO)));
						}

					} else {
						// faccio scadere tutte le evidenze di tipo EG del lavoratore
						scadenzaEvidenza(connection, cdnLavoratore);
					}
				} catch (Exception ex) {
					logger.error("BatchRendicontazione errore: " + ex);
				} finally {
					if (dc != null) {
						dc.close();
					}
				}
			}
		} catch (Throwable e) {
			logger.error("BatchRendicontazione errore: " + e);
		} finally {
			logger.debug("BatchRendicontazione fine gestisciInvioLavoratore cdnlavoratore:" + cdnLavoratore.toString());
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
		logger.debug("BatchRendicontazione inserimento evidenza insertEvidenzaErr cdnlavoratore:"
				+ cdnLavoratore.toString());
		PreparedStatement preparedStatementIns = null;

		try {
			String statementInsertEv = "insert into AN_EVIDENZA (PRGEVIDENZA, CDNLAVORATORE, "
					+ " DATDATASCAD, STREVIDENZA, PRGTIPOEVIDENZA, CDNUTINS, DTMINS, CDNUTMOD, DTMMOD) "
					+ " values (S_AN_EVIDENZA.nextVal, ?, " + " trunc(ADD_MONTHS(sysdate,1)), "
					+ " ? , (select prgtipoevidenza from de_tipo_evidenza where codtipoevidenza = 'FR'),"
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

	public static void scadenzaEvidenza(Connection connection, BigDecimal cdnLavoratore) {
		logger.debug(
				"BatchRendicontazione scadenza evidenza scadenzaEvidenza cdnlavoratore:" + cdnLavoratore.toString());
		PreparedStatement preparedStatementUpd = null;

		try {
			String statementScadenzaEv = "UPDATE an_evidenza SET datdatascad = sysdate, "
					+ "strevidenza = strevidenza || ' Batch - Evidenza scaduta in seguito all''invio a Formazione eseguito in data ' || sysdate || '\n', numkloevidenza = numkloevidenza + 1 WHERE PRGTIPOEVIDENZA = (SELECT prgtipoevidenza FROM de_tipo_evidenza WHERE codtipoevidenza = 'FR') AND cdnlavoratore = ? AND datdatascad > sysdate";

			preparedStatementUpd = connection.prepareStatement(statementScadenzaEv);
			preparedStatementUpd.setBigDecimal(1, cdnLavoratore);
			preparedStatementUpd.executeUpdate();

			preparedStatementUpd.close();

		} catch (Exception e) {
			logger.error("Errore: scadenza evidenza batch rendicontazione", e);
		} finally {
			if (preparedStatementUpd != null) {
				try {
					preparedStatementUpd.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}
	}

	public static void aggiornaDataBatch(Connection connection) {
		logger.debug("BatchRendicontazione aggiornamento data chiamata batch");
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(QUERY_UPDATE_DATA_BATCH_SETA);
			preparedStatement.executeUpdate();

			preparedStatement.close();

		} catch (Exception e) {
			logger.error("Errore: aggiornamento data batch rendicontazione SETA", e);
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
