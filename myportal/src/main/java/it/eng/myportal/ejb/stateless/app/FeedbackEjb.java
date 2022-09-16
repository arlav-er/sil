package it.eng.myportal.ejb.stateless.app;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeTemaConsulenza;
import it.eng.myportal.entity.home.AppValutazioneHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeTemaConsulenzaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.rest.app.CheckerSec;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.EmptyParameterException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;
import it.eng.myportal.rest.app.exception.WrongParameterException;
import it.eng.myportal.rest.app.helper.AppUtils;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class FeedbackEjb implements Serializable {

	private static final long serialVersionUID = 3253913817162452810L;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeTemaConsulenzaHome deTemaConsulenzaHome;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	AppValutazioneHome appValutazioneHome;

	protected static Log log = LogFactory.getLog(FeedbackEjb.class);

	public String addAssistenza(String username, String codTipoTema, String messaggio) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {
				/*
				 * La tipologia messaggio per l'assistenza è esperto, eventualmente si potrà definire una categoria
				 * personalizzata per l'app.
				 */
				String codTipoMessaggio = it.eng.myportal.utils.ConstantsSingleton.App.MSG_RICHIESTA_ASSISTENZA;

				// L'oggetto non è esposto
				String oggetto = ConstantsSingleton.App.MSG_RICHIESTA_ASSISTENZA_OGGETTO;

				/*
				 * ------------------------------- Validazione ----------------------------------------
				 */
				validaRichiestaAssistenza(codTipoTema, oggetto, messaggio);

				/*
				 * ------------------------------- Map Entity e Persist -------------------------------
				 */
				MsgMessaggio nuovoMsg = this.fillMessaggio(codTipoMessaggio, codTipoTema, oggetto, messaggio,
						pfPrincipal);
				msgMessaggioHome.persist(nuovoMsg);

				// Set del ticket
				nuovoMsg.setTicket(nuovoMsg.getIdMsgMessaggio().toString());

				// Segno il messaggio come "letto" dal suo creatore.
				msgMessaggioHome.signAsRead(pfPrincipal.getIdPfPrincipal(), nuovoMsg.getIdMsgMessaggio(), true);

				/*
				 * -------------------------------Ret Json object -------------------------------------
				 */
				JSONObject obj = new JSONObject();
				obj.put(CheckerSec.STATUS, CheckerSec.OK);

				ret = obj.toString();

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il salvataggio della richiesta di assistenza");
		}
		return ret;
	}

	public String addValutazione(String username, Short numStelle, String messaggio) throws AppEjbException {

		String ret = null;

		try {

			PfPrincipal pfPrincipalMitt = null;

			if (username != null) {
				log.debug("Valutazione dell'utente " + username + ". " + numStelle + " stelle: " + " - Messaggio: "
						+ messaggio);
				pfPrincipalMitt = pfPrincipalHome.findByUsername(username);

				if (pfPrincipalMitt == null)
					throw new UserNotFoundException(username);

			} else {
				log.debug("Valutazione anonima. " + numStelle + " stelle: " + " - Messaggio: " + messaggio);
			}

			/*
			 * ------------------------------- Validazione ----------------------------------------
			 */
			validaRichiestaValutazione(numStelle, messaggio);

			// Le valutazioni vengono salvate in AppValutazione con utente amministratore (0)
			Integer idPfPrincipalAdmin = 0;
			PfPrincipal pfPrincipalAdmin = pfPrincipalHome.findById(idPfPrincipalAdmin);

			appValutazioneHome.persist(numStelle, messaggio, pfPrincipalMitt, pfPrincipalAdmin);

			/*
			 * -------------------------------Ret Json object -------------------------------------
			 */
			JSONObject obj = new JSONObject();
			obj.put(CheckerSec.STATUS, CheckerSec.OK);

			ret = obj.toString();

		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il salvataggio della valutazione");
		}
		return ret;
	}

	private void validaRichiestaAssistenza(String codTipoTema, String oggetto, String messaggio)
			throws AppEjbException {

		if (StringUtils.isBlank(oggetto))
			throw new EmptyParameterException("oggetto");
		else if (StringUtils.isBlank(messaggio))
			throw new EmptyParameterException("messaggio");

		// Validazione codTipoTema
		if (StringUtils.isBlank(codTipoTema))
			throw new EmptyParameterException("tipo");

		DeTemaConsulenza deTemaConsulenza = deTemaConsulenzaHome.findById(codTipoTema);

		Date oggi = Calendar.getInstance().getTime();
		if (deTemaConsulenza == null || !"Y".equals(deTemaConsulenza.getFlagCittadino())) {
			throw new WrongParameterException(codTipoTema + " - Codice tipo assistenza");
		} else if (oggi.before(deTemaConsulenza.getDtInizioVal()) || oggi.after(deTemaConsulenza.getDtFineVal())) {
			// Verifica tema valido
			throw new WrongParameterException(codTipoTema + " - Codice tipo assistenza");
		}
	}

	private void validaRichiestaValutazione(Short numStelle, String messaggio) throws AppEjbException {

		if (numStelle == null || numStelle < 1 || numStelle > 5) {
			throw new WrongParameterException("numStelle");
		}
	}

	private MsgMessaggio fillMessaggio(String codTipoMessaggio, String codTipoTema, String oggetto, String messaggio,
			PfPrincipal pfPrincipal) {
		MsgMessaggio nuovoMsg = new MsgMessaggio();
		nuovoMsg.setAtpConsulenza(null);
		nuovoMsg.setCorpo(StringUtils.abbreviate(messaggio, 4000));
		nuovoMsg.setCurricula(null);
		nuovoMsg.setDeProvinciaTo(AppUtils.getDeProvinciaRiferimento(pfPrincipal));
		nuovoMsg.setDeRuoloPortaleTo(null);

		if (codTipoTema != null)
			nuovoMsg.setDeTemaConsulenza(deTemaConsulenzaHome.findById(codTipoTema));
		if (codTipoMessaggio != null)
			nuovoMsg.setDeTipoMessaggio(deTipoMessaggioHome.findById(codTipoMessaggio));

		nuovoMsg.setDtScadenza(null);
		nuovoMsg.setIdMsgMessaggio(null);
		nuovoMsg.setInoltrante(null);
		nuovoMsg.setInoltrati(null);
		nuovoMsg.setLettere(null);
		nuovoMsg.setMsgAllegatos(null);
		nuovoMsg.setMsgContatto(null);
		nuovoMsg.setMsgMessaggioAtipico(null);
		nuovoMsg.setMsgMessaggioCl(null);

		// Si lascia null: viene persistita con setLetto()
		nuovoMsg.setMsgMessaggioLettos(null);

		nuovoMsg.setOggetto(StringUtils.abbreviate(oggetto, 250));
		nuovoMsg.setPfPrincipalFrom(pfPrincipal);
		nuovoMsg.setPfPrincipalIns(pfPrincipal);
		nuovoMsg.setPfPrincipalMod(pfPrincipal);
		nuovoMsg.setPfPrincipalTo(null);
		nuovoMsg.setPrecedente(null);
		nuovoMsg.setSuccessivo(null);
		// Ticket settato post persist
		nuovoMsg.setTicket(null);

		Date now = new Date();
		nuovoMsg.setDtmIns(now);
		nuovoMsg.setDtmMod(now);

		return nuovoMsg;
	}
}
