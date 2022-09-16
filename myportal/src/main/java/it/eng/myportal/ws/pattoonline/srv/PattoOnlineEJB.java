package it.eng.myportal.ws.pattoonline.srv;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.entity.PattoSil;
import it.eng.myportal.entity.PattoSilStorico;
import it.eng.myportal.entity.PfIdentityDevice;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.enums.StatoAccettazionePattoEnum;
import it.eng.myportal.entity.enums.TipoNotificaEnum;
import it.eng.myportal.entity.home.AppNotificaHome;
import it.eng.myportal.entity.home.PattoSilHome;
import it.eng.myportal.entity.home.PattoSilStoricoHome;
import it.eng.myportal.entity.home.PfIdentityDeviceHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.exception.MyPortalNotFoundException;
import it.eng.myportal.exception.MyPortalWsException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.ws.pattoonline.AccettazionePattoType.TipoAccettazioneEnum;
import it.eng.myportal.ws.pattoonline.PattoPortaleType;
import it.eng.myportal.ws.pattoonline.PattoType;
import it.eng.sil.base.utils.ConstantsBaseCommons;
import it.eng.sil.base.utils.DateUtils;
import it.eng.sil.base.utils.StringUtils;

@Stateless
public class PattoOnlineEJB {

	protected static Log log = LogFactory.getLog(PattoOnlineEJB.class);

	@EJB
	PattoSilHome pattoSilHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AppNotificaHome appNotificaHome;

	@EJB
	PfIdentityDeviceHome pfIdentityDeviceHome;

	@EJB
	PattoOnlineEJB pattoOnlineEJBHome;

	@EJB
	PattoSilStoricoHome pattoSilStoricoHome;

	/**
	 * 
	 * @param patto
	 * @param pdfPatto
	 * @return true if was a new patto
	 * @throws MyPortalWsException
	 */
	public boolean riceviPatto(PattoType patto, byte[] pdfPatto) throws MyPortalWsException {

		boolean hasCreatedNewPatto = false;
		if (StringUtils.isEmpty(patto.getCodServiziAmministrativi()))
			throw new MyPortalWsException("Codice servizi amministrativi mancante");
		// ignora eventuali trattini front-end
		patto.setCodServiziAmministrativi(patto.getCodServiziAmministrativi().replaceAll("-", ""));

		checkInput(patto);

		log.info("Inizio importazione patto " + patto.getCodServiziAmministrativi());
		log.info("Inizio importazione patto " + patto.getDataPatto());
		PfPrincipal targetCittadino;
		try {
			targetCittadino = pfPrincipalHome
					.findAbilitatoByCodServiziAmministrativi(patto.getCodServiziAmministrativi());
			PattoSil ptSil = pattoSilHome.findPatto(targetCittadino, patto);

			// JIRA ESL4SIL-32 - AGGIORNAMENTO PATTO
			if (ptSil != null) {
				// storicizzo solo se non ancora da accettare
				if (!ptSil.getCodStatoAccettazione().equals(StatoAccettazionePattoEnum.D))
					persistPattoStorico(targetCittadino, ptSil);

				// 2 Aggiornamento del record sul patto aggiornando
				ptSil.setPdfPatto(pdfPatto);
				ptSil.setTsInvio(new Date());
				ptSil.setCodStatoAccettazione(StatoAccettazionePattoEnum.D);
				ptSil.setTsAccettazione(null);
				ptSil.setTipoAccettazione(null);
				ptSil.setFlgPresaVisione(false);
				pattoSilHome.merge(ptSil);
			} else {
				// pattoSilHome persist patto&pdf
				pattoSilHome.savePatto(targetCittadino, pdfPatto, patto);
				hasCreatedNewPatto = true;
			}
			// Invio notifica informativa all'App forzando una transazione diversa
			try {
				pattoOnlineEJBHome.sendNotificaInfoPatto(targetCittadino);
			} catch (Exception e) {
				log.error(String.format(
						"Errore durante l'invio della notifica informativa relativa alla ricezione del patto proveniente da SIL per l'utente %s. Dettaglio: %s",
						targetCittadino.getUsername(), e.toString()));
			}
		} catch (MyPortalNotFoundException e) {
			throw new MyPortalWsException(
					"Cittadino non trovato per Codice amministrativo " + patto.getCodServiziAmministrativi());
		}
		return hasCreatedNewPatto;
	}

