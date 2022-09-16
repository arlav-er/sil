/*
 * Creato il 6-ottobre-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * Seleziona lo stato occupazionale iniziale e la lista degli stati occupazionali da cancellare
 * 
 */
public class ListaStatiOccupazionali {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ListaStatiOccupazionali.class.getName());
	/**
	 * E' la posizione, riferita alla List statiOccupazionali, dello StatoOccupazionaleBean da cui partire per il
	 * calcolo degli stati occupazionali successivi. Il primo stato occupazionale da cancellare sara' quello successivo.
	 */
	protected int posIniziale = -1;
	protected List statiOccupazionali;
	protected TransactionQueryExecutor txExecutor;
	protected boolean movInInseriemnto = false;

	public ListaStatiOccupazionali(SourceBean evento, List movimenti, List statiOccupazionali, List dids,
			List listaMobilita, TransactionQueryExecutor txExecutor) throws Exception {
		this.statiOccupazionali = statiOccupazionali;
		this.txExecutor = txExecutor;
		if (!evento.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)) {
			// quando chiamo ricrea passando l'evento MOBILITA'
			String dataRif = evento.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
			String cdnLavoratore = evento.getAttribute(MobilitaBean.DB_CDNLAVORATORE).toString();
			init(dataRif, cdnLavoratore, dids, listaMobilita, movimenti);
		} else {
			if (evento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO) == null
					|| evento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString().equals("-1"))
				movInInseriemnto = true;
			init(evento, movimenti, dids, listaMobilita);
		}
	}

	public ListaStatiOccupazionali(String dataRif, Object cdnLavoratore, List statiOccupazionali, List dids,
			List listaMobilita, List movimenti, String contesto, TransactionQueryExecutor txExecutor) throws Exception {
		StatoOccupazionaleBean statoOccCorrente = null;
		this.statiOccupazionali = statiOccupazionali;
		this.txExecutor = txExecutor;
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
			String dataInizio = (String) so.getDataInizio();
			if (DateUtils.compare(dataRif, dataInizio) > 0) {
				posIniziale = i;
				_logger.debug("ListaStatiOccupazionali.init():trovato indice=" + i);

			} else {
				break;
			}
		}
		if (posIniziale < 0) {
			StatoOccupazionaleBean stAperto = (StatoOccupazionaleBean) (statiOccupazionali.size() == 0 ? null
					: statiOccupazionali.get(statiOccupazionali.size() - 1));
			statoOccCorrente = StatoOccupazionaleBean.creaStatoOccupazionaleBeanIniziale(dataRif, cdnLavoratore,
					stAperto, dids, listaMobilita, movimenti, txExecutor);
			statiOccupazionali.add(0, statoOccCorrente);
			posIniziale = 0;
			_logger.debug("ListaStatiOccupazionali.init():indice non trocato");

		}

	}

	public ListaStatiOccupazionali(String dataRif, Object cdnLavoratore, List statiOccupazionali, List dids,
			List listaMobilita, List movimenti, TransactionQueryExecutor txExecutor) throws Exception {
		this.statiOccupazionali = statiOccupazionali;
		this.txExecutor = txExecutor;
		init(dataRif, cdnLavoratore, dids, listaMobilita, movimenti);
	}

	public ListaStatiOccupazionali(String dataRif, Object cdnLavoratore, List statiOccupazionali, List listaMobilita,
			TransactionQueryExecutor txExecutor) throws Exception {
		this.statiOccupazionali = statiOccupazionali;
		this.txExecutor = txExecutor;
		init(dataRif, cdnLavoratore, new ArrayList(), listaMobilita, null);
	}

	public StatoOccupazionaleBean getStatoOccupazionaleIniziale() {
		return (StatoOccupazionaleBean) statiOccupazionali.get(posIniziale);
	}

	public List getStatiOccupazionaliDaCancellare() {
		return statiOccupazionali.subList(posIniziale + 1, statiOccupazionali.size());
	}

	protected void init(String dataRif, Object cdnLavoratore, List dids, List listaMobilita, List movimenti)
			throws Exception {
		StatoOccupazionaleBean statoOccCorrente = null;
		BigDecimal prgStatoOccupaz2 = null;
		int nSoData = 0;
		nSoData = Controlli.numeroStatiOccupazData(statiOccupazionali, dataRif);
		if (nSoData > 1 && !movInInseriemnto) {
			if (movimenti != null) {
				for (int j = 0; j < movimenti.size(); j++) {
					Object o = movimenti.get(j);
					if (o instanceof MobilitaBean) {
						MobilitaBean mobilita = ((MobilitaBean) o);
						String dataInizioMob = mobilita.getDataInizio();
						if (DateUtils.compare(dataInizioMob, dataRif) == 0) {
							prgStatoOccupaz2 = mobilita.getPrgStatoOccupaz();
							break;
						}
					} else {
						if (o instanceof ChiusuraMobilitaBean) {
							ChiusuraMobilitaBean cMobilita = ((ChiusuraMobilitaBean) o);
							String dataChiusMobilita = cMobilita.getDataInizio();
							if (DateUtils.compare(dataChiusMobilita, dataRif) == 0) {
								prgStatoOccupaz2 = cMobilita.getPrgStatoOccupaz();
								break;
							}
						} else {
							if (o instanceof DidBean) {
								DidBean s = ((DidBean) o);
								String dataDid = s.getDataInizio();
								if (DateUtils.compare(dataDid, dataRif) == 0) {
									// Ho il mov. che sto analizzando
									prgStatoOccupaz2 = s.getPrgStatoOccupaz();
									break;
								}
							} else {
								if (o instanceof ChiusuraDidBean) {
									ChiusuraDidBean cD = ((ChiusuraDidBean) o);
									String dataChiusDid = cD.getDataInizio();
									if (DateUtils.compare(dataChiusDid, dataRif) == 0) {
										// Ho il mov. che sto analizzando
										prgStatoOccupaz2 = cD.getPrgStatoOccupaz();
										break;
									}
								} else {
									if (o instanceof MovimentoBean) {
										MovimentoBean m = ((MovimentoBean) o);
										String dataMov = m.getDataInizio();
										if (DateUtils.compare(dataMov, dataRif) == 0) {
											// Ho il mov. che sto analizzando
											prgStatoOccupaz2 = m.getPrgStatoOccupaz();
											break;
										}
									}
								}
							}
						}
					}
				}
				if (prgStatoOccupaz2 != null)
					for (int k = 0; k < statiOccupazionali.size(); k++) {
						StatoOccupazionaleBean so2 = (StatoOccupazionaleBean) statiOccupazionali.get(k);
						if (so2.getPrgStatoOccupaz().compareTo(prgStatoOccupaz2) >= 0)
							break;
						posIniziale = k;
					}
			}
		} else {
			for (int i = 0; i < statiOccupazionali.size(); i++) {
				StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
				String dataInizio = (String) so.getDataInizio();
				if (movInInseriemnto) {
					// Movimento gestito in inserimento
					if (DateUtils.compare(dataRif, dataInizio) > 0) {
						posIniziale = i;
						_logger.debug("ListaStatiOccupazionali.init():trovato indice=" + i);

					} else
						break;
					// posIniziale = statiOccupazionali.size()-1;
					// break;
				} else {
					if (nSoData == 1) {
						if (DateUtils.compare(dataRif, dataInizio) >= 0) {
							if (DateUtils.compare(dataRif, dataInizio) == 0)
								break;
							posIniziale = i;
							_logger.debug("ListaStatiOccupazionali.init():trovato indice=" + i);

						} else
							break;
					}
				}
				/*
				 * if (DateUtils.compare(dataRif, dataInizio)>=0) { posIniziale = i; _logger.debug(
				 * "ListaStatiOccupazionali.init():trovato indice="+i); } else break;
				 */
			}
		} // else

		if (posIniziale < 0 && nSoData == 0 && statiOccupazionali.size() > 0) { // 28/10/2004
			for (int i = 0; i < statiOccupazionali.size(); i++) {
				StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
				String dataInizio = (String) so.getDataInizio();
				if (DateUtils.compare(dataInizio, dataRif) < 0) {
					posIniziale = i;
					_logger.debug("ListaStatiOccupazionali.init():trovato indice=" + i);

				} else
					break;
			}
		}

		if (posIniziale < 0) {
			StatoOccupazionaleBean stAperto = (StatoOccupazionaleBean) (statiOccupazionali.size() == 0 ? null
					: statiOccupazionali.get(statiOccupazionali.size() - 1));
			statoOccCorrente = StatoOccupazionaleBean.creaStatoOccupazionaleBeanIniziale(dataRif, cdnLavoratore,
					stAperto, dids, listaMobilita, movimenti, txExecutor);
			statiOccupazionali.add(0, statoOccCorrente);
			posIniziale = 0;
			_logger.debug("ListaStatiOccupazionali.init():indice non trocato");

		}
	}

	protected void init(SourceBean movimento, List movimenti, List dids, List listaMobilita) throws Exception {
		String dataInizio = "";
		if (movimento.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
				&& movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).equals("PRO")
				&& movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
			Proroga pro = new Proroga(movimento);
			MovimentoBean avv = pro.getAvviamento();
			dataInizio = avv.getDataInizio();
		} else {
			if (movimento.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)
					&& movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).equals("TRA")
					&& movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
				Trasformazione movTra = new Trasformazione(movimento);
				Vector vetMovimenti = MovimentoBean.getMovimenti(movimenti);
				MovimentoBean avv = new MovimentoBean(movTra.getAvviamentoStart(movimento, vetMovimenti));
				dataInizio = avv.getDataInizio();
			} else {
				if (movimento.containsAttribute(MovimentoBean.DB_COD_MOVIMENTO)) {
					dataInizio = (String) movimento.getAttribute("datInizioMov");
				}
			}
		}
		init(dataInizio, movimento.getAttribute("cdnLavoratore"), dids, listaMobilita, movimenti);
	}

	public String toString() {
		String s = null;
		s = "ListaStatiOccupazionali: \r\n";
		if (statiOccupazionali.size() == 0)
			return s += "\tNON SONO PRESENTI STATI OCCUPAZIONALI\r\n";
		s += "\tstato occupazionale aperto:: " + statiOccupazionali.get(statiOccupazionali.size() - 1) + "\r\n";
		s += "\tstato occupazionale da riaprire::" + getStatoOccupazionaleIniziale() + "\r\n";
		s += "\tstato occupazionale da cui cancellare::"
				+ ((posIniziale + 1 < statiOccupazionali.size()) ? statiOccupazionali.get(this.posIniziale + 1)
						: "NON CI SONO STATI OCCUPAZIONALI DA CANCELLARE")
				+ "\r\n";
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
			s += "\r\n\t*) " + so;
		}

		return s;
	}

	/**
	 * Metodo per l'impstazione di posIniziale: punta allo stato occupazionale precedente a quello a cui è collegato il
	 * movimento in esame
	 */
	public void setPosInizialeDaPrg(BigDecimal prgStatoOcc, SourceBean mov, BigDecimal cdnLavoratore) throws Exception {
		StatoOccupazionaleBean statoOccCorrente = null;
		if (prgStatoOcc == null) {
			String dataRif = (String) mov.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			for (int i = 0; i < statiOccupazionali.size(); i++) {
				StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
				String dataInizio = so.getDataInizio();
				if (DateUtils.compare(dataRif, dataInizio) <= 0) {
					posIniziale = i - 1;// i>0?i-1:i;
					_logger.debug("ListaStatiOccupazionali.init():trovato2 indice=" + i);

					break;
				}
			}
		} else {
			for (int i = 0; i < statiOccupazionali.size(); i++) {
				StatoOccupazionaleBean so = (StatoOccupazionaleBean) statiOccupazionali.get(i);
				if (so.getPrgStatoOccupaz().equals(prgStatoOcc)) {
					posIniziale = i - 1;// i>0?i-1:i;
					_logger.debug("ListaStatiOccupazionali.init():trovato2 indice=" + i);

					break;
				}
			}
		}
		if (posIniziale < 0) {
			// Creare S.O. fittizio di Altro
			SourceBean ultimo = null;
			ultimo = new SourceBean("ROW");
			ultimo.setAttribute("CDNLAVORATORE", cdnLavoratore);
			ultimo.setAttribute("codStatoOccupaz", StatoOccupazionaleBean.decode(StatoOccupazionaleBean.C));
			ultimo.setAttribute("prgStatoOccupaz", new BigDecimal(-1));
			ultimo.setAttribute("codStatoOccupazRagg", "A");
			statoOccCorrente = new StatoOccupazionaleBean(ultimo, null, null, null);
			statiOccupazionali.add(0, statoOccCorrente);
			posIniziale = 0;
			_logger.debug("ListaStatiOccupazionali.setPosInizialeDaPrg():indice non trocato");

		} // end if(posIniziale<0)
	}// end setPosInizialeDaPrg
}