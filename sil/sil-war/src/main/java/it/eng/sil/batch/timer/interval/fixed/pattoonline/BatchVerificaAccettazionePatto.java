package it.eng.sil.batch.timer.interval.fixed.pattoonline;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.module.pattoonline.PattoOnLineModule;
import it.eng.sil.module.sifer.PartecipanteTirociniUtils;
import it.eng.sil.security.User;

@Singleton
public class BatchVerificaAccettazionePatto extends FixedTimerBatch {

	private static final BigDecimal USER_BATCH = new BigDecimal("190");

	private static final String TASK_ESEGUIBILE = "1";

	private static final String QUERY_ESEGUIBILITA = "SELECT                                    "
			+ "    nvl((                                 " + "        SELECT                            "
			+ "            ts_config_loc.strvalore       " + "        FROM                              "
			+ "            ts_config_loc                 "
			+ "            INNER JOIN de_tipo_config ON(ts_config_loc.codtipoconfig = de_tipo_config.codtipoconfig)  "
			+ "        WHERE                                       "
			+ "            ts_config_loc.strcodrif =(              "
			+ "                SELECT                              "
			+ "                    ts_generale.codprovinciasil     "
			+ "                FROM                                "
			+ "                    ts_generale                     "
			+ "                WHERE                               "
			+ "                    prggenerale = 1                 "
			+ "            )                                       "
			+ "            AND de_tipo_config.codtipoconfig = 'PTONLINE'               "
			+ "            AND trunc(ts_config_loc.datinizioval) <= trunc(sysdate)     "
			+ "            AND trunc(ts_config_loc.datfineval)> trunc(sysdate)         "
			+ "            ), '0') AS strvaloreconfig,                                 "
			+ "        (                                                               "
			+ "            SELECT                                                      "
			+ "                ts_generale.codprovinciasil                             "
			+ "            FROM                              " + "                ts_generale                   "
			+ "            WHERE                             " + "                prggenerale = 1               "
			+ "        ) AS codprovinciasil                  " + " FROM dual ";

	private static final String QUERY_PATTI_DA_VERIFICARE = "SELECT patto.prgpattolavoratore,               "
			+ "       lav.strcodicefiscale                   "
			+ "  FROM am_patto_lavoratore patto                           "
			+ " INNER JOIN an_lavoratore lav                              "
			+ "    ON patto.cdnlavoratore = lav.cdnlavoratore             "
			+ " INNER JOIN am_documento_coll doc_coll                     "
			+ "    ON (to_number(doc_coll.strchiavetabella) = patto.prgpattolavoratore) "
			+ " INNER JOIN am_documento am_doc                            "
			+ "    ON (am_doc.prgdocumento = doc_coll.prgdocumento AND    "
			+ "       am_doc.cdnlavoratore = patto.cdnlavoratore)         "
			+ " WHERE patto.codmonoaccettazione = 'D'                          " + "   AND patto.DATFINE is null "
			+ "   AND am_doc.codtipodocumento IN ('PT297', 'ACLA')        " + "   AND am_doc.codstatoatto = 'PR' ";

	private static final String QUERY_GET_UTENTE = "select strlogin, strnome, strcognome from ts_utente where cdnut = ?";

