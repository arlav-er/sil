package it.eng.myportal.entity.ejb;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.entity.PfIdentityProvider;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.decodifiche.DeTitoloSoggiorno;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloSoggiornoHome;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.siler.getDettaglioCittadino.out.RispostaDettaglioCittadino.DettaglioCittadino;
import it.eng.myportal.siler.putAccountCittadino.in.PutAccountCittadino;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

/**
 * Session Bean implementation class ClicLavoroEjb
 */
@Stateless
public class CittadinoEjb {

	protected final Log log = LogFactory.getLog(CittadinoEjb.class);

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	DeTitoloSoggiornoHome deTitoloSoggiornoHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;

	@PersistenceContext
	protected EntityManager entityManager;

	private PfPrincipal registraNuovoUtente(PutAccountCittadino data) throws ParseException {

		RegisterUtenteDTO dto = new RegisterUtenteDTO();
		dto.setAcceptInformativa(true);
		dto.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		dto.setAttivo(false);
		dto.setAutenticazioneForte(true);
		DeCittadinanzaDTO cittadinanzaDTO = new DeCittadinanzaDTO();
		cittadinanzaDTO.setId(data.getCittadinanza());
		dto.setCittadinanza(cittadinanzaDTO);
		dto.setCodiceFiscale(data.getCodiceFiscale());
		dto.setCodiceRichiestaAutForte(null);
		String cognome = data.getCognome();
		dto.setCognome(cognome);
		DeComuneDTO comune = deComuneHome.findDTOById(data.getComuneDomicilio());
		dto.setComune(comune);
		dto.setCap(comune.getCap());
		dto.setDataAssicurata(null);
		dto.setDataNascita(Utils.fromXMLGregorianCalendar(data.getDataNascita()));
		dto.setDataScadenzaDocumento(Utils.fromXMLGregorianCalendar(data.getDtScadenzaDocumento()));
		dto.setDocumentoIdentita(data.getDocumentoIdentita());
		dto.setDocumentoSoggiorno(null);
		dto.setDomanda("Quale e' il tuo codice fiscale?");
		dto.setDomicilio(comune);
		String emailUtente = data.getEmail();
		dto.setEmail(emailUtente);
		dto.setEmailConfirm(emailUtente);
		dto.setProvincia(deProvinciaHome.findDTOById(data.getCodProvinciaSil()));
		dto.setIndirizzo(data.getIndirizzoDomicilio());
		dto.setIndirizzoPEC(null);
		String nome = data.getNome();
		dto.setNome(nome);
		dto.setNumeroAssicurata(null);
		dto.setNumeroDocumento(data.getNumeroDocumento());
		String password = Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH);
		dto.setPassword(password);
		dto.setPasswordConfirm(password);
		dto.setRegisterFromProvider(false);
		dto.setRisposta(data.getCodiceFiscale());
		String username = data.getUsername();
		dto.setUsername(Utils.cut(username.toLowerCase(), 16));
		Integer idPfPrincipal = utenteInfoHome.register(dto, true);
		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);
		UtenteInfo info = utenteInfoHome.findById(idPfPrincipal);
		info.setIndirizzoDomicilio(data.getIndirizzoDomicilio());
		info.setDtNascita(Utils.fromXMLGregorianCalendar(data.getDataNascita()));
		info.setDeComuneDomicilio(deComuneHome.findById(data.getComuneDomicilio()));
		info.setDeComuneNascita(deComuneHome.findById(data.getComuneNascita()));
		info = utenteInfoHome.merge(info);
		principal.setUtenteInfo(info);
		EmailDTO registerEmail = EmailDTO.buildRegistrationEmailFromSil(dto);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerEmail);

		return principal;
	}

	public void aggiornaAccount(PutAccountCittadino data) throws MyPortalException {

		String idPfPrincipalStr = Utils.getStringFromSimpleXmlElement(data.getIdPfPrincipal());
		Integer idPfPrincipal = Integer.parseInt(idPfPrincipalStr);
		PfPrincipal principal = pfPrincipalHome.findById(idPfPrincipal);

		// Cerco l'account da aggiornare
		if (principal == null || principal.getIdPfPrincipal() == null) {
			throw new MyPortalException("03", "Cittadino non trovato");

		}

		// verifica se è un user FEDERA
		List<PfIdentityProvider> providers = pfIdentityProviderHome.findByPFPId(principal.getIdPfPrincipal());
		if (providers.isEmpty()) {
			// Se lo username è da modificare devo vedere se è già presente sul DB
			if (data.getUsername() != null && !data.getUsername().equals(principal.getUsername())) {
				PfPrincipal lista = pfPrincipalHome.findByUsername(data.getUsername().toUpperCase());
				if (lista != null && lista.getIdPfPrincipal() != null) {
					throw new MyPortalException("04", "Aggiornamento non riuscito: username già presente");
				}
			}
		}

		// Se l'email è da modificare devo vedere se è già presente sul DB
		if (data.getEmail() != null && !data.getEmail().equals(principal.getEmail())) {
			List<PfPrincipal> lista = pfPrincipalHome.findByEmail(data.getEmail().toUpperCase());
			if (lista.size() > 0) {
				throw new MyPortalException("05", "Aggiornamento non riuscito: e-mail già registrata");
			}
		}

		principal.getUtenteInfo().setCodiceFiscale(data.getCodiceFiscale());
		try {
			principal.getUtenteInfo().setDtNascita(Utils.fromXMLGregorianCalendar(data.getDataNascita()));
		} catch (ParseException e) {
			throw new MyPortalException("06", "Inserimento non riuscito");
		}
		// principal.setEmail(map.get(ConstantsSingleton.XmlCommons.EMAIL));
		principal.getUtenteInfo().setDeComuneNascita(deComuneHome.findById(data.getComuneNascita()));
		principal.getUtenteInfo().setDeComuneDomicilio(deComuneHome.findById(data.getComuneDomicilio()));
		principal.getUtenteInfo().setIndirizzoDomicilio(data.getIndirizzoDomicilio());
		principal.getUtenteInfo().setDeCittadinanza(deCittadinanzaHome.findById(data.getCittadinanza()));
		principal.getUtenteInfo().setDeProvincia(deProvinciaHome.findById(data.getCodProvinciaSil()));
		principal.getUtenteInfo().setDocumentoIdentita(data.getDocumentoIdentita());
		principal.getUtenteInfo().setNumeroDocumento(data.getNumeroDocumento());
		try {
			principal.getUtenteInfo()
					.setDataScadenzaDocumento(Utils.fromXMLGregorianCalendar(data.getDtScadenzaDocumento()));
		} catch (ParseException e) {
			throw new MyPortalException("06", "Inserimento non riuscito");
		}
		principal.setDtmMod(new Date());
		principal.setPfPrincipalMod(pfPrincipalHome.findById(0));
		principal.setFlagAbilitatoServizi(true);

		utenteInfoHome.merge(principal.getUtenteInfo());
		pfPrincipalHome.merge(principal);

	}

	public void inserisciAccount(PutAccountCittadino data) throws MyPortalException {

		// Controllo che l'email non sia già presente su DB
		if (data.getUsername() != null) {
			PfPrincipal principal = pfPrincipalHome.findByUsername(data.getUsername().toUpperCase());
			if (principal != null && principal.getIdPfPrincipal() != null) {

				throw new MyPortalException("04", "Inserimento non riuscito: username già presente");
			}
		}

		// Controllo che l'email non sia già presente su DB
		if (data.getEmail() != null) {
			List<PfPrincipal> lista = pfPrincipalHome.findByEmail(data.getEmail().toUpperCase());
			if (lista.size() > 0) {
				throw new MyPortalException("05", "Inserimento non riuscito: e-mail già registrata");
			}
		}

		try {
			PfPrincipal nuovoUtente = registraNuovoUtente(data);
			utenteInfoHome.findById(nuovoUtente.getIdPfPrincipal());
		} catch (ParseException e) {
			throw new MyPortalException("10", "Aggiornamento non riuscito");
		}

	}

	public void reinviaMailAccreditamento(Integer id, List<String> listaDestinatari) {

		PfPrincipal principal = pfPrincipalHome.findById(id);

		// Cerco l'account da aggiornare
		if (principal == null || principal.getIdPfPrincipal() == null) {
			throw new MyPortalException("03", "Cittadino non trovato");
		}

		if (principal.getFlagAbilitato()) {
			throw new MyPortalException("04", "Account già abilitato");
		}
		String vecchiaPassword = null;
		String vecchioToken = null;

		try {
			vecchiaPassword = principal.getPassWord();
			vecchioToken = principal.getConfirmationToken();
			String nuovaPassword = Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH);
			principal = resetPassordOnDB(principal, nuovaPassword);

			inviaMailReset(principal, listaDestinatari, nuovaPassword);
		} catch (Exception e) {
			ripristinaVecchiValori(id, vecchiaPassword, vecchioToken);
			throw new MyPortalException("02", "Errore di reinvio mail");
		}

	}

	private PfPrincipal resetPassordOnDB(PfPrincipal principal, String nuovaPassword) {
		principal.setConfirmationToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		principal.setPassWord(Utils.SHA1.encrypt(nuovaPassword));

		pfPrincipalHome.merge(principal);

		return principal;
	}

	private void inviaMailReset(PfPrincipal principal, List<String> listaDestinatari, String nuovaPassword)
			throws AddressException, MessagingException, NamingException, JMSException {

		EmailDTO emailDTO = EmailDTO.buildRegistrationEmailWS(principal, listaDestinatari, nuovaPassword);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, emailDTO);

	}

	private PfPrincipal ripristinaVecchiValori(Integer id, String vecchiaPassword, String vecchioToken) {

		PfPrincipal principal = pfPrincipalHome.findById(id);
		principal.setPassWord(vecchiaPassword);
		principal.setConfirmationToken(vecchioToken);

		pfPrincipalHome.merge(principal);

		return principal;

	}

	public List<String> trovaValidaIndirizzi(String destinatariStr) {
		List<String> listaDestinatari = new ArrayList<String>();
		if (destinatariStr == null || destinatariStr.isEmpty())
			return listaDestinatari;
		destinatariStr = destinatariStr.replace(";", ",");
		String[] lista = destinatariStr.split(",");
		for (String email : lista) {
			email = email.trim();
			Pattern p = Pattern.compile(
					"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
			Matcher m = p.matcher(email);
			if (m.matches())
				listaDestinatari.add(email);
		}
		return listaDestinatari;
	}

	public List<PfPrincipal> trovaUtenti(
			it.eng.myportal.siler.getAccountCittadino.in.AccountCittadino accountCittadinoIn) {
		List<PfPrincipal> listaPrincipal = pfPrincipalHome.findCittadinoByFilter(accountCittadinoIn.getNome(),
				accountCittadinoIn.getCognome(), accountCittadinoIn.getEmail(), accountCittadinoIn.getCodiceFiscale());

		return listaPrincipal;
	}

	public it.eng.myportal.siler.getAccountCittadino.out.RispostaAccountCittadino.DatiAccount.AccountCittadino creaDocumentDatiAccount(
			PfPrincipal principal) {

		it.eng.myportal.siler.getAccountCittadino.out.RispostaAccountCittadino.DatiAccount.AccountCittadino accountCittadino = new it.eng.myportal.siler.getAccountCittadino.out.RispostaAccountCittadino.DatiAccount.AccountCittadino();

		List<PfIdentityProvider> providers = pfIdentityProviderHome.findByPFPId(principal.getIdPfPrincipal());
		if (providers.isEmpty()) {
			accountCittadino.setUsername(principal.getUsername());
		} else {
			// prendo il primo,
			TipoProvider pv = providers.get(0).getCodTipoProvider();
			switch (pv) {
			case FACEBOOK:
				accountCittadino.setUsername("FACEBOOK");
				break;
			case SPID:
			case ICAR:
			case FEDERA:
				accountCittadino.setUsername("UT_CERTIFICATO");
				break;
			case GOOGLE:
				accountCittadino.setUsername("GOOGLE");
				break;
			case TWITTER:
				accountCittadino.setUsername("TWITTER");
				break;
			default:
				log.error("GRAVE: provider non contemplato: " + pv);
				accountCittadino.setUsername("UT_CERTIFICATO");
				break;
			}

		}
		accountCittadino.setCognome(principal.getCognome());
		accountCittadino.setNome(principal.getNome());
		accountCittadino.setEmail(principal.getEmail());
		accountCittadino.setIdPfPrincipal(principal.getIdPfPrincipal().toString());

		accountCittadino.setAbilitatoServiziAmministrativi(principal.getFlagAbilitatoServizi() ? "S" : "N");
		accountCittadino.setAbilitato(principal.getFlagAbilitato() ? "S" : "N");

		return accountCittadino;

	}

	public DettaglioCittadino creaDocumentDettaglioAccount(PfPrincipal principal) {

		DettaglioCittadino accountCittadino = new DettaglioCittadino();

		List<PfIdentityProvider> providers = pfIdentityProviderHome.findByPFPId(principal.getIdPfPrincipal());
		if (providers.isEmpty()) {
			accountCittadino.setUsername(principal.getUsername());
		} else {
			accountCittadino.setUsername("UT_CERTIFICATO");
		}

		accountCittadino.setCognome(principal.getCognome());
		accountCittadino.setNome(principal.getNome());
		accountCittadino.setEmail(principal.getEmail());
		accountCittadino.setIdPfPrincipal(principal.getIdPfPrincipal().toString());

		if (principal.getUtenteInfo().getDeComuneNascita() != null
				&& !principal.getUtenteInfo().getDeComuneNascita().getCodCom().isEmpty()) {
			accountCittadino.setComuneNascita(principal.getUtenteInfo().getDeComuneNascita().getCodCom());

		}
		if (principal.getUtenteInfo().getDeComuneDomicilio() != null
				&& !principal.getUtenteInfo().getDeComuneDomicilio().getCodCom().isEmpty()) {
			accountCittadino.setComuneDomicilio(principal.getUtenteInfo().getDeComuneDomicilio().getCodCom());

		}
		if (principal.getUtenteInfo().getIndirizzoDomicilio() != null
				&& !principal.getUtenteInfo().getIndirizzoDomicilio().isEmpty()) {
			accountCittadino.setIndirizzoDomicilio(principal.getUtenteInfo().getIndirizzoDomicilio());

		}
		if (principal.getUtenteInfo().getCodiceFiscale() != null
				&& !principal.getUtenteInfo().getCodiceFiscale().isEmpty()) {
			accountCittadino.setCodiceFiscale(principal.getUtenteInfo().getCodiceFiscale());

		}

		if (principal.getUtenteInfo().getDtNascita() != null) {
			try {
				accountCittadino.setDataNascita(Utils.toXMLGregorianCalendar(principal.getUtenteInfo().getDtNascita()));
			} catch (DatatypeConfigurationException e) {
				log.warn("Non ho inserito la data di nascita: " + e.getMessage());
			}

		}
		if (principal.getUtenteInfo().getDeCittadinanza() != null) {
			accountCittadino.setCittadinanza(principal.getUtenteInfo().getDeCittadinanza().getCodCittadinanza());
		}

		if (principal.getUtenteInfo().getDeCittadinanza() == null
				|| principal.getUtenteInfo().getDeCittadinanza().equals("000")) {
			if (principal.getUtenteInfo().getDocumentoIdentita() != null
					&& !principal.getUtenteInfo().getDocumentoIdentita().isEmpty()) {
				accountCittadino.setDocumentoIdentita(principal.getUtenteInfo().getDocumentoIdentita());
			}
		} else {
			if (principal.getUtenteInfo().getDocumentoSoggiorno() != null
					&& principal.getUtenteInfo().getDocumentoSoggiorno().getCodTitoloSoggiorno() != null) {
				DeTitoloSoggiorno deTitoloSoggiorno = deTitoloSoggiornoHome
						.findById(principal.getUtenteInfo().getDocumentoSoggiorno().getCodTitoloSoggiorno());
				accountCittadino.setDocumentoIdentita(deTitoloSoggiorno.getDescrizione());
				accountCittadino.setCodStatus(deTitoloSoggiorno.getCodTitoloSoggiorno());
			} else if (principal.getUtenteInfo().getDocumentoIdentita() != null
					&& !principal.getUtenteInfo().getDocumentoIdentita().isEmpty()) {
				accountCittadino.setDocumentoIdentita(principal.getUtenteInfo().getDocumentoIdentita());

			}
		}

		if (principal.getUtenteInfo().getNumeroDocumento() != null
				&& !principal.getUtenteInfo().getNumeroDocumento().isEmpty()) {
			accountCittadino.setNumeroDocumento(principal.getUtenteInfo().getNumeroDocumento());

		}
		if (principal.getUtenteInfo().getDataScadenzaDocumento() != null) {
			try {
				accountCittadino.setDtScadenzaDocumento(
						Utils.toXMLGregorianCalendar(principal.getUtenteInfo().getDataScadenzaDocumento()));
			} catch (DatatypeConfigurationException e) {
				log.warn("Non ho settato la data scadenza documento: " + e.getMessage());
			}

		}

		accountCittadino.setAbilitatoServiziAmministrativi(principal.getFlagAbilitatoServizi() ? "S" : "N");
		accountCittadino.setAbilitato(principal.getFlagAbilitato() ? "S" : "N");

		return accountCittadino;

	}

}
