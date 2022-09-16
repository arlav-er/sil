package it.eng.sil.batch.timer.interval.fixed.siaper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.engiweb.framework.dbaccess.DataConnectionManager;

import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.batch.timer.interval.fixed.security.CryptoBouncyCastle;
import it.eng.sil.batch.timer.interval.fixed.security.CryptoBouncyCastle.CryptoBouncyCastleException;
import it.eng.sil.util.batch.siaper.xsd.AnagraficaCompleta;
import it.eng.sil.util.batch.siaper.xsd.AnagraficaCompleta.Nascita;
import it.eng.sil.util.batch.siaper.xsd.Cessazione;
import it.eng.sil.util.batch.siaper.xsd.Comunicazione;
import it.eng.sil.util.batch.siaper.xsd.Contratto;
import it.eng.sil.util.batch.siaper.xsd.DatiLavoratore;
import it.eng.sil.util.batch.siaper.xsd.DatoreAnagraficaCompleta;
import it.eng.sil.util.batch.siaper.xsd.DatoreSedePrecedente;
import it.eng.sil.util.batch.siaper.xsd.IndirizzoConRecapitiItaliano;
import it.eng.sil.util.batch.siaper.xsd.IndirizzoItaliano;
import it.eng.sil.util.batch.siaper.xsd.Missione;
import it.eng.sil.util.batch.siaper.xsd.Proroga;
import it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.DatoreLavoro;
import it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.DittaUtilizzatrice;
import it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.DittaUtilizzatrice.InizioContratto;
import it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.InizioRapporto;
import it.eng.sil.util.batch.siaper.xsd.RapportoLavoro.Trasformazione;
import it.eng.sil.util.batch.siaper.xsd.Sesso;
import it.eng.sil.util.batch.siaper.xsd.SiNo;
import it.eng.sil.util.batch.siaper.xsd.TipoOrario;
import it.islm.siaper.wscob.WSCobSoapProxy;

@Singleton
public class BatchInvioSiaper extends FixedTimerBatch {

	private static final String QUERY_SIAPER_TS_GENERALE = "SELECT FLGINVIOSIAPER, "
			+ " NUMINVIOSIAPER, STRCHIAVEAESSIAPER " + " FROM TS_GENERALE";

	private static final String QUERY_SIAPER_DATA_ULTIMO_INVIO = "SELECT DATETL " + " FROM TS_MONITORAGGIO "
			+ " WHERE CODAMBITO='MOV_AP' ";

	private static final String QUERY_RICERCA_MOVIMENTI_NUOVI = "SELECT mov.prgmovimento " + "FROM AM_MOVIMENTO mov "
			+ "WHERE mov.codstatoatto = 'PR' " + "AND mov.codtipocontratto " + "IN ('A.03.08', 'A.03.09', 'A.03.10') "
			+ "AND mov.dtmins > ?";

	private static final String QUERY_NEXT_INVIO_SIAPER = " select S_AM_INVIO_SIAPER.nextval key from dual ";

