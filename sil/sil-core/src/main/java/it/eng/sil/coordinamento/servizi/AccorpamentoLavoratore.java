package it.eng.sil.coordinamento.servizi;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.cig.bean.AccorpaLavoratoreBean;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.xml.XMLValidator;

/**
 * Classe che si occupa dell'accorpamento del lavoratore. Riceve la comunicazione XML dall'NCR e la processa.
 * 
 * @author uberti
 */
public class AccorpamentoLavoratore implements ServizioSoap {

	private static Logger log = org.apache.log4j.Logger.getLogger(AccorpamentoLavoratore.class.getName());

	private String SELECT_COD_PROVINCIA = "SELECT codprovincia FROM ts_ws WHERE STRUSERID = ? AND STRPASSWORD = ?";

	private String GET_CDNLAVORATORE = "SELECT cdnlavoratore FROM an_lavoratore WHERE strcodicefiscale = ?";

	private String SELECT_PRG_ACCORDO_BY_CODACCORDO = "select prgaccordo from ci_accordo where codaccordo = ? ";

	private String ISCR_ACCORPATO = "SELECT to_char(aai.datinizio, 'dd/mm/yyyy') datinizio, "
			+ " to_char(aai.datfine, 'dd/mm/yyyy') datfine, aai.prgazienda, aai.codtipoiscr, aai.prgaltraiscr "
			+ " FROM ci_accordo ci " + " INNER JOIN am_altra_iscr aai " + " ON ci.prgaccordo = aai.prgaccordo "
			+ " INNER JOIN an_lavoratore al " + " ON al.cdnlavoratore = aai.cdnlavoratore "
			+ " WHERE ci.codaccordo = ? " + " AND al.strcodicefiscale = ? ";

	private String UPDATE_AM_ALTRA_ISCR = "update am_altra_iscr set " + " NUMKLOALTRAISCR = NUMKLOALTRAISCR + 1, "
			+ " DTMMOD = sysdate, " + " cdnlavoratore = ? " + " where cdnlavoratore = ? " + " and prgaccordo = ? ";

	private String UPDATE_OR_COLLOQUIO = "UPDATE or_colloquio " + " SET numklocolloquio = numklocolloquio + 1, "
			+ " cdnlavoratore     = ? " + " WHERE cdnlavoratore = ? " + " AND prgaltraiscr    = ? ";

	private String COUNT_ISCR_DA_ACCORPARE = "SELECT COUNT(*) AS ISCRACCORPANTE " + " FROM an_lavoratore al "
			+ "INNER JOIN am_altra_iscr aai " + " ON al.cdnlavoratore     = aai.cdnlavoratore "
			+ " WHERE aai.prgazienda    = ? " + " AND aai.datinizio       = to_date(?,'dd/mm/yyyy') "
			+ " AND aai.datfine       = to_date(?,'dd/mm/yyyy') " + " AND al.strcodicefiscale = ? "
			+ " AND aai.codstato       IS NULL " + " AND aai.codtipoiscr = ? ";

	private String DATI_ISCRIZ_ACCORPANTE = "SELECT aai.prgaltraiscr, " + " aai.cdnlavoratore "
			+ " FROM am_altra_iscr aai " + " INNER JOIN an_lavoratore al " + " ON aai.cdnlavoratore = al.cdnlavoratore "
			+ " INNER JOIN ci_accordo ci " + " ON ci.prgaccordo = aai.prgaccordo " + " WHERE al.strcodicefiscale = ? "
			+ " AND aai.prgazienda = ? " + " AND aai.datinizio       = to_date(?,'dd/mm/yyyy') "
			+ " AND aai.datfine       = to_date(?,'dd/mm/yyyy') " + " AND aai.codstato       IS NULL "
			+ " AND aai.codtipoiscr = ? ";

