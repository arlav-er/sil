package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.trasferimentoRamoAz.TrasferimentoRamoAzRequestParams;
import it.eng.sil.util.Utils;

/**
 * Modulo che esegue la validazione dei movimenti i cui prgMovimentoApp vengono passati come Vector di BigDecimal nella
 * ServiceRequest, se è presente l'attributo stringa VALIDATEALL al valore "true" viene estratta l'intera lista dalla
 * tabella AM_MOVIMENTO_APPOGGIO e vengono validati tutti i movimenti.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class ValidaMovimenti extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidaMovimenti.class.getName());
	/** Informazioni di connessione col DB */
	String pool = null;

	/**
	 * Costruttore
	 */
	public ValidaMovimenti() {
	}

	/**
	 * Servizio
	 * 
	 * @throws InterruptedException
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException, InterruptedException {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		pool = (String) getConfig().getAttribute("POOL");

		// Azione specificata dall'utente
		String azione = StringUtils.getAttributeStrNotNull(request, "AZIONE");

		// Se in sessione ho ancora una validazione massiva in atto
		SessionContainer sessione = getRequestContainer().getSessionContainer();
		if (sessione.getAttribute("VALIDATOREMASSIVOCORRENTE") != null) {
			// Guardo se devo fermarla
			if (azione.equalsIgnoreCase("arresta")) {
				try {
					Validator v = (Validator) sessione.getAttribute("VALIDATORCORRENTE");
					if (v != null) {
						v.stop();
					}
					reportOperation.reportSuccess(MessageCodes.LogOperazioniMovimenti.VALIDAZIONE_MASSIVA_INTERROTTA);
					return;
				} catch (NullPointerException e) {
					// Era già finita, lo segnalo all'utente
					reportOperation
							.reportFailure(MessageCodes.LogOperazioniMovimenti.VALIDAZIONE_MASSIVA_GIA_TERMINATA);
					return;
				}
			} else if (azione.equalsIgnoreCase("validaSelezionati") || azione.equalsIgnoreCase("validaTutti")
					|| azione.equalsIgnoreCase("validaFiltrati")) {
				reportOperation.reportFailure(MessageCodes.LogOperazioniMovimenti.VALIDAZIONE_MASSIVA_IN_ATTO);
				return;
			}
			// Guardo se devo iniziare una nuova validazione
		} else if (azione.equalsIgnoreCase("validaSelezionati") || azione.equalsIgnoreCase("validaTutti")
				|| azione.equalsIgnoreCase("validaFiltrati")) {
			sessione.delAttribute("RISULTATICORRENTI");

			// Caso di validazione di tutti i movimenti della tabella
			ArrayList prgMovAppArray = new ArrayList();
			if (azione.equalsIgnoreCase("validaTutti")) {
				// Estraggo l'intera lista dei prgMovimentoApp dalla tabella
				Object result = null;
				try {
					result = QueryExecutor.executeQuery("GET_PROGRESSIVI_MOVIMENTI_APPOGGIO", null, "SELECT",
							Values.DB_SIL_DATI);
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Errore nel recupero dei progressivi dei movimenti da validare. ", e);

					return;
				}

				// Esamino il risultato
				if (result instanceof SourceBean) {
					// Estraggo i progressivi ritrovati
					Vector v = ((SourceBean) result).getAttributeAsVector("ROW");
					for (int i = 0; i < v.size(); i++) {
						prgMovAppArray.add(((SourceBean) v.get(i)).getAttribute("PrgMovimentoApp"));
					}
				} else if (result instanceof Exception) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Errore nel recupero dei progressivi dei movimenti da validare. ", (Exception) result);

					return;
				} else {
					_logger.debug("Errore nel recupero dei progressivi dei movimenti da validare. ");
					return;
				}
			}
			// Caso di validazione dei movimenti selezionati
			else if (azione.equalsIgnoreCase("validaSelezionati")) {
				// Estraggo la lista dei prgMovimentoApp dalla Request
				Vector prgMovApp = request.getAttributeAsVector("ckeckboxMovimenti");
				if (prgMovApp != null) {
					Enumeration e = prgMovApp.elements();
					while (e.hasMoreElements()) {
						prgMovAppArray.add(new BigDecimal((String) e.nextElement()));
					}
				}
			} else if (azione.equalsIgnoreCase("validaFiltrati")) {
				// Estraggo la lista dei prgMovimentoApp dalla Request
				/*
				 * Vector prgMovApp = request.getAttributeAsVector("ckeckboxMovimenti"); if (prgMovApp != null) {
				 * Enumeration e = prgMovApp.elements();
				 */
				int i = 0;
				SourceBean sbMovFiltrato = null;
				Vector movimentiFiltrati = getServiceResponse().getAttributeAsVector("GETMOVIMENTIFILTRATI.ROWS.ROW");
				for (; i < movimentiFiltrati.size(); i++) {
					sbMovFiltrato = (SourceBean) movimentiFiltrati.get(i);
					prgMovAppArray.add((BigDecimal) sbMovFiltrato.getAttribute("PRGMOVAPP"));
				}
				// }
			}

			// Oggetto per validazione dei movimenti
			Validator validator = new Validator();

			// creo l'oggetto che esegue il log dei risultati
			ResultLogger resultLogger = null;
			BigDecimal prgValidazioneMassiva = null;

			try {
				resultLogger = new HtmlResultLogger();
				MultipleResultLogger dbLogger = new MultipleResultLogger(prgMovAppArray.size(), sessione);
				// può generare LogException
				prgValidazioneMassiva = dbLogger.getPrgValidazioneMassiva();

				resultLogger.addChildResultLogger(dbLogger); // (x)->Html->DB

			} catch (Exception e) {
				// Segnalo l'impossibilità di scrivere il log e ritorno
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile inizalizzare il logger per registrare i risultati", e);

				return;
			}

			// Creo l'oggetto per il recupero dei risultati
			ResultLogFormatter risultatiCorrenti = null;
			try {
				risultatiCorrenti = new ResultLogFormatter(prgValidazioneMassiva);
			} catch (Exception e) {
				// Segnalo l'impossibilità di istanziare l'oggetto per il
				// recupero del log
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile inizalizzare il logger per il recupero dei risultati", e);

				return;
			}

			// Creo il Validator che esegue come thread
			Thread t = new Thread(new BackGroundValidator(RequestContainer.getRequestContainer(), validator,
					resultLogger, prgMovAppArray), "VALIDATOREMASSIVO");

			// registro in sessione gli oggetti che effettuano l'importazione
			sessione.setAttribute("VALIDATOREMASSIVOCORRENTE", t);
			sessione.setAttribute("RISULTATICORRENTI", risultatiCorrenti);
			sessione.setAttribute("VALIDATORCORRENTE", validator);

			// Avvio la validazione massiva
			t.start();
			// Gestione della sincronizzazione nel caso di Trasferimento Ramo Azienda automatizzato:
			// aspetto la fine del thread in quanto ne viene lanciato uno alla volta.
			String automaticTRA = Utils.notNull(request.getAttribute(TrasferimentoRamoAzRequestParams.AUTOMATIC_TRA));
			if (automaticTRA.equalsIgnoreCase("TRUE")) {
				t.join();
			}
		}
	}
}