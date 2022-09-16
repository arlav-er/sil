/*
 * Creato il 7-mar-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.security.User;
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
import it.eng.sil.util.amministrazione.impatti.Trasformazione;

/**
 * @author Togna Cosimo
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class AnnullaDichiarazione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AnnullaDichiarazione.class.getName());

	/** Informazioni di connessione col DB */
	String pool = null;

	/*
	 * Annullamento della dichiarazione di reddito sanato
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor txExecutor = new TransactionQueryExecutor(getPool(), this);
		SanareSituazioneAmministrativa insano = null;
		Documento doc = new Documento();
		SourceBean rowWarning = null;
		try {
			txExecutor.initTransaction();
			// seleziono i movimenti coinvolti nella dichiarazione
			Object params[] = new Object[1];
			params[0] = serviceRequest.getAttribute("prgDichLav");
			SourceBean res = (SourceBean) txExecutor.executeQuery("GET_MOVIMENTI_COINVOLTI", params, "SELECT");
			Collection movimentiCoinvolti = res.getAttributeAsVector("ROW");
			// seleziono il progressivo del movimento iniziale (il movimento a
			// partire dal quale vanno ricalcolati gli impatti)
			SourceBean movimentoIniziale = (SourceBean) res.getAttributeAsVector("ROW").get(0);
			BigDecimal prgMovimentoIniziale = (BigDecimal) ((SourceBean) res.getAttributeAsVector("ROW").get(0))
					.getAttribute("PRGMOVIMENTO");

			// Seleziono il numKLO della tabella am_dichLav
			SourceBean res1 = (SourceBean) txExecutor.executeQuery("GET_NUMKLODICHLAV", params, "SELECT");
			BigDecimal numKloDichLav = (BigDecimal) ((SourceBean) res1.getAttributeAsVector("ROW").get(0))
					.getAttribute("numKloDichLav");
			numKloDichLav = numKloDichLav.add(new BigDecimal(1));

			// Aggiorno la tabella am_dichLav
			// il campo codStatoAtto='AN'
			// il campo codMotivo = codice dell'annullamento
			Object params1[] = new Object[4];
			params1[0] = serviceRequest.getAttribute("codStatoAtto");
			params1[1] = serviceRequest.getAttribute("motivazione");
			params1[2] = numKloDichLav;
			params1[3] = serviceRequest.getAttribute("prgDichLav");
			txExecutor.executeQuery("UPD_AM_DICH_LAV", params1, "UPDATE");

			// Aggiorno la tabella AM_DOCUMENTO
			// il campo codStatoAtto='AN'
			BigDecimal prgDocumento = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "prgDocumento", null);
			if (prgDocumento != null) {
				doc.setPrgDocumento(prgDocumento);
				doc.select();
				doc.setCodStatoAtto("AN");
				doc.setCodMotAnnullamentoAtto((String) serviceRequest.getAttribute("motivazione"));
				// recupero utente di modifica documento
				RequestContainer requestContainer = getRequestContainer();
				SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
				User user = (User) sessionContainer.getAttribute(User.USERID);
				BigDecimal userid = new BigDecimal(user.getCodut());
				doc.setCdnUtMod(userid);
				doc.setNumKloDocumento(doc.getNumKloDocumento().add(new BigDecimal("1")));
				doc.update(txExecutor);
			}

			// aggiorno per i movimenti coinvolti la tabella am_movimento
			for (Iterator iterator = movimentiCoinvolti.iterator(); iterator.hasNext();) {
				SourceBean tmp = (SourceBean) iterator.next();
				BigDecimal prgMovimento = (BigDecimal) tmp.getAttribute("PRGMOVIMENTO");

				// Seleziono il numKLO della tabella am_movimento
				Object params2[] = new Object[1];
				params2[0] = prgMovimento;
				SourceBean res2 = (SourceBean) txExecutor.executeQuery("GET_NUMKLOMOV", params2, "SELECT");
				BigDecimal numKloMov = (BigDecimal) ((SourceBean) res2.getAttributeAsVector("ROW").get(0))
						.getAttribute("NUMKLOMOV");
				numKloMov = numKloMov.add(new BigDecimal(1));

				// Aggiorno la tabella am_movimento (setto a null il campo
				// decretribuzionemensanata= null)
				Object params3[] = new Object[2];
				params3[0] = numKloMov;
				params3[1] = prgMovimento;
				txExecutor.executeQuery("UPD_AM_MOVIMENTO", params3, "UPDATE");

			}
			// Ricalcolo della situazione del lavoratore
			String cdnLavoratore = movimentoIniziale.getAttribute(MovimentoBean.DB_CDNLAVORATORE).toString();
			String dataInizio = movimentoIniziale.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			Vector movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, txExecutor);
			movimentiAperti = Controlli.togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = Controlli.togliMovimentoInDataFutura(movimentiAperti);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore,
					"01/01/0001"/* dataInizio */, txExecutor);
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
			String dataRif = (String) movimentoInizialeRicrea.getAttribute("datInizioMov");

			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti,
					dids, dataRif, txExecutor, RequestContainer.getRequestContainer());

			java.util.List statiOcc = sitAmm.getStatiOccupazionali();
			ListaStatiOccupazionaliSan lso = new ListaStatiOccupazionaliSan(movimentoInizialeRicrea,
					sitAmm.getMovimenti(), statiOcc, dids, sitAmm.getListaMobilita(), txExecutor);
			if (lso.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D || lso
					.getStatoOccupazionaleIniziale().getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
				// bisogna riaprire la did nel caso sia chiusa
				boolean pattoRiaperto = false;
				DidBean did = (DidBean) sitAmm.cercaDid((String) lso.getStatoOccupazionaleIniziale().getDataInizio());
				if (did == null) {
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
			txExecutor.commitTransaction();

		} catch (MobilitaException me) {
			txExecutor.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in AnnullaDichiarazione.", (Exception) me);

			addConfirm(serviceRequest, serviceResponse, me.getCode());
		}

		catch (ControlliException ce) {
			txExecutor.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in AnnullaDichiarazione.", (Exception) ce);

			addConfirm(serviceRequest, serviceResponse, ce.getCode());
		} catch (ProTrasfoException proTrasfEx) {
			txExecutor.rollBackTransaction();
			rowWarning = new SourceBean("ROW");
			int code = proTrasfEx.getCode();
			if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
				rowWarning.setAttribute("Error", "Movimento di proroga non collegato a nessun movimento precedente.");
			} else {
				rowWarning.setAttribute("Error",
						"Movimento di trasformazione a TD non collegato a nessun movimento precedente.");
			}
			serviceResponse.setAttribute((SourceBean) rowWarning);
		} catch (Exception e) {
			txExecutor.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "AnnullaDichiarazione.service()", e);

		}

	}

	private void addConfirm(SourceBean request, SourceBean response, int code) throws Exception {
		String forzaRicostruzione = "";
		String continuaRicalcoloSOccManuale = "";
		ArrayList warnings = new ArrayList();
		ArrayList nested = new ArrayList();
		switch (code) {
		case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
			continuaRicalcoloSOccManuale = "true";
			if (request.containsAttribute("FORZA_INSERIMENTO")) {
				forzaRicostruzione = request.getAttribute("FORZA_INSERIMENTO").toString();
			}
			break;
		default:
			forzaRicostruzione = "true";
			if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				continuaRicalcoloSOccManuale = request.getAttribute("CONTINUA_CALCOLO_SOCC").toString();
			}

		}
		SourceBean puResult = ProcessorsUtils.createResponse("", "CalcolaStatoOccupazionale", new Integer(code),
				"Situazione di incongruenza", warnings, nested);
		ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiOperazione",
				new String[] { forzaRicostruzione, continuaRicalcoloSOccManuale }, true);
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

}