	private String UPDATE_AN_LAVORATORE = "UPDATE an_lavoratore " + " SET cdnutmod           = 190, "
			+ "  dtmmod               = sysdate, " + "  numklolavoratore     = numklolavoratore + 1 "
			+ " WHERE strcodicefiscale = ? ";

	private String GET_PRG_TIPO_EVIDENZA = "select prgtipoevidenza from DE_TIPO_EVIDENZA where CODTIPOEVIDENZA = 'AV'";

	private String INSERT_EVIDENZA = "INSERT " + " INTO an_evidenza " + " ( " + " PRGEVIDENZA, " + " CDNLAVORATORE, "
			+ " DATDATASCAD, " + " STREVIDENZA, " + " CDNUTINS, " + " DTMINS, " + " CDNUTMOD, " + " DTMMOD, "
			+ " PRGTIPOEVIDENZA " + " ) " + " VALUES "
			+ " (S_AN_EVIDENZA.nextval, ?, to_date('01/01/2100', 'dd/mm/yyyy'), ?, 190, sysdate, 190, sysdate, ?)";

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coordinamento.ServizioSoap#elabora(it.eng.sil.coordinamento.wsClient.np.Execute)
	 */
	public String elabora(Execute parametri) {
		String risultato = null;
		String codiceRit = "0";

		log.info("Il servizio di accorpamento lavoratore e' stato chiamato");
		DataConnection dataConnection = null;
		StoredProcedureCommand command = null;
		DataResult dataResult = null;
		AccorpaLavoratoreBean bean = new AccorpaLavoratoreBean();
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			dataConnection.initTransaction();

			// parse dell'xml

			Document document = XMLValidator.parseXmlFile(parametri.getDati());

			// ottengo il bean con i dati dell'accorpamento
			bean = new AccorpaLavoratoreBean(document.getDocumentElement());
			bean.parseAcc();

			QueryExecutorObject qExec = new QueryExecutorObject();
			qExec.setTransactional(true);
			qExec.setDataConnection(dataConnection);
			qExec.setDontForgetException(false);

			// controllo che il SIL provinciale non sia il mittente stesso
			String codProvinciaRicevuto = getCodProvincia(bean, dataConnection, qExec);
			String codProvinciaLocale = InfoProvinciaSingleton.getInstance().getCodice();
			if (!codProvinciaLocale.equals(codProvinciaRicevuto)) {

				BigDecimal cndLavAccorpante = getCdnLavoratore(bean, dataConnection, qExec, bean.getCf_accorpante());
				BigDecimal cdnLavAccorpato = getCdnLavoratore(bean, dataConnection, qExec, bean.getCf_accorpato());

				// esiste CF accorpante e CF accorpato?
				if (cndLavAccorpante != null && cdnLavAccorpato != null) {

					List<String> accordi = bean.getListAccordi();
					List<String> iscrDaSpostare = new ArrayList<String>();
					for (String accordo : accordi) {

						// aggiorno l'anagrafica dell'accorpato per mandare l'informazione al SIFER
						aggiornaAnagraficaLavoratore(dataConnection, bean.getCf_accorpato(), qExec);

						// estraggo i dati dell'iscrizione del lavoratore accorpato
						SourceBean esitoSB = getDataLavoratore(dataConnection, qExec, accordo, bean.getCf_accorpato());
						String datInizio = (String) esitoSB.getAttribute("ROW.DATINIZIO");
						String datFine = (String) esitoSB.getAttribute("ROW.DATFINE");
						BigDecimal prgAzienda = (BigDecimal) esitoSB.getAttribute("ROW.PRGAZIENDA");
						String codTipoIscr = (String) esitoSB.getAttribute("ROW.CODTIPOISCR");
						BigDecimal prgAltraIscrAccorpato = (BigDecimal) esitoSB.getAttribute("ROW.PRGALTRAISCR");

						// controllo se esistono iscrizioni equivalenti e con codstato = null (non annullate) per
						// l'accorpato
						BigDecimal count = countIscrizioniEquivalenti(dataConnection, bean.getCf_accorpato(), qExec,
								datInizio, datFine, prgAzienda, codTipoIscr);

						if (count != null && count.intValue() == 1) {
							// controllo se esiste una e una sola iscrizione equivalente e con codstato = null (non
							// annullato) per l'accorpante
							count = countIscrizioniEquivalenti(dataConnection, bean.getCf_accorpante(), qExec,
									datInizio, datFine, prgAzienda, codTipoIscr);

							// ottengo i dati dell'iscrizione accorpante
							esitoSB = getDatiIscrizioneAccorpante(dataConnection, bean, qExec, prgAzienda, datInizio,
									datFine, codTipoIscr);
							BigDecimal prgAltraIscrAccorpante = (BigDecimal) esitoSB.getAttribute("ROW.PRGALTRAISCR");
							BigDecimal user = new BigDecimal(190);

							// sposto l'iscrizione dell'accorpato in quella dell'accorpante [DOPO il conteggio]
							spostaIscrizione(dataConnection, bean, qExec, accordo, prgAltraIscrAccorpato);
							log.info("[Codice domanda: " + bean.getCodiceDomanda()
									+ "] Sposto l'iscrizione dall'accorpato [" + bean.getCf_accorpato() + "]"
									+ " all'accorpante [" + bean.getCf_accorpante() + "]");

							if (count != null && count.intValue() == 1) {
								log.info("[Codice domanda: " + bean.getCodiceDomanda() + "] Esiste nell'accorpante ["
										+ bean.getCf_accorpante() + "] " + "un'unica iscrizione equivalente");

								// miniaccorpamento (cdnlav, accorpato, accorpante, user)
								log.info("[Codice domanda: " + bean.getCodiceDomanda()
										+ "] Accorpamento delle iscrizioni equivalenti " + "nell'accorpante ["
										+ bean.getCf_accorpante() + "]");
								command = (StoredProcedureCommand) dataConnection.createStoredProcedureCommand(
										" { call ? := PG_ACCORPA_LAVORATORE.accorpa_iscrizione(?,?,?,?) } ");
								ArrayList<DataField> inputParams = new ArrayList<DataField>();
								inputParams.add(dataConnection.createDataField("codiceRit", Types.NUMERIC, null));
								inputParams
										.add(dataConnection.createDataField("plav", Types.VARCHAR, cndLavAccorpante));
								inputParams.add(dataConnection.createDataField("inprgaltraiscr", Types.VARCHAR,
										prgAltraIscrAccorpato));
								inputParams.add(dataConnection.createDataField("prgaltraiscrto", Types.VARCHAR,
										prgAltraIscrAccorpante));
								inputParams.add(dataConnection.createDataField("putentemod", Types.VARCHAR, user));
								command.setAsOutputParameters(0);
								command.setAsInputParameters(1);
								command.setAsInputParameters(2);
								command.setAsInputParameters(3);
								command.setAsInputParameters(4);

								dataResult = command.execute(inputParams);
								PunctualDataResult pdr = (PunctualDataResult) dataResult.getDataObject();
								DataField df = pdr.getPunctualDatafield();
								codiceRit = ((BigDecimal) df.getObjectValue()).toString();
							} else {
								iscrDaSpostare.add(accordo);
							}
						}
					}

					// sposto le iscrizioni rimanenti dall'accorpato all'accorpante
					log.info("[Codice domanda: " + bean.getCodiceDomanda()
							+ "] Sposto le iscrizioni rimanenti dall'accorpato [" + bean.getCf_accorpato() + "]"
							+ " all'accorpante [" + bean.getCf_accorpante() + "]");
					for (String accordo : iscrDaSpostare) {
						// ottengo i dati dell'iscrizione accorpato
						SourceBean esitoSB = getDataLavoratore(dataConnection, qExec, accordo, bean.getCf_accorpato());
						BigDecimal prgAltraIscrAccorpato = (BigDecimal) esitoSB.getAttribute("ROW.PRGALTRAISCR");

						spostaIscrizione(dataConnection, bean, qExec, accordo, prgAltraIscrAccorpato);
					}

					// inserisco l'evidenza per il lavoratore accorpante
					log.info("[Codice domanda: " + bean.getCodiceDomanda()
							+ "] Inserisco l'evidenza per il lavoratore accorpante [CF: " + bean.getCf_accorpante()
							+ ", CDNLAV: " + cndLavAccorpante + "]");
					BigDecimal prgTipoEvidenza = getPrgTipoEvidenza(dataConnection, qExec);

					insertEvidenza(dataConnection, qExec, accordi, prgTipoEvidenza, cndLavAccorpante,
							bean.getCf_accorpato(), bean.getCf_accorpante());

					dataConnection.commitTransaction();

				} else {
					log.info("[Codice domanda: " + bean.getCodiceDomanda() + "] Almeno uno dei lavoratori ["
							+ bean.getCf_accorpato() + "," + bean.getCf_accorpante()
							+ "] non e' presente nel database");
				}
			} else {
				log.info("[Codice domanda: " + bean.getCodiceDomanda() + "] Il SIL provinciale corrente ["
						+ codProvinciaLocale + "] e' il mittente dell'XML");
			}

			// comunico l'esito in formato XML
			if ("0".equals(codiceRit) || "-6".equals(codiceRit)) {
				RispostaXML risposta = new RispostaXML("101", "Successo. Lavoratori: accorpato ["
						+ bean.getCf_accorpato() + "]" + " accorpante [" + bean.getCf_accorpante() + "]", "I");
				risultato = risposta.toXMLString();
			} else {
				RispostaXML risposta = new RispostaXML("999",
						"Errore durante l'accorpamento iscrizioni. Lavoratori: accorpato [" + bean.getCf_accorpato()
								+ "]" + " accorpante [" + bean.getCf_accorpante() + "]: codiceRit = " + codiceRit,
						"E");
				risultato = risposta.toXMLString();
				log.error(risultato);
			}

		} catch (Exception e) {
			try {
				dataConnection.rollBackTransaction();
			} catch (EMFInternalError e1) {
				log.debug("Errore Accorpamento Lavoratore - accorpato [" + bean.getCf_accorpato() + "]"
						+ " accorpante [" + bean.getCf_accorpante() + "]: ", e1);
			}

			log.debug("Errore Accorpamento Lavoratore - accorpato [" + bean.getCf_accorpato() + "]" + " accorpante ["
					+ bean.getCf_accorpante() + "]: ", e);
			log.debug("Errore Accorpamento Lavoratore - accorpato [" + bean.getCf_accorpato() + "]" + " accorpante ["
					+ bean.getCf_accorpante() + "]: " + parametri.toXMLString());
			log.debug("Errore Accorpamento Lavoratore - accorpato [" + bean.getCf_accorpato() + "]" + " accorpante ["
					+ bean.getCf_accorpante() + "]: " + parametri.getDati());

			RispostaXML risposta = new RispostaXML("999", "[Codice domanda: " + bean.getCodiceDomanda() + "] "
					+ "Errore durante l'esecuzione del servizio di accorpamento lavoratori" + " - accorpato ["
					+ bean.getCf_accorpato() + "]" + " accorpante [" + bean.getCf_accorpante() + "]: " + e.getMessage(),
					"E");
			risultato = risposta.toXMLString();
			log.error(risultato);

		} finally {
			Utils.releaseResources(dataConnection, command, dataResult);
		}

