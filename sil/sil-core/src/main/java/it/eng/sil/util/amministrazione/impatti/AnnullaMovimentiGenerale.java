package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/*
 * AnnullaMovimentiGenerale.java
 *
 * Creato on 29 Novembre 2004
 */
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.util.UtilityNumGGTraDate;

/**
 * Classe per la gestione degli impatti in seguito all'annullamento/annullamento per rettifica di un movimento.
 */

public class AnnullaMovimentiGenerale {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AnnullaMovimentiGenerale.class.getName());
	private static final int PREC = 0;
	private static final int SUCC = 1;
	private String classname = this.getClass().getName();
	/**
	 * TransactionQueryExecutor da utilizzare per le transazioni
	 */
	private TransactionQueryExecutor trans;
	/**
	 * Movimento su cui si sat eseguendo l'annullamento
	 */
	private SourceBean movimento;
	private BigDecimal cdnLavoratore;
	private BigDecimal prgMovimento;
	private RequestContainer requestContainer;
	private SourceBean request;
	private SourceBean response;

	/**
	 * Costruttore
	 */
	public AnnullaMovimentiGenerale(BigDecimal prg, SourceBean req, SourceBean resp, RequestContainer reqCont,
			TransactionQueryExecutor transQueryExec) {
		try {
			this.trans = transQueryExec;
			this.prgMovimento = prg;
			this.request = req;
			this.response = resp;
			this.requestContainer = reqCont;
			this.movimento = DBLoad.getMovimento(prgMovimento);
			this.cdnLavoratore = new BigDecimal(movimento.getAttribute(MovimentoBean.DB_CDNLAVORATORE).toString());
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'inizializzazione dei parametri", ex);

		}
	}

	public int codiceFallimento = 0;

	public SourceBean getMovimento() {
		return this.movimento;
	}

	public void setMovimento(SourceBean mov) {
		this.movimento = mov;
	}

	/**
	 * Metodo per la gestione degli impatti sul movimento quando si sta facendo la rettifica (processor
	 * RettificaMovimento)
	 * 
	 * @return boolean per determinare se è andato tutto a buon fine o vi è stato qualche errore
	 */
	public boolean generaImpatti() throws Exception {
		boolean permettiAnnullamento = true;
		boolean isOk = true;
		StatoOccupazionaleBean statoOccupazionaleCorrente = null;
		try {
			// if(movimento.getAttribute("CodTipoMov").equals("AVV")) {
			// Posso annullare un movimento solo se ho annullato tutti i suoi
			// successivi
			if (movimento.containsAttribute("PRGMOVIMENTOSUCC")
					&& !movimento.getAttribute("PRGMOVIMENTOSUCC").equals("")) {
				permettiAnnullamento = false;
				isOk = false;
			}
			// }
			if (permettiAnnullamento) {
				boolean isCesSenzaImpatti = false;
				// Recupero lo stato occupazionale corrente
				statoOccupazionaleCorrente = DBLoad.getStatoOccupazionale(cdnLavoratore, trans);
				/*
				 * Se si tratta di un movimento di CES. con data inizio > dataOdierna, gli impatti non sono ancora
				 * scattati per il movimento, per cui devono essere gestiti i collegati ed aggiornare lo stato atto
				 */
				if (movimento.getAttribute("CODTIPOMOV").equals("CES") && DateUtils
						.compare((String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO), DateUtils.getNow()) > 0)
					isCesSenzaImpatti = true;
				gestisciPrgCollegati();
				if (!isCesSenzaImpatti)
					calcolaStatoOccupaz(statoOccupazionaleCorrente);

				boolean pippo = false;
				if (pippo)
					throw new Exception("terminato per test");
			}
		} catch (ProTrasfoException e1) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella generazione degli impatti", (Exception) e1);

			isOk = false;
			codiceFallimento = e1.getCode();
		} catch (MobilitaException me) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella generazione degli impatti", (Exception) me);

			throw me;
		} catch (ControlliException ce) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella generazione degli impatti", (Exception) ce);

			throw ce;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella generazione degli impatti", e);

			throw e;
		}
		return isOk;
	}// end generaImpatti

	/**
	 * Implementa la logica per il calcolo del nuovo stato occupazionale, senza il movimento da annullare.
	 * 
	 * @return nothing
	 * @param statoOccupazionaleCorrente:
	 *            lo stato occupazionale corrente del lavoratore
	 */
	private void calcolaStatoOccupaz(StatoOccupazionaleBean statoOccupazionaleCorrente) throws Exception {
		BigDecimal prgStatoOccTmp = null;
		int anno = DateUtils.getAnno(DateUtils.getNow());
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		SituazioneAmministrativa sitAmm = null;
		String dataRif = "";
		String dataInizio = "01/01/" + anno;
		String dataFine = "31/12/" + anno;
		SourceBean movAperto = null;
		String dataRifAperto = "";
		//
		movimento.updAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ, "");
		// Risoluzione problema numklomov (non era aggiornato)
		MovimentoBean mApp = new MovimentoBean(getMovimento());
		DBStore.aggiornaStatoOccMovimento(mApp, requestContainer, trans);
		getMovimento().updAttribute("numklomov", mApp.getAttribute("numklomov"));
		//
		Vector vect = DBLoad.getMovimentiLavoratore(cdnLavoratore, trans);
		vect = togliMovimentoInEsame(vect);
		vect = togliMovimentoNonProt(vect);
		vect = togliMovimentoInDataFutura(vect);

		Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, trans);
		Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", trans);
		Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", trans);

		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();

		if (movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null
				&& !movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC).equals("")) {
			SourceBean movimentoSucc = movimento;
			Object prgMovimentoPrec = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
			SourceBean sb = null;
			String dataMov = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			while (prgMovimentoPrec != null) {
				sb = DBLoad.getMovimento(prgMovimentoPrec, trans);
				sb = DBLoad.getRowAttribute(sb);
				if (MovimentoBean.getTipoMovimento(sb) == MovimentoBean.PROROGA
						|| MovimentoBean.getTipoMovimento(sb) == MovimentoBean.TRASFORMAZIONE) {
					if (DateUtils.compare(sb.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString(),
							dataNormativa297) >= 0) {
						prgMovimentoPrec = sb.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
						dataMov = sb.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
						movimentoSucc = sb;
					} else {
						Object prgMovimentoSucc = movimentoSucc.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
						SourceBean sbS = DBLoad.getMovimento(prgMovimentoSucc, trans);
						sbS = DBLoad.getRowAttribute(sbS);
						dataMov = sbS.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
						sb = null;
						prgMovimentoPrec = null;
					}
				} else
					prgMovimentoPrec = null;
			}
			if (sb != null)
				dataRif = sb.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			else
				dataRif = dataMov;
		} else {
			dataRif = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		}

		/* ++++++++++++++ Ricostruzione storia ++++++++++++++ */

		// Imposto il prgstatoOccupaz del movimento da annullare a null
		if (movimento.containsAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ)
				&& !movimento.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ).equals(""))
			prgStatoOccTmp = new BigDecimal(movimento.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ).toString());

		sitAmm = new SituazioneAmministrativa(vect, statiOccupazionali, patti, dids, dataRif, trans, requestContainer);

		List listaMobilita = sitAmm.getListaMobilita();
		if (listaMobilita.size() > 0) {
			dataRif = Controlli.selezionaUltimaMobilita(listaMobilita, dataRif);
		}

		if (vect != null) {
			movAperto = Controlli.selezionaMovimentoApertoIniziale(dataRif, movimento, vect);
			if (movAperto != null) {
				dataRifAperto = movAperto.containsAttribute(MovimentoBean.DB_DATA_INIZIO)
						? movAperto.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()
						: "";
				if (!dataRifAperto.equals("") && DateUtils.compare(dataRifAperto, dataRif) < 0) {
					dataRif = dataRifAperto;
				}
			}
		}

		List statiOcc = null;
		ListaStatiOccupazionaliAnn lso = null;
		statiOcc = sitAmm.getStatiOccupazionali();
		lso = new ListaStatiOccupazionaliAnn(dataRif, cdnLavoratore, statiOcc, dids, listaMobilita, vect, trans);
		_logger.debug(lso.toString());

		StatoOccupazionaleBean soIniziale = lso.getStatoOccupazionaleIniziale();
		int ind = sitAmm.cercaIndice(soIniziale, sitAmm.getMovimenti());
		if (ind >= 0) {
			// controllo per far scattare o meno gli impatti in presenza
			// di movimenti orfani (proroghe o trasformazioni a t.d. senza mov
			// precedente)
			sitAmm.checkMovimentiOrfaniPerImpatti(sitAmm.getMovimenti(), ind);
		}
		sitAmm.resettaStatiOccupazionali(lso);
		/*
		 * Bisogna riaprire le DID in caso di riapertura di uno stato occupaz. di D o I
		 */
		if (lso.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
				|| lso.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
			boolean pattoRiaperto = false;
			// bisogna riaprire la did nel caso sia chiusa
			DidBean did = (DidBean) sitAmm.cercaDid((String) lso.getStatoOccupazionaleIniziale().getDataInizio());
			if (did == null) {
				did = (DidBean) sitAmm.cercaDid(dataRif);
			}
			if (did != null && did.getAttribute("datFine") != null) {
				String dataDichiarazione = did.getDataInizio();
				String dataFineDid = did.getDataFine();
				// riapro la did
				BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
				numKlo = numKlo.add(new BigDecimal(1));
				did.updAttribute("numKloDichDisp", numKlo);
				DBStore.riapriDID(did.getAttribute("prgDichDisponibilita"), dataDichiarazione, numKlo,
						RequestContainer.getRequestContainer(), trans);
				did.updAttribute("flag_changed", "1");
				did.setAttribute("datFine_originale", did.getAttribute("datFine"));
				did.delAttribute("datFineChanged");
				did.delAttribute("datFine");
				// Aggiorno il numklo dell'eventuale ChiusuraDID successiva
				for (int k = 0; k < sitAmm.getMovimenti().size(); k++) {
					EventoAmministrativo c = (EventoAmministrativo) sitAmm.getMovimenti().get(k);
					if (did.getTipoEventoAmministrativo() == EventoAmministrativo.DID) {
						if (c.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_DID) {
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
						if (did.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_DID) {
							if (c.getTipoEventoAmministrativo() == EventoAmministrativo.DID) {
								DidBean db = (DidBean) c;
								if (db.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita())) {
									_logger.debug(
											"SituazioneAmministrativa.dereferenziaMovimentiDa():aggiornamento DID: "
													+ db);

									db.updAttribute("numKloDichDisp", did.getAttribute("numKloDichDisp"));
									break;
								}
							}
						}
					}
				} // end for
					// riapertura patto associato alla did
				pattoRiaperto = sitAmm.riapriPattoAssocDid(did);
				// fine riapertura patto associato alla did
			}
			if (!pattoRiaperto) {
				sitAmm.riapriPattoMobilita((String) lso.getStatoOccupazionaleIniziale().getDataInizio(),
						cdnLavoratore.toString());
			}

		}
		sitAmm.ricrea(soIniziale, ind);

		// Nuovo stato occupazionale
		nuovoStatoOccupazionale = sitAmm.getStatoOccupazionaleAperto();

		if ((nuovoStatoOccupazionale == null) || nuovoStatoOccupazionale.getProgressivoDB() == null) {
			throw new Exception("Calcolo dello stato occupazionale nuovo non valido.");
		} else if (nuovoStatoOccupazionale.getStatoOccupazionale() != statoOccupazionaleCorrente
				.getStatoOccupazionale()) {
			// Per la visualizzazione degli alert
			if (!requestContainer.getServiceRequest()
					.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE))
				aggiungiAlert(statoOccupazionaleCorrente, nuovoStatoOccupazionale, true);

		} else
			aggiungiAlert(statoOccupazionaleCorrente, nuovoStatoOccupazionale, false);

	}// end statoOccCollegatoAlMov

	/**
	 * Toglie dalla lista dei movimenti il movimento che si sta annullando, in modo da poter ricalcolare lo stato
	 * occupazionale
	 * 
	 * @return il vettore dei movimenti nell'anno senza quello che sista annullando
	 * @param il
	 *            vettore dei movimenti nell'anno
	 */
	private Vector togliMovimentoInEsame(Vector vect) throws Exception {
		Vector v = new Vector(vect.size() - 1);
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (tmp.getAttribute("PRGMOVIMENTO").equals(prgMovimento))
				continue;
			v.add(tmp);
		} // end for
		return v;
	}// end statoOccCollegatoAlMov

	/**
	 * cancella dalla lista i movimenti non protocollati, e quindi che non conseguenze sugli impatti
	 */
	private Vector togliMovimentoNonProt(Vector vect) throws Exception {
		Vector v = (vect.size() >= 1) ? new Vector(vect.size() - 1) : new Vector(0);
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (!tmp.getAttribute("CODSTATOATTO").equals("PR"))
				continue;
			v.add(tmp);
		} // end for
		return v;
	}// end togliMovimentoNonProt

	private Vector togliMovimentoInDataFutura(Vector vect) throws Exception {
		Vector v = new Vector();
		String oggi = DateUtils.getNow();
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (DateUtils.compare(tmp.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString(), oggi) > 0)
				continue;
			v.add(tmp);
		} // end for
		return v;
	}

	/**
	 * Metodo per la gestione dei movimenti collegati: 1) se il movimento in questione ha un precedente, allora il
	 * precedente verrà collegato all'eventuale successivo del mov. in esame; 2) se il movimento in questione ha un
	 * successivo, allora il successivo verrà collegato all'eventuale precedente del mov. in esame. 3) Non verranno
	 * ripuliti i campi del prec. e succ. del movimento in esame.
	 * 
	 * @param movimento,
	 *            movimento da annullare
	 * @return nothimg
	 */
	public void gestisciPrgCollegati() throws Exception {
		Object prgMovPrec = null;
		Object prgMovSucc = null;
		Object campi[] = new Object[3];
		SourceBean movPrec = null;
		SourceBean movSucc = null;
		String codTipoMov = "";
		String codMonoTempoPrec = "";

		prgMovPrec = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null
				? movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC).toString()
				: "";
		prgMovSucc = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC) != null
				? movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC).toString()
				: "";

		// Carico gli eventuali movimenti precedenti e successivi
		if (!prgMovPrec.equals("")) {
			movPrec = DBLoad.getMovimento(prgMovPrec, trans);
			codMonoTempoPrec = (String) movPrec.getAttribute("CODMONOTEMPO");
		}
		if (!prgMovSucc.equals("")) {
			movSucc = DBLoad.getMovimento(prgMovSucc, trans);
		}

		if (!prgMovPrec.equals("")) {
			// Vi è un movimento precedente
			if ((movPrec != null) && movPrec.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
					&& (!movPrec.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).equals(""))) {

				// PrgMovimentoSucc
				campi[0] = prgMovSucc != null ? (String) prgMovSucc : "";

				if (!prgMovSucc.equals("") && (movSucc != null)) {
					codTipoMov = (String) movSucc.getAttribute("CODTIPOMOV");
					// DatFineMovEffettiva
					if (!codTipoMov.equals("CES")) {
						campi[1] = UtilityNumGGTraDate.sottraiGiorni((String) movSucc.getAttribute("DATINIZIOMOV"), 1);
					} else {
						campi[1] = (String) movSucc.getAttribute("DATINIZIOMOV");
					}

					// CodMonoTipoFine
					if (codTipoMov.equals("CES"))
						campi[2] = "C";
					else if (codTipoMov.equals("PRO"))
						campi[2] = "P";
					else if (codTipoMov.equals("TRA"))
						campi[2] = "T";
				} else {
					// DatFineMovEffettiva
					// 05-04-05 D'Auria-Togna
					// Se il movimento precedente alla cessazione/trasformazione
					// annullata è TI
					// la data fine effettiva del movimento precedente è null
					if (codMonoTempoPrec.equalsIgnoreCase("I")) {
						campi[1] = "";
					} else {
						campi[1] = (String) movPrec.getAttribute("DATFINEMOV");
					}

					// CodMonoTipoFine
					campi[2] = "";
				}
				// Aggiorno i campi del precedente, con i valori dell'eventuale
				// movimento successivo
				DBStore.aggiornaCampiMovimento(new MovimentoBean(movPrec), campi, requestContainer, trans, PREC);
			}
		}
		if (!prgMovSucc.equals("")) {
			// Vi è un movimento successivo
			if ((movSucc != null) && movSucc.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
					&& (!movSucc.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).equals(""))) {
				// PrgMovimentoPrec
				campi[0] = prgMovPrec != null ? (String) prgMovPrec : "";

				if (!prgMovPrec.equals("") && (movPrec != null)) {
					// CodMonoTempoMovPrec
					campi[1] = (String) movPrec.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);

					// DatInizioMovPrec
					campi[2] = (String) movPrec.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				} else {
					// CodMonoTempoMovPrec
					campi[1] = "";
					// DatInizioMovPrec
					campi[2] = "";
				}
				// Aggiorno i campi del successivo, con i valori dell'eventuale
				// movimento precedente
				DBStore.aggiornaCampiMovimento(new MovimentoBean(movSucc), campi, requestContainer, trans, SUCC);
			}
		}
	}// end gestisciPrgCollegati

	/**
	 * Metodo per l'impostazione dell'alert di cambiamento dello stato occupazionale
	 * 
	 */
	private void aggiungiAlert(StatoOccupazionaleBean old, StatoOccupazionaleBean nuovoSO, boolean cambiato)
			throws Exception {
		String msg = "";
		String vecchio = "";
		String nuovo = "";
		vecchio = old.getDescrizioneCompleta();
		nuovo = nuovoSO.getDescrizioneCompleta();
		msg = vecchio + " -> " + nuovo;
		ArrayList alerts = null;
		if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE))
			alerts = (java.util.ArrayList) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
		else {
			alerts = new java.util.ArrayList();
			request.setAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE, alerts);
		}
		if (cambiato)
			alerts.add("Stato occupazionale cambiato: \\n\\r" + msg);
		else
			alerts.add("Lo stato occupazionale del lavoratore non e' cambiato");
	}

}// end class
