package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;

public class StatoOccupazionaleManager2 {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(StatoOccupazionaleManager2.class.getName());

	private TransactionQueryExecutor txExecutor;
	private RequestContainer requestContainer;
	private List movimentiAnno;
	private DidBean did;
	private PattoBean patto;
	private CmBean cm;
	private Vector listaDisabiliCM = new Vector();
	private String codMonoProvenienza;
	private SituazioneAmministrativa sitAmm;

	public void setPatto(PattoBean patto) {
		this.patto = patto;
	}

	public void setCm(CmBean cm) {
		this.cm = cm;
	}

	public void setDid(DidBean did) {
		this.did = did;
	}

	public void setMovimentiAnno(List movimenti) {
		this.movimentiAnno = movimenti;
	}

	public void setListaDisabiliCM(Vector cm) {
		this.listaDisabiliCM = cm;
	}

	/**
	 * costruttore della classe che prende la situazione amministrativa del lavoratore
	 * 
	 * @param sitAmm
	 */
	public StatoOccupazionaleManager2(SituazioneAmministrativa sitAmm) {
		this.txExecutor = sitAmm.getTxExecutor();
		this.requestContainer = sitAmm.getRequestContainer();
		this.sitAmm = sitAmm;
		if (getCm() == null) {
			setCm(getSitAmm().getCm());
		}
		if (getListaDisabiliCM() == null || getListaDisabiliCM().size() == 0) {
			if (getSitAmm().getListaDisabiliCM() != null && getSitAmm().getListaDisabiliCM().size() > 0) {
				setListaDisabiliCM(getSitAmm().getListaDisabiliCM());
			}
		}
	}

	public void setCodMonoProvenienza(String cod) {
		this.codMonoProvenienza = cod;
	}

	public CmBean getCm() {
		return this.cm;
	}

	public Vector getListaDisabiliCM() {
		return this.listaDisabiliCM;
	}

	public SituazioneAmministrativa getSitAmm() {
		return this.sitAmm;
	}

