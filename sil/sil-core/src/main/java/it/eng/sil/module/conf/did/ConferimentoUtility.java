package it.eng.sil.module.conf.did;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.DidBean;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Applicazione;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Genere;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento;
import it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling;
import it.gov.mlps.types.GestisciDIDInput;
import it.gov.mlps.types.GestisciDIDOutput;

public class ConferimentoUtility {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ConferimentoUtility.class.getName());

	private MultipleTransactionQueryExecutor transExec = null;

	private BigDecimal prgElencoAnagrafico = null;
	private BigDecimal cdnLavoratore = null;
	private String dataDichiarazione = null;
	private String dataAvvioA02A05 = null;
	private String codCpi = null;
	private String codCpiMin = null;
	private String codRegioneMin = null;
	private String dataNormativa297 = null;
	private DidBean didConf = null;
	private String codiceFiscale = null;
	private BigDecimal prgPercorsoA02 = null;
	private BigDecimal prgColloquioA02 = null;

	private static final String noteColloquio = "colloquio inserito per conferimento did";

	public static final int ETA_MINIMA_CONF = 15;
	public static final String ESITOCONFERIMENTOOK = "E100";
	public static final String EVENTOCONVALIDA = "C";
	public static final String EVENTOINSERIMENTO = "I";
	public static final String EVENTOREVOCA = "R";
	public static final String STATODAINVIARE = "D";
	public static final String STATOINVIATO = "I";
	public static final String CONF_DID_ANPAL = "ANPAL";
	private GestisciDIDInput inputConferimento = null;
	private GestisciDIDOutput outputConferimento = null;

	public ConferimentoUtility(MultipleTransactionQueryExecutor tx, BigDecimal cdnLav, String dataDid, String codCpiRif)
			throws Exception {
		this.transExec = tx;
		this.cdnLavoratore = cdnLav;
		this.dataDichiarazione = dataDid;
		this.codCpi = codCpiRif;
		caricaInfoLavoratore();
	}

	public ConferimentoUtility() {
		super();
	}

	protected void caricaInfoLavoratore() throws Exception {
		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = cdnLavoratore;
		SourceBean row = null;
		if (transExec == null) {
			row = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE_FROM_CDNLAV", params, "SELECT",
					Values.DB_SIL_DATI);
		} else {
			row = (SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE_FROM_CDNLAV", params, "SELECT");
		}
		row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
		this.codiceFiscale = (String) row.getAttribute("strcodicefiscale");
		this.codCpiMin = (String) row.getAttribute("codcpimin");
		this.prgElencoAnagrafico = (BigDecimal) row.getAttribute("prgelencoanagrafico");
		this.codRegioneMin = (String) row.getAttribute("codmin");
		UtilsConfig utility = new UtilsConfig("AM_297");
		this.dataNormativa297 = utility.getValoreConfigurazione();
	}

	public int gestisciPresaInCarico150(User user) {
		BigDecimal userid = new BigDecimal(user.getCodut());
		BigDecimal prgAzione = null;
		BigDecimal prgColloquio = null;
		BigDecimal prgSpiCollegato = null;
		Object[] vettColloquioSEP = new Object[2];
		String dataColloquioSEP = null;
		try {
			Object[] params = new Object[] { cdnLavoratore, dataDichiarazione, dataDichiarazione, dataDichiarazione,
					dataDichiarazione };
			SourceBean row = (SourceBean) transExec.executeQuery("GET_AZIONE_PRESA_CARICO_150", params, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				prgAzione = (BigDecimal) row.getAttribute("prgPercorso");
			}
			if (prgAzione != null) {
				this.dataAvvioA02A05 = SourceBeanUtils.getAttrStrNotNull(row, "DATAVVIOAZIONE");
				return MessageCodes.CONFERIMENTO_DID.PRESA_CARICO150_PRESENTE;
			} else {
				this.dataAvvioA02A05 = null;
				prgSpiCollegato = getOperatoreCollegato(userid);
				prgColloquio = getColloquioPresaInCarico(null);

				BigDecimal prgAzioneColloquio = cercaAzione("A02", "05");

				String codServizioColloquio = getCodServizio(prgAzioneColloquio);

				if (prgColloquio == null) {
					vettColloquioSEP = getColloquioSEPPresaInCarico(codServizioColloquio);
					prgColloquio = (BigDecimal) vettColloquioSEP[0];
					dataColloquioSEP = (String) vettColloquioSEP[1];
					if (prgColloquio == null) {
						boolean inserimentoColloquio;
						prgColloquio = getProgressivoColloquio();

						inserimentoColloquio = inserisciColloquio(prgColloquio, codServizioColloquio, dataDichiarazione,
								userid, prgSpiCollegato);

						if (inserimentoColloquio) {
							inserimentoColloquio = inserisciSchedaColloquio(prgColloquio);
						}
					}
				}

				BigDecimal prgPercorso = getProgressivoPercorso();

				boolean inserimentoPercorso;
				if (dataColloquioSEP != null && !dataColloquioSEP.equals("")) {
					inserimentoPercorso = inserisciAzione(prgPercorso, prgColloquio, "AVV", "E", dataColloquioSEP,
							prgAzioneColloquio, prgSpiCollegato, userid);
				} else {
					inserimentoPercorso = inserisciAzione(prgPercorso, prgColloquio, "AVV", "E", dataDichiarazione,
							prgAzioneColloquio, prgSpiCollegato, userid);
				}
				if (inserimentoPercorso) {
					this.prgPercorsoA02 = prgPercorso;
					this.prgColloquioA02 = prgColloquio;
					return 0;
				} else {
					return MessageCodes.CONFERIMENTO_DID.ERR_PRESA_CARICO150;
				}

			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferimento did conferma:gestisciPresaInCarico150.",
					(Exception) e);
			return MessageCodes.CONFERIMENTO_DID.ERR_GENERICO_PRESA_CARICO150;
		}
	}

	public int gestisciPresaInCarico150Trasferimento(User user, String codEsito, String codEsitoRendicont,
			String dataTrasferimento) {
		BigDecimal userid = new BigDecimal(user.getCodut());
		BigDecimal prgAzione = null;
		BigDecimal prgColloquio = null;
		BigDecimal prgSpiCollegato = null;
		Object[] vettColloquioSEP = new Object[2];
		String dataColloquioSEP = null;
		try {
			Object[] params = new Object[] { cdnLavoratore, dataDichiarazione, dataDichiarazione, dataDichiarazione,
					dataDichiarazione };
			SourceBean row = (SourceBean) transExec.executeQuery("GET_AZIONE_PRESA_CARICO_150", params, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				prgAzione = (BigDecimal) row.getAttribute("prgPercorso");
			}
			if (prgAzione != null) {
				this.dataAvvioA02A05 = SourceBeanUtils.getAttrStrNotNull(row, "DATAVVIOAZIONE");
				return MessageCodes.CONFERIMENTO_DID.PRESA_CARICO150_PRESENTE;
			} else {
				this.dataAvvioA02A05 = null;
				prgSpiCollegato = getOperatoreCollegato(userid);
				prgColloquio = getColloquioPresaInCarico(dataTrasferimento);
				BigDecimal prgAzioneColloquio = cercaAzione("A02", "05");

				String codServizioColloquio = getCodServizio(prgAzioneColloquio);

				if (prgColloquio == null) {
					vettColloquioSEP = getColloquioSEPPresaInCarico(codServizioColloquio);
					prgColloquio = (BigDecimal) vettColloquioSEP[0];
					dataColloquioSEP = (String) vettColloquioSEP[1];
					if (prgColloquio == null) {
						boolean inserimentoColloquio;
						prgColloquio = getProgressivoColloquio();

						inserimentoColloquio = inserisciColloquio(prgColloquio, codServizioColloquio, dataTrasferimento,
								userid, prgSpiCollegato);

						if (inserimentoColloquio) {
							inserimentoColloquio = inserisciSchedaColloquio(prgColloquio);
						}
					}
				}

				BigDecimal prgPercorso = getProgressivoPercorso();
				boolean inserimentoPercorso;
				if (dataColloquioSEP != null && !dataColloquioSEP.equals("")) {
					inserimentoPercorso = inserisciAzione(prgPercorso, prgColloquio, codEsito, codEsitoRendicont,
							dataColloquioSEP, prgAzioneColloquio, prgSpiCollegato, userid);
				} else {
					inserimentoPercorso = inserisciAzione(prgPercorso, prgColloquio, codEsito, codEsitoRendicont,
							dataTrasferimento, prgAzioneColloquio, prgSpiCollegato, userid);
				}
				if (inserimentoPercorso) {
					this.prgPercorsoA02 = prgPercorso;
					this.prgColloquioA02 = prgColloquio;
					return 0;
				} else {
					return MessageCodes.CONFERIMENTO_DID.ERR_PRESA_CARICO150;
				}

			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in conferimento did conferma:gestisciPresaInCarico150.",
					(Exception) e);
			return MessageCodes.CONFERIMENTO_DID.ERR_GENERICO_PRESA_CARICO150;
		}
	}

	public int gestisciDID(User user, RequestContainer requestContainer, SourceBean request, SourceBean response) {
		boolean successDid = true;
		BigDecimal userid = new BigDecimal(user.getCodut());
		try {
			didConf = new DidBean();
			didConf.esisteDid(cdnLavoratore, transExec);
			BigDecimal prgDid = didConf.getPrgDichDisponibilita();
			if (prgDid == null) {
				if (DateUtils.compare(dataDichiarazione, dataNormativa297) < 0) {
					return MessageCodes.CONFERIMENTO_DID.ERR_CONTROLLI_STIPULA_DID;
				}
				didConf.setPrgElencoAnagrafico(getPrgElencoAnagrafico());
				int resultAutDid = didConf.gestisciAutomatismiDID(cdnLavoratore, dataDichiarazione, userid, transExec);

				if (resultAutDid <= 0) {

					successDid = didConf.inserisciDid(request, response, cdnLavoratore.toString(), dataDichiarazione,
							prgElencoAnagrafico, codCpi, userid, requestContainer, transExec);

					if (successDid) {
						// eventuale aggiornamento competenza amministrativa
						boolean cambioCPICompetenza = it.eng.sil.util.amministrazione.impatti.DidBean
								.aggiornaCPI(cdnLavoratore, codCpi, userid, transExec);
						// Aggiorna competenza indice regionale solo per RER
						if (cambioCPICompetenza) {
							try {
								// Gestione configurazione aggiornamento competenza indice regionale (IR solo per RER)
								UtilsConfig utility = new UtilsConfig("AGCOMPIR");
								String configIR = utility.getConfigurazioneDefault_Custom();
								if (configIR.equals(Properties.CUSTOM_CONFIG)) {
									it.eng.sil.util.amministrazione.impatti.DidBean.aggiornaCompetenzaIR(user,
											cdnLavoratore);
								}
							} catch (Exception irex) {
								it.eng.sil.util.TraceWrapper.error(_logger,
										"Conferma did ministeriale:inserisciDID():aggiornaCompetenzaIR", irex);
							}
						}
					} else {
						return didConf.getCodiceErroreDID();
					}

					if (successDid) {
						calcolaStatoOccupazionale(didConf.getDatDichiarazione());
						didConf.aggiornaInfoDid(transExec, cdnLavoratore);
						return 0;
					} else {
						return MessageCodes.CONFERIMENTO_DID.ERR_RICALCOLA_IMPATTI;
					}
				} else {
					// Effettuata in automatico la riapertura did annullata e la conseguente riapertura del patto
					if (request.containsAttribute("cdnLavoratore")) {
						request.updAttribute("cdnLavoratore", cdnLavoratore.toString());
					} else {
						request.setAttribute("cdnLavoratore", cdnLavoratore.toString());
					}
					if (request.containsAttribute("FORZA_INSERIMENTO")) {
						request.updAttribute("FORZA_INSERIMENTO", "true");
					} else {
						request.setAttribute("FORZA_INSERIMENTO", "true");
					}
					if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
						request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
					} else {
						request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
					}
					if (request.containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
						request.updAttribute("FORZA_CHIUSURA_MOBILITA", "true");
					} else {
						request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
					}
					requestContainer.setServiceRequest(request);
					calcolaStatoOccupazionale(dataDichiarazione);
					didConf.aggiornaInfoDid(transExec, cdnLavoratore);
					return 0;
				}
			} else {
				if (DateUtils.compare(didConf.getDatDichiarazione(), dataDichiarazione) != 0) {
					return MessageCodes.CONFERIMENTO_DID.DID_PRESENTE_NON_COERENTE;
				} else {
					return MessageCodes.CONFERIMENTO_DID.DID_PRESENTE;
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"Errore in inserimento dichiarazione immediata disponibiltà da conferimento did.", (Exception) e);
			return MessageCodes.CONFERIMENTO_DID.ERR_GENERICO_STIPULA_DID;
		}
	}

	private void calcolaStatoOccupazionale(String dataCalcolo) throws Exception {
		SituazioneAmministrativaFactory.newInstance(cdnLavoratore.toString(), dataCalcolo, transExec).calcolaImpatti();
	}

	private boolean inserisciColloquio(BigDecimal prgColloquio, String codServizio, String dataColloquio,
			BigDecimal userID, BigDecimal prgSpiCollegato) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio, cdnLavoratore, dataColloquio, codServizio, prgSpiCollegato,
				codCpi, noteColloquio, userID, userID };
		Boolean res = (Boolean) transExec.executeQuery("WS_RINNOVO_INSERISCI_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean inserisciSchedaColloquio(BigDecimal prgColloquio) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgColloquio };
		Boolean res = (Boolean) transExec.executeQuery("WS_RINNOVO_INSERISCI_SCHEDA_COLLOQUIO", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	private boolean inserisciAzione(BigDecimal prgPercorsoNew, BigDecimal prgcolloquio, String codEsito,
			String codEsitoRendicont, String dataRif, BigDecimal prgazioni, BigDecimal prgSpiCollegato,
			BigDecimal userID) throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgPercorsoNew, prgcolloquio, dataRif, dataRif, prgazioni, codEsito,
				codEsitoRendicont, dataRif, prgSpiCollegato, prgSpiCollegato, userID, userID, dataDichiarazione };
		Boolean res = (Boolean) transExec.executeQuery("INSERISCI_AZIONE_CONFERIMENTO_DID", params, "INSERT");
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	public DidBean getDidConf() {
		return this.didConf;
	}

	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public String getCodCpiMin() {
		return this.codCpiMin;
	}

	public String getCodRegioneMin() {
		return this.codRegioneMin;
	}

	private BigDecimal getProgressivoColloquio() throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) transExec.executeQuery("OR_COLLOQUIO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	private BigDecimal getProgressivoPercorso() throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) transExec.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("do_nextval");

		}
		return progressivo;
	}

	private BigDecimal getOperatoreCollegato(BigDecimal userId) throws Exception {
		BigDecimal prgSpi = null;
		Object[] params = new Object[] { userId };
		SourceBean row = (SourceBean) transExec.executeQuery("SEL_SPI_UTENTE", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgSpi = (BigDecimal) row.getAttribute("prgspi");

		}
		return prgSpi;
	}

	private BigDecimal cercaAzione(String tipoAttivita, String tipoProgetto) throws Exception {
		SourceBean azione = null;
		BigDecimal prgAzione = null;
		Object[] params = new Object[2];
		params[0] = tipoAttivita;
		params[1] = tipoProgetto;

		azione = (SourceBean) transExec.executeQuery("SELECT_AZIONE_CONFERIMENTO_DID", params, "SELECT");
		if (azione != null) {
			azione = (azione.containsAttribute("ROW") ? (SourceBean) azione.getAttribute("ROW") : azione);
			prgAzione = (BigDecimal) azione.getAttribute("PRGAZIONI");
		}
		return prgAzione;
	}

	private String getCodServizio(BigDecimal prgAzione) throws Exception {
		String codServizio = null;
		SourceBean row = null;
		Object[] params = new Object[1];
		params[0] = prgAzione;

		row = (SourceBean) transExec.executeQuery("SELECT_CODSERVIZIO_CONFERIMENTO_DID", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			codServizio = (String) row.getAttribute("CODSERVIZIO");
		}
		return codServizio;
	}

	public GestisciDID_Input getGestisciDIDWS(SourceBean serviceRequest, boolean isProfiling) {
		GestisciDID_Input conferimentoDid = new GestisciDID_Input();
		conferimentoDid.setGUIDOperatore(getCodRegioneMin());
		conferimentoDid.setCodiceFiscaleOperatore(null);
		conferimentoDid.setApplicazione(Applicazione.fromString("NCN"));
		conferimentoDid.setInformazioniDID(getInformazioniDID(serviceRequest));
		if (isProfiling) {
			conferimentoDid.setVariabiliDiProfiling(getVariabiliProfiling(serviceRequest));
		} else {
			conferimentoDid.setVariabiliDiProfiling(null);
		}

		try {
			inputConferimento = new GestisciDIDInput();
			inputConferimento.setGUIDOperatore(getCodRegioneMin());
			inputConferimento.setCodiceFiscaleOperatore(null);
			inputConferimento.setApplicazione(it.gov.mlps.types.Applicazione.NCN);
			inputConferimento.setInformazioniDID(getInformazioniDIDXml(serviceRequest));
			if (isProfiling) {
				inputConferimento.setVariabiliDiProfiling(getVariabiliProfilingXml(serviceRequest));
			} else {
				inputConferimento.setVariabiliDiProfiling(null);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in costruzione input conferimento xml da tracciare.",
					(Exception) e);
		}

		return conferimentoDid;
	}

	private InformazioniDID getInformazioniDID(SourceBean serviceRequest) {
		InformazioniDID informazioniDID = new InformazioniDID();
		informazioniDID.setTipoEvento(TipoEvento.fromValue(serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()));
		String dataDID = this.dataDichiarazione;
		Calendar calDID = Calendar.getInstance();
		int giornoCal = Integer.parseInt(dataDID.substring(0, 2));
		int meseCal = Integer.parseInt(dataDID.substring(3, 5));
		int annoCal = Integer.parseInt(dataDID.substring(6, 10));
		calDID.set(annoCal, meseCal - 1, giornoCal);
		informazioniDID.setDataDID(calDID);
		if (serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()
				.equalsIgnoreCase(ConferimentoUtility.EVENTOREVOCA)) {
			// data evento
			String dataEventoRevoca = getDataEventoRevoca(dataDID);
			Calendar calRevoca = Calendar.getInstance();
			int giornoOggi = Integer.parseInt(dataEventoRevoca.substring(0, 2));
			int meseOggi = Integer.parseInt(dataEventoRevoca.substring(3, 5));
			int annoOggi = Integer.parseInt(dataEventoRevoca.substring(6, 10));
			calRevoca.set(annoOggi, meseOggi - 1, giornoOggi);
			informazioniDID.setDataEvento(calRevoca);
			informazioniDID.setCodiceEntePromotore(getCodCpiMin());
			informazioniDID.setCodiceFiscale(new CodiceFiscale(this.codiceFiscale));
		} else if (serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()
				.equalsIgnoreCase(ConferimentoUtility.EVENTOCONVALIDA)) {
			if (StringUtils.isFilledNoBlank(this.dataAvvioA02A05)) {
				Calendar calAvvio = Calendar.getInstance();
				int giorno = Integer.parseInt(this.dataAvvioA02A05.substring(0, 2));
				int mese = Integer.parseInt(this.dataAvvioA02A05.substring(3, 5));
				int anno = Integer.parseInt(this.dataAvvioA02A05.substring(6, 10));
				calAvvio.set(anno, mese - 1, giorno);
				informazioniDID.setDataEvento(calAvvio);
			} else {
				informazioniDID.setDataEvento(calDID);
			}
			informazioniDID
					.setCodiceFiscale(new CodiceFiscale(serviceRequest.getAttribute("CODICEFISCALE").toString()));
			informazioniDID.setCodiceEntePromotore(serviceRequest.getAttribute("CODENTETIT").toString());
		} else {
			// data evento
			informazioniDID.setDataEvento(calDID);
			informazioniDID
					.setCodiceFiscale(new CodiceFiscale(serviceRequest.getAttribute("CODICEFISCALE").toString()));
			informazioniDID.setCodiceEntePromotore(serviceRequest.getAttribute("CODENTETIT").toString());
		}
		return informazioniDID;
	}

	private it.gov.mlps.types.InformazioniDID getInformazioniDIDXml(SourceBean serviceRequest) throws Exception {
		it.gov.mlps.types.InformazioniDID informazioniDID = new it.gov.mlps.types.InformazioniDID();
		informazioniDID.setTipoEvento(
				it.gov.mlps.types.TipoEvento.fromValue(serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()));
		String dataDID = this.dataDichiarazione;
		informazioniDID.setDataDID(datestringToXml(dataDID));
		if (serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()
				.equalsIgnoreCase(ConferimentoUtility.EVENTOREVOCA)) {
			// data evento
			String dataEventoRevoca = getDataEventoRevoca(dataDID);
			informazioniDID.setDataEvento(datestringToXml(dataEventoRevoca));
			informazioniDID.setCodiceEntePromotore(getCodCpiMin());
			informazioniDID.setCodiceFiscale(this.codiceFiscale);
		} else if (serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()
				.equalsIgnoreCase(ConferimentoUtility.EVENTOCONVALIDA)) {
			if (StringUtils.isFilledNoBlank(this.dataAvvioA02A05)) {
				informazioniDID.setDataEvento(datestringToXml(this.dataAvvioA02A05));
			} else {
				informazioniDID.setDataEvento(datestringToXml(dataDID));
			}
			informazioniDID.setCodiceFiscale(serviceRequest.getAttribute("CODICEFISCALE").toString());
			informazioniDID.setCodiceEntePromotore(serviceRequest.getAttribute("CODENTETIT").toString());
		} else {
			// data evento
			informazioniDID.setDataEvento(datestringToXml(dataDID));
			informazioniDID.setCodiceFiscale(serviceRequest.getAttribute("CODICEFISCALE").toString());
			informazioniDID.setCodiceEntePromotore(serviceRequest.getAttribute("CODENTETIT").toString());
		}
		return informazioniDID;
	}

	private VariabiliDiProfiling getVariabiliProfiling(SourceBean serviceRequest) {
		VariabiliDiProfiling profiling = new VariabiliDiProfiling();
		String strEta = serviceRequest.getAttribute("NUMETA").toString();
		Integer eta = Integer.parseInt(strEta);
		profiling.setEta(eta);
		profiling.setGenere(Genere.fromValue(serviceRequest.getAttribute("STRSESSO").toString()));
		profiling.setCittadinanza(serviceRequest.getAttribute("CODPFCITTADINANZA").toString());
		profiling.setDurataPresenzaInItalia(serviceRequest.getAttribute("CODPFPRESENZAIT").toString());
		profiling.setTitoloDiStudio(serviceRequest.getAttribute("codTitolo").toString());
		profiling.setProvinciaDiResidenza(serviceRequest.getAttribute("CODPROVINCIAMINRES").toString());
		String espLav = serviceRequest.getAttribute("FLGESPLAVORO").toString();
		boolean isEspLav = espLav.equals("S") ? true : false;
		profiling.setHaMaiAvutoUnLavoro(Boolean.valueOf(isEspLav));
		profiling.setCondizioneOccupazionaleUnAnnoPrima(serviceRequest.getAttribute("CODPFCONDOCCUP").toString());
		String mesiDisocc = serviceRequest.getAttribute("NUMMESIDISOCC").toString();
		Integer numMesiDisocc = Integer.parseInt(mesiDisocc);
		profiling.setDaQuantiMesiConclusoUltimoRappLavoro(numMesiDisocc);
		profiling
				.setPosizioneProfessioneUltimaOccupazione(serviceRequest.getAttribute("CODPFPOSIZIONEPROF").toString());
		String mesiRicercaLav = serviceRequest.getAttribute("NUMMESIRICERCALAV").toString();
		Integer numMesiRicercaLav = Integer.parseInt(mesiRicercaLav);
		profiling.setDaQuantiMesiStaCercandoLavoro(numMesiRicercaLav);
		profiling.setAttualmenteIscrittoScuolaUniversitaOCorsoFormazione(
				serviceRequest.getAttribute("CODPFISCRCORSO").toString());
		String nucleo = serviceRequest.getAttribute("NUMNUCLEOFAM").toString();
		Integer numNucleo = Integer.parseInt(nucleo);
		profiling.setNumeroComponentiFamiglia(numNucleo);
		String figliCarico = serviceRequest.getAttribute("FLGFIGLIACARICO").toString();
		boolean isFigliCarico = figliCarico.equals("S") ? true : false;
		profiling.setPresenzaFigliACarico(Boolean.valueOf(isFigliCarico));
		String figliMinorenni = serviceRequest.getAttribute("FLGFIGLIMINORENNI").toString();
		boolean isFigliMinorenni = figliMinorenni.equals("S") ? true : false;
		profiling.setPresenzaFigliACaricoMeno18Anni(Boolean.valueOf(isFigliMinorenni));
		profiling.setCondizioneOccupazionaleAnnoPrecedenteCalcolata(null);
		profiling.setDurataDisoccupazioneCalcolata(null);
		return profiling;
	}

	private it.gov.mlps.types.VariabiliDiProfiling getVariabiliProfilingXml(SourceBean serviceRequest) {
		it.gov.mlps.types.VariabiliDiProfiling profiling = new it.gov.mlps.types.VariabiliDiProfiling();
		String strEta = serviceRequest.getAttribute("NUMETA").toString();
		Integer eta = Integer.parseInt(strEta);
		profiling.setEta(eta);
		profiling.setGenere(it.gov.mlps.types.Genere.fromValue(serviceRequest.getAttribute("STRSESSO").toString()));
		profiling.setCittadinanza(serviceRequest.getAttribute("CODPFCITTADINANZA").toString());
		profiling.setDurataPresenzaInItalia(serviceRequest.getAttribute("CODPFPRESENZAIT").toString());
		profiling.setTitoloDiStudio(serviceRequest.getAttribute("codTitolo").toString());
		profiling.setProvinciaDiResidenza(serviceRequest.getAttribute("CODPROVINCIAMINRES").toString());
		String espLav = serviceRequest.getAttribute("FLGESPLAVORO").toString();
		boolean isEspLav = espLav.equals("S") ? true : false;
		profiling.setHaMaiAvutoUnLavoro(Boolean.valueOf(isEspLav));
		profiling.setCondizioneOccupazionaleUnAnnoPrima(serviceRequest.getAttribute("CODPFCONDOCCUP").toString());
		String mesiDisocc = serviceRequest.getAttribute("NUMMESIDISOCC").toString();
		Integer numMesiDisocc = Integer.parseInt(mesiDisocc);
		profiling.setDaQuantiMesiConclusoUltimoRappLavoro(numMesiDisocc);
		profiling
				.setPosizioneProfessioneUltimaOccupazione(serviceRequest.getAttribute("CODPFPOSIZIONEPROF").toString());
		String mesiRicercaLav = serviceRequest.getAttribute("NUMMESIRICERCALAV").toString();
		Integer numMesiRicercaLav = Integer.parseInt(mesiRicercaLav);
		profiling.setDaQuantiMesiStaCercandoLavoro(numMesiRicercaLav);
		profiling.setAttualmenteIscrittoScuolaUniversitaOCorsoFormazione(
				serviceRequest.getAttribute("CODPFISCRCORSO").toString());
		String nucleo = serviceRequest.getAttribute("NUMNUCLEOFAM").toString();
		Integer numNucleo = Integer.parseInt(nucleo);
		profiling.setNumeroComponentiFamiglia(numNucleo);
		String figliCarico = serviceRequest.getAttribute("FLGFIGLIACARICO").toString();
		boolean isFigliCarico = figliCarico.equals("S") ? true : false;
		profiling.setPresenzaFigliACarico(Boolean.valueOf(isFigliCarico));
		String figliMinorenni = serviceRequest.getAttribute("FLGFIGLIMINORENNI").toString();
		boolean isFigliMinorenni = figliMinorenni.equals("S") ? true : false;
		profiling.setPresenzaFigliACaricoMeno18Anni(Boolean.valueOf(isFigliMinorenni));
		profiling.setCondizioneOccupazionaleAnnoPrecedenteCalcolata(null);
		profiling.setDurataDisoccupazioneCalcolata(null);
		return profiling;
	}

	private XMLGregorianCalendar datestringToXml(String dateFromDB)
			throws ParseException, DatatypeConfigurationException {
		try {
			XMLGregorianCalendar xmlDateDB = null;
			if (dateFromDB != null && !"".equals(dateFromDB)) {
				DateFormat dfIn = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				xmlDateDB = DatatypeFactory.newInstance().newXMLGregorianCalendar(dfOut.format(dfIn.parse(dateFromDB)));
			}
			return xmlDateDB;
		} catch (ParseException epe) {
			throw epe;
		} catch (DatatypeConfigurationException edte) {
			throw edte;
		}
	}

	public MultipleTransactionQueryExecutor getTransExec() {
		return transExec;
	}

	public BigDecimal getPrgElencoAnagrafico() {
		return prgElencoAnagrafico;
	}

	public BigDecimal getPrgPercorsoA02() {
		return prgPercorsoA02;
	}

	public BigDecimal getPrgColloquioA02() {
		return prgColloquioA02;
	}

	public void setTransExec(MultipleTransactionQueryExecutor transExec) {
		this.transExec = transExec;
	}

	public static String getDescrizioneErroreMin(String codEsito) throws Exception {
		String descrizione = "";
		Object[] params = new Object[1];
		params[0] = codEsito;
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("SELECT_ESITO_CONFERIMENTO_DID", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			descrizione = row.containsAttribute("strdescrizione") ? row.getAttribute("strdescrizione").toString() : "";
		}
		return descrizione;
	}

	public static BigDecimal getProgressivoConferimento() throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_NEW_PRGCONFERIMENTODID", null, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("PRGCONFERIMENTODID");

		}
		return progressivo;
	}

	public static BigDecimal getProgressivoTracciamentoConferimento() throws Exception {
		BigDecimal progressivo = null;
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_NEW_TRACCIAMENTO_PRGCONFDID", null, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			progressivo = (BigDecimal) row.getAttribute("newKey");

		}
		return progressivo;
	}

	public static boolean inserisciTracciamentoConferimento(BigDecimal prgTracciamento, BigDecimal cdnLav,
			BigDecimal prgConferimento, GestisciDIDInput requestXml, String codEsito, GestisciDIDOutput responseXml)
			throws Exception {
		boolean isOK = false;
		Object[] params = new Object[] { prgTracciamento, cdnLav, prgConferimento,
				converConferimentoInputToString(requestXml), codEsito, converConferimentoOtputToString(responseXml) };
		Boolean res = (Boolean) QueryExecutor.executeQuery("CDD_INSERT_TRACCIAMENTO_CONF_DID", params, "INSERT",
				Values.DB_SIL_DATI);
		if (res != null && res.booleanValue()) {
			isOK = true;
		}
		return isOK;
	}

	public GestisciDIDInput getInputConferimento() {
		return this.inputConferimento;
	}

	public GestisciDIDOutput getOutputConferimento() {
		return this.outputConferimento;
	}

	public void svuotaCampi(SourceBean serviceRequest) throws SourceBeanException {
		if (serviceRequest.containsAttribute("NUMETA")) {
			serviceRequest.delAttribute("NUMETA");
		}
		if (serviceRequest.containsAttribute("STRSESSO")) {
			serviceRequest.delAttribute("STRSESSO");
		}
		if (serviceRequest.containsAttribute("CODPFCITTADINANZA")) {
			serviceRequest.delAttribute("CODPFCITTADINANZA");
		}
		if (serviceRequest.containsAttribute("codTitolo")) {
			serviceRequest.delAttribute("codTitolo");
		}
		if (serviceRequest.containsAttribute("CODPFCONDOCCUP")) {
			serviceRequest.delAttribute("CODPFCONDOCCUP");
		}
		if (serviceRequest.containsAttribute("NUMMESIDISOCC")) {
			serviceRequest.delAttribute("NUMMESIDISOCC");
		}
		if (serviceRequest.containsAttribute("NUMMESIRICERCALAV")) {
			serviceRequest.delAttribute("NUMMESIRICERCALAV");
		}
		if (serviceRequest.containsAttribute("CODPFISCRCORSO")) {
			serviceRequest.delAttribute("CODPFISCRCORSO");
		}
		if (serviceRequest.containsAttribute("CODPROVINCIARES")) {
			serviceRequest.delAttribute("CODPROVINCIARES");
		}
		if (serviceRequest.containsAttribute("CODPFPRESENZAIT")) {
			serviceRequest.delAttribute("CODPFPRESENZAIT");
		}
		if (serviceRequest.containsAttribute("FLGESPLAVORO")) {
			serviceRequest.delAttribute("FLGESPLAVORO");
		}
		if (serviceRequest.containsAttribute("CODPFPOSIZIONEPROF")) {
			serviceRequest.delAttribute("CODPFPOSIZIONEPROF");
		}
		if (serviceRequest.containsAttribute("NUMNUCLEOFAM")) {
			serviceRequest.delAttribute("NUMNUCLEOFAM");
		}
		if (serviceRequest.containsAttribute("FLGFIGLIACARICO")) {
			serviceRequest.delAttribute("FLGFIGLIACARICO");
		}
		if (serviceRequest.containsAttribute("FLGFIGLIMINORENNI")) {
			serviceRequest.delAttribute("FLGFIGLIMINORENNI");
		}
	}

	private it.gov.mlps.types.InformazioniDID getInformazioniDIDOutXml(GestisciDID_Output didOutput) throws Exception {
		it.gov.mlps.types.InformazioniDID informazioniDID = new it.gov.mlps.types.InformazioniDID();
		if (didOutput.getInformazioniDID() != null) {
			informazioniDID.setCodiceEntePromotore(didOutput.getInformazioniDID().getCodiceEntePromotore());
			informazioniDID.setCodiceFiscale(
					didOutput.getInformazioniDID().getCodiceFiscale().getCodiceFiscaleClassicoValue());
			informazioniDID.setTipoEvento(
					it.gov.mlps.types.TipoEvento.fromValue(didOutput.getInformazioniDID().getTipoEvento().getValue()));
			Calendar cal = didOutput.getInformazioniDID().getDataDID();
			Date date = cal.getTime();
			SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
			String dateDid = format1.format(date);
			Calendar calEv = didOutput.getInformazioniDID().getDataEvento();
			Date dateEv = calEv.getTime();
			SimpleDateFormat formatEv = new SimpleDateFormat("dd/MM/yyyy");
			String dateEvento = formatEv.format(dateEv);
			informazioniDID.setDataDID(datestringToXml(dateDid));
			informazioniDID.setDataEvento(datestringToXml(dateEvento));
		}
		return informazioniDID;
	}

	private JAXBElement<it.gov.mlps.types.Profiling> getVariabiliProfilingOutXml(GestisciDID_Output didOutput)
			throws Exception {
		it.gov.mlps.types.Profiling profiling = new it.gov.mlps.types.Profiling();

		if (didOutput.getProfiling() != null) {
			profiling.setEta(new Integer(didOutput.getProfiling().getEta()));
			profiling.setGenere(it.gov.mlps.types.Genere.fromValue(didOutput.getProfiling().getGenere().getValue()));
			profiling.setCittadinanza(didOutput.getProfiling().getCittadinanza());
			profiling.setDurataPresenzaInItalia(didOutput.getProfiling().getDurataPresenzaInItalia());
			profiling.setTitoloDiStudio(didOutput.getProfiling().getTitoloDiStudio());
			profiling.setProvinciaDiResidenza(didOutput.getProfiling().getProvinciaDiResidenza());
			profiling.setDurataDisoccupazioneCalcolata(didOutput.getProfiling().getDurataDisoccupazioneCalcolata());
			profiling.setAttualmenteIscrittoScuolaUniversitaOCorsoFormazione(
					didOutput.getProfiling().getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione());
			profiling.setCondizioneOccupazionaleAnnoPrecedenteCalcolata(
					didOutput.getProfiling().getCondizioneOccupazionaleAnnoPrecedenteCalcolata());
			profiling.setCondizioneProfessionaleAnnoPrecedente(
					didOutput.getProfiling().getCondizioneProfessionaleAnnoPrecedente());
			Calendar cal = didOutput.getProfiling().getDataInserimento();
			Date date = cal.getTime();
			SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
			String dateIns = format1.format(date);
			profiling.setDataInserimento(datestringToXml(dateIns));
			profiling.setDurataDellaDisoccupazione(didOutput.getProfiling().getDurataDellaDisoccupazione());
			profiling.setDurataPresenzaInItalia(didOutput.getProfiling().getDurataPresenzaInItalia());
			profiling.setDurataRicercaLavoro(didOutput.getProfiling().getDurataRicercaLavoro());
			profiling.setHaLavoratoAlmenoUnaVolta(new Boolean(didOutput.getProfiling().isHaLavoratoAlmenoUnaVolta()));
			profiling.setIDSProfiling(didOutput.getProfiling().getIDSProfiling());
			profiling.setNumeroComponentiFamiglia(new Integer(didOutput.getProfiling().getNumeroComponentiFamiglia()));
			profiling.setPosizioneUltimaOccupazione(didOutput.getProfiling().getPosizioneUltimaOccupazione());
			profiling.setPresenzaFigliMinoriACarico(didOutput.getProfiling().getPresenzaFigliMinoriACarico());
			profiling.setProbabilita(didOutput.getProfiling().getProbabilita());
			profiling.setRegioneDiResidenza(didOutput.getProfiling().getRegioneDiResidenza());
			profiling.setPresenzaFigliACarico(didOutput.getProfiling().isPresenzaFigliACarico());
		}
		return new JAXBElement(
				new QName("http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Profiling"),
				profiling.getClass(), profiling);
	}

	public boolean getGestisciOutputWS(GestisciDID_Output didOutput) {
		try {
			outputConferimento = new GestisciDIDOutput();

			outputConferimento.setEsito(didOutput.getEsito());
			outputConferimento.setInformazioniDID(getInformazioniDIDOutXml(didOutput));
			outputConferimento.setProfiling(getVariabiliProfilingOutXml(didOutput));

			return true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in costruzione output conferimento xml da tracciare.",
					(Exception) e);
			return false;
		}
	}

	private static String converConferimentoInputToString(GestisciDIDInput input) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(GestisciDIDInput.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(input, writer);
		String xmlInput = writer.getBuffer().toString();
		return xmlInput;
	}

	private static String converConferimentoOtputToString(GestisciDIDOutput output) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(GestisciDIDOutput.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(output, writer);
		String xmlInput = writer.getBuffer().toString();
		return xmlInput;
	}

	private String getDataEventoRevoca(String dataDID) {
		try {
			Object params[] = new Object[3];
			params[0] = cdnLavoratore;
			params[1] = dataDID;
			params[2] = dataDID;
			SourceBean row = null;
			if (transExec == null) {
				row = (SourceBean) QueryExecutor.executeQuery("SELECT_DATA_REVOCA_A02_05", params, "SELECT",
						Values.DB_SIL_DATI);
			} else {
				row = (SourceBean) transExec.executeQuery("SELECT_DATA_REVOCA_A02_05", params, "SELECT");
			}
			if (row != null) {
				Vector risultati = row.getAttributeAsVector("ROW");
				if (risultati != null && risultati.size() == 1) {
					SourceBean dataSB = (SourceBean) risultati.get(0);
					dataSB = dataSB.containsAttribute("ROW") ? (SourceBean) dataSB.getAttribute("ROW") : dataSB;
					String dataFineEvento = (String) dataSB.getAttribute("dataFineAzione");
					if (StringUtils.isFilledNoBlank(dataFineEvento)) {
						return dataFineEvento;
					} else {
						return DateUtils.getNow();
					}

				} else {
					return DateUtils.getNow();
				}
			} else {
				return DateUtils.getNow();
			}
		} catch (EMFInternalError e) {
			_logger.error(e);
			return DateUtils.getNow();
		}
	}

	private BigDecimal getColloquioPresaInCarico(String dataRif) throws Exception {
		BigDecimal prgColloquio = null;
		if (dataRif == null) {
			dataRif = dataDichiarazione;
		}
		Object[] params = new Object[] { cdnLavoratore, dataRif };
		SourceBean row = (SourceBean) transExec.executeQuery("GET_COLLOQUIO_CONFERIMENTO_DID", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgColloquio = (BigDecimal) row.getAttribute("prgcolloquio");
		}
		return prgColloquio;
	}

	private Object[] getColloquioSEPPresaInCarico(String codServizioColloquio) throws Exception {
		Object[] vettColloquioSEP = new Object[2];
		vettColloquioSEP[0] = null; // progressivo colloquio
		vettColloquioSEP[1] = null; // data colloquio
		BigDecimal prgColloquio = null;
		String dataColloquioSEP = null;
		Object[] params = null;
		params = new Object[] { cdnLavoratore, codServizioColloquio };
		SourceBean row = (SourceBean) transExec.executeQuery("GET_COLLOQUIO_SERVIZIO_CONFERIMENTO_DID", params,
				"SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgColloquio = (BigDecimal) row.getAttribute("prgcolloquio");
			dataColloquioSEP = (String) row.getAttribute("datacolloquio");
			vettColloquioSEP[0] = prgColloquio;
			vettColloquioSEP[1] = dataColloquioSEP;

		}
		return vettColloquioSEP;
	}

	public it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input getGestisciDIDWS_Anpal(
			SourceBean serviceRequest, boolean isProfiling) {
		it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input conferimentoDid = new it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input();
		conferimentoDid.setGUIDOperatore(getCodRegioneMin());
		conferimentoDid.setCodiceFiscaleOperatore(null);
		conferimentoDid.setApplicazione(
				it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.Applicazione.fromString("NCN"));
		conferimentoDid.setInformazioniDID(getInformazioniDID_Anpal(serviceRequest));
		if (isProfiling) {
			conferimentoDid.setVariabiliDiProfiling(getVariabiliProfiling_Anpal(serviceRequest));
		} else {
			conferimentoDid.setVariabiliDiProfiling(null);
		}

		try {
			inputConferimento = new GestisciDIDInput();
			inputConferimento.setGUIDOperatore(getCodRegioneMin());
			inputConferimento.setCodiceFiscaleOperatore(null);
			inputConferimento.setApplicazione(it.gov.mlps.types.Applicazione.NCN);
			inputConferimento.setInformazioniDID(getInformazioniDIDXml(serviceRequest));
			if (isProfiling) {
				inputConferimento.setVariabiliDiProfiling(getVariabiliProfilingXml(serviceRequest));
			} else {
				inputConferimento.setVariabiliDiProfiling(null);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in costruzione input conferimento xml da tracciare.",
					(Exception) e);
		}

		return conferimentoDid;
	}

	private it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID getInformazioniDID_Anpal(
			SourceBean serviceRequest) {
		it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID informazioniDID = new it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID();
		informazioniDID.setTipoEvento(it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento
				.fromValue(serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()));
		String dataDID = this.dataDichiarazione;
		Calendar calDID = Calendar.getInstance();
		int giornoCal = Integer.parseInt(dataDID.substring(0, 2));
		int meseCal = Integer.parseInt(dataDID.substring(3, 5));
		int annoCal = Integer.parseInt(dataDID.substring(6, 10));
		calDID.set(annoCal, meseCal - 1, giornoCal);
		informazioniDID.setDataDID(calDID);
		if (serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()
				.equalsIgnoreCase(ConferimentoUtility.EVENTOREVOCA)) {
			// data evento
			String dataEventoRevoca = getDataEventoRevoca(dataDID);
			Calendar calRevoca = Calendar.getInstance();
			int giornoOggi = Integer.parseInt(dataEventoRevoca.substring(0, 2));
			int meseOggi = Integer.parseInt(dataEventoRevoca.substring(3, 5));
			int annoOggi = Integer.parseInt(dataEventoRevoca.substring(6, 10));
			calRevoca.set(annoOggi, meseOggi - 1, giornoOggi);
			informazioniDID.setDataEvento(calRevoca);
			informazioniDID.setCodiceEntePromotore(getCodCpiMin());
			informazioniDID.setCodiceFiscale(
					new it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale(
							this.codiceFiscale));
		} else if (serviceRequest.getAttribute("CODPFTIPOEVENTO").toString()
				.equalsIgnoreCase(ConferimentoUtility.EVENTOCONVALIDA)) {
			if (StringUtils.isFilledNoBlank(this.dataAvvioA02A05)) {
				Calendar calAvvio = Calendar.getInstance();
				int giorno = Integer.parseInt(this.dataAvvioA02A05.substring(0, 2));
				int mese = Integer.parseInt(this.dataAvvioA02A05.substring(3, 5));
				int anno = Integer.parseInt(this.dataAvvioA02A05.substring(6, 10));
				calAvvio.set(anno, mese - 1, giorno);
				informazioniDID.setDataEvento(calAvvio);
			} else {
				informazioniDID.setDataEvento(calDID);
			}
			informazioniDID.setCodiceFiscale(
					new it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale(
							serviceRequest.getAttribute("CODICEFISCALE").toString()));
			informazioniDID.setCodiceEntePromotore(serviceRequest.getAttribute("CODENTETIT").toString());
		} else {
			// data evento
			informazioniDID.setDataEvento(calDID);
			informazioniDID.setCodiceFiscale(
					new it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale(
							serviceRequest.getAttribute("CODICEFISCALE").toString()));
			informazioniDID.setCodiceEntePromotore(serviceRequest.getAttribute("CODENTETIT").toString());
		}
		return informazioniDID;
	}

	private it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling getVariabiliProfiling_Anpal(
			SourceBean serviceRequest) {
		it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling profiling = new it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling();
		String strEta = serviceRequest.getAttribute("NUMETA").toString();
		Integer eta = Integer.parseInt(strEta);
		profiling.setEta(eta);
		profiling.setGenere(it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.Genere
				.fromValue(serviceRequest.getAttribute("STRSESSO").toString()));
		profiling.setCittadinanza(serviceRequest.getAttribute("CODPFCITTADINANZA").toString());
		profiling.setDurataPresenzaInItalia(serviceRequest.getAttribute("CODPFPRESENZAIT").toString());
		profiling.setTitoloDiStudio(serviceRequest.getAttribute("codTitolo").toString());
		profiling.setProvinciaDiResidenza(serviceRequest.getAttribute("CODPROVINCIAMINRES").toString());
		String espLav = serviceRequest.getAttribute("FLGESPLAVORO").toString();
		boolean isEspLav = espLav.equals("S") ? true : false;
		profiling.setHaMaiAvutoUnLavoro(Boolean.valueOf(isEspLav));
		profiling.setCondizioneOccupazionaleUnAnnoPrima(serviceRequest.getAttribute("CODPFCONDOCCUP").toString());
		String mesiDisocc = serviceRequest.getAttribute("NUMMESIDISOCC").toString();
		Integer numMesiDisocc = Integer.parseInt(mesiDisocc);
		profiling.setDaQuantiMesiConclusoUltimoRappLavoro(numMesiDisocc);
		profiling
				.setPosizioneProfessioneUltimaOccupazione(serviceRequest.getAttribute("CODPFPOSIZIONEPROF").toString());
		String mesiRicercaLav = serviceRequest.getAttribute("NUMMESIRICERCALAV").toString();
		Integer numMesiRicercaLav = Integer.parseInt(mesiRicercaLav);
		profiling.setDaQuantiMesiStaCercandoLavoro(numMesiRicercaLav);
		profiling.setAttualmenteIscrittoScuolaUniversitaOCorsoFormazione(
				serviceRequest.getAttribute("CODPFISCRCORSO").toString());
		String nucleo = serviceRequest.getAttribute("NUMNUCLEOFAM").toString();
		Integer numNucleo = Integer.parseInt(nucleo);
		profiling.setNumeroComponentiFamiglia(numNucleo);
		String figliCarico = serviceRequest.getAttribute("FLGFIGLIACARICO").toString();
		boolean isFigliCarico = figliCarico.equals("S") ? true : false;
		profiling.setPresenzaFigliACarico(Boolean.valueOf(isFigliCarico));
		String figliMinorenni = serviceRequest.getAttribute("FLGFIGLIMINORENNI").toString();
		boolean isFigliMinorenni = figliMinorenni.equals("S") ? true : false;
		profiling.setPresenzaFigliACaricoMeno18Anni(Boolean.valueOf(isFigliMinorenni));
		profiling.setCondizioneOccupazionaleAnnoPrecedenteCalcolata(null);
		profiling.setDurataDisoccupazioneCalcolata(null);
		return profiling;
	}

	public boolean getGestisciOutputWS(
			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output didOutput) {
		try {
			outputConferimento = new GestisciDIDOutput();

			outputConferimento.setEsito(didOutput.getEsito());
			outputConferimento.setInformazioniDID(getInformazioniDIDOutXml(didOutput));
			outputConferimento.setProfiling(getVariabiliProfilingOutXml(didOutput));

			return true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in costruzione output conferimento xml da tracciare.",
					(Exception) e);
			return false;
		}
	}

	private it.gov.mlps.types.InformazioniDID getInformazioniDIDOutXml(
			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output didOutput)
			throws Exception {
		it.gov.mlps.types.InformazioniDID informazioniDID = new it.gov.mlps.types.InformazioniDID();
		if (didOutput.getInformazioniDID() != null) {
			informazioniDID.setCodiceEntePromotore(didOutput.getInformazioniDID().getCodiceEntePromotore());
			informazioniDID.setCodiceFiscale(
					didOutput.getInformazioniDID().getCodiceFiscale().getCodiceFiscaleClassicoValue());
			informazioniDID.setTipoEvento(
					it.gov.mlps.types.TipoEvento.fromValue(didOutput.getInformazioniDID().getTipoEvento().getValue()));
			Calendar cal = didOutput.getInformazioniDID().getDataDID();
			Date date = cal.getTime();
			SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
			String dateDid = format1.format(date);
			Calendar calEv = didOutput.getInformazioniDID().getDataEvento();
			Date dateEv = calEv.getTime();
			SimpleDateFormat formatEv = new SimpleDateFormat("dd/MM/yyyy");
			String dateEvento = formatEv.format(dateEv);
			informazioniDID.setDataDID(datestringToXml(dateDid));
			informazioniDID.setDataEvento(datestringToXml(dateEvento));
		}
		return informazioniDID;
	}

	private JAXBElement<it.gov.mlps.types.Profiling> getVariabiliProfilingOutXml(
			it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output didOutput)
			throws Exception {
		it.gov.mlps.types.Profiling profiling = new it.gov.mlps.types.Profiling();

		if (didOutput.getProfiling() != null) {
			profiling.setEta(new Integer(didOutput.getProfiling().getEta()));
			profiling.setGenere(it.gov.mlps.types.Genere.fromValue(didOutput.getProfiling().getGenere().getValue()));
			profiling.setCittadinanza(didOutput.getProfiling().getCittadinanza());
			profiling.setDurataPresenzaInItalia(didOutput.getProfiling().getDurataPresenzaInItalia());
			profiling.setTitoloDiStudio(didOutput.getProfiling().getTitoloDiStudio());
			profiling.setProvinciaDiResidenza(didOutput.getProfiling().getProvinciaDiResidenza());
			profiling.setDurataDisoccupazioneCalcolata(didOutput.getProfiling().getDurataDisoccupazioneCalcolata());
			profiling.setAttualmenteIscrittoScuolaUniversitaOCorsoFormazione(
					didOutput.getProfiling().getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione());
			profiling.setCondizioneOccupazionaleAnnoPrecedenteCalcolata(
					didOutput.getProfiling().getCondizioneOccupazionaleAnnoPrecedenteCalcolata());
			profiling.setCondizioneProfessionaleAnnoPrecedente(
					didOutput.getProfiling().getCondizioneProfessionaleAnnoPrecedente());
			Calendar cal = didOutput.getProfiling().getDataInserimento();
			Date date = cal.getTime();
			SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
			String dateIns = format1.format(date);
			profiling.setDataInserimento(datestringToXml(dateIns));
			profiling.setDurataDellaDisoccupazione(didOutput.getProfiling().getDurataDellaDisoccupazione());
			profiling.setDurataPresenzaInItalia(didOutput.getProfiling().getDurataPresenzaInItalia());
			profiling.setDurataRicercaLavoro(didOutput.getProfiling().getDurataRicercaLavoro());
			profiling.setHaLavoratoAlmenoUnaVolta(new Boolean(didOutput.getProfiling().isHaLavoratoAlmenoUnaVolta()));
			profiling.setIDSProfiling(didOutput.getProfiling().getIDSProfiling());
			profiling.setNumeroComponentiFamiglia(new Integer(didOutput.getProfiling().getNumeroComponentiFamiglia()));
			profiling.setPosizioneUltimaOccupazione(didOutput.getProfiling().getPosizioneUltimaOccupazione());
			profiling.setPresenzaFigliMinoriACarico(didOutput.getProfiling().getPresenzaFigliMinoriACarico());
			profiling.setProbabilita(didOutput.getProfiling().getProbabilita());
			profiling.setRegioneDiResidenza(didOutput.getProfiling().getRegioneDiResidenza());
			profiling.setPresenzaFigliACarico(didOutput.getProfiling().isPresenzaFigliACarico());
		}
		return new JAXBElement(
				new QName("http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Profiling"),
				profiling.getClass(), profiling);
	}
}