	private static final String QUERY_GET_FILE_COMUNICAZIONE = "SELECT mov.codcomunicazione as t_cob_comunicazione_codice,    "
			+ "mov.codcomunicazioneprec as t_cob_comunicazione_codprec,    "
			+ "TO_CHAR (mov.datfinepf, 'yyyy-mm-dd') as dataFinePeriodoFormativo,   "
			+ "TO_CHAR (mov.DATCOMUNICAZ, 'yyyy-mm-dd') as d_cob_invio,   " + "CASE   "
			+ "	when mov.codtipomov = 'TRA' and mov.codtipotrasf in ('01', '02', '03', '04', '05', '06') then 'Vardatori'   "
			+ "	when mov.prgaziendautiliz is null then 'Unilav'   " + "	else 'Unisomm'   "
			+ "END as t_cob_com_tipo_modello,   " + "lav.strcognome as t_cob_app_cognome,   "
			+ "lav.strnome as t_cob_app_nome,   " + "lav.strsesso as t_cob_app_sex,   "
			+ "lav.strcodicefiscale as t_cob_app_codice_fiscale,   " + "comlav.codcom as t_cob_app_nascita_comune,   "
			+ "citt.strdescrizione as t_cob_app_cittadinanza,   "
			+ "TO_CHAR (lav.datnasc, 'yyyy-mm-dd') as d_cob_app_nascita_data,   "
			+ "comlavres.codcom as t_cob_app_comune,   " + "lav.strcapres as t_cob_app_cap,   "
			+ "lav.strindirizzores as t_cob_app_indirizzo,   " + "titolo.codice as t_cob_app_livello_istruzione,   "
			+ "azi.strragionesociale as t_cob_datore_denominazione,   "
			+ "azi.strcodicefiscale as t_cob_datore_codice_fiscale,   "
			+ "de_attivita.codatecodot as t_cob_datore_settore,   "
			+ "mov.STRCOGNOMELEGRAPP as t_cob_datore_lr_cognome,   " + "mov.STRNOMELEGRAPP as t_cob_datore_lr_nome,   "
			+ "TO_CHAR(mov.DATNASCLEGRAPP, 'yyyy-mm-dd') as d_cob_datore_lr_nascita_data,   "
			+ "mov.CODCOMNASCLEGRAPP as t_cob_datore_lr_nascita_comune,     "
			+ "mov.STRSESSOLEGRAPP as t_cob_datore_lr_sex,   " + "CASE   "
			+ "	when unilegale.codcom is not null then unilegale.codcom   " + "	else com.codcom   "
			+ "END as t_cob_datore_sl_comune,   " + "CASE   "
			+ "	when unilegale.codcom is not null then comlegale.strcap   " + "	else com.strcap   "
			+ "END as t_cob_datore_sl_cap,   " + "CASE   "
			+ "	when unilegale.codcom is not null then unilegale.strindirizzo   " + "	else uni.strindirizzo   "
			+ "END as t_cob_datore_sl_indirizzo,   " + "CASE   "
			+ "	when unilegale.codcom is not null then unilegale.strtel   " + "	else uni.strtel   "
			+ "END as t_cob_datore_sl_tel,   " + "CASE   "
			+ "	when unilegale.codcom is not null then unilegale.strfax   " + "	else uni.strfax   "
			+ "END as t_cob_datore_sl_fax,   " + "CASE   "
			+ "	when unilegale.codcom is not null then unilegale.stremail   " + "	else uni.stremail   "
			+ "END as t_cob_datore_sl_email,   " + "uni.strtel as t_cob_datore_so_tel,    "
			+ "uni.strfax as t_cob_datore_so_fax,    " + "uni.stremail as t_cob_datore_so_email,    "
			+ "com.codcom as t_cob_datore_so_comune,   " + "com.strcap as t_cob_datore_so_cap,   "
			+ "uni.strindirizzo as t_cob_datore_so_indirizzo,   "
			+ "aziut.strragionesociale as t_cob_utiliz_denominazione,   "
			+ "aziut.strcodicefiscale as t_cob_utiliz_codice_fiscale,   "
			+ "att_util.codatecodot as t_cob_utiliz_settore,   "
			+ "TO_CHAR (mov.DATINIZIORAPLAV, 'yyyy-mm-dd') as d_cob_uti_inizio,   "
			+ "TO_CHAR (mov.DATFINERAPLAV, 'yyyy-mm-dd') as d_cob_uti_fine,   "
			+ "uniut.CODCOM as t_cob_utiliz_so_comune,   " + "uniut.STRCAP as t_cob_utiliz_so_cap,   "
			+ "uniut.strindirizzo as t_cob_utiliz_so_indirizzo,   " + "CASE   "
			+ "	when uniutlegale.codcom is not null then uniutlegale.codcom   " + "	else comut.codcom   "
			+ "END as t_cob_utiliz_sl_comune,   " + "CASE   "
			+ "	when uniutlegale.codcom is not null then comutlegale.strcap   " + "	else comut.strcap   "
			+ "END as t_cob_utiliz_sl_cap,   " + "CASE   "
			+ "	when uniutlegale.codcom is not null then uniutlegale.strindirizzo   " + "	else uniut.strindirizzo   "
			+ "END as t_cob_utiliz_sl_indirizzo,   " + "CASE   "
			+ "	when uniutlegale.codcom is not null then uniutlegale.strtel   " + "	else uniut.strtel   "
			+ "END as t_cob_utiliz_sl_tel,   " + "CASE   "
			+ "	when uniutlegale.codcom is not null then uniutlegale.strfax   " + "	else uniut.strfax   "
			+ "END as t_cob_utiliz_sl_fax,   " + "CASE   "
			+ "	when uniutlegale.codcom is not null then uniutlegale.stremail   " + "	else uniut.stremail   "
			+ "END as t_cob_utiliz_sl_email,   " + "tipomov.strdescrizione as t_cob_con_tipo_comunicazione,     "
			+ "CASE   " + "	when mov.codtipomov = 'AVV' then TO_CHAR (mov.datiniziomov, 'yyyy-mm-dd')   "
			+ "	when mov.codtipomov = 'PRO' then TO_CHAR (mov.datiniziomov, 'yyyy-mm-dd')   "
			+ "   when mov.codtipomov = 'TRA' then TO_CHAR (mov.datiniziomov, 'yyyy-mm-dd')   "
			+ "	when mov.codtipomov = 'CES' then TO_CHAR (mov.datiniziomov, 'yyyy-mm-dd')   " + "	else null   "
			+ "END as d_cob_con_inizio,   " + "TO_CHAR (mov.datfinemov, 'yyyy-mm-dd') as d_cob_con_fine,    "
			+ "mov.FLGLAVOROINMOBILITA as b_cob_con_mobilita,      "
			+ "mov.FLGLAVOROSTAGIONALE as b_cob_con_stagionale,    " + "mov.CODORARIO as t_cob_con_tipo_orario,   "
			+ "mov.numoresett avv_oresett,   " + "mans.codmansionedot as t_cob_con_qualifica_prof,    "
			+ "con.codtipocontratto as   t_cob_con_tipo,    " + "motces.codmvcessazione ces_cod_motivo,   "
			+ "mottrasf.codtipotrasf tra_cod_motivo,   " + "comprec.codcom as t_cob_datore_prec_so_comune,   "
			+ "comprec.strcap as t_cob_datore_prec_so_cap,   "
			+ "uniprec.strindirizzo as t_cob_datore_prec_so_indirizzo,   "
			+ "de_att_azi_prec.codatecodot as t_cob_datore_prec_settore,   "
			+ "aziprec.strragionesociale as t_cob_datore_prec_den,   "
			+ "aziprec.strcodicefiscale as t_cob_datore_prec_cf,   "
			+ "mov.strreferente as t_cob_delegato, mov.stremailreferente as t_cob_email_delegato   "
			+ "FROM am_movimento mov   "
			+ "INNER JOIN de_mv_tipo_mov tipomov ON tipomov.codtipomov = mov.codtipomov    "
			+ "INNER JOIN an_lavoratore lav ON lav.cdnlavoratore = mov.cdnlavoratore   "
			+ "LEFT JOIN am_movimento_apprendist app ON app.prgmovimento = mov.prgmovimento     "
			+ "INNER JOIN an_azienda azi ON azi.prgazienda = mov.prgazienda   "
			+ "INNER JOIN an_unita_azienda uni ON (uni.prgazienda = mov.prgazienda AND uni.prgunita = mov.prgunita)     "
			+ "LEFT JOIN de_comune com ON com.codcom = uni.codcom   "
			+ "LEFT JOIN an_azienda aziut ON aziut.prgazienda = mov.prgaziendautiliz   "
			+ "LEFT JOIN an_unita_azienda uniut ON (uniut.prgazienda = mov.prgaziendautiliz AND uniut.prgunita = mov.prgunitautiliz)     "
			+ "LEFT JOIN de_comune comut ON comut.codcom = uniut.codcom    "
			+ "LEFT JOIN am_movimento movprec on (mov.prgmovimentoprec = movprec.prgmovimento)    "
			+ "LEFT JOIN an_azienda aziprec ON aziprec.prgazienda = movprec.prgazienda   "
			+ "LEFT JOIN an_unita_azienda uniprec ON (uniprec.prgazienda = movprec.prgazienda AND uniprec.prgunita = movprec.prgunita)     "
			+ "LEFT JOIN de_comune comprec ON comprec.codcom = uniprec.codcom   "
			+ "LEFT JOIN de_attivita de_att_azi_prec ON (de_att_azi_prec.codateco = uniprec.codateco)   "
			+ "INNER JOIN de_attivita ON de_attivita.codateco = uni.codateco   "
			+ "LEFT JOIN de_attivita att_util ON att_util.codateco = uniut.codateco   "
			+ "LEFT JOIN an_azienda azilegale on (azilegale.prgazienda = mov.prgazienda)   "
			+ "LEFT JOIN an_unita_azienda unilegale on (azilegale.prgazienda = unilegale.prgazienda and unilegale.flgSede = 'S')   "
			+ "LEFT JOIN de_comune comlegale ON (comlegale.codcom = unilegale.codcom)   "
			+ "LEFT JOIN an_azienda aziutlegale on (aziutlegale.prgazienda = mov.prgaziendautiliz)   "
			+ "LEFT JOIN an_unita_azienda uniutlegale on (aziutlegale.prgazienda = uniutlegale.prgazienda and uniutlegale.flgSede = 'S')     "
			+ "LEFT JOIN de_comune comutlegale ON (comutlegale.codcom = uniutlegale.codcom)   "
			+ "LEFT JOIN de_cittadinanza citt ON citt.codcittadinanza = lav.codcittadinanza   "
			+ "LEFT JOIN de_comune comlavres ON comlavres.codcom = lav.codcomres   "
			+ "LEFT JOIN mn_titolo_l1 titolo ON titolo.codice = mov.codtitolo   "
			+ "INNER JOIN de_tipo_contratto con ON con.codtipocontratto = mov.codtipocontratto   "
			+ "LEFT JOIN de_mansione mans ON mans.codmansione = mov.codmansione   "
			+ "LEFT JOIN de_mv_cessazione motces ON motces.codmvcessazione = mov.codmvcessazione   "
			+ "LEFT JOIN an_unita_azienda aziapp ON aziapp.prgazienda = app.prgaziendainpscompetente AND aziapp.prgunita = app.prgunitainpscompetente     "
			+ "LEFT JOIN de_tipo_trasf mottrasf ON mottrasf.codtipotrasf = mov.codtipotrasf   "
			+ "LEFT JOIN DE_COMUNE comlav ON (lav.codcomnas = comlav.codcom)   " + "WHERE mov.prgmovimento = ?  ";

