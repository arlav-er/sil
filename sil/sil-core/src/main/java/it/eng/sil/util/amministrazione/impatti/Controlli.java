package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.movimenti.processors.Warning;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.Utils;

public class Controlli {

	private static Map contratti = null;

	static {
		contratti = new HashMap();
		contratti.put(Contratto.COD_AUTONOMO, new BigInteger(String.valueOf(Contratto.AUTONOMO)));
		contratti.put(Contratto.COD_COCOCO, new BigInteger(String.valueOf(Contratto.COCOCO)));
		contratti.put(Contratto.COD_DIP_TD, new BigInteger(String.valueOf(Contratto.DIP_TD)));
		contratti.put(Contratto.COD_DIP_TI, new BigInteger(String.valueOf(Contratto.DIP_TI)));
	}

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Controlli.class.getName());

	/**
	 * metodo chiamato nel calcolo dello stato occupazionale per controllare se c'è stato superamneto del limite nei
	 * movimenti aperti del lavoratore
	 * 
	 * @param statoOccupazionale
	 * @param did
	 * @param listaMobilita(Utilizzato
	 *            nel calcolo del reddito dei movimenti togliendo il reddito nei periodi di mobilita)
	 * @param cm
	 * @param movimento
	 * @param movimentiAnno
	 * @param limiteReddito
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static boolean redditoSuperiore(StatoOccupazionaleBean statoOccupazionale, SourceBean did,
			List listaMobilita, SourceBean cm, SourceBean movimento, Vector movimentiAnno,
			LimiteRedditoExt limiteReddito, String dataRif, TransactionQueryExecutor txExecutor) throws Exception {

		SourceBean sbMobilita = null;
		String cdnlavoratore = "";

		if (did != null && did.containsAttribute(DidBean.CDNLAVORATORE)) {
			cdnlavoratore = did.containsAttribute(DidBean.CDNLAVORATORE)
					? did.getAttribute(DidBean.CDNLAVORATORE).toString()
					: "";
		}
		if (cdnlavoratore.equals("")) {
			cdnlavoratore = movimento.containsAttribute(MovimentoBean.DB_CDNLAVORATORE)
					? movimento.getAttribute(MovimentoBean.DB_CDNLAVORATORE).toString()
					: "";
		}
		if (cdnlavoratore.equals("")) {
			if (listaMobilita.size() > 0) {
				sbMobilita = (SourceBean) listaMobilita.get(0);
				cdnlavoratore = sbMobilita.containsAttribute(MobilitaBean.DB_CDNLAVORATORE)
						? sbMobilita.getAttribute(MobilitaBean.DB_CDNLAVORATORE).toString()
						: "";
			}
		}

		if (!ControlloReddito.minoreDelLimite(statoOccupazionale, movimento,
				limiteReddito.getLimiteAnnoSuccessivo(movimento, cdnlavoratore, cm, txExecutor)))
			return true;

		BigDecimal prgMovimento = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		if (movimentiAnno == null) {
			// questa situazione non si dovrebbe mai verificare in quanto le query chiamate successivamente
			// GET_MOVIMENTI_ANNO_DID o GET_MOVIMENTI_ANNO non tengono in considerazione il fatto che i movimenti che
			// cadono nell'anno in cui
			// si sta calcolando lo stato occupazionale devono avere data inizio minore o uguale alla data inizio
			// movimento corrente (dataRif)
			movimentiAnno = getMovimentiAnno(statoOccupazionale, did, movimento);
			if (movimentiAnno != null) {
				Vector movimentiAnnoFinal = new Vector();
				int numMovAnno = movimentiAnno.size();
				for (int j = 0; j < numMovAnno; j++) {
					SourceBean movAnno = null;
					Object o = movimentiAnno.get(j);
					if (o instanceof SourceBean) {
						movAnno = (SourceBean) o;
					} else {
						movAnno = ((MovimentoBean) o).getSource();
					}
					BigDecimal prg = (BigDecimal) movAnno.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
					String dataMov = (String) movAnno.getAttribute("datInizioMov");
					String codTipoMov = (String) movAnno.getAttribute("codTipoMov");
					String codTipoContratto = movAnno.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
							? movAnno.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
							: "";
					if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)
							|| codTipoContratto.equalsIgnoreCase("Z.09.02")) {
						continue;
					}
					if (prg != null && prgMovimento != null && prg.equals(prgMovimento)) {
						continue;
					}
					if (DateUtils.compare(dataMov, dataRif) <= 0) {
						movimentiAnnoFinal.add(movAnno);
					}
				}
				movimentiAnno = new Vector(movimentiAnnoFinal);
			}
		}
		// vado a vedere se gli altri movimenti aperti provocano insieme il superamento del limite
		return redditoSuperiore(statoOccupazionale, cm, did, listaMobilita, movimentiAnno, movimento, limiteReddito,
				dataRif, txExecutor);
	}

	public static boolean redditoSuperiore(StatoOccupazionaleBean statoOccupazionale, SourceBean cm, SourceBean did,
			List listaMobilita, Vector rows, SourceBean movimento, LimiteRedditoExt limiteReddito, String dataRif,
			TransactionQueryExecutor txExecutor) throws Exception {

		String cdnlavoratore = "";
		if (did != null && did.containsAttribute(DidBean.CDNLAVORATORE)) {
			cdnlavoratore = did.getAttribute(DidBean.CDNLAVORATORE).toString();
		}
		redditoMovimentiAnno(statoOccupazionale, cm, did, listaMobilita, rows, movimento, limiteReddito, dataRif,
				txExecutor);
		return !ControlloReddito.minoreDelLimite(statoOccupazionale, movimento,
				limiteReddito.getLimiteAnnoSuccessivo(movimento, cdnlavoratore, cm, txExecutor));
	}

	/**
	 * ritorna i movimenti di un lavoratore a cavallo di un anno
	 * 
	 * @param statoOccupazionale
	 * @param did
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static Vector getMovimentiAnno(StatoOccupazionaleBean statoOccupazionale, SourceBean did,
			SourceBean movimento) throws Exception {
		String dataRif = null;
		String dataMov = (String) movimento.getAttribute("datInizioMov");
		String dataInizio = null;
		String dataFine = null;
		Vector rows = null;
		if (statoOccupazionale.getStatoOccupazionale() != StatoOccupazionaleBean.A22) {
			if ((did != null && did.containsAttribute("datdichiarazione")) && (DateUtils.getAnno(dataMov) == DateUtils
					.getAnno((String) did.getAttribute("datdichiarazione")))) {
				dataRif = (String) did.getAttribute("datdichiarazione");
				dataInizio = dataRif;
				dataFine = "31/12/" + DateUtils.getAnno(dataRif);
				rows = DBLoad.getMovimentiDID(dataInizio, dataFine, statoOccupazionale.getCdnLavoratore());
				rows = Controlli.gestisciPeriodiIntermittentiDid(rows, dataInizio, dataFine, null);
			} else {
				dataRif = "01/01/" + DateUtils.getAnno(dataMov);
				dataInizio = "01/01/" + DateUtils.getAnno(dataRif);
				dataFine = "31/12/" + DateUtils.getAnno(dataRif);
				rows = DBLoad.getMovimentiAnno(dataInizio, dataFine, statoOccupazionale.getCdnLavoratore());
				rows = Controlli.gestisciPeriodiIntermittenti(rows, dataInizio, dataFine, null);
			}
		} else {
			rows = new Vector(0);
		}
		return rows;
	}

	/**
	 * aggiorna il reddito nell'anno della did e nell'anno successivo in base ai movimenti presenti nel vettore rows
	 * preso come parametro
	 * 
	 * @param statoOccupazionale
	 * @param cm
	 * @param did
	 * @param rows
	 * @param movimento
	 * @param limiteReddito
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean redditoMovimentiAnno(StatoOccupazionaleBean statoOccupazionale, SourceBean cm,
			SourceBean did, List listaMobilita, Vector rows, SourceBean movimento, LimiteRedditoExt limiteReddito,
			String dataRif, TransactionQueryExecutor txExecutor) throws Exception {

		/*
		 * 27/10/2004 Al metodo aggiungiProroghe viene anche passato come ultimo parametro la data fino a cui arrivare a
		 * considerare i movimenti a ritroso (si considera l'anno). Nel caso della DID viene passato null, in quanto la
		 * logica è già implementata.
		 */
		boolean gestioneFornero = (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0);
		int ggDiLavoro = 0;
		int ggDiLavoroAnnoSuccessivo = 0;
		Vector movimentiTrattati = new Vector();
		boolean redditoCalcolato = false;
		String dataInizioMovimento = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		rows = aggiungiProroghe(rows, movimento, dataInizioMovimento, txExecutor);
		SourceBean mov = null;

		boolean movTrattato = false;
		String codMonoTipoAss = "";
		String codTipoAvviamento = "";
		String codTipoMov = "";
		BigDecimal prgMov = null;
		int rowsSize = rows.size();
		for (int i = 0; i < rowsSize; i++) {

			redditoCalcolato = false;
			movTrattato = false;
			Object o = rows.get(i);
			if (o instanceof SourceBean) {
				mov = (SourceBean) o;
			} else {
				mov = ((MovimentoBean) o).getSource();
			}

			boolean ret = mov.containsAttribute("FLAG_IN_INSERIMENTO");
			if (!ret) {
				if (MovimentoBean.getTipoMovimento(mov) == MovimentoBean.CESSAZIONE
						|| (mov.containsAttribute("codStatoAtto") && !mov.getAttribute("codStatoAtto").equals("PR")))
					continue;
			}
			codMonoTipoAss = mov.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? mov.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			codTipoAvviamento = mov.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? mov.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			codTipoMov = mov.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
					? mov.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
					: "";
			// non considerare I TIROCINI, TIPO AVVIAMENTO Z.09.02(vecchio codice RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (codMonoTipoAss.equals("T") || codTipoAvviamento.equals("RS3") || codTipoAvviamento.equals("Z.09.02")) {
				continue;
			}

			BigDecimal prgMovTest = null;
			prgMov = (BigDecimal) mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			if (codTipoMov.equalsIgnoreCase("PRO") || codTipoMov.equalsIgnoreCase("TRA")) {
				if (prgMov != null) {
					for (int j = 0; j < movimentiTrattati.size(); j++) {
						prgMovTest = (BigDecimal) movimentiTrattati.get(j);
						if (prgMovTest != null && prgMovTest.equals(prgMov)) {
							movTrattato = true;
							break;
						}
					}
				}
			}
			if (movTrattato)
				continue; // proroga e/o trasformazione già trattata con l'avviamento
			// per il momento suppongo che il contratto sia di assunzione ........................
			String dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			String dataInizioAppoggio = dataInizio;
			String dataFine = (String) mov.getAttribute(MovimentoBean.DB_DATA_FINE);

			if (dataInizioMovimento != null && !dataInizioMovimento.equals("")) {
				if (DateUtils.getAnno(dataInizio) < DateUtils.getAnno(dataInizioMovimento)
						&& (dataFine == null || dataFine.equals("")
								|| DateUtils.getAnno(dataFine) >= DateUtils.getAnno(dataInizioMovimento))) {
					dataInizioAppoggio = "01/01/" + DateUtils.getAnno(dataInizioMovimento);
					if (mov.containsAttribute(MovimentoBean.DB_DATA_INIZIO)) {
						mov.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioAppoggio);
					}
				}
			}

			// INIZIO:il reddito non va considerato per movimenti che si trovano in un periodo di mobilità
			if (listaMobilita.size() > 0 && !gestioneFornero) {
				// il reddito non va considerato per il periodo che si trova in mobilita
				ArrayList listaGiorni = Controlli.calcolaGiorniMovimento(listaMobilita, mov);
				Integer ggDiLavoroMov = (Integer) listaGiorni.get(0);
				Integer ggDiLavoroMovSucc = (Integer) listaGiorni.get(1);
				ggDiLavoro = ggDiLavoroMov.intValue();
				ggDiLavoroAnnoSuccessivo = ggDiLavoroMovSucc.intValue();
				BigDecimal retribuzione = Retribuzione.getRetribuzioneMen(mov);
				if (retribuzione == null) {
					// se la retribuzione non e' specificata allora si considera che il limite sia superato
					statoOccupazionale.setRedditoAlto();
					return statoOccupazionale;
				}
				statoOccupazionale.aggiorna(ggDiLavoro, retribuzione.doubleValue());
				statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());
				redditoCalcolato = true;
				if (mov.containsAttribute(MovimentoBean.DB_DATA_INIZIO)) {
					mov.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizio);
				}
			} else {
				if (codTipoMov.equalsIgnoreCase("AVV") && mov.containsAttribute("MOVIMENTI_PROROGATI")) {
					BigDecimal retribuzionePrec = null;
					Vector prec = (Vector) mov.getAttribute("MOVIMENTI_PROROGATI");
					SourceBean movimentoAvv = null;
					SourceBean movimentoSucc = null;
					String dataInizioPrec = "";
					String dataInizioSucc = "";
					String dataFinePrec = "";
					BigDecimal prgCurr = null;
					if (prec.size() > 0) {
						redditoCalcolato = true;
						int precSize = prec.size();
						for (int k = 0; k < precSize; k++) {
							movimentoAvv = (SourceBean) prec.get(k);
							int kSucc = k + 1;
							prgCurr = (BigDecimal) movimentoAvv.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
							if (prgCurr != null)
								movimentiTrattati.add(prgCurr);
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

							dataInizioAppoggio = dataInizioPrec;
							if (dataInizioMovimento != null && !dataInizioMovimento.equals("")) {
								if (DateUtils.getAnno(dataInizioPrec) < DateUtils.getAnno(dataInizioMovimento)
										&& (dataFinePrec == null || dataFinePrec.equals("") || DateUtils
												.getAnno(dataFinePrec) >= DateUtils.getAnno(dataInizioMovimento))) {
									dataInizioAppoggio = "01/01/" + DateUtils.getAnno(dataInizioMovimento);
									if (movimentoAvv.containsAttribute(MovimentoBean.DB_DATA_INIZIO)) {
										movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioAppoggio);
									}
								} else {
									if (DateUtils.getAnno(dataInizioPrec) < DateUtils.getAnno(dataInizioMovimento)
											&& dataFinePrec != null && !dataFinePrec.equals("") && DateUtils
													.getAnno(dataFinePrec) < DateUtils.getAnno(dataInizioMovimento)) {
										continue;
									} else {
										if (DateUtils.getAnno(dataInizioPrec) > DateUtils
												.getAnno(dataInizioMovimento)) {
											break;
										}
									}
								}
							}

							// prendo la retribuzione
							retribuzionePrec = Retribuzione.getRetribuzioneMen(movimentoAvv);
							if (retribuzionePrec == null) {
								// se la retribuzione non e' specificata allora si considera che il limite sia superato
								statoOccupazionale.setRedditoAlto();
								return statoOccupazionale;
							}
							if (did != null) {
								String dataDichDid = did.getAttribute(DidBean.DB_DAT_INIZIO).toString();
								if (DateUtils.getAnno(dataInizioAppoggio) > DateUtils.getAnno(dataDichDid)) {
									ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioAppoggio, dataFinePrec,
											dataInizioAppoggio);
									ggDiLavoroAnnoSuccessivo = ControlliExt
											.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataInizioAppoggio);
								} else {
									ggDiLavoro = Controlli.getNumeroGiorniDiLavoroAnnoDid(dataInizioAppoggio,
											dataFinePrec, did);
									ggDiLavoroAnnoSuccessivo = Controlli
											.getNumeroGiorniDiLavoroAnnoSuccDid(dataInizioAppoggio, dataFinePrec, did);
								}
							} else {
								ggDiLavoro = getNumeroGiorniDiLavoro(dataInizioAppoggio, dataFinePrec, dataRif);
								ggDiLavoroAnnoSuccessivo = getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataRif);
							}
							statoOccupazionale.aggiorna(ggDiLavoro, retribuzionePrec.doubleValue());
							statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo,
									retribuzionePrec.doubleValue());

							if (movimentoAvv.containsAttribute(MovimentoBean.DB_DATA_INIZIO)) {
								movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioPrec);
							}
						} // end for (int k = 0; k < precSize; k++)
					}
				}
			}

			if (!redditoCalcolato) {
				BigDecimal retribuzione = Retribuzione.getRetribuzioneMen(mov);
				if (retribuzione == null) {
					// se la retribuzione non e' specificata allora si considera che il limite sia superato
					statoOccupazionale.setRedditoAlto();
					return statoOccupazionale;
				}
				if (did != null) {
					String dataDichDid = did.getAttribute(DidBean.DB_DAT_INIZIO).toString();
					if (DateUtils.getAnno(dataInizioAppoggio) > DateUtils.getAnno(dataDichDid)) {
						ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioAppoggio, dataFine,
								dataInizioAppoggio);
						ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine,
								dataInizioAppoggio);
					} else {
						ggDiLavoro = Controlli.getNumeroGiorniDiLavoroAnnoDid(dataInizioAppoggio, dataFine, did);
						ggDiLavoroAnnoSuccessivo = Controlli.getNumeroGiorniDiLavoroAnnoSuccDid(dataInizioAppoggio,
								dataFine, did);
					}
				} else {
					ggDiLavoro = getNumeroGiorniDiLavoro(dataInizioAppoggio, dataFine, dataRif);
					ggDiLavoroAnnoSuccessivo = getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine, dataRif);
				}
				statoOccupazionale.aggiorna(ggDiLavoro, retribuzione.doubleValue());
				statoOccupazionale.aggiornaAnnoSuccessivo(ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());
				if (mov.containsAttribute(MovimentoBean.DB_DATA_INIZIO)) {
					mov.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizio);
				}
			}

			if (!inCollocamentoMirato(cm, dataInizioMovimento)) {
				switch (Contratto.getTipoContratto(mov)) {
				case Contratto.AUTONOMO:
				case Contratto.COCOCO:
					if (statoOccupazionale.getLimiteReddito() < limiteReddito.get(LimiteReddito.AUTONOMO))
						statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.AUTONOMO));
					break;
				case Contratto.DIP_TD:
				case Contratto.DIP_TI:
					double limite = limiteReddito.get(LimiteReddito.DIPENDENTE);
					if (annoDIDeqAnnoMov(dataInizioAppoggio, did))
						limite = limiteReddito.calcola(LimiteReddito.DIPENDENTE, dataInizioAppoggio);
					if (statoOccupazionale.getLimiteReddito() < limite)
						statoOccupazionale.setLimiteReddito(limite);
					break;
				}
			} else
				statoOccupazionale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
		}
		return statoOccupazionale;
	}

	/**
	 * 
	 * @param anno
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public static Vector componiVettoreProrogati(int anno, Vector p) throws Exception {
		Vector tmp = new Vector();
		// Scorrre indietro i movimenti prorogati solo se dell'anno della proroga considerata
		if (anno != 0) {
			int pSize = p.size();
			for (int j = 0; j < pSize; j++) {
				Object o = p.get(j);
				SourceBean mb = null;
				mb = (SourceBean) o;
				if (DateUtils.getAnno(mb.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()) == anno)
					tmp.add(mb);
			}
		} else
			tmp = p;
		return tmp;
	}

	/**
	 * 
	 * @param rows
	 * @param mov
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static Vector aggiungiProroghe(Vector rows, SourceBean mov, String dataRif,
			TransactionQueryExecutor txExecutor) throws Exception {
		Vector v = new Vector(rows);
		Vector p = null;
		Vector tmp = new Vector();
		int anno = 0;
		if (dataRif != null && !dataRif.equals(""))
			anno = DateUtils.getAnno(dataRif);
		if (mov != null && MovimentoBean.getTipoMovimento(mov) == MovimentoBean.PROROGA) {
			if (!mov.containsAttribute("MOVIMENTI_PROROGATI"))
				p = DBLoad.getMovimentiProroghe(mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC), txExecutor);
			else
				p = (Vector) mov.getAttribute("MOVIMENTI_PROROGATI");
			// Scorre indietro i movimenti prorogati solo se dell'anno della proroga considerata
			tmp = componiVettoreProrogati(anno, p);
			aggiornaMovimentiAnno(v, tmp);
		}
		for (int i = 0; i < v.size(); i++) {
			Object o = v.get(i);
			SourceBean m = null;
			if (o instanceof SourceBean)
				m = (SourceBean) o;
			else if (o instanceof MovimentoBean)
				m = ((MovimentoBean) o).getSource();
			else
				continue;
			if (MovimentoBean.getTipoMovimento(m) == MovimentoBean.PROROGA) {
				if (!m.containsAttribute("MOVIMENTI_PROROGATI"))
					p = DBLoad.getMovimentiProroghe(m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC), txExecutor);
				else
					p = (Vector) m.getAttribute("MOVIMENTI_PROROGATI");
				// Scorre indietro i movimenti prorogati solo se dell'anno della proroga considerata
				tmp = componiVettoreProrogati(anno, p);
				aggiornaMovimentiAnno(v, tmp);
			}
		}
		return v;
	}

	/**
	 * 
	 * @param rows
	 * @param proroghe
	 * @throws Exception
	 */
	public static void aggiornaMovimentiAnno(Vector rows, Vector proroghe) throws Exception {
		int prorogheSize = proroghe.size();
		for (int j = 0; j < prorogheSize; j++) {
			SourceBean p = (SourceBean) proroghe.get(j);
			BigDecimal prgPro = (BigDecimal) p.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			if (prgPro == null)
				continue;
			int i = 0;
			for (; i < rows.size(); i++) {
				Object o = rows.get(i);
				SourceBean m = null;
				if (o instanceof SourceBean)
					m = (SourceBean) o;
				else if (o instanceof MovimentoBean)
					m = ((MovimentoBean) o).getSource();
				else
					continue;
				BigDecimal prg = (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
				if (prg != null && prgPro.intValue() == prg.intValue())
					break;
			}
			if (i == rows.size())
				rows.add(p);
		}
	}

	/**
	 * controllo della categoria di appartenenza del lavoratore: giovane|adolescente|null (non giovane non viene piu'
	 * gestito)
	 * 
	 * @param nascita
	 * @param dataInizioCalcolo
	 * @param flgObbligo
	 * @param flgLaurea
	 * @return "Adolescente", "Giovane", null
	 */
	public static String getCat181(String nascita, String dataInizioCalcolo, String flgObbligo, String flgLaurea) {
		if (flgLaurea == null) {
			flgLaurea = "N";
		}

		if (flgObbligo == null) {
			flgObbligo = "N";
		}

		try {
			if (maggioreDiUno(nascita, 15, dataInizioCalcolo) && minoreDiUno(nascita, 18, dataInizioCalcolo)
					&& flgObbligo.equals("S")) {
				return "Adolescente";
			}

			if (maggioreDiUno(nascita, 18, dataInizioCalcolo)
					&& ((minoreDiUno(nascita, 30, dataInizioCalcolo) && flgLaurea.equals("S"))
							|| minoreDiUno(nascita, 26, dataInizioCalcolo))) {
				return "Giovane";
			}

			// se la persona ha un'età >= 30 anni e ha come titolo di studio la laurea oppure una
			// età >= 26 anni e non ha la laurea allora è considerata ADULTO
			if ((maggioreDiUno(nascita, 30, dataInizioCalcolo) && flgLaurea.equals("S"))
					|| (maggioreDiUno(nascita, 26, dataInizioCalcolo) && flgLaurea.equals("N"))) {
				return "Adulto";
			}

			return null;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Controlli.getCat181():", e);
			return null;
		}
	}

	/**
	 * Calcola il numero di mesi del contratto a tempo determinato di un lavoratore "giovane" o meno Calcolo: durata
	 * minore o uguale 8 mesi (4 se giovane)
	 * 
	 * @param movimento
	 * @return true se è in grado di mantenere lo stato di disoccupazione.
	 * @throws Exception
	 */
	public static boolean isCategoriaParticolare(SourceBean movimento) throws Exception {
		boolean ret = false;
		int contratto = Contratto.getTipoContratto(movimento);
		// TracerSingleton.log(Values.APP_NAME, TracerSingleton.DEBUG, "Controlli.isCategoriaParticolare(): movimento:"
		// + movimento.toString());

		if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
			Vector vAppoggio = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
			SourceBean movimentoAvv = (SourceBean) vAppoggio.get(vAppoggio.size() - 1);
			contratto = Contratto.getTipoContratto(movimentoAvv);
		}

		if (contratto == Contratto.DIP_TD) {
			int nMesi = numeroMesiDiLavoro(movimento);
			if (nMesi >= 8) {
				// sono sicuro che il lavoratore perdera' lo stato di disoccupazione
				ret = false;
			} else {
				if (nMesi < 4) {
					// sono certo che il lavoratore anche se giovane manterra' lo stato di disoccupazione
					ret = true;
				} else {
					// a questo punto debbo controllare se il tale appartiene alla categoria "giovane" ovvero "181"
					// ho bisogno di leggere alcuni dati relativi al lavoratore che al momento non ho
					SourceBean row = DBLoad.getCat181(movimento);
					String dataInizioMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
					if (dataInizioMov == null || dataInizioMov.equals(""))
						dataInizioMov = DateUtils.getNow();
					ret = StatoOccupazionaleManager.appartieneCat181(row, dataInizioMov);
					ret = !ret;
				}
			}
		}
		return ret;
	}

	/**
	 * Calcola il numero di mesi del contratto a tempo determinato di un lavoratore "giovane" o meno Calcolo: durata
	 * minore o uguale di 6 mesi (viene chiamata solo per annoRif >= anno decreto Fornero 2014) Per movimenti successivi
	 * al decreto 150 e lavoratore iscritto al CM, la gestione intermittente viene fatta in
	 * numeroMesiDiLavoroDecretoFornero(movimento)
	 * 
	 * @param movimento
	 * @return true se è in grado di mantenere lo stato di disoccupazione.
	 * @throws Exception
	 */
	public static boolean isCategoriaParticolareDecretoFornero(SourceBean movimento, String dataRif,
			boolean gestioneFornero) throws Exception {
		boolean ret = false;
		String flgSospensione = movimento.containsAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014)
				? movimento.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014).toString()
				: "";
		if (!gestioneFornero || flgSospensione.equalsIgnoreCase(MovimentoBean.SI)) {
			int nMesi = numeroMesiDiLavoroDecretoFornero(movimento);
			if (nMesi <= 6) {
				// sono sicuro che il lavoratore mantiene lo stato di disoccupazione
				ret = true;
			} else {
				if (!gestioneFornero) {
					SourceBean row = DBLoad.getCat181(movimento);
					String dataInizioMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
					if (dataInizioMov == null || dataInizioMov.equals("")) {
						dataInizioMov = DateUtils.getNow();
					}
					// 2013-2014 per considerare la sospensione ad 8 mesi per gli adulti
					// a questo punto debbo controllare se il tale appartiene alla categoria "giovane" ovvero "181"
					if (!StatoOccupazionaleManager.appartieneCat181(row, dataInizioMov)) {
						if (nMesi < 8) {
							ret = true;
						} else {
							ret = false;
						}
					} else {
						ret = false;
					}
				} else {
					ret = false;
				}
			}
		}
		return ret;
	}

	/**
	 * Calcola il numero di mesi del contratto a tempo determinato di un lavoratore "giovane" o meno Calcolo: durata
	 * minore o uguale di 6 mesi (viene chiamata solo per annoRif >= anno decreto 150) La gestione intermittenti viene
	 * fatta in numeroMesiDiLavoroDecreto150(movimento)
	 * 
	 * @param movimento
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static boolean isCategoriaParticolareDecreto150(SourceBean movimento, String dataRif) throws Exception {
		boolean ret = false;
		String flgSospensione = movimento.containsAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014)
				? movimento.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014).toString()
				: "";
		if (flgSospensione.equalsIgnoreCase(MovimentoBean.SI)) {
			int nMesi = numeroMesiDiLavoroDecreto150(movimento);
			if (nMesi <= 6) {
				// sono sicuro che il lavoratore mantiene lo stato di disoccupazione
				ret = true;
			} else {
				ret = false;
			}
		}
		return ret;
	}

	/**
	 * Calcola il numero di mesi del contratto a tempo determinato (per i movimenti che hanno impatti nella
	 * determinazione dello stato occupazionale alla chiusura della mobilità, o all'inizio del nuovo anno) di un
	 * lavoratore "giovane" o meno Calcolo: durata minore o uguale 8 mesi (4 se giovane)
	 * 
	 * @param movimento
	 * @return true se è in grado di mantenere lo stato di disoccupazione.
	 * @throws Exception
	 */
	public static boolean isCategoriaParticolare(int nMesi, SourceBean movimento) throws Exception {
		boolean ret = false;
		// TracerSingleton.log(Values.APP_NAME, TracerSingleton.DEBUG, "Controlli.isCategoriaParticolare(): movimento:"
		// + movimento.toString());

		if (nMesi >= 8) {
			// sono sicuro che il lavoratore perdera' lo stato di disoccupazione
			ret = false;
		} else {
			if (nMesi < 4) {
				// sono certo che il lavoratore anche se giovane manterra' lo stato di disoccupazione
				ret = true;
			} else {
				// a questo punto debbo controllare se il tale appartiene alla categoria "giovane" ovvero "181"
				// ho bisogno di leggere alcuni dati relativi al lavoratore che al momento non ho
				SourceBean row = DBLoad.getCat181(movimento);
				String dataInizioMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				if (dataInizioMov == null || dataInizioMov.equals(""))
					dataInizioMov = DateUtils.getNow();
				ret = StatoOccupazionaleManager.appartieneCat181(row, dataInizioMov);
				ret = !ret;
			}
		}
		return ret;
	}

	/**
	 * Calcola il numero di mesi del contratto a tempo determinato (per i movimenti che hanno impatti nella
	 * determinazione dello stato occupazionale alla chiusura della mobilità, o all'inizio del nuovo anno) di un
	 * lavoratore "giovane" o meno Calcolo: durata minore di 6 mesi (viene chiamata solo per annoRif >= anno decreto
	 * Fornero 2014)
	 * 
	 * @param movimento
	 * @return true se è in grado di mantenere lo stato di disoccupazione.
	 * @throws Exception
	 */
	public static boolean isCategoriaParticolareDecretoFornero(int nMesi, SourceBean movimento, String dataRif,
			boolean gestioneFornero) throws Exception {
		boolean ret = false;
		String flgSospensione = movimento.containsAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014)
				? movimento.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014).toString()
				: "";
		if (!gestioneFornero || flgSospensione.equalsIgnoreCase(MovimentoBean.SI)) {
			if (nMesi <= 6) {
				// sono sicuro che il lavoratore mantiene lo stato di disoccupazione
				ret = true;
			} else {
				if (!gestioneFornero) {
					SourceBean row = DBLoad.getCat181(movimento);
					String dataInizioMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
					if (dataInizioMov == null || dataInizioMov.equals("")) {
						dataInizioMov = DateUtils.getNow();
					}
					ret = StatoOccupazionaleManager.appartieneCat181(row, dataInizioMov);
					// 2013-2014 per considerare la sospensione ad 8 mesi per gli adulti
					// a questo punto debbo controllare se il tale appartiene alla categoria "giovane" ovvero "181"
					if (!StatoOccupazionaleManager.appartieneCat181(row, dataInizioMov)) {
						if (nMesi < 8) {
							ret = true;
						} else {
							ret = false;
						}
					} else {
						ret = false;
					}
				} else {
					ret = false;
				}
			}
		} else {
			ret = false;
		}
		return ret;
	}

	/**
	 * Calcola il numero di mesi del contratto a tempo determinato (per i movimenti che hanno impatti nella
	 * determinazione dello stato occupazionale alla chiusura della mobilità, o all'inizio del nuovo anno) di un
	 * lavoratore "giovane" o meno Calcolo: durata minore di 6 mesi (viene chiamata solo per annoRif >= anno decreto
	 * 150)
	 * 
	 * @param nMesi
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static boolean isCategoriaParticolareDecreto150(int nMesi, SourceBean movimento) throws Exception {
		boolean ret = false;
		String flgSospensione = movimento.containsAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014)
				? movimento.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014).toString()
				: "";
		if (flgSospensione.equalsIgnoreCase(MovimentoBean.SI)) {
			if (nMesi <= 6) {
				// sono sicuro che il lavoratore mantiene lo stato di disoccupazione
				ret = true;
			} else {
				ret = false;
			}
		} else {
			ret = false;
		}
		return ret;
	}

	/**
	 * restituisce true se l'anno della did e l'anno della dataInizio sono uguali.
	 * 
	 * @param dataInizio
	 * @param did
	 * @return
	 */
	public static boolean annoDIDeqAnnoMov(String dataInizio, SourceBean did) {
		if ((did == null) || !did.containsAttribute("datdichiarazione")) {
			return false;
		}

		String dataDID = (String) did.getAttribute("datdichiarazione");

		try {
			return (DateUtils.getAnno(dataDID) == DateUtils.getAnno(dataInizio));
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile determinare se anno did = anno movimento", e);
			return false;
		}
	}

	/**
	 * controlla se il lavoratore è in collocamento mirato. Serve per settare in modo opportuno il limite reddito. Dal 1
	 * Gennaio 2014 : il limite di reddito per i lavoratori disabili rientra nella normale gestione di limiti di reddito
	 * previsti per il lavoro subordinato o assimilato e per il reddito da lavoro autonomo.
	 * 
	 * @param row
	 * @return true se il lavoratore è in collocamento mirato
	 */
	public static boolean inCollocamentoMirato(SourceBean row, String dataRif) throws Exception {
		if (row == null || !row.containsAttribute("CODCMTIPOISCR")) {
			return false;
		}
		String codCM = (String) row.getAttribute("CODCMTIPOISCR");
		boolean isCM = ((codCM != null) && !codCM.equals(""));
		if (isCM) {
			if (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
				isCM = false;
			}
		}
		return isCM;
	}

	/**
	 * controlla se il lavoratore è in collocamento mirato. Serve per settare in modo opportuno il limite reddito. Dal 1
	 * Gennaio 2014 : il limite di reddito per i lavoratori disabili rientra nella normale gestione di limiti di reddito
	 * previsti per il lavoro subordinato o assimilato e per il reddito da lavoro autonomo.
	 * 
	 * @param row
	 * @return true se il lavoratore è in collocamento mirato
	 */
	public static boolean inCollocamentoMiratoAllaData(Vector rows, String dataRif) throws Exception {
		if (rows != null && rows.size() > 0) {
			int nCM = rows.size();
			for (int i = 0; i < nCM; i++) {
				SourceBean rowCM = (SourceBean) rows.get(i);
				rowCM = rowCM.containsAttribute("ROW") ? (SourceBean) rowCM.getAttribute("ROW") : rowCM;
				String codCM = (String) rowCM.getAttribute("CODCMTIPOISCR");
				if (codCM != null && !codCM.equals("")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean inCollocamentoMiratoAllaData(Vector rows, String dataInizioRif, String dataFineRif)
			throws Exception {
		boolean ret = false;
		if (rows != null && rows.size() > 0) {
			int nCM = rows.size();
			for (int i = 0; i < nCM; i++) {
				SourceBean rowCM = (SourceBean) rows.get(i);
				rowCM = rowCM.containsAttribute("ROW") ? (SourceBean) rowCM.getAttribute("ROW") : rowCM;
				if (rowCM != null && rowCM.containsAttribute("PRGCMISCR")) {
					String dataInizio = (String) rowCM.getAttribute("DATINIZIO");
					String dataFine = (String) rowCM.getAttribute("DATFINE");
					if (((dataInizioRif != null) && (!dataInizioRif.equals("")) && (dataInizio != null)
							&& (!dataInizio.equals("")) && (DateUtils.compare(dataInizio, dataInizioRif) <= 0)
							&& (dataFine == null || dataFine.equals("")
									|| DateUtils.compare(dataFine, dataInizioRif) >= 0))
							|| ((dataInizioRif != null) && (!dataInizioRif.equals("")) && (dataInizio != null)
									&& (!dataInizio.equals("")) && (DateUtils.compare(dataInizio, dataInizioRif) > 0)
									&& (dataFine == null || dataFine.equals("") || dataFineRif == null
											|| dataFineRif.equals("")
											|| DateUtils.compare(dataFine, dataInizioRif) >= 0))) {
						ret = true;
					}

				}
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param date
	 * @param years
	 * @param dataInizioCalcolo
	 * @return
	 * @throws Exception
	 */
	public static boolean maggioreDiUno(String date, int years, String dataInizioCalcolo) throws Exception {
		int[] nowA = DateUtils.split(dataInizioCalcolo);// DateUtils.split(DateUtils.getNow());
		int gg1 = nowA[0];
		int mm1 = nowA[1];
		int aa1 = nowA[2];

		int[] dateA = DateUtils.split(date);
		int gg2 = dateA[0];
		int mm2 = dateA[1];
		int aa2 = dateA[2];

		if ((aa1 - aa2) >= years) {
			if ((aa1 - aa2) == years) {
				if ((mm1 - mm2) > 0) {
					return true;
				} else if ((mm1 - mm2) == 0) {
					if ((gg1 - gg2) >= 0) {
						return true;
					}
				}
			} else {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @param date
	 * @param years
	 * @param dataInizioCalcolo
	 * @return
	 * @throws Exception
	 */
	public static boolean minoreDiUno(String date, int years, String dataInizioCalcolo) throws Exception {
		int[] nowA = DateUtils.split(dataInizioCalcolo);// DateUtils.split(DateUtils.getNow());
		int gg1 = nowA[0];
		int mm1 = nowA[1];
		int aa1 = nowA[2];

		int[] dateA = DateUtils.split(date);
		int gg2 = dateA[0];
		int mm2 = dateA[1];
		int aa2 = dateA[2];

		if ((aa1 - aa2) <= years) {
			if ((aa1 - aa2) == years) {
				if ((mm1 - mm2) < 0) {
					return true;
				} else {
					if ((mm1 - mm2) == 0) {
						if ((gg1 - gg2) < 0) {
							return true;
						}
					}
				}
			} else {
				return true;
			}
		}

		return false;
	}

	/**
	 * metodo per il calcolo dei mesi di lavoro di un movimento(movimenti che hanno impatti nella determinazione dello
	 * stato occupazionale alla chiusura della mobilità)
	 * 
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static int numeroMesiDiLavoroFineMobilita(SourceBean movimento) throws Exception {
		String dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		if (dataFine == null || dataFine.equals("")) {
			dataFine = "31/12/" + DateUtils.getAnno(dataInizio);
		}
		return numeroMesiDiLavoro(dataInizio, dataFine);
	}

	/**
	 * metodo per il calcolo dei mesi di lavoro di un movimento.
	 * 
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static int numeroMesiDiLavoro(SourceBean movimento) throws Exception {
		Object prec = null;
		String dataInizio = null;
		Vector precedenti = null;
		if (MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.PROROGA
				|| MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.TRASFORMAZIONE) {
			prec = (Object) movimento.getAttribute("MOVIMENTI_PROROGATI");
			if (prec == null) {
				prec = DBLoad.getMovimentiProroghe(movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC));
			}
			if (prec instanceof SourceBean) {
				Vector v = new Vector(1);
				v.add(prec);
				precedenti = v;
			} else {
				precedenti = (Vector) prec;
			}
			if (precedenti != null && precedenti.size() > 0) {
				SourceBean sb = (SourceBean) precedenti.get(0);
				dataInizio = (String) sb.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			} else {
				dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			}
		} else {
			dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		}

		String dataFine = "";
		// controllo se si tratta di movimenti proroga o trasformazione e
		// in questo caso dataFine è la data fine effettiva dell'ultimo
		// movimento prorogato o trasformato.
		if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
			Vector vAppoggio = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
			SourceBean movimentoAvv = (SourceBean) vAppoggio.get(vAppoggio.size() - 1);
			dataFine = (String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			if (dataFine == null) {
				dataFine = "31/12/"
						+ DateUtils.getAnno((String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO));
			}
		} else {
			dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		}

		String codMonoTempo = (String) movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
		if (codMonoTempo == null || codMonoTempo.equals("I")) {
			return Integer.MAX_VALUE;
		} else {
			return numeroMesiDiLavoro(dataInizio, dataFine);
		}
	}

	/**
	 * metodo per il calcolo dei mesi di lavoro di un movimento successivo al decreto Fornero 2014. Vengono gestiti i
	 * periodi intermittenti perché il metodo si invoca anche in presenza di rapporti successivi al decreto 150 e
	 * lavoratore iscritto al CM
	 * 
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static int numeroMesiDiLavoroDecretoFornero(SourceBean movimento) throws Exception {
		Object prec = null;
		String dataInizio = null;
		Vector precedenti = null;
		Object objGGIntermittenti = movimento.getAttribute("NUMGGINTERMITTENTE");
		if (objGGIntermittenti != null) {
			int numGGIntermittenti = Integer.parseInt(objGGIntermittenti.toString());
			if (numGGIntermittenti > Properties.GIORNI_SOSP_DECRETO150) {
				return Properties.MESI_SOSP_DECRETO150 + 1;
			} else {
				return Properties.MESI_SOSP_DECRETO150 - 1;
			}
		} else {
			if (MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.PROROGA
					|| MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.TRASFORMAZIONE) {
				prec = (Object) movimento.getAttribute("MOVIMENTI_PROROGATI");
				if (prec == null) {
					prec = DBLoad.getMovimentiProroghe(movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC));
				}
				if (prec instanceof SourceBean) {
					Vector v = new Vector(1);
					v.add(prec);
					precedenti = v;
				} else {
					precedenti = (Vector) prec;
				}
				if (precedenti != null && precedenti.size() > 0) {
					SourceBean sb = (SourceBean) precedenti.get(0);
					dataInizio = (String) sb.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				} else {
					dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				}
			} else {
				dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			}

			String dataFine = null;
			// controllo se si tratta di movimenti proroga o trasformazione e
			// in questo caso dataFine è la data fine effettiva dell'ultimo
			// movimento prorogato o trasformato.
			if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector vAppoggio = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
				SourceBean movimentoAvv = (SourceBean) vAppoggio.get(vAppoggio.size() - 1);
				dataFine = (String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			} else {
				dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			}

			if (dataFine == null || dataFine.equals("")) {
				return Integer.MAX_VALUE;
			} else {
				return numeroMesiDiLavoro(dataInizio, dataFine);
			}
		}
	}

	/**
	 * metodo per il calcolo dei mesi di lavoro di un movimento successivo al decreto 150
	 * 
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static int numeroMesiDiLavoroDecreto150(SourceBean movimento) throws Exception {
		Object prec = null;
		String dataInizio = null;
		Vector precedenti = null;
		Object objGGIntermittenti = movimento.getAttribute("NUMGGINTERMITTENTE");
		if (objGGIntermittenti != null) {
			int numGGIntermittenti = Integer.parseInt(objGGIntermittenti.toString());
			if (numGGIntermittenti > Properties.GIORNI_SOSP_DECRETO150) {
				return Properties.MESI_SOSP_DECRETO150 + 1;
			} else {
				return Properties.MESI_SOSP_DECRETO150 - 1;
			}
		} else {
			if (MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.PROROGA
					|| MovimentoBean.getTipoMovimento(movimento) == MovimentoBean.TRASFORMAZIONE) {
				prec = (Object) movimento.getAttribute("MOVIMENTI_PROROGATI");
				if (prec == null) {
					prec = DBLoad.getMovimentiProroghe(movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC));
				}
				if (prec instanceof SourceBean) {
					Vector v = new Vector(1);
					v.add(prec);
					precedenti = v;
				} else {
					precedenti = (Vector) prec;
				}
				if (precedenti != null && precedenti.size() > 0) {
					SourceBean sb = (SourceBean) precedenti.get(0);
					dataInizio = (String) sb.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				} else {
					dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				}
			} else {
				dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			}

			String dataFine = null;
			String oggi = DateUtils.getNow();

			// controllo se si tratta di movimenti proroga o trasformazione e
			// in questo caso dataFine è la data fine effettiva dell'ultimo
			// movimento prorogato o trasformato.
			if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector vAppoggio = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
				SourceBean movimentoAvv = (SourceBean) vAppoggio.get(vAppoggio.size() - 1);
				dataFine = (String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			} else {
				dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			}

			if (dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, oggi) > 0) {
				dataFine = oggi;
			}
			return numeroMesiDiLavoroDecreto150(dataInizio, dataFine);
		}
	}

	/**
	 * Calcola il numero di mesi di un dato movimento. Non è stato adeguato per gli intermittenti in quanto il metodo
	 * viene invocato solo per movimenti precedenti al decreto Fornero e di conseguenza non prevedono la gestione dei
	 * periodi intermittenti introdotta a partire dal decreto 150
	 * 
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public static int numeroMesiDiLavoroMovimento(SourceBean movimento) throws Exception {
		String dataInizio = null;
		dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine = "";
		// controllo se si tratta di movimenti proroga o trasformazione e
		// in questo caso dataFine è la data fine effettiva dell'ultimo
		// movimento prorogato o trasformato.
		if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
			Vector vAppoggio = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
			SourceBean movimentoAvv = (SourceBean) vAppoggio.get(vAppoggio.size() - 1);
			dataFine = (String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			if (dataFine == null) {
				dataFine = "31/12/"
						+ DateUtils.getAnno((String) movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO));
			}
		} else {
			dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		}

		String codMonoTempo = (String) movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
		if (codMonoTempo == null || codMonoTempo.equals("I"))
			return Integer.MAX_VALUE;
		return numeroMesiDiLavoro(dataInizio, dataFine);
	}

	/**
	 * Calcola il numero di mesi tra dataInizio e dataFine. Si conta un mese se si superano 15 gg di lavoro (Es.
	 * 14/12/2004 viene considerato un mese pieno, mentre il 17/12/2004 non viene considerato come un mese di lavoro).
	 * Questo fino a Gennaio 2014, poi si applica il calcolo numero mesi decreto Fornero
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @return
	 * @throws Exception
	 */
	public static int numeroMesiDiLavoro(String dataInizio, String dataFine) throws Exception {
		int ddInizio;
		int ddFine;
		// controllo la correttezza delle date
		if ((dataInizio == null) || (dataFine == null)) {
			throw new Exception("Calcolo numero mesi di lavoro: data inizio o data fine null");
		}

		if (DateUtils.compare(dataInizio, dataFine) > 0) {
			throw new Exception("Calcolo numero mesi di lavoro: data fine minore della data inizio");
		}
		// calcolo i mesi tra la data iniziale e finale (compreso il mese iniziale e finale)
		int mesi;
		if ((DateUtils.compare(dataFine, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0)
				|| (DateUtils.compare(dataInizio, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0
						&& DateUtils.compare(dataFine, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0)) {
			mesi = DateUtils.monthsBetween(dataInizio, dataFine);
			// i mesi iniziale e finale vanno considerati solo se i giorni commerciali lavorati sono almeno 16

			ddInizio = Integer.parseInt(dataInizio.substring(0, 2));
			ddFine = Integer.parseInt(dataFine.substring(0, 2));

			if (mesi == 1) {
				if ((ddFine - ddInizio) + 1 < 16)
					mesi = mesi - 1;
			} else {
				if (((30 - ddInizio) + 1) < 16)
					mesi = mesi - 1;
				if (ddFine < 16)
					mesi = mesi - 1;
			}
		} else {
			// data inizio e data fine successive al decreto Fornero 2014
			mesi = DateUtils.monthsBetweenFornero(dataInizio, dataFine);
		}
		return mesi;
	}

	/**
	 * Calcola il numero di mesi tra dataInizio e dataFine. Si contano i mesi commerciali di 30 giorni
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @return
	 * @throws Exception
	 */
	public static int numeroMesiDiLavoroDecreto150(String dataInizio, String dataFine) throws Exception {
		// controllo la correttezza delle date
		if ((dataInizio == null) || (dataFine == null)) {
			throw new Exception("Calcolo numero mesi di lavoro: data inizio o data fine null");
		}

		if (DateUtils.compare(dataInizio, dataFine) > 0) {
			throw new Exception("Calcolo numero mesi di lavoro: data fine minore della data inizio");
		}
		// calcolo i mesi tra la data iniziale e finale (compreso il mese iniziale e finale)
		int mesi = DateUtils.monthsBetween150(dataInizio, dataFine);
		return mesi;
	}

	/**
	 * I mesi vengono considerati di 30 (mesi commerciali).
	 * 
	 * @param date1
	 * @param date2
	 * @return numero di giorni commerciali tra due date
	 * @throws Exception
	 */
	public static int numeroGiorniDiLavoroCommerciali(String date1, String date2) throws Exception {
		int gma1[] = DateUtils.split(date1); // [gg,mm,aaaa]
		int gma2[] = DateUtils.split(date2);
		int ret = 0;
		// devo considerare i mesi di 30 giorni (commerciali)
		if (gma1[0] == 31)
			gma1[0] = 30;

		if (gma2[0] == 31)
			gma2[0] = 30;

		if ((gma1[0] == 29 && gma1[1] == 2 && (gma1[2] % 4 == 0))
				|| (gma1[0] == 28 && gma1[1] == 2 && (gma1[2] % 4 != 0)))
			gma1[0] = 30;

		if ((gma2[0] == 29 && gma2[1] == 2 && (gma2[2] % 4 == 0))
				|| (gma2[0] == 28 && gma2[1] == 2 && (gma2[2] % 4 != 0)))
			gma2[0] = 30;

		if (gma1[1] == gma2[1] && gma1[2] == gma2[2]) {
			ret = gma2[0] - gma1[0] + 1;
		} else {
			ret = 30 - (gma1[0] == 31 ? 30 : gma1[0]) + 1;
			ret += (gma2[0] == 31 ? 30 : gma2[0]);
			for (int i = gma1[2]; i <= gma2[2]; i++) {
				int mI = (gma1[2] == i) ? ((gma1[1] < 12 || i++ == i) ? gma1[1] + 1 : 1) : 1;
				int mF = (gma2[2] == i) ? ((gma2[1] > 1) ? gma2[1] - 1 : 0) : 12;
				for (int j = mI; j <= mF; j++)
					ret += 30;
			}
		}
		return ret;
	}

	/**
	 * Calcola il numero di giorni commerciali nell'anno della mobilità
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoroAnnoMobilita(String dataInizio, String dataFine, SourceBean mobilita)
			throws Exception {
		int ggDiLavoro = 0;
		int annoMob = 0;
		String dataFineAnnoMob = "";
		annoMob = DateUtils.getAnno(mobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString());

		if (DateUtils.getAnno(dataInizio) < annoMob) {
			// calcolo i giorni di lavoro commerciali a partire dall'inizio dell'anno della did
			dataInizio = "01/01/" + annoMob;
		}

		dataFineAnnoMob = dataFine;
		if (dataFine == null || DateUtils.getAnno(dataFine) > annoMob) {
			dataFineAnnoMob = "31/12/" + annoMob;
		}

		if (DateUtils.compare(dataInizio, dataFineAnnoMob) > 0) {
			ggDiLavoro = 0;
		} else {
			ggDiLavoro = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFineAnnoMob);
		}

		return ggDiLavoro;
	}

	/**
	 * Calcola il numero di giorni commerciali nell'anno successivo alla mobilità
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoroAnnoSuccMob(String dataInizio, String dataFine, SourceBean mobilita)
			throws Exception {
		int ggDiLavoroAnnoSuccessivo = 0;
		int annoMob = 0;
		int annoSuccMob = 0;
		String dataInizioSuccMob = "";
		String dataFineAnnoMob = "";
		String dataFineAnnoSuccMob = "";
		annoMob = DateUtils.getAnno(mobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString());
		annoSuccMob = annoMob + 1;
		dataInizioSuccMob = "01/01/" + annoSuccMob;

		if (DateUtils.getAnno(dataInizio) < annoMob) {
			// calcolo i giorni di lavoro commerciali a partire dall'inizio dell'anno della did
			dataInizio = "01/01/" + annoMob;
		}

		dataFineAnnoMob = dataFine;
		if (dataFine != null) {
			if (DateUtils.getAnno(dataFine) > annoMob) {
				dataFineAnnoMob = "31/12/" + annoMob;
				if (DateUtils.getAnno(dataFine) == annoSuccMob) {
					dataFineAnnoSuccMob = dataFine;
				} else {
					dataFineAnnoSuccMob = "31/12/" + annoSuccMob;
				}
			}
		} else {
			dataFineAnnoMob = "31/12/" + annoMob;
			dataFineAnnoSuccMob = "31/12/" + annoSuccMob;
		}

		if (dataFine == null || DateUtils.getAnno(dataFine) > annoMob) {
			if (DateUtils.getAnno(dataInizio) == DateUtils.getAnno(dataInizioSuccMob)) {
				ggDiLavoroAnnoSuccessivo = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFineAnnoSuccMob);
			} else {
				ggDiLavoroAnnoSuccessivo = Controlli.numeroGiorniDiLavoroCommerciali(dataInizioSuccMob,
						dataFineAnnoSuccMob);
			}
		} else {
			ggDiLavoroAnnoSuccessivo = 0;
		}
		return ggDiLavoroAnnoSuccessivo;
	}

	/**
	 * Calcola il numero di giorni commerciali nell'anno della did
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoroAnnoDid(String dataInizio, String dataFine, SourceBean did)
			throws Exception {
		int ggDiLavoro = 0;
		int annoDid = 0;
		String dataFineAnnoDid = "";
		annoDid = DateUtils.getAnno(did.getAttribute(DidBean.DB_DAT_INIZIO).toString());

		if (DateUtils.getAnno(dataInizio) < annoDid) {
			// calcolo i giorni di lavoro commerciali a partire dall'inizio dell'anno della did
			dataInizio = "01/01/" + annoDid;
		}

		dataFineAnnoDid = dataFine;
		if (dataFine == null || DateUtils.getAnno(dataFine) > annoDid) {
			dataFineAnnoDid = "31/12/" + annoDid;
		}

		if (DateUtils.compare(dataInizio, dataFineAnnoDid) > 0) {
			ggDiLavoro = 0;
		} else {
			ggDiLavoro = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFineAnnoDid);
		}

		return ggDiLavoro;
	}

	/**
	 * Calcola il numero di giorni commerciali nell'anno successivo della did
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoroAnnoSuccDid(String dataInizio, String dataFine, SourceBean did)
			throws Exception {
		int ggDiLavoroAnnoSuccessivo = 0;
		int annoDid = 0;
		int annoSuccDid = 0;
		String dataInizioSuccDid = "";
		String dataFineAnnoDid = "";
		String dataFineAnnoSuccDid = "";
		annoDid = DateUtils.getAnno(did.getAttribute(DidBean.DB_DAT_INIZIO).toString());
		annoSuccDid = annoDid + 1;
		dataInizioSuccDid = "01/01/" + annoSuccDid;

		if (DateUtils.getAnno(dataInizio) < annoDid) {
			// calcolo i giorni di lavoro commerciali a partire dall'inizio dell'anno della did
			dataInizio = "01/01/" + annoDid;
		}

		dataFineAnnoDid = dataFine;
		if (dataFine != null) {
			if (DateUtils.getAnno(dataFine) > annoDid) {
				dataFineAnnoDid = "31/12/" + annoDid;
				if (DateUtils.getAnno(dataFine) == annoSuccDid) {
					dataFineAnnoSuccDid = dataFine;
				} else {
					dataFineAnnoSuccDid = "31/12/" + annoSuccDid;
				}
			}
		} else {
			dataFineAnnoDid = "31/12/" + annoDid;
			dataFineAnnoSuccDid = "31/12/" + annoSuccDid;
		}

		if (dataFine == null || DateUtils.getAnno(dataFine) > annoDid) {
			if (DateUtils.getAnno(dataInizio) == DateUtils.getAnno(dataInizioSuccDid)) {
				ggDiLavoroAnnoSuccessivo = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFineAnnoSuccDid);
			} else {
				ggDiLavoroAnnoSuccessivo = Controlli.numeroGiorniDiLavoroCommerciali(dataInizioSuccDid,
						dataFineAnnoSuccDid);
			}
		} else {
			ggDiLavoroAnnoSuccessivo = 0;
		}
		return ggDiLavoroAnnoSuccessivo;
	}

	/**
	 * Calcola il numero di giorni commerciali tra dataFine e dataInizio nell'arco dell' anno attuale (anno attuale
	 * settata all'anno della data dataRif). Data Fine settata al 31/12/2004 ma poi numeroGiorniDiLavoroCommerciali
	 * calcola il numero di giorni commerciali
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoro(String dataInizio, String dataFine, String dataRif) throws Exception {
		if (dataInizio == null) {
			return -1;
		}
		int annoAttuale = DateUtils.getAnno(dataRif);
		if (dataFine == null)
			dataFine = "31/12/" + annoAttuale;
		if (annoAttuale + 1 <= DateUtils.getAnno(dataFine))
			dataFine = "31/12/" + annoAttuale;
		return Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFine);
	}

	/**
	 * Calcola il numero di giorni commerciali tra dataFine e dataInizio
	 * 
	 * @param dataInizio
	 * @param dataFine
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoro(String dataInizio, String dataFine) throws Exception {
		if (dataInizio == null) {
			return -1;
		}
		return Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFine);
	}

	/**
	 * Calcola il numero di giorni commerciali nell'anno successivo a partire dall'inizio dell'anno successivo alla data
	 * dataRif fino alla dataFine.
	 * 
	 * @param dataFine
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public static int getNumeroGiorniDiLavoroAnnoSuccessivo(String dataFine, String dataRif) throws Exception {
		if (dataRif == null) {
			return -1;
		}
		int giorni = 0;
		String dataInizio = "01/01/" + (DateUtils.getAnno(dataRif) + 1);
		if (dataFine == null || DateUtils.getAnno(dataInizio) < DateUtils.getAnno(dataFine)) {
			dataFine = "31/12/" + (DateUtils.getAnno(dataInizio));
		}
		if (DateUtils.compare(dataInizio, dataFine) < 0)
			giorni = Controlli.numeroGiorniDiLavoroCommerciali(dataInizio, dataFine);

		return giorni;
	}

	/**
	 * crea un SourceBean dai dati contenuti nella Map, in modo che venga trattato come se fosse stato letto dal db.
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static SourceBean getMovimento(Map request) throws Exception {
		SourceBean movimento = new SourceBean("MOV");
		//
		Object cdnLavoratore = request.get(MovimentoBean.DB_CDNLAVORATORE);
		Object dataComunicazione = request.get(MovimentoBean.DB_DATA_COMUNICAZIONE);
		Object dataInizioAvv = request.get(MovimentoBean.REQ_DATA_INIZIO_AVV); // la datInizioAvv spedita dal browser
		// non corrisponde al campo del db (datAinizioAvv)
		Object dataInizio = request.get(MovimentoBean.DB_DATA_INIZIO);
		Object tipo = request.get(MovimentoBean.DB_COD_MOVIMENTO);
		Object codAssunzione = request.get(MovimentoBean.DB_COD_ASSUNZIONE);
		String retribuzione = Utils.notNull(request.get(MovimentoBean.DB_RETRIBUZIONE));
		String motivoCessazione = (String) request.get(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE);
		String motivoCessazionePrec = (String) request.get("CODMVCESSAZIONEPREC");
		String dataFine = (String) request.get(MovimentoBean.DB_DATA_FINE);
		String dataFineEffettiva = (String) request.get(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		Object flgDurata = request.get(MovimentoBean.DB_COD_MONO_TEMPO);
		Object prgMovimento = request.get(MovimentoBean.DB_PRG_MOVIMENTO);
		String datSitSanata = Utils.notNull(request.get(MovimentoBean.DB_DATA_SIT_SANATA));
		String decRetribuzioneMenSanata = Utils.notNull(request.get(MovimentoBean.DB_RETRIBUZIONE_SANATA));
		String codiceDich = Utils.notNull(request.get("CODICEDICH"));
		String tipoDichSanata = Utils.notNull(request.get(MovimentoBean.DB_TIPO_DICH_SANATA));
		Object prgDichLav = request.get(MovimentoBean.DB_PRG_DICH_LAV);
		String datIinizioMovSupReddito = Utils.notNull(request.get(MovimentoBean.DB_DATA_INIZIO_MOV_REDDITO));
		Object prgMovimentoPrec = request.get(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		Object flgModTempo = request.get(MovimentoBean.DB_FLG_MOD_TEMPO);
		Object flgModReddito = request.get(MovimentoBean.DB_FLG_MOD_REDDITO);
		Object contesto = request.get(MovimentoBean.CONTESTO_OPERATIVO);
		Object prgUnita = request.get(MovimentoBean.DB_PRG_UNITA);
		if (prgUnita == null) {
			prgUnita = request.get(MovimentoBean.DB_PRG_UNITA_VAL_MAS);
		}
		Object prgAzienda = request.get(MovimentoBean.DB_PRG_AZIENDA);
		Object codStatoAtto = request.get("CODSTATOATTO");
		Object codMonoTipoAvv = request.get(MovimentoBean.DB_COD_MONO_TIPO_ASS);
		Object codContratto = request.get(MovimentoBean.DB_COD_CONTRATTO);
		Object codOrario = request.get(MovimentoBean.DB_COD_ORARIO);
		Object numGGPrevistiAgr = request.get("NUMGGPREVISTIAGR");
		Object numGGEffettuatiAgr = request.get("NUMGGEFFETTUATIAGR");
		Object numOreSett = request.get("NUMORESETT");
		Object flagLavoroAgr = request.get("FLGLAVOROAGR");
		Object flgSospensione2014 = request.get(MovimentoBean.DB_FLG_SOSPENSIONE_2014);

		// INIZIO COSTRUZIONE MOVIMENTO (FASE DI INSERIMENTO, RETTIFICA, VALIDAZIONE)
		movimento.setAttribute(MovimentoBean.DB_CDNLAVORATORE, cdnLavoratore);
		if (dataComunicazione != null)
			movimento.setAttribute(MovimentoBean.DB_DATA_COMUNICAZIONE, dataComunicazione);
		if (dataInizio != null)
			movimento.setAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizio);
		if (dataInizioAvv != null)
			movimento.setAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC, dataInizioAvv);
		movimento.setAttribute(MovimentoBean.DB_COD_MOVIMENTO, tipo);
		if (codAssunzione != null)
			movimento.setAttribute(MovimentoBean.DB_COD_ASSUNZIONE, codAssunzione);
		if (codMonoTipoAvv != null)
			movimento.setAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS, codMonoTipoAvv);
		if (codContratto != null)
			movimento.setAttribute(MovimentoBean.DB_COD_CONTRATTO, codContratto);
		if ((datSitSanata != null) && (!datSitSanata.equals("")))
			movimento.setAttribute(MovimentoBean.DB_DATA_SIT_SANATA, datSitSanata);
		if ((decRetribuzioneMenSanata != null) && (!decRetribuzioneMenSanata.equals("")))
			movimento.setAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA, new BigDecimal(decRetribuzioneMenSanata));
		if ((codiceDich != null) && (!codiceDich.equals("")))
			movimento.setAttribute(MovimentoBean.DB_COD_TIPO_DICH, codiceDich);
		if ((tipoDichSanata != null) && (!tipoDichSanata.equals("")))
			movimento.setAttribute(MovimentoBean.DB_TIPO_DICH_SANATA, tipoDichSanata);
		if ((prgDichLav != null) && (!prgDichLav.equals("")))
			movimento.setAttribute(MovimentoBean.DB_PRG_DICH_LAV, (BigDecimal) prgDichLav);
		if ((datIinizioMovSupReddito != null) && (!datIinizioMovSupReddito.equals("")))
			movimento.setAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_REDDITO, datIinizioMovSupReddito);
		if (motivoCessazione != null && motivoCessazione.length() > 0)
			movimento.setAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE, motivoCessazione);
		if (dataFine != null && dataFine.length() > 0)
			movimento.setAttribute(MovimentoBean.DB_DATA_FINE, dataFine);
		if (motivoCessazionePrec != null && motivoCessazionePrec.length() > 0) {
			movimento.setAttribute("CODMVCESSAZIONEPREC", motivoCessazionePrec);
		}
		if ((retribuzione != null) && (!retribuzione.equals("")))
			movimento.setAttribute(MovimentoBean.DB_RETRIBUZIONE, new BigDecimal(retribuzione.replace(',', '.')));
		if (flgDurata != null)
			movimento.setAttribute(MovimentoBean.DB_COD_MONO_TEMPO, flgDurata);
		try {
			if (prgMovimento != null && prgMovimento instanceof String)
				movimento.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal((String) prgMovimento));
			else if (prgMovimento != null && prgMovimento instanceof BigDecimal)
				movimento.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, (BigDecimal) prgMovimento);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Controlli.getMovimento():impossibile estrarre il prgMovimento dalla Map record ", e);
		}
		try {
			if (prgMovimentoPrec != null && prgMovimentoPrec instanceof String)
				movimento.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC, new BigDecimal((String) prgMovimentoPrec));
			else if (prgMovimentoPrec != null && prgMovimentoPrec instanceof BigDecimal)
				movimento.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC, (BigDecimal) prgMovimentoPrec);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Controlli.getMovimento():impossibile estrarre il prgMovimentoPrec dalla Map record ", e);
		}
		if (prgUnita != null)
			movimento.setAttribute(MovimentoBean.DB_PRG_UNITA,
					prgUnita instanceof String ? new BigDecimal((String) prgUnita) : prgUnita);
		if (prgAzienda != null)
			movimento.setAttribute(MovimentoBean.DB_PRG_AZIENDA,
					prgAzienda instanceof String ? new BigDecimal((String) prgAzienda) : prgAzienda);
		if (flgModTempo != null)
			movimento.setAttribute(MovimentoBean.DB_FLG_MOD_TEMPO, flgModTempo);
		if (flgModReddito != null)
			movimento.setAttribute(MovimentoBean.DB_FLG_MOD_REDDITO, flgModReddito);
		if (dataFineEffettiva != null && dataFineEffettiva.length() > 0)
			movimento.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFineEffettiva);
		else if (dataFine != null && dataFine.length() > 0)
			movimento.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFine);
		if (codStatoAtto != null)
			movimento.setAttribute("CODSTATOATTO", codStatoAtto);
		if (codOrario != null)
			movimento.setAttribute(MovimentoBean.DB_COD_ORARIO, codOrario);
		if (numGGPrevistiAgr != null)
			movimento.setAttribute("NUMGGPREVISTIAGR", numGGPrevistiAgr);
		if (numGGEffettuatiAgr != null)
			movimento.setAttribute("NUMGGEFFETTUATIAGR", numGGEffettuatiAgr);
		if (contesto != null)
			movimento.setAttribute("CONTESTO_OPERATIVO", contesto);
		if (numOreSett != null)
			movimento.setAttribute("NUMORESETT", numOreSett);
		if (flagLavoroAgr != null)
			movimento.setAttribute("FLGLAVOROAGR", flagLavoroAgr);
		if (flgSospensione2014 != null)
			movimento.setAttribute("FLGSOSPENSIONE2014", flgSospensione2014);

		// FINE COSTRUZIONE MOVIMENTO (FASE DI INSERIMENTO, RETTIFICA, VALIDAZIONE)
		return movimento;
	}

	/**
	 * metodo invocato nella gestione di una TRASFORMAZIONE per normalizzare il movimento precedente collegato al
	 * movimento di TRASFORMAZIONE
	 * 
	 * @param movDaNormalizzare
	 * @param movRiferimento
	 * @param movimenti
	 * @param txExecutor
	 * @return il movimento normalizzato (viene cambiata la data fine e data fine effettiva del movimento precedente).
	 * @throws Exception
	 */
	public static SourceBean normalizza(SourceBean movDaNormalizzare, SourceBean movRiferimento, Vector movimenti,
			TransactionQueryExecutor txExecutor) throws Exception {
		SourceBean mov = null;
		SourceBean sbApp = null;
		BigDecimal prgApp;
		switch (MovimentoBean.getTipoMovimento(movRiferimento)) {
		case MovimentoBean.TRASFORMAZIONE:
			if (movDaNormalizzare != null) {
				BigDecimal prgMovPrec = null;
				String dataFineEffettiva = "";
				String dataInizioPrec = "";
				SourceBean sbMovNormalizzare = null;
				prgMovPrec = (BigDecimal) movDaNormalizzare.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
				for (int k = 0; k < movimenti.size(); k++) {
					sbApp = (SourceBean) movimenti.get(k);
					prgApp = (BigDecimal) sbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
					if (prgApp.equals(prgMovPrec)) {
						dataFineEffettiva = (String) movRiferimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
						dataInizioPrec = sbApp.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
						if (DateUtils.compare(dataFineEffettiva, dataInizioPrec) > 0) {
							dataFineEffettiva = DateUtils.giornoPrecedente(dataFineEffettiva);
						}
						mov = (SourceBean) movimenti.get(k);
						mov.updAttribute(MovimentoBean.DB_DATA_FINE, dataFineEffettiva);
						mov.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFineEffettiva);
						break;
					}
				}
			}
			break;

		case MovimentoBean.ASSUNZIONE:
		case MovimentoBean.PROROGA:
			mov = new SourceBean(movDaNormalizzare);
			mov.updAttribute(MovimentoBean.DB_DATA_FINE,
					DateUtils.giornoPrecedente((String) movRiferimento.getAttribute(MovimentoBean.DB_DATA_INIZIO)));
			mov.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, mov.getAttribute(MovimentoBean.DB_DATA_FINE));
			mov.updAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_ASSUNZIONE);
			mov.updAttribute(MovimentoBean.FLAG_IN_INSERIMENTO, "1");
			break;

		case MovimentoBean.CESSAZIONE:
			throw new Exception("impossibile normalizzare un movimento di cessazione");
		}
		return mov;
	}

	/**
	 * metodo invocato nella gestione di una TRASFORMAZIONE.
	 * 
	 * @param trasformazione
	 * @return
	 * @throws Exception
	 */
	public static SourceBean normalizzaTrasformazione(SourceBean trasformazione) throws Exception {
		SourceBean mov = new SourceBean(trasformazione);
		mov.updAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_TRASFORMAZIONE);
		if (mov.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).equals("D"))
			mov.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, mov.getAttribute(MovimentoBean.DB_DATA_FINE));
		else {
			mov.delAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			mov.delAttribute(MovimentoBean.DB_DATA_FINE);
		}
		mov.updAttribute(MovimentoBean.FLAG_IN_INSERIMENTO, "1");
		mov.updAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(-1));
		return mov;
	}

	/**
	 * 
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static SourceBean cessazioneVirtuale(SourceBean movimento) throws Exception {
		SourceBean cessazione = new SourceBean("CESSAZIONE_VIRTUALE");
		String dataInizioAvv = "";
		if (movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
			dataInizioAvv = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC);
		} else {
			dataInizioAvv = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		}
		String dataInizioMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		dataInizioMov = DateUtils.giornoPrecedente(dataInizioMov);
		Object prgMovPrec = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		cessazione.setAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC, dataInizioAvv);
		cessazione.setAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioMov);
		cessazione.setAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_CESSAZIONE);
		cessazione.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC, prgMovPrec);
		cessazione.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(-1));
		cessazione.setAttribute(MovimentoBean.DB_CDNLAVORATORE, MovimentoBean.getCdnLavoratore(movimento));
		return cessazione;
	}

	/**
	 * 
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	public static SourceBean creaCessazione(SourceBean movimento) throws Exception {
		SourceBean cessazione = new SourceBean("CESSAZIONE_VIRTUALE");
		String dataInizioAvv = "";
		if (movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
			dataInizioAvv = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC);
		} else {
			dataInizioAvv = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		}
		String dataInizioMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE);
		cessazione.setAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC, dataInizioAvv);
		cessazione.setAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioMov);
		cessazione.setAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_CESSAZIONE);
		cessazione.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC,
				movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO));
		cessazione.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(-1));
		cessazione.setAttribute(MovimentoBean.DB_CDNLAVORATORE, MovimentoBean.getCdnLavoratore(movimento));
		return cessazione;
	}

	/**
	 * 
	 * @param movimenti
	 * @return
	 */
	public static boolean trasformazioneImpattante(SourceBean movimenti) {
		String flgModReddito = (String) movimenti.getAttribute(MovimentoBean.DB_FLG_MOD_REDDITO);
		String flgModTempo = (String) movimenti.getAttribute(MovimentoBean.DB_FLG_MOD_TEMPO);
		return (flgModReddito != null && flgModReddito.equals("S")) || (flgModTempo != null && flgModTempo.equals("S"));
	}

	/**
	 * 
	 * @param mov
	 * @param txExecutor
	 * @return
	 * @throws Exception
	 */
	public static SourceBean movimentoPrecedenteCollegato(SourceBean mov, TransactionQueryExecutor txExecutor)
			throws Exception {
		BigDecimal prgMovimentoPrec = (BigDecimal) mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		if (prgMovimentoPrec == null)
			return null;
		return DBLoad.getMovimento(prgMovimentoPrec, txExecutor);
	}

	/**
	 * 
	 * @param movimento
	 * @param cessazione
	 * @return
	 * @throws Exception
	 */
	public static boolean cessazioneAnticipata(SourceBean movimento, SourceBean cessazione) throws Exception {
		String codMonoTempo = (String) movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
		if (codMonoTempo == null || codMonoTempo.equals("I"))
			return true;
		String dataFineEffettiva = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE);
		String codMonoTipoFine = (String) movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE);
		return DateUtils.compare(dataFineEffettiva, dataFine) < 0;
	}

	/**
	 * 
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	public static Vector togliCessazioni(java.util.List rows) throws Exception {
		int rowsSize = rows.size();
		Vector v = new Vector(rowsSize);
		for (int i = 0; i < rowsSize; i++) {
			Object o = rows.get(i);
			SourceBean m = null;
			if (o instanceof DidBean || // Non si devono considerare le DID
					o instanceof ChiusuraDidBean || // Non si devono considerare le chiusure DID
					o instanceof MobilitaBean || // Non si devono considerare le Mobilità
					o instanceof ChiusuraMobilitaBean)
				continue; // Non si devono considerare le chiusure mobilità
			if (o instanceof SourceBean)
				m = (SourceBean) o;
			else
				m = ((MovimentoBean) o).getSource();
			// TracerSingleton.log(Values.APP_NAME, TracerSingleton.DEBUG, "Controlli.movimentiAperti():" + m);
			String codTipoMov = (String) m.getAttribute(MovimentoBean.DB_COD_MOVIMENTO);
			if (codTipoMov == null || codTipoMov.equals(MovimentoBean.COD_CESSAZIONE))
				continue;
			v.add(m);
		}
		return v;
	}

	public static Vector gestisciPeriodiIntermittenti(java.util.List rows, String dataInizioAnno, String dataFineAnno,
			TransactionQueryExecutor txExecutor) throws Exception {
		int rowsSize = rows.size();
		Vector v = new Vector(rowsSize);
		Vector periodi = null;
		Vector movimentiIntermittenti = new Vector();
		boolean aggiuntoPeriodoIntermittente = false;
		String dataOggi = DateUtils.getNow();
		for (int i = 0; i < rowsSize; i++) {
			boolean isRapportoIntermittente = false;
			Object o = rows.get(i);
			SourceBean m = null;
			if (o instanceof SourceBean)
				m = (SourceBean) o;
			else
				m = ((MovimentoBean) o).getSource();

			Object prgMovimento = m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			Object prgMovimentoIntermittente = prgMovimento;

			Vector periodiAvv = MovimentoBean.caricaPeriodiLavorativi(new BigDecimal(prgMovimento.toString()), null);
			if (periodiAvv != null && periodiAvv.size() > 0) {
				int periodiSize = periodiAvv.size();
				boolean exit = false;
				for (int iPer = 0; iPer < periodiSize && !exit; iPer++) {
					SourceBean mPer = (SourceBean) periodiAvv.get(iPer);
					if (mPer.containsAttribute("VALIDO")
							&& mPer.getAttribute("VALIDO").toString().equalsIgnoreCase("S")) {
						isRapportoIntermittente = true;
						prgMovimentoIntermittente = (BigDecimal) mPer.getAttribute("PRGMOVIMENTO");
						exit = true;
					}
				}
			}

			if (isRapportoIntermittente) {
				if (!movimentiIntermittenti.contains(prgMovimentoIntermittente)) {
					SourceBean res = null;
					Object params[] = new Object[4];
					params[0] = prgMovimentoIntermittente;
					params[1] = dataFineAnno;
					params[2] = dataInizioAnno;
					params[3] = dataInizioAnno;
					if (txExecutor != null) {
						res = (SourceBean) txExecutor.executeQuery("GET_PERIODI_LAVORATIVI_MOVIMENTO_INTERMITTENTE",
								params, "SELECT");
					} else {
						res = (SourceBean) QueryExecutor.executeQuery("GET_PERIODI_LAVORATIVI_MOVIMENTO_INTERMITTENTE",
								params, "SELECT", Values.DB_SIL_DATI);
					}
					if (res != null) {
						periodi = res.getAttributeAsVector("ROW");
						if (periodi != null && periodi.size() > 0) {
							int nsizePeriodi = periodi.size();
							boolean addPeriodi = true;
							int numGiorniIntermittenti = 0;
							String dataFineSosp = null;
							String dataFinePeriodoIntermittente = null;
							for (int j = 0; j < nsizePeriodi; j++) {
								SourceBean periodo = (SourceBean) periodi.get(j);
								if (periodo.containsAttribute("GIORNI")) {
									numGiorniIntermittenti = numGiorniIntermittenti
											+ ((new Integer(periodo.getAttribute("GIORNI").toString())).intValue());
								}
								dataFinePeriodoIntermittente = periodo.containsAttribute("datFineMov")
										? periodo.getAttribute("datFineMov").toString()
										: null;
							}
							if (numGiorniIntermittenti > Properties.GIORNI_SOSP_DECRETO150
									&& dataFinePeriodoIntermittente != null) {
								if (DateUtils.compare(dataFinePeriodoIntermittente, dataOggi) >= 0) {
									int ggDiffSosp = numGiorniIntermittenti - Properties.GIORNI_SOSP_DECRETO150;
									dataFineSosp = DateUtils.giornoSuccessivo(DateUtils
											.aggiungiNumeroGiorni(dataFinePeriodoIntermittente, -(ggDiffSosp)));
									if (DateUtils.compare(dataFineSosp, dataOggi) <= 0) {
										// Gestione decadenza inizio rapporto per intermittenti che superano i 180
										// giorni
										addPeriodi = false;
									}
								} else {
									// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
									addPeriodi = false;
								}
							}
							if (addPeriodi) {
								aggiuntoPeriodoIntermittente = true;
								movimentiIntermittenti.add(prgMovimentoIntermittente);
								if (!prgMovimentoIntermittente.equals(prgMovimento)) {
									if (!movimentiIntermittenti.contains(prgMovimento)) {
										movimentiIntermittenti.add(prgMovimento);
									}
								}
								for (int j = 0; j < nsizePeriodi; j++) {
									Object op = periodi.get(j);
									SourceBean mp = null;
									if (op instanceof SourceBean)
										mp = (SourceBean) op;
									else
										mp = ((MovimentoBean) op).getSource();

									v.add(mp);
								}
							} else {
								// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
								v.add(m);
							}
						}
					}
				}
			} else {
				v.add(m);
			}
		}

		if (aggiuntoPeriodoIntermittente) {
			// la lista deve essere riordinata
			v = MovimentoBean.ordinaListaMovimenti(v);
		}
		return v;
	}

	public static Vector gestisciPeriodiIntermittentiDid(java.util.List rows, String dataDid, String dataFineAnno,
			TransactionQueryExecutor txExecutor) throws Exception {
		int rowsSize = rows.size();
		Vector v = new Vector(rowsSize);
		Vector periodi = null;
		Vector movimentiIntermittenti = new Vector();
		boolean aggiuntoPeriodoIntermittente = false;
		String dataOggi = DateUtils.getNow();
		for (int i = 0; i < rowsSize; i++) {
			boolean isRapportoIntermittente = false;
			Object o = rows.get(i);
			SourceBean m = null;
			if (o instanceof SourceBean)
				m = (SourceBean) o;
			else
				m = ((MovimentoBean) o).getSource();

			Object prgMovimento = m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			Object prgMovimentoIntermittente = prgMovimento;

			Vector periodiAvv = MovimentoBean.caricaPeriodiLavorativi(new BigDecimal(prgMovimento.toString()), null);
			if (periodiAvv != null && periodiAvv.size() > 0) {
				int periodiSize = periodiAvv.size();
				boolean exit = false;
				for (int iPer = 0; iPer < periodiSize && !exit; iPer++) {
					SourceBean mPer = (SourceBean) periodiAvv.get(iPer);
					if (mPer.containsAttribute("VALIDO")
							&& mPer.getAttribute("VALIDO").toString().equalsIgnoreCase("S")) {
						isRapportoIntermittente = true;
						prgMovimentoIntermittente = (BigDecimal) mPer.getAttribute("PRGMOVIMENTO");
						exit = true;
					}
				}
			}

			if (isRapportoIntermittente) {
				if (!movimentiIntermittenti.contains(prgMovimentoIntermittente)) {
					SourceBean res = null;
					Object params[] = new Object[5];
					params[0] = prgMovimentoIntermittente;
					params[1] = dataDid;
					params[2] = dataDid;
					params[3] = dataDid;
					params[4] = dataFineAnno;
					if (txExecutor != null) {
						res = (SourceBean) txExecutor.executeQuery("GET_PERIODI_LAVORATIVI_MOVIMENTO_INTERMITTENTE_DID",
								params, "SELECT");
					} else {
						res = (SourceBean) QueryExecutor.executeQuery(
								"GET_PERIODI_LAVORATIVI_MOVIMENTO_INTERMITTENTE_DID", params, "SELECT",
								Values.DB_SIL_DATI);
					}
					if (res != null) {
						periodi = res.getAttributeAsVector("ROW");
						if (periodi != null && periodi.size() > 0) {
							int nsizePeriodi = periodi.size();
							boolean addPeriodi = true;
							int numGiorniIntermittenti = 0;
							String dataFineSosp = null;
							String dataFinePeriodoIntermittente = null;
							for (int j = 0; j < nsizePeriodi; j++) {
								SourceBean periodo = (SourceBean) periodi.get(j);
								if (periodo.containsAttribute("GIORNI")) {
									numGiorniIntermittenti = numGiorniIntermittenti
											+ ((new Integer(periodo.getAttribute("GIORNI").toString())).intValue());
								}
								dataFinePeriodoIntermittente = periodo.containsAttribute("datFineMov")
										? periodo.getAttribute("datFineMov").toString()
										: null;
							}
							if (numGiorniIntermittenti > Properties.GIORNI_SOSP_DECRETO150
									&& dataFinePeriodoIntermittente != null) {
								if (DateUtils.compare(dataFinePeriodoIntermittente, dataOggi) >= 0) {
									int ggDiffSosp = numGiorniIntermittenti - Properties.GIORNI_SOSP_DECRETO150;
									dataFineSosp = DateUtils.giornoSuccessivo(DateUtils
											.aggiungiNumeroGiorni(dataFinePeriodoIntermittente, -(ggDiffSosp)));
									if (DateUtils.compare(dataFineSosp, dataOggi) <= 0) {
										// Gestione decadenza inizio rapporto per intermittenti che superano i 180
										// giorni
										addPeriodi = false;
									}
								} else {
									// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
									addPeriodi = false;
								}
							}
							if (addPeriodi) {
								aggiuntoPeriodoIntermittente = true;
								movimentiIntermittenti.add(prgMovimentoIntermittente);
								if (!prgMovimentoIntermittente.equals(prgMovimento)) {
									if (!movimentiIntermittenti.contains(prgMovimento)) {
										movimentiIntermittenti.add(prgMovimento);
									}
								}
								for (int j = 0; j < nsizePeriodi; j++) {
									Object op = periodi.get(j);
									SourceBean mp = null;
									if (op instanceof SourceBean)
										mp = (SourceBean) op;
									else
										mp = ((MovimentoBean) op).getSource();

									v.add(mp);
								}
							} else {
								// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
								v.add(m);
							}
						}
					}
				}
			} else {
				v.add(m);
			}
		}

		if (aggiuntoPeriodoIntermittente) {
			// la lista deve essere riordinata
			v = MovimentoBean.ordinaListaMovimenti(v);
		}
		return v;
	}

	/**
	 * Funzione chiamata nel metodo StatoOccupazionaleManager2.cessazione
	 * 
	 * @param movimentiAperti
	 *            i movimenti aperti letti dal db. (contiene gli avviamenti con il vettore dei prorogati e eventualmente
	 *            movimenti orfani)
	 * @param movimento
	 *            il movimento che si sta trattando
	 * @param dataRif
	 * @return il vettore contenente tutti i movimenti aperti, escluso il movimento che si sta trattando
	 * @throws java.lang.Exception
	 */
	public static Vector listaMovimentiAperti(Vector movimentiAperti, SourceBean movimento, String dataRif)
			throws Exception {
		int j = 0;
		Vector tmpMovAperti = new Vector();
		String dataOggi = DateUtils.getNow();
		String codTipoAvviamento = "";
		int movimentiApertiSize = movimentiAperti.size();
		BigDecimal prgMovCessato = null;
		if (movimento != null) {
			prgMovCessato = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		}
		for (int i = 0; i < movimentiApertiSize; i++) {
			boolean isMovCessato = false;
			SourceBean m = (SourceBean) movimentiAperti.get(i);
			// TracerSingleton.log(Values.APP_NAME, TracerSingleton.DEBUG, "Controlli.movimentiAperti():" + m);
			String dataFineEffettiva = (String) m.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			String codTipoMov = m.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
					? m.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
					: "";
			if (codTipoMov.equalsIgnoreCase("AVV") && m.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector vettSucc = (Vector) m.getAttribute("MOVIMENTI_PROROGATI");
				// prendo l'ultima proroga o trasformazione
				if (vettSucc.size() > 0) {
					int k = vettSucc.size() - 1;
					SourceBean movimentoUltimo = (SourceBean) vettSucc.get(k);
					dataFineEffettiva = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
							? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
							: "";
					if (prgMovCessato != null) {
						for (int iPrec = 0; iPrec <= k; iPrec++) {
							SourceBean movProrogato = (SourceBean) vettSucc.get(iPrec);
							BigDecimal prgMovProrogato = (BigDecimal) movProrogato
									.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
							if (prgMovProrogato != null && prgMovCessato.equals(prgMovProrogato)) {
								isMovCessato = true;
							}
						}
					}
				}
			}
			codTipoAvviamento = m.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? m.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// VENGONO CONSIDERATI MOVIMENTI NON APERTI : TIPO AVVIAMENTO RS3 o Z.09.02
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (MovimentoBean.getTipoMovimento(m) != MovimentoBean.CESSAZIONE
					&& (codTipoAvviamento.equals("RS3") || codTipoAvviamento.equalsIgnoreCase("Z.09.02"))) {
				j++;
			} else {
				if ((MovimentoBean.getTipoMovimento(m) != MovimentoBean.CESSAZIONE
						&& ((Controlli.movimentiUguali(m, movimento) && // include la sua chiusura (mov non aperto)
								dataFineEffettiva != null && !dataFineEffettiva.equals("")
								&& DateUtils.compare(dataFineEffettiva, dataOggi) < 0)
								|| ((dataFineEffettiva != null && !dataFineEffettiva.equals(""))
										&& (DateUtils.compare(dataFineEffettiva, dataRif) < 0))))
						|| MovimentoBean.getTipoMovimento(m) == MovimentoBean.CESSAZIONE) {
					///////
					// movimenti che non sono aperti
					j++;
				} else {
					// movimenti aperti
					if (!isMovCessato) {
						tmpMovAperti.add(m);
					} else {
						// movimenti che non sono aperti
						j++;
					}
				}
			}
		}
		// TracerSingleton.log(Values.APP_NAME, TracerSingleton.DEBUG, "Controlli.listaMovimentiAperti(): n = " +
		// (tmpMovAperti.size()));
		return tmpMovAperti;
	}

	/**
	 * 
	 * @param movimentiAperti
	 * @param dataRif
	 * @return vettore contenente tutti i movimenti aperti ad una certa data
	 * @throws Exception
	 */
	public static Vector listaMovimentiAperti(Vector movimentiAperti, String dataRif) throws Exception {
		Vector tmpMovAperti = new Vector();
		int j = 0;
		String codTipoAvviamento = "";
		int movimentiApertiSize = movimentiAperti.size();
		for (int i = 0; i < movimentiApertiSize; i++) {
			SourceBean sb = (SourceBean) movimentiAperti.get(i);
			String dataFineEffettiva = (String) sb.getAttribute("datFineMovEffettiva");

			String codTipoMov = sb.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
					? sb.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
					: "";
			if (codTipoMov.equalsIgnoreCase("AVV") && sb.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector vettSucc = (Vector) sb.getAttribute("MOVIMENTI_PROROGATI");
				// prendo l'ultima proroga o trasformazione
				if (vettSucc.size() > 0) {
					int k = vettSucc.size() - 1;
					SourceBean movimentoUltimo = (SourceBean) vettSucc.get(k);
					dataFineEffettiva = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
							? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
							: "";
				}
			}
			codTipoAvviamento = sb.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? sb.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// VENGONO CONSIDERATI MOVIMENTI NON APERTI : TIPO AVVIAMENTO RS3 o Z.09.02
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (MovimentoBean.getTipoMovimento(sb) != MovimentoBean.CESSAZIONE
					&& (codTipoAvviamento.equals("RS3") || codTipoAvviamento.equalsIgnoreCase("Z.09.02"))) {
				j++;
			} else {
				if ((MovimentoBean.getTipoMovimento(sb) != MovimentoBean.CESSAZIONE && (dataFineEffettiva != null
						&& !dataFineEffettiva.equals("") && DateUtils.compare(dataRif, dataFineEffettiva) > 0))
						|| MovimentoBean.getTipoMovimento(sb) == MovimentoBean.CESSAZIONE) {
					j++;
				} else {
					tmpMovAperti.add(sb);
				}
			}
		}
		return tmpMovAperti;
	}

	public static Vector getMovimentiAperti(Vector movimentiAperti, String dataRif) throws Exception {
		Vector tmpMovAperti = new Vector();
		int j = 0;
		String codTipoAvviamento = "";
		int movimentiApertiSize = movimentiAperti.size();
		for (int i = 0; i < movimentiApertiSize; i++) {
			SourceBean sb = (SourceBean) movimentiAperti.get(i);
			String dataFineEffettiva = (String) sb.getAttribute("datFineMovEffettiva");
			String dataInizioMov = (String) sb.getAttribute("datInizioMov");
			String codTipoMov = sb.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
					? sb.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
					: "";
			if (codTipoMov.equalsIgnoreCase("AVV") && sb.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector vettSucc = (Vector) sb.getAttribute("MOVIMENTI_PROROGATI");
				// prendo l'ultima proroga o trasformazione
				if (vettSucc.size() > 0) {
					int k = vettSucc.size() - 1;
					SourceBean movimentoUltimo = (SourceBean) vettSucc.get(k);
					dataFineEffettiva = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
							? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
							: "";
				}
			}
			codTipoAvviamento = sb.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? sb.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// VENGONO CONSIDERATI MOVIMENTI NON APERTI : TIPO AVVIAMENTO RS3 o Z.09.02
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (DateUtils.compare(dataInizioMov, dataRif) > 0) {
				continue;
			}

			if (MovimentoBean.getTipoMovimento(sb) != MovimentoBean.CESSAZIONE
					&& (codTipoAvviamento.equals("RS3") || codTipoAvviamento.equalsIgnoreCase("Z.09.02"))) {
				j++;
			} else {
				if ((MovimentoBean.getTipoMovimento(sb) != MovimentoBean.CESSAZIONE && (dataFineEffettiva != null
						&& !dataFineEffettiva.equals("") && DateUtils.compare(dataRif, dataFineEffettiva) > 0))
						|| MovimentoBean.getTipoMovimento(sb) == MovimentoBean.CESSAZIONE) {
					j++;
				} else {
					tmpMovAperti.add(sb);
				}
			}
		}
		return tmpMovAperti;
	}

	/**
	 * Metodo restituisce true se nel vettore input movimenti ci sono solo Tirocini
	 * 
	 * @param movimenti
	 * @return
	 * @throws Exception
	 */
	public static boolean soloTirocini(Vector movimenti) throws Exception {
		boolean ret = true;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			SourceBean m = (SourceBean) movimenti.get(i);
			if (MovimentoBean.getTipoMovimento(m) != MovimentoBean.CESSAZIONE
					&& (!m.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
							|| !m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString().equalsIgnoreCase("T"))) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	public static boolean soloLavoriLSU(Vector movimenti) throws Exception {
		boolean ret = true;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			SourceBean m = (SourceBean) movimenti.get(i);
			if (MovimentoBean.getTipoMovimento(m) != MovimentoBean.CESSAZIONE && (!m.containsAttribute("CODTIPOASS")
					|| !m.getAttribute("CODTIPOASS").toString().equalsIgnoreCase("C.03.00"))) {
				return false;
			}
		}
		return ret;
	}

	/**
	 * Considero l'assenza del codStatoAtto come se esistesse e fosse PR (il codStatoAtto e' assente nei movimenti
	 * fittizi)
	 * 
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	public static int movimentiAvvProtocollati(java.util.List rows) throws Exception {
		int n = 0;
		int rowsSize = rows.size();
		for (int i = 0; i < rowsSize; i++) {
			SourceBean m = (SourceBean) rows.get(i);
			if (m.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					&& m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString().equals("T")) {
				continue;
			}
			boolean ret = m.containsAttribute("FLAG_IN_INSERIMENTO");
			if (ret && MovimentoBean.getTipoMovimento(m) != MovimentoBean.CESSAZIONE) {
				n++;
			} else {
				if ((m.getAttribute(MovimentoBean.DB_COD_STATO_ATTO) == null
						|| m.getAttribute(MovimentoBean.DB_COD_STATO_ATTO).equals("PR"))
						&& MovimentoBean.getTipoMovimento(m) != MovimentoBean.CESSAZIONE)
					n++;
			}
		}
		return n;
	}

	/**
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 * @throws Exception
	 */
	public static boolean movimentiUguali(SourceBean m1, SourceBean m2) throws Exception {
		String dataInizio1 = (String) m1.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine1 = (String) m1.getAttribute(MovimentoBean.DB_DATA_FINE);
		String codMonoTempo1 = (String) m1.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
		String codMonoTempo2 = (String) m2.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
		String dataInizio2 = (String) m2.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine2 = (String) m2.getAttribute(MovimentoBean.DB_DATA_FINE);
		Object o1 = m1.getAttribute("prgAzienda");
		Object o2 = m2.getAttribute("prgAzienda");
		BigDecimal azienda1 = null, azienda2 = null;
		if (o1 != null)
			if (o1 instanceof BigDecimal)
				azienda1 = (BigDecimal) o1;
			else
				azienda1 = new BigDecimal((String) o1);
		if (o2 != null)
			if (o2 instanceof BigDecimal)
				azienda2 = (BigDecimal) o2;
			else
				azienda2 = new BigDecimal((String) o2);
		Object u1 = m1.getAttribute("prgUnita");
		Object u2 = m2.getAttribute("prgUnita");
		BigDecimal unita1 = null, unita2 = null;
		if (u1 != null)
			if (u1 instanceof BigDecimal)
				unita1 = (BigDecimal) u1;
			else
				unita1 = new BigDecimal((String) u1);
		if (o2 != null)
			if (u2 instanceof BigDecimal)
				unita2 = (BigDecimal) u2;
			else
				unita2 = new BigDecimal((String) u2);

		return DateUtils.compare(dataInizio1, dataInizio2) == 0 && (azienda1.equals(azienda2))
				&& ((unita1 == null && unita2 == null) || (unita1 != null && unita2 != null && unita1.equals(unita2)))
				&& ((dataFine1 != null && !dataFine1.equals("") && dataFine2 != null && !dataFine2.equals("")
						&& DateUtils.compare(dataFine1, dataFine2) == 0)
						|| (dataFine1 == null && dataFine2 == null && codMonoTempo1 != null && codMonoTempo1.equals("I")
								&& codMonoTempo1.equals(codMonoTempo2)));
	}

	/**
	 * Inserisce nella posizione opportuna il movimento di cessazione, non ancora presente nel db e quindi assente nella
	 * lista dei movimenti aperti estratti precedentemente. Il SourceBean di cessazione estratto in seguito dalla lista
	 * delle rows sara' contraddistinto dall' attributo FLAG_IN_INSERIMENTO.
	 * 
	 * @param rows
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static boolean inserisciCessazione(Vector rows, SourceBean c) throws Exception {
		int i = 0;
		String dataInizio = (String) c.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataInizioM = null;
		boolean flag = false;
		boolean ret = true;
		int rowsSize = rows.size();
		for (; i < rowsSize; i++) {
			SourceBean m = (SourceBean) rows.get(i);
			dataInizioM = (String) m.getAttribute(MovimentoBean.DB_DATA_INIZIO);

			flag = DateUtils.compare(dataInizio, dataInizioM) < 0;
			if (flag)
				break;
		}
		if (i <= rowsSize) {
			c.updAttribute(MovimentoBean.FLAG_IN_INSERIMENTO, "0");
			c.updAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(-1));
			rows.insertElementAt(c, i);
		}
		return ret;
	}

	/**
	 * 
	 * @param rows
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static boolean aggiungiMovimento(Vector rows, SourceBean c) throws Exception {
		int i = 0;
		String dataInizio = (String) c.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataInizioM = null;
		boolean flag = false;
		boolean ret = true;
		int rowsSize = rows.size();
		for (; i < rowsSize; i++) {
			SourceBean m = (SourceBean) rows.get(i);
			dataInizioM = (String) m.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			flag = DateUtils.compare(dataInizio, dataInizioM) < 0;
			if (flag)
				break;
		}
		if (i <= rowsSize) {
			c.updAttribute(MovimentoBean.FLAG_IN_INSERIMENTO, "0");
			c.updAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(-1));
			rows.insertElementAt(c, i);
		}
		return ret;
	}

	/**
	 * Controlla che tutti i movimenti in essere mantengano il lavoratore nella categoria di sospensione di anzianità
	 * (B1), a seguito di una cessazione. Dal decreto fornero 2014, nel caso in cui la cessazione si trova in mobilità,
	 * allora sicuramente devo proseguire con il B1, perché altrimenti la mobilità sarebbe decaduta in presenza di un
	 * rapporto parasubordinato
	 * 
	 * @param movimenti
	 * @return
	 * @throws Exception
	 */
	public static boolean sospensioneAnzianita(Vector movimenti, String dataRif, List listaMobilita) throws Exception {
		// scorro i movimenti
		// controllo se reddito superiore al limite e durata movimento < 8 mesi
		boolean ret = false;
		int nPosMobilita = -1;
		SourceBean sbMobilita = null;
		String dataInizioMob = "";
		String dataFineMob = "";
		String dataFineMov = "";
		String dataInizioMov = "";
		// A questo punto dataRif non può essere null, lascio il controllo per prevedere la gestione alla data odierna
		if (dataRif == null || dataRif.equals("")) {
			dataRif = DateUtils.getNow();
		}
		boolean gestioneFornero = (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0);
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			nPosMobilita = -1;
			SourceBean sb = (SourceBean) movimenti.get(i);
			if (MovimentoBean.getTipoMovimento(sb) == MovimentoBean.CESSAZIONE)
				continue;

			String codMonoTipoAss = sb.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? sb.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			String codTipoAvviamento = sb.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? sb.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// non considerare I TIROCINI, TIPO AVVIAMENTO Z.09.02 (vecchi RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (codMonoTipoAss.equals("T") || codTipoAvviamento.equals("RS3")
					|| codTipoAvviamento.equalsIgnoreCase("Z.09.02")) {
				continue;
			}

			if (MovimentoBean.getTipoMovimento(sb) == MovimentoBean.PROROGA
					|| MovimentoBean.getTipoMovimento(sb) == MovimentoBean.TRASFORMAZIONE) {
				// devo risalire alla lista movimenti a partire dall'avviamento
				if (!sb.containsAttribute("MOVIMENTI_PROROGATI")) {
					Vector v = DBLoad.getMovimentiProroghe(sb.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC));
					v.add(sb);
					sb.setAttribute("MOVIMENTI_PROROGATI", v);
				}
			}

			// controllo se il movimento si trova in un periodo di mobilità
			dataInizioMov = sb.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			dataFineMov = sb.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? sb.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";

			int numeroMesi = 0;
			if (!gestioneFornero) {
				if (listaMobilita.size() > 0) {
					nPosMobilita = Controlli.isInMobilita(listaMobilita, dataInizioMov, dataFineMov);
				}
				if (nPosMobilita >= 0) {
					if (sb.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString().equals("I")) {
						ret = ret || Controlli.isCategoriaParticolare(sb);
					} else {
						numeroMesi = 0;
						String dataInizioAnnoMobilita = "";
						sbMobilita = (SourceBean) listaMobilita.get(nPosMobilita);
						dataFineMob = sbMobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)
								? sbMobilita.getAttribute(MobilitaBean.DB_DAT_FINE).toString()
								: "";
						dataInizioMob = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
						if (DateUtils.compare(dataInizioMov, dataInizioMob) < 0) {
							if (DateUtils.getAnno(dataInizioMov) != DateUtils.getAnno(dataInizioMob)) {
								dataInizioAnnoMobilita = "01/01/" + DateUtils.getAnno(dataInizioMob);
							} else {
								dataInizioAnnoMobilita = dataInizioMov;
							}

							sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioAnnoMobilita);
							if (dataFineMov.equals("") || (!dataFineMov.equals("")
									&& DateUtils.compare(dataFineMov, dataInizioMob) >= 0)) {
								sb.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
										DateUtils.giornoPrecedente(dataInizioMob));
							}
							numeroMesi = Controlli.numeroMesiDiLavoroFineMobilita(sb);
							sb.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFineMov);
							sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioMov);

							if (dataFineMov.equals("") || (!dataFineMov.equals("") && !dataFineMob.equals("")
									&& DateUtils.compare(dataFineMov, dataFineMob) > 0)) {
								sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, DateUtils.giornoSuccessivo(dataFineMob));
								numeroMesi = numeroMesi + Controlli.numeroMesiDiLavoroFineMobilita(sb);
								sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioMov);
							}
						} else {
							sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, DateUtils.giornoSuccessivo(dataFineMob));
							numeroMesi = Controlli.numeroMesiDiLavoroFineMobilita(sb);
							sb.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioMov);
						}
						ret = ret || Controlli.isCategoriaParticolare(numeroMesi, sb);
					}
				} else {
					ret = ret || Controlli.isCategoriaParticolare(sb);
				}

				// si arriva a questo punto partendo da una situazione che permette al lavoratore
				// di restare in 150 (se tempo indeterminato con reddito basso subito oppure a seguito della cessazione)
				if (sb.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString().equals("I")) {
					ret = ret || false;
				} else {
					// se il movimento è a t.d. con reddito alto (arrivo qui da uno stato occupazionale B1)
					// ed è chiuso allora genera precarietà (chiude la sospensione anzianita)
					if (DateUtils.compare(sb.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString(),
							dataRif) < 0) {
						ret = ret || false;
					}
				}
			} else {
				if (listaMobilita.size() > 0) {
					nPosMobilita = Controlli.isInMobilita(listaMobilita, dataRif);
				}
				if (nPosMobilita >= 0) {
					ret = ret || true;
				} else {
					ret = ret || Controlli.isCategoriaParticolareDecretoFornero(sb, dataRif, gestioneFornero);
				}
				// se il movimento con reddito alto (arrivo qui da uno stato occupazionale B1)
				// ed è chiuso allora genera precarietà (chiude la sospensione anzianita)
				String dataFineEffettiva = sb.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
						? sb.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
						: "";
				if (!dataFineEffettiva.equals("") && DateUtils.compare(dataFineEffettiva, dataRif) < 0) {
					ret = ret || false;
				}
			}
		}

		return ret;
	}

	/**
	 * Richiesta della pagina jsp
	 * 
	 * @return
	 */
	public static int getMesiAnzianita() {
		return 0;
	}

	/**
	 * Richiesta della pagina jsp
	 * 
	 * @return
	 */
	public static String getTipoAnzianita() {
		return null;
	}

	/**
	 * Richiesta della pagina jsp
	 * 
	 * @param codStatoOccupazRagg
	 * @param mesiInattivita
	 * @param sesso
	 * @return
	 */
	public static boolean donnaInInserimentoLavorativo(String codStatoOccupazRagg, BigDecimal mesiInattivita,
			String sesso) {
		int anni = mesiInattivita == null ? -1 : mesiInattivita.intValue() / 12;
		return codStatoOccupazRagg != null && codStatoOccupazRagg.equals("D") && anni >= 2 && sesso != null
				&& sesso.equals("F");
	}

	/**
	 * 
	 * @param codStatoOccRagg
	 * @param mesiAnzianita
	 * @param cat181
	 * @return
	 */
	public static String disoccInoccLungaDurata(String codStatoOccRagg, BigDecimal mesiAnzianita, String cat181) {
		String text = "";
		int mesi = mesiAnzianita == null ? -1 : mesiAnzianita.intValue();
		if (codStatoOccRagg != null && ((mesi > 12) || (mesi > 6 && cat181 != null && cat181.equalsIgnoreCase("G")))) {
			if (codStatoOccRagg.equals("D"))
				text = "Disoccupato di lunga durata";
			else if (codStatoOccRagg.equals("I"))
				text = "Inoccupato di lunga durata";
		}
		return text;
	}

	/**
	 * 
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public static boolean movimentoPrecedenteNormativa(SourceBean mov, String dataNormativa) throws Exception {
		String dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		if (DateUtils.compare(dataNormativa, dataInizio) > 0)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public static boolean movimentoInDataFutura(SourceBean mov) throws Exception {
		String dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		return (DateUtils.compare(dataInizio, DateUtils.getNow()) > 0);
	}

	/**
	 * 
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public static boolean cessazioneOdierna(SourceBean mov) throws Exception {
		String dataInizio = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		return DateUtils.compare(dataInizio, DateUtils.getNow()) == 0;
	}

	/**
	 * Il movimento termina nella sua data di fine naturale e non e' ancora stato gestito dal batch
	 * 
	 * @param movimento
	 * @param cessazione
	 * @return
	 * @throws Exception
	 */
	public static boolean cessazioneOdiernaNoBatch(SourceBean movimento, SourceBean cessazione) throws Exception {
		String dataFineMov = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE);
		String codMotivoFine = (String) movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE);
		String dataCessazione = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		return (DateUtils.compare(dataFineMov, dataCessazione) == 0 && codMotivoFine == null);
	}

	/**
	 * Questo metodo per adesso non viene invocato
	 * 
	 * @param rows
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public static boolean movimentiNonImpattanti(java.util.List rows, SourceBean mov) throws Exception {
		boolean ret = true;
		for (int i = 0; i < rows.size(); i++) {
			SourceBean m = (SourceBean) rows.get(i);
			if ((MovimentoBean.protocollato(m) || (m.containsAttribute("FLAG_IN_INSERIMENTO")))
					&& !(MovimentoBean.getTipoMovimento(m) == MovimentoBean.CESSAZIONE) && numeroMesiDiLavoro(m) >= 1)
				ret = false;
		}
		if (rows.size() == 0) {
			if (numeroMesiDiLavoro(mov) >= 1)
				ret = false;
			else
				ret = true;
		}
		return ret;
	}

	/**
	 * 
	 * @param dataRif
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public static Vector movimentiPrimaDi(String dataRif, List mov) throws Exception {
		Vector vec = new Vector();
		int movSize = mov.size();
		for (int i = 0; i < movSize; i++) {
			SourceBean sb = (SourceBean) mov.get(i);
			String dataSb = sb.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			if (DateUtils.compare(dataSb, dataRif) > 0)
				continue;
			vec.add(sb);
		}
		return vec;
	}

	/**
	 * Restituisce il numero di stati occupazionali presenti in una certa data
	 * 
	 * @param statiOccupazionali,
	 *            lista di stati occ. in cui cercare quelli della data di rif.
	 * @param dataRif,
	 *            data di riferimento
	 * @return count, numero di S.O. nella data di riferimento
	 */
	public static int numeroStatiOccupazData(List statiOccupazionali, String dataRif) {
		int count = 0;
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			StatoOccupazionaleBean soBean = (StatoOccupazionaleBean) statiOccupazionali.get(i);
			String dataSo = soBean.getDataInizio();
			if (dataSo != null && !dataSo.equals("") && dataSo.equals(dataRif))
				count++;
		}
		return count;
	}

	/**
	 * cancella dalla lista i movimenti non protocollati, e quindi che non hanno conseguenze sugli impatti.
	 * 
	 * @param vect
	 * @return
	 * @throws Exception
	 */
	public static Vector togliMovimentoNonProt(Vector vect) throws Exception {
		Vector v = new Vector();
		int vectSize = vect.size();
		for (int i = 0; i < vectSize; i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (!tmp.getAttribute("CODSTATOATTO").equals("PR"))
				continue;
			v.add(tmp);
		} // end for
		return v;
	}// end togliMovimentoNonProt

	/**
	 * Restituisce l'ultimo eventuale periodo di mobilità prima della data dataInizio. Se esistono più periodi di
	 * mobilità allora ritorno eventualmente il periodo di mobilità attivo e non quello sospeso
	 * 
	 * @param listaMobilita
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public static SourceBean getUltimaMobilita(List listaMobilita, String dataInizio) throws Exception {
		boolean isInMobilita = false;
		String dataInizioMob = "";
		String dataFineMob = "";
		SourceBean sbRet = null;
		SourceBean sbMobilita = null;
		MobilitaBean mobilita = null;
		String codMonoAttiva = "";
		String dataFineUltimaMob = "";
		int indiceUltimaMob = -1;
		int i = 0;
		for (; i < listaMobilita.size(); i++) {
			sbMobilita = (SourceBean) listaMobilita.get(i);
			dataInizioMob = (String) sbMobilita.getAttribute("datInizio");
			dataFineMob = (String) sbMobilita.getAttribute("datFine");
			if (dataFineMob != null && !dataFineMob.equals("")) {
				if (dataFineUltimaMob.equals("") && DateUtils.compare(dataFineMob, dataInizio) <= 0) {
					dataFineUltimaMob = dataFineMob;
					indiceUltimaMob = i;
				} else {
					if (DateUtils.compare(dataFineMob, dataInizio) <= 0
							&& DateUtils.compare(dataFineMob, dataFineUltimaMob) > 0) {
						dataFineUltimaMob = dataFineMob;
						indiceUltimaMob = i;
					}
				}
			}
		}
		// riparto dalla mobilità se esiste una mobilità che finisce prima dell'inizio del movimento
		// (se ho più di una mobilità che finisce nella stessa data, riparto da quella attiva se esiste)
		if (!dataFineUltimaMob.equals("")) {
			i = 0;
			for (; i < listaMobilita.size(); i++) {
				sbMobilita = (SourceBean) listaMobilita.get(i);
				dataFineMob = (String) sbMobilita.getAttribute("datFine");
				codMonoAttiva = (String) sbMobilita.getAttribute("codMonoAttiva");
				if (dataFineMob != null && !dataFineMob.equals("")) {
					if (DateUtils.compare(dataFineMob, dataFineUltimaMob) == 0) {
						if (codMonoAttiva.equalsIgnoreCase("A")) {
							indiceUltimaMob = i;
						}
					}
				}
			}
			sbRet = (SourceBean) listaMobilita.get(indiceUltimaMob);
		}
		return sbRet;
	}

	/**
	 * controlla se un evento ricade in un periodo di mobilità
	 * 
	 * @param listaMobilita
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public static SourceBean eventoInMobilita(List listaMobilita, String dataInizio, BigDecimal prgMobRicalcolo)
			throws Exception {
		boolean isInMobilita = false;
		String dataInizioMob = "";
		String dataFineMob = "";
		SourceBean sbRet = null;
		SourceBean sbMobilita = null;
		MobilitaBean mobilita = null;
		BigDecimal prgMobilitaIscr = null;
		int i = 0;
		if (Sottosistema.MO.isOff() || prgMobRicalcolo == null) {
			// in questo caso non si è verificato alcuno scorrimento e alcuna decadenza mobilità
			for (; i < listaMobilita.size(); i++) {
				sbMobilita = (SourceBean) listaMobilita.get(i);
				dataInizioMob = (String) sbMobilita.getAttribute("datInizio");
				dataFineMob = (String) sbMobilita.getAttribute("datFine");
				if (DateUtils.compare(dataInizio, dataInizioMob) >= 0 && (dataFineMob == null || dataFineMob.equals("")
						|| DateUtils.compare(dataFineMob, dataInizio) >= 0)) {
					isInMobilita = true;
					break;
				}
			}
			if (isInMobilita) {
				sbRet = sbMobilita;
			}
		} else {
			for (; i < listaMobilita.size(); i++) {
				sbMobilita = (SourceBean) listaMobilita.get(i);
				prgMobilitaIscr = (BigDecimal) sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
				if (prgMobilitaIscr.equals(prgMobRicalcolo)) {
					isInMobilita = true;
					break;
				}
			}
			if (isInMobilita) {
				dataInizioMob = (String) sbMobilita.getAttribute("datInizio");
				if (DateUtils.compare(dataInizio, dataInizioMob) >= 0) {
					sbRet = sbMobilita;
				}
			}
		}
		return sbRet;
	}

	public static int eventoInMobilita(List movimenti, String dataRif) throws Exception {
		boolean isInMobilita = false;
		int ret = -1;
		String dataInizioMob = "";
		String dataFineMob = "";
		MobilitaBean sbRet = null;
		Object obj = null;
		MobilitaBean mobilita = null;
		SourceBean sbMobilita = null;
		BigDecimal prgMobilitaIscr = null;
		int i = 0;
		for (; i < movimenti.size(); i++) {
			obj = movimenti.get(i);
			if (obj instanceof MobilitaBean) {
				mobilita = (MobilitaBean) movimenti.get(i);
				dataInizioMob = mobilita.getDataInizio();
				dataFineMob = mobilita.getDataFine();
				if (DateUtils.compare(dataRif, dataInizioMob) >= 0 && (dataFineMob == null || dataFineMob.equals("")
						|| DateUtils.compare(dataFineMob, dataRif) >= 0)) {
					ret = i;
					break;
				}
			}
		}
		return ret;
	}

	public static SourceBean eventoInMobilita(Vector listaMobilita, String dataRif) throws Exception {
		String dataInizioMob = "";
		String dataFineMob = "";
		SourceBean mobilitaRet = null;
		SourceBean mobilita = null;
		boolean trovataMob = false;
		for (int i = 0; (i < listaMobilita.size() && !trovataMob); i++) {
			mobilita = (SourceBean) listaMobilita.get(i);
			dataInizioMob = mobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
			dataFineMob = mobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)
					? mobilita.getAttribute(MobilitaBean.DB_DAT_FINE).toString()
					: null;
			if (DateUtils.compare(dataRif, dataInizioMob) >= 0 && (dataFineMob == null || dataFineMob.equals("")
					|| DateUtils.compare(dataFineMob, dataRif) >= 0)) {
				mobilitaRet = mobilita;
				trovataMob = true;
			}
		}
		return mobilitaRet;
	}

	/**
	 * Controllo se alla dataInizio del movimento il lavoratore si trova in un periodo di mobilità
	 * 
	 * @param listaMobilita
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public static int isInMobilita(List listaMobilita, String dataInizio) throws Exception {
		int nPosMobilita = -1;
		String dataInizioMob = "";
		String dataFineMob = "";
		String codMonoAttiva = "";
		SourceBean sbMobilita = null;
		MobilitaBean mobilita = null;
		for (int i = 0; i < listaMobilita.size(); i++) {
			sbMobilita = (SourceBean) listaMobilita.get(i);
			dataInizioMob = (String) sbMobilita.getAttribute("datInizio");
			dataFineMob = (String) sbMobilita.getAttribute("datFine");
			codMonoAttiva = sbMobilita.getAttribute("CODMONOATTIVA").toString();

			if (DateUtils.compare(dataInizio, dataInizioMob) >= 0
					&& (dataFineMob == null || dataFineMob.equals("")
							|| DateUtils.compare(dataFineMob, dataInizio) >= 0)
					&& codMonoAttiva.equalsIgnoreCase("A")) {
				nPosMobilita = i;
				break;
			}
		}
		return nPosMobilita;
	}

	/**
	 * Controllo se il movimento cade in un periodo di mobilità
	 * 
	 * @param listaMobilita
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public static int isInMobilita(List listaMobilita, String dataInizio, String dataFine) throws Exception {
		int nPosMobilita = -1;
		String dataInizioMob = "";
		String dataFineMob = "";
		String codMonoAttiva = "";
		SourceBean sbMobilita = null;
		MobilitaBean mobilita = null;
		for (int i = 0; i < listaMobilita.size(); i++) {
			sbMobilita = (SourceBean) listaMobilita.get(i);
			dataInizioMob = (String) sbMobilita.getAttribute("datInizio");
			dataFineMob = (String) sbMobilita.getAttribute("datFine");
			codMonoAttiva = sbMobilita.getAttribute("CODMONOATTIVA").toString();
			if (codMonoAttiva.equalsIgnoreCase("A")) {
				if (DateUtils.compare(dataInizio, dataInizioMob) < 0
						&& (dataFine.equals("") || DateUtils.compare(dataFine, dataFineMob) >= 0)) {
					nPosMobilita = i;
				} else {
					if (DateUtils.compare(dataInizio, dataInizioMob) >= 0 && dataFineMob != null
							&& !dataFineMob.equals("") && DateUtils.compare(dataInizio, dataFineMob) <= 0
							&& (dataFine.equals("") || DateUtils.compare(dataFine, dataFineMob) > 0)) {
						nPosMobilita = i;
					}
				}
			}
		}
		return nPosMobilita;
	}

	public static ArrayList calcolaGiorniMovimento(List listaMobilita, SourceBean movimento) throws Exception {
		// INIZIO: il reddito non va considerato per il periodo che si trova in mobilita
		boolean calcolaReddito = true;
		boolean calcoloGiorni = true;
		boolean isOk = false;
		String dataInizioMob = "";
		String dataFineMob = "";
		SourceBean sbMobilita = null;
		String codMonoAttiva = "";
		ArrayList listaRitorno = new ArrayList();
		Integer ggDiLavoroAnno = null;
		Integer ggDiLavoroAnnoSucc = null;
		String dataInizio = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		String dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		boolean escludiGiorni = false;
		for (int cont = 0; cont < listaMobilita.size(); cont++) {
			sbMobilita = (SourceBean) listaMobilita.get(cont);
			dataInizioMob = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();

			if (DateUtils.compare(dataInizioMob, dataInizio) <= 0) {
				escludiGiorni = true;

				dataFineMob = (String) sbMobilita.getAttribute(MobilitaBean.DB_DAT_FINE);
				codMonoAttiva = sbMobilita.containsAttribute("CODMONOATTIVA")
						? sbMobilita.getAttribute("CODMONOATTIVA").toString()
						: "";
				if (codMonoAttiva.equalsIgnoreCase("A")) {
					// movimento prima della mobilità (reddito da considerare interamente)
					if (DateUtils.compare(dataInizio, dataInizioMob) < 0 && dataFine != null && !dataFine.equals("")
							&& DateUtils.compare(dataFine, dataInizioMob) < 0) {
						isOk = true;
						break;
					}
					// movimento successivo alla mobilità (reddito da considerare interamente)
					if (dataFineMob != null && !dataFineMob.equals("")
							&& DateUtils.compare(dataInizio, dataFineMob) > 0) {
						isOk = true;
						break;
					}
					// movimento che si trova interamente in un periodo di mobilità
					if (DateUtils.compare(dataInizio, dataInizioMob) >= 0 && dataFine != null && !dataFine.equals("")
							&& dataFineMob != null && !dataFineMob.equals("")
							&& DateUtils.compare(dataFine, dataFineMob) <= 0) {
						calcolaReddito = false;
						isOk = true;
						break;
					}
					// movimento a t.i. che inizia in un periodo di mobilità con data fine mobilità null
					if (DateUtils.compare(dataInizio, dataInizioMob) >= 0
							&& (dataFineMob == null || dataFineMob.equals(""))
							&& (dataFine == null || dataFine.equals(""))) {
						calcolaReddito = false;
						isOk = true;
						break;
					}
					// movimento che inizia in un periodo di mobilità e termina dopo la mobilità
					if (DateUtils.compare(dataInizio, dataInizioMob) >= 0 && dataFineMob != null
							&& !dataFineMob.equals("")
							&& (dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, dataFineMob) > 0)
							&& DateUtils.compare(dataInizio, dataFineMob) <= 0) {
						dataInizio = DateUtils.giornoSuccessivo(dataFineMob);
						isOk = true;
						break;
					}
					// movimento che inizia prima della mobilità e termina in un periodo di mobilità
					if (DateUtils.compare(dataInizio, dataInizioMob) < 0 && dataFine != null && !dataFine.equals("")
							&& dataFineMob != null && !dataFineMob.equals("")
							&& DateUtils.compare(dataFine, dataFineMob) <= 0
							&& DateUtils.compare(dataFine, dataInizioMob) >= 0) {
						dataFine = DateUtils.giornoPrecedente(dataInizioMob);
						isOk = true;
						break;
					}
					// movimento a t.i. che inizia prima di un periodo di mobilità con data fine mobilità null
					if (DateUtils.compare(dataInizio, dataInizioMob) < 0
							&& (dataFine == null || dataFine.equals("")
									|| DateUtils.compare(dataFine, dataInizioMob) >= 0)
							&& (dataFineMob == null || dataFineMob.equals(""))) {
						dataFine = DateUtils.giornoPrecedente(dataInizioMob);
						isOk = true;
						break;
					}
					// movimento che inizia prima della mobilità e termina dopo la mobilità
					if (DateUtils.compare(dataInizio, dataInizioMob) < 0
							&& (dataFineMob != null && !dataFineMob.equals("") && (dataFine == null
									|| dataFine.equals("") || DateUtils.compare(dataFine, dataFineMob) > 0))) {
						String dataFinePrecMob = DateUtils.giornoPrecedente(dataInizioMob);
						int ggDiLavoroAdd = ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFinePrecMob,
								dataInizio);
						ggDiLavoroAdd = ggDiLavoroAdd
								+ ControlliExt.getNumeroGiorniDiLavoro(DateUtils.giornoSuccessivo(dataFineMob),
										dataFine, DateUtils.giornoSuccessivo(dataFineMob));
						ggDiLavoroAnno = new Integer(ggDiLavoroAdd);
						ggDiLavoroAnnoSucc = new Integer(
								ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine, dataInizio));
						calcoloGiorni = false;
						isOk = true;
						break;
					}
				}
			} else
				continue;
		}

		if (!escludiGiorni) {
			ggDiLavoroAnno = new Integer(ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFine, dataInizio));
			ggDiLavoroAnnoSucc = new Integer(ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine, dataInizio));
		} else {
			if (!calcolaReddito) {
				ggDiLavoroAnno = new Integer(0);
				ggDiLavoroAnnoSucc = new Integer(0);
			} else {
				if (calcoloGiorni) {
					if (isOk) {
						if (DateUtils.getAnno(dataInizio) > DateUtils.getAnno(dataInizioMob)) {
							ggDiLavoroAnno = new Integer(
									ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFine, dataInizio));
							ggDiLavoroAnnoSucc = new Integer(
									ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine, dataInizio));
						} else {
							ggDiLavoroAnno = new Integer(
									Controlli.getNumeroGiorniDiLavoroAnnoMobilita(dataInizio, dataFine, sbMobilita));
							ggDiLavoroAnnoSucc = new Integer(
									Controlli.getNumeroGiorniDiLavoroAnnoSuccMob(dataInizio, dataFine, sbMobilita));
						}
					} else {
						ggDiLavoroAnno = new Integer(
								ControlliExt.getNumeroGiorniDiLavoro(dataInizio, dataFine, dataInizio));
						ggDiLavoroAnnoSucc = new Integer(
								ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFine, dataInizio));
					}
				}
			}
		}
		listaRitorno.add(0, ggDiLavoroAnno);
		listaRitorno.add(1, ggDiLavoroAnnoSucc);
		return listaRitorno;
	}

	public static String selezionaUltimaMobilita(List listaMobilita, String dataRif) throws Exception {
		String dataRitorno = dataRif;
		String dataInizioMob = "";
		String dataFineMob = "";
		SourceBean sbMobilita = null;
		String codMonoAttiva = "";
		String dataFineUltimaMob = "";
		boolean isMobImpattante = false;
		int indiceUltimaMob = -1;
		int i = 0;
		for (; i < listaMobilita.size(); i++) {
			sbMobilita = (SourceBean) listaMobilita.get(i);
			dataInizioMob = (String) sbMobilita.getAttribute("datInizio");
			dataFineMob = (String) sbMobilita.getAttribute("datFine");
			if (dataFineMob != null && !dataFineMob.equals("") && dataRif != null && !dataRif.equals("")) {
				if (dataFineUltimaMob.equals("") && DateUtils.compare(dataFineMob, dataRif) <= 0) {
					dataFineUltimaMob = dataFineMob;
					indiceUltimaMob = i;
				} else {
					if (DateUtils.compare(dataFineMob, dataRif) <= 0
							&& DateUtils.compare(dataFineMob, dataFineUltimaMob) > 0) {
						dataFineUltimaMob = dataFineMob;
						indiceUltimaMob = i;
					}
				}
			}
			if (dataRif != null && !dataRif.equals("") && DateUtils.compare(dataRif, dataInizioMob) >= 0
					&& (dataFineMob == null || dataFineMob.equals("")
							|| DateUtils.compare(dataFineMob, dataRif) >= 0)) {
				isMobImpattante = true;
				break;
			}
		}
		if (isMobImpattante) {
			dataRitorno = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
		}
		/*
		 * else { //riparto dalla mobilità se esiste una mobilità che finisce prima della data inizio del movimento //da
		 * annullare (se ho più di una mobilità che finisce nella stessa data, riparto da quella attiva se esiste) if
		 * (!dataFineUltimaMob.equals("")) { i = 0; for (;i<listaMobilita.size();i++) { sbMobilita =
		 * (SourceBean)listaMobilita.get(i); dataFineMob = (String)sbMobilita.getAttribute("datFine"); codMonoAttiva =
		 * (String)sbMobilita.getAttribute("codMonoAttiva"); if (dataFineMob != null && !dataFineMob.equals("")) { if
		 * (DateUtils.compare(dataFineMob,dataFineUltimaMob) == 0) { if (codMonoAttiva.equalsIgnoreCase("A")) {
		 * indiceUltimaMob = i; } } } } sbMobilita = (SourceBean)listaMobilita.get(indiceUltimaMob); String dataMaxDiff
		 * = sbMobilita.getAttribute(MobilitaBean.DB_DAT_MAX_DIFF)!=null?sbMobilita.getAttribute(MobilitaBean.
		 * DB_DAT_MAX_DIFF).toString():""; if (dataMaxDiff.equals("")) { dataRitorno =
		 * sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString(); } else { if (dataRif != null &&
		 * !dataRif.equals("") && DateUtils.compare(dataRif,dataMaxDiff)<=0) { dataRitorno =
		 * sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString(); } } } }
		 */
		return dataRitorno;
	}

	/**
	 * DALLA LISTA DEI MOVIMENTI VENGONO TOLTI I MOVIMENTI NON PROTOCOLLATI
	 * 
	 * @param movimenti
	 * @return
	 */
	public static Vector togliMovNonProtocollati(Vector movimenti) {
		Vector v = new Vector();
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			SourceBean tmp = (SourceBean) movimenti.get(i);
			if (!tmp.getAttribute("CODSTATOATTO").equals("PR"))
				continue;
			v.add(tmp);
		} // end for
		return v;
	}

	/**
	 * DALLA LISTA VENGONO TOLTI I MOVIMENTI IN DATA FUTURA
	 * 
	 * @param vect
	 * @return
	 * @throws Exception
	 */
	public static Vector togliMovimentoInDataFutura(Vector vect) throws Exception {
		Vector v = new Vector();
		String oggi = DateUtils.getNow();
		int vectSize = vect.size();
		for (int i = 0; i < vectSize; i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (DateUtils.compare(tmp.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString(), oggi) > 0)
				continue;
			v.add(tmp);
		} // end for
		return v;
	}

	/**
	 * Restituisce il primo movimento aperto nella data dataInizioMov
	 * 
	 * @param dataInizioMov
	 * @param movimento
	 * @param movimentiAperti
	 * @return
	 * @throws Exception
	 */
	public static SourceBean selezionaMovimentoApertoIniziale(String dataInizioMov, SourceBean movimento,
			Vector movimentiAperti) throws Exception {
		SourceBean sbRitorno = movimento;
		String dataFineMovAperto = "";
		String dataInizioMovAperto = "";
		SourceBean rowsMov = null;
		String codMonoTipoAss = "";
		String codTipoAvviamento = "";
		int movimentiApertiSize = movimentiAperti.size();
		for (int j = 0; j < movimentiApertiSize; j++) {
			rowsMov = (SourceBean) movimentiAperti.get(j);
			dataInizioMovAperto = rowsMov.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			dataFineMovAperto = rowsMov.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? rowsMov.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			codMonoTipoAss = rowsMov.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? rowsMov.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			codTipoAvviamento = rowsMov.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? rowsMov.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			// VENGONO CONSIDERATI MOVIMENTI NON APERTI : TIPO AVVIAMENTO Z.09.02(vecchio codice RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			if (!codTipoAvviamento.equals("Z.09.02")) {
				if (!rowsMov.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("CES")
						&& DateUtils.compare(dataInizioMovAperto, dataInizioMov) <= 0
						&& ((!dataFineMovAperto.equals("") && DateUtils.compare(dataFineMovAperto, dataInizioMov) >= 0)
								|| (dataFineMovAperto.equals("")))) {
					sbRitorno = rowsMov;
					break;
				}
			}
		}
		return sbRitorno;
	}

	/**
	 * controllo delle proroghe senza precedente e delle trasformazioni a t.d. senza precedente
	 * 
	 * @param movimento
	 * @throws Exception
	 */
	public static void controllaMovimentoProTraSenzaPrec(SourceBean movimento) throws Exception {
		if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("PRO")
				&& movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) == null) {
			throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA);
		}

		if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("TRA")
				&& movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) == null
				&& movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString().equals("D")) {
			throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA);
		}
	}

	/**
	 * recupero l'avviamento dalla proroga o dalla trasformazione
	 * 
	 * @param movimento
	 * @param movimentiAperti
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static SourceBean recuperaAvvDaProTra(SourceBean movimento, Vector movimentiAperti,
			TransactionQueryExecutor transExec) throws Exception {

		if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("PRO")) {
			Proroga pro = new Proroga(movimento);
			movimento = pro.getAvviamento();
		} else {
			if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("TRA")
					&& movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
				SourceBean movPrec = Controlli.movimentoPrecedenteCollegato(movimento, transExec);
				SourceBean movTrasformato = Controlli.normalizza(movPrec, movimento, movimentiAperti, transExec);
				Trasformazione movPartenza = new Trasformazione(movTrasformato);
				movimento = movPartenza.getAvviamentoStart(movTrasformato, movimentiAperti);
			}
		}
		return movimento;
	}

	/**
	 * ritorna lo stato occupazionale aperto rispetto alla data inizio
	 * 
	 * @param statiOccupazionali
	 * @param dataInizio
	 * @return
	 * @throws Exception
	 */
	public static SourceBean getStatoOccupazionaleAperto(Vector statiOccupazionali, String dataInizio)
			throws Exception {
		boolean trovato = false;
		SourceBean sbRet = null;
		SourceBean sbStatoOcc = null;
		String dataInizioSo = "";
		String dataFineSo = "";
		int i = 0;
		for (; i < statiOccupazionali.size(); i++) {
			sbStatoOcc = (SourceBean) statiOccupazionali.get(i);
			dataInizioSo = (String) sbStatoOcc.getAttribute("datInizio");
			dataFineSo = (String) sbStatoOcc.getAttribute("datFine");
			if (DateUtils.compare(dataInizio, dataInizioSo) >= 0 && (dataFineSo == null || dataFineSo.equals("")
					|| DateUtils.compare(dataFineSo, dataInizio) >= 0)) {
				trovato = true;
				break;
			}
		}
		if (trovato) {
			sbRet = sbStatoOcc;
		}
		return sbRet;
	}

	/**
	 * 03/08/2005
	 * 
	 * Questo metodo restituisce true se devono scattare gli impatti in presenza di proroghe e/o trasformazioni (TD)
	 * orfane.
	 * 
	 * @return boolean
	 * @author D'Auria
	 * @author Togna
	 */
	public static boolean eseguiImpattiInPresenzaMovOrfani() {
		boolean eseguiImpatti = true;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("getFlagNonScattanoImpattiMovOrfani", null, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null && res.getAttribute("ROW.FLAG") != null
				&& ((String) res.getAttribute("ROW.FLAG")).equals("S")) {
			eseguiImpatti = false;
		}
		return (eseguiImpatti);
	}

	public static void addWarning(int code, String dettagli, RequestContainer requestContainer) throws Exception {
		ArrayList warnings = null;
		if (requestContainer.getServiceRequest()
				.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE))
			warnings = (ArrayList) requestContainer.getServiceRequest()
					.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
		else {
			warnings = new ArrayList();
			requestContainer.getServiceRequest().setAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE,
					warnings);
		}
		boolean esisteWarning = false;
		Warning objWarning = null;
		for (int i = 0; i < warnings.size(); i++) {
			Object objList = warnings.get(i);
			if (objList instanceof Warning) {
				objWarning = (Warning) objList;
				if (objWarning != null && objWarning.getCode() == code
						&& objWarning.getMessage().equalsIgnoreCase(dettagli)) {
					esisteWarning = true;
					break;
				}
			}
		}
		if (!esisteWarning) {
			warnings.add(new Warning(code, dettagli));
		}
	}

	/**
	 * Funzione che controlla se la data anzianita disoccupazione di uno stato occupazionale corrisponde alla data did o
	 * mobilita.
	 * 
	 * @param dids
	 * @param listaMobilita
	 * @param dataInizioSOcc
	 * @param dataAnzianita
	 * @return
	 * @throws Exception
	 */
	public static boolean isCorrettaDataAnzianita(List dids, List listaMobilita, String dataInizioSOcc,
			String dataAnzianita) throws Exception {
		boolean result = true;
		boolean controllaMobilita = ((dids.size() > 0) ? false : true);
		String dataDichiarazione = "";
		String dataChiusura = "";
		String codStatoAtto = "";
		String dataAnzianitaCorrente = "";
		DidBean sb = null;
		DidBean didDaControllare = null;
		SourceBean sbMobilita = null;
		SourceBean sbMobilitaDaControllare = null;

		if ((dids == null || dids.size() == 0) && (listaMobilita == null || listaMobilita.size() == 0)) {
			return false;
		}

		for (int i = 0; i < dids.size(); i++) {
			sb = (DidBean) dids.get(i);
			dataDichiarazione = sb.getDataInizio();
			if (DateUtils.compare(dataInizioSOcc, dataDichiarazione) >= 0) {
				didDaControllare = sb;
			} else {
				break;
			}
		}
		for (int j = 0; j < listaMobilita.size(); j++) {
			sbMobilita = (SourceBean) listaMobilita.get(j);
			dataDichiarazione = (String) sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO);
			if (DateUtils.compare(dataInizioSOcc, dataDichiarazione) >= 0) {
				sbMobilitaDaControllare = sbMobilita;
			} else {
				break;
			}
		}

		if (didDaControllare != null) {
			dataDichiarazione = didDaControllare.getDataInizio();
			dataChiusura = didDaControllare.getDataFine();
			codStatoAtto = didDaControllare.getCodStatoAtto();
			dataAnzianitaCorrente = dataDichiarazione;
			if (codStatoAtto != null && codStatoAtto.equalsIgnoreCase("PR")) {
				if (dataAnzianitaCorrente != null && DateUtils.compare(dataAnzianitaCorrente, dataAnzianita) != 0) {
					result = false;
				}
				if (dataChiusura != null && sbMobilitaDaControllare != null) {
					dataDichiarazione = (String) sbMobilitaDaControllare.getAttribute(MobilitaBean.DB_DAT_INIZIO);
					dataChiusura = (String) sbMobilitaDaControllare.getAttribute(MobilitaBean.DB_DAT_FINE);
					dataAnzianitaCorrente = dataDichiarazione;
					if (dataAnzianitaCorrente != null && DateUtils.compare(dataAnzianitaCorrente, dataAnzianita) != 0) {
						result = false;
					} else {
						result = true;
					}
				}
			}
		}

		if (controllaMobilita && sbMobilitaDaControllare != null) {
			dataDichiarazione = (String) sbMobilitaDaControllare.getAttribute(MobilitaBean.DB_DAT_INIZIO);
			dataChiusura = (String) sbMobilitaDaControllare.getAttribute(MobilitaBean.DB_DAT_FINE);
			dataAnzianitaCorrente = dataDichiarazione;
			if (dataAnzianitaCorrente != null && DateUtils.compare(dataAnzianitaCorrente, dataAnzianita) != 0) {
				result = false;
			}

		}
		return result;
	}

	/**
	 * Restituisce valore del flagMobilitaRimaneAperta = "N" solo se il rapporto a tempo indeterminato dura più del
	 * valore NUMMESIMOBILITAAPERTA associata al motivo di cessazione
	 * 
	 * @param flgMobilitaAperta
	 * @param movInMobilita
	 * @return
	 * @throws Exception
	 */
	public static String controlloMobilitaApertaDurata(String flgMobilitaAperta, MovimentoBean movInMobilita)
			throws Exception {
		String ret = flgMobilitaAperta;
		BigDecimal mesiMobilitaAperta = (BigDecimal) movInMobilita.getAttribute("MESIMOBILITAAPERTA");
		String dataInizioMov = movInMobilita.getDataInizio();
		String dataFineMov = movInMobilita.getDataFineEffettiva();
		if (dataFineMov != null && !dataFineMov.equals("")) {
			int numMesiLavoro = Controlli.numeroMesiDiLavoro(dataInizioMov, dataFineMov);
			if (numMesiLavoro > mesiMobilitaAperta.intValue()) {
				ret = "N";
			}
		}
		return ret;
	}

	public static String calcolaFineSospensione(String dataRif, int numMesi) throws Exception {
		String dataRet = DateUtils.aggiungiMesi(dataRif, numMesi, 0);
		return dataRet;
	}

}