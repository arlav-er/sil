/*
 * Creato il 24-giu-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author Togna C.
 * @author Landi G.
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class SituazioneAmministrativaFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SituazioneAmministrativaFactory.class.getName());
	/**
	 * Lavoratore
	 */
	private LavoratoreBean lavoratore;

	/**
	 * Situazione amminitrativa
	 */
	private SituazioneAmministrativa situazioneAmministrativa;
	/**
	 * TransactionQueryExecutor
	 */
	private TransactionQueryExecutor transactionQueryExecutor;
	/**
	 * data inizio
	 */
	private String dataInizio;

	/**
	 * Costruttore privato della classe. <b>Per calcolare gli impatti di un lavoratore è possibile effettuare una delle
	 * seguenti operazioni:<br>
	 * 1 - Non forza la ricostruzione storia: <br>
	 * SituazioneAmministrativaFactory.newInstance("cdnLavoratore", "01/02/2003", transactionExecutor).calcolaImpatti();
	 * 2 - Forza la ricostruzione storia: <br>
	 * SituazioneAmministrativaFactory.newInstance("cdnLavoratore", "01/02/2003", true,
	 * transactionExecutor).calcolaImpatti(); </b>
	 * 
	 * @param cdnLavoratore
	 * @param dataInizio
	 * @param forzaRicostruzione
	 * @param transactionQueryExecutor
	 * @throws ControlliException
	 * @throws Exception
	 */
	private SituazioneAmministrativaFactory(String cdnLavoratore, String dataInizio,
			TransactionQueryExecutor transactionQueryExecutor, LogBatch logBatch) throws ControlliException, Exception {

		setTransactionQueryExecutor(transactionQueryExecutor);
		setDataInizio(dataInizio);
		setLavoratore(new LavoratoreBean(cdnLavoratore, transactionQueryExecutor));
		// recupro eventi amministrativi associati al lavoratore
		List movimenti = getLavoratore().getMovimenti();

		List did = getLavoratore().getDid();
		List statiOccupazionali = getLavoratore().getStatiOccupazionali();
		List patti = getLavoratore().getPatti();
		// inizializzo la situazione amministrativa
		setSituazioneAmministrativa(new SituazioneAmministrativa(new Vector(movimenti), new Vector(statiOccupazionali),
				new Vector(patti), new Vector(did), dataInizio, getTransactionQueryExecutor(),
				RequestContainer.getRequestContainer(), logBatch));
		getLavoratore().setMobilita(MobilitaBean.getMobilita(cdnLavoratore, getTransactionQueryExecutor()));
		setDataCalcolo(dataInizio);
	}

	/**
	 * invocata quando si effettua la validazione mobilità
	 * 
	 * @param cdnLavoratore
	 * @param dataInizio
	 * @param transactionQueryExecutor
	 * @param reqCont
	 * @param logBatch
	 * @throws ControlliException
	 * @throws Exception
	 */
	private SituazioneAmministrativaFactory(String cdnLavoratore, String dataInizio,
			TransactionQueryExecutor transactionQueryExecutor, RequestContainer reqCont, LogBatch logBatch)
			throws ControlliException, Exception {

		setTransactionQueryExecutor(transactionQueryExecutor);
		setDataInizio(dataInizio);
		setLavoratore(new LavoratoreBean(cdnLavoratore, transactionQueryExecutor));
		// recupro eventi amministrativi associati al lavoratore
		List movimenti = getLavoratore().getMovimenti();
		List did = getLavoratore().getDid();
		List statiOccupazionali = getLavoratore().getStatiOccupazionali();
		List patti = getLavoratore().getPatti();
		// inizializzo la situazione amministrativa
		setSituazioneAmministrativa(new SituazioneAmministrativa(new Vector(movimenti), new Vector(statiOccupazionali),
				new Vector(patti), new Vector(did), dataInizio, getTransactionQueryExecutor(), reqCont, logBatch));
		getLavoratore().setMobilita(MobilitaBean.getMobilita(cdnLavoratore, getTransactionQueryExecutor()));
		setDataCalcolo(dataInizio);
	}

	/**
	 * Istanzia la classe senza forzare la ricostruzione storia
	 * 
	 * @param cdnLavoratore
	 * @param dataInizio
	 * @param transactionQueryExecutor
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	public static SituazioneAmministrativaFactory newInstance(String cdnLavoratore, String dataInizio,
			TransactionQueryExecutor transactionQueryExecutor) throws ControlliException, Exception {
		return new SituazioneAmministrativaFactory(cdnLavoratore, dataInizio, transactionQueryExecutor, null);
	}

	/**
	 * invocata quando si effettua la validazione mobilità
	 * 
	 * @param cdnLavoratore
	 * @param dataInizio
	 * @param requestContainer
	 * @param transactionQueryExecutor
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	public static SituazioneAmministrativaFactory newInstance(String cdnLavoratore, String dataInizio,
			RequestContainer requestContainer, TransactionQueryExecutor transactionQueryExecutor)
			throws ControlliException, Exception {
		return new SituazioneAmministrativaFactory(cdnLavoratore, dataInizio, transactionQueryExecutor,
				requestContainer, null);
	}

	/**
	 * Istanzia la classe senza forzando la ricostruzione storia in base al valore preso in input
	 * 
	 * @param cdnLavoratore
	 * @param dataInizio
	 * @param forzaRicostruzione
	 * @param transactionQueryExecutor
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	public static SituazioneAmministrativaFactory newInstance(String cdnLavoratore, String dataInizio,
			TransactionQueryExecutor transactionQueryExecutor, LogBatch logBatch) throws ControlliException, Exception {
		return new SituazioneAmministrativaFactory(cdnLavoratore, dataInizio, transactionQueryExecutor, logBatch);
	}

	/**
	 * Metodo invocato nel batch per movimenti a cavallo di anni (BatchImpattiTraAnni)
	 * 
	 * @param cdnLavoratore
	 * @param dataInizio
	 * @param transactionQueryExecutor
	 * @param logBatch
	 * @param requestContainer
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	public static SituazioneAmministrativaFactory newInstance(String cdnLavoratore, String dataInizio,
			TransactionQueryExecutor transactionQueryExecutor, LogBatch logBatch, RequestContainer requestContainer)
			throws ControlliException, Exception {
		return new SituazioneAmministrativaFactory(cdnLavoratore, dataInizio, transactionQueryExecutor,
				requestContainer, logBatch);
	}

	private void setDataCalcolo(String dataCalcolo) throws Exception {
		SourceBean movimento = null;
		Vector movimentiAperti = new Vector(getLavoratore().getMovimenti());
		if (movimentiAperti != null && movimentiAperti.size() > 0) {
			movimento = Controlli.selezionaMovimentoApertoIniziale(dataCalcolo, null, movimentiAperti);
			if (movimento != null) {
				Controlli.controllaMovimentoProTraSenzaPrec(movimento);
				movimento = Controlli.recuperaAvvDaProTra(movimento, movimentiAperti, transactionQueryExecutor);
				dataCalcolo = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			}
		}
		// recupero eventuale lista mobilita del lavoratore
		Vector rowsMobilita = new Vector(getLavoratore().getMobilita());
		SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataCalcolo,
				getSituazioneAmministrativa().getPrgMobilitaRicalcolo());
		if (sbMobilitaMov != null) {
			dataCalcolo = sbMobilitaMov.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
		} else {
			Vector statiOccupazionali = new Vector(getLavoratore().getStatiOccupazionali());
			SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali, dataCalcolo);
			if (sbStatoOccAperto == null) {
				SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataCalcolo);
				if (sbMobilita != null) {
					dataCalcolo = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
				}
			}
		}
		setDataInizio(dataCalcolo);
	}

	/**
	 * Metodo che calcola gli impatti
	 *
	 */
	public StatoOccupazionaleBean calcolaImpatti() throws Exception {

		// situazioneAmministrativa.setForzaRicostruzione(true);

		ListaStatiOccupazionali lso = new ListaStatiOccupazionali(getDataInizio(), getLavoratore().getCdnLavoratore(),
				getSituazioneAmministrativa().getStatiOccupazionali(), getSituazioneAmministrativa().getDids(),
				getSituazioneAmministrativa().getListaMobilita(), getSituazioneAmministrativa().getMovimenti(),
				"RICALCOLA_IMPATTI", getTransactionQueryExecutor());

		StatoOccupazionaleBean statoOccupazionaleIniziale = lso.getStatoOccupazionaleIniziale();
		int indice = getSituazioneAmministrativa().cercaIndice(statoOccupazionaleIniziale,
				getSituazioneAmministrativa().getMovimenti());
		situazioneAmministrativa.resettaStatiOccupazionali(lso);
		/*
		 * Bisogna riaprire le DID in caso di riapertura di uno stato occupaz. di D o I
		 */
		if (statoOccupazionaleIniziale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
				|| statoOccupazionaleIniziale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {

			riapriDid_Patto(statoOccupazionaleIniziale);

		}
		StatoOccupazionaleBean statoOccFinale = getSituazioneAmministrativa().ricrea(statoOccupazionaleIniziale,
				indice);

		return (statoOccFinale);
	}

	private void setLavoratore(LavoratoreBean lavoratore) {
		this.lavoratore = lavoratore;
	}

	private LavoratoreBean getLavoratore() {
		return (lavoratore);
	}

	private void setTransactionQueryExecutor(TransactionQueryExecutor transactionQueryExecutor) {
		this.transactionQueryExecutor = transactionQueryExecutor;
	}

	private void setDataInizio(String dataInizio) {
		this.dataInizio = dataInizio;
	}

	private TransactionQueryExecutor getTransactionQueryExecutor() {
		return (transactionQueryExecutor);
	}

	/**
	 * @return
	 */
	private String getDataInizio() {
		return dataInizio;
	}

	/**
	 * @return
	 */
	private SituazioneAmministrativa getSituazioneAmministrativa() {
		return situazioneAmministrativa;
	}

	/**
	 * @param amministrativa
	 */
	private void setSituazioneAmministrativa(SituazioneAmministrativa amministrativa) {
		situazioneAmministrativa = amministrativa;
	}

	/**
	 * Riapre did e patto a partire dallo stato occupazionale iniziale
	 * 
	 * @param statoOccupazionaleIniziale
	 * @throws Exception
	 */
	private void riapriDid_Patto(StatoOccupazionaleBean statoOccupazionaleIniziale) throws Exception {
		boolean pattoRiaperto = false;
		// bisogna riaprire la did nel caso sia chiusa
		DidBean did = (DidBean) getSituazioneAmministrativa()
				.cercaDid((String) statoOccupazionaleIniziale.getDataInizio());
		if (did == null) {
			did = (DidBean) getSituazioneAmministrativa().cercaDid(getDataInizio());
		}
		if (did != null && did.getAttribute("datFine") != null) {
			String dataDichiarazione = did.getDataInizio();
			String dataFineDid = did.getDataFine();
			// riapro la did
			BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
			numKlo = numKlo.add(new BigDecimal(1));
			did.updAttribute("numKloDichDisp", numKlo);
			DBStore.riapriDID(did.getAttribute("prgDichDisponibilita"), dataDichiarazione, numKlo,
					RequestContainer.getRequestContainer(), getTransactionQueryExecutor());
			did.updAttribute("flag_changed", "1");
			did.setAttribute("datFine_originale", did.getAttribute("datFine"));
			did.delAttribute("datFineChanged");
			did.delAttribute("datFine");
			int tipoEventoDid = did.getTipoEventoAmministrativo();
			// Aggiorno il numklo dell'eventuale ChiusuraDID successiva
			for (int k = 0; k < getSituazioneAmministrativa().getMovimenti().size(); k++) {
				EventoAmministrativo c = (EventoAmministrativo) getSituazioneAmministrativa().getMovimenti().get(k);
				int tipoEvento = c.getTipoEventoAmministrativo();
				if (tipoEventoDid == EventoAmministrativo.DID) {
					if (tipoEvento == EventoAmministrativo.CHIUSURA_DID) {
						ChiusuraDidBean cDb = (ChiusuraDidBean) c;
						if (cDb.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita())) {
							_logger.debug(
									"SituazioneAmministrativa.dereferenziaMovimentiDa():aggiornamento chiusura DID: "
											+ cDb);

							cDb.updAttribute("numKloDichDisp", did.getAttribute("numKloDichDisp"));
							break;
						}
					}
				} else {
					if (tipoEventoDid == EventoAmministrativo.CHIUSURA_DID) {
						if (tipoEvento == EventoAmministrativo.DID) {
							DidBean db = (DidBean) c;
							if (db.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita())) {
								_logger.debug(
										"SituazioneAmministrativa.dereferenziaMovimentiDa():aggiornamento DID: " + db);

								db.updAttribute("numKloDichDisp", did.getAttribute("numKloDichDisp"));
								break;
							}
						}
					}
				}
			} // end for
				// riapertura patto
			pattoRiaperto = getSituazioneAmministrativa().riapriPattoAssocDid(did);
			// fine riapertura patto
		}
		if (!pattoRiaperto) {
			getSituazioneAmministrativa().riapriPattoMobilita((String) statoOccupazionaleIniziale.getDataInizio(),
					getLavoratore().getCdnLavoratore().toString());
		}
	}

}