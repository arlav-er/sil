package it.eng.sil.batch.timer.interval.fixed.sap;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.module.anag.GestioneInviaSapYG;
import it.eng.sil.module.sifer.PartecipanteTirociniUtils;
import it.eng.sil.security.User;

@Singleton
public class BatchInvioAutomatico extends FixedTimerBatch {

	private static final BigDecimal USER_BATCH = new BigDecimal("100");

	private static final String DATE_BATCH_PREGRESSO = "31/12/2222";

	private static final String QUERY_ESEGUIBILITA = " select strvalore " + " from ts_config_loc "
			+ " where codtipoconfig = 'INVATSAP' "
			+ " and strcodrif = (select ts_generale.codprovinciasil from ts_generale where ts_generale.prggenerale = 1) ";

	private static final String QUERY_GET_UTENTE = "select strlogin, strnome, strcognome from ts_utente where cdnut = ?";

	private static final String QUERY_DATA_BATCH_INVIO_MASSIVO = " select to_char(dateTL, 'dd/mm/yyyy') as DATETL, "
			+ " MAXCARICOSAP, NUMMAXMINUTI " + " from ts_monitoraggio " + " where codambito = 'INVATSAP' ";

	private static final String QUERY_RICERCA_LAVORATORI_SAP = "SELECT DISTINCT CDNLAV, CFLAV FROM "
			+ " (SELECT COLL.CDNLAVORATORE CDNLAV," + " LAV.STRCODICEFISCALE CFLAV " + " FROM OR_COLLOQUIO COLL "
			+ " INNER JOIN OR_PERCORSO_CONCORDATO PERCORSO ON (COLL.PRGCOLLOQUIO = PERCORSO.PRGCOLLOQUIO) "
			+ " INNER JOIN AN_LAVORATORE LAV ON (LAV.CDNLAVORATORE = COLL.CDNLAVORATORE) "
			+ " WHERE TRUNC(PERCORSO.DTMMOD) BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TRUNC(SYSDATE) "
			+ " AND NOT EXISTS (SELECT 1 FROM TS_TRACCIAMENTO_SAP TRACCIAMENTO WHERE TRACCIAMENTO.CDNLAVORATORE = COLL.CDNLAVORATORE AND "
			+ " TRACCIAMENTO.DATINVIOMIN >= PERCORSO.DTMMOD) " + " AND LAV.STRCODICEFISCALE NOT IN "
			+ "  (SELECT DISTINCT STRCODICEFISCALE FROM TS_INVIO_AUT_SAP WHERE FLGINVIOSAP is NULL) " + " UNION "
			+ " SELECT ELENCO.CDNLAVORATORE CDNLAV, " + " LAV.STRCODICEFISCALE CFLAV "
			+ " FROM AM_DICH_DISPONIBILITA DID "
			+ " INNER JOIN AM_ELENCO_ANAGRAFICO ELENCO ON (DID.PRGELENCOANAGRAFICO = ELENCO.PRGELENCOANAGRAFICO) "
			+ " INNER JOIN AN_LAVORATORE LAV ON (LAV.CDNLAVORATORE = ELENCO.CDNLAVORATORE) "
			+ " WHERE DID.CODSTATOATTO = 'PR' AND TRUNC(DID.DTMMOD) BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TRUNC(SYSDATE) "
			+ " AND NOT EXISTS (SELECT 1 FROM TS_TRACCIAMENTO_SAP TRACCIAMENTO1 WHERE TRACCIAMENTO1.CDNLAVORATORE = ELENCO.CDNLAVORATORE AND "
			+ " TRACCIAMENTO1.DATINVIOMIN >= DID.DTMMOD ) " + " AND LAV.STRCODICEFISCALE NOT IN "
			+ "  (SELECT DISTINCT STRCODICEFISCALE FROM TS_INVIO_AUT_SAP WHERE FLGINVIOSAP is NULL) " + " )";

	private static final String QUERY_INSERT_INVIO_AUT_SAP = " INSERT INTO TS_INVIO_AUT_SAP (" + "   PRGINVIOAUTSAP, "
			+ "   CDNLAVORATORE, " + "   STRCODICEFISCALE, " + "   DATSELEZIONE, " + "   FLGINVIOSAP, "
			+ "   DATINVIO, " + "   DATESITOMIN, " + "   STRESITOMIN," + "   CODMINSAP," + "   PRGESTRAZIONESAP, "
			+ "   STRNOTE" + " ) VALUES "
			+ "(S_TS_INVIO_AUT_SAP.nextval,?,?,sysdate,null,null, null, null, null, null, null) ";

