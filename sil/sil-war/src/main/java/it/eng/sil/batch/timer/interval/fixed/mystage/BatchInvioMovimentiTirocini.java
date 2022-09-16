package it.eng.sil.batch.timer.interval.fixed.mystage;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Schedules;
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

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.coop.webservices.apapi.XmlUtils;
import it.eng.sil.coop.webservices.mystage.GetMovimentoTirocinio;
import it.eng.sil.coop.webservices.mystage.movimento.ImportMovimentoSilProxy;
import it.eng.sil.coop.webservices.mystage.movimento.output.MovimentoTirocinioElement;
import it.eng.sil.coop.webservices.mystage.movimento.output.ObjectFactory;
import it.eng.sil.module.sifer.PartecipanteTirociniUtils;

@Singleton
public class BatchInvioMovimentiTirocini extends FixedTimerBatch {
	private int contaMovimentiCorretti;
	private int contaMovimentiErrati;
	private static final String QUERY_RICERCA_LAVORATORI = " SELECT mov.prgmovimento, mov.prgmovimentorett "
			+ " FROM AM_MOVIMENTO MOV, " + " DE_MV_TIPO_MOV TMOV, " + " AN_UNITA_AZIENDA UAZ, " + " AN_AZIENDA AZ, "
			+ " AN_LAVORATORE LAV, " + " DE_COMUNE COM, " + " DE_PROVINCIA PROV, " + " DE_TIPO_CONTRATTO "
			+ " WHERE MOV.codTipoMov = TMOV.codTipoMov "
			+ " AND MOV.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+) "
			+ " AND MOV.cdnLavoratore = LAV.cdnLavoratore " + " AND MOV.prgAzienda = AZ.prgAzienda "
			+ " AND MOV.prgAzienda = UAZ.prgAzienda " + " AND MOV.prgUnita = UAZ.prgUnita "
			+ " AND UAZ.codCom = COM.codCom " + " AND COM.codProvincia = PROV.codProvincia(+) "
			+ " AND MOV.codTipoContratto = 'C.01.00' " + " AND MOV.codStatoAtto in ('PR', 'AN', 'AU') "
			+ " AND MOV.datcomunicaz >= to_date('01/01/2015', 'dd/mm/yyyy') "
			+ " AND TRUNC(MOV.Dtmmod) >= TO_DATE((select to_char(DATETL, 'dd/mm/yyyy') as DATETL from ts_monitoraggio "
			+ " where CODAMBITO = 'MYSTAGE'),'dd/mm/yyyy') ";

	private static final String QUERY_DATA_BATCH = "select to_char(DATETL, 'dd/mm/yyyy') as DATETL "
			+ " from ts_monitoraggio " + " where CODAMBITO = 'MYSTAGE' ";
	
	private static final String QUERY_CONFIG_POLO_REGIONALE = "select strvalore from ts_config_loc where codtipoconfig = 'PROVPREG' "
			+ " and num = 1 and strcodrif = (select codregionesil from ts_generale where prggenerale = 1) ";

	private static final String QUERY_UPDATE_DATA_BATCH = "update ts_monitoraggio set dateTL = sysdate where codambito = 'MYSTAGE' ";
	private static ObjectFactory factory = new ObjectFactory();
	private String codProvinciaMinisteriale = null;
	private String descProvinciaMinisteriale = null;
	private String codProvinciaPoloRegionale = null;
	private String descProvinciaPoloRegionale = "UNIFICATO";

	static Logger logger = Logger.getLogger(BatchInvioMovimentiTirocini.class.getName());

