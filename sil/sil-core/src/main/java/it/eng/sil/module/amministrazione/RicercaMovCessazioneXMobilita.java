/*
 * Created on Sep 24, 2007
 */
package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/**
 * Dalla lista dei movimenti non legati a mobilita' viene costruita una lista di movimenti di cessazioni (reali o avv
 * Z.09.02) associabili ad una iscrizione mobilita'.
 * 
 * @author savino
 */
public class RicercaMovCessazioneXMobilita extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RicercaMovCessazioneXMobilita.class.getName());

	// lista movimenti ordinati per datiniziomov crescente
	private Vector movimenti;
	// la lista dei movimenti risultato
	private SourceBean risultato;
	// il numnero di risultati nel SourceBaean risultato
	private int numeroRisultati = 0;
	private String prgAziendaRicerca;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			this.risultato = new SourceBean("ROWS");

			this.prgAziendaRicerca = (String) serviceRequest.getAttribute("PRGAZIENDA");

			disableMessageIdFail();
			disableMessageIdSuccess();
			setSectionQuerySelect("QUERIES.SELECT_QUERY");
			SourceBean row = doDynamicSelect(serviceRequest, serviceResponse, false);
			if (row == null) {
				// c'e' stato un errore
				throw new Exception("Ricerca fallita");
			}
			this.movimenti = row.getAttributeAsVector("ROW");

			avviaRicerca();

			setRisultatoOK();
			serviceResponse.setAttribute(this.risultato);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"Impossibile costruire la lista dei movimenti di cessazione associabili ad una mobilita'", e);

		}

	}

	private void setRisultatoOK() throws Exception {
		this.risultato.setAttribute("CURRENT_PAGE", new Integer("1"));
		this.risultato.setAttribute("NUM_PAGES", new Integer("1"));
		// this.risultato.setAttribute("ROWS_X_PAGE", new Integer("0"));
		this.risultato.setAttribute("ROWS_X_PAGE", new Integer(numeroRisultati));
		// this.risultato.setAttribute("NUM_RECORDS", new
		// Integer(this.movimenti.size()));
		this.risultato.setAttribute("NUM_RECORDS", new Integer(numeroRisultati));
	}

	private void avviaRicerca() throws Exception {
		for (int i = movimenti.size() - 1; i >= 0; i--) {
			SourceBean m = (SourceBean) movimenti.get(i);
			// cerca una cessazione (non SC) o un avviamento Z.09.02 (= ces)
			// per ogni cessazione cerco il suo avviamento originario
			if (!isUnaCessazione(m))
				continue;
			if (isAvviamentoRS3(m)) {
				// l'oggetto "m" e' l'avviamento Z.09.02 (vecchio codice RS3)
				SourceBean c = cercaCessazioneMob(m, i);
				if (c == null) {
					_logger.fatal("Impossibile trovare l'avviamento origine per l'avviamento Z.09.02 " + m.toString());

					continue;
				}
				// l'oggetto "c" e' la cessazione SC cessata
				// definitivamente dall'avviamento Z.09.02 (vecchio codice RS3)
				SourceBean avviamentoOrigine = cercaAvviamento(c, i);
				if (avviamentoOrigine == null) {
					_logger.fatal("Impossibile trovare l'avviamento origine per la cessazione SC" + c.toString());

					continue;
				}
				String motivoCessazione = (String) c.getAttribute("MOTCESSAZIONE");
				m.updAttribute("MOTCESSAZIONE", motivoCessazione);

				aggiungiRisultato(avviamentoOrigine, m);
			} else {
				SourceBean avviamentoOrigine = cercaAvviamento(m, i);
				if (avviamentoOrigine == null) {
					_logger.fatal("Impossibile trovare l'avviamento origine per la cessazione NON SC" + m.toString());

					continue;
				}
				aggiungiRisultato(avviamentoOrigine, m);
			}
		}
		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		String flgMovApprendistatoTIMob = sbGenerale.getAttribute("FLGAPPRENDISTATOTIMB") != null
				? sbGenerale.getAttribute("FLGAPPRENDISTATOTIMB").toString()
				: "";
		if (flgMovApprendistatoTIMob.equalsIgnoreCase("S")) {
			int numeroRisultatiSenzaApp = this.numeroRisultati;
			// i movimenti di apprendistato a TD devono essere considerati come movimenti che possono
			// portare il lavoratore in mobilità
			gestioneMovApprendistatoTD();
			if (numeroRisultatiSenzaApp < this.numeroRisultati) {
				ordinaRisultati();
			}
		}
	}

	/**
	 * @param m
	 * @return
	 */
	private boolean isAvviamentoRS3(SourceBean m) {
		String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
		String codTipoAss = (String) m.getAttribute("CODTIPOASS");
		return ("AVV".equals(codTipoMov) && "Z.09.02".equals(codTipoAss));
	}

	/**
	 * @param c
	 *            cessazione da cui partire
	 * @param da
	 *            indice da cui scorrere la lista dei movimenti
	 * @return
	 * @throws Exception
	 */
	private SourceBean cercaAvviamento(SourceBean c, int da) throws Exception {
		SourceBean temp = c;
		SourceBean avviamentoOrigine = null;
		String numOreSettimanali = "";
		boolean checkOreSett = false;
		for (int i = da; i >= 0; i--) {
			SourceBean m = (SourceBean) movimenti.get(i);
			if (isPrecedente(m, temp)) {
				if (isAvviamentoRS(m)) {
					temp = cercaCessazioneMob(m, i);
					if (temp == null)
						; // errore
					continue;
				} else if (isAvviamentoEffettivo(m)) {
					avviamentoOrigine = m;
					break;
				} else if (isTraOrPro(m)) {
					temp = m;
					String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
					String codMonoTempo = m.containsAttribute("codmonotempo")
							? m.getAttribute("codmonotempo").toString()
							: "";
					if (codTipoMov.equalsIgnoreCase("TRA") && codMonoTempo.equalsIgnoreCase("I")) {
						String codOrario = m.containsAttribute("CODORARIO") ? m.getAttribute("CODORARIO").toString()
								: "";
						if ((!codOrario.equals("")) && (!codOrario.equalsIgnoreCase("F"))
								&& (!codOrario.equalsIgnoreCase("N"))) {
							if (!checkOreSett) {
								numOreSettimanali = m.containsAttribute("NUMORESETTIMANALI")
										? m.getAttribute("NUMORESETTIMANALI").toString()
										: "";
								checkOreSett = true;
							}
						} else {
							numOreSettimanali = "";
							checkOreSett = true;
						}
					}
					continue;
				} else if (isCessazioneSC(m)) {
					temp = m;
					continue;
				} else {
					// errore
				}
			}
		}
		if (avviamentoOrigine != null) {
			if (checkOreSett) {
				if (numOreSettimanali.equals("")) {
					avviamentoOrigine.delAttribute("NUMORESETTIMANALI");
				} else {
					avviamentoOrigine.updAttribute("NUMORESETTIMANALI", numOreSettimanali);
				}
			}
		}
		return avviamentoOrigine;
	}

	private boolean isPrecedente(SourceBean m, SourceBean succ) {
		Object prgMovimentoPrec = succ.getAttribute("PRGMOVIMENTOPREC");
		String prgMovimento = m.getAttribute("PRGMOVIMENTO").toString();
		return (prgMovimentoPrec != null && prgMovimentoPrec.toString().equals(prgMovimento));
	}

	/**
	 * cessazione di un tempo TI, non SC oppure un avviamento TI Z.09.02(vecchio RS3)
	 * 
	 * @param m
	 * @return
	 */
	private boolean isUnaCessazione(SourceBean m) {
		String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
		String codMotivoCes = (String) m.getAttribute("CODMVCESSAZIONE");
		String codTipoAss = (String) m.getAttribute("CODTIPOASS");
		String codMonoTempo = (String) m.getAttribute("CODMONOTEMPO");
		String prgAzienda = m.getAttribute("PRGAZIENDA").toString();
		// se la ricerca e' limitata ad una azienda, considero solo le
		// cessazioni per quell'azienda
		if (this.prgAziendaRicerca != null && !this.prgAziendaRicerca.equals(prgAzienda))
			return false;
		// deve trattarsi di una cessazione di avviamento indeterminato
		if (!"I".equals(codMonoTempo))
			return false;

		if ("CES".equals(codTipoMov) && !("SC".equals(codMotivoCes)))
			return true;
		if ("AVV".equals(codTipoMov) && "Z.09.02".equals(codTipoAss))
			return true;
		// else
		return false;
	}

	/**
	 * Avviamento Z.09.00/Z.09.01/Z.09.02
	 */
	private boolean isAvviamentoRS(SourceBean m) {
		String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
		String codTipoAss = (String) m.getAttribute("CODTIPOASS");
		return ("AVV".equals(codTipoMov)
				&& ("Z.09.00".equals(codTipoAss) || "Z.09.01".equals(codTipoAss) || "Z.09.02".equals(codTipoAss)));
	}

	/**
	 * Avviamento non Z.09.00/Z.09.01/Z.09.02
	 */
	private boolean isAvviamentoEffettivo(SourceBean m) {
		String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
		String codTipoAss = (String) m.getAttribute("CODTIPOASS");
		return ("AVV".equals(codTipoMov)
				&& !("Z.09.00".equals(codTipoAss) || "Z.09.01".equals(codTipoAss) || "Z.09.02".equals(codTipoAss)));
	}

	/**
	 * Movimento di TRA oppure di PRO
	 */
	private boolean isTraOrPro(SourceBean m) {
		String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
		return ("TRA".equals(codTipoMov) || "PRO".equals(codTipoMov));
	}

	private boolean isCessazioneSC(SourceBean m) {
		String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
		String codMVCessazione = m.containsAttribute("CODMVCESSAZIONE") ? m.getAttribute("CODMVCESSAZIONE").toString()
				: "";
		return ("CES".equals(codTipoMov) && "SC".equals(codMVCessazione));
	}

	/**
	 * Aggiunge alla lista dei movimenti la cessazione effettiva/avviamento Z.09.02(vecchio RS3), modificata nelle data
	 * inizio e fine effettiva
	 * 
	 * @param avvIniziale
	 *            avviamento iniziale
	 * @param cessazione
	 *            effettiva dopo la sospensione della mobilita'
	 */
	private void aggiungiRisultato(SourceBean avvIniziale, SourceBean cessazione) throws SourceBeanException {
		String dataInizioMov = (String) avvIniziale.getAttribute("DATINIZIOMOV");
		String dataFineMov = (String) cessazione.getAttribute("DATINIZIOMOV");
		String oreSett = avvIniziale.containsAttribute("NUMORESETTIMANALI")
				? avvIniziale.getAttribute("NUMORESETTIMANALI").toString()
				: "";
		cessazione.updAttribute("DATINIZIOMOV", dataInizioMov);
		cessazione.updAttribute("DATFINEMOVEFFETTIVA", dataFineMov);
		if (!oreSett.equals("")) {
			cessazione.updAttribute("NUMORESETTIMANALI", oreSett);
		}
		this.risultato.setAttribute(cessazione);
		numeroRisultati = numeroRisultati + 1;
	}

	/**
	 * Cerca a ritroso nella lista dei movimenti una cessazione di tipo SC con lo stesso prgAzienda
	 * 
	 * @param avv
	 *            avviamento Z.09.00-Z.09.01/Z.09.02
	 * @return
	 */
	private SourceBean cercaCessazioneMob(SourceBean avv, int da) {
		String prgAziendaAvv = avv.getAttribute("PRGAZIENDA").toString();
		SourceBean cessazione = null;
		for (; da >= 0; da--) {
			SourceBean m = (SourceBean) this.movimenti.get(da);
			String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
			String prgAzienda = m.getAttribute("PRGAZIENDA").toString();
			String codMotivoCes = (String) m.getAttribute("CODMVCESSAZIONE");
			if (prgAziendaAvv.equals(prgAzienda) && "CES".equals(codTipoMov) && ("SC".equals(codMotivoCes))) {
				cessazione = m;
			}
		}
		return cessazione;
	}

	private void gestioneMovApprendistatoTD() throws SourceBeanException {
		for (int i = 0; i < movimenti.size(); i++) {
			SourceBean m = (SourceBean) movimenti.get(i);
			String codTipoMov = (String) m.getAttribute("CODTIPOMOV");
			boolean aggiungiApprendistato = true;
			boolean trovato = false;
			SourceBean mSucc = null;
			String prgAzienda = m.getAttribute("PRGAZIENDA").toString();
			if ((codTipoMov.equalsIgnoreCase("AVV"))
					&& (this.prgAziendaRicerca == null || this.prgAziendaRicerca.equals(prgAzienda))) {
				String codTipoAss = (String) m.getAttribute("CODTIPOASS");
				String codMonoTempo = (String) m.getAttribute("CODMONOTEMPO");
				if (codMonoTempo.equalsIgnoreCase("D")
						&& (codTipoAss.equalsIgnoreCase("A.03.00") || codTipoAss.equalsIgnoreCase("A.03.01")
								|| codTipoAss.equalsIgnoreCase("A.03.02") || codTipoAss.equalsIgnoreCase("A.03.03"))) {
					Object prgMovimentoSucc = m.getAttribute("PRGMOVIMENTOSUCC");
					String dataFineMovApprendistato = (String) m.getAttribute("DATFINEMOVEFFETTIVA");
					mSucc = m;
					while (prgMovimentoSucc != null) {
						trovato = false;
						for (int j = i + 1; j < movimenti.size(); j++) {
							mSucc = (SourceBean) movimenti.get(j);
							Object prgMovimento = mSucc.getAttribute("PRGMOVIMENTO");
							if (prgMovimento.equals(prgMovimentoSucc)) {
								trovato = true;
								String codTipoMovSucc = (String) mSucc.getAttribute("CODTIPOMOV");
								String codMonoTempoSucc = (String) mSucc.getAttribute("CODMONOTEMPO");
								if (codTipoMovSucc.equalsIgnoreCase("CES")) {
									prgMovimentoSucc = null;
								} else {
									if (codMonoTempoSucc.equalsIgnoreCase("D")) {
										prgMovimentoSucc = mSucc.getAttribute("PRGMOVIMENTOSUCC");
										dataFineMovApprendistato = (String) mSucc.getAttribute("DATFINEMOVEFFETTIVA");
									} else {
										aggiungiApprendistato = false;
										prgMovimentoSucc = null;
									}
								}
								break;
							}
						} // fine FOR
						if (!trovato) {
							prgMovimentoSucc = null;
							aggiungiApprendistato = false;
						}
					} // fine WHILE
					if (aggiungiApprendistato) {
						String dataInizioMovApp = (String) m.getAttribute("DATINIZIOMOV");
						String numOreSettimanali = m.containsAttribute("NUMORESETTIMANALI")
								? m.getAttribute("NUMORESETTIMANALI").toString()
								: "";
						String numOreSettimanaliSucc = mSucc.containsAttribute("NUMORESETTIMANALI")
								? mSucc.getAttribute("NUMORESETTIMANALI").toString()
								: "";
						mSucc.updAttribute("DATINIZIOMOV", dataInizioMovApp);
						mSucc.updAttribute("DATFINEMOVEFFETTIVA", dataFineMovApprendistato);
						if ((numOreSettimanaliSucc.equals("")) && (!numOreSettimanali.equals(""))) {
							mSucc.updAttribute("NUMORESETTIMANALI", numOreSettimanali);
						}
						this.risultato.setAttribute(mSucc);
						numeroRisultati = numeroRisultati + 1;
					}
				}
			}
		}
	}

	private void ordinaRisultati() throws Exception {
		SourceBean risultatoFinale = new SourceBean("ROWS");
		int indiceMinimo = 0;
		Vector vettRis = risultato.getAttributeAsVector("ROW");
		while (vettRis.size() > 0) {
			indiceMinimo = 0;
			SourceBean sb1 = (SourceBean) vettRis.get(0);
			String dataInizioMin = sb1.getAttribute("DATINIZIOMOV").toString();
			for (int j = 1; j < vettRis.size(); j++) {
				SourceBean sb2 = (SourceBean) vettRis.get(j);
				String dataInizioCurr = sb2.getAttribute("DATINIZIOMOV").toString();
				if (DateUtils.compare(dataInizioCurr, dataInizioMin) < 0) {
					indiceMinimo = j;
					dataInizioMin = dataInizioCurr;
				}
			}
			sb1 = (SourceBean) vettRis.get(indiceMinimo);
			risultatoFinale.setAttribute(sb1);
			vettRis.remove(indiceMinimo);
		}
		this.risultato = new SourceBean(risultatoFinale);
	}

}