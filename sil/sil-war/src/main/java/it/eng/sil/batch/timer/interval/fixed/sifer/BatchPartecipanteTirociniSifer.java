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
import java.util.HashMap;
import java.util.Iterator;
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
import it.eng.sil.module.sifer.PartecipanteTirociniUtils;

@Singleton
public class BatchPartecipanteTirociniSifer extends FixedTimerBatch {

	private static final String QUERY_RICERCA_LAVORATORI_TIROCINI_MODIFICATI = " SELECT DISTINCT MOV.CDNLAVORATORE "
			+ " FROM AM_MOVIMENTO MOV INNER JOIN AN_UNITA_AZIENDA UAZ ON (MOV.PRGAZIENDA = UAZ.PRGAZIENDA AND MOV.PRGUNITA = UAZ.PRGUNITA) "
			+ " INNER JOIN DE_COMUNE DECOM ON (UAZ.CODCOM = DECOM.CODCOM) "
			+ " LEFT JOIN AN_UNITA_AZIENDA UAZINT ON (MOV.PRGAZIENDAUTILIZ = UAZINT.PRGAZIENDA AND MOV.PRGUNITAUTILIZ = UAZINT.PRGUNITA) "
			+ " LEFT JOIN DE_COMUNE DECOMINT ON (UAZINT.CODCOM = DECOMINT.CODCOM) "
			+ " WHERE (MOV.CODSTATOATTO = 'PR') AND (MOV.CODTIPOMOV = 'AVV') "
			+ " AND (TRUNC(MOV.DATINIZIOMOV) >= (SELECT DATINIMOVBATCHTIR FROM TS_GENERALE where prggenerale = 1)) "
			+ " AND (MOV.CODTIPOCONTRATTO = 'C.01.00') AND (MOV.CODCOMUNICAZIONE IS NOT NULL) AND (TRUNC(MOV.DTMMOD) >= TO_DATE(?, 'DD/MM/YYYY')) "
			+ " AND (NVL(DECOMINT.CODPROVINCIA, 'XX') = (SELECT CODPROVINCIASIL FROM TS_GENERALE where prggenerale = 1) OR DECOM.CODPROVINCIA = (SELECT CODPROVINCIASIL FROM TS_GENERALE where prggenerale = 1))";

	private static final String QUERY_RICERCA_LAVORATORI_TIROCINI_NON_MODIFICATI = " SELECT DISTINCT MOV.CDNLAVORATORE "
			+ " FROM AM_MOVIMENTO MOV INNER JOIN AN_UNITA_AZIENDA UAZ ON (MOV.PRGAZIENDA = UAZ.PRGAZIENDA AND MOV.PRGUNITA = UAZ.PRGUNITA) "
			+ " INNER JOIN DE_COMUNE DECOM ON (UAZ.CODCOM = DECOM.CODCOM) "
			+ " LEFT JOIN AN_UNITA_AZIENDA UAZINT ON (MOV.PRGAZIENDAUTILIZ = UAZINT.PRGAZIENDA AND MOV.PRGUNITAUTILIZ = UAZINT.PRGUNITA) "
			+ " LEFT JOIN DE_COMUNE DECOMINT ON (UAZINT.CODCOM = DECOMINT.CODCOM) "
			+ " WHERE (MOV.CODSTATOATTO = 'PR') AND (MOV.CODTIPOMOV = 'AVV') "
			+ " AND (TRUNC(MOV.DATINIZIOMOV) >= (SELECT DATINIMOVBATCHTIR FROM TS_GENERALE where prggenerale = 1)) "
			+ " AND (MOV.CODTIPOCONTRATTO = 'C.01.00') AND (MOV.CODCOMUNICAZIONE IS NOT NULL) AND (TRUNC(MOV.DTMMOD) < TO_DATE(?, 'DD/MM/YYYY')) "
			+ " AND (NVL(DECOMINT.CODPROVINCIA, 'XX') = (SELECT CODPROVINCIASIL FROM TS_GENERALE where prggenerale = 1) OR DECOM.CODPROVINCIA = (SELECT CODPROVINCIASIL FROM TS_GENERALE where prggenerale = 1))";

	private static final String QUERY_CHECK_ANAGRAFICA = "SELECT COUNT(1) AS NUMLAV " + " FROM AN_LAVORATORE LAV "
			+ " WHERE LAV.CDNLAVORATORE = ? " + " AND TRUNC(LAV.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') ";

