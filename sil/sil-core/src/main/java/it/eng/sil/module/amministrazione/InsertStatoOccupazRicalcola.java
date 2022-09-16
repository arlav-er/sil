package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.utils.MessageBundle;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.util.amministrazione.impatti.ChiusuraDidBean;
import it.eng.sil.util.amministrazione.impatti.ChiusuraMobilitaBean;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.DidBean;
import it.eng.sil.util.amministrazione.impatti.EventoAmministrativo;
import it.eng.sil.util.amministrazione.impatti.ListaMobilita;
import it.eng.sil.util.amministrazione.impatti.MobilitaBean;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.PattoBean;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativa;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBase;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class InsertStatoOccupazRicalcola extends it.eng.sil.module.AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InsertStatoOccupazRicalcola.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		enableSimpleQuery(true);
		Object res = null;
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		RequestContainer requestContainer = RequestContainer.getRequestContainer();
		BigDecimal userID = (BigDecimal) requestContainer.getSessionContainer().getAttribute("_CDUT_");
		String data297 = null;
		String messaggioFail297 = null;
		try {
			// forzatura mobilità e/o did non stipulabile
			if (!request.containsAttribute("FORZA_INSERIMENTO")) {
				request.setAttribute("FORZA_INSERIMENTO", "true");
			} else {
				request.updAttribute("FORZA_INSERIMENTO", "true");
			}

			request.setAttribute("INS_SOCC_MANUALE_RICALCOLA", "true");

			UtilsConfig utility = new UtilsConfig("AM_297");
			data297 = utility.getValoreConfigurazione();

			SourceBean sbStatiOcc = null;
			SourceBean sbApp = null;
			String datInizioSOccApp = "";
			String datInizioSocc = request.containsAttribute("datInizio") ? request.getAttribute("datInizio").toString()
					: "";
			String cdnLavoratore = request.getAttribute("cdnlavoratore").toString();
			if (!datInizioSocc.equals("") && DateUtils.compare(datInizioSocc, data297) < 0) {
				if (request.containsAttribute("CONTINUA_CALCOLO_SOCC_PREC_297") && request
						.getAttribute("CONTINUA_CALCOLO_SOCC_PREC_297").toString().equalsIgnoreCase("false")) {
					Vector v = new Vector(1);
					v.add(data297);
					messaggioFail297 = MessageBundle
							.getMessage(MessageCodes.StatoOccupazionale.INSERT_CALCOLA_STATO_OCC_PREC_NORMATIVA_297, v);
					setMessageIdFail(MessageCodes.StatoOccupazionale.STATO_OCC_PREC_DATA_NORMATIVA_297);
					throw new ControlliException(MessageCodes.StatoOccupazionale.STATO_OCC_PREC_DATA_NORMATIVA_297);
				}
			}
			Vector movimentiAperti = null;
			Vector dids = null;
			Vector patti = null;
			Vector mobilita = null;
			BigDecimal prgStatoOccupaz = null;
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();
			String nuovoStatoOccupaz = "";
			String codMotivoFineAtto = null;
			nuovoStatoOccupaz = request.containsAttribute("nuovoRaggStatoOccupaz")
					? request.getAttribute("nuovoRaggStatoOccupaz").toString()
					: "";
			if (nuovoStatoOccupaz.equals("A") || nuovoStatoOccupaz.equals("O")) {
				// bisogna chiudere la did e il patto aperti
				String codStatoOccupaz = (String) request.getAttribute("codStatoOcc");
				SourceBean deStatoOccupaz = DBLoad.getDeStatoOccupaz(request, codStatoOccupaz, transExec);
				codMotivoFineAtto = (String) deStatoOccupaz.getAttribute("codMotivoFineAtto");
				if (codMotivoFineAtto == null) {
					codMotivoFineAtto = "AV";
				}
				if (request.containsAttribute("codMotivoFineAtto")) {
					request.updAttribute("codMotivoFineAtto", codMotivoFineAtto);
				} else {
					request.setAttribute("codMotivoFineAtto", codMotivoFineAtto);
				}
			}
			movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
			movimentiAperti = Controlli.togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = Controlli.togliMovimentoInDataFutura(movimentiAperti);
			dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", transExec);
			patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
			ListaMobilita listaMobilita = new ListaMobilita(cdnLavoratore, transExec);
			mobilita = listaMobilita.getMobilita();
			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
			SituazioneAmministrativa sitAmmTemp = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali,
					patti, dids, datInizioSocc, transExec, requestContainer);

			Vector listaStatiOccDaCancellare = new Vector();
			Vector statiOccupazionaliNew = new Vector();
			// determino gli stati occupazionali da cancellare
			for (int i = 0; i < statiOccupazionali.size(); i++) {
				sbApp = (SourceBean) statiOccupazionali.get(i);
				datInizioSOccApp = sbApp.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString();
				if (DateUtils.compare(datInizioSOccApp, datInizioSocc) >= 0) {
					listaStatiOccDaCancellare.add(sbApp);
				} else {
					statiOccupazionaliNew.add(sbApp);
				}
			}
			String dataAnzianitaDisoc = "";
			String dataInizioSOcc = "";
			// cancello gli stati occupazionali nella lista
			// listaStatiOccDaCancellare
			// e controllo se ci sono tra questi stati occupazionali gestiti
			// manualmente
			int indiceSOccManuale = 0;
			for (int i = 0; i < listaStatiOccDaCancellare.size(); i++) {
				sbStatiOcc = (SourceBean) listaStatiOccDaCancellare.get(i);
				StatoOccupazionaleBean sOcc = new StatoOccupazionaleBean(sbStatiOcc);
				// controllo stato occupazionale gestito manualmente
				if (sOcc.getCodMonoProvenienza().equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_MANUALE)
						|| sOcc.getCodMonoProvenienza().equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_AGG_MANUALE)
						|| sOcc.getCodMonoProvenienza()
								.equalsIgnoreCase(StatoOccupazionaleBase.STATO_OCC_REG_AGG_MANUALE)) {
					if (!requestContainer.getServiceRequest()
							.containsAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO")) {
						requestContainer.getServiceRequest().setAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO",
								"true");
					}
					sitAmmTemp.setStatiOccupazionaliManuali(sOcc, indiceSOccManuale);
					indiceSOccManuale = indiceSOccManuale + 1;
				}

				// controllo mesi anziantità e/o sospensione non calcolati dal
				// SIL
				if (!requestContainer.getServiceRequest().containsAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO")
						&& !requestContainer.getServiceRequest()
								.containsAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI")) {
					if ((!sOcc.getNumAnzianitaPrec297().equals("") && !sOcc.getNumAnzianitaPrec297().equals("0"))
							|| (!sOcc.getNumMesiSosp().equals("") && !sOcc.getNumMesiSosp().equals("0"))) {
						requestContainer.getServiceRequest().setAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI",
								"true");
					}
				}

				// controllo data anziantità stato occupazionale rispetto alla
				// did/mobilità valida
				if (!requestContainer.getServiceRequest().containsAttribute("ESISTENZA_STATO_OCC_MANUALE_IN_RICALCOLO")
						&& !requestContainer.getServiceRequest()
								.containsAttribute("STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI")
						&& !requestContainer.getServiceRequest()
								.containsAttribute("INCONGRUENZA_DATA_ANZIANITA_STATO_OCC")) {
					dataAnzianitaDisoc = sOcc.getDataAnzianita();
					dataInizioSOcc = sOcc.getDataInizio();
					if (!dataAnzianitaDisoc.equals("")) {
						if (!Controlli.isCorrettaDataAnzianita(sitAmmTemp.getDids(), sitAmmTemp.getListaMobilita(),
								dataInizioSOcc, dataAnzianitaDisoc)) {
							requestContainer.getServiceRequest().setAttribute("INCONGRUENZA_DATA_ANZIANITA_STATO_OCC",
									"true");
						}
					}
				}

				prgStatoOccupaz = (BigDecimal) sbStatiOcc.getAttribute("prgStatoOccupaz");
				dereferenziaEventiAmmimistrativi(movimentiAperti, dids, patti, mobilita, prgStatoOccupaz,
						requestContainer, transExec);
				DBStore.cancellaStatoOccupazionale(prgStatoOccupaz, transExec);
			}
			// riapro (se chiuso) l'ultimo stato occupazionale aperto
			if (statiOccupazionaliNew.size() > 0) {
				sbApp = (SourceBean) statiOccupazionaliNew.get(statiOccupazionaliNew.size() - 1);
				if (sbApp.containsAttribute(StatoOccupazionaleBean.DB_DAT_FINE)) {
					StatoOccupazionaleBean statoOccDaAggiornare = new StatoOccupazionaleBean(sbApp);
					DBStore.apriStatoOcc(statoOccDaAggiornare, requestContainer, transExec);
				}
			}
			// inserisco il nuovo stato occupazionale
			setSectionQueryInsert("QUERY_INSERT");
			res = doInsert(request, response, true);
			statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
			StatoOccupazionaleBean statoOccIniziale = new StatoOccupazionaleBean(
					(SourceBean) statiOccupazionali.get(statiOccupazionali.size() - 1));

			if (nuovoStatoOccupaz.equals("A") || nuovoStatoOccupaz.equals("O")) {
				SourceBean mobilitaAperta = Controlli.eventoInMobilita(mobilita, datInizioSocc);
				if (mobilitaAperta != null) {
					// chiusura iscrizione con motivo decadenza che non fa fare scorrimento e decadenza
					BigDecimal numkloMob = new BigDecimal(mobilitaAperta.getAttribute("NUMKLOMOBISCR").toString());
					BigDecimal prgMobilitaIscr = (BigDecimal) mobilitaAperta
							.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
					String dataDecadenza = DateUtils.giornoPrecedente(datInizioSocc);
					numkloMob = DBStore.aggiornaDataFineMobilita(prgMobilitaIscr, dataDecadenza,
							MobilitaBean.MOTIVO_DECADENZA_MOB_SOCC_MANUALE, userID, numkloMob, transExec);
					mobilitaAperta.updAttribute("NUMKLOMOBISCR", numkloMob);
				}
			}

			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti,
					dids, datInizioSocc, transExec, requestContainer);

			if (nuovoStatoOccupaz.equals("A") || nuovoStatoOccupaz.equals("O")) {
				DidBean did = (DidBean) sitAmm.cercaDid(datInizioSocc);
				SourceBean patto = sitAmm.cercaPatto(datInizioSocc);
				if (did != null) {
					// chiudo la did
					DBStore.chiudiDID(did, datInizioSocc, codMotivoFineAtto, requestContainer, transExec);
					BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
					sitAmm.aggiornaNumKloDichDispoInMovimenti(did, numKlo, sitAmm.getMovimenti());
				}
				if (patto != null) {
					// chiudo il patto
					DBStore.chiudiPatto297(patto, datInizioSocc, codMotivoFineAtto, requestContainer, transExec);
					sitAmm.aggiornaNumKloPatto(new BigDecimal(patto.getAttribute(PattoBean.PRG_PATTO_LAV).toString()),
							datInizioSocc, new BigDecimal(patto.getAttribute(PattoBean.NUMKLO_PATTO_LAV).toString()));
				}
			}

			sitAmm.setStatiOccupazionaliManuali(sitAmmTemp.getStatiOccupazionaliManuali());
			int indice = cercaIndice(statoOccIniziale, sitAmm.getMovimenti());
			StatoOccupazionaleBean statoOccFinale = sitAmm.ricrea(statoOccIniziale, indice);
			transExec.commitTransaction();
		}

		catch (ControlliException cEx) {
			if (transExec != null)
				transExec.rollBackTransaction();
			switch (cEx.getCode()) {
			case MessageCodes.StatoOccupazionale.STATO_OCC_PREC_DATA_NORMATIVA_297:
				addConfirm(request, response, cEx.getCode(), messaggioFail297);
				Vector paramV = new Vector(1);
				paramV.add(data297);
				reportOperation.reportFailure(
						MessageCodes.StatoOccupazionale.INSERT_CALCOLA_STATO_OCC_PREC_NORMATIVA_297,
						"InsertStatoOccupazRicalcola.service", "Data inizio stato occupazionale non valida", paramV);
				break;
			case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
			case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
			case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
			case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
				addConfirm(request, response, cEx.getCode(), null);
				reportOperation.reportFailure(cEx, "service()", "");
				break;
			default: // gestione errore generico
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in inserimento e ricalcolo stato occupazionale.",
						(Exception) cEx);

				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			}
		}

		catch (Exception e) {
			if (transExec != null)
				transExec.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.debug(_logger, "errore inserimento stato occupazionale:" + e.getMessage(), e);

			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
		}
	} // end service

	private void dereferenziaEventiAmmimistrativi(Vector movimenti, Vector dids, Vector patti, Vector mobilita,
			BigDecimal prg, RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {

		EventoAmministrativo evento = null;
		MovimentoBean mov = null;
		MobilitaBean mob = null;
		SourceBean sbApp = null;
		BigDecimal prgSOcc = null;
		for (int i = 0; i < movimenti.size(); i++) {
			sbApp = (SourceBean) movimenti.get(i);
			prgSOcc = (BigDecimal) sbApp.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ);
			if (prgSOcc == null)
				continue;
			if (prgSOcc.equals(prg)) {
				sbApp.delAttribute("prgStatoOccupaz");
				DBStore.aggiornaMovimento(sbApp, requestContainer, transExec);
			}
		}
		for (int i = 0; i < dids.size(); i++) {
			sbApp = (SourceBean) dids.get(i);
			prgSOcc = (BigDecimal) sbApp.getAttribute("prgstatooccupaz");
			if (prgSOcc == null)
				continue;
			if (prgSOcc.equals(prg)) {
				sbApp.delAttribute("prgStatoOccupaz");
				DBStore.aggiornaDID(sbApp, null, requestContainer, transExec);
			}
		}
		for (int i = 0; i < patti.size(); i++) {
			sbApp = (SourceBean) patti.get(i);
			prgSOcc = (BigDecimal) sbApp.getAttribute("prgStatoOccupaz");
			if (prgSOcc == null)
				continue;
			if (prgSOcc.equals(prg)) {
				sbApp.delAttribute("prgStatoOccupaz");
				DBStore.aggiornaPatto297(sbApp, null, requestContainer, transExec);
			}
		}
		for (int i = 0; i < mobilita.size(); i++) {
			sbApp = (SourceBean) mobilita.get(i);
			mob = new MobilitaBean(sbApp);
			prgSOcc = mob.getPrgStatoOccupaz();
			if (prgSOcc == null)
				continue;
			if (prgSOcc.equals(prg)) {
				sbApp.delAttribute("prgStatoOccupaz");
				mob.delAttribute("prgStatoOccupaz");
				mob.aggiornaStatoOccupaz(null, requestContainer, transExec);
				sbApp.updAttribute("numklomobiscr", mob.getAttribute("numklomobiscr"));
			}
		}
	}

	public int cercaIndice(StatoOccupazionaleBean statoOccIniziale, List movimenti) throws Exception {
		int i = 0;
		String dataRif = statoOccIniziale.getDataInizio();
		int indice = -1;
		for (; i < movimenti.size(); i++) {
			Object o = movimenti.get(i);
			String dataInizio = null;
			if (o instanceof MovimentoBean) {
				MovimentoBean mb = (MovimentoBean) o;
				dataInizio = mb.getDataInizio();
				if (mb.getTipoEventoAmministrativo() == EventoAmministrativo.CESSAZIONE) {
					dataInizio = DateUtils.giornoSuccessivo(dataInizio);
				}
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
				indice = i;
				break;
			}
		}
		return (indice);
	}

	private void addConfirm(SourceBean request, SourceBean response, int code, String messageFail) throws Exception {
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		SourceBean puResult = ProcessorsUtils.createResponse("", "", new Integer(code), "Situazione di incongruenza",
				warnings, nested);
		if (code == MessageCodes.StatoOccupazionale.STATO_OCC_PREC_DATA_NORMATIVA_297) {
			ProcessorsUtils.addConfirm(puResult, messageFail, "continuaInserimentoStatoOccPrec297",
					new String[] { "true" }, true);
		} else {
			ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "continuaRicalcolo",
					new String[] { "true" }, true);
		}
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}
}