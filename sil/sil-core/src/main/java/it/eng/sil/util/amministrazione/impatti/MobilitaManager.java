package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.util.Utils;

public class MobilitaManager {
	private SituazioneAmministrativa sitAmm;

	public MobilitaManager(SituazioneAmministrativa sitAmm) {
		this.sitAmm = sitAmm;
	}

	public MobilitaManager() {
	}

	public StatoOccupazionaleBean aggiornaDaMobilita(String dataInizio, String dataFine, String cdnLavoratore,
			Object cdnUtente, boolean forzaRicostruzione, TransactionQueryExecutor transExec, LogBatch logBatch)
			throws Exception {

		RequestContainer requestContainer = new RequestContainer();
		SessionContainer sc = new SessionContainer(false);
		sc.setAttribute("_CDUT_", new BigDecimal(cdnUtente.toString()));
		sc.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
		requestContainer.setSessionContainer(sc);
		SourceBean requestMobilita = new SourceBean("MOBILITA");
		requestMobilita.setAttribute(MobilitaBean.DB_DAT_INIZIO, dataInizio);
		requestMobilita.setAttribute(MobilitaBean.DB_DAT_FINE, dataFine);
		requestMobilita.setAttribute(MobilitaBean.DB_CDNLAVORATORE, cdnLavoratore);
		if (forzaRicostruzione) {
			requestMobilita.setAttribute("FORZA_INSERIMENTO", "true");
			requestMobilita.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
		}
		requestContainer.setServiceRequest(requestMobilita);
		RequestContainer.setRequestContainer(requestContainer);
		return aggiornaDaMobilita(requestMobilita, requestContainer, transExec, logBatch);
	}

