package it.eng.sil.coop.webservices.mystage.patto;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.mystage.patto.output.ObjectFactory;
import it.eng.sil.coop.webservices.mystage.patto.output.PattiLavoratore;
import it.eng.sil.coop.webservices.mystage.patto.output.PattiLavoratore.ProfilingPatto;

public class GetPattoLavoratoreMyStage {

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(GetPattoLavoratoreMyStage.class.getName());
	private static ObjectFactory factory = new ObjectFactory();

	private static final String ESITO_OK = "00";
	private static final String DESC_OK = "OK";

	private static String QUERY_STRING_POLITICHE_ATTIVE = "SELECT pc.prgpercorso, pc.prgcolloquio, pc.prgazioni, az.strdescrizione AS descAzione, az.codazionesifer, az.codsottoazione, az.prgazioneragg, az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, "
			+ " to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione "
			+ " FROM or_percorso_concordato pc JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) JOIN de_azione az ON (az.prgazioni = pc.prgazioni) JOIN de_azione_ragg ON (az.prgazioneragg = de_azione_ragg.prgazioniragg) JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " WHERE nvl(de_azione_ragg.flg_misurayei, 'N') = 'S' AND nvl(az.flgadesionegg, 'N') = 'N' AND trunc(SYSDATE) <= trunc(az.datfineval) AND co.cdnlavoratore = ? and nvl(es.flgformazione, 'N') = 'S' and pc.codesito not in ('NA', 'RIF') "
			+ " UNION "
			+ "SELECT pc.prgpercorso, pc.prgcolloquio, pc.prgazioni, az.strdescrizione AS descAzione, az.codazionesifer, az.codsottoazione, az.prgazioneragg, az.flgformazione, pc.flggruppo, pc.numpartecipanti, to_char(co.datcolloquio, 'dd/mm/yyyy') AS datcolloquio, to_char(pc.dateffettiva, 'dd/mm/yyyy') AS dateffettiva, "
			+ " to_char(pc.datstimata, 'dd/mm/yyyy') AS datstimata, co.codcpi, pc.numygdurataeff, pc.numygduratamin, pc.numygduratamax, pc.codtipologiadurata, pc.codesito, pc.cdnutmod, to_char(pc.datavvioazione, 'dd/mm/yyyy') AS datavvioazione "
			+ " FROM or_percorso_concordato pc JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) JOIN de_azione az ON (az.prgazioni = pc.prgazioni) JOIN de_esito es ON (es.codesito = pc.codesito) "
			+ " WHERE az.codazionesifer = 'A2R' AND trunc(SYSDATE) <= trunc(az.datfineval) AND co.cdnlavoratore = ? and nvl(es.flgformazione, 'N') = 'S' and pc.codesito not in ('NA', 'RIF') "
			+ " order by prgazioni";

	private static String QUERY_STRING_ADESIONE_A02 = "SELECT to_char(perAdesione.datadesionegg, 'dd/mm/yyyy') datadesionegg "
			+ " FROM or_percorso_concordato pc INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_azione_ragg ragg ON (az.prgazioneragg = ragg.prgazioniragg) "
			+ " INNER JOIN de_esito ON (pc.codesito = de_esito.codesito) "
			+ " LEFT JOIN or_percorso_concordato perAdesione ON (pc.prgpercorsoadesione = perAdesione.prgpercorso and pc.prgcolloquioadesione = perAdesione.prgcolloquio) "
			+ " WHERE co.cdnlavoratore = ? and nvl(de_esito.flgformazione, 'N') = 'S' and pc.codesito not in ('NA', 'RIF') and az.codazionesifer = 'A02' and nvl(ragg.flg_misurayei, 'N') = 'S' "
			+ " order by perAdesione.datadesionegg desc";

