/**
 * 
 */
package it.eng.sil.coop.webservices.corsoCIG;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.SiferMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.cigs.bean.BeanUtils;
import it.eng.sil.module.cigs.bean.InvioSiferXmlBuilder;

/**
 * @author girotti
 * 
 */
public class InvioWsSifer {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(InvioWsSifer.class);
	private String cdnGruppo;
	private String cdnProfilo;
	private String strMittente;
	private String cdnUtente;
	private String poloMittente;
	private final int maxRedeliveries = 10;
	private String wsUser;
	private String wsPassword;

	private static final String GET_CDN_LAV_MOD_BY_DATES = "SELECT aai.cdnlavoratore " + "FROM   am_altra_iscr aai "
			+ "       inner join an_lavoratore al " + "         ON al.cdnlavoratore = aai.cdnlavoratore "
			+ "       inner join or_colloquio oc " + "         ON oc.prgaltraiscr = aai.prgaltraiscr "
			+ "       inner join or_percorso_concordato opc " + "         ON oc.prgcolloquio = opc.prgcolloquio "
			+ "       inner join de_esito es " + "         ON es.codesito = opc.codesito "
			+ "WHERE  opc.codesitorendicont = 'E' " + "       AND opc.prgazioni = 151 "
			+ "       AND aai.cdnlavoratore IN (SELECT DISTINCT cdnlavoratore "
			+ "                                 FROM   (SELECT oc.cdnlavoratore, "
			+ "                                                dd.datainizio, "
			+ "                                                dd.datafine, "
			+ "                                                'PresaInCarico' AS origine "
			+ "                                         FROM   (SELECT To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datainizio, "
			+ "                                                        To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datafine "
			+ "                                                 FROM   dual) dd, "
			+ "                                                am_altra_iscr aai "
			+ "                                                inner join an_lavoratore al "
			+ "                                                  ON al.cdnlavoratore = "
			+ "                                                     aai.cdnlavoratore "
			+ "                                                inner join or_colloquio oc "
			+ "                                                  ON oc.prgaltraiscr = "
			+ "                                                     aai.prgaltraiscr "
			+ "                                inner join or_percorso_concordato opc "
			+ "                                  ON oc.prgcolloquio = opc.prgcolloquio "
			+ "                                inner join de_esito es "
			+ "                                  ON es.codesito = opc.codesito "
			+ "                                         WHERE  opc.codesitorendicont = 'E' "
			+ "                                            AND opc.prgazioni = 151 "
			+ "                                            AND Nvl(?, al.strcodicefiscale) = "
			+ "                                                al.strcodicefiscale "
			+ "                                            AND ( opc.dtmmod BETWEEN "
			+ "                                                  dd.datainizio AND dd.datafine "
			+ "                                                   OR oc.dtmmod BETWEEN "
			+ "                                                      dd.datainizio AND "
			+ "                                                      dd.datafine "
			+ "                                                   OR al.dtmmod BETWEEN "
			+ "                                                      dd.datainizio AND "
			+ "                                                      dd.datafine ) "
			+ "                                         UNION "
			+ "                                         SELECT aai.cdnlavoratore, "
			+ "                                                dd.datainizio, "
			+ "                                                dd.datafine, "
			+ "                                                'ServizioRendicontabile' AS "
			+ "                                                origine "
			+ "                                         FROM   (SELECT To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datainizio, "
			+ "                                                        To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datafine "
			+ "                                                 FROM   dual) dd, "
			+ "                                                am_altra_iscr aai "
			+ "                                                inner join an_lavoratore al "
			+ "                                                  ON al.cdnlavoratore = "
			+ "                                                     aai.cdnlavoratore "
			+ "                                                inner join or_colloquio oc "
			+ "                                                  ON oc.prgaltraiscr = "
			+ "                                                     aai.prgaltraiscr "
			+ "                                inner join or_percorso_concordato opc "
			+ "                                  ON oc.prgcolloquio = opc.prgcolloquio "
			+ "                                inner join de_esito es "
			+ "                                  ON es.codesito = opc.codesito "
			+ "                                inner join de_servizicig sc "
			+ "                                  ON sc.codservizicig = "
			+ "                                     opc.codservizicig "
			+ "                                         WHERE  opc.codesitorendicont != 'ENR' "
			+ "                                            AND Nvl(sc.codservizicig, 0) > 0 "
			+ "                                            AND Nvl(?, al.strcodicefiscale) = "
			+ "                                                al.strcodicefiscale "
			+ "                                            AND ( opc.dtmmod BETWEEN "
			+ "                                                  dd.datainizio AND dd.datafine "
			+ "                                                   OR oc.dtmmod BETWEEN "
			+ "                                                      dd.datainizio AND "
			+ "                                                      dd.datafine ) "
			+ "                                         UNION "
			+ "                                         SELECT aai.cdnlavoratore, "
			+ "                                                dd.datainizio, "
			+ "                                                dd.datafine, "
			+ "                                                'Corso' AS origine "
			+ "                                         FROM   (SELECT To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datainizio, "
			+ "                                                        To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datafine "
			+ "                                                 FROM   dual) dd, "
			+ "                                                ci_corso cc "
			+ "                                                inner join am_altra_iscr aai "
			+ "                                                  ON cc.prgaltraiscr = "
			+ "                                                     aai.prgaltraiscr "
			+ "                                                inner join an_lavoratore al "
			+ "                                                  ON al.cdnlavoratore = "
			+ "                                                     aai.cdnlavoratore "
			+ "                                         WHERE  cc.dtmmod BETWEEN "
			+ "                                                dd.datainizio AND "
			+ "                                                dd.datafine "
			+ "                                            AND Nvl(?, al.strcodicefiscale) = "
			+ "                                                al.strcodicefiscale "
			+ "                                         UNION "
			+ "                                         SELECT aai.cdnlavoratore, "
			+ "                                                dd.datainizio, "
			+ "                                                dd.datafine, "
			+ "                                                'MovimentoCes' AS origine "
			+ "                                         FROM   (SELECT To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datainizio, "
			+ "                                                        To_date(?, 'DD/MM/YYYY') "
			+ "                                                        AS "
			+ "                                                        datafine "
			+ "                                                 FROM   dual) dd, "
			+ "                                                am_movimento m "
			+ "                                                inner join am_altra_iscr aai "
			+ "                                                  ON m.cdnlavoratore = "
			+ "                                                     aai.cdnlavoratore "
			+ "                                                     AND "
			+ "                                         m.prgazienda = aai.prgazienda "
			+ "                                                     AND "
			+ "                                         m.prgunita = aai.prgunita "
			+ "                                                     AND m.datiniziomov > "
			+ "                                                         aai.datinizio "
			+ "                                                     AND m.codtipomov = 'CES' "
			+ "                                                     AND m.codmvcessazione <> "
			+ "                                                         'SC' "
			+ "                                                     AND m.codstatoatto = 'PR' "
			+ "                                                inner join an_lavoratore al "
			+ "                                                  ON al.cdnlavoratore = "
			+ "                                                     aai.cdnlavoratore "
			+ "                                         WHERE  m.dtmmod BETWEEN "
			+ "                                                dd.datainizio AND "
			+ "                                                dd.datafine "
			+ "                                            AND Nvl(?, al.strcodicefiscale) = "
			+ "                                                al.strcodicefiscale "
			+ "                                         UNION " + "                                         SELECT "
			+ "                                To_number(accorpa.cdnlavoratore), " + "dd.datainizio, " + "dd.datafine, "
			+ "'AccorpamentoLavoratore' AS origine " + "         FROM   (SELECT To_date(?, 'DD/MM/YYYY') AS "
			+ "                        datainizio, " + "                        To_date(?, 'DD/MM/YYYY') AS "
			+ "                        datafine " + "                 FROM   dual) dd, "
			+ "                an_lavoratore_accorpa accorpa " + "         WHERE " + "To_date( "
			+ "         To_char(accorpa.dtmins, 'DD/MM/YYYY'), 'DD/MM/YYYY') " + "                BETWEEN "
			+ "                       dd.datainizio AND dd.datafine " + "            AND "
			+ "Nvl(?, accorpa.strcodicefiscale) = accorpa.strcodicefiscale " + "UNION " + "SELECT iscr.cdnlavoratore, "
			+ "dd.datainizio, " + "dd.datafine, " + "'ModificaAccordo' AS origine "
			+ "FROM   (SELECT To_date(?, 'DD/MM/YYYY') AS " + "datainizio, " + "To_date(?, 'DD/MM/YYYY') AS "
			+ "datafine " + "FROM   dual) dd, " + "ci_accordo acc " + "join am_altra_iscr iscr "
			+ "ON iscr.prgaccordo = acc.prgaccordo " + "join an_lavoratore lav "
			+ "ON lav.cdnlavoratore = iscr.cdnlavoratore " + "WHERE "
			+ "Nvl(?, lav.strcodicefiscale) = lav.strcodicefiscale " + "AND acc.dtmmod BETWEEN "
			+ "dd.datainizio AND dd.datafine)) ";

