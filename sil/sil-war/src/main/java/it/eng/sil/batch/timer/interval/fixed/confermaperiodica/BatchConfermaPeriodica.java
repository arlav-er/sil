package it.eng.sil.batch.timer.interval.fixed.confermaperiodica;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.engiweb.framework.dbaccess.DataConnectionManager;

import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.mail.SendMail;

@Singleton
public class BatchConfermaPeriodica extends FixedTimerBatch {

	private static final String QUERY_MOTIVO_CONTATTO = " select prgmotcontatto " + " from de_motivo_contattoag "
			+ " where strdescrizione = 'segnalazione scadenza'";

	private static final String QUERY_GIORNI_CONFERMA_PERIODICA = " select strvalore " + " from ts_config_loc "
			+ " where codtipoconfig = 'GG_C_PER' "
			+ " and strcodrif = (select ts_generale.codprovinciasil from ts_generale) ";

	private static final String QUERY_RICERCA_OPERATORE = " select prgspi, cdnut " + " from ts_utente "
			+ " where cdnut = (select cdnut from ts_utente where strlogin = 'PORTALE') ";

	private static final String QUERY_RICERCA_LAVORATORI = " select " + "   lav.cdnlavoratore, " + "   lav.stremail, "
			+ "   col.codcpi, " + "   lav.strcell, " + "   lav.strnome, " + "   lav.strcognome, " + "   cpi.strtel, "
			+ "   spi.strnome strnomespi, " + "   spi.strcognome strcognomespi, "
			+ "   TO_CHAR(did.datdichiarazione,'DD/MM/YYYY') datdichiarazione, "
			+ "   TO_CHAR(per.datstimata,'DD/MM/YYYY') datstimata" + " from or_percorso_concordato per "
			+ " inner join or_colloquio col on col.prgcolloquio = per.prgcolloquio "
			+ " inner join an_lavoratore lav on lav.cdnlavoratore = col.cdnlavoratore "
			+ " inner join de_azione azione on azione.prgazioni = per.prgazioni "
			+ " inner join de_esito esito on esito.codesito = per.codesito "
			+ " inner join de_cpi cpi on cpi.codCpi = col.codCpi "
			+ " inner join an_spi spi on spi.prgspi = col.prgspi "
			+ " inner join am_elenco_anagrafico elenco on elenco.cdnlavoratore = lav.cdnlavoratore "
			+ " inner join am_dich_disponibilita did on elenco.prgelencoanagrafico = did.prgelencoanagrafico "
			+ " where esito.codesito = 'CC' " + " and lower(azione.strdescrizione) = lower('Conferma periodica') "
			+ " and (" + "     trunc(per.datstimata) = trunc(SYSDATE + ?) " +
			// " or trunc(per.datstimata) = trunc(SYSDATE - :giorni_conferma_periodica)" +
			" ) " + " and elenco.datcan is null " + " and did.datfine is null " + " and did.codstatoatto = 'PR'";

	private static final String QUERY_DATI_EMAIL = " select ts_email.stroggetto, ts_email.stremailmittente, ts_generale.strsmtpserver, ts_email.strcorpoemail "
			+ " from ts_email, ts_generale " + " where  ts_email.codtipoemail = 'CONFPER' ";

	private static final String QUERY_NEXT_CONTATTO = " select s_ag_contatto.nextval key from dual ";

	private static final String QUERY_INSERT_CONTATTO = " INSERT INTO AG_CONTATTO (" + "   PRGCONTATTO, "
			+ "   CODCPICONTATTO, " + "   DATCONTATTO, " + "   STRORACONTATTO, " + "   PRGSPICONTATTO, "
			+ "   TXTCONTATTO, " + "   STRIO, " + "   PRGTIPOCONTATTO, " + "   CDNLAVORATORE, " + "   CDNUTINS, "
			+ "   DTMINS, " + "   CDNUTMOD, " + "   DTMMOD, " + "   STRCELLSMSINVIO, " + "   flginviatosms,"
			+ "   prgmotcontatto " + " ) VALUES (?,?,to_date(?,'dd/mm/yyyy'),?,?,?,?,?,?,?,sysdate,?,sysdate,?,?,?) ";

	private static final String QUERY_UPDATE_CONTATTO = " UPDATE ag_contatto " + "  SET flginviatosms = ?, "
			+ "      NUMKLOCONTATTO = NUMKLOCONTATTO + 1 " + " WHERE prgcontatto = ? ";

	// private static long remainingRepetitions = 0;

	static Logger logger = Logger.getLogger(BatchConfermaPeriodica.class.getName());

