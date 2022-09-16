package it.eng.sil.util.amministrazione.impatti;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.MovimentoNonCollegatoException;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.amministrazione.UtilsMobilita;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.processors.Warning;
import it.eng.sil.module.patto.bean.Patto;
import it.eng.sil.util.Sottosistema;

public class SituazioneAmministrativa {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SituazioneAmministrativa.class.getName());
	/**
	 * Lista di oggetti MovimentoAmministrativo, ovvero MovimentoBean e DidBean, che intercorrono nella ricostruzione
	 * della storia
	 */
	private List movimenti;
	/**
	 * Vettore di oggetti StatoOccupazionaleBean: si tratta di tutti gli stati occupazionali presenti sul db prima della
	 * ricostruzione della storia.
	 */
	private Vector statiOccupazionali;
	/**
	 * vettore degli stati occupazionali finali del lavoratore dopo la ricostruzione storia
	 */
	private Vector statiOccupazionaliFinali = new Vector();
	private TransactionQueryExecutor txExecutor;
	private RequestContainer requestContainer;
	/**
	 * Stato occupazionale aperto alla fine della ricostruzione
	 */
	private StatoOccupazionaleBean statoOccupazionaleAperto;
	/**
	 * Stato occupazionale legato al movimento che ha prodotto la ricostruzione della storia
	 */
	private StatoOccupazionaleBean statoOccupazionaleMovimentoGestito;
	/**
	 * Lista degli di oggetti StatoOccupazionaleBean rappresentanti gli stati occupazionali creati nella ricostruzione
	 * della storia.
	 */
	private List statiOccupazionaliCreati = new ArrayList();
	private List patti;
	private List dids = new ArrayList();
	// contiene la lista delle mobilità aggiornate dalla procedura di allineamento(scorrimento e decadenza)
	private List listaMobilita = new ArrayList();
	// contiene la lista delle mobilità con i dati letti dal db e non aggiornati
	// rispetto alla fase di allineamento
	private List listaMobilitaDB = new ArrayList();
	private EventoAmministrativo movimentoGestito;
	private List messaggi = new ArrayList();
	private ListaStatiOccupazionali lso;
	private SourceBean statoOccupazionaleApertoOld;
	private Object cdnLavoratoreSitAmm = null;
	private BigDecimal prgMobilitaRicalcolo = null;
	private CmBean cm;
	private Vector listaDisabiliCM = new Vector();
	private List listaMovimentiIntermittenti = new ArrayList();

	/**
	 * Lista di oggetti StatoOccupazionaleBean cancellati prima di iniziare la ricostruzione della storia
	 */
	private List statiOccupazionaliCancellati = new ArrayList();
	/**
	 * se true alcuni controlli bloccanti vengono aggirati, come nel caso della did
	 */
	private boolean forzaRicostruzione;
	private boolean continuaRicalcolo;

	private String tipoCongif_MOV_C03 = "0";

	/**
	 * File di log dove segnalare eventuali forzature in ricostruzione storia nel lancio dei batch
	 */
	private LogBatch logFileBatch = null;
	private int indiceMovimentoPrec = -1;

	private String dataPrec297 = EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
	private int annoPrec297 = EventoAmministrativo.ANNO_DATA_NORMATIVA_DEFAULT;

	public SourceBean sbGenerale = null;

	private List<StatoOccupazionaleBean> statiOccupazionaliManuali = new ArrayList<StatoOccupazionaleBean>();

	private String prgStatoOccMovNonCollegato = "";

	private String dataEventoAmministrativo = null;

	private String data150 = null;

	public void setPrgStatoOccMovNonCollegato(String _prg) {
		prgStatoOccMovNonCollegato = _prg;
	}

	public void setStatoOccupazionaleAperto(StatoOccupazionaleBean nuovoStatoOcc) {
		this.statoOccupazionaleAperto = nuovoStatoOcc;
	}

	public void setPrgMobilitaRicalcolo(BigDecimal prg) {
		this.prgMobilitaRicalcolo = prg;
	}

	public void setListaMobilita(Vector mob) {
		this.listaMobilita = mob;
	}

	public void setListaMobilitaDB(Vector mob) {
		this.listaMobilitaDB = mob;
	}

	public String getPrgStatoOccMovNonCollegato() {
		return prgStatoOccMovNonCollegato;
	}

	public List getMovimenti() {
		return this.movimenti;
	}

	public List getStatiOccupazionali() {
		return this.statiOccupazionali;
	}

	public List getListaMobilita() {
		return this.listaMobilita;
	}

	public List getListaMobilitaDB() {
		return this.listaMobilitaDB;
	}

	public BigDecimal getPrgMobilitaRicalcolo() {
		return this.prgMobilitaRicalcolo;
	}

	public void setCm(CmBean cm) {
		this.cm = cm;
	}

	public CmBean getCm() {
		return this.cm;
	}

	public void setListaDisabiliCM(Vector cm) {
		this.listaDisabiliCM = cm;
	}

	public Vector getListaDisabiliCM() {
		return this.listaDisabiliCM;
	}

	/**
	 * costruttore della classe che rappresenta la situazione amministrativa del lavoratore (movimenti, dids, patti,
	 * lso)
	 * 
	 * @param rows
	 * @param statiOcc
	 * @param patti
	 * @param dids
	 * @param txExecutor
	 * @param requestContainer
	 * @throws ControlliException
	 * @throws Exception
	 */
	public SituazioneAmministrativa(Vector rows, Vector statiOcc, Vector patti, Vector dids, String dataInizioEvento,
			TransactionQueryExecutor txExecutor, RequestContainer requestContainer)
			throws ControlliException, Exception {
		this.txExecutor = txExecutor;
		this.requestContainer = requestContainer;
		this.patti = patti;
		if (requestContainer.getServiceRequest().containsAttribute("FORZA_INSERIMENTO")
				&& requestContainer.getServiceRequest().getAttribute("FORZA_INSERIMENTO").equals("true")) {
			setForzaRicostruzione(true);
		}
		if (requestContainer.getServiceRequest().containsAttribute("CONTINUA_CALCOLO_SOCC")
				&& requestContainer.getServiceRequest().getAttribute("CONTINUA_CALCOLO_SOCC").equals("true")) {
			continuaRicalcolo = true;
		}
		this.statoOccupazionaleApertoOld = statiOcc.size() > 0
				? (SourceBean) ((SourceBean) statiOcc.lastElement()).cloneObject()
				: null;

		// toglie le did annullate
		dids = togliEventiAnnullati(dids);

		// Gestione eventuale mobilità del lavoratore; bisogna recuperare il cdnlavoratore.
		// Il recupero va fatto nella lista dei movimenti(rows), oppure nella lista degli
		// stati occupazionali, oppure nella lista delle did. La gestione della
		// mobilità viene fatta a questo livello per evitare di modificare
		// tutte le chiamate al costruttore della classe.
		Object cdnLavoratore = null;
		SourceBean sb = null;
		if (rows.size() > 0) {
			sb = (SourceBean) rows.get(0);
			cdnLavoratore = sb.getAttribute("cdnlavoratore");
		}

		if (cdnLavoratore == null) {
			if (statiOcc.size() > 0) {
				sb = (SourceBean) statiOcc.get(0);
				cdnLavoratore = sb.getAttribute("cdnlavoratore");
			}
		}

		if (cdnLavoratore == null) {
			if (dids.size() > 0) {
				sb = (SourceBean) dids.get(0);
				cdnLavoratore = sb.getAttribute("cdnlavoratore");
			}
		}

		if (cdnLavoratore == null) {
			cdnLavoratore = requestContainer.getServiceRequest().getAttribute("cdnlavoratore");
		}
		this.cdnLavoratoreSitAmm = cdnLavoratore;
		this.sbGenerale = DBLoad.getInfoGenerali();
		setDataEventoAmministrativo(dataInizioEvento);
		setData150((this.sbGenerale.getAttribute("DAT150") != null ? this.sbGenerale.getAttribute("DAT150").toString()
				: null));
		Vector rowsMobilita = null;
		if (cdnLavoratore == null) {
			rowsMobilita = new Vector();
		} else {
			ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, txExecutor);
			rowsMobilita = mobilita.getMobilita();

		}
		setListaMobilita(rowsMobilita);
		Vector rowsMobilitaApp = new Vector();
		for (int cont = 0; cont < rowsMobilita.size(); cont++) {
			rowsMobilitaApp.add(rowsMobilita.get(cont));
		}
		setListaMobilitaDB(rowsMobilitaApp);
		creaBeans(rows, statiOcc, patti, dids, rowsMobilita);
	}

	public SituazioneAmministrativa(Vector rows, Vector statiOcc, Vector patti, Vector dids, String dataInizioEvento,
			TransactionQueryExecutor txExecutor, RequestContainer requestContainer, LogBatch logBatch)
			throws ControlliException, Exception {
		this.txExecutor = txExecutor;
		this.requestContainer = requestContainer;
		this.patti = patti;
		this.logFileBatch = logBatch;
		if (requestContainer.getServiceRequest().containsAttribute("FORZA_INSERIMENTO")
				&& requestContainer.getServiceRequest().getAttribute("FORZA_INSERIMENTO").equals("true")) {
			setForzaRicostruzione(true);
		}
		if (requestContainer.getServiceRequest().containsAttribute("CONTINUA_CALCOLO_SOCC")
				&& requestContainer.getServiceRequest().getAttribute("CONTINUA_CALCOLO_SOCC").equals("true")) {
			continuaRicalcolo = true;
		}
		this.statoOccupazionaleApertoOld = statiOcc.size() > 0
				? (SourceBean) ((SourceBean) statiOcc.lastElement()).cloneObject()
				: null;

		// toglie le did annullate
		dids = togliEventiAnnullati(dids);

		// Gestione eventuale mobilità del lavoratore; bisogna recuperare il cdnlavoratore.
		// Il recupero va fatto nella lista dei movimenti(rows), oppure nella lista degli
		// stati occupazionali, oppure nella lista delle did. La gestione della
		// mobilità viene fatta a questo livello per evitare di modificare
		// tutte le chiamate al costruttore della classe.
		Object cdnLavoratore = null;
		SourceBean sb = null;
		if (rows.size() > 0) {
			sb = (SourceBean) rows.get(0);
			cdnLavoratore = sb.getAttribute("cdnlavoratore");
		}

		if (cdnLavoratore == null) {
			if (statiOcc.size() > 0) {
				sb = (SourceBean) statiOcc.get(0);
				cdnLavoratore = sb.getAttribute("cdnlavoratore");
			}
		}

		if (cdnLavoratore == null) {
			if (dids.size() > 0) {
				sb = (SourceBean) dids.get(0);
				cdnLavoratore = sb.getAttribute("cdnlavoratore");
			}
		}

		if (cdnLavoratore == null) {
			cdnLavoratore = requestContainer.getServiceRequest().getAttribute("cdnlavoratore");
		}
		this.cdnLavoratoreSitAmm = cdnLavoratore;
		this.sbGenerale = DBLoad.getInfoGenerali();
		setDataEventoAmministrativo(dataInizioEvento);
		setData150((this.sbGenerale.getAttribute("DAT150") != null ? this.sbGenerale.getAttribute("DAT150").toString()
				: null));
		Vector rowsMobilita = null;
		if (cdnLavoratore == null) {
			rowsMobilita = new Vector();
		} else {
			ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, txExecutor);
			rowsMobilita = mobilita.getMobilita();

		}
		setListaMobilita(rowsMobilita);
		Vector rowsMobilitaApp = new Vector();
		for (int cont = 0; cont < rowsMobilita.size(); cont++) {
			rowsMobilitaApp.add(rowsMobilita.get(cont));
		}
		setListaMobilitaDB(rowsMobilitaApp);
		creaBeans(rows, statiOcc, patti, dids, rowsMobilita);
	}

	/**
	 * 
	 * @param rows
	 * @param statiOcc
	 * @param patti
	 * @param dids
	 * @throws ControlliException
	 * @throws Exception
	 */
	private void creaBeans(Vector rows, Vector statiOcc, Vector patti, Vector dids, Vector rowsMobilita)
			throws ControlliException, Exception {
		StatoOccupazionaleBean sob = null;
		StatoOccupazionaleBean sobc = null;
		// Lettura configurazione(Default "0" o Custom "1") MOV_C03 (codTipoContratto = "C.03.00")
		// Nel caso la configurazione sia Custom, questi movimenti non devono
		// risultare impattanti nel calcolo dello stato occupazionale.
		// Dalla versione 2.23.0 i movimenti con codTipoContratto = "C.03.00" (LSU) sono trattati come i tirocini
		UtilsConfig utility = new UtilsConfig("MOV_C03");
		String tipoConfig = utility.getConfigurazioneDefault_Custom();
		setTipoCongif_MOV_C03(tipoConfig);

		utility = new UtilsConfig("AM_297");
		String dataConfig297 = utility.getValoreConfigurazione();
		setDataPrec297(dataConfig297);
		setAnnoPrec297(DateUtils.getAnno(dataConfig297));

		movimenti = new ArrayList(rows.size());
		statiOccupazionali = listaStatiOccupazionali(statiOcc, dids, patti);
		SourceBean so = null;
		// e' la lista degli avviamenti terminati ma senza movimento di cessazione
		// andranno inseriti nella lista dei movimenti
		ArrayList movimentiInseriti = new ArrayList();
		ArrayList didInserite = new ArrayList(dids.size());
		// carico in un vettore il valore del flag FLGMOBILITARIMANEAPERTA
		// associata al motivo di cessazione del movimento
		Vector rowsMvCessazione = DBLoad.getMotivoCessazione();
		String dataOggi = DateUtils.getNow();
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");

		Vector vettCM = DBLoad.getAllDisabiliCollocamentoMirato(cdnLavoratoreSitAmm, txExecutor);
		if (vettCM != null && vettCM.size() > 0) {
			// prendo il primo della lista che corrisponde all'ultima iscrizione inserita
			SourceBean rowCM = (SourceBean) vettCM.get(0);
			rowCM = rowCM.containsAttribute("ROW") ? (SourceBean) rowCM.getAttribute("ROW") : rowCM;
			cm = new CmBean(rowCM);
			this.setCm(cm);
			setListaDisabiliCM(vettCM);
		} else {
			cm = new CmBean(new SourceBean("ROWS"));
			this.setCm(cm);
		}

		int j = 0;
		int rowsSize = rows.size();
		for (int i = 0; i < rowsSize; i++) {
			SourceBean m = (SourceBean) rows.get(i);
			boolean ret = m.containsAttribute("FLAG_IN_INSERIMENTO");
			// escludo i movimenti che non sono protocollati (a meno che il movimento
			// non è in inserimento; in tal caso risulta essere in attesa di essere
			// protocollato e va considerato).
			if (!ret) {
				if (m.containsAttribute("codStatoAtto") && !m.getAttribute("codStatoAtto").equals("PR")) {
					continue;
				}
			}
			for (int t = 0; t < didInserite.size(); t++) {
				DidBean did = (DidBean) didInserite.get(t);
				String dataFine = (String) did.getAttribute("datFine");
				String codStatoAtto = (String) did.getAttribute("codStatoAtto");
				if (dataFine == null || codStatoAtto.equals("PA"))
					continue;
				String codMotivoFine = (String) did.getAttribute("codMotivoFineAtto");
				if (codMotivoFine == null || codMotivoFine.equals("AV"))
					continue;
				String dataInizioMovimento = (String) m.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				if (dataFine != null && !dataFine.equals("") && DateUtils.compare(dataFine, dataInizioMovimento) >= 0) {
					ChiusuraDidBean chiusuraDid = new ChiusuraDidBean(did);
					movimenti.add(chiusuraDid);
					didInserite.remove(t);
					t--;
				}

			}
			// se corrispondono inserisci le did chiuse
			for (; j < dids.size(); j++) {
				SourceBean did = (SourceBean) dids.get(j);
				String dataInizioMov = (String) m.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				String dataDichiarazione = (String) did.getAttribute("datDichiarazione");
				if (DateUtils.compare(dataInizioMov, dataDichiarazione) > 0) {
					DidBean didBean = new DidBean(did);
					sob = cercaStatoOccBean(statiOccupazionali, did);
					if (sob == null) {
						SourceBean sb = new SourceBean("ROW");
						sb.setAttribute("codStatoOccupaz", "C"); // altro
						sb.setAttribute("prgStatoOccupaz", new BigDecimal(-1));
						sb.setAttribute("cdnLavoratore", did.getAttribute("cdnLavoratore"));
						sob = new StatoOccupazionaleBean(sb, null, null, statiOccupazionali);
					}
					didBean.setStatoOccupazionale(sob);
					movimenti.add(didBean);
					didInserite.add(didBean);
					this.dids.add(didBean);
				} else {
					break;
				}
			}

			switch (MovimentoBean.getTipoMovimento(m)) {
			case MovimentoBean.ASSUNZIONE:
				// gestione periodi intermittenti in ricalcolo
				boolean escludiMovIntermittente = false;
				boolean addMovimentoInizialeIntermittente = false;
				int numGiorniIntermittenti = 0;
				String dataFineSosp = null;
				String dataFinePeriodoIntermittente = null;
				String dataInizioMov = m.containsAttribute(MovimentoBean.DB_DATA_INIZIO)
						? m.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()
						: "";
				String codContratto = m.containsAttribute(MovimentoBean.DB_COD_CONTRATTO)
						? m.getAttribute(MovimentoBean.DB_COD_CONTRATTO).toString()
						: "";
				BigDecimal prgMovimento = m.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						? (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						: null;
				if (prgMovimento != null && !prgMovimento.toString().equals("-1")
						&& codContratto.equalsIgnoreCase(Properties.CONTRATTO_LAVORO_INTERMITTENTE)
						&& DateUtils.compare(dataInizioMov, getData150()) >= 0) {
					Vector periodi = DBLoad.getPeriodiIntermittenti(prgMovimento, txExecutor);
					int nsize = periodi.size();
					for (int p = 0; p < nsize; p++) {
						SourceBean periodo = (SourceBean) periodi.get(p);
						periodo = periodo.containsAttribute("ROW") ? (SourceBean) periodo.getAttribute("ROW") : periodo;
						if (periodo.containsAttribute("VALIDO")
								&& periodo.getAttribute("VALIDO").toString().equalsIgnoreCase("S")) {
							if (periodo.containsAttribute("GIORNI")) {
								numGiorniIntermittenti = numGiorniIntermittenti
										+ ((new Integer(periodo.getAttribute("GIORNI").toString())).intValue());
							}
							dataFinePeriodoIntermittente = periodo.containsAttribute("DATAFINE")
									? periodo.getAttribute("DATAFINE").toString()
									: null;
						}
					}
					if (numGiorniIntermittenti > Properties.GIORNI_SOSP_DECRETO150
							&& dataFinePeriodoIntermittente != null) {
						if (DateUtils.compare(dataFinePeriodoIntermittente, dataOggi) >= 0) {
							int ggDiffSosp = numGiorniIntermittenti - Properties.GIORNI_SOSP_DECRETO150;
							dataFineSosp = DateUtils.giornoSuccessivo(
									DateUtils.aggiungiNumeroGiorni(dataFinePeriodoIntermittente, -(ggDiffSosp)));
							if (DateUtils.compare(dataFineSosp, dataOggi) <= 0) {
								dataFineSosp = DateUtils.giornoSuccessivo(dataFinePeriodoIntermittente);
								addMovimentoInizialeIntermittente = true;
							} else {
								int ggDiffSospOggi = DateUtils.daysBetween(dataOggi, dataFineSosp);
								numGiorniIntermittenti = Properties.GIORNI_SOSP_DECRETO150 - ggDiffSospOggi;
							}
						} else {
							dataFineSosp = DateUtils.giornoSuccessivo(dataFinePeriodoIntermittente);
							addMovimentoInizialeIntermittente = true;
						}
					} else {
						if (dataFinePeriodoIntermittente != null) {
							dataFineSosp = DateUtils.giornoSuccessivo(dataFinePeriodoIntermittente);
						}
					}

					if (addMovimentoInizialeIntermittente) {
						// Gestione decadenza inizio rapporto per intermittenti che superano i 180 giorni
						escludiMovIntermittente = false;
					} else {
						for (int p = 0; p < nsize; p++) {
							SourceBean periodo = (SourceBean) periodi.get(p);
							periodo = periodo.containsAttribute("ROW") ? (SourceBean) periodo.getAttribute("ROW")
									: periodo;
							if (periodo.containsAttribute("VALIDO")
									&& periodo.getAttribute("VALIDO").toString().equalsIgnoreCase("S")) {
								BigDecimal prgPeriodoLav = (BigDecimal) periodo.getAttribute("PRGPERIODOLAV");
								MovimentoBean movInterCurr = new MovimentoBean(periodo);
								escludiMovIntermittente = true;
								movInterCurr.setAttribute("NUMGGINTERMITTENTE", numGiorniIntermittenti);
								//
								String dataInizioPeriodo = movInterCurr.containsAttribute("DATAINIZIO")
										? movInterCurr.getAttribute("DATAINIZIO").toString()
										: null;
								if (movInterCurr.containsAttribute("DATAINIZIO")) {
									movInterCurr.delAttribute("DATAINIZIO");
								}
								if (dataInizioPeriodo != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioPeriodo);
								}
								//
								String dataFinePeriodo = periodo.containsAttribute("DATAFINE")
										? periodo.getAttribute("DATAFINE").toString()
										: null;
								if (movInterCurr.containsAttribute("DATAFINE")) {
									movInterCurr.delAttribute("DATAFINE");
								}
								if (dataFinePeriodo != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_DATA_FINE, dataFinePeriodo);
									movInterCurr.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataFinePeriodo);
									movInterCurr.setAttribute(MovimentoBean.DB_COD_MONO_TEMPO,
											CodMonoTempoEnum.DETERMINATO.getCodice());
								}
								//
								if (m.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_COD_ASSUNZIONE,
											m.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE));
								}
								//
								movInterCurr.setAttribute(MovimentoBean.DB_COD_STATO_ATTO, "PR");
								//
								if (m.getAttribute(MovimentoBean.DB_CDNLAVORATORE) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_CDNLAVORATORE,
											m.getAttribute(MovimentoBean.DB_CDNLAVORATORE));
								}
								//
								movInterCurr.delAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
								movInterCurr.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(-1));
								movInterCurr.setAttribute("PRGPERIODOINTERMITTENTE", prgPeriodoLav);
								//
								movInterCurr.setAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_ASSUNZIONE);
								//
								movInterCurr.setAttribute(MovimentoBean.DB_PRG_AZIENDA,
										m.getAttribute(MovimentoBean.DB_PRG_AZIENDA));
								//
								movInterCurr.setAttribute(MovimentoBean.DB_PRG_UNITA,
										m.getAttribute(MovimentoBean.DB_PRG_UNITA));
								//
								movInterCurr.setAttribute(MovimentoBean.DB_COD_CONTRATTO, codContratto);
								//
								if (m.getAttribute(MovimentoBean.DB_RETRIBUZIONE) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_RETRIBUZIONE,
											m.getAttribute(MovimentoBean.DB_RETRIBUZIONE));
								}
								//
								if (m.getAttribute(MovimentoBean.DB_COD_TIPO_DICH) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_COD_TIPO_DICH,
											m.getAttribute(MovimentoBean.DB_COD_TIPO_DICH));
								}
								if (m.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA,
											m.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA));
								}
								//
								if (m.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014,
											m.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014));
								}
								//
								if (m.getAttribute(MovimentoBean.DB_COD_ORARIO) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_COD_ORARIO,
											m.getAttribute(MovimentoBean.DB_COD_ORARIO));
								}
								if (m.getAttribute("NUMORESETT") != null) {
									movInterCurr.setAttribute("NUMORESETT", m.getAttribute("NUMORESETT"));
								}
								if (m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS) != null) {
									movInterCurr.setAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS,
											m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS));
								}
								movimenti.add(movInterCurr);
								movimentiInseriti.add(movInterCurr);
							}
						}
					}
				}

				MovimentoBean mb = null;
				String tipoFine = null;
				// escludiMovIntermittente = false quando a)non si tratta di un rapporto di lavoro intermittente,
				// b) quando la gestione dei rapporti di lavoro intermittenti prevede la decadenza dalla data inizio
				// rapporto
				// in quanto i periodi lavorativi superano i 180 giorni
				if (!escludiMovIntermittente) {
					if (m.containsAttribute(MovimentoBean.FLAG_IN_INSERIMENTO)
							|| m.containsAttribute(MovimentoBean.FLAG_VIRTUALE) || Controlli.movimentoInDataFutura(m)) {
						mb = new MovimentoBean(m);
					} else {
						so = cercaStatoOccMovimento(statiOcc, m);
						mb = new MovimentoBean(m);
						if (so != null) {
							sob = getStatoOccupazionale(statiOccupazionali, statiOcc, dids, patti, so);
							sobc = getStatoOccupazionaleCollegato(statiOccupazionali, statiOcc, dids, patti, so);
							sob.setStatoOccupazionaleBack(sobc);
							mb.setStatoOccupazionale(sob);
						}
					}

					movimenti.add(mb);

					tipoFine = mb.getCodMonoTipoFine();
					if (tipoFine == null && mb.getDataFineEffettiva() != null) {
						// debbo inserire il movimento nel vettore relativo
						movimentiInseriti.add(mb);
					}
				} else {
					// escludiMovIntermittente = true quando si tratta di un rapporto di lavoro intermittente con
					// periodi validi che non superano i 180 giorni
					// dataFinePeriodoIntermittente = data fine ultimo periodo intermittente
					// DEREFERENZIA MOVIMENTO DI ASSUNZIONE ORIGINALE E AGGIORNA LA DATA FINE SOSPENSIONE PER IL BATCH
					if (prgMovimento != null && !prgMovimento.toString().equals("-1")) {
						listaMovimentiIntermittenti.add(prgMovimento);
						DBStore.aggiornaMovimentoIntermittente(m, dataFineSosp, cdnUser, txExecutor);
					}
				}
				break;

			case MovimentoBean.CESSAZIONE:
				boolean escludiCessazione = false;
				MovimentoBean coll = null;
				BigDecimal prgMovimentoCessazione = m.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						? (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						: null;
				if (m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
					BigDecimal prgMovimentoPrec = (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
					if (listaMovimentiIntermittenti.contains(prgMovimentoPrec)) {
						escludiCessazione = true;
					}
				}
				if (!escludiCessazione) {
					so = cercaStatoOccMovimento(statiOcc, m);
					Cessazione cb = null;
					cb = new Cessazione(m);
					if (so != null && !(cb.inInserimento() || cb.virtuale())) {
						sob = getStatoOccupazionale(statiOccupazionali, statiOcc, dids, patti, so);
						sobc = getStatoOccupazionaleCollegato(statiOccupazionali, statiOcc, dids, patti, so);
						sob.setStatoOccupazionaleBack(sobc);
						cb.setStatoOccupazionale(sob);
					}
					try {
						coll = cercaMovimentoCollegato(movimenti, cb);
					} catch (MovimentoNonCollegatoException movNonColEx) {
						movimenti.add(cb);
						break;
					}
					if (coll == null)
						throw new ControlliException(MessageCodes.StatoOccupazionale.MOV_CESSATO_NON_TROVATO);
					cb.setMovimentoBack(coll);

					String codMotivoCessazione = cb.containsAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE)
							? cb.getAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE).toString()
							: "";
					// setto informazione flgMobilitaRimaneAperta nell'avviamento a seconda del motivo di cessazione.
					setInfoCessazioneInMovimento(codMotivoCessazione, rowsMvCessazione, coll);
					// DEVO SETTARE LA DATA FINE DEL MOVIMENTO PRECEDENTE (COLL) ANCHE IN CORRISPONDENZA
					// DEL VETTORE MOVIMENTI PROROGATI ASSOCIATO ALL'AVVIAMENTO A CUI E' COLLEGATA
					// LA TRASFORMAZIONE O LA PROROGA
					int tipoMovimento = coll.getTipoMovimento();
					if (tipoMovimento == MovimentoBean.PROROGA || tipoMovimento == MovimentoBean.TRASFORMAZIONE) {
						setMovimentoBackInAvv(cb, coll, movimenti);
					}
					movimenti.add(cb);
				} else {
					if (prgMovimentoCessazione != null && !prgMovimentoCessazione.toString().equals("-1")) {
						listaMovimentiIntermittenti.add(prgMovimentoCessazione);
						DBStore.aggiornaMovimentoIntermittente(m, null, cdnUser, txExecutor);
					}
				}
				break;

			case MovimentoBean.PROROGA:
				boolean escludiProroga = false;
				BigDecimal prgMovimentoProroga = m.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						? (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						: null;
				if (m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
					BigDecimal prgMovimentoPrec = (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
					if (listaMovimentiIntermittenti.contains(prgMovimentoPrec)) {
						escludiProroga = true;
					}
				}
				if (!escludiProroga) {
					so = cercaStatoOccMovimento(statiOcc, m);
					mb = null;
					mb = new MovimentoBean(m);
					if (so != null && !(mb.inInserimento() || mb.virtuale())) {
						sob = getStatoOccupazionale(statiOccupazionali, statiOcc, dids, patti, so);
						sobc = getStatoOccupazionaleCollegato(statiOccupazionali, statiOcc, dids, patti, so);
						sob.setStatoOccupazionaleBack(sobc);
						mb.setStatoOccupazionale(sob);
					}
					// i movimenti di proroga senza movimento collegato vengono inseriti nella
					// lista dei movimenti trattati dalla ricostruzione storia
					try {
						coll = cercaMovimentoCollegato(movimenti, mb);
					} catch (MovimentoNonCollegatoException me) {
						// throw new ControlliException(MessageCodes.StatoOccupazionale.PROROGA_NON_COLLEGATA);
						movimenti.add(mb);
						continue;
					}
					if (coll == null) {
						// throw new ControlliException(MessageCodes.StatoOccupazionale.MOV_CESSATO_NON_TROVATO);
						movimenti.add(mb);
						continue;
					}

					mb.setMovimentoBack(coll);
					// SETTO PER LA PROROGA IL FLAG NON IMPATTANTE NELLA
					// RICOSTRUZIONE DELLA STORIA (16/11/2004)
					mb.setAttribute("FLAG_NON_IMPATTANTE", "1");

					movimenti.add(mb);
					tipoFine = mb.getCodMonoTipoFine();
					if (tipoFine == null && mb.getDataFineEffettiva() != null) {
						// debbo inserire il movimento nel vettore relativo
						movimentiInseriti.add(mb);
					}
					MovimentoBean mTemp = mb;
					// risale all'avviamento di partenza e inserisce la proroga nel
					// vettore dei movimenti prorogati che fanno riferimento all'avviamento
					gesticiProrogheTrasformazioni(movimenti, mTemp, m);
				} else {
					if (prgMovimentoProroga != null && !prgMovimentoProroga.toString().equals("-1")) {
						listaMovimentiIntermittenti.add(prgMovimentoProroga);
						DBStore.aggiornaMovimentoIntermittente(m, null, cdnUser, txExecutor);
					}
				}
				break;

			case MovimentoBean.TRASFORMAZIONE:
				boolean escludiTra = false;
				BigDecimal prgMovimentoTra = m.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						? (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
						: null;
				if (m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
					BigDecimal prgMovimentoPrec = (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
					if (listaMovimentiIntermittenti.contains(prgMovimentoPrec)) {
						escludiTra = true;
					}
				}
				if (!escludiTra) {
					boolean bTraAddMovimenti = false;
					String codMonoTempo = (String) m.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO);
					so = cercaStatoOccMovimento(statiOcc, m);
					Trasformazione tb = new Trasformazione(m);
					if (so != null && !(tb.inInserimento() || tb.virtuale())) {
						sob = getStatoOccupazionale(statiOccupazionali, statiOcc, dids, patti, so);
						sobc = getStatoOccupazionaleCollegato(statiOccupazionali, statiOcc, dids, patti, so);
						sob.setStatoOccupazionaleBack(sobc);
						tb.setStatoOccupazionale(sob);
					}
					// i movimenti di trasformazione senza movimento collegato vengono inseriti nella
					// lista dei movimenti trattati dalla ricostruzione storia
					try {
						coll = cercaMovimentoCollegato(movimenti, tb);
					} catch (MovimentoNonCollegatoException movNonColEx) {
						movimenti.add(tb);
						bTraAddMovimenti = true;
						tipoFine = tb.getCodMonoTipoFine();
						if (tipoFine == null && tb.getDataFineEffettiva() != null) {
							// debbo inserire il movimento nel vettore relativo
							movimentiInseriti.add(tb);
						}
						break;
					}
					if (coll == null) {
						// throw new ControlliException(MessageCodes.StatoOccupazionale.MOV_TRASFORMATO_NON_TROVATO);
						if (!bTraAddMovimenti)
							movimenti.add(tb);
						continue;
					}
					tb.setMovimentoBack(coll);
					// SETTO PER LA TRASFORMAZIONE IL FLAG NON IMPATTANTE
					// NELLA RICOSTRUZIONE DELLA STORIA
					tb.setAttribute("FLAG_NON_IMPATTANTE", "1");

					movimenti.add(tb);
					tipoFine = tb.getCodMonoTipoFine();
					if (tipoFine == null && tb.getDataFineEffettiva() != null) {
						// debbo inserire il movimento nel vettore relativo
						movimentiInseriti.add(tb);
					}
					MovimentoBean mTempTra = tb;
					// risale all'avviamento di partenza e inserisce la trasformazione nel
					// vettore dei movimenti prorogati che fanno riferimento all'avviamento
					gesticiProrogheTrasformazioni(movimenti, mTempTra, m);
				} else {
					if (prgMovimentoTra != null && !prgMovimentoTra.toString().equals("-1")) {
						listaMovimentiIntermittenti.add(prgMovimentoTra);
						DBStore.aggiornaMovimentoIntermittente(m, null, cdnUser, txExecutor);
					}
				}
				break;
			}
			sob = null;
		}
		// se l'ultimo movimento era antecedente a/alle did, allora bisogna aggiungerle
		// prima aggiungo le eventuali did chiuse
		for (int t = 0; t < didInserite.size(); t++) {
			DidBean did = (DidBean) didInserite.get(t);
			String dataFine = (String) did.getAttribute("datFine");
			String codStatoAtto = (String) did.getAttribute("codStatoAtto");
			if (dataFine == null || codStatoAtto.equals("PA"))
				continue;
			String codMotivoFine = (String) did.getAttribute("codMotivoFineAtto");
			if (codMotivoFine == null || codMotivoFine.equals("AV"))
				continue;
			ChiusuraDidBean chiusuraDid = new ChiusuraDidBean(did);
			movimenti.add(chiusuraDid);
		}

		for (; j < dids.size(); j++) {
			DidBean didBean = new DidBean((SourceBean) dids.get(j));
			movimenti.add(didBean);
			this.dids.add(didBean);
			// controllo se la did e' stata chiusa
			// in questo caso aggiungo subito la sua chiusura
			String dataFine = (String) didBean.getAttribute("datFine");
			String codStatoAtto = (String) didBean.getAttribute("codStatoAtto");
			if (dataFine == null)
				continue;
			if (dataFine == null || codStatoAtto.equals("PA"))
				continue;
			String codMotivoFine = (String) didBean.getAttribute("codMotivoFineAtto");
			if (codMotivoFine == null || codMotivoFine.equals("AV"))
				continue;
			ChiusuraDidBean chiusuraDid = new ChiusuraDidBean(didBean);
			movimenti.add(chiusuraDid);

		}
		// --------------------------------------
		// ora debbo inserire le cessazioni virtuali nella lista dei movimenti
		// NB. Avro' sempre le did successive ai movimenti, se i giorni di inizio coincidono.
		// per cui se ho un movimento con data inizio = data fine e una did con data dichiarazione = data inizio,
		// debbo stare attento a posizionare la cessazione davanti al movimento terminato ma dietro la did
		// Comunque ci pensa il sort successivo a mettere in ordine i movimenti
		int i = 0;
		int t = 0;
		int ind = 0;
		for (; t < movimentiInseriti.size(); t++) {
			MovimentoBean mbDaCessare = (MovimentoBean) movimentiInseriti.get(t);
			// solo se non e' um movimento in gestione, ovvero un movimento di cui si sta gestendo la cessazione
			// inseriamo la cessazione virtuale nella lista dei movimenti impattanti lo stato occupazionale,
			// questo per evitare 'spiacevoli' doppioni!

			// 16/11/2004 non devo inserire le cessazioni di avviamenti che hanno delle proroghe
			// e cessazioni di proroghe che a loro volta sono prorogate

			if (mbDaCessare.getMovimentoNext() != null) {
				int tipoMovimento = mbDaCessare.getMovimentoNext().getTipoMovimento();
				if (tipoMovimento == MovimentoBean.CESSAZIONE || tipoMovimento == MovimentoBean.TRASFORMAZIONE
						|| tipoMovimento == MovimentoBean.PROROGA) {
					continue;
				}
			}

			SourceBean sb = new SourceBean("ROW");
			sb.setAttribute(MovimentoBean.DB_CDNLAVORATORE, mbDaCessare.getCdnLavoratore());
			sb.setAttribute(MovimentoBean.DB_COD_MOVIMENTO, MovimentoBean.COD_CESSAZIONE);
			sb.setAttribute(MovimentoBean.DB_DATA_INIZIO, mbDaCessare.getDataFineEffettiva());
			sb.setAttribute(MovimentoBean.DB_DATA_INIZIO_MOV_PREC, mbDaCessare.getDataInizio());
			sb.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC, "13214");
			if (!mbDaCessare.inInserimento() && !mbDaCessare.virtuale())
				sb.updAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC, mbDaCessare.getPrgMovimento());
			sb.setAttribute(MovimentoBean.FLAG_VIRTUALE, "0");
			Cessazione cb = new Cessazione(sb);
			cb.setMovimentoBack(mbDaCessare);
			movimenti.add(cb);
		}

		// GESTIONE EVENTUALE MOBILITA ED EVENTUALE CHIUSURAMOBILITA
		int rowsMobilitaSize = rowsMobilita.size();
		for (j = 0; j < rowsMobilitaSize; j++) {
			MobilitaBean mobilita = new MobilitaBean((SourceBean) rowsMobilita.get(j));
			movimenti.add(mobilita);
		}
		// Se una mobilità termina in una data D1, allora l'evento chiusura mobilità
		// viene creato il giorno successivo alla data di fine mobilità D1
		String dataEventoChiusuraMob = "";
		for (i = 0; i < rowsMobilitaSize; i++) {
			SourceBean sbMobilita = (SourceBean) rowsMobilita.get(i);
			MobilitaBean mobilita = new MobilitaBean(sbMobilita);
			String dataFine = mobilita.getDataFine();
			if (dataFine == null || dataFine.equals(""))
				continue;
			ChiusuraMobilitaBean chiusuraMobilita = new ChiusuraMobilitaBean(mobilita);
			dataEventoChiusuraMob = DateUtils.giornoSuccessivo(dataFine);
			chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO, dataEventoChiusuraMob);
			chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_FINE, dataEventoChiusuraMob);
			movimenti.add(chiusuraMobilita);
		}
		// guarda le implementazioni del metodo compareTo() delle classi MovimentoBean, DidBean, MobilitaBean
		Collections.sort(movimenti);
		// devo controllare se tra la data inizio mobilità e data fine mobilità esiste un movimento
		// a t.indeterminato full-time e in quel caso devo spostare la fine mobilità
		// alla data inizio del movimento (allineamento solo se ci sono periodi di Mobilità)
		if (listaMobilita.size() > 0) {
			String flgMovApprendistatoTIMob = this.sbGenerale.getAttribute("FLGAPPRENDISTATOTIMB") != null
					? this.sbGenerale.getAttribute("FLGAPPRENDISTATOTIMB").toString()
					: "";
			Vector vettMovApprendistato = null;
			if (flgMovApprendistatoTIMob.equalsIgnoreCase("S")) {
				vettMovApprendistato = new Vector();
				int movimentiSize = movimenti.size();
				Object objApp = null;
				for (int jApp = 0; jApp < movimentiSize; jApp++) {
					objApp = movimenti.get(jApp);
					if (objApp instanceof MovimentoBean) {
						MovimentoBean movimentoCurr = (MovimentoBean) objApp;
						String codTipoMov = movimentoCurr.getAttribute("CODTIPOMOV") != null
								? movimentoCurr.getAttribute("CODTIPOMOV").toString()
								: "";
						if (!codTipoMov.equalsIgnoreCase("CES")) {
							String codTipoAss = movimentoCurr.getAttribute("CODTIPOASS") != null
									? movimentoCurr.getAttribute("CODTIPOASS").toString()
									: "";
							String codMonoTempo = movimentoCurr.getAttribute("CODMONOTEMPO") != null
									? movimentoCurr.getAttribute("CODMONOTEMPO").toString()
									: "";
							if (codMonoTempo.equalsIgnoreCase("D")
									&& (codTipoAss.equals("A.03.00") || codTipoAss.equals("A.03.01")
											|| codTipoAss.equals("A.03.02") || codTipoAss.equals("A.03.03"))) {
								movimentoCurr.updAttribute("CODMONOTEMPO", "I");
								vettMovApprendistato.add(new Integer(jApp));
							}
						}
					}
				}
			}
			AllineamentoMobilita();
			if (flgMovApprendistatoTIMob.equalsIgnoreCase("S") && vettMovApprendistato.size() > 0) {
				int sizeVett = vettMovApprendistato.size();
				for (int jApp = 0; jApp < sizeVett; jApp++) {
					Integer indiceCurr = (Integer) vettMovApprendistato.get(jApp);
					MovimentoBean objApp = (MovimentoBean) movimenti.get(indiceCurr.intValue());
					objApp.updAttribute("CODMONOTEMPO", "D");
				}
			}
			Collections.sort(movimenti);
		}
		logMovimenti();
		// log Errori movimenti
		// logErroriMovimenti(movimenti);
	}

	private void setInfoCessazioneInMovimento(String codMotivoCessazione, Vector rowsMvCessazione, MovimentoBean mov)
			throws Exception {
		String flagMobilitaAperta = "";
		BigDecimal mesiMobilitaAperta = null;
		if (!codMotivoCessazione.equals("") && rowsMvCessazione != null && rowsMvCessazione.size() > 0) {
			SourceBean row = null;
			for (int k = 0; k < rowsMvCessazione.size(); k++) {
				row = (SourceBean) rowsMvCessazione.get(k);
				if (row.containsAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE)
						&& row.getAttribute(MovimentoBean.DB_COD_MOTIVO_CESSAZIONE).toString()
								.equalsIgnoreCase(codMotivoCessazione)) {
					flagMobilitaAperta = row.containsAttribute("FLGMOBILITARIMANEAPERTA")
							? row.getAttribute("FLGMOBILITARIMANEAPERTA").toString()
							: "";
					mesiMobilitaAperta = (BigDecimal) row.getAttribute("NUMMESIMOBAPERTA");
					break;
				}
			}
			if (mov.containsAttribute("FLGMOBILITARIMANEAPERTA")) {
				mov.updAttribute("FLGMOBILITARIMANEAPERTA", flagMobilitaAperta);
			} else {
				mov.setAttribute("FLGMOBILITARIMANEAPERTA", flagMobilitaAperta);
			}
			if (mesiMobilitaAperta != null) {
				mov.setAttribute("MESIMOBILITAAPERTA", mesiMobilitaAperta);
			}
		}
	}

	private void setMovimentoBackInAvv(Cessazione cessazione, MovimentoBean precedente, List movimenti)
			throws Exception {
		String dataInizioCess = (String) cessazione.getDataInizio();
		EventoAmministrativo evento = null;
		BigDecimal prgPrecedente = null;
		BigDecimal prg = null;
		prgPrecedente = (BigDecimal) precedente.getPrgMovimento();
		MovimentoBean mov = null;
		boolean trovato = false;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			if (trovato)
				break;
			evento = (EventoAmministrativo) movimenti.get(i);
			if (evento.getTipoEventoAmministrativo() == EventoAmministrativo.AVVIAMENTO) {
				mov = (MovimentoBean) evento;
				if (mov.containsAttribute("MOVIMENTI_PROROGATI")) {
					Vector prec = (Vector) mov.getAttribute("MOVIMENTI_PROROGATI");
					SourceBean movApp = null;
					for (int k = 0; k < prec.size(); k++) {
						movApp = (SourceBean) prec.get(k);
						prg = (BigDecimal) movApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
						if (prg != null && prg.equals(prgPrecedente)) {
							if (movApp.containsAttribute(MovimentoBean.DB_DATA_FINE)) {
								movApp.updAttribute(MovimentoBean.DB_DATA_FINE, dataInizioCess);
							} else {
								movApp.setAttribute(MovimentoBean.DB_DATA_FINE, dataInizioCess);
							}
							if (movApp.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
								movApp.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataInizioCess);
							} else {
								movApp.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA, dataInizioCess);
							}
							trovato = true;
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Data una trasformazione o proroga, attraverso il prgmovimentoprec si risale all'avviamento e aggiunge al vettore
	 * dei MOVIMENTI_PROROGATI la trasformazione o la proroga presa come argomento.
	 * 
	 * @param movimenti
	 * @param mTemp
	 * @param m
	 * @throws Exception
	 */
	private void gesticiProrogheTrasformazioni(List movimenti, MovimentoBean mTemp, SourceBean m) throws Exception {

		boolean flag = true;
		int jCont = 0;
		SourceBean mbApp = null;
		Vector vProrogati = null;
		SourceBean sbApp = null;
		while (flag) {
			if (mTemp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC) != null) {
				sbApp = dammiMovimento(movimenti, (BigDecimal) mTemp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC));
				if (sbApp == null) {
					sbApp = (SourceBean) DBLoad.getMovimento(mTemp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC),
							txExecutor);
				}
				mTemp = new MovimentoBean(sbApp);
				if (mTemp != null) {
					if (mTemp.getTipoMovimento() == MovimentoBean.ASSUNZIONE) {
						flag = false;
						int movimentiSize = movimenti.size();
						for (jCont = 0; jCont < movimentiSize; jCont++) {
							mbApp = (SourceBean) movimenti.get(jCont);
							if (mbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO) != null
									&& mTemp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString()
											.equals(mbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString())) {

								if (mbApp.getAttribute("MOVIMENTI_PROROGATI") != null) {
									vProrogati = (Vector) mbApp.getAttribute("MOVIMENTI_PROROGATI");
									vProrogati.add(m);
								} else {
									vProrogati = new Vector();
									vProrogati.add(mTemp);
									vProrogati.add(m);
									mbApp.setAttribute("MOVIMENTI_PROROGATI", vProrogati);
								}
								break;
							}
						}
					}
				} else {
					flag = false;
				}
			} else {
				flag = false;
			}
		}
	}

	/**
	 * cerca il SourceBean dello stato occupazionale legato al movimento. Se non lo trova ritorna null e il movimento
	 * non e' in inserimento ritorna null
	 * 
	 * @param v
	 * @param movimento
	 * @return
	 * @throws Exception
	 */
	private SourceBean cercaStatoOccMovimento(Vector v, SourceBean movimento) throws Exception {
		BigDecimal prgSO = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ);
		SourceBean ret = null;
		if (prgSO == null && !movimento.containsAttribute(MovimentoBean.FLAG_IN_INSERIMENTO)) {
			return null;
		}

		if (!movimento.containsAttribute(MovimentoBean.FLAG_IN_INSERIMENTO)) {
			for (int i = 0; ret == null && i < v.size(); i++) {
				SourceBean so = (SourceBean) v.get(i);
				if (so.getAttribute("PRGSTATOOCCUPAZ").equals(prgSO))
					ret = so;
			}
			if (ret == null) {
				ret = DBLoad.getStatoOccupazionaleSpecifico(prgSO, txExecutor);
			}
		} else {
			ret = new SourceBean("ROW");
			ret.setAttribute("prgStatoOccupaz", new BigDecimal(-1));
			ret.setAttribute("codstatoOccupaz", "C");
			ret.setAttribute("cdnLavoratore", movimento.getAttribute(MovimentoBean.DB_CDNLAVORATORE));
		}
		return ret;
	}

	/**
	 * Restituisce lo stato occupazionale associato al movimento
	 * 
	 * @param v
	 * @param movimento
	 * @return
	 */
	private StatoOccupazionaleBean cercaStatoOccBean(List v, SourceBean movimento) {
		BigDecimal prgSO = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ);
		StatoOccupazionaleBean ret = null;
		for (int i = 0; ret == null && i < v.size(); i++) {
			StatoOccupazionaleBean so = (StatoOccupazionaleBean) v.get(i);
			if (so.getPrgStatoOccupaz().equals(prgSO))
				ret = so;
		}
		return ret;
	}

	private SourceBean cercaDID(Vector v, SourceBean m) {
		return cercaCollegato(v, m);
	}

	private SourceBean cercaPatto(Vector v, SourceBean m) {
		return cercaCollegato(v, m);
	}

	private List estraiPatti(Vector patti, SourceBean m) throws SourceBeanException {
		// cerca patto
		List lista = new ArrayList(patti.size());
		BigDecimal prgSO = (BigDecimal) m.getAttribute("PRGSTATOOCCUPAZ");
		if (prgSO == null)
			return lista;
		for (int i = 0; i < patti.size(); i++) {
			SourceBean patto = (SourceBean) patti.get(i);
			if (patto.getAttribute("PRGSTATOOCCUPAZ") != null && patto.getAttribute("PRGSTATOOCCUPAZ").equals(prgSO)) {
				lista.add(new PattoBean(patto));
			}
		}
		return lista;
		// crea PattoBean
		// aggiungi in lista
		// ritorna la lista;
	}

	private SourceBean cercaCollegato(Vector v, SourceBean m) {
		BigDecimal prgSO = (BigDecimal) m.getAttribute("PRGSTATOOCCUPAZ");
		SourceBean ret = null;
		for (int i = 0; ret == null && i < v.size(); i++) {
			SourceBean so = (SourceBean) v.get(i);
			Object soV = so.getAttribute("PRGSTATOOCCUPAZ");
			if (soV != null && soV.equals(prgSO))
				ret = so;
		}
		return ret;
	}

	private MovimentoBean cercaMovimentoCollegato(List movimenti, MovimentoBean movimento)
			throws ControlliException, Exception, MovimentoNonCollegatoException {
		MovimentoBean mb = null;
		int movimentiSize = movimenti.size();
		int i = 0;
		for (; i < movimentiSize; i++) {
			EventoAmministrativo m = (EventoAmministrativo) movimenti.get(i);
			int tipoEvento = m.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.DID || tipoEvento == EventoAmministrativo.CHIUSURA_DID
					|| tipoEvento == EventoAmministrativo.PATTO || tipoEvento == EventoAmministrativo.MOBILITA
					|| tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA)
				continue;
			mb = (MovimentoBean) m;
			try {
				if (mb.esisteMovimentoNext(movimento)) {
					break;
				}
			} catch (MovimentoNonCollegatoException mNCEx) {
				throw mNCEx;
			}
		}
		if (i == movimentiSize) {
			// Controllo se ho un movimento non collegato
			Object prgMovimentoPrec = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
			if (prgMovimentoPrec == null)
				throw new MovimentoNonCollegatoException(55555);
			SourceBean sb = (SourceBean) DBLoad.getMovimento(prgMovimentoPrec, txExecutor);
			mb = new MovimentoBean(sb);
		}
		return mb;
	}

	private StatoOccupazionaleBean getStatoOccupazionale(List statiOccupazionali, Vector statiOccs, Vector dids,
			Vector patti, SourceBean so) throws Exception {
		StatoOccupazionaleBean sob = cercaStatoOccBean(statiOccupazionali, so);
		if (sob == null) {
			List pattiAssociati = estraiPatti(patti, so);
			SourceBean did = (SourceBean) cercaDID(dids, so);
			sob = new StatoOccupazionaleBean(so, did, pattiAssociati, statiOccupazionali);
		}
		return sob;
	}

	private StatoOccupazionaleBean getStatoOccupazionaleCollegato(List statiOccupazionali, Vector statiOccs,
			Vector dids, Vector patti, SourceBean so) throws Exception {
		StatoOccupazionaleBean sob = cercaStatoOccBean(statiOccupazionali, so);
		if (sob == null) {
			sob = getStatoOccupazionale(statiOccupazionali, statiOccs, dids, patti, so);
			StatoOccupazionaleBean soBack = cercaSOccPrec(statiOccs, statiOccupazionali, so);
			SourceBean patto = (SourceBean) cercaPatto(patti, soBack.getSource());
			SourceBean did = (SourceBean) cercaDID(dids, soBack.getSource());
			sob.setStatoOccupazionaleBack(soBack);
		} else {
			StatoOccupazionaleBean soBack = sob.getStatoOccupazionaleBack();
			if (soBack == null) {
				soBack = cercaSOccPrec(statiOccs, statiOccupazionali, so);
				SourceBean patto = (SourceBean) cercaPatto(patti, soBack.getSource());
				SourceBean did = (SourceBean) cercaDID(dids, soBack.getSource());
				sob.setStatoOccupazionaleBack(soBack);
			}
		}
		return sob.getStatoOccupazionaleBack();
	}

	private StatoOccupazionaleBean cercaSOccPrec(Vector statiOcc, List statiOccupazionali, SourceBean so)
			throws Exception {
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			StatoOccupazionaleBean soPrec = (StatoOccupazionaleBean) statiOccupazionali.get(i);
			if (soPrec.getDataFine() == null || soPrec.getDataFine().equals(""))
				continue;
			String dataFinePrec = DateUtils.giornoSuccessivo(soPrec.getDataFine());
			if (DateUtils.compare(dataFinePrec, (String) so.getAttribute("datinizio")) == 0) {
				return soPrec;
			}
		}
		for (int i = 0; i < statiOcc.size(); i++) {
			SourceBean soPrec = (SourceBean) statiOcc.get(i);
			if (soPrec.getAttribute("datFine") == null || so.getAttribute("datInizio") == null)
				continue;
			String dataFinePrec = DateUtils.giornoSuccessivo((String) soPrec.getAttribute("datFine"));
			if (DateUtils.compare(dataFinePrec, (String) so.getAttribute("datinizio")) == 0) {
				return new StatoOccupazionaleBean(soPrec, null, null, statiOccupazionali);
			}
		}
		// se si e' giunti qui significa che lo stato occupazionale precedente ad so non esiste nel db:
		// bisogna crearne uno
		// virtuale con un numero di informazione minimale tale da permettere un corretto funzionamento del
		// codice di ricostruizione della storia amministrativa del lavoratore
		StatoOccupazionaleBean soPrec = DBLoad.getStatoOccupazionaleUltimo(so.getAttribute("cdnLavoratore"),
				(String) so.getAttribute("datInizio"), txExecutor);
		if (soPrec != null)
			return new StatoOccupazionaleBean(soPrec, null);
		Object cdnLavoratore = so.getAttribute("cdnLavoratore");
		so = new SourceBean("ROW");
		so.setAttribute("codStatoOccupaz", "C"); // altro
		so.setAttribute("prgStatoOccupaz", new BigDecimal(-1));
		so.setAttribute("cdnLavoratore", cdnLavoratore);
		return new StatoOccupazionaleBean(so, null, null, statiOccupazionali);
	}

	/**
	 * Esecuzione impatti in presenza o meno di movimenti orfani. In particolare nella TS_GENERALE c'è un flag
	 * FLGNONSCATTIMPATTICONMOVORFANI Se il flag = "S" allora deve essere possibile completare l'operazione senza però
	 * far scattare gli impatti amministrativi (segnalazione con warning) Se il flag = "N" allora non deve essere
	 * possibile completare l'operazione
	 * 
	 * @param movimenti
	 * @param indice
	 * @throws Exception
	 */
	protected void checkMovimentiOrfaniPerImpatti(List movimenti, int indice) throws Exception {
		Object obj = null;
		if (!Controlli.eseguiImpattiInPresenzaMovOrfani()) {
			int movimentiSize = movimenti.size();
			for (int i = indice; i < movimentiSize; i++) {
				obj = movimenti.get(i);
				if (obj instanceof MovimentoBean) {
					Controlli.controllaMovimentoProTraSenzaPrec((SourceBean) movimenti.get(i));
				}
			}
		}
	}

	/**
	 * Ricrea la situazione amministrativa a partire dalla mobilità
	 * 
	 * @param mobilita
	 * @throws ControlliException
	 * @throws Exception
	 */
	public void ricreaDaMobilita(SourceBean mobilita) throws ControlliException, Exception {
		_logger.debug("SituazioneAmministrativa.ricrea()");

		String dataRif = mobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
		StatoOccupazionaleBean statoOccIniziale = null;

		Object cdnLavoratore = mobilita.getAttribute(MovimentoBean.DB_CDNLAVORATORE);
		this.lso = new ListaStatiOccupazionali(mobilita, this.movimenti, this.statiOccupazionali, this.dids,
				this.listaMobilita, txExecutor);

		_logger.debug("SituazioneAmministrativa.ricrea(): " + lso);

		// Per ricostruire la situazione amministrativa ho bisogno dell'indice da cui far partire l'operazione
		int ind = cercaIndiceDaMobilita(dataRif);
		if (ind >= 0) {
			// controllo per far scattare o meno gli impatti in presenza
			// di movimenti orfani (proroghe o trasformazioni a t.d. senza mov precedente)
			checkMovimentiOrfaniPerImpatti(this.movimenti, ind);
		}
		resettaStatiOccupazionali(this.lso);
		_logger.debug("SituazioneAmministrativa.ricrea(): movimento iniziale:" + movimenti.get(ind));

		if (ind < 0) {
			// non ci sono movimenti a partire dai quali ricostruire gli stati occupazionali
			// in teoria sarebbe un errore dato che sono entrato in questo metodo proprio per ricostruire
			// la situazione amministrativa a partire da un movimento
		} else {
			statoOccIniziale = lso.getStatoOccupazionaleIniziale();
			_logger.debug("SituazioneAmministrativa.ricreaDaMobilita():avvio ricostruzione storia");

			ricostruzioneStoria(ind, statoOccIniziale, mobilita);
		}
	}

	/**
	 * Ricrea la situazione a partire dalla did, con lo stato occupazionale aperto a partire dalla did statoOccIniziale
	 * 
	 * @param did
	 * @param statoOccIniziale
	 * @throws ControlliException
	 * @throws Exception
	 */
	public void ricrea(SourceBean did, StatoOccupazionaleBean statoOccIniziale, boolean inChiusura)
			throws ControlliException, Exception {
		Object o = did.getAttribute("prgDichDisponibilita");
		BigDecimal prgDichDisp = null;
		if (o instanceof String) {
			prgDichDisp = new BigDecimal((String) o);
		} else {
			prgDichDisp = (BigDecimal) o;
		}
		int movimentiSize = movimenti.size();
		if (inChiusura) {
			for (int j = 0; j < movimentiSize; j++) {
				EventoAmministrativo m = (EventoAmministrativo) movimenti.get(j);
				if (m.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_DID)
					if (((ChiusuraDidBean) m).getPrgDichDisponibilita().equals(prgDichDisp)) {
						if (j + 1 < movimentiSize) {
							ricostruzioneStoria(j + 1, statoOccIniziale, null);
						}
						return;
					}
			}
		}
		int i = 0;
		for (; i < movimentiSize; i++) {
			EventoAmministrativo m = (EventoAmministrativo) movimenti.get(i);
			if (m.getTipoEventoAmministrativo() == EventoAmministrativo.DID) {
				DidBean didBean = (DidBean) m;
				if (didBean.getPrgDichDisponibilita().intValue() == prgDichDisp.intValue())
					break;
			}
		}
		if (i == movimentiSize) {
			_logger.debug(
					"SituazioneAmministrativa.ricrea(): impossibile trovare la did a partire dalla quale ricreare la storia");

			throw new ControlliException(MessageCodes.StatoOccupazionale.ERRORE_GENERICO);
		}
		if (i + 1 < movimentiSize) {
			did.setAttribute("RICOSTRUZIONE_STORIA_ESEGUITA", "true");
			ricostruzioneStoria(i + 1, statoOccIniziale, null);
		}
	}

	/**
	 * ricostruisce la storia a partire dal movimento movimentoIniziale, che rappresenta il movimento gestito.
	 */
	public void ricrea(SourceBean movimentoIniziale) throws ControlliException, Exception {
		_logger.debug("SituazioneAmministrativa.ricrea()");

		String dataRif = null;
		StatoOccupazionaleBean statoOccIniziale = null;
		if (!movimentoIniziale.containsAttribute("prgMovimento"))
			movimentoIniziale.updAttribute("prgMovimento", new BigDecimal(-1));

		Object cdnLavoratore = movimentoIniziale.getAttribute(MovimentoBean.DB_CDNLAVORATORE);
		this.lso = new ListaStatiOccupazionali(movimentoIniziale, this.movimenti, this.statiOccupazionali, this.dids,
				this.listaMobilita, txExecutor);
		_logger.debug("SituazioneAmministrativa.ricrea(): " + lso);

		StatoOccupazionaleBean statoOccupazionaleIniziale = lso.getStatoOccupazionaleIniziale();
		// Per ricostruire la situazione amministrativa ho bisogno dell'indice da cui far partire l'operazione
		int ind = cercaIndice(statoOccupazionaleIniziale, getMovimenti());
		if (ind >= 0) {
			// controllo per far scattare o meno gli impatti in presenza
			// di movimenti orfani (proroghe o trasformazioni a t.d. senza mov precedente)
			checkMovimentiOrfaniPerImpatti(this.movimenti, ind);
		}
		resettaStatiOccupazionali(this.lso);
		// Bisogna riaprire le DID in caso di riapertura di uno stato occupaz. di D o I
		if (lso.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
				|| lso.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
			boolean pattoRiaperto = false;
			// bisogna riaprire la did nel caso sia chiusa
			DidBean did = (DidBean) cercaDid((String) lso.getStatoOccupazionaleIniziale().getDataInizio());
			if (did == null) {
				dataRif = (String) movimentoIniziale.getAttribute("datInizioMov");
				// dataRif == null quando il metodo ricrea viene chiamato passando la mobilità
				// e in questo caso non bisogna riaprire la did nel caso in cui sia chiusa
				if (dataRif != null) {
					did = (DidBean) cercaDid(it.eng.afExt.utils.DateUtils.giornoPrecedente(dataRif));
					if (did == null)
						did = (DidBean) cercaDid(dataRif);
				}
			}
			if (did != null && did.getAttribute("datFine") != null) {
				String dataDichiarazione = did.getDataInizio();
				String dataFineDid = did.getDataFine();
				// riapro la did
				BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
				numKlo = numKlo.add(new BigDecimal(1));
				did.updAttribute("numKloDichDisp", numKlo);
				DBStore.riapriDID(did.getAttribute("prgDichDisponibilita"), dataDichiarazione, numKlo,
						RequestContainer.getRequestContainer(), txExecutor);
				did.updAttribute("flag_changed", "1");
				did.setAttribute("datFine_originale", did.getAttribute("datFine"));
				did.delAttribute("datFineChanged");
				did.delAttribute("datFine");
				// Aggiorno il numklo dell'eventuale ChiusuraDID successiva
				int movimentiSize = movimenti.size();
				for (int k = 0; k < movimentiSize; k++) {
					EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
					if (did.getTipoEventoAmministrativo() == EventoAmministrativo.DID) {
						if (c.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_DID) {
							ChiusuraDidBean cDb = (ChiusuraDidBean) c;
							if (cDb.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita())) {
								_logger.debug("SituazioneAmministrativa.ricrea():aggiornamento chiusura DID: " + cDb);

								cDb.updAttribute("numKloDichDisp", did.getAttribute("numKloDichDisp"));
								break;
							}
						}
					} else {
						if (did.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_DID) {
							if (c.getTipoEventoAmministrativo() == EventoAmministrativo.DID) {
								DidBean db = (DidBean) c;
								if (db.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita())) {
									_logger.debug("SituazioneAmministrativa.ricrea():aggiornamento DID: " + db);

									db.updAttribute("numKloDichDisp", did.getAttribute("numKloDichDisp"));
									break;
								}
							}
						}
					}
				} // end for
					// riapertura patto associato alla did
				pattoRiaperto = riapriPattoAssocDid(did);
				// fine riapertura patto associato alla did
			}
			if (!pattoRiaperto) {
				riapriPattoMobilita((String) lso.getStatoOccupazionaleIniziale().getDataInizio(),
						cdnLavoratore.toString());
			}
		}
		_logger.debug("SituazioneAmministrativa.ricrea(): movimento iniziale:" + movimenti.get(ind));

		if (ind < 0) {
			// non ci sono movimenti a partire dai quali ricostruire gli stati occupazionali
			// in teoria sarebbe un errore dato che sono entrato in questo metodo proprio per ricostruire
			// la situazione amministrativa a partire da un movimento
		} else {
			statoOccIniziale = lso.getStatoOccupazionaleIniziale();
			_logger.debug("SituazioneAmministrativa.ricrea():avvio ricostruzione storia");

			ricostruzioneStoria(ind, statoOccIniziale, movimentoIniziale);
		}
	}

	/**
	 * Utilizzato per la situazione da sanare
	 * 
	 * @param movimentoIniziale
	 * @param lso
	 * @throws ControlliException
	 * @throws Exception
	 */
	public void ricrea(SourceBean movimentoIniziale, ListaStatiOccupazionali lso) throws ControlliException, Exception {
		_logger.debug("SituazioneAmministrativa.ricrea()");

		String dataRif = null;
		StatoOccupazionaleBean statoOccIniziale = null;
		if (!movimentoIniziale.containsAttribute("prgMovimento"))
			movimentoIniziale.updAttribute("prgMovimento", new BigDecimal(-1));

		Object cdnLavoratore = movimentoIniziale.getAttribute(MovimentoBean.DB_CDNLAVORATORE);
		this.lso = lso;
		_logger.debug("SituazioneAmministrativa.ricrea(): " + lso);

		resettaStatiOccupazionali(this.lso);
		// ora possiamo scorrere i movimenti per ricostruire la situazione amministrativa
		// ho bisogno dell'indice da cui far partire l'operazione
		int ind = cercaIndice((String) movimentoIniziale.getAttribute("datInizioMov"));
		_logger.debug("SituazioneAmministrativa.ricrea(): movimento iniziale:" + movimenti.get(ind));

		if (ind < 0) {
			// non ci sono movimenti a partire dai quali ricostruire gli stati occupazionali
			// in teoria sarebbe un errore dato che sono entrato in questo metodo proprio per ricostruire
			// la situazione amministrativa a partire da un movimento
		} else {
			statoOccIniziale = lso.getStatoOccupazionaleIniziale();
			_logger.debug("SituazioneAmministrativa.ricrea():avvio ricostruzione storia");

			ricostruzioneStoria(ind, statoOccIniziale, movimentoIniziale);
		}
	}

	/**
	 * INVOCATO NELLA NUOVA GESTIONE DEGLI IMPATTI (SITUAZIONEAMMINISTRATIVAFACTORY) E IN ANNULLA E RETTIFICA MOVIMENTO
	 * 
	 * @param statoOccIniziale
	 * @param movimenti
	 * @return
	 * @throws Exception
	 */
	public int cercaIndice(StatoOccupazionaleBean statoOccIniziale, List movimenti) throws Exception {
		int i = 0;
		String dataRif = statoOccIniziale.getDataInizio();
		int movimentiSize = movimenti.size();
		for (; i < movimentiSize; i++) {
			Object o = movimenti.get(i);
			String dataInizio = null;
			if (o instanceof MovimentoBean) {
				MovimentoBean mb = (MovimentoBean) o;
				dataInizio = mb.getDataInizio();
			} else {
				if (o instanceof ChiusuraDidBean) {
					ChiusuraDidBean chiusuradid = (ChiusuraDidBean) o;
					dataInizio = chiusuradid.getDataFine();
				} else {
					if (o instanceof DidBean) {
						DidBean db = (DidBean) o;
						dataInizio = (String) db.getAttribute("datDichiarazione");
					} else {
						if (o instanceof MobilitaBean) {
							MobilitaBean mobilita = (MobilitaBean) o;
							dataInizio = mobilita.getDataInizio();
						} else {
							if (o instanceof ChiusuraMobilitaBean) {
								ChiusuraMobilitaBean chMobilita = (ChiusuraMobilitaBean) o;
								dataInizio = chMobilita.getDataInizio();
							}
						}
					}
				}
			}
			if (DateUtils.compare(dataInizio, dataRif) > 0) {
				break;
			}
		}
		if (movimentiSize > 0) {
			if (i == movimentiSize) {
				// non ha trovato nessun evento amministrativo con data inizio > data inizio dello
				// stato occupazionale iniziale
				EventoAmministrativo m = (EventoAmministrativo) movimenti.get(i - 1);
				String dataInizioEvento = null;
				int tipoEvento = m.getTipoEventoAmministrativo();
				if (tipoEvento == EventoAmministrativo.DID)
					dataInizioEvento = ((DidBean) m).getDataInizio();
				else if (tipoEvento == EventoAmministrativo.CHIUSURA_DID)
					dataInizioEvento = ((ChiusuraDidBean) m).getDataFine();
				else if (tipoEvento == EventoAmministrativo.MOBILITA)
					dataInizioEvento = ((MobilitaBean) m).getDataInizio();
				else if (tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA)
					dataInizioEvento = ((ChiusuraMobilitaBean) m).getDataInizio();
				else
					dataInizioEvento = ((MovimentoBean) m).getDataInizio();

				if (DateUtils.compare(dataInizioEvento, dataRif) < 0)
					i = -1;
				else
					i = i - 1;
			}
		}
		i = movimentiSize == 0 ? -1 : i;
		return (i);
	}

	/**
	 * chiamata dalla ricostruzione storia quando si incontra l'evento amministrativo Did
	 * 
	 * @param so
	 * @param did
	 * @return
	 * @throws ControlliException
	 * @throws Exception
	 */
	private StatoOccupazionaleBean gestisciDid(StatoOccupazionaleBean so, DidBean did,
			StatoOccupazionaleManager2 statoOccManager) throws ControlliException, Exception {
		SourceBean sb = did.getSource();
		Vector vettParametriRicostruzione = null;
		String dataRif = (String) did.getAttribute("datDichiarazione");
		String cdnLavoratore = it.eng.sil.util.Utils.notNull(did.getAttribute("cdnLavoratore"));
		Vector rows = estraiMovimentiAnno(dataRif);
		StatoOccupazionaleBean ret = null;
		boolean riapri = false;
		try {
			String dataFine = (String) did.getAttribute("datFine");
			if (dataFine != null) {
				String codMotivoFine = (String) did.getAttribute("codMotivoFineAtto");
				if (codMotivoFine.equals("AV"))
					riapri = true;
			}
			ret = DIDManager.inserisci((StatoOccupazionaleBean) so, sb, requestContainer, null, rows, movimenti, "DID",
					this.getListaMobilita(), vettParametriRicostruzione, statoOccManager, getTipoCongif_MOV_C03(),
					getData150(), getListaDisabiliCM(), txExecutor);
			if (riapri) {
				_logger.debug(
						"SituazioneAmministrativa.gestisciDid(): e' necessario riaprire la did storicizzata " + did);

				did.getSource().delAttribute("datFine");
				BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
				numKlo = numKlo.add(new BigDecimal(1));
				did.getSource().updAttribute("numKloDichDisp", numKlo);
				_logger.debug("SituazioneAmministrativa.gestisciDid(): viene riaperta la did " + did);

				DBStore.apriDID(did.getPrgDichDisponibilita(), did.getDataInizio(), null, so.getPrgStatoOccupaz(),
						numKlo, requestContainer, txExecutor);
				apriDidInVettoreDids(did.getPrgDichDisponibilita());
				did.getSource().updAttribute("flag_changed", "1");
				did.getSource().delAttribute("datFineChanged");
				// riapertura patto associato alla did
				boolean pattoRiaperto = riapriPattoAssocDid(did);
				if (!pattoRiaperto) {
					riapriPattoMobilita(dataRif, cdnLavoratore.toString());
				}
			}
		} catch (ControlliException ce) {
			if (!forzaRicostruzione) {
				requestContainer.getServiceRequest().setAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE,
						"");
				throw ce;
			} else {
				if (logFileBatch != null) {
					String msg = "";
					msg = "Dichiarazione non stipulabile e/o non valida che è stata chiusa dal batch.";
					logFileBatch.writeLog(msg);
				}
			}
			ret = so;
			boolean isInMobilita = DIDManager.dichiarazioneDidInMobilita(this.getListaMobilita(), dataRif);
			if (isInMobilita) {
				// devo controllare se chiudere o meno la did
				if (so.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_O) {
					if (!did.containsAttribute("codMotivoFineAtto")
							|| did.getAttribute("codMotivoFineAtto").toString().equals("")
							|| did.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
						DBStore.chiudiDID(did.getSource(), dataRif, "AV", requestContainer, txExecutor);
						chiudiDidInVettoreDids(did.getPrgDichDisponibilita(), dataRif);
						SourceBean patto = cercaPatto(dataRif);
						if (patto != null) {
							DBStore.chiudiPatto297(patto, dataRif, "AV", requestContainer, txExecutor);
							aggiornaNumKloPatto(new BigDecimal(patto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
									dataRif, new BigDecimal(patto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
						}
						_logger.debug("SituazioneAmministrativa.gestisciDid():forzata la chiusura della did:" + did);
					} else {
						addWarning(MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO);
					}
				} else {
					// devo controllare se riaprire o meno la did
					if (so.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
							|| so.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
						if (riapri) {
							_logger.debug(
									"SituazioneAmministrativa.gestisciDid(): e' necessario riaprire la did storicizzata "
											+ did);

							did.getSource().delAttribute("datFine");
							BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
							numKlo = numKlo.add(new BigDecimal(1));
							did.getSource().updAttribute("numKloDichDisp", numKlo);
							_logger.debug("SituazioneAmministrativa.gestisciDid(): viene riaperta la did " + did);

							DBStore.apriDID(did.getPrgDichDisponibilita(), did.getDataInizio(), null,
									so.getPrgStatoOccupaz(), numKlo, requestContainer, txExecutor);
							apriDidInVettoreDids(did.getPrgDichDisponibilita());
							did.getSource().updAttribute("flag_changed", "1");
							did.getSource().delAttribute("datFineChanged");
							// riapertura patto associato alla did
							boolean pattoRiaperto = riapriPattoAssocDid(did);
							if (!pattoRiaperto) {
								riapriPattoMobilita(dataRif, cdnLavoratore.toString());
							}
						}
					}
				}
			} else {
				// aggiungi warning
				addWarning(MessageCodes.DID.STATO_OCCUPAZIONALE_NON_CONGRUENTE);
				addWarning(MessageCodes.DID.CHIUSURA_AVVENUTA);
				// chiudi la did
				if (!did.containsAttribute("codMotivoFineAtto")
						|| did.getAttribute("codMotivoFineAtto").toString().equals("")
						|| did.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
					DBStore.chiudiDID(did.getSource(), dataRif, "AV", requestContainer, txExecutor);
					chiudiDidInVettoreDids(did.getPrgDichDisponibilita(), dataRif);
					SourceBean patto = cercaPatto(dataRif);
					if (patto != null) {
						DBStore.chiudiPatto297(patto, dataRif, "AV", requestContainer, txExecutor);
						aggiornaNumKloPatto(new BigDecimal(patto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
								dataRif, new BigDecimal(patto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
					}
					_logger.debug("SituazioneAmministrativa.gestisciDid():forzata la chiusura della did:" + did);

				} else {
					addWarning(MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO);
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return ret;
	}

	/**
	 * Controlla se la did precedente e' stata chiusa. In caso contrario bisogna chiuderla prima di riaprire la
	 * successiva. Infatti non e' ammesso che vi siano due did aperte o che la did aperta preceda una chiusa, a
	 * prescindere che siano o meno protocollate. Il motivo di fine atto viene ripreso dal record originario, mentre
	 * evito di far scattare l'impatto sullo stato occupazionale della chiusura did.
	 * 
	 * @param did
	 * @throws Exception
	 */
	private StatoOccupazionaleBean chiusuraDidPrecedente(DidBean did, StatoOccupazionaleBean statoOccCorrente)
			throws Exception {
		DidBean didPrec = getDidPrecedente(did);
		StatoOccupazionaleBean nuovoStatoOcc = statoOccCorrente;
		// controlla che sia chiusa
		if (didPrec != null) {
			SourceBean didSb = didPrec.getSource();
			didSb = DBLoad.getDID(didSb.getAttribute("prgDichDisponibilita"), txExecutor);
			if (didSb.getAttribute("datFine") == null) {
				String dataChiusura = did.getDataInizio();
				if (DateUtils.compare(dataChiusura, didPrec.getDataInizio()) > 0)
					dataChiusura = DateUtils.giornoPrecedente(dataChiusura);
				_logger.debug(
						"SituazioneAmministrativa.chiusuraDidPrecedente(): trovata did aperta e' necessario chiuderla per evitare di averne due aperte: did trovata: "
								+ didPrec);

				String codMotivoFineAtto = (String) didSb.getAttribute("codMotivoFineAtto");
				if (codMotivoFineAtto == null) {
					codMotivoFineAtto = "AV";
					didPrec.updAttribute("codMotivoFineAtto", codMotivoFineAtto);
				}
				DBStore.chiudiDID(didPrec.getSource(), dataChiusura, codMotivoFineAtto, requestContainer, txExecutor);
				chiudiDidInVettoreDids(didPrec.getPrgDichDisponibilita(), dataChiusura);
				// il nuovo stato occupazionale di raggruppamento è altro (A) e dettaglio
				// in base al motivo di chiusura Did (se e solo se non c'è una did aperta nella stessa data)
				didPrec.updAttribute("datFine", dataChiusura);
				SourceBean patto = cercaPatto(didPrec.getPrgDichDisponibilita(), (BigDecimal) cdnLavoratoreSitAmm);
				if (patto != null) {
					DBStore.chiudiPatto297(patto, dataChiusura, "AV", requestContainer, txExecutor);
					aggiornaNumKloPatto(new BigDecimal(patto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
							dataChiusura, new BigDecimal(patto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
				}
				_logger.debug(
						"SituazioneAmministrativa.chiusuraDidPrecedente():forzata la chiusura del patto per evitare di averne due aperti:"
								+ patto);

				SourceBean didInDataRif = cercaDid(did.getDataInizio());
				// controlla se ci sono movimenti aperti nella stessa data di chiusura did
				SourceBean movInDataRif = cercaMovimento(did.getDataInizio());
				if (didInDataRif == null) {
					if (movInDataRif != null) {
						nuovoStatoOcc.setStatoOccupazionale(StatoOccupazionaleBean.B);
						nuovoStatoOcc.setDataInizio(did.getDataInizio());
						nuovoStatoOcc.setCodMonoProvenienza("D");
						nuovoStatoOcc.setChanged(true);
						DBStore.creaNuovoStatoOcc(nuovoStatoOcc, did.getDataInizio(), requestContainer, txExecutor);
					} else {
						SourceBean sbAssociatoAlMotivoFineAtto = DBLoad.getStatoOccAssociatoAlMotivoFineAtto(
								didPrec.getSource().getAttribute("codMotivoFineAtto").toString(), "AM_DIC_D",
								did.getDataInizio());
						if ((sbAssociatoAlMotivoFineAtto != null) && (!sbAssociatoAlMotivoFineAtto
								.getAttribute("codStatoOccupaz").toString().equals(""))) {
							if (statoOccCorrente.getStatoOccupazionaleRagg() != StatoOccupazionaleBean.RAGG_A) {
								nuovoStatoOcc.setStatoOccupazionale(
										sbAssociatoAlMotivoFineAtto.getAttribute("codStatoOccupaz").toString());
								nuovoStatoOcc.setDataInizio(did.getDataInizio());
								nuovoStatoOcc.setCodMonoProvenienza("D");
								nuovoStatoOcc.setChanged(true);
								DBStore.creaNuovoStatoOcc(nuovoStatoOcc, did.getDataInizio(), requestContainer,
										txExecutor);
							}
						} else {
							// per sicurezza se non c'è il motivo di chiusura did (in generale viene valorizzato)
							if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.C1) {
								nuovoStatoOcc.setStatoOccupazionale(StatoOccupazionaleBean.C1);
								nuovoStatoOcc.setDataInizio(did.getDataInizio());
								nuovoStatoOcc.setCodMonoProvenienza("D");
								nuovoStatoOcc.setChanged(true);
								DBStore.creaNuovoStatoOcc(nuovoStatoOcc, did.getDataInizio(), requestContainer,
										txExecutor);
							}
						}
					}
				}
			}
		}
		return nuovoStatoOcc;
	}

	/**
	 * metodo invocato nella gestione della did
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public Vector estraiMovimentiAnno(String dataRif) throws Exception {
		Vector v = new Vector();
		BigDecimal prgMovimentoSucc = null;
		MovimentoBean movimentoSucc = null;
		BigDecimal prgMovimentoApp = null;
		String dataInizioApp = "";
		String dataFineApp = "";
		String codTipoMovApp = "";
		MovimentoBean eventoApp = null;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			EventoAmministrativo m = (EventoAmministrativo) movimenti.get(i);
			int tipoEvento = m.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.TRASFORMAZIONE
					|| tipoEvento == EventoAmministrativo.PROROGA) {
				MovimentoBean mb = (MovimentoBean) m;
				if (DateUtils.compare(dataRif, mb.getDataInizio()) >= 0
						&& ((mb.getDataFineEffettiva() != null && !mb.getDataFineEffettiva().equals("")
								&& DateUtils.compare(dataRif, mb.getDataFineEffettiva()) <= 0)
								|| mb.getDataFineEffettiva() == null)) {
					v.add(mb.getSource());
				} else {
					if (mb.containsAttribute("MOVIMENTI_PROROGATI")) {
						Vector vProrogati = (Vector) mb.getAttribute("MOVIMENTI_PROROGATI");
						// if (mb.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC) != null) {
						// &&mb.getDataFineEffettiva()!= null && DateUtils.getAnno(mb.getDataFineEffettiva()) ==
						// DateUtils.getAnno(dataRif)) {
						// prgMovimentoSucc = (BigDecimal)mb.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC);
						for (int iContSucc = 0; iContSucc < vProrogati.size(); iContSucc++) {
							eventoApp = new MovimentoBean((SourceBean) vProrogati.get(iContSucc));
							int tipoEventoApp = eventoApp.getTipoEventoAmministrativo();
							if (tipoEventoApp == EventoAmministrativo.TRASFORMAZIONE
									|| tipoEventoApp == EventoAmministrativo.PROROGA) {
								movimentoSucc = (MovimentoBean) eventoApp;
								dataInizioApp = movimentoSucc.getDataInizio();
								dataFineApp = movimentoSucc.getDataFineEffettiva();
								prgMovimentoApp = movimentoSucc.getPrgMovimento();
								codTipoMovApp = movimentoSucc.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString();
								if (!codTipoMovApp.equals("CES")) {
									if (dataRif != null && !dataRif.equals("")
											&& (DateUtils.compare(dataRif, dataInizioApp) <= 0 || (DateUtils
													.compare(dataRif, dataInizioApp) > 0
													&& (dataFineApp == null || dataFineApp.equals("")
															|| DateUtils.compare(dataRif, dataFineApp) <= 0)))) {
										v.add(mb.getSource());
										break;
									} else {
										if (movimentoSucc.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC) != null) {
											prgMovimentoSucc = (BigDecimal) movimentoSucc
													.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC);
										} else {
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return v;
	}

	/**
	 * Viene ricostruita la storia a partire dal movimento corrispondente alla posizione ind del vettore dei movimenti
	 * 
	 * @param ind
	 * @param statoOccCorrente
	 * @param movimentoIniziale
	 * @throws Exception
	 */
	private StatoOccupazionaleBean ricostruzioneStoria(int ind, StatoOccupazionaleBean statoOccCorrente,
			SourceBean movimentoIniziale) throws Exception {
		String dataRif = null;
		String msgControlli = "";
		String dataStatoOccupazionaleManuale = "";
		int annoStatoOccManuale = 0;
		int indiceStatoOccManuali = 0;
		int sizeStatiOccManuali = 0;
		StatoOccupazionaleBean ultimoSOccManuale = null;
		String dataInizioUltimoSOccManuale = "";
		boolean esisteWarningEventoSOccManuale = false;
		boolean esistonoStatiOccManuali = false;
		if (getStatiOccupazionaliManuali() != null && getStatiOccupazionaliManuali().size() > 0) {
			esistonoStatiOccManuali = true;
			sizeStatiOccManuali = getStatiOccupazionaliManuali().size();
			ultimoSOccManuale = getStatoOccupazionaliManuali(sizeStatiOccManuali - 1);
			dataInizioUltimoSOccManuale = ultimoSOccManuale.getDataInizio();
		}

		// controllo se ci sono stati occupazionali(tra quelli cancellati prima di effettuare la
		// ricostruzione dello stato occupazionale del lavoratore)gestiti manualmente
		try {
			if (requestContainer.getServiceRequest().containsAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO")) {
				if (esistonoStatiOccManuali) {
					msgControlli = "Esistono stati occupazionali inseriti/modificati manualmente che non saranno cancellati dal ricalcolo impatti.";
				} else {
					msgControlli = "Esistono stati occupazionali inseriti/modificati manualmente che potrebbero essere cancellati dal ricalcolo impatti.";
				}
				requestContainer.getServiceRequest().delAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO");
				if (esistonoStatiOccManuali) {
					throw new ControlliException(MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE);
				} else {
					throw new ControlliException(MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE);
				}
			} else {
				if (requestContainer.getServiceRequest()
						.containsAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI")) {
					msgControlli = "Esistono stati occupazionali con mesi anzianità/sospensione non calcolati dal Sil che potrebbero essere cancellati dal ricalcolo impatti.";
					requestContainer.getServiceRequest().delAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI");
					throw new ControlliException(
							MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI);
				} else {
					if (requestContainer.getServiceRequest()
							.containsAttribute("INCONGRUENZA_DATA_ANZIANITA_STATO_OCC")) {
						msgControlli = "Esistono stati occupazionali di disoccupazione con anzianità diversa dalla data di stipula DID/iscrizione alla mobilità che potrebbero essere cancellati dal ricalcolo impatti.";
						requestContainer.getServiceRequest().delAttribute("INCONGRUENZA_DATA_ANZIANITA_STATO_OCC");
						throw new ControlliException(
								MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA);
					}
				}
			}
		} catch (ControlliException ce) {
			String valore = "";
			if (!continuaRicalcolo) {
				if (forzaRicostruzione)
					valore = "true";
				else
					valore = "false";
				requestContainer.getServiceRequest().setAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE,
						valore);
				throw ce;
			} else {
				if (logFileBatch != null) {
					String msg = "";
					switch (ce.getCode()) {
					case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
						msg = "Trovati stati occupazionali inseriti/modificati manualmente che sono stati cancellati dal batch.";
						break;
					case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
						msg = " Trovati stati occupazionali inseriti/modificati manualmente che non sono stati cancellati dal batch.";
						break;
					case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
						msg = "Trovati stati occupazionali con mesi anzianità/sospensione non calcolati dal Sil che sono stati cancellati dal batch.";
						break;
					case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
						msg = "Trovati stati occupazionali con mesi anzianità/sospensione non calcolati dal Sil che sono stati cancellati dal batch.";
						break;
					default: // gestione errore generico
						msg = "Trovati stati occupazionali gestiti manualmente che sono stati cancellati dal batch.";
					}
					logFileBatch.writeLog(msg);
				}
			}
		}
		String dataInizioSoIniziale = statoOccCorrente.getDataInizio();
		int annoSoccIniziale = DateUtils.getAnno(dataInizioSoIniziale);
		if (statoOccCorrente.virtuale()) {
			DBStore.creaNuovoStatoOcc(statoOccCorrente, dataInizioSoIniziale, requestContainer, txExecutor);
			statiOccupazionaliCreati.add(statoOccCorrente);
		}
		Object cdnLavoratore = statoOccCorrente.getCdnLavoratore();
		StatoOccupazionaleBean nuovoStatoOcc = null;
		StatoOccupazionaleManager2 statoOccManager = new StatoOccupazionaleManager2(this);
		int i = ind;
		int annoRicostruzioneCorrente = -1;
		int annoRicostruzionePrec = -1;
		String dataInizioEvento = "";
		int annoInizioEvento = 0;
		boolean ricostruzioneEseguita = false;
		String dataCalcoloInizioAnno = "";
		String flgImpattiTraAnni = "";
		String flgImpattiTirInMobSosp = "";
		String flgImpattiTirInMobEff = "";
		int numGiorniDidVA18 = 0;
		// LETTURA DALLA TS_GENERALE PER GESTIRE O MENO GLI IMPATTI A CAVALLO DI DUE O PIU' ANNI
		if (this.sbGenerale == null) {
			this.sbGenerale = DBLoad.getInfoGenerali();
		}
		if (this.sbGenerale != null) {
			flgImpattiTraAnni = this.sbGenerale.containsAttribute("FLGIMPATTITRAANNI")
					? this.sbGenerale.getAttribute("FLGIMPATTITRAANNI").toString()
					: "";
			flgImpattiTirInMobSosp = this.sbGenerale.containsAttribute("FLGSCATTANOIMPATTITIRINMOBSOSP")
					? this.sbGenerale.getAttribute("FLGSCATTANOIMPATTITIRINMOBSOSP").toString()
					: "";
			flgImpattiTirInMobEff = this.sbGenerale.containsAttribute("FLGSCATTANOIMPATTITIRINMOBEFF")
					? this.sbGenerale.getAttribute("FLGSCATTANOIMPATTITIRINMOBEFF").toString()
					: "";
			if (sbGenerale.containsAttribute("NUMGGDID")) {
				numGiorniDidVA18 = Integer.parseInt(sbGenerale.getAttribute("NUMGGDID").toString());
			}
		}

		try {
			apriIscrizioneCM(statoOccCorrente, statoOccManager, cdnLavoratore);
		} catch (Throwable eCM) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"SituazioneAmministrativa" + ":execute() :errore riapertura iscrizione cm", eCM);
		}

		Vector vettParametriImpatti = new Vector();
		vettParametriImpatti.add(0, flgImpattiTirInMobSosp);
		vettParametriImpatti.add(1, flgImpattiTirInMobEff);
		// flag = "S", allora il lavoratore in mobilità in
		// presenza di tirocini può diventare Disoccupato con attività senza contratto
		MovimentoBean m = null;
		int movimentiSize = movimenti.size();

		for (; i >= 0 && i < movimentiSize; i++) {
			ricostruzioneEseguita = true;
			statoOccCorrente = (StatoOccupazionaleBean) statoOccCorrente.clone();
			EventoAmministrativo o = (EventoAmministrativo) movimenti.get(i);
			dataInizioEvento = o.getDataInizio();
			int tipoEvento = o.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.CHIUSURA_DID) {
				ChiusuraDidBean chiusuraDid = (ChiusuraDidBean) o;
				String dataChiusuraDID = chiusuraDid.getDataFine();
				if (dataChiusuraDID != null && !dataChiusuraDID.equals("")) {
					dataInizioEvento = dataChiusuraDID;
				}
			}

			if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.PROROGA
					|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE
					|| tipoEvento == EventoAmministrativo.CESSAZIONE) {
				// gestione dei movimenti (eventi amministrativi) in ricostruzione storia
				m = (MovimentoBean) o;
				// PROROGA SENZA PRECEDENTE PRODUCE UN ERRORE BLOCCANTE
				// TRASFORMAZIONE SENZA PRECEDENTE PRODUCE UN ERRORE BLOCCANTE SOLO SE SI TRATTA DI UN T.D.
				// MODIFICA 03/11/2004 Landi Giovanni
				if (m.getTipoMovimento() == MovimentoBean.PROROGA && m.getCollegato() == null) {
					throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA);
				}

				if (m.getTipoMovimento() == MovimentoBean.TRASFORMAZIONE
						&& Contratto.getTipoContratto(m) == Contratto.DIP_TD && m.getCollegato() == null) {
					throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA);
				}

				String codStatoAtto = (String) m.getAttribute("codStatoAtto");
				// controllo sullo stato dell'atto
				if (!(m.inInserimento() || m.virtuale()) && !codStatoAtto.equals("PR"))
					continue;
			}

			// GESTIONE IMPATTI ALL'INIZIO DELL'ANNO SE IL FLGIMPATTITRAANNI = "S"
			if (flgImpattiTraAnni.equals("S")) {
				annoInizioEvento = DateUtils.getAnno(dataInizioEvento);
				if (annoRicostruzioneCorrente == -1) {
					annoRicostruzioneCorrente = annoInizioEvento;
					annoRicostruzionePrec = DateUtils.getAnno(dataInizioSoIniziale);
				} else {
					annoRicostruzioneCorrente = annoInizioEvento;
				}

				if (annoRicostruzioneCorrente > annoRicostruzionePrec) {
					// Trovato evento amministrativo nell'anno successivo. In questo caso
					// bisogna determinare lo stato occupazionale all'inizio dell'anno.
					for (int contAnno = annoRicostruzionePrec + 1; contAnno <= annoRicostruzioneCorrente; contAnno++) {
						dataCalcoloInizioAnno = "01/01/" + contAnno;
						if (esistonoStatiOccManuali
								&& DateUtils.compare(dataCalcoloInizioAnno, DateUtils.getNow()) <= 0) {
							boolean esciSOccMan = false;
							for (indiceStatoOccManuali = 0; (!esciSOccMan
									&& indiceStatoOccManuali < sizeStatiOccManuali); indiceStatoOccManuali++) {
								StatoOccupazionaleBean statoOccManualeCurr = getStatoOccupazionaliManuali(
										indiceStatoOccManuali);
								boolean statoOccInserito = statoOccManualeCurr.containsAttribute("s_occ_inserito");
								if (!statoOccInserito) {
									dataStatoOccupazionaleManuale = statoOccManualeCurr.getDataInizio();
									annoStatoOccManuale = DateUtils.getAnno(dataStatoOccupazionaleManuale);
									if (annoStatoOccManuale < contAnno) {
										StatoOccupazionaleBean nuovoStatoOccBean = inserisciStatoOccupazionaleManuale(
												statoOccCorrente, statoOccManualeCurr, cdnLavoratore,
												dataStatoOccupazionaleManuale);
										statoOccCorrente = nuovoStatoOccBean;
										statiOccupazionaliCreati.add(nuovoStatoOccBean);
										statoOccManualeCurr.setAttribute("s_occ_inserito", "true");
									} else {
										esciSOccMan = true;
									}
								}
							}
						}

						// bisogna rideterminare lo stato occupazionale all'inizio del nuovo anno
						if (DateUtils.compare(dataCalcoloInizioAnno, DateUtils.getNow()) <= 0
								&& annoRicostruzioneCorrente > annoPrec297
								&& DateUtils.compare(dataCalcoloInizioAnno, dataPrec297) >= 0) {
							// statoOccCorrente viene aggiunto nel vettore statiOccupazionaliCreati
							// se lo stato occupazionale all'inizio del nuovo anno è cambiato
							statoOccCorrente = impattiACavalloDiAnni(statoOccCorrente, dataCalcoloInizioAnno, movimenti,
									statoOccManager);
							try {
								if (statoOccCorrente.ischanged()) {
									chiudiIscrizioneCM(statoOccCorrente, statoOccManager, cdnLavoratore);
								}
							} catch (Throwable eCM) {
								it.eng.sil.util.TraceWrapper.debug(_logger,
										"SituazioneAmministrativa" + ":execute() :errore chiusura iscrizione cm", eCM);
							}
						}
					}
					annoRicostruzionePrec = annoRicostruzioneCorrente;
				}
			}

			if (esistonoStatiOccManuali) {
				String dataControlloEvento = dataInizioEvento;
				if (tipoEvento == EventoAmministrativo.CESSAZIONE) {
					dataControlloEvento = DateUtils.giornoSuccessivo(dataInizioEvento);
				}
				if (DateUtils.compare(dataControlloEvento, dataInizioUltimoSOccManuale) == 0) {
					if (!esisteWarningEventoSOccManuale) {
						addWarning(MessageCodes.StatoOccupazionale.DATA_ST_OCCUPAZ_MANUALE_UGUALE_EVENTO);
						esisteWarningEventoSOccManuale = true;
					}
				}
				boolean esciSOccMan = false;
				for (indiceStatoOccManuali = 0; (!esciSOccMan
						&& indiceStatoOccManuali < sizeStatiOccManuali); indiceStatoOccManuali++) {
					StatoOccupazionaleBean statoOccManualeCurr = getStatoOccupazionaliManuali(indiceStatoOccManuali);
					boolean statoOccInserito = statoOccManualeCurr.containsAttribute("s_occ_inserito");
					if (!statoOccInserito) {
						dataStatoOccupazionaleManuale = statoOccManualeCurr.getDataInizio();
						// devo inserire lo stato occupazionale manuale in quanto ho trovato un evento successivo alla
						// data inizio
						// dello stato occupazionale manuale
						if (tipoEvento == EventoAmministrativo.CESSAZIONE) {
							if (DateUtils.compare(dataInizioEvento, dataStatoOccupazionaleManuale) >= 0) {
								StatoOccupazionaleBean nuovoStatoOccBean = inserisciStatoOccupazionaleManuale(
										statoOccCorrente, statoOccManualeCurr, cdnLavoratore,
										dataStatoOccupazionaleManuale);
								statoOccCorrente = nuovoStatoOccBean;
								statiOccupazionaliCreati.add(nuovoStatoOccBean);
								statoOccManualeCurr.setAttribute("s_occ_inserito", "true");
							} else {
								esciSOccMan = true;
							}
						} else {
							if (DateUtils.compare(dataInizioEvento, dataStatoOccupazionaleManuale) > 0) {
								StatoOccupazionaleBean nuovoStatoOccBean = inserisciStatoOccupazionaleManuale(
										statoOccCorrente, statoOccManualeCurr, cdnLavoratore,
										dataStatoOccupazionaleManuale);
								statoOccCorrente = nuovoStatoOccBean;
								statiOccupazionaliCreati.add(nuovoStatoOccBean);
								statoOccManualeCurr.setAttribute("s_occ_inserito", "true");
							} else {
								esciSOccMan = true;
							}
						}
					}
				}
			}

			// GESTIONE MOBILITA E FINE DELLA MOBILITA (MOBILITABEAN E CHIUSURAMOBILITABEAN)
			if (tipoEvento == EventoAmministrativo.MOBILITA) {
				nuovoStatoOcc = gestisciMobilitaInRicostruzione(o, statoOccCorrente, i, vettParametriImpatti,
						statoOccManager);
				if (nuovoStatoOcc.ischanged()) {
					statoOccCorrente = nuovoStatoOcc;
					statiOccupazionaliCreati.add(nuovoStatoOcc);
					try {
						apriIscrizioneCM(nuovoStatoOcc, statoOccManager, cdnLavoratore);
					} catch (Throwable eCM) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"SituazioneAmministrativa" + ":execute() :errore riapertura iscrizione cm", eCM);
					}
				}
				continue;
			}

			if (tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA) {
				statoOccCorrente = gestisciChiusuraMobilitaInRicostruzione(o, statoOccCorrente, cdnLavoratore,
						statoOccManager);
				try {
					if (statoOccCorrente.ischanged()) {
						chiudiIscrizioneCM(statoOccCorrente, statoOccManager, cdnLavoratore);
					}
				} catch (Throwable eCM) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"SituazioneAmministrativa" + ":execute() :errore chiusura iscrizione cm", eCM);
				}
				continue;
			}

			// GESTIONE EVENTO AMMINISTRATIVO DID IN RICOSTRUZIONE STORIA
			if (tipoEvento == EventoAmministrativo.DID) {
				DidBean db = (DidBean) o;
				// se la did è precedente alla data 30/01/2003 oppure alla data presente in configurazione, allora
				// l'evento
				// amministrativo non ha impatti nella ricostruzione
				if (DateUtils.compare(db.getDataInizio(), dataPrec297) < 0) {
					nuovoStatoOcc = statoOccCorrente;
				} else {
					nuovoStatoOcc = controllaDidPrecInRicostruzione(o, statoOccCorrente);
					if (nuovoStatoOcc.ischanged()) {
						statoOccCorrente = nuovoStatoOcc;
						statiOccupazionaliCreati.add(nuovoStatoOcc);
						try {
							chiudiIscrizioneCM(nuovoStatoOcc, statoOccManager, cdnLavoratore);
						} catch (Throwable eCM) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"SituazioneAmministrativa" + ":execute() :errore chiusura iscrizione cm", eCM);
						}
					}
					nuovoStatoOcc = gestisciDidInRicostruzione(o, statoOccCorrente, i, statoOccManager);
					if (nuovoStatoOcc.ischanged()) {
						statoOccCorrente = nuovoStatoOcc;
						statiOccupazionaliCreati.add(nuovoStatoOcc);
						try {
							apriIscrizioneCM(nuovoStatoOcc, statoOccManager, cdnLavoratore);
						} catch (Throwable eCM) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"SituazioneAmministrativa" + ":execute() :errore riapertura iscrizione cm", eCM);
						}
					}
				}
				continue;
			}

			// GESTIONE EVENTO AMMINISTRATIVO CHIUSURA_DID IN RICOSTRUZIONE STORIA
			if (tipoEvento == EventoAmministrativo.CHIUSURA_DID) {
				ChiusuraDidBean chiusuraDid = (ChiusuraDidBean) o;
				_logger.debug("SituazioneAmministrativa.ricostruizioneStoria():ChiusuraDID:" + chiusuraDid);

				// se la chiusura della did è precedente alla data 30/01/2003 oppure alla data presente in
				// configurazione,
				// allora l'evento amministrativo non ha impatti nella ricostruzione
				String dataChiusuraDid = chiusuraDid.getDataFine();
				if (dataChiusuraDid == null || dataChiusuraDid.equals("")
						|| DateUtils.compare(dataChiusuraDid, dataPrec297) < 0) {
					nuovoStatoOcc = statoOccCorrente;
				} else {
					// gestisci la chiusura della did
					statoOccCorrente = gestisciChiusuraDidInRicostruzione(o, statoOccCorrente, vettParametriImpatti);
					try {
						if (statoOccCorrente.ischanged()) {
							chiudiIscrizioneCM(statoOccCorrente, statoOccManager, cdnLavoratore);
						}
					} catch (Throwable eCM) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"SituazioneAmministrativa" + ":execute() :errore chiusura iscrizione cm", eCM);
					}
				}
				continue;
			}

			// controllo sulla data futura
			if (DateUtils.compare(DateUtils.getNow(), m.getDataInizio()) < 0) {
				continue;
			}

			dataRif = m.getDataInizio();
			// se il movimento è precedente alla data 30/01/2003 oppure alla data presente in configurazione, allora
			// l'evento
			// amministrativo non ha impatti nella ricostruzione storia
			if (DateUtils.compare(dataRif, dataPrec297) < 0) {
				nuovoStatoOcc = statoOccCorrente;
				continue;
			}

			// GESTIONE MOVIMENTO IN RICOSTRUZIONE A SECONDA DEL TIPO MOVIMENTO
			nuovoStatoOcc = gestisciMovimentoInRicostruzione(m, statoOccCorrente, cdnLavoratore, statoOccManager,
					dataRif, vettParametriImpatti, i);

			if (m.inInserimento())
				this.statoOccupazionaleMovimentoGestito = nuovoStatoOcc;
			else if (!m.virtuale()) // e' un movimento reale presente sul db e va aggiornato
				DBStore.aggiornaMovimento(m, requestContainer, txExecutor);
			if (m.precInInserimento()) {
				indiceMovimentoPrec = i;
				// e' stato aggiornato e quindi bisogna aggiornare il numklo nel modulo esterno
				BigDecimal numKlo = (BigDecimal) m.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
				if (requestContainer.getServiceRequest()
						.containsAttribute(MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO)) {
					requestContainer.getServiceRequest().updAttribute(MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO,
							numKlo.add(new BigDecimal(1)));
				} else {
					requestContainer.getServiceRequest().setAttribute(MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO,
							numKlo.add(new BigDecimal(1)));
				}
			}
			if (nuovoStatoOcc.ischanged()) {
				statoOccCorrente = nuovoStatoOcc;
				statiOccupazionaliCreati.add(nuovoStatoOcc);
				try {
					chiudiIscrizioneCM(nuovoStatoOcc, statoOccManager, cdnLavoratore);
				} catch (Throwable eCM) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"SituazioneAmministrativa" + ":execute() :errore chiusura iscrizione cm", eCM);
				}
			}
		}

		// determinazione eventuale dello stato occupazionale all'inizio dell'anno corrente
		if (flgImpattiTraAnni.equals("S")) {
			if (ricostruzioneEseguita
					|| requestContainer.getServiceRequest().containsAttribute("INS_SOCC_MANUALE_RICALCOLA")) {
				int annoCorrente = DateUtils.getAnno(DateUtils.getNow());
				int annoUltimoEvento = 0;
				if (ricostruzioneEseguita) {
					EventoAmministrativo objUltimoEv = (EventoAmministrativo) movimenti.get(movimentiSize - 1);
					annoUltimoEvento = DateUtils.getAnno(objUltimoEv.getDataInizio());
				} else {
					annoUltimoEvento = annoSoccIniziale;
				}
				if (annoCorrente > annoUltimoEvento) {
					for (int contAnno = annoUltimoEvento + 1; contAnno <= annoCorrente; contAnno++) {
						dataCalcoloInizioAnno = "01/01/" + contAnno;
						if (esistonoStatiOccManuali) {
							boolean esciSOccMan = false;
							for (indiceStatoOccManuali = 0; (!esciSOccMan
									&& indiceStatoOccManuali < sizeStatiOccManuali); indiceStatoOccManuali++) {
								StatoOccupazionaleBean statoOccManualeCurr = getStatoOccupazionaliManuali(
										indiceStatoOccManuali);
								boolean statoOccInserito = statoOccManualeCurr.containsAttribute("s_occ_inserito");
								if (!statoOccInserito) {
									dataStatoOccupazionaleManuale = statoOccManualeCurr.getDataInizio();
									annoStatoOccManuale = DateUtils.getAnno(dataStatoOccupazionaleManuale);
									if (annoStatoOccManuale < contAnno) {
										StatoOccupazionaleBean nuovoStatoOccBean = inserisciStatoOccupazionaleManuale(
												statoOccCorrente, statoOccManualeCurr, cdnLavoratore,
												dataStatoOccupazionaleManuale);
										statoOccCorrente = nuovoStatoOccBean;
										statiOccupazionaliCreati.add(nuovoStatoOccBean);
										statoOccManualeCurr.setAttribute("s_occ_inserito", "true");
									} else {
										esciSOccMan = true;
									}
								}
							}
						}

						if (DateUtils.compare(dataCalcoloInizioAnno, dataPrec297) >= 0) {
							// statoOccCorrente viene aggiunto nel vettore statiOccupazionaliCreati
							// se lo stato occupazionale all'inizio del nuovo anno è cambiato
							statoOccCorrente = impattiACavalloDiAnni(statoOccCorrente, dataCalcoloInizioAnno, movimenti,
									statoOccManager);
							try {
								if (statoOccCorrente.ischanged()) {
									chiudiIscrizioneCM(statoOccCorrente, statoOccManager, cdnLavoratore);
								}
							} catch (Throwable eCM) {
								it.eng.sil.util.TraceWrapper.debug(_logger,
										"SituazioneAmministrativa" + ":execute() :errore chiusura iscrizione cm", eCM);
							}
						}
					}
				}
			}
		}

		if (esistonoStatiOccManuali) {
			boolean esciSOccMan = false;
			for (indiceStatoOccManuali = 0; (!esciSOccMan
					&& indiceStatoOccManuali < sizeStatiOccManuali); indiceStatoOccManuali++) {
				StatoOccupazionaleBean statoOccManualeCurr = getStatoOccupazionaliManuali(indiceStatoOccManuali);
				boolean statoOccInserito = statoOccManualeCurr.containsAttribute("s_occ_inserito");
				if (!statoOccInserito) {
					dataStatoOccupazionaleManuale = statoOccManualeCurr.getDataInizio();
					// devo inserire lo stato occupazionale manuale alla fine perché successivo ad ogni evento
					// amministrativo
					if (DateUtils.compare(DateUtils.getNow(), dataStatoOccupazionaleManuale) >= 0) {
						if (statoOccCorrente != null && DateUtils.compare(dataStatoOccupazionaleManuale,
								statoOccCorrente.getDataInizio()) >= 0) {
							StatoOccupazionaleBean nuovoStatoOccBean = inserisciStatoOccupazionaleManuale(
									statoOccCorrente, statoOccManualeCurr, cdnLavoratore,
									dataStatoOccupazionaleManuale);
							statoOccCorrente = nuovoStatoOccBean;
							statiOccupazionaliCreati.add(nuovoStatoOccBean);
							statoOccManualeCurr.setAttribute("s_occ_inserito", "true");
						}
					} else {
						esciSOccMan = true;
					}
				}
			}
		}

		// se provengo dalla did ho movimentoIniziale null
		if (movimentoIniziale != null) {
			if (movimenti.get(ind) instanceof MovimentoBean
					&& ((MovimentoBean) movimenti.get(ind)).getPrgStatoOccupaz() != null)
				movimentoIniziale.updAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ,
						((MovimentoBean) movimenti.get(ind)).getPrgStatoOccupaz());
		}
		if (ind >= 0) {
			this.statoOccupazionaleMovimentoGestito = ((EventoAmministrativo) movimenti.get(ind))
					.getStatoOccupazionale();
			this.movimentoGestito = (EventoAmministrativo) movimenti.get(ind);
		}
		// 23-11-2004 alla fine della ricostruzione storia cancello gli stati
		// occupazionali che hanno la stessa data inizio (prendo il max
		// PRGSTATOOCCUPAZ). I movimenti che facevano riferimento agli stati
		// occupazionali da cancellare devono essere associati allo stato
		// occupazionale con PRGSTATOOCCUPAZ max.
		Vector statiOccupazionaliNew = DBLoad.getStatiOccupazionali(cdnLavoratore, txExecutor);
		SourceBean tmp1 = null;
		SourceBean tmp2 = null;
		Vector listStOccDaCancellare = new Vector();
		for (int iCont = 0; iCont < statiOccupazionaliNew.size(); iCont++) {
			if (iCont < statiOccupazionaliNew.size() - 1) {
				tmp1 = (SourceBean) statiOccupazionaliNew.get(iCont);
				tmp2 = (SourceBean) statiOccupazionaliNew.get(iCont + 1);
				if (tmp1.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString()
						.equals(tmp2.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString())) {
					listStOccDaCancellare.add(tmp1);
				}
			}
		}

		Vector didsNew = null;

		// listStOccDaCancellare vanno tolti dagli stati occupazionali creati statiOccupazionaliCreati
		if (listStOccDaCancellare.size() > 0) {
			if (statiOccupazionaliCreati.size() > 0) {
				statiOccupazionaliCreati = normalizzaStatiOccupazionaliCreati(listStOccDaCancellare);
			}
			if (indiceMovimentoPrec < 0) {
				for (int index = 0; index < movimentiSize; index++) {
					EventoAmministrativo evento = (EventoAmministrativo) movimenti.get(index);
					int tipoEventoAmministrativo = evento.getTipoEventoAmministrativo();
					if (tipoEventoAmministrativo == EventoAmministrativo.AVVIAMENTO
							|| tipoEventoAmministrativo == EventoAmministrativo.PROROGA
							|| tipoEventoAmministrativo == EventoAmministrativo.TRASFORMAZIONE
							|| tipoEventoAmministrativo == EventoAmministrativo.CESSAZIONE) {
						m = (MovimentoBean) evento;
						if (m.precInInserimento()) {
							indiceMovimentoPrec = index;
							break;
						}
					}
				}
			}
			didsNew = DBLoad.getDichiarazioniDisponibilitaProtocollate(cdnLavoratore, "01/01/0001", txExecutor);
			Vector movimentiApertiNew = DBLoad.getMovimentiLavoratoreProtocollati(cdnLavoratore, txExecutor);
			Vector rowsMobilitaNew = null;
			ListaMobilita mobilitaNew = new ListaMobilita(cdnLavoratore, txExecutor);
			rowsMobilitaNew = mobilitaNew.getMobilita();
			resettaStatiOccupazionaliNormalizzati(statiOccupazionaliNew, listStOccDaCancellare, movimentiApertiNew,
					didsNew, rowsMobilitaNew);
			// Eliminando gli stati occupazionali nella stessa data, si possono creare situazioni
			// di stati occupazionali uguali consecutivi nelle date; bisogna crearne uno solo, mettendo la data fine
			// del primo stato occupazionale uguale alla data fine dell'ultimo stato occupazionale
			// (01/01/2004 al 30/04/2004 Occupato, 01/05/2004 Occupato)
			StatoOccupazionaleBean.normalizzaStatiOccupazionali(statiOccupazionaliNew, listStOccDaCancellare,
					statiOccupazionaliFinali);
			statiOccupazionaliFinali = cancellaStatiOccupazionaliUgualiConsecutivi(statiOccupazionaliFinali,
					movimentiApertiNew, didsNew, rowsMobilitaNew);
		} else {
			statiOccupazionaliFinali = statiOccupazionaliNew;
		}

		StatoOccupazionaleBean sOccFinale = null;
		if (statiOccupazionaliFinali.size() > 0) {
			SourceBean sbOccAperto = (SourceBean) statiOccupazionaliFinali.get(statiOccupazionaliFinali.size() - 1);
			// Gestione eventuale chiusura e riapertura iscrizioni al collocamento mirato
			if (Sottosistema.CM.isOn()) {
				LavoratoreBean lav = new LavoratoreBean("CM", cdnLavoratore, txExecutor);
				List iscrCM = lav.getCM();
				if (iscrCM.size() > 0) {
					int risultato = CmBean.gestisciChiusuraRiaperturaCM(statiOccupazionaliFinali, cdnLavoratore, iscrCM,
							txExecutor);
					if (risultato == 1) {
						// c'è stata chiusura CM
						addWarning(MessageCodes.General.UPDATE_SUCCESS, "Effettuata chiusura CM");
					} else {
						if (risultato == 2) {
							// c'è stata riapertura CM
							addWarning(MessageCodes.General.UPDATE_SUCCESS, "Effettuata riapertura CM");
						}
					}
				}
			}
			sOccFinale = new StatoOccupazionaleBean(sbOccAperto);
			this.statoOccupazionaleAperto = sOccFinale;
		} else {
			this.statoOccupazionaleAperto = statoOccCorrente;
		}
		// se ci sono patti bisogna associare lo stato occupazionale del relativo periodo
		// tutto questo puo' essere valido in situazioni ideali ma non in tutti i casi
		riassociaPatti(statiOccupazionaliFinali);

		if (this.statoOccupazionaleAperto != null) {
			if (this.statoOccupazionaleAperto.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
					|| this.statoOccupazionaleAperto.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
				// Se il lavoratore alla fine del ricalcolo si trova in 150, allora al massimo un patto deve essere
				// aperto; nel caso ci fossero più patti,
				// viene mantenuto aperto sempre e comunque l'ultimo, se l'ultimo non è stato già chiuso dall'operatore
				// e non per avviamento
				controllaPattoAperto();
				// eventuale chiusura accordo generico
				Patto ptAccordo = new Patto();
				ptAccordo.controllaChiusuraAccordoGenerico(cdnLavoratore, requestContainer, txExecutor);
			} else {
				// Se il lavoratore alla fine del ricalcolo non si trova in 150, allora non posso avere patti aperti
				controllaChiusuraPatti();
				// eventuale riapertura accordo generico
				Patto ptAccordo = new Patto();
				ptAccordo.controllaAperturaAccordoGenerico(cdnLavoratore, requestContainer, txExecutor);
			}
		}

		boolean flag = false;
		if (flag) {
			throw new Exception("interrotto per test  *******************************");
		}
		return statoOccupazionaleAperto;
	}

	/**
	 * riporto in ricostruzione storia lo stato occupazionale manuale
	 * 
	 * @param statoOccCorrente
	 * @param statoOccManuale
	 * @param cdnLavoratore
	 * @param dataStatoOccupazionaleManuale
	 * @return
	 * @throws Exception
	 */
	public StatoOccupazionaleBean inserisciStatoOccupazionaleManuale(StatoOccupazionaleBean statoOccCorrente,
			StatoOccupazionaleBean statoOccManuale, Object cdnLavoratore, String dataStatoOccupazionaleManuale)
			throws Exception {
		StatoOccupazionaleBean nuovoStatoOccupazionale = new StatoOccupazionaleBean();
		nuovoStatoOccupazionale.setCdnLavoratore((BigDecimal) cdnLavoratore);
		nuovoStatoOccupazionale.setStatoOccupazionale(statoOccManuale.getStatoOccupazionale());
		nuovoStatoOccupazionale.setCodMonoProvenienza(Properties.PROVENIENZA_STATO_OCC_MANUALE);
		nuovoStatoOccupazionale.setDataInizio(dataStatoOccupazionaleManuale);
		nuovoStatoOccupazionale.setNumMesiSosp(statoOccManuale.getNumMesiSosp());
		nuovoStatoOccupazionale.setIndennizzato(statoOccManuale.getIndennizzato());
		nuovoStatoOccupazionale.setPensionato(statoOccManuale.getPensionato());
		nuovoStatoOccupazionale.setNumAnzianitaPrec297(statoOccManuale.getNumAnzianitaPrec297());
		nuovoStatoOccupazionale.setChanged(true);
		nuovoStatoOccupazionale.setDataCalcoloAnzianita(statoOccManuale.getDataCalcoloAnzianita());
		nuovoStatoOccupazionale.setDataCalcoloMesiSosp(statoOccManuale.getDataCalcoloMesiSosp());
		nuovoStatoOccupazionale.setDataAnzianita(statoOccManuale.getDataAnzianita());
		if (statoOccManuale.getAttribute("cdnutins") != null) {
			nuovoStatoOccupazionale.setAttribute("cdnutins", statoOccManuale.getAttribute("cdnutins"));
		}

		if (statoOccManuale.getAttribute("cdnutmod") != null) {
			nuovoStatoOccupazionale.setAttribute("cdnutmod", statoOccManuale.getAttribute("cdnutmod"));
		}
		if (statoOccManuale.getAttribute("dtmIns") != null) {
			nuovoStatoOccupazionale.setAttribute("dtmIns", statoOccManuale.getAttribute("dtmIns"));
		}
		if (statoOccManuale.getAttribute("dtmMod") != null) {
			nuovoStatoOccupazionale.setAttribute("dtmMod", statoOccManuale.getAttribute("dtmMod"));
		}
		StatoOccupazionaleBean nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoStatoOccupazionale,
				statoOccCorrente);
		DBStore.creaNuovoStatoOccManuale(nuovoStatoOccBean, dataStatoOccupazionaleManuale, requestContainer,
				txExecutor);
		statoOccCorrente = nuovoStatoOccBean;
		statiOccupazionaliCreati.add(nuovoStatoOccBean);
		// Se è Altro o Occupato controllare la chiusura DID e PATTO COME AVVIENE IN InsertStatoOccupazRicalcola
		// Nota: se è l'ultimo stato occupazione rispetto agli eventi, gestire alla fine del for
		if (statoOccManuale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_A
				|| statoOccManuale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_O) {
			String codStatoOccupaz = statoOccManuale.getStatoOccupaz();
			SourceBean deStatoOccupaz = DBLoad.getDeStatoOccupaz(requestContainer.getServiceRequest(), codStatoOccupaz,
					txExecutor);
			String codMotivoFineAtto = (String) deStatoOccupaz.getAttribute("codMotivoFineAtto");
			if (codMotivoFineAtto == null) {
				codMotivoFineAtto = Properties.DECADUTO_PER_AVVIAMENTO;
			}

			SourceBean didAperta = cercaDid(dataStatoOccupazionaleManuale);
			SourceBean pattoAperto = cercaPatto(dataStatoOccupazionaleManuale);
			String dataChiusuraDid = DateUtils.giornoPrecedente(dataStatoOccupazionaleManuale);
			// seleziona il patto aperto
			if (didAperta != null) {
				String dataDichiarazione = (String) didAperta.getAttribute("datDichiarazione");
				if (dataDichiarazione != null && !dataDichiarazione.equals("")
						&& DateUtils.compare(dataDichiarazione, dataChiusuraDid) > 0) {
					dataChiusuraDid = dataDichiarazione;
				}

				DBStore.chiudiDID(didAperta, dataChiusuraDid, codMotivoFineAtto, requestContainer, txExecutor);
				DidBean didApertaBean = new DidBean(didAperta);
				chiudiDidInVettoreDids(didApertaBean.getPrgDichDisponibilita(), dataChiusuraDid);
				aggiornaNumKloDichDispoInMovimenti(didApertaBean,
						new BigDecimal(didApertaBean.getAttribute("numKloDichDisp").toString()), movimenti);
			}
			if (pattoAperto != null) {
				DBStore.chiudiPatto297(pattoAperto, dataChiusuraDid, codMotivoFineAtto, requestContainer, txExecutor);
				aggiornaNumKloPatto(new BigDecimal(pattoAperto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
						dataChiusuraDid,
						new BigDecimal(pattoAperto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
			}
		}
		return nuovoStatoOccBean;
	}

	/**
	 * viene invocato per cancellare stati occupazionali uguali e consecutivi che
	 * 
	 * @param statiOccupazionaliFinali
	 * @param movimentiApertiNew
	 * @param didsNew
	 * @param rowsMobilitaNew
	 * @throws Exception
	 */
	private Vector cancellaStatiOccupazionaliUgualiConsecutivi(Vector statiOccupazionaliFinali,
			Vector movimentiApertiNew, Vector didsNew, Vector rowsMobilitaNew) throws Exception {
		Vector listaStatiUguali = new Vector();
		Vector vettFinaliSOcc = new Vector();
		Vector listaDateAggiornare = new Vector();
		Vector vettCorrispondenzeStatoOccUguali = new Vector();
		String dataAggiornare = "";
		BigDecimal prgStatoOccupaz = null;
		BigDecimal numKlo = null;
		SourceBean tmp1 = null;
		SourceBean tmp2 = null;
		String codStatoOccupaz = "";

		int i = 0;
		int k = 0;
		boolean trovato = false;
		for (int iCont = 0; iCont < statiOccupazionaliFinali.size(); iCont++) {
			trovato = false;
			tmp1 = (SourceBean) statiOccupazionaliFinali.get(iCont);
			prgStatoOccupaz = (BigDecimal) tmp1.getAttribute("prgStatoOccupaz");
			codStatoOccupaz = tmp1.getAttribute("codStatoOccupaz").toString();
			numKlo = (BigDecimal) tmp1.getAttribute("numklostatooccupaz");
			for (int jCont = iCont + 1; jCont < statiOccupazionaliFinali.size(); jCont++) {
				tmp2 = (SourceBean) statiOccupazionaliFinali.get(jCont);
				if (codStatoOccupaz.equals(tmp2.getAttribute("codStatoOccupaz").toString())) {
					if ((tmp2.getAttribute("CODMONOPROVENIENZA") == null) || (!tmp2.getAttribute("CODMONOPROVENIENZA")
							.toString().equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_MANUALE)
							&& !tmp2.getAttribute("CODMONOPROVENIENZA").toString()
									.equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_AGG_MANUALE)
							&& !tmp2.getAttribute("CODMONOPROVENIENZA").toString()
									.equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_REG_AGG_MANUALE))) {
						listaStatiUguali.add(tmp2);
						vettCorrispondenzeStatoOccUguali.add(tmp1);
						dataAggiornare = (String) tmp2.getAttribute(StatoOccupazionaleBean.DB_DAT_FINE);
						tmp2.setAttribute("Stato_Occupazionale_Eliminato", "true");
						trovato = true;
					}
				} else {
					break;
				}
			}
			if (trovato) {
				DBStore.chiudiStatoOcc(new StatoOccupazionaleBean(tmp1), dataAggiornare, requestContainer, txExecutor);
				// devo aggiornare lo stato occupazionale con la nuova data fine
			}
		}

		for (int iCont = 0; iCont < statiOccupazionaliFinali.size(); iCont++) {
			tmp1 = (SourceBean) statiOccupazionaliFinali.get(iCont);
			if (!tmp1.containsAttribute("Stato_Occupazionale_Eliminato")) {
				vettFinaliSOcc.add(tmp1);
			}
		}

		if (listaStatiUguali.size() > 0) {
			// devo eliminare gli stati occupazionali nella lista
			i = 0;
			SourceBean sbApp = null;
			StatoOccupazionaleBean soApp = null;
			BigDecimal prgNewStatoOccupaz = null;
			BigDecimal prgStatoOcc = null;
			for (; i < listaStatiUguali.size(); i++) {
				sbApp = (SourceBean) vettCorrispondenzeStatoOccUguali.get(i);
				soApp = new StatoOccupazionaleBean(sbApp);
				prgNewStatoOccupaz = soApp.getPrgStatoOccupaz();
				SourceBean sb = (SourceBean) listaStatiUguali.get(i);
				StatoOccupazionaleBean so = new StatoOccupazionaleBean(sb);
				prgStatoOcc = so.getPrgStatoOccupaz();
				AllineamentoStatiOccupazionali(this.movimenti, prgStatoOcc, soApp);
				dereferenziaMovimentiNormalizzati(movimentiApertiNew, so, soApp);
				List pattiAssociatiSO = DBLoad.getPattoAssociatoStatoOccupaz(prgStatoOcc, txExecutor);
				if (pattiAssociatiSO.size() > 0) {
					dereferenziaPatti(pattiAssociatiSO, prgNewStatoOccupaz);
				}
				DidBean did = null;
				SourceBean sbDid = null;
				k = 0;
				for (; k < didsNew.size(); k++) {
					sbDid = (SourceBean) didsNew.get(k);
					did = new DidBean(sbDid);
					if (did.getPrgStatoOccupaz() != null && did.getPrgStatoOccupaz().equals(prgStatoOcc)) {
						_logger.debug(
								"SituazioneAmministrativa.resettaStatiOccupazionaliNormalizzati(): e' necessario aggiornare la did storicizzata "
										+ did);

						numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
						numKlo = numKlo.add(new BigDecimal(1));
						did.getSource().updAttribute("numKloDichDisp", numKlo);
						_logger.debug("SituazioneAmministrativa.gestisciDid(): viene aggiornata la did " + did);

						DBStore.riassociaDIDSOccInStoria(did.getPrgDichDisponibilita(), prgNewStatoOccupaz, numKlo,
								requestContainer, txExecutor);
						if (sbDid.containsAttribute("prgStatoOccupaz")) {
							sbDid.updAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
						} else {
							sbDid.setAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
						}
						if (sbDid.containsAttribute("numklodichdisp")) {
							sbDid.updAttribute("numklodichdisp", numKlo);
						} else {
							sbDid.setAttribute("numklodichdisp", numKlo);
						}
					}
				}

				MobilitaBean mobilita = null;
				SourceBean sbMobilita = null;
				k = 0;
				for (; k < rowsMobilitaNew.size(); k++) {
					sbMobilita = (SourceBean) rowsMobilitaNew.get(k);
					mobilita = new MobilitaBean(sbMobilita);
					if (mobilita.getPrgStatoOccupaz() != null && mobilita.getPrgStatoOccupaz().equals(prgStatoOcc)) {
						_logger.debug(
								"SituazioneAmministrativa.resettaStatiOccupazionaliNormalizzati(): e' necessario aggiornare la mobilita storicizzata "
										+ mobilita);

						mobilita.aggiornaStatoOccupaz(prgNewStatoOccupaz, requestContainer, txExecutor);
						if (sbMobilita.containsAttribute("prgStatoOccupaz")) {
							sbMobilita.updAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
						} else {
							sbMobilita.setAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
						}
						if (sbMobilita.containsAttribute("numKloMobIscr")) {
							sbMobilita.updAttribute("numKloMobIscr",
									(BigDecimal) mobilita.getAttribute("numKloMobIscr"));
						} else {
							sbMobilita.setAttribute("numKloMobIscr",
									(BigDecimal) mobilita.getAttribute("numKloMobIscr"));
						}
					}
				}
				if (this.getPrgStatoOccMovNonCollegato() != null
						&& this.getPrgStatoOccMovNonCollegato().equals(prgStatoOcc.toString())) {
					setPrgStatoOccMovNonCollegato(prgNewStatoOccupaz.toString());
				}
				DBStore.cancellaStatoOccupazionale(prgStatoOcc, txExecutor);
			}
		}
		return vettFinaliSOcc;
	}

	private void AllineamentoStatiOccupazionali(List movimenti, BigDecimal prgStatoOcc, StatoOccupazionaleBean soApp)
			throws Exception {
		StatoOccupazionaleBean statoOccGestito = this.statoOccupazionaleMovimentoGestito;
		EventoAmministrativo evento = null;
		StatoOccupazionaleBean statoOccCorrente = null;
		if (statoOccGestito != null && statoOccGestito.getPrgStatoOccupaz().equals(prgStatoOcc)) {
			this.statoOccupazionaleMovimentoGestito = soApp;
		}
		int movimentiSize = movimenti.size();
		for (int j = 0; j < movimentiSize; j++) {
			evento = (EventoAmministrativo) movimenti.get(j);
			statoOccCorrente = evento.getStatoOccupazionale();
			if (statoOccCorrente != null && statoOccCorrente.getPrgStatoOccupaz().equals(prgStatoOcc)) {
				evento.setStatoOccupazionale(soApp);
			}
		}
	}

	/**
	 * Questo metodo restituisce la lista degli stati occupazionali creati nella ricostruzione storia senza gli stati
	 * occupazionali presenti nel vettore lista preso come parametro (il vettore contiene gli stati occupazionali da
	 * cancellare, cioè quelli con la stessa data inizio di un altro stato occupazionale ma con prgStatoOccupaz minore)
	 * 
	 * @param lista
	 * @return
	 * @throws Exception
	 */
	private List normalizzaStatiOccupazionaliCreati(Vector lista) throws Exception {
		boolean trovato = false;
		List statiOccupazionaliCreatiNorm = new ArrayList();
		SourceBean sb = null;
		SourceBean sbOld = null;
		StatoOccupazionaleBean soOld = null;
		Object prgsoOld = null;
		Object prgso = null;
		for (int i = 0; i < statiOccupazionaliCreati.size(); i++) {
			trovato = false;
			sbOld = (SourceBean) statiOccupazionaliCreati.get(i);
			soOld = new StatoOccupazionaleBean(sbOld);
			prgsoOld = sbOld.getAttribute("prgStatoOccupaz");
			for (int j = 0; j < lista.size(); j++) {
				sb = (SourceBean) lista.get(j);
				prgso = sb.getAttribute("prgStatoOccupaz");
				if (prgso.equals(prgsoOld)) {
					trovato = true;
				}
			}
			if (!trovato) {
				statiOccupazionaliCreatiNorm.add(soOld);
			}
		}
		return statiOccupazionaliCreatiNorm;
	}

	/**
	 * Questo metodo riassocia i movimenti, patti, e did al nuovo stato occupazionale (quello che ha la stessa data
	 * Inizio dello stato occupazionale da cancellare) e infine esegue la cancellazione nella tabella am_stato_occupaz
	 * 
	 * @param listaCompleta
	 * @param listaCancella
	 * @throws Exception
	 */
	private void resettaStatiOccupazionaliNormalizzati(Vector listaCompleta, Vector listaCancella,
			Vector movimentiApertiNew, Vector dids, Vector listaMobilita) throws Exception {
		BigDecimal prgStatoOcc = null;
		int i = 0;
		int j = 0;
		int posizione = 0;
		SourceBean sbApp = null;
		BigDecimal prgNewStatoOccupaz = null;
		StatoOccupazionaleBean soApp = null;
		SourceBean sbCancella = null;
		SourceBean sbCompleta = null;
		String dataInizioCancella = "";
		String dataInizioCompleta = "";
		// devo ricavare il nuovo stato occupazionale(quello che ha la
		// stessa data Inizio, elemento che appartiene al
		// vettore listaCompleta e non appartiene al vettore listaCancella)
		Vector vettCorrispondenzeStatoOcc = new Vector();
		for (; i < listaCancella.size(); i++) {
			sbCancella = (SourceBean) listaCancella.get(i);
			dataInizioCancella = sbCancella.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString();
			for (; j < listaCompleta.size(); j++) {
				sbCompleta = (SourceBean) listaCompleta.get(j);
				dataInizioCompleta = sbCompleta.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString();
				if (dataInizioCancella.equals(dataInizioCompleta)) {
					posizione = j;
				}
			}
			sbCompleta = (SourceBean) listaCompleta.get(posizione);
			vettCorrispondenzeStatoOcc.add(sbCompleta);
		}
		i = 0;
		for (; i < listaCancella.size(); i++) {
			sbApp = (SourceBean) vettCorrispondenzeStatoOcc.get(i);
			soApp = new StatoOccupazionaleBean(sbApp);
			prgNewStatoOccupaz = soApp.getPrgStatoOccupaz();
			SourceBean sb = (SourceBean) listaCancella.get(i);
			StatoOccupazionaleBean so = new StatoOccupazionaleBean(sb);
			prgStatoOcc = so.getPrgStatoOccupaz();
			AllineamentoStatiOccupazionali(this.movimenti, prgStatoOcc, soApp);
			dereferenziaMovimentiNormalizzati(movimentiApertiNew, so, soApp);
			List pattiAssociatiSO = DBLoad.getPattoAssociatoStatoOccupaz(prgStatoOcc, txExecutor);
			if (pattiAssociatiSO.size() > 0) {
				dereferenziaPatti(pattiAssociatiSO, prgNewStatoOccupaz);
			}

			DidBean did = null;
			SourceBean sbDid = null;
			for (int k = 0; k < dids.size(); k++) {
				sbDid = (SourceBean) dids.get(k);
				did = new DidBean(sbDid);
				if (did.getPrgStatoOccupaz() != null && did.getPrgStatoOccupaz().equals(prgStatoOcc)) {
					_logger.debug(
							"SituazioneAmministrativa.resettaStatiOccupazionaliNormalizzati(): e' necessario aggiornare la did storicizzata "
									+ did);

					BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
					numKlo = numKlo.add(new BigDecimal(1));
					did.getSource().updAttribute("numKloDichDisp", numKlo);
					_logger.debug("SituazioneAmministrativa.gestisciDid(): viene aggiornata la did " + did);

					DBStore.riassociaDIDSOccInStoria(did.getPrgDichDisponibilita(), prgNewStatoOccupaz, numKlo,
							requestContainer, txExecutor);
					if (sbDid.containsAttribute("prgStatoOccupaz")) {
						sbDid.updAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
					} else {
						sbDid.setAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
					}
					if (sbDid.containsAttribute("numKloDichDisp")) {
						sbDid.updAttribute("numKloDichDisp", numKlo);
					} else {
						sbDid.setAttribute("numKloDichDisp", numKlo);
					}
				}
			}

			MobilitaBean mobilita = null;
			SourceBean sbMobilita = null;
			for (int k = 0; k < listaMobilita.size(); k++) {
				sbMobilita = (SourceBean) listaMobilita.get(k);
				mobilita = new MobilitaBean(sbMobilita);
				if (mobilita.getPrgStatoOccupaz() != null && mobilita.getPrgStatoOccupaz().equals(prgStatoOcc)) {
					_logger.debug(
							"SituazioneAmministrativa.resettaStatiOccupazionaliNormalizzati(): e' necessario aggiornare la mobilita storicizzata "
									+ mobilita);

					mobilita.aggiornaStatoOccupaz(prgNewStatoOccupaz, requestContainer, txExecutor);
					if (sbMobilita.containsAttribute("prgStatoOccupaz")) {
						sbMobilita.updAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
					} else {
						sbMobilita.setAttribute("prgStatoOccupaz", prgNewStatoOccupaz);
					}
					if (sbMobilita.containsAttribute("numKloMobIscr")) {
						sbMobilita.updAttribute("numKloMobIscr", (BigDecimal) mobilita.getAttribute("numKloMobIscr"));
					} else {
						sbMobilita.setAttribute("numKloMobIscr", (BigDecimal) mobilita.getAttribute("numKloMobIscr"));
					}
				}
			}
			if (this.getPrgStatoOccMovNonCollegato() != null
					&& this.getPrgStatoOccMovNonCollegato().equals(prgStatoOcc.toString())) {
				setPrgStatoOccMovNonCollegato(prgNewStatoOccupaz.toString());
			}
			DBStore.cancellaStatoOccupazionale(prgStatoOcc, txExecutor);
		}
	}

	/**
	 * Aggiorna i movimenti che hanno prgStatoOccupaz = prgStatoOcc, settando questo campo a null e aggiornando con il
	 * nuovo stato occupazionale
	 * 
	 * @param movimenti
	 * @param statoOccupazionale
	 * @throws Exception
	 */
	private void dereferenziaMovimentiNormalizzati(Vector movimentiNew, StatoOccupazionaleBean statoOccupazionale,
			StatoOccupazionaleBean soNew) throws Exception {
		int i = 0;
		BigDecimal prgStatoOcc = statoOccupazionale.getPrgStatoOccupaz();
		BigDecimal prgso = null;
		_logger.debug(
				"SituazioneAmministrativa.dereferenziaMovimentiNormalizzati():ricerca per prgStOcc:" + prgStatoOcc);

		int movimentiNewSize = movimentiNew.size();
		Object prgMovimentoPrec = null;
		MovimentoBean m = null;
		if (indiceMovimentoPrec >= 0) {
			EventoAmministrativo o = (EventoAmministrativo) movimenti.get(indiceMovimentoPrec);
			m = (MovimentoBean) o;
			prgMovimentoPrec = m.getPrgMovimento();
		}
		for (; i < movimentiNewSize; i++) {
			SourceBean sbEvento = (SourceBean) movimentiNew.get(i);
			if (sbEvento.getAttribute("codtipomov").toString().equals("AVV")
					|| sbEvento.getAttribute("codtipomov").toString().equals("TRA")
					|| sbEvento.getAttribute("codtipomov").toString().equals("PRO")
					|| sbEvento.getAttribute("codtipomov").toString().equals("CES")) {
				MovimentoBean mb = new MovimentoBean(sbEvento);
				if (mb.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ) == null)
					continue;
				Object prgMovimento = mb.getPrgMovimento();
				prgso = (BigDecimal) mb.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ);
				if (prgStatoOcc.equals(prgso)) {
					_logger.debug("SituazioneAmministrativa.dereferenziaMovimentiNormalizzati():deassociato:" + mb);

					mb.setStatoOccupazionale(soNew);
					DBStore.aggiornaMovimento(mb, requestContainer, txExecutor);
					if (sbEvento.containsAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ)) {
						sbEvento.updAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ, soNew.getPrgStatoOccupaz());
					} else {
						sbEvento.setAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ, soNew.getPrgStatoOccupaz());
					}
					if (sbEvento.containsAttribute(MovimentoBean.DB_NUM_K_LOCK)) {
						sbEvento.updAttribute(MovimentoBean.DB_NUM_K_LOCK,
								(BigDecimal) mb.getAttribute(MovimentoBean.DB_NUM_K_LOCK));
					} else {
						sbEvento.setAttribute(MovimentoBean.DB_NUM_K_LOCK,
								(BigDecimal) mb.getAttribute(MovimentoBean.DB_NUM_K_LOCK));
					}
					// controllo se il movimento e' in gestione esterna nel qual caso bisogna aggiornare il
					// numkloprec per evitare che i successivi moduli di aggiornamento provochino un errore di
					// concorrenza
					// in particolare il processor UpdateMovimentoPrec
					if (prgMovimentoPrec != null && prgMovimento.equals(prgMovimentoPrec)) {
						BigDecimal numKlo = (BigDecimal) mb.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
						if (requestContainer.getServiceRequest()
								.containsAttribute(MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO)) {
							requestContainer.getServiceRequest().updAttribute(
									MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO, numKlo.add(new BigDecimal(1)));
						} else {
							requestContainer.getServiceRequest().setAttribute(
									MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO, numKlo.add(new BigDecimal(1)));
						}
					}
				}
			}
		}
	}

	/**
	 * I patti non intervengono nella determinazione dello stato occupazionale, per cui non vengono gestiti in fase di
	 * ricostruzione della storia, ma potrebbero essere stati deassociati dallo stato occupazionale. E' necessario
	 * riassociarli ad uno stato occupazionale congruente con la data del patto.
	 * 
	 * @throws Exception
	 */
	private void riassociaPatti(Vector statiOccupazionaliFinali) throws Exception {
		_logger.debug(
				"SituazioneAmministrativa.riassociaPatti(): i patti vengono riassociati agli stati occupazionali corrispondenti in base alle date");

		BigDecimal prgStatoOccNew = null;
		for (int j = 0; j < patti.size(); j++) {
			SourceBean patto = (SourceBean) patti.get(j);
			if (patto.containsAttribute(PattoBean.DB_COD_STATO_ATTO)
					&& patto.getAttribute(PattoBean.DB_COD_STATO_ATTO).toString().equalsIgnoreCase("PR")) {
				String dataStipula = (String) patto.getAttribute("datStipula");
				for (int k = 0; k < statiOccupazionaliFinali.size(); k++) {
					SourceBean sOcc = (SourceBean) statiOccupazionaliFinali.get(k);
					String dataInizioSOcc = sOcc.containsAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO)
							? sOcc.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString()
							: "";
					if (!dataInizioSOcc.equals("") && DateUtils.compare(dataStipula, dataInizioSOcc) >= 0) {
						prgStatoOccNew = (BigDecimal) sOcc.getAttribute("prgStatoOccupaz");
						patto.updAttribute("prgStatoOccupaz", prgStatoOccNew);
						DBStore.aggiornaPatto297(patto, (BigDecimal) patto.getAttribute("prgStatoOccupaz"),
								requestContainer, txExecutor);
					}
				}
			}
		}
	}

	/**
	 * Se il lavoratore alla fine del ricalcolo si trova in 150, allora al massimo un patto deve essere aperto; nel caso
	 * ci fossero più patti, viene mantenuto aperto sempre e comunque l'ultimo, tranne se l'ultimo è stato chiuso
	 * dall'operatore
	 * 
	 * @throws Exception
	 */
	private void controllaPattoAperto() throws Exception {
		_logger.debug("SituazioneAmministrativa.controllaPattoApero(): controllo patto aperto");
		String dataStipulaPatto = "";
		String dataChiusuraPatto = "";
		int pattiSize = patti.size();
		String dataStipulaPattoPrec = "";
		if (pattiSize > 0) {
			boolean pattoAperto = false;
			for (int k = pattiSize - 1; k >= 0; k--) {
				SourceBean ultimoPatto = (SourceBean) patti.get(k);
				String dataFine = (String) ultimoPatto.getAttribute(PattoBean.DB_DAT_FINE);
				String codStatoAtto = (String) ultimoPatto.getAttribute(PattoBean.DB_COD_STATO_ATTO);
				if ((codStatoAtto != null)
						&& (codStatoAtto.equalsIgnoreCase("PR") || codStatoAtto.equalsIgnoreCase("PP"))) {
					dataStipulaPatto = (String) ultimoPatto.getAttribute(PattoBean.DB_DAT_INIZIO);
					if (dataFine == null || dataFine.equals("")) {
						if (!pattoAperto) {
							pattoAperto = true;
							// gestione adeguamento patto ANP
							String codTipoPatto = ultimoPatto.getAttribute("codtipopatto") != null
									? ultimoPatto.getAttribute("codtipopatto").toString()
									: "";
							if (!codTipoPatto.equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {
								DBStore.trasformaPattoAccordoANP(ultimoPatto, requestContainer, txExecutor);
							}
						} else {
							// dataStipulaPattoPrec contiene la data stipula del patto precedente
							if (dataStipulaPattoPrec.equals("")) {
								dataChiusuraPatto = dataStipulaPatto;
							} else {
								dataChiusuraPatto = DateUtils.giornoPrecedente(dataStipulaPattoPrec);
							}
							DBStore.chiudiPatto297(ultimoPatto, dataChiusuraPatto, "AV", requestContainer, txExecutor);
						}
					}
					dataStipulaPattoPrec = dataStipulaPatto;
				}
			}
		}
	}

	/**
	 * Se il lavoratore alla fine del ricalcolo non si trova in 150, allora non posso avere patti aperti
	 * 
	 * @throws Exception
	 */
	private void controllaChiusuraPatti() throws Exception {
		_logger.debug("SituazioneAmministrativa.controllaChiusuraPatti(): controllo chiusura patti");
		SourceBean pattoSuccessivo = null;
		int pattiSize = patti.size();
		// devo assicurarmi che i patti siano chiusi
		for (int j = 0; j < pattiSize; j++) {
			SourceBean patto = (SourceBean) patti.get(j);
			BigDecimal prgPatto = new BigDecimal(patto.getAttribute(PattoBean.PRG_PATTO_LAV).toString());
			String dataFine = (String) patto.getAttribute(PattoBean.DB_DAT_FINE);
			String codMotivoFinePatto = (String) patto.getAttribute(PattoBean.COD_MOTIVO_FINE_ATTO);
			String codStatoAtto = (String) patto.getAttribute(PattoBean.DB_COD_STATO_ATTO);
			if ((codStatoAtto != null)
					&& (codStatoAtto.equalsIgnoreCase("PR") || codStatoAtto.equalsIgnoreCase("PP"))) {
				String dataChiusura = "";
				int jSucc = j + 1;
				if (dataFine == null || dataFine.equals("")) {
					if (jSucc < pattiSize) {
						pattoSuccessivo = (SourceBean) patti.get(jSucc);
						String dataStipulaSucc = (String) pattoSuccessivo.getAttribute(PattoBean.DB_DAT_INIZIO);
						dataChiusura = DateUtils.giornoPrecedente(dataStipulaSucc);
					} else {
						dataChiusura = (String) patto.getAttribute(PattoBean.DB_DAT_INIZIO);
					}
					DBStore.chiudiPatto297(patto, dataChiusura, "AV", requestContainer, txExecutor);
				} else {
					if (jSucc < pattiSize) {
						pattoSuccessivo = (SourceBean) patti.get(jSucc);
						String dataStipulaSucc = (String) pattoSuccessivo.getAttribute(PattoBean.DB_DAT_INIZIO);
						if (DateUtils.compare(dataFine, dataStipulaSucc) > 0) {
							if (codMotivoFinePatto == null || codMotivoFinePatto.equals("")
									|| codMotivoFinePatto.equalsIgnoreCase("AV")) {
								DBStore.chiudiPatto297(patto, DateUtils.giornoPrecedente(dataStipulaSucc), "AV",
										requestContainer, txExecutor);
							}
						} else {
							if (codMotivoFinePatto == null || codMotivoFinePatto.equals("")) {
								DBStore.chiudiPatto297(patto, dataFine, "AV", requestContainer, txExecutor);
							}
						}
					} else {
						if (codMotivoFinePatto == null || codMotivoFinePatto.equals("")) {
							DBStore.chiudiPatto297(patto, dataFine, "AV", requestContainer, txExecutor);
						}
					}
				}
			}
		}
	}

	/**
	 * Chiamata quando nella ricostruzione storia si riparte dalla Mobilità
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	private int cercaIndiceDaMobilita(String dataRif) throws Exception {
		int i = 0;
		int ind = 0;
		int movimentiSize = movimenti.size();
		for (; i < movimentiSize; i++) {
			Object o = movimenti.get(i);
			String dataInizio = null;
			BigDecimal prg = null;
			MovimentoBean mb = null;
			EventoAmministrativo mLetto = (EventoAmministrativo) o;
			if (o instanceof MovimentoBean) {
				mb = (MovimentoBean) o;
				dataInizio = mb.getDataInizio();
			} else {
				if (o instanceof ChiusuraDidBean) {
					ChiusuraDidBean cdb = (ChiusuraDidBean) o;
					dataInizio = (String) cdb.getAttribute("datFine");
				} else {
					if (o instanceof DidBean) {
						DidBean cdb = (DidBean) o;
						dataInizio = (String) cdb.getDataInizio();
					} else {
						if (o instanceof ChiusuraMobilitaBean) {
							ChiusuraMobilitaBean chMobilita = (ChiusuraMobilitaBean) o;
							dataInizio = chMobilita.getDataInizio();
						} else {
							if (o instanceof MobilitaBean) {
								MobilitaBean mobilita = (MobilitaBean) o;
								dataInizio = mobilita.getDataInizio();
							}
						}
					}
				}
			}
			if (DateUtils.compare(dataRif, dataInizio) > 0)
				ind = i;
			else {
				break;
			}
		}
		return ind;
	}

	/**
	 * per sanare la situazione amministrativa
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public int cercaIndice(String dataRif) throws Exception {
		int i = 0;
		int ind = 0;
		int movimentiSize = movimenti.size();
		for (; i < movimentiSize; i++) {
			Object o = movimenti.get(i);
			String dataInizio = null;
			BigDecimal prg = null;
			MovimentoBean mb = null;
			EventoAmministrativo mLetto = (EventoAmministrativo) o;
			if (o instanceof MovimentoBean) {
				mb = (MovimentoBean) o;
				dataInizio = mb.getDataInizio();
			} else {
				if (o instanceof ChiusuraDidBean) {
					ChiusuraDidBean cdb = (ChiusuraDidBean) o;
					dataInizio = (String) cdb.getAttribute("datFine");
				} else {
					if (o instanceof DidBean) {
						DidBean cdb = (DidBean) o;
						dataInizio = (String) cdb.getDataInizio();
					} else {
						if (o instanceof ChiusuraMobilitaBean) {
							ChiusuraMobilitaBean chMobilita = (ChiusuraMobilitaBean) o;
							dataInizio = chMobilita.getDataInizio();
						} else {
							if (o instanceof MobilitaBean) {
								MobilitaBean mobilita = (MobilitaBean) o;
								dataInizio = mobilita.getDataInizio();
							}
						}
					}
				}
			}
			if (DateUtils.compare(dataRif, dataInizio) >= 0) {
				if (DateUtils.compare(dataRif, dataInizio) > 0)
					ind = i;
				else {
					ind = i;
					break;
				}
			}
		}
		return ind;
	}

	/**
	 * Estrapola dal vettore movimenti la lista dei movimenti nell'anno di dataRif
	 * 
	 * @param dataRif
	 * @param prgMov
	 *            e' il movimento che si sta' trattando, che quindi non deve essere presente nella lista (dato che viene
	 *            ricostruita la storia, se inserisco un movimento questo non deve essere presente tra quelli gia'
	 *            registrati)
	 * @return
	 * @throws Exception
	 */
	public Vector getMovimentiAnno(String dataRif, BigDecimal prgMov, BigDecimal prgPeriodoLav) throws Exception {
		int movimentiSize = movimenti.size();
		Vector v = new Vector(movimentiSize);
		String dataInizioAnno = "01/01/" + DateUtils.getAnno(dataRif);
		String codTipoAvviamento = "";
		boolean exit = false;
		for (int i = 0; (i < movimentiSize && !exit); i++) {
			Object o = movimenti.get(i);
			if (o instanceof DidBean || o instanceof ChiusuraDidBean || o instanceof MobilitaBean
					|| o instanceof ChiusuraMobilitaBean)
				continue;
			MovimentoBean movimento = (MovimentoBean) o;
			// scarto le cessazioni
			if (movimento.getTipoMovimento() == MovimentoBean.CESSAZIONE)
				continue;
			// non considerare TIPO AVVIAMENTO Z.09.02 (codice vecchio RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			if (codTipoAvviamento.equals("Z.09.02"))
				continue;
			String dataInizio = (String) movimento.getDataInizio();
			if (DateUtils.compare(dataInizio, dataRif) > 0) {
				exit = true;
			} else {
				BigDecimal prg = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
				BigDecimal prgPer = (BigDecimal) movimento.getAttribute("PRGPERIODOINTERMITTENTE");
				String dataFine = (String) movimento.getDataFineEffettiva();
				if (prgPeriodoLav != null && prgPer != null) {
					if ((dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, dataInizioAnno) >= 0)
							&& (!prgPeriodoLav.equals(prgPer)) // evito di prendere lo stesso periodo lavorativo che sto
																// gestendo
					) {
						v.add(movimento.getSource());
					}
				} else {
					if ((dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, dataInizioAnno) >= 0)
							&& (prgPer != null || prgMov == null || !prgMov.equals(prg)) // evito di prendere lo stesso
																							// movimento che sto
																							// gestendo
					// perche' in inserimento di un avviamento l'avviamento stesso non e' ancora presente tra i
					// movimenti nel db
					) {
						v.add(movimento.getSource());
					}
				}
			}
		}
		return v;
	}

	/**
	 * Estrapola dal vettore movimenti la lista dei movimenti nell'anno di dataRif escluso il movimento con prgMovimento
	 * = prgMov
	 * 
	 * @param dataRif
	 * @param prgMov
	 * @param tipoRapporto
	 *            = SUBORDINATO allora li prende tutti, AUTONOMO prende solo quelli autonomi, PARASUBORDINATO prende
	 *            solo quelli parasubordinati
	 * @return
	 * @throws Exception
	 */
	public Vector getMovimentiAnnoRedditoMobilita(String dataRif, BigDecimal prgMov, String tipoRapporto,
			BigDecimal prgPeriodoLav) throws Exception {
		int movimentiSize = movimenti.size();
		Vector v = new Vector(movimentiSize);
		String dataInizioAnno = "01/01/" + DateUtils.getAnno(dataRif);
		String codTipoAvviamento = "";
		boolean exit = false;
		boolean movDaTrattare = false;
		Map<BigDecimal, String> mapProrogati = new HashMap<BigDecimal, String>();
		for (int i = 0; (i < movimentiSize && !exit); i++) {
			Object o = movimenti.get(i);
			if (o instanceof DidBean || o instanceof ChiusuraDidBean || o instanceof MobilitaBean
					|| o instanceof ChiusuraMobilitaBean)
				continue;
			MovimentoBean movimento = (MovimentoBean) o;
			// scarto le cessazioni
			if (movimento.getTipoMovimento() == MovimentoBean.CESSAZIONE)
				continue;
			// non considerare TIPO AVVIAMENTO Z.09.02 (codice vecchio RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			if (codTipoAvviamento.equals("Z.09.02"))
				continue;

			String flgSospensione = movimento.containsAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014)
					? movimento.getAttribute(MovimentoBean.DB_FLG_SOSPENSIONE_2014).toString()
					: "";
			String codMonoTipoAss = movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					? movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
					: "";
			if (codMonoTipoAss.equalsIgnoreCase("T"))
				continue;

			if (tipoRapporto.equalsIgnoreCase(MovimentoBean.RAPPORTOAUTONOMO)
					|| tipoRapporto.equalsIgnoreCase(MovimentoBean.RAPPORTOPARASUBORDINATO)) {
				// Non devo prendere i rapporti di lavoro subordinato
				if (flgSospensione.equalsIgnoreCase(MovimentoBean.SI)) {
					continue;
				}
			}

			movDaTrattare = false;

			BigDecimal prg = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			BigDecimal prgPer = (BigDecimal) movimento.getAttribute("PRGPERIODOINTERMITTENTE");
			String dataInizio = (String) movimento.getDataInizio();
			String dataFine = (String) movimento.getDataFineEffettiva();
			// se il movimento è un avviamento considero gli eventuali movimenti prorogati
			if (movimento.getTipoMovimento() == MovimentoBean.ASSUNZIONE) {
				movDaTrattare = true;
				if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
					Vector prec = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
					int precSize = prec.size();
					if (precSize > 0) {
						int movUltimoCatena = precSize - 1;
						SourceBean movimentoUltimo = (SourceBean) prec.get(movUltimoCatena);
						dataFine = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
								? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
								: "";
						for (int iProrogati = 0; iProrogati < precSize; iProrogati++) {
							SourceBean mov = (SourceBean) prec.get(iProrogati);
							BigDecimal prgMovProrogato = (BigDecimal) mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
							mapProrogati.put(prgMovProrogato, "S");
						}
					}
				}
			} else {
				if (!mapProrogati.containsKey(prg)) {
					// sono nel caso che la proroga o trasformazione può essere orfana
					movDaTrattare = true;
				}
			}

			if (movDaTrattare) {
				if (prgPeriodoLav != null && prgPer != null && prgPeriodoLav.equals(prgPer)) {
					exit = true;
				} else {
					if (prgPer != null && prgMov != null && !prg.toString().equals("-1")
							&& !prgMov.toString().equals("-1")) {
						exit = true;
					} else {
						if (prgPer == null && prgMov != null && prgMov.equals(prg)) {
							exit = true;
						} else {
							if (DateUtils.compare(dataInizio, dataRif) > 0) {
								exit = true;
							} else {
								if (prgPeriodoLav != null && prgPer != null) {
									if ((dataFine == null || dataFine.equals("")
											|| DateUtils.compare(dataFine, dataInizioAnno) >= 0)
											&& (!prgPeriodoLav.equals(prgPer)) // evito di prendere lo stesso periodo
																				// lavorativo che sto gestendo
									) {
										v.add(movimento.getSource());
									}
								} else {
									if ((dataFine == null || dataFine.equals("")
											|| DateUtils.compare(dataFine, dataInizioAnno) >= 0)
											&& (prgPer != null || prgMov == null || !prgMov.equals(prg)) // evito di
																											// prendere
																											// lo stesso
																											// movimento
																											// che sto
																											// gestendo
									// perche' in inserimento di un avviamento l'avviamento stesso non e' ancora
									// presente tra i movimenti nel db
									) {
										v.add(movimento.getSource());
									}
								}
							}
						}
					}
				}
			}
		}
		return v;
	}

	/**
	 * Estrae della lista dei movimenti quelli che sono aperti nella data della cessazione
	 * 
	 * @param dataRif
	 *            data di cessazione del movimento
	 * @return
	 * @throws Exception
	 */
	public Vector getMovimentiAnnoDID(String dataRif, int posizione) throws Exception {
		String codTipoAvviamento = "";
		int movimentiSize = movimenti.size();
		String dataRifKey = dataRif.replace("/", "");
		String keyMovAperto = "MOV_APERTO_" + dataRifKey;
		boolean exit = false;
		boolean movOrfanoAvv = false;
		Vector v = new Vector();
		for (int i = 0; i < posizione; i++) {
			Object o = movimenti.get(i);
			if (o instanceof DidBean || o instanceof ChiusuraDidBean || o instanceof MobilitaBean
					|| o instanceof ChiusuraMobilitaBean) {
				continue;
			}
			MovimentoBean movimento = (MovimentoBean) o;
			if (movimento.getTipoMovimento() == MovimentoBean.CESSAZIONE) {
				continue;
			}
			String codTipoMov = movimento.containsAttribute("codTipoMov")
					? movimento.getAttribute("codTipoMov").toString()
					: "";
			movOrfanoAvv = false;
			if (!codTipoMov.equalsIgnoreCase("AVV")) {
				BigDecimal prgMovPrec = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
				if (prgMovPrec == null) {
					movOrfanoAvv = true;
				} else {
					while (prgMovPrec != null) {
						boolean movPrecTrovato = false;
						for (int iPrec = 0; iPrec < movimentiSize && !movPrecTrovato; iPrec++) {
							Object oPrec = movimenti.get(iPrec);
							if (oPrec instanceof DidBean || oPrec instanceof ChiusuraDidBean
									|| oPrec instanceof MobilitaBean || oPrec instanceof ChiusuraMobilitaBean) {
								continue;
							}
							MovimentoBean movimentoPrec = (MovimentoBean) oPrec;
							if (movimentoPrec.getTipoMovimento() == MovimentoBean.CESSAZIONE) {
								continue;
							}
							BigDecimal prgMovimento = (BigDecimal) movimentoPrec
									.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
							if (prgMovimento.equals(prgMovPrec)) {
								String codTipoMovPrec = movimentoPrec.containsAttribute("codTipoMov")
										? movimentoPrec.getAttribute("codTipoMov").toString()
										: "";
								prgMovPrec = (BigDecimal) movimentoPrec
										.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
								if (prgMovPrec == null && !codTipoMovPrec.equalsIgnoreCase("AVV")) {
									movOrfanoAvv = true;
								}
								movPrecTrovato = true;
							}
						}
						if (!movPrecTrovato) {
							prgMovPrec = null;
						}
					}
				}
			}

			if (!codTipoMov.equalsIgnoreCase("AVV") && movimento.containsAttribute("FLAG_NON_IMPATTANTE")
					&& !movOrfanoAvv) {
				continue;
			}

			// non considerare TIPO AVVIAMENTO Z.09.02 (codice vecchio RS3)
			// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			if (codTipoAvviamento.equals("Z.09.02")) {
				continue;
			}
			String dataFine = null;
			if (codTipoMov.equalsIgnoreCase("AVV") && movimento.containsAttribute("MOVIMENTI_PROROGATI")) {
				Vector prec = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
				if (prec.size() > 0) {
					SourceBean movApp = (SourceBean) prec.get(prec.size() - 1);
					dataFine = (String) movApp.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
				} else
					dataFine = (String) movimento.getDataFineEffettiva();
			} else {
				dataFine = (String) movimento.getDataFineEffettiva();
			}
			if (dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, dataRif) >= 0) {
				SourceBean mov = movimento.getSource();
				if (!mov.containsAttribute(keyMovAperto)) {
					if (dataFine != null && !dataFine.equals("") && DateUtils.compare(dataFine, dataRif) == 0) {
						mov.setAttribute(keyMovAperto, "false");
					}
					if (movimento.isCessato())
						mov.updAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE, "C");
					else if (!(movimento.getTipoMovimento() == MovimentoBean.PROROGA
							|| movimento.getTipoMovimento() == MovimentoBean.TRASFORMAZIONE))
						mov.delAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE);
					v.add(mov);
				}
			}
		}

		for (int iCes = posizione + 1; iCes < movimentiSize && !exit; iCes++) {
			EventoAmministrativo o = (EventoAmministrativo) movimenti.get(iCes);
			String dataInizioEvento = o.getDataInizio();
			int tipoEvento = o.getTipoEventoAmministrativo();
			if (DateUtils.compare(dataInizioEvento, dataRif) > 0) {
				exit = true;
			} else {
				if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.PROROGA
						|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
					if (DateUtils.compare(dataInizioEvento, dataRif) == 0) {
						MovimentoBean movimento = (MovimentoBean) o;
						String codTipoMov = movimento.containsAttribute("codTipoMov")
								? movimento.getAttribute("codTipoMov").toString()
								: "";
						movOrfanoAvv = false;
						if (!codTipoMov.equalsIgnoreCase("AVV")) {
							BigDecimal prgMovPrec = (BigDecimal) movimento
									.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
							if (prgMovPrec == null) {
								movOrfanoAvv = true;
							} else {
								while (prgMovPrec != null) {
									boolean movPrecTrovato = false;
									for (int iPrec = 0; iPrec < movimentiSize && !movPrecTrovato; iPrec++) {
										Object oPrec = movimenti.get(iPrec);
										if (oPrec instanceof DidBean || oPrec instanceof ChiusuraDidBean
												|| oPrec instanceof MobilitaBean
												|| oPrec instanceof ChiusuraMobilitaBean) {
											continue;
										}
										MovimentoBean movimentoPrec = (MovimentoBean) oPrec;
										if (movimentoPrec.getTipoMovimento() == MovimentoBean.CESSAZIONE) {
											continue;
										}
										BigDecimal prgMovimento = (BigDecimal) movimentoPrec
												.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
										if (prgMovimento.equals(prgMovPrec)) {
											String codTipoMovPrec = movimentoPrec.containsAttribute("codTipoMov")
													? movimentoPrec.getAttribute("codTipoMov").toString()
													: "";
											prgMovPrec = (BigDecimal) movimentoPrec
													.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
											if (prgMovPrec == null && !codTipoMovPrec.equalsIgnoreCase("AVV")) {
												movOrfanoAvv = true;
											}
											movPrecTrovato = true;
										}
									}
									if (!movPrecTrovato) {
										prgMovPrec = null;
									}
								}
							}
						}

						if (!codTipoMov.equalsIgnoreCase("AVV") && movimento.containsAttribute("FLAG_NON_IMPATTANTE")
								&& !movOrfanoAvv) {
							continue;
						}
						codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
								? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
								: "";
						if (codTipoAvviamento.equals("Z.09.02")) {
							continue;
						}

						SourceBean mov = movimento.getSource();
						v.add(mov);
					}
				}
			}
		}
		return v;
	}

	/**
	 * ritorna i movimenti aperti in una certa data
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public Vector getMovimentiAperti(String dataInizio) throws Exception {
		int movimentiSize = movimenti.size();
		Vector v = new Vector(movimentiSize);
		String codTipoAvviamento = "";
		Map<BigDecimal, String> mapProrogati = new HashMap<BigDecimal, String>();
		for (int i = 0; i < movimentiSize; i++) {
			Object o = movimenti.get(i);
			if (o instanceof DidBean || o instanceof ChiusuraDidBean || o instanceof MobilitaBean
					|| o instanceof ChiusuraMobilitaBean)
				continue;
			MovimentoBean movimento = (MovimentoBean) o;
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			if (movimento.getTipoMovimento() == MovimentoBean.CESSAZIONE || codTipoAvviamento.equals("Z.09.02"))
				continue;
			String dataInizioMov = (String) movimento.getDataInizio();
			String dataFineMov = (String) movimento.getDataFineEffettiva();
			int codTipoMov = movimento.getTipoMovimento();
			BigDecimal prgMovimento = movimento.getPrgMovimento();
			if (!mapProrogati.containsKey(prgMovimento)) {
				if (codTipoMov == MovimentoBean.ASSUNZIONE) {
					if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
						Vector prec = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
						int precSize = prec.size();
						if (precSize > 0) {
							int movUltimoCatena = precSize - 1;
							SourceBean movimentoUltimo = (SourceBean) prec.get(movUltimoCatena);
							dataFineMov = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
									? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
									: "";
							for (int kPrec = 0; kPrec < precSize; kPrec++) {
								SourceBean movimentoCatena = (SourceBean) prec.get(kPrec);
								BigDecimal prgMovProrogato = (BigDecimal) movimentoCatena
										.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
								mapProrogati.put(prgMovProrogato, "S");
							}
						}
					}
					if (((DateUtils.compare(dataInizioMov, dataInizio) <= 0 && (dataFineMov == null
							|| dataFineMov.equals("") || DateUtils.compare(dataFineMov, dataInizio) >= 0)))) {
						SourceBean mov = movimento.getSource();
						v.add(mov);
					}
				} else {
					if (((DateUtils.compare(dataInizioMov, dataInizio) <= 0 && (dataFineMov == null
							|| dataFineMov.equals("") || DateUtils.compare(dataFineMov, dataInizio) >= 0)))) {
						SourceBean mov = movimento.getSource();
						v.add(mov);
					}
				}
			}
		}
		return v;
	}

	/**
	 * 
	 * @param dataRif
	 *            data del movimento che si sta trattando
	 * @param dataDid
	 *            data dichiarazione della did quando presente, oppure se non è presente = data del movimento
	 * @param prgMov
	 *            chiave del movimento
	 * @return
	 * @throws Exception
	 */
	public Vector getMovimentiAnnoDallaDID(String dataRif, String dataDid, BigDecimal prgMov, BigDecimal prgPeriodoLav,
			boolean didPresente) throws Exception {
		int movimentiSize = movimenti.size();
		Vector v = new Vector(movimentiSize);
		String codTipoAvviamento = "";
		String dataInizioAnno = "";
		if (didPresente) {
			if (DateUtils.getAnno(dataDid) != DateUtils.getAnno(dataRif)) {
				dataInizioAnno = "01/01/" + DateUtils.getAnno(dataRif);
			} else {
				dataInizioAnno = dataDid;
			}
		} else {
			dataInizioAnno = "01/01/" + DateUtils.getAnno(dataRif);
		}
		for (int i = 0; i < movimentiSize; i++) {
			Object o = movimenti.get(i);
			if (o instanceof DidBean || o instanceof ChiusuraDidBean || o instanceof MobilitaBean
					|| o instanceof ChiusuraMobilitaBean)
				continue;
			MovimentoBean movimento = (MovimentoBean) o;
			codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
					? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
					: "";
			if (movimento.getTipoMovimento() == MovimentoBean.CESSAZIONE || codTipoAvviamento.equals("Z.09.02"))
				continue;
			BigDecimal prg = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			String dataInizio = (String) movimento.getDataInizio();
			String dataFine = (String) movimento.getDataFineEffettiva();
			BigDecimal prgPer = (BigDecimal) movimento.getAttribute("PRGPERIODOINTERMITTENTE");

			if (prgPeriodoLav != null && prgPer != null) {
				if (((DateUtils.compare(dataInizio, dataRif) <= 0 && (dataFine == null || dataFine.equals("")
						|| DateUtils.compare(dataFine, dataInizioAnno) >= 0))) && (!prgPeriodoLav.equals(prgPer)) // evito
																													// di
																													// prendere
																													// lo
																													// stesso
																													// periodo
																													// lavorativo
																													// che
																													// sto
																													// gestendo
				) {
					SourceBean mov = movimento.getSource();
					if (movimento.isCessato()) {
						mov.updAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE, "C");
					} else if (movimento.getTipoMovimento() == MovimentoBean.ASSUNZIONE) {
						mov.delAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE);
					}
					v.add(mov);
				}
			} else {
				if (((DateUtils.compare(dataInizio, dataRif) <= 0 && (dataFine == null || dataFine.equals("")
						|| DateUtils.compare(dataFine, dataInizioAnno) >= 0)))
						&& (prgPer != null || prgMov == null || !prgMov.equals(prg)) // evito di prendere lo stesso
																						// movimento che sto gestendo
				) {
					SourceBean mov = movimento.getSource();
					if (movimento.isCessato()) {
						mov.updAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE, "C");
					} else if (!(movimento.getTipoMovimento() == MovimentoBean.PROROGA
							|| movimento.getTipoMovimento() == MovimentoBean.TRASFORMAZIONE)) {
						mov.delAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE);
					}
					v.add(mov);
				}
			}
		}
		return v;
	}

	/**
	 * Associa ad ogni stato occupazionale did e patti
	 * 
	 * @param statiOccs
	 * @param dids
	 * @param patti
	 * @return la lista con le nuove associazioni.
	 * @throws Exception
	 */
	private Vector listaStatiOccupazionali(Vector statiOccs, Vector dids, Vector patti) throws Exception {
		Vector lista = new Vector();
		for (int i = 0; i < statiOccs.size(); i++) {
			SourceBean so = (SourceBean) statiOccs.get(i);
			List pattiAssociati = estraiPatti(patti, so);
			SourceBean did = (SourceBean) cercaDID(dids, so);
			StatoOccupazionaleBean soBean = new StatoOccupazionaleBean(so, did, pattiAssociati, lista);
			if (i > 0) {
				soBean.setStatoOccupazionaleBack((StatoOccupazionaleBean) lista.get(i - 1));
				((StatoOccupazionaleBean) lista.get(i - 1)).setStatoOccupazionaleNext(soBean);
			}
			lista.add(soBean);
		}
		return lista;
	}

	/**
	 * Aggiorna i movimenti e did che hanno prgStatoOcc(da cancellare) = prgSOcc, settando questo campo a null
	 * 
	 * @param movimenti
	 * @param statoOccupazionale
	 * @throws Exception
	 */

	private void dereferenziaEventiAmmInRicostruzione(List movimenti, StatoOccupazionaleBean statoOccupazionale,
			Object cdnUser) throws Exception {
		int i = 0;
		MovimentoBean mb = null;
		MobilitaBean mobilita = null;
		DidBean db = null;
		BigDecimal prgStatoOcc = statoOccupazionale.getPrgStatoOccupaz();
		BigDecimal prgSOcc = null;
		_logger.debug(
				"SituazioneAmministrativa.dereferenziaEventiAmmInRicostruzione():ricerca per prgStOcc:" + prgStatoOcc);

		int movimentiSize = movimenti.size();
		for (; i < movimentiSize; i++) {
			EventoAmministrativo o = (EventoAmministrativo) movimenti.get(i);
			_logger.debug("SituazioneAmministrativa.dereferenziaEventiAmmInRicostruzione():" + o);

			int tipoEvento = o.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.TRASFORMAZIONE
					|| tipoEvento == EventoAmministrativo.PROROGA || tipoEvento == EventoAmministrativo.CESSAZIONE) {
				mb = (MovimentoBean) movimenti.get(i);
				prgSOcc = mb.getPrgStatoOccupaz();
				if (mb.virtuale() || prgSOcc == null)
					continue;
				if (prgStatoOcc.equals(prgSOcc)) {
					_logger.debug("SituazioneAmministrativa.dereferenziaEventiAmmInRicostruzione():deassociato:" + mb);

					SourceBean so = mb.getStatoOccupazionale().getSource();
					Object prgso = so.getAttribute("prgStatoOccupaz");
					so.delAttribute("prgStatoOccupaz");
					DBStore.aggiornaMovimento(mb, cdnUser, txExecutor);
					// ripristino il prgStatoOccupaz necessario, anche se non piu' presente nel db, per le successive
					// computazioni
					so.setAttribute("prgStatoOccupaz", prgso);
					// controllo se il movimento e' in gestione esterna nel qual caso bisogna aggiornare il
					// numkloprec per evitare che i successivi moduli di aggiornamento provochino un errore di
					// concorrenza
					if (mb.precInInserimento()) {
						// si tratta del movimento prec, ovvero del movimento cessato o prororago o trasformato che
						// sara'
						// aggiornato dal modulo successivo
						BigDecimal numKlo = (BigDecimal) mb.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
						if (requestContainer.getServiceRequest()
								.containsAttribute(MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO)) {
							requestContainer.getServiceRequest().updAttribute(
									MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO, numKlo.add(new BigDecimal(1)));
						} else {
							requestContainer.getServiceRequest().setAttribute(
									MovimentoBean.REQ_NUM_K_LO_MOV_PREC_CAMBIATO, numKlo.add(new BigDecimal(1)));
						}
					}
				}
			} else {
				if (tipoEvento == EventoAmministrativo.DID) {
					db = (DidBean) o;
					prgSOcc = db.getPrgStatoOccupaz();
					if (prgSOcc == null)
						continue;
					if (prgStatoOcc.equals(prgSOcc)) {
						_logger.debug(
								"SituazioneAmministrativa.dereferenziaEventiAmmInRicostruzione():deassociato:" + db);

						DBStore.aggiornaDID(db, null, requestContainer, txExecutor);
						// Aggiorno il numklo dell'eventuale ChiusuraDID successiva
						for (int k = i; k < movimentiSize; k++) {
							EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
							if (c.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_DID) {
								ChiusuraDidBean cDb = (ChiusuraDidBean) c;
								if (cDb.getPrgDichDisponibilita().equals(db.getPrgDichDisponibilita())) {
									_logger.debug(
											"SituazioneAmministrativa.dereferenziaEventiAmmInRicostruzione():aggiornamento chiusura DID: "
													+ cDb);

									cDb.updAttribute("numKloDichDisp", db.getAttribute("numKloDichDisp"));
									break;
								}
							}
						} // end for
					}
				} else {
					if (tipoEvento == EventoAmministrativo.MOBILITA) {
						mobilita = (MobilitaBean) o;
						prgSOcc = mobilita.getPrgStatoOccupaz();
						if (prgSOcc == null)
							continue;
						if (prgStatoOcc.equals(prgSOcc)) {
							_logger.debug(
									"SituazioneAmministrativa.dereferenziaEventiAmmInRicostruzione():deassociata mobilità:"
											+ mobilita);

							mobilita.aggiornaStatoOccupaz(null, requestContainer, txExecutor);
							// *** Aggiorno il numklo dell'eventuale ChiusuraMobilita successiva ***
							for (int k = i; k < movimentiSize; k++) {
								EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
								if (c.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_MOBILITA) {
									ChiusuraMobilitaBean cMobilita = (ChiusuraMobilitaBean) c;
									if (cMobilita.getPrgMobilitaIscr().equals(mobilita.getPrgMobilitaIscr())) {
										_logger.debug(
												"SituazioneAmministrativa.dereferenziaEventiAmmInRicostruzione():aggiornamento numKlo chiusura mobilita: "
														+ cMobilita);

										cMobilita.updAttribute("numKloMobIscr", mobilita.getAttribute("numKloMobIscr"));
										break;
									}
								}
							} // end for
						}
					}
				}
			}
		}
	}

	private void dereferenziaPatti(List patti, BigDecimal prgStatoOcc) throws Exception {
		for (int i = 0; i < patti.size(); i++) {
			if (patti.get(i) instanceof PattoBean) {
				PattoBean patto = (PattoBean) patti.get(i);
				DBStore.aggiornaPatto297(patto, prgStatoOcc, requestContainer, txExecutor);
				aggiornaNumKloPatto(patto.getPrgPattoLavBigDec(), null, patto.getNumklo());
				_logger.debug(
						"SituazioneAmministrativa.dereferenziaPatti(): prg=" + patto.getAttribute("prgPattoLavoratore")
								+ ", prgStOcc=" + patto.getAttribute("prgStatoOccupaz"));

			} else {
				SourceBean sbPatto = (SourceBean) patti.get(i);
				DBStore.aggiornaPatto297(sbPatto, prgStatoOcc, requestContainer, txExecutor);
				aggiornaNumKloPatto(new BigDecimal(sbPatto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()), null,
						new BigDecimal(sbPatto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
				_logger.debug("SituazioneAmministrativa.dereferenziaPatti(): prg="
						+ sbPatto.getAttribute("prgPattoLavoratore") + ", prgStOcc="
						+ sbPatto.getAttribute("prgStatoOccupaz"));

			}
		}
	}

	private void dereferenziaPatti(List patti) throws Exception {
		for (int i = 0; i < patti.size(); i++) {
			if (patti.get(i) instanceof PattoBean) {
				PattoBean patto = (PattoBean) patti.get(i);
				DBStore.aggiornaPatto297(patto, null, requestContainer, txExecutor);
				aggiornaNumKloPatto(patto.getPrgPattoLavBigDec(), null, patto.getNumklo());
				_logger.debug(
						"SituazioneAmministrativa.dereferenziaPatti(): prg=" + patto.getAttribute("prgPattoLavoratore")
								+ ", prgStOcc=" + patto.getAttribute("prgStatoOccupaz"));

			} else {
				SourceBean sbPatto = (SourceBean) patti.get(i);
				DBStore.aggiornaPatto297(sbPatto, null, requestContainer, txExecutor);
				aggiornaNumKloPatto(new BigDecimal(sbPatto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()), null,
						new BigDecimal(sbPatto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
				_logger.debug("SituazioneAmministrativa.dereferenziaPatti(): prg="
						+ sbPatto.getAttribute("prgPattoLavoratore") + ", prgStOcc="
						+ sbPatto.getAttribute("prgStatoOccupaz"));

			}
		}
	}

	public void resettaStatiOccupazionali(ListaStatiOccupazionali lista) throws Exception {
		// chiudi so aperto
		// apri so iniziale
		// deassocia so
		// cancella so
		StatoOccupazionaleBean statoOccIniziale = lista.getStatoOccupazionaleIniziale();
		_logger.debug("SituazioneAmministrativa.resettaStatiOccupazionali(): stato occupazionale iniziale:"
				+ statoOccIniziale);

		String dataAnzianitaDisoc = "";
		String dataInizioSOcc = "";
		String prgListaStatiOccCanc = "";
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		/*
		 * chiudi s occ aperto (si suppone sia l'ultimo della lista), ma potrebbe anche non esistere uno stato
		 * occupazionale aperto, in questo caso non sara' necessario chiuderlo.
		 */
		StatoOccupazionaleBean statoOccAperto = null;
		if (statiOccupazionali.size() > 0) {
			statoOccAperto = (StatoOccupazionaleBean) statiOccupazionali.get(statiOccupazionali.size() - 1);
			if (!statoOccIniziale.compare(statoOccAperto)) {
				if (!statoOccAperto.virtuale())
					DBStore.chiudiStatoOcc((StatoOccupazionaleBean) statoOccAperto, DateUtils.getNow(),
							requestContainer, txExecutor);
				if (!statoOccIniziale.virtuale())
					DBStore.apriStatoOcc((StatoOccupazionaleBean) statoOccIniziale, requestContainer, txExecutor);
			}
			// else sono uguali, quindi lo stato occupazionale iniziale e quello aperto coincidono
		} else {
			// lo stato occupazionale aperto e' quello iniziale, sempre che non sia virtuale, ovvero non presente
			// nel db. Non ho bisogno di aprirlo o chiuderlo.
		}

		// ora bisogna dereferenziare gli stati occupazionali da cancellare dagli eventi amministrativi
		List statiOccDaCancellare = lista.getStatiOccupazionaliDaCancellare();
		_logger.debug("SituazioneAmministrativa.resettaStatiOccupazionali()" + statiOccDaCancellare);

		int statiOccDaCancellareSize = statiOccDaCancellare.size();
		int indiceSOccManuale = 0;
		if (statiOccDaCancellareSize > 0) {
			List pattiAssociatiLavoratore = DBLoad.getPattiAssociatiLav(cdnLavoratoreSitAmm, txExecutor);
			for (int i = 0; i < statiOccDaCancellareSize; i++) {
				StatoOccupazionaleBean sOcc = (StatoOccupazionaleBean) statiOccDaCancellare.get(i);
				// controllo se lo stato occupazionale è stato modificato manualmente (solo tra
				// quelli che possono essere cancellati durante il ricalcolo dello stato occupazionale)
				if (sOcc.getCodMonoProvenienza().equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_MANUALE)
						|| sOcc.getCodMonoProvenienza().equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_AGG_MANUALE)
						|| sOcc.getCodMonoProvenienza()
								.equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_REG_AGG_MANUALE)) {
					if (!requestContainer.getServiceRequest()
							.containsAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO")) {
						requestContainer.getServiceRequest().setAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO",
								"true");
					}
					if (!requestContainer.getServiceRequest()
							.containsAttribute("CANCELLA_STATO_OCC_MANUALE_IN_RICALCOLO")) {
						setStatiOccupazionaliManuali(sOcc, indiceSOccManuale);
						indiceSOccManuale = indiceSOccManuale + 1;
					}
				}
				// controllo mesi anziantità e/o sospensione non calcolati dal SIL
				if (!requestContainer.getServiceRequest().containsAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO")
						&& !requestContainer.getServiceRequest()
								.containsAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI")) {
					if ((!sOcc.getNumAnzianitaPrec297().equals("") && !sOcc.getNumAnzianitaPrec297().equals("0"))
							|| (!sOcc.getNumMesiSosp().equals("") && !sOcc.getNumMesiSosp().equals("0"))) {
						requestContainer.getServiceRequest().setAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI",
								"true");
					}
				}
				// controllo data anziantità stato occupazionale rispetto alla did/mobilità valida
				if (!requestContainer.getServiceRequest().containsAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO")
						&& !requestContainer.getServiceRequest()
								.containsAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI")
						&& !requestContainer.getServiceRequest()
								.containsAttribute("INCONGRUENZA_DATA_ANZIANITA_STATO_OCC")) {
					dataAnzianitaDisoc = sOcc.getDataAnzianita();
					dataInizioSOcc = sOcc.getDataInizio();
					if (!dataAnzianitaDisoc.equals("")) {
						if (!Controlli.isCorrettaDataAnzianita(dids, listaMobilita, dataInizioSOcc,
								dataAnzianitaDisoc)) {
							requestContainer.getServiceRequest().setAttribute("INCONGRUENZA_DATA_ANZIANITA_STATO_OCC",
									"true");
						}
					}
				}

				dereferenziaEventiAmmInRicostruzione(movimenti, sOcc, cdnUser);

				BigDecimal prgStatoOcc = sOcc.getPrgStatoOccupaz();
				if (pattiAssociatiLavoratore.size() > 0) {
					List pattiAssociatiSO = getPattoAssociatoStatoOccupaz(prgStatoOcc, pattiAssociatiLavoratore);
					if (pattiAssociatiSO.size() > 0) {
						sOcc.setPatti(pattiAssociatiSO);
						dereferenziaPatti(sOcc.getPatti());
					}
				}
				prgListaStatiOccCanc = prgListaStatiOccCanc + prgStatoOcc.toString() + ",";
				statiOccupazionaliCancellati.add(sOcc);
			}
			cancellazioneFisicaStatoOccupazionali(prgListaStatiOccCanc);
		}
		this.statoOccupazionaleAperto = statoOccIniziale;
	}

	public void cancellazioneFisicaStatoOccupazionali(String prgListaStatiOccCanc) throws Exception {
		int sizeListaPrgOccCanc = prgListaStatiOccCanc.length();
		if (sizeListaPrgOccCanc > 0) {
			// numero massimo di elementi in una lista è 1000
			int maxNumberExpressionList = 1000;
			prgListaStatiOccCanc = prgListaStatiOccCanc.substring(0, sizeListaPrgOccCanc - 1);
			Vector vettPrg = it.eng.afExt.utils.StringUtils.split(prgListaStatiOccCanc, ",");
			int vettPrgSize = vettPrg.size();
			if (vettPrgSize <= maxNumberExpressionList) {
				String deleteQuery = "delete from am_stato_occupaz where prgStatoOccupaz in (" + prgListaStatiOccCanc
						+ ")";
				txExecutor.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
			} else {
				int j = 0;
				int nContTotal = 0;
				int nciclo = 0;
				prgListaStatiOccCanc = "";
				while (nContTotal < vettPrgSize) {
					if (j < maxNumberExpressionList) {
						prgListaStatiOccCanc = prgListaStatiOccCanc
								+ vettPrg.get((nciclo * maxNumberExpressionList) + j) + ",";
						j = j + 1;
						nContTotal = nContTotal + 1;
					} else {
						if (!prgListaStatiOccCanc.equals("")) {
							prgListaStatiOccCanc = prgListaStatiOccCanc.substring(0, prgListaStatiOccCanc.length() - 1);
							String deleteQuery = "delete from am_stato_occupaz where prgStatoOccupaz in ("
									+ prgListaStatiOccCanc + ")";
							txExecutor.executeQueryByStringStatement(deleteQuery, null,
									TransactionQueryExecutor.DELETE);
						}
						prgListaStatiOccCanc = "";
						j = 0;
						nciclo = nciclo + 1;
					}
				}
				if (!prgListaStatiOccCanc.equals("")) {
					prgListaStatiOccCanc = prgListaStatiOccCanc.substring(0, prgListaStatiOccCanc.length() - 1);
					String deleteQuery = "delete from am_stato_occupaz where prgStatoOccupaz in ("
							+ prgListaStatiOccCanc + ")";
					txExecutor.executeQueryByStringStatement(deleteQuery, null, TransactionQueryExecutor.DELETE);
				}
			}
		}
	}

	public List getPattoAssociatoStatoOccupaz(BigDecimal prgStatoOcc, List patti) {
		List pattiTrovati = new ArrayList();
		int pattiSize = patti.size();
		for (int i = 0; i < pattiSize; i++) {
			SourceBean patto = (SourceBean) patti.get(i);
			Object prgStatoOccPatto = patto.getAttribute(PattoBean.PRG_STATO_OCCUPAZ);
			if (prgStatoOccPatto != null && patto.getAttribute(PattoBean.PRG_STATO_OCCUPAZ).equals(prgStatoOcc)) {
				pattiTrovati.add(patto);
			}
		}
		return pattiTrovati;
	}

	/**
	 * invocato in ricostruzione in presenza dell'evento chiusura did
	 * 
	 * @param data1
	 *            contiene la data dichiarazione della did
	 * @param data2
	 *            contiene la data chiusura della did
	 * @return
	 * @throws Exception
	 */
	public List cercaPatto(String data1, String data2) throws Exception {
		List pattiTrovati = new ArrayList();
		SourceBean patto = null;
		for (int i = 0; i < patti.size(); i++) {
			patto = (SourceBean) patti.get(i);
			String dataInizio = (String) patto.getAttribute("datStipula");
			if (DateUtils.compare(data1, dataInizio) <= 0
					&& (data2 == null || data2.equals("") || DateUtils.compare(data2, dataInizio) >= 0))
				pattiTrovati.add(patto);
		}
		return pattiTrovati;
	}

	/**
	 * Cerca nella lista dei patti quello che rientra nella dataRif, aperto o meno che sia. Nel caso in cui vi siano
	 * piu' patti aperti e chiusi nello stesso giorno, verra' selezionato l'ultimo della lista.
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public SourceBean cercaPatto(String dataRif) throws Exception {
		SourceBean patto = null;
		boolean chiudiPatto = true;
		String dataChiusura = DateUtils.giornoPrecedente(dataRif);
		for (int i = 0; i < patti.size(); i++) {
			SourceBean sb = (SourceBean) patti.get(i);
			String dataStipula = (String) sb.getAttribute("datStipula");
			String dataFinePatto = (String) sb.getAttribute("datFine");
			// prgDichDispo:prg della did a cui è legato il patto
			BigDecimal prgDichDispo = (BigDecimal) sb.getAttribute(PattoBean.PRG_DICH_DISPO);
			// non cerco più tra i patti aperti (non controllo più la data fine)
			int numDidSize = dids.size();
			if (numDidSize > 0) {
				for (int j = 0; j < numDidSize; j++) {
					chiudiPatto = true;
					SourceBean sbDid = (SourceBean) dids.get(j);
					String dataDid = (String) sbDid.getAttribute(DidBean.DB_DAT_INIZIO);
					String dataFineDid = (String) sbDid.getAttribute(DidBean.DB_DAT_FINE);
					BigDecimal prgDid = (BigDecimal) sbDid.getAttribute(DidBean.DB_PRG_DID);
					if (dataFineDid == null || dataFineDid.equals("") || DateUtils.compare(dataFineDid, dataRif) >= 0) {
						// patto potrebbe essere collegato alla did
						if (DateUtils.compare(dataStipula, dataDid) >= 0 && DateUtils.compare(dataRif, dataDid) >= 0) {
							if (prgDichDispo == null || (prgDid != null && !prgDichDispo.equals(prgDid))) {
								// non ci deve essere una did successiva compatibile
								// con la data di stipula del patto che stiamo trattando
								if ((prgDid != null && prgDichDispo != null && !prgDichDispo.equals(prgDid))
										&& (j >= (numDidSize - 1))) {
									chiudiPatto = false;
									break;
								} else {
									for (int k = j + 1; k < numDidSize; k++) {
										SourceBean sbDidSucc = (SourceBean) dids.get(k);
										String dataDidSucc = (String) sbDidSucc.getAttribute(DidBean.DB_DAT_INIZIO);
										if (DateUtils.compare(dataStipula, dataDidSucc) >= 0) {
											chiudiPatto = false;
											break;
										}
									}
								}
							}
							if (Sottosistema.MO.isOn() && chiudiPatto && prgDichDispo == null
									&& listaMobilita.size() > 0) {
								// non ci deve essere un periodo di mobilità successivo compatibile
								// con la data di stipula del patto che stiamo trattando
								for (int k = 0; k < listaMobilita.size(); k++) {
									SourceBean sbMobilita = (SourceBean) listaMobilita.get(k);
									String dataInizioMob = sbMobilita.containsAttribute("DATINIZIOORIGINALE")
											? sbMobilita.getAttribute("DATINIZIOORIGINALE").toString()
											: sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
									if (DateUtils.compare(dataStipula, dataInizioMob) >= 0) {
										chiudiPatto = false;
										break;
									}
								}
							}
							// gestione a seguito della stipula patto 150 per lavoratori D o I senza did e mobilita'
							if (chiudiPatto) {
								if ((prgDichDispo != null && (dataFinePatto == null || dataFinePatto.equals("")
										|| DateUtils.compare(dataChiusura, dataFinePatto) < 0))
										|| (prgDichDispo == null && DateUtils.compare(dataStipula, dataChiusura) <= 0
												&& (dataFinePatto == null || dataFinePatto.equals("")
														|| DateUtils.compare(dataChiusura, dataFinePatto) < 0))) {
									patto = sb;
									break;
								}
							}
						}
					} else {
						// data chiusura did < data Rif, allora in questo caso controllo la compatibilità
						// del patto con eventuali periodi di mobilità per verificare se il patto in
						// questione deve essere chiuso
						if (Sottosistema.MO.isOn() && prgDichDispo == null && listaMobilitaDB.size() > 0) {
							// se prima del calcolo stato occupazionale ho periodi di mobilità, e dopo
							// l'allineamento delle mobilità non sono presenti più periodi di mobilità allora
							// il patto deve essere restituito dalla funzione per essere chiuso
							if (listaMobilita.size() == 0) {
								if (dataFinePatto == null || dataFinePatto.equals("")
										|| DateUtils.compare(dataChiusura, dataFinePatto) < 0) {
									patto = sb;
								}
							} else {
								for (int j1 = 0; j1 < listaMobilita.size(); j1++) {
									SourceBean sbMobilita = (SourceBean) listaMobilita.get(j1);
									String dataInizioMob = sbMobilita.containsAttribute("DATINIZIOORIGINALE")
											? sbMobilita.getAttribute("DATINIZIOORIGINALE").toString()
											: sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
									if (DateUtils.compare(dataStipula, dataInizioMob) >= 0
											&& DateUtils.compare(dataRif, dataInizioMob) >= 0) {
										for (int k = j1 + 1; k < listaMobilita.size(); k++) {
											SourceBean sbMobilitaSucc = (SourceBean) listaMobilita.get(k);
											String dataInizioMobSucc = sbMobilitaSucc
													.containsAttribute("DATINIZIOORIGINALE")
															? sbMobilitaSucc.getAttribute("DATINIZIOORIGINALE")
																	.toString()
															: sbMobilitaSucc.getAttribute(MobilitaBean.DB_DAT_INIZIO)
																	.toString();
											if (DateUtils.compare(dataStipula, dataInizioMobSucc) >= 0) {
												// esiste una mobilità successiva compatibile con il patto,
												// il patto non deve essere restituito dalla funzione per essere chiuso
												chiudiPatto = false;
												break;
											}
										}
										if (chiudiPatto) {
											if (dataFinePatto == null || dataFinePatto.equals("")
													|| DateUtils.compare(dataChiusura, dataFinePatto) < 0) {
												patto = sb;
											}
										}
									}
								}
							}
						}
						// gestione a seguito della stipula patto 150 per lavoratori D o I senza did e mobilita'
						if (prgDichDispo == null && chiudiPatto
								&& (dataFinePatto == null || dataFinePatto.equals("")
										|| DateUtils.compare(dataStipula, dataChiusura) <= 0
												&& DateUtils.compare(dataChiusura, dataFinePatto) < 0)) {
							patto = sb;
						}
					}
				}
			} else {
				// il lavoratore non ha dichiarato did e quindi controllo solo se ci sono periodi di mobilità
				// compatibili con il patto per verificare se il patto in questione deve essere chiuso
				// oppure stati occupazionali inseriti manualmente compatibili con il patto
				if (Sottosistema.MO.isOn()) {
					if (listaMobilitaDB.size() > 0) {
						// se prima del calcolo stato occupazionale ho periodi di mobilità, e dopo
						// l'allineamento delle mobilità non sono presenti più periodi di mobilità allora
						// il patto deve essere restituito dalla funzione per essere chiuso
						if (listaMobilita.size() == 0) {
							if (dataFinePatto == null || dataFinePatto.equals("")
									|| DateUtils.compare(dataChiusura, dataFinePatto) < 0) {
								patto = sb;
							}
						} else {
							for (int j = 0; j < listaMobilita.size(); j++) {
								chiudiPatto = true;
								SourceBean sbMobilita = (SourceBean) listaMobilita.get(j);
								String dataInizioMob = sbMobilita.containsAttribute("DATINIZIOORIGINALE")
										? sbMobilita.getAttribute("DATINIZIOORIGINALE").toString()
										: sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
								if (DateUtils.compare(dataStipula, dataInizioMob) >= 0
										&& DateUtils.compare(dataRif, dataInizioMob) >= 0) {
									for (int k = j + 1; k < listaMobilita.size(); k++) {
										SourceBean sbMobilitaSucc = (SourceBean) listaMobilita.get(k);
										String dataInizioMobSucc = sbMobilitaSucc
												.containsAttribute("DATINIZIOORIGINALE")
														? sbMobilitaSucc.getAttribute("DATINIZIOORIGINALE").toString()
														: sbMobilitaSucc.getAttribute(MobilitaBean.DB_DAT_INIZIO)
																.toString();
										if (DateUtils.compare(dataStipula, dataInizioMobSucc) >= 0) {
											// esiste una mobilità successiva compatibile con il patto,
											// il patto non deve essere restituito dalla funzione per essere chiuso
											chiudiPatto = false;
											break;
										}
									}
									if (chiudiPatto) {
										if (dataFinePatto == null || dataFinePatto.equals("")
												|| DateUtils.compare(dataChiusura, dataFinePatto) < 0) {
											patto = sb;
										}
									}
								}
							}
						}
					}
				}
				// gestione a seguito della stipula patto 150 per lavoratori D o I senza did e mobilita'
				if (prgDichDispo == null && chiudiPatto
						&& (dataFinePatto == null || dataFinePatto.equals("")
								|| DateUtils.compare(dataStipula, dataChiusura) <= 0
										&& DateUtils.compare(dataChiusura, dataFinePatto) < 0)) {
					patto = sb;
				}
			}
		}
		return patto;
	}

	/**
	 * Metodo invocato per la riapertura di un patto, stipulato a partire da un'iscrizione alla mobilità
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public SourceBean cercaPattoMob(String dataRif) throws Exception {
		SourceBean sb = null;
		String dataStipula = "";
		String dataFinePatto = "";
		SourceBean patto = null;
		boolean checkPattoMob = false;
		BigDecimal prgDicDispPatto = null;
		for (int i = 0; i < patti.size(); i++) {
			sb = (SourceBean) patti.get(i);
			dataStipula = (String) sb.getAttribute("datStipula");
			dataFinePatto = (sb.getAttribute("datFine") != null) ? sb.getAttribute("datFine").toString() : "";
			prgDicDispPatto = (sb.getAttribute(PattoBean.PRG_DICH_DISPO) != null)
					? new BigDecimal(sb.getAttribute(PattoBean.PRG_DICH_DISPO).toString())
					: null;
			if (prgDicDispPatto == null) {
				// considero solo i patti non legati ad una did
				checkPattoMob = false;
				if (listaMobilita != null) {
					int sizeListaMob = listaMobilita.size();
					for (int k = 0; k < sizeListaMob; k++) {
						SourceBean sbMobilita = (SourceBean) listaMobilita.get(k);
						String dataInizioMob = sbMobilita.containsAttribute("DATINIZIOORIGINALE")
								? sbMobilita.getAttribute("DATINIZIOORIGINALE").toString()
								: sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
						if (DateUtils.compare(dataStipula, dataInizioMob) >= 0
								&& DateUtils.compare(dataInizioMob, dataRif) <= 0) {
							patto = sb;
							checkPattoMob = true;
						}
					}
				}
				// patto 150 stipulato a partire da uno stato occupazionale di D o I senza DID e mobilita'
				if (!checkPattoMob) {
					if (DateUtils.compare(dataStipula, dataRif) >= 0) {
						patto = sb;
					}
				}
			} else {
				if (dataFinePatto.equals("")) {
					// ho già riaperto un patto associato ad una did
					return null;
				}
			}
		}
		return patto;
	}

	/**
	 * Recupera il patto associato ad una certa DID (in caso di più patti associati alla stessa did, restituisce
	 * l'ultimo, cioè quello con data stipula maggiore)
	 * 
	 * @param prgDichDisp
	 * @return
	 * @throws Exception
	 */
	public SourceBean cercaPatto(BigDecimal prgDichDisp, BigDecimal cdnLav) throws Exception {
		SourceBean patto = null;
		SourceBean sb = null;
		BigDecimal prgDicDispPatto = null;
		if (patti.size() <= 0) {
			patti = DBLoad.getPattiStoricizzati(cdnLav, txExecutor);
		}
		for (int i = 0; i < patti.size(); i++) {
			sb = (SourceBean) patti.get(i);
			prgDicDispPatto = (sb.getAttribute(PattoBean.PRG_DICH_DISPO) != null)
					? new BigDecimal(sb.getAttribute(PattoBean.PRG_DICH_DISPO).toString())
					: null;
			if (prgDicDispPatto != null && prgDicDispPatto.equals(prgDichDisp))
				patto = sb;
		}
		return patto;
	}

	/**
	 * Aggiorna al nuovo valore il numklo del patto su cui è stato effettuato l'aggiornamento
	 * 
	 * @param prgPattoLav
	 * @param datFine
	 * @param nuovo
	 *            numklo
	 * @return
	 * @throws Exception
	 */
	public void aggiornaNumKloPatto(BigDecimal prgPattoLav, String datFine, BigDecimal numklo) throws Exception {
		for (int i = 0; i < patti.size(); i++) {
			SourceBean sb = (SourceBean) patti.get(i);
			BigDecimal prgPatto = new BigDecimal(sb.getAttribute(PattoBean.PRG_PATTO_LAV).toString());
			if (prgPattoLav.equals(prgPatto)) {
				sb.updAttribute(PattoBean.NUMKLO_PATTO_LAV, numklo);
				if (datFine != null && !datFine.equals("")) {
					String dataStipula = sb.containsAttribute(PattoBean.DB_DAT_INIZIO)
							? sb.getAttribute(PattoBean.DB_DAT_INIZIO).toString()
							: "";
					if (!dataStipula.equals("") && DateUtils.compare(dataStipula, datFine) > 0)
						datFine = dataStipula;
					if (sb.containsAttribute(PattoBean.DB_DAT_FINE)) {
						sb.updAttribute(PattoBean.DB_DAT_FINE, datFine);
					} else {
						sb.setAttribute(PattoBean.DB_DAT_FINE, datFine);
					}
				}
				break;
			}
		}
	}

	/**
	 * Aggiorna nel vettore patti il numklo del patto, cancella la dataFine, e il codMotivoFineAtto
	 * 
	 * @param prgPattoLav
	 * @param numklo
	 * @throws Exception
	 */
	public void aggiornaPattoRiaperto(BigDecimal prgPattoLav, BigDecimal numklo) throws Exception {
		for (int i = 0; i < patti.size(); i++) {
			SourceBean patto = (SourceBean) patti.get(i);
			BigDecimal prgPatto = new BigDecimal(patto.getAttribute(PattoBean.PRG_PATTO_LAV).toString());
			if (prgPattoLav.equals(prgPatto)) {
				patto.updAttribute(PattoBean.NUMKLO_PATTO_LAV, numklo);
				if (patto.containsAttribute(PattoBean.DB_DAT_FINE))
					patto.delAttribute(PattoBean.DB_DAT_FINE);
				if (patto.containsAttribute(PattoBean.COD_MOTIVO_FINE_ATTO))
					patto.delAttribute(PattoBean.COD_MOTIVO_FINE_ATTO);
				break;
			}
		}
	}

	/**
	 * Aggiorna al nuovo valore il numklo della did nel vettore dei movimenti della situazione amministrativa del
	 * lavoratore
	 * 
	 * @param did
	 * @param nuovo
	 *            numklo
	 * @param movimenti
	 * @return
	 * @throws Exception
	 */
	public void aggiornaNumKloDichDispoInMovimenti(DidBean did, BigDecimal numklo, List movimenti) throws Exception {
		int movimentiSize = movimenti.size();
		for (int k = 0; k < movimentiSize; k++) {
			EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
			int tipoEvento = c.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.DID) {
				DidBean didApp = (DidBean) c;
				if (didApp.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita())) {
					_logger.debug("StatoOccupazionaleManager2.avviamento():aggiornamento numKlo DID: " + did);

					didApp.updAttribute("numKloDichDisp", numklo);
				}
			} else {
				if (tipoEvento == EventoAmministrativo.CHIUSURA_DID) {
					ChiusuraDidBean cDb = (ChiusuraDidBean) c;
					if (cDb.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita())) {
						_logger.debug(
								"SituazioneAmministrativa.ricostruzioneStoria():aggiornamento numKlo chiusura DID: "
										+ cDb);

						cDb.updAttribute("numKloDichDisp", did.getAttribute("numKloDichDisp"));
					}
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------
	/**
	 * Aggiorna al nuovo valore il numklo della mobilità nel vettore dei movimenti della situazione amministrativa del
	 * lavoratore
	 * 
	 * @param mob
	 * @param nuovo
	 *            numklo
	 * @param movimenti
	 * @return
	 * @throws Exception
	 */
	public void aggiornaNumKloMobInMovimenti(MobilitaBean mob, BigDecimal numklo, List movimenti) throws Exception {
		int movimentiSize = movimenti.size();
		for (int k = 0; k < movimentiSize; k++) {
			EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
			int tipoEvento = c.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.MOBILITA) {
				MobilitaBean mobApp = (MobilitaBean) c;
				if (mobApp.getPrgMobilitaIscr().equals(mob.getPrgMobilitaIscr())) {
					_logger.debug("StatoOccupazionaleManager2.avviamento():aggiornamento numKlo Mobilità: " + mob);

					mobApp.updAttribute("NUMKLOMOBISCR", numklo);
				}
			} else {
				if (tipoEvento == EventoAmministrativo.CHIUSURA_MOBILITA) {
					ChiusuraMobilitaBean cMob = (ChiusuraMobilitaBean) c;
					if (cMob.getPrgMobilitaIscr().equals(mob.getPrgMobilitaIscr())) {
						_logger.debug(
								"SituazioneAmministrativa.ricostruzioneStoria():aggiornamento numKlo chiusura MOBILITA: "
										+ cMob);

						// cMob.updAttribute("NUMKLOMOBISCR", mob.getAttribute("NUMKLOMOBISCR"));
						cMob.updAttribute("NUMKLOMOBISCR", numklo);
					}
				}
			}
		}
	}

	/**
	 * restituisce l'eventuale did aperta in quella data
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public SourceBean cercaDid(String dataRif) throws Exception {
		SourceBean did = null;
		for (int i = 0; i < dids.size(); i++) {
			DidBean sb = (DidBean) dids.get(i);
			String dataInizio = sb.getDataInizio();
			String dataFine = (String) sb.getAttribute("datFine");
			String codStatoAtto = (String) sb.getAttribute("codStatoAtto");
			if (codStatoAtto.equals("PR") && DateUtils.compare(dataInizio, dataRif) <= 0
					&& (dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, dataRif) >= 0)) {
				did = sb.getSource();
			}
		}
		return did;
	}

	/**
	 * restituisce l'eventuale movimento aperto in quella data
	 * 
	 * @param dataRif
	 * @return
	 * @throws Exception
	 */
	public SourceBean cercaMovimento(String dataRif) throws Exception {
		SourceBean movimentoRitorno = null;
		String codTipoAvviamento = "";
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			Object movimento = movimenti.get(i);
			if (movimento instanceof MovimentoBean) {
				MovimentoBean mb = new MovimentoBean((SourceBean) movimento);
				String dataInizio = mb.getDataInizio();
				String dataFine = mb.getDataFineEffettiva();
				String codStatoAtto = mb.containsAttribute("codStatoAtto") ? mb.getAttribute("codStatoAtto").toString()
						: "";
				String codTipoMov = mb.containsAttribute("codTipoMov") ? mb.getAttribute("codTipoMov").toString() : "";
				codTipoAvviamento = mb.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
						? mb.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
						: "";
				// esclude i movimenti non protocollati, cessazioni, tipo avviamento Z.09.02 (codice vecchio RS3)
				// (cessazione attività lavorativa dopo un periodo di sospeso per contrazione)
				if ((codStatoAtto.equals("PR") || mb.inInserimento()) && !codTipoMov.equals("CES")
						&& !codTipoAvviamento.equals("Z.09.02") && DateUtils.compare(dataInizio, dataRif) <= 0
						&& (dataFine == null || dataFine.equals("") || DateUtils.compare(dataFine, dataRif) >= 0)) {
					movimentoRitorno = mb.getSource();
					break;
				}
			}
		}
		return movimentoRitorno;
	}

	public StatoOccupazionaleBean getStatoOccupazionaleAperto() {
		return this.statoOccupazionaleAperto;
	}

	/**
	 * Si tratta del movimento passato al metodo ricrea().
	 * 
	 * @return
	 */
	public StatoOccupazionaleBean getStatoOccupazionaleMovimentoGestito() {
		return movimentoGestito.getStatoOccupazionale();
	}

	/**
	 * Quando la ricostruzione parte dall' inserimento di una cessazione o trasformazione, bigogna ritornare lo stato
	 * occupazionale associato al movimento che si sta inserendo, che non e' quello passato al metodo ricrea() bensi'
	 * quello ad esso collegato. Per cui bisogna chiamare questo metodo che ritorna appunto lo stato occupazionale
	 * legato al movimento collegato a quello gestito dalla classe ( e passato al metodo ricrea() )
	 * 
	 * @return
	 * @throws Exception
	 */
	public StatoOccupazionaleBean getStatoOccupazionaleMovimentoCollegato() throws Exception {
		int tipoEvento = movimentoGestito.getTipoEventoAmministrativo();
		if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.TRASFORMAZIONE
				|| tipoEvento == EventoAmministrativo.PROROGA || tipoEvento == EventoAmministrativo.CESSAZIONE)
			return ((MovimentoBean) movimentoGestito).getStatoOccupazionale();
		else
			throw new ControlliException(MessageCodes.StatoOccupazionale.ERRORE_GENERICO);
	}

	/**
	 * @return
	 */
	public TransactionQueryExecutor getTxExecutor() {
		return this.txExecutor;
	}

	/**
	 * @return
	 */
	public RequestContainer getRequestContainer() {
		return this.requestContainer;
	}

	/**
	 * Lista di DidBean del lavoratore
	 * 
	 * @return
	 */
	public List getDids() {
		return this.dids;
	}

	/**
	 * Lista di SourceBean dei patti del lavoratore
	 * 
	 * @return
	 */
	public List getPatti() {
		return this.patti;
	}

	/**
	 * @return
	 */
	public List getMessaggi() {
		return messaggi;
	}

	public List getStatiOccupazionaliCreati() {
		return this.statiOccupazionaliCreati;
	}

	/**
	 * @param b
	 */
	public void setForzaRicostruzione(boolean b) {
		forzaRicostruzione = b;
	}

	public boolean getForzaRicostruzione() {
		return forzaRicostruzione;
	}

	private DidBean getDidPrecedente(DidBean did) throws Exception {
		DidBean didPrec = null;
		if (did.getAttribute("codStatoAtto").equals("PA") && did.getAttribute("datFine") != null)
			return null;
		for (int i = 0; i < dids.size(); i++) {
			DidBean db = (DidBean) dids.get(i);
			if (db.getPrgDichDisponibilita().equals(did.getPrgDichDisponibilita()))
				break;
			else
				didPrec = db;
		}
		return didPrec;
	}

	/**
	 * Lista degli stati occupazionali cancellati. Gli oggetti sono StatoOccupazionaleBean
	 */
	public List getStatiOccupazionaliCancellati() {
		return this.statiOccupazionaliCancellati;
	}

	/**
	 * Aggiunge nella lista dei warnings uno oggetto Warning con codice code
	 * 
	 * @param code
	 * @throws Exception
	 */
	public void addWarning(int code) throws Exception {
		List warnings = null;
		if (requestContainer.getServiceRequest()
				.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE)) {
			warnings = (List) requestContainer.getServiceRequest()
					.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
		} else {
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
				if (objWarning != null && objWarning.getCode() == code) {
					esisteWarning = true;
					break;
				}
			}
		}
		if (!esisteWarning) {
			warnings.add(new Warning(code, ""));
		}
	}

	/**
	 * Aggiunge nella lista dei warnings uno oggetto Warning con codice code e sostituisce i parametri contenuti in
	 * params
	 * 
	 * @param code
	 * @param params
	 * @throws Exception
	 */
	public void addWarning(int code, int codeDettaglio, Vector params) throws Exception {
		List warnings = null;
		if (requestContainer.getServiceRequest()
				.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE)) {
			warnings = (List) requestContainer.getServiceRequest()
					.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
		} else {
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
				if (objWarning != null && objWarning.getCode() == code) {
					esisteWarning = true;
					break;
				}
			}
		}
		if (!esisteWarning) {
			warnings.add(new Warning(code, codeDettaglio, params));
		}
	}

	/**
	 * Aggiunge nella lista dei warnings uno oggetto Warning
	 * 
	 * @param code
	 *            codice warning
	 * @param dettaglio
	 *            descrizione del dettaglio
	 * @throws Exception
	 */
	public void addWarning(int code, String dettaglio) throws Exception {
		List warnings = null;
		if (requestContainer.getServiceRequest()
				.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE)) {
			warnings = (List) requestContainer.getServiceRequest()
					.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
		} else {
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
				if (objWarning != null && objWarning.getCode() == code) {
					esisteWarning = true;
					break;
				}
			}
		}
		if (!esisteWarning) {
			warnings.add(new Warning(code, dettaglio));
		}
	}

	/**
	 * Stampa sul file di log le informazioni sui movimenti in gestione
	 * 
	 * @throws Exception
	 */
	private void logMovimenti() throws Exception {
		_logger.debug("SituazioneAmministrativa: movimenti ordinati:\r\n");

		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++)
			_logger.debug("\r\nSituazioneAmministrativa: " + movimenti.get(i));

	}

	/**
	 * Allineamento fine Mobilita (nota: se ho un movimento a tempo indeterminato che comporta l'uscita dalla mobilità,
	 * la data evento fine mobilità è settata al giorno di inizio del movimento stesso, perché il giorno precedente il
	 * lavoratore risulta ancora in mobilità
	 * 
	 * @throws Exception
	 */
	private void AllineamentoMobilita() throws Exception {
		Object obj = null;
		Object objApp = null;
		Object objApp2 = null;
		Object objMobSucc = null;
		SourceBean sbMobilita = null;
		SourceBean sbMobilitaSucc = null;
		int k, j;
		int i = 0;
		int indiceFineMobilita = -1;
		boolean uscitaMobilita = false;
		boolean generaUscitaMobilita = false;
		boolean generaUscitaMobilitaPerReddito = false;
		boolean cancellaDecadenzaTI = true;
		boolean cancellaDecadenzaReddito = true;
		String dataInizioMovTI = "";
		MovimentoBean movInMobilita = null;
		MobilitaBean mobilita = null;
		ChiusuraMobilitaBean chiusuraMobilita = null;
		String dataChiusuraMobilita = "";
		String dataInizioMobilita = "";
		String dataInizioMobilitaOrig = "";
		String dataFineMovTI = "";
		BigDecimal numkloMob = null;
		BigDecimal prgMobilitaIscr = null;
		BigDecimal prgMobilitaCorr = null;
		BigDecimal prgMobilita = null;
		String codComuneUaz = "";
		String flgMobilitaAperta = "";
		BigDecimal mesiMobilitaAperta = null;
		String dataChiusuraMobAllineata = "";
		String datFineMobOrig = "";
		String datFineMob = "";
		String datMaxDiff = "";
		String datFineMobDopoScorr = "";
		int indicePosMobInLista = -1;
		boolean scorrimentoMotivoFine = true;
		boolean decadenzaMotivoFine = true;
		String codMotivoFine = "";
		boolean scorrimentoInterseca = false;
		Boolean scorrimentoEseguito = new Boolean(false);
		BigDecimal userID = (BigDecimal) requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Vector vettIndispTemp = null;
		Vector movimentiAmm = new Vector();
		int annoCorrente = DateUtils.getAnno();
		int annoFornero = DateUtils.getAnno(MessageCodes.General.DATA_DECRETO_FORNERO_2014);
		int movimentiSize = 0;
		Map<BigDecimal, String> mapProrogati = new HashMap<BigDecimal, String>();

		for (int iCont = 0; iCont < movimenti.size(); iCont++) {
			EventoAmministrativo evento = (EventoAmministrativo) movimenti.get(iCont);
			if (evento.getTipoEventoAmministrativo() == EventoAmministrativo.AVVIAMENTO
					|| evento.getTipoEventoAmministrativo() == EventoAmministrativo.PROROGA
					|| evento.getTipoEventoAmministrativo() == EventoAmministrativo.TRASFORMAZIONE) {
				movimentiAmm.add(evento);
			}
		}

		try {
			if ((Sottosistema.MO.isOn())
					&& (!requestContainer.getServiceRequest().containsAttribute("DISABILITA_SCORRIMENTO_MOBILITA"))) {
				// calcolo eventuale scorrimento
				if (cdnLavoratoreSitAmm != null) {
					vettIndispTemp = DBLoad.getIndisponibilitaLavoratore(cdnLavoratoreSitAmm, "I4", this.txExecutor);
				}
			}
			movimentiSize = movimenti.size();

			for (; i < movimentiSize; i++) {
				obj = movimenti.get(i);
				dataChiusuraMobilita = "";
				if (obj instanceof ChiusuraMobilitaBean)
					continue;
				if (obj instanceof MobilitaBean) {
					cancellaDecadenzaTI = true;
					cancellaDecadenzaReddito = true;
					generaUscitaMobilitaPerReddito = false;
					mobilita = (MobilitaBean) movimenti.get(i);
					dataInizioMobilita = mobilita.getDataInizio();
					dataInizioMobilitaOrig = dataInizioMobilita;
					// Se trovo una mobilita con data inizio > sysdate, allora ho terminato
					// la fase di allineamento (scorrimento e decadenza)
					if (DateUtils.compare(dataInizioMobilita, DateUtils.getNow()) > 0)
						break;
					prgMobilita = mobilita.getPrgMobilitaIscr();
					datFineMobOrig = mobilita.getDataFineOrig();
					datFineMob = mobilita.getDataFine();
					datMaxDiff = mobilita.getDataMaxDiff();
					scorrimentoMotivoFine = !(mobilita.containsAttribute("FLGSCORRIMENTO")
							&& mobilita.getAttribute("FLGSCORRIMENTO").toString().equalsIgnoreCase("N"));
					decadenzaMotivoFine = !(mobilita.containsAttribute("FLGDECADENZA")
							&& mobilita.getAttribute("FLGDECADENZA").toString().equalsIgnoreCase("N"));
					codMotivoFine = mobilita.containsAttribute("CODMOTIVOFINE")
							? mobilita.getAttribute("CODMOTIVOFINE").toString()
							: "";
					for (j = i + 1; j < movimentiSize; j++) {
						objApp = movimenti.get(j);
						if (objApp instanceof ChiusuraMobilitaBean) {
							chiusuraMobilita = (ChiusuraMobilitaBean) objApp;
							dataChiusuraMobilita = chiusuraMobilita.getDataInizio();
							indiceFineMobilita = j;
							break;
						}
					}
					// ricerca della mobilita nella listaMobilita per possibili
					// aggiornamenti della data inizio e/o data fine
					indicePosMobInLista = -1;
					for (int iCont = 0; iCont < listaMobilita.size(); iCont++) {
						sbMobilita = (SourceBean) listaMobilita.get(iCont);
						prgMobilitaCorr = new BigDecimal(
								sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR).toString());
						if (prgMobilitaCorr.equals(prgMobilita)) {
							indicePosMobInLista = iCont;
							break;
						}
					}

					// se la mobilità è precedente al 30/01/2003 oppure alla data presente in configurazione, allora non
					// la considero
					if (DateUtils.compare(dataInizioMobilita, dataPrec297) < 0 && !datFineMob.equals("")
							&& DateUtils.compare(datFineMob, dataPrec297) < 0) {
						continue;
					}

					// se la mobilità si interseca con la data 30/01/2003 oppure alla data presente in configurazione,
					// allora devo settare la data inizio della mobilità alla data riferimento normativa
					if (DateUtils.compare(dataInizioMobilita, dataPrec297) < 0
							&& (datFineMob.equals("") || DateUtils.compare(datFineMob, dataPrec297) >= 0)) {
						dataInizioMobilita = dataPrec297;
						mobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO, dataInizioMobilita);
						// devo aggiornare anche la mobilità nella lista delle mobilità(listaMobilita)
						if (indicePosMobInLista >= 0) {
							sbMobilita = (SourceBean) listaMobilita.get(indicePosMobInLista);
							sbMobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO, dataInizioMobilita);
							sbMobilita.setAttribute("DATINIZIOORIGINALE", dataInizioMobilitaOrig);
						}
					}

					// no decadenza per reddito
					int annoInizialeMob = 0;
					int annoRedditoIniziale = 0;
					int annoRedditoFinale = 0;
					double limiteAttuale = 0;
					Reddito reddito = null;
					Reddito redditoMobilita = null;
					LimiteRedditoExt limiteReddito = null;
					String dataReddito = "";
					String dataRifRedditoAnno = "";
					Vector movimentiAnno = null;
					Vector movimentiAnnoMobilita = null;
					int ggDiLavoro = 0;
					int ggDiLavoroAnnoSuccessivo = 0;
					boolean decadenzaPerReddito = false;
					boolean superamentoRedditoInMobilita = false;
					BigDecimal retribuzione = null;
					if (Sottosistema.MO.isOn()) {
						annoInizialeMob = DateUtils.getAnno(dataInizioMobilita);
						annoRedditoIniziale = annoInizialeMob;
						annoRedditoFinale = annoInizialeMob;
					}

					// eventuale scorrimento se l'interruttore della mobilità è ON e provengo da un contesto
					// in cui lo scorrimento non è disabilitato (gestione movimenti, condizione di maternità,
					// validazione movimenti, dalla gestione manuale, quando cambia datFineIndennizzo)
					if (Sottosistema.MO.isOn()) {
						if ((scorrimentoMotivoFine) && (!requestContainer.getServiceRequest()
								.containsAttribute("DISABILITA_SCORRIMENTO_MOBILITA"))) {
							// calcolo eventuale scorrimento
							Vector vettDateDopoScorr = null;
							if (vettIndispTemp != null && vettIndispTemp.size() > 0
									&& this.cdnLavoratoreSitAmm != null) {
								boolean addLavoratoreRequest = false;
								boolean addComuneUAz = false;
								boolean addDataFineMovMob = false;
								String codComPrec = "";
								String datFineMovPrec = "";
								String dataMaxDiffX = datMaxDiff; // (data max diff memorizzata sul db)
								String dataMaxDiffOrig = dataInizioMobilitaOrig;
								// Ricalcolo data max differimento a partire dalla data fine originaria in base all'età
								// del lavoratore
								// rispetto alla data fine movimento che ha messo il lavoratore in mobilità e al comune
								// dell'unità produttiva
								String dataFineMovMobilita = DateUtils.giornoPrecedente(dataInizioMobilitaOrig);
								codComuneUaz = mobilita.getAttribute("CODCOM") != null
										? mobilita.getAttribute("CODCOM").toString()
										: "";
								SourceBean serviceResponseNew = new SourceBean("SERVICE_RESPONSE");
								ResponseContainer responseContainer = new ResponseContainer();
								if (!requestContainer.getServiceRequest().containsAttribute("cdnLavoratore")) {
									requestContainer.getServiceRequest().setAttribute("cdnLavoratore",
											this.cdnLavoratoreSitAmm);
									addLavoratoreRequest = true;
								}
								if (!requestContainer.getServiceRequest().containsAttribute("DATFINEMOV")) {
									requestContainer.getServiceRequest().setAttribute("DATFINEMOV",
											dataFineMovMobilita);
									addDataFineMovMob = true;
								} else {
									datFineMovPrec = requestContainer.getServiceRequest().getAttribute("DATFINEMOV")
											.toString();
									requestContainer.getServiceRequest().updAttribute("DATFINEMOV",
											dataFineMovMobilita);
								}

								DefaultRequestContext drc = new DefaultRequestContext(requestContainer,
										responseContainer);
								ModuleIFace mobilitaDifferimento = ModuleFactory.getModule("M_MOB_DURATAMOBILITA");
								((AbstractModule) mobilitaDifferimento).setRequestContext(drc);
								mobilitaDifferimento.service(requestContainer.getServiceRequest(), serviceResponseNew);
								BigDecimal numMesi = null;
								if (serviceResponseNew.getAttribute("DURATA_MOB.MESI") != null) {
									numMesi = new BigDecimal(
											serviceResponseNew.getAttribute("DURATA_MOB.MESI").toString());
									if (numMesi.intValue() > 0) {
										dataMaxDiffOrig = DateUtils.aggiungiMesi(dataMaxDiffOrig,
												(2 * numMesi.intValue()), -1);
									}
								}
								if (!codComuneUaz.equals("")) {
									if (!requestContainer.getServiceRequest().containsAttribute("CODCOM")) {
										requestContainer.getServiceRequest().setAttribute("CODCOM", codComuneUaz);
										addComuneUAz = true;
									} else {
										codComPrec = requestContainer.getServiceRequest().getAttribute("CODCOM")
												.toString();
										requestContainer.getServiceRequest().updAttribute("CODCOM", codComuneUaz);
									}
									serviceResponseNew = null;
									responseContainer = null;
									drc = null;
									serviceResponseNew = new SourceBean("SERVICE_RESPONSE");
									responseContainer = new ResponseContainer();
									drc = new DefaultRequestContext(requestContainer, responseContainer);
									ModuleIFace mesiCassaMezz = ModuleFactory.getModule("M_MOB_MESIMOBEXCASSAMEZZ");
									((AbstractModule) mesiCassaMezz).setRequestContext(drc);
									mesiCassaMezz.service(requestContainer.getServiceRequest(), serviceResponseNew);
									BigDecimal numMesiComune = null;
									if (serviceResponseNew.getAttribute("ROWS.ROW.NUMMESIMOBEXCASSAMEZZ") != null) {
										numMesiComune = new BigDecimal(serviceResponseNew
												.getAttribute("ROWS.ROW.NUMMESIMOBEXCASSAMEZZ").toString());
										if (numMesiComune.intValue() > 0) {
											dataMaxDiffOrig = DateUtils.aggiungiMesi(dataMaxDiffOrig,
													numMesiComune.intValue(), 0);
										}
									}
									if (addComuneUAz) {
										requestContainer.getServiceRequest().delAttribute("CODCOM");
									} else {
										requestContainer.getServiceRequest().updAttribute("CODCOM", codComPrec);
									}
								}
								if (addLavoratoreRequest) {
									requestContainer.getServiceRequest().delAttribute("cdnLavoratore");
								}
								if (addDataFineMovMob) {
									requestContainer.getServiceRequest().delAttribute("DATFINEMOV");
								} else {
									requestContainer.getServiceRequest().updAttribute("DATFINEMOV", datFineMovPrec);
								}

								String datMaxDiffY = dataMaxDiffOrig;
								vettDateDopoScorr = UtilsMobilita.ScorrimentoMobilita(prgMobilita,
										dataInizioMobilitaOrig, datFineMobOrig, dataMaxDiffOrig, decadenzaMotivoFine,
										i + 1, this.movimenti, vettIndispTemp);
								if (vettDateDopoScorr != null) {
									String datMaxDiffZ = vettDateDopoScorr.get(1).toString();
									if (DateUtils.compare(datMaxDiffY, datMaxDiffZ) != 0) {
										datMaxDiff = datMaxDiffZ;
									} else {
										datMaxDiff = dataMaxDiffX;
									}
								}
							} else {
								vettDateDopoScorr = UtilsMobilita.ScorrimentoMobilita(prgMobilita,
										dataInizioMobilitaOrig, datFineMobOrig, datMaxDiff, decadenzaMotivoFine, i + 1,
										this.movimenti, vettIndispTemp);
								if (vettDateDopoScorr != null) {
									datMaxDiff = vettDateDopoScorr.get(1).toString();
								}
							}
							// aggiornamento degli oggetti in memoria che rappresentano la mobilità
							// con la nuova data fine se c'è stato scorrimento ed eventualmente la nuova data max diff.
							if (vettDateDopoScorr != null) {
								datFineMobDopoScorr = vettDateDopoScorr.get(0).toString();
								scorrimentoEseguito = (Boolean) vettDateDopoScorr.get(2);
								if (!datFineMobDopoScorr.equals("") && !datFineMobDopoScorr.equals(datFineMob)) {
									scorrimentoInterseca = false;
									if ((indicePosMobInLista >= 0)
											&& (indicePosMobInLista < (listaMobilita.size() - 1))) {
										// verifico se lo scorrimento provoca intersezione e in tal caso lo scorrimento
										// non viene fatto; bisogna segnalare con un warning che non è stato fatto
										// lo scorrimento per questa mobilità
										sbMobilitaSucc = (SourceBean) listaMobilita.get(indicePosMobInLista + 1);
										if (DateUtils.compare(datFineMobDopoScorr, sbMobilitaSucc
												.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString()) >= 0) {
											scorrimentoInterseca = true;
											// warning per segnalare che lo scorrimento della mobilità produce
											// intersezione
											addWarning(MessageCodes.ScorrimentoMobilita.INTERSEZIONE_MOBILITA,
													"Mobilità con data inizio " + dataInizioMobilitaOrig);
										}
									}
									if (!scorrimentoInterseca) {
										if (mobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)) {
											mobilita.updAttribute(MobilitaBean.DB_DAT_FINE, datFineMobDopoScorr);
										} else {
											mobilita.setAttribute(MobilitaBean.DB_DAT_FINE, datFineMobDopoScorr);
										}
										dataChiusuraMobilita = DateUtils.giornoSuccessivo(datFineMobDopoScorr);

										if (chiusuraMobilita.containsAttribute(MobilitaBean.DB_DAT_INIZIO)) {
											chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO,
													dataChiusuraMobilita);
										} else {
											chiusuraMobilita.setAttribute(MobilitaBean.DB_DAT_INIZIO,
													dataChiusuraMobilita);
										}

										if (chiusuraMobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)) {
											chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_FINE,
													dataChiusuraMobilita);
										} else {
											chiusuraMobilita.setAttribute(MobilitaBean.DB_DAT_FINE,
													dataChiusuraMobilita);
										}
										// devo aggiornare anche la mobilità nella lista delle mobilità
										if (indicePosMobInLista >= 0) {
											sbMobilita = (SourceBean) listaMobilita.get(indicePosMobInLista);
											sbMobilita.updAttribute(MobilitaBean.DB_DAT_FINE, datFineMobDopoScorr);
										}
										// Aggiornamento sul db
										numkloMob = new BigDecimal(mobilita.getAttribute("NUMKLOMOBISCR").toString());
										numkloMob = DBStore.aggiornaMobilitaScorrimento(prgMobilita,
												datFineMobDopoScorr, datMaxDiff, userID, numkloMob, this.txExecutor);
										// --------------------------- Aggiorno il numklo dell'evento mobilità e evento
										// chiusura mobilità nella lista degli eventi Movimenti
										mobilita.updAttribute("NUMKLOMOBISCR", numkloMob);
										aggiornaNumKloMobInMovimenti(mobilita, numkloMob, movimenti);
										UtilsMobilita.aggiornaMobilitaInListaMobilita(this.getListaMobilita(),
												chiusuraMobilita, numkloMob);
										if (getPrgMobilitaRicalcolo() == null) {
											setPrgMobilitaRicalcolo(prgMobilita);
										}
										if (scorrimentoEseguito.booleanValue()) {
											// warning per segnalare l'avvenuto scorrimento
											addWarning(MessageCodes.ScorrimentoMobilita.OPERAZIONE_OK,
													"Mobilità con data inizio " + dataInizioMobilitaOrig);
										}
									}
								}
							}
						} // if ( (scorrimentoMotivoFine) &&
							// (!requestContainer.getServiceRequest().containsAttribute("DISABILITA_SCORRIMENTO_MOBILITA"))
							// )
					} // if (Sottosistema.MO.isOn())

					// Decadenza : ricerca di eventuali movimenti non compatibili con l'iscrizione alla mobilità
					// se il flag decadenzaMotivoFine = true
					if (decadenzaMotivoFine) {
						if (dataChiusuraMobilita != null && !dataChiusuraMobilita.equals("")) {
							String dataFineMobEffettiva = DateUtils.giornoPrecedente(dataChiusuraMobilita);
							int annoFineMob = DateUtils.getAnno(dataFineMobEffettiva);
							for (j = 0; j < movimentiSize && !generaUscitaMobilitaPerReddito; j++) {
								objApp = movimenti.get(j);
								if (objApp instanceof MovimentoBean) {
									movInMobilita = (MovimentoBean) objApp;
									// Se trovo movimenti in data futura termino il controllo sulla decadenza
									if (DateUtils.compare(movInMobilita.getDataInizio(), DateUtils.getNow()) > 0)
										break;
									String codTipoAvviamento = movInMobilita
											.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
													? movInMobilita.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
															.toString()
													: "";
									// Non considero TIPO AVVIAMENTO Z.09.02 (codice vecchio RS3) (cessazione attività
									// lavorativa dopo un periodo di sospeso per contrazione)
									if (codTipoAvviamento.equals("Z.09.02"))
										continue;
									// decadenza per movimenti a tempo indeterminato full-time,
									// che non siano: lavoro autonomo, tirocini, occasionale, progetto, cococo
									if (!movInMobilita.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
											.equalsIgnoreCase("CES")
											&& DateUtils.compare(movInMobilita.getDataInizio(),
													dataFineMobEffettiva) <= 0) {
										String codMonoTipoAss = movInMobilita
												.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
														? movInMobilita.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
																.toString()
														: "";
										String codContratto = movInMobilita
												.containsAttribute(MovimentoBean.DB_COD_CONTRATTO)
														? movInMobilita.getAttribute(MovimentoBean.DB_COD_CONTRATTO)
																.toString()
														: "";
										flgMobilitaAperta = movInMobilita.containsAttribute("FLGMOBILITARIMANEAPERTA")
												? movInMobilita.getAttribute("FLGMOBILITARIMANEAPERTA").toString()
												: "";
										mesiMobilitaAperta = (BigDecimal) movInMobilita
												.getAttribute("MESIMOBILITAAPERTA");
										String codMonoTempo = movInMobilita
												.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString();
										if (codMonoTempo.equalsIgnoreCase("I")
												&& flgMobilitaAperta.equalsIgnoreCase("S")
												&& mesiMobilitaAperta != null) {
											flgMobilitaAperta = Controlli
													.controlloMobilitaApertaDurata(flgMobilitaAperta, movInMobilita);
										}

										if (codMonoTempo.equalsIgnoreCase("I")
												&& !flgMobilitaAperta.equalsIgnoreCase("S")
												&& movInMobilita.containsAttribute("codOrario")
												&& (movInMobilita.getAttribute("codOrario").toString()
														.equalsIgnoreCase("F")
														|| (movInMobilita.getAttribute("codOrario").toString()
																.equalsIgnoreCase("N")
																&& (!movInMobilita.containsAttribute("NUMORESETT")
																		|| movInMobilita.getAttribute("NUMORESETT")
																				.toString().equals(""))))
												&& !codMonoTipoAss.equalsIgnoreCase(MovimentoBean.CODMONOTIPOAUTONOMO)
												&& !codMonoTipoAss.equalsIgnoreCase("T")
												&& !codContratto.equalsIgnoreCase("LO")
												&& !codContratto.equalsIgnoreCase("PG")
												&& !codContratto.equalsIgnoreCase("CO")
												&& !codContratto.equalsIgnoreCase("LI")) {
											dataInizioMovTI = movInMobilita.getDataInizio();
											dataFineMovTI = movInMobilita.getDataFineEffettiva();
											if (dataFineMovTI == null || dataFineMovTI.equals("")) {
												cancellaDecadenzaTI = false;
												uscitaMobilita = true;
												objApp2 = movimenti.get(indiceFineMobilita);
												chiusuraMobilita = (ChiusuraMobilitaBean) objApp2;
												// vado a eliminare eventuali mobilità successive che non hanno senso
												// a partire dalla chiusura mobilità trovata in precedenza
												for (k = indiceFineMobilita + 1; k < movimentiSize; k++) {
													objMobSucc = movimenti.get(k);
													if (objMobSucc instanceof ChiusuraMobilitaBean) {
														continue;
													}
													if (objMobSucc instanceof MobilitaBean) {
														// || objMobSucc instanceof ChiusuraMobilitaBean) {
														sbMobilita = (SourceBean) movimenti.get(k);
														prgMobilitaIscr = (BigDecimal) sbMobilita
																.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
														// nel vettore dei movimenti sia per l'evento MobilitaBean che
														// ChiusuraMobilitaBean setto l'attributo
														// MOBILITA_NON_IMPATTANTE
														// (non è necessario aggiornare le date in quanto gli eventi nel
														// ricalcolo non vengono considerati).
														UtilsMobilita.rimuoviMobilitaDaMovimenti(prgMobilitaIscr,
																this.movimenti);
														// nella lista listaMobilita l'evento MobilitaBean viene
														// eliminato fisicamente
														UtilsMobilita.rimuoviMobDallaListaMobilita(prgMobilitaIscr,
																this.getListaMobilita());
														// aggiornamento della decadenza sul db delle mobilità
														// successive
														if (Sottosistema.MO.isOn()) {

															numkloMob = new BigDecimal(sbMobilita
																	.getAttribute("NUMKLOMOBISCR").toString());
															String dataDecadenza = sbMobilita
																	.getAttribute(MobilitaBean.DB_DAT_INIZIO)
																	.toString();
															numkloMob = DBStore.aggiornaDataFineMobilita(
																	prgMobilitaIscr, dataDecadenza,
																	MobilitaBean.motivoDecadenzaTI, userID, numkloMob,
																	this.txExecutor);
															// --------------------------- Aggiorno il numklo
															// dell'evento mobilità e evento chiusura mobilità nella
															// lista degli eventi Movimenti
															sbMobilita.updAttribute("NUMKLOMOBISCR", numkloMob);
															aggiornaNumKloMobInMovimenti(new MobilitaBean(sbMobilita),
																	numkloMob, movimenti);
														}
													}
												}
											} else {
												if (DateUtils.compare(dataFineMovTI, dataInizioMobilitaOrig) >= 0) {
													cancellaDecadenzaTI = false;
												}
											}
											// controllo se devo o meno considerare la mobilità corrente
											if (dataFineMovTI == null || dataFineMovTI.equals("")
													|| DateUtils.compare(dataFineMovTI, dataInizioMobilitaOrig) >= 0) {
												if (Sottosistema.MO.isOn() && getPrgMobilitaRicalcolo() == null) {
													setPrgMobilitaRicalcolo(prgMobilita);
												}
												if (DateUtils.compare(dataInizioMobilitaOrig, dataPrec297) < 0) {
													if (DateUtils.compare(dataInizioMovTI, dataPrec297) < 0) {
														mobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO,
																dataInizioMobilitaOrig);
														// devo aggiornare anche la mobilità nella lista delle mobilità
														if (indicePosMobInLista >= 0) {
															sbMobilita = (SourceBean) listaMobilita
																	.get(indicePosMobInLista);
															sbMobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO,
																	dataInizioMobilitaOrig);
														}
													}
												}

												if (DateUtils.compare(dataInizioMovTI, dataInizioMobilitaOrig) <= 0) {
													// cancello la mobilità e la chiusura mobilità corrente
													// se il movimento a t.i. inizia prima del periodo di mobilità.
													sbMobilita = (SourceBean) movimenti.get(i);
													prgMobilitaIscr = (BigDecimal) sbMobilita
															.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
													// nel vettore dei movimenti sia per l'evento MobilitaBean che
													// ChiusuraMobilitaBean setto l'attributo MOBILITA_NON_IMPATTANTE
													// (non è necessario aggiornare le date in quanto gli eventi nel
													// ricalcolo non vengono considerati).
													UtilsMobilita.rimuoviMobilitaDaMovimenti(prgMobilitaIscr,
															this.movimenti);
													// nella lista listaMobilita l'evento MobilitaBean viene eliminato
													// fisicamente
													UtilsMobilita.rimuoviMobDallaListaMobilita(prgMobilitaIscr,
															this.getListaMobilita());
													// aggiornamento della decadenza sul db
													if (Sottosistema.MO.isOn()) {
														numkloMob = new BigDecimal(
																sbMobilita.getAttribute("NUMKLOMOBISCR").toString());
														String dataDecadenza = sbMobilita
																.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
														numkloMob = DBStore.aggiornaDataFineMobilita(prgMobilitaIscr,
																dataDecadenza, MobilitaBean.motivoDecadenzaTI, userID,
																numkloMob, this.txExecutor);
														// --------------------------- Aggiorno il numklo dell'evento
														// mobilità e evento chiusura mobilità nella lista degli eventi
														// Movimenti
														sbMobilita.updAttribute("NUMKLOMOBISCR", numkloMob);
														aggiornaNumKloMobInMovimenti(new MobilitaBean(sbMobilita),
																numkloMob, movimenti);

													}
													if (uscitaMobilita) {
														throw new MobilitaException(
																MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA);
													} else {
														generaUscitaMobilita = true;
														break;
													}
												} else {
													String dataDecadenza = DateUtils.giornoPrecedente(dataInizioMovTI);
													if (Sottosistema.MO.isOff()) {
														// sposto la fine della mobilità al giorno inizio del mov a t.i.
														chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO,
																dataInizioMovTI);
														chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_FINE,
																dataInizioMovTI);
														UtilsMobilita.aggiornaMobilitaInListaMobilita(
																this.getListaMobilita(), chiusuraMobilita);
													} else {
														sbMobilita = (SourceBean) movimenti.get(i);
														BigDecimal numKloMobNew = UtilsMobilita.settaDecadenzaMobilita(
																sbMobilita, chiusuraMobilita, this.movimenti,
																movInMobilita, dataInizioMovTI, userID,
																this.getListaMobilita(), MobilitaBean.motivoDecadenzaTI,
																this.txExecutor);
														aggiornaNumKloMobInMovimenti(new MobilitaBean(sbMobilita),
																numKloMobNew, movimenti);
													}
													UtilsMobilita.settaMovimentoFuoriMobilita(this.movimenti,
															movInMobilita, dataInizioMovTI);
													if (mobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)) {
														mobilita.updAttribute(MobilitaBean.DB_DAT_FINE, dataDecadenza);
													} else {
														mobilita.setAttribute(MobilitaBean.DB_DAT_FINE, dataDecadenza);
													}

													if (uscitaMobilita) {
														throw new MobilitaException(
																MessageCodes.Mobilita.USCITA_MOBILITA);
													} else {
														generaUscitaMobilita = true;
														break;
													}
												}
											}
										} else {
											// devo considerare il reddito per la decadenza della mobilità, tranne i
											// tirocini
											// se arriviamo qui, codTipoMov <> CES e data Inizio Mov <= data fine mob
											// effettiva
											if (Sottosistema.MO.isOn() && !codMonoTipoAss.equalsIgnoreCase("T")) {
												BigDecimal prgMovimentoInMob = movInMobilita.getPrgMovimento();
												BigDecimal prgPeriodoLav = (BigDecimal) movInMobilita
														.getAttribute("PRGPERIODOINTERMITTENTE");
												String dataInizioMovReddito = movInMobilita.getDataInizio();
												String dataFineMovReddito = movInMobilita.getDataFineEffettiva();
												boolean gestione150 = (DateUtils.compare(dataInizioMovReddito,
														getData150()) >= 0);
												boolean iscrittoCM = Controlli.inCollocamentoMiratoAllaData(
														getListaDisabiliCM(), dataInizioMovReddito);
												boolean movDaTrattare = false;
												// se il movimento è un avviamento considero gli eventuali movimenti
												// prorogati per determinare la data fine
												String codTipoMov = movInMobilita
														.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString();
												if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_ASSUNZIONE)) {
													movDaTrattare = true;
													if (movInMobilita.getAttribute("MOVIMENTI_PROROGATI") != null) {
														Vector prec = (Vector) movInMobilita
																.getAttribute("MOVIMENTI_PROROGATI");
														int precSize = prec.size();
														if (precSize > 0) {
															int movUltimoCatena = precSize - 1;
															SourceBean movimentoUltimo = (SourceBean) prec
																	.get(movUltimoCatena);
															dataFineMovReddito = movimentoUltimo.containsAttribute(
																	MovimentoBean.DB_DATA_FINE_EFFETTIVA)
																			? movimentoUltimo.getAttribute(
																					MovimentoBean.DB_DATA_FINE_EFFETTIVA)
																					.toString()
																			: "";
															for (int kPrec = 0; kPrec < precSize; kPrec++) {
																SourceBean movimentoCatena = (SourceBean) prec
																		.get(kPrec);
																BigDecimal prgMovProrogato = (BigDecimal) movimentoCatena
																		.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
																mapProrogati.put(prgMovProrogato, "S");
															}
														}
													}
												} else {
													if (!mapProrogati.containsKey(prgMovimentoInMob)) {
														movDaTrattare = true;
													}
												}

												if (movDaTrattare) {
													if ((DateUtils.compare(dataInizioMovReddito,
															MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0)
															|| (dataFineMovReddito == null
																	|| dataFineMovReddito.equals("")
																	|| DateUtils.compare(dataFineMovReddito,
																			MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0)) {
														// controllo se il movimento si trova nel periodo di mobilità
														if (dataFineMovReddito == null || dataFineMovReddito.equals("")
																|| DateUtils.compare(dataFineMovReddito,
																		dataInizioMobilita) >= 0) {

															if ((dataFineMovReddito == null)
																	|| (dataFineMovReddito.equals(""))
																	|| (annoFineMob < DateUtils
																			.getAnno(dataFineMovReddito))) {
																dataFineMovReddito = "31/12/" + annoFineMob;
															}
															annoRedditoFinale = DateUtils.getAnno(dataFineMovReddito);
															int annoInizioMov = DateUtils.getAnno(dataInizioMovReddito);
															if (annoInizioMov < annoInizialeMob) {
																annoRedditoIniziale = annoInizialeMob;
															} else {
																annoRedditoIniziale = annoInizioMov;
															}
															// per i rapporti subordinati c'è sospensione a prescindere
															// dalla durata nel caso si superasse il reddito
															// per gli altri tipi di rapporto se c'è superamento del
															// reddito, la mobilità decade e il lavoratore deve
															// risultare Occupato
															String flgSospensione = movInMobilita.containsAttribute(
																	MovimentoBean.DB_FLG_SOSPENSIONE_2014)
																			? movInMobilita.getAttribute(
																					MovimentoBean.DB_FLG_SOSPENSIONE_2014)
																					.toString()
																			: "";
															// se il movimento è un rapporto autonomo devo considerare
															// solo i redditi di lavoro autonomo
															// se il movimento è un rapporto parasubordinato devo
															// considerare solo i redditi di lavoro parasubordinato
															String tipoRapporto = "";
															if (codMonoTipoAss.equalsIgnoreCase(
																	MovimentoBean.CODMONOTIPOAUTONOMO)) {
																tipoRapporto = MovimentoBean.RAPPORTOAUTONOMO;
															} else {
																if (flgSospensione.equalsIgnoreCase(MovimentoBean.SI)) {
																	tipoRapporto = MovimentoBean.RAPPORTOSUBORDINATO;
																} else {
																	tipoRapporto = MovimentoBean.RAPPORTOPARASUBORDINATO;
																}
															}
															// reddito calcolato anno per anno, tra l'anno iniziale e
															// l'anno di fine del movimento
															for (int jAnno = annoRedditoIniziale; jAnno <= annoRedditoFinale
																	&& jAnno <= annoCorrente; jAnno++) {
																if (jAnno >= annoFornero) {
																	String dataFineMovRedditoAnno = "";
																	String dataInizioMovRedditoAnno = "";
																	if (DateUtils
																			.getAnno(dataInizioMovReddito) == jAnno) {
																		dataInizioMovRedditoAnno = dataInizioMovReddito;
																	} else {
																		dataInizioMovRedditoAnno = "01/01/" + jAnno;
																	}
																	dataReddito = dataInizioMovRedditoAnno;
																	if (DateUtils.getAnno(dataFineMovReddito) > jAnno) {
																		dataFineMovRedditoAnno = "31/12/" + jAnno;
																	} else {
																		dataFineMovRedditoAnno = dataFineMovReddito;
																	}

																	if (jAnno == annoInizialeMob) {
																		SourceBean didAperta = cercaDid(
																				dataInizioMobilita);
																		if (didAperta != null) {
																			String dataDichiarazione = (String) didAperta
																					.getAttribute("datDichiarazione");
																			if (dataDichiarazione != null
																					&& !dataDichiarazione.equals("")) {
																				if (DateUtils.getAnno(
																						dataDichiarazione) < annoInizialeMob) {
																					dataInizioMobilita = "01/01/"
																							+ annoInizialeMob;
																				} else {
																					dataInizioMobilita = dataDichiarazione;
																				}
																			}
																		}
																	}

																	reddito = new Reddito(0, 0);
																	redditoMobilita = new Reddito(0, 0);
																	limiteAttuale = 0;
																	// estrae i movimenti dell'anno
																	movimentiAnno = getMovimentiAnnoRedditoMobilita(
																			dataReddito, prgMovimentoInMob,
																			tipoRapporto, prgPeriodoLav);
																	limiteReddito = new LimiteRedditoExt(dataReddito);
																	limiteAttuale = UtilsMobilita.getLimiteAttuale(
																			movInMobilita, this.getCm().getSource(),
																			dataInizioMovRedditoAnno, movimentiAmm,
																			limiteReddito, limiteAttuale);
																	// decadenzaPerReddito = true se e solo se i
																	// movimenti che si devono considerare a seconda del
																	// tipoRapporto fanno superare il limite reddito
																	decadenzaPerReddito = UtilsMobilita
																			.controllaDecadenzaReddito(movimentiAnno,
																					jAnno, dataInizioMobilita,
																					dataFineMobEffettiva, dataReddito,
																					reddito, limiteAttuale);
																	if (tipoRapporto.equalsIgnoreCase(
																			MovimentoBean.RAPPORTOSUBORDINATO)) {
																		superamentoRedditoInMobilita = UtilsMobilita
																				.controllaDecadenzaReddito(
																						movimentiAnno, jAnno,
																						dataInizioMobilita,
																						dataFineMobEffettiva,
																						dataReddito, redditoMobilita,
																						limiteAttuale);
																	} else {
																		movimentiAnnoMobilita = getMovimentiAnnoRedditoMobilita(
																				dataReddito, prgMovimentoInMob,
																				MovimentoBean.RAPPORTOSUBORDINATO,
																				prgPeriodoLav);
																		// superamentoRedditoInMobilita = true se e solo
																		// se tutti movimenti fanno superare il limite
																		// reddito
																		superamentoRedditoInMobilita = UtilsMobilita
																				.controllaDecadenzaReddito(
																						movimentiAnnoMobilita, jAnno,
																						dataInizioMobilita,
																						dataFineMobEffettiva,
																						dataReddito, redditoMobilita,
																						limiteAttuale);
																	}
																	// se decadenzaPerReddito = true allora a maggior
																	// ragione superamentoRedditoInMobilita = true
																	if (!decadenzaPerReddito) {
																		if (codTipoMov.equalsIgnoreCase(
																				MovimentoBean.COD_ASSUNZIONE)) {
																			if (movInMobilita.getAttribute(
																					"MOVIMENTI_PROROGATI") != null) {
																				Vector prec = (Vector) movInMobilita
																						.getAttribute(
																								"MOVIMENTI_PROROGATI");
																				SourceBean movimentoAvv = null;
																				SourceBean movimentoSucc = null;
																				String dataInizioPrec = "";
																				String dataInizioSucc = "";
																				String dataFinePrec = "";
																				BigDecimal retribuzionePrec = null;
																				int precSize = prec.size();
																				for (int kPrec = 0; kPrec < precSize; kPrec++) {
																					movimentoAvv = (SourceBean) prec
																							.get(kPrec);
																					BigDecimal prgMovProrogato = (BigDecimal) movimentoAvv
																							.getAttribute(
																									MovimentoBean.DB_PRG_MOVIMENTO);
																					int kSucc = kPrec + 1;
																					dataInizioPrec = movimentoAvv
																							.getAttribute(
																									MovimentoBean.DB_DATA_INIZIO)
																							.toString();
																					if (movimentoAvv.getAttribute(
																							MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null) {
																						dataFinePrec = movimentoAvv
																								.getAttribute(
																										MovimentoBean.DB_DATA_FINE_EFFETTIVA)
																								.toString();
																					} else {
																						dataFinePrec = null;
																					}
																					// Se il movimento successivo nel
																					// vettore dei movimenti prorogati
																					// ha la stessa data inizio
																					// del movimento corrente nel
																					// vettore dei prorogati, allora al
																					// fine del reddito non lo considero
																					if (kSucc < precSize) {
																						movimentoSucc = (SourceBean) prec
																								.get(kSucc);
																						dataInizioSucc = movimentoSucc
																								.getAttribute(
																										MovimentoBean.DB_DATA_INIZIO)
																								.toString();
																						if (DateUtils.compare(
																								dataInizioPrec,
																								dataInizioSucc) == 0) {
																							continue;
																						}
																					}
																					// prendo la retribuzione
																					if (DateUtils.getAnno(
																							dataInizioPrec) < jAnno) {
																						dataInizioPrec = "01/01/"
																								+ jAnno;
																					}
																					if (dataFinePrec == null
																							|| dataFinePrec.equals("")
																							|| (DateUtils.getAnno(
																									dataFinePrec) > jAnno)) {
																						dataFinePrec = "31/12/" + jAnno;
																					}
																					retribuzionePrec = Retribuzione
																							.getRetribuzioneMen(
																									movimentoAvv);
																					annoInizioMov = DateUtils
																							.getAnno(dataInizioPrec);
																					if (annoInizioMov <= jAnno
																							&& (dataFinePrec == null
																									|| dataFinePrec
																											.equals("")
																									|| DateUtils
																											.getAnno(
																													dataFinePrec) >= jAnno)) {
																						if (retribuzionePrec != null) {
																							ggDiLavoro = ControlliExt
																									.getNumeroGiorniDiLavoro(
																											dataInizioPrec,
																											dataFinePrec,
																											dataReddito);
																							ggDiLavoroAnnoSuccessivo = ControlliExt
																									.getNumeroGiorniDiLavoroAnnoSuccessivo(
																											dataFinePrec,
																											dataReddito);
																							reddito.aggiorna(ggDiLavoro,
																									ggDiLavoroAnnoSuccessivo,
																									retribuzionePrec
																											.doubleValue());
																							redditoMobilita.aggiorna(
																									ggDiLavoro,
																									ggDiLavoroAnnoSuccessivo,
																									retribuzionePrec
																											.doubleValue());
																							if (redditoMobilita
																									.getRedditoAnno() > limiteAttuale) {
																								superamentoRedditoInMobilita = true;
																							}
																							if (reddito
																									.getRedditoAnno() > limiteAttuale) {
																								decadenzaPerReddito = true;
																								break;
																							}
																						} else {
																							decadenzaPerReddito = true;
																							superamentoRedditoInMobilita = true;
																							break;
																						}
																					}
																				}
																			} else {
																				ggDiLavoro = ControlliExt
																						.getNumeroGiorniDiLavoro(
																								dataInizioMovRedditoAnno,
																								dataFineMovRedditoAnno,
																								dataReddito);
																				retribuzione = Retribuzione
																						.getRetribuzioneMen(
																								movInMobilita);
																				if (retribuzione != null) {
																					reddito.aggiorna(ggDiLavoro,
																							ggDiLavoroAnnoSuccessivo,
																							retribuzione.doubleValue());
																					redditoMobilita.aggiorna(ggDiLavoro,
																							ggDiLavoroAnnoSuccessivo,
																							retribuzione.doubleValue());
																					if (redditoMobilita
																							.getRedditoAnno() > limiteAttuale) {
																						superamentoRedditoInMobilita = true;
																					}
																					if (reddito
																							.getRedditoAnno() > limiteAttuale) {
																						decadenzaPerReddito = true;
																					}
																				} else {
																					decadenzaPerReddito = true;
																					superamentoRedditoInMobilita = true;
																				}
																			}
																		} else {
																			// sono nel caso che la proroga o
																			// trasformazione può essere orfana
																			ggDiLavoro = ControlliExt
																					.getNumeroGiorniDiLavoro(
																							dataInizioMovRedditoAnno,
																							dataFineMovRedditoAnno,
																							dataReddito);
																			retribuzione = Retribuzione
																					.getRetribuzioneMen(movInMobilita);
																			if (retribuzione != null) {
																				reddito.aggiorna(ggDiLavoro,
																						ggDiLavoroAnnoSuccessivo,
																						retribuzione.doubleValue());
																				redditoMobilita.aggiorna(ggDiLavoro,
																						ggDiLavoroAnnoSuccessivo,
																						retribuzione.doubleValue());
																				if (redditoMobilita
																						.getRedditoAnno() > limiteAttuale) {
																					superamentoRedditoInMobilita = true;
																				}
																				if (reddito
																						.getRedditoAnno() > limiteAttuale) {
																					decadenzaPerReddito = true;
																				}
																			} else {
																				decadenzaPerReddito = true;
																				superamentoRedditoInMobilita = true;
																			}
																		}
																	}

																	// Se c'è stato superamento del reddito e si è in
																	// presenza di un rapporto che non consente
																	// la sospensione, mobilità decade e il lavoratore
																	// deve decadere dallo stato di disoccupazione
																	if (decadenzaPerReddito) {
																		if (flgSospensione
																				.equalsIgnoreCase(MovimentoBean.SI)) {
																			// se supera il reddito anno iniziale allora
																			// deve fare sospensione, altrimenti lo
																			// stato
																			// di sospensione viene creato dal ricalcolo
																			// che viene fatto all'inizio del nuovo anno
																			if (jAnno == annoRedditoIniziale) {
																				if (!movInMobilita.containsAttribute(
																						Contratto.FIELD_FLG_SOSPENSIONE)) {
																					movInMobilita.setAttribute(
																							Contratto.FIELD_FLG_SOSPENSIONE,
																							"S");
																				}
																			}
																		} else {
																			// cancello la mobilità e la chiusura
																			// mobilità corrente se il movimento
																			// inizia prima del periodo di mobilità.
																			if (DateUtils.compare(dataInizioMovReddito,
																					dataInizioMobilitaOrig) <= 0) {
																				sbMobilita = (SourceBean) movimenti
																						.get(i);
																				prgMobilitaIscr = (BigDecimal) sbMobilita
																						.getAttribute(
																								MobilitaBean.DB_PRG_MOBILITA_ISCR);
																				// nel vettore dei movimenti sia per
																				// l'evento MobilitaBean che
																				// ChiusuraMobilitaBean setto
																				// l'attributo MOBILITA_NON_IMPATTANTE
																				// (non è necessario aggiornare le date
																				// in quanto gli eventi nel ricalcolo
																				// non vengono considerati).
																				UtilsMobilita
																						.rimuoviMobilitaDaMovimenti(
																								prgMobilitaIscr,
																								this.movimenti);
																				// nella lista listaMobilita l'evento
																				// MobilitaBean viene eliminato
																				// fisicamente
																				UtilsMobilita
																						.rimuoviMobDallaListaMobilita(
																								prgMobilitaIscr,
																								this.getListaMobilita());
																			}
																			// presenza di rapporto parasubordinato o
																			// autonomo con superamento del reddito,
																			// quindi la mobilità decade
																			String motivoDecadenza = MobilitaBean.motivoDecadenzaReddito;
																			if (gestione150 && !iscrittoCM) {
																				motivoDecadenza = MobilitaBean.motivoDecadenza;
																			}
																			BigDecimal numKloMobNew = UtilsMobilita
																					.settaDecadenzaMobilitaPerReddito(
																							sbMobilita,
																							chiusuraMobilita,
																							dataInizioMovRedditoAnno,
																							userID,
																							this.getListaMobilita(),
																							motivoDecadenza,
																							this.txExecutor);
																			aggiornaNumKloMobInMovimenti(
																					new MobilitaBean(sbMobilita),
																					numKloMobNew, movimenti);
																			generaUscitaMobilita = true;
																			if (jAnno == annoRedditoIniziale) {
																				generaUscitaMobilitaPerReddito = true;
																			}
																			if (getPrgMobilitaRicalcolo() == null) {
																				setPrgMobilitaRicalcolo(prgMobilita);
																			}

																			// se decade anno iniziale, altrimenti lo
																			// stato occupazionale Occupato
																			// viene creato dal ricalcolo che viene
																			// fatto all'inizio del nuovo anno
																			if (jAnno == annoRedditoIniziale) {
																				if (!movInMobilita.containsAttribute(
																						"FLGCONTRATTODECADENZA")) {
																					movInMobilita.setAttribute(
																							"FLGCONTRATTODECADENZA",
																							"S");
																				}
																			}
																			cancellaDecadenzaReddito = false;
																			break;
																		}
																	} else {
																		if (tipoRapporto.equalsIgnoreCase(
																				MovimentoBean.RAPPORTOAUTONOMO)
																				|| tipoRapporto.equalsIgnoreCase(
																						MovimentoBean.RAPPORTOPARASUBORDINATO)) {
																			if (superamentoRedditoInMobilita) {
																				// se supera il reddito anno iniziale
																				// allora deve fare sospensione,
																				// altrimenti lo stato
																				// di sospensione viene creato dal
																				// ricalcolo che viene fatto all'inizio
																				// del nuovo anno
																				if (jAnno == annoRedditoIniziale) {
																					if (!movInMobilita
																							.containsAttribute(
																									Contratto.FIELD_FLG_SOSPENSIONE)) {
																						movInMobilita.setAttribute(
																								Contratto.FIELD_FLG_SOSPENSIONE,
																								"S");
																					}
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												} // end if decadenza per reddito solo per quelli a cavallo della legge
													// Fornero
											} // enf if non considero i Tirocini, lavoro occasionale, progetto, cococo
												// nella determinazione del reddito
										} // end if devo considerare il reddito per la decadenza della mobilità
									} // end if movimento <> CES e cade nel periodo di mobilità
								} // enf if (objApp instanceof MovimentoBean)
							} // for (j = 0; j < movimentiSize; j++)
						} // end if (dataChiusuraMobilita != null && !dataChiusuraMobilita.equals(""))

						// Se la mobilità prima dell'evento ha un motivo "decaduto per avv. a TI" ("G")
						// e dopo l'evento non decade più, allora viene eliminato il motivo di decadenza
						if (Sottosistema.MO.isOn() && ((codMotivoFine.equalsIgnoreCase(MobilitaBean.motivoDecadenzaTI)
								&& cancellaDecadenzaTI)
								|| ((codMotivoFine.equalsIgnoreCase(MobilitaBean.motivoDecadenzaReddito)
										|| codMotivoFine.equalsIgnoreCase(MobilitaBean.motivoDecadenza))
										&& cancellaDecadenzaReddito))) {
							numkloMob = new BigDecimal(mobilita.getAttribute("NUMKLOMOBISCR").toString());
							numkloMob = DBStore.cancellaDecadenza(prgMobilita, userID, numkloMob, this.txExecutor);
							// --------------------------- Aggiorno il numklo dell'evento mobilità e evento chiusura
							// mobilità nella lista degli eventi Movimenti
							mobilita.updAttribute("NUMKLOMOBISCR", numkloMob);
							aggiornaNumKloMobInMovimenti(mobilita, numkloMob, movimenti);
						}
					} else { // if (decadenzaMotivoFine)
								// Siamo nel caso in cui la mobilità ha un motivo fine che non permette la decadenza
								// Per i movimenti nel periodo di mobilità e successivi al decreto Fornero devo
								// controllare il reddito per
								// verificare se vi è sospensione. Per i movimenti precedenti il sistema determina lo
								// stato occupazionale come B2 (In Mobilità Occupato)
						if (dataChiusuraMobilita != null && !dataChiusuraMobilita.equals("")) {
							boolean superamentoLimiteReddito = false;
							String dataFineMobEffettiva = DateUtils.giornoPrecedente(dataChiusuraMobilita);
							int annoFineMob = DateUtils.getAnno(dataFineMobEffettiva);
							for (j = 0; j < movimentiSize; j++) {
								objApp = movimenti.get(j);
								if (objApp instanceof MovimentoBean) {
									movInMobilita = (MovimentoBean) objApp;
									// Se trovo movimenti in data futura termino il controllo sul reddito
									if (DateUtils.compare(movInMobilita.getDataInizio(), DateUtils.getNow()) > 0)
										break;
									String codTipoAvviamento = movInMobilita
											.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
													? movInMobilita.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
															.toString()
													: "";
									// Non considero TIPO AVVIAMENTO Z.09.02 (codice vecchio RS3) (cessazione attività
									// lavorativa dopo un periodo di sospeso per contrazione)
									if (codTipoAvviamento.equals("Z.09.02"))
										continue;

									String dataInizioMovReddito = movInMobilita.getDataInizio();
									String dataFineMovReddito = movInMobilita.getDataFineEffettiva();
									BigDecimal prgMovimentoInMob = movInMobilita.getPrgMovimento();
									BigDecimal prgPeriodoLav = (BigDecimal) movInMobilita
											.getAttribute("PRGPERIODOINTERMITTENTE");
									String codMonoTipoAss = movInMobilita
											.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
													? movInMobilita.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
															.toString()
													: "";
									boolean movDaTrattare = false;
									// si escludono i tirocini
									if (!movInMobilita.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString()
											.equalsIgnoreCase("CES")
											&& DateUtils.compare(dataInizioMovReddito, dataFineMobEffettiva) <= 0
											&& !codMonoTipoAss.equalsIgnoreCase("T")) {

										String codTipoMov = movInMobilita.getAttribute(MovimentoBean.DB_COD_MOVIMENTO)
												.toString();
										if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_ASSUNZIONE)) {
											movDaTrattare = true;
											if (movInMobilita.getAttribute("MOVIMENTI_PROROGATI") != null) {
												Vector prec = (Vector) movInMobilita
														.getAttribute("MOVIMENTI_PROROGATI");
												int precSize = prec.size();
												if (precSize > 0) {
													int movUltimoCatena = precSize - 1;
													SourceBean movimentoUltimo = (SourceBean) prec.get(movUltimoCatena);
													dataFineMovReddito = movimentoUltimo
															.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
																	? movimentoUltimo.getAttribute(
																			MovimentoBean.DB_DATA_FINE_EFFETTIVA)
																			.toString()
																	: "";
													for (int kPrec = 0; kPrec < precSize; kPrec++) {
														SourceBean movimentoCatena = (SourceBean) prec.get(kPrec);
														BigDecimal prgMovProrogato = (BigDecimal) movimentoCatena
																.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
														mapProrogati.put(prgMovProrogato, "S");
													}
												}
											}
										} else {
											if (!mapProrogati.containsKey(prgMovimentoInMob)) {
												movDaTrattare = true;
											}
										}

										if (movDaTrattare) {
											if ((DateUtils.compare(dataInizioMovReddito,
													MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0)
													|| (dataFineMovReddito == null || dataFineMovReddito.equals("")
															|| DateUtils.compare(dataFineMovReddito,
																	MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0)) {

												// controllo se il movimento si trova nel periodo di mobilità
												if (dataFineMovReddito == null || dataFineMovReddito.equals("")
														|| DateUtils.compare(dataFineMovReddito,
																dataInizioMobilita) >= 0) {
													if ((dataFineMovReddito == null) || (dataFineMovReddito.equals(""))
															|| (annoFineMob < DateUtils.getAnno(dataFineMovReddito))) {
														dataFineMovReddito = "31/12/" + annoFineMob;
													}
													annoRedditoFinale = DateUtils.getAnno(dataFineMovReddito);
													int annoInizioMov = DateUtils.getAnno(dataInizioMovReddito);
													if (annoInizioMov < annoInizialeMob) {
														annoRedditoIniziale = annoInizialeMob;
													} else {
														annoRedditoIniziale = annoInizioMov;
													}
													// reddito calcolato anno per anno, tra l'anno iniziale e l'anno di
													// fine del movimento
													for (int jAnno = annoRedditoIniziale; jAnno <= annoRedditoFinale
															&& jAnno <= annoCorrente; jAnno++) {
														if (jAnno >= annoFornero) {
															String dataFineMovRedditoAnno = "";
															String dataInizioMovRedditoAnno = "";
															if (DateUtils.getAnno(dataInizioMovReddito) == jAnno) {
																dataInizioMovRedditoAnno = dataInizioMovReddito;
															} else {
																dataInizioMovRedditoAnno = "01/01/" + jAnno;
															}
															dataReddito = dataInizioMovRedditoAnno;
															if (DateUtils.getAnno(dataFineMovReddito) > jAnno) {
																dataFineMovRedditoAnno = "31/12/" + jAnno;
															} else {
																dataFineMovRedditoAnno = dataFineMovReddito;
															}

															if (jAnno == annoInizialeMob) {
																SourceBean didAperta = cercaDid(dataInizioMobilita);
																if (didAperta != null) {
																	String dataDichiarazione = (String) didAperta
																			.getAttribute("datDichiarazione");
																	if (dataDichiarazione != null
																			&& !dataDichiarazione.equals("")) {
																		if (DateUtils.getAnno(
																				dataDichiarazione) < annoInizialeMob) {
																			dataInizioMobilita = "01/01/"
																					+ annoInizialeMob;
																		} else {
																			dataInizioMobilita = dataDichiarazione;
																		}
																	}
																}
															}

															reddito = new Reddito(0, 0);
															limiteAttuale = 0;
															// estrae i movimenti dell'anno
															movimentiAnno = getMovimentiAnnoRedditoMobilita(dataReddito,
																	prgMovimentoInMob,
																	MovimentoBean.RAPPORTOSUBORDINATO, prgPeriodoLav);
															limiteReddito = new LimiteRedditoExt(dataReddito);
															limiteAttuale = UtilsMobilita.getLimiteAttuale(
																	movInMobilita, this.getCm().getSource(),
																	dataInizioMovRedditoAnno, movimentiAmm,
																	limiteReddito, limiteAttuale);
															superamentoLimiteReddito = UtilsMobilita
																	.controllaDecadenzaReddito(movimentiAnno, jAnno,
																			dataInizioMobilita, dataFineMobEffettiva,
																			dataReddito, reddito, limiteAttuale);

															if (!superamentoLimiteReddito) {
																if (codTipoMov.equalsIgnoreCase(
																		MovimentoBean.COD_ASSUNZIONE)) {
																	if (movInMobilita.getAttribute(
																			"MOVIMENTI_PROROGATI") != null) {
																		Vector prec = (Vector) movInMobilita
																				.getAttribute("MOVIMENTI_PROROGATI");
																		SourceBean movimentoAvv = null;
																		SourceBean movimentoSucc = null;
																		String dataInizioPrec = "";
																		String dataInizioSucc = "";
																		String dataFinePrec = "";
																		BigDecimal retribuzionePrec = null;
																		int precSize = prec.size();
																		for (int kPrec = 0; kPrec < precSize; kPrec++) {
																			movimentoAvv = (SourceBean) prec.get(kPrec);
																			BigDecimal prgMovProrogato = (BigDecimal) movimentoAvv
																					.getAttribute(
																							MovimentoBean.DB_PRG_MOVIMENTO);
																			int kSucc = kPrec + 1;
																			dataInizioPrec = movimentoAvv.getAttribute(
																					MovimentoBean.DB_DATA_INIZIO)
																					.toString();
																			if (movimentoAvv.getAttribute(
																					MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null) {
																				dataFinePrec = movimentoAvv
																						.getAttribute(
																								MovimentoBean.DB_DATA_FINE_EFFETTIVA)
																						.toString();
																			} else {
																				dataFinePrec = null;
																			}
																			// Se il movimento successivo nel vettore
																			// dei movimenti prorogati ha la stessa data
																			// inizio
																			// del movimento corrente nel vettore dei
																			// prorogati, allora al fine del reddito non
																			// lo considero
																			if (kSucc < precSize) {
																				movimentoSucc = (SourceBean) prec
																						.get(kSucc);
																				dataInizioSucc = movimentoSucc
																						.getAttribute(
																								MovimentoBean.DB_DATA_INIZIO)
																						.toString();
																				if (DateUtils.compare(dataInizioPrec,
																						dataInizioSucc) == 0) {
																					continue;
																				}
																			}
																			// prendo la retribuzione
																			if (DateUtils
																					.getAnno(dataInizioPrec) < jAnno) {
																				dataInizioPrec = "01/01/" + jAnno;
																			}
																			if (dataFinePrec == null
																					|| dataFinePrec.equals("")
																					|| (DateUtils.getAnno(
																							dataFinePrec) > jAnno)) {
																				dataFinePrec = "31/12/" + jAnno;
																			}
																			retribuzionePrec = Retribuzione
																					.getRetribuzioneMen(movimentoAvv);
																			annoInizioMov = DateUtils
																					.getAnno(dataInizioPrec);
																			if (annoInizioMov <= jAnno
																					&& (dataFinePrec == null
																							|| dataFinePrec.equals("")
																							|| DateUtils.getAnno(
																									dataFinePrec) >= jAnno)) {
																				if (retribuzionePrec != null) {
																					ggDiLavoro = ControlliExt
																							.getNumeroGiorniDiLavoro(
																									dataInizioPrec,
																									dataFinePrec,
																									dataReddito);
																					ggDiLavoroAnnoSuccessivo = ControlliExt
																							.getNumeroGiorniDiLavoroAnnoSuccessivo(
																									dataFinePrec,
																									dataReddito);
																					reddito.aggiorna(ggDiLavoro,
																							ggDiLavoroAnnoSuccessivo,
																							retribuzionePrec
																									.doubleValue());
																					if (reddito
																							.getRedditoAnno() > limiteAttuale) {
																						superamentoLimiteReddito = true;
																						break;
																					}
																				} else {
																					superamentoLimiteReddito = true;
																					break;
																				}
																			}
																		}
																	} else {
																		ggDiLavoro = ControlliExt
																				.getNumeroGiorniDiLavoro(
																						dataInizioMovRedditoAnno,
																						dataFineMovRedditoAnno,
																						dataReddito);
																		retribuzione = Retribuzione
																				.getRetribuzioneMen(movInMobilita);
																		if (retribuzione != null) {
																			reddito.aggiorna(ggDiLavoro,
																					ggDiLavoroAnnoSuccessivo,
																					retribuzione.doubleValue());
																			if (reddito
																					.getRedditoAnno() > limiteAttuale) {
																				superamentoLimiteReddito = true;
																			}
																		} else {
																			superamentoLimiteReddito = true;
																		}
																	}
																} else {
																	// sono nel caso che la proroga o trasformazione può
																	// essere orfana
																	ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(
																			dataInizioMovRedditoAnno,
																			dataFineMovRedditoAnno, dataReddito);
																	retribuzione = Retribuzione
																			.getRetribuzioneMen(movInMobilita);
																	if (retribuzione != null) {
																		reddito.aggiorna(ggDiLavoro,
																				ggDiLavoroAnnoSuccessivo,
																				retribuzione.doubleValue());
																		if (reddito.getRedditoAnno() > limiteAttuale) {
																			superamentoLimiteReddito = true;
																		}
																	} else {
																		superamentoLimiteReddito = true;
																	}
																}
															}

															// Se c'è stato superamento del reddito allora si è in
															// presenza di una sospensione, in quanto
															// siamo nel caso che la mobilità ha un motivo fine che non
															// permette la decadenza
															if (superamentoLimiteReddito) {
																// se supera il reddito anno iniziale allora deve fare
																// sospensione, altrimenti lo stato
																// di sospensione viene creato dal ricalcolo che viene
																// fatto all'inizio del nuovo anno
																if (jAnno == annoRedditoIniziale) {
																	if (!movInMobilita.containsAttribute(
																			Contratto.FIELD_FLG_SOSPENSIONE)) {
																		movInMobilita.setAttribute(
																				Contratto.FIELD_FLG_SOSPENSIONE, "S");
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}

					} // else if (decadenzaMotivoFine)
				} // if (obj instanceof MobilitaBean)
			} // for (; i < movimenti.size(); i++)

			if (generaUscitaMobilita) {
				throw new MobilitaException(MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA);
			}
		} catch (MobilitaException me) {
			String valore = "";
			boolean viewMsgMobilita = true;
			if (!uscitaMobilita) {
				if (getDataEventoAmministrativo() != null && !getDataEventoAmministrativo().equals("")
						&& dataInizioMobilitaOrig != null && !dataInizioMobilitaOrig.equals("")
						&& datFineMobDopoScorr != null && !datFineMobDopoScorr.equals("")) {
					if (!(DateUtils.compare(getDataEventoAmministrativo(), dataInizioMobilitaOrig) >= 0
							&& DateUtils.compare(getDataEventoAmministrativo(), datFineMobDopoScorr) <= 0)) {
						viewMsgMobilita = false;
					}
				}
			}
			// FORZA_CHIUSURA_MOBILITA forzatura in ricostruzione storia sulla mobilità quando si protocolla la did
			if (viewMsgMobilita && !forzaRicostruzione
					&& !requestContainer.getServiceRequest().containsAttribute("FORZA_CHIUSURA_MOBILITA")
					&& getDataEventoAmministrativo() != null && !getDataEventoAmministrativo().equals("")
					&& dataInizioMobilitaOrig != null && !dataInizioMobilitaOrig.equals("")
					&& datFineMobDopoScorr != null && !datFineMobDopoScorr.equals("")
					&& ((DateUtils.compare(getDataEventoAmministrativo(), dataInizioMobilitaOrig) < 0)
							|| (DateUtils.compare(getDataEventoAmministrativo(), dataInizioMobilitaOrig) >= 0
									&& DateUtils.compare(getDataEventoAmministrativo(), datFineMobDopoScorr) <= 0))) {
				if (continuaRicalcolo)
					valore = "true";
				else
					valore = "false";
				requestContainer.getServiceRequest().setAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE,
						"");
				requestContainer.getServiceRequest().setAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE", valore);
				this.setForzaRicostruzione(true);
				throw me;
			} else {
				if (viewMsgMobilita && !forzaRicostruzione
						&& !requestContainer.getServiceRequest().containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
					if (continuaRicalcolo)
						valore = "true";
					else
						valore = "false";
					requestContainer.getServiceRequest()
							.setAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE, "");
					requestContainer.getServiceRequest().setAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE", valore);
					this.setForzaRicostruzione(true);
				}
				if (logFileBatch != null) {
					String msg = "";
					switch (me.getCode()) {
					case MessageCodes.Mobilita.MOVIMENTO_NON_COMPATIBILE_CON_MOBILITA:
						msg = "Trovato movimento che ha comportato l'uscita dalla mobilità.";
						break;
					case MessageCodes.Mobilita.USCITA_MOBILITA:
						msg = "Trovato movimento non compatibile con l'iscrizione alla mobilità.";
						break;
					default: // gestione errore generico
						msg = "Trovata situazione anomala rispetto alla mobilità";
					}
					logFileBatch.writeLog(msg);
				}
			}
		}
	}

	/**
	 * @return
	 */
	public SourceBean getStatoOccupazionaleApertoOld() {
		return statoOccupazionaleApertoOld;
	}

	/**
	 * Metodo per recuperare lo stato occupazionale di un dato movimento
	 * 
	 * @param mov
	 * @return sobean
	 */
	public StatoOccupazionaleBean getStatoOccupazionale(SourceBean mov) throws Exception {
		StatoOccupazionaleBean soBean = null;
		if (!mov.containsAttribute("prgMovimento"))
			mov.setAttribute("prgMovimento", new BigDecimal(-1));
		BigDecimal prgMovInEsame = new BigDecimal(mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString());
		int movimentiSize = this.movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			Object o = movimenti.get(i);
			if (o instanceof MovimentoBean) {
				MovimentoBean mb = ((MovimentoBean) o);
				if ((mb.getPrgMovimento() != null) && mb.getPrgMovimento().equals(prgMovInEsame)) {
					SourceBean s = DBLoad.getStatoOccupazionaleSpecifico(mb.getPrgStatoOccupaz(), txExecutor);
					if (s != null)
						soBean = new StatoOccupazionaleBean(s);
					break;
				}
			}
		}
		return soBean;
	}

	public boolean riapriPattoAssocDid(DidBean did) throws Exception {
		boolean pattoRiaperto = false;
		SourceBean pattoApp = cercaPatto(did.getPrgDichDisponibilita(), did.getCdnLavoratore());
		SourceBean sb = null;
		if (pattoApp != null) {
			// Ho trovato un patto stipulato a partire da quella did
			PattoBean patto = new PattoBean(pattoApp);
			String codMotivoFinePatto = patto.getCodMotivoFineAtto();
			if (patto.getAttribute("PRGDICHDISPONIBILITA") != null && did.getPrgDichDisponibilita() != null
					&& patto.getPrgDichDisponibilitaBigDec().equals(did.getPrgDichDisponibilita())
					&& patto.isProtocollato() && codMotivoFinePatto.equals("AV")) {
				DBStore.riapriPatto(patto.getPrgPattoLavBigDec(), null, patto.getNumklo().add(new BigDecimal("1")),
						RequestContainer.getRequestContainer(), txExecutor);
				// Aggiorna il numKlo, cancello datFine e cancello codMotivoFinePatto
				// nel vettore dei patti della situazione amministrativa
				aggiornaPattoRiaperto(patto.getPrgPattoLavBigDec(), patto.getNumklo().add(new BigDecimal("1")));
				pattoRiaperto = true;
			}
		}
		return pattoRiaperto;
	}

	/**
	 * cerca un patto a partire da una mobilita' oppure da uno stato occupazionale D o I inserito manualmente senza DID
	 * e mobilita'
	 * 
	 * @param dataRif
	 * @param cdnLavoratore
	 * @throws Exception
	 */
	public void riapriPattoMobilita(String dataRif, String cdnLavoratore) throws Exception {
		SourceBean pattoApp = cercaPattoMob(dataRif);
		SourceBean sb = null;
		if (pattoApp != null) {
			PattoBean patto = new PattoBean(pattoApp);
			String codMotivoFinePatto = patto.getCodMotivoFineAtto();
			if (patto.isProtocollato() && codMotivoFinePatto.equals("AV")) {
				DBStore.riapriPatto(patto.getPrgPattoLavBigDec(), null, patto.getNumklo().add(new BigDecimal("1")),
						RequestContainer.getRequestContainer(), txExecutor);
				// Aggiorna il numKlo, cancello datFine e cancello codMotivoFinePatto
				// nel vettore dei patti della situazione amministrativa
				aggiornaPattoRiaperto(patto.getPrgPattoLavBigDec(), patto.getNumklo().add(new BigDecimal("1")));
			}
		}
	}

	public SourceBean dammiMovimento(List movimenti, BigDecimal prg) {
		SourceBean sb = null;
		BigDecimal prgCorr = null;
		SourceBean sbCorr = null;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			sbCorr = (SourceBean) movimenti.get(i);
			if (sbCorr.containsAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
					&& sbCorr.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).equals(prg)) {
				sb = sbCorr;
				break;
			}
		}
		return sb;
	}

	public DidBean cercaUltimaDidStoricizzata(List dids, String dataRif) throws Exception {
		DidBean did = null;
		String dataDichiarazione = null;
		String dataFine = null;
		String codMotivoChiusura = null;
		int ind = -1;
		for (int i = 0; i < dids.size(); i++) {
			did = (DidBean) dids.get(i);
			dataDichiarazione = did.getDataInizio();
			dataFine = (String) did.getAttribute("datFine");
			codMotivoChiusura = did.containsAttribute("codmotivofineatto")
					? did.getAttribute("codmotivofineatto").toString()
					: "";
			if (did.getSource().containsAttribute("flag_changed"))
				dataFine = (String) did.getSource().getAttribute("datFineChanged");
			if (dataFine != null && !dataFine.equals("") && codMotivoChiusura.equals("AV")
					&& DateUtils.compare(dataDichiarazione, dataRif) <= 0)
				ind = i;
			else {
				if ((DateUtils.compare(dataDichiarazione, dataRif) <= 0) && (dataFine == null || dataFine.equals(""))) {
					ind = -1;
				}
				break;
			}
		}
		if (ind < 0)
			did = null;
		else
			did = (DidBean) dids.get(ind);
		return did;
	}

	/**
	 * INVOCATA NELLA NUOVA GESTIONE DEGLI IMPATTI (SITUAZIONEAMMINISTRATIVAFACTORY E IN ANNULLA E RETTIFICA MOVIMENTO)
	 * 
	 * @param statoOccIniziale
	 * @param indice
	 * @return
	 * @throws Exception
	 */
	public StatoOccupazionaleBean ricrea(StatoOccupazionaleBean statoOccIniziale, int indice) throws Exception {
		StatoOccupazionaleBean statoOccFinale = null;
		statoOccFinale = ricostruzioneStoria(indice, statoOccIniziale, null);
		return (statoOccFinale);
	}

	private StatoOccupazionaleBean gestisciMobilitaInRicostruzione(EventoAmministrativo evento,
			StatoOccupazionaleBean statoOccCorrente, int i, Vector vettParametriImpatti,
			StatoOccupazionaleManager2 statoOccManager) throws Exception {
		StatoOccupazionaleBean nuovoStatoOcc = null;
		MobilitaBean mob = (MobilitaBean) evento;
		if (mob.containsAttribute("MOBILITA_NON_IMPATTANTE")) {
			String dataMob = evento.getDataInizio();
			SourceBean pattoAperto = cercaPatto(dataMob);
			if (pattoAperto != null) {
				if (!pattoAperto.containsAttribute("codMotivoFineAtto")
						|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("")
						|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
					DBStore.chiudiPatto297(pattoAperto, dataMob, "AV", requestContainer, txExecutor);
					aggiornaNumKloPatto(new BigDecimal(pattoAperto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
							dataMob, new BigDecimal(pattoAperto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
				}
			}
			nuovoStatoOcc = statoOccCorrente;
		} else {
			Vector configurazioniDefaul_Custom = new Vector();
			configurazioniDefaul_Custom.add(0, getTipoCongif_MOV_C03());
			String dataInizioMob = mob.getDataInizio();
			String codMonoAttiva = mob.getAttribute("CODMONOATTIVA").toString();
			// se la mobilità è precedente alla data 30/01/2003 oppure alla data presente in configurazione, allora
			// l'evento
			// amministrativo non ha impatti nella ricostruzione (viene aggiunto warning)
			if (DateUtils.compare(dataInizioMob, dataPrec297) < 0) {
				Vector paramV = new Vector(1);
				paramV.add(dataPrec297);
				addWarning(MessageCodes.Mobilita.MOBILITA_PREC_297, MessageCodes.Mobilita.MOBILITA_PREC_DATA_297,
						paramV);
				nuovoStatoOcc = statoOccCorrente;
			} else {
				// controllo sulla data futura
				if (DateUtils.compare(DateUtils.getNow(), dataInizioMob) >= 0) {
					String dataFineMob = mob.getDataFine();
					if (codMonoAttiva.equalsIgnoreCase("A")) {
						Vector movimentiAperti = null;
						movimentiAperti = getMovimentiAperti(dataInizioMob);
						MobilitaManager mobilitaManager = new MobilitaManager(this);
						StatoOccupazionaleBean statoOcc = mobilitaManager.gestisciMobilita(mob,
								this.getCm().getSource(), statoOccCorrente, movimentiAperti, requestContainer,
								vettParametriImpatti, configurazioniDefaul_Custom, getDataPrec297(), getData150(),
								txExecutor);
						nuovoStatoOcc = new StatoOccupazionaleBean(statoOcc, statoOccCorrente);
						nuovoStatoOcc.setChanged(statoOcc.ischanged());
					} else {
						// evento Mobilità in questo caso equivale alla gestione della did
						Vector rows = estraiMovimentiAnno(dataInizioMob);
						StatoOccupazionaleBean statoOcc = DIDManager.inserisci(statoOccCorrente, mob, requestContainer,
								null, rows, movimenti, "MOBILITA", this.getListaMobilita(), vettParametriImpatti,
								statoOccManager, getTipoCongif_MOV_C03(), getData150(), getListaDisabiliCM(),
								txExecutor);
						nuovoStatoOcc = new StatoOccupazionaleBean(statoOcc, statoOccCorrente);
						nuovoStatoOcc.setChanged(statoOcc.ischanged());
					}

					mob.aggiornaStatoOccupaz(nuovoStatoOcc.getPrgStatoOccupaz(), requestContainer, txExecutor);
					// *** Aggiorno il numklo dell'eventuale ChiusuraMobilita successiva ***
					int movimentiSize = movimenti.size();
					for (int k = i; k < movimentiSize; k++) {
						EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
						if (c.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_MOBILITA) {
							ChiusuraMobilitaBean cMobilita = (ChiusuraMobilitaBean) c;
							if (cMobilita.getPrgMobilitaIscr().equals(mob.getPrgMobilitaIscr())) {
								_logger.debug(
										"SituazioneAmministrativa.ricostruzioneStoria():aggiornamento numKlo chiusura mobilita: "
												+ cMobilita);

								cMobilita.updAttribute("numKloMobIscr", mob.getAttribute("numKloMobIscr"));
								break;
							}
						}
					} // end for
					if (nuovoStatoOcc.ischanged()) {
						statoOccCorrente = nuovoStatoOcc;
						statiOccupazionaliCreati.add(nuovoStatoOcc);
					}
				} else {
					nuovoStatoOcc = statoOccCorrente;
				}
			}
		}
		return nuovoStatoOcc;
	}

	private StatoOccupazionaleBean gestisciChiusuraMobilitaInRicostruzione(EventoAmministrativo evento,
			StatoOccupazionaleBean statoOccCorrente, Object cdnLavoratore, StatoOccupazionaleManager2 statoOccManager)
			throws Exception {
		StatoOccupazionaleBean nuovoStatoOccBean = null;
		ChiusuraMobilitaBean chiusuraMobilita = (ChiusuraMobilitaBean) evento;
		if (chiusuraMobilita.containsAttribute("MOBILITA_NON_IMPATTANTE")) {
			nuovoStatoOccBean = statoOccCorrente;
		} else {
			// dataChiusuraMobilita: data in cui il lavoratore non si trova in mobilità
			String dataChiusuraMobilita = chiusuraMobilita.getDataInizio();
			boolean gestione150 = (DateUtils.compare(dataChiusuraMobilita, getData150()) >= 0);
			String codMonoAttiva = chiusuraMobilita.getAttribute("CODMONOATTIVA").toString();
			// se la chiusura mobilità è precedente alla data 30/01/2003 oppure alla data presente in configurazione,
			// allora l'evento amministrativo non ha impatti nella ricostruzione
			if (DateUtils.compare(dataChiusuraMobilita, dataPrec297) < 0) {
				nuovoStatoOccBean = statoOccCorrente;
			} else {
				// controllo sulla data futura
				if (DateUtils.compare(DateUtils.getNow(), dataChiusuraMobilita) < 0) {
					nuovoStatoOccBean = statoOccCorrente;
				} else {
					StatoOccupazionaleBean nuovoStatoOccupazionale = null;
					if (codMonoAttiva.equalsIgnoreCase("A")) {
						// quando si incontra un'evento di chiusura mobilità, lo stato
						// occupazionale a quel punto può essere: disoccupato(in questo caso
						// bisogna vedere se riaprire o meno la did), occupato(in
						// questo caso lo stato occupazionale successivo non cambia) oppure
						// mobilita:occupato. In questo ultimo caso ci sono dei movimenti
						// aperti: movimenti a t.d. o a t.i. part time. Bisogna ricalcolare
						// il nuovo stato occupazionale.
						if (statoOccCorrente.getStatoOccupazionale() == StatoOccupazionaleBean.B) {
							nuovoStatoOccBean = statoOccCorrente;
						} else {
							if (statoOccCorrente.getStatoOccupazionale() == StatoOccupazionaleBean.A213
									|| statoOccCorrente.getStatoOccupazionale() == StatoOccupazionaleBean.A212) {
								// gestione chiusura mobilita post decreto 150 se sono Precario (Disoccupato, Sospeso o
								// Occupato)
								if (statoOccCorrente.getStatoOccupazionale() == StatoOccupazionaleBean.A212
										&& gestione150) {
									String dataInizioMobilita = cercaDataInizioMobilita(this.getListaMobilita(),
											chiusuraMobilita);
									Vector movimentiImpattiMobilita = movimentiImpattantiFineMobilita(chiusuraMobilita,
											dataInizioMobilita, dataChiusuraMobilita);
									if (movimentiImpattiMobilita.size() > 0) {
										MobilitaManager mobilitaManager = new MobilitaManager();
										nuovoStatoOccupazionale = mobilitaManager.calcola(movimentiImpattiMobilita,
												chiusuraMobilita, dataInizioMobilita, statoOccCorrente, dids,
												getData150(), statoOccManager, getListaDisabiliCM());
										if (statoOccCorrente.getStatoOccupazionale() != nuovoStatoOccupazionale
												.getStatoOccupazionale()) {
											nuovoStatoOccupazionale.setCodMonoProvenienza("M");
											// provenienza da movimenti
											nuovoStatoOccupazionale.setDataInizio(dataChiusuraMobilita);
											nuovoStatoOccupazionale.setChanged(true);
											nuovoStatoOccupazionale.setDataCalcoloAnzianita(
													statoOccCorrente.getDataCalcoloAnzianita());
											nuovoStatoOccupazionale
													.setDataCalcoloMesiSosp(statoOccCorrente.getDataCalcoloMesiSosp());
											nuovoStatoOccupazionale
													.setDataAnzianita(statoOccCorrente.getDataAnzianita());
											nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoStatoOccupazionale,
													statoOccCorrente);
											DBStore.creaNuovoStatoOcc(nuovoStatoOccBean, dataChiusuraMobilita,
													requestContainer, txExecutor);
											statoOccCorrente = nuovoStatoOccBean;
											statiOccupazionaliCreati.add(nuovoStatoOccBean);
											// se il nuovo stato occupazionale all'atto della chiusura mobilità è
											// occupato
											// allora devo chiudere l'eventuale did aperta e patto collegato
											if (nuovoStatoOccBean.getStatoOccupazionale() == StatoOccupazionaleBean.B) {
												String dataChiusuraDid = dataChiusuraMobilita;
												SourceBean didAperta = cercaDid(dataChiusuraDid);
												SourceBean pattoAperto = cercaPatto(dataChiusuraDid);
												// seleziona il patto aperto
												if (didAperta != null) {
													String dataDichiarazione = (String) didAperta
															.getAttribute("datDichiarazione");
													if (dataDichiarazione != null && !dataDichiarazione.equals("")
															&& DateUtils.compare(dataDichiarazione,
																	dataChiusuraDid) > 0)
														dataChiusuraDid = dataDichiarazione;
													if (!didAperta.containsAttribute("codMotivoFineAtto")
															|| didAperta.getAttribute("codMotivoFineAtto").toString()
																	.equals("")
															|| didAperta.getAttribute("codMotivoFineAtto").toString()
																	.equals("AV")) {
														DBStore.chiudiDID(didAperta, dataChiusuraDid, "AV",
																requestContainer, txExecutor);
														DidBean didApertaBean = new DidBean(didAperta);
														chiudiDidInVettoreDids(didApertaBean.getPrgDichDisponibilita(),
																dataChiusuraDid);
														aggiornaNumKloDichDispoInMovimenti(didApertaBean,
																new BigDecimal(didApertaBean
																		.getAttribute("numKloDichDisp").toString()),
																movimenti);
													} else {
														addWarning(MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO);
													}
												}
												if (pattoAperto != null) {
													if (!pattoAperto.containsAttribute("codMotivoFineAtto")
															|| pattoAperto.getAttribute("codMotivoFineAtto").toString()
																	.equals("")
															|| pattoAperto.getAttribute("codMotivoFineAtto").toString()
																	.equals("AV")) {
														DBStore.chiudiPatto297(pattoAperto, dataChiusuraDid, "AV",
																requestContainer, txExecutor);
														aggiornaNumKloPatto(new BigDecimal(pattoAperto
																.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
																dataChiusuraDid,
																new BigDecimal(pattoAperto
																		.getAttribute(PattoBean.NUMKLO_PATTO_LAV)
																		.toString()));
													}
												}
											}
										} else {
											nuovoStatoOccBean = statoOccCorrente;
										}
									} else {
										nuovoStatoOccupazionale = new StatoOccupazionaleBean();
										nuovoStatoOccupazionale.setCdnLavoratore((BigDecimal) cdnLavoratore);
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
										nuovoStatoOccupazionale.setCodMonoProvenienza("M");
										nuovoStatoOccupazionale.setDataInizio(dataChiusuraMobilita);
										nuovoStatoOccupazionale.setChanged(true);
										nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoStatoOccupazionale,
												statoOccCorrente);
										DBStore.creaNuovoStatoOcc(nuovoStatoOccBean, dataChiusuraMobilita,
												requestContainer, txExecutor);
										statoOccCorrente = nuovoStatoOccBean;
										statiOccupazionaliCreati.add(nuovoStatoOccBean);
									}
								} else {
									nuovoStatoOccBean = statoOccCorrente;
								}
							} else {
								if (statoOccCorrente.getStatoOccupazionale() == StatoOccupazionaleBean.A21) {
									Vector movimentiAperti = estraiMovimentiDaEventi(this.movimenti);
									Vector movimentiImpattiMobilita = Controlli.getMovimentiAperti(movimentiAperti,
											dataChiusuraMobilita);
									// se ci sono movimenti aperti che sono dei tirocini e lo stato occupazionale è A21
									// (Disoccupato)
									// allora devo creare lo stato occupazionale Disoccupato con attività senza
									// contratto
									if (movimentiImpattiMobilita.size() > 0
											&& Controlli.soloTirocini(movimentiImpattiMobilita)) {
										nuovoStatoOccupazionale = new StatoOccupazionaleBean();
										nuovoStatoOccupazionale.setCdnLavoratore((BigDecimal) cdnLavoratore);
										nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A213);
										nuovoStatoOccupazionale.setCodMonoProvenienza("M");
										nuovoStatoOccupazionale.setDataInizio(dataChiusuraMobilita);
										nuovoStatoOccupazionale.setChanged(true);
										nuovoStatoOccupazionale
												.setDataCalcoloAnzianita(statoOccCorrente.getDataCalcoloAnzianita());
										nuovoStatoOccupazionale
												.setDataCalcoloMesiSosp(statoOccCorrente.getDataCalcoloMesiSosp());
										nuovoStatoOccupazionale.setDataAnzianita(statoOccCorrente.getDataAnzianita());
										nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoStatoOccupazionale,
												statoOccCorrente);
										DBStore.creaNuovoStatoOcc(nuovoStatoOccBean, dataChiusuraMobilita,
												requestContainer, txExecutor);
										statoOccCorrente = nuovoStatoOccBean;
										statiOccupazionaliCreati.add(nuovoStatoOccBean);
									} else {
										nuovoStatoOccBean = statoOccCorrente;
									}
								} else {
									if (statoOccCorrente.getStatoOccupazionale() == StatoOccupazionaleBean.B2
											|| statoOccCorrente.getStatoOccupazionale() == StatoOccupazionaleBean.B1) {
										String dataInizioMobilita = cercaDataInizioMobilita(this.getListaMobilita(),
												chiusuraMobilita);
										// tra i movimenti aperti devo considerare a questo punto la durata e il reddito
										// per determinare il nuovo stato occupazionale. Chiamo un metodo per
										// determinare i movimenti impattanti per il calcolo del nuovo stato
										// occupazionale.
										Vector movimentiImpattiMobilita = movimentiImpattantiFineMobilita(
												chiusuraMobilita, dataInizioMobilita, dataChiusuraMobilita);
										if (movimentiImpattiMobilita.size() > 0) {
											MobilitaManager mobilitaManager = new MobilitaManager();
											nuovoStatoOccupazionale = mobilitaManager.calcola(movimentiImpattiMobilita,
													chiusuraMobilita, dataInizioMobilita, statoOccCorrente, dids,
													getData150(), statoOccManager, getListaDisabiliCM());
											if (statoOccCorrente.getStatoOccupazionale() != nuovoStatoOccupazionale
													.getStatoOccupazionale()) {
												nuovoStatoOccupazionale.setCodMonoProvenienza("M");
												// provenienza da movimenti
												nuovoStatoOccupazionale.setDataInizio(dataChiusuraMobilita);
												nuovoStatoOccupazionale.setChanged(true);
												nuovoStatoOccupazionale.setDataCalcoloAnzianita(
														statoOccCorrente.getDataCalcoloAnzianita());
												nuovoStatoOccupazionale.setDataCalcoloMesiSosp(
														statoOccCorrente.getDataCalcoloMesiSosp());
												nuovoStatoOccupazionale
														.setDataAnzianita(statoOccCorrente.getDataAnzianita());
												nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoStatoOccupazionale,
														statoOccCorrente);
												DBStore.creaNuovoStatoOcc(nuovoStatoOccBean, dataChiusuraMobilita,
														requestContainer, txExecutor);
												statoOccCorrente = nuovoStatoOccBean;
												statiOccupazionaliCreati.add(nuovoStatoOccBean);
												// se il nuovo stato occupazionale all'atto della chiusura mobilità è
												// occupato
												// allora devo chiudere l'eventuale did aperta e patto collegato
												if (nuovoStatoOccBean
														.getStatoOccupazionale() == StatoOccupazionaleBean.B) {
													String dataChiusuraDid = dataChiusuraMobilita;
													SourceBean didAperta = cercaDid(dataChiusuraDid);
													SourceBean pattoAperto = cercaPatto(dataChiusuraDid);
													// seleziona il patto aperto
													if (didAperta != null) {
														String dataDichiarazione = (String) didAperta
																.getAttribute("datDichiarazione");
														if (dataDichiarazione != null && !dataDichiarazione.equals("")
																&& DateUtils.compare(dataDichiarazione,
																		dataChiusuraDid) > 0)
															dataChiusuraDid = dataDichiarazione;
														if (!didAperta.containsAttribute("codMotivoFineAtto")
																|| didAperta.getAttribute("codMotivoFineAtto")
																		.toString().equals("")
																|| didAperta.getAttribute("codMotivoFineAtto")
																		.toString().equals("AV")) {
															DBStore.chiudiDID(didAperta, dataChiusuraDid, "AV",
																	requestContainer, txExecutor);
															DidBean didApertaBean = new DidBean(didAperta);
															chiudiDidInVettoreDids(
																	didApertaBean.getPrgDichDisponibilita(),
																	dataChiusuraDid);
															aggiornaNumKloDichDispoInMovimenti(didApertaBean,
																	new BigDecimal(didApertaBean
																			.getAttribute("numKloDichDisp").toString()),
																	movimenti);
														} else {
															addWarning(
																	MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO);
														}
													}
													if (pattoAperto != null) {
														if (!pattoAperto.containsAttribute("codMotivoFineAtto")
																|| pattoAperto.getAttribute("codMotivoFineAtto")
																		.toString().equals("")
																|| pattoAperto.getAttribute("codMotivoFineAtto")
																		.toString().equals("AV")) {
															DBStore.chiudiPatto297(pattoAperto, dataChiusuraDid, "AV",
																	requestContainer, txExecutor);
															aggiornaNumKloPatto(new BigDecimal(pattoAperto
																	.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
																	dataChiusuraDid,
																	new BigDecimal(pattoAperto
																			.getAttribute(PattoBean.NUMKLO_PATTO_LAV)
																			.toString()));
														}
													}
												}
											} else {
												nuovoStatoOccBean = statoOccCorrente;
											}
										} else {
											nuovoStatoOccupazionale = new StatoOccupazionaleBean();
											nuovoStatoOccupazionale.setCdnLavoratore((BigDecimal) cdnLavoratore);
											nuovoStatoOccupazionale.setStatoOccupazionale(StatoOccupazionaleBean.A21);
											nuovoStatoOccupazionale.setCodMonoProvenienza("M");
											nuovoStatoOccupazionale.setDataInizio(dataChiusuraMobilita);
											nuovoStatoOccupazionale.setChanged(true);
											nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoStatoOccupazionale,
													statoOccCorrente);
											DBStore.creaNuovoStatoOcc(nuovoStatoOccBean, dataChiusuraMobilita,
													requestContainer, txExecutor);
											statoOccCorrente = nuovoStatoOccBean;
											statiOccupazionaliCreati.add(nuovoStatoOccBean);
										}
									} else {
										nuovoStatoOccBean = statoOccCorrente;
									}
								}
							}
						}
					} else {
						// eventuale evento ChiusuraMobilità (flag = "S") e in questo caso
						// bisogna proseguire con lo stesso stato occupazionale
						nuovoStatoOccBean = statoOccCorrente;
					}
				}
			}
		}
		return nuovoStatoOccBean;
	}

	private StatoOccupazionaleBean controllaDidPrecInRicostruzione(EventoAmministrativo evento,
			StatoOccupazionaleBean statoOccCorrente) throws Exception {
		StatoOccupazionaleBean nuovoStatoOcc = null;
		DidBean db = (DidBean) evento;
		_logger.debug("SituazioneAmministrativa.ricostruzioneStoria(): DID:" + db);

		nuovoStatoOcc = chiusuraDidPrecedente(db, statoOccCorrente);
		return nuovoStatoOcc;
	}

	private StatoOccupazionaleBean gestisciDidInRicostruzione(EventoAmministrativo evento,
			StatoOccupazionaleBean statoOccCorrente, int i, StatoOccupazionaleManager2 statoOccManager)
			throws Exception {
		StatoOccupazionaleBean nuovoStatoOcc = null;
		DidBean db = (DidBean) evento;
		_logger.debug("SituazioneAmministrativa.ricostruzioneStoria(): DID:" + db);

		// lettura codStatoAtto associato alla did
		String codStatoAtto = (String) db.getAttribute("codStatoAtto");
		// debbo controllare che la did abbia avuto impatti, ovvero che sia protocollata
		// se non è protocollata non viene considerata nella ricostruzione storia
		if (!codStatoAtto.equals("PR")) {
			_logger.debug("SituazioneAmministrativa.ricostruzioneStoria(): DID:" + db + " scartata");

			nuovoStatoOcc = statoOccCorrente;
		} else {
			// chiamata al metodo che si occupa di calcolare il nuovo stato occupazionale
			// in seguito all'evento amministrativo rappresentato dalla did
			StatoOccupazionaleBean statoOcc = gestisciDid(statoOccCorrente, db, statoOccManager);
			nuovoStatoOcc = new StatoOccupazionaleBean(statoOcc, statoOccCorrente);
			nuovoStatoOcc.setChanged(statoOcc.ischanged());
			// Aggiornamento sul db dello stato occupazionale associato alla did
			DBStore.aggiornaDID(db.getSource(), nuovoStatoOcc.getPrgStatoOccupaz(), requestContainer, txExecutor);
			// *** Aggiorno il numklo dell'eventuale ChiusuraDID successiva ***
			int movimentiSize = movimenti.size();
			for (int k = i; k < movimentiSize; k++) {
				EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
				if (c.getTipoEventoAmministrativo() == EventoAmministrativo.CHIUSURA_DID) {
					ChiusuraDidBean cDb = (ChiusuraDidBean) c;
					if (cDb.getPrgDichDisponibilita().equals(db.getPrgDichDisponibilita())) {
						_logger.debug(
								"SituazioneAmministrativa.ricostruzioneStoria():aggiornamento numKlo chiusura DID: "
										+ cDb);
						cDb.updAttribute("numKloDichDisp", db.getAttribute("numKloDichDisp"));
						break;
					}
				}
			} // end for
		}
		return nuovoStatoOcc;
	}

	private StatoOccupazionaleBean gestisciChiusuraDidInRicostruzione(EventoAmministrativo evento,
			StatoOccupazionaleBean statoOccCorrente, Vector vettParametriImpatti) throws Exception {
		ChiusuraDidBean chiusuraDid = (ChiusuraDidBean) evento;
		// gestisci la chiusura della did
		Vector movApertiPerTirocini = null;
		int nPosMobilita = -1;
		String dataFineDid = (String) chiusuraDid.getAttribute("datFine");
		String codMotivoFine = (String) chiusuraDid.getAttribute("codMotivoFineAtto");
		String dataDichiarazione = (String) chiusuraDid.getDataInizio();
		String flgImpattiTirInMobSosp = vettParametriImpatti.get(0).toString();
		String flgImpattiTirInMobEff = vettParametriImpatti.get(1).toString();
		if (dataFineDid == null)
			dataFineDid = (String) chiusuraDid.getAttribute("datFine_originale");
		if (dataFineDid != null && codMotivoFine == null)
			codMotivoFine = "AV";
		boolean gestione150 = (DateUtils.compare(dataFineDid, getData150()) >= 0);
		// Aggiornamento sul db della did specificando la data chiusura
		// e il motivo di chiusura della did
		DBStore.chiudiDID(chiusuraDid.getSource(), dataFineDid, codMotivoFine, requestContainer, txExecutor);
		chiudiDidInVettoreDids(chiusuraDid.getPrgDichDisponibilita(), dataFineDid);
		// *** Aggiorno il numklo della DID precedente ***
		int movimentiSize = movimenti.size();
		for (int k = 0; k < movimentiSize; k++) {
			EventoAmministrativo c = (EventoAmministrativo) movimenti.get(k);
			if (c.getTipoEventoAmministrativo() == EventoAmministrativo.DID) {
				DidBean did = (DidBean) c;
				if (did.getPrgDichDisponibilita().equals(chiusuraDid.getPrgDichDisponibilita())) {
					_logger.debug("SituazioneAmministrativa.ricostruzioneStoria():aggiornamento numKlo DID: " + did);
					did.updAttribute("numKloDichDisp", chiusuraDid.getAttribute("numKloDichDisp"));
					break;
				}
			}
		} // end for
			// creazione stato occupazionale associato al motivo di fine atto
		String codStatoOccupaz = StatoOccupazionaleBean.getStatoOccupazionaleAssociatoAlMotivoDineAtto(codMotivoFine,
				dataFineDid);
		nPosMobilita = Controlli.isInMobilita(this.listaMobilita, dataFineDid);
		// controlla se ci sono movimenti aperti nella stessa data di chiusura did
		movApertiPerTirocini = Controlli.getMovimentiAperti(estraiMovimentiDaEventi(this.movimenti), dataFineDid);
		if (movApertiPerTirocini.size() > 0) {
			if (!Controlli.soloTirocini(movApertiPerTirocini)) {
				// se risulta gia occupato deve prosegiure con lo stesso stato occupazionale
				// e non ne deve crearne uno nuovo, se non si trova in mobilità
				if (nPosMobilita >= 0) {
					// gestione prima del decreto fornero 2014
					if (dataFineDid == null || dataFineDid.equals("")
							|| DateUtils.compare(dataFineDid, MessageCodes.General.DATA_DECRETO_FORNERO_2014) < 0) {

						if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.B2) {
							statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.B2);
							statoOccCorrente.setDataInizio(dataFineDid);
							statoOccCorrente.setCodMonoProvenienza("M");
							statoOccCorrente.setChanged(true);
							DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
						} else {
							statoOccCorrente.setChanged(false);
						}
					}
					// gestione post decreto fornero 2014, se alla data chiusura DID c'è un periodo di mobilità, allora
					// vuol dire che a quella data non esiste di sicuro un movimento con contratto parasubordinato (che
					// non fa sospensione), altrimenti
					// nella fase di AllineamentoMobilità la mobilità stessa, alla data dataFineDid, sarebbe già
					// decaduta
					else {
						MobilitaManager mobManager = new MobilitaManager(this);
						double limiteAttuale = mobManager.getLimiteAttuale(movApertiPerTirocini, dataFineDid, this.cm);
						boolean superamentoReddito = mobManager.controllaSuperamentoReddito(movApertiPerTirocini,
								dataFineDid, limiteAttuale);
						if (superamentoReddito) {
							if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.B1) {
								statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.B1);
								statoOccCorrente.setDataInizio(dataFineDid);
								statoOccCorrente.setCodMonoProvenienza("M");
								statoOccCorrente.setChanged(true);
								DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
							} else {
								statoOccCorrente.setChanged(false);
							}
						} else {
							if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.A212) {
								statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.A212);
								statoOccCorrente.setDataInizio(dataFineDid);
								statoOccCorrente.setCodMonoProvenienza("M");
								statoOccCorrente.setChanged(true);
								DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
							} else {
								statoOccCorrente.setChanged(false);
							}
						}

					}
				} else {
					if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.B) {
						statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.B);
						statoOccCorrente.setDataInizio(dataFineDid);
						statoOccCorrente.setCodMonoProvenienza("M");
						statoOccCorrente.setChanged(true);
						DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
					} else {
						statoOccCorrente.setChanged(false);
					}
				}
			} else {
				// solo tirocini
				if (nPosMobilita >= 0) {
					if (flgImpattiTirInMobEff.equalsIgnoreCase("S")) {
						if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.A213) {
							statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.A213);
							statoOccCorrente.setDataInizio(dataFineDid);
							statoOccCorrente.setCodMonoProvenienza("M");
							statoOccCorrente.setChanged(true);
							DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
						} else {
							statoOccCorrente.setChanged(false);
						}
					} else {
						if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
							statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.A21);
							statoOccCorrente.setDataInizio(dataFineDid);
							statoOccCorrente.setCodMonoProvenienza("M");
							statoOccCorrente.setChanged(true);
							DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
						} else {
							statoOccCorrente.setChanged(false);
						}
					}
				} else {
					if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.encode(codStatoOccupaz)) {
						statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.encode(codStatoOccupaz));
						statoOccCorrente.setDataInizio(dataFineDid);
						statoOccCorrente.setCodMonoProvenienza("D");
						statoOccCorrente.setChanged(true);
						DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
					} else {
						statoOccCorrente.setChanged(false);
					}
				}
			}
		} else {
			// Non ci sono movimenti aperti nel giorno chiusura did
			// Se la chiusura did avviene in un periodo di mobilità e non ci sono
			// movimenti aperti allora devo creare lo stato occupazionale di Disoccupato
			if (nPosMobilita >= 0) {
				if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.A21) {
					statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.A21);
					statoOccCorrente.setDataInizio(dataFineDid);
					statoOccCorrente.setCodMonoProvenienza("B");
					statoOccCorrente.setChanged(true);
					DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
				} else {
					statoOccCorrente.setChanged(false);
				}
			} else {
				if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.encode(codStatoOccupaz)) {
					statoOccCorrente.setStatoOccupazionale(StatoOccupazionaleBean.encode(codStatoOccupaz));
					statoOccCorrente.setDataInizio(dataFineDid);
					statoOccCorrente.setCodMonoProvenienza("D");
					statoOccCorrente.setChanged(true);
					DBStore.creaNuovoStatoOcc(statoOccCorrente, dataFineDid, requestContainer, txExecutor);
				} else {
					statoOccCorrente.setChanged(false);
				}
			}
		}
		if (statoOccCorrente.ischanged()) {
			statiOccupazionaliCreati.add(statoOccCorrente);
		}
		// ****************************************************//
		// chiusura eventuale patto associato alla did precedentemente chiusa
		List pattiPeriodo = cercaPatto(dataDichiarazione, dataFineDid);
		for (int j = 0; j < pattiPeriodo.size(); j++) {
			SourceBean patto = (SourceBean) pattiPeriodo.get(j);
			if (patto.getAttribute("PRGDICHDISPONIBILITA") != null && chiusuraDid.getPrgDichDisponibilita() != null
					&& patto.getAttribute("PRGDICHDISPONIBILITA").equals(chiusuraDid.getPrgDichDisponibilita())) {
				String dataChiusuraPatto = patto.getAttribute(PattoBean.DB_DAT_FINE) != null
						? patto.getAttribute(PattoBean.DB_DAT_FINE).toString()
						: "";
				if (dataChiusuraPatto.equals("")) {
					DBStore.chiudiPatto297(patto, dataFineDid, codMotivoFine, requestContainer, txExecutor);
					aggiornaNumKloPatto(new BigDecimal(patto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
							dataFineDid, new BigDecimal(patto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
				}
			}
		}
		return statoOccCorrente;
	}

	private StatoOccupazionaleBean gestisciMovimentoInRicostruzione(MovimentoBean m,
			StatoOccupazionaleBean statoOccCorrente, Object cdnLavoratore, StatoOccupazionaleManager2 statoOccManager,
			String dataRif, Vector vettParametriRicostruzione, int posizione) throws Exception {
		StatoOccupazionaleBean nuovoStatoOcc = null;
		Vector movimentiAnno = null;
		switch (m.getTipoMovimento()) {
		case MovimentoBean.ASSUNZIONE:
			_logger.debug("SituazioneAmministrativa.ricostruzioneStoria():AVV:" + m);

			if (m.containsAttribute("FLAG_NON_IMPATTANTE")) {
				nuovoStatoOcc = statoOccCorrente;
			} else {
				movimentiAnno = getMovimentiAnno(dataRif, (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO),
						(BigDecimal) m.getAttribute("PRGPERIODOINTERMITTENTE"));
				// chiamata all'algoritmo che calcola il nuovo stato occupazionale
				// a partire da quello corrente e in seguito al movimento considerato nella ricostruzione
				nuovoStatoOcc = statoOccManager.avviamento(m, statoOccCorrente, movimentiAnno,
						vettParametriRicostruzione, posizione);
			}
			m.setStatoOccupazionale(nuovoStatoOcc);
			break;
		case MovimentoBean.CESSAZIONE:
			_logger.debug("SituazioneAmministrativa.ricostruzioneStoria():CES:" + m);

			if (m.containsAttribute("FLAG_NON_IMPATTANTE")) {
				nuovoStatoOcc = statoOccCorrente;
			} else {
				movimentiAnno = new Vector();
				if (dataRif != null && !dataRif.equals("")) {
					movimentiAnno = getMovimentiAnnoDID(dataRif, posizione);
				}
				// chiamata all'algoritmo che calcola il nuovo stato occupazionale
				// a partire da quello corrente e in seguito alla cessazione considerata nella ricostruzione
				nuovoStatoOcc = statoOccManager.cessazioneTD((Cessazione) m, statoOccCorrente, movimentiAnno,
						vettParametriRicostruzione, getData150(), getListaDisabiliCM());
				// riapertura eventuale did e patto (in mobilità può succedere che la did stipulata
				// in precedenza venga chiusa perché nel periodo di mobilità c'è un mov a t.i full time con reddito alto
				// che provoca la chiusura. Bisogna gestire il caso in cui il lavoratore da occupato all'
				// interno di una mobilità passi a disoccupato (cessazione del mov a t.i e come conseguenza con reddito
				// basso nei mesi
				// in cui lavora durante il tempo indeterminato) e in questo caso per coerenza bisogna riaprire la did e
				// l'eventuale patto.
				// Tutto questo se e solo se non esiste già una did successiva già aperta e in tal caso
				// la funzione cercaUltimaDidStoricizzata restituisce null
				if (nuovoStatoOcc.getStatoOccupazionale() == StatoOccupazionaleBean.A21) {
					boolean pattoRiaperto = false;
					DidBean did = cercaUltimaDidStoricizzata(dids, dataRif);
					if (did != null && did.getAttribute("datFine") != null) {
						String dataDichiarazione = did.getDataInizio();
						if (DIDManager.dichiarazioneDidInMobilita(this.getListaMobilita(), dataDichiarazione)) {
							// riapro la did
							BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
							numKlo = numKlo.add(new BigDecimal(1));
							did.updAttribute("numKloDichDisp", numKlo);
							DBStore.riapriDID(did.getAttribute("prgDichDisponibilita"), dataDichiarazione, numKlo,
									RequestContainer.getRequestContainer(), txExecutor);
							did.updAttribute("flag_changed", "1");
							did.setAttribute("datFine_originale", did.getAttribute("datFine"));
							did.delAttribute("datFineChanged");
							did.delAttribute("datFine");
							// Aggiorno il numklo dell'eventuale ChiusuraDID successiva
							aggiornaNumKloDichDispoInMovimenti(did, numKlo, movimenti);
							// riapertura patto associato alla did
							pattoRiaperto = riapriPattoAssocDid(did);
						}
					}
					if (!pattoRiaperto) {
						riapriPattoMobilita(dataRif, cdnLavoratore.toString());
					}
				}
			}
			m.setStatoOccupazionale(nuovoStatoOcc);
			if (m.getCollegato() != null && m.getPrgMovimento() != null)
				((Cessazione) m).movimentoCessato();
			else if (m.inInserimento())
				setPrgStatoOccMovNonCollegato(nuovoStatoOcc.getProgressivoDB().toString());

			break;

		case MovimentoBean.TRASFORMAZIONE:
			Trasformazione tb = (Trasformazione) m;
			_logger.debug("SituazioneAmministrativa.ricostruzioneStoria():TRA:" + tb);

			if (dataRif != null && !dataRif.equals("")
					&& DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0
					&& m.containsAttribute(Contratto.FIELD_FLG_SOSPENSIONE)) {
				// il field FLGCONTRATTOSOSPENSIONE per le trasformazioni si può avere nel caso in cui la trasformazione
				// sia orfana
				if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.B1) {
					StatoOccupazionaleBean n = new StatoOccupazionaleBean();
					n.setCdnLavoratore(m.getCdnLavoratore());
					n.setStatoOccupazionaleRagg("D");
					n.setStatoOccupazionale("B1");
					n.setDataAnzianita(statoOccCorrente.getDataAnzianita());
					n.setDataCalcoloAnzianita(statoOccCorrente.getDataCalcoloAnzianita());
					n.setDataCalcoloMesiSosp(statoOccCorrente.getDataCalcoloMesiSosp());
					nuovoStatoOcc = new StatoOccupazionaleBean(n, statoOccCorrente);
					nuovoStatoOcc.setChanged(true);
					nuovoStatoOcc.setCodMonoProvenienza("M");
					DBStore.creaNuovoStatoOcc(nuovoStatoOcc, dataRif, requestContainer, txExecutor);
					m.setStatoOccupazionale(nuovoStatoOcc);
				} else {
					nuovoStatoOcc = statoOccCorrente;
					m.setStatoOccupazionale(nuovoStatoOcc);
				}
			} else {
				// se la trasformazione era in un periodo di mobilità allora è impattante (se è a tempo indeterminato)
				int nPosMobilita = -1;
				String codMonoTempo = tb.getCodMonoTempo();
				if (codMonoTempo.equals("I")) {
					List listaMobilitaIniziale = new ArrayList();
					if (listaMobilita.size() > 0) {
						ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, txExecutor);
						listaMobilitaIniziale = mobilita.getMobilita();
						nPosMobilita = Controlli.isInMobilita(listaMobilitaIniziale, tb.getDataInizio());
					}
				}
				if (nPosMobilita >= 0) {
					movimentiAnno = getMovimentiAnno(dataRif,
							(BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO),
							(BigDecimal) m.getAttribute("PRGPERIODOINTERMITTENTE"));
					_logger.debug(
							"SituazioneAmministrativa.ricostruzioneStoria():TRA:trasformazione non impattante:" + tb);

					m.setAttribute("TRASFORMAZIONE_IN_MOBILITA", "1");
					nuovoStatoOcc = statoOccManager.avviamento(m, statoOccCorrente, movimentiAnno,
							vettParametriRicostruzione, posizione);
					m.setStatoOccupazionale(nuovoStatoOcc);
					if (m.getCollegato() == null && m.inInserimento())
						setPrgStatoOccMovNonCollegato(nuovoStatoOcc.getProgressivoDB().toString());
				} else {
					// Controllo se il movimento non è impattante (FLAG_NON_IMPATTANTE); in tal caso
					// la trasformazione è stata considerata nel movimento di avviamento corrispondente
					if (m.containsAttribute("FLAG_NON_IMPATTANTE")) {
						nuovoStatoOcc = statoOccCorrente;
						m.setStatoOccupazionale(nuovoStatoOcc);
					} else {
						if (tb.modificato() && (m.getCollegato() != null)) {
							// Movimento collegato
							MovimentoBean avv = tb.getAvviamento();
							dataRif = avv.getDataInizio();
							movimentiAnno = getMovimentiAnno(dataRif,
									(BigDecimal) avv.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO),
									(BigDecimal) m.getAttribute("PRGPERIODOINTERMITTENTE"));
							_logger.debug("SituazioneAmministrativa.ricostruzioneStoria():TRA-AVV:" + avv);

							nuovoStatoOcc = statoOccManager.avviamento(avv, statoOccCorrente, movimentiAnno,
									vettParametriRicostruzione, posizione);
							tb.setStatoOccupazionale(nuovoStatoOcc);
						} else {
							// al limite cambia il tipo movimento , "AVV", prima di passarlo a metodo di aggiornamento
							movimentiAnno = getMovimentiAnno(dataRif,
									(BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO),
									(BigDecimal) m.getAttribute("PRGPERIODOINTERMITTENTE"));
							_logger.debug(
									"SituazioneAmministrativa.ricostruzioneStoria():TRA:trasformazione non impattante:"
											+ tb);

							nuovoStatoOcc = statoOccManager.avviamento(m, statoOccCorrente, movimentiAnno,
									vettParametriRicostruzione, posizione);
							m.setStatoOccupazionale(nuovoStatoOcc);
							if (m.getCollegato() == null && m.inInserimento())
								setPrgStatoOccMovNonCollegato(nuovoStatoOcc.getProgressivoDB().toString());
						}
					}
				}
			}
			break;

		case MovimentoBean.PROROGA:
			_logger.debug("SituazioneAmministrativa.ricostruzioneStoria():PRO:" + m);

			// Se la proroga si trova in un periodo di mobilità, lo stato occupazionale può cambiare, altrimenti
			// Controllo se il movimento non è impattante (FLAG_NON_IMPATTANTE); in tal caso
			// la proroga è stata considerata nel movimento di avviamento corrispondente
			if (dataRif != null && !dataRif.equals("")
					&& DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0
					&& m.containsAttribute(Contratto.FIELD_FLG_SOSPENSIONE)) {
				// il field FLGCONTRATTOSOSPENSIONE per le proroghe si può avere nel caso in cui la proroga sia orfana
				if (statoOccCorrente.getStatoOccupazionale() != StatoOccupazionaleBean.B1) {
					StatoOccupazionaleBean n = new StatoOccupazionaleBean();
					n.setCdnLavoratore(m.getCdnLavoratore());
					n.setStatoOccupazionaleRagg("D");
					n.setStatoOccupazionale("B1");
					n.setDataAnzianita(statoOccCorrente.getDataAnzianita());
					n.setDataCalcoloAnzianita(statoOccCorrente.getDataCalcoloAnzianita());
					n.setDataCalcoloMesiSosp(statoOccCorrente.getDataCalcoloMesiSosp());
					nuovoStatoOcc = new StatoOccupazionaleBean(n, statoOccCorrente);
					nuovoStatoOcc.setChanged(true);
					nuovoStatoOcc.setCodMonoProvenienza("M");
					DBStore.creaNuovoStatoOcc(nuovoStatoOcc, dataRif, requestContainer, txExecutor);
					m.setStatoOccupazionale(nuovoStatoOcc);
				} else {
					nuovoStatoOcc = statoOccCorrente;
					m.setStatoOccupazionale(nuovoStatoOcc);
				}
			} else {
				if (m.containsAttribute("FLAG_NON_IMPATTANTE")) {
					nuovoStatoOcc = statoOccCorrente;
					m.setStatoOccupazionale(nuovoStatoOcc);
				}
				// la parte di codice dell'else non dovrebbe più essere eseguita
				// in quanto le proroghe hanno l'attributo FLAG_NON_IMPATTANTE (creaBeans)
				else {
					movimentiAnno = getMovimentiAnno(dataRif,
							(BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO),
							(BigDecimal) m.getAttribute("PRGPERIODOINTERMITTENTE"));
					nuovoStatoOcc = statoOccManager.proroga(m, statoOccCorrente, movimentiAnno,
							vettParametriRicostruzione, posizione);
					m.setStatoOccupazionale(nuovoStatoOcc);
				}
			}
			break;
		}
		return nuovoStatoOcc;
	}

	/**
	 * 
	 * @param statoOccCorrente
	 * @param dataCalcoloInizioAnno
	 * @param movimenti
	 * @throws Exception
	 */
	public StatoOccupazionaleBean impattiACavalloDiAnni(StatoOccupazionaleBean statoOccCorrente,
			String dataCalcoloInizioAnno, List movimenti, StatoOccupazionaleManager2 statoOccManager) throws Exception {

		Vector movimentiImpatti = movimentiImpattantiCalcoloStatoOccupaz(dataCalcoloInizioAnno, movimenti);
		if (movimentiImpatti.size() > 0) {
			StatoOccupazionaleBean nuovoStatoOccupazionale = null;
			StatoOccupazionaleBean nuovoStatoOccBean = null;
			nuovoStatoOccupazionale = calcolaStatoOccupazionaleInData(movimentiImpatti, statoOccCorrente,
					dataCalcoloInizioAnno, listaMobilita, statoOccManager);

			if (statoOccCorrente.getStatoOccupazionale() != nuovoStatoOccupazionale.getStatoOccupazionale()) {
				nuovoStatoOccupazionale.setCodMonoProvenienza("M");
				// provenienza da movimenti
				nuovoStatoOccupazionale.setDataInizio(dataCalcoloInizioAnno);
				nuovoStatoOccupazionale.setChanged(true);
				// Ereditano data anzianità e sospensione dallo stato occupazionale precedente e nel caso in cui
				// lo stato occupazionale nuovo non fosse come raggruppamento D o I, tali date vengono memorizzare vuote
				// nel metodo DBStore.creaNuovoStatoOcc
				nuovoStatoOccupazionale.setDataCalcoloAnzianita(statoOccCorrente.getDataCalcoloAnzianita());
				nuovoStatoOccupazionale.setDataCalcoloMesiSosp(statoOccCorrente.getDataCalcoloMesiSosp());
				nuovoStatoOccupazionale.setDataAnzianita(statoOccCorrente.getDataAnzianita());
				nuovoStatoOccupazionale.setCdnLavoratore(statoOccCorrente.getCdnLavoratore());
				// nuovoStatoOccBean = new StatoOccupazionaleBean(nuovoStatoOccupazionale,statoOccCorrente);
				DBStore.creaNuovoStatoOcc(nuovoStatoOccupazionale, dataCalcoloInizioAnno, requestContainer, txExecutor);
				statoOccCorrente = nuovoStatoOccupazionale;
				statiOccupazionaliCreati.add(nuovoStatoOccupazionale);
				// se il nuovo stato occupazionale è occupato
				// allora devo chiudere l'eventuale did aperta e patto collegato
				if (nuovoStatoOccupazionale.getStatoOccupazionale() == StatoOccupazionaleBean.B) {
					SourceBean didAperta = cercaDid(dataCalcoloInizioAnno);
					SourceBean pattoAperto = cercaPatto(dataCalcoloInizioAnno);
					String dataChiusuraDid = DateUtils.giornoPrecedente(dataCalcoloInizioAnno);
					// seleziona il patto aperto
					if (didAperta != null) {
						String dataDichiarazione = (String) didAperta.getAttribute("datDichiarazione");
						if (dataDichiarazione != null && !dataDichiarazione.equals("")
								&& DateUtils.compare(dataDichiarazione, dataChiusuraDid) > 0)
							dataChiusuraDid = dataDichiarazione;
						if (!didAperta.containsAttribute("codMotivoFineAtto")
								|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("")
								|| didAperta.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
							DBStore.chiudiDID(didAperta, dataChiusuraDid, "AV", requestContainer, txExecutor);
							DidBean didApertaBean = new DidBean(didAperta);
							chiudiDidInVettoreDids(didApertaBean.getPrgDichDisponibilita(), dataChiusuraDid);
							aggiornaNumKloDichDispoInMovimenti(didApertaBean,
									new BigDecimal(didApertaBean.getAttribute("numKloDichDisp").toString()), movimenti);
						} else {
							addWarning(MessageCodes.DID.DID_GIA_CHIUSA_NON_PER_AVVIAMENTO);
						}
					}
					if (pattoAperto != null) {
						if (!pattoAperto.containsAttribute("codMotivoFineAtto")
								|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("")
								|| pattoAperto.getAttribute("codMotivoFineAtto").toString().equals("AV")) {
							DBStore.chiudiPatto297(pattoAperto, dataChiusuraDid, "AV", requestContainer, txExecutor);
							aggiornaNumKloPatto(
									new BigDecimal(pattoAperto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
									dataChiusuraDid,
									new BigDecimal(pattoAperto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
						}
					}
				}
			}
		}
		return statoOccCorrente;
	}

	/**
	 * determina i movimenti impattanti per il calcolo dello stato occupazionale in una data di riferimento
	 * 
	 * @param dataRif
	 * @param movimenti
	 * @return
	 * @throws Exception
	 */
	public Vector movimentiImpattantiCalcoloStatoOccupaz(String dataRif, List movimenti) throws Exception {
		int iCont = 0;
		MovimentoBean movimento = null;
		EventoAmministrativo objEvento = null;
		String dataInizioMov = "";
		String dataFineMov = "";
		Map<BigDecimal, String> mapProrogati = new HashMap<BigDecimal, String>();
		boolean movDaTrattare = false;
		Vector movimentiImpattanti = new Vector();
		int movimentiSize = movimenti.size();
		for (; iCont < movimentiSize; iCont++) {
			movDaTrattare = false;
			objEvento = (EventoAmministrativo) movimenti.get(iCont);
			int tipoEvento = objEvento.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.AVVIAMENTO) {
				movDaTrattare = true;
				movimento = (MovimentoBean) objEvento;
				dataInizioMov = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
				dataFineMov = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
						? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
						: "";
				if (movimento.getAttribute("MOVIMENTI_PROROGATI") != null) {
					Vector prec = (Vector) movimento.getAttribute("MOVIMENTI_PROROGATI");
					int precSize = prec.size();
					if (precSize > 0) {
						int movUltimoCatena = precSize - 1;
						SourceBean movimentoUltimo = (SourceBean) prec.get(movUltimoCatena);
						dataFineMov = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
								? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
								: "";
						for (int kPrec = 0; kPrec < precSize; kPrec++) {
							SourceBean movimentoCatena = (SourceBean) prec.get(kPrec);
							BigDecimal prgMovProrogato = (BigDecimal) movimentoCatena
									.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
							mapProrogati.put(prgMovProrogato, "S");
						}
					}
				} else {
					dataInizioMov = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
					dataFineMov = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
							? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
							: "";
				}
			} else {
				if (tipoEvento == EventoAmministrativo.PROROGA || tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
					movimento = (MovimentoBean) objEvento;
					BigDecimal prgMovimento = movimento.getPrgMovimento();
					if (!mapProrogati.containsKey(prgMovimento)) {
						movDaTrattare = true;
						dataInizioMov = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
						dataFineMov = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
								? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
								: "";
					}

				}
			}

			if (movDaTrattare) {
				if (!movimento.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS) || !movimento
						.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString().equalsIgnoreCase("T")) {
					// dataInizioMov anche = dataRif per le proroghe che iniziano
					// all'inizio dell'anno
					// dataFineMov anche = dataRif per i movimenti che finiscono all'inizio
					// dell'anno
					if ((DateUtils.compare(dataInizioMov, dataRif) < 0
							|| (DateUtils.compare(dataInizioMov, dataRif) == 0
									&& tipoEvento != EventoAmministrativo.AVVIAMENTO))
							&& (dataFineMov == null || dataFineMov.equals("")
									|| DateUtils.compare(dataFineMov, dataRif) >= 0)) {
						movimentiImpattanti.add(movimento);
					}
				}
			}
		}
		return movimentiImpattanti;
	}

	/**
	 * 
	 * @param movimenti
	 *            (contiene i movimenti a cavallo della data dataRif (01/01/yyyy))
	 * @param statoOccIniziale
	 * @param dataRif
	 * @param listaMobilita
	 * @param statoOccManager
	 * @return
	 * @throws Exception
	 */
	public StatoOccupazionaleBean calcolaStatoOccupazionaleInData(Vector movimenti,
			StatoOccupazionaleBean statoOccIniziale, String dataRif, List listaMobilita,
			StatoOccupazionaleManager2 statoOccManager) throws Exception {

		StatoOccupazionaleBean nuovoStatoOccupazionale = new StatoOccupazionaleBean();
		StatoOccupazionaleBean n = null;
		MovimentoBean movimento = null;
		MovimentoBean movimentoDurata = null;
		int ggDiLavoro = 0;
		String dataFineMov = "";
		int annoDataRif = DateUtils.getAnno(dataRif);
		String dataAttuale = DateUtils.getNow();
		int annoDecretoFornero = DateUtils.getAnno(MessageCodes.General.DATA_DECRETO_FORNERO_2014);
		int annoDecreto150 = DateUtils.getAnno(getData150());
		boolean gestioneFornero = false;
		boolean gestione150 = false;
		LimiteRedditoExt limiteReddito = null;
		boolean appCategoriaParticolare = true;
		boolean appCategoriaParticolareCurr = true;
		boolean appCategoriaParticolareTD = true;
		StatoOccupazionaleBean statoOccupazionaleMovTI = new StatoOccupazionaleBean();
		boolean trovatoMovTIDecadenza = false;
		int nPosMovTD = -1;
		int nPosMovTIDecadenza = -1;
		boolean superatoAnnoPrimo = false;
		boolean noReddito = false;
		boolean noRedditoCurr = false;
		int codTipoMov = 0;
		int index = 0;
		int numeroMesi = 0;
		boolean movInMobilita = false;
		SourceBean sbMobilita = null;
		String dataInizioMovDurata = "";
		String dataPrecMobilita = "";
		String dataInizioMobilita = "";
		String dataFineMobilita = "";
		String dataFineMovDurata = "";
		String dataInizioAnnoMobilita = "";
		String dataFineAnnoMobilita = "";
		boolean bCalcolaGiorni = false;
		gestioneFornero = (DateUtils.compare(dataRif, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0);
		gestione150 = (DateUtils.compare(dataRif, getData150()) >= 0);
		boolean iscrittoCM = Controlli.inCollocamentoMiratoAllaData(getListaDisabiliCM(), dataRif);
		switch (statoOccIniziale.getStatoOccupazionaleRagg()) {
		case StatoOccupazionaleBean.RAGG_D:
			switch (statoOccIniziale.getStatoOccupazionale()) {
			case StatoOccupazionaleBean.B1: // sospensione anzianità
			case StatoOccupazionaleBean.B3: // occupato a rischio disoccupazione
			case StatoOccupazionaleBean.A212: // precario
			case StatoOccupazionaleBean.B2: // Disoccupato - Occupato in mobilità
				if (statoOccIniziale.getStatoOccupazionale() == StatoOccupazionaleBean.B2 && !gestioneFornero) {
					nuovoStatoOccupazionale = statoOccIniziale;
				} else {
					if (listaMobilita.size() > 0) {
						for (; index < listaMobilita.size(); index++) {
							sbMobilita = (SourceBean) listaMobilita.get(index);
							dataInizioMobilita = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
							dataFineMobilita = sbMobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)
									? sbMobilita.getAttribute(MobilitaBean.DB_DAT_FINE).toString()
									: "";
							if (sbMobilita.getAttribute("CODMONOATTIVA").toString().equals("A")
									&& !dataFineMobilita.equals("")
									&& DateUtils.compare(dataInizioMobilita, dataRif) <= 0
									&& DateUtils.compare(dataFineMobilita, dataRif) >= 0) {
								movInMobilita = true;
								break;
							}
						}
					}

					limiteReddito = new LimiteRedditoExt(dataRif);
					int movimentiSize = movimenti.size();
					for (int i = 0; i < movimentiSize; i++) {
						movimento = (MovimentoBean) movimenti.get(i);
						codTipoMov = movimento.getTipoMovimento();
						movimentoDurata = movimento;
						dataInizioMovDurata = movimentoDurata.getDataInizio();
						dataFineMovDurata = movimentoDurata.getDataFineEffettiva();
						if (codTipoMov == MovimentoBean.ASSUNZIONE) {
							if (movimentoDurata.getAttribute("MOVIMENTI_PROROGATI") != null) {
								Vector prec = (Vector) movimentoDurata.getAttribute("MOVIMENTI_PROROGATI");
								if (prec.size() > 0) {
									SourceBean movimentoUltimo = (SourceBean) prec.get(prec.size() - 1);
									dataFineMovDurata = movimentoUltimo
											.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null
													? (String) movimentoUltimo
															.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
													: null;
								}
							}
						}

						// calcolo durata
						if (!movInMobilita) {
							if (annoDataRif < annoDecretoFornero) {
								appCategoriaParticolareCurr = Controlli.isCategoriaParticolare(movimentoDurata);
							} else {
								if (annoDataRif == annoDecretoFornero) {
									appCategoriaParticolareCurr = Controlli
											.isCategoriaParticolareDecretoFornero(movimentoDurata, dataRif, false);
								} else {
									if (gestione150 && !iscrittoCM) {
										appCategoriaParticolareCurr = Controlli
												.isCategoriaParticolareDecreto150(movimentoDurata, dataRif);
									} else {
										appCategoriaParticolareCurr = Controlli
												.isCategoriaParticolareDecretoFornero(movimentoDurata, dataRif, true);
									}
								}
							}
							appCategoriaParticolare = appCategoriaParticolare && appCategoriaParticolareCurr;
						} else {
							// considero la durata escludendo i periodi di mobilità, fino al decreto Fornero 2014,
							// altrimenti la durata è per intero
							dataInizioMobilita = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
							dataPrecMobilita = DateUtils.giornoPrecedente(dataInizioMobilita);
							dataFineMobilita = sbMobilita.getAttribute(MobilitaBean.DB_DAT_FINE).toString();
							String dataSuccMobilita = DateUtils.giornoSuccessivo(dataFineMobilita);
							if (movimentoDurata.getTipoMovimento() == MovimentoBean.ASSUNZIONE
									&& movimentoDurata.getAttribute("MOVIMENTI_PROROGATI") != null) {
								Vector prec = (Vector) movimentoDurata.getAttribute("MOVIMENTI_PROROGATI");
								SourceBean movimentoAvv = null;
								SourceBean movimentoSucc = null;
								String dataInizioPrec = "";
								String dataInizioSucc = "";
								String dataFinePrec = "";
								String dataFineUltimoMovPro = null;
								String dataInizioPrimoMovPro = null;
								if (prec.size() > 0) {
									movimentoAvv = (SourceBean) prec.get(prec.size() - 1);
									if (movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null) {
										dataFineUltimoMovPro = movimentoAvv
												.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString();
									} else {
										dataFineUltimoMovPro = null;
									}
									movimentoAvv = (SourceBean) prec.get(0);
									dataInizioPrimoMovPro = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO)
											.toString();
								}
								if (gestione150 && !iscrittoCM) {
									if (dataFineUltimoMovPro == null || dataFineUltimoMovPro.equals("")
											|| DateUtils.compare(dataFineUltimoMovPro, dataAttuale) > 0) {
										dataFineUltimoMovPro = dataAttuale;
									}
									numeroMesi = Controlli.numeroMesiDiLavoroDecreto150(dataInizioPrimoMovPro,
											dataFineUltimoMovPro);
								} else {
									if (gestioneFornero) {
										if (dataFineUltimoMovPro == null || dataFineUltimoMovPro.equals("")) {
											numeroMesi = Integer.MAX_VALUE;
										} else {
											numeroMesi = Controlli.numeroMesiDiLavoro(dataInizioPrimoMovPro,
													dataFineUltimoMovPro);
										}
									} else {
										int precSize = prec.size();
										for (int k = 0; k < precSize; k++) {
											movimentoAvv = (SourceBean) prec.get(k);
											int kSucc = k + 1;
											dataInizioPrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO)
													.toString();
											if (movimentoAvv
													.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null) {
												dataFinePrec = movimentoAvv
														.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString();
											} else {
												dataFinePrec = null;
											}

											// Se il movimento successivo nel vettore dei movimenti prorogati ha la
											// stessa data inizio
											// del movimento corrente nel vettore dei prorogati, allora al fine del
											// reddito non lo considero
											if (kSucc < precSize) {
												movimentoSucc = (SourceBean) prec.get(kSucc);
												dataInizioSucc = movimentoSucc
														.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
												if (DateUtils.compare(dataInizioPrec, dataInizioSucc) == 0) {
													continue;
												}
											}

											if (DateUtils.compare(dataInizioPrec, dataInizioMobilita) < 0) {
												if (DateUtils.getAnno(dataInizioPrec) < DateUtils
														.getAnno(dataInizioMobilita)) {
													dataInizioAnnoMobilita = "01/01/"
															+ DateUtils.getAnno(dataInizioMobilita);
													movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO,
															dataInizioAnnoMobilita);
												} else {
													dataInizioAnnoMobilita = dataInizioPrec;
												}

												if (dataFinePrec == null || dataFinePrec.equals("")
														|| (dataFinePrec != null && !dataFinePrec.equals("")
																&& DateUtils.compare(dataFinePrec,
																		dataInizioMobilita) >= 0)) {
													if (DateUtils.getAnno(dataPrecMobilita) < DateUtils
															.getAnno(dataInizioAnnoMobilita)) {
														dataFineAnnoMobilita = dataInizioAnnoMobilita;
													} else {
														dataFineAnnoMobilita = dataPrecMobilita;
													}
													if (movimentoAvv
															.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
														movimentoAvv.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
																dataFineAnnoMobilita);
													} else {
														movimentoAvv.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
																dataFineAnnoMobilita);
													}
													numeroMesi = numeroMesi
															+ Controlli.numeroMesiDiLavoroFineMobilita(movimentoAvv);
												} else {
													if (DateUtils.compare(dataFinePrec, dataInizioMobilita) < 0) {
														if (DateUtils.getAnno(dataFinePrec) == DateUtils
																.getAnno(dataInizioMobilita)) {
															numeroMesi = numeroMesi + Controlli
																	.numeroMesiDiLavoroFineMobilita(movimentoAvv);
														}
													}
												}
												movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizioPrec);

												if (dataFinePrec == null || dataFinePrec.equals("")) {
													if (movimentoAvv
															.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
														movimentoAvv.delAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
													}
												} else {
													movimentoAvv.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
															dataFinePrec);
												}

												if (dataFinePrec != null && !dataFinePrec.equals("")
														&& DateUtils.compare(dataFinePrec, dataFineMobilita) > 0) {
													movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO,
															dataSuccMobilita);
													numeroMesi = numeroMesi
															+ Controlli.numeroMesiDiLavoroFineMobilita(movimentoAvv);
													movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO,
															dataInizioPrec);
												} else {
													if (dataFinePrec == null || dataFinePrec.equals("")) {
														numeroMesi = Integer.MAX_VALUE;
													}
												}
											} else {
												if (dataFinePrec == null || dataFinePrec.equals("")
														|| DateUtils.compare(dataFinePrec, dataFineMobilita) > 0) {
													String dataCalcoloMesi = dataInizioPrec;
													if (DateUtils.compare(dataSuccMobilita, dataInizioPrec) > 0) {
														movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO,
																dataSuccMobilita);
													}
													numeroMesi = numeroMesi
															+ Controlli.numeroMesiDiLavoroFineMobilita(movimentoAvv);
													movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO,
															dataInizioPrec);
												}
											}
										} // end for (int k=0;k<prec.size();k++)
									}
								}
							} // else if movimentoDurata.getAttribute("MOVIMENTI_PROROGATI") != null)
							else {
								if (gestione150 && !iscrittoCM) {
									if (dataFineMovDurata == null || dataFineMovDurata.equals("")
											|| DateUtils.compare(dataFineMovDurata, dataAttuale) > 0) {
										dataFineMovDurata = dataAttuale;
									}
									numeroMesi = Controlli.numeroMesiDiLavoroDecreto150(dataInizioMovDurata,
											dataFineMovDurata);
								} else {
									if (gestioneFornero) {
										if (dataFineMovDurata == null || dataFineMovDurata.equals("")) {
											numeroMesi = Integer.MAX_VALUE;
										} else {
											numeroMesi = Controlli.numeroMesiDiLavoro(dataInizioMovDurata,
													dataFineMovDurata);
										}
									} else {
										if (DateUtils.compare(dataInizioMovDurata, dataInizioMobilita) < 0) {
											if (DateUtils.getAnno(dataInizioMovDurata) < DateUtils
													.getAnno(dataInizioMobilita)) {
												dataInizioAnnoMobilita = "01/01/"
														+ DateUtils.getAnno(dataInizioMobilita);
												movimentoDurata.updAttribute(MovimentoBean.DB_DATA_INIZIO,
														dataInizioAnnoMobilita);
											} else {
												dataInizioAnnoMobilita = dataInizioMovDurata;
											}

											if (dataFineMovDurata == null || dataFineMovDurata.equals("")
													|| (dataFineMovDurata != null && !dataFineMovDurata.equals("")
															&& DateUtils.compare(dataFineMovDurata,
																	dataInizioMobilita) >= 0)) {
												if (DateUtils.getAnno(dataPrecMobilita) < DateUtils
														.getAnno(dataInizioAnnoMobilita)) {
													dataFineAnnoMobilita = dataInizioAnnoMobilita;
												} else {
													dataFineAnnoMobilita = dataPrecMobilita;
												}
												if (movimentoDurata
														.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
													movimentoDurata.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
															dataFineAnnoMobilita);
												} else {
													movimentoDurata.setAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
															dataFineAnnoMobilita);
												}
												numeroMesi = Controlli.numeroMesiDiLavoroFineMobilita(movimentoDurata);
											} else {
												if (DateUtils.compare(dataFineMovDurata, dataInizioMobilita) < 0) {
													if (DateUtils.getAnno(dataFineMovDurata) == DateUtils
															.getAnno(dataInizioMobilita)) {
														numeroMesi = Controlli
																.numeroMesiDiLavoroFineMobilita(movimentoDurata);
													} else {
														numeroMesi = 0;
													}
												}
											}
											movimentoDurata.updAttribute(MovimentoBean.DB_DATA_INIZIO,
													dataInizioMovDurata);

											if (dataFineMovDurata == null || dataFineMovDurata.equals("")) {
												if (movimentoDurata
														.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)) {
													movimentoDurata.delAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
												}
											} else {
												movimentoDurata.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
														dataFineMovDurata);
											}

											if (dataFineMovDurata != null && !dataFineMovDurata.equals("")
													&& DateUtils.compare(dataFineMovDurata, dataFineMobilita) > 0) {
												movimentoDurata.updAttribute(MovimentoBean.DB_DATA_INIZIO,
														dataSuccMobilita);
												numeroMesi = numeroMesi
														+ Controlli.numeroMesiDiLavoroFineMobilita(movimentoDurata);
												movimentoDurata.updAttribute(MovimentoBean.DB_DATA_INIZIO,
														dataInizioMovDurata);
											} else {
												if (dataFineMovDurata == null || dataFineMovDurata.equals("")) {
													numeroMesi = Integer.MAX_VALUE;
												}
											}
										} else {
											if (dataFineMovDurata == null || dataFineMovDurata.equals("")
													|| DateUtils.compare(dataFineMovDurata, dataFineMobilita) > 0) {
												movimentoDurata.updAttribute(MovimentoBean.DB_DATA_INIZIO,
														dataSuccMobilita);
												numeroMesi = Controlli.numeroMesiDiLavoroFineMobilita(movimentoDurata);
												movimentoDurata.updAttribute(MovimentoBean.DB_DATA_INIZIO,
														dataInizioMovDurata);
											}
										}
									}
								} // end if movimentoDurata.getAttribute("MOVIMENTI_PROROGATI") != null)
							}

							if (annoDataRif < annoDecretoFornero) {
								appCategoriaParticolareCurr = Controlli.isCategoriaParticolare(numeroMesi,
										movimentoDurata);
							} else {
								if (annoDataRif == annoDecretoFornero) {
									appCategoriaParticolareCurr = Controlli.isCategoriaParticolareDecretoFornero(
											numeroMesi, movimentoDurata, dataRif, false);
								} else {
									if (gestione150 && !iscrittoCM) {
										appCategoriaParticolareCurr = Controlli
												.isCategoriaParticolareDecreto150(numeroMesi, movimentoDurata);
									} else {
										appCategoriaParticolareCurr = Controlli.isCategoriaParticolareDecretoFornero(
												numeroMesi, movimentoDurata, dataRif, true);
									}
								}
							}
							appCategoriaParticolare = appCategoriaParticolare && appCategoriaParticolareCurr;
						}

						ggDiLavoro = 0;
						SourceBean sbcm = null;
						CmBean cm = null;
						if (statoOccManager != null && statoOccManager.getCm() == null) {
							if (this.getCm() != null) {
								statoOccManager.setCm(this.getCm());
							}
						}
						if (Contratto.getTipoContratto(movimento) == Contratto.DIP_TI) {
							if (!gestioneFornero) {
								if (nPosMovTIDecadenza < 0) {
									nPosMovTIDecadenza = i;
								}
								trovatoMovTIDecadenza = true;
							} else {
								if (!appCategoriaParticolareCurr) {
									trovatoMovTIDecadenza = true;
									if (nPosMovTIDecadenza < 0) {
										nPosMovTIDecadenza = i;
									}
								}
							}
						} else {
							if (nPosMovTD < 0) {
								nPosMovTD = i;
							}
							// appCategoriaParticolareTD per il momento non viene utilizzato per il calcolo dello stato
							// occupazionale
							// all'inizio del nuovo anno, dove l'anno è maggiore o uguale dell'anno decreto Fornero 2014
							if (annoDataRif < annoDecretoFornero) {
								appCategoriaParticolareTD = appCategoriaParticolareTD
										&& Controlli.isCategoriaParticolare(movimentoDurata);
							} else {
								if (annoDataRif == annoDecretoFornero) {
									appCategoriaParticolareTD = appCategoriaParticolareTD && Controlli
											.isCategoriaParticolareDecretoFornero(movimentoDurata, dataRif, false);
								} else {
									if (gestione150 && !iscrittoCM) {
										appCategoriaParticolareTD = appCategoriaParticolareTD
												&& Controlli.isCategoriaParticolareDecreto150(movimentoDurata, dataRif);
									} else {
										appCategoriaParticolareTD = appCategoriaParticolareTD && Controlli
												.isCategoriaParticolareDecretoFornero(movimentoDurata, dataRif, true);
									}
								}
							}
						}

						// calcolo del reddito
						if (Controlli.inCollocamentoMirato(this.getCm().getSource(), dataRif)) {
							if (statoOccIniziale.getLimiteReddito() < limiteReddito.get(LimiteReddito.CM)) {
								statoOccIniziale.setLimiteReddito(limiteReddito.get(LimiteReddito.CM));
							}
						} else {
							if (movInMobilita) {
								switch (Contratto.getTipoContratto(movimento)) {
								case Contratto.AUTONOMO:
									if (DateUtils.compare(dataRif,
											MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
										boolean esisteMovimentoParaSubordinato = false;
										if (movimenti.size() > 0) {
											esisteMovimentoParaSubordinato = ControlliExt
													.getMovimentiLavoroParaSubordinato(dataRif, movimenti);
										}
										if (esisteMovimentoParaSubordinato) {
											if (statoOccIniziale.getLimiteReddito() < limiteReddito
													.get(LimiteReddito.DIPENDENTE)) {
												statoOccIniziale
														.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
											}
										} else {
											if (statoOccIniziale.getLimiteReddito() < limiteReddito
													.get(LimiteReddito.AUTONOMO)) {
												statoOccIniziale
														.setLimiteReddito(limiteReddito.get(LimiteReddito.AUTONOMO));
											}
										}
									} else {
										boolean esisteMovimentoDip = false;
										if (movimenti.size() > 0) {
											esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(
													DateUtils.giornoSuccessivo(dataRif), movimenti);
										}
										if (esisteMovimentoDip) {
											if (statoOccIniziale.getLimiteReddito() < limiteReddito
													.get(LimiteReddito.DIPENDENTE)) {
												statoOccIniziale
														.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
											}
										} else {
											if (statoOccIniziale.getLimiteReddito() < limiteReddito
													.get(LimiteReddito.AUTONOMO)) {
												statoOccIniziale
														.setLimiteReddito(limiteReddito.get(LimiteReddito.AUTONOMO));
											}
										}
									}
									break;

								case Contratto.COCOCO:
								case Contratto.DIP_TD:
								case Contratto.DIP_TI:
									if (DateUtils.compare(dataRif,
											MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
										boolean esisteMovimentoParaSubordinato = false;
										if (movimenti.size() > 0) {
											esisteMovimentoParaSubordinato = ControlliExt
													.getMovimentiLavoroParaSubordinato(dataRif, movimenti);
										}
										if (esisteMovimentoParaSubordinato) {
											if (statoOccIniziale.getLimiteReddito() < limiteReddito
													.get(LimiteReddito.DIPENDENTE)) {
												statoOccIniziale
														.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
											}
										} else {
											boolean esisteMovimentoAutonomo = false;
											if (movimenti.size() > 0) {
												esisteMovimentoAutonomo = ControlliExt
														.getMovimentiLavoroAutonomo(dataRif, movimenti);
											}
											if (esisteMovimentoAutonomo) {
												if (statoOccIniziale.getLimiteReddito() < limiteReddito
														.get(LimiteReddito.AUTONOMO)) {
													statoOccIniziale.setLimiteReddito(
															limiteReddito.get(LimiteReddito.AUTONOMO));
												}
											} else {
												if (statoOccIniziale.getLimiteReddito() < limiteReddito
														.get(LimiteReddito.DIPENDENTE)) {
													statoOccIniziale.setLimiteReddito(
															limiteReddito.get(LimiteReddito.DIPENDENTE));
												}
											}
										}
									} else {
										if (statoOccIniziale.getLimiteReddito() < limiteReddito
												.get(LimiteReddito.DIPENDENTE)) {
											statoOccIniziale
													.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
										}
									}
									break;
								}
							} else {
								switch (Contratto.getTipoContratto(movimento)) {
								case Contratto.AUTONOMO:
								case Contratto.COCOCO:
									// controllare se nella data inizio movimento ci sono
									// rapporti di lavoro dipendente aperti. In tal caso bisogna considerare
									// limite reddito di lavoro dipendente
									boolean esisteMovimentoDip = false;
									if (movimenti.size() > 0) {
										esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(
												DateUtils.giornoSuccessivo(dataRif), movimenti);
									}
									if (esisteMovimentoDip) {
										if (statoOccIniziale.getLimiteReddito() < limiteReddito
												.get(LimiteReddito.DIPENDENTE)) {
											statoOccIniziale
													.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
										}
									} else {
										if (statoOccIniziale.getLimiteReddito() < limiteReddito
												.get(LimiteReddito.AUTONOMO)) {
											statoOccIniziale
													.setLimiteReddito(limiteReddito.get(LimiteReddito.AUTONOMO));
										}
									}
									break;

								case Contratto.DIP_TD:
								case Contratto.DIP_TI:
									if (statoOccIniziale.getLimiteReddito() < limiteReddito
											.get(LimiteReddito.DIPENDENTE)) {
										statoOccIniziale.setLimiteReddito(limiteReddito.get(LimiteReddito.DIPENDENTE));
									}
									break;
								}
							}
						}

						if (movimentoDurata.getTipoMovimento() == MovimentoBean.ASSUNZIONE
								&& movimentoDurata.getAttribute("MOVIMENTI_PROROGATI") != null) {
							Vector prec = (Vector) movimentoDurata.getAttribute("MOVIMENTI_PROROGATI");
							SourceBean movimentoAvv = null;
							SourceBean movimentoSucc = null;
							String dataInizioPrec = "";
							String dataInizioSucc = "";
							String dataFinePrec = "";
							BigDecimal retribuzionePrec = null;
							int ggDiLavoroPrec = 0;
							int precSize = prec.size();

							for (int k = 0; k < precSize; k++) {
								bCalcolaGiorni = false;
								movimentoAvv = (SourceBean) prec.get(k);
								int kSucc = k + 1;
								dataInizioPrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
								dataFinePrec = movimentoAvv.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
										? movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
										: null;

								// Se il movimento successivo nel vettore dei movimenti prorogati ha la stessa data
								// inizio
								// del movimento corrente nel vettore dei prorogati, allora al fine del reddito non lo
								// considero.
								// Tale situazone può succedere per le trasformazioni
								if (kSucc < precSize) {
									movimentoSucc = (SourceBean) prec.get(kSucc);
									dataInizioSucc = movimentoSucc.getAttribute(MovimentoBean.DB_DATA_INIZIO)
											.toString();
									if (DateUtils.compare(dataInizioPrec, dataInizioSucc) == 0) {
										continue;
									}
								}

								if (DateUtils.compare(dataInizioPrec, dataRif) <= 0 && (dataFinePrec == null
										|| dataFinePrec.equals("") || DateUtils.compare(dataFinePrec, dataRif) >= 0)) {
									dataInizioPrec = dataRif;
									// devo prendere i giorni anno successivo nel caso di iscrizione alla lista mobilità
									bCalcolaGiorni = true;
									movimentoAvv.updAttribute(MovimentoBean.DB_DATA_INIZIO, dataRif);
								} else {
									if (DateUtils.compare(dataInizioPrec, dataRif) > 0
											&& DateUtils.getAnno(dataInizioPrec) == annoDataRif) {
										bCalcolaGiorni = true;
									}
								}

								// prendo la retribuzione
								retribuzionePrec = Retribuzione.getRetribuzioneMen(movimentoAvv);
								if (retribuzionePrec != null) {
									if (bCalcolaGiorni) {
										ggDiLavoroPrec = ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec,
												dataFinePrec, dataInizioPrec);
									} else {
										ggDiLavoroPrec = 0;
									}
									nuovoStatoOccupazionale.aggiorna(ggDiLavoroPrec, retribuzionePrec.doubleValue());

									if (Contratto.getTipoContratto(movimentoAvv) == Contratto.DIP_TI) {
										statoOccupazionaleMovTI.aggiorna(ggDiLavoroPrec,
												retribuzionePrec.doubleValue());
									}
								} else {
									if (bCalcolaGiorni) {
										if (!appCategoriaParticolareCurr) {
											noReddito = true;
										} else {
											noRedditoCurr = true;
										}
									}
								}
							} // end for (int k = 0; k < precSize; k++)

						} else {
							bCalcolaGiorni = false;
							dataFineMov = (String) movimentoDurata.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
							if (dataFineMov == null || dataFineMov.equals("")) {
								dataFineMov = "31/12/" + annoDataRif;
							}
							if (DateUtils.getAnno(dataInizioMovDurata) <= annoDataRif) {
								bCalcolaGiorni = true;
								if (DateUtils.compare(dataInizioMovDurata, dataRif) < 0) {
									dataInizioMovDurata = dataRif;
								}
								ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioMovDurata, dataFineMov,
										dataInizioMovDurata);
							} else {
								ggDiLavoro = 0;
							}
							BigDecimal retribuzione = Retribuzione.getRetribuzioneMen(movimentoDurata);
							if (retribuzione == null && bCalcolaGiorni) {
								if (!appCategoriaParticolareCurr) {
									noReddito = true;
								} else {
									noRedditoCurr = true;
								}
							} else {
								if (bCalcolaGiorni) {
									nuovoStatoOccupazionale.aggiorna(ggDiLavoro, retribuzione.doubleValue());
									if (Contratto.getTipoContratto(movimentoDurata) == Contratto.DIP_TI) {
										statoOccupazionaleMovTI.aggiorna(ggDiLavoro, retribuzione.doubleValue());
									}
								}
							}
						}
					}
					superatoAnnoPrimo = nuovoStatoOccupazionale.getReddito() > statoOccIniziale.getLimiteReddito();

					if (gestione150 && !iscrittoCM) {
						if (!superatoAnnoPrimo && !noReddito && !noRedditoCurr && movInMobilita) {
							nuovoStatoOccupazionale.setStatoOccupazionale("A212");
						} else {
							if (movInMobilita || appCategoriaParticolare) {
								nuovoStatoOccupazionale.setStatoOccupazionale("B1");
							} else {
								nuovoStatoOccupazionale.setStatoOccupazionale("B");
							}
						}
					} else {
						if (superatoAnnoPrimo || noReddito || noRedditoCurr) {
							if (noReddito) {
								// noReddito = true quando ho trovato un movimento che non è categoria particolare
								// (non appartiene a CategoriaParticolare (quindi decade a meno che non esiste un
								// periodo di mobilità)
								if (gestioneFornero && movInMobilita) {
									nuovoStatoOccupazionale.setStatoOccupazionale("B1");
								} else {
									nuovoStatoOccupazionale.setStatoOccupazionale("B");
								}
							} else {
								if (superatoAnnoPrimo) {
									// superatoAnnoPrimo = true
									if (trovatoMovTIDecadenza) {
										boolean superatoTI = statoOccupazionaleMovTI.getReddito() > statoOccIniziale
												.getLimiteReddito();
										if (superatoTI) {
											if (gestioneFornero && movInMobilita) {
												nuovoStatoOccupazionale.setStatoOccupazionale("B1");
											} else {
												nuovoStatoOccupazionale.setStatoOccupazionale("B");
											}
										} else {
											if (nPosMovTIDecadenza > nPosMovTD) {
												if (gestioneFornero && movInMobilita) {
													nuovoStatoOccupazionale.setStatoOccupazionale("B1");
												} else {
													nuovoStatoOccupazionale.setStatoOccupazionale("B");
												}
											} else {
												if (appCategoriaParticolareTD) {
													// reddito alto ma appartenenza alla categoria particolare
													nuovoStatoOccupazionale.setStatoOccupazionale("B1");
												} else {
													if (gestioneFornero && movInMobilita) {
														nuovoStatoOccupazionale.setStatoOccupazionale("B1");
													} else {
														nuovoStatoOccupazionale.setStatoOccupazionale("B");
													}
												}
											}
										}
									} else {
										if (appCategoriaParticolare) {
											// reddito alto ma appartenenza alla categoria particolare
											nuovoStatoOccupazionale.setStatoOccupazionale("B1");
										} else {
											if (gestioneFornero && movInMobilita) {
												nuovoStatoOccupazionale.setStatoOccupazionale("B1");
											} else {
												nuovoStatoOccupazionale.setStatoOccupazionale("B");
											}
										}
									}
								} else {
									// noRedditoCurr = true allora trovato movimento con reddito null e se arrivo a
									// questo
									// punto significa che noReddito = false, superatoAnnoPrimo = false e tutti i
									// movimenti esaminati a cavallo dell'anno
									// appartengono alla categoria particolare (num mesi <= 6 e flgSospensione = SI)
									nuovoStatoOccupazionale.setStatoOccupazionale("B1");
								}
							}
						} else {
							nuovoStatoOccupazionale.setStatoOccupazionale("A212");
						}
					}
				}

				break;

			default:
				nuovoStatoOccupazionale = statoOccIniziale;
				break;
			}
			break;

		case StatoOccupazionaleBean.RAGG_I:
			nuovoStatoOccupazionale = statoOccIniziale;
			break;

		case StatoOccupazionaleBean.RAGG_A:
			nuovoStatoOccupazionale = statoOccIniziale;
			break;

		case StatoOccupazionaleBean.RAGG_O:
			nuovoStatoOccupazionale = statoOccIniziale;
			break;
		}
		return nuovoStatoOccupazionale;
	}

	/**
	 * determino quali sono i movimenti da considerare nella determinazione del nuovo stato occupazionale quando si
	 * incontra la chiusura della mobilità
	 * 
	 * @param chiusuraMobilita
	 * @param dataChiusuraMobilita
	 * @return
	 * @throws Exception
	 */
	public Vector movimentiImpattantiFineMobilita(ChiusuraMobilitaBean chiusuraMobilita, String dataInizioMobilita,
			String dataChiusuraMobilita) throws Exception {
		String dataInizioMov = "";
		String dataFineMov = "";
		EventoAmministrativo objEvento = null;
		MovimentoBean eventoApp = null;
		MovimentoBean mov = null;
		Vector movimentiImpattiMobilita = new Vector();
		MovimentoBean movApp = null;
		int iCont = 0;
		SourceBean didAperta = null;
		int annoInizioMob = DateUtils.getAnno(dataInizioMobilita);
		boolean bInseritoAvv = false;
		String codTipoAvviamento = "";
		int movimentiSize = movimenti.size();
		for (; iCont < movimentiSize; iCont++) {
			bInseritoAvv = false;
			objEvento = (EventoAmministrativo) movimenti.get(iCont);
			int tipoEvento = objEvento.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.PROROGA
					|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
				movApp = (MovimentoBean) objEvento;
				codTipoAvviamento = movApp.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
						? movApp.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
						: "";
				// non considerare TIPO AVVIAMENTO Z.09.02(codice vecchio RS3)(cessazione attività lavorativa dopo un
				// periodo
				// di sospeso per contrazione) e i tirocini che sono considerati prima di questo metodo
				if ((!codTipoAvviamento.equals("Z.09.02"))
						&& (!movApp.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
								|| !movApp.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString().equals("T"))) {
					dataInizioMov = movApp.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
					dataFineMov = movApp.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
							? movApp.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
							: "";
					MovimentoBean movAvviamento = null;
					if (tipoEvento == EventoAmministrativo.PROROGA
							|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
						Vector vettoreMovimenti = estraiMovimentiDaEventi(movimenti);
						SourceBean sbAvviamento = getAvviamentoStart(movApp, vettoreMovimenti);
						movAvviamento = new MovimentoBean(sbAvviamento);
						if (movAvviamento != null) {
							bInseritoAvv = cercaAvviamento(movAvviamento.getPrgMovimento(), movimentiImpattiMobilita);
						}
					} else {
						if (movApp.getAttribute("MOVIMENTI_PROROGATI") != null) {
							Vector prec = (Vector) movApp.getAttribute("MOVIMENTI_PROROGATI");
							if (prec.size() > 0) {
								SourceBean movimentoUltimo = (SourceBean) prec.get(prec.size() - 1);
								dataFineMov = (String) movimentoUltimo
										.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
							}
						}
					}

					if (DateUtils.compare(dataChiusuraMobilita, dataInizioMov) == 0
							&& (tipoEvento == EventoAmministrativo.PROROGA
									|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE)) {
						if (movAvviamento != null && !bInseritoAvv) {
							movimentiImpattiMobilita.add(movAvviamento);
						}
					} else {
						if ((DateUtils.compare(dataInizioMov, dataChiusuraMobilita) < 0)
								&& (DateUtils.compare(dataInizioMov, dataInizioMobilita) >= 0)
								&& (dataFineMov == null || dataFineMov.equals("")
										|| DateUtils.compare(dataFineMov, dataChiusuraMobilita) >= 0)) {
							if (tipoEvento == EventoAmministrativo.PROROGA
									|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
								if (movAvviamento != null && !bInseritoAvv) {
									movimentiImpattiMobilita.add(movAvviamento);
								}
							} else {
								movimentiImpattiMobilita.add(movApp);
							}
						} else {
							if (DateUtils.compare(dataInizioMov, dataInizioMobilita) < 0) {
								if ((dataFineMov != null && !dataFineMov.equals("")
										&& DateUtils.getAnno(dataFineMov) >= DateUtils.getAnno(dataChiusuraMobilita))
										|| (dataFineMov == null || dataFineMov.equals(""))) {

									if (tipoEvento == EventoAmministrativo.PROROGA
											|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
										if (movAvviamento != null && !bInseritoAvv) {
											movimentiImpattiMobilita.add(movAvviamento);
										}
									} else {
										movimentiImpattiMobilita.add(movApp);
									}
								}
							}
						}
					}
				}
			}
		}
		return movimentiImpattiMobilita;
	}

	/**
	 * 
	 * @param movimenti
	 * @return
	 */
	private Vector estraiMovimentiDaEventi(List movimenti) {
		Vector vettoreMovimenti = new Vector();
		EventoAmministrativo objEvento = null;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			objEvento = (EventoAmministrativo) movimenti.get(i);
			int tipoEvento = objEvento.getTipoEventoAmministrativo();
			if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.PROROGA
					|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
				vettoreMovimenti.add(objEvento);
			}
		}
		return vettoreMovimenti;
	}

	/**
	 * 
	 * @param prg
	 * @param movimenti
	 * @return
	 */
	private boolean cercaAvviamento(BigDecimal prg, List movimenti) {
		boolean trovato = false;
		MovimentoBean movimento = null;
		BigDecimal prgMov = null;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			movimento = (MovimentoBean) movimenti.get(i);
			prgMov = movimento.getPrgMovimento();
			if (prgMov != null && prg != null && prgMov.equals(prg)) {
				trovato = true;
				break;
			}
		}
		return trovato;
	}

	/**
	 * 
	 * @param mov
	 * @param movimenti
	 * @return
	 * @throws Exception
	 */
	public SourceBean getAvviamentoStart(SourceBean mov, Vector movimenti) throws Exception {
		SourceBean sbRitorno = mov;
		SourceBean sbApp = null;
		BigDecimal prgMovimentoPrec = (BigDecimal) mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		BigDecimal prgMovimentoApp = null;
		boolean trovato = false;
		int movimentiSize = movimenti.size();
		while (prgMovimentoPrec != null) {
			trovato = false;
			for (int k = 0; k < movimentiSize; k++) {
				sbApp = (SourceBean) movimenti.get(k);
				prgMovimentoApp = (BigDecimal) sbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
				if (prgMovimentoApp.equals(prgMovimentoPrec)) {
					prgMovimentoPrec = (BigDecimal) sbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
					trovato = true;
					sbRitorno = sbApp;
					break;
				}
			}
			if (!trovato) {
				prgMovimentoPrec = null;
			}
		}
		return sbRitorno;
	}

	/**
	 * Metodo usato per il controllo degli errori sull'ordinamento dei movimenti Stampa su un file di log gli eventuali
	 * movimenti inseriti non correttamente nella lista dei movimenti
	 * 
	 * @param movimenti
	 *            da controllare
	 * @author Togna Cosimo
	 */
	private void logErroriMovimenti(List movimenti) {
		String data = "";
		SourceBean request = getRequestContainer().getServiceRequest();
		if (request != null && request.getAttribute("DATABATCH2") != null) {
			data = (String) request.getAttribute("DATABATCH2");
		} else {
			data = (new SimpleDateFormat("dd-MM-yyyy")).format(new java.util.Date());
		}
		// istanzio il file di log
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "ErroriMovimenti";

		nomeFile = nomeFile + data + ".log";

		LogBatch logBatch = new LogBatch(nomeFile, dir);

		// stampo sul log il cdn lavoratore analizzato
		if (movimenti.size() != 0) {
			String cdnLavoratore = ((SourceBean) movimenti.get(0)).getAttribute(MovimentoBean.DB_CDNLAVORATORE)
					.toString();
			logBatch.writeLog("=================== LAVORATORE ANALIZZATO CON IDENTIFICATIVO " + cdnLavoratore
					+ "===================");
		}

		// controllo se i movimenti sono stati inseriti correttamente
		for (int i = 0; i < movimenti.size(); i++) {

			// recupero prgStatoOccupazionale del Movimento A
			SourceBean movimentoA = (SourceBean) movimenti.get(i);
			BigDecimal prgStatoOccupazA = null;
			if (movimentoA.getAttribute("prgStatoOccupaz") != null) {
				prgStatoOccupazA = (BigDecimal) movimentoA.getAttribute("prgStatoOccupaz");
			}

			for (int j = i + 1; j < movimenti.size(); j++) {
				// recupero prgStatoOccupazionale del Movimento B
				SourceBean movimentoB = (SourceBean) movimenti.get(j);
				BigDecimal prgStatoOccupazB = null;
				if (movimentoB.getAttribute("prgStatoOccupaz") != null) {
					prgStatoOccupazB = (BigDecimal) movimentoB.getAttribute("prgStatoOccupaz");
				}

				// confronto se il prgMovimentoA è > del prgMovimentoB
				// nel qual caso c'è errore
				if (prgStatoOccupazA != null && prgStatoOccupazB != null) {
					if (prgStatoOccupazA.compareTo(prgStatoOccupazB) == 1) {
						// :::::::::::ERRORE:::::::::::::
						logBatch.writeLog("=================== ERRORE ===================");
						logBatch.writeLog("IL Movimento con prgStatoOccupaz    " + prgStatoOccupazA);
						logBatch.writeLog("è inserito prima del");
						logBatch.writeLog("Movimento con prgStatoOccupaz    " + prgStatoOccupazB);
					}
				}
			}

		}
	}

	public Vector getStatiOccFinali() {
		return statiOccupazionaliFinali;
	}

	public void chiudiDidInVettoreDids(BigDecimal prg, String dataChiusura) throws Exception {
		DidBean did = null;
		BigDecimal prgdichdispo = null;
		for (int i = 0; i < dids.size(); i++) {
			did = (DidBean) dids.get(i);
			prgdichdispo = (BigDecimal) did.getPrgDichDisponibilita();
			if (prgdichdispo.equals(prg)) {
				if (did.containsAttribute(DidBean.DB_DAT_FINE)) {
					did.updAttribute(DidBean.DB_DAT_FINE, dataChiusura);
				} else {
					did.setAttribute(DidBean.DB_DAT_FINE, dataChiusura);
				}
				break;
			}
		}
	}

	public void apriDidInVettoreDids(BigDecimal prg) throws Exception {
		DidBean did = null;
		BigDecimal prgdichdispo = null;
		for (int i = 0; i < dids.size(); i++) {
			did = (DidBean) dids.get(i);
			prgdichdispo = (BigDecimal) did.getPrgDichDisponibilita();
			if (prgdichdispo.equals(prg)) {
				if (did.containsAttribute(DidBean.DB_DAT_FINE)) {
					did.delAttribute(DidBean.DB_DAT_FINE);
					break;
				}
			}
		}
	}

	/**
	 * Controlla che i movimenti in data futura del lavoratore non abbiano un prgStatoOccupazionale nel qual caso è
	 * presente un errore
	 * 
	 * @param movimenti
	 *            Movimenti da controllare
	 * @throws Exception
	 *             se un movimento in data futura ha il prgStatoOccupazionale non nullo
	 */
	private void checkMovimentiFuturi(java.util.Collection movimenti, Object cdnLavoratore) throws Exception {
		for (java.util.Iterator iterator = movimenti.iterator(); iterator.hasNext();) {
			EventoAmministrativo eventoCorrente = (EventoAmministrativo) iterator.next();
			String dataInizioEvento = (String) eventoCorrente.getDataInizio();
			String dataAttuale = DateUtils.getNow();

			// Controllo che il movimento è un movimento futuro
			if (DateUtils.compare(dataInizioEvento, dataAttuale) == 1) {
				Object prgStatoOccupazionale = eventoCorrente.getStatoOccupazionale();
				// Controllo che il prgStatoOccupazionale sia != null
				if (prgStatoOccupazionale != null) {
					_logger.fatal("Impossibile proseguire poiché per il lavoratore con progressivo " + cdnLavoratore
							+ " è stato trovato un evento amminitrativo \"" + eventoCorrente.getDescrizione()
							+ "\" futuro con data inizio " + eventoCorrente.getDataInizio() + " e data fine "
							+ eventoCorrente.getDataFine() + " con StatoOccupazionale non nullo");

					// throw new
					// ControlliException(MessageCodes.StatoOccupazionale.MOV_FUTURO_CON_STATO_OCCUPAZ_NON_NULLO);
					throw new Exception("Movimento in data futura del lavoratore ha il prgStatoOccupazionale != null");
				}
			}
		}
	}

	private Vector togliEventiAnnullati(Vector vect) {
		Vector v = new Vector();
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (tmp.containsAttribute("CODSTATOATTO") && (tmp.getAttribute("CODSTATOATTO").equals("PR")
					|| tmp.getAttribute("CODSTATOATTO").equals("PA"))) {
				v.add(tmp);
			}
		} // end for
		return v;
	}

	private String cercaDataInizioMobilita(List listaMobilita, ChiusuraMobilitaBean chiusuraMobilita) {
		String dataInizioMobilita = "";
		BigDecimal prgChiusuraMob = chiusuraMobilita.getPrgMobilitaIscr();
		BigDecimal prgMobilita = null;
		SourceBean sbMobilita = null;
		for (int index = 0; index < listaMobilita.size(); index++) {
			sbMobilita = (SourceBean) listaMobilita.get(index);
			prgMobilita = new BigDecimal(sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR).toString());
			if (prgMobilita.equals(prgChiusuraMob)) {
				dataInizioMobilita = sbMobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO).toString();
				break;
			}
		}
		return dataInizioMobilita;
	}

	private void apriIscrizioneCM(StatoOccupazionaleBean statoOccCorrente, StatoOccupazionaleManager2 statoOccManager,
			Object cdnLavoratore) {
		try {
			BigDecimal cdnUser = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute("_CDUT_");
			if (statoOccCorrente != null) {
				String dataInizioSoIniziale = statoOccCorrente.getDataInizio();
				if (Sottosistema.CM.isOn()) {
					String codStatoOccupazRaggIniziale = statoOccCorrente.getStatoOccupazRagg();
					if ((codStatoOccupazRaggIniziale != null) && (codStatoOccupazRaggIniziale
							.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_D)
							|| codStatoOccupazRaggIniziale.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_I))) {
						LavoratoreBean lavCMRiaprire = new LavoratoreBean("CM", cdnLavoratore, txExecutor);
						List iscrCMRiaprire = lavCMRiaprire.getCM();
						if (iscrCMRiaprire.size() > 0) {
							Vector vettCMDaRiaprire = CmBean.getRiaperturaCM(iscrCMRiaprire, dataInizioSoIniziale);
							int vettCMSize = vettCMDaRiaprire.size();
							if (vettCMSize > 0) {
								for (int k = 0; k < vettCMSize; k++) {
									Object prgIscr = vettCMDaRiaprire.get(k);
									CmBean.riapriIscrizioni(prgIscr, txExecutor);
								}
								// Sono state fatte delle riaperture per quel lavoratore, e quindi bisogna
								// settare TS_CONGIF_TAB.flgAbilita='S' sul record avente cdnconfigtab=cdnlavoratore
								CmBean.aggiornaConfigTab(cdnLavoratore, "S", txExecutor);
								CmBean.aggiornaSchedaIscrLavCM(cdnLavoratore.toString(), cdnUser, txExecutor);
								Vector vettCMAll = DBLoad.getAllDisabiliCollocamentoMirato(cdnLavoratore, txExecutor);
								if (vettCMAll != null && vettCMAll.size() > 0) {
									// prendo il primo della lista che corrisponde all'ultima iscrizione inserita
									SourceBean rowCM = (SourceBean) vettCMAll.get(0);
									rowCM = rowCM.containsAttribute("ROW") ? (SourceBean) rowCM.getAttribute("ROW")
											: rowCM;
									cm = new CmBean(rowCM);
									this.setCm(cm);
									setListaDisabiliCM(vettCMAll);
									if (statoOccManager != null) {
										statoOccManager.setListaDisabiliCM(getListaDisabiliCM());
									}
								} else {
									cm = new CmBean(new SourceBean("ROWS"));
									this.setCm(cm);
								}
								if (statoOccManager != null) {
									statoOccManager.setCm(cm);
								}
							}
						}
					}
				}
			}
		} catch (Throwable eCM) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"SituazioneAmministrativa" + ":execute() :errore riapertura iscrizione cm", eCM);
		}
	}

	private void chiudiIscrizioneCM(StatoOccupazionaleBean statoOccCorrente, StatoOccupazionaleManager2 statoOccManager,
			Object cdnLavoratore) {
		try {
			BigDecimal cdnUser = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute("_CDUT_");
			if (statoOccCorrente != null) {
				String dataInizioSoCorrente = statoOccCorrente.getDataInizio();
				if (Sottosistema.CM.isOn()) {
					String codStatoOccupazRaggIniziale = statoOccCorrente.getStatoOccupazRagg();
					if ((codStatoOccupazRaggIniziale != null) && (codStatoOccupazRaggIniziale
							.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_A)
							|| codStatoOccupazRaggIniziale.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_O))) {

						LavoratoreBean lavCMChiudere = new LavoratoreBean("CM", cdnLavoratore, txExecutor);
						List iscrCMChiudere = lavCMChiudere.getCM();

						Vector vettCMAperte = new Vector();
						Vector vettCM = CmBean.getChiusuraCM(iscrCMChiudere, vettCMAperte);
						int vettCMSize = vettCM.size();
						if (vettCMSize > 0) {
							for (int k = 0; k < vettCMSize; k++) {
								Object prgIscr = vettCM.get(k);
								CmBean.chiudiIscrizioni(prgIscr, DateUtils.giornoPrecedente(dataInizioSoCorrente),
										txExecutor);
							}
							if (vettCMAperte.size() == 0) {
								// Il lavoratore non ha più un record protocollato aperto CM, e in questo caso
								// devo settare TS_CONGIF_TAB.flgAbilita='N' sul record avente
								// cdnconfigtab=cdnlavoratore
								CmBean.aggiornaConfigTab(cdnLavoratore, "N", txExecutor);
							}
							CmBean.aggiornaSchedaIscrLavCM(cdnLavoratore.toString(), cdnUser, txExecutor);
							Vector vettCMAll = DBLoad.getAllDisabiliCollocamentoMirato(cdnLavoratore, txExecutor);
							if (vettCMAll != null && vettCMAll.size() > 0) {
								// prendo il primo della lista che corrisponde all'ultima iscrizione inserita
								SourceBean rowCM = (SourceBean) vettCMAll.get(0);
								rowCM = rowCM.containsAttribute("ROW") ? (SourceBean) rowCM.getAttribute("ROW") : rowCM;
								cm = new CmBean(rowCM);
								this.setCm(cm);
								setListaDisabiliCM(vettCMAll);
								if (statoOccManager != null) {
									statoOccManager.setListaDisabiliCM(getListaDisabiliCM());
								}
							} else {
								cm = new CmBean(new SourceBean("ROWS"));
								this.setCm(cm);
							}
							if (statoOccManager != null) {
								statoOccManager.setCm(cm);
							}
						}
					}
				}
			}
		} catch (Throwable eCM) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"SituazioneAmministrativa" + ":execute() :errore riapertura iscrizione cm", eCM);
		}
	}

	public void setTipoCongif_MOV_C03(String val) {
		this.tipoCongif_MOV_C03 = val;
	}

	public String getTipoCongif_MOV_C03() {
		return this.tipoCongif_MOV_C03;
	}

	public void setDataPrec297(String val) {
		this.dataPrec297 = val;
	}

	public String getDataPrec297() {
		return this.dataPrec297;
	}

	public void setAnnoPrec297(int val) {
		this.annoPrec297 = val;
	}

	public int getAnnoPrec297() {
		return this.annoPrec297;
	}

	public void setStatiOccupazionaliManuali(StatoOccupazionaleBean sOcc, int indice) {
		this.statiOccupazionaliManuali.add(indice, sOcc);
	}

	public StatoOccupazionaleBean getStatoOccupazionaliManuali(int indice) {
		return this.statiOccupazionaliManuali.get(indice);
	}

	public void setStatiOccupazionaliManuali(List<StatoOccupazionaleBean> sOccManuali) {
		this.statiOccupazionaliManuali = sOccManuali;
	}

	public List<StatoOccupazionaleBean> getStatiOccupazionaliManuali() {
		return this.statiOccupazionaliManuali;
	}

	public void setDataEventoAmministrativo(String dataRif) {
		this.dataEventoAmministrativo = dataRif;
	}

	public String getDataEventoAmministrativo() {
		return this.dataEventoAmministrativo;
	}

	public void setData150(String dataRif) {
		this.data150 = dataRif;
	}

	public String getData150() {
		return this.data150;
	}

}
