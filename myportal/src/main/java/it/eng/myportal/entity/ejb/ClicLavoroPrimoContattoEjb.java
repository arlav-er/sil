package it.eng.myportal.entity.ejb;

import it.eng.myportal.cliclavoro.messaggio.Messaggio;
import it.eng.myportal.dtos.MsgContattoDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.PrimoContattoDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.CvCandidaturaCl;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.MsgContatto;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.MsgMessaggioCl;
import it.eng.myportal.entity.MsgSoggettoCl;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.MsgSoggettoClHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.enums.AzioneServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.DeStatoInvioCl;
import it.eng.myportal.utils.ConstantsSingleton.TipoSoggetto;

import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servizi per la gestione dei contatti azienda->lavoratore e
 * lavoratore->azienda
 * 
 * Si trovano i servizi da invocare quando un'azienda vuole contattare un
 * lavoratore.
 */   
@Stateless
public class ClicLavoroPrimoContattoEjb {

	protected final Log log = LogFactory.getLog(ClicLavoroPrimoContattoEjb.class);

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	private ClicLavoroEjb clicLavoroEjb;

	@EJB
	private ClicLavoroMessaggioEjb clicLavoroMessaggioEjb;

	@EJB
	private DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	private MsgSoggettoClHome msgSoggettoClHome;

	@EJB
	private MsgMessaggioHome msgMessaggioHome;

	@EJB
	NotificationBuilder notificationBuilder;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Servizio invocato quando l'azienda vuole contattare un lavoratore
	 * 
	 * @param primoContatto
	 *            il primo contatto effettuato
	 */
	public void sendPrimoContatto(PrimoContattoDTO primoContatto) {
		CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(primoContatto.getIdCvDatiPersonali());
		PfPrincipal pfPrincipalAzienda = pfPrincipalHome.findById(primoContatto.getIdPfPrincipalAzienda());
		VaDatiVacancy vaDatiVacancy = null;
		if (primoContatto.getIdVaDatiVacancy() != null) {
			vaDatiVacancy = vaDatiVacancyHome.findById(primoContatto.getIdVaDatiVacancy());
		}
		creaMessaggioPrimoContatto(pfPrincipalAzienda, cvDatiPersonali, primoContatto.getMessaggio(), vaDatiVacancy);
	}

	/**
	 * Crea il messaggio da inviare al lavoratore
	 * 
	 * @param pfPrincipalAzienda
	 *            azienda che contatta
	 * @param cvDatiPersonali
	 *            curriculum del lavoratore contattato
	 * @param corpo
	 *            corpo del messaggio inviato
	 * @param vaDatiVacancy
	 *            eventuale vacancy per la quale viene contattato il lavoratore
	 */
	private void creaMessaggioPrimoContatto(PfPrincipal pfPrincipalAzienda, CvDatiPersonali cvDatiPersonali,
			String corpo, VaDatiVacancy vaDatiVacancy) {
		AziendaInfo aziendaInfo = pfPrincipalAzienda.getAziendaInfo();

		PfPrincipal pfPrincipalFrom = pfPrincipalAzienda;
		Date now = new Date();

		PfPrincipal pfPrincipalTo;
		// se il cv proviene da clicLavoro invio il messaggio all'utente
		// fittizio
		if (cvDatiPersonali.isFromClicLavoro()) {
			pfPrincipalTo = pfPrincipalHome.getClicLavoro();
		} else {

			if (cvDatiPersonali.getPfPrincipalPalese() != null) {
				// se cv intermediato mando msg utente palese e notifica a
				// utente redazione
				pfPrincipalTo = pfPrincipalHome.findById(cvDatiPersonali.getPfPrincipalPalese().getIdPfPrincipal());
				Set<MsgMessaggioDTO> messaggi = notificationBuilder.buildNotificationCvIntermediato(cvDatiPersonali,
						aziendaInfo.getRagioneSociale());
				notificationBuilder.sendNotification(messaggi);
			} else {
				pfPrincipalTo = pfPrincipalHome.findById(cvDatiPersonali.getPfPrincipal().getIdPfPrincipal());
			}
		}

		// crea il messaggio di primo contatto
		MsgMessaggio msgPrimoContatto = new MsgMessaggio();
		msgPrimoContatto.setDeTipoMessaggio(deTipoMessaggioHome
				.findById(ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO));
		msgPrimoContatto.setOggetto("L'azienda " + aziendaInfo.getRagioneSociale() + " è interessata a contattarti");
		msgPrimoContatto.setPfPrincipalFrom(pfPrincipalFrom);
		msgPrimoContatto.setPfPrincipalTo(pfPrincipalTo);
		msgPrimoContatto.setCorpo(corpo);
		msgPrimoContatto.setDtmIns(now);
		msgPrimoContatto.setDtmMod(now);
		msgPrimoContatto.setPfPrincipalIns(pfPrincipalFrom);
		msgPrimoContatto.setPfPrincipalMod(pfPrincipalFrom);
		entityManager.persist(msgPrimoContatto);
		msgPrimoContatto.setTicket("" + msgPrimoContatto.getIdMsgMessaggio());
		entityManager.merge(msgPrimoContatto);

		MsgContatto msgContatto = new MsgContatto();
		msgContatto.setMsgMessaggio(msgPrimoContatto);
		msgContatto.setIdMsgMessaggio(msgPrimoContatto.getIdMsgMessaggio());
		msgContatto.setCvDatiPersonali(cvDatiPersonali);
		msgContatto.setDtmIns(now);
		msgContatto.setDtmMod(now);
		msgContatto.setPfPrincipalIns(pfPrincipalFrom);
		msgContatto.setPfPrincipalMod(pfPrincipalFrom);
		entityManager.persist(msgContatto);

		// se deve essere inviato a clic lavoro crea anche il record su
		// msg_messaggio_cl e l'xml
		if (cvDatiPersonali.isFromClicLavoro()) {
			this.invioClicLavoro(pfPrincipalAzienda, cvDatiPersonali, corpo, vaDatiVacancy, pfPrincipalTo,
					pfPrincipalFrom, msgPrimoContatto);
		}
	}

