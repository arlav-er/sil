package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;

public class StatoOccupazionaleManager {
	public static final String WARNING_STATO_OCCUPAZIONALE = "WARNING_STATO_OCCUPAZIONALE";
	public static final String ALERT_STATO_OCCUPAZIONALE = "ALERT_STATO_OCCUPAZIONALE";
	public static final String CONFIRM_STATO_OCCUPAZIONALE = "CONFIRM_STATO_OCCUPAZIONALE";
	public static final int NO_SANAMENTO_SITUAZIONE = 0;
	public static final int SANAMENTO_GENERICA = 1;
	public static final int SANAMENTO_DETTAGLIO = -1;
	public static final int GIORNI_COMMERCIALI_ANNO = 360;

	/**
	 * @param SourceBean
	 *            row e' la riga estratta dal db contenente i tre campi necessari per il calcolo della categoria di
	 *            appartenenza del lavoratore: DATNASC, FLGOBBLIGOSCOLASTICO, FLGLAUREA
	 */
	public static boolean appartieneCat181(SourceBean row, String dataInizioMov) {
		String dataNascitaLavoratore = (String) row.getAttribute("DATNASC");
		String flgObbFo = (String) row.getAttribute("FLGOBBLIGOSCOLASTICO");
		String laurea = (String) row.getAttribute("FLGLAUREA");
		String cat = Controlli.getCat181(dataNascitaLavoratore, dataInizioMov, flgObbFo, laurea);

		return ((cat != null) && "giovane".equals(cat.toLowerCase()));
	}

	/**
	 * @param SourceBean
	 *            <b>movimento</b> il movimento in base al quale calcolare il nuovo stato occupazionale
	 * @param RequestContainer
	 *            <b>requestContainer</b> e' necessario per permettere il prelievo dei dati in sessione (es. _USER_)
	 * @param SourceBean
	 *            <b>response</b>
	 * @param TransactionQueryExecutor
	 *            <b>transExec</b> le operazioni col db avvengono all' interno di una transazione nella quale viene
	 *            anche inserito il record del movimento.
	 *
	 * @return StatoOccupazionale il nuovo stato occupazionale del lavoratore
	 */
	public static StatoOccupazionaleBean avviamento(SourceBean movimento,
			StatoOccupazionaleBean statoOccupazionaleIniziale, RequestContainer requestContainer, SourceBean response,
			TransactionQueryExecutor transExec, Vector movimentiAnno) throws Exception {
		// recupero del contesto operativo
		String contestoOperativo = movimento.containsAttribute("CONTESTO_OPERATIVO")
				? movimento.getAttribute("CONTESTO_OPERATIVO").toString()
				: "";
		// funzione warning sui dati del movimento (durata, reddito)
		MostraInfoMovimento(movimento, requestContainer);

		SourceBean cm = null;
		SourceBean did = null;
		String dataInizio = null;
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		Object cdnLavoratore = null;
		cdnLavoratore = MovimentoBean.getCdnLavoratore(movimento);
		// lettura dello stato occupazionale di partenza del lavoratore
		if (statoOccupazionaleIniziale == null) {
			statoOccupazionaleIniziale = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);
		}
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();
		if (Controlli.movimentoPrecedenteNormativa(movimento, dataNormativa297)) {
			// movimento precedente alla dataNormativa297;sola segnalazione e non blocco
			// dell'operazione in quanto gli impatti non avvengono
			nuovoStatoOccupazionale = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(movimento);
			nuovoStatoOccupazionale.setChanged(false);
			if (response != null) {
				Vector paramV = new Vector(1);
				paramV.add(dataNormativa297);
				MessageAppender.appendMessage(response, MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_DATA_NORMATIVA,
						paramV);
			}
		}
		// Controllo se il movimento di avviamento fa scattare o meno la ricostruzione storia.
		// Questa chiamata serve solo per la visualizzazione del messaggio finale,
		// in quanto la ricostruzione storia viene sempre eseguita.
		boolean ricostruisciStoria = ControlliRicostruzioneStoria.ricostruisciStoriaAvv(movimento,
				statoOccupazionaleIniziale, cdnLavoratore, transExec);

		dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		cdnLavoratore = MovimentoBean.getCdnLavoratore(movimento);
		Vector movimentiAperti = null;
		Vector dids = null;
		Vector patti = null;
		Vector statiOccupazionali = null;

		statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
		dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", transExec);
		patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
		movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
		movimentiAperti = togliMovNonProtocollati(movimentiAperti);
		movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
		StatoOccupazionaleBean so = null;
		SourceBean avv = new SourceBean(movimento);
		// inserimento del movimento di avviamento nel vettore movimentiAperti
		Controlli.inserisciCessazione(movimentiAperti, avv);

		SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti, dids,
				dataInizio, transExec, requestContainer);

		// Recupero eventuale lista mobilita del lavoratore
		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
		Vector rowsMobilita = mobilita.getMobilita();
		// SE ESISTE UN MOVIMENTO APERTO RISPETTO ALLA DATA INIZIO DEL
		// MOVIMENTO IN INSERIMENTO, ALLORA RIPARTO DAL MOVIMENTO APERTO
		String dataInizioMov = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		if (movimentiAperti != null) {
			movimento = Controlli.selezionaMovimentoApertoIniziale(dataInizioMov, movimento, movimentiAperti);
		}
		// controlla se il movimento restituito dal metodo selezionaMovimentoApertoIniziale è una
		// proroga o trasformazione (controllo sui precedenti) e nel caso in cui il movimento selezionato
		// è una proroga o trasformazione con il precedente, allora devo ripartire dall'avviamento
		Controlli.controllaMovimentoProTraSenzaPrec(movimento);
		movimento = Controlli.recuperaAvvDaProTra(movimento, movimentiAperti, transExec);
		String dataControlloMobilita = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataControlloMobilita,
				sitAmm.getPrgMobilitaRicalcolo());
		if (sbMobilitaMov != null) {
			// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
			// DATA INIZIO DEL MOVIMENTO
			sitAmm.ricreaDaMobilita(sbMobilitaMov);
		} else {
			// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
			// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
			SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali,
					dataControlloMobilita);
			if (sbStatoOccAperto != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
				sitAmm.ricrea(movimento);
			} else {
				SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataControlloMobilita);
				if (sbMobilita != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
					sitAmm.ricreaDaMobilita(sbMobilita);
				} else {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(movimento);
				}
			}
		}

		so = sitAmm.getStatoOccupazionale(movimento);
		// alert stato occupazionale se non sono in validazione massiva
		if (!contestoOperativo.equalsIgnoreCase(MovimentoBean.CONTESTO_VALIDAZIONE_MASSIVA)) {
			if (ricostruisciStoria) {
				impostaAlert(sitAmm, requestContainer.getServiceRequest(), cdnLavoratore);
			} else {
				if (statoOccupazionaleIniziale.getStatoOccupazionale() != sitAmm.getStatoOccupazionaleAperto()
						.getStatoOccupazionale()/* so.getStatoOccupazionale() */) {
					requestContainer.getServiceRequest().setAttribute("STATO_OCC_APERTO",
							sitAmm.getStatoOccupazionaleAperto().getDescrizioneCompleta());
					ControlliRicostruzioneStoria.aggiungiAlert(statoOccupazionaleIniziale, so,
							requestContainer.getServiceRequest());
				}
			}
		}
		boolean pippo = false;
		if (pippo)
			throw new Exception("Eccezione per TEST");
		return so;
	}

	/**
	 * Viene chiamato dai bacth sui movimenti futuri quando il movimento che si sta processando è un avviamento. Si è
	 * reso necessario perché il metodo usato quando si inserisce un avviamento, recupera i movimenti del lavoratore e
	 * poi gli aggiunge il movimento in inserimento (che non è ancora presente sul db). Se utilizzassimo questo metodo
	 * nei batch, ci ritroveremo il movimento replicato nel vettore dei movimentiAperti (quando recupera i movimenti del
	 * lavoratore, recupera anche il movimento che il batch deve processare in quanto è già salvato sul db).
	 * 
	 * @param movimento
	 * @param statoOccupazionaleIniziale
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @param movimentiAnno
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean avviamentoDaBatchFuturi(SourceBean movimento,
			StatoOccupazionaleBean statoOccupazionaleIniziale, RequestContainer requestContainer, SourceBean response,
			TransactionQueryExecutor transExec, Vector movimentiAnno, LogBatch logBatch) throws Exception {

		SourceBean cm = null;
		SourceBean did = null;
		String dataInizio = null;
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		Object cdnLavoratore = null;
		cdnLavoratore = MovimentoBean.getCdnLavoratore(movimento);
		// lettura dello stato occupazionale di partenza del lavoratore
		if (statoOccupazionaleIniziale == null) {
			statoOccupazionaleIniziale = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);
		}
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();

		if (Controlli.movimentoPrecedenteNormativa(movimento, dataNormativa297)) {
			// movimento precedente alla dataNormativa297;sola segnalazione e non blocco
			// dell'operazione in quanto gli impatti non avvengono
			nuovoStatoOccupazionale = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(movimento);
			nuovoStatoOccupazionale.setChanged(false);
			if (response != null) {
				Vector paramV = new Vector(1);
				paramV.add(dataNormativa297);
				MessageAppender.appendMessage(response, MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_DATA_NORMATIVA,
						paramV);
			}
		}

		dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		cdnLavoratore = MovimentoBean.getCdnLavoratore(movimento);
		Vector movimentiAperti = null;
		Vector dids = null;
		Vector patti = null;
		Vector statiOccupazionali = null;

		statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
		dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", transExec);
		patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
		movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
		movimentiAperti = togliMovNonProtocollati(movimentiAperti);
		movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
		StatoOccupazionaleBean so = null;

		SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti, dids,
				dataInizio, transExec, requestContainer, logBatch);

		// recupero eventuale lista mobilita del lavoratore
		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
		Vector rowsMobilita = mobilita.getMobilita();
		// SE ESISTE UN MOVIMENTO APERTO RISPETTO ALLA DATA INIZIO DEL
		// MOVIMENTO IN INSERIMENTO, ALLORA RIPARTO DAL MOVIMENTO APERTO
		String dataInizioMov = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		if (movimentiAperti != null) {
			movimento = Controlli.selezionaMovimentoApertoIniziale(dataInizioMov, movimento, movimentiAperti);
		}
		// controlla se il movimento restituito dal metodo selezionaMovimentoApertoIniziale è una
		// proroga o trasformazione (controllo sui precedenti) e nel caso in cui il movimento selezionato
		// è una proroga o trasformazione con il precedente, allora devo ripartire dall'avviamento
		Controlli.controllaMovimentoProTraSenzaPrec(movimento);
		movimento = Controlli.recuperaAvvDaProTra(movimento, movimentiAperti, transExec);
		String dataControlloMobilita = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataControlloMobilita,
				sitAmm.getPrgMobilitaRicalcolo());
		if (sbMobilitaMov != null) {
			// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
			// DATA INIZIO DEL MOVIMENTO
			sitAmm.ricreaDaMobilita(sbMobilitaMov);
		} else {
			// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
			// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
			SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali,
					dataControlloMobilita);
			if (sbStatoOccAperto != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
				sitAmm.ricrea(movimento);
			} else {
				SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataControlloMobilita);
				if (sbMobilita != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
					sitAmm.ricreaDaMobilita(sbMobilita);
				} else {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(movimento);
				}
			}
		}

		so = sitAmm.getStatoOccupazionale(movimento);
		boolean pippo = false;
		if (pippo)
			throw new Exception("Eccezione per TEST");
		return so;
	}

	/**
	 * DALLA LISTA DEI MOVIMENTI VENGONO TOLTI I MOVIMENTI NON PROTOCOLLATI
	 * 
	 * @param movimenti
	 * @return
	 */
	public static Vector togliMovNonProtocollati(Vector movimenti) {
		Vector v = new Vector();
		for (int i = 0; i < movimenti.size(); i++) {
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
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (DateUtils.compare(tmp.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString(), oggi) > 0)
				continue;
			v.add(tmp);
		} // end for
		return v;
	}

	/**
	 * 
	 * @param movimento
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean aggiorna(SourceBean movimento, RequestContainer requestContainer,
			SourceBean response, TransactionQueryExecutor transExec) throws Exception {
		StatoOccupazionaleBean socc = null;

		// innanzi tutto controllo se il movimento e' gestibile: se e' successivo alla data dataNormativa297
		// e se non e' in data futura (per la cessazione si puo' cessare un movimento con data massima del giorno
		// precedente alla data odierna)
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();
		if (Controlli.movimentoPrecedenteNormativa(movimento, dataNormativa297)) {
			socc = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(movimento);
			socc.setChanged(false);
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
			Vector paramV = new Vector(1);
			paramV.add(dataNormativa297);
			warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
					MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_NORMATIVA,
					MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_DATA_NORMATIVA, paramV));
			return socc;
		}

		// nel caso di proroghe/trasformazioni il movimento da controllare
		// per determinare se è un movimento in data futura, è l'avviamento
		// GESTIONE COMMENTATA su richiesta F.V. in data 23/12/2004
		// SourceBean movGestibile = null;
		// movGestibile = selezionaMovimentoPerImpatti(movimento);
		if (Controlli.movimentoInDataFutura(movimento)) {
			// e' necessario mostrare a video l'informazione che il movimento
			// in inserimento al momento non provochera' impatti amministrativi
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
			warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
					MessageCodes.StatoOccupazionale.GESTIONE_FUTURA, ""));
			// restituisco un oggetto stato occupazionale con informazioni minime. Il movimento non
			// sara' associato ad alcuno stato occupazionale
			StatoOccupazionaleBean sOcc = new StatoOccupazionaleBean();
			sOcc.setChanged(false);
			return sOcc;
		}

		switch (MovimentoBean.getTipoMovimento(movimento)) {
		case MovimentoBean.ASSUNZIONE:
			socc = avviamento(movimento, null, requestContainer, response, transExec, null);
			break;

		case MovimentoBean.CESSAZIONE:
			socc = cessazione(movimento, requestContainer, response, transExec);
			break;
		case MovimentoBean.TRASFORMAZIONE:
			socc = trasformazione(movimento, requestContainer, response, transExec);
			break;
		case MovimentoBean.PROROGA:
			socc = proroga(movimento, requestContainer, response, transExec);
		}

		boolean pippo = false;
		if (pippo)
			throw new Exception("terminato per test");
		return socc;
	}

	/**
	 * metodo invocato dal processor EseguiImpatti per gli impatti amministrativi in seguito ad un evento amministrativo
	 * 
	 * @param request
	 * @param user
	 * @param txExecutor
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean aggiorna(Map request, BigDecimal user, TransactionQueryExecutor txExecutor)
			throws Exception {
		// creazione di un SourceBean dai dati contenuti nella Map
		// che rappresenta il movimento che si sta inserendo
		SourceBean movimento = Controlli.getMovimento(request);
		return aggiorna(movimento, RequestContainer.getRequestContainer(), null, txExecutor);

	}

	/**
	 * Metodo per l'aggiornamento dello stato occupazionale per il batch che processa i movimenti con data inizio
	 * futura.
	 * 
	 * @param movimento
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean aggiornaStatoPerBatch(SourceBean movimento, RequestContainer requestContainer,
			SourceBean response, TransactionQueryExecutor transExec, LogBatch logBatch) throws Exception {
		StatoOccupazionaleBean socc = null;

		// controllo se il movimento e' gestibile, cioè e' successivo
		// alla data dataNormativa297
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();
		if (Controlli.movimentoPrecedenteNormativa(movimento, dataNormativa297)) {
			socc = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(movimento);
			socc.setChanged(false);
			if (response != null) {
				Vector paramV = new Vector(1);
				paramV.add(dataNormativa297);
				MessageAppender.appendMessage(response, MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_DATA_NORMATIVA,
						paramV);
			}
			return socc;
		}

		// controlla se la proroga o trasformazione è un movimento senza precedente
		// e in questo caso lancio un'eccezione
		Controlli.controllaMovimentoProTraSenzaPrec(movimento);

		switch (MovimentoBean.getTipoMovimento(movimento)) {
		case MovimentoBean.ASSUNZIONE:
			socc = avviamentoDaBatchFuturi(movimento, null, requestContainer, response, transExec, null, logBatch);
			break;

		case MovimentoBean.CESSAZIONE:
			socc = cessazioneDaBatchFuturi(movimento, requestContainer, response, transExec, logBatch);
			break;
		case MovimentoBean.TRASFORMAZIONE:
			socc = trasformazioneDaBatchFuturi(movimento, requestContainer, response, transExec, logBatch);
			break;
		case MovimentoBean.PROROGA:
			socc = prorogaDaBatchFuturi(movimento, requestContainer, response, transExec, logBatch);
		}
		return socc;
	}

	/**
	 * 
	 * @param cessazione
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean cessazione(SourceBean cessazione, RequestContainer requestContainer,
			SourceBean response, TransactionQueryExecutor transExec) throws Exception {
		BigDecimal prgMovimentoCollegato = (BigDecimal) cessazione.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		// determino il movimento di avviamento collegato alla cessazione
		SourceBean avviamento = null;

		if (prgMovimentoCollegato != null) {
			avviamento = DBLoad.getMovimento(prgMovimentoCollegato, transExec);
		} else {
			BigDecimal prgMov = (BigDecimal) cessazione.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			avviamento = DBLoad.getMovimento(prgMov, transExec);
		}
		return cessazione(cessazione, avviamento, requestContainer, response, transExec);
	}

	/**
	 * 
	 * @param cessazione
	 * @param avviamento
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean cessazione(SourceBean cessazione, SourceBean avviamento,
			RequestContainer requestContainer, SourceBean response, TransactionQueryExecutor transExec)
			throws Exception {

		String dataInizio = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine = "31/12/" + DateUtils.getAnno(dataInizio);
		String cdnLavoratore = MovimentoBean.getCdnLavoratore(cessazione);
		Vector movimentiAperti = DBLoad.getMovimentiAnno(dataInizio, dataFine, cdnLavoratore, transExec);
		movimentiAperti = togliMovNonProtocollati(movimentiAperti);
		movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
		movimentiAperti = MovimentoBean.gestisciTuttiPeriodiIntermittenti(movimentiAperti, transExec);
		return cessazione(cessazione, avviamento, null, movimentiAperti, requestContainer, response, transExec);
	}

	/**
	 * Metodo chiamato in seguito all'inserimento di una cessazione per il calcolo degli impatti amministrativi.
	 * 
	 * @param cessazione
	 * @param avviamento
	 * @param statoOccupazionaleIniziale
	 * @param movimentiAperti
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean cessazione(SourceBean cessazione, SourceBean avviamento,
			StatoOccupazionaleBean statoOccupazionaleIniziale, Vector movimentiAperti,
			RequestContainer requestContainer, SourceBean response, TransactionQueryExecutor transExec)
			throws Exception {

		// recupero del contesto operativo
		String contestoOperativo = cessazione.containsAttribute("CONTESTO_OPERATIVO")
				? cessazione.getAttribute("CONTESTO_OPERATIVO").toString()
				: "";
		boolean skipRicostruzione = requestContainer.getServiceRequest().containsAttribute("FLAG_SKIP_RICOSTRUZIONE");
		StatoOccupazionaleBean statoOccupazionale = null;

		String cdnLavoratore = MovimentoBean.getCdnLavoratore(cessazione);
		// lettura dello stato occupazionale di partenza del lavoratore
		if (statoOccupazionaleIniziale == null) {
			statoOccupazionaleIniziale = DBLoad.getStatoOccupazionale(cdnLavoratore.toString(), transExec);
		}

		if ((Contratto.DIP_TI == Contratto.getTipoContratto(avviamento))
				|| Controlli.cessazioneAnticipata(avviamento, cessazione)
				|| Controlli.cessazioneOdiernaNoBatch(avviamento, cessazione)) {

			Vector statiOccupazionali = new Vector();
			Vector patti = null;
			Vector dids = null;
			Vector didsUltima = new Vector();
			Vector pattiUltimo = new Vector();
			String dataInizio = (String) avviamento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			// Controllo se il movimento di cessazione fa scattare o meno la ricostruzione storia.
			// Questa chiamata serve solo per la visualizzazione del messaggio finale,
			// in quanto la ricostruzione storia viene sempre eseguita.
			boolean ricostruisciStoria = ControlliRicostruzioneStoria.ricostruisciStoriaCes(avviamento, cdnLavoratore,
					requestContainer, transExec);

			dids = DBLoad.getDichiarazioniDisponibilita(cdnLavoratore, "01/01/0001" /* dataIizio */, transExec);
			patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
			// i movimenti aperti li ho gia' letti
			// problema : in questi movimenti non e' ancora presente il movimento di cessazione
			// debbo crearne uno fittizio ed aggiungerlo alla lista
			SourceBean ce = new SourceBean(cessazione);
			Vector movAperti = DBLoad.getMovimentiLavoratore(cessazione.getAttribute(MovimentoBean.DB_CDNLAVORATORE),
					transExec);
			movAperti = togliMovNonProtocollati(movAperti);

			if (!Controlli.inserisciCessazione(movAperti, ce))
				throw new ControlliException(MessageCodes.StatoOccupazionale.MOV_CESSATO_NON_TROVATO);
			// ora il SourceBean di cessazione e' presente nella lista dei movimenti aperti,
			// contraddistinto dall'attributo FLAG_IN_INSERIMENTO
			dataInizio = (String) avviamento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movAperti, statiOccupazionali, patti, dids,
					dataInizio, transExec, requestContainer);

			// recupero eventuale lista mobilita del lavoratore
			ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
			Vector rowsMobilita = mobilita.getMobilita();
			if (statoOccupazionaleIniziale.getProgressivoDB() == null) {
				String dataRif = (String) cessazione.getAttribute("datInizioMov");
				// creazione di uno stato occupazionale iniziale con data inizio uguale
				// alla data inizio del movimento di cessazione (se la data inizio è
				// precedente alla data normativa dataNormativa297 allora viene settata la data
				// inizio dello stato occupazionale iniziale = dataNormativa297).
				statoOccupazionaleIniziale = StatoOccupazionaleBean.creaStatoOccupazionaleBeanIniziale(dataRif,
						cdnLavoratore, null, dids, rowsMobilita, movimentiAperti, transExec);
				if (statoOccupazionaleIniziale.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D) {
					statoOccupazionaleIniziale.setStatoOccupazionale(StatoOccupazionaleBean.B1);
				}
			}
			// SE ESISTE UN MOVIMENTO APERTO RISPETTO ALLA DATA INIZIO DEL
			// MOVIMENTO IN INSERIMENTO, ALLORA RIPARTO DAL MOVIMENTO APERTO
			// (PER ORA COMMENTATA)
			// movimentiAperti = movAperti;
			// String dataInizioMov = avviamento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			// if (movimentiAperti != null) {
			// avviamento = Controlli.selezionaMovimentoApertoIniziale(dataInizioMov,avviamento,movimentiAperti);
			// }
			String dataControlloMobilita = avviamento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataControlloMobilita,
					sitAmm.getPrgMobilitaRicalcolo());
			if (sbMobilitaMov != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
				// DATA INIZIO DEL MOVIMENTO
				sitAmm.ricreaDaMobilita(sbMobilitaMov);
			} else {
				// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
				// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
				SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali,
						dataControlloMobilita);
				if (sbStatoOccAperto != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(avviamento);
				} else {
					SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataControlloMobilita);
					if (sbMobilita != null) {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
						sitAmm.ricreaDaMobilita(sbMobilita);
					} else {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
						sitAmm.ricrea(avviamento);
					}
				}
			}

			statoOccupazionale = sitAmm.getStatoOccupazionaleAperto();
			if (requestContainer.getServiceRequest().containsAttribute("STATO_OCC_APERTO")) {
				requestContainer.getServiceRequest().delAttribute("STATO_OCC_APERTO");
			}
			// alert stato occupazionale se non sono in validazione massiva
			if (!contestoOperativo.equalsIgnoreCase(MovimentoBean.CONTESTO_VALIDAZIONE_MASSIVA)) {
				if (ricostruisciStoria) {
					impostaAlert(sitAmm, requestContainer.getServiceRequest(), cdnLavoratore);
				} else {
					if (statoOccupazionaleIniziale.getStatoOccupazionale() != statoOccupazionale
							.getStatoOccupazionale()) {
						ControlliRicostruzioneStoria.aggiungiAlert(statoOccupazionaleIniziale, statoOccupazionale,
								requestContainer.getServiceRequest());
					}
				}
			}
		} else
			statoOccupazionale = statoOccupazionaleIniziale;
		// il movimento e' gia' stato processato dal batch giornaliero
		// lo stato occupazionale non cambia
		return statoOccupazionale;
	}

	/**
	 * Viene chiamato dai bacth sui movimenti futuri quando il movimento che si sta
	 * 
	 * @param cessazione
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @param logBatch
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean cessazioneDaBatchFuturi(SourceBean cessazione,
			RequestContainer requestContainer, SourceBean response, TransactionQueryExecutor transExec,
			LogBatch logBatch) throws Exception {

		boolean skipRicostruzione = requestContainer.getServiceRequest().containsAttribute("FLAG_SKIP_RICOSTRUZIONE");
		StatoOccupazionaleBean statoOccupazionale = null;

		String cdnLavoratore = MovimentoBean.getCdnLavoratore(cessazione);

		BigDecimal prgMovimentoCollegato = (BigDecimal) cessazione.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		// determino il movimento di avviamento collegato alla cessazione
		SourceBean avviamento = null;

		if (prgMovimentoCollegato != null) {
			avviamento = DBLoad.getMovimento(prgMovimentoCollegato, transExec);
		} else {
			BigDecimal prgMov = (BigDecimal) cessazione.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			avviamento = DBLoad.getMovimento(prgMov, transExec);
		}

		String dataInizio = (String) cessazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine = "31/12/" + DateUtils.getAnno(dataInizio);
		Vector statiOccupazionali = new Vector();
		Vector patti = null;
		Vector dids = null;
		Vector didsUltima = new Vector();
		Vector pattiUltimo = new Vector();
		String dataInizioAvv = (String) avviamento.getAttribute(MovimentoBean.DB_DATA_INIZIO);

		dids = DBLoad.getDichiarazioniDisponibilita(cdnLavoratore, "01/01/0001" /* dataIizio */, transExec);
		patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
		Vector movimentiAperti = DBLoad.getMovimentiLavoratore(cessazione.getAttribute(MovimentoBean.DB_CDNLAVORATORE),
				transExec);
		movimentiAperti = togliMovNonProtocollati(movimentiAperti);
		statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);

		SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti, dids,
				dataInizioAvv, transExec, requestContainer, logBatch);

		// recupero eventuale lista mobilita del lavoratore
		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
		Vector rowsMobilita = mobilita.getMobilita();
		String dataControlloMobilita = avviamento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataControlloMobilita,
				sitAmm.getPrgMobilitaRicalcolo());
		if (sbMobilitaMov != null) {
			// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
			// DATA INIZIO DEL MOVIMENTO
			sitAmm.ricreaDaMobilita(sbMobilitaMov);
		} else {
			// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
			// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
			SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali,
					dataControlloMobilita);
			if (sbStatoOccAperto != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
				sitAmm.ricrea(avviamento);
			} else {
				SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataControlloMobilita);
				if (sbMobilita != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
					sitAmm.ricreaDaMobilita(sbMobilita);
				} else {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(avviamento);
				}
			}
		}

		statoOccupazionale = sitAmm.getStatoOccupazionale(cessazione);
		return statoOccupazionale;
	}

	/**
	 * Viene chiamato dal batch cessazioni td, che considera i movomenti terminati il giorno precedente all'avvio.
	 * Vengono considerati solo i movimenti finiti allo scadere della data fine movimento originario, quindi non cessati
	 * anticipatamente
	 * 
	 * @param cessazione
	 * @param movimento
	 * @param statoOccIniziale
	 * @param movimentiAperti
	 * @param requestContainer
	 * @param request
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean cessazioneTD(SourceBean cessazione, SourceBean movimento,
			StatoOccupazionaleBean statoOccIniziale, Vector movimentiAperti, RequestContainer requestContainer,
			SourceBean request, TransactionQueryExecutor transExec, LogBatch logBatch) throws Exception {

		Object prgMovGestito = null;
		String dataInizio = (String) movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataFine = "31/12/" + DateUtils.getAnno(dataInizio);
		String cdnLavoratore = MovimentoBean.getCdnLavoratore(movimento);
		if (movimentiAperti == null) {
			movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
			movimentiAperti = togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
		}
		if (cessazione == null)
			cessazione = Controlli.creaCessazione(movimento);
		if (statoOccIniziale == null)
			statoOccIniziale = DBLoad.getStatoOccupazionale(MovimentoBean.getCdnLavoratore(movimento), transExec);
		// per le cessazioni a tempo determinato (il secondo controllo sembra inutile)
		if ((Contratto.DIP_TD == Contratto.getTipoContratto(movimento))
				|| !Controlli.cessazioneAnticipata(movimento, cessazione)) {

			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, /* dataInizio, */ transExec);
			Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
			Vector dids = DBLoad.getDichiarazioniDisponibilita(cdnLavoratore, "01/01/0001" /* dataIizio */, transExec);

			if (cessazione != null) {
				Controlli.inserisciCessazione(movimentiAperti, cessazione);
			}
			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti,
					dids, dataInizio, transExec, requestContainer, logBatch);

			// recupero eventuale lista mobilita del lavoratore
			ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
			Vector rowsMobilita = mobilita.getMobilita();

			Controlli.controllaMovimentoProTraSenzaPrec(movimento);
			movimento = Controlli.recuperaAvvDaProTra(movimento, movimentiAperti, transExec);
			// SE CI SONO MOVIMENTI APERTI CON DATA MINORE, ALLORA RIPARTO DAL PRIMO MOVIMENTO APERTO
			String dataInizioMov = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			if (movimentiAperti != null) {
				movimento = Controlli.selezionaMovimentoApertoIniziale(dataInizioMov, movimento, movimentiAperti);
			}
			// controlla se il movimento restituito dal metodo selezionaMovimentoApertoIniziale è una
			// proroga o trasformazione (controllo sui precedenti) e nel caso in cui il movimento selezionato
			// è una proroga o trasformazione con il precedente, allora devo ripartire dall'avviamento
			Controlli.controllaMovimentoProTraSenzaPrec(movimento);
			movimento = Controlli.recuperaAvvDaProTra(movimento, movimentiAperti, transExec);

			String dataControlloMobilita = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataControlloMobilita,
					sitAmm.getPrgMobilitaRicalcolo());
			if (sbMobilitaMov != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
				// DATA INIZIO DEL MOVIMENTO
				sitAmm.ricreaDaMobilita(sbMobilitaMov);
			} else {
				// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
				// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
				SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali,
						dataControlloMobilita);
				if (sbStatoOccAperto != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(movimento);
				} else {
					SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataControlloMobilita);
					if (sbMobilita != null) {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
						sitAmm.ricreaDaMobilita(sbMobilita);
					} else {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
						sitAmm.ricrea(movimento);
					}
				}
			}

			statoOccIniziale = sitAmm.getStatoOccupazionaleAperto();
		}
		return statoOccIniziale;
	}

	/**
	 * 
	 * @param trasformazione
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean trasformazione(SourceBean trasformazione, RequestContainer requestContainer,
			SourceBean response, TransactionQueryExecutor transExec) throws Exception {
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		StatoOccupazionaleBean statoOccupazionaleIniziale = null;
		// recupero del contesto operativo
		String contestoOperativo = trasformazione.containsAttribute("CONTESTO_OPERATIVO")
				? trasformazione.getAttribute("CONTESTO_OPERATIVO").toString()
				: "";
		// funzione warning sui dati del movimento (durata, reddito)
		MostraInfoMovimento(trasformazione, requestContainer);
		String cdnLavoratore = MovimentoBean.getCdnLavoratore(trasformazione);
		// recupero eventuale lista mobilita del lavoratore
		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
		Vector rowsMobilita = mobilita.getMobilita();

		if (rowsMobilita.size() > 0 || Controlli.trasformazioneImpattante(trasformazione)) {
			// leggere il movimento collegato
			SourceBean movPrec = Controlli.movimentoPrecedenteCollegato(trasformazione, transExec);
			// lo considero un movimento cessato naturalmente per scadenza dei termini,
			// e non un movimento cessato anticipatamente
			// avviare la procedura di cessazione
			String dataInizio = (String) trasformazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			String dataFine = "31/12/" + DateUtils.getAnno(dataInizio);

			Vector movimentiAperti = null;

			movimentiAperti = DBLoad.getMovimentiLavoratore(trasformazione.getAttribute(MovimentoBean.DB_CDNLAVORATORE),
					transExec);
			movimentiAperti = togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
			// normalizzo la trasformazione in inserimento
			SourceBean avviamentoVirtuale = Controlli.normalizzaTrasformazione(trasformazione);
			// aggiungo alla lista dei movimentiAperti il movimento normalizzato
			Controlli.aggiungiMovimento(movimentiAperti, avviamentoVirtuale);
			// normalizzo il movimento precedente
			SourceBean movTrasformato = Controlli.normalizza(movPrec, trasformazione, movimentiAperti, transExec);

			dataInizio = (String) movTrasformato.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, /* dataInizio, */ transExec);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore,
					"01/01/0001"/* dataInizio */, transExec);
			Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
			// recupero il movimento da passare a ricrea (IL PRIMO MOVIMENTO
			// DELLA CATENA COLLEGATO ALLA TRASFORMAZIONE CHE SI STA INSERENDO)
			Trasformazione movPartenza = new Trasformazione(movTrasformato);
			SourceBean movIniziale = movPartenza.getAvviamentoStart(movTrasformato, movimentiAperti);
			String dataInizioAvv = movIniziale.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti,
					dids, dataInizioAvv, transExec, requestContainer);

			SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataInizioAvv,
					sitAmm.getPrgMobilitaRicalcolo());
			if (sbMobilitaMov != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
				// DATA INIZIO DEL MOVIMENTO
				sitAmm.ricreaDaMobilita(sbMobilitaMov);
			} else {
				// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
				// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
				SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali, dataInizioAvv);
				if (sbStatoOccAperto != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(movIniziale);
				} else {
					SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataInizioAvv);
					if (sbMobilita != null) {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
						sitAmm.ricreaDaMobilita(sbMobilita);
					} else {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
						sitAmm.ricrea(movIniziale);
					}
				}
			}
			nuovoStatoOccupazionale = sitAmm.getStatoOccupazionale(trasformazione);
			// alert stato occupazionale se non sono in validazione massiva
			if (!contestoOperativo.equalsIgnoreCase(MovimentoBean.CONTESTO_VALIDAZIONE_MASSIVA)) {
				impostaAlert(sitAmm, requestContainer.getServiceRequest(), cdnLavoratore);
			}
		} else {
			nuovoStatoOccupazionale = DBLoad.getStatoOccupazionale(MovimentoBean.getCdnLavoratore(trasformazione),
					transExec);
		}
		return nuovoStatoOccupazionale;
	}

	/**
	 * Viene chiamato dai bacth sui movimenti futuri quando il movimento che si sta processando è una trasformazione.
	 * 
	 * @param trasformazione
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean trasformazioneDaBatchFuturi(SourceBean trasformazione,
			RequestContainer requestContainer, SourceBean response, TransactionQueryExecutor transExec,
			LogBatch logBatch) throws Exception {
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		String dataInizio = "";
		String dataInizioTrasformazione = (String) trasformazione.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		String dataInizioMovPrec = "";

		String cdnLavoratore = MovimentoBean.getCdnLavoratore(trasformazione);
		// recupero eventuale lista mobilita del lavoratore
		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
		Vector rowsMobilita = mobilita.getMobilita();

		if (rowsMobilita.size() > 0 || Controlli.trasformazioneImpattante(trasformazione)) {
			// leggere il movimento collegato
			SourceBean movPrec = Controlli.movimentoPrecedenteCollegato(trasformazione, transExec);
			String dataFine = "31/12/" + DateUtils.getAnno(dataInizioTrasformazione);

			Vector movimentiAperti = null;
			movimentiAperti = DBLoad.getMovimentiLavoratore(trasformazione.getAttribute(MovimentoBean.DB_CDNLAVORATORE),
					transExec);
			movimentiAperti = togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
			// normalizzo il movimento precedente
			SourceBean movTrasformato = null;
			if (movPrec != null) {
				dataInizioMovPrec = (String) movPrec.getAttribute(MovimentoBean.DB_DATA_INIZIO);
				if (DateUtils.compare(dataInizioTrasformazione, dataInizioMovPrec) < 0) {
					throw new Exception(
							"La data inizio trasformazione deve essere successiva alla data di inizio del movimento precedente.");
				}
				movTrasformato = Controlli.normalizza(movPrec, trasformazione, movimentiAperti, transExec);
			} else {
				movTrasformato = trasformazione;
			}
			dataInizio = (String) movTrasformato.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, /* dataInizio, */ transExec);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore,
					"01/01/0001"/* dataInizio */, transExec);
			Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
			// recupero il movimento da passare a ricrea (IL PRIMO MOVIMENTO
			// DELLA CATENA COLLEGATO ALLA TRASFORMAZIONE CHE SI STA INSERENDO)
			Trasformazione movPartenza = new Trasformazione(movTrasformato);
			SourceBean movIniziale = movPartenza.getAvviamentoStart(movTrasformato, movimentiAperti);
			String dataInizioAvv = movIniziale.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();

			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti,
					dids, dataInizioAvv, transExec, requestContainer, logBatch);

			SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataInizioAvv,
					sitAmm.getPrgMobilitaRicalcolo());
			if (sbMobilitaMov != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
				// DATA INIZIO DEL MOVIMENTO
				sitAmm.ricreaDaMobilita(sbMobilitaMov);
			} else {
				// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
				// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
				SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali, dataInizioAvv);
				if (sbStatoOccAperto != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(movIniziale);
				} else {
					SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataInizioAvv);
					if (sbMobilita != null) {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
						sitAmm.ricreaDaMobilita(sbMobilita);
					} else {
						// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
						sitAmm.ricrea(movIniziale);
					}
				}
			}

			nuovoStatoOccupazionale = sitAmm.getStatoOccupazionale(trasformazione);
		} else {
			nuovoStatoOccupazionale = DBLoad.getStatoOccupazionale(MovimentoBean.getCdnLavoratore(trasformazione),
					transExec);
		}
		return nuovoStatoOccupazionale;
	}

	/**
	 * 
	 * @param movimento
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean proroga(SourceBean movimento, RequestContainer requestContainer,
			SourceBean response, TransactionQueryExecutor transExec) throws Exception {
		// recupero del contesto operativo
		String contestoOperativo = movimento.containsAttribute("CONTESTO_OPERATIVO")
				? movimento.getAttribute("CONTESTO_OPERATIVO").toString()
				: "";
		// funzione warning sui dati del movimento (durata, reddito)
		MostraInfoMovimento(movimento, requestContainer);

		// leggere i movimenti collegati
		Object prgMovPrec = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		Vector proroghe = DBLoad.getMovimentiProroghe(prgMovPrec, transExec);
		if (proroghe.size() == 0)
			throw new ControlliException(MessageCodes.StatoOccupazionale.MOV_PROROGATO_NON_TROVATO);
		movimento.setAttribute("MOVIMENTI_PROROGATI", proroghe);
		movimento.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
				movimento.getAttribute(MovimentoBean.DB_DATA_FINE));

		StatoOccupazionaleBean statoOccupazionaleIniziale = null;
		SourceBean cm = null;
		SourceBean did = null;
		String dataInizio = null;
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		Object cdnLavoratore = null;
		cdnLavoratore = MovimentoBean.getCdnLavoratore(movimento);
		statoOccupazionaleIniziale = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);

		// leggo l'avviamento di partenza
		Proroga pro = new Proroga(movimento);
		MovimentoBean mAvvDaProroga = pro.getAvviamento(transExec);
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();
		if (Controlli.movimentoPrecedenteNormativa(movimento, dataNormativa297)) {
			// movimento precedente alla dataNormativa297; sola segnalazione e non blocco
			// dell'operazione in quanto gli impatti non avvengono.
			nuovoStatoOccupazionale = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(movimento);
			nuovoStatoOccupazionale.setChanged(false);
			if (response != null) {
				Vector paramV = new Vector(1);
				paramV.add(dataNormativa297);
				MessageAppender.appendMessage(response, MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_DATA_NORMATIVA,
						paramV);
			}
		}

		// Controllo se il movimento di proroga fa scattare o meno la ricostruzione storia.
		// Questa chiamata serve solo per la visualizzazione del messaggio finale,
		// in quanto la ricostruzione storia viene sempre eseguita.
		boolean ricostruisciStoria = ControlliRicostruzioneStoria.ricostruisciStoriaAvv(movimento,
				statoOccupazionaleIniziale, cdnLavoratore, transExec);

		dataInizio = (String) mAvvDaProroga.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		Vector movimentiAperti = null;
		Vector dids = null;
		Vector patti = null;
		Vector statiOccupazionali = null;
		statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
		dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", transExec);
		patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
		movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
		movimentiAperti = togliMovNonProtocollati(movimentiAperti);
		movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
		StatoOccupazionaleBean so = null;
		// AGGIUNGO LA PROROGA AL VETTORE DEI MOVIMENTI APERTI
		Controlli.inserisciCessazione(movimentiAperti, movimento);
		SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti, dids,
				dataInizio, transExec, requestContainer);

		// recupero eventuale lista mobilita del lavoratore
		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
		Vector rowsMobilita = mobilita.getMobilita();
		String dataControlloMobilita = mAvvDaProroga.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataControlloMobilita,
				sitAmm.getPrgMobilitaRicalcolo());
		if (sbMobilitaMov != null) {
			// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
			// DATA INIZIO DEL MOVIMENTO
			sitAmm.ricreaDaMobilita(sbMobilitaMov);
		} else {
			// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
			// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
			SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali,
					dataControlloMobilita);
			if (sbStatoOccAperto != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
				sitAmm.ricrea(mAvvDaProroga);
			} else {
				SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataControlloMobilita);
				if (sbMobilita != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
					sitAmm.ricreaDaMobilita(sbMobilita);
				} else {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(mAvvDaProroga);
				}
			}
		}

		so = sitAmm.getStatoOccupazionale(mAvvDaProroga);
		// alert stato occupazionale se non sono in validazione massiva
		if (!contestoOperativo.equalsIgnoreCase(MovimentoBean.CONTESTO_VALIDAZIONE_MASSIVA)) {
			if (ricostruisciStoria) {
				impostaAlert(sitAmm, requestContainer.getServiceRequest(), cdnLavoratore);
			} else {
				if (statoOccupazionaleIniziale.getStatoOccupazionale() != sitAmm.getStatoOccupazionaleAperto()
						.getStatoOccupazionale()) {
					requestContainer.getServiceRequest().setAttribute("STATO_OCC_APERTO",
							sitAmm.getStatoOccupazionaleAperto().getDescrizioneCompleta());
					ControlliRicostruzioneStoria.aggiungiAlert(statoOccupazionaleIniziale, so,
							requestContainer.getServiceRequest());
				}
			}
		}
		boolean pippo = false;
		if (pippo)
			throw new Exception("Eccezione per TEST");
		return so;
	}

	/**
	 * Viene chiamato dai bacth sui movimenti futuri quando il movimento che si sta processando è una proroga. Si è reso
	 * necessario perché il metodo usato quando si inserisce una proroga, recupera i movimenti del lavoratore e poi gli
	 * aggiunge il movimento in inserimento (che non è ancora presente sul db). Se utilizzassimo questo metodo nei
	 * batch, ci ritroveremo il movimento replicato nel vettore dei movimentiAperti (quando recupera i movimenti del
	 * lavoratore, recupera anche il movimento che il batch deve processare in quanto è già salvato sul db).
	 * 
	 * @param movimento
	 * @param requestContainer
	 * @param response
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static StatoOccupazionaleBean prorogaDaBatchFuturi(SourceBean movimento, RequestContainer requestContainer,
			SourceBean response, TransactionQueryExecutor transExec, LogBatch logBatch) throws Exception {

		// leggere i movimenti collegati
		Object prgMovPrec = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
		Vector proroghe = DBLoad.getMovimentiProroghe(prgMovPrec, transExec);
		if (proroghe.size() == 0)
			throw new ControlliException(MessageCodes.StatoOccupazionale.MOV_PROROGATO_NON_TROVATO);
		movimento.setAttribute("MOVIMENTI_PROROGATI", proroghe);
		movimento.updAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA,
				movimento.getAttribute(MovimentoBean.DB_DATA_FINE));

		StatoOccupazionaleBean statoOccupazionaleIniziale = null;
		SourceBean cm = null;
		SourceBean did = null;
		String dataInizio = null;
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		Object cdnLavoratore = null;
		cdnLavoratore = MovimentoBean.getCdnLavoratore(movimento);
		statoOccupazionaleIniziale = DBLoad.getStatoOccupazionale(cdnLavoratore, transExec);

		// leggo l'avviamento di partenza
		Proroga pro = new Proroga(movimento);
		MovimentoBean mAvvDaProroga = pro.getAvviamento();
		UtilsConfig utility = new UtilsConfig("AM_297");
		String dataNormativa297 = utility.getValoreConfigurazione();
		if (Controlli.movimentoPrecedenteNormativa(movimento, dataNormativa297)) {
			// movimento precedente alla dataNormativa297; sola segnalazione e non blocco
			// dell'operazione in quanto gli impatti non avvengono.
			nuovoStatoOccupazionale = StatoOccupazionaleBean.creaStatoOccupazionaleIniziale(movimento);
			nuovoStatoOccupazionale.setChanged(false);
			if (response != null) {
				Vector paramV = new Vector(1);
				paramV.add(dataNormativa297);
				MessageAppender.appendMessage(response, MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_DATA_NORMATIVA,
						paramV);
			}
		}

		dataInizio = (String) mAvvDaProroga.getAttribute(MovimentoBean.DB_DATA_INIZIO);
		Vector movimentiAperti = null;
		Vector dids = null;
		Vector patti = null;
		Vector statiOccupazionali = null;
		statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
		dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", transExec);
		patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", transExec);
		movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
		movimentiAperti = togliMovNonProtocollati(movimentiAperti);
		movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
		StatoOccupazionaleBean so = null;

		SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti, dids,
				dataInizio, transExec, requestContainer, logBatch);

		// recupero eventuale lista mobilita del lavoratore
		ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transExec);
		Vector rowsMobilita = mobilita.getMobilita();
		String dataControlloMobilita = mAvvDaProroga.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		SourceBean sbMobilitaMov = Controlli.eventoInMobilita(rowsMobilita, dataControlloMobilita,
				sitAmm.getPrgMobilitaRicalcolo());
		if (sbMobilitaMov != null) {
			// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA' APERTA RISPETTO ALLA
			// DATA INIZIO DEL MOVIMENTO
			sitAmm.ricreaDaMobilita(sbMobilitaMov);
		} else {
			// SE NELLA DATA INIZIO DEL MOVIMENTO RISULTA APERTO UNO STATO OCCUPAZIONALE
			// BISOGNA PROSEGUIRE DA QUELLO NELLA RICOSTRUZIONE STORIA
			SourceBean sbStatoOccAperto = Controlli.getStatoOccupazionaleAperto(statiOccupazionali,
					dataControlloMobilita);
			if (sbStatoOccAperto != null) {
				// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
				sitAmm.ricrea(mAvvDaProroga);
			} else {
				SourceBean sbMobilita = Controlli.getUltimaMobilita(rowsMobilita, dataControlloMobilita);
				if (sbMobilita != null) {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DALLA MOBILITA'
					sitAmm.ricreaDaMobilita(sbMobilita);
				} else {
					// AVVIO DELLA RICOSTRUZIONE STORIA A PARTIRE DAL MOVIMENTO
					sitAmm.ricrea(mAvvDaProroga);
				}
			}
		}

		so = sitAmm.getStatoOccupazionale(mAvvDaProroga);

		boolean pippo = false;
		if (pippo)
			throw new Exception("Eccezione per TEST");
		return so;
	}

	/**
	 * Imposta nella request i messaggi (che saranno inseriti nell' oggetto Alert) del cambio di stato occupazionale che
	 * sara' mostrato a video
	 * 
	 * @param stOcc
	 * @param request
	 * @throws Exception
	 */
	public static void impostaAlert(StatoOccupazionaleBean stOcc, SourceBean request) throws Exception {
		ArrayList alerts = null;
		if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE))
			alerts = (java.util.ArrayList) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
		else {
			alerts = new java.util.ArrayList();
			request.setAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE, alerts);
		}

		alerts.add("Stato occupazionale cambiato: \\n\\r" + stOcc.msgNuovoStato());
	}

	public static void impostaAlert(StatoOccupazionaleBean stOcc, StatoOccupazionaleBean stOccPrec, SourceBean request)
			throws Exception {
		ArrayList alerts = null;
		if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE)) {
			alerts = (java.util.ArrayList) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
			if (alerts.size() > 0)
				alerts.remove(alerts.size() - 1);
		} else {
			alerts = new java.util.ArrayList();
			request.setAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE, alerts);
		}
		if (stOcc.getStatoOccupazionale() != stOccPrec.getStatoOccupazionale())
			alerts.add("Stato occupazionale cambiato: \\n\\r" + stOcc.msgNuovoStato(stOccPrec));
	}

	/**
	 * Imposta nella request i messaggi (che saranno inseriti nell' Alert) dell' avvenuta ricostruzione della storia del
	 * lavoratore e le informazioni relative
	 * 
	 * @param sitAmm
	 * @param request
	 * @param cdnLavoratore
	 * @throws Exception
	 */
	public static void impostaAlert(SituazioneAmministrativa sitAmm, SourceBean request, Object cdnLavoratore)
			throws Exception {
		ArrayList alerts = null;
		boolean flag = true;
		boolean visualizzatoMess = false;
		StatoOccupazionaleBean statoOccupazionaleCorrente = null;
		statoOccupazionaleCorrente = DBLoad.getStatoOccupazionale(cdnLavoratore, sitAmm.getTxExecutor());
		SourceBean stOld = sitAmm.getStatoOccupazionaleApertoOld();
		if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE))
			alerts = (java.util.ArrayList) request.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
		else {
			alerts = new java.util.ArrayList();
			request.setAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE, alerts);
		}
		String msg = "Ricostruita storia del lavoratore\\n\\r";

		if (stOld != null) {
			if (!statoOccupazionaleCorrente.getAttribute("codStatoOccupaz").toString()
					.equals(stOld.getAttribute("codStatoOccupaz").toString())
					|| sitAmm.getStatiOccupazionaliCancellati().size() > 0) {

				int statoOccOld = StatoOccupazionaleBean.encode((String) stOld.getAttribute("codStatoOccupaz"));
				int statoOccCorrente = StatoOccupazionaleBean
						.encode((String) statoOccupazionaleCorrente.getAttribute("codStatoOccupaz"));

				if (statoOccCorrente == statoOccOld) {
					msg += "Lo stato occupazionale del lavoratore non e' cambiato";
					flag = false;
					visualizzatoMess = true;
				}
			}
		}
		if (flag) {
			msg += statoOccupazionaleCorrente.msgNuovoStato(sitAmm.getStatoOccupazionaleApertoOld());
		} else {
			if (!visualizzatoMess) {
				msg += "Lo stato occupazionale del lavoratore non e' cambiato";
			}
		}
		alerts.add(msg);
	}

	public static SourceBean selezionaMovimentoPerImpatti(SourceBean movimento) throws Exception {
		SourceBean movGestibile = null;
		if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("PRO")) {
			Proroga pro = new Proroga(movimento);
			MovimentoBean mAvvDaProroga = pro.getAvviamento();
			movGestibile = (SourceBean) mAvvDaProroga;
		} else {
			if (movimento.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("TRA")) {
				Trasformazione tra = new Trasformazione(movimento);
				MovimentoBean mAvvDaTrasf = tra.getAvvDaTrasformazione();
				movGestibile = (SourceBean) mAvvDaTrasf;
			} else {
				movGestibile = movimento;
			}
		}
		// per eventuali movimenti non collegati
		if (movGestibile == null) {
			movGestibile = movimento;
		}
		return movGestibile;
	}

	/**
	 * Visualizza le informazioni del movimento che si sta inserendo; in particolare durata e reddito del movimento(se
	 * stiamo inserendo una proroga, le informazioni visualizzate riguardano solo la proroga e non il movimento
	 * precedente
	 * 
	 * @param movimento
	 * @param requestContainer
	 * @throws Exception
	 */
	public static void MostraInfoMovimento(SourceBean movimento, RequestContainer requestContainer) throws Exception {
		String data150 = "";
		boolean gestione150 = false;
		String codTipoAvviamento = movimento.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
				? movimento.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
				: "";
		String dataInizioInfo = movimento.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
		// Durata e reddito del movimento visualizzato solo se il codice tipo avviamento
		// è diverso da Z.09.02 (codice vecchio RS3) (cessazione attività lavorativa dopo un periodo di sospeso per
		// contrazione)
		if (!codTipoAvviamento.equals("Z.09.02")) {
			// leggo dalla ts_generale se visualizzare info del movimento
			SourceBean sbGenerale = DBLoad.getInfoGenerali();
			String flagWarningDurata = "S";
			String flagWarningReddito = "S";
			if (sbGenerale != null) {
				if (sbGenerale.containsAttribute("FLGWARNINGMOVDURATA")) {
					flagWarningDurata = sbGenerale.getAttribute("FLGWARNINGMOVDURATA").toString();
				}
				if (sbGenerale.containsAttribute("FLGWARNINGMOVREDDITO")) {
					flagWarningReddito = sbGenerale.getAttribute("FLGWARNINGMOVREDDITO").toString();
				}
				if (sbGenerale.containsAttribute("DAT150")) {
					data150 = sbGenerale.getAttribute("DAT150").toString();
					if (!data150.equals("") && DateUtils.compare(dataInizioInfo, data150) >= 0) {
						gestione150 = true;
					}
				}
			}

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

			int nmesiLavoro;
			StatoOccupazionaleBean sOccInfoMov = new StatoOccupazionaleBean();
			Integer numMesiLavoro = null;
			int numeroGiorniCommerciali;
			int numeroGiorniCommercialiAnnoSucc;
			BigDecimal retribuzioneMov = null;
			BigInteger retribuzioneArrPrimoAnno = null;
			BigInteger retribuzioneArrAnnoSucc = null;
			BigDecimal retribuzione = null;
			BigDecimal retribuzioneMovAnnoSucc = null;

			String dataFineInfoPrimoAnno = "31/12/" + DateUtils.getAnno(dataInizioInfo);
			String dataFineInfo = movimento.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";

			if (flagWarningDurata.equals("S")) {
				if (movimento.getAttribute(MovimentoBean.DB_COD_MONO_TEMPO).toString().equals("D")) {
					// durata
					if (!gestione150) {
						nmesiLavoro = Controlli.numeroMesiDiLavoro(dataInizioInfo, dataFineInfo);
					} else {
						nmesiLavoro = Controlli.numeroMesiDiLavoroDecreto150(dataInizioInfo, dataFineInfo);
					}
					numMesiLavoro = new Integer(nmesiLavoro);
					warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
							MessageCodes.InfoMovimento.DURATA_MESI_MOVIMENTO, numMesiLavoro.toString() + " mesi"));
				} else {
					// durata
					if (!gestione150) {
						nmesiLavoro = Controlli.numeroMesiDiLavoro(dataInizioInfo, dataFineInfoPrimoAnno);
					} else {
						nmesiLavoro = Controlli.numeroMesiDiLavoroDecreto150(dataInizioInfo, dataFineInfoPrimoAnno);
					}
					numMesiLavoro = new Integer(nmesiLavoro);
					warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
							MessageCodes.InfoMovimento.DURATA_MESI_MOVIMENTO_PRIMO_ANNO,
							numMesiLavoro.toString() + " mesi"));
				}
			}

			if (flagWarningReddito.equals("S") && !gestione150) {
				// reddito primo anno
				if (!dataFineInfo.equals("") && DateUtils.getAnno(dataInizioInfo) == DateUtils.getAnno(dataFineInfo))
					numeroGiorniCommerciali = Controlli.getNumeroGiorniDiLavoro(dataInizioInfo, dataFineInfo);
				else
					numeroGiorniCommerciali = Controlli.getNumeroGiorniDiLavoro(dataInizioInfo, dataFineInfoPrimoAnno);

				if (movimento.containsAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA)
						&& !movimento.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA).toString().equals(""))
					retribuzione = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA);
				else
					retribuzione = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_RETRIBUZIONE);

				if (retribuzione != null) {
					sOccInfoMov.aggiorna(numeroGiorniCommerciali, retribuzione.doubleValue());
					retribuzioneMov = new BigDecimal(sOccInfoMov.getReddito());
					retribuzioneArrPrimoAnno = retribuzioneMov.toBigInteger();
					warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
							MessageCodes.InfoMovimento.REDDITO_MOVIMENTO_PRIMO_ANNO,
							retribuzioneArrPrimoAnno.toString() + " euro"));
					// eventuale reddito anno successivo
					if (dataFineInfo.equals("")
							|| DateUtils.getAnno(dataInizioInfo) != DateUtils.getAnno(dataFineInfo)) {
						if (dataFineInfo.equals("")) {
							numeroGiorniCommercialiAnnoSucc = GIORNI_COMMERCIALI_ANNO;
						} else {
							String dataInizioAnnoSucc = "01/01/" + DateUtils.getAnno(dataFineInfo);
							numeroGiorniCommercialiAnnoSucc = Controlli.getNumeroGiorniDiLavoro(dataInizioAnnoSucc,
									dataFineInfo);
						}
						sOccInfoMov.aggiornaAnnoSuccessivo(numeroGiorniCommercialiAnnoSucc, retribuzione.doubleValue());
						retribuzioneMov = new BigDecimal(sOccInfoMov.getRedditoAnnoSuccessivo());
						retribuzioneArrAnnoSucc = retribuzioneMov.toBigInteger();
						warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
								MessageCodes.InfoMovimento.REDDITO_MOVIMENTO_ANNO_SUCC,
								retribuzioneArrAnnoSucc.toString() + " euro"));
					}
				} else {
					warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
							MessageCodes.InfoMovimento.REDDITO_MOVIMENTO_NULLO, ""));
				}
			}
		}
	}
}