	/**
	 * 
	 * @param movimento
	 * @param statoOccIniziale
	 * @param movimenti
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	public StatoOccupazionaleBean proroga(MovimentoBean movimento, StatoOccupazionaleBean statoOccIniziale,
			Vector movimenti, Vector vettParametriRicostruzione, int posizione) throws ControlliException, Exception {
		// rileggi il movimento collegato , e' gia' associato all'oggetto
		// movimento?
		// fai il controllo dello stato occupazionale come se fosse un
		// avviamento , con la differenza che nel calcolo dello
		// stato di sospensione la durata del rapporto va sommata a quella del
		// movimento prorogato
		MovimentoBean movimentoPrec = movimento.getMovimentoBack();
		MovimentoBean movimentoPrePre = null;
		while (movimentoPrec != null) {
			movimentoPrePre = movimentoPrec;
			if (movimentoPrec.getTipoMovimento() == MovimentoBean.PROROGA)
				movimentoPrec = movimentoPrec.getMovimentoBack();
			else
				break;
			// ho tutti i movimenti collegati in modo da poter calcolare il
			// numero di mesi conplessivi di lavoro
		}
		if (movimentoPrec == null) {
			if (movimentoPrePre != null)
				movimentoPrec = movimentoPrePre;
			else
				movimentoPrec = movimento;
			while (true) {
				Object prgMovimentoPrec = movimentoPrec.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
				SourceBean sbPrec = DBLoad.getMovimento(prgMovimentoPrec, txExecutor);
				MovimentoBean mbPrec = new MovimentoBean(sbPrec);
				movimentoPrec.setMovimentoBack(mbPrec);
				if (mbPrec.getTipoMovimento() != MovimentoBean.PROROGA)
					break;
				movimentoPrec = mbPrec;
			}
		}
		_logger.debug("StatoOccupazionaleManager2.proroga():" + movimento);

		return avviamento(movimento, statoOccIniziale, movimenti, vettParametriRicostruzione, posizione);
	}

	/**
	 * @param MovimentoBean
	 *            <b>movimento</b> il movimento in base al quale calcolare il nuovo stato occupazionale
	 * @param StatoOccupazionaleBean
	 *            statoOccIniziale
	 * @param Vector
	 *            <b>movimenti</b> i movimenti aperti nell'anno di inserimento del movimento
	 * 
	 * @return StatoOccupazionaleBean il nuovo stato occupazionale del lavoratore
	 */
	public StatoOccupazionaleBean avviamento(MovimentoBean movimento, StatoOccupazionaleBean statoOccIniziale,
			Vector movimenti, Vector vettParametriRicostruzione, int posizione) throws ControlliException, Exception {

		CmBean cm = getCm();
		// controllo se la dataInizio del movimento si trova in un periodo di
		// mobilità del lavoratore
		String dataInizioMov = movimento.getDataInizio();
		String codTipoAss = "";
		int nPosMobilita = -1;
		nPosMobilita = Controlli.isInMobilita(this.sitAmm.getListaMobilita(), dataInizioMov);
		String codTipoAvviamento = "";
		String dataInizio = null;
		dataInizio = movimento.getDataInizio();
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		Vector vettMovAperti = null;

		boolean gestioneDecreto150 = (this.sitAmm.getData150() != null && !this.sitAmm.getData150().equals("")
				&& DateUtils.compare(dataInizio, this.sitAmm.getData150()) >= 0);
		boolean iscrittoCM = Controlli.inCollocamentoMiratoAllaData(this.sitAmm.getListaDisabiliCM(), dataInizio);

		switch (statoOccIniziale.getStatoOccupazionaleRagg()) {
		// se altro non fare nulla
		case StatoOccupazionaleBean.RAGG_A:
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			codTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			if (codTipoAvviamento.equals("Z.09.02")) {
				// cessazione(tipo di movimento C): il lavoratore diviene
				// cessato non rientrato (AM5.1)
				// controllo sempre se non ha movimenti aperti
				vettMovAperti = Controlli.listaMovimentiAperti(movimenti, dataInizio);
				if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
					if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.C0) {
						nuovoStatoOccupazionale = statoOccIniziale;
						nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.C0);
						nuovoStatoOccupazionale.setChanged(true);
						nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
					} else {
						nuovoStatoOccupazionale = statoOccIniziale;
						nuovoStatoOccupazionale.setChanged(false);
					}
				} else {
					nuovoStatoOccupazionale = statoOccIniziale;
					nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B);
					nuovoStatoOccupazionale.setChanged(true);
					nuovoStatoOccupazionale.setCodMonoProvenienza("M");
					nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
				}

				if (nuovoStatoOccupazionale.ischanged()) {
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer, txExecutor);
				}
			} else {
				if (movimento.getCollegato() == null && movimento.getAttribute("CODTIPOMOV").equals("TRA")) {
					// Movimento non collegato
					if (nPosMobilita >= 0 && !movimento.containsAttribute("FLG_MOVIMENTO_NON_MOBILITA")) {
						nuovoStatoOccupazionale = calcolaStatoOccInMobilita(movimento, statoOccIniziale,
								vettParametriRicostruzione, this.sitAmm.getData150(), this.sitAmm.getListaDisabiliCM());
					} else {
						vettMovAperti = Controlli.listaMovimentiAperti(movimenti, dataInizio);
						if (codTipoAss.equalsIgnoreCase("T")
								&& (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti))) {
							nuovoStatoOccupazionale = statoOccIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						} else {
							if (codTipoAss.equalsIgnoreCase("T")
									&& DeTipoContrattoConstant.mapContratti_Tirocini.containsKey(codTipoAvviamento)) {
								Controlli.addWarning(
										MessageCodes.StatoOccupazionale.TIROCINIO_DURANTE_ATTIVITA_LAVORATIVA,
										"Data tirocinio " + dataInizioMov, requestContainer);
							}
							StatoOccupazionaleBean n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(movimento.getCdnLavoratore());
							n.setStatoOccupazionaleRagg("O");
							n.setStatoOccupazionale("B");
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
							if (nuovoStatoOccupazionale.ischanged())
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
										txExecutor);
						}
					}
				} else {
					if (nPosMobilita >= 0 && !movimento.containsAttribute("FLG_MOVIMENTO_NON_MOBILITA")) {
						nuovoStatoOccupazionale = calcolaStatoOccInMobilita(movimento, statoOccIniziale,
								vettParametriRicostruzione, this.sitAmm.getData150(), this.sitAmm.getListaDisabiliCM());
					} else {
						vettMovAperti = Controlli.listaMovimentiAperti(movimenti, dataInizio);
						if (codTipoAss.equalsIgnoreCase("T")
								&& (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti))) {
							nuovoStatoOccupazionale = statoOccIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						} else {
							if (codTipoAss.equalsIgnoreCase("T")
									&& DeTipoContrattoConstant.mapContratti_Tirocini.containsKey(codTipoAvviamento)) {
								Controlli.addWarning(
										MessageCodes.StatoOccupazionale.TIROCINIO_DURANTE_ATTIVITA_LAVORATIVA,
										"Data tirocinio " + dataInizioMov, requestContainer);
							}
							nuovoStatoOccupazionale = StatoOccupazionaleSuccessivo.calcola(movimento, cm, null,
									statoOccIniziale, movimenti, sitAmm.getListaMobilita(),
									StatoOccupazionaleManager.NO_SANAMENTO_SITUAZIONE, sitAmm.getData150(),
									sitAmm.getListaDisabiliCM(), txExecutor);
							if (nuovoStatoOccupazionale.getStatoOccupazionale() == StatoOccupazionaleBean.B) {
								nuovoStatoOccupazionale.setChanged(true);
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
								nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
										txExecutor);
							} else {
								_logger.debug(
										"StatoOccupazionaleManager2.avviamento(): stato occupazionale iniziale non gestito nei diagrammi standard");

							}
						}
					}
				}
			}
			break;
		case StatoOccupazionaleBean.RAGG_O:
			// gestione ripresa attività lavorativa oppure cessazione definitiva
			// attività lavorativa
			if (statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A0) {
				// Sospeso per contrazione d'attività
				codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
						? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
						: "";
				if (codTipoAvviamento.equals("Z.09.02")) {
					// cessazione(tipo di movimento C): il lavoratore diviene
					// cessato non rientrato (AM5.1)
					// controllo sempre se non ha movimenti aperti
					vettMovAperti = Controlli.listaMovimentiAperti(movimenti, dataInizio);
					if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
						nuovoStatoOccupazionale = statoOccIniziale;
						nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.C0);
						nuovoStatoOccupazionale.setChanged(true);
						nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
					} else {
						nuovoStatoOccupazionale = statoOccIniziale;
						nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B);
						nuovoStatoOccupazionale.setChanged(true);
						nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
					}
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer, txExecutor);
				} else {
					// CASO IN CUI LO STATO OCC AO(SOSPESO PER CONTRAZIONE
					// ATTIVITA' E' STATO GENERATO PER
					// UN LAVORATORE NON IN 150; QUESTO SIGNIFICA CHE QUANDO
					// REGISTRO UN MOVIMENTO DI RIPRESA
					// ATTIVITA' IL LAVORATORE RISULTA ESSERE OCCUPATO).
					nuovoStatoOccupazionale = statoOccIniziale;
					nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B);
					nuovoStatoOccupazionale.setChanged(true);
					nuovoStatoOccupazionale.setCodMonoProvenienza("M");
					nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer, txExecutor);
				}
			} else {
				if (movimento.getCollegato() == null && movimento.getAttribute("CODTIPOMOV").equals("TRA")) {
					// Movimento non collegato
					if ((statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A1)
							|| (statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.B)) {
						nuovoStatoOccupazionale = statoOccIniziale;
						nuovoStatoOccupazionale.setChanged(false);
					}
				} else {
					// Movimento collegato
					// se occupato
					// non faccio nessun controllo , tanto i casi non dipendono
					// dal movimento o quant'altro
					if (statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A1) {
						nuovoStatoOccupazionale = statoOccIniziale;
						nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B);
						nuovoStatoOccupazionale.setChanged(true);
						nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer, txExecutor);
					} else {
						SourceBean pattoAperto = this.sitAmm.cercaPatto(dataInizio);
						SourceBean didAperta = this.sitAmm.cercaDid(dataInizio);
						String dataChiusura = DateUtils.giornoPrecedente(dataInizio);

						// seleziona il patto 'aperto'
						if (didAperta != null) {
							String dataDichiarazione = (String) didAperta.getAttribute("datDichiarazione");
							if (dataDichiarazione != null && !dataDichiarazione.equals("")
									&& DateUtils.compare(dataDichiarazione, dataChiusura) > 0)
								dataChiusura = dataDichiarazione;
							if (!didAperta.containsAttribute("codMotivoFineAtto")
									|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("")
									|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
								DBStore.chiudiDID(didAperta, dataChiusura, "AV", requestContainer, txExecutor);
								DidBean didApertaBean = new DidBean(didAperta);
								sitAmm.chiudiDidInVettoreDids(didApertaBean.getPrgDichDisponibilita(), dataChiusura);
								List movimentiAmm = sitAmm.getMovimenti();
								sitAmm.aggiornaNumKloDichDispoInMovimenti(didApertaBean,
										new BigDecimal(didApertaBean.getAttribute("numKloDichDisp").toString()),
										movimentiAmm);
							} else {
								StatoOccupazionaleBean.addWarning(MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO,
										requestContainer);
							}
						}

						if (pattoAperto != null) {
							if (!pattoAperto.containsAttribute("codMotivoFineAtto")
									|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("")
									|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
								DBStore.chiudiPatto297(pattoAperto, dataChiusura, "AV", requestContainer, txExecutor);
								sitAmm.aggiornaNumKloPatto(
										new BigDecimal(pattoAperto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
										dataChiusura, new BigDecimal(
												pattoAperto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
							}
						}
						nuovoStatoOccupazionale = statoOccIniziale;
					}
				}
			}
			break;
		case StatoOccupazionaleBean.RAGG_I:
			if (movimento.getCollegato() == null && movimento.getAttribute("CODTIPOMOV").equals("TRA")) {
				// Movimento non collegato
				// bisogna generare un'eccezione e segnalare che non è possibile
				// che ci sia una
				// trasformazione senza movimento collegato dopo un periodo in
				// cui il lavoratore si trova in 150
				throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297);
			} else {
				// Se il movimento contiene il campo FLGCONTRATTODECADENZA, allora significa che ha provocato la
				// decadenza della mobilità per superamento reddito
				if (movimento.containsAttribute("FLGCONTRATTODECADENZA")) {
					nuovoStatoOccupazionale = statoOccupatoDecadenzaReddito(sitAmm, movimento.getCdnLavoratore(),
							statoOccIniziale, dataInizio);
				} else {
					codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
							? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
							: "";
					if (codTipoAvviamento.equals("Z.09.02")) {
						nuovoStatoOccupazionale = statoOccIniziale;
						nuovoStatoOccupazionale.setChanged(false);
					} else {
						// Movimento collegato
						String dataRif = "";
						dataRif = movimento.getDataInizioAvv();
						if (dataRif == null || dataRif.equals("")) {
							dataRif = movimento.getDataInizio();
						}
						DidBean didBean = (DidBean) sitAmm.cercaDid(dataRif);
						String dataDid = null;
						boolean didPresente = false;
						if (didBean != null) {
							didPresente = true;
							dataDid = (String) didBean.getAttribute("datDichiarazione");
						} else {
							dataDid = movimento.getDataInizio();
						}
						BigDecimal prgPeriodoLav = (BigDecimal) movimento.getAttribute("PRGPERIODOINTERMITTENTE");
						movimenti = sitAmm.getMovimentiAnnoDallaDID(movimento.getDataInizio(), dataDid,
								movimento.getPrgMovimento(), prgPeriodoLav, didPresente);
						// essendo lo stato occupazionale di partenza di inoccupato
						// non dovrebbero esserci movimenti aperti
						String codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
								? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
								: "";
						if (codMonoTipoAss.equals("T")) {
							nuovoStatoOccupazionale = statoOccIniziale;
							if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.A223) {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A223);
								nuovoStatoOccupazionale.setChanged(true);
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
								nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
										txExecutor);
							} else {
								nuovoStatoOccupazionale.setChanged(false);
							}
						} else {
							nuovoStatoOccupazionale = StatoOccupazionaleSuccessivo.calcola(movimento, cm, didBean,
									statoOccIniziale, movimenti, sitAmm.getListaMobilita(),
									StatoOccupazionaleManager.NO_SANAMENTO_SITUAZIONE, sitAmm.getData150(),
									sitAmm.getListaDisabiliCM(), txExecutor);
							switch (nuovoStatoOccupazionale.getStatoOccupazionale()) {
							// il dettaglio
							case StatoOccupazionaleBean.C:
							case StatoOccupazionaleBean.A22:
								// in teoria da disoccupato non e' possibile
								// arrivare in questi due stati
								// sarebbe un errore quindi lanciare una eccezione
								// throw new Exception("stato occupazionale di
								// arrivo non possibile");
								nuovoStatoOccupazionale = statoOccIniziale;
								break;
							case StatoOccupazionaleBean.B:
								// OCCUPATO
								// debbo annullare i campi indennita' di
								// disoccupazione e data anzianita' di
								// disoccupazione
								nuovoStatoOccupazionale.setIndennizzato(null);
								nuovoStatoOccupazionale.setDataAnzianita(null);
								//
								nuovoStatoOccupazionale.setChanged(true);
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
								nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
										txExecutor);
								// ora bisogna chiudere la did ed il patto
								// AV "perdita dello stato di disoccupazione"
								/** ****************************************************************** */
								String dataChiusura = DateUtils.giornoPrecedente(
										(String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO));
								Object cdnLavoratore = movimento.getCdnLavoratore();
								_logger.debug("StatoOccupazionaleManager2.avviamento():chiusura did e patto");

								// seleziona la did 'aperta'
								SourceBean pattoAperto = this.sitAmm.cercaPatto(dataInizio);
								SourceBean didAperta = this.sitAmm.cercaDid(dataInizio);

								// seleziona il patto 'aperto'
								if (didAperta != null) {
									String dataDichiarazione = (String) didAperta.getAttribute("datDichiarazione");
									if (dataDichiarazione != null && !dataDichiarazione.equals("")
											&& DateUtils.compare(dataDichiarazione, dataChiusura) > 0)
										dataChiusura = dataDichiarazione;
									if (!didAperta.containsAttribute("codMotivoFineAtto")
											|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("")
											|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
										DBStore.chiudiDID(didAperta, dataChiusura, "AV", requestContainer, txExecutor);
										DidBean didApertaBean = new DidBean(didAperta);
										sitAmm.chiudiDidInVettoreDids(didApertaBean.getPrgDichDisponibilita(),
												dataChiusura);
										List movimentiAmm = sitAmm.getMovimenti();
										sitAmm.aggiornaNumKloDichDispoInMovimenti(didApertaBean,
												new BigDecimal(didApertaBean.getAttribute("numKloDichDisp").toString()),
												movimentiAmm);
									} else {
										StatoOccupazionaleBean.addWarning(
												MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO, requestContainer);
									}
								}

								if (pattoAperto != null) {
									if (!pattoAperto.containsAttribute("codMotivoFineAtto")
											|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("")
											|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
										DBStore.chiudiPatto297(pattoAperto, dataChiusura, "AV", requestContainer,
												txExecutor);
										sitAmm.aggiornaNumKloPatto(
												new BigDecimal(
														pattoAperto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
												dataChiusura, new BigDecimal(pattoAperto
														.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
									}
								}
								/**/
								break;
							case StatoOccupazionaleBean.A212:
								// DISOCCUPATO IN SOSPENSIONE DI ATTIVITA' (le
								// operazione sono le stesse del caso successivo)
							case StatoOccupazionaleBean.B1:
								// PRECARIO CON ATTIVITA' LAVORATIVA CHE NON
								// SOSPENDE LO STATO DI DISOCCUPAZIONE
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
								nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
								nuovoStatoOccupazionale.setChanged(true);
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
										txExecutor);
								// ora nell' oggetto statoOccupazionale e' presente
								// la chiave del record inserito da utilizzare come
								// fk
								// in insert movimento
								break;

							case StatoOccupazionaleBean.A21:
								// Disoccupato (può succedere se il rapporto dura
								// meno di 16 giorni)
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
								nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
								nuovoStatoOccupazionale.setChanged(true);
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
										txExecutor);
								break;
							/*
							 * case StatoOccupazionale.D4: ******* VEDI PIU' SU *******
							 */
							// mantengo lo stato di disoccupazione e non dovrei
							// chiudere ed inserire alcunche'
							// pero' .......
							// ci sono casi particolari a seconda del dettaglio di
							// partenza e arrivo
							default:
								// erroraccio
								// fare in modo che non ci si arrivi, quindi gestire
								// errori precedenti tramite lancio di
								// eccezioni.
								nuovoStatoOccupazionale = statoOccIniziale;
								// throw new Exception("Stato occupazionale di
								// arrivo non possibile");
							}
						}
					}
				} // end else
			}
			break;
		// se disoccupato
		case StatoOccupazionaleBean.RAGG_D:
			if (movimento.getCollegato() == null && movimento.getAttribute("CODTIPOMOV").equals("TRA")) {
				// Movimento non collegato
				// bisogna generare un'eccezione e segnalare che non è possibile
				// che ci sia una
				// trasformazione senza movimento collegato dopo un periodo in
				// cui il lavoratore si trova in 150
				throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297);
			} else {
				// Movimento collegato
				// Se il movimento contiene il campo FLGCONTRATTODECADENZA, allora significa che ha provocato la
				// decadenza della mobilità per superamento reddito
				if (movimento.containsAttribute("FLGCONTRATTODECADENZA")) {
					nuovoStatoOccupazionale = statoOccupatoDecadenzaReddito(sitAmm, movimento.getCdnLavoratore(),
							statoOccIniziale, dataInizio);
				} else {
					if (nPosMobilita >= 0 && !movimento.containsAttribute("FLG_MOVIMENTO_NON_MOBILITA")) {
						codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
								? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
								: "";
						if (codTipoAvviamento.equals("Z.09.02")) {
							nuovoStatoOccupazionale = statoOccIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						} else {
							nuovoStatoOccupazionale = calcolaStatoOccInMobilita(movimento, statoOccIniziale,
									vettParametriRicostruzione, this.sitAmm.getData150(),
									this.sitAmm.getListaDisabiliCM());
						}
					} else {
						// Sospeso per contrazione d'attività
						codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
								? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
								: "";
						if (codTipoAvviamento.equals("Z.09.02")
								&& statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A212) {
							// cessazione(tipo di movimento C): il lavoratore
							// diviene Disoccupato (AM5.2)
							// controllo sempre se non ha movimenti aperti
							nuovoStatoOccupazionale = statoOccIniziale;
							vettMovAperti = Controlli.listaMovimentiAperti(movimenti, dataInizio);
							if (vettMovAperti.size() == 0) {
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
								nuovoStatoOccupazionale.setChanged(true);
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
								nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
								DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
										txExecutor);
							} else {
								if (Controlli.soloTirocini(vettMovAperti)) {
									nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
									nuovoStatoOccupazionale.setChanged(true);
									nuovoStatoOccupazionale.setCodMonoProvenienza("M");
									nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
									DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
											txExecutor);
								} else {
									nuovoStatoOccupazionale.setChanged(false);
								}
							}
						} else {
							if (codTipoAvviamento.equals("Z.09.02")
									&& statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A21) {
								nuovoStatoOccupazionale = statoOccIniziale;
								vettMovAperti = Controlli.listaMovimentiAperti(movimenti, dataInizio);
								if (vettMovAperti.size() == 0) {
									nuovoStatoOccupazionale.setChanged(false);
								} else {
									if (Controlli.soloTirocini(vettMovAperti)) {
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
										nuovoStatoOccupazionale.setChanged(true);
										nuovoStatoOccupazionale.setCodMonoProvenienza("M");
										nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
										DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
												txExecutor);
									} else {
										if (gestioneDecreto150 && !iscrittoCM) {
											if (Controlli.isCategoriaParticolareDecreto150(movimento, dataInizio)) {
												nuovoStatoOccupazionale
														.setStatoOccupazionale(StatoOccupazionaleBean.B1);
												nuovoStatoOccupazionale.setChanged(true);
												nuovoStatoOccupazionale.setCodMonoProvenienza("M");
												nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
												DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio,
														requestContainer, txExecutor);
											}
										} else {
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A212);
											nuovoStatoOccupazionale.setChanged(true);
											nuovoStatoOccupazionale.setCodMonoProvenienza("M");
											nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
											DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio,
													requestContainer, txExecutor);
										}
									}
								}
							} else {
								String dataRif = "";
								dataRif = movimento.getDataInizioAvv();
								if (dataRif == null || dataRif.equals("")) {
									dataRif = movimento.getDataInizio();
								}
								DidBean didBean = (DidBean) sitAmm.cercaDid(dataRif);
								boolean didPresente = false;
								String dataDid = null;
								if (didBean != null) {
									didPresente = true;
									dataDid = (String) didBean.getAttribute("datDichiarazione");
								} else {
									dataDid = movimento.getDataInizio();
								}
								BigDecimal prgPeriodoLav = (BigDecimal) movimento
										.getAttribute("PRGPERIODOINTERMITTENTE");
								movimenti = sitAmm.getMovimentiAnnoDallaDID(movimento.getDataInizio(), dataDid,
										movimento.getPrgMovimento(), prgPeriodoLav, didPresente);
								codTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
										? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
										: "";
								vettMovAperti = Controlli.listaMovimentiAperti(movimenti, dataInizio);
								if (codTipoAss.equalsIgnoreCase("T")) {
									nuovoStatoOccupazionale = statoOccIniziale;
									if (vettMovAperti.size() == 0
											|| (vettMovAperti.size() > 0 && Controlli.soloTirocini(vettMovAperti))) {
										if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.A213) {
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
											nuovoStatoOccupazionale.setChanged(true);
											nuovoStatoOccupazionale.setCodMonoProvenienza("M");
											nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
											DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio,
													requestContainer, txExecutor);
										} else {
											nuovoStatoOccupazionale.setChanged(false);
										}
									} else {
										if (DeTipoContrattoConstant.mapContratti_Tirocini
												.containsKey(codTipoAvviamento)) {
											Controlli.addWarning(
													MessageCodes.StatoOccupazionale.TIROCINIO_DURANTE_ATTIVITA_LAVORATIVA,
													"Data tirocinio " + dataInizioMov, requestContainer);
										}
										nuovoStatoOccupazionale.setChanged(false);
									}
								} else {
									nuovoStatoOccupazionale = StatoOccupazionaleSuccessivo.calcola(movimento, cm,
											didBean, statoOccIniziale, movimenti, sitAmm.getListaMobilita(),
											StatoOccupazionaleManager.NO_SANAMENTO_SITUAZIONE, sitAmm.getData150(),
											sitAmm.getListaDisabiliCM(), txExecutor);
									switch (nuovoStatoOccupazionale.getStatoOccupazionale()) {
									// il dettaglio
									case StatoOccupazionaleBean.C:
									case StatoOccupazionaleBean.A22:
										// in teoria da disoccupato non e' possibile
										// arrivare in questi due stati
										nuovoStatoOccupazionale = statoOccIniziale;
										break;
									case StatoOccupazionaleBean.B:
										// chiudi record precedente
										// crea un nuovo record di stato
										// occupazionale
										// chiudi il patto e la did (il lavoratore
										// non ne ha piu' diritto)

										nuovoStatoOccupazionale.setIndennizzato(null);
										nuovoStatoOccupazionale.setDataAnzianita(null);
										nuovoStatoOccupazionale.setCodMonoProvenienza("M");
										nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
										nuovoStatoOccupazionale.setChanged(true);
										DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
												txExecutor);
										// ora bisogna chiudere la did ed il patto
										// AV "perdita dello stato di
										// disoccupazione"
										/** ********************************* */
										Object cdnLavoratore = movimento.getCdnLavoratore();
										String dataChiusura = DateUtils.giornoPrecedente(
												(String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO));
										_logger.debug("StatoOccupazionaleManager2.avviamento():chiusura did e patto");

										// seleziona la did 'aperta'
										SourceBean pattoAperto = this.sitAmm.cercaPatto(dataInizio);
										SourceBean didAperta = this.sitAmm.cercaDid(dataInizio);

										// seleziona il patto 'aperto'
										if (didAperta != null) {
											String dataDichiarazione = (String) didAperta
													.getAttribute("datDichiarazione");
											if (dataDichiarazione != null && !dataDichiarazione.equals("")
													&& DateUtils.compare(dataDichiarazione, dataChiusura) > 0)
												dataChiusura = dataDichiarazione;
											if (!didAperta.containsAttribute("codMotivoFineAtto")
													|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("")
													|| didAperta.getAttribute("codMotivoFineAtto").toString()
															.equals("AV")) {
												DBStore.chiudiDID(didAperta, dataChiusura, "AV", requestContainer,
														txExecutor);
												DidBean didApertaBean = new DidBean(didAperta);
												sitAmm.chiudiDidInVettoreDids(didApertaBean.getPrgDichDisponibilita(),
														dataChiusura);
												List movimentiAmm = sitAmm.getMovimenti();
												sitAmm.aggiornaNumKloDichDispoInMovimenti(
														didApertaBean, new BigDecimal(didApertaBean
																.getAttribute("numKloDichDisp").toString()),
														movimentiAmm);
											} else {
												StatoOccupazionaleBean.addWarning(
														MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO,
														requestContainer);
											}
										}

										if (pattoAperto != null) {
											if (!pattoAperto.containsAttribute("codMotivoFineAtto")
													|| pattoAperto.getAttribute("codMotivoFineAtto").toString()
															.equals("")
													|| pattoAperto.getAttribute("codMotivoFineAtto").toString()
															.equals("AV")) {
												DBStore.chiudiPatto297(pattoAperto, dataChiusura, "AV",
														requestContainer, txExecutor);
												sitAmm.aggiornaNumKloPatto(
														new BigDecimal(pattoAperto.getAttribute(PattoBean.PRG_PATTO_LAV)
																.toString()),
														dataChiusura, new BigDecimal(pattoAperto
																.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
											}
										}

										/** ******************* */
										break;
									case StatoOccupazionaleBean.A21:
										// ci sono casi particolari a seconda del
										// dettaglio di partenza e arrivo
										// throw new Exception("stato occupazionale
										// raggiunto non ammesso");
										nuovoStatoOccupazionale = statoOccIniziale;
										break;
									case StatoOccupazionaleBean.A212:
										// precario
										if (statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A21
												|| statoOccIniziale
														.getStatoOccupazionale() == StatoOccupazionaleBean.A213) {
											nuovoStatoOccupazionale.setCodMonoProvenienza("M");
											nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
											nuovoStatoOccupazionale.setChanged(true);
											DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio,
													requestContainer, txExecutor);
										}
										break;
									case StatoOccupazionaleBean.B1:
										if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.B1) {
											nuovoStatoOccupazionale.setChanged(true);
											nuovoStatoOccupazionale.setCodMonoProvenienza("M");
											nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
											DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio,
													requestContainer, txExecutor);
										}
										break;
									case StatoOccupazionaleBean.B3:
										// lo stato occupazionale iniziale è B3 (Disoc/Occupato a rischio
										// disoccupazione) derivante dalla did
										// dichiarata con il flag rischio disoccupazione = S e il movimento inizia lo
										// stesso giorno della did
										nuovoStatoOccupazionale = statoOccIniziale;
										break;
									default:
										// erroraccio
										// fare in modo che non ci si arrivi, quindi
										// gestire errori precedenti tramite lancio
										// di
										// eccezioni.
										nuovoStatoOccupazionale = statoOccIniziale;
									}
								}
							}
						}
					}
				}
			} // end else
			break;
		}
		return nuovoStatoOccupazionale;
	}

	/**
	 * @param MovimentoBean
	 *            <b>movimento</b> il movimento in base al quale calcolare il nuovo stato occupazionale
	 * @param StatoOccupazionaleBean
	 *            statoOccIniziale
	 * @param RequestContainer
	 *            <b>requestContainer</b> e' necessario per permettere il prelievo dei dati in sessione (es. _USER_)
	 * @param SourceBean
	 *            <b>response</b>
	 * @param TransactionQueryExecutor
	 *            <b>transExec</b> le operazioni col db avvengono all' interno di una transazione nella quale viene
	 *            anche inserito il record del movimento.
	 * 
	 * @return StatoOccupazionaleBean il nuovo stato occupazionale del lavoratore
	 */
	public StatoOccupazionaleBean cessazioneTD(Cessazione cessazione, StatoOccupazionaleBean statoOccIniziale,
			Vector movimentiAperti, Vector vettParametriRicostruzione, String dat150, Vector listaDisabiliCM)
			throws ControlliException, Exception {
		MovimentoBean movimento = null;
		if (cessazione.getCollegato() != null)// Movimento collegato
			movimento = cessazione.getMovimentoBack();
		StatoOccupazionaleBean nuovoStatoOcc = cessazione(cessazione, movimento, movimentiAperti, statoOccIniziale,
				vettParametriRicostruzione, dat150, listaDisabiliCM);
		return nuovoStatoOcc;
	}

	public StatoOccupazionaleBean cessazione(Cessazione cessazione, MovimentoBean movimento, Vector movimentiAperti,
			StatoOccupazionaleBean statoOccupazionaleIniziale, Vector vettParametriRicostruzione, String dat150,
			Vector listaDisabiliCM) throws Exception {
		String dataFine = null;
		StatoOccupazionaleBean nuovoStatoOccupazionale = (StatoOccupazionaleBean) statoOccupazionaleIniziale.clone();
		nuovoStatoOccupazionale.setChanged(false);
		String dataRif = "";
		String dataInizio = cessazione.getDataInizio();
		String codMotivoFine = "";
		BigDecimal cdnLavoratore = cessazione.getCdnLavoratore();
		Vector vettMovAperti = null;
		String dataInizioSO = "";
		// Movimento non collegato
		if ((movimento == null) && (cessazione.getCollegato() == null)) {
			dataRif = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		} else {
			dataRif = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		}
		dataInizioSO = DateUtils.giornoSuccessivo(dataRif);

		boolean gestioneDecreto150 = (dat150 != null && !dat150.equals("")
				&& DateUtils.compare(dataInizioSO, dat150) >= 0);
		boolean iscrittoCM = Controlli.inCollocamentoMiratoAllaData(listaDisabiliCM, dataInizioSO);

		String codTipoAvviamento = "";
		String flgImpattiTirInMobEff = vettParametriRicostruzione.get(1).toString();

		_logger.debug(
				"StatoOccupazionaleManager2.cessazione(): avviamento=" + movimento + ", cessazione=" + cessazione);

		switch (statoOccupazionaleIniziale.getStatoOccupazionaleRagg()) {
		case StatoOccupazionaleBean.RAGG_O:
			codMotivoFine = cessazione.containsAttribute("CODMVCESSAZIONE")
					? cessazione.getAttribute("CODMVCESSAZIONE").toString()
					: "";
			if (movimento != null) { // movimento = null per le cessazioni
										// orfane
				vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(), dataInizio);
			} else {
				vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, dataInizio);
			}
			// controllo se il motivo della cessazione è "sospeso dal lavoro"
			// per gestire la creazione
			// dello stato occupazionale Occ: Sospeso per contrazione d'attività
			// (cessazione con motivo
			// S2 per tempi indeterminati e determinati)
			if (codMotivoFine.equals("SC") && !gestioneDecreto150) {
				// controlla se cio sono movimenti aperti
				dataFine = dataInizio;
				if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
					// Il nuovo stato occupazionale è di Sospeso per contrazione d'attività
					nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
					nuovoStatoOccupazionale.setStatoOccupazionaleRagg("O");
					nuovoStatoOccupazionale.setStatoOccupazionale("A0");
					nuovoStatoOccupazionale.setChanged(true);
					nuovoStatoOccupazionale.setCodMonoProvenienza("M");
				} else {
					nuovoStatoOccupazionale = statoOccupazionaleIniziale;
					nuovoStatoOccupazionale.setChanged(false);
					sitAmm.addWarning(MessageCodes.StatoOccupazionale.STATO_SOSPESO_PER_CONTRAZIONE_NON_GENERATO);
				}
			} else {
				if (cessazione.getCollegato() == null) {
					// Movimento non collegato
					dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
					if (statoOccupazionaleIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A0) {
						if (gestioneDecreto150) {
							if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
								// Il nuovo stato occupazionale è di 'Cessato non rientrato'
								nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
								nuovoStatoOccupazionale.setStatoOccupazionaleRagg("A");
								nuovoStatoOccupazionale.setStatoOccupazionale("C0");
								nuovoStatoOccupazionale.setChanged(true);
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							} else {
								// Sospeso per contrazione d'attività
								nuovoStatoOccupazionale = statoOccupazionaleIniziale;
								nuovoStatoOccupazionale.setChanged(false);
							}
						} else {
							// Sospeso per contrazione d'attività
							nuovoStatoOccupazionale = statoOccupazionaleIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						}
					} else {
						// Controllo se esistoni altri movimenti aperti
						vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, dataInizio);
						if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
							// Il nuovo stato occupazionale è di 'Cessato non rientrato'
							nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
							nuovoStatoOccupazionale.setStatoOccupazionaleRagg("A");
							nuovoStatoOccupazionale.setStatoOccupazionale("C0");
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						} else {
							nuovoStatoOccupazionale = statoOccupazionaleIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						}
					}
				} else {
					// se la cessazione si riferisce ad un movimento di
					// avviamento Z.09.02 (vecchio codice RS3) (cessazione
					// attività dopo la sospensione per contrazione, non devo
					// considerarlo perché già
					// considerato come avviamento (tipo di movimento C))
					codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
							? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
							: "";
					if (!codTipoAvviamento.equals("Z.09.02")) {
						if (statoOccupazionaleIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A0) {
							if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
								// Il nuovo stato occupazionale è di 'Cessato non rientrato'
								dataFine = dataInizio;
								nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
								nuovoStatoOccupazionale.setStatoOccupazionaleRagg("A");
								nuovoStatoOccupazionale.setStatoOccupazionale("C0");
								nuovoStatoOccupazionale.setChanged(true);
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							} else {
								nuovoStatoOccupazionale = statoOccupazionaleIniziale;
								nuovoStatoOccupazionale.setChanged(false);
							}
						} else {
							dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
							vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(),
									dataRif);
							if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
								dataFine = (String) movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.C0);
								nuovoStatoOccupazionale.setChanged(true);
								nuovoStatoOccupazionale.setCodMonoProvenienza("M");
								nuovoStatoOccupazionale.setPensionato(statoOccupazionaleIniziale.getPensionato());
							}
						}
					} else {
						if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
							// Il nuovo stato occupazionale è di 'Cessato non rientrato'
							dataFine = dataInizio;
							nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
							nuovoStatoOccupazionale.setStatoOccupazionaleRagg("A");
							nuovoStatoOccupazionale.setStatoOccupazionale("C0");
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						} else {
							nuovoStatoOccupazionale = statoOccupazionaleIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						}
					}
				}
			}

			if (nuovoStatoOccupazionale.ischanged()) {
				DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, DateUtils.giornoSuccessivo(dataFine),
						requestContainer, txExecutor);
			}

			break;

		case StatoOccupazionaleBean.RAGG_D:
			if (cessazione.getCollegato() != null) {
				// se la cessazione si riferisce ad un movimento di avviamento
				// Z.09.02 (vecchio codice RS3) (cessazione
				// attività dopo la sospensione per contrazione, non devo
				// considerarlo perché già
				// considerato come avviamento (tipo di movimento C))
				codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
						? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
						: "";
				if (!codTipoAvviamento.equals("Z.09.02")) {
					dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
					// Movimento collegato
					if (statoOccupazionaleIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A213) {
						vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(), dataRif);
						if (vettMovAperti.size() == 0) {
							dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
							nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
							nuovoStatoOccupazionale.setChanged(true);
						} else {
							if (Controlli.soloTirocini(vettMovAperti)) {
								nuovoStatoOccupazionale = statoOccupazionaleIniziale;
								nuovoStatoOccupazionale.setChanged(false);
							}
						}
					} else if (statoOccupazionaleIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.A212) {
						// gestione stato occupazionale Sospeso per contrazione attività
						codMotivoFine = cessazione.containsAttribute("CODMVCESSAZIONE")
								? cessazione.getAttribute("CODMVCESSAZIONE").toString()
								: "";
						// controllo se il motivo della cessazione è "sospeso dal lavoro" per gestire la creazione dello
						// stato occupazionale
						// Occupato: Sospeso per contrazione d'attività (cessazione con motivo SC per tempi
						// indeterminati e determinati)
						if (codMotivoFine.equals("SC")) {
							nuovoStatoOccupazionale = statoOccupazionaleIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						} else {
							vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(),
									dataRif);
							if (vettMovAperti.size() == 0) {
								dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
								nuovoStatoOccupazionale.setChanged(true);
							} else {
								if (Controlli.soloTirocini(vettMovAperti)) {
									dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
									nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
									nuovoStatoOccupazionale.setChanged(true);
								}
							}
						}
					} else if (statoOccupazionaleIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.B2) {
						vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(), dataRif);
						if (vettMovAperti.size() == 0 || (Controlli.soloTirocini(vettMovAperti)
								&& !flgImpattiTirInMobEff.equalsIgnoreCase("S"))) {
							dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
							nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
							nuovoStatoOccupazionale.setChanged(true);
						} else {
							if (Controlli.soloTirocini(vettMovAperti)) {
								dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
								nuovoStatoOccupazionale.setChanged(true);
							}
						}
					} else if ((statoOccupazionaleIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.B1)
							|| (statoOccupazionaleIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.B3)) {
						// B1 o B3
						vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(), dataRif);
						if (vettMovAperti.size() == 0) {
							nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
							nuovoStatoOccupazionale.setChanged(true);
							dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
						} else {
							// Se esiste mov a t.i. che implica precarietà e
							// viene inserito un movimento succ
							// a t.d con reddito alto, si ha sospensione
							// anzianità solo nel periodo di durata del mov a
							// t.d.
							// Se esiste sospensione anzianità e viene inserito
							// un movimento a t.i. nello stesso anno
							// con reddito basso, si esce dal 150 (situazione
							// non gestita in questo punto ma quando considera
							// l'avviamento a tempo indetermianto.)
							// Se nello stesso anno si hanno più rapporti a t.d.
							// la sospensione anzianità di uno
							// di essi implica la sospensione anzianità anche
							// nei periodi successivi.
							if (Controlli.soloTirocini(vettMovAperti)) {
								dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
								nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
								nuovoStatoOccupazionale.setChanged(true);
							} else {
								if (gestioneDecreto150 && !iscrittoCM) {
									if (statoOccupazionaleIniziale
											.getStatoOccupazionale() == StatoOccupazionaleBean.B3) {
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
										nuovoStatoOccupazionale.setChanged(true);
									}
								} else {
									if (!Controlli.sospensioneAnzianita(movimentiAperti, dataRif,
											sitAmm.getListaMobilita())) {
										dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A212);
										nuovoStatoOccupazionale.setChanged(true);
									} else {
										// Arrivo con lo stato occ. Disoccuopato: Occupato a rischio disoccupazione solo
										// quando in presenza di una
										// DID esiste un solo movimento a TI aperto con superamento reddito e nella DID
										// è stato indicato il flag rischio disoccupazione
										if (statoOccupazionaleIniziale
												.getStatoOccupazionale() == StatoOccupazionaleBean.B3) {
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
											nuovoStatoOccupazionale.setChanged(true);
										}
									}
								}
							}
						}
					} else {
						_logger.debug(
								"StatoOccupazionaleManager2.cessazione(): presenza di movimenti non impattanti - stato occupazionale iniziale non gestito nei diagrammi standard");

					}
					if (nuovoStatoOccupazionale.ischanged() || nuovoStatoOccupazionale.virtuale()) {
						/**
						 * dalla correzione del diagramma non e' piu' necessario fare il controllo della did e del patto
						 * aperti con relativo messaggio
						 */
						nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						nuovoStatoOccupazionale.setPensionato(statoOccupazionaleIniziale.getPensionato());
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, DateUtils.giornoSuccessivo(dataFine),
								requestContainer, txExecutor);
					}
				}
			} // end if(cessazione.getCollegato()!=null)
			break;
		case StatoOccupazionaleBean.RAGG_I:
			if (cessazione.getCollegato() != null) {
				// Movimento collegato
				_logger.debug(
						"StatoOccupazionaleManager.cessazione(): presenza di movimenti non impattanti - stato occupazionale iniziale non gestito nei diagrammi standard");

			} // end if(cessazione.getCollegato()!=null)
			if (movimento != null) { // movimento = null per le cessazioni
										// orfane
				vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(), dataInizio);
			} else {
				vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, dataInizio);
			}
			if (statoOccupazionaleIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.A22) {
				if (vettMovAperti.size() == 0) {
					nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
					nuovoStatoOccupazionale.setStatoOccupazionaleRagg("I");
					nuovoStatoOccupazionale.setStatoOccupazionale("A22");
					nuovoStatoOccupazionale.setChanged(true);
					nuovoStatoOccupazionale.setCodMonoProvenienza("M");
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, DateUtils.giornoSuccessivo(dataInizio),
							requestContainer, txExecutor);
				} else {
					nuovoStatoOccupazionale = statoOccupazionaleIniziale;
					nuovoStatoOccupazionale.setChanged(false);
				}
			}
			break;

		case StatoOccupazionaleBean.RAGG_A:
			codMotivoFine = cessazione.containsAttribute("CODMVCESSAZIONE")
					? cessazione.getAttribute("CODMVCESSAZIONE").toString()
					: "";
			if (movimento != null) { // movimento = null per le cessazioni
										// orfane
				vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(), dataInizio);
			} else {
				vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, dataInizio);
			}
			// controllo se il motivo della cessazione è "sospeso dal lavoro"
			// per gestire la creazione
			// dello stato occupazionale Occ: Sospeso per contrazione d'attività
			if (codMotivoFine.equals("SC") && !gestioneDecreto150) {
				// controlla se ci sono movimenti aperti
				dataFine = dataInizio;
				if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
					// Il nuovo stato occupazionale è di Sospeso per contrazione d'attività
					nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
					nuovoStatoOccupazionale.setStatoOccupazionaleRagg("O");
					nuovoStatoOccupazionale.setStatoOccupazionale("A0");
					nuovoStatoOccupazionale.setChanged(true);
					nuovoStatoOccupazionale.setCodMonoProvenienza("M");
				} else {
					// Lo stato occupazionale è 'Occupato'
					nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
					nuovoStatoOccupazionale.setStatoOccupazionaleRagg("O");
					nuovoStatoOccupazionale.setStatoOccupazionale("B");
					nuovoStatoOccupazionale.setChanged(true);
					nuovoStatoOccupazionale.setCodMonoProvenienza("M");
					sitAmm.addWarning(MessageCodes.StatoOccupazionale.STATO_SOSPESO_PER_CONTRAZIONE_NON_GENERATO);
				}
				DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, DateUtils.giornoSuccessivo(dataFine),
						requestContainer, txExecutor);
			} else {
				if (cessazione.getCollegato() == null) {
					// Movimento non collegato
					// Controllo se esistoni altri movimenti aperti
					vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, dataInizio);
					dataFine = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
					if (vettMovAperti.size() > 0 && !Controlli.soloTirocini(vettMovAperti)) {
						// Lo stato occupazionale è 'Occupato'
						nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
						nuovoStatoOccupazionale.setStatoOccupazionaleRagg("O");
						nuovoStatoOccupazionale.setStatoOccupazionale("B");
						nuovoStatoOccupazionale.setChanged(true);
						nuovoStatoOccupazionale.setCodMonoProvenienza("M");
					} else {
						if (statoOccupazionaleIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.C0) {
							// Lo stato occupazionale è 'Cessato non rientrato'
							nuovoStatoOccupazionale.setCdnLavoratore(cdnLavoratore);
							nuovoStatoOccupazionale.setStatoOccupazionaleRagg("A");
							nuovoStatoOccupazionale.setStatoOccupazionale("C0");
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							nuovoStatoOccupazionale.setPensionato(statoOccupazionaleIniziale.getPensionato());
						} else {
							nuovoStatoOccupazionale = statoOccupazionaleIniziale;
							nuovoStatoOccupazionale.setChanged(false);
						}
					}
					if (nuovoStatoOccupazionale.ischanged())
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, DateUtils.giornoSuccessivo(dataFine),
								requestContainer, txExecutor);
				} // if(cessazione.getCollegato()==null)
				else {
					// se la cessazione si riferisce ad un movimento di
					// avviamento Z.09.02(vecchio codice RS3) (cessazione
					// attività dopo la sospensione per contrazione, non devo
					// considerarlo perché già
					// considerato come avviamento (tipo di movimento C))
					codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
							? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
							: "";
					if (!codTipoAvviamento.equals("Z.09.02")) {
						switch (statoOccupazionaleIniziale.getStatoOccupazionale()) {
						case StatoOccupazionaleBean.C1:
						case StatoOccupazionaleBean.C11:
						case StatoOccupazionaleBean.C12:
						case StatoOccupazionaleBean.C13:
							vettMovAperti = Controlli.listaMovimentiAperti(movimentiAperti, movimento.getSource(),
									dataRif);
							if (vettMovAperti.size() == 0 || Controlli.soloTirocini(vettMovAperti)) {
								nuovoStatoOccupazionale = statoOccupazionaleIniziale;
								nuovoStatoOccupazionale.setChanged(false);
							} else {
								if (nuovoStatoOccupazionale.virtuale()) {
									nuovoStatoOccupazionale.setChanged(true);
									nuovoStatoOccupazionale.setCodMonoProvenienza("M");
									nuovoStatoOccupazionale.setPensionato(statoOccupazionaleIniziale.getPensionato());
									DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale,
											DateUtils.giornoSuccessivo(dataFine), requestContainer, txExecutor);
								}
							}

							break;

						case StatoOccupazionaleBean.C:

							// punto aperto
						case StatoOccupazionaleBean.E:
						case StatoOccupazionaleBean.D:
							// punto aperto
						default:
							// !!!!!!!!!!!!!!!!!!!!!!!!
						}
					}
				} // end else
			}
			break;
		}

		return nuovoStatoOccupazionale;
	}

	public StatoOccupazionaleBean calcolaStatoOccInMobilita(MovimentoBean movimento,
			StatoOccupazionaleBean statoOccIniziale, Vector vettParametriRicostruzione, String dat150,
			Vector listaDisabiliCM) throws Exception {
		String dataInizio = "";
		String codMonoTipoAss = "";
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
				? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
				: "";
		String flgImpattiTirInMobEff = vettParametriRicostruzione.get(1).toString();
		dataInizio = movimento.getDataInizio();

		if (DateUtils.compare(dataInizio, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0) {
			if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.B2) {
				StatoOccupazionaleBean n = new StatoOccupazionaleBean();
				n.setCdnLavoratore(movimento.getCdnLavoratore());
				if (codMonoTipoAss.equalsIgnoreCase("T")) {
					if (flgImpattiTirInMobEff.equalsIgnoreCase("S")) {
						if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.A213) {
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("A213");
							n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
							n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
							n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
									txExecutor);
						} else {
							nuovoStatoOccupazionale = statoOccIniziale;
						}
					} else {
						nuovoStatoOccupazionale = statoOccIniziale;
					}
				} else {
					n.setStatoOccupazionaleRagg("D");
					n.setStatoOccupazionale("B2");
					n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
					n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
					n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
					nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
					nuovoStatoOccupazionale.setChanged(true);
					nuovoStatoOccupazionale.setCodMonoProvenienza("M");
					DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer, txExecutor);
				}
			} else {
				nuovoStatoOccupazionale = statoOccIniziale;
			}
		} else { // else if (DateUtils.compare(dataInizio, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0)
			if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.B1
					&& statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.A212) {
				if (codMonoTipoAss.equalsIgnoreCase("T")) {
					if (flgImpattiTirInMobEff.equalsIgnoreCase("S")) {
						if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.A213) {
							StatoOccupazionaleBean n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(movimento.getCdnLavoratore());
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("A213");
							n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
							n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
							n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
									txExecutor);
						} else {
							nuovoStatoOccupazionale = statoOccIniziale;
						}
					} else {
						nuovoStatoOccupazionale = statoOccIniziale;
					}
				} else {
					if (movimento.containsAttribute(Contratto.FIELD_FLG_SOSPENSIONE)) {
						if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.B1) {
							StatoOccupazionaleBean n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(movimento.getCdnLavoratore());
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("B1");
							n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
							n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
							n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
									txExecutor);
						} else {
							nuovoStatoOccupazionale = statoOccIniziale;
						}
					} else {
						if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.A212) {
							StatoOccupazionaleBean n = new StatoOccupazionaleBean();
							n.setCdnLavoratore(movimento.getCdnLavoratore());
							n.setStatoOccupazionaleRagg("D");
							n.setStatoOccupazionale("A212");
							n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
							n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
							n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
							nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
							nuovoStatoOccupazionale.setChanged(true);
							nuovoStatoOccupazionale.setCodMonoProvenienza("M");
							DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer,
									txExecutor);
						} else {
							nuovoStatoOccupazionale = statoOccIniziale;
						}
					}
				}
			} else {
				if (movimento.containsAttribute(Contratto.FIELD_FLG_SOSPENSIONE)) {
					if (statoOccIniziale.getStatoOccupazionale() != StatoOccupazionaleBean.B1) {
						StatoOccupazionaleBean n = new StatoOccupazionaleBean();
						n.setCdnLavoratore(movimento.getCdnLavoratore());
						n.setStatoOccupazionaleRagg("D");
						n.setStatoOccupazionale("B1");
						n.setDataAnzianita(statoOccIniziale.getDataAnzianita());
						n.setDataCalcoloAnzianita(statoOccIniziale.getDataCalcoloAnzianita());
						n.setDataCalcoloMesiSosp(statoOccIniziale.getDataCalcoloMesiSosp());
						nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
						nuovoStatoOccupazionale.setChanged(true);
						nuovoStatoOccupazionale.setCodMonoProvenienza("M");
						DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer, txExecutor);
					} else {
						nuovoStatoOccupazionale = statoOccIniziale;
					}
				}
				// statoOccIniziale.getStatoOccupazionale() = StatoOccupazionaleBean.A212
				else {
					nuovoStatoOccupazionale = statoOccIniziale;
				}
			}
		}

		return nuovoStatoOccupazionale;
	}

	public StatoOccupazionaleBean statoOccupatoDecadenzaReddito(SituazioneAmministrativa sitAmm, BigDecimal cdnlav,
			StatoOccupazionaleBean statoOccIniziale, String dataInizio) throws Exception {
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		StatoOccupazionaleBean n = new StatoOccupazionaleBean();
		n.setCdnLavoratore(cdnlav);
		n.setStatoOccupazionaleRagg("O");
		n.setStatoOccupazionale("B");
		nuovoStatoOccupazionale = new StatoOccupazionaleBean(n, statoOccIniziale);
		nuovoStatoOccupazionale.setChanged(true);
		nuovoStatoOccupazionale.setCodMonoProvenienza("M");
		nuovoStatoOccupazionale.setPensionato(statoOccIniziale.getPensionato());
		DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataInizio, requestContainer, txExecutor);

		String dataChiusura = DateUtils.giornoPrecedente(dataInizio);
		SourceBean pattoAperto = this.sitAmm.cercaPatto(dataInizio);
		SourceBean didAperta = this.sitAmm.cercaDid(dataInizio);

		// seleziona il patto 'aperto'
		if (didAperta != null) {
			String dataDichiarazione = (String) didAperta.getAttribute("datDichiarazione");
			if (dataDichiarazione != null && !dataDichiarazione.equals("")
					&& DateUtils.compare(dataDichiarazione, dataChiusura) > 0) {
				dataChiusura = dataDichiarazione;
			}
			if (!didAperta.containsAttribute("codMotivoFineAtto")
					|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("")
					|| didAperta.getAttribute("codMotivoFineAtto").toString().equalsIgnoreCase("AV")) {
				DBStore.chiudiDID(didAperta, dataChiusura, "AV", requestContainer, txExecutor);
				DidBean didApertaBean = new DidBean(didAperta);
				sitAmm.chiudiDidInVettoreDids(didApertaBean.getPrgDichDisponibilita(), dataChiusura);
				List movimentiAmm = sitAmm.getMovimenti();
				sitAmm.aggiornaNumKloDichDispoInMovimenti(didApertaBean,
						new BigDecimal(didApertaBean.getAttribute("numKloDichDisp").toString()), movimentiAmm);
			} else {
				StatoOccupazionaleBean.addWarning(MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO, requestContainer);
			}
		}
		if (pattoAperto != null) {
			if (!pattoAperto.containsAttribute("codMotivoFineAtto")
					|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("")
					|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equalsIgnoreCase("AV")) {
				DBStore.chiudiPatto297(pattoAperto, dataChiusura, "AV", requestContainer, txExecutor);
				sitAmm.aggiornaNumKloPatto(new BigDecimal(pattoAperto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
						dataChiusura, new BigDecimal(pattoAperto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
			}
		}
		return nuovoStatoOccupazionale;
	}

	public TransactionQueryExecutor getTxExecutor() {
		return this.txExecutor;
	}

}