	private void invioClicLavoro(PfPrincipal pfPrincipalAzienda, CvDatiPersonali cvDatiPersonali, String corpo,
			VaDatiVacancy vaDatiVacancy, PfPrincipal pfPrincipalTo, PfPrincipal pfPrincipalFrom,
			MsgMessaggio msgPrimoContatto) {
		AziendaInfo aziendaInfo = pfPrincipalAzienda.getAziendaInfo();
		String codiceComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioCandidatura();

		UtenteInfo utenteInfo = null;
		// se c'è un utente palese è da inviare a lui e non alla provincia
		PfPrincipal pfPrincPalese = cvDatiPersonali.getPfPrincipalPalese();
		PfPrincipal pfPrincipalProp = cvDatiPersonali.getPfPrincipal();
		if (pfPrincPalese != null) {
			utenteInfo = pfPrincPalese.getUtenteInfo();
		} else {
			if (pfPrincipalProp.isUtente()) {
				utenteInfo = cvDatiPersonali.getPfPrincipal().getUtenteInfo();
			}
		}

		boolean fromVacancy = false;
		VaContatto vaContatto = null;
		if (vaDatiVacancy != null) {
			fromVacancy = true;
			vaContatto = vaDatiVacancy.getVaContattoPrinc();
		}

		it.eng.myportal.entity.decodifiche.DeStatoInvioCl deStatoInvioCl = deStatoInvioClHome
				.findById(DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO);

		// salvo i dati del mittente
		MsgSoggettoCl msgSoggettoMittente = new MsgSoggettoCl();
		msgSoggettoMittente.setCodiceFiscale(aziendaInfo.getCodiceFiscale());
		msgSoggettoMittente.setDenominazione(aziendaInfo.getRagioneSociale());
		if (fromVacancy) {
			msgSoggettoMittente.setIndirizzo(vaDatiVacancy.getIndirizzoLavoro());
			msgSoggettoMittente.setDeComune(vaDatiVacancy.getDeComune());
			msgSoggettoMittente.setCodComunicazione(vaDatiVacancy.getVaVacancyCl().getCodComunicazione());
			if (vaContatto != null) {
				if (vaContatto.getTelRiferimentoPub() != null
						&& !("").equalsIgnoreCase(vaContatto.getTelRiferimentoPub())) {
					msgSoggettoMittente.setTelefono(vaContatto.getTelRiferimentoPub());
				} else {
					msgSoggettoMittente.setTelefono("-");
				}
				if (vaContatto.getMail() != null && !("").equalsIgnoreCase(vaContatto.getMail())) {
					msgSoggettoMittente.setEmail(vaContatto.getMail());
				} 
			} else {
				throw new MyPortalException("cliclavoro.nocontatto");
			}
		} else {
			if (aziendaInfo.getIndirizzoSede() != null && !("").equalsIgnoreCase(aziendaInfo.getIndirizzoSede())) {
				msgSoggettoMittente.setIndirizzo(aziendaInfo.getIndirizzoSede());
			} else {
				msgSoggettoMittente.setIndirizzo("-");
			}
			msgSoggettoMittente.setDeComune(aziendaInfo.getDeComuneSede());

			if (aziendaInfo.getTelefonoSede() != null && !("").equalsIgnoreCase(aziendaInfo.getTelefonoSede())) {
				msgSoggettoMittente.setTelefono(aziendaInfo.getTelefonoSede());
			} else {
				msgSoggettoMittente.setTelefono("-");
			}			
			msgSoggettoMittente.setEmail(pfPrincipalAzienda.getEmail());			
		}
		Date now = new Date();
		msgSoggettoMittente.setDtmIns(now);
		msgSoggettoMittente.setDtmMod(now);
		msgSoggettoMittente.setPfPrincipalIns(pfPrincipalTo);
		msgSoggettoMittente.setPfPrincipalMod(pfPrincipalTo);
		entityManager.persist(msgSoggettoMittente);

		// salvo i dati del destinatario
		MsgSoggettoCl msgSoggettoDestinatario = new MsgSoggettoCl();
		// mettere solo codice intermediario se c'è
		String codiceFiscaleSoggDest = "00000000000";
		String denominazioneSoggDest = "Clic Lavoro";
		if (StringUtils.isEmpty(cvDatiPersonali.getIntermediario())) {
			if (utenteInfo != null && !StringUtils.isEmpty(utenteInfo.getCodiceFiscale())) {
				codiceFiscaleSoggDest = utenteInfo.getCodiceFiscale();
			}
			denominazioneSoggDest = new StringBuilder().append(cvDatiPersonali.getPfPrincipal().getNome()).append("- ").append(cvDatiPersonali.getPfPrincipal().getCognome()).toString();			
			msgSoggettoDestinatario.setDenominazione(denominazioneSoggDest);
		} else {
			msgSoggettoDestinatario.setCodIntermediario(cvDatiPersonali.getIntermediario());
			msgSoggettoDestinatario.setDenominazione(cvDatiPersonali.getDenominazioneIntermediario());
		}
		msgSoggettoDestinatario.setCodiceFiscale(codiceFiscaleSoggDest);		
		//

		CvCandidaturaCl cvCandidaturaCl = cvDatiPersonali.getCvCandidaturaCl();
		if (cvCandidaturaCl.getCodComunicazione() != null) {
			msgSoggettoDestinatario.setCodComunicazione(cvCandidaturaCl.getCodComunicazione());
		}

		msgSoggettoDestinatario.setDeComune(cvDatiPersonali.getDeComuneDomicilio());
		if (cvDatiPersonali.getIndirizzoDomicilio() != null) {
			msgSoggettoDestinatario.setIndirizzo(cvDatiPersonali.getIndirizzoDomicilio());
		} else if (utenteInfo != null && utenteInfo.getIndirizzoDomicilio() != null) {
			msgSoggettoDestinatario.setIndirizzo(utenteInfo.getIndirizzoDomicilio());
		} else {
			msgSoggettoDestinatario.setIndirizzo("-");
		}
		msgSoggettoDestinatario.setDtmIns(now);
		msgSoggettoDestinatario.setDtmMod(now);
		msgSoggettoDestinatario.setEmail(cvDatiPersonali.getEmail());
		msgSoggettoDestinatario.setPfPrincipalIns(pfPrincipalFrom);
		msgSoggettoDestinatario.setPfPrincipalMod(pfPrincipalFrom);
		if (cvDatiPersonali.getTel1() != null && !("").equalsIgnoreCase(cvDatiPersonali.getTel1())) {
			msgSoggettoDestinatario.setTelefono(cvDatiPersonali.getTel1());
		} else {
			msgSoggettoDestinatario.setTelefono("-");
		}

		entityManager.persist(msgSoggettoDestinatario);

		MsgMessaggioCl msgMessaggioCl = new MsgMessaggioCl();
		msgMessaggioCl.setMsgMessaggio(msgPrimoContatto);
		msgMessaggioCl.setIdMsgMessaggio(msgPrimoContatto.getIdMsgMessaggio());
		msgMessaggioCl.setCodComunicazione(codiceComunicazione);
		msgMessaggioCl.setDtmIns(now);
		msgMessaggioCl.setDtmMod(now);
		msgMessaggioCl.setPfPrincipalIns(pfPrincipalFrom);
		msgMessaggioCl.setPfPrincipalMod(pfPrincipalFrom);
		msgMessaggioCl.setCodTipoSoggettoMitt(TipoSoggetto.AZIENDA);
		msgMessaggioCl.setMittente(msgSoggettoMittente);
		msgMessaggioCl.setDestinatario(msgSoggettoDestinatario);
		msgMessaggioCl.setDeStatoInvioCl(deStatoInvioCl);
		entityManager.persist(msgMessaggioCl);

		entityManager.flush();
		entityManager.refresh(msgPrimoContatto);
		// NDRodi: Pegoraro recion
		try {
			Messaggio messaggioXml = clicLavoroMessaggioEjb.creaMessaggio(msgPrimoContatto);
			String xml = clicLavoroMessaggioEjb.convertToString(messaggioXml);
			ClInvioComunicazione clInvioComunicazione = new ClInvioComunicazione();
			clInvioComunicazione.setAzioneServizio(AzioneServizio.INVIO_MESSAGGIO);
			clInvioComunicazione.setCodComunicazione(msgMessaggioCl.getCodComunicazione());
			clInvioComunicazione.setDeStatoInvioCl(deStatoInvioCl);
			clInvioComunicazione.setDestinatario(ConstantsSingleton.DeProvenienza.COD_MINISTERO);
			clInvioComunicazione.setDtmIns(now);
			clInvioComunicazione.setDtmMod(now);
			clInvioComunicazione.setFileComunicazione(xml);
			clInvioComunicazione.setFlagInviato(false);
			clInvioComunicazione.setMittente("MYPORTAL");
			clInvioComunicazione.setPfPrincipalIns(pfPrincipalFrom);
			clInvioComunicazione.setPfPrincipalMod(pfPrincipalFrom);
			entityManager.persist(clInvioComunicazione);
		} catch (Exception e) {
			throw new MyPortalException("cliclavoro.nomessaggio", e);
		}

	}