	private static final String QUERY_LAVORATORI_ACCORPATI = " SELECT DISTINCT CDNLAVORATORE "
			+ " FROM TS_INVIO_AUT_SAP " + " WHERE  FLGINVIOSAP IS NULL " + " AND CDNLAVORATORE NOT IN "
			+ " (SELECT DISTINCT CDNLAVORATORE FROM AN_LAVORATORE) ";

	private static final String UPDATE_LAVORATORI_ACCORPATI_TS_INVIO_AUT_SAP = " UPDATE TS_INVIO_AUT_SAP "
			+ "  SET DATESITOMIN = sysdate, " + "      DATINVIO = sysdate, " + "      FLGINVIOSAP = 'X',  "
			+ "      STRNOTE = 'LAVORATORE ACCORPATO' " + " WHERE CDNLAVORATORE = ? ";

	private static final String QUERY_SELECT_SAP_DA_INVIARE = " SELECT PRGINVIOAUTSAP, CDNLAVORATORE "
			+ " FROM TS_INVIO_AUT_SAP " + " WHERE  FLGINVIOSAP IS NULL " + " AND ROWNUM <= ?";

	private static final String QUERY_SELECT_SAP_DA_INVIARE_NO_LIMIT = " SELECT PRGINVIOAUTSAP, CDNLAVORATORE "
			+ " FROM TS_INVIO_AUT_SAP " + " WHERE  FLGINVIOSAP IS NULL ";

	private static final String QUERY_UPDATE_INVIO_AUT_SAP = " UPDATE TS_INVIO_AUT_SAP " + "  SET FLGINVIOSAP = ?, "
			+ "      DATINVIO = sysdate, " + "      PRGESTRAZIONESAP = ?, " + "      STRNOTE = ? "
			+ " WHERE PRGINVIOAUTSAP = ? ";

	private static final String QUERY_GET_PRGINVIOAUTSAP = " SELECT PRGESTRAZIONESAP, PRGINVIOAUTSAP "
			+ " FROM TS_INVIO_AUT_SAP " + " WHERE  FLGINVIOSAP IS NOT NULL " + " AND CODMINSAP IS NULL "
			+ " AND STRESITOMIN IS NULL " + " AND DATESITOMIN IS NULL" + " ORDER BY PRGESTRAZIONESAP";

	private static final String QUERY_SELECT_DATI_UPDATE = " SELECT CODMINSAP, "
			+ " TO_CHAR(DATINVIOMIN, 'dd/mm/yyyy') as DATLASTINVIOSAP, " + " STRESITOMIN "
			+ " FROM TS_TRACCIAMENTO_SAP " + " WHERE  PRGESTRAZIONESAP = ?";

	private static final String QUERY_UPDATE_INVIO_AUT_SAP_STEP3 = " UPDATE TS_INVIO_AUT_SAP "
			+ "  SET DATESITOMIN = to_date(?, 'dd/mm/yyyy'), " + "      STRESITOMIN = ?, " + "      CODMINSAP = ? "
			+ " WHERE PRGINVIOAUTSAP = ? " + " AND PRGESTRAZIONESAP = ?";

	private static final String QUERY_UPDATE_DATA_BATCH_INVIO_MASSIVO = "update ts_monitoraggio set dateTL = sysdate where codambito = 'INVATSAP' ";