	private static final String GET_DEPLOY_PARAMS = "SELECT ws.struserid AS wsuser, ws.strpassword AS wspassword, NULL AS cdngruppo, NULL AS cdnprofilo, "
			+ "       NULL         AS strmittente, NULL AS cdnutente, NULL AS polomittente " + "FROM   ts_generale gen "
			+ "       INNER JOIN ts_ws ws " + "       ON     gen.codprovinciasil = ws.codprovincia "
			+ "WHERE  ws.codservizio             = 'SIL_CIG_2_SIFER'";

	/**
	 * 
	 */
	public InvioWsSifer() {
	}

	public String inviaWsLavModificatiIeri() throws Exception {
		logger.debug("inviaWsLavModificatiIeri() - start");

		DataConnection dc = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
		QueryExecutorObject qExec = getQueryExecutorObject(dc);
		Calendar calendar = Calendar.getInstance();
		Date fine = new Date(calendar.getTimeInMillis());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date inizio = new Date(calendar.getTimeInMillis());
		List<BigDecimal> lista = getListaCdnLavByDates(inizio, fine, "", qExec);
		// List<BigDecimal> lista = getListaCdnLavByStatement(
		// GET_CDN_LAV_MOD_IERI, qExec);
		int numLavInviatiSifer = 0;
		if (lista != null) {
			numLavInviatiSifer = lista.size();
		}
		logger.info("Invio Ws Sifer i dati di " + numLavInviatiSifer + " lavoratori");
		String wsReturned = sendWsSiferByCdnLavoratore(lista, qExec);

		logger.debug("inviaWsLavModificatiIeri() - end");
		return wsReturned;
	}

