package it.eng.sil.batch.timer.interval.fixed.sms;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.engiweb.framework.dbaccess.DataConnectionManager;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.module.batch.Constants;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.batch.InviaSMS;

@Singleton
public class BatchInvioSMS extends FixedTimerBatch {

	private Logger logger = Logger.getLogger(BatchInvioSMS.class.getName());

	// QUERY PER ESTRAZIONE INFO UTENTE
	private String QUERY_INFO_UTENTE = " SELECT AN_SPI.PRGSPI, AN_SPI.STRCOGNOME COGNOMESPI, AN_SPI.STRNOME NOMESPI, AN_SPI.STRTELOPERATORE TELSPI "
			+ " FROM TS_UTENTE INNER JOIN AN_SPI ON (TS_UTENTE.PRGSPI = AN_SPI.PRGSPI) " + " WHERE TS_UTENTE.CDNUT = ?";

	// QUERY PER ESTRAZIONE BATCH PROGRAMMATI
	private String QUERY_BATCH_PROGRAMMATI = " SELECT PRGPROGRAMMABATCH, CODTIPOBATCH, TO_CHAR(DATULTIMAELAB, 'DD/MM/YYYY') DATULTIMAELAB, "
			+ "CODSERVIZIO, CODSTATOAPPUNTAMENTO, PRGAZIONI, CODESITO, PRGMOTCONTATTO, NUMGGPERIODOPROG, NUMGGPRECAVVISO, NUMGGPERIODODATE, CODTIPOSMS, STRDESCRIZIONE, "
			+ "FLGATTIVO, TO_CHAR(DATINIZIOVAL, 'DD/MM/YYYY') DATINIZIOVAL, TO_CHAR(DATFINEVAL, 'DD/MM/YYYY') DATFINEVAL, CDNUTINS, CDNUTMOD, "
			+ "CODMOTIVOFINEATTODID, FLGCMISCR, NUMGGRANGEFINEDID, "
			+ "TO_CHAR(DTMINS, 'DD/MM/YYYY') DTMINS, TO_CHAR(DTMMOD, 'DD/MM/YYYY') DTMMOD, NUMKLOPROGRAMMABATCH, CODCPI, PRGTIPOEVIDENZA "
			+ " FROM TS_PROGRAMMA_BATCH WHERE FLGATTIVO = 'S' AND TRUNC(SYSDATE) BETWEEN TRUNC(DATINIZIOVAL) AND TRUNC(DATFINEVAL)";

	private static final String QUERY_UPDATE_PROGRAMMAZIONE_BATCH = " UPDATE TS_PROGRAMMA_BATCH "
			+ " SET DATULTIMAELAB = SYSDATE, DTMMOD = SYSDATE, CDNUTMOD = ?, "
			+ " NUMKLOPROGRAMMABATCH = ? + 1, STRNOTE = NULL " + " WHERE PRGPROGRAMMABATCH = ? ";

	private static final String QUERY_UPDATE_ERRORE_PROGRAMMAZIONE_BATCH = " UPDATE TS_PROGRAMMA_BATCH "
			+ " SET DTMMOD = SYSDATE, CDNUTMOD = ?, " + " NUMKLOPROGRAMMABATCH = ? + 1, STRNOTE = ? "
			+ " WHERE PRGPROGRAMMABATCH = ? ";

	// Commentato perché nel vecchio scheduler jboss non è attivo
	// @Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "12", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info("Batch Invio SMS START effectiveStartDate:" + df.format(effectiveStartDate));

			Connection connection = null;
			Statement statement = null;
			boolean oldAutoCommit = false;

