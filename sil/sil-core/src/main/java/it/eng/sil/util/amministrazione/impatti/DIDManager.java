package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.patto.bean.Patto;
import it.eng.sil.util.Utils;

public class DIDManager {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DIDManager.class.getName());

	/**
	 * aggiorna lo stato occupazionale dall'inserimento della dichiarazione di immediata disponibilita'
	 * 
	 * @return List: L'elemento alla posizione 0 è lo stato occupazionale al momento della stipula did L'elemento alla
	 *         posizione 1 è lo stato occupazionale attuale del lavoratore
	 */
	public static List aggiornaDaDID(SourceBean request, RequestContainer requestContainer, SourceBean response,
			TransactionQueryExecutor transExec) throws ControlliException, Exception {

		List statiOccupazionaliReturn = new ArrayList(2);
		Vector vettParametriRicostruzionePerTir = null;
		StatoOccupazionaleManager2 statoOccManager = null;
		// prima debbo controllare lo stato occupazionale precedente la data dichiarazione
		// nonche' se la data dichiarazione e' precedente la data di sistema, ovvero se sto' inserendo una
		// dichiarazione vecchia
		StatoOccupazionaleBean statoOccupazionale = null;
		String dataRif = (String) request.getAttribute("datDichiarazione");
		int anno = DateUtils.getAnno(dataRif);
		String dataFine = "31/12/" + anno;

		String cdnLavoratore = Utils.notNull(request.getAttribute("cdnLavoratore"));
		String dataAttuale = DateUtils.getNow();
		int prgDichDisponibilita = Integer.parseInt((String) request.getAttribute("prgDichDisp"));
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		// recupero eventuale lista Mobilita relative al lavoratore
		ListaMobilita listaMobilita = new ListaMobilita(cdnLavoratore, transExec);
		List rowsMobilita = listaMobilita.getMobilita();

		if (DateUtils.compare(dataRif, dataAttuale) < 0) {
			// dataDichiarazione did precedente alla data odierna
			if (!dichiarazioneAntecedentePossibile(cdnLavoratore, dataRif, transExec))
				throw new ControlliException(MessageCodes.DID.ESISTE_DID_FUTURA);
			StatoOccupazionaleBean statoOccupazionaleAperto = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);
			String dataStatoOcc = statoOccupazionaleAperto.getDataInizio();
			// se lo stato occupazionale non esiste significa che non ha senso ricostruire la storia
			// se dataStatoOcc == null allora lo stato occupazionale non esiste
			boolean ricostruzioneStoria = false;
			if (dataStatoOcc != null && !dataStatoOcc.equals("") && DateUtils.compare(dataRif, dataStatoOcc) < 0) {
				ricostruzioneStoria = true;
			}
			if (!ricostruzioneStoria) {
				SourceBean ultimoMovimento = DBLoad.getUltimoMovimento(cdnLavoratore, transExec);
				String dataInizioUltimoMov = (String) ultimoMovimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				if (dataInizioUltimoMov != null && !dataInizioUltimoMov.equals("")
						&& DateUtils.compare(dataRif, dataInizioUltimoMov) < 0) {
					ricostruzioneStoria = true;
				}
			}
			Vector movimentiLavoratore = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
			movimentiLavoratore = Controlli.togliMovNonProtocollati(movimentiLavoratore);
			Vector movApertiDa = getMovimentiApertiDa(movimentiLavoratore, dataRif);
			if (movApertiDa != null && movApertiDa.size() > 0) {
				// 26/04/2005 forzatura per effettuare la ricostruzione storia
				ricostruzioneStoria = true;
			}
			if (ricostruzioneStoria) {
				// ricrea la situazione amministrativa
				String dataInizio = dataRif;
				Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, /* dataInizio, */
						transExec);
				Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, dataInizio, transExec);
				Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, dataInizio, transExec);
				movimentiLavoratore = Controlli.togliMovimentoInDataFutura(movimentiLavoratore);

				SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiLavoratore, statiOccupazionali,
						patti, dids, dataInizio, transExec, requestContainer);

				List statiOcc = sitAmm.getStatiOccupazionali();
				ListaStatiOccupazionali lso = new ListaStatiOccupazionali(dataRif, cdnLavoratore, statiOcc,
						sitAmm.getListaMobilita(), transExec);
				if (_logger.isDebugEnabled()) {
					_logger.debug(lso.toString());
				}
				// deassocio lo stato occupazionale della did inserita precedentemente (prima fase di inserimento)
				sitAmm.resettaStatiOccupazionali(lso);
				StatoOccupazionaleBean soIniziale = lso.getStatoOccupazionaleIniziale();
				// calcola nuovo stato a partire da soIniziale
				movApertiDa = sitAmm.estraiMovimentiAnno(dataRif);
				nuovoStatoOccupazionale = inserisci(soIniziale, request, requestContainer, response, movApertiDa,
						sitAmm.getMovimenti(), "DID", sitAmm.getListaMobilita(), vettParametriRicostruzionePerTir,
						statoOccManager, sitAmm.getTipoCongif_MOV_C03(), sitAmm.getData150(),
						sitAmm.getListaDisabiliCM(), transExec);
				StatoOccupazionaleBean nuovoStatoOcc = new StatoOccupazionaleBean(nuovoStatoOccupazionale, null);
				for (int i = 0; i < sitAmm.getDids().size(); i++) {
					SourceBean did = (SourceBean) sitAmm.getDids().get(i);
					BigDecimal prgDich = (BigDecimal) did.getAttribute("prgDichDisponibilita");
					if (prgDich.intValue() == prgDichDisponibilita) {
						did.updAttribute("codStatoAtto", "PR");
						break;
					}
				}
				// si assume che se nello stesso giorno della dichiarazione della did sono stati inseriti movimenti
				// la did precedera' comunque la registrazione dei movimenti medesimi
				sitAmm.ricrea(request, nuovoStatoOcc, false);
				StatoOccupazionaleBean nuovoStatoOccupazionaleDid = null;
				Vector statiOccFinali = sitAmm.getStatiOccFinali();
				if (statiOccFinali != null && statiOccFinali.size() > 0) {
					String dataInizioSocc = "";
					for (int i = 0; i < statiOccFinali.size(); i++) {
						StatoOccupazionaleBean nuovoStatoOccupazionaleApp = new StatoOccupazionaleBean(
								(SourceBean) statiOccFinali.get(i));
						dataInizioSocc = nuovoStatoOccupazionaleApp.getDataInizio();
						if (!dataInizioSocc.equals("") && DateUtils.compare(dataInizioSocc, dataRif) > 0) {
							break;
						} else {
							nuovoStatoOccupazionaleDid = nuovoStatoOccupazionaleApp;
						}
					}
				}
				if (nuovoStatoOccupazionaleDid == null)
					nuovoStatoOccupazionaleDid = nuovoStatoOccupazionale;
				statiOccupazionaliReturn.add(0, nuovoStatoOccupazionaleDid);

				if (request.containsAttribute("RICOSTRUZIONE_STORIA_ESEGUITA")) {
					statiOccupazionaliReturn.add(1, sitAmm.getStatoOccupazionaleAperto());
				} else {
					statiOccupazionaliReturn.add(1, nuovoStatoOccupazionaleDid);
					if (sitAmm.getStatiOccupazionaliManuali() != null
							&& sitAmm.getStatiOccupazionaliManuali().size() > 0) {
						String dataStatoOccDid = nuovoStatoOccupazionaleDid.getDataInizio();
						int numSOccManuali = sitAmm.getStatiOccupazionaliManuali().size();
						for (int k = 0; k < numSOccManuali; k++) {
							StatoOccupazionaleBean statoOccManualeCurr = sitAmm.getStatoOccupazionaliManuali(k);
							String dataStatoOccupazionaleManuale = statoOccManualeCurr.getDataInizio();
							if (DateUtils.compare(dataStatoOccupazionaleManuale, dataStatoOccDid) >= 0) {
								StatoOccupazionaleBean nuovoStatoOccBean = sitAmm.inserisciStatoOccupazionaleManuale(
										nuovoStatoOccupazionaleDid, statoOccManualeCurr, new BigDecimal(cdnLavoratore),
										dataStatoOccupazionaleManuale);

							}
						}
					}
				}
			} else {
				UtilsConfig utility = new UtilsConfig("MOV_C03");
				String tipoConfig = utility.getConfigurazioneDefault_Custom();
				SourceBean sbGenerale = DBLoad.getInfoGenerali();
				String data150 = sbGenerale.getAttribute("DAT150") != null
						? sbGenerale.getAttribute("DAT150").toString()
						: null;
				if (statoOccupazionaleAperto.getProgressivoDB() == null) {
					statoOccupazionaleAperto = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(request);
				}
				Vector vettCM = DBLoad.getAllDisabiliCollocamentoMirato(cdnLavoratore, transExec);
				nuovoStatoOccupazionale = inserisci(statoOccupazionaleAperto, request, requestContainer, response, null,
						null, "DID", rowsMobilita, vettParametriRicostruzionePerTir, statoOccManager, tipoConfig,
						data150, vettCM, transExec);
				if (nuovoStatoOccupazionale != null) {
					if (nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
							|| nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
						// eventuale chiusura accordo generico
						Patto ptAccordo = new Patto();
						ptAccordo.controllaChiusuraAccordoGenerico(cdnLavoratore, requestContainer, transExec);
					} else {
						// eventuale riapertura accordo generico
						Patto ptAccordo = new Patto();
						ptAccordo.controllaAperturaAccordoGenerico(cdnLavoratore, requestContainer, transExec);
					}
				}
				statiOccupazionaliReturn.add(0, nuovoStatoOccupazionale);
				statiOccupazionaliReturn.add(1, nuovoStatoOccupazionale);
			}
		} else {
			try {
				statoOccupazionale = DBLoad.getStatoOccupazionale(request.getAttribute("cdnLavoratore"), transExec);
			} catch (Exception e) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("lo stato occupazionale non esiste?", e);
				}
			}
			UtilsConfig utility = new UtilsConfig("MOV_C03");
			String tipoConfig = utility.getConfigurazioneDefault_Custom();
			SourceBean sbGenerale = DBLoad.getInfoGenerali();
			String data150 = sbGenerale.getAttribute("DAT150") != null ? sbGenerale.getAttribute("DAT150").toString()
					: null;
			if (statoOccupazionale == null || statoOccupazionale.getProgressivoDB() == null) {
				statoOccupazionale = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(request);
			}
			Vector vettCM = DBLoad.getAllDisabiliCollocamentoMirato(cdnLavoratore, transExec);
			nuovoStatoOccupazionale = inserisci(statoOccupazionale, request, requestContainer, response, null, null,
					"DID", rowsMobilita, vettParametriRicostruzionePerTir, statoOccManager, tipoConfig, data150, vettCM,
					transExec);
			if (nuovoStatoOccupazionale != null) {
				if (nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
						|| nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
					// eventuale chiusura accordo generico
					Patto ptAccordo = new Patto();
					ptAccordo.controllaChiusuraAccordoGenerico(cdnLavoratore, requestContainer, transExec);
				} else {
					// eventuale riapertura accordo generico
					Patto ptAccordo = new Patto();
					ptAccordo.controllaAperturaAccordoGenerico(cdnLavoratore, requestContainer, transExec);
				}
			}
			statiOccupazionaliReturn.add(0, nuovoStatoOccupazionale);
			statiOccupazionaliReturn.add(1, nuovoStatoOccupazionale);
		}
		if (false)
			throw new Exception("annullamento operazione per test");
		return (statiOccupazionaliReturn);
	}

	/**
	 * 
	 * @param statoOccupazionale
	 * @param request
	 * @param requestContainer
	 * @param response
	 * @param rows
	 * @param tuttiIMov
	 * @param provenienza
	 * @param listaMobilita
	 * @param vettParametriRicostruzione
	 *            (tale parametro è null quando il metodo viene invocato dalla did, mentre è valorizzato quando è
	 *            invocato da mobilità sospesa)
	 * @param transExec
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean inserisci(StatoOccupazionaleBean statoOccupazionale, SourceBean request,
			RequestContainer requestContainer, SourceBean response, Vector rows, List tuttiIMov, String provenienza,
			List listaMobilita, Vector vettParametriRicostruzione, StatoOccupazionaleManager2 statoOccManager,
			String tipoConfigMov_C03, String dat150, Vector listaDisabiliCM, TransactionQueryExecutor transExec)
			throws ControlliException, Exception {

		Vector configurazioniDefaul_Custom = new Vector();
		configurazioniDefaul_Custom.add(0, tipoConfigMov_C03);
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		Vector movApertiPerTirocini = null;
		String codMonoProvenienza = "";
		String dataRif = "";
		boolean didDecretoFornero2014 = false;
		if (provenienza.equalsIgnoreCase("MOBILITA")) {
			dataRif = (String) request.getAttribute("DATINIZIO");
			codMonoProvenienza = "B";
		} else {
			dataRif = (String) request.getAttribute("datDichiarazione");
			codMonoProvenienza = "D";
			if (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
				didDecretoFornero2014 = true;
			}
		}

		String cdnLavoratore = Utils.notNull(request.getAttribute("cdnLavoratore"));
		nuovoStatoOccupazionale = (StatoOccupazionaleBean) statoOccupazionale.clone();
		int anno = DateUtils.getAnno(dataRif);
		Reddito reddito = null;
		Reddito redditoTI = null;
		String dataFine = "31/12/" + anno;
		// Il flag flgImpattiTirocini viene settato a "S" per default; quando il metodo viene invocato
		// da did allora tale flag rimane ad "S" e tale valore consente la creazione dello stato occupazionale
		// A213, Disoccupato con attività senza contratto. Quando viene invocato da mobilita sospesa, allora il
		// valore viene letto dal vettore vettParametriRicostruzione contenente il valore impostato nella
		// TS_GENERALE; a seconda del valore nella TS_GENERALE del flag FLGSCATTANOIMPATTITIRINMOBSOSP,
		// viene creato o meno lo stato occupazionale A213, Disoccupato con attività senza contratto
		String flgImpattiTirocini = "S";
		boolean gestione150 = (DateUtils.compare(dataRif, dat150) >= 0);
		boolean iscrittoCM = false;
		if (listaDisabiliCM != null && listaDisabiliCM.size() > 0) {
			iscrittoCM = Controlli.inCollocamentoMiratoAllaData(listaDisabiliCM, dataRif);
		}

		SourceBean cm = null;
		if (rows == null) {
			rows = DBLoad.getMovimentiAnno(dataRif, dataFine, statoOccupazionale.getCdnLavoratore());
			rows = Controlli.togliCessazioni(rows);
			rows = Controlli.gestisciPeriodiIntermittenti(rows, dataRif, dataFine, null);
		}
		int codControlloIns = 0;
		if (provenienza.equalsIgnoreCase("MOBILITA")) {
			codControlloIns = ERRORI.NO_ERR;
			if (vettParametriRicostruzione != null && vettParametriRicostruzione.size() > 0) {
				flgImpattiTirocini = vettParametriRicostruzione.get(0).toString();
			}
		} else {
			codControlloIns = inserimentoDIDPossibile(request, statoOccupazionale, requestContainer, response, rows,
					listaMobilita, tuttiIMov, statoOccManager, configurazioniDefaul_Custom, dat150, listaDisabiliCM,
					transExec);
		}

		// ripeto il controllo non si sa mai se nel frattempo sono cambiati dei dati
		if (codControlloIns > 0) {
			// Nei movimenti da considerare per il calcolo del reddito
			// vanno considerati solo i movimenti che si intersecano con la DID.
			// In particolare rows conterrà anche eventuali movimenti di tirocini (che poi vengono
			// esclusi nel calcolo del reddito), e non contiene le cessazione attività lavorativa
			// dopo un periodo di sospensione per contrazione d'attività che hanno codTipoAvviamento = "Z.09.02" (codice
			// vecchio RS3).
			rows = dammiMovimentiCavalloDid(rows, tuttiIMov, dataRif);
			if (!provenienza.equalsIgnoreCase("MOBILITA")) {
				// Gestione dichiarazione DID a rischio disoccupazione 07/2016
				if (codControlloIns == ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE) {
					nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B3);
					nuovoStatoOccupazionale.setChanged(true);

					nuovoStatoOccupazionale.setCodMonoProvenienza(codMonoProvenienza);
					nuovoStatoOccupazionale.setDataCalcoloAnzianita(dataRif);
					String d1 = dataRif;
					String d2 = "29/01/2003";
					nuovoStatoOccupazionale.setDataAnzianita(dataRif);
					nuovoStatoOccupazionale.setNumMesiSosp("0");
					nuovoStatoOccupazionale.setNumAnzianitaPrec297("0");
					nuovoStatoOccupazionale.setPensionato(statoOccupazionale.getPensionato());
					nuovoStatoOccupazionale.setDataCalcoloMesiSosp(DateUtils.compare(d1, d2) > 0 ? d1 : d2);
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, transExec);

					return nuovoStatoOccupazionale;
				}
			}

			switch (statoOccupazionale.getStatoOccupazionaleRagg()) {
			case StatoOccupazionaleBean.RAGG_D:
			case StatoOccupazionaleBean.RAGG_I:
				break;
			case StatoOccupazionaleBean.RAGG_A:

				switch (statoOccupazionale.getStatoOccupazionale()) {
				case StatoOccupazionaleBean.C: // altro
				case StatoOccupazionaleBean.D: // Segnalato dalle imprese di lavoro temporaneo
				case StatoOccupazionaleBean.E: // Segnalato dalle agenzie di mediazione
				case StatoOccupazionaleBean.F: // Proveniente dal flusso scolastico
					// E' possibile che per i lavoratori provenienti da prolabor lo stato
					// occupazionale sia altro nonostante vi siano lavori aperti.
					// La funzione inoccupato ritorna true (non ci sono movimenti precedenti) anche in
					// presenza di soli movimenti di Tirocinio
					if (inoccupato(rows, tuttiIMov, null, request, transExec, dataRif)) {
						movApertiPerTirocini = Controlli.getMovimentiAperti(rows, dataRif);
						if (movApertiPerTirocini.size() == 0) {
							nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A22);
							nuovoStatoOccupazionale.setChanged(true);
						} else {
							if (flgImpattiTirocini.equalsIgnoreCase("S")) {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A223);
								nuovoStatoOccupazionale.setChanged(true);
							} else {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A22);
								nuovoStatoOccupazionale.setChanged(true);
							}
						}
					} else {
						movApertiPerTirocini = Controlli.getMovimentiAperti(rows, dataRif);
						if (movApertiPerTirocini.size() == 0) {
							nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
							nuovoStatoOccupazionale.setChanged(true);
						} else {
							if (Controlli.soloTirocini(movApertiPerTirocini)) {
								if (flgImpattiTirocini.equalsIgnoreCase("S")) {
									nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
									nuovoStatoOccupazionale.setChanged(true);
								} else {
									nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
									nuovoStatoOccupazionale.setChanged(true);
								}
							} else {
								// ora controllo come nel caso di occupato se il reddito mi permette la stipula
								reddito = new Reddito(new LimiteRedditoExt(dataRif), request);
								redditoTI = new Reddito(new LimiteRedditoExt(dataRif), request);
								if (statoOccManager != null && statoOccManager.getCm() != null) {
									cm = statoOccManager.getCm().getSource();
								} else {
									cm = DBLoad.getCollocamentoMirato(request, transExec);
									if (cm != null && statoOccManager != null) {
										statoOccManager.setCm(new CmBean(cm));
									}
								}
								reddito.aggiorna(rows, cm, dataRif, listaMobilita, configurazioniDefaul_Custom,
										transExec);

								if (gestione150 && !iscrittoCM) {
									if (esisteSospensioneAnzianita(rows, cm, tipoConfigMov_C03, listaMobilita,
											redditoTI, dataRif, dat150, cdnLavoratore, iscrittoCM, transExec)) {
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
										nuovoStatoOccupazionale.setChanged(true);
									} else {
										// gestione did post decreto 150 con movimenti che non fanno sospensione
									}
								} else {
									if (ControlloReddito.minoreDelLimite(reddito, null, cdnLavoratore, cm, transExec)) {
										if (esisteMovDi16gg(rows, dataRif, tipoConfigMov_C03)) {
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A212);
											nuovoStatoOccupazionale.setChanged(true);
										} else {
											if (statoOccupazionale
													.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
												nuovoStatoOccupazionale
														.setStatoOccupazionale(StatoOccupazionaleBean.A21);
												nuovoStatoOccupazionale.setChanged(true);
											}
										}
									} else if (esisteSospensioneAnzianita(rows, cm, tipoConfigMov_C03, listaMobilita,
											redditoTI, dataRif, dat150, cdnLavoratore, iscrittoCM, transExec)) {
										// Non dovremmo trovarci in questa situazione, B1 per DID con data >= data
										// decreto Fornero
										// (inserimentoDIDPossibile abbiamo i controlli nuovi)
										if (didDecretoFornero2014) {
											throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
										} else {
											if (esisteMovDi16gg(rows, dataRif, tipoConfigMov_C03)) {
												nuovoStatoOccupazionale
														.setStatoOccupazionale(StatoOccupazionaleBean.B1);
												nuovoStatoOccupazionale.setChanged(true);
											} else {
												if (statoOccupazionale
														.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
													nuovoStatoOccupazionale
															.setStatoOccupazionale(StatoOccupazionaleBean.A21);
													nuovoStatoOccupazionale.setChanged(true);
												}

											}
										}
									}
								}
							}
						}
					}

					if (nuovoStatoOccupazionale.ischanged()) {
						nuovoStatoOccupazionale.setCodMonoProvenienza(codMonoProvenienza);
						nuovoStatoOccupazionale.setDataCalcoloAnzianita(dataRif);
						nuovoStatoOccupazionale.setDataCalcoloMesiSosp(dataRif);
						nuovoStatoOccupazionale.setDataAnzianita(dataRif);
						nuovoStatoOccupazionale.setPensionato(statoOccupazionale.getPensionato());
						nuovoStatoOccupazionale.setNumMesiSosp("0");
						nuovoStatoOccupazionale.setNumAnzianitaPrec297("0");
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, transExec);
					}

					break;

				case StatoOccupazionaleBean.C1:
				case StatoOccupazionaleBean.C14:
				case StatoOccupazionaleBean.C11:
				case StatoOccupazionaleBean.C12:
				case StatoOccupazionaleBean.C13:
					// Controllo stato dei movimenti, ed in base a quelli creare il nuovo stato occupazionale
					// La funzione inoccupato ritorna true (non ci sono movimenti precedenti) anche in
					// presenza di soli movimenti di Tirocinio
					if (inoccupato(rows, tuttiIMov, null, request, transExec, dataRif)) {
						movApertiPerTirocini = Controlli.getMovimentiAperti(rows, dataRif);
						if (movApertiPerTirocini.size() == 0) {
							nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A22);
							nuovoStatoOccupazionale.setChanged(true);
						} else {
							if (flgImpattiTirocini.equalsIgnoreCase("S")) {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A223);
								nuovoStatoOccupazionale.setChanged(true);
							} else {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A22);
								nuovoStatoOccupazionale.setChanged(true);
							}
						}
					} else {
						reddito = new Reddito(new LimiteRedditoExt(dataRif), request);
						redditoTI = new Reddito(new LimiteRedditoExt(dataRif), request);
						if (statoOccManager != null && statoOccManager.getCm() != null) {
							cm = statoOccManager.getCm().getSource();
						} else {
							cm = DBLoad.getCollocamentoMirato(request, transExec);
							if (cm != null && statoOccManager != null) {
								statoOccManager.setCm(new CmBean(cm));
							}
						}
						reddito.aggiorna(rows, cm, dataRif, listaMobilita, configurazioniDefaul_Custom, transExec);
						movApertiPerTirocini = Controlli.getMovimentiAperti(rows, dataRif);
						if (movApertiPerTirocini.size() == 0) {
							nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
							nuovoStatoOccupazionale.setChanged(true);
						} else {
							if (Controlli.soloTirocini(movApertiPerTirocini)) {
								if (flgImpattiTirocini.equalsIgnoreCase("S")) {
									nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
									nuovoStatoOccupazionale.setChanged(true);
								} else {
									nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
									nuovoStatoOccupazionale.setChanged(true);
								}
							} else {
								if (gestione150 && !iscrittoCM) {
									if (esisteSospensioneAnzianita(rows, cm, tipoConfigMov_C03, listaMobilita,
											redditoTI, dataRif, dat150, cdnLavoratore, iscrittoCM, transExec)) {
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
										nuovoStatoOccupazionale.setChanged(true);
									} else {
										// gestione did post decreto 150 con movimenti che non fanno sospensione
									}
								} else {
									if (ControlloReddito.minoreDelLimite(reddito, null, cdnLavoratore, cm, transExec)) {
										// limite reddito inferiore
										if (esisteMovDi16gg(rows, dataRif, tipoConfigMov_C03)) {
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A212);
											nuovoStatoOccupazionale.setChanged(true);
										} else {
											if (statoOccupazionale
													.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
												nuovoStatoOccupazionale
														.setStatoOccupazionale(StatoOccupazionaleBean.A21);
												nuovoStatoOccupazionale.setChanged(true);
											}
										}
									} else {
										if (esisteSospensioneAnzianita(rows, cm, tipoConfigMov_C03, listaMobilita,
												redditoTI, dataRif, dat150, cdnLavoratore, iscrittoCM, transExec)) {
											// Non dovremmo trovarci in questa situazione, B1 per DID con data >= data
											// decreto Fornero
											// (inserimentoDIDPossibile abbiamo i controlli nuovi)
											if (didDecretoFornero2014) {
												throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
											} else {
												if (esisteMovDi16gg(rows, dataRif, tipoConfigMov_C03)) {
													nuovoStatoOccupazionale
															.setStatoOccupazionale(StatoOccupazionaleBean.B1);
													nuovoStatoOccupazionale.setChanged(true);
												} else {
													if (statoOccupazionale
															.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
														nuovoStatoOccupazionale
																.setStatoOccupazionale(StatoOccupazionaleBean.A21);
														nuovoStatoOccupazionale.setChanged(true);
													}
												}
											}
										} else {
											throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
										}
									}
								}
							}
						}
					}

					if (nuovoStatoOccupazionale.ischanged()) {
						nuovoStatoOccupazionale.setCodMonoProvenienza(codMonoProvenienza);
						nuovoStatoOccupazionale.setDataCalcoloAnzianita(dataRif);
						nuovoStatoOccupazionale.setDataAnzianita(dataRif);
						nuovoStatoOccupazionale.setNumMesiSosp("0");
						nuovoStatoOccupazionale.setNumAnzianitaPrec297("0");
						nuovoStatoOccupazionale.setPensionato(statoOccupazionale.getPensionato());
						String d1 = dataRif;
						String d2 = "29/01/2003";
						nuovoStatoOccupazionale.setDataCalcoloMesiSosp(DateUtils.compare(d1, d2) > 0 ? d1 : d2);
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, transExec);
					}

					break;
				}
				if (statoOccupazionale.getStatoOccupazionale() != StatoOccupazionaleBean.C0)
					break;
				else
					; // prosegui nella successiva sezione

			case StatoOccupazionaleBean.RAGG_O:
				if (statoOccupazionale.getStatoOccupazionale() == StatoOccupazionaleBean.A0) {
					if (gestione150 && !iscrittoCM) {
						nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
						nuovoStatoOccupazionale.setChanged(true);
					} else {
						nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A212);
						nuovoStatoOccupazionale.setChanged(true);
					}
				} else {
					// controllo del reddito e della situazione particolare
					movApertiPerTirocini = Controlli.getMovimentiAperti(rows, dataRif);
					if (movApertiPerTirocini.size() == 0) {
						nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
						nuovoStatoOccupazionale.setChanged(true);
					} else {
						if (Controlli.soloTirocini(movApertiPerTirocini)) {
							if (flgImpattiTirocini.equalsIgnoreCase("S")) {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
								nuovoStatoOccupazionale.setChanged(true);
							} else {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
								nuovoStatoOccupazionale.setChanged(true);
							}
						} else {
							reddito = new Reddito(new LimiteRedditoExt(dataRif), request);
							redditoTI = new Reddito(new LimiteRedditoExt(dataRif), request);
							if (statoOccManager != null && statoOccManager.getCm() != null) {
								cm = statoOccManager.getCm().getSource();
							} else {
								cm = DBLoad.getCollocamentoMirato(request, transExec);
								if (cm != null && statoOccManager != null) {
									statoOccManager.setCm(new CmBean(cm));
								}
							}
							reddito.aggiorna(rows, cm, dataRif, listaMobilita, configurazioniDefaul_Custom, transExec);
							if (gestione150 && !iscrittoCM) {
								if (esisteSospensioneAnzianita(rows, cm, tipoConfigMov_C03, listaMobilita, redditoTI,
										dataRif, dat150, cdnLavoratore, iscrittoCM, transExec)) {
									nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
									nuovoStatoOccupazionale.setChanged(true);
								} else {
									// gestione did post decreto 150 con movimenti che non fanno sospensione
								}
							} else {
								if (ControlloReddito.minoreDelLimite(reddito, null, cdnLavoratore, cm, transExec)) {
									if (esisteMovDi16gg(rows, dataRif, tipoConfigMov_C03)) {
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A212);
										nuovoStatoOccupazionale.setChanged(true);
									} else {
										if (statoOccupazionale.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
											nuovoStatoOccupazionale.setChanged(true);
										}
									}
								} else if (esisteSospensioneAnzianita(rows, cm, tipoConfigMov_C03, listaMobilita,
										redditoTI, dataRif, dat150, cdnLavoratore, iscrittoCM, transExec)) {
									// Non dovremmo trovarci in questa situazione, B1 per DID con data >= data decreto
									// Fornero
									// (inserimentoDIDPossibile abbiamo i controlli nuovi)
									if (didDecretoFornero2014) {
										throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
									} else {
										if (esisteMovDi16gg(rows, dataRif, tipoConfigMov_C03)) {
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
											nuovoStatoOccupazionale.setChanged(true);
										} else {
											if (statoOccupazionale
													.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
												nuovoStatoOccupazionale
														.setStatoOccupazionale(StatoOccupazionaleBean.A21);
												nuovoStatoOccupazionale.setChanged(true);
											}
										}
									}
								}
							}
							if (Controlli.movimentiAvvProtocollati(rows) == 0) {
								// questa e' una situazione che si puo' verificare quando non e' stato inserito il
								// movimento di cessazione, per cui non ci sono movimenti ma il lavoratore risulta
								// occupato.
								// Registro il nuovo stato occupazionale ma fornisco una segnalazione all'operatore
								// sempre che la response esista, cosa vera solo e solo se la chiamata avviene
								// dall'inserimento
								// manuale della did, non dalla ricostruzione della storia amministrativa
								// NB il messaggio al massimo comparira nella pop-up della gestione stampe non di certo
								// nella
								// pagina della did
								if (_logger.isDebugEnabled()) {
									_logger.debug(
											"DIDManager.inserisci(): stato occupazionale iniziale non gestito nei diagrammi standard");
								}
								if (response != null)
									it.eng.afExt.utils.MessageAppender.appendMessage(response,
											MessageCodes.DID.OCCUPATO_SENZA_MOVIMENTI);
							}
						}
					}
				}
				// se tutto ok
				if (nuovoStatoOccupazionale.ischanged()) {
					nuovoStatoOccupazionale.setCodMonoProvenienza(codMonoProvenienza);
					nuovoStatoOccupazionale.setDataCalcoloAnzianita(dataRif);
					String d1 = dataRif;
					String d2 = "29/01/2003";
					nuovoStatoOccupazionale.setDataAnzianita(dataRif);
					nuovoStatoOccupazionale.setNumMesiSosp("0");
					nuovoStatoOccupazionale.setNumAnzianitaPrec297("0");
					nuovoStatoOccupazionale.setPensionato(statoOccupazionale.getPensionato());
					nuovoStatoOccupazionale.setDataCalcoloMesiSosp(DateUtils.compare(d1, d2) > 0 ? d1 : d2);
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, transExec);
				}

				break;
			}
		} else {
			if (codControlloIns == ERRORI.REDDITO_SUPERIORE) {
				throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
			} else if (codControlloIns == ERRORI.TERMINI_NON_SCADUTI) {
				throw new ControlliException(MessageCodes.DID.TERMINI_DID_NON_SCADUTI);
			} else if (codControlloIns == ERRORI.ERR_DID_NO_SOSPENSIONE) {
				throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
			} else {
				throw new ControlliException(MessageCodes.DID.CONTROLLI_INSERIMENTO_NON_SUPERATI);
			}
		}
		return nuovoStatoOccupazionale;
	}

	/**
	 * Si tratta del movimento della durata minore di 8 mesi o 4 se il lavoratore rientra nella categoria "giovane".
	 * Sempre che il rapporto sia a tempo determinato. Basta che un movimento non rientri nella categoria di
	 * "sospensione di anzianita'" che venga ritornato false. La procedura è stata adeguata in seguito al decreto
	 * Fornero (durata minore o uguale 6 mesi considerando anche il reddito) e decreto 150 (durata minore o uguale 6
	 * mesi)
	 */
	public static boolean esisteSospensioneAnzianita(Vector rows, SourceBean cm, String tipoConfigC03,
			List listaMobilita, Reddito redditoTI, String dataRif, String dat150, String cdnLavoratore,
			boolean iscrittoCM, TransactionQueryExecutor transExec) throws Exception {
		boolean ret = true;
		String codMonoTipoAss = "";
		String codTipoAvviamento = "";
		int rowSize = rows.size();
		boolean gestione150 = (DateUtils.compare(dataRif, dat150) >= 0);
		for (int i = 0; ret && (i < rowSize); i++) {
			SourceBean movimento = (SourceBean) rows.get(i);
			boolean flagIns = movimento.containsAttribute("FLAG_IN_INSERIMENTO");
			if (!flagIns || MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.CESSAZIONE) {
				if (MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.CESSAZIONE
						|| !MovimentoBean.protocollato(movimento))
					continue;
			}
			codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// non considerare I TIROCINI, TIPO AVVIAMENTO Z.09.02(vecchio codice RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (codMonoTipoAss.equals("T") || codTipoAvviamento.equals("RS3") || codTipoAvviamento.equals("Z.09.02")) {
				continue;
			}
			redditoTI.aggiorna(movimento, cm, dataRif, listaMobilita);
			String dataFineMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			if (gestione150 && !iscrittoCM) {
				ret = Controlli.isCategoriaParticolareDecreto150(movimento, dataRif);
			} else {
				if (Contratto.getTipoContratto(movimento) == Contratto.DIP_TI
						&& (dataFineMov == null || dataFineMov.equals(""))) {
					if (!ControlloReddito.minoreDelLimite(redditoTI, null, cdnLavoratore, cm, transExec)) {
						ret = false;
					}
				} else {
					boolean isCatParticolare = false;
					int annoRif = DateUtils.getAnno(dataRif);
					if (annoRif < DateUtils.getAnno(MessageCodes.General.DATA_DECRETO_FORNERO_2014)) {
						isCatParticolare = Controlli.isCategoriaParticolare(movimento);
					} else {
						isCatParticolare = Controlli.isCategoriaParticolareDecretoFornero(movimento, dataRif, true);
					}
					if (!ControlloReddito.minoreDelLimite(redditoTI, null, cdnLavoratore, cm, transExec)
							&& !isCatParticolare) {
						ret = false;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Invocato per verificare se la did post decreto 150 è stipulabile per lavoratori non iscritti al cm
	 * 
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	public static boolean priviDiImpiego150(Vector rows) throws Exception {
		boolean ret = true;
		String codMonoTipoAss = "";
		String codTipoAvviamento = "";
		int rowSize = rows.size();
		for (int i = 0; ret && (i < rowSize); i++) {
			SourceBean movimento = (SourceBean) rows.get(i);
			boolean flagIns = movimento.containsAttribute("FLAG_IN_INSERIMENTO");
			if (!flagIns || MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.CESSAZIONE) {
				if (MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.CESSAZIONE
						|| !MovimentoBean.protocollato(movimento))
					continue;
			}
			codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// non considerare I TIROCINI, TIPO AVVIAMENTO Z.09.02(vecchio codice RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (codMonoTipoAss.equals("T") || codTipoAvviamento.equals("RS3") || codTipoAvviamento.equals("Z.09.02")) {
				continue;
			}
			ret = false;
		}
		return ret;
	}

	/**
	 * ritorna true se esiste almeno un movimento che a partire dalla data inizio stato occupazionale (che può essere la
	 * data della did) dura più di 15 giorni (per generare o meno sospensione o precarieta).
	 */
	public static boolean esisteMovDi16gg(Vector rows, String dataRif, String tipoConfigC03) throws Exception {
		boolean ret = false;
		String dataFineMov = "";
		String dataInizioMov = "";
		String codMonoTipoAss = "";
		String codTipoAvviamento = "";
		int rowSize = rows.size();
		for (int i = 0; i < rowSize; i++) {
			dataFineMov = "";
			SourceBean movimento = (SourceBean) rows.get(i);
			boolean flagIns = movimento.containsAttribute("FLAG_IN_INSERIMENTO");
			if (!flagIns || MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.CESSAZIONE) {
				if (MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.CESSAZIONE
						|| !MovimentoBean.protocollato(movimento))
					continue;
			}
			codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// non considerare I TIROCINI, TIPO AVVIAMENTO Z.09.02(vecchio codice RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (codMonoTipoAss.equals("T") || codTipoAvviamento.equals("RS3") || codTipoAvviamento.equals("Z.09.02")) {
				continue;
			}
			dataInizioMov = movimento.containsAttribute(MovimentoBean.DB_DATA_INIZIO)
					? movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()
					: "";
			dataFineMov = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			String codTipoMov = movimento.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
					? movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
					: "";
			if (codTipoMov.equalsIgnoreCase("AVV") && movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector vettSucc = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
				// prendo l'ultima proroga o trasformazione
				if (vettSucc.size() > 0) {
					int k = vettSucc.size() - 1;
					SourceBean movimentoUltimo = (SourceBean) vettSucc.get(k);
					dataFineMov = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
							? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
							: "";
				}
			}
			if (DateUtils.compare(dataInizioMov, dataRif) <= 0) {
				if (dataFineMov.equals("")) {
					ret = true;
					break;
				} else {
					if (DateUtils.compare(dataFineMov, dataRif) >= 0
							&& (DateUtils.daysBetween(dataRif, dataFineMov)) + 1 > 15) {
						ret = true;
						break;
					}
				}
			}

		}
		return ret;
	}

	/**
	 * Controlla se e' possibile dichiarare la did in base allo stato occupazionale corrente e dei movimenti in corso
	 * nell' anno di riferimento, ovvero di dichiarazione in corso
	 */
	public static boolean inserimentoDID(SourceBean request, RequestContainer requestContainer, SourceBean response,
			SituazioneAmministrativa sitAmm, TransactionQueryExecutor transExec) throws Exception, ControlliException {
		List movimenti = sitAmm.getMovimenti();
		String configMOV_C03 = sitAmm.getTipoCongif_MOV_C03();
		Vector configurazioniDefaul_Custom = new Vector();
		configurazioniDefaul_Custom.add(0, configMOV_C03);
		String dataNormativa297 = sitAmm.getDataPrec297();
		// controllare lo stato occupazionale precedente la data dichiarazione;
		// se la data dichiarazione e' precedente la data di sistema,
		// ovvero se sto' inserendo una dichiarazione vecchia
		StatoOccupazionaleBean statoOccupazionale = null;
		String dataRif = (String) request.getAttribute("datDichiarazione");
		String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
		StatoOccupazionaleManager2 statoOccManager = null;
		// controllo se la data dichiarazione della did si trova in un periodo di mobilità del lavoratore
		ListaMobilita listaMobilita = new ListaMobilita(cdnLavoratore, transExec);
		List rowsMobilita = listaMobilita.getMobilita();
		boolean isInMobilita = checkDidInMobilita(rowsMobilita, dataRif);
		if (isInMobilita) {
			throw new ControlliException(MessageCodes.DID.ESISTE_PERIODO_MOBILITA);
		}

		boolean isTrasferimento = request.containsAttribute("DATTRASFERIMENTO");
		boolean didStipulabile = true;
		statoOccupazionale = DBLoad.getStatoOccupazionaleUltimo(cdnLavoratore, dataRif, transExec);
		if (statoOccupazionale == null) {
			SourceBean sb = new SourceBean(request);
			sb.delAttribute("prgStatoOccupaz");
			statoOccupazionale = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(sb);
		} else {
			statoOccupazionale = DBLoad.getStatoOccupazionale((String) request.getAttribute("cdnLavoratore"),
					transExec);
			String dataInizioStatoOcc = statoOccupazionale.getDataInizio();
			// 06/04/2005 SE LA DATA DI DICHIARAZIONE DELLA DID E' PRECEDENTE ALLA DATA INIZIO STATO
			// OCCUPAZIONALE ATTUALE DI DISOCCUPATO O INOCCUPATO VIENE PERMESSO INSERIMENTO DID PER
			// I LAVORATORI CON UN PERIODO DI MOBILITA SUCCESSIVO ALLA DID.
			// NEGLI ALTRI CASI, UNO STATO OCCUPAZIONALE ATTUALE DI DISOCCUPATO O INOCCUPATO IMPLICA LA
			// PRESENZA DI UNA DID. IL SISTEMA IN QUESTO CASO TRAMITE TRIGGER NON PERMETTE COMUNQUE
			// L'INSERIMENTO DI UNA DID PRECEDENTE.
			if ((!checkDidAnzianitaMobilita(rowsMobilita, dataRif))
					&& (statoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
							|| statoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I)) {
				if (dataInizioStatoOcc != null && !dataInizioStatoOcc.equals("") && dataRif != null
						&& !dataRif.equals("") && DateUtils.compare(dataRif, dataInizioStatoOcc) >= 0) {
					didStipulabile = false;
				}
			}
		}
		// se lo stato occupazionale del lavoratore e' di disoccupato o inoccupato alla data della stipula della did,
		// non ha senso che ridichiari la DID (a meno di trasferimento)
		if (!didStipulabile && !isTrasferimento)
			throw new ControlliException(MessageCodes.DID.LAVORATORE_GIA_DISOCCUPATO);
		// if ((statoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
		// || statoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I)
		// && !isTrasferimento)

		// Controlla che la data di dichiarazione non sia precedente alla data normativa 297 presente nella
		// configurazione AM_297
		if (DateUtils.compare(dataRif, dataNormativa297) < 0) {
			throw new ControlliException(MessageCodes.DID.DATA_DID_PRECEDENTE_AL_30012003);
		}

		int anno = DateUtils.getAnno(dataRif);
		String dataFine = "31/12/" + anno;
		Vector rows = DBLoad.getMovimentiAnno(dataRif, dataFine, statoOccupazionale.getCdnLavoratore(), transExec);
		rows = Controlli.togliCessazioni(rows);
		rows = Controlli.gestisciPeriodiIntermittenti(rows, dataRif, dataFine, transExec);

		int code = inserimentoDIDPossibile(request, statoOccupazionale, requestContainer, response, rows, rowsMobilita,
				movimenti, statoOccManager, configurazioniDefaul_Custom, sitAmm.getData150(),
				sitAmm.getListaDisabiliCM(), transExec);

		if (code < 0) {
			if (code == ERRORI.REDDITO_SUPERIORE) {
				throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
			}
			if (code == ERRORI.ERR_DID_NO_SOSPENSIONE) {
				throw new ControlliException(MessageCodes.DID.REDDITO_SUPERIORE_LIMITE);
			}
			if (code == ERRORI.TERMINI_NON_SCADUTI) {
				throw new ControlliException(MessageCodes.DID.TERMINI_DID_NON_SCADUTI);
			}
			if (code == ERRORI.ERR_MOV_RISCHIO_DISOCCUPAZIONE) {
				// situazione gestita solo nella stipula did online(ws StipulaDid)
				throw new ControlliException(MessageCodes.DID.ERR_MOV_RISCHIO_DISOCCUPAZIONE);
			}
			throw new ControlliException(MessageCodes.StatoOccupazionale.ERRORE_GENERICO);
		}
		return (code == ERRORI.NO_ERR || code == ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE);
	}

	/**
	 * Controlla che i movimenti dell'anno di riferimento della did permettano nell'insieme la sua stipula
	 */
	public static int inserimentoDIDPossibile(SourceBean request, StatoOccupazionaleBean statoOccupazionale,
			RequestContainer requestContainer, SourceBean response, Vector rows, List listaMobilita, List movimenti,
			StatoOccupazionaleManager2 statoOccManager, Vector configurazioniDefaul_Custom, String dat150,
			Vector listaDisabiliCM, TransactionQueryExecutor transExec) throws Exception {
		String dataDichiarazione = (String) request.getAttribute("datDichiarazione");
		String cdnLavoratore = Utils.notNull(request.getAttribute("cdnLavoratore"));
		String flgRischioDisoccupazione = Utils.notNull(request.getAttribute("flgRischioDisoccupazione"));
		String flgLavoroAutonomo = Utils.notNull(request.getAttribute("flgLavoroAutonomo"));
		// situazione gestita solo nella stipula did online(ws StipulaDid)
		boolean didOnLine = (request.containsAttribute("DIDONLINE")
				&& request.getAttribute("DIDONLINE").toString().equalsIgnoreCase("true"));
		Reddito reddito = null;
		Reddito redditoTI = null;
		SourceBean cm = null;
		boolean didDecretoFornero2014 = false;
		boolean didDecreto150 = false;
		if (DateUtils.compare(dataDichiarazione, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
			didDecretoFornero2014 = true;
		}
		if (DateUtils.compare(dataDichiarazione, dat150) >= 0) {
			didDecreto150 = true;
		}
		boolean iscrittoCM = false;
		if (listaDisabiliCM != null && listaDisabiliCM.size() > 0) {
			iscrittoCM = Controlli.inCollocamentoMiratoAllaData(listaDisabiliCM, dataDichiarazione);
		}

		String configMOV_C03 = configurazioniDefaul_Custom.get(0).toString();
		// nei movimenti da considerare per il calcolo del reddito e quindi controllare se la
		// did è stipulabile o meno vanno considerati solo i movimenti che si intersecano con la did
		rows = dammiMovimentiCavalloDid(rows, movimenti, dataDichiarazione);

		switch (statoOccupazionale.getStatoOccupazionaleRagg()) {
		case StatoOccupazionaleBean.RAGG_D:
		case StatoOccupazionaleBean.RAGG_I:
			return ERRORI.NO_ERR;

		case StatoOccupazionaleBean.RAGG_A:

			switch (statoOccupazionale.getStatoOccupazionale()) {
			case StatoOccupazionaleBean.C1: // C1*
			case StatoOccupazionaleBean.C11: // C11
			case StatoOccupazionaleBean.C12: // C12*
			case StatoOccupazionaleBean.C13: // C13*
			case StatoOccupazionaleBean.C14: // *

				if (terminiUltimaDichiarazioneScaduti(request, transExec)) {
					reddito = new Reddito(new LimiteRedditoExt(dataDichiarazione), request);
					redditoTI = new Reddito(new LimiteRedditoExt(dataDichiarazione), request);
					if (statoOccManager != null && statoOccManager.getCm() != null) {
						cm = statoOccManager.getCm().getSource();
					} else {
						cm = DBLoad.getCollocamentoMirato(request, transExec);
						if (cm != null && statoOccManager != null) {
							statoOccManager.setCm(new CmBean(cm));
						}
					}
					if (didDecreto150 && !iscrittoCM) {
						// Gestione dichiarazione DID a rischio disoccupazione 07/2016
						if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI)) {
							if (rows.size() == 0) {
								return ERRORI.NO_ERR;
							} else {
								if (rows.size() == 1) {
									SourceBean mov = (SourceBean) rows.get(0);
									String codMonoTempo = getCodMonoTempoMovimento(mov);
									if (codMonoTempo.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
										// CONTROLLI SUPERATI
										return ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE;
									} else {
										// CONTROLLI FALLITI
										return ERRORI.REDDITO_SUPERIORE;
									}
								} else {
									if (rows.size() > 1 && didOnLine) {
										// CONTROLLI FALLITI
										return ERRORI.ERR_MOV_RISCHIO_DISOCCUPAZIONE;
									} else {
										// CONTROLLI FALLITI
										return ERRORI.REDDITO_SUPERIORE;
									}
								}
							}
						} else {
							// Gestione dichiarazione DID in presenza di rapporto di lavoro autonomo
							if (flgLavoroAutonomo.equalsIgnoreCase(MovimentoBean.SI)) {
								if (rows.size() == 0) {
									return ERRORI.NO_ERR;
								} else {
									// CONTROLLI FALLITI
									return ERRORI.REDDITO_SUPERIORE;
								}
							} else {
								if (priviDiImpiego150(rows)) {
									return ERRORI.NO_ERR;
								} else {
									return ERRORI.ERR_DID_NO_SOSPENSIONE;
								}
							}
						}
					} else {
						reddito.aggiorna(rows, cm, dataDichiarazione, listaMobilita, configurazioniDefaul_Custom,
								transExec);

						if ((ControlloReddito.minoreDelLimite(reddito, null, cdnLavoratore, cm, transExec))
								|| (esisteSospensioneAnzianita(rows, cm, configMOV_C03, listaMobilita, redditoTI,
										dataDichiarazione, dat150, cdnLavoratore, iscrittoCM, transExec)
										&& !didDecretoFornero2014)) {
							return ERRORI.NO_ERR;
						} else {

							// Gestione dichiarazione DID a rischio disoccupazione 07/2016
							if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI) && rows.size() == 1) {
								SourceBean mov = (SourceBean) rows.get(0);
								String codMonoTempo = getCodMonoTempoMovimento(mov);
								if (codMonoTempo.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
									return ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE;
								} else {
									return ERRORI.REDDITO_SUPERIORE;
								}
							} else {
								if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI) && rows.size() > 1
										&& didOnLine) {
									return ERRORI.ERR_MOV_RISCHIO_DISOCCUPAZIONE;
								} else {
									return ERRORI.REDDITO_SUPERIORE;
								}
							}
						}
					}

				}

				return ERRORI.TERMINI_NON_SCADUTI;

			case StatoOccupazionaleBean.C: // C
			case StatoOccupazionaleBean.E: // E
			case StatoOccupazionaleBean.F: // F
			case StatoOccupazionaleBean.D: // D
			case StatoOccupazionaleBean.A0: // sospeso per contrazione di attivita': deve ancora essere gestito
				reddito = new Reddito(new LimiteRedditoExt(dataDichiarazione), request);
				redditoTI = new Reddito(new LimiteRedditoExt(dataDichiarazione), request);
				if (statoOccManager != null && statoOccManager.getCm() != null) {
					cm = statoOccManager.getCm().getSource();
				} else {
					cm = DBLoad.getCollocamentoMirato(request, transExec);
					if (cm != null && statoOccManager != null) {
						statoOccManager.setCm(new CmBean(cm));
					}
				}

				if (didDecreto150 && !iscrittoCM) {
					// Gestione dichiarazione DID a rischio disoccupazione 07/2016
					if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI)) {
						if (rows.size() == 0) {
							return ERRORI.NO_ERR;
						} else {
							if (rows.size() == 1) {
								SourceBean mov = (SourceBean) rows.get(0);
								String codMonoTempo = getCodMonoTempoMovimento(mov);
								if (codMonoTempo.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
									// CONTROLLI SUPERATI
									return ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE;
								} else {
									// CONTROLLI FALLITI
									return ERRORI.REDDITO_SUPERIORE;
								}
							} else {
								if (rows.size() > 1 && didOnLine) {
									// CONTROLLI FALLITI
									return ERRORI.ERR_MOV_RISCHIO_DISOCCUPAZIONE;
								} else {
									// CONTROLLI FALLITI
									return ERRORI.REDDITO_SUPERIORE;
								}
							}
						}
					} else {
						// Gestione dichiarazione DID in presenza di rapporto di lavoro autonomo
						if (flgLavoroAutonomo.equalsIgnoreCase(MovimentoBean.SI)) {
							if (rows.size() == 0) {
								return ERRORI.NO_ERR;
							} else {
								// CONTROLLI FALLITI
								return ERRORI.REDDITO_SUPERIORE;
							}
						} else {
							if (priviDiImpiego150(rows)) {
								return ERRORI.NO_ERR;
							} else {
								return ERRORI.ERR_DID_NO_SOSPENSIONE;
							}
						}
					}
				} else {
					reddito.aggiorna(rows, cm, dataDichiarazione, listaMobilita, configurazioniDefaul_Custom,
							transExec);

					if ((ControlloReddito.minoreDelLimite(reddito, null, cdnLavoratore, cm, transExec))
							|| (esisteSospensioneAnzianita(rows, cm, configMOV_C03, listaMobilita, redditoTI,
									dataDichiarazione, dat150, cdnLavoratore, iscrittoCM, transExec)
									&& !didDecretoFornero2014)) {
						return ERRORI.NO_ERR;
					} else {
						// Gestione dichiarazione DID a rischio disoccupazione 07/2016
						if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI) && rows.size() == 1) {
							SourceBean mov = (SourceBean) rows.get(0);
							String codMonoTempo = getCodMonoTempoMovimento(mov);
							if (codMonoTempo.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
								return ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE;
							} else {
								return ERRORI.REDDITO_SUPERIORE;
							}
						} else {
							if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI) && rows.size() > 1
									&& didOnLine) {
								return ERRORI.ERR_MOV_RISCHIO_DISOCCUPAZIONE;
							} else {
								return ERRORI.REDDITO_SUPERIORE;
							}
						}
					}
				}

			case StatoOccupazionaleBean.C0: // C0 cessato non rientrato
				// passo allo stato di raggruppamento O . VEDI DIAGRAMMA
			}

		case StatoOccupazionaleBean.RAGG_O:
			reddito = new Reddito(new LimiteRedditoExt(dataDichiarazione), request);
			redditoTI = new Reddito(new LimiteRedditoExt(dataDichiarazione), request);
			if (statoOccManager != null && statoOccManager.getCm() != null) {
				cm = statoOccManager.getCm().getSource();
			} else {
				cm = DBLoad.getCollocamentoMirato(request, transExec);
				if (cm != null && statoOccManager != null) {
					statoOccManager.setCm(new CmBean(cm));
				}
			}

			if (didDecreto150 && !iscrittoCM) {
				// Gestione dichiarazione DID a rischio disoccupazione 07/2016
				if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI)) {
					if (rows.size() == 0) {
						return ERRORI.NO_ERR;
					} else {
						if (rows.size() == 1) {
							SourceBean mov = (SourceBean) rows.get(0);
							String codMonoTempo = getCodMonoTempoMovimento(mov);
							if (codMonoTempo.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
								// CONTROLLI SUPERATI
								return ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE;
							} else {
								// CONTROLLI FALLITI
								return ERRORI.REDDITO_SUPERIORE;
							}
						} else {
							if (rows.size() > 1 && didOnLine) {
								// CONTROLLI FALLITI
								return ERRORI.ERR_MOV_RISCHIO_DISOCCUPAZIONE;
							} else {
								// CONTROLLI FALLITI
								return ERRORI.REDDITO_SUPERIORE;
							}
						}
					}
				} else {
					// Gestione dichiarazione DID in presenza di rapporto di lavoro autonomo
					if (flgLavoroAutonomo.equalsIgnoreCase(MovimentoBean.SI)) {
						if (rows.size() == 0) {
							return ERRORI.NO_ERR;
						} else {
							// CONTROLLI FALLITI
							return ERRORI.REDDITO_SUPERIORE;
						}
					} else {
						if (priviDiImpiego150(rows)) {
							return ERRORI.NO_ERR;
						} else {
							return ERRORI.ERR_DID_NO_SOSPENSIONE;
						}
					}
				}
			} else {
				reddito.aggiorna(rows, cm, dataDichiarazione, listaMobilita, configurazioniDefaul_Custom, transExec);

				if ((ControlloReddito.minoreDelLimite(reddito, null, cdnLavoratore, cm, transExec))
						|| (esisteSospensioneAnzianita(rows, cm, configMOV_C03, listaMobilita, redditoTI,
								dataDichiarazione, dat150, cdnLavoratore, iscrittoCM, transExec)
								&& !didDecretoFornero2014)) {
					if (terminiUltimaDichiarazioneScaduti(request, transExec)) {
						return ERRORI.NO_ERR;
					} else {
						return ERRORI.TERMINI_NON_SCADUTI;
					}
				} else {
					// Gestione dichiarazione DID a rischio disoccupazione 07/2016
					if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI) && rows.size() == 1) {
						SourceBean mov = (SourceBean) rows.get(0);
						String codMonoTempo = getCodMonoTempoMovimento(mov);
						if (codMonoTempo.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
							if (terminiUltimaDichiarazioneScaduti(request, transExec)) {
								return ERRORI.NO_ERR_RISCHIO_DISOCCUPAZIONE;
							} else {
								return ERRORI.TERMINI_NON_SCADUTI;
							}
						} else {
							return ERRORI.REDDITO_SUPERIORE;
						}
					} else {
						if (flgRischioDisoccupazione.equalsIgnoreCase(MovimentoBean.SI) && rows.size() > 1
								&& didOnLine) {
							return ERRORI.ERR_MOV_RISCHIO_DISOCCUPAZIONE;
						} else {
							return ERRORI.REDDITO_SUPERIORE;
						}
					}
				}
			}

		default:
		}
		throw new Exception("stato di raggruppamento inesistente");
	}

	/**
	 * Una delle condizioni per cui la did possa essere rilasciata e' che siano trascorsi n mesi dall' ultima did, nel
	 * caso in cui questa sia stata chiusa per decadenza dallo stato di disoccupazione, o meglio se lo stato
	 * occupazionale nel momento della chiusura della did sia uno che riguardi la decadenza dallo stato di
	 * disoccupazione.
	 * 
	 * @return true se i termini sono scaduti e la did puo' essere stipulata
	 */
	public static boolean terminiUltimaDichiarazioneScaduti(SourceBean request, TransactionQueryExecutor transExec)
			throws Exception {
		Vector rows = DBLoad.getUltimaDIDStoricizzata(request, (String) request.getAttribute("datDichiarazione"),
				transExec);
		BigDecimal prg = null;
		Object oPrg = request.getAttribute("prgDichDisponibilita");
		SourceBean row = null;
		if (oPrg == null) {
			// prima fase di inserimento della did; non e' ancora stata inserita nella tabella
			// quindi non corro il rischio di estrarre dal db la did stessa
			if (rows.size() > 0)
				row = (SourceBean) rows.firstElement();
			if (_logger.isDebugEnabled()) {
				_logger.debug("DIDManager.terminiUltimaDichiarazioneScaduti(): " + row);
			}
		} else {

			if (oPrg instanceof String)
				prg = new BigDecimal((String) oPrg);
			else
				prg = (BigDecimal) oPrg;
			for (int i = 0; i < rows.size(); i++) {
				SourceBean did = (SourceBean) rows.get(i);
				BigDecimal prgI = (BigDecimal) did.getAttribute("prgDichDisponibilita");

				if (prg.equals(prgI))
					row = (i - 1 < 0) ? null : (SourceBean) rows.get(i - 1);
			}
			if (row == null) {
				row = new SourceBean("ROW");
			}
		}
		if (row != null && row.getAttribute("PRGDICHDISPONIBILITA") != null) {
			String dataRif = (String) request.getAttribute("datDichiarazione");
			String codMonoSpec = "";
			String codMotivoFineAtto = row.containsAttribute("codMotivoFineAtto")
					? row.getAttribute("codMotivoFineAtto").toString()
					: "";
			if (!codMotivoFineAtto.equals("")) {
				SourceBean sbMotivoFineAtto = DBLoad.getStatoOccAssociatoAlMotivoFineAtto(codMotivoFineAtto, "AM_DIC_D",
						dataRif);
				if (sbMotivoFineAtto != null) {
					codMonoSpec = sbMotivoFineAtto.containsAttribute("codMonoSpec")
							? sbMotivoFineAtto.getAttribute("codMonoSpec").toString()
							: "";
				}
			}
			// leggo la data inizio della did
			// leggo tutti gli stati occupazionali a partire da quella data e fino alla data di riferimento
			// se uno di questi stati occupazionali ha codice altro decaduto allora vado a leggere dalla
			// tabella il numero di mesi di sospensione
			// e faccio i conti
			String dataFineDidStoricizzata = (String) row.getAttribute("datFine");
			Object cdnLavoratore = request.getAttribute("cdnLavoratore");
			SourceBean soDecaduto = DBLoad.getStatoOccupazionaleDecadutoDa(cdnLavoratore, dataFineDidStoricizzata,
					dataRif, transExec);
			if (soDecaduto.getAttribute("prgstatooccupaz") != null) {
				// suppongo che lo stato occupazionale successivo alla chiusura della did sia dei tipi sopra
				BigDecimal numeroMesi = null;
				if (codMonoSpec.equalsIgnoreCase("A")) { // A=il motivo di chiusura did riguarda l' art.16 L.56/87)
					numeroMesi = (BigDecimal) soDecaduto.getAttribute("numMesiBloccoArt16");
				} else {
					numeroMesi = (BigDecimal) soDecaduto.getAttribute("numMesiBlocco");
				}

				if (numeroMesi == null) {
					return true;
				}

				String dataDichiarazione = dataRif;
				if ((DateUtils.daysBetween(dataFineDidStoricizzata, dataDichiarazione) / 30.0) > numeroMesi
						.doubleValue()) {
					return true;
				} else {
					return false;
				}
			}

			return true;
		}

		return true;
	}

	/**
	 * Controlla che il lavoratore non abbia mai lavorato, ovvero non siano presenti movimenti nel db, non sia
	 * valorizzato il campo codUltimoContratto nella dichiarazione precedente, e non sia presente nessun record in
	 * pr_mansione con flgEsperienza = 'S'
	 * 
	 * @param rows
	 *            il vettore dei movimenti del lavoratore
	 * @param did
	 *            ultima dichiarazione di immediata dispnibilita' storicizzata
	 * @param request
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static boolean inoccupato(Vector movimentiAnno, List tuttiIMov, SourceBean did, SourceBean request,
			TransactionQueryExecutor transExec, String dataDichiarazione) throws Exception {
		boolean ret = false;

		if (Controlli.movimentiAvvProtocollati(movimentiAnno) == 0) {
			Object cdnLavoratore = request.getAttribute("cdnLavoratore");
			if (tuttiIMov == null) {
				tuttiIMov = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
				tuttiIMov = MovimentoBean.gestisciTuttiPeriodiIntermittenti(tuttiIMov, transExec);
			}
			tuttiIMov = Controlli.togliCessazioni(tuttiIMov);
			tuttiIMov = Controlli.movimentiPrimaDi(dataDichiarazione, tuttiIMov);
			if (Controlli.movimentiAvvProtocollati(tuttiIMov) == 0) {
				SourceBean mansione = DBLoad.getMansioneConEsperienza(cdnLavoratore, transExec);
				if (mansione.getAttribute("prgmansione") == null) {
					ret = true;
				} else {
					ret = false;
				}
			}
		}
		return ret;
	}

	/**
	 * controllo se la data dichiarazione della did si trova in un periodo di mobilità del lavoratore al momento della
	 * dichiarazione di immediata disponibilita
	 * 
	 * @param cdnLavoratore
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static boolean checkDidInMobilita(List rowsMobilita, String dataRif) throws Exception {
		boolean isInMobilita = false;
		String dataInizioMob = "";
		String dataFineMob = "";
		SourceBean sbMobilita = null;

		for (int i = 0; i < rowsMobilita.size(); i++) {
			sbMobilita = (SourceBean) rowsMobilita.get(i);
			dataInizioMob = sbMobilita.getAttribute("datInizio").toString();
			dataFineMob = sbMobilita.getAttribute("datFine").toString();
			if ((DateUtils.compare(dataRif, dataInizioMob) > 0) && (dataFineMob == null || dataFineMob.equals("")
					|| DateUtils.compare(dataFineMob, dataRif) >= 0)) {
				isInMobilita = true;
				break;
			}
		}
		return isInMobilita;
	}

	/**
	 * controllo se la data dichiarazione della did coincide con l'inizio di un periodo di mobilita'
	 * 
	 * @param cdnLavoratore
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static boolean checkDidAnzianitaMobilita(List rowsMobilita, String dataRif) throws Exception {
		boolean isAnzianitaMobilita = false;
		String dataInizioMob = "";
		SourceBean sbMobilita = null;

		for (int i = 0; i < rowsMobilita.size(); i++) {
			sbMobilita = (SourceBean) rowsMobilita.get(i);
			dataInizioMob = sbMobilita.getAttribute("datInizio").toString();
			if (DateUtils.compare(dataRif, dataInizioMob) == 0) {
				isAnzianitaMobilita = true;
				break;
			}
		}
		return isAnzianitaMobilita;
	}

	/**
	 * controllo se la did si trova in un periodo di mobilità del lavoratore durante il ricalcolo stato occupazionale
	 * 
	 * @param cdnLavoratore
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static boolean dichiarazioneDidInMobilita(List rowsMobilita, String dataRif) throws Exception {
		boolean isInMobilita = false;
		String dataInizioMob = "";
		String dataFineMob = "";
		SourceBean sbMobilita = null;

		for (int i = 0; i < rowsMobilita.size(); i++) {
			sbMobilita = (SourceBean) rowsMobilita.get(i);
			dataInizioMob = sbMobilita.getAttribute("datInizio").toString();
			dataFineMob = sbMobilita.getAttribute("datFine").toString();
			if ((DateUtils.compare(dataRif, dataInizioMob) >= 0) && (dataFineMob == null || dataFineMob.equals("")
					|| DateUtils.compare(dataFineMob, dataRif) >= 0)) {
				isInMobilita = true;
				break;
			}
		}
		return isInMobilita;
	}

	/**
	 * Controlla se esistono dichiarazioni di immediata disponiblita' o patti stipulati a partire da dataRif
	 * 
	 * @param cdnLavoratore
	 * @param dataRif
	 *            e' la data a partire dalla quale si estraggono i patti e le dichiarazioni
	 * @param txExec
	 * @return true se non ci sono patti o dichiarazioni stipulate dal lavoratore in data successiva a dataRif, false
	 *         altrimenti
	 * @throws Exception
	 *             se si verifica un errore nell'accesso ai dati
	 */
	public static boolean dichiarazioneAntecedentePossibile(Object cdnLavoratore, String dataRif,
			TransactionQueryExecutor txExec) throws Exception {
		return true;
		/*
		 * gestione precedente Vector v = DBLoad.getDichiarazioniDisponibilita(cdnLavoratore,dataRif, txExec); if
		 * (v.size()>0) return false; v = DBLoad.getPattiStoricizzati(cdnLavoratore, dataRif, txExec); if
		 * (v.size()>0)return false; return true;
		 */
	}

	/**
	 * ritorna un vector con tutti i movimenti a cavallo della data dichiarazione della did e che vanno considerati nel
	 * calcolo del reddito
	 * 
	 * @param rows
	 * @param dataDichiarazione
	 * @return
	 * @throws Exception
	 */
	public static Vector dammiMovimentiCavalloDid(Vector rows, List movimenti, String dataDichiarazione)
			throws Exception {
		Vector rowsReturn = new Vector();
		String dataInizio = "";
		String dataFine = "";
		String codTipoMov = "";
		BigDecimal prgMovPrec = null;
		String codTipoAvviamento = "";
		boolean trovato;
		int rowsSize = rows.size();
		int movimentiSize = 0;
		if (movimenti != null) {
			movimentiSize = movimenti.size();
		}
		for (int i = 0; i < rowsSize; i++) {
			SourceBean mov = (SourceBean) rows.get(i);
			dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			dataFine = mov.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? mov.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			codTipoMov = mov.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
					? mov.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
					: "";
			codTipoAvviamento = mov.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? mov.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// non considerare TIPO AVVIAMENTO Z.09.02(vecchio codice RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (!codTipoAvviamento.equals("Z.09.02")) {
				if (codTipoMov.equalsIgnoreCase("AVV") && mov.getAttribute("MOVIMENTI_PROROGATI") != null) {
					Vector prec = (Vector) mov.getAttribute("MOVIMENTI_PROROGATI");
					// prendo l'ultima proroga o trasformazione
					if (prec.size() > 0) {
						int k = prec.size() - 1;
						SourceBean movimentoUltimo = (SourceBean) prec.get(k);
						dataFine = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
								? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
								: "";
					}
				} else {
					if ((movimentiSize > 0)
							&& (codTipoMov.equalsIgnoreCase("PRO") || codTipoMov.equalsIgnoreCase("TRA"))) {
						prgMovPrec = mov.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC)
								? new BigDecimal(mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC).toString())
								: null;
						while (prgMovPrec != null) {
							MovimentoBean movAppoggio = null;
							trovato = false;
							for (int k = 0; k < movimentiSize; k++) {
								EventoAmministrativo o = (EventoAmministrativo) movimenti.get(k);
								int tipoEvento = o.getTipoEventoAmministrativo();
								if (tipoEvento == EventoAmministrativo.AVVIAMENTO
										|| tipoEvento == EventoAmministrativo.PROROGA
										|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
									movAppoggio = (MovimentoBean) o;
									BigDecimal prgMovAppoggio = movAppoggio.getPrgMovimento();
									if (prgMovAppoggio != null && prgMovAppoggio.equals(prgMovPrec)) {
										trovato = true;
										mov = movAppoggio;
										break;
									}
								}
							}
							if (trovato) {
								dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
								prgMovPrec = mov.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC)
										? new BigDecimal(
												mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC).toString())
										: null;
							} else {
								prgMovPrec = null;
							}
						}
					}
				}
				if (!codTipoMov.equalsIgnoreCase("CES")) {
					if (DateUtils.compare(dataInizio, dataDichiarazione) <= 0 && (dataFine.equals("")
							|| dataFine == null || DateUtils.compare(dataFine, dataDichiarazione) >= 0)) {
						boolean trovatoMov = false;
						BigDecimal prgMovDaInserire = new BigDecimal(
								mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString());
						for (int j = 0; j < rowsReturn.size(); j++) {
							SourceBean movDaControllare = (SourceBean) rowsReturn.get(j);
							BigDecimal prgMovControllare = movDaControllare
									.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
											? new BigDecimal(movDaControllare
													.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString())
											: null;
							if (prgMovControllare != null && prgMovDaInserire.equals(prgMovControllare)) {
								trovatoMov = true;
								break;
							}
						}
						if (!trovatoMov)
							rowsReturn.add(mov);
					}
				}
			}
		}
		return rowsReturn;
	}

	public static Vector getMovimentiApertiDa(Vector movimenti, String data) throws Exception {
		Vector v = new Vector();
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			SourceBean m = (SourceBean) movimenti.get(i);
			String codTipoMov = m.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString();
			String dataFineMovEff = m.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? m.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			String dataInizioMov = m.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			if ((!codTipoMov.equalsIgnoreCase("CES"))
					&& (dataFineMovEff.equals("") || DateUtils.compare(dataFineMovEff, data) >= 0)
					&& (DateUtils.compare(dataInizioMov, data) <= 0)) {
				v.add(m);
			}
		}
		return v;
	}

	public static String getCodMonoTempoMovimento(SourceBean movimento) {
		String codMonoTempo = "";
		if (movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TEMPO)) {
			codMonoTempo = movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString();
		}
		String codTipoMov = movimento.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
				? movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
				: "";
		if (codTipoMov.equalsIgnoreCase("AVV") && movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
			Vector vettSucc = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
			// prendo l'ultima movimento della catena
			if (vettSucc.size() > 0) {
				int indiceUltimo = vettSucc.size() - 1;
				SourceBean movimentoUltimo = (SourceBean) vettSucc.get(indiceUltimo);
				if (movimentoUltimo.containsAttribute(MovimentoBean.DB_COD_MONO_TEMPO)) {
					codMonoTempo = movimentoUltimo.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString();
				}
			}
		}
		return codMonoTempo;
	}

	public static class ERRORI {
		public static final int NO_ERR = 1;
		public static final int NO_ERR_RISCHIO_DISOCCUPAZIONE = 2;
		public static final int TERMINI_NON_SCADUTI = -1;
		public static final int REDDITO_SUPERIORE = -2;
		public static final int DID_IN_PERIODO_MOBILITA = -3;
		public static final int ERR_MOV_RISCHIO_DISOCCUPAZIONE = -4;
		public static final int ERR_DID_NO_SOSPENSIONE = -5;
	}
}