	private static final String QUERY_INSERT_INVIO_SIAPER = " INSERT INTO AM_INVIO_SIAPER (" + "   PRGINVIOSIAPER, "
			+ "   PRGMOVIMENTO, " + "   DTMINS, " + "   DTMMOD, " + "   CDNUTINS, " + "   CDNUTMOD, "
			+ "   NUMKLOINVSIAPER, " + "   DATAINVIO," + "   STRESITO," + "   STR_ERRORE," + "   FILECOMUNICAZIONE, "
			+ "   NUMTENTATIVIINVIO " + " ) VALUES " + "(?,?,sysdate,sysdate, 100,100, 1, null, null, null, ?, 0 ) ";

	private static final String QUERY_SELECT_MOVIMENTI_DA_INVIARE = " SELECT PRGINVIOSIAPER, FILECOMUNICAZIONE, "
			+ " NUMKLOINVSIAPER, NUMTENTATIVIINVIO " + " FROM AM_INVIO_SIAPER "
			+ " WHERE  (STRESITO IS NULL OR STRESITO = 'KO') " + "	AND NUMTENTATIVIINVIO < ? ";

	private static final String QUERY_GET_WS_ENDPOINT = "SELECT STRURL FROM TS_ENDPOINT WHERE STRNAME ='InvioCOAPPRENDISTATO'";

	private static final String QUERY_UPDATE_DATA_BATCH_SIAPER = " UPDATE AM_INVIO_SIAPER " + "  SET DTMMOD = sysdate, "
			+ "      CDNUTMOD = 100, " + "      NUMKLOINVSIAPER = ?, " + "      DATAINVIO = sysdate, "
			+ "      STRESITO = ?, " + "      STR_ERRORE = ?, " + "      NUMTENTATIVIINVIO = ?  "
			+ " WHERE PRGINVIOSIAPER = ? ";

	private static final String QUERY_UPDATE_TS_MONITORAGGIO = " UPDATE TS_MONITORAGGIO SET DATETL = sysdate WHERE CODAMBITO='MOV_AP' ";

	private java.sql.Date ultimoInvioSiaper = null;
	private BigDecimal numMaxTentativi = null;
	private String cryptographicKey = null;

	private String codiceEsito = null;
	private String descrizioneEsito = null;