			try {
				BigDecimal cdnutBatch = Properties.UT_OPERATORE_IMPOSTAZIONI;
				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(true);
				String dataLancioBatch = DateUtils.getNow();

				if (connection != null) {

					// Recupero info utente - spi
					BigDecimal prgSpi = null;
					String cognomeSpi = "";
					String nomeSpi = "";
					String telSpi = "";

					PreparedStatement preparedStatementSpi = null;
					preparedStatementSpi = connection.prepareStatement(QUERY_INFO_UTENTE);
					preparedStatementSpi.setBigDecimal(1, cdnutBatch);
					ResultSet resultSetSpi = preparedStatementSpi.executeQuery();
					if (resultSetSpi != null) {
						while (resultSetSpi.next()) {
							prgSpi = resultSetSpi.getBigDecimal("PRGSPI");
							cognomeSpi = resultSetSpi.getString("COGNOMESPI");
							nomeSpi = resultSetSpi.getString("NOMESPI");
							telSpi = resultSetSpi.getString("TELSPI");
						}
					}

					if (resultSetSpi != null) {
						resultSetSpi.close();
					}
					if (preparedStatementSpi != null) {
						preparedStatementSpi.close();
					}

					// Recupero batch programmati
					PreparedStatement preparedStatementBatch = connection.prepareStatement(QUERY_BATCH_PROGRAMMATI);
					ResultSet resultSetBatch = preparedStatementBatch.executeQuery();

					if (resultSetBatch != null) {
						while (resultSetBatch.next()) {
							String tipoBatch = "";
							String dataUltimaElaborazione = "";
							BigDecimal giorniLancioBatch = null;
							String servizio = "";
							String statoAppuntamento = "";
							BigDecimal motivoContatto = null;
							BigDecimal giorniContattoBatch = null;
							BigDecimal giorniPeriodoCheck = null;
							BigDecimal prgAzione = null;
							String codEsitoAz = "";
							String tipoSMS = "";
							BigDecimal prgBatch = null;
							BigDecimal numkloBatch = null;
							BigDecimal prgTipoEvidenza = null;
							String strNote = "";
							String codMotivoFineAttoDid = "";
							String flgIscrCM = "";
							BigDecimal giorniRangeFineDid = null;
							boolean parametriBatchOK = true;
							String codCpi = "";

							prgBatch = resultSetBatch.getBigDecimal("PRGPROGRAMMABATCH");
							dataUltimaElaborazione = resultSetBatch.getString("DATULTIMAELAB");
							giorniLancioBatch = resultSetBatch.getBigDecimal("NUMGGPERIODOPROG");
							numkloBatch = resultSetBatch.getBigDecimal("NUMKLOPROGRAMMABATCH");

							if (dataUltimaElaborazione == null || dataUltimaElaborazione.equals("")
									|| DateUtils.compare(DateUtils.getNow(), DateUtils.aggiungiNumeroGiorni(
											dataUltimaElaborazione, giorniLancioBatch.intValue())) >= 0) {
								try {
									tipoBatch = resultSetBatch.getString("CODTIPOBATCH");
									Integer chiaveBatch = Constants.mapBatch.get(tipoBatch);
									motivoContatto = resultSetBatch.getBigDecimal("PRGMOTCONTATTO");
									giorniContattoBatch = resultSetBatch.getBigDecimal("NUMGGPRECAVVISO");
									servizio = resultSetBatch.getString("CODSERVIZIO");
									statoAppuntamento = resultSetBatch.getString("CODSTATOAPPUNTAMENTO");
									giorniPeriodoCheck = resultSetBatch.getBigDecimal("NUMGGPERIODODATE");
									prgAzione = resultSetBatch.getBigDecimal("PRGAZIONI");
									codEsitoAz = resultSetBatch.getString("CODESITO");
									tipoSMS = resultSetBatch.getString("CODTIPOSMS");
									prgTipoEvidenza = resultSetBatch.getBigDecimal("PRGTIPOEVIDENZA");
									codMotivoFineAttoDid = resultSetBatch.getString("CODMOTIVOFINEATTODID");
									flgIscrCM = resultSetBatch.getString("FLGCMISCR");
									giorniRangeFineDid = resultSetBatch.getBigDecimal("NUMGGRANGEFINEDID");
									codCpi = resultSetBatch.getString("CODCPI");

									switch (chiaveBatch.intValue()) {
									case Constants.APPUNTAMENTI: {
										if (motivoContatto == null || giorniContattoBatch == null || servizio == null
												|| servizio.equals("") || statoAppuntamento == null
												|| statoAppuntamento.equals("") || giorniPeriodoCheck == null
												|| tipoSMS == null || tipoSMS.equals("") || prgTipoEvidenza == null) {
											strNote = "PARAMETRI BATCH INVIO SMS APPUNTAMENTI MANCANTI. DATA LANCIO = "
													+ dataLancioBatch;
											parametriBatchOK = false;
										}
										break;
									}
									case Constants.AZIONI_PROGRAMMATE: {
										if (motivoContatto == null || giorniContattoBatch == null || prgAzione == null
												|| codEsitoAz == null || codEsitoAz.equals("")
												|| giorniPeriodoCheck == null || tipoSMS == null || tipoSMS.equals("")
												|| prgTipoEvidenza == null) {
											strNote = "PARAMETRI BATCH INVIO SMS AZIONI PROGRAMMATE MANCANTI. DATA LANCIO = "
													+ dataLancioBatch;
											parametriBatchOK = false;
										}
										break;
									}
									case Constants.CONFERMA_DID: {
										if (motivoContatto == null || giorniContattoBatch == null || tipoSMS == null
												|| tipoSMS.equals("") || prgTipoEvidenza == null) {
											strNote = "PARAMETRI BATCH INVIO SMS CONFERMA ANNUALE DID MANCANTI. DATA LANCIO = "
													+ dataLancioBatch;
											parametriBatchOK = false;
										}
										break;
									}
									case Constants.PERDITA_DISOCC: {
										if (flgIscrCM == null || flgIscrCM.equals("") || giorniPeriodoCheck == null
												|| tipoSMS == null || tipoSMS.equals("") || prgTipoEvidenza == null) {
											strNote = "PARAMETRI BATCH INVIO SMS PERDITA DISOCCUPAZIONE MANCANTI. DATA LANCIO = "
													+ dataLancioBatch;
											parametriBatchOK = false;
										}
										break;
									}
									default: {
										strNote = "BATCH NON GESTITO. DATA LANCIO = " + dataLancioBatch;
										parametriBatchOK = false;
									}
									}

									if (!parametriBatchOK) {
										logger.info(strNote);
										// aggiorna il campo strNote per tracciare la data e l'errore
										aggiornaErroreSchedulazione(connection, cdnutBatch, numkloBatch, strNote,
												prgBatch);
									} else {
										InviaSMS inviaSms = new InviaSMS(chiaveBatch.intValue(), servizio,
												statoAppuntamento, motivoContatto, giorniContattoBatch,
												giorniPeriodoCheck, prgAzione, codEsitoAz, codMotivoFineAttoDid,
												flgIscrCM, giorniRangeFineDid, tipoSMS, prgTipoEvidenza, prgSpi,
												cognomeSpi, nomeSpi, telSpi, codCpi);

										logger.info("INIZIO " + inviaSms.getDescrizioneBatch());

										int numContattiOK = inviaSms.invia();

										logger.info("FINE " + inviaSms.getDescrizioneBatch() + ". Contatti trattati: "
												+ numContattiOK);

										boolean isBatchAggiornato = aggiornaSchedulazione(connection, cdnutBatch,
												numkloBatch, prgBatch);

										if (!isBatchAggiornato) {
											logger.error(
													"ERRORE DURANTE L'AGGIORNAMENTO DELLA DATA SCHEDULAZIONE DEL BATCH. DATA LANCIO = "
															+ dataLancioBatch);
										}
									}
								} catch (Exception eConn) {
									logger.error("Errore inatteso durante la schedulazione del batch: " + prgBatch);
									strNote = "ERRORE INATTESO DURANTE LA SCHEDULAZIONE DEL BATCH. DATA LANCIO = "
											+ dataLancioBatch;
									// aggiorna il campo strNote per tracciare la data e l'errore
									aggiornaErroreSchedulazione(connection, cdnutBatch, numkloBatch, strNote, prgBatch);
								}
							}
						}
					}

					if (resultSetBatch != null) {
						resultSetBatch.close();
					}
					if (preparedStatementBatch != null) {
						preparedStatementBatch.close();
					}
				}
			} catch (Exception e) {
				logger.error("perform(Date, long)", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}

			final Date stopDate = new Date();
			logger.info("Batch Invio SMS STOP at:" + df.format(stopDate));
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchInvioSMS] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private boolean aggiornaSchedulazione(Connection connection, BigDecimal cdnutBatch, BigDecimal numkloBatch,
			BigDecimal progressivo) {

		boolean isBatchAggiornato = false;

		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(QUERY_UPDATE_PROGRAMMAZIONE_BATCH);

			preparedStatement.setBigDecimal(1, cdnutBatch);
			preparedStatement.setBigDecimal(2, numkloBatch);
			preparedStatement.setBigDecimal(3, progressivo);

			if (preparedStatement.executeUpdate() > 0) {
				isBatchAggiornato = true;
			}

		} catch (Exception e) {
			logger.error("Errore: aggiornamento schedulazione batch", e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
		return isBatchAggiornato;
	}

	private void aggiornaErroreSchedulazione(Connection connection, BigDecimal cdnutBatch, BigDecimal numkloBatch,
			String errore, BigDecimal progressivo) {
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(QUERY_UPDATE_ERRORE_PROGRAMMAZIONE_BATCH);

			preparedStatement.setBigDecimal(1, cdnutBatch);
			preparedStatement.setBigDecimal(2, numkloBatch);
			preparedStatement.setString(3, errore);
			preparedStatement.setBigDecimal(4, progressivo);

			preparedStatement.executeUpdate();
		} catch (Exception e) {
			logger.error("Errore: aggiornamento errore schedulazione batch", e);
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

}