	public String inviaWsLavCrash2and3Aprile() throws Exception {
		logger.info("inviaWsLavCrash2and3Aprile() - start");

		DataConnection dc = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
		QueryExecutorObject qExec = getQueryExecutorObject(dc);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Date inizio = simpleDateFormat.parse("2016-04-01");
		Date fine = simpleDateFormat.parse("2016-04-04");

		List<BigDecimal> lista = getListaCdnLavByDates(inizio, fine, "", qExec);

		logger.info("----------------------------------------------------");

		int numLavInviatiSifer = 0;
		if (lista != null) {
			numLavInviatiSifer = lista.size();

			logger.info("Lavoratori da inviare: " + lista.size());

			logger.info("----------------------------------------------------");
			for (BigDecimal cdnLavoratore : lista) {
				logger.info("cdnLavoratore: " + cdnLavoratore.longValue());
			}

		}
		logger.info("Invio Ws Sifer i dati di " + numLavInviatiSifer + " lavoratori");
		String wsReturned = sendWsSiferByCdnLavoratore(lista, qExec);

		logger.info("inviaWsLavCrash2and3Aprile() - end");
		return wsReturned;
	}

	/*
	 * private List<BigDecimal> getListaCdnLavByStatement(String statement, QueryExecutorObject qExec) { if
	 * (logger.isDebugEnabled()) { logger .debug("getListaCdnLavByStatement(String, QueryExecutorObject) - start"); }
	 * 
	 * List<DataField> inputParameters = new ArrayList<DataField>(); // ------------------- CdnLav
	 * qExec.setInputParameters(inputParameters); qExec.setType(QueryExecutorObject.SELECT);
	 * qExec.setStatement(statement); Object returned = qExec.exec(); BigDecimal cndLavavoratore = null;
	 * List<BigDecimal> lista = new ArrayList<BigDecimal>(); if (returned instanceof SourceBean) { SourceBean listCdnLav
	 * = (SourceBean) returned; Vector<SourceBean> vCdnlav = listCdnLav .getAttributeAsVector(InvioSiferXmlBuilder.ROW);
	 * for (SourceBean sourceBean : vCdnlav) { cndLavavoratore = BeanUtils.getBigDecimal(sourceBean,
	 * InvioSiferXmlBuilder.CDNLAVORATORE, BigDecimal.ZERO); lista.add(cndLavavoratore); } }
	 * 
	 * if (logger.isDebugEnabled()) { logger .debug("getListaCdnLavByStatement(String, QueryExecutorObject) - end"); }
	 * return lista; }
	 */