	/**
	 * Aggiorna lo stato occupazionale a fronte dell'inserimento della mobilità
	 * 
	 * @param request
	 * @param requestContainer
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public StatoOccupazionaleBean aggiornaDaMobilita(SourceBean requestMobilita, RequestContainer requestContainer,
			TransactionQueryExecutor transExec, LogBatch logBatch) throws Exception {
		// prima debbo controllare lo stato occupazionale precedente la data
		// inizio mobilità
		// nonche' se la data inizio mobilità e' precedente la data di sistema,
		// ovvero se sto' inserendo una
		// mobilità vecchia.
		StatoOccupazionaleBean statoOccupazionale = null;
		String dataRif = (String) requestMobilita.getAttribute("datInizio");
		String cdnLavoratore = Utils.notNull(requestMobilita.getAttribute("cdnLavoratore"));
		String dataAttuale = DateUtils.getNow();
		StatoOccupazionaleBean nuovoStatoOccupazionale = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);

		if (DateUtils.compare(dataRif, dataAttuale) <= 0) {
			// data inizio mobilita non futura
			StatoOccupazionaleBean statoOccupazionaleAperto = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);
			String dataStatoOcc = statoOccupazionaleAperto.getDataInizio();
			boolean ricostruzioneStoria = true;

			if (ricostruzioneStoria) {
				// ricrea la situazione amministrativa
				String dataInizio = dataRif;
				Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
				Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001",
						transExec);
				Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
				Vector movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);

				SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali,
						patti, dids, dataInizio, transExec, requestContainer, logBatch);
				// ricostruzione storia
				sitAmm.ricreaDaMobilita(requestMobilita);
				nuovoStatoOccupazionale = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);
			}
		}

		boolean flag = false;
		if (flag)
			throw new Exception("annullamento operazione per test");
		// valore con cui si va ad aggiornare il prgstatooccupaz relativo alla
		// mobilità.
		return nuovoStatoOccupazionale;
	}

	/**
	 * Metodo invocato in ricostruzione storia quando si incontra l'evento amministrativo mobilita per calcolare il
	 * nuovo stato occupazionale.
	 * 
	 * @param mobilita
	 * @param statoOccIniziale
	 * @param movimentiAperti
	 * @param requestContainer
	 * @param txExecutor
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	public StatoOccupazionaleBean gestisciMobilita(MobilitaBean mobilita, SourceBean cm,
			StatoOccupazionaleBean statoOccIniziale, Vector movimentiAperti, RequestContainer requestContainer,
			Vector vettParametriRicostruzione, Vector configurazioniDefaul_Custom, String DATA_NORMATIVA, String dat150,
			TransactionQueryExecutor txExecutor) throws ControlliException, Exception {

		String dataRif = mobilita.getDataInizio();
		if (DateUtils.compare(dataRif, DATA_NORMATIVA) < 0) {
			dataRif = DATA_NORMATIVA;
		}
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		StatoOccupazionaleBean n = null;
		Vector movApertiPerTirocini = null;
		String flgImpattiTirInMobEff = vettParametriRicostruzione.get(1).toString();
		BigDecimal cdnLav = mobilita.getCdnLavoratore();
		boolean gestione150 = (DateUtils.compare(dataRif, dat150) >= 0);
		switch (statoOccIniziale.getStatoOccupazionaleRagg()) {
		case StatoOccupazionaleBean.RAGG_D:
			switch (statoOccIniziale.getStatoOccupazionale()) {
			case StatoOccupazionaleBean.B1: // sospensione anzianità
			case StatoOccupazionaleBean.A212: // precario
				if (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0) {
					n = new StatoOccupazionaleBean();
					n.setCdnLavoratore(cdnLav);
					n.setStatoOccupazionaleRagg("D");
					n.setStatoOccupazionale("B2"); // in mobilità occupato
					n.setCodMonoProvenienza("B");
					n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
					n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
					n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
					n.setPensionato(statoOccIniziale.getPensionato());
					nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
					nuovoStatoOccupazionale.setChanged(true);
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
					break;
				} else {
					nuovoStatoOccupazionale = statoOccIniziale;
					break;
				}

			case StatoOccupazionaleBean.A21:
				movApertiPerTirocini = Controlli.getMovimentiAperti(movimentiAperti, dataRif);
				if (movApertiPerTirocini.size() > 0 && Controlli.soloTirocini(movApertiPerTirocini)
						&& flgImpattiTirInMobEff.equalsIgnoreCase("S")) {
					n = new StatoOccupazionaleBean();
					n.setCdnLavoratore(cdnLav);
					n.setStatoOccupazionaleRagg("D");
					n.setStatoOccupazionale("A213");
					n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
					n.setDataCalcoloAnzianita(dataRif);
					n.setDataCalcoloMesiSosp(dataRif);
					n.setDataAnzianita(dataRif);
					n.setPensionato(statoOccIniziale.getPensionato());
					nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
					nuovoStatoOccupazionale.setChanged(true);
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
				} else {
					if (movApertiPerTirocini.size() > 0 && !Controlli.soloTirocini(movApertiPerTirocini)) {
						if (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0) {
							n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(cdnLav);
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("B2");
							n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
							n.setDataCalcoloAnzianita(dataRif);
							n.setDataCalcoloMesiSosp(dataRif);
							n.setDataAnzianita(dataRif);
							n.setPensionato(statoOccIniziale.getPensionato());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
						} else {
							// bisogna vedere il reddito dei movimenti e creare lo stato occupazionale in base al
							// reddito
							// gestione post decreto fornero 2014, alla data inizio mobilità non esiste
							// di sicuro un movimento con contratto parasubordinato (che non fa sospensione), altrimenti
							// nella fase di AllineamentoMobilità la mobilità stessa, alla data inizio mobilità, sarebbe
							// già decaduta
							double limiteAttuale = getLimiteAttuale(movApertiPerTirocini, dataRif, cm);
							boolean superamentoReddito = controllaSuperamentoReddito(movApertiPerTirocini, dataRif,
									limiteAttuale);
							if (superamentoReddito) {
								n = new StatoOccupazionaleBean();
								n.setCdnLavoratore(cdnLav);
								n.setStatoOccupazionaleRagg("D");
								n.setStatoOccupazionale("B1");
								n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
								n.setDataCalcoloAnzianita(dataRif);
								n.setDataCalcoloMesiSosp(dataRif);
								n.setDataAnzianita(dataRif);
								n.setPensionato(statoOccIniziale.getPensionato());
								nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
								nuovoStatoOccupazionale.setChanged(true);
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer,
										txExecutor);
							} else {
								n = new StatoOccupazionaleBean();
								n.setCdnLavoratore(cdnLav);
								n.setStatoOccupazionaleRagg("D");
								n.setStatoOccupazionale("A212");
								n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
								n.setDataCalcoloAnzianita(dataRif);
								n.setDataCalcoloMesiSosp(dataRif);
								n.setDataAnzianita(dataRif);
								n.setPensionato(statoOccIniziale.getPensionato());
								nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
								nuovoStatoOccupazionale.setChanged(true);
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer,
										txExecutor);
							}
						}
					}
				}
				break;

			default:
				break;
			}
			if (nuovoStatoOccupazionale == null) {
				nuovoStatoOccupazionale = statoOccIniziale;
			}
			break;

		case StatoOccupazionaleBean.RAGG_I:
			n = new StatoOccupazionaleBean();
			n.setCdnLavoratore(cdnLav);
			n.setStatoOccupazionaleRagg("D");
			n.setStatoOccupazionale("A21"); // disoccupato
			n.setCodMonoProvenienza("B");
			n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
			n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
			n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
			n.setPensionato(statoOccIniziale.getPensionato());
			nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
			nuovoStatoOccupazionale.setChanged(true);
			DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
			break;

		case StatoOccupazionaleBean.RAGG_A:
			movApertiPerTirocini = Controlli.getMovimentiAperti(movimentiAperti, dataRif);
			if (movApertiPerTirocini.size() == 0
					|| (Controlli.soloTirocini(movApertiPerTirocini)) && !flgImpattiTirInMobEff.equalsIgnoreCase("S")) {
				n = new StatoOccupazionaleBean();
				n.setCdnLavoratore(cdnLav);
				n.setStatoOccupazionaleRagg("D");
				n.setStatoOccupazionale("A21");
				n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
				n.setDataCalcoloAnzianita(dataRif);
				n.setDataCalcoloMesiSosp(dataRif);
				n.setDataAnzianita(dataRif);
				n.setPensionato(statoOccIniziale.getPensionato());
				nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
				nuovoStatoOccupazionale.setChanged(true);
				DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
			} else {
				// movimenti a t.d. o t.i. part time allora In Mobilita Occupato
				// movimento a t.i full time Occupato (se esiste qualche mov a
				// t.i. precedente alla mobilità,
				// la data inizio mobilità e data fine mobilità viene anticipata
				// al giorno precedente l'inizio
				// del movimento a tempo indeterminato).
				if (Controlli.soloTirocini(movApertiPerTirocini)) {
					n = new StatoOccupazionaleBean();
					n.setCdnLavoratore(cdnLav);
					n.setStatoOccupazionaleRagg("D");
					n.setStatoOccupazionale("A213");
					n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
					n.setDataCalcoloAnzianita(dataRif);
					n.setDataCalcoloMesiSosp(dataRif);
					n.setDataAnzianita(dataRif);
					n.setPensionato(statoOccIniziale.getPensionato());
					nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
					nuovoStatoOccupazionale.setChanged(true);
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
				} else {
					if (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0) {
						n = new StatoOccupazionaleBean();
						n.setCdnLavoratore(cdnLav);
						n.setStatoOccupazionaleRagg("D");
						n.setStatoOccupazionale("B2");
						n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
						n.setDataCalcoloAnzianita(dataRif);
						n.setDataCalcoloMesiSosp(dataRif);
						n.setDataAnzianita(dataRif);
						n.setPensionato(statoOccIniziale.getPensionato());
						nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
						nuovoStatoOccupazionale.setChanged(true);
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
					} else {
						// vedere il reddito
						double limiteAttuale = getLimiteAttuale(movApertiPerTirocini, dataRif, cm);
						boolean superamentoReddito = controllaSuperamentoReddito(movApertiPerTirocini, dataRif,
								limiteAttuale);
						if (superamentoReddito) {
							n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(cdnLav);
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("B1");
							n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
							n.setDataCalcoloAnzianita(dataRif);
							n.setDataCalcoloMesiSosp(dataRif);
							n.setDataAnzianita(dataRif);
							n.setPensionato(statoOccIniziale.getPensionato());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
						} else {
							n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(cdnLav);
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("A212");
							n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
							n.setDataCalcoloAnzianita(dataRif);
							n.setDataCalcoloMesiSosp(dataRif);
							n.setDataAnzianita(dataRif);
							n.setPensionato(statoOccIniziale.getPensionato());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
						}
					}
				}
			}
			if (nuovoStatoOccupazionale != null) {
				if (nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
						|| nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
					// riapertura eventuale patto
					sitAmm.riapriPattoMobilita(dataRif, cdnLav.toString());
				}
			}
			break;

		case StatoOccupazionaleBean.RAGG_O:
			// movimenti a t.d. o t.i. part time allora In Mobilita Occupato
			// movimento a t.i full time Occupato (se esiste qualche mov a t.i.
			// precedente alla mobilità,
			// la data inizio mobilità e data fine mobilità viene anticipata al
			// giorno precedente l'inizio
			// del movimento a tempo indeterminato).
			movApertiPerTirocini = Controlli.getMovimentiAperti(movimentiAperti, dataRif);
			if (movApertiPerTirocini.size() == 0
					|| (Controlli.soloTirocini(movApertiPerTirocini)) && !flgImpattiTirInMobEff.equalsIgnoreCase("S")) {
				n = new StatoOccupazionaleBean();
				n.setCdnLavoratore(cdnLav);
				n.setStatoOccupazionaleRagg("D");
				n.setStatoOccupazionale("A21");
				n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
				n.setDataCalcoloAnzianita(dataRif);
				n.setDataCalcoloMesiSosp(dataRif);
				n.setDataAnzianita(dataRif);
				n.setPensionato(statoOccIniziale.getPensionato());
				nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
				nuovoStatoOccupazionale.setChanged(true);
				DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
			} else {
				if (Controlli.soloTirocini(movApertiPerTirocini)) {
					n = new StatoOccupazionaleBean();
					n.setCdnLavoratore(cdnLav);
					n.setStatoOccupazionaleRagg("D");
					n.setStatoOccupazionale("A213");
					n.setCodMonoProvenienza("B"); // provenienza dalla
													// mobilità
					n.setDataCalcoloAnzianita(dataRif);
					n.setDataCalcoloMesiSosp(dataRif);
					n.setDataAnzianita(dataRif);
					n.setPensionato(statoOccIniziale.getPensionato());
					nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
					nuovoStatoOccupazionale.setChanged(true);
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
				} else {
					if (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0) {
						n = new StatoOccupazionaleBean();
						n.setCdnLavoratore(cdnLav);
						n.setStatoOccupazionaleRagg("D");
						n.setStatoOccupazionale("B2");
						n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
						n.setDataCalcoloAnzianita(dataRif);
						n.setDataCalcoloMesiSosp(dataRif);
						n.setDataAnzianita(dataRif);
						n.setPensionato(statoOccIniziale.getPensionato());
						nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
						nuovoStatoOccupazionale.setChanged(true);
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
					} else {
						// controlla il reddito, alla data inizio mobilità non esiste
						// di sicuro un movimento con contratto parasubordinato (che non fa sospensione), altrimenti
						// nella fase di AllineamentoMobilità la mobilità stessa, alla data inizio mobilità, sarebbe già
						// decaduta
						double limiteAttuale = getLimiteAttuale(movApertiPerTirocini, dataRif, cm);
						boolean superamentoReddito = controllaSuperamentoReddito(movApertiPerTirocini, dataRif,
								limiteAttuale);
						if (superamentoReddito) {
							n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(cdnLav);
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("B1");
							n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
							n.setDataCalcoloAnzianita(dataRif);
							n.setDataCalcoloMesiSosp(dataRif);
							n.setDataAnzianita(dataRif);
							n.setPensionato(statoOccIniziale.getPensionato());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
						} else {
							n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(cdnLav);
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("A212");
							n.setCodMonoProvenienza("B"); // provenienza dalla mobilità
							n.setDataCalcoloAnzianita(dataRif);
							n.setDataCalcoloMesiSosp(dataRif);
							n.setDataAnzianita(dataRif);
							n.setPensionato(statoOccIniziale.getPensionato());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataRif, requestContainer, txExecutor);
						}
					}
				}
			}
			if (nuovoStatoOccupazionale != null) {
				if (nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
						|| nuovoStatoOccupazionale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
					// riapertura eventuale patto
					sitAmm.riapriPattoMobilita(dataRif, cdnLav.toString());
				}
			}
			break;
		}
		return nuovoStatoOccupazionale;
	}

	public double getLimiteAttuale(Vector movAperti, String dataRif, SourceBean cm) throws Exception {
		double limiteAttuale = 0;
		int nSize = movAperti.size();
		LimiteRedditoExt limiteReddito = new LimiteRedditoExt(dataRif);
		for (int i = 0; i < nSize; i++) {
			SourceBean movimento = (SourceBean) movAperti.get(i);
			if (Controlli.inCollocamentoMirato(cm, dataRif)) {
				if (limiteAttuale < limiteReddito.get(LimiteReddito.CM)) {
					limiteAttuale = limiteReddito.get(LimiteReddito.CM);
				}
			} else {
				switch (Contratto.getTipoContratto(movimento)) {
				case Contratto.AUTONOMO:
					// controllare se nella data inizio movimento ci sono
					// rapporti di lavoro dipendente aperti. In tal caso bisogna considerare
					// limite reddito di lavoro dipendente
					boolean esisteMovimentoDip = false;
					if (movAperti.size() > 0) {
						esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(dataRif, movAperti);
					}
					if (esisteMovimentoDip) {
						if (limiteAttuale < limiteReddito.get(LimiteReddito.DIPENDENTE)) {
							limiteAttuale = limiteReddito.get(LimiteReddito.DIPENDENTE);
						}
					} else {
						if (limiteAttuale < limiteReddito.get(LimiteReddito.AUTONOMO)) {
							limiteAttuale = limiteReddito.get(LimiteReddito.AUTONOMO);
						}
					}
					break;
				case Contratto.COCOCO:
				case Contratto.DIP_TD:
				case Contratto.DIP_TI:
					// controllare se nella data inizio movimento ci sono
					// rapporti di lavoro dipendente aperti. In tal caso bisogna considerare
					// limite reddito di lavoro dipendente
					if (limiteAttuale < limiteReddito.get(LimiteReddito.DIPENDENTE)) {
						limiteAttuale = limiteReddito.get(LimiteReddito.DIPENDENTE);
					}
					break;
				}
			}
		}
		return limiteAttuale;
	}

	public boolean controllaSuperamentoReddito(Vector movAperti, String dataRif, double limiteAttuale)
			throws Exception {
		int ggDiLavoro = 0;
		int ggDiLavoroAnnoSuccessivo = 0;
		BigDecimal retribuzione = null;
		int annoInizioMov = 0;
		int annoReddito = DateUtils.getAnno(dataRif);
		boolean superamentoReddito = false;
		Reddito reddito = new Reddito(0, 0);
		int nSize = movAperti.size();
		Map<BigDecimal, String> mapProrogati = new HashMap<BigDecimal, String>();
		for (int i = 0; (!superamentoReddito && i < nSize); i++) {
			SourceBean movAnno = (SourceBean) movAperti.get(i);
			String dataInizioMov = movAnno.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			String dataFineMov = movAnno.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? movAnno.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			String codTipoMov = movAnno.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString();
			BigDecimal prgMovimento = (BigDecimal) movAnno.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			if (!mapProrogati.containsKey(prgMovimento)) {
				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_ASSUNZIONE)) {
					if (movAnno.getAttribute("MOVIMENTI_PROROGATI") != null) {
						Vector prec = (Vector) movAnno.getAttribute("MOVIMENTI_PROROGATI");
						SourceBean movimentoAvv = null;
						SourceBean movimentoSucc = null;
						String dataInizioPrec = "";
						String dataInizioSucc = "";
						String dataFinePrec = "";
						BigDecimal retribuzionePrec = null;
						int precSize = prec.size();
						for (int k = 0; (!superamentoReddito && k < precSize); k++) {
							movimentoAvv = (SourceBean) prec.get(k);
							BigDecimal prgMovProrogato = (BigDecimal) movimentoAvv
									.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
							mapProrogati.put(prgMovProrogato, "S");
							int kSucc = k + 1;
							dataInizioPrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
							if (movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null) {
								dataFinePrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
										.toString();
							} else {
								dataFinePrec = null;
							}
							// Se il movimento successivo nel vettore dei movimenti prorogati ha la stessa data inizio
							// del movimento corrente nel vettore dei prorogati, allora al fine del reddito non lo
							// considero
							if (kSucc < precSize) {
								movimentoSucc = (SourceBean) prec.get(kSucc);
								dataInizioSucc = movimentoSucc.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
								if (DateUtils.compare(dataInizioPrec, dataInizioSucc) == 0) {
									continue;
								}
							}
							// prendo la retribuzione
							if (DateUtils.getAnno(dataInizioPrec) < annoReddito) {
								dataInizioPrec = "01/01/" + annoReddito;
							}
							if (dataFinePrec == null || dataFinePrec.equals("")
									|| (DateUtils.getAnno(dataFinePrec) > annoReddito)) {
								dataFinePrec = "31/12/" + annoReddito;
							}
							retribuzionePrec = Retribuzione.getRetribuzioneMen(movimentoAvv);
							annoInizioMov = DateUtils.getAnno(dataInizioPrec);
							if (annoInizioMov <= annoReddito && (dataFinePrec == null || dataFinePrec.equals("")
									|| DateUtils.getAnno(dataFinePrec) >= annoReddito)) {
								if (retribuzionePrec != null) {
									ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec, dataFinePrec,
											dataRif);
									ggDiLavoroAnnoSuccessivo = ControlliExt
											.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataRif);
									reddito.aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo,
											retribuzionePrec.doubleValue());
									if (reddito.getRedditoAnno() > limiteAttuale) {
										superamentoReddito = true;
									}
								} else {
									superamentoReddito = true;
								}
							}
						}
					} else {
						if (DateUtils.getAnno(dataInizioMov) < annoReddito) {
							dataInizioMov = "01/01/" + annoReddito;
						}
						if (dataFineMov.equals("") || (DateUtils.getAnno(dataFineMov) > annoReddito)) {
							dataFineMov = "31/12/" + annoReddito;
						}

						ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioMov, dataFineMov, dataRif);
						retribuzione = Retribuzione.getRetribuzioneMen(movAnno);
						if (retribuzione != null) {
							reddito.aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());
							if (reddito.getRedditoAnno() > limiteAttuale) {
								superamentoReddito = true;
							}
						} else {
							superamentoReddito = true;
						}
					}
				} else {
					if (DateUtils.getAnno(dataInizioMov) < annoReddito) {
						dataInizioMov = "01/01/" + annoReddito;
					}
					if (dataFineMov.equals("") || (DateUtils.getAnno(dataFineMov) > annoReddito)) {
						dataFineMov = "31/12/" + annoReddito;
					}

					ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioMov, dataFineMov, dataRif);
					retribuzione = Retribuzione.getRetribuzioneMen(movAnno);
					if (retribuzione != null) {
						reddito.aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());
						if (reddito.getRedditoAnno() > limiteAttuale) {
							superamentoReddito = true;
						}
					} else {
						superamentoReddito = true;
					}
				}
			}
		}
		return superamentoReddito;
	}

	/**
	 * Metodo invocato in ricostruzione storia quando si incontra l'evento amministrativo chiusura mobilita per
	 * calcolare il nuovo stato occupazionale.
	 * 
	 * @param movimenti
	 * @param chiusuraMobilita
	 * @param statoOccIniziale
	 * @return
	 * @throws Exception
	 */
	public StatoOccupazionaleBean calcola(Vector movimenti, ChiusuraMobilitaBean chiusuraMobilita,
			String dataInizioMobilita, StatoOccupazionaleBean statoOccIniziale, List dids, String dat150,
			StatoOccupazionaleManager2 statoOccManager, Vector listaDisabiliCM) throws Exception {

		String dataFineMov = "";
		String dataInizioOriginale = "";
		String dataFineMobilita = chiusuraMobilita.getDataInizio();
		String dataInizioStatoOcc = dataFineMobilita;
		boolean gestioneFornero = (DateUtils.compare(dataFineMobilita,
				MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0);
		boolean gestione150 = (DateUtils.compare(dataFineMobilita, dat150) >= 0);
		boolean iscrittoCM = false;
		if (listaDisabiliCM != null && listaDisabiliCM.size() > 0) {
			iscrittoCM = Controlli.inCollocamentoMiratoAllaData(listaDisabiliCM, dataFineMobilita);
		}
		String cdnlavoratore = chiusuraMobilita.getCdnLavoratore().toString();
		boolean exit = false;
		boolean isTempoIndeterminato = false;
		boolean noReddito = false; // reddito = null per i movimenti a T.D.
		boolean noRedditoTI = false; // reddito = null per i movimenti a T.I.
		boolean trovatoMovTIDecadenza = false;
		StatoOccupazionaleBean statoOccupazionale = (StatoOccupazionaleBean) statoOccIniziale.clone();
		StatoOccupazionaleBean statoOccupazionaleMovTI = new StatoOccupazionaleBean();
		MovimentoBean movimento = null;
		LimiteRedditoExt limiteReddito = null;
		limiteReddito = new LimiteRedditoExt(dataInizioStatoOcc);
		double limiteAnnoSuccessivoTotal = 0;
		double limiteAnnoSuccessivo = 0;
		int numeroMesiTotal = 0;
		int numeroMesi = 0;
		int ggDiLavoro = 0;
		int ggDiLavoroAnnoSuccessivo = 0;
		String dataInizioAnnoMobilita = "";
		String dataFineAnnoMobilita = "";
		boolean isCategoriaParticolareTD = true;
		String dataPrecMobilita = DateUtils.giornoPrecedente(dataInizioMobilita);
		boolean didTrovata = true;
		boolean isCategoriaParticolareCurr = true;
		movimenti = NormalizzaMovimentiStessaData(movimenti);
		TransactionQueryExecutor transExec = null;
		if (statoOccManager != null) {
			transExec = statoOccManager.getTxExecutor();
		}
		for (int i = 0; i < movimenti.size(); i++) {
			isTempoIndeterminato = false;
			didTrovata = true;
			movimento = (MovimentoBean) movimenti.get(i);
			numeroMesi = 0;
			ggDiLavoro = 0;
			ggDiLavoroAnnoSuccessivo = 0;
			dataInizioOriginale = movimento.getDataInizio();
			dataFineMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			SourceBean sbcm = null;
			CmBean cm = null;
			if (movimento != null) {
				if (statoOccManager != null && statoOccManager.getCm() != null) {
					cm = statoOccManager.getCm();
				} else {
					sbcm = DBLoad.getCollocamentoMirato(movimento, transExec);
					if (sbcm != null) {
						cm = new CmBean(sbcm);
						if (statoOccManager != null) {
							statoOccManager.setCm(cm);
						}

					}
				}
			}
			// calcolo del reddito
			if (Controlli.inCollocamentoMirato(cm, dataInizioStatoOcc)) {
				if (statoOccupazionale.getLimiteReddito() < limiteReddito.get(LimiteReddito.CM)) {
					statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
				}
			} else {
				switch (Contratto.getTipoContratto(movimento)) {
				case Contratto.AUTONOMO:
				case Contratto.COCOCO:
					// controllare se nella data inizio movimento ci sono
					// rapporti di lavoro dipendente aperti. In tal caso bisogna
					// considerare
					// limite reddito di lavoro dipendente
					boolean esisteMovimentoDip = false;
					if (movimenti.size() > 0) {
						esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(dataFineMobilita, movimenti);
					}
					if (esisteMovimentoDip) {
						if (statoOccupazionale.getLimiteReddito() < limiteReddito.get(LimiteReddito.DIPENDENTE))
							statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
					} else {
						if (statoOccupazionale.getLimiteReddito() < limiteReddito.get(LimiteReddito.AUTONOMO))
							statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.AUTONOMO));
					}
					break;

				case Contratto.DIP_TD:
				case Contratto.DIP_TI:
					if (statoOccupazionale.getLimiteReddito() < limiteReddito.get(LimiteReddito.DIPENDENTE))
						statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
					break;
				}
			}

			if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("AVV")
					&& movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector prec = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
				SourceBean movimentoAvv = null;
				SourceBean movimentoSucc = null;
				String dataInizioPrec = "";
				String dataInizioSucc = "";
				String dataFinePrec = "";
				String dataFineUltimoMovPro = null;
				String dataInizioPrimoMovPro = null;
				if (prec.size() > 0) {
					movimentoAvv = (SourceBean) prec.get(prec.size() - 1);
					dataFineUltimoMovPro = (String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
					movimentoAvv = (SourceBean) prec.get(0);
					dataInizioPrimoMovPro = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
					didTrovata = cercaDid(dataInizioPrimoMovPro, dataFineUltimoMovPro, dataInizioMobilita);
				}

				if (gestione150 && !iscrittoCM) {
					isCategoriaParticolareCurr = Controlli.isCategoriaParticolareDecreto150(movimento,
							dataFineMobilita);
					isCategoriaParticolareTD = isCategoriaParticolareTD && isCategoriaParticolareCurr;
				} else {
					if (gestioneFornero) {
						isCategoriaParticolareCurr = Controlli.isCategoriaParticolareDecretoFornero(movimento,
								dataFineMobilita, true);
						isCategoriaParticolareTD = isCategoriaParticolareTD && isCategoriaParticolareCurr;
					}
				}
				BigDecimal retribuzionePrec = null;
				int precSize = prec.size();

				for (int k = 0; k < precSize; k++) {
					movimentoAvv = (SourceBean) prec.get(k);
					int kSucc = k + 1;
					dataInizioPrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
					dataFinePrec = (String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
					// Se il movimento successivo nel vettore dei movimenti prorogati ha la stessa data inizio
					// del movimento corrente nel vettore dei prorogati, allora al fine del reddito non lo considero.
					// Questa situazione si può verificare con le trasformazioni DL (distacco lavoratore)
					if (kSucc < precSize) {
						movimentoSucc = (SourceBean) prec.get(kSucc);
						dataInizioSucc = movimentoSucc.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
						if (DateUtils.compare(dataInizioPrec, dataInizioSucc) == 0) {
							continue;
						}
					}

					// prendo la retribuzione
					retribuzionePrec = Retribuzione.getRetribuzioneMen(movimentoAvv);

					if (DateUtils.getAnno(dataInizioStatoOcc) < DateUtils
							.getAnno(MessageCodes.General.DATA_DECRETO_FORNERO_2014)) {
						if (DateUtils.compare(dataInizioPrec, dataInizioMobilita) < 0) {

							if (didTrovata) {
								if (DateUtils.getAnno(dataInizioPrec) < DateUtils.getAnno(dataInizioMobilita)) {
									dataInizioAnnoMobilita = "01/01/" + DateUtils.getAnno(dataInizioMobilita);
									movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioAnnoMobilita);
								} else {
									dataInizioAnnoMobilita = dataInizioPrec;
								}

								if (dataFinePrec == null || dataFinePrec.equals("")
										|| DateUtils.compare(dataFinePrec, dataInizioMobilita) >= 0) {
									if (DateUtils.getAnno(dataPrecMobilita) < DateUtils
											.getAnno(dataInizioAnnoMobilita)) {
										dataFineAnnoMobilita = dataInizioAnnoMobilita;
									} else {
										dataFineAnnoMobilita = dataPrecMobilita;
									}
									if (movimentoAvv.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
										movimentoAvv.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
												dataFineAnnoMobilita);
									} else {
										movimentoAvv.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
												dataFineAnnoMobilita);
									}
									ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(
											dataInizioAnnoMobilita, dataFineAnnoMobilita, dataInizioAnnoMobilita);
									numeroMesi = numeroMesi + Controlli.numeroMesiDiLavoroFineMobilita(movimentoAvv);
								} else {
									if (DateUtils.compare(dataFinePrec, dataInizioMobilita) < 0) {
										if (DateUtils.getAnno(dataFinePrec) == DateUtils.getAnno(dataInizioMobilita)) {
											ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(
													dataInizioAnnoMobilita, dataFinePrec, dataInizioAnnoMobilita);
											numeroMesi = numeroMesi
													+ Controlli.numeroMesiDiLavoroFineMobilita(movimentoAvv);
										} else {
											ggDiLavoro = ggDiLavoro + 0;
											numeroMesi = numeroMesi + 0;
										}
									}
								}
								movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioPrec);
							}

							if (dataFinePrec == null || dataFinePrec.equals("")) {
								if (movimentoAvv.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
									movimentoAvv.delAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
								}
							} else {
								movimentoAvv.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFinePrec);
							}

							if (dataFinePrec != null && !dataFinePrec.equals("")
									&& DateUtils.compare(dataFinePrec, dataFineMobilita) >= 0) {
								ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(dataInizioStatoOcc,
										dataFinePrec, dataInizioStatoOcc);
								ggDiLavoroAnnoSuccessivo = ggDiLavoroAnnoSuccessivo + ControlliExt
										.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataInizioStatoOcc);
								movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioStatoOcc);
								numeroMesi = numeroMesi + Controlli.numeroMesiDiLavoroFineMobilita(movimentoAvv);
								movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioPrec);
							} else {
								if (dataFinePrec == null || dataFinePrec.equals("")) {
									ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(dataInizioStatoOcc,
											dataFinePrec, dataInizioStatoOcc);
									ggDiLavoroAnnoSuccessivo = ggDiLavoroAnnoSuccessivo + ControlliExt
											.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataInizioStatoOcc);
									numeroMesi = Integer.MAX_VALUE;
								}
							}

						} else {
							if (dataFinePrec == null || dataFinePrec.equals("")
									|| DateUtils.compare(dataFinePrec, dataFineMobilita) >= 0) {
								String dataCalcoloMesi = dataInizioPrec;
								if (DateUtils.compare(dataInizioStatoOcc, dataInizioPrec) > 0) {
									dataCalcoloMesi = dataInizioStatoOcc;
									movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioStatoOcc);
								}
								numeroMesi = numeroMesi + Controlli.numeroMesiDiLavoroFineMobilita(movimentoAvv);
								ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(dataCalcoloMesi,
										dataFinePrec, dataCalcoloMesi);
								ggDiLavoroAnnoSuccessivo = ggDiLavoroAnnoSuccessivo + ControlliExt
										.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataCalcoloMesi);
								movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioPrec);
							}
						}
					} else { // else if (DateUtils.getAnno(dataInizioStatoOcc) <
								// DateUtils.getAnno(MessageCodes.General.DATA_DECRETO_FORNERO_2014))
						int annoStatoOccupazionale = DateUtils.getAnno(dataInizioStatoOcc);
						int annoFineMovimento = 0;
						if (dataFinePrec == null || dataFinePrec.equals("")) {
							annoFineMovimento = annoStatoOccupazionale;
						} else {
							annoFineMovimento = DateUtils.getAnno(dataFinePrec);
						}

						if (DateUtils.compare(dataInizioPrec, dataInizioMobilita) < 0) {
							if (annoFineMovimento >= annoStatoOccupazionale) {
								if (didTrovata) {
									ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec,
											dataFinePrec, dataInizioPrec);
									ggDiLavoroAnnoSuccessivo = ggDiLavoroAnnoSuccessivo + ControlliExt
											.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataInizioPrec);
									if (dataFinePrec == null || dataFinePrec.equals("")) {
										numeroMesi = Integer.MAX_VALUE;
									} else {
										numeroMesi = numeroMesi
												+ Controlli.numeroMesiDiLavoro(dataInizioPrec, dataFinePrec);
									}
								}
							}
						} else {
							if (dataFinePrec == null || dataFinePrec.equals("")) {
								numeroMesi = Integer.MAX_VALUE;
							} else {
								numeroMesi = numeroMesi + Controlli.numeroMesiDiLavoro(dataInizioPrec, dataFinePrec);
							}
							if (annoFineMovimento >= annoStatoOccupazionale) {
								ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec,
										dataFinePrec, dataInizioPrec);
								ggDiLavoroAnnoSuccessivo = ggDiLavoroAnnoSuccessivo + ControlliExt
										.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataInizioPrec);
							}
						}
					}

					if (ggDiLavoro > 0 || ggDiLavoroAnnoSuccessivo > 0) {
						String codMonoTipoAss = movimentoAvv.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
								? movimentoAvv.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
								: "";
						String codMonoTempo = movimentoAvv.containsAttribute(MovimentoBean.DB_COD_MONO_TEMPO)
								? movimentoAvv.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString()
								: "";
						if ((Contratto.getTipoContratto(movimentoAvv) == Contratto.DIP_TI)
								|| (codMonoTipoAss.equalsIgnoreCase("N") && codMonoTempo.equalsIgnoreCase("I"))) {
							if (!gestioneFornero) {
								trovatoMovTIDecadenza = true;
								isTempoIndeterminato = true;
								// se nel momento in cui trovo il tempo
								// indeterminato il reddito è
								// gia alto allora devo creare uno stato
								// occupazionale di Occupato
								if (noReddito) {
									exit = true;
								}
							} else {
								if (!isCategoriaParticolareCurr) {
									trovatoMovTIDecadenza = true;
									isTempoIndeterminato = true;
									if (noReddito) {
										exit = true;
									}
								}
							}
						}
						if (retribuzionePrec == null) {
							if (dataFinePrec == null || dataFinePrec.equals("")
									|| DateUtils.compare(dataFinePrec, dataFineMobilita) >= 0) {
								if ((Contratto.getTipoContratto(movimentoAvv) == Contratto.DIP_TI)
										|| (codMonoTipoAss.equalsIgnoreCase("N")
												&& codMonoTempo.equalsIgnoreCase("I"))) {
									if (!gestioneFornero) {
										noRedditoTI = true;
									} else {
										if (!isCategoriaParticolareCurr) {
											noRedditoTI = true;
										}
									}
								} else {
									noReddito = true;
								}
							}
						} else {
							statoOccupazionale.aggiorna(ggDiLavoro, retribuzionePrec.doubleValue());
							statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
									retribuzionePrec.doubleValue());
							if ((Contratto.getTipoContratto(movimentoAvv) == Contratto.DIP_TI)
									|| (codMonoTipoAss.equalsIgnoreCase("N") && codMonoTempo.equalsIgnoreCase("I"))) {
								if (!gestioneFornero) {
									statoOccupazionaleMovTI.aggiorna(ggDiLavoro, retribuzionePrec.doubleValue());
									statoOccupazionaleMovTI.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
											retribuzionePrec.doubleValue());
								} else {
									if (!isCategoriaParticolareCurr) {
										statoOccupazionaleMovTI.aggiorna(ggDiLavoro, retribuzionePrec.doubleValue());
										statoOccupazionaleMovTI.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
												retribuzionePrec.doubleValue());
									}
								}
							}
						}
					}
				} // end for (int k = 0; k < precSize; k++)

				if (!gestioneFornero) {
					if (dataFineUltimoMovPro != null && !dataFineUltimoMovPro.equals("")) {
						isCategoriaParticolareCurr = Controlli.isCategoriaParticolare(numeroMesi, movimento);
						isCategoriaParticolareTD = isCategoriaParticolareTD && isCategoriaParticolareCurr;
					}
				}
			} else { // else if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("AVV") &&
						// movimento.getAttribute("MOVIMENTI_PROROGATI") != null)
				if (DateUtils.getAnno(dataInizioStatoOcc) < DateUtils
						.getAnno(MessageCodes.General.DATA_DECRETO_FORNERO_2014)) {
					if (DateUtils.compare(dataInizioOriginale, dataInizioMobilita) < 0) {
						didTrovata = cercaDid(dataInizioOriginale, dataFineMov, dataInizioMobilita);
						if (didTrovata) {
							if (DateUtils.getAnno(dataInizioOriginale) < DateUtils.getAnno(dataInizioMobilita)) {
								dataInizioAnnoMobilita = "01/01/" + DateUtils.getAnno(dataInizioMobilita);
								movimento.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioAnnoMobilita);
							} else {
								dataInizioAnnoMobilita = dataInizioOriginale;
							}

							if (dataFineMov == null || dataFineMov.equals("")
									|| (dataFineMov != null && !dataFineMov.equals("")
											&& DateUtils.compare(dataFineMov, dataInizioMobilita) >= 0)) {
								if (DateUtils.getAnno(dataPrecMobilita) < DateUtils.getAnno(dataInizioAnnoMobilita)) {
									dataFineAnnoMobilita = dataInizioAnnoMobilita;
								} else {
									dataFineAnnoMobilita = dataPrecMobilita;
								}
								if (movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
									movimento.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFineAnnoMobilita);
								} else {
									movimento.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFineAnnoMobilita);
								}
								ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioAnnoMobilita,
										dataFineAnnoMobilita, dataInizioAnnoMobilita);
								numeroMesi = Controlli.numeroMesiDiLavoroFineMobilita(movimento);
							} else {
								if (DateUtils.compare(dataFineMov, dataInizioMobilita) < 0) {
									if (DateUtils.getAnno(dataFineMov) == DateUtils.getAnno(dataInizioMobilita)) {
										ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioAnnoMobilita,
												dataFineMov, dataInizioAnnoMobilita);
										numeroMesi = Controlli.numeroMesiDiLavoroFineMobilita(movimento);
									} else {
										ggDiLavoro = 0;
										numeroMesi = 0;
									}
								}
							}
							movimento.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioOriginale);
						}

						if (dataFineMov == null || dataFineMov.equals("")) {
							if (movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
								movimento.delAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
							}
						} else {
							movimento.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFineMov);
						}

						if (dataFineMov != null && !dataFineMov.equals("")
								&& DateUtils.compare(dataFineMov, dataFineMobilita) >= 0) {
							ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(dataInizioStatoOcc,
									dataFineMov, dataInizioStatoOcc);
							movimento.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioStatoOcc);
							numeroMesi = numeroMesi + Controlli.numeroMesiDiLavoroFineMobilita(movimento);
							ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFineMov,
									dataInizioStatoOcc);
							movimento.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioOriginale);
						} else {
							if (dataFineMov == null || dataFineMov.equals("")) {
								ggDiLavoro = ggDiLavoro + ControlliExt.getNumeroGiorniDiLavoro(dataInizioStatoOcc,
										dataFineMov, dataInizioStatoOcc);
								ggDiLavoroAnnoSuccessivo = ControlliExt
										.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFineMov, dataInizioStatoOcc);
								numeroMesi = Integer.MAX_VALUE;
							}
						}
					} else {
						if (dataFineMov == null || dataFineMov.equals("")
								|| DateUtils.compare(dataFineMov, dataFineMobilita) >= 0) {
							movimento.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioStatoOcc);
							numeroMesi = Controlli.numeroMesiDiLavoroFineMobilita(movimento);
							ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioStatoOcc, dataFineMov,
									dataInizioStatoOcc);
							ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFineMov,
									dataInizioStatoOcc);
							movimento.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioOriginale);
						}
					}
				} else { // else if (DateUtils.getAnno(dataInizioStatoOcc) <
							// DateUtils.getAnno(MessageCodes.General.DATA_DECRETO_FORNERO_2014))
					if (DateUtils.compare(dataInizioOriginale, dataInizioMobilita) < 0) {
						// controlla se il movimento si interseca con il periodo di mobilità
						didTrovata = cercaDid(dataInizioOriginale, dataFineMov, dataInizioMobilita);
						if (didTrovata) {
							ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioOriginale, dataFineMov,
									dataInizioOriginale);
							ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFineMov,
									dataInizioOriginale);
							if (dataFineMov == null || dataFineMov.equals("")) {
								numeroMesi = Integer.MAX_VALUE;
							} else {
								numeroMesi = Controlli.numeroMesiDiLavoro(dataInizioOriginale, dataFineMov);
							}
						}
					} else {
						if (dataFineMov == null || dataFineMov.equals("")) {
							numeroMesi = Integer.MAX_VALUE;
						} else {
							if (DateUtils.compare(dataFineMov, dataFineMobilita) >= 0) {
								numeroMesi = Controlli.numeroMesiDiLavoro(dataInizioOriginale, dataFineMov);
							}
						}
						if (dataFineMov == null || dataFineMov.equals("")
								|| DateUtils.compare(dataFineMov, dataFineMobilita) >= 0) {
							ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioOriginale, dataFineMov,
									dataInizioOriginale);
							ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFineMov,
									dataInizioOriginale);
						}
					}
				}

				if (!gestioneFornero) {
					if (Contratto.getTipoContratto(movimento) == Contratto.DIP_TD
							|| (dataFineMov != null && !dataFineMov.equals(""))) {
						isCategoriaParticolareCurr = Controlli.isCategoriaParticolare(numeroMesi, movimento);
						isCategoriaParticolareTD = isCategoriaParticolareTD && isCategoriaParticolareCurr;
					}
				} else {
					if (gestione150 && !iscrittoCM) {
						isCategoriaParticolareCurr = Controlli.isCategoriaParticolareDecreto150(numeroMesi, movimento);
						isCategoriaParticolareTD = isCategoriaParticolareTD && isCategoriaParticolareCurr;
					} else {
						isCategoriaParticolareCurr = Controlli.isCategoriaParticolareDecretoFornero(numeroMesi,
								movimento, dataFineMobilita, true);
						isCategoriaParticolareTD = isCategoriaParticolareTD && isCategoriaParticolareCurr;
					}
				}

				if (ggDiLavoro > 0 || ggDiLavoroAnnoSuccessivo > 0) {
					String codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
							? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
							: "";
					String codMonoTempo = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TEMPO)
							? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString()
							: "";
					if ((Contratto.getTipoContratto(movimento) == Contratto.DIP_TI)
							|| (codMonoTipoAss.equalsIgnoreCase("N") && codMonoTempo.equalsIgnoreCase("I"))) {
						// (dataFineMov == null || dataFineMov.equals(""))) {
						if (!gestioneFornero) {
							trovatoMovTIDecadenza = true;
							isTempoIndeterminato = true;
							// se nel momento in cui trovo il tempo indeterminato il
							// reddito è
							// gia alto allora devo creare uno stato occupazionale
							// di Occupato
							if (noReddito) {
								exit = true;
							}
						} else {
							if (!isCategoriaParticolareCurr) {
								trovatoMovTIDecadenza = true;
								isTempoIndeterminato = true;
								if (noReddito) {
									exit = true;
								}
							}
						}
					}
					BigDecimal retribuzione = Retribuzione.getRetribuzioneMen(movimento);
					if (retribuzione == null) {
						if (didTrovata || dataFineMov == null || dataFineMov.equals("")
								|| DateUtils.compare(dataFineMov, dataFineMobilita) >= 0) {
							if ((Contratto.getTipoContratto(movimento) == Contratto.DIP_TI)
									|| (codMonoTipoAss.equalsIgnoreCase("N") && codMonoTempo.equalsIgnoreCase("I"))) {
								if (!gestioneFornero) {
									noRedditoTI = true;
								} else {
									if (!isCategoriaParticolareCurr) {
										noRedditoTI = true;
									}
								}
							} else {
								noReddito = true;
							}
						}
					} else {
						statoOccupazionale.aggiorna(ggDiLavoro, retribuzione.doubleValue());
						statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());

						if ((Contratto.getTipoContratto(movimento) == Contratto.DIP_TI)
								|| (codMonoTipoAss.equalsIgnoreCase("N") && codMonoTempo.equalsIgnoreCase("I"))) {
							if (!gestioneFornero) {
								statoOccupazionaleMovTI.aggiorna(ggDiLavoro, retribuzione.doubleValue());
								statoOccupazionaleMovTI.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
										retribuzione.doubleValue());
							} else {
								if (!isCategoriaParticolareCurr) {
									statoOccupazionaleMovTI.aggiorna(ggDiLavoro, retribuzione.doubleValue());
									statoOccupazionaleMovTI.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
											retribuzione.doubleValue());
								}
							}
						}
					}
				}
			}

			if (numeroMesi > numeroMesiTotal) {
				numeroMesiTotal = numeroMesi;
			}

			if (i == 0) {
				limiteAnnoSuccessivoTotal = limiteReddito.getLimiteAnnoSuccessivo(movimento, cdnlavoratore,
						cm.getSource(), transExec);
			} else {
				limiteAnnoSuccessivo = limiteReddito.getLimiteAnnoSuccessivo(movimento, cdnlavoratore, cm.getSource(),
						transExec);
				if (limiteAnnoSuccessivo > limiteAnnoSuccessivoTotal) {
					limiteAnnoSuccessivoTotal = limiteAnnoSuccessivo;
				}
			}

			// Se ho trovato un tempo indeterminato, allora devo creare uno
			// stato occupazionale di Occupato se ho superato il reddito fino a quel momento
			if (isTempoIndeterminato) {
				if (!exit) {
					exit = statoOccupazionale.getReddito() > statoOccupazionale.getLimiteReddito();
				}
			}
		} // for (int i = 0; i < movimenti.size(); i++)

		// superamento del reddito per tutti i movimenti
		boolean superatoAnnoPrimo = statoOccupazionale.getReddito() > statoOccupazionale.getLimiteReddito();
		boolean superatoAnnoSecondo = statoOccupazionale.getRedditoAnnoSuccessivo() > limiteAnnoSuccessivoTotal;
		// boolean superato = superatoAnnoPrimo || superatoAnnoSecondo;
		boolean superato = superatoAnnoPrimo;
		// superamento del reddito per solo i movimenti a tempo indeterminato
		boolean superatoAnnoPrimoTI = statoOccupazionaleMovTI.getReddito() > statoOccupazionale.getLimiteReddito();
		boolean superatoAnnoSecondoTI = statoOccupazionaleMovTI.getRedditoAnnoSuccessivo() > limiteAnnoSuccessivoTotal;
		// boolean superatoTI = superatoAnnoPrimoTI || superatoAnnoSecondoTI;
		boolean superatoTI = superatoAnnoPrimoTI;

		if (gestione150 && !iscrittoCM) {
			if (isCategoriaParticolareTD) {
				statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO_CAT_SPECIALE);
			} else {
				statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
			}
		} else {
			if (trovatoMovTIDecadenza) {
				if (superatoTI || noRedditoTI || exit) {
					statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
				} else {
					if (superato || noReddito) {
						statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO_CAT_SPECIALE);
					} else {
						statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_BASSO);
					}
				}
			} else {
				if (!noReddito && !superato) {
					statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_BASSO);
				} else {
					// siamo nel caso di reddito alto
					if (gestioneFornero) {
						// gestione contratti subordinati o parasubordinati (non fanno sospensione)
						if (isCategoriaParticolareTD) {
							statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO_CAT_SPECIALE);
						} else {
							statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
						}
					} else {
						if (isCategoriaParticolareTD && numeroMesiTotal >= 0) {
							// reddito alto ma appartenenza alla categoria del contratto
							// td minore di 8 mesi (o 4 se giovane) oppure 6 mesi dopo il decreto Fornero
							statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO_CAT_SPECIALE);
						} else {
							statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
						}
					}
				}
			}
		}

		return statoOccupazionale;
	}

	/**
	 * restituisce il source bean degli attributi togliendo il nodo ROW
	 * 
	 * @param row
	 * @return
	 */
	public SourceBean getRowAttribute(SourceBean row) {
		return row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
	}

	public SourceBean getMobilitaSpecifica(Object prg, TransactionQueryExecutor txExecutor) throws Exception {
		Object params[] = { prg };
		SourceBean row = null;
		row = (SourceBean) txExecutor.executeQuery("GET_MOBILITA_SPECIFICA", params, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il movimento");
		return getRowAttribute(row);
	}

	public void aggiornaMobilitaPerBatch(SourceBean mobilita, String codMotivoFine, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal numKlo = new BigDecimal(mobilita.getAttribute("NUMKLOMOBISCR").toString());
		BigDecimal newNumKlo = new BigDecimal(numKlo.intValue() + 1);
		Object prgMobilita = mobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
		Object codMonoBatch = "";
		Object cdnUser = null;
		if (!DatiDiTest.TEST)
			cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		else
			cdnUser = "3";
		Object params[] = new Object[5];
		params[0] = codMonoBatch;
		params[1] = newNumKlo;
		params[2] = cdnUser;
		params[3] = codMotivoFine;
		params[4] = prgMobilita;
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_MOBILITA_PER_BATCH", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile aggiornare la mobilità con prg = " + (BigDecimal) prgMobilita);
	}

	/**
	 * restituisce true se il movimento si interseca con l'inizio della did virtuale che corrisponde alla data di inizio
	 * Mobilità
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public boolean cercaDid(String dataInizio, String dataFine, String dataInizioMobilita) throws Exception {
		boolean didTrovata = false;
		String dataInizioDid = dataInizioMobilita;
		if (dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, dataInizioDid) >= 0) {
			didTrovata = true;
		}
		return didTrovata;
	}

	public Vector NormalizzaMovimentiStessaData(Vector movimenti) {
		MovimentoBean movimento = null;
		MovimentoBean movimentoSucc = null;
		String datInizioMov = "";
		String datInizioMovSucc = "";
		String codMonoTempo = "";
		String codMonoTempoSucc = "";
		boolean inserito = false;
		for (int i = 0; i < movimenti.size() - 1; i++) {
			inserito = false;
			movimento = (MovimentoBean) movimenti.get(i);
			datInizioMov = movimento.getDataInizio();
			codMonoTempo = movimento.getCodMonoTempo();
			if (codMonoTempo != null && !codMonoTempo.equals("") && codMonoTempo.equals("I")) {
				movimentoSucc = (MovimentoBean) movimenti.get(i + 1);
				datInizioMovSucc = movimentoSucc.getDataInizio();
				codMonoTempoSucc = movimentoSucc.getCodMonoTempo();
				if (datInizioMovSucc.equals(datInizioMov)) {
					if (codMonoTempoSucc != null && !codMonoTempoSucc.equals("") && codMonoTempoSucc.equals("D")) {
						movimenti.set(i, movimentoSucc);
						movimenti.set(i + 1, movimento);
					}
				}

			}
		}
		return movimenti;
	}
}
