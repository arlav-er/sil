package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class StatoOccupazionaleSuccessivo {

	public static StatoOccupazionaleBean calcola(MovimentoBean movimento, CmBean cm, DidBean did,
			StatoOccupazionaleBean statoOccIniziale, Vector movimentiAnno, List listaMobilita, int tipoDichiarazione,
			String dat150, Vector listaDisabiliCM, TransactionQueryExecutor txExecutor)
			throws ControlliException, Exception {
		Reddito reddito = null;
		LimiteRedditoExt limiteReddito = null;
		// controllo se il moviemnto e' un movimento di proroga
		SourceBean m = movimento.getSource();
		boolean flag = true;
		MovimentoBean mb = movimento;
		SourceBean sb = m;
		SourceBean sbMov = movimento.getSource();
		SourceBean sbDid = null;
		if (did != null)
			sbDid = did.getSource();
		SourceBean sbCM = null;
		if (cm != null)
			sbCM = cm.getSource();
		StatoOccupazionaleBean nuovoStatoOccBean = null;
		StatoOccupazionaleBean nuovoSOcc = getStatoOccupazionale(sbMov, sbCM, sbDid, statoOccIniziale, movimentiAnno,
				listaMobilita, tipoDichiarazione, dat150, listaDisabiliCM, txExecutor);
		if (statoOccIniziale.getStatoOccupazionale() != nuovoSOcc.getStatoOccupazionale()) {
			nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoSOcc, statoOccIniziale);
		} else
			nuovoStatoOccBean = statoOccIniziale;
		return nuovoStatoOccBean;
	}

	public static StatoOccupazionaleBean getStatoOccupazionale(SourceBean movimento, SourceBean cm, SourceBean did,
			StatoOccupazionaleBean statoOccIniziale, Vector movimentiAnno, List listaMobilita, int tipoDichiarazione,
			String dat150, Vector listaDisabiliCM, TransactionQueryExecutor txExecutor) throws Exception {
		/***********************************************************************
		 * AM_MOVIMENTO.DATINIZIOMOV | AM_MOVIMENTO.DATFINEMOV | AM_MOVIMENTO.DECRETRIBUZIONEMEN |
		 * AM_MOVIMENTO.CODCONTRATTO | AM_MOVIMENTO.CODMVCESSAZIONE | AM_MOVIMENTO.CODTIPOMOV
		 **********************************************************************/
		int ggDiLavoro = 0;
		int ggDiLavoroAnnoSuccessivo = 0;
		boolean exit = false;
		boolean noReddito = false;
		LimiteRedditoExt limiteReddito = null;
		String dataRif = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		boolean gestioneFornero = (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0);
		boolean gestioneDecreto150 = (dat150 != null && !dat150.equals("") && DateUtils.compare(dataRif, dat150) >= 0);
		int annoPrimo = DateUtils.getAnno(dataRif);
		String dataInizio = "";
		String dataFine = "";
		BigDecimal retribuzione = null;
		String dataInizioDid = "";
		StatoOccupazionaleBean statoOccupazionale = (StatoOccupazionaleBean) statoOccIniziale.clone();
		// di seguito: se lo stato occupaz. di raggruppamento è Altro, lo stato
		// occupazionale
		// successivo è OCCUPATO (indipendentemente dalla durata del movimento).
		if (statoOccIniziale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_A) {
			// creare stato occupazionale occupato a prescindere dalla durata
			// del movimento
			// if (Controlli.numeroMesiDiLavoro(movimento)>=1) {
			statoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B);
			statoOccupazionale.setChanged(true);
			// }
			return statoOccupazionale;
		}
		// Disoccupato - Occupato a rischio di disoccupazione creato a partire dall'unico movimento a TI aperto al
		// momento della did
		// inserita con il flag a rischio disoccupazione = S. In questo caso devo proseguire con lo stato occupazionale
		// B3
		if (statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.B3
				&& statoOccIniziale.getDataInizio() != null
				&& DateUtils.compare(statoOccIniziale.getDataInizio(), dataRif) == 0) {
			statoOccupazionale.setChanged(false);
			return statoOccupazionale;
		}

		String dataDichDid = "";
		if (did != null) {
			dataDichDid = did.getAttribute("datdichiarazione").toString();
		}

		try {
			// I TIROCINI NON HANNO IMPATTI PER QUANTO RIGUARDA IL REDDITO E
			// DURATA
			String codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			switch (MovimentoBean.getTipoMovimento(movimento)) {
			case MovimentoBean.PROROGA:
			case MovimentoBean.ASSUNZIONE:

				dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
				if (dataFine == null)
					dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE);
				limiteReddito = new LimiteRedditoExt(dataInizio);
				retribuzione = Retribuzione.getRetribuzioneMen(movimento);
				if (retribuzione != null && (tipoDichiarazione == StatoOccupazionaleManager.NO_SANAMENTO_SITUAZIONE
						|| tipoDichiarazione == StatoOccupazionaleManager.SANAMENTO_DETTAGLIO)) {
					// calcolo del reddito
					switch (Contratto.getTipoContratto(movimento)) {
					case Contratto.AUTONOMO:

						// come minimo ricalcola il limite
					case Contratto.COCOCO:

						// anche qui va ricalcolato il limite del reddito
						if (Controlli.inCollocamentoMirato(cm, dataRif)) {
							statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
						} else {
							// controllare se nella data inizio movimento ci
							// sono
							// rapporti di lavoro dipendente aperti. In tal caso
							// bisogna considerare
							// limite reddito di lavoro dipendente
							boolean esisteMovimentoDip = false;
							if (movimentiAnno.size() > 0) {
								esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(dataInizio,
										movimentiAnno);
							}
							if (esisteMovimentoDip) {
								String dataDid = "";
								dataDid = did.getAttribute("datdichiarazione").toString();
								statoOccupazionale
										.setLimiteReddito(limiteReddito.calcola(LimiteReddito.DIPENDENTE, dataDid));
							} else {
								statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.AUTONOMO));
							}
						}
						break;

					case Contratto.DIP_TD:

						// suppongo che il limite sia minore o uguale a quello
						// passato come parametro al metodo
						if (Controlli.inCollocamentoMirato(cm, dataRif)) {
							statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
						} else {
							if (Controlli.annoDIDeqAnnoMov(dataInizio, did)) {
								dataInizioDid = did.getAttribute("datdichiarazione").toString();
								statoOccupazionale.setLimiteReddito(
										limiteReddito.calcola(LimiteReddito.DIPENDENTE, dataInizioDid));
							} else {
								statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
							}
						}
						break;

					case Contratto.DIP_TI:
						// Il contratto e' a tempo indeterminato: perdo comunque
						// lo stato di occupazione
						dataInizioDid = "";
						if (Controlli.inCollocamentoMirato(cm, dataRif)) {
							if (Controlli.annoDIDeqAnnoMov(dataInizio, did)) {
								dataInizioDid = did.getAttribute("datdichiarazione").toString();
								statoOccupazionale
										.setLimiteReddito(limiteReddito.calcola(LimiteReddito.CM, dataInizioDid));
							} else {
								statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
							}
						} else {
							if (Controlli.annoDIDeqAnnoMov(dataInizio, did)) {
								dataInizioDid = did.getAttribute("datdichiarazione").toString();
								statoOccupazionale.setLimiteReddito(
										limiteReddito.calcola(LimiteReddito.DIPENDENTE, dataInizioDid));
							} else {
								statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
							}
						}
						break;
					}
					// Il reddito non va considerato per il periodo che si trova
					// in mobilita
					// se esistono periodi di mobilità
					if (listaMobilita.size() > 0 && !gestioneFornero) {
						ArrayList listaGiorni = Controlli.calcolaGiorniMovimento(listaMobilita, movimento);
						Integer ggDiLavoroMov = (Integer) listaGiorni.get(0);
						Integer ggDiLavoroMovSucc = (Integer) listaGiorni.get(1);
						ggDiLavoro = ggDiLavoroMov.intValue();
						ggDiLavoroAnnoSuccessivo = ggDiLavoroMovSucc.intValue();
					} else {
						if (did != null) {
							if (DateUtils.getAnno(dataInizio) > DateUtils.getAnno(dataDichDid)) {
								ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFine, dataInizio);
								ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine,
										dataInizio);
							} else {
								ggDiLavoro = Controlli.getNumeroGiorniDiLavoroAnnoDid(dataInizio, dataFine, did);
								ggDiLavoroAnnoSuccessivo = Controlli.getNumeroGiorniDiLavoroAnnoSuccDid(dataInizio,
										dataFine, did);
							}
						} else {
							ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFine, dataInizio);
							ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine,
									dataInizio);
						}
					}

					// DEVO CALCOLARE IL REDDITO NEL CASO DI PROROGA
					// CONSIDERANDO I GIORNI LAVORATIVI E IL REDDITO
					// IN OGNI INTERVALLO (REDDITOAVV*GGAVV + REDDITOPRO*GGPRO)

					if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
						Vector prec = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
						SourceBean movimentoAvv = null;
						SourceBean movimentoSucc = null;
						int annoInizioMov = 0;
						String dataInizioPrec = "";
						String dataInizioSucc = "";
						String dataFinePrec = "";
						BigDecimal retribuzionePrec = null;
						int ggDiLavoroPrec = 0;
						int ggDiLavoroAnnoSuccPrec = 0;
						String dataInizioPro = "";
						Object dataFinePro = "";
						BigDecimal retribuzionePro = null;
						int ggDiLavoroPro = 0;
						int precSize = prec.size();

						for (int k = 0; k < precSize; k++) {
							movimentoAvv = (SourceBean) prec.get(k);
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
							retribuzionePrec = Retribuzione.getRetribuzioneMen(movimentoAvv);
							// Il reddito non va considerato per
							// trasformazioni/proroghe che si trovano in un
							// periodo di mobilità
							if (listaMobilita.size() > 0 && !gestioneFornero) {
								ArrayList listaGiorni = Controlli.calcolaGiorniMovimento(listaMobilita, movimentoAvv);
								Integer ggDiLavoroMov = (Integer) listaGiorni.get(0);
								Integer ggDiLavoroMovSucc = (Integer) listaGiorni.get(1);
								ggDiLavoroPrec = ggDiLavoroMov.intValue();
								ggDiLavoroAnnoSuccPrec = ggDiLavoroMovSucc.intValue();
								if (retribuzionePrec != null
										&& (tipoDichiarazione == StatoOccupazionaleManager.NO_SANAMENTO_SITUAZIONE
												|| tipoDichiarazione == StatoOccupazionaleManager.SANAMENTO_DETTAGLIO)) {
									if (!codMonoTipoAss.equals("T")) {
										statoOccupazionale.aggiorna(ggDiLavoroPrec, retribuzionePrec.doubleValue());
										statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccPrec,
												retribuzionePrec.doubleValue());
									}
								} else {
									// non devo calcolare il reddito (reddito
									// alto)
									exit = false;
									if (annoPrimo == DateUtils.getAnno(dataInizioPrec)) {
										noReddito = true;
									}
								}
							} else {
								if (retribuzionePrec != null
										&& (tipoDichiarazione == StatoOccupazionaleManager.NO_SANAMENTO_SITUAZIONE
												|| tipoDichiarazione == StatoOccupazionaleManager.SANAMENTO_DETTAGLIO)) {
									if (k == 0) {
										annoInizioMov = DateUtils.getAnno(dataInizioPrec);
										ggDiLavoroPrec = ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec,
												dataFinePrec, dataInizioPrec);
										ggDiLavoroAnnoSuccPrec = ControlliExt
												.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataInizioPrec);
										if (!codMonoTipoAss.equals("T")) {
											statoOccupazionale.aggiorna(ggDiLavoroPrec, retribuzionePrec.doubleValue());
											statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccPrec,
													retribuzionePrec.doubleValue());
										}
									} else {
										if (DateUtils.getAnno(dataInizioPrec) == annoInizioMov) {
											ggDiLavoroPrec = ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec,
													dataFinePrec, dataInizioPrec);
											ggDiLavoroAnnoSuccPrec = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(
													dataFinePrec, dataInizioPrec);
											if (!codMonoTipoAss.equals("T")) {
												statoOccupazionale.aggiorna(ggDiLavoroPrec,
														retribuzionePrec.doubleValue());
												statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccPrec,
														retribuzionePrec.doubleValue());
											}
										} else {
											ggDiLavoroAnnoSuccPrec = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccProTra(
													dataFinePrec, dataInizioPrec);
											if (!codMonoTipoAss.equals("T")) {
												statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccPrec,
														retribuzionePrec.doubleValue());
											}
										}
									}
								} else {
									// non devo calcolare il reddito (reddito
									// alto)
									exit = false;
									if (annoPrimo == DateUtils.getAnno(dataInizioPrec)) {
										noReddito = true;
									}
								}
							}
						} // end for (int k = 0; k < precSize; k++)

					} else {
						if (!codMonoTipoAss.equals("T")) {
							statoOccupazionale.aggiorna(ggDiLavoro, retribuzione.doubleValue());
							statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
									retribuzione.doubleValue());
						}
					}

				} else {
					// considero il limite reddito superato e non calcolo la
					// retribuzione
					// in realtà la retribuzione potrebbe servirmi per il
					// relativo campo nella am_stato_occupazionale
					exit = false;
					noReddito = true;
				}

				break;

			case MovimentoBean.CESSAZIONE:

				break;
			case MovimentoBean.TRASFORMAZIONE:
				String flgModReddito = (String) movimento.getAttribute(MovimentoBean.DB_FLG_MOD_REDDITO);
				String flgModTempo = (String) movimento.getAttribute(MovimentoBean.DB_FLG_MOD_TEMPO);
				if (movimento.containsAttribute("TRASFORMAZIONE_IN_MOBILITA")) {
					// in questo caso la trasformazione è impattante
					dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
					dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
					if (dataFine == null)
						dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE);
					limiteReddito = new LimiteRedditoExt(dataInizio);
					retribuzione = Retribuzione.getRetribuzioneMen(movimento);
					if (retribuzione != null && (tipoDichiarazione == StatoOccupazionaleManager.NO_SANAMENTO_SITUAZIONE
							|| tipoDichiarazione == StatoOccupazionaleManager.SANAMENTO_DETTAGLIO)) {
						// calcolo del reddito
						switch (Contratto.getTipoContratto(movimento)) {
						case Contratto.AUTONOMO:

							// come minimo ricalcola il limite
						case Contratto.COCOCO:

							// anche qui va ricalcolato il limite del reddito
							if (Controlli.inCollocamentoMirato(cm, dataRif)) {
								statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
							} else {
								// controllare se nella data inizio movimento ci
								// sono
								// rapporti di lavoro dipendente aperti. In tal
								// caso bisogna considerare
								// limite reddito di lavoro dipendente
								boolean esisteMovimentoDip = false;
								if (movimentiAnno.size() > 0) {
									esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(dataInizio,
											movimentiAnno);
								}
								if (esisteMovimentoDip) {
									String dataDid = "";
									dataDid = did.getAttribute("datdichiarazione").toString();
									statoOccupazionale
											.setLimiteReddito(limiteReddito.calcola(LimiteReddito.DIPENDENTE, dataDid));
								} else {
									statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.AUTONOMO));
								}
							}
							break;

						case Contratto.DIP_TD:
							// suppongo che il limite sia minore o uguale a
							// quello passato come parametro al metodo
							if (Controlli.inCollocamentoMirato(cm, dataRif)) {
								statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
							} else {
								if (Controlli.annoDIDeqAnnoMov(dataInizio, did)) {
									dataInizioDid = did.getAttribute("datdichiarazione").toString();
									statoOccupazionale.setLimiteReddito(
											limiteReddito.calcola(LimiteReddito.DIPENDENTE, dataInizioDid));
								} else {
									statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
								}
							}
							break;

						case Contratto.DIP_TI:

							// Il contratto e' a tempo indeterminato: perdo
							// comunque lo stato di occupazione
							if (Controlli.inCollocamentoMirato(cm, dataRif)) {
								if (Controlli.annoDIDeqAnnoMov(dataInizio, did)) {
									dataInizioDid = did.getAttribute("datdichiarazione").toString();
									statoOccupazionale
											.setLimiteReddito(limiteReddito.calcola(LimiteReddito.CM, dataInizioDid));
								} else {
									statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
								}
							} else {
								if (Controlli.annoDIDeqAnnoMov(dataInizio, did)) {
									dataInizioDid = did.getAttribute("datdichiarazione").toString();
									statoOccupazionale.setLimiteReddito(
											limiteReddito.calcola(LimiteReddito.DIPENDENTE, dataInizioDid));
								} else {
									statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
								}
							}
							// dataFine = null;
							break;
						}

						ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFine, dataInizio);
						ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine,
								dataInizio);
						if (!codMonoTipoAss.equals("T")) {
							statoOccupazionale.aggiorna(ggDiLavoro, retribuzione.doubleValue());
							statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
									retribuzione.doubleValue());
						}

					} else {
						// considero il limite reddito superato e non calcolo la
						// retribuzione
						// in realtà la retribuzione potrebbe servirmi per il
						// relativo campo nella am_stato_occupazionale
						exit = false;
						noReddito = true;
					}
				} else {
					if ((flgModReddito != null && flgModReddito.equals("S"))
							|| (flgModTempo != null && flgModTempo.equals("S"))) {
						// e' cambiato o il reddito o la durata del contratto.
						// Questo ha impatti amministrativi
						// per il momento non so come reperire il movimento
						// collegato a quello di trasformazione
						// non faccio niente
						exit = true;
					} else {
						// lo stato occupazionale non cambia. Probabilmente e'
						// cambiata la mansione ma cio' non ha impatti a livello
						// amministrativo
						exit = true;
					}
				}
				break;

			default:
			} // ho calcolato il limite del reddito oltre il quale il
				// lavoratore perde lo stato di disoccupato

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!exit) {
			boolean iscrittoCM = Controlli.inCollocamentoMiratoAllaData(listaDisabiliCM, dataRif);
			if (gestioneDecreto150 && !iscrittoCM) {
				boolean isCatParticolare150 = false;
				isCatParticolare150 = Controlli.isCategoriaParticolareDecreto150(movimento, dataRif);
				if (isCatParticolare150) {
					statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO_CAT_SPECIALE);
				} else {
					statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
				}
			} else {
				boolean redditoSup = Controlli.redditoSuperiore(statoOccupazionale, did, listaMobilita, cm, movimento,
						movimentiAnno, limiteReddito, dataRif, txExecutor);
				// Arrivo con lo stato occ. Disoccuopato: Occupato a rischio disoccupazione solo quando in presenza di
				// una
				// DID esiste un solo movimento a TI aperto con superamento reddito e nella DID è stato indicato il flag
				// rischio disoccupazione
				if (statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.B3
						&& (noReddito || redditoSup)) {
					statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
				} else {
					if (tipoDichiarazione == StatoOccupazionaleManager.SANAMENTO_GENERICA
							|| (!noReddito && !redditoSup)) {

						statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_BASSO);

						// chiamo il calcolo del reddito per una data
						// chiamo il calcolo del reddito per il prossimo anno
						// prendo il massimo tra i due redditi presunti
						// rifaccio il confronto, anzi no:))
						// chiamo il metodo getRedditoMax(statoOccupazionale)
					} else {
						// debbo controllare il tipo e la durata dell' ultimo movimento
						// e se il lavoratore e' giovane o meno
						dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
						dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
						boolean isCatParticolare = false;
						if (!gestioneFornero) {
							isCatParticolare = Controlli.isCategoriaParticolare(movimento);
						} else {
							isCatParticolare = Controlli.isCategoriaParticolareDecretoFornero(movimento, dataRif, true);
						}
						if (!gestioneFornero) {
							if ((Contratto.getTipoContratto(movimento) == Contratto.DIP_TD) && isCatParticolare
									&& (Controlli.numeroMesiDiLavoroMovimento(movimento) >= 1
									// ||
									// Controlli.numeroGiorniDiLavoroCommerciali(dataInizio,
									// dataFine) > 15
									)) {
								// reddito alto ma appartenenza alla categoria del contratto
								// td minore di 8 mesi (6 mesi dal decreto Fornero)
								statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO_CAT_SPECIALE);
							} else {
								if (Contratto.getTipoContratto(movimento) == Contratto.DIP_TI && dataFine != null) {
									int mesiLavoro = Controlli.numeroMesiDiLavoro(dataInizio, dataFine);
									if (mesiLavoro >= 1) {
										statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
									} else {
										if (redditoSup || noReddito) {
											statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
										} else {
											statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.MOV_MIN_15GG);
										}
									}
								} else {
									// reddito alto e non appartenenza alla categoria del
									// contratto td minore di 8 mesi
									if (Controlli.numeroMesiDiLavoroMovimento(movimento) >= 1)
										statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
									else
										statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.MOV_MIN_15GG);
								}
							}
						} else {
							// gestione contratti subordinati o parasubordinati (non fanno sospensione)
							if (isCatParticolare) {
								statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO_CAT_SPECIALE);
							} else {
								if (Contratto.getTipoContratto(movimento) == Contratto.DIP_TI && dataFine != null) {
									int mesiLavoro = Controlli.numeroMesiDiLavoro(dataInizio, dataFine);
									if (mesiLavoro >= 1) {
										statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
									} else {
										if (redditoSup || noReddito) {
											statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
										} else {
											statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.MOV_MIN_15GG);
										}
									}
								} else {
									// reddito alto e non appartenenza alla categoria del
									// contratto td minore di 8 mesi
									if (Controlli.numeroMesiDiLavoroDecretoFornero(movimento) >= 1) {
										statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.REDDITO_ALTO);
									} else {
										statoOccupazionale.calcolaNuovoStato(StatoOccupazionaleBase.MOV_MIN_15GG);
									}
								}
							}
						}
					}
				}
			}
		}
		// else ho letto un movimento che mi ha permesso di calcolare lo so; non
		// devo fare altro

		return statoOccupazionale;
	}
}