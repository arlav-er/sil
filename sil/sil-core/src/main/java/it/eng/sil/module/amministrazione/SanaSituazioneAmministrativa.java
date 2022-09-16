package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.DidBean;
import it.eng.sil.util.amministrazione.impatti.ListaStatiOccupazionaliSan;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.Proroga;
import it.eng.sil.util.amministrazione.impatti.SanareSituazioneAmministrativa;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativa;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleManager;
import it.eng.sil.util.amministrazione.impatti.Trasformazione;

public class SanaSituazioneAmministrativa extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SanaSituazioneAmministrativa.class.getName());
	private boolean errorProtocollazione = false;

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor txExecutor = new TransactionQueryExecutor(getPool(), this);
		String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
		String dataInizio = (String) request.getAttribute("datDichiarazione");
		SanareSituazioneAmministrativa insano = null;
		try {
			txExecutor.initTransaction();
			//
			String limite = (String) request.getAttribute("limite");
			String tipoDichiarazione = (String) request.getAttribute("tipoDichiarazione");
			request.setAttribute(MovimentoBean.FLAG_OP_SANARE, "S");
			request.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO, tipoDichiarazione.equals("dett") ? "S" : "N");
			request.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO, limite.equals("sup") ? "S" : "N");
			//
			if (limite.equals("inf"))
				request.setAttribute(StatoOccupazionaleBean.DATA_RESET_MESI_SOSP, dataInizio);
			//
			insano = new SanareSituazioneAmministrativa(getRequestContainer(), response);
			SourceBean movimenti[] = insano.getMovimenti();
			insano.preparaMovimenti(movimenti);

			// Decreto 05/11/2019
			/*
			 * if(insano.getCodiceErrore() != 0) { throw new Exception(); }
			 */

			SourceBean movimentoIniziale = insano.getMovimentoIniziale();
			request.updAttribute("codTipoDich", movimentoIniziale.getAttribute("codTipoDich"));
			inserisciDichiarazioni(movimenti, request, movimentoIniziale, txExecutor);

			// Protocolla la dichiarazione di reddito sanata
			// 02/04/2007 DOCAREA: la protocollazione del documento dovrebbe
			// sempre essere l'ultima operazione
			// protocollaDichiarazione(request, txExecutor);

			// ora posso rileggere i movimenti aggiornati (almeno in parte)
			Vector movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, txExecutor);
			movimentiAperti = Controlli.togliMovNonProtocollati(movimentiAperti);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", txExecutor);
			SourceBean ultimaDid = cercaUltimaDid(dids);
			insano.preparaMovimenti(movimenti, movimentiAperti, ultimaDid);
			movimentoIniziale = insano.cercaMovimento(
					(BigDecimal) movimentoIniziale.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO), movimentiAperti);

			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, txExecutor);
			Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", txExecutor);

			String codTipoMov = movimentoIniziale.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString();
			SourceBean movimentoInizialeRicrea = movimentoIniziale;
			if (codTipoMov.equals("TRA")) {
				Trasformazione movTra = new Trasformazione(movimentoIniziale);
				movimentoInizialeRicrea = movTra.getAvviamentoStart(movimentoIniziale, movimentiAperti);
			} else {
				if (codTipoMov.equals("PRO")) {
					Proroga movPro = new Proroga(movimentoIniziale);
					movimentoInizialeRicrea = movPro.getAvviamento();
				}
			}

			// NEL CASO DI TRASFORMAZIONI NON COLLEGATE ALLORA RIPARTO SEMPRE
			// DAL MOVIMENTO INIZIALE
			if (movimentoInizialeRicrea == null) {
				movimentoInizialeRicrea = movimentoIniziale;
			}
			String dataIniziale = (String) movimentoInizialeRicrea.getAttribute("datInizioMov");

			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti,
					dids, dataIniziale, txExecutor, RequestContainer.getRequestContainer());

			java.util.List statiOcc = sitAmm.getStatiOccupazionali();
			ListaStatiOccupazionaliSan lso = new ListaStatiOccupazionaliSan(movimentoInizialeRicrea,
					sitAmm.getMovimenti(), statiOcc, dids, sitAmm.getListaMobilita(), txExecutor);
			if (lso.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D || lso
					.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
				boolean pattoRiaperto = false;
				// bisogna riaprire la did nel caso sia chiusa
				DidBean did = (DidBean) sitAmm.cercaDid((String) lso.getStatoOccupazionaleIniziale().getDataInizio());
				if (did == null) {
					String dataRif = (String) movimentoIniziale.getAttribute("datInizioMov");
					did = (DidBean) sitAmm.cercaDid(it.eng.afExt.utils.DateUtils.giornoPrecedente(dataRif));
					if (did == null)
						did = (DidBean) sitAmm.cercaDid(dataRif);
				}
				if (did != null && did.getAttribute("datFine") != null) {
					// riapro la did
					String dataDichiarazione = (String) did.getAttribute("datdichiarazione");
					BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
					numKlo = numKlo.add(new BigDecimal(1));
					did.updAttribute("numKloDichDisp", numKlo);
					DBStore.riapriDID(did.getAttribute("prgDichDisponibilita"), dataDichiarazione, numKlo,
							RequestContainer.getRequestContainer(), txExecutor);
					did.updAttribute("flag_changed", "1");
					did.setAttribute("datFine_originale", did.getAttribute("datFine"));
					did.delAttribute("datFineChanged");
					did.delAttribute("datFine");
					sitAmm.aggiornaNumKloDichDispoInMovimenti(did, numKlo, sitAmm.getMovimenti());
					// riapertura patto associato alla did
					pattoRiaperto = sitAmm.riapriPattoAssocDid(did);
					// fine riapertura patto associato alla did
				}
				if (!pattoRiaperto) {
					sitAmm.riapriPattoMobilita((String) lso.getStatoOccupazionaleIniziale().getDataInizio(),
							cdnLavoratore);
				}

			}
			// ricostruzione storia
			sitAmm.ricrea(movimentoInizialeRicrea, lso);
			if (limite.equals("sup")) {
				if (request.containsAttribute(SanareSituazioneAmministrativa.FLAG_LIMITE_INF)
						&& !request.containsAttribute(SanareSituazioneAmministrativa.FLAG_LIMITE_SUP)
						&& (!request.containsAttribute(MovimentoBean.FLAG_OP_SANARE_FORZATURA)
								|| !request.getAttribute(MovimentoBean.FLAG_OP_SANARE_FORZATURA).equals("S"))) {
					// c'e' stato almeno un caso in cui il limite non e' stato
					// superato
					throw new ControlliException(MessageCodes.StatoOccupazionale.REDDITO_INFERIORE_OP_SANARE);
				}
			}

			// 30/03/2007 DOCAREA: la protocollazione del documento e' stata
			// spostata come ultima operazione
			protocollaDichiarazione(request, txExecutor);
			txExecutor.commitTransaction();
			// 30/03/2007 DOCAREA: operazione ok. Bisogna cancellare il file
			// inviato a docarea (se esiste e se si e' in docarea...)
			// il file e' stato inserito nella request dalla chiamata
			// ProtocolloDocumentoUtil.putInResponse(doc) in fondo alla classe
			ProtocolloDocumentoUtil.cancellaFileDocarea();
			response.setAttribute("RESULT", "OK");
			response.setAttribute("prgDichLav", request.getAttribute("prgDichLav"));
			reportOperation.reportSuccess(MessageCodes.StatoOccupazionale.SITUAZIONE_SANATA);
		} catch (ProTrasfoException e) {
			txExecutor.rollBackTransaction();
			int code = e.getCode();
			addConfirm(response, code);
			response.setAttribute("movimenti_ricostruiti", insano.getMovimenti());
			if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA)
				reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA, e, "service", "");
			else
				reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA, e, "service", "");
		} catch (ControlliException ce) {
			txExecutor.rollBackTransaction();
			switch (ce.getCode()) {
			case MessageCodes.StatoOccupazionale.REDDITO_SUPERIORE_OP_SANARE:
			case MessageCodes.DID.REDDITO_SUPERIORE_LIMITE:
				addConfirm(response, ce.getCode());
				break;
			case MessageCodes.StatoOccupazionale.REDDITO_INFERIORE_OP_SANARE:
				addConfirm(response, ce.getCode());
				break;
			case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
			case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
			case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
			case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
				addConfirm(request, response, ce.getCode());
				break;

			default:
				if (request.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
					addConfirm(response, ce.getCode());
				}
			}
			response.setAttribute("movimenti_ricostruiti", insano.getMovimenti());
			reportOperation.reportFailure(ce, "service()", "");
		}

		catch (MobilitaException me) {
			txExecutor.rollBackTransaction();
			addConfirm(response, me.getCode());
			response.setAttribute("movimenti_ricostruiti", insano.getMovimenti());
			reportOperation.reportFailure(me, "service()", "");
		}

		catch (Exception e) {
			txExecutor.rollBackTransaction();
			response.setAttribute("movimenti_ricostruiti", insano.getMovimenti());
			if (errorProtocollazione) {
				// 02/04/2007 DOCAREA: cattura il codice di errore di docarea
				// (se e' attivo)
				if (e instanceof EMFUserError)
					reportOperation.reportFailure((EMFUserError) e, "service", "");
				else
					reportOperation.reportFailure(MessageCodes.Protocollazione.ERR_GENERICO_NELLA_SP, e, "service", "");
			} else {

				// Decreto 05/11/2019
				/*
				 * if(insano.getCodiceErrore() != 0) { Vector paramV = new Vector(1);
				 * paramV.add(insano.getDescErrore());
				 * //reportOperation.reportFailure(MessageCodes.ControlliMovimentiDecreto.
				 * ERR_COMPENSO_RETRIBUZIONE_SANATORIA, "", "", paramV);
				 * reportOperation.reportFailure(insano.getCodiceErrore(), "", "", paramV); }else {
				 */
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
				// }

			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "SanaSituazioneAmministrativa.service()", e);

		}

	}

	private void addConfirm(SourceBean response, int code) throws Exception {
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		SourceBean puResult = ProcessorsUtils.createResponse("", "", new Integer(code), "Situazione di incongruenza",
				warnings, nested);
		ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiOperazione", new String[] {}, true);

		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

	private void addConfirm(SourceBean request, SourceBean response, int code) throws Exception {
		String forzaRicostruzione = "";
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		SourceBean puResult = ProcessorsUtils.createResponse("", "", new Integer(code), "Situazione di incongruenza",
				warnings, nested);
		if (request.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
			forzaRicostruzione = RequestContainer.getRequestContainer().getServiceRequest()
					.getAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE).toString();
			ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "continuaRicalcolo",
					new String[] { forzaRicostruzione }, true);
		}
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

	private void inserisciDichiarazioni(SourceBean[] movimenti, SourceBean request, SourceBean movimentoIniziale,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal prgMovimentoIniziale = (java.math.BigDecimal) movimentoIniziale.getAttribute("prgMovimento");
		DBStore.creaDichiarazioneLavoratore(request, prgMovimentoIniziale, transExec);
		for (int i = 0; i < movimenti.length; i++) {
			if (movimenti[i].containsAttribute(MovimentoBean.FLAG_OP_SANARE)) {
				movimenti[i].setAttribute("prgDichLav", request.getAttribute("prgDichLav"));
				DBStore.aggiornaMovInfoSanare(request, movimenti[i], transExec);
				DBStore.creaDichiarazioneLavDettaglio(movimenti[i], transExec);
			}
		}
	}

	public SourceBean cercaUltimaDid(Vector dids) throws Exception {
		SourceBean ret = null;
		for (int i = dids.size() - 1; i >= 0; i--) {
			SourceBean did = (SourceBean) dids.get(i);
			String dataDichiarazione = (String) did.getAttribute("datDichiarazione");
			String codStatoAtto = (String) did.getAttribute("codStatoAtto");
			String dataFine = (String) did.getAttribute("datFine");
			if (codStatoAtto.equals("PR")) {
				ret = did;
				break;
			}
		}
		return ret;
	}

	/**
	 * Questo metodo protocolla la situazione di reddito sanata. Crea il documento associato alla dichiarazione
	 * inserendone i dati nelle tabelle AM_DOCUMENTO e AM_DOCUMENTO_COLL
	 * 
	 * @param request
	 *            Request
	 * @param txExecutor
	 *            TransactionQueryExecutor
	 * @author Togna Cosimo 17-03-05
	 */
	private void protocollaDichiarazione(SourceBean request, TransactionQueryExecutor txExecutor) throws Exception {

		Documento doc = new Documento();

		String annoProt = StringUtils.getAttributeStrNotNull(request, "annoProt");
		String dataProt = StringUtils.getAttributeStrNotNull(request, "dataProt");
		String oraProt = StringUtils.getAttributeStrNotNull(request, "oraProt");
		String tipoProt = StringUtils.getAttributeStrNotNull(request, "tipoProt");
		String prgAzDest = StringUtils.getAttributeStrNotNull(request, "PRGAZIENDADESTINAZIONE");
		String prgUAzDest = StringUtils.getAttributeStrNotNull(request, "PRGUNITADESTINAZIONE");
		String datComunicaz = StringUtils.getAttributeStrNotNull(request, "DATCOMUNICAZ");
		String datInizioMov = StringUtils.getAttributeStrNotNull(request, "DATTRASFERIMENTO");
		String numProt = StringUtils.getAttributeStrNotNull(request, "numProt");
		String docInOrOut = StringUtils.getAttributeStrNotNull(request, "docInOrOut");
		String rif = StringUtils.getAttributeStrNotNull(request, "rif");

		if (!annoProt.equals(""))
			doc.setNumAnnoProt(new BigDecimal(annoProt));
		if (!numProt.equals(""))
			doc.setNumProtocollo(new BigDecimal(numProt));
		if (!dataProt.equals("") && !oraProt.equals(""))
			doc.setDatProtocollazione(dataProt + " " + oraProt);
		if (!tipoProt.equals(""))
			doc.setTipoProt(tipoProt);
		if (!prgAzDest.equals(""))
			doc.setPrgAzienda(new BigDecimal(prgAzDest));
		if (!prgUAzDest.equals(""))
			doc.setPrgUnita(new BigDecimal(prgUAzDest));
		if (!datComunicaz.equals(""))
			doc.setDatAcqril(datComunicaz);
		if (!datInizioMov.equals(""))
			doc.setDatInizio(datInizioMov);
		if (!docInOrOut.equals(""))
			doc.setCodMonoIO(docInOrOut);
		if (!rif.equals(""))
			doc.setFlgDocAmm("S");

		// Recupero dati protocollazione
		String codCpi = StringUtils.getAttributeStrNotNull(request, "CODRIF");
		String cdnLav = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
		String page = "DichRedDettaglioPage";

		if (!codCpi.equals(""))
			doc.setCodCpi(codCpi);
		if (!cdnLav.equals(""))
			doc.setCdnLavoratore(new BigDecimal(cdnLav));

		doc.setCodTipoDocumento("DR02");

		doc.setPagina(page);
		doc.setCdnUtMod((BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
		doc.setCdnUtIns((BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
		doc.setChiaveTabella(request.getAttribute("prgDichLav").toString());
		doc.setDatAcqril(DateUtils.getNow());

		doc.setTipoProt("S");
		doc.setCdnComponente(new BigDecimal("373"));
		doc.setCodStatoAtto("PR");

		try {
			// 02/04/2007 prima della operazione di inserimento bisogna
			// registrare il documento nella service request.
			// Verra' poi ripreso dalla chiamata
			// ProtocolloDocumentoUtil.cancellaFileDocarea();
			ProtocolloDocumentoUtil.putInRequest(doc);
			doc.insert(txExecutor);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "SanaSituazioneAmministrativa.protocollaDichiarazione()", e);

			errorProtocollazione = true;
			throw e;
		}

	}
}