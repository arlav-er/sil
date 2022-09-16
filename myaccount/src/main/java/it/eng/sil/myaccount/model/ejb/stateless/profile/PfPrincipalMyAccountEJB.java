package it.eng.sil.myaccount.model.ejb.stateless.profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.helpers.PfPrincipalFilter;
import it.eng.sil.myaccount.model.ejb.stateless.sare.UtenteAziendaSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.model.enums.TipoOrdinamento;
import it.eng.sil.myaccount.model.exceptions.MyAccountException;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.exceptions.MyCasTooManyResultsException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo_;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo_;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal_;
import it.eng.sil.mycas.model.entity.profilo.Provincia;
import it.eng.sil.mycas.model.entity.profilo.Provincia_;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo_;
import it.eng.sil.mycas.model.manager.decodifiche.DeRuoloPortaleEJB;

@Stateless
public class PfPrincipalMyAccountEJB extends it.eng.sil.mycas.model.manager.profilatura.PfPrincipalEJB {

	@EJB
	ConstantsSingleton constantsSingleton;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	ProvinciaEJB provinciaEJB;

	@EJB
	EmailManager emailManager;

	@EJB
	UtenteAziendaSareEJB utenteAziendaSareEJB;

	@EJB
	DeRuoloPortaleEJB deRuoloPortaleEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni utente";
	}

	@Override
	public Class<PfPrincipal> getEntityClass() {
		return PfPrincipal.class;
	}

	public boolean isUniqueEmail(String email) {
		log.debug("getting pfPrincipal with email: " + email);
		boolean existing = false;

		TypedQuery<Long> q = entityMan.createNamedQuery("countPfPrincipalByEmail", Long.class)
				.setParameter("emailParam", email);

		existing = ((Long) q.getSingleResult()) > 0;

		return !existing;
	}

	public boolean isPECUniqueEmail(String email) {
		log.debug("getting pfPrincipal with email: " + email);
		boolean existing = false;

		TypedQuery<Long> q = entityMan.createNamedQuery("countPfPrincipalByPECEmail", Long.class)
				.setParameter("emailParam", email);

		existing = ((Long) q.getSingleResult()) > 0;

		return !existing;
	}

	public boolean exists(String username) {
		return findByUsername(username) != null;
	}

	/**
	 * Cerca un pfPrincipal a partire dal suo Username
	 * 
	 * @param name
	 * @return
	 */
	public PfPrincipal findByUsername(String name) {
		log.debug("getting pfPrincipal with username: " + name);

		List<PfPrincipal> list = entityMan.createNamedQuery("findPfPrincipalByUsername", PfPrincipal.class)
				.setParameter("usernameParam", name).getResultList();

		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	/** Se il cittadino si registra da solo, parte non abilitato e con la password scaduta. */
	public PfPrincipal registerCittadino(PfPrincipal pfPrincipalRegister, boolean pwdScaduta) throws MyCasException {
		return register(pfPrincipalRegister, ConstantsSingleton.DeRuoloPortaleConstants.CITTADINO, 0, false, true,
				pwdScaduta, false);
	}

	/**
	 * Se il cittadino è stato registrato da pannello amministratore o da provincia, è già abilitato e ha la password
	 * non scaduta.
	 */
	public PfPrincipal registerCittadinoFromPannello(PfPrincipal pfPrincipalRegister, Integer idPrincipalIns)
			throws MyCasException {
		return register(pfPrincipalRegister, ConstantsSingleton.DeRuoloPortaleConstants.CITTADINO, idPrincipalIns,
				false, true, false, true);
	}

	/** Se l'azienda si registra da sola, parte non abilitata e con la password scaduta. */
	public PfPrincipal registerAzienda(PfPrincipal pfPrincipalRegister, boolean pwdScaduta, boolean abilitatoSare)
			throws MyCasException {
		return register(pfPrincipalRegister, ConstantsSingleton.DeRuoloPortaleConstants.AZIENDA, 0, abilitatoSare, true,
				pwdScaduta, false);
	}

	/**
	 * Se l'azienda è stata registrata da pannello amministratore o da provincia, è già abilitata e ha la password non
	 * scaduta.
	 */
	public PfPrincipal registerAziendaFromPannello(PfPrincipal pfPrincipalRegister, String codRuoloPortale,
			boolean pwdScaduta, boolean abilitatoSare) throws MyCasException {
		return register(pfPrincipalRegister, codRuoloPortale, 0, abilitatoSare, true, pwdScaduta, false);
	}

	/** Gli utenti registrati da porting SARE usano una procedura a parte */
	public PfPrincipal registerAziendaFromPorting(PfPrincipal pfPrincipalRegister, String codRuoloPortale)
			throws MyCasException {
		return registerFromPorting(pfPrincipalRegister, codRuoloPortale);
	}

	/** Gli utenti registrati da porting SARE usano una procedura a parte */
	public PfPrincipal registerProvinciaFromPorting(PfPrincipal pfPrincipalRegister) throws MyCasException {
		return registerFromPorting(pfPrincipalRegister, ConstantsSingleton.DeRuoloPortaleConstants.PROVINCIA);
	}

	/** Gli utenti regionali partono già abilitati */
	public PfPrincipal registerRegione(PfPrincipal pfPrincipalRegister, boolean pwdScaduta) throws MyCasException {
		return register(pfPrincipalRegister, ConstantsSingleton.DeRuoloPortaleConstants.REGIONE, 0, false, true,
				pwdScaduta, true);
	}

	/** Gli utenti provinciali partono già abilitati */
	public PfPrincipal registerProvincia(PfPrincipal pfPrincipalRegister, boolean pwdScaduta, String codRuoloPortale)
			throws MyCasException {
		return register(pfPrincipalRegister, codRuoloPortale, 0, false, true, pwdScaduta, true);
	}

	private PfPrincipal register(PfPrincipal pfPrincipalRegister, String codRuoloPortale, Integer idPrincipalIns,
			boolean abilitatoSare, boolean encrypt, boolean pwdScaduta, boolean giaAbilitato) throws MyCasException {
		Date now = new Date();
		PfPrincipal principal = new PfPrincipal();
		principal.setFlagAbilitato(giaAbilitato);
		principal.setFlagAbilitatoSare(abilitatoSare);
		principal.setFlagAbilitatoServizi(false);
		principal.setFlagAbilitaPec(pfPrincipalRegister.getFlagAbilitaPec());
		principal.setRichiestaRegForteToken(pfPrincipalRegister.getRichiestaRegForteToken());
		principal.setUsername(pfPrincipalRegister.getUsername());

		if (encrypt) {
			principal.setPassWord(Utils.SHA1.encrypt(pfPrincipalRegister.getPassWord()));
		} else {
			principal.setPassWord(pfPrincipalRegister.getPassWord());
		}

		if (pwdScaduta) {
			principal.setDtScadenza(DateUtils.addDays(now, -1));
		} else {
			principal.setDtScadenza(DateUtils.addDays(now, 90));
		}

		principal.setCognome(pfPrincipalRegister.getCognome());
		principal.setNome(pfPrincipalRegister.getNome());
		principal.setEmail(pfPrincipalRegister.getEmail());
		principal.setDomanda(pfPrincipalRegister.getDomanda());
		principal.setRisposta(pfPrincipalRegister.getRisposta());
		principal.setDtFineValidita(DateUtils.addDays(now, 30));
		principal.setStileSelezionato("myportal");
		principal.setConfirmationToken(pfPrincipalRegister.getConfirmationToken());
		principal.setIndirizzoUtente(pfPrincipalRegister.getIndirizzoUtente());
		principal.setTelefonoUtente(pfPrincipalRegister.getTelefonoUtente());
		principal.setPrivacy("Y");
		principal.setNumTentativi(0);
		principal.setDeRuoloPortale(deRuoloPortaleEJB.findById(codRuoloPortale));
		principal = persist(idPrincipalIns, principal);

		return principal;
	}

	private PfPrincipal registerFromPorting(PfPrincipal pfPrincipalRegister, String codRuoloPortale)
			throws MyCasException {
		pfPrincipalRegister.setDeRuoloPortale(deRuoloPortaleEJB.findById(codRuoloPortale));
		pfPrincipalRegister = persist(0, pfPrincipalRegister);
		return pfPrincipalRegister;
	}

	public Boolean confirmUser(PfPrincipal pfPrincipal) throws MyCasException {
		pfPrincipal.setFlagAbilitato(true);
		pfPrincipal.setConfirmationToken(null);
		merge(0, pfPrincipal);

		return Boolean.TRUE;
	}

	public void updateCittadino(PfPrincipal pfPrincipalRegister) throws MyCasException {
		PfPrincipal principal = findById(pfPrincipalRegister.getIdPfPrincipal());
		principal.setCognome(pfPrincipalRegister.getCognome());
		principal.setNome(pfPrincipalRegister.getNome());
		principal.setEmail(pfPrincipalRegister.getEmail());
		principal.setDomanda(pfPrincipalRegister.getDomanda());
		principal.setRisposta(pfPrincipalRegister.getRisposta());
		principal.setIndirizzoUtente(pfPrincipalRegister.getIndirizzoUtente());
		principal.setTelefonoUtente(pfPrincipalRegister.getTelefonoUtente());
		principal.setFlagAbilitaPec(pfPrincipalRegister.getFlagAbilitaPec());

		merge(0, principal);
	}

	public void updateRichiestaToken(PfPrincipal pfPrincipalRegister) throws MyCasException {
		PfPrincipal principal = findById(pfPrincipalRegister.getIdPfPrincipal());
		principal.setFlagAbilitatoServizi(pfPrincipalRegister.getFlagAbilitatoServizi());
		principal.setRichiestaRegForteToken(pfPrincipalRegister.getRichiestaRegForteToken());

		merge(0, principal);
	}

	public PfPrincipal updateAzienda(PfPrincipal pfPrincipalRegister) throws MyCasException {
		PfPrincipal principal = findById(pfPrincipalRegister.getIdPfPrincipal());
		principal.setCognome(pfPrincipalRegister.getCognome());
		principal.setNome(pfPrincipalRegister.getNome());
		principal.setEmail(pfPrincipalRegister.getEmail());
		principal.setDomanda(pfPrincipalRegister.getDomanda());
		principal.setRisposta(pfPrincipalRegister.getRisposta());
		principal.setFlagAbilitatoSare(pfPrincipalRegister.getFlagAbilitatoSare());

		return merge(0, principal);
	}

	public PfPrincipal updateProvincia(PfPrincipal pfPrincipalRegister) throws MyCasException {
		PfPrincipal principal = findById(pfPrincipalRegister.getIdPfPrincipal());
		principal.setCognome(pfPrincipalRegister.getCognome());
		principal.setNome(pfPrincipalRegister.getNome());
		principal.setDomanda(pfPrincipalRegister.getDomanda());
		principal.setRisposta(pfPrincipalRegister.getRisposta());

		return merge(0, principal);
	}

	public void changePasswordUtente(UtenteInfo utenteInfo) throws MyCasException {
		PfPrincipal pfPrincipalEdit;
		try {
			pfPrincipalEdit = findById(utenteInfo.getPfPrincipal().getIdPfPrincipal());
		} catch (MyCasNoResultException e) {
			log.error("Errore nella changePasswordUtente()", e);
			throw new EJBException(e);
		}

		pfPrincipalEdit.setDtScadenza(DateUtils.addDays(new Date(), 90));
		pfPrincipalEdit.setPassWord(Utils.SHA1.encrypt(utenteInfo.getPfPrincipal().getPassWord()));
		pfPrincipalEdit.setPasswordToken(null);
		merge(0, pfPrincipalEdit);
	}

	public List<PfPrincipal> verifyEmailOrUsername(String paramRecuperoPassword) {
		log.debug("getting pfPrincipal with param recupera password: " + paramRecuperoPassword);
		List<PfPrincipal> listUsername = entityMan.createNamedQuery("findPfPrincipalByUsername", PfPrincipal.class)
				.setParameter("usernameParam", paramRecuperoPassword).getResultList();
		List<PfPrincipal> listEmail = entityMan.createNamedQuery("findPfPrincipalByEmail", PfPrincipal.class)
				.setParameter("emailParam", paramRecuperoPassword).getResultList();
		listUsername.addAll(listEmail);
		return listUsername;
	}

	public void getTokenRecuperaPassword(PfPrincipal utente) throws MyCasException {
		String token = Utils.randomString(ConstantsSingleton.TOKEN_LENGTH);
		utente.setPasswordToken(token);
		merge(utente.getIdPfPrincipal(), utente);
	}

	/**
	 * Setta a "true" o "false" il flagAbilitato di un utente.
	 */
	public PfPrincipal gestioneAbilitaUtente(PfPrincipal utente, Integer idPrincipalMod) throws MyCasException {
		utente = findById(utente.getIdPfPrincipal());
		if (!utente.getFlagAbilitato()) {
			utente.setFlagAbilitato(true);
		} else {
			utente.setFlagAbilitato(false);
		}

		return merge(idPrincipalMod, utente);
	}

	/**
	 * Setta a true o a false il flagAbilitato al SARE di un azienda
	 */
	public PfPrincipal gestioneAbilitazioneSARE(PfPrincipal utente, Integer idPrincipalMod) throws MyCasException {
		utente = findById(utente.getIdPfPrincipal());
		if (!utente.getFlagAbilitatoSare()) {
			utente.setFlagAbilitatoSare(true);
		} else {
			utente.setFlagAbilitatoSare(false);
		}
		return merge(idPrincipalMod, utente);
	}

	/**
	 * Setta a true o a false il flagAbilitato ai servizi di un utente
	 */
	public PfPrincipal gestioneAbilitaServizi(PfPrincipal utente, Integer idPrincipalMod) throws MyCasException {
		utente = findById(utente.getIdPfPrincipal());
		if (!utente.getFlagAbilitatoServizi()) {
			utente.setFlagAbilitatoServizi(true);
		} else {
			utente.setFlagAbilitatoServizi(false);
		}

		return merge(idPrincipalMod, utente);
	}

	/**
	 * Invalida la password di un utente, ovvero setto la data di scadenza a ieri (costringendolo a scegliere una nuova
	 * password al prossimo login).
	 */
	public PfPrincipal invalidaPassword(PfPrincipal utente, Integer idPrincipalMod) throws MyCasException {
		utente = findById(utente.getIdPfPrincipal());
		utente.setDtScadenza(DateUtils.addDays(new Date(), -1));
		return merge(idPrincipalMod, utente);
	}

	/**
	 * Effettuo il reset della password di un utente, ovvero: setto la password a quella di default ("Temporanea123") e
	 * setto la data di scadenza a ieri (per costringere l'utente a scegliere una nuova password al prossimo login).
	 */
	public PfPrincipal resetDefaultPassword(PfPrincipal utente, Integer idPrincipalMod) throws MyCasException {
		utente = findById(utente.getIdPfPrincipal());
		utente.setPassWord(Utils.SHA1.encrypt(constantsSingleton.getDefaultPassword()));
		utente.setDtScadenza(DateUtils.addDays(new Date(), -1));
		return merge(idPrincipalMod, utente);
	}

	/**
	 * Effettua la cancellazione logica di un utente : viene aggiunto "CANC_" davanti ad username ed email (tenendo
	 * conto che lo username può essere al massimo di 16 caratteri), viene fatta scadere la password e tutte le
	 * abilitazioni vengono
	 */
	public PfPrincipal cancellaUtente(PfPrincipal utente, Integer idPrincipalMod) throws MyCasException {
		utente = findById(utente.getIdPfPrincipal());

		String changedUsername = "CANC_" + utente.getUsername();
		if (changedUsername.length() > 16)
			changedUsername = changedUsername.substring(0, 16);

		utente.setUsername(changedUsername);
		utente.setEmail("CANC_" + utente.getEmail());
		utente.setDtScadenza(DateUtils.addDays(new Date(), -1));
		utente.setFlagAbilitato(false);
		utente.setFlagAbilitatoSare(false);
		utente.setFlagAbilitatoServizi(false);
		return merge(idPrincipalMod, utente);
	}

	public PfPrincipal findByResetPassword(String usernameParam, String emailParam, String tokenParam) {
		List<PfPrincipal> list = entityMan.createNamedQuery("findPfPrincipalForResetPwd", PfPrincipal.class)
				.setParameter("emailParam", emailParam).setParameter("usernameParam", usernameParam)
				.setParameter("passwordTokenParam", tokenParam).getResultList();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * Cambia la password di un utente; se questo è abilitato al SARE allora chiama un ws per modificare la password
	 * anche lì.
	 */
	public void changePassword(Integer idPfPrincipal, String newPassword) throws MyAccountException, MyCasException {
		PfPrincipal pfPrincipalChange = findById(idPfPrincipal);

		pfPrincipalChange.setDtScadenza(DateUtils.addDays(new Date(), 90));
		pfPrincipalChange.setPassWord(Utils.SHA1.encrypt(newPassword));

		merge(0, pfPrincipalChange);

		// can throw MyAccountException
		changeOldSarePass(pfPrincipalChange);
	}

	/**
	 * Cambio la password di un utente e setto la scadenza della nuova password a 90 giorni da oggi. Se l'utente è
	 * un'azienda o una provincia abilitata al SARE, chiamo un ws per cambiare la password anche lì. Arriva dal forgotPassword
	 */
	public void resetPassword(PfPrincipal utente, String newPassword) throws MyCasException, MyAccountException {
		utente = findByResetPassword(utente.getUsername(), utente.getEmail(), utente.getPasswordToken());
		utente.setDtScadenza(DateUtils.addDays(new Date(), 90));
		utente.setPassWord(Utils.SHA1.encrypt(newPassword));
		utente.setPasswordToken(null);
		merge(utente.getIdPfPrincipal(), utente);

		changeOldSarePass(utente);

	}

	public void changePasswordAzienda(AziendaInfo aziendaInfo) throws MyCasException {

		PfPrincipal pfPrincipalEdit = null;
		try {
			pfPrincipalEdit = findById(aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		} catch (MyCasNoResultException e) {
			log.error("Errore nella changePasswordAzienda()", e);
			throw new EJBException(e);
		}
		pfPrincipalEdit.setDtScadenza(DateUtils.addDays(new Date(), 90));
		pfPrincipalEdit.setPassWord(Utils.SHA1.encrypt(aziendaInfo.getPfPrincipal().getPassWord()));
		pfPrincipalEdit.setPasswordToken(null);

		merge(aziendaInfo.getPfPrincipal().getIdPfPrincipal(), pfPrincipalEdit);

		try {
			changeOldSarePass(pfPrincipalEdit);
		} catch (MyAccountException e) {
			log.error("Errore nella changePasswordAzienda()", e);
			throw new EJBException(e.getMessage(), e);
		}

	}

	private void changeOldSarePass(PfPrincipal pfPrincipalChange) throws MyCasNoResultException, MyAccountException {
		if (pfPrincipalChange.isAzienda()) {
			Boolean checkUserSare = pfPrincipalChange.getFlagAbilitatoSare();
			if (checkUserSare) {
				// Se il SARE è attivo, gli notifico il cambio password
				if (!constantsSingleton.isSareDisabled()) {
					AziendaInfo aziendaInfo = aziendaInfoEJB.findById(pfPrincipalChange.getIdPfPrincipal());
					String esitoWs = utenteAziendaSareEJB.modificaPasswordUtenteSare(pfPrincipalChange, aziendaInfo);
					log.info("SARE: modifica password azienda " + pfPrincipalChange.getUsername() + " con esito="
							+ esitoWs);
				}
			}
		} else if (pfPrincipalChange.isProvincia()) {
			Boolean checkUserSare = pfPrincipalChange.getFlagAbilitatoSare();
			if (checkUserSare) {
				// Se il SARE è attivo, gli notifico il cambio password
				if (!constantsSingleton.isSareDisabled()) {
					// cosa cambia tra le seguenti due? bohh
					// Provincia provincia = provinciaEJB.findById(utente.getIdPfPrincipal());

					Provincia provincia = provinciaEJB.findByPfPrincipalId(pfPrincipalChange.getIdPfPrincipal());
					String esitoWs = utenteAziendaSareEJB.modificaPasswordCpiSare(pfPrincipalChange, provincia);
					log.info("SARE: modifica password azienda " + pfPrincipalChange.getUsername() + " con esito="
							+ esitoWs);
				}
			}
		}
	}

	/**
	 * Determina se l'account un certo utente è ablitato.
	 */
	public Boolean findFlagAbilitatoByIdPfPrincipal(Integer idPfPrincipal) {
		TypedQuery<Boolean> query = entityMan.createNamedQuery("findFlagAbilitatoByIdPfPrincipal", Boolean.class)
				.setParameter("idPfPrincipalParam", idPfPrincipal);
		return query.getSingleResult();
	}

	/**
	 * Cambia la password di un utente e fa scadere allo stesso tempo il suo account, costringendolo a ri-cambiare la
	 * password quando farà il prossimo login. Inoltre, genera un token di conferma account.
	 */
	public void modifyDatiAccount(PfPrincipal pfPrincipal, String password) throws MyCasException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date ieri = cal.getTime();

		pfPrincipal.setPassWord(Utils.SHA1.encrypt(password));
		pfPrincipal.setConfirmationToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		pfPrincipal.setDtScadenza(ieri);

		merge(0, pfPrincipal);
	}

	/**
	 * Abilita un utente ai servizi amministrativi senza passare per la PEC, e manda una e-mail di conferma al suo
	 * indirizzo e-mail normale.
	 */
	public void setUtenteAbilitatoForteNoPec(Integer idDaAbilitare, Integer idPfPrincipalMod) throws MyCasException {
		PfPrincipal utenteModify = findById(idDaAbilitare);
		utenteModify.setFlagAbilitatoServizi(true);
		merge(idPfPrincipalMod, utenteModify);

		// Mando una email di notifica all'utente.
		String codProvinciaRiferimento = utenteModify.getUtenteInfo().getDeProvincia() == null ? null
				: utenteModify.getUtenteInfo().getDeProvincia().getCodProvincia();
		emailManager.sendEmailAccreditamentoForteNoPec(utenteModify.getUsername(), utenteModify.getEmail(),
				codProvinciaRiferimento);
	}

	/**
	 * Abilita un utente ai servizi amministrativi e manda una mail di conferma al suo indirizzo PEC.
	 */
	public void setUtenteAbilitatoForteConPec(Integer idDaAbilitare, Integer idPfPrincipalMod, String tokenRichiesta,
			String emailPec) throws MyCasException {
		PfPrincipal utenteModify = findById(idDaAbilitare);
		utenteModify.setRegistrazioneForteToken(Utils.randomString(24));
		merge(idPfPrincipalMod, utenteModify);

		// Mando una email di notifica all'utente.
		String codProvinciaRiferimento = utenteModify.getUtenteInfo().getDeProvincia() == null ? null
				: utenteModify.getUtenteInfo().getDeProvincia().getCodProvincia();
		emailManager.sendEmailAccreditamentoForteConPec(utenteModify.getUsername(), emailPec,
				utenteModify.getRegistrazioneForteToken(), codProvinciaRiferimento);
	}

	/**
	 * Estrae una lista di PfPrincipal in base a vari parametri di ricerca. E' possibile paginare la ricerca.
	 */
	public List<PfPrincipal> findByFilter(PfPrincipalFilter filter, Integer first, Integer pageSize) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> criteria = cb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> pfPrincipalRoot = criteria.from(PfPrincipal.class);
		criteria.select(pfPrincipalRoot);
		criteria.groupBy(pfPrincipalRoot.get(PfPrincipal_.idPfPrincipal));

		// Condizioni Where
		List<Predicate> whereConditions = createFindByFilterWhereConditions(pfPrincipalRoot, filter);
		if (whereConditions != null && whereConditions.size() > 0) {
			criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		}
		// Ordinamento
		List<Order> orderList = new ArrayList<Order>();
		if (filter.getTipoOrdinamentoEnum() == TipoOrdinamento.ALFABETICO) {
			orderList.add(cb.asc(pfPrincipalRoot.get(PfPrincipal_.cognome)));
			orderList.add(cb.asc(pfPrincipalRoot.get(PfPrincipal_.nome)));
		} else {
			orderList.add(cb.asc(pfPrincipalRoot.get(PfPrincipal_.username)));
		}
		criteria.orderBy(orderList);
		// Creazione query
		TypedQuery<PfPrincipal> query = entityMan.createQuery(criteria);
		if (first != null && first > 0) {
			query.setFirstResult(first);
		}
		if (pageSize != null && pageSize > 0) {
			query.setMaxResults(pageSize);
		}

		return query.getResultList();
	}

	/**
	 * Estrae una lista di PfPrincipal in base a vari parametri di ricerca, senza paginazione.
	 */
	public List<PfPrincipal> findByFilter(PfPrincipalFilter filtro) {
		return findByFilter(filtro, null, null);
	}

	/**
	 * Estrae username, nome e cognome di una lista di utenti in base a vari parametri di ricerca. E' possibile paginare
	 * la ricerca.
	 */
	public List<PfPrincipal> findUsernameNomeCognomeRagioneSocialeByFilter(PfPrincipalFilter filter, Integer first,
			Integer pageSize) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
		Root<PfPrincipal> pfPrincipalRoot = criteria.from(PfPrincipal.class);
		Join<PfPrincipal, AziendaInfo> aziendaInfoJoin = pfPrincipalRoot.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
		criteria.select(cb.array(pfPrincipalRoot.get(PfPrincipal_.idPfPrincipal),
				pfPrincipalRoot.get(PfPrincipal_.username), pfPrincipalRoot.get(PfPrincipal_.nome),
				pfPrincipalRoot.get(PfPrincipal_.cognome), aziendaInfoJoin.get(AziendaInfo_.ragioneSociale)));
		criteria.groupBy(pfPrincipalRoot.get(PfPrincipal_.idPfPrincipal),
				aziendaInfoJoin.get(AziendaInfo_.ragioneSociale));

		// Condizioni Where
		List<Predicate> whereConditions = createFindByFilterWhereConditions(pfPrincipalRoot, filter);
		if (whereConditions != null && whereConditions.size() > 0) {
			criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		}
		// Ordinamento
		List<Order> orderList = new ArrayList<Order>();
		if (filter.getTipoOrdinamentoEnum() == TipoOrdinamento.ALFABETICO) {
			orderList.add(cb.asc(pfPrincipalRoot.get(PfPrincipal_.cognome)));
			orderList.add(cb.asc(pfPrincipalRoot.get(PfPrincipal_.nome)));
		} else {
			orderList.add(cb.asc(pfPrincipalRoot.get(PfPrincipal_.username)));
		}
		criteria.orderBy(orderList);
		// Creazione query
		TypedQuery<Object[]> query = entityMan.createQuery(criteria);
		if (first != null && first > 0) {
			query.setFirstResult(first);
		}
		if (pageSize != null && pageSize > 0) {
			query.setMaxResults(pageSize);
		}

		List<Object[]> tempResultList = query.getResultList();
		List<PfPrincipal> finalResultList = new ArrayList<PfPrincipal>();
		for (Object[] tempResult : tempResultList) {
			PfPrincipal finalResult = new PfPrincipal();
			finalResult.setIdPfPrincipal(Integer.parseInt(tempResult[0].toString()));
			finalResult.setUsername(tempResult[1].toString());
			finalResult.setNome(tempResult[2].toString());
			finalResult.setCognome(tempResult[3].toString());
			AziendaInfo aziendaInfo = new AziendaInfo();
			aziendaInfo.setRagioneSociale(tempResult[4] != null ? tempResult[4].toString() : null);
			finalResult.setAziendaInfo(aziendaInfo);

			finalResultList.add(finalResult);
		}
		return finalResultList;
	}

	/**
	 * Estrae username, nome e cognome di una lista di utenti in base a vari parametri di ricerca, senza paginazione.
	 */
	public List<PfPrincipal> findUsernameNomeCognomeByFilter(PfPrincipalFilter filter) {
		return findUsernameNomeCognomeRagioneSocialeByFilter(filter, null, null);
	}

	/**
	 * Estrae il numero di PfPrincipal che corrispondono a dei parametri di ricerca.
	 */
	public Integer findCountByFilter(PfPrincipalFilter filter) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<PfPrincipal> pfPrincipalRoot = criteria.from(PfPrincipal.class);
		criteria.select(cb.count(pfPrincipalRoot.get(PfPrincipal_.idPfPrincipal)));

		List<Predicate> whereConditions = createFindByFilterWhereConditions(pfPrincipalRoot, filter);
		if (whereConditions != null && whereConditions.size() > 0) {
			criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		}
		TypedQuery<Long> query = entityMan.createQuery(criteria);
		return query.getSingleResult().intValue();
	}

	/**
	 * Costruisce la condizione WHERE delle query findByFilter e findCountByFilter.
	 */
	private List<Predicate> createFindByFilterWhereConditions(Root<PfPrincipal> pfPrincipalRoot,
			PfPrincipalFilter filter) {
		List<Predicate> predicati = new ArrayList<Predicate>();
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();

		// Se devo includere gli utenti non profilati, faccio una outer join con gpProfilatura,
		// altrimenti una inner join.
		Join<PfPrincipal, GpProfilatura> gpProfilaturaJoin;
		if (filter.isIncludiNonProfilati()) {
			gpProfilaturaJoin = pfPrincipalRoot.join(PfPrincipal_.gpProfilaturaList, JoinType.LEFT);
		} else {
			gpProfilaturaJoin = pfPrincipalRoot.join(PfPrincipal_.gpProfilaturaList, JoinType.INNER);
		}

		// Faccio comunque tutte le join fino alla tabella tipoGruppo.
		Join<GpProfilatura, GpRuoloGruppo> gpRuoloGruppoJoin = gpProfilaturaJoin.join(GpProfilatura_.gpRuoloGruppo,
				JoinType.LEFT);
		Join<GpRuoloGruppo, GpGruppo> gpGruppoJoin = gpRuoloGruppoJoin.join(GpRuoloGruppo_.gpGruppo, JoinType.LEFT);
		Join<GpGruppo, GpDeTipoGruppo> gpDeTipoGruppoJoin = gpGruppoJoin.join(GpGruppo_.gpDeTipoGruppo, JoinType.LEFT);

		// Filtro sull'ID
		if (filter.getIdPfPrincipal() != null && filter.getIdPfPrincipal() > 0) {
			predicati.add(cb.equal(pfPrincipalRoot.get(PfPrincipal_.idPfPrincipal), filter.getIdPfPrincipal()));
		}
		// Fitro sul macroTipo.
		if (filter.getMacroTipo() != null) {
			predicati.add(cb.equal(gpDeTipoGruppoJoin.get(GpDeTipoGruppo_.gpDeMacroTipo), filter.getMacroTipo()));
		}

		// Filtro sul gruppo (ovvero: che abbia almeno una profilatura relativa a questo gruppo)
		if (filter.getGruppo() != null) {
			predicati.add(cb.equal(gpGruppoJoin, filter.getGruppo()));
		}

		// Filtro sullo username.
		if (filter.getUsername() != null && !filter.getUsername().trim().isEmpty()) {
			if (filter.isLikeSearch()) {
				predicati.add(cb.like(cb.upper(pfPrincipalRoot.get(PfPrincipal_.username)),
						filter.getUsername().trim().toUpperCase() + "%"));
			} else {
				predicati.add(cb.equal(cb.upper(pfPrincipalRoot.get(PfPrincipal_.username)),
						filter.getUsername().trim().toUpperCase()));
			}
		}

		// Filter by email
		if (filter.getEmail() != null && !filter.getEmail().trim().isEmpty()) {
			if (filter.isLikeSearch()) {
				predicati.add(cb.like(cb.upper(pfPrincipalRoot.get(PfPrincipal_.email)),
						filter.getEmail().trim().toUpperCase() + "%"));
			} else {
				predicati.add(cb.equal(cb.upper(pfPrincipalRoot.get(PfPrincipal_.email)),
						filter.getEmail().trim().toUpperCase()));
			}

		}

		// Filtro sul nome.
		if (filter.getNome() != null && !filter.getNome().trim().isEmpty()) {
			if (filter.isLikeSearch()) {
				predicati.add(cb.like(cb.upper(pfPrincipalRoot.get(PfPrincipal_.nome)),
						filter.getNome().trim().toUpperCase() + "%"));
			} else {
				predicati.add(cb.equal(cb.upper(pfPrincipalRoot.get(PfPrincipal_.nome)),
						filter.getNome().trim().toUpperCase()));
			}
		}

		// Filtro sul cognome.
		if (filter.getCognome() != null && !filter.getCognome().trim().isEmpty()) {
			if (filter.isLikeSearch()) {
				predicati.add(cb.like(cb.upper(pfPrincipalRoot.get(PfPrincipal_.cognome)),
						filter.getCognome().trim().toUpperCase() + "%"));
			} else {
				predicati.add(cb.equal(cb.upper(pfPrincipalRoot.get(PfPrincipal_.cognome)),
						filter.getCognome().trim().toUpperCase()));
			}
		}

		// Filtro sul codice fiscale; per limitare i join al minimo indispensabile devo fare varie condizioni.
		if (filter.getCodiceFiscale() != null && !filter.getCodiceFiscale().trim().isEmpty()) {
			Predicate cfUtentePredicate = null;
			Predicate cfAziendaPredicate = null;
			Predicate cfProvinciaPredicate = null;

			// Se sto cercando utenti di macro-tipo "cittadino" o "qualsiasi", faccio join con UtenteInfo
			if (filter.getMacroTipo() == null
					|| filter.getMacroTipo().getCodMacroTipo().equals(GpDeMacroTipoEnum.CIT)) {
				Join<PfPrincipal, UtenteInfo> utenteInfoJoin = pfPrincipalRoot.join(PfPrincipal_.utenteInfo,
						JoinType.LEFT);
				if (filter.isLikeSearch()) {
					cfUtentePredicate = cb.like(cb.upper(utenteInfoJoin.get(UtenteInfo_.codiceFiscale)),
							filter.getCodiceFiscale().toUpperCase() + "%");
				} else {
					cfUtentePredicate = cb.equal(cb.upper(utenteInfoJoin.get(UtenteInfo_.codiceFiscale)),
							filter.getCodiceFiscale().toUpperCase());
				}
			}

			// Se sto cercando utenti di macro-tipo "azienda" o "qualsiasi", faccio join con AziendaInfo
			if (filter.getMacroTipo() == null
					|| filter.getMacroTipo().getCodMacroTipo().equals(GpDeMacroTipoEnum.AZI)) {
				Join<PfPrincipal, AziendaInfo> aziendaInfoJoin = pfPrincipalRoot.join(PfPrincipal_.aziendaInfo,
						JoinType.LEFT);
				if (filter.isLikeSearch()) {
					cfAziendaPredicate = cb.like(cb.upper(aziendaInfoJoin.get(AziendaInfo_.codiceFiscale)),
							filter.getCodiceFiscale().toUpperCase() + "%");
				} else {
					cfAziendaPredicate = cb.equal(cb.upper(aziendaInfoJoin.get(AziendaInfo_.codiceFiscale)),
							filter.getCodiceFiscale().toUpperCase());
				}
			}
			// Se sto cercando utenti di macro-tipo "regione" o "qualsiasi", faccio join con Provincia
			if (filter.getMacroTipo() == null
					|| filter.getMacroTipo().getCodMacroTipo().equals(GpDeMacroTipoEnum.PRV)) {
				Join<PfPrincipal, Provincia> provinciaInfoJoin = pfPrincipalRoot
						.join(PfPrincipal_.provinciasForIdPfPrincipal, JoinType.LEFT);
				if (filter.isLikeSearch()) {
					cfProvinciaPredicate = cb.like(cb.upper(provinciaInfoJoin.get(Provincia_.codiceFiscale)),
							filter.getCodiceFiscale().toUpperCase() + "%");
				} else {
					cfProvinciaPredicate = cb.equal(cb.upper(provinciaInfoJoin.get(Provincia_.codiceFiscale)),
							filter.getCodiceFiscale().toUpperCase());
				}
			}

			// Se sto cercando utenti "qualsiasi", faccio una OR tra la condizione su UtenteInfo, quella su AziendaInfo
			// e quella su Provincia
			if (cfUtentePredicate != null && cfAziendaPredicate != null && cfProvinciaPredicate != null) {
				predicati.add(cb.or(cfUtentePredicate, cfAziendaPredicate, cfProvinciaPredicate));
			} else if (cfUtentePredicate != null) {
				predicati.add(cfUtentePredicate);
			} else if (cfAziendaPredicate != null) {
				predicati.add(cfAziendaPredicate);
			} else if (cfProvinciaPredicate != null) {
				predicati.add(cfProvinciaPredicate);
			}
		}

		// Restituisco l'intera clausola WHERE della query.
		return predicati;
	}

	/**
	 * Trova il macro-tipo di un utente, passando per le sue profilature e i suoi gruppi. Lancia un'eccezione se per
	 * qualche motivo l'utente ha profilature appartenenti a più di un macro-tipo, o se non ha nessuna profilatura.
	 */
	public GpDeMacroTipo findMacroTipoUtente(Integer idPfPrincipal)
			throws MyCasNoResultException, MyCasTooManyResultsException {
		TypedQuery<GpDeMacroTipo> namedQuery = entityMan.createNamedQuery("findMacroTipoUtente", GpDeMacroTipo.class);
		namedQuery.setParameter("idPfPrincipal", idPfPrincipal);
		List<GpDeMacroTipo> results = namedQuery.getResultList();

		if (results == null || results.isEmpty()) {
			throw new MyCasNoResultException("Ogni utente deve avere un macro-tipo");
		} else if (results.size() > 1) {
			throw new MyCasTooManyResultsException("Un utente non può avere più di un macro-tipo");
		} else {
			return results.get(0);
		}
	}

	/**
	 * Trova l'utente (DEVE essercene solo uno, se no lancio un'eccezione) che ha una profilatura con ruolo "Superutente
	 * SARE".
	 */
	public PfPrincipal findSuperuserSare() throws MyCasNoResultException, MyCasTooManyResultsException {
		String descrizioneRuoloSuperuser = ConstantsSingleton.GpRuoloConstants.SUPERUSER_SARE;
		TypedQuery<PfPrincipal> namedQuery = entityMan.createNamedQuery("findPfPrincipalByDescrizioneRuolo",
				PfPrincipal.class);
		namedQuery.setParameter("descrizione", descrizioneRuoloSuperuser);
		List<PfPrincipal> resultList = namedQuery.getResultList();
		if (resultList == null || resultList.isEmpty()) {
			throw new MyCasNoResultException("Non esiste un superuser SARE.");
		} else if (resultList.size() > 1) {
			throw new MyCasTooManyResultsException("Ci sono più utenti con ruolo 'Superuser SARE'");
		} else {
			return resultList.get(0);
		}
	}
}