	public PattoPortaleType richiestaPatto(PattoType patto) throws MyPortalWsException {
		PattoPortaleType ret = new PattoPortaleType();
		if (StringUtils.isEmpty(patto.getCodServiziAmministrativi()))
			throw new MyPortalWsException("Codice servizi amministrativi mancante");
		// ignora eventuali trattini front-end
		patto.setCodServiziAmministrativi(patto.getCodServiziAmministrativi().replaceAll("-", ""));

		log.info("Cerco patto firmato per: " + patto.getCodServiziAmministrativi());
		PfPrincipal targetCittadino;
		try {
			targetCittadino = pfPrincipalHome
					.findAbilitatoByCodServiziAmministrativi(patto.getCodServiziAmministrativi());
			// pattoSilHome persist patto&pdf
			PattoSil ptSil = pattoSilHome.findPatto(targetCittadino, patto);
			if (ptSil == null)
				throw new MyPortalWsException("Patto non trovato per "
						+ (patto.getCodiceFiscale() != null ? patto.getCodiceFiscale().getValue()
								: patto.getCodServiziAmministrativi()));

			PattoType pattoFirmato = new PattoType();
			pattoFirmato.setAnnoProtocollo(new BigInteger("" + ptSil.getNumAnnoProtocollo()));
			pattoFirmato.setNumProtocollo(ptSil.getNumProtocollo());
			pattoFirmato.setCodiceFiscale(patto.getCodiceFiscale());
			pattoFirmato.setDataPatto(DateUtils.toXMLGregorianCalendar(ptSil.getDtPatto()));
			pattoFirmato.setCodProvinciaProv(ptSil.getDeProvincia().getCodProvincia());
			pattoFirmato.setCodServiziAmministrativi(patto.getCodServiziAmministrativi());
			// Accettazione
			if (ptSil.getTsAccettazione() != null) {
				it.eng.myportal.ws.pattoonline.AccettazionePattoType at = new it.eng.myportal.ws.pattoonline.AccettazionePattoType();
				at.setDtmAccettazione(DateUtils.toXMLGregorianCalendar(ptSil.getTsAccettazione()));
				TipoAccettazioneEnum tAcc = TipoAccettazioneEnum.OTP;
				switch (ptSil.getTipoAccettazione()) {
				case CIE:
					tAcc = TipoAccettazioneEnum.CIE;
					break;
				case OTP:
					tAcc = TipoAccettazioneEnum.OTP;
					break;
				case SMS:
					tAcc = TipoAccettazioneEnum.SMS;
					break;
				case SPID:
					tAcc = TipoAccettazioneEnum.SPID;
					break;
				default:
					log.info("GRAVE tipo accettazione non previsto: " + ptSil.getTipoAccettazione());
					break;
				}

				at.setTipoAccettazione(tAcc);
				ret.setAccettazionePatto(at);
			}

			ret.setPatto(pattoFirmato);

		} catch (MyPortalNotFoundException e) {
			throw new MyPortalWsException(
					"Cittadino non trovato per Codice amministrativo " + patto.getCodServiziAmministrativi());
		}
		return ret;

	}