	private static String QUERY_STRING_ADESIONE_C06 = "SELECT to_char(perAdesione.datadesionegg, 'dd/mm/yyyy') datadesionegg "
			+ " FROM or_percorso_concordato pc INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ " INNER JOIN de_azione_ragg ragg ON (az.prgazioneragg = ragg.prgazioniragg) "
			+ " INNER JOIN de_esito ON (pc.codesito = de_esito.codesito) "
			+ " INNER JOIN or_percorso_concordato perAdesione ON (pc.prgpercorsoadesione = perAdesione.prgpercorso and pc.prgcolloquioadesione = perAdesione.prgcolloquio) "
			+ " INNER JOIN am_patto_lavoratore patto on (co.cdnlavoratore = patto.cdnlavoratore) "
			+ " INNER JOIN am_lav_patto_scelta on (patto.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore and "
			+ " to_number(am_lav_patto_scelta.strchiavetabella) = pc.prgpercorso and am_lav_patto_scelta.codlsttab = 'OR_PER') "
			+ " WHERE co.cdnlavoratore = ? and to_date(?, 'dd/mm/yyyy') between trunc(patto.datstipula) and trunc(nvl(patto.datfine, sysdate)) "
			+ " and nvl(de_esito.flgformazione, 'N') = 'S' and pc.codesito not in ('NA', 'RIF') and az.codazionesifer = 'C06' and nvl(ragg.flg_misurayei, 'N') = 'S' "
			+ " order by perAdesione.datadesionegg desc";

	private static String codiceStr = null;

