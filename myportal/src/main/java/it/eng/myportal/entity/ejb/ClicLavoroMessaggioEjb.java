package it.eng.myportal.entity.ejb;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.validation.Schema;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import it.eng.myportal.cliclavoro.messaggio.Corpo;
import it.eng.myportal.cliclavoro.messaggio.DatiSistema;
import it.eng.myportal.cliclavoro.messaggio.Destinatario;
import it.eng.myportal.cliclavoro.messaggio.Interlocutore;
import it.eng.myportal.cliclavoro.messaggio.Messaggio;
import it.eng.myportal.cliclavoro.messaggio.Mittente;
import it.eng.myportal.cliclavoro.messaggio.SiNo;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.CvCandidaturaCl;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.MsgContatto;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.MsgMessaggioCl;
import it.eng.myportal.entity.MsgSoggettoCl;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.TipoMessaggio;
import it.eng.myportal.utils.ConstantsSingleton.TipoSoggetto;
import it.eng.myportal.utils.Utils;

/**
 * EJB contenente i metodi per la generazione dei messaggi da inviare a
 * ClicLavoro
 * 
 * @author Rodi
 */
@Stateless
public class ClicLavoroMessaggioEjb {

	protected final Log log = LogFactory.getLog(this.getClass());

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;

	@EJB
	NotificationBuilder notificationBuilder;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Converte il messaggio in stringa xml
	 * 
	 * @param messaggio
	 *            messaggio da convertire
	 * @return il messaggio in foramto xml
	 * @throws JAXBException
	 *             in caso di errore
	 * @throws SAXException
	 *             in caso di errore
	 */
	public String convertToString(Messaggio messaggio) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(Messaggio.class);
		Marshaller marshaller = jc.createMarshaller();
		Schema schema = Utils.getXsdSchema("cliclavoro" + File.separator + "messaggio.xsd");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setSchema(schema);
		StringWriter writer = new StringWriter();
		marshaller.marshal(messaggio, writer);
		String xmlRichiesta = writer.getBuffer().toString();
		return xmlRichiesta;
	}

	/**
	 * Genera un messaggio a partire dalla stringa xml
	 * 
	 * @param xmlMessaggio
	 *            xml del messaggio
	 * @return l'oggetto
	 * @throws JAXBException
	 *             in caso di errore
	 */
	public Messaggio convertToMessaggio(String xmlMessaggio) throws JAXBException {
		JAXBContext jaxbContext;
		Messaggio messaggio = null;
		try {
			jaxbContext = JAXBContext.newInstance(Messaggio.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			messaggio = (Messaggio) jaxbUnmarshaller.unmarshal(new StringReader(xmlMessaggio));
		} catch (JAXBException e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return messaggio;
	}

	/**
	 * Esegue un test di invio messaggi verso ClicLavoro
	 */
	public void testInvio() {
		List<MsgMessaggio> msgContattos = msgMessaggioHome.findAll(ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
		for (MsgMessaggio msgMessaggio : msgContattos) {
			try {
				Messaggio messaggio = creaMessaggio(msgMessaggio);
				String stringa = convertToString(messaggio);
				log.info(stringa);
			} catch (Exception e) {
				log.error("Errore durante la creazione del messaggio: " + e.getMessage());
			}

		}

		msgContattos = msgMessaggioHome.findAll(ConstantsSingleton.MsgMessaggio.RISPOSTA_CONTATTO);
		for (MsgMessaggio msgMessaggio : msgContattos) {
			try {
				Messaggio messaggio = creaMessaggio(msgMessaggio);
				String stringa = convertToString(messaggio);
				log.info(stringa);
			} catch (Exception e) {
				log.error("Errore durante la creazione del messaggio: " + e.getMessage());
			}

		}

	}

	/**
	 * Crea il messaggio da inviare a clicLavoro
	 * 
	 * @param msgMessaggio
	 *            messaggio da cui partire
	 * @return il messaggio da inviare
	 */
	public Messaggio creaMessaggio(MsgMessaggio msgMessaggio) {
		boolean mittenteClicLavoro = false;
		boolean destinatarioClicLavoro = false;
		PfPrincipal utenteDestinatario = msgMessaggio.getPfPrincipalTo();
		if (ConstantsSingleton.USERNAME_CLICLAVORO_CANDIDATURE.equalsIgnoreCase(utenteDestinatario.getUsername())) {
			destinatarioClicLavoro = true;
		}
		else if (ConstantsSingleton.USERNAME_CLICLAVORO_VACANCY.equalsIgnoreCase(utenteDestinatario.getUsername())) {
			destinatarioClicLavoro = true;
		}
		
		PfPrincipal utenteMittente = msgMessaggio.getPfPrincipalFrom();
		if (ConstantsSingleton.USERNAME_CLICLAVORO_CANDIDATURE.equalsIgnoreCase(utenteMittente.getUsername())) {
			mittenteClicLavoro = true;
		}
		else if (ConstantsSingleton.USERNAME_CLICLAVORO_VACANCY.equalsIgnoreCase(utenteMittente.getUsername())) {
			mittenteClicLavoro = true;
		}
		
		MsgSoggettoCl mittente = msgMessaggio.getMsgMessaggioCl().getMittente();
		MsgSoggettoCl destinatario = msgMessaggio.getMsgMessaggioCl().getDestinatario();
		MsgMessaggioCl msgMessaggioCl = msgMessaggio.getMsgMessaggioCl();
		try {
			Messaggio messaggio = new Messaggio();
			messaggio.setMittente(buildMittente(mittenteClicLavoro, mittente, msgMessaggioCl.getCodTipoSoggettoMitt().toString()));
			messaggio.setDestinatario(buildDestinatario(destinatarioClicLavoro, destinatario));
			messaggio.setCorpo(buildCorpo(msgMessaggio));
			messaggio.setDatiSistema(buildDatiSistema(msgMessaggio));
			return messaggio;
		} catch (DatatypeConfigurationException e) {
			throw new MyPortalException("generic.error", e);
		}
	}

	/**
	 * Costruisce la parte di datiSistema del messaggio
	 * 
	 * @param msgMessaggio
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	protected DatiSistema buildDatiSistema(MsgMessaggio msgMessaggio) throws DatatypeConfigurationException {
		DatiSistema datiSistema = new DatiSistema();
		datiSistema.setTipocomunicazione(msgMessaggio.getDeTipoMessaggio().getCodTipoMessaggio());
		datiSistema.setData(Utils.toXMLGregorianCalendar(new Date()));
		datiSistema.setCodicemessaggio(msgMessaggio.getMsgMessaggioCl().getCodComunicazione());
		String codComunicazionePrec = msgMessaggio.getMsgMessaggioCl().getCodComunicazionePrec();
		if (codComunicazionePrec != null && !("").equals(codComunicazionePrec)) {
			datiSistema.setCodicemessaggioprecedente(codComunicazionePrec);
		}
		return datiSistema;
	}

	protected Corpo buildCorpo(MsgMessaggio msgMessaggio) {
		Corpo corpo = new Corpo();
		corpo.setTestomessaggio(msgMessaggio.getCorpo());
		return corpo;
	}

	protected Mittente buildMittente(boolean mittenteCliclavoro, MsgSoggettoCl mittenteCl, String tipoSoggetto) {
		Mittente mittente = new Mittente();
		mittente.setTiposoggetto(tipoSoggetto);
		Interlocutore interlocutore = buildInterlocutore(mittenteCliclavoro, mittenteCl);
		mittente.setInterlocutore(interlocutore);
		return mittente;
	}

	protected Destinatario buildDestinatario(boolean destinatarioCliclavoro, MsgSoggettoCl destinatarioCl) {
		Destinatario destinatario = new Destinatario();
		Interlocutore interlocutore = buildInterlocutore(destinatarioCliclavoro, destinatarioCl);
		destinatario.setInterlocutore(interlocutore);
		return destinatario;
	}

	/**
	 * Costruisce un interlocutore
	 * 
	 * @param msgSoggettoCl
	 * @return
	 */
	protected Interlocutore buildInterlocutore(boolean isCliclavoro, MsgSoggettoCl msgSoggettoCl) {
		Interlocutore interlocutore = new Interlocutore();
		if (!StringUtils.isEmpty(msgSoggettoCl.getCodIntermediario())) {
			interlocutore.setCodiceintermediario(msgSoggettoCl.getCodIntermediario().trim());
		}
		else {
			if (!isCliclavoro) {
				interlocutore.setCodicefiscale(msgSoggettoCl.getCodiceFiscale().trim());
				interlocutore.setDenominazione(msgSoggettoCl.getDenominazione().trim());			
				interlocutore.setIndirizzo(msgSoggettoCl.getIndirizzo().trim());
				interlocutore.setComune(msgSoggettoCl.getDeComune().getCodCom());			
				interlocutore.setTelefono(msgSoggettoCl.getTelefono().trim());
				interlocutore.setEmail(msgSoggettoCl.getEmail().trim());
			}
		}  		
		interlocutore.setCodiceoffertacandidatura(msgSoggettoCl.getCodComunicazione());
		return interlocutore;
	}

	/**
	 * Ricezione candidatura da PDD, inserisce il messaggio partendo dall'xml
	 * ricevuto
	 * 
	 * Questo metodo è un po' complesso
	 * 
	 * @param messaggio
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void riceviMessaggioCliclavoro(Messaggio messaggio) {
		log.info("Acquisizione messaggio con codice: " + messaggio.getDatiSistema().getCodicemessaggio());
		Date now = new Date();
		Mittente mitt = messaggio.getMittente();
		String tipoSoggettoMitt = mitt.getTiposoggetto();
		Interlocutore interlMitt = mitt.getInterlocutore();
		MsgSoggettoCl msgSoggettoClMitt = new MsgSoggettoCl();
		log.info("CF Mittente: " + interlMitt.getCodicefiscale());
		msgSoggettoClMitt.setCodiceFiscale(interlMitt.getCodicefiscale());
		msgSoggettoClMitt.setDenominazione(interlMitt.getDenominazione());
		msgSoggettoClMitt.setIndirizzo(interlMitt.getIndirizzo());
		msgSoggettoClMitt.setDeComune(deComuneHome.findById(interlMitt.getComune()));
		msgSoggettoClMitt.setCodComunicazione(interlMitt.getCodiceoffertacandidatura());
		msgSoggettoClMitt.setTelefono(interlMitt.getTelefono());
		msgSoggettoClMitt.setEmail(interlMitt.getEmail());
		msgSoggettoClMitt.setDtmIns(now);
		msgSoggettoClMitt.setPfPrincipalIns(pfPrincipalHome.getAdministrator());
		msgSoggettoClMitt.setDtmMod(now);
		msgSoggettoClMitt.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
		entityManager.persist(msgSoggettoClMitt);

		Destinatario dest = messaggio.getDestinatario();
		Interlocutore interlDest = dest.getInterlocutore();
		MsgSoggettoCl msgSoggettoClDest = new MsgSoggettoCl();
		log.info("CF Destinatario: " + interlDest.getCodicefiscale());
		msgSoggettoClDest.setCodiceFiscale(interlDest.getCodicefiscale());
		msgSoggettoClDest.setDenominazione(interlDest.getDenominazione());
		msgSoggettoClDest.setIndirizzo(interlDest.getIndirizzo());
		msgSoggettoClDest.setDeComune(deComuneHome.findById(interlDest.getComune()));
		msgSoggettoClDest.setCodComunicazione(interlDest.getCodiceoffertacandidatura());
		msgSoggettoClDest.setTelefono(interlDest.getTelefono());
		msgSoggettoClDest.setEmail(interlDest.getEmail());
		msgSoggettoClDest.setDtmIns(now);
		msgSoggettoClDest.setPfPrincipalIns(pfPrincipalHome.getAdministrator());
		msgSoggettoClDest.setDtmMod(now);
		msgSoggettoClDest.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
		entityManager.persist(msgSoggettoClDest);

		MsgMessaggio msgMessaggio = new MsgMessaggio();
		msgMessaggio.setDtmIns(now);
		msgMessaggio.setPfPrincipalIns(pfPrincipalHome.getAdministrator());
		msgMessaggio.setDtmMod(now);
		msgMessaggio.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
		msgMessaggio.setCorpo(messaggio.getCorpo().getTestomessaggio());
		msgMessaggio
				.setDeTipoMessaggio(deTipoMessaggioHome.findById(messaggio.getDatiSistema().getTipocomunicazione()));

		boolean fromAziendaToLavoratore = TipoSoggetto.AZIENDA.compareTo(new Integer(tipoSoggettoMitt)) == 0;
		boolean fromLavoratoreToAzienda = TipoSoggetto.CITTADINO.compareTo(new Integer(tipoSoggettoMitt)) == 0;
		boolean fromIntermediario = TipoSoggetto.INTERMEDIARIO.compareTo(new Integer(tipoSoggettoMitt)) == 0;

		String codiceMessaggioPrecedente = messaggio.getDatiSistema().getCodicemessaggioprecedente();

		PfPrincipal pfPrincipalDest = null;
		VaDatiVacancy vaDatiVacancy = null;
		CvDatiPersonali cvDatiPersonali = null;

		String oggetto = "Messaggio";
		if (fromLavoratoreToAzienda) {
			log.info("Messaggio da Lavoratore ad Azienda");
			PfPrincipal pfPrincipalMitt = getPfPrincipalMessaggio(interlMitt.getCodiceoffertacandidatura());

			String codiceVacancy = messaggio.getDestinatario().getInterlocutore().getCodiceoffertacandidatura();
			vaDatiVacancy = vaDatiVacancyHome.findByCodComunicazione(codiceVacancy);

			String codiceCurriculum = messaggio.getMittente().getInterlocutore().getCodiceoffertacandidatura();
			cvDatiPersonali = cvDatiPersonaliHome.findByCodComunicazione(codiceCurriculum);

			if (vaDatiVacancy != null) {
				log.info("E' presente la vacancy con codice: " + codiceVacancy);
				oggetto = mitt.getInterlocutore().getDenominazione() + " si è candidato all'offerta di lavoro "
						+ vaDatiVacancy.getAttivitaPrincipale();
			} else {
				log.info("Non è presente la vacancy con codice: " + codiceVacancy);
				oggetto = mitt.getInterlocutore().getDenominazione()
						+ " si è candidato ad un’offerta di lavoro non più presente in " + ConstantsSingleton.TITLE_APP +" (provenienza Cliclavoro)";
			}

			msgMessaggio.setOggetto(oggetto);
			// CANDIDATURA
			if (pfPrincipalMitt != null) {
				log.info("E' presente il lavoratore mittente, imposto '" + pfPrincipalMitt.getUsername() + "'");
				msgMessaggio.setPfPrincipalFrom(pfPrincipalMitt);
			} else {
				log.info("Non è presente il lavoratore mittente, imposto l'utente 'clicLavoro'");
				msgMessaggio.setPfPrincipalFrom(pfPrincipalHome.getClicLavoro());
			}

			AziendaInfo azInfo = null;
			if (codiceMessaggioPrecedente != null
					&& TipoMessaggio.RISPOSTA_CONTATTO.equals(messaggio.getDatiSistema().getTipocomunicazione())) {
				MsgMessaggioCl msgMessaggioClPrec = msgMessaggioHome.findByCodComunicazione(codiceMessaggioPrecedente);
				MsgMessaggio msgPrec = msgMessaggioClPrec.getMsgMessaggio();
				PfPrincipal soggettoMittenteMsgPrec = msgPrec.getPfPrincipalFrom();
				if (soggettoMittenteMsgPrec.isAzienda()) {
					azInfo = soggettoMittenteMsgPrec.getAziendaInfo();
				} else {
					azInfo = aziendaInfoHome.findByCodiceFiscale(interlDest.getCodicefiscale());
				}
			} else {
				azInfo = aziendaInfoHome.findByCodiceFiscale(interlDest.getCodicefiscale());
			}
			if (azInfo != null) {
				pfPrincipalDest = azInfo.getPfPrincipal();
				log.info("E' presente l'azienda destinatario, imposto '" + pfPrincipalDest.getUsername() + "'");
				msgMessaggio.setPfPrincipalTo(pfPrincipalDest);
			} else {
				log.info("Non è presente l'azienda destinatario, imposto 'cliclavoro'");
				msgMessaggio.setPfPrincipalTo(pfPrincipalHome.getClicLavoro());
			}
		} else if (fromAziendaToLavoratore) {
			log.info("Messaggio da Azienda a Lavoratore");
			// PRIMO CONTATTO

			// se msg risposta recupero l'azienda dal msg prec
			AziendaInfo azInfo = null;
			if (codiceMessaggioPrecedente != null
					&& TipoMessaggio.RISPOSTA_CONTATTO.equals(messaggio.getDatiSistema().getTipocomunicazione())) {
				MsgMessaggioCl msgMessaggioClPrec = msgMessaggioHome.findByCodComunicazione(codiceMessaggioPrecedente);
				MsgMessaggio msgPrec = msgMessaggioClPrec.getMsgMessaggio();
				PfPrincipal soggettoMittenteMsgPrec = msgPrec.getPfPrincipalFrom();
				if (soggettoMittenteMsgPrec.isAzienda()) {
					azInfo = soggettoMittenteMsgPrec.getAziendaInfo();
				} else {
					azInfo = aziendaInfoHome.findByCodiceFiscale(interlDest.getCodicefiscale());
				}
			} else {
				azInfo = aziendaInfoHome.findByCodiceFiscale(interlDest.getCodicefiscale());
			}

			String codiceVacancy = messaggio.getMittente().getInterlocutore().getCodiceoffertacandidatura();
			vaDatiVacancy = vaDatiVacancyHome.findByCodComunicazione(codiceVacancy);

			String codiceCurriculum = messaggio.getDestinatario().getInterlocutore().getCodiceoffertacandidatura();
			cvDatiPersonali = cvDatiPersonaliHome.findByCodComunicazione(codiceCurriculum);

			if (azInfo != null) {
				PfPrincipal pfPrincipal = azInfo.getPfPrincipal();
				log.info("E' presente l'azienda mittente, imposto '" + pfPrincipal.getUsername() + "'");
				msgMessaggio.setPfPrincipalFrom(pfPrincipal);
			} else {
				log.info("E' presente l'azienda mittente, imposto 'cliclavoro'");
				msgMessaggio.setPfPrincipalFrom(pfPrincipalHome.getClicLavoro());
			}
			msgMessaggio.setOggetto("L'azienda " + mitt.getInterlocutore().getDenominazione()
					+ " è interessata a contattarti.");
			// se cv intermediato cambio il destinatario prendendolo da cv
			if (cvDatiPersonali != null && cvDatiPersonali.getPfPrincipalPalese() != null
					&& cvDatiPersonali.getPfPrincipalPalese().getIdPfPrincipal() != null) {
				log.info("Primo contatto con cv intermediato, il destinatario è pfPrincipalPalese del cv = "
						+ cvDatiPersonali.getPfPrincipalPalese().getIdPfPrincipal());
				msgMessaggio.setPfPrincipalTo(cvDatiPersonali.getPfPrincipalPalese());
				// poi mando la notifica alla redazione (che sta nel cv, in
				// pfprincipal)
				Set<MsgMessaggioDTO> messaggi = notificationBuilder.buildNotificationCvIntermediato(cvDatiPersonali,
						mitt.getInterlocutore().getDenominazione());
				notificationBuilder.sendNotification(messaggi);

			} else {
				pfPrincipalDest = getPfPrincipalMessaggio(interlDest.getCodiceoffertacandidatura());
				if (pfPrincipalDest != null) {
					log.info("E' presente il lavoratore destinatario, imposto '" + pfPrincipalDest.getUsername() + "'");
					msgMessaggio.setPfPrincipalTo(pfPrincipalDest);
				} else {
					log.info("E' presente il lavoratore destinatario, imposto 'cliclavoro'");
					msgMessaggio.setPfPrincipalTo(pfPrincipalHome.getClicLavoro());
				}
			}
		} else if (fromIntermediario) {
			log.info("Messaggio da Intermediario");
			// PRIMO CONTATTO

			String codice = messaggio.getDestinatario().getInterlocutore().getCodiceoffertacandidatura();
			vaDatiVacancy = vaDatiVacancyHome.findByCodComunicazione(codice);
			cvDatiPersonali = cvDatiPersonaliHome.findByCodComunicazione(codice);
			ClInvioComunicazione clInvioComunicazione = clInvioComunicazioneHome
					.findFromMyPortalByCodComunicazione(codice);
			if (vaDatiVacancy != null) {
				pfPrincipalDest = vaDatiVacancy.getPfPrincipal();
				oggetto = mitt.getInterlocutore().getDenominazione() + " si è candidato all'offerta di lavoro "
						+ vaDatiVacancy.getAttivitaPrincipale();
				log.info("Il destinatario è un'azienda '" + pfPrincipalDest.getUsername() + "'");
			} else if (cvDatiPersonali != null) {
				pfPrincipalDest = cvDatiPersonali.getPfPrincipal();
				oggetto = "L'azienda " + mitt.getInterlocutore().getDenominazione() + " è interessata a contattarti.";
				log.info("Il destinatario è un cittadino '" + pfPrincipalDest.getUsername() + "'");
			} else if (clInvioComunicazione != null) {
				pfPrincipalDest = clInvioComunicazione.getPfPrincipalMod();
				if (pfPrincipalDest.isAzienda()) {
					oggetto = mitt.getInterlocutore().getDenominazione() + " si è candidato ad una offerta di lavoro.";
					log.info("Il destinatario è un'azienda '" + pfPrincipalDest.getUsername() + "'");
				} else if (pfPrincipalDest.isUtente()) {
					oggetto = "L'azienda " + mitt.getInterlocutore().getDenominazione()
							+ " è interessata a contattarti.";
					log.info("Il destinatario è un cittadino '" + pfPrincipalDest.getUsername() + "'");
				} else {
					log.info("Il destinatario è qualcosa che non è nè un cittadino nè un'azienda...forse è un mostro...");
					oggetto = "Messaggio da ClicLavoro a ClicLavoro";
				}
			} else {
				log.info("Il destinatario è cliclavoro");
				pfPrincipalDest = pfPrincipalHome.getClicLavoro();
			}

			msgMessaggio.setPfPrincipalFrom(pfPrincipalHome.getClicLavoro());
			msgMessaggio.setPfPrincipalTo(pfPrincipalDest);
			msgMessaggio.setOggetto(oggetto);
		}

		/* è un messaggio di risposta? */

		if (codiceMessaggioPrecedente != null
				&& TipoMessaggio.RISPOSTA_CONTATTO.equals(messaggio.getDatiSistema().getTipocomunicazione())) {
			log.info("E' un messaggio di risposta");
			MsgMessaggioCl msgMessaggioClPrec = msgMessaggioHome.findByCodComunicazione(codiceMessaggioPrecedente);
			if (msgMessaggioClPrec != null) {
				log.info("E' presente il messaggio precedente");

				/* setto il messaggio precedente se lo trovo */
				msgMessaggio.setPrecedente(msgMessaggioClPrec.getMsgMessaggio());

				MsgMessaggio msgPrec = msgMessaggioClPrec.getMsgMessaggio();
				PfPrincipal soggettoMittenteMsgPrec = msgPrec.getPfPrincipalFrom();

				/*
				 * se il contatto è positivo e il destinatario è un utente del
				 * portale popolo le tabelle di visibilità per permettere a
				 * questo utente di visualizzare i dati
				 */
				if (SiNo.SI.equals(messaggio.getCorpo().getContattopositivo()) && (soggettoMittenteMsgPrec != null)) {
					if (fromLavoratoreToAzienda) { // il lavoratore ha concesso
													// all'azienda di
													// visualizzare il proprio
													// CV
						log.info("Il lavoratore ha concesso all'azienda di visualizzare il proprio CV");
						if (cvDatiPersonali != null) {
							log.info("ed il CV è presente");
							soggettoMittenteMsgPrec.getViewableCvDatiPersonali().add(cvDatiPersonali);
							pfPrincipalHome.merge(soggettoMittenteMsgPrec);
						}
					} else if (fromAziendaToLavoratore) { // l'azienda ha
															// concesso al
															// lavoratore di
															// visualizzare
															// l'offerta di
															// lavoro
						log.info("L'azienda ha concesso al lavoratore di visualizzare l'offerta di lavoro");
						if (vaDatiVacancy != null) {
							log.info("e la Vacancy è presente");
							soggettoMittenteMsgPrec.getViewableVaDatiVacancy().add(vaDatiVacancy);
							pfPrincipalHome.merge(soggettoMittenteMsgPrec);
						}

					} else if (fromIntermediario) {
						if (vaDatiVacancy != null) {
							log.info("L'azienda ha concesso al lavoratore di visualizzare l'offerta di lavoro");
							log.info("e la Vacancy è presente");
							soggettoMittenteMsgPrec.getViewableVaDatiVacancy().add(vaDatiVacancy);
							pfPrincipalHome.merge(soggettoMittenteMsgPrec);
						} else if (cvDatiPersonali != null) {
							log.info("Il lavoratore ha concesso all'azienda di visualizzare il proprio CV");
							log.info("ed il CV è presente");
							soggettoMittenteMsgPrec.getViewableCvDatiPersonali().add(cvDatiPersonali);
							pfPrincipalHome.merge(soggettoMittenteMsgPrec);
						}
					}
				}
			} else {
				log.info("Non è stato trovato il messaggio precedente");
			}
		}

		entityManager.persist(msgMessaggio);

		msgMessaggio.setTicket(msgMessaggio.getIdMsgMessaggio().toString());
		entityManager.merge(msgMessaggio);

		MsgContatto msgContatto = new MsgContatto();
		msgContatto.setMsgMessaggio(msgMessaggio);
		msgContatto.setIdMsgMessaggio(msgMessaggio.getIdMsgMessaggio());
		msgContatto.setDtmIns(now);
		msgContatto.setPfPrincipalIns(pfPrincipalHome.getAdministrator());
		msgContatto.setDtmMod(now);
		msgContatto.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
		if (SiNo.SI.equals(messaggio.getCorpo().getContattopositivo())) {
			msgContatto.setFlagEsito(true);
		}
		msgContatto.setCvDatiPersonali(cvDatiPersonali);
		msgContatto.setVaDatiVacancy(vaDatiVacancy);
		entityManager.persist(msgContatto);

		MsgMessaggioCl msgMessaggioCl = new MsgMessaggioCl();
		msgMessaggioCl.setIdMsgMessaggio(msgMessaggio.getIdMsgMessaggio());
		msgMessaggioCl.setMittente(msgSoggettoClMitt);
		msgMessaggioCl.setDestinatario(msgSoggettoClDest);
		msgMessaggioCl.setCodComunicazione(messaggio.getDatiSistema().getCodicemessaggio());
		msgMessaggioCl.setCodComunicazionePrec(messaggio.getDatiSistema().getCodicemessaggioprecedente());
		msgMessaggioCl.setCodTipoSoggettoMitt(new Integer(tipoSoggettoMitt));
		msgMessaggioCl.setDeStatoInvioCl(deStatoInvioClHome.findById(ConstantsSingleton.DeStatoInvioCl.INVIATA));
		msgMessaggioCl.setMsgMessaggio(msgMessaggio);
		msgMessaggioCl.setDtmIns(now);
		msgMessaggioCl.setPfPrincipalIns(pfPrincipalHome.getAdministrator());
		msgMessaggioCl.setDtmMod(now);
		msgMessaggioCl.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
		entityManager.persist(msgMessaggioCl);

	}

	/**
	 * recupero l'utente del CV a cui associare il messaggio controllo prima
	 * nella CV_CANDIDATURA_CL se esiste la candidatura inviata a ClicLavoro nel
	 * caso in cui è stata cancellata controllo se riesco a recuperare l'utente
	 * nella CL_INVIO_COMUNICAZIONE
	 * 
	 * @param codComunicazione
	 * @return
	 */
	private PfPrincipal getPfPrincipalMessaggio(String codComunicazione) {
		PfPrincipal pfPrincipal = null;
		List<CvCandidaturaCl> candidaturas = entityManager
				.createNamedQuery("findCandidaturaByCodComunicazione", CvCandidaturaCl.class)
				.setParameter("codComunicazione", codComunicazione).getResultList();
		if (candidaturas != null && !candidaturas.isEmpty()) {
			pfPrincipal = candidaturas.get(0).getCvDatiPersonali().getPfPrincipal();
		} else {
			// utente modifica è quello proprietario di CV
			List<ClInvioComunicazione> comunicaziones = entityManager
					.createNamedQuery("findComunicazioneByCodComunicazione", ClInvioComunicazione.class)
					.setParameter("codComunicazione", codComunicazione).getResultList();
			if (comunicaziones != null && !comunicaziones.isEmpty()) {
				pfPrincipal = comunicaziones.get(0).getPfPrincipalMod();
			}
		}
		return pfPrincipal;
	}

}
