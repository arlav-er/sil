package it.eng.myportal.entity.ejb;

import it.eng.myportal.cliclavoro.messaggio.Messaggio;
import it.eng.myportal.dtos.MsgContattoDTO;
import it.eng.myportal.entity.AcCandidatura;
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
import it.eng.myportal.entity.VaVacancyCl;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.MsgSoggettoClHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.enums.AzioneServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.DeStatoInvioCl;
import it.eng.myportal.utils.ConstantsSingleton.TipoSoggetto;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servizi per la gestione dei contatti lavoratore->azienda e
 * azienda->lavoratore
 * 
 * Si trovano i servizi da invocare quando un'azienda vuole contattare un
 * lavoratore.
 */
@Stateless
public class ClicLavoroCandidaturaEjb {

	protected final Log log = LogFactory.getLog(ClicLavoroCandidaturaEjb.class);

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
	private MsgSoggettoClHome msgSoggettoClHome;

	@EJB
	private MsgMessaggioHome msgMessaggioHome;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Crea il messaggio di primo contatto
	 * 
	 * @param acCandidatura
	 * @param cvCandidaturaCl
	 *            codice candidatura associato al cv con il quale ci si candida,
	 *            non è possibile recuperarlo in alcun modo pertanto lo passo
	 *            come parametro
	 * @param pfPrincipalInvio
	 * 
	 */
	public void creaMessaggioCandidatura(AcCandidatura acCandidatura, CvCandidaturaCl cvCandidaturaCl, PfPrincipal pfPrincipalInvio) {
		boolean checkNotificaBuild = false;
		boolean checkNotificaBuildPalese = false;
		VaDatiVacancy vaDatiVacancy = acCandidatura.getVaDatiVacancy();
		AziendaInfo aziendaInfo;
		String oggettoMessaggio;
		if (vaDatiVacancy != null) {
			if (vaDatiVacancy.getFlagAnonima()) {
				/*
				 * se la vacancy e' anonima il messaggio va inviato all'azienda
				 * principale, il CPI
				 */
				aziendaInfo = vaDatiVacancy.getPfPrincipal().getAziendaInfo();
			} else {
				/* Inserire nuova gestione per RER */
				if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
					if (vaDatiVacancy.getDeProvenienzaVacancy() != null) { // caso Provenienza MYPORTAL
						if (vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza()
								.equalsIgnoreCase(ConstantsSingleton.DeProvenienza.COD_MYPORTAL)) {
							// && datiVacancy.getPfPrincipal().getIdPfPrincipal() ==
							// candidatura.getPfPrincipal().getIdPfPrincipal() ) {
							checkNotificaBuild = true;
						} else { // caso Pubblicazione Anonima – Pubblicazione Anonima/Preselezione quando
									// provenienza SIL%
							if (vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza()
									.startsWith(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC)) {
								if (vaDatiVacancy.getDeEvasioneRich().getCodEvasione() != null
										&& !vaDatiVacancy.getDeEvasioneRich().getCodEvasione().isEmpty()) { // vacancy
																											// dotata di
																											// cod
																											// evasione
									if (vaDatiVacancy.getDeEvasioneRich().getCodEvasione()
											.equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_ANONIMA)
											|| vaDatiVacancy.getDeEvasioneRich().getCodEvasione().equalsIgnoreCase(
													ConstantsSingleton.Evasione.PUBB_ANONIMA_PRESELEZIONE)) {
										// invio solo al titolare dell'annuncio che è il CPI
										checkNotificaBuild = true;
									} else if (vaDatiVacancy.getDeEvasioneRich().getCodEvasione()
											.equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE)) {
										// invio del messaggio solo all'azienda ID_PF_PRINCIPAL_PALESE
										checkNotificaBuildPalese = true;
									} else if (vaDatiVacancy.getDeEvasioneRich().getCodEvasione()
											.equalsIgnoreCase(ConstantsSingleton.Evasione.PUBB_PALESE_PRESELEZIONE)) {
										// invio solo al titolare dell'annuncio che è il CPI
										checkNotificaBuild = true;
									}
								}
							}
						}
					}

					if (checkNotificaBuildPalese && vaDatiVacancy.getPfPrincipalPalese() != null) {
						// se la vacancy non e' anonima ed esiste l'azienda palese
						// collegata il messaggio va inviato a lei

						aziendaInfo = vaDatiVacancy.getPfPrincipalPalese().getAziendaInfo();

					} else if (checkNotificaBuild && vaDatiVacancy.getPfPrincipal() != null) {
						// se la vacancy non e' anonima e non esiste l'azienda
						// palese collegata il messaggio va inviato all'azienda
						// principale

						aziendaInfo = vaDatiVacancy.getPfPrincipal().getAziendaInfo();

					} else {
						// caso in cui non è anonima , non è palese non è palese preselezione --
						// dovrebbe essere un caso impossibile da verificarsi !!!
						aziendaInfo = vaDatiVacancy.getPfPrincipal().getAziendaInfo();
					}
				} else {
					// VECCHIA GESTIONE VALIDA PER LE ATRE REGIONI
					if (vaDatiVacancy.getPfPrincipalPalese() != null) {

						// se la vacancy non e' anonima ed esiste l'azienda palese
						// collegata il messaggio va inviato a lei

						aziendaInfo = vaDatiVacancy.getPfPrincipalPalese().getAziendaInfo();
					} else {

						// se la vacancy non e' anonima e non esiste l'azienda
						// palese collegata il messaggio va inviato all'azienda
						// principale

						aziendaInfo = vaDatiVacancy.getPfPrincipal().getAziendaInfo();
					}
				}

			}
			oggettoMessaggio = "Candidatura all'offerta di lavoro " + vaDatiVacancy.getAttivitaPrincipale();
		} else {
			aziendaInfo = acCandidatura.getPfPrincipal().getAziendaInfo();
			oggettoMessaggio = "Candidatura";
		}
		CvDatiPersonali cvDatiPersonali = acCandidatura.getCvDatiPersonali();
		PfPrincipal pfPrincipalFrom = pfPrincipalInvio;

		Date now = new Date();

		PfPrincipal pfPrincipalTo;
		// se la vacancy proviene da clicLavoro invio il messaggio all'utente
		// fittizio
		if ((vaDatiVacancy != null) && (vaDatiVacancy.isFromClicLavoro())) {
			// TODO utente fittizio clic lavoro
			pfPrincipalTo = pfPrincipalHome.getClicLavoro();
		}
		// altrimenti invio il messaggio all'azienda
		else {
			pfPrincipalTo = aziendaInfo.getPfPrincipal();
		}

		// crea il messaggio di primo contatto
		MsgMessaggio msgPrimoContatto = new MsgMessaggio();
		msgPrimoContatto.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO));
		msgPrimoContatto.setOggetto(oggettoMessaggio);
		msgPrimoContatto.setPfPrincipalFrom(pfPrincipalFrom);
		msgPrimoContatto.setPfPrincipalTo(pfPrincipalTo);
		msgPrimoContatto.setCorpo(acCandidatura.getCommento());
		msgPrimoContatto.setDtmIns(now);
		msgPrimoContatto.setDtmMod(now);
		msgPrimoContatto.setPfPrincipalIns(pfPrincipalFrom);
		msgPrimoContatto.setPfPrincipalMod(pfPrincipalFrom);
		entityManager.persist(msgPrimoContatto);
		msgPrimoContatto.setTicket("" + msgPrimoContatto.getIdMsgMessaggio());
		entityManager.merge(msgPrimoContatto);

		// aggiungi il contatto
		MsgContatto msgContatto = new MsgContatto();
		msgContatto.setMsgMessaggio(msgPrimoContatto);
		msgContatto.setIdMsgMessaggio(msgPrimoContatto.getIdMsgMessaggio());
		msgContatto.setAcCandidatura(acCandidatura);
		msgContatto.setDtmIns(now);
		msgContatto.setDtmMod(now);
		msgContatto.setPfPrincipalIns(pfPrincipalFrom);
		msgContatto.setPfPrincipalMod(pfPrincipalFrom);
		entityManager.persist(msgContatto);

		// se il messaggio deve essere inviato a clic lavoro crea anche il
		// record su msg_messaggio_cl e l'xml
		if ((vaDatiVacancy != null) && (vaDatiVacancy.isFromClicLavoro() && (cvCandidaturaCl != null))) {
			String codiceComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioCandidatura();
			UtenteInfo utenteInfo = pfPrincipalFrom.getUtenteInfo();

			VaContatto vaContatto = vaDatiVacancy.getVaContattoPrinc();
			VaVacancyCl vaVacancyCl = vaDatiVacancy.getVaVacancyCl();
			// questi due record devono essere presenti
			if (vaVacancyCl == null) {
				throw new MyPortalException("cliclavoro.novacancycl");
			}
			if (vaContatto == null) {
				throw new MyPortalException("cliclavoro.nocontatto");
			}
			it.eng.myportal.entity.decodifiche.DeStatoInvioCl deStatoInvioCl = deStatoInvioClHome.findById(DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO);

			// salvo i dati del mittente
			MsgSoggettoCl msgSoggettoMittente = new MsgSoggettoCl();
			msgSoggettoMittente.setCodiceFiscale(utenteInfo.getCodiceFiscale());
			msgSoggettoMittente.setCodComunicazione(cvCandidaturaCl.getCodComunicazione());
			msgSoggettoMittente.setDenominazione(cvDatiPersonali.getPfPrincipal().getNome() + "- " + cvDatiPersonali.getPfPrincipal().getCognome());
			msgSoggettoMittente.setDeComune(cvDatiPersonali.getDeComuneDomicilio());
			msgSoggettoMittente.setIndirizzo(cvDatiPersonali.getIndirizzoDomicilio());
			msgSoggettoMittente.setEmail(cvDatiPersonali.getEmail());
			if (StringUtils.isEmpty(cvDatiPersonali.getTel1())) {
				msgSoggettoMittente.setTelefono("0000");
			}
			else {
				String tel = cvDatiPersonali.getTel1();
				if (tel.length() > 15) {
					tel = (cvDatiPersonali.getTel1()).substring(0, 15);
				}
				msgSoggettoMittente.setTelefono(tel);
			}			

			msgSoggettoMittente.setDtmIns(now);
			msgSoggettoMittente.setDtmMod(now);
			msgSoggettoMittente.setPfPrincipalIns(pfPrincipalFrom);
			msgSoggettoMittente.setPfPrincipalMod(pfPrincipalFrom);
			entityManager.persist(msgSoggettoMittente);

			/* se c'e' un intermediario quello e' il destinatario, altrimenti e' l'azienda collegata alla vacancy */
			MsgSoggettoCl msgSoggettoDestinatario = new MsgSoggettoCl();
			if (StringUtils.isEmpty(vaDatiVacancy.getIntermediario())) {
				String codiceFiscaleSoggDest = aziendaInfo.getCodiceFiscale();
				String denominazioneSoggDest = aziendaInfo.getRagioneSociale();
				msgSoggettoDestinatario.setCodiceFiscale(codiceFiscaleSoggDest);   
				msgSoggettoDestinatario.setDenominazione(denominazioneSoggDest);
			} else {
				msgSoggettoDestinatario.setCodIntermediario(vaDatiVacancy.getIntermediario());
				
				// DONA 28/10/2013
				//servono solo per i NOT NULL sul DB 
				// da non spedire a CLICLAVORO
				String codiceFiscaleSoggDest = aziendaInfo.getCodiceFiscale();
				String denominazioneSoggAzienda = aziendaInfo.getRagioneSociale();
				String denominazioneSoggDestInterm = vaDatiVacancy.getDenominazioneIntermediario();
				if (StringUtils.isEmpty(vaDatiVacancy.getDenominazioneIntermediario())) {
					msgSoggettoDestinatario.setDenominazione(denominazioneSoggAzienda);
				}
				else {
					msgSoggettoDestinatario.setDenominazione(denominazioneSoggDestInterm);
				}
				msgSoggettoDestinatario.setCodiceFiscale(codiceFiscaleSoggDest);   
				
				
			}
			msgSoggettoDestinatario.setCodComunicazione(vaVacancyCl.getCodComunicazione());
			msgSoggettoDestinatario.setDeComune(vaDatiVacancy.getDeComune());
			msgSoggettoDestinatario.setIndirizzo(vaDatiVacancy.getIndirizzoLavoro());
			msgSoggettoDestinatario.setEmail(vaContatto.getMail());
			if (vaContatto.getTelRiferimentoPub() != null) {
				String telAz = vaContatto.getTelRiferimentoPub();
				if (telAz.length() > 15) {
					telAz = (vaContatto.getTelRiferimentoPub()).substring(0, 15);
				}
				msgSoggettoDestinatario.setTelefono(telAz);
			} else {
				String telAz = aziendaInfo.getTelefonoSede();
				if (telAz.length() > 15) {
					telAz = (aziendaInfo.getTelefonoSede()).substring(0, 15);
				}
				msgSoggettoDestinatario.setTelefono(telAz);
			}
			msgSoggettoDestinatario.setDtmIns(now);
			msgSoggettoDestinatario.setDtmMod(now);
			msgSoggettoDestinatario.setPfPrincipalIns(pfPrincipalTo);
			msgSoggettoDestinatario.setPfPrincipalMod(pfPrincipalTo);
			entityManager.persist(msgSoggettoDestinatario);

			// salvo il messaggio per clic lavoro
			MsgMessaggioCl msgMessaggioCl = new MsgMessaggioCl();
			msgMessaggioCl.setMsgMessaggio(msgPrimoContatto);
			msgMessaggioCl.setIdMsgMessaggio(msgPrimoContatto.getIdMsgMessaggio());
			msgMessaggioCl.setCodComunicazione(codiceComunicazione);
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
			entityManager.refresh(msgPrimoContatto);

			try {
				// crero l'xml da inviare a clic lavoro e lo metto in uscita
				Messaggio messaggioXml = clicLavoroMessaggioEjb.creaMessaggio(msgPrimoContatto);
				String xml = clicLavoroMessaggioEjb.convertToString(messaggioXml);
				ClInvioComunicazione clInvioComuniocazione = new ClInvioComunicazione();
				clInvioComuniocazione.setAzioneServizio(AzioneServizio.INVIO_MESSAGGIO);
				clInvioComuniocazione.setCodComunicazione(msgMessaggioCl.getCodComunicazione());
				clInvioComuniocazione.setDeStatoInvioCl(deStatoInvioCl);
				clInvioComuniocazione.setDestinatario(ConstantsSingleton.DeProvenienza.COD_MINISTERO);
				clInvioComuniocazione.setDtmIns(now);
				clInvioComuniocazione.setDtmMod(now);
				clInvioComuniocazione.setFileComunicazione(xml);
				clInvioComuniocazione.setFlagInviato(false);
				clInvioComuniocazione.setMittente("MYPORTAL");
				clInvioComuniocazione.setPfPrincipalIns(pfPrincipalFrom);
				clInvioComuniocazione.setPfPrincipalMod(pfPrincipalFrom);

				entityManager.persist(clInvioComuniocazione);
			} catch (Exception e) {
				throw new MyPortalException("cliclavoro.nomessaggio", e);
			}

		}
	}

	public void inviaRispostaCandidatura(MsgContattoDTO risposta, Integer idMsgCandidatura) {
		PfPrincipal from = pfPrincipalHome.findById(risposta.getIdFrom());
		MsgMessaggio candidatura = msgMessaggioHome.findById(idMsgCandidatura);
		creaRispostaCandidatura(from, candidatura, risposta.getCorpo(), risposta.getEsito());

	}

	/**
	 * Crea il messaggio da inviare al lavoratore in risposta alla candidatura
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
	private void creaRispostaCandidatura(PfPrincipal pfPrincipalFrom, MsgMessaggio msgCandidatura, String risposta, Boolean esito) {
		Date now = new Date();
		MsgContatto msgCandidaturaContatto = msgCandidatura.getMsgContatto();
		MsgMessaggioCl msgCandidaturaCl = msgCandidatura.getMsgMessaggioCl();
		MsgSoggettoCl msgCandidaturaMittente = null;
		MsgSoggettoCl msgCandidaturaDestinatario = null;
		PfPrincipal pfPrincipalTo;
		// se la candidatura proviene da clic lavoro, rispondo a clic lavoro,
		// altrimenti al lavoratore
		if (msgCandidaturaCl != null) {
			msgCandidaturaMittente = msgCandidaturaCl.getMittente();
			msgCandidaturaDestinatario = msgCandidaturaCl.getDestinatario();
			pfPrincipalTo = pfPrincipalHome.getClicLavoro();
		} else {
			pfPrincipalTo = msgCandidatura.getPfPrincipalFrom();
		}

		// crea il messaggio di risposta
		MsgMessaggio msgRisposta = new MsgMessaggio();
		msgRisposta.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.RISPOSTA_CONTATTO));
		msgRisposta.setOggetto("Re: " + msgCandidatura.getOggetto());
		msgRisposta.setTicket(msgCandidatura.getTicket());
		msgRisposta.setPfPrincipalFrom(pfPrincipalFrom);
		msgRisposta.setPfPrincipalTo(pfPrincipalTo);
		msgRisposta.setCorpo(risposta);
		msgRisposta.setDtmIns(now);
		msgRisposta.setDtmMod(now);
		msgRisposta.setPfPrincipalIns(pfPrincipalFrom);
		msgRisposta.setPfPrincipalMod(pfPrincipalFrom);
		msgRisposta.setPrecedente(msgCandidatura);
		entityManager.persist(msgRisposta);

		MsgContatto msgRispostaContatto = new MsgContatto();
		msgRispostaContatto.setMsgMessaggio(msgRisposta);
		msgRispostaContatto.setIdMsgMessaggio(msgRisposta.getIdMsgMessaggio());
		msgRispostaContatto.setAcCandidatura(msgCandidaturaContatto.getAcCandidatura());
		msgRispostaContatto.setFlagEsito(esito);
		msgRispostaContatto.setDtmIns(now);
		msgRispostaContatto.setDtmMod(now);
		msgRispostaContatto.setPfPrincipalIns(pfPrincipalFrom);
		msgRispostaContatto.setPfPrincipalMod(pfPrincipalFrom);
		entityManager.persist(msgRispostaContatto);

		// se deve essere inviato a clic lavoro crea anche il record su
		// msg_messaggio_cl e l'xml
		if (msgCandidaturaCl != null) {
			String codiceComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioCandidatura();
			it.eng.myportal.entity.decodifiche.DeStatoInvioCl deStatoInvioCl = deStatoInvioClHome.findById(DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO);

			// salvo i dati del mittente
			MsgSoggettoCl msgSoggettoMittente = msgSoggettoClHome.copy(msgCandidaturaDestinatario, pfPrincipalFrom);

			// salvo i dati del destinatario
			MsgSoggettoCl msgSoggettoDestinatario = msgSoggettoClHome.copy(msgCandidaturaMittente, pfPrincipalTo);

			MsgMessaggioCl msgMessaggioCl = new MsgMessaggioCl();
			msgMessaggioCl.setMsgMessaggio(msgRisposta);
			msgMessaggioCl.setIdMsgMessaggio(msgRisposta.getIdMsgMessaggio());
			msgMessaggioCl.setCodComunicazione(codiceComunicazione);
			msgMessaggioCl.setCodComunicazionePrec(msgCandidaturaCl.getCodComunicazione());
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