	public String getDati(String userName, String password, String codiceFiscale, String dataInizioTirocinio) {
		TransactionQueryExecutor tex = null;
		DataConnection conn = null;
		String descrizioneStr = null;
		DataConnection dc = null;
		try {
			// Inizializzo il TransactionQueryExecutor
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			tex = new TransactionQueryExecutor(conn, null, null);
			tex.initTransaction();

			// verifica dati accesso
			if (isAutenticazioneValida(userName, password, tex)) {
				logger.debug("Autenticato con userName:" + userName + ",  password:" + password);
			} else {
				codiceStr = "01";
				descrizioneStr = "Errore in fase di autenticazione";
				throw new MyStageException("", codiceStr, descrizioneStr);
			}

			QueryExecutorObject qExec = getQueryExecutorObject();
			dc = qExec.getDataConnection();

			BigDecimal cdnLavoratore = null;
			qExec.setStatement(SQLStatements.getStatement("WS_GET_CDN_LAVORATORE"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> params = new ArrayList<DataField>();
			params.add(dc.createDataField("STRCODICEFISCALE", Types.VARCHAR, codiceFiscale));
			qExec.setInputParameters(params);
			SourceBean anLavBeanRows = (SourceBean) qExec.exec();
			if (anLavBeanRows != null && !anLavBeanRows.getAttributeAsVector("ROW").isEmpty()) {
				cdnLavoratore = (BigDecimal) anLavBeanRows.getAttribute("ROW.CDNLAVORATORE");
			} else {
				codiceStr = "02";
				descrizioneStr = "Nessun lavoratore trovato";
				throw new MyStageException("", codiceStr, descrizioneStr);
			}

			String codProvinciaMinisteriale = null;
			String descrProvinciaMinisteriale = null;
			qExec.setStatement(SQLStatements.getStatement("GET_INFO_TS_GENERALE"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> paramsGenerale = new ArrayList<DataField>();
			qExec.setInputParameters(paramsGenerale);
			SourceBean wsGenerale = (SourceBean) qExec.exec();
			if (wsGenerale != null) {
				codProvinciaMinisteriale = (String) wsGenerale.getAttribute("ROW.CODMIN");
				descrProvinciaMinisteriale = (String) wsGenerale.getAttribute("ROW.STRDENOMINAZIONE");
			}

			qExec.setStatement(SQLStatements.getStatement("GET_Partecipante_GG_DatiProfiling_patto_WS"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> paramsPatto = new ArrayList<DataField>();
			paramsPatto.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, cdnLavoratore));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			paramsPatto.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
			qExec.setInputParameters(paramsPatto);
			SourceBean pattoBeanRows = (SourceBean) qExec.exec();
			if ((pattoBeanRows == null) || (pattoBeanRows.getAttributeAsVector("ROW").size() == 0)) {
				codiceStr = "05";
				descrizioneStr = "Nessun patto MGG protocollato";
				throw new MyStageException("", codiceStr, descrizioneStr);
			}

			PattiLavoratore patti = factory.createPattiLavoratore();
			ErroreMyStage errore = getPatti(qExec, dc, patti, pattoBeanRows, cdnLavoratore, codProvinciaMinisteriale,
					descrProvinciaMinisteriale, dataInizioTirocinio);
			if (errore.errCod != 0) {
				logger.error(
						"GetPattoLavoratoreMyStage: Invio a mystage non effettuato: cdnlavoratore = " + cdnLavoratore);
				descrizioneStr = errore.erroreEsteso;
				throw new MyStageException("", codiceStr, descrizioneStr);
			}

			// Restituisco l'xml risultato
			patti.setCodiceEsito(ESITO_OK);
			patti.setDescEsito(DESC_OK);
			String xmlPatti = convertInputToString(patti);
			logger.debug("XML Risposta " + xmlPatti);

			tex.commitTransaction();

			return xmlPatti;
		} catch (MyStageException e) {
			try {
				if (tex != null) {
					tex.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				logger.error("problema con la rollback", e1);
			}
			return ritornoErrore(e.getRespCode(), e.getRespDesc());
		} catch (Throwable e) {
			try {
				if (tex != null) {
					tex.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				logger.error("problema con la rollback", e1);
			}
			return ritornoErrore("99", "Errore generico");
		} finally {
			Utils.releaseResources(dc, null, null);
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (EMFInternalError emf) {
				logger.error(emf);
			}
		}

	}

	private static ErroreMyStage getPatti(QueryExecutorObject qExec, DataConnection dc, PattiLavoratore patti,
			SourceBean pattiBeanRows, BigDecimal cdnLavoratore, String codProvincia, String descProvincia,
			String dataInizioTirocinio) throws Exception {

		if (pattiBeanRows != null) {
			Vector pattiBeanVector = pattiBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgPattoLavoratore = null;
			BigDecimal numIndiceSvantaggio2 = null;
			String datRiferimento = null;
			XMLGregorianCalendar datRiferimentoGregorian = null;
			String datStipula = null;
			String codCpi = null;
			String dataChiusuraPatto = null;
			String motivoChiusuraPatto = null;
			String datAdesioneGg = null;
			String datAdesioneGgMax = null;
			boolean erroreNoAdesioneA02 = false;
			boolean erroreDoppiaAdesioneA02 = false;
			BigDecimal numProtocollo = null;
			XMLGregorianCalendar datStipulaGregorian = null;
			XMLGregorianCalendar dataChiusuraPattoGregorian = null;
			XMLGregorianCalendar datAdesioneGgGregorian = null;

			SourceBean pattiBeanRow = (SourceBean) pattiBeanVector.elementAt(0);

			datStipulaGregorian = null;
			dataChiusuraPattoGregorian = null;
			datAdesioneGgGregorian = null;

			prgPattoLavoratore = (BigDecimal) pattiBeanRow.getAttribute("PRGPATTOLAVORATORE");
			numIndiceSvantaggio2 = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO2");
			datRiferimento = (String) pattiBeanRow.getAttribute("DATRIFERIMENTO");
			if (datRiferimento != null) {
				datRiferimentoGregorian = toXMLGregorianCalendarDateOnly(datRiferimento);
			}
			datStipula = (String) pattiBeanRow.getAttribute("DATSTIPULA");
			if (datStipula != null) {
				datStipulaGregorian = toXMLGregorianCalendarDateOnly(datStipula);
			}
			codCpi = (String) pattiBeanRow.getAttribute("CODCPI");
			dataChiusuraPatto = (String) pattiBeanRow.getAttribute("DATFINE");
			if (dataChiusuraPatto != null) {
				dataChiusuraPattoGregorian = toXMLGregorianCalendarDateOnly(dataChiusuraPatto);
			}
			motivoChiusuraPatto = (String) pattiBeanRow.getAttribute("CODMOTIVOFINEATTO");
			numProtocollo = (BigDecimal) pattiBeanRow.getAttribute("NUMPROTOCOLLO");

			/* recupero della data adesione GG relativa al patto */
			try {
				SourceBean adesioneAttivaBeanRows = null;
				List<DataField> param = new ArrayList<DataField>();
				param = new ArrayList<DataField>();
				param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
				qExec.setInputParameters(param);
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setStatement(QUERY_STRING_ADESIONE_A02);

				adesioneAttivaBeanRows = (SourceBean) qExec.exec();
				if (adesioneAttivaBeanRows != null && !adesioneAttivaBeanRows.getAttributeAsVector("ROW").isEmpty()) {
					Vector pattiAttivazione = adesioneAttivaBeanRows.getAttributeAsVector("ROW");
					int sizePatti = pattiAttivazione.size();
					for (int k = 0; (k < sizePatti && !erroreNoAdesioneA02 && !erroreDoppiaAdesioneA02); k++) {
						SourceBean pattoAtt = (SourceBean) pattiAttivazione.get(k);
						datAdesioneGg = pattoAtt.containsAttribute("DATADESIONEGG")
								? pattoAtt.getAttribute("DATADESIONEGG").toString()
								: "";
						if (datAdesioneGg.equals("")) {
							erroreNoAdesioneA02 = true;
						} else {
							if (datAdesioneGgMax == null) {
								datAdesioneGgMax = datAdesioneGg;
							} else {
								if (DateUtils.compare(datAdesioneGg, datAdesioneGgMax) == 0) {
									erroreDoppiaAdesioneA02 = true;
								}
							}
						}
					}
					if (erroreNoAdesioneA02 || erroreDoppiaAdesioneA02) {
						codiceStr = "06";
						Vector<String> params = new Vector<String>();
						params.add("A02 - PATTO DI ATTIVAZIONE");
						if (erroreNoAdesioneA02) {
							logger.error(
									"Impossibile recuperare i dati relativi all'adesione GG, esistono azioni A02 senza la data adesione: "
											+ cdnLavoratore);
							return new ErroreMyStage(MessageCodes.YG.ERR_WS_POLITICA_ATTIVA_MYSTAGE_NO_DATADESIONE,
									params);
						} else {
							logger.error(
									"Impossibile recuperare i dati relativi all'adesione GG, esistono diverse azioni A02 con la stessa data adesione massima: "
											+ cdnLavoratore);
							return new ErroreMyStage(MessageCodes.YG.ERR_WS_POLITICA_ATTIVA_MULTIPLA_MYSTAGE, params);
						}
					}
					// Esiste una sola azione A02 - PATTO DI ATTIVAZIONE rispetto alla data adesione massima
					if (datAdesioneGgMax != null) {
						datAdesioneGgGregorian = toXMLGregorianCalendarDateOnly(datAdesioneGgMax);
					}
				} else {
					// recupero della data adesione GG relativa all'azione CO6
					SourceBean adesioneAttivaA2RRows = null;
					List<DataField> paramA2R = new ArrayList<DataField>();
					paramA2R = new ArrayList<DataField>();
					paramA2R.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
					paramA2R.add(dc.createDataField("", Types.VARCHAR, dataInizioTirocinio));
					qExec.setInputParameters(paramA2R);
					qExec.setType(QueryExecutorObject.SELECT);
					qExec.setStatement(QUERY_STRING_ADESIONE_C06);

					adesioneAttivaA2RRows = (SourceBean) qExec.exec();
					if (adesioneAttivaA2RRows != null && !adesioneAttivaA2RRows.getAttributeAsVector("ROW").isEmpty()) {
						Vector pattiC06 = adesioneAttivaA2RRows.getAttributeAsVector("ROW");
						SourceBean pattoC06 = (SourceBean) pattiC06.get(0);
						datAdesioneGgMax = pattoC06.containsAttribute("DATADESIONEGG")
								? pattoC06.getAttribute("DATADESIONEGG").toString()
								: null;
						if (datAdesioneGgMax != null) {
							datAdesioneGgGregorian = toXMLGregorianCalendarDateOnly(datAdesioneGgMax);
						}
					}
				}
			} catch (Throwable e) {
				logger.error("Impossibile recuperare i dati relativi all'adesione GG: " + cdnLavoratore);
				codiceStr = "03";
				return new ErroreMyStage(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO);
			}

			ProfilingPatto profilingPatto = factory.createPattiLavoratoreProfilingPatto();

			profilingPatto.setAdesioneGgData(datAdesioneGgGregorian);
			profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);

			if (numIndiceSvantaggio2 != null) {
				profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
			}

			profilingPatto.setPattoGgData(datStipulaGregorian);
			if (codCpi != null) {
				profilingPatto.setPattoCpi(codCpi.trim());
			}
			if (codProvincia != null) {
				profilingPatto.setProvinciaProvenienza(codProvincia);
			}
			if (descProvincia != null) {
				profilingPatto.setDescrProvinciaProvenienza(descProvincia);
			}

			profilingPatto.setDataChiusuraPatto(dataChiusuraPattoGregorian);
			if (motivoChiusuraPatto != null) {
				profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
			}
			if (numProtocollo != null) {
				profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
			}

			PattiLavoratore.ProfilingPatto.PoliticheAttive politicheAttive = factory
					.createPattiLavoratoreProfilingPattoPoliticheAttive();

			/* recupero delle azioni relative al patto */
			try {
				SourceBean politicheAttiveBeanRows = null;
				List<DataField> param = new ArrayList<DataField>();
				param = new ArrayList<DataField>();
				param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
				param.add(dc.createDataField("", Types.BIGINT, cdnLavoratore));
				qExec.setInputParameters(param);
				qExec.setType(QueryExecutorObject.SELECT);
				qExec.setStatement(QUERY_STRING_POLITICHE_ATTIVE);
				politicheAttiveBeanRows = (SourceBean) qExec.exec();
				ErroreMyStage erroreMyStage = getPoliticheAttive(qExec, dc, politicheAttive, politicheAttiveBeanRows,
						cdnLavoratore);
				if (erroreMyStage.errCod != 0) {
					return erroreMyStage;
				} else {
					if (politicheAttive.getPoliticaAttiva().size() > 0) {
						profilingPatto.setPoliticheAttive(politicheAttive);
					}
					patti.getProfilingPatto().add(profilingPatto);
				}

			} catch (Throwable e) {
				logger.error("Impossibile recuperare i dati relativi alle politiche attive: " + cdnLavoratore);
				codiceStr = "04";
				return new ErroreMyStage(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO);
			}
		}

		return new ErroreMyStage(0);
	}

	private static ErroreMyStage getPoliticheAttive(QueryExecutorObject qExec, DataConnection dc,
			PattiLavoratore.ProfilingPatto.PoliticheAttive politicheAttive, SourceBean politicheAttiveBeanRows,
			BigDecimal cdnLavoratore) throws Exception {

		if (politicheAttiveBeanRows != null) {
			Vector politicheAttiveBeanVector = politicheAttiveBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgPercorso = null;
			BigDecimal prgColloquio = null;
			String codAzioneSifer = null;
			String codSottoAzione = null;
			BigDecimal prgAzioneRagg = null;
			String flgGruppo = null;
			BigDecimal numPartecipanti = null;
			BigDecimal prgAzione = null;
			BigDecimal prgAzionePrec = null;
			String descAzione = null;
			String datColloquio = null;
			String datEffettiva = null;
			String datStimata = null;
			BigDecimal numYgDurataEff = null;
			BigDecimal numYgDurataMin = null;
			BigDecimal numYgDurataMax = null;
			String codTipologiaDurata = null;
			String codEsito = null;
			String datAvvioAzione = null;
			XMLGregorianCalendar datAvvioAzioneGregorian = null;

			for (int i = 0; i < politicheAttiveBeanVector.size(); i++) {
				XMLGregorianCalendar datColloquioGregorian = null;
				XMLGregorianCalendar datEffettivaGregorian = null;
				XMLGregorianCalendar datStimataGregorian = null;
				SourceBean politicheAttiveBeanRow = (SourceBean) politicheAttiveBeanVector.elementAt(i);

				prgPercorso = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGPERCORSO");
				prgColloquio = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGCOLLOQUIO");
				prgAzione = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGAZIONI");
				descAzione = (String) politicheAttiveBeanRow.getAttribute("DESCAZIONE");
				if (prgAzionePrec == null) {
					prgAzionePrec = prgAzione;
				} else {
					/*
					 * if (prgAzione.compareTo(prgAzionePrec) == 0) { logger.error("Politica attiva " + descAzione +
					 * " multipla:" + cdnLavoratore); codiceStr = "06"; Vector<String> params = new Vector<String>();
					 * params.add(descAzione); return new
					 * ErroreMyStage(MessageCodes.YG.ERR_WS_POLITICA_ATTIVA_MULTIPLA_MYSTAGE, params); } else {
					 */
					prgAzionePrec = prgAzione;
					// }
				}

				codAzioneSifer = (String) politicheAttiveBeanRow.getAttribute("CODAZIONESIFER");
				codSottoAzione = (String) politicheAttiveBeanRow.getAttribute("CODSOTTOAZIONE");
				prgAzioneRagg = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGAZIONERAGG");
				flgGruppo = (String) politicheAttiveBeanRow.getAttribute("FLGGRUPPO");
				numPartecipanti = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMPARTECIPANTI");
				datColloquio = (String) politicheAttiveBeanRow.getAttribute("DATCOLLOQUIO");
				if (datColloquio != null) {
					datColloquioGregorian = toXMLGregorianCalendarDateOnly(datColloquio);
				}
				datEffettiva = (String) politicheAttiveBeanRow.getAttribute("DATEFFETTIVA");
				if (datEffettiva != null) {
					datEffettivaGregorian = toXMLGregorianCalendarDateOnly(datEffettiva);
				}
				datStimata = (String) politicheAttiveBeanRow.getAttribute("DATSTIMATA");
				if (datStimata != null) {
					datStimataGregorian = toXMLGregorianCalendarDateOnly(datStimata);
				}
				numYgDurataEff = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAEFF");
				numYgDurataMin = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAMIN");
				numYgDurataMax = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMYGDURATAMAX");
				codTipologiaDurata = (String) politicheAttiveBeanRow.getAttribute("CODTIPOLOGIADURATA");
				codEsito = (String) politicheAttiveBeanRow.getAttribute("CODESITO");
				datAvvioAzione = (String) politicheAttiveBeanRow.getAttribute("DATAVVIOAZIONE");
				if (datAvvioAzione != null) {
					datAvvioAzioneGregorian = toXMLGregorianCalendarDateOnly(datAvvioAzione);
				}

				PattiLavoratore.ProfilingPatto.PoliticheAttive.PoliticaAttiva politicaAttiva = factory
						.createPattiLavoratoreProfilingPattoPoliticheAttivePoliticaAttiva();

				politicaAttiva.setDataColloquio(datColloquioGregorian);
				politicaAttiva.setDataFineAttivita(datEffettivaGregorian);
				politicaAttiva.setDataStimataFineAttivita(datStimataGregorian);
				if (numYgDurataEff != null && (numYgDurataEff.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataEffettiva(numYgDurataEff.toBigInteger());
				}
				if (numYgDurataMax != null && (numYgDurataMax.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataMassima(numYgDurataMax.toBigInteger());
				}
				if (numYgDurataMin != null && (numYgDurataMin.compareTo(new BigDecimal(0)) > 0)) {
					politicaAttiva.setDurataMinima(numYgDurataMin.toBigInteger());
				}
				if (codEsito != null) {
					politicaAttiva.setEsito(codEsito.trim());
				}
				if (flgGruppo != null) {
					politicaAttiva.setFlgGruppo(flgGruppo.trim());
				}
				if (prgAzioneRagg != null) {
					politicaAttiva.setMisura(prgAzioneRagg.toBigInteger());
				}
				if (numPartecipanti != null) {
					politicaAttiva.setNumPartecipanti(numPartecipanti.toBigInteger());
				}
				if (prgColloquio != null) {
					politicaAttiva.setPrgColloquio(prgColloquio.toBigInteger());
				}
				if (prgPercorso != null) {
					politicaAttiva.setPrgPercorso(prgPercorso.toBigInteger());
				}
				if (codAzioneSifer != null) {
					politicaAttiva.setTipoAttivita(codAzioneSifer.trim());
				}
				if (codSottoAzione != null) {
					politicaAttiva.setSottoAttivita(codSottoAzione.trim());
				}
				if (codTipologiaDurata != null) {
					politicaAttiva.setTipologiaDurata(codTipologiaDurata.trim());
				}
				politicaAttiva.setDataAvvioAttivita(datAvvioAzioneGregorian);

				politicheAttive.getPoliticaAttiva().add(politicaAttiva);
			}
		}
		return new ErroreMyStage(0);
	}

	private boolean isAutenticazioneValida(String userName, String password, TransactionQueryExecutor tex)
			throws Throwable {
		// Controllare autenticazione - by db!
		String usernameTsWs = null;
		String passwordTsWs = null;
		try {
			SourceBean logon = (SourceBean) tex.executeQuery("GET_MyStage_Mov_CredWS", new Object[] {}, "SELECT");
			usernameTsWs = (String) logon.getAttribute("ROW.struserid");
			passwordTsWs = (String) logon.getAttribute("ROW.strpassword");

			if (usernameTsWs == null || passwordTsWs == null) {
				return false;
			}

			if (usernameTsWs.equalsIgnoreCase(userName) && passwordTsWs.equalsIgnoreCase(password)) {
				logger.info("Autenticato con userName:" + userName + ",  password:" + password);
				return true;
			} else {
				logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password);
				return false;
			}
		} catch (Throwable e) {
			logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password, e);
			// return false;
			throw e;
		}
	}

	public static QueryExecutorObject getQueryExecutorObject() throws NamingException, SQLException, EMFInternalError {
		InitialContext ctx = new InitialContext();
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = new QueryExecutorObject();

			qExec.setRequestContainer(null);
			qExec.setResponseContainer(null);
			qExec.setDataConnection(dc);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setTransactional(true);
			qExec.setDontForgetException(false);
		} else {
			logger.error("Impossibile ottenere una connessione");
			return null;
		}
		return qExec;
	}

	private static XMLGregorianCalendar toXMLGregorianCalendarDateOnly(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}

	private static String convertInputToString(PattiLavoratore msg) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(PattiLavoratore.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(msg, writer);
		String xml = writer.getBuffer().toString();
		return xml;
	}

	private static String ritornoErrore(String codice, String descErrore) {
		String xmlPatti = "";
		PattiLavoratore pattiErrore = null;
		try {
			pattiErrore = factory.createPattiLavoratore();
			pattiErrore.setCodiceEsito(codice);
			pattiErrore.setDescEsito(descErrore);
			xmlPatti = convertInputToString(pattiErrore);
		} catch (Throwable e) {
			logger.error("Errore creazione input XML", e);
		}
		return xmlPatti;
	}
}