	public String inviaWsLavAll(Date inizio, Date fine, String codiceFiscale) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("inviaWsLavAll(Date, Date, String) - start");
		}

		DataConnection dc = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
		QueryExecutorObject qExec = getQueryExecutorObject(dc);
		qExec.setDontForgetException(true);
		List<BigDecimal> listaByDates = getListaCdnLavByDates(inizio, fine, codiceFiscale, qExec);
		String wsReturned = sendWsSiferByCdnLavoratore(listaByDates, qExec);

		if (logger.isDebugEnabled()) {
			logger.debug("inviaWsLavAll(Date, Date, String) - end");
		}
		return wsReturned;
	}

	public List<BigDecimal> getListaCdnLavByDates(Date inizio, Date fine, String codiceFiscale,
			QueryExecutorObject qExec) {
		if (logger.isDebugEnabled()) {
			logger.debug("getListaCdnLavByDates(Date, Date, String, QueryExecutorObject) - start");
		}

		List<DataField> inputParameters = new ArrayList<DataField>();
		DataConnection dc = qExec.getDataConnection();
		// ------------------- CdnLav
		String SQL_DATE_FORMAT = "dd/MM/yyyy";
		SimpleDateFormat sql2date = new SimpleDateFormat(SQL_DATE_FORMAT);
		// Sono 4 query in join, ogniuna vuole datainizio, datafine e codice
		// fiscale
		String dataInzio = sql2date.format(inizio);
		String dataFine = sql2date.format(fine);
		for (int i = 0; i <= 5; i++) {
			inputParameters.add(dc.createDataField("inizio", Types.VARCHAR, dataInzio));
			inputParameters.add(dc.createDataField("fine", Types.VARCHAR, dataFine));
			if (StringUtils.isFilledNoBlank(codiceFiscale)) {
				inputParameters.add(dc.createDataField("codiceFiscale", Types.VARCHAR, codiceFiscale));
			} else {
				inputParameters.add(dc.createDataField("codiceFiscale", Types.NULL, null));
			}
		}
		qExec.setInputParameters(inputParameters);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(GET_CDN_LAV_MOD_BY_DATES);
		Object returned = qExec.exec();
		BigDecimal cndLavavoratore = null;
		List<BigDecimal> lista = new ArrayList<BigDecimal>();
		if (returned instanceof SourceBean) {
			SourceBean listCdnLav = (SourceBean) returned;
			Vector<SourceBean> vCdnlav = listCdnLav.getAttributeAsVector(InvioSiferXmlBuilder.ROW);
			for (SourceBean sourceBean : vCdnlav) {
				cndLavavoratore = BeanUtils.getBigDecimal(sourceBean, InvioSiferXmlBuilder.CDNLAVORATORE,
						BigDecimal.ZERO);
				lista.add(cndLavavoratore);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getListaCdnLavByDates(Date, Date, String, QueryExecutorObject) - end");
		}
		return lista;
	}

	public String sendWsSiferByCdnLavoratore(Collection<BigDecimal> lista, QueryExecutorObject qExec) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("sendWsSiferByCdnLavoratore(Collection<BigDecimal>, QueryExecutorObject) - start");
		}

		List<DataField> inputParameters = new ArrayList<DataField>();
		// ------------------- CdnLav
		qExec.setInputParameters(inputParameters);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(GET_DEPLOY_PARAMS);
		Object returned = qExec.exec();
		if (returned instanceof SourceBean) {
			SourceBean deployParamsFull = (SourceBean) returned;
			SourceBean deployParams = (SourceBean) deployParamsFull.getAttribute(InvioSiferXmlBuilder.ROW);
			if (deployParams == null) {
				throw new Exception(
						"Impossibile ottenere i parametri di invio by deploy; deployParamsFull:" + deployParamsFull);
			}
			wsUser = BeanUtils.getObjectToString(deployParams, "wsUser", "");
			wsPassword = BeanUtils.getObjectToString(deployParams, "wsPassword", "");
			cdnGruppo = BeanUtils.getObjectToString(deployParams, "cdnGruppo", "");
			cdnProfilo = BeanUtils.getObjectToString(deployParams, "cdnProfilo", "");
			strMittente = BeanUtils.getObjectToString(deployParams, "strMittente", "");
			cdnUtente = BeanUtils.getObjectToString(deployParams, "cdnUtente", "");
			poloMittente = BeanUtils.getObjectToString(deployParams, "poloMittente", "");
		}

		StringBuilder sb = new StringBuilder();
		for (BigDecimal cndLavavoratore : lista) {
			String xmlAccordi = null;
			String validityErrors;
			try {
				xmlAccordi = getXml4WsSifer(cndLavavoratore, qExec);
			} catch (Exception e) {
				final String errorString = "Errore sulla generazione di xmlAccordi generato con cndLavavoratore:"
						+ cndLavavoratore;
				logger.error(errorString, e);
				sb.append(errorString);
				sb.append("\r\n Exception:");
				sb.append(e.getLocalizedMessage());
				sb.append("\r\n");
				continue;
			}

			validityErrors = getValidityErrors(xmlAccordi);
			if (!StringUtils.isEmptyNoBlank(validityErrors)) {
				final String errorString = "xmlAccordi generato con cndLavavoratore:" + cndLavavoratore
						+ ", ha generato il seguente errore di validazione:" + validityErrors + ", xmlAccordi:"
						+ xmlAccordi;
				logger.error(errorString);
				sb.append(errorString);
				sb.append("\r\n");
			} else {
				sb.append("------Inviato a Sifer per cndLavavoratore:");
				sb.append(cndLavavoratore);
				sb.append("\r\n");
				sb.append(xmlAccordi);
				sb.append("\r\n");
				sb.append("\r\n");
				sb.append("\r\n");
				if (logger.isDebugEnabled()) {
					logger.debug("xmlAccordi inviato per cndLavavoratore:" + cndLavavoratore);
					logger.debug(xmlAccordi);
				}
				inviaXml2Sifer(xmlAccordi, cndLavavoratore.toString());
			}
		}

		try {
			qExec.getDataConnection().close();
		} catch (Exception e) {
			logger.error("sendWsSiferByCdnLavoratore(Collection<BigDecimal>, QueryExecutorObject)", e);
		}

		String returnString = sb.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("sendWsSiferByCdnLavoratore(Collection<BigDecimal>, QueryExecutorObject) - end");
		}
		return returnString;
	}

	private void inviaXml2Sifer(String xmlAccordi, String cdnLavoratore) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("inviaXml2Sifer(String) - start");
		}

		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setPoloMittente(poloMittente);
		testataMessaggio.setCdnUtente(cdnUtente);
		testataMessaggio.setCdnGruppo(cdnGruppo);
		testataMessaggio.setCdnProfilo(cdnProfilo);
		testataMessaggio.setStrMittente(strMittente);
		testataMessaggio.setMaxRedeliveries(maxRedeliveries);
		SiferMessage sendSiferMessage = new SiferMessage();
		sendSiferMessage.setTestata(testataMessaggio);

		sendSiferMessage.userName = wsUser;
		sendSiferMessage.password = wsPassword;
		sendSiferMessage.datiXml = xmlAccordi;
		sendSiferMessage.cdnLavoratore = cdnLavoratore;

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();

		OutQ outQ = new OutQ();

		// mando il messaggio
		sendSiferMessage.setDataSourceJndi(dataSourceJndiName);
		sendSiferMessage.send(outQ);

		if (logger.isDebugEnabled()) {
			logger.debug("inviaXml2Sifer(String) - end");
		}
	}

	public String getXml4WsSifer(BigDecimal cndLavavoratore, QueryExecutorObject qExec) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("getXml4WsSifer(BigDecimal, QueryExecutorObject) - start");
		}

		InvioSiferXmlBuilder siferXmlB = InvioSiferXmlBuilder.getInstance(cndLavavoratore, qExec);
		String siferXml = siferXmlB.buildXml();

		if (logger.isDebugEnabled()) {
			logger.debug("getXml4WsSifer(BigDecimal, QueryExecutorObject) - end");
		}
		return siferXml;
	}

	public static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {
		if (logger.isDebugEnabled()) {
			logger.debug("getQueryExecutorObject(DataConnection) - start");
		}

		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setRequestContainer(null);
		qExec.setResponseContainer(null);
		qExec.setDataConnection(dc);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setTransactional(true);
		qExec.setDontForgetException(true);

		if (logger.isDebugEnabled()) {
			logger.debug("getQueryExecutorObject(DataConnection) - end");
		}
		return qExec;
	}

	public String getValidityErrors(String datiRichiestaXml) {
		if (logger.isDebugEnabled()) {
			logger.debug("getValidityErrors(String) - start");
		}

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			// create schema by reading it from an XSD file:
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "PartecipanteCrisi.xsd");
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			// at last perform validation:
			StringReader datiXmlReader = new StringReader(datiRichiestaXml);
			StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
			validator.validate(datiXmlStreamSource);
		} catch (Exception e) {
			logger.error("getValidityErrors(String)", e);
			String returnString = e.getLocalizedMessage();
			if (logger.isDebugEnabled()) {
				logger.debug("getValidityErrors(String) - end");
			}
			return returnString;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getValidityErrors(String) - end");
		}
		return null;
	}

}