		return risultato;
	}

	/**
	 * Ottengo i dati dell'iscrizione accorpante
	 * 
	 * @param dataConnection
	 *            DataConnection
	 * @param bean
	 *            AccorpaLavoratoreBean
	 * @param qExec
	 *            QueryExecutorObject
	 * @param prgAzienda
	 *            BigDecimal
	 * @param datInizio
	 *            String
	 * @param datFine
	 *            String
	 * @param codTipoIscr
	 *            String
	 * @return SourceBean
	 */
	private SourceBean getDatiIscrizioneAccorpante(DataConnection dataConnection, AccorpaLavoratoreBean bean,
			QueryExecutorObject qExec, BigDecimal prgAzienda, String datInizio, String datFine, String codTipoIscr) {
		SourceBean esitoSB;
		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("STRCODICEFISCALE", Types.VARCHAR, bean.getCf_accorpante()));
		inputParameters.add(dataConnection.createDataField("PRGAZIENDA", Types.NUMERIC, prgAzienda));
		inputParameters.add(dataConnection.createDataField("DATINIZIO", Types.VARCHAR, datInizio));
		inputParameters.add(dataConnection.createDataField("DATFINE", Types.VARCHAR, datFine));
		inputParameters.add(dataConnection.createDataField("CODTIPOISCR", Types.VARCHAR, codTipoIscr));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(DATI_ISCRIZ_ACCORPANTE);
		esitoSB = (SourceBean) qExec.exec();
		return esitoSB;
	}

	/**
	 * Sposta l'iscrizione da l'accorpato all'accorpante.
	 * 
	 * @param dataConnection
	 *            DataConnection
	 * @param bean
	 *            AccorpaLavoratoreBean
	 * @param qExec
	 *            QueryExecutorObject
	 * @param accordo
	 *            String
	 * @param prgAltraIscrAccorpato
	 *            BigDecimal
	 */
	private void spostaIscrizione(DataConnection dataConnection, AccorpaLavoratoreBean bean, QueryExecutorObject qExec,
			String accordo, BigDecimal prgAltraIscrAccorpato) {

		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("STRCODICEFISCALE", Types.VARCHAR, bean.getCf_accorpante()));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(GET_CDNLAVORATORE);
		SourceBean esitoSB = (SourceBean) qExec.exec();
		BigDecimal cdnLavoratoreAccorpante = (BigDecimal) esitoSB.getAttribute("ROW.CDNLAVORATORE");

		inputParameters.clear();
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("STRCODICEFISCALE", Types.VARCHAR, bean.getCf_accorpato()));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(GET_CDNLAVORATORE);
		esitoSB = (SourceBean) qExec.exec();
		BigDecimal cdnLavoratoreAccorpato = (BigDecimal) esitoSB.getAttribute("ROW.CDNLAVORATORE");

		inputParameters.clear();
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("CODACCORDO", Types.VARCHAR, accordo));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(SELECT_PRG_ACCORDO_BY_CODACCORDO);
		esitoSB = (SourceBean) qExec.exec();
		@SuppressWarnings("unchecked")
		Vector<SourceBean> rows = esitoSB.getAttributeAsVector("ROW");

		// sposta am_altra_iscr
		for (SourceBean row : rows) {
			BigDecimal prgAccordo = (BigDecimal) row.getAttribute("PRGACCORDO");
			qExec.setType(QueryExecutorObject.UPDATE);
			inputParameters.clear();
			inputParameters
					.add(dataConnection.createDataField("CDNLAVORATORE1", Types.NUMERIC, cdnLavoratoreAccorpante));
			inputParameters
					.add(dataConnection.createDataField("CDNLAVORATORE2", Types.NUMERIC, cdnLavoratoreAccorpato));
			inputParameters.add(dataConnection.createDataField("PRGACCORDO", Types.NUMERIC, prgAccordo));
			qExec.setInputParameters(inputParameters);
			qExec.setStatement(UPDATE_AM_ALTRA_ISCR);
			qExec.exec();
		}

		// sposta or_colloquio
		qExec.setType(QueryExecutorObject.UPDATE);
		inputParameters.clear();
		inputParameters.add(dataConnection.createDataField("CDNLAVORATORE1", Types.NUMERIC, cdnLavoratoreAccorpante));
		inputParameters.add(dataConnection.createDataField("CDNLAVORATORE2", Types.NUMERIC, cdnLavoratoreAccorpato));
		inputParameters.add(dataConnection.createDataField("PRGALTRAISCR2", Types.NUMERIC, prgAltraIscrAccorpato));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(UPDATE_OR_COLLOQUIO);
		qExec.exec();

	}

	/**
	 * Aggiorna utente e data modifica del lavoratore per scatenare il SIFER.
	 * 
	 * @param dataConnection
	 *            DataConnection
	 * @param codiceFiscale
	 *            String
	 * @param qExec
	 *            QueryExecutorObject
	 */
	private void aggiornaAnagraficaLavoratore(DataConnection dataConnection, String codiceFiscale,
			QueryExecutorObject qExec) {
		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		qExec.setType(QueryExecutorObject.UPDATE);
		inputParameters.add(dataConnection.createDataField("STRCODICEFISCALE", Types.VARCHAR, codiceFiscale));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(UPDATE_AN_LAVORATORE);
		qExec.exec();
	}

	/**
	 * Conta le iscrizioni equivalenti
	 * 
	 * @param dataConnection
	 *            DataConnection
	 * @param codFiscale
	 *            String
	 * @param qExec
	 *            QueryExecutorObject
	 * @param datInizio
	 *            String
	 * @param datFine
	 *            String
	 * @param prgAzienda
	 *            BigDecimal
	 * @param codTipoIscr
	 *            String
	 * @return
	 */
	private BigDecimal countIscrizioniEquivalenti(DataConnection dataConnection, String codFiscale,
			QueryExecutorObject qExec, String datInizio, String datFine, BigDecimal prgAzienda, String codTipoIscr) {
		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		SourceBean esitoSB;
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("PRGAZIENDA", Types.NUMERIC, prgAzienda));
		inputParameters.add(dataConnection.createDataField("DATINIZIO", Types.VARCHAR, datInizio));
		inputParameters.add(dataConnection.createDataField("DATFINE", Types.VARCHAR, datFine));
		inputParameters.add(dataConnection.createDataField("STRCODICEFISCALE", Types.VARCHAR, codFiscale));
		inputParameters.add(dataConnection.createDataField("CODTIPOISCR", Types.VARCHAR, codTipoIscr));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(COUNT_ISCR_DA_ACCORPARE);
		esitoSB = (SourceBean) qExec.exec();
		BigDecimal count = (BigDecimal) esitoSB.getAttribute("ROW.ISCRACCORPANTE");
		return count;
	}

	/**
	 * Recupera le informazioni per verificare l'equivalenza di iscrizioni
	 * 
	 * @param dataConnection
	 *            DataConnection
	 * @param qExec
	 *            QueryExecutorObject
	 * @param accordo
	 *            accordo in esame
	 * @return SourceBean
	 */
	private SourceBean getDataLavoratore(DataConnection dataConnection, QueryExecutorObject qExec, String accordo,
			String codiceFiscale) {
		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("CODACCORDO", Types.VARCHAR, accordo));
		inputParameters.add(dataConnection.createDataField("STRCODICEFISCALE", Types.VARCHAR, codiceFiscale));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(ISCR_ACCORPATO);
		SourceBean esitoSB = (SourceBean) qExec.exec();
		return esitoSB;
	}

	/**
	 * Recupera il codprovincia dalla TS_WS
	 * 
	 * @param bean
	 *            bean contenente userid e password
	 * @param dataConnection
	 *            connessione al db
	 * @param qExec
	 *            queryexecutor per eseguire la query
	 * @return String
	 */
	private String getCodProvincia(AccorpaLavoratoreBean bean, DataConnection dataConnection,
			QueryExecutorObject qExec) {
		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("STRUSERID", Types.VARCHAR, bean.getUsername()));
		inputParameters.add(dataConnection.createDataField("STRPASSWORD", Types.VARCHAR, bean.getPassword()));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(SELECT_COD_PROVINCIA);
		SourceBean esitoSB = (SourceBean) qExec.exec();
		String codProvincia = (String) esitoSB.getAttribute("ROW.CODPROVINCIA");
		return codProvincia;
	}

	/**
	 * Ottiene il cdnlavoratore del lavoratore sul database
	 * 
	 * @param bean
	 *            bean contenente userid e password
	 * @param dataConnection
	 *            connessione al db
	 * @param qExec
	 *            queryexecutor per eseguire la query
	 * @param codiceFiscale
	 *            codice fiscale da cercare
	 * @return BigDecimal cdnlavoratore
	 */
	private BigDecimal getCdnLavoratore(AccorpaLavoratoreBean bean, DataConnection dataConnection,
			QueryExecutorObject qExec, String codiceFiscale) {
		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		qExec.setType(QueryExecutorObject.SELECT);
		inputParameters.add(dataConnection.createDataField("STRCODICEFISCALE", Types.VARCHAR, codiceFiscale));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(GET_CDNLAVORATORE);
		SourceBean esitoSB = (SourceBean) qExec.exec();
		BigDecimal cdnLav = (BigDecimal) esitoSB.getAttribute("ROW.CDNLAVORATORE");
		return cdnLav;
	}

	/**
	 * Ottiene il prgTipoEvidenza dal db
	 * 
	 * @param dataConnection
	 *            DataConnection
	 * @param qExec
	 *            QueryExecutorObject
	 * @return BigDecimal prgTipoEvidenza
	 */
	private BigDecimal getPrgTipoEvidenza(DataConnection dataConnection, QueryExecutorObject qExec) {
		BigDecimal prgTipoEvidenza = null;
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(GET_PRG_TIPO_EVIDENZA);
		qExec.setInputParameters(new ArrayList<DataField>());
		SourceBean esitoSB = (SourceBean) qExec.exec();
		prgTipoEvidenza = (BigDecimal) esitoSB.getAttribute("ROW.PRGTIPOEVIDENZA");

		return prgTipoEvidenza;
	}

	/**
	 * Inserisce l'evidenza per il lavoratore accorpante (solo se l'accorpamento va a buon fine)
	 * 
	 * @param dataConnection
	 *            DataConnection
	 * @param qExec
	 *            QueryExecutorObject
	 * @param accordi
	 *            List<String>
	 * @param prgTipoEvidenza
	 *            BigDecimal
	 * @param cdnLavoratore
	 *            BigDecimal
	 * @param cfAccorpato
	 *            String
	 * @param cfAccorpante
	 *            String
	 */
	private void insertEvidenza(DataConnection dataConnection, QueryExecutorObject qExec, List<String> accordi,
			BigDecimal prgTipoEvidenza, BigDecimal cdnLavoratore, String cfAccorpato, String cfAccorpante) {

		String strEvidenza = new StringBuffer(
				"A seguito di una procedura di modifica di codice fiscale nel SIL Regionale, ")
				+ "sono stati spostati tramite procedura automatica i seguenti accordi:\n";
		for (String codAccordo : accordi) {
			strEvidenza += codAccordo;
			strEvidenza += "\n";
		}
		strEvidenza += "dal lavoratore " + cfAccorpato + " al lavoratore " + cfAccorpante;

		ArrayList<DataField> inputParameters = new ArrayList<DataField>();
		qExec.setType(QueryExecutorObject.INSERT);
		inputParameters.add(dataConnection.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
		inputParameters.add(dataConnection.createDataField("STREVIDENZA", Types.VARCHAR, strEvidenza));
		inputParameters.add(dataConnection.createDataField("PRGTIPOEVIDENZA", Types.NUMERIC, prgTipoEvidenza));
		qExec.setInputParameters(inputParameters);
		qExec.setStatement(INSERT_EVIDENZA);
		qExec.exec();
	}

}