	static Logger logger = Logger.getLogger(BatchVerificaAccettazionePatto.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "18", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info("BatchVerificaAccettazionePatto START effectiveStartDate:" + df.format(effectiveStartDate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(effectiveStartDate);

			Connection connection = null;
			Statement statement = null;
			boolean oldAutoCommit = false;

			try {

				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);

				if (connection != null) {
					logger.debug("BatchVerificaAccettazionePatto. VERIFICA ESEGUIBILITA' BATCH");
					Statement statementEseguibilita = connection.createStatement();
					ResultSet resultSetEseguibilita = statementEseguibilita.executeQuery(QUERY_ESEGUIBILITA);
					String esitoEseguibilita = null;
					if (resultSetEseguibilita != null) {
						while (resultSetEseguibilita.next()) {
							esitoEseguibilita = resultSetEseguibilita.getString("strvaloreconfig");
						}
					}
					statementEseguibilita.close();
					resultSetEseguibilita.close();

					if (esitoEseguibilita != null && esitoEseguibilita.equalsIgnoreCase(TASK_ESEGUIBILE)) {
						logger.debug("BatchVerificaAccettazionePatto.INZIO TASK ESEGUIBILE");

						RequestContainer requestContainer = null;

						SessionContainer sessionContainer = null;
						try {
							requestContainer = new RequestContainer();

							sessionContainer = new SessionContainer(true);
						} catch (Exception e) {
							logger.error("Errore: batch verifica accettazione patto", e);
						}

						String username = null;
						String nome = null;
						String cognome = null;
						PreparedStatement psUser = null;
						// patti da accettare
						psUser = connection.prepareStatement(QUERY_GET_UTENTE);
						psUser.setBigDecimal(1, USER_BATCH);
						ResultSet rsUser = psUser.executeQuery();
						if (rsUser != null) {
							while (rsUser.next()) {
								username = rsUser.getString("STRLOGIN");
								nome = rsUser.getString("STRNOME");
								cognome = rsUser.getString("STRCOGNOME");
							}
						}
						psUser.close();
						rsUser.close();

						Statement statementDatiDaVerificare = null;
						statementDatiDaVerificare = connection.createStatement();
						ResultSet resultSetVerificareAccettazionePatto = statementDatiDaVerificare
								.executeQuery(QUERY_PATTI_DA_VERIFICARE);

						HashMap<BigDecimal, String> pattiDaControllare = new HashMap<BigDecimal, String>();
						if (resultSetVerificareAccettazionePatto != null) {
							boolean found = false;
							while (resultSetVerificareAccettazionePatto.next()) {
								found = true;
								BigDecimal prgPattoLavoratore = resultSetVerificareAccettazionePatto
										.getBigDecimal("prgpattolavoratore");
								String cfLav = resultSetVerificareAccettazionePatto.getString("strcodicefiscale");
								// ogni lavoratore lo aggiungo a pattiDaControllare
								pattiDaControllare.put(prgPattoLavoratore, cfLav);
							}
							if (!found) {
								logger.info(
										"BatchVerificaAccettazionePatto - Nessun patto in stato D (da accettare) trovato");
							}
						}
						statementDatiDaVerificare.close();
						resultSetVerificareAccettazionePatto.close();

						User user = new User(USER_BATCH.intValue(), username, nome, cognome);
						sessionContainer.setAttribute(User.USERID, user);
						requestContainer.setSessionContainer(sessionContainer);
						logger.debug("BatchVerificaAccettazionePatto UTENTE CONFIGURATO ");

						SourceBean request = new SourceBean("SERVICE_REQUEST");

						requestContainer.setServiceRequest(request);

						Iterator<BigDecimal> prgPattiIterator = pattiDaControllare.keySet().iterator();

						while (prgPattiIterator.hasNext()) {
							QueryExecutorObject qExec = null;
							DataConnection dc = null;
							try {
								BigDecimal prgPattoItem = (BigDecimal) prgPattiIterator.next();
								String cfLavItem = pattiDaControllare.get(prgPattoItem);
								PattoOnLineModule moduloPattoOnLine = new PattoOnLineModule();
								qExec = PartecipanteTirociniUtils.getQueryExecutorObject();
								dc = qExec.getDataConnection();
								dc.initTransaction();
								SourceBean response = new SourceBean("SERVICE_RESPONSE");
								moduloPattoOnLine.setReqContainer(requestContainer);
								moduloPattoOnLine.setBatchConnection(dc.getInternalConnection());
								moduloPattoOnLine.setQueryExecObj(qExec);
								moduloPattoOnLine.setPrgPattoLavoratore(prgPattoItem);
								logger.debug(
										"BatchVerificaAccettazionePatto - RichiestaPatto in corso (chiamata al service). CF lav: "
												+ cfLavItem + " - PrgPatto: " + prgPattoItem);
								moduloPattoOnLine.service(request, response);
								if (response.containsAttribute("ESITO")) {
									String esito = (String) response.getAttribute("ESITO");
									if (esito.equalsIgnoreCase("OK")) {
										dc.commitTransaction();
									} else {
										dc.rollBackTransaction();
									}
								}

							} catch (EMFInternalError e) {
								try {
									dc.rollBackTransaction();
									continue;
								} catch (EMFInternalError emf) {
									continue;
								}
							} finally {
								Utils.releaseResources(dc, null, null);
								qExec = null;
							}

						}
					} else {
						logger.debug(
								"BatchVerificaAccettazionePatto VERIFICA ACCETTAZIONE PATTO non eseguito: LA CONFIGURAZIONE NON PREVEDE LA SUA ESECUZIONE.");
					}
				}
			} catch (Exception e) {
				logger.error("Errore BatchVerificaAccettazionePatto: ", e);
			} catch (Throwable e) {
				logger.error("Errore BatchVerificaAccettazionePatto: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchVerificaAccettazionePatto] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
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