	static Logger logger = Logger.getLogger(BatchInvioSiaper.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "17", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info("Batch Notturno BatchInvioSiaper START effectiveStartDate:" + df.format(effectiveStartDate));

			Connection connection = null;
			Statement statement = null;
			boolean oldAutoCommit = false;

			try {

				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);
				if (connection != null) {
					boolean canContinue = true;
					String flagInvioSiaper = null;

					Statement statementGeneralBatch = connection.createStatement();
					ResultSet resultSetGeneral = statementGeneralBatch.executeQuery(QUERY_SIAPER_TS_GENERALE);
					if (resultSetGeneral != null) {
						while (resultSetGeneral.next()) {
							flagInvioSiaper = resultSetGeneral.getString("FLGINVIOSIAPER");
							if (flagInvioSiaper == null || !flagInvioSiaper.equalsIgnoreCase("S")) {
								canContinue = false;
								logger.info(
										"Batch Notturno BatchInvioSiaper non valido in questo ambiente, valore flag_invio_siaper='N'");
							} else {
								numMaxTentativi = resultSetGeneral.getBigDecimal("NUMINVIOSIAPER");
								cryptographicKey = resultSetGeneral.getString("STRCHIAVEAESSIAPER");
							}
						}
					} else {
						canContinue = false;
					}
					statementGeneralBatch.close();
					resultSetGeneral.close();
					if (canContinue) {
						Statement statementUltimoInvio = connection.createStatement();
						ResultSet resultsetUltimoInvio = statementUltimoInvio
								.executeQuery(QUERY_SIAPER_DATA_ULTIMO_INVIO);
						if (resultsetUltimoInvio != null) {
							while (resultsetUltimoInvio.next()) {
								ultimoInvioSiaper = resultsetUltimoInvio.getDate("DATETL");
								// se dataUltimoInvioSiaper è null la inizializzo a sysdate - 1
								if (ultimoInvioSiaper == null) {
									Calendar cal = Calendar.getInstance();
									cal.add(Calendar.DAY_OF_MONTH, -1);
									ultimoInvioSiaper = new java.sql.Date(cal.getTimeInMillis());
								}
							}
						}
						statementUltimoInvio.close();
						resultsetUltimoInvio.close();

						// prima transazione
						eseguiStepUno(connection);
						connection.commit();

						// seconda transazione
						eseguiStepDue(connection);
						// connection.commit();
					}
				}
			} catch (Exception e) {
				logger.error("Errore Batch Notturno BatchInvioSiaper: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}

			final Date stopDate = new Date();
			logger.info("Batch Notturno BatchInvioSiaper STOP at:" + df.format(stopDate));
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchInvioSiaper] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void eseguiStepUno(Connection connection) {
		logger.debug("Batch Notturno BatchInvioSiaper.INIZIO ESECUZIONE STEP 1");
		List<BigDecimal> idMovimenti = new ArrayList<BigDecimal>();
		try {
			PreparedStatement preparedStatementMovimentiBatch = null;
			preparedStatementMovimentiBatch = connection.prepareStatement(QUERY_RICERCA_MOVIMENTI_NUOVI);
			preparedStatementMovimentiBatch.setDate(1, ultimoInvioSiaper);
			ResultSet resultSetMovimenti = preparedStatementMovimentiBatch.executeQuery();
			if (resultSetMovimenti != null) {
				while (resultSetMovimenti.next()) {
					BigDecimal idMov = resultSetMovimenti.getBigDecimal("prgmovimento");
					idMovimenti.add(idMov);
				}
			}
			preparedStatementMovimentiBatch.close();
			resultSetMovimenti.close();
		} catch (Exception e) {
			logger.error("Batch Notturno BatchInvioSiaper. Errore bloccante STEP 1.", e);
			return;
		}
		if (!idMovimenti.isEmpty()) {
			logger.debug("Batch Notturno BatchInvioSiaper. Ci sono " + idMovimenti.size()
					+ " movimenti da inserire in AM_INVIO_SIAPER");
			Statement statementIS = null;
			PreparedStatement preparedXMLStatement = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			ResultSet resultSetDatiXML = null;
			for (BigDecimal prgMovimento : idMovimenti) {
				logger.debug("Batch Notturno BatchInvioSiaper.STEP 1 - MOVIMENTO: " + prgMovimento);
				BigDecimal prgInvioSiaper = null;
				try {
					statementIS = connection.createStatement();
					resultSet = statementIS.executeQuery(QUERY_NEXT_INVIO_SIAPER);
					if (resultSet != null) {
						while (resultSet.next()) {
							prgInvioSiaper = resultSet.getBigDecimal("key");
						}
					}

					// PREPARAZIONE XML
					String fileComunicazione = null;
					preparedXMLStatement = connection.prepareStatement(QUERY_GET_FILE_COMUNICAZIONE);
					preparedXMLStatement.setBigDecimal(1, prgMovimento);
					resultSetDatiXML = preparedXMLStatement.executeQuery();
					if (resultSetDatiXML != null) {
						while (resultSetDatiXML.next()) {
							fileComunicazione = creaValidaXml(resultSetDatiXML);
						}
					}

					if (fileComunicazione != null) {
						preparedStatement = connection.prepareStatement(QUERY_INSERT_INVIO_SIAPER);
						preparedStatement.setBigDecimal(1, prgInvioSiaper);
						preparedStatement.setBigDecimal(2, prgMovimento);
						preparedStatement.setString(3, fileComunicazione);
						if (preparedStatement.executeUpdate() <= 0) {
							prgInvioSiaper = null;
						}
					} else {
						prgInvioSiaper = null;
					}
				} catch (Exception e) {
					logger.error("Batch Notturno BatchInvioSiaper. STEP 1. Eccezione gestita." + e.getMessage());
					continue;
				} finally {
					releaseResources(resultSet, statementIS);
					releaseResources(resultSetDatiXML, preparedXMLStatement);
					releaseResources(null, preparedStatement);
				}
			}
		} else {
			logger.debug("Batch Notturno BatchInvioSiaper. Nessun movimento da inserire in AM_INVIO_SIAPER");
		}
		logger.debug("Batch Notturno BatchInvioSiaper.FINE ESECUZIONE STEP 1");
	}

	private void eseguiStepDue(Connection connection) {
		logger.debug("Batch Notturno BatchInvioSiaper.INIZIO ESECUZIONE STEP 2");
		PreparedStatement preparedStatementDaInviare = null;
		ResultSet resultSetDaInviare = null;
		List<BigDecimal> prgInvioSiaperLog = new ArrayList<BigDecimal>();
		try {
			preparedStatementDaInviare = connection.prepareStatement(QUERY_SELECT_MOVIMENTI_DA_INVIARE);
			preparedStatementDaInviare.setBigDecimal(1, numMaxTentativi);
			resultSetDaInviare = preparedStatementDaInviare.executeQuery();
			if (resultSetDaInviare != null) {
				// WSCobLocator wsLocator = new WSCobLocator();
				WSCobSoapProxy proxyWS = new WSCobSoapProxy();
				proxyWS.setEndpoint(getWSEndpoint(connection));
				while (resultSetDaInviare.next()) {
					BigDecimal prgInvioSiaper = resultSetDaInviare.getBigDecimal("PRGINVIOSIAPER");
					BigDecimal numkLoInvioSiaper = resultSetDaInviare.getBigDecimal("NUMKLOINVSIAPER");
					BigDecimal numeroTentativi = resultSetDaInviare.getBigDecimal("NUMTENTATIVIINVIO");
					Clob xmlDaCriptare = resultSetDaInviare.getClob("FILECOMUNICAZIONE");
					logger.debug("Batch Notturno BatchInvioSiaper.STEP 2 - PRGINVIOSIAPER: " + prgInvioSiaper);
					String comunicazioneTradotta = xmlDaCriptare.getSubString(1, (int) xmlDaCriptare.length());
					logger.debug("Batch Notturno BatchInvioSiaper.STEP 2 - COMUNICAZIONE IN CHIARO: "
							+ comunicazioneTradotta);
					String comunicazioneTradottaCriptata = null;
					CryptoBouncyCastle cryptoBouncyCastle = new CryptoBouncyCastle(cryptographicKey);
					byte[] comunicazioneTradottaCriptataBytes = null;
					boolean useCripting = true;
					try {
						comunicazioneTradottaCriptata = cryptoBouncyCastle.encrypt(comunicazioneTradotta);
						comunicazioneTradottaCriptataBytes = comunicazioneTradottaCriptata.getBytes("UTF-8");
						logger.debug("Batch Notturno BatchInvioSiaper.STEP 2 - COMUNICAZIONE CRIPTATA: "
								+ comunicazioneTradottaCriptataBytes);
					} catch (UnsupportedEncodingException e) {
						logger.error("Batch Notturno BatchInvioSiaper", e);
						comunicazioneTradottaCriptata = comunicazioneTradotta;
						useCripting = false;
					} catch (CryptoBouncyCastleException e) {
						logger.error("Batch Notturno BatchInvioSiaper", e);
						comunicazioneTradottaCriptata = comunicazioneTradotta;
						useCripting = false;
					}
					String esitoInvocazione = null;
					String erroreInvocazione = null;
					try {
						esitoInvocazione = proxyWS.save(comunicazioneTradottaCriptataBytes, useCripting);
						leggiXML(esitoInvocazione);
						if (getCodiceEsito().equalsIgnoreCase("00")) {
							esitoInvocazione = "OK";
						} else {
							esitoInvocazione = "KO";
							erroreInvocazione = getDescrizioneEsito();
						}
					} catch (RemoteException e) {
						logger.error("Batch Notturno BatchInvioSiaper", e);
						esitoInvocazione = "KO";
						int endIndex = (e.getMessage().length() > 4000) ? 4000 : e.getMessage().length();
						erroreInvocazione = e.getMessage().substring(0, endIndex);
					} catch (Exception e) {
						logger.error("Batch Notturno BatchInvioSiaper", e);
						esitoInvocazione = "KO";
						int endIndex = (e.getMessage().length() > 4000) ? 4000 : e.getMessage().length();
						erroreInvocazione = e.getMessage().substring(0, endIndex);
					}
					aggiornaDataBatch(connection, esitoInvocazione, erroreInvocazione, prgInvioSiaper,
							numkLoInvioSiaper, numeroTentativi);
					prgInvioSiaperLog.add(prgInvioSiaper);
				}
			} else {
				logger.debug(
						"Batch Notturno BatchInvioSiaper. Nessun movimento presente in AM_INVIO_SIAPER da inviare a SIAPER");
				return;
			}
			aggiornaTSMonitoraggio(connection);
			connection.commit();
			logger.debug("Batch Notturno BatchInvioSiaper. Aggiornamento tabella di log");
			aggiornaLogInvioSiaper(connection, prgInvioSiaperLog);
			connection.commit();
		} catch (SQLException e) {
			logger.error("Batch Notturno BatchInvioSiaper. STEP 2", e);
		} finally {
			releaseResources(resultSetDaInviare, preparedStatementDaInviare);
		}
		logger.debug("Batch Notturno BatchInvioSiaper.FINE ESECUZIONE STEP 2");
	}

	private String creaValidaXml(ResultSet rs) {

		Comunicazione comunicazionePat = null;
		try {
			comunicazionePat = new Comunicazione();

			// DATI GENERALI
			comunicazionePat.setCodiceComunicazione(rs.getString("T_COB_COMUNICAZIONE_CODICE"));
			comunicazionePat.setTipoTracciato(rs.getString("T_COB_COM_TIPO_MODELLO"));
			comunicazionePat.setTipoMovimento(rs.getString("T_COB_CON_TIPO_COMUNICAZIONE"));
			comunicazionePat.setCodiceComunicazionePrec(rs.getString("T_COB_COMUNICAZIONE_CODPREC"));
			comunicazionePat.setDataInvio(stringToXMLGregorianCalendar(rs.getString("D_COB_INVIO")));
			String delegato = rs.getString("T_COB_DELEGATO");
			if (delegato != null) {
				delegato = delegato.replaceAll("MITTENTESARE", "");
				delegato = delegato.trim();
			}
			comunicazionePat.setDelegato(delegato);
			comunicazionePat.setEMailDelegato(rs.getString("T_COB_EMAIL_DELEGATO"));
			// FINE DATI GENERALI

			// DATA FINE PERIODO FORMATIVO
			String dataFinePeriodoFormativo = rs.getString("dataFinePeriodoFormativo");

			// DATORE DI LAVORO
			DatoreLavoro datoreLav = new DatoreLavoro();
			datoreLav.setCodiceFiscale(rs.getString("T_COB_DATORE_CODICE_FISCALE"));
			datoreLav.setDenominazione(rs.getString("T_COB_DATORE_DENOMINAZIONE"));
			datoreLav.setSettore(rs.getString("T_COB_DATORE_SETTORE"));
			// Anagrafica Legale Rappresentante Datore Lavoro
			DatoreAnagraficaCompleta anagLRDatore = new DatoreAnagraficaCompleta();
			anagLRDatore.setCognome(rs.getString("T_COB_DATORE_LR_COGNOME"));
			anagLRDatore.setNome(rs.getString("T_COB_DATORE_LR_NOME"));
			if (rs.getString("T_COB_DATORE_LR_SEX") != null) {
				anagLRDatore.setSesso(Sesso.fromValue(rs.getString("T_COB_DATORE_LR_SEX").toUpperCase()));
			} else {
				anagLRDatore.setSesso(Sesso.F);
			}
			it.eng.sil.util.batch.siaper.xsd.DatoreAnagraficaCompleta.Nascita nascitaLRDatore = new it.eng.sil.util.batch.siaper.xsd.DatoreAnagraficaCompleta.Nascita();
			nascitaLRDatore.setComune(rs.getString("T_COB_DATORE_LR_NASCITA_COMUNE"));
			nascitaLRDatore.setData(stringToXMLGregorianCalendar(rs.getString("D_COB_DATORE_LR_NASCITA_DATA")));
			anagLRDatore.setNascita(nascitaLRDatore);
			datoreLav.setDatoreAnagraficaCompleta(anagLRDatore);
			// FINE Anagrafica Legale Rappresentante Datore Lavoro
			// UNITA AZIENDA SEDE LEGALE
			IndirizzoConRecapitiItaliano sedeLegaleAzienda = new IndirizzoConRecapitiItaliano();
			sedeLegaleAzienda.setCap(rs.getString("T_COB_DATORE_SL_CAP"));
			sedeLegaleAzienda.setComune(rs.getString("T_COB_DATORE_SL_COMUNE"));
			sedeLegaleAzienda.setIndirizzo(rs.getString("T_COB_DATORE_SL_INDIRIZZO"));
			sedeLegaleAzienda.setTelefono(rs.getString("T_COB_DATORE_SL_TEL"));
			sedeLegaleAzienda.setFax(rs.getString("T_COB_DATORE_SL_FAX"));
			sedeLegaleAzienda.setEMail(rs.getString("T_COB_DATORE_SL_EMAIL"));
			datoreLav.setSedeLegale(sedeLegaleAzienda);
			// FINE UNITA AZIENDA SEDE LEGALE
			// UNITA OPERATIVA AZIENDA
			IndirizzoConRecapitiItaliano sedeLavoroAzienda = new IndirizzoConRecapitiItaliano();
			sedeLavoroAzienda.setCap(rs.getString("T_COB_DATORE_SO_CAP"));
			sedeLavoroAzienda.setComune(rs.getString("T_COB_DATORE_SO_COMUNE"));
			sedeLavoroAzienda.setIndirizzo(rs.getString("T_COB_DATORE_SO_INDIRIZZO"));
			datoreLav.setSedeLavoro(sedeLavoroAzienda);
			// FINE UNITA OPERATIVA AZIENDA
			comunicazionePat.setDatoreLavoro(datoreLav);
			// FINE DATORE DI LAVORO

			// DATI LAVORATORE
			DatiLavoratore datiLavoratore = new DatiLavoratore();
			AnagraficaCompleta anagLav = new AnagraficaCompleta();
			anagLav.setCittadinanza(rs.getString("T_COB_APP_CITTADINANZA"));
			anagLav.setCognome(rs.getString("T_COB_APP_COGNOME"));
			anagLav.setNome(rs.getString("T_COB_APP_NOME"));
			anagLav.setCodiceFiscale(rs.getString("T_COB_APP_CODICE_FISCALE"));
			if (rs.getString("T_COB_APP_SEX") != null) {
				anagLav.setSesso(Sesso.fromValue(rs.getString("T_COB_APP_SEX").toUpperCase()));
			} else {
				anagLav.setSesso(Sesso.F);
			}
			Nascita nascitaLav = new Nascita();
			nascitaLav.setComune(rs.getString("T_COB_APP_NASCITA_COMUNE"));
			nascitaLav.setData(stringToXMLGregorianCalendar(rs.getString("D_COB_APP_NASCITA_DATA")));
			anagLav.setNascita(nascitaLav);
			datiLavoratore.setAnagraficaCompleta(anagLav);
			datiLavoratore.setLivelloIstruzione(rs.getString("T_COB_APP_LIVELLO_ISTRUZIONE"));
			IndirizzoItaliano indLav = new IndirizzoItaliano();
			indLav.setCap(rs.getString("T_COB_APP_CAP"));
			indLav.setComune(rs.getString("T_COB_APP_COMUNE"));
			indLav.setIndirizzo(rs.getString("T_COB_APP_INDIRIZZO"));
			datiLavoratore.setIndirizzoLavoratore(indLav);
			comunicazionePat.setLavoratore(datiLavoratore);
			// FINE DATI LAVORATORE

			// DITTA UTILIZZATRICE (EVENTUALE)
			if (comunicazionePat.getTipoTracciato().equalsIgnoreCase("UNISOMM")) {
				DittaUtilizzatrice dittaUt = new DittaUtilizzatrice();
				dittaUt.setDenominazione(rs.getString("T_COB_UTILIZ_DENOMINAZIONE"));
				dittaUt.setCodiceFiscale(rs.getString("T_COB_UTILIZ_CODICE_FISCALE"));
				dittaUt.setSettore(rs.getString("T_COB_UTILIZ_SETTORE"));
				InizioContratto inizioContratto = new InizioContratto();
				Missione missione = new Missione();
				missione.setDataInizioMissione(stringToXMLGregorianCalendar(rs.getString("D_COB_UTI_INIZIO")));
				missione.setDataFineMissione(stringToXMLGregorianCalendar(rs.getString("D_COB_UTI_FINE")));
				missione.setQualificaProfessionale(rs.getString("T_COB_CON_QUALIFICA_PROF"));
				TipoOrario tipoOrarioM = new TipoOrario();
				tipoOrarioM.setCodice(rs.getString("T_COB_CON_TIPO_ORARIO"));
				tipoOrarioM.setOreSettimanaliMedie(rs.getString("AVV_ORESETT"));
				missione.setTipoOrario(tipoOrarioM);
				inizioContratto.setInizio(missione);
				dittaUt.setInizioContratto(inizioContratto);
				IndirizzoConRecapitiItaliano sedeLegaleDitta = new IndirizzoConRecapitiItaliano();
				sedeLegaleDitta.setCap(rs.getString("T_COB_UTILIZ_SL_CAP"));
				sedeLegaleDitta.setComune(rs.getString("T_COB_UTILIZ_SL_COMUNE"));
				sedeLegaleDitta.setIndirizzo(rs.getString("T_COB_UTILIZ_SL_INDIRIZZO"));
				sedeLegaleDitta.setTelefono(rs.getString("T_COB_UTILIZ_SL_TEL"));
				sedeLegaleDitta.setFax(rs.getString("T_COB_UTILIZ_SL_FAX"));
				sedeLegaleDitta.setEMail(rs.getString("T_COB_UTILIZ_SL_EMAIL"));
				dittaUt.setSedeLegale(sedeLegaleDitta);
				IndirizzoConRecapitiItaliano sedeLavoroDitta = new IndirizzoConRecapitiItaliano();
				sedeLavoroDitta.setCap(rs.getString("T_COB_UTILIZ_SO_CAP"));
				sedeLavoroDitta.setComune(rs.getString("T_COB_UTILIZ_SO_COMUNE"));
				sedeLavoroDitta.setIndirizzo(rs.getString("T_COB_UTILIZ_SO_INDIRIZZO"));
				dittaUt.setSedeLavoro(sedeLavoroDitta);
				comunicazionePat.setDittaUtilizzatrice(dittaUt);
			}
			// FINE DITTA UTILIZZATRICE (EVENTUALE)

			// GESTIONE EVENTO (Assunzione, Proroga, Cesszione, Trasformazione)
			if (comunicazionePat.getTipoMovimento().equalsIgnoreCase("PROROGA")) {
				Proroga proroga = new Proroga();
				it.eng.sil.util.batch.siaper.xsd.Proroga.Contratto contrattoProroga = new it.eng.sil.util.batch.siaper.xsd.Proroga.Contratto();
				contrattoProroga.setDataInizio(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_INIZIO")));
				contrattoProroga.setDataFine(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_FINE")));
				contrattoProroga.setDataFinePF(stringToXMLGregorianCalendar(dataFinePeriodoFormativo));
				if (rs.getString("B_COB_CON_MOBILITA") != null) {
					SiNo sino = rs.getString("B_COB_CON_MOBILITA").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					contrattoProroga.setLavInMobilita(sino);
				} else {
					contrattoProroga.setLavInMobilita(null);
				}
				if (rs.getString("B_COB_CON_STAGIONALE") != null) {
					SiNo sino = rs.getString("B_COB_CON_STAGIONALE").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					contrattoProroga.setLavoroStagionale(sino);
				} else {
					contrattoProroga.setLavoroStagionale(null);
				}
				contrattoProroga.setQualificaProfessionale(rs.getString("T_COB_CON_QUALIFICA_PROF"));
				contrattoProroga.setTipologiaContrattuale(rs.getString("T_COB_CON_TIPO"));
				TipoOrario toProroga = new TipoOrario();
				toProroga.setCodice(rs.getString("T_COB_CON_TIPO_ORARIO"));
				toProroga.setOreSettimanaliMedie(rs.getString("AVV_ORESETT"));
				contrattoProroga.setTipoOrario(toProroga);
				proroga.setContratto(contrattoProroga);
				comunicazionePat.setProroga(proroga);
			} else if (comunicazionePat.getTipoMovimento().equalsIgnoreCase("CESSAZIONE")) {
				Cessazione cessazione = new Cessazione();
				cessazione.setCodiceCausa(rs.getString("CES_COD_MOTIVO"));
				Contratto contrattoC = new Contratto();
				contrattoC.setDataInizio(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_INIZIO")));
				contrattoC.setDataFine(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_FINE")));
				contrattoC.setDataFinePF(stringToXMLGregorianCalendar(dataFinePeriodoFormativo));
				if (rs.getString("B_COB_CON_MOBILITA") != null) {
					SiNo sino = rs.getString("B_COB_CON_MOBILITA").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					contrattoC.setLavInMobilita(sino);
				} else {
					contrattoC.setLavInMobilita(null);
				}
				if (rs.getString("B_COB_CON_STAGIONALE") != null) {
					SiNo sino = rs.getString("B_COB_CON_STAGIONALE").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					contrattoC.setLavoroStagionale(sino);
				} else {
					contrattoC.setLavoroStagionale(null);
				}
				contrattoC.setQualificaProfessionale(rs.getString("T_COB_CON_QUALIFICA_PROF"));
				contrattoC.setTipologiaContrattuale(rs.getString("T_COB_CON_TIPO"));
				TipoOrario tipoOrarioC = new TipoOrario();
				tipoOrarioC.setCodice(rs.getString("T_COB_CON_TIPO_ORARIO"));
				tipoOrarioC.setOreSettimanaliMedie(rs.getString("AVV_ORESETT"));
				contrattoC.setTipoOrario(tipoOrarioC);
				cessazione.setContratto(contrattoC);
				comunicazionePat.setCessazione(cessazione);
			} else if (comunicazionePat.getTipoMovimento().equalsIgnoreCase("TRASFORMAZIONE")) {
				Trasformazione trasformazione = new Trasformazione();
				trasformazione.setCodiceTrasformazione(rs.getString("TRA_COD_MOTIVO"));
				Contratto contrattoT = new Contratto();
				contrattoT.setDataInizio(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_INIZIO")));
				contrattoT.setDataFine(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_FINE")));
				contrattoT.setDataFinePF(stringToXMLGregorianCalendar(dataFinePeriodoFormativo));
				if (rs.getString("B_COB_CON_MOBILITA") != null) {
					SiNo sino = rs.getString("B_COB_CON_MOBILITA").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					contrattoT.setLavInMobilita(sino);
				} else {
					contrattoT.setLavInMobilita(null);
				}
				if (rs.getString("B_COB_CON_STAGIONALE") != null) {
					SiNo sino = rs.getString("B_COB_CON_STAGIONALE").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					contrattoT.setLavoroStagionale(sino);
				} else {
					contrattoT.setLavoroStagionale(null);
				}
				contrattoT.setQualificaProfessionale(rs.getString("T_COB_CON_QUALIFICA_PROF"));
				contrattoT.setTipologiaContrattuale(rs.getString("T_COB_CON_TIPO"));
				TipoOrario tipoOrarioT = new TipoOrario();
				tipoOrarioT.setCodice(rs.getString("T_COB_CON_TIPO_ORARIO"));
				tipoOrarioT.setOreSettimanaliMedie(rs.getString("AVV_ORESETT"));
				contrattoT.setTipoOrario(tipoOrarioT);
				trasformazione.setContratto(contrattoT);
				DatoreSedePrecedente datorePrec = new DatoreSedePrecedente();
				datorePrec.setCodiceFiscale(rs.getString("T_COB_DATORE_PREC_CF"));
				datorePrec.setDenominazione(rs.getString("T_COB_DATORE_PREC_DEN"));
				datorePrec.setSettore(rs.getString("T_COB_DATORE_PREC_SETTORE"));
				IndirizzoConRecapitiItaliano indDatorePrec = new IndirizzoConRecapitiItaliano();
				indDatorePrec.setCap(rs.getString("T_COB_DATORE_PREC_SO_CAP"));
				indDatorePrec.setComune(rs.getString("T_COB_DATORE_PREC_SO_COMUNE"));
				indDatorePrec.setIndirizzo(rs.getString("T_COB_DATORE_PREC_SO_INDIRIZZO"));
				datorePrec.setSede(indDatorePrec);
				trasformazione.setDatoreLavoroPrec(datorePrec);
				comunicazionePat.setTrasformazione(trasformazione);
			} else if (comunicazionePat.getTipoMovimento().equalsIgnoreCase("ASSUNZIONE")) {
				InizioRapporto inizioRapporto = new InizioRapporto();
				inizioRapporto.setDataInizio(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_INIZIO")));
				inizioRapporto.setDataFine(stringToXMLGregorianCalendar(rs.getString("D_COB_CON_FINE")));
				inizioRapporto.setDataFinePF(stringToXMLGregorianCalendar(dataFinePeriodoFormativo));
				if (rs.getString("B_COB_CON_MOBILITA") != null) {
					SiNo sino = rs.getString("B_COB_CON_MOBILITA").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					inizioRapporto.setLavInMobilita(sino);
				} else {
					inizioRapporto.setLavInMobilita(null);
				}
				if (rs.getString("B_COB_CON_STAGIONALE") != null) {
					SiNo sino = rs.getString("B_COB_CON_STAGIONALE").equalsIgnoreCase("S") ? SiNo.SI : SiNo.NO;
					inizioRapporto.setLavoroStagionale(sino);
				} else {
					inizioRapporto.setLavoroStagionale(null);
				}
				inizioRapporto.setQualificaProfessionale(rs.getString("T_COB_CON_QUALIFICA_PROF"));
				inizioRapporto.setTipologiaContrattuale(rs.getString("T_COB_CON_TIPO"));
				TipoOrario toInizio = new TipoOrario();
				toInizio.setCodice(rs.getString("T_COB_CON_TIPO_ORARIO"));
				toInizio.setOreSettimanaliMedie(rs.getString("AVV_ORESETT"));
				inizioRapporto.setTipoOrario(toInizio);
				comunicazionePat.setInizioRapporto(inizioRapporto);
			}
			// FINE GESTIONE EVENTO (Assunzione, Proroga, Cesszione, Trasformazione)
			return convertInputToString(comunicazionePat);

		} catch (SQLException se) {
			logger.error("Errore: ", se);
		} catch (ParseException pe) {
			logger.error("Errore: ", pe);
		} catch (DatatypeConfigurationException dce) {
			logger.error("Errore: ", dce);
		} catch (JAXBException je) {
			logger.error("Errore: ", je);
		}

		return null;

	}

	private String getWSEndpoint(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet endpointRS = statement.executeQuery(QUERY_GET_WS_ENDPOINT);
		if (endpointRS != null) {
			while (endpointRS.next()) {
				return endpointRS.getString(1);
			}
		}
		return null;
	}

	private boolean aggiornaDataBatch(Connection connection, String esito, String errore, BigDecimal prgInvioSiaper,
			BigDecimal numkLoInvioSiaper, BigDecimal numeroTentativi) {

		boolean isInvioSiaperAggiornato = false;

		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(QUERY_UPDATE_DATA_BATCH_SIAPER);
			preparedStatement.setBigDecimal(1, numkLoInvioSiaper.add(new BigDecimal(1)));
			preparedStatement.setString(2, esito);
			preparedStatement.setString(3, errore);
			preparedStatement.setBigDecimal(4, numeroTentativi.add(new BigDecimal(1)));
			preparedStatement.setBigDecimal(5, prgInvioSiaper);

			if (preparedStatement.executeUpdate() > 0) {
				isInvioSiaperAggiornato = true;
			}

		} catch (Exception e) {
			logger.error("Errore: aggiornamento AM_INVIO_SIAPER", e);
		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}

		return isInvioSiaperAggiornato;
	}

	private void aggiornaLogInvioSiaper(Connection connection, List<BigDecimal> prgInvioSiaperLog) throws SQLException {
		CallableStatement stmt = null;
		for (BigDecimal prgInvioSiaper : prgInvioSiaperLog) {
			// Tabella di log
			stmt = connection.prepareCall("{call PG_LOG.doLog(?, ?, ?, ?) }");
			String condizioneWhere = "WHERE PRGINVIOSIAPER = " + prgInvioSiaper;
			stmt.setString(1, "U");
			stmt.setString(2, "AM_INVIO_SIAPER");
			stmt.setBigDecimal(3, new BigDecimal(100));
			stmt.setString(4, condizioneWhere);
			stmt.execute();
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	private boolean aggiornaTSMonitoraggio(Connection connection) {

		boolean isTSGeneraleAggiornato = false;

		Statement statement = null;

		try {

			statement = connection.createStatement();

			if (statement.executeUpdate(QUERY_UPDATE_TS_MONITORAGGIO) > 0) {
				isTSGeneraleAggiornato = true;
			}

		} catch (Exception e) {
			logger.error("Errore: aggiornamento TS_MONITORAGGIO", e);
		} finally {

			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

		}

		return isTSGeneraleAggiornato;

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
		this.cryptographicKey = null;
		this.numMaxTentativi = null;
		this.ultimoInvioSiaper = null;
	}

	private XMLGregorianCalendar stringToXMLGregorianCalendar(String s)
			throws ParseException, DatatypeConfigurationException {
		XMLGregorianCalendar result = null;
		if (s == null) {
			return result;
		}

		Date date;
		DateFormat simpleDateFormat;
		GregorianCalendar gregorianCalendar;

		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		date = simpleDateFormat.parse(s);
		gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.setTime(date);
		result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		result.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		result.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		result.setHour(DatatypeConstants.FIELD_UNDEFINED);
		result.setMinute(DatatypeConstants.FIELD_UNDEFINED);
		result.setSecond(DatatypeConstants.FIELD_UNDEFINED);
		return result;
	}

	private static String convertInputToString(Comunicazione xml) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Comunicazione.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(xml, writer);
		String xmlStr = writer.getBuffer().toString();
		return xmlStr;
	}

	private void leggiXML(String xml) throws Exception {
		if (xml == null)
			throw new IllegalArgumentException("La stringa xml e' nulla");
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(false);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(is);
		doc.normalize();
		NodeList nList = doc.getElementsByTagName("Esito");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				setCodiceEsito(eElement.getElementsByTagName("codice").item(0).getFirstChild().getNodeValue());
				setDescrizioneEsito(
						eElement.getElementsByTagName("descrizione").item(0).getFirstChild().getNodeValue());
			}
		}
	}

	public String getCodiceEsito() {
		return codiceEsito;
	}

	public void setCodiceEsito(String codiceEsito) {
		this.codiceEsito = codiceEsito;
	}

	public String getDescrizioneEsito() {
		return descrizioneEsito;
	}

	public void setDescrizioneEsito(String descrizioneEsito) {
		this.descrizioneEsito = descrizioneEsito;
	}

	private void releaseResources(ResultSet rs, Statement statement) {

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}

		if (rs != null) {
			try {

				rs.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}

	}

}