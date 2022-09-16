package it.eng.sil.coop.webservices.yg.notificaSap;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.ModificaCPICompIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coordinamento.servizi.ServizioSoap;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.pojo.yg.NotificaSap;
import it.eng.sil.util.InfoProvinciaSingleton;

public class RegistraNotificaSAP implements ServizioSoap {

	private static final Logger _logger = Logger.getLogger(RegistraNotificaSAP.class.getName());

	private String xmlNotifica = null;
	private final String MOTIVO_BRUCIATURA = "01";
	private final String MOTIVO_PERDITA_POSSESSO = "02";
	private final String MOTIVO_AGGIORNAMENTO = "07";
	private final String COD_STATO_BRUCIATA = "02";
	private final String COD_STAT_CAMBIO_TITOLARITA = "04";
	private final Integer CDN_UT_SIL_COOP = 190;
	private final String EVIDENZA_AGGIORNAMENTO_SAP = "SP";

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coop.services.IFaceService#send(javax.jms.Message)
	 */
	public String elabora(Execute parametri) {
		_logger.debug("Notifica SAP... ");

		TransactionQueryExecutor tex = null;
		RispostaXML rispostaXML = new RispostaXML("", "", "");
		String risultato = "";
		boolean isOKCodiceMotivo = true;
		boolean isOkTransactionWithError = false;
		try {
			xmlNotifica = parametri.getDati();

			NotificaSap notificaSAP = convertToVerificaSAP(xmlNotifica);
			String codMinSAPNotifica = notificaSAP.getIdentificativoSAP();
			String codMotivoNotifica = notificaSAP.getMotivoNotifica();

			boolean coopAbilitata = true;
			String coopAttiva = System.getProperty("cooperazione.enabled");
			if (coopAttiva != null && coopAttiva.equalsIgnoreCase("false")) {
				coopAbilitata = false;
			}

			SourceBean motivoNotificaSB = (SourceBean) QueryExecutor.executeQuery("GET_MOTIVO_NOTIFICA_SAP_LAV",
					new Object[] { codMotivoNotifica }, "SELECT", Values.DB_SIL_DATI);
			if (motivoNotificaSB != null) {
				motivoNotificaSB = motivoNotificaSB.containsAttribute("ROW")
						? (SourceBean) motivoNotificaSB.getAttribute("ROW")
						: motivoNotificaSB;
				if (motivoNotificaSB.getAttribute("CODMOTIVOMODIFICA") == null) {
					isOKCodiceMotivo = false;
				}
			} else {
				isOKCodiceMotivo = false;
			}

			if (!isOKCodiceMotivo) {
				// ERRORE SU CODICE MOTIVO
				String errMsg = "RegistraNotificaSAP: fallito. Codice motivo non riconosciuto.\n" + xmlNotifica;
				_logger.error(errMsg);
				rispostaXML = new RispostaXML("999", errMsg, "E");
			} else {
				// Inizializzo il TransactionQueryExecutor
				tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				tex.initTransaction();

				/* inserisco la notifica */
				tex.executeQuery("INSERT_SP_NOTIFICA",
						new Object[] { codMinSAPNotifica, codMotivoNotifica, CDN_UT_SIL_COOP, CDN_UT_SIL_COOP },
						"INSERT");

				if (codMotivoNotifica.equals(MOTIVO_BRUCIATURA) || codMotivoNotifica.equals(MOTIVO_PERDITA_POSSESSO)
						|| codMotivoNotifica.equals(MOTIVO_AGGIORNAMENTO)) {
					SourceBean spLavoratoreSB = (SourceBean) tex.executeQuery("SELECT_SP_LAVORATORE",
							new Object[] { codMinSAPNotifica }, "SELECT");
					if (spLavoratoreSB.getAttribute("ROW") instanceof Vector) {
						// PIU' DI UN SP_LAVORATORE TROVATO
						String errMsg = "RegistraNotificaSAP: fallito. Trovata piu' di una riga su SP_LAVORATORE con CODMINSAP = "
								+ codMinSAPNotifica + " e DATFINEVAL = NULL";
						_logger.error(errMsg);
						rispostaXML = new RispostaXML("999", errMsg, "E");
						tex.rollBackTransaction();
						isOkTransactionWithError = true;
					} else {
						SourceBean spLavoratoreRow = (SourceBean) spLavoratoreSB.getAttribute("ROW");

						if (spLavoratoreRow != null) {
							BigDecimal prgSpLav = (BigDecimal) spLavoratoreRow.getAttribute("PRGSPLAV");
							BigDecimal cdnLavoratore = (BigDecimal) spLavoratoreRow.getAttribute("CDNLAVORATORE");
							String datInizioVal = (String) spLavoratoreRow.getAttribute("DATINIZIOVAL");
							BigDecimal numKloSap = (BigDecimal) spLavoratoreRow.getAttribute("NUMKLOSAP");

							if (codMotivoNotifica.equals(MOTIVO_AGGIORNAMENTO)) {
								if (prgSpLav != null) {
									String strEvidenza = new StringBuffer(
											"Attenzione: e' giunta una notifica dal Ministero. Per il lavoratore e' disponibile un aggiornamento della SAP su NCN.")
													.toString();
									SourceBean spEvidenza = (SourceBean) tex.executeQuery(
											"GET_PRG_TIPO_EVIDENZA_BY_COD_TIPO_EVIDENZA",
											new Object[] { EVIDENZA_AGGIORNAMENTO_SAP }, "SELECT");
									if (spEvidenza != null) {
										BigDecimal prgTipoEvidenza = (BigDecimal) spEvidenza
												.getAttribute("row.prgtipoevidenza");
										insertEvidenza(tex, cdnLavoratore, strEvidenza, prgTipoEvidenza);
									}
								}
							} else {
								/*
								 * se la data di chiusura e' precedente all'apertura uso la stessa data di apertura
								 * anche per la chiusura
								 */
								String oggi = DateUtils.getNow();
								String datFineVal = DateUtils.giornoPrecedente(oggi);
								if (DateUtils.daysBetween(datInizioVal, datFineVal) < 0) {
									datFineVal = datInizioVal;
								}

								/* termino la validita' del record trovato */
								tex.executeQuery("CHIUDU_SP_LAVORATORE",
										new Object[] { datFineVal, CDN_UT_SIL_COOP, numKloSap, prgSpLav }, "UPDATE");

								if (codMotivoNotifica.equals(MOTIVO_BRUCIATURA)) {
									tex.executeQuery("INSERT_SP_LAVORATORE", new Object[] { CDN_UT_SIL_COOP,
											CDN_UT_SIL_COOP, COD_STATO_BRUCIATA, prgSpLav }, "INSERT");
								} else if (codMotivoNotifica.equals(MOTIVO_PERDITA_POSSESSO)) {
									tex.executeQuery("INSERT_SP_LAVORATORE", new Object[] { CDN_UT_SIL_COOP,
											CDN_UT_SIL_COOP, COD_STAT_CAMBIO_TITOLARITA, prgSpLav }, "INSERT");
									SourceBean anLavStoriaInfSB = (SourceBean) tex.executeQuery(
											"SELECT_AN_LAV_STORIA_INF_SAP", new Object[] { cdnLavoratore }, "SELECT");
									if (anLavStoriaInfSB.getAttribute("ROW") instanceof Vector) {
										String errMsg = "RegistraNotificaSAP: fallito. Trovata piu' di una riga su AN_LAV_STORIA_INF con CDNLAVORATORE = "
												+ cdnLavoratore;
										_logger.error(errMsg);
										rispostaXML = new RispostaXML("999", errMsg, "E");
										tex.rollBackTransaction();
										isOkTransactionWithError = true;
									} else {
										SourceBean anLavStoriaInfRow = (SourceBean) anLavStoriaInfSB
												.getAttribute("ROW");
										if (anLavStoriaInfRow != null) {
											BigDecimal prgLavStoriaInf = (BigDecimal) anLavStoriaInfRow
													.getAttribute("PRGLAVSTORIAINF");
											String codCpiTit = (String) anLavStoriaInfRow.getAttribute("CODCPITIT");
											String codComDom = (String) anLavStoriaInfRow.getAttribute("CODCOMDOM");
											String strCodiceFiscaleOld = (String) anLavStoriaInfRow
													.getAttribute("STRCODICEFISCALEOLD");
											BigDecimal cdnUtModSchedaAnagProf = (BigDecimal) anLavStoriaInfRow
													.getAttribute("CDNUTMODSCHEDAANAGPROF");
											String dtmModSchedaAnagProf = (String) anLavStoriaInfRow
													.getAttribute("DTMMODSCHEDAANAGPROF");
											String codMonoTipoOrig = (String) anLavStoriaInfRow
													.getAttribute("CODMONOTIPOORIG");
											String datDichiarazione = (String) anLavStoriaInfRow
													.getAttribute("DATDICHIARAZIONE");
											String flg181 = (String) anLavStoriaInfRow.getAttribute("FLG181");
											String datAnzianitaDisoc = (String) anLavStoriaInfRow
													.getAttribute("DATANZIANITADISOC");
											BigDecimal numAnzianitaPrec97 = (BigDecimal) anLavStoriaInfRow
													.getAttribute("NUMANZIANITAPREC297");
											BigDecimal numMesiSosp = (BigDecimal) anLavStoriaInfRow
													.getAttribute("NUMMESISOSP");
											String flgCessato = (String) anLavStoriaInfRow.getAttribute("FLGCESSATO");
											String codMotivoCessato = (String) anLavStoriaInfRow
													.getAttribute("CODMOTIVOCESSATO");
											String codStatoOccupazOrig = (String) anLavStoriaInfRow
													.getAttribute("CODSTATOOCCUPAZORIG");
											String codLavFlag = (String) anLavStoriaInfRow.getAttribute("CODLAVFLAG");
											String codStatoOccupazDerivatoDaPro = (String) anLavStoriaInfRow
													.getAttribute("CODSTATOOCCUPAZDERIVATODAPRO");
											String strChiaveTabellaProLabor = (String) anLavStoriaInfRow
													.getAttribute("STRCHIAVETABELLAPROLABOR");
											String datChiaveTabellaProLabor = (String) anLavStoriaInfRow
													.getAttribute("DATCHIAVETABELLAPROLABOR");
											String strNomeTabellaProLabor = (String) anLavStoriaInfRow
													.getAttribute("STRNOMETABELLAPROLABOR");
											String strNote = (String) anLavStoriaInfRow.getAttribute("STRNOTE");
											String codMonoCalcoloAnzianitaPrec297 = (String) anLavStoriaInfRow
													.getAttribute("CODMONOCALCOLOANZIANITAPREC297");
											String datCalcoloAnzianita = (String) anLavStoriaInfRow
													.getAttribute("DATCALCOLOANZIANITA");
											String datCalcoloMesiSosp = (String) anLavStoriaInfRow
													.getAttribute("DATCALCOLOMESISOSP");
											String flgStampaTrasf = (String) anLavStoriaInfRow
													.getAttribute("FLGSTAMPATRASF");
											String flgStampaDoc = (String) anLavStoriaInfRow
													.getAttribute("FLGSTAMPADOC");
											String codCpiOrigPrec = (String) anLavStoriaInfRow
													.getAttribute("CODCPIORIGPREC");

											tex.executeQuery("INSERT_AN_LAV_STORIA_INF_SAP", new Object[] {
													cdnLavoratore, codCpiTit, codComDom, strCodiceFiscaleOld,
													cdnUtModSchedaAnagProf, dtmModSchedaAnagProf, codMonoTipoOrig,
													datDichiarazione, flg181, datAnzianitaDisoc, numAnzianitaPrec97,
													numMesiSosp, flgCessato, codMotivoCessato, codStatoOccupazOrig,
													codLavFlag, codStatoOccupazDerivatoDaPro, strChiaveTabellaProLabor,
													datChiaveTabellaProLabor, strNomeTabellaProLabor, strNote,
													codMonoCalcoloAnzianitaPrec297, datCalcoloAnzianita,
													datCalcoloMesiSosp, flgStampaTrasf, flgStampaDoc, codCpiOrigPrec,
													CDN_UT_SIL_COOP, CDN_UT_SIL_COOP }, "INSERT");

											/*
											 * PEZZA: completo la chiusura del vecchio record in an_lav_storia_inf PER
											 * CONSENTIRE LA INSERT IN LG_LAV_STORIA_INF
											 */
											tex.executeQuery("CHIUDI_AN_LAV_STORIA_INF_SAP",
													new Object[] { CDN_UT_SIL_COOP, prgLavStoriaInf }, "UPDATE");

											/* invoco indice regionale */
											SourceBean cpiMasterSB = (SourceBean) tex
													.executeQuery("SELECT_MASTER_CPI_YG", new Object[] {}, "SELECT");
											SourceBean cpiMasterRow = (SourceBean) cpiMasterSB.getAttribute("ROW");
											if (cpiMasterRow != null) {
												String codCpi = (String) cpiMasterRow.getAttribute("CODCPI");

												SourceBean anLavoratoreSB = (SourceBean) tex.executeQuery(
														"SELECT_DATI_AN_LAVORATORE_SAP", new Object[] { cdnLavoratore },
														"SELECT");
												SourceBean anLavoratoreRow = (SourceBean) anLavoratoreSB
														.getAttribute("ROW");
												if (anLavoratoreRow != null) {
													String codiceFiscale = (String) anLavoratoreRow
															.getAttribute("STRCODICEFISCALE");
													String cognome = (String) anLavoratoreRow
															.getAttribute("STRCOGNOME");
													String nome = (String) anLavoratoreRow.getAttribute("STRNOME");
													String dataNascita = (String) anLavoratoreRow
															.getAttribute("DATNASC");
													String codComNascita = (String) anLavoratoreRow
															.getAttribute("CODCOMNAS");
													tex.executeQuery("TRACCIA_AN_LAVORATORE",
															new Object[] { CDN_UT_SIL_COOP, cdnLavoratore }, "UPDATE");
													if (coopAbilitata) {
														callIDX(codCpi, codiceFiscale, cognome, nome, dataNascita,
																codComNascita, codCpiTit);
													}
												} else {
													String errMsg = "RegistraNotificaSAP: fallito. Nessun AN_LAVORATORE trovato con CDNLAVORATORE = "
															+ cdnLavoratore;
													_logger.error(errMsg);
													rispostaXML = new RispostaXML("999", errMsg, "E");
													tex.rollBackTransaction();
													isOkTransactionWithError = true;
												}
											}
										}
									}
								}
							}
						}
					}
				}

				if (!isOkTransactionWithError) {
					// -- COMMIT TRANSAZIONE
					String infoMsg = "Notifica SAP registrata.";
					_logger.info(infoMsg);
					rispostaXML = new RispostaXML("101", infoMsg, "I");
					tex.commitTransaction();
				}
			}
		} catch (Exception e) {
			try {
				if (tex != null) {
					String errMsg = "RegistraNotificaSAP: fallito. \n" + xmlNotifica;
					_logger.error(errMsg, e);
					rispostaXML = new RispostaXML("999", errMsg, "E");
					tex.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				_logger.error("RegistraNotificaSAP: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "RegistraNotificaSAP: problema con la rollback", "E");
			}
		}
		risultato = rispostaXML.toXMLString();
		return risultato;
	}

	private void callIDX(String nuovoCpiComp, String codiceFiscale, String cognome, String nome, String dataNascita,
			String codComNascita, String codCpiPrec) {
		String cdnGruppo = null, cdnProfilo = null, strMittente = null, poloMittente = null, cdnUtente = null;
		String dataRichiesta = null;
		String codProvinciaOp = null;

		dataRichiesta = DateUtils.getNow();

		// parametri di inserimento
		cdnGruppo = "1";
		cdnProfilo = "4";
		strMittente = "Utente di Cooperazione";
		cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
		poloMittente = InfoProvinciaSingleton.getInstance().getCodice();
		// il polo mitttente e' il De_provincia.codProvincia dell'operatore
		codProvinciaOp = poloMittente;

		TestataMessageTO testata = new TestataMessageTO();
		testata.setPoloMittente(poloMittente);
		testata.setCdnUtente(cdnUtente);
		testata.setCdnGruppo(cdnGruppo);
		testata.setCdnProfilo(cdnProfilo);
		testata.setStrMittente(strMittente);

		ModificaCPICompIRMessage messaggio = new ModificaCPICompIRMessage();
		// imposto i parametri applicativi
		messaggio.setCodCpiNuovo(nuovoCpiComp);
		messaggio.setCodiceFiscale(codiceFiscale);
		// si potrebbe omettere dato che e' il poloMittente
		messaggio.setCodProvinciaOp(codProvinciaOp);
		messaggio.setCognome(cognome);
		messaggio.setCodComNascita(codComNascita);
		messaggio.setNome(nome);
		messaggio.setDataNascita(dataNascita);
		messaggio.setDataRichiesta(dataRichiesta);
		// codcpitit indifferentemente dal mono tipo
		messaggio.setCodCpiPrec(codCpiPrec);
		messaggio.setTestata(testata);

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();

		messaggio.setDataSourceJndi(dataSourceJndiName);

		OutQ outQ = new OutQ();

		try {
			messaggio.send(outQ);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public NotificaSap convertToVerificaSAP(String xmlNotificaSAP) throws JAXBException {
		JAXBContext jaxbContext;
		NotificaSap notificaSap = null;
		try {
			jaxbContext = JAXBContext.newInstance(NotificaSap.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			notificaSap = (NotificaSap) jaxbUnmarshaller.unmarshal(new StringReader(xmlNotificaSAP));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return notificaSap;
	}

	public boolean insertEvidenza(TransactionQueryExecutor tex, BigDecimal cdnLavoratore, String strEvidenza,
			BigDecimal prgTipoEvidenza) throws Exception {
		Object[] params = new Object[3];
		params[0] = cdnLavoratore;
		params[1] = strEvidenza;
		params[2] = prgTipoEvidenza;
		boolean esito = ((Boolean) tex.executeQuery("INSERT_EVIDENZA", params, "INSERT")).booleanValue();

		return esito;
	}
}