	private void checkInput(PattoType patto) throws MyPortalWsException {
		if (patto == null)
			throw new MyPortalWsException("Patto nullo");
		if (patto.getCodiceFiscale() == null
				|| !Utils.controlloRegexMinisterialeCodiceFiscale(patto.getCodiceFiscale().getValue()))
			throw new MyPortalWsException("CF errato: " + patto.getCodiceFiscale().getValue());

		PfPrincipal targetCittadino;
		PattoSil ptSil;
		try {
			targetCittadino = pfPrincipalHome
					.findAbilitatoByCodServiziAmministrativi(patto.getCodServiziAmministrativi());

			UtenteInfo utenteInfo = utenteInfoHome.findById(targetCittadino.getIdPfPrincipal());
			if (utenteInfo == null)
				throw new MyPortalWsException(
						"GRAVE L'account non e` cittadino? IdPfPrincipal:" + targetCittadino.getIdPfPrincipal());
			if (!utenteInfo.getCodiceFiscale().equals(patto.getCodiceFiscale().getValue()))
				throw new MyPortalWsException(
						"Il CF non corrisponde a quello del cittadino: " + utenteInfo.getCodiceFiscale());

		} catch (MyPortalNotFoundException e) {
			throw new MyPortalWsException(
					"Cittadino non trovato per Codice amministrativo " + patto.getCodServiziAmministrativi());
		}
	}

	private void persistPattoStorico(PfPrincipal targetCittadino, PattoSil ptSil) {
		Date now = new Date();
		// 1 Storicizzare
		PattoSilStorico storico = new PattoSilStorico();
		storico.setCodStatoAccettazione(ptSil.getCodStatoAccettazione());
		storico.setDtmIns(ptSil.getDtmIns());
		storico.setIdPattoSil(ptSil.getIdPattoSil());
		storico.setTipoAccettazione(ptSil.getTipoAccettazione());
		storico.setTsAccettazione(ptSil.getTsAccettazione());
		storico.setTsInvio(ptSil.getTsInvio());
		storico.setDtmIns(now);
		storico.setDtmMod(now);
		storico.setPfPrincipalIns(targetCittadino);
		storico.setPfPrincipalMod(targetCittadino);

		pattoSilStoricoHome.persist(storico);

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AppNotifica sendNotificaInfoPatto(PfPrincipal targetCittadino) {
		// Se l'utente è in possesso dell'app si invia l'informativa relativa alla ricezione patto inviato da CPI

		AppNotifica appNotifica = null;

		List<PfIdentityDevice> devices = pfIdentityDeviceHome.findDevice(targetCittadino.getIdPfPrincipal());

		boolean hasApp = devices != null && !devices.isEmpty();

		if (hasApp) {

			MessageFormat formatter = new MessageFormat(ConstantsSingleton.App.PATTO_INFORMA_MSG);
			Object[] paramInformaMessaggio = { ConstantsSingleton.getNumOreMaxValidazionePatto() };
			String messaggio = formatter.format(paramInformaMessaggio);

			appNotifica = appNotificaHome.persistAndSend(ConstantsSingleton.App.PATTO_INFORMA_TITOLO,
					ConstantsSingleton.App.PATTO_INFORMA_SOTTOTITOLO, messaggio, null /* deliveryTimeOfDay */,
					targetCittadino.getIdPfPrincipal() /* utente destinatario */, TipoNotificaEnum.INFO_PATTO,
					ConstantsBaseCommons.Users.ADMINISTRATOR /* utente di inserimento */);

			log.info(String.format(
					"Notifica informativa relativa alla ricezione del patto proveniente da SIL per l'utente: %s, sid notifica: %s, si trova nello stato: %s",
					targetCittadino.getUsername(),
					appNotifica.getSidNotifica() != null ? appNotifica.getSidNotifica() : "",
					appNotifica.getStato().getDescrizione()));
		} else {
			log.info(String.format(
					"Notifica informativa relativa alla ricezione del patto proveniente da SIL NON INVIATA in quanto l'utente %s non utilizza l'App Lavoro per Te",
					targetCittadino.getUsername()));
		}

		return appNotifica;
	}
}
