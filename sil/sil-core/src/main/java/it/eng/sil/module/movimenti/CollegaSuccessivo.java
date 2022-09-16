/*
 * Creato il 12-ott-04
 */
package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.ProTrasfoException;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativa;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * Modulo per il collegamento manuale dei movimenti
 * <p>
 * 
 * @author roccetti
 */
public class CollegaSuccessivo extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CollegaSuccessivo.class.getName());
	/** Informazioni di connessione col DB */
	String pool = null;

	public void service(SourceBean request, SourceBean response) throws Exception {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Controllo la coerenza delle date di inizio e fine dei movimenti da
		// collegare
		String inizioMovOrig = (String) request.getAttribute("datInizioMovOrig");
		SourceBean sbDatiMovPartenza = null;
		BigDecimal prgMovimentoPartenza = null;
		if (request.containsAttribute("prgMovimento") && !request.getAttribute("prgMovimento").toString().equals("")) {
			prgMovimentoPartenza = new BigDecimal((String) request.getAttribute("prgMovimento"));
		}
		String fineMovOrig = (String) request.getAttribute("datFineMovOrig");
		String inizioMovDest = (String) request.getAttribute("datInizioMovDest");
		String fineMovDest = (String) request.getAttribute("datFineMovDest");
		String fineMovOrigPerTrasfo = (fineMovOrig != null && !fineMovOrig.equals(""))
				? DateUtils.giornoSuccessivo(fineMovOrig)
				: "";
		String inizioMovOrigPerTrasfo = (inizioMovOrig != null && !inizioMovOrig.equals(""))
				? DateUtils.giornoSuccessivo(inizioMovOrig)
				: "";
		String codMonoTempoOrig = (String) request.getAttribute("codMonoTempoOrig");
		String codMonoTempoDest = (String) request.getAttribute("codMonoTempoDest");
		// D'Auria Giovanni 19/05/2005
		// Ho aggiunto il tipo del movimento di destinazione per poter
		// evitare che si colleghi una trasformazione o proroga a tempo
		// determinato
		String codTipoMovDest = (String) request.getAttribute("CODTIPOMOVDEST");

		Date datInizioOrig, datFineOrig, datInizioDest = null;
		Date datFineOrigPerTrasfo = null;
		Date datInizioOrigPerTrasfo = null;
		GregorianCalendar inizioOrig, fineOrig, inizioDest = null;
		GregorianCalendar fineOrigPerTrasfo = null;
		GregorianCalendar inizioOrigPerTrasfo = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			datInizioOrig = formatter.parse(inizioMovOrig);
			inizioOrig = new GregorianCalendar();
			inizioOrig.setTime(datInizioOrig);
			datInizioOrigPerTrasfo = formatter.parse(inizioMovOrigPerTrasfo);
			inizioOrigPerTrasfo = new GregorianCalendar();
			inizioOrigPerTrasfo.setTime(datInizioOrigPerTrasfo);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nel parsing della data: " + inizioMovOrig, e);

			reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_ORIG);
			return;
		}
		try {
			datInizioDest = formatter.parse(inizioMovDest);
			inizioDest = new GregorianCalendar();
			inizioDest.setTime(datInizioDest);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nel parsing della data: " + inizioMovDest, e);

			reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_DEST);
			return;
		}

		if (inizioDest.before(inizioOrig)) {
			reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_DEST_PRIMA_INIZIO_ORIG);
			return;
		}

		// DG 19/05/2005 inizio
		// Commentato su richiesta di Emanuela per poter controllare se senza
		// questo controllo ci sono problemi per gli impatti
		// if( (codTipoMovDest.equalsIgnoreCase("PRO") ||
		// codTipoMovDest.equalsIgnoreCase("TRA")) &&
		// (codMonoTempoDest.equalsIgnoreCase("D")) ){
		// reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_TRA_PRO_TD);
		// return;
		// }
		// fine

		// Se è un TD controllo anche rispetto alla data di fine del movimento
		// originale
		if (codMonoTempoOrig != null && codMonoTempoOrig.equalsIgnoreCase("D")) {
			try {
				datFineOrig = formatter.parse(fineMovOrig);
				fineOrig = new GregorianCalendar();
				fineOrig.setTime(datFineOrig);
				datFineOrigPerTrasfo = formatter.parse(fineMovOrigPerTrasfo);
				fineOrigPerTrasfo = new GregorianCalendar();
				fineOrigPerTrasfo.setTime(datFineOrigPerTrasfo);
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nel parsing della data: " + fineMovOrig, e);

				reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_FINE_ORIG);
				return;
			}

			// if (inizioDest.after(fineOrig)) {
			if (codTipoMovDest.equalsIgnoreCase("TRA")) {
				// Trasformazione a tempo determinato collegata a un tempo
				// determinato
				if (codMonoTempoDest.equalsIgnoreCase("D")) {
					if (inizioDest.after(fineOrig) || inizioDest.before(inizioOrigPerTrasfo)) {
						reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_DEST_DOPO_FINE_ORIG);
						return;
					}
				}
				// Trasformazione a tempo indeterminato collegata a un tempo
				// determinato
				else {
					if (inizioDest.after(fineOrigPerTrasfo) || inizioDest.before(inizioOrigPerTrasfo)) {
						reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_DEST_DOPO_FINE_ORIG);
						return;
					}
				}
			} else {
				if (codTipoMovDest.equalsIgnoreCase("PRO")) {
					// proroga collegata a un tempo determinato
					if (!inizioDest.equals(fineOrigPerTrasfo)) {
						reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_DEST_DOPO_FINE_ORIG);
						return;
					}
				} else {
					// cessazioni
					if (inizioDest.after(fineOrig)) {
						reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_DEST_DOPO_FINE_ORIG);
						return;
					}
				}
			}
		} else {
			if (codMonoTempoOrig == null || !codMonoTempoOrig.equalsIgnoreCase("I")) {
				_logger.debug("Errore nell'indicazione del codMonoTempo: " + String.valueOf(codMonoTempoOrig));

				reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_COD_MONO_TEMPO_ORIG);
				return;
			}
			// Trasformazione a tempo indeterminato collegata a un tempo
			// indeterminato
			if (codTipoMovDest.equalsIgnoreCase("TRA")) {
				if (inizioDest.before(inizioOrigPerTrasfo)) {
					reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_INIZIO_DEST_DOPO_FINE_ORIG);
					return;
				}
			}
		}

		// Controllo se come movimento successivo sto collegando una cessazione
		boolean collegamentoCess = StringUtils.getAttributeStrNotNull(request, "codTipoMovDest")
				.equalsIgnoreCase("CES");

		// Ricalcolo FlgModTempo per il mov destinazione
		if (codMonoTempoOrig.compareTo(codMonoTempoDest) != 0) {
			request.setAttribute("FlgModTempoDest", "S");
		} else if (codMonoTempoDest.equalsIgnoreCase("I")) {
			request.setAttribute("FlgModTempoDest", "N");
		} else {
			if (fineMovOrig != null && fineMovOrig.equalsIgnoreCase(fineMovDest)) {
				request.setAttribute("FlgModTempoDest", "N");
			} else
				request.setAttribute("FlgModTempoDest", "S");
		}

		// Ricalcolo FlgModReddito per il mov destinazione
		if (!collegamentoCess) {
			String redditoOrig = (String) request.getAttribute("decRetribuzioneMenOrig");
			String redditoDest = (String) request.getAttribute("decRetribuzioneMenDest");
			try {
				BigDecimal reddOrig = new BigDecimal(redditoOrig);
				BigDecimal reddDest = new BigDecimal(redditoDest);
				request.updAttribute("FlgModRedditoDest",
						(reddOrig.doubleValue() == reddDest.doubleValue() ? "N" : "S"));
			} catch (Exception e) {
				// Proseguo uguale senza segnalare nulla all'utente
			}
		}

		// Segnalazione soli errori/problemi
		disableMessageIdSuccess();
		disableMessageIdFail();

		// Transazione per i movimenti da collegare
		TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool());
		this.enableTransactions(trans);
		trans.initTransaction();

		if (prgMovimentoPartenza != null) {
			sbDatiMovPartenza = DBLoad.getMovimento(prgMovimentoPartenza, trans);
		}
		if (sbDatiMovPartenza != null && sbDatiMovPartenza.containsAttribute("codTipoMov")
				&& sbDatiMovPartenza.getAttribute("codTipoMov").toString().equalsIgnoreCase("CES")) {
			trans.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_CES_NO_SUCCESSIVO);
			return;
		}

		// Dopo ogni operazione raccoglie il risultato
		boolean result = true;
		// Aggiorno il movimento origine
		setSectionQueryUpdate((collegamentoCess ? "QUERY_UPDATE_ORIG_CES" : "QUERY_UPDATE_ORIG_TP"));
		result = doUpdate(request, response);
		if (!result) {
			trans.rollBackTransaction();
			reportOperation.reportSuccess(MessageCodes.CollegaMov.ERR_UPDATE_ORIG);
			return;
		}

		// aggiorno il movimento destinazione
		setSectionQueryUpdate("QUERY_UPDATE_DEST");
		result = doUpdate(request, response);
		if (!result) {
			trans.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_UPDATE_DEST);
			return;
		}

		// Ricalcolo dello stato occupazionale
		StatoOccupazionaleBean statoOccPrecAlCorrente = null;
		StatoOccupazionaleBean nuovoStatoOccupazionale = null;
		SituazioneAmministrativa sitAmm = null;
		String cdnLav = (String) request.getAttribute("cdnLavoratore");
		SourceBean records = new SourceBean("RECORDS");
		SourceBean record = new SourceBean("RECORD");
		int anno = 0;

		try {
			anno = DateUtils.getAnno(DateUtils.getNow());
			String dataIn = "01/01/" + anno;
			String dataFine = "31/12/" + anno;
			BigDecimal cdnLavoratore = new BigDecimal(cdnLav);

			// Considero tutti i movimenti del lavoratore
			Vector vect = DBLoad.getMovimentiLavoratore(cdnLavoratore, trans);
			vect = Controlli.togliMovimentoNonProt(vect);

			// recupero gli stati occupaz (compreso quello appena inserito), did
			// e patti del lavoratore
			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, trans);
			Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", trans);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", trans);
			// consente la forzatura in ricostruzione storia
			if (!request.containsAttribute("FORZA_INSERIMENTO")) {
				request.setAttribute("FORZA_INSERIMENTO", "true");
			} else {
				request.updAttribute("FORZA_INSERIMENTO", "true");
			}
			// consente la forzatura in ricostruzione storia per stati
			// occupazionali gestiti manualmente
			if (!request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			} else {
				request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
			}
			sitAmm = new SituazioneAmministrativa(vect, statiOccupazionali, patti, dids, inizioMovOrig, trans,
					RequestContainer.getRequestContainer());
			sitAmm.ricrea(DBLoad.getMovimento(request.getAttribute("prgMovimento"), trans));
		}

		catch (ProTrasfoException proTrasfEx) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella ricostruzione della storia.",
					(Exception) proTrasfEx);

			trans.rollBackTransaction();
			int code = 0;
			// comunque collego il movimento senza far scattare gli impatti
			if (!Controlli.eseguiImpattiInPresenzaMovOrfani()) {
				// Transazione per i movimenti da collegare
				TransactionQueryExecutor transNew = new TransactionQueryExecutor(getPool());
				this.enableTransactions(transNew);
				transNew.initTransaction();
				// Dopo ogni operazione raccoglie il risultato
				boolean resultNew = true;
				// Aggiorno il movimento origine
				setSectionQueryUpdate((collegamentoCess ? "QUERY_UPDATE_ORIG_CES" : "QUERY_UPDATE_ORIG_TP"));
				resultNew = doUpdate(request, response);
				if (!resultNew) {
					transNew.rollBackTransaction();
					reportOperation.reportSuccess(MessageCodes.CollegaMov.ERR_UPDATE_ORIG);
					return;
				}
				// aggiorno il movimento destinazione
				setSectionQueryUpdate("QUERY_UPDATE_DEST");
				resultNew = doUpdate(request, response);
				if (!resultNew) {
					transNew.rollBackTransaction();
					reportOperation.reportSuccess(MessageCodes.CollegaMov.ERR_UPDATE_DEST);
					return;
				}
				// Se è andato tutto bene termino con commit
				transNew.commitTransaction();
				code = proTrasfEx.getCode();
				if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
					reportOperation.reportSuccess(MessageCodes.CollegaMov.WARNING_PRO_NON_COLLEGATA);
				} else {
					if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
						reportOperation.reportSuccess(MessageCodes.CollegaMov.WARNING_TRASFO_TD_NON_COLLEGATA);
					}
				}
			} else {
				code = proTrasfEx.getCode();
				if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
					reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA);
				} else {
					if (code == MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA) {
						reportOperation.reportFailure(MessageCodes.CollegaMov.ERR_TRASFO_TD_NON_COLLEGATA);
					}
				}
			}
			return;
		}

		catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella ricostruzione della storia.", e);

			trans.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.StatoOccupazionale.ERRORE_GENERICO);
			return;
		}

		// Se è andato tutto bene termino con commit
		trans.commitTransaction();
		reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		return;
	}
}