	static Logger logger = Logger.getLogger(BatchInvioAutomatico.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "18", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info("BatchInvioAutomatico START effectiveStartDate:" + df.format(effectiveStartDate));

			Calendar cal = Calendar.getInstance();
			cal.setTime(effectiveStartDate);
			int oraInizio = cal.get(Calendar.HOUR_OF_DAY);
			int minutiInizio = cal.get(Calendar.MINUTE);

			Connection connection = null;
			Statement statement = null;
			boolean oldAutoCommit = false;
			boolean batchTerminatoDurata = false;

			try {

				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
				ResultSet resultSet = null;

				if (connection != null) {
					logger.debug("BatchInvioAutomatico. VERIFICA ESEGUIBILITA' BATCH");
					Statement statementEseguibilita = connection.createStatement();
					ResultSet resultSetEseguibilita = statementEseguibilita.executeQuery(QUERY_ESEGUIBILITA);
					String esitoEseguibilita = null;
					if (resultSetEseguibilita != null) {
						while (resultSetEseguibilita.next()) {
							esitoEseguibilita = resultSetEseguibilita.getString("strvalore");
						}
					}
					statementEseguibilita.close();
					resultSetEseguibilita.close();

					if (StringUtils.isFilledNoBlank(esitoEseguibilita) && esitoEseguibilita.equalsIgnoreCase("S")) {
						logger.debug("BatchInvioAutomatico.INZIO ESECUZIONE STEP 1");

						String dataEsecuzioneBatch = null;
						BigDecimal limit = null;
						BigDecimal numMaxMinuti = null;
						HashMap<BigDecimal, String> lavSendSap = new HashMap<BigDecimal, String>();
						Statement statementDataBatch = null;
						statementDataBatch = connection.createStatement();
						ResultSet resultSetDataBatch = statementDataBatch.executeQuery(QUERY_DATA_BATCH_INVIO_MASSIVO);
						if (resultSetDataBatch != null) {
							while (resultSetDataBatch.next()) {
								dataEsecuzioneBatch = resultSetDataBatch.getString("DATETL");
								limit = resultSetDataBatch.getBigDecimal("MAXCARICOSAP");
								numMaxMinuti = resultSetDataBatch.getBigDecimal("NUMMAXMINUTI");
							}
						}
						statementDataBatch.close();
						resultSetDataBatch.close();

						if (dataEsecuzioneBatch != null) {
							boolean isPregresso = false;
							if (dataEsecuzioneBatch.equalsIgnoreCase(DATE_BATCH_PREGRESSO)) {
								isPregresso = true;
								logger.debug("BatchInvioAutomatico: GESTIONE PREGRESSO ");
							}
							BigDecimal cdnLavoratore = null;
							String codiceFiscaleLavoratore = null;
							// QUERY RICERCA LAVORATORI
							PreparedStatement preparedStatement = null;
							preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI_SAP);
							preparedStatement.setString(1, dataEsecuzioneBatch);
							preparedStatement.setString(2, dataEsecuzioneBatch);
							resultSet = preparedStatement.executeQuery();

							if (resultSet != null) {
								while (resultSet.next()) {
									cdnLavoratore = resultSet.getBigDecimal("CDNLAV");
									codiceFiscaleLavoratore = resultSet.getString("CFLAV");
									// ogni lavoratore lo aggiungo a lavSendSap
									lavSendSap.put(cdnLavoratore, codiceFiscaleLavoratore);
								}
							}
							preparedStatement.close();
							resultSet.close();
							logger.debug("BatchInvioAutomatico.STEP 1: ci sono " + lavSendSap.size()
									+ " sap da inserire nella tabella TS_INVIO_AUT_SAP");
							popolaSapMassivo(lavSendSap, connection);
							if (!isPregresso) {
								logger.debug("BatchInvioAutomatico: aggiornamento datetl in ts_monitoraggio");
								aggiornaDataBatch(connection);
							}
							connection.commit();
							logger.debug("BatchInvioAutomatico.FINE ESECUZIONE STEP 1");
							logger.debug("BatchInvioAutomatico.INZIO ESECUZIONE STEP 2");
							lavoratoriAccorpati(connection);
							connection.commit();
							batchTerminatoDurata = invioSapMassivo(connection, isPregresso, limit, oraInizio,
									minutiInizio, numMaxMinuti);
							connection.commit();
							logger.debug("BatchInvioAutomatico.FINE ESECUZIONE STEP 2");
							logger.debug("BatchInvioAutomatico.INZIO ESECUZIONE STEP 3");
							ritornoMinistero(connection);
							connection.commit();
							logger.debug("BatchInvioAutomatico.FINE ESECUZIONE STEP 3");
						} else {
							logger.error("Settare la data ultimo lancio del BatchInvioAutomatico");
						}
					} else {
						logger.debug(
								"BatchInvioAutomatico INVIO AUTOMATICO SAP non eseguito: LA CONFIGURAZIONE NON PREVEDE LA SUA ESECUZIONE.");
					}
				}
			} catch (Exception e) {
				logger.error("Errore BatchInvioAutomatico: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}

			final Date stopDate = new Date();
			if (batchTerminatoDurata) {
				logger.info("BatchInvioAutomatico STOP superamento orario at:" + df.format(stopDate));
			} else {
				logger.info("BatchInvioAutomatico STOP at:" + df.format(stopDate));
			}
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchInvioAutomatico] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void lavoratoriAccorpati(Connection connection) {
		/* esclusione lavoratori accorpati */
		PreparedStatement sLavAcc = null;
		try {
			sLavAcc = connection.prepareStatement(QUERY_LAVORATORI_ACCORPATI);
			logger.debug(
					"BatchInvioAutomatico. STEP2 - CERCO EVENTUALI LAVORATORI ACCORPATI PER ESCLUDERLI DALL'INVIO SAP");
			ResultSet rsLavAcc = sLavAcc.executeQuery();
			if (rsLavAcc != null) {
				logger.debug("BatchInvioAutomatico. STEP2 - AGGIORNO TS_INVIO_AUT_SAP PER LAVORATORI ACCORPATI");
				while (rsLavAcc.next()) {
					BigDecimal cdnLav = rsLavAcc.getBigDecimal("CDNLAVORATORE");
					PreparedStatement prepUpd = connection
							.prepareStatement(UPDATE_LAVORATORI_ACCORPATI_TS_INVIO_AUT_SAP);
					prepUpd.setBigDecimal(1, cdnLav);
					prepUpd.executeUpdate();
					prepUpd.close();
				}
			}
			sLavAcc.close();
			rsLavAcc.close();
		} catch (Exception e) {
			logger.error("Errore: lavoratoriAccorpati", e);
		} finally {
			if (sLavAcc != null) {
				try {
					sLavAcc.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
		/* fine esclusione lavoratori accorpati */
	}

	private static void aggiornaDataBatch(Connection connection) {

		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(QUERY_UPDATE_DATA_BATCH_INVIO_MASSIVO);
			preparedStatement.executeUpdate();
			preparedStatement.close();

		} catch (Exception e) {
			logger.error("Errore: aggiornamento data batch invio massivo sap", e);
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

	@SuppressWarnings("rawtypes")
	private static void popolaSapMassivo(HashMap<BigDecimal, String> lavSendSap, Connection connection) {
		Iterator cdnLavoratori = lavSendSap.keySet().iterator();
		while (cdnLavoratori.hasNext()) {
			BigDecimal cdnLavoratore = (BigDecimal) cdnLavoratori.next();
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(QUERY_INSERT_INVIO_AUT_SAP);
				preparedStatement.setBigDecimal(1, cdnLavoratore);
				preparedStatement.setString(2, lavSendSap.get(cdnLavoratore));
				preparedStatement.executeUpdate();
				preparedStatement.close();
			} catch (SQLException e) {
				logger.error("BatchInvioAutomatico. STEP1 - Eccezione gestita." + e.getMessage());
				continue;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private static boolean invioSapMassivo(Connection connection, boolean isPregresso, BigDecimal limit, int oraInizio,
			int minutiInizio, BigDecimal numMaxMinuti) throws Exception {
		RequestContainer requestContainer = null;
		// ResponseContainer responseContainer = null;
		SessionContainer sessionContainer = null;
		try {
			requestContainer = new RequestContainer();
			// responseContainer = new ResponseContainer();
			sessionContainer = new SessionContainer(true);
		} catch (Exception e) {
			logger.error("Errore: batch invio massivo sap", e);
		}
		String username = null;
		String nome = null;
		String cognome = null;
		PreparedStatement psUser = null;

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
		User user = new User(USER_BATCH.intValue(), username, nome, cognome);
		sessionContainer.setAttribute(User.USERID, user);
		requestContainer.setSessionContainer(sessionContainer);
		SourceBean request = new SourceBean("SERVICE_REQUEST");
		request.setAttribute("INVIASAPFROMDID", "S");
		requestContainer.setServiceRequest(request);

		PreparedStatement sInvioSap = null;
		if (limit != null && limit.intValue() > 0) {
			sInvioSap = connection.prepareStatement(QUERY_SELECT_SAP_DA_INVIARE);
			sInvioSap.setBigDecimal(1, limit);
			logger.debug("BatchInvioAutomatico. STEP2 - MAX CARICO: " + limit);
		} else {
			sInvioSap = connection.prepareStatement(QUERY_SELECT_SAP_DA_INVIARE_NO_LIMIT);
			logger.debug(
					"BatchInvioAutomatico. STEP2 - SELEZIONO TUTTE LE RIGHE CON FLAGINVIO NULL, MAX CARICO NON SETTATO: è null o valore < 0");
		}

		ResultSet rsInvioSap = sInvioSap.executeQuery();
		HashMap<BigDecimal, BigDecimal> cdnPrgInvioSap = new HashMap<BigDecimal, BigDecimal>();
		if (rsInvioSap != null) {
			while (rsInvioSap.next()) {
				BigDecimal cdnLav = rsInvioSap.getBigDecimal("CDNLAVORATORE");
				BigDecimal prgInvio = rsInvioSap.getBigDecimal("PRGINVIOAUTSAP");
				cdnPrgInvioSap.put(cdnLav, prgInvio);
			}
		}
		sInvioSap.close();
		rsInvioSap.close();

		Iterator cdnLavoratoriSap = cdnPrgInvioSap.keySet().iterator();
		boolean batchTerminatoDurata = false;

		while (cdnLavoratoriSap.hasNext() && !batchTerminatoDurata) {
			QueryExecutorObject qExec = null;
			DataConnection dc = null;
			BigDecimal cdnLav = (BigDecimal) cdnLavoratoriSap.next();
			BigDecimal prgInvioAut = cdnPrgInvioSap.get(cdnLav);
			GestioneInviaSapYG moduloSap = new GestioneInviaSapYG();
			try {
				qExec = PartecipanteTirociniUtils.getQueryExecutorObject();
				dc = qExec.getDataConnection();
				dc.initTransaction();
				/*
				 * ora solo sap 2 qExec.setStatement(SQLStatements.getStatement("SELECT_DATA_SAP2"));
				 * qExec.setType(QueryExecutorObject.SELECT); SourceBean sbDataSap2 = (SourceBean) qExec.exec();
				 */
				List<DataField> paramsLav = new ArrayList<DataField>();
				paramsLav.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLav));

				qExec.setStatement(SQLStatements.getStatement("SELECT_MOVIMENTI_RIPETUTI"));
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setInputParameters(paramsLav);
				SourceBean sbMovimentiRipetuti = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("SELECT_MOVIMENTI_SUCCESSIVI"));
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setInputParameters(paramsLav);
				SourceBean sbMovimentiSuccessivi = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("SELECT_MOVIMENTI_PRECEDENTI"));
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setInputParameters(paramsLav);
				SourceBean sbMovimentiPrecedenti = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("SELECT_MOVIMENTI_SUCCESSIVI_APPROFONDITA"));
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setInputParameters(paramsLav);
				SourceBean sbMovimentiSuccApprofondita = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("SELECT_MOVIMENTI_PRECEDENTI_APPROFONDITA"));
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setInputParameters(paramsLav);
				SourceBean sbMovimentiPrecApprofondita = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("SELECT_MOVIMENTI_NO_SUCCESSIVO_PUNTATO_DA_PREC"));
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setInputParameters(paramsLav);
				SourceBean sbMovimentiNoSuccPuntatoPrec = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("SELECT_MOVIMENTI_NO_PRECEDENTE_PUNTATO_DA_SUCC"));
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setInputParameters(paramsLav);
				SourceBean sbMovimentiNoPrecPuntatoSucc = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("GET_AN_LAVORATORE_ANAG"));
				qExec.setType(QueryExecutorObject.SELECT);
				List<DataField> params = new ArrayList<DataField>();
				params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLav));
				qExec.setInputParameters(params);
				SourceBean sbAnLavoratore = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("GET_CPI_AN_LAVORATORE"));
				qExec.setType(QueryExecutorObject.SELECT);
				List<DataField> paramsCPI = new ArrayList<DataField>();
				paramsCPI.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLav));
				qExec.setInputParameters(paramsCPI);
				SourceBean sbCpiLavoratore = (SourceBean) qExec.exec();

				qExec.setStatement(SQLStatements.getStatement("SELECT_EXIST_SAP_LAVORATORE"));
				qExec.setType(QueryExecutorObject.SELECT);
				List<DataField> paramsSAP = new ArrayList<DataField>();
				paramsSAP.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLav));
				qExec.setInputParameters(paramsSAP);
				SourceBean sbSpLavoratore = (SourceBean) qExec.exec();

				// segnalazione redmine #7011 - 18/07/2018
				// qExec.setStatement(SQLStatements.getStatement("SELECT_CODCPIMIN_PROVINCIALI"));
				qExec.setStatement(SQLStatements.getStatement("SELECT_CODCPIMIN_PROV_REG"));
				qExec.setType(QueryExecutorObject.SELECT);
				List<DataField> paramsVoid = new ArrayList<DataField>();
				qExec.setInputParameters(paramsVoid);
				SourceBean sbCpiMinProvincia = (SourceBean) qExec.exec();

				request.updAttribute("CDNLAVORATORE", cdnLav.toString());
				SourceBean response = new SourceBean("SERVICE_RESPONSE");

				moduloSap.setReqContainer(requestContainer);
				/*
				 * ora solo sap 2 moduloSap.setSbDataSap2(sbDataSap2);
				 */
				moduloSap.setSbMovimentiRipetuti(sbMovimentiRipetuti);
				moduloSap.setSbMovimentiSuccessivi(sbMovimentiSuccessivi);
				moduloSap.setSbMovimentiPrecedenti(sbMovimentiPrecedenti);
				moduloSap.setSbMovimentiSuccApprofondita(sbMovimentiSuccApprofondita);
				moduloSap.setSbMovimentiPrecApprofondita(sbMovimentiPrecApprofondita);
				moduloSap.setSbMovimentiNoSuccPuntatoPrec(sbMovimentiNoSuccPuntatoPrec);
				moduloSap.setSbMovimentiNoPrecPuntatoSucc(sbMovimentiNoPrecPuntatoSucc);
				moduloSap.setSbAnLavoratore(sbAnLavoratore);
				moduloSap.setSbCpiLavoratore(sbCpiLavoratore);
				moduloSap.setSbSpLavoratore(sbSpLavoratore);
				moduloSap.setSbCpiMinProvincia(sbCpiMinProvincia);
				moduloSap.setBatchConnection(dc.getInternalConnection());
				moduloSap.setQueryExecObj(qExec);
				logger.debug("BatchInvioAutomatico. STEP2 - Invio sap in corso (chiamata al service)");
				moduloSap.service(request, response);
				aggiornaInvioAutSap(connection, prgInvioAut, moduloSap);
				dc.commitTransaction();
			} catch (Exception e) {
				try {
					if (moduloSap != null) {
						aggiornaInvioAutSap(connection, prgInvioAut, moduloSap);
					}
					// allineato con il funzionamento del pulsante "INVIA SAP"
					dc.commitTransaction();
				} catch (EMFInternalError e1) {
					logger.error("Errore: batch invio massivo sap: ", e1);
				}
				if (!moduloSap.isErroreGestito()) {
					logger.error("Errore: batch invio massivo sap: ", e);
				}
				continue;
			} finally {
				Utils.releaseResources(dc, null, null);
				qExec = null;
				if (numMaxMinuti != null && numMaxMinuti.intValue() > 0) {
					if (checkStopValidazioneOrario(oraInizio, minutiInizio, numMaxMinuti)) {
						batchTerminatoDurata = true;
					}
				}
			}
		}
		return batchTerminatoDurata;
	}

	private static void aggiornaInvioAutSap(Connection connection, BigDecimal prgInvioAut,
			GestioneInviaSapYG moduloSap) {
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(QUERY_UPDATE_INVIO_AUT_SAP);
			preparedStatement.setString(1, moduloSap.getFlgInvioSap());
			preparedStatement.setBigDecimal(2, moduloSap.getPrgEstrazioneSap());
			preparedStatement.setString(3, moduloSap.getNoteErrori());
			preparedStatement.setBigDecimal(4, prgInvioAut);
			preparedStatement.executeUpdate();
			preparedStatement.close();

		} catch (Exception e) {
			logger.error("Errore: aggiornamento data batch invio massivo sap", e);
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

	@SuppressWarnings("rawtypes")
	private static void ritornoMinistero(Connection connection) throws Exception {

		Statement sInvioSap = null;
		sInvioSap = connection.createStatement();
		ResultSet rsInvioSap = null;
		HashMap<BigDecimal, BigDecimal> cdnPrgInvioSap = new HashMap<BigDecimal, BigDecimal>();
		rsInvioSap = sInvioSap.executeQuery(QUERY_GET_PRGINVIOAUTSAP);
		if (rsInvioSap != null) {
			while (rsInvioSap.next()) {
				BigDecimal prgEstrazioneSap = rsInvioSap.getBigDecimal("PRGESTRAZIONESAP");
				BigDecimal prgInvioSap = rsInvioSap.getBigDecimal("PRGINVIOAUTSAP");
				cdnPrgInvioSap.put(prgEstrazioneSap, prgInvioSap);
			}
		}
		sInvioSap.close();
		rsInvioSap.close();

		Iterator iter = cdnPrgInvioSap.keySet().iterator();
		while (iter.hasNext()) {
			BigDecimal prgEstrazioneSap = (BigDecimal) iter.next();
			PreparedStatement preparedStatement = null;
			ResultSet rsTracciamentoSap = null;
			PreparedStatement updateStatement = null;
			try {
				preparedStatement = connection.prepareStatement(QUERY_SELECT_DATI_UPDATE);
				preparedStatement.setBigDecimal(1, prgEstrazioneSap);
				rsTracciamentoSap = preparedStatement.executeQuery();
				if (rsTracciamentoSap != null) {
					while (rsTracciamentoSap.next()) {
						try {
							String dateInvio = rsTracciamentoSap.getString("DATLASTINVIOSAP");
							String strEsitoMin = rsTracciamentoSap.getString("STRESITOMIN");
							String codMinSap = rsTracciamentoSap.getString("CODMINSAP");
							BigDecimal prgInvioSap = cdnPrgInvioSap.get(prgEstrazioneSap);
							updateStatement = connection.prepareStatement(QUERY_UPDATE_INVIO_AUT_SAP_STEP3);
							updateStatement.setString(1, dateInvio);
							updateStatement.setString(2, strEsitoMin);
							updateStatement.setString(3, codMinSap);
							updateStatement.setBigDecimal(4, prgInvioSap);
							updateStatement.setBigDecimal(5, prgEstrazioneSap);
							updateStatement.executeUpdate();
						} catch (Exception e) {
							logger.error("BatchInvioAutomatico. STEP3 - Eccezione gestita." + e.getMessage());
						} finally {
							if (updateStatement != null) {
								try {
									updateStatement.close();
								} catch (SQLException e) {
									logger.error(e);
								}
							}
						}
					}
				}
				preparedStatement.close();
				rsTracciamentoSap.close();
			} catch (Exception e) {
				logger.error("BatchInvioAutomatico. STEP3 - Eccezione gestita." + e.getMessage());
				continue;
			} finally {

				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
				if (rsTracciamentoSap != null) {
					try {
						rsTracciamentoSap.close();
					} catch (SQLException e) {
						logger.error(e);
					}
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

	public static boolean checkStopValidazioneOrario(int oraInizio, int minutiInizio, BigDecimal numMaxMinuti) {
		Date dataCurr = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataCurr);
		int oraCurr = cal.get(Calendar.HOUR_OF_DAY);
		int minCurr = cal.get(Calendar.MINUTE);
		int minutiInizialiTotali = (oraInizio * 60) + minutiInizio;
		int diffMinuti = 0;
		if ((oraCurr < oraInizio) || (oraCurr == oraInizio && minCurr < minutiInizio)) {
			diffMinuti = (((24 - oraInizio) * 60) - minutiInizio) + ((oraCurr * 60) + minCurr);
		} else {
			int minutiCurrTotali = (oraCurr * 60) + minCurr;
			diffMinuti = minutiCurrTotali - minutiInizialiTotali;
		}
		if (diffMinuti >= numMaxMinuti.intValue()) {
			return true;
		} else {
			return false;
		}
	}

}