	private static final String QUERY_CHECK_PERMESSO_SOGG = "SELECT COUNT(1) AS NUMPERMESSOSOGG "
			+ " FROM AM_EX_PERM_SOGG EX " + " WHERE EX.CDNLAVORATORE = ? AND EX.DATSCADENZA > SYSDATE "
			+ " AND TRUNC(EX.DTMMOD) >= TO_DATE(?,'dd/mm/yyyy') ";

	private static final String QUERY_CHECK_ACCORPAMENTO = "SELECT COUNT(1) AS NUMACCORP "
			+ " FROM AN_LAVORATORE_ACCORPA AC" + " WHERE (AC.CDNLAVORATORE = ? " + " OR AC.CDNLAVORATOREACCORPATO = ?) "
			+ " AND TRUNC(AC.DTMINS) >= TO_DATE(?,'dd/mm/yyyy') ";

	private static final String QUERY_DATA_BATCH_SIFER = "select to_char(DATETL, 'dd/mm/yyyy') as DATETL "
			+ " from ts_monitoraggio " + " where CODAMBITO = 'TF_SIFER' ";

	private static final String QUERY_DATA_INIZIO_MOV_BATCH_SIFER = "select to_char(DATINIMOVBATCHTIR, 'dd/mm/yyyy') as DATINIMOVBATCHTIR "
			+ " from TS_GENERALE where prggenerale = 1 ";

	private static final String QUERY_UPDATE_DATA_BATCH_SIFER = "update ts_monitoraggio set dateTL = sysdate where codambito = 'TF_SIFER' ";