	// Commentato in quanto non più utilizzato
	// @Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "02", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info(
					"Batch Notturno BatchConfermaPeriodica START effectiveStartDate:" + df.format(effectiveStartDate));

			Connection connection = null;
			Statement statement = null;
			boolean oldAutoCommit = false;

			try {

				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(true);

				BigDecimal prgMotivoContatto = null;
				Integer giorniConfermaPeriodica = null;
				BigDecimal prgSpi = null;
				BigDecimal cdnUt = null;

				String strOggettoEmail = null;
				String strMittenteEmail = null;
				String strSmtpserverEmail = null;
				String strCorpoEmail = null;

				BigDecimal cdnLavoratore = null;
				String strEmail = null;
				String codCpi = null;
				String strCell = null;
				String strNomeLavoratore = null;
				String strCognomeLavoratore = null;
				String strTelCpi = null;
				String strNomeSpi = null;
				// String strCognomeSpi = null;
				String strDataDid = null;
				String strDataStimata = null;

				ResultSet resultSet = null;
				boolean isBatchEnabled = false;

				if (connection != null) {

					statement = connection.createStatement();

					// QUERY MOTIVO CONTATTO

					resultSet = statement.executeQuery(QUERY_MOTIVO_CONTATTO);
					if (resultSet != null) {
						while (resultSet.next()) {
							prgMotivoContatto = resultSet.getBigDecimal("prgmotcontatto");
						}
					}
					resultSet.close();

					// QUERY GIORNI CONFERMA PERIODICA

					resultSet = statement.executeQuery(QUERY_GIORNI_CONFERMA_PERIODICA);
					if (resultSet != null) {
						while (resultSet.next()) {
							giorniConfermaPeriodica = Integer.parseInt(resultSet.getString("strvalore"));
						}
					}
					resultSet.close();

					// QUERY RICERCA OPERATORE

					resultSet = statement.executeQuery(QUERY_RICERCA_OPERATORE);
					if (resultSet != null) {
						while (resultSet.next()) {
							prgSpi = resultSet.getBigDecimal("prgspi");
							cdnUt = resultSet.getBigDecimal("cdnut");
						}
					}
					resultSet.close();

					// QUERY DATI EMAIL

					resultSet = statement.executeQuery(QUERY_DATI_EMAIL);
					if (resultSet != null) {
						while (resultSet.next()) {

							strOggettoEmail = resultSet.getString("stroggetto");
							strMittenteEmail = resultSet.getString("stremailmittente");
							strSmtpserverEmail = resultSet.getString("strsmtpserver");
							strCorpoEmail = resultSet.getString("strcorpoemail");

							// riga trovata, si presuppone che il batch sia abilitato
							isBatchEnabled = true;

						}
					}
					resultSet.close();

					if (isBatchEnabled) {

						// QUERY RICERCA LAVORATORI

						PreparedStatement preparedStatement = null;
						preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI);
						preparedStatement.setInt(1, giorniConfermaPeriodica);
						resultSet = preparedStatement.executeQuery();

						if (resultSet != null) {

							while (resultSet.next()) {

								cdnLavoratore = resultSet.getBigDecimal("cdnlavoratore");
								strEmail = resultSet.getString("stremail");
								codCpi = resultSet.getString("codcpi");
								strCell = resultSet.getString("strcell");
								strNomeLavoratore = resultSet.getString("strnome");
								strCognomeLavoratore = resultSet.getString("strcognome");
								strTelCpi = resultSet.getString("strtel");
								strNomeSpi = resultSet.getString("strnomespi");
								// strCognomeSpi = resultSet.getString("strcognomespi");
								strDataDid = resultSet.getString("datdichiarazione");
								strDataStimata = resultSet.getString("datstimata");

								strCorpoEmail = "Salve " + strNomeLavoratore + " " + strCognomeLavoratore + ",\n\n"
										+ "a seguito della Dichiarazione di Immediata Disponibilità da te rilasciata in data "
										+ strDataDid + ", " + "ti ricordiamo che puoi confermare entro il "
										+ strDataStimata + " il tuo stato di Disoccupazione tramite "
										+ "l'operazione di Conferma Periodica presente all'interno dei Servizi Amministrativi On-Line sul Portale Lavoro Per Te\n\n"
										+ "Se ti è impossibile eseguire l'operazione online, puoi contattare il CPI telefonicamente al numero "
										+ strTelCpi + " e " + "chiedere di " + strNomeSpi
										+ ", per fissare un appuntamento.";

								// INSERIMENTO CONTATTO

								BigDecimal prgContatto = inserisciContatto(connection, cdnLavoratore, strEmail,
										strMittenteEmail, strOggettoEmail, strCorpoEmail, codCpi, strCell, prgSpi,
										cdnUt, prgMotivoContatto);

								if (prgContatto != null) {

									// INVIO EMAIL

									boolean isEmailSpedita = inviaEmail(strOggettoEmail, strCorpoEmail, strEmail,
											strMittenteEmail, null, // altriDestinatariTO,
											null, // altriDestinatariCC
											null, // altriDestinatariBCC
											strSmtpserverEmail);

									// AGGIORNAMENTO CONTATTO

									if (isEmailSpedita) {
										boolean isContattoAggiornato = aggiornaContatto(connection, prgContatto);
										if (!isContattoAggiornato) {
											logger.error("Errore durante l'aggiornamento del contatto: " + prgContatto);
										}
									}

								}

							}
						}

						preparedStatement.close();
						resultSet.close();

					} else {
						logger.info(
								"Batch Notturno BatchConfermaPeriodica - Entry nella TS_EMAIL non presente, nessuna email verrà inviata.");
					}

				}

			} catch (Exception e) {
				logger.error("perform(Date, long)", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}

			final Date stopDate = new Date();
			logger.info("Batch Notturno BatchConfermaPeriodica STOP at:" + df.format(stopDate));
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchConferimaPeriodica] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
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

	public static boolean inviaEmail(String strOggettoEmail, String strMessaggioEmail, String strEmailTo,
			String strMittenteEmail, String altriDestinatariTO, String altriDestinatariCC, String altriDestinatariBCC,
			String strSmtpServer) {

		boolean isEmailSpedita = false;

		String to = strEmailTo;
		// if (altriDestinatariTO != null && !"".equalsIgnoreCase(altriDestinatariTO)) {
		// to += "," + altriDestinatariTO;
		// }

		String cc = null;
		// if (altriDestinatariCC != null && !"".equalsIgnoreCase(altriDestinatariCC)) {
		// cc = altriDestinatariCC;
		// }

		String bcc = null;
		// String bcc = strMittenteEmail;
		// if (altriDestinatariBCC != null && !"".equalsIgnoreCase(altriDestinatariBCC)) {
		// bcc += "," + altriDestinatariBCC;
		// }

		try {

			SendMail sendMail = new SendMail();
			sendMail.setSMTPServer(strSmtpServer);
			sendMail.setFromRecipient(strMittenteEmail);
			sendMail.setToRecipient(to);
			sendMail.setCcRecipient(cc);
			sendMail.setBccRecipient(bcc);
			sendMail.setSubject(strOggettoEmail);
			sendMail.setBody(strMessaggioEmail);

			sendMail.send();

			isEmailSpedita = true;

		} catch (Exception e) {
			logger.error("Errore: invio email", e);
		}
		return isEmailSpedita;

	}

	public static BigDecimal inserisciContatto(Connection connection, BigDecimal cdnLavoratore,
			String destinatarioEmail, String mittenteEmail, String oggettoEmail, String mailMessage, String codCpi,
			String cellLavoratore, BigDecimal prgSpi, BigDecimal cdnUtInsMod, BigDecimal prgMotivoContatto) {

		BigDecimal prgContatto = null;

		Statement statement = null;
		PreparedStatement preparedStatement = null;

		try {

			statement = connection.createStatement();

			ResultSet resultSet = null;

			// QUERY NEXT CONTATTO

			resultSet = statement.executeQuery(QUERY_NEXT_CONTATTO);
			if (resultSet != null) {
				while (resultSet.next()) {
					prgContatto = resultSet.getBigDecimal("key");
				}
			}
			resultSet.close();

			if (prgContatto != null) {

				Date oggi = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String strDataContatto = formatter.format(oggi);
				formatter = new SimpleDateFormat("HH:mm");
				String strOraContatto = formatter.format(oggi);

				String txtContatto = "Inviata a: " + destinatarioEmail + ", " + mittenteEmail + "\nOggetto: "
						+ oggettoEmail + "\n\n" + mailMessage;

				preparedStatement = connection.prepareStatement(QUERY_INSERT_CONTATTO);
				preparedStatement.setBigDecimal(1, prgContatto);
				preparedStatement.setString(2, codCpi);
				preparedStatement.setString(3, strDataContatto);
				preparedStatement.setString(4, strOraContatto);
				preparedStatement.setBigDecimal(5, prgSpi);
				preparedStatement.setString(6, txtContatto);
				preparedStatement.setString(7, "O");
				preparedStatement.setBigDecimal(8, new BigDecimal(3)); // prg tipo contatto EMAIL
				preparedStatement.setBigDecimal(9, cdnLavoratore);
				preparedStatement.setBigDecimal(10, cdnUtInsMod);
				preparedStatement.setBigDecimal(11, cdnUtInsMod);
				preparedStatement.setString(12, cellLavoratore);
				preparedStatement.setString(13, "N");
				preparedStatement.setBigDecimal(14, prgMotivoContatto);

				if (preparedStatement.executeUpdate() <= 0) {
					prgContatto = null;
				}

			}

		} catch (Exception e) {

			logger.error("Errore: inserimento contatto email", e);

		} finally {

			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}

		return prgContatto;

	}

	public static boolean aggiornaContatto(Connection connection, BigDecimal prgContatto) {

		boolean isContattoAggiornato = false;

		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(QUERY_UPDATE_CONTATTO);

			preparedStatement.setString(1, "S");
			preparedStatement.setBigDecimal(2, prgContatto);

			if (preparedStatement.executeUpdate() > 0) {
				isContattoAggiornato = true;
			}

		} catch (Exception e) {
			logger.error("Errore: aggiornamento contatto email", e);
		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}

		return isContattoAggiornato;

	}

}
