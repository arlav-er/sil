/*
 * Creato il 7-ago-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.coop.InviaMigrazione;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

/**
 * @author giuliani
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InviaMigrazioneInCOOP implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaMigrazioneInCOOP.class.getName());

	// vettore delle warnings da restituire
	ArrayList warnings = new ArrayList();
	// Vettore dei risultati annidati da restituire
	ArrayList nested = new ArrayList();

	private TransactionQueryExecutor txExecutor;
	private User user;
	private String name = "";

	public InviaMigrazioneInCOOP(TransactionQueryExecutor txExecutor) {
		this.name = "Invia movimento in cooperazione";
		this.txExecutor = txExecutor;
		SessionContainer sc = RequestContainer.getRequestContainer().getSessionContainer();
		user = (User) sc.getAttribute(User.USERID);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.sil.module.movimenti.RecordProcessor#processRecord(java.util.Map)
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// Controllo se è attiva la l'interoperabilità
		// in caso contrario nonfaccio niente.
		String coopAttiva = System.getProperty("cooperazione.enabled");
		if ((coopAttiva == null) || (!coopAttiva.equalsIgnoreCase("true"))) {
			// Se NON sono in cooperazione non faccio nulla
			// Se la variabile di ambiente cooperazione.enabled non esiste
			// a default considero la cooperazione NON attiva
			return null;
		}

		// Per inviare un movimento in cooperazione il lavoratore deve essere
		// competente
		// di un CPI esterno alla provincia che abbia la cooperazione attiva. Il
		// CPI lo
		// reperisco sul DB e non prendo quello passato perché reputo le
		// informazioni locali
		// più attnedibili.
		BigDecimal cdnLavoratore = (BigDecimal) record.get("CDNLAVORATORE");

		Object[] obj;
		String codProvincia = InfoProvinciaSingleton.getInstance().getCodice();// (String)
																				// user.getCodRif();
		try {
			SourceBean row = null;

			// Controllo se devo mandare la migrazione e se la posso mandare in
			// cooperazione
			// (altrimenti si procede per i canali tradizionali => non si fa
			// niente)
			obj = new Object[3];
			obj[0] = cdnLavoratore;
			obj[1] = codProvincia;
			obj[2] = "MessageReceiver"; // Service da chiamare per tutti i
										// servizi ASINCRONI
			row = (SourceBean) txExecutor.executeQuery("CHECK_SEND_IN_COOP", obj, "SELECT");
			String tipoInvioCPI = StringUtils.getAttributeStrNotNull(row, "ROW.CODMONOTIPOFILE");
			String flgPoloAttivo = StringUtils.getAttributeStrNotNull(row, "ROW.FLGPOLOATTIVO");
			String codProvinOrig = StringUtils.getAttributeStrNotNull(row, "ROW.CODPROVINCIA");

			// Controllo se devo inviare la migrazione in COOPERAZIONE al CPI di
			// provenienza
			if (tipoInvioCPI.equalsIgnoreCase("C")) {
				if (flgPoloAttivo != null && flgPoloAttivo.equalsIgnoreCase("S")) {
					// Se il CPI a cui mandare il movimento è uno di quelli in
					// cooperazione
					// allora chiamo la classe che provvede a inviare la
					// migrazione
					SourceBean request = new SourceBean("SERVICE_REQUEST");
					SourceBean response = new SourceBean("SERVICE_RESPONSE");
					InviaMigrazione inviaMigrazione = new InviaMigrazione();

					String prgMovimento = ((BigDecimal) record.get("PRGMOVIMENTO")).toString();
					request.setAttribute("PRGMOVIMENTO", prgMovimento);
					request.setAttribute("codProvinciaDestinazione", codProvinOrig);

					try {
						inviaMigrazione.service(request, response, txExecutor, user);
					} catch (Exception e1) {
						it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName(), e1);

						warnings.add(new Warning(MessageCodes.EsportaMigrazioni.ERR_INVIO_MIGRAZ_IN_COOP, null));
						return ProcessorsUtils.createResponse(name, this.getClass().getName(), null, null, warnings,
								nested);
						// reportOperation.reportFailure(MessageCodes.EsportaMigrazioni.ERR_INVIO_MIGRAZ_IN_COOP);
					}
				} else {
					return ProcessorsUtils.createResponse(name, this.getClass().getName(),
							new Integer(MessageCodes.EsportaMigrazioni.ERR_POLONONATTIVO_IN_COOP), "", warnings, null);
				}
			}

		} catch (EMFInternalError ex) {
			_logger.debug(this.getClass().getName() + ex.toString());

			warnings.add(new Warning(MessageCodes.EsportaMigrazioni.ERR_INVIO_MIGRAZ_IN_COOP, null));
			return ProcessorsUtils.createResponse(name, this.getClass().getName(), null, null, warnings, nested);
		}

		return null;
	}

}