	static Logger logger = Logger.getLogger(BatchPartecipanteTirociniSifer.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "23", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info("Batch Notturno BatchPartecipanteTirociniSifer START effectiveStartDate:"
					+ df.format(effectiveStartDate));

			Connection connection = null;
			Statement statement = null;
			boolean oldAutoCommit = false;

			try {

				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(true);

				ResultSet resultSet = null;
				ResultSet resultSet1 = null;
				boolean checkInvio = false;

				if (connection != null) {
					String dataEsecuzioneBatch = null;
					HashMap<BigDecimal, String> lavSendSifer = new HashMap<BigDecimal, String>();
					PreparedStatement preparedStatementDataBatch = null;
					preparedStatementDataBatch = connection.prepareStatement(QUERY_DATA_BATCH_SIFER);
					ResultSet resultSetDataBatch = preparedStatementDataBatch.executeQuery();
					if (resultSetDataBatch != null) {
						while (resultSetDataBatch.next()) {
							dataEsecuzioneBatch = resultSetDataBatch.getString("DATETL");
						}
					}
					preparedStatementDataBatch.close();
					resultSetDataBatch.close();

					String dataEsecuzioneMovBatch = null;
					PreparedStatement preparedStatementDataMovBatch = null;
					preparedStatementDataMovBatch = connection.prepareStatement(QUERY_DATA_INIZIO_MOV_BATCH_SIFER);
					ResultSet resultSetDataMovBatch = preparedStatementDataMovBatch.executeQuery();
					if (resultSetDataMovBatch != null) {
						while (resultSetDataMovBatch.next()) {
							dataEsecuzioneMovBatch = resultSetDataMovBatch.getString("DATINIMOVBATCHTIR");
						}
					}
					preparedStatementDataMovBatch.close();
					resultSetDataMovBatch.close();

					if (dataEsecuzioneBatch != null) {
						BigDecimal cdnLavoratore = null;
						// QUERY RICERCA LAVORATORI DA SPEDIRE A SIFER CHE HANNO UN TIROCINIO MODIFICATO
						PreparedStatement preparedStatement = null;
						preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI_TIROCINI_MODIFICATI);
						preparedStatement.setString(1, dataEsecuzioneBatch);
						resultSet = preparedStatement.executeQuery();

						if (resultSet != null) {
							while (resultSet.next()) {
								cdnLavoratore = resultSet.getBigDecimal("CDNLAVORATORE");
								// ogni lavoratore lo aggiungo a lavSendSifer
								lavSendSifer.put(cdnLavoratore, "s");
							}
						}
						preparedStatement.close();
						resultSet.close();

						// QUERY RICERCA LAVORATORI DA SPEDIRE A SIFER CHE HANNO UN TIROCINIO NON MODIFICATO
						PreparedStatement preparedStatement1 = null;
						preparedStatement1 = connection
								.prepareStatement(QUERY_RICERCA_LAVORATORI_TIROCINI_NON_MODIFICATI);
						preparedStatement1.setString(1, dataEsecuzioneBatch);
						resultSet1 = preparedStatement1.executeQuery();

						if (resultSet1 != null) {
							while (resultSet1.next()) {
								checkInvio = false;
								cdnLavoratore = resultSet1.getBigDecimal("CDNLAVORATORE");
								if (!lavSendSifer.containsKey(cdnLavoratore)) {
									if (!checkInvio) {
										// verifica modifica anagrafica lavoratore
										BigDecimal numAnag = null;
										PreparedStatement preparedStatementCheckAnag = null;
										preparedStatementCheckAnag = connection
												.prepareStatement(QUERY_CHECK_ANAGRAFICA);
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
										// verifica modifica permesso di soggiorno
										BigDecimal numSogg = null;
										PreparedStatement preparedStatementCheckSogg = null;
										preparedStatementCheckSogg = connection
												.prepareStatement(QUERY_CHECK_PERMESSO_SOGG);
										preparedStatementCheckSogg.setBigDecimal(1, cdnLavoratore);
										preparedStatementCheckSogg.setString(2, dataEsecuzioneBatch);
										ResultSet resultSetCheckSogg = preparedStatementCheckSogg.executeQuery();
										if (resultSetCheckSogg != null) {
											while (resultSetCheckSogg.next()) {
												numSogg = resultSetCheckSogg.getBigDecimal("NUMPERMESSOSOGG");
											}
										}
										preparedStatementCheckSogg.close();
										resultSetCheckSogg.close();

										if (numSogg != null && numSogg.compareTo(new BigDecimal(0)) > 0) {
											checkInvio = true;
										}
									}

									if (!checkInvio) {
										// verifica esistenza accorpamento
										BigDecimal numAccorp = null;
										PreparedStatement preparedStatementCheckAccorp = null;
										preparedStatementCheckAccorp = connection
												.prepareStatement(QUERY_CHECK_ACCORPAMENTO);
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

									// per ogni lavoratore che soddisfa i criteri lo aggiungo a lavSendSifer
									if (checkInvio) {
										lavSendSifer.put(cdnLavoratore, "s");
									}
								}
							}
						}

						preparedStatement1.close();
						resultSet1.close();

						// per ogni lavoratore che soddisfa i criteri di estrazione richiamo l'invio dei dati a SIFER
						Iterator<BigDecimal> i = lavSendSifer.keySet().iterator();
						while (i.hasNext()) {
							QueryExecutorObject qExec = null;
							DataConnection dc = null;

							try {
								BigDecimal cdnLavKey = (BigDecimal) i.next();

								qExec = PartecipanteTirociniUtils.getQueryExecutorObject();
								dc = qExec.getDataConnection();
								qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_anlav"));
								qExec.setType(QueryExecutorObject.SELECT);
								List<DataField> params = new ArrayList<DataField>();
								params.add(dc.createDataField("_ENCRYPTER_KEY_", Types.VARCHAR,
										System.getProperty("_ENCRYPTER_KEY_")));
								params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavKey));
								qExec.setInputParameters(params);
								SourceBean anLavBeanRows = (SourceBean) qExec.exec();

								qExec.setStatement(
										SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_perm_sogg"));
								qExec.setType(QueryExecutorObject.SELECT);
								List<DataField> paramsPerm = new ArrayList<DataField>();
								paramsPerm.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavKey));
								qExec.setInputParameters(paramsPerm);
								SourceBean permSoggBeanRows = (SourceBean) qExec.exec();

								qExec.setStatement(
										SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_codprovincia"));
								qExec.setType(QueryExecutorObject.SELECT);
								List<DataField> paramsProv = new ArrayList<DataField>();
								qExec.setInputParameters(paramsProv);
								SourceBean codProvinciaBeanRows = (SourceBean) qExec.exec();

								qExec.setStatement(
										SQLStatements.getStatement("GET_Partecipante_GG_DatiGiovane_titstudio"));
								qExec.setType(QueryExecutorObject.SELECT);
								List<DataField> paramsTit = new ArrayList<DataField>();
								paramsTit.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavKey));
								qExec.setInputParameters(paramsTit);
								SourceBean titStudioBeanRows = (SourceBean) qExec.exec();

								qExec.setStatement(
										SQLStatements.getStatement("GET_Partecipante_Tirocini_Movimentazione"));
								qExec.setType(QueryExecutorObject.SELECT);
								List<DataField> paramsMov = new ArrayList<DataField>();
								paramsMov.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavKey));
								paramsMov
										.add(dc.createDataField("DATINIZIOMOV", Types.VARCHAR, dataEsecuzioneMovBatch));
								qExec.setInputParameters(paramsMov);
								SourceBean movimentazioneBeanRows = (SourceBean) qExec.exec();

								qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_Tirocini_CredWS"));
								qExec.setType(QueryExecutorObject.SELECT);
								List<DataField> paramsWS = new ArrayList<DataField>();
								qExec.setInputParameters(paramsWS);
								SourceBean wsRows = (SourceBean) qExec.exec();

								int errCod;
								try {
									logger.debug(
											"BatchPartecipanteTirociniSifer chiamata sendPartecipanteTirocini cdnlavoratore:"
													+ cdnLavKey.toString());
									errCod = PartecipanteTirociniUtils.sendPartecipanteTirocini(cdnLavKey.toString(),
											anLavBeanRows, permSoggBeanRows, codProvinciaBeanRows, titStudioBeanRows,
											movimentazioneBeanRows, wsRows);
									logger.debug(
											"BatchPartecipanteTirociniSifer return sendPartecipanteTirocini erroreSifer:"
													+ errCod);
								} catch (Exception e) {
									errCod = 99;
									logger.error("Errore BatchPartecipanteTirociniSifer: ", e);
								}

								String msgErr = "";
								if (errCod != 0) {
									// inserimento EVIDENZA
									if (MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE == errCod) {
										int koWs = MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATI_LAVORATORE;
										msgErr = MessageBundle.getMessage(String.valueOf(koWs));
										msgErr = msgErr + "Si prega di correggere i dati da inviare a Formazione'";
									} else {
										msgErr = MessageBundle.getMessage(String.valueOf(errCod));
									}

									insertEvidenzaErr(connection, cdnLavKey, msgErr);
								} else {
									int okWs = MessageCodes.YG.WS_PARTECIPANTE_OK;
									msgErr = MessageBundle.getMessage(String.valueOf(okWs));
								}
							}

							catch (Throwable ex) {
								logger.error("BatchPartecipanteTirociniSifer errore: " + ex);
							} finally {
								if (dc != null) {
									dc.close();
								}
							}
						}

						// aggiorna la data di esecuzione del batch
						aggiornaDataBatch(connection);
					} else {
						logger.error("Settare la data ultimo lancio del Batch Notturno BatchPartecipanteTirociniSifer");
					}
				}

			} catch (Exception e) {
				logger.error("Errore Batch Notturno BatchPartecipanteTirociniSifer: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}

			final Date stopDate = new Date();
			logger.info("Batch Notturno BatchPartecipanteTirociniSifer STOP at:" + df.format(stopDate));
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchPartecipanteTirocinioSifer] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
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
		logger.debug("BatchPartecipanteTirociniSifer inserimento evidenza insertEvidenzaErr cdnlavoratore:"
				+ cdnLavoratore.toString());
		PreparedStatement preparedStatementIns = null;

		try {
			String statementInsertEv = "insert into AN_EVIDENZA (PRGEVIDENZA, CDNLAVORATORE, "
					+ " DATDATASCAD, STREVIDENZA, PRGTIPOEVIDENZA, CDNUTINS, DTMINS, CDNUTMOD, DTMMOD) "
					+ " values (S_AN_EVIDENZA.nextVal, ?, " + " to_date('01/01/2100','dd/mm/yyyy'), "
					+ " ? , (select prgtipoevidenza from de_tipo_evidenza where codtipoevidenza = 'TF'),"
					+ " 365, sysdate, " + " 365, sysdate)";

			preparedStatementIns = connection.prepareStatement(statementInsertEv);
			preparedStatementIns.setBigDecimal(1, cdnLavoratore);
			preparedStatementIns.setString(2, msgErr);
			preparedStatementIns.executeUpdate();

			preparedStatementIns.close();

		} catch (Exception e) {
			logger.error("Errore: inserimento evidenza batch partecipante Tirocini SIFER", e);
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

		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(QUERY_UPDATE_DATA_BATCH_SIFER);
			preparedStatement.executeUpdate();

			preparedStatement.close();

		} catch (Exception e) {
			logger.error("Errore: aggiornamento data batch partecipante Tirocini SIFER", e);
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