	@Schedules({ @Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "13", minute = "0", persistent = false),
			@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "20", minute = "0", persistent = false) })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info("Batch Notturno BatchInvioMovimentiTirocini START effectiveStartDate:"
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

				String dataEsecuzioneBatch = null;
				if (connection != null) {
					PreparedStatement preparedStatementPolo = null;
					preparedStatementPolo = connection.prepareStatement(QUERY_CONFIG_POLO_REGIONALE);
					
					ResultSet resultSetPolo = preparedStatementPolo.executeQuery();
					
					if (resultSetPolo != null) {
						while (resultSetPolo.next()) {
							codProvinciaPoloRegionale = resultSetPolo.getString("strvalore");
						}
						resultSetPolo.close();
					}
					
					PreparedStatement preparedStatementDataBatch = null;
					preparedStatementDataBatch = connection.prepareStatement(QUERY_DATA_BATCH);

					ResultSet resultSetDataBatch = preparedStatementDataBatch.executeQuery();

					if (resultSetDataBatch != null) {
						while (resultSetDataBatch.next()) {

							dataEsecuzioneBatch = resultSetDataBatch.getString("DATETL");
						}
					}

					logger.debug("DataEsecuzioneBatch= " + dataEsecuzioneBatch);

					resultSetDataBatch.close();

					BigDecimal prgmovimento = null;
					BigDecimal prgmovimentorett = null;
					// QUERY RICERCA LAVORATORI DA SPEDIRE A MYSTAGE
					PreparedStatement preparedStatement = null;
					preparedStatement = connection.prepareStatement(QUERY_RICERCA_LAVORATORI);
					resultSet = preparedStatement.executeQuery();

					contaMovimentiCorretti = 0;
					contaMovimentiErrati = 0;

					if (resultSet != null && dataEsecuzioneBatch != null) {
						while (resultSet.next()) {
							try {
								ArrayList<BigDecimal> movTirocini = new ArrayList<BigDecimal>();
								prgmovimento = resultSet.getBigDecimal("PRGMOVIMENTO");
								prgmovimentorett = resultSet.getBigDecimal("PRGMOVIMENTORETT");
								// movTirocini contiene l'intera sequenza, a partire dai movimenti rettificati fino a
								// quello
								// protocollato
								movTirocini.add(0, prgmovimento);
								while (prgmovimentorett != null) {
									movTirocini.add(0, prgmovimentorett);
									String queryRett = "select prgmovimentorett from am_movimento where prgmovimento = "
											+ prgmovimentorett.toString();
									ResultSet resultSetRett = null;
									PreparedStatement preparedStatementRett = null;
									preparedStatementRett = connection.prepareStatement(queryRett);
									resultSetRett = preparedStatementRett.executeQuery();
									if (resultSetRett != null && resultSetRett.next()) {
										prgmovimentorett = resultSetRett.getBigDecimal("prgmovimentorett");
										if (prgmovimentorett != null && movTirocini.contains(prgmovimentorett)) {
											// per evitare loop dovuti a dati sporchi nel db
											prgmovimentorett = null;
										}
									} else {
										prgmovimentorett = null;
									}
									if (preparedStatementRett != null) {
										preparedStatementRett.close();
									}
									if (resultSetRett != null) {
										resultSetRett.close();
									}
								}

								for (BigDecimal prgMov : movTirocini) {
									gestisciInvioTirocinioLavoratore(dataEsecuzioneBatch, prgMov);
								}

							} catch (Exception e) {
								logger.error("Errore Batch Notturno BatchPartecipanteGGSifer: ", e);
							}
						}
					} else {

						logger.warn("Lo statement SQL 'QUERY_RICERCA_LAVORATORI' ha dato resultset VUOTO");
					}

					// aggiorna la data di esecuzione del batch
					aggiornaDataBatch(connection);

					preparedStatement.close();
					resultSet.close();
					logger.info("movimenti tirocini corretti inviati a MyStage:" + contaMovimentiCorretti);
					logger.info("movimenti tirocini errati inviati a MyStage:" + contaMovimentiErrati);
					logger.info("movimenti tirocini TOTALI inviati a MyStage:"
							+ (contaMovimentiCorretti + contaMovimentiErrati));
				}

			} catch (Exception e) {
				logger.error("Errore Batch Notturno BatchInvioMovimentiTirocini: ", e);
			} finally {
				releaseResources(connection, statement, oldAutoCommit);
			}

			final Date stopDate = new Date();
			logger.info("Batch Notturno BatchInvioMovimentiTirocini STOP at:" + df.format(stopDate));

		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchConferimaPeriodica] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void gestisciInvioTirocinioLavoratore(String dataEsecuzioneBatch, BigDecimal prgmovimento)
			throws Exception {
		logger.debug("BatchInvioMovimentiTirocini inizio gestisciInvioTirocinioLavoratore prgmovimento:"
				+ prgmovimento.toString());
		// istanzio una nuova connessione per ogni lavoratore e la chiudo in fondo all'esecuzione
		QueryExecutorObject qExec = null;
		DataConnection dc = null;

		try {

			qExec = PartecipanteTirociniUtils.getQueryExecutorObject();
			dc = qExec.getDataConnection();

			qExec.setStatement(SQLStatements.getStatement("GET_MyStage_Mov_CredWS"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> paramsWS = new ArrayList<DataField>();
			qExec.setInputParameters(paramsWS);
			SourceBean wsRows = (SourceBean) qExec.exec();

			if (getCodProvinciaMinisteriale() == null) {
				qExec.setStatement(SQLStatements.getStatement("GET_INFO_TS_GENERALE"));
				qExec.setType(QueryExecutorObject.SELECT);
				List<DataField> paramsGenerale = new ArrayList<DataField>();
				qExec.setInputParameters(paramsGenerale);
				SourceBean wsGenerale = (SourceBean) qExec.exec();
				if (wsGenerale != null) {
					setCodProvinciaMinisteriale((String) wsGenerale.getAttribute("ROW.CODMIN"));
					setDescProvinciaMinisteriale((String) wsGenerale.getAttribute("ROW.STRDENOMINAZIONE"));
				}
			}

			qExec.setStatement(SQLStatements.getStatement("GET_MOVIMENTO_Tirocini_MYSTAGE"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> paramsMov = new ArrayList<DataField>();
			paramsMov.add(dc.createDataField("PRGMOVIMENTO", Types.NUMERIC, prgmovimento));
			qExec.setInputParameters(paramsMov);
			SourceBean movimentazioneBeanRows = (SourceBean) qExec.exec();

			String strCodiceFiscaleDatore = null;
			String strRagioneSociale = null;
			String codComunicazione = null;
			String strIndirizzo = null;
			String codAzAteco = null;
			String azUtiCf = null;
			String azUtiDenom = null;
			String azUtiInd = null;
			String datInizioMov = null;
			String datFineMov = null;
			String datFinePf = null;
			String codMansioneMin = null;
			String codContratto = null;
			String codOrario = null;
			BigDecimal numoreSett = null;
			String isMissione = null;
			String codCategoriaTir = null;
			String codTipologiaTir = null;
			String codQualificaSrq = null;
			String indirizzoLavoro = null;
			BigDecimal prgMovimento = null;
			BigDecimal prgMovimentoPrec = null;
			BigDecimal prgMovimentoSucc = null;
			BigDecimal prgMovimentoRett = null;
			String dataInvioCo = null;
			String cfTirocinante = null;
			String nomeTirocinante = null;
			String cognomeTirocinante = null;
			String sessoTirocinante = null;
			String datNascTirocinante = null;
			String provinciaTirocinante = null;
			String codTipoMov = null;
			String azUtiTel = null;
			String azUtiEmail = null;
			String codStatoAtto = null;
			String qualificaSrqDesc = null;
			String tipoContrattoDesc = null;
			String qualificaProfessionaleDesc = null;
			String provinciaTirocinanteDesc = null;

			String provinciaTirocinanteDom = null;
			String provinciaTirocinanteDomDesc = null;
			String provinciaAziendaOsp = null;
			String provinciaAziendaOspDesc = null;
			String provinciaAziendaProm = null;
			String provinciaAziendaPromDesc = null;

			MovimentoTirocinioElement comunicazioniObbligatoria = factory.createMovimentoTirocinio();
			if (movimentazioneBeanRows != null) {
				SourceBean movimentazioneBeanRow = (SourceBean) movimentazioneBeanRows.getAttribute("ROW");
				if (movimentazioneBeanRow != null) {
					XMLGregorianCalendar datInizioMovGregorian = null;
					XMLGregorianCalendar datFinePfGregorian = null;
					XMLGregorianCalendar datFineMovGregorian = null;
					XMLGregorianCalendar datNascTirocinanteGregorian = null;

					codTipoMov = (String) movimentazioneBeanRow.getAttribute("CODTIPOMOV");
					strCodiceFiscaleDatore = (String) movimentazioneBeanRow.getAttribute("STRCODICEFISCALE");
					strRagioneSociale = (String) movimentazioneBeanRow.getAttribute("STRRAGIONESOCIALE");
					codComunicazione = (String) movimentazioneBeanRow.getAttribute("CODCOMUNICAZIONE");
					strIndirizzo = (String) movimentazioneBeanRow.getAttribute("STRINDIRIZZO");
					codAzAteco = (String) movimentazioneBeanRow.getAttribute("CODAZATECO");
					azUtiCf = (String) movimentazioneBeanRow.getAttribute("AZUTICF");
					azUtiTel = (String) movimentazioneBeanRow.getAttribute("STRTELUTILIZ");
					azUtiEmail = (String) movimentazioneBeanRow.getAttribute("STREMAILUTILIZ");
					azUtiDenom = (String) movimentazioneBeanRow.getAttribute("AZUTIDENOM");
					azUtiInd = (String) movimentazioneBeanRow.getAttribute("AZUTIIND");
					datInizioMov = (String) movimentazioneBeanRow.getAttribute("DATINIZIOMOV");

					if (datInizioMov != null) {
						datInizioMovGregorian = toXMLGregorianCalendarDateOnly(datInizioMov);
					}
					datFineMov = (String) movimentazioneBeanRow.getAttribute("DATFINEMOV");
					if (datFineMov != null) {
						datFineMovGregorian = toXMLGregorianCalendarDateOnly(datFineMov);
					}
					datFinePf = (String) movimentazioneBeanRow.getAttribute("DATFINEPF");
					if (datFinePf != null) {
						datFinePfGregorian = toXMLGregorianCalendarDateOnly(datFinePf);
					}
					codMansioneMin = (String) movimentazioneBeanRow.getAttribute("CODMANSIONE_MIN");
					codContratto = (String) movimentazioneBeanRow.getAttribute("CODCONTRATTO");
					codOrario = (String) movimentazioneBeanRow.getAttribute("CODORARIO");
					numoreSett = (BigDecimal) movimentazioneBeanRow.getAttribute("NUMORESETT");
					isMissione = (String) movimentazioneBeanRow.getAttribute("ISMISSIONE");
					codCategoriaTir = (String) movimentazioneBeanRow.getAttribute("CODCATEGORIATIR");
					codTipologiaTir = (String) movimentazioneBeanRow.getAttribute("CODTIPOLOGIATIR");
					codQualificaSrq = (String) movimentazioneBeanRow.getAttribute("CODQUALIFICASRQ");
					prgMovimento = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTO");
					prgMovimentoPrec = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTOPREC");
					prgMovimentoSucc = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTOSUCC");
					prgMovimentoRett = (BigDecimal) movimentazioneBeanRow.getAttribute("PRGMOVIMENTORETT");
					dataInvioCo = (String) movimentazioneBeanRow.getAttribute("DATCOMUNICAZ");
					cfTirocinante = (String) movimentazioneBeanRow.getAttribute("CODFISCALETIROCINANTE");
					nomeTirocinante = (String) movimentazioneBeanRow.getAttribute("NOMETIROCINANTE");
					cognomeTirocinante = (String) movimentazioneBeanRow.getAttribute("COGNOMETIROCINANTE");
					sessoTirocinante = (String) movimentazioneBeanRow.getAttribute("SESSOTIROCINANTE");
					datNascTirocinante = (String) movimentazioneBeanRow.getAttribute("DATNASCTIROCINANTE");
					codStatoAtto = (String) movimentazioneBeanRow.getAttribute("CODSTATOATTO");
					qualificaSrqDesc = (String) movimentazioneBeanRow.getAttribute("CODQUALIFICASRQ_DESC");
					tipoContrattoDesc = (String) movimentazioneBeanRow.getAttribute("TIPOCONTRATTO_DESC");
					qualificaProfessionaleDesc = (String) movimentazioneBeanRow.getAttribute("MANSIONE_DESC");

					provinciaTirocinante = (String) movimentazioneBeanRow.getAttribute("PROVINCIA_TIROCINANTE");
					provinciaTirocinanteDesc = (String) movimentazioneBeanRow
							.getAttribute("PROVINCIA_TIROCINANTE_DESC");

					provinciaTirocinanteDom = (String) movimentazioneBeanRow.getAttribute("PROVINCIA_TIROCINANTE_DOM");
					provinciaTirocinanteDomDesc = (String) movimentazioneBeanRow
							.getAttribute("PROVINCIA_TIROCINANTE_DOM_DESC");
					provinciaAziendaOsp = (String) movimentazioneBeanRow.getAttribute("PROVINCIA_AZIENDA_OSP");
					provinciaAziendaOspDesc = (String) movimentazioneBeanRow.getAttribute("PROVINCIA_AZIENDA_OSP_DESC");
					provinciaAziendaProm = (String) movimentazioneBeanRow.getAttribute("PROVINCIA_AZIENDA_PROM");
					provinciaAziendaPromDesc = (String) movimentazioneBeanRow
							.getAttribute("PROVINCIA_AZIENDA_PROM_DESC");

					if (codStatoAtto != null) {
						comunicazioniObbligatoria.setCodStatoAtto(codStatoAtto);
					}

					if (datNascTirocinante != null) {
						datNascTirocinanteGregorian = toXMLGregorianCalendarDateOnly(datNascTirocinante);
					}

					if ("1".equals(isMissione)) {
						// gestire utilizzatore
						indirizzoLavoro = azUtiInd;
					} else {
						indirizzoLavoro = strIndirizzo;
					}

					if ("T".equals(codOrario)) {
						codOrario = "FT";
					} else if ("P".equals(codOrario)) {
						codOrario = "PT";
					} else if ("N".equals(codOrario)) {
						if (numoreSett == null) {
							codOrario = "FT";
						} else {
							codOrario = "PT";
						}
					}

					if (codComunicazione != null) {
						comunicazioniObbligatoria.setCodiceComunicazioneAvviamento(codComunicazione.trim());
					}

					comunicazioniObbligatoria.setDataFine(datFineMovGregorian);
					comunicazioniObbligatoria.setDataFinePeriodoFormativo(datFinePfGregorian);
					comunicazioniObbligatoria.setDataInizio(datInizioMovGregorian);
					if (strCodiceFiscaleDatore != null) {
						comunicazioniObbligatoria.setDatoreLavoroCodiceFiscale(strCodiceFiscaleDatore.trim());
					}
					if (strRagioneSociale != null) {
						comunicazioniObbligatoria.setDatoreLavoroDenominazione(strRagioneSociale.trim());
					}
					if (codAzAteco != null) {
						comunicazioniObbligatoria.setDatoreLavoroSettore(codAzAteco.trim());
					}
					if (codMansioneMin != null) {
						comunicazioniObbligatoria.setQualificaProfessionale(codMansioneMin.trim());
					}
					if (codQualificaSrq != null) {
						comunicazioniObbligatoria.setQualificaSrq(codQualificaSrq.trim());
					}
					if (indirizzoLavoro != null) {
						comunicazioniObbligatoria.setSedeLavoroIndirizzo(indirizzoLavoro.trim());
					}
					if (codContratto != null) {
						comunicazioniObbligatoria.setTipoContratto(codContratto.trim());
					}
					if (codCategoriaTir != null) {
						comunicazioniObbligatoria.setTirocinioCategoria(codCategoriaTir.trim());
					}
					if (codTipologiaTir != null) {
						comunicazioniObbligatoria.setTirocinioTipologia(codTipologiaTir.trim());
					}
					if (azUtiCf != null) {
						comunicazioniObbligatoria.setUtilizzatoreCodiceFiscale(azUtiCf.trim());
					}
					if (azUtiTel != null) {
						comunicazioniObbligatoria.setUtilizzatoreTel(azUtiTel);
					}
					if (azUtiEmail != null) {
						comunicazioniObbligatoria.setUtilizzatoreEmail(azUtiEmail);
					}
					if (azUtiDenom != null) {
						comunicazioniObbligatoria.setUtilizzatoreDenominazione(azUtiDenom.trim());
					}
					if (prgMovimento != null) {
						comunicazioniObbligatoria.setPrgMovimentoSil(prgMovimento.toBigInteger());
					}

					comunicazioniObbligatoria.setCodTipoMov(codTipoMov);
					if (prgMovimentoPrec != null) {
						comunicazioniObbligatoria.setPrgMovimentoSilPrec(prgMovimentoPrec.toBigInteger());
					}
					if (prgMovimentoSucc != null) {
						comunicazioniObbligatoria.setPrgMovimentoSilSucc(prgMovimentoSucc.toBigInteger());
					}
					if (prgMovimentoRett != null) {
						comunicazioniObbligatoria.setPrgMovimentoSilRett(prgMovimentoRett.toBigInteger());
					}

					comunicazioniObbligatoria.setDataInvioCo(dataInvioCo);
					if (cfTirocinante != null) {
						comunicazioniObbligatoria.setTirocinanteCodiceFiscale(cfTirocinante.trim());
					}
					if (nomeTirocinante != null) {
						comunicazioniObbligatoria.setTirocinanteNome(nomeTirocinante.trim());
					}
					if (cognomeTirocinante != null) {
						comunicazioniObbligatoria.setTirocinanteCognome(cognomeTirocinante.trim());
					}
					if (sessoTirocinante != null) {
						comunicazioniObbligatoria.setTirocinanteSesso(sessoTirocinante.trim());
					}
					comunicazioniObbligatoria.setTirocinanteDatnasc(datNascTirocinanteGregorian);

					comunicazioniObbligatoria.setProvinciaTirocinante(provinciaTirocinante.trim());

					if (codProvinciaPoloRegionale != null && !codProvinciaPoloRegionale.equals("")) {
						comunicazioniObbligatoria.setProvinciaProvenienza(codProvinciaPoloRegionale);
					}
					else {
						comunicazioniObbligatoria.setProvinciaProvenienza(getCodProvinciaMinisteriale());
					}

					if (qualificaSrqDesc != null) {
						comunicazioniObbligatoria.setQualificaSrqDesc(qualificaSrqDesc);
					}

					if (tipoContrattoDesc != null) {
						comunicazioniObbligatoria.setTipoContrattoDesc(tipoContrattoDesc);
					}

					if (qualificaProfessionaleDesc != null) {
						comunicazioniObbligatoria.setQualificaProfessionaleDesc(qualificaProfessionaleDesc);
					}

					comunicazioniObbligatoria.setProvinciaTirocinanteDesc(provinciaTirocinanteDesc.trim());

					if (codProvinciaPoloRegionale != null && !codProvinciaPoloRegionale.equals("")) {
						comunicazioniObbligatoria.setProvinciaDesc(descProvinciaPoloRegionale);
					}
					else {
						comunicazioniObbligatoria.setProvinciaDesc(getDescProvinciaMinisteriale());
					}

					comunicazioniObbligatoria.setProvinciaTirocinanteDom(provinciaTirocinanteDom.trim());
					comunicazioniObbligatoria.setProvinciaTirocinanteDomDesc(provinciaTirocinanteDomDesc.trim());

					comunicazioniObbligatoria.setProvinciaAziendaOsp(provinciaAziendaOsp.trim());
					comunicazioniObbligatoria.setProvinciaAziendaOspDesc(provinciaAziendaOspDesc.trim());

					if (provinciaAziendaProm != null) {
						comunicazioniObbligatoria.setProvinciaAziendaProm(provinciaAziendaProm.trim());
						comunicazioniObbligatoria.setProvinciaAziendaPromDesc(provinciaAziendaPromDesc.trim());
					}
				}
			}

			/*
			 * invio dati a MyStage
			 */
			String endPoint = getEndpointUrl(dc.getInternalConnection());
			System.out.println("endpoint " + endPoint);
			if (endPoint == null) {
				logger.error("Nessun EndPoint definito per la comunicazione a MyPortal");
			} else {
				String strUserId = null;
				String strPassword = null;
				if (wsRows != null) {
					strUserId = (String) wsRows.getAttribute("ROW.STRUSERID");
					strPassword = (String) wsRows.getAttribute("ROW.STRPASSWORD");
				}

				String xmlToSend = convertInputToString(comunicazioniObbligatoria);
				logger.debug("XML inviata a WS mystage: " + xmlToSend);

				// validazione xsd
				boolean outputXmlIsValid = XmlUtils.isXmlValid(xmlToSend, GetMovimentoTirocinio.MovTir_SchemaFile);
				if (outputXmlIsValid) {
					ImportMovimentoSilProxy proxy = new ImportMovimentoSilProxy();
					proxy.setEndpoint(endPoint);
					try {
						String risposta = proxy.putMovimento(strUserId, strPassword, xmlToSend);
						logger.debug("Valore tornato dal WS mystage: " + risposta);
						if (risposta.startsWith("Movimento Tirocinio caricato correttamente"))
							contaMovimentiCorretti++;
						else
							contaMovimentiErrati++;

						if (risposta.startsWith("-")) {
							// questo invia una mail
							logger.error("Valore negativo tornato dal WS mystage: " + risposta + "\n\n" + xmlToSend);
						}

					} catch (Exception e) {
						// questo invia una mail
						logger.error("Errore nel web service di mystage", e);
					}
				} else {
					logger.error("Errore validazione XML movimento " + prgMovimento);
				}
			}
		} catch (Throwable e) {
			logger.error("BatchInvioMovimentiTirocini errore: " + e);
		} finally {
			if (dc != null) {
				dc.close();
			}
			logger.debug("BatchInvioMovimentiTirocini fine gestisciInvioTirocinioLavoratore prgmovimento:"
					+ prgmovimento.toString());
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

	public static void aggiornaDataBatch(Connection connection) {
		logger.debug("BatchInvioMovimentiTirocini aggiornamento data chiamata batch");
		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(QUERY_UPDATE_DATA_BATCH);
			preparedStatement.executeUpdate();

			preparedStatement.close();

		} catch (Exception e) {
			logger.error("Errore: aggiornamento data batch movimenti tirocini", e);
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

	public static String getEndpointUrl(Connection conn) throws Exception {
		PreparedStatement psUrl = null;
		String endPoint = null;
		ResultSet rsUrl = null;

		String statementUrl = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT WHERE strName = 'MyStageMovimenti'";
		psUrl = conn.prepareStatement(statementUrl);

		rsUrl = psUrl.executeQuery();
		if (rsUrl.next()) {
			endPoint = rsUrl.getString("strUrl");
		}

		rsUrl.close();
		psUrl.close();

		return endPoint;
	}

	public String getCodProvinciaMinisteriale() {
		return this.codProvinciaMinisteriale;
	}

	public void setCodProvinciaMinisteriale(String value) {
		this.codProvinciaMinisteriale = value;
	}

	public String getDescProvinciaMinisteriale() {
		return this.descProvinciaMinisteriale;
	}

	public void setDescProvinciaMinisteriale(String value) {
		this.descProvinciaMinisteriale = value;
	}

	private static String convertInputToString(MovimentoTirocinioElement xml) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(MovimentoTirocinioElement.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(xml, writer);
		String xmlStr = writer.getBuffer().toString();
		return xmlStr;
	}
}