	public void inviaRispostaPrimoContatto(MsgContattoDTO risposta, Integer idMsgPrimoContatto) {
		PfPrincipal from = pfPrincipalHome.findById(risposta.getIdFrom());
		MsgMessaggio primoContatto = msgMessaggioHome.findById(idMsgPrimoContatto);
		creaRispostaPrimoContatto(from, primoContatto, risposta.getCorpo(), risposta.getEsito());

	}

	/**
	 * Crea il messaggio da inviare all'azienda in risposta al primo contatto
	 * 
	 * @param pfPrincipalAzienda
	 *            azienda a cui rispondere
	 * @param cvDatiPersonali
	 *            curriculum del lavoratore contattato
	 * @param corpo
	 *            corpo del messaggio inviato
	 * @param vaDatiVacancy
	 *            eventuale vacancy legata al contatto
	 */
	private void creaRispostaPrimoContatto(PfPrincipal pfPrincipalFrom, MsgMessaggio msgPrimoContatto, String risposta,
			Boolean esito) {
		Date now = new Date();
		MsgContatto msgPrimoContattoContatto = msgPrimoContatto.getMsgContatto();
		MsgMessaggioCl msgPrimoContattoCl = msgPrimoContatto.getMsgMessaggioCl();

		PfPrincipal pfPrincipalTo;
		MsgSoggettoCl msgPrimoContattoMittente = null;
		MsgSoggettoCl msgPrimoContattoDestinatario = null;
		// se il primo contatto proviene da clic lavoro, rispondo a clic lavoro,
		// altrimenti all'azienda
		if (msgPrimoContatto.getMsgMessaggioCl() != null) {
			pfPrincipalTo = pfPrincipalHome.getClicLavoro();
			msgPrimoContattoMittente = msgPrimoContattoCl.getMittente();
			msgPrimoContattoDestinatario = msgPrimoContattoCl.getDestinatario();
		} else {
			pfPrincipalTo = msgPrimoContatto.getPfPrincipalFrom();
		}

		// crea il messaggio di risposta
		MsgMessaggio msgRisposta = new MsgMessaggio();
		msgRisposta.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.RISPOSTA_CONTATTO));
		msgRisposta.setOggetto("Re: " + msgPrimoContatto.getOggetto());
		msgRisposta.setTicket(msgPrimoContatto.getTicket());
		msgRisposta.setPfPrincipalFrom(pfPrincipalFrom);

		msgRisposta.setPfPrincipalTo(pfPrincipalTo);
		msgRisposta.setCorpo(risposta);
		msgRisposta.setDtmIns(now);
		msgRisposta.setDtmMod(now);
		msgRisposta.setPfPrincipalIns(pfPrincipalFrom);
		msgRisposta.setPfPrincipalMod(pfPrincipalFrom);
		msgRisposta.setPrecedente(msgPrimoContatto);
		entityManager.persist(msgRisposta);

		MsgContatto msgRispostaContatto = new MsgContatto();
		msgRispostaContatto.setMsgMessaggio(msgRisposta);
		msgRispostaContatto.setIdMsgMessaggio(msgRisposta.getIdMsgMessaggio());
		msgRispostaContatto.setCvDatiPersonali(msgPrimoContattoContatto.getCvDatiPersonali());
		msgRispostaContatto.setFlagEsito(esito);
		msgRispostaContatto.setDtmIns(now);
		msgRispostaContatto.setDtmMod(now);
		msgRispostaContatto.setPfPrincipalIns(pfPrincipalFrom);
		msgRispostaContatto.setPfPrincipalMod(pfPrincipalFrom);
		entityManager.persist(msgRispostaContatto);

		// se deve essere inviato a clic lavoro crea anche il record su
		// msg_messaggio_cl e l'xml
		if (msgPrimoContatto.getMsgMessaggioCl() != null) {
			String codiceComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioCandidatura();
			it.eng.myportal.entity.decodifiche.DeStatoInvioCl deStatoInvioCl = deStatoInvioClHome
					.findById(DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO);

			// salvo i dati del mittente
			MsgSoggettoCl msgSoggettoMittente = msgSoggettoClHome.copy(msgPrimoContattoDestinatario, pfPrincipalFrom);

			// salvo i dati del destinatario
			MsgSoggettoCl msgSoggettoDestinatario = msgSoggettoClHome.copy(msgPrimoContattoMittente, pfPrincipalTo);

			MsgMessaggioCl msgMessaggioCl = new MsgMessaggioCl();
			msgMessaggioCl.setMsgMessaggio(msgRisposta);
			msgMessaggioCl.setIdMsgMessaggio(msgRisposta.getIdMsgMessaggio());
			msgMessaggioCl.setCodComunicazione(codiceComunicazione);
			msgMessaggioCl.setCodComunicazionePrec(msgPrimoContattoCl.getCodComunicazione());
			msgMessaggioCl.setDtmIns(now);
			msgMessaggioCl.setDtmMod(now);
			msgMessaggioCl.setPfPrincipalIns(pfPrincipalFrom);
			msgMessaggioCl.setPfPrincipalMod(pfPrincipalFrom);
			msgMessaggioCl.setCodTipoSoggettoMitt(TipoSoggetto.CITTADINO);
			msgMessaggioCl.setMittente(msgSoggettoMittente);
			msgMessaggioCl.setDestinatario(msgSoggettoDestinatario);
			msgMessaggioCl.setDeStatoInvioCl(deStatoInvioCl);
			entityManager.persist(msgMessaggioCl);

			entityManager.flush();
			entityManager.refresh(msgRisposta);
			// NDRodi: Pegoraro recion
			try {
				Messaggio messaggioXml = clicLavoroMessaggioEjb.creaMessaggio(msgRisposta);
				String xml = clicLavoroMessaggioEjb.convertToString(messaggioXml);
				ClInvioComunicazione clInvioComunicazione = new ClInvioComunicazione();
				clInvioComunicazione.setAzioneServizio(AzioneServizio.INVIO_MESSAGGIO);
				clInvioComunicazione.setCodComunicazione(msgMessaggioCl.getCodComunicazione());
				clInvioComunicazione.setDeStatoInvioCl(deStatoInvioCl);
				clInvioComunicazione.setDestinatario(ConstantsSingleton.DeProvenienza.COD_MINISTERO);
				clInvioComunicazione.setDtmIns(now);
				clInvioComunicazione.setDtmMod(now);
				clInvioComunicazione.setFileComunicazione(xml);
				clInvioComunicazione.setFlagInviato(false);
				clInvioComunicazione.setMittente("MYPORTAL");
				clInvioComunicazione.setPfPrincipalIns(pfPrincipalFrom);
				clInvioComunicazione.setPfPrincipalMod(pfPrincipalFrom);
				entityManager.persist(clInvioComunicazione);
			} catch (Exception e) {
				throw new MyPortalException("cliclavoro.nomessaggio", e);
			}

		}
	}
}
